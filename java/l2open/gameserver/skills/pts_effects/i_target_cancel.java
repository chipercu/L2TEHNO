package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {i_target_cancel;80}
 * @i_target_cancel
 * @80 - шанс прохождения эффекта, расчитывается по общей формуле дебафов.
 **/
/**
 * @author : Diagod
 **/
public class i_target_cancel extends L2Effect
{
	public i_target_cancel(Env env, EffectTemplate template, Integer chance)
	{
		super(env, template);
		_instantly = true;
		env.value = chance;
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().getCastingSkill() != null && (getEffected().getCastingSkill().getSkillType() == SkillType.TAKECASTLE || getEffected().getCastingSkill().getSkillType() == SkillType.TAKEFORTRESS || getEffected().getCastingSkill().getSkillType() == SkillType.TAKEFLAG))
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(Formulas.calcSkillSuccess(_env, getEffector().getChargedSpiritShot(), false))
		{
			if(!getEffected().isPlayable())
			{
				getEffected().getAI().setAttackTarget(null);
				getEffected().stopMove(true, true);
				//getEffected().getAI().clearTasks();
			}
			if(getEffected().getTarget() != null)
			{
				getEffected().stopMove(true, true);
				getEffected().abortAttack(true, true);
				getEffected().abortCast(true);
			}
			getEffected().setTarget(null);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}