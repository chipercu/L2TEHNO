package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.HennaEquipList;
import l2open.gameserver.serverpackets.HennaUnequipList;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.HennaTreeTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * This class ...
 *
 * @version $Revision$ $Date$
 */
public class L2SymbolMakerInstance extends L2NpcInstance
{

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equals("Draw"))
		{
			L2HennaInstance[] henna = HennaTreeTable.getInstance().getAvailableHenna(player.getClassId(), player.getSex());
			HennaEquipList hel = new HennaEquipList(player, henna);
			player.sendPacket(hel);
		}
		else if(command.equals("RemoveList"))
			//showRemoveChat(player);
			player.sendPacket(new HennaUnequipList(player));
		else if(command.startsWith("Remove "))
		{
			if(player.getInventory().getCountOf(ConfigValue.HennaRemoveItemId) >= ConfigValue.HennaRemoveItemCount)
			{
				if(ConfigValue.HennaRemoveItemCount > 0)
					player.getInventory().destroyItemByItemId(ConfigValue.HennaRemoveItemId, ConfigValue.HennaRemoveItemCount, true);
				int slot = Integer.parseInt(command.substring(7));
				PlayerData.getInstance().removeHenna(player, slot);
			}
			else
			{
				if(ConfigValue.HennaRemoveItemId == 57)
					player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
				else
					player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	private void showRemoveChat(L2Player player)
	{
		StringBuffer html1 = new StringBuffer("<html><body>");
		html1.append("Select symbol you would like to remove:<br><br>");
		boolean hasHennas = false;
		for(int i = 1; i <= 3; i++)
		{
			L2HennaInstance henna = player.getHenna(i);
			if(henna != null)
			{
				hasHennas = true;
				html1.append("<a action=\"bypass -h npc_%objectId%_Remove " + i + "\">" + henna.getName() + "</a><br>");
			}
		}
		if(!hasHennas)
			html1.append("You don't have any symbol to remove!");
		html1.append("</body></html>");

		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setHtml(html1.toString());
		player.sendPacket(html);
	}

	public L2SymbolMakerInstance(int objectID, L2NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom;
		if(val == 0)
			pom = String.valueOf(npcId);
		else
			pom = npcId + "-" + val;

		return "data/html/symbolmaker/" + pom + ".htm";
	}
}