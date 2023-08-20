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

/**
 * AI Female Stakato 
 * @author Drizzy
 * @date 25.08.10
 */
 
public class FemaleSpikedStakato extends Fighter
{	
	public L2Character attacker;
	private static final int MOB = 22622;
	private static final int MOBS_COUNT = 1;
	private static final int MOB1 = 22619;
	private static final int MOBS1_COUNT = 3;
	
	public FemaleSpikedStakato(L2Character actor)
	{
		super(actor);
	}
	
	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		if(actor != null)
			if(Rnd.chance(80))
			{
				for(int i = 0; i < MOBS1_COUNT; i++)
					try
					{
						Location pos = GeoEngine.findPointToStay(actor.getX(), actor.getY(), actor.getZ(), 100, 120, actor.getReflection().getGeoIndex());
						L2Spawn sp = new L2Spawn(NpcTable.getTemplate(MOB1));
						sp.setLoc(pos);
						L2NpcInstance npc = sp.doSpawn(true);
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, Rnd.get(1, 100));
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
			}
			else
			{
				for(int i = 0; i < MOBS_COUNT; i++)
					try
					{
						Location pos = GeoEngine.findPointToStay(actor.getX(), actor.getY(), actor.getZ(), 100, 120, actor.getReflection().getGeoIndex());
						L2Spawn sp = new L2Spawn(NpcTable.getTemplate(MOB));
						sp.setLoc(pos);
						L2NpcInstance npc = sp.doSpawn(true);
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
