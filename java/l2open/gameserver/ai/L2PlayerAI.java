package l2open.gameserver.ai;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2TamedBeastInstance;
import l2open.gameserver.serverpackets.ExRotation;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.serverpackets.SystemMessage;

public class L2PlayerAI extends L2PlayableAI 
{
    public L2PlayerAI(L2Player actor) 
	{
        super(actor);
    }

    @Override
    protected void onIntentionRest() 
	{
        changeIntention(CtrlIntention.AI_INTENTION_REST, null, null);
        setAttackTarget(null);
        clientStopMoving();
    }

    @Override
    protected void onIntentionActive() 
	{
        clearNextAction();
        changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
    }

    @Override
    public void onIntentionInteract(L2Object object, int type) 
	{
        L2Player actor = getActor();
        if (actor == null)
            return;

        if (actor.getSittingTask()) 
		{
            setNextAction(nextAction.INTERACT, object, type, false, false);
            return;
        } 
		else if (actor.isSitting()) 
		{
            actor.sendPacket(Msg.YOU_CANNOT_MOVE_WHILE_SITTING);
            clientActionFailed();
            return;
        }
        super.onIntentionInteract(object, type);
    }

    @Override
    public void onIntentionPickUp(L2Object object) 
	{
        L2Player actor = getActor();
        if (actor == null)
            return;

        if (actor.getSittingTask()) 
		{
            setNextAction(nextAction.PICKUP, object, null, false, false);
            return;
        } 
		else if (actor.isSitting()) 
		{
            actor.sendPacket(Msg.YOU_CANNOT_MOVE_WHILE_SITTING);
            clientActionFailed();
            return;
        }
        super.onIntentionPickUp(object);
    }

    @Override
    protected void thinkAttack(boolean checkRange) 
	{
        L2Player actor = getActor();
        if (actor == null)
            return;

        if (actor.isInFlyingTransform()) 
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			return;
        }
		
		if(actor.isCombatFlagEquipped())
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
			return;
		}
        super.thinkAttack(checkRange);
    }

    @Override
    public void Attack(L2Object target, boolean forceUse, boolean dontMove) 
	{
        L2Player actor = getActor();
        if (actor == null)
            return;

        if (actor.isInFlyingTransform()) 
		{
            actor.sendActionFailed();
            return;
        }

        if (System.currentTimeMillis() - actor.getLastAttackPacket() < ConfigValue.AttackPacketDelay) 
		{
            actor.sendActionFailed();
            return;
        }
        actor.setLastAttackPacket();

        if (actor.getSittingTask()) 
		{
            setNextAction(nextAction.ATTACK, target, null, forceUse, false);
            return;
        } else if (actor.isSitting()) 
		{
            actor.sendPacket(Msg.YOU_CANNOT_MOVE_WHILE_SITTING);
            clientActionFailed();
            return;
        }
        super.Attack(target, forceUse, dontMove);
    }

    @Override
    public void Cast(L2Skill skill, L2Character target, boolean forceUse, boolean dontMove) 
	{
        L2Player actor = getActor();
        if (actor == null)
            return;

        if (!(skill.getSkillType() == L2Skill.SkillType.CRAFT && ConfigValue.AllowTalkWhileSitting))
            // Если в этот момент встаем, то использовать скилл когда встанем
            if (actor.getSittingTask()) 
			{
                setNextAction(nextAction.CAST, skill, target, forceUse, dontMove);
                clientActionFailed();
                return;
            }
            // если сидим - скиллы нельзя использовать
            else if (actor.isSitting()) 
			{
                if (skill.isTransformation())
                    actor.sendPacket(Msg.YOU_CANNOT_TRANSFORM_WHILE_SITTING);
                else
                    actor.sendPacket(Msg.YOU_CANNOT_MOVE_WHILE_SITTING);

                clientActionFailed();
                return;
            }
        super.Cast(skill, target, forceUse, dontMove);
    }

    @Override
    protected void ATTACKED(L2Character attacker, int damage, L2Skill skill) 
	{
        L2Player actor = getActor();
        if (actor == null)
            return;
        // notify the tamed beast of attacks
        if(actor.getTrainedBeast() != null)
            for(L2TamedBeastInstance tamedBeast : actor.getTrainedBeast())
				if(tamedBeast != null)
					tamedBeast.onOwnerGotAttacked(attacker);

        super.ATTACKED(attacker, damage, skill);
    }

    @Override
    public L2Player getActor() 
	{
        return (L2Player) super.getActor();
    }

    @Override
    protected void onAttackFail() 
	{ 
        for (L2Cubic cubic : getActor().getCubics())
            cubic.stopAttack();  
    }

	@Override
	protected void thinkCoupleAction(L2Player target, Integer socialId, boolean cancel)
	{
		L2Player actor = getActor();
		if(target == null || !target.isOnline())
		{
			actor.sendPacket(new SystemMessage(3121));
			return;
		}

		if(cancel || !actor.isInRange(target, 300) || actor.isInRange(target, 5) || actor.getReflection() != target.getReflection() || !GeoEngine.canSeeTarget(actor, target, false))
		{
			target.sendPacket(new SystemMessage(3121));
			actor.sendPacket(new SystemMessage(3121));
			return;
		}
		if(_forceUse) // служит только для флага что б активировать у другого игрока социалку
			target.getAI().setIntention(CtrlIntention.AI_INTENTION_COUPLE_ACTION, actor, socialId);
		actor.sendPacket((new SystemMessage(3151)).addName(target));
		//
		int heading = actor.calcHeading(target.getX(), target.getY());
		actor.setHeading(heading);
		actor.broadcastPacket(new ExRotation(actor.getObjectId(), heading));
		//
		actor.broadcastPacket(new SocialAction(actor.getObjectId(), socialId));
	}
}