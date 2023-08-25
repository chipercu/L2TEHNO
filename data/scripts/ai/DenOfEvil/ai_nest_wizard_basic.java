package ai.DenOfEvil;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date: 27.02.2013
 */

public class ai_nest_wizard_basic extends Mystic
{
	private L2NpcInstance myself = null;

	public ai_nest_wizard_basic(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public int HELP_PROB = 800;

	@Override
	public void NO_DESIRE()
	{
		if(getActor().getMyLeader() != null)
		{
			AddFollowDesire(getActor().getMyLeader(),5);
		}
		else
		{
			AddMoveAroundDesire(5,5);
		}
		super.NO_DESIRE();
	}

	@Override
	protected void onEvtClanAttacked(L2Character victim, L2Character attacker, int damage)
	{
		super.onEvtClanAttacked(victim, attacker, damage);
		if(IsNullCreature(victim) == 0 && victim != myself)
		{
			if(IsNullCreature(attacker) == 0)
			{
				int i0 = Rnd.get(10000);
				if(i0 <= HELP_PROB)
				{
					MakeAttackEvent(attacker,damage,0);
				}
			}
		}
		if(getActor().getMyLeader() != null && victim == getActor().getMyLeader())
		{
			if(IsNullCreature(attacker) == 0)
			{
				MakeAttackEvent(attacker,damage,0);
			}
		}
	}

	@Override
	public void PARTY_ATTACKED(L2Character attacker, L2Character party_member_attacked, int damage)
	{
		if(party_member_attacked != myself)
		{
			if(IsNullCreature(attacker) == 0)
			{
				MakeAttackEvent(attacker,1,0);
			}
		}
		super.PARTY_ATTACKED(attacker, party_member_attacked, damage);
	}
}
