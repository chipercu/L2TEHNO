package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.FinishRotating;
import l2open.gameserver.serverpackets.StartRotating;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {i_align_direction;80}
 * @i_align_direction
 * @80 - шанс прохождения эффекта, расчитывается по общей формуле дебафов.
 **/
/**
 * @author : Diagod
 **/
public class i_align_direction extends L2Effect
{
	public i_align_direction(Env env, EffectTemplate template, Integer chance)
	{
		super(env, template);
		_instantly = true;
		env.value = chance;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(Formulas.calcSkillSuccess(_env, getEffector().getChargedSpiritShot(), false))
		{
			getEffected().broadcastPacket2(new StartRotating(getEffected(), getEffected().getHeading(), 1, 65535));
			getEffected().broadcastPacket2(new FinishRotating(getEffected(), getEffector().getHeading(), 65535));
			getEffected().setHeading(getEffector().getHeading());
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}