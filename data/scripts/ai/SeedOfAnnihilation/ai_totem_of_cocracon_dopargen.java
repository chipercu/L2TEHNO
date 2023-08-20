package ai.SeedOfAnnihilation;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * АИ для тотемов допагена. 1оо% ПТС.
 */
 /**
 03:02:40.036  Object array [ITEMS] resized: 580000 -> 590000
java.lang.NoSuchFieldException: type
        at java.lang.Class.getField(Unknown Source)
        at l2open.gameserver.model.L2Spawn.setAIField(L2Spawn.java:717)
        at l2open.gameserver.model.L2Spawn.intializeNpc(L2Spawn.java:475)
        at l2open.gameserver.model.L2Spawn.doSpawn(L2Spawn.java:455)
        at l2open.gameserver.model.L2Spawn.doSpawn(L2Spawn.java:407)
        at l2open.gameserver.ai.DefaultAI.CreateOnePrivateEx(DefaultAI.java:2064)
        at ai.SeedOfAnnihilation.ai_boss_dopagen.TIMER_FIRED_EX(ai_boss_dopagen.java:162)
        at l2open.gameserver.ai.AbstractAI.notifyEvent(AbstractAI.java:185)
        at l2open.gameserver.ai.AbstractAI.notifyEvent(AbstractAI.java:116)
        at l2open.gameserver.ai.L2CharacterAI$Timer.run(L2CharacterAI.java:361)
        at java.util.concurrent.Executors$RunnableAdapter.call(Unknown Source)
        at java.util.concurrent.FutureTask$Sync.innerRun(Unknown Source)
        at java.util.concurrent.FutureTask.run(Unknown Source)
        at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$301(Unknown Source)
        at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(Unknown Source)
        at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(Unknown Source)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
        at java.lang.Thread.run(Unknown Source)
java.lang.NoSuchFieldException: type
        at java.lang.Class.getField(Unknown Source)
        at l2open.gameserver.model.L2Spawn.setAIField(L2Spawn.java:717)
        at l2open.gameserver.model.L2Spawn.intializeNpc(L2Spawn.java:475)
        at l2open.gameserver.model.L2Spawn.doSpawn(L2Spawn.java:455)
        at l2open.gameserver.model.L2Spawn.doSpawn(L2Spawn.java:407)
        at l2open.gameserver.ai.DefaultAI.CreateOnePrivateEx(DefaultAI.java:2064)
        at ai.SeedOfAnnihilation.ai_boss_dopagen.TIMER_FIRED_EX(ai_boss_dopagen.java:162)
        at l2open.gameserver.ai.AbstractAI.notifyEvent(AbstractAI.java:185)
        at l2open.gameserver.ai.AbstractAI.notifyEvent(AbstractAI.java:116)
        at l2open.gameserver.ai.L2CharacterAI$Timer.run(L2CharacterAI.java:361)
        at java.util.concurrent.Executors$RunnableAdapter.call(Unknown Source)
        at java.util.concurrent.FutureTask$Sync.innerRun(Unknown Source)
        at java.util.concurrent.FutureTask.run(Unknown Source)
        at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$301(Unknown Source)
        at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(Unknown Source)
        at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(Unknown Source)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
        at java.lang.Thread.run(Unknown Source)

 **/
public class ai_totem_of_cocracon_dopargen extends DefaultAI
{
	private L2Character myself = null;
	private int S_TIMER = 1114;
	private int WEAK_SKILL_TIMER = 1115;
	private int WEAK_SKILL_CHECK_TIMER = 1116;
	private int display_skill = 6375;
	private int weak_skill = 6373;
	private int item_1hs = 15280;
	private int CHANGE_TIMER = 1117;
	private L2Character c_ai4 = null;
	public int type = 0;

	public ai_totem_of_cocracon_dopargen(L2Character actor)
	{
		super(actor);
		AI_TASK_ACTIVE_DELAY = 5000;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		if(type == 1)
			AddTimerEx(S_TIMER,(5 * 1000));
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == S_TIMER)
		{
			int i0 = Rnd.get(2);
			if(i0 == 0)
			{
				BroadcastScriptEvent(20091016,i0,1500);
				SendScriptEvent(getActor(), 20091016, i0);
			}
			else if(i0 == 1)
			{
				BroadcastScriptEvent(20091016,i0,1500);
				SendScriptEvent(getActor(), 20091016, i0);
			}
			AddTimerEx(S_TIMER,( 30 * 1000 ));
		}
		else if(timer_id == WEAK_SKILL_TIMER)
		{
			if(IsNullCreature(c_ai4) == 0)
			{
				if(getActor().getRealDistance3D(c_ai4) <= 150)
				{
					AddUseSkillDesire(c_ai4, SkillTable.getInstance().getInfo(weak_skill, 1), 1);
				}
				else
				{
					AddTimerEx(WEAK_SKILL_CHECK_TIMER, 1000);
				}
			}
		}
		else if(timer_id == WEAK_SKILL_CHECK_TIMER)
		{
			if(IsNullCreature(c_ai4) == 0)
			{
				if(getActor().getEffectList().getEffectsBySkillId(weak_skill) == null)
				{
					SendScriptEvent(c_ai4, 20091024, 0);
				}
				int i1 = Rnd.get(3);
				switch(i1)
				{
					case 0:
						CreateOnePrivateEx(22760,"Fighter",getActor().getX(),getActor().getY(),getActor().getZ()).getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, ((L2NpcInstance)c_ai4).getMostHated());
						break;
					case 1:
						CreateOnePrivateEx(22761,"Fighter",getActor().getX(),getActor().getY(),getActor().getZ()).getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, ((L2NpcInstance)c_ai4).getMostHated());
						break;
					case 2:
						CreateOnePrivateEx(22762, "Fighter", getActor().getX(), getActor().getY(), getActor().getZ()).getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, ((L2NpcInstance)c_ai4).getMostHated());
						break;
				}
			}
		}
		else if(timer_id == CHANGE_TIMER && getActor().getRightHandItem() != 0)
		{
			getActor().setRHandId(0);
			getActor().updateAbnormalEffect();
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 20091016 && script_event_arg2 == type)
		{
			if(getActor().getRightHandItem() != item_1hs)
			{
				getActor().setRHandId(item_1hs);
				getActor().updateAbnormalEffect();
			}
			AddUseSkillDesire(getActor(), SkillTable.getInstance().getInfo(display_skill, 1), 1);
			AddTimerEx(CHANGE_TIMER,( 10 * 1000 ));
		}
		else if(script_event_arg1 == 8)
		{
			c_ai4 = L2ObjectsStorage.getNpc(script_event_arg2);
		}
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character caster, L2Character target)
	{
		if(skill.getId() == display_skill)
		{
			AddTimerEx(WEAK_SKILL_TIMER,(5 * 1000));
		}
		else if(skill.getId() == weak_skill)
		{
			AddTimerEx(WEAK_SKILL_CHECK_TIMER,1000);
		}
		super.onEvtFinishCasting(skill, caster,target);
	}
}
