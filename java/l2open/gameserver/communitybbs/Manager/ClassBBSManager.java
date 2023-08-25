package l2open.gameserver.communitybbs.Manager;

import l2open.config.*;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2SkillLearn;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.Files;
import l2open.util.Util;

import java.util.StringTokenizer;

public class ClassBBSManager extends BaseBBSManager
{
	private static ClassBBSManager _Instance = null;

	public static ClassBBSManager getInstance()
	{
		if (_Instance == null)
			_Instance = new ClassBBSManager();
		return _Instance;
	}

	private static boolean jobLevelContains(int lvl)
	{
		switch(lvl)
		{
			case 1:
				if(ConfigValue.AllowClassMasters[0] != 0)
					return true;
				return false;
			case 2:
				if(ConfigValue.AllowClassMasters[1] != 0)
					return true;
				return false;
			case 3:
				if(ConfigValue.AllowClassMasters[2] != 0)
					return true;
				return false;
		}
		return false;
	}
	public void parsecmd(String command, L2Player activeChar)
	{
		if(activeChar.getEventMaster() != null && activeChar.getEventMaster().blockBbs())
			return;
		activeChar.getAI().clearNextAction();
		ClassId classId = activeChar.getClassId();
		int jobLevel = classId.getLevel();
		int level = activeChar.getLevel();
		StringBuilder html = new StringBuilder("");
		html.append("<br>");
		html.append("<table width=445>");
		html.append("<tr><td>");
		if(ConfigValue.AllowClassMasters[0] == 0 && ConfigValue.AllowClassMasters[1] == 0 && ConfigValue.AllowClassMasters[2] == 0)
			jobLevel = 4;

		if(level >= 20 && jobLevel == 1 || level >= 40 && jobLevel == 2 || level >= ConfigValue.JobLevel3 && jobLevel == 3 && jobLevelContains(jobLevel))
		{
			L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.ClassMastersPriceItem[jobLevel-1]);
			html.append("Вы должны заплатить: <font color=\"LEVEL\">");
			html.append(Util.formatAdena(ConfigValue.ClassMastersPrice[jobLevel-1]) + "</font> <font color=\"LEVEL\">" + item.getName() + "</font> для смены профессии<br>");
			html.append("<center><table width=445><tr>");
			for (ClassId cid : ClassId.values())
			{
				if (cid == ClassId.inspector)
					continue;
				if (cid.childOf(classId) && cid.level() == classId.level() + 1)
					html.append("<td><center><button value=\"" + cid.name() + "\" action=\"bypass -h _bbsclass;change_class;" + cid.getId() + ";" + ConfigValue.ClassMastersPrice[jobLevel-1] + "\" width=150 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center></td>");
			}
			html.append("</tr></table></center>");
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");
		}
		else
		{
			switch (jobLevel)
			{
				case 1:
					html.append("Приветствую, <font color=F2C202>" + activeChar.getName() + "</font> . Ваша текущая профессия <font color=F2C202>" + activeChar.getClassId().name() + "</font><br>");
					html.append("Для того, что бы сменить вашу профессию, вы должны достичь: <font color=F2C202>20-го уровня.</font><br>");
					html.append("Для активации сабклассов вы должны достичь <font color=F2C202>"+ConfigValue.NoblessSellSubLevel+"-го уровня.</font><br>");
					html.append("Что бы стать дворянином, вы должны прокачать сабкласс до <font color=F2C202>"+ConfigValue.JobLevel3+"-го уровня.</font><br>");
					break;
				case 2:
					html.append("Приветствую, <font color=F2C202>" + activeChar.getName() + "</font> . Ваша текущая профессия <font color=F2C202>" + activeChar.getClassId().name() + "</font><br>");
					html.append("Для того, что бы сменить вашу профессию, вы должны достичь: <font color=F2C202>40-го уровня.</font><br>");
					html.append("Для активации сабклассов вы должны достичь <font color=F2C202>"+ConfigValue.NoblessSellSubLevel+"-го уровня.</font><br>");
					html.append("Что бы стать дворянином, вы должны прокачать сабкласс до <font color=F2C202>"+ConfigValue.JobLevel3+"-го уровня.</font><br>");
					break;
				case 3:
					html.append("Приветствую, <font color=F2C202>" + activeChar.getName() + "</font> . Ваша текущая профессия <font color=F2C202>" + activeChar.getClassId().name() + "</font><br>");
					html.append("Для того, что бы сменить вашу профессию, вы должны достичь: <font color=F2C202>"+ConfigValue.JobLevel3+"-го уровня.</font><br>");
					html.append("Для активации сабклассов вы должны достичь <font color=F2C202>"+ConfigValue.NoblessSellSubLevel+"-го уровня.</font><br>");
					html.append("Что бы стать дворянином, вы должны прокачать сабкласс до <font color=F2C202>"+ConfigValue.JobLevel3+"-го уровня</font><br>");
					break;
				case 4:
					html.append("Приветствую, <font color=F2C202>" + activeChar.getName() + "</font> . Ваша текущая профессия <font color=F2C202>" + activeChar.getClassId().name() + "</font><br>");
					html.append("Для вас больше нет доступных профессий, либо Класс-мастер в данный момент не доступен.<br>");
					if(level < ConfigValue.NoblessSellSubLevel)
						break;
					html.append("Вы достигли <font color=F2C202>"+ConfigValue.NoblessSellSubLevel+"-го уровня</font>, активация сабклассов теперь доступна.<br>");
					if (!activeChar.isNoble())
						html.append("Вы можете получить дворянство. Посетите раздел 'Магазин'.<br>");
					else
						html.append("Вы уже дворянин. Получение дворянства более не доступно.<br>");
					break;
			}

		}

		String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "100.htm", activeChar);
		content = content.replace("%classmaster%", html.toString());
		if(jobLevel > 0 && jobLevel < 4)
			content = content.replace("%classpice%", String.valueOf(ConfigValue.ClassMastersPrice[jobLevel-1]));
		separateAndSend(content, activeChar);

		if (command.startsWith("_bbsclass;change_class;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			short val = Short.parseShort(st.nextToken());
			int price = Integer.parseInt(st.nextToken());
			if (price >= 0 && activeChar.getAdena() >= price)
			{
				if(price > 0)
					activeChar.reduceAdena(price, true);
				changeClass(activeChar, val);
				if(ConfigValue.GiveAllSkillsForClassUp)
					giveAllSkills(activeChar);
				parsecmd("_bbsclass;", activeChar);
			}
			else if(jobLevel > 0 && jobLevel < 4)
				activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_DO_NOT_HAVE_ENOUGH_ADENA));
		}
	}

	private void changeClass(L2Player player, int val)
	{
		player.getAI().clearNextAction();
		if (player.getClassId().getLevel() == 3)
			player.sendPacket(Msg.YOU_HAVE_COMPLETED_THE_QUEST_FOR_3RD_OCCUPATION_CHANGE_AND_MOVED_TO_ANOTHER_CLASS_CONGRATULATIONS);
		else
			player.sendPacket(Msg.CONGRATULATIONS_YOU_HAVE_TRANSFERRED_TO_A_NEW_CLASS);

		player.setClassId(val, false);
		if(player.getClassId().getLevel() == 4)
			ClassBBSManagerAddReward(player);
		player.broadcastUserInfo(true);
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

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{
	}

	public static void ClassBBSManagerAddReward(L2Player player)
	{
		if(ConfigValue.ThirdClassListToReward.length > 0 && player.getActiveClass().isBase())
		{
			int class_id = player.class_id();
			for(int i=0;i<ConfigValue.ThirdClassListToReward.length;i++)
			{
				if(class_id == ConfigValue.ThirdClassListToReward[i])
				{
					player.getInventory().addItem(ConfigValue.ThirdClassListToRewardId[i], ConfigValue.ThirdClassListToRewardCount[i]);
					player.sendPacket(SystemMessage.obtainItems(ConfigValue.ThirdClassListToRewardId[i], ConfigValue.ThirdClassListToRewardCount[i], 0));
					break;
				}
			}
		}
	}
}