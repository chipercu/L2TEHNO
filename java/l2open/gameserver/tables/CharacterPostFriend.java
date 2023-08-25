package l2open.gameserver.tables;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import javolution.util.FastMap;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;

public class CharacterPostFriend
{
	private static final Logger _log = Logger.getLogger(CharacterPostFriend.class.getName());
	private static final CharacterPostFriend _instance = new CharacterPostFriend();
	private static final String SELECT_SQL_QUERY = "SELECT pf.post_friend, c.char_name FROM character_post_friends pf LEFT JOIN characters c ON pf.post_friend = c.obj_Id WHERE pf.object_id = ?";
	private static final String INSERT_SQL_QUERY = "INSERT INTO character_post_friends(object_id, post_friend) VALUES (?,?)";
	private static final String DELETE_SQL_QUERY = "DELETE FROM character_post_friends WHERE object_id=? AND post_friend=?";

	public static CharacterPostFriend getInstance()
	{
		return _instance;
	}

	public IntObjectMap<String> select(int objectId)
	{
		IntObjectMap<String> set = new CHashIntObjectMap<String>();
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			statement.setInt(1, objectId);
			rset = statement.executeQuery();
			while (rset.next())
				set.put(rset.getInt(1), rset.getString(2));
		}
		catch (Exception e)
		{
			//_log.info("CharacterPostFriend.load(L2Player): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return set;
	}

	public void insert(L2Player player, int val)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_SQL_QUERY);
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, val);
			statement.execute();
		}
		catch (Exception e)
		{
			_log.info("CharacterPostFriend.insert(L2Player, int): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void delete(L2Player player, int val)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL_QUERY);
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, val);
			statement.execute();
		}
		catch (Exception e)
		{
			_log.info("CharacterPostFriend.delete(L2Player, int): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}
}