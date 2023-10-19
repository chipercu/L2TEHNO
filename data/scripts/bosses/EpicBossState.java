package bosses;

import java.sql.ResultSet;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.FiltredStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EpicBossState
{
	public enum State
	{
		NOTSPAWN,
		ALIVE,
		DEAD,
		INTERVAL
	}

	private int _bossId;
	private long _respawnDate;
	private State _state;

	private static final Log _log = LogFactory.getLog(EpicBossState.class.getName());

	public int getBossId()
	{
		return _bossId;
	}

	public void setBossId(int newId)
	{
		_bossId = newId;
	}

	public State getState()
	{
		return _state;
	}

	public void setState(State newState)
	{
		_state = newState;
	}

	public long getRespawnDate()
	{
		return _respawnDate;
	}

	public void setRespawnDate(long interval)
	{
		_respawnDate = interval + System.currentTimeMillis();
	}

	public void setRespawnDate2(long interval)
	{
		_respawnDate = interval;
	}

	public EpicBossState(int bossId)
	{
		this(bossId, true);
	}

	public EpicBossState(int bossId, boolean isDoLoad)
	{
		_bossId = bossId;
		if(isDoLoad)
			load();
	}

	public void load()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("SELECT * FROM epic_boss_spawn WHERE bossId = ? LIMIT 1");
			statement.setInt(1, _bossId);
			rset = statement.executeQuery();

			if(rset.next())
			{
				_respawnDate = rset.getLong("respawnDate") * 1000L;

				if(_respawnDate - System.currentTimeMillis() <= 0)
					_state = State.NOTSPAWN;
				else
				{
					int tempState = rset.getInt("state");
					if(tempState == State.NOTSPAWN.ordinal())
						_state = State.NOTSPAWN;
					else if(tempState == State.INTERVAL.ordinal())
						_state = State.INTERVAL;
					else if(tempState == State.ALIVE.ordinal())
						_state = State.ALIVE;
					else if(tempState == State.DEAD.ordinal())
						_state = State.DEAD;
					else
						_state = State.NOTSPAWN;
				}
			}
		}
		catch(Exception e)
		{
			_log.error(e.getMessage(), e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void save()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO epic_boss_spawn (bossId,respawnDate,state) VALUES(?,?,?)");
			statement.setInt(1, _bossId);
			statement.setInt(2, (int) (_respawnDate / 1000));
			statement.setInt(3, _state.ordinal());
			statement.execute();
			statement.close();
		}
		catch(Exception e)
		{
			_log.error(e.getMessage(), e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void update()
	{
		ThreadConnection con = null;
		FiltredStatement statement = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			statement.executeUpdate("UPDATE epic_boss_spawn SET respawnDate=" + _respawnDate / 1000 + ", state=" + _state.ordinal() + " WHERE bossId=" + _bossId);
			_log.info("update EpicBossState: ID:" + _bossId + ", RespawnDate:" + _respawnDate / 1000 + ", State:" + _state.toString());
		}
		catch(Exception e)
		{
			_log.warn("Exeption on update EpicBossState: ID " + _bossId + ", RespawnDate:" + _respawnDate / 1000 + ", State:" + _state.toString(), e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void setNextRespawnDate(long newRespawnDate)
	{
		_respawnDate = newRespawnDate;
	}

	public long getInterval()
	{
		long interval = _respawnDate - System.currentTimeMillis();
		return interval > 0 ? interval : 0;
	}
}