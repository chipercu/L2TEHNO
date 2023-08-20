package l2open.gameserver.serverpackets;

/**
 *
 * sample
 * <p>
 * 4c
 * 01 00 00 00
 * <p>
 *
 * format
 * cd
 */
public class JoinParty extends L2GameServerPacket
{
	private int _response;

	public JoinParty(int response)
	{
		_response = response;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x3A);
		writeD(_response);
	}
}