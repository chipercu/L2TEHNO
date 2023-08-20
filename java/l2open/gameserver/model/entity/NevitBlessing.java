package l2open.gameserver.model.entity;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.gameserver.instancemanager.NevitManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExNavitAdventEffect;
import l2open.gameserver.serverpackets.ExNavitAdventPointInfoPacket;
import l2open.gameserver.serverpackets.ExNavitAdventTimeChange;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.AbnormalVisualEffect;
import l2open.util.reference.*;

import java.lang.ref.WeakReference;

public class NevitBlessing
{
	private HardReference<? extends L2Player> owner_ref = HardReferences.emptyRef();

	private int _pointsNevit = 0;

	private boolean _bonusNevitActive = false;
	
	private int _timeBonusNevit = 0;

	private boolean _nevitBuff = false;

	public NevitBlessing(L2Player player)
	{
		owner_ref = player.getRef();
	}

	private L2Player getPlayer()
	{
		return owner_ref.get();
	}

	public synchronized void addPoints(int count)
	{
		L2Player player = getPlayer();
		if(player == null || !ConfigValue.EnableNevitBonus)
			return;

		if(count != 0)
		{
			int before = _pointsNevit;
			_pointsNevit += count;
			_pointsNevit = Math.max(Math.min(_pointsNevit, 7200), 0);

			if(before < 5250 && _pointsNevit >= 5250)
				player.sendPacket(new SystemMessage(3269));
			else if (before < 3500 && _pointsNevit >= 3500)
				player.sendPacket(new SystemMessage(3268));
			else if (before < 1750 && _pointsNevit >= 1750)
				player.sendPacket(new SystemMessage(3267));
		}
		if(_pointsNevit >= 7200 && !isBuffActive() && !player.isInZonePeace())
		{
			_pointsNevit = 0;
			if(ConfigValue.EnableNevitBonus)
				ThreadPoolManager.getInstance().execute(new NevitBuffTask(180));
			stopBonus();
		}

		if(!isBuffActive())
			player.sendPacket(new ExNavitAdventPointInfoPacket(_pointsNevit));
	}

	public int getPoints()
	{
		return _pointsNevit;
	}

	public boolean onNevitBonusTimeTick()
	{
		if(!_bonusNevitActive)
			return false;

		setBonusTime(_timeBonusNevit + 1);
		addPoints(ConfigValue.CurrentPointMinuteUp);
		return _timeBonusNevit < 240;
	}

	public void setBonusTime(int val)
	{
		_timeBonusNevit = val;
		L2Player player = getPlayer();
		if(player == null || !ConfigValue.EnableNevitBonus)
			return;
		player.sendPacket(new ExNavitAdventTimeChange(_timeBonusNevit * 60, _bonusNevitActive));
	}

	public int getBonusTime()
	{
		return _timeBonusNevit;
	}

	public boolean isBonusActive()
	{
		return _bonusNevitActive;
	}

	public void startBonus()
	{
		L2Player player = getPlayer();
		if(player == null || !ConfigValue.EnableNevitBonus)
			return;
		if (!_bonusNevitActive && _timeBonusNevit < 240 && !player.isInPeaceZone() && !isBuffActive())
		{
			_bonusNevitActive = true;
			player.sendPacket(new ExNavitAdventTimeChange(_timeBonusNevit * 60, true));
			if (!NevitManager.getInstance().containPlayer(player))
				NevitManager.getInstance().addPlayer(player);
		}
  }

	public void stopBonus()
	{
		if (_bonusNevitActive)
		{
			_bonusNevitActive = false;
			L2Player player = getPlayer();
			if(player == null || !ConfigValue.EnableNevitBonus)
				return;
			player.sendPacket(new ExNavitAdventTimeChange(_timeBonusNevit * 60, false));
		}
	}

	public boolean isBuffActive()
	{
		return _nevitBuff;
	}

	public void stopBuff()
	{
		if (!isBuffActive() || !ConfigValue.EnableNevitBonus)
			return;

		_nevitBuff = false;

		L2Player player = getPlayer();
		if(player == null || !ConfigValue.EnableNevitBonus)
			return;
		if(ConfigValue.EnableNevitAbnormal)
			player.stopAbnormalEffect(AbnormalVisualEffect.ave_unk20);
		player.sendPacket(new ExNavitAdventEffect(-1), new ExNavitAdventPointInfoPacket(_pointsNevit));
	}

	public class NevitBuffTask extends l2open.common.RunnableImpl
	{
		int _time = 0;

		NevitBuffTask(int time)
		{
			_time = time;
		}

		public void runImpl()
		{
			L2Player player = getPlayer();
			if(player == null || !ConfigValue.EnableNevitBonus)
				return;
			player.sendPacket(new ExNavitAdventEffect(_time));
			if (_time > -1)
			{
				_nevitBuff = true;
				if(ConfigValue.EnableNevitAbnormal)
					player.startAbnormalEffect(AbnormalVisualEffect.ave_unk20);
				player.sendPacket(new SystemMessage(3274));
				ThreadPoolManager.getInstance().schedule(new NevitBuffTask(-1), _time * 1000);
			}
			else
			{
				_nevitBuff = false;
				if(ConfigValue.EnableNevitAbnormal)
					player.stopAbnormalEffect(AbnormalVisualEffect.ave_unk20);
			}
			
			player.sendPacket(new SystemMessage(_time > -1 ? 3266 : 3275));
			addPoints(0);
		}
	}
}