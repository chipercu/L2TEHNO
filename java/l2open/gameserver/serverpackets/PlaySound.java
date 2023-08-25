package l2open.gameserver.serverpackets;

import l2open.util.Location;

public class PlaySound extends L2GameServerPacket
{
	private int _unknown1;
	private String _soundFile;
	private int _unknown3;
	private int _unknown4;

	private int _x;
	private int _y;
	private int _z;
	private int _h;

	public PlaySound(String soundFile)
	{
		_unknown1 = 0;
		_soundFile = soundFile;
		_unknown3 = 0;
		_unknown4 = 0;
		_x = 0;
		_y = 0;
		_z = 0;
		_h = 0;
	}

	public PlaySound(int type, String soundFile, int type2)
	{
		_unknown1 = type;
		_soundFile = soundFile;
		_unknown3 = type2;
		_unknown4 = 0;
		_x = 0;
		_y = 0;
		_z = 0;
		_h = 0;
	}

	public PlaySound(int unknown1, String soundFile, int unknown3, int unknown4, Location loc)
	{
		_unknown1 = unknown1;
		_soundFile = soundFile;
		_unknown3 = unknown3;
		_unknown4 = unknown4;
		_x = loc.x;
		_y = loc.y;
		_z = loc.z;
		_h = loc.h;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x9e);
		writeD(_unknown1); //unknown 0 for quest and ship, c4 toturial = 2
		writeS(_soundFile);
		writeD(_unknown3); //unknown 0 for quest; 1 for ship;
		writeD(_unknown4); //0 for quest; objectId of ship
		writeD(_x); //x
		writeD(_y); //y
		writeD(_z); //z
		writeD(_h); //не уверен на все 100% :)
	}
}