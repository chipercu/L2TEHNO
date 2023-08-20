package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Character.HateInfo;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2NpcInstance.AggroInfo;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.util.Rnd;

/**
 * {i_randomize_hate;80}
 * @i_randomize_hate
 * @80 - шанс прохождения эффекта.
 * Выбирает рандомную цель из своего агро листа, если там только 1 цель, то продолжает лупить ее...
 * Только вот не пойму, он берет максХейт у кого или рандомно из всего списка...
 **/
/**
 * @author : Diagod
 **/
public class i_randomize_hate extends L2Effect
{
	public i_randomize_hate(Env env, EffectTemplate template, Integer chance)
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
			L2NpcInstance monster = (L2NpcInstance)getEffected();
			//L2Character most_hated = monster.getAI().getAttackTarget();
			L2Character most_hated = monster.getMostHated();
			if(most_hated == null)
				return;
			HateInfo hate_max = most_hated.getHateList().get(monster);
			L2Character target = monster.getRandomHated();
			HateInfo hate_select = target.getHateList().get(monster);
			if(target == null)
				return;

			if(target != null && target != monster)
			{
				final int select_hate = hate_select.hate;
				final int max_hate = hate_max.hate;

				hate_select.hate = max_hate;
				hate_max.hate = select_hate;
				
				monster.getAI().setAttackTarget(target);
				monster.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, target, null);
				monster.getAI().addTaskAttack(target);
			}
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}