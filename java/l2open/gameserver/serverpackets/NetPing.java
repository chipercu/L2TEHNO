package l2open.gameserver.serverpackets;

public class NetPing extends L2GameServerPacket
{
	private int _time;

	public NetPing(int time)
	{
		_time = time;
	}

	public NetPing()
	{
		_time = (int)(System.currentTimeMillis() - l2open.gameserver.GameStart.serverUpTime());
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xD9);
		//writeD((int)(System.currentTimeMillis() - l2open.gameserver.GameStart.serverUpTime())); // Можно отправить либо, что...Клиент вернет это значение...Скорей всего тут отправляется время в МС или возможно номер запроса.
		writeD(_time);
		//getClient().pingTime = System.currentTimeMillis(); // Записываем время отправки пакета.
		//getClient().ping_send = 1; // Записываем время отправки пакета.
	}
}
