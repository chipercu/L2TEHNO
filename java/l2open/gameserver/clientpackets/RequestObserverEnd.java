package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.entity.olympiad.OlympiadGame;

public class RequestObserverEnd extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar != null && activeChar.inObserverMode())
			activeChar.leaveObserverMode(Olympiad.getGameBySpectator(activeChar));
	}
}