package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.Ranger;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

/**
 * AI для Karul Bugbear ID: 20600
 *
 * @author Diamond
 */
public class KarulBugbear extends Ranger
{
	private boolean _firstTimeAttacked = true;

	public KarulBugbear(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		if(_firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			if(Rnd.chance(25))
				Functions.npcSay(actor, "Your rear is practically unguarded!");
		}
		else if(Rnd.chance(10))
			Functions.npcSay(actor, "Watch your back!");
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		_firstTimeAttacked = true;
		super.MY_DYING(killer);
	}
}