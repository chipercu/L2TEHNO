package ai.hellbound;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;

/**
 * AI боса Demon Prince для Tower of Infinitum:
 * - при смерти спаунит портал.
 * - на 10% ХП использует скилл NPC Ultimate Defense(5044.3)
 */
public class DemonPrince extends Fighter
{
	private static final int ULTIMATE_DEFENSE_SKILL_ID = 5044;
	private static final L2Skill ULTIMATE_DEFENSE_SKILL = SkillTable.getInstance().getInfo(ULTIMATE_DEFENSE_SKILL_ID, 3);
	private static final int TELEPORTATION_CUBIC_ID = 32374;
	private static final Location CUBIC_POSITION = new Location(-22144, 278744, -8239, 0);
	private boolean _notUsedUltimateDefense = true;

	public DemonPrince(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(_notUsedUltimateDefense && actor.getCurrentHpPercents() < 10)
		{
			_notUsedUltimateDefense = false;

			clearTasks();
			addTaskBuff(actor, ULTIMATE_DEFENSE_SKILL);
		}

		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		_notUsedUltimateDefense = true;

		L2NpcInstance cubic = new L2NpcInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(TELEPORTATION_CUBIC_ID));
		cubic.setSpawnedLoc(CUBIC_POSITION);
		cubic.setReflection(actor.getReflection()); // Задаём рефлект.
		cubic.onSpawn();
		cubic.spawnMe(CUBIC_POSITION);

		if(killer != null && killer.isPlayable())
		{
			Reflection ref = killer.getPlayer().getReflection();
			if(ref != null)
			{
				for(L2Player p : ref.getPlayers())
					p.setVarInst(ref.getName(), String.valueOf(System.currentTimeMillis()));
			}
		}

		super.MY_DYING(killer);
	}
}