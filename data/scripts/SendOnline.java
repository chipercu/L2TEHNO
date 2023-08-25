import java.util.concurrent.ScheduledFuture;

import l2open.extensions.scripts.ScriptFile;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.tables.FakePlayersTable;

public class SendOnline extends L2GameServerPacket implements ScriptFile, Runnable
{
	private static ScheduledFuture<?> _task = null;

	public void runImpl()
	{
		if(ConfigValue.SendOnlineTimeResend == 0)
			return;
		refresh();
		for(L2Player player : L2ObjectsStorage.getPlayers())
			if(!player.isInOfflineMode())
				player.sendPacket(this);
		if(_task != null)
		{
			_task.cancel(false);
			_task = null;
		}
		_task = ThreadPoolManager.getInstance().schedule(new SendOnline(), ConfigValue.SendOnlineTimeResend);
	}

	private int online;

	public SendOnline()
	{
		refresh();
	}

	public void refresh()
	{
		int offline = L2ObjectsStorage.getAllOfflineCount();
		int all = L2ObjectsStorage.getAllPlayersCount();
		int fake = FakePlayersTable.getFakePlayersCount();
		int fakeMy = ConfigValue.SendOnlineFakeMy;
		switch(ConfigValue.SendOnlineType)
		{
			case 1:
				online = all - offline + fakeMy;
				break;
			case 2:
				online = offline + fakeMy;
				break;
			case 3:
				online = all + fakeMy;
				break;
			case 4:
				online = all + fake + fakeMy;
				break;
			default:
				online = 0;
				break;
		}		
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xb0); 
		writeC(0xA1);
		writeC(0x01);
		writeD(ConfigValue.SendOnlineX); // Координата Х в клиенте
		writeD(ConfigValue.SendOnlineY); // Координата Y в клиенте
		writeD(Integer.decode("0x" + ConfigValue.SendOnlineColor)); // Цвет текста 0xFFBBGGRR
		writeD(online);
	}

	public void onLoad()
	{
		if(ConfigValue.SendOnlineTimeResend > 0)
			_task = ThreadPoolManager.getInstance().schedule(new SendOnline(), ConfigValue.SendOnlineTimeResend);
	}

	public void onReload()
	{
		if(_task != null)
		{
			_task.cancel(true);
			_task = null;
		}
	}

	public void onShutdown()
	{
		if(_task != null)
		{
			_task.cancel(true);
			_task = null;
		}
	}

	@Override
	public void run() {
		runImpl();
	}
}