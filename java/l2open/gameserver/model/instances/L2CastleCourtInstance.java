package l2open.gameserver.model.instances;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;
import l2open.util.Rnd;

public class L2CastleCourtInstance extends L2NpcInstance
{
	//private static Logger _log = Logger.getLogger(L2CastleCourtInstance.class.getName());

	protected static final int COND_ALL_FALSE = 0;
	protected static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
	protected static final int COND_OWNER = 2;

	/**
	 * @param template
	 */
	public L2CastleCourtInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		int condition = validateCondition(player);
		if(condition <= COND_ALL_FALSE || condition == COND_BUSY_BECAUSE_OF_SIEGE)
			return;

		if((player.getClanPrivileges() & L2Clan.CP_CS_USE_FUNCTIONS) != L2Clan.CP_CS_USE_FUNCTIONS)
		{
			player.sendMessage("You don't have rights to do that.");
			return;
		}

		if(condition == COND_OWNER)
			super.onBypassFeedback(player, command);

		if(command.equalsIgnoreCase("ClanGate"))
		{
			final L2Clan clan = ClanTable.getInstance().getClan(getCastle().getOwnerId());
			if(clan != null)
			{
				final L2Player cl = clan.getLeader().getPlayer();
				if(cl != null && cl.getEffectList().getEffectsBySkillId(3632) != null)
				{
					Location dst = Rnd.coordsRandomize(cl, 50);
					player.teleToLocation(dst);
				}
				else
				{
					final NpcHtmlMessage html = new NpcHtmlMessage(player, this);
					html.setFile("data/html/castle/CourtMagician/CourtMagician-nocg.htm");
					html.replace("%objectId%", String.valueOf(getObjectId()));
					html.replace("%npcname%", String.valueOf(getName() + " " + getTitle()));
					player.sendPacket(html);
				}
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		player.sendActionFailed();
		String filename = "data/html/castle/CourtMagician/CourtMagician-no.htm";

		int condition = validateCondition(player);
		if(condition > COND_ALL_FALSE)
			if(condition == COND_BUSY_BECAUSE_OF_SIEGE)
				filename = "data/html/castle/CourtMagician/CourtMagician-busy.htm"; // Busy because of siege
			else if(condition == COND_OWNER)
				if(val == 0)
					filename = "data/html/castle/CourtMagician/CourtMagician.htm";
				else
					filename = "data/html/castle/CourtMagician/CourtMagician-" + val + ".htm";

		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}

	protected int validateCondition(L2Player player)
	{
		if(player.isGM())
			return COND_OWNER;
		if(getCastle() != null && getCastle().getId() > 0)
			if(player.getClan() != null)
				if(getCastle().getSiege().isInProgress() || TerritorySiege.isInProgress())
					return COND_BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
				else if(getCastle().getOwnerId() == player.getClanId()) // Clan owns castle
					return COND_OWNER;
		return COND_ALL_FALSE;
	}
}