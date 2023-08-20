package l2open.gameserver.serverpackets;

import l2open.gameserver.GameTimeController;

public class ClientSetTime extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0xf2);
		writeD(GameTimeController.getInstance().getGameTime()); // time in client minutes
		writeD(6); //constant to match the server time( this determines the speed of the client clock)
	}
}