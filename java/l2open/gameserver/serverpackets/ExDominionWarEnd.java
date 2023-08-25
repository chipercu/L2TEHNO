package l2open.gameserver.serverpackets;

/**
 * @author VISTALL
 * @date 12:11/05.03.2011
 */
public class ExDominionWarEnd extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExDominionWarEnd();

	@Override
	public void writeImpl()
	{
		writeC(0xFe);
		writeH(getClient().isLindvior() ? 0xA5 : 0xA4);
	}
}
