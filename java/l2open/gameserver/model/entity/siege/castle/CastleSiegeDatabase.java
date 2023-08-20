package l2open.gameserver.model.entity.siege.castle;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.SiegeDatabase;
import l2open.gameserver.model.entity.residence.Castle;

public class CastleSiegeDatabase extends SiegeDatabase
{
	public CastleSiegeDatabase(Siege siege)
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
			statement = con.prepareStatement("UPDATE castle SET siegeDate = ?, siegeHourOfDay=?, setSiege="+((Castle)_siege.getSiegeUnit())._setNewData+" WHERE id = ?");
			statement.setLong(1, _siege.getSiegeDate().getTimeInMillis() / 1000);
			statement.setInt(2, _siege.getSiegeUnit().getSiegeHourOfDay());
			statement.setInt(3, _siege.getSiegeUnit().getId());
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