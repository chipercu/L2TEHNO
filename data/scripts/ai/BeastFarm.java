package ai;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

/**
 * User: Drizzy
 * Date: 20.05.11
 * Time: 15:41
 * АИ для агресивных мобов на ферме. Атакует мобов которых можно кормить (только 1ю стадию).
 */
public class BeastFarm extends Fighter
{
	private long _lastAttackTime = 0;
	private static final long NextAttack = 120 * 1000; // 120 сек
	private L2NpcInstance monster;
	private static final int[] FEEDABLE_BEASTS = { 18873, 18880, 18887, 18894 };

	public BeastFarm(L2Character actor)
	{
		super(actor);
		AI_TASK_DELAY = 1000;
	}

	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return true;

		if(_lastAttackTime + (NextAttack + Rnd.get(10000, 15000)) < System.currentTimeMillis())
		{
			if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
			{
				if(monster == null)
				    for(L2NpcInstance npc : L2World.getAroundNpc(actor, 1000, 200))
						if(npc.getNpcId() == FEEDABLE_BEASTS[Rnd.get(FEEDABLE_BEASTS.length)])
						{
							npc.addDamageHate(actor, 0, 100);
							monster = npc;
						}
				if(monster != null)
				{
				    setIntention(CtrlIntention.AI_INTENTION_ATTACK, monster);
					_lastAttackTime = System.currentTimeMillis();
				}
			}
		}
		return true;
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		monster = null;
		_lastAttackTime = 0;
		super.MY_DYING(killer);
	}
}
