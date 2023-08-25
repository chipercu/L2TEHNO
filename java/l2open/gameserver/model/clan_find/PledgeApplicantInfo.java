package l2open.gameserver.model.clan_find;

import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;

/**
 * @author Sdw
 */
public class PledgeApplicantInfo
{
	private final int _playerId;
	private final int _requestClanId;
	private String _playerName;
	private int _playerLvl;
	private int _classId;
	private final int _karma;
	private final String _message;
	
	public PledgeApplicantInfo(int playerId, String playerName, int playerLevel, int karma, int requestClanId, String message)
	{
		_playerId = playerId;
		_requestClanId = requestClanId;
		_playerName = playerName;
		_playerLvl = playerLevel;
		_karma = karma;
		_message = message;
	}
	
	public int getPlayerId()
	{
		return _playerId;
	}
	
	public int getRequestClanId()
	{
		return _requestClanId;
	}
	
	public String getPlayerName()
	{
		if (isOnline() && !getPlayerInstance().getName().equalsIgnoreCase(_playerName))
		{
			_playerName = getPlayerInstance().getName();
		}
		return _playerName;
	}
	
	public int getPlayerLvl()
	{
		if (isOnline() && (getPlayerInstance().getLevel() != _playerLvl))
		{
			_playerLvl = getPlayerInstance().getLevel();
		}
		return _playerLvl;
	}

	public int getClassId()
	{
		if (isOnline() && (getPlayerInstance().getBaseClassId() != _classId))
		{
			_classId = getPlayerInstance().getClassId().getId();
		}
		return _classId;
	}
	
	public String getMessage()
	{
		return _message;
	}
	
	public int getKarma()
	{
		return _karma;
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