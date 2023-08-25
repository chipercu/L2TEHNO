package ai.hellbound;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;

/**
 * Darion Challenger 7го этажа Tully Workshop
 */
public class DarionChallenger extends Fighter
{
	private static final int TeleportCube = 32467;

	public DarionChallenger(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		if(checkAllDestroyed())
			try
			{
					L2Spawn sp = new L2Spawn(NpcTable.getTemplate(TeleportCube));
					sp.setLoc(new Location(-12527, 279714, -11622, 16384));
					sp.doSpawn(true);
					sp.stopRespawn();
					ThreadPoolManager.getInstance().schedule(new Unspawn(), 600 * 1000L); // 10 mins
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		super.MY_DYING(killer);
	}

	private static boolean checkAllDestroyed()
	{
		if(!L2ObjectsStorage.getAllByNpcId(25600, true).isEmpty())
			return false;
		if(!L2ObjectsStorage.getAllByNpcId(25601, true).isEmpty())
			return false;
		if(!L2ObjectsStorage.getAllByNpcId(25602, true).isEmpty())
			return false;

		return true;
	}

	private class Unspawn extends l2open.common.RunnableImpl
	{
		public Unspawn()
		{}

		@Override
		public void runImpl()
		{
			for(L2NpcInstance npc : L2ObjectsStorage.getAllByNpcId(TeleportCube, true))
				npc.deleteMe();
		}
	}
}