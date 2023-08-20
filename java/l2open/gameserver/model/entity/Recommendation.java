package l2open.gameserver.model.entity;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.skills.SkillAbnormalType;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;

public class Recommendation
{
	private final WeakReference<L2Player> owner;
	private int _recomHave;
	private int _recomLeft;
	private int _recomTimeLeft;
	private long _recomBonusStart;
	public static final int[][] REC_BONUS = 
	{
		{ 25, 50, 50, 50, 50, 50, 50, 50, 50, 50 },
		{ 16, 33, 50, 50, 50, 50, 50, 50, 50, 50 },
		{ 12, 25, 37, 50, 50, 50, 50, 50, 50, 50 },
		{ 10, 20, 30, 40, 50, 50, 50, 50, 50, 50 },
		{ 8, 16, 25, 33, 41, 50, 50, 50, 50, 50 },
		{ 7, 14, 21, 28, 35, 42, 50, 50, 50, 50 },
		{ 6, 12, 18, 25, 31, 37, 43, 50, 50, 50 },
		{ 5, 11, 16, 22, 27, 33, 38, 44, 50, 50 },
		{ 5, 10, 15, 20, 25, 30, 35, 40, 45, 50 },
		{ 5, 10, 15, 20, 25, 30, 35, 40, 45, 50 }, // - нужно для 85+ уровней
		{ 5, 10, 15, 20, 25, 30, 35, 40, 45, 50 }
	};

	public static final int[][] NEVITS_HOURGlASS = { { 20, 4000, 17095, 17096, 17097, 17098, 17099 }, { 40, 30000, 17100, 17101, 17102, 17103, 17104 }, { 52, 110000, 17105, 17106, 17107, 17108, 17109 }, { 61, 310000, 17110, 17111, 17112, 17113, 17114 }, { 76, 970000, 17115, 17116, 17117, 17118, 17119 }, { 80, 2160000, 17120, 17121, 17122, 17123, 17124 }, { 86, 5000000, 17125, 17126, 17127, 17128, 17129 } };

	public static final int[][] NEVITS_HOURGLASS_SKILLS = { { 9115, 3600 }, { 9116, 5400 }, { 9117, 7200 }, { 9118, 9000 }, { 9119, 10800 }, { 9120, 3600 }, { 9121, 5400 }, { 9122, 7200 }, { 9123, 9000 }, { 9124, 10800 }, { 9125, 3600 }, { 9126, 5400 }, { 9127, 7200 }, { 9128, 9000 }, { 9129, 10800 }, { 9130, 3600 }, { 9131, 5400 }, { 9132, 7200 }, { 9133, 9000 }, { 9134, 10800 }, { 9135, 3600 }, { 9136, 5400 }, { 9137, 7200 }, { 9138, 9000 }, { 9139, 10800 }, { 9140, 3600 }, { 9141, 5400 }, { 9142, 7200 }, { 9143, 9000 }, { 9144, 10800 }, { 9145, 3600 }, { 9146, 5400 }, { 9147, 7200 }, { 9148, 9000 }, { 9149, 10800 } };
	public ScheduledFuture<?> _recVoteTask;

	public Recommendation(L2Player player)
	{
		owner = new WeakReference<L2Player>(player);
	}

	private L2Player getPlayer()
	{
		return owner.get();
	}

	public void checkRecom()
	{
		L2Player player = getPlayer();
		if (player == null)
			return;
			
		if (_recVoteTask != null)
		{
			_recVoteTask.cancel(false);
		}
		Calendar temp = Calendar.getInstance();
		temp.set(Calendar.HOUR_OF_DAY, 6);
		temp.set(Calendar.MINUTE, 30);
		temp.set(Calendar.SECOND, 0);
		long count = Math.round((System.currentTimeMillis() / 1000 - player.getLastAccess()) / 86400); // сколько дней назад был ласт заход...
																					
		if (count == 0 // последний заход был меньше суток назад?
		&& player.getLastAccess() < temp.getTimeInMillis() / 1000 // Если последний заход был ДО 6.30
		&& System.currentTimeMillis() > temp.getTimeInMillis()) // Если сейчас времени больше чем 6.30 утра.
			count++;
		int time = 0;

		if (count != 0)
		{
			setRecomLeft(20);
			setRecomTimeLeft(3600);
			int have = getRecomHave();
			for (int i = 0; i < count; i++)
				have -= 20;
			if (have < 0)
				have = 0;
			setRecomHave(have);
			time = 2;
		}
		updateVoteInfo();
		player.sendUserInfo(true); 
		_recVoteTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RecVoteTask(time), 3600000, 3600000);
	}

	public void restartRecom()
	{
		L2Player player = getPlayer();
		if (player == null)
			return;
		try
		{
			if (player.getLevel() > 20)
			{
				setRecomLeft(20);
				setRecomTimeLeft(3600);
			}

			_recomHave -= 20;
			if (_recomHave < 0)
			{
				_recomHave = 0;
			}
			if (_recVoteTask != null)
				_recVoteTask.cancel(false);
			_recVoteTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RecVoteTask(2), 3600000, 3600000);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void startRecBonus()
	{
		if(isRecBonusActive() || isHourglassBonusActive() > 0)
			return;
		L2Player player;
		if(getRecomTimeLeft() == 0 || getRecomHave() < 10 || ((player = getPlayer()) != null && player.isInZonePeace()))
		{
			stopRecBonus();
			return;
		}
		_recomBonusStart = System.currentTimeMillis();
		updateVoteInfo();
	}

	public void stopRecBonus()
	{
		if(!isRecBonusActive())
			return;

		_recomTimeLeft = getRecomTimeLeft();
		_recomBonusStart = 0;
		updateVoteInfo();
	}

	public boolean isRecBonusActive()
	{
		return _recomBonusStart != 0;
	}

	public void updateVoteInfo()
	{
		L2Player player = getPlayer();
		if (player == null)
			return;
		player.sendUserInfo(true);
	}

	public void addRecomHave(int value)
	{
		setRecomHave(getRecomHave() + value);
		updateVoteInfo();
	}	

	public void addRecomLeft(int value)
	{
		setRecomLeft(getRecomLeft() + value);
		updateVoteInfo();
	}

	public void giveRecom(L2Player target)
	{
		int targetRecom = target.getRecommendation().getRecomHave();
		if (targetRecom < 255)
		{
			target.getRecommendation().setRecomHave(targetRecom + 1);
			target.getRecommendation().updateVoteInfo();
		}
		if (_recomLeft > 0)
			_recomLeft -= 1;
	}

	public int getRecomHave()
	{
		return _recomHave;
	}

	public void setRecomHave(int value)
	{
		_recomHave = Math.max(Math.min(value, 255), 0);
	}

	public int getRecomLeft()
	{
		return _recomLeft;
	}

	public void setRecomLeft(int value)
	{
		_recomLeft = Math.max(Math.min(value, 255), 0);
	}

	public int getRecomTimeLeft()
	{
		return isRecBonusActive() ? Math.max(_recomTimeLeft - (int)(System.currentTimeMillis() - _recomBonusStart) / 1000, 0) :  _recomTimeLeft;
	}

	public long isHourglassBonusActive()
	{
		if(getPlayer().isConnected())
		{
			L2Effect effect = getPlayer().getEffectList().getEffectByStackType(SkillAbnormalType.vote);
			if(effect != null)
				return effect.getTimeLeft();
		}
		return 0L;
	}

	public void setRecomTimeLeft(int value)
	{
		_recomTimeLeft = value;
	}

	public int getRecomExpBonus()
	{
		L2Player player = getPlayer();
		if(player == null)
			return 0;
		else if(isHourglassBonusActive() <= 0)
		{
			if(getRecomTimeLeft() <= 0)
				return 0;
			else if(player.getLevel() < 1)
				return 0;
			else if(getRecomHave() < 1)
				return 0;
		}
		if(getRecomHave() >= 100)
			return 50;
		int arg1 = (int)Math.floor(player.getLevel() / 10);
		int arg2 = (int)Math.floor(getRecomHave() / 10);
		return REC_BONUS[arg1][arg2];
	}

	public void stopRecomendationTask()
	{
		L2Player player = getPlayer();
		if (player == null)
			return;
		if (_recVoteTask != null)
			_recVoteTask.cancel(false);
	}

	public class RecVoteTask extends l2open.common.RunnableImpl
	{
		int _time;

		public RecVoteTask(int time)
		{
			_time = time;
		}

		public void runImpl()
		{
			if (_time > 0)
			{
				addRecomLeft(10);
				_time -= 1;
			}
			else
			{
				addRecomLeft(1);
			}
		}
	}
}