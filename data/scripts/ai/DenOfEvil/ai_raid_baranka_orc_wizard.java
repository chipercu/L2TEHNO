package ai.DenOfEvil;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date: 27.02.2013
 */

public class ai_raid_baranka_orc_wizard extends ai_nest_wizard_summon_private
{
	private L2NpcInstance myself = null;

	public ai_raid_baranka_orc_wizard(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public int debug_mode = 0;

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		super.SCRIPT_EVENT( script_event_arg1,  script_event_arg2,  script_event_arg3);
		if(script_event_arg1 == 2214005)
		{
			Despawn(myself);
		}
		else if(script_event_arg1 == 2214007)
		{
			Despawn(myself);
		}
		else if(script_event_arg1 == 2214008)
		{
			if(myself.IsMyBossAlive() > 0)
			{
				if(myself.getMyLeader() == GetCreatureFromIndex(script_event_arg2))
				{
					MakeAttackEvent(GetCreatureFromIndex(script_event_arg3),500,0);
				}
			}
		}
	}

	@Override
	public void MY_DYING(L2Character killer)
	{
		super.MY_DYING(killer);
		BroadcastScriptEvent(2214006,0,4000);
	}
}