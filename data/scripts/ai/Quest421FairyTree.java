package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;

public class Quest421FairyTree extends Fighter
{
	public Quest421FairyTree(L2Character actor)
	{
		super(actor);
		actor.p_block_move(true, null);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill sk)
	{
		L2NpcInstance actor = getActor();
		if(actor != null && attacker != null && !attacker.isPet() && attacker.isPlayer())
		{
			L2Skill skill = SkillTable.getInstance().getInfo(5423, 12);
			skill.getEffects(actor, attacker, false, false);
		}
		else
			super.ATTACKED(attacker, damage, sk);
	}
	
	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}