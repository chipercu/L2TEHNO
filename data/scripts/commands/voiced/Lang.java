package commands.voiced;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2WorldRegion;
import l2open.util.Files;
import l2open.util.PrintfFormat;
import org.apache.commons.lang3.math.NumberUtils;

import communityboard.CommunityBoardCabinet;

/**
 * @Author: Diamond
 * @Date: 10/07/2007
 * @Time: 15:07:08
 */
public class Lang extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "lang", "cfg", "animation", "flag_attack", "attack" };

	public static final PrintfFormat cfg_row = new PrintfFormat("<table><tr><td width=5></td><td width=120>%s:</td><td width=100>%s</td></tr></table>");
	public static final PrintfFormat cfg_button = new PrintfFormat("<button width=%d height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h user_cfg %s\" value=\"%s\">");

	private static String OFF = "<font color=\"FF0000\">OFF</font>";
	private static String ON = "<font color=\"00CC00\">ON</font>";

	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		if(command.equals("lang"))
		{
			if(args != null)
				if(args.equalsIgnoreCase("en") || args.equalsIgnoreCase("EN"))
					activeChar.setVar("lang@", "en");
				else if(args.equalsIgnoreCase("ru") || args.equalsIgnoreCase("RU"))
					activeChar.setVar("lang@", "ru");
		}
		else if(command.equals("animation"))
		{
			if(args != null)
			{
				int dist=3000;
				try
				{
					dist=Integer.parseInt(args);
				}
				catch(Exception e)
				{}
				if(dist <= 0)
					activeChar.sendMessage("Ваша анимация выключена.");
				else if(dist < 3000)
					activeChar.sendMessage("Ваша анимация включена на дистанцию: "+dist);
				else
					activeChar.sendMessage("Ваша анимация включена максимально.");

				activeChar.set_show_buff_anim_dist(dist);
				activeChar.setVar("show_buff_anim_dist", String.valueOf(dist));
			}
			return true;
		}
		else if(command.equals("flag_attack"))
		{
			if(args != null)
			{
				int dist=3000;
				try
				{
					dist=Integer.parseInt(args);
				}
				catch(Exception e)
				{}
				if(dist <= 0)
					activeChar.sendMessage("Вы отключили отображение эффектов атаки.");
				else if(dist < 3000)
					activeChar.sendMessage("Вы изменили дистанцию видимости эффектов атаки: "+dist);

				activeChar.set_show_attack_flag_dist(dist);
				activeChar.setVar("show_attack_flag_dist", String.valueOf(dist));
			}
			return true;
		}
		else if(command.equals("attack"))
		{
			if(args != null)
			{
				int dist=3000;
				try
				{
					dist=Integer.parseInt(args);
				}
				catch(Exception e)
				{}
				if(dist <= 0)
					activeChar.sendMessage("Вы отключили отображение атаки.");
				else if(dist < 3000)
					activeChar.sendMessage("Вы изменили дистанцию видимости атаки: "+dist);

				activeChar.set_show_attack_dist(dist);
				activeChar.setVar("show_attack_dist", String.valueOf(dist));
			}
			return true;
		}
		else if(command.equals("cfg"))
			if(args != null)
			{
				String[] param = args.split(" ");
				activeChar.sendMessage("["+param[0]+"]["+(param.length > 1 ? param[1] : "-")+"]");
				if(param.length == 2)
				{
					if(param[0].equalsIgnoreCase("dli"))
						if(param[1].startsWith("on"))
							activeChar.setVar("DroplistIcons", "1");
						else if(param[1].startsWith("of"))
							activeChar.unsetVar("DroplistIcons");

					if(param[0].equalsIgnoreCase("ssc"))
						if(param[1].startsWith("of") && ConfigValue.SkillsShowChance)
							activeChar.setVar("SkillsHideChance", "1");
						else if(param[1].startsWith("on"))
							activeChar.unsetVar("SkillsHideChance");

					if(param[0].equalsIgnoreCase("SkillsMobChance"))
						if(param[1].startsWith("on") && ConfigValue.SkillsShowChance)
							activeChar.setVar("SkillsMobChance", "1");
						else if(param[1].startsWith("of"))
							activeChar.unsetVar("SkillsMobChance");

					if(param[0].equalsIgnoreCase("noe"))
						if(param[1].startsWith("on"))
							activeChar.setVar("NoExp", "1");
						else if(param[1].startsWith("of"))
							activeChar.unsetVar("NoExp");

					if(param[0].equalsIgnoreCase("pf"))
						if(param[1].startsWith("of"))
							activeChar.setVar("no_pf", "1");
						else if(param[1].startsWith("on"))
							activeChar.unsetVar("no_pf");

					if(param[0].equalsIgnoreCase("trace"))
						if(param[1].startsWith("on"))
							activeChar.setVar("trace", "1");
						else if(param[1].startsWith("of"))
							activeChar.unsetVar("trace");

					if(param[0].equalsIgnoreCase("notraders"))
					{
						if(param[1].startsWith("on"))
							activeChar.setVar("notraders", "1");
						else if(param[1].startsWith("of"))
							activeChar.unsetVar("notraders");
						for(L2WorldRegion neighbor : activeChar.getCurrentRegion().getNeighbors())
							neighbor.removeObjectsFromPlayer(activeChar);
						for(L2WorldRegion neighbor : activeChar.getCurrentRegion().getNeighbors())
							neighbor.showObjectsToPlayer(activeChar, false);
					}

					if(param[0].equalsIgnoreCase("notShowBuffAnim"))
					{
						if(param[1].startsWith("on"))
						{
							activeChar.set_show_buff_anim_dist(0);
							activeChar.setVar("show_buff_anim_dist", "0");
						}
						else if(param[1].startsWith("of"))
						{
							activeChar.set_show_buff_anim_dist(3000);
							activeChar.setVar("show_buff_anim_dist", "3000");
						}
					}

					if(param[0].equalsIgnoreCase("noShift"))
						if(param[1].startsWith("on"))
							activeChar.setVar("noShift", "1");
						else if(param[1].startsWith("of"))
							activeChar.unsetVar("noShift");

					if(ConfigValue.EnableNoCarrier && param[0].equalsIgnoreCase("noCarrier"))
					{
						int time = NumberUtils.toInt(param[1], ConfigValue.NoCarrierDefaultTime);

						if(time > ConfigValue.NoCarrierMaxTime)
							time = ConfigValue.NoCarrierMaxTime;
						else if(time < ConfigValue.NoCarrierMinTime)
							time = ConfigValue.NoCarrierMinTime;

						activeChar.setVar("noCarrier", String.valueOf(time), -1);
					}
					if(param[0].equalsIgnoreCase("translit"))
						if(param[1].startsWith("on"))
							activeChar.setVar("translit", "tl");
						else if(param[1].startsWith("la") || param[1].startsWith("tl") || param[1].startsWith("tc"))
							activeChar.setVar("translit", "tc");
						else if(param[1].startsWith("of"))
							activeChar.unsetVar("translit");

					if(param[0].equalsIgnoreCase("autoloot"))
						activeChar.setAutoLoot(Boolean.parseBoolean(param[1]));

					if(param[0].equalsIgnoreCase("autolooth"))
						activeChar.setAutoLootHerbs(Boolean.parseBoolean(param[1]));

					if(param[0].equalsIgnoreCase("autolootspecial"))
						activeChar.setAutoLootSpecial(Boolean.parseBoolean(param[1]));
						
					if(param[0].equalsIgnoreCase("TalismanSumLife"))
						activeChar.setVar("TalismanSumLife", param[1]);

					if(param[0].equalsIgnoreCase("EnableAutoAttribute"))
						activeChar.setVar("EnableAutoAttribute", param[1]);

					if(param[0].equalsIgnoreCase("SkillsShowChanceFull"))
						activeChar.setVar("SkillsShowChanceFull", param[1]);

					if(param[0].equalsIgnoreCase("send_visual_id"))
					{
						activeChar.send_visual_id = Boolean.parseBoolean(param[1]);

						for(L2WorldRegion neighbor : activeChar.getCurrentRegion().getNeighbors())
							neighbor.removeObjectsFromPlayer(activeChar);

						for(L2WorldRegion neighbor : activeChar.getCurrentRegion().getNeighbors())
							neighbor.showObjectsToPlayer(activeChar, false);

						activeChar.setVar("send_visual_id", String.valueOf(activeChar.send_visual_id));
					}
					
					if(param[0].equalsIgnoreCase("send_visual_enchant"))
					{
						activeChar.send_visual_enchant = Boolean.parseBoolean(param[1]);

						for(L2WorldRegion neighbor : activeChar.getCurrentRegion().getNeighbors())
							neighbor.removeObjectsFromPlayer(activeChar);

						for(L2WorldRegion neighbor : activeChar.getCurrentRegion().getNeighbors())
							neighbor.showObjectsToPlayer(activeChar, false);

						activeChar.setVar("send_visual_enchant", String.valueOf(activeChar.send_visual_enchant));
					}
					
					

					if(param[0].equalsIgnoreCase("disable_cloak"))
					{
						activeChar.disable_cloak = Boolean.parseBoolean(param[1]);

						for(L2WorldRegion neighbor : activeChar.getCurrentRegion().getNeighbors())
							neighbor.removeObjectsFromPlayer(activeChar);

						for(L2WorldRegion neighbor : activeChar.getCurrentRegion().getNeighbors())
							neighbor.showObjectsToPlayer(activeChar, false);

						activeChar.setVar("disable_cloak", String.valueOf(activeChar.disable_cloak));
					}

					if(param[0].equalsIgnoreCase("show_old_dam_message"))
						if(param[1].startsWith("on"))
						{
							activeChar.old_dam_message = true;
							activeChar.setVar("show_old_dam_message", "1");
						}
						else if(param[1].startsWith("of"))
						{
							activeChar.old_dam_message = false;
							activeChar.unsetVar("show_old_dam_message");
						}
					if(param[0].equalsIgnoreCase("event_invite"))
					{
						if(param[1].startsWith("on"))
						{
							activeChar.setVar("event_invite", String.valueOf(true));
							activeChar.sendMessage("Приглашение на ивент включены.");
						}
						else
						{
							activeChar.setVar("event_invite", String.valueOf(false));
							activeChar.sendMessage("Приглашение на ивент отключены.");
						}
					}
					else if(param[0].equalsIgnoreCase("animation"))
					{
						if(param.length == 2)
						{
							int dist=3000;
							try
							{
								Math.min(dist=Integer.parseInt(param[1]), 3000);
							}
							catch(Exception e)
							{}
							if(dist <= 0)
								activeChar.sendMessage("Ваша анимация выключена.");
							else if(dist < 3000)
								activeChar.sendMessage("Ваша анимация включена на дистанцию: "+dist);
							else
								activeChar.sendMessage("Ваша анимация включена максимально.");

							activeChar.set_show_buff_anim_dist(dist);
							activeChar.setVar("show_buff_anim_dist", String.valueOf(dist));
						}
					}
					else if(param[0].equalsIgnoreCase("flag_attack"))
					{
						if(param.length == 2)
						{
							int dist=3000;
							try
							{
								Math.min(dist=Integer.parseInt(param[1]), 3000);
							}
							catch(Exception e)
							{}
							if(dist <= 0)
								activeChar.sendMessage("Вы отключили отображение эффектов атаки.");
							else if(dist < 3000)
								activeChar.sendMessage("Вы изменили дистанцию видимости эффектов атаки: "+dist);

							activeChar.set_show_attack_flag_dist(dist);
							activeChar.setVar("show_attack_flag_dist", String.valueOf(dist));
						}
					}
					else if(param[0].equalsIgnoreCase("attack"))
					{
						if(param.length == 2)
						{
							int dist=3000;
							try
							{
								Math.min(dist=Integer.parseInt(param[1]), 3000);
							}
							catch(Exception e)
							{}
							if(dist <= 0)
								activeChar.sendMessage("Вы отключили отображение атаки.");
							else if(dist < 3000)
								activeChar.sendMessage("Вы изменили дистанцию видимости атаки: "+dist);

							activeChar.set_show_attack_dist(dist);
							activeChar.setVar("show_attack_dist", String.valueOf(dist));
						}
					}
				}
			}

		String dialog = Files.read("data/scripts/commands/voiced/lang.htm", activeChar);

		dialog = dialog.replaceFirst("%lang%", "<font color=\"339966\">" + activeChar.getVar("lang@").toUpperCase() + "</font>");
		dialog = dialog.replace("<?lang?>", activeChar.getVar("lang@").toUpperCase().equals("RU") ? "EN" : "RU");
		// ----
		dialog = dialog.replace("<?player_lang?>", lang(activeChar).toUpperCase());

		dialog = dialog.replace("<?player_dli?>", DroplistIcons(activeChar, false));
		dialog = dialog.replace("<?button_dli?>", DroplistIcons(activeChar, true));

		dialog = dialog.replace("<?player_noe?>", NoExp(activeChar, false));
		dialog = dialog.replace("<?button_noe?>", NoExp(activeChar, true));

		dialog = dialog.replace("<?player_notraders?>", NotShowTraders(activeChar, false));
		dialog = dialog.replace("<?button_notraders?>", NotShowTraders(activeChar, true));

		dialog = dialog.replace("<?player_notShowBuffAnim?>", notShowBuffAnim(activeChar, false));
		dialog = dialog.replace("<?button_notShowBuffAnim?>", notShowBuffAnim(activeChar, true));

		dialog = dialog.replace("<?player_noShift?>", noShift(activeChar, false));
		dialog = dialog.replace("<?button_noShift?>", noShift(activeChar, true));

		dialog = dialog.replace("<?player_pathfind?>", pathfind(activeChar, false));
		dialog = dialog.replace("<?button_pathfind?>", pathfind(activeChar, true));

		dialog = dialog.replace("<?player_trace?>", trace(activeChar, false));
		dialog = dialog.replace("<?button_trace?>", trace(activeChar, true));

		dialog = dialog.replace("<?player_skill_chance?>", SkillsHideChance(activeChar, false));
		dialog = dialog.replace("<?button_skill_chance?>", SkillsHideChance(activeChar, true));

		dialog = dialog.replace("<?player_monster_skill_chance?>", MonsterSkillsHideChance(activeChar, false));
		dialog = dialog.replace("<?button_monster_skill_chance?>", MonsterSkillsHideChance(activeChar, true));

		dialog = dialog.replace("<?player_autolooth?>", AutoLoot(activeChar, false));
		dialog = dialog.replace("<?button_autolooth?>", AutoLoot(activeChar, true));

		dialog = dialog.replace("<?player_autolooth_sp?>", AutoLoot_sp(activeChar, false));
		dialog = dialog.replace("<?button_autolooth_sp?>", AutoLoot_sp(activeChar, true));

		dialog = dialog.replace("<?player_autolooth_herbs?>", AutoLootHerbs(activeChar, false));
		dialog = dialog.replace("<?button_autolooth_herbs?>", AutoLootHerbs(activeChar, true));

		//--
		dialog = dialog.replace("<?TalismanSumLife?>", TalismanSumLife(activeChar, false));
		dialog = dialog.replace("<?button_TalismanSumLife?>", TalismanSumLife(activeChar, true));

		dialog = dialog.replace("<?EnableAutoAttribute?>", EnableAutoAttribute(activeChar, false));
		dialog = dialog.replace("<?button_EnableAutoAttribute?>", EnableAutoAttribute(activeChar, true));

		dialog = dialog.replace("<?send_visual_id?>", send_visual_id(activeChar, false));
		dialog = dialog.replace("<?button_send_visual_id?>", send_visual_id(activeChar, true));

		dialog = dialog.replace("<?send_visual_enchant?>", send_visual_enchant(activeChar, false));
		dialog = dialog.replace("<?button_send_visual_enchant?>", send_visual_enchant(activeChar, true));
		
		

		dialog = dialog.replace("<?disable_cloak?>", disable_cloak(activeChar, false));
		dialog = dialog.replace("<?button_disable_cloak?>", disable_cloak(activeChar, true));

		dialog = dialog.replace("<?show_old_dam_message?>", show_old_dam_message(activeChar, false));
		dialog = dialog.replace("<?button_show_old_dam_message?>", show_old_dam_message(activeChar, true));

		dialog = dialog.replace("<?event_invite?>", event_invite(activeChar, false));
		dialog = dialog.replace("<?button_event_invite?>", event_invite(activeChar, true));

		dialog = dialog.replace("<?player_noCarrier?>", ConfigValue.EnableNoCarrier ? activeChar.getVarB("noCarrier") ? activeChar.getVarInt("noCarrier") == ConfigValue.NoCarrierMaxTime ? "<font color=\"00FF00\">" + activeChar.getVar("noCarrier") + "</font>" : "<font color=\"LEVEL\">" + activeChar.getVar("noCarrier") + "</font>" : "<font color=\"FF0000\">0</font>" : "<font color=\"FF0000\">N/A</font>");

		dialog = dialog.replace("<?flag_attack?>", activeChar.show_attack_flag_dist() >= 2500 ? "<font color=\"00FF00\">" + activeChar.show_attack_flag_dist() + "</font>" : activeChar.show_attack_flag_dist() > 0 ? "<font color=\"LEVEL\">" + activeChar.show_attack_flag_dist() + "</font>" : "<font color=\"FF0000\">0</font>");
		dialog = dialog.replace("<?attack?>", activeChar.show_attack_dist() >= 2500 ? "<font color=\"00FF00\">" + activeChar.show_attack_dist() + "</font>" :activeChar.show_attack_dist() > 0 ? "<font color=\"LEVEL\">" + activeChar.show_attack_dist() + "</font>" : "<font color=\"FF0000\">0</font>");
		dialog = dialog.replace("<?animation?>", activeChar.show_buff_anim_dist() >= 2500 ? "<font color=\"00FF00\">" + activeChar.show_buff_anim_dist() + "</font>" :activeChar.show_buff_anim_dist() > 0 ? "<font color=\"LEVEL\">" + activeChar.show_buff_anim_dist() + "</font>" : "<font color=\"FF0000\">0</font>");

		String tl2 = activeChar.getVar("translit");
		if(tl2 == null)
			dialog = dialog.replace("<?player_translit?>", "<font color=\"99CC00\">OFF</font>");
		else if(tl2.equals("tl"))
			dialog = dialog.replace("<?player_translit?>", "<font color=\"99CC00\">ON</font>");
		else
			dialog = dialog.replace("<?player_translit?>", "<font color=\"99CC00\">TC</font>");
		// ----
		
		
		dialog = dialog.replaceFirst("%dli%", activeChar.getVarB("DroplistIcons") ? "On" : "Off");
		dialog = dialog.replaceFirst("%noe%", activeChar.getVarB("NoExp") ? "On" : "Off");
		dialog = dialog.replaceFirst("%pf%", activeChar.getVarB("no_pf") ? "Off" : "On");
		dialog = dialog.replaceFirst("%trace%", activeChar.getVarB("trace") ? "On" : "Off");
		dialog = dialog.replaceFirst("%notraders%", activeChar.getVarB("notraders") ? "On" : "Off");
		dialog = dialog.replaceFirst("%notShowBuffAnim%", activeChar.getVarInt("show_buff_anim_dist", 3000) > 0 ? "On" : "Off");
		dialog = dialog.replaceFirst("%noShift%", activeChar.getVarB("noShift") ? "On" : "Off");
		dialog = dialog.replaceFirst("%noCarrier%", ConfigValue.EnableNoCarrier ? (activeChar.getVarB("noCarrier") ? activeChar.getVar("noCarrier") : "0") : "N/A");

		dialog = dialog.replaceFirst("%show_buff_anim_dist%", activeChar.getVar("show_buff_anim_dist", "3000"));

		if(!ConfigValue.SkillsShowChance)
		{
			dialog = dialog.replaceFirst("%ssc%", "N/A");
			dialog = dialog.replaceFirst("%SkillsMobChance%", "N/A");
		}
		else
		{
			if(!activeChar.getVarB("SkillsHideChance"))
				dialog = dialog.replaceFirst("%ssc%", "On");
			else
				dialog = dialog.replaceFirst("%ssc%", "Off");

			if(activeChar.getVarB("SkillsMobChance"))
				dialog = dialog.replaceFirst("%SkillsMobChance%", "On");
			else
				dialog = dialog.replaceFirst("%SkillsMobChance%", "Off");
		}

		String tl = activeChar.getVar("translit");
		if(tl == null)
			dialog = dialog.replaceFirst("%translit%", "Off");
		else if(tl.equals("tl"))
			dialog = dialog.replaceFirst("%translit%", "On");
		else
			dialog = dialog.replaceFirst("%translit%", "Lt");

		String additional = "";
		String bt;

		if(ConfigValue.AutoLootIndividual)
		{
			if(ConfigValue.AutoLoot)
			{
				if(activeChar.isAutoLootEnabled())
					bt = cfg_button.sprintf(new Object[] { 100, "autoloot false", new CustomMessage("scripts.commands.voiced.Lang.Disable", activeChar).toString() });
				else
					bt = cfg_button.sprintf(new Object[] { 100, "autoloot true", new CustomMessage("scripts.commands.voiced.Lang.Enable", activeChar).toString() });
				additional += cfg_row.sprintf(new Object[] { "Auto-loot", bt });
			}
			if(ConfigValue.AutoLootHerbs)
			{
				if(activeChar.isAutoLootHerbsEnabled())
					bt = cfg_button.sprintf(new Object[] { 100, "autolooth false", new CustomMessage("scripts.commands.voiced.Lang.Disable", activeChar).toString() });
				else
					bt = cfg_button.sprintf(new Object[] { 100, "autolooth true", new CustomMessage("scripts.commands.voiced.Lang.Enable", activeChar).toString() });
				additional += cfg_row.sprintf(new Object[] { "Auto-loot herbs", bt });
			}
			if(ConfigValue.AutoLoot)
			{
				if(activeChar.isAutoLootSpecialEnabled())
					bt = cfg_button.sprintf(new Object[] { 100, "autolootspecial false", new CustomMessage("scripts.commands.voiced.Lang.Disable", activeChar).toString() });
				else
					bt = cfg_button.sprintf(new Object[] { 100, "autolootspecial true", new CustomMessage("scripts.commands.voiced.Lang.Enable", activeChar).toString() });
				additional += cfg_row.sprintf(new Object[] { "Auto-loot special", bt });
			}
		}

		if(ConfigValue.TalismanSumLife)
		{
			if(activeChar.getVarB("TalismanSumLife", false))
				bt = cfg_button.sprintf(new Object[] { 100, "TalismanSumLife false", new CustomMessage("scripts.commands.voiced.Lang.Disable", activeChar).toString() });
			else
				bt = cfg_button.sprintf(new Object[] { 100, "TalismanSumLife true", new CustomMessage("scripts.commands.voiced.Lang.Enable", activeChar).toString() });
			additional += cfg_row.sprintf(new Object[] { "Talisman sum life", bt });
		}

		if(ConfigValue.EnableAutoAttribute)
		{
			if(activeChar.getVarB("EnableAutoAttribute", false))
				bt = cfg_button.sprintf(new Object[] { 100, "EnableAutoAttribute false", new CustomMessage("scripts.commands.voiced.Lang.Disable", activeChar).toString() });
			else
				bt = cfg_button.sprintf(new Object[] { 100, "EnableAutoAttribute true", new CustomMessage("scripts.commands.voiced.Lang.Enable", activeChar).toString() });
			additional += cfg_row.sprintf(new Object[] { "Enable Auto Attribute", bt });
		}

		if(ConfigValue.SkillsShowChanceFull)
		{
			if(activeChar.getVarB("SkillsShowChanceFull", false))
				bt = cfg_button.sprintf(new Object[] { 100, "SkillsShowChanceFull false", new CustomMessage("scripts.commands.voiced.Lang.Disable", activeChar).toString() });
			else
				bt = cfg_button.sprintf(new Object[] { 100, "SkillsShowChanceFull true", new CustomMessage("scripts.commands.voiced.Lang.Enable", activeChar).toString() });
			additional += cfg_row.sprintf(new Object[] { "Enable debug chance skill", bt });
		}

		if(ConfigValue.EnableVisualPersonal)
		{
			if(activeChar.send_visual_id)
				bt = cfg_button.sprintf(new Object[] { 100, "send_visual_id false", new CustomMessage("scripts.commands.voiced.Lang.Disable", activeChar).toString() });
			else
				bt = cfg_button.sprintf(new Object[] { 100, "send_visual_id true", new CustomMessage("scripts.commands.voiced.Lang.Enable", activeChar).toString() });
			additional += cfg_row.sprintf(new Object[] { "Enable visual item", bt });

		}

		if(ConfigValue.EnableVisualEnchantPersonal)
		{
			if(activeChar.send_visual_enchant)
				bt = cfg_button.sprintf(new Object[] { 100, "send_visual_enchant false", new CustomMessage("scripts.commands.voiced.Lang.Disable", activeChar).toString() });
			else
				bt = cfg_button.sprintf(new Object[] { 100, "send_visual_enchant true", new CustomMessage("scripts.commands.voiced.Lang.Enable", activeChar).toString() });
			additional += cfg_row.sprintf(new Object[] { "Enable visual item", bt });

		}

		if(ConfigValue.DisableCloakPersonal)
		{
			if(activeChar.disable_cloak)
				bt = cfg_button.sprintf(new Object[] { 100, "disable_cloak false", new CustomMessage("scripts.commands.voiced.Lang.Disable", activeChar).toString() });
			else
				bt = cfg_button.sprintf(new Object[] { 100, "disable_cloak true", new CustomMessage("scripts.commands.voiced.Lang.Enable", activeChar).toString() });
			additional += cfg_row.sprintf(new Object[] { "Disable visual cloak", bt });
		}
		

		if(activeChar.getVarB("show_old_dam_message", false))
			bt = cfg_button.sprintf(new Object[] { 100, "show_old_dam_message of", new CustomMessage("scripts.commands.voiced.Lang.Disable", activeChar).toString() });
		else
			bt = cfg_button.sprintf(new Object[] { 100, "show_old_dam_message on", new CustomMessage("scripts.commands.voiced.Lang.Enable", activeChar).toString() });
		additional += cfg_row.sprintf(new Object[] { "Old damage message", bt });

		dialog = dialog.replaceFirst("%additional%", additional);

		show(dialog, activeChar);

		return true;
	}
	// -----------------------------------
	public static String lang(L2Player player)
	{
		return "<font color=\"339966\">" + player.getVar("lang@") + "</font>";
	}

	public static String show_old_dam_message(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("show_old_dam_message", false))
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg show_old_dam_message off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg show_old_dam_message on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("show_old_dam_message", false) ? ON : OFF;
		return _msg;
	}

	public static String disable_cloak(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.disable_cloak)
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg disable_cloak false\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg disable_cloak true\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.disable_cloak ? ON : OFF;
		return _msg;
	}

	public static String send_visual_id(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.send_visual_id)
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg send_visual_id false\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg send_visual_id true\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.send_visual_id ? ON : OFF;
		return _msg;
	}

	public static String send_visual_enchant(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.send_visual_enchant)
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg send_visual_enchant false\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg send_visual_enchant true\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.send_visual_enchant ? ON : OFF;
		return _msg;
	}

	public static String EnableAutoAttribute(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("EnableAutoAttribute", false))
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg EnableAutoAttribute false\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg EnableAutoAttribute true\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("EnableAutoAttribute", false) ? ON : OFF;
		return _msg;
	}

	public static String TalismanSumLife(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("TalismanSumLife", false))
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg TalismanSumLife false\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg TalismanSumLife true\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("TalismanSumLife", false) ? ON : OFF;
		return _msg;
	}

	public static String DroplistIcons(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("DroplistIcons"))
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg dli off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg dli on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("DroplistIcons") ? ON : OFF;
		return _msg;
	}

	public static String NoExp(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("NoExp"))
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg noe off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg noe on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("NoExp") ? ON : OFF;
		return _msg;
	}

	public static String NotShowTraders(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("notraders"))
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg notraders off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg notraders on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("notraders") ? ON : OFF;
		return _msg;
	}

	public static String notShowBuffAnim(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarInt("show_buff_anim_dist", 3000) > 0)
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg notShowBuffAnim on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg notShowBuffAnim off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = (player.getVarInt("show_buff_anim_dist", 3000) > 0) ? OFF : ON;
		return _msg;
	}

	public static String SkillsHideChance(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("SkillsHideChance"))
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg ssc off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg ssc on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("SkillsHideChance") ? ON : OFF;
		return _msg;
	}

	public static String MonsterSkillsHideChance(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("SkillsMobChance"))
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg SkillsMobChance off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg SkillsMobChance on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("SkillsMobChance") ? ON : OFF;
		return _msg;
	}

	public static String AutoLoot(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.isAutoLootEnabled())
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg autoloot false\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg autoloot true\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.isAutoLootEnabled() ? ON : OFF;
		return _msg;
	}

	public static String AutoLoot_sp(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.isAutoLootSpecialEnabled())
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg autolootspecial false\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg autolootspecial true\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.isAutoLootSpecialEnabled() ? ON : OFF;
		return _msg;
	}

	public static String AutoLootHerbs(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.isAutoLootHerbsEnabled())
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg autolooth off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg autolooth on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.isAutoLootHerbsEnabled() ? ON : OFF;
		return _msg;
	}

	public static String noShift(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("noShift"))
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg noShift off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg noShift on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("noShift") ? ON : OFF;
		return _msg;
	}

	public static String trace(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("trace"))
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg trace off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg trace on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("trace") ? ON : OFF;
		return _msg;
	}

	public static String pathfind(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(!player.getVarB("no_pf"))
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg pf off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg pf on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = !player.getVarB("no_pf") ? ON : OFF;
		return _msg;
	}

	public static String event_invite(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("event_invite", true))
				_msg = "<button value=\"OFF\" action=\"bypass -h user_cfg event_invite off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h user_cfg event_invite on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("event_invite", true) ? ON : OFF;
		return _msg;
	}
	// -----------------------------------
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}