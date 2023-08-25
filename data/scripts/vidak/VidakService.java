package vidak;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.idfactory.IdFactory;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.*;
import l2open.gameserver.model.items.*;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.handler.*;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.*;

import java.io.File;

import static l2open.gameserver.model.base.Race.*;

/**
После создания перса мы появляемся в мире, сразу же выскакивает окошко в котором нам предложат выбрать лвл которым мы станем. (перс вкопан в землю и до тех пор пока не заполним, он не побежит)
Текст:Приветствую вас в мире .....................................
Стать  40 лвлом     или   Стать 80 лвлом
1)Выбрав 80 лвл, идет следом выбор профы которой мы будем играть.
2) выбрав профу, выскакивает окошко с выбором шмота( целые сеты) нажав на выбранный шмот он сразу одевается на перса. (бижа идет в месте со шмотом)
3) Далее идет выбор пуши которая так же одевается сразу же на перса.
4) 1000 сосок сразу появляются в инвентаре и на панельке активны
(окна всех 4 пунктов нельзя закрыть) если делают ребут начинать с того окна на котором остановились.

5) Следом идет окно( поздравляю вы готовы к массовым сражениям однако ваш аккаунт находится в зоне риска, сделайте привязку сейчас,  либо она будет сделана автоматически после 3х заходов на аккаунт.)
5.1 Кнопка сделать привязку. После нажатия закрывать окно.
5.2 Кнопка закрыть окно.
5.3 Не делать привязку и не беспокоить меня больше. (снизу будет надпись типа если украдут шмот то все мы шлем вас нафиг)
Окно 5 можно закрыть. Если человек нажал не делать привязку,  значит окно больше не выскакивает. В базе сделать пометку отказался от привязки. Но он может сделать привязку через алт+б.
2-
1)Выбрав вариант с 40 лвлом  следом идет вариант с выбором профы.
и тупо по аналогии все пункты выше. Только сеты и пушки разные 
**/
public class VidakService  extends Functions implements ScriptFile
{
	public void lang(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(param[0].equals("en"))
			player.setVar("lang@", "en", -1);
		else
			player.setVar("lang@", "ru", -1);
		int type = player.getVarInt("VidakSystem", ConfigValue.VidakSystemType);
		int level = type-type%10;
		if(type == 0)
			show123(Files.read("data/scripts/vidak/vidak_service.htm", player), player);
		else if(type%10 == 1)
			showClassPage(player, level);
		else if(type%10 == 2)
			show123(Files.read("data/scripts/vidak/vidak_service_armor"+(player.getRace() == Race.kamael ? "_kamael" : "")+"_"+level+".htm", player), player);
		else if(type%10 == 3)
			show123(Files.read("data/scripts/vidak/vidak_service_weapon_"+level+".htm", player), player);
		else if(type%10 == 4 || type%10 == 5)
			show123(Files.read("data/scripts/vidak/vidak_service_hwid.htm", player), player);
	}

	public void lvl_up(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		int level = Integer.parseInt(param[0]);
		player.setVar("VidakSystem", String.valueOf(level+1));
		Long exp_add = Experience.LEVEL[level] - player.getExp();
		player.addExpAndSp(exp_add, 0, false, false);
		showClassPage(player, level);
		//show123(Files.read("data/scripts/vidak/vidak_service_"+level+".htm", player), player);
	}

	public void set_class(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		int classID = Integer.parseInt(param[0]);
		player.setClassId(classID, false);
		player.updateStats();
		player.broadcastUserInfo(true);

		if(player.getClassId().getLevel() == 3)
			player.sendPacket(Msg.YOU_HAVE_COMPLETED_THE_QUEST_FOR_3RD_OCCUPATION_CHANGE_AND_MOVED_TO_ANOTHER_CLASS_CONGRATULATIONS);
		else
			player.sendPacket(Msg.CONGRATULATIONS_YOU_HAVE_TRANSFERRED_TO_A_NEW_CLASS);
		giveAllSkills(player);
		int level = Integer.parseInt(param[1]);
		player.setVar("VidakSystem", String.valueOf(level+2));
		show123(Files.read("data/scripts/vidak/vidak_service_armor"+(player.getRace() == Race.kamael ? "_kamael" : "")+"_"+level+".htm", player), player);
	}

	public void buy_armor(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		int level = Integer.parseInt(param[0]);
		for(String id : param[1].split(":"))
			create_item(player, Integer.parseInt(id), 1);
		player.setVar("VidakSystem", String.valueOf(level+3));
		show123(Files.read("data/scripts/vidak/vidak_service_weapon_"+level+".htm", player), player);
	}

	public void buy_weapon(String[] param)
	{
		final L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		int level = Integer.parseInt(param[0]);
		for(String id : param[1].split(":"))
			create_item(player, Integer.parseInt(id), 1);
		int item_id = Integer.parseInt(param[2]);

		L2ItemInstance item = create_item(player, item_id, 1000);
		L2ShortCut newsc = new L2ShortCut(9, 0, L2ShortCut.TYPE_ITEM, item.getObjectId(), -1);
		player.sendPacket(new ShortCutRegister(newsc));
		player.registerShortCut(newsc);

		player.addAutoSoulShot(item_id);
		player.sendPacket(new ExAutoSoulShot(item_id, true));
		player.sendPacket(new SystemMessage(SystemMessage.THE_USE_OF_S1_WILL_NOW_BE_AUTOMATED).addString(item.getName()));
		IItemHandler handler = ItemHandler.getInstance().getItemHandler(item_id);
		handler.useItem(player, item, false);

		player.setVar("VidakSystem", String.valueOf(level+4));
		show123(Files.read("data/scripts/vidak/vidak_service_hwid.htm", player), player);

		String text = player.isLangRus() ? "Через 5 секунд вы будете телепортированы." : "After 5 seconds you will be teleported.";

		player.sendPacket(new ExShowScreenMessage(text, 10000, ScreenMessageAlign.TOP_CENTER, text.length() < 64));
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			public void run()
			{
				int rnd = 100;
				player.setReflection(0);
				player.teleToLocation(83427 + Rnd.get(-rnd, rnd), 148619 + Rnd.get(-rnd, rnd), -3408);
			}
		}, 5000);
	}

	public void hwid_lock()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		player.addAccLock(player.getHWIDs());
		if(player.isLangRus())
			player.sendMessage("Вы успешно привязали свой аккаунт, к данному компьютеру.");
		else
			player.sendMessage("You successfully tied your account to this computer.");

		player.is_block = false;
		player.setFlying(false);

		player.setVar("VidakSystem", String.valueOf(9));
		player.sendPacket(TutorialCloseHtml.STATIC);
		//show(Files.read("data/scripts/vidak/vidak_service_ok.htm", player), player);
	}

	public void no_hwid_lock()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		player.is_block = false;
		player.setFlying(false);
		player.setVar("VidakSystem", String.valueOf(8));
		player.sendPacket(TutorialCloseHtml.STATIC);
		show_welcome(player);
	}

	public void cl_hwid_lock()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		player.is_block = false;
		player.setFlying(false);
		player.sendPacket(TutorialCloseHtml.STATIC);
		show_welcome(player);
	}

	private String page(CustomMessage text)
	{
		StringBuilder html = new StringBuilder();

		html.append("<tr>");
		html.append("<td WIDTH=20 align=left valign=top></td>");
		html.append("<td WIDTH=190 align=left valign=top>");
		html.append(text);
		html.append("</td>");
		html.append("</tr>");

		return html.toString();
	}

	private String block(String icon, String text, CustomMessage action, String bypass)
	{
		StringBuilder html = new StringBuilder();

		html.append("<table border=1 cellspacing=0 cellpadding=0>");
		html.append("<tr>");
		html.append("<td width=220><img src=\"l2ui.squaregray\" width=\"220\" height=\"1\"></td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("<table border=1 cellspacing=4 cellpadding=3>");
		html.append("<tr>");
		html.append("<td FIXWIDTH=50 align=right valign=top><img src=\"" + icon + "\" width=32 height=32></td>");
		html.append("<td FIXWIDTH=176 align=left valign=top>");
		html.append(text);
		html.append("</td>");
		html.append("<td FIXWIDTH=95 align=center valign=top>");
		html.append("<button value=\"" + action + "\" action=\"" + bypass + "\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"80\" height=\"25\"/>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");

		return html.toString();
	}

	private void showClassPage(L2Player player, int level)
	{
		ClassId classId = player.getClassId();

		StringBuilder html = new StringBuilder();
		html.append("<table border=0 width=292>");
		html.append(page(new CustomMessage("communityboard.classmaster.current.profession", player).addString(DifferentMethods.htmlClassNameNonClient(player, player.getClassId().getId()).toString())));
		html.append("</table>");

		for(ClassId cid : ClassId.VALUES)
		{
			if(cid != ClassId.inspector && cid != ClassId.judicator && cid.childOf(classId) && (level == 40 && cid.getLevel() == 2 || level == 80 && cid.getLevel() == 4))
			{
				html.append("<table border=0 cellspacing=0 cellpadding=0>");
				html.append("<tr>");
				html.append("<td width=292><center><img src=\"l2ui.squaregray\" width=\"220\" height=\"1\"></center></td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("<table border=0 cellspacing=4 cellpadding=3>");
				html.append("<tr>");
				html.append("<td FIXWIDTH=50 align=right valign=top><img src=\"icon.etc_royal_membership_i00\" width=32 height=32></td>");
				html.append("<td FIXWIDTH=176 align=left valign=top>");
				html.append("<font color=\"0099FF\">" + DifferentMethods.htmlClassNameNonClient(player, cid.getId()) + ".</font>&nbsp;<br1>"+(cid.getLevel()-1)+"-я профессия.");
				html.append("</td>");
				html.append("<td FIXWIDTH=95 align=center valign=top>");
				html.append("<button value=\"" + (new CustomMessage("communityboard.classmaster.change", player)) + "\" action=\"bypass -h scripts_vidak.VidakService:set_class " + cid.getId() + " " + level + "\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"95\" height=\"25\"/>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
			}
		}

		String content = Files.read("data/scripts/vidak/classmanager.htm", player);
		content = content.replace("%classmaster%", html.toString());
		show123(content, player);
	}

	private void giveAllSkills(L2Player player)
	{
		int unLearnable = 0;
		int skillCounter = 0;
		GArray<L2SkillLearn> skills = player.getAvailableSkills(player.getClassId());
		while(skills.size() > unLearnable)
		{
			unLearnable = 0;
			for(L2SkillLearn s : skills)
			{
				L2Skill sk = SkillTable.getInstance().getInfo(s.id, s.skillLevel);
				if(sk == null || !sk.getCanLearn(player.getClassId()) || s.getMinLevel() > ConfigValue.AutoLearnSkillsMaxLevel || (s.getItemId() > 0 && !ConfigValue.AutoLearnForgottenSkills))
				{
					unLearnable++;
					continue;
				}
				if(player.getSkillLevel(sk.getId()) == -1)
					skillCounter++;
				player.addSkill(sk, true);
				s.deleteSkills(player);
			}
			skills = player.getAvailableSkills(player.getClassId());
		}
		player.sendPacket(new SkillList(player));
	}

	public L2ItemInstance create_item(L2Player player, int item_id, long count)
	{
		L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), ItemTemplates.getInstance().getTemplate(item_id), false);
		item.setEnchantLevel(0);
		item.setCount(count);
		PcInventory inv = player.getInventory();
		synchronized(inv)
		{
			inv.addItem(item);
			player.sendPacket(SystemMessage.obtainItems(item));
			player.tryEqupUneqipItem(item);
		}
		return item;
	}

	public void OnPlayerEnter(L2Player player)
	{
		if(ConfigValue.VidakSystem && player != null)
		{
			int type = player.getVarInt("VidakSystem", ConfigValue.VidakSystemType);
			
			//if(type%10 < 4)
			//	player.setReflection(new Reflection("CharCreateOnInstance"));
				//player.teleToLocation(pos[0], pos[1], pos[2]);
			int level = type-type%10;
			if(type == 0 && player.getLevel() < 40) // Выбор уровня.
			{
				player.is_block = true;
				player.setFlying(true); // хак позволяющий сделать логаут
				show123(Files.read("data/scripts/vidak/vidak_service.htm", player), player);
			}
			else if(type%10 == 1) // Выбор класса.
			{
				player.is_block = true;
				player.setFlying(true); // хак позволяющий сделать логаут
				showClassPage(player, level);
			}
			else if(type%10 == 2) // Выбор брони.
			{
				player.is_block = true;
				player.setFlying(true); // хак позволяющий сделать логаут
				show123(Files.read("data/scripts/vidak/vidak_service_armor"+(player.getRace() == Race.kamael ? "_kamael" : "")+"_"+level+".htm", player), player);
			}
			else if(type%10 == 3) // Выбор оружия.
			{
				player.is_block = true;
				player.setFlying(true); // хак позволяющий сделать логаут
				show123(Files.read("data/scripts/vidak/vidak_service_weapon_"+level+".htm", player), player);
			}
			else if(type%10 == 4) // Первое предложение привязки
			{
				player.is_block = true;
				player.setFlying(true); // хак позволяющий сделать логаут
				
				player.setVar("VidakSystem", String.valueOf(level+5));
				show123(Files.read("data/scripts/vidak/vidak_service_hwid.htm", player), player);
			}
			else if(type%10 == 5) // Второе предложение привязки
			{
				//player.is_block = true;
				//player.setFlying(true); // хак позволяющий сделать логаут
				
				player.setVar("VidakSystem", String.valueOf(level+6));
				show123(Files.read("data/scripts/vidak/vidak_service_hwid.htm", player), player);
			}
			/*else if(type%10 == 6) // Автоматическая привязка
			{
				//player.is_block = true;
				//player.setFlying(true); // хак позволяющий сделать логаут
				player.is_block = false;
				player.setFlying(false);

				player.addAccLock(player.getHWIDs());
				player.sendMessage("Ваш аккант был привязан автоматически.");

				player.setVar("VidakSystem", String.valueOf(level+7));
				show_welcome(player);
				
				//show123(Files.read("data/scripts/vidak/vidak_service_hwid.htm", player), player);
			}*/
			else
			{
				show_welcome(player);
				//player.is_block = false;
				//player.setFlying(false);
			}
			/*else
			{
				//player.is_block = true;
				//player.setFlying(true); // хак позволяющий сделать логаут
				show123(Files.read("data/scripts/vidak/vidak_service_ok.htm", player), player);
			}*/
		}
	}

	public void show_welcome(L2Player player)
	{
		if(ConfigValue.ShowHTMLWelcome && player.getClan() == null)
		{
			String welcomePath = "data/html/welcome.htm";
			File mainText = new File(ConfigValue.DatapackRoot, welcomePath); // Return the pathfile of the HTML file
			if(mainText.exists())
				player.sendPacket(new NpcHtmlMessage(1).setFile(welcomePath));
		}
	}

	public void show_html(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		show123(Files.read("data/scripts/vidak/"+param[0]+".htm", player), player);
	}

	public void show123(String html, L2Player player)
	{
		String type = "";
		switch(player.getRace())
		{
			case darkelf:
				if(player.getSex() == 1)
					type = "delf_f";
				else
					type = "delf_m";
				break;
			case elf:
				if(player.getSex() == 1)
					type = "elf_f";
				else
					type = "elf_m";
				break;
			case dwarf:
				if(player.getSex() == 1)
					type = "gnom_f";
				else
					type = "gnom_m";
				break;
			case human:
				if(player.getSex() == 1)
				{
					if(player.getClassId().isMage())
						type = "magik_f";
					else
						type = "human_f";
				}
				else
				{
					if(player.getClassId().isMage())
						type = "magik_m";
					else
						type = "human_m";
				}
				break;
			case kamael:
				if(player.getSex() == 1)
					type = "kama_f";
				else
					type = "kama_m";
				break;
			case orc:
				if(player.getSex() == 1)
				{
					if(player.getClassId().isMage())
						type = "saman_f";
					else
						type = "ork_f";
				}
				else
				{
					if(player.getClassId().isMage())
						type = "saman_m";
					else
						type = "ork_m";
				}
				break;
		}

		html = html.replace("%src_race%", "icons."+type);
		player.cleanBypasses(false, true);
		html = player.encodeBypasses(html, false, true);
		player.sendPacket(new TutorialShowHtml(html));
	}

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}
