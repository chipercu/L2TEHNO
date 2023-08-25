package l2open.loginserver.gameservercon.gspackets;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.loginserver.L2LoginClient;
import l2open.loginserver.LoginController;
import l2open.loginserver.gameservercon.AttGS;
import l2open.loginserver.gameservercon.lspackets.ChangePasswordResponse;
import l2open.util.Log;

import java.sql.ResultSet;
import java.util.logging.Logger;

public class ChangePassword extends ClientBasePacket
{
	private static final Logger log = Logger.getLogger(ChangePassword.class.getName());

	public ChangePassword(byte[] decrypt, AttGS gameserver)
	{
		super(decrypt, gameserver);
	}

	@Override
	public void read()
	{
		String accname = readS().trim();
		String oldPass = readS().trim();
		String newPass = readS().trim();
		String hwid = readS().trim();

		String dbPassword = null;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			try
			{
				statement = con.prepareStatement("SELECT * FROM accounts WHERE login = ?");
				statement.setString(1, accname);
				rs = statement.executeQuery();
				if(rs.next())
					dbPassword = rs.getString("password");
			}
			catch(Exception e)
			{
				log.warning("Can't recive old password for account " + accname + ", exciption :" + e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseSR(statement, rs);
			}

			//Encode old password and compare it to sended one, send packet to determine changed or not.
			try
			{
				if(!LoginController.DEFAULT_CRYPT.compare(oldPass, dbPassword))
				{
					ChangePasswordResponse cp1;
					cp1 = new ChangePasswordResponse(accname, false);
					sendPacket(cp1);
				}
				else
				{
					statement = con.prepareStatement("UPDATE accounts SET password = ? WHERE login = ?");
					statement.setString(1, LoginController.DEFAULT_CRYPT.encrypt(newPass));
					statement.setString(2, accname);
					int result = statement.executeUpdate();
					L2LoginClient client = LoginController.getInstance().getAuthedClient(accname);
					if(result != 0)
						Log.add("<acc=\"" + accname + "\" old=\"" + oldPass + "\" new=\"" + newPass + "\" ip=\"" + (client != null ? client.getIpAddress() : "0.0.0.0") + "\" hwid=\"" + hwid + "\" />", "passwords");

					ChangePasswordResponse cp1;
					cp1 = new ChangePasswordResponse(accname, result != 0);
					sendPacket(cp1);
				}
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			finally
			{
				DatabaseUtils.closeStatement(statement);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeConnection(con);
		}
	}
}
