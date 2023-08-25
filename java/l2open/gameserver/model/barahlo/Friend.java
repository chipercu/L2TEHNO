package l2open.gameserver.model.barahlo;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2ObjectsStorage;

public class Friend
{
	private final int _objectId;
	private String _name;
	private int _classId;
	private int _level;

	public Friend(int objectId, String name, int classId, int level)
	{
		_objectId = objectId;
		_name = name;
		_classId = classId;
		_level = level;
	}

	public String getName()
	{
		L2Player player = getPlayer();
		return player == null ? _name : player.getName();
	}

	public int getObjectId()
	{
		return _objectId;
	}

	public int getClassId()
	{
		L2Player player = getPlayer();
		return player == null ? _classId : player.getActiveClassId();
	}

	public int getLevel()
	{
		L2Player player = getPlayer();
		return player == null ? _level : player.getLevel();
	}

	public boolean isOnline()
	{
		L2Player player = L2ObjectsStorage.getPlayer(_objectId);
		return player != null && !player.isInOfflineMode();
	}

	public L2Player getPlayer()
	{
		L2Player player = L2ObjectsStorage.getPlayer(_objectId);
		return player != null && !player.isInOfflineMode() ? player : null;
	}
}