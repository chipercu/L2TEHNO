package commands.voiced;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.*;
import l2open.gameserver.tables.player.PlayerData;

/**
 * @author: Diagod
 * open-team.ru
 **/
public class Relog extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "relog"};

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
		if(command.startsWith("relog"))
			PlayerData.getInstance().relog(activeChar);
		return false;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}