package ai.dragonvalley;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.NpcUtils;
import l2open.util.Rnd;

public class DragonKnight extends Fighter
{
	public DragonKnight(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		super.MY_DYING(killer);
		switch(getActor().getNpcId())
		{
			case 22844:
				if(Rnd.chance(50))
				{
					L2NpcInstance n = NpcUtils.spawnSingle(22845, getActor().getLoc());
					n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
				}
				break;
			case 22845:
				if(Rnd.chance(50))
				{
					L2NpcInstance n = NpcUtils.spawnSingle(22846, getActor().getLoc());
					n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
				}
				break;
		}

	}
}