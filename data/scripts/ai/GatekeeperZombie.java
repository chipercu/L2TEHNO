package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Mystic;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.instances.L2NpcInstance;

/**
 * AI охраны входа в Pagan Temple.<br>
 * <li>кидаются на всех игроков, у которых в кармане нету предмета 8064 или 8067
 * <li>не умеют ходить
 *
 * @author SYS
 */
public class GatekeeperZombie extends Mystic
{
	public GatekeeperZombie(L2Character actor)
	{
		super(actor);
		actor.p_block_move(true, null);
	}

	@Override
	public void checkAggression(L2Character target)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || target == null || !target.isPlayable())
			return;
		if(getIntention() != CtrlIntention.AI_INTENTION_ACTIVE)
			return;
		if(_globalAggro < 0)
			return;
		if(!actor.isInRange(target, actor.getAggroRange()))
			return;
		if(Math.abs(target.getZ() - actor.getZ()) > 400)
			return;
		if(Functions.getItemCount((L2Playable) target, 8067) != 0 || Functions.getItemCount((L2Playable) target, 8064) != 0)
			return;
		if(!GeoEngine.canAttacTarget(actor, target, false))
			return;
		target.addDamageHate(actor, 0, 1);
		setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}