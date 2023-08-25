package ai;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;

public class PowderKeg extends DefaultAI
{
	boolean doCast = false;
	public PowderKeg(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(doCast)
			return;
		doCast = true;
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		actor.setTarget(attacker);
		actor.doCast(SkillTable.getInstance().getInfo(5714, 1), attacker, true);
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}
}