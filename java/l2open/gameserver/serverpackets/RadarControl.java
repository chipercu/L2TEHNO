package l2open.gameserver.serverpackets;

import l2open.util.Location;

/**
 * Примеры пакетов:
 *
 * Ставит флажок на карте и показывает стрелку на компасе:
 * EB 00 00 00 00 01 00 00 00 40 2B FF FF 8C 3C 02 00 A0 F6 FF FF
 * Убирает флажок и стрелку
 * EB 02 00 00 00 02 00 00 00 40 2B FF FF 8C 3C 02 00 A0 F6 FF FF
 */
public class RadarControl extends L2GameServerPacket
{
	private int _showRadar;
	private int _type;
	private Location _loc;

	public RadarControl(int showRadar, int type, Location loc)
	{
		_showRadar = showRadar; // showRader?? 0 = showradar; 1 = delete radar;
		_type = type; // 1 - только стрелка над головой, 2 - флажок на карте
		_loc = loc;
	}

	public RadarControl(int showRadar, int type, int x, int y, int z)
	{
		this(showRadar, type, new Location(x, y, z));
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xf1);
		writeD(_showRadar);
		writeD(_type); //maybe type
		writeD(_loc.x); //x
		writeD(_loc.y); //y
		writeD(_loc.z); //z
	}
}