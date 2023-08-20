package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

import java.util.Collection;

public class BlockList extends L2GameServerPacket
{
	Collection<String> blockList;

	public BlockList(L2Player player)
	{
		blockList = player.getBlockList();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xD5);
		writeD(blockList.size());
		for(String name : blockList)
		{
			writeS(name);
			writeS(""); // комментарий
		}
	}
}