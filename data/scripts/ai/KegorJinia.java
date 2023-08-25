package ai;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Mystic;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

/**
 * @author Diagod
 * 27.05.2011
 * Бафают всех в округе и пиздят Фрею...
 **/
public class KegorJinia extends Mystic
{
	private static final L2Skill KEGOR_BUFF = SkillTable.getInstance().getInfo(6289, 1); // 18851
	private static final L2Skill JINIA_BUFF = SkillTable.getInstance().getInfo(6288, 1); // 18850

	public KegorJinia(L2Character actor)
	{
		super(actor);
		ThreadPoolManager.getInstance().schedule(new BUFF(), 100);
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return true;
		if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
			return startAtack();
		return true;
	}

	private boolean startAtack()
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.getReflection() == null || actor.getReflection().getMonsters() == null)
			return true;
		if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
		{
			int size = actor.getReflection().getMonsters().size();
			if(size > 0)
			{
				L2NpcInstance mob = actor.getReflection().getMonsters().get(Rnd.get(size));
				if(!mobAtack(mob))
					for(int i=0;i<size;i++)
						if(mobAtack(actor.getReflection().getMonsters().get(i)))
							break;
			}
		}
		return true;
	}

	private boolean mobAtack(L2NpcInstance mob)
	{
		L2NpcInstance actor = getActor();
		if(mob != null)
		{
			if(mob.isDead())
				return false;
			if(mob.getNpcId() != 29179 && mob.getNpcId() != 29180)
				return false;
			mob.addDamageHate(actor, 0, Rnd.get(50, 150)); // Это нужно, чтобы гвард не перестал атаковать цель после первых ударов
			actor.setRunning(); // Включаем бег...
			actor.setAttackTimeout(Integer.MAX_VALUE + System.currentTimeMillis()); // Это нужно, чтобы не сработал таймаут
			actor.getAI().setAttackTarget(mob); // На всякий случай, не обязательно делать
			actor.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, mob, null); // Переводим в состояние атаки
			actor.getAI().addTaskAttack(mob); // Добавляем отложенное задание атаки, сработает в самом конце движения
			return true;
		}
		return false;
	}

	@Override
	public void onIntentionAttack(L2Character target)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		if(target.isPlayer())
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			return;
		}
		super.onIntentionAttack(target);
	}

	private class BUFF extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			L2NpcInstance npc = getActor();
			if(npc == null || npc.isDead())
				return;
			try
			{
				if(npc.getNpcId() == 18851)
					npc.doCast(KEGOR_BUFF, npc, true);
				else
					npc.doCast(JINIA_BUFF, npc, true);
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				startAtack();
				ThreadPoolManager.getInstance().schedule(new BUFF(), 29000);
			}
			catch (Throwable t)
			{
			}
		}
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	public boolean isNotReturnHome()
	{
		return true;
	}
}