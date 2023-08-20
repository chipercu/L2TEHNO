package l2open.extensions.listeners.events.AbstractAI;

import l2open.extensions.listeners.events.DefaultMethodInvokeEvent;
import l2open.gameserver.ai.AbstractAI;

/**
 * @Author: Diamond
 * @Date: 08/11/2007
 * @Time: 7:17:24
 */
public class AbstractAINotifyEvent extends DefaultMethodInvokeEvent
{
	public AbstractAINotifyEvent(String methodName, AbstractAI owner, Object[] args)
	{
		super(methodName, owner, args);
	}

	@Override
	public AbstractAI getOwner()
	{
		return (AbstractAI) super.getOwner();
	}

	@Override
	public Object[] getArgs()
	{
		return super.getArgs();
	}
}