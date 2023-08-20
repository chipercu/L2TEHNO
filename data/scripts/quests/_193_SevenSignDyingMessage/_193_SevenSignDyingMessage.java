package quests._193_SevenSignDyingMessage;

import javolution.util.FastMap;
import quests._192_SevenSignSeriesOfDoubt._192_SevenSignSeriesOfDoubt;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExStartScenePlayer;
import l2open.gameserver.tables.SkillTable;

public class _193_SevenSignDyingMessage extends Quest implements ScriptFile
{
	// NPCs
	private static int Hollint = 30191;
	private static int Cain = 32569;
	private static int Eric = 32570;
	private static int SirGustavAthebaldt = 30760;

	// MOBs
	private static int ShilensEvilThoughts = 27375;

	// ITEMS
	private static int JacobsNecklace = 13814;
	private static int DeadmansHerb = 13816;
	private static int SculptureofDoubt = 14352;

	private static FastMap<Integer, Integer> spawns = new FastMap<Integer, Integer>();

	public _193_SevenSignDyingMessage()
	{
		super(false);

		addStartNpc(Hollint);
		addTalkId(Cain, Eric, SirGustavAthebaldt);
		addKillId(ShilensEvilThoughts);
		addQuestItem(JacobsNecklace, DeadmansHerb, SculptureofDoubt);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		L2Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("30191-02.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(JacobsNecklace, 1);
		}
		else if(event.equalsIgnoreCase("32569-05.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32570-02.htm"))
		{
			st.set("cond", "3");
			st.giveItems(DeadmansHerb, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("30760-02.htm"))
		{
			if(!player.isSubClassActive())
			{
				st.addExpAndSp(52518015, 5817677);
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
			}
			else
				htmltext = "<html><body>Only characters who are <font color=\"LEVEL\">main class</font>.</body></html>";
		}
		else if(event.equalsIgnoreCase("close_your_eyes"))
		{
			st.set("cond", "4");
			st.takeItems(DeadmansHerb, 1);
			player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ_DYING_MASSAGE);
			st.playSound(SOUND_MIDDLE);
			return "";
		}
		else if(event.equalsIgnoreCase("32569-09.htm"))
		{
			htmltext = "32569-09.htm";
			Functions.npcSay(npc, st.getPlayer().getName() + "! That stranger must be defeated. Here is the ultimate help!");
			L2NpcInstance mob = st.addSpawn(ShilensEvilThoughts, 180000);
			spawns.put(player.getObjectId(), mob.getObjectId());
			Functions.npcSay(mob, "You are not the owner of that item.");
			mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 100000);
		}
		else if(event.equalsIgnoreCase("32569-13.htm"))
		{
			st.set("cond", "6");
			st.takeItems(SculptureofDoubt, 1);
			st.playSound(SOUND_MIDDLE);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int id = st.getState();
		L2Player player = st.getPlayer();
		if(npcId == Hollint)
		{
			if(id == CREATED)
			{
				if(player.getLevel() < 79 && player.isSubClassActive())
				{
					st.exitCurrentQuest(true);
					return "30191-00.htm";
				}
				QuestState qs = player.getQuestState(_192_SevenSignSeriesOfDoubt.class);
				if(qs == null || !qs.isCompleted())
				{
					st.exitCurrentQuest(true);
					return "noquest";
				}
				return "30191-01.htm";
			}
			else if(cond == 1)
				return "30191-03.htm";
		}
		else if(npcId == Cain)
		{
			if(cond == 1)
				return "32569-01.htm";
			else if(cond == 2)
				return "32569-06.htm";
			else if(cond == 3)
				return "32569-07.htm";
			else if(cond == 4)
			{
				Integer obj_id = spawns.get(player.getObjectId());
				L2NpcInstance mob = obj_id != null ? L2ObjectsStorage.getNpc(obj_id) : null;
				if(mob == null || mob.isDead())
					return "32569-08.htm";
				else
					return "32569-09.htm";
			}
			else if(cond == 5)
				return "32569-10.htm";
			else if(cond == 6)
				return "32569-13.htm";
		}
		else if(npcId == Eric)
		{
			if(cond == 2)
				return "32570-01.htm";
			else if(cond == 3)
				return "32570-03.htm";
		}
		else if(npcId == SirGustavAthebaldt)
		{
			if(cond == 6)
				return "30760-01.htm";
		}
		return "noquest";
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		L2Player player = st.getPlayer();
		if(player == null)
			return null;
		Integer id = spawns.get(player.getObjectId());
		if(npcId == ShilensEvilThoughts && cond == 4 && id != null && id == npc.getObjectId())
		{
			st.set("cond", "5");
			st.playSound(SOUND_ITEMGET);
			st.giveItems(SculptureofDoubt, 1);
			Functions.npcSay(npc, st.getPlayer().getName() + "! You may have won this time... But next time, I will surely capture you!");
			L2NpcInstance cain = L2ObjectsStorage.getByNpcId(Cain);
			if(cain != null)
			{
				Functions.npcSay(npc, "Well done. " + player.getName() + ". You help is much appreciated.");
				cain.doCast(SkillTable.getInstance().getInfo(1218, 33), player, true); // Greater Battle Heal
			}
		}
		return null;
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}