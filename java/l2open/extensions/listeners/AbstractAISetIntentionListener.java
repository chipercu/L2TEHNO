package l2open.extensions.listeners;

import l2open.extensions.listeners.events.AbstractAI.AbstractAISetIntention;
import l2open.extensions.listeners.events.MethodEvent;
import l2open.gameserver.ai.AbstractAI;
import l2open.gameserver.ai.CtrlIntention;

/**
 * @Author: Diamond
 * @Date: 08/11/2007
 * @Time: 7:17:24
 */
/**
 * НЕ ИСПОЛЬЗУЕТСЯ!
 **/
public abstract class AbstractAISetIntentionListener implements MethodInvokeListener, MethodCollection
{
	@Override
	public final void methodInvoked(MethodEvent e)
	{
		AbstractAISetIntention event = (AbstractAISetIntention) e;
		AbstractAI ai = event.getOwner();
		CtrlIntention evt = (CtrlIntention) event.getArgs()[0];
		Object arg0 = event.getArgs()[1];
		Object arg1 = event.getArgs()[2];
		SetIntention(ai, evt, arg0, arg1);
	}

	@Override
	public final boolean accept(MethodEvent event)
	{
		String method = event.getMethodName();
		return event instanceof AbstractAISetIntention && method.equals(AbstractAIsetIntention);
	}

	public abstract void SetIntention(AbstractAI ai, CtrlIntention intention, Object arg0, Object arg1);
}
