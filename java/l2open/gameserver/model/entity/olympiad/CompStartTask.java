package l2open.gameserver.model.entity.olympiad;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;

class CompStartTask extends l2open.common.RunnableImpl
{
	@Override
	public void runImpl()
	{
		if(Olympiad.isOlympiadEnd())
			return;

		Olympiad._manager = new OlympiadManager();
		Olympiad._inCompPeriod = true;

		new Thread(Olympiad._manager).start();

		ThreadPoolManager.getInstance().schedule(new CompEndTask(), Olympiad.getMillisToCompEnd());

		Announcements.getInstance().announceToAll(Msg.THE_OLYMPIAD_GAME_HAS_STARTED);
		Olympiad._log.info("Olympiad System: Olympiad Game Started");
		if(ConfigValue.OlympiadStatEnable)
			OlympiadStat.update_week_stat();
	}
}