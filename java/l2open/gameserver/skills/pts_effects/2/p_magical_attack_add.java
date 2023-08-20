package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.skills.funcs.*;

/**
 * {p_magical_attack_add;{all};75;per};
 **/
/**
 * @author : Diagod
 **/
public class p_magical_attack_add extends L2Effect
{
	private double _val;
	private FuncCalc _func;

	public p_magical_attack_add(Env env, EffectTemplate template, String cond, Double value, FuncCalc func)
	{
		super(env, template);

		_val = value;
		_func = func;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		/*if(_func == FuncCalc.diff)
			getEffected().getStat().p_magical_attack_diff(_val);
		else
			getEffected().getStat().p_magical_attack_per(_val);*/
	}

	@Override
	public void onExit()
	{
		super.onExit();
		/*if(_func == FuncCalc.diff)
			getEffected().getStat().p_magical_attack_diff(-_val);
		else
			getEffected().getStat().p_magical_attack_per(-_val);*/
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}