package quests._197_SevenSignTheSacredBookOfSeal;

import javolution.util.FastMap;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import quests._196_SevenSignSealOfTheEmperor._196_SevenSignSealOfTheEmperor;

public class _197_SevenSignTheSacredBookOfSeal extends Quest implements ScriptFile
{	
	private static final int Wood = 32593;
	private static final int Orven = 30857;
	private static final int Leopard = 32594;
	private static final int Lawrence = 32595;
	private static final int Sophia = 32596;
	
	private static final int SculptureofDoubt = 14355;
	private static final int MysteriousHandwrittenText = 13829;
	private static final int ShillenEvilSpiritId = 27343;
	
	private static FastMap<Integer, Integer> spawns = new FastMap<Integer, Integer>();
	
	public _197_SevenSignTheSacredBookOfSeal()
	{
		super(false);
		
		addStartNpc(Wood);
		
		addTalkId(Orven);
		addTalkId(Leopard);
		addTalkId(Lawrence);
		addTalkId(Sophia);
		
		addKillId(ShillenEvilSpiritId);
		
		addQuestItem(SculptureofDoubt);
		addQuestItem(MysteriousHandwrittenText);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		int cond = st.getInt("cond");
		String htmltext = event;
		L2Player player = st.getPlayer();
		
		if(event.equals("32593-04.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("30857-04.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equals("32594-03.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32595-04.htm"))
		{
			htmltext = "32595-04.htm";
			L2NpcInstance mob = st.addSpawn(ShillenEvilSpiritId, 180000);
			spawns.put(player.getObjectId(), mob.getObjectId());
			Functions.npcSay(mob, "You are not the owner of that item.");
			mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 100000);
		}
		else if(event.equals("32595-08.htm"))
		{
			st.set("cond", "5");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equals("32596-04.htm"))
		{
			st.set("cond", "6");
			st.playSound(SOUND_MIDDLE);
			st.giveItems(MysteriousHandwrittenText, 1);
		}
		else if(event.equalsIgnoreCase("32593-08.htm"))
		{
			if(!player.isSubClassActive())
			{
				st.addExpAndSp(52518015, 5817677);
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
				st.takeItems(MysteriousHandwrittenText, -1);
				st.takeItems(SculptureofDoubt, -1);
			}
			else
				htmltext = "<html><body>Only characters who are <font color=\"LEVEL\">main class</font>.</body></html>";
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
		
		if(npcId == Wood)
		{
			if(id == CREATED)
			{
				if(player.getLevel() < 79 && player.isSubClassActive())
				{
					st.exitCurrentQuest(true);
					return "32593-00.htm";
				}
				QuestState qs = player.getQuestState(_196_SevenSignSealOfTheEmperor.class);
				if(qs == null || !qs.isCompleted())
				{
					st.exitCurrentQuest(true);
					return "noquest";
				}
				return "32593-01.htm";
			}
			if(cond == 1)
				return "32593-05.htm";
			else if(cond == 6)
				return "32593-06.htm";
		}
		else if(npcId == Orven)
		{
			if(cond == 1)
				return "30857-01.htm";
			else if(cond == 2)
				return "30857-05.htm";
		}
		else if(npcId == Leopard)
		{
			if(cond == 2)
				return "32594-01.htm";
			else if(cond == 3)
				return "32594-04.htm";
		}
		else if(npcId == Lawrence)
		{
			if(cond == 3)
			{
				Integer obj_id = spawns.get(player.getObjectId());
				L2NpcInstance mob = obj_id != null ? L2ObjectsStorage.getNpc(obj_id) : null;
				if(mob == null || mob.isDead())
					return "32595-01.htm";
				else
					return "32595-05.htm";
					
			}
			else if(cond == 4)
				return "32595-06.htm";
			else if(cond == 5)
				return "32595-09.htm";
		}
		else if(npcId == Sophia)
		{
			if(cond == 5)
				return "32596-01.htm";
			else if(cond == 6)
				return "32596-05.htm";
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
		if(npcId == ShillenEvilSpiritId && cond == 3 && id != null && id == npc.getObjectId())
		{
			st.set("cond", "4");
			st.playSound(SOUND_ITEMGET);
			st.giveItems(SculptureofDoubt, 1);
			Functions.npcSay(npc, st.getPlayer().getName() + "! You may have won this time... But next time, I will surely capture you!");
			L2NpcInstance lawrence = L2ObjectsStorage.getByNpcId(Lawrence);
			if(lawrence != null)
				Functions.npcSay(lawrence, "Well done. " + player.getName() + ". You help is much appreciated.");
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