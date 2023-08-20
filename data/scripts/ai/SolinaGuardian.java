package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

public class SolinaGuardian extends Fighter
{
	public SolinaGuardian(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		getActor().i_ai0 = 1;
		getActor().AddTimerEx(2201,6000);
		getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getInfo(6371, 1));
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 2201)
		{
			if(getActor().i_ai0 == 1)
			{
				getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getInfo(6371, 1));
				getActor().AddTimerEx(2201,6000);
			}
		}
	}
}
