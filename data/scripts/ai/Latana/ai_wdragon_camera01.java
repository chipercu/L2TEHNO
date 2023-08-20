package ai.Latana;

import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.SpecialCamera;
import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.util.*;

/**
 * @author Drizzy
 * @date 01.02.2014
 * open-team.ru
 **/

public class ai_wdragon_camera01 extends DefaultAI
{
	private L2Character myself;
	public ai_wdragon_camera01(L2Character self)
	{
		super(self);
		myself = self;
	}

	@Override
	public void onEvtSpawn()
	{
		myself.AddTimerEx(3000,10);
		super.onEvtSpawn();
	}

	@Override
	public void SEE_CREATURE(L2Character target)
	{
		if(target.is_pc() == 1 && myself.i_ai0 == 0)
		{
			myself.i_ai0 = 1;
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 2316004)
		{
			if(script_event_arg2 == 1)
			{
				myself.AddTimerEx(1000,10);
			}
			if(script_event_arg2 == 2)
			{
				myself.AddTimerEx(2000,10);
			}
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 3000)
		{
			//myself.LookNeighbor(2000);
			myself.AddTimerEx(3000,(10 * 1000));
		}
		if(timer_id == 1000)
		{
			broadcastPacket(new SpecialCamera(myself,600,200,5,0,15000,10000,-10,8,1,1,1));
			myself.AddTimerEx(1001,2000);
		}
		if(timer_id == 1001)
		{
			broadcastPacket(new SpecialCamera(myself,400,200,5,4000,15000,10000,-10,8,1,1,0));
			myself.AddTimerEx(1002,4000);
		}
		if(timer_id == 1002)
		{
			broadcastPacket(new SpecialCamera(myself,300,195,4,1500,15000,10000,-5,10,1,1,0));
			myself.AddTimerEx(1003,1700);
		}
		if(timer_id == 1003)
		{
			broadcastPacket(new SpecialCamera(myself,130,2,5,0,15000,10000,0,0,1,0,1));
			myself.AddTimerEx(1004,2000);
		}
		if(timer_id == 1004)
		{
			broadcastPacket(new SpecialCamera(myself, 220, 0, 4, 800, 15000, 10000, 5, 10, 1, 0, 0));
			myself.AddTimerEx(1005,2000);
		}
		if(timer_id == 1005)
		{
			broadcastPacket(new SpecialCamera(myself,250,185,5,4000,15000,10000,-5,10,1,1,0));
			myself.AddTimerEx(1006,4000);
		}
		if(timer_id == 1006)
		{
			broadcastPacket(new SpecialCamera(myself,200,0,5,2000,15000,10000,0,25,1,0,0));
			myself.AddTimerEx(1007,4530);
		}
		if(timer_id == 1007)
		{
			broadcastPacket(new SpecialCamera(myself,300,-3,5,3500,15000,6000,0,6,1,0,0));
			myself.AddTimerEx(9999,10000);
		}
		if(timer_id == 2000)
		{
			broadcastPacket(new SpecialCamera(myself,250,0,6,0,15000,10000,2,0,1,0,1));
			myself.AddTimerEx(2001,2000);
		}
		if(timer_id == 2001)
		{
			broadcastPacket(new SpecialCamera(myself,230,0,5,2000,15000,10000,0,0,1,0,0));
			myself.AddTimerEx(2002,2500);
		}
		if(timer_id == 2002)
		{
			broadcastPacket(new SpecialCamera(myself,180,175,2,1500,15000,10000,0,10,1,1,0));
			myself.AddTimerEx(2003,1500);
		}
		if(timer_id == 2003)
		{
			broadcastPacket(new SpecialCamera(myself,300,180,5,1500,15000,3000,0,6,1,1,0));
			myself.AddTimerEx(9999,3000);
		}
		if(timer_id == 9999)
		{
			Suicide(myself);
		}
	}

	protected void broadcastPacket(L2GameServerPacket mov)
	{
		if (myself.getReflection() != null)
		{
			for (L2Character characters : myself.getReflection().getPlayers())
			{
				if (characters.isPlayer())
				{
					characters.sendPacket(mov);
				}
			}
		}
	}
}
