package ai.hellbound;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;

/**
 * AI Steel Citadel Keymaster в городе-инстанте на Hellbound<br>
 * - кричит когда его атакуют первый раз
 * - портает к себе Amaskari, если был атакован
 * - не использует random walk
 * 
 * @author SYS
 */
public class SteelCitadelKeymaster extends Fighter
{
	private boolean _firstTimeAttacked = true;
	private static final int AMASKARI_ID = 22449;

	public SteelCitadelKeymaster(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		if(_firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			Functions.npcSay(actor, 1800078);
			for(L2NpcInstance npc : L2World.getAroundNpc(actor))
				if(npc.getNpcId() == AMASKARI_ID && npc.getReflectionId() == actor.getReflectionId() && !npc.isDead())
				{
					npc.teleToLocation(Location.findPointToStay(actor, 150, 200));
					break;
				}
		}

		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		_firstTimeAttacked = true;
		super.MY_DYING(killer);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}