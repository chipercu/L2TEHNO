package l2open.gameserver;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.listeners.PropertyCollection;
import l2open.extensions.listeners.engine.DefaultListenerEngine;
import l2open.extensions.listeners.engine.ListenerEngine;
import l2open.gameserver.instancemanager.DayNightSpawnManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ClientSetTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class GameTimeController
{
	private static final Logger _log = Logger.getLogger(GameTimeController.class.getName());

	public static final int TICKS_PER_SECOND = 10;
	public static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
	public static final int TICKS_IN_DAY = 60 * 60 * 4 * TICKS_PER_SECOND; // в игровых сутках 4 часа
	public static final int SaveGameTimeInterval = ConfigValue.SaveGameTimeInterval * TICKS_PER_SECOND;

	private static GameTimeController _instance = new GameTimeController();

	private static int _gameTicks, _gameTicksLastSave;
	private static long _gameStartTime;
	private static boolean _isNight;

	private MainThread _mainThread;

	private final ListenerEngine<GameTimeController> listenerEngine = new DefaultListenerEngine<GameTimeController>(this);

	/**
	 * one ingame day is 240 real minutes
	 */
	public static GameTimeController getInstance()
	{
		ThreadPoolManager.getInstance();
		return _instance;
	}

	private GameTimeController()
	{
		_gameTicks = ServerVariables.getInt("GameTicks", 0);
		if(_gameTicks > 0)
			_gameTicks = _gameTicks % TICKS_IN_DAY;
		else
			_gameTicks = 3600000 / MILLIS_IN_TICK;
		_gameTicksLastSave = _gameTicks;
		_gameStartTime = System.currentTimeMillis() - _gameTicks * MILLIS_IN_TICK; // offset so that the server starts a day begin

		_mainThread = new MainThread();
		_mainThread.start();

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new TimerWatcher(), 0, 1000);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new CheckSunState(), ((getGameTime() / 60 % 24 + 1) * 60 - getGameTime()) * 1000L, 120000);
	}

	public boolean isNowNight()
	{
		return _isNight;
	}

	public int getGameTime()
	{
		return _gameTicks / (TICKS_PER_SECOND * 10);
	}

	private void SaveTime()
	{
		if(_gameTicks - _gameTicksLastSave > SaveGameTimeInterval)
			try
			{
				ServerVariables.set("GameTicks", _gameTicks);
				_gameTicksLastSave = _gameTicks;
			}
			catch(Exception E)
			{}
	}

	public void stopTimers()
	{
		_mainThread._stop = true;
	}

	class MainThread extends Thread
	{
		protected boolean _stop;

		public MainThread()
		{
			super("GameTimeThread");
			setDaemon(true);
			setPriority(MAX_PRIORITY);
			_stop = false;
		}

		@Override
		public void run()
		{
			try
			{
				while(!_stop)
				{
					_gameTicks = (int) ((System.currentTimeMillis() - _gameStartTime) / MILLIS_IN_TICK);
					sleep(100); //TODO в конфиг
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			_log.info("TimerThread was canceled");
		}
	}

	class TimerWatcher extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(_mainThread.isAlive())
				SaveTime();
			else
			{
				String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
				_log.warning(time + " MainThread stop with following error. Restarting...");
				_mainThread = new MainThread();
				_mainThread.start();
			}
		}
	}

	public class CheckSunState extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			int h = getGameTime() / 60 % 24; // Time in hour
			boolean tempIsNight = h < 6;

			if(tempIsNight != isNowNight()) // If diff day/night state
				_isNight = tempIsNight; // Set current day/night varible to value of temp varible
			else
				return; // Do nothing if same state

			DayNightSpawnManager.getInstance().notifyChangeMode();

			getListenerEngine().firePropertyChanged(PropertyCollection.GameTimeControllerDayNightChange, getInstance(), !_isNight, _isNight);

			for(L2Player player : L2ObjectsStorage.getPlayers())
			{
				player.checkDayNightMessages();
				player.sendPacket(new ClientSetTime());
			}
		}
	}

	public ListenerEngine<GameTimeController> getListenerEngine()
	{
		return listenerEngine;
	}
}