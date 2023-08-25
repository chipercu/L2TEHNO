package l2open.util;

import l2open.extensions.listeners.MethodInvokeListener;
import l2open.extensions.listeners.events.MethodEvent;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2Object;

public class SchedulableEvent implements MethodInvokeListener
{
	public String _class;
	public String _method;
	public Integer _delay;
	public String[] _args;

	public SchedulableEvent(String clas, String method, String[] args, Integer delay)
	{
		_class = clas;
		_method = method;
		_delay = delay;
		_args = args;
	}

	@Override
	public boolean accept(MethodEvent event)
	{
		return true;
	}

	@Override
	public void methodInvoked(MethodEvent e)
	{
		Functions.executeTask((L2Object) e.getOwner(), _class, _method, new Object[] { _args }, _delay);
		//((L2Object) e.getOwner()).removeMethodInvokeListener(e.getMethodName(), this);
	}
}