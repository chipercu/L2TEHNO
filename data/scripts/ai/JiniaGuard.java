package ai;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

/**
 * @author Diagod
 * 18.15.2011
 **/
public class JiniaGuard extends Fighter
{
	public JiniaGuard(L2Character actor)
	{
		super(actor);
		AI_TASK_DELAY = 1000;
		AI_TASK_ACTIVE_DELAY = 1000;
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return true;
		if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
		{
			try
			{
				if(actor.getNpcId() == 22767)
				{
					int size = actor.getReflection().getMonsters().size();
					L2NpcInstance mob = actor.getReflection().getMonsters().get(Rnd.get(size));
					if(!mobAtack(mob, true))
						for(int i=0;i<size;i++)
							if(mobAtack(actor.getReflection().getMonsters().get(i), true))
								break;
				}
				else
				{
					int size = actor.getReflection().getMonsters().size();
					L2NpcInstance mob = actor.getReflection().getMonsters().get(Rnd.get(size));
					if(!mobAtack(mob, false))
						for(int i=0;i<size;i++)
							if(mobAtack(actor.getReflection().getMonsters().get(i), false))
								break;
				}
			}
			catch(Exception e)
			{
				//actor.deleteMe();
			}
		}
		return true;
	}

	private boolean mobAtack(L2NpcInstance mob, boolean is)
	{
		L2NpcInstance actor = getActor();
		if(mob != null)
		{
			if(mob.isDead())
				return false;
			if(is && (mob.getNpcId() == 22767 || mob.getNpcId() == 18847))
				return false;
			else if(!is && (mob.getNpcId() == 18848 || mob.getNpcId() == 18849 || mob.getNpcId() == 18926))
				return false;
			mob.addDamageHate(actor, 0, Rnd.get(50, 150)); // Это нужно, чтобы гвард не перестал атаковать цель после первых ударов
			actor.setRunning(); // Включаем бег...
			actor.setAttackTimeout(Integer.MAX_VALUE + System.currentTimeMillis()); // Это нужно, чтобы не сработал таймаут
			actor.getAI().setAttackTarget(mob); // На всякий случай, не обязательно делать
			actor.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, mob, null); // Переводим в состояние атаки
			actor.getAI().addTaskAttack(mob); // Добавляем отложенное задание атаки, сработает в самом конце движения
			return true;
		}
		return false;
	}

	@Override
	public void onIntentionAttack(L2Character target)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		if(actor.getNpcId() == 18848 || actor.getNpcId() == 18849 || actor.getNpcId() == 18926)
		{
			if(target.getNpcId() == 18848 || target.getNpcId() == 18849 || target.getNpcId() == 18926 || target.isPlayer())
			{
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				return;
			}
		}
		else if(actor.getNpcId() == 22767)
		{
			if(target.getNpcId() == 22767 || target.getNpcId() == 18847)
			{
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				return;
			}
		}
		super.onIntentionAttack(target);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}