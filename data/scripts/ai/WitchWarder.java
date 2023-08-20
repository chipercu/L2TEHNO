package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;

/**
 * AI штрафных мобов Witch Warder на Isle of Prayer, спавнятся из АИ IsleOfPrayerMystic/Fighter.<br>
 * - Деспавнятся при простое более 3(?) минуты<br>
 * - Не используют функцию Random Walk<br>
 * ID: 18364, 18365, 18366
 * @author SYS
 */
public class WitchWarder extends Fighter
{
	private long _wait_timeout = 0;
	private boolean _wait = false;
	private static final int DESPAWN_TIME = 3 * 60 * 1000; // 3 min

	public WitchWarder(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return true;

		if(_def_think)
		{
			doTask();
			_wait = false;
			return true;
		}

		if(!_wait)
		{
			_wait = true;
			_wait_timeout = System.currentTimeMillis() + DESPAWN_TIME;
		}

		if(_wait_timeout != 0 && _wait && _wait_timeout < System.currentTimeMillis())
			actor.deleteMe();

		return super.thinkActive();
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}