package l2open.extensions.listeners.events;

public class DefaultPropertyChangeEvent implements PropertyEvent
{
	private final String event;
	private final Object actor;
	private final Object oldV;
	private final Object newV;

	public DefaultPropertyChangeEvent(String event, Object actor, Object oldV, Object newV)
	{
		this.event = event;
		this.actor = actor;
		this.oldV = oldV;
		this.newV = newV;
	}

	public Object getObject()
	{
		return actor;
	}

	public Object getOldValue()
	{
		return oldV;
	}

	public Object getNewValue()
	{
		return newV;
	}

	public String getProperty()
	{
		return event;
	}
}
