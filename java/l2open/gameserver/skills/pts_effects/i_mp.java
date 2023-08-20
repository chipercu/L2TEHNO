package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncPTS;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {i_mp;63;diff}
 * @i_mp
 * @-63 - Количество добавляемого МП.
 * @diff - что делаем, добавляем статик или процент.
 **/
/**
 * @author : Diagod
 **/
public class i_mp extends L2Effect
{
	private double _mp;
	private FuncPTS _act;

	public i_mp(Env env, EffectTemplate template, Double mp, FuncPTS act)
	{
		super(env, template);

		_mp = mp;
		_act = act;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isDead() || _effected.block_mp.get())
			return;

		_mp = _act.calc(_mp, _effected.getCurrentMp());

		if(_mp > 0)
		{
			if(_effected.isHealBlocked(true, false))
				return;
			//_mp = Math.max(0, _mp);
		}

		_effected.setCurrentMp(_effected.getCurrentMp() + _mp);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}