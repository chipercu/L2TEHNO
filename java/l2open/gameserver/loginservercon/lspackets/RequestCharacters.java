package l2open.gameserver.loginservercon.lspackets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import l2open.database.FiltredStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.loginservercon.AttLS;
import l2open.gameserver.loginservercon.gspackets.ReplyCharacters;

public class RequestCharacters extends LoginServerBasePacket
{
	private static Logger _log = Logger.getLogger(RequestCharacters.class.getName());
	String account;

	public RequestCharacters(byte[] decrypt, AttLS loginServer)
	{
		super(decrypt, loginServer);
	}

	public void read()
	{
		account = readS();
		sendCharacters();
	}

	private void sendCharacters()
	{
		ThreadConnection con = null;
		FiltredStatement statement = null;
		ResultSet rset = null;
		int chars = 0;
		List<Long> charToDel = new ArrayList<Long>();
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			rset = statement.executeQuery("SELECT deletetime FROM characters WHERE `account_name`='" + account + "'");
			while(rset.next())
			{
				chars++;
				long delTime = rset.getLong("deletetime");
				if(delTime != 0)
					charToDel.add(delTime);
			}
		}
		catch (SQLException e)
		{
			_log.warning("Error select characters count for account: " + account + ". E: " + e.getLocalizedMessage());
		}
		finally
		{
			con.close();
			statement.close();
			try
			{
				rset.close();
			}
			catch(SQLException e)
			{
				_log.warning("Error close conection. E: " + e.getLocalizedMessage());
			}
		}
		getLoginServer().sendPacket(new ReplyCharacters(account, chars, charToDel));
	}
}