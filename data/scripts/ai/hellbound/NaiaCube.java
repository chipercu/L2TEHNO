package ai.hellbound;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.instancemanager.NaiaCoreManager;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.DoorTable;

public class NaiaCube extends DefaultAI
{

	public NaiaCube(L2NpcInstance actor)
	{
		super(actor);
		actor.p_block_move(true, null);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		ThreadPoolManager.getInstance().schedule(new Despawn(getActor()), 120 * 1000L);
	}

	private class Despawn extends l2open.common.RunnableImpl
	{
		L2NpcInstance _npc;

		private Despawn(L2NpcInstance npc)
		{
			_npc = npc;
		}

		@Override
		public void runImpl()
		{
			NaiaCoreManager.setZoneActive(false);
			DoorTable.getInstance().getDoor(20240001).openMe();
			DoorTable.getInstance().getDoor(18250025).openMe();
			_npc.deleteMe();
		}
	}
}