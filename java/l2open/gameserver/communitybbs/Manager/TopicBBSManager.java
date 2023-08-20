package l2open.gameserver.communitybbs.Manager;

import javolution.util.FastList;
import javolution.util.FastMap;
import l2open.gameserver.communitybbs.BB.Forum;
import l2open.gameserver.communitybbs.BB.Post;
import l2open.gameserver.communitybbs.BB.Topic;
import l2open.gameserver.model.L2Player;
import l2open.util.Log;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.DateFormat;
import java.util.*;

public class TopicBBSManager extends BaseBBSManager
{
	private List<Topic> _table;
	private Map<Forum, Integer> _Maxid;
	private static TopicBBSManager _Instance;

	public static TopicBBSManager getInstance()
	{
		if(_Instance == null)
			_Instance = new TopicBBSManager();
		return _Instance;
	}

	public TopicBBSManager()
	{
		_table = new FastList<Topic>();
		_Maxid = new FastMap<Forum, Integer>();
	}

	public void addTopic(Topic tt)
	{
		_table.add(tt);
	}

	public void delTopic(Topic topic)
	{
		_table.remove(topic);
	}

	public void setMaxID(int id, Forum f)
	{
		_Maxid.remove(f);
		_Maxid.put(f, id);
	}

	public int getMaxID(Forum f)
	{
		Integer i = _Maxid.get(f);
		if(i == null)
			return 0;
		return i;
	}

	public Topic getTopicByID(int idf)
	{
		for(Topic t : _table)
			if(t.getID() == idf)
				return t;
		return null;
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player)
	{
		if(ar1.equals("crea"))
		{
			Forum f = ForumsBBSManager.getInstance().getForumByID(Integer.parseInt(ar2));
			if(f == null)
				separateAndSend("<html><body><br><br><center>the forum: " + ar2 + " is not implemented yet</center><br><br></body></html>", player);
			else
			{
				f.vload();
				Topic t = new Topic(Topic.ConstructorType.CREATE, TopicBBSManager.getInstance().getMaxID(f) + 1, Integer.parseInt(ar2), ar5, Calendar.getInstance().getTimeInMillis(), player.getName(), player.getObjectId(), Topic.MEMO, 0);
				f.addtopic(t);
				TopicBBSManager.getInstance().setMaxID(t.getID(), f);
				Post p = new Post(player.getName(), player.getObjectId(), Calendar.getInstance().getTimeInMillis(), t.getID(), f.getID(), ar4);
				PostBBSManager.getInstance().addPostByTopic(p, t);
				parsecmd("_bbsmemo", player);
			}
			Log.add(player.getName()+": ar2='"+ar2+"' ar3='"+ar3+"' ar4='"+ar4+"' ar5='"+ar5+"'", "forum_create");
		}
		else if(ar1.equals("del"))
		{
			Forum f = ForumsBBSManager.getInstance().getForumByID(Integer.parseInt(ar2));
			if(f == null)
				separateAndSend("<html><body><br><br><center>the forum: " + ar2 + " does not exist !</center><br><br></body></html>", player);
			else
			{
				Topic t = f.gettopic(Integer.parseInt(ar3));
				if(t == null)
					separateAndSend("<html><body><br><br><center>the topic: " + ar3 + " does not exist !</center><br><br></body></html>", player);
				else
				{
					Post p = PostBBSManager.getInstance().getGPosttByTopic(t);
					if(p != null)
						p.deleteme(t);
					t.deleteme(f);
					parsecmd("_bbsmemo", player);
				}
			}
		}
		else
			separateAndSend("<html><body><br><br><center>the command: " + ar1 + " is not implemented yet</center><br><br></body></html>", player);
	}

	@Override
	public void parsecmd(String command, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(player == null)
			return;
		if(command.equals("_bbsmemo"))
		{
			if(player.getMemo() != null)
				showTopics(player.getMemo(), player, 1, player.getMemo().getID());
			else
				showTopics(null, player, 1, 0);
		}
		else if(command.startsWith("_bbstopics;read"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int idf = Integer.parseInt(st.nextToken());
			String index = null;
			if(st.hasMoreTokens())
				index = st.nextToken();
			int ind = 0;
			if(index == null)
				ind = 1;
			else
				ind = Integer.parseInt(index);
			showTopics(ForumsBBSManager.getInstance().getForumByID(idf), player, ind, idf);
		}
		else if(command.startsWith("_bbstopics;crea"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int idf = Integer.parseInt(st.nextToken());
			showNewTopic(ForumsBBSManager.getInstance().getForumByID(idf), player, idf);
		}
		else if(command.startsWith("_bbstopics;del"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int idf = Integer.parseInt(st.nextToken());
			int idt = Integer.parseInt(st.nextToken());
			Forum f = ForumsBBSManager.getInstance().getForumByID(idf);
			if(f == null)
				separateAndSend("<html><body><br><br><center>the forum: " + idf + " does not exist !</center><br><br></body></html>", player);
			else
			{
				Topic t = f.gettopic(idt);
				if(t == null)
					separateAndSend("<html><body><br><br><center>the topic: " + idt + " does not exist !</center><br><br></body></html>", player);
				else
				{
					Post p = PostBBSManager.getInstance().getGPosttByTopic(t);
					if(p != null)
						p.deleteme(t);
					t.deleteme(f);
					parsecmd("_bbsmemo", player);
				}
			}
		}
		else
			separateAndSend("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", player);
	}

	/**
	 * @param forumByID
	 * @param player
	 * @param idf
	 */
	private void showNewTopic(Forum forum, L2Player player, int idf)
	{
		if(forum == null)
			separateAndSend("<html><body><br><br><center>the forum: " + idf + " is not implemented yet</center><br><br></body></html>", player);
		else if(forum.getType() == Forum.MEMO)
			ShowMemoNewTopics(forum, player);
		else
			separateAndSend("<html><body><br><br><center>the forum: " + forum.getName() + " is not implemented yet</center><br><br></body></html>", player);
	}

	/**
	 * @param forum
	 * @param activeChar
	 */
	private void ShowMemoNewTopics(Forum forum, L2Player activeChar)
	{
		StringBuilder htmlCode = new StringBuilder("<html><body><center>");
		htmlCode.append("<br><br><br1><br1><table border=0 cellspacing=0 cellpadding=0>");
		htmlCode.append("<tr><td FIXWIDTH=15>&nbsp;</td>");
		htmlCode.append("<td width=755 height=30 align=left>");
		if(!activeChar.isLangRus())
			htmlCode.append("<a action=\"bypass -h _bbshome\">Home</a>&nbsp;&nbsp;&gt;&nbsp;<a action=\"bypass -h _bbsmemo\">Memo Form</a>&nbsp;&nbsp;&gt;&nbsp;Написать");
		else
			htmlCode.append("<a action=\"bypass -h _bbshome\">Главная</a>&nbsp;&nbsp;&gt;&nbsp;<a action=\"bypass -h _bbsmemo\">Памятка</a>&nbsp;&nbsp;&gt;&nbsp;Написать");
		htmlCode.append("</td></tr></table>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0><tr><td width=755><img src=\"L2UI.SquareGray\" width=\"755\" height=\"1\"></td></tr></table>");
		htmlCode.append("<table fixwidth=755 border=0 cellspacing=0 cellpadding=0>");
		htmlCode.append("<tr><td width=5 height=20></td></tr>");
		htmlCode.append("<tr>");
		htmlCode.append("<td width=5></td>");
		htmlCode.append("<td FIXWIDTH=80 height=29>&$413;:</td>");
		htmlCode.append("<td FIXWIDTH=520><edit var=\"Title\" width=670 height=13 length=\"128\"></td>");
		htmlCode.append("<td width=5></td>");
		htmlCode.append("</tr>");
		htmlCode.append("</table>");
		htmlCode.append("<table fixwidth=755 border=0 cellspacing=0 cellpadding=0>");
		htmlCode.append("<tr>");
		htmlCode.append("<td width=5></td>");
		htmlCode.append("<td FIXWIDTH=80 height=29 valign=top>&$427;:</td>");
		htmlCode.append("<td FIXWIDTH=520><MultiEdit var=\"Content\" width=670 height=313></td>");
		htmlCode.append("<td width=5></td>");
		htmlCode.append("</tr>");
		htmlCode.append("<tr><td width=5 height=10></td></tr>");
		htmlCode.append("</table>");
		htmlCode.append("<table fixwidth=755 border=0 cellspacing=0 cellpadding=0>");
		htmlCode.append("<tr><td height=10></td></tr>");
		htmlCode.append("<tr>");
		htmlCode.append("<td width=5></td>");
		htmlCode.append("<td align=center FIXWIDTH=80 height=29>&nbsp;</td>");
		htmlCode.append("<td align=center FIXWIDTH=70><button value=\"&$140;\" action=\"Write Topic crea " + forum.getID() + " Title Content Title\" back=\"l2ui_ct1.button.button_df_small_down\" width=70 height=25 fore=\"l2ui_ct1.button.button_df_small\" ></td>");
		htmlCode.append("<td align=center FIXWIDTH=70><button value=\"&$141;\" action=\"bypass -h _bbsmemo\" back=\"l2ui_ct1.button.button_df_small_down\" width=70 height=25 fore=\"l2ui_ct1.button.button_df_small\"></td>");
		htmlCode.append("<td align=center FIXWIDTH=70>&nbsp;</td>");
		htmlCode.append("<td align=center FIXWIDTH=340>&nbsp;</td><td width=5></td>");
		htmlCode.append("</tr></table>");
		htmlCode.append("</body></html>");
		send1001(htmlCode.toString(), activeChar);
		send1002(activeChar);
	}

	/**
	 * @param memo
	 */
	private void showTopics(Forum forum, L2Player player, int index, int idf)
	{
		if(forum == null)
			separateAndSend("<html><body><br><br><center>the forum: " + idf + " is not implemented yet</center><br><br></body></html>", player);
		else if(forum.getType() == Forum.MEMO)
			ShowMemoTopics(forum, player, index);
		else
			separateAndSend("<html><body><br><br><center>the forum: " + forum.getName() + " is not implemented yet</center><br><br></body></html>", player);
	}

	/**
	 * @param forum
	 * @param player
	 */
	private void ShowMemoTopics(Forum forum, L2Player activeChar, int index)
	{
		forum.vload();
		StringBuilder htmlCode = new StringBuilder("<html><body><center>");
		htmlCode.append("<br><br><br1><br1><table border=0 cellspacing=0 cellpadding=0>");
		htmlCode.append("<tr><td FIXWIDTH=15>&nbsp;</td>");
		htmlCode.append("<td width=755 height=30 align=left>");
		if(!activeChar.isLangRus())
			htmlCode.append("<a action=\"bypass -h _bbshome\">Home</a>&nbsp;&nbsp;&gt;&nbsp;Memo Form");
		else
			htmlCode.append("<a action=\"bypass -h _bbshome\">Главная</a>&nbsp;&nbsp;&gt;&nbsp;Памятка");
		htmlCode.append("</td></tr></table>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0>");
		htmlCode.append("<tr><td height=5></td></tr>");
		htmlCode.append("</table>");
		htmlCode.append("<table border=0 cellspacing=0 cellpadding=0 width=755 height=25 bgcolor=A7A19A>");
		htmlCode.append("<tr>");
		htmlCode.append("<td FIXWIDTH=5></td>");
		htmlCode.append("<td FIXWIDTH=500 align=center>&$413;</td>");
		htmlCode.append("<td FIXWIDTH=150 align=center></td>");
		htmlCode.append("<td FIXWIDTH=70 align=center>&$418;</td>");
		htmlCode.append("</tr>");
		htmlCode.append("</table>");

		for(int i = 0, j = getMaxID(forum) + 1; i < 12 * index; j--)
		{
			if(j < 0)
				break;
			Topic t = forum.gettopic(j);
			if(t != null)
			{
				String yy = "";
				String mm = "";
				String dd = "";
				String date = DateFormat.getInstance().format(new Date(t.getDate()));
				StringTokenizer st = new StringTokenizer(date.substring(0, date.length() - 5), ".");
				if(st.hasMoreTokens())
					dd = String.valueOf(st.nextToken());
				if(st.hasMoreTokens())
					mm = String.valueOf(st.nextToken());
				if(st.hasMoreTokens())
					yy = String.valueOf(st.nextToken());
				if(i >= 12 * (index - 1))
				{
					htmlCode.append("<table border=0 cellspacing=0 cellpadding=5 WIDTH=755><tr><td FIXWIDTH=5></td>");
					htmlCode.append("<td FIXWIDTH=500><a action=\"bypass -h _bbsposts;read;" + forum.getID() + ";" + t.getID() + "\">" + t.getName() + "</a></td><td FIXWIDTH=150 align=center></td>");
					htmlCode.append("<td FIXWIDTH=70 align=center>" + yy + "-" + mm + "-" + dd + "</td>");
					htmlCode.append("</tr></table><img src=\"L2UI.Squaregray\" width=\"755\" height=\"1\">");
				}
				i++;
			}
		}

		htmlCode.append("<br><table width=755 cellspace=0 cellpadding=0><tr>");
		htmlCode.append("<td width=50><button value=\"&$422;\" action=\"bypass -h _bbsmemo\" back=\"l2ui_ct1.button.button_df_small_down\" width=70 height=25 fore=\"l2ui_ct1.button.button_df_small\"></td>");
		htmlCode.append("<td width=510 align=center>");
		htmlCode.append("<table border=0><tr>");
		htmlCode.append("<td><table><tr><td></td></tr><tr>");

		if(index == 1)
			htmlCode.append("<td><button action=\"\" back=\"L2UI_CT1.Button_DF_Left_Down\" fore=\"L2UI_CT1.Button_DF_Left\" width=15 height=15 ></td>");
		else
			htmlCode.append("<td><button action=\"bypass -h _bbstopics;read;" + forum.getID() + ";" + (index - 1) + "\" back=\"L2UI_CT1.Button_DF_Left_Down\" fore=\"L2UI_CT1.Button_DF_Left\" width=15 height=15 ></td>");

		htmlCode.append("</tr></table></td>");

		int maxpage = getMaxID(forum) / 12;
		if(maxpage * 12 != getMaxID(forum))
			maxpage++;

		int i = 0;
		if(maxpage > 21)
		{
			if(index <= 11)
			{
				for(i = 1; i <= (10 + index); i++)
				{
					if(i == index)
						htmlCode.append("<td height=15> " + i + " </td>");
					else
						htmlCode.append("<td height=15><a action=\"bypass -h _bbstopics;read;" + forum.getID() + ";" + i + "\"> " + i + " </a></td>");
				}
			}
			else if(index > 11 && (maxpage - index) > 10)
			{
				for(i = (index - 10); i <= (index - 1); i++)
				{
					if(i == index)
						continue;
					else
						htmlCode.append("<td height=15><a action=\"bypass -h _bbstopics;read;" + forum.getID() + ";" + i + "\"> " + i + " </a></td>");
				}
				for(i = index; i <= (index + 10); i++)
				{
					if(i == index)
						htmlCode.append("<td height=15> " + i + " </td>");
					else
						htmlCode.append("<td height=15><a action=\"bypass -h _bbstopics;read;" + forum.getID() + ";" + i + "\"> " + i + " </a></td>");
				}
			}
			else if((maxpage - index) <= 10)
			{
				for(i = (index - 10); i <= maxpage; i++)
				{
					if(i == index)
						htmlCode.append("<td height=15> " + i + " </td>");
					else
						htmlCode.append("<td height=15><a action=\"bypass -h _bbstopics;read;" + forum.getID() + ";" + i + "\"> " + i + " </a></td>");
				}
			}
		}
		else
		{
			for(i = 1; i <= maxpage; i++)
			{
				if(i == index)
					htmlCode.append("<td height=15> " + i + " </td>");
				else
					htmlCode.append("<td height=15><a action=\"bypass -h _bbstopics;read;" + forum.getID() + ";" + i + "\"> " + i + " </a></td>");
			}
		}

		htmlCode.append("<td><table><tr><td></td></tr>");

		if(index == maxpage)
			htmlCode.append("<tr><td><button action=\"\" back=\"L2UI_CT1.Button_DF_Right_Down\" fore=\"L2UI_CT1.Button_DF_Right\" width=15 height=15 ></td></tr>");
		else
			htmlCode.append("<tr><td><button action=\"bypass -h _bbstopics;read;" + forum.getID() + ";" + (index + 1) + "\" back=\"L2UI_CT1.Button_DF_Right_Down\" fore=\"L2UI_CT1.Button_DF_Right\" width=15 height=15 ></td></tr>");

		htmlCode.append("</table></td></tr></table></td>");
		htmlCode.append("<td align=right><button value=\"&$421;\" action=\"bypass -h _bbstopics;crea;" + forum.getID() + "\" back=\"l2ui_ct1.button.button_df_small_down\" width=70 height=25 fore=\"l2ui_ct1.button.button_df_small\" ></td></tr>");
		htmlCode.append("<tr><td width=5 height=10></td></tr>");
		htmlCode.append("<tr>");
		htmlCode.append("<td></td>");
		htmlCode.append("<td align=center><table border=0><tr><td></td><td><edit var=\"Search\" width=130 height=15></td>");
		htmlCode.append("<td><button value=\"&$420;\" action=\"Write 5 -2 0 Search _ _\" back=\"l2ui_ct1.button.button_df_small_down\" width=70 height=25 fore=\"l2ui_ct1.button.button_df_small\"></td></tr></table></td>");
		htmlCode.append("</tr>");
		htmlCode.append("</table>");
		htmlCode.append("</center></body></html>");
		separateAndSend(htmlCode.toString(), activeChar);
	}
}