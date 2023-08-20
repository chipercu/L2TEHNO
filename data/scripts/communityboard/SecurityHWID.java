package communityboard;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.util.*;

//import java.util.logging.Logger;

/**
 * @author Powered by L2CCCP
 */

public class SecurityHWID
{
	//private static final Logger _log = Logger.getLogger(SecurityHWID.class.getName());

	public static boolean lock(L2Player player)
	{
		if(!ConfigValue.AccHwidLockEnable)
		{
			//Functions.show(Files.read("data/scripts/commands/voiced/lock/lock_disable.htm", player), player, null); Решить выводить хтмлки или нет. Если да то выводить по языку.
			player.sendMessage("Сервис выключен."); //TODO: перевести на системное сообщение.
			return false;
		}

		if(ConfigValue.ProtectEnable)
		{
			if(player.getAccLock() != null && Util.contains(player.getAccLock(), player.getHWIDs()))
			{
				player.sendMessage("Ваш аккаунт, уже привязан к данному компьютеру.");
				return false;
			}
			else if(ConfigValue.AccHwidLockOnlyFullInfo && PlayerData.getInstance().canEnterMailOrQuestion(player, false))
			{
				player.sendMessage("Привязка доступна только для полных регистраций.");
				Functions.show(Files.read("data/scripts/services/enter_new_mail.htm", player), player, null);
				return false;
			}
			else if(ConfigValue.AccHwidLockOnlyFullInfo && PlayerData.getInstance().canEnterMailOrQuestion(player, true))
			{
				player.sendMessage("Привязка доступна только для полных регистраций.");
				Functions.show(Files.read("data/scripts/services/enter_new_mail.htm", player), player, null);
				return false;
			}
			else if(DifferentMethods.getPay(player, ConfigValue.AccHwidLockPriceId, ConfigValue.AccHwidLockPriceCount, true))
			{
				player.addAccLock(player.getHWIDs());
				player.broadcastSkill(new MagicSkillUse(player, player, 5662, 1, 0, 0), true);
				player.sendMessage("Аккаунт успешно привязан к железу вашего компьютера."); //TODO: перевести на системное сообщение.
				return true;
			}
		}
		else
		{
			player.sendMessage("В данный момент функция не доступна. Обратитесь за помощью к Администрации.");
			return false;
		}
		return false;
	}

	public static boolean unlock(L2Player player)
	{
		if(!ConfigValue.AccHwidLockEnable)
		{
			//Functions.show(Files.read("data/scripts/commands/voiced/lock/lock_disable.htm", player), player, null); Решить выводить хтмлки или нет. Если да то выводить по языку.
			player.sendMessage("Сервис выключен."); //TODO: перевести на системное сообщение.
			return false;
		}

		//Functions.show(Files.read("data/scripts/commands/voiced/lock/unlock_hwid.htm", player), player, null); Решить выводить хтмлки или нет. Если да то выводить по языку.
		player.removeAccLock(player.getHWIDs());
		player.sendMessage("Вы упешно сняли привязку аккаунта по HWID."); //TODO: перевести на системное сообщение.
		player.broadcastSkill(new MagicSkillUse(player, player, 6802, 1, 1000, 0), true);
		return true;
	}
}
