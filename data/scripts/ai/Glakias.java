package ai;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Mystic;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Character.HateInfo;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.*;
import l2open.gameserver.model.instances.L2NpcInstance.AggroInfo;

/**
 * @author Diagod
 * 27.05.2011
 **/
public class Glakias extends Mystic
{
	public Glakias(L2Character actor)
	{
		super(actor);
	}

		public L2Character getRandomHated()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return null;
		GArray<AggroInfo> aggroList = actor.getAggroList();

		GArray<AggroInfo> activeList = new GArray<AggroInfo>();
		GArray<AggroInfo> passiveList = new GArray<AggroInfo>();

		for(AggroInfo ai : aggroList)
			if(ai.hate > 0)
			{
				L2Playable cha = ai.attacker;
				if(cha != null)
					if(cha.isStunned() || cha.isSleeping() || cha.isParalyzed() || cha.isAfraid() || cha.isBlocked() || Math.abs(cha.getZ() - actor.getZ()) > 200 || !GeoEngine.canAttacTarget(actor, cha, false))
						passiveList.add(ai);
					else
						activeList.add(ai);
			}

		if(!activeList.isEmpty())
			aggroList = activeList;
		else
			aggroList = passiveList;

		if(!aggroList.isEmpty())
			return aggroList.get(Rnd.get(aggroList.size())).attacker;
		return null;
	}

	protected L2Character prepareTarget()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return null;

		// Новая цель исходя из агрессивности
		L2Character hated = actor.isConfused() && getAttackTarget() != actor ? getAttackTarget() : actor.getMostHated();
		
		if(!GeoEngine.canAttacTarget(actor, hated, false) && GeoEngine.canAttacTarget(actor, actor.getMostHated(), false))
			hated = actor.getMostHated();

		//_log.info("FreyaGuard: prepareTarget["+getAttackTarget()+"]["+actor.getMostHated()+"]");
		// Для "двинутых" боссов, иногда, выбираем случайную цель
		if(!actor.isConfused() && Rnd.chance(isMadness) || !GeoEngine.canAttacTarget(actor, hated, false))
		{
			L2Character randomHated = getRandomHated();
			if(randomHated != null && randomHated != hated && randomHated != actor)
			{
				setAttackTarget(randomHated);
				if(_madnessTask == null && !actor.isConfused())
				{
					actor.startConfused();
					_madnessTask = ThreadPoolManager.getInstance().scheduleAI(new MadnessTask(), 10000);
				}
				return randomHated;
			}
		}

		if(hated != null && hated != actor && !hated.isAlikeDead())
		{
			setAttackTarget(hated);
			return hated;
		}

		returnHome(false);
		return null;
	}

	protected void tryMoveToTarget(L2Character target, int range)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		range = range == 0 ? actor.getPhysicalAttackRange() : Math.max(0, range);
		if(!actor.followToCharacter(target, range, true, true))
			_pathfind_fails++;

		if(_pathfind_fails >= 10 /*&& System.currentTimeMillis() - (actor.getAttackTimeout() - getMaxAttackTimeout()) < getTeleportTimeout() && actor.isInRange(target, 2000)*/)
		{
			_pathfind_fails = 0;
			HateInfo hate = target.getHateList().get(actor);
			if(hate == null || hate.damage < 100 && hate.hate < 100)
			{
			//	returnHome(true);
			//	return false;
			}
			try
			{
				int size = actor.getReflection().getPlayers().size();
				if(size > 0)
				{
					L2Player player = actor.getReflection().getPlayers().get(Rnd.get(size));
					if(!playerAtack(player))
						for(int i=0;i<size;i++)
							if(playerAtack(actor.getReflection().getPlayers().get(i)))
								break;
				}
			}
			catch(Exception e) // игнорируем все ошибки...В частности обычно бьект оаут арей индекс, когда рефлект пустой...
			{
				actor.deleteMe();
			}
			//Location loc = GeoEngine.moveCheckForAI(target.getLoc(), actor.getLoc(), actor.getReflection().getGeoIndex());
			//if(!GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), loc.x, loc.y, loc.z, actor.getReflection().getGeoIndex())) // Для подстраховки
			//	loc = target.getLoc();
			//actor.teleToLocation(loc);
			//_log.info("FreyaGuard: teleToLocation["+loc+"]");
			//actor.broadcastSkill(new MagicSkillUse(actor, actor, 2036, 1, 500, 600000));
			//ThreadPoolManager.getInstance().scheduleAI(new Teleport(GeoEngine.moveCheckForAI(target.getLoc(), actor.getLoc(), actor.getReflection().getGeoIndex())), 500);
		}
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return true;
		if(actor.getReflectionId() < 1)
			actor.deleteMe();
		if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE || getIntention() == CtrlIntention.AI_INTENTION_IDLE)
		{
			try
			{
				int size = actor.getReflection().getPlayers().size();
				if(size > 0)
				{
					L2Player player = actor.getReflection().getPlayers().get(Rnd.get(size));
					if(!playerAtack(player))
						for(int i=0;i<size;i++)
							if(playerAtack(actor.getReflection().getPlayers().get(i)))
								break;
				}
			}
			catch(Exception e)
			{
				actor.deleteMe();
			}
		}
		return true;
	}

	private boolean playerAtack(L2Player player)
	{
		L2NpcInstance actor = getActor();
		if(player != null)
		{
			if(player.isDead() || player.isInvisible() && player.isGM() || player.isInZone(L2Zone.ZoneType.other)/* || !GeoEngine.canAttacTarget(actor, player, false)*/)
				return false;

			player.addDamageHate(actor, 0, Rnd.get(50, 150)); // Это нужно, чтобы гвард не перестал атаковать цель после первых ударов
			actor.setRunning(); // Включаем бег...
			actor.setAttackTimeout(Integer.MAX_VALUE + System.currentTimeMillis()); // Это нужно, чтобы не сработал таймаут
			actor.getAI().setAttackTarget(player); // На всякий случай, не обязательно делать
			actor.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, player, null); // Переводим в состояние атаки
			actor.getAI().addTaskAttack(player); // Добавляем отложенное задание атаки, сработает в самом конце движения
			return true;
		}
		return false;
	}

	@Override
	public void thinkAttack()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		L2Character target;
		if((target = prepareTarget()) == null)
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			return;
		}
		if(target.isDead() || target.getNpcId() == 18855 || target.getNpcId() == 18856 || target.getNpcId() == 18854 || target.getNpcId() == 25699 || target.getNpcId() == 25700 || target.getNpcId() == 18853 || target.getNpcId() == 29179 || target.getNpcId() == 29180 || target.getNpcId() == 29177 || target.getNpcId() == 29178)
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			return;
		}
		super.thinkAttack();
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