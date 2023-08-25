package l2open.extensions.listeners.events.L2Object;

import l2open.extensions.listeners.events.DefaultPropertyChangeEvent;
import l2open.gameserver.model.L2Object;

/**
 * @author Death
 * Date: 22/8/2007
 * Time: 15:29:26
 */
public class PropertyChangeEvent extends DefaultPropertyChangeEvent
{
	public PropertyChangeEvent(String event, L2Object actor, Object oldV, Object newV)
	{
		super(event, actor, oldV, newV);
	}

	@Override
	public L2Object getObject()
	{
		return (L2Object) super.getObject();
	}
}
