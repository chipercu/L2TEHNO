package ai;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2NpcInstance;

import java.util.concurrent.locks.ReentrantLock;

/**
*	AI Queen Shyeed для Stakato Nest
*	Накладывает баф\дебаф в зоне. Взависимости от того жив рб или нет.
* 	Author: Drizzy
*	Date: 19.08.10
*/
public class QueenShyeed extends Fighter
{	
	private L2Zone _zone;
	private L2Zone _zone1;
	private L2Zone _zone2;
	protected final ReentrantLock dieLock = new ReentrantLock();
	
	public QueenShyeed(L2Character actor)
	{
		super(actor);
	}

	public boolean isGlobalAI()
	{
		return true;
	}
	
	public final L2Zone getZoneBuff()
	{
		if(_zone == null)
			_zone = ZoneManager.getInstance().getZoneById(ZoneType.other, 999222, true);
		return _zone;
	}

	
	public final L2Zone getZoneDebuff()
	{
		if (_zone1 == null)
			_zone1 = ZoneManager.getInstance().getZoneById(ZoneType.damage, 999223, false);
		return _zone1;
	}
	
	public final L2Zone getZoneDebuffNpc()
	{
		if(_zone2 == null)
			_zone2 = ZoneManager.getInstance().getZoneById(ZoneType.other, 999224, false);
		return _zone2;
	}
	
	@Override
	protected boolean thinkActive()
	{
		return super.thinkActive();
	}
	
	@Override
	protected void onEvtSpawn()
	{
		// при спауне запускаем таймер
		ThreadPoolManager.getInstance().schedule(new activeZone(), 10000);
		super.onEvtSpawn();
	}
	
	@Override
	protected void MY_DYING(L2Character killer)
	{
		dieLock.lock();
		try
		{
			// при смерте меняет зоны (с бафа на дебаф).
			getZoneDebuff().setActive(false);
			getZoneDebuffNpc().setActive(false);
			getZoneBuff().setActive(true);
		}
		finally
		{
			dieLock.unlock();
		}
		super.MY_DYING(killer);
	}
	
	private class activeZone extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null || actor.isDead() || !actor.isVisible())
				return;

			// меняем активность зоны
			getZoneBuff().setActive(false);
			getZoneDebuff().setActive(true);
			getZoneDebuffNpc().setActive(true);
		}
	}
}