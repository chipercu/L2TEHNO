package quests._625_TheFinestIngredientsPart2;

import l2open.common.ThreadPoolManager;
import l2open.extensions.listeners.MethodCollection;
import l2open.extensions.listeners.MethodInvokeListener;
import l2open.extensions.listeners.events.MethodEvent;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Rnd;

public class _625_TheFinestIngredientsPart2 extends Quest implements ScriptFile
{
	// NPCs
	private static int Jeremy = 31521;
	private static int Yetis_Table = 31542;
	// Mobs
	private static int RB_Icicle_Emperor_Bumbalump = 25296;
	// Items
	private static short Soy_Sauce_Jar = 7205;
	private static short Food_for_Bumbalump = 7209;
	private static short Special_Yeti_Meat = 7210;
	private static short Reward_First = 4589;
	private static short Reward_Last = 4594;

	public _625_TheFinestIngredientsPart2()
	{
		super(true);
		addStartNpc(Jeremy);
		addTalkId(Yetis_Table);
		addKillId(RB_Icicle_Emperor_Bumbalump);
		addQuestItem(Food_for_Bumbalump, Special_Yeti_Meat);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		int _state = st.getState();
		int cond = st.getInt("cond");
		if(event.equalsIgnoreCase("jeremy_q0625_0104.htm") && _state == CREATED)
		{
			if(st.getQuestItemsCount(Soy_Sauce_Jar) == 0)
			{
				st.exitCurrentQuest(true);
				return "jeremy_q0625_0102.htm";
			}
			st.setState(STARTED);
			st.set("cond", "1");
			st.takeItems(Soy_Sauce_Jar, 1);
			st.giveItems(Food_for_Bumbalump, 1);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("jeremy_q0625_0301.htm") && _state == STARTED && cond == 3)
		{
			st.exitCurrentQuest(true);
			if(st.getQuestItemsCount(Special_Yeti_Meat) == 0)
				return "jeremy_q0625_0302.htm";
			st.takeItems(Special_Yeti_Meat, 1);
			st.giveItems(Rnd.get(Reward_First, Reward_Last), 5, true);
		}
		else if(event.equalsIgnoreCase("yetis_table_q0625_0201.htm") && _state == STARTED && cond == 1)
		{
			if(ServerVariables.getLong(_625_TheFinestIngredientsPart2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
				return "yetis_table_q0625_0204.htm";
			if(st.getQuestItemsCount(Food_for_Bumbalump) == 0)
				return "yetis_table_q0625_0203.htm";
			if(BumbalumpSpawned())
				return "yetis_table_q0625_0202.htm";
			st.takeItems(Food_for_Bumbalump, 1);
			st.set("cond", "2");
			ThreadPoolManager.getInstance().schedule(new BumbalumpSpawner(), 1000);
		}

		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int _state = st.getState();
		int npcId = npc.getNpcId();
		if(_state == CREATED)
		{
			if(npcId != Jeremy)
				return "noquest";
			if(st.getPlayer().getLevel() < 73)
			{
				st.exitCurrentQuest(true);
				return "jeremy_q0625_0103.htm";
			}
			if(st.getQuestItemsCount(Soy_Sauce_Jar) == 0)
			{
				st.exitCurrentQuest(true);
				return "jeremy_q0625_0102.htm";
			}
			st.set("cond", "0");
			return "jeremy_q0625_0101.htm";
		}

		if(_state != STARTED)
			return "noquest";
		int cond = st.getInt("cond");

		if(npcId == Jeremy)
		{
			if(cond == 1)
				return "jeremy_q0625_0105.htm";
			if(cond == 2)
				return "jeremy_q0625_0202.htm";
			if(cond == 3)
				return "jeremy_q0625_0201.htm";
		}

		if(npcId == Yetis_Table)
		{
			if(ServerVariables.getLong(_625_TheFinestIngredientsPart2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
				return "yetis_table_q0625_0204.htm";
			if(cond == 1)
				return "yetis_table_q0625_0101.htm";
			if(cond == 2)
			{
				if(BumbalumpSpawned())
					return "yetis_table_q0625_0202.htm";
				ThreadPoolManager.getInstance().schedule(new BumbalumpSpawner(), 1000);
				return "yetis_table_q0625_0201.htm";
			}
			if(cond == 3)
				return "yetis_table_q0625_0204.htm";
		}

		return "noquest";
	}

	private static class DieListener implements MethodInvokeListener
	{
		@Override
		public boolean accept(MethodEvent event)
		{
			return true;
		}

		@Override
		public void methodInvoked(MethodEvent e)
		{
			ServerVariables.set(_625_TheFinestIngredientsPart2.class.getSimpleName(), String.valueOf(System.currentTimeMillis()));
		}
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{

		if(st.getInt("cond") == 1 || st.getInt("cond") == 2)
		{
			if(st.getQuestItemsCount(Food_for_Bumbalump) > 0)
				st.takeItems(Food_for_Bumbalump, 1);
			st.giveItems(Special_Yeti_Meat, 1);
			st.set("cond", "3");
			st.playSound(SOUND_MIDDLE);
		}

		return null;
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static boolean BumbalumpSpawned()
	{
		return L2ObjectsStorage.getByNpcId(RB_Icicle_Emperor_Bumbalump) != null;
	}

	public class BumbalumpSpawner extends l2open.common.RunnableImpl
	{
		private L2Spawn _spawn = null;
		private int tiks = 0;

		public BumbalumpSpawner()
		{
			if(BumbalumpSpawned())
				return;
			L2NpcTemplate template = NpcTable.getTemplate(RB_Icicle_Emperor_Bumbalump);
			if(template == null)
				return;
			try
			{
				_spawn = new L2Spawn(template);
			}
			catch(Exception E)
			{
				return;
			}
			_spawn.setLocx(158240);
			_spawn.setLocy(-121536);
			_spawn.setLocz(-2253);
			_spawn.setHeading(Rnd.get(0, 0xFFFF));
			_spawn.setAmount(1);
			_spawn.doSpawn(true);
			_spawn.stopRespawn();
			for(L2NpcInstance _npc : _spawn.getAllSpawned())
				_npc.addMethodInvokeListener(MethodCollection.doDie, new DieListener());
		}

		public void Say(String test)
		{
			for(L2NpcInstance _npc : _spawn.getAllSpawned())
				Functions.npcSay(_npc, test);
		}

		public void runImpl()
		{
			if(_spawn == null)
				return;
			if(tiks == 0)
				Say("I will crush you!");
			if(tiks < 1200 && BumbalumpSpawned())
			{
				tiks++;
				if(tiks == 1200)
					Say("May the gods forever condemn you! Your power weakens!");
				ThreadPoolManager.getInstance().schedule(this, 1000);
				return;
			}
			_spawn.despawnAll();
		}
	}
}