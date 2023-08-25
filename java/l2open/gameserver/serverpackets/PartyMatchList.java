package l2open.gameserver.serverpackets;

import l2open.gameserver.model.PartyRoom;

public class PartyMatchList extends L2GameServerPacket
{
	private int _id;
	private int _minLevel;
	private int _maxLevel;
	private int _lootDist;
	private int _maxMembers;
	private int _location;
	private String _title;

	public PartyMatchList()
	{}

	public PartyMatchList(PartyRoom room)
	{
		_id = room.getId();
		_minLevel = room.getMinLevel();
		_maxLevel = room.getMaxLevel();
		_lootDist = room.getLootDist();
		_maxMembers = room.getMaxMembers();
		_location = room.getLocation();
		_title = room.getTitle();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x9d);
		writeD(_id); // room id
		writeD(_maxMembers); //max members
		writeD(_minLevel); //min level
		writeD(_maxLevel); //max level
		writeD(_lootDist); //loot distribution 1-Random 2-Random includ. etc
		writeD(_location); //location
		writeS(_title); // room name
	}
}