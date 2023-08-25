package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;

/**
 * @author VISTALL
 * @date  11:31/18.11.2010
 */
public class KrateisFighter extends Fighter
{
	public KrateisFighter(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		super.MY_DYING(killer);

		L2Player player = killer.getPlayer();
		if(player == null)
			return;

		//KrateisCubeEvent cubeEvent = getActor().getEvent(KrateisCubeEvent.class);
		//if(cubeEvent == null)
			return;

		//KrateisCubePlayerObject particlePlayer = cubeEvent.getParticlePlayer(player);

		//particlePlayer.setPoints(particlePlayer.getPoints() + 3);
		//cubeEvent.updatePoints(particlePlayer);
	}
}
