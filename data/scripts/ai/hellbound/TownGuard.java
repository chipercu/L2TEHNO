package ai.hellbound;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

/**
 * AI Town Guard в городе-инстанте на Hellbound<br>
 * - перед тем как броситься в атаку кричат
 *
 * @author SYS
 */
public class TownGuard extends Fighter
{
	public TownGuard(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onIntentionAttack(L2Character target)
	{
		L2NpcInstance actor = getActor();
		if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(50))
			Functions.npcSay(actor, "Invader!");
		super.onIntentionAttack(target);
	}
}