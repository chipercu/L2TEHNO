package l2open.gameserver.model.entity.siege.clanhall;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.SiegeDatabase;

public class ClanHallSiegeDatabase extends SiegeDatabase
{
	public ClanHallSiegeDatabase(Siege siege)
	{
		super(siege);
	}

	@Override
	public void saveSiegeDate()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clanhall SET siegeDate = ? WHERE id = ?");
			statement.setLong(1, _siege.getSiegeDate().getTimeInMillis() / 1000);
			statement.setInt(2, _siege.getSiegeUnit().getId());
			statement.execute();
		}
		catch(Exception e)
		{
			System.out.println("Exception: saveSiegeDate(): " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}
}