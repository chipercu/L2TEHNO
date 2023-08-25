package l2open.gameserver.serverpackets;

public class ExBR_BuyProductResult extends L2GameServerPacket
{
    private int code;
    public ExBR_BuyProductResult(int code) {
        this.code = code;
    }

    @Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0xCC : 0xD8);
		writeD(code);
	}
}