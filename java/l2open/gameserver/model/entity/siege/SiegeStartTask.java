package l2open.gameserver.model.entity.siege;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.entity.siege.fortress.FortressSiege;
import l2open.gameserver.model.entity.residence.ResidenceType;
import l2open.gameserver.serverpackets.SystemMessage;

import java.util.Calendar;

public class SiegeStartTask extends l2open.common.RunnableImpl
{
	private Siege _siege;

	public SiegeStartTask(Siege siege)
	{
		_siege = siege;
	}

	public void runImpl()
	{
		if(_siege.isInProgress())
			return;

		try
		{
			long timeRemaining = _siege.getSiegeDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
			if(timeRemaining > 86400000)
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_siege), timeRemaining - 86400000); // Prepare task for 24 before siege start to end registration
			else if(timeRemaining <= 86400000 && timeRemaining > 3600000)
			{
				checkRegistrationOver(0);
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_siege), timeRemaining - 3600000); // Prepare task for 1 hr left before siege start.
			}
			else if(timeRemaining <= 3600000 && timeRemaining > 600000)
			{
				_siege.announceStartSiege(3600);
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_siege), timeRemaining - 600000); // Prepare task for 10 minute left.
			}
			else if(timeRemaining <= 600000 && timeRemaining > 300000)
			{
				checkRegistrationOver(1);
				_siege.announceStartSiege(600);
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_siege), timeRemaining - 300000); // Prepare task for 5 minute left.
			}
			else if(timeRemaining <= 300000 && timeRemaining > 60000)
			{
				checkRegistrationOver(1);
				_siege.announceStartSiege(300);
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_siege), timeRemaining - 60000); // Prepare task for 1 minute left.
			}
			else if(timeRemaining <= 60000 && timeRemaining > 30000)
			{
				checkRegistrationOver(1);
				_siege.announceStartSiege(60);
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_siege), timeRemaining - 30000); // Prepare task for 30 seconds count down
			}
			else if(timeRemaining <= 30000 && timeRemaining > 10000)
			{
				checkRegistrationOver(1);
				_siege.announceStartSiege(30);
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_siege), timeRemaining - 10000); // Prepare task for 10 seconds count down
			}
			else if(timeRemaining <= 10000 && timeRemaining > 5000)
			{
				checkRegistrationOver(1);
				_siege.announceStartSiege(10);
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_siege), timeRemaining - 4000); // Prepare task for 5 seconds count down
			}
			else if(timeRemaining <= 5000 && timeRemaining > 2000)
			{
				checkRegistrationOver(1);
				_siege.announceStartSiege(5);
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_siege), timeRemaining-1000); // Prepare task for second count down
			}
			else if(timeRemaining <= 2000 && timeRemaining > 0)
			{
				checkRegistrationOver(1);
				_siege.announceStartSiege(1);
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_siege), timeRemaining); // Prepare task for second count down
			}
			else
			{
				_siege.announceStartSiege(0);
				_siege.startSiege();
			}
		}
		catch(Throwable t)
		{}
	}

	private void checkRegistrationOver(int type)
	{
		if(type == 1 && _siege.getSiegeUnit().getType() == ResidenceType.Fortress)
		{
			FortressSiege _fs = (FortressSiege)_siege;
			if(_fs != null && _fs._mercenary != null && _fs._mercenary.getSpawn() != null)
			{
				_fs._mercenary.getSpawn().stopRespawn();
				_fs._mercenary.getSpawn().getLastSpawn().deleteMe();
				_fs._mercenary = null;
			}
		}
		if(!_siege.isRegistrationOver() && _siege.getSiegeRegistrationEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis() <= 10000)
		{
			_siege.announceToPlayer(new SystemMessage(SystemMessage.THE_REGISTRATION_TERM_FOR_S1_HAS_ENDED).addString(_siege.getSiegeUnit().getName()), false, false);
			_siege.setRegistrationOver(true);
			_siege.getDatabase().clearSiegeClan(SiegeClanType.DEFENDER_WAITING);
			_siege.getDatabase().clearSiegeClan(SiegeClanType.DEFENDER_REFUSED);
		}
	}
}