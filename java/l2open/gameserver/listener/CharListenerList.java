package l2open.gameserver.listener;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.listener.Listener;
import l2open.gameserver.listener.ListenerList;
import l2open.gameserver.listener.actor.OnAttackHitListener;
import l2open.gameserver.listener.actor.OnAttackListener;
import l2open.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2open.gameserver.listener.actor.OnCurrentMpReduceListener;
import l2open.gameserver.listener.actor.OnDeathListener;
import l2open.gameserver.listener.actor.OnKillListener;
import l2open.gameserver.listener.actor.OnMagicHitListener;
import l2open.gameserver.listener.actor.OnMagicUseListener;
import l2open.gameserver.listener.actor.OnMoveListener;
import l2open.gameserver.listener.actor.OnRegenTaskListener;
import l2open.gameserver.listener.actor.OnReviveListener;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.util.Location;

/**
 * @author G1ta0
 */
public class CharListenerList extends ListenerList<L2Character>
{
	final static ListenerList<L2Character> global = new ListenerList<L2Character>();

	protected final L2Character actor;

	public CharListenerList(L2Character actor)
	{
		this.actor = actor;
	}

	public L2Character getActor()
	{
		return actor;
	}

	public static boolean addGlobal(Listener<L2Character> listener)
	{
		return global.add(listener);
	}

	public static boolean removeGlobal(Listener<L2Character> listener)
	{
		return global.remove(listener);
	}

	public void onAttack(L2Character target)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnAttackListener.class.isInstance(listener))
					((OnAttackListener) listener).onAttack(getActor(), target);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnAttackListener.class.isInstance(listener))
					((OnAttackListener) listener).onAttack(getActor(), target);
	}

	public void onAttackHit(L2Character attacker)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnAttackHitListener.class.isInstance(listener))
					((OnAttackHitListener) listener).onAttackHit(getActor(), attacker);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnAttackHitListener.class.isInstance(listener))
					((OnAttackHitListener) listener).onAttackHit(getActor(), attacker);
	}

	public void onMagicUse(L2Skill skill, L2Character target, boolean alt)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnMagicUseListener.class.isInstance(listener))
					((OnMagicUseListener) listener).onMagicUse(getActor(), skill, target, alt);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnMagicUseListener.class.isInstance(listener))
					((OnMagicUseListener) listener).onMagicUse(getActor(), skill, target, alt);
	}

	public void onMagicHit(L2Skill skill, L2Character caster)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnMagicHitListener.class.isInstance(listener))
					((OnMagicHitListener) listener).onMagicHit(getActor(), skill, caster);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnMagicHitListener.class.isInstance(listener))
					((OnMagicHitListener) listener).onMagicHit(getActor(), skill, caster);
	}

	public void onDeath(L2Character killer)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnDeathListener.class.isInstance(listener))
					((OnDeathListener) listener).onDeath(getActor(), killer);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnDeathListener.class.isInstance(listener))
					((OnDeathListener) listener).onDeath(getActor(), killer);
	}

	public void onKill(L2Character victim)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnKillListener.class.isInstance(listener) && !((OnKillListener) listener).ignorePetOrSummon())
					((OnKillListener) listener).onKill(getActor(), victim);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnKillListener.class.isInstance(listener) && !((OnKillListener) listener).ignorePetOrSummon())
					((OnKillListener) listener).onKill(getActor(), victim);
	}

	public void onKillIgnorePetOrSummon(L2Character victim)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnKillListener.class.isInstance(listener) && ((OnKillListener) listener).ignorePetOrSummon())
					((OnKillListener) listener).onKill(getActor(), victim);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnKillListener.class.isInstance(listener) && ((OnKillListener) listener).ignorePetOrSummon())
					((OnKillListener) listener).onKill(getActor(), victim);
	}

	public void onCurrentHpDamage(double damage, L2Character attacker, L2Skill skill, boolean crit)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnCurrentHpDamageListener.class.isInstance(listener))
					((OnCurrentHpDamageListener) listener).onCurrentHpDamage(getActor(), damage, attacker, skill, crit);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnCurrentHpDamageListener.class.isInstance(listener))
					((OnCurrentHpDamageListener) listener).onCurrentHpDamage(getActor(), damage, attacker, skill, crit);
	}

	public void onCurrentMpReduce(final double consumed, final L2Character attacker)
	{
		if(!CharListenerList.global.getListeners().isEmpty())
			for(Listener<L2Character> listener : CharListenerList.global.getListeners())
				if(OnCurrentMpReduceListener.class.isInstance(listener))
					((OnCurrentMpReduceListener) listener).onCurrentMpReduce(getActor(), consumed, attacker);
		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnCurrentMpReduceListener.class.isInstance(listener))
					((OnCurrentMpReduceListener) listener).onCurrentMpReduce(getActor(), consumed, attacker);
	}

	public void onRevive()
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(listener instanceof OnReviveListener)
					((OnReviveListener) listener).onRevive(getActor());

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(listener instanceof OnReviveListener)
					((OnReviveListener) listener).onRevive(getActor());
	}

	public void onAddHp(double addHp)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(listener instanceof OnRegenTaskListener)
					((OnRegenTaskListener) listener).onAddHp(getActor(), addHp);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(listener instanceof OnRegenTaskListener)
					((OnRegenTaskListener) listener).onAddHp(getActor(), addHp);
	}

	public void onAddMp(double addMp)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(listener instanceof OnRegenTaskListener)
					((OnRegenTaskListener) listener).onAddMp(getActor(), addMp);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(listener instanceof OnRegenTaskListener)
					((OnRegenTaskListener) listener).onAddMp(getActor(), addMp);
	}

	public void onMove(final Location loc)
	{
		if(!CharListenerList.global.getListeners().isEmpty())
			for(final Listener<L2Character> listener : CharListenerList.global.getListeners())
				if(OnMoveListener.class.isInstance(listener))
					((OnMoveListener) listener).onMove(getActor(), loc);
		if(!getListeners().isEmpty())
			for(final Listener<L2Character> listener : getListeners())
				if(OnMoveListener.class.isInstance(listener))
					((OnMoveListener) listener).onMove(getActor(), loc);
	}
}
