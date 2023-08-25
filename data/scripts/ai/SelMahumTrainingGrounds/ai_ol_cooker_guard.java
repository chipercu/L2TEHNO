package ai.SelMahumTrainingGrounds;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * @date: 25.10.2013
 * open-team.ru
 * АИ для для гвардов повара в селмахум
 **/

public class ai_ol_cooker_guard extends Fighter
{
	private L2Character myself = null;
	public ai_ol_cooker_guard(L2Character self)
	{
		super(self);
		myself = self;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}

	@Override
	public boolean isNotReturnHome()
	{
		return true;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		if(IsNullCreature(getActor().getMyLeader()) == 0)
		{
			ChangeMoveType(0);
			if(myself.param1 == 1)
			{
				AddFollowDesire(getActor().getMyLeader(), (90 + Rnd.get(90)));
			}
			else
			{
				AddFollowDesire(getActor().getMyLeader(), (270 - Rnd.get(90)));
			}
		}
	}

	@Override
	public void PARTY_ATTACKED(L2Character attacker, L2Character party_member_attacked, int damage)
	{
		AddAttackDesire(attacker, 1, 5000);
		super.PARTY_ATTACKED( attacker, party_member_attacked, damage);
	}
}
