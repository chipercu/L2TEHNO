package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;

/**
 * AI рейд босов Aenkinel в Delusion Chamber
 * @author SYS
 */
public class Aenkinel extends Fighter
{

	public Aenkinel(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		// Устанавливаем реюз для Tower и Great Seal 
		if(actor.getNpcId() == 25694 || actor.getNpcId() == 25695)
		{
			String refName = actor.getReflection().getName();
			for(L2Player p : L2World.getAroundPlayers(actor))
				if(p != null)
					p.setVarInst(refName, String.valueOf(System.currentTimeMillis()));
		}

		if(actor.getNpcId() == 25694)
			for(int i = 0; i < 4; i++)
				actor.getReflection().addSpawnWithoutRespawn(18820, actor.getLoc(), 250);
		else if(actor.getNpcId() == 25695)
			for(int i = 0; i < 4; i++)
				actor.getReflection().addSpawnWithoutRespawn(18823, actor.getLoc(), 250);

		super.MY_DYING(killer);
	}
}