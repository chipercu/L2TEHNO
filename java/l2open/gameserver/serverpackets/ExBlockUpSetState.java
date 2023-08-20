package l2open.gameserver.serverpackets;

public class ExBlockUpSetState extends L2GameServerPacket
{
	private int BlockUpStateType = 0; //TODO

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x98);
		writeD(BlockUpStateType);
		switch(BlockUpStateType)
		{
			case 0:
				//dddddd
				break;
			case 1:
				//dd
				break;
			case 2:
				//ddd
				break;
		}
	}
}