package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncPTS;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {i_cp;63;diff}
 * @i_cp
 * @-63 - Количество добавляемого ЦП.
 * @diff - что делаем, добавляем статик или процент.
 **/
/**
 * @author : Diagod
 **/
public class i_cp extends L2Effect
{
	private double _cp;
	private FuncPTS _act;

	public i_cp(Env env, EffectTemplate template, Double cp, FuncPTS act)
	{
		super(env, template);

		_cp = cp;
		_act = act;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isDead() || _effected.block_hp.get())
			return;

		_cp = _act.calc(_cp, _effected.getMaxCp());

		if(_cp > 0)
		{
			if(_effected.isHealBlocked(true, false))
				return;
			_cp = Math.max(0, Math.min(_cp, _effected.calcStat(Stats.CP_LIMIT, null, null) * _effected.getMaxCp() / 100. - _effected.getCurrentCp()));
			
			_effected.sendPacket(new SystemMessage(SystemMessage.S1_WILL_RESTORE_S2S_CP).addNumber((long) _cp));
		}

		_effected.setCurrentCp(_effected.getCurrentCp() + _cp);
	}


	@Override
	public boolean onActionTime()
	{
		return false;
	}
}