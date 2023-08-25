package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.Ranger;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

/**
 * AI для Delu Lizardman Commander Agent ID: 21107
 */
public class DeluLizardmanSpecialCommander extends Ranger
{
	private boolean _firstTimeAttacked = true;

	public DeluLizardmanSpecialCommander(L2Character actor)
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
			if(Rnd.chance(40))
				Functions.npcSay(actor, "Come on, Ill take you on!");
		}
		else if(Rnd.chance(15))
			Functions.npcSay(actor, "How dare you interrupt a sacred duel! You must be taught a lesson!");
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		_firstTimeAttacked = true;
		super.MY_DYING(killer);
	}
}