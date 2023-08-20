package l2open.loginserver.gameservercon.gspackets;

import l2open.loginserver.gameservercon.AttGS;

public class PlayerInGame extends ClientBasePacket
{
	public PlayerInGame(byte[] decrypt, AttGS gameserver)
	{
		super(decrypt, gameserver);
	}

	@Override
	public void read()
	{
		String acc = readS();
		if(acc.isEmpty())
			getGameServer().clearAccountInGameServer();
		else
			getGameServer().addAccountInGameServer(acc);
		getGameServer().setPlayerCount(readH());
	}
}