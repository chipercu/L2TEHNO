package ai.hellbound;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.DoorTable;

/**
 * Через 10 сек после смерти активирует зону перехода на следующий этаж Tylly's Workshop
 * @author SYS
 */
public class MasterZelos extends Fighter
{
	private static L2Zone _zone;
	private static final int[] doors = { 19260054, 19260053 };
	
	public MasterZelos(L2Character actor)
	{
		super(actor);
		_zone = ZoneManager.getInstance().getZoneById(ZoneType.dummy, 797551);
	}

	@Override
	protected void onEvtSpawn()
	{
	  	ThreadPoolManager.getInstance().schedule(new spawnMasterZelos(), 10000);
		super.onEvtSpawn();
	}

	private class spawnMasterZelos extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null || !actor.isVisible())
				return;
			setZoneInactive();
			//Doors
			for (int door : doors)
			{
				DoorTable.getInstance().getDoor(door).closeMe();
			}
		}
	}


	
	@Override
	protected void MY_DYING(L2Character killer)
	{
		//Doors
		for (int door : doors)
		{
			DoorTable.getInstance().getDoor(door).openMe();
		}
		super.MY_DYING(killer);
		setZoneActive();
	}

	private void setZoneActive()
	{
		_zone.setActive(true);
	}

	private void setZoneInactive()
	{
		_zone.setActive(false);
	}
}