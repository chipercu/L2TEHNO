package ai.hellbound;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;

public class Pylon extends Fighter
{
	public Pylon(L2NpcInstance actor)
	{
		super(actor);
		actor.p_block_move(true, null);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		L2NpcInstance actor = getActor();
		for(int i = 0; i < 7; i++)
			try
		{
				L2Spawn sp = new L2Spawn(NpcTable.getTemplate(22422));
				sp.setLoc(Location.findPointToStay(actor, 150, 550));
				sp.doSpawn(true);
				sp.stopRespawn();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}