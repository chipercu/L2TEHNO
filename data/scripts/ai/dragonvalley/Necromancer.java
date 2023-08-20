package ai.dragonvalley;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Mystic;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.NpcUtils;
import l2open.util.Rnd;

public class Necromancer extends Mystic
{
	public Necromancer(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		super.MY_DYING(killer);
		if(Rnd.chance(30))
		{
			L2NpcInstance n = NpcUtils.spawnSingle(Rnd.chance(50) ? 22818 : 22819, getActor().getLoc());
			n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
		}
	}
}