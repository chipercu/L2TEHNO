package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.*;
import l2open.util.Rnd;

/**
 *@author: Diagod
 */

public class q_bloody_senior extends Fighter
{
	public q_bloody_senior(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		L2Player c0 = GetCreatureFromIndex(getActor().param1).getPlayer();
		if(IsNullCreature(c0) == 0)
			Say(MakeFString(70955,c0.getName(),"","","",""));

		AddTimerEx(70902,(1000 * 120));
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 70902)
			Say(MakeFString(70957,"","","","",""));
		super.TIMER_FIRED_EX(timer_id, arg);
	}
}
