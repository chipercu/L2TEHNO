package l2open.gameserver.loginservercon.gspackets;

import l2open.common.TaskPriority;
import l2open.util.CRC32;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class GameServerBasePacket
{
	private final ByteArrayOutputStream _bao = new ByteArrayOutputStream();

	protected void writeD(int value)
	{
		_bao.write(value & 0xff);
		_bao.write(value >> 8 & 0xff);
		_bao.write(value >> 16 & 0xff);
		_bao.write(value >> 24 & 0xff);
	}

	protected void writeH(int value)
	{
		_bao.write(value & 0xff);
		_bao.write(value >> 8 & 0xff);
	}

	protected void writeC(int value)
	{
		_bao.write(value & 0xff);
	}

	protected void writeQ(long value)
	{
		_bao.write((int)(value & 0xFF));
		_bao.write((int)(value >> 8 & 0xFF));
		_bao.write((int)(value >> 16 & 0xFF));
		_bao.write((int)(value >> 24 & 0xFF));
		_bao.write((int)(value >> 32 & 0xFF));
		_bao.write((int)(value >> 40 & 0xFF));
		_bao.write((int)(value >> 48 & 0xFF));
		_bao.write((int)(value >> 56 & 0xFF));
	}

	protected void writeF(double org)
	{
		long value = Double.doubleToRawLongBits(org);
		_bao.write((int) (value & 0xff));
		_bao.write((int) (value >> 8 & 0xff));
		_bao.write((int) (value >> 16 & 0xff));
		_bao.write((int) (value >> 24 & 0xff));
		_bao.write((int) (value >> 32 & 0xff));
		_bao.write((int) (value >> 40 & 0xff));
		_bao.write((int) (value >> 48 & 0xff));
		_bao.write((int) (value >> 56 & 0xff));
	}

	protected void writeS(String text)
	{
		try
		{
			if(text != null)
				_bao.write(text.getBytes("UTF-16LE"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		_bao.write(0);
		_bao.write(0);
	}

	protected void writeB(byte[] array)
	{
		try
		{
			_bao.write(array);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public byte[] getBytes()
	{
		int padding = (_bao.size() + 4) % 8;
		if(padding != 0)
			for(int i = padding; i < 8; i++)
				writeC(0x00);
		writeD(CRC32.updateBytes(0, _bao.toByteArray(), 0, _bao.size())); // reserve for checksum
		return _bao.toByteArray();
	}

	public TaskPriority getPriority()
	{
		return TaskPriority.PR_HIGH;
	}

	public String getType()
	{
		return "[GS2LS] " + getClass().getSimpleName();
	}

	public String toString()
	{
		return getType();
	}
}