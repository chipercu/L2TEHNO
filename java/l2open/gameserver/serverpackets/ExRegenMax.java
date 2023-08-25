package l2open.gameserver.serverpackets;

public class ExRegenMax extends L2GameServerPacket
{
	private double _hp_tick;
	private int _abnormal_time;
	private int _tick_time;

	public ExRegenMax(int time, int tick_time, double hp_tick)
	{
		_hp_tick = hp_tick;
		_abnormal_time = time;
		_tick_time = tick_time;
	}

	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x01);
		writeD(1); // - Всегда 1
		writeD(_abnormal_time); // - Время эффекта
		writeD(_tick_time); // Количество тиков интервала t_hp;50;5 - в нашем случае это 5.
		writeF(_hp_tick); // Количество ХП за тик.
	}
}