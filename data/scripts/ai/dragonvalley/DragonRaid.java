package ai.dragonvalley;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.instances.L2NpcInstance;

public class DragonRaid extends Fighter
{
	private long lastAttackTime = 0;

	public DragonRaid(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		lastAttackTime = System.currentTimeMillis();
	}

	@Override
	protected boolean thinkActive()
	{
		super.thinkActive();
		if(lastAttackTime != 0 && lastAttackTime + 30 * 60 * 1000L < System.currentTimeMillis())
			getActor().deleteMe();
		return true;
	}
}