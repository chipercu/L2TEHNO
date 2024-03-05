package l2open.gameserver.model.instances;

import gnu.trove.TIntObjectHashMap;
import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.Announcements;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2ObjectTasks.SoulConsumeTask;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.base.ItemToDrop;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemLocation;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestEventType;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.serverpackets.SpawnEmitter;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.*;
import l2open.util.reference.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class manages all Monsters.
 * <p>
 * L2MonsterInstance :<BR><BR>
 * <li>L2MinionInstance</li>
 * <li>L2RaidBossInstance </li>
 */
public class L2MonsterInstance extends L2NpcInstance {
    public static final class RewardInfo {
        public L2Player _attacker;
        public int _dmg = 0;

        public RewardInfo(final L2Player attacker, final int dmg) {
            _attacker = attacker;
            _dmg = dmg;
        }

        public void addDamage(int dmg) {
            if (dmg < 0)
                dmg = 0;

            _dmg += dmg;
        }

        @Override
        public int hashCode() {
            return _attacker.getObjectId();
        }
    }

    private boolean _dead = false, _dying = false;
    private final ReentrantLock dieLock = new ReentrantLock(), dyingLock = new ReentrantLock(),
            sweepLock = new ReentrantLock(), harvestLock = new ReentrantLock();

    /**
     * Stores the extra (over-hit) damage done to the L2NpcInstance when the attacker uses an over-hit enabled skill
     */
    private double _overhitDamage;

    /**
     * Stores the attacker who used the over-hit enabled skill on the L2NpcInstance
     */

    public MinionList _minionList;
    private ScheduledFuture<?> minionMaintainTask;

    private static final int MONSTER_MAINTENANCE_INTERVAL = 1000;

    /**
     * Максимальный уровень мобов
     */
    private static final int MONSTER_MAX_LEVEL = 200;

    private GArray<L2ItemInstance> _inventory;

    /**
     * crops
     */
    protected L2ItemInstance _harvestItem;
    protected L2Item _seeded;
    protected int _seederId;
    protected HardReference<? extends L2Player> spoiler_ref = HardReferences.emptyRef();
    protected HardReference<? extends L2Character> overhit_attacker_ref = HardReferences.emptyRef();


    private boolean _absorbed;

    private final TIntObjectHashMap<AbsorberInfo> _absorbersList = new TIntObjectHashMap<AbsorberInfo>();

    /**
     * Table containing all Items that a Dwarf can Sweep on this L2NpcInstance
     */
    private L2ItemInstance[] _sweepItems;

    // For ALT_GAME_MATHERIALSDROP
    protected static final L2DropData[] _matdrop = new L2DropData[]{
            //                                           Item              Price Chance
            new L2DropData(1864, 1, 1, 50000, 1), // Stem              100   5%
            new L2DropData(1865, 1, 1, 25000, 1), // Varnish           200   2.5%
            new L2DropData(1866, 1, 1, 16666, 1), // Suede             300   1.6666%
            new L2DropData(1867, 1, 1, 33333, 1), // Animal Skin       150   3.3333%
            new L2DropData(1868, 1, 1, 50000, 1), // Thread            100   5%
            new L2DropData(1869, 1, 1, 25000, 1), // Iron Ore          200   2.5%
            new L2DropData(1870, 1, 1, 25000, 1), // Coal              200   2.5%
            new L2DropData(1871, 1, 1, 25000, 1), // Charcoal          200   2.5%
            new L2DropData(1872, 1, 1, 50000, 1), // Animal Bone       150   5%
            new L2DropData(1873, 1, 1, 10000, 1), // Silver Nugget     500   1%
            new L2DropData(1874, 1, 1, 1666, 20), // Oriharukon Ore    3000  0.1666%
            new L2DropData(1875, 1, 1, 1666, 20), // Stone of Purity   3000  0.1666%
            new L2DropData(1876, 1, 1, 5000, 20), // Mithril Ore       1000  0.5%
            new L2DropData(1877, 1, 1, 1000, 20), // Adamantite Nugget 5000  0.1%
            new L2DropData(4039, 1, 1, 833, 40), //  Mold Glue         6000  0.0833%
            new L2DropData(4040, 1, 1, 500, 40), //  Mold Lubricant    10000 0.05%
            new L2DropData(4041, 1, 1, 217, 40), //  Mold Hardener     23000 0.0217%
            new L2DropData(4042, 1, 1, 417, 40), //  Enria             12000 0.0417%
            new L2DropData(4043, 1, 1, 833, 40), //  Asofe             6000  0.0833%
            new L2DropData(4044, 1, 1, 833, 40) //   Thons             6000  0.0833%
    };

    protected static final GArray<L2DropGroup> _herbs = new GArray<L2DropGroup>(3);

    static {
        L2DropGroup d = new L2DropGroup(0);
        d.addDropItem(new L2DropData(8600, 1, 1, 120000, 1)); // of Life                    15%
        d.addDropItem(new L2DropData(8603, 1, 1, 120000, 1)); // of Mana                    15%
        d.addDropItem(new L2DropData(8601, 1, 1, 40000, 1)); //  Greater of Life            5%
        d.addDropItem(new L2DropData(8604, 1, 1, 40000, 1)); //  Greater of Mana            5%
        d.addDropItem(new L2DropData(8602, 1, 1, 12000, 1)); //  Superior of Life           1.6%
        d.addDropItem(new L2DropData(8605, 1, 1, 12000, 1)); //  Superior of Mana           1.6%
        d.addDropItem(new L2DropData(8614, 1, 1, 3000, 1)); //   of Recovery                0.3%
        _herbs.add(d);
        d = new L2DropGroup(0);
        d.addDropItem(new L2DropData(8611, 1, 1, 50000, 1)); //  of Speed                   5%
        d.addDropItem(new L2DropData(8606, 1, 1, 50000, 1)); //  of Power                   5%
        d.addDropItem(new L2DropData(8608, 1, 1, 50000, 1)); //  of Atk. Spd.               5%
        d.addDropItem(new L2DropData(8610, 1, 1, 50000, 1)); //  of Critical Attack         5%
        d.addDropItem(new L2DropData(10656, 1, 1, 50000, 1)); // of Critical Attack - Power 5%
        d.addDropItem(new L2DropData(10655, 1, 1, 50000, 1)); // of Life Force Absorption   5%
        d.addDropItem(new L2DropData(8607, 1, 1, 50000, 1)); //  of Magic                   5%
        d.addDropItem(new L2DropData(8609, 1, 1, 50000, 1)); //  of Casting Speed           5%
        d.addDropItem(new L2DropData(8612, 1, 1, 10000, 1)); //  of Warrior                 1%
        d.addDropItem(new L2DropData(8613, 1, 1, 10000, 1)); //  of Mystic                  1%
        _herbs.add(d);
        d = new L2DropGroup(0);
        d.addDropItem(new L2DropData(10657, 1, 1, 3000, 1)); //  of Doubt                   0.3%
        d.addDropItem(new L2DropData(13028, 1, 1, 2000, 1)); //  of Vitality                0.2%
        _herbs.add(d);
    }

    protected static final L2DropData[] _lifestones = new L2DropData[]{
            //
            new L2DropData(8723, 1, 1, 200, 44, 46), // Life Stone: level 46
            new L2DropData(8724, 1, 1, 200, 47, 49), // Life Stone: level 49
            new L2DropData(8725, 1, 1, 200, 50, 52), // Life Stone: level 52
            new L2DropData(8726, 1, 1, 200, 53, 55), // Life Stone: level 55
            new L2DropData(8727, 1, 1, 200, 56, 58), // Life Stone: level 58
            new L2DropData(8728, 1, 1, 200, 59, 61), // Life Stone: level 61
            new L2DropData(8729, 1, 1, 200, 62, 66), // Life Stone: level 64
            new L2DropData(8730, 1, 1, 200, 67, 72), // Life Stone: level 67
            new L2DropData(8731, 1, 1, 200, 73, 75), // Life Stone: level 70
            new L2DropData(8732, 1, 1, 200, 76, 79), // Life Stone: level 76
            new L2DropData(9573, 1, 1, 150, 80, 81), // Life Stone: level 80
            new L2DropData(10483, 1, 1, 120, 82, 83), // Life Stone: level 82
            new L2DropData(14166, 1, 1, 100, 84, MONSTER_MAX_LEVEL), // Life Stone: level 84
            new L2DropData(8733, 1, 1, 100, 44, 46), // Mid-Grade Life Stone: level 46
            new L2DropData(8734, 1, 1, 100, 47, 49), // Mid-Grade Life Stone: level 49
            new L2DropData(8735, 1, 1, 100, 50, 52), // Mid-Grade Life Stone: level 52
            new L2DropData(8736, 1, 1, 100, 53, 55), // Mid-Grade Life Stone: level 55
            new L2DropData(8737, 1, 1, 100, 56, 58), // Mid-Grade Life Stone: level 58
            new L2DropData(8738, 1, 1, 100, 59, 61), // Mid-Grade Life Stone: level 61
            new L2DropData(8739, 1, 1, 100, 62, 66), // Mid-Grade Life Stone: level 64
            new L2DropData(8740, 1, 1, 100, 67, 72), // Mid-Grade Life Stone: level 67
            new L2DropData(8741, 1, 1, 100, 73, 75), // Mid-Grade Life Stone: level 70
            new L2DropData(8742, 1, 1, 100, 76, 79), // Mid-Grade Life Stone: level 76
            new L2DropData(9574, 1, 1, 80, 80, 81), // Mid-Grade Life Stone: level 80
            new L2DropData(10484, 1, 1, 60, 82, 83), // Mid-Grade Life Stone: level 82
            new L2DropData(14167, 1, 1, 40, 84, MONSTER_MAX_LEVEL), // Mid-Grade Life Stone: level 84
            new L2DropData(8743, 1, 1, 30, 44, 46), // High-Grade Life Stone: level 46
            new L2DropData(8744, 1, 1, 30, 47, 49), // High-Grade Life Stone: level 49
            new L2DropData(8745, 1, 1, 30, 50, 52), // High-Grade Life Stone: level 52
            new L2DropData(8746, 1, 1, 30, 53, 55), // High-Grade Life Stone: level 55
            new L2DropData(8747, 1, 1, 30, 56, 58), // High-Grade Life Stone: level 58
            new L2DropData(8748, 1, 1, 30, 59, 61), // High-Grade Life Stone: level 61
            new L2DropData(8749, 1, 1, 30, 62, 66), // High-Grade Life Stone: level 64
            new L2DropData(8750, 1, 1, 30, 67, 72), // High-Grade Life Stone: level 67
            new L2DropData(8751, 1, 1, 30, 73, 75), // High-Grade Life Stone: level 70
            new L2DropData(8752, 1, 1, 30, 76, 79), // High-Grade Life Stone: level 76
            new L2DropData(9575, 1, 1, 25, 80, 81), // High-Grade Life Stone: level 80
            new L2DropData(10485, 1, 1, 20, 82, 83), // High-Grade Life Stone: level 82
            new L2DropData(14168, 1, 1, 30, 84, MONSTER_MAX_LEVEL), // High-Grade Life Stone: level 84
    };

    protected static final L2DropData[] _toplifestones = new L2DropData[]{
            //
            new L2DropData(8753, 1, 1, 100000, 44, 46), // Top-Grade Life Stone: level 46
            new L2DropData(8754, 1, 1, 100000, 47, 49), // Top-Grade Life Stone: level 49
            new L2DropData(8755, 1, 1, 100000, 50, 52), // Top-Grade Life Stone: level 52
            new L2DropData(8756, 1, 1, 100000, 53, 55), // Top-Grade Life Stone: level 55
            new L2DropData(8757, 1, 1, 100000, 56, 58), // Top-Grade Life Stone: level 58
            new L2DropData(8758, 1, 1, 100000, 59, 61), // Top-Grade Life Stone: level 61
            new L2DropData(8759, 1, 1, 100000, 62, 66), // Top-Grade Life Stone: level 64
            new L2DropData(8760, 1, 1, 100000, 67, 72), // Top-Grade Life Stone: level 67
            new L2DropData(8761, 1, 1, 100000, 73, 75), // Top-Grade Life Stone: level 70
            new L2DropData(8762, 1, 1, 100000, 76, 79), // Top-Grade Life Stone: level 76
            new L2DropData(9576, 1, 1, 85000, 80, 81), // Top-Grade Life Stone: level 80
            new L2DropData(10486, 1, 1, 65000, 82, 83), // Top-Grade Life Stone: level 82
            new L2DropData(14169, 1, 1, 50000, 84, MONSTER_MAX_LEVEL), // Top-Grade Life Stone: level 84
    };

    protected static final L2DropData[] _raiditems = new L2DropData[]{
            //
            new L2DropData(9814, 1, 2, 300000, 40, 74), // Memento Mori
            new L2DropData(9815, 1, 2, 300000, 40, 70), // Dragon Heart
            new L2DropData(9816, 1, 2, 300000, 40, 74), // Earth Egg
            new L2DropData(9817, 1, 2, 300000, 40, 74), // Nonliving Nucleus
            new L2DropData(9818, 1, 2, 300000, 40, 70), // Angelic Essence
            new L2DropData(8176, 1, 2, 300000, 40, 74) //  Destruction Tombstone
    };

    /**
     * Constructor<?> of L2MonsterInstance (use L2Character and L2NpcInstance constructor).<BR><BR>
     *
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Call the L2Character constructor to set the _template of the L2MonsterInstance (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR) </li>
     * <li>Set the name of the L2MonsterInstance</li>
     * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it </li><BR><BR>
     *
     * @param objectId Identifier of the object to initialized
     * @param template to apply to the NPC
     */
    public L2MonsterInstance(int objectId, L2NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public boolean isMovementDisabled() {
        // Невозможность ходить для этих мобов
        return getNpcId() == 18344 || getNpcId() == 18345 || super.isMovementDisabled();
    }

    @Override
    public boolean isLethalImmune() {
        return _isChampion > 0 || super.isLethalImmune();
    }

    @Override
    public boolean isFearImmune() {
        return _isChampion > 0 || isEpicRaid() || super.isFearImmune();
    }

    @Override
    public boolean isParalyzeImmune() {
        return _isChampion > 0 || isEpicRaid() || super.isParalyzeImmune();
    }

    /**
     * Return True if the attacker is not another L2MonsterInstance.<BR><BR>
     */
    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        return !attacker.isMonster() && getNpcId() != 18850 && getNpcId() != 18851/** затык на время**/;
    }

    private int _isChampion;

    public int getChampion() {
        return _isChampion;
    }

    public void setChampion(int level) {
        if (level == 0) {
            removeSkillById(4407);
            _isChampion = 0;
        } else {
            addSkill(SkillTable.getInstance().getInfo(4407, level));
            _isChampion = level;
            setCurrentHp(getMaxHp(), false);
        }
    }

    public boolean canChampion() {
        return getTemplate().revardExp > 0;
    }

    @Override
    public int getTeam() {
        return getChampion();
    }

    /**
     * Очищает флаги состояний смерти. Дает или очищает статус чемпиона. Восстанавливает HP. Спавнит миньонов. Отключает аггр на 10 секунд.
     */
    @Override
    public void onSpawn() {
        if (ConfigValue.AnnounceSpawnNpcType > -1 && Util.contains(ConfigValue.AnnounceSpawnNpcList, getNpcId())) {
            if (ConfigValue.AnnounceSpawnNpcType == 0)
                Announcements.getInstance().announceByCustomMessage("AnnounceSpawnNpc_" + getNpcId(), null);
            else if (ConfigValue.AnnounceSpawnNpcType == 1)
                Announcements.getInstance().announceByCustomMessage("AnnounceSpawnNpc_All", null);
        }
        _dead = false;
        _dying = false;
        overhit_attacker_ref = HardReferences.emptyRef();
        setChampion(0);
        if (getLevel() >= ConfigValue.ChampionMinLevel && getLevel() <= ConfigValue.ChampionMaxLevel && !isRaid() && getReflection().canChampions() && !isRefRaid() && !(this instanceof L2MinionInstance) && !(this instanceof L2ChestInstance) && getTemplate().revardExp > 0 && (ConfigValue.CanNotChampionsId[0] < 1 || !Util.contains_int(ConfigValue.CanNotChampionsId, getNpcId()) && (ConfigValue.CanChampionsId[0] < 1 || Util.contains_int(ConfigValue.CanChampionsId, getNpcId())))) {
            double random = Rnd.nextDouble();
            if (ConfigValue.AltChampionChance2 / 100 >= random)
                setChampion(2);
            else if ((ConfigValue.AltChampionChance1 + ConfigValue.AltChampionChance2) / 100 >= random)
                setChampion(1);
        }
        setCurrentHpMp(getMaxHp(), getMaxMp(), true);
        super.onSpawn();
        spawnMinions();
        getAI().setGlobalAggro(System.currentTimeMillis() + 10000);

        // Clear mob spoil, absorbs, seed
        setSpoiled(false, null);
        _sweepItems = null;
        resetAbsorbList();
        _seeded = null;
        _seederId = 0;
        spoiler_ref = HardReferences.emptyRef();
    }

    protected int getMaintenanceInterval() {
        return MONSTER_MAINTENANCE_INTERVAL;
    }

    public MinionList getMinionList() {
        return _minionList;
    }

    public void setNewMinionList() {
        _minionList = new MinionList(this);
    }

    public class MinionMaintainTask extends l2open.common.RunnableImpl {
        public void runImpl() {
            if (L2MonsterInstance.this == null || L2MonsterInstance.this.isDead())
                return;
            try {
                if (L2MonsterInstance.this._minionList == null)
                    L2MonsterInstance.this.setNewMinionList();
                L2MonsterInstance.this._minionList.maintainMinions();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void spawnMinions() {
        if (getTemplate().getMinionData().size() > 0) {
            if (minionMaintainTask != null) {
                minionMaintainTask.cancel(true);
                minionMaintainTask = null;
            }
            //minionMaintainTask = ThreadPoolManager.getInstance().schedule(new MinionMaintainTask(), getMaintenanceInterval());
            ThreadPoolManager.getInstance().execute(new MinionMaintainTask());
        }
    }

    public Location getMinionPosition() {
        return Location.getAroundPosition(this, this, 100, 150, 10);
    }

    @Override
    public void callMinionsToAssist(L2Character attacker) {
        if (_minionList != null && _minionList.hasMinions())
            for (L2MinionInstance minion : _minionList.getSpawnedMinions()) {
                if (minion != null && minion.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK && !minion.isDead())
                    minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
                if (minion != null && !minion.isDead())
                    minion.getAI().notifyEvent(CtrlEvent.EVT_PARTY_ATTACKED, new Object[]{attacker, this, 0});
            }
    }

    public void setDead(boolean dead) {
        _dead = dead;
    }

    public void removeMinions() {
        if (minionMaintainTask != null) {
            minionMaintainTask.cancel(true);
            minionMaintainTask = null;
        }
        if (_minionList != null)
            _minionList.maintainLonelyMinions();
        _minionList = null;
    }

    public int getTotalSpawnedMinionsInstances() {
        return _minionList == null ? 0 : _minionList.countSpawnedMinions();
    }

    public void notifyMinionDied(L2MinionInstance minion) {
        if (_minionList != null)
            _minionList.removeSpawnedMinion(minion);
    }

    @Override
    public boolean hasMinions() {
        return _minionList != null && _minionList.hasMinions();
    }

    public void setReflection(int i) {
        super.setReflection(i);

        if (hasMinions())
            for (L2MinionInstance m : _minionList.getSpawnedMinions())
                m.setReflection(i);
    }

    @Override
    public void deleteMe() {
        removeMinions();
        if (_inventory != null)
            synchronized (_inventory) {
                for (L2ItemInstance item : _inventory)
                    getTemplate().giveItem(item, false);
                _inventory = null;
            }
        super.deleteMe();
    }

    @Override
    public void doDie(final L2Character killer) {
        if (_dead)
            return;
        dieLock.lock();
        try {
            if (minionMaintainTask != null) {
                try {
                    minionMaintainTask.cancel(true);
                    minionMaintainTask = null;
                } catch (Exception e) {
                }
            }
            if (_dead)
                return;
            _dieTime = System.currentTimeMillis();
            _dead = true;

            try {
                dyingLock.lock();
                _dying = true;
                calculateRewards(killer);
            } catch (final Exception e) {
                e.printStackTrace();
            } finally {
                _dying = false;
                dyingLock.unlock();
            }
        } finally {
            dieLock.unlock();
        }

        super.doDie(killer);
    }

    /**
     * ** Обычные мобы:
     * Дроп имеют право подобрать те, кто хоть раз ударил моба или их член пати.
     * ** Рейды:
     * Право на подбор дропа у членов пати, которая нанесла макс дамаг в общей сумме.
     **/
    public void calculateRewards(L2Character lastAttacker) {
        HashMap<L2Playable, AggroInfo> aggroList = getAggroMap();
        L2Character topDamager = getTopDamager(aggroList.values());
        if (lastAttacker == null && topDamager != null)
            lastAttacker = topDamager;
        if (lastAttacker == null || aggroList.isEmpty())
            return;
        L2Player killer = lastAttacker.getPlayer();
        if (killer == null)
            return;

        if (topDamager == null)
            topDamager = lastAttacker;

        // Notify the Quest Engine of the L2NpcInstance death if necessary
        try {
            if (killer.getAttainment() != null)
                killer.getAttainment().incPve(this, true);
            if (ConfigValue.KillCounter)
                killer.incrementKillsCounter(getNpcId());
            getTemplate().killscount++;

            Quest[] arrayOfQuest = null;
            if (getTemplate().hasQuestEvents() && ((arrayOfQuest = getTemplate().getEventQuests(QuestEventType.MOBKILLED)) != null)) {
                GArray<L2Player> players = null; // массив с игроками, которые могут быть заинтересованы в квестах
                if ((isRaid() || isEpicRaid()) && ConfigValue.NoLasthitOnRaid) // Для альта на ластхит берем всех игроков вокруг
                {
                    players = new GArray<L2Player>();
                    for (L2Playable pl : aggroList.keySet())
                        if (pl.isPlayer() && pl.getReflectionId() == getReflectionId() && (pl.isInRange(this, ConfigValue.AltPartyDistributionRange) || pl.isInRange(killer, ConfigValue.AltPartyDistributionRange)) && Math.abs(pl.getZ() - getZ()) < 400)
                            players.add((L2Player) pl);
                } else if (killer.getParty() != null) // если пати то собираем всех кто подходит
                {
                    players = new GArray<L2Player>(killer.getParty().getMemberCount());
                    for (L2Player pl : killer.getParty().getPartyMembers())
                        if (pl.getReflectionId() == getReflectionId() && (pl.isInRange(this, ConfigValue.AltPartyDistributionRange) || pl.isInRange(killer, ConfigValue.AltPartyDistributionRange)) && Math.abs(pl.getZ() - getZ()) < 400)
                            players.add(pl);
                }

                for (Quest quest : arrayOfQuest) {
                    L2Player toReward = killer;
                    if (quest.getParty() != Quest.PARTY_NONE && players != null)
                        if (isRaid() || isEpicRaid() || quest.getParty() == Quest.PARTY_ALL) // если цель рейд или квест для всей пати награждаем всех участников
                        {
                            for (L2Player pl : players) {
                                QuestState qs = pl.getQuestState(quest.getName());
                                if (qs != null && !qs.isCompleted())
                                    quest.notifyKill(this, qs);
                            }
                            toReward = null;
                        } else { // иначе выбираем одного
                            GArray<L2Player> interested = new GArray<L2Player>(players.size());
                            for (L2Player pl : players) {
                                QuestState qs = pl.getQuestState(quest.getName());
                                if (qs != null && !qs.isCompleted()) // из тех, у кого взят квест
                                    interested.add(pl);
                            }

                            if (interested.isEmpty())
                                continue;

                            toReward = interested.get(Rnd.get(interested.size()));
                            if (toReward == null)
                                toReward = killer;
                        }

                    // Уебищная затычка для квестов Путь Лорда...По другому просто нужно пихать в АИ мобов выдачу награды, а мне лень...Мб потом уберу...
                    if (toReward != null && quest.getQuestIntId() >= 708 && quest.getQuestIntId() <= 716 && toReward.getClan() != null && toReward.getClan().getLeader().isOnline() && toReward.getClan().getLeader().getPlayer().getQuestState(quest.getName()) != null) {
                        QuestState qs = toReward.getQuestState(quest.getName());
                        if (qs == null)
                            QuestManager.getQuest(quest.getQuestIntId()).newQuestState(toReward, 0);
                        if (qs != null && !qs.isCompleted())
                            quest.notifyKill(this, qs);
                    } else if (toReward != null) {
                        QuestState qs = toReward.getQuestState(quest.getName());
                        if (qs != null && !qs.isCompleted())
                            quest.notifyKill(this, qs);
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // Distribute Exp and SP rewards to L2Player (including Summon owner) that hit the L2NpcInstance and to their Party members
        FastMap<L2Player, RewardInfo> rewards = new FastMap<L2Player, RewardInfo>().setShared(true);
        boolean boss = (isRaid() || isBoss() || isRefRaid()) && !isEpicRaid();

        for (AggroInfo info : aggroList.values()) {
            if (info.damage <= 1)
                continue;
            L2Character attacker = info.attacker;
            if (attacker == null || !attacker.isPlayer())
                continue;
            L2Player player = attacker.getPlayer();
            if (player != null) {
                if (player.getAttainment() != null && player.isInRange(this, 2000)) {
                    if (boss || isEpicRaid())
                        player.getAttainment().setKillRaid(this);
                    //else
                    //	player.getAttainment().incPve(this, false);
                }
                RewardInfo reward = rewards.get(player);
                if (reward == null)
                    rewards.put(player, new RewardInfo(player, info.damage));
                else
                    reward.addDamage(info.damage);
            }
        }

        // Сначала дроп, а потом лвлАп, а то расчет дропа идет уже по новому лвлу)
        // Manage Base, Quests and Special Events drops of the L2NpcInstance
        if (lastAttacker.getLevel() > getLevel() - ConfigValue.DropPenaltyDiff) {
            doItemDrop(topDamager);
        }

        // Manage Sweep drops of the L2NpcInstance
        if (isSpoiled()) {
            doSweepDrop(spoiler_ref.get(), topDamager);

            if (ConfigValue.EnableAgationSpoil) {
                L2Player player = (L2Player) topDamager;
                if (player.getAgathion() != null && player.getAgathion().isUseSpoil()){
                    topDamager.altUseSkill(SkillTable.getInstance().getInfo(444, 1), this);
                }
            }
        }

        for (FastMap.Entry<L2Player, RewardInfo> e = rewards.head(), end = rewards.tail(); e != null && (e = e.getNext()) != end && e != null; ) {
            L2Player attacker = e.getKey();
            RewardInfo reward = e.getValue();
            if (attacker == null || attacker.isDead() || reward == null)
                continue;
            L2Party party = attacker.getParty();
            int maxHp = getMaxHp();
            if (party == null) {
                int damage = Math.min(reward._dmg, maxHp);
                if (damage > 0) {
                    double[] xpsp = calculateExpAndSp(attacker, attacker.getLevel(), damage);
                    double neededExp = attacker.calcStat(Stats.SOULS_CONSUME_EXP, 0, this, null); // Начисление душ камаэлянам
                    if (neededExp > 0 && xpsp[0] > neededExp) {
                        broadcastPacket(new SpawnEmitter(this, attacker));
                        ThreadPoolManager.getInstance().schedule(new SoulConsumeTask(attacker), 1000);
                    }
                    xpsp[0] = applyOverhit(killer, xpsp[0]);
                    xpsp = attacker.applyVitality(this, xpsp[0], xpsp[1], 1.0);

                    if (ConfigValue.RangEnable) {
                        long point = (long) ((xpsp[0] / 100) * ConfigValue.RangPercentAddPointMob[attacker.getRangId()]);
                        if (point < 1)
                            point = 1;
                        attacker.addRangPoint(point);
                        attacker.sendMessage("Получено " + point + " Очков Воина. Всего " + attacker.getRangPoint() + " Очков Воина.");
                    }
                    if (attacker.getLevel() > getLevel() - ConfigValue.ExpSpPenaltyDiff)
                        attacker.addExpAndSp((long) xpsp[0], (long) xpsp[1], false, true, (long) xpsp[2], (long) xpsp[3], this);
                }
                rewards.remove(attacker);
            } else {
                int partyDmg = 0;
                int partylevel = 1;
                GArray<L2Player> rewardedMembers = new GArray<L2Player>();
                for (L2Player partyMember : party.getPartyMembers()) {
                    RewardInfo ai = rewards.remove(partyMember);
                    if (partyMember.isDead() || !partyMember.isInRange(lastAttacker, ConfigValue.AltPartyDistributionRange))
                        continue;
                    if (ai != null)
                        partyDmg += ai._dmg;
                    rewardedMembers.add(partyMember);
                    if (partyMember.getLevel() > partylevel)
                        partylevel = partyMember.getLevel();
                }
                partyDmg = Math.min(partyDmg, maxHp);
                if (partyDmg > 0) {
                    double[] xpsp = calculateExpAndSp(attacker, partylevel, partyDmg);
                    double partyMul = (double) partyDmg / maxHp;
                    xpsp[0] *= partyMul;
                    xpsp[1] *= partyMul;
                    xpsp[0] = applyOverhit(killer, xpsp[0]);
                    party.distributeXpAndSp(xpsp[0], xpsp[1], rewardedMembers, lastAttacker, this);
                }
            }
        }

        // Check the drop of a cursed weapon
        CursedWeaponsManager.getInstance().dropAttackable(this, killer);

        if (!isRaid()) // С рейдов падают только топовые лайфстоны
        {
            double chancemod = ((L2NpcTemplate) _template).rateHp * Experience.penaltyModifier(calculateLevelDiffForDrop(topDamager.getLevel(), false), 9);

            // Дополнительный дроп материалов
            if (ConfigValue.AltMatherialsDrop && chancemod > 0 && (!isSeeded() || _seeded.isAltSeed()))
                for (L2DropData d : _matdrop)
                    if (getLevel() >= d.getMinLevel()) {
                        long count = Util.rollDrop(d.getMinDrop(), d.getMaxDrop(), d.getChance() * chancemod * RateService.getRateDropItems(killer) * killer.getRateItems(), true, killer);
                        if (count > 0)
                            dropItem(killer, d.getItemId(), count);
                    }
        }
    }

    /**
     * Моб уже формально мертв, но его труп еще нельзя использовать поскольку не закончен подсчет наград
     */
    public boolean isDying() {
        return _dying;
    }

    public void giveItem(L2ItemInstance item, boolean store) {
        if (_inventory == null)
            _inventory = new GArray<L2ItemInstance>();

        synchronized (_inventory) {
            if (item.isStackable())
                for (L2ItemInstance i : _inventory)
                    if (i.getItemId() == item.getItemId()) {
                        i.setCount(item.getCount() + i.getCount());
                        if (store)
                            i.updateDatabase(true, false);
                        return;
                    }

            _inventory.add(item);

            if (store) {
                item.setOwnerId(getNpcId());
                item.setLocation(ItemLocation.MONSTER);
                item.updateDatabase();
            }
        }
    }

    @Override
    public void onRandomAnimation() {
        // Action id для живности 1-3
        broadcastPacket(new SocialAction(getObjectId(), Rnd.get(1, 3)));
    }

    @Override
    public int getKarma() {
        return 0;
    }

    /**
     * This class contains all AbsorberInfo of the L2Attackable against the absorber L2Character.
     */
    public static final class AbsorberInfo {
        public int _objId;
        /**
         * The attacker L2Character concerned by this AbsorberInfo of this L2Attackable.
         */
        public double _absorbedHP;

        AbsorberInfo(int objId, double pAbsorbedHP) {
            _objId = objId;
            _absorbedHP = pAbsorbedHP;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof AbsorberInfo) {
                return (((AbsorberInfo) obj)._objId == _objId);
            }

            return false;
        }

        @Override
        public int hashCode() {
            return _objId;
        }
    }

    /**
     * Adds an attacker that successfully absorbed the soul of this L2NpcInstance into the _absorbersList.<BR><BR>
     *
     * params:  attacker  - a valid L2Player
     *      condition - an integer indicating the event when mob dies. This should be:
     *              = 0   - "the crystal scatters";
     *              = 1   - "the crystal failed to absorb. nothing happens";
     *              = 2   - "the crystal resonates because you got more than 1 crystal on you";
     *              = 3   - "the crystal cannot absorb the soul because the mob level is too low";
     *              = 4   - "the crystal successfuly absorbed the soul";
     */
    /**
     * Activate the absorbed soul condition on the L2Attackable.
     */
    public void absorbSoul() {
        _absorbed = true;
    }

    /**
     * @return True if the L2Attackable had his soul absorbed.
     */
    public boolean isAbsorbed() {
        return _absorbed;
    }

    /**
     * Adds an attacker that successfully absorbed the soul of this L2Attackable into the _absorbersList.
     *
     * @param attacker
     */
    public void addAbsorber(L2Player attacker) {
        // If we have no _absorbersList initiated, do it
        AbsorberInfo ai = _absorbersList.get(attacker.getObjectId());

        // If the L2Character attacker isn't already in the _absorbersList of this L2Attackable, add it
        if (ai == null) {
            ai = new AbsorberInfo(attacker.getObjectId(), getCurrentHp());
            _absorbersList.put(attacker.getObjectId(), ai);
        } else {
            ai._objId = attacker.getObjectId();
            ai._absorbedHP = getCurrentHp();
        }

        // Set this L2Attackable as absorbed
        absorbSoul();
    }

    public void resetAbsorbList() {
        _absorbed = false;
        _absorbersList.clear();
    }

    public TIntObjectHashMap<AbsorberInfo> getAbsorbersList() {
        return _absorbersList;
    }

    public L2ItemInstance takeHarvest() {
        harvestLock.lock();
        final L2ItemInstance harvest = _harvestItem;
        _harvestItem = null;
        _seeded = null;
        _seederId = 0;
        harvestLock.unlock();
        return harvest;
    }

    public void setSeeded(L2Item seed, L2Player player) {
        if (player == null)
            return;

        harvestLock.lock();
        try {
            _seeded = seed;
            _seederId = player.getObjectId();

            _harvestItem = ItemTemplates.getInstance().createItem(L2Manor.getInstance().getCropType(seed.getItemId()));
            // Количество всходов от xHP до (xHP + xHP/2)
            if (getTemplate().rateHp <= 1)
                _harvestItem.setCount(1);
            else
                _harvestItem.setCount(Rnd.get(Math.round(getTemplate().rateHp * ConfigValue.RateManor), Math.round(1.5 * getTemplate().rateHp * ConfigValue.RateManor)));
        } finally {
            harvestLock.unlock();
        }
    }

    public boolean isSeeded(L2Player seeder) {
        if (_seederId == 0)
            return false;
        return seeder.getObjectId() == _seederId || _dieTime + 10000 < System.currentTimeMillis();
    }

    public boolean isSeeded() {
        return _seeded != null;
    }

    /**
     * True if a Dwarf has used Spoil on this L2NpcInstance
     */
    private boolean _isSpoiled;

    /**
     * Return True if this L2NpcInstance has drops that can be sweeped.<BR><BR>
     */
    public boolean isSpoiled() {
        return _isSpoiled;
    }

    public boolean isSpoiled(L2Player spoiler) {
        L2Player this_spoiler;
        sweepLock.lock();
        try {
            if (!_isSpoiled) // если не заспойлен то false
                return false;
            this_spoiler = spoiler_ref.get();
        } finally {
            sweepLock.unlock();
        }
        if (this_spoiler == null || spoiler.getObjectId() == this_spoiler.getObjectId() && getDeadTime() < 20000)
            return true;
        if (getDistance(this_spoiler) > ConfigValue.AltPartyDistributionRange) // если спойлер слишком далеко разрешать
            return true;
        if (spoiler.getParty() != null && spoiler.getParty().containsMember(this_spoiler)) // сопартийцам тоже можно
            return true;
        return false;
    }

    /**
     * Set the spoil state of this L2NpcInstance.<BR><BR>
     *
     * @param spoiler
     */
    public void setSpoiled(boolean isSpoiled, L2Player spoiler) {
        sweepLock.lock();
        try {
            _isSpoiled = isSpoiled;
            if (spoiler != null)
                spoiler_ref = spoiler.getRef();
            else
                spoiler_ref = HardReferences.emptyRef();
        } finally {
            sweepLock.unlock();
        }
    }

    public L2Player getTopToDrop() {
        RewardInfo top_reward_info = null;
        L2Player player;
        int index = Util.contains_i1(getNpcId(), ConfigValue.RaidToRandomDrop, 0);
        GArray<AggroInfo> temp = new GArray<AggroInfo>();

        if (index > -1) {
            Log.add("L2MonsterInstance[" + getName() + "][" + getNpcId() + "][" + getLoc() + "][" + getReflectionId() + "]: getRndToDrop", "drop_debug");
            for (L2Playable playable : L2World.getAroundPlayables(this))
                if (playable != null) {
                    //_log.info("Npc["+getLoc()+"] getAggroList: "+playable);
                    HateInfo hateInfo = playable.getHateList().get(this);
                    Log.add("L2MonsterInstance[" + getName() + "][" + getNpcId() + "][" + getLoc() + "]: getAggroList: " + playable + " hateInfo[" + (hateInfo != null ? hateInfo.damage : 0) + "]=" + hateInfo, "drop_debug");
                    if (hateInfo != null && hateInfo.damage >= ConfigValue.RaidToRandomDrop[index][1]) {
                        AggroInfo aggroInfo = new AggroInfo(playable);
                        aggroInfo.hate = hateInfo.hate;
                        aggroInfo.damage = hateInfo.damage;
                        temp.add(aggroInfo);
                    }
                }
            if (temp.size() > 0) {
                AggroInfo info = temp.get(Rnd.get(temp.size()));
                player = info.attacker.getPlayer();
                top_reward_info = new RewardInfo(player, info.damage);
            }
        } else {
            Log.add("L2MonsterInstance[" + getName() + "][" + getNpcId() + "][" + getLoc() + "][" + getReflectionId() + "]: getTopToDrop", "drop_debug");
            for (L2Playable playable : L2World.getAroundPlayables(this))
                if (playable != null) {
                    //_log.info("Npc["+getLoc()+"] getAggroList: "+playable);
                    HateInfo hateInfo = playable.getHateList().get(this);
                    Log.add("L2MonsterInstance[" + getName() + "][" + getNpcId() + "][" + getLoc() + "]: getAggroList: " + playable + " hateInfo=" + hateInfo, "drop_debug");
                    if (hateInfo != null) {
                        AggroInfo aggroInfo = new AggroInfo(playable);
                        aggroInfo.hate = hateInfo.hate;
                        aggroInfo.damage = hateInfo.damage;
                        temp.add(aggroInfo);
                    }
                }

            Map<L2Player, RewardInfo> attackers = new HashMap<L2Player, RewardInfo>();
            RewardInfo reward;

            for (AggroInfo info : temp) {
                Log.add("L2MonsterInstance[" + getName() + "][" + getNpcId() + "]: info: " + info, "drop_debug");
                if (info == null)
                    continue;

                if (info.damage > 1 && info.attacker.isPlayable()) {
                    Log.add("L2MonsterInstance[" + getName() + "][" + getNpcId() + "]: info.damage: " + info.damage, "drop_debug");
                    if (info.attacker.isPlayer())
                        player = info.attacker.getPlayer();
                    else {
                        player = info.attacker.getPlayer();
                        if (info.attacker.getLevel() - info.attacker.getPlayer().getLevel() > 20)
                            continue;
                    }

                    L2Party party = player.getParty();
                    if (party != null)
                        player = party.getPartyMembers().get(0);
                    reward = attackers.get(player);

                    if (reward == null) {
                        reward = new RewardInfo(player, info.damage);
                        attackers.put(player, reward);
                    } else
                        reward.addDamage(info.damage);
                } else
                    Log.add("L2MonsterInstance[" + getName() + "][" + getNpcId() + "]: Err info.damage0: " + info.damage, "drop_debug");
            }

            for (RewardInfo reward_info : attackers.values())
                if (reward_info._attacker != null && (top_reward_info == null || reward_info._dmg > top_reward_info._dmg))
                    top_reward_info = reward_info;
        }
        return top_reward_info != null ? top_reward_info._attacker : null;
    }

    private void addReward(L2Player player, int index) {
        int item_id = (int) ConfigValue.RewardForMonsters[index][1];
        long item_count = ConfigValue.RewardForMonsters[index][2];

        if (item_id == L2Item.ITEM_ID_FAME)
            player.setFame((int) (player.getFame() + item_count), "KillMob");
        else if (item_id == L2Item.ITEM_ID_CLAN_REPUTATION_SCORE && player.getClan() != null)
            player.getClan().incReputation((int) (item_count), true, "KillMob");
        else if (item_id == L2Item.ITEM_ID_PC_BANG_POINTS)
            player.addPcBangPoints((int) (item_count), false, 1);
        else {
            player.getInventory().addItem(item_id, item_count);
            player.sendPacket(SystemMessage.obtainItems(item_id, item_count, 0));
        }
    }

    public void doItemDrop(L2Character topDamager) {
        int index_rb = Util.contains_i1(getNpcId(), ConfigValue.RaidToRandomDrop, 0);
        if (index_rb > -1) {
            GArray<AggroInfo> temp = new GArray<AggroInfo>();
            Log.add("L2MonsterInstance[" + getName() + "][" + getNpcId() + "][" + getLoc() + "][" + getReflectionId() + "]: getRndToDrop", "drop_debug");
            for (L2Playable playable : L2World.getAroundPlayables(this))
                if (playable != null) {
                    //_log.info("Npc["+getLoc()+"] getAggroList: "+playable);
                    HateInfo hateInfo = playable.getHateList().get(this);
                    Log.add("L2MonsterInstance[" + getName() + "][" + getNpcId() + "][" + getLoc() + "]: getAggroList: " + playable + " hateInfo[" + (hateInfo != null ? hateInfo.damage : 0) + "]=" + hateInfo, "drop_debug");
                    if (hateInfo != null && hateInfo.damage >= ConfigValue.RaidToRandomDrop[index_rb][1]) {
                        AggroInfo aggroInfo = new AggroInfo(playable);
                        aggroInfo.hate = hateInfo.hate;
                        aggroInfo.damage = hateInfo.damage;
                        temp.add(aggroInfo);
                    }
                }
            next_player:
            for (int i = 0; i < ConfigValue.RaidToRandomDrop[index_rb][2] && temp.size() > 0; i++) {
                AggroInfo info = temp.remove(Rnd.get(temp.size()));
                L2Player player = info.attacker.getPlayer();
                if (player != null) {
                    for (long[] item : ConfigValue.RaidToRandomDropList) {
                        if (Rnd.chance(item[2])) {
                            L2ItemInstance item_t;

                            for (long i2 = 0; i2 < item[1]; i2++) {
                                item_t = ItemTemplates.getInstance().createItem((int) item[0]);
                                if (item_t.isStackable()) {
                                    i2 = item[1];
                                    item_t.setCount(item[1]);
                                }

                                Announcements.getInstance().announceToAllC("AnnounceRaidToRandomDropList_" + getNpcId(), player.getName(), (item_t.getCount() + " " + item_t.getName()));

                                player.sendPacket(SystemMessage.obtainItems(item_t));
                                player.getInventory().addItem(item_t);
                            }
                            continue next_player;
                        }
                    }
                } else
                    i--;
            }
        } else {
            boolean boss = isRaid() || isBoss() || isEpicRaid() || isRefRaid();
            L2Player player = topDamager.getPlayer();
            if (boss) {
                player = getTopToDrop();
                if (ConfigValue.EnableClanPoint && player.getClanId() > 0)
                    for (int i = 0; i < ConfigValue.ClanPointRaidId.length; i++)
                        if (ConfigValue.ClanPointRaidId[i] == getNpcId()) {
                            player.getClan().clan_point += ConfigValue.ClanPointRaidCount[i];
                            PlayerData.getInstance().updateClanInDB(player.getClan());
                            player.getClan().sendMessageToAll("Начислено " + ConfigValue.ClanPointRaidCount[i] + " очков рейтинга ( Убийство Эпика ).");
                            break;
                        }
            }
            if (player == null) {
                player = topDamager.getPlayer();
                Log.add("L2MonsterInstance[" + getName() + "][" + getNpcId() + "]: ERROR TopToDrop==null, set_new_top_damager: " + player.getName(), "drop_debug");
            }
            if (c_ai3 != null && c_ai3.getPlayer() != null)
                player = c_ai3.getPlayer();
            if (player == null)
                return;

            if (ConfigValue.EnableRewardForMonsters) {
                int index = Util.getAIndexLong((long) getNpcId(), ConfigValue.RewardForMonsters, 0);
                if (index > -1) {
                    HashMap<L2Playable, AggroInfo> aggroList = getAggroMap();

                    if (aggroList != null && !aggroList.isEmpty()) {
                        for (L2Playable pl : aggroList.keySet())
                            if (pl.isPlayer() && pl.getReflectionId() == getReflectionId() && (pl.isInRange(this, ConfigValue.AltPartyDistributionRange) || pl.isInRange(player, ConfigValue.AltPartyDistributionRange)) && Math.abs(pl.getZ() - getZ()) < 400)
                                addReward(pl.getPlayer(), index);
                    }
					/*if(player.getParty() != null)
					{
						if(player.getParty().getCommandChannel() != null)
							for(L2Player member : player.getParty().getCommandChannel().getMembers())
								if(member.isInRange(this, ConfigValue.AltPartyDistributionRange) || member.isInRange(player, ConfigValue.AltPartyDistributionRange))
									addReward(member, index);
						else
							for(L2Player member2 : player.getParty().getPartyMembers())
								if(member2.isInRange(this, ConfigValue.AltPartyDistributionRange) || member2.isInRange(player, ConfigValue.AltPartyDistributionRange))
									addReward(member2, index);
					}
					else
						addReward(player, index);*/
                }
            }

            double mod = calcStat(Stats.DROP, 1., topDamager, null);
            double mod_adena = calcStat(Stats.ADENA, 1., topDamager, null);

            if (getTemplate().getDropData() != null) {
                GArray<ItemToDrop> drops = null;
                if (this instanceof L2ChestInstance)
                    drops = getTemplate().getDropData().rollChest(calculateLevelDiffForDrop(topDamager.getLevel(), false), this, player, mod);
                else
                    drops = getTemplate().getDropData().rollDrop(calculateLevelDiffForDrop(topDamager.getLevel(), false), this, player, mod, mod_adena);
                if (boss)
                    Log.add(getTypeName() + ": " + getName() + "[" + getNpcId() + "] seeded[" + (_seeded == null ? "NULL" : _seeded.isAltSeed()) + "] to drop " + player + " drop_count[" + drops.size() + "]", "drop_boss_debug");
                for (ItemToDrop drop : drops) {
                    if (boss)
                        Log.add("\tDrop [" + drop.itemId + "][" + drop.count + "]", "drop_boss_debug");
                    // Если в моба посеяно семя, причем не альтернативное - не давать никакого дропа, кроме адены.
                    if (_seeded != null && !_seeded.isAltSeed() && !drop.isAdena)
                        continue;
                    if (drop.isAdena) {
                        if (getChampion() > 0)
                            dropItem(player, drop.itemId, (drop.count * ConfigValue.ChampionAdenasRewards));
                        else {
                            dropItem(player, drop.itemId, (int) (drop.count));
                            continue;
                        }
                    }
                    if (getChampion() > 0 && !drop.isAdena)
                        dropItem(player, drop.itemId, (long) (drop.count * ConfigValue.ChampionRewards));
                    else if (ConfigValue.DoubleDropAdenaForChamp)
                        dropItem(player, drop.itemId, drop.count);
                }
            }
            if (getChampion() > 0 && (ConfigValue.ChampionRewardLowerLvlItemChance > 0 || ConfigValue.ChampionRewardHigherLvlItemChance > 0)) {
                int champqty = Rnd.get(ConfigValue.ChampionRewardItemQtyMin[getChampion() - 1], ConfigValue.ChampionRewardItemQtyMax[getChampion() - 1]);

                L2ItemInstance item = ItemTemplates.getInstance().createItem(ConfigValue.ChampionRewardItemID);
                item.setCount(champqty);

                if (player.getLevel() <= getLevel() && (Rnd.get(100) < ConfigValue.ChampionRewardLowerLvlItemChance))
                    dropItem(player, item);
                else if (player.getLevel() > getLevel() && (Rnd.get(100) < ConfigValue.ChampionRewardHigherLvlItemChance))
                    dropItem(player, item);
            }
            if (getChampion() > 0 && ConfigValue.ChampionRewardFame.length > 0 && ConfigValue.ChampionRewardFame[0] > 0) {
                if (getChampion() == 1)
                    player.setFame(player.getFame() + (getLevel() * ConfigValue.ChampionRewardFame[0]), "KillChamp");
                else
                    player.setFame(player.getFame() + (getLevel() * ConfigValue.ChampionRewardFame[1]), "KillChamp");
            }

            if (_inventory != null)
                synchronized (_inventory) {
                    for (L2ItemInstance drop : _inventory)
                        if (drop != null) {
                            player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2MonsterInstance.ItemBelongedToOther", player).addString(drop.getName()));
                            dropItem(player, drop);
                        }
                    if (_inventory != null)
                        _inventory.clear();
                    _inventory = null;
                }

            GArray<L2ItemInstance> templateInv = getTemplate().takeInventory();
            if (templateInv != null) {
                for (L2ItemInstance drop : templateInv)
                    if (drop != null) {
                        player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2MonsterInstance.ItemBelongedToOther", player).addString(drop.getName()));
                        dropItem(player, drop);
                    }
                if (_inventory != null)
                    _inventory.clear();
                _inventory = null;
            }
        }
    }

    protected void doSweepDrop(final L2Character lastAttacker, L2Character topDamager) {
        if (lastAttacker == null)
            return;

        final L2Player player = lastAttacker.getPlayer();

        if (player == null)
            return;

        final int levelDiff = calculateLevelDiffForDrop(topDamager.getLevel(), true);

        final GArray<L2ItemInstance> spoiled = new GArray<L2ItemInstance>();

        if (getTemplate().getDropData() != null) {
            double mod = calcStat(Stats.SPOIL, 1., lastAttacker, null);
            final GArray<ItemToDrop> spoils = getTemplate().getDropData().rollSpoil(levelDiff, this, player, mod);
            for (final ItemToDrop spoil : spoils) {
                final L2ItemInstance dropit = ItemTemplates.getInstance().createItem(spoil.itemId);
                dropit.setCount(spoil.count);
                spoiled.add(dropit);
            }
        }

        if (spoiled.size() > 0)
            _sweepItems = spoiled.toArray(new L2ItemInstance[spoiled.size()]);
    }

    protected double[] calculateExpAndSp(L2Character attacker, int level, long damage) {
        if (!isInRange(attacker, ConfigValue.AltPartyDistributionRange) && Math.abs(attacker.getZ() - getZ()) < 400)
            return new double[]{0., 0., 0., 0.};

        int diff = level - getLevel();
        if (level > 77 && diff > 3 && diff <= 5) // kamael exp penalty
            diff += 3;

        double xp = getExpReward() * damage / getMaxHp();
        double sp = getSpReward() * damage / getMaxHp();

        if (diff > 5) {
            double mod = Math.pow(.83, diff - 5);
            xp *= mod;
            sp *= mod;
        }

        xp = Math.max(0, xp);
        sp = Math.max(0, sp);

        return new double[]{xp, sp, 0., 0.};
    }

    protected double applyOverhit(L2Player killer, double xp) {
        if (xp > 0 && getOverhitAttacker() != null && killer == getOverhitAttacker()) {
            int overHitExp = calculateOverhitExp(xp);
            killer.sendPacket(Msg.OVER_HIT, new SystemMessage(SystemMessage.ACQUIRED_S1_BONUS_EXPERIENCE_THROUGH_OVER_HIT).addNumber(overHitExp));
            xp += overHitExp;
        }
        return xp;
    }

    public L2Character getOverhitAttacker() {
        return overhit_attacker_ref.get();
    }

    @Override
    public void setOverhitAttacker(L2Character overhitAttacker) {
        if (overhitAttacker != null)
            overhit_attacker_ref = overhitAttacker.getRef();
        else
            overhit_attacker_ref = HardReferences.emptyRef();
    }

    public double getOverhitDamage() {
        return _overhitDamage;
    }

    @Override
    public void setOverhitDamage(double damage) {
        _overhitDamage = damage;
    }

    public int calculateOverhitExp(final double normalExp) {
        double overhitPercentage = getOverhitDamage() * 100 / getMaxHp();
        if (overhitPercentage > 25)
            overhitPercentage = 25;
        double overhitExp = overhitPercentage / 100 * normalExp;
        overhit_attacker_ref = HardReferences.emptyRef();
        setOverhitDamage(0);
        return (int) Math.round(overhitExp);
    }

    /**
     * Return True if a Dwarf use Sweep on the L2NpcInstance and if item can be spoiled.<BR><BR>
     */
    public boolean isSweepActive() {
        dyingLock.lock();
        try {
            return _sweepItems != null && _sweepItems.length > 0;
        } finally {
            dyingLock.unlock();
        }
    }

    /**
     * Return table containing all L2ItemInstance that can be spoiled.<BR><BR>
     */
    public L2ItemInstance[] takeSweep() {
        L2ItemInstance[] sweep;
        sweepLock.lock();
        try {
            sweep = (_sweepItems == null || _sweepItems.length == 0) ? null : _sweepItems.clone();
            _sweepItems = null;
        } finally {
            sweepLock.unlock();
        }
        return sweep;
    }

    @Override
    public int isUnDying() {
        return getTemplate().undying;
    }

    @Override
    public boolean isInvul() {
        return _isInvul || _isInvul_skill != null;
    }

    @Override
    public boolean isAggressive() {
        return (ConfigValue.AltChampionAggro || getChampion() == 0) && super.isAggressive();
    }

    @Override
    public String getFactionId() {
        return ConfigValue.AltChampionSocial || getChampion() == 0 ? super.getFactionId() : "";
    }

    @Override
    public String toString() {
        return "Mob " + getName() + " [" + getNpcId() + "][" + (!isDead()) + "][" + getObjectId() + "]";
    }

    // Не отображаем на монстрах значки клана.
    @Override
    public boolean isCrestEnable() {
        return false;
    }

    @Override
    public boolean isEpicRaid() {
        return getNpcId() == 29181 || getNpcId() == 29179 || getNpcId() == 29180 || getNpcId() == 29047 || getNpcId() == 29068 || getNpcId() == 29020 || getNpcId() == 29028 || getNpcId() == 29062 || getNpcId() == 29065 || getNpcId() == 29186 || getNpcId() == 25700 || getNpcId() == 25699 || getNpcId() == 29178 || getNpcId() == 29177;
    }

    @Override
    public boolean isMonster() {
        return true;
    }

    public boolean can_drop_epaulette(L2Player player) {
        return true;
    }
}