package commands.voiced;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.GuardOpenURLPacket;

public class Url extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "url" };

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
		if(command.equalsIgnoreCase("url"))
		{
			activeChar.sendPacket(new GuardOpenURLPacket(args));
			return true;
		}
		return false;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}