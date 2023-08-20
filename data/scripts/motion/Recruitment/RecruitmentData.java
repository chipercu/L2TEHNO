package services;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.barahlo.academ.*;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2ClanMember;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.tables.ClanTable;

public class RecruitmentData
{
	private static final RecruitmentData _instance = new RecruitmentData();

	public static RecruitmentData getInstance()
	{
		return _instance;
	}

	public boolean checkClanInvite(L2Player player, int clanId, int obj, int unity)
	{
		ClanRequest request = ClanRequest.getClanInvitePlayer(clanId, obj);
		if(request == null)
		{
			player.sendMessage("Error, this player cannot invited to the clan.");
			return false;
		}
		L2Player requestor = request.getPlayer();
		L2Clan rclan = requestor.getClan();
		if(rclan != null)
		{
			ClanRequest.removeClanInvitePlayer(clanId, obj);
			player.sendMessage("This player cannot invite to the clan becouse he is in othr clan " + rclan.getName() + ".");
			return false;
		}

		L2Clan clan = player.getClan();
		if(clan != null)
		{
			if(clan.getSubPledgeMembersCount(unity) >= clan.getSubPledgeLimit(unity))
			{
				player.sendMessage("Unity " + clan.getSubPledge(unity).getName() + " is full!");
				return false;
			}
		}

		return true;
	}

	public L2Player restore(int obj)
	{
		L2Player object = L2ObjectsStorage.getPlayer(obj);
		if(object != null)
			return object;
		else
			return null;
	}

	public void inviteRemove(L2Player player, String id)
	{
		L2Clan clan = player.getClan();

		if(clan == null)
			return;

		if((player.getClanPrivileges() & L2Clan.CP_CL_INVITE_CLAN) != L2Clan.CP_CL_INVITE_CLAN)
		{
			player.sendPacket(Msg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
			return;
		}

		int obj = Integer.parseInt(id);
		for(ClanRequest request : clan.getInviteList())
		{
			L2Player remove = request.getPlayer();
			int r_obj = remove.getObjectId();
			if(r_obj == obj)
			{
				if(!remove.isOnline())
				{
					L2Player restore = restore(r_obj);
					if(restore != null)
						remove = restore;
				}

				ClanRequest.removeClanInvitePlayer(clan.getClanId(), remove.getObjectId());

				if(remove.isOnline())
				{
					remove.sendPacket(new ExShowScreenMessage("L2Clan '" + clan.getName() + "' rejected your application to join!", 10000, ScreenMessageAlign.TOP_CENTER, true));
					remove.sendMessage("L2Clan '" + clan.getName() + "' rejected your application to join!");
				}
			}
		}
	}

	public boolean checkClanWar(L2Clan clan, L2Clan targetClan, L2Player player, boolean msg)
	{
		if(clan == null || targetClan == null)
		{
			if(msg)
				player.sendPacket(Msg.A_DECLARATION_OF_CLAN_WAR_AGAINST_AN_ALLIED_CLAN_CANT_BE_MADE);
			return false;
		}
		else if(!((player.getClanPrivileges() & L2Clan.CP_CL_CLAN_WAR) == L2Clan.CP_CL_CLAN_WAR))
		{
			if(msg)
				player.sendActionFailed();
			return false;
		}
		else if(clan.getWarsCount() >= 30)
		{
			if(msg)
				player.sendPacket(Msg.A_DECLARATION_OF_WAR_AGAINST_MORE_THAN_30_CLANS_CANT_BE_MADE_AT_THE_SAME_TIME);
			return false;
		}
		else if(clan.getLevel() < 3 || clan.getMembersCount() < 15)
		{
			if(msg)
				player.sendPacket(Msg.A_CLAN_WAR_CAN_BE_DECLARED_ONLY_IF_THE_CLAN_IS_LEVEL_THREE_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_FIFTEEN_OR_GREATER);
			return false;
		}
		else if(clan.equals(targetClan))
		{
			if(msg)
				player.sendPacket(Msg.FOOL_YOU_CANNOT_DECLARE_WAR_AGAINST_YOUR_OWN_CLAN);
			return false;
		}
		else if(clan.getAllyId() == targetClan.getAllyId() && clan.getAllyId() != 0)
		{
			if(msg)
				player.sendPacket(Msg.A_DECLARATION_OF_CLAN_WAR_AGAINST_AN_ALLIED_CLAN_CANT_BE_MADE);
			return false;
		}
		else if(targetClan.getLevel() < 3 || targetClan.getMembersCount() < 15)
		{
			if(msg)
				player.sendPacket(Msg.A_CLAN_WAR_CAN_BE_DECLARED_ONLY_IF_THE_CLAN_IS_LEVEL_THREE_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_FIFTEEN_OR_GREATER);
			return false;
		}
		else
			return true;
	}

	public void checkAndStartWar(L2Player player, int war)
	{
		L2Clan clan = player.getClan();
		L2Clan targetClan = ClanTable.getInstance().getClan(war);

		if(checkClanWar(targetClan, clan, player, true))
		{
			if(clan.isAtWarWith(targetClan.getClanId()))
			{
				player.sendPacket(Msg.THE_DECLARATION_OF_WAR_HAS_BEEN_ALREADY_MADE_TO_THE_CLAN);
				return;
			}
			else
				ClanTable.getInstance().startClanWar(player.getClan(), targetClan);
		}
	}

	public void checkAndStopWar(L2Player player, int war)
	{
		L2Clan clan = player.getClan();
		L2Clan targetClan = ClanTable.getInstance().getClan(war);

		if(clan == null || targetClan == null)
		{
			player.sendPacket(Msg.A_DECLARATION_OF_CLAN_WAR_AGAINST_AN_ALLIED_CLAN_CANT_BE_MADE);
			return;
		}
		else if(!((player.getClanPrivileges() & L2Clan.CP_CL_CLAN_WAR) == L2Clan.CP_CL_CLAN_WAR))
		{
			player.sendPacket(Msg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		else if(!clan.isAtWarWith(targetClan.getClanId()))
		{
			player.sendPacket(Msg.YOU_HAVE_NOT_DECLARED_A_CLAN_WAR_TO_S1_CLAN);
			return;
		}

		//for(UnitMember mbr : clan)
		for(L2ClanMember mbr : clan._members.values())
		{
			if(mbr.isOnline() && mbr.getPlayer().isInCombat())
			{
				player.sendPacket(Msg.A_CEASE_FIRE_DURING_A_CLAN_WAR_CAN_NOT_BE_CALLED_WHILE_MEMBERS_OF_YOUR_CLAN_ARE_ENGAGED_IN_BATTLE);
				return;
			}
		}

		ClanTable.getInstance().stopClanWar(clan, targetClan);
	}

	public boolean haveWars(L2Clan clan)
	{
		for(L2Clan war : clan.getEnemyClans())
		{
			if(war.isAtWarWith(clan.getClanId()))
				return true;
		}

		return false;
	}

	public final String name(int id)
	{
		return "&%" + id + ";";
	}

	public void sendInviteTask(L2Player player, String clanId, String note, boolean invite)
	{
		int id = Integer.parseInt(clanId);
		L2Clan clan = ClanTable.getInstance().getClan(id);

		if(player == null || clan == null)
			return;

		if(invite)
		{
			if(!clan.checkInviteList(player.getObjectId()))
			{
				clan.getInviteList().add(new ClanRequest(System.currentTimeMillis(), player, id, note));
				//for(UnitMember members : clan.getAllMembers()) // Отсылаем уведомление о заявке всем членам клана с правами приема в клан!
				for(L2ClanMember members : clan._members.values())
				{
					L2Player member = members.getPlayer();
					if(member != null)
					{
						if((member.getClanPrivileges() & L2Clan.CP_CL_INVITE_CLAN) == L2Clan.CP_CL_INVITE_CLAN)
						{
							member.sendPacket(new ExShowScreenMessage("Received a request to join the clan at player: " + player.getName(), 10000, ScreenMessageAlign.TOP_CENTER, true));
							member.sendMessage("Received a request to join the clan at player: " + player.getName());
						}
					}
				}
			}
			else
				player.sendMessage("You have already submitted an request to the clan!");
		}
		else
		{
			if(ClanRequest.removeClanInvitePlayer(clan.getClanId(), player))
			{
				L2Player leader = clan.getLeader().getPlayer();
				if(leader != null)
				{
					leader.sendPacket(new ExShowScreenMessage(player.getName() + " deleted his request to join the clan!", 10000, ScreenMessageAlign.TOP_CENTER, true));
					leader.sendMessage(player.getName() + " deleted his request to join the clan!");
				}
			}
		}
	}

	public boolean checkAcademyInvite(L2Player player)
	{
		if(player.getClan() != null)
		{
			player.sendMessage("Error: You are already in a clan!");
			return false;
		}
		else if(player.getClassId().level() > 1)
		{
			player.sendMessage("Error: You can not join the Academy!");
			return false;
		}
		else if(AcademiciansStorage.getInstance().find(player.getObjectId())) //Нужно ли Оо?
		{
			player.sendMessage("Error: You are already pass the Academy in another clan!");
			return false;
		}

		return true;
	}
}