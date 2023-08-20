package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.Env;

public final class EffectResDebuff extends L2Effect
{
	public EffectResDebuff(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}