package services.SubClass;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2SubClass;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2SkillLearn;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.base.PlayerClass;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.util.Util;
import l2open.util.GArray;
import l2open.util.Files;
import l2open.util.РазноеГовно;

public class SubClass extends Functions implements ScriptFile
{
	public void add()
	{
		L2Player player = (L2Player) getSelf();

		if(!ConfigValue.CarrerSubAdd)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}
		
		if(!checkCondition(player))
			return;

		String html = Files.read("data/scripts/services/SubClass/add.htm", player);

		Set<PlayerClass> sub = getAvailableSubClasses(player, true);
		String content = "";

		if(sub != null && !sub.isEmpty())
		{
			for(PlayerClass newClass : sub)
			{
				content += "<button value=\"" + format(newClass) + "\" action=\"bypass -h scripts_services.SubClass.SubClass:do_add " + newClass.ordinal() + "\" width=200 height=29 back=L2UI_CT1.OlympiadWnd_DF_Info_Down fore=L2UI_CT1.OlympiadWnd_DF_Info>";
			}
		}
		else
		{
			player.sendMessage(new CustomMessage("services.SubClass.add.empty", player));
			return;
		}

		html = html.replace("<?content?>", content);
		show(html, player);
	}

	public void do_add(String[] param)
	{
		L2Player player = (L2Player) getSelf();

		if(!ConfigValue.CarrerSubAdd)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}

		if(!checkCondition(player))
			return;

		int id = Integer.parseInt(param[0]);
		Map<Integer, L2SubClass> list = player.getSubClasses();

		if(player.getLevel() < ConfigValue.AltLevelToGetSubclass)
		{
			player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2VillageMasterInstance.NoSubBeforeLevel", player).addNumber(ConfigValue.AltLevelToGetSubclass));
			return;
		}

		if(!list.isEmpty())
		{
			for(L2SubClass subClass : list.values())
			{
				if(subClass.getLevel() < ConfigValue.AltLevelToGetSubclass)
				{
					player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2VillageMasterInstance.NoSubBeforeLevel", player).addNumber(ConfigValue.AltLevelToGetSubclass));
					return;
				}
			}
		}

		if(!ConfigValue.AltAllowSubClassWithoutQuest && !list.isEmpty() && list.size() <= 1 && ConfigValue.CarrerSubAddPrice <= 0)
		{
			if(player.isQuestCompleted("_234_FatesWhisper"))
			{
				if(player.getRace() == Race.kamael)
				{
					if(!player.isQuestCompleted("_236_SeedsOfChaos"))
					{
						player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2VillageMasterInstance.QuestSeedsOfChaos", player));
						return;
					}
				}
				else
				{
					if(!player.isQuestCompleted("_235_MimirsElixir"))
					{
						player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2VillageMasterInstance.QuestMimirsElixir", player));
						return;
					}
				}
			}
			else
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2VillageMasterInstance.QuestFatesWhisper", player));
				return;
			}
		}

		if(ConfigValue.CarrerSubAddPrice != 0 && player.getInventory().getCountOf(ConfigValue.CarrerSubAddItem) < ConfigValue.CarrerSubAddPrice)
		{
			int enoughItemCount = (int) (ConfigValue.CarrerSubAddPrice - player.getInventory().getCountOf(ConfigValue.CarrerSubAddItem));
			player.sendMessage(new CustomMessage("communityboard.enoughItemCount", player).addString(Util.formatAdena(enoughItemCount)).addItemName(ConfigValue.CarrerSubAddItem));
			return;
		}

		if(PlayerData.getInstance().addSubClass(player, id, true, 0, false))
		{
			if(ConfigValue.CarrerSubAddPrice != 0)
			{
				player.getInventory().destroyItemByItemId(ConfigValue.CarrerSubAddItem, ConfigValue.CarrerSubAddPrice, true);
				player.sendMessage(new CustomMessage("common.take.item", player).addString(Util.formatAdena(ConfigValue.CarrerSubAddPrice)).addItemName(ConfigValue.CarrerSubAddItem));
			}
			player.sendMessage(new CustomMessage("services.SubClass.add.done", player).addString(DifferentMethods.htmlClassNameNonClient(player, id).toString()));
		}
		else
			player.sendMessage(new CustomMessage("services.SubClass.add.haveall", player));
	}

	public void change()
	{
		L2Player player = (L2Player) getSelf();

		if(!ConfigValue.CarrerSubChange)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}

		if(!checkCondition(player))
			return;

		String html;

		String content = "";

		final int base = player.getBaseClassId();
		Map<Integer, L2SubClass> list = player.getSubClasses();

		if(list.size() < 2)
			html = Files.read("data/scripts/services/SubClass/change_empty.htm", player);
		else
		{
			html = Files.read("data/scripts/services/SubClass/change.htm", player);

			if(base == player.getActiveClassId())
				content += "<button value=\"" + DifferentMethods.htmlClassNameNonClient(player, base) + " (Базовый)\" width=200 height=29 back=L2UI_CT1.OlympiadWnd_DF_Watch_Down fore=L2UI_CT1.OlympiadWnd_DF_Watch><br1>";
			else
			{
				content += "<button value=\"" + DifferentMethods.htmlClassNameNonClient(player, player.getActiveClassId()) + " (Активный)\" width=200 height=29 back=L2UI_CT1.OlympiadWnd_DF_Watch_Down fore=L2UI_CT1.OlympiadWnd_DF_Watch><br1>";
				content += "<button value=\"" + DifferentMethods.htmlClassNameNonClient(player, base) + " (Базовый)\" action=\"bypass -h scripts_services.SubClass.SubClass:do_change " + base + "\" width=200 height=29 back=L2UI_CT1.OlympiadWnd_DF_HeroConfirm_Down fore=L2UI_CT1.OlympiadWnd_DF_HeroConfirm><br1>";				
			}

			for(L2SubClass subClass : list.values())
			{
				if(subClass.isBase())
					continue;
				int sub = subClass.getClassId();

				if(sub != player.getActiveClassId())
					content += "<button value=\"" + DifferentMethods.htmlClassNameNonClient(player, sub) + "\" action=\"bypass -h scripts_services.SubClass.SubClass:do_change " + sub + "\" width=200 height=29 back=L2UI_CT1.OlympiadWnd_DF_Info_Down fore=L2UI_CT1.OlympiadWnd_DF_Info><br1>";
			}
		}
		html = html.replace("<?content?>", content);
		show(html, player);
	}

	public void do_change(String[] param)
	{
		L2Player player = (L2Player) getSelf();

		if(!ConfigValue.CarrerSubChange)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}

		if(!checkCondition(player))
			return;

		int id = Integer.parseInt(param[0]);
		if(DifferentMethods.getPay(player, ConfigValue.CarrerSubChangeItem, ConfigValue.CarrerSubChangePrice, true))
		{
			player.setActiveSubClass(id, true);
			player.sendMessage(new CustomMessage("services.SubClass.change.done", player).addString(DifferentMethods.htmlClassNameNonClient(player, player.getActiveClassId()).toString()));
			return;
		}
	}

	public void cancel()
	{
		L2Player player = (L2Player) getSelf();

		if(!ConfigValue.CarrerSubCancel)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}

		if(!checkCondition(player))
			return;

		String html = Files.read("data/scripts/services/SubClass/cancel.htm", player);

		String content = "";

		Map<Integer, L2SubClass> list = player.getSubClasses();

		for(L2SubClass sub : list.values())
		{
			if(!sub.isBase() && !sub.isBase2())
			{
				content += "<button value=\"" + DifferentMethods.htmlClassNameNonClient(player, sub.getClassId()) + "\" action=\"bypass -h scripts_services.SubClass.SubClass:cancel_choice " + sub.getClassId() + "\" width=200 height=29 back=L2UI_CT1.OlympiadWnd_DF_Info_Down fore=L2UI_CT1.OlympiadWnd_DF_Info><br1>";
			}
		}

		html = html.replace("<?content?>", content);
		show(html, player);
	}

	public void delete()
	{
		L2Player player = (L2Player) getSelf();

		if(!ConfigValue.CarrerSubCancel)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}

		if(!checkCondition(player))
			return;

		String html = Files.read("data/scripts/services/SubClass/cancel.htm", player);

		String content = "";

		Map<Integer, L2SubClass> list = player.getSubClasses();

		for(L2SubClass sub : list.values())
		{
			if(!sub.isBase() && !sub.isBase2())
			{
				content += "<button value=\"" + DifferentMethods.htmlClassNameNonClient(player, sub.getClassId()) + "\" action=\"bypass -h scripts_services.SubClass.SubClass:do_cancel " + sub.getClassId() + " -1\" width=200 height=29 back=L2UI_CT1.OlympiadWnd_DF_Info_Down fore=L2UI_CT1.OlympiadWnd_DF_Info><br1>";
			}
		}

		html = html.replace("<?content?>", content);
		show(html, player);
	}

	public void cancel_choice(String[] param)
	{
		L2Player player = (L2Player) getSelf();

		if(!ConfigValue.CarrerSubCancel)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}

		if(!checkCondition(player))
			return;

		int id = Integer.parseInt(param[0]);
		String html = Files.read("data/scripts/services/SubClass/cancel_choice.htm", player);

		String content = "";

		Set<PlayerClass> sub = getAvailableSubClasses(player, true);

		if(!sub.isEmpty())
			for(PlayerClass subClass : sub)
				content += "<button value=\"" + DifferentMethods.htmlClassNameNonClient(player, subClass.ordinal()) + "\" action=\"bypass -h scripts_services.SubClass.SubClass:do_cancel " + id + " " + subClass.ordinal() + "\" width=200 height=29 back=L2UI_CT1.OlympiadWnd_DF_Info_Down fore=L2UI_CT1.OlympiadWnd_DF_Info><br1>";
		else
		{
			player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime", player));
			return;
		}
		html = html.replace("<?content?>", content);
		show(html, player);
	}

	public void do_cancel(String[] param)
	{
		L2Player player = (L2Player) getSelf();

		if(!ConfigValue.CarrerSubCancel)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}

		if(!checkCondition(player))
			return;

		int oldid = Integer.parseInt(param[0]);
		int newid = Integer.parseInt(param[1]);

		if(DifferentMethods.getPay(player, ConfigValue.CarrerSubCancelItem, ConfigValue.CarrerSubCancelPrice, true))
		{
			if(PlayerData.getInstance().modifySubClass(player, oldid, newid))
			{
				if(newid > -1)
					player.sendMessage(new CustomMessage("services.SubClass.cancel.done", player).addString(DifferentMethods.htmlClassNameNonClient(player, newid).toString()));
				else
					player.sendMessage(new CustomMessage("services.SubClass.cancel.delete", player));
				return;
			}
			else
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded", player));
				return;
			}
		}
	}

	private Set<PlayerClass> getAvailableSubClasses(L2Player player, boolean isNew)
	{
		final int charClassId = player.getBaseClassId();

		PlayerClass currClass = PlayerClass.values()[charClassId];

		Set<PlayerClass> availSubs = currClass.getAvailableSubclasses();
		if(availSubs == null)
			return Collections.emptySet();

		availSubs.remove(currClass);

		for(PlayerClass availSub : availSubs)
		{
			for(L2SubClass subClass : player.getSubClasses().values())
			{
				if(availSub.ordinal() == subClass.getClassId())
				{
					availSubs.remove(availSub);
					continue;
				}

				ClassId parent = ClassId.VALUES[availSub.ordinal()].getParent(player.getSex());
				if(parent != null && parent.getId() == subClass.getClassId())
				{
					availSubs.remove(availSub);
					continue;
				}

				ClassId subParent = ClassId.VALUES[subClass.getClassId()].getParent(player.getSex());
				if(subParent != null && subParent.getId() == availSub.ordinal())
					availSubs.remove(availSub);
			}

			if(availSub.isOfRace(Race.kamael))
			{
				if((currClass == PlayerClass.MaleSoulHound || currClass == PlayerClass.FemaleSoulHound || currClass == PlayerClass.FemaleSoulbreaker || currClass == PlayerClass.MaleSoulbreaker) && (availSub == PlayerClass.FemaleSoulbreaker || availSub == PlayerClass.MaleSoulbreaker))
					availSubs.remove(availSub);

				if(currClass == PlayerClass.Berserker || currClass == PlayerClass.Doombringer || currClass == PlayerClass.Arbalester || currClass == PlayerClass.Trickster)
					if(player.getSex() == 1 && availSub == PlayerClass.MaleSoulbreaker || player.getSex() == 0 && availSub == PlayerClass.FemaleSoulbreaker)
						availSubs.remove(availSub);

				if(availSub == PlayerClass.Inspector && player.getSubClasses().size() < (isNew ? 3 : 4))
					availSubs.remove(availSub);
			}
		}
		return availSubs;
	}

	private String format(PlayerClass className)
	{
		String classNameStr = className.toString();
		char[] charArray = classNameStr.toCharArray();

		for(int i = 1; i < charArray.length; i++)
			if(Character.isUpperCase(charArray[i]))
				classNameStr = classNameStr.substring(0, i) + " " + classNameStr.substring(i);

		return classNameStr;
	}

	private boolean checkCondition(L2Player player)
	{
		player.getAI().clearNextAction();
		ClassId classId = player.getClassId();
		int jobLevel = classId.getLevel();
		if(player.getLevel() < 40)
		{
			player.sendMessage("You must be level 40 or more to operate with your sub-classes.");
			return false;
		}
		else if(jobLevel < 3)
		{
			player.sendMessage("You must be level 40 or more to operate with your sub-classes.");
			return false;
		}
		else if(player.isDead() || player.isAlikeDead() || player.isCastingNow() || player.isAttackingNow())
		{
			player.sendMessage(player.isLangRus() ? "Сменить саб-класс в вашем состоянии невозможно" : "You can`t change sub-class in this condition");
			return false;
		}
		else if(player.getPet() != null)
		{
			player.sendPacket(new SystemMessage(SystemMessage.A_SUB_CLASS_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SERVITOR_OR_PET_IS_SUMMONED));
			return false;
		}
		else if(player.isActionsDisabled() || player.getTransformation() != 0)
		{
			player.sendPacket(new SystemMessage(SystemMessage.SUB_CLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE));
			return false;
		}
		else if(player.getWeightPenalty() >= 3)
		{
			player.sendPacket(new SystemMessage(SystemMessage.A_SUB_CLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT));
			return false;
		}
		else if(player.getInventoryLimit() * 0.8 < player.getInventory().getSize())
		{
			player.sendPacket(new SystemMessage(SystemMessage.A_SUB_CLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT));
			return false;
		}
		else if(player.isInCombat())
		{
			player.sendMessage(player.isLangRus() ? "Сменить саб-класс в боевом режиме нельзя" : "You can`t change sub-class in fight mode");
			return false;
		}
		else if(player.isInZone(L2Zone.ZoneType.battle_zone) || player.isInZone(L2Zone.ZoneType.no_escape) || player.isInZone(L2Zone.ZoneType.epic) || player.isInZone(L2Zone.ZoneType.Siege) || player.isInZone(L2Zone.ZoneType.RESIDENCE) || player.getVar("jailed") != null)
		{
			player.sendMessage(player.isLangRus() ? "Нельзя сменить саб-класс в данной локации" : "You can`t change sub-class in this location");
			return false;
		}
		else if(player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped())
		{
			player.sendMessage(player.isLangRus() ? "Сменить саб-класс со Знаменем невозможно" : "You can`t change sub-class with handing the flag");
			return false;
		}
		else if(ConfigValue.EnableOlympiad && Olympiad.isRegisteredInComp(player) || player.isInOlympiadMode())
		{
			player.sendMessage(player.isLangRus() ? "Во время Олимпиады сменить саб-класс невозможно" : "You can`t change sub-class during the Olympiad running");
			return false;
		}
		else if(player.getReflectionId() != 0)
		{
			player.sendMessage(player.isLangRus() ? "Вы не можете сменить саб-класс, находясь во временной зоне" : "You can`t change sub-class being in time zone");
			return false;
		}
		else if(player.isInDuel() || player.getTeam() != 0)
		{
			player.sendMessage(player.isLangRus() ? "Во время дуэли сменить саб-класс невозможно" : "You can`t change sub-class during a duel");
			return false;
		}
		else if(player.isInCombat() || player.getPvpFlag() != 0)
		{
			player.sendMessage(player.isLangRus() ? "Во время боя сменить саб-класс невозможно" : "You can`t change sub-class during the fight");
			return false;
		}
		else if(player.isOnSiegeField() || player.isInZoneBattle())
		{
			player.sendMessage(player.isLangRus() ? "Во время полномасштабных сражений - осад крепостей, замков, холлов клана, сменить саб-класс невозможно" : "You can`t change sub-class in siege battle");
			return false;
		}
		else if(player.isFlying())
		{
			player.sendMessage(player.isLangRus() ? "Во время полета сменить саб-класс невозможно" : "You can`t change sub-class during the flight");
			return false;
		}
		else if(player.isInWater() || player.isInVehicle())
		{
			player.sendMessage(player.isLangRus() ? "Вы не можете сменить саб-класс, находясь в воде" : "You can`t change sub-class being in water");
			return false;
		}
		return true;
	}

	public void change_class(String[] param)
	{
		L2Player player = (L2Player) getSelf();

		if(player == null && param.length != 2)
			return;

		player.getAI().clearNextAction();
		int class_id = Integer.parseInt(param[0]);
		int price_id = Integer.parseInt(param[1]);
		changeClass(player, class_id, price_id);
		if(ConfigValue.GiveAllSkillsForClassUp)
			giveAllSkills(player);
	}

	private void changeClass(L2Player player, int class_id, int price_id)
	{
		if(player == null)
			return;

		int item = ConfigValue.ClassMastersPriceItem[price_id];
		long count = ConfigValue.ClassMastersPrice[price_id];

		if(DifferentMethods.getPay(player, item, count, true))
		{
			player.setClassId(class_id, false);
			player.updateStats();
			player.broadcastUserInfo(true);

			if(player.getClassId().getLevel() == 3)
				player.sendPacket(Msg.YOU_HAVE_COMPLETED_THE_QUEST_FOR_3RD_OCCUPATION_CHANGE_AND_MOVED_TO_ANOTHER_CLASS_CONGRATULATIONS);
			else
				player.sendPacket(Msg.CONGRATULATIONS_YOU_HAVE_TRANSFERRED_TO_A_NEW_CLASS);
		}

		РазноеГовно.incLevelClassMaster(player);
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

	@Override
	public void onLoad()
	{
		_log.info("Loaded Service: SubClass sell");
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}
