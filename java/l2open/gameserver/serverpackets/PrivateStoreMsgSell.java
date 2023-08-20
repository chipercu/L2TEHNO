package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

public class PrivateStoreMsgSell extends L2GameServerPacket
{
	private final int char_obj_id;
	private final String store_name;
	private boolean WholeMsg;

	/**
	 * Название личного магазина продажи
	 * @param player
	 */
	public PrivateStoreMsgSell(L2Player player, boolean _WholeMsg)
	{
		WholeMsg = _WholeMsg;
		char_obj_id = player.getObjectId();
		store_name = player.getTradeList() == null ? "" : (WholeMsg ? player.getTradeList().getSellPkgStoreName() : player.getTradeList().getSellStoreName());
	}

	@Override
	protected final void writeImpl()
	{
		if(WholeMsg)
		{
			writeC(EXTENDED_PACKET);
			writeHG(0x80);
		}
		else
			writeC(0xA2);
		writeD(char_obj_id);
		writeS(store_name);
	}

	@Override
	public String getType()
	{
		return WholeMsg ? "[S] FE:80 PrivateStoreWholeMsgSell" : "[S] A2 PrivateStoreMsgSell";
	}
}