package l2open.gameserver.serverpackets;

public class ExBR_MinigameLoadScoresPacket extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0xDE : 0xDD);
		writeD(0);
		writeD(0);
		writeD(0);

		int size = 0; //100?
		writeD(size);
		for(int i = 0; i < size; i++)
		{
			writeD(0); //позиция?
			writeS(""); //имя??
			writeD(0); //счет??
		}
		//if(size < 100) writeD(-1);
	}
}