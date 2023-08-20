package ai;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.tables.SkillTable;
import l2open.util.*;

/**
 * @author: Diagod
 * AI для Scarecrow ID: 18912
 */
public class ai_monastery_puppet extends L2CharacterAI
{
	private L2Character myself = null;

	public ai_monastery_puppet(L2Character self)
	{
		super(self);
		myself = self;
	}

	public int TIMER = 1000;

	@Override
	public void onEvtSpawn()
	{
		AddTimerEx(TIMER,(5 * 1000));
		super.onEvtSpawn();
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TIMER)
		{
			BroadcastScriptEvent(21140014,myself.getObjectId(),400);
			AddTimerEx(TIMER,(30 * 1000));
		}
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}
