package l2open.gameserver.listener.actor.player.impl;

import l2open.gameserver.listener.actor.player.OnAnswerListener;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2PetInstance;

/**
 * @author VISTALL
 * @date 11:35/15.04.2011
 */
public class ReviveAnswerListener implements OnAnswerListener
{

	private final double _power;
	private final boolean _forPet;
	private final long _timeStamp;

	public ReviveAnswerListener(double power, boolean forPet, int expiration)
	{
		_forPet = forPet;
		_power = power;
		_timeStamp = expiration > 0 ? System.currentTimeMillis() + expiration : Long.MAX_VALUE;
	}

	@Override
	public void sayYes(L2Player player)
	{
		if(System.currentTimeMillis() > _timeStamp)
			return;

		if(!player.isDead() && !_forPet || _forPet && player.getPet() != null && !player.getPet().isDead())
			return;

		if(!_forPet)
			player.doRevive(_power);
		else if(player.getPet() != null)
			((L2PetInstance) player.getPet()).doRevive(_power);
	}

	@Override
	public void sayNo(L2Player player)
	{

	}

	public double getPower()
	{
		return _power;
	}

	public boolean isForPet()
	{
		return _forPet;
	}
}
