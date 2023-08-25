package l2open.gameserver.model.clan_find;

import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;

/**
 * @author Sdw
 */
public class PledgeWaitingInfo
{
	private int _playerId;
	private int _playerClassId;
	private int _playerLvl;
	private final int _karma;
	private String _playerName;
	
	public PledgeWaitingInfo(int playerId, int playerLvl, int karma, int classId, String playerName)
	{
		_playerId = playerId;
		_playerClassId = classId;
		_playerLvl = playerLvl;
		_karma = karma;
		_playerName = playerName;
	}
	
	public int getPlayerId()
	{
		return _playerId;
	}
	
	public void setPlayerId(int playerId)
	{
		_playerId = playerId;
	}
	
	public int getPlayerClassId()
	{
		if (isOnline() && (getPlayerInstance().getBaseClassId() != _playerClassId))
		{
			_playerClassId = getPlayerInstance().getClassId().getId();
		}
		return _playerClassId;
	}
	
	public int getPlayerLvl()
	{
		if (isOnline() && (getPlayerInstance().getLevel() != _playerLvl))
		{
			_playerLvl = getPlayerInstance().getLevel();
		}
		return _playerLvl;
	}
	
	public int getKarma()
	{
		return _karma;
	}
	
	public String getPlayerName()
	{
		if (isOnline() && !getPlayerInstance().getName().equalsIgnoreCase(_playerName))
		{
			_playerName = getPlayerInstance().getName();
		}
		return _playerName;
	}
	
	public L2Player getPlayerInstance()
	{
		return L2ObjectsStorage.getPlayer(_playerId);
	}
	
	public boolean isOnline()
	{
		return getPlayerInstance() != null && getPlayerInstance().isOnline();
	}
}
