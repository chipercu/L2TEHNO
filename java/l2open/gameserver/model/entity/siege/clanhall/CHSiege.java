package l2open.gameserver.model.entity.siege.clanhall;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.logging.Logger;

public abstract class CHSiege
{
	private static final Logger _log = Logger.getLogger(CHSiege.class.getName());
	private Calendar _siegeDate;
	public Calendar _siegeEndDate;
	private boolean _isInProgress;

	public CHSiege()
	{
		_isInProgress = false;
	}

	public long restoreSiegeDate(int ClanHallId)
	{
		long res = 0;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT siegeDate FROM clanhall WHERE id=?");
			statement.setInt(1, ClanHallId);
			rs = statement.executeQuery();

			if(rs.next())
				res = rs.getLong("siegeDate") * 1000;

			rs.close();
			statement.close();
		}
		catch(Exception e)
		{
			_log.warning("Exception: can't get clanhall siege date: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
		return res;
	}

	public void setNewSiegeDate(long siegeDate, int ClanHallId, int hour, int day)
	{
		Calendar tmpDate = Calendar.getInstance();
		if(siegeDate > System.currentTimeMillis())
			return;
		tmpDate.setTimeInMillis(System.currentTimeMillis());

		tmpDate.set(7, day);
		tmpDate.add(5, 14);
		tmpDate.set(11, hour);
		tmpDate.set(12, 0);
		tmpDate.set(13, 0);
		setSiegeDate(tmpDate);

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clanhall SET siegeDate=? WHERE id = ?");
			statement.setLong(1, getSiegeDate().getTimeInMillis() / 1000L);
			statement.setInt(2, ClanHallId);
			statement.execute();
			statement.close();
		}
		catch(Exception e)
		{
			_log.warning("Exception: can't save clanhall siege date: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public final Calendar getSiegeDate()
	{
		return _siegeDate;
	}

	public final void setSiegeDate(Calendar par)
	{
		_siegeDate = par;
	}

	public final boolean getIsInProgress()
	{
		return _isInProgress;
	}

	public final void setIsInProgress(boolean par)
	{
		_isInProgress = par;
	}
}
