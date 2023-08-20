package communityboard;

import java.util.StringTokenizer;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.Func;
import l2open.gameserver.skills.funcs.FuncAdd;
import l2open.gameserver.skills.funcs.FuncDiv;
import l2open.gameserver.skills.funcs.FuncEnchant;
import l2open.gameserver.skills.funcs.FuncMul;
import l2open.gameserver.skills.funcs.FuncSet;
import l2open.gameserver.skills.funcs.FuncSub;
import l2open.gameserver.skills.funcs.FuncTemplate;
import l2open.gameserver.templates.L2Armor;
import l2open.gameserver.templates.L2EtcItem;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.Util;

public class CommunityBoardItemInfo extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	private static int CommunityBoardItemInfo = 1 << 133;
	private String val1 = "";
	private String val2 = "";
	private String val3 = "";
	private String val4 = "";

	private static int RUSSIAN = 1;

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player)
	{}

	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	@SuppressWarnings("rawtypes")
	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}

	private static enum Commands
	{
		_bbsitemlist,
		_bbsitematributes,
		_bbsitemstats,
		_bbsitemskills,
		_bbsarmorinfoid,
		_bbsarmorinfoname,
		_bbsweaponinfoid,
		_bbsweaponinfoname,
		_bbsiteminfoid,
		_bbsiteminfoname
	}

	@Override
	public void parsecmd(String command, L2Player activeChar)
	{
		if(activeChar.getEventMaster() != null && activeChar.getEventMaster().blockBbs())
			return;
		if(activeChar.is_block || activeChar.isInEvent() > 0)
			return;
		//if((Functions.script & CommunityBoardItemInfo) != CommunityBoardItemInfo)
		//	return;
		StringTokenizer st = new StringTokenizer(command, " ");
		String cmd = st.nextToken();

		val1 = "";
		val2 = "";
		val3 = "";
		val4 = "";

		if(st.countTokens() == 1)
			val1 = st.nextToken();
		else if(st.countTokens() == 2)
		{
			val1 = st.nextToken();
			val2 = st.nextToken();
		}
		else if(st.countTokens() == 3)
		{
			val1 = st.nextToken();
			val2 = st.nextToken();
			val3 = st.nextToken();
		}
		else if(st.countTokens() == 4)
		{
			val1 = st.nextToken();
			val2 = st.nextToken();
			val3 = st.nextToken();
			val4 = st.nextToken();
		}

		if(cmd.equalsIgnoreCase("_bbsitemlist"))
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "/wiki/iteminfo/list.htm", activeChar);
			ShowBoard.separateAndSend(content, activeChar);
		}
		else if(cmd.equalsIgnoreCase("_bbsarmorinfoid"))
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "/wiki/iteminfo/iteminfo.htm", activeChar);
			content = content.replace("%iteminfo%", generateArmorInfo(activeChar, Integer.parseInt(val1)));
			ShowBoard.separateAndSend(content, activeChar);

		}
		else if(cmd.equalsIgnoreCase("_bbsarmorinfoname"))
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "/wiki/iteminfo/iteminfo.htm", activeChar);
			String str = null;

			if(!val1.equals(""))
				str = val1;

			if(!val2.equals(""))
				str = val1 + " " + val2;

			if(!val3.equals(""))
				str = val1 + " " + val2 + " " + val3;

			if(!val4.equals(""))
				str = val1 + " " + val2 + " " + val3 + " " + val4;

			content = content.replace("%iteminfo%", generateArmorInfo(activeChar, str));
			ShowBoard.separateAndSend(content, activeChar);

		}
		else if(cmd.equalsIgnoreCase("_bbsweaponinfoid"))
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "/wiki/iteminfo/iteminfo.htm", activeChar);
			content = content.replace("%iteminfo%", generateWeaponInfo(activeChar, Integer.parseInt(val1)));
			ShowBoard.separateAndSend(content, activeChar);
		}
		else if(cmd.equalsIgnoreCase("_bbsweaponinfoname"))
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "/wiki/iteminfo/iteminfo.htm", activeChar);
			String str = null;

			if(!val1.equals(""))
				str = val1;

			if(!val2.equals(""))
				str = val1 + " " + val2;

			if(!val3.equals(""))
				str = val1 + " " + val2 + " " + val3;

			if(!val4.equals(""))
				str = val1 + " " + val2 + " " + val3 + " " + val4;

			content = content.replace("%iteminfo%", generateWeaponInfo(activeChar, str));
			ShowBoard.separateAndSend(content, activeChar);
		}
		else if(cmd.equalsIgnoreCase("_bbsiteminfoid"))
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "/wiki/iteminfo/iteminfo.htm", activeChar);
			content = content.replace("%iteminfo%", generateItemInfo(activeChar, Integer.parseInt(val1)));
			ShowBoard.separateAndSend(content, activeChar);
		}
		else if(cmd.equalsIgnoreCase("_bbsiteminfoname"))
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "/wiki/iteminfo/iteminfo.htm", activeChar);
			String str = null;

			if(!val1.equals(""))
				str = val1;

			if(!val2.equals(""))
				str = val1 + " " + val2;

			if(!val3.equals(""))
				str = val1 + " " + val2 + " " + val3;

			if(!val4.equals(""))
				str = val1 + " " + val2 + " " + val3 + " " + val4;

			content = content.replace("%iteminfo%", generateItemInfo(activeChar, str));
			ShowBoard.separateAndSend(content, activeChar);
		}
		else if(cmd.equalsIgnoreCase("_bbsitemskills"))
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "/wiki/iteminfo/iteminfo.htm", activeChar);
			content = content.replace("%iteminfo%", generateItemSkills(activeChar, Integer.parseInt(val1)));
			ShowBoard.separateAndSend(content, activeChar);
		}
		else if(cmd.equalsIgnoreCase("_bbsitemstats"))
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "/wiki/iteminfo/iteminfo.htm", activeChar);
			content = content.replace("%iteminfo%", generateItemStats(activeChar, Integer.parseInt(val1)));
			ShowBoard.separateAndSend(content, activeChar);
		}
		else if(cmd.equalsIgnoreCase("_bbsitematributes"))
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "/wiki/iteminfo/iteminfo.htm", activeChar);
			content = content.replace("%iteminfo%", generateItemAttribute(activeChar, Integer.parseInt(val1)));
			ShowBoard.separateAndSend(content, activeChar);
		}
		else
			separateAndSend(DifferentMethods.getErrorHtml(activeChar, command), activeChar);
	}

	private String generateItemSkills(L2Player player, int id)
	{
		StringBuilder result = new StringBuilder();

		result.append("<table width=690 border=0>");

		L2Item temp = ItemTemplates.getInstance().getTemplate(id);

		String str;
		if(temp.isWeapon())
			str = "_bbsweaponinfoid";
		else if(temp.isArmor() || temp.isAccessory())
			str = "_bbsarmorinfoid";
		else
			str = "_bbsiteminfoid";

		if(temp.getAttachedSkills() != null)
			for(L2Skill skill : temp.getAttachedSkills())
			{
				result.append("<tr>");
				result.append("<td FIXWIDTH=50 align=right valign=top>");
				result.append("<img src=\"").append(skill.getIcon()).append("\" width=32 height=32>");
				result.append("</td>");
				result.append("<td FIXWIDTH=671 align=left valign=top>");
				result.append(new CustomMessage("communityboard.wiki.items.skill.name", player).addSkillName(skill)).append("<br1>").append(new CustomMessage("communityboard.wiki.items.skill.id", player).addNumber(skill.getId())).append(new CustomMessage("communityboard.wiki.items.skill.level", player).addNumber(skill.getLevel()));
				result.append("</td>");
				result.append("</tr>");
			}

		result.append("</table>");

		result.append(InfoButton(player, str, temp.getItemId()));
		return result.toString();
	}

	private String generateItemStats(L2Player player, int id)
	{
		StringBuilder result = new StringBuilder();

		result.append("<table width=690 border=0>");

		L2Item temp = ItemTemplates.getInstance().getTemplate(id);

		String str;
		if(temp.isWeapon())
			str = "_bbsweaponinfoid";
		else if(temp.isArmor() || temp.isAccessory())
			str = "_bbsarmorinfoid";
		else
			str = "_bbsiteminfoid";

		for(FuncTemplate func : temp.getAttachedFuncs())
		{
			if(getFunc(player, func) != null)
				result.append("<tr><td>› <font color=\"b09979\">").append(getFunc(player, func)).append("</font></td></tr><br>");
			//_log.info("func: " + func._stat);
		}

		result.append("</table>");

		result.append(InfoButton(player, str, temp.getItemId()));
		return result.toString();
	}

	private String generateItemAttribute(L2Player player, int id)
	{
		StringBuilder result = new StringBuilder();

		L2Item temp = ItemTemplates.getInstance().getTemplate(id);

		String str;
		if(temp.isWeapon())
			str = "_bbsweaponinfoid";
		else if(temp.isArmor() || temp.isAccessory())
			str = "_bbsarmorinfoid";
		else
			str = "_bbsiteminfoid";

		/*if(temp.getBaseAttributeValue(Element.FIRE) > 0)
			result.append(AttributeHtml(player, "etc_fire_stone_i00", new CustomMessage("common.element.0", player).toString(), temp.getBaseAttributeValue(Element.FIRE)));
		if(temp.getBaseAttributeValue(Element.WATER) > 0)
			result.append(AttributeHtml(player, "etc_water_stone_i00", new CustomMessage("common.element.1", player).toString(), temp.getBaseAttributeValue(Element.WATER)));
		if(temp.getBaseAttributeValue(Element.WIND) > 0)
			result.append(AttributeHtml(player, "etc_wind_stone_i00", new CustomMessage("common.element.2", player).toString(), temp.getBaseAttributeValue(Element.WIND)));
		if(temp.getBaseAttributeValue(Element.EARTH) > 0)
			result.append(AttributeHtml(player, "etc_earth_stone_i00", new CustomMessage("common.element.3", player).toString(), temp.getBaseAttributeValue(Element.EARTH)));
		if(temp.getBaseAttributeValue(Element.HOLY) > 0)
			result.append(AttributeHtml(player, "etc_holy_stone_i00", new CustomMessage("common.element.4", player).toString(), temp.getBaseAttributeValue(Element.HOLY)));
		if(temp.getBaseAttributeValue(Element.UNHOLY) > 0)
			result.append(AttributeHtml(player, "etc_unholy_stone_i00", new CustomMessage("common.element.5", player).toString(), temp.getBaseAttributeValue(Element.UNHOLY)));*/

		result.append(InfoButton(player, str, temp.getItemId()));
		return result.toString();
	}

	private String generateItemInfo(L2Player player, String name)
	{
		StringBuilder result = new StringBuilder();

		for(L2Item temp : ItemTemplates.getInstance().getAllTemplates())
			if(temp != null && !temp.isArmor() && !temp.isWeapon() && !temp.isAccessory() && (temp.getName() == name || val2.equals("") ? temp.getName().startsWith(name) : temp.getName().contains(name) || temp.getName().equals(name) || temp.getName().equalsIgnoreCase(name)))
			{
				result.append("<center><table width=690>");
				result.append("<tr>");
				result.append("<td WIDTH=690 align=center valign=top>");
				result.append("<center><button value=\"");
				result.append(temp.getName());
				result.append("\" action=\"bypass -h _bbsiteminfoid ").append(temp.getItemId()).append("\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"></center>");
				result.append("</td>");
				result.append("</tr>");
				result.append("</table></center>");
			}

		return result.toString();
	}

	private String generateItemInfo(L2Player player, int id)
	{
		StringBuilder result = new StringBuilder();

		L2Item temp = ItemTemplates.getInstance().getTemplate(id);
		if(temp != null && !temp.isArmor() && !temp.isWeapon() && !temp.isAccessory())
		{
			L2EtcItem etcitem = (L2EtcItem) temp;
			String icon = etcitem.getIcon();
			if(icon == null || icon.isEmpty())
				icon = "icon.etc_question_mark_i00";

			result.append("<center><table width=690>");
			result.append("<tr>");
			result.append("<td WIDTH=690 align=center valign=top>");
			result.append("<table border=0 cellspacing=4 cellpadding=3>");
			result.append("<tr>");
			result.append("<td FIXWIDTH=50 align=right valign=top>");
			result.append("<img src=\"").append(icon).append("\" width=32 height=32>");
			result.append("</td>");
			result.append("<td FIXWIDTH=671 align=left valign=top>");
			result.append("<font color=\"0099FF\">").append(player.getLangId() == RUSSIAN ? "Название предмета:</font> " : "Item name:</font> ").append(Files.htmlItemName(etcitem.getItemId())).append("<br1><font color=\"LEVEL\">").append(player.getLangId() == RUSSIAN ? "ID предмета:</font> " : "Item ID:</font> ").append(etcitem.getItemId()).append("&nbsp;");
			result.append("</td>");
			result.append("</tr>");
			result.append("</table>");
			result.append("<table border=0 cellspacing=0 cellpadding=0>");
			result.append("<tr>");
			result.append("<td width=690>");
			result.append("<img src=\"l2ui.squaregray\" width=\"690\" height=\"1\">");
			result.append("</td>");
			result.append("</tr>");
			result.append("</table>");
			result.append("</td>");
			result.append("</tr>");
			result.append("</table>");
			result.append("<br><table width=690>");
			result.append("<tr>");
			result.append("<td>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Тип предмета: " : "Item type: ").append("</font>").append(etcitem.getItemType().toString()).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Вес: " : "Weight: ").append("</font>").append(etcitem.getWeight()).append("&nbsp;").append("<br>");
			result.append("</td>");
			result.append("<td>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Цена продажи: " : "Sale price: ").append("</font>").append(Util.formatAdena(etcitem.getReferencePrice() / 2)).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Будет стыковаться: " : "It will be docked: ").append("</font>").append(etcitem.isStackable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("</td>");
			result.append("<td>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Временный предмет: " : "A temporary items: ").append("</font>").append(etcitem.getDurability() > 0 ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Можно выбросить: " : "You can throw: ").append("</font>").append(etcitem.isDropable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("</td>");
			result.append("<td>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Можно продать: " : "Can be sold: ").append("</font>").append(etcitem.isSellable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Можно обменять: " : "Can be exchanged: ").append("</font>").append(etcitem.isTradeable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("</td>");
			result.append("</tr>");
			result.append("</table></center>");

			if(etcitem.getAttachedSkills() != null && etcitem.getAttachedSkills().length > 0)
				result.append(Button(player, new CustomMessage("communityboard.wiki.items.list.skill", player).toString(), "_bbsitemskills", etcitem.getItemId()));
		}
		else
			result.append(player.getLangId() == RUSSIAN ? "<table width=755><tr><td width=755><center><font name=\"hs12\" color=\"FF0000\">Предмет не найден</font></center></td></tr></table><br>" : "<table width=755><tr><td width=755><center><font name=\"hs12\" color=\"FF0000\">Item not found</font></center></td></tr></table><br>");

		return result.toString();
	}

	private String generateWeaponInfo(L2Player player, String name)
	{
		StringBuilder result = new StringBuilder();

		for(L2Item temp : ItemTemplates.getInstance().getAllTemplates())
			if(temp != null && temp.isWeapon() && (temp.getName() == name || val2.equals("") ? temp.getName().startsWith(name) : temp.getName().contains(name) || temp.getName().equals(name) || temp.getName().equalsIgnoreCase(name)))
			{
				result.append("<center><table width=690>");
				result.append("<tr>");
				result.append("<td WIDTH=690 align=center valign=top>");
				result.append("<center><button value=\"");
				result.append(temp.getName());
				result.append("\" action=\"bypass -h _bbsweaponinfoid ").append(temp.getItemId()).append("\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"></center>");
				result.append("</td>");
				result.append("</tr>");
				result.append("</table></center>");
			}

		return result.toString();
	}

	private String generateWeaponInfo(L2Player player, int id)
	{
		StringBuilder result = new StringBuilder();

		L2Item temp = ItemTemplates.getInstance().getTemplate(id);
		if(temp != null && temp.isWeapon())
		{
			L2Weapon weapon = (L2Weapon) temp;
			String icon = weapon.getIcon();
			if(icon == null || icon.isEmpty())
				icon = "icon.etc_question_mark_i00";

			result.append("<center><table width=690>");
			result.append("<tr>");
			result.append("<td WIDTH=690 align=center valign=top>");
			result.append("<table border=0 cellspacing=4 cellpadding=3>");
			result.append("<tr>");
			result.append("<td FIXWIDTH=50 align=right valign=top>");
			result.append("<img src=\"").append(icon).append("\" width=32 height=32>");
			result.append("</td>");
			result.append("<td FIXWIDTH=671 align=left valign=top>");
			result.append("<font color=\"0099FF\">").append(player.getLangId() == RUSSIAN ? "Название предмета:</font> " : "Item name:</font> ").append(Files.htmlItemName(weapon.getItemId())).append(" (<font color=\"b09979\">").append(weapon.getItemType().toString()).append("</font>)<br1><font color=\"LEVEL\">").append(player.getLangId() == RUSSIAN ? "ID предмета:</font> " : "Item ID:</font> ").append(weapon.getItemId()).append("&nbsp;");
			result.append("</td>");
			result.append("</tr>");
			result.append("</table>");
			result.append("<table border=0 cellspacing=0 cellpadding=0>");
			result.append("<tr>");
			result.append("<td width=690>");
			result.append("<img src=\"l2ui.squaregray\" width=\"690\" height=\"1\">");
			result.append("</td>");
			result.append("</tr>");
			result.append("</table>");
			result.append("</td>");
			result.append("</tr>");
			result.append("</table>");
			result.append("<br><table width=690>");
			result.append("<tr>");
			result.append("<td>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Грейд оружия: " : "Weapon grade: ").append("</font>").append(weapon.getCrystalType()).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Слот: " : "Slot: ").append("</font>").append(getBodyPart(player, weapon)).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Разбивается на кристаллы: " : "Divided into crystals: ").append("</font>").append(weapon.isCrystallizable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			if(weapon.isCrystallizable())
				result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Количество кристаллов: " : "Number of crystals: ").append("</font>").append(weapon.getCrystalCount()).append("&nbsp;").append("<br>");
			else
				result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Количество кристаллов:</font> 0" : "Number of crystals:</font> 0").append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Потребление спиритов: " : "Consume spiritshot: ").append("</font>").append(weapon.getSpiritShotCount()).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Оружие камаелей: " : "Kamael weapons: ").append("</font>").append(weapon.getKamaelAnalog() > 0 ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("</td>");
			result.append("<td>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Вес: " : "Weight: ").append("</font>").append(weapon.getWeight()).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Цена продажи: " : "Sale price: ").append("</font>").append(Util.formatAdena(weapon.getReferencePrice() / 2)).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Будет стыковаться: " : "It will be docked: ").append("</font>").append(weapon.isStackable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Временный предмет: " : "A temporary item: ").append("</font>").append(weapon.getDurability() > 0 ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Можно выбросить: " : "You can throw: ").append("</font>").append(weapon.isDropable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Реюз Атаки: " : "Attack reuse: ").append("</font>").append(weapon.getAttackReuseDelay() / 1000).append(" сек.").append("&nbsp;").append("</font><br>");
			result.append("</td>");
			result.append("<td>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Можно продать: " : "Can be sold: ").append("</font>").append(weapon.isSellable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			//result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Можно вставить аугментацию: " : "You can insert the argument: ").append("</font>").append(weapon.isAugmentable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			//result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Можно вставить атрибут: " : "You can insert an attribute: ").append("</font>").append(weapon.isAttributable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Можно обменять: " : "Can be exchanged: ").append("</font>").append(weapon.isTradeable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Потребление сосок: " : "Consume soulshot: ").append("</font>").append(weapon.getSoulShotCount()).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Потребление МП: " : "Consume Mp: ").append("</font>").append(weapon.getMpConsume()).append("&nbsp;").append("<br>");
			result.append("</td>");
			result.append("</tr>");
			result.append("</table></center>");

			if(weapon.getAttachedSkills() != null && weapon.getAttachedSkills().length > 0)
				result.append(Button(player, new CustomMessage("communityboard.wiki.items.list.skill", player).toString(), "_bbsitemskills", weapon.getItemId()));
			if(weapon.getAttachedFuncs().length > 0)
				result.append(Button(player, new CustomMessage("communityboard.wiki.items.list.bonus", player).toString(), "_bbsitemstats", weapon.getItemId()));
			if(weapon.isAttAtack())
				result.append(Button(player, new CustomMessage("communityboard.wiki.items.list.att", player).toString(), "_bbsitematributes", weapon.getItemId()));
		}
		else
			result.append(player.getLangId() == RUSSIAN ? "<table width=690><tr><td width=690><center><font name=\"hs12\" color=\"FF0000\">Предмет не найден</font></center></td></tr></table><br>" : "<table width=690><tr><td width=690><center><font name=\"hs12\" color=\"FF0000\">Item not found</font></center></td></tr></table><br>");

		return result.toString();
	}

	private String generateArmorInfo(L2Player player, String name)
	{
		StringBuilder result = new StringBuilder();

		for(L2Item temp : ItemTemplates.getInstance().getAllTemplates())
			if(temp != null && (temp.isArmor() || temp.isAccessory()) && (temp.getName() == name || val2.equals("") ? temp.getName().startsWith(name) : temp.getName().contains(name) || temp.getName().startsWith(name) || temp.getName().equals(name) || temp.getName().equalsIgnoreCase(name)))
			{
				result.append("<center><table width=690>");
				result.append("<tr>");
				result.append("<td WIDTH=690 align=center valign=top>");
				result.append("<center><button value=\"");
				result.append(temp.getName());
				result.append("\" action=\"bypass -h _bbsarmorinfoid ").append(temp.getItemId()).append("\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"></center>");
				result.append("</td>");
				result.append("</tr>");
				result.append("</table></center>");
			}

		return result.toString();
	}

	private String generateArmorInfo(L2Player player, int id)
	{
		StringBuilder result = new StringBuilder();

		L2Item temp = ItemTemplates.getInstance().getTemplate(id);
		if(temp != null && (temp.isArmor() || temp.isAccessory()))
		{
			L2Armor armor = (L2Armor) temp;
			String icon = armor.getIcon();
			if(icon == null || icon.isEmpty())
				icon = "icon.etc_question_mark_i00";

			result.append("<center><table width=690>");
			result.append("<tr>");
			result.append("<td WIDTH=690 align=center valign=top>");
			result.append("<table border=0 cellspacing=4 cellpadding=3>");
			result.append("<tr>");
			result.append("<td FIXWIDTH=50 align=right valign=top>");
			result.append("<img src=\"").append(icon).append("\" width=32 height=32>");
			result.append("</td>");
			result.append("<td FIXWIDTH=671 align=left valign=top>");
			result.append("<font color=\"0099FF\">").append(player.getLangId() == RUSSIAN ? "Название предмета:</font> " : "Item name:</font> ").append(Files.htmlItemName(armor.getItemId())).append("<br1><font color=\"LEVEL\">").append(player.getLangId() == RUSSIAN ? "ID предмета:</font> " : "Item ID:</font> ").append(armor.getItemId()).append("&nbsp;");
			result.append("</td>");
			result.append("</tr>");
			result.append("</table>");
			result.append("<table border=0 cellspacing=0 cellpadding=0>");
			result.append("<tr>");
			result.append("<td width=690>");
			result.append("<img src=\"l2ui.squaregray\" width=\"690\" height=\"1\">");
			result.append("</td>");
			result.append("</tr>");
			result.append("</table>");
			result.append("</td>");
			result.append("</tr>");
			result.append("</table>");
			result.append("<br><table width=690>");
			result.append("<tr>");
			result.append("<td>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Тип доспехов: " : "Armor type: ").append("</font>").append(armor.getItemType().toString()).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Грейд доспехов: " : "Armor grade: ").append("</font>").append(armor.getCrystalType()).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Слот: " : "Slot: ").append("</font>").append(getBodyPart(player, armor)).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Разбивается на кристаллы: " : "Divided into crystals: ").append("</font>").append(armor.isCrystallizable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			if(armor.isCrystallizable())
				result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Количество кристаллов: " : "Number of crystals: ").append("</font>").append(armor.getCrystalCount()).append("&nbsp;").append("<br>");
			else
				result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Количество кристаллов:</font> 0" : "Number of crystals:</font> 0").append("&nbsp;").append("<br>");
			result.append("</td>");
			result.append("<td>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Вес: " : "Weight: ").append("</font>").append(armor.getWeight()).append("&nbsp;").append("</font><br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Цена продажи: " : "Sale price: ").append("</font>").append(Util.formatAdena(armor.getReferencePrice() / 2)).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Будет стыковаться: " : "It will be docked: ").append("</font>").append(armor.isStackable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Временный предмет: " : "A temporary item: ").append("</font>").append(armor.getDurability() > 0 ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Можно выбросить: " : "You can throw: ").append("</font>").append(armor.isDropable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("</td>");
			result.append("<td>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Можно продать: " : "Can be sold: ").append("</font>").append(armor.isSellable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			//result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Можно вставить аугментацию: " : "You can insert the argument: ").append("</font>").append(armor.isAugmentable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			//result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Можно вставить атрибут: " : "You can insert an attribute: ").append("</font>").append(armor.isAttributable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("› <font color=\"b09979\">").append(player.getLangId() == RUSSIAN ? "Можно обменять: " : "Can be exchanged: ").append("</font>").append(armor.isTradeable() ? (new CustomMessage("common.result.yes", player)) : (new CustomMessage("common.result.no", player))).append("&nbsp;").append("<br>");
			result.append("</td>");
			result.append("</tr>");
			result.append("</table></center>");

			if(armor.getAttachedSkills() != null && armor.getAttachedSkills().length > 0)
			{
				result.append(Button(player, new CustomMessage("communityboard.wiki.items.list.skill", player).toString(), "_bbsitemskills", armor.getItemId()));
			}
			if(armor.getAttachedFuncs().length > 0)
			{
				result.append(Button(player, new CustomMessage("communityboard.wiki.items.list.bonus", player).toString(), "_bbsitemstats", armor.getItemId()));
			}
			if(armor.isAttDef())
			{
				result.append(Button(player, new CustomMessage("communityboard.wiki.items.list.att", player).toString(), "_bbsitematributes", armor.getItemId()));
			}
		}
		else
			result.append(player.getLangId() == RUSSIAN ? "<table width=690><tr><td width=690><center><font name=\"hs12\" color=\"FF0000\">Предмет не найден</font></center></td></tr></table><br>" : "<table width=690><tr><td width=690><center><font name=\"hs12\" color=\"FF0000\">Item not found</font></center></td></tr></table><br>");

		return result.toString();
	}

	private String getFunc(L2Player player, FuncTemplate func)
	{
		if(func.getFunc(null) != null)
		{
			String str;
			Func f = func.getFunc(null);
			if(getStats(player, f) != null)
			{
				if(f instanceof FuncAdd)
				{
					str = player.getLangId() == RUSSIAN ? "Увеличивает " : "Increases";
					return str + getStats(player, f) + " на " + f._value;
				}
				else if(f instanceof FuncSet)
				{
					str = player.getLangId() == RUSSIAN ? "Увеличивает " : "Sets";
					return str + getStats(player, f) + " в " + f._value;
				}
				else if(f instanceof FuncSub)
				{
					str = player.getLangId() == RUSSIAN ? "Увеличивает " : "Decreases";
					return str + getStats(player, f) + " на " + f._value;
				}
				else if(f instanceof FuncMul)
				{
					str = player.getLangId() == RUSSIAN ? "Увеличивает " : "Multiplies";
					return str + getStats(player, f) + " на " + f._value;
				}
				else if(f instanceof FuncDiv)
				{
					str = player.getLangId() == RUSSIAN ? "Увеличивает " : "Divides";
					return str + getStats(player, f) + " на " + f._value;
				}
				else if(f instanceof FuncEnchant)
				{
					str = player.getLangId() == RUSSIAN ? "Заточка: Увеличивает " : "Increases in the sharpening";
					return str + getStats(player, f) + " на " + f._value;
				}
			}
		}
		return new CustomMessage("common.not.recognized", player).toString();
	}

	private String getStats(L2Player player, Func f)
	{
		String str;
		if(f._stat == Stats.p_max_hp)
		{
			str = player.getLangId() == RUSSIAN ? "максимальное ХП" : "max HP";
			return str;
		}
		else if(f._stat == Stats.p_max_mp)
		{
			str = player.getLangId() == RUSSIAN ? "максимальное МП" : "max MP";
			return str;
		}
		else if(f._stat == Stats.p_max_cp)
		{
			str = player.getLangId() == RUSSIAN ? "максимальное СП" : " max CP";
			return str;
		}
		else if(f._stat == Stats.REGENERATE_HP_RATE)
		{
			str = player.getLangId() == RUSSIAN ? "регенерация ХП" : "regeneration HP";
			return str;
		}
		else if(f._stat == Stats.REGENERATE_CP_RATE)
		{
			str = player.getLangId() == RUSSIAN ? "регенерация СП" : "regeneration CP";
			return str;
		}
		else if(f._stat == Stats.REGENERATE_MP_RATE)
		{
			str = player.getLangId() == RUSSIAN ? "регенерация МП" : "regeneration MP";
			return str;
		}
		else if(f._stat == Stats.p_speed)
		{
			str = player.getLangId() == RUSSIAN ? "скорость" : "speed";
			return str;
		}
		else if(f._stat == Stats.p_physical_defence)
		{
			str = player.getLangId() == RUSSIAN ? "физическую защиту" : "physical defence";
			return str;
		}
		else if(f._stat == Stats.p_magical_defence)
		{
			str = player.getLangId() == RUSSIAN ? "магическую защиту" : "magical defence";
			return str;
		}
		else if(f._stat == Stats.p_physical_attack)
		{
			str = player.getLangId() == RUSSIAN ? "физическую атаку" : "physical attack";
			return str;
		}
		else if(f._stat == Stats.p_magical_attack)
		{
			str = player.getLangId() == RUSSIAN ? "магическую атаку" : "magical attack";
			return str;
		}
		else if(f._stat == Stats.ATK_REUSE || f._stat == Stats.ATK_BASE)
		{
			str = player.getLangId() == RUSSIAN ? "реюз атаку" : "reuse attack";
			return str;
		}
		else if(f._stat == Stats.EVASION_RATE)
		{
			str = player.getLangId() == RUSSIAN ? "точность" : "avoid";
			return str;
		}
		else if(f._stat == Stats.p_hit)
		{
			str = player.getLangId() == RUSSIAN ? "уклонение" : "evasion";
			return str;
		}
		else if(f._stat == Stats.CRITICAL_BASE)
		{
			str = player.getLangId() == RUSSIAN ? "шанс критического удара" : "crit";
			return str;
		}
		else if(f._stat == Stats.SHIELD_DEFENCE)
		{
			str = player.getLangId() == RUSSIAN ? "защиту щитом" : "defense shield";
			return str;
		}
		else if(f._stat == Stats.SHIELD_RATE)
		{
			str = player.getLangId() == RUSSIAN ? "шанс уклониться щитом" : "chance to avoid a shield";
			return str;
		}
		else if(f._stat == Stats.POWER_ATTACK_RANGE)
		{
			str = player.getLangId() == RUSSIAN ? "радиус физической атаки" : "reuse physical attack";
			return str;
		}
		else if(f._stat == Stats.STAT_STR)
		{
			str = player.getLangId() == RUSSIAN ? "СТР" : "STR";
			return str;
		}
		else if(f._stat == Stats.STAT_CON)
		{
			str = player.getLangId() == RUSSIAN ? "КОН" : "CON";
			return str;
		}
		else if(f._stat == Stats.STAT_DEX)
		{
			str = player.getLangId() == RUSSIAN ? "ДЕХ" : "DEX";
			return str;
		}
		else if(f._stat == Stats.STAT_INT)
		{
			str = player.getLangId() == RUSSIAN ? "ИНТ" : "INT";
			return str;
		}
		else if(f._stat == Stats.STAT_WIT)
		{
			str = player.getLangId() == RUSSIAN ? "ВИТ" : "WIT";
			return str;
		}
		else if(f._stat == Stats.STAT_MEN)
		{
			str = player.getLangId() == RUSSIAN ? "МЕН" : "MEN";
			return str;
		}
		else if(f._stat == Stats.MP_PHYSICAL_SKILL_CONSUME)
		{
			str = player.getLangId() == RUSSIAN ? "потребление мп физических скилов" : "mp consume physical skill";
			return str;
		}
		return new CustomMessage("common.not.recognized", player).toString();
	}

	private String getBodyPart(L2Player player, L2Item item)
	{
		if(item.getBodyPart() == L2Item.SLOT_R_EAR || item.getBodyPart() == L2Item.SLOT_L_EAR)
			return new CustomMessage("common.item.template.name.1", player).toString();
		else if(item.getBodyPart() == L2Item.SLOT_NECK)
			return new CustomMessage("common.item.template.name.2", player).toString();
		else if(item.getBodyPart() == L2Item.SLOT_R_FINGER || item.getBodyPart() == L2Item.SLOT_L_FINGER)
			return new CustomMessage("common.item.template.name.3", player).toString();
		else if(item.getBodyPart() == L2Item.SLOT_HEAD)
			return new CustomMessage("common.item.template.name.4", player).toString();
		else if(item.getBodyPart() == L2Item.SLOT_L_HAND)
			return new CustomMessage("common.item.template.name.5", player).toString();
		else if(item.getBodyPart() == L2Item.SLOT_R_HAND || item.getBodyPart() == L2Item.SLOT_LR_HAND)
			return new CustomMessage("common.item.template.name.6", player).toString();
		else if(item.getBodyPart() == L2Item.SLOT_GLOVES)
			return new CustomMessage("common.item.template.name.7", player).toString();
		else if(item.getBodyPart() == L2Item.SLOT_CHEST)
			return new CustomMessage("common.item.template.name.8", player).toString();
		else if(item.getBodyPart() == L2Item.SLOT_LEGS)
			return new CustomMessage("common.item.template.name.9", player).toString();
		else if(item.getBodyPart() == L2Item.SLOT_FEET)
			return new CustomMessage("common.item.template.name.10", player).toString();
		else if(item.getBodyPart() == L2Item.SLOT_BACK)
			return new CustomMessage("common.item.template.name.11", player).toString();
		else if(item.getBodyPart() == L2Item.SLOT_FULL_ARMOR)
			return new CustomMessage("common.item.template.name.12", player).toString();
		else if(item.getBodyPart() == L2Item.SLOT_HAIR)
			return new CustomMessage("common.item.template.name.13", player).toString();
		else if(item.getBodyPart() == L2Item.SLOT_FORMAL_WEAR)
			return new CustomMessage("common.item.template.name.14", player).toString();
		else if(item.getBodyPart() == L2Item.SLOT_FORMAL_WEAR)
			return new CustomMessage("common.item.template.name.15", player).toString();
		else if(item.isUnderwear())
			return new CustomMessage("common.item.template.name.16", player).toString();
		else if(item.isBracelet())
			return new CustomMessage("common.item.template.name.17", player).toString();
		else if(item.isTalisman())
			return new CustomMessage("common.item.template.name.18", player).toString();
		else if(item.isBelt())
			return new CustomMessage("common.item.template.name.19", player).toString();
		return new CustomMessage("common.not.recognized", player).toString();
	}

	private String Button(L2Player player, String name, String bypass, int value)
	{
		StringBuilder result = new StringBuilder();

		result.append("<center><table width=690>");
		result.append("<tr>");
		result.append("<td WIDTH=690 align=center valign=top>");
		result.append("<center><button value=\"");
		result.append(name);
		result.append("\" action=\"bypass -h ").append(bypass).append(" ").append(value).append("\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"></center>");
		result.append("</td>");
		result.append("</tr>");
		result.append("</table></center>");

		return result.toString();
	}

	private String InfoButton(L2Player player, String bypass, int value)
	{
		StringBuilder result = new StringBuilder();

		result.append("<center><table width=690>");
		result.append("<tr>");
		result.append("<td WIDTH=690 align=center valign=top>");
		result.append("<center><br><br><button value=\"");
		result.append(new CustomMessage("communityboard.wiki.items.info", player).toString());
		result.append("\" action=\"bypass -h ").append(bypass).append(" ").append(value).append("\" width=200 height=29  back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"></center>");
		result.append("</td>");
		result.append("</tr>");
		result.append("</table></center>");

		return result.toString();
	}

	private String AttributeHtml(L2Player player, String icon, String name, int value)
	{
		StringBuilder result = new StringBuilder();

		result.append("<center><table width=690>");
		result.append("<tr>");
		result.append("<td WIDTH=690 align=center valign=top>");
		result.append("<table border=0 cellspacing=4 cellpadding=3>");
		result.append("<tr>");
		result.append("<td FIXWIDTH=50 align=right valign=top>");
		result.append("<img src=\"icon." + icon + "\" width=32 height=32>");
		result.append("</td>");
		result.append("<td FIXWIDTH=671 align=left valign=top>");
		result.append("<font color=\"0099FF\">" + name + "</font><br1><font color=\"LEVEL\">" + new CustomMessage("common.element.bonus", player).toString() + "</font> " + value);
		result.append("</td>");
		result.append("</tr>");
		result.append("</table>");
		result.append("<table border=0 cellspacing=0 cellpadding=0>");
		result.append("<tr>");
		result.append("<td width=690>");
		result.append("<img src=\"l2ui.squaregray\" width=\"690\" height=\"1\">");
		result.append("</td>");
		result.append("</tr>");
		result.append("</table>");
		result.append("</td>");
		result.append("</tr>");
		result.append("</table></center>");

		return result.toString();
	}
}