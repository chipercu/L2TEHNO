package l2open.gameserver.model.quest;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class QuestTimer
{
	public class ScheduleTimerTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			if(!isActive())
				return;

			L2Player pl = getPlayer();
			if(pl != null && getQuest() != null && getQuest().getName() != null && getName() != null)
				pl.processQuestEvent(getQuest().getName(), getName(), getNpc());
			cancel();
		}
	}

	private boolean _isActive = true;
	private String _name;
	private L2NpcInstance _npc;
	private long _time;
	private L2Player _owner = null;
	private Quest _quest;
	private ScheduledFuture<?> _schedular;

	public QuestTimer(Quest quest, String name, long time, L2NpcInstance npc, L2Player player)
	{
		_name = name;
		_quest = quest;
		_owner = player;
		_npc = npc;
		_time = time;
		_schedular = ThreadPoolManager.getInstance().schedule(new ScheduleTimerTask(), time); // Prepare auto end task
	}

	public void cancel()
	{
		_isActive = false;

		if(_schedular != null)
		{
			// Запоминаем оставшееся время, для возможности возобновления таска
			_time = _schedular.getDelay(TimeUnit.SECONDS);
			_schedular.cancel(false);
		}

		getQuest().removeQuestTimer(this);
	}

	public final boolean isActive()
	{
		return _isActive;
	}

	public final String getName()
	{
		return _name;
	}

	public final L2NpcInstance getNpc()
	{
		return _npc;
	}

	public final L2Player getPlayer()
	{
		return _owner;
	}

	public final Quest getQuest()
	{
		return _quest;
	}

	// Проверяет, совпадают ли указанные параметры с параметрами этого таймера
	public boolean isMatch(Quest quest, String name, L2Player player)
	{
		return quest != null && name != null && quest == getQuest() && name.equalsIgnoreCase(getName()) && player == getPlayer();
	}

	@Override
	public final String toString()
	{
		return _name;
	}

	public long getTime()
	{
		return _time;
	}
}