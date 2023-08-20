package ai;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;
import l2open.util.Util;

public class NpcAttacker extends AbstractSOIMonster
{
	private static final L2Skill[] skill_list = { SkillTable.getInstance().getInfo(5909, 1), SkillTable.getInstance().getInfo(5910, 1) };
	private int list;

	public NpcAttacker(L2Character actor)
	{
		super(actor);
		AI_TASK_DELAY = 1000;
		AI_TASK_ACTIVE_DELAY = 1000;
	}

	public NpcAttacker(L2NpcInstance actor, int arg)
	{
		super(actor);
		AI_TASK_DELAY = 1000;
		AI_TASK_ACTIVE_DELAY = 1000;
		list = arg;
	}

	@Override
	protected boolean defaultFightTask()
	{
		L2NpcInstance npc = getActor();
		if(npc == null || npc.isDead())
			return false;
		L2Character mob = getAttackTarget();
		if(mob != null && mob.getNpcId() == list)
		{
			L2Skill skill = skill_list[Rnd.get(2)];
			addTaskCast(mob, skill);
			mob.getAI().notifyEvent(CtrlEvent.EVT_SEE_SPELL, skill, npc);
			return true;
		}
		return super.defaultFightTask();
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance npc = getActor();
		if(npc == null)
			return true;
		if(npc.getTarget() == null || !npc.getTarget().isMonster())
		{
			for(L2NpcInstance npc2 : npc.getAroundNpc(1000, 200))
			{
				if (npc2.getNpcId() == list)
				{
					npc.startConfused();
					npc.setTarget(npc2);
					npc.addDamageHate(npc2, 0, 500);
					setIntention(CtrlIntention.AI_INTENTION_ATTACK, npc2, null);
					break;
				}
			}
		}
		return super.thinkActive();
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(getActor().isConfused())
			getActor().stopConfused();
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}