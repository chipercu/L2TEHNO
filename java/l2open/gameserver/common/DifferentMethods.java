package l2open.gameserver.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemClass;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.Util;

public class DifferentMethods
{
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	public static void clear(L2Player player)
	{
		if(false)
		{
			for(L2ItemInstance item : player.getInventory().getItems())
			{
				if(item.getCount() == 1)
					player.sendMessage(item.getName() + " был удален.");
				else if(item.getCount() > 1)
					player.sendMessage(item.getCount() + " " + item.getName() + " было удалено.");
				player.getInventory().destroyItemByItemId(item.getItemId(), item.getCount(), true);
			}

			for(L2ItemInstance item : player.getWarehouse().listItems(ItemClass.ALL))
			{
				if(item.getCount() == 1)
					player.sendMessage(item.getName() + " был удален.");
				else if(item.getCount() > 1)
					player.sendMessage(item.getCount() + " " + item.getName() + " было удалено.");
				player.getWarehouse().destroyItem(item.getItemId(), item.getCount());
			}

			if(player.getClan() != null && player.isClanLeader())
				for(L2ItemInstance item : player.getClan().getWarehouse().listItems(ItemClass.ALL))
				{
					if(item.getCount() == 1)
						player.sendMessage(item.getName() + " был удален.");
					else if(item.getCount() > 1)
						player.sendMessage(item.getCount() + " " + item.getName() + " было удалено.");
					player.getClan().getWarehouse().destroyItem(item.getItemId(), item.getCount());
				}

			player.sendMessage("За подмену данных все предметы были удалены.");
		}
	}

	public static void communityNextPage(L2Player player, String link)
	{
		ICommunityHandler handler = CommunityHandler.getInstance().getCommunityHandler(link.split(":")[0]);
		if(handler != null)
			handler.parsecmd(link, player);
	}

	private static final Date d = new Date(System.currentTimeMillis());

	public static String time()
	{
		return TIME_FORMAT.format(d);
	}

	public static boolean getPay(L2Player player, int itemid, long count, boolean sendMessage)
	{
		if(itemid == -1)
			return true;
		int enoughItemCount = (int) (count - player.getInventory().getCountOf(itemid));

		if(count == 0)
			return true;

		if(player.getInventory().getCountOf(itemid) < count)
		{
			if(sendMessage)
				sendMessage(player, new CustomMessage("communityboard.enoughItemCount", player).addString(Util.formatAdena(enoughItemCount)).addItemName(itemid));

			return false;
		}
		else
		{
			player.getInventory().destroyItemByItemId(itemid, count, true);
			if(sendMessage)
				sendMessage(player, new CustomMessage("common.take.item", player).addString(Util.formatAdena(count)).addItemName(itemid));

			return true;
		}
	}

	public static String getPrice(L2Player player, long count, int itemid)
	{
		if(count != 0)
			return new CustomMessage("scripts.services.cost", player).addString(Util.formatAdena(count)).addItemName(itemid).toString();
		else
			return new CustomMessage("scripts.services.cost.free", player).toString();
	}

	public static void sendMessage(L2Player player, CustomMessage message)
	{
		player.sendPacket(new ExShowScreenMessage(message.toString(), 3000, ScreenMessageAlign.TOP_CENTER, true));
		player.sendMessage(message);
	}

	public static void addItem(L2Player player, int itemid, long count)
	{
		player.getInventory().addItem(itemid, count);
		player.sendMessage("Вы получили " + count + " " + getItemName(itemid));
	}

	public static long addMinutes(long count)
	{
		long MINUTE = count * 1000 * 60;
		return MINUTE;
	}

	public static long addDay(long count)
	{
		long DAY = count * 1000 * 60 * 60 * 24;
		return DAY;
	}

	public static String getItemName(int itemId)
	{
		return ItemTemplates.getInstance().getTemplate(itemId).getName();
	}

	public static String getNpcName(int npcId)
	{
		L2NpcTemplate template = NpcTable.getTemplate(npcId);
		return template.name;
	}

	public static String declension(L2Player player, long count, String Type)
	{
		String one = "";
		String two = "";
		String five = "";

		if(Type.equals("Days"))
		{
			one = new CustomMessage("common.declension.day.1", player).toString();
			two = new CustomMessage("common.declension.day.2", player).toString();
			five = new CustomMessage("common.declension.day.5", player).toString();
		}
		else if(Type.equals("Hour"))
		{
			one = new CustomMessage("common.declension.hour.1", player).toString();
			two = new CustomMessage("common.declension.hour.2", player).toString();
			five = new CustomMessage("common.declension.hour.5", player).toString();
		}
		else if(Type.equals("Piece"))
		{
			one = new CustomMessage("common.declension.piece.1", player).toString();
			two = new CustomMessage("common.declension.piece.2", player).toString();
			five = new CustomMessage("common.declension.piece.5", player).toString();
		}
		else if(Type.equals("Point"))
		{
			one = new CustomMessage("common.declension.point.1", player).toString();
			two = new CustomMessage("common.declension.point.2", player).toString();
			five = new CustomMessage("common.declension.point.5", player).toString();
		}
		else if(Type.equals("Monster"))
		{
			one = new CustomMessage("common.declension.monster.1", player).toString();
			two = new CustomMessage("common.declension.monster.2", player).toString();
			five = new CustomMessage("common.declension.monster.5", player).toString();
		}
		else if(Type.equals("Second"))
		{
			one = new CustomMessage("common.declension.second.1", player).toString();
			two = new CustomMessage("common.declension.second.2", player).toString();
			five = new CustomMessage("common.declension.second.5", player).toString();
		}

		if(count > 100)
			count %= 100;

		if(count > 20)
			count %= 10;

		switch((int)count)
		{
			case 1:
				return one.toString();
			case 2:
			case 3:
			case 4:
				return two.toString();
			default:
				return five.toString();
		}
	}

	public static String consider(L2Player player)
	{
		String _msg = "";
		if(player.hasBonus())
		{
			int Total, Day, Hour = 0;

			Total = (int) ((player.getNetConnection().getBonusExpire() - System.currentTimeMillis() / 1000L));
			Day = Math.round(Total / 60 / 60 / 24);
			Hour = (Total - Day * 24 * 60 * 60) / 60 / 60;

			if(Day >= 1)
				_msg = new CustomMessage("communityboard.premium.day.hour", player).addNumber(Day).toString();
			else if(Day < 1 && Hour >= 0)
				_msg = new CustomMessage("communityboard.premium.hour", player).addNumber(Hour).toString();
			else
				_msg = "<font color=\"LEVEL\"><a action=\"bypass -h _bbsscripts; ;services.RateBonus:list\">" + new CustomMessage("communityboard.buy.premium", player) + "</a></font>";
		}
		else
			_msg = "<font color=\"LEVEL\"><a action=\"bypass -h _bbsscripts; ;services.RateBonus:list\">" + new CustomMessage("communityboard.buy.premium", player) + "</a></font>";

		return _msg;
	}

	public static String buttonCab(L2Player player)
	{
		String _msg = "";
		StringBuilder html = new StringBuilder();
		String[] color = new String[] { "333333", "666666" };
		int colorN = 0;
		int block = ConfigValue.RateBonusTime.length / 2;
		if(player.hasBonus())
		{
			for(int i = 1; i <= ConfigValue.RateBonusTime.length; i++)
			{
				if(colorN > 1)
					colorN = 0;

				html.append("<table height=50 bgcolor=" + color[colorN] + ">");
				colorN++;
				html.append("<tr>");
				html.append("<td width=250 align=center>");
				html.append("<table border=0 cellspacing=2 cellpadding=3>");
				html.append("<tr>");
				html.append("<td align=right valign=top>");
				html.append("<button action=\"bypass -h _bbsscripts; ;services.RateBonus:update " + (i - 1) + ";_bbscabinet:premium\" back=\"l2ui_ch3.PremiumItemBtn_Down\" fore=\"l2ui_ch3.PremiumItemBtn\" width=\"32\" height=\"32\"/>");
				html.append("</td>");
				html.append("<td width=204 align=left valign=top>");
				html.append("<font color=\"0099FF\">" + new CustomMessage("communityboard.cabinet.premium.button.update", player).addString(ConfigValue.RateBonusTime[i - 1] + " " + declension(player, ConfigValue.RateBonusTime[i - 1], "Days")) + "</font>&nbsp;<br1>›&nbsp;" + new CustomMessage("scripts.services.cost", player).addString(Util.formatAdena(ConfigValue.RateBonusPrice[i - 1])).addString(getItemName(ConfigValue.RateBonusItem[i - 1])) + "");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table><br>");
				html.append(i == block ? "</td><td width=250 align=center valign=top>" : "");
				if(i == block)
					colorN = 1;

			}
			_msg = html.toString();
		}
		else
		{
			for(int i = 1; i <= ConfigValue.RateBonusTime.length; i++)
			{
				if(colorN > 1)
					colorN = 0;

				html.append("<table height=50 bgcolor=" + color[colorN] + ">");
				colorN++;
				html.append("<tr>");
				html.append("<td width=250 align=center>");
				html.append("<table border=0 cellspacing=2 cellpadding=3>");
				html.append("<tr>");
				html.append("<td align=right valign=top>");
				html.append("<button action=\"bypass -h _bbsscripts; ;services.RateBonus:get " + (i - 1) + ";_bbscabinet:premium\" back=\"l2ui_ch3.PremiumItemBtn_Down\" fore=\"l2ui_ch3.PremiumItemBtn\" width=\"32\" height=\"32\"/>");
				html.append("</td>");
				html.append("<td width=204 align=left valign=top>");
				html.append("<font color=\"0099FF\">" + new CustomMessage("communityboard.cabinet.premium.button", player).addString(String.valueOf(ConfigValue.RateBonusValue[i - 1])).addString(ConfigValue.RateBonusTime[i - 1] + " " + declension(player, ConfigValue.RateBonusTime[i - 1], "Days")) + "</font>&nbsp;<br1>›&nbsp;" + new CustomMessage("scripts.services.cost", player).addString(Util.formatAdena(ConfigValue.RateBonusPrice[i - 1])).addString(getItemName(ConfigValue.RateBonusItem[i - 1])) + "");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table><br>");
				html.append(i == block ? "</td><td width=250 align=center valign=top>" : "");
				if(i == block)
					colorN = 1;

			}
			_msg = html.toString();
		}
		return _msg;
	}

	public static String images(L2Player player)
	{
		String _msg = "";
		if(player.hasBonus())
			_msg = "<img src=\"branchsys.primeitem_symbol\" width=\"14\" height=\"14\">";
		else
			_msg = "<img src=\"branchsys.br_freeserver_mark\" width=\"14\" height=\"14\">";
		return _msg;
	}

	public static String getErrorHtml(L2Player player, String bypass)
	{
		String _msg = "";
		if(ConfigValue.FunnyError)
			_msg = Files.read(ConfigValue.CommunityBoardHtmlRoot + "fun_error.htm", player);
		else
			_msg = Files.read(ConfigValue.CommunityBoardHtmlRoot + "error.htm", player);
		_msg = _msg.replace("<?bypass?>", bypass);
		return _msg;
	}

	private static long offline_refresh = 0;
	private static int offline_count = 0;
	private static long online_refresh = 0;
	private static int online_count = 0;

	public static int getAllOfflineCount()
	{
		if(!ConfigValue.AllowOfflineTrade)
			return 0;

		long now = System.currentTimeMillis();
		if(now > offline_refresh)
		{
			offline_refresh = now + ConfigValue.OfflineRefresh*1000;
			offline_count = 0;
			for(L2Player player : L2ObjectsStorage.getPlayers())
				if(player.isInOfflineMode())
					offline_count++;
		}

		return offline_count;
	}

	public static int getOnlineCount()
	{
		long now = System.currentTimeMillis();
		if(now > online_refresh)
		{
			online_refresh = now + ConfigValue.OnlineRefresh*1000;
			online_count = 0;
			for(L2Player player : L2ObjectsStorage.getPlayers())
				if(!player.isInOfflineMode() && !player.isBot2())
					online_count++;
		}

		return online_count;
	}

	public static String getOnline()
	{
		String _msg = "";
		int online = getOnlineCount();
		int offtrade = L2ObjectsStorage.getAllOfflineCount();

		if(ConfigValue.OnlineCheatType == 1)
			_msg = String.valueOf(online + online * ConfigValue.OnlineCheatPercent / 100 + offtrade * ConfigValue.OfflineCheatPercent / 100);
		else if(ConfigValue.OnlineCheatEnable)
		{
			if(ConfigValue.OnlineCheatPercentEnable)
				_msg = String.valueOf(online + online * ConfigValue.OnlineCheatPercent / 100);
			else
				_msg = String.valueOf(online + ConfigValue.OnlineCheatCount);
		}
		else
			_msg = String.valueOf(online);

		return _msg;
	}

	public static String getOffline()
	{
		String _msg = "";
		int offtrade = L2ObjectsStorage.getAllOfflineCount();

		if(ConfigValue.OfflineCheatEnable && ConfigValue.AllowOfflineTrade && ConfigValue.OnlineCheatType == 0)
		{
			if(ConfigValue.OfflineCheatPercentEnable && ConfigValue.AllowOfflineTrade)
				_msg = String.valueOf(offtrade + offtrade * ConfigValue.OfflineCheatPercent / 100);
			else
				_msg = String.valueOf(offtrade + ConfigValue.OfflineCheatCount);
		}
		else
			_msg = String.valueOf(offtrade);

		return _msg;
	}

	public static CustomMessage getCastleName(L2Player player, int id)
	{
		return new CustomMessage("common.castle." + id, player);
	}

	public static String getItemColor(L2Player player, int item_id, long item_count)
	{
		if(player.getInventory().getCountOf(item_id) >= item_count)
			return "00ff00";
		return "ff0000";
	}

	public static CustomMessage getFortName(L2Player player, int id)
	{
		return new CustomMessage("common.fort." + id, player);
	}

	public static CustomMessage htmlClassNameNonClient(L2Player player, int classId)
	{
		if(classId < 0 || classId > 136 || (classId > 118 && classId < 123) || (classId > 57 && classId < 88))
			return new CustomMessage("utils.classId.name.default", player);
		else
			return new CustomMessage("utils.classId.name." + classId, player);
	}

	public static CustomMessage htmlStatusNameNonClient(L2Player player, int rank)
	{
		return new CustomMessage("utils.status." + rank, player);
	}

	/**
	 *@param list - Массив с данными целых чисел.
	 *@param id - Целое число которое нужно найти.
	 *@return Возврат результата. (Если <b>True</b> - Значит <b>id</b> найдено в <b>list</b>)
	 **/
	public static boolean findInIntList(int[] list, int id)
	{
		for(int i : list)
		{
			if(i == id)
				return true;
		}
		return false;
	}
}
