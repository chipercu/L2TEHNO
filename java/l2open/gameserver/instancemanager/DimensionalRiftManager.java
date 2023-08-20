package l2open.gameserver.instancemanager;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.DimensionalRift;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.TeleportToLocation;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.TerritoryTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class DimensionalRiftManager
{
	private static Logger _log = Logger.getLogger(DimensionalRiftManager.class.getName());
	private static DimensionalRiftManager _instance;
	private FastMap<Integer, FastMap<Integer, DimensionalRiftRoom>> _rooms = new FastMap<Integer, FastMap<Integer, DimensionalRiftRoom>>().setShared(true);
	private final static int DIMENSIONAL_FRAGMENT_ITEM_ID = 7079;

	public static DimensionalRiftManager getInstance()
	{
		if(_instance == null)
			_instance = new DimensionalRiftManager();

		return _instance;
	}

	public DimensionalRiftManager()
	{
		load();
	}

	public DimensionalRiftRoom getRoom(int type, int room)
	{
		return _rooms.get(type).get(room);
	}

	public FastMap<Integer, DimensionalRiftRoom> getRooms(int type)
	{
		return _rooms.get(type);
	}

	public void load()
	{
		int countGood = 0, countBad = 0;
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);

			File file = new File(ConfigValue.DatapackRoot + "/data/xml/dimensionalRift.xml");
			if(!file.exists())
				throw new IOException();

			Document doc = factory.newDocumentBuilder().parse(file);
			NamedNodeMap attrs;
			int type;
			int roomId;
			int mobId, delay, count;
			L2Spawn spawnDat;
			L2NpcTemplate template;
			Location tele = new Location();
			int xMin = 0, xMax = 0, yMin = 0, yMax = 0, zMin = 0, zMax = 0;
			boolean isBossRoom;

			for(Node rift = doc.getFirstChild(); rift != null; rift = rift.getNextSibling())
				if("rift".equalsIgnoreCase(rift.getNodeName()))
					for(Node area = rift.getFirstChild(); area != null; area = area.getNextSibling())
						if("area".equalsIgnoreCase(area.getNodeName()))
						{
							attrs = area.getAttributes();
							type = Integer.parseInt(attrs.getNamedItem("type").getNodeValue());

							for(Node room = area.getFirstChild(); room != null; room = room.getNextSibling())
								if("room".equalsIgnoreCase(room.getNodeName()))
								{
									attrs = room.getAttributes();
									roomId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
									Node boss = attrs.getNamedItem("isBossRoom");
									isBossRoom = boss != null ? Boolean.parseBoolean(boss.getNodeValue()) : false;

									for(Node coord = room.getFirstChild(); coord != null; coord = coord.getNextSibling())
										if("teleport".equalsIgnoreCase(coord.getNodeName()))
										{
											attrs = coord.getAttributes();
											tele = new Location(attrs.getNamedItem("loc").getNodeValue());
										}
										else if("zone".equalsIgnoreCase(coord.getNodeName()))
										{
											attrs = coord.getAttributes();
											xMin = Integer.parseInt(attrs.getNamedItem("xMin").getNodeValue());
											xMax = Integer.parseInt(attrs.getNamedItem("xMax").getNodeValue());
											yMin = Integer.parseInt(attrs.getNamedItem("yMin").getNodeValue());
											yMax = Integer.parseInt(attrs.getNamedItem("yMax").getNodeValue());
											zMin = Integer.parseInt(attrs.getNamedItem("zMin").getNodeValue());
											zMax = Integer.parseInt(attrs.getNamedItem("zMax").getNodeValue());
										}

									int loc_id = IdFactory.getInstance().getNextId();
									L2Territory territory = new L2Territory(loc_id);
									territory.add(xMin, yMin, zMin, zMax);
									territory.add(xMax, yMin, zMin, zMax);
									territory.add(xMax, yMax, zMin, zMax);
									territory.add(xMin, yMax, zMin, zMax);
									territory.validate();
									TerritoryTable.getInstance().getLocations().put(loc_id, territory);
									L2World.addTerritory(territory);

									if(!_rooms.containsKey(type))
										_rooms.put(type, new FastMap<Integer, DimensionalRiftRoom>().setShared(true));

									_rooms.get(type).put(roomId, new DimensionalRiftRoom(territory, tele, isBossRoom));

									for(Node spawn = room.getFirstChild(); spawn != null; spawn = spawn.getNextSibling())
										if("spawn".equalsIgnoreCase(spawn.getNodeName()))
										{
											attrs = spawn.getAttributes();
											mobId = Integer.parseInt(attrs.getNamedItem("mobId").getNodeValue());
											delay = Integer.parseInt(attrs.getNamedItem("delay").getNodeValue());
											count = Integer.parseInt(attrs.getNamedItem("count").getNodeValue());

											template = NpcTable.getTemplate(mobId);
											if(template == null)
												_log.warning("Template " + mobId + " not found!");
											if(!_rooms.containsKey(type))
												_log.warning("Type " + type + " not found!");
											else if(!_rooms.get(type).containsKey(roomId))
												_log.warning("Room " + roomId + " in Type " + type + " not found!");

											if(template != null && _rooms.containsKey(type) && _rooms.get(type).containsKey(roomId))
											{
												spawnDat = new L2Spawn(template);
												spawnDat.setLocation(loc_id);
												spawnDat.setHeading(-1);
												spawnDat.setRespawnDelay(delay);
												spawnDat.setAmount(count);
												if(delay > 0)
													spawnDat.startRespawn();
												_rooms.get(type).get(roomId).getSpawns().add(spawnDat);
												countGood++;
											}
											else
												countBad++;
										}
								}
						}
		}
		catch(Exception e)
		{
			_log.warning("Error on loading dimensional rift spawns:");
			e.printStackTrace();
		}
		int typeSize = _rooms.keySet().size();
		int roomSize = 0;

		for(int b : _rooms.keySet())
			roomSize += _rooms.get(b).keySet().size();

		_log.info("DimensionalRiftManager: Loaded " + typeSize + " room types with " + roomSize + " rooms.");
		_log.info("DimensionalRiftManager: Loaded " + countGood + " dimensional rift spawns, " + countBad + " errors.");
	}

	public void reload()
	{
		for(int b : _rooms.keySet())
			_rooms.get(b).clear();

		_rooms.clear();
		load();
	}

	public boolean checkIfInRiftZone(Location loc, boolean ignorePeaceZone)
	{
		if(ignorePeaceZone)
			return _rooms.get(0).get(1).checkIfInZone(loc);
		return _rooms.get(0).get(1).checkIfInZone(loc) && !_rooms.get(0).get(0).checkIfInZone(loc);
	}

	public boolean checkIfInPeaceZone(Location loc)
	{
		return _rooms.get(0).get(0).checkIfInZone(loc);
	}

	public void teleportToWaitingRoom(L2Player player)
	{
		teleToLocation(player, getRoom(0, 0).getTeleportCoords().rnd(0, 250, false), null);
	}

	public void start(L2Player player, int type, L2NpcInstance npc)
	{
		if(!player.isInParty())
		{
			showHtmlFile(player, "/data/html/rift/NoParty.htm", npc);
			return;
		}

		if(!player.isGM())
		{
			if(!player.getParty().isLeader(player))
			{
				showHtmlFile(player, "/data/html/rift/NotPartyLeader.htm", npc);
				return;
			}

			if(player.getParty().isInDimensionalRift())
			{
				showHtmlFile(player, "/data/html/rift/Cheater.htm", npc);

				if(!player.isGM())
					_log.warning("Player " + player.getName() + "(" + player.getObjectId() + ") was cheating in dimension rift area!");

				return;
			}

			if(player.getParty().getMemberCount() < ConfigValue.RiftMinPartySize)
			{
				showHtmlFile(player, "/data/html/rift/SmallParty.htm", npc);
				return;
			}

			for(L2Player p : player.getParty().getPartyMembers())
				if(!checkIfInPeaceZone(p.getLoc()))
				{
					showHtmlFile(player, "/data/html/rift/NotInWaitingRoom.htm", npc);
					return;
				}

			L2ItemInstance i;
			for(L2Player p : player.getParty().getPartyMembers())
			{
				i = p.getInventory().getItemByItemId(DIMENSIONAL_FRAGMENT_ITEM_ID);
				if(i == null || i.getCount() < getNeededItems(type))
				{
					showHtmlFile(player, "/data/html/rift/NoFragments.htm", npc);
					return;
				}
			}

			for(L2Player p : player.getParty().getPartyMembers())
				p.getInventory().destroyItemByItemId(DIMENSIONAL_FRAGMENT_ITEM_ID, getNeededItems(type), true);
		}

		new DimensionalRift(player.getParty(), type, Rnd.get(1, _rooms.get(type).size() - 1));
	}

	public class DimensionalRiftRoom
	{
		private final L2Territory _territory;
		private final Location _teleportCoords;
		private final boolean _isBossRoom;
		private final GArray<L2Spawn> _roomSpawns;

		public DimensionalRiftRoom(L2Territory territory, Location tele, boolean isBossRoom)
		{
			_territory = territory;
			_teleportCoords = tele;
			_isBossRoom = isBossRoom;
			_roomSpawns = new GArray<L2Spawn>();
		}

		public Location getTeleportCoords()
		{
			return _teleportCoords;
		}

		public boolean checkIfInZone(Location loc)
		{
			return checkIfInZone(loc.x, loc.y, loc.z);
		}

		public boolean checkIfInZone(int x, int y, int z)
		{
			return _territory.isInside(x, y, z);
		}

		public boolean isBossRoom()
		{
			return _isBossRoom;
		}

		public GArray<L2Spawn> getSpawns()
		{
			return _roomSpawns;
		}
	}

	private long getNeededItems(int type)
	{
		switch(type)
		{
			case 1:
				return ConfigValue.RecruitFC;
			case 2:
				return ConfigValue.SoldierFC;
			case 3:
				return ConfigValue.OfficerFC;
			case 4:
				return ConfigValue.CaptainFC;
			case 5:
				return ConfigValue.CommanderFC;
			case 6:
				return ConfigValue.HeroFC;
			default:
				return Long.MAX_VALUE;
		}
	}

	public void showHtmlFile(L2Player player, String file, L2NpcInstance npc)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, npc);
		html.setFile(file);
		html.replace("%t_name%", npc.getName());
		player.sendPacket(html);
	}

	public static void teleToLocation(L2Player player, Location loc, Reflection ref)
	{
		if(player.isFakeDeath())
			player.breakFakeDeath();

		if(player.isTeleporting() || player.isLogoutStarted())
			return;

		player.abortCast(true);

		player.clearHateList(true);

		//if(!player.isVehicle() && !player.isFlying() && !L2World.isWater(loc.x, loc.y, loc.z))
		//	loc.z = GeoEngine.getHeight(loc.x, loc.y, loc.z, ref.getGeoIndex());

		player.setTarget(null);

		player.setIsTeleporting(60000);

		if(player.isInVehicle())
			player.setVehicle(null);

		player.decayMe();

		player.setXYZInvisible(loc);
		if(ref == null)
			ref = ReflectionTable.getInstance().getDefault();
		player.setReflection(ref);

		// Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения" 
		player.setLastClientPosition(null);
		player.setLastServerPosition(null);
		player.sendPacket(new TeleportToLocation(player, loc, 0));
	}
}