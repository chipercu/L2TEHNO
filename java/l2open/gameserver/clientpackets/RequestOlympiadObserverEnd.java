package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.entity.olympiad.OlympiadGame;

/**
 * format ch
 * c: (id) 0xD0
 * h: (subid) 0x29
 *
 */
public class RequestOlympiadObserverEnd extends L2GameClientPacket
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