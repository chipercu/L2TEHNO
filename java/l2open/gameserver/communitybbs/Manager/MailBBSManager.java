package l2open.gameserver.communitybbs.Manager;

import javolution.util.FastList;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.serverpackets.ExMailArrived;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.serverpackets.SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang3.StringEscapeUtils;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MailBBSManager extends BaseBBSManager
{
	private static Log		_log		= LogFactory.getLog(MailBBSManager.class);

	private static MailBBSManager	_instance	= new MailBBSManager();

	public static MailBBSManager getInstance()
	{
		return _instance;
	}

	private class UpdateMail
	{
		private int	obj_id;
		private int	letterId;
		private int	senderId;
		private String	location;
		private String	recipientNames;
		private String	subject;
		private String	message;
		private String	sentDateFormated;
		private long	sendDate;
		private String	deleteDateFormated;
		private long	deleteDate;
		private String	unread;
	}

	public FastList<UpdateMail> getMail(L2Player activeChar)
	{
		FastList<UpdateMail> _letters = new FastList<UpdateMail>();

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet result = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM character_mail WHERE obj_id = ? ORDER BY letterId DESC");
			statement.setInt(1, activeChar.getObjectId());
			result = statement.executeQuery();
			while (result.next())
			{
				UpdateMail letter = new UpdateMail();
				letter.obj_id = result.getInt("obj_id");
				letter.letterId = result.getInt("letterId");
				letter.senderId = result.getInt("senderId");
				letter.location = result.getString("location");
				letter.recipientNames = result.getString("recipientNames");

				letter.subject = StringEscapeUtils.escapeHtml4(result.getString("subject")).replace("\n", "<br1>");
				letter.message = StringEscapeUtils.escapeHtml4(result.getString("message")).replace("\n", "<br1>");

				letter.sendDate = result.getLong("sendDate");
				letter.sentDateFormated = new SimpleDateFormat("yyyy-MM-dd").format(new Date(letter.sendDate));
				letter.deleteDate = result.getLong("deleteDate");
				letter.deleteDateFormated = new SimpleDateFormat("yyyy-MM-dd").format(new Date(letter.deleteDate));
				letter.unread = result.getString("unread");
				_letters.add(letter);
			}
		}
		catch(Exception e)
		{
			_log.warn("couldnt load mail for " + activeChar.getName(), e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, result);
		}
		return _letters;
	}

	private UpdateMail getLetter(L2Player activeChar, int letterId)
	{
		UpdateMail letter = new UpdateMail();
		for(UpdateMail temp : getMail(activeChar))
		{
			letter = temp;
			if(letter.letterId == letterId)
				break;
		}
		return letter;
	}

	@Override
	public void parsecmd(String command, L2Player activeChar)
	{
		if(activeChar.getEventMaster() != null && activeChar.getEventMaster().blockBbs())
			return;
		if(command.equals("_maillist_0_1_0_"))
		{
			showInbox(activeChar, 1);
		}
		else if(command.startsWith("_maillist_0_1_0_ "))
		{
			showInbox(activeChar, Integer.parseInt(command.substring(17)));
		}
		else if(command.equals("_maillist_0_1_0_sentbox"))
		{
			showSentbox(activeChar, 1);
		}
		else if(command.startsWith("_maillist_0_1_0_sentbox "))
		{
			showSentbox(activeChar, Integer.parseInt(command.substring(24)));
		}
		else if(command.equals("_maillist_0_1_0_archive"))
		{
			showMailArchive(activeChar, 1);
		}
		else if(command.startsWith("_maillist_0_1_0_archive "))
		{
			showMailArchive(activeChar, Integer.parseInt(command.substring(24)));
		}
		else if(command.equals("_maillist_0_1_0_temp_archive"))
		{
			showTempMailArchive(activeChar, 1);
		}
		else if(command.startsWith("_maillist_0_1_0_temp_archive "))
		{
			showTempMailArchive(activeChar, Integer.parseInt(command.substring(29)));
		}
		else if(command.equals("_maillist_0_1_0_write"))
		{
			showWriteView(activeChar);
		}
		else if(command.startsWith("_maillist_0_1_0_view "))
		{
			UpdateMail letter = getLetter(activeChar, Integer.parseInt(command.substring(21)));
			showLetterView(activeChar, letter);
			if(!letter.unread.equals("false"))
				setLetterToRead(letter.letterId);
		}
		else if(command.startsWith("_maillist_0_1_0_reply "))
		{
			UpdateMail letter = getLetter(activeChar, Integer.parseInt(command.substring(22)));
			showWriteView(activeChar, getCharName(letter.senderId), letter);
		}
		else if (command.startsWith("_maillist_0_1_0_delete "))
		{
			UpdateMail letter = getLetter(activeChar, Integer.parseInt(command.substring(23)));
			try
			{
				if(letter != null)
					deleteLetter(letter.letterId);
			}
			catch(Exception e)
			{}
			showInbox(activeChar, 1);
		}
		else
		{
			ShowBoard.separateAndSend("<html><body><br><br><center>The command: [" + command + "] isn't implemented yet!</center><br><br></body></html>", activeChar);
		}
	}
	
	private String abbreviate(String s, int maxWidth)
	{
		return s.length() > maxWidth ? s.substring(0, maxWidth--) + "..." : s;
	}

	private void showInbox(L2Player activeChar, int page)
	{
		int index = 0, minIndex = 0, maxIndex = 0;
		maxIndex = (page == 1 ? page * 14 : (page * 15) - 1);
		minIndex = maxIndex - 14;

		StringBuilder htmlCode = new StringBuilder("<html><body><br>");
		htmlCode.append("<html>");
		htmlCode.append("<body><br><br>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0 width=810><tr><td width=10></td><td width=800 height=30 align=left>");
		htmlCode.append("<a action=\"bypass -h _bbshome\">HOME</a>&nbsp;&gt;&nbsp;<a action=\"bypass -h _maillist_0_1_0_\">Inbox</a>");
		htmlCode.append("</td></tr>");
		htmlCode.append("</table>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0 width=810 bgcolor=808080>");
		htmlCode.append("<tr><td height=10></td></tr>");
		htmlCode.append("<tr>");
		htmlCode.append("<td fixWIDTH=5></td>");
		htmlCode.append("<td fixWIDTH=760>");
		htmlCode.append("<a action=\"bypass -h _maillist_0_1_0_\">[Inbox]</a>(").append(countLetters(activeChar, "inbox")).append(")&nbsp;").append("<a action=\"bypass -h _maillist_0_1_0_sentbox\">[Sent Box]</a>(").append(countLetters(activeChar, "sentbox")).append(")&nbsp;").append("<a action=\"bypass -h _maillist_0_1_0_archive\">[Mail Archive]</a>(").append(countLetters(activeChar, "archive")).append(")&nbsp;").append("<a action=\"bypass -h _maillist_0_1_0_temp_archive\">[Temporary Mail Archive]</a>(").append(countLetters(activeChar, "temparchive")).append(")</td>");
		htmlCode.append("<td fixWIDTH=5></td>");
		htmlCode.append("</tr>");
		htmlCode.append("<tr><td height=10></td></tr>");
		htmlCode.append("</table>");
		if(countLetters(activeChar, "inbox") == 0)
			htmlCode.append("<br><center>Your inbox is empty.</center>");
		else
		{
			htmlCode.append("<br>");
			htmlCode.append("<table border=0 cellspacing=0 cellpadding=2 bgcolor=808080 width=770>");
			htmlCode.append("<tr>");
			htmlCode.append("<td FIXWIDTH=5></td>");
			htmlCode.append("<td FIXWIDTH=150 align=center>Author</td>");
			htmlCode.append("<td FIXWIDTH=460 align=left>Title</td>");
			htmlCode.append("<td FIXWIDTH=150 align=center>Authoring Date</td>");
			htmlCode.append("<td FIXWIDTH=5></td>");
			htmlCode.append("</tr></table>");
			for(UpdateMail letter : getMail(activeChar))
			{
				if(activeChar.getObjectId() == letter.obj_id && letter.location.equals("inbox"))
				{
					if(index < minIndex)
					{
						index++;
						continue;
					}
					if(index > maxIndex)
						break;
					String tempName = getCharName(letter.senderId);
					htmlCode.append("<table border=0 cellspacing=0 cellpadding=2 width=770>");
					htmlCode.append("<tr>");
					htmlCode.append("<td FIXWIDTH=5></td>");
					htmlCode.append("<td FIXWIDTH=150 align=center>").append(abbreviate(tempName, 6)).append("</td>");
					htmlCode.append("<td FIXWIDTH=460 align=left><a action=\"bypass -h _maillist_0_1_0_view ").append(letter.letterId).append("\">").append(abbreviate(letter.subject, 51)).append("</a></td>");
					htmlCode.append("<td FIXWIDTH=150 align=center>").append(letter.sentDateFormated).append("</td>");
					htmlCode.append("<td FIXWIDTH=5></td>");
					htmlCode.append("</tr></table>");
					htmlCode.append("<img src=\"L2UI.SquareBlank\" width=\"770\" height=\"3\">");
					htmlCode.append("<img src=\"L2UI.SquareGrey\" width=\"770\" height=\"1\">");
					index++;
				}
			}
		}
		htmlCode.append("<table width=770><tr>");
		htmlCode.append("<td align=right><button value=\"Write\" action=\"bypass -h _maillist_0_1_0_write\" width=60 height=30 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("<center><table width=770><tr>");
		htmlCode.append("<td align=right><button action=\"bypass -h _maillist_0_1_0_ ").append(page == 1 ? page : page - 1).append("\" width=16 height=16 back=\"L2UI_ct1.button_df_left_down\" fore=\"L2UI_ct1.button_df_left\"></td>");
		for(int i = 1; i <= 7; i++)
			htmlCode.append("<td align=center fixedwidth=10><a action=\"bypass -h _maillist_0_1_0_ ").append(i).append("\">").append(i).append("</a></td>");
		htmlCode.append("<td align=left><button action=\"bypass -h _maillist_0_1_0_ ").append(page + 1).append("\" width=16 height=16 back=\"L2UI_ct1.button_df_right_down\" fore=\"L2UI_ct1.button_df_right\"></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("<table><tr>");
		htmlCode.append("<td align=right><combobox width=65 var=combo list=\"Writer\"></td>");
		htmlCode.append("<td align=center><edit var=\"keyword\" width=130 height=11 length=\"16\"></td>");
		htmlCode.append("<td align=left><button value=\"Search\" action=\"bypass -h _maillist_0_1_0_search $combo $keyword\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("</tr></table></center>");
		htmlCode.append("</body></html>");
		separateAndSend(htmlCode.toString(), activeChar);
	}

	private void showLetterView(L2Player activeChar, UpdateMail letter)
	{
		StringBuilder htmlCode = new StringBuilder("<html><body><br>");
		htmlCode.append("<html>");
		htmlCode.append("<body><br><br>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0 width=770><tr><td width=10></td><td height=30 width=760 align=left>");
		htmlCode.append("<a action=\"bypass -h _bbshome\">HOME</a>&nbsp;&gt;&nbsp;<a action=\"bypass -h _maillist_0_1_0_\">Inbox</a>");
		htmlCode.append("</td></tr>");
		htmlCode.append("</table>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0 width=770 bgcolor=808080>");
		htmlCode.append("<tr><td height=10></td></tr>");
		htmlCode.append("<tr>");
		htmlCode.append("<td FIXWIDTH=5 height=20></td>");
		htmlCode.append("<td FIXWIDTH=100 height=20 align=right>Sender:&nbsp;</td>");
		htmlCode.append("<td FIXWIDTH=360 height=20 align=left>").append(getCharName(letter.senderId)).append("</td>");//
		htmlCode.append("<td FIXWIDTH=150 height=20 align=right>Send Time:&nbsp;</td>");
		htmlCode.append("<td FIXWIDTH=150 height=20 align=left>").append(letter.sentDateFormated).append("</td>");//
		htmlCode.append("<td fixWIDTH=5 height=20></td>");
		htmlCode.append("</tr><tr>");
		htmlCode.append("<td FIXWIDTH=5 height=20></td>");
		htmlCode.append("<td FIXWIDTH=100 height=20 align=right>Recipient:&nbsp;</td>");
		htmlCode.append("<td FIXWIDTH=360 height=20 align=left>").append(letter.recipientNames).append("</td>");//
		htmlCode.append("<td FIXWIDTH=150 height=20 align=right>Delete Intended Time:&nbsp;</td>");
		htmlCode.append("<td FIXWIDTH=150 height=20 align=left>").append(letter.deleteDateFormated).append("</td>");//
		htmlCode.append("<td fixWIDTH=5 height=20></td>");
		htmlCode.append("</tr><tr>");
		htmlCode.append("<td FIXWIDTH=5 height=20></td>");
		htmlCode.append("<td FIXWIDTH=100 height=20 align=right>Title:&nbsp;</td>");
		htmlCode.append("<td FIXWIDTH=360 height=20 align=left>").append(letter.subject).append("</td>");//
		htmlCode.append("<td FIXWIDTH=150 height=20></td>");
		htmlCode.append("<td FIXWIDTH=150 height=20></td>");
		htmlCode.append("<td fixWIDTH=5 height=20></td>");
		htmlCode.append("</tr>");
		htmlCode.append("<tr><td height=10></td></tr>");
		htmlCode.append("</table>");
		htmlCode.append("<table width=770><tr>");
		htmlCode.append("<td height=10></td>");
		htmlCode.append("<td height=10></td>");
		htmlCode.append("<td height=10></td>");
		htmlCode.append("</tr><tr>");
		htmlCode.append("<td FIXWIDTH=100></td>");
		htmlCode.append("<td FIXWIDTH=560>").append(letter.message).append("</td>");
		htmlCode.append("<td FIXWIDTH=100></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("<img src=\"L2UI.SquareBlank\" width=\"770\" height=\"3\">");
		htmlCode.append("<img src=\"L2UI.SquareGrey\" width=\"770\" height=\"1\">");
		htmlCode.append("<table width=770><tr>");
		htmlCode.append("<td align=left><button value=\"View List\" action=\"bypass -h _maillist_0_1_0_\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("<td FIXWIDTH=300></td>");
		htmlCode.append("<td align=right><button value=\"Reply\" action=\"bypass -h _maillist_0_1_0_reply ").append(letter.letterId).append("\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("<td align=right><button value=\"Deliver\" action=\"bypass -h _maillist_0_1_0_deliver\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("<td align=right><button value=\"Delete\" action=\"bypass -h _maillist_0_1_0_delete ").append(letter.letterId).append("\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("<td align=right><button value=\"Store\" action=\"bypass -h _maillist_0_1_0_store ").append(letter.letterId).append("\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("<td align=right><button value=\"Mail Writing\" action=\"bypass -h _maillist_0_1_0_write\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("</body></html>");
		separateAndSend(htmlCode.toString(), activeChar);
	}

	private void showSentbox(L2Player activeChar, int page)
	{
		int index = 0, minIndex = 0, maxIndex = 0;
		maxIndex = (page == 1 ? page * 14 : (page * 15) - 1);
		minIndex = maxIndex - 14;

		StringBuilder htmlCode = new StringBuilder("<html><body><br>");
		htmlCode.append("<html>");
		htmlCode.append("<body><br><br>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0 width=810><tr><td width=10></td><td width=800 height=30 align=left>");
		htmlCode.append("<a action=\"bypass -h _bbshome\">HOME</a>&nbsp;&gt;&nbsp;<a action=\"bypass -h _maillist_0_1_0_sentbox\">Sent Box</a>");
		htmlCode.append("</td></tr>");
		htmlCode.append("</table>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0 width=810 bgcolor=808080>");
		htmlCode.append("<tr><td height=10></td></tr>");
		htmlCode.append("<tr>");
		htmlCode.append("<td fixWIDTH=5></td>");
		htmlCode.append("<td fixWIDTH=760>");
		htmlCode.append("<a action=\"bypass -h _maillist_0_1_0_\">[Inbox]</a>(").append(countLetters(activeChar, "inbox")).append(")&nbsp;").append("<a action=\"bypass -h _maillist_0_1_0_sentbox\">[Sent Box]</a>(").append(countLetters(activeChar, "sentbox")).append(")&nbsp;").append("<a action=\"bypass -h _maillist_0_1_0_archive\">[Mail Archive]</a>(").append(countLetters(activeChar, "archive")).append(")&nbsp;").append("<a action=\"bypass -h _maillist_0_1_0_temp_archive\">[Temporary Mail Archive]</a>(").append(countLetters(activeChar, "temparchive")).append(")</td>");
		htmlCode.append("<td fixWIDTH=5></td>");
		htmlCode.append("</tr>");
		htmlCode.append("<tr><td height=10></td></tr>");
		htmlCode.append("</table>");
		if(countLetters(activeChar, "sentbox") == 0)
			htmlCode.append("<br><center>Your sent box is empty.</center>");
		else
		{
			htmlCode.append("<br>");
			htmlCode.append("<table border=0 cellspacing=0 cellpadding=2 bgcolor=808080 width=770>");
			htmlCode.append("<tr>");
			htmlCode.append("<td FIXWIDTH=5></td>");
			htmlCode.append("<td FIXWIDTH=150 align=center>Author</td>");
			htmlCode.append("<td FIXWIDTH=460 align=left>Title</td>");
			htmlCode.append("<td FIXWIDTH=150 align=center>Authoring Date</td>");
			htmlCode.append("<td FIXWIDTH=5></td>");
			htmlCode.append("</tr></table>");
			for(UpdateMail letter : getMail(activeChar))
			{
				if(activeChar.getObjectId() == letter.obj_id  && letter.location.equals("sentbox"))
				{
					if(index < minIndex)
					{
						index++;
						continue;
					}
					if(index > maxIndex)
						break;
					String tempName = getCharName(letter.senderId);
					htmlCode.append("<table border=0 cellspacing=0 cellpadding=2 width=770>");
					htmlCode.append("<tr>");
					htmlCode.append("<td FIXWIDTH=5></td>");
					htmlCode.append("<td FIXWIDTH=150 align=center>").append(abbreviate(tempName, 6)).append("</td>");
					htmlCode.append("<td FIXWIDTH=460 align=left><a action=\"bypass -h _maillist_0_1_0_view ").append(letter.letterId).append("\">").append(abbreviate(letter.subject, 51)).append("</a></td>");
					htmlCode.append("<td FIXWIDTH=150 align=center>").append(letter.sentDateFormated).append("</td>");
					htmlCode.append("<td FIXWIDTH=5></td>");
					htmlCode.append("</tr></table>");
					htmlCode.append("<img src=\"L2UI.SquareBlank\" width=\"770\" height=\"3\">");
					htmlCode.append("<img src=\"L2UI.SquareGrey\" width=\"770\" height=\"1\">");
					index++;
				}
			}
		}
		htmlCode.append("<table width=770><tr>");
		htmlCode.append("<td align=right><button value=\"Write\" action=\"bypass -h _maillist_0_1_0_write\" width=60 height=30 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("<center><table width=770><tr>");
		htmlCode.append("<td align=right><button action=\"bypass -h _maillist_0_1_0_sentbox ").append(page == 1 ? page : page - 1).append("\" width=16 height=16 back=\"L2UI_ct1.button_df_left_down\" fore=\"L2UI_ct1.button_df_left\"></td>");
		for(int i = 1; i <= 7; i++)
			htmlCode.append("<td align=center fixedwidth=10><a action=\"bypass -h _maillist_0_1_0_sentbox ").append(i).append("\">").append(i).append("</a></td>");
		htmlCode.append("<td align=left><button action=\"bypass -h _maillist_0_1_0_sentbox ").append(page + 1).append("\" width=16 height=16 back=\"L2UI_ct1.button_df_right_down\" fore=\"L2UI_ct1.button_df_right\"></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("<table><tr>");
		htmlCode.append("<td align=right><combobox width=65 var=combo list=\"Writer\"></td>");
		htmlCode.append("<td align=center><edit var=\"keyword\" width=130 height=11 length=\"16\"></td>");
		htmlCode.append("<td align=left><button value=\"Search\" action=\"bypass -h _maillist_0_1_0_search $combo $keyword\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("</tr></table></center>");
		htmlCode.append("</body></html>");
		separateAndSend(htmlCode.toString(), activeChar);
	}

	private void showMailArchive(L2Player activeChar, int page)
	{
		int index = 0, minIndex = 0, maxIndex = 0;
		maxIndex = (page == 1 ? page * 14 : (page * 15) - 1);
		minIndex = maxIndex - 14;

		StringBuilder htmlCode = new StringBuilder("<html><body><br>");
		htmlCode.append("<html>");
		htmlCode.append("<body><br><br>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0 width=810><tr><td width=10></td><td width=800 height=30 align=left>");
		htmlCode.append("<a action=\"bypass -h _bbshome\">HOME</a>&nbsp;&gt;&nbsp;<a action=\"bypass -h _maillist_0_1_0_archive\">Mail Archive</a>");
		htmlCode.append("</td></tr>");
		htmlCode.append("</table>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0 width=810 bgcolor=808080>");
		htmlCode.append("<tr><td height=10></td></tr>");
		htmlCode.append("<tr>");
		htmlCode.append("<td fixWIDTH=5></td>");
		htmlCode.append("<td fixWIDTH=760>");
		htmlCode.append("<a action=\"bypass -h _maillist_0_1_0_\">[Inbox]</a>(").append(countLetters(activeChar, "inbox")).append(")&nbsp;").append("<a action=\"bypass -h _maillist_0_1_0_sentbox\">[Sent Box]</a>(").append(countLetters(activeChar, "sentbox")).append(")&nbsp;").append("<a action=\"bypass -h _maillist_0_1_0_archive\">[Mail Archive]</a>(").append(countLetters(activeChar, "archive")).append(")&nbsp;").append("<a action=\"bypass -h _maillist_0_1_0_temp_archive\">[Temporary Mail Archive]</a>(").append(countLetters(activeChar, "temparchive")).append(")</td>");
		htmlCode.append("<td fixWIDTH=5></td>");
		htmlCode.append("</tr>");
		htmlCode.append("<tr><td height=10></td></tr>");
		htmlCode.append("</table>");
		if(countLetters(activeChar, "archive") == 0)
			htmlCode.append("<br><center>Your mail archive is empty.</center>");
		else
		{
			htmlCode.append("<br>");
			htmlCode.append("<table border=0 cellspacing=0 cellpadding=2 bgcolor=808080 width=770>");
			htmlCode.append("<tr>");
			htmlCode.append("<td FIXWIDTH=5></td>");
			htmlCode.append("<td FIXWIDTH=150 align=center>Author</td>");
			htmlCode.append("<td FIXWIDTH=460 align=left>Title</td>");
			htmlCode.append("<td FIXWIDTH=150 align=center>Authoring Date</td>");
			htmlCode.append("<td FIXWIDTH=5></td>");
			htmlCode.append("</tr></table>");
			for(UpdateMail letter : getMail(activeChar))
			{
				if(activeChar.getObjectId() == letter.obj_id  && letter.location.equals("archive"))
				{
					if(index < minIndex)
					{
						index++;
						continue;
					}
					if(index > maxIndex)
						break;
					String tempName = getCharName(letter.senderId);
					htmlCode.append("<table border=0 cellspacing=0 cellpadding=2 width=770>");
					htmlCode.append("<tr>");
					htmlCode.append("<td FIXWIDTH=5></td>");
					htmlCode.append("<td FIXWIDTH=150 align=center>").append(abbreviate(tempName, 6)).append("</td>");
					htmlCode.append("<td FIXWIDTH=460 align=left><a action=\"bypass -h _maillist_0_1_0_view ").append(letter.letterId).append("\">").append(abbreviate(letter.subject, 51)).append("</a></td>");
					htmlCode.append("<td FIXWIDTH=150 align=center>").append(letter.sentDateFormated).append("</td>");
					htmlCode.append("<td FIXWIDTH=5></td>");
					htmlCode.append("</tr></table>");
					htmlCode.append("<img src=\"L2UI.SquareBlank\" width=\"770\" height=\"3\">");
					htmlCode.append("<img src=\"L2UI.SquareGrey\" width=\"770\" height=\"1\">");
					index++;
				}
			}
		}
		htmlCode.append("<table width=770><tr>");
		htmlCode.append("<td align=right><button value=\"Write\" action=\"bypass -h _maillist_0_1_0_write\" width=60 height=30 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("<center><table width=770><tr>");
		htmlCode.append("<td align=right><button action=\"bypass -h _maillist_0_1_0_archive ").append(page == 1 ? page : page - 1).append("\" width=16 height=16 back=\"L2UI_ct1.button_df_left_down\" fore=\"L2UI_ct1.button_df_left\"></td>");
		for(int i = 1; i <= 7; i++)
			htmlCode.append("<td align=center fixedwidth=10><a action=\"bypass -h _maillist_0_1_0_archive ").append(i).append("\">").append(i).append("</a></td>");
		htmlCode.append("<td align=left><button action=\"bypass -h _maillist_0_1_0_archive ").append(page + 1).append("\" width=16 height=16 back=\"L2UI_ct1.button_df_right_down\" fore=\"L2UI_ct1.button_df_right\"></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("<table><tr>");
		htmlCode.append("<td align=right><combobox width=65 var=combo list=\"Writer\"></td>");
		htmlCode.append("<td align=center><edit var=\"keyword\" width=130 height=11 length=\"16\"></td>");
		htmlCode.append("<td align=left><button value=\"Search\" action=\"bypass -h _maillist_0_1_0_search $combo $keyword\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("</tr></table></center>");
		htmlCode.append("</body></html>");
		separateAndSend(htmlCode.toString(), activeChar);
	}

	private void showTempMailArchive(L2Player activeChar, int page)
	{
		int index = 0, minIndex = 0, maxIndex = 0;
		maxIndex = (page == 1 ? page * 14 : (page * 15) - 1);
		minIndex = maxIndex - 14;

		StringBuilder htmlCode = new StringBuilder("<html><body><br>");
		htmlCode.append("<html>");
		htmlCode.append("<body><br><br>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0 width=810><tr><td width=10></td><td width=800 height=30 align=left>");
		htmlCode.append("<a action=\"bypass -h _bbshome\">HOME</a>&nbsp;&gt;&nbsp;<a action=\"bypass -h _maillist_0_1_0_temp_archive\">Temporary Mail Archive</a>");
		htmlCode.append("</td></tr>");
		htmlCode.append("</table>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0 width=810 bgcolor=808080>");
		htmlCode.append("<tr><td height=10></td></tr>");
		htmlCode.append("<tr>");
		htmlCode.append("<td fixWIDTH=5></td>");
		htmlCode.append("<td fixWIDTH=760>");
		htmlCode.append("<a action=\"bypass -h _maillist_0_1_0_\">[Inbox]</a>(").append(countLetters(activeChar, "inbox")).append(")&nbsp;").append("<a action=\"bypass -h _maillist_0_1_0_sentbox\">[Sent Box]</a>(").append(countLetters(activeChar, "sentbox")).append(")&nbsp;").append("<a action=\"bypass -h _maillist_0_1_0_archive\">[Mail Archive]</a>(").append(countLetters(activeChar, "archive")).append(")&nbsp;").append("<a action=\"bypass -h _maillist_0_1_0_temp_archive\">[Temporary Mail Archive]</a>(").append(countLetters(activeChar, "temparchive")).append(")</td>");
		htmlCode.append("<td fixWIDTH=5></td>");
		htmlCode.append("</tr>");
		htmlCode.append("<tr><td height=10></td></tr>");
		htmlCode.append("</table>");
		if(countLetters(activeChar, "temparchive") == 0)
			htmlCode.append("<br><center>Your temporary mail archive is empty.</center>");
		else
		{
			htmlCode.append("<br>");
			htmlCode.append("<table border=0 cellspacing=0 cellpadding=2 bgcolor=808080 width=770>");
			htmlCode.append("<tr>");
			htmlCode.append("<td FIXWIDTH=5></td>");
			htmlCode.append("<td FIXWIDTH=150 align=center>Author</td>");
			htmlCode.append("<td FIXWIDTH=460 align=left>Title</td>");
			htmlCode.append("<td FIXWIDTH=150 align=center>Authoring Date</td>");
			htmlCode.append("<td FIXWIDTH=5></td>");
			htmlCode.append("</tr></table>");
			for(UpdateMail letter : getMail(activeChar))
			{
				if(activeChar.getObjectId() == letter.obj_id && letter.location.equals("temparchive"))
				{
					if(index < minIndex)
					{
						index++;
						continue;
					}
					if(index > maxIndex)
						break;
					String tempName = getCharName(letter.senderId);
					htmlCode.append("<table border=0 cellspacing=0 cellpadding=2 width=770>");
					htmlCode.append("<tr>");
					htmlCode.append("<td FIXWIDTH=5></td>");
					htmlCode.append("<td FIXWIDTH=150 align=center>").append(abbreviate(tempName, 6)).append("</td>");
					htmlCode.append("<td FIXWIDTH=460 align=left><a action=\"bypass -h _maillist_0_1_0_view ").append(letter.letterId).append("\">").append(abbreviate(letter.subject, 51)).append("</a></td>");
					htmlCode.append("<td FIXWIDTH=150 align=center>").append(letter.sentDateFormated).append("</td>");
					htmlCode.append("<td FIXWIDTH=5></td>");
					htmlCode.append("</tr></table>");
					htmlCode.append("<img src=\"L2UI.SquareBlank\" width=\"770\" height=\"3\">");
					htmlCode.append("<img src=\"L2UI.SquareGrey\" width=\"770\" height=\"1\">");
					index++;
				}
			}
		}
		htmlCode.append("<table width=770><tr>");
		htmlCode.append("<td align=right><button value=\"Write\" action=\"bypass -h _maillist_0_1_0_write\" width=60 height=30 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("<center><table width=770><tr>");
		htmlCode.append("<td align=right><button action=\"bypass -h _maillist_0_1_0_temp_archive ").append(page == 1 ? page : page - 1).append("\" width=16 height=16 back=\"L2UI_ct1.button_df_left_down\" fore=\"L2UI_ct1.button_df_left\"></td>");
		for(int i = 1; i <= 7; i++)
			htmlCode.append("<td align=center fixedwidth=10><a action=\"bypass -h _maillist_0_1_0_temp_archive ").append(i).append("\">").append(i).append("</a></td>");
		htmlCode.append("<td align=left><button action=\"bypass -h _maillist_0_1_0_temp_archive ").append(page + 1).append("\" width=16 height=16 back=\"L2UI_ct1.button_df_right_down\" fore=\"L2UI_ct1.button_df_right\"></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("<table><tr>");
		htmlCode.append("<td align=right><combobox width=65 var=combo list=\"Writer\"></td>");
		htmlCode.append("<td align=center><edit var=\"keyword\" width=130 height=11 length=\"16\"></td>");
		htmlCode.append("<td align=left><button value=\"Search\" action=\"bypass -h _maillist_0_1_0_search $combo $keyword\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("</tr></table></center>");
		htmlCode.append("</body></html>");
		separateAndSend(htmlCode.toString(), activeChar);
	}

	private void showWriteView(L2Player activeChar)
	{
		StringBuilder htmlCode = new StringBuilder("<html><body><br>");
		htmlCode.append("<html>");
		htmlCode.append("<body><br><br>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0 width=770><tr><td width=10></td><td height=30 width=760 align=left>");
		htmlCode.append("<a action=\"bypass -h _bbshome\">HOME</a>&nbsp;&gt;&nbsp;<a action=\"bypass -h _maillist_0_1_0_\">Inbox</a>");
		htmlCode.append("</td></tr>");
		htmlCode.append("</table>");
		htmlCode.append("<img src=\"L2UI.SquareBlank\" width=\"770\" height=\"3\">");
		htmlCode.append("<img src=\"L2UI.SquareGrey\" width=\"770\" height=\"1\">");
		htmlCode.append("<table width=770><tr>");
		htmlCode.append("<td FIXWIDTH=5></td>");
		htmlCode.append("<td FIXWIDTH=60 align=center>Recipient</td>");
		htmlCode.append("<td FIXWIDTH=690 align=left><edit var=\"Recipients\" width=700 height=11 length=\"128\"></td>");
		htmlCode.append("<td FIXWIDTH=5></td>");
		htmlCode.append("</tr><tr>");
		htmlCode.append("<td FIXWIDTH=5></td>");
		htmlCode.append("<td FIXWIDTH=60 align=center>Title</td>");
		htmlCode.append("<td FIXWIDTH=690 align=left><edit var=\"Title\" width=700 height=11 length=\"128\"></td>");
		htmlCode.append("<td FIXWIDTH=5></td>");
		htmlCode.append("</tr><tr>");
		htmlCode.append("<td FIXWIDTH=5></td>");
		htmlCode.append("<td FIXWIDTH=60 align=center>Body</td>");
		htmlCode.append("<td FIXWIDTH=690 align=left><MultiEdit var=\"Message\" width=700 height=200></td>");
		htmlCode.append("<td FIXWIDTH=5></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("<table width=770><tr>");
		htmlCode.append("<td align=left></td>");
		htmlCode.append("<td FIXWIDTH=60></td>");
		htmlCode.append("<td align=left><button value=\"Send\" action=\"Write Mail Send _ Recipients Title Message\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("<td align=left><button value=\"Cancel\" action=\"bypass -h _maillist_0_1_0_\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("<td align=left><button value=\"Delete\" action=\"bypass -h _maillist_0_1_0_delete 0\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("<td FIXWIDTH=400></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("</body></html>");
		separateAndSend(htmlCode.toString(), activeChar);
	}

	private void showWriteView(L2Player activeChar, String parcipientName, UpdateMail letter)
	{
		StringBuilder htmlCode = new StringBuilder("<html><body><br>");
		htmlCode.append("<html>");
		htmlCode.append("<body><br><br>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0 width=770><tr><td width=10></td><td height=30 width=760 align=left>");
		htmlCode.append("<a action=\"bypass -h _bbshome\">HOME</a>&nbsp;&gt;&nbsp;<a action=\"bypass -h _maillist_0_1_0_\">Inbox</a>");
		htmlCode.append("</td></tr>");
		htmlCode.append("</table>");
		htmlCode.append("<img src=\"L2UI.SquareBlank\" width=\"770\" height=\"3\">");
		htmlCode.append("<img src=\"L2UI.SquareGrey\" width=\"770\" height=\"1\">");
		htmlCode.append("<table width=770><tr>");
		htmlCode.append("<td FIXWIDTH=5></td>");
		htmlCode.append("<td FIXWIDTH=60 align=center>Recipient</td>");
		htmlCode.append("<td FIXWIDTH=690 align=left><combobox width=684 var=\"Recipient\" list=\"").append(parcipientName).append("\"></td>");
		htmlCode.append("<td FIXWIDTH=5></td>");
		htmlCode.append("</tr><tr>");
		htmlCode.append("<td FIXWIDTH=5></td>");
		htmlCode.append("<td FIXWIDTH=60 align=center>Title</td>");
		htmlCode.append("<td FIXWIDTH=690 align=left><edit var=\"Title\" width=684 height=11 length=\"128\"></td>");
		htmlCode.append("<td FIXWIDTH=5></td>");
		htmlCode.append("</tr><tr>");
		htmlCode.append("<td FIXWIDTH=5></td>");
		htmlCode.append("<td FIXWIDTH=60 align=center valign=top>Body</td>");
		htmlCode.append("<td FIXWIDTH=690 align=left><multiedit var=\"Message\" width=684 height=300 length=\"2000\"></td>");
		htmlCode.append("<td FIXWIDTH=5></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("<table width=770><tr>");
		htmlCode.append("<td align=left></td>");
		htmlCode.append("<td FIXWIDTH=60></td>");
		htmlCode.append("<td align=left><button value=\"Send\" action=\"Write Mail Send _ Recipient Title Message\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("<td align=left><button value=\"Cancel\" action=\"bypass -h _maillist_0_1_0_\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("<td align=left><button value=\"Delete\" action=\"bypass -h _maillist_0_1_0_delete ").append(letter.letterId).append("\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		htmlCode.append("<td FIXWIDTH=400></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("</body></html>");

		send1001(htmlCode.toString(), activeChar);
		send1002(activeChar, " ", "Re: " + letter.subject, "0");
	}

	private void sendLetter(String recipients, String subject, String message, L2Player activeChar)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		FiltredPreparedStatement statement2 = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			String[] recipAr = recipients.split(";");
			message = message.replaceAll("\n", "<br1>");
			boolean sent = false;
			long date = Calendar.getInstance().getTimeInMillis();
			int countRecips = 0;
			int countTodaysLetters = 0;

			if(subject.isEmpty())
				subject = "(no subject)";

			for(UpdateMail letter : getMail(activeChar))
				if(date < letter.sendDate + Long.valueOf("86400000") && letter.location.equals("sentbox"))
					countTodaysLetters++;

			if(countTodaysLetters >= 10)
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.NO_MORE_MESSAGES_MAY_BE_SENT_AT_THIS_TIME_EACH_ACCOUNT_IS_ALLOWED_10_MESSAGES_PER_DAY));
				return;
			}

			for(String recipient : recipAr)
			{
				int recipId = getobj_id(recipient.trim());
				if(recipId == 0)
					activeChar.sendMessage("Could not find " + recipient.trim() + ", Therefor will not get mail.");
				else if(isGM(recipId) && !activeChar.isGM())
					activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_CANNOT_SEND_MAIL_TO_A_GM_SUCH_AS_S1));
				else if(isBlocked(activeChar, recipId) && !activeChar.isGM())
				{
					SystemMessage sm = new SystemMessage(SystemMessage.S1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_S1_);

					L2Player player = L2ObjectsStorage.getPlayer(recipId);
					if(player != null && player.isOnline())
						sm.addName(player);
					activeChar.sendPacket(sm);
				}
				else if(isRecipInboxFull(recipId) && !activeChar.isGM())
				{
					activeChar.sendMessage(recipient.trim() + "'s inbox is full.");
					activeChar.sendPacket(new SystemMessage(SystemMessage.THE_MESSAGE_WAS_NOT_SENT));

					L2Player player = L2ObjectsStorage.getPlayer(recipId);
					if(player != null && player.isOnline())
						player.sendPacket(new SystemMessage(SystemMessage.MAILBOX_IS_FULL100_MESSAGE_MAXIMUM));
				}
				else if((countRecips < 5 && !activeChar.isGM()) || activeChar.isGM())
				{
					statement = con.prepareStatement("INSERT INTO character_mail (obj_id, senderId, location, recipientNames, subject, message, sendDate, deleteDate, unread) VALUES (?,?,?,?,?,?,?,?,?)");
					statement.setInt(1, recipId);
					statement.setInt(2, activeChar.getObjectId());
					statement.setString(3, "inbox");
					statement.setString(4, recipients);
					statement.setString(5, subject);
					statement.setString(6, message);
					statement.setLong(7, date);
					statement.setLong(8, date + Long.valueOf("7948804000"));
					statement.setString(9, "true");
					statement.execute();
					DatabaseUtils.closeStatement(statement);
					sent = true;
					countRecips++;

					for(L2Player player : L2ObjectsStorage.getPlayers())
						if(player.getObjectId() == recipId && player.isOnline())
						{
							player.sendPacket(new SystemMessage(SystemMessage.YOUVE_GOT_MAIL));
							player.sendPacket(ExMailArrived.STATIC_PACKET);
						}

				}
			}
			statement2 = con.prepareStatement("INSERT INTO character_mail (obj_id, senderId, location, recipientNames, subject, message, sendDate, deleteDate, unread) VALUES (?,?,?,?,?,?,?,?,?)");
			statement2.setInt(1, activeChar.getObjectId());
			statement2.setInt(2, activeChar.getObjectId());
			statement2.setString(3, "sentbox");
			statement2.setString(4, recipients);
			statement2.setString(5, subject);
			statement2.setString(6, message);
			statement2.setLong(7, date);
			statement2.setLong(8, date + Long.valueOf("7948804000"));
			statement2.setString(9, "false");
			statement2.execute();
			DatabaseUtils.closeStatement(statement2);

			if(countRecips > 5 && !activeChar.isGM())
				activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_ARE_LIMITED_TO_FIVE_RECIPIENTS_AT_A_TIME));

			if(sent)
				activeChar.sendPacket(new SystemMessage(SystemMessage.YOUVE_SENT_MAIL));
		}
		catch(Exception e)
		{
			_log.warn("couldnt send letter for " + activeChar.getName(), e);
		}
		finally
		{
			DatabaseUtils.closeConnection(con);
		}
	}

	private int countLetters(L2Player activeChar, String location)
	{
		int count = 0;
		for(UpdateMail letter : getMail(activeChar))
			if(activeChar.getObjectId() == letter.obj_id && letter.location.equals(location))
				count++;
		return count;
	}

	private boolean isBlocked(L2Player activeChar, int recipId)
	{
		L2Player player = L2ObjectsStorage.getPlayer(recipId);
		if(player != null)
			if(player.isBlockAll() || player.getBlockList().contains(activeChar))
				return true;
		return false;
	}

	public void storeLetter(int letterId)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		FiltredPreparedStatement statement2 = null;
		ResultSet result = null;
		try
		{
			int ownerId, senderId;
			long date;
			String recipientNames, subject, message;

			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT obj_id, senderId, recipientNames, subject, message, sendDate  FROM character_mail WHERE letterId = ?");
			statement.setInt(1, letterId);
			result = statement.executeQuery();
			result.next();
			ownerId = result.getInt("obj_id");
			senderId = result.getInt("senderId");
			recipientNames = result.getString("recipientNames");
			subject = result.getString("subject");
			message = result.getString("message");
			date = result.getLong("sendDate");

			statement2 = con.prepareStatement("INSERT INTO character_mail_deleted (ownerId, letterId, senderId, recipientNames, subject, message, date) VALUES (?,?,?,?,?,?,?)");
			statement2.setInt(1, ownerId);
			statement2.setInt(2, letterId);
			statement2.setInt(3, senderId);
			statement2.setString(4, recipientNames);
			statement2.setString(5, subject);
			statement2.setString(6, message);
			statement2.setLong(7, date);
			statement2.execute();
		}
		catch(Exception e)
		{
			_log.warn("couldnt store letter " + letterId, e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, result);
			DatabaseUtils.closeStatement(statement2);
		}
	}

	public void deleteLetter(int letterId)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM character_mail WHERE letterId = ?");
			statement.setInt(1, letterId);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn("couldnt delete letter " + letterId, e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	private void setLetterToRead(int letterId)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE character_mail SET unread = ? WHERE letterId = ?");
			statement.setString(1, "false");
			statement.setInt(2, letterId);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn("couldnt set unread to false for " + letterId, e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	private String getCharName(int obj_id)
	{
		L2Player player = L2ObjectsStorage.getPlayer(obj_id);
		return player == null ? "No Name" : player.getName();
	}

	private int getobj_id(String charName)
	{
		L2Player player = L2World.getPlayer(charName);
		return player == null ? 0 : player.getObjectId();
	}

	private boolean isGM(int obj_id)
	{
		boolean isGM = false;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet result = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT accesslevel FROM characters WHERE obj_id = ?");
			statement.setInt(1, obj_id);
			result = statement.executeQuery();
			result.next();
			isGM = result.getInt(1) > 0;
		}
		catch(Exception e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, result);
		}
		return isGM;
	}

	private boolean isRecipInboxFull(int obj_id)
	{
		boolean isFull = false;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet result = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT COUNT(*) FROM character_mail WHERE obj_id = ? AND location = ?");
			statement.setInt(1, obj_id);
			statement.setString(2, "inbox");
			result = statement.executeQuery();
			result.next();
			isFull = result.getInt(1) >= 100;
		}
		catch(Exception e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, result);
		}
		return isFull;
	}

	/** FIXME is there a better way? */
	public boolean hasUnreadMail(L2Player activeChar)
	{
		boolean hasUnreadMail = false;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet result = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT COUNT(*) FROM character_mail WHERE obj_id = ? AND location = ? AND unread = ?");
			statement.setInt(1, activeChar.getObjectId());
			statement.setString(2, "inbox");
			statement.setString(3, "true");
			result = statement.executeQuery();
			result.next();
			hasUnreadMail = result.getInt(1) > 0;
		}
		catch(Exception e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, result);
		}
		return hasUnreadMail;
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{
		if(ar1.equals("Send"))
		{
			ar4 = StringEscapeUtils.escapeHtml4(ar4).replace("\n", "<br1>");
			ar5 = StringEscapeUtils.escapeHtml4(ar5).replace("\n", "<br1>");

			sendLetter(ar3, ar4, ar5, activeChar);
			showSentbox(activeChar, 0);
		}
	}
}
