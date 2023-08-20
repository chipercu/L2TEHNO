package ai.DenOfEvil;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date: 27.02.2013
 */
public class ai_raid_baranka_observer extends DefaultAI
{
	private L2NpcInstance myself = null;

	public ai_raid_baranka_observer(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public int debug_mode = 0;

	@Override
	public void SEE_CREATURE(L2Character creature)
	{
		if(creature != null && (creature.isPlayer() || creature.isPet() || creature.isSummon()))
		{
			if(creature.isPlayer())
			{
				SendScriptEvent(getActor().getMyLeader(),2214009,GetIndexFromCreature(creature),0);
			}
			else if(creature.getPet() != null)
			{
				SendScriptEvent(getActor().getMyLeader(),2214009,GetIndexFromCreature(creature.getPet().getPlayer()),0);
			}
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 2214007)
		{
			Despawn(myself);
		}
	}
}