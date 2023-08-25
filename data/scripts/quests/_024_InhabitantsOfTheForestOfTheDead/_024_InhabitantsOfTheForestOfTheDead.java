package quests._024_InhabitantsOfTheForestOfTheDead;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _024_InhabitantsOfTheForestOfTheDead extends Quest implements ScriptFile
{
	// Список NPC
	private static final int DORIAN = 31389;
	private static final int TOMBSTONE = 31531;
	private static final int MAID_OF_LIDIA = 31532;
	private static final int MYSTERIOUS_WIZARD = 31522;

	// Список итемов
	private static final short LIDIA_HAIR_PIN = 7148;
	private static final short SUSPICIOUS_TOTEM_DOLL = 7151;
	private static final short FLOWER_BOUQUET = 7152;
	private static final short SILVER_CROSS_OF_EINHASAD = 7153;
	private static final short BROKEN_SILVER_CROSS_OF_EINHASAD = 7154;
	private static final short SUSPICIOUS_TOTEM_DOLL1 = 7156;
	private static final short LIDIAS_LETTER = 7065;

	// Список мобов
	// Bone Snatchers, Bone Shapers, Bone Collectors, Bone Animators, Bone Slayers, Skull Collectors, Skull Animators
	private static final int[] MOBS = new int[] { 21557, 21558, 21560, 21561, 21562, 21563, 21564, 21565, 21566, 21567 };
	private static final int[] VAMPIRE = new int[] { 25328, 25329, 25330, 25331, 25332 };

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _024_InhabitantsOfTheForestOfTheDead()
	{
		super(false);

		addStartNpc(DORIAN);
		addTalkId(TOMBSTONE, MAID_OF_LIDIA, MYSTERIOUS_WIZARD);
		addKillId(MOBS);
		addQuestItem(LIDIA_HAIR_PIN, SUSPICIOUS_TOTEM_DOLL, FLOWER_BOUQUET, SILVER_CROSS_OF_EINHASAD, BROKEN_SILVER_CROSS_OF_EINHASAD, LIDIAS_LETTER);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("31389-03.htm"))
		{
			st.giveItems(FLOWER_BOUQUET, 1);
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("31531-02.htm"))
		{
			st.takeItems(FLOWER_BOUQUET, -1);
			st.set("cond", "2");
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("31389-13.htm"))
		{
			st.giveItems(SILVER_CROSS_OF_EINHASAD, 1);
			st.set("cond", "3");
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("31389-19.htm"))
		{
			st.set("cond", "5");
			st.setState(STARTED);
			st.takeItems(BROKEN_SILVER_CROSS_OF_EINHASAD, -1);
		}
		else if(event.equalsIgnoreCase("31532-04.htm"))
		{
			st.set("cond", "6");
			st.setState(STARTED);
			if(st.getQuestItemsCount(LIDIAS_LETTER) < 1)
				st.giveItems(LIDIAS_LETTER, 1);
		}
		else if(event.equalsIgnoreCase("31532-06.htm"))
		{
			st.takeItems(LIDIA_HAIR_PIN, -1);
			st.set("hairpin_is_given", "1");
		}
		else if(event.equalsIgnoreCase("31532-19.htm"))
		{
			st.unset("hairpin_is_given");
			st.set("cond", "9");
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("31522-03.htm"))
		{
			st.takeItems(SUSPICIOUS_TOTEM_DOLL, -1);
			st.set("totemdoll_is_given", "1");
		}
		else if(event.equalsIgnoreCase("31522-08.htm"))
		{
			st.set("cond", "11");
			st.setState(STARTED);
			st.startQuestTimer("To talk with Mystik", 600000);
		}
		else if(event.equalsIgnoreCase("To talk with Mystik"))
			htmltext = null;
		else if(event.equalsIgnoreCase("31522-21.htm"))
		{
			QuestState qs_25 = st.getPlayer().getQuestState("_025_HidingBehindTheTruth");
			if(qs_25 != null && qs_25.getInt("cond") < 3)
			{
				qs_25.set("cond", "3");
				qs_25.setState(STARTED);
			}
			if(st.getQuestItemsCount(SUSPICIOUS_TOTEM_DOLL1) < 1)
				st.giveItems(SUSPICIOUS_TOTEM_DOLL1, 1);
			st.startQuestTimer("html", 10000);
			if(st.getPlayer().getVar("lang@").equalsIgnoreCase("en"))
			{
				st.getPlayer().sendMessage("Congratulations! You are completed this quest!" + " \n The Quest \"Hiding Behind the Truth\"" + " become available.\n Show Suspicious Totem Doll to " + " Priest Benedict.");
			}
			else
			{
				st.getPlayer().sendMessage("Поздравляем! Вы завершили этот квест!" + " \n Квест \"Hiding Behind the Truth\"" + " теперь доступен.\n Покажите \"Suspicious Totem Doll\" священнику Бенедикту (Priest Benedict).");
			}
		}
		else if(event.equalsIgnoreCase("html"))
		{
			st.playSound(SOUND_FINISH);
			st.addExpAndSp(242105, 22529, true);
			st.exitCurrentQuest(false);
			htmltext = "31522-22.htm";
		}
		else if(event.startsWith("playerInMobRange")) //TODO: Сделать какой-то стандарт
		{
			if(st.getInt("cond") == 3)
			{
				String[] arr = event.split("_");
				int id = Integer.parseInt(arr[1]);
				if(arrayContains(VAMPIRE, id))
				{
					st.takeItems(LIDIA_HAIR_PIN, -1); //TODO: проверить на соответсвие оффу
					st.takeItems(SILVER_CROSS_OF_EINHASAD, -1);
					if(st.getQuestItemsCount(BROKEN_SILVER_CROSS_OF_EINHASAD) < 1)
						st.giveItems(BROKEN_SILVER_CROSS_OF_EINHASAD, 1);
					st.playSound(SOUND_HORROR2);
					st.set("cond", "4");
					st.setState(STARTED);
				}
			}
			htmltext = null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(st.getState() == CREATED && npcId == DORIAN)
		{
			QuestState LidiasHeart = st.getPlayer().getQuestState("_023_LidiasHeart");
			if(LidiasHeart != null && LidiasHeart.isCompleted() && st.getPlayer().getLevel() > 64)
				htmltext = "31389-01.htm";
			else
				htmltext = "31389-02.htm"; // Если 23 квест не пройден
		}
		else if(st.getState() == STARTED)
		{
			switch(npcId)
			{
				case DORIAN:
				{
					if(cond == 1 && st.getQuestItemsCount(FLOWER_BOUQUET) > 0)
						htmltext = "31389-04.htm"; // Если букет еще в руках
					else if(cond == 2 && st.getQuestItemsCount(FLOWER_BOUQUET) < 1)
						htmltext = "31389-05.htm";
					else if(cond == 3 && st.getQuestItemsCount(SILVER_CROSS_OF_EINHASAD) > 0)
						htmltext = "31389-14.htm";
					else if(cond == 4 && st.getQuestItemsCount(BROKEN_SILVER_CROSS_OF_EINHASAD) > 0)
						htmltext = "31389-15.htm";
					else if(cond == 5)
						htmltext = "31389-19.htm";
					else if(cond == 7 && st.getQuestItemsCount(LIDIA_HAIR_PIN) < 1)
					{
						htmltext = "31389-21.htm";
						st.giveItems(LIDIA_HAIR_PIN, 1);
						st.set("hairpin_is_given", "0");
						st.set("cond", "8");
						st.setState(STARTED);
					}
					break;
				}
				case TOMBSTONE:
				{
					if(cond == 1 && st.getQuestItemsCount(FLOWER_BOUQUET) > 0)
						htmltext = "31531-01.htm";
					else if(cond == 2 && st.getQuestItemsCount(FLOWER_BOUQUET) < 1)
						htmltext = "31531-03.htm"; // Если букет уже оставлен
					break;
				}
				case MAID_OF_LIDIA:
				{
					if(cond == 5)
						htmltext = "31532-01.htm";
					else if(cond == 6)
					{
						htmltext = "31532-07.htm";
						st.set("cond", "7");
						st.setState(STARTED);
					}
					else if(cond == 7)
						htmltext = "31532-07.htm";
					else if(cond == 8)
					{
						if(st.getQuestItemsCount(LIDIA_HAIR_PIN) > 0)
							htmltext = "31532-05.htm";
						else if(st.getInt("hairpin_is_given") == 1)
							htmltext = "31532-06.htm";
						else
						{
							st.set("cond", "7");
							st.setState(STARTED);
						}
					}
					break;
				}
				case MYSTERIOUS_WIZARD:
				{
					if(cond == 10)
					{
						if(st.getQuestItemsCount(SUSPICIOUS_TOTEM_DOLL) > 0)
							htmltext = "31522-01.htm";
						else if(st.getInt("totemdoll_is_given") == 1)
							htmltext = "31522-03.htm";
						else
						{
							st.set("cond", "9");
							st.setState(STARTED);
						}
					}
					else if(cond == 11)
					{
						if(st.getQuestTimer("To talk with Mystik") != null)
							htmltext = "31522-08.htm";
						else
						{
							if(st.getQuestItemsCount(SUSPICIOUS_TOTEM_DOLL1) < 1)
								htmltext = "31522-09.htm";
							else
								htmltext = "31522-22.htm";
						}
					}
					break;
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(arrayContains(MOBS, npcId))
			if(cond == 9 && Rnd.chance(70))
			{
				if(st.getQuestItemsCount(SUSPICIOUS_TOTEM_DOLL) < 1)
					st.giveItems(SUSPICIOUS_TOTEM_DOLL, 1);
				st.set("totemdoll_is_given", "0");
				st.playSound(SOUND_MIDDLE);
				st.set("cond", "10");
				st.setState(STARTED);
			}
		return null;
	}

	private boolean arrayContains(int[] array, int id)
	{
		for(int i : array)
			if(i == id)
				return true;
		return false;
	}
}