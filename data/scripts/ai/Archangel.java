package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;

public class Archangel extends Fighter
{
	public Archangel(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(attacker == null || actor == null)
			return;

		if(attacker.getLevel() > 82 || attacker.getLevel() < 68)
			return;
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void onEvtAggression(L2Character attacker, int aggro)
	{
		L2NpcInstance actor = getActor();
		if(attacker == null || actor == null)
			return;

		if(attacker.getLevel() > 82 || attacker.getLevel() < 68)
			return;
		super.onEvtAggression(attacker, aggro);
	}
}