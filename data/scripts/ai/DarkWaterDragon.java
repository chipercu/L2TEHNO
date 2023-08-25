package ai;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * AI моба Dark Water Dragon для Isle of Prayer.<br>
 * - Если был атакован, спавнится 5 миньонов Shade двух видов.<br>
 * - Если осталось меньше половины HP, спавнится еще 5 таких же миньонов.<br>
 * - После смерти, спавнит второго дракона, Fafurion Kindred<br>
 * - Не используют функцию Random Walk, если были заспавнены "миньоны"<br>
 * @author SYS & Diamond
 */
public class DarkWaterDragon extends Fighter
{
	private int _mobsSpawned = 0;
	private static final int FAFURION = 18482;
	private static final int SHADE1 = 22268;
	private static final int SHADE2 = 22269;
	private static final int MOBS[] = { SHADE1, SHADE2 };
	private static final int MOBS_COUNT = 5;

	public DarkWaterDragon(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(actor != null && !actor.isDead())
			switch(_mobsSpawned)
			{
				case 0:
					_mobsSpawned = 1;
					spawnShades(attacker);
					break;
				case 1:
					if(actor.getCurrentHp() - damage < actor.getMaxHp() / 2)
					{
						_mobsSpawned = 2;
						spawnShades(attacker);
					}
					break;
			}

		super.ATTACKED(attacker, damage, skill);
	}

	private void spawnShades(L2Character attacker)
	{
		L2NpcInstance actor = getActor();
		if(actor != null)
			for(int i = 0; i < MOBS_COUNT; i++)
				try
				{
					Location pos = GeoEngine.findPointToStay(actor.getX(), actor.getY(), actor.getZ(), 100, 120, actor.getReflection().getGeoIndex());
					L2Spawn sp = new L2Spawn(NpcTable.getTemplate(MOBS[Rnd.get(MOBS.length)]));
					sp.setLoc(pos);
					sp.stopRespawn();
					L2NpcInstance npc = sp.doSpawn(true);
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		_mobsSpawned = 0;
		L2NpcInstance actor = getActor();
		if(actor != null)
			try
			{
				Location pos = GeoEngine.findPointToStay(actor.getX(), actor.getY(), actor.getZ(), 100, 120, actor.getReflection().getGeoIndex());
				L2Spawn sp = new L2Spawn(NpcTable.getTemplate(FAFURION));
				sp.setLoc(pos);
				sp.stopRespawn();
				sp.doSpawn(true);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		super.MY_DYING(killer);
	}

	@Override
	protected boolean randomWalk()
	{
		return _mobsSpawned == 0;
	}
}