package ai;

import l2open.config.ConfigValue;
import l2open.gameserver.Announcements;
import l2open.gameserver.GameTimeController;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

/**
 * AI Shadai для Hellbound.
 * @author Nano
 * @date : 15.06.11    15:20
 * На оффе некоторые ждут по 2 недели.
 */
public class Shadai extends DefaultAI
{
	private static long Check_Shadai = 3 * 60 * 1000; // 3 min
	private long _lastCheck = System.currentTimeMillis();

	public Shadai(L2Character actor)
	{
		super(actor);
		actor.decayMe();
	}

	@Override
	protected boolean thinkActive()
	{
		if(System.currentTimeMillis() - _lastCheck < Check_Shadai)
			return false;
		L2NpcInstance _thisActor = getActor();

		if(HellboundManager.getInstance().getLevel() >= 9)
		{
			if (GameTimeController.getInstance().isNowNight() == true)
			{
				if (!_thisActor.isVisible())
				{
					if (Rnd.chance(ConfigValue.ChanceSpawnShadai))
					{
						_thisActor.toggleVisible();
						if (ConfigValue.AnnounceShadaiSpawn)
							Announcements.getInstance().announceByCustomMessage("ai.Shadai.announce", null);
					}
				}
			}
			else
			{
				if(_thisActor.isVisible())
					_thisActor.toggleVisible();
			}
		}

		_lastCheck = System.currentTimeMillis();

		return true;
	}


	public boolean isGlobalAI()
	{
		return true;
	}

	protected boolean randomWalk()
	{
		return false;
	}
}