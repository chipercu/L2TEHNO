package ai;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import npc.model.HoSBossInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Util;

/**
 * AI босов близнецов Yehan Klodekus и Yehan Klanikus для Seed of Infinity, инстанс Hall of Suffering:
 * - становится неуявзвимым, если далеко от брата
 * - если убивают и при этом у брата более 10% ХП, то ресаемся и хилим себя на 15%
 *
 * @author Diagod
 */
public class HallOfSufferingBoss extends Fighter
{
	private int _brotherId;
	private HoSBossInstance _brother;
	private long _wait_timeout = 0;

	protected ScheduledFuture<?> _mobTask;

	private static final long INVUL_DISTANCE = 300;
	private static final L2Skill SKILL_BUFF = SkillTable.getInstance().getInfo(5934, 1);
	private static final L2Skill SKILL_DEFEAT = SkillTable.getInstance().getInfo(5823, 1);
	private static final L2Skill SKILL_ARISE = SkillTable.getInstance().getInfo(5824, 1);

	private boolean searchBrother()
	{
		HoSBossInstance actor = getActor();
		if(actor == null)
			return false;
		if(_brother == null)
		{
			// Ищем брата не чаще, чем раз в 15 секунд, если по каким-то причинам его нету
			if(System.currentTimeMillis() > _wait_timeout)
			{
				_wait_timeout = System.currentTimeMillis() + 15000;
				for(L2NpcInstance npc : L2World.getAroundNpc(actor))
					if(npc.getNpcId() == _brotherId)
					{
						_brother = (HoSBossInstance)npc;
						return true;
					}
			}
		}
		return false;
	}

	private void startBuffTasks()
	{
		HoSBossInstance actor = getActor();
		if(actor == null)
			return;
		if (_brother != null && !_brother.isStartBuffTask() && !actor.isStartBuffTask() && actor.getCurrentHpPercents() <= 60)
		{
			actor.startBuffTask();
			_mobTask = ThreadPoolManager.getInstance().schedule(new InstanceTask(), 100);
		}
	}

	@Override
	protected void thinkAttack()
	{
		HoSBossInstance actor = getActor();
		if(actor == null)
			return;
		if(_brother == null)
			searchBrother();
		else
		{
			if(!_brother.isDead() && !actor.isInRange(_brother, INVUL_DISTANCE))
				actor.setIsInvul(true);
			else
				actor.setIsInvul(false);
		}
		super.thinkAttack();
	}

	public HallOfSufferingBoss(L2Character actor)
	{
		super(actor);
		if(actor.getNpcId() == 25665)
			_brotherId = 25666;
		else
			_brotherId = 25665;
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		HoSBossInstance actor = getActor();
		if(actor == null)
			return;
		startBuffTasks();
		if(_brother == null)
			searchBrother();
		else if(_brother.getCurrentHpPercents() > 20 && actor.getCurrentHp() - damage < actor.getMaxHp() / 10)
		{
			// Если у брата > 20% ХП, то невозможно опустить ХП ниже 10%
			actor.abortAttack(true, false);
			actor.abortCast(true);
			actor.stopMove();
			clearTasks();
			addTaskBuff(actor, SKILL_DEFEAT);
			addTaskBuff(actor, SKILL_ARISE);
			for(L2Playable playable : L2World.getAroundPlayables(actor))
			{
				if(playable.getTargetId() == actor.getObjectId())
				{
					playable.abortAttack(true, false);
					playable.abortCast(true);
					playable.setTarget(null);
				}
			}
			actor.setCurrentHp(actor.getMaxHp() / 3, true);
			return;
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected boolean thinkActive()
	{
		if(_brother == null)
			searchBrother();
		return super.thinkActive();
	}

	@Override
	public HoSBossInstance getActor()
	{
		return (HoSBossInstance) super.getActor();
	}

	private class InstanceTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			HoSBossInstance npc = getActor();
			if(npc == null || npc.isDead())
				return;
			try
			{
				for(L2Playable playable : npc.getAggroMap().keySet())
				{
					if(playable.isPlayer() && !playable.isDead() && playable.getReflectionId() == npc.getReflectionId() && playable.isInRange(npc, 900) && (Math.abs(playable.getZ() - npc.getZ()) < 200))
						SKILL_BUFF.getEffects(npc, playable, false, false);
				}
				Util.cancelFuture(false, new Future[] { _mobTask });
				_mobTask = ThreadPoolManager.getInstance().schedule(new InstanceTask(), 60000);
			}
			catch (Throwable localThrowable1)
			{
			}
		}
	}
}