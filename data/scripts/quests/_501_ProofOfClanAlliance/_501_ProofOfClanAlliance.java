package quests._501_ProofOfClanAlliance;

import java.util.Arrays;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.model.quest.QuestTimer;
import l2open.gameserver.tables.SkillTable;
import l2open.util.GArray;
import l2open.util.Rnd;

public class _501_ProofOfClanAlliance extends Quest implements ScriptFile
{
	// Quest Npcs
	private static final int SIR_KRISTOF_RODEMAI = 30756;
	private static final int STATUE_OF_OFFERING = 30757;
	private static final int WITCH_ATHREA = 30758;
	private static final int WITCH_KALIS = 30759;

	// Quest Items
	private static final short HERB_OF_HARIT = 3832;
	private static final short HERB_OF_VANOR = 3833;
	private static final short HERB_OF_OEL_MAHUM = 3834;
	private static final short BLOOD_OF_EVA = 3835;
	private static final short SYMBOL_OF_LOYALTY = 3837;
	private static final short PROOF_OF_ALLIANCE = 3874;
	private static final short VOUCHER_OF_FAITH = 3873;
	private static final short ANTIDOTE_RECIPE = 3872;
	private static final short POTION_OF_RECOVERY = 3889;

	// Quest mobs, drop, rates and prices
	private static final int[] CHESTS = { 27173, 27174, 27175, 27176, 27177 };
	private static final int[][] MOBS = { { 20685, HERB_OF_VANOR }, { 20644, HERB_OF_HARIT },
			{ 20576, HERB_OF_OEL_MAHUM } };

	private static final short RATE = 35;
	// stackable items paid to retry chest game: (default 10k adena)
	private static final short RETRY_PRICE = 10000;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _501_ProofOfClanAlliance()
	{
		super(PARTY_NONE);

		addStartNpc(SIR_KRISTOF_RODEMAI);
		addStartNpc(STATUE_OF_OFFERING);
		addStartNpc(WITCH_ATHREA);

		addTalkId(WITCH_KALIS);

		addQuestItem(SYMBOL_OF_LOYALTY);
		addQuestItem(ANTIDOTE_RECIPE);

		for(int[] i : MOBS)
		{
			addKillId(i[0]);
			addQuestItem(i[1]);
		}

		for(int i : CHESTS)
			addKillId(i);
	}

	public QuestState getLeader(QuestState st)
	{
		L2Clan clan = st.getPlayer().getClan();
		QuestState leader = null;
		if(clan != null && clan.getLeader() != null && clan.getLeader().getPlayer() != null)
			leader = clan.getLeader().getPlayer().getQuestState(getName());
		return leader;
	}

	public void removeQuestFromMembers(QuestState st, boolean leader)
	{
		removeQuestFromOfflineMembers(st);
		removeQuestFromOnlineMembers(st, leader);
	}

	public void removeQuestFromOfflineMembers(QuestState st)
	{
		if(st.getPlayer() == null || st.getPlayer().getClan() == null)
		{
			st.exitCurrentQuest(true);
			return;
		}

		int clan = st.getPlayer().getClan().getClanId();

		ThreadConnection con = null;
		FiltredPreparedStatement offline = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("DELETE FROM character_quests WHERE name = ? AND char_id IN (SELECT obj_id FROM characters WHERE clanId = ? AND online = 0)");
			offline.setString(1, getName());
			offline.setInt(2, clan);
			offline.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, offline);
		}
	}

	public void removeQuestFromOnlineMembers(QuestState st, boolean leader)
	{
		if(st.getPlayer() == null || st.getPlayer().getClan() == null)
		{
			st.exitCurrentQuest(true);
			return;
		}

		QuestState l;
		L2Player pleader = null;

		if(leader)
		{
			l = getLeader(st);
			if(l != null)
				pleader = l.getPlayer();
		}

		if(pleader != null)
		{
			//pleader.p_block_move(false, null);
			pleader.getEffectList().stopEffect(4082);
		}
		for(L2Player pl : st.getPlayer().getClan().getOnlineMembers(st.getPlayer().getClan().getLeaderId()))
			if(pl != null && pl.getQuestState(getName()) != null)
				pl.getQuestState(getName()).exitCurrentQuest(true);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		if(st.getPlayer() == null || st.getPlayer().getClan() == null)
		{
			st.exitCurrentQuest(true);
			return "noquest";
		}

		QuestState leader = getLeader(st);
		if(leader == null)
		{
			removeQuestFromMembers(st, true);
			return "Quest Failed";
		}

		String htmltext = event;

		/* ##### Leaders area ###### */
		if(st.getPlayer().isClanLeader())
		{
			// SIR_KRISTOF_RODEMAI
			if(event.equalsIgnoreCase("30756-03.htm"))
			{
				st.set("cond", "1");
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
			}

			// WITCH_KALIS
			else if(event.equalsIgnoreCase("30759-03.htm"))
			{
				st.set("cond", "2");
				st.set("dead_list", " ");
			}
			else if(event.equalsIgnoreCase("30759-07.htm"))
			{
				st.takeItems(SYMBOL_OF_LOYALTY, -1);
				st.giveItems(ANTIDOTE_RECIPE, 1);
				st.addNotifyOfDeath(st.getPlayer());
				st.set("cond", "3");
				st.set("chest_count", "0");
				st.set("chest_game", "0");
				st.set("chest_try", "0");
				st.startQuestTimer("poison_timer", 3600000);
				st.getPlayer().altUseSkill(SkillTable.getInstance().getInfo(4082, 1), st.getPlayer());
				//st.getPlayer().p_block_move(true, null);
				htmltext = "30759-07.htm";
			}
		}

		// Timers
		if(event.equalsIgnoreCase("poison_timer"))
		{
			removeQuestFromMembers(st, true);
			htmltext = "30759-09.htm";
		}
		else if(event.equalsIgnoreCase("chest_timer"))
		{
			htmltext = "";
			if(leader.getInt("chest_game") < 2)
				stop_chest_game(st);
		}

		/* ##### Members area ###### */

		// STATUE_OF_OFFERING
		else if(event.equalsIgnoreCase("30757-04.htm"))
		{
			GArray<String> deadlist = new GArray<String>();
			deadlist.addAll(Arrays.asList(leader.get("dead_list").split(" ")));
			deadlist.add(st.getPlayer().getName());
			String deadstr = "";
			for(String s : deadlist)
				deadstr += s + " ";
			leader.set("dead_list", deadstr);
			st.addNotifyOfDeath(leader.getPlayer());
			if(Rnd.chance(50))
				st.getPlayer().reduceCurrentHp(st.getPlayer().getCurrentHp() * 8, st.getPlayer(), null, true, true, false, false, false, st.getPlayer().getCurrentHp() * 8, true, false, false, false);
			st.giveItems(SYMBOL_OF_LOYALTY, 1);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30757-05.htm"))
			st.exitCurrentQuest(true);

		// WITCH_ATHREA
		else if(event.equalsIgnoreCase("30758-03.htm"))
			start_chest_game(st);
		else if(event.equalsIgnoreCase("30758-07.htm"))
			if(st.getQuestItemsCount(ADENA_ID) < RETRY_PRICE)
				htmltext = "30758-06.htm";
			else
				st.takeItems(ADENA_ID, RETRY_PRICE);

		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");

		if(st.getPlayer() == null || st.getPlayer().getClan() == null)
		{
			st.exitCurrentQuest(true);
			return htmltext;
		}

		QuestState leader = getLeader(st);
		if(leader == null)
		{
			removeQuestFromMembers(st, true);
			return "Quest Failed";
		}

		int npcId = npc.getNpcId();
		if(npcId == SIR_KRISTOF_RODEMAI)
		{
			if(!st.getPlayer().isClanLeader())
			{
				st.exitCurrentQuest(true);
				return "30756-10.htm";
			}
			else if(st.getPlayer().getClan().getLevel() <= 2)
			{
				st.exitCurrentQuest(true);
				return "30756-08.htm";
			}
			else if(st.getPlayer().getClan().getLevel() >= 4)
			{
				st.exitCurrentQuest(true);
				return "30756-09.htm";
			}
			else if(st.getQuestItemsCount(VOUCHER_OF_FAITH) > 0)
			{
				st.playSound(SOUND_FANFARE2);
				st.takeItems(VOUCHER_OF_FAITH, -1);
				st.giveItems(PROOF_OF_ALLIANCE, 1);
				st.addExpAndSp(0, 120000);
				htmltext = "30756-07.htm";
				st.exitCurrentQuest(true);
			}
			else if(cond == 1 || cond == 2)
				return "30756-06.htm";
			else if(st.getQuestItemsCount(PROOF_OF_ALLIANCE) == 0)
			{
				st.set("cond", "0");
				return "30756-01.htm";
			}
			else
			{
				st.exitCurrentQuest(true);
				return htmltext;
			}
		}
		else if(npcId == WITCH_KALIS)
		{
			if(st.getPlayer().isClanLeader())
			{
				if(cond == 1)
					return "30759-01.htm";
				else if(cond == 2)
				{
					htmltext = "30759-05.htm";
					if(st.getQuestItemsCount(SYMBOL_OF_LOYALTY) == 3)
					{
						int deads = 0;
						try
						{
							deads = st.get("dead_list").split(" ").length;
						}
						finally
						{
							if(deads == 3)
								htmltext = "30759-06.htm";
						}
					}
				}
				else if(cond == 3)
					if(st.getQuestItemsCount(HERB_OF_HARIT) > 0 && st.getQuestItemsCount(HERB_OF_VANOR) > 0 && st.getQuestItemsCount(HERB_OF_OEL_MAHUM) > 0 && st.getQuestItemsCount(BLOOD_OF_EVA) > 0 && st.getQuestItemsCount(ANTIDOTE_RECIPE) > 0)
					{
						st.takeItems(ANTIDOTE_RECIPE, 1);
						st.takeItems(HERB_OF_HARIT, 1);
						st.takeItems(HERB_OF_VANOR, 1);
						st.takeItems(HERB_OF_OEL_MAHUM, 1);
						st.takeItems(BLOOD_OF_EVA, 1);
						st.giveItems(POTION_OF_RECOVERY, 1);
						st.giveItems(VOUCHER_OF_FAITH, 1);
						QuestTimer timer = leader.getQuestTimer("poison_timer");
						if(timer != null)
							timer.cancel();
						removeQuestFromMembers(st, false);
						//st.getPlayer().p_block_move(false, null);
						st.getPlayer().getEffectList().stopEffect(4082);
						st.set("cond", "4");
						st.playSound(SOUND_FINISH);
						return "30759-08.htm";
					}
					else if(st.getQuestItemsCount(VOUCHER_OF_FAITH) == 0)
						return "30759-10.htm";
			}
			else if(leader.getInt("cond") == 3)
				return "30759-11.htm";
		}
		else if(npcId == STATUE_OF_OFFERING)
		{
			if(st.getPlayer().isClanLeader())
				return "30757-03.htm";
			else if(st.getPlayer().getLevel() <= 39)
			{
				st.exitCurrentQuest(true);
				return "30757-02.htm";
			}
			else
			{
				String[] dlist;
				int deads;
				try
				{
					dlist = leader.get("dead_list").split(" ");
					deads = dlist.length;
				}
				catch(Exception e)
				{
					removeQuestFromMembers(st, true);
					return "Who are you?";
				}
				if(deads < 3)
				{
					for(String str : dlist)
						if(st.getPlayer().getName().equalsIgnoreCase(str))
							return "you cannot die again!";
					return "30757-01.htm";
				}
			}
		}
		else if(npcId == WITCH_ATHREA)
		{
			if(st.getPlayer().isClanLeader())
				return "30757-03.htm";

			// Проверяем, участвует ли в квесте
			String[] dlist;
			try
			{
				dlist = leader.get("dead_list").split(" ");
			}
			catch(Exception e)
			{
				st.exitCurrentQuest(true);
				return "Who are you?";
			}
			Boolean flag = false;
			if(dlist != null)
				for(String str : dlist)
					if(st.getPlayer().getName().equalsIgnoreCase(str))
						flag = true;
			if(!flag)
			{
				st.exitCurrentQuest(true);
				return "Who are you?";
			}

			int game_state = leader.getInt("chest_game");
			if(game_state == 0)
			{
				if(leader.getInt("chest_try") == 0)
					return "30758-01.htm";
				return "30758-05.htm";
			}
			else if(game_state == 1)
				return "30758-09.htm";
			else if(game_state == 2)
			{
				st.playSound(SOUND_FINISH);
				st.giveItems(BLOOD_OF_EVA, 1);
				QuestTimer timer = leader.getQuestTimer("chest_timer");
				if(timer != null)
					timer.cancel();
				stop_chest_game(st);
				leader.set("chest_game", "3");
				return "30758-08.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getPlayer() == null || st.getPlayer().getClan() == null)
		{
			st.exitCurrentQuest(true);
			return "noquest";
		}

		QuestState leader = getLeader(st);
		if(leader == null)
		{
			removeQuestFromMembers(st, true);
			return "Quest Failed";
		}

		// first part, general checking
		int npcId = npc.getNpcId();

		QuestTimer timer = leader.getQuestTimer("poison_timer");
		if(timer == null)
		{
			stop_chest_game(st);
			return "Quest Failed";
		}

		// second part, herbs gathering
		for(int[] m : MOBS)
			if(npcId == m[0] && st.getInt(String.valueOf(m[1])) == 0)
				if(Rnd.chance(RATE))
				{
					st.giveItems(m[1], 1);
					leader.set(String.valueOf(m[1]), "1");
					st.playSound(SOUND_MIDDLE);
					return null;
				}

		// third part, chest game
		for(int i : CHESTS)
			if(npcId == i)
			{
				timer = leader.getQuestTimer("chest_timer");
				if(timer == null)
				{
					stop_chest_game(st);
					return "Time is up!";
				}
				if(Rnd.chance(25))
				{
					Functions.npcSay(npc, "###### BINGO! ######");
					int count = leader.getInt("chest_count");
					if(count < 4)
					{
						count += 1;
						leader.set("chest_count", String.valueOf(count));
					}
					if(count >= 4)
					{
						stop_chest_game(st);
						leader.set("chest_game", "2");
						timer.cancel();
						st.playSound(SOUND_MIDDLE);
					}
					else
						st.playSound(SOUND_ITEMGET);
				}
				return null;
			}
		return null;
	}

	public void start_chest_game(QuestState st)
	{
		if(st.getPlayer() == null || st.getPlayer().getClan() == null)
		{
			st.exitCurrentQuest(true);
			return;
		}

		QuestState leader = getLeader(st);
		if(leader == null)
		{
			removeQuestFromMembers(st, true);
			return;
		}

		leader.set("chest_game", "1");
		leader.set("chest_count", "0");
		int attempts = leader.getInt("chest_try");
		leader.set("chest_try", String.valueOf(attempts + 1));

		for(L2NpcInstance npc : L2ObjectsStorage.getAllByNpcId(CHESTS, false))
			npc.deleteMe();

		for(int n = 1; n <= 5; n++)
			for(int i : CHESTS)
				leader.addSpawn(i, 102100, 103450, -3400, 0, 100, 60000);
		leader.startQuestTimer("chest_timer", 60000);
	}

	public void stop_chest_game(QuestState st)
	{
		QuestState leader = getLeader(st);
		if(leader == null)
		{
			removeQuestFromMembers(st, true);
			return;
		}

		for(L2NpcInstance npc : L2ObjectsStorage.getAllByNpcId(CHESTS, false))
			npc.deleteMe();

		leader.set("chest_game", "0");
	}

	@Override
	public String onDeath(L2Character npc, L2Playable pc, QuestState st)
	{
		if(st.getPlayer() == null || st.getPlayer().getClan() == null)
		{
			st.exitCurrentQuest(true);
			return null;
		}

		QuestState leader = getLeader(st);
		if(leader == null)
		{
			removeQuestFromMembers(st, true);
			return null;
		}

		if(st.getPlayer() == pc)
		{
			QuestTimer timer1 = leader.getQuestTimer("poison_timer");
			QuestTimer timer2 = leader.getQuestTimer("chest_timer");
			if(timer1 != null)
				timer1.cancel();
			if(timer2 != null)
				timer2.cancel();

			removeQuestFromMembers(st, true);
		}
		return null;
	}
}