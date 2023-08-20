package ai.hellbound;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2MinionInstance;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2RabInstance;
import l2open.util.GArray;
import l2open.util.MinionList;
import l2open.util.Rnd;

public class QuarrySlaveKiller extends Fighter
{
	public QuarrySlaveKiller(L2Character actor)
	{
		super(actor);
		AI_TASK_ACTIVE_DELAY = 1000;
		AI_TASK_ATTACK_DELAY = 1000;
		AI_TASK_DELAY = 1000;
	}
	
	@Override
	protected  boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return false;

		GArray<L2NpcInstance> around = actor.getAroundNpc(1000, 300);
		if(around != null && !around.isEmpty())
		{
			for(L2NpcInstance npc : around)
			{
				if(npc.getNpcId() == 32299)
				{
					if(npc instanceof L2RabInstance)
					{
						if(((L2RabInstance) npc).getFollowTask() != null)
						{
							npc.addDamageHate(actor, 0, Rnd.get(50, 100));
							actor.getAI().setAttackTarget(npc);
							actor.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, npc, null);
							actor.getAI().addTaskAttack(npc);
							actor.setRunning();
							if(actor.getNpcId() == 22346)
							{
								MinionList ml = ((L2MonsterInstance) actor).getMinionList();
								if(ml != null)
								{
									for(L2MinionInstance m : ml.getSpawnedMinions())
									{
										npc.addDamageHate(m, 0, Rnd.get(50,100));
										m.getAI().setAttackTarget(npc);
										m.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, npc, null);
										m.getAI().addTaskAttack(npc);
										m.setRunning();
									}
								}
							}
							continue;
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		HellboundManager.getInstance().addPoints(5);
		super.MY_DYING(killer);
	}
}