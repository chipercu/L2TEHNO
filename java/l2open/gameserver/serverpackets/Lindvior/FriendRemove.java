package l2open.gameserver.serverpackets;

/**
 * @author Bonux
**/
public class FriendRemove extends L2GameServerPacket
{
	private final String _friendName;

	public FriendRemove(String name)
	{
		_friendName = name;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x57);
		writeD(1); //UNK
		writeS(_friendName); //FriendName
	}
}
