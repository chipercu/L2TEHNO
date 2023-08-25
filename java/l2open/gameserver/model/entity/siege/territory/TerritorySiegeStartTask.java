package l2open.gameserver.model.entity.siege.territory;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.model.entity.residence.Castle;

public class TerritorySiegeStartTask extends l2open.common.RunnableImpl
{
	public void runImpl()
	{
		if(TerritorySiege.isInProgress())
			return;

		try
		{
			long timeRemaining = TerritorySiege.getSiegeDate().getTimeInMillis() - System.currentTimeMillis();
			if(timeRemaining > 7200000)
				ThreadPoolManager.getInstance().schedule(new TerritorySiegeStartTask(), timeRemaining - 7200000); // Prepare task for 2 hr left before siege start.
			else if(timeRemaining <= 7200000 && timeRemaining > 1200000)
			{
				checkRegistrationOver();
				ThreadPoolManager.getInstance().schedule(new TerritorySiegeStartTask(), timeRemaining - 1200000); // Prepare task for 20 minute left.
			}
			else if(timeRemaining <= 1200000 && timeRemaining > 600000)
			{
				checkRegistrationOver();
				TerritorySiege.announceToPlayer(Msg.THE_TERRITORY_WAR_WILL_BEGIN_IN_20_MINUTES_TERRITORY_RELATED_FUNCTIONS_IE__BATTLEFIELD_CHANNEL, false);
				ThreadPoolManager.getInstance().schedule(new TerritorySiegeStartTask(), timeRemaining - 600000); // Prepare task for 10 minute left.
			}
			else if(timeRemaining <= 600000 && timeRemaining > 300000)
			{
				checkRegistrationOver();
				TerritorySiege.announceToPlayer(Msg.TERRITORY_WAR_BEGINS_IN_10_MINUTES, false);
				ThreadPoolManager.getInstance().schedule(new TerritorySiegeStartTask(), timeRemaining - 300000); // Prepare task for 5 minute left.
				if(ConfigValue.TerritorySiegeReturnFlags)
					for(Castle castle : CastleManager.getInstance().getCastles().values())
						castle.clear_flags();
			}
			else if(timeRemaining <= 300000 && timeRemaining > 60000)
			{
				checkRegistrationOver();
				TerritorySiege.announceToPlayer(Msg.TERRITORY_WAR_BEGINS_IN_5_MINUTES, false);
				ThreadPoolManager.getInstance().schedule(new TerritorySiegeStartTask(), timeRemaining - 60000); // Prepare task for 1 minute left.
			}
			else if(timeRemaining <= 60000 && timeRemaining > 0)
			{
				checkRegistrationOver();
				TerritorySiege.announceToPlayer(Msg.TERRITORY_WAR_BEGINS_IN_1_MINUTE, false);
				ThreadPoolManager.getInstance().schedule(new TerritorySiegeStartTask(), timeRemaining); // Prepare task start siege
			}
			else
				TerritorySiege.startSiege();
		}
		catch(Throwable t)
		{}
	}

	private void checkRegistrationOver()
	{
		if(!TerritorySiege.isRegistrationOver() && TerritorySiege.getSiegeRegistrationEndDate().getTimeInMillis() - System.currentTimeMillis() <= 10000)
		{
			TerritorySiege.announceToPlayer(Msg.THE_TERRITORY_WAR_REQUEST_PERIOD_HAS_ENDED, false);
			TerritorySiege.setRegistrationOver(true);
		}
	}
}