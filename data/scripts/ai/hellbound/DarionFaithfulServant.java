package ai.hellbound;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * Darion Faithful Servant 6го этажа Tully Workshop
 */
public class DarionFaithfulServant extends Fighter
{
	private static final int MysteriousAgent = 32372;
	private static final int[][] AGENT_COORDINATES_8ST_ROOM =
	{
		// 8th floor room 1
		{
			-13312, 279172, -10492, -20300
		},
		// 8th floor room 2
		{
			-11696, 280208, -10492, 13244
		},
		// 8th floor room 3
		{
			-13008, 280496, -10492, 27480
		},
		// 8th floor room 4
		{
			-11984, 278880, -10492, -4472
		}
	};
	
	private static final int[][] AGENT_COORDINATES_6ST_ROOM =
	{
		// 6th floor room 1
		{
			-13312, 279172, -13599, -20300
		},
		// 6th floor room 2
		{
			-11696, 280208, -13599, 13244
		},
		// 6th floor room 3
		{
			-13008, 280496, -13599, 27480
		},
		// 6th floor room 4
		{
			-11984, 278880, -13599, -4472
		}
	};

	public DarionFaithfulServant(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		if(Rnd.chance(15))
			try
			{
				if(getActor().getNpcId() == 22405 || getActor().getNpcId() == 22406 || getActor().getNpcId() == 22407)
				{
					L2Spawn sp = new L2Spawn(NpcTable.getTemplate(MysteriousAgent));
					sp.setLoc(new Location(AGENT_COORDINATES_6ST_ROOM[ROOM - 1]));
					sp.doSpawn(true);
					sp.stopRespawn();
					ThreadPoolManager.getInstance().schedule(new Unspawn(), 600 * 1000L); // 10 mins
				}
				else if(getActor().getNpcId() == 22408 || getActor().getNpcId() == 22409 || getActor().getNpcId() == 22410)
				{
					L2Spawn sp = new L2Spawn(NpcTable.getTemplate(MysteriousAgent));
					sp.setLoc(new Location(AGENT_COORDINATES_8ST_ROOM[ROOM - 1]));
					sp.doSpawn(true);
					sp.stopRespawn();
					ThreadPoolManager.getInstance().schedule(new Unspawn(), 600 * 1000L); // 10 mins
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		if (((getActor().getNpcId() - 22404) == 3) || ((getActor().getNpcId() - 22404) == 6))
		{
			getActor().broadcastPacket(new NpcSay(getActor(), Say2C.NPC_SHOUT, 1800130));
		}
		else
		{
			NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1800131, killer.getName());
			getActor().broadcastPacket(ns);
		}
		super.MY_DYING(killer);
	}

	private class Unspawn extends l2open.common.RunnableImpl
	{
		public Unspawn()
		{}

		@Override
		public void runImpl()
		{
			for(L2NpcInstance npc : L2ObjectsStorage.getAllByNpcId(MysteriousAgent, true))
				npc.deleteMe();
		}
	}

}