package quests.SagasSuperclass;

import java.util.HashMap;
import java.util.Iterator;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.model.quest.QuestTimer;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.reference.*;
import quests._067_SagaOfTheDoombringer._067_SagaOfTheDoombringer;
import quests._068_SagaOfTheSoulHound._068_SagaOfTheSoulHound;
import quests._069_SagaOfTheTrickster._069_SagaOfTheTrickster;
import quests._070_SagaOfThePhoenixKnight._070_SagaOfThePhoenixKnight;
import quests._071_SagaOfEvasTemplar._071_SagaOfEvasTemplar;
import quests._072_SagaOfTheSwordMuse._072_SagaOfTheSwordMuse;
import quests._073_SagaOfTheDuelist._073_SagaOfTheDuelist;
import quests._074_SagaOfTheDreadnoughts._074_SagaOfTheDreadnoughts;
import quests._075_SagaOfTheTitan._075_SagaOfTheTitan;
import quests._076_SagaOfTheGrandKhavatari._076_SagaOfTheGrandKhavatari;
import quests._077_SagaOfTheDominator._077_SagaOfTheDominator;
import quests._078_SagaOfTheDoomcryer._078_SagaOfTheDoomcryer;
import quests._079_SagaOfTheAdventurer._079_SagaOfTheAdventurer;
import quests._080_SagaOfTheWindRider._080_SagaOfTheWindRider;
import quests._081_SagaOfTheGhostHunter._081_SagaOfTheGhostHunter;
import quests._082_SagaOfTheSagittarius._082_SagaOfTheSagittarius;
import quests._083_SagaOfTheMoonlightSentinel._083_SagaOfTheMoonlightSentinel;
import quests._084_SagaOfTheGhostSentinel._084_SagaOfTheGhostSentinel;
import quests._085_SagaOfTheCardinal._085_SagaOfTheCardinal;
import quests._086_SagaOfTheHierophant._086_SagaOfTheHierophant;
import quests._087_SagaOfEvasSaint._087_SagaOfEvasSaint;
import quests._088_SagaOfTheArchmage._088_SagaOfTheArchmage;
import quests._089_SagaOfTheMysticMuse._089_SagaOfTheMysticMuse;
import quests._090_SagaOfTheStormScreamer._090_SagaOfTheStormScreamer;
import quests._091_SagaOfTheArcanaLord._091_SagaOfTheArcanaLord;
import quests._092_SagaOfTheElementalMaster._092_SagaOfTheElementalMaster;
import quests._093_SagaOfTheSpectralMaster._093_SagaOfTheSpectralMaster;
import quests._094_SagaOfTheSoultaker._094_SagaOfTheSoultaker;
import quests._095_SagaOfTheHellKnight._095_SagaOfTheHellKnight;
import quests._096_SagaOfTheSpectralDancer._096_SagaOfTheSpectralDancer;
import quests._097_SagaOfTheShillienTemplar._097_SagaOfTheShillienTemplar;
import quests._098_SagaOfTheShillienSaint._098_SagaOfTheShillienSaint;
import quests._099_SagaOfTheFortuneSeeker._099_SagaOfTheFortuneSeeker;
import quests._100_SagaOfTheMaestro._100_SagaOfTheMaestro;

public abstract class SagasSuperclass extends Quest implements ScriptFile
{
	protected int id = 0;
	protected int classid = 0;
	protected int prevclass = 0;
	protected int[] NPC = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
	public int[] Items = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
	protected int[] Mob = new int[] { 0, 1, 2 };
	protected int[] X = new int[] { 0, 1, 2 };
	protected int[] Y = new int[] { 0, 1, 2 };
	protected int[] Z = new int[] { 0, 1, 2 };
	public String[] Text = new String[18];
	protected GArray<Spawn> Spawn_List = new GArray<Spawn>();

	private class Spawn
	{
		public final int npcId, TimeToLive;
		public final long spawned_at;
		public HardReference<? extends L2NpcInstance> _npc_ref = HardReferences.emptyRef();
		public HardReference<L2Player> _char_ref = HardReferences.emptyRef();

		public Spawn(L2NpcInstance npc, L2Player _char, int TimeToLive)
		{
			npcId = npc.getNpcId();
			_npc_ref = npc.getRef();
			_char_ref = _char.getRef();
			this.TimeToLive = TimeToLive;
			spawned_at = System.currentTimeMillis();
		}

		public L2NpcInstance getNPC()
		{
			return _npc_ref.get();
		}
	}

	protected int[] Archon_Minions = new int[] { 21646, 21647, 21648, 21649, 21650, 21651 };
	protected int[] Guardian_Angels = new int[] { 27214, 27215, 27216 };
	protected int[] Archon_Hellisha_Norm = new int[] { 18212, 18213, 18214, 18215, 18216, 18217, 18218, 18219 };

	protected static HashMap<Integer, Class<?>> Quests = new HashMap<Integer, Class<?>>();
	static
	{
		Quests.put(67, _067_SagaOfTheDoombringer.class);
		Quests.put(68, _068_SagaOfTheSoulHound.class);
		Quests.put(69, _069_SagaOfTheTrickster.class);
		Quests.put(70, _070_SagaOfThePhoenixKnight.class);
		Quests.put(71, _071_SagaOfEvasTemplar.class);
		Quests.put(72, _072_SagaOfTheSwordMuse.class);
		Quests.put(73, _073_SagaOfTheDuelist.class);
		Quests.put(74, _074_SagaOfTheDreadnoughts.class);
		Quests.put(75, _075_SagaOfTheTitan.class);
		Quests.put(76, _076_SagaOfTheGrandKhavatari.class);
		Quests.put(77, _077_SagaOfTheDominator.class);
		Quests.put(78, _078_SagaOfTheDoomcryer.class);
		Quests.put(79, _079_SagaOfTheAdventurer.class);
		Quests.put(80, _080_SagaOfTheWindRider.class);
		Quests.put(81, _081_SagaOfTheGhostHunter.class);
		Quests.put(82, _082_SagaOfTheSagittarius.class);
		Quests.put(83, _083_SagaOfTheMoonlightSentinel.class);
		Quests.put(84, _084_SagaOfTheGhostSentinel.class);
		Quests.put(85, _085_SagaOfTheCardinal.class);
		Quests.put(86, _086_SagaOfTheHierophant.class);
		Quests.put(87, _087_SagaOfEvasSaint.class);
		Quests.put(88, _088_SagaOfTheArchmage.class);
		Quests.put(89, _089_SagaOfTheMysticMuse.class);
		Quests.put(90, _090_SagaOfTheStormScreamer.class);
		Quests.put(91, _091_SagaOfTheArcanaLord.class);
		Quests.put(92, _092_SagaOfTheElementalMaster.class);
		Quests.put(93, _093_SagaOfTheSpectralMaster.class);
		Quests.put(94, _094_SagaOfTheSoultaker.class);
		Quests.put(95, _095_SagaOfTheHellKnight.class);
		Quests.put(96, _096_SagaOfTheSpectralDancer.class);
		Quests.put(97, _097_SagaOfTheShillienTemplar.class);
		Quests.put(98, _098_SagaOfTheShillienSaint.class);
		Quests.put(99, _099_SagaOfTheFortuneSeeker.class);
		Quests.put(100, _100_SagaOfTheMaestro.class);
	}

	protected static int[][] QuestClass = new int[][] { { 0x7f }, { 0x80, 0x81 }, { 0x82 }, { 0x05 }, { 0x14 }, { 0x15 },
			{ 0x02 }, { 0x03 }, { 0x2e }, { 0x30 }, { 0x33 }, { 0x34 }, { 0x08 }, { 0x17 }, { 0x24 }, { 0x09 }, { 0x18 },
			{ 0x25 }, { 0x10 }, { 0x11 }, { 0x1e }, { 0x0c }, { 0x1b }, { 0x28 }, { 0x0e }, { 0x1c }, { 0x29 }, { 0x0d },
			{ 0x06 }, { 0x22 }, { 0x21 }, { 0x2b }, { 0x37 }, { 0x39 } };

	private void cleanTempVars()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement st = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			st = con.prepareStatement("DELETE FROM character_quests WHERE name=? AND (var='spawned' OR var='kills' OR var='Archon' OR var LIKE 'Mob_%')");
			st.setString(1, getName());
			st.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, st);
		}
	}

	private void FinishQuest(QuestState st, L2Player player)
	{
		st.addExpAndSp(2586527, 0, true);
		st.giveItems(ADENA_ID, ConfigValue.QuestSagasRewardAdenaCount, false);
		st.giveItems(6622, ConfigValue.QuestSagasRewardCodexCount, false);
		st.exitCurrentQuest(true);
		player.setClassId(getClassId(player));
		if(player.getBaseClassId() == player.getActiveClassId() && player.getBaseClassId() == getPrevClass(player))
			player.setBaseClass(getClassId(player));
		player.broadcastUserInfo(true);
		Cast(st.findTemplate(NPC[0]), player, 4339, 1);
	}

	public void onLoad()
	{
		cleanTempVars();
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public SagasSuperclass(boolean party)
	{
		super(party);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new SpawnCleaner(), 60000, 10000);
	}

	protected void registerNPCs()
	{
		addStartNpc(NPC[0]);
		addAttackId(Mob[2]);
		addFirstTalkId(NPC[4]);

		for(int npc : NPC)
			addTalkId(npc);

		for(int mobid : Mob)
			addKillId(mobid);

		for(int mobid : Archon_Minions)
			addKillId(mobid);

		for(int mobid : Guardian_Angels)
			addKillId(mobid);

		for(int mobid : Archon_Hellisha_Norm)
			addKillId(mobid);

		for(int ItemId : Items)
			if(ItemId != 0 && ItemId != 7080 && ItemId != 7081 && ItemId != 6480 && ItemId != 6482)
				addQuestItem(ItemId);
	}

	protected int getClassId(L2Player player)
	{
		return classid;
	}

	protected int getPrevClass(L2Player player)
	{
		return prevclass;
	}

	protected void Cast(L2NpcInstance npc, L2Character target, int skillId, int level)
	{
		target.broadcastSkill(new MagicSkillUse(target, target, skillId, level, 6000, 1), true);
		target.broadcastSkill(new MagicSkillUse(npc, npc, skillId, level, 6000, 1), true);
	}

	public class SpawnCleaner extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			synchronized (Spawn_List)
			{
				long curr_time = System.currentTimeMillis();
				for(Spawn spawn : Spawn_List)
				{
					L2NpcInstance npc = spawn.getNPC();
					if(curr_time - spawn.spawned_at > spawn.TimeToLive || npc == null)
					{
						if(npc != null)
							npc.deleteMe();
						Spawn_List.remove(spawn);
					}
				}
			}
		}
	}

	protected void AddSpawn(L2Player player, L2NpcInstance mob, int TimeToLive)
	{
		synchronized (Spawn_List)
		{
			Spawn_List.add(new Spawn(mob, player, TimeToLive));
		}
	}

	protected L2NpcInstance FindMySpawn(L2Player player, int npcId)
	{
		if(npcId == 0 || player == null)
			return null;
		synchronized (Spawn_List)
		{
			for(Spawn spawn : Spawn_List)
				if(spawn._char_ref.get() == player && spawn.npcId == npcId)
					return spawn.getNPC();
		}
		return null;
	}

	protected void DeleteSpawn(L2Player player, int npcId)
	{
		if(npcId == 0 || player == null)
			return;
		synchronized (Spawn_List)
		{
			Iterator<Spawn> it = Spawn_List.iterator();
			while(it.hasNext())
			{
				Spawn spawn = it.next();
				if(spawn._char_ref.get() == player && spawn.npcId == npcId)
				{
					L2NpcInstance npc = spawn.getNPC();
					if(npc != null)
						npc.deleteMe();
					it.remove();
				}
			}
		}
	}

	protected void DeleteMySpawn(L2Player player, int npcId)
	{
		if(npcId > 0 && player != null)
			DeleteSpawn(player, npcId);
	}

	protected L2NpcInstance spawn(int id, Location loc)
	{
		L2NpcTemplate template = NpcTable.getTemplate(id);
		L2Spawn spawn;
		try
		{
			spawn = new L2Spawn(template);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		spawn.setLoc(loc);
		L2NpcInstance npc = spawn.doSpawn(true);
		spawn.stopRespawn();
		return npc;
	}

	public void giveHallishaMark(QuestState st2)
	{
		if(L2ObjectsStorage.getNpc(st2.getInt("Archon")) != null)
			return; // Не убили, или убили чужого

		QuestTimer qt = st2.getQuestTimer("Archon Hellisha has despawned");
		if(qt != null)
		{
			qt.cancel();
			qt = null;
		}

		if(st2.getQuestItemsCount(Items[3]) < 700)
			st2.giveItems(Items[3], Rnd.get(1, 4));
		else
		{
			st2.takeItems(Items[3], 20);
			L2NpcInstance Archon = spawn(Mob[1], st2.getPlayer().getLoc());
			AddSpawn(st2.getPlayer(), Archon, 600000);
			int ArchonId = Archon.getObjectId();
			st2.set("Archon", str(ArchonId));
			startQuestTimer("Archon Hellisha has despawned", 600000, Archon, st2.getPlayer());
			Archon.setRunning();
			Archon.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, new Object[] { st2.getPlayer(), 100000 });
			AutoChat(Archon, Text[13].replace("PLAYERNAME", st2.getPlayer().getName()));
		}
	}

	protected QuestState findRightState(L2Player player, L2NpcInstance npc)
	{
		if(player == null || npc == null)
			return null;

		synchronized (Spawn_List)
		{
			for(Spawn spawn : Spawn_List)
				if(spawn._char_ref.get() == player && spawn.getNPC() == npc)
					return player.getQuestState(getName());

			for(Spawn spawn : Spawn_List)
				if(spawn.getNPC() == npc)
				{
					player = spawn._char_ref.get();
					return player == null ? null : player.getQuestState(getName());
				}
		}

		return null;
	}

	public static QuestState findQuest(L2Player player)
	{
		QuestState st = null;
		for(Integer q : Quests.keySet())
		{
			st = player.getQuestState(Quests.get(q));
			if(st != null)
			{
				int[] qc = QuestClass[q - 67];
				for(int c : qc)
					if(player.getClassId().getId() == c)
						return st;
			}
		}
		return null;
	}

	public static void process_step_15to16(QuestState st)
	{
		if(st == null || st.getInt("cond") != 15)
			return;
		int Halishas_Mark = ((SagasSuperclass) st.getQuest()).Items[3];
		int Resonance_Amulet = ((SagasSuperclass) st.getQuest()).Items[8];

		st.takeItems(Halishas_Mark, -1);
		if(st.getQuestItemsCount(Resonance_Amulet) == 0)
			st.giveItems(Resonance_Amulet, 1);
		st.set("cond", "16");
		st.playSound(SOUND_MIDDLE);
	}

	protected void AutoChat(L2NpcInstance npc, String text)
	{
		if(npc != null)
			Functions.npcSay(npc, text);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = ""; // simple initialization...if none of the events match, return nothing.
		L2Player player = st.getPlayer();

		if(event.equalsIgnoreCase("0-011.htm") || event.equalsIgnoreCase("0-012.htm") || event.equalsIgnoreCase("0-013.htm") || event.equalsIgnoreCase("0-014.htm") || event.equalsIgnoreCase("0-015.htm"))
			htmltext = event;
		else if(event.equalsIgnoreCase("accept"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(Items[10], 1);
			htmltext = "0-03.htm";
		}
		else if(event.equalsIgnoreCase("0-1"))
		{
			if(player.getLevel() < 76)
			{
				htmltext = "0-02.htm";
				st.exitCurrentQuest(true);
			}
			else
				htmltext = "0-05.htm";
		}
		else if(event.equalsIgnoreCase("0-2"))
		{
			if(player.getLevel() >= 76)
			{
				htmltext = "0-07.htm";
				st.takeItems(Items[10], -1);
				FinishQuest(st, player);
			}
			else
			{
				st.takeItems(Items[10], -1);
				st.playSound(SOUND_MIDDLE);
				st.set("cond", "20");
				htmltext = "0-08.htm";
			}
		}
		else if(event.equalsIgnoreCase("1-3"))
		{
			st.set("cond", "3");
			htmltext = "1-05.htm";
		}
		else if(event.equalsIgnoreCase("1-4"))
		{
			st.set("cond", "4");
			st.takeItems(Items[0], 1);
			if(Items[11] != 0)
				st.takeItems(Items[11], 1);
			st.giveItems(Items[1], 1);
			htmltext = "1-06.htm";
		}
		else if(event.equalsIgnoreCase("2-1"))
		{
			st.set("cond", "2");
			htmltext = "2-05.htm";
		}
		else if(event.equalsIgnoreCase("2-2"))
		{
			st.set("cond", "5");
			st.takeItems(Items[1], 1);
			st.giveItems(Items[4], 1);
			htmltext = "2-06.htm";
		}
		else if(event.equalsIgnoreCase("3-5"))
			htmltext = "3-07.htm";
		else if(event.equalsIgnoreCase("3-6"))
		{
			st.set("cond", "11");
			htmltext = "3-02.htm";
		}
		else if(event.equalsIgnoreCase("3-7"))
		{
			st.set("cond", "12");
			htmltext = "3-03.htm";
		}
		else if(event.equalsIgnoreCase("3-8"))
		{
			st.set("cond", "13");
			st.takeItems(Items[2], 1);
			st.giveItems(Items[7], 1);
			htmltext = "3-08.htm";
		}
		else if(event.equalsIgnoreCase("4-1"))
			htmltext = "4-010.htm";
		else if(event.equalsIgnoreCase("4-2"))
		{
			st.giveItems(Items[9], 1);
			st.set("cond", "18");
			st.playSound(SOUND_MIDDLE);
			htmltext = "4-011.htm";
		}
		else if(event.equalsIgnoreCase("4-3"))
		{
			st.giveItems(Items[9], 1);
			st.set("cond", "18");
			st.set("Quest0", "0");
			st.playSound(SOUND_MIDDLE);
			L2NpcInstance Mob_2 = FindMySpawn(player, NPC[4]);
			if(Mob_2 != null)
			{
				AutoChat(Mob_2, Text[13].replace("PLAYERNAME", player.getName()));
				DeleteMySpawn(player, NPC[4]);
				QuestTimer qt = st.getQuestTimer("Mob_2 has despawned");
				if(qt != null)
					qt.cancel();
				qt = st.getQuestTimer("NPC_4 Timer");
				if(qt != null)
					qt.cancel();
			}
			return null;
		}
		else if(event.equalsIgnoreCase("5-1"))
		{
			st.set("cond", "6");
			st.takeItems(Items[4], 1);
			Cast(st.findTemplate(NPC[5]), player, 4546, 1);
			st.playSound(SOUND_MIDDLE);
			htmltext = "5-02.htm";
		}
		else if(event.equalsIgnoreCase("6-1"))
		{
			st.set("cond", "8");
			st.takeItems(Items[5], 1);
			Cast(st.findTemplate(NPC[6]), player, 4546, 1);
			st.playSound(SOUND_MIDDLE);
			htmltext = "6-03.htm";
		}
		else if(event.equalsIgnoreCase("7-1"))
		{
			if(FindMySpawn(player, Mob[0]) == null)
			{
				L2NpcInstance Mob_1 = spawn(Mob[0], new Location(X[0], Y[0], Z[0]));
				AddSpawn(player, Mob_1, 180000);
				startQuestTimer("Mob_0 Timer", 500L, Mob_1, player);
				startQuestTimer("Mob_1 has despawned", 120000L, Mob_1, player);
				htmltext = "7-02.htm";
			}
			else
				htmltext = "7-03.htm";
		}
		else if(event.equalsIgnoreCase("7-2"))
		{
			st.set("cond", "10");
			st.takeItems(Items[6], 1);
			Cast(st.findTemplate(NPC[7]), player, 4546, 1);
			st.playSound(SOUND_MIDDLE);
			htmltext = "7-06.htm";
		}
		else if(event.equalsIgnoreCase("8-1"))
		{
			st.set("cond", "14");
			st.takeItems(Items[7], 1);
			Cast(st.findTemplate(NPC[8]), player, 4546, 1);
			st.playSound(SOUND_MIDDLE);
			htmltext = "8-02.htm";
		}
		else if(event.equalsIgnoreCase("9-1"))
		{
			st.set("cond", "17");
			st.takeItems(Items[8], 1);
			Cast(st.findTemplate(NPC[9]), player, 4546, 1);
			st.playSound(SOUND_MIDDLE);
			htmltext = "9-03.htm";
		}
		else if(event.equalsIgnoreCase("10-1"))
		{
			if(st.getInt("Quest0") == 0 || FindMySpawn(player, NPC[4]) == null)
			{
				DeleteMySpawn(player, NPC[4]);
				DeleteMySpawn(player, Mob[2]);
				st.set("Quest0", "1");
				st.set("Quest1", "45");

				L2NpcInstance NPC_4 = spawn(NPC[4], new Location(X[2], Y[2], Z[2]));
				L2NpcInstance Mob_2 = spawn(Mob[2], new Location(X[1], Y[1], Z[1]));
				AddSpawn(player, Mob_2, 300000);
				AddSpawn(player, NPC_4, 300000);
				startQuestTimer("Mob_2 Timer", 1000, Mob_2, player);
				startQuestTimer("Mob_2 despawn", 59000, Mob_2, player);
				startQuestTimer("NPC_4 Timer", 500, NPC_4, player);
				startQuestTimer("NPC_4 despawn", 60000, NPC_4, player);
				htmltext = "10-02.htm";
			}
			else if(st.getInt("Quest1") == 45)
				htmltext = "10-03.htm";
			else if(st.getInt("Tab") == 1)
			{
				L2NpcInstance Mob_2 = FindMySpawn(player, NPC[4]);
				if(Mob_2 == null || !st.getPlayer().knowsObject(Mob_2))
				{
					DeleteMySpawn(player, NPC[4]);
					Mob_2 = spawn(NPC[4], new Location(X[2], Y[2], Z[2]));
					AddSpawn(player, Mob_2, 300000);
					st.set("Quest0", "1");
					st.set("Quest1", "0"); // На всякий случай
					QuestTimer qt = st.getQuestTimer("NPC_4 despawn");
					if(qt != null)
					{
						qt.cancel();
						qt = null;
					}
					startQuestTimer("NPC_4 despawn", 180000, Mob_2, player);
				}
				htmltext = "10-04.htm";
			}
		}
		else if(event.equalsIgnoreCase("10-2"))
		{
			st.set("cond", "19");
			st.takeItems(Items[9], 1);
			Cast(st.findTemplate(NPC[10]), player, 4546, 1);
			st.playSound(SOUND_MIDDLE);
			htmltext = "10-06.htm";
		}
		else if(event.equalsIgnoreCase("11-9"))
		{
			st.set("cond", "15");
			htmltext = "11-03.htm";
		}
		else if(event.equalsIgnoreCase("Mob_0 Timer"))
		{
			AutoChat(FindMySpawn(player, Mob[0]), Text[0].replace("PLAYERNAME", player.getName()));
			return null;
		}
		else if(event.equalsIgnoreCase("Mob_1 has despawned"))
		{
			AutoChat(FindMySpawn(player, Mob[0]), Text[1].replace("PLAYERNAME", player.getName()));
			DeleteMySpawn(player, Mob[0]);
			return null;
		}
		else if(event.equalsIgnoreCase("Archon Hellisha has despawned"))
		{
			AutoChat(npc, Text[6].replace("PLAYERNAME", player.getName()));
			DeleteMySpawn(player, Mob[1]);
			return null;
		}
		else if(event.equalsIgnoreCase("Mob_2 Timer"))
		{
			L2NpcInstance NPC_4 = FindMySpawn(player, NPC[4]);
			L2NpcInstance Mob_2 = FindMySpawn(player, Mob[2]);
			if(NPC_4.knowsObject(Mob_2))
			{
				NPC_4.setRunning();
				NPC_4.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, Mob_2, null);
				Mob_2.setRunning();
				Mob_2.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, NPC_4, null);
				AutoChat(Mob_2, Text[14].replace("PLAYERNAME", player.getName()));
			}
			else
				startQuestTimer("Mob_2 Timer", 1000, npc, player);
			return null;
		}
		else if(event.equalsIgnoreCase("Mob_2 despawn"))
		{
			L2NpcInstance Mob_2 = FindMySpawn(player, Mob[2]);
			AutoChat(Mob_2, Text[15].replace("PLAYERNAME", player.getName()));
			st.set("Quest0", "2");
			if(Mob_2 != null)
				Mob_2.reduceCurrentHp(9999999, Mob_2, null, true, true, false, false, false, 9999999, true, false, false, false);
			DeleteMySpawn(player, Mob[2]);
			return null;
		}
		else if(event.equalsIgnoreCase("NPC_4 Timer"))
		{
			AutoChat(FindMySpawn(player, NPC[4]), Text[7].replace("PLAYERNAME", player.getName()));
			startQuestTimer("NPC_4 Timer 2", 1500, npc, player);
			if(st.getInt("Quest1") == 45)
				st.set("Quest1", "0");
			return null;
		}
		else if(event.equalsIgnoreCase("NPC_4 Timer 2"))
		{
			AutoChat(FindMySpawn(player, NPC[4]), Text[8].replace("PLAYERNAME", player.getName()));
			startQuestTimer("NPC_4 Timer 3", 10000, npc, player);
			return null;
		}
		else if(event.equalsIgnoreCase("NPC_4 Timer 3"))
		{
			if(st.getInt("Quest0") == 0)
			{
				startQuestTimer("NPC_4 Timer 3", 13000, npc, player);
				AutoChat(FindMySpawn(player, NPC[4]), Text[Rnd.get(9, 10)].replace("PLAYERNAME", player.getName()));
			}
			return null;
		}
		else if(event.equalsIgnoreCase("NPC_4 despawn"))
		{
			st.set("Quest1", str(st.getInt("Quest1") + 1));
			L2NpcInstance NPC_4 = FindMySpawn(player, NPC[4]);
			if(st.getInt("Quest0") == 1 || st.getInt("Quest0") == 2 || st.getInt("Quest1") > 3)
			{
				st.set("Quest0", "0");
				AutoChat(NPC_4, Text[Rnd.get(11, 12)].replace("PLAYERNAME", player.getName()));
				if(NPC_4 != null)
					NPC_4.reduceCurrentHp(9999999, NPC_4, null, true, true, false, false, false, 9999999, true, false, false, false);
				DeleteMySpawn(player, NPC[4]);
			}
			else
				startQuestTimer("NPC_4 despawn", 1000, npc, player);
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		L2Player player = st.getPlayer();
		if(player.getClassId().getId() != getPrevClass(player))
		{
			st.exitCurrentQuest(true);
			return htmltext;
		}

		if(cond == 0)
		{
			if(npcId == NPC[0])
				htmltext = "0-01.htm";
		}
		else if(cond == 1)
		{
			if(npcId == NPC[0])
				htmltext = "0-04.htm";
			else if(npcId == NPC[2])
				htmltext = "2-01.htm";
		}
		else if(cond == 2)
		{
			if(npcId == NPC[2])
				htmltext = "2-02.htm";
			else if(npcId == NPC[1])
				htmltext = "1-01.htm";
		}
		else if(cond == 3)
		{
			if(npcId == NPC[1])
				if(st.getQuestItemsCount(Items[0]) > 0)
				{
					if(Items[11] == 0)
						htmltext = "1-03.htm";
					else if(st.getQuestItemsCount(Items[11]) > 0)
						htmltext = "1-03.htm";
					else
						htmltext = "1-02.htm";
				}
				else
					htmltext = "1-02.htm";
		}
		else if(cond == 4)
		{
			if(npcId == NPC[1])
				htmltext = "1-04.htm";
			else if(npcId == NPC[2])
				htmltext = "2-03.htm";
		}
		else if(cond == 5)
		{
			if(npcId == NPC[2])
				htmltext = "2-04.htm";
			else if(npcId == NPC[5])
				htmltext = "5-01.htm";
		}
		else if(cond == 6)
		{
			if(npcId == NPC[5])
				htmltext = "5-03.htm";
			else if(npcId == NPC[6])
				htmltext = "6-01.htm";
		}
		else if(cond == 7)
		{
			if(npcId == NPC[6])
				htmltext = "6-02.htm";
		}
		else if(cond == 8)
		{
			if(npcId == NPC[6])
				htmltext = "6-04.htm";
			else if(npcId == NPC[7])
				htmltext = "7-01.htm";
		}
		else if(cond == 9)
		{
			if(npcId == NPC[7])
				htmltext = "7-05.htm";
		}
		else if(cond == 10)
		{
			if(npcId == NPC[7])
				htmltext = "7-07.htm";
			else if(npcId == NPC[3])
				htmltext = "3-01.htm";
		}
		else if(cond == 11 || cond == 12)
		{
			if(npcId == NPC[3])
				if(st.getQuestItemsCount(Items[2]) > 0)
					htmltext = "3-05.htm";
				else
					htmltext = "3-04.htm";
		}
		else if(cond == 13)
		{
			if(npcId == NPC[3])
				htmltext = "3-06.htm";
			else if(npcId == NPC[8])
				htmltext = "8-01.htm";
		}
		else if(cond == 14)
		{
			if(npcId == NPC[8])
				htmltext = "8-03.htm";
			else if(npcId == NPC[11])
				htmltext = "11-01.htm";
		}
		else if(cond == 15)
		{
			if(npcId == NPC[11])
				htmltext = "11-02.htm";
			else if(npcId == NPC[9])
				htmltext = "9-01.htm";
		}
		else if(cond == 16)
		{
			if(npcId == NPC[9])
				htmltext = "9-02.htm";
		}
		else if(cond == 17)
		{
			if(npcId == NPC[9])
				htmltext = "9-04.htm";
			else if(npcId == NPC[10])
				htmltext = "10-01.htm";
		}
		else if(cond == 18)
		{
			if(npcId == NPC[10])
				htmltext = "10-05.htm";
		}
		else if(cond == 19)
		{
			if(npcId == NPC[10])
				htmltext = "10-07.htm";
			if(npcId == NPC[0])
				htmltext = "0-06.htm";
		}
		else if(cond == 20)
			if(npcId == NPC[0])
				if(player.getLevel() >= 76)
				{
					htmltext = "0-09.htm";
					if(getClassId(player) < 131 || getClassId(player) > 135)
						FinishQuest(st, player);
				}
				else
					htmltext = "0-010.htm";
		return htmltext;
	}

	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		String htmltext = "";
		QuestState st = player.getQuestState(getName());
		if(st == null)
			return htmltext;
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(npcId == NPC[4])
			if(cond == 17)
			{
				QuestState st2 = findRightState(player, npc);
				if(st2 != null)
					if(st == st2)
					{
						if(st.getInt("Tab") == 1)
						{
							if(st.getInt("Quest0") == 0)
								htmltext = "4-04.htm";
							else if(st.getInt("Quest0") == 1)
								htmltext = "4-06.htm";
						}
						else if(st.getInt("Quest0") == 0)
							htmltext = "4-01.htm";
						else if(st.getInt("Quest0") == 1)
							htmltext = "4-03.htm";
					}
					else if(st.getInt("Tab") == 1)
					{
						if(st.getInt("Quest0") == 0)
							htmltext = "4-05.htm";
						else if(st.getInt("Quest0") == 1)
							htmltext = "4-07.htm";
					}
					else if(st.getInt("Quest0") == 0)
						htmltext = "4-02.htm";
			}
			else if(cond == 18)
				htmltext = "4-08.htm";
		return htmltext;
	}

	@Override
	public String onAttack(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getPlayer();
		if(st.getInt("cond") == 17)
			if(npc.getNpcId() == Mob[2])
			{
				QuestState st2 = findRightState(player, npc);
				if(st == st2)
				{
					st.set("Quest0", str(st.getInt("Quest0") + 1));
					if(st.getInt("Quest0") == 1)
						AutoChat(npc, Text[16].replace("PLAYERNAME", player.getName()));
					if(st.getInt("Quest0") > 15)
					{
						st.set("Quest0", "1");
						AutoChat(npc, Text[17].replace("PLAYERNAME", player.getName()));
						npc.reduceCurrentHp(9999999, npc, null, true, true, false, false, false, 9999999, true, false, false, false);
						DeleteMySpawn(player, Mob[2]);
						QuestTimer qt = st.getQuestTimer("Mob_2 despawn");
						if(qt != null)
						{
							qt.cancel();
							qt = null;
						}
						st.set("Tab", "1");
					}
				}
			}
		return null;
	}

	protected boolean isArchonMinions(int npcId)
	{
		for(int id : Archon_Minions)
			if(id == npcId)
				return true;
		return false;
	}

	protected boolean isArchonHellishaNorm(int npcId)
	{
		for(int id : Archon_Hellisha_Norm)
			if(id == npcId)
				return true;
		return false;
	}

	protected boolean isGuardianAngels(int npcId)
	{
		for(int id : Guardian_Angels)
			if(id == npcId)
				return true;
		return false;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		L2Player player = st.getPlayer();
		if(player.getActiveClassId() != getPrevClass(player))
			return null;

		if(isArchonMinions(npcId))
		{
			L2Party party = player.getParty();
			if(party != null)
			{
				for(L2Player player1 : party.getPartyMembers())
					if(player1.getDistance(player) <= ConfigValue.AltPartyDistributionRange)
					{
						QuestState st1 = findQuest(player1);
						if(st1 != null && st1.getCond() == 15)
							((SagasSuperclass) st1.getQuest()).giveHallishaMark(st1);
					}
			}
			else
			{
				QuestState st1 = findQuest(player);
				if(st1 != null && st1.getCond() == 15)
					((SagasSuperclass) st1.getQuest()).giveHallishaMark(st1);
			}
		}
		else if(isArchonHellishaNorm(npcId))
		{
			QuestState st1 = findQuest(player);
			if(st1 != null)
				if(st1.getInt("cond") == 15)
				{
					// This is just a guess....not really sure what it actually says, if anything
					AutoChat(npc, ((SagasSuperclass) st1.getQuest()).Text[4].replace("PLAYERNAME", st1.getPlayer().getName()));
					process_step_15to16(st1);
				}
		}
		else if(isGuardianAngels(npcId))
		{
			QuestState st1 = findQuest(player);
			if(st1 != null)
				if(st1.getInt("cond") == 6)
					if(st1.getInt("kills") < 9)
						st1.set("kills", str(st1.getInt("kills") + 1));
					else
					{
						st1.playSound(SOUND_MIDDLE);
						st1.giveItems(((SagasSuperclass) st1.getQuest()).Items[5], 1);
						st1.set("cond", "7");
					}
		}
		else
		{
			int cond = st.getInt("cond");
			if(npcId == Mob[0] && cond == 8)
			{
				QuestState st2 = findRightState(player, npc);
				if(st2 != null)
				{
					if(!player.isInParty())
						if(st == st2)
						{
							AutoChat(npc, Text[12].replace("PLAYERNAME", player.getName()));
							st.giveItems(Items[6], 1);
							st.set("cond", "9");
							st.playSound(SOUND_MIDDLE);
						}
					QuestTimer qt = st.getQuestTimer("Mob_1 has despawned");
					if(qt != null)
					{
						qt.cancel();
						qt = null;
					}
					DeleteMySpawn(st2.getPlayer(), Mob[0]);
				}
			}
			else if(npcId == Mob[1] && cond == 15)
			{
				QuestState st2 = findRightState(player, npc);
				if(st2 != null)
				{
					if(!player.isInParty())
						if(st == st2)
						{
							AutoChat(npc, Text[4].replace("PLAYERNAME", player.getName()));
							process_step_15to16(st);
						}
						else
							AutoChat(npc, Text[5].replace("PLAYERNAME", player.getName()));
					QuestTimer qt = st.getQuestTimer("Archon Hellisha has despawned");
					if(qt != null)
					{
						qt.cancel();
						qt = null;
					}
					DeleteMySpawn(st2.getPlayer(), Mob[1]);
				}
			}
			else if(npcId == Mob[2] && cond == 17)
			{
				QuestState st2 = findRightState(player, npc);
				if(st == st2)
				{
					st.set("Quest0", "1");
					AutoChat(npc, Text[17].replace("PLAYERNAME", player.getName()));
					npc.reduceCurrentHp(9999999, npc, null, true, true, false, false, false, 9999999, true, false, false, false);
					DeleteMySpawn(player, Mob[2]);
					QuestTimer qt = st.getQuestTimer("Mob_2 despawn");
					if(qt != null)
					{
						qt.cancel();
						qt = null;
					}
					st.set("Tab", "1");
				}
			}
		}
		return null;
	}
}
