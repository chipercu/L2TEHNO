package l2open.extensions.network;

import java.nio.ByteBuffer;

@SuppressWarnings("unchecked")
public abstract class ReceivablePacket<T extends MMOClient> extends AbstractPacket<T> implements Runnable
{
	protected T _client;
	protected ByteBuffer _buf;

	protected void setByteBuffer(ByteBuffer buf)
	{
		_buf = buf;
	}

	@Override
	protected ByteBuffer getByteBuffer()
	{
		return _buf;
	}

	protected void setClient(T client)
	{
		_client = client;
	}

	@Override
	public T getClient()
	{
		return _client;
	}
	//-----------
	protected int getAvaliableBytes()
	{
		return getByteBuffer().remaining();
	}

	protected void readB(byte[] dst)
	{
		getByteBuffer().get(dst);
	}

	protected void readB(byte[] dst, int offset, int len)
	{
		getByteBuffer().get(dst, offset, len);
	}

	protected int readC()
	{
		return getByteBuffer().get() & 0xFF;
	}

	protected int readH()
	{
		return getByteBuffer().getShort() & 0xFFFF;
	}

	protected int readD()
	{
		return getByteBuffer().getInt();
	}

	protected long readQ()
	{
		return getByteBuffer().getLong();
	}

	protected double readF()
	{
		return getByteBuffer().getDouble();
	}

	protected String readS()
	{
		StringBuilder sb = new StringBuilder();
		char ch;
		while((ch = getByteBuffer().getChar()) != 0)
			sb.append(ch);
		return sb.toString();
	}

	protected String readS(int Maxlen)
	{
		String ret = readS();
		return ret.length() > Maxlen ? ret.substring(0, Maxlen) : ret;
	}

	protected abstract boolean read();

	public boolean isFilter()
	{
		return true;
	}
}