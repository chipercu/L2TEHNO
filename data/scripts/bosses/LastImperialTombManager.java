package bosses;

import javolution.util.FastMap;
import l2open.common.*;
import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.*;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: unkown
 * @reauthor: Drizzy
 * @edit-date: 17.11.2011
 * @ Manager for mini-instance after spawn Frintezza. Rework for Freya Throne.
 */
public class LastImperialTombManager extends Quest implements ScriptFile {
    private static final int[] _room1Doors = {17130051, 17130052, 17130053, 17130054, 17130055, 17130056, 17130057, 17130058}; // 17130042
    private static final int[] _room2InsideDoors = {17130061, 17130062, 17130063, 17130064, 17130065, 17130066, 17130067, 17130068, 17130069, 17130070,};
    private static final int _room2OutsideDoor1 = 17130043;
    private static final int _room2OutsideDoor2 = 17130045;
    private static final int[] blockANpcs = {18329, 18330, 18331, 18333};
    private static final int[] blockBNpcs = {18334, 18335, 18336, 18337, 18338};
    private static final int _room3Door = 17130046;
    private static final int SCROLL = 8073;
    private static final int ALARM_DEVICE = 18328;
    private static final int CHOIR_CAPTAIN = 18334;
    private static L2Zone _zone;
    private static FastMap<Integer, List<Integer>> membersCC = new FastMap<Integer, List<Integer>>();
    public static FastMap<Integer, World> worlds = new FastMap<Integer, World>();
    private final ReentrantLock lock = new ReentrantLock();

    public LastImperialTombManager() {
        super("LastImperialTombManager", 0, 99995);
        addKillId(18334);
        addKillId(18328);
        addKillId(18339);
        addKillId(18329, 18330, 18331, 18334, 18335, 18336, 18337, 18338, 18339);

        addTalkId(32011);

    }

    private void init() {
        _zone = ZoneManager.getInstance().getZoneById(ZoneType.epic, 702121, false);
        LastImperialTombSpawnlist.clear();
        LastImperialTombSpawnlist.fill();
    }

    public class World {
        public GArray<L2NpcInstance> _darkChoirCaptains = new GArray<L2NpcInstance>();
        public GArray<L2NpcInstance> _room1Monsters = new GArray<L2NpcInstance>();
        public GArray<L2NpcInstance> _room2InsideMonsters = new GArray<L2NpcInstance>();
        public GArray<L2NpcInstance> _room2OutsideMonsters = new GArray<L2NpcInstance>();
        public ScheduledFuture<?> _Room1SpawnTask = null;
        public ScheduledFuture<?> _Room2InsideDoorOpenTask = null;
        public ScheduledFuture<?> _Room2OutsideSpawnTask = null;
        public ScheduledFuture<?> _monsterSpawnTask = null, _activityTimeEndTask = null, _intervalEndTask = null, _dieTask = null;
        public Reflection reflection;
        public L2NpcInstance frintezza, weakScarlet, strongScarlet, cube;
        public L2NpcInstance[] portraits = new L2NpcInstance[4];
        public L2NpcInstance[] demons = new L2NpcInstance[4];
        public int _scarletMorph = 0, scarlet_x, scarlet_y, scarlet_z, scarlet_h, scarlet_a;
        public L2NpcInstance _frintezzaDummy, overheadDummy, portraitDummy, portraitDummy1, portraitDummy3, scarletDummy;
        public L2Zone _zonefrintezza;
        public boolean canSpawn = false;
        public L2CommandChannel channel = null;

        public World(Reflection ref) {
            reflection = ref;
        }
    }

    public static void addWorld(int id, World world) {
        worlds.put(id, world);
    }

    public static World getWorld(int id) {
        World world = worlds.get(id);
        if (world != null)
            return world;
        return null;
    }

    public static Collection<World> getAllWorld() {
        return worlds.values();
    }

    @Override
    public String onEvent(String event, QuestState st, final L2NpcInstance npc) {
        if (event == null || st == null || npc == null)
            return "";
        if (event.equalsIgnoreCase("tryRegistration")) {
            tryRegistration(st.getPlayer(), st);
        }
        return null;
    }

    public void tryRegistration(L2Player player, QuestState st) {
        L2Party party = player.getParty();

        InstancedZoneManager izm = InstancedZoneManager.getInstance();
        FastMap<Integer, InstancedZoneManager.InstancedZone> izs = InstancedZoneManager.getInstance().getById(136);
        if (izs == null) {
            player.sendPacket(Msg.SYSTEM_ERROR);
            return;
        }
        InstancedZoneManager.InstancedZone iz = izs.get(0);
        if (iz == null) {
            player.sendPacket(Msg.SYSTEM_ERROR);
            return;
        }
        String name = iz.getName();
        int minMembers = iz.getMinParty();
        int maxMembers = iz.getMaxParty();
        int min_level = iz.getMinLevel();
        int max_level = iz.getMaxLevel();

        if (party == null) {
            player.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
            return;
        }

        // Если игрок тпанулся из инста(смерть, сое), возвращаем его в инстанс
        if (party.isInReflection()) {
            Reflection old_ref = party.getReflection();
            if (old_ref.getInstancedZoneId() != 136 || player.getActiveReflection() != old_ref) {
                player.sendMessage("Неправильно выбран инстанс");
                return;
            } else if (player.getLevel() < min_level || player.getLevel() > max_level) {
                player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
                return;
            } else if (player.isCursedWeaponEquipped() || player.isInFlyingTransform() || player.isDead()) {
                player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
                return;
            } else if (izm.getTimeToNextEnterInstance(name, player) > 0) {
                player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
                return;
            } else if (ConfigValue.FrintezzaHwidProtect)
                for (L2Player pl : old_ref.getPlayers())
                    if (pl != null && pl.getHWIDs().equals(player.getHWIDs())) {
                        player.sendMessage("В инстанс можно попасть только с одного ПК.");
                        return;
                    }

            player.setReflection(old_ref);
            player.teleToLocation(iz.getTeleportCoords(), old_ref.getId());
            return;
        }
        if (ConfigValue.FrintezzaOneEnter && worlds.size() > 1) {
            player.sendMessage("Рейд Босс Фринтеза уже занят.");
            return;
        }
        if (!ConfigValue.DEBUG_FRINTEZZA) {
            if (party.isInCommandChannel()) {
                L2CommandChannel cc = party.getCommandChannel();

                if (cc.getChannelLeader() != player) {
                    player.sendMessage("You must be leader of the command channel.");
                    return;
                } else if (cc.getMemberCount() < minMembers) {
                    player.sendMessage("The command channel must contains at least " + minMembers + " members.");
                    return;
                }

                List<String> _hwid = new ArrayList<String>(cc.getMemberCount());
                int count = 0;
                for (L2Player member : cc.getMembers()) {
                    if (member.getLevel() < min_level || member.getLevel() > max_level) {
                        player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
                        return;
                    } else if (member.isCursedWeaponEquipped() || member.isInFlyingTransform() || member.isDead()) {
                        player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
                        return;
                    } else if (!player.isInRange(member, 500)) {
                        member.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
                        player.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
                        return;
                    } else if (izm.getTimeToNextEnterInstance(name, member) > 0) {
                        cc.broadcastToChannelMembers(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(member));
                        return;
                    } else if (ConfigValue.FrintezzaHwidProtect) {
                        if (_hwid.contains(member.getHWIDs())) {
                            player.sendMessage("В инстанс можно попасть только с одного ПК. Игрок '" + member.getName() + "' пытается завести более одного окна.");
                            continue;
                        }
                        _hwid.add(member.getHWIDs());
                    }
                    count++;
                }
                if (ConfigValue.FrintezzaHwidProtect && count < minMembers) {
                    player.sendMessage("The command channel must contains at least " + minMembers + " members.");
                    return;
                } else if (count > maxMembers) {
                    player.sendMessage("The command channel must contains not more than " + maxMembers + " members.");
                    return;
                }
            } else if (!ConfigValue.FrintezzaNeedCommandChanel && minMembers <= 9) {
                if (!party.isLeader(player)) {
                    player.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER);
                    return;
                } else if (party.getMemberCount() < minMembers) {
                    player.sendMessage("The command channel must contains at least " + minMembers + " members.");
                    return;
                }

                List<String> _hwid = new ArrayList<String>(party.getMemberCount());
                int count = 0;
                for (L2Player member : party.getPartyMembers()) {
                    if (member.getLevel() < min_level || member.getLevel() > max_level) {
                        player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
                        return;
                    } else if (member.isCursedWeaponEquipped() || member.isInFlyingTransform() || member.isDead()) {
                        player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
                        return;
                    } else if (!player.isInRange(member, 500)) {
                        member.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
                        player.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
                        return;
                    } else if (izm.getTimeToNextEnterInstance(name, member) > 0) {
                        party.broadcastToPartyMembers(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(member));
                        return;
                    } else if (ConfigValue.FrintezzaHwidProtect) {
                        if (_hwid.contains(member.getHWIDs())) {
                            player.sendMessage("В инстанс можно попасть только с одного ПК. Игрок '" + member.getName() + "' пытается завести более одного окна.");
                            continue;
                        }
                        _hwid.add(member.getHWIDs());
                    }
                    count++;
                }
                if (ConfigValue.FrintezzaHwidProtect && count < minMembers) {
                    player.sendMessage("The command channel must contains at least " + minMembers + " members.");
                    return;
                } else if (count > maxMembers) {
                    player.sendMessage("The command channel must contains not more than " + maxMembers + " members.");
                    return;
                }
            } else {
                player.sendPacket(Msg.YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_IN_A_CURRENT_COMMAND_CHANNEL);
                return;
            }
        }
        if (player.getInventory().getCountOf(SCROLL) < 1) {
            player.sendMessage("You must possess a \"Frintezza's Magic Force Field Removal Scroll\".");
            return;
        }

        final Reflection r = new Reflection(name);
        r.setInstancedZoneId(136);
        for (InstancedZoneManager.InstancedZone i : izs.values()) {
            if (r.getTeleportLoc() == null) {
                r.setTeleportLoc(i.getTeleportCoords());
            }
            r.FillSpawns(i.getSpawnsInfo());
            r.FillDoors(i.getDoors());
        }
        World world = new World(r);
        lock.lock();
        try {
            addWorld(r.getId(), world);
        } finally {
            lock.unlock();
        }
		/*ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				worlds.remove(r.getId());
			}
		}, 7210000);*/
        r.startCollapseTimer(7205000);
        doInvade(st, r);
    }

    private synchronized void doInvade(QuestState st, final Reflection r) {
        if (st.getPlayer().getInventory().getCountOf(SCROLL) < 1) {
            st.getPlayer().sendMessage("You must possess a \"Frintezza's Magic Force Field Removal Scroll\".");
            return;
        }
        st.getPlayer().getInventory().destroyItemByItemId(SCROLL, 1, true);
        FastMap<Integer, InstancedZoneManager.InstancedZone> izs = InstancedZoneManager.getInstance().getById(136);
        if (izs == null) {
            st.getPlayer().sendPacket(Msg.SYSTEM_ERROR);
            return;
        }
        InstancedZoneManager.InstancedZone iz = izs.get(0);
        if (iz == null) {
            st.getPlayer().sendPacket(Msg.SYSTEM_ERROR);
            return;
        }

        r.setCoreLoc(r.getReturnLoc());
        r.setReturnLoc(st.getPlayer().getLoc());
        L2Party party = st.getPlayer().getParty();
        if (!ConfigValue.DEBUG_FRINTEZZA) {
            if (party.isInCommandChannel()) {
                L2CommandChannel cc = party.getCommandChannel();
                List<Integer> _member_list = new ArrayList<Integer>(cc.getMembers().size());
                List<String> _hwid = new ArrayList<String>(cc.getMemberCount());

                World world = getWorld(r.getId());
                world.channel = cc;
                for (L2Player member : cc.getMembers()) {
                    if (ConfigValue.FrintezzaHwidProtect) {
                        if (_hwid.contains(member.getHWIDs()))
                            continue;
                        _hwid.add(member.getHWIDs());
                    }
                    _member_list.add(member.getObjectId());
                    member.setVar("backCoords", r.getReturnLoc().toXYZString());
                    member.teleToLocation(iz.getTeleportCoords(), r.getId());
                    Quest q = QuestManager.getQuest(99995);
                    if (q != null)
                        member.processQuestEvent(q.getName(), "", null);
                }
                cc.setReflection(r);
                r.setCommandChannel(cc);
                membersCC.put(r.getId(), _member_list);
            } else if (!ConfigValue.FrintezzaNeedCommandChanel) {
                List<Integer> _member_list = new ArrayList<Integer>(party.getMemberCount());
                List<String> _hwid = new ArrayList<String>(party.getMemberCount());

                World world = getWorld(r.getId());
                for (L2Player member : party.getPartyMembers()) {
                    if (ConfigValue.FrintezzaHwidProtect) {
                        if (_hwid.contains(member.getHWIDs()))
                            continue;
                        _hwid.add(member.getHWIDs());
                    }
                    _member_list.add(member.getObjectId());
                    member.setVar("backCoords", r.getReturnLoc().toXYZString());
                    member.teleToLocation(iz.getTeleportCoords(), r.getId());
                    Quest q = QuestManager.getQuest(99995);
                    if (q != null)
                        member.processQuestEvent(q.getName(), "", null);
                }
                party.setReflection(r);
                r.setParty(party);
                membersCC.put(r.getId(), _member_list);
            }
        } else {
            for (L2Player member : party.getPartyMembers()) {
                member.setVar("backCoords", r.getReturnLoc().toXYZString());
                member.teleToLocation(iz.getTeleportCoords(), r.getId());
                Quest q = QuestManager.getQuest(99995);
                if (q != null)
                    member.processQuestEvent(q.getName(), "", null);
            }
            party.setReflection(r);
            r.setParty(party);
        }
    }

    @Override
    public String onKill(L2NpcInstance npc, QuestState st) {
        if (npc.getReflectionId() > 0) {
            final World world = getWorld(npc.getReflectionId());
            if (npc.getNpcId() == 18334)
                onKillDarkChoirCaptain(world.reflection.getId());
            if (npc.getNpcId() == 18328) {
                npc.Shout(npc.MakeFString(1801085, "", "", "", "", ""));
                onKillHallAlarmDevice(world.reflection.getId());
            }
            if (npc.getNpcId() == 18339)
                onKillDarkChoirPlayer(world.reflection.getId());

            if (Util.contains_int(blockANpcs, npc.getNpcId()))
                for (L2NpcInstance np : world._room1Monsters) {
                    if (np != null && !np.isDead())
                        return null;
                    ReflectionTable.getInstance().get(world.reflection.getId()).openDoor(17130042);
                    ReflectionTable.getInstance().get(world.reflection.getId()).openDoor(17130043);
                    return null;
                }
            else if (Util.contains_int(blockBNpcs, npc.getNpcId())) {
                for (L2NpcInstance np : world._room2OutsideMonsters)
                    if (np != null && !np.isDead())
                        return null;

                for (L2NpcInstance np : world._darkChoirCaptains) {
                    if (np != null && !np.isDead())
                        return null;
                    ReflectionTable.getInstance().get(world.reflection.getId()).openDoor(17130045);
                    ReflectionTable.getInstance().get(world.reflection.getId()).openDoor(17130046);
                    return null;
                }
            }
        }
        return null;
    }

    private void onKillHallAlarmDevice(int world_id) {
        World world = getWorld(world_id);
        if (world._Room1SpawnTask != null) {
            world._Room1SpawnTask.cancel(true);
            world._Room1SpawnTask = null;
        }
        world._Room1SpawnTask = ThreadPoolManager.getInstance().schedule(new SpawnRoom1Mobs1st(world.reflection.getId()), 5000);
        spawnRoom2InsideMob(world_id);
    }

    private void onKillDarkChoirPlayer(int world_id) {
        World world = getWorld(world_id);
        int killCnt = 0;
        for (L2NpcInstance DarkChoirPlayer : world._room2InsideMonsters)
            if (DarkChoirPlayer.isDead())
                killCnt++;
        if (world._room2InsideMonsters.size() <= killCnt) {
            if (world._Room2InsideDoorOpenTask != null)
                world._Room2InsideDoorOpenTask.cancel(true);
            if (world._Room2OutsideSpawnTask != null)
                world._Room2OutsideSpawnTask.cancel(true);
            world._Room2InsideDoorOpenTask = ThreadPoolManager.getInstance().schedule(new OpenRoom2InsideDoors(world.reflection.getId()), 3000);
            world._Room2OutsideSpawnTask = ThreadPoolManager.getInstance().schedule(new SpawnRoom2OutsideMobs(world.reflection.getId()), 4000);
        }
    }

    private void onKillDarkChoirCaptain(int world_id) {
        World world = getWorld(world_id);
        int killCnt = 0;
        for (L2NpcInstance DarkChoirCaptain : world._darkChoirCaptains)
            if (DarkChoirCaptain.isDead())
                killCnt++;
        if (world._darkChoirCaptains.size() <= killCnt) {
            ReflectionTable.getInstance().get(world.reflection.getId()).openDoor(_room2OutsideDoor1);
            //ReflectionTable.getInstance().get(world.reflection.getId()).openDoor(_room2OutsideDoor2);
            //ReflectionTable.getInstance().get(world.reflection.getId()).openDoor(_room3Door);
            if (!world.canSpawn) {
                FrintezzaManager.setScarletSpawnTask(false, world.reflection.getId());
                world.canSpawn = true;
            }
        }
    }

    private void openRoom1Doors(int world_id) {
        for (int door : _room1Doors)
            ReflectionTable.getInstance().get(world_id).openDoor(door);
    }

    private void spawnRoom2InsideMob(int world_id) {
        World world = getWorld(world_id);
        for (L2Spawn spawn : LastImperialTombSpawnlist.getRoom2InsideSpawnList()) {
            L2Spawn s = spawn.clone();
            s.setReflection(world.reflection.getId());
            L2NpcInstance mob = s.doSpawn(true);
            mob.getSpawn().stopRespawn();
            world._room2InsideMonsters.add(mob);
        }
    }

    private class SpawnRoom1Mobs1st extends RunnableImpl {
        private int world_id;

        private SpawnRoom1Mobs1st(int _world_id) {
            world_id = _world_id;
        }

        public void runImpl() {
            World world = getWorld(world_id);
            L2NpcInstance mob;
            for (L2Spawn spawn : LastImperialTombSpawnlist.getRoom1SpawnList1st())
                if (spawn.getNpcId() != ALARM_DEVICE) {
                    L2Spawn s = spawn.clone();
                    s.setReflection(world_id);
                    mob = s.doSpawn(true);
                    mob.getSpawn().stopRespawn();
                    mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getRandomPlayer(world_id), 1);
                    mob.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, getRandomPlayer(world_id), null);
                    world._room1Monsters.add(mob);
                }
            openRoom1Doors(world_id);
            ReflectionTable.getInstance().get(world_id).openDoor(_room2OutsideDoor1);
            if (world._Room1SpawnTask != null)
                world._Room1SpawnTask.cancel(true);
        }
    }

    private class OpenRoom2InsideDoors extends RunnableImpl {
        private int world_id;

        private OpenRoom2InsideDoors(int _world_id) {
            world_id = _world_id;
        }

        public void runImpl() {
            ReflectionTable.getInstance().get(world_id).closeDoor(_room2OutsideDoor1);
            for (int door : _room2InsideDoors)
                ReflectionTable.getInstance().get(world_id).openDoor(door);
        }
    }

    private class SpawnRoom2OutsideMobs extends RunnableImpl {
        private int world_id;

        private SpawnRoom2OutsideMobs(int _world_id) {
            world_id = _world_id;
        }

        public void runImpl() {
            World world = getWorld(world_id);
            for (L2Spawn spawn : LastImperialTombSpawnlist.getRoom2OutsideSpawnList()) {
                if (spawn.getNpcId() == CHOIR_CAPTAIN) {
                    L2Spawn s = spawn.clone();
                    s.setReflection(world_id);
                    L2NpcInstance mob = s.doSpawn(true);
                    mob.getSpawn().stopRespawn();
                    mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getRandomPlayer(world_id), 1);
                    mob.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, getRandomPlayer(world_id), null);
                    world._darkChoirCaptains.add(mob);
                } else {
                    L2Spawn s = spawn.clone();
                    s.setReflection(world_id);
                    L2NpcInstance mob = s.doSpawn(true);
                    mob.getSpawn().stopRespawn();
                    mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getRandomPlayer(world_id), 1);
                    mob.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, getRandomPlayer(world_id), null);
                    world._room2OutsideMonsters.add(mob);
                }
            }
        }
    }

    public static L2Zone getZone() {
        return _zone;
    }

    public static FastMap<Integer, List<Integer>> getMembersCC() {
        return membersCC;
    }

    public void onLoad() {
        init();
    }

    public void onReload() {
    }

    public void onShutdown() {
    }

    private static L2Player getRandomPlayer(int world_id) {
        World world = getWorld(world_id);
        List<L2Player> list = world.reflection.getPlayers();
        if (list.isEmpty())
            return null;
        return list.get(Rnd.get(list.size()));
    }
}