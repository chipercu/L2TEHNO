package l2open.status.gshandlers;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.Announcements;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.tables.player.PlayerData;
import l2open.util.AutoBan;
import l2open.util.Log;
import l2open.util.Util;

import java.io.PrintWriter;

public class HandlerNoChannel
{
	public static void BanChat(String fullCmd, String[] argv, PrintWriter _print)
	{
		if(argv.length < 3 || argv[1].equalsIgnoreCase("?") || argv[2] == null || argv[2].isEmpty())
			_print.println("USAGE: nochannel adminName charName [period] [reason]");
		else
		{
			int timeval = 30; // if no args, then 30 min default.
			if(argv.length > 3)
				try
				{
					timeval = Integer.parseInt(argv[3]);
				}
				catch(Exception E)
				{
					timeval = 30;
				}
			_print.println(Nochannel(null, argv[1], argv[2], timeval, argv.length > 4 ? Util.joinStrings(" ", argv, 4) : null));
		}
	}

	public static String Nochannel(L2Player adminChar, String adminName, String charName, int val, String reason)
	{
		L2Player player = L2World.getPlayer(charName);

		if(player != null)
			charName = player.getName();
		else if(Util.GetCharIDbyName(charName) == 0)
			return "Игрок " + charName + " не найден.";

		if((adminName == null || adminName.isEmpty()) && adminChar != null)
			adminName = adminChar.getName();

		if(reason == null || reason.isEmpty())
			reason = "не указана"; // if no args, then "не указана" default.

		String result, announce = null;
		if(val == 0) //unban
		{
			if(adminChar != null && !adminChar.getPlayerAccess().CanUnBanChat)
				return "Вы не имеете прав на снятие бана чата.";
			if(ConfigValue.MAT_ANNOUNCE)
				announce = ConfigValue.MAT_ANNOUNCE_NICK && adminName != null && !adminName.isEmpty() ? adminName + " снял бан чата с игрока " + charName + "." : "С игрока " + charName + " снят бан чата.";
			Log.add(adminName + " снял бан чата с игрока " + charName + ".", "banchat", adminChar);
			result = "Вы сняли бан чата с игрока " + charName + ".";
		}
		else if(val < 0)
		{
			if(adminChar != null && adminChar.getPlayerAccess().BanChatMaxValue > 0)
				return "Вы можете банить не более чем на " + adminChar.getPlayerAccess().BanChatMaxValue + " минут.";
			if(ConfigValue.MAT_ANNOUNCE)
				announce = ConfigValue.MAT_ANNOUNCE_NICK && adminName != null && !adminName.isEmpty() ? adminName + " забанил чат игроку " + charName + " на бессрочный период, причина: " + reason + "." : "Забанен чат игроку " + charName + " на бессрочный период, причина: " + reason + ".";
			Log.add(adminName + " забанил чат игроку " + charName + " на бессрочный период, причина: " + reason + ".", "banchat", adminChar);
			result = "Вы забанили чат игроку " + charName + " на бессрочный период.";
		}
		else
		{
			if(adminChar != null && !adminChar.getPlayerAccess().CanUnBanChat && (player == null || player.getNoChannel() != 0))
				return "Вы не имеете права изменять время бана.";
			if(adminChar != null && adminChar.getPlayerAccess().BanChatMaxValue != -1 && val > adminChar.getPlayerAccess().BanChatMaxValue)
				return "Вы можете банить не более чем на " + adminChar.getPlayerAccess().BanChatMaxValue + " минут.";
			if(ConfigValue.MAT_ANNOUNCE)
				announce = ConfigValue.MAT_ANNOUNCE_NICK && adminName != null && !adminName.isEmpty() ? adminName + " забанил чат игроку " + charName + " на " + val + " минут, причина: " + reason + "." : "Забанен чат игроку " + charName + " на " + val + " минут, причина: " + reason + ".";
			Log.add(adminName + " забанил чат игроку " + charName + " на " + val + " минут, причина: " + reason + ".", "banchat", adminChar);
			result = "Вы забанили чат игроку " + charName + " на " + val + " минут.";
		}

		if(player != null)
			updateNoChannel(player, val, reason);
		else
			AutoBan.ChatBan(charName, val, reason, adminName);

		if(announce != null)
			if(ConfigValue.MAT_ANNOUNCE_FOR_ALL_WORLD)
				Announcements.getInstance().announceToAll(announce);
			else
				Announcements.shout(adminChar, announce, Say2C.CRITICAL_ANNOUNCEMENT);

		return result;
	}

	private static void updateNoChannel(L2Player player, int time, String reason)
	{
		PlayerData.getInstance().updateNoChannel(player, time * 60000);
		if(time == 0)
			player.sendMessage(new CustomMessage("common.ChatUnBanned", player));
		else if(time > 0)
		{
			if(reason == null || reason.isEmpty())
				player.sendMessage(new CustomMessage("common.ChatBanned", player).addNumber(time));
			else
				player.sendMessage(new CustomMessage("common.ChatBannedWithReason", player).addNumber(time).addString(reason));
		}
		else if(reason == null || reason.isEmpty())
			player.sendMessage(new CustomMessage("common.ChatBannedPermanently", player));
		else
			player.sendMessage(new CustomMessage("common.ChatBannedPermanentlyWithReason", player).addString(reason));
	}
}