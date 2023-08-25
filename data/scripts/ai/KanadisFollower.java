package ai;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.SeducedInvestigatorInstance;
import l2open.util.GArray;
import l2open.util.Rnd;

/**
 * AI Kanadis Follower и минионов для Rim Pailaka
 */

public class KanadisFollower extends Fighter
{
	private int npcRnd;
	private GArray<L2NpcInstance> _massiveNpc = new GArray<L2NpcInstance>();

	public KanadisFollower(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		L2NpcInstance actor = getActor();
		GArray<L2NpcInstance> around = L2World.getAroundNpc(actor, 2500, 1000);
		if(around != null && !around.isEmpty())
		{
			for(L2NpcInstance npc : around)
			{
				if(npc instanceof SeducedInvestigatorInstance)
				{
					_massiveNpc.add(npc);
				}
			}
			if(!_massiveNpc.isEmpty())
			{
				npcRnd = Rnd.get(_massiveNpc.size());
			}
			if(npcRnd >= 0 && !_massiveNpc.isEmpty())
			{
				_massiveNpc.get(npcRnd).addDamageHate(actor, 0, Rnd.get(50, 150)); // Это нужно, чтобы гвард не перестал атаковать цель после первых ударов
				actor.setRunning();
				actor.getAI().setAttackTarget(_massiveNpc.get(npcRnd)); // На всякий случай, не обязательно делать
				actor.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, npcRnd, null); // Переводим в состояние атаки
				actor.getAI().addTaskAttack(_massiveNpc.get(npcRnd)); // Добавляем отложенное задание атаки, сработает в самом конце движения
			}
		}
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(attacker.getNpcId() == 36562)
		{
			actor.addDamageHate((L2NpcInstance)attacker, 0, 100);
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}

	@Override
	public boolean isNotReturnHome()
	{
		return true;
	}
}