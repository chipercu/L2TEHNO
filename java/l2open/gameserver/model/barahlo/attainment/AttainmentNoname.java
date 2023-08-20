package l2open.gameserver.model.barahlo.attainment;

import l2open.config.ConfigValue;
import l2open.gameserver.model.*;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.taskmanager.AttainmentTaskManager;
import l2open.util.Rnd;

/**
Attainment13_count = 1440
Attainment13_reward = 57,100,57,1000


# 0-Small, 1-Normal, 2-Large, 3-VeryLarge, 4-Giant
Attainment13_Font = 3

# 1024-TopRightRelative, 2-TopRight, 0-TopLeft, 1-TopCenter, 6-MiddleRight, 4-MiddleLeft, 5-MiddleCenter, 10-BottomRight, 8-BottomLeft, 9-BottomCenter
Attainment13_ScreenPos = 0

# 0-Normal,  1-Shadowed
Attainment13_FontStyle = 1

Attainment13_Color = 0xFF, 0xFF, 0xFF, 0xFF
Attainment13_xy = 0,0
**/
public class AttainmentNoname extends Attainment
{
	public AttainmentNoname(L2Player owner)
	{
		super(owner);
	}

	public void incTime()
	{
		if(_owner.isInOfflineMode() || _owner._last_active+ConfigValue.Attainment13_count*1000L < System.currentTimeMillis() || !AttainmentTaskManager.isActive() || _owner.getLevel() < ConfigValue.Attainment13_Level)
			return;

		_owner.setVar("Attainment13_time", String.valueOf(_owner.getVarInt("Attainment13_time", 0)+1));

		/*int time = _owner.getVarInt("Attainment13_time", 0);
		if(time >= ConfigValue.Attainment13_count-1)
		{
			setReward(ConfigValue.Attainment13_reward);
			time=0;
			_owner.setVar("Attainment13_time", String.valueOf(time));
		}
		else
		{
			time++;
			_owner.setVar("Attainment13_time", String.valueOf(time));
		}*/
	}
}