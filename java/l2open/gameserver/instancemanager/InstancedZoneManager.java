package l2open.gameserver.instancemanager;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2CommandChannel;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2Territory;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.TerritoryTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class InstancedZoneManager {
    private static Logger _log = Logger.getLogger(InstancedZoneManager.class.getName());
    private static InstancedZoneManager _instance;
    private FastMap<Integer, FastMap<Integer, InstancedZone>> _instancedZones = new FastMap<Integer, FastMap<Integer, InstancedZone>>().setShared(true);
    private static GArray<String> _names = new GArray<String>();
    private static GArray<Integer> _ids = new GArray<Integer>();
    private static HashMap<Integer, String> _idForName = new HashMap<Integer, String>();

    public static InstancedZoneManager getInstance() {
        if (_instance == null)
            _instance = new InstancedZoneManager();

        return _instance;
    }

    public InstancedZoneManager() {
        load();
    }

    public FastMap<Integer, InstancedZone> getById(Integer id) {
        return _instancedZones.get(id);
    }

    /**
     * Возвращает сброс реюза в виде Crontab
     */
    private int getResetReuseByName(String name, long time) {
        for (FastMap<Integer, InstancedZone> ils : _instancedZones.values()) {
            if (ils == null)
                continue;
            InstancedZone il = ils.get(0);
            if (il.getName().equals(name)) {
                if (il.getReuseTime() > -1)
                    return (int) Math.max((il.getReuseTime() * 60000 + time - System.currentTimeMillis()) / 60000, 0);

                if (il.getResetReuse() != null)
                    return (int) Math.max((il.getResetReuse().timeNextUsage(time) - System.currentTimeMillis()) / 60000, 0);
            }
        }
        return 0;
    }

    /**
     * Возвращает сброс реюза в виде Crontab
     */
    private int getResetReuseByName(Integer id, long time) {
        for (FastMap<Integer, InstancedZone> ils : _instancedZones.values()) {
            if (ils == null)
                continue;
            InstancedZone il = ils.get(0);
            if (il.getId() == id) {
                if (il.getReuseTime() > -1)
                    return (int) Math.max((il.getReuseTime() * 60000 + time - System.currentTimeMillis()) / 60000, 0);

                if (il.getResetReuse() != null)
                    return (int) Math.max((il.getResetReuse().timeNextUsage(time) - System.currentTimeMillis()) / 60000, 0);
            }
        }
        return 0;
    }

    /**
     * Возвращает время в минутах до следующего входа в указанный инстанс.
     */
    public int getTimeToNextEnterInstance(String name, L2Player player) {
        //if(player.isGM())
        //	return 0;
        String var = player.getVar(name);
        if (var == null)
            return 0;

        return getResetReuseByName(name, Long.parseLong(var));
    }

    /**
     * Возвращает время в минутах до следующего входа в указанный инстанс.
     */
    public int getTimeToNextEnterInstance(Integer id, L2Player player) {
        //if(player.isGM())
        //	return 0;
        String var = null;
        try {
            var = player.getVar(getName(id));
        } catch (NullPointerException e) {
        }
        if (var == null)
            return 0;

        return getResetReuseByName(getName(id), Long.parseLong(var));
    }

    /**
     * Возвращает массив униканых имен инстансов
     */
    public GArray<String> getNames() {
        return _names;
    }

    /**
     * Возвращает массив униканых ID инстансов
     */
    public GArray<Integer> getIds() {
        return _ids;
    }

    public String getName(Integer id) {
        return _idForName.get(id);
    }

    public void load() {
        int countGood = 0, countBad = 0;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);

            File file;

            if (ConfigValue.develop) {
                file = new File("data/xml/instances.xml");
            } else {
                file = new File(ConfigValue.DatapackRoot + "/data/xml/instances.xml");
            }


            if (!file.exists())
                throw new IOException();

            Document doc = factory.newDocumentBuilder().parse(file);
            NamedNodeMap attrs;
            Integer instanceId;
            String name;
            Crontab resetReuse = new Crontab("0 0 * * *"); // Сброс реюза по умолчанию в каждые сутки в 0:00
            int timelimit = -1;
            boolean dispellBuffs = true;
            Integer roomId;
            int mobId, doorId, respawn, respawnRnd, count;
            // 0 - точечный, в каждой указанной точке; 1 - один точечный спаун в рандомной точке; 2 - локационный; 3 - спаун в рандомной локе
            int spawnType = 0;
            L2Spawn spawnDat = null;
            L2NpcTemplate template;
            L2DoorInstance door;
            int mapx = -1;
            int mapy = -1;

            InstancedZone instancedZone;

            for (Node iz = doc.getFirstChild(); iz != null; iz = iz.getNextSibling())
                if ("list".equalsIgnoreCase(iz.getNodeName())) {
                    for (Node area = iz.getFirstChild(); area != null; area = area.getNextSibling())
                        if ("instance".equalsIgnoreCase(area.getNodeName())) {
                            int reuseTime = -1;

                            attrs = area.getAttributes();
                            instanceId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                            name = attrs.getNamedItem("name").getNodeValue();
                            if (!_names.contains(name))
                                _names.add(name);
                            if (!_ids.contains(instanceId))
                                _ids.add(instanceId);

                            if (!_idForName.containsKey(instanceId))
                                _idForName.put(instanceId, name);
                            if (attrs.getNamedItem("reuseTime") != null)
                                reuseTime = Integer.parseInt(attrs.getNamedItem("reuseTime").getNodeValue());
                            resetReuse = new Crontab(attrs.getNamedItem("resetReuse").getNodeValue());

                            Node nodeTimelimit = attrs.getNamedItem("timelimit");
                            if (nodeTimelimit != null)
                                timelimit = Integer.parseInt(nodeTimelimit.getNodeValue());

                            Node nodeDispellBuffs = attrs.getNamedItem("dispellBuffs");
                            dispellBuffs = nodeDispellBuffs == null || Boolean.parseBoolean(nodeDispellBuffs.getNodeValue());

                            int minLevel = 0, maxLevel = 0, minParty = 1, maxParty = 9;
                            Location tele = new Location();
                            Location ret = new Location();

                            for (Node room = area.getFirstChild(); room != null; room = room.getNextSibling())
                                if ("level".equalsIgnoreCase(room.getNodeName())) {
                                    attrs = room.getAttributes();
                                    minLevel = Integer.parseInt(attrs.getNamedItem("min").getNodeValue());
                                    maxLevel = Integer.parseInt(attrs.getNamedItem("max").getNodeValue());
                                }

                            for (Node room = area.getFirstChild(); room != null; room = room.getNextSibling())
                                if ("party".equalsIgnoreCase(room.getNodeName())) {
                                    attrs = room.getAttributes();
                                    minParty = Integer.parseInt(attrs.getNamedItem("min").getNodeValue());
                                    maxParty = Integer.parseInt(attrs.getNamedItem("max").getNodeValue());

                                    switch (instanceId){
                                        case 139: {
                                            minParty = ConfigValue.FreyaMinPlayers;
                                            break;
                                        }
                                        case 144:{
                                            minParty = ConfigValue.FreyaHardMinPlayers;
                                            break;
                                        }
                                        case 136:{
                                            minParty = ConfigValue.FrintezzaMinPlayers;
                                            break;
                                        }
                                        case 114:{
                                            minParty = ConfigValue.ZakenNightMinPlayers;
                                            break;
                                        }
                                        case 133:{
                                            minParty = ConfigValue.ZakenMinPlayers;
                                            break;
                                        }
                                        case 135:{
                                            minParty = ConfigValue.ZakenHardMinPlayers;
                                            break;
                                        }
                                    }

                                }

                            for (Node room = area.getFirstChild(); room != null; room = room.getNextSibling())
                                if ("return".equalsIgnoreCase(room.getNodeName()))
                                    ret = new Location(room.getAttributes().getNamedItem("loc").getNodeValue());

                            for (Node room = area.getFirstChild(); room != null; room = room.getNextSibling())
                                if ("geodata".equalsIgnoreCase(room.getNodeName())) {
                                    String[] rxy = room.getAttributes().getNamedItem("map").getNodeValue().split("_");
                                    mapx = Integer.parseInt(rxy[0]);
                                    mapy = Integer.parseInt(rxy[1]);
                                }
                            for (Node room = area.getFirstChild(); room != null; room = room.getNextSibling())
                                if ("location".equalsIgnoreCase(room.getNodeName())) {
                                    attrs = room.getAttributes();
                                    roomId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());

                                    for (Node coord = room.getFirstChild(); coord != null; coord = coord.getNextSibling())
                                        if ("teleport".equalsIgnoreCase(coord.getNodeName()))
                                            tele = new Location(coord.getAttributes().getNamedItem("loc").getNodeValue());

                                    if (!_instancedZones.containsKey(instanceId))
                                        _instancedZones.put(instanceId, new FastMap<Integer, InstancedZone>().setShared(true));

                                    instancedZone = new InstancedZone(instanceId, name, resetReuse, reuseTime, timelimit, minLevel, maxLevel, minParty, maxParty, tele, ret);
                                    instancedZone.setDispellBuffs(dispellBuffs);
                                    instancedZone.setMapX(mapx);
                                    instancedZone.setMapY(mapy);

                                    _instancedZones.get(instanceId).put(roomId, instancedZone);

                                    for (Node spawn = room.getFirstChild(); spawn != null; spawn = spawn.getNextSibling())
                                        if ("spawn".equalsIgnoreCase(spawn.getNodeName())) {
                                            attrs = spawn.getAttributes();
                                            String[] mobs = attrs.getNamedItem("mobId").getNodeValue().split(" ");

                                            Node respawnNode = attrs.getNamedItem("respawn");
                                            respawn = respawnNode != null ? Integer.parseInt(respawnNode.getNodeValue()) : 0;

                                            Node respawnRndNode = attrs.getNamedItem("respawnRnd");
                                            respawnRnd = respawnRndNode != null ? Integer.parseInt(respawnRndNode.getNodeValue()) : 0;

                                            Node countNode = attrs.getNamedItem("count");
                                            count = countNode != null ? Integer.parseInt(countNode.getNodeValue()) : 1;

                                            Node spawnTypeNode = attrs.getNamedItem("type");
                                            if (spawnTypeNode == null || spawnTypeNode.getNodeValue().equalsIgnoreCase("point"))
                                                spawnType = 0;
                                            else if (spawnTypeNode.getNodeValue().equalsIgnoreCase("rnd"))
                                                spawnType = 1;
                                            else if (spawnTypeNode.getNodeValue().equalsIgnoreCase("loc"))
                                                spawnType = 2;
                                            else if (spawnTypeNode.getNodeValue().equalsIgnoreCase("rndloc")) {
                                                spawnType = 3;
                                            } else {
                                                spawnType = 0;
                                                _log.warning("Spawn type  '" + spawnTypeNode.getNodeValue() + "' is unknown!");
                                            }

                                            int locId = IdFactory.getInstance().getNextId();
                                            L2Territory territory = new L2Territory(locId);
                                            for (Node location = spawn.getFirstChild(); location != null; location = location.getNextSibling())
                                                if ("coords".equalsIgnoreCase(location.getNodeName()))
                                                    territory.add(new Location(location.getAttributes().getNamedItem("loc").getNodeValue()));
                                            if (spawnType == 2) //точечный спавн не проверять
                                                territory.validate();
                                            List<Integer> ids = new ArrayList<Integer>();
                                            if (spawnType == 3) // рандомная лока
                                            {
                                                for (Node location = spawn.getFirstChild(); location != null; location = location.getNextSibling())
                                                    if ("locs".equalsIgnoreCase(location.getNodeName())) {
                                                        ids.add(Integer.parseInt(location.getAttributes().getNamedItem("id").getNodeValue()));
                                                    }
                                            }
                                            TerritoryTable.getInstance().getLocations().put(locId, territory);
                                            L2World.addTerritory(territory);

                                            for (String mob : mobs) {
                                                mobId = Integer.parseInt(mob);
                                                template = NpcTable.getTemplate(mobId);
                                                if (template == null)
                                                    _log.warning("Template " + mobId + " not found!");

                                                if (template != null && _instancedZones.containsKey(instanceId) && _instancedZones.get(instanceId).containsKey(roomId)) {
                                                    spawnDat = new L2Spawn(template);
                                                    if (spawnType == 3) {
                                                        locId = ids.get(Rnd.get(ids.size()));
                                                        spawnDat.setLocation(locId);
                                                    } else
                                                        spawnDat.setLocation(locId);
                                                    spawnDat.setRespawnDelay(respawn, respawnRnd);
                                                    spawnDat.setAmount(count);
                                                    if (respawn > 0)
                                                        spawnDat.startRespawn();

                                                    _instancedZones.get(instanceId).get(roomId).getSpawnsInfo().add(new SpawnInfo(locId, spawnDat, spawnType));

                                                    countGood++;

                                                    try {
                                                        for (Node event = spawn.getFirstChild(); event != null; event = event.getNextSibling())
                                                            if ("event".equalsIgnoreCase(event.getNodeName())) {
                                                                attrs = event.getAttributes();
                                                                String trigger = attrs.getNamedItem("trigger").getNodeValue();
                                                                GArray<String> pars = new GArray<String>();
                                                                for (Node param = event.getFirstChild(); param != null; param = param.getNextSibling())
                                                                    if ("param".equalsIgnoreCase(param.getNodeName()))
                                                                        pars.add(param.getAttributes().getNamedItem("value").getNodeValue());
                                                                String cl = attrs.getNamedItem("class").getNodeValue();
                                                                String me = attrs.getNamedItem("method").getNodeValue();
                                                                Integer del = Integer.parseInt(attrs.getNamedItem("delay").getNodeValue());
                                                                String[] param = pars.toArray(new String[pars.size()]);
                                                                SchedulableEvent se = new SchedulableEvent(cl, me, param, del);
                                                                if (spawnDat._events == null)
                                                                    spawnDat._events = new HashMap<String, GArray<SchedulableEvent>>();
                                                                GArray<SchedulableEvent> arr = spawnDat._events.get(trigger);
                                                                if (arr == null) {
                                                                    arr = new GArray<SchedulableEvent>();
                                                                    spawnDat._events.put(trigger, arr);
                                                                }
                                                                arr.add(se);
                                                            }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                } else
                                                    countBad++;
                                            }
                                        }
                                }

                            for (Node room = area.getFirstChild(); room != null; room = room.getNextSibling())
                                if ("door".equalsIgnoreCase(room.getNodeName())) {
                                    attrs = room.getAttributes();
                                    doorId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                                    Node openedNode = attrs.getNamedItem("opened");
                                    boolean opened = openedNode == null ? false : Boolean.parseBoolean(openedNode.getNodeValue());
                                    Node invulNode = attrs.getNamedItem("invul");
                                    boolean invul = invulNode == null ? true : Boolean.parseBoolean(invulNode.getNodeValue());

                                    L2DoorInstance newDoor = DoorTable.getInstance().getDoor(doorId);
                                    if (newDoor == null)
                                        _log.warning("Door " + doorId + " not found!");
                                    else {
                                        door = newDoor.clone();
                                        door.setOpen(opened);
                                        door.setHPVisible(!invul);
                                        door.setIsInvul(invul);
                                        if (!_instancedZones.get(instanceId).get(0).getDoors().add(door))
                                            _log.warning("Failed to add door " + doorId + " for instanced zone " + instanceId);
                                    }
                                }
                        }
                }
        } catch (Exception e) {
            _log.warning("Error on loading instanced spawns:");
            e.printStackTrace();
        }
        int locSize = _instancedZones.keySet().size();
        int roomSize = 0;

        for (Integer b : _instancedZones.keySet())
            roomSize += _instancedZones.get(b).keySet().size();

        _log.info("InstancedZoneManager: Loaded " + locSize + " zones with " + roomSize + " rooms.");
        _log.info("InstancedZoneManager: Loaded " + countGood + " instanced location spawns, " + countBad + " errors.");
    }

    public void reload() {
        _instancedZones.clear();
        _instance = null;
    }

    public class InstancedZone {
        private final String _name;
        private final int _instanceId;
        private final Crontab _resetReuse;
        private final int _reuseTime;
        private final int _timelimit;
        private boolean _dispellBuffs;
        private final int _minLevel;
        private final int _maxLevel;
        private final int _minParty;
        private final int _maxParty;
        private final Location _teleportCoords;
        private final Location _returnCoords;
        private final GArray<L2DoorInstance> _doors;
        private final GArray<SpawnInfo> _spawnsInfo;

        private int _mapx;
        private int _mapy;

        public InstancedZone(int instanceId, String name, Crontab resetReuse, int reuseTime, int timelimit, int minLevel, int maxLevel, int minParty, int maxParty, Location tele, Location ret) {
            _instanceId = instanceId;
            _name = name;
            _resetReuse = resetReuse;
            _reuseTime = reuseTime;
            _timelimit = timelimit;
            _dispellBuffs = true;
            _minLevel = minLevel;
            _maxLevel = maxLevel;
            _teleportCoords = tele;
            _returnCoords = ret;
            _doors = new GArray<L2DoorInstance>();
            _minParty = minParty;
            _maxParty = maxParty;
            _spawnsInfo = new GArray<SpawnInfo>();
        }

        public int getMapX() {
            return _mapx;
        }

        public int getMapY() {
            return _mapy;
        }

        public void setMapX(int mapx) {
            _mapx = mapx;
        }

        public void setMapY(int mapy) {
            _mapy = mapy;
        }

        public String getName() {
            return _name;
        }

        public int getId() {
            return _instanceId;
        }

        public Crontab getResetReuse() {
            return _resetReuse;
        }

        public int getReuseTime() {
            return _reuseTime;
        }

        public boolean isDispellBuffs() {
            return _dispellBuffs;
        }

        public void setDispellBuffs(boolean val) {
            _dispellBuffs = val;
        }

        public int getTimelimit() {
            return _timelimit;
        }

        public int getMinLevel() {
            return _minLevel;
        }

        public int getMaxLevel() {
            return _maxLevel;
        }

        public int getMinParty() {
            return _minParty;
        }

        public int getMaxParty() {
            return _maxParty;
        }

        public Location getTeleportCoords() {
            return _teleportCoords;
        }

        public Location getReturnCoords() {
            return _returnCoords;
        }

        public GArray<L2DoorInstance> getDoors() {
            return _doors;
        }

        public GArray<SpawnInfo> getSpawnsInfo() {
            return _spawnsInfo;
        }

        private int memberCount(L2Player player) {
            L2Party party = player.getParty();
            if (party != null) {
                if (party.isInCommandChannel() && party.getCommandChannel().getChannelLeader() == player)
                    return party.getCommandChannel().getMemberCount();
                if (party.getPartyLeader() == player)
                    return party.getMemberCount();
            }
            return 1;
        }

        public boolean tryToReenter(L2Player player) {
            Reflection ref = ReflectionTable.getInstance().playerFindReflection(Integer.valueOf(player.getObjectId()));
            if (ref == null)
                return false;
            if (ref.getInstancedZone() != null) {
                if (!equals(ref.getInstancedZone())) {
                    player.sendPacket(new SystemMessage(2105));
                    return true;
                }
            } else if (ref.getName() != getName()) {
                player.sendPacket(new SystemMessage(2105));
                return true;
            }
            if (player.isCursedWeaponEquipped()) {
                player.sendPacket(new SystemMessage(2098).addName(player));
                return true;
            }
            if (getMinParty() > 1 && !player.isInParty()) {
                player.sendPacket(new SystemMessage(2101));
                return true;
            }
            if (player.getLevel() < getMinLevel() || player.getLevel() > getMaxLevel()) {
                player.sendPacket(new SystemMessage(2097).addName(player));
                return true;
            }
            player.setVar("backCoords", ref.getReturnLoc().toXYZString());
            if (ref.getInstancedZone() != null)
                player.teleToLocation(ref.getInstancedZone().getTeleportCoords(), ref.getId());
            else
                player.teleToLocation(ref.getTeleportLoc(), ref.getId());
            if (isDispellBuffs()) {
                dispellBuffs(player);
                dispellBuffs(player.getPet());
            }
            return true;
        }

        private void dispellBuffs(L2Playable playable) {
            if (playable != null) {
                boolean update = false;
                for (L2Effect effect : playable.getEffectList().getAllEffects()) {
                    if (effect.getEffectType() == EffectType.Vitality)
                        continue;
                    if (!effect.getSkill().isOffensive() && !effect.getSkill().getName().startsWith("Adventurer's ")) {
                        update = true;
                        effect.exit(false, false);
                    }
                }
                if (update)
                    playable.updateEffectIcons();
            }
        }
    }

    public class SpawnInfo {
        private final int _locationId;
        private final L2Spawn _spawn;
        private final int _type;

        public SpawnInfo(int locationId, L2Spawn spawn, int type) {
            _locationId = locationId;
            _spawn = spawn;
            _type = type;
        }

        public int getLocationId() {
            return _locationId;
        }

        public L2Spawn getSpawn() {
            return _spawn;
        }

        public int getType() {
            return _type;
        }
    }

    public static SystemMessage checkCondition(int cond, L2Player player, boolean bools, String text, String quest) {
        InstancedZoneManager zone = getInstance();
        if (zone == null)
            return Msg.SYSTEM_ERROR;
        FastMap<Integer, InstancedZone> izs = zone.getById(cond);
        if (izs == null)
            return Msg.SYSTEM_ERROR;
        InstancedZone iz = izs.get(0);
        if (iz == null)
            return Msg.SYSTEM_ERROR;

        String name = iz.getName();
        int min_level = iz.getMinLevel();
        int max_level = iz.getMaxLevel();
        int minMembers = iz.getMinParty();
        int maxMembers = iz.getMaxParty();

        boolean bool = bools;// iz.isNeedChannel();
        int min = minMembers > 1 && !bool ? 1 : 0;
        if (bool && zone.getTimeToNextEnterInstance(name, player) > 0)
            return new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player);
        if (min != 0) {
            if (!player.isInParty())
                return Msg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER;
            if (!player.getParty().isLeader(player))
                return Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER;
            if (player.getParty().getMemberCount() < minMembers || player.getParty().getMemberCount() > maxMembers)
                return Msg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT;
            for (L2Player member : player.getParty().getPartyMembers()) {
                SystemMessage messeg;
                if (quest != null && quest.isEmpty()) {
                    QuestState state = member.getQuestState(quest);
                    if (state == null || state.getState() != 2 || !state.isCompleted())
                        return new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member);
                }
                if (member.getLevel() < min_level || member.getLevel() > max_level) {
                    messeg = new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member);
                    member.sendPacket(messeg);
                    return messeg;
                }
                if (member.isCursedWeaponEquipped() || member.isInFlyingTransform() || member.isDead() || member.isCombatFlagEquipped() || member.isTerritoryFlagEquipped())
                    return new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member);
                if (zone.getTimeToNextEnterInstance(name, member) > 0)
                    return new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(member);
                if (!player.isInRange(member, 500)) {
                    member.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
                    return Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK;
                }
            }
        } else if (bool) {
            if (player.getParty() == null)
                return Msg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER;
            if (player.getParty().getCommandChannel() == null)
                return Msg.YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL;
            L2CommandChannel channel = player.getParty().getCommandChannel();
            if (channel.getChannelLeader().getObjectId() != player.getObjectId())
                return Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER;
            if (channel.getMemberCount() < minMembers)
                return new SystemMessage("The command channel must contains at least " + minMembers + " members.");
            if (channel.getMemberCount() > maxMembers)
                return new SystemMessage("The command channel must contains not more than " + maxMembers + " members.");
            for (L2Player member : channel.getMembers()) {
                SystemMessage messeg;
                if (quest != null && quest.isEmpty()) {
                    QuestState state = member.getQuestState(quest);
                    if (state == null || state.getState() != 2 || !state.isCompleted())
                        return new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member);
                }
                SystemMessage localSystemMessage;
                if (member.getLevel() < min_level || member.getLevel() > max_level) {
                    messeg = new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member);
                    member.sendPacket(messeg);
                    return messeg;
                }
                if (member.isCursedWeaponEquipped() || member.isInFlyingTransform() || member.isDead() || member.isCombatFlagEquipped() || member.isTerritoryFlagEquipped())
                    return new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member);
                if (zone.getTimeToNextEnterInstance(name, member) > 0)
                    return new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(member);
                if (!player.isInRange(member, 500)) {
                    messeg = new SystemMessage(SystemMessage.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addName(member);
                    member.sendPacket(messeg);
                    return messeg;
                }
            }
        }
        return null;
    }
}