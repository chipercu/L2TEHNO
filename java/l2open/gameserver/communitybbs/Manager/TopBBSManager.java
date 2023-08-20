package l2open.gameserver.communitybbs.Manager;

import l2open.config.*;
import l2open.database.*;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.barahlo.CBBuffSch;
import l2open.util.Files;

import java.sql.ResultSet;

public class TopBBSManager extends BaseBBSManager
{
	private String showBuffList(L2Player player)
	{
		StringBuilder html = new StringBuilder();
		html.append("<table width=150>");
		if(player != null && player._buffSchem != null)
			for(CBBuffSch sch : player._buffSchem.values())
			{
				html.append("<tr>");
				html.append("<td>");
				html.append("<button value=\"" + sch.SchName + "\" action=\"bypass -h _bbsbuff;restore;" + sch.id + "; $tvari \" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
				html.append("</td>");
				html.append("<td>");
				html.append("<button value=\"Удалить\" action=\"bypass -h _bbsbuff;delete;" + sch.id + "\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
				html.append("</td>");
				html.append("</tr>");
			}
		html.append("</table>");
		return html.toString();
	}

	public void showTopPage(L2Player activeChar, String page, String subcontent)
	{
		if(page == null || page.isEmpty())
			page = "index";
		else
			page = page.replace("../", "").replace("../", "").replace("../", "").replace("..\\", "").replace("..\\", "").replace("..\\", ""); // делаем так для надежности ибо раньше проканывал вариант типа ....//

		if(page.equals("index") && activeChar.getEventMaster() != null)
		{
			String html = activeChar.getEventMaster().getBbsIndex(activeChar, "_bbstop;"+page);
			if(html != null)
			{
				separateAndSend(html, activeChar);
				return;
			}
		}

		page = ConfigValue.CommunityBoardHtmlRoot + page + ".htm";

		String content = readHtml(page, activeChar);
		if(content == null)
		{
			if(subcontent == null)
				content = "<html><body><br><br><center>404 Not Found: " + page + "</center></body></html>";
			else
				content = "<html><body>%content%</body></html>";
		}
		if(subcontent != null)
			content = content.replace("%content%", subcontent);
		content = content.replace("%sch%", showBuffList(activeChar));
		separateAndSend(content, activeChar);
	}

	@Override
	public void parsecmd(String command, L2Player activeChar)
	{
		if(activeChar.getEventMaster() != null && activeChar.getEventMaster().blockBbs())
			return;
		if(command.equals("_bbstop") || command.equals("_bbshome"))
			showTopPage(activeChar, "index", null);
		else if(command.startsWith("_bbstop;"))
			showTopPage(activeChar, command.replaceFirst("_bbstop;", ""), null);
		else
			separateAndSend("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", activeChar);
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{}

	private static TopBBSManager _Instance = new TopBBSManager();

	public static TopBBSManager getInstance()
	{
		return _Instance;
	}
}