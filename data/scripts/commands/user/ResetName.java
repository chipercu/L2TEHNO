package commands.user;


import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IUserCommandHandler;
import l2open.gameserver.handler.UserCommandHandler;
import l2open.gameserver.model.L2Player;

public class ResetName implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 117 };

	public boolean useUserCommand(int id, L2Player activeChar)
	{
		if(COMMAND_IDS[0] != id)
			return false;

		if(activeChar.getVar("TitleColor") != null)
		{
			activeChar.setTitleColor(Integer.decode("0xFFFF77"));
			activeChar.setVar("TitleColor", "0xFFFF77");
			activeChar.broadcastUserInfo(true);
			return true;
		}
		return false;
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