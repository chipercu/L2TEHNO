package l2open.extensions.network;

import java.nio.ByteBuffer;

public abstract class AbstractPacket<T>
{
	protected abstract ByteBuffer getByteBuffer();

	public abstract T getClient();

	protected SelectorThread getCurrentSelectorThread()
	{
		Thread result = Thread.currentThread();
		return result instanceof SelectorThread ? (SelectorThread) result : null;
	}
}