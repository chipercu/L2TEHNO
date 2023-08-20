package services;

import java.util.Map.Entry;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.database.mysql;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2SubClass;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.base.PlayerClass;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.CharNameTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.util.GArray;
import l2open.util.Log;
import l2open.util.Util;
import l2open.util.Files;

public class Rename extends Functions implements ScriptFile
{
	public void rename_page()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		String html = Files.read("data/scripts/services/Rename_rename.htm", player);
		html = html.replace("<?price?>", Util.formatAdena(ConfigValue.NickChangePrice));
		html = html.replace("<?price_id?>", String.valueOf(ConfigValue.NickChangeItem));
		show(html, player);
	}

	public void changesex_page()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(!player.isInPeaceZone())
		{
			show(Files.read("data/scripts/services/Rename_changesex1.htm", player), player);
			return;
		}

		if(player.getRace() == Race.kamael)
		{
			show(Files.read("data/scripts/services/Rename_changesex2.htm", player), player);
			return;
		}
		String name = player.getSex() == 0 ? "Мужчина" : "Женщина";
		boolean isMale = player.getTemplate().isMale;

		String html = Files.read("data/scripts/services/Rename_changesex.htm", player);
		html = html.replace("<?button1?>", isMale ? "<img src=\"L2UI_CT1.ItemWindow_DF_SlotBox_Disable\" width=\"32\" height=\"32\">" : "<button action=\"bypass -h scripts_services.Rename:changesex\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>");
		html = html.replace("<?button2?>", isMale ? "<button action=\"bypass -h scripts_services.Rename:changesex\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>" : "<img src=\"L2UI_CT1.ItemWindow_DF_SlotBox_Disable\" width=\"32\" height=\"32\">");
		html = html.replace("<?price?>", Util.formatAdena(ConfigValue.SexChangePrice));
		html = html.replace("<?price_id?>", String.valueOf(ConfigValue.SexChangeItem));
		html = html.replace("<?name?>", name);
		show(html, player);
	}

	public void separate_page()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(player.isHero())
		{
			show(Files.read("data/scripts/services/Rename_separate1.htm", player), player);
			return;
		}
		else if(player.getSubClasses().size() == 1)
		{
			show(Files.read("data/scripts/services/Rename_separate2.htm", player), player);
			return;
		}
		else if(player.isSubClassActive())
		{
			show(Files.read("data/scripts/services/Rename_separate3.htm", player), player);
			return;
		}
		else if(player.getActiveClass().getLevel() < ConfigValue.NoblessSellSubLevel)
		{
			String html = Files.read("data/scripts/services/Rename_separate4.htm", player);
			html = html.replace("<?arg1?>", String.valueOf(ConfigValue.NoblessSellSubLevel));
			show(html, player);
			return;
		}

		String html = Files.read("data/scripts/services/Rename_separate.htm", player);
		html = html.replace("<?arg1?>", String.valueOf(new CustomMessage("scripts.services.Separate.Price", player).addString(Util.formatAdena(ConfigValue.SeparateSubPrice)).addItemName(ConfigValue.SeparateSubItem)));

		String append = "";
		for(L2SubClass s : player.getSubClasses().values())
			if(!s.isBase() && s.getClassId() != ClassId.inspector.getId() && s.getClassId() != ClassId.judicator.getId() && s.getCertification() == 0)
				append += "<tr><td><button value=\"" + new CustomMessage("scripts.services.Separate.Button", player).addString(ClassId.values()[s.getClassId()].toString()) + "\" action=\"bypass -h scripts_services.Rename:separate " + s.getClassId() + " $name\" width=200 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>";

		html = html.replace("<?arg2?>", String.valueOf(append));
		show(html, player);
	}

	public void separate(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(player.isHero())
		{
			show(Files.read("data/scripts/services/Rename_separate1.htm", player), player);
			return;
		}
		else if(player.getSubClasses().size() == 1)
		{
			show(Files.read("data/scripts/services/Rename_separate2.htm", player), player);
			return;
		}
		else if(player.isSubClassActive())
		{
			show(Files.read("data/scripts/services/Rename_separate3.htm", player), player);
			return;
		}
		else if(player.getActiveClass().getLevel() < ConfigValue.NoblessSellSubLevel)
		{
			String html = Files.read("data/scripts/services/Rename_separate4.htm", player);
			html = html.replace("<?arg1?>", String.valueOf(ConfigValue.NoblessSellSubLevel));
			show(html, player);
			return;
		}
		else if(param.length < 2)
		{
			show("You must specify target.", player);
			return;
		}
		else if(getItemCount(player, ConfigValue.SeparateSubItem) < ConfigValue.SeparateSubPrice)
		{
			if(ConfigValue.SeparateSubItem == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		int classtomove = Integer.parseInt(param[0]);
		if(classtomove == 136 && ConfigValue.SeparateSubBlockJudicator)
		{
			show(Files.read("data/scripts/services/Rename_separate5.htm", player), player);
			return;
		}

		int newcharid = 0;
		for(Entry<Integer, String> e : player.getAccountChars().entrySet())
			if(e.getValue().equalsIgnoreCase(param[1]))
				newcharid = e.getKey();

		if(newcharid == 0)
		{
			show("Target not exists.", player);
			return;
		}

		if(mysql.simple_get_int("level", "character_subclasses", "char_obj_id=" + newcharid + " AND level > 1") > 1)
		{
			show("Target must have level 1.", player);
			return;
		}

		mysql.set("DELETE FROM character_subclasses WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_skills WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_skills_save WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_effects_save WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_hennas WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_shortcuts WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_variables WHERE obj_id=" + newcharid);

		mysql.set("UPDATE character_subclasses SET char_obj_id=" + newcharid + ", isBase=1, certification='0' WHERE char_obj_id=" + player.getObjectId() + " AND class_id=" + (ConfigValue.Multi_Enable ? 0 : classtomove));
		mysql.set("UPDATE character_skills SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + (ConfigValue.Multi_Enable ? 0 : classtomove));
		mysql.set("UPDATE character_skills_save SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + (ConfigValue.Multi_Enable ? 0 : classtomove));
		mysql.set("UPDATE character_effects_save SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + (ConfigValue.Multi_Enable ? 0 : classtomove));
		mysql.set("UPDATE character_hennas SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + (ConfigValue.Multi_Enable ? 0 : classtomove));
		mysql.set("UPDATE character_shortcuts SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + (ConfigValue.Multi_Enable ? 0 : classtomove));

		//mysql.set("UPDATE olympiad_history SET object_id_1=" + newcharid + ", class_id_1=" + classtomove + " WHERE object_id_1=" + player.getObjectId());
		//mysql.set("UPDATE olympiad_history SET object_id_2=" + newcharid + ", class_id_2=" + classtomove + " WHERE object_id_2=" + player.getObjectId());

		mysql.set("UPDATE character_variables SET obj_id=" + newcharid + " WHERE obj_id=" + player.getObjectId() + " AND name like 'TransferSkills%'");

		//Olympiad.changeNobleClass(player.getObjectId(), classtomove);
		//Olympiad.changeNobleId(player.getObjectId(), newcharid);

		PlayerData.getInstance().modifySubClass(player, classtomove, 0);

		removeItem(player, ConfigValue.SeparateSubItem, ConfigValue.SeparateSubPrice);
		Log.add("Character " + player.getName() + " base changed to " + newcharid, "services");
		player.logout(false, false, false, true);
	}

	public void changebase_page()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(!player.isInPeaceZone())
		{
			StringBuilder append = new StringBuilder();

			append.append("<html noscrollbar>");
			append.append("<title>Смена базового класса</title>");
			append.append("<body>");
			append.append("<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
			append.append("<tr>");
			append.append("<td valign=\"top\">");
			append.append("<table width=280 align=\"center\">");
			append.append("<tr>");
			append.append("<td valign=\"top\" width=10></td>");
			append.append("<td valign=\"top\" align=\"center\" width=270><br>");
			append.append("<font color=\"FF0000\" name=\"hs12\">Ошибка</font>");
			append.append("<br><br>Вы должны быть в мирной зоне.</td>");
			append.append("</tr>");
			append.append("</table>");
			append.append("</td>");
			append.append("</tr>");
			append.append("</table>");
			append.append("</body>");
			append.append("</html>");

			show(append.toString(), player);
			return;
		}

		if(player.isHero())
		{
			StringBuilder append = new StringBuilder();

			append.append("<html noscrollbar>");
			append.append("<title>Смена базового класса</title>");
			append.append("<body>");
			append.append("<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
			append.append("<tr>");
			append.append("<td valign=\"top\">");
			append.append("<table width=280 align=\"center\">");
			append.append("<tr>");
			append.append("<td valign=\"top\" width=10></td>");
			append.append("<td valign=\"top\" align=\"center\" width=270><br>");
			append.append("<font color=\"FF0000\" name=\"hs12\">Ошибка</font>");
			append.append("<br><br>Сервис не доступен для Героев.</td>");
			append.append("</tr>");
			append.append("</table>");
			append.append("</td>");
			append.append("</tr>");
			append.append("</table>");
			append.append("</body>");
			append.append("</html>");

			show(append.toString(), player);
			return;
		}

		String append = "Base class changing";
		append += "<br>";
		append += "<font color=\"LEVEL\">" + new CustomMessage("scripts.services.BaseChange.Price", player).addString(Util.formatAdena(ConfigValue.BaseChangePrice)).addItemName(ConfigValue.BaseChangeItem) + "</font>";
		append += "<table>";

		GArray<L2SubClass> possible = new GArray<L2SubClass>();
		if(player.getBaseClassId() == player.getActiveClassId())
		{
			possible.addAll(player.getSubClasses().values());
			possible.remove(player.getSubClasses().get(player.getBaseClassId()));

			for(L2SubClass s : player.getSubClasses().values())
				for(L2SubClass s2 : player.getSubClasses().values())
					if(s != s2 && !PlayerClass.areClassesComportable(PlayerClass.values()[s.getClassId()], PlayerClass.values()[s2.getClassId()]) || s2.getLevel() < ConfigValue.NoblessSellSubLevel)
						possible.remove(s2);
		}

		if(possible.isEmpty())
			append += "<tr><td width=300>" + new CustomMessage("scripts.services.BaseChange.NotPossible", player) + "</td></tr>";
		else
			for(L2SubClass s : possible)
				append += "<tr><td><button value=\"" + new CustomMessage("scripts.services.BaseChange.Button", player).addString(ClassId.values()[s.getClassId()].toString()) + "\" action=\"bypass -h scripts_services.Rename:changebase " + s.getClassId() + "\" width=200 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>";
		append += "</table>";
		show(append, player);
	}

	public void changebase(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(!player.isInPeaceZone())
		{
			StringBuilder append = new StringBuilder();

			append.append("<html noscrollbar>");
			append.append("<title>Смена базового класса</title>");
			append.append("<body>");
			append.append("<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
			append.append("<tr>");
			append.append("<td valign=\"top\">");
			append.append("<table width=280 align=\"center\">");
			append.append("<tr>");
			append.append("<td valign=\"top\" width=10></td>");
			append.append("<td valign=\"top\" align=\"center\" width=270><br>");
			append.append("<font color=\"FF0000\" name=\"hs12\">Ошибка</font>");
			append.append("<br><br>Вы должны быть в мирной зоне.</td>");
			append.append("</tr>");
			append.append("</table>");
			append.append("</td>");
			append.append("</tr>");
			append.append("</table>");
			append.append("</body>");
			append.append("</html>");

			show(append.toString(), player);
			return;
		}

		if(player.isSubClassActive())
		{
			StringBuilder append = new StringBuilder();

			append.append("<html noscrollbar>");
			append.append("<title>Смена базового класса</title>");
			append.append("<body>");
			append.append("<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
			append.append("<tr>");
			append.append("<td valign=\"top\">");
			append.append("<table width=280 align=\"center\">");
			append.append("<tr>");
			append.append("<td valign=\"top\" width=10></td>");
			append.append("<td valign=\"top\" align=\"center\" width=270><br>");
			append.append("<font color=\"FF0000\" name=\"hs12\">Ошибка</font>");
			append.append("<br><br>Перейдите на базовый класс.</td>");
			append.append("</tr>");
			append.append("</table>");
			append.append("</td>");
			append.append("</tr>");
			append.append("</table>");
			append.append("</body>");
			append.append("</html>");

			show(append.toString(), player);
			return;
		}

		if(player.isHero())
		{
			StringBuilder append = new StringBuilder();

			append.append("<html noscrollbar>");
			append.append("<title>Смена базового класса</title>");
			append.append("<body>");
			append.append("<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
			append.append("<tr>");
			append.append("<td valign=\"top\">");
			append.append("<table width=280 align=\"center\">");
			append.append("<tr>");
			append.append("<td valign=\"top\" width=10></td>");
			append.append("<td valign=\"top\" align=\"center\" width=270><br>");
			append.append("<font color=\"FF0000\" name=\"hs12\">Ошибка</font>");
			append.append("<br><br>Сервис не доступен для Героев.</td>");
			append.append("</tr>");
			append.append("</table>");
			append.append("</td>");
			append.append("</tr>");
			append.append("</table>");
			append.append("</body>");
			append.append("</html>");

			show(append.toString(), player);
			return;
		}

		if(getItemCount(player, ConfigValue.BaseChangeItem) < ConfigValue.BaseChangePrice)
		{
			if(ConfigValue.BaseChangeItem == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		player.getActiveClass().setBase(false);
		if(player.getActiveClass().getLevel() > Experience.getMaxSubLevel())
			player.setLevel(Experience.getMaxSubLevel());
		player.checkSkills(0);

		int target = Integer.parseInt(param[0]);
		player.getSubClasses().get(target).setBase(true);
		player.getSubClasses().get(target).setCertification(player.getSubClasses().get(target).getCertification());
		player.setBaseClass(target);

		player.setHairColor(0);
		player.setHairStyle(0);
		player.setFace(0);
		Olympiad.unRegisterNoble(player);
		mysql.set("UPDATE olympiad_nobles SET class_id=" + target + " WHERE char_id=" + player.getObjectId());
		mysql.set("UPDATE olympiad_history SET class_id_1=" + target + " WHERE object_id_1=" + player.getObjectId());
		mysql.set("UPDATE olympiad_history SET class_id_2=" + target + " WHERE object_id_2=" + player.getObjectId());
		Olympiad.changeNobleClass(player.getObjectId(), target);

		removeItem(player, ConfigValue.BaseChangeItem, ConfigValue.BaseChangePrice);
		player.logout(false, false, false, true);
		Log.add("Character " + player + " base changed to " + target, "services");
	}

	public void rename(String[] args)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(args.length != 1)
		{
			show(Files.read("data/scripts/services/Rename_rename1.htm", player), player);
			return;
		}
		else if(player.getSiegeState() != 0)
		{
			show(Files.read("data/scripts/services/Rename_rename2.htm", player), player);
			return;
		}

		String name = args[0];
		if(!Util.isMatchingRegexp(name, ConfigValue.NickChangeSymbolTemp))
		{
			show(Files.read("data/scripts/services/Rename_rename3.htm", player), player);
			return;
		}
		else if(getItemCount(player, ConfigValue.NickChangeItem) < ConfigValue.NickChangePrice)
		{
			if(ConfigValue.NickChangeItem == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}
		else if(CharNameTable.getInstance().doesCharNameExist(name))
		{
			show(Files.read("data/scripts/services/Rename_rename4.htm", player), player);
			return;
		}

		removeItem(player, ConfigValue.NickChangeItem, ConfigValue.NickChangePrice);

		String oldName = player.getName();
		player.reName(name, true);
		if(ConfigValue.ReNameAnnouncements)
			Announcements.getInstance().announceToAll("'"+oldName+"' сменил свой ник на '"+name+"'.");
		Log.add("Character " + oldName + " renamed to " + name, "renames");
		StringBuilder append = new StringBuilder();

		String html = Files.read("data/scripts/services/Rename_rename5.htm", player);
		html = html.replace("<?old_name?>", oldName);
		html = html.replace("<?name?>", name);
		show(html, player);
	}

	public void changesex()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(player.getRace() == Race.kamael)
		{
			show(Files.read("data/scripts/services/Rename_changesex2.htm", player), player);
			return;
		}

		if(!player.isInPeaceZone())
		{
			show(Files.read("data/scripts/services/Rename_changesex1.htm", player), player);
			return;
		}

		if(getItemCount(player, ConfigValue.SexChangeItem) < ConfigValue.SexChangePrice)
		{
			if(ConfigValue.SexChangeItem == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		ThreadConnection con = null;
		FiltredPreparedStatement offline = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("UPDATE characters SET sex = ? WHERE obj_Id = ?");
			offline.setInt(1, player.getSex() == 1 ? 0 : 1);
			offline.setInt(2, player.getObjectId());
			offline.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			show(new CustomMessage("common.Error", player), player);
			return;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, offline);
		}

		player.setHairColor(0);
		player.setHairStyle(0);
		player.setFace(0);
		removeItem(player, ConfigValue.SexChangeItem, ConfigValue.SexChangePrice);
		player.logout(false, false, false, true);
		Log.add("Character " + player + " sex changed to " + (player.getSex() == 1 ? "male" : "female"), "renames");
	}

	public void rename_clan_page()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(player.getClan() == null || !player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_A_CLAN_LEADER).addName(player));
			return;
		}

		String html = Files.read("data/scripts/services/Rename_rename_clan.htm", player);
		html = html.replace("<?price?>", Util.formatAdena(ConfigValue.ClanNameChangePrice));
		html = html.replace("<?price_id?>", String.valueOf(ConfigValue.ClanNameChangeItem));
		show(html, player);
	}

	public void rename_clan(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null || param == null || param.length == 0)
			return;

		if(player.getClan() == null || !player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_A_CLAN_LEADER).addName(player));
			return;
		}

		if(player.getSiegeState() != 0)
		{
			show(new CustomMessage("scripts.services.Rename.SiegeNow", player), player);
			return;
		}

		if(!Util.isMatchingRegexp(param[0], ConfigValue.ClanNameTemplate))
		{
			player.sendPacket(Msg.CLAN_NAME_IS_INCORRECT);
			return;
		}
		if(ClanTable.getInstance().getClanByName(param[0]) != null)
		{
			player.sendPacket(Msg.THIS_NAME_ALREADY_EXISTS);
			return;
		}

		if(getItemCount(player, ConfigValue.ClanNameChangeItem) < ConfigValue.ClanNameChangePrice)
		{
			if(ConfigValue.ClanNameChangeItem == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		show(new CustomMessage("scripts.services.Rename.changedname", player).addString(player.getClan().getName()).addString(param[0]), player);
		player.getClan().setName(param[0]);
		PlayerData.getInstance().updateClanInDB(player.getClan());
		removeItem(player, ConfigValue.ClanNameChangeItem, ConfigValue.ClanNameChangePrice);
		player.getClan().broadcastClanStatus(true, true, false);
		Log.add("Character " + player + " ClanName " + player.getClan().getName() + " changed to " + param[0], "services");
	}

	public void onLoad()
	{
		_log.info("Loaded Service: change sex services.");
		_log.info("Loaded Service: change name services.");
		_log.info("Loaded Service: change clan name services.");
		_log.info("Loaded Service: change base class services.");
		_log.info("Loaded Service: separate services.");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}