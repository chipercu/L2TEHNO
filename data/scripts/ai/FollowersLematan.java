package ai;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;

/**
 *  - AI мобов Followers Lematan, миньёны-лекари Боса Lematan в пайлаке 61-67.
 *  - Не умеют ходить, лечат Боса.
 */
public class FollowersLematan extends Fighter
{
	private static int LEMATAN = 18633;
	private L2NpcInstance _lematan = null;

	public FollowersLematan(L2Character actor)
	{
		super(actor);
		AI_TASK_DELAY = 1000;
		AI_TASK_ACTIVE_DELAY = 1000;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		for(L2NpcInstance npc : L2World.getAroundNpc(getActor(), 1000, 300))
			if(npc.getNpcId() == LEMATAN)
				_lematan = npc;

		ThreadPoolManager.getInstance().schedule(new ScheduleTimerTask("Skill", this), 15000);
	}

	public void onTimer(String event)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		if(event.equals("Skill"))
		{
			if(_lematan != null && _lematan.getNpcId() == LEMATAN)
			{
				actor.setTarget(_lematan);
				actor.doCast(SkillTable.getInstance().getInfo(5712, 1), _lematan, true);
			}
			ThreadPoolManager.getInstance().schedule(new ScheduleTimerTask("Skill", this), 15000);
		}
	}

	private class ScheduleTimerTask extends l2open.common.RunnableImpl
	{
		private String _name;
		private FollowersLematan _caller;

		public ScheduleTimerTask(String name, FollowersLematan classPtr)
		{
			_name = name;
			_caller = classPtr;
		}

		@Override
        public void runImpl()
		{
			_caller.onTimer(_name);
		}
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}