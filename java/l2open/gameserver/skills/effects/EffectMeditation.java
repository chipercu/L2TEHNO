package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.Env;

/**
 * Иммобилизует и парализует на время действия.
 * @author Diamond
 * @date 24.07.2007
 * @time 5:32:46
 */
public final class EffectMeditation extends L2Effect
{
	public EffectMeditation(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.block();
		_effected.setMeditated(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.unblock();
		_effected.setMeditated(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}