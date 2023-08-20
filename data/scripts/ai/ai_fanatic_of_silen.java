package ai;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.util.Rnd;

/**
 *@author: Drizzy
 *АИ для мобов на пристани глудио.
 */

public class ai_fanatic_of_silen extends DefaultAI
{
	public ai_fanatic_of_silen(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		BroadcastScriptEvent(1000,getActor().getObjectId(),1200);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(attacker.isPlayable())
		{
			getActor().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		}
		else
		{
			attacker.addDamageHate(getActor(), 0, Rnd.get(50, 150)); // Это нужно, чтобы гвард не перестал атаковать цель после первых ударов
			getActor().setRunning();
			getActor().getAI().setAttackTarget(attacker); // На всякий случай, не обязательно делать
			getActor().getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker, null); // Переводим в состояние атаки
			getActor().getAI().addTaskAttack(attacker); // Добавляем отложенное задание атаки, сработает в самом конце движения
		}
		super.ATTACKED(attacker, damage, skill);
	}

}
