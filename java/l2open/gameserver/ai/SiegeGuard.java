package l2open.gameserver.ai;

import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2NpcInstance.AggroInfo;
import l2open.gameserver.model.instances.L2SiegeGuardInstance;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;

public abstract class SiegeGuard extends DefaultAI
{
	public SiegeGuard(L2Character actor)
	{
		super(actor);
		setGlobalAggro(0);
		MAX_Z_AGGRO_RANGE = 500;
		MAX_AGGRO_RANGE = 1000;
	}

	@Override
	public int getMaxPathfindFails()
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxAttackTimeout()
	{
		return Integer.MAX_VALUE;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}

	@Override
	public boolean canSeeInSilentMove(L2Playable target)
	{
		// Осадные гварды могут видеть игроков в режиме Silent Move с вероятностью 10%
		return !target.isSilentMoving() || Rnd.chance(10);
	}

	@Override
	public void checkAggression(L2Character target)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || target == null || !target.isPlayable() || !actor.isAutoAttackable(target))
			return;
		super.checkAggression(target);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(actor != null && attacker != null && attacker.getPlayer() != null)
		{
			L2Clan clan = attacker.getPlayer().getClan();
			if(clan != null && SiegeManager.getSiege(actor, true) == clan.getSiege() && clan.isDefender())
				return;
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{
		L2NpcInstance actor = getActor();
		if(actor != null && target != null && target.getPlayer() != null)
		{
			L2Player player = target.getPlayer();
			L2Clan clan = player.getClan();
			if(clan != null && SiegeManager.getSiege(actor, true) == clan.getSiege() && clan.isDefender())
				return;
			Castle castle = actor.getCastle();
			if(player.getTerritorySiege() > -1 && castle != null && player.getTerritorySiege() == castle.getId())
				return;
		}
		super.onEvtAggression(target, aggro);
	}

	@Override
	protected boolean thinkActive()
	{
		if(_globalAggro < 0)
			_globalAggro++;
		else if(_globalAggro > 0)
			_globalAggro--;

		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return true;

		if(_def_think)
		{
			doTask();
			return true;
		}

		actor.setRunning();
		actor.setAttackTimeout(Long.MAX_VALUE);

		Location sloc = actor.getSpawnedLoc();
		int homeX = actor.getX() - sloc.x;
		int homeY = actor.getY() - sloc.y;

		// Проверка на расстояние до точки спауна
		if(homeX * homeX + homeY * homeY > 10000)
		{
			teleportHome(true);
			return true;
		}

		return false;
	}

	@Override
	protected void thinkAttack()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		actor.setRunning();
		actor.setAttackTimeout(Long.MAX_VALUE);

		Location sloc = actor.getSpawnedLoc();
		int homeX = actor.getX() - sloc.x;
		int homeY = actor.getY() - sloc.y;

		// Проверка на расстояние до точки спауна
		if(homeX * homeX + homeY * homeY > 9000000)
		{
			teleportHome(true);
			return;
		}

		super.thinkAttack();
	}

	private L2Character getMostHated()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return null;

		GArray<AggroInfo> aggroList = actor.getAggroList();

		AggroInfo mosthated1 = null;
		for(AggroInfo ai : aggroList)
			if(mosthated1 == null || mosthated1.attacker == null)
				mosthated1 = ai;
			else if(ai.attacker != null && actor.getDistance(ai.attacker) < actor.getDistance(mosthated1.attacker))
				mosthated1 = ai;

		AggroInfo mosthated2 = null;
		for(AggroInfo ai : aggroList)
			if(mosthated2 == null || mosthated2.attacker == null)
				mosthated2 = ai;
			else if(ai.attacker != null && mosthated2.hate < ai.hate)
				mosthated2 = ai;

		if(mosthated1 != null && mosthated2 != null)
			return Rnd.chance(50) ? mosthated1.attacker : mosthated2.attacker;

		if(mosthated1 != null)
			return mosthated1.attacker;
		if(mosthated2 != null)
			return mosthated2.attacker;
		return null;
	}

	@Override
	public L2Character prepareTarget()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return null;
		// Новая цель исходя из агрессивности
		L2Character hated = getMostHated();
		if(hated != null && hated != actor && !hated.isAlikeDead())
		{
			setAttackTarget(hated);
			return hated;
		}
		returnHome(true);
		return null;
	}

	/*@Override
	public L2SiegeGuardInstance getActor()
	{
		return (L2SiegeGuardInstance) super.getActor();
	}*/
}