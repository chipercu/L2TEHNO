package l2open.gameserver.loginservercon.lspackets;

import l2open.common.TaskPriority;
import l2open.gameserver.loginservercon.AttLS;
import l2open.gameserver.loginservercon.gspackets.GameServerBasePacket;

import java.util.Collection;

public abstract class LoginServerBasePacket extends l2open.common.RunnableImpl
{
	private byte[] _decrypt;
	private int _off;
	private final AttLS loginServer;

	public LoginServerBasePacket(byte[] decrypt, AttLS loginServer)
	{
		_decrypt = decrypt;
		_off = 1; // skip packet type id
		this.loginServer = loginServer;
	}

	public int readD()
	{
		int result = _decrypt[_off++] & 0xff;
		result |= _decrypt[_off++] << 8 & 0xff00;
		result |= _decrypt[_off++] << 0x10 & 0xff0000;
		result |= _decrypt[_off++] << 0x18 & 0xff000000;
		return result;
	}

	public int readC()
	{
		return _decrypt[_off++] & 0xff;
	}

	public int readH()
	{
		int result = _decrypt[_off++] & 0xff;
		result |= _decrypt[_off++] << 8 & 0xff00;
		return result;
	}

	public double readF()
	{
		long result = _decrypt[_off++] & 0xff;
		result |= _decrypt[_off++] << 8 & 0xff00;
		result |= _decrypt[_off++] << 0x10 & 0xff0000;
		result |= _decrypt[_off++] << 0x18 & 0xff000000;
		result |= _decrypt[_off++] << 0x20 & 0xff00000000l;
		result |= _decrypt[_off++] << 0x28 & 0xff0000000000l;
		result |= _decrypt[_off++] << 0x30 & 0xff000000000000l;
		result |= _decrypt[_off++] << 0x38 & 0xff00000000000000l;
		return Double.longBitsToDouble(result);
	}

	public String readS()
	{
		String result = null;
		try
		{
			result = new String(_decrypt, _off, _decrypt.length - _off, "UTF-16LE");
			result = result.substring(0, result.indexOf(0x00));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(result != null)
			_off += result.length() * 2 + 2;
		return result;
	}

	public final byte[] readB(int length)
	{
		byte[] result = new byte[length];
		System.arraycopy(_decrypt, _off, result, 0, length);
		_off += length;
		return result;
	}

	public TaskPriority getPriority()
	{
		return TaskPriority.PR_HIGH;
	}

	public void runImpl()
	{
		try
		{
			read();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public abstract void read();

	public AttLS getLoginServer()
	{
		return loginServer;
	}

	public void sendPacket(GameServerBasePacket packet)
	{
		loginServer.sendPacket(packet);
	}

	public void sendPackets(Collection<GameServerBasePacket> packets)
	{
		loginServer.sendPackets(packets);
	}
}