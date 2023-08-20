package l2open.extensions.listeners;

import l2open.extensions.listeners.events.MethodEvent;
import l2open.gameserver.model.L2Character;

/**
 * НЕ ИСПОЛЬЗУЕТСЯ!
 **/
public abstract class OnAttackedListener implements MethodInvokeListener, MethodCollection
{
	@Override
	public final void methodInvoked(MethodEvent e)
	{
		OnAttacked((L2Character) e.getArgs()[0], (L2Character) e.getArgs()[1], (Integer) e.getArgs()[2], (Boolean) e.getArgs()[3], (Boolean) e.getArgs()[4], (Boolean) e.getArgs()[5], (Boolean) e.getArgs()[6], (Boolean) e.getArgs()[7]);
	}

	@Override
	public final boolean accept(MethodEvent event)
	{
		return event.getMethodName().equals(OnAttacked);
	}

	public abstract void OnAttacked(L2Character actor, L2Character target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld, boolean unchargeSS);
}