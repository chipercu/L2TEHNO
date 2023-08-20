package l2open.gameserver.ai;

import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;

public class Ranger extends DefaultAI
{
	public Ranger(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		return super.thinkActive() || defaultThinkBuff(10);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		super.ATTACKED(attacker, damage, skill);
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead() || attacker == null || actor.getDistance(attacker) > 200)
			return;

		for(Task task : _task_list)
			if(task != null && task.type == TaskType.MOVE)
				return;

		int posX = actor.getX();
		int posY = actor.getY();
		int posZ = actor.getZ();

		int old_posX = posX;
		int old_posY = posY;
		int old_posZ = posZ;

		int signx = posX < attacker.getX() ? -1 : 1;
		int signy = posY < attacker.getY() ? -1 : 1;

		// int range = (int) ((actor.calculateAttackSpeed()  /1000 * actor.getWalkSpeed() )* 0.71); // was "actor.getPhysicalAttackRange()"    0.71 = sqrt(2) / 2

		int range = (int) (0.71 * actor.calculateAttackDelay() / 1000 * actor.getMoveSpeed());

		posX += signx * range;
		posY += signy * range;
		posZ = GeoEngine.getHeight(posX, posY, posZ, actor.getReflection().getGeoIndex());

		if(GeoEngine.canMoveToCoord(old_posX, old_posY, old_posZ, posX, posY, posZ, actor.getReflection().getGeoIndex()))
		{
			addTaskMove(posX, posY, posZ, false);
			addTaskAttack(attacker);
		}
	}

	@Override
	protected boolean createNewTask()
	{
		return defaultFightTask();
	}

	@Override
	public int getRatePHYS()
	{
		return 25;
	}

	@Override
	public int getRateDOT()
	{
		return 40;
	}

	@Override
	public int getRateDEBUFF()
	{
		return 25;
	}

	@Override
	public int getRateDAM()
	{
		return 50;
	}

	@Override
	public int getRateSTUN()
	{
		return 25;
	}

	@Override
	public int getRateBUFF()
	{
		return 5;
	}

	@Override
	public int getRateHEAL()
	{
		return 50;
	}
}