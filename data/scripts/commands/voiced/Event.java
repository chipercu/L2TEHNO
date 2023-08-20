package commands.voiced;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.*;


public class Event extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "event_invite" };

	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		command = command.intern();
		if(command.equalsIgnoreCase("event_invite"))
		{
			if(activeChar.getVarB("event_invite", true))
			{
				activeChar.setVar("event_invite", String.valueOf(false));
				activeChar.sendMessage("Приглашение на ивент отключены.");
			}
			else
			{
				activeChar.setVar("event_invite", String.valueOf(true));
				activeChar.sendMessage("Приглашение на ивент включены.");
			}
		}
		return false;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}