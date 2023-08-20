package ai.hellbound;

import bosses.BelethManager;
import l2open.gameserver.ai.Mystic;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;
import l2open.util.Util;

public class Beleth extends Mystic
{
	private final L2Skill Bleed = SkillTable.getInstance().getInfo(5495, 1);
	private final L2Skill HornOfRising = SkillTable.getInstance().getInfo(5497, 1);
	private static final L2Skill Fireball = SkillTable.getInstance().getInfo(5496, 1);

	public Beleth(L2Character actor)
	{
		super(actor);
		AI_TASK_DELAY = 1000;
		AI_TASK_ACTIVE_DELAY = 1000;
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		BelethManager.setBelethDead();
		super.MY_DYING(killer);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean createNewTask()
	{
		L2NpcInstance actor = getActor();
		if (actor == null)
		{
			return true;
		}

		if (!BelethManager.getZone().checkIfInZone(actor))
		{
			teleportHome(true);
			return false;
		}

		clearTasks();

		L2Character target;
		if ((target = prepareTarget()) == null)
		{
			return false;
		}

		if (!BelethManager.getZone().checkIfInZone(target))
		{
			target.removeFromHatelist(actor, false);
			return false;
		}
		if(Rnd.chance(80) && !actor.isCastingNow() && Util.checkIfInRange(900, actor, target, false))
		{
			addTaskCast(target, Fireball);
		}
		return true;
	}

	/** если видит хилл скил
	 * @param skill
	 * @param caster
	 */
	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		L2NpcInstance npc = getActor();
		if (npc != null && !npc.isDead() && (npc.getNpcId() == 29118) && !npc.isCastingNow() && skill.getSkillType() == L2Skill.SkillType.HEAL && Rnd.get(100) < 80 && Util.checkIfInRange(600, npc, caster, false))
		{
			clearTasks();
			addTaskCast(caster, HornOfRising);
		}
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	public void checkAggression(L2Character target)
	{
		super.checkAggression(target);
	}

	@Override
	protected void onEvtSpawn()
	{
		L2NpcInstance actor = getActor();
		if (actor.getNpcId() == 29118)
		{
			actor.setRunning();
			if (actor.getAroundCharacters(300, 200).size() > 2 && Rnd.get(100) < 60)
			{
				clearTasks();
				addTaskCast((L2Character) actor.getTarget(), Bleed);
			}
		}
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		L2NpcInstance actor = getActor();
		if (actor != null && !BelethManager.getZone().checkIfInZone(actor))
		{
			teleportHome(true);
		}
		return false;
	}
}