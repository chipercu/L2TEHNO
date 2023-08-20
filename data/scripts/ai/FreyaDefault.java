package ai;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Mystic;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

/**
 * @author Diagod
 * 18.05.2011
 **/
public class FreyaDefault extends Mystic
{
	public FreyaDefault(L2Character actor)
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
		if(actor.getReflectionId() < 1)
			actor.deleteMe();
		if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
		{
			int size = actor.getReflection().getMonsters().size();
			if(size > 0)
			{
				L2NpcInstance mob = actor.getReflection().getMonsters().get(Rnd.get(size));
				if(!mobAtack(mob))
					for(int i=0;i<size;i++)
						if(mobAtack(actor.getReflection().getMonsters().get(i)))
							break;
			}
		}
		return true;
	}

	private boolean mobAtack(L2NpcInstance mob)
	{
		L2NpcInstance actor = getActor();
		if(mob != null)
		{
			if(mob.isDead())
				return false;
			if(mob.getNpcId() == 22767)
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
	public void thinkAttack()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		L2Character target;
		if((target = prepareTarget()) == null)
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			return;
		}
		if(target.isDead() || target.getNpcId() == 22767)
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			return;
		}
		super.thinkAttack();
	}
	
	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	public boolean isNotReturnHome()
	{
		return true;
	}
}