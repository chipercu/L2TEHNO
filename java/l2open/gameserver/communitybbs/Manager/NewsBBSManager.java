package l2open.gameserver.communitybbs.Manager;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;

/**
 * @author Powered by L2CCCP
 */
public class NewsBBSManager extends BaseBBSManager
{
	static final Logger _log = Logger.getLogger(NewsBBSManager.class.getName());
	private static NewsBBSManager _instance;

	public static NewsBBSManager getInstance()
	{
		if(_instance == null)
			_instance = new NewsBBSManager();

		return _instance;
	}

	public static class ProjectManager
	{
		public int[] id = new int[ConfigValue.NewsCount];
		public int[] type = new int[ConfigValue.NewsCount];
		public String[] title_ru = new String[ConfigValue.NewsCount];
		public String[] title_en = new String[ConfigValue.NewsCount];
		public String[] text_ru = new String[ConfigValue.NewsCount];
		public String[] text_en = new String[ConfigValue.NewsCount];
		public String[] info_ru = new String[ConfigValue.NewsCount];
		public String[] info_en = new String[ConfigValue.NewsCount];
		public String[] author = new String[ConfigValue.NewsCount];
		public Date[] date = new Date[ConfigValue.NewsCount];
	}

	public static class ServerManager
	{
		public int[] id = new int[ConfigValue.NewsCount];
		public int[] type = new int[ConfigValue.NewsCount];
		public String[] title_ru = new String[ConfigValue.NewsCount];
		public String[] title_en = new String[ConfigValue.NewsCount];
		public String[] text_ru = new String[ConfigValue.NewsCount];
		public String[] text_en = new String[ConfigValue.NewsCount];
		public String[] info_ru = new String[ConfigValue.NewsCount];
		public String[] info_en = new String[ConfigValue.NewsCount];
		public String[] author = new String[ConfigValue.NewsCount];
		public Date[] date = new Date[ConfigValue.NewsCount];
	}

	static ProjectManager ProjectNews = new ProjectManager();
	static ServerManager ServerNews = new ServerManager();

	public long lUpdateTime = System.currentTimeMillis() / 1000;

	DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	@Override
	public void parsecmd(String command, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(!ConfigValue.NewsAllow)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}
		else if(command.startsWith("_bbsproject"))
		{
			StringTokenizer id = new StringTokenizer(command, ": ");
			id.nextToken();
			int number = Integer.parseInt(id.nextToken());
			showProject(player, number);
		}
		else if(command.startsWith("_bbsserver"))
		{
			StringTokenizer id = new StringTokenizer(command, ":");
			id.nextToken();
			int number = Integer.parseInt(id.nextToken());
			showServer(player, number);
		}
		else
			separateAndSend("<html><body><br><br><center>На данный момент функция: " + command + " пока не реализована</center><br><br>", player);
	}

	public String parse(L2Player player, String type, int id)
	{
		StringBuilder html = new StringBuilder();

		if(type.equals("project"))
		{
			if(ProjectNews.title_ru[id] != null)
			{
				String date = dateFormat.format(ProjectNews.date[id]);
				html.append("<table border=0 cellspacing=4 cellpadding=3>");
				html.append("<tr>");
				html.append("<td FIXWIDTH=50 align=right valign=top>");
				html.append("<img src=l2ui.ActionWnd.bbs_Webfolder width=32 height=32>");
				html.append("</td>");
				html.append("<td FIXWIDTH=200 align=left valign=top>");
				html.append("<font color=\"8DB600\"><a action=\"bypass -h _bbsproject:").append(ProjectNews.id[id]).append("\">").append(player.isLangRus() ? ProjectNews.title_ru[id] : ProjectNews.title_en[id]).append("</a></font><br1>");
				html.append("<font color=AAAAAA>").append(player.isLangRus() ? ProjectNews.info_ru[id] : ProjectNews.info_en[id]).append("<font>");
				html.append("</td>");
				html.append("<td FIXWIDTH=95 align=center valign=top>").append(date).append("</td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("<table border=0 cellspacing=0 cellpadding=0>");
				html.append("<tr>");
				html.append("<td width=345>");
				html.append("<img src=l2ui.squaregray width=345 height=1>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
			}
			else
			{
				html.append("<table border=0 cellspacing=4 cellpadding=3>");
				html.append("<tr>");
				html.append("<td FIXWIDTH=50 align=right valign=top>");
				html.append("<img src=l2ui.ActionWnd.bbs_Webfolder width=32 height=32>");
				html.append("</td>");
				html.append("<td FIXWIDTH=200 align=left valign=top>");
				html.append("<font color=\"FF0000\">...<br1>...</font>");
				html.append("</td>");
				html.append("<td FIXWIDTH=95 align=center valign=top><font color=\"FF0000\">...</font></td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("<table border=0 cellspacing=0 cellpadding=0>");
				html.append("<tr>");
				html.append("<td width=345>");
				html.append("<img src=l2ui.squaregray width=345 height=1>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
			}
		}
		else if(type.equals("server"))
		{
			if(ServerNews.title_ru[id] != null)
			{
				String date = dateFormat.format(ServerNews.date[id]);
				html.append("<table border=0 cellspacing=4 cellpadding=3>");
				html.append("<tr>");
				html.append("<td FIXWIDTH=50 align=right valign=top>");
				html.append("<img src=l2ui.bbs_folder width=32 height=32>");
				html.append("</td>");
				html.append("<td FIXWIDTH=200 align=left valign=top>");
				html.append("<font color=\"8DB600\"><a action=\"bypass -h _bbsserver:").append(ServerNews.id[id]).append("\">").append(player.isLangRus() ? ServerNews.title_ru[id] : ServerNews.title_en[id]).append("</a></font><br1>");
				html.append("<font color=AAAAAA>").append(player.isLangRus() ? ServerNews.info_ru[id] : ServerNews.info_en[id]).append("<font>");
				html.append("</td>");
				html.append("<td FIXWIDTH=95 align=center valign=top>").append(date).append("</td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("<table border=0 cellspacing=0 cellpadding=0>");
				html.append("<tr>");
				html.append("<td width=345>");
				html.append("<img src=l2ui.squaregray width=345 height=1>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
			}
			else
			{
				html.append("<table border=0 cellspacing=4 cellpadding=3>");
				html.append("<tr>");
				html.append("<td FIXWIDTH=50 align=right valign=top>");
				html.append("<img src=l2ui.bbs_folder width=32 height=32>");
				html.append("</td>");
				html.append("<td FIXWIDTH=200 align=left valign=top>");
				html.append("<font color=\"FF0000\">...<br1>...</font>");
				html.append("</td>");
				html.append("<td FIXWIDTH=95 align=center valign=top><font color=\"FF0000\">...</font></td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("<table border=0 cellspacing=0 cellpadding=0>");
				html.append("<tr>");
				html.append("<td width=345>");
				html.append("<img src=l2ui.squaregray width=345 height=1>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
			}
		}
		return html.toString();
	}

	public void selectPortalNews()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		int counter = 0;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM bbs_news WHERE type='0' ORDER BY date DESC, id DESC LIMIT 0," + ConfigValue.NewsCount + "");
			rset = statement.executeQuery();

			while(rset.next())
			{
				ProjectNews.id[counter] = rset.getInt("id");
				ProjectNews.type[counter] = rset.getInt("type");
				ProjectNews.title_ru[counter] = rset.getString("title_ru");
				ProjectNews.title_en[counter] = rset.getString("title_en");
				ProjectNews.text_ru[counter] = rset.getString("text_ru");
				ProjectNews.text_en[counter] = rset.getString("text_en");
				ProjectNews.info_ru[counter] = rset.getString("info_ru");
				ProjectNews.info_en[counter] = rset.getString("info_en");
				ProjectNews.author[counter] = rset.getString("author");
				ProjectNews.date[counter] = rset.getDate("date");
				counter++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void selectServerNews()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		int counter = 0;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM bbs_news WHERE type='1' ORDER BY date DESC, id DESC LIMIT 0," + ConfigValue.NewsCount + "");
			rset = statement.executeQuery();

			while(rset.next())
			{
				ServerNews.id[counter] = rset.getInt("id");
				ServerNews.type[counter] = rset.getInt("type");
				ServerNews.title_ru[counter] = rset.getString("title_ru");
				ServerNews.title_en[counter] = rset.getString("title_en");
				ServerNews.text_ru[counter] = rset.getString("text_ru");
				ServerNews.text_en[counter] = rset.getString("text_en");
				ServerNews.info_ru[counter] = rset.getString("info_ru");
				ServerNews.info_en[counter] = rset.getString("info_en");
				ServerNews.author[counter] = rset.getString("author");
				ServerNews.date[counter] = rset.getDate("date");
				counter++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	private void showProject(L2Player player, int number)
	{
		String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "news/project.htm", player);

		for(int i = 0; i < ConfigValue.NewsCount; i++)
		{
			if(ProjectNews.id[i] == number)
			{
				String date = dateFormat.format(ProjectNews.date[i]);
				content = content.replace("<?title?>", player.isLangRus() ? ProjectNews.title_ru[i] : ProjectNews.title_en[i]);
				content = content.replace("<?content?>", player.isLangRus() ? ProjectNews.text_ru[i] : ProjectNews.text_en[i]);
				content = content.replace("<?date?>", date);
				content = content.replace("<?info?>", player.isLangRus() ? ProjectNews.info_ru[i] : ProjectNews.info_en[i]);
				content = content.replace("<?author?>", ProjectNews.author[i]);
			}
		}

		separateAndSend(content, player);
	}

	private void showServer(L2Player player, int number)
	{
		String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "news/server.htm", player);

		for(int i = 0; i < ConfigValue.NewsCount; i++)
		{
			if(ServerNews.id[i] == number)
			{
				String date = dateFormat.format(ServerNews.date[i]);
				content = content.replace("<?title?>", player.isLangRus() ? ServerNews.title_ru[i] : ServerNews.title_en[i]);
				content = content.replace("<?content?>", player.isLangRus() ? ServerNews.text_ru[i] : ServerNews.text_en[i]);
				content = content.replace("<?date?>", date);
				content = content.replace("<?info?>", player.isLangRus() ? ServerNews.info_ru[i] : ServerNews.info_en[i]);
				content = content.replace("<?author?>", ServerNews.author[i]);
			}
		}

		separateAndSend(content, player);
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{}
}