package ai.hellbound;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.instances.L2NpcInstance;

public class OutpostGuards extends Fighter
{
	public OutpostGuards(L2NpcInstance actor)
	{
		super(actor);
		actor.p_block_move(true, null);
	}
}