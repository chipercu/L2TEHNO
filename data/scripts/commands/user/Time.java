package commands.user;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.GameTimeController;
import l2open.gameserver.handler.IUserCommandHandler;
import l2open.gameserver.handler.UserCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SystemMessage;

/**
 * Support for /time command
 */
public class Time implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 77 };

	public boolean useUserCommand(int id, L2Player activeChar)
	{
		if(COMMAND_IDS[0] != id)
			return false;

		int t = GameTimeController.getInstance().getGameTime();
		int h = t / 60 % 24;
		int m = t % 60;

		SystemMessage sm;
		if(h >= 0 && h < 6)
			sm = new SystemMessage(SystemMessage.THE_CURRENT_TIME_IS_S1S2_IN_THE_NIGHT);
		else
			sm = new SystemMessage(SystemMessage.THE_CURRENT_TIME_IS_S1S2_IN_THE_DAY);
		sm.addNumber(h).addNumber(m);
		activeChar.sendPacket(sm);
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
