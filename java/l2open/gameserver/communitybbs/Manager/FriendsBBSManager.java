package l2open.gameserver.communitybbs.Manager;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.Say2;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.FriendsTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.util.LogChat;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.StringTokenizer;

public class FriendsBBSManager extends BaseBBSManager
{
	private static final int friendsPerPage = 12;
	private static final int blocksPerPage = 12;

	private String joinTokens(StringTokenizer st, String glue)
	{
		if(!st.hasMoreTokens())
			return "";
		String result = st.nextToken();
		while(st.hasMoreTokens())
			result += glue + st.nextToken();
		return result;
	}

	@Override
	public void parsecmd(String command, L2Player activeChar)
	{
		if(activeChar.getEventMaster() != null && activeChar.getEventMaster().blockBbs())
			return;
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();

		if(cmd.equalsIgnoreCase("friendlist"))
		{
			int page = 0;
			try
			{
				page = Integer.parseInt(st.nextToken());
			}
			catch(Exception e)
			{}
			showFriendsList(activeChar, page);
			return;
		}

		if(cmd.equalsIgnoreCase("friendinfo"))
		{
			showFriendsPI(activeChar, joinTokens(st, "_"));
			return;
		}

		if(cmd.equalsIgnoreCase("friendadd"))
		{
			FriendsTable.getInstance().TryFriendInvite(activeChar, joinTokens(st, "_"));
			showFriendsList(activeChar, 0);
			return;
		}

		if(cmd.equalsIgnoreCase("frienddel"))
		{
			FriendsTable.getInstance().TryFriendDelete(activeChar, joinTokens(st, "_"));
			showFriendsList(activeChar, 0);
			return;
		}

		if(cmd.equalsIgnoreCase("blocklist"))
		{
			int page = 0;
			try
			{
				page = Integer.parseInt(st.nextToken());
			}
			catch(Exception e)
			{}
			showBlockList(activeChar, page);
			return;
		}

		if(cmd.equalsIgnoreCase("blockadd"))
		{
			activeChar.addToBlockList(joinTokens(st, "_").trim());
			showBlockList(activeChar, 0);
			return;
		}

		if(cmd.equalsIgnoreCase("blockdel"))
		{
			activeChar.removeFromBlockList(joinTokens(st, "_").trim());
			showBlockList(activeChar, 0);
			return;
		}

		separateAndSend("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", activeChar);
	}

	private void showFriendsPI(L2Player activeChar, String name)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name LIKE ? LIMIT 1");
			statement.setString(1, name);
			rset = statement.executeQuery();
			if(!rset.next())
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_ON_YOUR_FRIEND_LIST).addString(name));
				showFriendsList(activeChar, 0);
				return;
			}

			int targetId = rset.getInt("obj_Id");
			if(!FriendsTable.getInstance().checkIsFriends(activeChar.getObjectId(), targetId))
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_ON_YOUR_FRIEND_LIST).addString(name));
				showFriendsList(activeChar, 0);
				return;
			}
		}
		catch(Exception e)
		{
			showFriendsList(activeChar, 0);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		StringBuilder htmlCode = new StringBuilder("<html><body><br>");
		htmlCode.append("<table border=0><tr><td FIXWIDTH=15></td><td align=center>L2 Community Board<img src=\"sek.cbui355\" width=610 height=1></td></tr><tr><td FIXWIDTH=15></td><td>");
		L2Player player = L2ObjectsStorage.getPlayer(name);

		if(player != null)
		{
			String sex = player.getSex() == 1 ? "Female" : "Male";
			String levelApprox = "low";
			if(player.getLevel() >= 60)
				levelApprox = "very high";
			else if(player.getLevel() >= 40)
				levelApprox = "high";
			else if(player.getLevel() >= 20)
				levelApprox = "medium";
			htmlCode.append("<table border=0><tr><td>" + player.getName() + " (" + sex + " " + player.getTemplate().className + "):</td></tr>");
			htmlCode.append("<tr><td>Level: " + levelApprox + "</td></tr>");
			htmlCode.append("<tr><td><br></td></tr>");

			int uptime = (int) player.getUptime() / 1000;
			int h = uptime / 3600;
			int m = (uptime - h * 3600) / 60;
			int s = uptime - h * 3600 - m * 60;

			htmlCode.append("<tr><td>Uptime: " + h + "h " + m + "m " + s + "s</td></tr>");
			htmlCode.append("<tr><td><br></td></tr>");

			if(player.getClan() != null)
			{
				htmlCode.append("<tr><td>Clan: " + player.getClan().getName() + "</td></tr>");
				htmlCode.append("<tr><td><br></td></tr>");
			}

			htmlCode.append("<tr><td><multiedit var=\"pm\" width=240 height=40><button value=\"Send PM\" action=\"Write Region PM " + player.getName() + " pm pm pm\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr><tr><td><br><button value=\"Back\" action=\"bypass -h _friendlist_0_\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table>");
			htmlCode.append("</td></tr></table>");
			htmlCode.append("</body></html>");
			separateAndSend(htmlCode.toString(), activeChar);
		}
		else
			separateAndSend("<html><body><br><br><center>No player with name " + name + "</center><br><br></body></html>", activeChar);
	}

	private void showBlockList(L2Player activeChar, int page)
	{
		StringBuilder htmlCode = new StringBuilder("<html><body><br><br>");
		htmlCode.append("<table width=755 bgcolor=A7A19A>");
		htmlCode.append("<tr><td WIDTH=5></td><td height=10 WIDTH=750></td></tr>");
		htmlCode.append("<tr><td></td><td height=20>");
		htmlCode.append("<a action=\"bypass -h _friendlist_0_\">[Список друзей]</a> &nbsp; ");
		htmlCode.append("<a action=\"bypass -h _blocklist_0_\">[Список заблокированных]</a>");
		htmlCode.append("</td></tr><tr><td></td><td height=10></td></tr></table>");

		htmlCode.append("<img src=\"L2UI.squareblank\" width=1 height=10>");
		htmlCode.append("<center><font color=\"FF3333\">Список заблокированных</font></center>");

		float total = 0;
		String[] blockList = activeChar.getBlockList().toArray(new String[activeChar.getBlockList().size()]);
		if(blockList != null && blockList.length > 0)
		{
			Arrays.sort(blockList);
			htmlCode.append("<center><img src=\"L2UI.SquareWhite\" width=625 height=1></center><br>");
			htmlCode.append("<table height=350><tr><td height=350 valign=top><table>");
			total = blockList.length;
			int i = 0;
			for(String blockname : blockList)
			{
				if(i >= page * blocksPerPage && i < (page + 1) * blocksPerPage)
				{
					htmlCode.append("<tr><td width=20></td>");
					htmlCode.append("<td width=99 valign=top>" + blockname + "</td>");
					htmlCode.append("<td width=99 valign=top><button value=\"&$425;\" action=\"bypass -h _blockdel_" + blockname + "\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=75 height=22 fore=\"L2UI_CT1.Button_DF_Small\"></td>");
					htmlCode.append("</tr>");
				}
				i++;
			}
			htmlCode.append("</table></td></tr></table>");
			htmlCode.append("<br><center><img src=\"L2UI.SquareWhite\" width=625 height=1></center><br><br>");
		}

		int pages = total == 0 ? 0 : (int) Math.ceil(total / blocksPerPage) - 1;

		htmlCode.append("<table>");
		htmlCode.append("<tr><td width=20></td>");
		htmlCode.append("<td valign=top width=99><edit var=\"block\" width=95></td>");
		htmlCode.append("<td width=99><button value=\"&$993;\" action=\"bypass -h _blockadd_ $block\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=95 height=22 fore=\"L2UI_CT1.Button_DF_Small\"></td>");
		if(pages > 0)
		{
			String bt_prev = page > 0 ? "<button value=\"&$543;\" action=\"bypass -h _blocklist_" + String.valueOf(page - 1) + "_\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=80 height=25 fore=\"L2UI_CT1.Button_DF_Small\">" : "";
			String bt_next = pages > page ? "<button value=\"&$544;\" action=\"bypass -h _blocklist_" + String.valueOf(page + 1) + "_\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=80 height=25 fore=\"L2UI_CT1.Button_DF_Small\">" : "";

			htmlCode.append("<td width=250></td>");
			htmlCode.append("<td width=99>" + bt_prev + "</td>");
			htmlCode.append("<td align=center width=99>Page: " + String.valueOf(page + 1) + "/" + String.valueOf(pages + 1) + "</td>");
			htmlCode.append("<td width=99>" + bt_next + "</td>");
		}
		htmlCode.append("</tr></table>");
		htmlCode.append("</body></html>");
		separateAndSend(htmlCode.toString(), activeChar);
	}

	private void showFriendsList(L2Player activeChar, int page)
	{
		StringBuilder htmlCode = new StringBuilder("<html><body><br><br>");
		htmlCode.append("<table width=755 bgcolor=A7A19A>");
		htmlCode.append("<tr><td WIDTH=5></td><td height=10 WIDTH=750></td></tr>");
		htmlCode.append("<tr><td></td><td height=20>");
		htmlCode.append("<a action=\"bypass -h _friendlist_0_\">[Список друзей]</a> &nbsp; ");
		htmlCode.append("<a action=\"bypass -h _blocklist_0_\">[Список заблокированных]</a>");
		htmlCode.append("</td></tr><tr><td></td><td height=10></td></tr></table>");

		htmlCode.append("<img src=\"L2UI.squareblank\" width=1 height=10>");
		htmlCode.append("<center><font color=\"33FF33\">Список друзей</font></center>");

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		float total = 0;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT friend_id,char_name,(SELECT COUNT(*) FROM character_friends WHERE char_id=?) AS total FROM character_friends LEFT JOIN characters ON ( character_friends.friend_id = characters.obj_Id ) WHERE char_id=? LIMIT ?,?");
			statement.setInt(1, activeChar.getObjectId());
			statement.setInt(2, activeChar.getObjectId());
			statement.setInt(3, page * friendsPerPage);
			statement.setInt(4, friendsPerPage);
			rset = statement.executeQuery();

			if(rset.next())
			{
				htmlCode.append("<center><img src=\"L2UI.SquareWhite\" width=625 height=1></center><br>");
				htmlCode.append("<table height=350><tr><td height=350 valign=top><table>");
				do
				{
					if(total == 0)
						total = rset.getInt("total");
					int friendId = rset.getInt("friend_id");
					String friendName = rset.getString("char_name");
					L2Player friend = friendId != 0 ? L2ObjectsStorage.getPlayer(friendId) : L2ObjectsStorage.getPlayer(friendName);
					htmlCode.append("<tr><td width=20></td>");
					htmlCode.append("<td width=99 valign=top>" + (friend == null ? friendName : "<a action=\"bypass -h _friendinfo_" + friendName + "\">" + friendName + "</a>") + "</td>");
					htmlCode.append("<td align=center width=70 valign=top>" + (friend == null ? "Offline" : "Online") + "</td>");
					htmlCode.append("<td width=99 valign=top><button value=\"&$425;\" action=\"bypass -h _frienddel_" + friendName + "\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=75 height=22 fore=\"L2UI_CT1.Button_DF_Small\"></td>");
					htmlCode.append("</tr>");
				} while(rset.next());
				htmlCode.append("</table></td></tr></table>");
				htmlCode.append("<br><center><img src=\"L2UI.SquareWhite\" width=625 height=1></center><br><br>");
			}
		}
		catch(Exception e)
		{
			htmlCode.append("<tr><td>Can`t show friends list, call to GM</td></tr>");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		int pages = total == 0 ? 0 : (int) Math.ceil(total / friendsPerPage) - 1;

		htmlCode.append("<table>");
		htmlCode.append("<tr><td width=20></td>");
		htmlCode.append("<td valign=top width=99><edit var=\"invite\" width=95></td>");
		htmlCode.append("<td width=99><button value=\"&$396;\" action=\"bypass -h _friendadd_ $invite\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=80 height=22 fore=\"L2UI_CT1.Button_DF_Small\"></td>");
		if(pages > 0)
		{
			String bt_prev = page > 0 ? "<button value=\"&$543;\" action=\"bypass -h _friendlist_" + String.valueOf(page - 1) + "_\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=80 height=25 fore=\"L2UI_CT1.Button_DF_Small\">" : "";
			String bt_next = pages > page ? "<button value=\"&$544;\" action=\"bypass -h _friendlist_" + String.valueOf(page + 1) + "_\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=80 height=25 fore=\"L2UI_CT1.Button_DF_Small\">" : "";

			htmlCode.append("<td width=250></td>");
			htmlCode.append("<td width=99>" + bt_prev + "</td>");
			htmlCode.append("<td align=center width=99>Page: " + String.valueOf(page + 1) + "/" + String.valueOf(pages + 1) + "</td>");
			htmlCode.append("<td width=99>" + bt_next + "</td>");
		}

		htmlCode.append("</tr></table>");
		htmlCode.append("</body></html>");
		separateAndSend(htmlCode.toString(), activeChar);
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{
		if(activeChar == null)
			return;

		if(ar1.equals("PM"))
		{
			StringBuilder htmlCode = new StringBuilder("<html><body><br>");
			htmlCode.append("<table border=0><tr><td FIXWIDTH=15></td><td align=center>L2 Community Board<img src=\"sek.cbui355\" width=610 height=1></td></tr><tr><td FIXWIDTH=15></td><td>");

			try
			{
				L2Player reciever = L2ObjectsStorage.getPlayer(ar2);
				if(reciever == null)
				{
					htmlCode.append("Player not found!<br><button value=\"Back\" action=\"bypass -h _friendinfo_" + ar2 + "\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
					htmlCode.append("</td></tr></table></body></html>");
					separateAndSend(htmlCode.toString(), activeChar);
					return;
				}

				if(activeChar.getNoChannel() != 0)
				{
					if(activeChar.getNoChannelRemained() > 0 || activeChar.getNoChannel() < 0)
					{
						if(activeChar.getNoChannel() > 0)
							htmlCode.append("You are banned in all chats, time remained " + Math.round(activeChar.getNoChannelRemained() / 60000) + " min.<br><button value=\"Back\" action=\"bypass -h _friendinfo_" + reciever.getName() + "\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
						else
							htmlCode.append("You are banned in all chats permanently.<br><button value=\"Back\" action=\"bypass -h _friendinfo_" + reciever.getName() + "\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
						htmlCode.append("</td></tr></table></body></html>");
						separateAndSend(htmlCode.toString(), activeChar);
						return;
					}
					PlayerData.getInstance().updateNoChannel(activeChar, 0);
				}

				LogChat.add(ar3, "TELL", activeChar.getName(), reciever.getName());

				Say2 cs = new Say2(activeChar.getObjectId(), Say2C.TELL, activeChar.getName(), ar3);
				if(!reciever.getMessageRefusal())
				{
					reciever.sendPacket(cs);
					activeChar.sendPacket(cs);
					htmlCode.append("Message Sent<br><button value=\"Back\" action=\"bypass -h _friendinfo_" + reciever.getName() + "\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
					htmlCode.append("</td></tr></table></body></html>");
					separateAndSend(htmlCode.toString(), activeChar);
				}
				else
				{
					SystemMessage sm = Msg.THE_PERSON_IS_IN_A_MESSAGE_REFUSAL_MODE;
					activeChar.sendPacket(sm);
					showFriendsPI(activeChar, reciever.getName());
				}
			}
			catch(StringIndexOutOfBoundsException e)
			{}
		}
		else
			separateAndSend("<html><body><br><br><center>the command: " + ar1 + " is not implemented yet</center><br><br></body></html>", activeChar);
	}

	private static FriendsBBSManager _Instance = null;

	public static FriendsBBSManager getInstance()
	{
		if(_Instance == null)
			_Instance = new FriendsBBSManager();
		return _Instance;
	}
}