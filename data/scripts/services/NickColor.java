package services;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.Util;

public class NickColor extends Functions implements ScriptFile
{

	public void list(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(!ConfigValue.NickColorChangeEnabled)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}

		final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
		final int perpage = 6;
		int counter = 0;

		String html = Files.read("data/scripts/services/NickColor/index.htm", player);
		String template = Files.read("data/scripts/services/NickColor/template.htm", player);
		String block = "";
		String list = "";
		for(int i = (page - 1) * perpage; i < ConfigValue.NickColorChangeList.length; i++)
		{
			String color = ConfigValue.NickColorChangeList[i];
			block = template;
			block = block.replace("{bypass}", "bypass -h scripts_services.NickColor:change " + color);
			block = block.replace("{color}", (color.substring(4, 6) + color.substring(2, 4) + color.substring(0, 2)));
			if(color.equalsIgnoreCase("FFFFFF"))
			{
				block = block.replace("{count}", " Free");
				block = block.replace("{item}", "");			
			}
			else
			{
				block = block.replace("{count}", Util.formatAdena(ConfigValue.NickColorChangePrice));
				block = block.replace("{item}", " &#" + ConfigValue.NickColorChangeItem + ";");
			}
			list += block;

			counter++;

			if(counter >= perpage)
				break;
		}

		double count = Math.ceil((double) ConfigValue.NickColorChangeList.length / (double) perpage); // Используем округление для получения последней страницы с остатком!
		int inline = 1;
		String navigation = "";
		for(int i = 1; i <= count; i++)
		{
			if(i == page)
				navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h scripts_services.NickColor:list " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
			else
				navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h scripts_services.NickColor:list " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";

			if(inline == 8)
			{
				navigation += "</tr><tr>";
				inline = 0;
			}
			inline++;
		}

		if(navigation.equals("")) //Для избежания крита клиента!
			navigation = "<td width=30 align=center valign=top>...</td>";

		html = html.replace("{list}", list);
		html = html.replace("{navigation}", navigation);

		show(html, player, null);
	}

	public void change(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(param[0].equalsIgnoreCase("FFFFFF"))
		{
			player.setNameColor(Integer.decode("0xFFFFFF"));
			player.broadcastUserInfo(true);
			return;
		}

		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.NickColorChangeItem);
		L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
		if(pay != null && pay.getCount() >= ConfigValue.NickColorChangePrice || ConfigValue.NickColorChangeFreePremium && player.getNetConnection().getBonus() > 1)
		{
			player.setNameColor(Integer.decode("0x" + param[0]));
			if(!ConfigValue.NickColorChangeFreePremium || player.getNetConnection().getBonus() <= 1)
				player.getInventory().destroyItem(pay, ConfigValue.NickColorChangePrice, true);
			player.broadcastUserInfo(true);
		}
		else if(ConfigValue.NickColorChangeItem == 57)
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
	}

	public void list2()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		String html = Files.read("data/scripts/services/NickColor/title.htm", player);

		StringBuilder append = new StringBuilder();
		int i = 1;
		for(String color : ConfigValue.TitleColorChangeList)
		{
			append.append("<td width=32 align=center valign=top height=20>");
			append.append("<table border=0 cellspacing=0 cellpadding=0 width=30 height=20>");
			append.append("<tr>");
			append.append("<td width=32 height=45 align=center valign=top>");
			append.append("<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 bgcolor=\"").append(color.substring(4, 6) + color.substring(2, 4) + color.substring(0, 2)).append("\">");
			append.append("<tr>");
			append.append("<td width=32 align=center valign=top>");
			append.append("<button action=\"bypass -h scripts_services.NickColor:change2 ").append(color).append("\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>");
			append.append("</td>");
			append.append("</tr>");
			append.append("</table>");
			append.append("</td>");
			append.append("</tr>");
			append.append("</table>");
			append.append("</td>");

			if(i == 5)
			{
				append.append("</tr></table><table width=30><tr>");
				i = 1;
			}
			else
				i++;
		}

		html = html.replace("<?replace?>", append.toString());
		html = html.replace("<?price?>", Util.formatAdena(ConfigValue.TitleColorChangePrice));
		html = html.replace("<?price_id?>", String.valueOf(ConfigValue.TitleColorChangeItem));
		show(html, player);

	}

	public void change2(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(param[0].equalsIgnoreCase("FFFFFF"))
		{
			player.setTitleColor(Integer.decode("0xFFFFFF"));
			player.setVar("TitleColor", "0xFFFFFF");
			player.broadcastUserInfo(true);
			return;
		}

		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.TitleColorChangeItem);
		L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
		if(pay != null && pay.getCount() >= ConfigValue.TitleColorChangePrice || ConfigValue.TitleColorChangeFreePremium && player.getNetConnection().getBonus() > 1)
		{
			player.setTitleColor(Integer.decode("0x" + param[0]));
			player.setVar("TitleColor", "0x" + param[0]);
			if(!ConfigValue.TitleColorChangeFreePremium || player.getNetConnection().getBonus() <= 1)
				player.getInventory().destroyItem(pay, ConfigValue.TitleColorChangePrice, true);
			player.broadcastUserInfo(true);
		}
		else if(ConfigValue.TitleColorChangeItem == 57)
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
	}

	public void onLoad()
	{
		_log.info("Loaded Service: Nick color change");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}