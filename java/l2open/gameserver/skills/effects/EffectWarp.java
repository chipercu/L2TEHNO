package l2open.gameserver.skills.effects;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.FlyToLocation;
import l2open.gameserver.skills.Env;
import l2open.util.Location;

public class EffectWarp extends L2Effect
{
	public EffectWarp(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		Location flyLoc = _effector.getFlyLocation(_effector, getSkill());
		if(flyLoc != null)
		{
			_effector.setFlyLoc(flyLoc);
			_effector.broadcastPacket(new FlyToLocation(_effector, flyLoc, FlyToLocation.FlyType.DUMMY));
			_effector.setLoc(flyLoc);
		}
		else
		{
			_effector.sendPacket(Msg.CANNOT_SEE_TARGET());
			return;
		}
	}


	@Override
	public boolean onActionTime()
	{
		return false;
	}
}