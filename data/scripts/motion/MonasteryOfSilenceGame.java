package motion;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.Location;
import l2open.util.Rnd;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Заебашил: Diagod
 * open-team.ru
 ********************************************************************************************
 * Не нравится мой код, иди на 8====> "в лес".
 ********************************************************************************************
 * Механизм для мини-игры в МоС.
 * Если чесно то он кривой в пизду, можно раза в 2 сделать проще его, но мне вломы...Та и работает он впринципи на 99% по оффу, может там какая-то мелочь и не так должна быть...
 **/
public class MonasteryOfSilenceGame extends Functions implements ScriptFile
{
	private final ReentrantLock lock = new ReentrantLock();

	private static L2NpcInstance burner1[] = new L2NpcInstance[2];
	private static L2NpcInstance burner2[] = new L2NpcInstance[2];
	private static L2NpcInstance burner3[] = new L2NpcInstance[2];
	private static L2NpcInstance burner4[] = new L2NpcInstance[2];
	private static L2NpcInstance burner5[] = new L2NpcInstance[2];
	private static L2NpcInstance burner6[] = new L2NpcInstance[2];
	private static L2NpcInstance burner7[] = new L2NpcInstance[2];
	private static L2NpcInstance burner8[] = new L2NpcInstance[2];
	private static L2NpcInstance burner9[] = new L2NpcInstance[2];

	public static FastMap<Long, Room> rooms = new FastMap<Long, Room>();
	public static FastMap<Long, Integer> playId = new FastMap<Long, Integer>();

	// Координаты спауна Котлов...
	private static final Location[] BurnerSpawn = 
												{
													new Location(110238, -82249, -1589), // [POT_NUMBER]=2
													new Location(110240, -82406, -1589), // [POT_NUMBER]=3
													new Location(110236, -82097, -1590), // [POT_NUMBER]=1
													new Location(110414, -82091, -1590), // [POT_NUMBER]=4
													new Location(110413, -82252, -1591), // [POT_NUMBER]=5
													new Location(110413, -82410, -1591), // [POT_NUMBER]=6
													new Location(110568, -82092, -1590), // [POT_NUMBER]=7
													new Location(110566, -82250, -1592), // [POT_NUMBER]=8
													new Location(110560, -82415, -1592), // [POT_NUMBER]=9

													new Location(114399, -70596, -544), // [POT_NUMBER]=1
													new Location(114587, -70596, -544), // [POT_NUMBER]=2
													new Location(114772, -70595, -544), // [POT_NUMBER]=3
													new Location(114398, -70781, -544), // [POT_NUMBER]=4
													new Location(114583, -70784, -544), // [POT_NUMBER]=5
													new Location(114769, -70786, -544), // [POT_NUMBER]=6
													new Location(114398, -70968, -544), // [POT_NUMBER]=7
													new Location(114585, -70972, -544), // [POT_NUMBER]=8
													new Location(114767, -70974, -544)  // [POT_NUMBER]=9
												};

	public MonasteryOfSilenceGame()
	{}

	public class Room
	{
		public int _roomId = 0;
		public long _objectId = 0;
		public L2Player _player = null;
		
		// Игра окончена...
		public boolean _matchEnd = true;

		// Никто не играет...
		public boolean _freeGame = true; // Когда игрок использует все 9 попыток то ставим опять true
		public long _freeGameTime = 0;

		// Играет игрок...
		public boolean _playGame = false; // Когда игрок использует все 9 попыток то ставим опять false

		public ScheduledFuture<?> _gameTimeExpired = null;
		public ScheduledFuture<?> _hurryUp = null;
		public L2NpcInstance[] burnSave = new L2NpcInstance[9];
		public L2NpcInstance[] burnTurn = new L2NpcInstance[9];
		public int burnTurnCount = 0;
		public L2NpcInstance _npc = null;

		public Room(int roomId, long objectId)
		{
			_roomId = roomId;
			_objectId = objectId;
		}

		private void start(int top)
		{
			burnTurnCount = 0;
			//_log.info("MonasteryOfSilenceGame: top="+top);
			try
			{
				burner1[_roomId-1] = spawnBurner(1, _roomId, BurnerSpawn[(_roomId-1)*9]);
				burner2[_roomId-1] = spawnBurner(2, _roomId, BurnerSpawn[(_roomId-1)*9+1]);
				burner3[_roomId-1] = spawnBurner(3, _roomId, BurnerSpawn[(_roomId-1)*9+2]);
				burner4[_roomId-1] = spawnBurner(4, _roomId, BurnerSpawn[(_roomId-1)*9+3]);
				burner5[_roomId-1] = spawnBurner(5, _roomId, BurnerSpawn[(_roomId-1)*9+4]);
				burner6[_roomId-1] = spawnBurner(6, _roomId, BurnerSpawn[(_roomId-1)*9+5]);
				burner7[_roomId-1] = spawnBurner(7, _roomId, BurnerSpawn[(_roomId-1)*9+6]);
				burner8[_roomId-1] = spawnBurner(8, _roomId, BurnerSpawn[(_roomId-1)*9+7]);
				burner9[_roomId-1] = spawnBurner(9, _roomId, BurnerSpawn[(_roomId-1)*9+8]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			ThreadPoolManager.getInstance().schedule(new StartBurn(_roomId, 0), 1*1000);
			if(top == 1)
			{
				_hurryUp = ThreadPoolManager.getInstance().schedule(new HURRY_UP(_npc, 1), 2*60*1000);
				ThreadPoolManager.getInstance().schedule(new GAME_TIME(_roomId), 190*1000);
			}
			
			_matchEnd = false;
			_freeGame = false;
			// Это мега затычка на всякий пожарный...говорят, бывает виснет мини игра...
			_freeGameTime = System.currentTimeMillis() + 5 * 60 * 1000;
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

	// До пизды не красиво, нужно переделать но очень вломы...
	public void startRoom(L2NpcInstance npc, long objectId)
	{
		int id = npc.getAI().ROOM_ID;
		if(playId.get(objectId) == null || getRoom(id) == null)
		{
			Room room = null;
			if(getRoom(id) == null)
			{
				if(playId.get(objectId) != null)
					playId.remove(objectId);
				room = new Room(id, objectId);
				room._npc = npc;
				room._player = L2ObjectsStorage.getPlayer((int)objectId);
				room.start(1);
				lock.lock();
				try
				{
					addRoom(id, room);
				}
				finally
				{
					lock.unlock();
				}
			}
			else
			{
				room = getRoom(id);
				room._objectId = objectId;
				room._player = L2ObjectsStorage.getPlayer((int)objectId);
				room.start(1);
			}
			playId.put(objectId, 1);
		}
		else if(playId.get(objectId) == 1)
		{
			Room _room = getRoom(id);
			_room.start(2);
			playId.remove(objectId);
			playId.put(objectId, 2);
		}
		else if(playId.get(objectId) == 2)
		{
			Room _room = getRoom(id);
			_room.start(3);
			playId.remove(objectId);
			playId.put(objectId, 3);
		}

		// На всякий случай, если кто-то сильно умный...
		L2ObjectsStorage.getPlayer((int)objectId).setVar("MonasteryOfSilenceGameReuse", String.valueOf(System.currentTimeMillis() + (ConfigValue.MonasteryOfSilenceGameReuse+5) * 60 * 1000));
	}

	public boolean matchEnd(int _roomId)
	{
		Room _room = getRoom(_roomId);
		if(_room == null || _room._matchEnd)
			return true;
		return false;
	}

	public boolean freeGame(int _roomId, L2NpcInstance npc)
	{
		Room _room = getRoom(_roomId);
		if(_room == null || _room._freeGame)
			return true;
		else if(_room != null && _room._freeGameTime < System.currentTimeMillis())
		{
			endGame(_room, npc, 1);
			return true;
		}
		return false;
	}

	public boolean playGame(int _roomId)
	{
		Room _room = getRoom(_roomId);
		if(_room == null || !_room._playGame)
			return false;
		return true;
	}

	public boolean isPlayer(long objectId, int _roomId)
	{
		Room _room = getRoom(_roomId);
		if(_room != null && _room._objectId == objectId)
			return true;
		return false;
	}

	public static int gameCount(L2Player player)
	{
		return playId.get((long)player.getObjectId()) == null ? 0 : playId.get((long)player.getObjectId());
	}

	// Рамдомно запаливаем котлы и заносим их в масив для дальнейшего сравнения...
	private class StartBurn extends l2open.common.RunnableImpl
	{
		private int _id = 0;
		private int _roomId = 0;

		@Override
		public void runImpl()
		{
			Room _room = getRoom(_roomId);
			if(_id < 9)
			{
				switch(Rnd.get(8))
				{
					case 0:
						_room.burnSave[_id] = burner1[_roomId-1];
						burner1[_roomId-1].setNpcState(1);
						ThreadPoolManager.getInstance().schedule(new StartBurn(_roomId, _id+1), 3*1000);
						ThreadPoolManager.getInstance().schedule(new OffBurn(burner1[_roomId-1]), 2*1000);
						break;
					case 1:
						_room.burnSave[_id] = burner2[_roomId-1];
						burner2[_roomId-1].setNpcState(1);
						ThreadPoolManager.getInstance().schedule(new StartBurn(_roomId, _id+1), 3*1000);
						ThreadPoolManager.getInstance().schedule(new OffBurn(burner2[_roomId-1]), 2*1000);
						break;
					case 2:
						_room.burnSave[_id] = burner3[_roomId-1];
						burner3[_roomId-1].setNpcState(1);
						ThreadPoolManager.getInstance().schedule(new StartBurn(_roomId, _id+1), 3*1000);
						ThreadPoolManager.getInstance().schedule(new OffBurn(burner3[_roomId-1]), 2*1000);
						break;
					case 3:
						_room.burnSave[_id] = burner4[_roomId-1];
						burner4[_roomId-1].setNpcState(1);
						ThreadPoolManager.getInstance().schedule(new StartBurn(_roomId, _id+1), 3*1000);
						ThreadPoolManager.getInstance().schedule(new OffBurn(burner4[_roomId-1]), 2*1000);
						break;
					case 4:
						_room.burnSave[_id] = burner5[_roomId-1];
						burner5[_roomId-1].setNpcState(1);
						ThreadPoolManager.getInstance().schedule(new StartBurn(_roomId, _id+1), 3*1000);
						ThreadPoolManager.getInstance().schedule(new OffBurn(burner5[_roomId-1]), 2*1000);
						break;
					case 5:
						_room.burnSave[_id] = burner6[_roomId-1];
						burner6[_roomId-1].setNpcState(1);
						ThreadPoolManager.getInstance().schedule(new StartBurn(_roomId, _id+1), 3*1000);
						ThreadPoolManager.getInstance().schedule(new OffBurn(burner6[_roomId-1]), 2*1000);
						break;
					case 6:
						_room.burnSave[_id] = burner7[_roomId-1];
						burner7[_roomId-1].setNpcState(1);
						ThreadPoolManager.getInstance().schedule(new StartBurn(_roomId, _id+1), 3*1000);
						ThreadPoolManager.getInstance().schedule(new OffBurn(burner7[_roomId-1]), 2*1000);
						break;
					case 7:
						_room.burnSave[_id] = burner8[_roomId-1];
						burner8[_roomId-1].setNpcState(1);
						ThreadPoolManager.getInstance().schedule(new StartBurn(_roomId, _id+1), 3*1000);
						ThreadPoolManager.getInstance().schedule(new OffBurn(burner8[_roomId-1]), 2*1000);
						break;
					case 8:
						_room.burnSave[_id] = burner9[_roomId-1];
						burner9[_roomId-1].setNpcState(1);
						ThreadPoolManager.getInstance().schedule(new StartBurn(_roomId, _id+1), 3*1000);
						ThreadPoolManager.getInstance().schedule(new OffBurn(burner9[_roomId-1]), 2*1000);
						break;
				}
			}
			else
				ThreadPoolManager.getInstance().schedule(new PC_TURN(_room._npc), 3*1000);
		}

		public StartBurn(int room, int id)
		{
			_id = id;
			_roomId = room;
		}
	}

	private class OffBurn extends l2open.common.RunnableImpl
	{
		private L2NpcInstance _npc = null;

		@Override
		public void runImpl()
		{
			_npc.setNpcState(2);
		}

		public OffBurn(L2NpcInstance npc)
		{
			_npc = npc;
		}
	}

	private class PC_TURN extends l2open.common.RunnableImpl
	{
		private L2NpcInstance _npc = null;
		private Room _room = null;

		@Override
		public void runImpl()
		{
			_room = getRoom(_npc.getAI().ROOM_ID);
			_room._playGame = true;
			NpcSay cs = new NpcSay(_npc, Say2C.NPC_ALL, 60003);
			sendPacketToPlayer(cs, _npc);
		}

		public PC_TURN(L2NpcInstance npc)
		{
			_npc = npc;
		}
	}

	private class GAME_TIME extends l2open.common.RunnableImpl
	{
		private int _roomId = 0;
		private Room _room = null;

		@Override
		public void runImpl()
		{
			_room = getRoom(_roomId);
			_room._matchEnd = true;
		}

		public GAME_TIME(int id)
		{
			_roomId = id;
		}
	}

	private class HURRY_UP extends l2open.common.RunnableImpl
	{
		private L2NpcInstance _npc = null;
		private int _id = -1;

		@Override
		public void runImpl()
		{
			long roomId = _npc.getAI().ROOM_ID;
			NpcSay cs = null;
			switch(_id)
			{
				case 1:
					cs = new NpcSay(_npc, Say2C.NPC_ALL, 60001);
					getRoom(roomId)._hurryUp = ThreadPoolManager.getInstance().schedule(new HURRY_UP(_npc, 2), 60*1000);
					break;
				case 2:
					cs = new NpcSay(_npc, Say2C.NPC_ALL, 60002);
					endGame(getRoom(roomId), _npc, 10*1000);
					break;
			}
			
			sendPacketToPlayer(cs, _npc);
		}

		public HURRY_UP(L2NpcInstance npc, int id)
		{
			_id = id;
			_npc = npc;
		}
	}

	public void endGame(Room room, L2NpcInstance npc, long time)
	{
		if(room._gameTimeExpired != null)
		{
			room._gameTimeExpired.cancel(true);
			room._gameTimeExpired = null;
		}
		ThreadPoolManager.getInstance().schedule(new GAME_TIME_EXPIRED(npc), time);
	}

	private class GAME_TIME_EXPIRED extends l2open.common.RunnableImpl
	{
		private L2NpcInstance _npc = null;
		private Room _room = null;

		@Override
		public void runImpl()
		{
			_room = getRoom(_npc.getAI().ROOM_ID);
			_room._freeGame = true;
			_room._freeGameTime = 0;
			_room._playGame = false;

			NpcSay cs = new NpcSay(_npc, Say2C.NPC_ALL, 60004);
			sendPacketToPlayer(cs, _npc);

			burner1[_room._roomId-1].deleteMe();
			burner2[_room._roomId-1].deleteMe();
			burner3[_room._roomId-1].deleteMe();
			burner4[_room._roomId-1].deleteMe();
			burner5[_room._roomId-1].deleteMe();
			burner6[_room._roomId-1].deleteMe();
			burner7[_room._roomId-1].deleteMe();
			burner8[_room._roomId-1].deleteMe();
			burner9[_room._roomId-1].deleteMe();

			_room._matchEnd = true; // Игра окончена...
			if(_room != null && _room._player != null)
			{
				L2ItemInstance item = _room._player.getInventory().getItemByItemId(15485);
				if(item != null)
					_room._player.getInventory().destroyItem(item, item.getCount(), false); // Удаляем факел по окончанию игры...
				_room._player.setVar("MonasteryOfSilenceGameReuse", String.valueOf(System.currentTimeMillis() + ConfigValue.MonasteryOfSilenceGameReuse * 60 * 1000));
			}
			playId.remove(_room._objectId); // Удаляем информацию о этапе игры...
		}

		public GAME_TIME_EXPIRED(L2NpcInstance npc)
		{
			_npc = npc;
		}
	}

	public void setTurn(final L2NpcInstance npc, final L2Player player)
	{
		long roomId = npc.getAI().ROOM_ID;
		Room _room = getRoom(roomId);
		if(_room._objectId == player.getObjectId() && _room.burnTurnCount < 9)
		{
			_room.burnTurn[_room.burnTurnCount] = npc;
			_room.burnTurnCount++;
			npc.setNpcState(1);
			ThreadPoolManager.getInstance().schedule(new OffBurn(npc), 2*1000);
		}
		if(_room.burnTurnCount == 9)
		{
			final Room room = _room;
			ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					long objectId = player.getObjectId();
					// Деспауним котлы...
					burner1[room._roomId-1].deleteMe();
					burner2[room._roomId-1].deleteMe();
					burner3[room._roomId-1].deleteMe();
					burner4[room._roomId-1].deleteMe();
					burner5[room._roomId-1].deleteMe();
					burner6[room._roomId-1].deleteMe();
					burner7[room._roomId-1].deleteMe();
					burner8[room._roomId-1].deleteMe();
					burner9[room._roomId-1].deleteMe();

					boolean isWin = true;
					for(int i=0;i<9;i++)
					{
						if(room.burnTurn[i] != room.burnSave[i])
						{
							isWin = false;
							room.burnTurnCount = 0;
							break;
						}
					}
					if(isWin)
					{
						NpcSay cs = new NpcSay(room._npc, Say2C.NPC_ALL, 60005);
						sendPacketToPlayer(cs, room._npc);
						room._matchEnd = true; // Игра окончена...
						L2ItemInstance item = player.getInventory().getItemByItemId(15485);
						if(item != null)
							player.getInventory().destroyItem(item, item.getCount(), false); // Удаляем факел по окончанию игры...
						playId.remove(objectId); // Удаляем информацию о этапе игры...
						room.burnTurnCount = 0;
						room._player = null;
						room._objectId = 0;
						// Останавливаем таймеры...
						if(room._hurryUp != null)
						{
							room._hurryUp.cancel(false);
							room._hurryUp = null;
						}
						if(room._gameTimeExpired != null)
						{
							room._gameTimeExpired.cancel(false);
							room._gameTimeExpired = null;
						}
						// Спауним сундук...
						try
						{
							L2Spawn spawn = new L2Spawn(NpcTable.getTemplate(18934));
							spawn.setRespawnDelay(0, 0);
							spawn.setLoc(getLocVar(room._roomId));
							spawn.doSpawn(true);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						// TODO: обнулить все...
					}
					else
					{
						if(gameCount(player) < 3)
						{
							NpcSay cs = new NpcSay(room._npc, Say2C.NPC_ALL, 60006);
							sendPacketToPlayer(cs, room._npc);
							room._matchEnd = true; // Игра окончена...
						}
						else
						{
							NpcSay cs = new NpcSay(room._npc, Say2C.NPC_ALL, 60007);
							sendPacketToPlayer(cs, room._npc);
							room._matchEnd = true; // Игра окончена...
							L2ItemInstance item = player.getInventory().getItemByItemId(15485);
							player.getInventory().destroyItem(item, item.getCount(), false); // Удаляем факел по окончанию игры...
							playId.remove(objectId); // Удаляем информацию о этапе игры...
							room._player = null;
							room._objectId = 0;
							// Останавливаем таймеры...
							if(room._hurryUp != null)
							{
								room._hurryUp.cancel(false);
								room._hurryUp = null;
							}
							if(room._gameTimeExpired != null)
							{
								room._gameTimeExpired.cancel(false);
								room._gameTimeExpired = null;
							}
						}
					}
				}
			}, 3200);
			_room._freeGameTime = 0;
			_room._freeGame = true; // когда игрок использует все 9 попыток то ставим опять true
			_room._playGame = false; // когда игрок использует все 9 попыток то ставим опять false
		}
	}

	private L2NpcInstance spawnBurner(int number, int roomId, Location loc)
	{
		L2NpcInstance npc = null;
		L2Spawn spawn = null;
		StatsSet npcDat = null;
		L2NpcTemplate template = null;
		lock.lock();
		try
		{
			template = NpcTable.getTemplate(18913);
			npcDat = template.getSet();
			template.setSet(npcDat);

			spawn = new L2Spawn(template);
			spawn.setAmount(1);
			spawn.setRespawnDelay(0, 0);
			spawn.setAIParam("POT_NUMBER="+number+";ROOM_ID="+roomId);
			spawn.setLoc(loc);
			npc = spawn.doSpawn(true);
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

	public static Location getLocVar(int id)
	{
		switch(id)
		{
			case 1:
				return new Location(110772, -82063, -1584);
			case 2:
				return new Location(114915, -70998, -544);
		}
		return null;
	}

	public static void sendPacketToPlayer(L2GameServerPacket packets, L2NpcInstance npc)
	{
		long roomId = npc.getAI().ROOM_ID;
		Room _room = getRoom(roomId);
		if(_room != null && _room._player != null)
			_room._player.sendPacket(packets);
	}

	public void onLoad()
	{
		_log.info("MonasteryOfSilenceGame: Loaded");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}