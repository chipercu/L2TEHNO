package quests._279_TargetOfOpportunity;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

import java.util.Arrays;

public class _279_TargetOfOpportunity extends Quest implements ScriptFile
{
	public void onLoad()
	{
	}

	public void onReload()
	{
	}

	public void onShutdown()
	{
	}

	public _279_TargetOfOpportunity()
	{
		super(PARTY_ALL);
		addStartNpc(npcJerian);
		addKillId(mobs);
	}

	private static final int npcJerian = 32302;
	private static final int[] mobs = {22373, 22374, 22375, 22376};
	private static final int[] itemsSealComponents = {15517, 15518, 15519, 15520};
	private static final int[] itemSealBreakers = {15515, 15516};

	@Override
	public String onTalk(L2NpcInstance npc, QuestState qs)
	{
		String htm = "noquest";
		L2Player player = qs.getPlayer();
		int id = qs.getState();
		if(id == CREATED)
		{
			if(player.getLevel() >= 82)
			{
				htm = "32302-01.htm";
			}
			else
			{
				htm = "32302-02.htm";
			}
		}
		else if(id == STARTED)
		{
			if(qs.getQuestItemsCount(itemsSealComponents[0]) > 0 && qs.getQuestItemsCount(itemsSealComponents[1]) > 0 && qs.getQuestItemsCount(itemsSealComponents[2]) > 0 && qs.getQuestItemsCount(itemsSealComponents[0]) > 0)
			{
				htm = "32302-07.htm";
			}
			else
			{
				htm = "32302-06.htm";
			}
		}
		return htm;
	}

	@Override
	public String onEvent(String event, QuestState qs, L2NpcInstance npc)
	{
		String htm = event;
		L2Player player = qs.getPlayer();
		if(player.getLevel() < 82)
		{
			htm = "32302-02.htm";
			qs.exitCurrentQuest(true);
		}
		if(event.equalsIgnoreCase("32302-05.htm"))
		{
			qs.setState(STARTED);
			qs.setCond(1);
			qs.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32302-08.htm") && qs.getQuestItemsCount(itemsSealComponents[0]) > 0 && qs.getQuestItemsCount(itemsSealComponents[1]) > 0 && qs.getQuestItemsCount(itemsSealComponents[2]) > 0 && qs.getQuestItemsCount(itemsSealComponents[0]) > 0)
		{
			qs.takeItems(itemsSealComponents[0], -1);
			qs.takeItems(itemsSealComponents[1], -1);
			qs.takeItems(itemsSealComponents[2], -1);
			qs.takeItems(itemsSealComponents[3], -1);
			qs.giveItems(itemSealBreakers[0], 1);
			qs.giveItems(itemSealBreakers[1], 1);
			qs.playSound(SOUND_FINISH);
			qs.exitCurrentQuest(true);
		}
		return htm;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState qs)
	{
		L2Player player = qs.getPlayer();
		int idx = Arrays.binarySearch(mobs, npc.getNpcId());
		if(player == null || idx < 0)
		{
			return null;
		}
		if(Rnd.get(1000) < 311)
		{
			if(qs.getQuestItemsCount(itemsSealComponents[idx]) < 1)
			{
				qs.giveItems(itemsSealComponents[idx], 1);
				if(haveAllExceptThis(qs, idx))
				{
					qs.setCond(2);
					qs.playSound(SOUND_MIDDLE);
				}
				else
				{
					qs.playSound(SOUND_ITEMGET);
				}
			}
		}
		return null;
	}

	private static final boolean haveAllExceptThis(QuestState st, int idx)
	{
		for(int i = 0; i < itemsSealComponents.length; i++)
		{
			if(i == idx)
			{
				continue;
			}
			if(st.getQuestItemsCount(itemsSealComponents[i]) < 1)
			{
				return false;
			}
		}
		return true;
	}
}