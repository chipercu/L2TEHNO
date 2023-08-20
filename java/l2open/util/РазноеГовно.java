package l2open.util;

import l2open.config.ConfigValue;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.barahlo.CBTpSch;
import l2open.gameserver.model.barahlo.CBBuffSch;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;

import java.util.logging.Logger;

public class РазноеГовно
{
	private static Logger _лог = Logger.getLogger(РазноеГовно.class.getName());

	public static String слепить_список_точек_тп(L2Player игрок)
	{
		StringBuilder штмл = new StringBuilder();
		штмл.append("<tr>");
		int и=0;
		if(игрок._tpSchem.size() > 0)
		for(CBTpSch sch : игрок._tpSchem.values())
		{
			if(игрок._tpSchem.size() > и)
			{
				штмл.append("	<td>");
				штмл.append("		<button value=\"" + sch.name + "\" action=\"bypass -h _bbsteleport:go " + sch.x + " " + sch.y + " " + sch.z + " " + 100000 + "\" width=100 height=30 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
				штмл.append("	</td>");
			}
			if(и != 3)
			{
				штмл.append("	<td>");
				штмл.append("		<br>");
				штмл.append("	</td>");
			}
			и++;
			if(и == 4)
				break;
		}
		else
		{
			штмл.append("	<td>");
			штмл.append("		<br>");
			штмл.append("	</td>");
		}

		штмл.append("</tr>");
		return штмл.toString();
	}

	private static String[] _img = {"branchsys2.br_vitality_day_i00", "icon.skill6885", "icon.skill6171", "branchsys2.s_g_fantastic_magic", "branchsys2.br_skill1561"};
	public static String слепить_список_бафов2(L2Player игрок)
	{
		StringBuilder штмл = new StringBuilder();
		if(игрок._buffSchem.size() > 0)
		{
			int и=0;
			for(CBBuffSch sch : игрок._buffSchem.values())
			{
				if(и >= 5)
					break;
				штмл.append("<table width=222 height=40 bgcolor=333333>");
				штмл.append("	<tr>");
				штмл.append("		<td width=40 height=40 align=right valign=center>");
				штмл.append("			<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background=\""+_img[и]+"\">");
				штмл.append("				<tr>");
				штмл.append("					<td width=32 align=center valign=top>");
				штмл.append("						<img src=\"icon.panel_2\" width=32 height=32>");
				штмл.append("					</td>");
				штмл.append("				</tr>");
				штмл.append("			</table>");
				штмл.append("		</td>");
				штмл.append("		<td width=168 height=40 align=center valign=center>");
				штмл.append("			<table width=164 height=31 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
				штмл.append("				<tr>");
				штмл.append("					<td width=162 height=29 align=center valign=top>");
				штмл.append("						<button value=\"" + sch.SchName + "\" action=\"bypass -h _bbsbufferuse " + sch.id + " $tvari\" width=160 height=27 back=\"L2UI_CT1.ListCTRL_DF_Title_Down\" fore=\"L2UI_CT1.ListCTRL_DF_Title\">");
				штмл.append("					</td>");
				штмл.append("				</tr>");
				штмл.append("			</table>");
				штмл.append("		</td>");
				штмл.append("	</tr>");
				штмл.append("</table>");
				и++;
			}
		}

		return штмл.toString();
	}

	public static String слепить_список_бафов(L2Player игрок)
	{
		StringBuilder штмл = new StringBuilder();
		штмл.append("<tr>");
		int и=0;
		if(игрок._buffSchem.size() > 0)
		for(CBBuffSch sch : игрок._buffSchem.values())
		{
			if(игрок._buffSchem.size() > и)
			{
				штмл.append("	<td>");
				штмл.append("		<button value=\"" + sch.SchName + "\" action=\"bypass -h _bbsbufferuse " + sch.id + " $tvari \" width=100 height=30 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
				штмл.append("	</td>");
			}
			if(и != 3)
			{
				штмл.append("	<td>");
				штмл.append("		<br>");
				штмл.append("	</td>");
			}
			и++;
			if(и == 4)
				break;
		}
		else
		{
			штмл.append("	<td>");
			штмл.append("		<br>");
			штмл.append("	</td>");
		}

		штмл.append("</tr>");
		return штмл.toString();
	}
	// ---------------------------------------------------------------------------------
	public static void incLevelClassMaster(L2Player player)
	{
		ClassId classId = player.getClassId();

		int job_level = classId.getLevel();
		int level = player.getLevel();

		if(level >= 20 && job_level == ConfigValue.ClassMasterWindowForLevelUpJobList[job_level] || level >= 40 && job_level == ConfigValue.ClassMasterWindowForLevelUpJobList[job_level] || level >= 76 && job_level == ConfigValue.ClassMasterWindowForLevelUpJobList[job_level])
		{
			String content = Files.read("./data/html/classmanager.htm", player);
			if(content == null)
				return;
			L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.ClassMastersPriceItem[job_level-1]);

			content = content.replace("<?player_name?>", player.getName());
			content = content.replace("<?item_name?>", item.getName());
			content = content.replace("<?item_count?>", Util.formatAdena(ConfigValue.ClassMastersPrice[job_level-1]));

			StringBuilder html = new StringBuilder();
			for(ClassId cid : ClassId.values())
			{
				// Инспектор является наследником trooper и warder, но сменить его как профессию нельзя,
				// т.к. это сабкласс. Наследуется с целью получения скилов родителей.
				if(cid == ClassId.inspector)
					continue;
				if(cid.childOf(classId) && cid.getLevel() == classId.getLevel() + 1)
					//html.append("<a action=\"bypass -h scripts_services.SubClass:change_class").append(cid.getId()).append(" ").append(ConfigValue.ClassMastersPrice[job_level-1]).append("\">").append(cid.name()).append("</a><br>");
					html.append("<button value=\"").append(DifferentMethods.htmlClassNameNonClient(player, cid.getId())).append("\" action=\"bypass -h scripts_services.SubClass.SubClass:change_class ").append(cid.getId()).append(" ").append(job_level-1).append("\" width=150 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"><br1>");
			}

			content = content.replace("<?button?>", html.toString());
			player.sendPacket(new NpcHtmlMessage(player, null).setHtml(content));
		}
		else if(ConfigValue.EnableHtmlReward52 && level >= 52 && !player.getVarB("reward_52", false))
		{
			String content = Files.read("./data/html/html_reward_52.htm", player);
			content = content.replace("<?player_name?>", player.getName());
			player.sendPacket(new NpcHtmlMessage(player, null).setHtml(content));
			player.setVar("reward_52", String.valueOf(true));
		}
	}
}