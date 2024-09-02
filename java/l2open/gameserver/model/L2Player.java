package l2open.gameserver.model;

import javolution.util.FastMap;
import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigSystem;
import l2open.config.ConfigValue;
import l2open.database.mysql;
import l2open.extensions.Bonus;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.network.SendablePacket;
import l2open.extensions.scripts.Events;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.Scripts;
import l2open.extensions.scripts.Scripts.ScriptClassAndMethod;
import l2open.gameserver.GameTimeController;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.ai.L2CharacterAI;
import l2open.gameserver.ai.L2PlayableAI;
import l2open.gameserver.ai.L2PlayableAI.nextAction;
import l2open.gameserver.ai.L2PlayerAI;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.clientpackets.EnterWorld;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.BB.Forum;
import l2open.gameserver.communitybbs.CommunityBoard;
import l2open.gameserver.communitybbs.Manager.ForumsBBSManager;
import l2open.gameserver.communitybbs.PartyMaker.PartyMaker;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.ClanHallManager;
import l2open.gameserver.instancemanager.CoupleManager;
import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.instancemanager.DimensionalRiftManager;
import l2open.gameserver.instancemanager.FortressManager;
import l2open.gameserver.instancemanager.FortressSiegeManager;
import l2open.gameserver.instancemanager.HandysBlockCheckerManager;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.PartyRoomManager;
import l2open.gameserver.instancemanager.PlayerLevelRewardManager;
import l2open.gameserver.instancemanager.PlayerManager;
import l2open.gameserver.instancemanager.PlayerRewardManager;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.listener.PlayerListenerList;
import l2open.gameserver.listener.actor.player.OnAnswerListener;
import l2open.gameserver.loginservercon.LSConnection;
import l2open.gameserver.loginservercon.gspackets.ChangeAccessLevel;
import l2open.gameserver.model.BypassManager.BypassType;
import l2open.gameserver.model.BypassManager.DecodedBypass;
import l2open.gameserver.model.L2Multisell.MultiSellListContainer;
import l2open.gameserver.model.L2ObjectTasks.AutoCpTask;
import l2open.gameserver.model.L2ObjectTasks.BonusTask;
import l2open.gameserver.model.L2ObjectTasks.BonusTask2;
import l2open.gameserver.model.L2ObjectTasks.BotCheck;
import l2open.gameserver.model.L2ObjectTasks.BroadcastCharInfoTask;
import l2open.gameserver.model.L2ObjectTasks.EnchantResetCount;
import l2open.gameserver.model.L2ObjectTasks.EndSitDownTask;
import l2open.gameserver.model.L2ObjectTasks.EndStandUpTask;
import l2open.gameserver.model.L2ObjectTasks.InventoryEnableTask;
import l2open.gameserver.model.L2ObjectTasks.KickTask;
import l2open.gameserver.model.L2ObjectTasks.LookingForFishTask;
import l2open.gameserver.model.L2ObjectTasks.PvPFlagTask;
import l2open.gameserver.model.L2ObjectTasks.ReturnTerritoryFlagTask;
import l2open.gameserver.model.L2ObjectTasks.TeleportTask;
import l2open.gameserver.model.L2ObjectTasks.UserInfoTask;
import l2open.gameserver.model.L2ObjectTasks.WaterTask;
import l2open.gameserver.model.L2Skill.AddedSkill;
import l2open.gameserver.model.L2Skill.SkillTargetType;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.barahlo.CBBuffSch;
import l2open.gameserver.model.barahlo.CBBuffSchemePerform;
import l2open.gameserver.model.barahlo.CBTpSch;
import l2open.gameserver.model.barahlo.ProtectFunction;
import l2open.gameserver.model.barahlo.academ.Academicians;
import l2open.gameserver.model.barahlo.academ.AcademiciansStorage;
import l2open.gameserver.model.barahlo.academ.AcademyRequest;
import l2open.gameserver.model.barahlo.academ.AcademyStorage;
import l2open.gameserver.model.barahlo.academ.dao.AcademiciansDAO;
import l2open.gameserver.model.barahlo.academ.dao.AcademyRequestDAO;
import l2open.gameserver.model.barahlo.attainment.Attainment;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.base.L2EnchantSkillLearn;
import l2open.gameserver.model.base.PlayerAccess;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.base.Transaction.TransactionType;
import l2open.gameserver.model.entity.BlockCheckerEngine;
import l2open.gameserver.model.entity.DimensionalRift;
import l2open.gameserver.model.entity.Duel;
import l2open.gameserver.model.entity.Duel.DuelState;
import l2open.gameserver.model.entity.NevitBlessing;
import l2open.gameserver.model.entity.Recommendation;
import l2open.gameserver.model.entity.SevenSignsFestival.DarknessFestival;
import l2open.gameserver.model.entity.olympiad.CompType;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.entity.olympiad.OlympiadGame;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.residence.ResidenceType;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.gameserver.model.entity.vehicle.L2Ship;
import l2open.gameserver.model.entity.vehicle.L2Vehicle;
import l2open.gameserver.model.instances.L2AgathionInstance;
import l2open.gameserver.model.instances.L2ClanHallManagerInstance;
import l2open.gameserver.model.instances.L2DecoyInstance;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2FestivalMonsterInstance;
import l2open.gameserver.model.instances.L2GuardInstance;
import l2open.gameserver.model.instances.L2HennaInstance;
import l2open.gameserver.model.instances.L2MinionInstance;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2PetInstance;
import l2open.gameserver.model.instances.L2StaticObjectInstance;
import l2open.gameserver.model.instances.L2TamedBeastInstance;
import l2open.gameserver.model.instances.L2TerritoryFlagInstance;
import l2open.gameserver.model.instances.L2TrapInstance;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemLocation;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.model.items.PcFreight;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.model.items.PcWarehouse;
import l2open.gameserver.model.items.Warehouse;
import l2open.gameserver.model.items.Warehouse.WarehouseType;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestEventType;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.AbnormalStatusUpdate;
import l2open.gameserver.serverpackets.BlockList;
import l2open.gameserver.serverpackets.CameraMode;
import l2open.gameserver.serverpackets.ChangeWaitType;
import l2open.gameserver.serverpackets.CharInfo;
import l2open.gameserver.serverpackets.CharMoveToLocation;
import l2open.gameserver.serverpackets.ConfirmDlg;
import l2open.gameserver.serverpackets.DeleteObject;
import l2open.gameserver.serverpackets.DropItem;
import l2open.gameserver.serverpackets.EnchantResult;
import l2open.gameserver.serverpackets.EtcStatusUpdate;
import l2open.gameserver.serverpackets.ExAirShipInfo;
import l2open.gameserver.serverpackets.ExAutoSoulShot;
import l2open.gameserver.serverpackets.ExBR_AgathionEnergyInfoPacket;
import l2open.gameserver.serverpackets.ExBasicActionList;
import l2open.gameserver.serverpackets.ExBlockAddResult;
import l2open.gameserver.serverpackets.ExBlockRemoveResult;
import l2open.gameserver.serverpackets.ExBrExtraUserInfo;
import l2open.gameserver.serverpackets.ExChangeNpcState;
import l2open.gameserver.serverpackets.ExDominionWarStart;
import l2open.gameserver.serverpackets.ExDuelUpdateUserInfo;
import l2open.gameserver.serverpackets.ExFishingEnd;
import l2open.gameserver.serverpackets.ExFishingStart;
import l2open.gameserver.serverpackets.ExGetOnAirShip;
import l2open.gameserver.serverpackets.ExMoveToLocationAirShip;
import l2open.gameserver.serverpackets.ExOlympiadMatchEnd;
import l2open.gameserver.serverpackets.ExOlympiadMode;
import l2open.gameserver.serverpackets.ExOlympiadSpelledInfo;
import l2open.gameserver.serverpackets.ExPCCafePointInfo;
import l2open.gameserver.serverpackets.ExSetCompassZoneCode;
import l2open.gameserver.serverpackets.ExStartScenePlayer;
import l2open.gameserver.serverpackets.ExStorageMaxCount;
import l2open.gameserver.serverpackets.ExUseSharedGroupItem;
import l2open.gameserver.serverpackets.ExVitalityEffectInfo;
import l2open.gameserver.serverpackets.ExVoteSystemInfo;
import l2open.gameserver.serverpackets.GetItem;
import l2open.gameserver.serverpackets.GetOnVehicle;
import l2open.gameserver.serverpackets.HennaInfo;
import l2open.gameserver.serverpackets.InventoryUpdate;
import l2open.gameserver.serverpackets.ItemList;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.MyTargetSelected;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.NpcInfo;
import l2open.gameserver.serverpackets.ObserverEnd;
import l2open.gameserver.serverpackets.ObserverStart;
import l2open.gameserver.serverpackets.PartySmallWindowUpdate;
import l2open.gameserver.serverpackets.PartySpelled;
import l2open.gameserver.serverpackets.PetInfo;
import l2open.gameserver.serverpackets.PetItemList;
import l2open.gameserver.serverpackets.PlaySound;
import l2open.gameserver.serverpackets.PledgeShowMemberListDelete;
import l2open.gameserver.serverpackets.PledgeShowMemberListUpdate;
import l2open.gameserver.serverpackets.PrivateStoreListBuy;
import l2open.gameserver.serverpackets.PrivateStoreListSell;
import l2open.gameserver.serverpackets.PrivateStoreMsgBuy;
import l2open.gameserver.serverpackets.PrivateStoreMsgSell;
import l2open.gameserver.serverpackets.QuestList;
import l2open.gameserver.serverpackets.RecipeShopMsg;
import l2open.gameserver.serverpackets.RecipeShopSellList;
import l2open.gameserver.serverpackets.RelationChanged;
import l2open.gameserver.serverpackets.Ride;
import l2open.gameserver.serverpackets.Say2;
import l2open.gameserver.serverpackets.SendTradeDone;
import l2open.gameserver.serverpackets.SetupGauge;
import l2open.gameserver.serverpackets.ShortBuffStatusUpdate;
import l2open.gameserver.serverpackets.ShortCutInit;
import l2open.gameserver.serverpackets.ShortCutRegister;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.serverpackets.SkillCoolTime;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.serverpackets.Snoop;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.serverpackets.SpawnItem;
import l2open.gameserver.serverpackets.SpawnItemPoly;
import l2open.gameserver.serverpackets.StaticObject;
import l2open.gameserver.serverpackets.StatusUpdate;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.serverpackets.TargetSelected;
import l2open.gameserver.serverpackets.TargetUnselected;
import l2open.gameserver.serverpackets.TeleportToLocation;
import l2open.gameserver.serverpackets.UserInfo;
import l2open.gameserver.serverpackets.ValidateLocation;
import l2open.gameserver.serverpackets.VehicleDeparture;
import l2open.gameserver.serverpackets.VehicleInfo;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.SkillAbnormalType;
import l2open.gameserver.skills.SkillTimeStamp;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.skills.skillclasses.Call;
import l2open.gameserver.skills.skillclasses.Charge;
import l2open.gameserver.skills.skillclasses.Transformation;
import l2open.gameserver.tables.CharTemplateTable;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.MapRegion;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.PetDataTable;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.taskmanager.AttainmentTaskManager;
import l2open.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2open.gameserver.taskmanager.VitalityManager;
import l2open.gameserver.templates.L2Armor;
import l2open.gameserver.templates.L2Armor.ArmorType;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2PlayerTemplate;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.ArrayUtils;
import l2open.util.AutoBan;
import l2open.util.EffectsComparator;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Log;
import l2open.util.RateService;
import l2open.util.Rnd;
import l2open.util.Util;
import l2open.util.reference.HardReference;
import l2open.util.РазноеГовно;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import static l2open.gameserver.model.L2Zone.ZoneType.Siege;
import static l2open.gameserver.model.L2Zone.ZoneType.damage;
import static l2open.gameserver.model.L2Zone.ZoneType.epic;
import static l2open.gameserver.model.L2Zone.ZoneType.instant_skill;
import static l2open.gameserver.model.L2Zone.ZoneType.no_landing;
import static l2open.gameserver.model.L2Zone.ZoneType.other;
import static l2open.gameserver.model.L2Zone.ZoneType.peace_zone;
import static l2open.gameserver.model.L2Zone.ZoneType.poison;
import static l2open.gameserver.model.L2Zone.ZoneType.set_fame;
import static l2open.gameserver.model.L2Zone.ZoneType.ssq_zone;
import static l2open.gameserver.model.L2Zone.ZoneType.swamp;

public class L2Player extends L2Playable {
    protected static final Logger _log = Logger.getLogger(L2Player.class.getName());

    public HashMap<Integer, L2SubClass> _classlist = new HashMap<Integer, L2SubClass>(4);

    public static final short STORE_PRIVATE_NONE = 0;
    public static final short STORE_PRIVATE_SELL = 1;
    public static final short STORE_PRIVATE_BUY = 3;
    public static final short STORE_PRIVATE_MANUFACTURE = 5;
    public static final short STORE_OBSERVING_GAMES = 7;
    public static final short STORE_PRIVATE_SELL_PACKAGE = 8;
    public static final short STORE_PRIVATE_BUFF = 9;

    /*
1385	u,Кочевник\0
1386	u,Вассал\0
1387	u,Ученик\0
1388	u,Наследник\0
1389	u,Рыцарь\0
1390	u,Старейшина\0
1391	u,Барон\0
1392	u,Виконт\0
1393	u,Граф\0
1394	u,Маркиз\0
1395	u,Герцог\0
1396	u,Великий герцог\0
1397	u,Король\0
1398	u,Император\0
*/
    public static final int RANK_VAGABOND = 0; // Кочевник
    public static final int RANK_VASSAL = 1; // Вассал
    public static final int RANK_HEIR = 2; // Наследник
    public static final int RANK_KNIGHT = 3; // Рыцарь
    public static final int RANK_WISEMAN = 4; // Старейшина
    public static final int RANK_BARON = 5; // Барон
    public static final int RANK_VISCOUNT = 6; // Виконт
    public static final int RANK_COUNT = 7; // Граф
    public static final int RANK_MARQUIS = 8; // Маркиз
    public static final int RANK_DUKE = 9; // Герцог
    public static final int RANK_GRAND_DUKE = 10; // Великий герцог
    public static final int RANK_DISTINGUISHED_KING = 11; // Король
    public static final int RANK_EMPEROR = 12; // Император

    public static final int LANG_ENG = 0;
    public static final int LANG_RUS = 1;
    public static final int LANG_UNK = -1;

    /**
     * The table containing all minimum level needed for each Expertise (None, D, C, B, A, S, S80, S84)
     */
    public static final int[] EXPERTISE_LEVELS = {
            //
            0, //NONE
            20, //D
            40, //C
            52, //B
            61, //A
            76, //S
            80, //S80
            84, //S84
            Integer.MAX_VALUE, // затычка
    };

    protected ClassId _skillLearningClassId;

    protected L2GameClient _connection;
    public String _accountName;

    protected int _karma, _pkKills, _pvpKills;
    protected int _face, _hairStyle, _hairColor;
    protected int _recomHave, _recomLeft, _fame;
    //protected int _deleteTimer;
    protected int _partyMatchingLevels, _partyMatchingRegion;
    protected Integer _partyRoom = 0;

    public long _createTime, _onlineTime, _onlineBeginTime, _leaveClanTime, _deleteClanTime, _NoChannel, _NoChannelBegin;

    public long _last_active = 0;

    /**
     * The Color of players name / title (white is 0xFFFFFF)
     */
    protected int _nameColor, _titlecolor;

    protected int _vitalityLevel = -1;
    protected double _vitality = ConfigValue.VitalityMax;
    protected int _curWeightPenalty = 0;

    protected boolean _relax;

    boolean sittingTaskLaunched;

    /**
     * Time counter when L2Player is sitting
     */
    protected int _waitTimeWhenSit;

    public boolean AutoLoot = ConfigValue.AutoLoot, AutoLootHerbs = ConfigValue.AutoLootHerbs;
    public boolean AutoLootSpecial = false;

    protected PcInventory _inventory = new PcInventory(this);
    protected PcWarehouse _warehouse = new PcWarehouse(this);
    protected PcFreight _freight = new PcFreight(this);
    public final BookMarkList bookmarks = new BookMarkList(this, 0);

    /**
     * The table containing all L2RecipeList of the L2Player
     */
    protected final Map<Integer, L2Recipe> _recipebook = new TreeMap<Integer, L2Recipe>();
    protected final Map<Integer, L2Recipe> _commonrecipebook = new TreeMap<Integer, L2Recipe>();

    /**
     * Premium Items
     */
    protected Map<Integer, PremiumItem> _premiumItems = new TreeMap<Integer, PremiumItem>();

    /**
     * The table containing all Quests began by the L2Player
     */
    protected final HashMap<String, QuestState> _quests = new HashMap<String, QuestState>();

    protected final HashMap<Integer, Long> _itemBay = new HashMap<Integer, Long>();

    /**
     * The list containing all shortCuts of this L2Player
     */
    protected final ShortCuts _shortCuts = new ShortCuts(this);

    /**
     * The list containing all macroses of this L2Player
     */
    protected final MacroList _macroses = new MacroList(this);

    protected StatsChangeRecorder _statsChangeRecorder;

    public L2Radar radar;

    protected L2TradeList _tradeList;
    protected L2ManufactureList _createList;
    protected ConcurrentLinkedQueue<TradeItem> _sellList, _sellPkgList, _buyList;

    // hennas
    public final L2HennaInstance[] _henna = new L2HennaInstance[3];
    protected short _hennaSTR, _hennaINT, _hennaDEX, _hennaMEN, _hennaWIT, _hennaCON;

    protected L2Party _party;
    protected L2Clan _clan;
    protected int _pledgeClass = 0, _pledgeType = 0, _powerGrade = 0, _lvlJoinedAcademy = 0, _apprentice = 0;

    //GM Stuff
    protected int _accessLevel;
    protected PlayerAccess _playerAccess = new PlayerAccess();
    protected boolean _messageRefusal = false, _tradeRefusal = false, _exchangeRefusal = false, _invisible = false, _blockAll = false;

    /**
     * The protected Store type of the L2Player (STORE_PRIVATE_NONE=0, STORE_PRIVATE_SELL=1, sellmanage=2, STORE_PRIVATE_BUY=3, buymanage=4, STORE_PRIVATE_MANUFACTURE=5)
     */
    protected short _privatestore;

    /**
     * The L2Summon of the L2Player
     */
    protected L2Summon _summon = null;

    protected L2DecoyInstance _decoy = null;

    protected GArray<L2Cubic> cubics = null;
    protected L2AgathionInstance _agathion = null;

    protected Transaction _transaction;

    protected L2ItemInstance _arrowItem;

    /**
     * The fists L2Weapon of the L2Player (used when no weapon is equipped)
     */
    protected L2Weapon _fistsWeaponItem;

    protected long _uptime;

    protected HashMap<Integer, String> _chars = new HashMap<Integer, String>(8);

    public byte updateKnownCounter = 0;

    /**
     * The current higher Expertise of the L2Player (None=0, D=1, C=2, B=3, A=4, S=5, S80=6, S84=7)
     */
    public int expertiseIndex = 0;
    int armorExpertisePenalty = 0;
    int weaponExpertisePenalty = 0;

    protected L2ItemInstance _enchantScroll = null;

    protected WarehouseType _usingWHType;
    protected boolean _isOnline = false;
    protected boolean _isDeleting = false;

    protected boolean _inventoryDisable = false;

    /**
     * The L2NpcInstance corresponding to the last Folk which one the player talked.
     */
    protected L2NpcInstance _lastNpc = null;
    protected String _lastBBS_script_operation = null;

    /**
     * тут храним мультиселл с которым работаем, полезно...
     */
    protected MultiSellListContainer _multisell = null;

    protected ConcurrentSkipListSet<Integer> _activeSoulShots = new ConcurrentSkipListSet<Integer>();

    /**
     * Location before entering Observer Mode
     */
    protected Location _obsLoc = new Location();
    protected L2WorldRegion _observNeighbor;
    protected byte _observerMode = 0;

    public int _telemode = 0;

    /**
     * Эта точка проверяется при нештатном выходе чара, и если не равна null чар возвращается в нее
     * Используется например для возвращения при падении с виверны
     * Поле heading используется для хранения денег возвращаемых при сбое
     */
    public Location _stablePoint = null;

    /**
     * new loto ticket
     **/
    public int _loto[] = new int[5];
    /**
     * new race ticket
     **/
    public int _race[] = new int[2];

    public final FastMap<Integer, String> _blockList = new FastMap<Integer, String>().setShared(true); // characters blocked with '/block <charname>' cmd

    protected boolean _isConnected = true;

    /**
     * Тип хиро:
     * -1 - нету Хиро.
     * 0 - Хиро за олимпиаду.
     * 1 - Хиро за ивенты.
     * 2 - Донат Хиро.
     **/
    protected int _heroType = -1;
    protected int _team = 0;
    protected int _damageMy = 0;
    protected int _checksForTeam = 0;

    // time on login in game
    protected long _lastAccess;

    /**
     * True if the L2Player is in a boat
     */
    protected L2Vehicle _vehicle;
    protected Location _inVehiclePosition;

    protected int _baseClass = -1;
    protected L2SubClass _activeClass = null;
    public int _activeClassId = -1;

    protected Bonus _bonus;
    protected Future<?> _bonusExpiration;
    public Future<?> _bonusExpiration2[] = new Future<?>[14];
    protected Future<?> _pcCafePointsTask;
    public Future<?> _AttainmentTask;

    protected Future<?> _noCarrierTask;

    public boolean _isSitting = false;

    protected boolean _noble = false;
    protected boolean _inOlympiadMode = false;
    protected byte _eventNotUseItem = 0;
    protected boolean _isCreateCommandChannelWithItem = false;
    protected OlympiadGame _olympiadGame = null;
    protected int _olympiadSide = -1;
    public int _olympiadObserveId = -1;

    /**
     * ally with ketra or varka related wars
     */
    protected int _varka = 0;
    protected int _ketra = 0;
    protected int _ram = 0;

    /**
     * The Siege state
     */
    protected int _siegeState = 0;

    protected byte[] _keyBindings;

    public ScheduledFuture<?> _taskWater;
    public ScheduledFuture<?> _forceTask;
    public ScheduledFuture<?> _soulTask;

    public HashMap<Integer, Long> _StatKills;
    public HashMap<Integer, Long> _StatDrop;
    public HashMap<Integer, Long> _StatCraft;

    protected Forum _forumMemo;

    protected int _cursedWeaponEquippedId = 0;

    protected L2Fishing _fishCombat;
    protected boolean _fishing = false;
    protected Location _fishLoc = new Location();
    protected L2ItemInstance _lure = null;
    public ScheduledFuture<?> _taskforfish;
    public ScheduledFuture<?> recVoteTask;
    protected Future<?> _kickTask;

    protected boolean _isInCombatZone;
    protected boolean _isOnSiegeField;
    protected boolean _isInPeaceZone;
    protected boolean _isInSSZone;

    protected boolean _offline = false;

    /**
     * Трансформация
     */
    protected int _transformationId;
    protected int _transformationTemplate;
    protected String _transformationName;

    protected int _pcBangPoints;

    /**
     * Коллекция для временного хранения скилов данной трансформации
     */
    HashMap<Integer, L2Skill> _transformationSkills = new HashMap<Integer, L2Skill>();

    protected int _expandInventory = 0;
    protected int _expandWarehouse = 0;

    protected GArray<String> bypasses = null, bypasses_bbs = null, bypasses_special = null;
    protected static final String NOT_CONNECTED = "<not connected>";
    protected static Map<ClassId, Quest> _classQuests = new HashMap<ClassId, Quest>();
    protected static List<Quest> _breakQuests = new ArrayList<Quest>();

    protected int timeOnline = 0;

    protected long _lastItemAuctionInfoRequest = 0;

    protected int _handysBlockCheckerEventArena = -1;

    public HashMap<Integer, CBBuffSch> _buffSchem = null;
    public HashMap<Integer, CBBuffSchemePerform> _buffSchemePerform = null;
    public HashMap<Integer, CBTpSch> _tpSchem = null;

    public Future<?> _heroTask;

    public long MailSent = 0;
    public long TellChatLaunched = 0;
    public long ShoutChatLaunched = 0;
    public long TradeChatLaunched = 0;
    public long HeroChatLaunched = 0;
    public int ZoneEnteredNoLandingFlying = 0;
    public long _set_fame = -1;
    protected ScheduledFuture<?> _mountFeedTask;
    protected L2Character _mount = null;

    private final Lock reuseSkillLock = new ReentrantLock();

    /**
     * Конструктор для L2Player. Напрямую не вызывается, для создания игрока используется PlayerManager.create
     */
    public L2Player(final int objectId, final L2PlayerTemplate template, final String accountName, int bot) {
        super(objectId, template);
        // Create an AI
        setAI(new L2PlayerAI(this));

        _accountName = accountName;
        _nameColor = 0xFFFFFF;
        _titlecolor = 0xFFFF77;
        _baseClass = getClassId().getId();
        _postFriends = Containers.emptyIntObjectMap();
    }

    /**
     * Constructor<?> of L2Player (use L2Character constructor).<BR><BR>
     *
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Call the L2Character constructor to create an empty _skills slot and copy basic Calculator set to this L2Player </li>
     * <li>Create a L2Radar object</li>
     * <li>Retrieve from the database all items of this L2Player and add them to _inventory </li>
     *
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SET the account name of the L2Player</B></FONT><BR><BR>
     *
     * @param objectId Identifier of the object to initialized
     * @param template The L2PlayerTemplate to apply to the L2Player
     */
    public L2Player(final int objectId, final L2PlayerTemplate template, int bot) {
        this(objectId, template, null, bot);

        getInventory().restore();

        // Create a L2Radar object
        radar = new L2Radar(this);

        if (!ConfigValue.EverybodyHasAdminRights || bot > 0)
            setPlayerAccess(ConfigSystem.gmlist.get(objectId));
        else {
            if (ConfigSystem.gmlist.containsKey(objectId))
                setPlayerAccess(ConfigSystem.gmlist.get(objectId));
            else
                setPlayerAccess(ConfigSystem.gmlist.get(new Integer(0)));
        }

        // Retrieve from the database all macroses of this L2Player and add them to _macroses
        _macroses.restore();
    }

    public String getAccountName() {
        if (_connection == null)
            return _accountName;
        return _connection.getLoginName();
    }

    public String getIP() {
        if (_connection == null)
            return NOT_CONNECTED;
        return _connection.getIpAddr();
    }

    public int getQuestInventoryLimit() {
        return ConfigValue.MaximumQuestInventorySlot;
    }

    /**
     * Возвращает список персонажей на аккаунте, за исключением текущего
     *
     * @return Список персонажей
     */
    public HashMap<Integer, String> getAccountChars() {
        return _chars;
    }

    @Override
    public final L2PlayerTemplate getTemplate() {
        return (L2PlayerTemplate) _template;
    }

    @Override
    public L2PlayerTemplate getBaseTemplate() {
        return (L2PlayerTemplate) _baseTemplate;
    }

    public void changeSex() {
        _template = CharTemplateTable.getInstance().getTemplate(getClassId(), getSex() != 1);
    }

    @Override
    public L2PlayableAI getAI() {
        if (_ai == null)
            _ai = new L2PlayerAI(this);
        return (L2PlayableAI) _ai;
    }

    @Override
    public void doAttack(final L2Character target, boolean force) {
        super.doAttack(target, force);

        if (_agathion != null) {
            _agathion.doAction(target);
            _agathion.doSweep(target);
        }

    }

    @Override
    public void doCast(final L2Skill skill, final L2Character target, boolean forceUse) {
        if (skill == null)
            return;

        if (isCombatFlagEquipped() || isTerritoryFlagEquipped()) {
            if (skill.getSkillType() != SkillType.TAKEFORTRESS && skill.getSkillType() != SkillType.TAKEFLAG) {
                sendActionFailed();
                return;
            }
        }

        super.doCast(skill, target, forceUse);

        if (getUseSeed() != 0 && skill.getSkillType() == SkillType.SOWING)
            sendPacket(new ExUseSharedGroupItem(getUseSeed(), getUseSeed(), 5000, 5000));

        if (skill.isOffensive() && target != null) {
            for (L2Cubic cubic : getCubics()) {
                if (cubic.getTargetType().startsWith("target") || cubic.getTargetType().startsWith("by_skill")) {
                    cubic.startAttack(target);
                }
            }
            if (_agathion != null) {
                _agathion.doAction(target);
                _agathion.doSweep(target);
            }
        }
    }

    public void refreshSavedStats() {
        getStatsChangeRecorder().refreshSaves();
    }

    @Override
    public void sendChanges() {
        //Util.test();
        getStatsChangeRecorder().sendChanges();
    }

    public StatsChangeRecorder getStatsChangeRecorder() {
        if (_statsChangeRecorder == null)
            _statsChangeRecorder = new StatsChangeRecorder(this);
        return _statsChangeRecorder;
    }

    @Override
    public final byte getLevel() {
        return _activeClass == null ? 1 : _activeClass.getLevel();
    }

    public final boolean setLevel(final int lvl) {
        if (_activeClass != null)
            _activeClass.setLevel((byte) lvl);
        return lvl == getLevel();
    }

    public byte getSex() {
        return getTemplate().isMale ? (byte) 0 : (byte) 1;
    }

    public int getFace() {
        return _face;
    }

    public void setFace(int face) {
        _face = face;
    }

    public int getHairColor() {
        return _hairColor;
    }

    public void setHairColor(int hairColor) {
        _hairColor = hairColor;
    }

    public int getHairStyle() {
        return _hairStyle;
    }

    public void setHairStyle(int hairStyle) {
        _hairStyle = hairStyle;
    }

    public boolean isInStoreMode() {
        return _privatestore != STORE_PRIVATE_NONE && _privatestore != STORE_OBSERVING_GAMES;
    }

    public void offline() {
        _nameColor = Integer.decode("0x" + ConfigValue.OfflineTradeNameColor);
        setOfflineMode(true);
        clearHateList(false);
        setVar("offline", String.valueOf(System.currentTimeMillis() / 1000));
        if (ConfigValue.OfflineTradeDaysToKick > 0)
            startKickTask(ConfigValue.OfflineTradeDaysToKick * 60 * 60 * 24 * 1000L);
        if (isFestivalParticipant()) {
            L2Party playerParty = getParty();

            if (playerParty != null)
                playerParty.broadcastMessageToPartyMembers(getName() + " has been removed from the upcoming festival.");
        }

        if (getParty() != null)
            getParty().oustPartyMember(this);

        if (getPet() != null && getPet().getNpcId() != PetDataTable.IMPROVED_BABY_KOOKABURRA_ID && getPet().getNpcId() != PetDataTable.IMPROVED_BABY_COUGAR_ID)
            getPet().unSummon();

        CursedWeaponsManager.getInstance().doLogout(this);

        // Убираем чара с реги на оли...
        if (Olympiad.isRegistered(this))
            Olympiad.removeRegistration(getObjectId());

        if (isInOlympiadMode() || getOlympiadGame() != null)
            Olympiad.logoutPlayer(this);

        stopPcBangPointsTask();
        sendPacket(ConfigValue.DellClientIfOffline ? Msg.LeaveWorld : Msg.ServerClose(this));
        setConnected(false);
        setOnlineStatus(true);
        //LSConnection.getInstance().removeAccount(getNetConnection());
        //LSConnection.getInstance().sendPacket(new PlayerLogout(getNetConnection().getLoginName()));
        broadcastUserInfo(true);

        PlayerData.getInstance().store(this, false);
        _connection.OnOfflineTrade();

        setLogout(false);

        if (_connection != null) {
            _connection.setActiveChar(null);
            //_connection.close(new ServerClose()); // не будет кикать клиент...
            setNetConnection(null);
        }
        //TODO освобождать кучу других объектов связанных с игроком не нужных в оффлайне
    }

    /**
     * Сохраняет персонажа в бд и запускает необходимые процедуры.
     *
     * @param shutdown тру при шатдауне
     * @param restart  тру при рестарте. Игнорируется шатдаун.
     * @param kicked   Отобразить у клиента табличку с мессагой о закрытии коннекта, отобрать проклятое оружие.
     * @param instant  Выкидывает моментально, не оставляя чара в игре.
     */
    public void logout(boolean shutdown, boolean restart, boolean kicked, boolean instant) {
        logout(shutdown, restart, kicked, instant, -1);
    }

    public void logout(boolean shutdown, boolean restart, boolean kicked, boolean instant, long time) {
        //Util.test();
        //_log.info("L2Player->: logout["+getName()+"]: shutdown["+shutdown+"] restart["+restart+"] kicked["+kicked+"] instant["+instant+"]");
        if (isLogout())
            return;

        //_log.info("L2Player->: logout["+getName()+"]: 2");
        setLogout(true);
        if (isLogoutStarted())
            return;
        //_log.info("L2Player->: logout["+getName()+"]: 3");

        abortAttack(true, true);
        abortCast(true);

        Log.LogChar(this, Log.Logout, "");

        // Убираем чара с реги на оли...
        if (Olympiad.isRegistered(this)) {
            Olympiad.removeRegistration(getObjectId());
            instant = true;
        }

        // с обсерва в любом случае делаем логаут...
        if (inObserverMode()) {
            leaveObserverMode(Olympiad.getGameBySpectator(this));
            returnFromObserverMode();
            instant = true;
        }

        // с оли в любом случае делаем логаут...
        if (isInOlympiadMode() || getOlympiadGame() != null) {
            Olympiad.logoutPlayer(this);
            instant = true;
        }

        // Сохраняем инфу о джайле
        if (getVar("jailed") != null) {
            long curTime = System.currentTimeMillis() / 1000;
            String[] jailTime = getVar("jailed").split(";");
            long jailTimes = Long.parseLong(jailTime[0]);
            long timeToJail = Long.parseLong(jailTime[1]);
            long jTime = curTime - timeToJail;
            if (jTime < jailTimes) {
                jailTimes = jailTimes - jTime;
                setVar("jailed", jailTimes + ";" + curTime);
            }
        }

        setVar("EnchantCount", _enchantCount, System.currentTimeMillis() + (ConfigValue.EnchantProtectTime * 1000));

        HandyBlockGameClear();
        // Msg.ExRestartClient - 2 таблички появляется (вторая GG Fail), нажатие ок приводит к закрытию клиента
        // Msg.ServerClose - табличка появляется, после нажатия ок переходит к диалогу ввода логина/пароля
        // Msg.LeaveWorld - молча закрывает клиент (используется при выходе из игры)

        if (kicked && ConfigValue.AllowCursedWeapons && ConfigValue.DropCursedWeaponsOnKick)
            if (isCursedWeaponEquipped()) {
                _pvpFlag = 0;
                CursedWeaponsManager.getInstance().dropPlayer(this);
            }

        if (restart) {
            // При рестарте просто обнуляем коннект
            if (instant) {
                //_log.info("L2Player->: logout["+getName()+"]: 4");
                deleteMe();
            } else {
                //_log.info("L2Player->: logout["+getName()+"]: 5");
                if (time == -1)
                    scheduleDelete();
                else
                    scheduleDelete(time);
            }
            if (_connection != null)
                _connection.setActiveChar(null);
        } else {
            L2GameServerPacket sp = shutdown || kicked ? Msg.ServerClose(this) : Msg.LeaveWorld;
            sendPacket(sp);
            if (_connection != null && _connection.getConnection() != null)
                _connection.getConnection().close(sp);
            if (instant) {
                //_log.info("L2Player->: logout["+getName()+"]: 6");
                deleteMe();
            } else {
                //_log.info("L2Player->: logout["+getName()+"]: 7");
                scheduleDelete();
            }
        }

        _connection = null;
        setConnected(false);
        broadcastUserInfo(false);
    }

    public void HandyBlockGameClear() {
        if (getBlockCheckerArena() < 0)
            return;
        else if (isInEvent() == 6) {
            try {
                String back = getVar("backCoords");
                if (back != null) {
                    unsetVar("backCoords");
                    unsetVar("reflection");
                    teleToLocation(new Location(back), 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        teleToLocation(-57478, -60367, -2370);
        setTransformation(0);
        getEffectList().stopAllEffects(true);
        int arena = getBlockCheckerArena();
        int team = HandysBlockCheckerManager.getInstance().getHolder(arena).getPlayerTeam(this);
        HandysBlockCheckerManager.getInstance().removePlayer(this, arena, team);
        // Remove team aura
        setTeam(0, true);
        broadcastCharInfo();
        setBlockCheckerArena((byte) -1);

        // Remove the event items
        PcInventory inv = getInventory();

        if (inv.getCountOf(13787) > 0)
            inv.destroyItemByItemId(13787, inv.getCountOf(13787), true);
        if (inv.getCountOf(13788) > 0)
            inv.destroyItemByItemId(13788, inv.getCountOf(13788), true);
    }

    public void prepareToLogout() {
        if (isFlying() && !checkLandingState())
            setLoc(MapRegion.getTeleToClosestTown(this));

        if (isCastingNow())
            abortCast(true);

        // При логауте автоматом проигрывается дуэль.
        if (getDuel() != null)
            getDuel().onPlayerDefeat(this);

        if (isFestivalParticipant()) {
            L2Party playerParty = getParty();

            if (playerParty != null)
                playerParty.broadcastMessageToPartyMembers(getName() + " has been removed from the upcoming festival.");
        }

        CursedWeaponsManager.getInstance().doLogout(this);

        getRecommendation().stopRecomendationTask();
        if (getEventMaster() != null)
            getEventMaster().onPlayerExit(this);
        getListeners().onExit();
        // Вызов всех хэндлеров, определенных в скриптах
        Object[] script_args = new Object[]{this};
        for (ScriptClassAndMethod handler : Scripts.onPlayerExit)
            callScripts(handler.scriptClass, handler.method, script_args);

        if (_stablePoint != null) {
            teleToLocation(_stablePoint);
            addAdena(_stablePoint.h);
            unsetVar("wyvern_moneyback");
        }

        if (getPet() != null)
            try {
                getPet().unSummon();
            } catch (Throwable t) {
                t.printStackTrace();
                _log.log(Level.WARNING, "prepareToLogout()", t);
            }

        if (isInParty())
            try {
                leaveParty();
            } catch (Throwable t) {
                t.printStackTrace();
                _log.log(Level.WARNING, "prepareToLogout()", t);
            }
    }

    private boolean _logoutStarted = false;

    public boolean isLogoutStarted() {
        return _logoutStarted;
    }

    public void setLogoutStarted(boolean logoutStarted) {
        _logoutStarted = logoutStarted;
    }

    private boolean _logout = false;

    public boolean isLogout() {
        return _logout;
    }

    public void setLogout(boolean logout) {
        _logout = logout;
        if (_noCarrierTask != null) {
            _noCarrierTask.cancel(false);
            _noCarrierTask = null;
        }
    }

    /**
     * @return a table containing all L2RecipeList of the L2Player.<BR><BR>
     */
    public Collection<L2Recipe> getDwarvenRecipeBook() {
        return _recipebook.values();
    }

    public Collection<L2Recipe> getCommonRecipeBook() {
        return _commonrecipebook.values();
    }

    public int recipesCount() {
        return _commonrecipebook.size() + _recipebook.size();
    }

    public boolean hasRecipe(final L2Recipe id) {
        return _recipebook.containsValue(id) || _commonrecipebook.containsValue(id);
    }

    public boolean findRecipe(final int id) {
        return _recipebook.containsKey(id) || _commonrecipebook.containsKey(id);
    }

    /**
     * Add a new L2RecipList to the table _recipebook containing all L2RecipeList of the L2Player
     */
    public void registerRecipe(final L2Recipe recipe, boolean saveDB) {
        if (recipe.isDwarvenRecipe())
            _recipebook.put(recipe.getId(), recipe);
        else
            _commonrecipebook.put(recipe.getId(), recipe);
        if (saveDB)
            mysql.set("REPLACE INTO character_recipebook (char_id, id) VALUES(?,?)", getObjectId(), recipe.getId());
    }

    /**
     * Remove a L2RecipList from the table _recipebook containing all L2RecipeList of the L2Player
     */
    public void unregisterRecipe(final int RecipeID) {
        if (_recipebook.containsKey(RecipeID)) {
            mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", getObjectId(), RecipeID);
            _recipebook.remove(RecipeID);
        } else if (_commonrecipebook.containsKey(RecipeID)) {
            mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", getObjectId(), RecipeID);
            _commonrecipebook.remove(RecipeID);
        } else
            _log.warning("Attempted to remove unknown RecipeList" + RecipeID);
    }

    // ------------------- Quest Engine ----------------------

    public QuestState getQuestState(String quest) {
        return _quests != null ? _quests.get(quest) : null;
    }

    public QuestState getQuestState(int quest) {
        return _quests != null ? _quests.get(QuestManager.getQuest(quest).getName()) : null;
    }

    public QuestState getQuestState(Class<?> quest) {
        return getQuestState(quest.getSimpleName());
    }

    public boolean isQuestCompleted(String quest) {
        QuestState q = getQuestState(quest);
        return q != null && q.isCompleted();
    }

    public boolean isQuestCompleted(Class<?> quest) {
        QuestState q = getQuestState(quest);
        return q != null && q.isCompleted();
    }

    public void setQuestState(QuestState qs) {
        _quests.put(qs.getQuest().getName(), qs);
    }

    public void delQuestState(String quest) {
        _quests.remove(quest);
    }

    public Quest[] getAllActiveQuests() {
        List<Quest> quests = new ArrayList<Quest>();
        for (final QuestState qs : _quests.values())
            if (qs != null && qs.isStarted())
                quests.add(qs.getQuest());
        return quests.toArray(new Quest[quests.size()]);
    }

    public QuestState[] getAllQuestsStates() {
        return _quests.values().toArray(new QuestState[_quests.size()]);
    }

    public List<QuestState> getQuestsForEvent(L2NpcInstance npc, QuestEventType event) {
        List<QuestState> states = new ArrayList<QuestState>();
        Quest[] quests = npc.getTemplate().getEventQuests(event);
        if (quests != null)
            for (Quest quest : quests)
                if (getQuestState(quest.getName()) != null && !getQuestState(quest.getName()).isCompleted())
                    states.add(getQuestState(quest.getName()));
        return states;
    }

    public void processQuestEvent(String quest, String event, L2NpcInstance npc) {
        if (event == null)
            event = "";
        QuestState qs = getQuestState(quest);
        if (qs == null) {
            Quest q = QuestManager.getQuest(quest);
            if (q == null) {
                System.out.println("Quest " + quest + " not found!!!");
                return;
            }
            qs = q.newQuestState(this, Quest.CREATED);
        }
        if (qs == null || qs.isCompleted())
            return;
        qs.getQuest().notifyEvent(event, qs, npc);
        sendPacket(new QuestList(this));
    }

    /**
     * Проверка на переполнение инвентаря и перебор в весе для квестов и эвентов
     *
     * @return true если ве проверки прошли успешно
     */

    public boolean isQuestContinuationPossible(boolean msg) {
        return isQuestContinuationPossible(msg, false);
    }

    public boolean isQuestContinuationPossible(boolean msg, boolean includeQuestInv) {
        if (!includeQuestInv) {
            if (getInventory().getSize(false) <= (getInventoryLimit() * 0.8)) {
                return true;
            } else {
                if (msg)
                    sendPacket(Msg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_VOLUME_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                return false;
            }
        } else {
            if (getInventory().getSize(true) <= (getQuestInventoryLimit() * 0.9))
                return true;
            else {
                if (msg)
                    sendPacket(Msg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_VOLUME_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                return false;
            }
        }
    }

    // ----------------- End of Quest Engine -------------------

    public Collection<L2ShortCut> getAllShortCuts() {
        return _shortCuts.getAllShortCuts();
    }

    public L2ShortCut getShortCut(int slot, int page) {
        return _shortCuts.getShortCut(slot, page);
    }

    public void registerShortCut(L2ShortCut shortcut) {
        _shortCuts.registerShortCut(shortcut);
    }

    public void deleteShortCut(int slot, int page) {
        _shortCuts.deleteShortCut(slot, page);
    }

    public void registerMacro(L2Macro macro) {
        _macroses.registerMacro(macro);
    }

    public void deleteMacro(int id) {
        _macroses.deleteMacro(id);
    }

    public MacroList getMacroses() {
        return _macroses;
    }

    /**
     * Возвращает состояние осады L2Player.<BR>
     * 1 = attacker, 2 = defender, 0 = не учавствует
     *
     * @return состояние осады
     */
    public int getSiegeState() {
        return _siegeState;
    }

    /**
     * Устанавливает состояние осады L2Player.<BR>
     * 1 = attacker, 2 = defender, 0 = не учавствует
     */
    public void setSiegeState(int siegeState) {
        _siegeState = siegeState;
        broadcastRelationChanged();
    }

    public boolean isCastleLord(int castleId) {
        return _clan != null && isClanLeader() && _clan.getHasCastle() == castleId;
    }

    /**
     * Проверяет является ли этот персонаж владельцем крепости
     *
     * @param fortressId
     * @return true если владелец
     */
    public boolean isFortressLord(int fortressId) {
        return _clan != null && isClanLeader() && _clan.getHasFortress() == fortressId;
    }

    public boolean isHideoutLord(int clan_hall_id) {
        return _clan != null && isClanLeader() && _clan.getHasHideout() == clan_hall_id;
    }

    public int getPkKills() {
        return _pkKills;
    }

    public void setPkKills(final int pkKills) {
        _pkKills = pkKills;
    }

    public long getCreateTime() {
        return _createTime;
    }

    public void setCreateTime(final long createTime) {
        _createTime = createTime;
    }

	/*public int getDeleteTimer()
	{
		return _deleteTimer;
	}

	public void setDeleteTimer(final int deleteTimer)
	{
		_deleteTimer = deleteTimer;
	}*/

    public int getCurrentLoad() {
        return getInventory().getTotalWeight();
    }

    public long getLastAccess() {
        return _lastAccess;
    }

    public void setLastAccess(long value) {
        _lastAccess = value;
    }

    @Override
    public int getKarma() {
        return _karma;
    }

    public void setKarma(int karma) {
        if (karma < 0)
            karma = 0;

        if (_karma == karma)
            return;

        _karma = karma;

        if (karma > 0)
            for (final L2Character object : L2World.getAroundCharacters(this))
                if (object instanceof L2GuardInstance && object.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
                    object.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);

        sendChanges();

        if (getPet() != null)
            getPet().broadcastPetInfo();
    }

    public int getMaxLoad() {
        if (isBot123())
            return Integer.MIN_VALUE;
        if (getPlayerAccess() != null && getPlayerAccess().IsGM && getPlayerAccess().GodMode)
            return Integer.MAX_VALUE;
        // Weight Limit = (CON Modifier*69000)*Skills
        // Source http://l2open.bravehost.com/weightlimit.html (May 2007)
        // Fitted exponential curve to the data
        int con = getCON();
        if (con < 1)
            return (int) ((31000 * ConfigValue.MaxLoadModifier) * getRateMaxLoad());
            //else if(con > 59)
            //	return (int) ((176000 * ConfigValue.MaxLoadModifier) * getRateMaxLoad());
        else
            return (int) (calcStat(Stats.MAX_LOAD, Math.pow(1.029993928, con) * 30495.627366 * ConfigValue.MaxLoadModifier, this, null) * getRateMaxLoad());
    }

    public int getArmorExpertisePenalty() {
        return armorExpertisePenalty;
    }

    public int getWeaponsExpertisePenalty() {
        return weaponExpertisePenalty;
    }

    public int getWeightPenalty() {
        return _curWeightPenalty;
    }

    public void refreshOverloaded() {
        if (isMassUpdating() || getMaxLoad() <= 0)
            return;

        setOverloaded(getCurrentLoad() > getMaxLoad());
        double weightproc = 100. * (getCurrentLoad() - calcStat(Stats.MAX_NO_PENALTY_LOAD, 0, this, null)) / getMaxLoad();
        int newWeightPenalty = 0;

        if (weightproc < 50)
            newWeightPenalty = 0;
        else if (weightproc < 66.6)
            newWeightPenalty = 1;
        else if (weightproc < 80)
            newWeightPenalty = 2;
        else if (weightproc < 100)
            newWeightPenalty = 3;
        else
            newWeightPenalty = 4;

        if (_curWeightPenalty == newWeightPenalty)
            return;

        _curWeightPenalty = newWeightPenalty;
        if (_curWeightPenalty > 0)
            super.addSkill(SkillTable.getInstance().getInfo(4270, _curWeightPenalty));
        else
            super.removeSkill(getKnownSkill(4270), false, true);

        EtcStatusUpdate();
    }

    public void checkGradeExpertiseUpdate() {
        if (isMassUpdating())
            return;

        int level = (int) calcStat(Stats.GRADE_EXPERTISE_LEVEL, getLevel(), null, null);
        int current = expertiseIndex, changeTo = -1;

        for (short i = 0; i < EXPERTISE_LEVELS.length; i++)
            if (level >= EXPERTISE_LEVELS[i])
                changeTo = i;

        if (changeTo == -1)
            return;

        if (current == changeTo) // nothing to change
            return;

        super.removeSkill(getKnownSkill(239), false, true);
        if (changeTo > 0)
            super.addSkill(SkillTable.getInstance().getInfo(239, changeTo));

        sendPacket(new SkillList(this));
        expertiseIndex = changeTo;
    }

    public void validateItemExpertisePenalties(boolean grade, boolean armor, boolean weapon) {
        if (grade)
            checkGradeExpertiseUpdate();
        if (armor)
            checkArmorPenalty();
        if (weapon)
            checkWeaponPenalty();
    }

    public void checkArmorPenalty() {
        if (ConfigValue.checkArmorPenalty) {
            int current = -1;
            L2ItemInstance[] f = getInventory().getItems();
            for (L2ItemInstance item : f) {
                if (item != null && item.isEquipped()) {
                    int itemType2 = item.getItem().getType2();
                    if (itemType2 == L2Item.TYPE2_SHIELD_ARMOR || itemType2 == L2Item.TYPE2_ACCESSORY) {
                        int crystaltype = item.getItem().getCrystalType().ordinal();
                        if (current < crystaltype)
                            current = crystaltype;
                    }
                }
            }
            if (current <= expertiseIndex) {
                armorExpertisePenalty = 0;

                L2Skill s = getKnownSkill(6213);
                if (s != null) {
                    this.removeSkillById(s.getId());
                    sendPacket(new SkillList(this));
                    EtcStatusUpdate();
                }
            } else {
                int penalty = current - expertiseIndex;

                if (penalty > 4)
                    penalty = 4;

                if (armorExpertisePenalty != penalty) {
                    armorExpertisePenalty = penalty;

                    super.removeSkill(getKnownSkill(6213), false, true);
                    if (penalty > 0)
                        super.addSkill(SkillTable.getInstance().getInfo(6213, penalty));

                    sendPacket(new SkillList(this));
                    if (penalty > 0)
                        EtcStatusUpdate();
                }
            }
        }
    }

    public void checkWeaponPenalty() {
        if (ConfigValue.checkWeaponPenalty) {
            int current = -1;
            L2ItemInstance[] f = getInventory().getItems();
            for (L2ItemInstance item : f) {
                if (item.isEquipped()) {
                    int itemType2 = item.getItem().getType2();
                    if (itemType2 == L2Item.TYPE2_WEAPON) {
                        int crystaltype = item.getItem().getCrystalType().ordinal();
                        if (current < crystaltype)
                            current = crystaltype;
                    }
                }
            }
            if (current <= expertiseIndex) {
                weaponExpertisePenalty = 0;

                L2Skill s = getKnownSkill(6209);
                if (s != null) {
                    this.removeSkillById(s.getId());
                    sendPacket(new SkillList(this));
                    EtcStatusUpdate();
                }
            } else {
                int penalty = current - expertiseIndex;

                if (penalty > 4)
                    penalty = 4;

                if (weaponExpertisePenalty != penalty) {
                    weaponExpertisePenalty = penalty;

                    super.removeSkill(getKnownSkill(6209), false, true);
                    if (penalty > 0)
                        super.addSkill(SkillTable.getInstance().getInfo(6209, penalty));

                    sendPacket(new SkillList(this));
                    if (penalty > 0)
                        EtcStatusUpdate();
                }
            }
        }
    }

    public int getPvpKills() {
        return _pvpKills;
    }

    public void setPvpKills(int pvpKills) {
        _pvpKills = pvpKills;
        if (ConfigValue.EnablePvpTitleColor)
            setChangeTitleColor();
        if (ConfigValue.EnablePvpNameColor)
            setChangeNameColor();
    }

    public ClassId getClassId() {
        return getTemplate().classId;
    }

    public void addClanPointsOnProfession(final int id) {
        if (getLvlJoinedAcademy() != 0 && _clan != null && _clan.getLevel() >= 5 && ClassId.values()[id].getLevel() == 2)
            _clan.incReputation(100, true, "Academy");
        else if (getLvlJoinedAcademy() != 0 && _clan != null && _clan.getLevel() >= 5 && ClassId.values()[id].getLevel() == 3) {
            int earnedPoints = 0;
            if (getLvlJoinedAcademy() <= 16)
                earnedPoints = 650;
            else if (getLvlJoinedAcademy() >= 39)
                earnedPoints = 190;
            else
                earnedPoints = 650 - (getLvlJoinedAcademy() - 16) * 20;

            _clan.removeClanMember(getObjectId());
            SystemMessage sm = new SystemMessage(SystemMessage.CLAN_ACADEMY_MEMBER_S1_HAS_SUCCESSFULLY_COMPLETED_THE_2ND_CLASS_TRANSFER_AND_OBTAINED_S2_CLAN_REPUTATION_POINTS);
            sm.addString(getName());
            sm.addNumber(_clan.incReputation(earnedPoints, true, "Academy"));
            _clan.broadcastToOnlineMembers(sm);
            _clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListDelete(getName()), this);

            setClan(null);
            unsetVar("canWhWithdraw");
            setTitle("");
            sendPacket(Msg.CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_AS_A_GRADUATE_OF_THE_ACADEMY_YOU_CAN_IMMEDIATELY_JOIN_A_CLAN_AS_A_REGULAR_MEMBER_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES);
            setLeaveClanTime(0);

            broadcastUserInfo(true);
            broadcastRelationChanged();

            sendPacket(Msg.PledgeShowMemberListDeleteAll);

            L2ItemInstance academyCirclet = ItemTemplates.getInstance().createItem(8181);
            getInventory().addItem(academyCirclet);
            sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1).addItemName(academyCirclet.getItemId()));

            if (ConfigValue.RecruitmentAllow) {
                Academicians academic = AcademiciansStorage.getInstance().get(getObjectId());
                if (academic != null) {
                    //_log.info("L2Player: academic_clan="+academic.getClanId());
                    //for(AcademyRequest request2 : AcademyStorage.getInstance().get())
                    //	_log.info("\t\t: clan="+request2.getClanId());
                    AcademyRequest request = AcademyStorage.getInstance().getReguest(academic.getClanId());
                    AcademiciansDAO.getInstance().delete(academic);

                    if (!AcademiciansStorage.getInstance().clanCheck(academic.getClanId())) {
                        AcademyRequestDAO.getInstance().delete(academic.getClanId());
                        AcademyStorage.getInstance().get().remove(request);
                        AcademyStorage.getInstance().updateList();
                    }

                    // выдаем награду, только если прошел вовремя академку...
                    if (academic.getTime() >= System.currentTimeMillis())
                        Functions.addItem(this, request.getItem(), request.getPrice());
                }
            } else if (ConfigValue.AcademicEnable) {
                l2open.gameserver.model.barahlo.academ2.Academicians academic = l2open.gameserver.model.barahlo.academ2.AcademiciansStorage.getInstance().getAcademicMap().get(getObjectId());
                if (academic != null) {
                    l2open.gameserver.model.barahlo.academ2.AcademiciansStorage.getInstance().delAcademic(academic, true);
                }
            }
        }
    }

    /**
     * Set the template of the L2Player.
     *
     * @param id The Identifier of the L2PlayerTemplate to set to the L2Player
     */
    public synchronized void setClassId(final int id, boolean noban) {
        if (!noban && !(ClassId.values()[id].equalsOrChildOf(ClassId.values()[getActiveClassId()]) || getPlayerAccess().CanChangeClass || ConfigValue.EverybodyHasAdminRights)) {
            Thread.dumpStack();
            Util.handleIllegalPlayerAction(this, "L2Player[1544]", "tried to change class " + _activeClassId + " to " + id, 1);
            return;
        }

        boolean newClass = false;
        //Если новый ID не принадлежит имеющимся классам значит это новая профа
        if (!getSubClasses().containsKey(id)) {
            newClass = true;
            final L2SubClass cclass = getActiveClass();
            getSubClasses().remove(getActiveClassId());
            PlayerData.getInstance().changeClassInDb(this, cclass.getClassId(), id);
            if (ConfigValue.PLRM_Enable)
                PlayerLevelRewardManager.getInstance().changeClass(this, cclass.getClassId(), id);
            if (cclass.isBase()) {
                setBaseClass(id);
                addClanPointsOnProfession(id);
                L2ItemInstance coupons = null;
                if (ClassId.values()[id].getLevel() == 2) {
                    if (ConfigValue.AllowShadowWeapons)
                        coupons = ItemTemplates.getInstance().createItem(8869);
                    unsetVar("newbieweapon");
                    unsetVar("p1q2");
                    unsetVar("p1q3");
                    unsetVar("p1q4");
                    unsetVar("prof1");
                    unsetVar("ng1");
                    unsetVar("ng2");
                    unsetVar("ng3");
                    unsetVar("ng4");
                } else if (ClassId.values()[id].getLevel() == 3) {
                    if (ConfigValue.AllowShadowWeapons)
                        coupons = ItemTemplates.getInstance().createItem(8870);
                    unsetVar("newbiearmor");
                    unsetVar("dd1"); // удаляем отметки о выдаче дименшен даймондов
                    unsetVar("dd2");
                    unsetVar("dd3");
                    unsetVar("prof2.1");
                    unsetVar("prof2.2");
                    unsetVar("prof2.3");
                    PlayerData.getInstance().checkReferralBonus(this, 1);
                } else if (ClassId.values()[id].getLevel() == 4) {
                    PlayerData.getInstance().checkReferralBonus(this, 2);
                    Olympiad.changeNobleClass(getObjectId(), id);
                }

                if (coupons != null) {
                    coupons.setCount(15);
                    getInventory().addItem(coupons);
                    sendPacket(SystemMessage.obtainItems(coupons));
                }
            }

            // Выдача Holy Pomander
            switch (ClassId.values()[id]) {
                case cardinal:
                    Functions.addItem(this, 15307, 1);
                    break;
                case evaSaint:
                    Functions.addItem(this, 15308, 1);
                    break;
                case shillienSaint:
                    Functions.addItem(this, 15309, 4);
                    break;
            }

            cclass.setClassId(id);
            getSubClasses().put(id, cclass);
            rewardSkills();
            PlayerData.getInstance().storeCharSubClasses(this);

            // Социалка при получении профы
            broadcastSkill(new MagicSkillUse(this, this, 5103, 1, 1000, 0), true);
            //broadcastPacket(new SocialAction(getObjectId(), 16));
            sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
            broadcastUserInfo(true);
        }

        L2PlayerTemplate t = CharTemplateTable.getInstance().getTemplate(id, getSex() == 1);
        if (t == null) {
            _log.severe("Missing template for classId: " + id);
            // do not throw error - only print error
            return;
        }

        // Set the template of the L2Player
        setTemplate(t);
        //setBaseTemplate(t);

        // Update class icon in party and clan
        if (isInParty())
            getParty().broadcastToPartyMembers(new PartySmallWindowUpdate(this));
        if (getClan() != null)
            getClan().broadcastToOnlineMembers(new PledgeShowMemberListUpdate(this));
        getListeners().onSetClass(id);
    }

    public void setClassId(final int id) {
        setClassId(id, false);
    }

    public long getExp() {
        return _activeClass == null ? 0 : _activeClass.getExp();
    }

    public long getMaxExp() {
        return _activeClass == null ? Experience.LEVEL[Experience.getMaxLevel() + 1] : _activeClass.getMaxExp();
    }

    public void addExp(long val) {
        if (_activeClass != null)
            _activeClass.addExp(val);
    }

    public void setEnchantScroll(final L2ItemInstance scroll) {
        _enchantScroll = scroll;
    }

    public L2ItemInstance getEnchantScroll() {
        return _enchantScroll;
    }

    public void setFistsWeaponItem(final L2Weapon weaponItem) {
        _fistsWeaponItem = weaponItem;
    }

    public L2Weapon getFistsWeaponItem() {
        return _fistsWeaponItem;
    }

    public L2Weapon findFistsWeaponItem(final int classId) {
        //human fighter fists
        if (classId >= 0x00 && classId <= 0x09)
            return (L2Weapon) ItemTemplates.getInstance().getTemplate(246);

        //human mage fists
        if (classId >= 0x0a && classId <= 0x11)
            return (L2Weapon) ItemTemplates.getInstance().getTemplate(251);

        //elven fighter fists
        if (classId >= 0x12 && classId <= 0x18)
            return (L2Weapon) ItemTemplates.getInstance().getTemplate(244);

        //elven mage fists
        if (classId >= 0x19 && classId <= 0x1e)
            return (L2Weapon) ItemTemplates.getInstance().getTemplate(249);

        //dark elven fighter fists
        if (classId >= 0x1f && classId <= 0x25)
            return (L2Weapon) ItemTemplates.getInstance().getTemplate(245);

        //dark elven mage fists
        if (classId >= 0x26 && classId <= 0x2b)
            return (L2Weapon) ItemTemplates.getInstance().getTemplate(250);

        //orc fighter fists
        if (classId >= 0x2c && classId <= 0x30)
            return (L2Weapon) ItemTemplates.getInstance().getTemplate(248);

        //orc mage fists
        if (classId >= 0x31 && classId <= 0x34)
            return (L2Weapon) ItemTemplates.getInstance().getTemplate(252);

        //dwarven fists
        if (classId >= 0x35 && classId <= 0x39)
            return (L2Weapon) ItemTemplates.getInstance().getTemplate(247);

        return null;
    }

    /**
     * Добавляет чару опыт и/или сп с учетом личного бонуса
     */
    @Override
    public void addExpAndSp(long addToExp, long addToSp) {
        addExpAndSp(addToExp, addToSp, true, true, 0, 0, null);
    }

    @Override
    public void addExpAndSp(long addToExp, long addToSp, boolean applyBonus, boolean appyToPet) {
        addExpAndSp(addToExp, addToSp, applyBonus, appyToPet, 0, 0, null);
    }

    /**
     * Добавляет чару опыт и/или сп, с учетом личного бонуса или нет
     */
    @Override
    public void addExpAndSp(long addToExp, long addToSp, boolean applyBonus, boolean appyToPet, long addToExpExVit, long addToSpExVit, L2MonsterInstance monster) {
        long firstAddToExp = addToExp;
        long firstAddToSp = addToSp;

        if (applyBonus) {
            addToExp *= getRateExp();
            addToSp *= getRateSp();

            if (monster != null && monster.isEpicRaid()) {
                addToExp *= ConfigValue.RateEpicXp;
                addToSp *= ConfigValue.RateEpicSp;
            } else if (monster != null && (monster.isRaid() || monster.isBoss())) {
                addToExp *= ConfigValue.RateRbXp;
                addToSp *= ConfigValue.RateRbSp;
            } else {
                addToExp *= RateService.getRateXp(this);
                addToSp *= RateService.getRateSp(this);
            }
        }

        if (addToExp > 0) {
            if (appyToPet) {
                L2Summon pet = getPet();
                if (pet != null && !pet.isDead())
                    // Sin Eater забирает всю экспу у персонажа
                    if (pet.getNpcId() == PetDataTable.SIN_EATER_ID) {
                        pet.addExpAndSp(addToExp, 0);
                        addToExp = 0;
                    } else if (pet.isPet() && pet.getLevel() == ConfigValue.MaxPetLevel && pet.getExp() >= pet.getExpForNextLevel()) // На 85 уровне петы не забирают опыт.
                    {
                        addToExp *= 1f;
                    } else if (pet.isPet() && pet.getExpPenalty() > 0f) {
                        int dl = getLevel() - pet.getLevel();
                        if (dl > 5) //Чар больше пета на 5 лвлов
                        {
                            if (dl > 25) // Уровень пета на 25 и более меньше уровня чара стабильно 0.149.
                            {
                                pet.addExpAndSp((long) (addToExp * (pet.getExpPenalty() * 0.149f)), 0);
                                addToExp *= 1f - (pet.getExpPenalty() * 0.149f);
                            } else {
                                //Пет получает меньше экспы.
                                pet.addExpAndSp((long) (addToExp * (pet.getExpPenalty() * ((float) (1 / (Math.pow(1.1, dl - 5)))))), 0);
                                addToExp *= 1f - (pet.getExpPenalty() * ((float) (1 / (Math.pow(1.1, dl - 5)))));
                            }
                        } else if (dl < -10) // Пет больше чара на 10 лвлов.
                        {
                            if (dl < -30) // Если пет больше чара на 31 лвлов, то устанавливаем эту планку, иначе экспа у чара уходит в минус.
                            {
                                pet.addExpAndSp((long) (addToExp * (pet.getExpPenalty() * ((float) (1 / (Math.pow(1.05, -30 + 10)))))), 0);
                                addToExp *= 1f - (pet.getExpPenalty() * ((float) (1 / (Math.pow(1.05, -30 + 10)))));
                            } else {
                                //Пет получает больше экспы.
                                pet.addExpAndSp((long) (addToExp * (pet.getExpPenalty() * ((float) (1 / (Math.pow(1.05, dl + 10)))))), 0);
                                addToExp *= 1f - (pet.getExpPenalty() * ((float) (1 / (Math.pow(1.05, dl + 10)))));
                            }
                        } else {
                            //Пет получает стандартную экспу.
                            pet.addExpAndSp((long) (addToExp * pet.getExpPenalty()), 0);
                            addToExp *= 1f - pet.getExpPenalty();
                        }
                    } else if (pet.isSummon()) {
                        addToExp *= 1f - pet.getExpPenalty();
                    }
            }

            // Remove Karma when the player kills L2MonsterInstance
            if (!isCursedWeaponEquipped() && addToSp > 0 && _karma > 0)
                _karma -= addToSp / (ConfigValue.SPDivider * RateService.getRateSp(this));

            if (_karma < 0)
                _karma = 0;

            long max_xp = getVarB("NoExp") ? Experience.LEVEL[getLevel() + 1] - 1 : getMaxExp();
            addToExp = Math.min(addToExp, max_xp - getExp());
        }

        addExp(addToExp);
        addSp(addToSp);

        long expWithoutBonus = addToExp - firstAddToExp + addToExpExVit;
        long spWithoutBonus = addToSp - firstAddToSp + addToSpExVit;

        if (addToExp > 0 && addToSp > 0 && (expWithoutBonus > 0 || spWithoutBonus > 0))
            sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_EXP_BONUS_S2_AND_S3_SP_BONUS_S4).addLong(addToExp).addLong(addToExp - expWithoutBonus).addLong(addToSp).addLong(addToSp - spWithoutBonus));
        else if (addToSp > 0 && addToExp == 0)
            sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_SP).addNumber(addToSp));
        else if (addToSp > 0 && addToExp > 0)
            sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_EXPERIENCE_AND_S2_SP).addNumber(addToExp).addNumber(addToSp));
        else if (addToSp == 0 && addToExp > 0)
            sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_EXPERIENCE).addNumber(addToExp));

        long exp = getExp();
        int level = getLevel();
        int level_old = getLevel();

        boolean increase = false;

        if (exp >= Experience.LEVEL[level + 1] && level < Experience.getMaxLevel()) {
            increaseLevelAction();
            increase = true;
        }

        while (_activeClass != null && level < Experience.getMaxLevel() && exp >= Experience.LEVEL[level + 1] && _activeClass.incLevel())
            level = getLevel();

        while (exp < Experience.LEVEL[level] && decreaseLevel())
            level = getLevel();
        if (increase) {
            increaseLevel();
            PlayerRewardManager.getInstance().inc_level(this);
            getListeners().onSetLevel(getLevel());
        }

        L2Summon pet = getPet();
        if (pet != null && pet.isPet() && PetDataTable.isPremiumPet(pet.getNpcId())) {
            L2PetInstance _pet = (L2PetInstance) pet;
            _pet.setLevel(getLevel());
            _pet.setExp(_pet.getExpForNextLevel());
            _pet.broadcastStatusUpdate();
        }

        sendChanges();
    }

    private void increaseLevelAction() {
        if (getLevel() >= Experience.getMaxLevel())
            return;
        sendPacket(Msg.YOU_HAVE_INCREASED_YOUR_LEVEL);
        broadcastPacket(new SocialAction(getObjectId(), SocialAction.LEVEL_UP));
    }

    /**
     * Give Expertise skill of this level.<BR><BR>
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Get the Level of the L2Player </li>
     * <li>Add the Expertise skill corresponding to its Expertise level</li>
     * <li>Update the overloaded status of the L2Player</li><BR><BR>
     *
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T give other free skills (SP needed = 0)</B></FONT><BR><BR>
     */
    public void rewardSkills() {
        boolean update = false;
        if (ConfigValue.AutoLearnSkills) {
            int unLearnable = 0;
            GArray<L2SkillLearn> skills = getAvailableSkills(getClassId());
            while (skills.size() > unLearnable) {
                unLearnable = 0;
                for (L2SkillLearn s : skills) {
                    L2Skill sk = SkillTable.getInstance().getInfo(s.id, s.skillLevel);
                    if (sk == null || !sk.getCanLearn(getClassId()) || s.getMinLevel() > ConfigValue.AutoLearnSkillsMaxLevel || (s.getItemId() > 0 && !ConfigValue.AutoLearnForgottenSkills)) {
                        unLearnable++;
                        continue;
                    }
                    addSkill(sk, true);
                    s.deleteSkills(this);
                }
                skills = getAvailableSkills(getClassId());
            }
            update = true;
        } else
            // Скиллы дающиеся бесплатно не требуют изучения
            for (L2SkillLearn skill : getAvailableSkills(getClassId()))
                if (skill._repCost == 0 && skill._spCost == 0 && skill.itemCount == 0) {
                    L2Skill sk = SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel());
                    addSkill(sk, true);
                    skill.deleteSkills(this);
                    if (getAllShortCuts().size() > 0 && sk.getLevel() > 1)
                        for (L2ShortCut sc : getAllShortCuts())
                            if (sc.id == sk.getId() && sc.type == L2ShortCut.TYPE_SKILL) {
                                L2ShortCut newsc = new L2ShortCut(sc.slot, sc.page, sc.type, sc.id, sk.getLevel());
                                sendPacket(new ShortCutRegister(newsc));
                                registerShortCut(newsc);
                            }
                    update = true;
                }

        if (update)
            sendPacket(new SkillList(this));

        // This function gets called on login, so not such a bad place to check weight
        // Update the overloaded status of the L2Player
        refreshOverloaded();
        checkGradeExpertiseUpdate();
        checkWeaponPenalty();
        checkArmorPenalty();
    }

    public GArray<L2SkillLearn> getAvailableSkills(ClassId classId) {
        GArray<L2SkillLearn> result = new GArray<L2SkillLearn>();

        for (L2SkillLearn temp : SkillTreeTable.getInstance().getSkillTrees().get(classId))
            if (temp.minLevel <= getLevel()) {
                if (!temp.canLearnSkill(this))
                    continue;
                boolean knownSkill = false;
                for (L2Skill s : getAllSkillsArray())
                    if (s != null) {
                        if (s.getId() == temp.id) {
                            if (s.getLevel() == temp.skillLevel - 1)
                                result.add(temp); // this is the next level of a skill that we know
                            knownSkill = true;
                            break;
                        }
                    }
                if (!knownSkill && temp.skillLevel == 1)
                    result.add(temp); // this is a new skill
            }
        return result;
    }

    public Collection<L2SkillLearn> getAvailableSkillsLind(ClassId classId) {
        TreeMap<Short, L2SkillLearn> skillListMap = new TreeMap<Short, L2SkillLearn>();

        for (L2SkillLearn temp : SkillTreeTable.getInstance().getSkillTrees().get(classId))
            if (temp.minLevel <= getLevel() && !skillListMap.containsKey(temp.id)) {
                if (!temp.canLearnSkill(this))
                    continue;
                //_log.info("ExAcquirableSkillListByClass: ["+temp.id+"]["+temp.skillLevel+"]["+classId+"]["+ClassId.values()[getActiveClassId()]+"]["+temp.getItemId()+"]["+temp.name+"]");
                boolean knownSkill = false;
                for (L2Skill s : getAllSkillsArray())
                    if (s != null) {
                        if (s.getId() == temp.id) {
                            if (s.getLevel() == temp.skillLevel - 1)
                                skillListMap.put(temp.id, temp); // this is the next level of a skill that we know
                            knownSkill = true;
                            break;
                        }
                    }
                if (!knownSkill && temp.skillLevel == 1)
                    skillListMap.put(temp.id, temp); // this is a new skill
            }
        return skillListMap.values();
    }

    public Race getRace() {
        //_log.info("race: "+getBaseTemplate().race);
        return getBaseTemplate().race;
    }

    public int class_race = 0;

    public int getClassRace() {
        //_log.info("race: "+getBaseTemplate().class_race);
        return ConfigValue.EnableCustomBaseClass && class_race > 0 ? class_race : getBaseClassId();
    }

    public int getIntSp() {
        return (int) getSp();
    }

    public long getSp() {
        return _activeClass == null ? 0 : _activeClass.getSp();
    }

    public void setSp(long sp) {
        if (_activeClass != null)
            _activeClass.setSp(sp);
    }

    public void addSp(long val) {
        if (_activeClass != null)
            _activeClass.addSp(val);
    }

    public int getClanId() {
        return _clan == null ? 0 : _clan.getClanId();
    }

    @Override
    public int getClanCrestId() {
        return getEventMaster() == null ? (_clan == null ? 0 : _clan.getCrestId()) : getEventMaster().getClanCrestId(this);
    }

    @Override
    public int getClanCrestLargeId() {
        return getEventMaster() == null ? (_clan == null ? 0 : _clan.getCrestLargeId()) : getEventMaster().getClanCrestLargeId(this);
    }

    public long getLeaveClanTime() {
        return _leaveClanTime;
    }

    public long getDeleteClanTime() {
        return _deleteClanTime;
    }

    public void setLeaveClanTime(final long time) {
        _leaveClanTime = time;
    }

    public void setDeleteClanTime(final long time) {
        _deleteClanTime = time;
    }

    public void setOnlineTime(final long time) {
        _onlineTime = time;
        _onlineBeginTime = System.currentTimeMillis();
    }

    public long getOnlineTime() {
        return (_onlineBeginTime > 0 ? (_onlineTime + System.currentTimeMillis() - _onlineBeginTime) : _onlineTime);
    }

    public void setNoChannel(final long time) {
        _NoChannel = time;
        if (_NoChannel > 2145909600000L || _NoChannel < 0)
            _NoChannel = -1;

        if (_NoChannel > 0)
            _NoChannelBegin = System.currentTimeMillis();
        else
            _NoChannelBegin = 0;

        EtcStatusUpdate();
    }

    public long getNoChannel() {
        return _NoChannel;
    }

    public long getNoChannelRemained() {
        if (_NoChannel == 0)
            return 0;
        else if (_NoChannel < 0)
            return -1;
        else {
            long remained = _NoChannel - System.currentTimeMillis() + _NoChannelBegin;
            if (remained < 0)
                return 0;

            return remained;
        }
    }

    public void setLeaveClanCurTime() {
        _leaveClanTime = System.currentTimeMillis();
        unsetVar("join_clan");
    }

    public void setDeleteClanCurTime() {
        _deleteClanTime = System.currentTimeMillis();
    }

    public boolean canJoinClan() {
        if (getEventMaster() != null)
            return getEventMaster().canJoinClan(this);
        if (_leaveClanTime == 0)
            return true;
        if (System.currentTimeMillis() - _leaveClanTime >= ConfigValue.EXPELLED_PLAYER_PENALTY * 1000L) {
            _leaveClanTime = 0;
            return true;
        }
        return false;
    }

    public boolean canCreateClan() {
        if (_deleteClanTime == 0)
            return true;
        if (System.currentTimeMillis() - _deleteClanTime >= ConfigValue.DROP_CLAN_PENALTY * 1000L) {
            _deleteClanTime = 0;
            return true;
        }
        return false;
    }

    public SystemMessage canJoinParty(L2Player inviter) {
        Transaction transaction = getTransaction();
        if (transaction != null && transaction.isInProgress() && transaction.getOtherPlayer(this) != inviter)
            return Msg.WAITING_FOR_ANOTHER_REPLY; // занят
        else if (isBlockAll() || getMessageRefusal()) // всех нафиг
            return Msg.THE_PERSON_IS_IN_A_MESSAGE_REFUSAL_MODE;
        else if (isInParty()) // уже
            return new SystemMessage(SystemMessage.S1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED).addString(getName());
        else if (ReflectionTable.getInstance().findSoloKamaloka(getObjectId()) != null) // в соло каме
            return Msg.INVALID_TARGET();
        else if (isCursedWeaponEquipped() || inviter.isCursedWeaponEquipped()) // зарич
            return Msg.INVALID_TARGET();
        else if (inviter.isInOlympiadMode() || isInOlympiadMode()) // олимпиада
            return Msg.INVALID_TARGET();
        else if (!inviter.getPlayerAccess().CanJoinParty || !getPlayerAccess().CanJoinParty) // низя
            return Msg.INVALID_TARGET();
        else if (getTeam() != inviter.getTeam()) // участник пвп эвента или дуэли
            return Msg.INVALID_TARGET();
        else if (!can_create_party || !inviter.can_create_party)
            return Msg.INVALID_TARGET();
        return null;
    }

    @Override
    public PcInventory getInventory() {
        return _inventory;
    }

    public void removeItemFromShortCut(final int objectId) {
        _shortCuts.deleteShortCutByObjectId(objectId);
    }

    public void removeSkillFromShortCut(final int skillId) {
        _shortCuts.deleteShortCutBySkillId(skillId);
    }

    @Override
    public boolean isSitting() {
        return inObserverMode() || _isSitting;
    }

    public void setSitting(boolean val) {
        _isSitting = val;
    }

    public boolean getSittingTask() {
        return sittingTaskLaunched;
    }

    @Override
    public void sitDown(boolean force) {
        if (isSitting() || sittingTaskLaunched || isAlikeDead())
            return;

        if (isStunned() || isActionBlock() || isSleeping() || isParalyzed() || isAttackingNow() || isCastingNow() || (isMoving && !force)) {
            getAI().setNextAction(nextAction.REST, null, null, false, false);
            return;
        }

        resetWaitSitTime();
        getAI().setIntention(CtrlIntention.AI_INTENTION_REST, null, null);
        broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_SITTING));
        sittingTaskLaunched = true;
        _isSitting = true;
        ThreadPoolManager.getInstance().schedule(new EndSitDownTask(this), 2500, true);
    }

    @Override
    public void standUp() {
        if (_isSitting && !sittingTaskLaunched && !isInStoreMode() && !isAlikeDead()) {
            if (_relax) {
                setRelax(false);
                getEffectList().stopAllSkillEffects(EffectType.c_rest);
                getEffectList().stopAllSkillEffects(EffectType.c_chameleon_rest);
            }
            getAI().clearNextAction();
            broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_STANDING));
            sittingTaskLaunched = true;
            _isSitting = true;
            ThreadPoolManager.getInstance().schedule(new EndStandUpTask(this), 2500, true);
        }
    }

    public void setRelax(final boolean val) {
        _relax = val;
    }

    public void updateWaitSitTime() {
        if (_waitTimeWhenSit < 200)
            _waitTimeWhenSit += 2;
    }

    public int getWaitSitTime() {
        return _waitTimeWhenSit;
    }

    public void resetWaitSitTime() {
        _waitTimeWhenSit = 0;
    }

    public Warehouse getWarehouse() {
        return _warehouse;
    }

    public Warehouse getFreight() {
        return _freight;
    }

    public long getAdena() {
        return getInventory().getAdena();
    }

    /**
     * Забирает адену у игрока.<BR><BR>
     *
     * @param adena  - сколько адены забрать
     * @param notify - отображать системное сообщение
     * @return L2ItemInstance - остаток адены
     */
    public L2ItemInstance reduceAdena(long adena, boolean notify) {
        if (notify && adena > 0)
            sendPacket(new SystemMessage(SystemMessage.S1_ADENA_DISAPPEARED).addNumber(adena));
        return getInventory().reduceAdena(adena);
    }

    /**
     * Добавляет адену игроку.<BR><BR>
     *
     * @param adena - сколько адены дать
     * @return L2ItemInstance - новое количество адены
     * TODO добавить параметр update как в reduceAdena
     */
    public L2ItemInstance addAdena(final long adena) {
        return getInventory().addAdena(adena);
    }

    public L2GameClient getNetConnection() {
        return _connection;
    }

    public int getRevision() {
        return _connection == null ? 0 : _connection.getRevision();
    }

    public void setNetConnection(final L2GameClient connection) {
        _connection = connection;
        //if(_connection == null)
        //	System.out.println("setNetConnection null!!!");
        _isConnected = connection != null && connection.isConnected();
    }

    public void closeNetConnection() {
        if (_connection != null)
            _connection.closeNow(false);
    }

    @Override
    public void onAction(final L2Player player, boolean shift, int addDist) {
        if (Events.onAction(player, getPlayer(), shift)) {
            player.sendActionFailed();
            return;
        }
        // Check if the other player already target this L2Player
        if (player.getTarget() != getPlayer()) {
            player.setTarget(getPlayer());
            if (player.getTarget() != getPlayer())
                player.sendActionFailed();
            else {
                //player.sendPacket(new MyTargetSelected(getObjectId(), 0)); // The color to display in the select window is White
                //player.sendPacket(new ExAbnormalStatusUpdateFromTarget(getPlayer()));
            }
        } else {
            if (getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE) {
                if (getDistance(player) > INTERACTION_DISTANCE && getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
                    if (!shift)
                        player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, getPlayer(), 100);
                    player.sendActionFailed();
                } else
                    player.doInteract(getPlayer(), 0);
            } else if (isAutoAttackable(player)) {
                // Player with lvl < 21 can't attack a cursed weapon holder, and a cursed weapon holder can't attack players with lvl < 21 or Cursed player is in Peace zone.
                if (isCursedWeaponEquipped() && player.getLevel() < 21 || player.isCursedWeaponEquipped() && getLevel() < 21 || player.isCursedWeaponEquipped() && isInZonePeace())
                    player.sendActionFailed();
                else
                    player.getAI().Attack(getPlayer(), false, shift);
            } else if (player != getPlayer() && !shift)
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, getPlayer(), (int) Math.ceil(ConfigValue.FollowRange + getMinDistance(player)));
            else
                player.sendActionFailed();
        }
    }

    @Override
    public void broadcastStatusUpdate() {
        // Send the Server->Client packet StatusUpdate with current HP and MP to all L2Player that must be informed of HP/MP updates of this L2Player
        if (ConfigValue.ForceStatusUpdate)
            super.broadcastStatusUpdate();
        else if (!needStatusUpdate()) //По идее еше должно срезать траффик. Будут глюки с отображением - убрать это условие.
            return;

        sendStatusUpdate(false, StatusUpdate.CUR_HP, StatusUpdate.CUR_MP, StatusUpdate.CUR_CP, StatusUpdate.DAMAGE);

        // Check if a party is in progress
        if (isInParty())
            // Send the Server->Client packet PartySmallWindowUpdate with current HP, MP and Level to all other L2Player of the Party
            getParty().broadcastToPartyMembers(this, new PartySmallWindowUpdate(this));

        if (getDuel() != null)
            getDuel().broadcastToOppositTeam(this, new ExDuelUpdateUserInfo(this));

        if (isInOlympiadMode() && isOlympiadCompStart() && _olympiadGame != null)
            _olympiadGame.broadcastInfo(this, null, false);
    }

    public Future<?> _broadcastCharInfoTask;

    /**
     * Отправляет UserInfo даному игроку и CharInfo всем окружающим.<BR><BR>
     *
     * <B><U> Концепт</U> :</B><BR><BR>
     * Сервер шлет игроку UserInfo.
     * <B><U> Действия</U> :</B><BR><BR>
     * <li>Отсылка игроку UserInfo(личные и общие данные)</li>
     * <li>Отсылка другим игрокам CharInfo(Public data only)</li><BR><BR>
     *
     * <FONT COLOR=#FF0000><B> <U>Внимание</U> : НЕ ПОСЫЛАЙТЕ UserInfo другим игрокам либо CharInfo даному игроку.<BR>
     * НЕ ВЫЗЫВАЕЙТЕ ЭТОТ МЕТОД КРОМЕ ОСОБЫХ ОБСТОЯТЕЛЬСТВ(смена сабкласса к примеру)!!! Траффик дико кушается у игроков и начинаются лаги.<br>
     * Используйте метод {@link l2open.gameserver.model.L2Player#sendChanges()}</B></FONT><BR><BR>
     */
    @Override
    public void broadcastUserInfo(boolean force) {
        sendUserInfo(force);

        if (isInvisible())
            return;

        if (ConfigValue.BroadcastCharInfoInterval == 0)
            force = true;

        L2GameServerPacket dominion = getTerritorySiege() > -1 ? new ExDominionWarStart(this) : null;
        if (dominion != null)
            sendPacket(dominion);
        if (force) {
            broadcastCharInfo();
            if (_broadcastCharInfoTask != null) {
                _broadcastCharInfoTask.cancel(false);
                _broadcastCharInfoTask = null;
            }
            return;
        }

        if (_broadcastCharInfoTask != null)
            return;

        _broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new BroadcastCharInfoTask(this), ConfigValue.BroadcastCharInfoInterval, true);
    }

    public L2GameServerPacket newCharInfo() {
        if (!isPolymorphed())
            return new CharInfo(this);
        else if (getPolytype() == L2Object.POLY_NPC)
            return new NpcInfo(this);
        else
            return new SpawnItemPoly(this);
    }

    public void broadcastCharInfo() {
        if (isInvisible())
            return;

        L2GameServerPacket ci = newCharInfo();
        StatusUpdate su = null;
        if (ConfigValue.EnableLindvior) {
            if (getPvpFlag() > 0)
                su = new StatusUpdate(getObjectId()).addAttribute(StatusUpdate.PVP_FLAG, getPvpFlag());
            if (getKarma() > 0) {
                if (su == null)
                    su = new StatusUpdate(getObjectId());
                su.addAttribute(StatusUpdate.KARMA, getKarma());
            }
        }

        //L2GameServerPacket dominion = getTerritorySiege() > -1 ? new ExDominionWarStart(this) : null;
        for (L2Player player : L2World.getAroundPlayers(this)) {
            if (player != null && player != this) {
                player.sendPacket(ci);
                if (su != null)
                    player.sendPacket(su);
            }
            //if(dominion != null)
            //	player.sendPacket(dominion);
        }
    }

    public Future<?> _userInfoTask;
    public boolean entering = true;

    public void sendUserInfo(boolean force) {
        if (entering || isLogoutStarted())
            return;

        //Util.test();

        if (ConfigValue.UserInfoInterval == 0 || force) {
            sendPacket(new UserInfo(this), new ExBrExtraUserInfo(this), new ExVoteSystemInfo(this));
            if (_userInfoTask != null) {
                _userInfoTask.cancel(false);
                _userInfoTask = null;
            }
            return;
        }

        if (_userInfoTask != null)
            return;

        _userInfoTask = ThreadPoolManager.getInstance().schedule(new UserInfoTask(this), ConfigValue.UserInfoInterval, true);
    }

    @Override
    public StatusUpdate makeStatusUpdate(int... fields) {
        StatusUpdate su = new StatusUpdate(getObjectId());
        for (int field : fields)
            switch (field) {
                case StatusUpdate.CUR_HP:
                    su.addAttribute(field, (int) getCurrentHp());
                    break;
                case StatusUpdate.p_max_hp:
                    su.addAttribute(field, getMaxHp());
                    break;
                case StatusUpdate.CUR_MP:
                    su.addAttribute(field, (int) getCurrentMp());
                    break;
                case StatusUpdate.p_max_mp:
                    su.addAttribute(field, getMaxMp());
                    break;
                case StatusUpdate.CUR_LOAD:
                    su.addAttribute(field, getCurrentLoad());
                    break;
                case StatusUpdate.MAX_LOAD:
                    su.addAttribute(field, getMaxLoad());
                    break;
                case StatusUpdate.PVP_FLAG:
                    su.addAttribute(field, _pvpFlag);
                    break;
                case StatusUpdate.KARMA:
                    su.addAttribute(field, getKarma());
                    break;
                case StatusUpdate.CUR_CP:
                    su.addAttribute(field, (int) getCurrentCp());
                    break;
                case StatusUpdate.p_max_cp:
                    su.addAttribute(field, getMaxCp());
                    break;
				/*default:
					System.out.println("unknown StatusUpdate field: " + field);
					Thread.dumpStack();
					break;*/
            }
        return su;
    }

    public void sendStatusUpdate(boolean broadCast, int... fields) {
        if (fields.length == 0 || entering && !broadCast)
            return;

        StatusUpdate su = makeStatusUpdate(fields);
        if (!su.hasAttributes())
            return;

        if (!broadCast)
            sendPacket(su);
        else if (entering)
            broadcastPacketToOthers(su);
        else
            broadcastPacket(su);
    }

    /**
     * @return the Alliance Identifier of the L2Player.<BR><BR>
     */
    public int getAllyId() {
        return _clan == null ? 0 : _clan.getAllyId();
    }

    @Override
    public int getAllyCrestId() {
        return getEventMaster() == null ? (getAlliance() == null ? 0 : getAlliance().getAllyCrestId()) : getEventMaster().getAllyCrestId(this);
    }

    public HashMap<String, Integer> packetsStat = null;
    public boolean packetsCount = false;

    protected void sendPacketStatsUpdate(final L2GameServerPacket... packets) {
        if (packetsStat == null)
            packetsStat = new HashMap<String, Integer>();

        String className;
        Integer count;
        for (L2GameServerPacket packet : packets) {
            className = packet.getClass().getSimpleName();
            count = packetsStat.get(className);
            if (count == null)
                count = 1;
            else
                count++;
            packetsStat.put(className, count);
        }
    }

    protected void sendPacketsStatsUpdate(final Collection<L2GameServerPacket> packets) {
        if (packetsStat == null)
            packetsStat = new HashMap<String, Integer>();

        String className;
        Integer count;
        for (SendablePacket<?> packet : packets) {
            className = packet.getClass().getSimpleName();
            count = packetsStat.get(className);
            if (count == null)
                count = 1;
            else
                count++;
            packetsStat.put(className, count);
        }
    }

    /**
     * Send a Server->Client packet StatusUpdate to the L2Player.<BR><BR>
     */
    @Override
    public void sendPacket(final L2GameServerPacket... packets) {
        if (_isConnected && packets.length != 0)
            try {
                if (_connection != null)
                    _connection.sendPacket(packets);

                if (packetsCount && isGM())
                    sendPacketStatsUpdate(packets);
            } catch (final Exception e) {
                _log.log(Level.INFO, "", e);
                e.printStackTrace();
            }
    }

    public void sendPackets(final Collection<L2GameServerPacket> packets) {
        if (_isConnected && packets != null && packets.size() > 0)
            try {
                if (_connection != null)
                    _connection.sendPackets(packets);

                if (packetsCount && isGM())
                    sendPacketsStatsUpdate(packets);
            } catch (final Exception e) {
                _log.log(Level.INFO, "", e);
                e.printStackTrace();
            }
    }

    public void doInteract(L2Object target, int addDist) {
        if (target == null || isOutOfControl()) {
            sendActionFailed();
            return;
        }
        if (target.isPlayer()) {
            if (target.getDistance(this) <= INTERACTION_DISTANCE + addDist) {
                L2Player temp = (L2Player) target;

                String tradeBan = temp.getVar("tradeBan");
                if (tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis())) {
                    sendMessage("Trader is banned! Expires: " + (tradeBan.equals("-1") ? "never" : Util.formatTime((Long.parseLong(tradeBan) - System.currentTimeMillis()) / 1000)) + ".");
                    sendActionFailed();
                    return;
                }
                tradeBan = getVar("tradeBan");
                if (tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis())) {
                    sendMessage("Your trade is banned! Expires: " + (tradeBan.equals("-1") ? "never" : Util.formatTime((Long.parseLong(tradeBan) - System.currentTimeMillis()) / 1000)) + ".");
                    sendActionFailed();
                    return;
                }

                if (temp.getPrivateStoreType() == STORE_PRIVATE_SELL || temp.getPrivateStoreType() == STORE_PRIVATE_SELL_PACKAGE)
                    sendPacket(new PrivateStoreListSell(this, temp));
                else if (temp.getPrivateStoreType() == STORE_PRIVATE_BUY)
                    sendPacket(new PrivateStoreListBuy(this, temp));
                else if (temp.getPrivateStoreType() == STORE_PRIVATE_MANUFACTURE)
                    sendPacket(new RecipeShopSellList(this, temp));
                else if (temp.getPrivateStoreType() == STORE_PRIVATE_BUFF)
                    buff_list(temp, 0, 0);
            } else if (getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
                getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, 100); // TODO: ???
        } else
            target.onAction(this, false, (int) (getMinDistance(target) + 250));
        sendActionFailed();
    }

    public void doAutoLootOrDrop(L2ItemInstance item, L2NpcInstance fromNpc) {
        boolean forceAutoloot = fromNpc.isFlying() || getReflection().isAutolootForced();

        if ((fromNpc.isRaid() || fromNpc.isRefRaid() || fromNpc.isEpicRaid()) && !item.isHerb()) {
            if (!forceAutoloot && !ConfigValue.AutoLootFromRaids) {
                if (fromNpc.getMasterPartyRouting() == 1 && fromNpc.MPCC_GetMaster() != null)
                    item.dropToTheGround(fromNpc.MPCC_GetMaster(), fromNpc);
                else
                    item.dropToTheGround(this, fromNpc);
                return;
            } else if (fromNpc.getMasterPartyRouting() == 1 && fromNpc.MPCC_GetMaster() != null) {
                if (!fromNpc.MPCC_GetMaster().isInParty()) {
                    if (!fromNpc.MPCC_GetMaster().getInventory().validateWeight(item)) {
                        fromNpc.MPCC_GetMaster().sendActionFailed();
                        fromNpc.MPCC_GetMaster().sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                        item.dropToTheGround(fromNpc.MPCC_GetMaster(), fromNpc);
                        return;
                    }

                    if (!fromNpc.MPCC_GetMaster().getInventory().validateCapacity(item)) {
                        fromNpc.MPCC_GetMaster().sendActionFailed();
                        fromNpc.MPCC_GetMaster().sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                        item.dropToTheGround(fromNpc.MPCC_GetMaster(), fromNpc);
                        return;
                    }

                    // Send a System Message to the L2Player
                    fromNpc.MPCC_GetMaster().sendPacket(SystemMessage.obtainItems(item));

                    // Add the Item to the L2Player inventory
                    L2ItemInstance target2 = fromNpc.MPCC_GetMaster().getInventory().addItem(item);
                    Log.LogItem(fromNpc.MPCC_GetMaster(), fromNpc, Log.GetItemByAutoLoot, target2);

                    fromNpc.MPCC_GetMaster().sendChanges();
                } else if (item.getItemId() == 57)
                    // Distribute Adena between Party members
                    fromNpc.MPCC_GetMaster().getParty().distributeAdena(item, fromNpc, fromNpc.MPCC_GetMaster());
                else
                    // Distribute Item between Party members
                    fromNpc.MPCC_GetMaster().getParty().distributeItem(fromNpc.MPCC_GetMaster(), item, fromNpc);
                return;
            }
        }

        // Herbs
        if (item.isHerb()) {
            if (!AutoLootHerbs && !forceAutoloot) {
                item.dropToTheGround(this, fromNpc);
                return;
            }
            L2Skill[] skills = item.getItem().getAttachedSkills();
            if (skills != null && skills.length > 0)
                for (L2Skill skill : skills) {
                    altUseSkill(skill, this);
                    if (getPet() != null && getPet().isSummon() && !getPet().isDead() && skill.getAbnormalLv() == -1)
                        getPet().altUseSkill(skill, getPet());
                }
            item.deleteMe();
            //broadcastPacket(new GetItem(item, getObjectId()));
            return;
        }

        if ((!AutoLoot || ConfigValue.AutoLootPA && !hasBonus()) && (!AutoLootSpecial || !Util.contains(ConfigValue.AutoLootSpecialList, item.getItemId())) && !forceAutoloot) {
            item.dropToTheGround(this, fromNpc);
            return;
        }
        // Check if the L2Player is in a Party
        if (!isInParty()) {
            if (!getInventory().validateWeight(item)) {
                sendActionFailed();
                sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                item.dropToTheGround(this, fromNpc);
                return;
            }

            if (!getInventory().validateCapacity(item)) {
                sendActionFailed();
                sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                item.dropToTheGround(this, fromNpc);
                return;
            }

            // Send a System Message to the L2Player
            sendPacket(SystemMessage.obtainItems(item));

            // Add the Item to the L2Player inventory
            L2ItemInstance target2 = getInventory().addItem(item);
            Log.LogItem(this, fromNpc, Log.GetItemByAutoLoot, target2);

            sendChanges();
        } else if (item.getItemId() == 57)
            // Distribute Adena between Party members
            getParty().distributeAdena(item, fromNpc, this);
        else
            // Distribute Item between Party members
            getParty().distributeItem(this, item, fromNpc);

        broadcastPickUpMsg(item);
    }

    @Override
    public void doPickupItem(final L2Object object) {
        // Check if the L2Object to pick up is a L2ItemInstance
        if (!object.isItem()) {
            _log.warning("trying to pickup wrong target." + getTarget());
            return;
        }

        sendActionFailed();
        stopMove();

        L2ItemInstance item = (L2ItemInstance) object;

        if (item.getItem().isCombatFlag() && !FortressSiegeManager.checkIfCanPickup(this))
            return;

        synchronized (item) {
            // Check if me not owner of item and, if in party, not in owner party and nonowner pickup delay still active
            if (item.getDropTimeOwner() != 0 && item.getItemDropOwner() != null && item.getDropTimeOwner() > System.currentTimeMillis() && this != item.getItemDropOwner() && (!item.getItemDropOwner().isInParty() || !isInParty() || isInParty() && item.getItemDropOwner().isInParty() && getParty() != item.getItemDropOwner().getParty())) {
                SystemMessage sm;
                if (item.getItemId() == 57) {
                    sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
                    sm.addNumber(item.getCount());
                } else {
                    sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                    sm.addItemName(item.getItemId());
                }
                sendPacket(sm);
                return;
            }

            if (!item.isVisible())
                return;

            // Herbs
            if (item.isHerb()) {
                L2Skill[] skills = item.getItem().getAttachedSkills();
                if (skills != null && skills.length > 0)
                    for (L2Skill skill : skills) {
                        altUseSkill(skill, this);
                        if (getPet() != null && getPet().isSummon() && !getPet().isDead() && skill.getAbnormalLv() == -1)
                            getPet().altUseSkill(skill, getPet());
                    }
                item.deleteMe();
                broadcastPacket(new GetItem(item, getObjectId()));
                return;
            }

            boolean equip = (item.getCustomFlags() & L2ItemInstance.FLAG_EQUIP_ON_PICKUP) == L2ItemInstance.FLAG_EQUIP_ON_PICKUP;

            if (!isInParty() || equip) {
                if (!getInventory().validateWeight(item)) {
                    sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                    return;
                }

                if (!getInventory().validateCapacity(item)) {
                    sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                    return;
                }

                if (!item.pickupMe(this))
                    return;

                sendPacket(SystemMessage.obtainItems(item));

                Log.LogItem(this, Log.PickupItem, getInventory().addItem(item));

                if (equip)
                    getInventory().equipItem(item, true);

                sendChanges();
            } else if (item.getItemId() == 57) {
                if (!item.pickupMe(this))
                    return;
                getParty().distributeAdena(item, this);
            } else {
                // Нужно обязательно сначало удалить предмет с земли.
                if (!item.pickupMe(null)) {
                    return;
                }
                getParty().distributeItem(this, item);
            }

            item.setItemDropOwner(null, 0);
            broadcastPacket(new GetItem(item, getObjectId()));
            broadcastPickUpMsg(item);
        }
    }

    @Override
    public void setTarget(L2Object newTarget) {
        setTarget(newTarget, true);
    }


    public void setTarget(L2Object newTarget, boolean send_m) {
        //_log.info("setTarget: "+newTarget);
        //Util.test();
        // Check if the new target is visible
        if (ConfigValue.TestActionFail && isGM()) {
            sendMessage("L2Player: setTarget[" + newTarget + "][" + (newTarget != null ? newTarget.isVisible() : "null") + "]");
            _log.info("setTarget:[" + newTarget + "][" + (newTarget != null ? newTarget.isVisible() : "null") + "]");
        }
        if (newTarget != null && !newTarget.isVisible()) {
            newTarget = null;
            if (ConfigValue.TestActionFail && isGM()) {
                sendMessage("L2Player: setTarget[0]");
                _log.info("setTarget:12 null");
            }
        }

        // Can't target and attack festival monsters if not participant
        if (newTarget instanceof L2FestivalMonsterInstance && !isFestivalParticipant()) {
            newTarget = null;
            if (ConfigValue.TestActionFail && isGM()) {
                sendMessage("L2Player: setTarget[1]");
                _log.info("setTarget:13 null");
            }
        }

        L2Party party = getParty();

        // Can't target and attack rift invaders if not in the same room
        if (party != null && party.isInDimensionalRift()) {
            Integer riftType = party.getDimensionalRift().getType();
            Integer riftRoom = party.getDimensionalRift().getCurrentRoom();
            if (newTarget != null && !DimensionalRiftManager.getInstance().getRoom(riftType, riftRoom).checkIfInZone(newTarget.getX(), newTarget.getY(), newTarget.getZ())) {
                newTarget = null;
                if (ConfigValue.TestActionFail && isGM()) {
                    sendMessage("L2Player: setTarget[2]");
                    _log.info("setTarget:14 null");
                }
            }
        }

        L2Object oldTarget = getTarget();

        if (oldTarget != null) {
            if (oldTarget.equals(newTarget)) {
                if (ConfigValue.TestActionFail && isGM()) {
                    sendMessage("L2Player: setTarget[3] equals");
                    _log.info("setTarget:15 equals");
                }
                return;
            }

            // Remove the L2Player from the _statusListener of the old target if it was a L2Character
            if (oldTarget.isCharacter())
                ((L2Character) oldTarget).removeStatusListener(this);

            //Util.test();
            broadcastPacket(new TargetUnselected(this));
        }

        if (newTarget != null) {
            // Add the L2Player to the _statusListener of the new target if it's a L2Character
            if (newTarget.isCharacter())
                ((L2Character) newTarget).addStatusListener(this);

            sendPacket(new MyTargetSelected(newTarget.getObjectId(), (send_m && newTarget.isCharacter()) ? (getLevel() - ((L2Character) newTarget).getLevel()) : 0));
            broadcastPacketToOthers(new TargetSelected(getObjectId(), newTarget.getObjectId(), getLoc()));
        } else {
            sendActionFailed();
            //System.out.println("L2Player setTarget NULL!!! char name: " + getName());
        }

        if (newTarget != null && (oldTarget == null || oldTarget != newTarget) && newTarget.isCharacter() && newTarget != this)
            sendPacket(new ValidateLocation((L2Character) newTarget));

        //_log.info("setTarget: ok1");

        super.setTarget(newTarget);
    }

    /**
     * @return the active weapon instance (always equipped in the right hand).<BR><BR>
     */
    @Override
    public L2ItemInstance getActiveWeaponInstance() {
        if (_disarm.get())
            return null;
        return getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
    }

    /**
     * @return the active weapon item (always equipped in the right hand).<BR><BR>
     */
    @Override
    public L2Weapon getActiveWeaponItem() {
        final L2ItemInstance weapon = getActiveWeaponInstance();

        if (weapon == null)
            return getFistsWeaponItem();

        return (L2Weapon) weapon.getItem();
    }

    @Override
    public WeaponType getFistWeaponType() {
        return WeaponType.FIST;
    }

    /**
     * @return the secondary weapon instance (always equipped in the left hand).<BR><BR>
     */
    @Override
    public L2ItemInstance getSecondaryWeaponInstance() {
        return getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
    }

    /**
     * @return the secondary weapon item (always equipped in the left hand) or the fists weapon.<BR><BR>
     */
    @Override
    public L2Weapon getSecondaryWeaponItem() {
        final L2ItemInstance weapon = getSecondaryWeaponInstance();

        if (weapon == null)
            return getFistsWeaponItem();

        final L2Item item = weapon.getItem();

        if (item instanceof L2Weapon)
            return (L2Weapon) item;

        return null;
    }

    public boolean isWearingArmor(final ArmorType armorType) {
        final L2ItemInstance chest = getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);

        if (chest == null)
            return armorType == ArmorType.NONE;

        if (chest.getItemType() != armorType)
            return false;

        if (chest.getBodyPart() == L2Item.SLOT_FULL_ARMOR)
            return true;

        final L2ItemInstance legs = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);

        return legs == null ? armorType == ArmorType.NONE : legs.getItemType() == armorType;
    }

    @Override
    public void reduceCurrentHp(double i, L2Character attacker, L2Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean isDot, double i2, boolean sendMesseg, boolean bow, boolean crit, boolean tp) {
        if (attacker == null || isDead() || attacker.isDead()) {
            super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, isDot, i2, sendMesseg, bow, crit, tp);
            return;
        }

        if ((isInvul() || block_hp.get()) && attacker != this && !isDot) {
            attacker.sendPacket(Msg.THE_ATTACK_HAS_BEEN_BLOCKED);
            if (ConfigValue.TestAtackFail && attacker.isPlayer() && attacker.getPlayer().isGM()) {
                attacker.sendMessage("L2Player: [" + getName() + "][" + _isInvul + "][" + _isInvul_skill + "][" + block_hp.get() + "]");
                _log.info("L2Player: [" + getName() + "][" + _isInvul + "][" + _isInvul_skill + "][" + block_hp.get() + "]");
                Util.test();
            }
            return;
        }

        if (isInOfflineMode() && attacker.getPlayer() != null && !attacker.getPlayer().isGM()) {
            attacker.sendPacket(Msg.INVALID_TARGET());
            return;
        }

        if (this != attacker && isInOlympiadMode() && !isOlympiadCompStart()) {
            if (attacker != null && attacker.getPlayer() != null)
                attacker.getPlayer().sendPacket(Msg.INVALID_TARGET());
            return;
        }

        if (attacker.isPlayable() && isInZoneBattle() != attacker.isInZoneBattle()) {
            attacker.getPlayer().sendPacket(Msg.INVALID_TARGET());
            return;
        }

        double transMp = calcStat(Stats.TRANSFER_MP_DAMAGE_PERCENT, 0, attacker, skill);

        if (transMp > 0) {
            double damageMp = i * transMp;

            if (damageMp > getCurrentMp()) {
                sendPacket(new SystemMessage(SystemMessage.MP_BECAME_0_ARCANE_SHIELD_DISAPPEARING));
                getEffectList().stopEffect(1556);
                i = damageMp - getCurrentMp();
                setCurrentMp(0);
            } else {
                sendPacket(new SystemMessage(SystemMessage.MP_BECAME_0_ARCANE_SHIELD_DISAPPEARING).addNumber((int) damageMp));
                setCurrentMp(getCurrentMp() - damageMp);
                return;
            }
        }

        double trans = calcStat(Stats.TRANSFER_PET_DAMAGE_PERCENT, 0, attacker, skill);
        if (trans >= 1) {
            try {
                if (_summon != null && !_summon.isDead() && !_summon.isPet() && Util.checkIfInRange(1200, this, _summon, true) && (skill != null && !skill.isToggle() || skill == null) && !_summon.isInZonePeace() && (trans = (i / 100d * trans)) < _summon.getCurrentHp() - 1 && trans > 0) {
                    if (!_summon.isPetrification())
                        _summon.reduceCurrentHp(Math.min(trans, _summon.getCurrentHp() - 1), attacker, null, false, false, false, false, false, Math.min(trans, _summon.getCurrentHp() - 1), true, bow, crit, true);
                    i -= trans;
                }
            } catch (Exception e) {
                getEffectList().stopEffect(L2Skill.SKILL_TRANSFER_PAIN);
                getEffectList().stopEffect(711); // Divine Summoner Transfer Pain
                getEffectList().stopEffect(3667); // Yellow Talisman - Damage Transition
            }
        }

        if (attacker != this && sendMesseg)
            sendRDmgMsg(this, attacker, skill, (long) i, crit, false);

        double hp = directHp ? getCurrentHp() : getCurrentHp() + getCurrentCp();

        if (getDuel() != null)
            if (getDuel() != attacker.getDuel())
                getDuel().setDuelState(this, DuelState.Interrupted);
            else if (getDuel().getDuelState(this) == DuelState.Interrupted) {
                attacker.getPlayer().sendPacket(Msg.INVALID_TARGET());
                return;
            } else if (i >= hp) {
                setCurrentHp(1, false);
                getDuel().onPlayerDefeat(this);
                getDuel().stopFighting(attacker.getPlayer());
                return;
            }

        if (isInOlympiadMode() && _olympiadGame != null && i > 0) {
            if (_olympiadGame.getState() <= 0) {
                attacker.getPlayer().sendPacket(Msg.INVALID_TARGET());
                return;
            }
            if (this != attacker)
                addDamageMy((int) Math.min(hp, i));

            if (i >= hp && _olympiadGame.getType() != CompType.TEAM && _olympiadGame.getType() != CompType.TEAM_RANDOM) {
                _olympiadGame.setWinner(getOlympiadSide() == 1 ? 2 : 1);
                _olympiadGame.endGame(20000, false, null);
                setCurrentHp(1, false);
                attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                attacker.sendActionFailed();
                return;
            }
        } else if (_team > 0 && i > 0) {
            if (isInEvent() == 1)
                addDamageMy((int) Math.min(hp, i));
            else if (attacker.getPlayer() != null)
                attacker.getPlayer().addDamageMy((int) Math.min(hp, i));
        }
        // Reduce the current HP of the L2Player
        super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, isDot, i + trans, sendMesseg, bow, crit, tp);

        if (getEventMaster() != null)
            getEventMaster().reduceCurrentHp(i, this, attacker, skill);
        if (getAttainment() != null && attacker.isPlayer())
            getAttainment().reduceCurrentHp(i, attacker.getPlayer());
        //TODO: переделать на листенер
        if (getLevel() < 6 && getCurrentHpPercents() < 25) {
            Quest q = QuestManager.getQuest(255);
            if (q != null)
                processQuestEvent(q.getName(), "CE45", null);
        }
    }

    private void altDeathPenalty(final L2Character killer) {
        // Reduce the Experience of the L2Player in function of the calculated Death Penalty
        if (!ConfigValue.Delevel || killer == null)
            return;
        else if (isInZoneBattle() && killer.isPlayable())
            return;
        deathPenalty(killer);
    }

    public final boolean atWarWith(final L2Player player) {
        return _clan != null && player.getClan() != null && getPledgeType() != -1 && player.getPledgeType() != -1 && _clan.isAtWarWith(player.getClan().getClanId());
    }

    public boolean atMutualWarWith(L2Player player) {
        return _clan != null && player.getClan() != null && getPledgeType() != -1 && player.getPledgeType() != -1 && _clan.isAtWarWith(player.getClan().getClanId()) && player.getClan().isAtWarWith(_clan.getClanId());
    }

    public final void doPurePk(final L2Player killer) {
        // Check if the attacker has a PK counter greater than 0
        double pkCountMulti = Math.max(killer.getPkKills() / 2, 1);

        // Calculate the level difference Multiplier between attacker and killed L2Player
        //int lvlDiffMulti = Math.max(killer.getLevel() / _level, 1);

        // Calculate the new Karma of the attacker : newKarma = baseKarma*pkCountMulti*lvlDiffMulti
        // Add karma to attacker and increase its PK counter
        if (killer.getPkKills() > 1)
            pkCountMulti += 0.5;
        killer.increaseKarma((long) (ConfigValue.MinKarma * pkCountMulti));
        killer.setPkKills(killer.getPkKills() + 1);
        PlayerRewardManager.getInstance().inc_pc(killer, this);
    }

    private void setFameFoPVP(L2Player killer) {
        if (ConfigValue.setFameFoPVP || ConfigValue.setItemFoPVP) {
            if (ConfigValue.setItemFoPVPCheckClan && killer.getClan() != null && killer.getClan() == getClan()) {
                if (killer.isGM())
                    killer.sendMessage("setFameFoPVP[" + ConfigValue.setFameFoPVP + "][" + ConfigValue.setItemFoPVP + "]: Error clan.");
                return;
            } else if (ConfigValue.setItemFoPVPMinLevel > getLevel()) {
                if (killer.isGM())
                    killer.sendMessage("setFameFoPVP[" + ConfigValue.setFameFoPVP + "][" + ConfigValue.setItemFoPVP + "]: Error Level[" + ConfigValue.setItemFoPVPMinLevel + "]>[" + getLevel() + "].");
                return;
            } else if (ConfigValue.setItemFoPVPCheckIp && getIP().equals(killer.getIP())) {
                if (killer.isGM())
                    killer.sendMessage("setFameFoPVP[" + ConfigValue.setFameFoPVP + "][" + ConfigValue.setItemFoPVP + "]: Error IP.");
                return;
            } else if (ConfigValue.setItemFoPVPCheckHwid && getHWIDs().equals(killer.getHWIDs()) && ConfigValue.ProtectEnable) {
                if (killer.isGM())
                    killer.sendMessage("setFameFoPVP[" + ConfigValue.setFameFoPVP + "][" + ConfigValue.setItemFoPVP + "]: Error HWID.");
                return;
            } else if (!hasSetFame()) {
                if (killer.isGM())
                    killer.sendMessage("setFameFoPVP[" + ConfigValue.setFameFoPVP + "][" + ConfigValue.setItemFoPVP + "]: Error reuse[" + ((_set_fame - System.currentTimeMillis()) / 1000) + " s] kill.");
                return;
            } else if (!isInZone(set_fame)) {
                if (killer.isGM())
                    killer.sendMessage("setFameFoPVP[" + ConfigValue.setFameFoPVP + "][" + ConfigValue.setItemFoPVP + "]: Error died not fame zone.");
                return;
            } else if (!killer.isInZone(set_fame)) {
                if (killer.isGM())
                    killer.sendMessage("setFameFoPVP[" + ConfigValue.setFameFoPVP + "][" + ConfigValue.setItemFoPVP + "]: Error killer not fame zone.");
                return;
            }

            L2Zone z = ZoneManager.getInstance().getZoneByTypeAndObject(set_fame, killer);
            final Calendar c = Calendar.getInstance();
            if (z.getHourOfDay() != null && z.getHourOfDay().length > 0 && z.getHourOfDay().length < 24 && !Util.contains(z.getHourOfDay(), c.get(Calendar.HOUR_OF_DAY))) {
                if (killer.isGM())
                    killer.sendMessage("setFameFoPVP[" + ConfigValue.setFameFoPVP + "][" + ConfigValue.setItemFoPVP + "]: Error incorect time[" + c.get(Calendar.HOUR_OF_DAY) + "].");
                return;
            }
            if (ConfigValue.setFameFoPVP && z.getIndex() > 0)
                killer.setFame((int) (killer.getFame() + z.getIndex() * killer.getRateFame()), "PVP");
            if (ConfigValue.setItemFoPVP && z.item_reward != null) {
                for (L2Zone.ZoneItemInfo zii : z.item_reward)
                    if ((!zii.hwid || !getHWIDs().equals(killer.getHWIDs())) && (!zii.ip || !getIP().equals(killer.getIP())) && Rnd.chance(zii.chance)) {
                        long item_count = Rnd.get(zii.item_count_min, zii.item_count_max);

                        killer.getPlayer().getInventory().addItem(zii.item_id, item_count);
                        killer.getPlayer().sendPacket(SystemMessage.obtainItems(zii.item_id, item_count, 0));
                    }
            }
            setMyFame();
        }
    }

    public final void doKillInPeace(final L2Player killer) // Check if the L2Player killed haven't Karma
    {
        if (_karma <= 0)
            doPurePk(killer);
        else {
            if (checkPvP(killer)) {
                killer.setPvpKills(killer.getPvpKills() + 1);
                PlayerRewardManager.getInstance().inc_pvp(killer, this);
            }
        }
    }

    //
    public void checkAddItemToDrop(GArray<L2ItemInstance> array, GArray<L2ItemInstance> items, int maxCount) {
        for (int i = 0; i < maxCount && !items.isEmpty(); i++)
            array.add(items.remove(Rnd.get(items.size())));
    }

    protected void doPKPVPManage(L2Character killer) {
        if (isCombatFlagEquipped()) {
            L2ItemInstance flag = getActiveWeaponInstance();
            if (flag != null) {
                int customFlags = flag.getCustomFlags();
                flag.setCustomFlags(0, false);
                flag = getInventory().dropItem(flag, 1, true);
                flag.setCustomFlags(customFlags, false);
                flag.spawnMe2(flag.getLoc().correctGeoZ(), false);
                sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_DROPPED_S1).addItemName(flag.getItemId()));
            }
        }

        if (isTerritoryFlagEquipped()) {
            L2ItemInstance flag = getActiveWeaponInstance();
            if (flag != null && flag.getCustomType1() != 77) // 77 это эвентовый флаг
            {
                L2TerritoryFlagInstance flagNpc = TerritorySiege.getNpcFlagByItemId(flag.getItemId());
                flagNpc.drop(this);

                sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_DROPPED_S1).addItemName(flag.getItemId()));
                String terrName = CastleManager.getInstance().getCastleByIndex(flagNpc.getBaseTerritoryId()).getName();
                TerritorySiege.announceToPlayer(new SystemMessage(SystemMessage.THE_CHARACTER_THAT_ACQUIRED_S1_WARD_HAS_BEEN_KILLED).addString(terrName), true);
            }
        }

        for (L2ItemInstance item : getInventory().getItemsList())
            if ((item.getCustomFlags() & L2ItemInstance.FLAG_ALWAYS_DROP_ON_DIE) == L2ItemInstance.FLAG_ALWAYS_DROP_ON_DIE) {
                item = getInventory().dropItem(item, item.getCount(), false);
                item.dropMe(this, getLoc().rnd(0, 100, false));
                sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_DROPPED_S1).addItemName(item.getItemId()));
            }

        if (killer == null || killer == _summon)
            return;
        else if (killer.getObjectId() == _objectId)
            return;
        else if (killer.isPlayable() && (killer = killer.getPlayer()) == null)
            return;
        else if (killer.isPlayable())
            setFameFoPVP(killer.getPlayer());
        if (isInZoneBattle() || killer.isInZoneBattle())
            return;
        else if (killer.isPlayable()) {
            if (ConfigValue.RangEnable) {
                int raznica = killer.getPlayer().getLevel() - getLevel();
                if (10 >= raznica) {
                    long point = getRangPoint();

                    point = (long) ((point / 100f) * ConfigValue.RangPercentTransferPointChar[getRangId()]);
                    if (point > getRangPoint())
                        point = getRangPoint();
                    if (killer.getPlayer().getParty() != null) {
                        float point2 = (float) point / killer.getPlayer().getParty().getMemberCount() + 0.5f;
                        for (L2Player pl : killer.getPlayer().getParty().getPartyMembers()) {
                            pl.getPlayer().addRangPoint((long) point2);
                            pl.getPlayer().sendMessage("Получено " + ((long) point2) + " Очков Воина. Всего " + killer.getPlayer().getRangPoint() + " Очков Воина.");
                        }
                    } else {
                        killer.getPlayer().addRangPoint(point);
                        killer.getPlayer().sendMessage("Получено " + point + " Очков Воина. Всего " + killer.getPlayer().getRangPoint() + " Очков Воина.");
                    }
                    addRangPoint(-point);

                    sendMessage("Потеряно " + point + " Очков Воина. Всего " + getRangPoint() + " Очков Воина.");
                }
            }
        } else if (killer.isMonster()) {
            if (ConfigValue.RangEnable) {
                long point = getRangPoint();
                point = (long) ((point / 100f) * ConfigValue.RangPercentTransferPointMob[getRangId()]);
                if (point > getRangPoint())
                    point = getRangPoint();
                addRangPoint(-point);
                sendMessage("Потеряно " + point + " Очков Воина. Всего " + getRangPoint() + " Очков Воина.");
            }
        }

        // Processing Karma/PKCount/PvPCount for killer
        if (killer.isPlayer()) {
            final L2Player pk = (L2Player) killer;
            final int repValue = getLevel() - pk.getLevel() >= 20 ? 2 : 1;
            boolean war = atMutualWarWith(pk);

            if (getLevel() > 4 && _clan != null && pk.getClan() != null)
                if (war || _clan.getSiege() != null && _clan.getSiege() == pk.getClan().getSiege() && (_clan.isDefender() && pk.getClan().isAttacker() || _clan.isAttacker() && pk.getClan().isDefender()))
                    if (pk.getClan().getReputationScore() > 0 && _clan.getLevel() >= 5 && _clan.getReputationScore() > 0 && pk.getClan().getLevel() >= 5) {
                        _clan.incReputation(-repValue, true, "ClanWar");
                        pk.getClan().incReputation(repValue, true, "ClanWar");
                        //_clan.broadcastToOtherOnlineMembers(new SystemMessage(SystemMessage.YOUR_CLAN_MEMBER_S1_WAS_KILLED_S2_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_CLAN_REPUTATION_SCORE_AND_ADDED_TO_YOUR_OPPONENT_CLAN_REPUTATION_SCORE).addString(getName()).addNumber(-_clan.incReputation(-repValue, true, "ClanWar")), this);
                        //pk.getClan().broadcastToOtherOnlineMembers(new SystemMessage(SystemMessage.FOR_KILLING_AN_OPPOSING_CLAN_MEMBER_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_OPPONENTS_CLAN_REPUTATION_SCORE).addNumber(pk.getClan().incReputation(repValue, true, "ClanWar")), pk);
                    }

            if (isInZone(Siege)) {
                if (pk.getTerritorySiege() > -1 && getTerritorySiege() > -1 && pk.getTerritorySiege() != getTerritorySiege() && pk.getLevel() - getLevel() < 10 && pk.getLevel() > 61 && getLevel() > 61) {
                    if (getClanId() > 0 && getClanId() == pk.getClanId() || getAllyId() > 0 && getAllyId() == pk.getAllyId())
                        return;
                    //addReward(member, STATIC_BADGES, 5, 1);
                }
                return;
            } else if (pk.getTerritorySiege() > -1 && getTerritorySiege() > -1 && pk.getTerritorySiege() != getTerritorySiege())
                return;

            if (_pvpFlag > 0 || war || isFactionWar(pk)) {
                if (checkPvP(pk)) {
                    pk.setPvpKills(pk.getPvpKills() + 1);
                    PlayerRewardManager.getInstance().inc_pvp(pk, this);
                    if (getPryze(killer))
                        killer.getPlayer().getInventory().addItem(ConfigValue.toPvPItem, ConfigValue.toPvPItemCount);
                }
            } else {
                doKillInPeace(pk);
                if (getPryze(killer))
                    killer.getPlayer().getInventory().addItem(ConfigValue.toPvPItem, ConfigValue.toPvPItemCount);
            }

            if (ConfigValue.AddRndItemForPvpChance > 0 && ConfigValue.AddRndItemForPvp.length > 0 && Rnd.chance(ConfigValue.AddRndItemForPvpChance) && !getHWIDs().equals(killer.getPlayer().getHWIDs())) {
                long[] items = ConfigValue.AddRndItemForPvp[Rnd.get(ConfigValue.AddRndItemForPvp.length)];
                killer.getPlayer().getInventory().addItem((int) items[0], items[1]);
            }

            if (ConfigValue.SetFameForPvpPk > 0 && !getHWIDs().equals(killer.getPlayer().getHWIDs()))
                killer.getPlayer().setFame((int) (killer.getPlayer().getFame() + ConfigValue.SetFameForPvpPk * killer.getRateFame()), "PVP2");

            // Send a Server->Client UserInfo packet to attacker with its PvP Kills Counter
            pk.sendUserInfo(false);
        }

        int karma = _karma;
        decreaseKarma(ConfigValue.BaseKarmaLost);

        // в нормальных условиях вещи теряются только при смерти от гварда или игрока
        // кроме того, альт на потерю вещей при сметри позволяет терять вещи при смтери от монстра
        boolean isPvP = killer.isPlayable() || killer instanceof L2GuardInstance;

        if (killer.isMonster() && !ConfigValue.DropOnDie // если убил монстр и альт выключен
                || isPvP // если убил игрок или гвард и
                && (_pkKills < ConfigValue.MinPKToDropItems // количество пк слишком мало
                || karma == 0 && ConfigValue.KarmaNeededToDrop) // кармы нет
                || isFestivalParticipant() // в фестивале вещи не теряются
                || !killer.isMonster() && !isPvP) // в прочих случаях тоже
            return;

        // No drop from GM's
        if (!ConfigValue.CanGMDropEquipment && isGM() || getInventory().getItemsList().isEmpty())
            return;

        if (ConfigValue.CanTradeBanDropEquipment) {
            String tradeBan = getVar("tradeBan");
            if (tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
                return;
        }

        final int max_drop_count = isPvP ? ConfigValue.MaxItemsDroppable : 1;

        double dropRate; // базовый шанс в процентах
        if (isPvP)
            dropRate = (_pkKills * ConfigValue.ChanceOfPKsDropMod + ConfigValue.ChanceOfPKDropBase);
        else
            dropRate = ConfigValue.ChanceOfNormalDropBase;

        int dropEquipCount = 0, dropWeaponCount = 0, dropItemCount = 0;

        for (int i = 0; i < Math.ceil(dropRate / 100) && i < max_drop_count; i++)
            if (Rnd.chance(dropRate)) {
                int rand = Rnd.get(ConfigValue.ChanceOfDropWeapon + ConfigValue.ChanceOfDropEquippment + ConfigValue.ChanceOfDropOther) + 1;
                if (rand > ConfigValue.ChanceOfDropWeapon + ConfigValue.ChanceOfDropEquippment)
                    dropItemCount++;
                else if (rand > ConfigValue.ChanceOfDropWeapon)
                    dropEquipCount++;
                else
                    dropWeaponCount++;
            }

        GArray<L2ItemInstance> dropped_items = new GArray<L2ItemInstance>(), // общий массив с результатами выбора
                dropItem = new GArray<L2ItemInstance>(), dropEquip = new GArray<L2ItemInstance>(), dropWeapon = new GArray<L2ItemInstance>(); // временные

        for (L2ItemInstance item : getInventory().getItems()) {
            if (!item.canBeDropped(this, true) || Util.contains_int(ConfigValue.ListOfNonDroppableItems, item.getItemId()))
                continue;

            if (item.getItem().getType2() == L2Item.TYPE2_WEAPON)
                dropWeapon.add(item);
            else if (item.getItem().getType2() == L2Item.TYPE2_SHIELD_ARMOR || item.getItem().getType2() == L2Item.TYPE2_ACCESSORY)
                dropEquip.add(item);
            else if (item.getItem().getType2() == L2Item.TYPE2_OTHER)
                dropItem.add(item);
        }

        checkAddItemToDrop(dropped_items, dropWeapon, dropWeaponCount);
        checkAddItemToDrop(dropped_items, dropEquip, dropEquipCount);
        checkAddItemToDrop(dropped_items, dropItem, dropItemCount);

        // Dropping items, if present
        if (dropped_items.isEmpty())
            return;

        for (L2ItemInstance item : dropped_items) {
            if (item.isEquipped())
                getInventory().unEquipItemInSlot(item.getEquipSlot());

            if (item.isAugmented() && !ConfigValue.AllowDropAugmented)
                PlayerData.getInstance().removeAugmentation(item);

            item = getInventory().dropItem(item, item.getCount(), false);

            if (ConfigValue.MonstersLooters && killer.isMonster() && !item.isCursed() && _reflection <= 0) {
                if (killer.isMinion() && !((L2MinionInstance) killer).getLeader().isDead())
                    ((L2MinionInstance) killer).getLeader().giveItem(item, true);
                else
                    ((L2MonsterInstance) killer).giveItem(item, true);
            } else if (killer.isPlayer() && ConfigValue.AutoLoot && ConfigValue.AutoLootPK)
                ((L2Player) killer).getInventory().addItem(item);
            else if (killer.isSummon() && ConfigValue.AutoLoot && ConfigValue.AutoLootPK)
                killer.getPlayer().getInventory().addItem(item);
            else
                item.dropMe(this, getLoc().rnd(0, ConfigValue.MaxDropThrowDistance, false));

            if (item.getEnchantLevel() > 0)
                sendPacket(new SystemMessage(SystemMessage.DROPPED__S1_S2).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
            else
                sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_DROPPED_S1).addItemName(item.getItemId()));
        }
        refreshOverloaded();
    }

    public boolean getPryze(L2Object killer) {
        if (ConfigValue.toPvPItem == 0)
            return false;
        try {
            for (int id : ConfigValue.PvPZoneIds)
                if (ZoneManager.getInstance().getZoneById(ZoneType.other, id, true).checkIfInZone(killer))
                    return true;
        } catch (Exception e) {
        }
        return false;
    }

    public void twQuestNotify(L2Player killer, Quest q) {
        QuestState questState = killer.getQuestState(q.getName());
        if (questState == null) {
            questState = q.newQuestState(killer, Quest.CREATED);
            q.notifyPlayerKill(this, questState);
        } else if (questState.getState() == Quest.COMPLETED)
            ;
        else
            q.notifyPlayerKill(this, questState);
    }

    @Override
    public void doDie(L2Character killer) {
        dieLock.lock();
        try {
            if (_killedAlreadyPlayer)
                return;
            _killedAlreadyPlayer = true;
        } finally {
            dieLock.unlock();
        }

        if (isMounted()) {
            stopFeed();
            _mount = null;
        }

        //Check for active charm of luck for death penalty
        try {
            getDeathPenalty().checkCharmOfLuck();
        } catch (Exception e) {
        }

        L2TradeList tl = getTradeList();
        if (tl != null) {
            tl.removeAll();
            setTradeList(null);
        }

        if (isInTransaction()) {
            if (getTransaction().isTypeOf(TransactionType.TRADE))
                sendPacket(new SendTradeDone(0));
            getTransaction().cancel();
        }

        setPrivateStoreType(L2Player.STORE_PRIVATE_NONE);

        if (killer != null) {
            if (killer.isPlayable() && killer.getPlayer() != null && killer.getPlayer().getAttainment() != null)
                killer.getPlayer().getAttainment().kill_char(this);
            if (getAttainment() != null && killer.isPlayer())
                getAttainment().doDie(killer.getPlayer());
            try {
                if (checkSiegeFame(killer)) {
                    killer.getPlayer().setFame((int) (killer.getPlayer().getFame() + Rnd.get(ConfigValue.SiegeFameOnKillMin, ConfigValue.SiegeFameOnKillMax) * killer.getRateFame()), "OnKillSiegeFame");
                    if (ConfigValue.EnableFameProtect)
                        setMyFame();

                    TerritorySiege.addReward(killer.getPlayer(), TerritorySiege.KILL_REWARD, 1, getTerritorySiege());

                    if (getLevel() >= 61) {
                        Quest q = getClassQuest(getClassId());
                        if (q != null) {
                            L2Party party = killer.getPlayer().getParty();
                            if (party == null)
                                twQuestNotify(killer.getPlayer(), q);
                            else
                                for (L2Player member : party.getPartyMembers())
                                    if (member != null && member.isInRange(killer.getPlayer(), 2000))
                                        twQuestNotify(member, q);
                        }
                    }
                }
            } catch (Exception e) {
            }
        }

        // Kill the L2Player
        super.doDie(killer);

        // Dont unsummon a summon, it can kill few enemies. But pet must returned back into its item
        // Unsummon siege summons
        if (_summon != null && _summon.isSiegeWeapon())
            _summon.unSummon();

        // Unsummon Cubics and agathion
        if (!isBlessedByNoblesse() && !isSalvation()) {
            for (L2Cubic cubic : getCubics())
                cubic.deleteMe();
            getCubics().clear();
        }

        setAgathion(0);

        if (ConfigValue.LogKills && killer != null) {
            String coords = " at (" + getX() + "," + getY() + "," + getZ() + ")";
            if (killer.isNpc())
                Log.add("" + this + " karma " + _karma + " killed by mob " + killer.getNpcId() + coords, "kills");
            else if (killer instanceof L2Summon && killer.getPlayer() != null)
                Log.add("" + this + " karma " + _karma + " killed by summon of " + killer.getPlayer() + coords, "kills");
            else
                Log.add("" + this + " karma " + _karma + " killed by " + killer + coords, "kills");
        }

        boolean checkPvp = true;
        if (ConfigValue.AllowCursedWeapons)
            if (isCursedWeaponEquipped()) {
                CursedWeaponsManager.getInstance().dropPlayer(this);
                checkPvp = false;
            } else if (killer != null && killer.isPlayer() && killer.isCursedWeaponEquipped()) {
                CursedWeaponsManager.getInstance().increaseKills(((L2Player) killer).getCursedWeaponEquippedId());
                checkPvp = false;
            }

        if (checkPvp) {
            doPKPVPManage(killer);

            altDeathPenalty(killer);
        }

        if (killer != null && killer.isPlayable())
            set_no_kill_time();

        //And in the end of process notify death penalty that owner died :)
        getDeathPenalty().notifyDead(killer);

        setIncreasedForce(0);

        if (isInParty() && getParty().isInReflection() && getParty().getReflection() instanceof DimensionalRift)
            ((DimensionalRift) getParty().getReflection()).memberDead(this);

        stopWaterTask();
        getNevitBlessing().stopBuff();
        getNevitBlessing().stopBonus();
        getRecommendation().stopRecBonus();

        if (!isSalvation() && isInZone(Siege) && isCharmOfCourage()) {
            _reviveRequested = true;
            _revivePower = 100;
            sendPacket(new ConfirmDlg(SystemMessage.RESURRECTION_IS_POSSIBLE_BECAUSE_OF_THE_COURAGE_CHARM_S_EFFECT_WOULD_YOU_LIKE_TO_RESURRECT_NOW, 60000, 2));
            setCharmOfCourage(false);
        }

        if (getLevel() < 6) {
            Quest q = QuestManager.getQuest(255);
            if (q != null)
                processQuestEvent(q.getName(), "CE30", null);
        }

        if (ConfigValue.RemoveAutoShot)
            for (int itemId : getAutoSoulShot()) {
                removeAutoSoulShot(itemId);
                //sendPacket(new ExAutoSoulShot(itemId, false));
            }
    }

    public boolean checkSiegeFame(L2Character killer) {
        if (getTerritorySiege() > -1) {
            L2Player _killer = killer.getPlayer();
            if (_killer != null) {
                if (_killer.getTerritorySiege() < 0 || getTerritorySiege() < 0 || _killer.getTerritorySiege() == getTerritorySiege())
                    return false;
                if (_killer.getLevel() < 61 || getLevel() < 61)
                    return false;
                if (!hasSetFame())
                    return false;
				/*if(_killer.getLevel() - getLevel() > 10) // уточнить, мб и не нужно...
					return false;*/
				/*if(_killer.getAllyId() > 0 && getAllyId() > 0 && getAllyId() == _killer.getAllyId()) // уточнить, мб не нужно...
					return false;*/
				/*if(ConfigValue.isOnlySiegeZone)
				{
					if(isInZone(L2Zone.ZoneType.Siege) && killer.isInZone(L2Zone.ZoneType.Siege))
						return true;
				}
				else */
                if ((isInZone(L2Zone.ZoneType.battle_zone) && killer.isInZone(L2Zone.ZoneType.battle_zone)) || (isInZone(L2Zone.ZoneType.Siege) && killer.isInZone(L2Zone.ZoneType.Siege)))
                    return true;
            }
        } else {
            if (isInZone(L2Zone.ZoneType.Siege) && killer.isInZone(L2Zone.ZoneType.Siege)) {
                L2Clan clan1 = getClan();
                L2Clan clan2 = killer.getPlayer().getClan();
                if (clan1 != null && clan2 != null) {
                    if (!killer.isPlayer())
                        return false;
                    if (clan1 == null || clan2 == null)
                        return false;
                    if (clan1.getSiege() == null || clan2.getSiege() == null)
                        return false;
                    if (clan1.getSiege() != clan2.getSiege())
                        return false;
                    if (clan1.isDefender() && clan2.isDefender())
                        return false;
                    if (killer.getPlayer().getLevel() < 40 || getLevel() < 40)
                        return false;
                    if (!hasSetFame())
                        return false;
                    return true;
                }
            }
        }
        return false;
    }

    public void restoreExp() {
        restoreExp(100.);
    }

    public void restoreExp(double percent) {
        if (percent == 0)
            return;

        int lostexp = 0;

        String lostexps = getVar("lostexp");
        if (lostexps != null) {
            lostexp = Integer.parseInt(lostexps);
            unsetVar("lostexp");
        }

        if (lostexp != 0)
            addExpAndSp((long) (lostexp * percent / 100), 0, false, false, 0, 0, null);
    }

    public void deathPenalty(L2Character killer) {
        // зачем нам это на ивентах.
        if (getEventMaster() != null)
            return;
        //if(_bonusNevitActive)
        //	return;
        final boolean atwar = killer.getPlayer() != null ? atWarWith(killer.getPlayer()) : false;

        double deathPenaltyBonus = getDeathPenalty().getLevel() * ConfigValue.DeathPenaltyC5RateExpPenalty;
        if (deathPenaltyBonus < 2)
            deathPenaltyBonus = 1;
        else
            deathPenaltyBonus = deathPenaltyBonus / 2;

        // The death steal you some Exp: 10-40 lvl 8% loose
        double percentLost = 8.0;

        byte level = getLevel();
        if (level >= 79)
            percentLost = 1.0;
        else if (level >= 78)
            percentLost = 1.5;
        else if (level >= 76)
            percentLost = 2.0;
        else if (level >= 40)
            percentLost = 4.0;

        if (ConfigValue.EnableAltDeathPenalty)
            percentLost = percentLost * RateService.getRateXp(this) + _pkKills * ConfigValue.AltPKDeathRate;

        if (isFestivalParticipant() || atwar)
            percentLost = percentLost / 4.0;

        // Calculate the Experience loss
        int lostexp = (int) Math.round((Experience.LEVEL[level + 1] - Experience.LEVEL[level]) * percentLost / 100);
        lostexp *= deathPenaltyBonus;
        lostexp = (int) calcStat(Stats.EXP_LOST, lostexp, killer, null);
        // На зарегистрированной осаде нет потери опыта, на чужой осаде - как при обычной смерти от *моба*
        if (isInZone(Siege)) {
            Siege siege = SiegeManager.getSiege(this, true);
            if (siege != null && siege.isParticipant(this))
                lostexp = 0;

            if (getTerritorySiege() > -1 && TerritorySiege.checkIfInZone(this))
                lostexp = 0;

            // Battlefield Death Syndrome
            GArray<L2Effect> effect = getEffectList().getEffectsBySkillId(L2Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
            if (effect != null) {
                int syndromeLvl = effect.get(0).getSkill().getLevel();
                if (syndromeLvl < 5) {
                    getEffectList().stopEffect(L2Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
                    L2Skill skill = SkillTable.getInstance().getInfo(L2Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, syndromeLvl + 1);
                    skill.getEffects(this, this, false, false);
                } else if (syndromeLvl == 5) {
                    getEffectList().stopEffect(L2Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
                    L2Skill skill = SkillTable.getInstance().getInfo(L2Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, 5);
                    skill.getEffects(this, this, false, false);
                }
            } else {
                L2Skill skill = SkillTable.getInstance().getInfo(L2Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, 1);
                if (skill != null)
                    skill.getEffects(this, this, false, false);
            }
        }

        if (getNevitBlessing().isBuffActive())
            return;

        _log.fine(_name + "is dead, so exp to remove:" + lostexp);

        long before = getExp();
        addExpAndSp(-lostexp, 0, false, false);
        long lost = before - getExp();

        if (lost > 0)
            setVar("lostexp", String.valueOf(lost));
    }

    public void setPartyMatchingLevels(int levels) {
        _partyMatchingLevels = levels;
    }

    public int getPartyMatchingLevels() {
        return _partyMatchingLevels;
    }

    public void setPartyMatchingRegion(int region) {
        _partyMatchingRegion = region;
    }

    public int getPartyMatchingRegion() {
        return _partyMatchingRegion;
    }

    public Integer getPartyRoom() {
        return _partyRoom;
    }

    public void setPartyRoom(Integer partyRoom) {
        _partyRoom = partyRoom;
    }

    public void setTransaction(Transaction transaction) {
        _transaction = transaction;
    }

    public Transaction getTransaction() {
        return _transaction;
    }

    public boolean isInTransaction() {
        if (_transaction == null)
            return false;
        else if (!_transaction.isInProgress())
            return false;
        return true;
    }

    public GArray<L2GameServerPacket> addVisibleObject(L2Object object, L2Character dropper) {
        GArray<L2GameServerPacket> result = new GArray<L2GameServerPacket>();

        if (isLogoutStarted() || object == null || object.getObjectId() == getObjectId() || !object.isVisible())
            return result;

        if (object.isTrap()) {
            L2Character owner = ((L2TrapInstance) object).getOwner();
            if (!((L2TrapInstance) object).isDetected() && owner != this && (owner.getParty() == null || owner.getParty() != getParty()))
                return result;
        }

        if (object.isPolymorphed())
            switch (object.getPolytype()) {
                case L2Object.POLY_ITEM:
                    result.add(new SpawnItemPoly(object));
                    showMoves(result, object);
                    return result;
                case L2Object.POLY_NPC:
                    result.add(new NpcInfo(object.getPlayer()));
                    showMoves(result, object);
                    return result;
            }

        if (object.isFence()) {
            result.add(((L2Fence) object).newCharInfo());
            return result;
        } else if (object.isItem()) {
            if (dropper != null)
                result.add(new DropItem((L2ItemInstance) object, dropper.getObjectId()));
            else
                result.add(new SpawnItem((L2ItemInstance) object));
            return result;
        } else if (object.isDoor()) {
            result.add(new StaticObject((L2DoorInstance) object));
            return result;
        } else if (object instanceof L2StaticObjectInstance) {
            result.add(new StaticObject((L2StaticObjectInstance) object));
            return result;
        }

        if (object instanceof L2ClanHallManagerInstance)
            ((L2ClanHallManagerInstance) object).sendDecoInfo(this);

        if (object.isNpc()) {
            L2NpcInstance npc = (L2NpcInstance) object;
            result.add(new NpcInfo(npc, this));
            result.add(new ExChangeNpcState(npc.getObjectId(), npc.getNpcState()));
            showMoves(result, object);

            if (object.getAI() instanceof DefaultAI && !object.getAI().isActive())
                object.getAI().startAITask();

            return result;
        }

        if (object instanceof L2Summon) {
            L2Summon summon = (L2Summon) object;
            L2Player owner = summon.getPlayer();

            if (owner == this) {
                result.add(new PetInfo(summon, 2));
                result.add(new PartySpelled(summon, true));

                if (summon.isPet() && owner.getInventory().getItemByObjectId(((L2PetInstance) summon).getControlItem().getObjectId()) != null)
                    result.add(new PetItemList((L2PetInstance) summon));
            } else {
                L2Party party = getParty();
                if (getReflectionId() == -2 && (owner == null || party == null || party != owner.getParty())) // Чужие петы в GH не показываются для уменьшения лагов.
                    return result;
                result.add(new NpcInfo(summon, this, 2));
                if (owner != null && party != null && party == owner.getParty())
                    result.add(new PartySpelled(summon, true));
                //result.addAll(RelationChanged.update(this, owner, this));
            }

            showMoves(result, object);
            return result;
        }

        if (object.isPlayer()) {
            final L2Player otherPlayer = (L2Player) object;
            if (otherPlayer.isInvisible() && getObjectId() != otherPlayer.getObjectId() || otherPlayer.getPrivateStoreType() != STORE_PRIVATE_NONE && getVarB("notraders"))
                return result;

            if (getObjectId() != otherPlayer.getObjectId()) {
                result.add(otherPlayer.newCharInfo());
                if (ConfigValue.EnableLindvior && otherPlayer.getPvpFlag() > 0)
                    result.add(new StatusUpdate(otherPlayer.getObjectId()).addAttribute(StatusUpdate.PVP_FLAG, otherPlayer.getPvpFlag()));
                // хуевый затык для трансформаций, где чар садится на разную поебень...
                if (otherPlayer.isInMountTransform()) {
                    result.add(new CharInfo(otherPlayer));
                    result.add(new ExBrExtraUserInfo(otherPlayer));
                }
            }

            if (otherPlayer.getPrivateStoreType() != STORE_PRIVATE_NONE) {
                if (otherPlayer.getPrivateStoreType() == STORE_PRIVATE_BUY)
                    result.add(new PrivateStoreMsgBuy(otherPlayer));
                else if (otherPlayer.getPrivateStoreType() == STORE_PRIVATE_SELL)
                    result.add(new PrivateStoreMsgSell(otherPlayer, false));
                else if (otherPlayer.getPrivateStoreType() == STORE_PRIVATE_SELL_PACKAGE)
                    result.add(new PrivateStoreMsgSell(otherPlayer, true));
                else if (otherPlayer.getPrivateStoreType() == STORE_PRIVATE_MANUFACTURE)
                    result.add(new RecipeShopMsg(otherPlayer));
                if (isInZonePeace()) // Мирным торговцам не нужно посылать больше пакетов, для экономии траффика
                    return result;
            }

            if (otherPlayer.isCastingNow()) {
                L2Character castingTarget = otherPlayer.getCastingTarget();
                L2Skill castingSkill = otherPlayer.getCastingSkill();
                long animationEndTime = otherPlayer.getAnimationEndTime();
                if (castingSkill != null && castingTarget != null && castingTarget.isCharacter() && otherPlayer.getAnimationEndTime() > 0)
                    result.add(new MagicSkillUse(otherPlayer, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0));
            }

            result.addAll(RelationChanged.update(this, otherPlayer, this));
            if (getTerritorySiege() > -1)
                result.add(new ExDominionWarStart(otherPlayer));
            if (otherPlayer.isInVehicle())
                if (otherPlayer.getVehicle().isAirShip())
                    result.add(new ExGetOnAirShip(otherPlayer, (L2AirShip) otherPlayer.getVehicle(), otherPlayer.getInVehiclePosition()));
                else
                    result.add(new GetOnVehicle(otherPlayer, (L2Ship) otherPlayer.getVehicle(), otherPlayer.getInVehiclePosition()));
            else
                showMoves(result, object);
            return result;
        }

        if (object.isAirShip()) {
            L2AirShip boat = (L2AirShip) object;
            result.add(new ExAirShipInfo(boat));
            if (isInVehicle() && getVehicle() == boat)
                result.add(new ExGetOnAirShip(this, boat, getInVehiclePosition()));
            if (boat.isMoving)
                //result.add(new ExMoveToLocationAirShip(boat));
                result.add(new ExMoveToLocationAirShip(boat, boat.getLoc(), boat.getDestination()));
        } else if (object.isShip()) {
            L2Ship boat = (L2Ship) object;
            result.add(new VehicleInfo(boat));
            if (isInVehicle() && getVehicle() == boat)
                result.add(new GetOnVehicle(this, boat, getInVehiclePosition()));
            if (boat.isMoving)
                result.add(new VehicleDeparture(boat));
        }

        return result;
    }

    public L2GameServerPacket removeVisibleObject(L2Object object, DeleteObject packet, boolean deactivateAI) {
        if (isLogoutStarted() || object == null || object.getObjectId() == getObjectId()) // FIXME  || isTeleporting()
            return null;
        if (isInVehicle() && getVehicle() == object)
            return null;

        if (deactivateAI && object.isNpc()) {
            L2NpcInstance npc = (L2NpcInstance) object;
            L2WorldRegion region = npc.getCurrentRegion();
            L2CharacterAI ai = npc.getAI();
            if (ai instanceof DefaultAI && ai.isActive() && !ai.isGlobalAI() && (region == null || region.areNeighborsEmpty())) {
                npc.setTarget(null);
                npc.stopMove();
                npc.getAI().stopAITask();
            }
        }

        L2GameServerPacket result = (packet == null ? new DeleteObject(object) : packet);

        getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, object);
        return result;
    }

    private void showMoves(GArray<L2GameServerPacket> result, L2Object object) {
        if (object != null && object.isCharacter()) {
            L2Character obj = (L2Character) object;
            if (obj.isMoving || obj.isFollow)
                result.add(new CharMoveToLocation(obj, obj.getZ(), true));
        }
    }

    public boolean increaseLevel() {
        setCurrentHpMp(getMaxHp(), getMaxMp());
        setCurrentCp(getMaxCp());

        // Recalculate the party level
        if (isInParty())
            getParty().recalculatePartyData();

        if (_clan != null) {
            PledgeShowMemberListUpdate memberUpdate = new PledgeShowMemberListUpdate(this);
            for (L2Player clanMember : _clan.getOnlineMembers(0))
                clanMember.sendPacket(memberUpdate);
        }

        // Give Expertise skill of this level
        rewardSkills();

        // изжоп, что бы при лвл апе обновились скилы трансформы
        if (isTransformed() && !isCursedWeaponEquipped()) {
            // Добавляем скилы трансформации
            for (L2Effect effect : getEffectList().getAllEffects())
                if (effect != null && effect.getEffectType() == EffectType.Transformation) {
                    if (effect.getSkill().isTransformation() && !((Transformation) effect.getSkill()).isDisguise) {
                        for (AddedSkill s : effect.getSkill().getAddedSkills())
                            if (s.level == 0) // трансформация позволяет пользоваться обычным скиллом
                            {
                                int s2 = getSkillLevel(s.id);
                                if (s2 > 0) {
                                    L2Skill skill = SkillTable.getInstance().getInfo(s.id, s2);
                                    _transformationSkills.put(s.id, skill);
                                    addSkill(skill, false);
                                }
                            } else if (s.level == -2) // XXX: дикий изжоп для скиллов зависящих от уровня игрока
                            {
                                int learnLevel = Math.max(effect.getSkill().getMagicLevel(), 40);
                                int maxLevel = SkillTable.getInstance().getBaseLevel(s.id);
                                int curSkillLevel = 1;
                                if (maxLevel > 3)
                                    curSkillLevel += getLevel() - learnLevel;
                                else
                                    curSkillLevel += (getLevel() - learnLevel) / ((76 - learnLevel) / maxLevel); // не спрашивайте меня что это такое
                                curSkillLevel = Math.min(Math.max(curSkillLevel, 1), maxLevel);
                                L2Skill skill = SkillTable.getInstance().getInfo(s.id, curSkillLevel);
                                _transformationSkills.put(s.id, skill);
                                addSkill(skill, false);
                            }
                    }
                    break;
                }
        }

        // Удаляем лишние скилы которые получили)
        //checkSkills(0);

        Quest q = QuestManager.getQuest(255);
        if (q != null)
            processQuestEvent(q.getName(), "CE40", null);
        getNevitBlessing().addPoints(ConfigValue.CurrentPointLvlUp);
        if (ConfigValue.EnableClassMasterWindowForLevelUp)
            РазноеГовно.incLevelClassMaster(this);
        if (ConfigValue.Icrease78LevelLoc.length > 0 && getLevel() >= 78 && !getVarB("tp_z", false)) {
            scriptRequest("Приглашаем вас посетить Low-Зону прокачки", "Util:tp_z", new Object[0]);
            setVar("tp_z", "true");
        }
        if (getAttainment() != null)
            getAttainment().incLevel();
        return true;
    }

    public boolean decreaseLevel() {
        if (_activeClass == null || !_activeClass.decLevel())
            return false;

        // Recalculate the party level
        if (isInParty())
            getParty().recalculatePartyData();

        if (_clan != null) {
            PledgeShowMemberListUpdate memberUpdate = new PledgeShowMemberListUpdate(this);
            for (L2Player clanMember : _clan.getOnlineMembers(getObjectId()))
                if (!clanMember.equals(this))
                    clanMember.sendPacket(memberUpdate);
        }

        if (ConfigValue.AltRemoveSkillsOnDelevel)
            checkSkills(10);
        // Give Expertise skill of this level
        rewardSkills();
        getNevitBlessing().addPoints(-ConfigValue.CurrentPointLvlUp);
        return true;
    }

    /**
     * Удаляет все скиллы, которые учатся на уровне большем, чем текущий+maxDiff
     */
    private static int levelWithoutEnchant(L2Skill skill) {
        return skill.getDisplayLevel() > 100 ? skill.getBaseLevel() : skill.getLevel();
    }

    // Вернуть назад поебень как была)))
    public void checkSkills(int maxDiff) {
        for (L2Skill sk : getAllSkillsArray()) {
            if (sk != null) {
                //boolean ap = false;
                //_log.info("checkSkills: maxDiff: " + maxDiff + "  getLevelLearn: " + sk.getLevelLearn() + "  getBaseLevel: " + sk.getBaseLevel() + "  getLevel: " + getLevel() + "  name: " + sk.getName() + "  id: " + sk.getId());
                if ((sk.getLevelLearn() > getLevel() || sk.getLevelLearn() == 0) && sk.getBaseLevel() > 1) {
                    //_log.info("checkSkills1: " + sk.getId());
                    L2Skill skill = SkillTable.getInstance().getInfo(sk.getId(), levelWithoutEnchant(sk)/*sk.getBaseLevel()*/);
                    if (skill.getLevelLearn() > getLevel()) {
                        //_log.info("checkSkills: remove1 skill name: " + skill.getName() + "  id: " + skill.getId());
                        removeSkill(sk, true, false);
                        short baselvl = 0;
                        while (true) {
                            baselvl = (short) (skill.getBaseLevel() - 1);
                            skill = SkillTable.getInstance().getInfo(skill.getId(), baselvl);
                            //if(skill != null)
                            //	_log.info("checkSkills: set skill name: " + skill.getName() + "  id: " + skill.getId() + " for New Level: " + skill.getLevel());
                            if (baselvl > 0) {
                                skill.setBaseLevel(baselvl);
                                if (skill.getLevelLearn() <= getLevel() + maxDiff) {
                                    //_log.info("checkSkills: addSkill: " + skill.getLevelLearn() + "  getLevel " + getLevel() + "  name: " + skill.getName() + "  id: " + skill.getId());
                                    addSkill(skill, true);
                                    break;
                                }
                            } else
                                break;
                        }
                    }
                    //ap = true;
                }
                //else if(!ap)
                //	{
                //	_log.info("checkSkills: sk=" + (sk != null) + " getLevel()=" + getLevel() + " SkillTable.getInstance().getInfo(sk.getId()="+sk.getId()+", sk.getBaseLevel()="+sk.getBaseLevel()+")=" + (SkillTable.getInstance().getInfo(sk.getId(), sk.getBaseLevel()) != null));
                else if (sk.getLevelLearn() > getLevel() + maxDiff || (sk.getLevelLearn() == 0 && SkillTable.getInstance().getInfo(sk.getId(), levelWithoutEnchant(sk)/*sk.getBaseLevel()*/).getLevelLearn() > getLevel() + maxDiff)) {
                    //_log.info("checkSkills: remove2 skill name: " + sk.getName() + "  id: " + sk.getId());
                    removeSkill(sk, true, false);
                }
                //	}
            }
        }
        updateEffectIcons();
    }

    public void stopAllTimers() {
        for (L2Cubic cubic : getCubics()) {
            cubic.deleteMe();
        }
        getCubics().clear();
        setAgathion(0);
        stopFeed();
        if (_mount != null)
            PlayerData.getInstance().storePetFood(((L2PetInstance) _mount), _mountNpcId);
        _mount = null;
        stopWaterTask();
        stopForceTask();
        stopSoulTask();
        stopBonusTask();
        for (int i = 0; i < _bonusExpiration2.length; i++)
            stopBonusTask(i);
        stopKickTask();
        stopPcBangPointsTask();
    }

    @Override
    public L2Summon getPet() {
        return _summon;
    }

    public void setPet(L2Summon summon) {
        _summon = summon;
        AutoShot();
        if (summon == null)
            getEffectList().stopEffect(4140);
        if (summon != null && !isInvisible() && !isInOfflineMode())
            for (L2Player player : L2World.getAroundPlayers(this))
                if (player != null && _objectId != player.getObjectId())
                    player.sendPackets(RelationChanged.update(null, this, player));
    }

    public void scheduleDelete() {
        long time = 0L;

        if (ConfigValue.EnableNoCarrier)
            time = NumberUtils.toInt(getVar("noCarrier"), ConfigValue.NoCarrierDefaultTime);

        if (isInEvent() == 5)
            time = ConfigValue.Tournament_NoCarrier;

        if (getEventMaster() != null)
            time = getEventMaster().getOfflineTime(time);

        scheduleDelete(time * 1000L);
    }

    /**
     * Удалит персонажа из мира через указанное время, если на момент истечения времени он не будет присоединен.
     * <p>
     * TODO: через минуту делать его неуязвимым.
     * TODO: сделать привязку времени к контексту, для зон с лимитом времени оставлять в игре на все время в зоне.
     */
    public void scheduleDelete(long time) {
        if (time <= 0) {
            deleteMe();
            return;
        }

        abortAttack(true, true);
        abortCast(true);
        stopMove();
        broadcastCharInfo();

        synchronized (_storeLock) {
            PlayerManager.saveCharToDisk(this); // получаем лишнее сохранение при логауте, но так надежнее
        }

        _noCarrierTask = ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl() {
            public void runImpl() {
                if (getNetConnection() == null || !getNetConnection().isConnected())
                    deleteMe();
            }
        }, time);
    }

    @Override
    public void deleteMe() {
        if (isLogoutStarted())
            return;

        if (getPet() != null)
            PlayerData.getInstance().storeSummon(getPet(), this);

        L2WorldRegion observerRegion = _observNeighbor;
        if (observerRegion != null)
            observerRegion.removeObject(this, false);

        if (recVoteTask != null) {
            recVoteTask.cancel(true);
            recVoteTask = null;
        }
        setLogoutStarted(true);

        prepareToLogout();

        synchronized (_storeLock) {
            PlayerManager.saveCharToDisk(this);
        }

        // Останавливаем и запоминаем все квестовые таймеры
        Quest.pauseQuestTimes(this);

        super.deleteMe();

        _isDeleting = true;

        getEffectList().stopAllEffects(true);

        setMassUpdating(true);

        //Send friendlists to friends that this player has logged off
        EnterWorld.notifyFriends(this, false);

        if (isInTransaction())
            getTransaction().cancel();

        // Set the online Flag to True or False and update the characters table of the database with online status and lastAccess (called when login and logout)
        try {
            setOnlineStatus(false);
        } catch (Throwable t) {
            _log.log(Level.WARNING, "deletedMe()", t);
        }

        // Stop the HP/MP/CP Regeneration task (scheduled tasks)
        try {
            stopAllTimers();
        } catch (Throwable t) {
            _log.log(Level.WARNING, "deletedMe()", t);
        }

        // Cancel Attak or Cast
        try {
            setTarget(null);
        } catch (Throwable t) {
            _log.log(Level.WARNING, "deletedMe()", t);
        }

        try {
            if (getClanId() > 0 && _clan != null && _clan.getClanMember(getObjectId()) != null) {
                int sponsor = _clan.getClanMember(getObjectId()).getSponsor();
                int apprentice = getApprentice();
                PledgeShowMemberListUpdate memberUpdate = new PledgeShowMemberListUpdate(this);
                for (L2Player clanMember : _clan.getOnlineMembers(getObjectId())) {
                    if (clanMember.getObjectId() == getObjectId())
                        continue;
                    clanMember.sendPacket(memberUpdate);
                    if (clanMember.getObjectId() == sponsor)
                        clanMember.sendPacket(new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_APPRENTICE_HAS_LOGGED_OUT).addString(_name));
                    else if (clanMember.getObjectId() == apprentice)
                        clanMember.sendPacket(new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_SPONSOR_HAS_LOGGED_OUT).addString(_name));
                }
                _clan.getClanMember(getObjectId()).setPlayerInstance(this, true);
            }
        } catch (final Throwable t) {
            _log.log(Level.SEVERE, "deletedMe()", t);
        }

        try {
            if (isCombatFlagEquipped()) {
                L2ItemInstance flag = getActiveWeaponInstance();
                if (flag != null) {
                    int customFlags = flag.getCustomFlags();
                    flag.setCustomFlags(0, false);
                    flag = getInventory().dropItem(flag, 1, true);
                    flag.setCustomFlags(customFlags, false);
                    flag.spawnMe2(flag.getLoc().correctGeoZ(), false);
                }
            }

            if (isTerritoryFlagEquipped()) {
                L2ItemInstance flag = getActiveWeaponInstance();
                if (flag != null && flag.getCustomType1() != 77) // 77 это эвентовый флаг
                {
                    L2TerritoryFlagInstance flagNpc = TerritorySiege.getNpcFlagByItemId(flag.getItemId());
                    flagNpc.drop(this);
                }
            }

			/* TODO
			for(L2ItemInstance item : getInventory().getItemsList())
				if((item.getCustomFlags() & L2ItemInstance.FLAG_ALWAYS_DROP_ON_DIE) == L2ItemInstance.FLAG_DROP_ON_DISCONNECT)
				{
					item = getInventory().dropItem(item, item.getCount());
					item.dropMe(this, getLoc().rnd(0, 100, false));
				}
			*/
        } catch (Throwable t) {
            _log.log(Level.WARNING, "deletedMe()", t);
        }

        if (CursedWeaponsManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()) != null)
            CursedWeaponsManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()).setPlayer(null);

        if (getPartyRoom() > 0) {
            PartyRoom room = PartyRoomManager.getInstance().getRooms().get(getPartyRoom());
            if (room != null)
                if (room.getLeader() == null || room.getLeader().equals(this))
                    PartyRoomManager.getInstance().removeRoom(room.getId());
                else
                    room.removeMember(this, false);
        }

        setPartyRoom(0);

        setEffectList(null);

        // Update database with items in its inventory and remove them from the world
        try {
            getInventory().deleteMe();
        } catch (Throwable t) {
            _log.log(Level.WARNING, "deletedMe()", t);
        }

        removeTrap();

        if (_decoy != null)
            _decoy.unSummon();

        stopPvPFlag();

        // TODO: посмотреть, что еще нам нужно пообнулять...
        bookmarks.clear();
        _chat.clear();
        _chat_tell.clear();
        _mail.clear();
        if (_buffSchem != null)
            _buffSchem.clear();
        if (_tpSchem != null)
            _tpSchem.clear();
        _warehouse = null;
        _freight = null;
        _ai = null;
        _summon = null;
        _arrowItem = null;
        _fistsWeaponItem = null;
        _chars = null;
        _enchantScroll = null;
        _agathion = null;
        _lastNpc = null;
        _obsLoc = null;
        _observNeighbor = null;
        _buffSchem = null;
        _tpSchem = null;
        if (_taskforfish != null) {
            _taskforfish.cancel(false);
            _taskforfish = null;
        }
        if (_kickTask != null) {
            _kickTask.cancel(true);
            _kickTask = null;
        }
        if (_bonusExpiration != null) {
            _bonusExpiration.cancel(true);
            _bonusExpiration = null;
        }
        for (Future<?> be : _bonusExpiration2)
            if (be != null) {
                be.cancel(true);
                be = null;
            }

        if (_pcCafePointsTask != null) {
            _pcCafePointsTask.cancel(false);
            _pcCafePointsTask = null;
        }
        if (_AttainmentTask != null) {
            _AttainmentTask.cancel(false);
            _AttainmentTask = null;
        }
        if (_unjailTask != null) {
            _unjailTask.cancel(false);
            _unjailTask = null;
        }
        if (_heroTask != null) {
            _heroTask.cancel(false);
            _heroTask = null;
        }
        if (_bot_check != null) {
            _bot_check.cancel(false);
            _bot_check = null;
        }
        if (_bot_kick != null) {
            _bot_kick.cancel(false);
            _bot_kick = null;
        }
        if (_enchantSucer != null) {
            _enchantSucer.cancel(false);
            _enchantSucer = null;
        }
        if (_test_task != null) {
            _test_task.cancel(false);
            _test_task = null;
        }
        //_log.info("L2Player: deleteMe["+this+"]");
		/*_inventory.clear();

		_inventory = null;
		bookmarks = null;
		_recipebook = null;
		_commonrecipebook = null;
		_quests = null;
		_itemBay = null;
		_shortCuts = null;
		_macroses = null;
		_statsChangeRecorder = null;
		_henna = null;
		_blockList = null;*/
        //----------------------------------
		/*_ai = null;
		_classlist = null;
		_skillLearningClassId = null;
		_connection = null;
		_accountName = null;
		_warehouse = null;
		_freight = null;
		_premiumItems = null;
		radar = null;
		_tradeList = null;
		_createList = null;
		_sellList = null;
		_buyList = null;
		_party = null;
		_clan = null;
		_playerAccess = null;
		_summon = null;
		_decoy = null;
		cubics = null;
		_agathion = null;
		_transaction = null;
		_arrowItem = null;
		_fistsWeaponItem = null;
		_chars = null;
		_enchantScroll = null;
		_usingWHType = null;
		_lastNpc = null;
		_lastBBS_script_operation = null;
		_multisell = null;
		_activeSoulShots = null;
		_obsLoc = null;
		_observNeighbor = null;
		_stablePoint = null;
		_loto = null;
		_race = null;
		_vehicle = null;
		_inVehiclePosition = null;
		_activeClass = null;
		_bonus = null;
		_olympiadGame = null;
		_keyBindings = null;
		_taskWater = null;
		_StatKills = null;
		_StatDrop = null;
		_StatCraft = null;
		_forumMemo = null;
		_fishLoc = null;
		_lure = null;
		_transformationName = null;
		_transformationSkills = null;
		bypasses = null;
		bypasses_bbs = null;*/
    }

    public void setTradeList(final L2TradeList x) {
        _tradeList = x;
    }

    public L2TradeList getTradeList() {
        return _tradeList;
    }

    public void setSellList(final ConcurrentLinkedQueue<TradeItem> x) {
        _sellList = x;
        saveTradeList();
    }

    public void setSellPkgList(final ConcurrentLinkedQueue<TradeItem> x) {
        _sellPkgList = x;
        saveTradeList();
    }

    public ConcurrentLinkedQueue<TradeItem> getSellList() {
        return _sellList != null ? _sellList : new ConcurrentLinkedQueue<TradeItem>();
    }

    public ConcurrentLinkedQueue<TradeItem> getSellPkgList() {
        return _sellPkgList != null ? _sellPkgList : new ConcurrentLinkedQueue<TradeItem>();
    }

    public L2ManufactureList getCreateList() {
        return _createList;
    }

    public void setCreateList(final L2ManufactureList x) {
        _createList = x;
        saveTradeList();
    }

    public void setBuyList(final ConcurrentLinkedQueue<TradeItem> x) {
        _buyList = x;
        saveTradeList();
    }

    public ConcurrentLinkedQueue<TradeItem> getBuyList() {
        return _buyList != null ? _buyList : new ConcurrentLinkedQueue<TradeItem>();
    }

    public void setPrivateStoreType(final short type) {
        _privatestore = type;
        if (type != STORE_PRIVATE_NONE && type != STORE_OBSERVING_GAMES) {
            setVar("storemode", String.valueOf(type));
        } else {
            unsetVar("storemode");
            unsetVar("buf_title");
            unsetVar("buf_price");
            _buf_title = null;
        }
        getListeners().onSetPrivateStoreType(type);
    }

    public short getPrivateStoreType() {
        if (inObserverMode())
            return STORE_OBSERVING_GAMES;

        return _privatestore;
    }

    public void setSkillLearningClassId(final ClassId classId) {
        _skillLearningClassId = classId;
    }

    public ClassId getSkillLearningClassId() {
        return _skillLearningClassId;
    }

    /**
     * Set the _clan object, _clanId, _clanLeader Flag and title of the L2Player.<BR><BR>
     *
     * @param clan the clat to set
     */
    public void setClan(L2Clan clan) {
        setClan(clan, false);
    }

    public void setClan(L2Clan clan, boolean charEntrWorld) {
        if (_clan != clan && _clan != null || clan == null)
            unsetVar("canWhWithdraw");

        if (getAttainment() != null)
            getAttainment().setClan();
        L2Clan oldClan = _clan;
        if (oldClan != null && clan == null) {
            for (L2Skill skill : oldClan.getAllSkills())
                removeSkill(skill, false, false);
            for (int pledgeId : oldClan.getSquadSkills().keySet())
                if (pledgeId == _pledgeType) {
                    FastMap<Integer, L2Skill> skills = oldClan.getSquadSkills().get(pledgeId);
                    for (L2Skill s : skills.values())
                        removeSkill(s, false, false);
                }
            updateEffectIcons();
        }
        _clan = clan;
        if (clan == null) {
            if (isTerritoryFlagEquipped()) {
                L2ItemInstance flag = getActiveWeaponInstance();
                if (flag != null && flag.getCustomType1() != 77) // 77 это эвентовый флаг
                {
                    L2TerritoryFlagInstance flagNpc = TerritorySiege.getNpcFlagByItemId(flag.getItemId());
                    flagNpc.returnToCastle(this);
                }
            }
            _pledgeType = 0;
            _pledgeClass = 0;
            _powerGrade = 0;
            _apprentice = 0;
            if (_territorySide > -1) {
                _territorySide = -1;
                TerritorySiege.removePlayer(this);
            }
            if (!charEntrWorld)
                getInventory().checkAllConditions();
            return;
        }
        if (!clan.isMember(getObjectId())) {
            // char has been kicked from clan
            _log.fine("Char " + _name + " is kicked from clan: " + clan.getName());
            setClan(null);
            unsetVar("canWhWithdraw");
            setTitle("");
            return;
        }
        setTerritorySiege(clan.getTerritorySiege());

        if (!charEntrWorld) {
            TerritorySiege.clearReward(getObjectId());
            TerritorySiege.removePlayer(this);
        }
        setTitle("");
    }

    public L2Clan getClan() {
        return _clan;
    }

    public ClanHall getClanHall() {
        return ClanHallManager.getInstance().getClanHallByOwner(_clan);
    }

    public Castle getCastle() {
        return CastleManager.getInstance().getCastleByOwner(_clan);
    }

    public Fortress getFortress() {
        return FortressManager.getInstance().getFortressByOwner(_clan);
    }

    public L2Alliance getAlliance() {
        return _clan == null ? null : _clan.getAlliance();
    }

    public boolean isClanLeader() {
        return _clan != null && _objectId == _clan.getLeaderId();
    }

    public boolean isAllyLeader() {
        return getAlliance() != null && getAlliance().getLeader().getLeaderId() == getObjectId();
    }

    @Override
    public void reduceArrowCount() {
        sendPacket(Msg.YOU_CAREFULLY_NOCK_AN_ARROW);
        if (!ConfigValue.InfinityArrow) {
            try {
                L2ItemInstance arrows = getInventory().destroyItem(getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1, false);
                if (arrows == null || arrows.getCount() == 0) {
                    getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_LHAND);
                    _arrowItem = null;
                }
            } catch (Exception e) {
                getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_LHAND);
                _arrowItem = null;
            }
        }
    }

    /**
     * Equip arrows needed in left hand and send a Server->Client packet ItemList to the L2Player then return True.
     */
    protected boolean checkAndEquipArrows() {
        // Check if nothing is equipped in left hand
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND) == null) {
            // Get the L2ItemInstance of the arrows needed for this bow
            if (getActiveWeaponItem().getItemType() == WeaponType.BOW)
                _arrowItem = getInventory().findArrowForBow(getActiveWeaponItem());
            else if (getActiveWeaponItem().getItemType() == WeaponType.CROSSBOW)
                _arrowItem = getInventory().findArrowForCrossbow(getActiveWeaponItem());

            // Equip arrows needed in left hand
            if (_arrowItem != null)
                getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, _arrowItem);
        } else
            // Get the L2ItemInstance of arrows equipped in left hand
            _arrowItem = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);

        return _arrowItem != null;
    }

    public void setUptime(final long time) {
        _uptime = time;
    }

    public long getUptime() {
        return System.currentTimeMillis() - _uptime;
    }

    public boolean isInParty() {
        return _party != null;
    }

    public void setParty(final L2Party party) {
        _party = party;
    }

    public void joinParty(final L2Party party) {
        if (party != null) {
            _party = party;
            party.addPartyMember(this);
        }
    }

    public void leaveParty() {
        if (isInParty()) {
            _party.oustPartyMember(this);
            _party = null;
        }
    }

    public L2Party getParty() {
        return _party;
    }

    public boolean isGM() {
        return _playerAccess == null ? false : _playerAccess.IsGM && !isBot();
    }

    /**
     * Нигде не используется, но может пригодиться для БД
     */
    public void setAccessLevel(final int level) {
        _accessLevel = level;
    }

    /**
     * Нигде не используется, но может пригодиться для БД
     */
    @Override
    public int getAccessLevel() {
        return _accessLevel;
    }

    public void setPlayerAccess(final PlayerAccess pa) {
        if (pa != null)
            _playerAccess = pa;
        else
            _playerAccess = new PlayerAccess();

        setAccessLevel(isGM() || _playerAccess.Menu ? 100 : 0);
    }

    public PlayerAccess getPlayerAccess() {
        return _playerAccess;
    }

    public void setAccountAccesslevel(final int level, final String comments, int banTime) {
        LSConnection.getInstance(_connection.getLSId()).sendPacket(new ChangeAccessLevel(getAccountName(), level, comments, banTime));
    }

    @Override
    public double getLevelMod() {
        return (89. + getLevel()) / 100.0;
    }

    /**
     * Update Stats of the L2Player client side by sending Server->Client packet UserInfo/StatusUpdate to this L2Player and CharInfo/StatusUpdate to all L2Player in its _KnownPlayers (broadcast).<BR><BR>
     */
    @Override
    public void updateStats() {
        refreshOverloaded();
        checkGradeExpertiseUpdate();
        checkArmorPenalty();
        checkWeaponPenalty();
        sendChanges();
    }

    /**
     * Send a Server->Client StatusUpdate packet with Karma to the L2Player and all L2Player to inform (broadcast).
     */
    public void updateKarma(boolean flagChanged) {
        sendStatusUpdate(true, StatusUpdate.KARMA);
        if (flagChanged)
            broadcastRelationChanged();
    }

    public void setOnlineStatus(final boolean isOnline) {
        _isOnline = isOnline;
        PlayerData.getInstance().updateOnlineStatus(this);
    }

    /**
     * Decrease Karma of the L2Player and Send it StatusUpdate packet with Karma and PvP Flag (broadcast).
     */
    public void increaseKarma(long add_karma) {
        if (ConfigValue.CrazyZoneTpPk) {
            L2Zone zone = ZoneManager.getInstance().getZoneById(other, 500520);
            if (zone != null && zone.checkIfInZone(this))
                ThreadPoolManager.getInstance().schedule(new L2ObjectTasks.TeleportTask(this, new Location(ConfigValue.CrazyZoneTpPkLoc[0], ConfigValue.CrazyZoneTpPkLoc[1], ConfigValue.CrazyZoneTpPkLoc[2]), 0), 3333);
        }

        boolean flagChanged = _karma == 0;
        if (add_karma > ConfigValue.MaxKarmaIncrease)
            add_karma = ConfigValue.MaxKarmaIncrease;
        long new_karma = _karma + add_karma;

        if (new_karma > Integer.MAX_VALUE)
            new_karma = Integer.MAX_VALUE;

        if (_karma == 0 && new_karma > 0) {
            if (_pvpFlag > 0 && !_block_pvp_flag) {
                _pvpFlag = 0;
                if (_PvPRegTask != null) {
                    _PvPRegTask.cancel(true);
                    _PvPRegTask = null;
                }
                sendStatusUpdate(true, StatusUpdate.PVP_FLAG);
            }
            _karma = (int) new_karma;
            for (final L2Character cha : L2World.getAroundCharacters(this))
                if (cha instanceof L2GuardInstance && cha.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
                    cha.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
        } else
            _karma = (int) new_karma;

        updateKarma(flagChanged);
    }

    /**
     * Decrease Karma of the L2Player and Send it StatusUpdate packet with Karma and PvP Flag (broadcast).
     */
    public void decreaseKarma(final int i) {
        boolean flagChanged = _karma > 0;
        _karma -= i;
        if (_karma <= 0) {
            _karma = 0;
            updateKarma(flagChanged);
        } else
            updateKarma(false);
    }

    /**
     * Create a new L2Player and add it in the characters table of the database.<BR><BR>
     *
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Create a new L2Player with an account name </li>
     * <li>Set the name, the Hair Style, the Hair Color and	the Face type of the L2Player</li>
     * <li>Add the player in the characters table of the database</li><BR><BR>
     *
     * @param accountName The name of the L2Player
     * @param name        The name of the L2Player
     * @param hairStyle   The hair style Identifier of the L2Player
     * @param hairColor   The hair color Identifier of the L2Player
     * @param face        The face type Identifier of the L2Player
     * @return The L2Player added to the database or null
     */
    public static L2Player create(int classId, byte sex, String accountName, final String name, final byte hairStyle, final byte hairColor, final byte face, final int bot, final int level) {
        L2PlayerTemplate template = CharTemplateTable.getInstance().getTemplate(classId, sex != 0);

        // Create a new L2Player with an account name
        L2Player player = bot > 0 ? new L2BotPlayer(IdFactory.getInstance().getNextId(), template, accountName, bot) : new L2Player(IdFactory.getInstance().getNextId(), template, accountName, bot);

        player.setName(name);
        player.setTitle("");
        player.setHairStyle(hairStyle);
        player.setHairColor(hairColor);
        player.setFace(face);
        player.setCreateTime(System.currentTimeMillis());
        player.getRecommendation().setRecomLeft(20);

        // Add the player in the characters table of the database
        if (!PlayerManager.createDb(player, bot, level))
            return null;

        return player;
    }

    public Map<Integer, PremiumItem> getPremiumItemList() {
        return _premiumItems;
    }

    public Future<?> _unjailTask;

    public void incrementKillsCounter(final Integer Id) {
        final Long tmp = _StatKills.containsKey(Id) ? _StatKills.get(Id) + 1 : 1;
        _StatKills.put(Id, tmp);
        sendMessage(new CustomMessage("l2open.gameserver.model.L2Player.KillsCounter", this).addString(tmp.toString()));
    }

    public long getKillCount() {
        long result = 0;
        if (_StatKills != null)
            for (long kill : _StatKills.values())
                result += kill;
        return result;
    }

    public void incrementDropCounter(final Integer Id, final Long qty) {
        _StatDrop.put(Id, _StatDrop.containsKey(Id) ? _StatDrop.get(Id) + qty : qty);
    }

    public void incrementCraftCounter(final Integer Id, final int qty) {
        final Long tmp = _StatCraft.containsKey(Id) ? _StatCraft.get(Id) + qty : qty;
        _StatCraft.put(Id, tmp);
        sendMessage(new CustomMessage("l2open.gameserver.model.L2Player.CraftCounter", this).addString(tmp.toString()));
    }

    private final Object _storeLock = new Object();

    public boolean isOnline() {
        return _isOnline;
    }

    // нужно ток для логирования...
    public L2Skill addSkill(L2Skill newSkill) {
        if (ConfigValue.DebugSkillAdd1)
            Log.logTrace("ADD|" + newSkill, "add_skill_no_store1", getName());
        return super.addSkill(newSkill);
    }

    /**
     * Add a skill to the L2Player _skills and its Func objects to the calculator set of the L2Player and save update in the character_skills table of the database.
     *
     * @return The L2Skill replaced or null if just added a new L2Skill
     */
    public L2Skill addSkill(final L2Skill newSkill, final boolean store) {
        if (newSkill == null)
            return null;

        // Add a skill to the L2Player _skills and its Func objects to the calculator set of the L2Player
        L2Skill oldSkill = super.addSkill(newSkill);

        if (newSkill.equals(oldSkill))
            return oldSkill;

        // Add or update a L2Player skill in the character_skills table of the database
        if (store) {
            if (ConfigValue.DebugSkillAdd2) {
                Log.logTrace("ADD_STORE|" + newSkill + "|" + oldSkill, "add_skill", getName());
            }
            PlayerData.getInstance().storeSkill(this, newSkill, oldSkill);
        } else if (ConfigValue.DebugSkillAdd1) {
            Log.logTrace("ADD|" + newSkill, "add_skill_no_store2", getName());
        }

        return oldSkill;
    }

    public L2Skill removeSkill(L2Skill skill, boolean fromDB, boolean update_icon) {
        if (skill == null)
            return null;
        return PlayerData.getInstance().removeSkill(this, skill.getId(), fromDB, update_icon);
    }

    public void disableItem(int handler, int handler_lvl, int itemId, int grp_id, long timeTotal, long timeLeft) {
        if (timeLeft > 0)
            if (!isSkillDisabled(ConfigValue.SkillReuseType == 0 ? handler * 65536L + handler_lvl : handler)) {
                disableSkill(handler, handler_lvl, timeLeft);
                if (itemId == grp_id)
                    sendPacket(new ExUseSharedGroupItem(itemId, grp_id, (int) timeLeft, (int) timeTotal));
                else
                    for (Integer item_id : ItemTemplates.getInstance().getItemFoGrup(grp_id))
                        sendPacket(new ExUseSharedGroupItem(item_id, grp_id, (int) timeLeft, (int) timeTotal));
            }
    }

    public int getHennaEmptySlots() {
        int totalSlots = 1 + getClassId().level();
        for (int i = 0; i < 3; i++)
            if (_henna[i] != null)
                totalSlots--;

        if (totalSlots <= 0)
            return 0;

        return totalSlots;

    }

    /**
     * Calculate Henna modifiers of this L2Player.
     */
    public void recalcHennaStats() {
        _hennaINT = 0;
        _hennaSTR = 0;
        _hennaCON = 0;
        _hennaMEN = 0;
        _hennaWIT = 0;
        _hennaDEX = 0;

        for (int i = 0; i < 3; i++) {
            if (_henna[i] == null)
                continue;
            _hennaINT += _henna[i].getStatINT();
            _hennaSTR += _henna[i].getStatSTR();
            _hennaMEN += _henna[i].getStatMEM();
            _hennaCON += _henna[i].getStatCON();
            _hennaWIT += _henna[i].getStatWIT();
            _hennaDEX += _henna[i].getStatDEX();
        }

        if (_hennaINT > 5)
            _hennaINT = 5;
        if (_hennaSTR > 5)
            _hennaSTR = 5;
        if (_hennaMEN > 5)
            _hennaMEN = 5;
        if (_hennaCON > 5)
            _hennaCON = 5;
        if (_hennaWIT > 5)
            _hennaWIT = 5;
        if (_hennaDEX > 5)
            _hennaDEX = 5;
    }

    /**
     * @param slot id слота у перса
     * @return the Henna of this L2Player corresponding to the selected slot.<BR><BR>
     */
    public L2HennaInstance getHenna(final int slot) {
        if (slot < 1 || slot > 3)
            return null;
        return _henna[slot - 1];
    }

    public int getHennaStatINT() {
        return _hennaINT;
    }

    public int getHennaStatSTR() {
        return _hennaSTR;
    }

    public int getHennaStatCON() {
        return _hennaCON;
    }

    public int getHennaStatMEN() {
        return _hennaMEN;
    }

    public int getHennaStatWIT() {
        return _hennaWIT;
    }

    public int getHennaStatDEX() {
        return _hennaDEX;
    }

    @Override
    public boolean consumeItem(final int itemConsumeId, final int itemCount) {
        L2ItemInstance item = getInventory().getItemByItemId(itemConsumeId);
        if (item == null || item.getCount() < itemCount)
            return false;
        if (getInventory().destroyItem(item, itemCount, false) != null) {
            sendPacket(SystemMessage.removeItems(itemConsumeId, itemCount));
            return true;
        }
        return false;
    }

    @Override
    public boolean consumeItemMp(int itemId, int mp) {
        for (L2ItemInstance item : getInventory().getPaperdollItems())
            if (item != null && item.getItemId() == itemId) {
                final int newMp = item.getLifeTimeRemaining() - mp;
                if (newMp >= 0) {
                    item.setLifeTimeRemaining(this, newMp);
                    sendPacket(new InventoryUpdate().addModifiedItem(item));
                    return true;
                }
                break;
            }
        return false;
    }

    /**
     * @return True if the L2Player is a Mage.<BR><BR>
     */
    @Override
    public boolean isMageClass() {
        return _template.basePAtk == 3;
    }

    public boolean isMounted() {
        return _mountNpcId > 0;
    }

    /**
     * Проверяет, можно ли приземлиться в этой зоне.
     *
     * @return можно ли приземлится
     */
    public boolean checkLandingState() {
        if (isInZone(no_landing))
            return false;

        Siege siege = SiegeManager.getSiege(this, false);
        if (siege != null) {
            Residence unit = siege.getSiegeUnit();
            if (unit != null && getClan() != null && isClanLeader() && (getClan().getHasCastle() == unit.getId() || getClan().getHasFortress() == unit.getId()))
                return true;
            return false;
        }

        return true;
    }

    public void setMount(int npcId, int obj_id, int level) {
        if (isCursedWeaponEquipped() || isCombatFlagEquipped() || isTerritoryFlagEquipped()) {
            sendActionFailed();
            return;
        }

        switch (npcId) {
            case 0: // Dismount
                setFlying(false);
                setRiding(false);
                sendPacket(new SetupGauge(_objectId, 3, 0, 0));
                stopFeed();
                if (_mount != null)
                    PlayerData.getInstance().storePetFood(((L2PetInstance) _mount), _mountNpcId);
                _mount = null;
                if (getTransformation() > 0)
                    setTransformation(0);
                //removeSkillById(L2Skill.SKILL_STRIDER_ASSAULT);
                removeSkillById(L2Skill.SKILL_WYVERN_BREATH);
                getEffectList().stopEffect(L2Skill.SKILL_HINDER_STRIDER);
                break;
            case PetDataTable.STRIDER_WIND_ID:
            case PetDataTable.STRIDER_STAR_ID:
            case PetDataTable.STRIDER_TWILIGHT_ID:
            case PetDataTable.RED_STRIDER_WIND_ID:
            case PetDataTable.RED_STRIDER_STAR_ID:
            case PetDataTable.RED_STRIDER_TWILIGHT_ID:
            case PetDataTable.GUARDIANS_STRIDER_ID:
                setRiding(true);
                //if(isNoble())
                //	addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_STRIDER_ASSAULT, 1), false);
                break;
            case PetDataTable.WYVERN_ID:
                setFlying(true);
                setLoc(getLoc().changeZ(32));
                addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_WYVERN_BREATH, 1), false);
                break;
            case PetDataTable.WGREAT_WOLF_ID:
            case PetDataTable.FENRIR_WOLF_ID:
            case PetDataTable.WFENRIR_WOLF_ID:
                setRiding(true);
                break;
            case PetDataTable.AURA_BIRD_FALCON_ID:
                setLoc(getLoc().changeZ(32));
                setFlying(true);
                setTransformation(8);
                break;
            case PetDataTable.AURA_BIRD_OWL_ID:
                setLoc(getLoc().changeZ(32));
                setFlying(true);
                setTransformation(9);
                break;
            default:
                if (npcId >= 51001 && npcId <= 51019)
                    setRiding(true);
                break;
        }

        if (npcId > 0)
            unEquipWeapon();

        _mountNpcId = npcId;
        _mountObjId = obj_id;
        _mountLevel = level;

        _mount = getPet();
        if (isMounted())
            startFeed(_mount);

        broadcastUserInfo(true); // нужно послать пакет перед Ride для корректного снятия оружия с заточкой
        broadcastPacket(new Ride(this));
        broadcastUserInfo(true); // нужно послать пакет после Ride для корректного отображения скорости

        sendPacket(new SkillList(this));
    }

    protected void startFeed(L2Character summon) {
        if (!isMounted() || summon == null)
            return;
        if (summon.isPet()) {
            ((L2PetInstance) summon).setCurrentFed(((L2PetInstance) summon).getCurrentFed());
            ((L2PetInstance) summon).setControlItemObjId(((L2PetInstance) summon).getControlItemObjId());
            sendPacket(new SetupGauge(getObjectId(), 3, (((L2PetInstance) summon).getCurrentFed() * 10000) / ((L2PetInstance) summon).getFeedConsume(), (((L2PetInstance) summon).getMaxFed() * 10000) / (((L2PetInstance) summon).getFeedConsume())));
            if (!isDead())
                _mountFeedTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PetFeedTask(this), 10000, 10000);
        }
    }

    public void stopFeed() {
        if (_mountFeedTask != null) {
            _mountFeedTask.cancel(false);
            _mountFeedTask = null;
        }
    }

    public class PetFeedTask extends l2open.common.RunnableImpl {
        private final L2Player _player;

        public PetFeedTask(L2Player player) {
            _player = player;
        }

        @Override
        public void runImpl() {
            if (_player != null && _mount != null) {
                try {
                    if (!_player.isMounted() || (_player.getMountNpcId() == 0)) {
                        _player.stopFeed();
                        return;
                    }

                    if (((L2PetInstance) _mount).getCurrentFed() > ((L2PetInstance) _mount).getFeedConsume())
                        ((L2PetInstance) _mount).setCurrentFed(((L2PetInstance) _mount).getCurrentFed() - ((L2PetInstance) _mount).getFeedConsume());
                    else {
                        ((L2PetInstance) _mount).setCurrentFed(0);
                        _player.stopFeed();
                        _player.setMount(0, 0, 0);
                        _player.sendPacket(new SystemMessage(SystemMessage.YOU_ARE_OUT_OF_FEED_MOUNT_STATUS_CANCELED));
                    }

                    while (((L2PetInstance) _mount).isHungry() && ((L2PetInstance) _mount).tryFeed()) {
                    }
                } catch (Throwable e) {
                    //_log.log(Level.SEVERE, "", e);
                }
            }
        }
    }

    public void unEquipWeapon() {
        L2ItemInstance wpn = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
        if (wpn != null)
            sendDisarmMessage(wpn);
        getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_LHAND);

        wpn = getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
        if (wpn != null)
            sendDisarmMessage(wpn);
        getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_RHAND);

        checkGradeExpertiseUpdate();
        abortAttack(true, true);
        abortCast(true);
    }

	/*
	@Override
	public float getMovementSpeedMultiplier()
	{
		int template_speed = _template.baseRunSpd;
		if(isMounted())
		{
			L2PetData petData = PetDataTable.getInstance().getInfo(_mountNpcId, _mountLevel);
			if(petData != null)
				template_speed = petData.getSpeed();
		}
		return getRunSpeed() * 1f / template_speed;
	}
	*/

    @Override
    public int getSpeed(int baseSpeed) {
        if (isMounted()) {
            L2PetData petData = PetDataTable.getInstance().getInfo(_mountNpcId, _mountLevel);
            int speed = 187;
            if (petData != null)
                speed = petData.getSpeed();
            double mod = 1.;
            int level = getLevel();
            if (_mountLevel > level && level - _mountLevel > 10)
                mod = 0.5; // Штраф на разницу уровней между игроком и петом
            if (_mount != null && ((L2PetInstance) _mount).isHungry())
                mod = 0.5; // Штраф если маунт голодный.
            baseSpeed = (int) (mod * speed);
        }
        return super.getSpeed(baseSpeed);
    }

    @Override
    public int getMAtk(final L2Character target, final L2Skill skill) {
        if (isMounted()) {
            L2PetData petData = PetDataTable.getInstance().getInfo(_mountNpcId, _mountLevel);
            if (petData != null)
                return (int) calcStat(Stats.p_magical_attack, petData.getMAtk(), target, skill);
        }
        return (int) (super.getMAtk(target, skill) * getTemplate().m_def_mod);
    }

    @Override
    public double getPAtkSpd() {
        if (isMounted()) {
            L2PetData petData = PetDataTable.getInstance().getInfo(_mountNpcId, _mountLevel);
            if (petData != null)
                return (int) calcStat(Stats.p_attack_speed, petData.getAtkSpeed(), null, null);
        }
        return super.getPAtkSpd();
    }

    private int _mountNpcId;
    private int _mountObjId;
    private int _mountLevel;

    public int getMountNpcId() {
        return _mountNpcId;
    }

    public int getMountObjId() {
        return _mountObjId;
    }

    public int getMountLevel() {
        return _mountLevel;
    }

    public void sendDisarmMessage(L2ItemInstance wpn) {
        if (wpn.getEnchantLevel() > 0) {
            SystemMessage sm = new SystemMessage(SystemMessage.EQUIPMENT_OF__S1_S2_HAS_BEEN_REMOVED);
            sm.addNumber(wpn.getEnchantLevel());
            sm.addItemName(wpn.getItemId());
            sendPacket(sm);
        } else {
            SystemMessage sm = new SystemMessage(SystemMessage.S1__HAS_BEEN_DISARMED);
            sm.addItemName(wpn.getItemId());
            sendPacket(sm);
        }
    }

    /**
     * Send a Server->Client packet UserInfo to this L2Player and CharInfo to all L2Player in its _KnownPlayers.
     */
    @Override
    public void updateAbnormalEffect() {
        sendChanges();
    }

    /**
     * Disable the Inventory and create a new task to enable it after 1.5s.
     */
    public void tempInventoryDisable() {
        _inventoryDisable = true;
        ThreadPoolManager.getInstance().schedule(new InventoryEnableTask(this), 1500, true);
    }

    /**
     * @return True if the Inventory is disabled.<BR><BR>
     */
    public boolean isInventoryDisabled() {
        return _inventoryDisable;
    }

    /**
     * Устанавливает тип используемого склада.
     *
     * @param type тип склада:<BR>
     *             <ul>
     *             <li>WarehouseType.PRIVATE
     *             <li>WarehouseType.CLAN
     *             <li>WarehouseType.CASTLE
     *             <li>WarehouseType.FREIGHT
     *             </ul>
     */
    public void setUsingWarehouseType(final WarehouseType type) {
        _usingWHType = type;
    }

    /**
     * Возвращает тип используемого склада.
     *
     * @return null или тип склада:<br>
     * <ul>
     * <li>WarehouseType.PRIVATE
     * <li>WarehouseType.CLAN
     * <li>WarehouseType.CASTLE
     * <li>WarehouseType.FREIGHT
     * </ul>
     */
    public WarehouseType getUsingWarehouseType() {
        return _usingWHType;
    }

    public GArray<L2Cubic> getCubics() {
        return cubics == null ? new GArray<L2Cubic>() : cubics;
    }

    public void addCubic(L2Cubic cubic) {
        if (cubics == null) {
            cubics = new GArray<L2Cubic>();
        }
        deleteCubic(cubic.getSlot());
        cubics.add(cubic);
        broadcastUserInfo(true);
    }

    public L2Cubic getCubic(int slot) {
        for (L2Cubic cubic : cubics) {
            if (cubic.getSlot() == slot) {
                return cubic;
            }
        }
        return null;
    }

    public void deleteCubic(int slot) {
        for (L2Cubic cubic : cubics) {
            if (cubic.getSlot() == slot) {
                cubic.deleteMe();
                cubics.remove(cubic);
                broadcastUserInfo(true);
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "player '" + getName() + "'";
    }

    /**
     * @return the modifier corresponding to the Enchant Effect of the Active Weapon (Min : 127).<BR><BR>
     */
    public int getEnchantEffect() {
        final L2ItemInstance wpn = getActiveWeaponInstance();

        if (wpn == null || wpn.getVisualItemId() > 0)
            return 0;

        return Math.min(127, wpn.getVisualEnchantLevel() == -1 ? wpn.getEnchantLevel() : wpn.getVisualEnchantLevel());
    }

    /**
     * Set the _lastFolkNpc of the L2Player corresponding to the last Folk witch one the player talked.<BR><BR>
     */
    public void setLastNpc(final L2NpcInstance npc) {
        _lastNpc = npc;
    }

    /**
     * @return the _lastFolkNpc of the L2Player corresponding to the last Folk witch one the player talked.<BR><BR>
     */
    public L2NpcInstance getLastNpc() {
        return _lastNpc;
    }

    public void setLastBbsOperaion(final String operaion) {
        _lastBBS_script_operation = operaion;
    }

    public String getLastBbsOperaion() {
        return _lastBBS_script_operation;
    }

    public void setMultisell(MultiSellListContainer multisell) {
        _multisell = multisell;
    }

    public MultiSellListContainer getMultisell() {
        return _multisell;
    }

    /**
     * @return True if L2Player is a participant in the Festival of Darkness.<BR><BR>
     */
    public boolean isFestivalParticipant() {
        return getReflection() instanceof DarknessFestival;
    }

    @Override
    public boolean unChargeShots(boolean spirit) {
        L2ItemInstance weapon = getActiveWeaponInstance();
        if (weapon == null)
            return false;

        if (spirit)
            weapon.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
        else
            weapon.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);

        AutoShot();
        return true;
    }

    public boolean unChargeFishShot() {
        L2ItemInstance weapon = getActiveWeaponInstance();
        if (weapon == null)
            return false;
        weapon.setChargedFishshot(false);
        AutoShot();
        return true;
    }

    public void AutoShot() {
        synchronized (_activeSoulShots) {
            for (Integer e : _activeSoulShots) {
                if (e == null)
                    continue;
                L2ItemInstance item = getInventory().getItemByItemId(e);
                if (item == null) {
                    _activeSoulShots.remove(e);
                    continue;
                }
                IItemHandler handler = ItemHandler.getInstance().getItemHandler(e);
                if (handler == null)
                    continue;
                handler.useItem(this, item, false);
                //fireMethodInvoked(MethodCollection.onStartCast, new Object[] { null, this, false });
            }
        }
    }

    public boolean getChargedFishShot() {
        L2ItemInstance weapon = getActiveWeaponInstance();
        return weapon != null && weapon.getChargedFishshot();
    }

    @Override
    public boolean getChargedSoulShot() {
        L2ItemInstance weapon = getActiveWeaponInstance();
        return weapon != null && (weapon.getChargedSoulshot() == L2ItemInstance.CHARGED_SOULSHOT || isBot() && ConfigValue.BotIsChargedSpiritShot > 0);
    }

    @Override
    public int getChargedSpiritShot() {
        L2ItemInstance weapon = getActiveWeaponInstance();
        if (weapon == null)
            return 0;
        if (isBot())
            return ConfigValue.BotIsChargedSpiritShot;
        return weapon.getChargedSpiritshot();
    }

    public void addAutoSoulShot(Integer itemId) {
        _activeSoulShots.add(itemId);
        getListeners().onAutoSoulShot(itemId, true);
    }

    public void removeAutoSoulShot(Integer itemId) {
        _activeSoulShots.remove(itemId);
        getListeners().onAutoSoulShot(itemId, false);
    }

    public ConcurrentSkipListSet<Integer> getAutoSoulShot() {
        return _activeSoulShots;
    }

    public void setInvisible(boolean vis) {
        _invisible = vis;
    }

    @Override
    public boolean isInvisible() {
        return _invisible;
    }

    public int getClanPrivileges() {
        if (_clan == null)
            return 0;
        if (isClanLeader())
            return L2Clan.CP_ALL;
        if (_powerGrade < 1 || _powerGrade > 9)
            return 0;
        RankPrivs privs = _clan.getRankPrivs(_powerGrade);
        if (privs != null)
            return privs.getPrivs();
        return 0;
    }

    public boolean enterObserverMode(Location loc, OlympiadGame game) {
        return enterObserverMode(loc, game, -1, false);
    }

    public boolean enterObserverMode(Location loc, OlympiadGame game, int reflection, boolean oly_mod) {
        _observNeighbor = L2World.getRegion(loc);
        if (_observNeighbor == null)
            return false;

        setTarget(null);
        stopMove();
        setIsInvul(true);
        setInvisible(true);
        block();
        if (getCurrentRegion() != null)
            for (L2WorldRegion neighbor : getCurrentRegion().getNeighbors()) {
                neighbor.removePlayerFromOtherPlayers(this);
                neighbor.removeObjectsFromPlayer(this);
            }
        sendUserInfo(true);

        _observerMode = 1;

        if (reflection > 0)
            setReflection(reflection);

        if (game != null) {
            _olympiadObserveId = 1;

            // Меняем интерфейс
            sendPacket(new ExOlympiadMode(3));

            // Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
            setLastClientPosition(null);
            setLastServerPosition(null);

            setReflection(game.getReflect().getId());
        } else if (oly_mod) {
            _olympiadObserveId = 1;

            // Меняем интерфейс
            sendPacket(new ExOlympiadMode(3));

            // Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
            setLastClientPosition(null);
            setLastServerPosition(null);
        } else
            // Переходим в режим обсервинга
            sendPacket(new ObserverStart(loc));
        // "Телепортируемся"
        sendPacket(new TeleportToLocation(this, loc, 0));
        return true;
    }

    public void appearObserverMode() {
        L2WorldRegion observNeighbor = _observNeighbor;
        L2WorldRegion currentRegion = getCurrentRegion();
        if (observNeighbor == null || currentRegion == null) {
            leaveObserverMode(Olympiad.getGameBySpectator(this));
            return;
        }

        _observerMode = 3;

        // Очищаем все видимые обьекты
        for (L2WorldRegion neighbor : currentRegion.getNeighbors())
            neighbor.removeObjectsFromPlayer(this);

        // Добавляем фэйк в точку наблюдения
        if (!_observNeighbor.equals(currentRegion))
            _observNeighbor.addObject(this);

        // Показываем чару все обьекты, что находятся в точке наблюдения и соседних регионах
        for (L2WorldRegion neighbor : _observNeighbor.getNeighbors())
            neighbor.showObjectsToPlayer(this, false);

        if (getOlympiadObserveId() > -1) {
            if (_olympiadGame != null) {
                _olympiadGame.broadcastInfo(null, this, true);
                _olympiadGame.updateEffectIcons();
            }
        }
    }

    public void returnFromObserverMode() {
        _observerMode = 0;
        _observNeighbor = null;
        _olympiadObserveId = -1;
        setIsInvul(false);
        setInvisible(false);
        sendUserInfo(true);
        broadcastRelationChanged();

        L2WorldRegion currentRegion = getCurrentRegion();

        // Показываем чару все обьекты, что находятся в точке возрата и соседних регионах
        if (currentRegion != null)
            for (L2WorldRegion neighbor : currentRegion.getNeighbors())
                neighbor.showObjectsToPlayer(this, false);

        broadcastUserInfo(true);
    }

    public void leaveObserverMode(OlympiadGame game) {
        L2WorldRegion observNeighbor = _observNeighbor;

        // Удаляем фэйк из точки наблюдения и удаляем у чара все обьекты, что там находятся
        if (observNeighbor != null)
            for (L2WorldRegion neighbor : observNeighbor.getNeighbors()) {
                neighbor.removeObjectsFromPlayer(this);
                neighbor.removeObject(this, false);
            }

        _observNeighbor = null;
        _observerMode = 2;

        setTarget(null);
        setIsInvul(false);
        setInvisible(false);
        sendUserInfo(true);

        broadcastRelationChanged();
        unblock();

        setReflection(0);

        sendPacket(new ShowBoard(0));
        setEventMaster(null);

        if (game != null) {
            _olympiadGame = null;

            if (game != null)
                game.removeSpectator(this);

            // Меняем интерфейс
            sendPacket(new ExOlympiadMode(0));
            sendPacket(new ExOlympiadMatchEnd());

            // Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
            setLastClientPosition(null);
            setLastServerPosition(null);
        } else if (_olympiadObserveId > 0) {
            sendPacket(new ExOlympiadMode(0));

            setLastClientPosition(null);
            setLastServerPosition(null);
        } else
            // Выходим из режима обсервинга
            sendPacket(new ObserverEnd(this));
        _olympiadObserveId = -1;
        // "Телепортируемся"
        sendPacket(new TeleportToLocation(this, getLoc(), 0));
    }

    public void setOlympiadSide(final int i) {
        _olympiadSide = i;
    }

    public int getOlympiadSide() {
        return _olympiadSide;
    }

    public void setOlympiadGame(OlympiadGame game) {
        _olympiadGame = game;
    }

    public OlympiadGame getOlympiadGame() {
        return _olympiadGame;
    }

    public int getOlympiadObserveId() {
        return _olympiadObserveId;
    }

    public Location getObsLoc() {
        return _obsLoc;
    }

    @Override
    public boolean inObserverMode() {
        return _observerMode > 0;
    }

    public byte getObserverMode() {
        return _observerMode;
    }

    public void setObserverMode(byte mode) {
        _observerMode = mode;
    }

    public L2WorldRegion getObservNeighbor() {
        return _observNeighbor;
    }

    public void setObservNeighbor(L2WorldRegion region) {
        _observNeighbor = region;
    }

    public int getTeleMode() {
        return _telemode;
    }

    public void setTeleMode(final int mode) {
        _telemode = mode;
    }

    public void setLoto(final int i, final int val) {
        _loto[i] = val;
    }

    public int getLoto(final int i) {
        return _loto[i];
    }

    public void setRace(final int i, final int val) {
        _race[i] = val;
    }

    public int getRace(final int i) {
        return _race[i];
    }

    public boolean getMessageRefusal() {
        return _messageRefusal;
    }

    public void setMessageRefusal(final boolean mode) {
        _messageRefusal = mode;
        EtcStatusUpdate();
    }

    public void setTradeRefusal(final boolean mode) {
        _tradeRefusal = mode;
    }

    public boolean getTradeRefusal() {
        return _tradeRefusal;
    }

    public void setExchangeRefusal(final boolean mode) {
        _exchangeRefusal = mode;
    }

    public boolean getExchangeRefusal() {
        return _exchangeRefusal;
    }

    public void addToBlockList(final String charName) {
        if (charName == null || charName.equalsIgnoreCase(getName()) || isInBlockList(charName)) {
            // уже в списке
            sendPacket(Msg.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
            return;
        }

        L2Player block_target = L2World.getPlayer(charName);

        if (block_target != null) {
            if (block_target.isGM()) {
                sendPacket(Msg.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
                return;
            }
            _blockList.put(block_target.getObjectId(), block_target.getName());
            sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST).addString(block_target.getName()));
            block_target.sendPacket(new SystemMessage(SystemMessage.S1__HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST).addString(getName()));
            if (isLindvior())
                sendPacket(new ExBlockAddResult(charName));
            //sendPacket(new BlockList(this));
            return;
        }

        // чар не в игре
        int charId = Util.GetCharIDbyName(charName);

        if (charId == 0) {
            // чар не существует
            sendPacket(Msg.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
            return;
        }

        if (ConfigSystem.gmlist.containsKey(charId) && ConfigSystem.gmlist.get(charId).IsGM) {
            sendPacket(Msg.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
            return;
        }
        _blockList.put(charId, charName);
        sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST).addString(charName));
        if (isLindvior())
            sendPacket(new BlockList(this));
    }

    public void removeFromBlockList(final String charName) {
        int charId = 0;
        for (int blockId : _blockList.keySet())
            if (charName.equalsIgnoreCase(_blockList.get(blockId))) {
                charId = blockId;
                break;
            }
        if (charId == 0) {
            sendPacket(Msg.YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER_FROM_IGNORE_LIST);
            return;
        }
        sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_REMOVED_FROM_YOUR_IGNORE_LIST).addString(_blockList.remove(charId)));
        L2Player block_target = L2ObjectsStorage.getPlayer(charId);
        if (block_target != null)
            block_target.sendMessage(getName() + " has removed you from his/her Ignore List."); //В системных(619 == 620) мессагах ошибка ;)
        if (isLindvior())
            sendPacket(new ExBlockRemoveResult(charName));
        //sendPacket(new BlockList(this));
    }

    public boolean isInBlockList(final L2Player player) {
        return isInBlockList(player.getObjectId());
    }

    public boolean isInBlockList(final int charId) {
        return _blockList != null && _blockList.containsKey(charId);
    }

    public boolean isInBlockList(final String charName) {
        for (int blockId : _blockList.keySet())
            if (charName.equalsIgnoreCase(_blockList.get(blockId)))
                return true;
        return false;
    }

    public boolean isBlockAll() {
        return _blockAll;
    }

    public void setBlockAll(final boolean state) {
        _blockAll = state;
        EtcStatusUpdate();
    }

    public Collection<String> getBlockList() {
        return _blockList.values();
    }

    public void setConnected(boolean connected) {
        _isConnected = connected;
    }

    public boolean isConnected() {
        return _isConnected || isFantome();
    }

    public void setHero(final boolean hero, final int type) {
        _heroType = type;
    }

    @Override
    public boolean isHero() {
        return _heroType > -1;
    }

    @Override
    public int isHeroType() {
        return _heroType;
    }

    public void setIsInOlympiadMode(final boolean b) {
        _inOlympiadMode = b;
    }

    @Override
    public boolean isInOlympiadMode() {
        return _inOlympiadMode;
    }

    /**
     * <b>0</b> - False<br>
     * <b>1</b> - <font color=red>Fight Club</font><br>
     * <b>2</b> - <font color=red>Last Hero</font><br>
     * <b>3</b> - <font color=red>Capture The Flag</font><br>
     * <b>4</b> - <font color=red>Team vs Team</font><br>
     * <b>5</b> - <font color=red>Tournament</font><br>
     * <b>6</b> - <font color=red>EventBox</font><br>
     * <b>7</b> - <font color=red>CastleSiege</font><br>
     * <b>8</b> - <font color=red>FractionEvent1</font><br>
     * <b>9</b> - <font color=red>FractionEvent2</font><br>
     * <b>10</b> - <font color=red>FractionEvent3</font><br>
     * <b>11</b> - <font color=red>ChaosFestival</font><br>
     * <b>12</b> - <font color=red>Tournament2</font><br>
     * <b>13</b> - <font color=red>DeathMatch</font><br>
     * <b>14</b> - <font color=red>TheHungerGames</font><br>
     * <b>15</b> - <font color=red>Dota Game</font><br>
     */
    public void setIsInEvent(byte set) {
        _eventNotUseItem = set;
    }

    /**
     * Получаем название ивента.<br>
     * <b> <font color=red>Вызывать только после проверки isInEvent() != 0;</font></b>
     */
    public static String getEventName(byte id) {
        String name;
        switch (id) {
            case 1:
                name = "Fight Club";
                break;
            case 2:
                name = "Last Hero";
                break;
            case 3:
                name = "Capture The Flag";
                break;
            case 4:
                name = "Team vs Team";
                break;
            case 5:
                name = "Tournament";
                break;
            case 6:
                name = "Cubic";
                break;
            case 7:
                name = "Castle Siege";
                break;
            case 8:
                name = "Fraction Event 1";
                break;
            case 9:
                name = "Fraction Event 2";
                break;
            case 10:
                name = "Fraction Event 3";
                break;
            case 13:
                name = "Death Match";
                break;
            case 14:
                name = "The Hunger Games";
                break;
            default:
                name = "other";
                //setIsInEvent((byte) 0);
                break;
        }
        return name;
    }

    public byte isInEvent() {
        return _eventNotUseItem;
    }

    public boolean isOlympiadGameStart() {
        return _olympiadGame != null && _olympiadGame.getState() == 1;
    }

    public boolean isOlympiadCompStart() {
        return _olympiadGame != null && _olympiadGame.getState() == 2;
    }

    public void updateNobleSkills() {
        if (isNoble()) {
            super.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_BUILD_HEADQUARTERS, 1)/*.setNotUse(isClanLeader() ? 0 : 1)*/);
            super.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_STRIDER_ASSAULT, 1));
            super.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_WYVERN_AEGIS, 1));
            super.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_NOBLESSE_BLESSING, 1));
            super.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_SUMMON_CP_POTION, 1));
            super.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_FORTUNE_OF_NOBLESSE, 1));
            super.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_HARMONY_OF_NOBLESSE, 1));
            super.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_SYMPHONY_OF_NOBLESSE, 1));
        } else {
            super.removeSkillById(L2Skill.SKILL_BUILD_HEADQUARTERS, false);
            super.removeSkillById(L2Skill.SKILL_STRIDER_ASSAULT, false);
            super.removeSkillById(L2Skill.SKILL_WYVERN_AEGIS, false);
            if (!ConfigValue.CharacterSetSkillNoble)
                super.removeSkillById(L2Skill.SKILL_NOBLESSE_BLESSING, false);
            super.removeSkillById(L2Skill.SKILL_SUMMON_CP_POTION, false);
            super.removeSkillById(L2Skill.SKILL_FORTUNE_OF_NOBLESSE, false);
            super.removeSkillById(L2Skill.SKILL_HARMONY_OF_NOBLESSE, false);
            super.removeSkillById(L2Skill.SKILL_SYMPHONY_OF_NOBLESSE, false);
            updateEffectIcons();
        }
    }

    public void setNoble(boolean noble) {
        _noble = noble;
        if (getAttainment() != null)
            getAttainment().setNoble();
    }

    public boolean isNoble() {
        return _noble;
    }

    public int getSubLevel() {
        return isSubClassActive() ? getLevel() : 0;
    }

    /* varka silenos and ketra orc quests related functions */
    public void updateKetraVarka() {
        if (Functions.getItemCount(this, 7215) > 0)
            _ketra = 5;
        else if (Functions.getItemCount(this, 7214) > 0)
            _ketra = 4;
        else if (Functions.getItemCount(this, 7213) > 0)
            _ketra = 3;
        else if (Functions.getItemCount(this, 7212) > 0)
            _ketra = 2;
        else if (Functions.getItemCount(this, 7211) > 0)
            _ketra = 1;
        else if (Functions.getItemCount(this, 7225) > 0)
            _varka = 5;
        else if (Functions.getItemCount(this, 7224) > 0)
            _varka = 4;
        else if (Functions.getItemCount(this, 7223) > 0)
            _varka = 3;
        else if (Functions.getItemCount(this, 7222) > 0)
            _varka = 2;
        else if (Functions.getItemCount(this, 7221) > 0)
            _varka = 1;
        else {
            _varka = 0;
            _ketra = 0;
        }
    }

    public int getVarka() {
        return _varka;
    }

    public int getKetra() {
        return _ketra;
    }

    public void updateRam() {
        if (Functions.getItemCount(this, 7247) > 0)
            _ram = 2;
        else if (Functions.getItemCount(this, 7246) > 0)
            _ram = 1;
        else
            _ram = 0;
    }

    public int getRam() {
        return _ram;
    }

    public void setPledgeType(final int typeId) {
        _pledgeType = typeId;
    }

    public int getPledgeType() {
        return _pledgeType;
    }

    public void setLvlJoinedAcademy(int lvl) {
        _lvlJoinedAcademy = lvl;
    }

    public int getLvlJoinedAcademy() {
        return _lvlJoinedAcademy;
    }

    public void setPledgeClass(final int classId) {
        _pledgeClass = classId;
    }

    public int getPledgeClass() {
        return _pledgeClass;
    }

    public void updatePledgeClass() {
        byte CLAN_LEVEL = _clan == null ? -1 : _clan.getLevel();
        boolean IN_ACADEMY = _clan != null && _clan.isAcademy(_pledgeType);
        boolean IS_GUARD = _clan != null && _clan.isRoyalGuard(_pledgeType);
        boolean IS_KNIGHT = _clan != null && _clan.isOrderOfKnights(_pledgeType);
        boolean IS_GUARD_CAPTAIN = false;
        boolean IS_KNIGHT_COMMANDER = false;
        if (_clan != null && _pledgeType == 0) {
            int leaderOf = _clan.getClanMember(_objectId).isSubLeader();
            if (_clan.isRoyalGuard(leaderOf))
                IS_GUARD_CAPTAIN = true;
            else if (_clan.isOrderOfKnights(leaderOf))
                IS_KNIGHT_COMMANDER = true;
        }

        switch (CLAN_LEVEL) {
            case -1:
                _pledgeClass = RANK_VAGABOND;
                break;
            case 0:
            case 1:
            case 2:
            case 3:
                if (isClanLeader())
                    _pledgeClass = RANK_HEIR;
                else
                    _pledgeClass = RANK_VASSAL;
                break;
            case 4:
                if (isClanLeader())
                    _pledgeClass = RANK_KNIGHT;
                else
                    _pledgeClass = RANK_HEIR;
                break;
            case 5:
                if (isClanLeader())
                    _pledgeClass = RANK_WISEMAN;
                else if (IN_ACADEMY)
                    _pledgeClass = RANK_VASSAL;
                else
                    _pledgeClass = RANK_HEIR;
                break;
            case 6:
                if (isClanLeader())
                    _pledgeClass = RANK_BARON;
                else if (IN_ACADEMY)
                    _pledgeClass = RANK_VASSAL;
                else if (IS_GUARD_CAPTAIN)
                    _pledgeClass = RANK_WISEMAN;
                else if (IS_GUARD)
                    _pledgeClass = RANK_HEIR;
                else
                    _pledgeClass = RANK_KNIGHT;
                break;
            case 7:
                if (isClanLeader())
                    _pledgeClass = RANK_COUNT;
                else if (IN_ACADEMY)
                    _pledgeClass = RANK_VASSAL;
                else if (IS_GUARD_CAPTAIN)
                    _pledgeClass = RANK_VISCOUNT;
                else if (IS_GUARD)
                    _pledgeClass = RANK_KNIGHT;
                else if (IS_KNIGHT_COMMANDER)
                    _pledgeClass = RANK_BARON;
                else if (IS_KNIGHT)
                    _pledgeClass = RANK_HEIR;
                else
                    _pledgeClass = RANK_WISEMAN;
                break;
            case 8:
                if (isClanLeader())
                    _pledgeClass = RANK_MARQUIS;
                else if (IN_ACADEMY)
                    _pledgeClass = RANK_VASSAL;
                else if (IS_GUARD_CAPTAIN)
                    _pledgeClass = RANK_COUNT;
                else if (IS_GUARD)
                    _pledgeClass = RANK_WISEMAN;
                else if (IS_KNIGHT_COMMANDER)
                    _pledgeClass = RANK_VISCOUNT;
                else if (IS_KNIGHT)
                    _pledgeClass = RANK_KNIGHT;
                else
                    _pledgeClass = RANK_BARON;
                break;
            case 9:
                if (isClanLeader())
                    _pledgeClass = RANK_DUKE;
                else if (IN_ACADEMY)
                    _pledgeClass = RANK_VASSAL;
                else if (IS_GUARD_CAPTAIN)
                    _pledgeClass = RANK_MARQUIS;
                else if (IS_GUARD)
                    _pledgeClass = RANK_BARON;
                else if (IS_KNIGHT_COMMANDER)
                    _pledgeClass = RANK_COUNT;
                else if (IS_KNIGHT)
                    _pledgeClass = RANK_WISEMAN;
                else
                    _pledgeClass = RANK_VISCOUNT;
                break;
            case 10:
                if (isClanLeader())
                    _pledgeClass = RANK_GRAND_DUKE;
                else if (IN_ACADEMY)
                    _pledgeClass = RANK_VASSAL;
                else if (IS_GUARD)
                    _pledgeClass = RANK_VISCOUNT;
                else if (IS_KNIGHT)
                    _pledgeClass = RANK_BARON;
                else if (IS_GUARD_CAPTAIN)
                    _pledgeClass = RANK_DUKE;
                else if (IS_KNIGHT_COMMANDER)
                    _pledgeClass = RANK_MARQUIS;
                else
                    _pledgeClass = RANK_COUNT;
                break;
            case 11:
                if (isClanLeader())
                    _pledgeClass = RANK_DISTINGUISHED_KING;
                else if (IN_ACADEMY)
                    _pledgeClass = RANK_VASSAL;
                else if (IS_GUARD)
                    _pledgeClass = RANK_COUNT;
                else if (IS_KNIGHT)
                    _pledgeClass = RANK_VISCOUNT;
                else if (IS_GUARD_CAPTAIN)
                    _pledgeClass = RANK_GRAND_DUKE;
                else if (IS_KNIGHT_COMMANDER)
                    _pledgeClass = RANK_DUKE;
                else
                    _pledgeClass = RANK_MARQUIS;
                break;
        }

        if (isHero() && _pledgeClass < RANK_MARQUIS)
            _pledgeClass = RANK_MARQUIS;
        else if (_noble && _pledgeClass < RANK_BARON)
            _pledgeClass = RANK_BARON;
    }

    public void setPowerGrade(final int grade) {
        _powerGrade = grade;
    }

    public int getPowerGrade() {
        return _powerGrade;
    }

    public void setApprentice(final int apprentice) {
        _apprentice = apprentice;
    }

    public int getApprentice() {
        return _apprentice;
    }

    public int getSponsor() {
        return _clan == null ? 0 : _clan.getClanMember(getObjectId()).getSponsor();
    }

    public void setTeam(final int team, boolean checksForTeam) {
        _checksForTeam = checksForTeam ? 2 : 0;
        if (_team != team) {
            _team = team;

            broadcastUserInfo(true);
            if (getPet() != null)
                getPet().broadcastPetInfo();
        }
        _damageMy = 0;
    }

    public void setTeam(final int team, int checksForTeam) {
        _checksForTeam = checksForTeam;
        if (_team != team) {
            _team = team;

            broadcastUserInfo(true);
            if (getPet() != null)
                getPet().broadcastPetInfo();
        }
        _damageMy = 0;
    }

    @Override
    public int getTeam() {
        return _team;
    }

    public int getDamageMy() {
        return _damageMy;
    }

    public void addDamageMy(int damage) {
        _damageMy = getDamageMy() + damage;
    }

    public int isChecksForTeam() {
        return _checksForTeam;
    }

    public int getNameColor() {
        if (inObserverMode())
            return Color.black.getRGB();

        return _nameColor;
    }

    public void setNameColor(final int nameColor) {
        if (nameColor != (Integer.decode("0x" + ConfigValue.NormalNameColour)) && nameColor != (Integer.decode("0x" + ConfigValue.ClanleaderNameColour)) && nameColor != (Integer.decode("0x" + ConfigValue.GMNameColour)) && nameColor != (Integer.decode("0x" + ConfigValue.OfflineTradeNameColor)))
            setVar("namecolor", Integer.toHexString(nameColor));
        else if (nameColor == (Integer.decode("0x" + ConfigValue.NormalNameColour)))
            unsetVar("namecolor");
        _nameColor = nameColor;
    }

    public void setNameColor(final int red, final int green, final int blue) {
        _nameColor = (red & 0xFF) + ((green & 0xFF) << 8) + ((blue & 0xFF) << 16);
        if (_nameColor != (Integer.decode("0x" + ConfigValue.NormalNameColour)) && _nameColor != (Integer.decode("0x" + ConfigValue.ClanleaderNameColour)) && _nameColor != (Integer.decode("0x" + ConfigValue.GMNameColour)) && _nameColor != (Integer.decode("0x" + ConfigValue.OfflineTradeNameColor)))
            setVar("namecolor", Integer.toHexString(_nameColor));
        else
            unsetVar("namecolor");
    }

    public final void illegalAction(final String msg, final Integer jail_items) {
        Log.IllegalPlayerAction(this, msg, jail_items);
    }

    public final String toFullString() {
        final StringBuffer sb = new StringBuffer(160);

        sb.append("Player '").append(getName()).append("' [oid=").append(_objectId).append(", account='").append(getAccountName()).append(", ip=").append(getIP()).append("']");
        return sb.toString();
    }

    protected final FastMap<String, String> user_variables = new FastMap<String, String>().setShared(true);

    public void setVarInst(String name, String value) {
        if (getAttainment() != null)
            getAttainment().incReflection(-1);

        user_variables.put(name, value);
        mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,-1)", _objectId, name, value);
    }

    public void setVar(String name, String value) {
        user_variables.put(name, value);
        mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,-1)", _objectId, name, value);
    }

    public void setVar(String name, String value, long expirationTime) {
        user_variables.put(name, value);
        mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,?)", getObjectId(), name, value, expirationTime);
    }

    public void setVar(String name, int value, long expirationTime) {
        setVar(name, String.valueOf(value), expirationTime);
    }

    public void setVar(String name, long value, long expirationTime) {
        setVar(name, String.valueOf(value), expirationTime);
    }

    public void unsetVar(String name) {
        if (name == null)
            return;

        if (user_variables.remove(name) != null)
            mysql.set("DELETE FROM `character_variables` WHERE `obj_id`=? AND `type`='user-var' AND `name`=? LIMIT 1", _objectId, name);
    }

    public String getVar(String name, String defaultVal) {
        try {
            String var = user_variables.get(name);
            if (var == null)
                return defaultVal;
            return var;
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public String getVar(String name) {
        return user_variables.get(name);
    }

    public boolean getVarB(String name, boolean defaultVal) {
        String var = user_variables.get(name);
        if (var == null)
            return defaultVal;
        return !(var.equals("0") || var.equalsIgnoreCase("false"));
    }

    public boolean getVarB(String name) {
        String var = user_variables.get(name);
        return !(var == null || var.equals("0") || var.equalsIgnoreCase("false"));
    }

    public long getVarLong(String name) {
        return getVarLong(name, 0);
    }

    public long getVarLong(String name, long defaultVal) {
        long result = defaultVal;
        String var = getVar(name);
        if (var != null)
            result = Long.parseLong(var);
        return result;
    }

    public int getVarInt(String name) {
        return getVarInt(name, 0);
    }

    public int getVarInt(String name, int defaultVal) {
        int result = defaultVal;
        String var = getVar(name);
        if (var != null)
            result = Integer.parseInt(var);
        return result;
    }

    public FastMap<String, String> getVars() {
        return user_variables;
    }

    public String getLang() {
        return getVar("lang@", "ru");
    }

    public int getLangId() {
        String lang = getLang();
        if (lang.equalsIgnoreCase("en") || lang.equalsIgnoreCase("e") || lang.equalsIgnoreCase("eng"))
            return LANG_ENG;
        if (lang.equalsIgnoreCase("ru") || lang.equalsIgnoreCase("r") || lang.equalsIgnoreCase("rus"))
            return LANG_RUS;
        return LANG_UNK;
    }

    public boolean isLangRus() {
        return getLangId() == LANG_RUS;
    }

    public int isAtWarWith(final Integer id) {
        return _clan == null || !_clan.isAtWarWith(id) ? 0 : 1;
    }

    public int isAtWar() {
        return _clan == null || _clan.isAtWarOrUnderAttack() <= 0 ? 0 : 1;
    }

    public void stopWaterTask() {
        if (_taskWater != null) {
            _taskWater.cancel(false);
            _taskWater = null;
            sendPacket(new SetupGauge(getObjectId(), 2, 0, 0));
            sendChanges();
        }
    }

    public void startWaterTask() {
        if (isDead())
            stopWaterTask();
        else if (ConfigValue.AllowWater && _taskWater == null) {
            int timeinwater = (int) (calcStat(Stats.BREATH, 86, null, null) * 1000L);
            sendPacket(new SetupGauge(getObjectId(), 2, timeinwater, timeinwater));
            if (getTransformation() > 0 && getTransformationTemplate() > 0 && !isCursedWeaponEquipped())
                if (getTransformation() != 301 && getTransformation() != 302 && !isTransformLalka())
                    setTransformation(0);
            _taskWater = ThreadPoolManager.getInstance().scheduleAtFixedRate(new WaterTask(this), timeinwater, 1000);
            sendChanges();
        }
    }

    public void checkWaterState() {
        if (isInZoneWater())
            startWaterTask();
        else
            stopWaterTask();
    }

    private boolean _reviveRequested = false;
    private double _revivePower = 0;
    private boolean _revivePet = false;

    public void doRevive(double percent) {
        restoreExp(percent);
        // зачем здесь это?
        //if(getTransformation() > 0 && !isBlessedByNoblesse())
        //	setTransformation(0);
        doRevive();
        setResTime();
    }

    @Override
    public void doRevive() {
        super.doRevive();
        if (getObjectId() == i_ai7 && ConfigValue.TestRevive > 0) {
            _log.info("L2Player: doRevive -> " + getName() + " " + getTeam());
            Util.test();
        }
        getListeners().onRevive();
        setAgathionRes(false);
        unsetVar("lostexp");
        updateEffectIcons();
        AutoShot();
        _reviveRequested = false;
        _revivePower = 0;
    }

    public void reviveRequest(L2Player Reviver, double percent, boolean Pet) {
        if (_reviveRequested) {
            if (_revivePower > percent) {
                Reviver.sendPacket(Msg.BETTER_RESURRECTION_HAS_BEEN_ALREADY_PROPOSED);
                return;
            }
            if (Pet && !_revivePet) {
                Reviver.sendPacket(Msg.SINCE_THE_MASTER_WAS_IN_THE_PROCESS_OF_BEING_RESURRECTED_THE_ATTEMPT_TO_RESURRECT_THE_PET_HAS_BEEN_CANCELLED);
                return;
            }
            if (Pet && isDead()) {
                Reviver.sendPacket(Msg.WHILE_A_PET_IS_ATTEMPTING_TO_RESURRECT_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER);
                return;
            }
        }
        if (Pet && getPet() != null && getPet().isDead() || !Pet && isDead()) {
            _reviveRequested = true;
            _revivePower = percent;
            _revivePet = Pet;
            ConfirmDlg pkt = new ConfirmDlg(SystemMessage.S1_IS_MAKING_AN_ATTEMPT_AT_RESURRECTION_WITH_$S2_EXPERIENCE_POINTS_DO_YOU_WANT_TO_CONTINUE_WITH_THIS_RESURRECTION, 0, 2);
            pkt.addString(Reviver.getName()).addString(Math.round(_revivePower) + " percent");
            sendPacket(pkt);
        }
    }

    public void reviveAnswer(int answer) {
        if (!_reviveRequested || !isDead() && !_revivePet || _revivePet && getPet() != null && !getPet().isDead())
            return;
        if ((getTeam() > 0 || isInEvent() > 0) && (isRestartPoint() > 1 || isRestartPoint() == -1))
            return;
        if (answer == 1) {
            if (!_revivePet)
                doRevive(_revivePower);
            else if (getPet() != null)
                ((L2PetInstance) getPet()).doRevive(_revivePower);
            _reviveRequested = false;
            _revivePower = 0;
        }
    }

    /**
     * Координаты точки призыва персонажа
     */
    private Location _SummonCharacterCoords;
    private L2Character _SummonCharacter;

    /**
     * Флаг необходимости потребления Summoning Cystall-а при призыве персонажа
     */
    private int _SummonConsumeCrystall = 0;

    /**
     * Обработчик ответа клиента на призыв персонажа.
     *
     * @param answer Идентификатор запроса
     */
    public void summonCharacterAnswer(int answer) {
        int summoningCrystallId = 8615;
        if (answer == 1 && _SummonCharacterCoords != null && Call.canSummonHere(_SummonCharacter) == null) {
            abortAttack(true, true);
            abortCast(true);
            stopMove();
            if (Call.canBeSummoned(this) != null)
                return;
            if (_SummonConsumeCrystall > 0) {
                L2ItemInstance ConsumedItem = getInventory().getItemByItemId(summoningCrystallId);
                if (ConsumedItem != null && ConsumedItem.getCount() >= _SummonConsumeCrystall) {
                    getInventory().destroyItemByItemId(summoningCrystallId, _SummonConsumeCrystall, false);
                    sendPacket(SystemMessage.removeItems(summoningCrystallId, _SummonConsumeCrystall));
                    teleToLocation(_SummonCharacterCoords);
                } else
                    sendPacket(Msg.INCORRECT_ITEM_COUNT);
            } else
                teleToLocation(_SummonCharacterCoords);
        }
        _SummonCharacterCoords = null;
        _SummonCharacter = null;
    }

    /**
     * Отправляет запрос клиенту на призыв персонажа.
     *
     * @param Summoner Имя призывающего персонажа
     * @param loc          Координаты точки призыва персонажа
     */
    public void summonCharacterRequest(L2Character Summoner, Location loc, int SummonConsumeCrystall) {
        if (_SummonCharacterCoords == null) {
            _SummonConsumeCrystall = SummonConsumeCrystall;
            _SummonCharacterCoords = loc;
            _SummonCharacter = Summoner;
            ConfirmDlg cd = new ConfirmDlg(SystemMessage.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT, 60000, 1);
            cd.addString(Summoner.getName()).addZoneName(_SummonCharacterCoords);
            sendPacket(cd);
        }
    }

    String _scriptName = "";
    Object[] _scriptArgs = new Object[0];

    public void scriptAnswer(int answer) {
        if (answer == 1 && !_scriptName.equals("")) {
            if (_scriptName.equals("call_bbs")) {
                CommunityBoard.getInstance().handleCommands(getNetConnection(), (String) _scriptArgs[0]);
                //TODO [FUZZY]
            } else if (_scriptName.startsWith("PartyMaker")) {
                if (_scriptName.endsWith("deleteGroup")) {
                    PartyMaker.getInstance().deleteGroup(this);
                } else if (_scriptName.contains("excludeFromParty")) {
                    PartyMaker.getInstance().excludeFromParty(this, _scriptName.split(":")[2]);
                }
                //TODO [FUZZY]
            } else if (_scriptName.startsWith("FludProtect")) {
                this.unblock();
            } else {
                callScripts(_scriptName.split(":")[0], _scriptName.split(":")[1], _scriptArgs);
            }
        }
        _scriptName = "";
    }

    public void scriptRequest(String text, String scriptName, Object[] args) {
        if (isCombatFlagEquipped() || isTerritoryFlagEquipped())
            return;
        else if (_scriptName.equals("")) {
            _scriptName = scriptName;
            _scriptArgs = args;
            sendPacket(new ConfirmDlg(SystemMessage.S1, 30000, 3).addString(text));
        }
    }

    public boolean isReviveRequested() {
        return _reviveRequested;
    }

    public boolean isRevivingPet() {
        return _revivePet;
    }

    public int getTimeOnline() {
        return timeOnline;
    }

    public void setTimeOnline(int value) {
        timeOnline = value;
    }

    private Recommendation _recommendation = new Recommendation(this);

    public Recommendation getRecommendation() {
        return _recommendation;
    }

    private NevitBlessing _nevit = new NevitBlessing(this);

    public NevitBlessing getNevitBlessing() {
        return _nevit;
    }

    @Override
    public boolean isInVehicle() {
        return _vehicle != null;
    }

    public L2Vehicle getVehicle() {
        return _vehicle;
    }

    public void setVehicle(L2Vehicle boat) {
        if (boat == null && _vehicle != null)
            _vehicle._players.remove(this);
        else if (boat != null)
            boat._players.add(this);
        _vehicle = boat;
    }

    public Location getInVehiclePosition() {
        return _inVehiclePosition;
    }

    public void setInVehiclePosition() {
        _inVehiclePosition = new Location();
    }

    public void setInVehiclePosition(int x, int y, int z) {
        if (_inVehiclePosition == null)
            _inVehiclePosition = new Location(x, y, z);
        _inVehiclePosition.x = x;
        _inVehiclePosition.y = y;
        _inVehiclePosition.z = z;
    }

    public HashMap<Integer, L2SubClass> getSubClasses() {
        return _classlist;
    }

    public void setBaseClass(final int baseClass) {
        _baseClass = baseClass;
    }

    public int getBaseClassId() {
        return _baseClass;
    }

    public void setActiveClass(L2SubClass activeClass) {
        if (activeClass == null) {
            System.out.print("WARNING! setActiveClass(null);");
            Thread.dumpStack();
        }
        _activeClass = activeClass;
        _activeClassId = activeClass.getClassId();
    }

    public L2SubClass getActiveClass() {
        return _activeClass;
    }

    public int getActiveClassId() {
        return getActiveClass().getClassId();
    }

    /**
     * Устанавливает активный сабкласс
     *
     * <li>Retrieve from the database all skills of this L2Player and add them to _skills </li>
     * <li>Retrieve from the database all macroses of this L2Player and add them to _macroses</li>
     * <li>Retrieve from the database all shortCuts of this L2Player and add them to _shortCuts</li><BR><BR>
     */
    public void setActiveSubClass(final int subId, final boolean store) {
        final L2SubClass sub = getSubClasses().get(subId);
        if (sub == null) {
            System.out.print("WARNING! setActiveSubClass<?> :: sub == null :: subId == " + subId);
            Thread.dumpStack();
            return;
        }
        if (subId == getBaseClassId())
            sub.setBase(true);
        else
            sub.setBase(false);
        if (getActiveClass() != null) {
            PlayerData.getInstance().storeEffects(this);
            PlayerData.getInstance().storeDisableSkills(this);

            if (QuestManager.getQuest(422) != null) {
                String qn = QuestManager.getQuest(422).getName();
                if (qn != null) {
                    QuestState qs = getQuestState(qn);
                    if (qs != null)
                        qs.exitCurrentQuest(true);
                }
            }
        }

        if (store) {
            final L2SubClass oldsub = getActiveClass();
            oldsub.setCp(getCurrentCp());
            //oldsub.setExp(getExp());
            //oldsub.setLevel(getLevel());
            //oldsub.setSp(getSp());
            oldsub.setHp(getCurrentHp());
            oldsub.setMp(getCurrentMp());
            oldsub.setActive(false);
            getSubClasses().put(getActiveClassId(), oldsub);
        }

        sub.setActive(true);
        setActiveClass(sub);
        getSubClasses().put(getActiveClassId(), sub);

        setClassId(subId, false);

        if (!ConfigValue.Multi_Enable2)
            removeAllSkills(false);

        getEffectList().stopAllEffects(false);

        if (getPet() != null && (getPet().isSummon() || ConfigValue.ImprovedPetsLimitedUse && (getPet().getNpcId() == PetDataTable.IMPROVED_BABY_KOOKABURRA_ID && !isMageClass() || getPet().getNpcId() == PetDataTable.IMPROVED_BABY_BUFFALO_ID && isMageClass())))
            getPet().unSummon();

        for (L2Cubic cubic : getCubics())
            cubic.deleteMe();

        getCubics().clear();
        setAgathion(0);

        PlayerData.getInstance().restoreSkills(this);
        rewardSkills();
        sendPacket(new ExStorageMaxCount(this));
        sendPacket(new SkillList(this));

        if (ConfigValue.RestoreSubEffects || !store)
            PlayerData.getInstance().restoreEffects(this);
        if (isVisible()) // костыль для загрузки чара
            PlayerData.getInstance().restoreDisableSkills(this);

        getInventory().refreshListeners(false);
        getInventory().checkAllConditions();

        for (int i = 0; i < 3; i++)
            _henna[i] = null;

        PlayerData.getInstance().restoreHenna(this);
        sendPacket(new HennaInfo(this));

        setCurrentHpMp(sub.getHp(), sub.getMp());
        setCurrentCp(sub.getCp());
        broadcastUserInfo(true);
        updateStats();

        _shortCuts.restore();
        sendPacket(new ShortCutInit(this));
        for (int shotId : getAutoSoulShot())
            sendPacket(new ExAutoSoulShot(shotId, true));
        sendPacket(new SkillCoolTime(this));

        broadcastPacket(new SocialAction(getObjectId(), SocialAction.LEVEL_UP));

        getDeathPenalty().restore();

        setIncreasedForce(0);

        ThreadPoolManager.getInstance().schedule(new DisableToogle(), 500); //Затычка для канцела туглов при смене сабов

        updateEffectIcons();
    }

    public class DisableToogle extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            boolean update = false;
            for (L2Effect effect : getEffectList().getAllEffects())
                if (effect != null && effect.getSkill().isToggle()) {
                    update = true;
                    effect.exit(false, false);
                }
            if (update)
                updateEffectIcons();
        }
    }

    /**
     * Через delay миллисекунд выбросит игрока из игры
     */
    public void startKickTask(long delay) {
        if (_kickTask != null)
            stopKickTask();
        _kickTask = ThreadPoolManager.getInstance().schedule(new KickTask(this), delay, true);
    }

    public void stopKickTask() {
        if (_kickTask != null) {
            _kickTask.cancel(false);
            _kickTask = null;
        }
    }

    public void startBonusTask(long time) {
        time *= 1000;
        time -= System.currentTimeMillis();
        if (_bonusExpiration != null)
            stopBonusTask();
        _bonusExpiration = ThreadPoolManager.getInstance().schedule(new BonusTask(this), time, true);
    }

    public void startBonusTask(long time, int type) {
        time *= 1000;
        time -= System.currentTimeMillis();
        if (_bonusExpiration2[type] != null)
            stopBonusTask(type);
        _bonusExpiration2[type] = ThreadPoolManager.getInstance().schedule(new BonusTask2(this, type), time, true);
    }

    public void stopBonusTask() {
        if (_bonusExpiration != null) {
            _bonusExpiration.cancel(true);
            _bonusExpiration = null;
        }
    }

    public void stopBonusTask(int type) {
        if (_bonusExpiration2[type] != null) {
            _bonusExpiration2[type].cancel(true);
            _bonusExpiration2[type] = null;
        }
    }

    public int getInventoryLimit() {
        return (int) calcStat(Stats.INVENTORY_LIMIT, 0, null, null);
    }

    public int getWarehouseLimit() {
        return (int) calcStat(Stats.STORAGE_LIMIT, 0, null, null);
    }

    public int getFreightLimit() {
        // FIXME Не учитывается количество предметов, уже имеющееся на складе
        return getWarehouseLimit();
    }

    public int getTradeLimit() {
        return (int) calcStat(Stats.TRADE_LIMIT, 0, null, null);
    }

    public int getDwarvenRecipeLimit() {
        return (int) calcStat(Stats.DWARVEN_RECIPE_LIMIT, 50, null, null) + ConfigValue.AltAddRecipes;
    }

    public int getCommonRecipeLimit() {
        return (int) calcStat(Stats.COMMON_RECIPE_LIMIT, 50, null, null) + ConfigValue.AltAddRecipes;
    }

    @Override
    public int getNpcId() {
        // FIXMY: Не знаю на сколько принципиально здесь -2, по этому пускай будет так пока...
        return isPolymorphed() ? getPolyid() : -2;
    }

    public L2Object getVisibleObject(int id) {
        if (getObjectId() == id)
            return this;

        if (getTargetId() == id)
            return getTarget();

        if (_party != null)
            for (L2Player p : _party.getPartyMembers())
                if (p != null && p.getObjectId() == id)
                    return p;

        L2Object obj = L2World.getAroundObjectById(this, id);

        // Руль кланового летающего корабля
        if (obj == null && isInVehicle() && getVehicle().isClanAirShip() && ClanTable.getInstance().getClan(id) != null)
            obj = ((L2AirShip) getVehicle()).getControlKey();

        return obj == null || obj.isInvisible() ? null : obj;
    }

    @Override
    public int getPAtk(final L2Character target) {
        double init = getActiveWeaponInstance() == null ? (isMageClass() ? 3 : 4) : 0;
        return (int) (calcStat(Stats.p_physical_attack, init, target, null) * getTemplate().p_atk_mod);
    }

    @Override
    public int getPDef(final L2Character target) {
        double init = 4; //empty cloak and underwear slots

        final L2ItemInstance chest = getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
        if (chest == null)
            init += isMageClass() ? L2Armor.EMPTY_BODY_MYSTIC : L2Armor.EMPTY_BODY_FIGHTER;
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS) == null && (chest == null || chest.getBodyPart() != L2Item.SLOT_FULL_ARMOR))
            init += isMageClass() ? L2Armor.EMPTY_LEGS_MYSTIC : L2Armor.EMPTY_LEGS_FIGHTER;

        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD) == null)
            init += L2Armor.EMPTY_HELMET;
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES) == null)
            init += L2Armor.EMPTY_GLOVES;
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET) == null)
            init += L2Armor.EMPTY_BOOTS;

        return (int) (calcStat(Stats.p_physical_defence, init, target, null) * getTemplate().p_def_mod);
    }

    @Override
    public int getMDef(final L2Character target, final L2Skill skill) {
        double init = 0;

        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR) == null)
            init += L2Armor.EMPTY_EARRING;
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR) == null)
            init += L2Armor.EMPTY_EARRING;
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK) == null)
            init += L2Armor.EMPTY_NECKLACE;
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER) == null)
            init += L2Armor.EMPTY_RING;
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER) == null)
            init += L2Armor.EMPTY_RING;

        return (int) (calcStat(Stats.p_magical_defence, init, target, skill) * getTemplate().m_def_mod);
    }

    public boolean isSubClassActive() {
        return getBaseClassId() != getActiveClassId() && !getActiveClass().isBase2();
    }

    @Override
    public String getTitle() {
        if (ConfigValue.RangEnable && ConfigValue.RangEnableTitle)
            return (getRangTitle() + super.getTitle());
        else
            return super.getTitle();
    }

    // -------------------------------------- Система Рангов --------------------------------------
    private int _rangId = 0;

    public void setRangId(int id) {
        _rangId = id;
    }

    public int getRangId() {
        return _rangId;
    }

    private String _titleRang = "[Воин 9]";

    public void setRangTitle(String title) {
        String old = _titleRang;
        _titleRang = title;
        if (!old.equals(title))
            sendChanges();
        old = null;
    }

    public String getRangTitle() {
        return _titleRang;
    }

    private long _pointRang = 0;

    public void setRangPoint() {

    }

    public void setRangPoint(long point, boolean update, boolean updateDb, boolean insertDb) {

    }

    public void setRangPoint(long point) {

    }

    public void addRangPoint(long point) {

    }

    public long getRangPoint() {
        return _pointRang;
    }

    // -------------------------------------- Система Рангов End --------------------------------------
    public int getTitleColor() {
        return _titlecolor;
    }

    public void setTitleColor(final int color) {
        _titlecolor = color;
    }

    public void setTitleColor(final int red, final int green, final int blue) {
        _titlecolor = (red & 0xFF) + ((green & 0xFF) << 8) + ((blue & 0xFF) << 16);
    }

    public Forum getMemo() {
        if (_forumMemo == null) {
            if (ForumsBBSManager.getInstance().getForumByName("MemoRoot") == null)
                return null;
            if (ForumsBBSManager.getInstance().getForumByName("MemoRoot").GetChildByName(_accountName) == null)
                ForumsBBSManager.getInstance().CreateNewForum(_accountName, ForumsBBSManager.getInstance().getForumByName("MemoRoot"), Forum.MEMO, Forum.OWNERONLY, getObjectId());
            setMemo(ForumsBBSManager.getInstance().getForumByName("MemoRoot").GetChildByName(_accountName));
        }
        return _forumMemo;
    }

    /**
     * @param forum
     */
    public void setMemo(final Forum forum) {
        _forumMemo = forum;
    }

    @Override
    public boolean isCursedWeaponEquipped() {
        return _cursedWeaponEquippedId != 0;
    }

    public void setCursedWeaponEquippedId(int value) {
        _cursedWeaponEquippedId = value;
    }

    public int getCursedWeaponEquippedId() {
        return _cursedWeaponEquippedId;
    }

    private FishData _fish;

    public void setFish(FishData fish) {
        _fish = fish;
    }

    public void stopLookingForFishTask() {
        if (_taskforfish != null) {
            _taskforfish.cancel(false);
            _taskforfish = null;
        }
    }

    public void startLookingForFishTask(FishData fish, L2ItemInstance lure, boolean isFirst) {
        if (!isDead() && _taskforfish == null) {
            if (isFirst) {
                setFish(fish);
                setLure(lure);
                if (ConfigValue.FishingProtectChance > 0 && Rnd.chance(ConfigValue.FishingProtectChance)) {
                    ProtectFunction.getInstance().getFishProtect(this);
                    is_block = true;
                    return;
                }
            }
            p_block_move(true, null);
            setFishing(true);
            broadcastUserInfo(true);
            broadcastPacket(new ExFishingStart(this, _fish.getType(), getFishLoc(), _lure.isNightLure()));
            sendPacket(Msg.STARTS_FISHING);

            long checkDelay = 10000L;
            boolean isNoob = false;
            boolean isUpperGrade = false;

            if (_lure != null) {
                //int lureid = _lure.getItemId();
                isNoob = _fish.getGroup() == 0;
                isUpperGrade = _fish.getGroup() == 2;
				/*if(lureid == 6519 || lureid == 6522 || lureid == 6525 || lureid == 8505 || lureid == 8508 || lureid == 8511) //low grade
					checkDelay = Math.round((float) (_fish.getGutsCheckTime() * 1.33));
				else if(lureid == 6520 || lureid == 6523 || lureid == 6526 || lureid >= 8505 && lureid <= 8513 || lureid >= 7610 && lureid <= 7613 || lureid >= 7807 && lureid <= 7809 || lureid >= 8484 && lureid <= 8486) //medium grade, beginner, prize-winning & quest special bait
					checkDelay = Math.round((float) (_fish.getGutsCheckTime() * 1.00));
				else if(lureid == 6521 || lureid == 6524 || lureid == 6527 || lureid == 8507 || lureid == 8510 || lureid == 8513 || lureid == 8548) //high grade
					checkDelay = Math.round((float) (_fish.getGutsCheckTime() * 0.66));
				else
					checkDelay = _fish.getGutsCheckTime();*/
            }

            switch (_fish.getGroup()) {
                case 0:
                    checkDelay = Math.round(_fish.getGutsCheckTime() * 1.33);
                    break;
                case 1:
                    checkDelay = _fish.getGutsCheckTime();
                    break;
                case 2:
                    checkDelay = Math.round(_fish.getGutsCheckTime() * 0.66);
                    break;
            }
            try {
                _taskforfish = ThreadPoolManager.getInstance().scheduleAtFixedRate(new LookingForFishTask(this, _fish.getWaitTime(), _fish.getFishGuts(), _fish.getType(), isNoob, isUpperGrade), 10000, checkDelay);
            } catch (Exception e) {
                System.out.println("L2Player: " + checkDelay);
            }
        }
    }

    public void startFishCombat(boolean isNoob, boolean isUpperGrade) {
        _fishCombat = new L2Fishing(this, _fish, isNoob, isUpperGrade);
    }

    public void endFishing(boolean win) {
        ExFishingEnd efe = new ExFishingEnd(win, this);
        broadcastPacket(efe);
        _fishing = false;
        _fishLoc = new Location();
        broadcastUserInfo(true);
        if (_fishCombat == null)
            sendPacket(Msg.BAITS_HAVE_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
        _fishCombat = null;
        _lure = null;
        //Ends fishing
        sendPacket(Msg.ENDS_FISHING);
        p_block_move(false, null);
        stopLookingForFishTask();
    }

    public L2Fishing getFishCombat() {
        return _fishCombat;
    }

    public void setFishLoc(Location loc) {
        _fishLoc = loc;
    }

    public Location getFishLoc() {
        return _fishLoc;
    }

    public void setLure(L2ItemInstance lure) {
        _lure = lure;
    }

    public L2ItemInstance getLure() {
        return _lure;
    }

    public boolean isFishing() {
        return _fishing;
    }

    public void setFishing(boolean fishing) {
        _fishing = fishing;
    }

    public Bonus getBonus() {
        return _bonus;
    }

    public void restoreBonus() {
        _bonus = new Bonus(this);
    }

    @Override
    public double getRateAdena() {
        return calcStat(Stats.ADENA, _party == null ? _bonus.RATE_DROP_ADENA * getAltBonus() : _party._rateAdena, this, null);
    }

    @Override
    public double getRateItems() {
        return calcStat(Stats.DROP, _party == null ? _bonus.RATE_DROP_ITEMS * getAltBonus() : _party._rateDrop, this, null);
    }

    @Override
    public float getRateFame() {
        return _bonus.RATE_FAME * getAltBonus();
    }

    @Override
    public float getRateEpaulette() {
        return _bonus.RATE_EPAULETTE * getAltBonus();
    }

    @Override
    public float getRateMaxLoad() {
        return _bonus == null ? 1f : _bonus.RATE_MAX_LOAD;
    }

    @Override
    public double getRateExp() {
        return calcStat(Stats.EXP, (_party == null ? _bonus.RATE_XP * getAltBonus() : _party._rateExp), null, null);
    }

    @Override
    public double getRateSp() {
        return calcStat(Stats.SP, (_party == null ? _bonus.RATE_SP * getAltBonus() : _party._rateSp), null, null);
    }

    @Override
    public double getRateSpoil() {
        return calcStat(Stats.SPOIL, _party == null ? _bonus.RATE_DROP_SPOIL * getAltBonus() : _party._rateSpoil, this, null);
    }

    @Override
    public float getRateChest() {
        return 1.0f;//_bonus.RATE_DROP_CHEST;
    }

    private boolean _maried = false;
    private int _partnerId = 0;
    private int _coupleId = 0;
    private boolean _engagerequest = false;
    private int _engageid = 0;
    private boolean _maryrequest = false;
    private boolean _maryaccepted = false;

    public boolean isMaried() {
        return _maried;
    }

    public void setMaried(boolean state) {
        _maried = state;
    }

    public boolean isEngageRequest() {
        return _engagerequest;
    }

    public void setEngageRequest(boolean state, int playerid) {
        _engagerequest = state;
        _engageid = playerid;
    }

    public void setMaryRequest(boolean state) {
        _maryrequest = state;
    }

    public boolean isMaryRequest() {
        return _maryrequest;
    }

    public void setMaryAccepted(boolean state) {
        _maryaccepted = state;
    }

    public boolean isMaryAccepted() {
        return _maryaccepted;
    }

    public int getEngageId() {
        return _engageid;
    }

    public int getPartnerId() {
        return _partnerId;
    }

    public void setPartnerId(int partnerid) {
        _partnerId = partnerid;
    }

    public int getCoupleId() {
        return _coupleId;
    }

    public void setCoupleId(int coupleId) {
        _coupleId = coupleId;
    }

    public void engageAnswer(int answer) {
        if (!_engagerequest || _engageid == 0)
            return;

        L2Player ptarget = L2ObjectsStorage.getPlayer(_engageid);
        setEngageRequest(false, 0);
        if (ptarget != null)
            if (answer == 1) {
                CoupleManager.getInstance().createCouple(ptarget, this);
                ptarget.sendMessage(new CustomMessage("l2open.gameserver.model.L2Player.EngageAnswerYes", this));
            } else
                ptarget.sendMessage(new CustomMessage("l2open.gameserver.model.L2Player.EngageAnswerNo", this));
    }

    public final FastMap<Long, SkillTimeStamp> skillReuseTimeStamps = new FastMap<Long, SkillTimeStamp>().setShared(true);

    public FastMap<Long, SkillTimeStamp> getSkillReuseTimeStamps() {
        return skillReuseTimeStamps;
    }

    private void addSkillTimeStamp(Integer skillId, Integer level, long reuseDelay) {
        reuseSkillLock.lock();
        try {
            if (ConfigValue.PtsBagMacro) {
                reuseDelay -= ConfigValue.PtsBagMacroTime;
                if (reuseDelay < 500)
                    reuseDelay = 500;
            }
            skillReuseTimeStamps.put(ConfigValue.SkillReuseType == 0 ? skillId * 65536L + level : skillId, new SkillTimeStamp(skillId, level, System.currentTimeMillis() + reuseDelay, reuseDelay, 0));
        } finally {
            reuseSkillLock.unlock();
        }
    }

    private void removeSkillTimeStamp(Long skillId) {
        reuseSkillLock.lock();
        try {
            skillReuseTimeStamps.remove(skillId);
        } finally {
            reuseSkillLock.unlock();
        }
    }

    @Override
    public boolean isSkillDisabled(Long skillId) {
        reuseSkillLock.lock();
        try {
            SkillTimeStamp sts = skillReuseTimeStamps.get(skillId);

            if (sts == null)
                return false;
            if (sts.hasNotPassed())
                return true;
            skillReuseTimeStamps.remove(skillId);
            return false;
        } finally {
            reuseSkillLock.unlock();
        }
    }

    @Override
    public void disableSkill(int skillId, int level, long delay) {
        if (delay > 10)
            addSkillTimeStamp(skillId, level, delay);
    }

    @Override
    public void enableSkill(Long skillId) {
        removeSkillTimeStamp(skillId);
    }

    public ScheduledFuture<?> getWaterTask() {
        return _taskWater;
    }

    public DeathPenalty getDeathPenalty() {
        return getActiveClass().getDeathPenalty();
    }

    public void setDeathPeanalty(DeathPenalty dp) {
        getActiveClass().setDeathPenalty(dp);
    }

    //fast fix for dice spam
    public long lastDiceThrown = 0;

    private boolean _charmOfCourage = false;

    public boolean isCharmOfCourage() {
        return _charmOfCourage;
    }

    public void setCharmOfCourage(boolean val) {
        _charmOfCourage = val;

        if (!val)
            getEffectList().stopEffect(L2Skill.SKILL_CHARM_OF_COURAGE);

        EtcStatusUpdate();
    }

    public void revalidatePenalties() {
        super.removeSkill(getKnownSkill(4270), false, true);
        _curWeightPenalty = 0;
        armorExpertisePenalty = 0;
        weaponExpertisePenalty = 0;
        refreshOverloaded();
        validateItemExpertisePenalties(true, true, true);
    }

    private int _increasedForce = 0;
    private int _consumedSouls = 0;

    @Override
    public int getIncreasedForce() {
        return _increasedForce;
    }

    @Override
    public int getConsumedSouls() {
        return _consumedSouls;
    }

    @Override
    public void setConsumedSouls(int i, L2NpcInstance monster) {
        if (i == _consumedSouls)
            return;

        int max = (int) calcStat(Stats.SOULS_LIMIT, 0, monster, null);

        if (i > max)
            i = max;

        if (i <= 0) {
            _consumedSouls = 0;
            stopSoulTask();
            EtcStatusUpdate();
            return;
        }

        if (_consumedSouls != i) {
            int diff = i - _consumedSouls;
            if (diff > 0) {
                SystemMessage sm = new SystemMessage(SystemMessage.YOUR_SOUL_HAS_INCREASED_BY_S1_SO_IT_IS_NOW_AT_S2);
                sm.addNumber(diff);
                sm.addNumber(i);
                sendPacket(sm);
            }
        } else if (max == i) {
            sendPacket(Msg.SOUL_CANNOT_BE_ABSORBED_ANY_MORE);
            return;
        }

        _consumedSouls = i;
        restartSoulTask();
        EtcStatusUpdate();
    }

    private void restartSoulTask() {
        synchronized (this) {
            if (_soulTask != null) {
                _soulTask.cancel(false);
                _soulTask = null;
            }
            _soulTask = ThreadPoolManager.getInstance().schedule(new SoulTask(), 600000);
        }
    }

    public void stopSoulTask() {
        if (_soulTask != null) {
            _soulTask.cancel(false);
            _soulTask = null;
        }
    }

    protected class SoulTask extends l2open.common.RunnableImpl {
        @Override
        public void runImpl() {
            setConsumedSouls(0, null);
        }
    }

    @Override
    public void setIncreasedForce(int i) {
        i = Math.min(i, Charge.MAX_CHARGE);
        i = Math.max(i, 0);

        restartForceTask();

        if (i != 0 && i > _increasedForce)
            sendPacket(new SystemMessage(SystemMessage.YOUR_FORCE_HAS_INCREASED_TO_S1_LEVEL).addNumber(i));

        if (i == 0)
            stopForceTask();

        _increasedForce = i;
        EtcStatusUpdate();
    }

    private void restartForceTask() {
        if (_forceTask != null) {
            _forceTask.cancel(false);
            _forceTask = null;
        }
        _forceTask = ThreadPoolManager.getInstance().schedule(new ForceTask(), 600000);
    }

    public void stopForceTask() {
        if (_forceTask != null) {
            _forceTask.cancel(false);
            _forceTask = null;
        }
    }

    protected class ForceTask extends l2open.common.RunnableImpl {
        @Override
        public void runImpl() {
            setIncreasedForce(0);
        }
    }

    private long _lastFalling;

    public boolean isFalling() {
        return System.currentTimeMillis() - _lastFalling < 1500;
    }

    public void falling(int height) {
        if (!ConfigValue.DamageFromFalling || isDead() || isFlying() || isSwimming() || isInVehicle())
            return;
        _lastFalling = System.currentTimeMillis();
        int damage = (int) calcStat(Stats.FALL, getMaxHp() / 2000 * height, null, null);
        if (damage > 0) {
            int curHp = (int) getCurrentHp();
            if (curHp - damage < 1)
                setCurrentHp(1, false);
            else
                setCurrentHp(curHp - damage, false);
            sendPacket(new SystemMessage(SystemMessage.YOU_RECEIVED_S1_DAMAGE_FROM_TAKING_A_HIGH_FALL).addNumber(damage));
        }
    }

    /**
     * Системные сообщения о текущем состоянии хп
     */
    @Override
    public void checkHpMessages(double curHp, double newHp) {
        //сюда пасивные скиллы
        byte[] _hp = {30, 30, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60};
        int[] skills = {290, 291, 3027, 3028, 3029, 3030, 3031, 3032, 3033, 3034, 3056, 3069, 3071};

        //сюда активные эффекты. 292 - 30% сообщения не должно быть, только 60%
        int[] _effects_skills_id = {176, 292, 766};
        byte[] _effects_hp = {60, 60, 30};

        double percent = getMaxHp() / 100;
        int _curHpPercent = (int) (curHp / percent);
        int _newHpPercent = (int) (newHp / percent);
        boolean needsUpdate = false;

        //check for passive skills
        for (int i = 0; i < skills.length; i++) {
            short level = getSkillLevel(skills[i]);
            if (level > 0)
                if (_curHpPercent > _hp[i] && _newHpPercent <= _hp[i]) {
                    sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(skills[i], level)); // У Вас мало HP. Вы ощущаете эффект умения $s1.
                    needsUpdate = true;
                } else if (_curHpPercent <= _hp[i] && _newHpPercent > _hp[i]) {
                    sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR).addSkillName(skills[i], level)); // Количество HP увеличилось. Действие умения $s1 прекращается.
                    needsUpdate = true;
                }
        }

        //check for active effects
        for (Integer i = 0; i < _effects_skills_id.length; i++)
            if (getEffectList().getEffectBySkillId(_effects_skills_id[i]) != null)
                if (_curHpPercent > _effects_hp[i] && _newHpPercent <= _effects_hp[i]) {
                    sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(_effects_skills_id[i], 1)); // У Вас мало HP. Вы ощущаете эффект умения $s1.
                    needsUpdate = true;
                } else if (_curHpPercent <= _effects_hp[i] && _newHpPercent > _effects_hp[i]) {
                    sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR).addSkillName(_effects_skills_id[i], 1)); // Количество HP увеличилось. Действие умения $s1 прекращается.
                    needsUpdate = true;
                }

        if (needsUpdate)
            sendChanges();
    }

    /**
     * Системные сообщения для темных эльфов о вкл/выкл ShadowSence (skill id = 294)
     */
    public void checkDayNightMessages() {
        short level = getSkillLevel(294);
        if (level > 0)
            if (GameTimeController.getInstance().isNowNight())
                sendPacket(new SystemMessage(SystemMessage.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(294, level));
            else
                sendPacket(new SystemMessage(SystemMessage.IT_IS_DAWN_AND_THE_EFFECT_OF_S1_WILL_NOW_DISAPPEAR).addSkillName(294, level));
        sendChanges();
    }

    private boolean _isInDangerArea;

    public boolean isInDangerArea() {
        return _isInDangerArea;
    }

    public void setInDangerArea(boolean value) {
        _isInDangerArea = value;
    }

    public void setInCombatZone(boolean flag) {
        _isInCombatZone = flag;
    }

    public void setOnSiegeField(boolean flag) {
        _isOnSiegeField = flag;
    }

    public boolean isInPeaceZone() {
        return _isInPeaceZone || getReflection().isPeace();
    }

    public void setInPeaceZone(boolean b) {
        _isInPeaceZone = b;
        if (b) {
            if (getDuel() != null)
                getDuel().onPlayerDefeat(this);
            VitalityManager.getInstance().addRegenTask(this);
        }
    }

    public boolean isInSSZone() {
        return _isInSSZone;
    }

    public void setInSSZone(boolean b) {
        _isInSSZone = b;
    }

    public boolean isInCombatZone() {
        return _isInCombatZone;
    }

    public boolean isOnSiegeField() {
        return _isOnSiegeField;
    }

    public void doZoneCheck(int messageNumber) {
        boolean oldIsInDangerArea = isInDangerArea();
        boolean oldIsInCombatZone = isInCombatZone();
        boolean oldIsOnSiegeField = isOnSiegeField();
        boolean oldIsInPeaceZone = isInPeaceZone();
        boolean oldSSQZone = isInSSZone();

        setInDangerArea(isInZone(poison) || isInZone(instant_skill) || isInZone(swamp) || isInZone(damage));
        setInCombatZone(isInZoneBattle());
        setOnSiegeField(isInZone(Siege));
        setInPeaceZone(isInZone(peace_zone));
        setInSSZone(isInZone(ssq_zone));

        if (oldIsInDangerArea != isInDangerArea() || oldIsInCombatZone != isInCombatZone() || oldIsOnSiegeField != isOnSiegeField() || oldIsInPeaceZone != isInPeaceZone() || oldSSQZone != isInSSZone()) {
            sendPacket(new ExSetCompassZoneCode(this));
            EtcStatusUpdate();
            if (messageNumber != 0)
                sendPacket(new SystemMessage(messageNumber));
        }

        if ((oldIsInCombatZone != isInCombatZone() || oldIsOnSiegeField != isOnSiegeField()) && !isTeleporting())
            broadcastRelationChanged();

        if (oldIsOnSiegeField != isOnSiegeField())
            if (isOnSiegeField())
                sendPacket(Msg.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
            else
                sendPacket(Msg.YOU_HAVE_LEFT_A_COMBAT_ZONE);

        if (oldIsOnSiegeField != isOnSiegeField() && !isOnSiegeField() && !isTeleporting() && getPvpFlag() == 0)
            startPvPFlag(null);

        if (isInPeaceZone()) {
            getNevitBlessing().stopBonus();
            getRecommendation().stopRecBonus();
        }

        revalidateInResidence();
    }

    private Future<?> _returnTerritoryFlagTask = null;

    public void checkTerritoryFlag() {
        if (isTerritoryFlagEquipped()) {
            L2ItemInstance flag = getActiveWeaponInstance();
            if (flag != null && flag.getCustomType1() != 77) // 77 это эвентовый флаг
            {
                L2Zone siegeZone = ZoneManager.getInstance().getZoneByType(ZoneType.Siege, getX(), getY(), true);
                if (siegeZone == null && (_returnTerritoryFlagTask == null || _returnTerritoryFlagTask.isDone())) {
                    _returnTerritoryFlagTask = ThreadPoolManager.getInstance().schedule(new ReturnTerritoryFlagTask(this), 600000);
                    sendMessage("У вас есть 10 минут, чтобы вернуться в осадную зону, иначе флаг вернется в замок. Вы можете использовать форты как промежуточные точки, для сброса таймера.");
                }
                if (siegeZone != null && _returnTerritoryFlagTask != null) {
                    _returnTerritoryFlagTask.cancel(true);
                    _returnTerritoryFlagTask = null;
                }
            }
        }
    }

    private ResidenceType _inResidence = ResidenceType.None;

    public void revalidateInResidence() {
        L2Clan clan = _clan;
        if (clan == null)
            return;
        int clanHallIndex = clan.getHasHideout();
        if (clanHallIndex != 0) {
            ClanHall clansHall = ClanHallManager.getInstance().getClanHall(clanHallIndex);
            if (clansHall != null && clansHall.checkIfInZone(getX(), getY())) {
                setInResidence(ResidenceType.Clanhall);
                return;
            }
        }
        int castleIndex = clan.getHasCastle();
        if (castleIndex != 0) {
            Castle castle = CastleManager.getInstance().getCastleByIndex(castleIndex);
            if (castle != null && castle.checkIfInZone(getX(), getY())) {
                setInResidence(ResidenceType.Castle);
                return;
            }
        }
        int fortressIndex = clan.getHasFortress();
        if (fortressIndex != 0) {
            Fortress fort = FortressManager.getInstance().getFortressByIndex(fortressIndex);
            if (fort != null && fort.checkIfInZone(getX(), getY())) {
                setInResidence(ResidenceType.Fortress);
                return;
            }
        }
        setInResidence(ResidenceType.None);
    }

    public ResidenceType getInResidence() {
        return _inResidence;
    }

    public void setInResidence(ResidenceType inResidence) {
        _inResidence = inResidence;
    }

    @Override
    public void sendMessage(String message) {
        sendPacket(new SystemMessage(message));
    }

    @Override
    public void sendGMMessage(String message) {
        sendPacket(new Say2(0, Say2C.GM, "SYS", message));
    }

    private Location _lastClientPosition;
    private Location _lastServerPosition;

    @Override
    public void setLastClientPosition(Location position) {
        //if(position != null && ConfigValue.DebugMovePackets)
        //	_log.info("setLastClientPosition: x="+position.x+" y="+position.y+" z="+position.z);
        _lastClientPosition = position;
    }

    public Location getLastClientPosition() {
        return _lastClientPosition;
    }

    @Override
    public void setLastServerPosition(Location position) {
        _lastServerPosition = position;
    }

    public Location getLastServerPosition() {
        return _lastServerPosition;
    }

    private int _useSeed = 0;

    public void setUseSeed(int id) {
        _useSeed = id;
    }

    public int getUseSeed() {
        return _useSeed;
    }

    /**
     * 1. Вступил/вышел из клана.
     * 2. Вступил/вышел из альянса.
     * 3. Вступил/вышел из пати.
     * 4. Сменил лидера пати/клана.
     * 5. Началось ТВ/Осада.
     * 6. Чар вошел в игру.
     * 7. Кинул вар.
     * 8. Изменилась карма.
     * 9. флаганулся чар.
     * 10. вошел/вышел из зоны.
     * 11. Старт матча оли.
     * 12. Старт дуэли.
     * 13. Сделать отправку везде где задается setTeam!!!
     **/
    public int getRelation(L2Player target) {
        int result = 0;

        if (isInEvent() == 11)
            return result;

        if (getClan() != null) {
            result |= RelationChanged.RELATION_CLAN_MEMBER;
            if (getClan() == target.getClan())
                result |= RelationChanged.RELATION_CLAN_MATE;
            if (getAllyId() != 0)
                result |= RelationChanged.RELATION_ALLY_MEMBER;
        }

        if (isClanLeader())
            result |= RelationChanged.RELATION_LEADER;

        L2Party party = getParty();
        if (party != null && party == target.getParty()) {
            result |= RelationChanged.RELATION_HAS_PARTY;

            switch (party.getPartyMembers().indexOf(this)) {
                case 0:
                    result |= RelationChanged.RELATION_PARTYLEADER; // 0x10
                    break;
                case 1:
                    result |= RelationChanged.RELATION_PARTY4; // 0x8
                    break;
                case 2:
                    result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1; // 0x7
                    break;
                case 3:
                    result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2; // 0x6
                    break;
                case 4:
                    result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY1; // 0x5
                    break;
                case 5:
                    result |= RelationChanged.RELATION_PARTY3; // 0x4
                    break;
                case 6:
                    result |= RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1; // 0x3
                    break;
                case 7:
                    result |= RelationChanged.RELATION_PARTY2; // 0x2
                    break;
                case 8:
                    result |= RelationChanged.RELATION_PARTY1; // 0x1
                    break;
            }
        }

        if (getSiegeState() != 0) {
            result |= RelationChanged.RELATION_INSIEGE;
            if (getSiegeState() != target.getSiegeState() || (getClan() != null && target.getClan() != null && getClan() != target.getClan())) {
                result |= RelationChanged.RELATION_ENEMY;
            } else {
                result |= RelationChanged.RELATION_ALLY;
            }
            if (getSiegeState() == 1) {
                result |= RelationChanged.RELATION_ATTACKER;
            }
        }

        L2Clan clan1 = getClan();
        L2Clan clan2 = target.getClan();
        if (clan1 != null && clan2 != null && (getEventMaster() == null || getEventMaster().isClanWarIcon())) {
            if (target.getPledgeType() != L2Clan.SUBUNIT_ACADEMY && getPledgeType() != L2Clan.SUBUNIT_ACADEMY)
                if (clan2.isAtWarWith(clan1.getClanId())) {
                    result |= RelationChanged.RELATION_1SIDED_WAR;
                    if (clan1.isAtWarWith(clan2.getClanId()))
                        result |= RelationChanged.RELATION_MUTUAL_WAR;
                }
        }

        if (getBlockCheckerArena() != -1) {
            //result |= RelationChanged.RELATION_INSIEGE;
            HandysBlockCheckerManager.ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(getBlockCheckerArena());
            if (holder != null && holder.getPlayerTeam(this) == 0 || getTeam() == 2)
                result |= RelationChanged.RELATION_ENEMY;
            else
                result |= RelationChanged.RELATION_ALLY;
            //result |= RelationChanged.RELATION_ATTACKER;
        }

        if (getTerritorySiege() > -1) {
            result |= RelationChanged.RELATION_TERRITORY_WAR;
        }
        return result;
    }

    public void setBlockCheckerArena(byte arena) {
        _handysBlockCheckerEventArena = arena;
    }

    public int getBlockCheckerArena() {
        return _handysBlockCheckerEventArena;
    }

    /**
     * 0=White, 1=Purple, 2=PurpleBlink
     */
    protected int _pvpFlag;
    public boolean _block_pvp_flag = false;

    public Future<?> _PvPRegTask;
    public long _lastPvpAttack;

    public void setlastPvpAttack(long time) {
        _lastPvpAttack = time;
    }

    public long getlastPvpAttack() {
        return _lastPvpAttack;
    }

    @Override
    public void startPvPFlag(L2Character target) {
        if (!_block_pvp_flag) {
            long startTime = System.currentTimeMillis();
            if (target != null && target.getPvpFlag() != 0)
                startTime -= ConfigValue.PvPTime / 2;
            if (_pvpFlag != 0 && _lastPvpAttack > startTime)
                return;
            _lastPvpAttack = startTime;

            updatePvPFlag(1);

            if (_PvPRegTask == null)
                _PvPRegTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PvPFlagTask(this), 1000, 1000);
        }
    }

    public void stopPvPFlag() {
        if (!_block_pvp_flag) {
            if (_PvPRegTask != null) {
                _PvPRegTask.cancel(false);
                _PvPRegTask = null;
            }
            updatePvPFlag(0);
        }
    }

    public void updatePvPFlag(int value) {
        if (_handysBlockCheckerEventArena != -1)
            return;
        if (_pvpFlag == value)
            return;

        setPvpFlag(value);

        if (_karma < 1) {
            sendStatusUpdate(true, StatusUpdate.PVP_FLAG);
            if (getPet() != null)
                getPet().broadcastPetInfo();
        }

        broadcastRelationChanged();
    }

    public void setPvpFlag(int pvpFlag) {
        _pvpFlag = pvpFlag;
    }

    @Override
    public int getPvpFlag() {
        return _pvpFlag;
    }

    private Duel _duel;

    public void setDuel(Duel duel) {
        _duel = duel;
        broadcastCharInfo();
    }

    @Override
    public Duel getDuel() {
        return _duel;
    }

    public boolean isInDuel() {
        return _duel != null;
    }

    private List<L2TamedBeastInstance> _tamedBeast = null;

    public List<L2TamedBeastInstance> getTrainedBeast() {
        return _tamedBeast;
    }

    public void setTrainedBeast(L2TamedBeastInstance tamedBeast) {
        if (_tamedBeast == null)
            _tamedBeast = new CopyOnWriteArrayList<L2TamedBeastInstance>();
        _tamedBeast.add(tamedBeast);
    }

    public byte[] getKeyBindings() {
        return _keyBindings;
    }

    public void setKeyBindings(byte[] keyBindings) {
        if (keyBindings == null)
            keyBindings = new byte[0];
        _keyBindings = keyBindings;
    }

    /**
     * Устанавливает режим трансформаии<BR>
     *
     * @param transformationId идентификатор трансформации
     *                         Известные режимы:<BR>
     *                         <li>0 - стандартный вид чара
     *                         <li>1 - Onyx Beast
     *                         <li>2 - Death Blader
     *                         <li>etc.
     */
    public void setTransformation(int transformationId) {
        if (transformationId == _transformationId || _transformationId != 0 && transformationId != 0 || ConfigValue.NoTransformTerrFlag && isTerritoryFlagEquipped() || !can_transform && transformationId != 0 || isMounted() && transformationId != 0 && transformationId != 8 && transformationId != 9)
            return;

        // Для каждой трансформации свой набор скилов
        if (transformationId == 0) // Обычная форма
        {
            //Util.test();
            // Останавливаем текущий эффект трансформации
            for (L2Effect effect : getEffectList().getAllEffects())
                if (effect != null && effect.getEffectType() == EffectType.Transformation) {
                    if (effect.calc() == 0) // Не обрываем Dispel
                        continue;
                    preparateToTransform(effect.getSkill());
                    break;
                }
            getEffectList().stopAllSkillEffects(EffectType.Transformation);
            // Удаляем скилы трансформации
            if (_transformationSkills != null && !_transformationSkills.isEmpty()) {
                for (L2Skill s : _transformationSkills.values())
                    if (!s.isCommon() && !SkillTreeTable.getInstance().isSkillPossible(this, s.getId(), s.getLevel()))
                        super.removeSkill(s, false, false);
                super.removeSkillById(619, false);
                updateEffectIcons();
                _transformationSkills.clear();
            }
            if (getPet() != null && getPet().getNpcId() == 14870)
                getPet().unSummon();
        } else {
            if (!isCursedWeaponEquipped()) {
                // Добавляем скилы трансформации
                for (L2Effect effect : getEffectList().getAllEffects())
                    if (effect != null && effect.getEffectType() == EffectType.Transformation) {
                        if (effect.getSkill().isTransformation() && ((Transformation) effect.getSkill()).isDisguise) {
                            for (L2Skill s : getAllSkills())
                                if (s != null && (s.isActive() || s.isToggle()))
                                    _transformationSkills.put(s.getId(), s);
                        } else
                            for (AddedSkill s : effect.getSkill().getAddedSkills())
                                if (s.level == 0) // трансформация позволяет пользоваться обычным скиллом
                                {
                                    int s2 = getSkillLevel(s.id);
                                    if (s2 > 0)
                                        _transformationSkills.put(s.id, SkillTable.getInstance().getInfo(s.id, s2));
                                } else if (s.level == -2) // XXX: дикий изжоп для скиллов зависящих от уровня игрока
                                {
                                    int learnLevel = Math.max(effect.getSkill().getMagicLevel(), 40);
                                    int maxLevel = SkillTable.getInstance().getBaseLevel(s.id);
                                    int curSkillLevel = 1;
                                    if (maxLevel > 3)
                                        curSkillLevel += getLevel() - learnLevel;
                                    else
                                        curSkillLevel += (getLevel() - learnLevel) / ((76 - learnLevel) / maxLevel); // не спрашивайте меня что это такое
                                    curSkillLevel = Math.min(Math.max(curSkillLevel, 1), maxLevel);
                                    _transformationSkills.put(s.id, SkillTable.getInstance().getInfo(s.id, curSkillLevel));
                                } else
                                    _transformationSkills.put(s.id, s.getSkill());
                        preparateToTransform(effect.getSkill());
                        break;
                    }
            } else
                preparateToTransform(null);
            // Для все трансформаций кроме проклятых добавляем скилы:
            // - обратной трансформации (619)
            // - Decrease Bow/Crossbow Attack Speed (5491)
            if (!isCursedWeaponEquipped() && transformationId != 113) {
                _transformationSkills.put(L2Skill.SKILL_TRANSFOR_DISPELL, SkillTable.getInstance().getInfo(L2Skill.SKILL_TRANSFOR_DISPELL, 1));
                if (transformationId != 301 && transformationId != 302 && transformationId != 312 && transformationId != 313 && transformationId != 314 && transformationId != 315 && transformationId != 316 && transformationId != 317 && transformationId != 318 && transformationId != 219)
                    _transformationSkills.put(5491, SkillTable.getInstance().getInfo(5491, 1));
            }

            for (L2Skill s : _transformationSkills.values())
                addSkill(SkillTable.getInstance().getInfo(s.getId(), s.getLevel()), false);
        }

        int prev_t = _transformationId;
        _transformationId = transformationId;

        sendPacket(new ExBasicActionList(this));
        sendPacket(new SkillList(this));
        sendPacket(new ShortCutInit(this));
        if (getAutoSoulShot() != null)
            for (int shotId : getAutoSoulShot())
                sendPacket(new ExAutoSoulShot(shotId, true));
        if (!isDead())
            broadcastUserInfo(true);
        if (getPet() != null)
            getPet().updateStats();
		/*else
		{
			if(_broadcastCharInfoTask == null)
				_broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new BroadcastCharInfoTask(this), prev_t == 306 ? 10800 : 3000, true);
			if(_userInfoTask == null)
				_userInfoTask = ThreadPoolManager.getInstance().schedule(new UserInfoTask(this), prev_t == 306 ? 10800 : 3000, true);
		}*/
    }

    private void preparateToTransform(L2Skill transSkill) {
        if (transSkill == null || !transSkill.isBaseTransformation()) {
            boolean update = false;
            // Останавливаем тугл скиллы
            for (L2Effect effect : getEffectList().getAllEffects())
                if (effect != null && effect.getSkill().isToggle()) {
                    update = true;
                    effect.exit(false, false);
                }
            if (update)
                updateEffectIcons();
        }
    }

    public boolean isTransformed() {
        return _transformationId != 0;
    }

    public boolean isInFlyingTransform() {
        return _transformationId == 8 || _transformationId == 9 || _transformationId == 260;
    }

    public boolean isInMountTransform() {
        return _transformationId == 106 || _transformationId == 109 || _transformationId == 110 || _transformationId == 20001 || _transformationId == 400 || _transformationId == 401 || _transformationId == 402 || _transformationId == 403 || _transformationId == 404 || _transformationId == 405 || _transformationId == 406 || _transformationId == 407 || _transformationId == 408 || _transformationId == 409 || _transformationId == 410 || _transformationId == 411 || _transformationId == 412 || _transformationId == 413 || _transformationId == 414 || _transformationId == 415 || _transformationId == 416 || _transformationId == 417 || _transformationId == 418 || _transformationId == 419 || _transformationId == 420 || _transformationId == 421 || _transformationId == 422 || _transformationId == 423 || _transformationId == 424 || _transformationId == 425 || _transformationId == 426 || _transformationId == 427 || _transformationId == 428 || _transformationId == 429 || _transformationId == 430 || _transformationId == 431 || _transformationId == 432 || _transformationId == 433 || _transformationId == 434 || _transformationId == 435 || _transformationId == 436 || _transformationId == 437 || _transformationId == 438 || _transformationId == 439 || _transformationId == 440 || _transformationId == 441 || _transformationId == 442 || _transformationId == 443 || _transformationId == 444 || _transformationId == 445 || _transformationId == 446 || _transformationId == 447 || _transformationId == 448 || _transformationId == 449 || _transformationId == 450 || _transformationId == 451;
    }

	/*public boolean isInMountTransform()
	{
		return _transformationId == 106 || _transformationId == 109 || _transformationId == 110 || _transformationId == 20001;
	}*/

    /**
     * Возвращает режим трансформации
     *
     * @return ID режима трансформации
     */
    public int getTransformation() {
        return _transformationId;
    }

    /**
     * Возвращает имя трансформации
     *
     * @return String
     */
    public String getTransformationName() {
        return _transformationName;
    }

    /**
     * Устанавливает имя трансформаии
     *
     * @param name имя трансформации
     */
    public void setTransformationName(String name) {
        _transformationName = name;
    }

    /**
     * Устанавливает шаблон трансформации, используется для определения коллизий
     *
     * @param template ID шаблона
     */
    public void setTransformationTemplate(int template) {
        _transformationTemplate = template;
    }

    /**
     * Возвращает шаблон трансформации, используется для определения коллизий
     *
     * @return NPC ID
     */
    public int getTransformationTemplate() {
        return _transformationTemplate;
    }

    /**
     * Возвращает коллекцию скиллов, с учетом текущей трансформации
     */
    @Override
    public final Collection<L2Skill> getAllSkills() {
        // Трансформация неактивна
        if (_transformationId == 0)
            return super.getAllSkills();

        // Трансформация активна
        HashMap<Integer, L2Skill> tempSkills = new HashMap<Integer, L2Skill>();
        for (L2Skill s : super.getAllSkills())
            if (s != null && !s.isActive() && !s.isToggle())
                tempSkills.put(s.getId(), s);
        try {
            tempSkills.putAll(_transformationSkills); // Добавляем к пассивкам скилы текущей трансформации
        } catch (
                NullPointerException e) // Может выбивать НПЕ когда чар ушел в оффлайн и в тот же момент закончился эффект какого-то скилла...
        {
            ;
        }
        return tempSkills.values();
    }

    public void setAgathion(int id) {
        if (id == 0) {
            if (_agathion != null)
                _agathion.deleteMe();
            _agathion = null;
        } else
            _agathion = new L2AgathionInstance(this, id);

        broadcastUserInfo(true);
        sendPacket(new SkillList(this));
    }

    public L2AgathionInstance getAgathion() {
        return _agathion;
    }

    /**
     * Возвращает количество PcBangPoint'ов даного игрока
     *
     * @return количество PcCafe Bang Points
     */
    public int getPcBangPoints() {
        return _pcBangPoints;
    }

    /**
     * Устанавливает количество Pc Cafe Bang Points для даного игрока
     *
     * @param pcBangPoints новое количество PcCafeBangPoints
     */
    public void setPcBangPoints(int pcBangPoints) {
        _pcBangPoints = pcBangPoints;
    }

    public void addPcBangPoints(int count, boolean doublePoints, int type) {
        if (doublePoints)
            count *= 2;

        _pcBangPoints += count;

        if (ConfigValue.AltPcBangPointsEnabled)
            sendPacket(new SystemMessage(doublePoints ? SystemMessage.DOUBLE_POINTS_YOU_AQUIRED_S1_PC_BANG_POINT : SystemMessage.YOU_ACQUIRED_S1_PC_BANG_POINT).addNumber(count));
        sendPacket(new ExPCCafePointInfo(getPcBangPoints(), count, type, 2, 12));
    }

    public boolean reducePcBangPoints(int count) {
        if (_pcBangPoints < count)
            return false;

        _pcBangPoints -= count;

        if (ConfigValue.AltPcBangPointsEnabled)
            sendPacket(new SystemMessage(SystemMessage.YOU_ARE_USING_S1_POINT).addNumber(count));
        sendPacket(new ExPCCafePointInfo(getPcBangPoints(), count, 0, 2, 12));
        return true;
    }

    private Location _groundSkillLoc;

    public void setGroundSkillLoc(Location location) {
        _groundSkillLoc = location;
    }

    public Location getGroundSkillLoc() {
        return _groundSkillLoc;
    }

    public boolean isDeleting() {
        return _isDeleting;
    }

    public void setOfflineMode(boolean val) {
        if (!val)
            unsetVar("offline");
        _offline = val;
        setIsInvul(val);
    }

    public boolean isInOfflineMode() {
        return _offline;
    }

    public void saveTradeList() {
        String val = "";

        if (_sellList == null || _sellList.isEmpty())
            unsetVar("selllist");
        else {
            for (TradeItem i : _sellList)
                val += i.getObjectId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ":";
            setVar("selllist", val);
            val = "";
            if (_tradeList != null && _tradeList.getSellStoreName() != null)
                setVar("sellstorename", _tradeList.getSellStoreName());
        }

        if (_sellPkgList == null || _sellPkgList.isEmpty())
            unsetVar("sell_pkg_list");
        else {
            for (TradeItem i : _sellPkgList)
                val += i.getObjectId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ":";
            setVar("sell_pkg_list", val);
            val = "";
            if (_tradeList != null && _tradeList.getSellPkgStoreName() != null)
                setVar("sell_pkg_storename", _tradeList.getSellPkgStoreName());
        }

        if (_buyList == null || _buyList.isEmpty())
            unsetVar("buylist");
        else {
            for (TradeItem i : _buyList)
                val += i.getItemId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ";" + i.getEnchantLevel() + ";" + i.getAttackElement()[0] + ";" + i.getAttackElement()[1] + ";" + i.getDefenceFire() + ";" + i.getDefenceWater() + ";" + i.getDefenceWind() + ";" + i.getDefenceEarth() + ";" + i.getDefenceHoly() + ";" + i.getDefenceUnholy() + ";" + i.getAugmentationId() + ";" + i.getEnchantOptions()[0] + ";" + i.getEnchantOptions()[1] + ";" + i.getEnchantOptions()[2] + ":";
            setVar("buylist", val);
            val = "";
            if (_tradeList != null && _tradeList.getBuyStoreName() != null)
                setVar("buystorename", _tradeList.getBuyStoreName());
        }

        if (_createList == null || _createList.getList().isEmpty())
            unsetVar("createlist");
        else {
            for (L2ManufactureItem i : _createList.getList())
                val += i.getRecipeId() + ";" + i.getCost() + ":";
            setVar("createlist", val);
            if (_createList.getStoreName() != null)
                setVar("manufacturename", _createList.getStoreName());
        }
    }

    public void restoreTradeList() {
        if (getVar("selllist") != null) {
            _sellList = new ConcurrentLinkedQueue<TradeItem>();
            String[] items = getVar("selllist").split(":");
            for (String item : items) {
                if (item.equals(""))
                    continue;
                String[] values = item.split(";");
                if (values.length < 3)
                    continue;
                TradeItem i = new TradeItem();
                int oId = Integer.parseInt(values[0]);
                long count = Long.parseLong(values[1]);
                long price = Long.parseLong(values[2]);
                i.setObjectId(oId);

                L2ItemInstance itemToSell = getInventory().getItemByObjectId(oId);

                if (count < 1 || itemToSell == null)
                    continue;

                if (count > itemToSell.getCount())
                    count = itemToSell.getCount();

                i.setCount(count);
                i.setOwnersPrice(price);
                i.setItemId(itemToSell.getItemId());
                i.setEnchantLevel(itemToSell.getRealEnchantLevel());
                i.setAttackElement(itemToSell.getAttackElementAndValue());
                i.setDefenceFire(itemToSell.getDefenceFire());
                i.setDefenceWater(itemToSell.getDefenceWater());
                i.setDefenceWind(itemToSell.getDefenceWind());
                i.setDefenceEarth(itemToSell.getDefenceEarth());
                i.setDefenceHoly(itemToSell.getDefenceHoly());
                i.setDefenceUnholy(itemToSell.getDefenceUnholy());
                i.setAugmentationId(itemToSell.getAugmentationId());
                i.setEnchantOptions(itemToSell.getEnchantOptions());
                i.setVisualId(itemToSell._visual_item_id);
                _sellList.add(i);
            }
            if (_tradeList == null)
                _tradeList = new L2TradeList();
            if (getVar("sellstorename") != null)
                _tradeList.setSellStoreName(getVar("sellstorename"));
        }
        if (getVar("sell_pkg_list") != null) {
            _sellPkgList = new ConcurrentLinkedQueue<TradeItem>();
            String[] items = getVar("sell_pkg_list").split(":");
            for (String item : items) {
                if (item.equals(""))
                    continue;
                String[] values = item.split(";");
                if (values.length < 3)
                    continue;
                TradeItem i = new TradeItem();
                int oId = Integer.parseInt(values[0]);
                long count = Long.parseLong(values[1]);
                long price = Long.parseLong(values[2]);
                i.setObjectId(oId);

                L2ItemInstance itemToSell = getInventory().getItemByObjectId(oId);

                if (count < 1 || itemToSell == null)
                    continue;

                if (count > itemToSell.getCount())
                    count = itemToSell.getCount();

                i.setCount(count);
                i.setOwnersPrice(price);
                i.setItemId(itemToSell.getItemId());
                i.setEnchantLevel(itemToSell.getRealEnchantLevel());
                i.setAttackElement(itemToSell.getAttackElementAndValue());
                i.setDefenceFire(itemToSell.getDefenceFire());
                i.setDefenceWater(itemToSell.getDefenceWater());
                i.setDefenceWind(itemToSell.getDefenceWind());
                i.setDefenceEarth(itemToSell.getDefenceEarth());
                i.setDefenceHoly(itemToSell.getDefenceHoly());
                i.setDefenceUnholy(itemToSell.getDefenceUnholy());
                i.setAugmentationId(itemToSell.getAugmentationId());
                i.setEnchantOptions(itemToSell.getEnchantOptions());
                i.setVisualId(itemToSell._visual_item_id);
                _sellPkgList.add(i);
            }
            if (_tradeList == null)
                _tradeList = new L2TradeList();
            if (getVar("sell_pkg_storename") != null)
                _tradeList.setSellPkgStoreName(getVar("sell_pkg_storename"));
        }
        if (getVar("buylist") != null) {
            _buyList = new ConcurrentLinkedQueue<TradeItem>();
            String[] items = getVar("buylist").split(":");
            for (String item : items) {
                if (item.equals(""))
                    continue;
                String[] values = item.split(";");
                if (values.length < 3)
                    continue;
                TradeItem i = new TradeItem();

                if (values.length >= 12) {
                    i.setItemId(Integer.parseInt(values[0]));
                    i.setCount(Long.parseLong(values[1]));
                    i.setOwnersPrice(Long.parseLong(values[2]));
                    i.setEnchantLevel(Integer.parseInt(values[3]));
                    i.setAttackElement(new int[]{Integer.parseInt(values[4]), Integer.parseInt(values[5])});
                    i.setDefenceFire(Integer.parseInt(values[6]));
                    i.setDefenceWater(Integer.parseInt(values[7]));
                    i.setDefenceWind(Integer.parseInt(values[8]));
                    i.setDefenceEarth(Integer.parseInt(values[9]));
                    i.setDefenceHoly(Integer.parseInt(values[10]));
                    i.setDefenceUnholy(Integer.parseInt(values[11]));
                    if (values.length > 12) {
                        i.setAugmentationId(Integer.parseInt(values[12]));
                        i.setEnchantOptions(new int[]{Integer.parseInt(values[13]), Integer.parseInt(values[14]), Integer.parseInt(values[15])});
                    }
                } else
                // Что бы старые офтрейдеры остались...
                {
                    i.setItemId(Integer.parseInt(values[0]));
                    i.setCount(Long.parseLong(values[1]));
                    i.setOwnersPrice(Long.parseLong(values[2]));
                    i.setEnchantLevel(0);
                    i.setAttackElement(new int[]{-2, 0});
                    i.setDefenceFire(0);
                    i.setDefenceWater(0);
                    i.setDefenceWind(0);
                    i.setDefenceEarth(0);
                    i.setDefenceHoly(0);
                    i.setDefenceUnholy(0);
                }
                _buyList.add(i);
            }
            if (_tradeList == null)
                _tradeList = new L2TradeList();
            if (getVar("buystorename") != null)
                _tradeList.setBuyStoreName(getVar("buystorename"));
        }
        if (getVar("createlist") != null) {
            _createList = new L2ManufactureList();
            String[] items = getVar("createlist").split(":");
            for (String item : items) {
                if (item.equals(""))
                    continue;
                String[] values = item.split(";");
                if (values.length < 2)
                    continue;
                _createList.add(new L2ManufactureItem(Integer.parseInt(values[0]), Long.parseLong(values[1])));
            }
            if (getVar("manufacturename") != null)
                _createList.setStoreName(getVar("manufacturename"));
        }
    }

    public L2DecoyInstance getDecoy() {
        return _decoy;
    }

    public void setDecoy(L2DecoyInstance decoy) {
        _decoy = decoy;
    }

    public int getMountType() {
        switch (getMountNpcId()) {
            case PetDataTable.STRIDER_WIND_ID:
            case PetDataTable.STRIDER_STAR_ID:
            case PetDataTable.STRIDER_TWILIGHT_ID:
            case PetDataTable.RED_STRIDER_WIND_ID:
            case PetDataTable.RED_STRIDER_STAR_ID:
            case PetDataTable.RED_STRIDER_TWILIGHT_ID:
            case PetDataTable.GUARDIANS_STRIDER_ID:
                return 1;
            case PetDataTable.WYVERN_ID:
                return 2;
            case PetDataTable.WGREAT_WOLF_ID:
            case PetDataTable.FENRIR_WOLF_ID:
            case PetDataTable.WFENRIR_WOLF_ID:
                return 3;
        }
        if (getMountNpcId() >= 51001 && getMountNpcId() <= 51019)
            return 3;
        return 0;
    }

    @Override
    public float getColRadius() {
        if (getTransformation() != 0 && getTransformationTemplate() != 0 && NpcTable.getTemplate(getTransformationTemplate()) != null)
            return NpcTable.getTemplate(getTransformationTemplate()).collisionRadius;
        else if (isMounted() && NpcTable.getTemplate(getMountNpcId()) != null)
            return NpcTable.getTemplate(getMountNpcId()).collisionRadius;
        else
            return getBaseTemplate().collisionRadius;
    }

    @Override
    public float getColHeight() {
        if (getTransformation() != 0 && getTransformationTemplate() != 0 && NpcTable.getTemplate(getTransformationTemplate()) != null)
            return NpcTable.getTemplate(getTransformationTemplate()).collisionHeight;
        else if (isMounted() && NpcTable.getTemplate(getMountNpcId()) != null)
            return NpcTable.getTemplate(getMountNpcId()).collisionHeight + getBaseTemplate().collisionHeight;
        else
            return getBaseTemplate().collisionHeight;
    }

    @Override
    public void setReflection(int i) {
        if (_reflection == i)
            return;
        super.setReflection(i);
        if (_summon != null && !_summon.isDead())
            _summon.setReflection(i);
        if (i != 0) {
            String var = getVar("reflection");
            if (var == null || !var.equals(String.valueOf(i)))
                setVar("reflection", String.valueOf(i));
        } else
            unsetVar("reflection");
        if (getActiveClass() != null) {
            getInventory().checkAllConditions();
            // Для квеста _129_PailakaDevilsLegacy
            if (getPet() != null && (getPet().getNpcId() == 14916 || getPet().getNpcId() == 14917))
                getPet().unSummon();
        }
        if (getDuel() != null)
            getDuel().setDuelState(this, DuelState.Interrupted);
    }

    public boolean checkCoupleAction(L2Player target) {
        if (target.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IN_PRIVATE_STORE).addName(target));
            return false;
        }
        if (target.isFishing()) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_FISHING).addName(target));
            return false;
        }
        if (target.isInCombat()) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_COMBAT).addName(target));
            return false;
        }
        if (target.isCursedWeaponEquipped()) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_CURSED_WEAPON_EQUIPED).addName(target));
            return false;
        }
        if (target.isInOlympiadMode()) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_OLYMPIAD).addName(target));
            return false;
        }
        if (target.isOnSiegeField()) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_SIEGE).addName(target));
            return false;
        }
        if (target.isInVehicle() || target.getMountNpcId() != 0) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_VEHICLE_MOUNT_OTHER).addName(target));
            return false;
        }
        if (target.isTeleporting()) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_TELEPORTING).addName(target));
            return false;
        }
        if (target.getTransformation() != 0) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_TRANSFORM).addName(target));
            return false;
        }
        if (target.isDead()) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_DEAD).addName(target));
            return false;
        }
        return true;
    }

    public boolean isCombatFlagEquipped() {
        L2ItemInstance weapon = getActiveWeaponInstance();
        return weapon != null && weapon.getItem().isCombatFlag();
    }

    public boolean isTerritoryFlagEquipped() {
        L2ItemInstance weapon = getActiveWeaponInstance();
        return weapon != null && weapon.getItem().isTerritoryFlag();
    }

    private int _buyListId;

    public void setBuyListId(int listId) {
        _buyListId = listId;
    }

    public int getBuyListId() {
        return _buyListId;
    }

    public boolean checksForShop(boolean RequestManufacture) {
        if (!getPlayerAccess().UseTrade) {
            sendPacket(Msg.THIS_ACCOUNT_CANOT_USE_PRIVATE_STORES);
            return false;
        }

        String tradeBan = getVar("tradeBan");
        if (tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis())) {
            sendMessage("Your trade is banned! Expires: " + (tradeBan.equals("-1") ? "never" : Util.formatTime((Long.parseLong(tradeBan) - System.currentTimeMillis()) / 1000)) + ".");
            return false;
        }

        if (ConfigValue.TradeOnlyPice && !isInPeaceZone()) {
            sendPacket(RequestManufacture ? new SystemMessage(SystemMessage.A_PRIVATE_WORKSHOP_MAY_NOT_BE_OPENED_IN_THIS_AREA) : Msg.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_IN_THIS_AREA);
            return false;
        }

        if (ConfigValue.TradeZoneType.length > 0) {
            boolean trade_ok = false;
            for (ZoneType zoneType : ConfigValue.TradeZoneType) {
                L2Zone zone = getZone(zoneType);
                if (zone != null) {
                    trade_ok = true;
                    break;
                }
            }
            if (!trade_ok) {
                sendPacket(RequestManufacture ? new SystemMessage(SystemMessage.A_PRIVATE_WORKSHOP_MAY_NOT_BE_OPENED_IN_THIS_AREA) : Msg.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_IN_THIS_AREA);
                return false;
            }
        }

        String BLOCK_ZONE = RequestManufacture ? L2Zone.BLOCKED_ACTION_PRIVATE_WORKSHOP : L2Zone.BLOCKED_ACTION_PRIVATE_STORE;
        if (isActionBlocked(BLOCK_ZONE) && !isInStoreMode() && (!ConfigValue.NoTradeOnlyOffline || ConfigValue.NoTradeOnlyOffline && isInOfflineMode()) && (!ConfigValue.TradeOnlyReflectionZone || getReflectionId() != 0)) {
            sendPacket(RequestManufacture ? new SystemMessage(SystemMessage.A_PRIVATE_WORKSHOP_MAY_NOT_BE_OPENED_IN_THIS_AREA) : Msg.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_IN_THIS_AREA);
            return false;
        }

        if (isCastingNow()) {
            sendPacket(Msg.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_WHILE_USING_A_SKILL);
            return false;
        }

        if (isInCombat()) {
            sendPacket(Msg.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            return false;
        }

        if (isOutOfControl() || isActionsDisabled() || isMounted() || isInOlympiadMode() || getDuel() != null || isInEvent() > 0)
            return false;

        if (ConfigValue.TradeOnlyFar && !isInStoreMode()) {
            boolean tradenear = false;
            for (L2Player player : L2World.getAroundPlayers(this, ConfigValue.TradeRadius, 200))
                if (player.isInStoreMode()) {
                    tradenear = true;
                    break;
                }

            if (L2World.getAroundNpc(this, ConfigValue.TradeRadius + 100, 200).size() > 0)
                tradenear = true;

            if (tradenear) {
                sendMessage(new CustomMessage("trade.OtherTradersNear", this));
                return false;
            }
        }
        if (ConfigValue.TradeZoneGiranHarbor) {
            L2Zone zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.other, 500524, false);
            if (!isInZone(zone)) {
                sendMessage("Вы находитесь не в торговой зоне.");
                return false;
            }
        }
        if (ConfigValue.TradeMinLevel > 0) {
            if (getLevel() < ConfigValue.TradeMinLevel) {
                sendMessage("Вы не можете торговать, ваш уровень не достиг " + ConfigValue.TradeMinLevel + "-го.");
                return false;
            }
        }

        return true;
    }

    public int getFame() {
        return _fame;
    }

    public void setFame(int fame, String log) {
        fame = Math.min(ConfigValue.LimitFame, fame);
        if (log != null && !log.isEmpty())
            Log.add(_name + "|" + (fame - _fame) + "|" + fame + "|" + log, "fame");
        if (fame > _fame)
            sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_REPUTATION_SCORE).addNumber(fame - _fame));
        _fame = fame;
        sendChanges();
    }

    public int getVitalityLevel() {
        return ConfigValue.AltVitalityEnabled ? (getNevitBlessing().isBuffActive() ? 4 : _vitalityLevel) : getNevitBlessing().isBuffActive() ? 4 : 0;
    }

    public double getVitality() {
        return ConfigValue.AltVitalityEnabled ? _vitality : 0;
    }

    public void setVitality(double newVitality) {
        if (!ConfigValue.AltVitalityEnabled)
            return;

        newVitality = Math.max(Math.min(newVitality, ConfigValue.VitalityMax), 0);

        if (newVitality >= _vitality || getLevel() >= 10) {
            if (newVitality != _vitality)
                if (newVitality == 0)
                    sendPacket(Msg.VITALITY_IS_FULLY_EXHAUSTED);
                else if (newVitality == ConfigValue.VitalityMax)
                    sendPacket(Msg.YOUR_VITALITY_IS_AT_MAXIMUM);

            _vitality = newVitality;
        }

        int newLevel = 0;
        if (_vitality >= 8500)
            newLevel = 4;
        else if (_vitality >= 6500)
            newLevel = 3;
        else if (_vitality >= 1000)
            newLevel = 2;
        else if (_vitality >= 120)
            newLevel = 1;

        if (_vitalityLevel > newLevel)
            getNevitBlessing().addPoints(ConfigValue.CurrentPointVitalityUp);

        if (_vitalityLevel != newLevel) {
            if (_vitalityLevel != -1) // при ините чара сообщения не шлём
                sendPacket(newLevel < _vitalityLevel ? Msg.VITALITY_HAS_DECREASED : Msg.VITALITY_HAS_INCREASED);
            _vitalityLevel = newLevel;
            sendUserInfo(false);
        }
        if (isLindvior())
            sendPacket(new ExVitalityEffectInfo(this));
    }

    public float getVitalityBonus() {
        return getVitalityLevel() * ConfigValue.AltVitalityPower / 2f;
    }

    public double[] applyVitality(L2MonsterInstance monster, double xp, double sp, double partyVitalityMod) {
        float vitalitybonus = (monster.isRaid() ? 0 : getVitalityBonus());
        float recombonus = (getRecommendation().getRecomExpBonus() + 100) / 100.0F;
        double xpClear = xp * RateService.getRateXp(this) * getRateExp();
        double xpSend = 0.;
        double spClear = sp * RateService.getRateSp(this) * getRateSp();
        double spSend = 0.;

        if (xp > 0) {
            if (!getVarB("NoExp"))
                xp *= RateService.getRateXp(this) * getRateExp() * recombonus + (vitalitybonus * RateService.getRateXpVitality(this));
            else
                xp *= RateService.getRateXp(this) * getRateExp();

            xpSend = xp - (xp - xpClear);
        }
        if (sp > 0) {
            if (!getVarB("NoExp"))
                sp *= RateService.getRateSp(this) * getRateSp() * recombonus + (vitalitybonus * RateService.getRateSpVitality(this));
            else
                sp *= RateService.getRateSp(this) * getRateSp();

            spSend = sp - (sp - spClear);
        }
        if (xp > 0)
            if (!monster.isRaid()) {
                if (!(getVarB("NoExp") && getExp() == Experience.LEVEL[getLevel() + 1] - 1)) {
                    double mod = Experience.baseVitalityMod(getLevel(), monster.getLevel(), monster.getExpReward());
                    if (getNevitBlessing().isBuffActive() || getEffectList().getEffectByType(EffectType.Vitality) != null)
                        mod *= -1;
                    setVitality(getVitality() - mod * partyVitalityMod);
                }
            } else
                setVitality(getVitality() + ConfigValue.AltVitalityRaidBonus);

        if (!isInPeaceZone()) {
            getNevitBlessing().startBonus();
            getRecommendation().startRecBonus();

            if (getLevel() > monster.getLevel() + 9)
                return new double[]{xp, sp, xpSend, spSend};
            // int nevitPoints = Math.round(((monster.getExpReward() / (monster.getLevel() * monster.getLevel())) * 100) / 20);
            int nevitPoints = (int) Math.round(Math.sqrt(monster.getExpReward() / (monster.getLevel() * monster.getLevel()))); // Так на много лучше получается...тестил на дракосах на РПГ, из 20 убитых я даже 1% не апнул...а у нас же было из двух)))
            getNevitBlessing().addPoints(nevitPoints);
        }

        return new double[]{xp, sp, xpSend, spSend};
    }

    private int _incorrectValidateCount = 0;

    public int getIncorrectValidateCount() {
        return _incorrectValidateCount;
    }

    public void setIncorrectValidateCount(int count) {
        _incorrectValidateCount = count;
    }

    public int getExpandInventory() {
        return _expandInventory;
    }

    public void setExpandInventory(int inventory) {
        _expandInventory = inventory;
    }

    public int getExpandWarehouse() {
        return _expandWarehouse;
    }

    public void setExpandWarehouse(int warehouse) {
        _expandWarehouse = warehouse;
    }

    public void enterMovieMode() {
        if (isInMovie())
            return;

        setIsInMovie(true);
        setTarget(null);
        stopMove();
        setIsInvul(true);
        //p_block_move(true, null);
        sendPacket(new CameraMode(1));
    }

    public void leaveMovieMode() {
        setIsInMovie(false);
        if (!isGM())
            setIsInvul(false);
        //p_block_move(false, null);
        sendPacket(new CameraMode(0));
        broadcastUserInfo(true);
    }

    private int _movieId = 0;
    private boolean _isInMovie;

    public void setMovieId(int id) {
        _movieId = id;
    }

    public int getMovieId() {
        return _movieId;
    }

    public boolean isInMovie() {
        return _isInMovie;
    }

    public void setIsInMovie(boolean state) {
        _isInMovie = state;
    }

    public void showQuestMovie(int movieId) {
        if (isInMovie()) //already in movie
            return;

        sendActionFailed();
        setTarget(null);
        stopMove();
        setMovieId(movieId);
        setIsInMovie(true);
        sendPacket(new ExStartScenePlayer(movieId));
    }

    public void setAutoLoot(boolean enable) {
        if (ConfigValue.AutoLootIndividual) {
            AutoLoot = enable;
            setVar("AutoLoot", String.valueOf(enable));
        }
    }

    public void setAutoLootSpecial(boolean enable) {
        if (ConfigValue.AutoLootIndividual) {
            AutoLootSpecial = enable;
            setVar("AutoLootSpecial", String.valueOf(enable));
        }
    }

    public void setAutoLootHerbs(boolean enable) {
        if (ConfigValue.AutoLootIndividual) {
            AutoLootHerbs = enable;
            setVar("AutoLootHerbs", String.valueOf(enable));
        }
    }

    public boolean isAutoLootEnabled() {
        return AutoLoot;
    }

    public boolean isAutoLootHerbsEnabled() {
        return AutoLootHerbs;
    }

    public boolean isAutoLootSpecialEnabled() {
        return AutoLootSpecial;
    }

    public final void reName(String name, boolean saveToDB) {
        setName(name);
        if (saveToDB)
            PlayerData.getInstance().saveNameToDB(this);
        Olympiad.changeNobleName(getObjectId(), name);
        broadcastUserInfo(true);
    }

    public final void reName(String name) {
        reName(name, false);
    }

    @Override
    public L2Player getPlayer() {
        return this;
    }

    private GArray<String> getStoredBypasses(boolean bbs, boolean special) {
        if (bbs) {
            if (bypasses_bbs == null)
                bypasses_bbs = new GArray<String>();
            return bypasses_bbs;
        }
        if (special) {
            if (bypasses_special == null)
                bypasses_special = new GArray<String>();
            return bypasses_special;
        }
        if (bypasses == null)
            bypasses = new GArray<String>();
        return bypasses;
    }

    public void cleanBypasses(boolean bbs, boolean special) {
        GArray<String> bypassStorage = getStoredBypasses(bbs, special);
        synchronized (bypassStorage) {
            bypassStorage.clear();
        }
    }

    public String encodeBypasses(String htmlCode, boolean bbs, boolean special) {
        GArray<String> bypassStorage = getStoredBypasses(bbs, special);
        synchronized (bypassStorage) {
            return BypassManager.encode(htmlCode, bypassStorage, bbs, special);
        }
    }

    public DecodedBypass decodeBypass(String bypass, boolean special) {
        BypassType bpType = BypassManager.getBypassType(bypass);
        boolean bbs = bpType == BypassType.ENCODED_BBS || bpType == BypassType.SIMPLE_BBS;
        GArray<String> bypassStorage = getStoredBypasses(bbs, special);
        //System.out.println("BypassType: "+bpType);
        if (bpType == BypassType.ENCODED || bpType == BypassType.ENCODED_BBS)
            return BypassManager.decode(bypass, bypassStorage, bbs, this);
        else if (bpType == BypassType.SIMPLE && !bypass.startsWith("_bbsscripts") && !bypass.startsWith("scripts"))
            return new DecodedBypass(bypass, false).trim();
        else if (bpType == BypassType.SIMPLE_BBS)
            return new DecodedBypass(bypass, true).trim();
        increaseByPassFail(bypass);
        //_log.warning("Direct access to bypass: '" + bypass + "' / Player: " + getName());
        return null;
    }

    /**
     * Сброс реюза всех скилов персонажа.
     */
    public void resetSkillsReuse() {
        getSkillReuseTimeStamps().clear();
        sendPacket(new SkillCoolTime(this));
    }

    private int _territorySide = -1;

    public void setTerritorySiege(int side) {
        _territorySide = side;
    }

    public int getTerritorySiege() {
        L2Clan clan = getClan();
        if (clan != null && clan.getTerritorySiege() > -1)
            return clan.getTerritorySiege();
        if (_territorySide > -1)
            return _territorySide;
        return -1;
    }

    private int siegeSide = 0;

    public boolean isRegisteredOnThisSiegeField(int value) {
        return siegeSide == value || (siegeSide >= 81 && siegeSide <= 89);
    }

    public int getSiegeSide() {
        return siegeSide;
    }

    public void setSiegeSide(int value) {
        siegeSide = value;
    }

    private final List<L2Player> _snoopListener = new ArrayList<L2Player>();
    private final List<L2Player> _snoopedPlayer = new ArrayList<L2Player>();

    public void broadcastSnoop(final int type, final String name, final String _text, final int speakerId) {
        if (_snoopListener.size() > 0) {
            final Snoop sn = new Snoop(getObjectId(), getName(), type, speakerId, name, _text);
            for (final L2Player pci : _snoopListener)
                if (pci != null)
                    pci.sendPacket(sn);
        }
    }

    public void addSnooper(final L2Player pci) {
        if (!_snoopListener.contains(pci))
            _snoopListener.add(pci);
    }

    public void removeSnooper(final L2Player pci) {
        _snoopListener.remove(pci);
    }

    public void addSnooped(final L2Player pci) {
        if (!_snoopedPlayer.contains(pci))
            _snoopedPlayer.add(pci);
    }

    public void removeSnooped(final L2Player pci) {
        _snoopedPlayer.remove(pci);
    }

    public long quest_last_reward_time = 0;

    public IntObjectMap<String> _postFriends;

    public IntObjectMap<String> getPostFriends() {
        return _postFriends;
    }

    public String _hwid = "NULL";

    public String getHWIDs() {
        return _hwid;
    }

    private int _points = 0;

    public int getPoints() {
        return _points;
    }

    public int getPoint(boolean set_game) {
        _points = mysql.simple_get_int("points", set_game ? "market_point" : "accounts", "login='" + getAccountName() + "'", !set_game);
        return _points;
    }

    public void onTeleported() {
        if (getPet() != null)
            getPet().teleportToOwner();

        if (isFakeDeath())
            breakFakeDeath();

        if (isInVehicle())
            setXYZInvisible(getVehicle().getLoc());

        // 15 секунд после телепорта на персонажа не агрятся мобы
        setNonAggroTime(System.currentTimeMillis() + ConfigValue.NonAggroTime * 1000);

        spawnMe();

        setLastClientPosition(getLoc());
        setLastServerPosition(getLoc());

        setIsTeleporting(0);

        // На всякий случай оставлю это гавно...
        if (_isPendingRevive)
            doRevive();

        if (getTrainedBeast() != null) {
            for (L2TamedBeastInstance tamedBeast : getTrainedBeast())
                tamedBeast.deleteMe();
            getTrainedBeast().clear();
        }

        checkWaterState();

        if (!isLogoutStarted() && HandysBlockCheckerManager.isRegistered(this)) {
            if (BlockCheckerEngine._teleport.containsKey(this)) {
                if (!BlockCheckerEngine._teleport.get(this)) {
                    HandyBlockGameClear();
                } else {
                    BlockCheckerEngine._teleport.remove(this);
                    BlockCheckerEngine._teleport.put(this, false);
                }
            }
        }

        sendActionFailed();

        // боты очень редко, но вызывают ошибку...
        try {
            getAI().notifyEvent(CtrlEvent.EVT_TELEPORTED);
        } catch (Exception e) {
        }
        sendUserInfo(true);
        //broadcastRelationChanged();
    }

    private int _lectureMark = 0;

    public int getLectureMark() {
        return _lectureMark;
    }

    public void setLectureMark(int lectureMark) {
        _lectureMark = lectureMark;
    }

    public void setItemBayTime(int item_id, long time) {
        _itemBay.put(item_id, time);
    }

    public boolean getItemBayTime(int item_id) {
        if (_itemBay.containsKey(item_id)) {
            long item_time = _itemBay.get(item_id);
            long time = System.currentTimeMillis();
            if (item_time <= time)
                _itemBay.remove(item_id);
            else
                return true;
        }
        return false;
    }

    private AtomicBoolean isActive = new AtomicBoolean();

    public boolean isActive() {
        return isActive.get();
    }

    public void setActive() {
        _last_active = System.currentTimeMillis();
        setNonAggroTime(0);

        if (isActive.getAndSet(true))
            return;

        onActive();
    }

    private void onActive() {
        setNonAggroTime(0);
        sendPacket(new SystemMessage(SystemMessage.YOU_ARE_NO_LONGER_PROTECTED_FROM_AGGRESSIVE_MONSTERS));
        PlayerData.getInstance().restoreSummon(this);
    }

    public void tryEqupUneqipItem(L2ItemInstance item) {
        if (item == null || getObjectId() != item.getOwnerId() || (item.getLocation() != ItemLocation.INVENTORY && item.getLocation() != ItemLocation.PAPERDOLL && item.getLocation() != ItemLocation.PET && item.getLocation() != ItemLocation.PET_PAPERDOLL))
            return;

        if (item.isEquipped()) {
            getInventory().unEquipItemInBodySlotAndNotify(item.getBodyPart(), item, true);
            return;
        }

        getInventory().equipItem(item, true);
        if (!item.isEquipped())
            return;

        SystemMessage sm;
        if (item.getEnchantLevel() > 0) {
            sm = new SystemMessage(SystemMessage.EQUIPPED__S1_S2);
            sm.addNumber(item.getEnchantLevel());
            sm.addItemName(item.getItemId());
        } else
            sm = new SystemMessage(SystemMessage.YOU_HAVE_EQUIPPED_YOUR_S1).addItemName(item.getItemId());
        sendPacket(sm);
        validateItemExpertisePenalties(false, item.getItem() instanceof L2Armor, item.getItem() instanceof L2Weapon);

        if (item.getItem().getType2() == L2Item.TYPE2_ACCESSORY || item.getItem().isTalisman()) {
            sendUserInfo(true);
            // TODO убрать, починив предварительно отображение бижы
            sendPacket(new ItemList(this, false));
        } else
            broadcastUserInfo(true);
    }

    public static void addClassQuest(ClassId c, Quest quest) {
        _classQuests.put(c, quest);
    }

    public Quest getClassQuest(ClassId c) {
        return _classQuests.get(c);
    }

    public static Map<ClassId, Quest> getAllClassQuests() {
        return _classQuests;
    }

    public static void addBreakQuest(Quest q) {
        _breakQuests.add(q);
    }

    public static List<Quest> getBreakQuests() {
        return _breakQuests;
    }

    public void startPcBangPointsTask() {
        if (ConfigValue.AltPcBangPointsEnabled && ConfigValue.AltPcBangPointsDelay > 0 && _pcCafePointsTask == null)
            _pcCafePointsTask = LazyPrecisionTaskManager.getInstance().addPCCafePointsTask(this);
        if (getAttainment() != null && !getVarB("Attainment13") && _AttainmentTask == null)
            _AttainmentTask = AttainmentTaskManager.getInstance().addPCAttainmentTask(this);

    }

    public void stopPcBangPointsTask() {
        if (_pcCafePointsTask != null)
            _pcCafePointsTask.cancel(false);
        _pcCafePointsTask = null;

        stopAttainmentTask();
    }

    public void stopAttainmentTask() {
        if (_AttainmentTask != null) {
            AttainmentTaskManager.getInstance().dellPCAttainmentTask(this);
            _AttainmentTask.cancel(false);
        }
        _AttainmentTask = null;
    }

    public Map<Integer, Long> getInstanceReuses() {
        Map<Integer, Long> _instancesReuses = new ConcurrentHashMap<Integer, Long>();
        InstancedZoneManager ilm = InstancedZoneManager.getInstance();
        for (Integer id : ilm.getIds()) {
            long limit = ilm.getTimeToNextEnterInstance(id, this);
            if (limit > 0)
                _instancesReuses.put(id, limit);
        }
        return _instancesReuses;
    }

    @SuppressWarnings("unchecked")
    @Override
    public HardReference<L2Player> getRef() {
        return (HardReference<L2Player>) super.getRef();
    }

    private boolean _agathionResAvailable = false;

    public boolean isAgathionResAvailable() {
        return _agathionResAvailable;
    }

    public void setAgathionRes(boolean val) {
        _agathionResAvailable = val;
    }

    private String[] _accLock = null;

    public String[] getAccLock() {
        return _accLock;
    }

    public void clearAccLock() {
        _accLock = null;
        PlayerData.getInstance().clearHwidLock(this);
        //unsetVar("AccHWIDLock");
    }

    public void setAccLock(String[] accLock) {
        _accLock = accLock;
        if (_accLock != null && _accLock.length == 0)
            _accLock = null;
    }

    public void addAccLock(String accLock) {
        if (_accLock == null)
            _accLock = new String[0];
        _accLock = ArrayUtils.add(_accLock, accLock);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < _accLock.length; i++) {
            sb.append(_accLock[i]);
            if (_accLock.length - 1 > i)
                sb.append(";");
        }
        Log.add(getAccountName() + "|" + getName() + "|ADD|" + accLock + "", "char_hwid");
        PlayerData.getInstance().editHwidLock(this, sb.toString());
        //setVar("AccHWIDLock", sb.toString());
    }

    public void removeAccLock(String accLock) {
        _accLock = ArrayUtils.removes(_accLock, accLock);
        if (_accLock == null || _accLock.length == 0) {
            _accLock = null;
            PlayerData.getInstance().clearHwidLock(this);
            //unsetVar("AccHWIDLock");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < _accLock.length; i++) {
                sb.append(_accLock[i]);
                if (_accLock.length - 1 > i)
                    sb.append(";");
            }
            PlayerData.getInstance().editHwidLock(this, sb.toString());
            //setVar("AccHWIDLock", sb.toString());
        }
        Log.add(getAccountName() + "|" + getName() + "|DELETE|" + accLock + "", "char_hwid");
    }

    private int restart_point_type = 0;

    public int isRestartPoint() {
        return restart_point_type;
    }

    // пусть будет, вломы всем ивенты править...
    public void setRestartPoint(boolean value) {
        restart_point_type = value ? 0 : 2;
    }

    /**
     * -1 - Разрешено возрождение, запрещен рес чара.
     * 0 - Разрешено возрождение и рес чара
     * 1 - Запрещено возрождение, но разрешен рес чара
     * 2 - Запрещено возрождение и рес чара
     **/
    public void setRestartPoint(int value) {
        restart_point_type = value;
    }

    private String _lastFile = "data/html/npcdefault.htm";

    public void setLastFile(String lf) {
        _lastFile = lf;
    }

    public String getLastFile() {
        return _lastFile;
    }

    public boolean getAndSetLastItemAuctionRequest() {
        if (_lastItemAuctionInfoRequest + 2000L < System.currentTimeMillis()) {
            _lastItemAuctionInfoRequest = System.currentTimeMillis();
            return true;
        } else {
            _lastItemAuctionInfoRequest = System.currentTimeMillis();
            return false;
        }
    }

    private long _talkNoMove = 0;

    public void startNoMove() {
        _talkNoMove = System.currentTimeMillis() + 1600;
    }

    public boolean isStartNoMove() {
        if (_talkNoMove >= System.currentTimeMillis())
            return true;
        return false;
    }

    public boolean isCreateCommandChannelWithItem() {
        return _isCreateCommandChannelWithItem;
    }

    public void setCreateCommandChannelWithItem(boolean val) {
        _isCreateCommandChannelWithItem = val;
    }

    public ScheduledFuture<?> _enchantSucer; // Таск на обнуление щетчика заточки.
    public long _enchantSucerTime = 0; // Будем использовать, что бы умники не сбрасывали счетчик релогом чара...
    public int _enchantCount = 0; // Количество попыток заточить вещи.
    public boolean _enchantDisable = false;
    public String _setImage = "";

    public ScheduledFuture<?> _bot_check;
    public ScheduledFuture<?> _bot_kick;
    public ScheduledFuture<?> _test_task;

    public void increasEnchantCount() {
        if (_enchantCount == 0) {
            startEnchantTask(ConfigValue.EnchantProtectTime * 1000);
            _enchantSucerTime = System.currentTimeMillis();
        }
        _enchantCount++;
        if (_enchantCount >= ConfigValue.EnchantProtectCount) {
            _enchantDisable = true;
            switch (ConfigValue.EnchantProtectType) {
                case 0:
                    ProtectFunction.getInstance().getEnchantProtect(this, true);
                    break;
                case 1:
                    ProtectFunction.getInstance().getEnchantProtect(this, false);
                    break;
            }
        }
    }

    public void setEnableEnchant() {
        _enchantDisable = false;
    }

    public boolean isEnchantDisable(L2ItemInstance itemToEnchant, L2ItemInstance scroll) {
        if (_enchantDisable) {
            switch (ConfigValue.EnchantProtectPunishment) {
                case 0: // Просто выбиваем чара и закрываем клиент.
                    logout(false, false, false, true);
                    break;
                case 1: // Просто выбиваем чара и закрываем клиент.
                    logout(false, false, false, true);
                    break;
            }
            setEnchantScroll(null);
            sendPacket(EnchantResult.FAILED_NO_CRYSTALS);
            _enchantDisable = false;
            return true;
        }
        return false;
    }

    public CustomMessage getOnlineTime(L2Player player) {
        int total = (int) (player.getOnlineTime() / 1000);
        int days = total / (60 * 60 * 24);
        int hours = total / (60 * 60) % 24;

        if (days >= 1)
            return new CustomMessage("l2cccp.gameserver.model.Player.getOnlineTime.day", player).addNumber(days).addNumber(hours);
        else
            return new CustomMessage("l2cccp.gameserver.model.Player.getOnlineTime.hour", player).addNumber(hours);
    }

    public boolean isTransformLalka() {
        return getTransformation() == 312 || getTransformation() == 313 || getTransformation() == 314 || getTransformation() == 315 || getTransformation() == 316 || getTransformation() == 317 || getTransformation() == 318;
    }

    public void startEnchantTask(long time) {
        _enchantSucer = ThreadPoolManager.getInstance().schedule(new EnchantResetCount(this), time);
    }

    public void startBotCheck(long time) {
        if (ConfigValue.BotProtectEnable) {
            if (_bot_check != null)
                _bot_check.cancel(false);

            setVar("startBotCheck", String.valueOf(System.currentTimeMillis()));
            setVar("startBotCheckTime", String.valueOf(time));
            _bot_check = ThreadPoolManager.getInstance().schedule(new BotCheck(this, false), time * 1000);
        }
    }

    public void botCheck() {
        if (getReflectionId() == 0 && getPvpFlag() <= 0 && !isInOlympiadMode() && getOlympiadGame() == null && getTeam() <= 0 && !isInOfflineMode() && !isInStoreMode() && !isGM() && !isInPeaceZone() && !isInCombatZone() && !hasBonus() && (!ConfigValue.BotProtectEnableZoneCheck || ZoneManager.getInstance().checkIfInZone(ZoneType.zone_check_bot, this)) && !isInZone(epic)) {
            if (_bot_kick != null)
                _bot_kick.cancel(false);

            _bot_kick = ThreadPoolManager.getInstance().schedule(new BotCheck(this, true), ConfigValue.BotProtectRequestTime * 1000);
            ProtectFunction.getInstance().getBotProtect(this);
        } else if (!isInOfflineMode() && !isGM())
            startBotCheck(Rnd.get(ConfigValue.BotProtectTimeMin, ConfigValue.BotProtectTimeMax));
    }

    @Override
    public int class_id() {
        return getClassId().getId();
    }

    // ----- Поебень ------
    public int last_freeway_id = -1;
    public int last_node_id = 0;

    public boolean hasBonus() {
        return getNetConnection() != null && getNetConnection().getBonus() > 1;
    }

    private boolean _block_use_item = false;

    public boolean isBlockUseItem() {
        return _block_use_item || isStunned() || isActionBlock();
    }

    public void setBlockUseItem(boolean value) {
        _block_use_item = value;
    }

    private boolean _communityBlock = false;

    public boolean isCommunityBlock() {
        return _communityBlock;
    }

    public void setCommunityBlock(boolean value) {
        _communityBlock = value;
    }

    private boolean _isEventReg = false;

    public boolean isEventReg() {
        return _isEventReg;
    }

    public void setEventReg(boolean value) {
        _isEventReg = value;
    }

    public int _catalystId = 0;

    private String _changePasswordResult = "<font color=\"33CC33\">Заполните все поля</font>";//TODO: перенести в CustomMessage

    public String setPasswordResult(String result) {
        _changePasswordResult = result;
        return _changePasswordResult;
    }

    public String getPasswordResult() {
        return _changePasswordResult;
    }

    // Переменная и методы для сервиса покупки очков клана в дп.
    private int _pointToBuy = 0;

    public int setPointToBuy(int result) {
        _pointToBuy = result;
        return _pointToBuy;
    }

    public int getPointToBuy() {
        return _pointToBuy;
    }

    public boolean hasSetFame() {
        return _set_fame < System.currentTimeMillis();
    }

    public void setMyFame() {
        _set_fame = System.currentTimeMillis() + 60 * 3 * 1000;
        setVar("set_fame", String.valueOf(_set_fame));
        ThreadPoolManager.getInstance().schedule(new SetMyFame(), 61 * 3 * 1000);
    }

    public class SetMyFame extends l2open.common.RunnableImpl {
        public void runImpl() {
            broadcastUserInfo(true);
        }
    }

    private int byPassFail = 0;

    private void increaseByPassFail(String text) {
        byPassFail++;
        if (byPassFail >= ConfigValue.ByPassFailCount) {
            byPassFail = 0;
            switch (ConfigValue.ByPassFailPunishment) {
                case 0: // Тюрьма
                    Log.add("Char: " + getName() + ", Punishment: Jail after " + ConfigValue.ByPassFailPunishmentTime + " min, bypass: '" + text + "'", "bypass_fail");
                    setVar("jailedFrom", getX() + ";" + getY() + ";" + getZ() + ";" + getReflection().getId());
                    _unjailTask = ThreadPoolManager.getInstance().schedule(new TeleportTask(this, new Location(getX(), getY(), getZ()), 0), ConfigValue.ByPassFailPunishmentTime * 60000);
                    setVar("jailed", ConfigValue.ByPassFailPunishmentTime * 60000 + ";" + (System.currentTimeMillis() / 1000));
                    teleToLocation(-114648, -249384, -2984, -3);
                    break;
                case 1: // Тюрьма + Кик
                    Log.add("Char: " + getName() + ", Punishment: Kick + Jail after " + ConfigValue.ByPassFailPunishmentTime + " min, bypass: '" + text + "'", "bypass_fail");
                    setVar("jailedFrom", getX() + ";" + getY() + ";" + getZ() + ";" + getReflection().getId());
                    _unjailTask = ThreadPoolManager.getInstance().schedule(new TeleportTask(this, new Location(getX(), getY(), getZ()), 0), ConfigValue.ByPassFailPunishmentTime * 60000);
                    setVar("jailed", ConfigValue.ByPassFailPunishmentTime * 60000 + ";" + (System.currentTimeMillis() / 1000));
                    teleToLocation(-114648, -249384, -2984, -3);
                    logout(false, false, true, true);
                    break;
                case 2: // Бан
                    Log.add("Char: " + getName() + ", Punishment: Ban after " + ConfigValue.ByPassFailPunishmentTime + " day, bypass: '" + text + "'", "bypass_fail");
                    setAccessLevel(-100);
                    AutoBan.Banned(this, ConfigValue.ByPassFailPunishmentTime, "Fail Bypass", "ByPassFail");
                    logout(false, false, true, true);
                    break;
                case 3: // Просто Кик
                    Log.add("Char: " + getName() + ", Punishment: Kick, bypass: '" + text + "'", "bypass_fail");
                    logout(false, false, true, true);
                    break;
                default:
                    // Ничего не делаем, просто игнорим...
                    Log.add("Char: " + getName() + ", Punishment: None, bypass: '" + text + "'", "bypass_fail");
                    break;
            }
        }
    }

    public boolean canTransformation(boolean useSummon, L2Skill skill) {
        if (!can_transform || isCombatFlagEquipped() || isTerritoryFlagEquipped()) {
            sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill.getId(), skill.getLevel()));
            return false;
        } else if (getTransformation() != 0 && skill.getId() != L2Skill.SKILL_TRANSFOR_DISPELL) {
            // Для всех скилов кроме Transform Dispel
            sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
            return false;
        }
        // Нельзя использовать летающую трансформу на территории Aden, или слишком высоко/низко, или при вызванном пете/саммоне, или в инстансе
        else if ((skill.getId() == L2Skill.SKILL_FINAL_FLYING_FORM || skill.getId() == L2Skill.SKILL_AURA_BIRD_FALCON || skill.getId() == L2Skill.SKILL_AURA_BIRD_OWL) && (getX() > -166168 || getZ() <= 0 || getZ() >= 6000 || getPet() != null || getReflection().getId() != 0)) {
            sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill.getId(), skill.getLevel()));
            return false;
        }
        // Нельзя отменять летающую трансформу слишком высоко над землей
        else if (isInFlyingTransform() && skill.getId() == L2Skill.SKILL_TRANSFOR_DISPELL && Math.abs(getZ() - getLoc().correctGeoZ().z) > 333) {
            sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill.getId(), skill.getLevel()));
            return false;
        }
        // Нельзя отменять трансформацию хэнди гейм.
        else if ((getTransformation() == 121 || getTransformation() == 122) && skill.getId() == L2Skill.SKILL_TRANSFOR_DISPELL) {
            sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill.getId(), skill.getLevel()));
            return false;
        } else if (isInWater()) {
            sendPacket(Msg.YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER);
            return false;
        } else if (isRiding()) {
            sendPacket(Msg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_PET);
            return false;
        }
        // Для трансформации у игрока не должно быть активировано умение Mystic Immunity.
        for (L2Effect effect : getEffectList().getAllEffects())
            if (effect != null && effect.getSkill().getId() == 1411/*effect.getEffectType() == EffectType.BuffImmunity*/) {
                sendPacket(Msg.YOU_CANNOT_POLYMORPH_WHILE_UNDER_THE_EFFECT_OF_A_SPECIAL_SKILL);
                return false;
            }
        if (isInVehicle()) {
            sendPacket(Msg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT);
            return false;
        } else if (useSummon && (getPet() == null || !getPet().isSummon() || getPet().isDead())) {
            sendPacket(Msg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
            return false;
        } else if (getPet() != null && getPet().isPet() && skill.getId() != L2Skill.SKILL_TRANSFOR_DISPELL && !skill.isBaseTransformation()) {
            sendPacket(Msg.YOU_CANNOT_POLYMORPH_WHEN_YOU_HAVE_SUMMONED_A_SERVITOR_PET);
            return false;
        }
        return true;
    }

    public long _set_sub = 0;

    public boolean hasNextSub() {
        return _set_sub < System.currentTimeMillis();
    }

    public boolean _roomDone = false;

    public void roomDone() {
        setVar("canEnterBeleth", String.valueOf(System.currentTimeMillis() + 43200000), System.currentTimeMillis() + 43200000);
        _roomDone = true;
    }

    public boolean canEnterBeleth() {
        if (getTeam() > 0)
            return true;
        String text = "";
        if (!_roomDone && getReflectionId() == 0 && !isGM())
            switch (ConfigValue.BelethBugPunishment) {
                case 0: // Тюрьма
                    Log.add("Char: " + getName() + ", Punishment: Jail after " + ConfigValue.ByPassFailPunishmentTime + " min: '" + text + "'", "beleth_bug");
                    setVar("jailedFrom", getX() + ";" + getY() + ";" + getZ() + ";" + getReflection().getId());
                    _unjailTask = ThreadPoolManager.getInstance().schedule(new TeleportTask(this, new Location(getX(), getY(), getZ()), 0), ConfigValue.ByPassFailPunishmentTime * 60000);
                    setVar("jailed", ConfigValue.ByPassFailPunishmentTime * 60000 + ";" + (System.currentTimeMillis() / 1000));
                    teleToLocation(-114648, -249384, -2984, -3);
                    break;
                case 1: // Тюрьма + Кик
                    Log.add("Char: " + getName() + ", Punishment: Kick + Jail after " + ConfigValue.ByPassFailPunishmentTime + " min: '" + text + "'", "beleth_bug");
                    setVar("jailedFrom", getX() + ";" + getY() + ";" + getZ() + ";" + getReflection().getId());
                    _unjailTask = ThreadPoolManager.getInstance().schedule(new TeleportTask(this, new Location(getX(), getY(), getZ()), 0), ConfigValue.ByPassFailPunishmentTime * 60000);
                    setVar("jailed", ConfigValue.ByPassFailPunishmentTime * 60000 + ";" + (System.currentTimeMillis() / 1000));
                    teleToLocation(-114648, -249384, -2984, -3);
                    logout(false, false, true, true);
                    break;
                case 2: // Бан
                    teleToLocation(83432, 148712, -3408);
                    Log.add("Char: " + getName() + ", Punishment: Ban after " + ConfigValue.ByPassFailPunishmentTime + " day: '" + text + "'", "beleth_bug");
                    setAccessLevel(-100);
                    AutoBan.Banned(this, ConfigValue.ByPassFailPunishmentTime, "Fail Bypass", "ByPassFail");
                    logout(false, false, true, true);
                    break;
                case 3: // Просто Кик
                    teleToLocation(83432, 148712, -3408);
                    Log.add("Char: " + getName() + ", Punishment: Kick: '" + text + "'", "beleth_bug");
                    logout(false, false, true, true);
                    break;
                case 4: // Разрешаем ити дальше...
                    return true;
                default:
                    teleToLocation(83432, 148712, -3408);
                    // Ничего не делаем, просто игнорим...
                    Log.add("Char: " + getName() + ", Punishment: None: '" + text + "'", "beleth_bug");
                    break;
            }
        return _roomDone || getReflectionId() != 0 || isGM();
    }

    long resTime = 0;

    public void setResTime() {
        resTime = System.currentTimeMillis();
    }

    public long getResTime() {
        return resTime;
    }


    @Override
    public int getAgathionEnergy() {
        L2ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
        return item == null ? 0 : item.getAgathionEnergy();
    }

    @Override
    public void setAgathionEnergy(int val) {
        L2ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
        if (item == null)
            return;
        item._storedInDb = false;
        item.setAgathionEnergy(val);
        PlayerData.getInstance().updateInDb(item);

        sendPacket(new ExBR_AgathionEnergyInfoPacket(1, item));
    }

    public boolean p_transfer_stats = false;
    public boolean can_create_party = true;
    public boolean can_transform = true;
    public boolean is_dv = false;
    public long chat_time = 0;
    public String chat_text = "";
    public String l2question = "";
    public String l2answer = "";
    public String password = "";

    public float getAltBonus() {
        if (getAttainment() != null && getAttainment().checkAttainment10())
            return getHwidBonus() * ConfigValue.Attainment10_bonus;
        return getHwidBonus();
    }

    public float getHwidBonus() {
        if (getAccLock() != null)
            return ConfigValue.AddHwidBonus;
        return 1f;
    }

    public void hwid_confirm(String answer_confirm) {
        try {
            answer_confirm = answer_confirm.trim();
            if (!answer_confirm.isEmpty()) {
                if (!l2answer.isEmpty() && answer_confirm.equals(l2answer)) {
                    sendMessage("HWID добавлен, приятной игры!");
                    is_block = false;
                    addAccLock(getHWIDs());
                    sendActionFailed();
                    return;
                }
            }
        } catch (Exception e) {
        }
		/*NpcHtmlMessage block_msg = new NpcHtmlMessage(5);
		block_msg.setHtml(Files.read("data/scripts/services/hwid_confirm.htm", this).replace("<?question?>", l2question));
		sendPacket(block_msg);*/
        logout(false, false, false, true);
    }

    protected Map<Long, String> _chat = new ConcurrentHashMap<Long, String>(30);
    protected Map<Long, String> _chat_tell = new ConcurrentHashMap<Long, String>(30);
    protected Map<Long, String> _mail = new ConcurrentHashMap<Long, String>(10);

    public boolean addChat(String chat, int type) {
        Map<Long, String> _text = null;

        int ChatFilterTextLength = -1;
        int ChatFilterTypeWith = -1;
        int ChatFilterPercentStartsWith = -1;
        int ChatFilterPercentEndsWith = -1;
        int ChatFilterCountWith = -1;
        int ChatFilterCountStartsWith = -1;
        int ChatFilterTextTime = -1;
        int ChatFilterTextCount = -1;
        int ChatFilterTextSizeClear = 1;

        switch (type) {
            case 0:
                _text = _chat;
                ChatFilterTextLength = ConfigValue.ChatFilterTextLength;
                ChatFilterTypeWith = ConfigValue.ChatFilterTypeWith;
                ChatFilterPercentStartsWith = ConfigValue.ChatFilterPercentStartsWith;
                ChatFilterPercentEndsWith = ConfigValue.ChatFilterPercentEndsWith;
                ChatFilterCountWith = ConfigValue.ChatFilterCountWith;
                ChatFilterCountStartsWith = ConfigValue.ChatFilterCountStartsWith;
                ChatFilterTextTime = ConfigValue.ChatFilterTextTime * 1000;
                ChatFilterTextCount = ConfigValue.ChatFilterTextCount;
                ChatFilterTextSizeClear = ConfigValue.ChatFilterTextSizeClear;
                break;
            case 1:
                _text = _mail;
                ChatFilterTextLength = ConfigValue.MailFilterTextLength;
                ChatFilterTypeWith = ConfigValue.MailFilterTypeWith;
                ChatFilterPercentStartsWith = ConfigValue.MailFilterPercentStartsWith;
                ChatFilterPercentEndsWith = ConfigValue.MailFilterPercentEndsWith;
                ChatFilterCountWith = ConfigValue.MailFilterCountWith;
                ChatFilterCountStartsWith = ConfigValue.MailFilterCountStartsWith;
                ChatFilterTextTime = ConfigValue.MailFilterTextTime * 1000;
                ChatFilterTextCount = ConfigValue.MailFilterTextCount;
                ChatFilterTextSizeClear = ConfigValue.MailFilterTextSizeClear;
                break;
            case 2:
                _text = _chat_tell;
                ChatFilterTextLength = ConfigValue.ChatTellFilterTextLength;
                ChatFilterTypeWith = ConfigValue.ChatTellFilterTypeWith;
                ChatFilterPercentStartsWith = ConfigValue.ChatTellFilterPercentStartsWith;
                ChatFilterPercentEndsWith = ConfigValue.ChatTellFilterPercentEndsWith;
                ChatFilterCountWith = ConfigValue.ChatTellFilterCountWith;
                ChatFilterCountStartsWith = ConfigValue.ChatTellFilterCountStartsWith;
                ChatFilterTextTime = ConfigValue.ChatTellFilterTextTime * 1000;
                ChatFilterTextCount = ConfigValue.ChatTellFilterTextCount;
                ChatFilterTextSizeClear = ConfigValue.ChatTellFilterTextSizeClear;
                break;
        }

        String chat_compare;
        int size = chat.length();
        if (size >= ChatFilterTextLength) {
            long time = System.currentTimeMillis();
            int start;
            int end;

            if (ChatFilterTypeWith == 0) {
                start = (int) (size / 100f * ChatFilterPercentStartsWith);
                end = (int) (size - size / 100f * ChatFilterPercentEndsWith);

                chat_compare = chat.substring(start, end).toLowerCase();
            } else if (ChatFilterTypeWith == 1) {
                end = Math.min(ChatFilterCountWith + ChatFilterCountStartsWith, size);

                chat_compare = chat.substring(ChatFilterCountStartsWith, end).toLowerCase();
            } else
                chat_compare = chat.toLowerCase();

            for (long t : _text.keySet()) {
                if (t + ChatFilterTextTime < time) {
                    _text.remove(t);
                    //_log.info("t2="+t+" type="+type);
                } else if ((time - t) < ChatFilterTextTime && _text.get(t).contains(chat_compare)) {
                    int count = 0;
                    for (long t2 : _text.keySet())
                        if ((time - t2) < ChatFilterTextTime && _text.get(t2).contains(chat_compare))
                            count++;
                    //_log.info("t2="+t+" count="+count+" type="+type);
                    if (count >= ChatFilterTextCount) {
                        _text.clear();
                        if (type == 1)
                            MailParcelController.getInstance().sendGmNotification(getName(), "GM Notification", "Mail spam detection!", "Spamer: " + getName() + " \n Text: \n" + chat, 2592000);
                        else if (type == 2)
                            MailParcelController.getInstance().sendGmNotification(getName(), "GM Notification", "PM spam detection!", "Spamer: " + getName() + " \n Text: \n" + chat, 2592000);
                        //_log.info("sendGmNotification["+type+"]");
                        return false;
                    }
                }
            }

            if (_text.size() >= ChatFilterTextSizeClear)
                _text.clear();
            _text.put(time, chat.toLowerCase());
        }
        return true;
    }

    public Attainment _attainment = null;

    public Attainment getAttainment() {
        return _attainment;
    }

    public List<L2Skill> _gavnocod;
    public String _buf_title = null;

    public void buff_list(L2Player target, int page_id, int target_type) {
        List<L2Skill> skill_list = new ArrayList<L2Skill>();
        for (L2Skill skill : target.getAllSkills())
            if (skill.getSkillType() == SkillType.BUFF && skill.getTargetType() != SkillTargetType.TARGET_SELF && skill.hasEffects() && !Util.contains(ConfigValue.BuffStoreNoSkill, skill.getId()) && (!ConfigValue.BuffStoreCheckCondSkill || skill.checkCondition(target, this, true, true, false)))
                skill_list.add(skill);

        int size = skill_list.size();
        int max_page_size = 7;
        int page_list = size / max_page_size;
        if (size > max_page_size * page_list)
            page_list++;
        if (page_id >= page_list)
            page_id = page_list - 1;
        if (page_id < 0)
            page_id = 0;
        int page_start_index = max_page_size * page_id;
        int page_end_index = page_start_index + max_page_size;

        long price = target.getVarLong("buf_price", 0);

        NpcHtmlMessage html = new NpcHtmlMessage(5);

        StringBuffer replyMSG = new StringBuffer("<html noscrollbar><title>Buff Store: " + (target_type == 0 ? "Player" : "Summon") + "</title><body>");

        replyMSG.append("<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
        replyMSG.append("	<tr>");
        replyMSG.append("		<td valign=\"top\">");
        replyMSG.append("			<table border=0 cellspacing=0 cellpadding=4 width=280 align=center height=280>");

        for (int i = page_start_index; i < page_end_index; i++) {
            replyMSG.append("				<tr>");
            replyMSG.append("					<td height=40 FIXWIDTH=210 align=center valign=top>");
            replyMSG.append("						<table border=0 cellspacing=2 cellpadding=4 width=293 height=40 " + ((i % 2) == 0 ? "bgcolor=333333" : "") + ">");
            if (skill_list.size() > i) {
                L2Skill skill = skill_list.get(i);
                L2EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(skill.getId(), skill.getDisplayLevel());
                replyMSG.append("							<tr>");
                replyMSG.append("								<td FIXWIDTH=40 align=right valign=top>");
                replyMSG.append("									<img src=\"" + skill.getIcon() + "\" width=32 height=32>");
                replyMSG.append("								</td>");
                replyMSG.append("								<td FIXWIDTH=200 align=left valign=top>");
                replyMSG.append("									<font color=\"LEVEL\"><a action=\"bypass -h _buff_store:" + target.getObjectId() + ":" + target_type + ":" + skill.getId() + ":" + price + "\">" + skill.getName() + (isGM() ? ("[" + skill.getId() + "]") : "") + "</a></font><br1>› Lv " + (sl != null ? ("<font color=\"00ff00\">" + sl.getType() + "</font>") : skill.getLevel()) + "      Price: " + price + " " + DifferentMethods.getItemName(ConfigValue.BuffStoreItemId));
                replyMSG.append("								</td>");
                replyMSG.append("							</tr>");
            }
            replyMSG.append("						</table>");
            replyMSG.append("					</td>");
            replyMSG.append("				</tr>");
        }

        replyMSG.append("			</table>");
        replyMSG.append("			<center>");
        replyMSG.append("				<table border=0 cellspacing=0 cellpadding=4 width=280 height=1 align=\"center\">");
        replyMSG.append("					<tr>");
        replyMSG.append("						<td height=1 FIXWIDTH=210 align=center valign=top>");
        replyMSG.append("							<img src=\"l2ui.squaregray\" width=\"300\" height=\"1\">");
        replyMSG.append("						</td>");
        replyMSG.append("					</tr>");
        replyMSG.append("				</table>");
        replyMSG.append("				<table border=0 cellspacing=0 cellpadding=1 height=15 align=\"center\">");
        replyMSG.append("					<tr>");
        replyMSG.append("						<td height=15 align=center valign=top>");
        replyMSG.append("							<button action=\"bypass -h _buff_list:" + target.getObjectId() + ":" + (page_id - 1) + ":" + target_type + "\" value=\"\" width=16 height=16 back=\"l2ui_ch3.shortcut_prev\" fore=\"l2ui_ch3.shortcut_prev\"/>");
        replyMSG.append("						</td>");
        replyMSG.append("						<td height=15 width=50 align=center valign=top>");
        replyMSG.append("							<center>" + (page_id + 1) + "/" + page_list + "</center>");
        replyMSG.append("						</td>");
        replyMSG.append("						<td height=15 align=center valign=top>");
        replyMSG.append("							<button action=\"bypass -h _buff_list:" + target.getObjectId() + ":" + (page_id + 1) + ":" + target_type + "\" value=\"\" width=16 height=16 back=\"l2ui_ch3.shortcut_next\" fore=\"l2ui_ch3.shortcut_next\"/>");
        replyMSG.append("						</td>");
        replyMSG.append("					</tr>");
        replyMSG.append("				</table>");
        replyMSG.append("				<table border=0 cellspacing=0 cellpadding=4 width=280 height=1 align=\"center\">");
        replyMSG.append("					<tr>");
        replyMSG.append("						<td height=1 FIXWIDTH=210 align=center valign=top>");
        replyMSG.append("							<img src=\"l2ui.squaregray\" width=\"300\" height=\"1\">");
        replyMSG.append("						</td>");
        replyMSG.append("					</tr>");
        replyMSG.append("				</table>");
        replyMSG.append("			</center>");
        replyMSG.append("			<center>");
        replyMSG.append("				<table border=0 cellspacing=0 cellpadding=0 width=280 align=\"center\">");
        replyMSG.append("					<tr>");
        replyMSG.append("						<td height=25 FIXWIDTH=110 align=center valign=top>");
        replyMSG.append("							<button action=\"bypass -h _buff_list:" + target.getObjectId() + ":" + page_id + ":0\" value=\"For Player\" width=110 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"/>");
        replyMSG.append("						</td>");
        replyMSG.append("						<td height=25 FIXWIDTH=110 align=center valign=top>");
        replyMSG.append("							<button action=\"bypass -h _buff_list:" + target.getObjectId() + ":" + page_id + ":1\" value=\"For Summon\" width=110 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"/>");
        replyMSG.append("						</td>");
        replyMSG.append("					</tr>");
        replyMSG.append("				</table>");
        replyMSG.append("			</center>");
        replyMSG.append("		</td>");
        replyMSG.append("	</tr>");
        replyMSG.append("</table>");
        replyMSG.append("</body>");
        replyMSG.append("</html>");

        html.setHtml(replyMSG.toString());
        sendPacket(html);
    }

    public static void buff_store(L2Player target, L2Player playable, int target_type, int skill_id) {
        L2Playable buff_target = ((target_type == 1 && playable.getPet() != null) ? playable.getPet() : playable);
        L2Skill skill = target.getKnownSkill(skill_id);
        final double hp = buff_target.getCurrentHp();
        final double mp = buff_target.getCurrentMp();
        final double cp = buff_target.getCurrentCp();
        if (skill != null && !skill.checkSkillAbnormal(buff_target) && !skill.isBlockedByChar(buff_target, skill)) {
            for (EffectTemplate et : skill.getEffectTemplates()) {
                int result;
                Env env = new Env(buff_target, buff_target, skill);
                L2Effect effect = et.getEffect(env);
                if (effect != null && effect.getCount() == 1 && effect.getTemplate()._instantly && !effect.getSkill().isToggle()) {
                    effect.onStart();
                    effect.onActionTime();
                    effect.onExit();
                }
                if (effect != null && (result = buff_target.getEffectList().addEffect(effect)) > 0) {
                    if ((result & 2) == 2)
                        buff_target.setCurrentHp(hp, false);
                    if ((result & 4) == 4)
                        buff_target.setCurrentMp(mp);
                    if ((result & 8) == 8)
                        buff_target.setCurrentCp(cp);
                }
            }
        }
        buff_target.updateEffectIcons();
    }

    public void EtcStatusUpdate() {
        sendPacket(new EtcStatusUpdate(this));
        //l2open.util.Util.test();
    }

    public boolean canPenaltyChat() {
        if (!ConfigValue.ChatLimitZoneCheck || ZoneManager.getInstance().checkIfInZone(ZoneType.zone_chat_limit, this))
            return true;
        return false;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public int is_pc() {
        return 1;
    }

    public boolean can_private_log = false;
    public long no_kill_time = -1;

    public boolean no_kill_time() {
        return no_kill_time > System.currentTimeMillis();
    }

    // К - костыли, но так лучше чем дубликат и проще, чем переделывать эту херь, она юзаеться в кастом скриптах клиетов.
    public long getKillTime() {
        return no_kill_time - ConfigValue.AttainmentKillProtectTime * 1000;
    }

    public void set_no_kill_time() {
        if (no_kill_time())
            return;
        no_kill_time = System.currentTimeMillis() + ConfigValue.AttainmentKillProtectTime * 1000;
        setVar("no_kill_time", String.valueOf(no_kill_time));
    }

    public boolean isLindvior() {
        return ConfigValue.EnableLindvior && getRevision() >= 525;
    }

    public int _visual_enchant_level_test = -1;

    public Reflection getActiveReflection() {
        for (Reflection r : ReflectionTable.getInstance().getAll())
            if (r != null && Util.contains(r.getVisitors(), getObjectId()))
                return r;
        return null;
    }

    public boolean _enable_auto = true;
    public int _enable_auto_cp_hp = 0;
    public int _enable_auto_cp_mp = 0;
    public int _enable_auto_cp_cp = 0;

    public int _time_auto_cp_hp = 1000;
    public int _time_auto_cp_mp = 1000;
    public int _time_auto_cp_cp = 1000;

    public int _item_id_auto_cp_hp = ConfigValue.AutoCpPointsHp[0];
    public int _item_id_auto_cp_mp = ConfigValue.AutoCpPointsMp[0];
    public int _item_id_auto_cp_cp = ConfigValue.AutoCpPointsCp[0];

    public ScheduledFuture<?> _next_use_auto_cp_hp;
    public ScheduledFuture<?> _next_use_auto_cp_mp;
    public ScheduledFuture<?> _next_use_auto_cp_cp;

    public void autoCpStart(int type, boolean force) {
        if (_enable_auto && (!ConfigValue.AutoCpOnlyPremim || hasBonus())) {
            L2ItemInstance item = null;
            int item_id = -1;

            double limit_hp = calcStat(Stats.p_limit_hp, null, null) * getMaxHp() / 100;
            double limit_mp = calcStat(Stats.MP_LIMIT, null, null) * getMaxMp() / 100;
            double limit_cp = calcStat(Stats.CP_LIMIT, null, null) * getMaxCp() / 100;

            if ((type == 0 || type == -1) && getCurrentHpPercents() < _enable_auto_cp_hp && _currentHp < limit_hp && (_next_use_auto_cp_hp == null || _next_use_auto_cp_hp.isDone() || force)) {
                item = getInventory().getItemByItemId(_item_id_auto_cp_hp);
                if (item == null)
                    item = getInventory().getItemByItemId(1540);
                //for(int i=0;item == null || i<_item_id_auto_cp_hp.length;i++)
                //	item = getInventory().getItemByItemId(_item_id_auto_cp_hp[i]);
                if (item != null)
                    _next_use_auto_cp_hp = ThreadPoolManager.getInstance().schedule(new AutoCpTask(this, type), _time_auto_cp_hp + 50);
            } else if ((type == 1 || type == -1) && getCurrentMpPercents() < _enable_auto_cp_mp && _currentMp < limit_mp && (_next_use_auto_cp_mp == null || _next_use_auto_cp_mp.isDone() || force)) {
                item = getInventory().getItemByItemId(_item_id_auto_cp_mp);
                //for(int i=0;item == null || i<_item_id_auto_cp_mp.length;i++)
                //	item = getInventory().getItemByItemId(_item_id_auto_cp_mp[i]);
                if (item != null)
                    _next_use_auto_cp_mp = ThreadPoolManager.getInstance().schedule(new AutoCpTask(this, type), _time_auto_cp_mp + 50);
            } else if (type == 2 && getCurrentCpPercents() < _enable_auto_cp_cp && _currentCp < limit_cp && (_next_use_auto_cp_cp == null || _next_use_auto_cp_cp.isDone() || force)) {
                item = getInventory().getItemByItemId(_item_id_auto_cp_cp);
                if (item == null)
                    item = getInventory().getItemByItemId(5591);
                //for(int i=0;item == null || i<_item_id_auto_cp_cp.length;i++)
                //	item = getInventory().getItemByItemId(_item_id_auto_cp_cp[i]);
                if (item != null)
                    _next_use_auto_cp_cp = ThreadPoolManager.getInstance().schedule(new AutoCpTask(this, type), _time_auto_cp_cp + 50);
            }

            if (item == null || isOutOfControl() || isDead() || isFishing() || (!item.getOlympiadUse() && isInOlympiadMode()) || isPotionsDisabled() || getInventory().isLockedItem(item) || isInvisible() || block_hp.get() || isSilentMoving() || isMeditated())
                return;
            ItemTemplates.useHandler(this, item, false);
        }
    }

    public ScheduledFuture<?> _skill_add_mod;
    public long _enchant_time = 0;
    public int raid_points = 0;

    public void addRaidPoints(int rp) {
        raid_points += rp;
        setVar("raid_points", String.valueOf(getRaidPoints()));
    }

    public int getRaidPoints() {
        return raid_points;
    }

    public boolean isActionBlocked(String action) {
        if (_zones == null)
            return ConfigValue.TradeOnlyUnblockZone;
        for (L2Zone z : _zones)
            if (z != null && z.getType() == ZoneType.unblock_actions && z.isActionBlocked(action))
                return false;
        for (L2Zone z : _zones)
            if (z != null && z.getType() != ZoneType.unblock_actions && z.isActionBlocked(action))
                return true;
        return ConfigValue.TradeOnlyUnblockZone;
    }

    public int fraction_point = 0;
    public int fraction_rang = 1;

    public void addFractionPoint(int point) {
    }

    public void setChangeNameColor() {
        String color = null;
        for (int i = 0; i < ConfigValue.PvpNameColorCount.length; i++)
            if (getPvpKills() >= ConfigValue.PvpNameColorCount[i])
                color = ConfigValue.PvpNameColorList[i].trim();
        if (color != null) {
            setNameColor(Integer.decode(color).intValue());
            broadcastUserInfo(true);
        }
    }

    public void setChangeTitleColor() {
        String color = null;
        for (int i = 0; i < ConfigValue.PvpTitleColorCount.length; i++)
            if (getPvpKills() >= ConfigValue.PvpTitleColorCount[i])
                color = ConfigValue.PvpTitleColorList[i].trim();
        if (color != null) {
            setTitleColor(Integer.decode(color).intValue());
            setVar("TitleColor", color);
            broadcastUserInfo(true);
        }
    }

    public boolean isFactionWar(L2Player player) {
        return false;
    }

    // ------------------------------------------------------------------------
    public int[] _paperdoll_test;

    public boolean send_visual_id = true;
    public boolean send_visual_enchant = true;
    public boolean disable_cloak = false;

    public boolean _bdsa = false;

    public boolean isBot123() {
        return _bdsa;
    }

    private int _show_buff_anim_dist = 3000;
    private int _show_attack_dist = 3000;
    private int _show_attack_flag_dist = 3000;

    public int show_attack_flag_dist() {
        return _show_attack_flag_dist;
    }

    public int show_attack_dist() {
        return _show_attack_dist;
    }

    public int show_buff_anim_dist() {
        return _show_buff_anim_dist;
    }

    public void set_show_attack_flag_dist(int value) {
        _show_attack_flag_dist = Math.min(3000, Math.max(0, value));
    }

    public void set_show_attack_dist(int value) {
        _show_attack_dist = Math.min(3000, Math.max(0, value));
    }

    public void setNotShowBuffAnim(boolean value) {
        _show_buff_anim_dist = value ? 0 : 3000;
    }

    public void set_show_buff_anim_dist(int value) {
        _show_buff_anim_dist = Math.min(3000, Math.max(0, value));
    }

    // TODO: переделать на L2GamePacketHandler, установить для дебага счетчик пакетов, выяснить какие пакеты могут вызывать лишнюю нагрузку на трафик...
    private long _lastAttackPacket = 0;

    public long getLastAttackPacket() {
        return _lastAttackPacket;
    }

    public void setLastAttackPacket() {
        _lastAttackPacket = System.currentTimeMillis();
    }

    private long _lastMovePacket = 0;

    public long getLastMovePacket() {
        return _lastMovePacket;
    }

    public void setLastMovePacket() {
        _lastMovePacket = System.currentTimeMillis();
    }

    public boolean isPhantom() {
        return false;
    }

    // ---- пока так пускай побудет ----
    private long _lastRequestRecipeShopManageQuitPacket = 0;

    public long getLastRequestRecipeShopManageQuitPacket() {
        return _lastRequestRecipeShopManageQuitPacket;
    }

    public void setLastRequestRecipeShopManageQuitPacket() {
        _lastRequestRecipeShopManageQuitPacket = System.currentTimeMillis();
    }

    private long _lastRequestBypassToServerPacket = 0;

    public long getLastRequestBypassToServerPacket() {
        return _lastRequestBypassToServerPacket;
    }

    public void setLastRequestBypassToServerPacket() {
        _lastRequestBypassToServerPacket = System.currentTimeMillis();
    }

    private long _lastAppearingPacket = 0;

    public long getLastAppearingPacket() {
        return _lastAppearingPacket;
    }

    public void setLastAppearingPacket() {
        _lastAppearingPacket = System.currentTimeMillis();
    }

    private long _lastRequestPrivateStoreQuitBuyPacket = 0;

    public long getLastRequestPrivateStoreQuitBuyPacket() {
        return _lastRequestPrivateStoreQuitBuyPacket;
    }

    public void setLastRequestPrivateStoreQuitBuyPacket() {
        _lastRequestPrivateStoreQuitBuyPacket = System.currentTimeMillis();
    }

    private long _lastRequestReloadPacket = 0;

    public long getLastRequestReloadPacket() {
        return _lastRequestReloadPacket;
    }

    public void setLastRequestReloadPacket() {
        _lastRequestReloadPacket = System.currentTimeMillis();
    }

    private long _lastRequestActionUsePacket = 0;

    public long getLastRequestActionUsePacket() {
        return _lastRequestActionUsePacket;
    }

    public void setLastRequestActionUsePacket() {
        _lastRequestActionUsePacket = System.currentTimeMillis();
    }

    // п2
    private long _lastRequestExBR_LectureMarkPacket = 0;

    public long getLastRequestExBR_LectureMarkPacket() {
        return _lastRequestExBR_LectureMarkPacket;
    }

    public void setLastRequestExBR_LectureMarkPacket() {
        _lastRequestExBR_LectureMarkPacket = System.currentTimeMillis();
    }

    private long _lastRequestPartyMatchListPacket = 0;

    public long getLastRequestPartyMatchListPacket() {
        return _lastRequestPartyMatchListPacket;
    }

    public void setLastRequestPartyMatchListPacket() {
        _lastRequestPartyMatchListPacket = System.currentTimeMillis();
    }

    private long _lastRequestPreviewItemPacket = 0;

    public long getLastRequestPreviewItemPacket() {
        return _lastRequestPreviewItemPacket;
    }

    public void setLastRequestPreviewItemPacket() {
        _lastRequestPreviewItemPacket = System.currentTimeMillis();
    }

    private long _lastRequestPrivateStoreQuitSellPacket = 0;

    public long getLastRequestPrivateStoreQuitSellPacket() {
        return _lastRequestPrivateStoreQuitSellPacket;
    }

    public void setLastRequestPrivateStoreQuitSellPacket() {
        _lastRequestPrivateStoreQuitSellPacket = System.currentTimeMillis();
    }

    private long _lastRequestRecipeShopListSetPacket = 0;

    public long getLastRequestRecipeShopListSetPacket() {
        return _lastRequestRecipeShopListSetPacket;
    }

    public void setLastRequestRecipeShopListSetPacket() {
        _lastRequestRecipeShopListSetPacket = System.currentTimeMillis();
    }

    private long _lastRequestRefineCancelPacket = 0;

    public long getLastRequestRefineCancelPacket() {
        return _lastRequestRefineCancelPacket;
    }

    public void setLastRequestRefineCancelPacket() {
        _lastRequestRefineCancelPacket = System.currentTimeMillis();
    }

    private long _lastRequestWithdrawalPledgePacket = 0;

    public long getLastRequestWithdrawalPledgePacket() {
        return _lastRequestWithdrawalPledgePacket;
    }

    public void setLastRequestWithdrawalPledgePacket() {
        _lastRequestWithdrawalPledgePacket = System.currentTimeMillis();
    }

    private long _lastSetPrivateStoreBuyListPacket = 0;

    public long getLastSetPrivateStoreBuyListPacket() {
        return _lastSetPrivateStoreBuyListPacket;
    }

    public void setLastSetPrivateStoreBuyListPacket() {
        _lastSetPrivateStoreBuyListPacket = System.currentTimeMillis();
    }

    private long _lastSetPrivateStoreListPacket = 0;

    public long getLastSetPrivateStoreListPacket() {
        return _lastSetPrivateStoreListPacket;
    }

    public void setLastSetPrivateStoreListPacket() {
        _lastSetPrivateStoreListPacket = System.currentTimeMillis();
    }

    private long _lastRequestMagicSkillUsePacket = 0;

    public long getLastRequestMagicSkillUsePacket() {
        return _lastRequestMagicSkillUsePacket;
    }

    public void setLastRequestMagicSkillUsePacket() {
        _lastRequestMagicSkillUsePacket = System.currentTimeMillis();
    }

    private long _lastRequestSetPledgeCrestPacket = 0;

    public long getLastRequestSetPledgeCrestPacket() {
        return _lastRequestSetPledgeCrestPacket;
    }

    public void setLastRequestSetPledgeCrestPacket() {
        _lastRequestSetPledgeCrestPacket = System.currentTimeMillis();
    }

    private long _lastRequestSetPledgeCrestLargePacket = 0;

    public long getLastRequestSetPledgeCrestLargePacket() {
        return _lastRequestSetPledgeCrestLargePacket;
    }

    public void setLastRequestSetPledgeCrestLargePacket() {
        _lastRequestSetPledgeCrestLargePacket = System.currentTimeMillis();
    }

    private long _lastSendWareHouseDepositListPacket = 0;

    public long getLastSendWareHouseDepositListPacket() {
        return _lastSendWareHouseDepositListPacket;
    }

    public void setLastSendWareHouseDepositListPacket() {
        _lastSendWareHouseDepositListPacket = System.currentTimeMillis();
    }

    private long _lastSendWareHouseWithDrawListPacket = 0;

    public long getLastSendWareHouseWithDrawListPacket() {
        return _lastSendWareHouseWithDrawListPacket;
    }

    public void setLastSendWareHouseWithDrawListPacket() {
        _lastSendWareHouseWithDrawListPacket = System.currentTimeMillis();
    }

    private long _lastEnterWorldPacket = 0;

    public long getLastEnterWorldPacket() {
        return _lastEnterWorldPacket;
    }

    public void setLastEnterWorldPacket() {
        _lastEnterWorldPacket = System.currentTimeMillis();
    }

    // ---- пока так пускай побудет ----
    public boolean _active_item_protect = false;

    public boolean canItemAction() {
        if (_active_item_protect) {
            NpcHtmlMessage block_msg = new NpcHtmlMessage(5);
            String htm = Files.read("data/scripts/commands/voiced/item_protect_enter.htm", this);

            htm = htm.replace("<?num1?>", "");
            htm = htm.replace("<?num2?>", "");
            htm = htm.replace("<?num3?>", "");
            htm = htm.replace("<?num4?>", "");

            htm = htm.replace("<?num_enter?>", "");

            block_msg.setHtml(htm);
            sendPacket(block_msg);
        }
        return !_active_item_protect;
    }

    // ------------------------------------------------------------
    public PlayerListenerList getListeners() {
        if (listeners == null)
            synchronized (this) {
                if (listeners == null)
                    listeners = new PlayerListenerList(this);
            }
        return (PlayerListenerList) listeners;
    }

    public int getMaxHp() {
        if (ConfigValue.EnablePtsPlayerStat)
            return (int) (calcStat(Stats.p_max_hp, _activeClass == null ? 1 : _activeClass.getMaxHp(), null, null) * getTemplate().hp_mod);
        return (int) (calcStat(Stats.p_max_hp, _template.baseHpMax, null, null) * getTemplate().hp_mod);
    }

    public int getMaxMp() {
        if (ConfigValue.EnablePtsPlayerStat)
            return (int) (calcStat(Stats.p_max_mp, _activeClass == null ? 1 : _activeClass.getMaxMp(), null, null) * getTemplate().mp_mod);
        return (int) (calcStat(Stats.p_max_mp, _template.baseMpMax, null, null) * getTemplate().mp_mod);
    }

    public int getMaxCp() {
        if (ConfigValue.EnablePtsPlayerStat)
            return (int) calcStat(Stats.p_max_cp, _activeClass == null ? 1 : _activeClass.getMaxCp(), null, null);
        return (int) calcStat(Stats.p_max_cp, _template.baseCpMax, null, null);
    }

    // -------------
    private Future<?> _updateEffectIconsTask;

    private class UpdateEffectIcons extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            updateEffectIconsImpl();
            _updateEffectIconsTask = null;
        }
    }

    @Override
    public void updateEffectIcons() {
        if (entering || isLogoutStarted() || isMassUpdating())
            return;

        if (ConfigValue.UpdateEffectIconsInterval == 0) {
            if (_updateEffectIconsTask != null) {
                _updateEffectIconsTask.cancel(false);
                _updateEffectIconsTask = null;
            }
            updateEffectIconsImpl();
            return;
        }

        if (_updateEffectIconsTask != null)
            return;

        _updateEffectIconsTask = ThreadPoolManager.getInstance().schedule(new UpdateEffectIcons(), ConfigValue.UpdateEffectIconsInterval);
    }

    public void updateEffectIconsImpl() {
        //l2open.util.Util.test();
        L2Effect[] effects = getEffectList().getAllFirstEffects();
        Arrays.sort(effects, EffectsComparator.getInstance());

        PartySpelled ps = new PartySpelled(this, false);
        AbnormalStatusUpdate mi = new AbnormalStatusUpdate();

        for (L2Effect effect : effects)
            if (effect != null && effect.isInUse()) {
                if (effect.getAbnormalType() == SkillAbnormalType.life_force_kamael || effect.getAbnormalType() == SkillAbnormalType.hp_recover || effect.getAbnormalType() == SkillAbnormalType.life_force_others)
                    sendPacket(new ShortBuffStatusUpdate(effect));
                else
                    effect.addIcon(mi);
                if (_party != null)
                    effect.addPartySpelledIcon(ps);
            }

        sendPacket(mi);
        if (_party != null)
            _party.broadcastToPartyMembers(ps);

        // TODO: !!!???
        if (ConfigValue.EnableOlympiad && isInOlympiadMode()) {
            if (_olympiadGame != null) {
                ExOlympiadSpelledInfo OlympiadSpelledInfo = new ExOlympiadSpelledInfo();
                for (L2Effect effect : effects)
                    if (effect != null && effect.isInUse())
                        effect.addOlympiadSpelledIcon(this, OlympiadSpelledInfo);

                if (isOlympiadCompStart() && (_olympiadGame.getType() == CompType.CLASSED || _olympiadGame.getType() == CompType.NON_CLASSED))
                    for (L2Player member : _olympiadGame.getTeamMembers(this))
                        member.sendPacket(OlympiadSpelledInfo);

                if (ConfigValue.EnableEffectIconOlympiad)
                    for (L2Player member : _olympiadGame.getSpectators())
                        member.sendPacket(OlympiadSpelledInfo);
            }
        }
        if (getEventMaster() != null)
            getEventMaster().updateEffectIcons(this);
    }

    // -----------------------------------
    public void addQuickVar(String name, String value) {
        user_variables.put(name, value);
    }

    // -----------------------------------
	/*int prev_l=-1;
	int prev_q=-1;
	int prev_a=-1;

	public void updateItemList(boolean show)
	{
		updateItemList(show, false);
	}

	public void updateItemList(boolean show, boolean force)
	{
		int length = 0;
		int agathion = 0;

		LockType lockType = getInventory().getLockType();
		int[] lockItems = getInventory().getLockItems();

		L2ItemInstance[] _items = getInventory().getItems();
		List<L2ItemInstance> questItems = new ArrayList<L2ItemInstance>();

		for(int i = 0; i < _items.length; i++)
		{
			L2ItemInstance temp = _items[i];
			if(temp != null && temp.getItem().isQuest())
			{
				questItems.add(temp); // add to questinv
				_items[i] = null; // remove from list
			}
			else
			{
				length++; // increase size
				if(temp != null && temp.getItem().getAgathionEnergy() > 0)
					agathion++;
			}
		}

		sendPacket(new ItemList(1, this, length, _items, show, lockType, lockItems));
		//if(prev_l != length || force)
		{
			if(!force)
				prev_l = length;

			if(length > 0)
				sendPacket(new ItemList(2, this, length, _items, show, lockType, lockItems));
		}
		sendPacket(new ExQuestItemList(1, questItems));
		//if(prev_q != questItems.size() || force)
		{
			if(!force)
				prev_q = questItems.size();

			if(questItems.size() > 0)
				sendPacket(new ExQuestItemList(2, questItems));
		}
		//else
		//	_log.info("prev_l="+prev_l+" length="+length+" prev_q="+questItems.size()+" force="+force);

		if(prev_a != agathion)
		{
			if(!force)
				prev_a = agathion;
			if(agathion > 0)
				sendPacket(new ExBR_AgathionEnergyInfoPacket(agathion, _items));
		}
	}
	public void updateWareHouseWithdrawList(WarehouseType type, ItemClass _class)
	{
		sendPacket(new WareHouseWithdrawList(1, this, type, _class));
		//sendPacket(new WareHouseWithdrawList(2, this, type, _class));
	}

	public void updateWareHouseDepositList(WarehouseType type)
	{
		sendPacket(new WareHouseDepositList(1, this, type));
		//sendPacket(new WareHouseDepositList(2, this, type));
	}

	public void requestTradeStart(L2Player requestor)
	{
		requestor.sendPacket(new SystemMessage(SystemMessage.YOU_BEGIN_TRADING_WITH_C1).addString(getName()), new TradeStart(1, requestor, this)*, new TradeStart(2, requestor, this)*);
		sendPacket(new SystemMessage(SystemMessage.YOU_BEGIN_TRADING_WITH_C1).addString(requestor.getName()), new TradeStart(1, this, requestor)*, new TradeStart(2, this, requestor)*);
	}

	public void requestPrivateStoreManageListBuy()
	{
		sendPacket(new PrivateStoreManageListBuy(1, this));
		//sendPacket(new PrivateStoreManageListBuy(2, this));
	}

	public void requestPrivateStoreManageList(boolean _package)
	{
		sendPacket(new PrivateStoreManageList(1, this, _package));
		//sendPacket(new PrivateStoreManageList(2, this, _package));
	}

	public void requestGMViewItemList(L2Player activeChar)
	{
		L2ItemInstance[] _items = activeChar.getInventory().getItems();
		sendPacket(new GMViewItemList(1, activeChar, _items));
		//sendPacket(new GMViewItemList(2, activeChar, _items));
	}
	// RequestRecipeItemMakeSelf
	// RequestRecipeShopMakeItem*/
    // ----------------------------------------------------------------------
    private Pair<Integer, OnAnswerListener> _askDialog = null;

    public void ask(ConfirmDlg dlg, OnAnswerListener listener) {
        if (_askDialog != null)
            return;

        int rnd = Rnd.get(10, 100000);
        _askDialog = new ImmutablePair<Integer, OnAnswerListener>(rnd, listener);
        dlg.setRequestId(rnd);
        sendPacket(dlg);
    }

    public Pair<Integer, OnAnswerListener> getAskListener(boolean clear) {
        if (!clear) {
            return _askDialog;
        } else {
            Pair<Integer, OnAnswerListener> ask = _askDialog;
            _askDialog = null;
            return ask;
        }
    }

    public boolean hasDialogAskActive() {
        return _askDialog != null;
    }

    private boolean checkPvP(L2Player killer) {
        if (ConfigValue.PvPCheckCheckOnCLAN && getClanId() > 0 && getClanId() == killer.getClanId())
            return false;
        else if (ConfigValue.PvPCheckCheckOnHWID && getHWIDs().equals(killer.getHWIDs()))
            return false;
        else if (ConfigValue.PvPCheckCheckOnIP && getIP().equals(killer.getIP()))
            return false;
        else if (ConfigValue.TimeLastPvP > 0 && (getKillTime() + ConfigValue.TimeLastPvP * 1000) > System.currentTimeMillis())
            return false;
        return true;
    }

    private final Map<String, Object> quickVars = new ConcurrentHashMap<>();

    public void addQuickVar(String name, Object value) {
        if (quickVars.containsKey(name))
            quickVars.remove(name);
        quickVars.put(name, value);
    }

    public void deleteQuickVar(String name) {
        quickVars.remove(name);
    }

    public int getQuickVarI(String name, int... defaultValue) {
        if (!quickVars.containsKey(name)) {
            if (defaultValue.length > 0)
                return defaultValue[0];
            return -1;
        }
        return ((Integer) quickVars.get(name)).intValue();
    }

    public boolean getQuickVarB(String name, boolean... defaultValue) {
        if (!quickVars.containsKey(name)) {
            if (defaultValue.length > 0)
                return defaultValue[0];
            return false;
        }
        return ((Boolean) quickVars.get(name)).booleanValue();
    }

    public boolean containsQuickVar(String name) {
        return quickVars.containsKey(name);
    }

    public String getQuickVarS(String name, String... defaultValue) {
        if (!quickVars.containsKey(name)) {
            if (defaultValue.length > 0)
                return defaultValue[0];
            return null;
        }
        return (String) quickVars.get(name);
    }
}