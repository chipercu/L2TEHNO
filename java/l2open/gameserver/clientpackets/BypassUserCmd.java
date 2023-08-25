package l2open.gameserver.clientpackets;

import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.handler.IUserCommandHandler;
import l2open.gameserver.handler.UserCommandHandler;
import l2open.gameserver.model.L2Player;

/**
 * format:  cd
 * Пример пакета по команде /loc:
 * AA 00 00 00 00
 */
public class BypassUserCmd extends L2GameClientPacket
{
	private int _command;

	@Override
	public void readImpl()
	{
		_command = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;

		IUserCommandHandler handler = UserCommandHandler.getInstance().getUserCommandHandler(_command);

		if(handler == null)
			activeChar.sendMessage(new CustomMessage("common.S1NotImplemented", activeChar).addString(String.valueOf(_command)));
		else
			handler.useUserCommand(_command, activeChar);
	}
}