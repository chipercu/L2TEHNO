package ai.CryptsOfDisgrace;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;

/**
 * @author: Drizzy
 * @date: 21.08.2012
 */

public class ai_legend_orc_ev_slave extends Fighter
{
	private L2Character myself = null;

	public ai_legend_orc_ev_slave(L2Character actor)
	{
		super(actor);
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 2114002)
		{
			L2Character c0 = L2ObjectsStorage.getCharacter(script_event_arg2);
			if(IsNullCreature(c0) == 0)
			{
				AddFollowDesire(c0,5);
			}
		}
	}

}
