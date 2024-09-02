package l2open.gameserver.model.instances;

import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Events;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.Scripts;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.L2CharacterAI;
import l2open.gameserver.cache.*;
import l2open.gameserver.clientpackets.RequestExRemoveItemAttribute;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2ObjectTasks.NotifyFactionTask;
import l2open.gameserver.model.L2ObjectTasks.RandomAnimationTask;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.barahlo.CertificationFunctions;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.entity.DimensionalRift;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.gameserver.model.entity.siege.clanhall.RainbowSpringSiege;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestEventType;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.serverpackets.ExEnchantSkillList.EnchantSkillType;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncTemplate;
import l2open.gameserver.tables.*;
import l2open.gameserver.tables.TeleportTable.TeleportLocation;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.taskmanager.DecayTaskManager;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.gameserver.xml.ItemTemplates;

import l2open.pts.loader.*;
import l2open.util.*;
import l2open.util.reference.HardReference;

import java.io.File;
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import static l2open.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;
import static l2open.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;

public class L2NpcInstance extends L2Character {
    private long _lastFactionNotifyTime = 0;
    public int minFactionNotifyInterval = 500;
    public boolean hasChatWindow = true;
    protected long _dieTime = 0;
    public long _currentTick = 0;
    private int _personalAggroRange = -1;
    private byte _level = 0;
    private int _weaponEnchant = -1;
    private boolean _isHideName = false;
    private int _currentLHandId;
    private int _currentRHandId;

    private double _currentCollisionRadius;
    private double _currentCollisionHeight;

    /**
     * Нужно для отображения анимации спауна, используется в пакете NpcInfo:
     * 0=false, 1=true, 2=summoned (only works if model has a summon animation)
     **/
    private int _showSpawnAnimation = 2;

    public long pathfindCount;
    public long pathfindTime;

    private int _agroRange = 0;
    private int _eventFlag = 0;
    public byte _targetable = 1;
    private L2Player MPCC_Master = null;
    private int MPR = 0;
    public int _nps_string_name = -1;
    public int _nps_string_title = -1;

    /**
     * The character that summons this NPC.
     */
    private L2Character _summoner = null;

    public void callFriends(L2Character attacker, int damage) {
        callFriends(attacker, damage, false);
    }

    public void callFriends(L2Character attacker, boolean kill) {
        callFriends(attacker, 0, kill);
    }

    public void callFriends(L2Character attacker) {
        callFriends(attacker, 0, false);
    }

    public void callFriends(L2Character attacker, int damage, boolean kill) {
        if (attacker == null)
            return;
        if (!kill) {
            if (System.currentTimeMillis() - _lastFactionNotifyTime > minFactionNotifyInterval) {
                if (isMonster())
                    if (isMinion()) {
                        // Call master
                        L2MonsterInstance master = ((L2MinionInstance) this).getLeader();
                        if (master != null) {
                            if (!master.isInCombat() && !master.isDead())
                                master.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, new Object[]{attacker, Rnd.get(1, 100)});
                            master.getAI().notifyEvent(CtrlEvent.EVT_PARTY_ATTACKED, new Object[]{attacker, this, damage});
                            MinionList list = master.getMinionList();
                            if (list != null)
                                for (L2MinionInstance m : list.getSpawnedMinions())
                                    if (m != this) {
                                        m.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, new Object[]{attacker, Rnd.get(1, 100)});
                                        m.getAI().notifyEvent(CtrlEvent.EVT_PARTY_ATTACKED, new Object[]{attacker, this, damage});
                                    }
                        }
                    } else
                        // Call minions
                        callMinionsToAssist(attacker);

                if (getFactionId() != null && !getFactionId().isEmpty())
                    // call friend's
                    ThreadPoolManager.getInstance().schedule(new NotifyFactionTask(this, attacker, damage, false), 100);

                _lastFactionNotifyTime = System.currentTimeMillis();
            }
        } else if (getFactionId() != null && !getFactionId().isEmpty())
            ThreadPoolManager.getInstance().schedule(new NotifyFactionTask(this, attacker, damage, true), 100);
    }

    protected static final Logger _log = Logger.getLogger(L2NpcInstance.class.getName());
    private final ClassId[] _classesToTeach;

    /** The delay after witch the attacked is stopped */
    private long _attack_timeout;
    private Location _spawnedLoc = null;

    private static FastMap<String, Constructor<?>> _ai_constructors = new FastMap<String, Constructor<?>>().setShared(true);
    private final ReentrantLock getAiLock = new ReentrantLock(), decayLock = new ReentrantLock();

    @Override
    public L2CharacterAI getAI() {
        if (_ai != null)
            return _ai;

        getAiLock.lock();
        try {
            if (_ai == null) {
                Constructor<?> ai_constructor = _ai_constructors.get(getTemplate().ai_type);
                if (ai_constructor != null)
                    try {
                        _ai = (L2CharacterAI) ai_constructor.newInstance(this);
                        //System.out.println("L2NpcInstance: "+getTemplate().ai_type);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                if (_ai == null)
                    _ai = new L2CharacterAI(this);
            }
        } finally {
            getAiLock.unlock();
        }
        return _ai;
    }

    public void setAttackTimeout(long time) {
        _attack_timeout = time;
    }

    public long getAttackTimeout() {
        return _attack_timeout;
    }

    /**
     * Return the position of the spawned point.<BR><BR>
     * Может возвращать случайную точку, поэтому всегда следует кешировать результат вызова!
     */
    public Location getSpawnedLoc() {
        return _spawnedLoc;
    }

    public void setSpawnedLoc(Location loc) {
        _spawnedLoc = loc;
    }

    public L2Character getMyLeader() {
        return boss;
    }

    public L2Character boss = null;

    public L2NpcInstance(int objectId, L2NpcTemplate template) {
        super(objectId, template);

        if (template == null) {
            _log.warning("No template for Npc. Please check your datapack is setup correctly.");
            throw new IllegalArgumentException();
        }

        _classesToTeach = template.getTeachInfo();
        setNameCreate(template.name);
        setTitleCreate(template.title);

        setFlying(template.isFlying);

        String implementationName = template.ai_type;

        Constructor<?> ai_constructor = _ai_constructors.get(implementationName);

        if (ai_constructor == null) {
            try {
                if (!implementationName.equalsIgnoreCase("npc"))
                    ai_constructor = Class.forName("l2open.gameserver.ai." + implementationName).getConstructors()[0];
            } catch (Exception e) {
                try {
                    ai_constructor = Scripts.getInstance().getClasses().get("ai." + implementationName).getRawClass().getConstructors()[0];
                } catch (Exception e1) {
                    _log.warning("AI type " + template.ai_type + " not found!");
                    e1.printStackTrace();
                }
            }

            //System.out.println("L2NpcInstance2: "+getTemplate().ai_type);

            if (ai_constructor != null)
                _ai_constructors.put(implementationName, ai_constructor);
        }

        // Монстров тоже исключаем.
        if (hasRandomAnimation() && ai_constructor == null && !isMonster())
            startRandomAnimation();

        // инициализация параметров оружия
        _currentLHandId = getTemplate().lhand;
        _currentRHandId = getTemplate().rhand;
        _weaponEnchant = _weaponEnchant < 0 && Rnd.chance(ConfigValue.MonstersWeaponEnchantChance) ? Rnd.get((ConfigValue.MonstersWeaponEnchantMin), ConfigValue.MonstersWeaponEnchantMax) : 0;

        // инициализация коллизий
        _currentCollisionHeight = getTemplate().collisionHeight;
        _currentCollisionRadius = getTemplate().collisionRadius;

        FuncTemplate baseFireRes = new FuncTemplate(null, "Add", Stats.FIRE_RECEPTIVE, 0x40, getTemplate().baseFireRes == 0 ? 20 : getTemplate().baseFireRes);
        FuncTemplate baseWindRes = new FuncTemplate(null, "Add", Stats.WIND_RECEPTIVE, 0x40, getTemplate().baseWindRes == 0 ? 20 : getTemplate().baseWindRes);
        FuncTemplate baseWaterRes = new FuncTemplate(null, "Add", Stats.WATER_RECEPTIVE, 0x40, getTemplate().baseWaterRes == 0 ? 20 : getTemplate().baseWaterRes);
        FuncTemplate baseEarthRes = new FuncTemplate(null, "Add", Stats.EARTH_RECEPTIVE, 0x40, getTemplate().baseEarthRes == 0 ? 20 : getTemplate().baseEarthRes);
        FuncTemplate baseDarkRes = new FuncTemplate(null, "Add", Stats.UNHOLY_RECEPTIVE, 0x40, getTemplate().baseDarkRes == 0 ? 20 : getTemplate().baseDarkRes);
        FuncTemplate baseHolyRes = new FuncTemplate(null, "Add", Stats.SACRED_RECEPTIVE, 0x40, getTemplate().baseHolyRes == 0 ? 20 : getTemplate().baseHolyRes);

        FuncTemplate elemAtkPower = null;

        switch (getTemplate().atkElement) {
            case 0:
                elemAtkPower = new FuncTemplate(null, "Add", Stats.ATTACK_ELEMENT_FIRE, 0x40, getTemplate().elemAtkPower);
                break;
            case 1:
                elemAtkPower = new FuncTemplate(null, "Add", Stats.ATTACK_ELEMENT_WATER, 0x40, getTemplate().elemAtkPower);
                break;
            case 2:
                elemAtkPower = new FuncTemplate(null, "Add", Stats.ATTACK_ELEMENT_WIND, 0x40, getTemplate().elemAtkPower);
                break;
            case 3:
                elemAtkPower = new FuncTemplate(null, "Add", Stats.ATTACK_ELEMENT_EARTH, 0x40, getTemplate().elemAtkPower);
                break;
            case 4:
                elemAtkPower = new FuncTemplate(null, "Add", Stats.ATTACK_ELEMENT_SACRED, 0x40, getTemplate().elemAtkPower);
                break;
            case 5:
                elemAtkPower = new FuncTemplate(null, "Add", Stats.ATTACK_ELEMENT_UNHOLY, 0x40, getTemplate().elemAtkPower);
                break;
        }

        if (getTemplate().atkElement > -1)
            addStatFunc(elemAtkPower.getFunc(this));

        addStatFunc(baseFireRes.getFunc(this));
        addStatFunc(baseWindRes.getFunc(this));
        addStatFunc(baseWaterRes.getFunc(this));
        addStatFunc(baseEarthRes.getFunc(this));
        addStatFunc(baseDarkRes.getFunc(this));
        addStatFunc(baseHolyRes.getFunc(this));
    }

    public int getRightHandItem() {
        return _currentRHandId;
    }

    public int getLeftHandItem() {
        return _currentLHandId;
    }

    public void setLHandId(int newWeaponId) {
        _currentLHandId = newWeaponId;
    }

    public void setRHandId(int newWeaponId) {
        _currentRHandId = newWeaponId;
    }

    public void setHideName(boolean val) {
        _isHideName = val;
    }

    public boolean isHideName() {
        return _isHideName;
    }

    public double getCollisionHeight() {
        return _currentCollisionHeight;
    }

    public void setCollisionHeight(double offset) {
        _currentCollisionHeight = offset;
    }

    public double getCollisionRadius() {
        return _currentCollisionRadius;
    }

    public void setCollisionRadius(double collisionRadius) {
        _currentCollisionRadius = collisionRadius;
    }

    @Override
    public void doDie(L2Character killer) {
        _dieTime = System.currentTimeMillis();
        setDecayed(false);

        if (isMonster() && (((L2MonsterInstance) this).isSeeded() || ((L2MonsterInstance) this).isSpoiled()))
            DecayTaskManager.getInstance().addDecayTask(this, 20000);
        else
            DecayTaskManager.getInstance().addDecayTask(this);

        // установка параметров оружия и коллизий по умолчанию
        _currentLHandId = getTemplate().lhand;
        _currentRHandId = getTemplate().rhand;
        _currentCollisionHeight = getTemplate().collisionHeight;
        _currentCollisionRadius = getTemplate().collisionRadius;

        getAI().stopAITask();
        super.doDie(killer);

        clearAggroList(false);
    }

    public int IsMyLord(L2Player player) {
        return player.isCastleLord(getCastle().getId()) ? 1 : 0;
    }

    public boolean isDominionLord(L2Player player) {
        return getCastle().getDominionLord() == player.getObjectId();
    }

    public int IsDominionOfLord(int id) {
        return CastleManager.getInstance().getCastleByIndex(id - 80).getDominionLord() > 0 ? 1 : 0;
    }

    public class AggroInfo {
        public L2Playable attacker;
        public int hate;
        public int damage;

        public AggroInfo(L2Playable attacker) {
            this.attacker = attacker;
        }
    }

    public long getDeadTime() {
        if (_dieTime <= 0)
            return 0;
        return System.currentTimeMillis() - _dieTime;
    }

    public HashMap<L2Playable, AggroInfo> getAggroMap() {
        HashMap<L2Playable, AggroInfo> temp = new HashMap<L2Playable, AggroInfo>();
        for (L2Playable playable : L2World.getAroundPlayables(this))
            if (playable != null) {
                HateInfo hateInfo = playable.getHateList().get(this);
                if (hateInfo != null) {
                    AggroInfo aggroInfo = new AggroInfo(playable);
                    aggroInfo.hate = hateInfo.hate;
                    aggroInfo.damage = hateInfo.damage;
                    temp.put(playable, aggroInfo);
                }
            }
        return temp;
    }

    public GArray<AggroInfo> getAggroList() {
        GArray<AggroInfo> temp = new GArray<AggroInfo>();
        for (L2Playable playable : L2World.getAroundPlayables(this))
            if (playable != null) {
                //if(getNpcId() == 29177)
                //	_log.info("Npc["+getLoc()+"] getAggroList: "+playable);
                HateInfo hateInfo = playable.getHateList().get(this);
                if (hateInfo != null) {
                    AggroInfo aggroInfo = new AggroInfo(playable);
                    aggroInfo.hate = hateInfo.hate;
                    aggroInfo.damage = hateInfo.damage;
                    temp.add(aggroInfo);
                }
            }
        return temp;
    }

    public GArray<L2Playable> getAggroListPlayable() {
        GArray<L2Playable> temp = new GArray<L2Playable>();
        for (L2Playable playable : L2World.getAroundPlayables(this))
            if (playable != null && playable.getHateList().get(this) != null)
                temp.add(playable);
        return temp;
    }

    public void clearAggroList(boolean onlyHate) {
        for (L2Playable playable : L2World.getAroundPlayables(this))
            if (playable != null)
                playable.removeFromHatelist(this, onlyHate);
    }

    public L2Character getMostHated() {
        L2Character target = getAI().getAttackTarget();
        if (target != null && target.isNpc() && target.isVisible() && target != this && !target.isDead() && target.isInRange(this, 2000))
            return target;

        GArray<AggroInfo> aggroList = getAggroList();

        GArray<AggroInfo> activeList = new GArray<AggroInfo>();
        GArray<AggroInfo> passiveList = new GArray<AggroInfo>();

        for (AggroInfo ai : aggroList)
            if (ai.hate > 0) {
                L2Playable cha = ai.attacker;
                if (cha != null)
                    if (!cha.isSummon() && (cha.isStunned() || cha.isActionBlock() || cha.isSleeping() || cha.isParalyzed() || cha.isAfraid() || cha.isBlocked()))
                        passiveList.add(ai);
                    else
                        activeList.add(ai);
            }

        if (!activeList.isEmpty())
            aggroList = activeList;
        else
            aggroList = passiveList;

        AggroInfo mosthated = null;

        for (AggroInfo ai : aggroList)
            if (mosthated == null)
                mosthated = ai;
            else if (mosthated.hate < ai.hate)
                mosthated = ai;

        return mosthated != null ? mosthated.attacker : null;
    }

    public L2Character getRandomHated() {
        GArray<AggroInfo> aggroList = getAggroList();

        GArray<AggroInfo> activeList = new GArray<AggroInfo>();
        GArray<AggroInfo> passiveList = new GArray<AggroInfo>();

        for (AggroInfo ai : aggroList)
            if (ai.hate > 0) {
                L2Playable cha = ai.attacker;
                if (cha != null)
                    if (cha.isStunned() || cha.isActionBlock() || cha.isSleeping() || cha.isParalyzed() || cha.isAfraid() || cha.isBlocked() || Math.abs(cha.getZ() - getZ()) > 200)
                        passiveList.add(ai);
                    else
                        activeList.add(ai);
            }

        if (!activeList.isEmpty())
            aggroList = activeList;
        else
            aggroList = passiveList;

        if (!aggroList.isEmpty())
            return aggroList.get(Rnd.get(aggroList.size())).attacker;
        return null;
    }

    public boolean isNoTarget() {
        return getAggroList().size() == 0;
    }

    public void dropItem(L2Player lastAttacker, int itemId, long itemCount) {
        if (itemCount == 0 || lastAttacker == null)
            return;

        if (ConfigValue.DropCounter)
            lastAttacker.incrementDropCounter(itemId, itemCount);

        L2ItemInstance item;

        for (long i = 0; i < itemCount; i++) {
            item = ItemTemplates.getInstance().createItem(itemId);

            // Set the Item quantity dropped if L2ItemInstance is stackable
            if (item.isStackable()) {
                i = itemCount; // Set so loop won't happent again
                item.setCount(itemCount); // Set item count
            }

            if (isRaid() || isRefRaid()) {
                SystemMessage sm;
                if (itemId == 57) {
                    sm = new SystemMessage(SystemMessage.S1_DIED_AND_HAS_DROPPED_S2_ADENA);
                    sm.addString(getName());
                    sm.addNumber(item.getCount());
                } else {
                    sm = new SystemMessage(SystemMessage.S1_DIED_AND_DROPPED_S3_S2);
                    sm.addString(getName());
                    sm.addItemName(itemId);
                    sm.addNumber(item.getCount());
                }
                broadcastPacket(sm);
            }

            lastAttacker.doAutoLootOrDrop(item, this);
        }
    }

    public void dropItem(L2Player lastAttacker, L2ItemInstance item) {
        if (item.getCount() == 0)
            return;

        if (isRaid() || isRefRaid()) {
            SystemMessage sm;
            if (item.getItemId() == 57) {
                sm = new SystemMessage(SystemMessage.S1_DIED_AND_HAS_DROPPED_S2_ADENA);
                sm.addString(getName());
                sm.addNumber(item.getCount());
            } else {
                sm = new SystemMessage(SystemMessage.S1_DIED_AND_DROPPED_S3_S2);
                sm.addString(getName());
                sm.addItemName(item.getItemId());
                sm.addNumber(item.getCount());
            }
            broadcastPacket(sm);
        }

        lastAttacker.doAutoLootOrDrop(item, this);
    }

    public L2ItemInstance getActiveWeapon() {
        return null;
    }

    @Override
    public boolean isAttackable(L2Character attacker) {
        return /*getTemplate().can_be_attacked == 1 || ConfigValue.TestAllAttack && */true;
    }

    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        return false;
    }

    @Override
    public void onSpawn() {
        setDecayed(false);
        _dieTime = 0;
        _currentTick = System.currentTimeMillis();
    }

    @Override
    public void spawnMe() {
        super.spawnMe();
        getAI().notifyEvent(CtrlEvent.EVT_SPAWN);
    }

    @Override
    public L2NpcTemplate getTemplate() {
        return (L2NpcTemplate) _template;
    }

    @Override
    public int getNpcId() {
        return getTemplate().npcId;
    }

    protected boolean _unAggred = false;

    public void setUnAggred(boolean state) {
        _unAggred = state;
    }

    /**
     * Return True if the L2NpcInstance is aggressive (ex : L2MonsterInstance in function of aggroRange).<BR><BR>
     */
    public boolean isAggressive() {
        return getAggroRange() > 0;
    }

    public int getAggroRange() {
        if (_unAggred)
            return 0;

        if (_personalAggroRange >= 0)
            return _personalAggroRange;

        return getTemplate().aggroRange;
    }

    /**
     * Устанавливает данному npc новый aggroRange.
     * Если установленый aggroRange < 0, то будет братся аггрорейндж с темплейта.
     * @param aggroRange новый agrroRange
     */
    public void setAggroRange(int aggroRange) {
        _personalAggroRange = aggroRange;
    }

    public int getFactionRange() {
        return getTemplate().factionRange;
    }

    /**
     * Возвращает группу социальности или пустой String (не null)
     */
    public String getFactionId() {
        return getTemplate().factionId;
    }

    public long getExpReward() {
        return (long) calcStat(Stats.EXP, getTemplate().revardExp, null, null);
    }

    public long getSpReward() {
        return (long) calcStat(Stats.SP, getTemplate().revardSp, null, null);
    }

    @Override
    public void deleteMe() {
        super.deleteMe();
        if (_spawn != null)
            _spawn.stopRespawn();
        setSpawn(null);
        getAI().stopAITask();
    }

    private L2Spawn _spawn;

    public L2Spawn getSpawn() {
        return _spawn;
    }

    public void setSpawn(L2Spawn spawn) {
        _spawn = spawn;
    }

    @Override
    public void onDecay() {
        decayLock.lock();
        try {
            if (isDecayed())
                return;
            setDecayed(true);

            super.onDecay();

            if (_spawn != null)
                _spawn.decreaseCount(this);
            else
                deleteMe(); // Если этот моб заспавнен не через стандартный механизм спавна значит посмертие ему не положено и он умирает насовсем
        } finally {
            decayLock.unlock();
        }
    }

    private boolean _isDecayed = false;

    public final void setDecayed(boolean mode) {
        _isDecayed = mode;
    }

    public final boolean isDecayed() {
        return _isDecayed;
    }

    public void endDecayTask() {
        DecayTaskManager.getInstance().cancelDecayTask(this);
        onDecay();
    }

    @Override
    public boolean isUndead() {
        return getTemplate().isUndead();
    }

    public void setLevel(byte level) {
        _level = level;
    }

    @Override
    public byte getLevel() {
        return _level == 0 ? getTemplate().level : _level;
    }

    private int _displayId = 0;

    public void setDisplayId(int displayId) {
        _displayId = displayId;
    }

    public int getDisplayId() {
        return _displayId > 0 ? _displayId : getTemplate().displayId;
    }

    @Override
    public L2ItemInstance getActiveWeaponInstance() {
        // regular NPCs dont have weapons instancies
        return null;
    }

    @Override
    public WeaponType getFistWeaponType() {
        return getTemplate().base_attack_type;
    }

    @Override
    public L2Weapon getActiveWeaponItem() {
        // Get the weapon identifier equipped in the right hand of the L2NpcInstance
        int weaponId = getTemplate().rhand;

        if (weaponId < 1)
            return null;

        // Get the weapon item equipped in the right hand of the L2NpcInstance
        L2Item item = ItemTemplates.getInstance().getTemplate(getTemplate().rhand);

        if (!(item instanceof L2Weapon))
            return null;

        return (L2Weapon) item;
    }

    @Override
    public L2ItemInstance getSecondaryWeaponInstance() {
        // regular NPCs dont have weapons instances
        return null;
    }

    @Override
    public L2Weapon getSecondaryWeaponItem() {
        // Get the weapon identifier equipped in the right hand of the L2NpcInstance
        int weaponId = getTemplate().lhand;

        if (weaponId < 1)
            return null;

        // Get the weapon item equipped in the right hand of the L2NpcInstance
        L2Item item = ItemTemplates.getInstance().getTemplate(getTemplate().lhand);

        if (!(item instanceof L2Weapon))
            return null;

        return (L2Weapon) item;
    }

    @Override
    public void updateAbnormalEffect() {
        if (isFlying()) // FIXME
            return;
        for (L2Player _cha : L2World.getAroundPlayers(this))
            _cha.sendPacket(new NpcInfo(this, _cha));
    }

    // У NPC всегда 2
    public void onRandomAnimation() {
        broadcastPacket2(new SocialAction(getObjectId(), 2));
        _lastSocialAction = System.currentTimeMillis();
    }

    public void startRandomAnimation() {
        new RandomAnimationTask(this);
    }

    @Override
    public boolean hasRandomAnimation() {
        if (ConfigValue.MaxNPCAnimation <= 0)
            return false;
        if (getTemplate().randomAnimationDisabled)
            return false;
        return true;
    }

    @Override
    public boolean isInvul() {
        return ConfigValue.AllNpcInvull || getNpcId() == 36402;
    }

    public Castle getCastle() {
        if (ConfigValue.NoCastleTaxInOffshore && (getReflection().getId() != 0 || isInZone(ZoneType.offshore)))
            return null;
        return TownManager.getInstance().getClosestTown(this).getCastle();
    }

    public Castle getCastle(L2Player player) {
        return getCastle();
    }

    private int _fortressId = -1;

    public Fortress getFortress() {
        if (_fortressId < 0)
            _fortressId = FortressManager.getInstance().findNearestFortressIndex(getX(), getY(), 32768); // 32768
        return FortressManager.getInstance().getFortressByIndex(_fortressId);
    }

    private int _clanHallId = -1;

    public ClanHall getClanHall() {
        if (_clanHallId < 0)
            _clanHallId = ClanHallManager.getInstance().findNearestClanHallIndex(getX(), getY(), 15000); // 32768
        return ClanHallManager.getInstance().getClanHall(_clanHallId);
    }

    private long _lastSocialAction;

    @Override
    public void onAction(L2Player player, boolean shift, int addDist) {
        if (ConfigValue.DebugOnAction)
            _log.info("DebugOnAction: L2NPC:onAction->start");
        //Util.test();
        if (_targetable != 1) {
            player.sendActionFailed();
            return;
        }

        if (player.getTarget() != this) {
            player.setTarget(this);
            if (player.getTarget() == this) {
                if (this instanceof SeducedInvestigatorInstance || isAutoAttackable(player))
                    player.sendPacket(makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.p_max_hp));
            }
            player.sendActionFailed();
            return;
        }

        if (Events.onAction(player, this, shift)) {
            player.sendActionFailed();
            return;
        }

        if (isAutoAttackable(player)) {
            if (ConfigValue.DebugOnAction)
                _log.info("DebugOnAction: L2NPC:onAction->AI:Attack");
            player.getAI().Attack(this, false, shift);
            return;
        }

        if (ConfigValue.DebugOnAction)
            _log.info("DebugOnAction: L2NPC:onAction->1[" + INTERACTION_DISTANCE + "][" + addDist + "][" + getDistance(player) + "]");
        if (!isInRange(player, INTERACTION_DISTANCE + addDist) || Math.abs(player.getZ() - getZ()) > 100) {
            if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, 100);
            player.sendActionFailed();
            return;
        }

        if (!ConfigValue.AltKarmaPlayerCanShop && player.getKarma() > 0 && !player.isGM() && !(this instanceof L2WarehouseInstance || this instanceof L2ResidenceManager || this instanceof L2ClanHallDoormenInstance || this instanceof L2CastleDoormenInstance || this instanceof L2FortressDoormenInstance)) {
            player.sendActionFailed();
            return;
        }

        // С NPC нельзя разговаривать мертвым и сидя
        if (!ConfigValue.AllowTalkWhileSitting && player.isSitting() || player.isAlikeDead())
            return;

        if (System.currentTimeMillis() - _lastSocialAction > 10000 && !getTemplate().randomAnimationDisabled) {
            broadcastPacket2(new SocialAction(getObjectId(), 2));
            _lastSocialAction = System.currentTimeMillis();
        }

        player.sendActionFailed();
        player.stopMove(false, false);

        boolean hasPts = false;

        if (_isBusy)
            showBusyWindow(player);
        else if (getAI().is_pts)
            getAI().TALKED(player, 0, 0);
        else if (hasChatWindow) {
            player.startNoMove();
            boolean flag = false;
            Quest[] qlst = getTemplate().getEventQuests(QuestEventType.NPC_FIRST_TALK);
            if (qlst != null && qlst.length > 0)
                for (Quest element : qlst) {
                    QuestState qs = player.getQuestState(element.getName());
                    if ((qs == null || !qs.isCompleted()) && element.notifyFirstTalk(this, player))
                        flag = true;
                }
            if (!flag)
                showChatWindow(player, 0);
        }
        if (ConfigValue.DebugOnAction)
            _log.info("DebugOnAction: L2NPC:onAction->finish");
    }

    public void showQuestWindow(L2Player player, String questId) {
        if (!player.isQuestContinuationPossible(true, true))
            return;

        int count = 0;
        for (QuestState quest : player.getAllQuestsStates())
            if (quest != null && ((quest.getQuest().getQuestIntId() < 999 || quest.getQuest().getQuestIntId() > 10000) && quest.getQuest().getQuestIntId() != 255) && quest.isStarted() && quest.getCond() > 0)
                count++;

        if (count > 40) {
            showChatWindow(player, "data/html/quest-limit.htm");
            return;
        }

        try {
            // Get the state of the selected quest
            QuestState qs = player.getQuestState(questId);
            if (qs != null) {
                if (qs.isCompleted()) {
                    Functions.show(new CustomMessage("quests.QuestAlreadyCompleted", player), player);
                    return;
                }
                if (qs.getQuest().notifyTalk(this, qs))
                    return;
            } else {
                Quest q = QuestManager.getQuest(questId);
                if (q != null) {
                    // check for start point
                    Quest[] qlst = getTemplate().getEventQuests(QuestEventType.QUEST_START);
                    if (qlst != null && qlst.length > 0)
                        for (Quest element : qlst)
                            if (element == q) {
                                qs = q.newQuestState(player, Quest.CREATED);
                                if (qs.getQuest().notifyTalk(this, qs))
                                    return;
                                break;
                            }
                }
            }

            showChatWindow(player, "data/html/no-quest.htm");
        } catch (Exception e) {
            _log.warning("problem with npc text " + e);
            e.printStackTrace();
        }

        player.sendActionFailed();
    }

    public static boolean canBypassCheck(L2Player player, L2NpcInstance npc) {
        if (npc == null || player.getDuel() != null || !ConfigValue.AllowTalkWhileSitting && player.isSitting() || !npc.isInRange(player, npc.INTERACTION_DISTANCE + npc.BYPASS_DISTANCE_ADD) || Math.abs(player.getZ() - npc.getZ()) > 100 || player.isStunned() || player.isSleeping() || player.isParalyzed() || player.isActionBlock() || player.isAttackingNow() || !npc.isVisible() || player.isAlikeDead() || player.isPlayer() && (player.isTeleporting() || player.isLogoutStarted())) {
            player.sendActionFailed();
            return false;
        }
        return true;
    }

    public void MENU_SELECTED(L2Player talker, int ask, int reply) {
        if (!canBypassCheck(talker, this))
            return;
        int i0 = 0;
        if (ask == 708 && getNpcId() == 30332) {
            if (reply == 1) {
                if (HaveMemo(talker, 708) == 1 && (GetMemoState(talker, 708) / 10) == 1) {
                    i0 = GetMemoState(talker, 708);
                    SetMemoState(talker, 708, (i0 + 10));
                    ShowPage(talker, "captain_bathia_q0708_02.htm");
                    SetFlagJournal(talker, 708, 6);
                    ShowQuestMark(talker, 708);
                    SoundEffect(talker, "ItemSound.quest_middle");
                }
            }
            if (reply == 2) {
                if (HaveMemo(talker, 708) == 1 && (GetMemoState(talker, 708) / 10) == 3) {
                    if (GetMemoState(talker, 708) % 10 == 9) {
                        DeleteItem1(talker, 13848, OwnItemCount(talker, 13848));
                        i0 = GetMemoState(talker, 708);
                        SetMemoState(talker, 708, (i0 + 10));
                        ShowPage(talker, "captain_bathia_q0708_05.htm");
                        SetFlagJournal(talker, 708, 9);
                        ShowQuestMark(talker, 708);
                        SoundEffect(talker, "ItemSound.quest_middle");
                    } else {
                        DeleteItem1(talker, 13848, OwnItemCount(talker, 13848));
                        i0 = GetMemoState(talker, 708);
                        SetMemoState(talker, 708, (i0 + 10));
                        ShowPage(talker, "captain_bathia_q0708_05.htm");
                        SetFlagJournal(talker, 708, 8);
                        ShowQuestMark(talker, 708);
                        SoundEffect(talker, "ItemSound.quest_middle");
                    }
                    Say(70854);
                }
            }
        } else if (ask == 709 && getNpcId() == 30735) {
            if (reply == 1) {
                if (HaveMemo(talker, 709) == 1 && (GetMemoState(talker, 709) / 10) == 1 && IsMyLord(talker) == 1) {
                    i0 = GetMemoState(talker, 709);
                    SetMemoState(talker, 709, (i0 + 10));
                    ShowPage(talker, "sophia_q0709_02.htm");
                    SetFlagJournal(talker, 709, 6);
                    ShowQuestMark(talker, 709);
                    SoundEffect(talker, "ItemSound.quest_middle");
                }
            }
            if (reply == 2) {
                if (HaveMemo(talker, 709) == 1 && (GetMemoState(talker, 709) / 10) == 3 && IsMyLord(talker) == 1) {
                    if ((GetMemoState(talker, 709) % 10) != 9) {
                        DeleteItem1(talker, 13850, OwnItemCount(talker, 13850));
                        i0 = GetMemoState(talker, 709);
                        SetMemoState(talker, 709, (i0 + 10));
                        ShowPage(talker, "sophia_q0709_05.htm");
                        SetFlagJournal(talker, 709, 8);
                        ShowQuestMark(talker, 709);
                        SoundEffect(talker, "ItemSound.quest_middle");
                    } else {
                        DeleteItem1(talker, 13850, OwnItemCount(talker, 13850));
                        i0 = GetMemoState(talker, 709);
                        SetMemoState(talker, 709, (i0 + 10));
                        ShowPage(talker, "sophia_q0709_06.htm");
                        SetFlagJournal(talker, 709, 9);
                        ShowQuestMark(talker, 709);
                        SoundEffect(talker, "ItemSound.quest_middle");
                    }
                }
            }
        } else if (ask == 711 && getNpcId() == 30969) {
            if (reply == 1) {
                ShowPage(talker, "iason_haine_q0711_04.htm");
            }
            if (reply == 2) {
                L2Player c0 = Pledge_GetLeader(talker);
                if (IsNullCreature(c0) == 0) {
                    if (HaveMemo(c0, 711) == 1 && GetMemoState(c0, 711) == 4) {
                        SetMemoState(c0, 711, 5);
                        ShowPage(talker, "iason_haine_q0711_05.htm");
                        SetFlagJournal(c0, 711, 4);
                        ShowQuestMark(c0, 711);
                        SoundEffect(c0, "ItemSound.quest_middle");
                    }
                } else {
                    ShowPage(talker, "iason_haine_q0711_06.htm");
                }
            }
        } else if (ask == 351 && getNpcId() == 30916) {
            if (reply == 1) {
                StringBuilder fhtml0 = new StringBuilder();
                FHTML_SetFileName(fhtml0, "captain_gosta_q0351_03.htm");
                FHTML_SetInt(fhtml0, "quest_id", 351);
                ShowQuestFHTML(talker, fhtml0, 351);
            }
        }
        if (ask == 351 && getNpcId() == 30969) {
            if (reply == 1) {
                if (OwnItemCount(talker, 4297) == 0) {
                    ShowPage(talker, "iason_haine_q0351_02.htm");
                } else if ((GetCurrentTick() - talker.quest_last_reward_time) > 1) {
                    talker.quest_last_reward_time = GetCurrentTick();
                    if (OwnItemCount(talker, 4297) >= 10) {
                        GiveItem1(talker, 57, (3880 + (20 * OwnItemCount(talker, 4297))));
                    } else {
                        GiveItem1(talker, 57, (20 * OwnItemCount(talker, 4297)));
                    }
                    DeleteItem1(talker, 4297, OwnItemCount(talker, 4297));
                    AddLog(3, talker, 351);
                    ShowPage(talker, "iason_haine_q0351_03.htm");
                    L2Player c0 = Pledge_GetLeader(talker);
                    if (IsNullCreature(c0) == 0) {
                        if (HaveMemo(c0, 711) == 1 && (((GetMemoState(c0, 711) % 100) >= 5) && (GetMemoState(c0, 711) % 100) < 15)) {
                            i0 = GetMemoState(c0, 711);
                            SetMemoState(c0, 711, (i0 + 1));
                        }
                    }
                }
            }
            if (reply == 2) {
                if (OwnItemCount(talker, 4298) == 0) {
                    ShowPage(talker, "iason_haine_q0351_04.htm");
                } else if ((GetCurrentTick() - talker.quest_last_reward_time) > 1) {
                    talker.quest_last_reward_time = GetCurrentTick();
                    GiveItem1(talker, 4407, OwnItemCount(talker, 4298));
                    GiveItem1(talker, 57, 3880);
                    DeleteItem1(talker, 4298, OwnItemCount(talker, 4298));
                    AddLog(3, talker, 351);
                    ShowPage(talker, "iason_haine_q0351_05.htm");
                    SetFlagJournal(talker, 351, 2);
                    ShowQuestMark(talker, 351);
                    L2Player c0 = Pledge_GetLeader(talker);
                    if (IsNullCreature(c0) == 0) {
                        if (HaveMemo(c0, 711) == 1 && (((GetMemoState(c0, 711) % 100) >= 5) && (GetMemoState(c0, 711) % 100) < 15)) {
                            i0 = GetMemoState(c0, 711);
                            SetMemoState(c0, 711, (i0 + 1));
                        }
                    }
                }
            }
            if (reply == 3) {
                ShowPage(talker, "iason_haine_q0351_06.htm");
            }
            if (reply == 4) {
                if (OwnItemCount(talker, 4298) == 0 && OwnItemCount(talker, 4297) == 0) {
                    ShowPage(talker, "iason_haine_q0351_07.htm");
                } else {
                    ShowPage(talker, "iason_haine_q0351_08.htm");
                }
            }
            if (reply == 5) {
                ShowPage(talker, "iason_haine_q0351_09.htm");
                if (OwnItemCount(talker, 4296) > 0) {
                    DeleteItem1(talker, 4296, 1);
                }
                RemoveMemo(talker, 351);
                AddLog(2, talker, 351);
                SoundEffect(talker, "ItemSound.quest_finish");
            }
        } else if (ask == 716 && getNpcId() == 31348) {
            if (reply == 1) {
                if (HaveMemo(talker, 716) == 1 && GetMemoState(talker, 716) == 2) {
                    ShowPage(talker, "falsepriest_agripel_q0716_02.htm");
                }
            }
            if (reply == 2) {
                if (HaveMemo(talker, 716) == 1 && GetMemoState(talker, 716) == 2) {
                    ShowPage(talker, "falsepriest_agripel_q0716_03.htm");
                    SetMemoState(talker, 716, 3);
                    SetFlagJournal(talker, 716, 3);
                    ShowQuestMark(talker, 716);
                    SoundEffect(talker, "ItemSound.quest_middle");
                }
            }
            if (reply == 3) {
                if (HaveMemo(talker, 716) == 1 && (GetMemoState(talker, 716) / 10) == 1) {
                    if (GetMemoStateEx(talker, 716, 1) >= 100) {
                        ShowPage(talker, "falsepriest_agripel_q0716_07.htm");
                    } else {
                        ShowPage(talker, "falsepriest_agripel_q0716_06.htm");
                    }
                }
            }
            if (reply == 4) {
                if (HaveMemo(talker, 716) == 1 && (GetMemoState(talker, 716) / 10) == 1 && GetMemoStateEx(talker, 716, 1) < 100) {
                    ShowPage(talker, "falsepriest_agripel_q0716_08.htm");
                }
            }
            if (reply == 5) {
                if (HaveMemo(talker, 716) == 1 && (GetMemoState(talker, 716) / 10) == 1 && GetMemoStateEx(talker, 716, 1) >= 100) {
                    ShowPage(talker, "falsepriest_agripel_q0716_09.htm");
                }
            }
            if (reply == 6) {
                if (HaveMemo(talker, 716) == 1 && (GetMemoState(talker, 716) / 10) == 1 && GetMemoStateEx(talker, 716, 1) >= 100) {
                    i0 = GetMemoState(talker, 716);
                    SetMemoState(talker, 716, (i0 + 10));
                    ShowPage(talker, "falsepriest_agripel_q0716_10.htm");
                    SetFlagJournal(talker, 716, 8);
                    ShowQuestMark(talker, 716);
                    SoundEffect(talker, "ItemSound.quest_middle");
                }
            }
        }
    }

    public void QUEST_ACCEPTED(int quest_id, L2Player talker) {
        if (!canBypassCheck(talker, this))
            return;
        if (quest_id == 351) {
            SetCurrentQuestID(351);
            if (GetInventoryInfo(talker, 0) >= (GetInventoryInfo(talker, 1) * 0.800000) || GetInventoryInfo(talker, 2) >= (GetInventoryInfo(talker, 3) * 0.800000)) {
                ShowSystemMessage(talker, 1118);
                return;
            }
            if ((GetCurrentTick() - talker.quest_last_reward_time) > 1) {
                talker.quest_last_reward_time = GetCurrentTick();
                GiveItem1(talker, 4296, 1);
                SetMemo(talker, 351);
                SetMemoState(talker, 351, 1);
                AddLog(1, talker, 351);
                SetFlagJournal(talker, 351, 1);
                ShowQuestPage(talker, "captain_gosta_q0351_04.htm", 351);
                SoundEffect(talker, "ItemSound.quest_accept");
            }
            return;
        }
    }

    public void onBypassFeedback(L2Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        try {
            if (command.equalsIgnoreCase("TerritoryStatus")) {
                NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                html.setFile("data/html/merchant/territorystatus.htm");
                html.replace("%npcname%", getName());

                Castle castle = getCastle(player);
                if (castle != null && castle.getId() > 0) {
                    html.replace("%castlename%", castle.getName());
                    html.replace("%taxpercent%", String.valueOf(castle.getTaxPercent()));

                    if (castle.getOwnerId() > 0) {
                        L2Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
                        if (clan != null) {
                            html.replace("%clanname%", clan.getName());
                            html.replace("%clanleadername%", clan.getLeaderName());
                        } else {
                            html.replace("%clanname%", "unexistant clan");
                            html.replace("%clanleadername%", "None");
                        }
                    } else {
                        html.replace("%clanname%", "NPC");
                        html.replace("%clanleadername%", "None");
                    }
                } else {
                    html.replace("%castlename%", "Open");
                    html.replace("%taxpercent%", "0");

                    html.replace("%clanname%", "No");
                    html.replace("%clanleadername%", getName());
                }

                player.sendPacket(html);
            } else if (command.startsWith("Quest")) {
                String quest = command.substring(5).trim();
                if (quest.length() == 0)
                    showQuestWindow(player);
                else
                    showQuestWindow(player, quest);
            } else if (command.startsWith("Chat"))
                try {
                    int val = Integer.parseInt(command.substring(5));
                    showChatWindow(player, val);
                } catch (NumberFormatException nfe) {
                    String filename = command.substring(5).trim();
                    if (filename.length() == 0)
                        showChatWindow(player, "data/html/npcdefault.htm");
                    else
                        showChatWindow(player, filename);
                }
            else if (command.startsWith("Loto")) {
                int val = Integer.parseInt(command.substring(5));
                showLotoWindow(player, val);
            } else if (command.startsWith("AttributeCancel"))
                player.sendPacket(new ExShowBaseAttributeCancelWindow(player));
            else if (command.startsWith("CPRecovery"))
                makeCPRecovery(player);
            else if (command.startsWith("MPRecovery"))
                player.setCurrentCp(player.getMaxCp());
            else if (command.startsWith("NpcLocationInfo")) {
                int val = Integer.parseInt(command.substring(16));
                L2NpcInstance npc = L2ObjectsStorage.getByNpcId(val);
                if (npc != null) {
                    // Убираем флажок на карте и стрелку на компасе
                    player.sendPacket(new RadarControl(2, 2, npc.getLoc()));
                    // Ставим флажок на карте и стрелку на компасе
                    player.sendPacket(new RadarControl(0, 1, npc.getLoc()));
                }
            } else if (command.startsWith("SupportMagic"))
                makeSupportMagic(player);
            else if (command.startsWith("ProtectionBlessing")) {
                // Не выдаём блессиг протекшена ПКшникам.
                if (player.getKarma() > 0)
                    return;
                if (player.getLevel() > 39 || player.getClassId().getLevel() >= 3) {
                    String content = "<html><body>Blessing of protection not available for characters whose level more than 39 or completed second class transfer.</body></html>";
                    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                    html.setHtml(content);
                    player.sendPacket(html);
                    return;
                }
                doCast(SkillTable.getInstance().getInfo(5182, 1), player, true);
            } else if (command.startsWith("Multisell") || command.startsWith("multisell")) {
                String listId = command.substring(9).trim();
                Castle castle = getCastle(player);
                L2Multisell.getInstance().SeparateAndSend(Integer.parseInt(listId), player, castle != null ? castle.getTaxRate() : 0);
            } else if (command.startsWith("EnterRift")) {
                StringTokenizer st = new StringTokenizer(command);
                st.nextToken(); //no need for "enterRift"

                Integer b1 = Integer.parseInt(st.nextToken()); //type

                DimensionalRiftManager.getInstance().start(player, b1, this);
            } else if (command.startsWith("ChangeRiftRoom")) {
                if (player.isInParty() && player.getParty().isInReflection() && player.getParty().getReflection() instanceof DimensionalRift)
                    ((DimensionalRift) player.getParty().getReflection()).manualTeleport(player, this);
                else
                    DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);
            } else if (command.startsWith("ExitRift")) {
                if (player.isInParty() && player.getParty().isInReflection() && player.getParty().getReflection() instanceof DimensionalRift)
                    ((DimensionalRift) player.getParty().getReflection()).manualExitRift(player, this);
                else
                    DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);
            } else if (command.equalsIgnoreCase("SkillList"))
                showSkillList(player);
            else if (command.equalsIgnoreCase("ClanSkillList"))
                showClanSkillList(player);
            else if (command.equalsIgnoreCase("FishingSkillList"))
                showFishingSkillList(player);
            else if (command.equalsIgnoreCase("TransformationSkillList"))
                showTransformationSkillList(player);
            else if (command.equalsIgnoreCase("CertificationSkillList"))
                showCertificationSkillList(player);
            else if (command.equalsIgnoreCase("EnchantSkillList"))
                showEnchantSkillList(player, false);
            else if (command.startsWith("SafeEnchantSkillList"))
                showEnchantSkillList(player, true);
            else if (command.equalsIgnoreCase("CollectionSkillList"))
                showCollectionSkillList(player);
            else if (command.startsWith("ChangeEnchantSkillList"))
                showEnchantChangeSkillList(player);
            else if (command.startsWith("UntrainEnchantSkillList"))
                showEnchantUntrainSkillList(player);
            else if (command.startsWith("showOtherSkillList"))
                showOtherSkillList(player, Integer.parseInt(command.split(":")[1]));
            else if (command.startsWith("Augment")) {
                int cmdChoice = Integer.parseInt(command.substring(8, 9).trim());
                if (cmdChoice == 1)
                    player.sendPacket(Msg.SELECT_THE_ITEM_TO_BE_AUGMENTED, Msg.ExShowVariationMakeWindow);
                else if (cmdChoice == 2)
                    player.sendPacket(Msg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION, Msg.ExShowVariationCancelWindow);
            } else if (command.startsWith("Link"))
                showChatWindow(player, "data/html/" + command.substring(5));
            else if (command.startsWith("Teleport")) {
                if (player.getTransformation() == 111 || player.getTransformation() == 112 || player.getTransformation() == 124)
                    return;
                int cmdChoice = Integer.parseInt(command.substring(9, 10).trim());
                TeleportLocation[] list = TeleportTable.getInstance().getTeleportLocationList(getNpcId(), cmdChoice);
                if (list != null)
                    showTeleportList(player, list);
                else
                    player.sendMessage("Ссылка неисправна, сообщите администратору.");
            } else if (command.startsWith("open_gate")) {
                int val = Integer.parseInt(command.substring(10));
                DoorTable.getInstance().getDoor(val).openMe();
                player.sendActionFailed();
            } else if (command.equalsIgnoreCase("TransferSkillList"))
                showTransferSkillList(player);
            else if (command.equalsIgnoreCase("CertificationCancel"))
                CertificationFunctions.cancelCertification(this, player);
            else if (command.startsWith("RemoveTransferSkill")) {
                StringTokenizer st = new StringTokenizer(command);
                st.nextToken();

                ClassId classId = player.getClassId();
                if (classId != null) {
                    int item_id = 0;
                    switch (classId) {
                        case cardinal:
                            item_id = 15307;
                            break;
                        case evaSaint:
                            item_id = 15308;
                            break;
                        case shillienSaint:
                            item_id = 15309;
                            break;
                    }

                    String var = player.getVar("TransferSkills" + item_id);
                    if (var == null || var.isEmpty())
                        return;

                    String[] skills = var.split(";");

                    if (player.getAdena() < 10000000) {
                        player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                        return;
                    }

                    player.unsetVar("TransferSkills" + item_id); // TODO мб вариант выше правильнее, и нужно удалять по одному?

                    for (String skill : skills)
                        PlayerData.getInstance().removeSkill(player, Integer.parseInt(skill), true, false);
                    player.updateEffectIcons();

                    player.reduceAdena(10000000, true);
                    Functions.addItem(player, item_id, skills.length);
                }
            } else if (command.equalsIgnoreCase("SquadSkillList"))
                showSquadSkillList(player);
            else if (command.startsWith("event")) {
                int val = 0;
                try {
                    val = Integer.parseInt(command.substring(6));
                } catch (IndexOutOfBoundsException ignored) {
                } catch (NumberFormatException ignored) {
                }
                if (val == 0)
                    return;
                if (getNpcId() == 35596)
                    RainbowSpringSiege.getInstance().exchangeItem(player, val);
            } else if (command.startsWith("ExitFromQuestInstance")) {
                Reflection r = player.getReflection();
                r.startCollapseTimer(60000);
                player.teleToLocation(r.getReturnLoc(), 0);
                if (command.length() > 22)
                    try {
                        int val = Integer.parseInt(command.substring(22));
                        showChatWindow(player, val);
                    } catch (NumberFormatException nfe) {
                        String filename = command.substring(22).trim();
                        if (filename.length() > 0)
                            showChatWindow(player, filename);
                    }
            } else if (command.startsWith("goto")) {
                InstantTeleport(player, 182960 + Rnd.get(50), -11904 + Rnd.get(50), -4897);
            }
        } catch (StringIndexOutOfBoundsException sioobe) {
            _log.info("Incorrect htm bypass! npcId=" + getTemplate().npcId + " command=[" + command + "]");
        } catch (NumberFormatException nfe) {
            _log.info("Invalid bypass to Server command parameter! npcId=" + getTemplate().npcId + " command=[" + command + "]");
        }
    }

    public void showTeleportList(L2Player player, TeleportLocation[] list) {
        StringBuffer sb = new StringBuffer();

        sb.append("!Gatekeeper ").append(_name).append(":<br>\n");

        if (list != null) {
            for (TeleportLocation tl : list)
                if (tl._item.getItemId() == 57) {
                    float pricemod = player.getLevel() <= ConfigValue.GkFree ? 0f : ConfigValue.GkCostMultiplier;
                    if (tl._price > 0 && pricemod > 0) {
                        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                        if (day != 1 && day != 7 && (hour <= 8 || hour >= 24))
                            pricemod /= 2;
                    }
                    sb.append("[scripts_Util:Gatekeeper ").append(tl._target).append(" ").append((int) (tl._price * pricemod)).append(" @811;").append(tl.getName(player)).append("|").append(tl.getName(player));
                    if (tl._price > 0)
                        sb.append(" - ").append((int) (tl._price * pricemod)).append(" Adena");
                    sb.append("]<br1>\n");
                } else
                    sb.append("[scripts_Util:QuestGatekeeper ").append(tl._target).append(" ").append(tl._price).append(" ").append(tl._item.getItemId()).append(" @811;").append(tl.getName(player)).append("|").append(tl.getName(player)).append(" - ").append(tl._price).append(" ").append(tl._item.getName()).append("]<br1>\n");
        } else
            sb.append("No teleports available.");

        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setHtml(Strings.bbParse(sb.toString()));
        player.sendPacket(html);
    }

    public void showQuestWindow(L2Player player) {
        // collect awaiting quests and start points
        List<Integer> options = new ArrayList<Integer>();

        List<QuestState> awaits = player.getQuestsForEvent(this, QuestEventType.QUEST_TALK);
        Quest[] starts = getTemplate().getEventQuests(QuestEventType.QUEST_START);

        if (awaits != null)
            for (QuestState x : awaits)
                if (!options.contains(x.getQuest().getQuestIntId()))
                    if (x.getQuest().getQuestIntId() > 0 && x.getQuest().getQuestIntId() != 999) {
                        //System.out.println("showQuestWindow awaits: "+x.getQuest().getName());
                        options.add(x.getQuest().getQuestIntId());
                    }

        if (starts != null)
            for (Quest x : starts)
                if (!options.contains(x.getQuestIntId()))
                    if (x.getQuestIntId() > 0 && x.getQuestIntId() != 999) {
                        //System.out.println("showQuestWindow starts: "+x.getName());
                        options.add(x.getQuestIntId());
                    }

        // Display a QuestChooseWindow (if several quests are available) or QuestWindow
		/*if(options.size() > 1 && !options.contains(999))
			showQuestChooseWindow(player, options.toArray(new Integer[options.size()]));
		else if(options.size() == 1)
			showQuestWindow(player, QuestManager.getQuest(options.get(0)).getName());
		else if(options.contains(999) && player.getQuestState(999).isStarted())
			showQuestWindow(player, QuestManager.getQuest(options.get(1)).getName());
		else if(options.size() > 1)
			showQuestChooseWindow(player, options.toArray(new Integer[options.size()]));
		else
			showQuestWindow(player, "");*/
        if (options.size() > 1)
            showQuestChooseWindow(player, options.toArray(new Integer[options.size()]));
        else if (options.size() == 1)
            showQuestWindow(player, QuestManager.getQuest(options.get(0)).getName());
        else
            showQuestWindow(player, "");
    }

    public void showQuestChooseWindow(L2Player player, Integer[] quests) {
        StringBuffer sb = new StringBuffer();

        sb.append("<html><body><title>Talk about:</title><br>");

        for (Integer q : quests)
            if (q != 999) {
                if (player.getQuestState(QuestManager.getQuest(q).getName()) == null)
                    sb.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Quest ").append(QuestManager.getQuest(q).getName()).append("\">[").append(QuestManager.getQuest(q).getDescr(player)).append("]</a><br>");
                else if (player.getQuestState(QuestManager.getQuest(q).getName()).isCompleted() || !player.getQuestState(QuestManager.getQuest(q).getName()).isNowAvailable()) {
                    if (player.getLang().equals("en"))
                        sb.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Quest ").append(QuestManager.getQuest(q).getName()).append("\">[").append(QuestManager.getQuest(q).getDescr(player)).append(" (Done)").append("]</a><br>");
                    else
                        sb.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Quest ").append(QuestManager.getQuest(q).getName()).append("\">[").append(QuestManager.getQuest(q).getDescr(player)).append(" (Завершено)").append("]</a><br>");
                } else {
                    if (player.getLang().equals("en"))
                        sb.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Quest ").append(QuestManager.getQuest(q).getName()).append("\">[").append(QuestManager.getQuest(q).getDescr(player)).append(" (In Progress)").append("]</a><br>");
                    else
                        sb.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Quest ").append(QuestManager.getQuest(q).getName()).append("\">[").append(QuestManager.getQuest(q).getDescr(player)).append(" (В процессе)").append("]</a><br>");
                }
            }
        sb.append("</body></html>");

        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setHtml(sb.toString());
        player.sendPacket(html);
    }

    public void ShowPage(L2Player player, String fileName) {
        NpcHtmlMessage npcReply = new NpcHtmlMessage(getObjectId());
        npcReply.setHtml(Files.read_pts(fileName, player));
        player.sendPacket(npcReply);
    }

    public void showChatWindow(L2Player player, int val) {
        if (getTemplate().chatWindowDisabled)
            return;

        int npcId = getTemplate().npcId;
        String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH;

        switch (npcId) {
            case 31111: // Gatekeeper Spirit (Disciples)
                int sealAvariceOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE);
                int playerCabal = SevenSigns.getInstance().getPlayerCabal(player);
                int compWinner = SevenSigns.getInstance().getCabalHighestScore();
                if (playerCabal == sealAvariceOwner && playerCabal == compWinner)
                    switch (sealAvariceOwner) {
                        case SevenSigns.CABAL_DAWN:
                            filename += "spirit_dawn.htm";
                            break;
                        case SevenSigns.CABAL_DUSK:
                            filename += "spirit_dusk.htm";
                            break;
                        case SevenSigns.CABAL_NULL:
                            filename += "spirit_null.htm";
                            break;
                    }
                else
                    filename += "spirit_null.htm";
                break;
            case 31112: // Gatekeeper Spirit (Disciples)
                filename += "spirit_exit.htm";
                break;
            case 31688:
                if (player.isNoble())
                    filename = Olympiad.OLYMPIAD_HTML_PATH + "noble_main.htm";
                else
                    filename = Olympiad.OLYMPIAD_HTML_PATH + "manager.htm";
                break;
            case 31690:
            case 31769:
            case 31770: // Monument of Heroes
            case 31771:
            case 31772:
                if (player.isHeroType() == 0 || Hero.getInstance().isInactiveHero(player.getObjectId()) || player.isHeroType() == 2 && ConfigValue.SellHeroItemForPremium)
                    filename = Olympiad.OLYMPIAD_HTML_PATH + "hero_main.htm";
                else
                    filename = getHtmlPath(npcId, val);
                break;
            default:
                if (npcId >= 31093 && npcId <= 31094 || npcId >= 31172 && npcId <= 31201 || npcId >= 31239 && npcId <= 31254)
                    return;
                // Get the text of the selected HTML file in function of the npcId and of the page number
                filename = getHtmlPath(npcId, val);
                break;
        }

        player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
    }

    public void showChatWindow(L2Player player, String filename) {
        player.sendPacket(new NpcHtmlMessage(player, this, filename, 0));
    }

    public String getHtmlPath(int npcId, int val) {
        String pom;
        if (val == 0)
            pom = String.valueOf(npcId);
        else
            pom = npcId + "-" + val;
        String temp = "data/html/default/" + pom + ".htm";
        File mainText = new File(temp);
        if (mainText.exists())
            return temp;

        temp = "data/html/trainer/" + pom + ".htm";
        mainText = new File(temp);
        if (mainText.exists())
            return temp;

        temp = "data/html/lottery/" + pom + ".htm";
        mainText = new File(temp);
        if (mainText.exists())
            return temp;

        temp = "data/html/instance/kamaloka/" + pom + ".htm";
        mainText = new File(temp);
        if (mainText.exists())
            return temp;

        // If the file is not found, the standard message "I have nothing to say to you" is returned
        return "data/html/npcdefault.htm";
    }

    /** For Lottery Manager **/
    public void showLotoWindow(L2Player player, int val) {
        int npcId = getTemplate().npcId;
        String filename;
        SystemMessage sm;
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);

        // if loto
        if (val == 0) {
            filename = getHtmlPath(npcId, 1);
            html.setFile(filename);
        } else if (val >= 1 && val <= 21) {
            if (!LotteryManager.getInstance().isStarted()) {
                /** LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD **/
                player.sendPacket(Msg.LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD);
                return;
            }
            if (!LotteryManager.getInstance().isSellableTickets()) {
                /** TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE **/
                player.sendPacket(Msg.TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE);
                return;
            }

            filename = getHtmlPath(npcId, 5);
            html.setFile(filename);

            int count = 0;
            int found = 0;

            // counting buttons and unsetting button if found
            for (int i = 0; i < 5; i++)
                if (player.getLoto(i) == val) {
                    // unsetting button
                    player.setLoto(i, 0);
                    found = 1;
                } else if (player.getLoto(i) > 0)
                    count++;

            // if not rearched limit 5 and not unseted value
            if (count < 5 && found == 0 && val <= 20)
                for (int i = 0; i < 5; i++)
                    if (player.getLoto(i) == 0) {
                        player.setLoto(i, val);
                        break;
                    }

            //setting pusshed buttons
            count = 0;
            for (int i = 0; i < 5; i++)
                if (player.getLoto(i) > 0) {
                    count++;
                    String button = String.valueOf(player.getLoto(i));
                    if (player.getLoto(i) < 10)
                        button = "0" + button;
                    String search = "fore=\"L2UI.lottoNum" + button + "\" back=\"L2UI.lottoNum" + button + "a_check\"";
                    String replace = "fore=\"L2UI.lottoNum" + button + "a_check\" back=\"L2UI.lottoNum" + button + "\"";
                    html.replace(search, replace);
                }
            if (count == 5) {
                String search = "0\">Return";
                String replace = "22\">The winner selected the numbers above.";
                html.replace(search, replace);
            }
            player.sendPacket(html);
        }

        if (val == 22) {
            if (!LotteryManager.getInstance().isStarted()) {
                /** LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD **/
                player.sendPacket(Msg.LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD);
                return;
            }
            if (!LotteryManager.getInstance().isSellableTickets()) {
                /** TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE **/
                player.sendPacket(Msg.TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE);
                return;
            }

            int price = ConfigValue.AltLotteryPrice;
            int lotonumber = LotteryManager.getInstance().getId();
            int enchant = 0;
            int type2 = 0;
            for (int i = 0; i < 5; i++) {
                if (player.getLoto(i) == 0)
                    return;
                if (player.getLoto(i) < 17)
                    enchant += Math.pow(2, player.getLoto(i) - 1);
                else
                    type2 += Math.pow(2, player.getLoto(i) - 17);
            }
            if (player.getAdena() < price) {
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
            player.reduceAdena(price, true);
            sm = new SystemMessage(SystemMessage.ACQUIRED__S1_S2);
            sm.addNumber(lotonumber);
            sm.addItemName(4442);
            player.sendPacket(sm);
            L2ItemInstance item = ItemTemplates.getInstance().createItem(4442);
            item.setCustomType1(lotonumber);
            item.setEnchantLevel(enchant);
            item.setCustomType2(type2);
            player.getInventory().addItem(item);
            Log.LogItem(player, Log.BuyItem, item);
            //html.setHtml("<html><body>Lottery Ticket Seller:<br>Thank you for playing the lottery<br>The winners will be announced at 7:00 pm <br><center><a action=\"bypass -h npc_%objectId%_Chat 0\">Back</a></center></body></html>");
            html.setFile("data/html/lottery/lottery-22.htm");

        } else if (val == 23) //23 - current lottery jackpot
        {
            filename = getHtmlPath(npcId, 3);
            html.setFile(filename);
        } else if (val == 24) {
            filename = getHtmlPath(npcId, 4);
            html.setFile(filename);

            int lotonumber = LotteryManager.getInstance().getId();
            String message = "";

            for (L2ItemInstance item : player.getInventory().getItems()) {
                if (item == null)
                    continue;
                if (item.getItemId() == 4442 && item.getCustomType1() < lotonumber) {
                    message = message + "<a action=\"bypass -h npc_%objectId%_Loto " + item.getObjectId() + "\">" + item.getCustomType1() + " Event Number ";
                    int[] numbers = LotteryManager.getInstance().decodeNumbers(item.getRealEnchantLevel(), item.getCustomType2());
                    for (int i = 0; i < 5; i++)
                        message += numbers[i] + " ";
                    int[] check = LotteryManager.getInstance().checkTicket(item);
                    if (check[0] > 0) {
                        switch (check[0]) {
                            case 1:
                                message += "- 1st Prize";
                                break;
                            case 2:
                                message += "- 2nd Prize";
                                break;
                            case 3:
                                message += "- 3th Prize";
                                break;
                            case 4:
                                message += "- 4th Prize";
                                break;
                        }
                        message += " " + check[1] + "a.";
                    }
                    message += "</a><br>";
                }
            }
            if (message == "")
                message += "There is no winning lottery ticket...<br>";
            html.replace("%result%", message);
        } else if (val == 25) {
            filename = getHtmlPath(npcId, 2);
            html.setFile(filename);
        } else if (val > 25) {
            int lotonumber = LotteryManager.getInstance().getId();
            L2ItemInstance item = player.getInventory().getItemByObjectId(val);
            if (item == null || item.getItemId() != 4442 || item.getCustomType1() >= lotonumber)
                return;
            int[] check = LotteryManager.getInstance().checkTicket(item);

            player.sendPacket(SystemMessage.removeItems(4442, 1));

            int adena = check[1];
            if (adena > 0)
                player.addAdena(adena);
            player.getInventory().destroyItem(item, 1, true);
            return;
        }

        html.replace("%objectId%", String.valueOf(getObjectId()));
        html.replace("%race%", "" + LotteryManager.getInstance().getId());
        html.replace("%adena%", "" + LotteryManager.getInstance().getPrize());
        html.replace("%ticket_price%", "" + ConfigValue.LotteryTicketPrice);
        html.replace("%prize5%", "" + ConfigValue.Lottery5NumberRate * 100);
        html.replace("%prize4%", "" + ConfigValue.Lottery4NumberRate * 100);
        html.replace("%prize3%", "" + ConfigValue.Lottery3NumberRate * 100);
        html.replace("%prize2%", "" + ConfigValue.Lottery2and1NumberPrize);
        html.replace("%enddate%", "" + DateFormat.getDateInstance().format(LotteryManager.getInstance().getEndDate()));

        player.sendPacket(html);
        player.sendActionFailed();
    }

    public void makeCPRecovery(L2Player player) {
        if (getNpcId() != 31225 && getNpcId() != 31226)
            return;
        int neededmoney = 100;
        long currentmoney = player.getAdena();
        if (neededmoney > currentmoney) {
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }
        player.reduceAdena(neededmoney, true);
        player.setCurrentCp(player.getMaxCp());
        player.sendPacket(new SystemMessage(SystemMessage.S1_CPS_WILL_BE_RESTORED).addString(player.getName()));
    }

    static int[][] _mageBuff = new int[][]{
            // minlevel maxlevel skill skilllevel
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4322, 1}, // windwalk
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4323, 1}, // shield
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 5637, 1}, // Magic Barrier 1
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4328, 1}, // blessthesoul
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4329, 1}, // acumen
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4330, 1}, // concentration
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4331, 1}, // empower
            {16, 34, 4338, 1}, // life cubic
    };

    static int[][] _warrBuff = new int[][]{
            // minlevel maxlevel skill
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4322, 1}, // windwalk
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4323, 1}, // shield
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 5637, 1}, // Magic Barrier 1
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4324, 1}, // btb
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4325, 1}, // vampirerage
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4326, 1}, // regeneration
            {ConfigValue.BuffMinLevel, 39, 4327, 1}, // haste 1
            {40, ConfigValue.BuffMaxLevel, 5632, 1}, // haste 2
            {16, 34, 4338, 1}, // life cubic
    };

    static int[][] _summonBuff = new int[][]{
            // minlevel maxlevel skill
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4322, 1}, // windwalk
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4323, 1}, // shield
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 5637, 1}, // Magic Barrier 1
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4324, 1}, // btb
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4325, 1}, // vampirerage
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4326, 1}, // regeneration
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4328, 1}, // blessthesoul
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4329, 1}, // acumen
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4330, 1}, // concentration
            {ConfigValue.BuffMinLevel, ConfigValue.BuffMaxLevel, 4331, 1}, // empower
            {ConfigValue.BuffMinLevel, 39, 4327, 1}, // haste 1
            {40, ConfigValue.BuffMaxLevel, 5632, 1}, // haste 2
    };

    public void makeSupportMagic(L2Player player) {
        // Prevent a cursed weapon weilder of being buffed
        if (player.isCursedWeaponEquipped())
            return;
        int lvl = player.getLevel();

        if (lvl < ConfigValue.BuffMinLevel) {
            player.sendPacket(new NpcHtmlMessage(player, this, "data/html/default/newbie_nosupport6.htm", 0).replace("%minlevel%", String.valueOf(ConfigValue.BuffMinLevel)));
            return;
        }
        if (lvl > ConfigValue.BuffMaxLevel) {
            player.sendPacket(new NpcHtmlMessage(player, this, "data/html/default/newbie_nosupport62.htm", 0).replace("%maxlevel%", String.valueOf(ConfigValue.BuffMaxLevel)));
            return;
        }

        GArray<L2Character> target = new GArray<L2Character>();
        target.add(player);

        if (!player.isMageClass() || player.getTemplate().race == Race.orc) {
            for (int[] buff : _warrBuff)
                if (lvl >= buff[0] && lvl <= buff[1]) {
                    broadcastSkill(new MagicSkillUse(this, player, buff[2], buff[3], 0, 0));
                    callSkill(SkillTable.getInstance().getInfo(buff[2], buff[3]), target, true);
                }
        } else
            for (int[] buff : _mageBuff)
                if (lvl >= buff[0] && lvl <= buff[1]) {
                    broadcastSkill(new MagicSkillUse(this, player, buff[2], buff[3], 0, 0));
                    callSkill(SkillTable.getInstance().getInfo(buff[2], buff[3]), target, true);
                }

        if (ConfigValue.BuffSummon && player.getPet() != null && !player.getPet().isDead()) {
            target.clear();
            target = new GArray<L2Character>();
            target.add(player.getPet());

            for (int[] buff : _summonBuff)
                if (lvl >= buff[0] && lvl <= buff[1]) {
                    broadcastSkill(new MagicSkillUse(this, player.getPet(), buff[2], buff[3], 0, 0));
                    callSkill(SkillTable.getInstance().getInfo(buff[2], buff[3]), target, true);
                }
        }
    }

    private boolean _isBusy;
    private String _busyMessage = "";

    public final boolean isBusy() {
        return _isBusy;
    }

    public void setBusy(boolean isBusy) {
        _isBusy = isBusy;
    }

    public final String getBusyMessage() {
        return _busyMessage;
    }

    public void setBusyMessage(String message) {
        _busyMessage = message;
    }

    public void showBusyWindow(L2Player player) {
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setFile("data/html/npcbusy.htm");
        html.replace("%npcname%", getName());
        html.replace("%playername%", player.getName());
        html.replace("%busymessage%", _busyMessage);
        player.sendPacket(html);
    }

    public void showSkillList(L2Player player) {
        if (player.getTransformation() != 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append(new CustomMessage("l2open.gameserver.model.instances.L2NpcInstance.CantTeachBecauseTransformation", player));
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);

            return;
        }

        ClassId classId = player.getClassId();

        if (classId == null)
            return;

        int npcId = getTemplate().npcId;

        if (_classesToTeach == null) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append("I cannot teach you. My class list is empty.<br> Ask admin to fix it. Need add my npcid and classes to skill_learn.sql.<br>NpcId:" + npcId + ", Your classId:" + player.getClassId().getId() + "<br>");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);

            return;
        }

        if (!(getTemplate().canTeach(classId) || getTemplate().canTeach(classId.getParent(player.getSex())))) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append(new CustomMessage("l2open.gameserver.model.instances.L2NpcInstance.WrongTeacherClass", player));
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);

            return;
        }

        AcquireSkillList asl = new AcquireSkillList(AcquireSkillList.USUAL);
        int counts = 0;

        GArray<L2SkillLearn> skills = player.getAvailableSkills(classId);
        for (L2SkillLearn s : skills) {
            if (s.getItemCount() == -1)
                continue;
            L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
            if (sk == null || !sk.getCanLearn(player.getClassId()) || !sk.canTeachBy(npcId))
                continue;
            int cost = SkillTreeTable.getInstance().getSkillCost(player, sk);
            counts++;
            asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), cost, 0);
        }

        if (counts == 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            int minlevel = SkillTreeTable.getInstance().getMinLevelForNewSkill(player, classId);

            if (minlevel > 0) {
                SystemMessage sm = new SystemMessage(SystemMessage.YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN__COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1);
                sm.addNumber(minlevel);
                player.sendPacket(sm);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("<html><head><body>");
                sb.append("You've learned all skills for your class.");
                sb.append("</body></html>");
                html.setHtml(sb.toString());
                player.sendPacket(html);
            }
        } else
            player.sendPacket(asl);

        player.sendActionFailed();
    }

    public void showOtherSkillList(L2Player player, int type) {
        if (player.getTransformation() != 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append(new CustomMessage("l2open.gameserver.model.instances.L2NpcInstance.CantTeachBecauseTransformation", player));
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);

            return;
        }

        AcquireSkillList asl = new AcquireSkillList(type);
        int counts = 0;

        L2SkillLearn[] skills = SkillTreeTable.getInstance().getAvailableOtherSkills(player, type);
        for (L2SkillLearn s : skills) {
            L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
            if (sk == null)
                continue;
            int cost = SkillTreeTable.getInstance().getSkillCost(player, sk);
            counts++;
            asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getSpCost(), s.getItemId());
        }

        if (counts == 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append("You've learned all skills.");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);
        } else
            player.sendPacket(asl);

        player.sendActionFailed();
    }

    public void showTransferSkillList(L2Player player) {
        if (player.getTransformation() != 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append(new CustomMessage("l2open.gameserver.model.instances.L2NpcInstance.CantTeachBecauseTransformation", player));
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);
            return;
        }

        ClassId classId = player.getClassId();
        if (classId == null)
            return;

        if (player.getLevel() < 76 || classId.getLevel() < 4) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append("You must have 3rd class change quest completed.");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);
            return;
        }

        GArray<L2SkillLearn> skills = new GArray<L2SkillLearn>();
        skills.addAll(SkillTreeTable.getInstance().getAvailableTransferSkills(player, classId));

        if (skills.isEmpty()) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append("There is no skills for your class.");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);
            return;
        }

        AcquireSkillList asl = new AcquireSkillList(AcquireSkillList.TRANSFER);
        for (L2SkillLearn s : skills) {
            if (s.getItemCount() == -1)
                continue;
            L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
            if (sk != null)
                asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), 0, 0);
        }

        player.sendPacket(asl);
    }

    public void showEnchantSkillList(L2Player player, boolean isSafeEnchant) {
        if (!enchantChecks(player))
            return;
        GArray<L2Skill> skills = SkillTreeTable.getInstance().getSkillsToEnchant(player);
        ExEnchantSkillList esl = new ExEnchantSkillList(isSafeEnchant ? EnchantSkillType.SAFE : EnchantSkillType.NORMAL);
        int counts = 0;
        for (L2Skill s : skills) {
            counts++;
            esl.addSkill(s.getId(), s.getDisplayLevel());
        }
        if (counts == 0)
            player.sendPacket(Msg.THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT);
        else
            player.sendPacket(esl);
    }

    public void showEnchantChangeSkillList(L2Player player) {
        if (!enchantChecks(player))
            return;
        ExEnchantSkillList esl = new ExEnchantSkillList(EnchantSkillType.CHANGE_ROUTE);
        int counts = 0;
        for (L2Skill s : player.getAllSkills()) {
            if (s.getDisplayLevel() < 100)
                continue;
            counts++;
            esl.addSkill(s.getId(), s.getDisplayLevel());
        }
        if (counts == 0)
            player.sendPacket(Msg.THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT);
        else
            player.sendPacket(esl);
    }

    public void showEnchantUntrainSkillList(L2Player player) {
        if (!enchantChecks(player))
            return;
        ExEnchantSkillList esl = new ExEnchantSkillList(EnchantSkillType.UNTRAIN);
        int counts = 0;
        for (L2Skill s : player.getAllSkills()) {
            if (s.getDisplayLevel() < 100)
                continue;
            counts++;
            esl.addSkill(s.getId(), s.getDisplayLevel());
        }
        if (counts == 0)
            player.sendPacket(Msg.THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT);
        else
            player.sendPacket(esl);
    }

    public void showSquadSkillList(L2Player player) {
        if (player.getClan() == null || !player.isClanLeader()) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append("Only the castle owning clan leader can add a squad skill!");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);
            return;
        }
        if (player.getTransformation() != 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append(new CustomMessage("l2open.game.model.instances.L2NpcInstance.CantTeachBecauseTransformation", player));
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);
            return;
        }
        GArray<L2SkillLearn> skills = new GArray<L2SkillLearn>();
        skills.addAll(SkillTreeTable.getInstance().getAvailableSquadSkills(player.getClan()));
        if (skills.isEmpty()) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append("This squad has no available skills to learn.");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);
            return;
        }
        AcquireSkillList asl = new AcquireSkillList(AcquireSkillList.CLAN_ADDITIONAL);
        for (L2SkillLearn skill : skills)
            if (SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel()) != null)
                asl.addSkill(skill.getId(), skill.getLevel(), skill.getLevel(), skill.getRepCost(), skill.getItemId());
        player.sendPacket(asl);
    }

    private boolean enchantChecks(L2Player player) {
        if (player.getTransformation() != 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append(new CustomMessage("l2open.gameserver.model.instances.L2NpcInstance.CantTeachBecauseTransformation", player));
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);
            return false;
        }

        int npcId = getTemplate().npcId;

        if (_classesToTeach == null) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append("I cannot teach you. My class list is empty.<br> Ask admin to fix it. Need add my npcid and classes to skill_learn.sql.<br>NpcId:" + npcId + ", Your classId:" + player.getClassId().getId() + "<br>");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);
            return false;
        }

        if (!(getTemplate().canTeach(player.getClassId()) || getTemplate().canTeach(player.getClassId().getParent(player.getSex())))) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append(new CustomMessage("l2open.gameserver.model.instances.L2NpcInstance.WrongTeacherClass", player));
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);
            return false;
        }

        if (player.getClassId().getLevel() < 4) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("data/html/skillenchant_notfourthclass.htm");
            player.sendPacket(html);
            return false;
        }

        if (player.getLevel() < 76) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("data/html/skillenchant_levelmismatch.htm");
            player.sendPacket(html);
            return false;
        }

        return true;
    }

    public void showCollectionSkillList(L2Player player) {
        if (player.getTransformation() != 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append(new CustomMessage("l2open.gameserver.model.instances.L2NpcInstance.CantTeachBecauseTransformation", player));
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);

            return;
        }

        AcquireSkillList asl = new AcquireSkillList(AcquireSkillList.COLLECTION);
        int counts = 0;

        L2SkillLearn[] skills = SkillTreeTable.getInstance().getAvailableCollectionSkills(player);
        for (L2SkillLearn s : skills) {
            L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
            if (sk == null)
                continue;
            int cost = SkillTreeTable.getInstance().getSkillCost(player, sk);
            counts++;
            asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), cost, 0);
        }

        if (counts == 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append("You've learned all skills.");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);
        } else
            player.sendPacket(asl);

        player.sendActionFailed();
    }

    public void showFishingSkillList(L2Player player) {
        if (player.getTransformation() != 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append(new CustomMessage("l2open.gameserver.model.instances.L2NpcInstance.CantTeachBecauseTransformation", player));
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);

            return;
        }

        AcquireSkillList asl = new AcquireSkillList(AcquireSkillList.FISHING);
        int counts = 0;

        L2SkillLearn[] skills = SkillTreeTable.getInstance().getAvailableFishingSkills(player);
        for (L2SkillLearn s : skills) {
            L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
            if (sk == null)
                continue;
            int cost = SkillTreeTable.getInstance().getSkillCost(player, sk);
            counts++;
            asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), cost, 0);
        }

        if (counts == 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append("You've learned all skills.");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);
        } else
            player.sendPacket(asl);

        player.sendActionFailed();
    }

    public void showCertificationSkillList(L2Player player) {
        if (player.getTransformation() != 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append(new CustomMessage("l2open.gameserver.model.instances.L2NpcInstance.CantTeachBecauseTransformation", player));
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);

            return;
        }

        if (!ConfigValue.AllowLearnTransSkillsWOQuest)
            if (!player.isQuestCompleted("_136_MoreThanMeetsTheEye")) {
                showChatWindow(player, "data/html/trainer/" + getNpcId() + "-noquest.htm");
                return;
            }

        if (player.isSubClassActive()) {
            player.sendPacket(new SystemMessage(SystemMessage.THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUB_CLASS_STATE_PLEASE_TRY_AGAIN_AFTER_CHANGING_TO_THE));
            return;
        }

        AcquireSkillList asl = new AcquireSkillList(AcquireSkillList.CERTIFICATION);
        int counts = 0;

        L2SkillLearn[] skills = SkillTreeTable.getAvailableCertificationSkills(player);
        for (L2SkillLearn s : skills) {
            L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
            if (sk == null)
                continue;
            counts++;
            asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), 0, 1);
        }

        if (counts == 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append("You've learned all skills.");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);
        } else
            player.sendPacket(asl);

        player.sendActionFailed();
    }

    public void showTransformationSkillList(L2Player player) {
        if (player.getTransformation() != 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append(new CustomMessage("l2open.gameserver.model.instances.L2NpcInstance.CantTeachBecauseTransformation", player));
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);

            return;
        }

        if (!ConfigValue.AllowLearnTransSkillsWOQuest)
            if (!player.isQuestCompleted("_136_MoreThanMeetsTheEye")) {
                showChatWindow(player, "data/html/trainer/" + getNpcId() + "-noquest.htm");
                return;
            }

        AcquireSkillList asl = new AcquireSkillList(AcquireSkillList.TRANSFORMATION);
        int counts = 0;

        L2SkillLearn[] skills = SkillTreeTable.getInstance().getAvailableTransformationSkills(player);
        for (L2SkillLearn s : skills) {
            L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
            if (sk == null)
                continue;
            int cost = SkillTreeTable.getInstance().getSkillCost(player, sk);
            counts++;
            asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), cost, 1);
        }

        if (counts == 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append("You've learned all skills.");
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);
        } else
            player.sendPacket(asl);

        player.sendActionFailed();
    }

    public void showClanSkillList(L2Player player) {
        if (player.getTransformation() != 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><body>");
            sb.append(new CustomMessage("l2open.gameserver.model.instances.L2NpcInstance.CantTeachBecauseTransformation", player));
            sb.append("</body></html>");
            html.setHtml(sb.toString());
            player.sendPacket(html);

            return;
        }

        if (player.getClan() == null || !player.isClanLeader()) {
            player.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            player.sendActionFailed();
            return;
        }

        AcquireSkillList asl = new AcquireSkillList(AcquireSkillList.CLAN);
        int counts = 0;

        L2SkillLearn[] skills = SkillTreeTable.getInstance().getAvailableClanSkills(player.getClan());
        for (L2SkillLearn s : skills) {
            L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
            if (sk == null)
                continue;
            int cost = s.getRepCost();
            counts++;
            asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), cost, 0);
        }

        if (counts == 0) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setHtml("<html><head><body>You've learned all skills.</body></html>");
            player.sendPacket(html);
        } else
            player.sendPacket(asl);

        player.sendActionFailed();
    }

    /**
     * Возвращает режим NPC: свежезаспавненный или нормальное состояние
     * @return true, если NPC свежезаспавненный
     */
    public int isShowSpawnAnimation() {
        return _showSpawnAnimation;
    }

    public void setShowSpawnAnimation(int value) {
        _showSpawnAnimation = value;
    }

    @Override
    public boolean getChargedSoulShot() {
        switch (getTemplate().shots) {
            case SOUL:
            case SOUL_SPIRIT:
                //case SOUL_BSPIRIT:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getChargedSpiritShot() {
        switch (getTemplate().shots) {
            case SPIRIT:
            case SOUL_SPIRIT:
                return 1;
            case BSPIRIT:
            case SOUL_BSPIRIT:
                return 2;
            default:
                return 0;
        }
    }

    @Override
    public boolean unChargeShots(boolean spirit) {
        //broadcastSkill(new MagicSkillUse(this, spirit ? 2061 : 2039, 1, 0, 0)); пакет больше не шлется, из клиента анимация убрана
        return true;
    }

    @Override
    public float getColRadius() {
        return (float) getCollisionRadius();
    }

    @Override
    public float getColHeight() {
        return (float) getCollisionHeight();
    }

    /**
     * @return the character that summoned this NPC.
     */
    public L2Character getSummoner() {
        return _summoner;
    }

    /**
     * @param summoner the summoner of this NPC.
     */
    public void setSummoner(L2Character summoner) {
        _summoner = summoner;
    }

    public L2Character getTopDamager(Collection<AggroInfo> aggroList) {
        AggroInfo top = null;
        for (AggroInfo aggro : aggroList)
            if (aggro.attacker != null && (top == null || aggro.damage > top.damage))
                top = aggro;
        return top != null ? top.attacker : null;
    }

    public int calculateLevelDiffForDrop(int charLevel, boolean is_spoil) {
        if (!ConfigValue.UseDeepBlueDropRules)
            return 0;

        int mobLevel = getLevel();
        // According to official data (Prima), deep blue mobs are 9 or more levels below players
        int deepblue_maxdiff = is_spoil ? ConfigValue.DeepBlueSpoilMaxDiff : (isRaid() || isRefRaid() ? ConfigValue.DeepBlueDropRaidMaxDiff : ConfigValue.DeepBlueDropMaxDiff);

        return Math.max(charLevel - mobLevel - deepblue_maxdiff, 0);
    }

    public boolean isFearImmune() {
        return (isNpc() && !isMonster()) && !isSiegeGuard();
    }

    public boolean isSevenSignsMonster() {
        return getName().startsWith("Lilim ") || getName().startsWith("Nephilim ") || getName().startsWith("Lith ") || getName().startsWith("Gigant ");
    }

    public void onClanAttacked(L2NpcInstance attacked_member, L2Character attacker, int damage) {
        onClanAttacked(attacked_member, attacker, damage, false);
    }

    public void onClanAttacked(L2NpcInstance attacked_member, L2Character attacker, int damage, boolean isKill) {
        String my_name = getName();
        String attacked_name = attacked_member.getName();

        if (my_name.startsWith("Lilim ") && attacked_name.startsWith("Nephilim "))
            return;
        if (my_name.startsWith("Nephilim ") && attacked_name.startsWith("Lilim "))
            return;
        if (my_name.startsWith("Lith ") && attacked_name.startsWith("Gigant "))
            return;
        if (my_name.startsWith("Gigant ") && attacked_name.startsWith("Lith "))
            return;

        if (!isKill)
            getAI().notifyEvent(CtrlEvent.EVT_CLAN_ATTACKED, new Object[]{attacked_member, attacker, damage});
        else
            getAI().notifyEvent(CtrlEvent.EVT_CLAN_DEAD, new Object[]{attacked_member, attacker});
    }

    public String getTypeName() {
        return getClass().getSimpleName().replaceFirst("L2", "").replaceFirst("Instance", "");
    }

    @Override
    public String toString() {
        return "NPC " + getName() + " [" + getNpcId() + "]";
    }

    public void refreshID() {
        L2ObjectsStorage.remove(this);
        _objectId = IdFactory.getInstance().getNextId();
        L2ObjectsStorage.put(this);
        getEffectList().setOwner(this);
        getAI().refreshActor(this);
        _moveTaskRunnable.updateStoreId(this);
        //if(_move_data != null)
        //	_move_data.update(this);
    }

    private boolean _isUnderground = false;

    public void setUnderground(boolean b) {
        _isUnderground = b;
    }

    public boolean isUnderground() {
        return _isUnderground;
    }

    public void setWeaponEnchant(int val) {
        _weaponEnchant = val;
    }

    public int getWeaponEnchant() {
        return _weaponEnchant;
    }

    public void setNpcLeader(L2Character leader) {
        if (leader != null)
            boss = leader;
    }

    public void TALKED(L2Player talker) {

    }

    public void TALK_SELECTED(L2NpcInstance myself, L2Player talker, int _code, int _from_choice) {
        ShowPage(talker, "noquest.htm");
    }

    public void TELEPORT_REQUESTED(L2Player talker) {
    }

    public void MANOR_MENU_SELECTED(L2Player talker, int ask, int state, int time) {

    }

    public int getAgroRange() {
        return getTemplate().agro_range;
    }

    public int getEventFlag() {
        return getTemplate().event_flag;
    }

    @Override
    public int isUnDying() {
        return getTemplate().undying;
    }
    // ---------------------------------------- Все эти методы с офф ядра, делаю их, что бы удобней было писать разные плюшки с офф скриптов. ---------------------------------------
    /**
     * HaveMemo - Проверка наличия квеста, 0 - квест не взят, 1 - квест взят.
     * GetMemoCount - количество взятых квестов...
     * SetFlagJournal(talker,708,5) - это типа нашего COND...
     * pledge_id - getClanId()
     **/

    /**
     * Проверить количество предметов у игрока.
     * @param itemId
     **/
    public static long OwnItemCount(L2Character player, int itemId) {
        return player.getPlayer().getInventory().getCountOf(itemId);
    }

    public static long OwnItemCount(L2Player player, int itemId) {
        return player.getInventory().getCountOf(itemId);
    }

    /**
     * Удаляет указанные предметы из инвентаря игрока, и обновляет инвентарь
     * @param itemId : id удаляемого предмета
     * @param count : число удаляемых предметов<br>
     * Если count передать -1, то будут удалены все указанные предметы.
     * @return Количество удаленных предметов
     **/
    public static long DeleteItem1(L2Character c0, int itemId, long count) {
        if (c0 == null)
            return 0;
        L2Player player = c0.getPlayer();
        if (player == null)
            return 0;
        L2ItemInstance item = player.getInventory().getItemByItemId(itemId);
        if (item == null)
            return 0;
        if (count < 0 || count > item.getCount())
            count = item.getCount();
        player.getInventory().destroyItemByItemId(itemId, count, true);
        player.sendPacket(SystemMessage.removeItems(itemId, count));

        return count;
    }

    /**
     * Добавить предмет игроку
     * @param itemId
     * @param count
     **/
    public static L2ItemInstance GiveItem1(L2Player player, int itemId, long count) {
        if (player == null)
            return null;

        if (count <= 0)
            count = 1;

        L2Item template = ItemTemplates.getInstance().getTemplate(itemId);
        if (template == null)
            return null;

        L2ItemInstance ret = null;
        if (template.isStackable()) {
            L2ItemInstance item = ItemTemplates.getInstance().createItem(itemId);
            item.setCount(count);
            ret = player.getInventory().addItem(item);
            Log.LogItem(player, Log.Sys_GetItem, item);
        } else
            for (int i = 0; i < count; i++) {
                L2ItemInstance item = ItemTemplates.getInstance().createItem(itemId);
                ret = player.getInventory().addItem(item);
                Log.LogItem(player, Log.Sys_GetItem, item);
            }

        player.sendPacket(SystemMessage.obtainItems(template.getItemId(), count, 0));
        player.sendStatusUpdate(false, StatusUpdate.CUR_LOAD);
        return ret;
    }

    public static void InstantTeleport(L2Character player, int x, int y, int z) {
        player.teleToLocation(x, y, z);
    }

    public void Say(int strId) {
        NpcSay ns = new NpcSay(this, Say2C.NPC_ALL, strId);
        broadcastPacket2(ns);
    }

    public void Say(String strId) {
        NpcSay ns = new NpcSay(this, Say2C.NPC_ALL, strId);
        broadcastPacket2(ns);
    }

    public static void Say(L2Character player, L2NpcInstance npc, int fStringId) {
        NpcSay cs = new NpcSay(npc, Say2C.NPC_ALL, fStringId);
        if (player != null)
            player.sendPacket(cs);
        else {
            for (L2Player pl : L2World.getAroundPlayers(npc, 500, 200))
                if (pl != null && !pl.isBlockAll())
                    pl.sendPacket(cs);
        }
    }

    public static void Say(L2Character player, L2NpcInstance npc, String fStringId) {
        NpcSay cs = new NpcSay(npc, Say2C.NPC_ALL, fStringId);
        if (player != null)
            player.sendPacket(cs);
        else {
            for (L2Player pl : L2World.getAroundPlayers(npc, 500, 200))
                if (pl != null && !pl.isBlockAll())
                    pl.sendPacket(cs);
        }
    }

    public static int GiveEventItem(L2Player player, int need_id, long need_count, int id, long count, int day, int hour, int min, int sec) {
        if (!player.isQuestContinuationPossible(true))
            return 0;

        if (player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
            return 0;

        if (player.getInventory().getItemByItemId(need_id) == null || player.getInventory().getItemByItemId(need_id).getCount() < need_count) {
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
            return 0;
        }
        if (player.getItemBayTime(id))
            return -1;
        long time = System.currentTimeMillis() + (day * 86400000) + (hour * 3600000) + (min * 60000) + (sec * 1000);
        player.setItemBayTime(id, time);
        Functions.removeItem(player, need_id, need_count);
        Functions.addItem(player, id, count);
        //player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1).addItemName(id));
        player.sendPacket(new ItemList(player, true));
        return 1;
    }

    public synchronized L2NpcInstance CreateOnePrivateEx(int npc_id, String npc_ai, int arg1, int arg2, int x, int y, int z, int heading, int param1, int param2, int param3) {
        if (npc_id > 1000000)
            npc_id -= 1000000;
        L2NpcTemplate template = NpcTable.getTemplate(npc_id);
        if (template == null) {
            System.out.println("CreateOnePrivateEx: Error!!! -> " + npc_id + " :" + npc_ai);
            return null;
        }
        String instance = (arg2 == 1 ? "L2Minion" : "L2Monster"); // L2Minion
        L2NpcInstance character = null;
        template.ai_type = npc_ai;
        template.setInstance(instance);

        try {
            L2Spawn sp = new L2Spawn(template);
            sp.setLocx(x);
            sp.setLocy(y);
            sp.setLocz(z);
            sp.setHeading(heading);
            sp.setRespawnDelay(arg1);
            character = sp.doSpawn(true, arg2 == 1, param1, param2, param3, this);
            if (character == null)
                _log.warning("L2NpcInstance " + getNpcId() + " WTF???");
            if (arg2 == 1) {
                ((L2MinionInstance) character).setLeader((L2MonsterInstance) this);
                if (((L2MonsterInstance) this)._minionList == null)
                    ((L2MonsterInstance) this).setNewMinionList();
                MinionList list = ((L2MonsterInstance) this).getMinionList();
                if (list != null)
                    list.addSpawnedMinion((L2MinionInstance) character);
            }
            if (arg1 == 0)
                character.setSpawn(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return character;
    }

    public int GetIndexFromCreature(L2Character arg0) {
        return arg0.getObjectId();
    }

    public void SetFlagJournal(L2Player player, int id, int state) {
        player.getQuestState(id).setCond(state);
    }

    public void ShowQuestMark(L2Player player, int id) {
        if (player != null) {
            QuestState qs = player.getQuestState(id);
            player.sendPacket(new ExShowQuestMark(id, qs == null ? 0 : qs.getCond()));
        }
    }

    public void SoundEffect(L2Player player, String sound) {
        if (player != null)
            player.sendPacket(new PlaySound(sound));
    }

    // Получаем и сравниваем второй стейт с нужным нам при условии что поставили цифру 1 если поставим 0, то это будет первый стейт
    public int GetMemoStateEx(L2Player player, int id, int val) {
        if (player.getQuestState(id) == null)
            return -1;
        return player.getQuestState(id).getInt("MemoState" + val);
    }

    public void SetMemoStateEx(L2Player player, int id, int val, int state) {
        if (player.getQuestState(id) == null && state == 1)
            QuestManager.getQuest(id).newQuestState(player, 2);
        player.getQuestState(id).set("MemoState" + val, state);
    }

    public int GetMemoState(L2Player player, int id) {
        if (player.getQuestState(id) == null)
            return -1;
        return player.getQuestState(id).getInt("MemoState0");
    }

    public void SetMemoState(L2Player player, int id, int state) {
        if (player.getQuestState(id) == null && state == 1)
            QuestManager.getQuest(id).newQuestState(player, 2);
        player.getQuestState(id).set("MemoState0", state);
    }

    public void RemoveMemo(L2Player player, int id) {
        player.getQuestState(id).unset("MemoState0");
        player.getQuestState(id).unset("MemoState1");

        // Вот это уточнить потом!!!...
        player.getQuestState(id).exitCurrentQuest(true);
    }

    public void SetMemo(L2Player player, int id) {
        QuestManager.getQuest(id).newQuestState(player, 2);
    }

    public void SetOneTimeQuestFlag(L2Player player, int id, int value) {
        PlayerData.getInstance().SetOneTimeQuestFlag(player, id, value);
    }

    public int GetOneTimeQuestFlag(L2Player player, int id) {
        QuestState quests = player.getQuestState(id);
        if (quests != null && quests.isCompleted())
            return 1;
        return 0;
    }

    public int GetCurrentTick() {
        return (int) (System.currentTimeMillis() - _currentTick) / 1000;
    }

    public int IsNullCreature(L2Character target) {
        return target == null ? 1 : 0;
    }

    public int IsNull(Object target) {
        return target == null ? 1 : 0;
    }

    public int DistFromMe(L2Character actor) {
        return (int) getRealDistance3D(actor);
    }

    public int Castle_GetPledgeId() {
        if (getCastle() == null || getCastle().getOwner() == null)
            return 0;
        return getCastle().getOwner().getClanId();
    }

    public L2Player Pledge_GetLeader(L2Player player) {
        if (player == null || player.getClan() == null || !player.getClan().getLeader().isOnline())
            return null;
        return player.getClan().getLeader().getPlayer();
    }

    /**
     * -1 -	Независимый статус
     *  0 -	Не Определен
     *  1 -	Статус Контракт
     **/
    public int Fortress_GetContractStatus(int fort_id) {
        switch (FortressManager.getInstance().getFortressByIndex(fort_id).getFortState()) {
            case 0:
                return 0;
            case 1:
                return -1;
            case 2:
                return 1;
        }
        return 0;
    }

    public boolean Castle_IsUnderSiege() {
        return getCastle().getSiege().isInProgress();
    }

    public int GetDominionWarState(int id) {
        return TerritorySiege.isInProgress() ? 5 : 0;
    }

    public int HaveMemo(L2Player player, int id) {
        return (player.getQuestState(QuestManager.getQuest(id).getName()) == null || player.getQuestState(QuestManager.getQuest(id).getName()).getCond() < 1) ? 0 : 1;
    }

    public void FHTML_SetFileName(StringBuilder fhtml0, String fileName) {
        fhtml0.delete(0, fhtml0.length());
        fhtml0.append(Files.read_pts(fileName, null));
    }

    public void FHTML_SetInt(StringBuilder fhtml0, String valName, int value) {
        int size = fhtml0.length();
        String txt = fhtml0.toString();
        fhtml0.delete(0, size);
        fhtml0.append(txt.replace("<?" + valName + "?>", String.valueOf(value)));
    }

    public void FHTML_SetStr(StringBuilder fhtml0, String valName, String value) {
        int size = fhtml0.length();
        String txt = fhtml0.toString();
        fhtml0.delete(0, size);
        fhtml0.append(txt.replace("<?" + valName + "?>", value));
    }

    public void DeclareLord(int id, L2Player player) {
        CastleManager.getInstance().getCastleByIndex(id - 80).setDominionLord(player.getObjectId(), true);
    }

    public void ShowQuestFHTML(L2Player player, StringBuilder fhtml0, int id) {
        NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
        npcReply.setHtml(fhtml0.toString());
        npcReply.setQuest(id);
        player.sendPacket(npcReply);
    }

    public void ShowQuestPage(L2Player player, String fileName, int id) {
        NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
        npcReply.setHtml(Files.read_pts(fileName, player));
        npcReply.setQuest(id);
        player.sendPacket(npcReply);
    }

    public void ShowFHTML(L2Player player, StringBuilder fhtml0) {
        NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
        npcReply.setHtml(fhtml0.toString());
        player.sendPacket(npcReply);
    }

    public void ShowOnScreenMsgStr(L2Character c, int nPosition, int nUnk1, int nSize, int nUnk2, int nUnk3, int nEffect, int nTime, int nUnk4, String pwsMsg) {
        c.sendPacket(new ExShowScreenMessage(nPosition, nUnk1, nSize, nUnk2, nUnk3, nEffect, nTime, nUnk4, pwsMsg));
    }

    public String MakeFString(int msgId, String add1, String add2, String add3, String add4, String add5) {
        String t = FStringCache.getString(msgId);
        if (!add1.isEmpty())
            t = t.replace("$s1", add1);
        if (!add1.isEmpty())
            t = t.replace("$s2", add2);
        if (!add1.isEmpty())
            t = t.replace("$s3", add3);
        if (!add1.isEmpty())
            t = t.replace("$s4", add4);
        if (!add1.isEmpty())
            t = t.replace("$s5", add5);
        return t;
    }

    public void ShowSystemMessage(L2Player talker, int msgId) {
        talker.sendPacket(new SystemMessage(msgId));
    }

    // Не красиво до пизды, но пускай пока будет так...
    public int GetMemoCount(L2Player player) {
        int count = 0;
        for (QuestState quest : player.getAllQuestsStates())
            if (quest != null && ((quest.getQuest().getQuestIntId() < 999 || quest.getQuest().getQuestIntId() > 10000) && quest.getQuest().getQuestIntId() != 255) && quest.isStarted() && quest.getCond() > 0)
                count++;
        return count;
    }

    public int GetInventoryInfo(L2Player talker, int type) {
        switch (type) {
            case 0:
                return talker.getInventory().getSize(false); // Количество занятых ячеек обычного инвентаря.
            case 1:
                return talker.getInventoryLimit(); // Максимально разрешенное количество ячеек обычного инвентаря.
            case 2:
                return talker.getInventory().getSize(true); // Количество занятых ячеек квестового инвентаря.
            case 3:
                return talker.getQuestInventoryLimit(); // Максимально разрешенное количество ячеек квестового инвентаря.
        }
        return -1;
    }

    private FastMap<String, Integer> _choice = new FastMap<String, Integer>();

    public void AddChoice(int code, String buton) {
        if (_choice.containsKey(buton.replace(" (In Progress)", "").replace(" (Done)", "")))
            _choice.remove(buton.replace(" (In Progress)", "").replace(" (Done)", ""));
        _choice.put(buton, code);
    }

    public void AddLog(int type, L2Player talker, int questsId) {
    }

    // В ПТСке это дает понять НПСу с каким ID квеста он сейчас работает, чесно пока хуй его знает зачем оно нужно и как влияет и нужно ли оно нам...
    public void SetCurrentQuestID(int questsId) {
    }

    public void ShowMultisell(int id, L2Player talker) {
        Castle castle = getCastle(talker);
        L2Multisell.getInstance().SeparateAndSend(id, talker, castle != null ? castle.getTaxRate() : 0);
    }

    // Показывает ШТМЛ при нажатии на кнопку Квест...@id имеет 2 значения, 0 и 1, что оно такое хуй его знает.
    public void ShowChoicePage(L2Player player, int id) {
        StringBuffer sb = new StringBuffer();

        sb.append("<html><body>");
        for (String buton : _choice.keySet())
            if (player.getLang().equals("en"))
                sb.append("<a action=\"bypass -h talk_select?code=" + _choice.get(buton)).append("\">[").append(buton).append("]</a><br>");
            else
                sb.append("<a action=\"bypass -h talk_select?code=" + _choice.get(buton)).append("\">[").append(buton).append("]</a><br>");
        sb.append("</body></html>");

        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setHtml(sb.toString());
        player.sendPacket(html);
        _choice.clear();
    }

    public void ShowMsgInTerritory(int unk, String ter_name, int msgId) {
        for (L2Player player : ZoneManager.getInstance().getZoneByName(ter_name).getInsidePlayers())
            player.sendPacket(new SystemMessage(msgId));
    }

    public void InstantTeleportInMyTerritory(int x, int y, int z, int rnd) {
        for (L2Playable obj : L2World.getAroundPlayables(this, 500, 150))
            if (obj != null)
                obj.teleToLocation(Location.coordsRandomize(x, y, z, 0, rnd, 0));
    }

    public void CastBuffForQuestReward(L2Character target, int skill) {
        L2Skill castingSkill = SkillTable.getInstance().getInfo(skill / 65536, skill % 65536);
        target.broadcastSkill(new MagicSkillUse(this, target, castingSkill.getDisplayId(), castingSkill.getLevel(), 0, 0), true);
        onMagicUseTimer(target, castingSkill, true);
    }

    public int IsMyBossAlive() {
        if (getMyLeader() != null && !getMyLeader().isDead())
            return 1;
        return 0;
    }

    //Privates=[croc_of_swamp:croc_of_swamp:1:1200sec;croc_of_swamp:croc_of_swamp:1:1200sec;warrior_of_swamp:warrior_of_swamp:1:1200sec;warrior_of_swamp:warrior_of_swamp:1:1200sec;warrior_of_swamp:warrior_of_swamp:1:1200sec]
    public synchronized void CreatePrivates(String Privates) {
        if (Privates.isEmpty())
            return;
        Privates = Privates.replace("[", "").replace("]", "");
        String[] npcs = Privates.split(";");

        for (int i = 0; i < npcs.length; i++) {
            String[] arg = npcs[i].split(":");
            String pts_npc_name = arg[0];
            String npc_ai = arg[1];
            int count = Integer.parseInt(arg[2]);
            int resp = Integer.parseInt(arg[3].substring(0, arg[3].length() - 3));
            Location loc = Rnd.coordsRandomize(getX(), getY(), getZ(), 0, 120, 120);
            CreateOnePrivateEx(NpcData.getNpcId(pts_npc_name), npc_ai, resp, 1, loc.x, loc.y, loc.z, getHeading(), 0, 0, 0);
        }
    }

    public L2Character top_desire_target() {
        return getTopDamager(getAggroMap().values());
    }

    public void Shout(String strId) {
        NpcSay ns = new NpcSay(this, Say2C.NPC_SHOUT, strId);
        broadcastPacket2(ns);
    }

    public void Shout(int strId) {
        NpcSay ns = new NpcSay(this, Say2C.NPC_SHOUT, strId);
        broadcastPacket2(ns);
    }

    public void Area_SetOnOff(String areaName, int action) {
        if (action == 1)
            ZoneManager.getInstance().getZoneByName(areaName).setActive(true);
        else
            ZoneManager.getInstance().getZoneByName(areaName).setActive(false);
    }

    public int GetDirection(L2Character target) {
        return (int) Location.calculateAngleFrom(target, target.getTarget());
    }

    public void RemoveHateInfoByCreature(L2Character target) {
        target.removeFromHatelist(this, false);
    }

    public void MPCC_SetMasterPartyRouting(L2CommandChannel channel, int i) {
        MPR = i;
        if (channel != null)
            MPCC_Master = channel.getChannelLeader();
        else
            MPCC_Master = null;
        Log.add("L2MonsterInstance[" + getName() + "][" + getNpcId() + "][" + getLoc() + "][" + getReflectionId() + "]: set master[" + MPCC_Master + "]", "mpcc_debug");
    }

    public int getMasterPartyRouting() {
        return MPR;
    }

    public void setMasterPartyRouting(int i) {
        MPR = i;
    }

    public L2Player MPCC_GetMaster() {
        return MPCC_Master;
    }

    public void ChangeNickName(L2NpcInstance npc, int str_id) {
        npc._nps_string_name = str_id;
    }

    /**
     * Привелегии игрока в клане:
     * [PP_JOIN]				=	1
     * [PP_GIVE_NICKNAME]		=	2
     * [PP_VIEW_WAREHOUSE]		=	3
     * [PP_MANAGE_GRADE]		=	4
     * [PP_DECLARE_WAR]			=	5
     * [PP_OUST_MEMBER]			=	6
     * [PP_SET_CREST]			=	7
     * [PP_MANAGE_MASTER]		=	8
     * [PP_MANAGE_GROWTH]		= 	9
     * [PP_SUMMON_AIRSHIP]		= 	10
     * [PP_OPEN_AGIT_DOOR]		=	11
     * [PP_USE_AGIT_FUNC]		=	12
     * [PP_AGIT_AUCTION]		=	13
     * [PP_OUST_FROM_AGIT]		=	14
     * [PP_CONTROL_AGIT_FUNC]	=	15
     * +[PP_OPEN_CASTLE_DOOR]	=	16
     * [PP_MANAGE_MANOR]		=	17
     * [PP_REGISTER_CASTLE_WAR]	=	18
     * [PP_USE_CASTLE_FUNC]		=	19
     * [PP_OUST_FROM_CASTLE]	=	20
     * [PP_MANAGE_TAX]			=	21
     * [PP_MANAGE_MERCENARY]	=	22
     * [PP_CONTROL_CASTLE_FUNC]	=	23
     **/
    public int HavePledgePower(L2Player player, int val) {
        Castle castle = getCastle();
        if (castle != null && castle.getId() > 0 && castle.getOwnerId() == player.getClanId() && (player.getClanPrivileges() & L2Clan.CP_CS_ENTRY_EXIT) == L2Clan.CP_CS_ENTRY_EXIT)
            return 16;
        return 0;
    }

    // Не известно, возможно альянс или принадлежность к академии...
    public int Castle_GetPledgeState(Object arg0) {
        return 0;
    }

    public int residence_id() {
        if (getClanHall().getId() > -1)
            return getClanHall().getId();
        else if (getFortress().getId() > -1)
            return getFortress().getId();
        else
            return getCastle().getId();
    }

    @Override
    public boolean isNpc() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public HardReference<? extends L2NpcInstance> getRef() {
        return (HardReference<? extends L2NpcInstance>) super.getRef();
    }

    public void setRaidStatus(RaidBossSpawnManager.Status status) {
    }

    public RaidBossSpawnManager.Status getRaidStatus() {
        return null;
    }
}