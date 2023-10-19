package l2open.gameserver.communitybbs.Manager;

import l2open.util.Files;

import java.util.StringTokenizer;

import javolution.util.FastList;

import l2open.config.ConfigValue;
import l2open.gameserver.model.*;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.entity.market.*;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.L2Armor;
import l2open.gameserver.templates.L2EtcItem;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2Item.Grade;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.xml.ItemTemplates;

/**
 * @author Diagod
 */
public class MarcketBBSManager extends BaseBBSManager
{
	private static MarcketBBSManager _instance = null;

	public static MarcketBBSManager getInstance()
	{
		if(_instance == null)
			_instance = new MarcketBBSManager();
		return _instance;
	}

	private void selectProduct(L2Player player, int objId)
	{
		L2ItemInstance item = player.getInventory().getItemByObjectId(objId);
		if(item == null/* || !checkItem(item)*/)
			return;

		StringBuilder sb = new StringBuilder();
		int enchant_level = item.getRealEnchantLevel();
		int augment_id = item.getAugmentationId();
		int attribute_fire = item.getDefenceFire();
		int attribute_water = item.getDefenceFire();
		int attribute_wind = item.getDefenceFire();
		int attribute_earth = item.getDefenceFire();
		int attribute_holy = item.getDefenceFire();
		int attribute_unholy = item.getDefenceFire();
		int att_count = attribute_fire + attribute_water + attribute_wind + attribute_earth + attribute_holy + attribute_unholy;
		long item_count = item.getCount();

		String icon = "l2ui_ch3.petitem_click";
		if(item.getItem().isPvP())
			icon = "icon.pvp_tab";
		else if(item.getItemId() == 9912)
			icon = "icon.fort_tab";

		sb.append("<table width=594 height=40 border=0>");
		sb.append("<tr>");
		sb.append("<td width=30 height=32 align=center>");
		sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0 background=\"").append(item.getItem().getIcon()).append("\">");
		sb.append("<tr>");
		sb.append("<td valign=top align=center>");
		sb.append("<img src=\"").append(icon).append("\" width=32 height=32>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("</td>");
		sb.append("<td width=400 align=left>");
		sb.append("<font color=LEVEL>").append(item.getName()).append("</font>&nbsp;<font color=ff8000>").append(item.getItem().getAdditionalName().length() > 0 ? "-&nbsp;" + item.getItem().getAdditionalName() : "").append("</font> ").append(enchant_level > 0 ? "+" + enchant_level : "");
		sb.append("<br1>");
		//sb.append(new CustomMessage("communityboard.commission.shop.sell.count", player).addString(Util.formatAdena(item_count)).addString(DifferentMethods.declension(player, (int) item_count, "Piece")).toString());
		sb.append("</td>");
		sb.append("<td width=160 align=right>");
		sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0>");
		sb.append("<tr>");
		if(item.getItem().isPvP())
		{
			sb.append("<td width=36 height=32 align=right>");
			sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0 background=\"icon.pvp_point_i00\">");
			sb.append("<tr>");
			sb.append("<td valign=top align=center>");
			sb.append("<img src=\"icon.pvp_tab\" width=32 height=32>");
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("</td>");
			sb.append("<td width=6 height=32 align=right>");
			sb.append("</td>");
		}
		if(enchant_level > 0)
		{
			sb.append("<td width=36 height=32 align=right>");
			sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0 background=\"icon.etc_blessed_scrl_of_ench_wp_s_i05\">");
			sb.append("<tr>");
			sb.append("<td valign=top align=center>");
			sb.append("<img src=\"l2ui_ch3.multisell_plusicon\" width=32 height=32>");
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("</td>");
			sb.append("<td width=6 height=32 align=right>");
			sb.append("</td>");
		}
		if(att_count > 0)
		{
			sb.append("<td width=36 height=32 align=right>");
			sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0 background=\"icon.skill1352\">");
			sb.append("<tr>");
			sb.append("<td valign=top align=center>");
			sb.append("<img src=\"l2ui_ch3.multisell_plusicon\" width=32 height=32>");
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("</td>");
			sb.append("<td width=6 height=32 align=right>");
			sb.append("</td>");
		}
		if(augment_id > 0)
		{
			sb.append("<td width=36 height=32 align=right>");
			sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0 background=\"icon.etc_mineral_unique_i03\">");
			sb.append("<tr>");
			sb.append("<td valign=top align=center>");
			sb.append("<img src=\"l2ui_ch3.shortcut_recipeicon\" width=32 height=32>");
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("</td>");
			sb.append("<td width=6 height=32 align=right>");
			sb.append("</td>");
		}
		sb.append("<td width=36 height=32 align=right>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<table width=594 border=0>");
		sb.append("<tr>");
		sb.append("<td width=594 height=10>");
		sb.append("<img src=\"l2ui.squaregray\" width=594 height=1>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");

		if(att_count > 0)
		{
			if(attribute_fire > 0)
			{
				String icon_fire = "icon.etc_fire_stone_i00";
				if(item.isWeapon() && attribute_fire > 150 || item.isArmor() && attribute_fire > 60)
				{
					icon_fire = "icon.etc_fire_crystal_i00";
				}
				sb.append("<table width=594 height=40 border=0>");
				sb.append("<tr>");
				sb.append("<td width=30 height=32 align=center>");
				sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0 background=\"").append(icon_fire).append("\">");
				sb.append("<tr>");
				sb.append("<td valign=top align=center>");
				sb.append("<img src=\"icon.panel_2\" width=32 height=32>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("<td width=400 align=left>");
				//sb.append("<font color=ef1c29>").append(new CustomMessage("common.element.0", player).toString()).append("</font>");
				sb.append("<br1>");
			//	sb.append("<font color=B59A75>").append(new CustomMessage("common.element.bonus", player).toString()).append("</font> <font color=ef1c29>").append(attribute_fire).append("</font>");
				sb.append("</td>");
				sb.append("<td width=160 align=right>");
				sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0>");
				sb.append("<tr>");
				sb.append("<td width=36 height=32 align=right>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table width=594 border=0>");
				sb.append("<tr>");
				sb.append("<td width=594 height=10>");
				sb.append("<img src=\"l2ui.squaregray\" width=594 height=1>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
			}

			if(attribute_water > 0)
			{
				String icon_water = "icon.etc_water_stone_i00";
				if(item.isWeapon() && attribute_water > 150 || item.isArmor() && attribute_water > 60)
				{
					icon_water = "icon.etc_water_crystal_i00";
				}
				sb.append("<table width=594 height=40 border=0>");
				sb.append("<tr>");
				sb.append("<td width=30 height=32 align=center>");
				sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0 background=\"").append(icon_water).append("\">");
				sb.append("<tr>");
				sb.append("<td valign=top align=center>");
				sb.append("<img src=\"icon.panel_2\" width=32 height=32>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("<td width=400 align=left>");
				//sb.append("<font color=1896de>").append(new CustomMessage("common.element.1", player).toString()).append("</font>");
				sb.append("<br1>");
				//sb.append("<font color=B59A75>").append(new CustomMessage("common.element.bonus", player).toString()).append("</font> <font color=1896de>").append(attribute_water).append("</font>");
				sb.append("</td>");
				sb.append("<td width=160 align=right>");
				sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0>");
				sb.append("<tr>");
				sb.append("<td width=36 height=32 align=right>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table width=594 border=0>");
				sb.append("<tr>");
				sb.append("<td width=594 height=10>");
				sb.append("<img src=\"l2ui.squaregray\" width=594 height=1>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
			}

			if(attribute_wind > 0)
			{
				String icon_wind = "icon.etc_wind_stone_i00";
				if(item.isWeapon() && attribute_wind > 150 || item.isArmor() && attribute_wind > 60)
				{
					icon_wind = "icon.etc_wind_crystal_i00";
				}
				sb.append("<table width=594 height=40 border=0>");
				sb.append("<tr>");
				sb.append("<td width=30 height=32 align=center>");
				sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0 background=\"").append(icon_wind).append("\">");
				sb.append("<tr>");
				sb.append("<td valign=top align=center>");
				sb.append("<img src=\"icon.panel_2\" width=32 height=32>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("<td width=400 align=left>");
				//sb.append("<font color=5ababd>").append(new CustomMessage("common.element.2", player).toString()).append("</font>");
				sb.append("<br1>");
				//sb.append("<font color=B59A75>").append(new CustomMessage("common.element.bonus", player).toString()).append("</font> <font color=5ababd>").append(attribute_wind).append("</font>");
				sb.append("</td>");
				sb.append("<td width=160 align=right>");
				sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0>");
				sb.append("<tr>");
				sb.append("<td width=36 height=32 align=right>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table width=594 border=0>");
				sb.append("<tr>");
				sb.append("<td width=594 height=10>");
				sb.append("<img src=\"l2ui.squaregray\" width=594 height=1>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
			}

			if(attribute_earth > 0)
			{
				String icon_earth = "icon.etc_earth_stone_i00";
				if(item.isWeapon() && attribute_earth > 150 || item.isArmor() && attribute_earth > 60)
				{
					icon_earth = "icon.etc_earth_crystal_i00";
				}
				sb.append("<table width=594 height=40 border=0>");
				sb.append("<tr>");
				sb.append("<td width=30 height=32 align=center>");
				sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0 background=\"").append(icon_earth).append("\">");
				sb.append("<tr>");
				sb.append("<td valign=top align=center>");
				sb.append("<img src=\"icon.panel_2\" width=32 height=32>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("<td width=400 align=left>");
				//sb.append("<font color=347021>").append(new CustomMessage("common.element.3", player).toString()).append("</font>");
				sb.append("<br1>");
				//sb.append("<font color=B59A75>").append(new CustomMessage("common.element.bonus", player).toString()).append("</font> <font color=347021>").append(attribute_earth).append("</font>");
				sb.append("</td>");
				sb.append("<td width=160 align=right>");
				sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0>");
				sb.append("<tr>");
				sb.append("<td width=36 height=32 align=right>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table width=594 border=0>");
				sb.append("<tr>");
				sb.append("<td width=594 height=10>");
				sb.append("<img src=\"l2ui.squaregray\" width=594 height=1>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
			}

			if(attribute_holy > 0)
			{
				String icon_holy = "icon.etc_holy_stone_i00";
				if(item.isWeapon() && attribute_holy > 150 || item.isArmor() && attribute_holy > 60)
				{
					icon_holy = "icon.etc_holy_crystal_i00";
				}
				sb.append("<table width=594 height=40 border=0>");
				sb.append("<tr>");
				sb.append("<td width=30 height=32 align=center>");
				sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0 background=\"").append(icon_holy).append("\">");
				sb.append("<tr>");
				sb.append("<td valign=top align=center>");
				sb.append("<img src=\"icon.panel_2\" width=32 height=32>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("<td width=400 align=left>");
				//sb.append("<font color=52d3ff>").append(new CustomMessage("common.element.4", player).toString()).append("</font>");
				sb.append("<br1>");
				//sb.append("<font color=B59A75>").append(new CustomMessage("common.element.bonus", player).toString()).append("</font> <font color=52d3ff>").append(attribute_holy).append("</font>");
				sb.append("</td>");
				sb.append("<td width=160 align=right>");
				sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0>");
				sb.append("<tr>");
				sb.append("<td width=36 height=32 align=right>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table width=594 border=0>");
				sb.append("<tr>");
				sb.append("<td width=594 height=10>");
				sb.append("<img src=\"l2ui.squaregray\" width=594 height=1>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
			}

			if(attribute_unholy > 0)
			{
				String icon_unholy = "icon.etc_unholy_stone_i00";
				if(item.isWeapon() && attribute_unholy > 150 || item.isArmor() && attribute_unholy > 60)
				{
					icon_unholy = "icon.etc_unholy_crystal_i00";
				}
				sb.append("<table width=594 height=40 border=0>");
				sb.append("<tr>");
				sb.append("<td width=30 height=32 align=center>");
				sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0 background=\"").append(icon_unholy).append("\">");
				sb.append("<tr>");
				sb.append("<td valign=top align=center>");
				sb.append("<img src=\"icon.panel_2\" width=32 height=32>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("<td width=400 align=left>");
				//sb.append("<font color=632970>").append(new CustomMessage("common.element.5", player).toString()).append("</font>");
				sb.append("<br1>");
				//sb.append("<font color=B59A75>").append(new CustomMessage("common.element.bonus", player).toString()).append("</font> <font color=632970>").append(attribute_unholy).append("</font>");
				sb.append("</td>");
				sb.append("<td width=160 align=right>");
				sb.append("<table width=30 height=32 cellspacing=0 cellpadding=0>");
				sb.append("<tr>");
				sb.append("<td width=36 height=32 align=right>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table width=594 border=0>");
				sb.append("<tr>");
				sb.append("<td width=594 height=10>");
				sb.append("<img src=\"l2ui.squaregray\" width=594 height=1>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
			}
		}

		sb.append("<table width=594 border=0>");
		if(item.isStackable() && item.getCount() > 1)
		{
			sb.append("<tr>");
			sb.append("<td width=594 align=center>");
		//	sb.append(new CustomMessage("communityboard.commission.shop.set.count", player).addString(Util.formatAdena(item_count)).addString(DifferentMethods.declension(player, (int) item_count, "Piece")).toString());
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("	<td width=594 align=center>");
			sb.append("<br><edit var=\"item_count\" width=200 height=10><br>");
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td width=594 height=10>");
			sb.append("<img src=\"l2ui.squaregray\" width=594 height=1>");
			sb.append("</td>");
			sb.append("</tr>");
		}
		sb.append("<tr>");
		sb.append("<td width=594 align=center>");
		//sb.append(new CustomMessage("communityboard.commission.shop.info", player).toString());
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td align=center>");
		sb.append("<table width=500 border=0 cellspacing=1 cellpadding=1>");
		sb.append("<tr>");
		sb.append("<td width=270 align=center>");
		sb.append("<edit var=\"price_coint\" width=100 height=10>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td width=270 align=center>");
		sb.append("<combobox var=\"price\" list=\"").append(getAvailablePrice()).append("\" width=100 height=15><br><br>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td width=270 align=center>");
		//sb.append("<button action=\"bypass _bbscommission:create-").append(objId).append("- $price - $price_coint - ").append(item.isStackable() && item.getCount() > 1 ? " $item_count " : "1").append("\" value=\"").append(new CustomMessage("communityboard.commission.shop.sell", player).toString()).append("\" width=200 height=31 back=\"L2UI_CT1.OlympiadWnd_DF_BuyEtc_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_BuyEtc\">");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("</td>");
		sb.append("</tr>");

		int need_item_id = 57;
		int need_item_count = 1000;
		/*if(item.isArmor())
		{
			need_item_id = Config.BBS_COMMISSION_ARMOR_PRICE[0];
			need_item_count = Config.BBS_COMMISSION_ARMOR_PRICE[1];
		}
		else if(item.isWeapon())
		{
			need_item_id = Config.BBS_COMMISSION_WEAPON_PRICE[0];
			need_item_count = Config.BBS_COMMISSION_WEAPON_PRICE[1];
		}
		else if(item.isAccessory())
		{
			need_item_id = Config.BBS_COMMISSION_JEWERLY_PRICE[0];
			need_item_count = Config.BBS_COMMISSION_JEWERLY_PRICE[1];
		}
		else
		{
			need_item_id = Config.BBS_COMMISSION_OTHER_PRICE[0];
			need_item_count = Config.BBS_COMMISSION_OTHER_PRICE[1];
		}*/

		if(need_item_id > 0 && need_item_count > 0)
		{
			sb.append("<tr>");
			sb.append("<td width=594 align=center>");
			//sb.append("<br>").append(new CustomMessage("communityboard.commission.shop.attention", player).addNumber(need_item_count).addItemName(need_item_id).toString()).append("");
			sb.append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");

		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "/commission/index.htm", player);
		html = html.replace("<?content?>", sb.toString());
		html = html.replace("<?pages?>", "");
		html = html.replace("<?category?>", "select");
		//html = html.replace("<?category_name?>", getCategoryName(player, "select"));
		separateAndSend(html, player);
	}

	public String getAvailablePrice()
	{
		String rewards = "";
		int[] list = {57, 4037};
		/*for(int id : list)
			if(rewards.isEmpty())
				rewards += DifferentMethods.getItemName(id);
			else
				rewards += ";" + DifferentMethods.getItemName(id);*/
		return rewards;
	}

	public void parsecmd(String command, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(!ConfigValue.EnableMarcket)
			return;
		if(command.equals("_marketpage;index")) // Главная Аукциона...
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "marcket/index.htm", player);
			content = content.replace("%totalbids%", "Всего товаров на рынке: "+AuctionRegistration.getInstance().getBidsCount(0));
			content = content.replace("%totalbids2%", "Всего товаров на аукционе: "+AuctionRegistration.getInstance().getBidsCount(1));
			content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");
			separateAndSend(content, player);
		}
		else if(command.startsWith("_marketpage;main;")) // страничка пользователя
		{
			if(!AuctionRegistration.getInstance().isPlayerRegistr(player.getObjectId()))
			{
				String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "marcket/reg_index.htm", player);
				separateAndSend(content, player);
				content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");
				return;
			}
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();

			int type = Integer.parseInt(st.nextToken());
			int page = Integer.parseInt(st.nextToken());
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "marcket/admin-page.htm", player);
			content = content.replace("%content%", getMarketPage(player, page, type));
			content = content.replace("%totalbids%", "Всего товаров на рынке: "+AuctionRegistration.getInstance().getBidsCount(0));
			content = content.replace("%totalbids2%", "Всего товаров на аукционе: "+AuctionRegistration.getInstance().getBidsCount(1));
			content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");
			separateAndSend(content, player);
		}
		else if(command.startsWith("_marketpage;add;")) // добавить товар на рынок...
		{
			if(!AuctionRegistration.getInstance().isPlayerRegistr(player.getObjectId()))
			{
				String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "marcket/reg_index.htm", player);
				content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");
				separateAndSend(content, player);
				return;
			}
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();

			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "marcket/add.htm", player);
			content = content.replace("%values%", AuctionRegistration.getInstance().getPriceList());
			content = content.replace("%charitems%", getAddItemsList(player, Integer.parseInt(st.nextToken())));
			content = content.replace("%taxprocent%", String.valueOf((int)(AuctionRegistration.MARKET_TAX * 100)));
			content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");
			separateAndSend(content, player);
		}
		else if(command.startsWith("_marketpage;info;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();

			separateAndSend(genBidInfoPage(player, Integer.parseInt(st.nextToken())), player);
		}
		else if(command.startsWith("_marketpage;cab;")) // свой кабинет...
		{
			if(!AuctionRegistration.getInstance().isPlayerRegistr(player.getObjectId()))
			{
				String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "marcket/reg_index.htm", player);
				content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");
				separateAndSend(content, player);
				return;
			}

			int type = 0;
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			type = Integer.parseInt(st.nextToken());

			separateAndSend(genCabPage(player, type), player);
		}
		else if(command.startsWith("_marketpage;marketadd;"))
		{
			StringTokenizer st = new StringTokenizer(command, "; ");
			st.nextToken();
			st.nextToken();

			int costItemCount = 0;
			int costItemId = 0;
			String tax = "";
			int itemObjId = 0;
			int type = 0;

			try
			{
				costItemCount = Integer.parseInt(st.nextToken());
				costItemId = AuctionRegistration.getInstance().getShortItemId(st.nextToken());
				tax = st.nextToken();
				itemObjId = Integer.parseInt(st.nextToken());
				type = Integer.parseInt(st.nextToken());
			}
			catch(NumberFormatException e)
			{
				return;
			}
			AuctionRegistration.getInstance().addLot(player.getObjectId(), itemObjId, costItemId, costItemCount, tax, type);
		}
		else if(command.startsWith("_marketpage;marketdel;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();

			AuctionRegistration.getInstance().deleteLot(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		}
		else if(command.startsWith("_marketpage;marketbuy")) // покупка итема...
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			// TODO: Сделать возможность делать ставки для аукциона.
			AuctionRegistration.getInstance().buyLot(player.getObjectId(), Integer.parseInt(st.nextToken()), 0);
		}
		else if(command.startsWith("_marketpage;reg")) // регистрация на рынке...
		{
			try
			{
				if(!AuctionRegistration.getInstance().isPlayerRegistr(player.getObjectId()))
				{
					StringTokenizer st = new StringTokenizer(command, "; ");
					st.nextToken();
					st.nextToken();

					String name = st.nextToken(); // Реальное имя игрока.
					String firstname = st.nextToken(); // Реальная фамилия игрока.
					String cash = st.nextToken() + st.nextToken(); // WM кошелек игрока.
					String data = "TODO"; // Дата регистрации...

					AuctionRegistration.getInstance().setRegistrPlayer(player, name, firstname, cash, data); 
				}
			}
			catch(Exception e)
			{}
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "marcket/index.htm", player);
			content = content.replace("%totalbids%", "Всего товаров на рынке: "+AuctionRegistration.getInstance().getBidsCount(0));
			content = content.replace("%totalbids2%", "Всего товаров на аукционе: "+AuctionRegistration.getInstance().getBidsCount(1));
			content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");
			separateAndSend(content, player);
		}
	}

	private String getMarketPage(L2Player player, int page, int type)
	{
		FastList<LotInfo> items = AuctionRegistration.getInstance().getAllBids(type); // 0 - для рынка, 1 - для Аукциона.
		StringBuilder sb = new StringBuilder();
		int numlines = page * AuctionRegistration.LOTS_PER_PAGE + AuctionRegistration.LOTS_PER_PAGE; 
		if(items != null && items.size() != 0)
		{
			sb.append("<table width=\"300\">"); 
			for(int i = page * AuctionRegistration.LOTS_PER_PAGE;i < numlines;i++)
			{
				if(items.size() - i <= 0)
					break;
				if(player.getObjectId() == items.get(i).objectId)
					break;
				String bidderName = AuctionRegistration.getInstance().getSellerName(items.get(i).objectId);
				sb.append(BidItem(items.get(i)));

				sb.append
				(
					"<tr>"+
					"<td>"+
					"<font color=\"808080\">Тип: " + 
					getItemType(getTemplate(items.get(i))) + 
					". Продавец: " + 
					bidderName + 
					"</font>"+
					"</td>"+
					"</tr>"
				);
			}
			sb.append("</table>");
			sb.append("<br>Страницы:&nbsp;");
			int pg = getMarketPagesCount(type);
			for(int i = 0;i < pg;i++)
				sb.append("<a action=\"bypass -h _marketpage;main;"+type+";"+i+"\">"+i+ "</a>&nbsp;");
		}
		else 
			sb.append("На рынке нету предметов !");
		return sb.toString();
	}

	private String getAddItemsList(L2Player player, int type)
	{
		StringBuilder tx = new StringBuilder();
		FastList<L2ItemInstance> charItems = new FastList<L2ItemInstance>();
		for(L2ItemInstance item: player.getInventory().getItems())
		{
			if(AuctionRegistration.getInstance().checkItemForMarket(item))
				charItems.add(item);
		}
		for(int i = 0;i < charItems.size();i++)
		{
			if(isAlreadyAdded(player, charItems.get(i)))
				continue;
			tx.append(""+/*<img src="+charItems.get(i).getItem().getIcon()+" width=32 height=32>*/"<button value=\"\" action=\"bypass -h _marketpage;marketadd; $count $value $tax "+charItems.get(i).getObjectId()+" "+type+"\" width=32 height=32 back=\""+charItems.get(i).getItem().getIcon()+"\" fore=\""+charItems.get(i).getItem().getIcon()+"\"> <font color=\"808080\">"+charItems.get(i).getName() + " +" + charItems.get(i).getRealEnchantLevel() + "</font>");
		}
		return tx.toString();
	}

	private String getBuyItemsList(int playerId, int type, int type2)
	{
		StringBuilder tx = new StringBuilder();
		FastList<Integer> charItems = new FastList<Integer>();
		
		type2 = 2;

		FastList<LotInfo> lot = AuctionRegistration.getInstance().getAllBids(type2); // 2 - рынок, 3 - аукцион
		if(type == 1) // список купленых товаров...
		{
			for(LotInfo item : lot)
				if(item.buyrId == playerId)
					charItems.add(item.itemId);
		}
		else if(type == 2) // список проданых товаров...
		{
			for(LotInfo item : lot)
				if(item.objectId == playerId)
					charItems.add(item.itemId);
		}

		for(int i = 0;i < charItems.size();i++)
		{
			
			tx.append("<img src="+charItems.get(i)/*.getItem().getIcon()*/+" width=32 height=32><font color=\"808080\">"+charItems.get(i)/*.getName()*/ + " +" + charItems.get(i)/*.getRealEnchantLevel()*/ + "</font>");
		}
		return tx.toString();
	}

	private String genBidInfoPage(L2Player player, int bidId)
	{
		LotInfo bid = AuctionRegistration.getInstance().getLotById(bidId);
		String priceName = AuctionRegistration.getInstance().getShortItemName(bid.cashValue);
		String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "marcket/bid_info.htm", player);
		content = content.replace("%itemtitle%", bid.itemName);
		content = content.replace("%itemname%", bid.itemName+ " (" + getItemGrade(getTemplate(bid)) + " Grade)");
		content = content.replace("%itemtype%", getItemType(getTemplate(bid)));
		content = content.replace("%itemaugmentation%", getItemById(bid.itemObjectId).isAugmented() ? "Да" : "Нет");
		content = content.replace("%enchantlevel%", String.valueOf(getItemById(bid.itemObjectId).getRealEnchantLevel()));
		content = content.replace("%itemprice%", bid.cashCount + " <font color=\"LEVEL\">" + priceName + "</font>");
		content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");

		if(bid.type == 1 || bid.type == 3)
		{
			double mtax = bid.cashCount * AuctionRegistration.MARKET_TAX;
			content = content.replace("%markettax%", (int)mtax + " <font color=\"LEVEL\">" + priceName + "</font> (Полная цена: " + (bid.cashCount + (int)mtax) + " <font color\"LEVEL\">" + priceName + "</font>)");
		}
		else
			content = content.replace("%markettax%", "Платит продавец");
		content = content.replace("%seller%", AuctionRegistration.getInstance().getSellerName(bid.objectId));
		if(player.getObjectId() == bid.objectId)
			content = content.replace("%LINKS%", "<a action=\"bypass -h _marketpage;marketdel;" + player.getObjectId() + ";" + bid.itemObjectId + "\">Убрать с рынка</a>");
		else
			content = content.replace("%LINKS%", "<a action=\"bypass -h _marketpage;marketbuy;" + bid.itemObjectId + "\">Купить</a>");
		return content;
	}

	private String genCabPage(L2Player player, int type)
	{
		String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "marcket/904.htm", player);
		content = content.replace("%charname%", player.getName());
		content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");

		StringBuilder tx = new StringBuilder();
		FastList<LotInfo> bids = AuctionRegistration.getInstance().getAllPlayerBids(player.getObjectId(), type);
		if(bids != null && bids.size() != 0)
		{
			int i = 1;
			for(LotInfo bid: bids)
			{
				tx.append
				(
					"<table width=250>"+
					"<tr><td>"+i+".</td>"+
					"<td><img src="+getItemById(bid.itemObjectId).getItem().getIcon()+" width=32 height=32></td>"+
					"<td>"+bid.itemName+" +"+getItemById(bid.itemObjectId).getRealEnchantLevel()+"&nbsp;</td></tr>"+
					"</table>"+
					"&nbsp;<a action=\"bypass -h _marketpage;info;" + 
					bid.itemObjectId + 
					"\">Инфо</a>"+
					"<a action=\"bypass -h _marketpage;marketdel;" + 
					player.getObjectId() + 
					";" + 
					bid.itemObjectId + "\">Убрать с рынка</a><br>");
				i++;
			}
		}
		else
			tx.append("У Вас нет предметов на рынке.");
		content = content.replace("%charitems%", tx.toString());
		return content;
	}

	private String BidItem(LotInfo bid)
	{
		// tx.append(""+/*<img src="+charItems.get(i).getItem().getIcon()+" width=32 height=32>*/"<button value=\"\" action=\"bypass -h _marketpage;marketadd; $count $value $tax "+charItems.get(i).getObjectId()+" "+type+"\" width=32 height=32 back=\""+charItems.get(i).getItem().getIcon()+"\" fore=\""+charItems.get(i).getItem().getIcon()+"\"> <font color=\"808080\">"+charItems.get(i).getName() + " +" + charItems.get(i).getRealEnchantLevel() + "</font>");
		String info = "";

		info += "<tr><td><button value=\"\" action=\"bypass -h _marketpage;info;"+bid.itemObjectId+"\" width=32 height=32 back=\""+getItemById(bid.itemObjectId).getItem().getIcon()+"\" fore=\""+getItemById(bid.itemObjectId).getItem().getIcon()+"\">"+

		"&nbsp;" + 
		"(" + 
		getItemGrade(getTemplate(bid)) + 
		")&nbsp;+" +  
		getItemById(bid.itemObjectId).getRealEnchantLevel() + 
		"</td></tr>";

		//info += "<tr><td><a action=\"bypass -h _marketpage;info;" + bid.itemObjectId + "\">" + bid.itemName + "</a>&nbsp;" + "(" + getItemGrade(getTemplate(bid)) + ")&nbsp;+" +  getItemById(bid.itemObjectId).getRealEnchantLevel() +  "</td></tr>";
		info += "<tr><td><font color=\"808080\">Цена: " + bid.cashCount  + " " + AuctionRegistration.getInstance().getShortItemName(bid.cashValue) + "</font></td></tr>";
		return info;
	}

	public int getMarketPagesCount(int type)
	{
		int pages = 0;
		for(int allbids = AuctionRegistration.getInstance().getBidsCount(type);allbids > 0;allbids -= AuctionRegistration.LOTS_PER_PAGE)
		{
			pages++;
		}
		return pages;
	}

	private boolean isAlreadyAdded(L2Player player, L2ItemInstance item)
	{
		FastList<LotInfo> bids = AuctionRegistration.getInstance().getAllSellPlayerBids(player.getObjectId());
		for(LotInfo bid: bids)
		{
			if(bid.itemObjectId == item.getObjectId())
				return true;
		}
		return false;
	}

	private String getItemType(L2Item item)
	{
		if(item instanceof L2Weapon)
			return "Оружие";
		else if(item instanceof L2Armor)
			return "Броня";
		else if(item instanceof L2EtcItem)
			return "Другое";
		return "";
	}

	private String getItemGrade(L2Item item)
	{
		String grade = "";
		Grade itemGrade = item.getItemGrade();
		switch(itemGrade)
		{
			case NONE:
				grade = "None";
				break;
			case D:
				grade = "D";
				break;
			case C:
				grade = "C";
				break;
			case B:
				grade = "B";
				break;
			case A:
				grade = "A";
				break;
			case S:
				grade = "S";
				break;
			case S80:
				grade = "S80";
				break;
			case S84:
				grade = "S84";
				break;
		}
		return "<font color=\"LEVEL\">" + grade + "</font>";
	}

	private L2Item getTemplate(LotInfo bid)
	{
		return ItemTemplates.getInstance().getTemplate(bid.itemId);
	}

	public L2ItemInstance getItemById(int itemObjectId)
	{
		return PlayerData.getInstance().restoreFromDb(itemObjectId);
	}

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{
	}
}