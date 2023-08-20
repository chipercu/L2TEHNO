package l2open.gameserver.ai;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Skill.NextAction;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.serverpackets.MyTargetSelected;
import l2open.gameserver.serverpackets.MoveToPawn;
import l2open.gameserver.serverpackets.Say2;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.util.*;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import static l2open.gameserver.ai.CtrlIntention.*;

public class L2PlayableAI extends L2CharacterAI
{
	private boolean thinking = false; // to prevent recursive thinking

	private Object _intention_arg0 = null, _intention_arg1 = null;
	private L2Skill _skill;

	private nextAction _nextAction;
	private Object _nextAction_arg0;
	private Object _nextAction_arg1;
	private boolean _nextAction_arg2;
	private boolean _nextAction_arg3;
	private boolean _nextAction_arg4;
	private boolean _nextCast;

	protected boolean _forceUse;
	protected boolean _dontMove;

	private ScheduledFuture<?> _followTask;

	public L2PlayableAI(L2Playable actor)
	{
		super(actor);
	}

	public enum nextAction
	{
		ATTACK,
		CAST,
		MOVE,
		REST,
		PICKUP,
		INTERACT, 
		EQIP,
		COUPLE_ACTION
	}

	@Override
	public void changeIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		super.changeIntention(intention, arg0, arg1);
		_intention_arg0 = arg0;
		_intention_arg1 = arg1;
	}

	@Override
	public void setIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		_intention_arg0 = null;
		_intention_arg1 = null;
		super.setIntention(intention, arg0, arg1);
	}

	@Override
	protected void onIntentionCast(L2Skill skill, L2Character target, boolean NextActionCast)
	{
		_skill = skill;
		_nextCast = NextActionCast;
		super.onIntentionCast(skill, target, NextActionCast);
	}
	
	public void setNextAction(nextAction action, Object arg0, Object arg1, boolean arg2, boolean arg3, boolean arg4)
	{
		_nextAction = action;
		_nextAction_arg0 = arg0;
		_nextAction_arg1 = arg1;
		_nextAction_arg2 = arg2;
		_nextAction_arg3 = arg3;
		_nextAction_arg4 = arg4;
		//_log.info("L2PlayableAI[0]: ["+action+"]:["+arg0+"]["+arg1+"]["+arg2+"]["+arg3+"]["+arg4+"]");
		//Util.test();
	}

	@Override
	public void setNextAction(nextAction action, Object arg0, Object arg1, boolean arg2, boolean arg3)
	{
		_nextAction = action;
		_nextAction_arg0 = arg0;
		_nextAction_arg1 = arg1;
		_nextAction_arg2 = arg2;
		_nextAction_arg3 = arg3;
		//_log.info("L2PlayableAI[1]: ["+action+"]:["+arg0+"]["+arg1+"]["+arg2+"]["+arg3+"]");
		//Util.test();
	}
	
	public nextAction getNextAction()
	{
		return _nextAction;
	}

	public boolean setNextIntention()
	{
		nextAction nextAction = _nextAction;
		Object nextAction_arg0 = _nextAction_arg0;
		Object nextAction_arg1 = _nextAction_arg1;
		boolean nextAction_arg2 = _nextAction_arg2;
		boolean nextAction_arg3 = _nextAction_arg3;
		boolean nextAction_arg4 = _nextAction_arg4;

		L2Playable actor = getActor();
		if(nextAction == null || actor == null)
			return false;

		L2Skill skill;
		L2Character target;
		L2Object object;

		switch(nextAction)
		{
			case ATTACK:
				if(nextAction_arg0 == null || !(nextAction_arg0 instanceof L2Character))
					return false;
				target = (L2Character) nextAction_arg0;
				_forceUse = nextAction_arg2;
				set_dontMove(nextAction_arg3);
				clearNextAction();
				setIntention(AI_INTENTION_ATTACK, target);
				break;
			case CAST:
				if(nextAction_arg0 == null || nextAction_arg1 == null || !(nextAction_arg0 instanceof L2Skill))
					return false;
				skill = (L2Skill) nextAction_arg0;
				target = (L2Character) nextAction_arg1;
				_forceUse = nextAction_arg2;
				set_dontMove(nextAction_arg3);
				clearNextAction();

				if(!skill.checkCondition(actor, target, _forceUse, _dontMove, false))
				{
					if(skill.getNextAction() == NextAction.ATTACK && !actor.equals(target) && !_forceUse && !_dontMove)
					{
						setNextAction(L2PlayableAI.nextAction.ATTACK, target, null, _forceUse, false);
						return setNextIntention();
					}
					return false;
				}
				setIntention(AI_INTENTION_CAST, skill, target, nextAction_arg4);
				break;
			case MOVE:
				if(nextAction_arg0 == null || nextAction_arg1 == null || !(nextAction_arg0 instanceof Location))
					return false;
				Location loc = (Location) nextAction_arg0;
				Integer offset = (Integer) nextAction_arg1;
				clearNextAction();
				actor.moveToLocation(loc, offset, nextAction_arg2);
				break;
			case REST:
				actor.sitDown(false);
				break;
			case INTERACT:
				if(nextAction_arg0 == null || !(nextAction_arg0 instanceof L2Object))
					return false;
				object = (L2Object) nextAction_arg0;
				clearNextAction();
				onIntentionInteract(object, nextAction_arg1 == null ? 0 : (Integer)nextAction_arg1);
				break;
			case PICKUP:
				if(nextAction_arg0 == null)
					return false;
				object = (L2Object) nextAction_arg0;
				clearNextAction();
				onIntentionPickUp(object);
				break;
			case EQIP:
				if(nextAction_arg0 == null || !actor.isPlayer())
					return false;
				((L2Player)actor).tryEqupUneqipItem((L2ItemInstance)nextAction_arg0);
				break;
			case COUPLE_ACTION:
				if(nextAction_arg0 == null || nextAction_arg1 == null)
					return false;
				target = (L2Character)nextAction_arg0;
				Integer socialId = (Integer)nextAction_arg1;
				_forceUse = nextAction_arg2;
				_nextAction = null;
				clearNextAction();
				onIntentionCoupleAction((L2Player)target, socialId);
				break;
			default:
				return false;
		}
		return true;
	}

	@Override
	protected void onIntentionCoupleAction(L2Player player, Integer socialId)
	{
		clearNextAction();
		changeIntention(CtrlIntention.AI_INTENTION_COUPLE_ACTION, player, socialId);
		onEvtThink();
	}

	@Override
	public void clearNextAction()
	{
		_nextAction = null;
		_nextAction_arg0 = null;
		_nextAction_arg1 = null;
		_nextAction_arg2 = false;
		_nextAction_arg3 = false;
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character actor, L2Character target)
	{
		if(!setNextIntention())
			setIntention(AI_INTENTION_ACTIVE);
	}

	@Override
	protected void onEvtReadyToAct()
	{
		if(!setNextIntention())
			onEvtThink();
	}

	@Override
	protected void onEvtArrived()
	{
		if(!setNextIntention())
			if(getIntention() == AI_INTENTION_INTERACT || getIntention() == AI_INTENTION_PICK_UP)
				onEvtThink();
			else if(getIntention() == AI_INTENTION_CAST) // TODO: тест
				thinkCast(true, false);
			else if(getActor() != null && getActor().isPlayer())
				changeIntention(AI_INTENTION_ACTIVE, null, null);
	}

	@Override
	protected void onEvtArrivedTarget(int i)
	{
		switch(getIntention())
		{
			case AI_INTENTION_ATTACK:
				thinkAttack(true);
				break;
			case AI_INTENTION_CAST:
				thinkCast(true, false);
				break;
			case AI_INTENTION_FOLLOW:
				try
				{
					if(_followTask != null)
						_followTask.cancel(false);
				}
				catch(Exception e)
				{}
				_followTask = ThreadPoolManager.getInstance().scheduleAI(new ThinkFollow((L2Character) _intention_arg0, (Integer) _intention_arg1), 500);
				break;
		}
	}

	@Override
	protected void onEvtThink()
	{
		L2Playable actor = getActor();
		if(actor == null || thinking || actor.isActionsDisabled())
			return;

		thinking = true;

		try
		{
			switch(getIntention())
			{
				case AI_INTENTION_ATTACK:
					thinkAttack(false);
					break;
				case AI_INTENTION_CAST:
					thinkCast(false, _nextCast);
					break;
				case AI_INTENTION_PICK_UP:
					thinkPickUp((L2Object) _intention_arg0);
					break;
				case AI_INTENTION_INTERACT:
					thinkInteract(_intention_arg1 == null ? 0 : (Integer)_intention_arg1, (L2Object) _intention_arg0);
					break;
				case AI_INTENTION_COUPLE_ACTION:
					thinkCoupleAction((L2Player)_intention_arg0, (Integer)_intention_arg1, false);
					break;
			}
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING,"Exception onEvtThink(): " + e);
			e.printStackTrace();
		}
		finally
		{
			thinking = false;
		}
	}

	public class ThinkFollow extends l2open.common.RunnableImpl
	{
		L2Character target;
		Integer offset;

		public ThinkFollow(L2Character t, Integer o)
		{
			target = t;
			offset = o;
		}

		public L2Playable getActor()
		{
			return L2PlayableAI.this.getActor();
		}

		@Override
		public void runImpl()
		{
			if(_followTask != null)
			{
				_followTask.cancel(false);
				_followTask = null;
			}
			L2Playable actor = getActor();
			if(actor == null)
				return;
			log("ThinkFollow: 1");
			if(getIntention() != AI_INTENTION_FOLLOW)
			{
				// Если пет прекратил преследование, меняем статус, чтобы не пришлось щелкать на кнопку следования 2 раза.
				if((actor.isPet() || actor.isSummon()) && getIntention() == AI_INTENTION_ACTIVE)
				{
					actor.setFollowStatus(false, false);
					_followTask = ThreadPoolManager.getInstance().scheduleAI(this, 1000);
				}
				log("ThinkFollow: Err 1");
				return;
			}
			//if(target == null)
			//	target = actor.getFollowTarget();
			if(target == null || (actor.getPlayer() != target && target.isAlikeDead()) || (actor.getDistance(target) > 4000 && !target.isTeleporting()) || target.isInvisible())
			{
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				log("ThinkFollow: Err 2");
				return;
			}
			L2Player actor_player = actor.getPlayer();
			if(actor_player == null || !actor_player.isConnected() || (actor.isPet() || actor.isSummon()) && actor_player.getPet() != actor)
			{
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				log("ThinkFollow: Err 3");
				return;
			}
			if(!actor.isInRange(target, offset + 20) && (!actor.isFollow || actor.getFollowTarget() != target) && !actor.p_block_controll.get())
			{
			//	log("ThinkFollow: ["+(!actor.isInRange(target, offset + 20))+"]["+(!actor.isFollow || actor.getFollowTarget() != target)+"["+(!actor.isFollow)+"]["+(actor.getFollowTarget() != target)+"]]["+(!actor.p_block_controll.get())+"]");
				actor.followToCharacter(target, offset, false, ConfigValue.FollowFindPathType == 0 || !actor.isPlayer());
			}
			//else
			_followTask = ThreadPoolManager.getInstance().scheduleAI(this, 250);
		}
	}

	@Override
	protected void onIntentionInteract(L2Object object, int type)
	{
		L2Playable actor = getActor();
		if(actor == null)
			return;
		if(actor.isActionsDisabled())
		{
			setNextAction(nextAction.INTERACT, object, type, false, false);
			clientActionFailed();
			return;
		}

		clearNextAction();
		changeIntention(AI_INTENTION_INTERACT, object, type);
		onEvtThink();
	}

	protected void thinkInteract(int type, L2Object target)
	{
		log("Debug: L2PlayableAI:thinkInteract["+type+"]");
		L2Playable actor = getActor();
		if(actor == null)
			return;
		else if(target == null)
		{
			setIntention(AI_INTENTION_ACTIVE);
			return;
		}

		int rngAdd = 20;
		if(type == 1)
			rngAdd = 100;
		else if(type > 10)
			rngAdd = ConfigValue.NewGeoEngine ? 250 : 250; // 100|250 - новый мувинг - 100, старый 250

		int range = (int) (Math.max(30, actor.getMinDistance(target)) + rngAdd);

		//_log.info("L2PlayableAI: thinkInteract["+type+"]: "+range);
		if(target.isDoor() ? actor.isInRange(target, range) : actor.isInRangeZ(target, range))
		{
			if(actor.isPlayer())
				((L2Player) actor).doInteract(target, 0/*type == 0 ? 0 : 200*/);
			setIntention(AI_INTENTION_ACTIVE);
		}
		else
		{
			actor.moveToLocation(target.getLoc(), rngAdd == 20 ? 40 : 110, true);
			setNextAction(nextAction.INTERACT, target, type+10, false, false);
		}
	}

	@Override
	protected void onIntentionPickUp(L2Object object)
	{
		L2Playable actor = getActor();
		if(actor == null)
			return;

		if(actor.isActionsDisabled())
		{
			setNextAction(nextAction.PICKUP, object, null, false, false);
			clientActionFailed();
			return;
		}

		clearNextAction();
		changeIntention(AI_INTENTION_PICK_UP, object, null);
		onEvtThink();
	}

	protected void thinkPickUp(final L2Object target)
	{
		final L2Playable actor = getActor();
		if(actor == null)
			return;

		if(target == null)
		{
			setIntention(AI_INTENTION_ACTIVE);
			return;
		}

		if(actor.isInRange(target, 30) && Math.abs(actor.getZ() - target.getZ()) < 50)
		{
			if(actor.isPlayer() || actor.isPet())
				actor.doPickupItem(target);
			setIntention(AI_INTENTION_ACTIVE);
		}
		else
			ThreadPoolManager.getInstance().execute(new l2open.common.RunnableImpl()
			{
				public void runImpl()
				{
					actor.moveToLocation(target.getLoc(), 10, true);
					setNextAction(nextAction.PICKUP, target, null, false, false);
				}
			});
	}

	protected void thinkAttack(boolean arrived)
	{
		if(ConfigValue.DebugOnAction)
			log("DebugOnAction: PLAYABLE_AI:thinkAttack["+arrived+"]->start");
		L2Playable actor = getActor();
		if(actor == null)
			return;

		//if(ConfigValue.DebugOnAction)
		//	log("DebugOnAction: PLAYABLE_AI:thinkAttack["+arrived+"]-> 1");

		L2Player player = actor.getPlayer();
		if(player == null)
		{
			onAttackFail();
			return;
		}

		//if(ConfigValue.DebugOnAction)
		//	log("DebugOnAction: PLAYABLE_AI:thinkAttack["+arrived+"]-> 2");

		if(actor.isActionsDisabled() || actor.isAttackingDisabled())
		{
			actor.sendActionFailed();

			log("L2PlayableAI(427): thinkAttack");
			return;
		}

		//if(ConfigValue.DebugOnAction)
		//	log("DebugOnAction: PLAYABLE_AI:thinkAttack["+arrived+"]-> 3");

		boolean isPosessed = actor instanceof L2Summon && ((L2Summon) actor).isPosessed();

		L2Character attack_target = getAttackTarget();
		if(attack_target == null || attack_target.isDead() || !isPosessed && !(_forceUse ? attack_target.isAttackable(player) : attack_target.isAutoAttackable(player)))
		{
			onAttackFail();
			actor.sendActionFailed();
			log("L2PlayableAI(437): sendActionFailed");

			return;
		}

		/*if(!arrived)
		{
			clientStopMoving();
			actor.doAttack(attack_target, _forceUse);
			log("L2PlayableAI(445): doAttack");
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
		boolean canSee = ConfigValue.AttackInBarrierPlayer && !bow_eq || GeoEngine.canAttacTarget(actor, attack_target, false);

		if(!canSee /*&& (Math.abs(actor.getZ() - attack_target.getZ()) > 200)*/)
		{
			//actor.sendMessage("CANNOT_SEE_TARGET() 2");
			onAttackFail();
			actor.sendActionFailed();
			actor.sendPacket(Msg.CANNOT_SEE_TARGET());
			log("L2PlayableAI(445): doAttack");
			return;
		}

		range += actor.getMinDistance(attack_target);

		if(actor.isFakeDeath())
			actor.breakFakeDeath();

		int offset = (int)Math.ceil(actor.getMinDistance(attack_target));
		if(actor.getPhysicalAttackRange() <= 300)
			offset += (int)(actor.getPhysicalAttackRange()*.67);
		else
			offset += (int)(actor.getPhysicalAttackRange()-100f);
		// на ПТСке еще +10 к offset

		log("L2PlayableAI(525): offset="+offset+" isInRangeZ("+attack_target+", "+range+")["+actor.isInRangeZ(attack_target, range)+"] getDistance3D["+actor.getDistance3D(attack_target)+"]["+attack_target.getDistance(actor)+"] getMinDistance["+actor.getMinDistance(attack_target)+"]");
		if(attack_target.isDoor() ? actor.isInRange(attack_target, arrived ? range+40 : range) : actor.isInRangeZ(attack_target, arrived ? range+40 : range))
		{
			if(!canSee)
			{
				//actor.sendMessage("CANNOT_SEE_TARGET() 3");
				onAttackFail();
				actor.sendActionFailed();
				actor.sendPacket(Msg.CANNOT_SEE_TARGET());
				log("L2PlayableAI(522): doAttack");
				return;
			}

			clientStopMoving(false);
			if(ConfigValue.DebugOnAction)
				log("DebugOnAction: PLAYABLE_AI:thinkAttack->doAttack");
			actor.doAttack(attack_target, _forceUse);
			log("L2PlayableAI(529): doAttack 2");

		}
		else if(!_dontMove)
		{
			if(ConfigValue.AttackTest2 == 0)
			{
				// _followTask = ThreadPoolManager.getInstance().scheduleAI(new ThinkFollow((L2Character) _intention_arg0, (Integer) _intention_arg1), 500);
				if(arrived && ConfigValue.NewGeoEngine)
				{
					//setNextAction(nextAction.MOVE, attack_target.getLoc(), offset, ConfigValue.FollowFindPathType == 0, false);
					//setNextAction(nextAction.ATTACK, attack_target, null, _forceUse, false);
					actor._moveTask = ThreadPoolManager.getInstance().scheduleAI(new L2ObjectTasks.ExecuteFollow(actor, attack_target, offset, ConfigValue.FollowFindPathType == 0 || !actor.isPlayer()), 800);
				}
				else
					actor.followToCharacter(attack_target, offset, true, ConfigValue.FollowFindPathType == 0 || !actor.isPlayer());
			}
			else if(ConfigValue.AttackTest2 == 1)
				ThreadPoolManager.getInstance().execute(new L2ObjectTasks.ExecuteFollow(actor, attack_target, range - 20, ConfigValue.FollowFindPathType == 0 || !actor.isPlayer()));
			else
				ThreadPoolManager.getInstance().execute(new L2ObjectTasks.ExecuteFollow(actor, attack_target, offset, ConfigValue.FollowFindPathType == 0 || !actor.isPlayer()));
		
	
			log("L2PlayableAI(534): doAttack");
			//if(!ConfigValue.FollowFindPathType == 0)
			//	set_dontMove(true);
		}
		else
		{

			if(ConfigValue.DebugOnAction)
				log("DebugOnAction: PLAYABLE_AI:thinkAttack["+arrived+"]-> 8");

			log("L2PlayableAI(539): doAttack");
		}
		actor.sendActionFailed();
		if(ConfigValue.DebugOnAction)
			log("DebugOnAction: PLAYABLE_AI:thinkAttack["+arrived+"]-> finish");
	}

	/**
	 * Нужен для оверрайда саммоном, чтобы он не прекращал фоллов.
	 */
	protected void onAttackFail()
	{
		/*if(ConfigValue.DebugOnAction)
		{
			log("DebugOnAction: PLAYABLE_AI:onAttackFail");
			if(ConfigValue.DebugOnActionTrace)
				Util.test();
		}*/
		setIntention(AI_INTENTION_ACTIVE);
	}

	protected void thinkCast(boolean arrived, boolean nextCast)
	{
		L2Playable actor = getActor();
		if(actor == null)
			return;

		L2Character attack_target = getAttackTarget();

		if(_skill.getSkillType() == SkillType.CRAFT || _skill.isToggle())
		{
			if(_skill.checkCondition(actor, attack_target, _forceUse, _dontMove, true))
			{
				if(_skill.getSkillType() == SkillType.CRAFT)
					clientStopMoving(false);
				actor.doCast(_skill, attack_target, _forceUse, nextCast);
			}
			return;
		}

		if(attack_target == null || ConfigValue.NoBuffDeadChar && attack_target.isDead() != _skill.getCorpse() && !_skill.isNotTargetAoE(getActor()))
		{
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
			return;
		}

		int range = actor.getMagicalAttackRange(_skill);
		if(range < 10)
			range = 10;

		int offset = (int)Math.ceil(actor.getMinDistance(attack_target));
		if(_skill.getCastRange() <= 300)
			offset += (int)(_skill.getCastRange()*.67);
		else
			offset += (int)(_skill.getCastRange()-100f);
		// на ПТСке еще +10 к offset
		/**
		if ( *(_DWORD *)(v3 + 8) <= 0 )
  {
    if ( *(_BYTE *)(v3 + 16) )
    {
      if ( v9 <= 300 )
        v15 = (double)v9 * 0.67;
      else
        v15 = (double)(v9 - 100);
      v12 = (signed int)floor(v15 + v8);
      *(_DWORD *)(v3 + 4) = v12 + 10;
    }
    else
    {
      v12 = 100;
      *(_DWORD *)(v3 + 4) = 110;
    }
  }
  else
  {
    if ( v10 <= 300 )
      v11 = (double)v10 * 0.67;
    else
      v11 = (double)(v10 - 100);
    v12 = (signed int)floor(v11 + v8);
    v13 = (double)(v12 + 10);
    v14 = (double)v10 + v8 - 2.0;
    if ( v14 <= v13 )
      v13 = v14;
    *(_DWORD *)(v3 + 4) = (signed int)floor(v13);
  }
		**/

		boolean canSee = _skill.getSkillType() == SkillType.TAKECASTLE || _skill.getSkillType() == SkillType.TAKEFORTRESS || GeoEngine.canAttacTarget(actor, attack_target, actor.isFlying());
		boolean noRangeSkill = _skill.getCastRange() == -2;

		// на ПТС нету проверки Z, просто дистанция считается с её учетом..
		if(!noRangeSkill && !canSee/* && (Math.abs(actor.getZ() - attack_target.getZ()) > 200)*/)
		{
			//actor.sendMessage("CANNOT_SEE_TARGET() 4");
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
			actor.sendPacket(Msg.CANNOT_SEE_TARGET());
			return;
		}

		range += actor.getMinDistance(attack_target);

		if(actor.isFakeDeath())
			actor.breakFakeDeath();

		//actor.sendMessage("isInRange: ["+(actor.isInRange(attack_target, arrived ? range + 40 : range))+"]["+range+"]["+actor.getDistance(attack_target)+"] arrived="+arrived);
		if((attack_target.isDoor() ? actor.isInRange(attack_target, arrived ? range+40 : range) : actor.isInRangeZ(attack_target, arrived ? range+40 : range)) || noRangeSkill)
		{
			// TODO: мобам тоже
			//if(actor.getObjectId() != attack_target.getObjectId() && ConfigValue.NewGeoEngine)
			//	actor.broadcastPacket(new MoveToPawn(actor, attack_target, offset));

			if(!noRangeSkill && !canSee)
			{
				//actor.sendMessage("CANNOT_SEE_TARGET() 5");
				setIntention(AI_INTENTION_ACTIVE);
				actor.sendActionFailed();
				actor.sendPacket(Msg.CANNOT_SEE_TARGET());
				return;
			}

			// Если скилл имеет следующее действие, назначим это действие после окончания действия скилла
			/*if(_skill.getNextAction() == NextAction.ATTACK && !actor.equals(attack_target))
				setNextAction(nextAction.ATTACK, attack_target, null, _forceUse, false);
			else
				clearNextAction();*/

			if(_skill.checkCondition(actor, attack_target, _forceUse, _dontMove, true))
			{
				// Список скилов будет пополнятся:)
				if(_skill.getId() != 527 && _skill.getId() != 528 && _skill.getId() != 628 && _skill.getId() != 1448)
					clientStopMoving(false);
				actor.doCast(_skill, attack_target, _forceUse, nextCast);
			}
			else
			{
				//Заглушка если скилл не юзнулся.
				if(_skill.getNextAction() == NextAction.ATTACK && !actor.equals(attack_target) && !_forceUse /*&& !actor.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? _skill.getId()*65536L+_skill.getLevel() : _skill.getId())*/)
					setNextAction(nextAction.ATTACK, attack_target, null, _forceUse, false);
				//else
					//clearNextAction();
				setNextIntention();
				if(getIntention() == CtrlIntention.AI_INTENTION_ATTACK)
					thinkAttack(false);
			}
		}
		else if(!_dontMove)
		{
			/*if(!arrived)
			{
				if(ConfigValue.AttackTest2 == 0)
				{
					actor.setFollowTarget(attack_target);
					if(attack_target.isDoor())
						actor.moveToLocation(attack_target.getLoc(), offset, true);
					else
						actor.moveToLocation(attack_target.getX(), attack_target.getY(), attack_target.getZ(), offset, false, true);
						//actor.followToCharacter(attack_target, offset), true, ConfigValue.FollowFindPathType == 0);
				}
				else if(ConfigValue.AttackTest2 == 1)
					ThreadPoolManager.getInstance().execute(new L2ObjectTasks.ExecuteFollow(actor, attack_target, range - 20, ConfigValue.FollowFindPathType == 0 || !actor.isPlayer()));
			}
			else if(ConfigValue.FollowFindPathType == 0 || !actor.isPlayer())*/
				ThreadPoolManager.getInstance().execute(new L2ObjectTasks.ExecuteFollow(actor, attack_target, offset, true));
		}
		else
		{
			actor.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		clearNextAction();
		L2Playable actor = getActor();
		if(actor != null)
			actor.clearHateList(true);
		super.MY_DYING(killer);
	}

	@Override
	protected void onEvtFakeDeath()
	{
		clearNextAction();
        L2Playable actor = getActor();
        if (actor != null)
            actor.abortCast(true);
		super.onEvtFakeDeath();
	}

	public void lockTarget(L2Character target)
	{
		L2Playable actor = getActor();
		if(actor == null)
			return;
		L2Object actorStoredTarget = actor.getTarget();

		if(target == null)
			actor.setAggressionTarget(null);
		else if(actor.getAggressionTarget() == null && (ConfigValue.p_target_me_broken || actorStoredTarget == null || actorStoredTarget != target))
		{
			
			actor.setAggressionTarget(target);
			actor.setTarget(target);

			clearNextAction();

			if(actorStoredTarget != target)
				actor.sendPacket(new MyTargetSelected(target.getObjectId(), 0));
		}
	}

	@Override
	public void Attack(L2Object target, boolean forceUse, boolean dontMove)
	{
		L2Playable actor = getActor();
		if(actor == null)
			return;

		if(target.isCharacter() && (actor.isActionsDisabled() || actor.isAttackingDisabled()))
		{
			// Если не можем атаковать, то атаковать позже
			setNextAction(nextAction.ATTACK, target, null, forceUse, false);
			actor.sendActionFailed();
			log("L2PlayableAI(677): Attack isActionsDisabled="+actor.isActionsDisabled()+" isAttackingDisabled="+actor.isAttackingDisabled());
			return;
		}

		set_dontMove(dontMove);
		_forceUse = forceUse;
		clearNextAction();
		//if(ConfigValue.DebugOnAction)
		//	log("DebugOnAction: PLAYABLE_AI:Attack->set:AI_INTENTION_ATTACK");
		setIntention(AI_INTENTION_ATTACK, target);
		log("L2PlayableAI(685): Attack 2");
	}

	@Override
	public void Cast(L2Skill skill, L2Character target, boolean forceUse, boolean dontMove)
	{
		L2Playable actor = getActor();
		if(actor == null)
			return;

		//_log.info("L2PlayableAI:->Cast: ["+actor.isActionsDisabled()+"]["+actor.getCastingSkill()+"]");
		// Если скилл альтернативного типа (например, бутылка на хп),
		// то он может использоваться во время каста других скиллов, или во время атаки, или на бегу.
		// Поэтому пропускаем дополнительные проверки.
		if(skill.isToggle())
		{
			if(actor.isToggleDisabled())
				clientActionFailed();
			else
				actor.altUseSkill(skill, target);
			return;
		}

		// Если не можем кастовать, то использовать скилл позже
		if(actor.isActionsDisabled() && (actor.getCastingSkill() == null || actor.getCastingSkill().getSkillType() != SkillType.TRANSFORMATION))
		{
			//if(!skill.checkCondition(actor, target, _forceUse, _dontMove, false))
			/*if(actor.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? skill.getId()*65536L+skill.getLevel() : skill.getId()))
			{
				if(skill.getNextAction() == NextAction.ATTACK && !actor.equals(target) && !_forceUse && !_dontMove)
				{
					setNextAction(L2PlayableAI.nextAction.ATTACK, target, null, _forceUse, false);
					clientActionFailed();
					return;
				}
			}*/
			//if(!actor.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? skill.getId()*65536L+skill.getLevel() : skill.getId()))
			if(ConfigValue.PtsSoulShotCast && actor.getCastingSkill() != null)
				setNextAction(nextAction.CAST, skill, target, forceUse, dontMove, true);
			else
				setNextAction(nextAction.CAST, skill, target, forceUse, dontMove);
			clientActionFailed();
			//setNextIntention();
			return;
		}

		//_actor.stopMove(null);
		_forceUse = forceUse;
		set_dontMove(dontMove);
		clearNextAction();
		setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
		//Util.test();
	}

	@Override
	public L2Playable getActor()
	{
		return (L2Playable) super.getActor();
	}

	public void set_dontMove(boolean dontMove)
	{
		_dontMove = dontMove;
		if(ConfigValue.DebugOnActionDontMoveTrace)
		{
			log("DebugOnAction: PLAYABLE_AI:set_dontMove["+dontMove+"]");
			Util.test();
		}
	}

	public boolean dontMove()
	{
		return _dontMove;
	}

	protected void thinkCoupleAction(L2Player target, Integer socialId, boolean cancel)
	{
		//
	}

	public void log(String text)
	{
		L2Playable actor = getActor();
		if(actor != null && actor.i_ai0 == 1994575)
		{
			_log.info(text);
			Say2 cs = new Say2(0, 0, "DEBUG", text);
			for(L2Player player : L2ObjectsStorage.getPlayers())
				if(player.isGM())
					player.sendPacket(cs);
		}
	}
}