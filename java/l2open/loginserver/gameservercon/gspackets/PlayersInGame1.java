package l2open.loginserver.gameservercon.gspackets;

import l2open.loginserver.gameservercon.AttGS;

public class PlayersInGame1 extends ClientBasePacket
{
	public PlayersInGame1(byte[] decrypt, AttGS gameserver)
	{
		super(decrypt, gameserver);
	}

	private String[] accs;
	private int player_conut;

	@Override
	public void read()
	{
		getGameServer().clear();

		player_conut = readH();
		accs = new String[readH()];
		for(int i = 0; i < accs.length; i++)
			accs[i] = readS();
		getGameServer().addAccountsInGameServer(accs);
		getGameServer().setPlayerCount(player_conut);

		int count = readD();
		for(int i=0;i<count;i++)
			getGameServer().addPlayerCount(readS(), readD());
	}
}