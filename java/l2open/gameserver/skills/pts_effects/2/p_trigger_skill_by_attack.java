package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.*;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * @author : Diagod
 **/
/**
 * {{p_trigger_skill_by_attack;{pk;1;40};0;{1;100;diff};[s_trigger_pvp_weapon_cancel1];target;{all}}}
 * p_trigger_skill_by_attack - срабатывает только от обычных физ атак...
 * {pk;1;40} - pk = тип таргета игрок/все обьекты, 1 = минимальный уровень цели, 100 = максимальный уровнь цели.
 * 0 - тип срабатывания, 0 - от дамага, 1 - от крита.
 * {1;100;diff} - 1 = минимальный дамаг для срабатывания тригера, 100 = шанс тригера, diff(per) = количество дамага или % от макс ХП.
 * [s_trigger_pvp_weapon_cancel1] - сам тригер, который сработает.
 * target - тип цели, на которую будет скастован тригер.
 * {all} - оружие с которым сработает тригер.
 **/
public class p_trigger_skill_by_attack extends L2Effect
{
	public p_trigger_skill_by_attack(Env env, EffectTemplate template)
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
