package ai.hellbound;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * AI NPC Sandstorm (ID: 32350)
 * - Используют randomWalk
 * - Все время бегают
 * - Если находят чара в радиусе 200, то на него сперва используют стан - 5435, затем "пинок" - 5494
 * - Цепляют даже тех, кто находится в режиме SilentMove
 * - Никогда и никого не атакуют
 * @author SYS & Diamond
 */
public class Sandstorm extends DefaultAI
{
	private static final int AGGRO_RANGE = 200;
	private static final L2Skill SKILL1 = SkillTable.getInstance().getInfo(5435, 1);
	private static final L2Skill SKILL2 = SkillTable.getInstance().getInfo(5494, 1);
	private long lastThrow = 0;
	protected static final int WALK_RANGE = 300;

	public Sandstorm(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return true;

		if(lastThrow + 5000 < System.currentTimeMillis())
			for(L2Playable target : L2World.getAroundPlayables(actor, AGGRO_RANGE, AGGRO_RANGE))
				if(target != null && !target.isAlikeDead() && !target.isInvul() && target.isVisible() && GeoEngine.canAttacTarget(actor, target, false))
				{
					actor.doCast(SKILL1, target, true);
					actor.doCast(SKILL2, target, true);
					lastThrow = System.currentTimeMillis();
					break;
				}

		return super.thinkActive();
	}

	@Override
	protected void thinkAttack()
	{}

	@Override
	protected void onIntentionAttack(L2Character target)
	{}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{}

	@Override
	protected void onEvtAggression(L2Character attacker, int aggro)
	{}

	@Override
	protected void onEvtClanAttacked(L2Character attacked_member, L2Character attacker, int damage)
	{}

	@Override
	protected boolean randomWalk()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return false;

		Location sloc = actor.getSpawnedLoc();

		int x = sloc.x + Rnd.get(2 * WALK_RANGE) - WALK_RANGE;
		int y = sloc.y + Rnd.get(2 * WALK_RANGE) - WALK_RANGE;
		int z = GeoEngine.getHeight(x, y, sloc.z, actor.getReflection().getGeoIndex());

		actor.setRunning();
		actor.moveToLocation(x, y, z, 0, true);

		return true;
	}
}