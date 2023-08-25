package ai.FieldOfSilenceWhispers;

import l2open.config.ConfigValue;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * АИ для дропа хербов в Field of Silence\Whispers.
 */

public class ai_reed_herb extends DefaultAI
{
	private L2Character myself = null;
	private int loot_extra01 = 14827;
	private int loot_extra02 = 14825;
	private int loot_extra03 = 14826;
	private int loot_extra04 = 14824;
	private int base_extra_roll = 100;
	private int loot01_extra_roll = 50;
	private int loot02_extra_roll = 5;
	private int loot03_extra_roll = 25;
	private int loot04_extra_roll = 10;
	private int TID_SWAMP_CHECK = 78001;
	private int TIME_SWAMP_CHECK = 240;

	public ai_reed_herb(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		if(ConfigValue.ai_reed_herb_enable)
		{
			if(myself.getZ() >= -3800 && myself.getZ() <= -3750)
			{
				if(Rnd.get(base_extra_roll) <= loot04_extra_roll )
				{
					DropItem1(getActor(),loot_extra04,1);
				}
				else if( Rnd.get(base_extra_roll) <= loot03_extra_roll )
				{
					DropItem1(getActor(),loot_extra03,1);
				}
				else if( Rnd.get(base_extra_roll) <= loot02_extra_roll )
				{
					DropItem1(getActor(),loot_extra02,1);
				}
				else if( Rnd.get(base_extra_roll) <= loot01_extra_roll )
				{
					DropItem1(getActor(),loot_extra01,1);
				}
			}
			AddTimerEx(TID_SWAMP_CHECK,( TIME_SWAMP_CHECK * 1000 ));
		}
		AddMoveAroundDesire(3000,50);
	}

	@Override
	public void NO_DESIRE()
	{
		AddMoveAroundDesire(3000,50);
		super.NO_DESIRE();
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TID_SWAMP_CHECK)
		{
			if(myself.getZ() >= -3800 && myself.getZ() <= -3750 )
			{
				if( Rnd.get(base_extra_roll) <= loot04_extra_roll )
				{
					DropItem1(getActor(),loot_extra04,1);
				}
				else if( Rnd.get(base_extra_roll) <= loot03_extra_roll )
				{
					DropItem1(getActor(),loot_extra03,1);
				}
				else if( Rnd.get(base_extra_roll) <= loot02_extra_roll )
				{
					DropItem1(getActor(),loot_extra02,1);
				}
				else if( Rnd.get(base_extra_roll) <= loot01_extra_roll )
				{
					DropItem1(getActor(),loot_extra01,1);
				}
			}
			AddTimerEx(TID_SWAMP_CHECK,( TIME_SWAMP_CHECK * 1000 ));
		}
	}
}
