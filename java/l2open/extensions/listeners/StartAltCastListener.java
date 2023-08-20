package l2open.extensions.listeners;

import l2open.extensions.listeners.events.MethodEvent;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;

public abstract class StartAltCastListener implements MethodInvokeListener, MethodCollection
{
	@Override
	public final void methodInvoked(MethodEvent e)
	{
		Object[] args = e.getArgs();
		onAltCastStart((L2Skill) args[0], (L2Character) args[1]);
	}

	@Override
	public final boolean accept(MethodEvent event)
	{
		return event.getMethodName().equals(onStartAltCast);
	}

	public abstract void onAltCastStart(L2Skill skill, L2Character target);
}
