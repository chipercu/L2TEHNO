package l2open.gameserver.loginservercon.gspackets;

public class TestConnectionResponse extends GameServerBasePacket
{
	public TestConnectionResponse()
	{
		//System.out.println("GS: response sent");
		writeC(0x0d);
	}
}