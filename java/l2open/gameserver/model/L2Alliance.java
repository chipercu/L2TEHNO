package l2open.gameserver.model;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.cache.CrestCache;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.tables.ClanTable;
import l2open.util.GArray;

import java.sql.ResultSet;
import java.util.Map;
import java.util.logging.Logger;

public class L2Alliance
{
	private static final Logger _log = Logger.getLogger(L2Alliance.class.getName());

	private String _allyName;
	private int _allyId;
	private L2Clan _leader = null;
	private Map<Integer, L2Clan> _members = new FastMap<Integer, L2Clan>().setShared(true);

	private int _allyCrestId;

	private long _expelledMemberTime;

	public L2Alliance(int allyId)
	{
		_allyId = allyId;
		restore();
	}

	public L2Alliance(int allyId, String allyName, L2Clan leader)
	{
		_allyId = allyId;
		_allyName = allyName;
		setLeader(leader);
	}

	public int getLeaderId()
	{
		return _leader != null ? _leader.getClanId() : 0;
	}

	public L2Clan getLeader()
	{
		return _leader;
	}

	public void setLeader(L2Clan leader)
	{
		_leader = leader;
		_members.put(leader.getClanId(), leader);
	}

	public String getAllyLeaderName()
	{
		return _leader != null ? _leader.getLeaderName() : "";
	}

	public void addAllyMember(L2Clan member, boolean storeInDb)
	{
		_members.put(member.getClanId(), member);

		if(storeInDb)
			storeNewMemberInDatabase(member);
	}

	public L2Clan getAllyMember(int id)
	{
		return _members.get(id);
	}

	public void removeAllyMember(int id)
	{
		if(_leader != null && _leader.getClanId() == id)
			return;
		L2Clan exMember = _members.remove(id);
		if(exMember == null)
		{
			_log.warning("Clan " + id + " not found in alliance while trying to remove");
			return;
		}
		removeMemberInDatabase(exMember);
	}

	public L2Clan[] getMembers()
	{
		return _members.values().toArray(new L2Clan[_members.size()]);
	}

	public int getMembersCount()
	{
		return _members.size();
	}

	public int getAllyId()
	{
		return _allyId;
	}

	public String getAllyName()
	{
		return _allyName;
	}

	public void setAllyCrestId(int allyCrestId)
	{
		_allyCrestId = allyCrestId;
	}

	public int getAllyCrestId()
	{
		return _allyCrestId;
	}

	public void setAllyId(int allyId)
	{
		_allyId = allyId;
	}

	public void setAllyName(String allyName)
	{
		_allyName = allyName;
	}

	public boolean isMember(int id)
	{
		return _members.containsKey(id);
	}

	public void setExpelledMemberTime(long time)
	{
		_expelledMemberTime = time;
	}

	public long getExpelledMemberTime()
	{
		return _expelledMemberTime;
	}

	public void setExpelledMember()
	{
		_expelledMemberTime = System.currentTimeMillis();
		updateAllyInDB();
	}

	public boolean canInvite()
	{
		return System.currentTimeMillis() - _expelledMemberTime >= ConfigValue.EXPELLED_MEMBER_ALY_PENALTY * 1000; // 24 * 60 * 60 * 1000L;
	}

	public void updateAllyInDB()
	{
		if(getLeaderId() == 0)
		{
			_log.warning("updateAllyInDB with empty LeaderId");
			Thread.dumpStack();
			return;
		}

		if(getAllyId() == 0)
		{
			_log.warning("updateAllyInDB with empty AllyId");
			Thread.dumpStack();
			return;
		}

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE ally_data SET leader_id=?,expelled_member=? WHERE ally_id=?");
			statement.setInt(1, getLeaderId());
			statement.setLong(2, getExpelledMemberTime() / 1000);
			statement.setInt(3, getAllyId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("error while updating ally '" + getAllyId() + "' data in db: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void store()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO ally_data (ally_id,ally_name,leader_id) values (?,?,?)");
			statement.setInt(1, getAllyId());
			statement.setString(2, getAllyName());
			statement.setInt(3, getLeaderId());
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("UPDATE clan_data SET ally_id=? WHERE clan_id=?");
			statement.setInt(1, getAllyId());
			statement.setInt(2, getLeaderId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("error while saving new ally to db " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	private void storeNewMemberInDatabase(L2Clan member)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_data SET ally_id=? WHERE clan_id=?");
			statement.setInt(1, getAllyId());
			statement.setInt(2, member.getClanId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("error while saving new alliance member to db " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	private void removeMemberInDatabase(L2Clan member)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_data SET ally_id=0 WHERE clan_id=?");
			statement.setInt(1, member.getClanId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("error while removing ally member in db " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	private void restore()
	{
		if(getAllyId() == 0) // no ally
			return;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			L2Clan member;

			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT ally_name,leader_id FROM ally_data where ally_id=?");
			statement.setInt(1, getAllyId());
			rset = statement.executeQuery();

			if(rset.next())
			{
				setAllyName(rset.getString("ally_name"));
				int leaderId = rset.getInt("leader_id");

				DatabaseUtils.closeDatabaseSR(statement, rset);
				statement = con.prepareStatement("SELECT clan_id,clan_name FROM clan_data WHERE ally_id=?");
				statement.setInt(1, getAllyId());
				rset = statement.executeQuery();

				while(rset.next())
				{
					member = ClanTable.getInstance().getClan(rset.getInt("clan_id"));
					if(member != null)
						if(member.getClanId() == leaderId)
							setLeader(member);
						else
							addAllyMember(member, false);
				}
			}

			setAllyCrestId(CrestCache.getAllyCrestId(getAllyId()));
		}
		catch(Exception e)
		{
			_log.warning("error while restoring ally");
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void broadcastToOnlineMembers(L2GameServerPacket packet)
	{
		for(L2Clan member : _members.values())
			if(member != null)
				member.broadcastToOnlineMembers(packet);
	}
	
	public void broadcastCSToOnlineMembers(L2GameServerPacket packet, L2Player player)
	{
		for(L2Clan member : _members.values())
			if(member != null)
				member.broadcastCSToOnlineMembers(packet, player);
	}

	public void broadcastToOtherOnlineMembers(L2GameServerPacket packet, L2Player player)
	{
		for(L2Clan member : _members.values())
			if(member != null)
				member.broadcastToOtherOnlineMembers(packet, player);
	}

	@Override
	public String toString()
	{
		return getAllyName();
	}

	public boolean hasAllyCrest()
	{
		return _allyCrestId > 0;
	}

	public L2Player[] getOnlineMembers(String exclude)
	{
		GArray<L2Player> result = new GArray<L2Player>();
		for(L2Clan temp : _members.values())
			for(L2ClanMember temp2 : temp.getMembers())
				if(temp2.isOnline() && temp2.getPlayer() != null && (exclude == null || !temp2.getName().equals(exclude)))
					result.add(temp2.getPlayer());

		return result.toArray(new L2Player[result.size()]);
	}

	public void broadcastAllyStatus(boolean relation)
	{
		for(L2Clan member : getMembers())
			member.broadcastClanStatus(false, true, relation);
	}
}