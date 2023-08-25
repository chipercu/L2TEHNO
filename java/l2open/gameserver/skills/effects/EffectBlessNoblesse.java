package l2open.gameserver.skills.effects;

import l2open.config.*;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.Env;

public final class EffectBlessNoblesse extends L2Effect
{
	public EffectBlessNoblesse(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().setIsBlessedByNoblesse(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if(ConfigValue.DeleteNoblesseBlessing)
			getEffected().setIsBlessedByNoblesse(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}