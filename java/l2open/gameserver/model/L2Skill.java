package l2open.gameserver.model;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.instancemanager.HandysBlockCheckerManager;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.entity.Duel;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.gameserver.model.entity.vehicle.L2Ship;
import l2open.gameserver.model.entity.vehicle.L2Vehicle;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.FlyToLocation.FlyType;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.serverpackets.DamageTextNewPacket;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.conditions.Condition;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.skills.enums.*;
import l2open.gameserver.skills.funcs.Func;
import l2open.gameserver.skills.funcs.FuncTemplate;
import l2open.gameserver.skills.skillclasses.*;
import l2open.gameserver.skills.skillclasses.DeathPenalty;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.gameserver.templates.StatsSet;
import l2open.util.ArrayMap;
import l2open.util.GArray;
import l2open.util.Rnd;
import l2open.util.Location;
import l2open.util.Util;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class L2Skill extends StatTemplate implements Cloneable {
    private final ReentrantLock lock = new ReentrantLock();
    private final ReentrantLock _effect_loc = new ReentrantLock();

    public static class AddedSkill {
        public int id;
        public int level;

        public AddedSkill(int id, int level) {
            this.id = id;
            this.level = level;
        }

        public L2Skill getSkill() {
            return SkillTable.getInstance().getInfo(id, level);
        }
    }

    public static enum Element {
        FIRE(0, Stats.ATTACK_ELEMENT_FIRE, Stats.FIRE_RECEPTIVE),
        WATER(1, Stats.ATTACK_ELEMENT_WATER, Stats.WATER_RECEPTIVE),
        WIND(2, Stats.ATTACK_ELEMENT_WIND, Stats.WIND_RECEPTIVE),
        EARTH(3, Stats.ATTACK_ELEMENT_EARTH, Stats.EARTH_RECEPTIVE),
        SACRED(4, Stats.ATTACK_ELEMENT_SACRED, Stats.SACRED_RECEPTIVE),
        UNHOLY(5, Stats.ATTACK_ELEMENT_UNHOLY, Stats.UNHOLY_RECEPTIVE),
        NONE(6, null, null);

        /**
         * Массив элементов без NONE
         **/
        public final static Element[] VALUES = Arrays.copyOf(values(), 6);

        private final int id;
        private final Stats attack;
        private final Stats defence;

        private Element(int id, Stats attack, Stats defence) {
            this.id = id;
            this.attack = attack;
            this.defence = defence;
        }

        public int getId() {
            return id;
        }

        public Stats getAttack() {
            return attack;
        }

        public Stats getDefence() {
            return defence;
        }

        public static Element getElementById(int id) {
            for (Element e : values())
                if (e.getId() == id)
                    return e;
            return NONE;
        }
    }

    public static enum NextAction {
        ATTACK,
        CAST,
        DEFAULT,
        MOVE,
        NONE
    }

    public static enum SkillOpType {
        OP_ACTIVE,
        OP_PASSIVE,
        OP_TOGGLE,
        OP_ON_ACTION;
    }

    /**
     * target_type = enemy	affect_scope = square	affect_object = not_friend	fan_range = {0;0;300;100}
     * square - прямоуголник, шириной 100 и длиной ДО цели в 300.
     * ***********************************************************************************************************************************
     **/
    public static enum SkillTargetType {
        TARGET_ALLY,
        TARGET_AREA,
        TARGET_AREA_AIM_CORPSE,
        TARGET_AURA,
        TARGET_PET_AURA,
        TARGET_CHEST,
        TARGET_CLAN,
        TARGET_CLAN_ONLY,
        TARGET_COMMAND_CHANEL,
        TARGET_CORPSE,
        TARGET_CORPSE_PLAYER,
        TARGET_ENEMY_PET,
        TARGET_ENEMY_SUMMON,
        TARGET_ENEMY_SERVITOR,
        TARGET_EVENT,
        TARGET_FLAGPOLE,
        TARGET_HOLY,
        TARGET_ITEM,
        TARGET_MULTIFACE,
        TARGET_MULTIFACE_AURA,
        TARGET_TUNNEL, // affect_scope = square - прямоугольная область...
        TARGET_TUNNEL_SELF, // affect_scope = square_pb - прямоугольная область, работает от кастера, до дистанции N
        TARGET_NONE,
        TARGET_ONE,
        TARGET_ONE_PARTY,
        TARGET_OWNER,
        TARGET_OWNER_PET,
        TARGET_PARTY,
        TARGET_PARTY_ONE,
        TARGET_PET,
        TARGET_SELF,
        TARGET_SERVITOR,
        TARGET_SIEGE,
        TARGET_UNLOCKABLE
    }

    public static enum SkillType {
        A1(Continuous.class),
        BALANCE(Balance.class),
        BEAST_FEED(BeastFeed.class),
        BEAST_FARM(BeastFarm.class),
        BLEED(Continuous.class),
        BUFF(Continuous.class),
        CALL(Call.class),
        CHARGE(Charge.class),
        CHARGE_SOUL(ChargeSoul.class),
        CHAIN_HEAL(ChainHeal.class),
        COMBATPOINTHEAL(CombatPointHeal.class),
        CONT(Toggle.class),
        CPDAM(CPDam.class),
        CPHOT(Continuous.class),
        CUB_DRAIN(CubDrain.class),
        CUB_HEAL(CubHeal.class),
        CUB_MDAM(CubMDam.class),
        CRAFT(Craft.class),
        CLAN_GATE(ClanGate.class),
        DEATH_PENALTY(DeathPenalty.class),
        DEBUFF(Continuous.class),
        DEFAULT(Default.class),
        DEFUSE_TRAP(DefuseTrap.class),
        DETECT_TRAP(DetectTrap.class),
        DISCORD(Continuous.class),
        DOT(Continuous.class),
        DRAIN(Drain.class),
        DRAIN_SOUL(DrainSoul.class),
        EFFECT(Effect.class),
        EFFECTS_FROM_SKILLS(EffectsFromSkills.class),
        ENCHANT_ARMOR,
        ENCHANT_WEAPON,
        ENERGY_REPLENISH(EnergyReplenish.class),
        EXTRACT_STONE(ExtractStone.class),
        FEED_PET,
        FISHING(Fishing.class),
        GIVE_VITALITY(GiveVitality.class),
        HARDCODED(Effect.class),
        HARVESTING(Harvesting.class),
        HEAL(Heal.class),
        HEAL_PERCENT(HealPercent.class),
        HOT(Continuous.class),
        KAMAEL_WEAPON_EXCHANGE(KamaelWeaponExchange.class),
        LETHAL_SHOT(LethalShot.class),
        LUCK,
        MANADAM(ManaDam.class),
        MANAHEAL(ManaHeal.class),
        MANAHEAL_PERCENT(ManaHealPercent.class),
        MDAM(MDam.class),
        MDOT(Continuous.class),
        MPHOT(Continuous.class),
        MUTE(Disablers.class),
        ADD_PC_BANG(PcBangPointsAdd.class),
        NOTDONE,
        NOTUSED,
        OUTPOST(Outpost.class),
        PARALYZE(Disablers.class),
        PASSIVE,
        PDAM(PDam.class),
        RDAM(RDam.class),
        POISON(Continuous.class),
        PUMPING(ReelingPumping.class),
        RECALL(Recall.class),
        REELING(ReelingPumping.class),
        REFILL(Refill.class),
        RESURRECT(Resurrect.class),
        RIDE(Ride.class),
        ROOT(Disablers.class),
        SHIFT_AGGRESSION(ShiftAggression.class),
        SIEGEFLAG(SiegeFlag.class),
        SLEEP(Disablers.class),
        SOULSHOT,
        SOWING(Sowing.class),
        SPAWN(Spawn.class),
        SPHEAL(SPHeal.class),
        EXPHEAL(EXPHeal.class),
        SPIRITSHOT,
        SPOIL(Spoil.class),
        STEAL_BUFF(StealBuff.class),
        STUN(Disablers.class),
        SUMMON(Summon.class),
        SUMMON_ITEM(SummonItem.class),
        SWEEP(Sweep.class),
        POKEMON(Pokemon.class),
        TAKECASTLE(TakeCastle.class),
        TAKEFORTRESS(TakeFortress.class),
        TAKEFLAG(TakeFlag.class),
        TELEPORT_NPC(TeleportNpc.class),
        TELEPORT(Teleport.class),
        TRANSFORMATION(Transformation.class),
        UNLOCK(Unlock.class),
        VITALITY_UPDATE(VitalityUpdate.class),
        WATCHER_GAZE(Continuous.class);

        private final Class<? extends L2Skill> clazz;

        private SkillType() {
            clazz = Default.class;
        }

        private SkillType(Class<? extends L2Skill> clazz) {
            this.clazz = clazz;
        }

        public L2Skill makeSkill(StatsSet set) {
            try {
                Constructor<? extends L2Skill> c = clazz.getConstructor(StatsSet.class);
                return c.newInstance(set);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    protected static Logger _log = Logger.getLogger(L2Skill.class.getName());

    protected static final AddedSkill[] _emptyAddedSkills = new AddedSkill[0];
    protected static final Func[] _emptyFunctionSet = new Func[0];

    protected EffectTemplate[] _effectTemplates;
    protected FuncTemplate[] _funcTemplates;

    protected GArray<Integer> _teachers; // which NPC teaches
    protected GArray<ClassId> _canLearn; // which classes can learn

    protected AddedSkill[] _addedSkills;

    protected final int[] _itemConsume;
    protected final int[] _itemConsumeId;

    public static final int SKILL_CUBIC_MASTERY = 143;
    public static final int SKILL_CRAFTING = 172;
    public static final int SKILL_POLEARM_MASTERY = 216;
    public static final int SKILL_CRYSTALLIZE = 248;
    public static final int SKILL_WEAPON_MAGIC_MASTERY1 = 249;
    public static final int SKILL_WEAPON_MAGIC_MASTERY2 = 250;
    public static final int SKILL_BLINDING_BLOW = 321;
    public static final int SKILL_STRIDER_ASSAULT = 325;
    public static final int SKILL_BUILD_HEADQUARTERS = 326;
    public static final int SKILL_WYVERN_AEGIS = 327;
    public static final int SKILL_BLUFF = 358;
    public static final int SKILL_HEROIC_MIRACLE = 395;
    public static final int SKILL_HEROIC_BERSERKER = 396;
    public static final int SKILL_SOUL_MASTERY = 467;
    public static final int SKILL_TRANSFOR_DISPELL = 619;
    public static final int SKILL_FINAL_FLYING_FORM = 840;
    public static final int SKILL_AURA_BIRD_FALCON = 841;
    public static final int SKILL_AURA_BIRD_OWL = 842;
    public static final int SKILL_DETECTION = 933;
    public static final int SKILL_RECHARGE = 1013;
    public static final int SKILL_TRANSFER_PAIN = 1262;
    public static final int SKILL_FISHING_MASTERY = 1315;
    public static final int SKILL_NOBLESSE_BLESSING = 1323;
    public static final int SKILL_SUMMON_CP_POTION = 1324;
    public static final int SKILL_FORTUNE_OF_NOBLESSE = 1325;
    public static final int SKILL_HARMONY_OF_NOBLESSE = 1326;
    public static final int SKILL_SYMPHONY_OF_NOBLESSE = 1327;
    public static final int SKILL_HEROIC_VALOR = 1374;
    public static final int SKILL_HEROIC_GRANDEUR = 1375;
    public static final int SKILL_HEROIC_DREAD = 1376;
    public static final int SKILL_MYSTIC_IMMUNITY = 1411;
    public static final int SKILL_RAID_BLESSING = 2168;
    public static final int SKILL_WEAPON_PENALTY = 6209;
    public static final int SKILL_ARMOR_PENALTY = 6213;
    public static final int SKILL_DISMISS_AGATHION = 3267;
    public static final int SKILL_HINDER_STRIDER = 4258;
    public static final int SKILL_WYVERN_BREATH = 4289;
    public static final int SKILL_RAID_CURSE = 4515;
    public static final int SKILL_CHARM_OF_COURAGE = 5041;
    public static final int SKILL_EVENT_TIMER = 5239;
    public static final int SKILL_BATTLEFIELD_DEATH_SYNDROME = 5660;

    public final static int SAVEVS_INT = 1;
    public final static int SAVEVS_WIT = 2;
    public final static int SAVEVS_MEN = 3;
    public final static int SAVEVS_CON = 4;
    public final static int SAVEVS_DEX = 5;
    public final static int SAVEVS_STR = 6;

    protected boolean _isBehind;
    protected boolean _isCancelable;
    public boolean _is_alt_cancel;
    protected boolean _isCorpse;
    protected boolean _isCommon;
    protected boolean _isItemHandler;
    protected Boolean _isOffensive;
    protected Boolean _isPvpSkill;
    protected Boolean _isPvm;
    protected boolean _isForceUse;
    protected int _isMagic;
    protected boolean _isSaveable;
    protected boolean _isSkillTimePermanent;
    protected boolean _isReuseDelayPermanent;
    protected boolean _isSuicideAttack;
    protected boolean _isShieldignore;
    protected boolean _isUndeadOnly;
    protected Boolean _isUseSS;
    protected boolean _isOverhit;
    protected boolean _isSoulBoost;
    protected boolean _isChargeBoost;
    protected boolean _isUsingWhileCasting;
    protected boolean _skillInterrupt;
    protected boolean _deathlink;
    protected boolean _basedOnTargetDebuff;
    protected boolean _isNotUsedByAI;
    protected boolean _isMusic;
    protected boolean _isNotAffectedByMute;
    protected boolean _flyingTransformUsage;
    protected boolean _isOlympiadEnabled;
    protected boolean _isTrigger;
    protected boolean _isStaticHeal;
    protected boolean _hideStartMessage;
    protected boolean _hideStopMessage;
    protected boolean _isCleanse;
    protected boolean _isCancel;
    protected boolean _isNewbie;
    protected boolean isEnemy;
    protected boolean _offlineTime;

    protected SkillType _skillType;
    protected SkillOpType _operateType;
    protected SkillTargetType _targetType;
    protected NextAction _nextAction;
    protected Element _element;
    protected FlyType _flyType;
    protected boolean _flyToBack;
    protected Condition[] _preCondition;
    protected int _saveVs = 0;

    protected Integer _id;
    protected Short _level;
    protected Short _baseLevel = 1;
    protected Integer _displayId;
    protected Short _displayLevel;

    protected int _activateRate;
    protected int _castRange;
    protected int _cancelTarget;
    protected int _condCharges;
    protected int _coolTime;
    protected int _delayedEffect;
    protected int _delayedEffectLevel;
    protected int _effectPoint;
    protected int _elementPower;
    protected int _flyRadius;
    protected int _forceId;
    protected int _hitTime;
    protected int _hpConsume;
    protected int _levelModifier;
    protected int _magicLevel;
    protected int _matak;
    protected int _minPledgeClass;
    protected int _minRank;
    protected int _negatePower;
    protected int _negateSkill;
    protected int _npcId;
    protected int _numCharges;
    protected int _skillInterruptTime;
    protected int _skillRadius;
    protected int _radius;
    protected int _soulsConsume;
    protected int _symbolId;
    protected int _weaponsAllowed;
    protected int _castCount;
    protected int _enchantLevelCount;
    protected int _criticalRate;
    protected int _absorbPartStatic;
    protected int _refId;
    protected int _refConsume;

    protected long _reuseDelay;

    protected double _power;
    protected double _powerPvP;
    protected double _powerPvE;
    protected double _power2;
    protected double _mpConsume1;
    protected double _mpConsume2;
    protected double _lethal1;
    protected double _lethal2;
    protected double _absorbPart;
    protected int _levelLearn;

    // ------------------------------------------------------------
    protected int _affect_range;
    public int affect_limit_p;
    public int affect_limit_n;
    public int affect_limit_s;

    public int fan_range_s;
    public int fan_range_h;
    public int fan_range_l;

    public TargetType target_type = TargetType.none;
    public AffectObject affect_object = AffectObject.none;
    public AffectScope affect_scope = AffectScope.none;
    // ------------------------------------------------------------

    protected String _name;

    public boolean _isStandart = false;

    protected boolean _applyOnCaster = false;
    // Жрет много памяти, включить только если будет необходимость
    //protected StatsSet _set;

    /**
     * Внимание!!! У наследников вручную надо поменять тип на public
     *
     * @param set парамерты скилла
     */
    protected L2Skill(StatsSet set) {
        //_set = set;
        _id = set.getInteger("skill_id");
        _level = set.getShort("level");
        _displayId = set.getInteger("displayId", _id);
        _displayLevel = set.getShort("displayLevel", _level);
        _name = set.getString("name");
        _operateType = set.getEnum("operateType", SkillOpType.class);
        _soulsConsume = set.getInteger("soulsConsume", 0);
        _isSoulBoost = set.getBool("soulBoost", false);
        _isChargeBoost = set.getBool("chargeBoost", false);
        _isUsingWhileCasting = set.getBool("isUsingWhileCasting", false);
        _matak = set.getInteger("mAtk", 0);
        _isUseSS = set.getBool("useSS", null);
        _forceId = set.getInteger("forceId", 0);
        _castCount = set.getInteger("castCount", 0);

        String s1 = set.getString("itemConsumeCount", "");
        String s2 = set.getString("itemConsumeId", "");

        if (s1.length() == 0)
            _itemConsume = new int[]{0};
        else {
            String[] s = s1.split(" ");
            _itemConsume = new int[s.length];
            for (int i = 0; i < s.length; i++)
                _itemConsume[i] = Integer.parseInt(s[i]);
        }

        if (s2.length() == 0)
            _itemConsumeId = new int[]{0};
        else {
            String[] s = s2.split(" ");
            _itemConsumeId = new int[s.length];
            for (int i = 0; i < s.length; i++)
                _itemConsumeId[i] = Integer.parseInt(s[i]);
        }

        _isItemHandler = set.getBool("isHandler", false);
        _isCommon = set.getBool("isCommon", false);
        _isSaveable = set.getBool("isSaveable", true);

        _skillRadius = set.getInteger("skillRadius", 80);
        _radius = set.getInteger("radius", 60);
        _targetType = set.getEnum("target", SkillTargetType.class);

        _isUndeadOnly = set.getBool("undeadOnly", false);
        _isCorpse = set.getBool("corpse", false);
        _power = set.getDouble("power", 0.);
        _power2 = set.getDouble("power2", 0.);
        _powerPvP = set.getDouble("powerPvP", 0.);
        _powerPvE = set.getDouble("powerPvE", 0.);
        _skillType = set.getEnum("skillType", SkillType.class);
        _isSuicideAttack = set.getBool("isSuicideAttack", false);
        _deathlink = set.getBool("deathlink", false);
        _basedOnTargetDebuff = set.getBool("basedOnTargetDebuff", false);
        _isNotUsedByAI = set.getBool("isNotUsedByAI", false);
        _isMusic = set.getBool("isMusic", false);
        _isNotAffectedByMute = set.getBool("isNotAffectedByMute", false);
        _flyingTransformUsage = set.getBool("flyingTransformUsage", false);
        _refId = set.getInteger("referenceId", 0);
        _refConsume = set.getInteger("referenceConsume", 0);
        _isCleanse = set.getBool("isCleanse", false);
        _isCancel = set.getBool("isCancel", false);
        _isNewbie = set.getBool("isNewbie", false);
        isEnemy = set.getBool("isEnemy", false); // затычка на время

        _isTrigger = set.getBool("isTrigger", false);
        _isStaticHeal = set.getBool("isStaticHeal", false);

        if (Util.isNumber(set.getString("element", "NONE")))
            _element = Element.getElementById(set.getInteger("element", 6));
        else
            _element = Element.valueOf(set.getString("element", "NONE").toUpperCase());

        _elementPower = set.getInteger("elementPower", 0);

        _isCancelable = set.getBool("cancelable", true);
        _is_alt_cancel = set.getBool("alt_cancel", true);
        _isShieldignore = set.getBool("shieldignore", false);
        _criticalRate = set.getInteger("criticalRate", 0);
        _isOverhit = set.getBool("overHit", false);
        _weaponsAllowed = set.getInteger("weaponsAllowed", 0);
        _minPledgeClass = set.getInteger("minPledgeClass", 0);
        _minRank = set.getInteger("minRank", 0);
        _isOffensive = set.getBool("isOffensive", null);
        _isPvpSkill = set.getBool("isPvpSkill", null);
        _isPvm = set.getBool("isPvm", null);
        _isForceUse = set.getBool("isForceUse", false);
        _isBehind = set.getBool("behind", false);
        _symbolId = set.getInteger("symbolId", 0);
        _npcId = set.getInteger("npcId", 0);
        _flyType = FlyType.valueOf(set.getString("flyType", "NONE").toUpperCase());
        _flyToBack = set.getBool("flyToBack", false);
        _flyRadius = set.getInteger("flyRadius", 200);
        _negateSkill = set.getInteger("negateSkill", 0);
        _negatePower = set.getInteger("negatePower", Integer.MAX_VALUE);
        _numCharges = set.getInteger("num_charges", 0);
        _condCharges = set.getInteger("cond_charges", 0);
        _cancelTarget = set.getInteger("cancelTarget", 0);
        _skillInterrupt = set.getBool("skillInterrupt", false);
        _lethal1 = set.getDouble("lethal1", 0);
        _lethal2 = set.getDouble("lethal2", 0);
        _absorbPart = set.getFloat("absorbPart", 0.f);
        _absorbPartStatic = set.getInteger("absorbPartStatic", 0);
        _hideStartMessage = set.getBool("isHideStartMessage", false);
        _hideStopMessage = set.getBool("isHideStopMessage", false);
        _offlineTime = set.getBool("offlineTime", false);
        _energyConsume = set.getInteger("energyConsume", 0);
        _delayedEffect = set.getInteger("delayedEffect", 0);
        _delayedEffectLevel = set.getInteger("delayedEffectLevel", 1);

        StringTokenizer st = new StringTokenizer(set.getString("addSkills", ""), ";");
        while (st.hasMoreTokens()) {
            int id = Integer.valueOf(st.nextToken());
            int level = Integer.valueOf(st.nextToken());
            if (level == -1)
                level = _level;
            _addedSkills = (AddedSkill[]) Util.addElementToArray(_addedSkills, new AddedSkill(id, level), AddedSkill.class);
        }

        String canLearn = set.getString("canLearn", null);
        if (canLearn == null)
            _canLearn = null;
        else {
            _canLearn = new GArray<ClassId>();
            st = new StringTokenizer(canLearn, " \r\n\t,;");
            while (st.hasMoreTokens()) {
                String cls = st.nextToken();
                try {
                    _canLearn.add(ClassId.valueOf(cls));
                } catch (Throwable t) {
                    _log.log(Level.SEVERE, "Bad class " + cls + " to learn skill", t);
                }
            }
        }

        String teachers = set.getString("teachers", null);
        if (teachers == null)
            _teachers = null;
        else {
            _teachers = new GArray<Integer>();
            st = new StringTokenizer(teachers, " \r\n\t,;");
            while (st.hasMoreTokens()) {
                String npcid = st.nextToken();
                try {
                    _teachers.add(Integer.parseInt(npcid));
                } catch (Throwable t) {
                    _log.log(Level.SEVERE, "Bad teacher id " + npcid + " to teach skill", t);
                }
            }
        }
        if (getId() == 42 || getId() == 321 || getId() == 369 || getId() == 409 || getId() == 628 || getId() == 692 || getId() == 1231 || getId() == 1234 || getId() == 22099 || getId() == 22100 || getId() == 3632 || getId() == 5084 || getId() == 5937 || getId() == 1554 || getId() == 1555 || getId() == 2246 || getId() == 2247 || getId() == 5602 || getId() == 5656)
            _applyOnCaster = true;
        else if (getId() == 368 || getId() == 405 || getId() == 450 || getId() == 985 || getId() == 1400)
            _applyOnCaster = true;
    }

    public final boolean getWeaponDependancy(L2Character activeChar) {
        if (_weaponsAllowed == 0)
            return true;

        if (activeChar.getActiveWeaponInstance() != null && activeChar.getActiveWeaponItem() != null)
            if ((activeChar.getActiveWeaponItem().getItemType().mask() & _weaponsAllowed) != 0)
                return true;

        if (activeChar.getSecondaryWeaponInstance() != null && activeChar.getSecondaryWeaponItem() != null)
            if ((activeChar.getSecondaryWeaponItem().getItemType().mask() & _weaponsAllowed) != 0)
                return true;

        if (isActive()) {
            StringBuffer skillmsg = new StringBuffer();
            skillmsg.append(_name);
            skillmsg.append(" can only be used with weapons of type ");
            for (WeaponType wt : WeaponType.values())
                if ((wt.mask() & _weaponsAllowed) != 0)
                    skillmsg.append(wt).append('/');
            skillmsg.setCharAt(skillmsg.length() - 1, '.');
            activeChar.sendMessage(skillmsg.toString());
        }

        return false;
    }

    public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first) {
        final L2Player player = activeChar.getPlayer();
        //if(player != null)
        //	Util.test();

        if (activeChar.isDead() || activeChar.getReflection().getId() == -3)
            return false;
        else if (target != null && (activeChar.getReflection() != target.getReflection() || target != activeChar && target.isInvisible() && getId() != SKILL_DETECTION)) {
            activeChar.sendPacket(Msg.CANNOT_SEE_TARGET());
            //activeChar.sendMessage("CANNOT_SEE_TARGET() 15");
            return false;
        } else if (first) {
            if (!getWeaponDependancy(activeChar))
                return false;
            else if (activeChar.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? _id * 65536L + _level : _id)) {
                activeChar.sendReuseMessage(this);
                return false;
            } else if (isMusic()) {
                double mpConsume2 = activeChar.calcStat(Stats.MP_DANCE_SKILL_CONSUME, getMpConsume2(), target, this);
                mpConsume2 += activeChar.getEffectList().getActiveMusicCount(0) * mpConsume2 / 2 + getMpConsume1();
                if (activeChar.getCurrentMp() < mpConsume2) {
                    activeChar.sendPacket(Msg.NOT_ENOUGH_MP);
                    return false;
                }
            } else if (activeChar.getCurrentMp() < (isMagic() ? (getMpConsume1() + activeChar.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, getMpConsume2(), target, this)) : (getMpConsume1() + activeChar.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, getMpConsume2(), target, this)))) {
                activeChar.sendPacket(Msg.NOT_ENOUGH_MP);
                return false;
            }
        }
        if (activeChar.getCurrentHp() < getHpConsume() + 1) {
            activeChar.sendPacket(Msg.NOT_ENOUGH_HP);
            return false;
        } else if (!_isItemHandler && activeChar.isMuted(this))
            return false;
        else if (player != null) {
            if (!isOlympiadEnabled() && player.isInOlympiadMode()) {
                player.sendPacket(Msg.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
                return false;
            } else if (player.isInFlyingTransform() && _isItemHandler && _skillType != SkillType.RECALL && !flyingTransformUsage()) {
                player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(getItemConsumeId()[0]));
                return false;
            } else if (player.isInVehicle()) {
                L2Vehicle vehicle = player.getVehicle();
                // На воздушных кораблях можно использовать скилы-хэндлеры всем кроме капитана
                if (vehicle.isAirShip() && (!_isItemHandler || ((L2AirShip) vehicle).getDriver() == player))
                    return false;
                    // С морских кораблей можно ловить рыбу
                else if (vehicle instanceof L2Ship && !(this instanceof Fishing || this instanceof ReelingPumping))
                    return false;
            }
            if (player.inObserverMode()) {
                activeChar.sendPacket(Msg.OBSERVERS_CANNOT_PARTICIPATE);
                return false;
            } else if (first && _itemConsume[0] > 0)
                for (int i = 0; i < _itemConsume.length; i++) {
                    Inventory inv = ((L2Playable) activeChar).getInventory();
                    if (inv == null)
                        inv = player.getInventory();
                    L2ItemInstance requiredItems = inv.getItemByItemId(_itemConsumeId[i]);
                    if (requiredItems == null || requiredItems.getCount() < _itemConsume[i]) {
                        if (activeChar == player) {
                            player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
                            if (player.isGM())
                                player.sendMessage("item: " + _itemConsumeId[i] + " count: " + _itemConsume[i] + " player_count: " + (requiredItems == null ? 0 : requiredItems.getCount()));
                        }
                        return false;
                    }
                }
            if (player.isFishing() && _id != 1312 && _id != 1313 && _id != 1314) {
                if (activeChar == player)
                    player.sendPacket(Msg.ONLY_FISHING_SKILLS_ARE_AVAILABLE);
                return false;
            }
        }
        if (activeChar.is_block_move() && (getFlyType() == FlyType.CHARGE || getFlyType() == FlyType.THROW_HORIZONTAL || getFlyType() == FlyType.THROW_UP)) {
            if (player != null)
                player.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
            return false;
        } else if (getFlyType() == FlyType.CHARGE && (activeChar.isFlying() ? !GeoEngine.canAttacTarget(activeChar, target, activeChar.isFlying()) : !GeoEngine.canMoveToCoord(activeChar.getX(), activeChar.getY(), activeChar.getZ(), target.getX(), target.getY(), target.getZ(), activeChar.getReflection().getGeoIndex()))) {
            if (player != null)
                player.sendPacket(Msg.CANNOT_SEE_TARGET());
            return false;
        }
        // Fly скиллы нельзя использовать слишком близко
        else if (first && target != null && getFlyType() == FlyType.CHARGE && activeChar.isInRange(target.getLoc(), Math.min(200, getFlyRadius()))) {
            if (player != null)
                player.sendPacket(Msg.THERE_IS_NOT_ENOUGH_SPACE_TO_MOVE_THE_SKILL_CANNOT_BE_USED);
            return false;
        }

        SystemMessage msg = checkTarget(activeChar, target, target, forceUse, first);
        if (msg != null && player != null) {
            player.sendPacket(msg);
            return false;
        }

        int condResult = condition(activeChar, target, first);
        if (condResult == -1)
            return false;
        else if (condResult == 0) {
            if (player != null && player.getEventMaster() != null)
                return player.getEventMaster().canUseSkill(player, this);
        } else if (first) {
            if (activeChar.getIncreasedForce() < _condCharges || activeChar.getIncreasedForce() < _numCharges) {
                activeChar.sendPacket(Msg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
                return false;
            } else if (getSoulsConsume() > activeChar.getConsumedSouls()) {
                activeChar.sendPacket(Msg.THERE_IS_NOT_ENOUGHT_SOUL);
                return false;
            } else if (getEnergyConsume() > activeChar.getAgathionEnergy()) {
                activeChar.sendPacket(Msg.THE_SKILL_HAS_BEEN_CANCELED_BECAUSE_YOU_HAVE_INSUFFICIENT_SOUL_AVATAR_ENERGY);
                return false;
            }
            if (getNumCharges() > 0)
                activeChar.setIncreasedForce(activeChar.getIncreasedForce() - getNumCharges());
            if (isSoulBoost())
                activeChar.setConsumedSouls(activeChar.getConsumedSouls() - Math.min(activeChar.getConsumedSouls(), 5), null);
            else if (getSoulsConsume() > 0)
                activeChar.setConsumedSouls(activeChar.getConsumedSouls() - getSoulsConsume(), null);

            if (getEnergyConsume() > 0)
                activeChar.setAgathionEnergy(activeChar.getAgathionEnergy() - getEnergyConsume());
        }
        if (player != null && player.getEventMaster() != null)
            return player.getEventMaster().canUseSkill(player, this);
        return true;
    }

    public int condition(L2Character activeChar, L2Character target, boolean first) {
        if (_preCondition == null || _preCondition.length == 0) {
            if (first) {
                if (activeChar.getIncreasedForce() < _condCharges || activeChar.getIncreasedForce() < _numCharges) {
                    activeChar.sendPacket(Msg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
                    return -1; // false
                }

                if (getSoulsConsume() > activeChar.getConsumedSouls()) {
                    activeChar.sendPacket(Msg.THERE_IS_NOT_ENOUGHT_SOUL);
                    return -1;
                }

                if (getNumCharges() > 0)
                    activeChar.setIncreasedForce(activeChar.getIncreasedForce() - getNumCharges());

                if (isSoulBoost())
                    activeChar.setConsumedSouls(activeChar.getConsumedSouls() - Math.min(activeChar.getConsumedSouls(), 5), null);
                else if (getSoulsConsume() > 0)
                    activeChar.setConsumedSouls(activeChar.getConsumedSouls() - getSoulsConsume(), null);
            }
            return 0; // true
        }

        Env env = new Env();
        env.character = activeChar;
        env.skill = this;
        env.target = target;

        if (first)
            for (Condition с : _preCondition)
                if (с != null && !с.test(env)) {
                    SystemMessage cond_msg = с.getSystemMsg();
                    if (cond_msg != null) {
                        cond_msg.args.clear();
                        activeChar.sendPacket(cond_msg.addSkillName(this.getId(), this.getLevel()));
                    }
                    return -1; // false
                }
        return 1; // ---
    }

    public SystemMessage checkTarget(L2Character activeChar, L2Character target, L2Character aimingTarget, boolean forceUse, boolean first) {
        return checkTarget(activeChar, target, aimingTarget, forceUse, first, false);
    }

    public SystemMessage checkTarget(L2Character activeChar, L2Character target, L2Character aimingTarget, boolean forceUse, boolean first, boolean AoE) {
        if (target == activeChar && isNotTargetAoE(activeChar) || target == activeChar.getPet() && (_targetType == SkillTargetType.TARGET_PET_AURA || _targetType == SkillTargetType.TARGET_AREA && forceUse && isEnemy && !AoE))
            return null;
        else if (getId() == 6688 && !target.isMonster())
            return Msg.INVALID_TARGET();
		/*else if(getCastRange() < Integer.MAX_VALUE && !GeoEngine.canAttacTarget(activeChar, target, activeChar.isFlying()))
		{
			//activeChar.sendMessage("CANNOT_SEE_TARGET() 14");
			return Msg.CANNOT_SEE_TARGET();
		}*/
        else if (target == null || isOffensive() && target == activeChar)
            return Msg.THAT_IS_THE_INCORRECT_TARGET();
        else if (activeChar.getReflection() != target.getReflection()) // Массовые атаки должны попадать по дагерам в Hide. Если потребуется убрать - раскомментировать. || target != activeChar && target.isInvisible() && getId() != SKILL_DETECTION)
        {
            //activeChar.sendMessage("CANNOT_SEE_TARGET() 13");
            return Msg.CANNOT_SEE_TARGET();
        }
        // Попадает ли цель в радиус действия в конце каста
        //else if(!first && target != activeChar && target == aimingTarget && getCastRange() > 0 && !activeChar.isInRange(target.getLoc(), getCastRange() + (getCastRange() < 200 ? 400 : 500)))
        //	return Msg.YOUR_TARGET_IS_OUT_OF_RANGE;
        // Для этих скиллов дальнейшие проверки не нужны
        else if (_skillType == SkillType.TAKECASTLE || _skillType == SkillType.TAKEFORTRESS || _skillType == SkillType.TAKEFLAG)
            return null;
            // Конусообразные скиллы
        else if (!first && target != activeChar && affect_scope == AffectScope.fan && !Util.isFacing(activeChar, target, fan_range_l))
            //else if(!first && target != activeChar && (_targetType == SkillTargetType.TARGET_MULTIFACE || _targetType == SkillTargetType.TARGET_MULTIFACE_AURA) && (_isBehind ? Util.isFacing(activeChar, target, 120) : !Util.isFacing(activeChar, target, 90)))
            return Msg.YOUR_TARGET_IS_OUT_OF_RANGE;
            // Проверка на каст по трупу
        else if (_isUndeadOnly && !target.isUndead() || (target.isDead() && isOffensive() && _targetType != SkillTargetType.TARGET_AREA_AIM_CORPSE && _targetType != SkillTargetType.TARGET_CORPSE && _targetType != SkillTargetType.TARGET_CORPSE_PLAYER && _skillType != SkillType.SWEEP && _skillType != SkillType.RESURRECT))
            return Msg.INVALID_TARGET();
        else if (target.isMonster() && ((L2MonsterInstance) target).isDying())
            return Msg.INVALID_TARGET();
            // Нельзя юзать дебафы и масс-скиллы на НПЦ и гвардов (но не осадных).
            // Re: Можно юзать масс скилы на НПС)
        else if ((target instanceof L2NpcInstance && !target.isMonster()) && !target.isSiegeGuard() && (_skillType == SkillType.DEBUFF))
            return Msg.INVALID_TARGET();
        else if (_targetType != SkillTargetType.TARGET_UNLOCKABLE && target.isDoor() && !((L2DoorInstance) target).isAttackable(activeChar))
            return Msg.INVALID_TARGET();
            // Для различных бутылок, и для скилла кормления, дальнейшие проверки не нужны
        else if (_skillType == SkillType.BEAST_FEED || _targetType == SkillTargetType.TARGET_UNLOCKABLE || _targetType == SkillTargetType.TARGET_CHEST)
            return null;
        else if (activeChar.isPlayable() || activeChar.getPlayer() != null) {
            L2Player player = activeChar.getPlayer();
            if (player == null)
                return Msg.THAT_IS_THE_INCORRECT_TARGET();
            else if (forceUse && player == target.getPlayer() && (target.isSummon() || target.isPet()) && !isOffensive())
                return null;
                // Запрет на атаку мирных NPC в осадной зоне на TW. Иначе таким способом набивают очки.
            else if (player.getTerritorySiege() > -1 && target.isNpc() && !(target instanceof L2TerritoryFlagInstance) && !(target.getAI() instanceof DefaultAI) && player.isInZone(ZoneType.Siege))
                return Msg.INVALID_TARGET();
            else if (target.isPlayable()) {
                if (isPvM())
                    return Msg.THAT_IS_THE_INCORRECT_TARGET();
                L2Player pcTarget = target.getPlayer();
                if (pcTarget == null)
                    return Msg.THAT_IS_THE_INCORRECT_TARGET();
                else if (player.isInZone(ZoneType.epic) != pcTarget.isInZone(ZoneType.epic))
                    return Msg.THAT_IS_THE_INCORRECT_TARGET();
                else if (pcTarget.isInOlympiadMode() && (!player.isInOlympiadMode() || player.getOlympiadGame() != pcTarget.getOlympiadGame())) // На всякий случай
                    return Msg.THAT_IS_THE_INCORRECT_TARGET();
                else if (player.getBlockCheckerArena() > -1 && pcTarget.getBlockCheckerArena() > -1 && _targetType == SkillTargetType.TARGET_EVENT)
                    return null;
                else if (player.getTeam() > 0 && player.isChecksForTeam() > 0 && pcTarget.getTeam() == 0) // Запрет на атаку/баф участником эвента незарегистрированного игрока
                    return Msg.THAT_IS_THE_INCORRECT_TARGET();
                else if (pcTarget.getTeam() > 0 && pcTarget.isChecksForTeam() > 0 && player.getTeam() == 0) // Запрет на атаку/баф участника эвента незарегистрированным игроком
                    return Msg.THAT_IS_THE_INCORRECT_TARGET();
                else if (!isOffensive() && player.getTeam() != pcTarget.getTeam() && player.isChecksForTeam() > 0 && (ConfigValue.ChecksForTeam || player.getEventMaster() != null && !player.getEventMaster().buffAnotherTeam())) // Запрет на баф участником эвента игрока из противоположной команды.
                    return Msg.THAT_IS_THE_INCORRECT_TARGET();
                else if (isOffensive()) {
                    if (player.getObjectId() == target.getPlayer().getObjectId() && ((activeChar.isSummon() || activeChar.isPet()) || (target.isSummon() || target.isPet()) && (!forceUse || _skillType == SkillType.DEBUFF)))
                        return Msg.INVALID_TARGET();
                    else if (player.isInOlympiadMode() && !player.isOlympiadCompStart()) // Бой еще не начался
                        return Msg.INVALID_TARGET();
                    else if (player.isInOlympiadMode() && player.isOlympiadCompStart() && player.getOlympiadSide() == pcTarget.getOlympiadSide() && !forceUse) // Свою команду атаковать нельзя
                        return Msg.THAT_IS_THE_INCORRECT_TARGET();
                    else if (player.getTeam() > 0 && player.isChecksForTeam() > 0 && pcTarget.getTeam() > 0 && pcTarget.isChecksForTeam() > 0 && player.getTeam() == pcTarget.getTeam() && (pcTarget.isChecksForTeam() > 1 || !forceUse)) // Свою команду атаковать нельзя
                        return Msg.THAT_IS_THE_INCORRECT_TARGET();
                    else if (/*first && */isAoE() && getCastRange() < Integer.MAX_VALUE && !GeoEngine.canAttacTarget(activeChar, target, activeChar.isFlying())) {
                        //activeChar.sendMessage("CANNOT_SEE_TARGET() 12");
                        return Msg.CANNOT_SEE_TARGET();
                    } else if (activeChar.isInZoneBattle() != target.isInZoneBattle() && !player.getPlayerAccess().PeaceAttack)
                        return Msg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;
                    else if ((activeChar.isInZonePeace() || target.isInZonePeace()) && !player.getPlayerAccess().PeaceAttack)
                        return Msg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;
                    else if (activeChar.isInZoneBattle()) {
                        L2Zone set_fame = player.getZone(ZoneType.set_fame);
                        if (set_fame != null && pcTarget.getZone(ZoneType.set_fame) != null && set_fame._no_attack_time > 0 && (player.getOnlineTime() < set_fame._no_attack_time || pcTarget.getOnlineTime() < set_fame._no_attack_time))
                            return Msg.INVALID_TARGET();
                        else if (set_fame != null && !set_fame._batle && !isForceUse() && player.getClanId() != 0 && player.getClanId() == pcTarget.getClanId())
                            return Msg.INVALID_TARGET();
                            // АоЕ скилы не проходят на членов пати и их питомцев, за исключением таргет АоЕ, при условии, что член пати или его пет в таргете.
                        else if (player.getParty() != null && player.getParty() == pcTarget.getParty() && !isForceUse() && getTargetType() != SkillTargetType.TARGET_PARTY && (!forceUse || _skillType == SkillType.DEBUFF || isAoE() && (!isEnemy || AoE)))
                            return Msg.INVALID_TARGET();
                        return null; // Остальные условия на аренах и на олимпиаде проверять не требуется
                    }

                    // Только враг и только если он еше не проиграл.
                    Duel duel1 = player.getDuel();
                    Duel duel2 = pcTarget.getDuel();
                    if (player != pcTarget && duel1 != null && duel1 == duel2) {
                        if (duel1.getTeamForPlayer(pcTarget) == duel1.getTeamForPlayer(player))
                            return Msg.INVALID_TARGET();
                        else if (duel1.getDuelState(player) != Duel.DuelState.Fighting)
                            return Msg.INVALID_TARGET();
                        else if (duel1.getDuelState(pcTarget) != Duel.DuelState.Fighting)
                            return Msg.INVALID_TARGET();
                        return null;
                    } else if (isPvpSkill() || !forceUse || isAoE()) {
                        if (player == pcTarget)
                            return Msg.INVALID_TARGET();
                        else if (player.getParty() != null && player.getParty() == pcTarget.getParty() && getTargetType() != SkillTargetType.TARGET_PARTY/* && !AoE*//*isAoE()*/)
                            return Msg.INVALID_TARGET();
                        else if (player.getClanId() != 0 && player.getClanId() == pcTarget.getClanId())
                            return Msg.INVALID_TARGET();
                        else if (player.getDuel() != null && pcTarget.getDuel() != player.getDuel())
                            return Msg.INVALID_TARGET();
                        else if (!forceUse && player.getClan() != null && pcTarget.getClan() != null && player.getClan().getAllyId() != 0 && pcTarget.getClan().getAllyId() != 0 && player.getClan().getAllyId() == pcTarget.getClan().getAllyId())
                            return Msg.INVALID_TARGET();
                    }
                    if (activeChar.isInZone(ZoneType.Siege) && target.isInZone(ZoneType.Siege)) {
                        if (player.getTerritorySiege() > -1 && player.getTerritorySiege() == pcTarget.getTerritorySiege())
                            return Msg.INVALID_TARGET();
                        L2Clan clan1 = player.getClan();
                        L2Clan clan2 = pcTarget.getClan();
                        if (clan1 == null || clan2 == null)
                            return null;
                        else if (clan1.getSiege() == null || clan2.getSiege() == null)
                            return null;
                        else if (clan1.getSiege() != clan2.getSiege())
                            return null;
                        else if (clan1.isDefender() && clan2.isDefender())
                            return Msg.INVALID_TARGET();
                        else if (clan1.getSiege().isMidVictory())
                            return null;
                        else if (clan1.isAttacker() && clan2.isAttacker())
                            return Msg.INVALID_TARGET();
                        return null;
                    } else if (player.atMutualWarWith(pcTarget) || player.isFactionWar(pcTarget))
                        return null;
                    else if (isForceUse())
                        return null;
                        // Drizzy: убрал, по тестам на оффе если рядом флагнутый и мы юзаем массуху то чар флагаеться. Защита от развода на флаг с копьем
					/*else if(!forceUse && player.getPvpFlag() == 0 && pcTarget.getPvpFlag() != 0 && aimingTarget != target)
						return Msg.INVALID_TARGET();*/
                    else if (pcTarget.getPvpFlag() != 0)
                        return null;
                    else if (pcTarget.getKarma() > 0)
                        return null;
                    else if (forceUse && !isPvpSkill() && (!isAoE() || aimingTarget == target))
                        return null;
                    return Msg.INVALID_TARGET();
                } else if (pcTarget == player)
                    return null;
                else if (player.isInOlympiadMode() && !forceUse && player.getOlympiadSide() != pcTarget.getOlympiadSide()) // Чужой команде помогать нельзя
                    return Msg.THAT_IS_THE_INCORRECT_TARGET();
                    //if(player.getTeam() > 0 && player.isChecksForTeam() > 0 && pcTarget.getTeam() > 0 && pcTarget.isChecksForTeam() > 0 && player.getTeam() != pcTarget.getTeam()) // Чужой команде помогать нельзя
                    //	return Msg.THAT_IS_THE_INCORRECT_TARGET();

                else if (!activeChar.isInZoneBattle() && target.isInZoneBattle())
                    return Msg.INVALID_TARGET();
                else if (activeChar.isInZonePeace() && !target.isInZonePeace() && _skillType != SkillType.CALL)
                    return Msg.INVALID_TARGET();
                else if (forceUse || isForceUse())
                    return null;
                else if (player.getDuel() != null && pcTarget.getDuel() != player.getDuel())
                    return Msg.INVALID_TARGET();
                else if (player != pcTarget && player.getDuel() != null && pcTarget.getDuel() != null && pcTarget.getDuel() == pcTarget.getDuel())
                    return Msg.INVALID_TARGET();
                else if (player.getParty() != null && player.getParty() == pcTarget.getParty())
                    return null;
                else if (player.getClanId() != 0 && player.getClanId() == pcTarget.getClanId())
                    return null;
                else if (player.atMutualWarWith(pcTarget) || player.isFactionWar(pcTarget))
                    return Msg.INVALID_TARGET();
                else if (pcTarget.getPvpFlag() != 0)
                    return Msg.INVALID_TARGET();
                else if (pcTarget.getKarma() > 0)
                    return Msg.INVALID_TARGET();
                if (!forceUse && !isOffensive() && target.isAutoAttackable(activeChar))
                    return Msg.INVALID_TARGET();
                return null;
            }
        } else if (activeChar.isInZonePeace() && isAoE() && isOffensive())
            return Msg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;
        else if (/*first && */isAoE() && isOffensive() && getCastRange() < Integer.MAX_VALUE && !GeoEngine.canAttacTarget(activeChar, target, activeChar.isFlying())) {
            //activeChar.sendMessage("CANNOT_SEE_TARGET() 11");
            return Msg.CANNOT_SEE_TARGET();
        }
        boolean isAutoAttackable = target.isAutoAttackable(activeChar);
        if (!forceUse && !isForceUse() && !isOffensive() && isAutoAttackable)
            return Msg.INVALID_TARGET();
        else if (!forceUse && !isForceUse() && isOffensive() && !isAutoAttackable)
            return Msg.INVALID_TARGET();
        else if (!target.isAttackable(activeChar))
            return Msg.INVALID_TARGET();
            // чаин хилл не может хилить противников при любом раскладе...
        else if (isAutoAttackable && _skillType == SkillType.CHAIN_HEAL || getId() == 1505)
            return Msg.INVALID_TARGET();
        return null;
    }

    public final L2Character getAimingTarget(L2Character activeChar, L2Object obj) {
        if (ConfigValue.EnableSkillTargetTest) {
            lock.lock();
            try {
                return (L2Character) target_type.getTarget(obj, activeChar, false);
            } finally {
                lock.unlock();
            }
        }

        L2Character target = obj == null || !obj.isCharacter() ? null : (L2Character) obj;
        lock.lock();
        try {
            switch (_targetType) {
                case TARGET_ALLY:
                case TARGET_CLAN:
                case TARGET_PARTY:
                case TARGET_CLAN_ONLY:
                case TARGET_COMMAND_CHANEL:
                case TARGET_SELF:
                case TARGET_AURA:
                case TARGET_MULTIFACE_AURA:
                case TARGET_TUNNEL_SELF:
                    return activeChar;
                case TARGET_HOLY:
                    return target != null && activeChar.isPlayer() && target.isArtefact() ? target : null;
                case TARGET_FLAGPOLE:
                    return activeChar.isPlayer() && target instanceof L2StaticObjectInstance && ((L2StaticObjectInstance) target).getType() == 3 ? target : null;
                case TARGET_UNLOCKABLE:
                    return target != null && target.isDoor() || target instanceof L2ChestInstance ? target : null;
                case TARGET_CHEST:
                    return target instanceof L2ChestInstance ? target : null;
                case TARGET_SERVITOR: //Пет или самон.
                    if (activeChar.isSummon() || activeChar.isPet())
                        target = activeChar.getPlayer();
                    else
                        return null;
                    return target != null && target.isDead() == _isCorpse ? target : null;
                case TARGET_PET:
                case TARGET_PET_AURA:
                    target = activeChar.getPet();
                    return target != null && target.isDead() == _isCorpse ? target : null;
                case TARGET_OWNER:
                    if (activeChar.isSummon())
                        target = activeChar.getPlayer();
                    else
                        return null;
                    return target != null && target.isDead() == _isCorpse ? target : null;
                case TARGET_OWNER_PET:
                    if (activeChar.isPet())
                        target = activeChar.getPlayer();
                    else
                        return null;
                    return target;
                case TARGET_ENEMY_PET:
                    if (target == null || target == activeChar.getPet() || !target.isPet())
                        return null;
                    return target;
                case TARGET_ENEMY_SUMMON:
                    if (target == null || target == activeChar.getPet() || !target.isSummon())
                        return null;
                    return target;
                case TARGET_ENEMY_SERVITOR:
                    if (target == null || target == activeChar.getPet() || !(target instanceof L2Summon))
                        return null;
                    return target;
                case TARGET_EVENT:
                    return target != null && !target.isDead() && target.getPlayer() != null && target.getPlayer().getBlockCheckerArena() > -1 ? target : null;
                case TARGET_ONE:
                    if (activeChar.p_party_buff.get() && !isOffensive())
                        return activeChar;
                    return target != null && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead()) || (target != null && target.isDead() && !isOffensive()) ? target : null;
                case TARGET_ONE_PARTY:
                    return target != null && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead()) || (target != null && target.isDead() && !isOffensive()) ? target : null;
                case TARGET_PARTY_ONE:
                    if (target == null)
                        return null;
                    L2Player player = activeChar.getPlayer();
                    L2Player ptarget = target.getPlayer();
                    if (getId() == 1258 && target instanceof SeducedInvestigatorInstance) //hardcore for 726\727 quest.
                        return target;
                        // self or self pet.
                    else if (ptarget != null && ptarget.equals(activeChar))
                        return target;
                        // olympiad party member or olympiad party member pet.
                    else if (player != null && player.isInOlympiadMode() && ptarget != null && player.getOlympiadSide() == ptarget.getOlympiadSide() && player.getOlympiadGame() == ptarget.getOlympiadGame() && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead()))
                        return target;
                        // party member or party member pet.
                    else if (ptarget != null && player != null && player.getParty() != null && player.getParty().containsMember(ptarget) && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead()))
                        return target;
                    return null;
                case TARGET_AREA:
                case TARGET_MULTIFACE:
                case TARGET_TUNNEL:
                    return target != null && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead()) ? target : null;
                case TARGET_AREA_AIM_CORPSE:
                    return target != null && target.isDead() && target.isNpc() ? target : null;
                case TARGET_CORPSE:
                    if (target != null && target.isNpc() && target.isDead() || target != null && target.isSummon() && target.isDead())
                        return target;
                    return null;
                case TARGET_CORPSE_PLAYER:
                    return target != null && target.isPlayable() && target.isDead() ? target : null;
                case TARGET_SIEGE:
                    return target != null && !target.isDead() && (target.isDoor() || target instanceof L2ControlTowerInstance) ? target : null;
                default:
                    activeChar.sendMessage("Target type of skill is not currently handled");
                    return null;
            }
        } finally {
            lock.unlock();
        }
    }

    public GArray<L2Character> getTargets(L2Character activeChar, L2Character aimingTarget, boolean forceUse) {
        if (ConfigValue.EnableSkillTargetTest) {
            GArray<L2Character> targets = new GArray<L2Character>();
            targets.addAll(affect_scope.getTargetList(activeChar, aimingTarget, this));
            return targets;
        }


        GArray<L2Character> targets = new GArray<L2Character>();
        if (oneTarget(activeChar)) {
            if (aimingTarget != null)
                targets.add(aimingTarget);
            return targets;
        }

        switch (_targetType) {
            case TARGET_ONE_PARTY: {
                if (aimingTarget != null && !aimingTarget.isDead()) {
                    if (aimingTarget.getParty() != null) {
                        for (L2Player member : aimingTarget.getParty().getPartyMembers())
                            if (member != null && !member.isDead() && activeChar.isInRangeZ(member, getAffectRange()))
                                targets.add(member);
                    } else
                        targets.add(aimingTarget);
                }
                break;
            }
            case TARGET_EVENT: {
                if (activeChar.isPlayer()) {
                    L2Player player = activeChar.getPlayer();
                    int playerArena = player.getBlockCheckerArena();

                    if (playerArena != -1) {
                        HandysBlockCheckerManager.ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(playerArena);
                        int team = holder.getPlayerTeam(player);
                        // Aura attack
                        for (L2Player actor : L2World.getAroundPlayers(activeChar, 250, 100))
                            if (holder.getPlayers().contains(actor) && holder.getPlayerTeam(actor) != team)
                                targets.add(actor);
                    }
                }
                break;
            }
            case TARGET_AREA_AIM_CORPSE:
            case TARGET_AREA:
            case TARGET_MULTIFACE:
            case TARGET_TUNNEL: {
                if (aimingTarget != null && aimingTarget.isDead() == _isCorpse && (!_isUndeadOnly || aimingTarget.isUndead()) && (!isEnemy || activeChar.getPlayer() != aimingTarget.getPlayer()))
                    targets.add(aimingTarget);
                addTargetsToList(targets, aimingTarget, activeChar, forceUse);
                break;
            }
            case TARGET_AURA:
            case TARGET_MULTIFACE_AURA:
            case TARGET_TUNNEL_SELF: {
                addTargetsToList(targets, activeChar, activeChar, forceUse);
                break;
            }
            case TARGET_PET_AURA: {
                if (activeChar.getPet() == null)
                    break;
                addTargetsToList(targets, activeChar.getPet(), activeChar, forceUse);
                break;
            }
            case TARGET_PARTY:
            case TARGET_CLAN:
            case TARGET_CLAN_ONLY:
            case TARGET_COMMAND_CHANEL:
            case TARGET_ALLY:
            case TARGET_ONE: // это здесь у нас для p_party_buff эффекта
            {
                if (activeChar.isMonster() || activeChar.isSiegeGuard()) {
                    targets.add(activeChar);
                    for (L2Character c : L2World.getAroundCharacters(activeChar, getAffectRange(), 128))
                        if (!c.isDead() && (c.isMonster() || c.isSiegeGuard()) /*&& ((L2MonsterInstance) c).getFactionId().equals(mob.getFactionId())*/)
                            targets.add(c);
                    break;
                }
                L2Player player = activeChar.getPlayer();
                if (player == null) {
                    if (activeChar.isPet() || activeChar.isSummon())
                        break;
                    System.out.println("L2Skill.getTargets | player = null | activeChar = " + activeChar + "[" + activeChar.getNpcId() + "] | SkillID: " + getId());
                    Thread.dumpStack();
                    break;
                }
                if (player.isInOlympiadMode()) {
                    addOlympiadTargetsToList(targets, player);
                    //addTargetAndPetToList(targets, player, player);
                    break;
                }
                int range = _targetType == SkillTargetType.TARGET_ONE ? getCastRange() : getAffectRange();
                for (L2Player target : L2World.getAroundPlayers(player, range, 128)) {
                    boolean check = false;
                    switch (_targetType) {
                        case TARGET_PARTY:
                        case TARGET_ONE:
                            check = player.getParty() != null && player.getParty() == target.getParty();
                            break;
                        case TARGET_CLAN:
                            check = player.getClanId() != 0 && target.getClanId() == player.getClanId() || player.getParty() != null && target.getParty() == player.getParty();
                            break;
                        case TARGET_CLAN_ONLY:
                            check = player.getClanId() != 0 && target.getClanId() == player.getClanId();
                            break;
                        case TARGET_COMMAND_CHANEL:
                            check = player.getParty() != null && target.getParty() != null && (player.getParty() == target.getParty() || player.getClanId() != 0 && target.getClanId() == player.getClanId() || player.getParty().getCommandChannel() != null && player.getParty().getCommandChannel() == target.getParty().getCommandChannel());
                            break;
                        case TARGET_ALLY:
                            check = player.getClanId() != 0 && target.getClanId() == player.getClanId() || player.getAllyId() != 0 && target.getAllyId() == player.getAllyId();
                            break;
                    }
                    if (!check)
                        continue;
                    if (checkTarget(player, target, aimingTarget, forceUse, false) != null)
                        continue;
                    addTargetAndPetToList(targets, player, target);
                }
                addTargetAndPetToList(targets, player, player);
                break;
            }
        }
        return targets;
    }

    private void addTargetAndPetToList(GArray<L2Character> targets, L2Player actor, L2Player target) {
        int range = _targetType == SkillTargetType.TARGET_ONE ? getCastRange() : getAffectRange();
        if ((actor == target || actor.isInRangeZ(target, range)) && target.isDead() == _isCorpse)
            targets.add(target);
        L2Summon pet = target.getPet();
        if (pet != null && actor.isInRangeZ(pet, range) && pet.isDead() == _isCorpse)
            targets.add(pet);
    }

    private void addOlympiadTargetsToList(GArray<L2Character> targets, L2Player player) {
        if (!_isCorpse)
            targets.add(player);
        L2Summon pet = player.getPet();
        if (pet != null && pet.isDead() == _isCorpse)
            targets.add(pet);
        for (L2Player target : L2World.getAroundPlayers(player, getAffectRange(), 128)) {
            if (player.getOlympiadSide() != target.getOlympiadSide()) // Чужой команде помогать нельзя
                continue;
            if (player.getOlympiadGame() != target.getOlympiadGame()) // Команде на чужой арене помогать нельзя
                continue;
            addTargetAndPetToList(targets, player, target);
        }
    }

    private void addTargetsToList(GArray<L2Character> targets, L2Character aimingTarget, L2Character activeChar, boolean forceUse) {
        if (aimingTarget != null) {
            int radius = getAffectRange();
            if (radius < 1 && fan_range_h > 0)
                radius = Math.max(fan_range_h, fan_range_l);
            L2Territory terr = null;
            if (_targetType == SkillTargetType.TARGET_TUNNEL) // square
            {
                radius = Math.max(fan_range_h, fan_range_l) + 50;
                terr = new L2Territory(0);

                int zmin1 = activeChar.getPrevZ() - 50;
                int zmax1 = activeChar.getPrevZ() + 50;
                int zmin2 = aimingTarget.getZ() - 50;
                int zmax2 = aimingTarget.getZ() + 50;

                //double angle = Location.calculateAngleFrom(activeChar, aimingTarget);
                double angle = Location.calculateAngleFrom(activeChar.getPrevX(), activeChar.getPrevY(), aimingTarget.getX(), aimingTarget.getY());

                double radian1 = Math.toRadians(angle - 90);
                double radian2 = Math.toRadians(angle + 90);

                int dx = aimingTarget.getX() - activeChar.getPrevX();
                int dy = aimingTarget.getY() - activeChar.getPrevY();

                int c_x = (int) (activeChar.getX() - Math.sin(radian1) * fan_range_h);
                int c_y = (int) (activeChar.getY() + Math.cos(radian1) * fan_range_h);

                // хз где я такое натестил, пздц...
                /**
                 * считает растояние fan_range_h от цели, до чара.
                 **/
				/*int c_x = aimingTarget.getX();
				int c_y = aimingTarget.getY();

				double distance = Math.sqrt(dx * dx + dy * dy);
				if(distance > fan_range_h)
				{
					double cut = fan_range_h / distance;
					c_x -= (int) Math.ceil(dx * cut);
					c_y -= (int) Math.ceil(dy * cut);
				}
				else
				{
					c_x = activeChar.getPrevX();
					c_y = activeChar.getPrevY();
				}*/

                int width = fan_range_l / 2; // _radius

                // fan_range_l - ширина
                // fan_range_h - дистанция до цели
                terr.add(c_x + (int) (Math.cos(radian1) * width), c_y + (int) (Math.sin(radian1) * width), zmin1, zmax1);
                terr.add(c_x + (int) (Math.cos(radian2) * width), c_y + (int) (Math.sin(radian2) * width), zmin1, zmax1);

                //terr.add(aimingTarget.getX() + (int) (Math.cos(radian2) * width), aimingTarget.getY() + (int) (Math.sin(radian2) * width), zmin2, zmax2);
                //terr.add(aimingTarget.getX() + (int) (Math.cos(radian1) * width), aimingTarget.getY() + (int) (Math.sin(radian1) * width), zmin2, zmax2);

                terr.add(activeChar.getX() + (int) (Math.cos(radian2) * width), activeChar.getY() + (int) (Math.sin(radian2) * width), zmin2, zmax2);
                terr.add(activeChar.getX() + (int) (Math.cos(radian1) * width), activeChar.getY() + (int) (Math.sin(radian1) * width), zmin2, zmax2);

                if (activeChar.isPlayer() && ((L2Player) activeChar).isGM()) {
                    activeChar.sendPacket(Functions.Points2Trace(terr.getCoords(), 50, true, false));
                    activeChar.sendPacket(Functions.Points2Trace(terr.getCoords(), 50, true, true));
                }
            } else if (_targetType == SkillTargetType.TARGET_TUNNEL_SELF) // square_pb
            {
                radius = Math.max(fan_range_h, fan_range_l) + 50;
                terr = new L2Territory(0);
                int width = fan_range_l / 2; // _radius

                int zmin1 = activeChar.getZ() - 50;
                int zmax1 = activeChar.getZ() + 50;

                double angle = Util.convertHeadingToDegree(activeChar.getHeading());

                angle += fan_range_s;
                if (angle >= 360)
                    angle -= 360;

                double radian1 = Math.toRadians(angle - 90);
                double radian2 = Math.toRadians(angle + 90);

                int c_x = (int) (activeChar.getX() - Math.sin(radian1) * fan_range_h);
                int c_y = (int) (activeChar.getY() + Math.cos(radian1) * fan_range_h);

                // fan_range_l - ширина
                // fan_range_h - дистанция до цели
                terr.add(c_x + (int) (Math.cos(radian1) * width), c_y + (int) (Math.sin(radian1) * width), zmin1, zmax1); // getPointInRadius(c_x_c_y, width, angle-90)
                terr.add(c_x + (int) (Math.cos(radian2) * width), c_y + (int) (Math.sin(radian2) * width), zmin1, zmax1); // getPointInRadius(c_x_c_y, width, angle+90)

                terr.add(activeChar.getX() + (int) (Math.cos(radian2) * width), activeChar.getY() + (int) (Math.sin(radian2) * width), zmin1, zmax1); // getPointInRadius(activeChar.getLoc(), width, angle+90)
                terr.add(activeChar.getX() + (int) (Math.cos(radian1) * width), activeChar.getY() + (int) (Math.sin(radian1) * width), zmin1, zmax1); // getPointInRadius(activeChar.getLoc(), width, angle-90)

                if (activeChar.isPlayer() && ((L2Player) activeChar).isGM()) {
                    activeChar.sendPacket(Functions.Points2Trace(terr.getCoords(), 50, true, false));
                    activeChar.sendPacket(Functions.Points2Trace(terr.getCoords(), 50, true, true));
                }

				/*public static Location getPointInRadius(Location a, Location b, double angle)
				{
					double radian1 = Math.toRadians(angle + calculateAngleFrom(a.getX(), a.getY(), b.getX(), b.getY()));
					int r = (int) Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY()));
					return new Location((int) Math.round(b.getX() + Math.cos(radian1) * r), (int) Math.round(b.getY() + Math.sin(radian1) * r), b.getZ());
				}

				public static Location getPointInRadius(Location a, int radius, double angle)
				{
					double radian1 = Math.toRadians(angle);
					return new Location((int) Math.round(a.x + Math.cos(radian1) * radius), (int) Math.round(a.y + Math.sin(radian1) * radius), a.z);
				}*/
            }

            //if(activeChar.isPlayer())
            //	_log.info("L2Skill:[0]["+radius+"] aimingTarget="+aimingTarget);

            boolean unlim = affect_limit_p == 0 && (affect_limit_n == 0 || Rnd.chance(50));
            int target_count = Rnd.get(affect_limit_p, affect_limit_p + affect_limit_n + 1);
            int npc_count = unlim ? Integer.MIN_VALUE : 0;
            int player_count = affect_limit_s == 0 ? Integer.MIN_VALUE : 0;

            for (L2Character target : aimingTarget.getAroundCharacters(radius, (int) aimingTarget.getColHeight() + 130)) {
                //if(activeChar.isPlayer())
                //	_log.info("L2Skill:[1] target="+target);
                if (terr != null && !terr.isInside(target))
                    continue;
                //if(activeChar.isPlayer())
                //	_log.info("L2Skill:[2] target="+target);
                if (target == null || activeChar == target || activeChar.getPlayer() != null && activeChar.getPlayer() == target.getPlayer())
                    continue;
                //if(activeChar.isPlayer())
                //	_log.info("L2Skill:[3] target="+target);
                if (getId() == SKILL_DETECTION && target.isInvisible() && target.getEffectList().getEffectByType(EffectType.p_hide) != null)
                    target.getEffectList().stopAllSkillEffects(EffectType.p_hide);

                if (checkTarget(activeChar, target, aimingTarget, forceUse, false, true) != null)
                    continue;
                    //if(activeChar.isPlayer())
                    //	_log.info("L2Skill:[4] target="+target);
                else if (!(activeChar instanceof L2DecoyInstance) && activeChar.isNpc() && target.isNpc() && activeChar.getNpcId() != 18622 && activeChar.getNpcId() != 18933)
                    continue;
                if (!target.isPlayable()) {
                    if (target_count <= npc_count)
                        continue;
                    npc_count++;
                } else {
                    if (10 <= player_count)
                        continue;
                    player_count++;
                }
                targets.add(target);
            }
        }
    }

    /**
     * Проверка на возможность проходимости скилла в независимости от того, есть ли на нем Immunity.
     */
    public final boolean canBeImmune() {
        return getId() != SKILL_RAID_CURSE && getId() != SKILL_WEAPON_PENALTY && getId() != SKILL_ARMOR_PENALTY && getId() != 4215 && !isPassive() && operate_type != OperateType.A1;
    }

    public final boolean canBeCounterAttack() {
        return getActivateRate() > 0;
    }

    public final void createEffects(final L2Character effector, final L2Character effected) {

    }

    public final void getEffects(final L2Character effector, final L2Character effected, final boolean calcChance, final boolean applyOnCaster) {
        //if(!applyOnCaster)
        //	new l2open.test.EffectInstance(effector, effected, this, 0);
        switch (ConfigValue.SkillTestVar) {
            case 0:
                getEffects0(effector, effected, calcChance, applyOnCaster);
                break;
            case 1:
                getEffects2(effector, effected, calcChance, applyOnCaster);
                break;
            case 2:
                getEffects3(effector, effected, calcChance, applyOnCaster);
                break;
        }
    }

    /**
     * Создает и применяет эффекты скилла. Выполняется в отдельном потоке.
     */
    public final void getEffects0(final L2Character effector, final L2Character effected, final boolean calcChance, final boolean applyOnCaster) {
        final boolean immune;
        if (isPassive() || _effectTemplates == null || _effectTemplates.length == 0 || effector == null || effected == null || (effected.isDead() && getId() != SKILL_BATTLEFIELD_DEATH_SYNDROME && !getCorpse()))
            return;
        else if (effected.p_block_buff.get() && canBeImmune() && !isCleanse() && getSkillType() == SkillType.BUFF || (effected.isPetrification() && getId() != 1551 && getId() != 1018 || effected.isInvul()) && isOffensive() && getSkillType() != SkillType.STEAL_BUFF && effector != effected) {
            if (!isCancel()) {
                if (effector.isPlayer())
                    effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addName(effected).addSkillName(_displayId, _displayLevel));
                return;
            }
            immune = true;
        } else if (effected.p_ignore_skill_freya && (getId() == 6274 || getId() == 6662 || getId() == 6275))
            return;
        else if (effected.getEffectList().getEffectByType(EffectType.ResDebuff) != null && canBeImmune() && isOffensive() && !applyOnCaster) {
            if (!isCancel()) {
                if (effected.isPlayer())
                    effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addName(effected).addSkillName(_displayId, _displayLevel));
                return;
            }
            immune = true;
        } else if (effected.isDoor())
            return;
        else if (isBlockedByChar(effected, this))
            return;
        else
            immune = false;
        final int sps = effector.getChargedSpiritShot();
        final double hp = effected.getCurrentHp();
        final double mp = effected.getCurrentMp();
        final double cp = effected.getCurrentCp();

        int obj_id = Rnd.get(Integer.MAX_VALUE - 1);
        ThreadPoolManager.getInstance().execute(new l2open.common.RunnableImpl() {
            @Override
            public void runImpl() {
                boolean success = true;
                try {
                    if (applyOnCaster == _applyOnCaster && calcChance && getActivateRate() > 0 && !Formulas.calcSkillSuccess(effector, effected, L2Skill.this, sps)) {
                        effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addString(effected.getName()).addSkillName(_displayId, _displayLevel));
                        success = false;
                    } else if (!applyOnCaster && !_applyOnCaster && checkSkillAbnormal(effected, !immune, obj_id))
                        success = false;
                    else if (applyOnCaster && _applyOnCaster && checkSkillAbnormal(effected, !immune, obj_id))
                        success = false;
                } finally {

                    boolean p_block_buff = effected.p_block_buff.get();
                    boolean p_block_debuff = effected.p_block_debuff.get();
                    boolean p_block_buff_pet = effected.getPet() != null ? effected.getPet().p_block_buff.get() : false;
                    final int mastery = effector.getSkillMastery(getId());
                    if (mastery == 2 && !applyOnCaster) // TODO: возмонжо условие !applyOnCaster лишнее...
                    {
                        effector.sendPacket(Msg.A_SKILL_IS_READY_TO_BE_USED_AGAIN_BUT_ITS_RE_USE_COUNTER_TIME_HAS_INCREASED);
                        effector.removeSkillMastery(getId());
                    }

                    boolean update_effect_list = false;
                    boolean applyCaster = false;
                    boolean counter_atack = false;

                    /**
                     * TODO: Переделать!!!
                     * 1. Срабатывает еще до расчета шанса.
                     * 2. Не работает на скилы с activate_rate=-1
                     * 3. Работает только на эффекты p_, на i_(мгновенного действия) не работает.
                     *
                     **/
                    if (canBeImmune() && success && getAbnormalType() != SkillAbnormalType.target_lock && isOffensive() && !effector.isTrap() && canBeCounterAttack() && Rnd.chance(effected.calcStat(isMagic() ? Stats.REFLECT_MAGIC_DEBUFF : Stats.REFLECT_PHYSIC_DEBUFF, 0, effector, L2Skill.this)) && effected.getEffectList().getEffectByType(EffectType.ResDebuff) == null) {
                        effected.sendPacket(new SystemMessage(SystemMessage.YOU_COUNTERED_C1S_ATTACK).addName(effector));
                        effector.sendPacket(new SystemMessage(SystemMessage.C1_DODGES_THE_ATTACK).addName(effected));
                        counter_atack = true;
                        p_block_buff = effector.p_block_buff.get();
                        p_block_debuff = effector.p_block_debuff.get();
                        p_block_buff_pet = effector.getPet() != null ? effector.getPet().p_block_buff.get() : false;
                    }

                    if (ConfigValue.EnableDamageOnScreen && effector.isPlayer() && isOffensive() && success && !immune) {
                        effector.sendPacket(new DamageTextNewPacket(effected.getObjectId(), getId(), getLevel(), ConfigValue.DamageOnScreenColorRSkillName, ConfigValue.DamageOnScreenColorRDmgMsg, ""));
                        //sendPacket(new DamageTextPacket(target.getObjectId(), damage, false, false, false, false, ConfigValue.DamageOnScreenFontId, ConfigValue.DamageOnScreenColorR, skill == null ? "" : skill.getName(), skill == null ? "" : skill.getIcon(), ConfigValue.DamageOnScreenRPosX, ConfigValue.DamageOnScreenRPosY, ConfigValue.DamageOnScreenRSizeX, ConfigValue.DamageOnScreenRSizeY));
                    }
				/*_effect_loc.lock();
				try
				{*/
                    loop:
                    for (EffectTemplate et : _effectTemplates) {
                        if (applyOnCaster != et._applyOnCaster || et._counter == 0 || et.getEffectType() != EffectType.DispelEffects && immune || et._level_min > getDisplayLevel() || et._level_max < getDisplayLevel())
                            continue;

                        L2Character target = effected;
                        if (et._applyOnCaster) {
                            applyCaster = true;
                            target = effector;
                        }

						/*if((target.isRaid() || target.isEpicRaid()) && et.getEffectType().isRaidImmune())
						{
							effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addString(effected.getName()).addSkillName(_displayId, _displayLevel));
							continue;
						}*/
                        Env env = new Env(effector, target, L2Skill.this);

                        if (mastery != 0)
                            env.skill_mastery = mastery;

                        // эффекты немедленного пременения не отражаются, а применяются на мешень.
                        if (target != effector && et.getEffectType() != EffectType.DispelEffects && counter_atack && !et._instantly) {
                            target = effector;
                            env.target = target;
                            applyCaster = true;
                        }
                        final L2Effect e = et.getEffect(env);
                        if (e != null) {
                            if (!success && !e._instantly)
                                continue;
                            e._obj_id = obj_id;
                            try {
                                int result;
                                // эффекты немедленного пременения не отражаются, а применяются на мешень.
                                if (e._count == 1 && e._instantly && !e.getSkill().isToggle()) {
                                    if (counter_atack && !et._applyOnCaster)
                                        env.target = effected;
                                    e.onStart();
                                    e.onActionTime();
                                    e.onExit();
                                    if (e.update_effect_list)
                                        update_effect_list = true;
                                } else if (!e.getSkill().isToggle() && (e.isOffensive() && p_block_debuff || !e.isOffensive() && p_block_buff))
                                    continue;
                                else if (e._count == 1 && e._instantly && !e.getSkill().isToggle() && counter_atack) {
                                    final L2Effect e2 = et.getEffect(new Env(effector, effected, L2Skill.this));
                                    e2.onStart();
                                    e2.onActionTime();
                                    e2.onExit();
                                    if (e2.update_effect_list)
                                        update_effect_list = true;
                                } else if ((result = e.getEffected().getEffectList().addEffect(e)) > 0) {
                                    update_effect_list = true;
                                    if ((result & 2) == 2)
                                        target.setCurrentHp(hp, false);
                                    if ((result & 4) == 4)
                                        target.setCurrentMp(mp);
                                    if ((result & 8) == 8)
                                        target.setCurrentCp(cp);
                                }
                            } catch (Exception e1) {
                                _log.info("L2Skill(1625): e == null ? " + (e == null) + " e.getEffected() == null ? " + (e.getEffected() == null) + " e.getEffected().getEffectList() == null ? " + (e.getEffected().getEffectList() == null) + " e.getTemplate() == ? " + (e.getTemplate() == null) + " e.getSkill() == null ? " + (e.getSkill() == null));
                                e1.printStackTrace();
                            }
                        }
                    }
				/*}
				finally
				{
					_effect_loc.unlock();
				}*/

                    L2Summon pet = effected.getPet();
                    if (pet != null && (pet.isSummon() || ConfigValue.ApplyBuffOnPet) && effected.isPlayer() && !applyOnCaster && getAbnormalInstant() != 1 && !p_block_buff_pet) {
                        if (getSkillType() == SkillType.BUFF && getTargetType() != SkillTargetType.TARGET_PARTY && getTargetType() != SkillTargetType.TARGET_CLAN_ONLY && getTargetType() != SkillTargetType.TARGET_CLAN && getTargetType() != SkillTargetType.TARGET_ALLY && getTargetType() != SkillTargetType.TARGET_COMMAND_CHANEL && !isCleanse() && getId() != 1157 && getId() != 1557 || getId() == 341) {
                            final double hp_pet = pet.getCurrentHp();
                            final double mp_pet = pet.getCurrentMp();
                            final double cp_pet = pet.getCurrentCp();
                            if (!isBlockedByChar(pet, L2Skill.this) && !checkSkillAbnormal(pet, true, obj_id)) {
                                boolean can_update_pet = false;
							/*_effect_loc.lock();
							try
							{*/
                                for (EffectTemplate et : getEffectTemplates()) {
                                    if (et.getEffectType() != et.getEffectType().Symbol) {
                                        // TODO: временная затычка, потом убрать!
                                        if (et._instantly && getId() != 7064 && getId() != 4527 && getId() != 3125 && getId() != 22230 && getId() != 22229 && getId() != 20006 && getId() != 1561 && getId() != 1349 && getId() != 707 && getId() != 121)
                                            continue;
                                        L2Effect effect = et.getEffect(new Env(effector, pet, L2Skill.this));
                                        if (effect == null) {
                                            //_log.warning("L2Skill(1601): Error Shedule SkillId=" + _displayId + " Level="+_displayLevel+" ");
                                            continue;
                                        }
                                        effect._obj_id = obj_id;
                                        try {
                                            int result;
                                            // эффекты немедленного пременения не отражаются, а применяются на мешень.
                                            if ((result = effect.getEffected().getEffectList().addEffect(effect)) > 0) {
                                                can_update_pet = true;
                                                if ((result & 2) == 2)
                                                    pet.setCurrentHp(hp_pet, false);
                                                if ((result & 4) == 4)
                                                    pet.setCurrentMp(mp_pet);
                                                if ((result & 8) == 8)
                                                    pet.setCurrentCp(cp_pet);
                                            }
                                        } catch (Exception e1) {
                                            _log.info("L2Skill(1625): effect == null ? " + (effect == null) + " effect.getEffected() == null ? " + (effect.getEffected() == null) + " effect.getEffected().getEffectList() == null ? " + (effect.getEffected().getEffectList() == null) + " effect.getTemplate() == ? " + (effect.getTemplate() == null) + " effect.getSkill() == null ? " + (effect.getSkill() == null));
                                            e1.printStackTrace();
                                        }

                                        //pet.getEffectList().addEffect(effect);
                                    }
                                }
							/*}
							finally
							{
								_effect_loc.unlock();
							}*/
                                if (can_update_pet)
                                    pet.updateEffectIcons();
                            }
                        }
                    } else if (pet != null && (pet.isSummon() || ConfigValue.ApplyBuffOnPet) && !pet.isDead() && getAbnormalInstant() == 1)
                        pet.altUseSkill(L2Skill.this, pet);

                    if (update_effect_list) {
                        effected.updateEffectIcons();
                        if (applyCaster)
                            effector.updateEffectIcons();
                    }

                    if (calcChance && !applyOnCaster) {
                        if (success) {
                            effector.sendPacket(new SystemMessage(SystemMessage.S1_HAS_SUCCEEDED).addSkillName(getId(), _displayLevel));
                            if (effector.isMonster()) {
                                effector.getAI().notifyEvent(CtrlEvent.EVT_SPELL_SUCCESSED, L2Skill.this, effected);
                            }
                        } else
                            effector.sendPacket(new SystemMessage(SystemMessage.S1_HAS_FAILED).addSkillName(getId(), _displayLevel));
                    }
                }
            }
        });
    }

    public final void getEffects1(final L2Character effector, final L2Character effected, final boolean calcChance, final boolean applyOnCaster) {
		/*if(effected.isPlayer())
		{
			_log.info("getCurrentHp: "+effected.getCurrentHp());
			_log.info("getCurrentMp: "+effected.getCurrentMp());
			_log.info("getCurrentCp: "+effected.getCurrentCp());
		}*/
        final boolean immune;
        if (isPassive() || _effectTemplates == null || _effectTemplates.length == 0 || effector == null || effected == null || (effected.isDead() && getId() != SKILL_BATTLEFIELD_DEATH_SYNDROME && !getCorpse()))
            return;
        else if (effected.p_block_buff.get() && canBeImmune() && !isCleanse() && getSkillType() == SkillType.BUFF || (effected.isPetrification() && getId() != 1551 && getId() != 1018 || effected.isInvul()) && isOffensive() && getSkillType() != SkillType.STEAL_BUFF && effector != effected) {
            if (!isCancel()) {
                if (effector.isPlayer())
                    effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addName(effected).addSkillName(_displayId, _displayLevel));
                return;
            }
            immune = true;
        } else if (effected.p_ignore_skill_freya && (getId() == 6274 || getId() == 6662 || getId() == 6275))
            return;
        else if (effected.getEffectList().getEffectByType(EffectType.ResDebuff) != null && canBeImmune() && isOffensive() && !applyOnCaster) {
            if (!isCancel()) {
                if (effected.isPlayer())
                    effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addName(effected).addSkillName(_displayId, _displayLevel));
                return;
            }
            immune = true;
        } else if (effected.isDoor())
            return;
        else if (isBlockedByChar(effected, this))
            return;
        else
            immune = false;
        final int sps = effector.getChargedSpiritShot();
        final double hp = effected.getCurrentHp();
        final double mp = effected.getCurrentMp();
        final double cp = effected.getCurrentCp();

        int obj_id = Rnd.get(Integer.MAX_VALUE - 1);
        ThreadPoolManager.getInstance().execute(new l2open.common.RunnableImpl() {
            @Override
            public void runImpl() {
                boolean success = true;

                try {
                    if (applyOnCaster == _applyOnCaster && calcChance && getActivateRate() > 0 && !applyOnCaster && !Formulas.calcSkillSuccess(effector, effected, L2Skill.this, sps)) {
                        effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addString(effected.getName()).addSkillName(_displayId, _displayLevel));
                        success = false;
                    } else if (!applyOnCaster && !_applyOnCaster && checkSkillAbnormal(effected, !immune, obj_id))
                        success = false;
                    else if (applyOnCaster && _applyOnCaster && checkSkillAbnormal(effected, !immune, obj_id))
                        success = false;
                } finally {
                    boolean p_block_buff = effected.p_block_buff.get();
                    boolean p_block_debuff = effected.p_block_debuff.get();
                    boolean p_block_buff_pet = effected.getPet() != null ? effected.getPet().p_block_buff.get() : false;
                    final int mastery = effector.getSkillMastery(getId());
                    if (mastery == 2 && !applyOnCaster) // TODO: возмонжо условие !applyOnCaster лишнее...
                    {
                        effector.sendPacket(Msg.A_SKILL_IS_READY_TO_BE_USED_AGAIN_BUT_ITS_RE_USE_COUNTER_TIME_HAS_INCREASED);
                        effector.removeSkillMastery(getId());
                    }

                    boolean update_effect_list = false;
                    boolean applyCaster = false;
                    boolean counter_atack = false;

                    /**
                     * TODO: Переделать!!!
                     * 1. Срабатывает еще до расчета шанса.
                     * 2. Не работает на скилы с activate_rate=-1
                     * 3. Работает только на эффекты p_, на i_(мгновенного действия) не работает.
                     *
                     **/
                    if (canBeImmune() && success && getAbnormalType() != SkillAbnormalType.target_lock && isOffensive() && !effector.isTrap() && canBeCounterAttack() && Rnd.chance(effected.calcStat(isMagic() ? Stats.REFLECT_MAGIC_DEBUFF : Stats.REFLECT_PHYSIC_DEBUFF, 0, effector, L2Skill.this)) && effected.getEffectList().getEffectByType(EffectType.ResDebuff) == null) {
                        effected.sendPacket(new SystemMessage(SystemMessage.YOU_COUNTERED_C1S_ATTACK).addName(effector));
                        effector.sendPacket(new SystemMessage(SystemMessage.C1_DODGES_THE_ATTACK).addName(effected));
                        counter_atack = true;
                        p_block_buff = effector.p_block_buff.get();
                        p_block_debuff = effector.p_block_debuff.get();
                        p_block_buff_pet = effector.getPet() != null ? effector.getPet().p_block_buff.get() : false;
                    }

                    if (ConfigValue.EnableDamageOnScreen && effector.isPlayer() && isOffensive() && success && !immune) {
                        effector.sendPacket(new DamageTextNewPacket(effected.getObjectId(), getId(), getLevel(), ConfigValue.DamageOnScreenColorRSkillName, ConfigValue.DamageOnScreenColorRDmgMsg, ""));
                        //sendPacket(new DamageTextPacket(target.getObjectId(), damage, false, false, false, false, ConfigValue.DamageOnScreenFontId, ConfigValue.DamageOnScreenColorR, skill == null ? "" : skill.getName(), skill == null ? "" : skill.getIcon(), ConfigValue.DamageOnScreenRPosX, ConfigValue.DamageOnScreenRPosY, ConfigValue.DamageOnScreenRSizeX, ConfigValue.DamageOnScreenRSizeY));
                    }
				/*_effect_loc.lock();
				try
				{*/
                    loop:
                    for (EffectTemplate et : _effectTemplates) {
                        if (applyOnCaster != et._applyOnCaster || et._counter == 0 || et.getEffectType() != EffectType.DispelEffects && immune || et._level_min > getDisplayLevel() || et._level_max < getDisplayLevel())
                            continue;

                        L2Character target = effected;
                        if (et._applyOnCaster) {
                            applyCaster = true;
                            target = effector;
                        }

						/*if((target.isRaid() || target.isEpicRaid()) && et.getEffectType().isRaidImmune())
						{
							effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addString(effected.getName()).addSkillName(_displayId, _displayLevel));
							continue;
						}*/
                        Env env = new Env(effector, target, L2Skill.this);

                        if (mastery != 0)
                            env.skill_mastery = mastery;

                        // эффекты немедленного пременения не отражаются, а применяются на мешень.
                        if (target != effector && et.getEffectType() != EffectType.DispelEffects && counter_atack && !et._instantly) {
                            target = effector;
                            env.target = target;
                            applyCaster = true;
                        }
                        final L2Effect e = et.getEffect(env);
                        if (e != null) {
                            if (!success && !e._instantly)
                                continue;
                            e._obj_id = obj_id;
                            try {
                                int result;
                                // эффекты немедленного пременения не отражаются, а применяются на мешень.
                                if (e._count == 1 && e._instantly && !e.getSkill().isToggle()) {
                                    if (counter_atack && !et._applyOnCaster)
                                        env.target = effected;
                                    e.onStart();
                                    e.onActionTime();
                                    e.onExit();
                                    if (e.update_effect_list)
                                        update_effect_list = true;
                                } else if (!e.getSkill().isToggle() && (e.isOffensive() && p_block_debuff || !e.isOffensive() && p_block_buff))
                                    continue;
                                else if (e._count == 1 && e._instantly && !e.getSkill().isToggle() && counter_atack) {
                                    final L2Effect e2 = et.getEffect(new Env(effector, effected, L2Skill.this));
                                    e2.onStart();
                                    e2.onActionTime();
                                    e2.onExit();
                                    if (e2.update_effect_list)
                                        update_effect_list = true;
                                } else if ((result = e.getEffected().getEffectList().addEffect(e)) > 0) {
                                    update_effect_list = true;
                                    if ((result & 2) == 2)
                                        target.setCurrentHp(hp, false);
                                    if ((result & 4) == 4)
                                        target.setCurrentMp(mp);
                                    if ((result & 8) == 8)
                                        target.setCurrentCp(cp);
                                }
                            } catch (Exception e1) {
                                _log.info("L2Skill(1625): e == null ? " + (e == null) + " e.getEffected() == null ? " + (e.getEffected() == null) + " e.getEffected().getEffectList() == null ? " + (e.getEffected().getEffectList() == null) + " e.getTemplate() == ? " + (e.getTemplate() == null) + " e.getSkill() == null ? " + (e.getSkill() == null));
                                e1.printStackTrace();
                            }
                        }
                    }
				/*}
				finally
				{
					_effect_loc.unlock();
				}*/

                    L2Summon pet = effected.getPet();
                    if (pet != null && (pet.isSummon() || ConfigValue.ApplyBuffOnPet) && effected.isPlayer() && !applyOnCaster && getAbnormalInstant() != 1 && !p_block_buff_pet) {
                        if (getSkillType() == SkillType.BUFF && getTargetType() != SkillTargetType.TARGET_PARTY && getTargetType() != SkillTargetType.TARGET_CLAN_ONLY && getTargetType() != SkillTargetType.TARGET_CLAN && getTargetType() != SkillTargetType.TARGET_ALLY && getTargetType() != SkillTargetType.TARGET_COMMAND_CHANEL && !isCleanse() && getId() != 1157 && getId() != 1557 || getId() == 341) {
                            final double hp_pet = pet.getCurrentHp();
                            final double mp_pet = pet.getCurrentMp();
                            final double cp_pet = pet.getCurrentCp();
                            if (!isBlockedByChar(pet, L2Skill.this) && !checkSkillAbnormal(pet, true, obj_id)) {
                                boolean can_update_pet = false;
							/*_effect_loc.lock();
							try
							{*/
                                for (EffectTemplate et : getEffectTemplates()) {
                                    if (et.getEffectType() != et.getEffectType().Symbol) {
                                        // TODO: временная затычка, потом убрать!
                                        if (et._instantly && getId() != 7064 && getId() != 4527 && getId() != 3125 && getId() != 22230 && getId() != 22229 && getId() != 20006 && getId() != 1561 && getId() != 1349 && getId() != 707 && getId() != 121)
                                            continue;
                                        L2Effect effect = et.getEffect(new Env(effector, pet, L2Skill.this));
                                        if (effect == null) {
                                            //_log.warning("L2Skill(1601): Error Shedule SkillId=" + _displayId + " Level="+_displayLevel+" ");
                                            continue;
                                        }
                                        effect._obj_id = obj_id;
                                        try {
                                            int result;
                                            // эффекты немедленного пременения не отражаются, а применяются на мешень.
                                            if ((result = effect.getEffected().getEffectList().addEffect(effect)) > 0) {
                                                can_update_pet = true;
                                                if ((result & 2) == 2)
                                                    pet.setCurrentHp(hp_pet, false);
                                                if ((result & 4) == 4)
                                                    pet.setCurrentMp(mp_pet);
                                                if ((result & 8) == 8)
                                                    pet.setCurrentCp(cp_pet);
                                            }
                                        } catch (Exception e1) {
                                            _log.info("L2Skill(1625): effect == null ? " + (effect == null) + " effect.getEffected() == null ? " + (effect.getEffected() == null) + " effect.getEffected().getEffectList() == null ? " + (effect.getEffected().getEffectList() == null) + " effect.getTemplate() == ? " + (effect.getTemplate() == null) + " effect.getSkill() == null ? " + (effect.getSkill() == null));
                                            e1.printStackTrace();
                                        }

                                        //pet.getEffectList().addEffect(effect);
                                    }
                                }
							/*}
							finally
							{
								_effect_loc.unlock();
							}*/
                                if (can_update_pet)
                                    pet.updateEffectIcons();
                            }
                        }
                    } else if (pet != null && (pet.isSummon() || ConfigValue.ApplyBuffOnPet) && !pet.isDead() && getAbnormalInstant() == 1)
                        pet.altUseSkill(L2Skill.this, pet);

                    if (update_effect_list) {
                        effected.updateEffectIcons();
                        if (applyCaster)
                            effector.updateEffectIcons();
                    }

                    if (calcChance)
                        if (success) {
                            effector.sendPacket(new SystemMessage(SystemMessage.S1_HAS_SUCCEEDED).addSkillName(getId(), _displayLevel));
                            if (effector.isMonster()) {
                                effector.getAI().notifyEvent(CtrlEvent.EVT_SPELL_SUCCESSED, L2Skill.this, effected);
                            }
                        } else
                            effector.sendPacket(new SystemMessage(SystemMessage.S1_HAS_FAILED).addSkillName(getId(), _displayLevel));
                }
            }
        });
    }

    public final void getEffects2(final L2Character effector, final L2Character effected, final boolean calcChance, final boolean applyOnCaster) {
		/*if(effected.isPlayer())
		{
			_log.info("getCurrentHp: "+effected.getCurrentHp());
			_log.info("getCurrentMp: "+effected.getCurrentMp());
			_log.info("getCurrentCp: "+effected.getCurrentCp());
		}*/
        final boolean immune;
        if (isPassive() || _effectTemplates == null || _effectTemplates.length == 0 || effector == null || effected == null || (effector.isDead() && getId() != SKILL_BATTLEFIELD_DEATH_SYNDROME) || (effected.isDead() && getId() != SKILL_BATTLEFIELD_DEATH_SYNDROME && !getCorpse()))
            return;
        else if (effected.p_block_buff.get() && canBeImmune() && !isCleanse() && getSkillType() == SkillType.BUFF || (effected.isPetrification() && getId() != 1551 && getId() != 1018 || effected.isInvul()) && isOffensive() && getSkillType() != SkillType.STEAL_BUFF && effector != effected) {
            if (!isCancel()) {
                if (effector.isPlayer())
                    effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addName(effected).addSkillName(_displayId, _displayLevel));
                return;
            }
            immune = true;
        } else if (effected.p_ignore_skill_freya && (getId() == 6274 || getId() == 6662 || getId() == 6275))
            return;
        else if (effected.getEffectList().getEffectByType(EffectType.ResDebuff) != null && canBeImmune() && isOffensive() && !applyOnCaster) {
            if (!isCancel()) {
                if (effected.isPlayer())
                    effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addName(effected).addSkillName(_displayId, _displayLevel));
                return;
            }
            immune = true;
        } else if (effected.isDoor())
            return;
        else if (isBlockedByChar(effected, this))
            return;
        else
            immune = false;
        final int sps = effector.getChargedSpiritShot();
        final boolean success;
        final double hp = effected.getCurrentHp();
        final double mp = effected.getCurrentMp();
        final double cp = effected.getCurrentCp();

        int obj_id = Rnd.get(Integer.MAX_VALUE - 1);
        if (applyOnCaster == _applyOnCaster && calcChance && getActivateRate() > 0 && !applyOnCaster && !Formulas.calcSkillSuccess(effector, effected, this, sps)) {
            effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addString(effected.getName()).addSkillName(_displayId, _displayLevel));
            success = false;
        } else if (!applyOnCaster && !_applyOnCaster && checkSkillAbnormal(effected, !immune, obj_id))
            success = false;
        else if (applyOnCaster && _applyOnCaster && checkSkillAbnormal(effected, !immune, obj_id))
            success = false;
        else
            success = true;

        ThreadPoolManager.getInstance().execute(new l2open.common.RunnableImpl() {
            @Override
            public void runImpl() {
                boolean p_block_buff = effected.p_block_buff.get();
                boolean p_block_debuff = effected.p_block_debuff.get();
                boolean p_block_buff_pet = effected.getPet() != null ? effected.getPet().p_block_buff.get() : false;
                final int mastery = effector.getSkillMastery(getId());
                if (mastery == 2 && !applyOnCaster) {
                    effector.sendPacket(Msg.A_SKILL_IS_READY_TO_BE_USED_AGAIN_BUT_ITS_RE_USE_COUNTER_TIME_HAS_INCREASED);
                    effector.removeSkillMastery(getId());
                }

                boolean update_effect_list = false;
                boolean applyCaster = false;
                boolean counter_atack = false;
                if (canBeImmune() && success && getAbnormalType() != SkillAbnormalType.target_lock && isOffensive() && !effector.isTrap() && canBeCounterAttack() && Rnd.chance(effected.calcStat(isMagic() ? Stats.REFLECT_MAGIC_DEBUFF : Stats.REFLECT_PHYSIC_DEBUFF, 0, effector, L2Skill.this)) && effected.getEffectList().getEffectByType(EffectType.ResDebuff) == null) {
                    effected.sendPacket(new SystemMessage(SystemMessage.YOU_COUNTERED_C1S_ATTACK).addName(effector));
                    effector.sendPacket(new SystemMessage(SystemMessage.C1_DODGES_THE_ATTACK).addName(effected));
                    counter_atack = true;
                    p_block_buff = effector.p_block_buff.get();
                    p_block_debuff = effector.p_block_debuff.get();
                    p_block_buff_pet = effector.getPet() != null ? effector.getPet().p_block_buff.get() : false;
                }

                _effect_loc.lock();
                try {
                    loop:
                    for (EffectTemplate et : _effectTemplates) {
                        if (applyOnCaster != et._applyOnCaster || et._counter == 0 || et.getEffectType() != EffectType.DispelEffects && immune || et._level_min > getDisplayLevel() || et._level_max < getDisplayLevel())
                            continue;

                        L2Character target = effected;
                        if (et._applyOnCaster) {
                            applyCaster = true;
                            target = effector;
                        }

                        if ((target.isRaid() || target.isEpicRaid()) && et.getEffectType().isRaidImmune()) {
                            effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addString(effected.getName()).addSkillName(_displayId, _displayLevel));
                            continue;
                        }
                        Env env = new Env(effector, target, L2Skill.this);

                        if (mastery != 0)
                            env.skill_mastery = mastery;

                        if (target != effector && et.getEffectType() != EffectType.DispelEffects && counter_atack) {
                            target = effector;
                            env.target = target;
                            applyCaster = true;
                        }
                        final L2Effect e = et.getEffect(env);
                        if (e != null) {
                            if (!success && !e._instantly)
                                continue;
                            e._obj_id = obj_id;
                            try {
                                int result;
                                // эффекты немедленного пременения не отражаются, а применяются на мешень.
                                if (e._count == 1 && e._instantly && !e.getSkill().isToggle() && !counter_atack) {
                                    e.onStart();
                                    e.onActionTime();
                                    e.onExit();
                                    if (e.update_effect_list)
                                        update_effect_list = true;
                                } else if (!e.getSkill().isToggle() && (e.isOffensive() && p_block_debuff || !e.isOffensive() && p_block_buff))
                                    continue;
                                else if (e._count == 1 && e._instantly && !e.getSkill().isToggle() && counter_atack) {
                                    final L2Effect e2 = et.getEffect(new Env(effector, effected, L2Skill.this));
                                    e2.onStart();
                                    e2.onActionTime();
                                    e2.onExit();
                                    if (e2.update_effect_list)
                                        update_effect_list = true;
                                } else if ((result = e.getEffected().getEffectList().addEffect(e)) > 0) {
                                    update_effect_list = true;
                                    if ((result & 2) == 2)
                                        target.setCurrentHp(hp, false);
                                    if ((result & 4) == 4)
                                        target.setCurrentMp(mp);
                                    if ((result & 8) == 8)
                                        target.setCurrentCp(cp);
                                }
                            } catch (Exception e1) {
                                _log.info("L2Skill(1625): e == null ? " + (e == null) + " e.getEffected() == null ? " + (e.getEffected() == null) + " e.getEffected().getEffectList() == null ? " + (e.getEffected().getEffectList() == null) + " e.getTemplate() == ? " + (e.getTemplate() == null) + " e.getSkill() == null ? " + (e.getSkill() == null));
                                e1.printStackTrace();
                            }
                        }
                    }
                } finally {
                    _effect_loc.unlock();
                }

                L2Summon pet = effected.getPet();
                if (pet != null && (pet.isSummon() || ConfigValue.ApplyBuffOnPet) && effected.isPlayer() && !applyOnCaster && getAbnormalInstant() != 1 && !p_block_buff_pet) {
                    if (getSkillType() == SkillType.BUFF && getTargetType() != SkillTargetType.TARGET_PARTY && getTargetType() != SkillTargetType.TARGET_CLAN_ONLY && getTargetType() != SkillTargetType.TARGET_CLAN && getTargetType() != SkillTargetType.TARGET_ALLY && getTargetType() != SkillTargetType.TARGET_COMMAND_CHANEL && !isCleanse() && getId() != 1157 && getId() != 1557 || getId() == 341) {
                        final double hp_pet = pet.getCurrentHp();
                        final double mp_pet = pet.getCurrentMp();
                        final double cp_pet = pet.getCurrentCp();
                        if (!isBlockedByChar(pet, L2Skill.this) && !checkSkillAbnormal(pet, true, obj_id)) {
                            boolean can_update_pet = false;
                            _effect_loc.lock();
                            try {
                                for (EffectTemplate et : getEffectTemplates()) {
                                    if (et.getEffectType() != et.getEffectType().Symbol) {
                                        // TODO: временная затычка, потом убрать!
                                        if (et._instantly && getId() != 7064 && getId() != 4527 && getId() != 3125 && getId() != 22230 && getId() != 22229 && getId() != 20006 && getId() != 1561 && getId() != 1349 && getId() != 707 && getId() != 121)
                                            continue;
                                        L2Effect effect = et.getEffect(new Env(effector, pet, L2Skill.this));
                                        if (effect == null) {
                                            //_log.warning("L2Skill(1601): Error Shedule SkillId=" + _displayId + " Level="+_displayLevel+" ");
                                            continue;
                                        }
                                        effect._obj_id = obj_id;
                                        try {
                                            int result;
                                            // эффекты немедленного пременения не отражаются, а применяются на мешень.
                                            if ((result = effect.getEffected().getEffectList().addEffect(effect)) > 0) {
                                                can_update_pet = true;
                                                if ((result & 2) == 2)
                                                    pet.setCurrentHp(hp_pet, false);
                                                if ((result & 4) == 4)
                                                    pet.setCurrentMp(mp_pet);
                                                if ((result & 8) == 8)
                                                    pet.setCurrentCp(cp_pet);
                                            }
                                        } catch (Exception e1) {
                                            _log.info("L2Skill(1625): effect == null ? " + (effect == null) + " effect.getEffected() == null ? " + (effect.getEffected() == null) + " effect.getEffected().getEffectList() == null ? " + (effect.getEffected().getEffectList() == null) + " effect.getTemplate() == ? " + (effect.getTemplate() == null) + " effect.getSkill() == null ? " + (effect.getSkill() == null));
                                            e1.printStackTrace();
                                        }

                                        //pet.getEffectList().addEffect(effect);
                                    }
                                }
                            } finally {
                                _effect_loc.unlock();
                            }
                            if (can_update_pet)
                                pet.updateEffectIcons();
                        }
                    }
                } else if (pet != null && (pet.isSummon() || ConfigValue.ApplyBuffOnPet) && !pet.isDead() && getAbnormalInstant() == 1)
                    pet.altUseSkill(L2Skill.this, pet);

                if (update_effect_list) {
                    effected.updateEffectIcons();
                    if (applyCaster)
                        effector.updateEffectIcons();
                }

                if (calcChance)
                    if (success) {
                        effector.sendPacket(new SystemMessage(SystemMessage.S1_HAS_SUCCEEDED).addSkillName(getId(), _displayLevel));
                        if (effector.isMonster()) {
                            effector.getAI().notifyEvent(CtrlEvent.EVT_SPELL_SUCCESSED, L2Skill.this, effected);
                        }
                    } else
                        effector.sendPacket(new SystemMessage(SystemMessage.S1_HAS_FAILED).addSkillName(getId(), _displayLevel));
            }
        });
    }

    public final void getEffects3(final L2Character effector, final L2Character effected, final boolean calcChance, final boolean applyOnCaster) {
		/*if(effected.isPlayer())
		{
			_log.info("getCurrentHp: "+effected.getCurrentHp());
			_log.info("getCurrentMp: "+effected.getCurrentMp());
			_log.info("getCurrentCp: "+effected.getCurrentCp());
		}*/
        final boolean immune;
        boolean p_block_buff = effected.p_block_buff.get();
        boolean p_block_debuff = effected.p_block_debuff.get();
        boolean p_block_buff_pet = effected.getPet() != null ? effected.getPet().p_block_buff.get() : false;

        if (isPassive() || _effectTemplates == null || _effectTemplates.length == 0 || effector == null || effected == null || (effector.isDead() && getId() != SKILL_BATTLEFIELD_DEATH_SYNDROME) || (effected.isDead() && getId() != SKILL_BATTLEFIELD_DEATH_SYNDROME && !getCorpse()))
            return;
        else if (p_block_buff && canBeImmune() && !isCleanse() && getSkillType() == SkillType.BUFF || (effected.isPetrification() && getId() != 1551 && getId() != 1018 || effected.isInvul()) && isOffensive() && getSkillType() != SkillType.STEAL_BUFF && effector != effected) {
            if (!isCancel()) {
                if (effector.isPlayer())
                    effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addName(effected).addSkillName(_displayId, _displayLevel));
                return;
            }
            immune = true;
        } else if (effected.p_ignore_skill_freya && (getId() == 6274 || getId() == 6662 || getId() == 6275))
            return;
        else if (effected.getEffectList().getEffectByType(EffectType.ResDebuff) != null && canBeImmune() && isOffensive() && !applyOnCaster) {
            if (!isCancel()) {
                if (effected.isPlayer())
                    effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addName(effected).addSkillName(_displayId, _displayLevel));
                return;
            }
            immune = true;
        } else if (effected.isDoor())
            return;
        else if (isBlockedByChar(effected, this))
            return;
        else
            immune = false;
        final int sps = effector.getChargedSpiritShot();
        final boolean success;
        final double hp = effected.getCurrentHp();
        final double mp = effected.getCurrentMp();
        final double cp = effected.getCurrentCp();

        int obj_id = Rnd.get(Integer.MAX_VALUE - 1);
        if (applyOnCaster == _applyOnCaster && calcChance && getActivateRate() > 0 && !applyOnCaster && !Formulas.calcSkillSuccess(effector, effected, this, sps)) {
            effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addString(effected.getName()).addSkillName(_displayId, _displayLevel));
            success = false;
        } else if (!applyOnCaster && !_applyOnCaster && checkSkillAbnormal(effected, !immune, obj_id))
            success = false;
        else if (applyOnCaster && _applyOnCaster && checkSkillAbnormal(effected, !immune, obj_id))
            success = false;
        else
            success = true;

		/*ThreadPoolManager.getInstance().execute(new l2open.common.RunnableImpl()
		{
			@Override
			public void runImpl()
			{*/
        final int mastery = effector.getSkillMastery(getId());
        if (mastery == 2 && !applyOnCaster) {
            effector.sendPacket(Msg.A_SKILL_IS_READY_TO_BE_USED_AGAIN_BUT_ITS_RE_USE_COUNTER_TIME_HAS_INCREASED);
            effector.removeSkillMastery(getId());
        }

        boolean update_effect_list = false;
        boolean applyCaster = false;
        boolean counter_atack = false;
        if (canBeImmune() && success && getAbnormalType() != SkillAbnormalType.target_lock && isOffensive() && !effector.isTrap() && canBeCounterAttack() && Rnd.chance(effected.calcStat(isMagic() ? Stats.REFLECT_MAGIC_DEBUFF : Stats.REFLECT_PHYSIC_DEBUFF, 0, effector, L2Skill.this)) && effected.getEffectList().getEffectByType(EffectType.ResDebuff) == null) {
            effected.sendPacket(new SystemMessage(SystemMessage.YOU_COUNTERED_C1S_ATTACK).addName(effector));
            effector.sendPacket(new SystemMessage(SystemMessage.C1_DODGES_THE_ATTACK).addName(effected));
            counter_atack = true;
            p_block_buff = effector.p_block_buff.get();
            p_block_debuff = effector.p_block_debuff.get();
            p_block_buff_pet = effector.getPet() != null ? effector.getPet().p_block_buff.get() : false;
        }

        _effect_loc.lock();
        try {
            loop:
            for (EffectTemplate et : _effectTemplates) {
                if (applyOnCaster != et._applyOnCaster || et._counter == 0 || et.getEffectType() != EffectType.DispelEffects && immune || et._level_min > getDisplayLevel() || et._level_max < getDisplayLevel())
                    continue;

                L2Character target = effected;
                if (et._applyOnCaster) {
                    applyCaster = true;
                    target = effector;
                }

                if ((target.isRaid() || target.isEpicRaid()) && et.getEffectType().isRaidImmune()) {
                    effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addString(effected.getName()).addSkillName(_displayId, _displayLevel));
                    continue;
                }
                Env env = new Env(effector, target, L2Skill.this);

                if (mastery != 0)
                    env.skill_mastery = mastery;

                if (target != effector && et.getEffectType() != EffectType.DispelEffects && counter_atack) {
                    target = effector;
                    env.target = target;
                    applyCaster = true;
                }
                final L2Effect e = et.getEffect(env);
                if (e != null) {
                    if (!success && !e._instantly)
                        continue;
                    e._obj_id = obj_id;
                    try {
                        int result;
                        // эффекты немедленного пременения не отражаются, а применяются на мешень.
                        if (e._count == 1 && e._instantly && !e.getSkill().isToggle() && !counter_atack) {
                            e.onStart();
                            e.onActionTime();
                            e.onExit();
                            if (e.update_effect_list)
                                update_effect_list = true;

                        } else if (!e.getSkill().isToggle() && (e.isOffensive() && p_block_debuff || !e.isOffensive() && p_block_buff))
                            continue;
                        else if (e._count == 1 && e._instantly && !e.getSkill().isToggle() && counter_atack) {
                            final L2Effect e2 = et.getEffect(new Env(effector, effected, L2Skill.this));
                            e2.onStart();
                            e2.onActionTime();
                            e2.onExit();
                            if (e2.update_effect_list)
                                update_effect_list = true;
                        } else if ((result = e.getEffected().getEffectList().addEffect(e)) > 0) {
                            update_effect_list = true;
                            if ((result & 2) == 2)
                                target.setCurrentHp(hp, false);
                            if ((result & 4) == 4)
                                target.setCurrentMp(mp);
                            if ((result & 8) == 8)
                                target.setCurrentCp(cp);
                        }
                    } catch (Exception e1) {
                        _log.info("L2Skill(1625): e == null ? " + (e == null) + " e.getEffected() == null ? " + (e.getEffected() == null) + " e.getEffected().getEffectList() == null ? " + (e.getEffected().getEffectList() == null) + " e.getTemplate() == ? " + (e.getTemplate() == null) + " e.getSkill() == null ? " + (e.getSkill() == null));
                        e1.printStackTrace();
                    }
                }
            }
        } finally {
            _effect_loc.unlock();
        }

        L2Summon pet = effected.getPet();
        if (pet != null && (pet.isSummon() || ConfigValue.ApplyBuffOnPet) && effected.isPlayer() && !applyOnCaster && getAbnormalInstant() != 1 && !p_block_buff_pet) {
            if (getSkillType() == SkillType.BUFF && getTargetType() != SkillTargetType.TARGET_PARTY && getTargetType() != SkillTargetType.TARGET_CLAN_ONLY && getTargetType() != SkillTargetType.TARGET_CLAN && getTargetType() != SkillTargetType.TARGET_ALLY && getTargetType() != SkillTargetType.TARGET_COMMAND_CHANEL && !isCleanse() && getId() != 1157 && getId() != 1557 || getId() == 341) {
                final double hp_pet = pet.getCurrentHp();
                final double mp_pet = pet.getCurrentMp();
                final double cp_pet = pet.getCurrentCp();
                if (!isBlockedByChar(pet, L2Skill.this) && !checkSkillAbnormal(pet, true, obj_id)) {
                    boolean can_update_pet = false;
                    _effect_loc.lock();
                    try {
                        for (EffectTemplate et : getEffectTemplates()) {
                            if (et.getEffectType() != et.getEffectType().Symbol) {
                                // TODO: временная затычка, потом убрать!
                                if (et._instantly && getId() != 7064 && getId() != 4527 && getId() != 3125 && getId() != 22230 && getId() != 22229 && getId() != 20006 && getId() != 1561 && getId() != 1349 && getId() != 707 && getId() != 121)
                                    continue;
                                L2Effect effect = et.getEffect(new Env(effector, pet, L2Skill.this));
                                if (effect == null) {
                                    //_log.warning("L2Skill(1601): Error Shedule SkillId=" + _displayId + " Level="+_displayLevel+" ");
                                    continue;
                                }
                                effect._obj_id = obj_id;
                                try {
                                    int result;
                                    // эффекты немедленного пременения не отражаются, а применяются на мешень.
                                    if ((result = effect.getEffected().getEffectList().addEffect(effect)) > 0) {
                                        can_update_pet = true;
                                        if ((result & 2) == 2)
                                            pet.setCurrentHp(hp_pet, false);
                                        if ((result & 4) == 4)
                                            pet.setCurrentMp(mp_pet);
                                        if ((result & 8) == 8)
                                            pet.setCurrentCp(cp_pet);
                                    }
                                } catch (Exception e1) {
                                    _log.info("L2Skill(1625): effect == null ? " + (effect == null) + " effect.getEffected() == null ? " + (effect.getEffected() == null) + " effect.getEffected().getEffectList() == null ? " + (effect.getEffected().getEffectList() == null) + " effect.getTemplate() == ? " + (effect.getTemplate() == null) + " effect.getSkill() == null ? " + (effect.getSkill() == null));
                                    e1.printStackTrace();
                                }

                                //pet.getEffectList().addEffect(effect);
                            }
                        }
                    } finally {
                        _effect_loc.unlock();
                    }
                    if (can_update_pet)
                        pet.updateEffectIcons();
                }
            }
        } else if (pet != null && (pet.isSummon() || ConfigValue.ApplyBuffOnPet) && !pet.isDead() && getAbnormalInstant() == 1)
            pet.altUseSkill(L2Skill.this, pet);

        if (update_effect_list) {
            effected.updateEffectIcons();
            if (applyCaster)
                effector.updateEffectIcons();
        }

        if (calcChance)
            if (success) {
                effector.sendPacket(new SystemMessage(SystemMessage.S1_HAS_SUCCEEDED).addSkillName(getId(), _displayLevel));
                if (effector.isMonster()) {
                    effector.getAI().notifyEvent(CtrlEvent.EVT_SPELL_SUCCESSED, L2Skill.this, effected);
                }
            } else
                effector.sendPacket(new SystemMessage(SystemMessage.S1_HAS_FAILED).addSkillName(getId(), _displayLevel));
			/*}
		});*/
    }

    public void exitEffect() {
        // TODO:
    }

    public final void attach(EffectTemplate effect) {
        if (effect._is_first != -1)
            _log.info("L2Skill: !!!!!!!!!!: " + effect);
        if (_effectTemplates == null) {
            effect._is_first = 0;
            _effectTemplates = new EffectTemplate[]{effect};
        } else {
            int len = _effectTemplates.length;
            effect._is_first = len;
            EffectTemplate[] tmp = new EffectTemplate[len + 1];
            System.arraycopy(_effectTemplates, 0, tmp, 0, len);
            tmp[len] = effect;
            _effectTemplates = tmp;
        }
    }

    public final void attach(FuncTemplate f) {
        if (_funcTemplates == null)
            _funcTemplates = new FuncTemplate[]{f};
        else {
            int len = _funcTemplates.length;
            FuncTemplate[] tmp = new FuncTemplate[len + 1];
            System.arraycopy(_funcTemplates, 0, tmp, 0, len);
            tmp[len] = f;
            _funcTemplates = tmp;
        }
    }

    public final Func[] getStatFuncs() {
        if (_funcTemplates == null)
            return _emptyFunctionSet;
        GArray<Func> funcs = new GArray<Func>();
        for (FuncTemplate t : _funcTemplates) {
            Func f = t.getFunc(this); // skill is owner
            if (f != null)
                funcs.add(f);
        }
        if (funcs.size() == 0)
            return _emptyFunctionSet;
        return funcs.toArray(new Func[funcs.size()]);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final L2Skill other = (L2Skill) obj;
        if (_displayId == null) {
            if (other._displayId != null)
                return false;
        } else if (!_displayId.equals(other._displayId))
            return false;
        if (_displayLevel == null) {
            if (other._displayLevel != null)
                return false;
        } else if (!_displayLevel.equals(other._displayLevel))
            return false;
        if (_id == null) {
            if (other._id != null)
                return false;
        } else if (!_id.equals(other._id))
            return false;
        if (_level == null) {
            if (other._level != null)
                return false;
        } else if (!_level.equals(other._level))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (_displayId == null ? 0 : _displayId.hashCode());
        result = prime * result + (_displayLevel == null ? 0 : _displayLevel.hashCode());
        result = prime * result + (_id == null ? 0 : _id.hashCode());
        result = prime * result + (_level == null ? 0 : _level.hashCode());
        return result;
    }

    public final void attach(Condition c) {
        _preCondition = (Condition[]) Util.addElementToArray(_preCondition, c, Condition.class);
    }

    public final boolean canTeachBy(int npcId) {
        return _teachers == null || _teachers.contains(npcId);
    }

    public final int getActivateRate() {
        return _activateRate;
    }

    public void setActivateRate(int rate) {
        _activateRate = rate;
    }

    public AddedSkill[] getAddedSkills() {
        return _addedSkills == null ? _emptyAddedSkills : _addedSkills;
    }

    public final boolean getCanLearn(ClassId cls) {
        return _canLearn == null || _canLearn.contains(cls);
    }

    /**
     * @return Returns the castRange.
     */
    public final int getCastRange() {
        return _castRange;
    }

    public final int getAffectRange() {
        return _affect_range;
    }

    public final int getAOECastRange() {
        return Math.max(_castRange, _skillRadius);
    }

    public int getCondCharges() {
        return _condCharges;
    }

    public final int getCoolTime() {
        return _coolTime;
    }

    public boolean getCorpse() {
        return _isCorpse;
    }

    public int getDelayedEffect() {
        return _delayedEffect;
    }

    public int getDelayedEffectLevel() {
        return _delayedEffectLevel;
    }

    public final int getDisplayId() {
        return _displayId;
    }

    public short getDisplayLevel() {
        return _displayLevel;
    }

    public int getEffectPoint() {
        return _effectPoint;
    }

    public EffectTemplate[] getEffectTemplates() {
        return _effectTemplates;
    }

    public L2Effect getSameByStackType(ConcurrentLinkedQueue<L2Effect> ef_list) {
        if (_effectTemplates == null)
            return null;
        L2Effect ret;
        for (EffectTemplate et : _effectTemplates)
            if (et != null && (ret = et.getSameByStackType(ef_list, getAbnormalType())) != null)
                return ret;
        return null;
    }

    public L2Effect getSameByStackType(EffectList ef_list) {
        return getSameByStackType(ef_list.getAllEffects());
    }

    public L2Effect getSameByStackType(L2Character actor) {
        return getSameByStackType(actor.getEffectList().getAllEffects());
    }

    public final Element getElement() {
        return _element;
    }

    public final int getElementPower() {
        return _elementPower;
    }

    public L2Skill getFirstAddedSkill() {
        if (_addedSkills == null)
            return null;
        return _addedSkills[0].getSkill();
    }

    public int getFlyRadius() {
        return _flyRadius;
    }

    public FlyType getFlyType() {
        return _flyType;
    }

    public boolean isFlyToBack() {
        return _flyToBack;
    }

    public int getForceId() {
        return _forceId;
    }

    public final int getHitTime() {
        return _hitTime;
    }

    /**
     * @return Returns the hpConsume.
     */
    public final int getHpConsume() {
        return _hpConsume;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    /**
     * @return Returns the itemConsume.
     */
    public final int[] getItemConsume() {
        return _itemConsume;
    }

    /**
     * @return Returns the itemConsumeId.
     */
    public final int[] getItemConsumeId() {
        return _itemConsumeId;
    }

    /**
     * @return Returns the level.
     */
    public final short getLevel() {
        return _level;
    }

    public final short getBaseLevel() {
        return _baseLevel;
    }

    public final void setBaseLevel(short baseLevel) {
        _baseLevel = baseLevel;
    }

    public final int getLevelModifier() {
        return _levelModifier;
    }

    public void setLevelModifier(int val) {
        _levelModifier = val;
    }

    public final int getMagicLevel() {
        return _magicLevel;
    }

    public int getMatak() {
        return _matak;
    }

    public int getMinPledgeClass() {
        return _minPledgeClass;
    }

    public int getMinRank() {
        return _minRank;
    }

    /**
     * @return Returns the mpConsume as _mpConsume1 + _mpConsume2.
     */
    public final double getMpConsume() {
        return getMpConsume1() + getMpConsume2();
    }

    /**
     * @return Returns the mpConsume1.
     */
    public final double getMpConsume1() {
        return _mpConsume1;
    }

    /**
     * @return Returns the mpConsume2.
     */
    public final double getMpConsume2() {
        return _mpConsume2;
    }

    public final int getLevelLearn() {
        return _levelLearn;
    }

    /**
     * @return Returns the name.
     */
    public final String getName() {
        return _name;
    }

    public int getNegatePower() {
        return _negatePower;
    }

    public int getNegateSkill() {
        return _negateSkill;
    }

    public NextAction getNextAction() {
        return _nextAction;
    }

    public void setNextAction(NextAction action) {
        _nextAction = action;
    }

    public int getNpcId() {
        return _npcId;
    }

    public int getNumCharges() {
        return _numCharges;
    }

    public final double getPower(L2Character target) {
        if (target != null) {
            if (target.isPlayable())
                return getPowerPvP();
            if (target.isMonster())
                return getPowerPvE();
        }
        return getPower();
    }

    public final double getPower() {
        return _power;
    }

    public final double getPower2() {
        return _power2;
    }

    public final double getPowerPvP() {
        return _powerPvP != 0 ? _powerPvP : _power;
    }

    public final boolean isPowerPvP() {
        return _powerPvP > 0;
    }

    public final double getPowerPvE() {
        return _powerPvE != 0 ? _powerPvE : _power;
    }

    public final long getReuseDelay() {
        return _reuseDelay;
    }

    /**
     * для изменения времени отката из скриптов
     */
    public final void setReuseDelay(long newReuseDelay) {
        _reuseDelay = newReuseDelay;
    }

    public int getSaveVs() {
        return _saveVs;
    }

    public void setSavevs(int vs) {
        _saveVs = vs;
    }

    public final boolean getShieldIgnore() {
        return _isShieldignore;
    }

    public final int getSkillInterruptTime() {
        return _skillInterruptTime;
    }

    public final SkillType getSkillType() {
        return _skillType;
    }

    public int getSoulsConsume() {
        return _soulsConsume;
    }

    public int getSymbolId() {
        return _symbolId;
    }

    public final SkillTargetType getTargetType() {
        return _targetType;
    }

    public final int getWeaponsAllowed() {
        return _weaponsAllowed;
    }

    public double getLethal1() {
        return _lethal1;
    }

    public double getLethal2() {
        return _lethal2;
    }

    public boolean isBlockedByChar(L2Character effected, L2Skill skill) {
        //_log.info("isBlockedByChar["+skill.getId()+"]="+(effected.getListBlockSkill() != null && effected.getListBlockSkill().contains(skill.getId())));
        //Util.test();
        if (effected.getListBlockBuffSlot() != null && effected.getListBlockBuffSlot().contains(skill.getAbnormalType()))
            return true;
        else if (effected.getListBlockSkill() != null && effected.getListBlockSkill().contains(skill.getId()))
            return true;
        return false;
    }

    public final boolean isCancelable() {
        return _isCancelable && !isTransformation() && !isToggle() && getMagicLevel() > 0;
    }

    /**
     * Является ли скилл общим
     */
    public final boolean isCommon() {
        return _isCommon;
    }

    public final int getCriticalRate() {
        return _criticalRate;
    }

    public final boolean isHandler() {
        return _isItemHandler;
    }

    public final boolean isMagic() {
        return _isMagic == 1;
    }

    public final int getMagic() {
        return _isMagic;
    }

    public final boolean isNewbie() {
        return _isNewbie;
    }

    public void setOperateType(SkillOpType type) {
        _operateType = type;
    }

    public final boolean isOnAction() {
        return _operateType == SkillOpType.OP_ON_ACTION;
    }

    public final boolean isOverhit() {
        return _isOverhit;
    }

    public final boolean isActive() {
        return _operateType == SkillOpType.OP_ACTIVE;
    }

    public final boolean isPassive() {
        return _operateType == SkillOpType.OP_PASSIVE;
    }

    public final boolean isLikePassive() {
        return _operateType == SkillOpType.OP_PASSIVE || _operateType == SkillOpType.OP_ON_ACTION;
    }

    public boolean isSaveable() {
        if (!ConfigValue.AltSaveUnsaveable && (isMusic() || _name.startsWith("Herb of")))
            return false;
        return _isSaveable;
    }

    /**
     * На некоторые скиллы и хендлеры предметов скорости каста/атаки не влияет
     * Скилы CA1/CA5 тоже имеют статик время каста...
     */
    public final boolean isSkillTimePermanent() {
        return _isSkillTimePermanent || getOperateType() == OperateType.CA1 || getOperateType() == OperateType.CA5;
    }

    public final boolean isReuseDelayPermanent() {
        return _isReuseDelayPermanent;
    }

    public void setSkillTimePermanent(boolean time) {
        _isSkillTimePermanent = time;
    }

    public void setReuseDelayPermanent(boolean delay) {
        _isReuseDelayPermanent = delay;
    }

    public boolean isDeathlink() {
        return _deathlink;
    }

    public boolean isBasedOnTargetDebuff() {
        return _basedOnTargetDebuff;
    }

    public boolean isSoulBoost() {
        return _isSoulBoost;
    }

    public boolean isChargeBoost() {
        return _isChargeBoost;
    }

    public boolean isUsingWhileCasting() {
        return _isUsingWhileCasting;
    }

    public boolean isBehind() {
        return _isBehind;
    }

    public boolean isHideStartMessage() {
        return _hideStartMessage;
    }

    public boolean isHideStopMessage() {
        return _hideStopMessage;
    }

    /**
     * Может ли скилл тратить шоты, для хендлеров всегда false
     */
    public boolean isSSPossible() {
        return Boolean.TRUE.equals(_isUseSS) || (_isUseSS == null && !_isItemHandler && !isMusic() && isActive() && !(getTargetType() == SkillTargetType.TARGET_SELF && !isMagic()) && !isTrigger());
    }

    public final boolean isSuicideAttack() {
        return _isSuicideAttack;
    }

    public final boolean isToggle() {
        return _operateType == SkillOpType.OP_TOGGLE;
    }

    public void setAffectRange(int value) {
        _affect_range = value;
    }

    public void setCastRange(int value) {
        _castRange = value;
    }

    public void setDisplayLevel(Short lvl) {
        _displayLevel = lvl;
    }

    public void setHitTime(int hitTime) {
        _hitTime = hitTime;
    }

    public void setHitCancelTime(int hitcancelTime) {
        _skillInterruptTime = hitcancelTime;
    }

    public void setCoolTime(int coolTime) {
        _coolTime = coolTime;
    }

    public void setEffectPoint(int point) {
        _effectPoint = point;
    }

    public void setHpConsume(int hpConsume) {
        _hpConsume = hpConsume;
    }

    public void setIsMagic(int isMagic) {
        _isMagic = isMagic;
    }

    public final void setMagicLevel(int newlevel) {
        _magicLevel = newlevel;
    }

    public void setMpConsume1(double mpConsume1) {
        _mpConsume1 = mpConsume1;
    }

    public void setMpConsume2(double mpConsume2) {
        _mpConsume2 = mpConsume2;
    }

    public void setLevelLearn(int level) {
        _levelLearn = level;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setOverhit(final boolean isOverhit) {
        _isOverhit = isOverhit;
    }

    public final void setPower(double power) {
        _power = power;
    }

    public boolean isItemSkill() {
        return _name.contains("Item Skill") || _name.contains("Talisman");
    }

    public int getReferenceItemId() {
        return _refId;
    }

    public int getReferenceConsume() {
        return _refConsume;
    }

    @Override
    public String toString() {
        return _name + "[id=" + _id + ",lvl=" + _level + "]";
    }

    public abstract void useSkill(L2Character activeChar, GArray<L2Character> targets);

    /**
     * Такие скиллы не аггрят цель, и не флагают чара, но являются "плохими"
     */
    public boolean isAI() {
        switch (_skillType) {
            case SOWING:
                return true;
            default:
                return false;
        }
    }

    public boolean isAoE() {
        switch (_targetType) {
            case TARGET_AREA:
            case TARGET_AREA_AIM_CORPSE:
            case TARGET_AURA:
            case TARGET_PET_AURA:
            case TARGET_MULTIFACE:
            case TARGET_MULTIFACE_AURA:
            case TARGET_TUNNEL:
            case TARGET_TUNNEL_SELF:
                return true;
            default:
                return false;
        }
    }

    public boolean isNotTargetAoE(L2Character caster) {
        switch (_targetType) {
            case TARGET_AURA:
            case TARGET_MULTIFACE_AURA:
            case TARGET_TUNNEL_SELF:
                if (isOffensive())
                    return !caster.isInZonePeace();
                return true;
            case TARGET_ALLY:
            case TARGET_CLAN:
            case TARGET_CLAN_ONLY:
            case TARGET_COMMAND_CHANEL:
            case TARGET_PARTY:
                return true;
            case TARGET_ONE:
                return caster.p_party_buff.get() && !isOffensive();
            default:
                return false;
        }
    }

    public boolean isOffensive() {
        if (_isOffensive != null)
            return _isOffensive;

        switch (_skillType) {
            case BLEED:
            case DEBUFF:
            case DOT:
            case DRAIN:
            case DRAIN_SOUL:
            case LETHAL_SHOT:
            case MANADAM:
            case MDAM:
            case MDOT:
            case MUTE:
            case PARALYZE:
            case PDAM:
            case RDAM:
            case CPDAM:
            case POISON:
            case ROOT:
            case SLEEP:
            case SOULSHOT:
            case SPIRITSHOT:
            case SPOIL:
            case STUN:
            case SWEEP:
            case HARVESTING:
            case TELEPORT_NPC:
            case SOWING:
            case A1:
            case STEAL_BUFF:
            case DISCORD:
            case CUB_MDAM:
            case CUB_DRAIN:
                return true;
            default:
                return false;
        }
    }

    public final boolean isForceUse() {
        return _isForceUse;
    }

    /**
     * Работают только против npc
     */
    public boolean isPvM() {
        if (_isPvm != null)
            return _isPvm;

        switch (_skillType) {
            case DISCORD:
                return true;
            case SWEEP:
                return true;
            default:
                return false;
        }
    }

    public final boolean isPvpSkill() {
        if (_isPvpSkill != null)
            return _isPvpSkill;

        switch (_skillType) {
            case BLEED:
            case DEBUFF:
            case DOT:
            case MDOT:
            case MUTE:
            case PARALYZE:
            case POISON:
            case ROOT:
            case SLEEP:
            case MANADAM:
            case STEAL_BUFF:
            case A1:
                return true;
            default:
                return false;
        }
    }

    public boolean isMusic() {
        return _isMusic;
    }

    public boolean isCleanse() {
        return _isCleanse;
    }

    public boolean isTrigger() {
        return _isTrigger;
    }

    public boolean isCancel() {
        return _isCancel;
    }

    public boolean isStaticHeal() {
        return _isStaticHeal;
    }

    public boolean oneTarget(L2Character caster) {
        switch (_targetType) {
            case TARGET_CORPSE:
            case TARGET_CORPSE_PLAYER:
            case TARGET_HOLY:
            case TARGET_FLAGPOLE:
            case TARGET_ITEM:
            case TARGET_NONE:
            case TARGET_PARTY_ONE:
            case TARGET_PET:
            case TARGET_OWNER:
            case TARGET_OWNER_PET:
            case TARGET_ENEMY_PET:
            case TARGET_ENEMY_SUMMON:
            case TARGET_ENEMY_SERVITOR:
            case TARGET_SELF:
            case TARGET_UNLOCKABLE:
            case TARGET_CHEST:
            case TARGET_SIEGE:
                return true;
            case TARGET_ONE:
                return !caster.p_party_buff.get() || isOffensive();
            default:
                return false;
        }
    }

    public int getCancelTarget() {
        return _cancelTarget;
    }

    public boolean isSkillInterrupt() {
        return _skillInterrupt;
    }

    public boolean isNotUsedByAI() {
        return _isNotUsedByAI;
    }

    public boolean isNotAffectedByMute() {
        return _isNotAffectedByMute;
    }

    public boolean flyingTransformUsage() {
        return _flyingTransformUsage;
    }

    public int getCastCount() {
        return _castCount;
    }

    public int getEnchantLevelCount() {
        return _enchantLevelCount;
    }

    public void setEnchantLevelCount(int count) {
        _enchantLevelCount = count;
    }

    public boolean isClanSkill() {
        return _id >= 370 && _id <= 391 || _id >= 611 && _id <= 616;
    }

    public boolean isBaseTransformation() //Inquisitor, Vanguard, Final Form...
    {
        return _id >= 810 && _id <= 813 || _id >= 1520 && _id <= 1522 || _id == 538;
    }

    public boolean isSummonerTransformation() // Spirit of the Cat etc
    {
        return _id >= 929 && _id <= 931;
    }

    public double getSimpleDamage(L2Character attacker, L2Character target) {
        if (isMagic()) {
            // магический урон
            double mAtk = attacker.getMAtk(target, this);
            double mdef = target.getMDef(null, this);
            double power = getPower();
            int sps = attacker.getChargedSpiritShot() > 0 && isSSPossible() ? attacker.getChargedSpiritShot() * 2 : 1;
            return 91 * power * Math.sqrt(sps * mAtk) / mdef;
        }
        // физический урон
        double pAtk = attacker.getPAtk(target);
        double pdef = target.getPDef(attacker);
        double power = getPower();
        int ss = attacker.getChargedSoulShot() && isSSPossible() ? 2 : 1;
        return ss * (pAtk + power) * 70. / pdef;
    }

    public long getReuseForMonsters() {
        long min = 1000;
        switch (_skillType) {
            case PARALYZE:
            case DEBUFF:
            case STEAL_BUFF:
                min = 10000;
                break;
            case MUTE:
            case ROOT:
            case SLEEP:
            case STUN:
                min = 5000;
                break;
        }
        return Math.max(Math.max(_hitTime + _coolTime, _reuseDelay), min);
    }

    public double getAbsorbPart() {
        return _absorbPart;
    }

    public int getAbsorbPartStatic() {
        return _absorbPartStatic;
    }

    public boolean checkSkillAbnormal(L2Character effected) {
        return checkSkillAbnormal(effected, true, -2);
    }

    public boolean checkSkillAbnormal(L2Character effected, boolean c_exit, int obj_id) {
        _effect_loc.lock();
        try {
            int exit = -1;
            if (isToggle())
                exit = getId();
            if (getAbnormalType() != SkillAbnormalType.none)
                for (L2Effect e : effected.getEffectList().getAllEffects()) {
                    if (e._obj_id == obj_id) {
                        Util.test(effected.getName(), null, "e[" + obj_id + "]=" + e, "checkSkillAbnormal");
                        continue;
                    }
                    if (e.getAbnormalType() == getAbnormalType() && e.getAbnormalLv() > getAbnormalLv())
                        return true;
                    else if (e.getAbnormalType() == getAbnormalType() && e.getAbnormalLv() <= getAbnormalLv()) {
                        exit = e.getSkill().getId();
                        break;
                    }
                }
            if (c_exit)
                try {
                    if (exit > -1) {
                        GArray<L2Effect> effs = effected.getEffectList().getEffectsBySkillId(exit);
                        if (effs != null) {
                            L2Skill sk = effs.get(0).getSkill();
                            if (getAbnormalInstant() == 1 && sk.getAbnormalInstant() < 1)
                                effected.getEffectList().addNextRun(sk.getAbnormalType(), exit);
                            else
                                for (L2Effect e : effs)
                                    if (e != null)
                                        e.exit(false, true);
                        }
                    }
                } catch (Exception e) {
                    return true;
                }
        } finally {
            _effect_loc.unlock();
        }
        return false;
    }

    public static void broadcastUseAnimation(L2Skill skill, L2Character user, GArray<L2Character> targets) {
        int displayId = 0, displayLevel = 0;

        if (skill.getEffectTemplates() != null) {
            displayId = skill.getEffectTemplates()[0]._displayId;
            displayLevel = skill.getEffectTemplates()[0]._displayLevel;
        }

        if (displayId == 0)
            displayId = skill.getDisplayId();
        if (displayLevel == 0)
            displayLevel = skill.getDisplayLevel();

        for (L2Character cha : targets)
            user.broadcastSkill(new MagicSkillUse(user, cha, displayId, displayLevel, 0, 0), true);
    }

    public boolean isOlympiadEnabled() {
        return _isOlympiadEnabled;
    }

    public void setIsOlympiadUse(boolean use) {
        _isOlympiadEnabled = use;
    }

    /**
     * Жрет много памяти (_set), включить только если будет необходимость
     * public L2Skill clone()
     * {
     * L2Skill skill = getSkillType().makeSkill(_set);
     * // Поля, перечисленные ниже, могут не совпадать с _set, поэтому обновляются отдельно
     * // Необходимо сверять этот список с SkillTable.loadSqlSkills()
     * skill.setPower(_power);
     * skill.setBaseLevel(_baseLevel);
     * skill.setMagicLevel(_magicLevel);
     * skill.setCastRange(_castRange);
     * skill.setName(_name);
     * skill.setHitTime(_hitTime);
     * skill.setHitCancelTime(_skillInterruptTime);
     * skill.setIsMagic(getMagic());
     * skill.setOverhit(_isOverhit);
     * skill.setHpConsume(getHpConsume());
     * skill.setMpConsume1(getMpConsume1());
     * skill.setMpConsume2(getMpConsume2());
     * return skill;
     * }
     */
    public boolean hasEffects() {
        return _effectTemplates != null && _effectTemplates.length > 0;
    }

    private int _isNotUse = 0;

    /**
     * В будущем пригодится мб...
     * Если стоит 0 то скилл можно юзать, если стоит 1 то скилл нельзя юзать он будет серым...
     **/
    public int isNotUse() {
        return _isNotUse;
    }

    public boolean isPenalty() {
        return _id == 6209 || _id == 6213 || _id == 4270 || _id == 4269 || _id == 4268 || _id == 5076;
    }

    public L2Skill setNotUse(int isNotUse) {
        _isNotUse = isNotUse;
        return this;
    }

    private SkillTrait _trait;

    public void setTrait(SkillTrait trait) {
        _trait = trait;
    }

    public SkillTrait getTraitType() {
        return _trait;
    }

    private SkillAbnormalType _abnormalType;

    public void setAbnormalType(SkillAbnormalType abnormalType) {
        _abnormalType = abnormalType;
    }

    public SkillAbnormalType getAbnormalType() {
        return _abnormalType;
    }

    private int _abnormalLv = 0;

    public void setAbnormalLv(int value) {
        _abnormalLv = value;
    }

    public int getAbnormalLv() {
        return _abnormalLv;
    }

    private int _abnormal_time = 0;

    public void setAbnormalTime(int value) {
        _abnormal_time = value;
    }

    public int getAbnormalTime() {
        return _abnormal_time;
    }

    private int _abnormal_time2 = 0;

    public void setAbnormalTime2(int value) {
        _abnormal_time2 = value;
    }

    public int getAbnormalTime2() {
        return _abnormal_time2;
    }

    private int _abnormal_instant = 0;

    public void setAbnormalInstant(int value) {
        _abnormal_instant = value;
    }

    public int getAbnormalInstant() {
        return _abnormal_instant;
    }

    private int _buff_protect_level = 0;

    public void setBuffProtectLevel(int value) {
        _buff_protect_level = value;
    }

    public int getBuffProtectLevel() {
        return _buff_protect_level;
    }

    private boolean _isHealSkill = false;

    public void setHealSkill(boolean value) {
        _isHealSkill = value;
    }

    public boolean isHealSkill() {
        return _isHealSkill;
    }

    private int _effectiveRange = -1;

    public void setEffectiveRange(int value) {
        _effectiveRange = value;
    }

    public int getEffectiveRange() {
        return _effectiveRange;
    }

    private int _irreplaceableBuff = 0;

    public void setIrreplaceableBuff(int value) {
        _irreplaceableBuff = value;
    }

    public int getIrreplaceableBuff() {
        return _irreplaceableBuff;
    }

    private int _effPoint = 0;

    public void setEffPoint(int value) {
        _effPoint = value;
    }

    public int getEffPoint() {
        return _effPoint;
    }

    private String _icon = "";

    public void setIcon(String value) {
        _icon = value;
    }

    public String getIcon() {
        return _icon;
    }

    protected int _energyConsume;

    public int getEnergyConsume() {
        return _energyConsume;
    }

    public AbnormalVisualEffect abnormal_visual_effect = AbnormalVisualEffect.ave_none;
    public AbnormalVisualEffect abnormal_visual_effect2 = AbnormalVisualEffect.ave_none;

    private OperateType operate_type;

    public void setOperateType(OperateType ot) {
        operate_type = ot;
    }

    public OperateType getOperateType() {
        return operate_type;
    }

    public boolean isTransformation() {
        return false;
    }

    public boolean isOfflineTime() {
        return _offlineTime;
    }
}