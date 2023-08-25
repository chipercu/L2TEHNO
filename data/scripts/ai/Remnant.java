package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2RemnantInstance;

public class Remnant extends Fighter
{
	public Remnant(L2Character actor)
	{
		super(actor);
	}

	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		if (skill.getId() != 2358)
		{
			return;
		}
		L2RemnantInstance actor = (L2RemnantInstance) getActor();
		if (actor == null || actor.getCurrentHp() == 0.5)
		{
			return;
		}
		actor.setBlessed(true);
		actor.doDie(actor);
	}
}