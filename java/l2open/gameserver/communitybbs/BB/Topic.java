package l2open.gameserver.communitybbs.BB;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.communitybbs.Manager.TopicBBSManager;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.logging.Logger;

public class Topic
{

	private static Logger _log = Logger.getLogger(Topic.class.getName());
	public static final int MORMAL = 0;
	public static final int MEMO = 1;

	private int _ID;
	private int _ForumID;
	private String _TopicName;
	private long _date;
	private String _OwnerName;
	private int _OwnerID;
	private int _type;
	private int _Creply;

	/**
	 * @param restaure
	 * @param i
	 * @param j
	 * @param string
	 * @param k
	 * @param string2
	 * @param l
	 * @param m
	 * @param n
	 */
	public Topic(ConstructorType ct, int id, int fid, String name, long date, String oname, int oid, int type, int Creply)
	{
		_ID = id;
		_ForumID = fid;
		_TopicName = name;
		_date = date;
		_OwnerName = oname;
		_OwnerID = oid;
		_type = type;
		_Creply = Creply;
		TopicBBSManager.getInstance().addTopic(this);

		if(ct == ConstructorType.CREATE)
			insertindb();
	}

	/**
	 *
	 */
	public void insertindb()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO topic (topic_id,topic_forum_id,topic_name,topic_date,topic_ownername,topic_ownerid,topic_type,topic_reply) values (?,?,?,?,?,?,?,?)");
			statement.setInt(1, _ID);
			statement.setInt(2, _ForumID);
			statement.setString(3, _TopicName);
			statement.setLong(4, _date);
			statement.setString(5, _OwnerName);
			statement.setInt(6, _OwnerID);
			statement.setInt(7, _type);
			statement.setInt(8, _Creply);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("error while saving new Topic to db " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}

	}

	public enum ConstructorType
	{
		RESTORE,
		CREATE
	}

	/**
	 * @return
	 */
	public int getID()
	{
		return _ID;
	}

	public int getForumID()
	{
		return _ForumID;
	}

	/**
	 * @return
	 */
	public String getName()
	{
		return StringEscapeUtils.escapeHtml4(_TopicName).replace("\n", "<br1>");
	}

	public String getOwnerName()
	{
		return _OwnerName;
	}

	public void deleteme(Forum f)
	{
		TopicBBSManager.getInstance().delTopic(this);
		f.RmTopicByID(getID());
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM topic WHERE topic_id=? AND topic_forum_id=?");
			statement.setInt(1, getID());
			statement.setInt(2, f.getID());
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
	 * @return
	 */
	public long getDate()
	{
		return _date;
	}
}
