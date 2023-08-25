package l2open.gameserver.model.entity.residence;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.FortressManager;
import l2open.gameserver.instancemanager.SiegeGuardManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.siege.SiegeClanType;
import l2open.gameserver.model.entity.siege.fortress.FortressSiege;
import l2open.gameserver.serverpackets.PlaySound;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;

import java.sql.ResultSet;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javolution.util.FastMap;

public class Fortress extends Residence
{
	protected static Logger _log = Logger.getLogger(Fortress.class.getName());

	private FortressSiege _siege = null;
	private int _siegeDate = 0;
	private int _lastSiegeDate = 0;
	private int _state = 0;
	private int _castleId = 0;
	private int _fortType = 0;
	public Location mercenaryLoc = null;
	public int mercenaryId = 0;

	public int fortress_scale = 0;
	public int[][] fortress_flag = new int[3][3]; //[fort_flag];{{111486;-14852;-832};{111368;-14851;-832};{111257;-14852;-832}}


	public Fortress(int fortressId)
	{
		super(fortressId);
	}

	@Override
	public FortressSiege getSiege()
	{
		if(_siege == null)
			_siege = new FortressSiege(this);
		return _siege;
	}

	/**
	 * Возращает дату предстоящей осады
	 * @return дата осады в unixtime
	 */
	public int getSiegeDate()
	{
		return _siegeDate;
	}
	
	public void setSiegeDate(int time)
	{
		_siegeDate = time;
	}

	@Override
	public int getLastSiegeDate()
	{
		return _lastSiegeDate;
	}

	@Override
	public void setLastSiegeDate(int time)
	{
		_lastSiegeDate = time;
	}

	/**
	 * @return Returns fortress type.<BR><BR>
	 * 0 - small (3 commanders) <BR>
	 * 1 - big (4 commanders + control room)
	 */
	public int getFortType()
	{
		return _fortType;
	}

	public void setFortType(int type)
	{
		_fortType = type;
	}

	/**
	 * @return Returns amount of barracks.
	 */
	public int getFortSize()
	{
		return getFortType() == 0 ? 3 : 5;
	}

	@Override
	public int getSiegeDayOfWeek()
	{
		return -1;
	}

	@Override
	public int getSiegeHourOfDay()
	{
		return -1;
	}

	@Override
	public void changeOwner(L2Clan clan)
	{
		// Если клан уже владел каким-либо замком/крепостью, отбираем его.
		if(clan != null)
		{
			if(clan.getHasFortress() != 0)
			{
				Fortress oldFortress = FortressManager.getInstance().getFortressByIndex(clan.getHasFortress());
				if(oldFortress != null)
					oldFortress.changeOwner(null);
			}
			if(clan.getHasCastle() != 0)
			{
				//Если у клана есть замок и он захватил форт, то форт переходить нпсам.
				clan = null;
			}
		}

		// Если этой крепостью уже кто-то владел, отбираем у него крепость
		if(getOwnerId() > 0 && (clan == null || clan.getClanId() != getOwnerId()))
		{
			// Удаляем фортовые скилы у старого владельца
			removeSkills();
			L2Clan oldOwner = getOwner();
			if(oldOwner != null)
				oldOwner.setHasFortress(0);
		}

		// Выдаем крепость новому владельцу
		if(clan != null)
			clan.setHasFortress(getId());
		
		// Удаляем регистрации на осаду
		if(clan == null) // Только в этом случае, иначе при Engrave удалятся тоже
		{
			getSiege().getDatabase().clearSiegeClan(SiegeClanType.DEFENDER);
			getSiege().getDatabase().clearSiegeClan(SiegeClanType.DEFENDER_WAITING);
			getSiege().getDatabase().clearSiegeClan(SiegeClanType.DEFENDER_REFUSED);
		}

		// Сохраняем в базу
		updateOwnerInDB(clan);

		// Выдаем фортовые скилы новому владельцу
		rewardSkills();

		// Удаляем наемных гвардов
		SiegeGuardManager.removeMercsFromDb(_id);

		// Удаляем все апгрейды крепости
		removeUpgrade();

		// Полупобеда, если идет осада
		if(clan != null && getSiege().isInProgress())
			getSiege().midVictory();

		setFortState(0, 0);
	}

	@Override
	protected void loadData()
	{
		_type = ResidenceType.Fortress;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("SELECT * FROM `forts` WHERE `id` = ?");
			statement.setInt(1, _id);
			rs = statement.executeQuery();

			while(rs.next())
			{
				_name = rs.getString("name");
				_siegeDate = rs.getInt("siegeDate");
				_lastSiegeDate = rs.getInt("lastSiegeDate");
				_state = rs.getInt("state");
				if(_state == 0)
					_state = 1;
				_castleId = rs.getInt("castleId");

				setOwnDate(rs.getInt("ownDate"));

				mercenaryLoc = new Location(rs.getString("mercenaryLoc"));
				mercenaryId = rs.getInt("mercenaryId");

				StringTokenizer st = new StringTokenizer(rs.getString("skills"), ";");
				while(st.hasMoreTokens())
				{
					L2Skill skill = SkillTable.getInstance().getInfo(Integer.valueOf(st.nextToken()), Integer.valueOf(st.nextToken()));
					if(skill != null)
						_skills.add(skill);
				}
			}
			DatabaseUtils.closeDatabaseSR(statement, rs);

			statement = con.prepareStatement("SELECT `clan_id` FROM `clan_data` WHERE hasFortress = ?");
			statement.setInt(1, _id);
			rs = statement.executeQuery();

			while(rs.next())
				_ownerId = rs.getInt("clan_id");

			_zone = ZoneManager.getInstance().getZoneByIndex(ZoneType.Fortress, getId(), true);
		}
		catch(Exception e)
		{
			_log.warning("Exception: Fortress.load(): " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}

	private void updateOwnerInDB(L2Clan clan)
	{
		_ownerId = clan == null ? 0 : clan.getClanId(); // Update owner id property

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_data SET hasFortress=0 WHERE hasFortress=? LIMIT 1");
			statement.setInt(1, getId());
			statement.execute();
			DatabaseUtils.closeStatement(statement);
			statement = null;

			if(clan != null)
			{
				statement = con.prepareStatement("UPDATE clan_data SET hasFortress=? WHERE clan_id=? LIMIT 1");
				statement.setInt(1, getId());
				statement.setInt(2, getOwnerId());
				statement.execute();

				clan.broadcastClanStatus(false, true, true);
				clan.broadcastToOnlineMembers(new PlaySound("Siege_Victory"));
			}
		}
		catch(Exception e)
		{
			_log.warning("Exception: updateOwnerInDB(L2Clan clan): " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	@Override
	public void saveOwnDate()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE forts SET ownDate = ? WHERE id = ?");
			statement.setInt(1, getOwnerId() != 0 ? getOwnDate() : 0);
			statement.setInt(2, getId());
			statement.execute();
		}
		catch(Exception e)
		{
			System.out.println("Exception: saveOwnDate(): " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * State:<BR>
	 * 0 - не определен<BR>
	 * 1 - независимый<BR>
	 * 2 - контракт с замком<BR>
	 * CastleId: замок, с которым заключаем контракт
	 */
	public void setFortState(int state, int castleId)
	{
		_state = state;
		_castleId = castleId;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE forts SET state = ?, castleId = ? WHERE id = ?");
			statement.setInt(1, getFortState());
			statement.setInt(2, getCastleId());
			statement.setInt(3, getId());
			statement.execute();
			statement.close();

		}
		catch(Exception e)
		{
			System.out.println("Exception: setFortState(int state, int castleId): " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public int getCastleId()
	{
		return _castleId;
	}

	/**
	 * State:<BR>
	 * 0 - не определен<BR>
	 * 1 - независимый<BR>
	 * 2 - контракт с замком
	 */
	public int getFortState()
	{
		return _state;
	}

	public FortStatus _fortStatus;

	public void notificationEventEx(int id, int event_id)
	{

	}

	public boolean _fortDoorDestroyed = false;
	public void setFortStatus(FortStatus fortStatus)
	{
		if(fortStatus == FortStatus.ON_FORTRESS_DOOR_BREAK && _fortDoorDestroyed)
			return;
		else if(fortStatus == _fortStatus)
			return;

		_fortStatus = fortStatus;
		notificationEventEx(getId(), fortStatus.ordinal());

		if(_fortStatus == FortStatus.ON_FORTRESS_DOOR_BREAK)
			_fortDoorDestroyed = true;
		else if(_fortStatus == FortStatus.ON_FORTRESS_START_SIEGE)
		{
			_fortStatus = FortStatus.ON_FORTRESS_START_BARRACK_CAPTURE;
			notificationEventEx(getId(), _fortStatus.ordinal());
		}
	}

	/**
	 * [FORTRESS_GUARD_REINFORCEMENT] = 0 // level 1/2 [FORTRESS_GUARD_POWER_UP] = 1 // level 1/2
	 * [FORTRESS_DOOR_POWER_UP] = 2 // level 1 [FORTRESS_PHOTOCANNON] = 3 // level 1 [FORTRESS_SCOUT] = 4 // level 1
	 */
	private Map<Integer, Integer> _facility = new FastMap<Integer, Integer>();
	public int getFacilityLevel(int type)
	{
		if(_facility.containsKey(type))	
			return _facility.get(type);
		return -1;
	}
}