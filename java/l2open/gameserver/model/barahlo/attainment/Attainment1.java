package l2open.gameserver.model.barahlo.attainment;

import l2open.config.ConfigValue;
import l2open.gameserver.model.*;
import l2open.gameserver.serverpackets.*;
import l2open.util.Log;
import l2open.util.Rnd;

/**
EnableAttainment=true
AttainmentType=1360
Attainment13_Minute=1

Attainment13_HwidCount=2

Attainment13_Set = 5,57,100,4037,1;5,57,100,4037,1;60,57,100,4037,1


# 0-Small, 1-Normal, 2-Large, 3-VeryLarge, 4-Giant
Attainment13_Font = 3

# 1024-TopRightRelative, 2-TopRight, 0-TopLeft, 1-TopCenter, 6-MiddleRight, 4-MiddleLeft, 5-MiddleCenter, 10-BottomRight, 8-BottomLeft, 9-BottomCenter
Attainment13_ScreenPos = 0

# 0-Normal,  1-Shadowed
Attainment13_FontStyle = 1

Attainment13_Color = 0xFF, 0xFF, 0xFF, 0xFF
Attainment13_xy = 0,0
**/
public class Attainment1 extends Attainment
{
	public Attainment1(L2Player owner)
	{
		super(owner);
	}

	public void enter_world(boolean first)
	{
		int step_count = ConfigValue.Attainment13_Set.length;
		if(step_count == 0 || _owner._AttainmentTask == null)
			return;

		if(ConfigValue.Attainment13_ResetStepOnRelog)
			_owner.unsetVar("Attainment13_step");
		if(ConfigValue.Attainment13_ResetTimeOnRelog)
			_owner.unsetVar("Attainment13_time");
		
		int step = _owner.getVarInt("Attainment13_step", 0);
		if(ConfigValue.Attainment13_EnableCycleReward)
			step = Math.min(step, step_count-1);

		if(step < step_count)
		{
			int time=(int)ConfigValue.Attainment13_Set[step][0]-_owner.getVarInt("Attainment13_time", 0);
			//_log.info("enter_world: ["+time+"]["+step+"]");

			if(ConfigValue.Attainment13_Msg && _owner.getVarB("Attainment13_Msg", true))
			{
				_owner.sendPacket(new SystemMessage(6492).addNumber(time));
				_owner.sendPacket(new ExEventMatchMessage(9, "RewID="+ConfigValue.Attainment13_Set[step][1]+" RewCount="+ConfigValue.Attainment13_Set[step][2]));

				//_owner.sendMessage("6492[0]: "+(time%60)+" step="+step);
			}
			else if(ConfigValue.Attainment13_Msg2)
			{
				//_owner.sendPacket(new ExPCCafePointInfo(0, 0, 5, 5, time));
				_owner.sendPacket(new SystemMessage(7000).addNumber(time));
			}
		}
	}

	// Attainment13_EnableCycleReward
	public void incTime()
	{
		int step_count = ConfigValue.Attainment13_Set.length;
		if(_owner.isInOfflineMode() || step_count == 0)
			return;

		int time = _owner.getVarInt("Attainment13_time", 0)+1;
		int step = _owner.getVarInt("Attainment13_step", 0);
		if(ConfigValue.Attainment13_EnableCycleReward)
			step = Math.min(step, step_count-1);

		if(step < step_count)
		{
			boolean add_reward=false;

			if(time >= ConfigValue.Attainment13_Set[step][0])
			{
				if(ConfigValue.Attainment13_Chance > -1)
				{
					if(Rnd.chance(ConfigValue.Attainment13_Chance))
					{
						long[] r = ConfigValue.Attainment13_Set[step];
						long[] reward = new long[r.length - 1];
						System.arraycopy(r, 1, reward, 0, r.length - 1);
						setRndReward(reward);
					}
					else
						_owner.sendMessage("Бонус за онлайн выдается каждые 20 минут. Шанс 25%");
				}
				else
				{
					add_reward=true;
					setReward(step, ConfigValue.Attainment13_Set[step]);
				}
				step++;
				if(ConfigValue.Attainment13_EnableCycleReward)
					step = Math.min(step, step_count-1);
				_owner.setVar("Attainment13_step", String.valueOf(step));
				_owner.setVar("Attainment13_time", "0");
				if(ConfigValue.Attainment13_Msg && _owner.getVarB("Attainment13_Msg", true))
				{
					_owner.sendPacket(new SystemMessage(6495));
					//_owner.sendMessage("6495: step="+step);
				}
			}
			else
				_owner.setVar("Attainment13_time", String.valueOf(time));

			if(add_reward && step < step_count && ConfigValue.Attainment13_Msg && _owner.getVarB("Attainment13_Msg", true))
			{
				time=(int)ConfigValue.Attainment13_Set[step][0];
				_owner.sendPacket(new SystemMessage(6492).addNumber(time));
				_owner.sendPacket(new ExEventMatchMessage(9, "RewID="+ConfigValue.Attainment13_Set[step][1]+" RewCount="+ConfigValue.Attainment13_Set[step][2]));
				//_owner.sendMessage("6492[1]: time"+(time%60)+" step="+step+" t2="+ConfigValue.Attainment13_Set[step][0]);
			}

			if(ConfigValue.Attainment13_Msg2)
			{
				time=(int)ConfigValue.Attainment13_Set[step][0]-time;
				//_owner.sendPacket(new ExPCCafePointInfo(0, 0, 5, 5, time));
				_owner.sendPacket(new SystemMessage(7000).addNumber(time));
			}
		}
	}

	public void setReward(int step, long[] item)
	{
		//_log.info("setReward: ["+step+"]");

		//_owner.sendMessage("setReward: step="+step);
		for(int i=1;i<item.length;i+=2)
		{
			_owner.getInventory().addItem((int)item[i], item[i+1]);
			_owner.sendPacket(SystemMessage.obtainItems((int)item[i], item[i+1], 0));
			Log.addMy("REWARD["+item[i]+"]["+item[i+1]+"]: step="+step+" name="+_owner.getName(), "", "rewards");
		}
	}

	public void incPvp(final L2Player died)
	{
		if(died.getHWIDs().equals(_owner.getHWIDs()) || ConfigValue.AttainmentKillProtect && died.no_kill_time())
			return;
		_pvp_count++;
		_owner.setVar("AttainmentPvP", String.valueOf(_pvp_count));
		if(_pvp_count >= ConfigValue.Attainment1_count)
		{
			_pvp_count=0;
			_owner.setVar("AttainmentPvP", "0");

			if(ConfigValue.Attainment1_reward.length > 1)
			{
				int index = Rnd.get(ConfigValue.Attainment1_reward.length/2)*2;
				
				_owner.getInventory().addItem((int)ConfigValue.Attainment1_reward[index], ConfigValue.Attainment1_reward[index+1]);
				_owner.sendPacket(SystemMessage.obtainItems((int)ConfigValue.Attainment1_reward[index], ConfigValue.Attainment1_reward[index+1], 0));
			}
		}
	}
}