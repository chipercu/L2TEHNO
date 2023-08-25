package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.BookMark;

/**
 * dd d*[ddddSdS]
 */
public class ExGetBookMarkInfo extends L2GameServerPacket
{
	private final int bookmarksCapacity;
	private final BookMark[] bookmarks;

	public ExGetBookMarkInfo(L2Player player)
	{
		bookmarksCapacity = player.bookmarks.getCapacity();
		bookmarks = player.bookmarks.toArray();
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x85 : 0x84);

		writeD(0x00); // должно быть 0
		writeD(bookmarksCapacity);
		writeD(bookmarks.length);
		int slotId = 0;
		for(BookMark bookmark : bookmarks)
		{
			writeD(++slotId);
			writeD(bookmark.x);
			writeD(bookmark.y);
			writeD(bookmark.z);
			writeS(bookmark.getName());
			writeD(bookmark.getIcon());
			writeS(bookmark.getAcronym());
		}
	}
}