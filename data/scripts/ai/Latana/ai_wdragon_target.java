package ai.Latana;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;

/**
 * @author Drizzy
 * @date 01.02.2014
 * open-team.ru
 **/

public class ai_wdragon_target extends DefaultAI
{
	private L2Character myself;
	public ai_wdragon_target(L2Character self)
	{
		super(self);
		myself = self;
	}

	@Override
	public void onEvtSpawn()
	{
		myself.AddTimerEx(1001,10);
		super.onEvtSpawn();
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 1001)
		{
			BroadcastScriptEvent(2316005,GetIndexFromCreature(myself),4000);
			AddTimerEx(3000,(60 * 1000));
		}
		if(timer_id == 3000)
		{
			Suicide(myself);
		}
	}
}
