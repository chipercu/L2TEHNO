package l2open.gameserver.serverpackets;

public class ExGoodsInventoryInfoPacket extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xE3);
		// just a trigger
	}
	/*
	writeEx(0x112);
		if (!_premiumItemMap.isEmpty())
		{
			writeH(_premiumItemMap.size());
			for (Map.Entry entry : _premiumItemMap.entrySet())
			{
				writeQ((Integer) entry.getKey());
				writeC(0);
				writeD(10003);
				writeS(((PremiumItem) entry.getValue()).getSender());
				writeS(((PremiumItem) entry.getValue()).getSender());// ((PremiumItem)entry.getValue()).getSenderMessage());
				writeQ(0);
				writeC(2);
				writeC(0);
				
				writeS(null);
				writeS(null);
				
				writeH(1);
				writeD(((PremiumItem) entry.getValue()).getItemId());
				writeD((int) ((PremiumItem) entry.getValue()).getCount());
			}
		}
		else
		{
			writeH(0);
		}
		*/
}
