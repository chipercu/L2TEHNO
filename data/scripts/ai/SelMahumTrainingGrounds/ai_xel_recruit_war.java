package ai.SelMahumTrainingGrounds;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;

/**
 * @author: Drizzy
 * @date: 25.10.2013
 * open-team.ru
 * АИ для мобов в селмахум
 **/

public class ai_xel_recruit_war extends Fighter
{
	private L2Character myself = null;
	public ai_xel_recruit_war(L2Character self)
	{
		super(self);
		myself = self;
	}

	public int trainer_id = -1;
	public int direction = -1;

	@Override
	public void NO_DESIRE()
	{
		if(myself.i_ai5 == 1)
			return;
		if(myself.getX() == getActor().getSpawnedLoc().x && getActor().getSpawnedLoc().y == myself.getY())
		{
			if(direction != myself.getHeading())
			{
				myself.setHeading(direction);
				myself.updateAbnormalEffect();
			}
		}
		else if(myself.i_ai6 == 0)
		{
			InstantTeleport(myself,getActor().getSpawnedLoc().x,getActor().getSpawnedLoc().y,getActor().getSpawnedLoc().z);
			myself.updateAbnormalEffect();
		}
		super.NO_DESIRE();
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		super.ATTACKED(attacker, damage, skill);

		if(myself.i_ai6 == 1)
			return;
		BroadcastScriptEvent((10016 + trainer_id),GetIndexFromCreature(attacker),1000);
	}

	@Override
	protected void onEvtClanAttacked(L2Character attacked_member, L2Character attacker, int damage)
	{
		if(myself.i_ai6 == 1)
			return;
		super.onEvtClanAttacked(attacked_member, attacker, damage);
	}


	@Override
	protected void MY_DYING(L2Character killer)
	{
		myself.i_ai5 = 1;
		super.MY_DYING(killer);
	}
}
