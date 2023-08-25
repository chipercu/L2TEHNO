package l2open.loginserver.gameservercon.lspackets;

public class RSAKey extends ServerBasePacket
{
	public RSAKey(byte[] data)
	{
		writeC(0);
		writeB(data);
	}
}
