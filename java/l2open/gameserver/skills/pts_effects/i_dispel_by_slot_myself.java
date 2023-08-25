package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {i_dispel_by_slot_myself;ma_up}
 * @i_dispel_by_slot_myself
 * @ma_up - abnormal_type с которым будут сняты бафы/дебафы.
 **/
/**
 * @author : Diagod
 **/
public class i_dispel_by_slot_myself extends L2Effect
{
	private SkillAbnormalType _sat;

	public i_dispel_by_slot_myself(Env env, EffectTemplate template, SkillAbnormalType sat)
	{
		super(env, template);
		_sat = sat;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		for(L2Effect e : _effector.getEffectList().getAllEffects())
			if(e.getAbnormalType() == _sat)
			{
				e.setCanDelay(false);
				e.exit(false, false);
				update_effect_list = true;
			}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}