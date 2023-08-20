package l2open.gameserver.model.entity.siege.territory;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.serverpackets.SystemMessage;

import java.util.Calendar;

public class TerritorySiegeEndTask extends l2open.common.RunnableImpl
{
	public void runImpl()
	{
		if(!TerritorySiege.isInProgress())
			return;

		try
		{
			long timeRemaining = TerritorySiege.getSiegeEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
			if(timeRemaining > 3600000)
				ThreadPoolManager.getInstance().schedule(new TerritorySiegeEndTask(), timeRemaining - 3600000); // Prepare task for 1 hr left.
			else if(timeRemaining <= 3600000 && timeRemaining > 1800000)
			{
				TerritorySiege.announceToPlayer(new SystemMessage(SystemMessage.THE_TERRITORY_WAR_WILL_END_IN_S1_HOURS).addNumber(1), true);
				ThreadPoolManager.getInstance().schedule(new TerritorySiegeEndTask(), timeRemaining - 1800000); // Prepare task for 30 minute left.
			}
			else if(timeRemaining <= 1800000 && timeRemaining > 600000)
			{
				TerritorySiege.announceToPlayer(new SystemMessage(SystemMessage.THE_TERRITORY_WAR_WILL_END_IN_S1_MINUTES).addNumber(30), true);
				ThreadPoolManager.getInstance().schedule(new TerritorySiegeEndTask(), timeRemaining - 600000); // Prepare task for 10 minute left.
			}
			else if(timeRemaining <= 600000 && timeRemaining > 300000)
			{
				TerritorySiege.announceToPlayer(new SystemMessage(SystemMessage.THE_TERRITORY_WAR_WILL_END_IN_S1_MINUTES).addNumber(10), true);
				ThreadPoolManager.getInstance().schedule(new TerritorySiegeEndTask(), timeRemaining - 300000); // Prepare task for 5 minute left.
			}
			else if(timeRemaining <= 300000 && timeRemaining > 60000)
			{
				TerritorySiege.announceToPlayer(new SystemMessage(SystemMessage.THE_TERRITORY_WAR_WILL_END_IN_S1_MINUTES).addNumber(5), true);
				ThreadPoolManager.getInstance().schedule(new TerritorySiegeEndTask(), timeRemaining - 60000); // Prepare task for 1 minute left.
			}
			else if(timeRemaining <= 60000 && timeRemaining > 30000)
			{
				TerritorySiege.announceToPlayer(new SystemMessage(SystemMessage.THE_TERRITORY_WAR_WILL_END_IN_S1_MINUTES).addNumber(1), true);
				ThreadPoolManager.getInstance().schedule(new TerritorySiegeEndTask(), timeRemaining - 30000); // Prepare task for 30 seconds count down
			}
			else if(timeRemaining <= 30000 && timeRemaining > 10000)
			{
				TerritorySiege.announceToPlayer(new SystemMessage(SystemMessage.S1_SECONDS_TO_THE_END_OF_TERRITORY_WAR).addNumber(30), true);
				ThreadPoolManager.getInstance().schedule(new TerritorySiegeEndTask(), timeRemaining - 10000); // Prepare task for 10 seconds count down
			}
			else if(timeRemaining <= 10000 && timeRemaining > 0)
			{
				TerritorySiege.announceToPlayer(new SystemMessage(SystemMessage.S1_SECONDS_TO_THE_END_OF_TERRITORY_WAR).addNumber(Math.round(timeRemaining / 1000) + 1), true);
				ThreadPoolManager.getInstance().schedule(new TerritorySiegeEndTask(), timeRemaining); // Prepare task for second count down
			}
			else
				TerritorySiege.endSiege();
		}
		catch(Throwable t)
		{}
	}
}