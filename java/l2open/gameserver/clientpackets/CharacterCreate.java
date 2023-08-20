package l2open.gameserver.clientpackets;

import java.sql.SQLException;

import l2open.config.ConfigValue;
import l2open.database.L2DatabaseFactory;
import l2open.database.mysql;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.PlayerManager;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2ShortCut;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2SkillLearn;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.CharacterSelectionInfo;
import l2open.gameserver.serverpackets.ExBrPremiumState;
import l2open.gameserver.tables.CharNameTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2PlayerTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Util;

/**

2386=1147-Squire's Pants Нижний доспех / Легкая броня
23=1146-Squire's Shirt Верхний доспех / Легкая броня
2369-Squire's Sword
5588-Tutorial Guide
1101=425-Apprentice's Tunic Верхний доспех / Мантия
1104=461-Apprentice's Stockings Нижний доспех / Мантия
6-Apprentice's Wand Дубина / Одноручное Оружие
10-Кинжал / Одноручное Оружие
2370-Guild Member's Club Дубина / Одноручное Оружие
2368-Training Gloves Кастеты / Двуручное Оружие



*/
public class CharacterCreate extends L2GameClientPacket
{
	// cSdddddddddddd
	private String _name;
	private int _sex;
	private int _classId;
	private int _hairStyle;
	private int _hairColor;
	private int _face;

	@Override
	public void readImpl()
	{
		_name = readS();
		readD(); // race
		_sex = readD();
		_classId = readD();
		readD(); // int
		readD(); // str
		readD(); // con
		readD(); // men
		readD(); // dex
		readD(); // wit
		_hairStyle = readD();
		_hairColor = readD();
		_face = readD();
	}

	@Override
	public void runImpl()
	{
		for(ClassId cid : ClassId.values())
			if(cid.getId() == _classId && cid.getLevel() != 1)
				return;
		if(CharNameTable.getInstance().accountCharNumber(getClient().getLoginName()) >= 8)
		{
			sendPacket(Msg.CharacterCreateFail_REASON_TOO_MANY_CHARACTERS);
			return;
		}
		else if(_name.trim().isEmpty() || !Util.isMatchingRegexp(_name, ConfigValue.CnameTemplate))
		{
			sendPacket(Msg.CharacterCreateFail_REASON_16_ENG_CHARS);
			return;
		}
		else if(CharNameTable.getInstance().doesCharNameExist(_name))
		{
			sendPacket(Msg.CharacterCreateFail_REASON_NAME_ALREADY_EXISTS);
			return;
		}
		else if(!validChar(_sex, _classId, _hairStyle, _hairColor, _face))
		{
			return;
		}

		L2Player newChar = L2Player.create(_classId, (byte) _sex, getClient().getLoginName(), _name, (byte) _hairStyle, (byte) _hairColor, (byte) _face, 0, ConfigValue.GetStartLevel);
		if(newChar == null)
			return;
		newChar.setConnected(false);

		sendPacket(Msg.CharacterCreateSuccess);

		initNewChar(getClient(), newChar);
	}

	private void initNewChar(L2GameClient client, L2Player newChar)
	{
		L2PlayerTemplate template = newChar.getTemplate();

		PlayerData.getInstance().restoreCharSubClasses(newChar);

		if(ConfigValue.StartingAdena > 0)
			newChar.addAdena(ConfigValue.StartingAdena);

		if(ConfigValue.GiveStartPremium)
		{
			if(CharNameTable.getInstance().accountCharNumber(getClient().getLoginName()) <= ConfigValue.GiveStartPremiumCharCount)
			{
				if(ConfigValue.StartPremiumType == 0)
				{
					try
					{
						mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE `accounts` SET `bonus`=?,`bonus_expire`=UNIX_TIMESTAMP()+" + (int) ConfigValue.StartPremiumRate[1] + "*24*60*60 WHERE `login`=?", ConfigValue.StartPremiumRate[0], newChar.getAccountName());
						newChar.setVar("PremiumStart", "-1");
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}
				}
				else if(ConfigValue.StartPremiumType == 1)
				{
					try
					{
						mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "REPLACE INTO bonus(obj_id, account, bonus_name, bonus_value, bonus_expire_time) VALUES ("+newChar.getObjectId()+",'"+newChar.getAccountName()+"','RATE_ALL',"+ConfigValue.StartPremiumRate[0]+",UNIX_TIMESTAMP()+" + (int) ConfigValue.StartPremiumRate[1] + "*24*60*60)");
						newChar.setVar("PremiumStart", "-1");
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}
				}
				
				
			}
		}

		if(ConfigValue.StartingItem.length > 0 && ConfigValue.StartingItem[0] > 0)
		{
			ItemTemplates itemTable = null;
			L2ItemInstance item = null;
			for(int i = 0; i < ConfigValue.StartingItem.length; i++)
			{
				itemTable = ItemTemplates.getInstance();
				item = itemTable.createItem(ConfigValue.StartingItem[i]);
				item.setCount(ConfigValue.StartingItemCount.length > i ? ConfigValue.StartingItemCount[i] : ConfigValue.StartingItemCount[0]);
				item.setEnchantLevel(ConfigValue.StartingItemEnchant.length > i ? ConfigValue.StartingItemEnchant[i] : ConfigValue.StartingItemEnchant[0]);
				newChar.getInventory().addItem(item);
			}
		}

		newChar.setXYZInvisible(template.spawnLoc);

		if(ConfigValue.CharTitle)
			newChar.setTitle(ConfigValue.CharAddTitle);
		else
			newChar.setTitle("");

		ItemTemplates itemTable = ItemTemplates.getInstance();
		{
			for(L2Item i : template.getItems())
			{
				L2ItemInstance item = itemTable.createItem(i.getItemId());
				newChar.getInventory().addItem(item);

				if(item.getItemId() == 5588) // tutorial book
					newChar.registerShortCut(new L2ShortCut(11, 0, L2ShortCut.TYPE_ITEM, item.getObjectId(), -1));

				if(item.isEquipable() && (newChar.getActiveWeaponItem() == null || item.getItem().getType2() != L2Item.TYPE2_WEAPON))
					newChar.getInventory().equipItem(item, false);
			}

			// Scroll of Escape: Kamael Village
			L2ItemInstance item = itemTable.createItem(9716);
			item.setCount(10);
			newChar.getInventory().addItem(item);

			// Adventurer's Scroll of Escape
			item = itemTable.createItem(10650);
			item.setCount(5);
			newChar.getInventory().addItem(item);
		}

		for(L2SkillLearn skill : newChar.getAvailableSkills(newChar.getClassId()))
			newChar.addSkill(SkillTable.getInstance().getInfo(skill.id, skill.skillLevel), true);

		if(newChar.getSkillLevel(1001) > 0) // Soul Cry
			newChar.registerShortCut(new L2ShortCut(1, 0, L2ShortCut.TYPE_SKILL, 1001, 1));
		if(newChar.getSkillLevel(1177) > 0) // Wind Strike
			newChar.registerShortCut(new L2ShortCut(1, 0, L2ShortCut.TYPE_SKILL, 1177, 1));
		if(newChar.getSkillLevel(1216) > 0) // Self Heal
			newChar.registerShortCut(new L2ShortCut(2, 0, L2ShortCut.TYPE_SKILL, 1216, 1));

		// add attack, take, sit shortcut
		newChar.registerShortCut(new L2ShortCut(0, 0, L2ShortCut.TYPE_ACTION, 2, -1));
		newChar.registerShortCut(new L2ShortCut(3, 0, L2ShortCut.TYPE_ACTION, 5, -1));
		newChar.registerShortCut(new L2ShortCut(10, 0, L2ShortCut.TYPE_ACTION, 0, -1));
		// fly transform
		newChar.registerShortCut(new L2ShortCut(0, 10, L2ShortCut.TYPE_SKILL, 911, 1));
		newChar.registerShortCut(new L2ShortCut(3, 10, L2ShortCut.TYPE_SKILL, 884, 1));
		newChar.registerShortCut(new L2ShortCut(4, 10, L2ShortCut.TYPE_SKILL, 885, 1));
		// air ship
		newChar.registerShortCut(new L2ShortCut(0, 11, L2ShortCut.TYPE_ACTION, 70, 0));

		startTutorialQuest(newChar);

		newChar.setCurrentHpMp(newChar.getMaxHp(), newChar.getMaxMp());
		newChar.setCurrentCp(0); // retail
		newChar.getRecommendation().setRecomTimeLeft(3600);

		if(ConfigValue.MailOnEnterGame)
		{
			MailParcelController.Letter mail = new MailParcelController.Letter();
			mail.senderId = 1;
			mail.senderName = ConfigValue.MailOnEnterGameSenderName;
			mail.receiverId = newChar.getObjectId();
			mail.receiverName = newChar.getName();
			mail.topic = ConfigValue.MailOnEnterGameTopic;
			mail.body = ConfigValue.MailOnEnterGameBody;
			mail.price = 0;
			mail.unread = 1;
			mail.system = 0;
			mail.hideSender = 2;
			mail.validtime = 720 * 3600 + (int) (System.currentTimeMillis() / 1000L);

			MailParcelController.getInstance().sendLetter(mail);
			//newChar.setVar("MyBirthdayReceiveYear", String.valueOf(now.get(Calendar.YEAR)), -1);
		}

		if(ConfigValue.CharacterCreate350q)
		{
			Quest q = QuestManager.getQuest("_350_EnhanceYourWeapon");
			QuestState qs = q.newQuestState(newChar, Quest.STARTED);
			qs.setCond(1);
		}
		if(ConfigValue.CharacterCreateNoble)
		{
			Quest q = QuestManager.getQuest("_234_FatesWhisper");
			QuestState qs = newChar.getQuestState(q.getName());
			if(qs != null)
				qs.exitCurrentQuest(true);
			q.newQuestState(newChar, Quest.COMPLETED);

			if(newChar.getRace() == Race.kamael)
			{
				q = QuestManager.getQuest("_236_SeedsOfChaos");
				qs = newChar.getQuestState(q.getName());
				if(qs != null)
					qs.exitCurrentQuest(true);
				q.newQuestState(newChar, Quest.COMPLETED);
			}
			else
			{
				q = QuestManager.getQuest("_235_MimirsElixir");
				qs = newChar.getQuestState(q.getName());
				if(qs != null)
					qs.exitCurrentQuest(true);
				q.newQuestState(newChar, Quest.COMPLETED);
			}

			Olympiad.addNoble(newChar);
			newChar.setNoble(true);
			newChar.updatePledgeClass();
			newChar.updateNobleSkills();
		}
		else if(ConfigValue.CharacterSetSkillNoble)
			newChar.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_NOBLESSE_BLESSING, 1));

		
		//newChar.setOnlineStatus(false);

		PlayerManager.saveCharToDisk(newChar);
		newChar.deleteMe(); // release the world of this character and it's inventory

		client.setCharSelection(CharacterSelectionInfo.loadCharacterSelectInfo(client.getLoginName()));
	}

	/**
	 * Прическа, цвет волос, лицо
	 *-------------------------
	 * Человек:
	 *----------
	 * Воин Мужик: 5(E), 4(D), 3(C)
	 * Воин Женщина: 7(G), 4(D), 3(C)
	 *----------
	 * Маг Мужик: 5(E), 4(D), 3(C)
	 * Маг Женщина: 7(G), 4(D), 3(C)
	 **************************************
	 * Эльф:
	 *----------
	 * Воин Мужик: 5(E), 4(D), 3(C)
	 * Воин Женщина: 7(G), 4(D), 3(C)
	 *----------
	 * Маг Мужик: 5(E), 4(D), 3(C)
	 * Маг Женщина: 7(G), 4(D), 3(C)
	 **************************************
	 * Темный Эльф:
	 *----------
	 * Воин Мужик: 5(E), 4(D), 3(C)
	 * Воин Женщина: 7(G), 4(D), 3(C)
	 *----------
	 * Маг Мужик: 5(E), 4(D), 3(C)
	 * Маг Женщина: 7(G), 4(D), 3(C)
	 **************************************
	 * Орк:
	 *----------
	 * Воин Мужик: 5(E), 4(D), 3(C)
	 * Воин Женщина: 7(G), 4(D), 3(C)
	 *----------
	 * Маг Мужик: 5(E), 4(D), 3(C)
	 * Маг Женщина: 7(G), 4(D), 3(C)
	 **************************************
	 * Гном:
	 *----------
	 * Воин Мужик: 5(E), 4(D), 3(C)
	 * Воин Женщина: 7(G), 4(D), 3(C)
	 **************************************
	 * Камаэль:
	 *----------
	 * Воин Мужик: 5(E), 3(C), 3(C)
	 * Воин Женщина: 7(G), 3(C), 3(C)
	 **************************************
	 **/
	private static boolean validChar(int sex, int classId, int hairStyle, int hairColor, int face)
	{
		if(sex < 0 || sex > 1 || hairStyle < 0 || hairColor < 0 || face < 0 || face >= 3 || sex == 0 && hairStyle >= 5 || sex == 1 && hairStyle >= 7 || hairColor >= 4 || (classId == 123 || classId == 124) && (ConfigValue.CannotCreateKamael || hairColor >= 3))
		{
			// TODO: Выдаем банан по железке и сам акк
			return false;
		}
		return true;
	}

	public static void startTutorialQuest(L2Player player)
	{
		Quest q = QuestManager.getQuest(255);
		if(q != null)
			q.newQuestState(player, Quest.CREATED);
	}
}