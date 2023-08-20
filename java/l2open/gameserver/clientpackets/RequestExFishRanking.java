package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;

public class RequestExFishRanking extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendMessage("Not done");

		// TODO
	}
}