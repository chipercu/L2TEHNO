package l2open.gameserver.serverpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;

import java.util.Map;

/**
 * Format: (c) d[dS]
 * d: list size
 * [
 *   d: char ID
 *   S: char Name
 * ]
 *
 * Пример с оффа:
 * C2 02 00 00 00 D0 33 08 00 43 00 4B 00 4A 00 49 00 41 00 44 00 75 00 4B 00 00 00 D0 A7 09 00 53 00 65 00 6B 00 61 00 73 00 00 00
 */
public class PackageToList extends L2GameServerPacket
{
	private Map<Integer, String> characters;

	public PackageToList(L2Player player)
	{
		characters = player.getAccountChars();
		if(characters.size() < 1)
		{
			characters = null;
			player.sendPacket(Msg.THAT_CHARACTER_DOES_NOT_EXIST);
			return;
		}
	}

	@Override
	protected final void writeImpl()
	{
		if(characters == null)
			return;

		writeC(0xc8);
		writeD(characters.size());
		for(Map.Entry<Integer, String> entry : characters.entrySet())
		{
			writeD(entry.getKey());
			writeS(entry.getValue());
		}
	}
}