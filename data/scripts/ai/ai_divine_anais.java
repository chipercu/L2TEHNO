package ai;

import l2open.gameserver.ai.Mystic;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Skill;

/**
 * @author: Drizzy
 * АИ для анаиса. Все механизмы сделаны по ПТС.
 */
public class ai_divine_anais extends Mystic
{
	private L2Character myself = null;
	private int TIME_FOR_ANAIS_INFO = 901;
	private int i_ai0 = 0;
	private L2Character h0;

	public ai_divine_anais(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		i_ai0 = 0;
		super.onEvtSpawn();
		AddTimerEx(TIME_FOR_ANAIS_INFO,(3 * 1000));
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		BroadcastScriptEvent(2114008,myself.getObjectId(),2000);
		AddTimerEx(TIME_FOR_ANAIS_INFO,( 30 * 1000 ));
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(i_ai0 == 0)
		{
			BroadcastScriptEvent(2114006,1,2000);
			i_ai0 = 1;
		}
		else if(myself.getCurrentHp() - damage <= (myself.getMaxHp() * 0.750000) && i_ai0 == 1)
		{
			BroadcastScriptEvent(2114006,2,2000);
			i_ai0 = 2;
		}
		else if(myself.getCurrentHp() - damage <= (myself.getMaxHp() * 0.500000) && i_ai0 == 2)
		{
			BroadcastScriptEvent(2114006,3,2000);
			i_ai0 = 3;
		}
		else if(myself.getCurrentHp() - damage <= (myself.getMaxHp() * 0.250000) && i_ai0 == 3)
		{
			BroadcastScriptEvent(2114006,4,2000);
			i_ai0 = 4;
		}
		super.ATTACKED(attacker,damage, skill);
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 21140010)
		{
			L2Character c0 = L2ObjectsStorage.getNpc(script_event_arg2);
			h0 = getActor().getRandomHated();
			int i0 = 0;
			if(h0 != null)
			{
				if(IsNullCreature(h0) == 0)
				{
					i0 = 1;
				}
			}
			if(i0 == 1)
			{
				if(IsNullCreature(c0) == 0)
				{
					SendScriptEvent(c0,2114009,h0.getObjectId());
				}
			}
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		BroadcastScriptEvent(2114007,1,3000);
		i_ai0 = 0;
		super.MY_DYING(killer);
	}
}