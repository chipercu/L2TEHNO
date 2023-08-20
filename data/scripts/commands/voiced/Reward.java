package commands.voiced;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.*;

public class Reward extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "checkrewtimer" };

	@Override
	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	@Override
	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		command = command.intern();
		if(command.startsWith("checkrewtimer"))
		{
			if(ConfigValue.Attainment13_Set.length == 0 || activeChar._AttainmentTask == null)
				return false;
			/*if(activeChar.getVarB("Attainment13_Msg", true))
			{*/
			//	activeChar.setVar("Attainment13_Msg", "false");

				int step = Math.min(activeChar.getVarInt("Attainment13_step", 0), (int)ConfigValue.Attainment13_Set.length-1);
				int time=(int)ConfigValue.Attainment13_Set[step][0]-activeChar.getVarInt("Attainment13_time", 0);

				activeChar.sendPacket(new SystemMessage(6492).addNumber(time%60));
				activeChar.sendPacket(new ExEventMatchMessage(9, "RewID="+ConfigValue.Attainment13_Set[step][1]+" RewCount="+ConfigValue.Attainment13_Set[step][2]));
			/*}
			else
			{
				activeChar.setVar("Attainment13_Msg", "true");
				activeChar.sendPacket(new SystemMessage(6496));
			}*/
		}
		return false;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}