package l2open.gameserver.model.entity.olympiad;

import l2open.gameserver.Announcements;
import l2open.gameserver.instancemanager.OlympiadHistoryManager;
import l2open.gameserver.model.entity.Hero;

public class ValidationTask extends l2open.common.RunnableImpl
{
	@Override
	public void runImpl()
	{
		OlympiadHistoryManager.getInstance().switchData();
		OlympiadDatabase.sortHerosToBe();
		OlympiadDatabase.saveNobleData();
		if(Hero.getInstance().computeNewHeroes(Olympiad._heroesToBe))
			Olympiad._log.warning("Olympiad: Error while computing new heroes!");
		Olympiad._log.warning("Olympiad: Validation Period has ended.");
		Announcements.getInstance().announceToAll("Olympiad Validation Period has ended");
		Olympiad._period = 0;
		Olympiad._currentCycle++;
		OlympiadDatabase.cleanupNobles();
		OlympiadDatabase.loadNoblesRank();
		Olympiad.removeBattlesCount(); // ?
		OlympiadDatabase.setNewOlympiadEnd();
		Olympiad.init();
		OlympiadDatabase.save();
	}
}