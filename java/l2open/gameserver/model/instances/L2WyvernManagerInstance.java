package l2open.gameserver.model.instances;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.PetDataTable;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.StringTokenizer;
import java.util.logging.Logger;

public final class L2WyvernManagerInstance extends L2NpcInstance
{
	private static Logger _log = Logger.getLogger(L2WyvernManagerInstance.class.getName());

	public L2WyvernManagerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		boolean condition = validateCondition(player);

		if(actualCommand.equalsIgnoreCase("RideHelp"))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("data/html/wyvern/help_ride.htm");
			html.replace("%npcname%", "Wyvern Manager " + getName());
			player.sendPacket(html);
			player.sendActionFailed();
		}
		if(condition)
		{
			if(actualCommand.equalsIgnoreCase("RideWyvern") && player.isClanLeader())
				if(!player.isRiding() || !PetDataTable.isStrider(player.getMountNpcId()))
				{
					NpcHtmlMessage html = new NpcHtmlMessage(player, this);
					html.setFile("data/html/wyvern/not_ready.htm");
					html.replace("%npcname%", "Wyvern Manager " + getName());
					player.sendPacket(html);
				}
				else if(player.getInventory().getItemByItemId(1460) == null || player.getInventory().getItemByItemId(1460).getCount() < 25)
				{
					NpcHtmlMessage html = new NpcHtmlMessage(player, this);
					html.setFile("data/html/wyvern/havenot_cry.htm");
					html.replace("%npcname%", "Wyvern Manager " + getName());
					player.sendPacket(html);
				}
				else if(SevenSigns.getInstance().getCurrentPeriod() == 3 && SevenSigns.getInstance().getCabalHighestScore() == 3)
				{
					NpcHtmlMessage html = new NpcHtmlMessage(player, this);
					html.setFile("data/html/wyvern/no_ride_dusk.htm");
					html.replace("%npcname%", "Wyvern Manager " + getName());
					player.sendPacket(html);
				}
				else
				{
					if(player.getInventory().destroyItemByItemId(1460, 25, true) == null)
						_log.info("L2WyvernManagerInstance[72]: Item not found!!!");
					player.setMount(PetDataTable.WYVERN_ID, player.getMountObjId(), player.getMountLevel());
					NpcHtmlMessage html = new NpcHtmlMessage(player, this);
					html.setFile("data/html/wyvern/after_ride.htm");
					html.replace("%npcname%", "Wyvern Manager " + getName());
					player.sendPacket(html);
				}
		}

		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		if(!validateCondition(player))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("data/html/wyvern/lord_only.htm");
			html.replace("%npcname%", "Wyvern Manager " + getName());
			player.sendPacket(html);
			player.sendActionFailed();
			return;
		}
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/wyvern/lord_here.htm");
		html.replace("%Char_name%", String.valueOf(player.getName()));
		html.replace("%npcname%", "Wyvern Manager " + getName());
		player.sendPacket(html);
		player.sendActionFailed();
	}

	private boolean validateCondition(L2Player player)
	{
		Residence residence = getCastle();
		if(residence != null && residence.getId() > 0)
			if(player.getClan() != null)
				if(residence.getOwnerId() == player.getClanId() && player.isClanLeader()) // Leader of clan
					return true; // Owner
		residence = getFortress();
		if(residence != null && residence.getId() > 0)
			if(player.getClan() != null)
				if(residence.getOwnerId() == player.getClanId() && player.isClanLeader()) // Leader of clan
					return true; // Owner
		residence = getClanHall();
		if(residence != null && residence.getId() > 0)
			if(player.getClan() != null)
				if(residence.getOwnerId() == player.getClanId() && player.isClanLeader()) // Leader of clan
					return true; // Owner		
		return false;
	}
}