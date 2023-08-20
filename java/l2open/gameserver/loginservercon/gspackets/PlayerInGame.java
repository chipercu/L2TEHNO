package l2open.gameserver.loginservercon.gspackets;

import l2open.extensions.network.SelectorStats;
import l2open.gameserver.GameServer;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerInGame extends GameServerBasePacket
{
	/**
	 * Посылает геймсерверу информацию об игроке в игре
	 * @param player - имя аккаунта или null как флаг инициализации списка
	 */
	public PlayerInGame(String player, int size, int protocol)
	{
		if(protocol == 3)
		{
			writeC(0x2F);
			writeS(player);
			writeH(size);

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
			writeC(0x02);
			writeS(player);
			writeH(size);
		}
		/*ConcurrentHashMap<String, Integer> list = GameServer.getSelectorThreads()[0]._stats._online;
		System.out.println("PlayerInGame: ["+list.size()+"]");
		for(Entry<String, Integer> entry : list.entrySet())
			System.out.println("-------: ["+entry.getKey()+"]["+entry.getValue()+"]");*/
		/*writeC(0x2F);
		writeS(player);
		writeH(size);

		ConcurrentHashMap<String, Integer> list = GameServer.getSelectorThreads()[0]._stats._online;

		writeD(list.size());
		for(Entry<String, Integer> entry : list.entrySet())
		{
			writeS(entry.getKey());
			writeD(entry.getValue());
		}*/
	}
}