package ai.SelMahumTrainingGrounds;

import l2open.gameserver.model.*;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date: 25.10.2013
 * open-team.ru
 * АИ для тренеров в селмахум
 **/
public class ai_xel_trainer_sniper extends ai_xel_trainer_wiz
{
	private L2Character myself = null;
	public ai_xel_trainer_sniper(L2Character self)
	{
		super(self);
		myself = self;
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 2219001)
		{
			if(!myself.isAttackingNow() && !myself.isDead())
			{
				switch(Rnd.get(6))
				{
					case 0:
						AddEffectActionDesire(myself,1,((130 * 1000) / 30),50);
						BroadcastScriptEvent(2219011,trainer_id,trainning_range);
						break;
					case 1:
						AddEffectActionDesire(myself,4,((70 * 1000) / 30),50);
						BroadcastScriptEvent(2219012,trainer_id,trainning_range);
						break;
					case 2:
						AddEffectActionDesire(myself,5,((30 * 1000) / 30),50);
						BroadcastScriptEvent(2219013,trainer_id,trainning_range);
						break;
					case 3:
					case 4:
					case 5:
						AddEffectActionDesire(myself,7,((130 * 1000) / 30),50);
						BroadcastScriptEvent(2219014,trainer_id,trainning_range);
						break;
				}
			}
			myself.AddTimerEx(2219001,(15 * 1000));
		}
	}
}
