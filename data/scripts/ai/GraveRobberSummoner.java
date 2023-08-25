package ai;

import l2open.gameserver.ai.Mystic;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncMul;
import l2open.util.Rnd;

/**
 * При спавне саммонят случайную охрану.
 * Защита прямо пропорциональна количеству охранников.
 * 
 * @author Diamond
 */
public class GraveRobberSummoner extends Mystic
{
	private static final int[] Servitors = { 22683, 22684, 22685, 22686 };

	private int _lastMinionCount = 0;

	public GraveRobberSummoner(L2Character actor)
	{
		super(actor);
	}

	@Override
	public void startAITask()
	{
		if(_aiTask == null)
		{
			L2MonsterInstance actor = (L2MonsterInstance) getActor();
			if(actor != null)
			{
				actor.removeMinions();
				actor.setNewMinionList();
				actor.getMinionList().spawnSingleMinionSync(Servitors[Rnd.get(Servitors.length)]);
				if(Rnd.chance(50))
					actor.getMinionList().spawnSingleMinionSync(Servitors[Rnd.get(Servitors.length)]);
				_lastMinionCount = actor.getMinionList().countSpawnedMinions();
				reapplyFunc(actor, _lastMinionCount);
			}
		}
		super.startAITask();
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2MonsterInstance actor = (L2MonsterInstance) getActor();
		if(actor == null)
			return;
		int minionCount = actor.getMinionList() == null ? 0 : actor.getMinionList().countSpawnedMinions();
		if(minionCount != _lastMinionCount)
		{
			_lastMinionCount = minionCount;
			reapplyFunc(actor, _lastMinionCount);
		}
		super.ATTACKED(attacker, damage, skill);
	}

	private void reapplyFunc(L2NpcInstance actor, int minionCount)
	{
		actor.removeStatsOwner(this);
		if(minionCount > 0)
		{
			actor.addStatFunc(new FuncMul(Stats.p_magical_defence, 0x30, this, (double)minionCount));
			actor.addStatFunc(new FuncMul(Stats.p_physical_defence, 0x30, this, (double)minionCount));
		}
	}
}