package l2open.gameserver.loginservercon.gspackets;

import l2open.extensions.network.SelectorStats;
import l2open.gameserver.GameServer;
import l2open.util.GArray;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class PlayersInGame extends GameServerBasePacket
{
	private PlayersInGame(int online, int protocol, String... accs)
	{
		if(protocol == 3)
		{
			writeC(0x1F);
			writeH(online);
			writeH(accs.length);
			for(String acc : accs)
				writeS(acc);

			try
			{
				ConcurrentHashMap<String, Integer> list = GameServer.getSelectorThreads()[0]._stats._online;
				writeD(list.size());
				for(Entry<String, Integer> entry : list.entrySet())
				{
					writeS(entry.getKey());
					writeD(entry.getValue());
				}
			}
			catch(Exception e)
			{
				writeD(0);
			}
		}
		else
		{
			writeC(0x0e);
			writeH(online);
			writeH(accs.length);
			for(String acc : accs)
				writeS(acc);
		}
		/*ConcurrentHashMap<String, Integer> list = GameServer.getSelectorThreads()[0]._stats._online;
		System.out.println("PlayersInGame: ["+list.size()+"]");
		for(Entry<String, Integer> entry : list.entrySet())
			System.out.println("-------: ["+entry.getKey()+"]["+entry.getValue()+"]");*/

	}

	private PlayersInGame(int online, int protocol, Collection<String> accs)
	{
		if(protocol == 3)
		{
			writeC(0x1F);
			writeH(online);
			writeH(accs.size());
			for(String acc : accs)
				writeS(acc);

			try
			{
				ConcurrentHashMap<String, Integer> list = GameServer.getSelectorThreads()[0]._stats._online;
				writeD(list.size());
				for(Entry<String, Integer> entry : list.entrySet())
				{
					writeS(entry.getKey());
					writeD(entry.getValue());
				}
			}
			catch(Exception e)
			{
				writeD(0);
			}
		}
		else
		{
			writeC(0x0e);
			writeH(online);
			writeH(accs.size());
			for(String acc : accs)
				writeS(acc);
		}
		/*ConcurrentHashMap<String, Integer> list = GameServer.getSelectorThreads()[0]._stats._online;
		System.out.println("PlayersInGame: ["+list.size()+"]");
		for(Entry<String, Integer> entry : list.entrySet())
			System.out.println("-------: ["+entry.getKey()+"]["+entry.getValue()+"]");*/

	}
	/*private PlayersInGame(int online, String... accs)
	{
		writeC(0x1F);
		writeH(online);
		writeH(accs.length);
		for(String acc : accs)
			writeS(acc);

		ConcurrentHashMap<String, Integer> list = GameServer.getSelectorThreads()[0]._stats._online;

		writeD(list.size());
		for(Entry<String, Integer> entry : list.entrySet())
		{
			writeS(entry.getKey());
			writeD(entry.getValue());
		}
	}

	private PlayersInGame(int online, Collection<String> accs)
	{
		writeC(0x1F);
		writeH(online);
		writeH(accs.size());
		for(String acc : accs)
			writeS(acc);

		ConcurrentHashMap<String, Integer> list = GameServer.getSelectorThreads()[0]._stats._online;

		writeD(list.size());
		for(Entry<String, Integer> entry : list.entrySet())
		{
			writeS(entry.getKey());
			writeD(entry.getValue());
		}
	}*/

	private static final int MaxAccountsDataSize = 65535 - 2 - 1 - 2 - 2; // 65535 - 2 байта длины - 1 байт ID - 2 байта онлайн - 2 байта длина масива 
	private static final int AvgAccountNameLength = 16;
	private static final int AvgAccountNameBytes = (AvgAccountNameLength + 1) * 2;
	private static final int AvgAccountsPerPacket = MaxAccountsDataSize / AvgAccountNameBytes;

	public static Collection<GameServerBasePacket> makePlayersInGame(int online, int protocol, Collection<String> accs)
	{
		GArray<GameServerBasePacket> retList = new GArray<GameServerBasePacket>(accs.size() / AvgAccountsPerPacket);
		GArray<String> nextList = new GArray<String>(AvgAccountsPerPacket);
		int accBytes, nextBytes = 0;

		for(String acc : accs)
		{
			accBytes = (acc.length() + 1) * 2;
			if(accBytes + nextBytes >= MaxAccountsDataSize)
			{
				retList.add(new PlayersInGame(online, protocol, nextList));
				nextList.clear();
				nextBytes = 0;
			}
			nextList.add(acc);
			nextBytes += accBytes;
		}
		if(nextList.size() > 0)
			retList.add(new PlayersInGame(online, protocol, nextList));

		return retList;
	}
}