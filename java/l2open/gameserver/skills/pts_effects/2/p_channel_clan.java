package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.*;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * @author : Diagod
 **/
public class p_channel_clan extends L2Effect
{
	public p_channel_clan(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
