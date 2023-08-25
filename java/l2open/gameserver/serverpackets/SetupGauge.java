package l2open.gameserver.serverpackets;

public class SetupGauge extends L2GameServerPacket
{
	public static final byte BLUE = 0;
	public static final byte RED = 1;
	public static final byte CYAN = 2;
	public static final byte GREEN = 3;

	private int _color;
	private int _cur_time;
	private int _max_time;
	private int _objId;

	public SetupGauge(int objId, int color, int cur_time, int max_time)
	{
		_objId = objId;
		_color = color;// color  0-blue   1-red  2-cyan  3-green
		_cur_time = cur_time;
		_max_time = max_time;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x6b);
		writeD(_objId);
		writeD(_color);
		writeD(_cur_time);
		writeD(_max_time);
	}
}