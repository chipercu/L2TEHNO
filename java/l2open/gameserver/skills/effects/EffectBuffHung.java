package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Env;

public final class EffectBuffHung extends L2Effect
{
	public EffectBuffHung(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.BuffHung;
	}

	@Override
	public void onStart()
	{
		// Nevit's Hourglass
		
		if (getEffected().isPlayer() && getSkill().getId() >= 9115 && getSkill().getId() <= 9149)
		{
			((L2Player) getEffected()).getRecommendation().stopRecBonus();
			super.onStart();
		}
	}
	
	@Override
	public void onExit()
	{
		// Nevit's Hourglass
		if (getEffected().isPlayer()/* && getSkill().getId() >= 9115 && getSkill().getId() <= 9149*/)
		{
			((L2Player) getEffected()).getRecommendation().startRecBonus();
			((L2Player) getEffected()).sendUserInfo(true);
			super.onExit();
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}