package communityboard;

import commands.voiced.Security;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.loginservercon.LSConnection;
import l2open.gameserver.loginservercon.gspackets.LockAccountIP;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.util.NetList;

/**
 * @author Powered by L2CCCP
 */
public class SecurityIP
{
	public static boolean lock(L2Player player)
	{
		if(!ConfigValue.LockAccountIP)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return false;
		}

		String ip = player.getIP();
		boolean invalid = false;

		try
		{
			NetList allowedList = new NetList();
			allowedList.LoadFromString(ip, ",");
			if(!allowedList.isIpInNets(player.getIP()))
				invalid = true;
		}
		catch(Exception e)
		{
			invalid = true;
		}

		if(invalid)
		{
			player.sendMessage(new CustomMessage("scripts.commands.voiced.Security.ip.error", player));
			return false;
		}

		Security.getInstance().showDefaultPage(player, Security.defaultPage);
		LSConnection.getInstance(player.getNetConnection().getLSId()).sendPacket(new LockAccountIP(player.getAccountName(), player.getIP(), (60 * 60 * 24 * 14)));
		player.sendMessage(new CustomMessage("scripts.commands.voiced.Security.ip.lock", player).addString(player.getIP()).addNumber(14).addString(DifferentMethods.declension(player, 14, "Days")));
		player.broadcastSkill(new MagicSkillUse(player, player, 5662, 1, 0, 0), true);
		return true;
	}

	public static boolean unlock(L2Player player)
	{
		if(!ConfigValue.LockAccountIP)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return false;
		}

		Security.getInstance().showDefaultPage(player, Security.defaultPage);
		LSConnection.getInstance(player.getNetConnection().getLSId()).sendPacket(new LockAccountIP(player.getAccountName(), "*", -1));
		player.sendMessage(new CustomMessage("scripts.commands.voiced.Security.ip.unlock", player));
		player.broadcastSkill(new MagicSkillUse(player, player, 6802, 1, 1000, 0), true);
		return true;
	}
}
