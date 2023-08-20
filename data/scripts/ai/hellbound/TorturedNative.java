package ai.hellbound;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

/**
 * AI Tortured Native в городе-инстанте на Hellbound<br>
 * - периодически кричат
 *
 * @author SYS
 */
public class TorturedNative extends Fighter
{
	public TorturedNative(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor.isDead())
			return true;

		if(Rnd.chance(1))
			if(Rnd.chance(10))
				Functions.npcSay(actor, "Eeeek... I feel sick... yow...!");
			else
				Functions.npcSay(actor, "It... will... kill... everyone...!");

		return super.thinkActive();
	}
}