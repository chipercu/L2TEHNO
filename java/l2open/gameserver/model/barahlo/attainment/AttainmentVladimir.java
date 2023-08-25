package l2open.gameserver.model.barahlo.attainment;

import l2open.config.ConfigValue;
import l2open.gameserver.model.*;
import l2open.gameserver.serverpackets.*;
import l2open.util.Rnd;

/**
Attainment13_count = 768
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
public class AttainmentVladimir extends Attainment
{
	public AttainmentVladimir(L2Player owner)
	{
		super(owner);
	}

	public void enter_world(boolean first)
	{
		if(ConfigValue.Attainment13_Set.length == 0 || _owner._AttainmentTask == null)
			return;
		if(ConfigValue.Attainment13_ResetStepOnRelog)
			_owner.unsetVar("Attainment13_step");
		if(ConfigValue.Attainment13_ResetTimeOnRelog)
			_owner.unsetVar("Attainment13_time");

		int step = Math.min(_owner.getVarInt("Attainment13_step", 0), (int)ConfigValue.Attainment13_Set.length-1);
		int time=(int)ConfigValue.Attainment13_Set[step][0]-_owner.getVarInt("Attainment13_time", 0);

		if(ConfigValue.Attainment13_Msg && _owner.getVarB("Attainment13_Msg", true))
		{
			_owner.sendPacket(new SystemMessage(6492).addNumber(time%60));
			//_owner.sendMessage("6492[0]: "+(time%60)+" step="+step);
		}
		else if(ConfigValue.Attainment13_Msg2)
		{
			_owner.sendPacket(new ExPCCafePointInfo(0, 0, 5, 5, time));
		}
		else if(ConfigValue.Attainment13_Msg3)
		{
			String hour = String.valueOf(time/60%24);
			String minuts = String.valueOf(time%60);

			if(hour.length() < 2)
				hour='0'+hour;
			if(minuts.length() < 2)
				minuts='0'+minuts;

			RegisterStringPacket p = new RegisterStringPacket(2000, hour+":"+minuts, ConfigValue.Attainment13_Font, 0, ConfigValue.Attainment13_Color, ConfigValue.Attainment13_ScreenPos, ConfigValue.Attainment13_FontStyle, 0, 61000, 0);
			p.setOffsetX(ConfigValue.Attainment13_xy[0]);
			p.setOffsetX(ConfigValue.Attainment13_xy[1]);

			_owner.sendPacket(p);
		}
	}

	public void incTime()
	{
		if(_owner.isInOfflineMode() || ConfigValue.Attainment13_Set.length == 0)
			return;

		int time = _owner.getVarInt("Attainment13_time", 0)+1;
		int step = Math.min(_owner.getVarInt("Attainment13_step", 0), ConfigValue.Attainment13_Set.length-1);

		if(time >= ConfigValue.Attainment13_Set[step][0]-1)
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
				setReward(ConfigValue.Attainment13_Set[step]);
			time=0;
			step = Math.min(step+1, ConfigValue.Attainment13_Set.length-1);
			_owner.setVar("Attainment13_step", String.valueOf(step));
			_owner.setVar("Attainment13_time", "0");
			if(ConfigValue.Attainment13_Msg && _owner.getVarB("Attainment13_Msg", true))
			{
				_owner.sendPacket(new SystemMessage(6495));
				//_owner.sendMessage("6495: step="+step);
			}
		}
		else
		{
			_owner.setVar("Attainment13_time", String.valueOf(time));
		}

		time=(int)ConfigValue.Attainment13_Set[step][0]-time;

		if(ConfigValue.Attainment13_Msg && _owner.getVarB("Attainment13_Msg", true))
		{
			_owner.sendPacket(new SystemMessage(6492).addNumber(time%60));
			//_owner.sendMessage("6492[1]: time"+(time%60)+" step="+step+" t2="+ConfigValue.Attainment13_Set[step][0]);
		}
		else if(ConfigValue.Attainment13_Msg2)
		{
			_owner.sendPacket(new ExPCCafePointInfo(0, 0, 5, 5, time));
		}
		else if(ConfigValue.Attainment13_Msg3)
		{
			String hour = String.valueOf(time/60%24);
			String minuts = String.valueOf(time%60);

			if(hour.length() < 2)
				hour='0'+hour;
			if(minuts.length() < 2)
				minuts='0'+minuts;

			RegisterStringPacket p = new RegisterStringPacket(2000, hour+":"+minuts, ConfigValue.Attainment13_Font, 0, ConfigValue.Attainment13_Color, ConfigValue.Attainment13_ScreenPos, ConfigValue.Attainment13_FontStyle, 0, 61000, 0);
			p.setOffsetX(ConfigValue.Attainment13_xy[0]);
			p.setOffsetX(ConfigValue.Attainment13_xy[1]);

			_owner.sendPacket(p);
		}
	}

	public void setReward(long[] item)
	{
		for(int i=1;i<item.length;i+=2)
		{
			_owner.getInventory().addItem((int)item[i], item[i+1]);
			_owner.sendPacket(SystemMessage.obtainItems((int)item[i], item[i+1], 0));
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

	public void oly_battle_end(boolean isWin)
	{
		if(isWin)
		{
			_oly_win++;
			_oly_loos=0;

			if(_oly_win >= ConfigValue.AttainmentOlympiadWin && ConfigValue.AttainmentOlympiadWin_reward.length > 0)
			{
				_oly_win=0;
				setRndReward(ConfigValue.AttainmentOlympiadWin_reward);
			}
		}
		else
		{
			_oly_win=0;
			_oly_loos++;

			if(_oly_loos >= ConfigValue.AttainmentOlympiadLoose && ConfigValue.AttainmentOlympiadLoose_reward.length > 0)
			{
				_oly_loos=0;
				setRndReward(ConfigValue.AttainmentOlympiadLoose_reward);
			}
		}
	}
	// ----------------------------------------------------------------------------------------------
	/*public void enter_world(boolean first)
	{
		int time=ConfigValue.Attainment13_count-_owner.getVarInt("Attainment13_time", 0);
		String hour = String.valueOf(time/60%24);
		String minuts = String.valueOf(time%60);

		if(hour.length() < 2)
			hour='0'+hour;
		if(minuts.length() < 2)
			minuts='0'+minuts;

		RegisterStringPacket p = new RegisterStringPacket(2000, hour+":"+minuts, ConfigValue.Attainment13_Font, 0, ConfigValue.Attainment13_Color, ConfigValue.Attainment13_ScreenPos, ConfigValue.Attainment13_FontStyle, 0, 61000, 0);
		p.setOffsetX(ConfigValue.Attainment13_xy[0]);
		p.setOffsetX(ConfigValue.Attainment13_xy[1]);

		_owner.sendPacket(p);
	}

	//RegisterStringPacket();
	public void incTime()
	{
		if(_owner.isInOfflineMode())
			return;
		int time = _owner.getVarInt("Attainment13_time", 0);
		if(time >= ConfigValue.Attainment13_count-1)
		{
			setRndReward(ConfigValue.Attainment13_reward);
			time=0;
			_owner.setVar("Attainment13_time", String.valueOf(time));
		}
		else
		{
			time++;
			_owner.setVar("Attainment13_time", String.valueOf(time));
		}

		time=ConfigValue.Attainment13_count-time;
		String hour = String.valueOf(time/60%24);
		String minuts = String.valueOf(time%60);

		if(hour.length() < 2)
			hour='0'+hour;
		if(minuts.length() < 2)
			minuts='0'+minuts;

		RegisterStringPacket p = new RegisterStringPacket(2000, hour+":"+minuts, ConfigValue.Attainment13_Font, 0, ConfigValue.Attainment13_Color, ConfigValue.Attainment13_ScreenPos, ConfigValue.Attainment13_FontStyle, 0, 61000, 0);
		p.setOffsetX(ConfigValue.Attainment13_xy[0]);
		p.setOffsetX(ConfigValue.Attainment13_xy[1]);

		_owner.sendPacket(p);
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
	}*/
}