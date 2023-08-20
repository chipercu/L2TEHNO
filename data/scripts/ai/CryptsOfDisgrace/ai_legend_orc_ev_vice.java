package ai.CryptsOfDisgrace;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;

/**
 * @author: Drizzy
 * @date: 21.08.2012
 */

public class ai_legend_orc_ev_vice extends Fighter
{
	private L2Character myself = null;
	private L2Character c0 = null;
	public ai_legend_orc_ev_vice(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		Shout(1800848);
		AddTimerEx(2114003,5000);
		c0 = L2ObjectsStorage.getCharacter(ID);
		if(IsNullCreature(c0) == 0)
		{
			getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, c0, 100);
			getActor().getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, c0, null);
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 2114003)
		{
			Shout(1800849);
			BroadcastScriptEvent(2114002,myself.getObjectId(),2000);
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		Shout(1800862);
		super.MY_DYING(killer);
	}
}
