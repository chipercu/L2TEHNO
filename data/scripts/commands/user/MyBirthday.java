package commands.user;

import java.util.Calendar;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IUserCommandHandler;
import l2open.gameserver.handler.UserCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SystemMessage;

/**
 * Support for /mybirthday command
 */
public class MyBirthday implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 126 };

	public boolean useUserCommand(int id, L2Player activeChar)
	{
		if(COMMAND_IDS[0] != id)
			return false;

		if(activeChar.getCreateTime() == 0)
			return false;

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(activeChar.getCreateTime());

		activeChar.sendPacket(new SystemMessage(SystemMessage.C1S_CHARACTER_BIRTHDAY_IS_S3S4S2).addString(activeChar.getName()).addNumber(c.get(Calendar.YEAR)).addNumber(c.get(Calendar.MONTH) + 1).addNumber(c.get(Calendar.DAY_OF_MONTH)));
		return true;
	}

	public final int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}

	public void onLoad()
	{
		UserCommandHandler.getInstance().registerUserCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}