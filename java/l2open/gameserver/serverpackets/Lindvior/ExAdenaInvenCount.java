package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

/**
 * @author Smo
 */
public class ExAdenaInvenCount extends L2GameServerPacket
{
	public int invsize;
	public long count;
	
	public ExAdenaInvenCount(L2Player pl)
	{
		count = pl.getInventory().getAdena();
		invsize = pl.getInventory().getSize();
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x148);
		writeQ(count);
		writeD(invsize);
	}
}
