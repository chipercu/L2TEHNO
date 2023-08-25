package ai;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;

public class SpawnNewNpc extends Fighter
{
	public int chance = 0;
	public int spawn_min = 0;
	public int spawn_max = 0;
	public int spawn_id = -1;

	public SpawnNewNpc(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		if(actor != null && actor.param1 == 0)
			if(Rnd.chance(chance))
			{
				for(int i = 0; i < Rnd.get(spawn_min,spawn_max); i++)
					try
					{
						Location pos = GeoEngine.findPointToStay(actor.getX() + 30, actor.getY() + Rnd.get(-50, 30), actor.getZ(), 100, 120, actor.getReflection().getGeoIndex());
						L2Spawn sp = new L2Spawn(NpcTable.getTemplate(spawn_id == -1 ? actor.getNpcId() : spawn_id));
						sp.setLoc(pos);
						L2NpcInstance npc = sp.doSpawn(true, false, 1, 0, 0, null);
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, Rnd.get(1, 100));
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
			}
		super.MY_DYING(killer);
	}
}