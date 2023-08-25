package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {i_delete_hate_of_me;80}
 * @i_delete_hate_of_me
 * @80 - шанс прохождения эффекта, расчитывается по общей формуле дебафов.
 * Работает только на НПС...
 **/
/**
 * @author : Diagod
 **/
public class i_delete_hate_of_me extends L2Effect
{
	public i_delete_hate_of_me(Env env, EffectTemplate template, Integer chance)
	{
		super(env, template);
		_instantly = true;
		env.value = chance;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(getEffected().isNpc() && Formulas.calcSkillSuccess(_env, getEffector().getChargedSpiritShot(), false))
		{
			L2NpcInstance npc = (L2NpcInstance) _effected;
			_effector.removeFromHatelist(npc, true);
			npc.abortAttack(true, false);
			npc.abortCast(true);
			npc.getAI().clearTasks();
			npc.getAI().setAttackTarget(null);
			if(npc.isNoTarget())
			{
				npc.getAI().setGlobalAggro(System.currentTimeMillis() + 10000);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			}
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}