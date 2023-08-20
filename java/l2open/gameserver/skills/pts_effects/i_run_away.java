package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.util.Location;
import l2open.util.Rnd;

public final class i_run_away extends L2Effect
{
	public static final int FEAR_RANGE = 3000;
	final double angle;

	public i_run_away(Env env, EffectTemplate template)
	{
		super(env, template);
		angle = Math.toRadians(Location.calculateAngleFrom(_effector, _effected));
	}

	@Override
	public boolean checkCondition()
	{
		if(_effected.isFearImmune())
		{
			getEffector().sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return false;
		}
		// Fear нельзя наложить на осадных саммонов
		else if(_effected instanceof L2Summon && ((L2Summon) _effected).isSiegeWeapon())
		{
			getEffector().sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return false;
		}
		else if(_effected.isInZonePeace())
		{
			getEffector().sendPacket(Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
			return false;
		}
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.getAI().clearTasks();
		_effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		_effected.startFear();
		onActionTime();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopFear();
		if(!_effected.isPlayable())
		{
			_effected.setRunning();
			_effected.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _effector, 999);
			_effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, _effector);
		}
		else
			_effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}

	@Override
	public boolean onActionTime()
	{
		if(_effected.isDead() && !_effected.isBlessedByNoblesse() && !_effected._blessed)
			return false;
		else if(!_effected.isDead())
		{
			int oldX = _effected.getX();
			int oldY = _effected.getY();
			int x = oldX + (int)(FEAR_RANGE * Math.cos(angle));
			int y = oldY + (int)(FEAR_RANGE * Math.sin(angle));
			_effected.moveToLocation(GeoEngine.moveCheck(oldX, oldY, _effected.getZ(), x, y, _effected.getReflection().getGeoIndex()), 0, false);
		}
		return true;
	}
}