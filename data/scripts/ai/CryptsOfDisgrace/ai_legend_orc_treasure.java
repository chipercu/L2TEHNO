package ai.CryptsOfDisgrace;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;

/**
 * @author: Drizzy
 * @date: 21.08.2012
 */

public class ai_legend_orc_treasure extends DefaultAI
{
	private L2Character myself = null;
	public ai_legend_orc_treasure(L2Character actor)
	{
		super(actor);
		myself = actor;

	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		AddTimerEx(2114002,((5 * 60) * 1000));
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 2114002)
		{
			//SetVisible(0);
			Suicide(myself);
		}
	}
}
