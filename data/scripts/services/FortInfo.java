package services;

import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.instancemanager.FortressManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Files;

/**
 * @author Powered by L2CCCP
 */
public class FortInfo extends Functions implements ScriptFile
{
	public void list(String[] param) //TODO: Переделать на норамльную навигацию.
	{
		L2Player player = (L2Player) getSelf();
		int page = Integer.parseInt(param[0]);
		String html = Files.read("data/scripts/show/fortlist.htm", player);
		String list = "";
		if(page == 1)
		{
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 101\" value=\"" + new CustomMessage("common.fort.101", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 102\" value=\"" + new CustomMessage("common.fort.102", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 103\" value=\"" + new CustomMessage("common.fort.103", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 104\" value=\"" + new CustomMessage("common.fort.104", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 105\" value=\"" + new CustomMessage("common.fort.105", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 106\" value=\"" + new CustomMessage("common.fort.106", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
		}
		else if(page == 2)
		{
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 107\" value=\"" + new CustomMessage("common.fort.107", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 108\" value=\"" + new CustomMessage("common.fort.108", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 109\" value=\"" + new CustomMessage("common.fort.109", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 110\" value=\"" + new CustomMessage("common.fort.110", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 111\" value=\"" + new CustomMessage("common.fort.111", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 112\" value=\"" + new CustomMessage("common.fort.112", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
		}
		else if(page == 3)
		{
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 113\" value=\"" + new CustomMessage("common.fort.113", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 114\" value=\"" + new CustomMessage("common.fort.114", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 115\" value=\"" + new CustomMessage("common.fort.115", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 116\" value=\"" + new CustomMessage("common.fort.116", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 117\" value=\"" + new CustomMessage("common.fort.117", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 118\" value=\"" + new CustomMessage("common.fort.118", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
		}
		else if(page == 4)
		{
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 119\" value=\"" + new CustomMessage("common.fort.119", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 120\" value=\"" + new CustomMessage("common.fort.120", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
			list += "<button action=\"bypass -h scripts_services.FortInfo:fort 121\" value=\"" + new CustomMessage("common.fort.121", player) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
		}

		html = html.replace("<?list?>", list);
		show(html, player);
	}

	public void fort(String[] param)
	{
		L2Player player = (L2Player) getSelf();

		int id = Integer.parseInt(param[0]);
		String page = Files.read("data/scripts/show/fort.htm", player);
		page = page.replace("<?name?>", DifferentMethods.getFortName(player, id).toString());
		page = page.replace("<?info?>", Fort(id));
		page = page.replace("<?skill?>", FortSkills(id));
		show(page, player);
	}

	private String Fort(int id) //TODO: Перевести на CustomMessage
	{
		String Big = "Большая.";
		String Small = "Малая.";
		String Territorial = "Территориальная.";
		String Border = "Пограничная.";

		String type = "", size = "";

		switch(id)
		{
			case 101:
				size = Small;
				type = Territorial;
				break;
			case 102:
				size = Big;
				type = Territorial;
				break;
			case 103:
				size = Small;
				type = Territorial;
				break;
			case 104:
				size = Big;
				type = Territorial;
				break;
			case 105:
				size = Small;
				type = Territorial;
				break;
			case 106:
				size = Small;
				type = Territorial;
				break;
			case 107:
				size = Big;
				type = Territorial;
				break;
			case 108:
				size = Small;
				type = Territorial;
				break;
			case 109:
				size = Big;
				type = Territorial;
				break;
			case 110:
				size = Big;
				type = Territorial;
				break;
			case 111:
				size = Small;
				type = Territorial;
				break;
			case 112:
				size = Big;
				type = Border;
				break;
			case 113:
				size = Big;
				type = Border;
				break;
			case 114:
				size = Small;
				type = Border;
				break;
			case 115:
				size = Small;
				type = Border;
				break;
			case 116:
				size = Big;
				type = Border;
				break;
			case 117:
				size = Big;
				type = Border;
				break;
			case 118:
				size = Big;
				type = Border;
				break;
			case 119:
				size = Small;
				type = Border;
				break;
			case 120:
				size = Small;
				type = Border;
				break;
			case 121:
				size = Small;
				type = Border;
				break;
			default:
				break;
		}

		StringBuilder html = new StringBuilder();
		Fortress fortress = FortressManager.getInstance().getFortressByIndex(id);
		html.append("<table border=0 width=290>");
		html.append("<tr>");
		html.append("<td width=54 align=center valign=top height=20>");
		html.append("<table border=0 cellspacing=0 cellpadding=0 width=30 height=20>");
		html.append("<tr>");
		html.append("<td width=32 height=45 align=center valign=top>");
		html.append("<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background=\"icon.weapon_fort_flag_i00\">");
		html.append("<tr>");
		html.append("<td width=32 align=center valign=top>");
		html.append("<img src=\"icon.castle_tab\" width=32 height=32>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</td>");
		html.append("<td FIXWIDTH=230 align=left valign=top>");

		String fort_owner;
		L2Clan owner = fortress.getOwnerId() == 0 ? null : ClanTable.getInstance().getClan(fortress.getOwnerId());
		if(owner == null)
			fort_owner = "NPC";
		else
			fort_owner = owner.getName();

		html.append("Владелец: <font color=\"FFFF00\">" + fort_owner + "</font>");
		html.append("<br1>Размер: <font color=\"AAAAAA\">" + size + "</font>");
		html.append("<br1>Тип: <font color=\"AAAAAA\">" + type + "</font>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		return html.toString();
	}

	private String FortSkills(int id)
	{
		L2Skill skill;

		StringBuilder html = new StringBuilder();
		switch(id)
		{
			case 101:
				skill = SkillTable.getInstance().getInfo(590, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(603, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 102:
				skill = SkillTable.getInstance().getInfo(602, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(604, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 103:
				skill = SkillTable.getInstance().getInfo(601, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(605, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 104:
				skill = SkillTable.getInstance().getInfo(595, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(606, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 105:
				skill = SkillTable.getInstance().getInfo(607, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(594, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 106:
				skill = SkillTable.getInstance().getInfo(608, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(593, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 107:
				skill = SkillTable.getInstance().getInfo(596, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(598, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 108:
				skill = SkillTable.getInstance().getInfo(592, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(599, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 109:
				skill = SkillTable.getInstance().getInfo(591, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(610, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 110:
				skill = SkillTable.getInstance().getInfo(597, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(601, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 111:
				skill = SkillTable.getInstance().getInfo(590, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(608, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 112:
				skill = SkillTable.getInstance().getInfo(590, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(608, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 113:
				skill = SkillTable.getInstance().getInfo(610, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(606, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 114:
				skill = SkillTable.getInstance().getInfo(609, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(605, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 115:
				skill = SkillTable.getInstance().getInfo(599, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(604, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 116:
				skill = SkillTable.getInstance().getInfo(598, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(603, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 117:
				skill = SkillTable.getInstance().getInfo(597, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(602, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(610, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 118:
				skill = SkillTable.getInstance().getInfo(601, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(596, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 119:
				skill = SkillTable.getInstance().getInfo(592, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(595, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 120:
				skill = SkillTable.getInstance().getInfo(591, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(594, 1);
				html.append(FortSkillsBlock(skill));
				break;
			case 121:
				skill = SkillTable.getInstance().getInfo(590, 1);
				html.append(FortSkillsBlock(skill));
				skill = SkillTable.getInstance().getInfo(593, 1);
				html.append(FortSkillsBlock(skill));
				break;
			default:
				break;
		}

		return html.toString();
	}

	private String FortSkillsBlock(L2Skill skill) //TODO: Перевести на CustomMessage
	{
		StringBuilder html = new StringBuilder();

		html.append("<br>");
		html.append("<table border=0 width=290 height=30>");
		html.append("<tr>");
		html.append("<td width=54 align=center valign=top height=20>");
		html.append("<table border=0 cellspacing=0 cellpadding=0 width=30 height=20>");
		html.append("<tr>");
		html.append("<td width=32 height=45 align=center valign=top>");
		html.append("<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background=\"icon.skill" + skill.getIcon() + "\">");
		html.append("<tr>");
		html.append("<td width=32 align=center valign=top>");
		html.append("<img src=\"icon.panel_2\" width=32 height=32>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</td>");
		html.append("<td FIXWIDTH=230 align=left valign=top>");
		html.append("<font color=\"CCFF33\">" + skill.getName() + "</font>");
		html.append("<br1>Уровень: <font color=\"AAAAAA\">" + skill.getLevel() + "</font>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");

		return html.toString();
	}

	@Override
	public void onLoad()
	{
		_log.info("Loaded Service: Fort Info");
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

}
