package communityboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.communitybbs.Manager.FailBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ShowCalc;
import l2open.util.Files;
import l2open.util.Util;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author Powered by L2CCCP
 */
public class General extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	static final Logger _log = Logger.getLogger(General.class.getName());

	private static int CabinetLicense = 1 << 10;

	@Override
	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}

	private static enum Commands
	{
		_bbssecurity,
		_bbscabinet,
		_bbsevents,
		_bbsshop,
		_bbsshow,
		_bbshtml,
		_bbspage
	}

	@Override
	public void parsecmd(String bypass, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(player.is_block || player.isInEvent() > 0)
			return;
		String html = ""; //Для каждого байпаса определяем свою страницу для избежания лишних подгрузок.

		if(bypass.startsWith(Commands._bbssecurity.toString()))
		{
			html = readHtml(ConfigValue.CommunityBoardHtmlRoot + "index.htm", player);

			String[] command = bypass.split(":");

			if(command[1].equals("lockip"))
				SecurityIP.lock(player);
			else if(command[1].equals("unlockip"))
				SecurityIP.unlock(player);
			else if(command[1].equals("lockhwid"))
				SecurityHWID.lock(player);
			else if(command[1].equals("unlockhwid"))
				SecurityHWID.unlock(player);
		}
		else if(bypass.startsWith(Commands._bbscabinet.toString()))
		{
			String[] command = bypass.split(":");
			if(command[1].equals("show"))
			{
				html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "cabinet/index.htm", player);
				html = html.replace("<?sex_count?>", String.valueOf(Util.formatAdena(ConfigValue.SexChangePrice)));
				html = html.replace("<?sex_item?>", String.valueOf(ConfigValue.SexChangeItem));
				html = html.replace("<?name_color_count?>", String.valueOf(Util.formatAdena(ConfigValue.NickColorChangePrice)));
				html = html.replace("<?name_color_item?>", String.valueOf(ConfigValue.NickColorChangeItem));
				html = html.replace("<?title_color_count?>", String.valueOf(Util.formatAdena(ConfigValue.TitleColorChangePrice)));
				html = html.replace("<?title_color_item?>", String.valueOf(ConfigValue.TitleColorChangeItem));
				html = html.replace("<?inventory_count?>", String.valueOf(Util.formatAdena(ConfigValue.ExpandInventoryPrice)));
				html = html.replace("<?inventory_item?>", String.valueOf(ConfigValue.ExpandInventoryItem));
				html = html.replace("<?name_count?>", String.valueOf(Util.formatAdena(ConfigValue.NickChangePrice)));
				html = html.replace("<?name_item?>", String.valueOf(ConfigValue.NickChangeItem));
				html = html.replace("<?wh_count?>", String.valueOf(Util.formatAdena(ConfigValue.ExpandWarehousePrice)));
				html = html.replace("<?wh_item?>", String.valueOf(ConfigValue.ExpandWarehouseItem));
				html = html.replace("<?separate_count?>", String.valueOf(Util.formatAdena(ConfigValue.SeparateSubPrice)));
				html = html.replace("<?separate_item?>", String.valueOf(ConfigValue.SeparateSubItem));
				html = html.replace("<?changebase_count?>", String.valueOf(Util.formatAdena(ConfigValue.BaseChangePrice)));
				html = html.replace("<?changebase_item?>", String.valueOf(ConfigValue.BaseChangeItem));
				html = html.replace("<?pet_name_count?>", String.valueOf(Util.formatAdena(ConfigValue.PetNameChangePrice)));
				html = html.replace("<?pet_name_item?>", String.valueOf(ConfigValue.PetNameChangeItem));
				html = html.replace("<?rename_count?>", String.valueOf(Util.formatAdena(ConfigValue.ClanNameChangePrice)));
				html = html.replace("<?rename_item?>", String.valueOf(ConfigValue.ClanNameChangeItem));
				html = html.replace("<?cwh_count?>", String.valueOf(Util.formatAdena(ConfigValue.ExpandCWHPrice)));
				html = html.replace("<?cwh_item?>", String.valueOf(ConfigValue.ExpandCWHItem));
			}
			else if(command[1].equals("premium"))
			{
				html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "cabinet/premium.htm", player);
				html = html.replace("<?buttons?>", String.valueOf(DifferentMethods.buttonCab(player)));
			}
			else if(command[1].equals("games"))
			{
				//if((Functions.script & CabinetLicense) != CabinetLicense)
				//	return;
				html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "cabinet/games/index.htm", player);
			}
			else if(command[1].equals("configuration"))
			{
				//if((Functions.script & CabinetLicense) != CabinetLicense)
				//	return;
				html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "cabinet/configuration.htm", player);

				html = html.replace("<?player_lang?>", CommunityBoardCabinet.lang(player).toUpperCase());

				html = html.replace("<?player_dli?>", CommunityBoardCabinet.DroplistIcons(player, false));
				html = html.replace("<?button_dli?>", CommunityBoardCabinet.DroplistIcons(player, true));

				html = html.replace("<?player_noe?>", CommunityBoardCabinet.NoExp(player, false));
				html = html.replace("<?button_noe?>", CommunityBoardCabinet.NoExp(player, true));

				html = html.replace("<?player_notraders?>", CommunityBoardCabinet.NotShowTraders(player, false));
				html = html.replace("<?button_notraders?>", CommunityBoardCabinet.NotShowTraders(player, true));

				html = html.replace("<?player_notShowBuffAnim?>", CommunityBoardCabinet.notShowBuffAnim(player, false));
				html = html.replace("<?button_notShowBuffAnim?>", CommunityBoardCabinet.notShowBuffAnim(player, true));

				html = html.replace("<?player_noShift?>", CommunityBoardCabinet.noShift(player, false));
				html = html.replace("<?button_noShift?>", CommunityBoardCabinet.noShift(player, true));

				html = html.replace("<?player_pathfind?>", CommunityBoardCabinet.pathfind(player, false));
				html = html.replace("<?button_pathfind?>", CommunityBoardCabinet.pathfind(player, true));

				html = html.replace("<?player_trace?>", CommunityBoardCabinet.trace(player, false));
				html = html.replace("<?button_trace?>", CommunityBoardCabinet.trace(player, true));

				html = html.replace("<?player_skill_chance?>", CommunityBoardCabinet.SkillsHideChance(player, false));
				html = html.replace("<?button_skill_chance?>", CommunityBoardCabinet.SkillsHideChance(player, true));

				html = html.replace("<?player_monster_skill_chance?>", CommunityBoardCabinet.MonsterSkillsHideChance(player, false));
				html = html.replace("<?button_monster_skill_chance?>", CommunityBoardCabinet.MonsterSkillsHideChance(player, true));

				html = html.replace("<?player_autolooth?>", CommunityBoardCabinet.AutoLoot(player, false));
				html = html.replace("<?button_autolooth?>", CommunityBoardCabinet.AutoLoot(player, true));

				html = html.replace("<?player_autolooth_herbs?>", CommunityBoardCabinet.AutoLootHerbs(player, false));
				html = html.replace("<?button_autolooth_herbs?>", CommunityBoardCabinet.AutoLootHerbs(player, true));

				html = html.replace("<?player_noCarrier?>", ConfigValue.EnableNoCarrier ? player.getVarB("noCarrier") ? "<font color=\"LEVEL\">" + player.getVar("noCarrier") + "</font>" : "<font color=\"LEVEL\">0</font>" : "<font color=\"FF0000\">N/A</font>");

				String tl = player.getVar("translit");
				if(tl == null)
					html = html.replace("<?player_translit?>", "<font color=\"99CC00\">OFF</font>");
				else if(tl.equals("tl"))
					html = html.replace("<?player_translit?>", "<font color=\"99CC00\">ON</font>");
				else
					html = html.replace("<?player_translit?>", "<font color=\"99CC00\">TC</font>");
			}
			else if(command[1].equals("cfg"))
			{
				//if((Functions.script & CabinetLicense) != CabinetLicense)
				//	return;
				String[] function = bypass.split(":");
				if(function[2].equals("lang"))
				{
					String[] lang = bypass.split(" ");
					if(lang[1].equals("en"))
						player.setVar("lang@", "en", -1);
					else if(lang[1].equals("ru"))
						player.setVar("lang@", "ru", -1);
					else if(lang[1].equals("br"))
						player.setVar("lang@", "br", -1);
				}
				else if(function[2].equals("translit"))
				{
					String[] translit = bypass.split(" ");
					if(translit[1].equals("on"))
						player.setVar("translit", "tl", -1);
					else if(translit[1].equals("tc"))
						player.setVar("translit", "tc", -1);
					else if(translit[1].equals("off"))
						player.unsetVar("translit");
				}
				else if(ConfigValue.EnableNoCarrier && function[2].equals("nocarrier"))
				{
					String[] second = bypass.split(" ");
					int time = NumberUtils.toInt(second[1], ConfigValue.NoCarrierDefaultTime);

					if(time > ConfigValue.NoCarrierMaxTime)
						time = ConfigValue.NoCarrierMaxTime;
					else if(time < ConfigValue.NoCarrierMinTime)
						time = ConfigValue.NoCarrierMinTime;

					player.setVar("noCarrier", String.valueOf(time), -1);
				}
				else if(function[2].equals("droplisticons"))
				{
					if(function[3].equals("on"))
						player.setVar("DroplistIcons", "1");
					else if(function[3].equals("off"))
						player.unsetVar("DroplistIcons");
				}
				else if(function[2].equals("exp"))
				{
					if(function[3].equals("on"))
						player.setVar("NoExp", "1");
					else if(function[3].equals("off"))
						player.unsetVar("NoExp");
				}
				else if(function[2].equals("notraders"))
				{
					if(function[3].equals("on"))
						player.setVar("notraders", "1");
					else if(function[3].equals("off"))
						player.unsetVar("notraders");
				}
				else if(function[2].equals("trace"))
				{
					if(function[3].equals("on"))
						player.setVar("trace", "1");
					else if(function[3].equals("off"))
						player.unsetVar("trace");
				}
				else if(function[2].equals("pathfind"))
				{
					if(function[3].equals("on"))
						player.unsetVar("no_pf");
					else if(function[3].equals("off"))
						player.setVar("no_pf", "1");
				}
				else if(function[2].equals("showbuffanim"))
				{
					if(function[3].equals("on"))
					{
						player.setNotShowBuffAnim(true);
						player.setVar("notShowBuffAnim", "1");
					}
					else if(function[3].equals("off"))
					{
						player.setNotShowBuffAnim(false);
						player.unsetVar("notShowBuffAnim");
					}
				}
				else if(function[2].equals("skillchance"))
				{
					if(function[3].equals("on") && ConfigValue.SkillsShowChance)
						player.setVar("SkillsHideChance", "1");
					else if(function[3].equals("off"))
						player.unsetVar("SkillsHideChance");
				}
				else if(function[2].equals("monsterskillchance"))
				{
					if(function[3].equals("on") && ConfigValue.SkillsShowChance)
						player.setVar("SkillsMobChance", "1");
					else if(function[3].equals("off"))
						player.unsetVar("SkillsMobChance");
				}
				else if(function[2].equals("noshift"))
				{
					if(function[3].equals("on"))
						player.setVar("noShift", "1");
					else if(function[3].equals("off"))
						player.unsetVar("noShift");
				}
				else if(function[2].equals("autoloot"))
				{
					if(function[3].equals("on"))
						player.setAutoLoot(true);
					else if(function[3].equals("off"))
						player.setAutoLoot(false);

					if(!ConfigValue.AutoLootIndividual)
						player.sendMessage("Сервис выключен!");
				}
				else if(function[2].equals("autolootherbs"))
				{
					if(function[3].equals("on"))
						player.setAutoLootHerbs(true);
					else if(function[3].equals("off"))
						player.setAutoLootHerbs(false);

					if(!ConfigValue.AutoLootIndividual)
						player.sendMessage("Сервис выключен!");
				}

				DifferentMethods.communityNextPage(player, "_bbscabinet:configuration");
				return;
			}
			else if(command[1].equals("clan"))
			{
				//if((Functions.script & CabinetLicense) != CabinetLicense)
				//	return;
				html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "cabinet/clan.htm", player);
				html = html.replace("<?rename_count?>", String.valueOf(Util.formatAdena(ConfigValue.ClanNameChangePrice)));
				html = html.replace("<?rename_item?>", String.valueOf(ConfigValue.ClanNameChangeItem));
				html = html.replace("<?cwh_count?>", String.valueOf(Util.formatAdena(ConfigValue.ExpandCWHPrice)));
				html = html.replace("<?cwh_item?>", String.valueOf(ConfigValue.ExpandCWHItem));

				int level = player.getClan() != null ? player.getClan().getLevel() : 0;
				html = html.replace("<?level_count?>", String.valueOf(level >= 11 ? "--" : Util.formatAdena(ConfigValue.ClanLevelPrice[level])));
				html = html.replace("<?level_item?>", String.valueOf(level >= 11 ? "--" : ConfigValue.ClanLevelItem[level]));
				html = html.replace("<?rep_count?>", String.valueOf(Util.formatAdena(ConfigValue.ClanPointPrice[1])));
				html = html.replace("<?rep_item?>", String.valueOf(ConfigValue.ClanPointItem));
			}
			else if(command[1].equals("password"))
			{
				//if((Functions.script & CabinetLicense) != CabinetLicense)
				//	return;
				html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "cabinet/password.htm", player);
				html = html.replace("<?result?>", String.valueOf(player.getPasswordResult()));
				html = html.replace("<?n1?>", String.valueOf(CommunityBoardCabinet.doCaptcha(true, false)));
				html = html.replace("<?n2?>", String.valueOf(CommunityBoardCabinet.doCaptcha(false, true)));
			}
			else if(command[1].startsWith("changepassword"))
			{
				//if((Functions.script & CabinetLicense) != CabinetLicense)
				//	return;
				String[] s = bypass.split(" ");
				String old;
				String newPass1;
				String newPass2;
				String n1;
				String n2;
				String captcha;
				try
				{
					old = s[1];
					newPass1 = s[2];
					newPass2 = s[3];
					n1 = s[4];
					n2 = s[5];
					captcha = s[6];

					CommunityBoardCabinet.changePassword(player, old, newPass1, newPass2, n1, n2, captcha);
				}
				catch(Exception e)
				{
					player.setPasswordResult(new CustomMessage("communityboard.cabinet.password.incorrect.input", player).toString());
				}
				DifferentMethods.communityNextPage(player, "_bbscabinet:password");
				return;
			}
			else
				html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "cabinet/" + command[1] + ".htm", player);

			CustomMessage No = new CustomMessage("common.result.no", player);
			html = html.replace("<?menu?>", Files.read(ConfigValue.CommunityBoardHtmlRoot + "cabinet/menu.htm", player));
			html = html.replace("<?player_class?>", String.valueOf(DifferentMethods.htmlClassNameNonClient(player, player.getClassId().getId())));
			html = html.replace("<?player_level?>", String.valueOf(player.getLevel()));
			html = html.replace("<?online_time?>", String.valueOf(player.getOnlineTime(player)));
			html = html.replace("<?player_name?>", String.valueOf(player.getName()));
			html = html.replace("<?player_pvp?>", String.valueOf(player.getPvpKills()));
			html = html.replace("<?player_pk?>", String.valueOf(player.getPkKills()));
			html = html.replace("<?player_clan1?>", String.valueOf(player.getClan() != null ? player.getClan().getName() : "<font color=\"FF0000\">" + No + "</font>"));
			html = html.replace("<?player_ally?>", String.valueOf(player.getClan() != null && player.getClan().getAlliance() != null ? player.getClan().getAlliance().getAllyName() : "<font color=\"FF0000\">" + No + "</font>"));
			html = html.replace("<?premium_img?>", String.valueOf(DifferentMethods.images(player)));
		}
		else if(bypass.startsWith("_bbsshow"))
		{
			String[] s = bypass.split(";");
			String link = s[1];
			DifferentMethods.communityNextPage(player, link);
			String[] function = bypass.split(":");
			if(function[1].startsWith("calculator"))
				player.sendPacket(new ShowCalc(4393));
			return;
		}
		else if(bypass.startsWith(Commands._bbshtml.toString()))
			html = readHtml(ConfigValue.CommunityBoardHtmlRoot + bypass.substring(9) + ".htm", player);
		else if(bypass.startsWith(Commands._bbspage.toString()))
		{
			String[] link = bypass.split(":");
			html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "page/" + link[1] + ".htm", player);
		}
		else if(bypass.startsWith(Commands._bbsshop.toString()))
		{
			String[] link = bypass.split(":");
			if(link[1].equals("open"))
			{
				if(!ConfigValue.AllowCBMInAbnormalState)
				{
					if(!player.isGM() && (player.isDead() || player.isAlikeDead() || player.isCastingNow() || player.isInCombat() || player.isAttackingNow() || player.isInOlympiadMode() || player.isInVehicle() || player.isFlying() || player.isInFlyingTransform()))
					{
						FailBBSManager.getInstance().parsecmd(bypass, player);
						return;
					}
				}

				String[] next = bypass.split(";");
				DifferentMethods.communityNextPage(player, next[1]);
				try
				{
					String[] multisell = link[2].split(";");
					L2Multisell.getInstance().SeparateAndSend(Integer.parseInt(multisell[0]), player, 0);
				}
				catch(Exception e)
				{}
				return;
			}
			else
				html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "shop/" + link[1] + ".htm", player);
		}
		else if(bypass.startsWith(Commands._bbsevents.toString()))
		{
			String[] link = bypass.split(":");
			if(link[1].equals("page"))
			{
				html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "events/" + link[2] + ".htm", player);

				if(link[2].equals("GroupvsGroup"))
				{
					html = html.replace("<?register?>", ConfigValue.GvG_Enable ? isActive(3) ? button("events.GvG.GvG:addGroup;_bbsevents:page:GroupvsGroup", "Регистрация") : "<font color=\"FF0000\">Дождитесь запуска ивента.</font>" : "<font color=\"FF0000\">Ивент выключен, регистрация недоступна.</font>");
					html = html.replace("<?time?>", EventTimer(3));
				}
				else if(link[2].equals("CaptureTheFlag"))
				{
					html = html.replace("<?register?>", isActive(2) ? isRunned(2) ? (isReg(2, player) ? button("events.CtF.CtF:un_reg;_bbsevents:page:CaptureTheFlag", "Отмена регистрации") : button("events.CtF.CtF:addPlayer;_bbsevents:page:CaptureTheFlag", "Регистрация")) : "<font color=\"FF0000\">Дождитесь запуска ивента.</font>" : "<font color=\"FF0000\">Ивент выключен, регистрация недоступна.</font>");
					html = html.replace("<?time?>", EventTimer(2));
				}
				else if(link[2].equals("LastHero"))
				{
					html = html.replace("<?register?>", isActive(0) ? isRunned(0) ? (isReg(0, player) ? button("events.lastHero.LastHero:un_reg;_bbsevents:page:LastHero", "Отмена регистрации") : button("events.lastHero.LastHero:addPlayer;_bbsevents:page:LastHero", "Регистрация")) : "<font color=\"FF0000\">Дождитесь запуска ивента.</font>" : "<font color=\"FF0000\">Ивент выключен, регистрация недоступна.</font>");
					html = html.replace("<?time?>", EventTimer(0));
				}
				else if(link[2].equals("EventBox"))
				{
					html = html.replace("<?register?>", isActive(5) ? isRunned(5) ? button("events.EventBox.EventBox:addPlayer;_bbsevents:page:GroupvsGroup", "Регистрация") : "<font color=\"FF0000\">Дождитесь запуска ивента.</font>" : "<font color=\"FF0000\">Ивент выключен, регистрация недоступна.</font>");
					html = html.replace("<?time?>", EventTimer(1));
				}
				else if(link[2].equals("TeamvsTeam"))
				{
					html = html.replace("<?register?>", isActive(1) ? isRunned(1) ? (isReg(1, player) ? button("events.TvT.TvT:un_reg;_bbsevents:page:TeamvsTeam", "Отмена регистрации") : button("events.TvT.TvT:addPlayer;_bbsevents:page:TeamvsTeam", "Регистрация")) : "<font color=\"FF0000\">Дождитесь запуска ивента.</font>" : "<font color=\"FF0000\">Ивент выключен, регистрация недоступна.</font>");
					html = html.replace("<?time?>", EventTimer(1));
				}
				else if(link[2].equals("ReachHeaven"))
				{
					html = html.replace("<?register?>", isActive(4) ? isRunned(4) ? button("ReachHeaven.ReachHeaven:addPlayer;_bbsevents:page:ReachHeaven", "Регистрация") : "<font color=\"FF0000\">Дождитесь запуска ивента.</font>" : "<font color=\"FF0000\">Ивент выключен, регистрация недоступна.</font>");
					html = html.replace("<?time?>", EventTimer(4));
				}
				else if(link[2].equals("AllEvents"))
					html = html.replace("<?time?>", EventTimer());
			}
		}
		if(html != null && !html.isEmpty())
			separateAndSend(addCustomReplace(html), player);
	}

	private String button(String bypass, String name)
	{
		return "<button value=\""+name+"\" action=\"bypass -h _bbsscripts; ;" + bypass + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">";
	}

	private static String EventTimer()
	{
		String times = "";
		String ON = "<font color=00ff00>Активен.</font>";
		String OFF = "<font color=ff0000>Отключен.</font>";

		times += "<table width=750 height=40><tr>" + block("Последний Герой", 1, false, EventTimer(0) + ".");
		times += block("Команда vs. Команда", 1, false, EventTimer(1) + ".") + "</tr></table>";

		times += "<table width=750 height=40><tr>" + block("Захват Флага", 1, false, EventTimer(2) + ".");
		times += block("Группа vs. Группа", 1, false, EventTimer(3) + ".") + "</tr></table>";

		times += "<table width=750 height=40><tr>" + block("Бойцовский Клуб", 0, false, ConfigValue.FightClubEnabled ? ON : OFF);
		times += block("Сундук Теней", 0, false, Functions.IsActive("CofferofShadows") ? ON : OFF) + "</tr></table>";

		times += "<table width=750 height=40><tr>" + block("Сверкающие Медали", 0, false, Functions.IsActive("glitter") ? ON : OFF);
		times += block("Сбор Урожая", 0, false, Functions.IsActive("TheFallHarvest") ? ON : OFF) + "</tr></table>";

		times += "<table width=750 height=40><tr>" + block("Мастер Заточки", 0, false, Functions.IsActive("MasterOfEnchanting") ? ON : OFF);
		times += block("День святого Валентина", 0, false, Functions.IsActive("March8") ? ON : OFF) + "</tr></table>";

		times += "<table width=750 height=40><tr>" + block("Поиски Сокровищ", 0, false, Functions.IsActive("RabbitsToRiches") ? ON : OFF);
		times += block("L2Day", 0, false, Functions.IsActive("l2day") ? ON : OFF) + "</tr></table>";

		times += "<table width=750 height=40><tr>" + block("Перемены в Душе", 0, false, Functions.IsActive("heart") ? ON : OFF);
		times += block("L2Coins", 0, false, Functions.IsActive("L2Coins") ? ON : OFF) + "</tr></table>";

		times += "<table width=750 height=40><tr>" + block("Спасение Снеговика", 0, true, Functions.IsActive("SavingSnowman") ? ON : OFF);
		times += block("Рождество", 0, true, Functions.IsActive("Christmas") ? ON : OFF) + "</tr></table>";

		return times;
	}

	private static String block(String title, int type, boolean last, String status)
	{
		StringBuilder html = new StringBuilder();

		html.append("<td WIDTH=345 align=center valign=top>");

		html.append("<table border=0 cellspacing=0 cellpadding=0>");
		html.append("<tr>");
		html.append("<td width=345>");
		html.append("<img src=\"l2ui.squaregray\" width=345 height=1>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");

		html.append("<table border=0 cellspacing=4 cellpadding=3>");
		html.append("<tr>");
		html.append("<td FIXWIDTH=50 align=right valign=top>");
		html.append("<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background=\"icon." + (type == 1 ? "skill6024" : "skill6038") + "\">");
		html.append("<tr>");
		html.append("<td width=32 align=center valign=top>");
		html.append("<img src=\"" + (type == 1 ? "l2ui_ch3.petitem_click" : "icon.panel_2") + "\" width=32 height=32>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</td>");
		html.append("<td FIXWIDTH=300 align=left valign=top>");
		html.append("<font color=" + (type == 1 ? "LEVEL" : "CCFF33") + ">" + title + ".</font>&nbsp;<br1>›&nbsp;" + (type == 1 ? "" : "Статус: ") + status);
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");

		if(last)
		{
			html.append("<table border=0 cellspacing=0 cellpadding=0>");
			html.append("<tr>");
			html.append("<td width=345>");
			html.append("<img src=\"l2ui.squaregray\" width=345 height=1>");
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");
		}

		html.append("</td>");

		return html.toString();
	}

	private static boolean isReg(int id, L2Player player)
	{
		switch(id)
		{
			case 0:
				if((Boolean) Functions.callScripts("events.lastHero.LastHero", "is_reg", new Object[] {player}))
					return true;
				break;
			case 1:
				if((Boolean) Functions.callScripts("events.TvT.TvT", "is_reg", new Object[] {player}))
					return true;
				break;
			case 2:
				if((Boolean) Functions.callScripts("events.CtF.CtF", "is_reg", new Object[] {player}))
					return true;
		}
		return false;
	}

	private static boolean isRunned(int id)
	{
		switch(id)
		{
			case 0:
				if((Boolean) Functions.callScripts("events.lastHero.LastHero", "isRunned", new Object[] {}))
					return true;
				break;
			case 1:
				if((Boolean) Functions.callScripts("events.TvT.TvT", "isRunned", new Object[] {}))
					return true;
				break;
			case 2:
				if((Boolean) Functions.callScripts("events.CtF.CtF", "isRunned", new Object[] {}))
					return true;
				break;
			case 3:
				if((Boolean) Functions.callScripts("events.GvG.GvG", "isActive", new Object[] {}))
					return true;
				break;
			case 4:
				if((Boolean) Functions.callScripts("ReachHeaven.ReachHeaven", "isRunned", new Object[] {}))
					return true;
				break;
			case 5:
				if((Boolean) Functions.callScripts("events.EventBox.EventBox", "isRunned", new Object[] {}))
					return true;
				break;
		}
		return false;
	}

	private static boolean isActive(int id)
	{
		switch(id)
		{
			case 0:
				if((Boolean) Functions.callScripts("events.lastHero.LastHero", "isActive", new Object[] {}))
					return true;
				break;
			case 1:
				if((Boolean) Functions.callScripts("events.TvT.TvT", "isActive", new Object[] {}))
					return true;
				break;
			case 2:
				if((Boolean) Functions.callScripts("events.CtF.CtF", "isActive", new Object[] {}))
					return true;
				break;
			case 3:
				if((Boolean) Functions.callScripts("events.GvG.GvG", "isActive", new Object[] {}))
					return true;
				break;
			case 4:
				if((Boolean) Functions.callScripts("ReachHeaven.ReachHeaven", "isActive", new Object[] {}))
					return true;
				break;
			case 5:
				if((Boolean) Functions.callScripts("events.EventBox.EventBox", "isActive", new Object[] {}))
					return true;
				break;
		}
		return false;
	}

	private static String EventTimer(int id)
	{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat date = new SimpleDateFormat("HH:mm");

		int[] time = new int[0];
		boolean active = isRunned(id);
		switch(id)
		{
			case 0:
				time = ConfigValue.LastHeroStartTime;
				break;
			case 1:
				time = ConfigValue.TeamvsTeamStartTime;
				break;
			case 2:
				time = ConfigValue.CaptureTheFlagStartTime;
				break;
			case 3:
				time = ConfigValue.GvG_StartTimeList;
				break;
			case 4:
				time = ConfigValue.RH_StartTimeList;
				break;
		}
		List<Long> time2 = new ArrayList<Long>();
		if(time.length > 0 && time[0] > -1)
			for(int i = 0; i < time.length; i += 2)
			{
				Calendar ci = Calendar.getInstance();
				ci.set(Calendar.HOUR_OF_DAY, time[i]);
				ci.set(Calendar.MINUTE, time[i + 1]);
				ci.set(Calendar.SECOND, 00);

				long delay = ci.getTimeInMillis() - System.currentTimeMillis();
				if(delay > -600000)
					time2.add(delay);
				ci = null;
			}
		Collections.sort(time2);
		String times = "";
		int count = 0;
		for(long time3 : time2)
		{
			calendar.add(Calendar.MILLISECOND, (int) time3);

			if(active)
				times = "<font color=00CC00>Проводиться в данный момент</font>";
			else
			{
				if(time3 > 0)
				{
					times += (count != 0 ? ", " : "") + date.format(calendar.getTime());
					count++;
				}
			}
			calendar.add(Calendar.MILLISECOND, -(int) time3);
		}
		calendar = null;

		if(times.equals("") || times == null)
			times = "<font color=FF0000>Сегодня больше не проводится</font>";

		return times;
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player)
	{}
}
