package l2open.gameserver.communitybbs.Manager;

import l2open.config.*;
import l2open.gameserver.GameTimeController;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.FakePlayersTable;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TreeSet;

/**
1169
1092
345

покраска титула


убрать статистику в КБ...





**/

public class RegionBBSManager extends BaseBBSManager
{
	private static final Object _lock = new Object();
	private static long player_names_cache_expire = 0;
	private static String[] player_names_cache;

	@Override
	public void parsecmd(String command, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(command.equals("_bbsloc"))
			showRegion(player, 0);
		else if(command.startsWith("_bbsloc;show;") && player.isGM())
		{
			int index = 0;
			try
			{
				index = Integer.parseInt(player.getVar("bbsloc_page"));
			}
			catch(Exception nfe)
			{}
			showRegion(player, index);
			
			AdminCommandHandler.getInstance().useAdminCommandHandler(player, "admin_character_list " + command.replaceFirst("_bbsloc;show;", ""));
		}
		else if(command.startsWith("_bbsloc;page;"))
		{
			try
			{
				int index = Integer.parseInt(command.replaceFirst("_bbsloc;page;", ""));
				showRegion(player, index);
			}
			catch(NumberFormatException nfe)
			{
				separateAndSend("<html><body><br><br><center>Error!</center><br><br></body></html>", player);
			}
		}
		else
			separateAndSend("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", player);
	}

	private String[] getPlayersArray(L2Player activeChar)
	{
		TreeSet<String> temp = new TreeSet<String>();
		for(L2Player player : L2ObjectsStorage.getPlayers())
		{
			if(player == null || (player.isInvisible() && player != activeChar && !activeChar.isGM()))
				continue;
			if(!(ConfigValue.HideGMStatus && player.isGM()))
				temp.add(player.getName());
		}
		temp.addAll(FakePlayersTable.getActiveFakePlayers());
		String[] player_names = temp.toArray(new String[temp.size()]);
		if(ConfigValue.CommunityBoardSortPlayersList)
			Arrays.sort(player_names);
		return player_names;
	}

	private void showRegion(L2Player player, int startIndex)
	{
		StringBuilder htmlCode = new StringBuilder("<html><body><br>");

		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Calendar cal = Calendar.getInstance();
		htmlCode.append("<table>");
		htmlCode.append("<tr><td width=100>Server Time: </td><td width=50>" + format.format(cal.getTime()) + "</td></tr>");
		int t = GameTimeController.getInstance().getGameTime();
		cal.set(Calendar.HOUR_OF_DAY, t / 60);
		cal.set(Calendar.MINUTE, t % 60);
		htmlCode.append("<tr><td width=100>Game Time: </td><td width=50>" + format.format(cal.getTime()) + "</td></tr>");
		htmlCode.append("</table>");
		htmlCode.append("<br><img src=\"L2UI.SquareWhite\" width=625 height=1><br>");

		if(ConfigValue.AllowCommunityBoardPlayersList.equalsIgnoreCase("all") || (ConfigValue.AllowCommunityBoardPlayersList.equalsIgnoreCase("GM") && player.isGM()))
		{
			if(player.isGM())
				player.setVar("bbsloc_page", String.valueOf(startIndex));

			String[] player_names;
			if(ConfigValue.CommunityBoardPlayersListCache > 0 && !player.isGM())
			{
				synchronized (_lock)
				{
					if(System.currentTimeMillis() > player_names_cache_expire)
					{
						player_names_cache = getPlayersArray(player);
						player_names_cache_expire = System.currentTimeMillis() + ConfigValue.CommunityBoardPlayersListCache;
					}
				}
				player_names = player_names_cache;
			}
			else
				player_names = getPlayersArray(player);

			htmlCode.append("<br> &nbsp; &nbsp; " + player_names.length + " Player(s) Online:<br><br>");

			htmlCode.append("<table border=0>");
			htmlCode.append("<tr><td><table border=0>");

			int cell = 0;
			int n = startIndex;
			for(int i = startIndex; i < startIndex + ConfigValue.NamePageSizeOnCommunityBoard; i++)
			{
				if(i >= player_names.length)
					break;

				String playerName = player_names[i]; // Get the current record

				cell++;

				if(cell == 1)
					htmlCode.append("<tr>");
				if(player.isGM())
					htmlCode.append("<td align=left valign=top fixwidth=120><a action=\"bypass -h _bbsloc;show;" + playerName + "\">" + playerName + "</a></td>");
				else
					htmlCode.append("<td align=left valign=top fixwidth=120>" + playerName + "</td>");

				if(cell == ConfigValue.NamePerRowOnCommunityBoard)
				{
					cell = 0;
					htmlCode.append("</tr>");
				}
				n++;
			}

			if(cell > 0 && cell < ConfigValue.NamePerRowOnCommunityBoard)
				htmlCode.append("</tr>");

			htmlCode.append("</table></td></tr>");

			if(player_names.length > ConfigValue.NamePageSizeOnCommunityBoard)
			{
				htmlCode.append("<tr><td> </td></tr>"); // для отступа
				htmlCode.append("<tr><td align=center valign=top>Displaying " + (startIndex + 1) + " - " + n + " player(s)</td></tr>");
				htmlCode.append("<tr><td align=center valign=top>");
				htmlCode.append("<table border=0 width=610><tr>");

				if(startIndex == 0)
					htmlCode.append("<td><button value=\"Prev\" width=50 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
				else
					htmlCode.append("<td><button value=\"Prev\" action=\"bypass -h _bbsloc;page;" + (startIndex - ConfigValue.NamePageSizeOnCommunityBoard) + "\" width=50 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");

				htmlCode.append("<td FIXWIDTH=10></td>");

				if(player_names.length <= startIndex + ConfigValue.NamePageSizeOnCommunityBoard)
					htmlCode.append("<td><button value=\"Next\" width=50 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
				else
					htmlCode.append("<td><button value=\"Next\" action=\"bypass -h _bbsloc;page;" + (startIndex + ConfigValue.NamePageSizeOnCommunityBoard) + "\" width=50 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");

				htmlCode.append("</tr></table>");
				htmlCode.append("</td></tr>");
			}

			htmlCode.append("</table>");
		}

		htmlCode.append("</body></html>");
		separateAndSend(htmlCode.toString(), player);
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player)
	{
		if(player == null)
			return;
		separateAndSend("<html><body><br><br><center>the command: " + ar1 + " is not implemented yet</center><br><br></body></html>", player);
	}

	private static RegionBBSManager _Instance = null;

	public static RegionBBSManager getInstance()
	{
		if(_Instance == null)
			_Instance = new RegionBBSManager();
		return _Instance;
	}
}