package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {i_dispel_by_name;{[s_br_herb_rose_red9]}}
 * @i_dispel_by_name
 * @s_br_herb_rose_red9 - имя скила, эффекты которого мы удаляем.
 **/
/**
 * @author : Diagod
 **/
public class i_dispel_by_name extends L2Effect
{
	private String _skill_name;

	public i_dispel_by_name(Env env, EffectTemplate template, String skill_name)
	{
		super(env, template);
		_skill_name = skill_name;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		for(L2Effect e : _effected.getEffectList().getAllEffects())
			if(e.getSkill().getName().equals(_skill_name))
			{
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