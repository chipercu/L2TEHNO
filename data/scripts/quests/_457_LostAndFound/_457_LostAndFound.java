package quests._457_LostAndFound;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

import java.util.concurrent.ScheduledFuture;

/**
 * Quest for Monastery of the Silence.
 * @author Drizzy
 * @date 05.07.2011
 */
public class _457_LostAndFound extends Quest implements ScriptFile
{
	private ScheduledFuture<?> FollowTask;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _457_LostAndFound()
	{
		super(true);
		addStartNpc(32759);
	}

	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		L2Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("lost_villager_q0457_06.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			npc.setFollowTarget(player);
			if(npc.getFollowTarget() != null)
			{
				if(FollowTask != null)
					FollowTask.cancel(false);
				FollowTask = null;
				FollowTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Follow(npc, player, st), 10, 1000);
			}
		}
		return event;
	}

	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int state = st.getState();
		int cond = st.getCond();
		if(npcId == 32759)
		{
			if(state == 1)
			{
				if(npc.getFollowTarget() != null && npc.getFollowTarget() != player)
					return "lost_villager_q0457_01a.htm";
				String req = (st.getPlayer().getVar("NextQuest457") == null || st.getPlayer().getVar("NextQuest457").equalsIgnoreCase("null")) ? "0" : st.getPlayer().getVar("NextQuest457");
               if(!st.isNowAvailable())
					return "nextday.htm";
				if(st.getPlayer().getLevel() >= 82)
					return "lost_villager_q0457_01.htm";
				return "lvl.htm";
			}
			if (state == 2)
			{
				if(npc.getFollowTarget() != null && npc.getFollowTarget() != player)
					return "lost_villager_q0457_01a.htm";
				if(cond == 2)
				{
					st.giveItems(15716, 1);
					st.playSound(SOUND_FINISH);
					st.setState(CREATED);
					st.exitCurrentQuest(this);
					npc.setFollowTarget(null);
					npc.deleteMe();
					return "lost_villager_q0457_09.htm";
				}
				if(cond == 1)
					return "lost_villager_q0457_08.htm";
			}
		}
		return "noquest";
	}

	private void checkInRadius(int id, QuestState st, L2NpcInstance npc)
	{
		L2NpcInstance quest0457 = L2ObjectsStorage.getByNpcId(id);
		if(npc.getRealDistance3D(quest0457) <= 150)
		{
			st.setCond(2);
			if(FollowTask != null)
				FollowTask.cancel(false);
			FollowTask = null;
			npc.stopMove();
		}
	}

	private class Follow extends l2open.common.RunnableImpl
	{
		private L2NpcInstance _npc;
		private L2Player player;
		private QuestState st;

		private Follow(L2NpcInstance npc, L2Player pl, QuestState _st)
		{
			_npc = npc;
			player = pl;
			st = _st;
		}

		public void runImpl()
		{
			_npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player, 250);
			checkInRadius(32764, st, _npc);
		}
	}
}