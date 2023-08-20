package l2open.loginserver.gameservercon.gspackets;

import l2open.loginserver.gameservercon.AttGS;

public class PlayerInGame1 extends ClientBasePacket
{
	public PlayerInGame1(byte[] decrypt, AttGS gameserver)
	{
		super(decrypt, gameserver);
	}

	@Override
	public void read()
	{
		getGameServer().clear();

		String acc = readS();
		if(acc.isEmpty())
			getGameServer().clearAccountInGameServer();
		else
			getGameServer().addAccountInGameServer(acc);
		getGameServer().setPlayerCount(readH());

		//System.out.println("_decrypt="+_decrypt.length+" _off="+_off);
		int count = readD();
		for(int i=0;i<count;i++)
			getGameServer().addPlayerCount(readS(), readD());
	}
}