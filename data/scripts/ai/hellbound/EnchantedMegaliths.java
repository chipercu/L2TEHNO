package ai.hellbound;

import l2open.gameserver.ai.Fighter;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SpawnTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * AI для 6го уровня хб
 *
 * @author Drizzy
 */
 
public class EnchantedMegaliths	extends Fighter
{
	private static int Megalith = 18484;
	private static int Hellinark = 22326;
	private static int[] MegalithMinions = { 22422, 22328 };
	private static Location[] MegalithSpawns = { new Location(-24542, 245792, -3133), new Location(-23839,246056,-3133), new Location(-23713,244358,-3133), new Location(-23224,244524,-3133), new Location(-24709,245186,-3133), new Location(-24394,244379,-3133) };

	public EnchantedMegaliths(L2Character actor)
	{
	super(actor);
	}
	
	protected void MY_DYING(L2Character killer)
	{
		boolean completed = getMegalithsCompleted();
		int id = getActor().getNpcId();
		int curHBLevel = HellboundManager.getInstance().getLevel();
		switch (id)
		{
		case 22326:
			if (curHBLevel == 6)
			{
				HellboundManager.getInstance().addPoints(10000);
				if(completed == true)
					HellboundManager.getInstance().changeLevel(7);
			}
		break;
		case 22422:
		case 22328:
			if(curHBLevel > 2 && curHBLevel == 6 )
			{
				changeMegalithMinion(1);
				
				int curr = getMegalith();
				int curr1 = getMegalithMinion();
				if(curr >= 6)
					if(curr1 >= 250)
						ServerVariables.set("MegalithsCompleted", "true");
			}
		break;
		case 18484:
			if(curHBLevel ==6)
			{
			int curr = getMegalith();
			int curr1 = getMegalithMinion();
				if(curr < 6)
					changeMegalith(1);
				if(curr >= 6)
					if(curr1 >= 250)
						ServerVariables.set("MegalithsCompleted", "true");
			}
		break;
		}
		super.MY_DYING(killer);
	}
	
	public static boolean getMegalithsCompleted()
	{
		return ServerVariables.getBool("MegalithsCompleted", false);
	}
	
	public static void changeMegalithMinion(int mod)
	{
		int curr = getMegalithMinion();
		int n = Math.max(0, mod + curr);
		if(curr != n)
		{
			ServerVariables.set("megaliths_killed", n);
		}
	}

	public static int getMegalithMinion()
	{
		return ServerVariables.getInt("megaliths_killed", 0);
	}
	
	
	public static void changeMegalith(int mod)
	{
		int curr = getMegalith();
		int n = Math.max(0, mod + curr);
		if(curr != n)
		{
			ServerVariables.set("megaliths_portals", n);
		}
	}

	public static int getMegalith()
	{
		return ServerVariables.getInt("megaliths_portals", 0);
	}
}