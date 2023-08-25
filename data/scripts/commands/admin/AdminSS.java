package commands.admin;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.SevenSigns;

public class AdminSS implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_ssq_change,
		admin_ssq_time,
		admin_ssq_cabal,
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().Menu)
			return false;

		switch(command)
		{
			case admin_ssq_change:
			{
				if(wordList.length > 2)
				{
					int period = Integer.parseInt(wordList[1]);
					int minutes = Integer.parseInt(wordList[2]);
					SevenSigns.getInstance().changePeriod(period, minutes * 60);
				}
				else if(wordList.length > 1)
				{
					int period = Integer.parseInt(wordList[1]);
					SevenSigns.getInstance().changePeriod(period);
				}
				else
					SevenSigns.getInstance().changePeriod();
				break;
			}
			case admin_ssq_time:
			{
				if(wordList.length > 1)
				{
					int time = Integer.parseInt(wordList[1]);
					SevenSigns.getInstance().setTimeToNextPeriodChange(time);
				}
				break;
			}
			case admin_ssq_cabal:
			{
				if(wordList.length > 3)
				{
					int player = Integer.parseInt(wordList[1]); // player objectid
					int cabal = Integer.parseInt(wordList[2]); // null dusk dawn
					int seal = Integer.parseInt(wordList[3]); // null avarice gnosis strife
					SevenSigns.getInstance().setPlayerInfo(player, cabal, seal);
				}
				break;
			}
		}
		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}