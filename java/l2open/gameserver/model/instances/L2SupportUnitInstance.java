package l2open.gameserver.model.instances;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;

public class L2SupportUnitInstance extends L2NpcInstance
{
	//private static Logger _log = Logger.getLogger(L2SupportUnitInstance.class.getName());

	protected static final int COND_ALL_FALSE = 0;
	protected static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
	protected static final int COND_OWNER = 2;

	/**
	 * @param template
	 */
	public L2SupportUnitInstance(int objectId, L2NpcTemplate template)
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
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		player.sendActionFailed();
		String filename = "/data/html/fortress/SupportUnitCaptain-no.htm";

		int condition = validateCondition(player);
		if(condition > COND_ALL_FALSE)
			if(condition == COND_BUSY_BECAUSE_OF_SIEGE)
				filename = "/data/html/fortress/SupportUnitCaptain-busy.htm"; // Busy because of siege
			else if(condition == COND_OWNER)
				if(val == 0)
					filename = "/data/html/fortress/SupportUnitCaptain.htm";
				else
					filename = "/data/html/fortress/SupportUnitCaptain-" + val + ".htm";

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
		if(getFortress() != null && getFortress().getId() > 0)
			if(player.getClan() != null)
				if(getFortress().getSiege().isInProgress() || TerritorySiege.isInProgress())
					return COND_BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
				else if(getFortress().getOwnerId() == player.getClanId()) // Clan owns fortress
					return COND_OWNER;
		return COND_ALL_FALSE;
	}
}