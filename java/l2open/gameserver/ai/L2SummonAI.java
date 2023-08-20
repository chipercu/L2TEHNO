package l2open.gameserver.ai;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2ObjectTasks;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.L2Weapon.WeaponType;

public class L2SummonAI extends L2PlayableAI
{
	public L2SummonAI(L2Summon actor)
	{
		super(actor);
	}

	@Override
	protected void onIntentionActive()
	{
		L2Summon actor = getActor();
		if(actor == null || !actor.isVisible())
			return;

		clearNextAction();

		if(actor.isPosessed())
		{
			actor.setRunning();
			if(getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
				setIntention(CtrlIntention.AI_INTENTION_ATTACK, actor.getPlayer(), null);
			return;
		}

		L2Player owner = actor.getPlayer();
		if(owner == null || owner.isAlikeDead() || actor.getDistance(owner) > 4000 || !owner.isConnected())
		{
			super.onIntentionActive();
			return;
		}

		if(actor.isFollow())
			setIntention(CtrlIntention.AI_INTENTION_FOLLOW, owner, ConfigValue.FollowRange);
		else
			super.onIntentionActive();
	}

	@Override
	protected void onAttackFail()
	{
		L2Summon actor = getActor();
		if(actor != null)
		{
			actor.setFollowTarget(actor.getPlayer());
			actor.setFollowStatus(actor.isFollow(), true);
		}
	}

	@Override
	protected void onEvtThink()
	{
		L2Summon actor = getActor();
		if(actor == null)
			return;

		if(actor.isPosessed())
		{
			setAttackTarget(actor.getPlayer());
			changeIntention(CtrlIntention.AI_INTENTION_ATTACK, actor.getPlayer(), null);
		}

		super.onEvtThink();
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2Summon actor = getActor();
		if(actor == null)
			return;
		if(attacker != null && actor.getPlayer().isDead() && !actor.isPosessed())
			Attack(attacker, false, false);
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void thinkAttack(boolean arrived)
	{
		L2Summon actor = getActor();
		if(actor == null)
			return;

		log("L2SummonAI: thinkAttack["+arrived+"]->start");
		//if(arrived && ConfigValue.DebugOnAction)
		//	l2open.util.Util.test();
		//_log.info("L2SummonAI(427): thinkAttack start");
		if(actor.isPosessed())
			setAttackTarget(actor.getPlayer());

		L2Player player = actor.getPlayer();
		if(player == null)
		{
			onAttackFail();
			return;
		}

		if(actor.isActionsDisabled() || actor.isAttackingDisabled())
		{
			actor.sendActionFailed();
			log("L2SummonAI: thinkAttack-> Err 1");
			return;
		}

		L2Character attack_target = getAttackTarget();
		if(attack_target == null || attack_target.isDead())
		{
			onAttackFail();
			actor.sendActionFailed();
			log("L2SummonAI: thinkAttack-> Err 2");
			return;
		}
		if(!actor.isPosessed() && !(_forceUse ? attack_target.isAttackable(actor) : attack_target.isAutoAttackable(actor)))
		{
			// TODO: 
			//if(arrived)
			//	actor.followToCharacter(attack_target, (int)(actor.getPhysicalAttackRange()*.67+actor.getMinDistance(attack_target)+1), true, true);
		// на ПТСке еще +10 к offset
			//else
			{
				onAttackFail();
				actor.sendActionFailed();
			}
			log("L2SummonAI: thinkAttack-> Err 3");
			return;
		}

		/*if(!arrived)
		{
			clientStopMoving();
			actor.doAttack(attack_target, _forceUse);
			//_log.info("L2SummonAI(445): doAttack");
			return;
		}*/

		int range = actor.getPhysicalAttackRange();
		if(range < 10)
			range = 10;
		L2Weapon weaponItem = actor.getActiveWeaponItem();
		WeaponType w_type = weaponItem != null ? weaponItem.getItemType() : null;
		if(w_type == null)
			w_type = actor.getFistWeaponType();
		boolean bow_eq = w_type != null && (w_type == WeaponType.BOW || w_type == WeaponType.CROSSBOW);
		boolean canSee = ConfigValue.AttackInBarrierSummon && !bow_eq || GeoEngine.canAttacTarget(actor, attack_target, false);

		if(!canSee /*&& (Math.abs(actor.getZ() - attack_target.getZ()) > 200)*/)
		{
			actor.sendPacket(Msg.CANNOT_SEE_TARGET());
			//actor.sendMessage("CANNOT_SEE_TARGET() 2");
			onAttackFail();
			actor.sendActionFailed();
			//_log.info("L2SummonAI(445): doAttack");
			log("L2SummonAI: thinkAttack-> Err 4");
			return;
		}

		range += actor.getMinDistance(attack_target);

		if(actor.isFakeDeath())
			actor.breakFakeDeath();

		/*if(arrived && ConfigValue.Test1 > 0)
		try
		{
			Thread.sleep(ConfigValue.Test1);
		}
		catch(Exception e){}*/

		int offset = (int)Math.ceil(actor.getMinDistance(attack_target));
		if(actor.getPhysicalAttackRange() <= 300)
			offset += (int)(actor.getPhysicalAttackRange()*.67);
		else
			offset += (int)(actor.getPhysicalAttackRange()-100f);
		// на ПТСке еще +10 к offset

		log("L2SummonAI: thinkAttack-> range["+actor.getDistance(attack_target)+"]: "+range);
		//_log.info("L2SummonAI(525): isInRangeZ("+attack_target+", "+rr+")["+actor.isInRangeZ(attack_target, rr)+"] getDistance3D["+actor.getDistance3D(attack_target)+"]["+attack_target.getDistance(actor)+"] getMinDistance["+actor.getMinDistance(attack_target)+"]");
		if(attack_target.isDoor() ? actor.isInRange(attack_target, arrived ? range+40 : range) : actor.isInRangeZ(attack_target, arrived ? range+40 : range))
		{
			if(!canSee)
			{
				actor.sendPacket(Msg.CANNOT_SEE_TARGET());
				//actor.sendMessage("CANNOT_SEE_TARGET() 3");
				onAttackFail();
				actor.sendActionFailed();
				//_log.info("L2SummonAI(522): doAttack");
				log("L2SummonAI: thinkAttack-> Err 5");
				return;
			}

			clientStopMoving(false);
			actor.doAttack(attack_target, _forceUse);
			log("L2SummonAI: thinkAttack-> doAttack 1");
			//_log.info("L2SummonAI(529): doAttack 2");
		}
		else if(!_dontMove)
		{
			//if(arrived)
			//	actor._move_data._moveTask = ThreadPoolManager.getInstance().scheduleAI(new L2ObjectTasks.ExecuteFollow(actor, attack_target, (int)Math.ceil(offset+actor.getMinDistance(attack_target)), true), 500);
			//else
				//actor.followToCharacter(attack_target, offset, true, true);
				actor.followToCharacter(attack_target, offset, true, true);
				log("L2SummonAI: thinkAttack-> follow");
			//ThreadPoolManager.getInstance().execute(new L2ObjectTasks.ExecuteFollow(actor, attack_target, (int)(offset+actor.getMinDistance(attack_target)+1)));
		
			//_log.info("L2SummonAI(534): doAttack");
		}
		else
		{
			actor.sendActionFailed();
			log("L2SummonAI: thinkAttack-> Err 6");
			//_log.info("L2SummonAI(539): doAttack");
		}
	}

	@Override
	public L2Summon getActor()
	{
		return (L2Summon) super.getActor();
	}
}