package commands.voiced;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.NetPing;
import l2open.util.Log;
import l2open.util.Util;

public class Debug extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "mobDbg", "dbg", "ping" };

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
		if(command.equalsIgnoreCase("ping"))
		{
			activeChar.getNetConnection().ping_send = 0;
			activeChar.sendPacket(new NetPing((int)(System.currentTimeMillis() - l2open.gameserver.GameStart.serverUpTime())));
		}
		else if(command.equalsIgnoreCase("mobDbg") || command.equalsIgnoreCase("dbg"))
		{
			L2Object target = activeChar.getTarget();
			if(target == null)
				target = activeChar;
			if(target.isCharacter())
			{
				Log.add(target.dump() + "AI: " + Util.dumpObject(target.getAI(), true, true, true) + "\n ========================================================================================== \n", "mobDbg");
				activeChar.sendMessage(target.getName() + "'s info dumped");
				return true;
			}
		}
		return false;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}