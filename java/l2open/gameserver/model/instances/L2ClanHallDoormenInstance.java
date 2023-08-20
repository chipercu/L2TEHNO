package l2open.gameserver.model.instances;

import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.StringTokenizer;

public class L2ClanHallDoormenInstance extends L2NpcInstance
{
	protected static int Cond_All_False = 0;
	protected static int Cond_Busy_Because_Of_Siege = 1;
	protected static int Cond_Owner = 2;

	/**
	 * @param objectId
	 * @param template
	 */
	public L2ClanHallDoormenInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		int condition = validateCondition(player);
		if(condition <= Cond_All_False)
			return;
		else if(condition == Cond_Owner)
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			String actualCommand = st.nextToken();
			String val = "";
			if(st.countTokens() >= 1)
				val = st.nextToken();

			if(actualCommand.equalsIgnoreCase("door"))
				if((player.getClanPrivileges() & L2Clan.CP_CH_ENTRY_EXIT) == L2Clan.CP_CH_ENTRY_EXIT)
				{
					if(val.equalsIgnoreCase("open"))
					{
						getClanHall().openCloseDoors(player, true);
						NpcHtmlMessage html = new NpcHtmlMessage(player, this);
						html.setFile("./data/html/doormen/clanhall/AfterDoorOpen.htm");
						sendHtmlMessage(player, html);
					}
					else if(val.equalsIgnoreCase("close"))
					{
						getClanHall().openCloseDoors(player, false);
						NpcHtmlMessage html = new NpcHtmlMessage(player, this);
						html.setFile("./data/html/doormen/clanhall/AfterDoorClose.htm");
						sendHtmlMessage(player, html);
					}
					else
						showChatWindow(player, 0);
				}
				else
					player.sendMessage(new CustomMessage("common.Privilleges", player));
		}
		super.onBypassFeedback(player, command);
	}

	private void sendHtmlMessage(L2Player player, NpcHtmlMessage html)
	{
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		String filename = "/data/html/doormen/clanhall/doormen-no.htm";
		int condition = validateCondition(player);
		if(condition == Cond_Owner) // Clan owns CH
			switch(getClanHall().getId())
			{
				case 36:
				case 37:
				case 38:
				case 39:
				case 40:
				case 41:
				case 51:
				case 52:
				case 53:
				case 54:
				case 55:
				case 56:
				case 57:
					filename = "/data/html/doormen/clanhall/doormen-elite.htm";
					break;
				default:
					filename = "/data/html/doormen/clanhall/doormen.htm";
					break;
			}
		NpcHtmlMessage html = new NpcHtmlMessage(player, this, filename, val);
		L2Clan clanowner = ClanTable.getInstance().getClan(getClanHall().getOwnerId());
		html.replace("%clanname%", clanowner != null ? clanowner.getName() : "NPC");
		html.replace("%clanlidername%", clanowner != null ? clanowner.getLeaderName() : "NPC");
		player.sendPacket(html);
	}

	protected int validateCondition(L2Player player)
	{
		if(player.isGM())
			return Cond_Owner;
		if(player.getClan() != null)
			if(getClanHall().getOwnerId() == player.getClanId())
				return Cond_Owner;
		return Cond_All_False;
	}
}