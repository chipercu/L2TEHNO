package commands.voiced;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Player;

import java.util.Date;

/**
 * @Author: Drizzy
 * @Date: 11.01.2012
 */
public class PremiumAccountInfo extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private final String[] _commandList = new String[] { "pa" };


	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}


	public boolean useVoicedCommand(String command, L2Player activeChar, String target)
	{
		if(command.equals("pa") && (target == null || target.equals("")))
		{
			
			if(activeChar.getBonus().RATE_ALL <= 1)
			{
				if(activeChar.getLang().equals("ru"))
					activeChar.sendMessage("У вас нету премиум аккаунта");
				else
					activeChar.sendMessage("You not have premium account");
			}
			else if(activeChar.getBonus().RATE_ALL > 1)
			{
				if(activeChar.getLang().equals("ru"))
					activeChar.sendMessage("До окончания действия премиум аккаунта осталось : " + new Date(activeChar.getBonus().bonus_expire_time[7] * 1000L).toString());
				else
					activeChar.sendMessage("Premium account expire time : "  + new Date(activeChar.getBonus().bonus_expire_time[7] * 1000L).toString());
			}

			/*if(activeChar.getBonus().INDEX < 0)
			{
				if(activeChar.getLang().equals("ru"))
					activeChar.sendMessage("У вас нету премиум аккаунта");
				else
					activeChar.sendMessage("You not have premium account");
			}
			else
			{
				if(activeChar.getLang().equals("ru"))
					activeChar.sendMessage("До окончания действия премиум аккаунта осталось : " + new Date(activeChar.getBonus().bonus_expire_time[13] * 1000L).toString());
				else
					activeChar.sendMessage("Premium account expire time : "  + new Date(activeChar.getBonus().bonus_expire_time[13] * 1000L).toString());
			}*/

			
			/*if(activeChar.getNetConnection().getBonus() <= 1)
			{
				if(activeChar.getLang().equals("ru"))
					activeChar.sendMessage("У вас нету премиум аккаунта");
				else
					activeChar.sendMessage("You not have premium account");
			}
			else if(activeChar.getNetConnection().getBonus() > 1)
			{
				if(activeChar.getLang().equals("ru"))
					activeChar.sendMessage("До окончания действия премиум аккаунта осталось : " + new Date(activeChar.getNetConnection().getBonusExpire() * 1000L).toString());
				else
					activeChar.sendMessage("Premium account expire time : "  + new Date(activeChar.getNetConnection().getBonusExpire() * 1000L).toString());
			}*/
			

			if(activeChar.getBonus().CanByTradeItemPA)
			{
				if(activeChar.getLang().equals("ru"))
					activeChar.sendMessage("До окончания действия трейд+ осталось : " + new Date(activeChar.getBonus().bonus_expire_time[10] * 1000L).toString());
				else
					activeChar.sendMessage("Trade+ expire time : "  + new Date(activeChar.getBonus().bonus_expire_time[10] * 1000L).toString());
			}
			
			return true;
		}
		return true;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}
