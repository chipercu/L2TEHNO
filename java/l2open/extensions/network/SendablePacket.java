package l2open.extensions.network;

import java.nio.ByteBuffer;

public abstract class SendablePacket<T extends MMOClient> extends AbstractPacket<T>
{
	/*@Override
	protected ByteBuffer getByteBuffer()
	{
		return ((SelectorThread) Thread.currentThread()).getWriteBuffer();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getClient()
	{
		return (T) ((SelectorThread) Thread.currentThread()).getWriteClient();
	}*/
	@Override
	protected ByteBuffer getByteBuffer()
	{
		return getCurrentSelectorThread().getWriteBuffer();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getClient()
	{
		SelectorThread<T> selector = getCurrentSelectorThread();
		return selector == null ? null : selector.getWriteClient();
	}
	//----
	protected void writeC(int data)
	{
		getByteBuffer().put((byte) data);
	}

	protected void writeF(double value)
	{
		getByteBuffer().putDouble(value);
	}

	protected void writeH(int value)
	{
		getByteBuffer().putShort((short) value);
	}

	protected void writeD(int value)
	{
		getByteBuffer().putInt(value);
	}

	protected void writeQ(long value)
	{
		getByteBuffer().putLong(value);
	}

	protected void writeB(byte[] data)
	{
		getByteBuffer().put(data);
	}

	protected void writeS(CharSequence charSequence)
	{
		if(charSequence != null)
		{
			int length = charSequence.length();
			for(int i = 0; i < length; i++)
				getByteBuffer().putChar(charSequence.charAt(i));
		}
		getByteBuffer().putChar('\000');
	}

	/**
	 * Отсылает число позиций + массив
	 */
	protected void writeDD(int[] values, boolean sendCount)
	{
		ByteBuffer buf = getByteBuffer();
		if(sendCount)
			buf.putInt(values.length);
		for(int value : values)
			buf.putInt(value);
	}

	protected void writeDD(int[] values)
	{
		writeDD(values, false);
	}

	protected abstract void write();

	protected abstract int getHeaderSize();

	protected abstract void writeHeader(int dataSize);
}