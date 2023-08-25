package ai;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;

/**
 * @author: Drizzy
 * АИ для жаровен у анаиса. Вызывают шарики.
 */
public class ai_monastery_anais_burner extends DefaultAI
{
	private L2Character myself = null;
	private int TIME_FOR_TARGET = 2000;
	private L2Character c_ai0;

	public ai_monastery_anais_burner(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 2114008)
		{
			c_ai0 = L2ObjectsStorage.getNpc(script_event_arg2);
		}
		else if(script_event_arg1 == 2114006 && script_event_arg2 == BURNER_NUMBER)
		{
			getActor().setNpcState(1);
			AddTimerEx(TIME_FOR_TARGET,(1000));
		}
		else if(script_event_arg1 == 2114009)
		{
			int count=0;
			for(L2NpcInstance npc : L2World.getAroundNpc(getActor(), 2500, 100))
				if(npc != null && !npc.isDead())
					count++;
			if(count >= 100)
				return;
			getActor().setNpcState(1);
			if(BURNER_NUMBER == 4)
				CreateOnePrivateEx(18929,"ai_grail_protection", "L2Monster", "id", script_event_arg2, 113610,-77318,56,0);
			else if(BURNER_NUMBER == 3)
				CreateOnePrivateEx(18929,"ai_grail_protection", "L2Monster", "id", script_event_arg2, 111971,-77366,56,0);
			else if(BURNER_NUMBER == 2)
				CreateOnePrivateEx(18929,"ai_grail_protection", "L2Monster", "id", script_event_arg2, 111957,-75691,56,0);
			else if(BURNER_NUMBER == 1)
				CreateOnePrivateEx(18929,"ai_grail_protection", "L2Monster", "id", script_event_arg2, 113552,-75715,56,0);
		}
		else if(script_event_arg1 == 2114007)
		{
			getActor().setNpcState(2);
			getActor().doDie(getActor());
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TIME_FOR_TARGET)
		{
			getActor().setNpcState(2);
			SendScriptEvent(c_ai0,21140010,myself.getObjectId());
			AddTimerEx(TIME_FOR_TARGET,(20 * 1000));
		}
	}
}