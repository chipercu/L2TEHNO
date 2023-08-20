package ai.hellbound;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;

/**
 * AI боса Ranku для Tower of Infinitum:
 * - при смерти спаунит портал.
 * - убивает своих минионов
 *
 * @author SYS
 */
public class Ranku extends Fighter
{
	private static final int TELEPORTATION_CUBIC_ID = 32375;
	private static final Location CUBIC_POSITION = new Location(-19056, 278732, -15000, 0);
	private static final int SCAPEGOAT_ID = 32305;

	private long _massacreTimer = 0;
	private long _massacreDelay = 30000;

	public Ranku(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		Reflection r = getActor().getReflection();
		if(r != null)
			for(int i = 0; i < 4; i++)
			{
				try
				{
					Location pos = GeoEngine.findPointToStay(getActor().getX(), getActor().getY(), getActor().getZ(), 100, 120, getActor().getReflection().getGeoIndex());
					L2Spawn sp = new L2Spawn(NpcTable.getTemplate(SCAPEGOAT_ID));
					sp.setLoc(pos);
					sp.doSpawn(true);

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
	}

	@Override
	protected void thinkAttack()
	{
		L2NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		if(_massacreTimer + _massacreDelay < System.currentTimeMillis())
		{
			L2NpcInstance victim = getScapegoat();
			_massacreTimer = System.currentTimeMillis();
			if(victim != null)
				actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, victim, 200000);
		}
		super.thinkAttack();
	}

	private L2NpcInstance getScapegoat()
	{
		for(L2NpcInstance n : getActor().getReflection().getMonsters())
			if(n.getNpcId() == SCAPEGOAT_ID && !n.isDead())
				return n;
		return null;
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();

		L2NpcInstance cubic = new L2NpcInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(TELEPORTATION_CUBIC_ID));
		cubic.setSpawnedLoc(CUBIC_POSITION);
		cubic.setReflection(actor.getReflection());
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