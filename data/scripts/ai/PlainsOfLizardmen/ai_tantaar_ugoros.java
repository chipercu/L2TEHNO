package ai.PlainsOfLizardmen;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Mystic;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.util.Rnd;
import npc.model.UgorosKeeperInstance;

/**
 * @author: Drizzy
 * АИ для угороса.
 */

public class ai_tantaar_ugoros extends Mystic
{
	private L2Character myself = null;
	private int TID_HERB_CHECK = 780001;
	private int TIME_HERB_CHECK = 2;
	private int TID_VACANCY_CHECK = 780002;
	private int TIME_VACANCY_CHECK = 5;
	private int TID_EXILE_DELAY = 780003;
	private int TIME_EXILE_DELAY = 3;
	private int SID_DEFAULT = 0;
	private int SID_ENGAGING = 1;
	private int SID_NO_NEED_KOMODO = 0;
	private int SID_SEARCHING_KOMODO = 1;
	private int SID_GOING_KOMODO = 2;
	private L2Character c_ai0;
	private L2Character c_ai2;
	private int i_ai1 = 0;
	private int i_ai2 = 0;
	private int i_ai3 = 0;
	public static int i_quest4 = 0;

	public ai_tantaar_ugoros(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1801077);
		getActor().broadcastPacket(ns);
		i_ai3 = SID_DEFAULT;
		i_ai1 = SID_NO_NEED_KOMODO;
		i_ai2 = 0;
		i_quest4 = 0;
		AddTimerEx(TID_VACANCY_CHECK,((TIME_VACANCY_CHECK * 60) * 1000));
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(IsNullCreature(attacker) == 0)
		{
			c_ai0 = attacker;
		}
		if(i_ai3 == SID_ENGAGING && myself.getCurrentHp() - damage <= (myself.getMaxHp() * 0.800000) && i_ai1 == SID_NO_NEED_KOMODO )
		{
			i_ai1 = SID_SEARCHING_KOMODO;
			i_ai2 = 0;
			c_ai2 = null;
			BroadcastScriptEvent(78010080, myself.getObjectId(), 5000);
			AddTimerEx(TID_HERB_CHECK,( TIME_HERB_CHECK * 1000 ));
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 78010080 && script_event_arg2 != 0)
		{
			if(i_ai1 == SID_SEARCHING_KOMODO && IsNullCreature(GetCreatureFromID(script_event_arg2)) == 0 && GetCreatureFromID(script_event_arg2) != getActor())
			{
				if(i_ai2 == 0 || i_ai2 >= DistFromMe(GetCreatureFromID(script_event_arg2)) )
				{
					c_ai2 = GetCreatureFromID(script_event_arg2);
					i_ai2 = DistFromMe(c_ai2);
				}
			}
		}
		else if(script_event_arg1 == 78010084 && IsNullCreature(L2ObjectsStorage.getCharacter(script_event_arg2)) == 0)
		{
			c_ai0 = GetCreatureFromID(script_event_arg2);
			if(i_ai3 == SID_DEFAULT)
			{
				NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1801078, c_ai0.getName());
				getActor().broadcastPacket(ns);
				i_ai3 = SID_ENGAGING;
				getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, c_ai0, 1);
				getActor().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, c_ai0);
			}
		}
		else if(script_event_arg1 == 78010085)
		{
			if(i_ai1 == SID_GOING_KOMODO)
			{
				NpcSay ns = new NpcSay(getActor(), Say2C.NPC_ALL, 1801081);
				getActor().broadcastPacket(ns);
				c_ai2.removeFromHatelist(getActor(), false);
				i_ai1 = SID_NO_NEED_KOMODO;
				i_ai2 = 0;
				c_ai2 = null;
				if(IsNullCreature(c_ai0) == 0)
				{
					getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, c_ai0, 10);
					getActor().getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, c_ai0, null);
				}
			}
			else if(IsNullCreature(c_ai0) == 0)
			{
				getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, c_ai0, 1);
				getActor().getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, c_ai0, null);
			}
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TID_HERB_CHECK)
		{
			if(IsNullCreature(c_ai2) == 0 && i_ai2 > 0)
			{
				NpcSay ns = new NpcSay(getActor(), Say2C.NPC_ALL, 1801079);
				getActor().broadcastPacket(ns);
				i_ai1 = SID_GOING_KOMODO;
				c_ai2.addDamageHate(getActor(), 100000, 200000); // Это нужно, чтобы гвард не перестал атаковать цель после первых ударов
				getActor().addDamageHate((L2NpcInstance)c_ai2, 100000, 200000); // Это нужно, чтобы гвард не перестал атаковать цель после первых ударов
				getActor().setRunning(); // Включаем бег...
				getActor().setAttackTimeout(Integer.MAX_VALUE + System.currentTimeMillis()); // Это нужно, чтобы не сработал таймаут
				getActor().getAI().setAttackTarget(c_ai2); // На всякий случай, не обязательно делать
				getActor().getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, c_ai2, null); // Переводим в состояние атаки
				getActor().getAI().addTaskAttack(c_ai2); // Добавляем отложенное задание атаки, сработает в самом конце движения
			}
			else
			{
				i_ai1 = SID_NO_NEED_KOMODO;
			}
		}
		else if(timer_id == TID_VACANCY_CHECK)
		{
			AddTimerEx(TID_VACANCY_CHECK,((TIME_VACANCY_CHECK * 60) * 1000));
			if(!getActor().isInCombat() && i_ai3 == SID_ENGAGING)
			{
				NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1801082);
				getActor().broadcastPacket(ns);
				AddTimerEx(TID_EXILE_DELAY, ((TIME_EXILE_DELAY * 60) * 1000));
			}
			else if(i_ai3 == SID_DEFAULT)
			{
				NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1801083);
				getActor().broadcastPacket(ns);
			}
		}
		else if(timer_id == TID_EXILE_DELAY)
		{
			getActor().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			i_ai3 = SID_DEFAULT;
			i_ai1 = SID_NO_NEED_KOMODO;
			i_ai2 = 0;
			c_ai2 = null;
			getActor().setCurrentHp(getActor().getMaxHp(), true);
			UgorosKeeperInstance.setAlreadyEnter(false);
		}
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		if(skill.getId() == 6648 && i_ai1 == SID_GOING_KOMODO)
		{
			getActor().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			NpcSay ns = new NpcSay(getActor(), Say2C.NPC_ALL, 1801080);
			getActor().broadcastPacket(ns);
			i_ai1 = SID_NO_NEED_KOMODO;
			i_ai2 = 0;
			c_ai2 = null;
			if(get_i_quest4() == 0)
			{
				set_i_quest4(1);
			}
			if(IsNullCreature(c_ai0) == 0)
			{
				getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, c_ai0, 10);
				getActor().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, c_ai0);
			}
		}
	}

	@Override
	public void NO_DESIRE()
	{
		if(IsNullCreature(c_ai0) == 0 && i_ai3 == SID_ENGAGING)
		{
			getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, c_ai0, 10);
			getActor().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, c_ai0);
		}
		super.NO_DESIRE();
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		NpcSay ns = new NpcSay(getActor(), Say2C.NPC_SHOUT, 1801084);
		getActor().broadcastPacket(ns);
		UgorosKeeperInstance.setAlreadyEnter(false);
		CreateOnePrivateEx(32740, "npc", "UgorosKeeper", "clearer_mode", 1, ((myself.getX() + Rnd.get(300)) - Rnd.get(300)), ((myself.getY() + Rnd.get(300)) - Rnd.get(300)), myself.getZ(), 0);
		i_ai1 = 0;
		i_ai2 = 0;
		i_ai3 = 0;
		super.MY_DYING(killer);
	}

	public static void set_i_quest4(int value)
	{
		i_quest4 = value;
	}

	public static int get_i_quest4()
	{
		return i_quest4;
	}
}
