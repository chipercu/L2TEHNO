package l2open.loginserver.gameservercon.gspackets;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.loginserver.gameservercon.AttGS;

import java.util.logging.Logger;

/**
 * @Author: SYS
 * @Date: 10/4/2007
 */
public class LockAccountIP extends ClientBasePacket
{
	private static final Logger _log = Logger.getLogger(LockAccountIP.class.getName());

	public LockAccountIP(byte[] decrypt, AttGS gameserver)
	{
		super(decrypt, gameserver);
	}

	@Override
	public void read()
	{
		String accname = readS();
		String IP = readS();
		int time = readD();

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE accounts SET AllowIPs = ?, lock_expire = ? WHERE login = ?");
			statement.setString(1, IP);
			statement.setInt(2, time);
			statement.setString(3, accname);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);
		}
		catch(Exception e)
		{
			_log.severe("Failed to lock/unlock account: " + e.getMessage());
		}
		finally
		{
			DatabaseUtils.closeConnection(con);
		}
	}
}