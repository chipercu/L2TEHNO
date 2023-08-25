package l2open.gameserver.skills.pts_effects;

import l2open.config.ConfigValue;
import l2open.gameserver.model.*;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.SkillTrait;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.skills.funcs.*;

/**
 * {p_attack_trait;trait_valakas;20}
 **/
/**
 * @author : Diagod
 **/
public class p_attack_trait extends L2Effect
{
	private SkillTrait _trait;
	private double _val;

	public p_attack_trait(Env env, EffectTemplate template, SkillTrait trait, Double value)
	{
		super(env, template);

		_trait = trait;
		_val = value;
	}

	/*
		v9 = (double *)sub_53E1D4((__int64)v3 + 6408, *((_DWORD *)a1 + 4));
		*v9 = (*((double *)v4 + 3) + 100.0) / 100.0 * *v9;
		
		double add = (val+100)/100*cur_val;
	*/
	@Override
	public void onStart()
	{
		super.onStart();
		if(ConfigValue.DebuffFormulaType == 1)
		{
			double val = _val;
			if(val == 100)
				val = 99d;
			val = (100d - val) / 100d;
			_effected.getTraitStat().modTrait(_trait.ordinal(), 1, val, false);
		}
		else
			_effected.getTraitStat().modTrait(_trait.ordinal(), 0, _val, false);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if(ConfigValue.DebuffFormulaType == 1)
		{
			double val = _val;
			if(val == 100)
				val = 99d;
			val = 100d / (100d - val);
			_effected.getTraitStat().modTrait(_trait.ordinal(), 1, val, false);
		}
		else
			_effected.getTraitStat().modTrait(_trait.ordinal(), 0, _val*-1, false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}