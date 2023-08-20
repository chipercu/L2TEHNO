package l2open.gameserver.model.clan_find;

import java.sql.ResultSet;
import java.util.logging.Logger;

import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Sdw
 */
public class ClanEntryManager
{
	protected static final Logger _log = Logger.getLogger(ClanEntryManager.class.getName());

	private static final Map<Integer, PledgeWaitingInfo> _waitingList = new ConcurrentHashMap<>();
	private static final Map<Integer, PledgeRecruitInfo> _clanList = new ConcurrentHashMap<>();
	private static final Map<Integer, Map<Integer, PledgeApplicantInfo>> _applicantList = new ConcurrentHashMap<>();
	
	private static final Map<Integer, ScheduledFuture<?>> _clanLocked = new ConcurrentHashMap<>();
	private static final Map<Integer, ScheduledFuture<?>> _playerLocked = new ConcurrentHashMap<>();
	
	private static final String INSERT_APPLICANT = "INSERT INTO pledge_applicant VALUES (?, ?, ?, ?)";
	private static final String DELETE_APPLICANT = "DELETE FROM pledge_applicant WHERE charId = ? AND clanId = ?";
	
	private static final String INSERT_WAITING_LIST = "INSERT INTO pledge_waiting_list VALUES (?, ?)";
	private static final String DELETE_WAITING_LIST = "DELETE FROM pledge_waiting_list WHERE char_id = ?";
	
	private static final String INSERT_CLAN_RECRUIT = "INSERT INTO pledge_recruit VALUES (?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_CLAN_RECRUIT = "UPDATE pledge_recruit SET karma = ?, information = ?, detailed_information = ?, application_type = ?, recruit_type = ? WHERE clan_id = ?";
	private static final String DELETE_CLAN_RECRUIT = "DELETE FROM pledge_recruit WHERE clan_id = ?";
	
	//@formatter:off
/*	private static final List<Comparator<PledgeWaitingInfo>> PLAYER_COMPARATOR = Arrays.asList(
		null,
		Comparator.comparing(PledgeWaitingInfo::getPlayerName), 
		Comparator.comparingInt(PledgeWaitingInfo::getKarma), 
		Comparator.comparingInt(PledgeWaitingInfo::getPlayerLvl), 
		Comparator.comparingInt(PledgeWaitingInfo::getPlayerClassId));
	//@formatter:on
	
	//@formatter:off
	private static final List<Comparator<PledgeRecruitInfo>> CLAN_COMPARATOR = Arrays.asList(
		null,
		Comparator.comparing(PledgeRecruitInfo::getClanName),
		Comparator.comparing(PledgeRecruitInfo::getClanLeaderName),
		Comparator.comparingInt(PledgeRecruitInfo::getClanLevel),
		Comparator.comparingInt(PledgeRecruitInfo::getKarma));*/
	//@formatter:on
	
	private static final long LOCK_TIME = TimeUnit.MINUTES.toMillis(5);
	
	protected ClanEntryManager()
	{
		load();
	}
	
	private void load()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM pledge_recruit");
			rs = statement.executeQuery();

			while (rs.next())
				_clanList.put(rs.getInt("clan_id"), new PledgeRecruitInfo(rs.getInt("clan_id"), rs.getInt("karma"), rs.getString("information"), rs.getString("detailed_information"), rs.getInt("application_type"), rs.getInt("recruit_type")));
			_log.info("Loaded: "+_clanList.size()+" clan entry.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT a.char_id, a.karma, cs.class_id, cs.level, b.char_name FROM pledge_waiting_list as a LEFT JOIN characters as b ON a.char_id = b.obj_Id LEFT JOIN character_subclasses as cs ON (a.char_id=cs.char_obj_id AND cs.isBase='1')");
			rs = statement.executeQuery();

			while (rs.next())
				_waitingList.put(rs.getInt("char_id"), new PledgeWaitingInfo(rs.getInt("char_id"), rs.getInt("level"), rs.getInt("karma"), rs.getInt("class_id"), rs.getString("char_name")));

			_log.info("Loaded: "+_waitingList.size()+" player in waiting list.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT a.charId, a.clanId, a.karma, a.message, cs.class_id, cs.level, b.char_name FROM pledge_applicant as a LEFT JOIN characters as b ON a.charId = b.obj_Id LEFT JOIN character_subclasses as cs ON (a.charId=cs.char_obj_id AND cs.isBase='1')");
			
			rs = statement.executeQuery();

			while(rs.next())
			{
				int clanId = rs.getInt("clanId");
				
				Map<Integer, PledgeApplicantInfo> _pl_list;
				if(_applicantList.containsKey(clanId))
					_pl_list = _applicantList.get(clanId);
				else
					_pl_list = new ConcurrentHashMap<Integer, PledgeApplicantInfo>();

				_pl_list.put(rs.getInt("charId"), new PledgeApplicantInfo(rs.getInt("charId"), rs.getString("char_name"), rs.getInt("level"), rs.getInt("karma"), rs.getInt("clanId"), rs.getString("message")));
				_applicantList.put(clanId, _pl_list);
			}

			_log.info("Loaded: "+_applicantList.size()+" player application.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}
	
	public Map<Integer, PledgeWaitingInfo> getWaitingList()
	{
		return _waitingList;
	}
	
	public Map<Integer, PledgeRecruitInfo> getClanList()
	{
		return _clanList;
	}
	
	public Map<Integer, Map<Integer, PledgeApplicantInfo>> getApplicantList()
	{
		return _applicantList;
	}

	public Map<Integer, PledgeApplicantInfo> getApplicantListForClan(int clanId)
	{
		if(_applicantList.containsKey(clanId))
			return _applicantList.get(clanId);
		return Collections.emptyMap();
	}

	public PledgeApplicantInfo getPlayerApplication(int clanId, int playerId)
	{
		if(_applicantList.containsKey(clanId))
			return _applicantList.get(clanId).get(playerId);
		return null;
	}

	public boolean removePlayerApplication(int clanId, int playerId)
	{
		final Map<Integer, PledgeApplicantInfo> clanApplicantList = _applicantList.get(clanId);

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_APPLICANT);
			statement.setInt(1, playerId);
			statement.setInt(2, clanId);
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
		return (clanApplicantList != null) && (clanApplicantList.remove(playerId) != null);
	}
	
	public boolean addPlayerApplicationToClan(int clanId, PledgeApplicantInfo info)
	{
		if (!_playerLocked.containsKey(info.getPlayerId()))
		{
			Map<Integer, PledgeApplicantInfo> _pl_list;
			if(_applicantList.containsKey(clanId))
				_pl_list = _applicantList.get(clanId);
			else
				_pl_list = new ConcurrentHashMap<Integer, PledgeApplicantInfo>();

			_pl_list.put(info.getPlayerId(), info);
			_applicantList.put(clanId, _pl_list);

			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;

			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(INSERT_APPLICANT);
				statement.setInt(1, info.getPlayerId());
				statement.setInt(2, info.getRequestClanId());
				statement.setInt(3, info.getKarma());
				statement.setString(4, info.getMessage());
				statement.executeUpdate();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}
			return true;
		}
		return false;
	}
	
	public int getClanIdForPlayerApplication(int playerId)
	{
		for(Map.Entry<Integer,Map<Integer,PledgeApplicantInfo>> e : _applicantList.entrySet())
			if(e.getValue().containsKey(playerId))
				return e.getKey();
		//return _applicantList.entrySet().stream().filter(e -> e.getValue().containsKey(playerId)).mapToInt(e -> e.getKey()).findFirst();
		return 0;
	}
	
	public boolean addToWaitingList(int playerId, PledgeWaitingInfo info)
	{
		if (!_playerLocked.containsKey(playerId))
		{
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;

			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(INSERT_WAITING_LIST);
				statement.setInt(1, info.getPlayerId());
				statement.setInt(2, info.getKarma());
				statement.executeUpdate();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}

			_waitingList.put(playerId, info);
			return true;
		}
		return false;
	}
	
	public boolean removeFromWaitingList(int playerId)
	{
		if (_waitingList.containsKey(playerId))
		{
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;

			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(DELETE_WAITING_LIST);
				statement.setInt(1, playerId);
				statement.executeUpdate();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}

			_waitingList.remove(playerId);
			lockPlayer(playerId);
			return true;
		}
		return false;
	}
	
	public boolean addToClanList(int clanId, PledgeRecruitInfo info)
	{
		if (!_clanList.containsKey(clanId) && !_clanLocked.containsKey(clanId))
		{
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;

			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(INSERT_CLAN_RECRUIT);
				statement.setInt(1, info.getClanId());
				statement.setInt(2, info.getKarma());
				statement.setString(3, info.getInformation());
				statement.setString(4, info.getDetailedInformation());
				statement.setInt(5, info.getApplicationType());
				statement.setInt(6, info.getRecruitType());
				statement.executeUpdate();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}
			
			_clanList.put(clanId, info);
			return true;
		}
		return false;
	}
	
	public boolean updateClanList(int clanId, PledgeRecruitInfo info)
	{
		if (_clanList.containsKey(clanId) && !_clanLocked.containsKey(clanId))
		{
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;

			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(UPDATE_CLAN_RECRUIT);
				statement.setInt(1, info.getKarma());
				statement.setString(2, info.getInformation());
				statement.setString(3, info.getDetailedInformation());
				statement.setInt(4, info.getApplicationType());
				statement.setInt(5, info.getRecruitType());
				statement.setInt(6, info.getClanId());
				statement.executeUpdate();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}

			if(_clanList.get(clanId) != null || _clanList.containsKey(clanId))
			{
				_clanList.put(clanId, info);
				return true;
			}
		}
		return false;
	}
	
	public boolean removeFromClanList(int clanId)
	{
		if (_clanList.containsKey(clanId))
		{
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;

			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(DELETE_CLAN_RECRUIT);
				statement.setInt(1, clanId);
				statement.executeUpdate();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}

			_clanList.remove(clanId);
			lockClan(clanId);
			return true;
		}
		return false;
	}
	
	public List<PledgeWaitingInfo> getSortedWaitingList(int levelMin, int levelMax, int role, int sortBy, boolean descending)
	{
		List<PledgeWaitingInfo> result = new ArrayList<>();
		for(PledgeWaitingInfo p : _waitingList.values())
			if(p.getPlayerLvl() >= levelMin && p.getPlayerLvl() <= levelMax)
				result.add(p);
		// TODO: сортировка...
		return result;
		//return _waitingList.values().stream().collect(Collectors.toList());
		/*sortBy = constrain(sortBy, 1, PLAYER_COMPARATOR.size() - 1);
		
		// TODO: Handle Role
		//@formatter:off
		return _waitingList.values().stream()
		      .filter(p -> ((p.getPlayerLvl() >= levelMin) && (p.getPlayerLvl() <= levelMax)))
		      .sorted(descending ? PLAYER_COMPARATOR.get(sortBy).reversed() : PLAYER_COMPARATOR.get(sortBy))
		      .collect(Collectors.toList());
		//@formatter:on*/
	}
	
	public List<PledgeWaitingInfo> queryWaitingListByName(String name)
	{
		List<PledgeWaitingInfo> result = new ArrayList<>();
		for(PledgeWaitingInfo p : _waitingList.values())
			if(p.getPlayerName().toLowerCase().contains(name))
				result.add(p);
		return result;
		//return _waitingList.values().stream().filter(p -> p.getPlayerName().toLowerCase().contains(name)).collect(Collectors.toList());
	}
	
	public List<PledgeRecruitInfo> getSortedClanListByName(String query, int type)
	{
		List<PledgeRecruitInfo> result = new ArrayList<>();
		for(PledgeRecruitInfo p : _clanList.values())
			if(type == 1)
			{
				if(p.getClanName().toLowerCase().contains(query))
					result.add(p);
			}
			else
			{
				if(p.getClanLeaderName().toLowerCase().contains(query))
					result.add(p);
			}
		return result;
	}
	
	public PledgeRecruitInfo getClanById(int clanId)
	{
		return _clanList.get(clanId);
	}
	
	public boolean isClanRegistred(int clanId)
	{
		return _clanList.get(clanId) != null;
	}
	
	public boolean isPlayerRegistred(int playerId)
	{
		return _waitingList.get(playerId) != null;
	}
	
	public List<PledgeRecruitInfo> getUnSortedClanList()
	{
		return new ArrayList<>(_clanList.values())/*.stream().collect(Collectors.toList())*/;
	}
	
	public List<PledgeRecruitInfo> getSortedClanList(int clanLevel, int karma, int sortBy, boolean descending)
	{
		List<PledgeRecruitInfo> result = new ArrayList<>();
		for(PledgeRecruitInfo p : _clanList.values())
			if(((clanLevel < 0) && (karma >= 0) && (karma != p.getKarma())) || ((clanLevel >= 0) && (karma < 0) && (clanLevel != p.getClanLevel())) || ((clanLevel >= 0) && (karma >= 0) && ((clanLevel != p.getClanLevel()) || (karma != p.getKarma()))))
				result.add(p);
		return result;
		// TODO: сортировка...
		/*sortBy = constrain(sortBy, 1, CLAN_COMPARATOR.size() - 1);
		//@formatter:off
		return _clanList.values().stream()
		      .filter((p -> (((clanLevel < 0) && (karma >= 0) && (karma != p.getKarma())) || ((clanLevel >= 0) && (karma < 0) && (clanLevel != p.getClanLevel())) || ((clanLevel >= 0) && (karma >= 0) && ((clanLevel != p.getClanLevel()) || (karma != p.getKarma()))))))
		      .sorted(descending ? CLAN_COMPARATOR.get(sortBy).reversed() : CLAN_COMPARATOR.get(sortBy))
		      .collect(Collectors.toList());
		//@formatter:on*/
	}
	
	public long getPlayerLockTime(int playerId)
	{
		return _playerLocked.get(playerId) == null ? 0 : _playerLocked.get(playerId).getDelay(TimeUnit.MINUTES);
	}
	
	public long getClanLockTime(int playerId)
	{
		return _clanLocked.get(playerId) == null ? 0 : _clanLocked.get(playerId).getDelay(TimeUnit.MINUTES);
	}
	
	private static void lockPlayer(int playerId)
	{
		_playerLocked.put(playerId, ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
		{
			public void runImpl()
			{
				_playerLocked.remove(playerId);
			}
		}, LOCK_TIME));
	}
	
	private static void lockClan(int clanId)
	{
		_clanLocked.put(clanId, ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
		{
			public void runImpl()
			{
				_clanLocked.remove(clanId);
			}
		}, LOCK_TIME));
	}
	
	public static ClanEntryManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanEntryManager _instance = new ClanEntryManager();
	}

	public static int constrain(int input, int min, int max)
	{
		return (input < min) ? min : (input > max) ? max : input;
	}
}
