package ai.hellbound;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.instancemanager.NaiaCoreManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;

import java.util.HashMap;
import java.util.Map;

public class NaiaSpore extends Fighter
{
	private static Map<Integer, Integer> epidosIndex = new HashMap<Integer, Integer>();

	static
	{
		epidosIndex.put(1, 0);
		epidosIndex.put(2, 0);
		epidosIndex.put(3, 0);
		epidosIndex.put(4, 0);
	}

	public NaiaSpore(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		int npcId = actor.getNpcId();
		switch(npcId)
		{
			//fire
			case 25605:
			{
				epidosIndex.put(1, epidosIndex.get(1) + 1);
				break;
			}
				//water
			case 25606:
			{
				epidosIndex.put(2, epidosIndex.get(2) + 1);
				break;
			}
				//wind
			case 25607:
			{
				epidosIndex.put(3, epidosIndex.get(3) + 1);
				break;
			}
				//earth
			case 25608:
			{
				epidosIndex.put(4, epidosIndex.get(4) + 1);
				break;
			}
			default:
				break;
		}

		if(isBossSpawnCondMet() != 0 && !NaiaCoreManager.isBossSpawned())
			NaiaCoreManager.spawnEpidos(isBossSpawnCondMet());

		super.MY_DYING(killer);
	}

	private int isBossSpawnCondMet()
	{
		for(int i = 1; i < 5; i++)
			if(epidosIndex.get(i) >= 100) // 100
				return i;

		return 0;
	}
}