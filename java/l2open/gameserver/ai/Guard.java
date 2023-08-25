package l2open.gameserver.ai;

import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;

public class Guard extends Fighter implements Runnable
{
	public boolean evilGuard = false;

	public Guard(L2Character actor)
	{
		super(actor);
		MAX_Z_AGGRO_RANGE = 500;
	}

	@Override
	protected void onEvtSpawn()
	{
		if(evilGuard)
		{
			L2NpcInstance actor = getActor();
			actor.getTemplate().baseAtkRange = 1000;
			actor.getTemplate().basePAtk = 50000;
			actor.getTemplate().basePDef = 10000;
			actor.getTemplate().baseMDef = 10000;
			actor.getTemplate().shots = L2NpcTemplate.ShotsType.SOUL;
			actor.setRHandId(13467); // Vesper Thrower (лук)
			actor.setLHandId(0); // убираем щит если есть
			actor.setTitle("Крысолов");
		}
	}

	@Override
	public void checkAggression(L2Character target)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || !(target.getKarma() > 0 || evilGuard && target.getPvpFlag() > 0))
			return;
		if(getIntention() != CtrlIntention.AI_INTENTION_ACTIVE)
			return;
		if(_globalAggro < 0)
			return;
		if(target.getHateList().get(actor) == null && !actor.isInRange(target, 600))
			return;
		if(Math.abs(target.getZ() - actor.getZ()) > MAX_Z_AGGRO_RANGE)
			return;
		if(target.isPlayable() && !canSeeInSilentMove((L2Playable) target))
			return;
		if(!GeoEngine.canAttacTarget(actor, target, false))
			return;
		if(target.isPlayer() && ((L2Player) target).isInvisible())
			return;

		if((target.isSummon() || target.isPet()) && target.getPlayer() != null)
			target.getPlayer().addDamageHate(actor, 0, 1);

		target.addDamageHate(actor, 0, 2);

		startRunningTask(2000);
		setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}