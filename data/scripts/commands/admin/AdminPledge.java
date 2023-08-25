package commands.admin;

import java.util.StringTokenizer;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.instancemanager.CastleSiegeManager;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ClanMember;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2VillageMasterInstance;
import l2open.gameserver.serverpackets.PledgeShowInfoUpdate;
import l2open.gameserver.serverpackets.PledgeStatusChanged;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.player.PlayerData;

/**
 * Pledge Manipulation //pledge <create|dismiss|setlevel|resetcreate|resetwait|addrep|setleader>
 */
public class AdminPledge implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_pledge,
		admin_clan_count
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		@SuppressWarnings("unused")
		Commands command = (Commands) comm;

		if(activeChar.getPlayerAccess() == null || !activeChar.getPlayerAccess().CanEditPledge || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			return false;

		L2Player target = (L2Player) activeChar.getTarget();

		if(fullString.startsWith("admin_clan_count"))
		{
			if(target.getClan() == null)
			{
				activeChar.sendPacket(Msg.INVALID_TARGET);
				return false;
			}
			L2Clan clan = target.getClan();
			activeChar.sendMessage("Clan '"+clan.getName()+"' online: "+clan.getOnlineMembers(-1).length+"/"+clan.getMembersCount());
		}
		else if(fullString.startsWith("admin_pledge"))
		{
			StringTokenizer st = new StringTokenizer(fullString);
			st.nextToken();

			String action = st.nextToken(); // create|dismiss|setlevel|resetcreate|resetwait|addrep

			if(action.equals("create"))
				try
				{
					String pledgeName = st.nextToken();
					L2Clan clan = ClanTable.getInstance().createClan(target, pledgeName);
					if(clan != null)
					{
						target.sendPacket(new PledgeShowInfoUpdate(clan));
						target.sendUserInfo(true);
						target.sendPacket(Msg.CLAN_HAS_BEEN_CREATED);
						return true;
					}
				}
				catch(Exception e)
				{}
			else if(action.equals("dismiss"))
			{
				if(target.getClan() == null)
				{
					activeChar.sendPacket(Msg.INVALID_TARGET);
					return false;
				}

				SiegeManager.removeSiegeSkills(target);
				for(L2Player clanMember : target.getClan().getOnlineMembers(0))
				{
					clanMember.setClan(null);
					clanMember.setTitle(null);
					clanMember.sendPacket(Msg.CLAN_HAS_DISPERSED);
					clanMember.broadcastUserInfo(true);
				}

				ThreadConnection con = null;
				FiltredPreparedStatement statement = null;
				try
				{
					con = L2DatabaseFactory.getInstance().getConnection();
					statement = con.prepareStatement("UPDATE characters SET clanid = 0 WHERE clanid=?");
					statement.setInt(1, target.getClanId());
					statement.execute();
					DatabaseUtils.closeStatement(statement);

					statement = con.prepareStatement("DELETE FROM clan_data WHERE clan_id=?");
					statement.setInt(1, target.getClanId());
					statement.execute();
					DatabaseUtils.closeStatement(statement);
					statement = null;
					target.sendPacket(Msg.CLAN_HAS_DISPERSED);
					target.broadcastUserInfo(true);
				}
				catch(Exception e)
				{}
				finally
				{
					DatabaseUtils.closeDatabaseCS(con, statement);
				}
				return true;
			}
			else if(action.equals("setlevel"))
			{
				if(target.getClan() == null)
				{
					activeChar.sendPacket(Msg.INVALID_TARGET);
					return false;
				}

				try
				{
					byte level = Byte.parseByte(st.nextToken());
					L2Clan clan = target.getClan();

					activeChar.sendMessage("You set level " + level + " for clan " + clan.getName());
					clan.setLevel(level);
					PlayerData.getInstance().updateClanInDB(clan);

					if(level < CastleSiegeManager.getSiegeClanMinLevel())
						SiegeManager.removeSiegeSkills(target);
					else
						SiegeManager.addSiegeSkills(target);

					if(level == 5)
						target.sendPacket(Msg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);

					PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(clan);
					PledgeStatusChanged ps = new PledgeStatusChanged(clan);

					for(L2Player member : clan.getOnlineMembers(0))
					{
						member.updatePledgeClass();
						member.sendPacket(Msg.CLANS_SKILL_LEVEL_HAS_INCREASED, pu, ps);
						member.broadcastUserInfo(true);
						if(member.getAttainment() != null)
							member.getAttainment().setClan();
					}

					return true;
				}
				catch(Exception e)
				{}
			}
			else if(action.equals("resetcreate"))
			{
				if(target.getClan() == null)
				{
					activeChar.sendPacket(Msg.INVALID_TARGET);
					return false;
				}
				target.getClan().setExpelledMemberTime(0);
				activeChar.sendMessage("The penalty for creating a clan has been lifted for" + target.getName());
			}
			else if(action.equals("resetwait"))
			{
				target.setLeaveClanTime(0);
				activeChar.sendMessage("The penalty for leaving a clan has been lifted for " + target.getName());
			}
			else if(action.equals("addrep"))
				try
				{
					int rep = Integer.parseInt(st.nextToken());

					if(target.getClan() == null || target.getClan().getLevel() < 5)
					{
						activeChar.sendPacket(Msg.INVALID_TARGET);
						return false;
					}
					target.getClan().incReputation(rep, false, "admin_manual");
					activeChar.sendMessage("Added " + rep + " clan points to clan " + target.getClan().getName() + ".");
				}
				catch(NumberFormatException nfe)
				{
					activeChar.sendMessage("Please specify a number of clan points to add.");
				}
			else if(action.equals("setleader"))
			{
				L2Clan clan = target.getClan();
				if(target.getClan() == null)
				{
					activeChar.sendPacket(Msg.INVALID_TARGET);
					return false;
				}
				String newLeaderName = null;
				if(st.hasMoreTokens())
					newLeaderName = st.nextToken();
				else
					newLeaderName = target.getName();
				L2ClanMember newLeader = clan.getClanMember(newLeaderName);
				if(newLeader == null)
				{
					activeChar.sendPacket(Msg.INVALID_TARGET);
					return false;
				}
				L2VillageMasterInstance.setLeader(activeChar, clan, newLeader);
			}
		}

		return false;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}