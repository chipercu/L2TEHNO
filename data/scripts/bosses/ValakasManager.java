package bosses;

import bosses.EpicBossState.State;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.database.mysql;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2CommandChannel;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2BossInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Crontab;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Log;
import l2open.util.Rnd;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import static l2open.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

/**
 * @author pchayka
 */

public class ValakasManager extends Functions implements ScriptFile {
    private static final Logger _log = Logger.getLogger(ValakasManager.class.getName());
    private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("HH:mm dd.MM.yyyy");
    private static final int _teleportCubeLocation[][] = {{214880, -116144, -1644, 0},
            {213696, -116592, -1644, 0},
            {212112, -116688, -1644, 0},
            {211184, -115472, -1664, 0},
            {210336, -114592, -1644, 0},
            {211360, -113904, -1644, 0},
            {213152, -112352, -1644, 0},
            {214032, -113232, -1644, 0},
            {214752, -114592, -1644, 0},
            {209824, -115568, -1421, 0},
            {210528, -112192, -1403, 0},
            {213120, -111136, -1408, 0},
            {215184, -111504, -1392, 0},
            {215456, -117328, -1392, 0},
            {213200, -118160, -1424, 0}};

    private static GArray<L2NpcInstance> _teleportCube = new GArray<L2NpcInstance>();
    private static GArray<L2NpcInstance> _spawnedMinions = new GArray<L2NpcInstance>();
    public static L2BossInstance _valakas;

    // Tasks.
    private static ScheduledFuture<?> _valakasSpawnTask = null;
    private static ScheduledFuture<?> _intervalEndTask = null;
    private static ScheduledFuture<?> _socialTask = null;
    private static ScheduledFuture<?> _mobiliseTask = null;
    private static ScheduledFuture<?> _moveAtRandomTask = null;
    private static ScheduledFuture<?> _respawnValakasTask = null;
    private static ScheduledFuture<?> _sleepCheckTask = null;
    private static ScheduledFuture<?> _onAnnihilatedTask = null;

    private static final int Valakas = 29028;
    private static final int _teleportCubeId = 31759;
    public static EpicBossState _state;
    private static L2Zone _zone;

    private static long _lastAttackTime = 0;
    private static int _count1 = 0;
    private static L2CommandChannel _channel = null;

    private static boolean Dying = false;
    private static final Location TELEPORT_POSITION = new Location(ConfigValue.ValakasEnterLoc[0], ConfigValue.ValakasEnterLoc[1], ConfigValue.ValakasEnterLoc[2]);

    private static class CheckLastAttack extends l2open.common.RunnableImpl {
        @Override
        public void runImpl() {
            if (_state.getState() == EpicBossState.State.ALIVE) {
                if (_lastAttackTime + ConfigValue.ValakasWaitingToSleep * 1000L < System.currentTimeMillis()) {
                    sleep();
                } else {
                    _sleepCheckTask = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 60000);
                }
            }
        }
    }

    private static class IntervalEnd extends l2open.common.RunnableImpl {
        @Override
        public void runImpl() {
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
            Announcements.getInstance().announceToAll("Валакас Возродился!");
        }
    }

    private static class onAnnihilated extends l2open.common.RunnableImpl {
        @Override
        public void runImpl() {
            sleep();
        }
    }

    private static class SpawnDespawn extends l2open.common.RunnableImpl {
        private int _distance = 2550;
        private int _taskId;
        private GArray<L2Player> _players = getPlayersInside();

        SpawnDespawn(int taskId) {
            _taskId = taskId;
        }

        @Override
        public void runImpl() {
            switch (_taskId) {
                case 1:
                    // Do spawn.
                    _valakas = (L2BossInstance) Functions.spawn(new Location(212852, -114842, -1632, 833), Valakas);

                    _valakas.block();
                    _valakas.broadcastPacket(new PlaySound(1, "BS03_A", 1, _valakas.getObjectId(), _valakas.getLoc()));

                    _state.setRespawnDate2(getRespawnInterval(false, "SPAWN"));
                    _state.setState(EpicBossState.State.ALIVE);
                    _state.update();

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(2), 16);
                    break;
                case 2:
                    // Do social.
                    _valakas.MPCC_SetMasterPartyRouting(_channel, 1);
                    _valakas.broadcastPacket(new SocialAction(_valakas.getObjectId(), 1));

                    // Set camera.
                    broadcastPacket(new SpecialCamera(_valakas, 1800, 180, -1, 1500, 15000, 10000, 0, 0, 1, 0, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(3), 1500);
                    break;
                case 3:
                    // Set camera.
                    broadcastPacket(new SpecialCamera(_valakas, 1300, 180, -5, 3000, 15000, 10000, 0, -5, 1, 0, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(4), 3300);
                    break;
                case 4:
                    // Set camera.
                    broadcastPacket(new SpecialCamera(_valakas, 500, 180, -8, 600, 15000, 10000, 0, 60, 1, 0, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(5), 2900);
                    break;
                case 5:
                    // Set camera.
                    broadcastPacket(new SpecialCamera(_valakas, 800, 180, -8, 2700, 15000, 10000, 0, 30, 1, 0, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(6), 2700);
                    break;
                case 6:
                    // Set camera.
                    broadcastPacket(new SpecialCamera(_valakas, 200, 250, 70, 0, 15000, 10000, 30, 80, 1, 0, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(7), 1);
                    break;
                case 7:
                    // Set camera.
                    broadcastPacket(new SpecialCamera(_valakas, 1100, 250, 70, 2500, 15000, 10000, 30, 80, 1, 0, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(8), 3200);
                    break;
                case 8:
                    // Set camera.
                    broadcastPacket(new SpecialCamera(_valakas, 700, 150, 30, 0, 15000, 10000, -10, 60, 1, 0, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(9), 1400);
                    break;
                case 9:
                    // Set camera.
                    broadcastPacket(new SpecialCamera(_valakas, 1200, 150, 20, 2900, 15000, 10000, -10, 30, 1, 0, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(10), 6700);
                    break;
                case 10:
                    // Set camera.
                    broadcastPacket(new SpecialCamera(_valakas, 750, 170, -10, 3400, 15000, 4000, 10, -15, 1, 0, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(11), 5700);
                    break;
                case 11:
                    // Reset camera.
                    //for(L2Player pc : _players)
                    //pc.leaveMovieMode();

                    _valakas.unblock();
                    broadcastScreenMessage(1000519);

                    // Move at random.
                    if (_valakas.getAI().getIntention() == AI_INTENTION_ACTIVE)
                        _valakas.moveToLocation(new Location(Rnd.get(211080, 214909), Rnd.get(-115841, -112822), -1662, 0), 0, false);

                    _valakas.AddTimerEx(1101, ((1 * 60) * 1000)); // Таск на спаун миньенов...
                    _sleepCheckTask = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 600000);
                    break;

                // Death Movie
                case 12:
                    _valakas.broadcastPacket(new PlaySound(1, "B03_D", 1, _valakas.getObjectId(), _valakas.getLoc()));
                    broadcastScreenMessage(1900151);
                    onValakasDie();
                    broadcastPacket(new SpecialCamera(_valakas, 2000, 130, -1, 0, 15000, 10000, 0, 0, 1, 1, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(13), 300);
                    break;
                case 13:
                    broadcastPacket(new SpecialCamera(_valakas, 1100, 210, -5, 3000, 15000, 10000, -13, 0, 1, 1, 0));

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(14), 3200);
                    break;
                case 14:
                    broadcastPacket(new SpecialCamera(_valakas, 1300, 200, -8, 3000, 15000, 10000, 0, 15, 1, 1, 0));

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(15), 4400);
                    break;
                case 15:
                    broadcastPacket(new SpecialCamera(_valakas, 1000, 190, 0, 500, 15000, 10000, 0, 10, 1, 1, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(16), 500);
                    break;
                case 16:
                    broadcastPacket(new SpecialCamera(_valakas, 1700, 120, 0, 2500, 15000, 10000, 12, 40, 1, 1, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(17), 4600);
                    break;
                case 17:
                    broadcastPacket(new SpecialCamera(_valakas, 1700, 20, 0, 700, 15000, 10000, 10, 10, 1, 1, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(18), 700);
                    break;
                case 18:
                    broadcastPacket(new SpecialCamera(_valakas, 1700, 10, 0, 1000, 15000, 10000, 20, 70, 1, 1, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(19), 2500);
                    break;
                case 19:
                    broadcastPacket(new SpecialCamera(_valakas, 1700, 10, 0, 300, 15000, 250, 20, -20, 1, 1, 0));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(20), 2500);
                    break;
                case 20:
                    for (L2Player pc : _players)
                        pc.altOnMagicUseTimer(pc, SkillTable.getInstance().getInfo(23312, 1));
                    break;
            }
        }
    }

    private static void banishForeigners() {
        for (L2Player player : getPlayersInside())
            player.teleToClosestTown();
    }

    private synchronized static void checkAnnihilated() {
        if (_onAnnihilatedTask == null && isPlayersAnnihilated())
            _onAnnihilatedTask = ThreadPoolManager.getInstance().schedule(new onAnnihilated(), 5000);
    }

    public static GArray<L2Player> getPlayersInside() {
        return getZone().getInsidePlayers();
    }

    private static long getRespawnInterval(boolean inc, String txt) {
        long time = (long) (ConfigValue.AltRaidRespawnMultiplier * (ConfigValue.FixintervalOfValakas + Rnd.get(0, ConfigValue.RandomIntervalOfValakas)));
        int index = ServerVariables.getInt("FixRespHourOfValakas", 0);
        // Это не нужно в силу наличия крона, но лень всем клиентам объяснять что и как
        if (ConfigValue.FixRespHourOfValakas.length > index && ConfigValue.FixRespHourOfValakas[index] > -1 || ConfigValue.FixRespDayOfWeekValakas > -1) {
            if (ConfigValue.FixRespDayOfWeekValakas > -1)
                time = 7 * 24 * 60 * 60000L;
            final Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis() + time);
            if (ConfigValue.FixRespHourOfValakas.length > index && ConfigValue.FixRespHourOfValakas[index] > -1) {
                c.set(Calendar.HOUR_OF_DAY, ConfigValue.FixRespHourOfValakas[index]);
                c.set(Calendar.MINUTE, 00);
                c.set(Calendar.SECOND, 00);
                if (inc) {
                    index++;
                    if (ConfigValue.FixRespHourOfValakas.length <= index)
                        index = 0;
                    ServerVariables.set("FixRespHourOfValakas", String.valueOf(index));
                }
                Log.add("Valakas getRespawnInterval[" + index + "][" + inc + "][" + txt + "]", "bosses");
            }
            if (ConfigValue.FixRespDayOfWeekValakas > -1)
                c.set(Calendar.DAY_OF_WEEK, ConfigValue.FixRespDayOfWeekValakas);
            return c.getTimeInMillis() + (Rnd.get(ConfigValue.AddRndRespHourOfValakas) * 60000);
        }
        return new Crontab(ConfigValue.ValakasCronResp).timeNextUsage(System.currentTimeMillis()) + time;
    }

    public static L2Zone getZone() {
        return _zone;
    }

    private static boolean isPlayersAnnihilated() {
        for (L2Player pc : getPlayersInside())
            if (!pc.isDead())
                return false;
        return true;
    }

    public static void OnDie(L2Character self, L2Character killer) {
        if (self.isPlayer() && _state != null && _state.getState() == State.ALIVE && _zone != null && _zone.checkIfInZone(self.getX(), self.getY()))
            checkAnnihilated();
        else if (self.isNpc() && self.getNpcId() == Valakas) {
            broadcastPacket(new SpecialCamera(self, 1200, 20, -10, 0, 10000, 13000, 0, 0, 0, 0, 0));
            ThreadPoolManager.getInstance().schedule(new SpawnDespawn(12), 300);
        }
    }

    private static void onValakasDie() {
        if (Dying)
            return;

        Dying = true;
        _state.setRespawnDate2(getRespawnInterval(true, "DIE"));
        _state.setState(EpicBossState.State.INTERVAL);
        _state.update();

        for (int[] ints : _teleportCubeLocation)
            _teleportCube.add(Functions.spawn(new Location(ints[0], ints[1], ints[2], ints[3]), _teleportCubeId));
        Log.add("Valakas died", "bosses");

        if (ConfigValue.ValakasCustomRewardPlayerCount > 0) {
            GArray<L2Player> _players = new GArray<L2Player>();
            _players.addAll(getPlayersInside());

            Announcements.getInstance().announceToAll("Поздравляем они получили частичку души Валакаса: ");
            for (int i = 0; i < ConfigValue.ValakasCustomRewardPlayerCount && _players.size() > 0; i++) {
                L2Player player = _players.remove(Rnd.get(_players.size()));
                if (player != null) {
                    Announcements.getInstance().announceToAll(player.getName());
                    if (ConfigValue.ValakasCustomReward.length > 0)
                        for (int i2 = 0; i2 < ConfigValue.ValakasCustomReward.length; i2 += 2)
                            Functions.addItem(player, (int) ConfigValue.ValakasCustomReward[i2], ConfigValue.ValakasCustomReward[i2 + 1]);

                    int rnd_size = ConfigValue.ValakasCustomRewardRnd.length;
                    if (rnd_size > 0) {
                        long[] reward = ConfigValue.ValakasCustomRewardRnd[Rnd.get(rnd_size)];
                        Functions.addItem(player, (int) reward[0], reward[1]);
                    }
                }
            }
        }
    }

    // Start interval.
    private static void setIntervalEndTask() {
        setUnspawn();

        if (_state.getState().equals(EpicBossState.State.ALIVE)) {
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
            return;
        }

        if (!_state.getState().equals(EpicBossState.State.INTERVAL)) {
            _state.setRespawnDate2(getRespawnInterval(false, "INTERVAL"));
            _state.setState(EpicBossState.State.INTERVAL);
            _state.update();
        }

        _intervalEndTask = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
    }

    // Clean Valakas's lair.
    private static void setUnspawn() {
        // Eliminate players.
        banishForeigners();

        if (_valakas != null) {
            _valakas.MPCC_SetMasterPartyRouting(null, 0);
            _valakas.deleteMe();
        }
        _channel = null;

        for (L2NpcInstance npc : _spawnedMinions)
            npc.deleteMe();

        // Delete teleport cube.
        for (L2NpcInstance cube : _teleportCube) {
            cube.getSpawn().stopRespawn();
            cube.deleteMe();
        }
        _teleportCube.clear();

        if (_valakasSpawnTask != null) {
            _valakasSpawnTask.cancel(false);
            _valakasSpawnTask = null;
        }
        if (_intervalEndTask != null) {
            _intervalEndTask.cancel(false);
            _intervalEndTask = null;
        }
        if (_socialTask != null) {
            _socialTask.cancel(false);
            _socialTask = null;
        }
        if (_mobiliseTask != null) {
            _mobiliseTask.cancel(false);
            _mobiliseTask = null;
        }
        if (_moveAtRandomTask != null) {
            _moveAtRandomTask.cancel(false);
            _moveAtRandomTask = null;
        }
        if (_sleepCheckTask != null) {
            _sleepCheckTask.cancel(false);
            _sleepCheckTask = null;
        }
        if (_respawnValakasTask != null) {
            _respawnValakasTask.cancel(false);
            _respawnValakasTask = null;
        }
        if (_onAnnihilatedTask != null) {
            _onAnnihilatedTask.cancel(false);
            _onAnnihilatedTask = null;
        }
        //mysql.set("UPDATE `bos_debug` SET `attacked`=0 where bos_id=29028");
    }

    private static void sleep() {
        setUnspawn();
        if (_state.getState().equals(EpicBossState.State.ALIVE)) {
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
        }
    }

    public static void setLastAttackTime() {
        //if(_lastAttackTime + 600000 < System.currentTimeMillis())
        //	mysql.set("UPDATE `bos_debug` SET `attacked`=1 where bos_id=29028");
        _lastAttackTime = System.currentTimeMillis();
    }

    // Setting Valakas spawn task.
    public synchronized static void setValakasSpawnTask() {
        if (_valakasSpawnTask == null)
            _valakasSpawnTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(1), ConfigValue.ValakasWaitingToSpawn * 60000L);
    }

    public static boolean isEnableEnterToLair() {
        return _state.getState() == EpicBossState.State.NOTSPAWN;
    }

    public static void broadcastScreenMessage(int id) {
        for (L2Player p : getPlayersInside())
            p.sendPacket(new ExShowScreenMessage(id, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, false));
    }

    public static void addValakasMinion(L2NpcInstance npc) {
        _spawnedMinions.add(npc);
    }

    private void init() {
        _state = new EpicBossState(Valakas);
        communityboard.CommunityBoardFullStats.addEpic(_state);
        _zone = ZoneManager.getInstance().getZoneById(ZoneType.epic, 702003, false);
        _log.info("ValakasManager: State of Valakas is " + _state.getState() + ".");
        if (!_state.getState().equals(EpicBossState.State.NOTSPAWN))
            setIntervalEndTask();

        _log.info("ValakasManager: Next spawn date of Valakas is " + toSimpleFormat(_state.getRespawnDate()) + ".");
    }

    public static void enterTheLair(L2Player ccleader) {
        if (ccleader == null)
            return;

        if (ccleader.isGM() && ConfigValue.ValakasDebug) {
            if (ccleader.getParty() != null)
                for (L2Player p : ccleader.getParty().getPartyMembers())
                    p.teleToLocation(TELEPORT_POSITION);
            else
                ccleader.teleToLocation(TELEPORT_POSITION);
            setValakasSpawnTask();
            return;
        }
        if (_state.getState() != EpicBossState.State.NOTSPAWN) {
            ccleader.sendMessage("Valakas is still reborning. You cannot invade the nest now");
            return;
        }
        if (_state.getState() == EpicBossState.State.ALIVE) {
            ccleader.sendMessage("Valakas has already been reborned and is being attacked. The entrance is sealed.");
            return;
        }
        Location loc = ccleader.getLoc();
        int _count = 0;

        if (!ConfigValue.ValakasNeedCC) {
            if (ccleader.getParty() == null) {
                if (!ccleader.isFlying() && !ccleader.isCursedWeaponEquipped() && !ccleader.isDead()) {
                    ccleader.teleToLocation(TELEPORT_POSITION);
                    Log.add("Enter[" + _count1 + "]: " + ccleader.getName(), "valakas_enter");
                } else
                    Log.add("NO enter1[" + (!ccleader.isFlying()) + "][" + (!ccleader.isCursedWeaponEquipped()) + "][" + (!ccleader.isDead()) + "]: " + ccleader.getName(), "valakas_enter");
                _count1++;
                setValakasSpawnTask();
                return;
            } else if (!ccleader.getParty().isInCommandChannel()) {
                for (L2Player p : ccleader.getParty().getPartyMembers())
                    if (!p.isFlying() && !p.isCursedWeaponEquipped() && !p.isDead() && p.isInRange(loc, 500)) {
                        p.teleToLocation(TELEPORT_POSITION);
                        _count++;
                        Log.add("Enter[" + _count + "][" + _count1 + "]: " + p.getName(), "valakas_enter");
                    } else
                        Log.add("NO enter1[" + (!p.isFlying()) + "][" + (!p.isCursedWeaponEquipped()) + "][" + (!p.isDead()) + "][" + (p.isInRange(loc, 500)) + "]: " + p.getName(), "valakas_enter");

                _count1 = _count1 + _count;
                setValakasSpawnTask();
                return;
            }
        }

        if (ccleader.getParty() == null || !ccleader.getParty().isInCommandChannel()) {
            ccleader.sendPacket(Msg.YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_IN_A_CURRENT_COMMAND_CHANNEL);
            return;
        }
        L2CommandChannel cc = ccleader.getParty().getCommandChannel();
        if (cc.getChannelLeader() != ccleader) {
            ccleader.sendMessage("Only the alliance channel leader can try to entry");
            return;
        }
        if (_count1 >= ConfigValue.ValakasMaxPlayer || cc.getMemberCount() >= ConfigValue.ValakasMaxPlayer) {
            ccleader.sendMessage("The maximum of " + ConfigValue.ValakasMaxPlayer + " players can invade the Valakas Nest");
            return;
        }

        // checking every member of CC for the proper conditions
		/*for(L2Player member : cc.getMembers())
		{
			if(member == null) 
				continue;
			if(member.isCursedWeaponEquipped() || member.isInFlyingTransform() || member.isDead()) 
			{
				ccleader.sendMessage("Command Channel member " + member.getName() + " doesn't meet the requirements to enter the nest");
				return;
			}
			if(!ccleader.isInRange(member, 500)) 
			{
				member.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
				ccleader.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
				return;
			}
		}*/


        Log.add("-----------------------------------------------------------------\nEnter Leader: " + ccleader.getName(), "valakas_enter");
        for (L2Player members : cc.getMembers())
            if (!members.isFlying() && !members.isCursedWeaponEquipped() && !members.isDead() && members.isInRange(loc, 500)) {
                members.teleToLocation(TELEPORT_POSITION);
                _count++;
                Log.add("Enter[" + _count + "][" + _count1 + "]: " + members.getName(), "valakas_enter");
            } else
                Log.add("NO enter1[" + (!members.isFlying()) + "][" + (!members.isCursedWeaponEquipped()) + "][" + (!members.isDead()) + "][" + (members.isInRange(loc, 500)) + "]: " + members.getName(), "valakas_enter");

        if (_count >= ConfigValue.MinChannelMembersValakas && _channel == null)
            _channel = cc;
        if (_count > 0)
            _count1 = _count1 + _count;

        setValakasSpawnTask();
    }

    public static String toSimpleFormat(long cal) {
        return SIMPLE_FORMAT.format(cal);
    }

    public static L2Player getRandomPlayer() {
        GArray<L2Player> list = getZone().getInsidePlayers();
        if (list.isEmpty())
            return null;
        return list.get(Rnd.get(list.size()));
    }

    protected static void broadcastPacket(L2GameServerPacket mov) {
        if (getZone() != null) {
            for (L2Character characters : getZone().getInsidePlayers()) {
                if (characters.isPlayer()) {
                    characters.sendPacket(mov);
                }
            }
        }
    }

    @Override
    public void onLoad() {
        init();
    }

    @Override
    public void onReload() {
        sleep();
    }

    @Override
    public void onShutdown() {
    }
}