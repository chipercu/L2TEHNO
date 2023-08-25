package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;

/**
 * AI Ancient Egg
 * @author Diagod
 * @date 21.04.11
 */
public class AncientEgg extends Fighter
{
	private boolean	_firstTimeAttacked	= true;

	public AncientEgg(L2Character actor)
	{
		super(actor);
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(_firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			Functions.npcShout(actor, ":(");
				for(L2NpcInstance npc : L2World.getAroundNpc(actor, 1000, 100))
				{
					npc.clearAggroList(false);
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 10000000);
					npc.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker, null);
				}
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		_firstTimeAttacked = true;
		super.MY_DYING(killer);
	}
}
