package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;

public class Kama56Minion extends Fighter
{
	public Kama56Minion(L2Character actor)
	{
		super(actor);
		actor.setIsInvul(true);
	}

	@Override
	protected void onEvtAggression(L2Character attacker, int aggro)
	{
		if(aggro < 10000000)
			return;
		super.onEvtAggression(attacker, aggro);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{}
}