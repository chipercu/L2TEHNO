package quests._700_CursedLife;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import quests._10273_GoodDayToFly._10273_GoodDayToFly;

public class _700_CursedLife extends Quest implements ScriptFile
{
	private int Orbyu = 32560;
	private static int[] Mobs = { 22602, 22603, 22604, 22605 };
	private int Rok = 25624;
	
	private int Swallowed_Skull = 13872;
	private int Swallowed_Sternum = 13873;
	private int Swallowed_Bones = 13874;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_EPILOGUE;
	
	public _700_CursedLife()
	{
		super(true);
		
		addStartNpc(Orbyu);
		for (int mob : Mobs)
	    	addKillId(mob);
		addKillId(Rok);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32560-03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32560-quit.htm"))
		{
			st.exitCurrentQuest(true);
			st.playSound(SOUND_FINISH);
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
		if(npcId == Orbyu)
		{
			if(id == CREATED)
			{
				QuestState qs = player.getQuestState(_10273_GoodDayToFly.class);
				if(qs != null && player.getLevel() >= 75 && qs.isCompleted())
					return "32560-01.htm";
				else
				{
					st.exitCurrentQuest(true);
					return "32560-00.htm";
				}
			}
			else if(cond == 1)
			{
				long Skull = st.getQuestItemsCount(Swallowed_Skull);
				long Sternum = st.getQuestItemsCount(Swallowed_Sternum);
				long Bones = st.getQuestItemsCount(Swallowed_Bones);
				if(Skull + Sternum + Bones > 0)
				{
					st.giveItems(ADENA_ID, 50 * Skull + 100 * Sternum + 150 * Bones);
					st.takeItems(Swallowed_Skull, -1);
					st.takeItems(Swallowed_Sternum, -1);
					st.takeItems(Swallowed_Bones, -1);
					return "32560-04.htm";
				}
				return  "32560-03.htm";
			}
			else if(cond == 2)
				return "30760-06.htm";
			else if(cond == 3)
				return "30760-08a.htm";
		}
		
		return "noquest";
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getState() != STARTED)
			return null;
		int npcId = npc.getNpcId();

		if(npcId == Rok)
		{
			if(st.getQuestItemsCount(Swallowed_Sternum) == 0)
			{
				st.rollAndGive(Swallowed_Sternum, 1, 80);
				st.playSound(SOUND_ITEMGET);
			}
			else if(st.getQuestItemsCount(Swallowed_Skull) == 0)
			{
				st.rollAndGive(Swallowed_Skull, 1, 80);
				st.playSound(SOUND_ITEMGET);
			}
		}
		else if(contains(Mobs, npcId))
		{
			st.rollAndGive(Swallowed_Bones, (int) (1 * ConfigValue.RateQuestsRewardDrop), 80);
			st.playSound("ItemSound.quest_itemget");
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