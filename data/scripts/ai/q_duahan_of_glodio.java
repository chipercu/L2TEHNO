package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.*;
import l2open.util.Rnd;

/**
 *@author: Diagod
 */

public class q_duahan_of_glodio extends Fighter
{
	public q_duahan_of_glodio(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		L2Player c0 = GetCreatureFromIndex(getActor().param1).getPlayer();
		if(IsNullCreature(c0) == 0)
			Say(MakeFString(70855,c0.getName(),"","","",""));
	}
}
