package ai;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;

/**
 * @author: Drizzy
 * АИ для шариков у анаиса.
 */
public class ai_grail_protection extends DefaultAI
{
	private L2Character myself = null;
	private int Skill01_ID = 6326;
	private int TIME_TO_DIE = 50001;
	private int TIME_TO_FOLLOW = 50002;
	private int TIME_EXPLODE = 5003;
	private int DIST_CHECK = 5004;
	private int i_ai1 = 0;
	private L2Character c0;

	public ai_grail_protection(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		//ChangeStatus(2);
		AddTimerEx(TIME_TO_FOLLOW,100);
		AddTimerEx(TIME_TO_DIE,((2 * 60) * 1000));
		AddTimerEx(DIST_CHECK,100);
		c0 = L2ObjectsStorage.getCharacter(id);
		if(IsNullCreature(c0) == 0)
		{
			getActor().setTitle("****" + c0.getName() + "****");
			getActor().updateAbnormalEffect();
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(getActor() == null)
			return;
		if(script_event_arg1 == 2114007)
		{
			getActor().doDie(getActor());
		}
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character caster, L2Character target)
	{
		AddTimerEx(TIME_EXPLODE,(100));
		super.onEvtFinishCasting(skill, caster, target);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(getActor() == null)
			return;
		AddTimerEx(TIME_TO_FOLLOW,(100));
		if(timer_id == TIME_TO_FOLLOW)
		{
			if(id != 0)
			{
				if(IsNullCreature(c0) == 0)
				{
					getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, c0, 100);
					getActor().getAI().setAttackTarget(c0); // На всякий случай, не обязательно делать
					getActor().getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, c0, null); // Переводим в состояние атаки
					getActor().getAI().addTaskAttack(c0); // Добавляем отложенное задание атаки, сработает в самом конце движения
				}
			}
		}
		else if(timer_id == TIME_TO_DIE)
		{
			getActor().doDie(getActor());
		}
		else if(timer_id == TIME_EXPLODE)
		{
			getActor().doDie(getActor());
		}
		else if(timer_id == DIST_CHECK)
		{
			AddTimerEx(DIST_CHECK,(100));
			if(id != 0)
			{
				if(getActor().getRealDistance3D(c0) < 100)
				{
					getActor().setTitle(c0.getName());
					getActor().updateAbnormalEffect();
					getActor().doCast(SkillTable.getInstance().getInfo(Skill01_ID, 1), c0, true);
				}
				else if(getActor().getRealDistance3D(c0) > 200000)
					getActor().doDie(getActor());
				else if(getActor().getRealDistance3D(c0) < 150 && i_ai1 == 2)
				{
					i_ai1 = 3;
					getActor().setTitle("*" + c0.getName() + "*");
					getActor().updateAbnormalEffect();
				}
				else if(getActor().getRealDistance3D(c0) < 200 && i_ai1 == 1)
				{
					i_ai1 = 2;
					getActor().setTitle("**" + c0.getName() + "**");
					getActor().updateAbnormalEffect();
				}
				else if(getActor().getRealDistance3D(c0) < 250 && i_ai1 == 0)
				{
					i_ai1 = 1;
					getActor().setTitle("***" + c0.getName() + "***");
					getActor().updateAbnormalEffect();
				}
			}
		}
	}
}
