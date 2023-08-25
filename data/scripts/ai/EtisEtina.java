package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;
import l2open.util.NpcUtils;

public class EtisEtina extends Fighter
{
	private boolean summonsReleased = false;
	private L2NpcInstance summon1;
	private L2NpcInstance summon2;

	public EtisEtina(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(actor.getCurrentHpPercents() < 70 && !summonsReleased)
		{
			summonsReleased = true;
			summon1 = NpcUtils.spawnSingle(18950, Location.getAroundPosition(attacker, actor, 50, 150, 10), actor.getReflection().getId(), 0);
			summon2 = NpcUtils.spawnSingle(18951, Location.getAroundPosition(attacker, actor, 50, 150, 10), actor.getReflection().getId(), 0);
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		if(summon1 != null && !summon1.isDead())
			summon1.decayMe();
		if(summon2 != null && !summon2.isDead())
			summon2.decayMe();
		super.MY_DYING(killer);
	}
}