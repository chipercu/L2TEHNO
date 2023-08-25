package l2open.gameserver.ai;

import l2open.config.ConfigValue;
import l2open.extensions.listeners.MethodCollection;
import l2open.extensions.listeners.engine.DefaultListenerEngine;
import l2open.extensions.listeners.engine.ListenerEngine;
import l2open.extensions.listeners.events.AbstractAI.AbstractAINotifyEvent;
import l2open.extensions.listeners.events.AbstractAI.AbstractAISetIntention;
import l2open.common.RunnableImpl;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.util.*;
import l2open.util.reference.*;

import java.util.logging.Logger;

public abstract class AbstractAI extends RunnableImpl
{
	protected static final Logger _log = Logger.getLogger(AbstractAI.class.getName());

	private HardReference<? extends L2Character> _actor = HardReferences.emptyRef();
	private HardReference<? extends L2Character> _attack_target = HardReferences.emptyRef();
	private CtrlIntention _intention = CtrlIntention.AI_INTENTION_IDLE;

	protected AbstractAI(L2Character actor)
	{
		refreshActor(actor);
	}

	public void runImpl() throws Exception
	{}

	public void refreshActor(L2Character actor)
	{
		if(actor == null)
			_actor = HardReferences.emptyRef();
		else
			_actor = actor.getRef();
	}

	public void changeIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		_intention = intention;
		if(intention != CtrlIntention.AI_INTENTION_CAST && intention != CtrlIntention.AI_INTENTION_ATTACK)
			setAttackTarget(null);
	}

	public final void setIntention(CtrlIntention intention)
	{
		setIntention(intention, null, null);
	}

	public final void setIntention(CtrlIntention intention, Object arg0)
	{
		setIntention(intention, arg0, null);
	}

	public void setIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		setIntention(intention, arg0, arg1, false);
	}

	public void setIntention(CtrlIntention intention, Object arg0, Object arg1, boolean NextAction)
	{
		if(intention != CtrlIntention.AI_INTENTION_CAST && intention != CtrlIntention.AI_INTENTION_ATTACK)
			setAttackTarget(null);

		L2Character actor = getActor();
		if(actor == null)
			return;

		if(!actor.isVisible())
		{
			if(_intention == CtrlIntention.AI_INTENTION_IDLE)
				return;

			intention = CtrlIntention.AI_INTENTION_IDLE;
		}

		if(ConfigValue.DebugOnAction && getActor().isPlayable() && !getActor().isPlayer())
		{
			_log.info("DebugOnAction: ABSTRACT_AI:setIntention->intention="+intention);
			Util.test();
		}

		switch(intention)
		{
			case AI_INTENTION_IDLE:
				onIntentionIdle();
				break;
			case AI_INTENTION_ACTIVE:
				onIntentionActive();
				break;
			case AI_INTENTION_REST:
				onIntentionRest();
				break;
			case AI_INTENTION_ATTACK:
				onIntentionAttack((L2Character) arg0);
				break;
			case AI_INTENTION_CAST:
				onIntentionCast((L2Skill) arg0, (L2Character) arg1, NextAction);
				break;
			case AI_INTENTION_PICK_UP:
				onIntentionPickUp((L2Object) arg0);
				break;
			case AI_INTENTION_INTERACT:
				onIntentionInteract((L2Object) arg0, arg1 == null ? 0 : (Integer)arg1);
				break;
			case AI_INTENTION_FOLLOW:
				onIntentionFollow((L2Character) arg0, (Integer) arg1);
				break;
			case AI_INTENTION_COUPLE_ACTION:
				onIntentionCoupleAction((L2Player)arg0, (Integer)arg1);
				break;
		}
	}

	public final void notifyEvent(CtrlEvent evt)
	{
		notifyEvent(evt, new Object[] {});
	}

	public final void notifyEvent(CtrlEvent evt, Object arg0)
	{
		notifyEvent(evt, new Object[] { arg0 });
	}

	public final void notifyEvent(CtrlEvent evt, Object arg0, Object arg1)
	{
		notifyEvent(evt, new Object[] { arg0, arg1 });
	}

	public void notifyEvent(CtrlEvent evt, Object[] args)
	{
		L2Character actor = getActor();
		if(actor == null || !actor.isVisible())
			return;

		// Правда так не будет работать Ивент, Трансопрт ну и хуй с ним)))
		//getListenerEngine().fireMethodInvoked(new AbstractAINotifyEvent(MethodCollection.AbstractAInotifyEvent, this, new Object[]{evt, args}));

		switch(evt)
		{
			case EVT_THINK:
				onEvtThink();
				break;
			case EVT_ATTACKED:
				ATTACKED((L2Character) args[0], ((Number) args[1]).intValue(), (L2Skill) args[2]);
				break;
			case EVT_CLAN_DEAD:
				CLAN_DIED((L2Character) args[0], (L2Character) args[1]);
				break;
			case EVT_CLAN_ATTACKED:
				onEvtClanAttacked((L2Character) args[0], (L2Character) args[1], ((Number) args[2]).intValue());
				break;
			case EVT_PARTY_DEAD:
				PARTY_DIED((L2Character) args[0], (L2Character) args[1]);
				break;
			case EVT_PARTY_ATTACKED:
				PARTY_ATTACKED((L2Character) args[0], (L2Character) args[1], ((Number) args[2]).intValue());
				break;
			case EVT_AGGRESSION:
				onEvtAggression((L2Character) args[0], ((Number) args[1]).intValue());
				break;
			case EVT_READY_TO_ACT:
				onEvtReadyToAct();
				break;
			case EVT_ARRIVED:
				onEvtArrived();
				break;
			case EVT_ARRIVED_TARGET:
				onEvtArrivedTarget(((Number) args[0]).intValue());
				break;
			case EVT_ARRIVED_BLOCKED:
				onEvtArrivedBlocked((Location) args[0]);
				break;
			case EVT_FORGET_OBJECT:
				onEvtForgetObject((L2Object) args[0]);
				break;
			case EVT_DEAD:
				MY_DYING((L2Character) args[0]);
				break;
			case EVT_FAKE_DEATH:
				onEvtFakeDeath();
				break;
			case EVT_FINISH_CASTING:
				onEvtFinishCasting((L2Skill) args[0], (L2Character) args[1], (L2Character) args[2]);
				break;
			case EVT_SEE_SPELL:
				onEvtSeeSpell((L2Skill) args[0], (L2Character) args[1]);
				break;
			case EVT_SPAWN:
				onEvtSpawn();
				break;
			case EVT_SPELL_SUCCESSED:
				SPELL_SUCCESSED((L2Skill) args[0], (L2Character) args[1]);
				break;
			case EVT_TIMER:
				TIMER_FIRED_EX(((Number) args[0]).intValue(), args);
				break;
			case EVT_FINISH_ATTACK:
				ATTACK_FINISHED((L2Character) args[0]);
				break;

			// ------------------
			case SCR_EVENT:
				SCRIPT_EVENT(((Number) args[0]).intValue(), ((Number) args[1]).intValue(), ((Number) args[2]).intValue());
				break;
			case SCR_EVENT1:
				TIMER_FIRED_EX(((Number) args[0]).intValue(), args);
				break;
			case SCR_EVENT2:
				TIMER_FIRED_EX(((Number) args[0]).intValue(), args);
				break;
		}
	}

	protected void clientActionFailed()
	{
		L2Character actor = getActor();
		if(actor != null && actor.isPlayer())
			actor.sendActionFailed();
	}

	/**
	 * Останавливает движение
	 * @param validate - рассылать ли ValidateLocation
	 */
	public void clientStopMoving(boolean validate)
	{
		L2Character actor = getActor();
		if(actor == null)
			return;
		actor.stopMove(validate, false);
	}

	/**
	 * Останавливает движение и рассылает ValidateLocation
	 */
	public void clientStopMoving()
	{
		L2Character actor = getActor();
		if(actor == null)
			return;
		actor.stopMove();
	}

	public L2Character getActor()
	{
		return _actor.get();
	}

	public CtrlIntention getIntention()
	{
		return _intention;
	}

	public void setAttackTarget(L2Character target)
	{
		if(target == null)
			_attack_target = HardReferences.emptyRef();
		else
			_attack_target = target.getRef();
	}

	public L2Character getAttackTarget()
	{
		return _attack_target.get();
	}

	/** Означает, что AI всегда включен, независимо от состояния региона */
	public boolean isGlobalAI()
	{
		return false;
	}

	/** Запрещает возвращение на точку спауна */
	public boolean isNotReturnHome()
	{
		return false;
	}

	public void setGlobalAggro(long value)
	{}

	public void setMaxPursueRange(int range)
	{}

	@Override
	public String toString()
	{
		return getL2ClassShortName() + " for " + getActor();
	}

	public String getL2ClassShortName()
	{
		return getClass().getName().replaceAll("^.*\\.(.*?)$", "$1");
	}

	protected abstract void onIntentionIdle();

	protected abstract void onIntentionActive();

	protected abstract void onIntentionRest();

	protected abstract void onIntentionAttack(L2Character target);

	protected abstract void onIntentionCast(L2Skill skill, L2Character target, boolean NextAction);

	protected abstract void onIntentionPickUp(L2Object item);

	protected abstract void onIntentionInteract(L2Object object, int type);

	protected abstract void onIntentionCoupleAction(L2Player player, Integer socialId);

	protected abstract void onEvtThink();

	protected abstract void ATTACKED(L2Character attacker, int damage, L2Skill skill);

	protected abstract void CLAN_DIED(L2Character attacked_member, L2Character attacker);

	protected abstract void onEvtClanAttacked(L2Character attacked_member, L2Character attacker, int damage);
	
	protected abstract void PARTY_DIED(L2Character killer, L2Character party_member_died);
	
	protected abstract void PARTY_ATTACKED(L2Character attacker, L2Character party_member_attacked, int damage);

	protected abstract void onEvtAggression(L2Character target, int aggro);

	protected abstract void onEvtReadyToAct();

	protected abstract void onEvtArrived();

	protected abstract void onEvtArrivedTarget(int i);

	protected abstract void onEvtArrivedBlocked(Location blocked_at_pos);

	protected abstract void onEvtForgetObject(L2Object object);

	protected abstract void MY_DYING(L2Character killer);

	protected abstract void onEvtFakeDeath();

	protected abstract void onEvtFinishCasting(L2Skill skill, L2Character actor, L2Character target);

	protected abstract void onEvtSeeSpell(L2Skill skill, L2Character caster);
	
	protected abstract void SPELL_SUCCESSED(L2Skill skill, L2Character actor);

	protected abstract void onEvtSpawn();

	protected abstract void onIntentionFollow(L2Character target, Integer offset);

	protected abstract void TIMER_FIRED_EX(int timerId, Object[] arg);

	protected abstract void ATTACK_FINISHED(L2Character target);

	protected abstract void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2);
	
	protected abstract void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3);

	protected abstract void NO_DESIRE();

	public void nextAttack()
	{}
}