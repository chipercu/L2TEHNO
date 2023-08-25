package ai.FieldOfSilenceWhispers;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * АИ для всех мобов в Field of Silence\Whispers 100% ПТС.
 */

public class warrior_basic_reed extends Fighter
{
	private int loot01 = 8603;
	private int loot02 = 8604;
	private int loot03 = 8605;
	private int loot04 = 8613;
	private int loot01_roll = 20;
	private int loot02_roll = 10;
	private int loot03_roll = 5;
	private int loot04_roll = 2;
	private int Skill_swamp = 6141;
	private int Skill_onekill = 2900;
	private int Skill_selfslow01 = 6140;
	private int TID_SIGNAL_ROUTINE = 78002;
	private int TIME_SIGNAL_ROUTINE = 30;
	private int i_ai1 = 0;
	private int i0 = 0;
	private int i1 = 0;
	private long spawn_time = 0;
	private L2Character c0;
	private boolean say = false;

	public warrior_basic_reed(L2Character actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected void onEvtSpawn()
	{
		i_ai1 = 0;
		i0 = 0;
		i1 = 0;
		super.onEvtSpawn();
		spawn_time = System.currentTimeMillis();
		if(getActor().getNpcId() == 22654)
		{
			AddUseSkillDesire(getActor(), SkillTable.getInstance().getInfo(Skill_swamp,1),1);
		}
		else if(getActor().getNpcId() == 22655)
		{
			AddUseSkillDesire(getActor(),SkillTable.getInstance().getInfo(Skill_selfslow01,1),1);
		}
		AddTimerEx(TID_SIGNAL_ROUTINE,(TIME_SIGNAL_ROUTINE + Rnd.get(10) * 1000));
	}

	@Override
	public void SEE_CREATURE(L2Character target)
	{
		if(!say)
			if(System.currentTimeMillis() - spawn_time >=  60 * 1000 && getActor().getNpcId() == 22654)
			{
				NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1800858);
				getActor().broadcastPacketToOthers(ns);
				say = true;
			}
		super.SEE_CREATURE(target);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(IsNullCreature(attacker) == 0 && !getActor().isDead())
		{
			if(attacker.isPlayer() || attacker.isSummon() || attacker.isPet())
			{
				if(getActor().getNpcId() == 22650 || getActor().getNpcId() == 22651 || getActor().getNpcId() == 22652 || getActor().getNpcId() == 22653 || getActor().getNpcId() == 22654)
				{
					BroadcastScriptEvent(78010082,attacker.getObjectId(),500);
				}
				if((getActor().getLevel() - attacker.getLevel()) <= 5 && attacker.getEffectList().getEffectsBySkillId(Skill_onekill) != null)
				{
					L2Character c_ai0 = null;
					if(attacker.isPlayer())
					{
						c_ai0 = attacker;
					}
					else if(attacker.isPet() || attacker.isSummon())
					{
						c_ai0 = attacker.getPet().getPlayer();
					}
					getActor().doDie(c_ai0);
				}
				else if(i_ai1 == 0 && getActor().getNpcId() != 22654 && getActor().getNpcId() != 22655)
				{
					if(attacker.isPlayer() && skill != null)
					{
						if(skill.isMagic())
						{
							if(attacker.isPlayer())
							{
								i0 = ((getActor().getLevel() - attacker.getLevel()) * 5);
							}
							else
							{
								i0 = ((attacker.getPet().getPlayer().getLevel() - getActor().getLevel()) * 5);
							}
							if((getActor().getMaxHp() * 0.900000) <= getActor().getCurrentHp() - damage)
							{
								i0 = (i0 + 15);
							}
							else if((getActor().getMaxHp() * 0.800000) <= getActor().getCurrentHp() - damage)
							{
								i0 = (i0 + 10);
							}
							else if((getActor().getMaxHp() * 0.700000) <= getActor().getCurrentHp() - damage)
							{
								i0 = (i0 + 5);
							}
							else if((getActor().getMaxHp() * 0.600000) <= getActor().getCurrentHp() - damage)
							{
								i0 = (i0 - 5);
							}
							else if((getActor().getMaxHp() * 0.500000) <= getActor().getCurrentHp() - damage)
							{
								i0 = (i0 - 10);
							}
							else
							{
								i0 = (i0 - 15);
							}
							if(getActor().getRealDistance3D(attacker) > 600)
							{
								i0 = (i0 - 15);
							}
							else if(getActor().getRealDistance3D(attacker) > 500)
							{
								i0 = (i0 - 10);
							}
							else if(getActor().getRealDistance3D(attacker) > 400)
							{
								i0 = (i0 - 5);
							}
							else if(getActor().getRealDistance3D(attacker) > 300)
							{
								i0 = (i0 + 5);
							}
							else if(getActor().getRealDistance3D(attacker) > 200)
							{
								i0 = (i0 + 10);
							}
							else
							{
								i0 = (i0 + 15);
							}
							if(Skill_GetConsumeMP(skill.getId()) > 125)
							{
								i0 = (i0 + 15);
							}
							else if(Skill_GetConsumeMP(skill.getId()) > 100)
							{
								i0 = (i0 + 10);
							}
							else if(Skill_GetConsumeMP(skill.getId()) > 75)
							{
								i0 = (i0 + 5);
							}
							else if(Skill_GetConsumeMP(skill.getId()) > 50)
							{
								i0 = (i0 - 5);
							}
							else if(Skill_GetConsumeMP(skill.getId()) > 25)
							{
								i0 = (i0 - 10);
							}
							else
							{
								i0 = (i0 - 15);
							}
							if(i0 < -30)
							{
								i0 = -30;
							}
							else if(i0 > 30)
							{
								i0 = 30;
							}
							i1 = (Rnd.get(100) + i0);
							if(i1 >= 120)
							{
								if(getActor().getNpcId() == 22650 || getActor().getNpcId() == 22651 || getActor().getNpcId() == 22652 || getActor().getNpcId() == 22653)
								{
									NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1800872);
									getActor().broadcastPacketToOthers(ns);
								}
								else if(getActor().getNpcId() == 22656 || getActor().getNpcId() == 22657)
								{
									NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1800873);
									getActor().broadcastPacketToOthers(ns);
								}
								else if(getActor().getNpcId() == 22658 || getActor().getNpcId() == 22659)
								{
									NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1800874);
									getActor().broadcastPacketToOthers(ns);
								}
								AddUseSkillDesire(getActor(), SkillTable.getInstance().getInfo(Skill_selfslow01, 3), 1);
								getActor().setCurrentHp(getActor().getCurrentHp() * 0.100000, true);
							}
							else if( i1 >= 85 )
							{
								if(getActor().getNpcId() == 22650 || getActor().getNpcId() == 22651 || getActor().getNpcId() == 22652 || getActor().getNpcId() == 22653)
								{
									NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1800857);
									getActor().broadcastPacketToOthers(ns);
								}
								else if( getActor().getNpcId() == 22656 || getActor().getNpcId() == 22657)
								{
									NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1800859);
									getActor().broadcastPacketToOthers(ns);
								}
								else if( getActor().getNpcId() == 22658 || getActor().getNpcId() == 22659)
								{
									NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1800860);
									getActor().broadcastPacketToOthers(ns);
								}
								AddUseSkillDesire(getActor(),SkillTable.getInstance().getInfo(Skill_selfslow01,2),1);
								getActor().setCurrentHp(getActor().getCurrentHp() * 0.500000, true);
							}
						}
					}
					i_ai1 = 1;
				}
			}
			else
			{
				//SetAbilityItemDrop(0); Выключение дропа итемов, нам это не нужно т.к. у нас когда нпс бьёт нпса и так не дропается ничего.
				getActor().setRunning();
				AddFleeDesire(attacker,100); // убегаем..
			}
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TID_SIGNAL_ROUTINE)
		{
			if(!getActor().isDead() && (getActor().getNpcId() == 22650 || getActor().getNpcId() == 22651 || getActor().getNpcId() == 22652 || getActor().getNpcId() == 22653 || getActor().getNpcId() == 22654 || getActor().getNpcId() == 22655))
			{
				BroadcastScriptEvent(78010077,getActor().getObjectId(),300);
			}
			AddTimerEx(TID_SIGNAL_ROUTINE,( TIME_SIGNAL_ROUTINE * 1000 ));
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if((((script_event_arg1 == 78010077 || script_event_arg1 == 78010079) && (getActor().getNpcId() == 22656 || getActor().getNpcId() == 22657 || getActor().getNpcId() == 22658 || getActor().getNpcId() == 22659)) || (script_event_arg1 == 78010078 && (getActor().getNpcId() == 22650 || getActor().getNpcId() == 22651 || getActor().getNpcId() == 22652 || getActor().getNpcId() == 22653 || getActor().getNpcId() == 22654 || getActor().getNpcId() == 22655))) || (script_event_arg1 == 78010082 && getActor().getNpcId() == 22655))
		{
			c0 = L2ObjectsStorage.getCharacter(script_event_arg2);
			if(IsNullCreature(c0) == 0 && !c0.isDead())
			{
				int i = Rnd.get(3);
				if(i == 0)
				{
					if(getActor().getNpcId() == 22656)
					{
						NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1800852);
						getActor().broadcastPacketToOthers(ns);
					}
					else if(getActor().getNpcId() == 22657)
					{
						NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1800853);
						getActor().broadcastPacketToOthers(ns);
					}
					else if(getActor().getNpcId() == 22658)
					{
						NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1800855);
						getActor().broadcastPacketToOthers(ns);
					}
					else if(getActor().getNpcId() == 22659)
					{
						NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1800856);
						getActor().broadcastPacketToOthers(ns);
					}
					else
					{
						NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1800858);
						getActor().broadcastPacketToOthers(ns);
					}
					c0.addDamageHate(getActor(), 0, Rnd.get(50, 150)); // Это нужно, чтобы гвард не перестал атаковать цель после первых ударов
					getActor().setRunning();
					getActor().getAI().setAttackTarget(c0); // На всякий случай, не обязательно делать
					getActor().getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, c0, null); // Переводим в состояние атаки
					getActor().getAI().addTaskAttack(c0); // Добавляем отложенное задание атаки, сработает в самом конце движения
				}
			}
		}
	}

	@Override
	public void ATTACK_FINISHED(L2Character target)
	{
		if((target.isDead() && (target.getNpcId() == 18805 || target.getNpcId() == 18806)) && ( getActor().getNpcId() == 22650 || getActor().getNpcId() == 22651 || getActor().getNpcId() == 22652 || getActor().getNpcId() == 22653 || getActor().getNpcId() == 22654 || getActor().getNpcId() == 22655))
		{
			AddMoveToDesire(getActor().getSpawnedLoc().x,getActor().getSpawnedLoc().y,getActor().getSpawnedLoc().z,1);
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		if(IsNullCreature(killer) == 0)
		{
			if(killer.isPlayer() && killer.isMageClass())
			{
				if(Rnd.get(100) <= loot01_roll)
				{
					DropItem1(getActor(),loot01,1);
				}
				else if(Rnd.get(100) <= loot02_roll)
				{
					DropItem1(getActor(),loot02,1);
				}
				else if(Rnd.get(100) <= loot03_roll)
				{
					DropItem1(getActor(),loot03,1);
				}
				else if(Rnd.get(100) <= loot04_roll)
				{
					DropItem1(getActor(),loot04,1);
				}
			}
		}
		i_ai1 = 0;
		i0 = 0;
		i1 = 0;
		super.MY_DYING(killer);
	}
}