package l2open.gameserver.skills.effects;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.skills.Env;
import l2open.util.GArray;
import l2open.util.Rnd;

public class EffectDiscord extends L2Effect
{
	public EffectDiscord(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		boolean multitargets = _skill.isAoE();

		if(!_effected.isMonster())
		{
			if(!multitargets)
				getEffector().sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return false;
		}

		if(_effected.isFearImmune() || _effected.isRaid() || _effected.isEpicRaid())
		{
			if(!multitargets)
				getEffector().sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return false;
		}

		// Discord нельзя наложить на осадных саммонов
		if(_effected instanceof L2Summon && ((L2Summon) _effected).isSiegeWeapon())
		{
			if(!multitargets)
				getEffector().sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return false;
		}

		if(_effected.isInZonePeace())
		{
			if(!multitargets)
				getEffector().sendPacket(Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
			return false;
		}

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effector.removeFromHatelist((L2NpcInstance)_effected, true);
		_effected.abortAttack(true, false);
		_effected.abortCast(true);
		ThreadPoolManager.getInstance().schedule(new RunAttack(),600);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effector.addDamageHate(((L2NpcInstance)_effected), 0, Rnd.get(500, 700));
		_effected.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, _effector, null);
		_effected.setRunning();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}

	public class RunAttack extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			GArray<L2Character> targetList = new GArray<L2Character>();

			for(L2Character character : _effected.getAroundCharacters(900, 300))
				if(character.isNpc() && character != getEffected())
					targetList.add(character);

			// if there is no target, exit function
			if(targetList.size() == 0)
			{
				return;
			}

			// Choosing randomly a new target
			L2Character target = targetList.get(Rnd.get(targetList.size()));

			// Attacking the target
			target.addDamageHate(((L2NpcInstance)_effected), 0, 1500); // Это нужно, чтобы гвард не перестал атаковать цель после первых ударов
			_effected.setRunning();
			_effected.getAI().setAttackTarget(target); // На всякий случай, не обязательно делать
			_effected.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, target, null); // Переводим в состояние атаки
			_effected.getAI().addTaskAttack(target); // Добавляем отложенное задание атаки, сработает в самом конце движения
		}
	}
}