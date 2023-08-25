package ai.hellbound;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2MinionInstance;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.MinionList;

/**
 * AI Миньона боса Ranku.<br>
 * При смерти превращается в злого миньона Eidolon
 *
 * @author SYS
 */
public class RankuScapegoat extends DefaultAI
{
	private static final int Eidolon_ID = 25543;

	public RankuScapegoat(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		L2MonsterInstance boss = ((L2MinionInstance) actor).getLeader();
		if(boss != null && !boss.isDead())
		{
			Location loc = actor.getLoc();
			L2MinionInstance newMinion = new L2MinionInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(Eidolon_ID), boss);
			newMinion.setSpawnedLoc(loc);
			newMinion.onSpawn();
			newMinion.spawnMe(loc);

			newMinion.getAI().setGlobalAggro(0);
			newMinion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, boss.getRandomHated(), 1);

			MinionList ml = boss.getMinionList();
			if(ml != null)
				ml.addSpawnedMinion(newMinion);
		}

		super.MY_DYING(killer);
	}
}