package ai.hellbound;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;
import zones.TullyWorkshopZone;

/**
 * RB Darion на крыше Tully Workshop
 *
 */
public class Darion extends Fighter
{
	private static final int[] doors = {
			20250009,
			20250008,
			20250004,
			20250005,
			20250006,
			20250007
	};

	public Darion(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		ThreadPoolManager.getInstance().schedule(new spawnDarion(), 10000);
		super.onEvtSpawn();
	}
	
	private class spawnDarion extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null || !actor.isVisible())
				return;
			for(int i = 0; i < 5; i++)
			{
				try
				{
					L2Spawn sp = new L2Spawn(NpcTable.getTemplate(Rnd.get(25614, 25615)));
					sp.setLoc(Location.findPointToStay(actor, 400, 900));
					sp.doSpawn(true);
					sp.stopRespawn();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			for (int door : doors)
			{
				DoorTable.getInstance().getDoor(door).closeMe();
			}
			TullyWorkshopZone.DarionSpawn();
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		//Doors
		for(int i : doors)
		{
			DoorTable.getInstance().getDoor(i).openMe();
		}

		for(L2NpcInstance npc : L2ObjectsStorage.getAllByNpcId(25614, false))
			npc.deleteMe();

		for(L2NpcInstance npc : L2ObjectsStorage.getAllByNpcId(25615, false))
			npc.deleteMe();

		TullyWorkshopZone.DarionDead();

		super.MY_DYING(killer);
	}

}