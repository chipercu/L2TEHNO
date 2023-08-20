package motion;

import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.Location;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Заебашил: Diagod
 * open-team.ru
 ********************************************************************************************
 * Не нравится мой код, иди на 8====> "в лес".
 ********************************************************************************************
 * Механизм для МоС.
 * При запуске сервера, запускается таймер на 1 час для спауна котлов во всех комнатах.
 * Если котлы не трогать то они через 15 минут деспаунятся и опять запускается респаун на 1 час.
 * Если ударить котел то через 30 секунд происходит респаун мобов которые находятся в комнате(удаляются старые и спаунятся новые в зависимости от удареного котла),
 * а так же удаляются котлы и ставится время респауна 1 час.
 **/
public class MonasteryOfSilence extends Functions implements ScriptFile
{
	private static final MonasteryOfSilence _instance = new MonasteryOfSilence();

	public static final MonasteryOfSilence getInstance()
	{
		return _instance;
	}

	private final ReentrantLock lock = new ReentrantLock();

	private static L2NpcInstance burner1[] = new L2NpcInstance[8];
	private static L2NpcInstance burner2[] = new L2NpcInstance[8];
	private static L2NpcInstance burner3[] = new L2NpcInstance[8];
	private static L2NpcInstance burner4[] = new L2NpcInstance[8];
	
	private static int time_spawn = 60 * 60 * 1000;
	private static int time_despawn = 3 * 60 * 1000;

	public static FastMap<Long, Room> rooms = new FastMap<Long, Room>();

	private static int[] locId = 
	{
		95000001, 96000001,
		95000002, 96000002,// 97000002,
		95000003, 96000003,// 97000003,
		95000004, 96000004,
		95000005, 96000005,
		95000006, 96000006,
		95000007, 96000007,
		95000008, 96000008
	};

	// Координаты спауна Котлов...
	private static final Location[] BurnerSpawn = 
												{
													new Location(113125, -73174, -598),
													new Location(113126, -73289, -598),
													new Location(113126, -73403, -598),
													new Location(113126, -73517, -598),

													new Location(113122, -71873, -600),
													new Location(113121, -72011, -600),
													new Location(113120, -72125, -600),
													new Location(113120, -72243, -600),

													new Location(112385, -80802, -1639),
													new Location(112383, -80913, -1639),
													new Location(112384, -81024, -1639),
													new Location(112383, -81131, -1639),

													new Location(112384, -79512, -1639),
													new Location(112383, -79628, -1638),
													new Location(112383, -79734, -1638),
													new Location(112383, -79841, -1638),

													new Location(108528, -76098, -1120),
													new Location(108408, -76096, -1120),
													new Location(108300, -76097, -1120),
													new Location(108178, -76095, -1120),

													new Location(109468, -76098, -1119),
													new Location(109574, -76094, -1119),
													new Location(109682, -76095, -1119),
													new Location(109803, -76093, -1119),

													new Location(115798, -76943, -79),
													new Location(115898, -76943, -79),
													new Location(116024, -76946, -80),
													new Location(116130, -76944, -79),

													new Location(117074, -76909, -80),
													new Location(117181, -76913, -81),
													new Location(117313, -76913, -80),
													new Location(117406, -76914, -81)
												};

	public MonasteryOfSilence()
	{}

	public class Room
	{
		public int _roomId = 0;
		public int _time = 0;
		public int _lastBurner = 4;

		public ScheduledFuture<?> _burnerSpawn = null;
		public ScheduledFuture<?> _burnerDeSpawn = null;
		public ScheduledFuture<?> _mobSpawn = null;

		public Room(int roomId)
		{
			_roomId = roomId;
		}

		private void start()
		{
			// Спауним мобов во всех комнатах...
			spawn(22798, locId[_roomId*2-1], 2, "");
			spawn(22799, locId[_roomId*2-1], 2, "");
			spawn(22800, locId[_roomId*2-1], 2, "");

			spawn(22798, locId[_roomId*2-2], 2, "");
			spawn(22799, locId[_roomId*2-2], 2, "");
			spawn(22800, locId[_roomId*2-2], 2, "");
			// Запускаем таймер на спаун котлов...По офф респаун через 1 час после деспауна...
			ThreadPoolManager.getInstance().schedule(new TimerSpawn(_roomId), time_spawn);
		}
	}

	public static void addRoom(long id, Room room)
	{
		rooms.put(id, room);
	}

	public static Room getRoom(long id)
	{
		Room room = rooms.get(id);
		if(room != null)
			return room;
		return null;
	}

	private void startRoom()
	{
		for(int i = 0;i < 8;i++)
		{
			Room room = new Room(i+1);
			room.start();

			lock.lock();
			try
			{
				addRoom(i+1, room);
			}
			finally
			{
				lock.unlock();
			}
		}
	}

	private class TimerSpawn extends l2open.common.RunnableImpl
	{
		private Room _room = null;
		private int _roomId = 0;
		@Override
		public void runImpl()
		{
			// Блокируем, что бы не выполнялось в перемешку...Лучше пускай будет задержка на пол секунды...
			lock.lock();
			try
			{
				_room = getRoom(_roomId);
				NpcSay cs = null;

				if(_room._burnerSpawn != null)
				{
					_room._burnerSpawn.cancel(false);
					_room._burnerSpawn = null;
				}
				burner1[_room._roomId-1] = spawnBurner(18914, 60008, 1, 95000000+_room._roomId, BurnerSpawn[(_room._roomId-1)*4]);
				burner2[_room._roomId-1] = spawnBurner(18914, 60009, 2, 95000000+_room._roomId, BurnerSpawn[(_room._roomId-1)*4+1]);
				burner3[_room._roomId-1] = spawnBurner(18914, 60010, 3, 95000000+_room._roomId, BurnerSpawn[(_room._roomId-1)*4+2]);
				burner4[_room._roomId-1] = spawnBurner(18914, 60011, 4, 95000000+_room._roomId, BurnerSpawn[(_room._roomId-1)*4+3]);

				// Загорается та жаровня, от которой были заспаунены последний рас мобы.
				switch(_room._lastBurner)
				{
					case 1:
						burner1[_room._roomId-1].setNpcState(1);
						break;
					case 2:
						burner2[_room._roomId-1].setNpcState(1);
						break;
					case 3:
						burner3[_room._roomId-1].setNpcState(1);
						break;
					case 4:
						burner4[_room._roomId-1].setNpcState(1);
						break;
				}

				// При спауне жаровни кричат в чат...
				cs = new NpcSay(burner1[_room._roomId-1], Say2C.NPC_ALL, 60011);
				broadcastPacket(cs, burner1[_room._roomId-1]);
				cs = new NpcSay(burner2[_room._roomId-1], Say2C.NPC_ALL, 60010);
				broadcastPacket(cs, burner2[_room._roomId-1]);
				cs = new NpcSay(burner3[_room._roomId-1], Say2C.NPC_ALL, 60009);
				broadcastPacket(cs, burner3[_room._roomId-1]);
				cs = new NpcSay(burner4[_room._roomId-1], Say2C.NPC_ALL, 60008);
				broadcastPacket(cs, burner4[_room._roomId-1]);
				// Запускаем таск на Деспаун котлов...3 минуты...
				if(_room._burnerDeSpawn == null)
					_room._burnerDeSpawn = ThreadPoolManager.getInstance().schedule(new TimerDeSpawn(_roomId, 0, true), time_despawn);
			}
			finally
			{
				lock.unlock();
			}
		}

		public TimerSpawn(int room)
		{
			_roomId = room;
		}
	}

	private class TimerDeSpawn extends l2open.common.RunnableImpl
	{
		private int _id = 0;
		private boolean _isAll = true;
		private Room _room = null;
		private int _roomId = 0;

		@Override
		public void runImpl()
		{
			_room = getRoom(_roomId);
			if(_room._burnerDeSpawn != null)
			{
				_room._burnerDeSpawn.cancel(false);
				_room._burnerDeSpawn = null;
			}
			if(_isAll)
			{
				// Удаляем котлы во всех комнатах...
				burner1[_room._roomId-1].deleteMe();
				burner2[_room._roomId-1].deleteMe();
				burner3[_room._roomId-1].deleteMe();
				burner4[_room._roomId-1].deleteMe();
			}
			else
			{
				// Удаляем котлы в выбраной комнате...
				burner1[_id-1].deleteMe();
				burner2[_id-1].deleteMe();
				burner3[_id-1].deleteMe();
				burner4[_id-1].deleteMe();
			}
			// Запускаем таск на Спаун котлов...1 час...
			_room._burnerSpawn = ThreadPoolManager.getInstance().schedule(new TimerSpawn(_roomId), time_spawn);
		}

		public TimerDeSpawn(int room, int id, boolean isAll)
		{
			_isAll = isAll;
			_id = id;
			_roomId = room;
		}
	}

	private class TimerSpawnMob extends l2open.common.RunnableImpl
	{
		private L2NpcInstance _npc = null;
		private Room _room = null;
		private int _roomId = 0;
		private int locID = 0;

		@Override
		public void runImpl()
		{
			// Блокируем, что бы не выполнялось в перемешку...Лучше пускай будет задержка на пол секунды...
			lock.lock();
			try
			{
				int burnID = _npc.getAI().BURNER_NUMBER;
				_room = getRoom(_roomId);
				locID = _roomId + 95000000;
				_room._lastBurner = burnID;

				for(L2NpcInstance mob : L2ObjectsStorage.getAllNpcs())
					if(mob.getNpcId() == 22798 || mob.getNpcId() == 22799 || mob.getNpcId() == 22800)
						if(mob.getSpawn().getLocation() == locID || mob.getSpawn().getLocation() == locID+1000000)
							mob.deleteMe();

				for(int i = 0;i < 3;i++)
				{
					if(locID != 95000002 && locID != 95000003 && i == 2)
						break;
					switch(burnID)
					{
						case 1:
							spawn(22798, locID, 2, "");
							spawn(22799, locID, 2, "");
							spawn(22800, locID, 2, "");
							break;
						case 2:
							spawn(22798, locID, 7, "");
							spawn(22799, locID, 1, "");
							spawn(22800, locID, 1, "");
							break;
						case 3:
							spawn(22798, locID, 1, "");
							spawn(22799, locID, 7, "");
							spawn(22800, locID, 1, "");
							break;
						case 4:
							spawn(22798, locID, 1, "");
							spawn(22799, locID, 1, "");
							spawn(22800, locID, 7, "");
							break;
					}
					locID = locID + 1000000;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				lock.unlock();
			}
		}

		public TimerSpawnMob(int room, L2NpcInstance npc)
		{
			_roomId = room;
			_npc = npc;
		}
	}

	public void setSpawn(L2NpcInstance npc)
	{
		Room _room = null;
		int room = npc.getAI().LOC_ID - 95000000;
		_room = getRoom(room);

		if(_room._burnerDeSpawn != null)
		{
			_room._burnerDeSpawn.cancel(false);
			_room._burnerDeSpawn = null;
		}
		if(_room._mobSpawn != null)
		{
			_room._mobSpawn.cancel(false);
			_room._mobSpawn = null;
		}
		
		if(_room._time == 0)
			ThreadPoolManager.getInstance().schedule(new Timer(20, room), 1);

		if(_room._mobSpawn == null)
			_room._mobSpawn = ThreadPoolManager.getInstance().schedule(new TimerSpawnMob(room, npc), _room._time*1000);

		if(_room._burnerDeSpawn == null)
			_room._burnerDeSpawn = ThreadPoolManager.getInstance().schedule(new TimerDeSpawn(room, room, false), (_room._time-15)*1000+1);

		NpcSay cs = new NpcSay(npc, Say2C.NPC_ALL, 60012);
		broadcastPacket(cs, npc);
	}

	private class Timer extends l2open.common.RunnableImpl
	{
		private int time = 0;
		Room _room = null;

		@Override
		public void runImpl()
		{
			_room._time = time;
			if(time < 1)
			{
				_room._time = 0;
				return;
			}
			ThreadPoolManager.getInstance().schedule(new Timer(time - 1, _room._roomId), 1000);
		}

		public Timer(int _time, int id)
		{
			_room = getRoom(id);
			time = _time;
			_room._time = _time;
		}
	}

	public static void broadcastPacket(L2GameServerPacket packets, L2NpcInstance npc)
	{
		for(L2Player player : L2World.getAroundPlayers(npc, 1000, 50))
			if(player != null)
				player.sendPacket(packets);
	}

	private L2Spawn spawn(int npcId, int locId, int count, String st)
	{
		L2Spawn sp = null;
		lock.lock();
		try
		{
			L2Spawn spawn = new L2Spawn(NpcTable.getTemplate(npcId));
			spawn.setAmount(count);
			spawn.setRespawnDelay(0, 0);
			spawn.setLocation(locId);
			spawn.init();
			sp = spawn;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
		}
		return sp;
	}

	private L2NpcInstance spawnBurner(int npcId, int str_id, int number, int lcId, Location loc)
	{
		L2NpcInstance npc = null;
		L2Spawn spawn = null;
		StatsSet npcDat = null;
		L2NpcTemplate template = null;
		lock.lock();
		try
		{
			template = NpcTable.getTemplate(npcId);
			npcDat = template.getSet();
			template.setSet(npcDat);

			spawn = new L2Spawn(template);
			spawn.setAmount(1);
			spawn.setRespawnDelay(0, 0);
			spawn.setLoc(loc);
			spawn.setAIParam("BURNER_NUMBER="+number+";LOC_ID="+lcId);
			npc = spawn.doSpawn(true);
			npc.ChangeNickName(npc, str_id);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
		}
		return npc;
	}

	public void onLoad()
	{
		_log.info("MonasteryOfSilence: Loaded");
		startRoom();
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}