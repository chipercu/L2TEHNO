package l2open.gameserver.communitybbs.BB;

import javolution.util.FastList;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.communitybbs.Manager.PostBBSManager;

import org.apache.commons.lang3.StringEscapeUtils;

import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Maktakien
 *
 */
public class Post
{
	private static Logger _log = Logger.getLogger(Post.class.getName());

	public class CPost
	{
		public int _PostID;
		public String _PostOwner;
		public int _PostOwnerID;
		public long _PostDate;
		public int _PostTopicID;
		public int _PostForumID;
		public String _PostTxt;
	}

	private List<CPost> _post;

	//public enum ConstructorType {REPLY, CREATE };
	public Post(String _PostOwner, int _PostOwnerID, long date, int tid, int _PostForumID, String txt)
	{
		_post = new FastList<CPost>();
		CPost cp = new CPost();
		cp._PostID = 0;

		cp._PostOwner = StringEscapeUtils.escapeHtml4(_PostOwner).replace("\n", "<br1>");
		cp._PostOwnerID = _PostOwnerID;
		cp._PostDate = date;
		cp._PostTopicID = tid;
		cp._PostForumID = _PostForumID;

		cp._PostTxt = StringEscapeUtils.escapeHtml4(txt).replace("\n", "<br1>");
		_post.add(cp);
		insertindb(cp);

	}

	public void insertindb(CPost cp)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO posts (post_id,post_owner_name,post_ownerid,post_date,post_topic_id,post_forum_id,post_txt) values (?,?,?,?,?,?,?)");
			statement.setInt(1, cp._PostID);
			statement.setString(2, cp._PostOwner);
			statement.setInt(3, cp._PostOwnerID);
			statement.setLong(4, cp._PostDate);
			statement.setInt(5, cp._PostTopicID);
			statement.setInt(6, cp._PostForumID);
			statement.setString(7, cp._PostTxt);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("error while saving new Post to db " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}

	}

	public Post(Topic t)
	{
		_post = new FastList<CPost>();
		load(t);
	}

	public CPost getCPost(int id)
	{
		int i = 0;
		for(CPost cp : _post)
		{
			if(i == id)
				return cp;
			i++;
		}
		return null;
	}

	public void deleteme(Topic t)
	{
		PostBBSManager.getInstance().delPostByTopic(t);
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM posts WHERE post_forum_id=? AND post_topic_id=?");
			statement.setInt(1, t.getForumID());
			statement.setInt(2, t.getID());
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * @param t
	 */
	private void load(Topic t)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM posts WHERE post_forum_id=? AND post_topic_id=? ORDER BY post_id ASC");
			statement.setInt(1, t.getForumID());
			statement.setInt(2, t.getID());
			rset = statement.executeQuery();
			while(rset.next())
			{
				CPost cp = new CPost();
				cp._PostID = Integer.parseInt(rset.getString("post_id"));
				cp._PostOwner = rset.getString("post_owner_name");
				cp._PostOwnerID = Integer.parseInt(rset.getString("post_ownerid"));
				cp._PostDate = Long.parseLong(rset.getString("post_date"));
				cp._PostTopicID = Integer.parseInt(rset.getString("post_topic_id"));
				cp._PostForumID = Integer.parseInt(rset.getString("post_forum_id"));
				cp._PostTxt = rset.getString("post_txt");
				_post.add(cp);
			}
		}
		catch(Exception e)
		{
			_log.warning("data error on Post " + t.getForumID() + "/" + t.getID() + " : " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	/**
	 * @param i
	 */
	public void updatetxt(int i)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			CPost cp = getCPost(i);
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE posts SET post_txt=? WHERE post_id=? AND post_topic_id=? AND post_forum_id=?");
			statement.setString(1, cp._PostTxt);
			statement.setInt(2, cp._PostID);
			statement.setInt(3, cp._PostTopicID);
			statement.setInt(4, cp._PostForumID);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("error while saving new Post to db " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}

	}
	/**
	 *
	 */

}
