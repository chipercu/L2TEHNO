package l2open.extensions.listeners.events;

/**
 * @author Death
 */
public class DefaultMethodInvokeEvent implements MethodEvent
{
	private final Object owner;
	private final Object[] args;
	private final String methodName;

	public DefaultMethodInvokeEvent(String methodName, Object owner, Object[] args)
	{
		this.methodName = methodName;
		this.owner = owner;
		this.args = args;
	}

	@Override
	public Object getOwner()
	{
		return owner;
	}

	@Override
	public Object[] getArgs()
	{
		return args;
	}

	@Override
	public String getMethodName()
	{
		return methodName;
	}
}