package quests._025_HidingBehindTheTruth;

import static l2open.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _025_HidingBehindTheTruth extends Quest implements ScriptFile
{
	// Список NPC
	private static final int AGRIPEL = 31348;
	private static final int BENEDICT = 31349;
	private static final int BROKEN_BOOK_SHELF = 31534;
	private static final int COFFIN = 31536;
	private static final int MAID_OF_LIDIA = 31532;
	private static final int MYSTERIOUS_WIZARD = 31522;
	private static final int TOMBSTONE = 31531;

	// Список итемов
	private static final short CONTRACT = 7066;
	private static final short EARRING_OF_BLESSING = 874;
	private static final short GEMSTONE_KEY = 7157;
	private static final short LIDIAS_DRESS = 7155;
	private static final short MAP_FOREST_OF_DEADMAN = 7063;
	private static final short NECKLACE_OF_BLESSING = 936;
	private static final short RING_OF_BLESSING = 905;
	private static final short SUSPICIOUS_TOTEM_DOLL = 7156;
	private static final short SUSPICIOUS_TOTEM_DOLL_2 = 7158;

	// Список мобов
	private static final int TRIOLS_PAWN = 27218;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _025_HidingBehindTheTruth()
	{
		super(false);

		addStartNpc(BENEDICT);
		addTalkId(new int[] { AGRIPEL, BROKEN_BOOK_SHELF, COFFIN, MAID_OF_LIDIA, MYSTERIOUS_WIZARD, TOMBSTONE });
		addKillId(TRIOLS_PAWN);
		addQuestItem(new int[] { CONTRACT, GEMSTONE_KEY, LIDIAS_DRESS, MAP_FOREST_OF_DEADMAN, SUSPICIOUS_TOTEM_DOLL_2 });
	}

	@Override
	public String onEvent(String event, QuestState qs, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("StartQuest"))
		{
			QuestState qs_24 = qs.getPlayer().getQuestState("_024_InhabitantsOfTheForestOfTheDead");
			if(qs_24 != null && qs_24.isCompleted() && qs.getPlayer().getLevel() > 65)
			{
				if(qs.getQuestItemsCount(SUSPICIOUS_TOTEM_DOLL) > 0)
				{
					htmltext = "31349-03.htm";
					qs.set("cond", "1");
					qs.setState(STARTED);
					qs.playSound(SOUND_ACCEPT);
				}
				else
					htmltext = "31349-03a.htm";
			}
			else
			{
				htmltext = "31349-02.htm";
				qs.exitCurrentQuest(true);
			}
		}
		else if(event.equalsIgnoreCase("31349-05.htm"))
		{
			qs.set("cond", "2");
			qs.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("31349-10.htm"))
		{
			qs.set("cond", "4");
			qs.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("31348-08.htm"))
		{
			if(qs.getInt("cond") == 4)
			{
				qs.set("cond", "5");
				qs.setState(STARTED);
				if(qs.getQuestItemsCount(SUSPICIOUS_TOTEM_DOLL) > 0)
					qs.takeItems(SUSPICIOUS_TOTEM_DOLL, -1);
				if(qs.getQuestItemsCount(GEMSTONE_KEY) < 1)
					qs.giveItems(GEMSTONE_KEY, 1);
			}
		}
		else if(event.equalsIgnoreCase("31522-04.htm"))
		{
			qs.set("cond", "6");
			qs.setState(STARTED);
			if(qs.getQuestItemsCount(MAP_FOREST_OF_DEADMAN) < 1)
				qs.giveItems(MAP_FOREST_OF_DEADMAN, 1);
		}
		else if(event.equalsIgnoreCase("31534-07.htm"))
		{
			spawnmob(TRIOLS_PAWN, qs);
			qs.set("cond", "7");
			qs.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("31534-11.htm"))
		{
			qs.set("id", "8");
			qs.giveItems(CONTRACT, 1);
		}
		else if(event.equalsIgnoreCase("31532-07.htm"))
		{
			qs.set("cond", "11");
			qs.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("31531-02.htm"))
		{
			qs.set("cond", "12");
			qs.setState(STARTED);
			L2Player player = qs.getPlayer();
			L2NpcInstance coffin = L2ObjectsStorage.getByNpcId(COFFIN);
			if(coffin == null)
			{
				qs.addSpawn(COFFIN, player.getX() + 50, player.getY() + 50, player.getZ());
				qs.startQuestTimer("Coffin_Spawn", 120000);
			}
		}
		else if(event.equalsIgnoreCase("Coffin_Spawn"))
		{
			L2NpcInstance coffin = L2ObjectsStorage.getByNpcId(COFFIN);
			if(coffin != null)
				coffin.deleteMe();
			if(qs.getInt("cond") == 12)
			{
				qs.set("cond", "11");
				qs.setState(STARTED);
			}
			return null;
		}
		else if(event.equalsIgnoreCase("Triols_Pawn"))
		{
			L2NpcInstance triols_pawn = L2ObjectsStorage.getByNpcId(TRIOLS_PAWN);
			if(triols_pawn != null && qs.getInt("cond") == 7)
				triols_pawn.deleteMe();
			return null;
		}
		else if(event.equalsIgnoreCase("Lidia_wait"))
		{
			qs.set("id", "14");
			return null;
		}
		else if(event.equalsIgnoreCase("31532-21.htm"))
		{
			qs.set("cond", "15");
			qs.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("31522-13.htm"))
		{
			qs.set("cond", "16");
			qs.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("31348-16.htm"))
		{
			qs.set("cond", "17");
			qs.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("31348-17.htm"))
		{
			qs.set("cond", "18");
			qs.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("31348-14.htm"))
		{
			qs.set("id", "16");
			qs.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("31532-25.htm"))
		{
			if(qs.getInt("cond") == 17)
			{
				qs.giveItems(RING_OF_BLESSING, 2);
				qs.giveItems(EARRING_OF_BLESSING, 1);
				qs.addExpAndSp(572277, 53750);
				qs.getPlayer().radar.removeAllMarkers();
				qs.playSound(SOUND_FINISH);
				qs.exitCurrentQuest(false);
			}
			else
				htmltext = "31532-24.htm";
		}
		else if(event.equalsIgnoreCase("31522-16.htm"))
			if(qs.getInt("cond") == 18)
			{
				qs.giveItems(NECKLACE_OF_BLESSING, 1);
				qs.giveItems(EARRING_OF_BLESSING, 1);
				qs.addExpAndSp(572277, 53750);
				qs.getPlayer().radar.removeAllMarkers();
				qs.playSound(SOUND_FINISH);
				qs.exitCurrentQuest(false);
			}
			else
				htmltext = "31522-15a.htm";
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int IntId = st.getInt("id");
		if(st.getState() == CREATED && npcId == BENEDICT)
			htmltext = "31349-01.htm";
		else if(st.getState() == STARTED)
		{
			switch(npcId)
			{
				case BENEDICT:
				{
					if(cond == 1)
						htmltext = "31349-04.htm";
					else if(cond == 2)
						htmltext = "31349-03a.htm";
					else if(cond == 3)
						htmltext = "31349-03.htm";
					else if(cond == 4)
						htmltext = "31349-10.htm";
					break;
				}
				case MYSTERIOUS_WIZARD:
				{
					if(cond == 2)
					{
						htmltext = "31522-01.htm";
						if(st.getQuestItemsCount(SUSPICIOUS_TOTEM_DOLL) < 1)
							st.giveItems(SUSPICIOUS_TOTEM_DOLL, 1);
						st.playSound(SOUND_MIDDLE);
						st.set("cond", "3");
						st.setState(STARTED);
					}
					else if(cond == 3)
						htmltext = "31522-02.htm";
					else if(cond == 5)
						htmltext = "31522-03.htm";
					else if(cond == 6)
						htmltext = "31522-04.htm";
					else if(cond == 8)
					{
						if(IntId == 8)
							htmltext = "31522-06.htm";
						else
							htmltext = "31522-04.htm";
					}
					else if(cond == 9)
						htmltext = "31522-06.htm";
					else if(cond == 15)
						htmltext = "31522-06a.htm";
					else if(cond == 16)
						htmltext = "31522-12.htm";
					else if(cond == 17)
						htmltext = "31522-15a.htm";
					else if(cond == 18)
						htmltext = "31522-15.htm";
					break;
				}
				case AGRIPEL:
				{
					if(cond == 4)
						htmltext = "31348-01.htm";
					else if(cond == 5)
						htmltext = "31348-08a.htm";
					else if(cond == 16)
					{
						if(IntId != 16)
							htmltext = "31348-09.htm";
						else
							htmltext = "31348-15.htm";
					}
					else if(cond == 17)
						htmltext = "31348-18.htm";
					else if(cond == 18)
						htmltext = "31348-19.htm";
					break;
				}
				case BROKEN_BOOK_SHELF:
				{
					if(cond == 6)
						htmltext = "31534-01.htm";
					else if(cond == 7)
					{
						htmltext = "31534-08.htm";
						spawnmob(TRIOLS_PAWN, st);
					}
					else if(cond == 8)
						if(IntId != 8)
							htmltext = "31534-10.htm";
						else
							htmltext = "31534-06.htm";
					break;
				}
				case MAID_OF_LIDIA:
				{
					if(cond == 8)
					{
						if(st.getQuestItemsCount(CONTRACT) > 0)
							htmltext = "31532-01.htm";
						else
							htmltext = "<font color=\"LEVEL\">Maid of Lidia:</font><br><br>You have no Contract...";
					}
					else if(10 < cond && cond < 13)
						htmltext = "31532-07.htm";
					else if(cond == 13)
					{
						if(st.getQuestItemsCount(LIDIAS_DRESS) > 0)
						{
							htmltext = "31532-09.htm";
							st.set("cond", "14");
							st.setState(STARTED);
							st.startQuestTimer("Lidia_wait", 60000);
							st.takeItems(LIDIAS_DRESS, -1);
						}
						else
							htmltext = "31532-07.htm";
					}
					else if(cond == 14)
					{
						if(IntId == 14)
							htmltext = "31532-10.htm";
						else
							htmltext = "31532-09.htm";
					}
					else if(cond == 15)
						htmltext = "31532-24.htm";
					else if(cond == 17)
					{
						htmltext = "31532-23.htm";
						st.set("id", "17");
					}
					break;
				}
				case TOMBSTONE:
				{
					if(cond == 11)
						htmltext = "31531-01.htm";
					else if(cond == 12)
						htmltext = "31531-02.htm";
					else if(cond == 13)
						htmltext = "31531-03.htm";
					break;
				}
				case COFFIN:
				{
					if(cond == 12)
					{
						htmltext = "31536-01.htm";
						st.set("cond", "13");
						st.setState(STARTED);
						st.giveItems(LIDIAS_DRESS, 1);
					}
					else if(cond == 13)
						htmltext = "31531-03.htm";
					break;
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState qs)
	{
		int npcId = npc.getNpcId();
		int cond = qs.getInt("cond");
		if(npcId == TRIOLS_PAWN && cond == 7)
		{
			npc.deleteMe();
			qs.giveItems(SUSPICIOUS_TOTEM_DOLL_2, 1);
			qs.playSound(SOUND_MIDDLE);
			qs.set("cond", "8");
			qs.setState(STARTED);
		}

		return null;
	}

	public void spawnmob(int npcId, QuestState st)
	{
		L2Player player = st.getPlayer();
		L2NpcInstance npc_already_spawned = L2ObjectsStorage.getByNpcId(npcId);
		if(npc_already_spawned == null)
		{
			st.addSpawn(npcId, player.getX() + 50, player.getY() + 50, player.getZ());
			st.startQuestTimer("Triols_Pawn", 600000);
		}
		L2NpcInstance npc_spawned = L2ObjectsStorage.getByNpcId(npcId);
		if(npc_spawned != null)
		{
			st.playSound(SOUND_BEFORE_BATTLE);
			player.addDamageHate(npc_spawned, 0, 9999);
			npc_spawned.getAI().setIntention(AI_INTENTION_ATTACK, player);
		}
	}
}