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

public class ai_wdragon_camera02 extends DefaultAI
{
	private L2Character myself;
	public ai_wdragon_camera02(L2Character self)
	{
		super(self);
		myself = self;
	}

	@Override
	public void onEvtSpawn()
	{
		myself.AddTimerEx(1000,1);
		super.onEvtSpawn();
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 1000)
		{
			broadcastPacket(new SpecialCamera(myself,450,200,3,0,15000,10000,-15,20,1,1,1));
			myself.AddTimerEx(1001,100);
		}
		if(timer_id == 1001)
		{
			broadcastPacket(new SpecialCamera(myself,350,200,5,5600,15000,10000,-15,10,1,1,0));
			myself.AddTimerEx(1002,5600);
		}
		if(timer_id == 1002)
		{
			broadcastPacket(new SpecialCamera(myself, 360, 200, 5, 1000, 15000, 2000, -15, 10, 1, 1, 0));
			myself.AddTimerEx(9999,10000);
		}
		if(timer_id == 9999)
		{
			Despawn(myself);
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
