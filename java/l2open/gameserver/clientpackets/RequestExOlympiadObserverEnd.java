package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.util.Strings;

/**
 * format ch
 * c: (id) 0xD0
 * h: (subid) 0x2F
 */
public class RequestExOlympiadObserverEnd extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(!activeChar.inObserverMode())
			return;
		NpcHtmlMessage reply = new NpcHtmlMessage(0);
		StringBuffer msg = new StringBuffer("");
		msg.append("!Grand Olympiad Game View:<br>");

		reply.setHtml(Strings.bbParse(msg.toString()));
		activeChar.sendPacket(reply);
	}
}