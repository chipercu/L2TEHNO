package communityboard;

import java.util.StringTokenizer;
//import java.util.logging.Logger;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.communitybbs.Manager.ClassBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.tables.SkillTable;
import l2open.util.*;

import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.SocialAction;

/**
 * @author L2CCCP
 * @coauthor Diagod
 */
public class CommunityBoardClassMaster extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	//private static final Logger _log = Logger.getLogger(CommunityBoardClassMaster.class.getName());

	private static enum Commands
	{
		_bbscarrer
	}

	@Override
	public void parsecmd(String bypass, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(player.is_block || player.isInEvent() > 0)
			return;
		if(!ConfigValue.CarrerAllow)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}
		else if(bypass.equals("_bbscarrer"))
			showClassPage(player);
		else if(bypass.startsWith("_bbscarrer:change_class:"))
		{
			StringTokenizer selectedClass = new StringTokenizer(bypass, ":");
			selectedClass.nextToken();
			selectedClass.nextToken();
			int classID = Integer.parseInt(selectedClass.nextToken());
			int id = Integer.parseInt(selectedClass.nextToken());
			int items = Integer.parseInt(selectedClass.nextToken());
			changeClass(player, classID, id, items);
			if(ConfigValue.GiveAllSkillsForClassUp)
				giveAllSkills(player);
		}
		else
			separateAndSend(DifferentMethods.getErrorHtml(player, bypass), player);
	}

	private String page(CustomMessage text)
	{
		StringBuilder html = new StringBuilder();

		html.append("<tr>");
		html.append("<td WIDTH=20 align=left valign=top></td>");
		html.append("<td WIDTH=690 align=left valign=top>");
		html.append(text);
		html.append("</td>");
		html.append("</tr>");

		return html.toString();
	}

	private String block(String icon, String text, CustomMessage action, String bypass)
	{
		StringBuilder html = new StringBuilder();

		html.append("<table border=0 cellspacing=0 cellpadding=0>");
		html.append("<tr>");
		html.append("<td width=720><img src=\"l2ui.squaregray\" width=\"720\" height=\"1\"></td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("<table border=0 cellspacing=4 cellpadding=3>");
		html.append("<tr>");
		html.append("<td FIXWIDTH=50 align=right valign=top><img src=\"" + icon + "\" width=32 height=32></td>");
		html.append("<td FIXWIDTH=576 align=left valign=top>");
		html.append(text);
		html.append("</td>");
		html.append("<td FIXWIDTH=95 align=center valign=top>");
		html.append("<button value=\"" + action + "\" action=\"" + bypass + "\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"80\" height=\"25\"/>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");

		return html.toString();
	}

	private static long[][] getPriceValue(int lvl)
	{
		switch(lvl)
		{
			case 1:
				return ConfigValue.CarrerFirstItemPrice;
			case 2:
				return ConfigValue.CarrerSecondItemPrice;
			case 3:
				return ConfigValue.CarrerThirdItemPrice;
		}
		return null;
	}

	private static boolean jobLevelContains(int lvl)
	{
		switch(lvl)
		{
			case 1:
				if(ConfigValue.CarrerList[0] != 0)
					return true;
				return false;
			case 2:
				if(ConfigValue.CarrerList[1] != 0)
					return true;
				return false;
			case 3:
				if(ConfigValue.CarrerList[2] != 0)
					return true;
				return false;
			case 10:
				if(ConfigValue.CarrerFirstItemPrice.length != 0)
					return true;
				return false;
			case 20:
				if(ConfigValue.CarrerSecondItemPrice.length != 0)
					return true;
				return false;
			case 30:
				if(ConfigValue.CarrerThirdItemPrice.length != 0)
					return true;
				return false;

		}
		return false;
	}

	private void showClassPage(L2Player player)
	{
		ClassId classId = player.getClassId();
		int jobLevel = classId.getLevel();
		int level = player.getLevel();

		StringBuilder html = new StringBuilder();
		html.append("<table width=755>");
		html.append(page(new CustomMessage("communityboard.classmaster.welcome", player).addString(player.getName())));
		html.append(page(new CustomMessage("communityboard.classmaster.current.profession", player).addString(DifferentMethods.htmlClassNameNonClient(player, player.getClassId().getId()).toString())));
		html.append("</table>");

		if(ConfigValue.CarrerList[0] == 0 && ConfigValue.CarrerList[1] == 0 && ConfigValue.CarrerList[2] == 0 && ConfigValue.CarrerFirstItemPrice.length == 0 && ConfigValue.CarrerSecondItemPrice.length == 0 && ConfigValue.CarrerThirdItemPrice.length == 0)
			jobLevel = 4;

		if((level >= 20 && jobLevel == 1 || level >= 40 && jobLevel == 2 || level >= ConfigValue.JobLevel3 && jobLevel == 3) && (jobLevelContains(jobLevel) || jobLevelContains(jobLevel*10)))
		{
			int id = jobLevel - 1;
			for(ClassId cid : ClassId.VALUES)
			{
				if(cid == ClassId.inspector)
					continue;
				if(cid.childOf(classId) && cid.level() == classId.level() + 1)
				{
					html.append("<table border=0 cellspacing=0 cellpadding=0>");
					html.append("<tr>");
					html.append("<td width=755><center><img src=\"l2ui.squaregray\" width=\"720\" height=\"1\"></center></td>");
					html.append("</tr>");
					html.append("</table>");
					html.append("<table border=0 cellspacing=4 cellpadding=3>");
					html.append("<tr>");
					html.append("<td FIXWIDTH=50 align=right valign=top><img src=\"icon.etc_royal_membership_i00\" width=32 height=32></td>");
					html.append("<td FIXWIDTH=576 align=left valign=top>");
					html.append("<font color=\"0099FF\">" + DifferentMethods.htmlClassNameNonClient(player, cid.getId()) + ".</font>&nbsp;<br1>›&nbsp;");

					long CarrerFirstItemPrice[][] = getPriceValue(jobLevel);
					if(CarrerFirstItemPrice.length != 0)
					{
						html.append("Необходимые предметы: ");
						for(int i=0;i<CarrerFirstItemPrice.length;i++)
						{
							html.append(Util.formatAdena(CarrerFirstItemPrice[i][1])).append(" ").append(DifferentMethods.getItemName((int)CarrerFirstItemPrice[i][0]));
							if(i != CarrerFirstItemPrice.length-1)
								html.append(", ");
						}
					}
					else
						html.append(new CustomMessage("scripts.services.cost", player).addString(Util.formatAdena(ConfigValue.CarrerPrice[id])).addString(DifferentMethods.getItemName(ConfigValue.CarrerItem[id])));

					if(ConfigValue.CarrerSecondItem.length > 0)
						html.append(" или " + new CustomMessage("scripts.services.cost", player).addString(Util.formatAdena(ConfigValue.CarrerSecondPrice[id])).addString(DifferentMethods.getItemName(ConfigValue.CarrerSecondItem[id])));

					html.append("</td>");
					html.append("<td FIXWIDTH=95 align=center valign=top>");
					html.append("<button value=\"" + (ConfigValue.CarrerSecondItem.length > 0 ? DifferentMethods.getItemName(ConfigValue.CarrerItem[id]) : new CustomMessage("communityboard.classmaster.change", player)) + "\" action=\"bypass -h _bbscarrer:change_class:" + cid.getId() + ":" + (id) + ":0\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"95\" height=\"25\"/>");
					html.append("</td>");

					if(ConfigValue.CarrerSecondItem.length > 0)
					{
						html.append("<td FIXWIDTH=95 align=center valign=top>");
						html.append("<button value=\"" + DifferentMethods.getItemName(ConfigValue.CarrerSecondItem[id]) + "\" action=\"bypass -h _bbscarrer:change_class:" + cid.getId() + ":" + (id) + ":1\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"95\" height=\"25\"/>");
						html.append("</td>");
					}
					html.append("</tr>");
					html.append("</table>");
				}
			}
		}
		else
			switch(jobLevel)
			{
				case 1:
					html.append("<table width=755>");
					html.append(page(new CustomMessage("communityboard.classmaster.profession.need", player).addNumber(20)));
					if(ConfigValue.CarrerSubAdd)
						html.append(page(new CustomMessage("communityboard.classmaster.subclass.need", player)));
					if(ConfigValue.CarrerBuyNobless)
						html.append(page(new CustomMessage("communityboard.classmaster.noblesse.need", player)));
					html.append("</table>");
					break;
				case 2:
					html.append("<table width=755>");
					html.append(page(new CustomMessage("communityboard.classmaster.profession.need", player).addNumber(40)));
					if(ConfigValue.CarrerSubAdd)
						html.append(page(new CustomMessage("communityboard.classmaster.subclass.need", player)));
					if(ConfigValue.CarrerBuyNobless)
						html.append(page(new CustomMessage("communityboard.classmaster.noblesse.need", player)));
					html.append("</table>");
					break;
				case 3:
					html.append("<table width=755>");
					html.append(page(new CustomMessage("communityboard.classmaster.profession.need", player).addNumber(ConfigValue.JobLevel3)));
					if(ConfigValue.CarrerSubAdd)
						html.append(page(new CustomMessage("communityboard.classmaster.subclass.need", player)));
					if(ConfigValue.CarrerBuyNobless)
						html.append(page(new CustomMessage("communityboard.classmaster.noblesse.need", player)));
					html.append("</table>");
					break;
				case 4:
					if(level >= ConfigValue.NoblessSellSubLevel)
					{
						html.append("<table width=755>");
						html.append(page(new CustomMessage("communityboard.classmaster.subclass.enable", player)));

						if(!player.isNoble() && player.getSubLevel() < ConfigValue.NoblessSellSubLevel)
						{
							html.append(page(new CustomMessage("communityboard.classmaster.noblesse.need", player)));
						}
						else if(!player.isNoble() && player.getSubLevel() >= ConfigValue.NoblessSellSubLevel)
						{
							html.append(page(new CustomMessage("communityboard.classmaster.noblesse.enable", player)));
						}
						else if(player.isNoble())
						{
							html.append(page(new CustomMessage("communityboard.classmaster.isnoblesse", player)));
						}
						html.append("</table>");

						if(ConfigValue.CarrerBuyNobless)
						{
							if(!player.isNoble())
								html.append(block("icon.skill1323", new CustomMessage("communityboard.classmaster.noblesse.buy.info", player) + " <br1>› " + DifferentMethods.getPrice(player, ConfigValue.NoblessSellPrice, ConfigValue.NoblessSellItem), new CustomMessage("communityboard.classmaster.noblesse.buy", player), "bypass -h _bbsscripts; ;services.NoblessSell:get;_bbscarrer"));
							else
								html.append(block("icon.skill1323", new CustomMessage("communityboard.classmaster.noblesse.buy.no", player).toString(), new CustomMessage("common.not.available", player), "bypass -h _bbscarrer"));
						}
					}
					if(ConfigValue.CarrerSubAdd)
						html.append(block("icon.etc_quest_subclass_reward_i00", new CustomMessage("communityboard.classmaster.subclass.add.info", player) + " <br1>› " + DifferentMethods.getPrice(player, ConfigValue.CarrerSubAddPrice, ConfigValue.CarrerSubAddItem), new CustomMessage("communityboard.classmaster.subclass.add", player), "bypass -h _bbsscripts; ;services.SubClass.SubClass:add;_bbscarrer"));

					if(ConfigValue.CarrerSubChange)
						html.append(block("icon.etc_quest_subclass_reward_i00", new CustomMessage("communityboard.classmaster.subclass.change.info", player) + " <br1>› " + DifferentMethods.getPrice(player, ConfigValue.CarrerSubChangePrice, ConfigValue.CarrerSubChangeItem), new CustomMessage("communityboard.classmaster.subclass.change", player), "bypass -h _bbsscripts; ;services.SubClass.SubClass:change;_bbscarrer"));

					if(ConfigValue.CarrerSubCancel)
						html.append(block("icon.etc_quest_subclass_reward_i00", new CustomMessage("communityboard.classmaster.subclass.cancel.info", player) + " <br1>› " + DifferentMethods.getPrice(player, ConfigValue.CarrerSubCancelPrice, ConfigValue.CarrerSubCancelItem), new CustomMessage("communityboard.classmaster.subclass.cancel", player), "bypass -h _bbsscripts; ;services.SubClass.SubClass:cancel;_bbscarrer"));
					break;
			}

		String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "services/classmanager.htm", player);
		content = content.replace("%classmaster%", html.toString());

		ShowBoard.separateAndSend(addCustomReplace(content), player);
	}

	private void changeClass(L2Player player, int classID, int id, int items)
	{
		if(player == null)
			return;

		ClassId class_ = player.getClassId();
		ClassId cid = ClassId.values()[classID];
		
		if(cid.childOf(class_) && cid.level() == class_.level() + 1)
		{
			long CarrerFirstItemPrice[][] = getPriceValue(id+1);
			if(CarrerFirstItemPrice.length != 0)
			{
				boolean enough_item = true;
				for(int i=0;i<CarrerFirstItemPrice.length;i++)
				{
					int item_id = (int)CarrerFirstItemPrice[i][0];
					long item_count = CarrerFirstItemPrice[i][1];

					if(item_count == 0)
						continue;

					if(player.getInventory().getCountOf(item_id) < item_count)
					{
						CustomMessage cm = new CustomMessage("communityboard.enoughItemCount", player).addString(Util.formatAdena(item_count - player.getInventory().getCountOf(item_id))).addItemName(item_id);

						player.sendPacket(new ExShowScreenMessage(cm.toString(), 3000, ScreenMessageAlign.TOP_CENTER, true));
						player.sendMessage(cm);
						enough_item = false;
						break;
					}
				}
				if(enough_item)
				{
					for(int i=0;i<CarrerFirstItemPrice.length;i++)
					{
						int item_id = (int)CarrerFirstItemPrice[i][0];
						long item_count = CarrerFirstItemPrice[i][1];

						player.getInventory().destroyItemByItemId(item_id, item_count, true);

						CustomMessage cm = new CustomMessage("common.take.item", player).addString(Util.formatAdena(item_count)).addItemName(item_id);
						player.sendPacket(new ExShowScreenMessage(cm.toString(), 3000, ScreenMessageAlign.TOP_CENTER, true));
						player.sendMessage(cm);
					}

					player.setClassId(classID, false);
					player.updateStats();
					player.broadcastUserInfo(true);

					if(player.getClassId().getLevel() == 4)
						player.sendPacket(Msg.YOU_HAVE_COMPLETED_THE_QUEST_FOR_3RD_OCCUPATION_CHANGE_AND_MOVED_TO_ANOTHER_CLASS_CONGRATULATIONS);
					else
						player.sendPacket(Msg.CONGRATULATIONS_YOU_HAVE_TRANSFERRED_TO_A_NEW_CLASS);

					if(player.getClassId().getLevel() == 4 && ConfigValue.GiveNobleForThirdProfession)
						becomeNoble(player);
					//if(player.getClassId().getLevel() == 4)
						ClassBBSManager.ClassBBSManagerAddReward(player);
				}
			}
			else
			{
				int item = items == 0 ? ConfigValue.CarrerItem[id] : ConfigValue.CarrerSecondItem[id];
				int count = items == 0 ? ConfigValue.CarrerPrice[id] : ConfigValue.CarrerSecondPrice[id];

				if(DifferentMethods.getPay(player, item, count, true))
				{
					player.setClassId(classID, false);
					player.updateStats();
					player.broadcastUserInfo(true);

					if(player.getClassId().getLevel() == 4)
						player.sendPacket(Msg.YOU_HAVE_COMPLETED_THE_QUEST_FOR_3RD_OCCUPATION_CHANGE_AND_MOVED_TO_ANOTHER_CLASS_CONGRATULATIONS);
					else
						player.sendPacket(Msg.CONGRATULATIONS_YOU_HAVE_TRANSFERRED_TO_A_NEW_CLASS);

					if(player.getClassId().getLevel() == 4 && ConfigValue.GiveNobleForThirdProfession)
						becomeNoble(player);
					//if(player.getClassId().getLevel() == 4)
						ClassBBSManager.ClassBBSManagerAddReward(player);
				}
			}
		}

		showClassPage(player);
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

	public void becomeNoble(L2Player player)
	{
		if(player.isNoble())
			return;
		Quest q = QuestManager.getQuest("_234_FatesWhisper");
		QuestState qs = player.getQuestState(q.getName());
		if(qs != null)
			qs.exitCurrentQuest(true);
		q.newQuestState(player, Quest.COMPLETED);

		if(player.getRace() == Race.kamael)
		{
			q = QuestManager.getQuest("_236_SeedsOfChaos");
			qs = player.getQuestState(q.getName());
			if(qs != null)
				qs.exitCurrentQuest(true);
			q.newQuestState(player, Quest.COMPLETED);
		}
		else
		{
			q = QuestManager.getQuest("_235_MimirsElixir");
			qs = player.getQuestState(q.getName());
			if(qs != null)
				qs.exitCurrentQuest(true);
			q.newQuestState(player, Quest.COMPLETED);
		}

		Olympiad.addNoble(player);
		player.setNoble(true);
		player.updatePledgeClass();
		player.updateNobleSkills();
		player.sendPacket(new SkillList(player));
		player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.VICTORY));
		player.broadcastUserInfo(true);
	}

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
}