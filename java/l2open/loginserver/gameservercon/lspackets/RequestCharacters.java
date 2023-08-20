package l2open.loginserver.gameservercon.lspackets;

public class RequestCharacters extends ServerBasePacket
{
	public RequestCharacters(String account)
	{
		writeC(0x0A);
		writeS(account);
	}
}