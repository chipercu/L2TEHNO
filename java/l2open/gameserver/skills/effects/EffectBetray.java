package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.skills.Env;

import static l2open.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

public class EffectBetray extends L2Effect
{
	public EffectBetray(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isSummon() || _effected.isPet())
		{
			L2Summon summon = (L2Summon) _effected;
			summon.setPossessed(true);
			summon.getAI().Attack(summon.getPlayer(), true, false);
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if(_effected instanceof L2Summon)
		{
			L2Summon summon = (L2Summon) _effected;
			summon.setPossessed(false);
			summon.getAI().setIntention(AI_INTENTION_ACTIVE);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}