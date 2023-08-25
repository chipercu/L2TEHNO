package ai;

import l2open.config.ConfigValue;
import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Mystic;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Character.HateInfo;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.serverpackets.PlaySound;
import l2open.gameserver.tables.SkillTable;
import l2open.util.*;
import bosses.FreyaManager;
import bosses.FreyaManager.World;
import l2open.util.GArray;
import java.util.concurrent.ScheduledFuture;

import l2open.gameserver.model.instances.L2NpcInstance.AggroInfo;

/**
 * @author Diagod
 * 27.05.2011
 **/
public class Freya extends Mystic
{
	public Freya(L2Character actor)
	{
		super(actor);
	}

	private int s_freya_eternal_blizzard1 = 6274;
	private int s_freya_eternal_blizzard_power1 = 6275;
	private int s_freya_eternal_blizzard_signal1 = 6276;

	private long _lastActive = 0;
	private long _time = 3000;

	public GArray<ScheduledFuture<?>> _shedule = new GArray<ScheduledFuture<?>>();

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

	@Override
	protected void onEvtAggression(L2Character attacker, int aggro)
	{
		if(attacker == null || attacker.getPlayer() == null)
			return;

		L2NpcInstance actor = getActor();
		if(actor == null || !actor.canAttackCharacter(attacker))
			return;

		L2Player player = attacker.getPlayer();

		actor.setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
		setGlobalAggro(0);

		attacker.addDamageHate(actor, 0, aggro);

		// Обычно 1 хейт добавляется хозяину суммона, чтобы после смерти суммона моб накинулся на хозяина.
		if(attacker.getPlayer() != null && aggro > 0 && (attacker.isSummon() || attacker.isPet()))
			attacker.getPlayer().addDamageHate(actor, 0, searchingMaster && attacker.isInRange(actor, 2000) ? aggro : 1);

		if(!actor.isRunning())
			startRunningTask(AI_TASK_ATTACK_DELAY);

		if(getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
		{
			// Показываем анимацию зарядки шотов, если есть таковые.
			switch(actor.getTemplate().shots)
			{
				case SOUL:
					actor.unChargeShots(false);
					break;
				case SPIRIT:
				case BSPIRIT:
					actor.unChargeShots(true);
					break;
				case SOUL_SPIRIT:
				case SOUL_BSPIRIT:
					actor.unChargeShots(false);
					actor.unChargeShots(true);
					break;
			}

			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
	}

	protected void tryMoveToTarget(L2Character target, int range)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		//_log.info("Freya: tryMoveToTarget["+range+"]"+target);
		range = range == 0 ? actor.getPhysicalAttackRange() : Math.max(0, range);
		if(!actor.followToCharacter(target, range, true, true))
			_pathfind_fails++;

		if(_pathfind_fails >= 10 /*&& System.currentTimeMillis() - (actor.getAttackTimeout() - getMaxAttackTimeout()) < getTeleportTimeout() && actor.isInRange(target, 2000)*/)
		{
			_pathfind_fails = 0;
			HateInfo hate = target.getHateList().get(actor);
			if(hate == null || hate.damage < 100 && hate.hate < 100)
			{
				//returnHome(true);
				//return false;
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
		if(_lastActive + _time > System.currentTimeMillis())
			return false;
		boolean _isAllDead = true;
		for(L2Player player : actor.getReflection().getPlayers())
			if(!player.isDead())
				_isAllDead = false;

		// Если всех убили то через минуту закрываем инстанс...
		if(_isAllDead)
			FreyaManager.batleFailStatic(actor);

		if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE || getIntention() == CtrlIntention.AI_INTENTION_IDLE)
		{
			_lastActive = System.currentTimeMillis();

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
			if(player.isDead() || player.isInvisible() && player.isGM() || !GeoEngine.canAttacTarget(actor, player, false) || player.isInZone(L2Zone.ZoneType.other))
				return false;
			_time = 5000;
			player.addDamageHate(actor, 0, 1000); // Это нужно, чтобы гвард не перестал атаковать цель после первых ударов
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
	public void startAITask()
	{
		final L2NpcInstance actor = getActor();
		if(_aiTask == null && actor != null)
		{
			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					setAITask(actor);
				}
			}, 6000);
		}
		super.startAITask();
	}

	@Override
	public void stopAITask()
	{
		final L2NpcInstance actor = getActor();
		if(_shedule != null)
			for(ScheduledFuture<?> _sh : _shedule)
				if(_sh != null)
				{
					_sh.cancel(false);
					_sh = null;
				}
		if(_shedule != null)
		{
			_shedule.clear();
			_shedule = null;
		}
		super.stopAITask();
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

	public static enum Methods
	{
		AddMoveToDesire,
		AddUseSkillDesire,
		AddTimerEx
	}
	private void runMethod(final Methods method, int time, final Object... arg)
	{
		final L2NpcInstance npc = getActor();
		_shedule.add(ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				switch(method)
				{
					case AddMoveToDesire:
						npc.setRunning();
						npc.moveToLocation((Integer)arg[0],(Integer)arg[1],(Integer)arg[2], 0, false);
						break;
					case AddUseSkillDesire:
						L2Character target = getAttackTarget();
						if(target != null)
						{
							//addTaskCast(target, (L2Skill)arg[0]);
							boolean isAoE = ((L2Skill)arg[0]).getTargetType() == L2Skill.SkillTargetType.TARGET_AURA;
							npc.doCast((L2Skill)arg[0], isAoE ? npc : target, !target.isPlayable());
						}
						break;
					case AddTimerEx:
						World world = FreyaManager.getWorld(npc.getReflectionId());
						if(((String)arg[0]).equalsIgnoreCase("TIMER_eternal_blizzard") && !world.finish)
						{
							boolean isHardmode = world._ishardMode;
							String voice = "";
							screenMessage(npc.getReflection(), 1801111, ScreenMessageAlign.TOP_CENTER, false); // Я чувствую сильное влияние магии!
							switch(Rnd.get(3))
							{
								case 0:
									voice = "SystemMsg_ru.freya_voice_09";
									break;
								case 1:
									voice = "SystemMsg_ru.freya_voice_10";
									break;
								case 2:
									voice = "SystemMsg_ru.freya_voice_11";
									break;
								default:
									System.out.println("Error > FreyaManeger.runMethod.AddTimerEx.Rnd.get(3)");
									break;
							}
							npc.broadcastPacket(new PlaySound(voice));
							if(isHardmode) // HardMode???
							{
								if((world._Force == 0 && npc.getCurrentHp() < npc.getMaxHp() * 0.80) || (world._Force == 1 && npc.getCurrentHp() < npc.getMaxHp() * 0.60) || (world._Force == 2 && npc.getCurrentHp() < npc.getMaxHp() * 0.40) || (world._Force == 3 && npc.getCurrentHp() < npc.getMaxHp() * 0.20))
								{
									runMethod(Methods.AddUseSkillDesire, 2000, getSkill(ConfigValue.FreyaseHardSkill ? 6697 : 6275, 1));
									world._Force++;
								}
								else
									runMethod(Methods.AddUseSkillDesire, 2000, getSkill(6275, 1));
								if(world.stages == 1)
									runMethod(Methods.AddTimerEx, ((Rnd.get(5) + 40) * 1000), "TIMER_eternal_blizzard");
								else if(world.stages == 3)
									runMethod(Methods.AddTimerEx, ((Rnd.get(5) + 35) * 1000), "TIMER_eternal_blizzard");
							}
							else
							{
								runMethod(Methods.AddUseSkillDesire, 2000, getSkill(6274, 1));
								runMethod(Methods.AddTimerEx, ((Rnd.get(5) + 55) * 1000), "TIMER_eternal_blizzard");
							}
						}
						else if(((String)arg[0]).equalsIgnoreCase("TIMER_use_freya_buff"))
						{
							addTaskCast(npc, getSkill(6284, 1));
							runMethod(Methods.AddTimerEx, 15 * 1000, "TIMER_use_freya_buff");
						}
						break;
				}
			}
		},
		time));
	}

	private void setAITask(L2NpcInstance npc)
	{
		World world = FreyaManager.getWorld(npc.getReflectionId());
		if(world == null)
			return;

		boolean isHardmode = world._ishardMode;

		switch(world.stages)
		{
			case 1:
				if(isHardmode)
				{
					//clearNextAction();
					//clearTasks();

					runMethod(Methods.AddUseSkillDesire, 100, getSkill(6285, 1));
					runMethod(Methods.AddMoveToDesire, 3000, 114730,-114805,-11200,50);
					screenMessage(npc.getReflection(), 1801097, ScreenMessageAlign.TOP_CENTER, false); // Фрея начинает двигаться.
					runMethod(Methods.AddTimerEx, 60000, "TIMER_eternal_blizzard", isHardmode);
					//runMethod(Methods.AddTimerEx, 15000, "TIMER_use_freya_buff", isHardmode);
				}
				else
				{
					//clearNextAction();
					//clearTasks();

					runMethod(Methods.AddMoveToDesire, 3000, 114730,-114805,-11200,50);
					screenMessage(npc.getReflection(), 1801097, ScreenMessageAlign.TOP_CENTER, false); // Фрея начинает двигаться.
					runMethod(Methods.AddTimerEx, 60000, "TIMER_eternal_blizzard", isHardmode);
					//runMethod(Methods.AddTimerEx, 15000, "TIMER_use_freya_buff", isHardmode);
				}
				break;
			case 3:
				if(isHardmode)
				{
					//clearNextAction();
					//clearTasks();

					runMethod(Methods.AddUseSkillDesire, 100, getSkill(6285, 1),1000000);
					runMethod(Methods.AddMoveToDesire, 10000, 114730,-114805,-11200,50);
					screenMessage(npc.getReflection(), 1801097, ScreenMessageAlign.TOP_CENTER, false); // Фрея начинает двигаться.
					runMethod(Methods.AddTimerEx, 50000, "TIMER_eternal_blizzard", isHardmode);
					//runMethod(Methods.AddTimerEx, 15000, "TIMER_use_freya_buff", isHardmode);
				}
				else
				{
					//clearNextAction();
					//clearTasks();

					runMethod(Methods.AddMoveToDesire, 5000, 114730,-114805,-11200,50);
					screenMessage(npc.getReflection(), 1801097, ScreenMessageAlign.TOP_CENTER, false); // Фрея начинает двигаться.
					runMethod(Methods.AddTimerEx, 50000, "TIMER_eternal_blizzard", isHardmode);
					//runMethod(Methods.AddTimerEx, 15000, "TIMER_use_freya_buff", isHardmode);
				}
				break;
		}
	}
			/*else if( ( isMove ) == 1 )
			{
				if(Rnd(10000) < 3333 )
				{
					if( gg->Rand(10000) < 5000 )
					{
						if( myself->Skill_GetConsumeMP(Ice_Ball) < ( attacker + 400 ) && myself->Skill_GetConsumeHP(Ice_Ball) < ( attacker + 328 ) && myself->Skill_InReuseDelay(Ice_Ball) == 0 )
						{
							myself->AddUseSkillDesire(attacker,Ice_Ball,0,1,1000000);
						}
					}
					else if( myself->Skill_GetConsumeMP(Ice_Ball) < ( attacker + 400 ) && myself->Skill_GetConsumeHP(Ice_Ball) < ( attacker + 328 ) && myself->Skill_InReuseDelay(Ice_Ball) == 0 )
					{
						myself->AddUseSkillDesire(( myself + 1504 ),Ice_Ball,0,1,1000000);
					}
				}
				if( gg->Rand(10000) < 800 )
				{
					if( gg->Rand(10000) < 5000 )
					{
						if( myself->Skill_GetConsumeMP(Summon_Elemental) < ( attacker + 400 ) && myself->Skill_GetConsumeHP(Summon_Elemental) < ( attacker + 328 ) && myself->Skill_InReuseDelay(Summon_Elemental) == 0 )
						{
							myself->AddUseSkillDesire(attacker,Summon_Elemental,0,1,1000000);
						}
						( myself + 1160 ) = gg->GetIndexFromCreature(attacker);
					}
					else if( myself->Skill_GetConsumeMP(Summon_Elemental) < ( attacker + 400 ) && myself->Skill_GetConsumeHP(Summon_Elemental) < ( attacker + 328 ) && myself->Skill_InReuseDelay(Summon_Elemental) == 0 )
					{
						myself->AddUseSkillDesire(( myself + 1504 ),Summon_Elemental,0,1,1000000);
					}
					( myself + 1160 ) = gg->GetIndexFromCreature(( myself + 1504 ));
				}
				if( gg->Rand(10000) < 1500 )
				{
					if( myself->Skill_GetConsumeMP(Self_Nova) < ( attacker + 400 ) && myself->Skill_GetConsumeHP(Self_Nova) < ( attacker + 328 ) && myself->Skill_InReuseDelay(Self_Nova) == 0 )
					{
						myself->AddUseSkillDesire(attacker,Self_Nova,0,1,1000000);
					}
				}
				if( gg->Rand(10000) < 500 )
				{
							( myself + 1184 ) = 1;
							myself->AddTimerEx(TIMER_enable_death_clack,( 15 * 1000 ));
							h0 = myself->GetMaxHateInfo(0);
							if( myself->IsNullCreature(( h0 + 8 )) == 0 )
							{
								if( debug_mode )
								{
									myself->Say("эLX  а. " + ( ( h0 + 8 ) + 344 ));
								}
								myself->AddUseSkillDesire(( h0 + 8 ),Death_Clack,0,1,10000000);
							}
							if( is_hard_mode != 1 )
							{
								i0 = Death_Clack_Count; // 2
							}
							else
							{
								i0 = Death_Clack_Count_Hard; // 3
							}
							select(i0)
							{
								case 2:
									if( myself->GetHateInfoCount() >= 2 )
									{
										h0 = myself->GetNthHateInfo(0,( gg->Rand(9) + 1 ),0);
										if( myself->IsNullHateInfo(h0) == 0 )
										{
											if( myself->IsNullCreature(( h0 + 8 )) == 0 )
											{
												if( debug_mode )
												{
													myself->Say("эLX  а. " + ( ( h0 + 8 ) + 344 ));
												}
												myself->AddUseSkillDesire(( h0 + 8 ),Death_Clack,0,1,10000000);
											}
										}
									}
									break;
								case 3:
									if( myself->GetHateInfoCount() >= 3 )
									{
										h0 = myself->GetNthHateInfo(0,( gg->Rand(4) + 1 ),0);
										if( myself->IsNullHateInfo(h0) == 0 )
										{
											if( myself->IsNullCreature(( h0 + 8 )) == 0 )
											{
												if( debug_mode )
												{
													myself->Say("эLX  а. " + ( ( h0 + 8 ) + 344 ));
												}
												myself->AddUseSkillDesire(( h0 + 8 ),Death_Clack,0,1,10000000);
											}
										}
										h0 = myself->GetNthHateInfo(0,( gg->Rand(( myself->GetHateInfoCount() - 1 )) + 1 ),0);
										if( myself->IsNullHateInfo(h0) == 0 )
										{
											if( myself->IsNullCreature(( h0 + 8 )) == 0 )
											{
												if( debug_mode )
												{
													myself->Say("эLX  а. " + ( ( h0 + 8 ) + 344 ));
												}
												myself->AddUseSkillDesire(( h0 + 8 ),Death_Clack,0,1,10000000);
											}
										}
									}
									break;
							}
				}
			}*/

	public void screenMessage(Reflection ref, int id, ScreenMessageAlign align, boolean bool)
	{
		for(L2Player members : ref.getPlayers())
			members.sendPacket(new ExShowScreenMessage(id, 6000, align, true, 1, -1, bool));
	}

	private L2Skill getSkill(int id, int level)
	{
		return SkillTable.getInstance().getInfo(id, level);
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