package ai;

import java.util.concurrent.ScheduledFuture;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncTemplate;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GCSArray;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * @author Diamond
 */
public class FafurionKindred extends Fighter
{
	private static final int DETRACTOR1 = 22270;
	private static final int DETRACTOR2 = 22271;

	private static final int Spirit_of_the_Lake = 2368;

	private static final int Water_Dragon_Scale = 9691;
	private static final int Water_Dragon_Claw = 9700;

	ScheduledFuture<?> spawnTask1;
	ScheduledFuture<?> spawnTask2;
	ScheduledFuture<?> spawnTask3;
	ScheduledFuture<?> spawnTask4;
	ScheduledFuture<?> poisonTask;
	ScheduledFuture<?> despawnTask;

	GCSArray<L2NpcInstance> spawns = new GCSArray<L2NpcInstance>();

	private static final FuncTemplate ft = new FuncTemplate(null, "Mul", Stats.HEAL_EFFECTIVNESS, 0x90, 0);

	public FafurionKindred(L2Character actor)
	{
		super(actor);
		actor.addStatFunc(ft.getFunc(this));
	}

	@Override
	public void startAITask()
	{
		if(_aiTask == null)
		{
			spawns.clear();

			spawnMob(DETRACTOR1);
			spawnMob(DETRACTOR2);
			spawnMob(DETRACTOR1);
			spawnMob(DETRACTOR2);

			spawnTask1 = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SpawnTask(DETRACTOR1), 2000, 40000);
			spawnTask2 = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SpawnTask(DETRACTOR2), 4000, 40000);
			spawnTask3 = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SpawnTask(DETRACTOR1), 8000, 40000);
			spawnTask4 = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SpawnTask(DETRACTOR2), 10000, 40000);
			poisonTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PoisonTask(), 3000, 3000);
			despawnTask = ThreadPoolManager.getInstance().schedule(new DeSpawnTask(), 300000);
		}
		super.startAITask();
	}

	@Override
	public void stopAITask()
	{
		if(_aiTask != null)
		{
			if(spawnTask1 != null)
			{
				spawnTask1.cancel(false);
				spawnTask1 = null;
			}
			if(spawnTask2 != null)
			{
				spawnTask2.cancel(false);
				spawnTask2 = null;
			}
			if(spawnTask3 != null)
			{
				spawnTask3.cancel(false);
				spawnTask3 = null;
			}
			if(spawnTask4 != null)
			{
				spawnTask4.cancel(false);
				spawnTask4 = null;
			}
			if(poisonTask != null)
			{
				poisonTask.cancel(false);
				poisonTask = null;
			}
			if(despawnTask != null)
			{
				despawnTask.cancel(false);
				despawnTask = null;
			}
			for(L2NpcInstance npc : spawns)
				if(npc != null)
					npc.deleteMe();
			spawns.clear();
		}
		super.stopAITask();
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead() || skill == null)
			return;
		// Лечим
		if(skill.getId() == Spirit_of_the_Lake)
			actor.setCurrentHp(actor.getCurrentHp() + 3000, false);
		caster.removeFromHatelist(actor, true);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	private class SpawnTask extends l2open.common.RunnableImpl
	{
		private final int _id;

		public SpawnTask(int id)
		{
			_id = id;
		}

		public void runImpl()
		{
			spawnMob(_id);
		}
	}

	private class PoisonTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor != null)
				actor.reduceCurrentHp(500, actor, null, true, false, true, false, true, 500, true, false, false, false); // Травим дракошу ядом
		}
	}

	private class DeSpawnTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor != null)
			{
				// Если продержались 5 минут, то выдаем награду, и деспавним

				dropItem(actor, Water_Dragon_Scale, Rnd.get(1, 2));
				if(Rnd.chance(36))
					dropItem(actor, Water_Dragon_Claw, Rnd.get(1, 3));

				actor.deleteMe();
			}
		}
	}

	private void spawnMob(int id)
	{
		L2NpcInstance actor = getActor();
		if(actor != null)
			try
			{
				Location pos = GeoEngine.findPointToStay(actor.getX(), actor.getY(), actor.getZ(), 100, 120, actor.getReflection().getGeoIndex());
				L2Spawn sp = new L2Spawn(NpcTable.getTemplate(id));
				sp.setLoc(pos);
				spawns.add(sp.doSpawn(true));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	private void dropItem(L2NpcInstance actor, int id, int count)
	{
		L2ItemInstance item = ItemTemplates.getInstance().createItem(id);
		item.setCount(count);
		item.dropToTheGround(null, actor);
	}
}