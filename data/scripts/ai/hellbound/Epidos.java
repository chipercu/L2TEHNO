package ai.hellbound;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.instancemanager.NaiaCoreManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;

public class Epidos extends Fighter
{

	public Epidos(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		NaiaCoreManager.removeSporesAndSpawnCube();
		super.MY_DYING(killer);
	}
}