package events.FightClub;

import java.text.NumberFormat;
import java.util.StringTokenizer;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;
import events.FightClub.FightClubManager;
import events.FightClub.Rate;

public class FightClubManagers extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	private static enum Commands
	{
		fightclub
	}

	public void parsecmd(String command, L2Player player)
	{
		if(!ConfigValue.FightClubEnabled)
		{
			String content = Files.read("data/scripts/events/FightClub/disabled.htm", player);
			separateAndSend(content, player);
			return;
		}
		if(command.equalsIgnoreCase("fightclub;index"))
		{
			String content = Files.read("data/scripts/events/FightClub/index.htm", player);
			separateAndSend(content, player);
		}
		else if(command.equalsIgnoreCase("fightclub;makebattle"))
			separateAndSend(battle(player), player);
		else if(command.equalsIgnoreCase("fightclub;info"))
		{
			String content = Files.read("data/scripts/events/FightClub/info.htm", player);
			separateAndSend(content, player);
		}
		else
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			String str1 = st.nextToken();
			if(str1.equalsIgnoreCase("fightclub;addbattle"))
			{
				long count = 0L;
				try
				{
					count = Long.parseLong(st.nextToken());
					if(count <= 0L || count > ConfigValue.MaxItemsCount)
						throw new NumberFormatException();
				}
				catch(Exception e)
				{
					result(player, "Ошибка!", "Вы не ввели количество, или неправильное число.");
					return;
				}
				String str2 = "";
				if(st.hasMoreTokens())
					for(str2 = st.nextToken(); st.hasMoreTokens(); str2 = str2 + " " + st.nextToken());
				String str3 = FightClubManager.addApplication(player, str2, count);
				if("OK".equalsIgnoreCase(str3))
					result(player, "Выполнено!", "Вы создали заявку на участие.<br>Ваша ставка - <font color=\"LEVEL\">" + count + " " + str2 + "</font><br><center>" + "Удачи!</center>");
				else if("NoItems".equalsIgnoreCase(str3))
					result(player, "Ошибка!", "У вас недостаточно или отсутствуют требующиеся предметы!");
				else if("reg".equalsIgnoreCase(str3))
					result(player, "Ошибка!", "Вы уже зарегистрированы! Если вы хотите изменить ставку, удалите старую регистрацию.");
			}
			else if(str1.equalsIgnoreCase("fightclub;delete"))
			{
				if(FightClubManager.isRegistered(player))
				{
					FightClubManager.deleteRegistration(player);
					result(player, "Выполнено!", "<center>Вы удалены из списка регистрации.</center>");
				}
				else
					result(player, "Ошибка!", "<center>Вы не были зарегистрированы на участие</center>");
			}
			else if(str1.equalsIgnoreCase("fightclub;openpage"))
				separateAndSend(fightsList(player, Integer.parseInt(st.nextToken())), player);
			else if(str1.equalsIgnoreCase("fightclub;tryaccept"))
				separateAndSend(result1(player, Integer.parseInt(st.nextToken())), player);
			else if(str1.equalsIgnoreCase("fightclub;accept"))
				result2(player, Integer.parseInt(st.nextToken()));
		}
	}

	private String fightsList(L2Player player, int val)
	{
		String content = Files.read("data/scripts/events/FightClub/fightslist.htm", player);
		StringBuilder sb = new StringBuilder();
		int i = FightClubManager.getRatesCount();
		int j = val * ConfigValue.RatesOnPage;
		if(j > i)
			j = i;
		if(i > 0)
		{
			sb.append("<table width=300>");
			for(int k = val * ConfigValue.RatesOnPage - ConfigValue.RatesOnPage; k < j; k++)
			{
				Rate rate = FightClubManager.getRateByIndex(k);
				sb.append("<tr>");
				sb.append("<td align=center width=95>");
				sb.append("<a action=\"bypass -h fightclub;tryaccept ").append(rate.getObjectId()).append("\">");
				sb.append("<font color=\"ffff00\">").append(rate.getPlayerName()).append("</font></a></td>");
				sb.append("<td align=center width=70>").append(rate.getPlayerLevel()).append("</td>");
				sb.append("<td align=center width=100><font color=\"ff0000\">");
				sb.append(rate.getPlayerClass()).append("</font></td>");
				sb.append("<td align=center width=135><font color=\"00ff00\">");
				sb.append(NumberFormat.getInstance().format(rate.getItemCount())).append(" ").append(rate.getItemName());
				sb.append("</font></td></tr>");
			}
			sb.append("</table><br><br><br>");
			int k = rate(i);
			sb.append("Страницы:&nbsp;");
			for(int m = 1; m <= k; m++)
				if(m == val)
					sb.append(m).append("&nbsp;");
				else
					sb.append("<a action=\"bypass -h fightclub;openpage ").append(m).append("\">").append(m).append("</a>&nbsp;");
		}
		else
			sb.append("<br><center>Ставок пока не сделано</center>");
		content = content.replace("%data%", sb.toString());
		return content;
	}

	private String battle(L2Player player)
	{
		String content = Files.read("data/scripts/events/FightClub/makebattle.htm", player);
		content = content.replace("%items%", FightClubManager.getItemsList());
		return content;
	}

	private String result1(L2Player player, int obj_id)
	{
		Rate rate = FightClubManager.getRateByStoredId(obj_id);
		String content = "";
		if(rate == null)
		{
			content = Files.read("data/scripts/events/FightClub/result.htm", player);
			content = content.replace("%title%", "Ошибка!");
			content = content.replace("%text%", "<center>Этот игрок уже участвует в битве!</center>");
			return content;
		}
		content = Files.read("data/scripts/events/FightClub/accept.htm", player);
		content = content.replace("%name%", rate.getPlayerName());
		content = content.replace("%class%", rate.getPlayerClass());
		content = content.replace("%level%", "" + rate.getPlayerLevel());
		content = content.replace("%rate%", NumberFormat.getInstance().format(rate.getItemCount()) + " " + rate.getItemName());
		content = content.replace("%storedId%", "" + rate.getObjectId());
		return content;
	}

	private void result2(L2Player player, int obj_id)
	{
		if(player.getObjectId() == obj_id)
		{
			result(player, "<center>Ошибка!", "Вы не можете вызвать на бой самого себя.</center>");
			return;
		}
		Rate rate = FightClubManager.getRateByStoredId(obj_id);
		if(player == null || rate == null)
		{
			result(player, "<center>Ошибка!", "</center>");
			return;
		}
		if(Functions.getItemCount(player, rate.getItemId()) < rate.getItemCount())
		{
			result(player, "<center>Ошибка!", "Вы не можете принять ставку, т.к. недостаточно необходимых предметов.");
			return;
		}
		if(FightClubManager.requestConfirmation(L2ObjectsStorage.getPlayer(obj_id), player))
			result(player, "Внимание!", "Вы отправили запрос сопернику. Если все условия соответствуют, Вас переместят на арену<br><center><font color=\"LEVEL\">Удачи!</font></center><br>");
	}

	private void result(L2Player player, String val1, String val2)
	{
		String content = Files.read("data/scripts/events/FightClub/result.htm", player);
		content = content.replace("%title%", val1);
		content = content.replace("%text%", val2);
		separateAndSend(content, player);
	}

	private int rate(int value)
	{
		if(value % ConfigValue.RatesOnPage > 0)
			return value / ConfigValue.RatesOnPage + 1;
		return value / ConfigValue.RatesOnPage;
	}

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{}

	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}
}