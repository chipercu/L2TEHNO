package l2open.gameserver.skills.conditions;

import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;

import java.util.logging.Logger;

public abstract class Condition implements ConditionListener
{
	static final Logger _log = Logger.getLogger(Condition.class.getName());

	private ConditionListener _listener;

	private SystemMessage _message;

	private boolean _result;

	public final void setSystemMsg(int msgId)
	{
		_message = new SystemMessage(msgId);
	}

	public final SystemMessage getSystemMsg()
	{
		return _message;
	}

	public void setListener(ConditionListener listener)
	{
		_listener = listener;
		notifyChanged();
	}

	public final ConditionListener getListener()
	{
		return _listener;
	}

	public final boolean test(Env env)
	{
		boolean res = testImpl(env);
		if(_listener != null && res != _result)
		{
			_result = res;
			notifyChanged();
		}
		return res;
	}

	protected abstract boolean testImpl(Env env);

	public void notifyChanged()
	{
		if(_listener != null)
			_listener.notifyChanged();
	}
}
