package l2open.gameserver.model.entity.olympiad;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;

class CompEndTask extends l2open.common.RunnableImpl
{
	@Override
	public void runImpl()
	{
		if(Olympiad.isOlympiadEnd())
			return;

		Olympiad._inCompPeriod = false;

		try
		{
			// Если остались игры, ждем их завершения еще одну минуту 
			if(Olympiad.getActiveGames() != null && !Olympiad.getActiveGames().isEmpty())
			{
				ThreadPoolManager.getInstance().schedule(new CompEndTask(), 60000);
				return;
			}

			Announcements.getInstance().announceToAll(Msg.THE_OLYMPIAD_GAME_HAS_ENDED);
			Olympiad._log.info("Olympiad System: Olympiad Game Ended");
			OlympiadDatabase.save();
			if(ConfigValue.OlympiadStatEnable)
				OlympiadStat.set_day_reward();
		}
		catch(Exception e)
		{
			Olympiad._log.warning("Olympiad System: Failed to save Olympiad configuration:");
			e.printStackTrace();
		}
		Olympiad.init();
	}
}