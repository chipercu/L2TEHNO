package ai.hellbound;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * Original Sin Warden 6го этажа Tully Workshop
 */
public class OriginalSinWarden extends Fighter
{
	private static final int[] servants1 = { 22424, 22425, 22426, 22427, 22428, 22429, 22430 };
	private static final int[] servants2 = { 22432, 22433, 22434, 22435, 22436, 22437, 22438 };
	private static final int[] DarionsFaithfulServants = { 22405, 22406, 22407 };
	private static final int[] DarionsFaithfulServants1 = { 22408, 22409, 22410 };

	public OriginalSinWarden(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		L2NpcInstance actor = getActor();
		switch(actor.getNpcId())
		{
			case 22423:
			{
				for(int i = 0; i < servants1.length; i++)
					try
					{
						L2Spawn sp = new L2Spawn(NpcTable.getTemplate(servants1[i]));
						sp.setLoc(Location.findPointToStay(actor, 150, 350));
						sp.doSpawn(true);
						sp.stopRespawn();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				break;
			}
			case 22431:
			{
				for(int i = 0; i < servants2.length; i++)
					try
					{
						L2Spawn sp = new L2Spawn(NpcTable.getTemplate(servants2[i]));
						sp.setLoc(Location.findPointToStay(actor, 150, 350));
						sp.doSpawn(true);
						sp.stopRespawn();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				break;
			}
			default:
				break;
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();

		if(Rnd.chance(15))
			try
			{
				switch(actor.getNpcId())
				{
					case 22423:
						L2Spawn sp = new L2Spawn(NpcTable.getTemplate(DarionsFaithfulServants[Rnd.get(DarionsFaithfulServants.length-1)]));
						sp.setLoc(Location.findPointToStay(actor, 150, 350));
						sp.setAIParam("ROOM="+actor.getAI().ROOM);
						sp.doSpawn(true);
						sp.stopRespawn();
						break;
					case 22431:
						L2Spawn sp1 = new L2Spawn(NpcTable.getTemplate(DarionsFaithfulServants1[Rnd.get(DarionsFaithfulServants1.length-1)]));
						sp1.setLoc(Location.findPointToStay(actor, 150, 350));
						sp1.setAIParam("ROOM="+actor.getAI().ROOM);
						sp1.doSpawn(true);
						sp1.stopRespawn();
						break;
					default:
						break;
						
				}

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		super.MY_DYING(killer);
	}

}