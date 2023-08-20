package l2open.gameserver.serverpackets;

/**
 * @author Smo
 */
public class ExBR_NewIConCashBtnWnd extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x144);
		writeH(0);
	}
}
