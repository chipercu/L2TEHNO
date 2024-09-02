package events.LastHunter;

import gnu.trove.list.array.TIntArrayList;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.LockType;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2Weapon;
import l2open.util.Files;
import l2open.util.GCSArray;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.Util;
import l2open.util.reference.*;

public class LastHunter extends Functions implements ScriptFile {
    private static final Logger _log = Logger.getLogger(LastHunter.class.getName());
    private static Reflection reflection = null;
    private static boolean _active = false;

    private static final String EVENT_NAME = "Last Hunter";

    public class StartTask extends l2open.common.RunnableImpl {
        public void runImpl() {

            if (!_active) {
                startTimerTask();
                return;
            }

            if (isPvPEventStarted()) {
                _log.info("Last Hero not started: another event is already running");
                startTimerTask();
                return;
            }

            if (TerritorySiege.isInProgress()) {
                _log.info("LastHero not started: TerritorySiege in progress");
                startTimerTask();
                return;
            }

            for (Castle c : CastleManager.getInstance().getCastles().values()) {
                if (c.getSiege() != null && c.getSiege().isInProgress()) {
                    _log.info("LastHero not started: CastleSiege in progress");
                    startTimerTask();
                    return;
                }
            }

            if (false)
                start(new String[]{"1", "1"});
            else
                start(new String[]{"-1", "-1"});
        }
    }

    private static ScheduledFuture<?> _startTask;
    private static GCSArray<HardReference<L2Player>> players_list = new GCSArray<>();
    private static GCSArray<HardReference<L2Player>> live_list = new GCSArray<>();

    private enum WeaponType {
        SHIELD,
        SWORD,
        MAGESWORD,
        BLUNT,
        DAGGER,
        BOW,
        POLE,
        ETC,
        FIST,
        DUAL,
        DUALFIST,
        BIGSWORD, // Two Handed Swords
        PET,
        ROD, // fishingrod
        BIGBLUNT,
        CROSSBOW,
        RAPIER,
        ANCIENTSWORD, // Kamael 2h sword
        DUALDAGGER

    }


    private static final int[] firstBuffs = new int[]{
            1035, 4,        //Mental Shield
            1036, 2,        //Magic Barrier
            1040, 3,        //Shield
            1204, 2,        //Wind Walk
            1045, 6,        //Bless the Body
            1048, 6,        //Bless the Soul
            1086, 2,        //Haste
            1085, 3,        //Acumen
            1078, 6,        //Concentration
            1240, 3,        //Guidance
            1062, 2,        //Berserker Spirit
            1044, 3,        //Regeneration
    };
    private static final int[] secondBuffs = new int[]{
            1259, 4,        //Resist Shock
            1303, 2,        //Wild Magic
            1352, 1,        //Elemental Protection
            1353, 1,        //Divine Protection
            1354, 1,        //Arcane Protection
            1397, 3,        //Clarity
            1499, 1,        //Improved Combat
            1500, 1,        //Improved Magic
            1501, 1,        //Improved Condition
            1502, 1,        //Improved Critical Attack
            1504, 1,        //Improved Movement
            1519, 1,        //Chant of Blood Awakening
            1503, 1,        //Improved Shield Defense
    };
    private static final int[] threeBuffsFiz = new int[]{
            264, 1,        //Song of Earth
            267, 1,        //Song of Warding
            268, 1,        //Song of Wind
            269, 1,        //Song of Hunter
            271, 1,        //Dance of the Warrior
            274, 1,        //Dance of Fire
            275, 1,        //Dance of Fury
            304, 1,        //Song of Vitality
            310, 1,        //Dance of the Vampire
            364, 1,        //Song of Champion
            266, 1,        //Song of Water
    };
    private static final int[] threeBuffsMag = new int[]{
            264, 1,        //Song of Earth
            267, 1,        //Song of Warding
            268, 1,        //Song of Wind
            273, 1,        //Dance of the Mystic
            276, 1,        //Dance of Concentration
            304, 1,        //Song of Vitality
            349, 1,        //Song of Renewal
            363, 1,        //Song of Meditation
            365, 1,        //Dance of Siren
            266, 1,        //Song of Water
            265, 1,        //Song of Life
    };
    private static final int[] fourBuffsFiz = new int[]{
            1363, 1,        //Chant of Victory
            1364, 1,        //Eye of Paagrio
            1388, 3,        //Greater Might
            1461, 1,        //Chant of Protection
            1542, 1,        //Counter Critical
            4699, 13,        //Blessing of Queen
            915, 1,            //Dance of Berserker
            1307, 3,        //Prayer
    };
    private static final int[] fourBuffsMag = new int[]{
            1357, 1,        //Prophecy of Wind
            1364, 1,        //Eye of Paagrio
            1389, 3,        //Greater Shield
            1461, 1,        //Chant of Protection
            1542, 1,        //Counter Critical
            4703, 13,        //Gift of Seraphim
            915, 1,            //Dance of Berserker
            1307, 3,        //Prayer
    };

    private static final int[] arrows = {
            1342,        //Iron Arrow  -  Arrow C
            1343,        //Silver Arrow  -  Arrow B
            1344,        //Mithril Arrow  -  Arrow A
            1345,        //Shining Arrow  -  Arrow S
    };
    private static final int[] bolts = {
            9634,        //Steel Bolt  -  Bolt C
            9635,        //Silver Bolt  -  Bolt B
            9636,        //Mithril Bolt  -  Bolt A
            9637,        //Shining Bolt  -  Bolt S
    };
    private static final int[] soulshot = {
            1464,   //Soulshot: C-grade
            1465,   //Soulshot: B-grade
            1466,   //Soulshot: A-grade
            1467,   //Soulshot: S-grade
    };
    private static final int[] spiritshot = {
            3949,   //spiritshot: C-grade
            3950,   //spiritshot: B-grade
            3951,   //spiritshot: A-grade
            3952,   //spiritshot: S-grade
    };
    public static int[] LastHunterStart = {0, 0, 0, 5, 0, 10, 0, 15, 0, 20, 0, 25, 0, 30, 0, 35, 0, 40, 0, 45, 0, 50, 0, 55, 0, 60, 1, 0, 1, 5, 1, 10, 1, 15, 1, 20, 1, 25, 1, 30, 1, 35, 1, 40, 1, 45, 1, 50, 1, 55, 1, 60, 2, 0, 2, 5, 2, 10, 2, 15, 2, 20, 2, 25, 2, 30, 2, 35, 2, 40, 2, 45, 2, 50, 2, 55, 2, 60, 3, 0, 3, 5, 3, 10, 3, 15, 3, 20, 3, 25, 3, 30, 3, 35, 3, 40, 3, 45, 3, 50, 3, 55, 3, 60, 4, 0, 4, 5, 4, 10, 4, 15, 4, 20, 4, 25, 4, 30, 4, 35, 4, 40, 4, 45, 4, 50, 4, 55, 4, 60, 5, 0, 5, 5, 5, 10, 5, 15, 5, 20, 5, 25, 5, 30, 5, 35, 5, 40, 5, 45, 5, 50, 5, 55, 5, 60, 6, 0, 6, 5, 6, 10, 6, 15, 6, 20, 6, 25, 6, 30, 6, 35, 6, 40, 6, 45, 6, 50, 6, 55, 6, 60, 7, 0, 7, 5, 7, 10, 7, 15, 7, 20, 7, 25, 7, 30, 7, 35, 7, 40, 7, 45, 7, 50, 7, 55, 7, 60, 8, 0, 8, 5, 8, 10, 8, 15, 8, 20, 8, 25, 8, 30, 8, 35, 8, 40, 8, 45, 8, 50, 8, 55, 8, 60, 9, 0, 9, 5, 9, 10, 9, 15, 9, 20, 9, 25, 9, 30, 9, 35, 9, 40, 9, 45, 9, 50, 9, 55, 9, 60, 10, 0, 10, 5, 10, 10, 10, 15, 10, 20, 10, 25, 10, 30, 10, 35, 10, 40, 10, 45, 10, 50, 10, 55, 10, 60, 11, 0, 11, 5, 11, 10, 11, 15, 11, 20, 11, 25, 11, 30, 11, 35, 11, 40, 11, 45, 11, 50, 11, 55, 11, 60, 12, 0, 12, 5, 12, 10, 12, 15, 12, 20, 12, 25, 12, 30, 12, 35, 12, 40, 12, 45, 12, 50, 12, 55, 12, 60, 13, 0, 13, 5, 13, 10, 13, 15, 13, 20, 13, 25, 13, 30, 13, 35, 13, 40, 13, 45, 13, 50, 13, 55, 13, 60, 14, 0, 14, 5, 14, 10, 14, 15, 14, 20, 14, 25, 14, 30, 14, 35, 14, 40, 14, 45, 14, 50, 14, 55, 14, 60, 15, 0, 15, 5, 15, 10, 15, 15, 15, 20, 15, 25, 15, 30, 15, 35, 15, 40, 15, 45, 15, 50, 15, 55, 15, 60, 16, 0, 16, 5, 16, 10, 16, 15, 16, 20, 16, 25, 16, 30, 16, 35, 16, 40, 16, 45, 16, 50, 16, 55, 16, 60, 17, 0, 17, 5, 17, 10, 17, 15, 17, 20, 17, 25, 17, 30, 17, 35, 17, 40, 17, 45, 17, 50, 17, 55, 17, 60, 18, 0, 18, 5, 18, 10, 18, 15, 18, 20, 18, 25, 18, 30, 18, 35, 18, 40, 18, 45, 18, 50, 18, 55, 18, 60, 19, 0, 19, 5, 19, 10, 19, 15, 19, 20, 19, 25, 19, 30, 19, 35, 19, 40, 19, 45, 19, 50, 19, 55, 19, 60, 20, 0, 20, 5, 20, 10, 20, 15, 20, 20, 20, 25, 20, 30, 20, 35, 20, 40, 20, 45, 20, 50, 20, 55, 20, 60, 21, 0, 21, 5, 21, 10, 21, 15, 21, 20, 21, 25, 21, 30, 21, 35, 21, 40, 21, 45, 21, 50, 21, 55, 21, 60, 22, 0, 22, 5, 22, 10, 22, 15, 22, 20, 22, 25, 22, 30, 22, 35, 22, 40, 22, 45, 22, 50, 22, 55, 22, 60, 23, 0, 23, 5, 23, 10, 23, 15, 23, 20, 23, 25, 23, 30, 23, 35, 23, 40, 23, 45, 23, 50, 23, 55};
    private static final int[] minLevelForCategory = {20, 30, 40, 52, 62, 76};
    private static final int[] maxLevelForCategory = {29, 39, 51, 61, 75, 85};

    private static final int bonusItemID = 57;
    private static final int bonusItemCount = 100000;
    private static final boolean setHero = true;
    private static final boolean winMaxDamager = true;

    private static final int _eventDuration = 60 * 4;
    private static boolean _isRegistrationActive = false;
    private static int _status = 0;
    private static int _time_to_start;
    private static int _category;
    private static int _minLevel;
    private static int _maxLevel;
    private static int _autoContinue = 0;
    private static Map<L2Player, ArrayList<L2ItemInstance>> items_collector = new HashMap<>();
    private static Map<L2Player, ArrayList<L2ItemInstance>> items_antidiup = new HashMap<>();
    private static ArrayList<L2NpcInstance> mobs = new ArrayList<>();
    private static final Map<String, Integer[]> robe_sets = new HashMap<String, Integer[]>() {{
        put("C", new Integer[]{
                2414,        //Full Plate Helmet  -  None C
                439,        //Karmian Tunic  -  Magic C
                471,        //Karmian Stockings  -  Magic C
                2454,        //Karmian Gloves  -  None C
                2430,        //Karmian Boots  -  None C
        });
        put("B", new Integer[]{
                2415,        //Avadon Circlet  -  None B
                2406,        //Avadon Robe  -  Magic B
                5716,        //Avadon Gloves  -  None B
                5732,        //Avadon Boots  -  None B
        });
        put("A", new Integer[]{
                2419,        //Majestic Circlet  -  None A
                2409,        //Majestic Robe  -  Magic A
                5776,        //Majestic Gauntlets  -  None A
                5788,        //Majestic Boots  -  None A
        });
        put("S80", new Integer[]{
                15608,        //Moirai Circlet  -  None S80
                15611,        //Moirai Tunic  -  Magic S80
                15614,        //Moirai Stockings  -  Magic S80
                15617,        //Moirai Gloves  -  None S80
                15620,        //Moirai Footwear  -  None S80
        });
    }};
    private static final Map<String, Integer[]> light_sets = new HashMap<String, Integer[]>() {{
        put("C", new Integer[]{
                2414,        //Full Plate Helmet  -  None C
                398,        //Plated Leather  -  Light C
                418,        //Plated Leather Gaiters  -  Light C
                2455,        //Plated Leather Gloves  -  None C
                2431,        //Plated Leather Boots  -  None C
        });
        put("B", new Integer[]{
                2417,        //Doom Helmet  -  None B
                2392,        //Leather Armor of Doom  -  Light B
                5723,        //Doom Gloves  -  None B
                5739,        //Doom Boots  -  None B
        });
        put("A", new Integer[]{
                2419,        //Majestic Circlet  -  None A
                2395,        //Majestic Leather Armor  -  Light A
                5775,        //Majestic Gauntlets  -  None A
                5787,        //Majestic Boots  -  None A
        });
        put("S80", new Integer[]{
                15607,        //Moirai Leather Helmet  -  None S80
                15610,        //Moirai Leather Breastplate  -  Light S80
                15613,        //Moirai Leather Leggings  -  Light S80
                15616,        //Moirai Leather Gloves  -  None S80
                15619,        //Moirai Leather Boots  -  None S80
        });
    }};
    private static final Map<String, Integer[]> heavy_sets = new HashMap<String, Integer[]>() {{
        put("C", new Integer[]{
                2414,        //Full Plate Helmet  -  None C
                356,        //Full Plate Armor  -  Heavy C
                2462,        //Full Plate Gauntlets  -  None C
                2438,        //Full Plate Boots  -  None C
        });
        put("B", new Integer[]{
                2416,        //Blue Wolf Helmet  -  None B
                358,        //Blue Wolf Breastplate  -  Heavy B
                2380,        //Blue Wolf Gaiters  -  Heavy B
                5718,        //Blue Wolf Gloves  -  None B
                5734,        //Blue Wolf Boots  -  None B
        });
        put("A", new Integer[]{
                2419,        //Majestic Circlet  -  None A
                2383,        //Majestic Plate Armor  -  Heavy A
                5774,        //Majestic Gauntlets  -  None A
                5786,        //Majestic Boots  -  None A
        });
        put("S80", new Integer[]{
                15606,        //Moirai Helmet  -  None S80
                15609,        //Moirai Breastplate  -  Heavy S80
                15612,        //Moirai Gaiters  -  Heavy S80
                15615,        //Moirai Gauntlets  -  None S80
                15618,        //Moirai Boots  -  None S80
        });
    }};
    private static final Map<String, Integer[]> jewels_sets = new HashMap<String, Integer[]>() {{
        put("C", new Integer[]{
                886,        //Ring of Binding  -  None C
                888,        //Blessed Ring  -  None C
                919,        //Blessed Necklace  -  None C
                855,        //Nassen's Earring  -  None C
                857,        //Blessed Earring  -  None C
        });
        put("B", new Integer[]{
                901,        //Ring of Holy Spirit  -  None B
                891,        //Sage's Ring  -  None B
                870,        //Earring of Holy Spirit  -  None B
                860,        //Sage's Earring  -  None B
                932,        //Necklace of Holy Spirit  -  None B
        });
        put("A", new Integer[]{
                862,        //Majestic Earring  -  None A
                893,        //Majestic Ring  -  None A
                924,        //Majestic Necklace  -  None A
                868,        //Earring of Phantom  -  None A
                899,        //Ring of Phantom  -  None A
        });
        put("S80", new Integer[]{
                15724,        //Moirai Earring  -  None S80
                15723,        //Moirai Ring  -  None S80
                15725,        //Moirai Necklace  -  None S80
                9455,        //Dynasty Earrings  -  None S
                9457,        //Dynasty Ring  -  None S
        });
    }};

    private static final Map<WeaponType, Integer[]> weapons_sets = new HashMap<WeaponType, Integer[]>() {{
        put(WeaponType.RAPIER, new Integer[]{9293, 9317, 9354, 10461});
        put(WeaponType.ANCIENTSWORD, new Integer[]{
                9297,        //Saber Tooth  -  Ancient Sword C
                9311,        //Innominate Victory  -  Ancient Sword B
                9357,        //Durendal  -  Ancient Sword A
                10464,        //Icarus Wingblade  -  Ancient Sword S80
        });
        put(WeaponType.CROSSBOW, new Integer[]{
                9259,        //Ballista  -  Crossbow C
                9326,        //Hell Hound  -  Crossbow B
                9363,        //Screaming Vengeance  -  Crossbow A
                10469,        //Icarus Shooter  -  Crossbow S80
        });
        put(WeaponType.MAGESWORD, new Integer[]{
                6313,        //Homunkulus's Sword  -  Sword C
                7722,        //Sword of Valhalla  -  Sword B
                5643,        //Sword of Miracles  -  Sword A
                10440,        //Icarus Spirit  -  Sword S80
        });
        put(WeaponType.POLE, new Integer[]{
                4852,        //Orcish Poleaxe  -  Pole C
                4859,        //Lance  -  Pole B
                8803,        //Tiphon's Spear  -  Pole A
                10450,        //Icarus Trident  -  Pole S80
        });
        put(WeaponType.DAGGER, new Integer[]{
                6358,        //Crystal Dagger  -  Dagger C
                4778,        //Kris  -  Dagger B
                8800,        //Naga Storm  -  Dagger A
                10446,        //Icarus Disperser  -  Dagger S80
        });
        put(WeaponType.BOW, new Integer[]{
                4815,        //Elemental Bow  -  Bow C
                4829,        //Bow of Peril  -  Bow B
                8808,        //Shyeed's Bow  -  Bow A
                10445,        //Icarus Spitter  -  Bow S80
        });
        put(WeaponType.SWORD, new Integer[]{
                4708,        //Samurai Longsword  -  Sword C
                4717,        //Sword of Damascus  -  Sword B
                5648,        //Dark Legion's Edge  -  Sword A
                10434,        //Icarus Sawsword  -  Sword S80
        });
        put(WeaponType.DUAL, new Integer[]{
                2582,        //Katana*Katana  -  Dual Sword C
                2626,        //Samurai Long Sword*Samurai Long Sword  -  Dual Sword B
                5706,        //Damascus*Damascus  -  Dual Sword A
                10415,        //Icarus Dual Sword  -  Dual Sword S80
        });
        put(WeaponType.BIGSWORD, new Integer[]{
                6347,        //Berserker Blade  -  Big Sword C
                4725,        //Great Sword  -  Big Sword B
                8791,        //Sword of Ipos  -  Big Sword A
                10437,        //Icarus Heavy Arms  -  Big Sword S80
        });
        put(WeaponType.BLUNT, new Integer[]{
                4745,        //Yaksa Mace  -  Blunt C
                4753,        //Art of Battle Axe  -  Blunt B
                8794,        //Barakiel's Axe  -  Blunt A
                10453,        //Icarus Hammer  -  Blunt S80
        });
        put(WeaponType.FIST, new Integer[]{
                4800,        //Knuckle Duster  -  Dual Fist C
                4806,        //Bellion Cestus  -  Dual Fist B
                8810,        //Sobekk's Hurricane  -  Dual Fist A
                10459,        //Icarus Hand  -  Dual Fist S80
        });
        put(WeaponType.SHIELD, new Integer[]{
                2497,        //Full Plate Shield  -  Shield C
                633,        //Zubei's Shield  -  Shield B
                2498,        //Shield of Nightmare  -  Shield A
                15621,        //Moirai Shield  -  Shield S80
        });

    }};


    private static final int armorPoint = 36643;
    private static final int weaponPoint = 36644;
    private static final int buffPoint = 36645;
    private static final int[] points = {armorPoint, weaponPoint, buffPoint};
    private static final int mob = 22745;

    private static final ArrayList<Location> spawnLocations = new ArrayList<>(Arrays.asList(
            new Location(7944, -21656, -3344), new Location(5656, -22456, -3161), new Location(4040, -21320, -3297), new Location(2904, -19544, -3313), new Location(2136, -17608, -3417), new Location(1624, -15384, -3194), new Location(1624, -13224, -3276), new Location(1848, -10552, -3492),
            new Location(2264, -7576, -3338), new Location(3672, -5704, -3068), new Location(5144, -5688, -3337), new Location(7416, -5448, -3247), new Location(6952, -22232, -3385), new Location(8840, -18776, -3527), new Location(9112, -17288, -3544)
    ));
    private static final ArrayList<Location> spawnPoints = new ArrayList<>(Arrays.asList(
            new Location(5496, -19640, -3638), new Location(6008, -17336, -3724), new Location(7416, -14600, -3730), new Location(5560, -14312, -3732), new Location(3480, -15544, -3570), new Location(3128, -11000, -3665), new Location(5112, -11384, -3660), new Location(5256, -8472, -3606),
            new Location(6872, -11528, -3669), new Location(6648, -7144, -3591), new Location(3368, -10216, -3790), new Location(3736, -17752, -3655), new Location(8232, -19608, -3546), new Location(6888, -21048, -3496), new Location(6104, -18904, -3739), new Location(13288, -13736, -3231),
            new Location(14024, -18232, -3155), new Location(13368, -9544, -3209), new Location(17368, -8856, -3188), new Location(17544, -14184, -3165), new Location(18152, -19288, -3275), new Location(21384, -17128, -3024), new Location(23112, -19960, -2725), new Location(27816, -19736, -2422),
            new Location(27768, -10792, -2346), new Location(20232, -12248, -2792), new Location(18888, -9272, -2767), new Location(22120, -6088, -2005), new Location(20952, -15928, -3069), new Location(16568, -19016, -3218)
    ));
    private static final ArrayList<Location> spawnMobs = new ArrayList<>(Arrays.asList(
            new Location(14376, -15096, -3168), new Location(13832, -8760, -3195), new Location(14904, -10424, -3334), new Location(15736, -9144, -3291),
            new Location(16568, -12936, -3157), new Location(17480, -14600, -3143), new Location(15672, -16600, -3236), new Location(15192, -18296, -3212),
            new Location(16904, -17832, -3228), new Location(18472, -19192, -3299), new Location(21832, -16760, -3008), new Location(23608, -18232, -2754),
            new Location(26392, -19112, -2478), new Location(26008, -15800, -2729), new Location(23000, -14872, -3087), new Location(20168, -15592, -3103),
            new Location(20744, -13912, -3044), new Location(21912, -11752, -2719), new Location(19608, -9272, -2802), new Location(20984, -8264, -2742),
            new Location(21848, -9096, -2808)
    ));


    private static final List<Long> time2 = new ArrayList<>();

    private static ScheduledFuture<?> _endTask;


    private static L2Zone _zone;
    private final ZoneListener _zoneListener = new ZoneListener();

    private void initTimer(boolean new_day) {
        time2.clear();
//        if (LastHunterStart[0] == -1)
//            return;
        long cur_time = System.currentTimeMillis();
        for (int i = 0; i < LastHunterStart.length; i += 2) {
            Calendar ci = Calendar.getInstance();
            if (new_day)
                ci.add(Calendar.HOUR_OF_DAY, 12);
            ci.set(Calendar.HOUR_OF_DAY, LastHunterStart[i]);
            ci.set(Calendar.MINUTE, LastHunterStart[i + 1]);
            ci.set(Calendar.SECOND, 00);

            long delay = ci.getTimeInMillis();
            if (delay - cur_time > 0)
                time2.add(delay);
            ci = null;
        }
        Collections.sort(time2);
        long delay = 0;
        while (time2.size() != 0 && (delay = time2.remove(0)) - cur_time <= 0) ;
        if (_startTask != null)
            _startTask.cancel(true);

        _startTask = ThreadPoolManager.getInstance().schedule(new StartTask(), 10000);
//        if (ConfigValue.develop){
//            _startTask = ThreadPoolManager.getInstance().schedule(new StartTask(), 10000);
//        }else {
//            if (delay - cur_time > 0){
//                _startTask = ThreadPoolManager.getInstance().schedule(new StartTask(), delay - cur_time);
//            }
//        }


    }

    public void unEquipItem(L2Player player, int slot) {
        final L2ItemInstance paperdollItem = player.getInventory().getPaperdollItem(slot);
        if (paperdollItem != null) {
            if (!items_collector.containsKey(player)) {
                ArrayList<L2ItemInstance> itemList = new ArrayList<>();
                itemList.add(paperdollItem);
                items_collector.put(player, itemList);
            } else {
                items_collector.get(player).add(paperdollItem);
            }
            player.getInventory().unEquipItemInSlot(slot);
        }

    }

    public static void equipItems(L2Player player) {
        final ArrayList<L2ItemInstance> itemList = items_collector.get(player);
        if (itemList != null) {
            for (L2ItemInstance itemInstance : itemList) {
                player.getInventory().equipItem(itemInstance, true);
            }
        }
    }

    public void addAndEquipItem(L2Player player, int id) {
        L2ItemInstance itemInstance = player.getInventory().addItem(id, 1);
        if (!items_antidiup.containsKey(player)) {
            ArrayList<L2ItemInstance> itemList = new ArrayList<>();
            itemList.add(itemInstance);
            items_antidiup.put(player, itemList);
        } else {
            items_antidiup.get(player).add(itemInstance);
        }
        player.getInventory().equipItem(itemInstance, true);
        if (itemInstance.isWeapon()) {
            int i = 0;
            if (itemInstance.getCrystalType() == L2Item.Grade.C) {
                i = 0;
            } else if (itemInstance.getCrystalType() == L2Item.Grade.B) {
                i = 1;
            } else if (itemInstance.getCrystalType() == L2Item.Grade.A) {
                i = 2;
            } else if (itemInstance.getCrystalType() == L2Item.Grade.S || itemInstance.getCrystalType() == L2Item.Grade.S80 || itemInstance.getCrystalType() == L2Item.Grade.S84) {
                i = 3;
            }
            autoSoulshot(player, player.getInventory().addItem(soulshot[i], 1000));
            autoSoulshot(player, player.getInventory().addItem(spiritshot[i], 1000));
            if (itemInstance.getItemType().equals(L2Weapon.WeaponType.BOW)) {
                final L2ItemInstance arrow = player.getInventory().addItem(arrows[i], 1000);
//                IItemHandler handler = ItemHandler.getInstance().getItemHandler(arrow.getItemId());
//                handler.useItem(player, arrow, false);
            } else if (itemInstance.getItemType().equals(L2Weapon.WeaponType.CROSSBOW)) {
                final L2ItemInstance bolt = player.getInventory().addItem(bolts[i], 1000);
//                IItemHandler handler = ItemHandler.getInstance().getItemHandler(bolt.getItemId());
//                handler.useItem(player, bolt, false);
            }
        }
    }

    public void autoSoulshot(L2Player player, L2ItemInstance soulshot) {
        player.addAutoSoulShot(soulshot.getItemId());
        player.AutoShot();
        player.sendPacket(new ExAutoSoulShot(soulshot.getItemId(), true));
        player.sendPacket(new SystemMessage(SystemMessage.THE_USE_OF_S1_WILL_NOW_BE_AUTOMATED).addString(soulshot.getName()));
        IItemHandler handler = ItemHandler.getInstance().getItemHandler(soulshot.getItemId());
        handler.useItem(player, soulshot, false);
    }

    public void playerHeal() {
        L2Player player = (L2Player) getSelf();
        player.setCurrentCp(player.getMaxCp());
        player.setCurrentHp(player.getMaxHp(), true);
        player.setCurrentMp(player.getMaxMp());

    }

    public void unlockItems(L2Player player) {
        player.getInventory().unlock();
    }

    //Armor Point
    public String DialogAppend_36643(Integer val) {
        String value = "Получить доспех!";
        String action = "bypass -h scripts_events.LastHunter.LastHunter:equipArmor";
        L2Player player = (L2Player) getSelf();
        if (val == 0)
            return Files.read("data/scripts/events/LastHunter/31226.html", player)
                    .replaceAll("content", htmlButton(value, action, 300, 32));
        return "";
    }

    //Weapon Point
    public String DialogAppend_36644(Integer val) {
        String value = "Получить оружие!";
        String action = "bypass -h scripts_events.LastHunter.LastHunter:equipWeapon";
        L2Player player = (L2Player) getSelf();
        if (val == 0)
            return Files.read("data/scripts/events/LastHunter/31226.html", player)
                    .replaceAll("content", htmlButton(value, action, 300, 32));
        return "";

    }

    //Buff Point
    public String DialogAppend_36645(Integer val) {
        L2Player player = (L2Player) getSelf();
        String value = "Получить магическую поддержку!";
        String value1 = "Получить магическую поддержку (магическая)";
        String value2 = "Получить магическую поддержку (физическая)";
        final String action1 = "bypass -h scripts_events.LastHunter.LastHunter:buffPlayer mag";
        final String action2 = "bypass -h scripts_events.LastHunter.LastHunter:buffPlayer fiz";
        ClassId classId = player.getClassId();
        if (classId == ClassId.maleSoulbreaker || classId == ClassId.maleSoulhound || classId == ClassId.femaleSoulbreaker || classId == ClassId.femaleSoulhound || classId == ClassId.maleSoldier || classId == ClassId.femaleSoldier) {
            String string = htmlButton(value1, action1, 300, 32) + htmlButton(value2, action2, 300, 32);
            return Files.read("data/scripts/events/LastHunter/31226.html", player)
                    .replaceAll("content", string);
        } else {
            if (classId.isMage()) {
                return Files.read("data/scripts/events/LastHunter/31226.html", player)
                        .replaceAll("content", htmlButton(value, action1, 200, 32));
            } else {
                return Files.read("data/scripts/events/LastHunter/31226.html", player)
                        .replaceAll("content", htmlButton(value, action2, 200, 32));
            }
        }
    }

    public String DialogAppend_36646(Integer val) {
        L2Player player = (L2Player) getSelf();
        String value = "Получить магическую поддержку!";
        String value1 = "Получить магическую поддержку (магическая)";
        String value2 = "Получить магическую поддержку (физическая)";
        final String action1 = "bypass -h scripts_events.LastHunter.LastHunter:buffPlayer mag";
        final String action2 = "bypass -h scripts_events.LastHunter.LastHunter:buffPlayer fiz";
        ClassId classId = player.getClassId();
        if (classId == ClassId.maleSoulbreaker || classId == ClassId.maleSoulhound || classId == ClassId.femaleSoulbreaker || classId == ClassId.femaleSoulhound || classId == ClassId.maleSoldier || classId == ClassId.femaleSoldier) {
            String string = htmlButton(value1, action1, 300, 32) + htmlButton(value2, action2, 300, 32);
            return Files.read("data/scripts/events/LastHunter/31226.html", player)
                    .replaceAll("content", string);
        } else {
            if (classId.isMage()) {
                return Files.read("data/scripts/events/LastHunter/31226.html", player)
                        .replaceAll("content", htmlButton(value, action1, 200, 32));
            } else {
                return Files.read("data/scripts/events/LastHunter/31226.html", player)
                        .replaceAll("content", htmlButton(value, action2, 200, 32));
            }
        }


//        if (val == 0)
//            return Files.read("data/scripts/events/LastHunter/31226.html", player)
//                    .replaceAll("content", htmlButton(value, action, 200, 32));
    }

    public void onLoad() {
        if (_active) {
            _zone = ZoneManager.getInstance().getZoneById(ZoneType.battle_zone, 9003, false);
            _zone.getListenerEngine().addMethodInvokedListener(_zoneListener);
            initTimer(true);
            _active = ServerVariables.getString("LastHunter", "on").equalsIgnoreCase("on");
            _log.info("Loaded Event: Last Hunter");
            for (L2Player player : L2ObjectsStorage.getPlayers()) {
                if (player.isGM()) {
                    player.sendMessage("Load Event : Last Hunter . Start Time : " + Calendar.getInstance().getTime());
                }
            }
        } else {
            for (L2Player player : L2ObjectsStorage.getPlayers()) {
                if (player.isGM()) {
                    player.sendMessage("Load Event : Last Hunter - " + _active);
                }
            }
        }
    }

    public void onReload() {
        if (_zone != null)
            _zone.getListenerEngine().removeMethodInvokedListener(_zoneListener);
        if (_startTask != null)
            _startTask.cancel(true);
    }

    public void onShutdown() {
        onReload();
    }


    public static boolean isActive() {
        return _active;
    }

    public void activateEvent() {
        L2Player player = (L2Player) getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            if (_startTask == null)
                initTimer(false);
            ServerVariables.set("LastHunter", "on");
            _log.info("Event 'Last Hunter' activated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.LastHunter.AnnounceEventStarted", null);
        } else
            player.sendMessage("Event 'Last Hero' already active.");

        _active = true;

        show(Files.read("data/html/admin/events.htm", player), player);
    }

    public void deactivateEvent() {
        L2Player player = (L2Player) getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            if (_startTask != null) {
                _startTask.cancel(true);
                _startTask = null;
            }
            ServerVariables.unset("LastHunter");
            _log.info("Event 'Last Hunter' deactivated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.LastHunter.AnnounceEventStoped", null);
        } else
            player.sendMessage("Event 'LastHunter' not active.");

        _active = false;

        show(Files.read("data/html/admin/events.htm", player), player);
    }

    public static boolean isRunned() {
        return _isRegistrationActive || _status > 0;
    }

    public void start(String[] var) {
        L2Player player = (L2Player) getSelf();
        if (var.length != 2) {
            if (player != null)
                player.sendMessage(new CustomMessage("common.Error", player));
            return;
        }

        int category;
        int autoContinue;
        try {
            category = Integer.parseInt(var[0]);
            autoContinue = Integer.parseInt(var[1]);
        } catch (Exception e) {
            if (player != null)
                player.sendMessage(new CustomMessage("common.Error", player));
            return;
        }

        _category = category;
        _autoContinue = autoContinue;

        if (_category == -1) {
            _minLevel = 1;
            _maxLevel = 85;
        } else {
            _minLevel = minLevelForCategory[_category - 1];
            _maxLevel = maxLevelForCategory[_category - 1];
        }

        if (_endTask != null) {
            if (player != null)
                player.sendMessage(new CustomMessage("common.TryLater", player));
            return;
        }
        reflection = new Reflection("LHunterInstances");
        reflection.setGeoIndex(GeoEngine.NextGeoIndex(20, 17, reflection.getId()));

        _status = 0;
        _isRegistrationActive = true;
        _time_to_start = 1;

        players_list = new GCSArray<>();
        live_list = new GCSArray<>();

        String[] param = {String.valueOf(_time_to_start), String.valueOf(_minLevel), String.valueOf(_maxLevel)};
        sayToAll("Старт ивента");

        executeTask("events.LastHunter.LastHunter", "question", new Object[0], 10000);
        executeTask("events.LastHunter.LastHunter", "announce", new Object[0], 60000);
    }

    public static void sayToAll(String text) {
        L2ObjectsStorage.getPlayers().stream()
                .filter(Objects::nonNull)
                .forEach(p -> new Say2(0, Say2C.CRITICAL_ANNOUNCEMENT, EVENT_NAME, text));
    }

    public static void question() {
        for (L2Player player : L2ObjectsStorage.getPlayers())
            if (player != null && player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel && player.getReflection().getId() <= 0 && !player.isInOlympiadMode() && !player.isDead() && !player.isInZone(ZoneType.epic) && !player.isFlying() && player.getVar("jailed") == null && player.getVarB("event_invite", true))
                player.scriptRequest("Регистрация на ивент " + EVENT_NAME, "events.LastHunter.LastHunter:addPlayer", new Object[0]);
    }

    public static void announce() {
        if (players_list.size() < 1) {
            sayToAll("ивент отменен, недостаточно участников");
            _isRegistrationActive = false;
            _status = 0;
            executeTask("events.LastHunter.LastHunter", "autoContinue", new Object[0], 10000);
            if (!players_list.isEmpty()) {
                for (HardReference<L2Player> ref : players_list) {
                    L2Player p = ref.get();
                    if (p == null)
                        continue;
                    p.setEventReg(false);
                }
            }
            players_list.clear();
            return;
        }

        if (_time_to_start > 1) {
            _time_to_start--;
            String[] param = {String.valueOf(_time_to_start), String.valueOf(_minLevel), String.valueOf(_maxLevel)};
            sayToAll("Старт ивента");
            executeTask("events.LastHunter.LastHunter", "announce", new Object[0], 60000);
        } else {
            _status = 1;
            _isRegistrationActive = false;
            sayToAll("Подготовка ивента");
            executeTask("events.LastHunter.LastHunter", "prepare", new Object[0], 5000);
        }
    }

    public void addPlayer() {
        L2Player player = (L2Player) getSelf();
        if (player == null || !checkPlayer(player, true))
            return;


        //   IP/HWID
//
//        if (ConfigValue.LastHeroIP) {
//            for (HardReference<L2Player> ref : players_list) {
//                L2Player p = ref.get();
//                if (p != null && p.getIP().equals(player.getIP())) {
//                    player.sendMessage("Игрок с данным IP уже зарегистрирован.");
//                    return;
//                }
//            }
//        }
//
//        if (ConfigValue.LastHeroHWID) {
//            for (HardReference<L2Player> ref : players_list) {
//                L2Player p = ref.get();
//                if (p != null && p.getHWIDs().equals(player.getHWIDs())) {
//                    player.sendMessage("С данного компьютера уже зарегистрирован 1 игрок.");
//                    return;
//                }
//            }
//        }
        player.setEventReg(true);
        if (players_list.size() < 16) {
            players_list.add(player.getRef());
            live_list.add(player.getRef());

            //items_collector.put(player.getRef(), new ArrayList<>());
            player.sendMessage(new CustomMessage("scripts.events.LastHunter.Registered", player));
        } else {
            player.sendMessage("Лимит игроков превышен, ждите следующую игру.");
        }

    }

    public static boolean is_reg(L2Player player) {
        return players_list.contains(player.getRef());
    }

    public static boolean checkPlayer(L2Player player, boolean first) {
        if (first && !_isRegistrationActive) {
            player.sendMessage(new CustomMessage("scripts.events.Late", player));
            return false;
        } else if (first && players_list.contains(player.getRef())) {
            player.sendMessage(new CustomMessage("scripts.events.LastHunter.Cancelled", player));
            return false;
        } else if (first && (player.isInEvent() != 0 || player.isEventReg())) {
            player.sendMessage(new CustomMessage("scripts.events.LastHunter.OtherEvent", player).addString(player.getEventName(player.isInEvent())));
            return false;
        } else if (player.getLevel() < _minLevel || player.getLevel() > _maxLevel) {
            player.sendMessage(new CustomMessage("scripts.events.LastHunter.CancelledLevel", player));
            return false;
        } else if (player.isMounted()) {
            player.sendMessage(new CustomMessage("scripts.events.LastHunter.Cancelled", player));
            return false;
        } else if (player.getDuel() != null) {
            player.sendMessage(new CustomMessage("scripts.events.LastHunter.CancelledDuel", player));
            return false;
        } else if (player.getTeam() != 0) {
            player.sendMessage(new CustomMessage("scripts.events.LastHunter.CancelledOtherEvent", player));
            return false;
        } else if (player.getOlympiadGame() != null || player.isInZoneOlympiad() || first && Olympiad.isRegistered(player)) {
            player.sendMessage(new CustomMessage("scripts.events.LastHunter.CancelledOlympiad", player));
            return false;
        } else if (player.isInParty() && player.getParty().isInDimensionalRift()) {
            player.sendMessage(new CustomMessage("scripts.events.LastHunter.CancelledOtherEvent", player));
            return false;
        } else if (player.isTeleporting()) {
            player.sendMessage(new CustomMessage("scripts.events.LastHunter.CancelledTeleport", player));
            return false;
        } else if (player.isCursedWeaponEquipped()) {
            player.sendMessage("С проклятым оружием на эвент нельзя.");
            return false;
        } else if (player.isInOfflineMode() || player.inObserverMode() || player.isLogout())// Если игрок в обсерве то удаляем его с ивента...нехуй было туда заходить)))
            return false;
        else if (player.isInStoreMode()) {
            player.sendMessage("Во время торговли на эвент нельзя.");
            return false;
        } else if (player.getVar("jailed") != null) {
            player.sendMessage("В тюрьме на эвент нельзя");
            return false;
        } else if (player.getReflection().getId() > 0) {
            player.sendMessage("Регистрация отменена, нельзя находится во временной зоне.");
            return false;
        }
        return true;
    }

    public static void loadMessage(String msg) {
        for (HardReference<L2Player> ref : players_list) {
            L2Player player = ref.get();
            player.sendPacket(new SystemMessage(msg));
        }
    }

    public static void prepare() {
        cleanPlayers();
        clearArena();
        ressurectPlayers();
        paralyzePlayers();
        //changeStyle();

        executeTask("events.LastHunter.LastHunter", "teleportPlayersToColiseum", new Object[0], 4000);
        executeTask("events.LastHunter.LastHunter", "loadMessage", new Object[]{"Идет восстановление участников..."}, 5000);
        executeTask("events.LastHunter.LastHunter", "healPlayers", new Object[0], 6000);
        executeTask("events.LastHunter.LastHunter", "loadMessage", new Object[]{"Подготовка снаряжения..."}, 9000);
        executeTask("events.LastHunter.LastHunter", "equipArmorOnStart", new Object[0], 10000);
        executeTask("events.LastHunter.LastHunter", "loadMessage", new Object[]{"Накладываются положительные эффекты..."}, 15000);
        executeTask("events.LastHunter.LastHunter", "buffPlayerToStart", new Object[0], 16000);
        executeTask("events.LastHunter.LastHunter", "go", new Object[0], 60000);
    }

    public static void go() {
        _status = 2;
        upParalyzePlayers();
        checkLive();
        //clearArena();
        spawnPoint();
        sayToAll("В бой!!!");
        _endTask = executeTask("events.LastHunter.LastHunter", "endBattle", new Object[0], _eventDuration * 1000L);
    }

    private static void spawnPoint() {
        for (Location loc : spawnPoints) {
            Functions.spawn(loc, points[Rnd.get(points.length)], _eventDuration, reflection.getId());
        }
        for (Location loc : spawnMobs) {
            final L2NpcInstance spawn = Functions.spawn(loc, mob, 30, reflection.getId());
            mobs.add(spawn);
            spawn.setHideName(true);
        }
    }

    public static void removeBuff() {
        for (HardReference<L2Player> ref : players_list) {
            L2Player player = ref.get();
            if (player != null) {
                try {
                    if (player.isCastingNow())
                        player.abortCast(true);
                    player.getEffectList().stopAllEffects();
                    if (player.getPet() != null) {
                        L2Summon summon = player.getPet();
                        summon.getEffectList().stopAllEffects();
                        if (summon.isPet())
                            summon.unSummon();
                    }
                    if (player.getAgathion() != null)
                        player.setAgathion(0);
                    player.sendPacket(new SkillList(player));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void endBattle() {
        _status = 0;

        if (live_list.size() == 1) {
            for (HardReference<L2Player> ref : live_list) {
                L2Player player = ref.get();
                if (player == null)
                    continue;
                sayToAll("победитель ивента " + player.getName());
                addItem(player, bonusItemID, bonusItemCount);
                if (setHero) {
                    setHero(player);
                }
                if (player.getAttainment() != null)
                    player.getAttainment().event_battle_end(1, true);
                break;
            }
        } else if (winMaxDamager) {
            int max_damage = 0;
            L2Player max_damager = null;
            for (HardReference<L2Player> ref : live_list) {
                L2Player player = ref.get();
                if (player == null)
                    continue;
                if (max_damage < player.getDamageMy()) {
                    max_damage = player.getDamageMy();
                    max_damager = ref.get();
                }
            }
            if (max_damager != null) {
                sayToAll("победитель ивента " + max_damager.getName());
                addItem(max_damager, bonusItemID, bonusItemCount);
                if (setHero) {
                    setHero(max_damager);
                }
                if (max_damager.getAttainment() != null)
                    max_damager.getAttainment().event_battle_end(1, true);

            }
        }
        sayToAll("Ивент завершен");
        executeTask("events.LastHunter.LastHunter", "end", new Object[0], 30000);
        _isRegistrationActive = false;
        if (_endTask != null) {
            _endTask.cancel(false);
            _endTask = null;
        }
    }

    public static void setHero(L2Player player) {
        // Статус меняется только на текущую логон сессию
        if (!player.isHero()) {
            if (true) {
                player.setHero(true, 1);
                player.updatePledgeClass();
                Hero.addSkills(player);
            } else {
                long expire = System.currentTimeMillis() + (3600000);
                player.setVar("HeroEvent", String.valueOf(expire), expire);
                player.setHero(true, 1);
                player.updatePledgeClass();
                Hero.addSkills(player);
                player._heroTask = ThreadPoolManager.getInstance().schedule(new L2ObjectTasks.UnsetHero(player, 1), 3600000);
            }

            player.sendPacket(new SkillList(player));
            if (player.isHero()) {
                player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
                Announcements.getInstance().announceToAll(player.getName() + " has become a hero.");
            }
            player.broadcastUserInfo(true);
        }
    }

    public static void end() {
        ressurectPlayers();
        healPlayers();
        executeTask("events.LastHunter.LastHunter", "teleportPlayersToSavedCoords", new Object[0], 3000);
        executeTask("events.LastHunter.LastHunter", "autoContinue", new Object[0], 10000);
        for (L2NpcInstance npc : mobs) {
            npc.deleteMe();
        }
    }

    public void autoContinue() {
        if (reflection != null) {
            reflection.startCollapseTimer(1);
            reflection = null;
        }
        if (_autoContinue > 0) {
            if (_autoContinue >= minLevelForCategory.length) {
                _autoContinue = 0;
                startTimerTask();
                return;
            }
            start(new String[]{"" + (_autoContinue + 1), "" + (_autoContinue + 1)});
        } else
            startTimerTask();
    }

    public void startTimerTask() {
        long delay = 0;
        long cur_time = System.currentTimeMillis();

        while (time2.size() != 0 && (delay = time2.remove(0)) - cur_time <= 0) ;
        if (_startTask != null)
            _startTask.cancel(true);
        if (delay - cur_time > 0)
            _startTask = ThreadPoolManager.getInstance().schedule(new StartTask(), delay - cur_time);
        else
            initTimer(true);
    }

    public static void teleportPlayersToColiseum() {
        for (int i = 0; i < players_list.size(); i++) {
            HardReference<L2Player> ref = players_list.get(i);
            L2Player player = ref.get();
            if (player == null)
                continue;
            unRide(player);
            unSummonPet(player, true);
            player.sendPacket(new ExSendUIEvent(player, false, false, 60, 0, "Death Lock"));
            Location pos = spawnLocations.get(i);
            player.setIsInEvent((byte) 16);
            player.can_create_party = false;
            player.setVar("backCoords", player.getLoc().toXYZString());
            player.setReflection(reflection);
            player.teleToLocation(pos.x, pos.y, pos.z);
            player.addZone(_zone);
        }
    }

    private static void lockItems(L2Player player) {
        TIntArrayList items = new TIntArrayList();
        for (L2ItemInstance item : player.getInventory().getItems()) {
            items.add(item.getItemId());
        }
        player.getInventory().lockItems(LockType.INCLUDE, items.toArray());
    }

    public static void teleportPlayersToSavedCoords() {
        for (HardReference<L2Player> ref : players_list) {
            L2Player player = ref.get();
            if (player == null)
                continue;
            try {
                player.setTeam(0, false);
                player._poly_id = 0 << 24;
                player.setIsInEvent((byte) 0);
                player.setEventReg(false);
                player.sendPacket(new ExSendUIEvent(player, true, false, 10, 0, "Finish"));
                destroyArmor(player);
                destroyWeapon(player);
                equipItems(player);
                player.getInventory().unlock();
                player.can_create_party = true;
                player.setIsInvul(false);
                player.getEffectList().stopAllEffects();
                if (player.getPet() != null) {
                    L2Summon summon = player.getPet();
                    summon.getEffectList().stopAllEffects();
                }
                String back = player.getVar("backCoords");
                if (back != null) {
                    player.unsetVar("backCoords");
                    player.unsetVar("reflection");
                    player.teleToLocation(new Location(back), 0);
                }
            } catch (Exception e) {
                player.teleToLocation(147800, -55320, -2728, 0);
                player.unsetVar("backCoords");
                player.unsetVar("reflection");
                e.printStackTrace();
            }
        }
    }

    public static void paralyzePlayers() {
        removeBuff();
        L2Skill revengeSkill = SkillTable.getInstance().getInfo(L2Skill.SKILL_RAID_CURSE, 1);
        for (HardReference<L2Player> ref : players_list) {
            L2Player player = ref.get();
            if (player == null)
                continue;
            player.getEffectList().stopEffect(L2Skill.SKILL_MYSTIC_IMMUNITY);
            player.getEffectList().stopEffect(1540);
            player.getEffectList().stopEffect(1418);
            player.getEffectList().stopEffect(396);
            player.getEffectList().stopEffect(914);
			/*revengeSkill.getEffects(player, player, false, false);
			if(player.getPet() != null)
				revengeSkill.getEffects(player, player.getPet(), false, false);*/
            player.p_block_move(true, null);
            // player.block_hp_mp(true);
            // player.startAMuted();
            // player.startPMuted();
            // player.setParalyzedSkill(true);
            if (player.getPet() != null) {
                player.getPet().p_block_move(true, null);
                player.getPet().block_hp_mp(true);
                player.getPet().startAMuted();
                player.getPet().startPMuted();
                player.getPet().setParalyzedSkill(true);
            }
            player.setInvisible(true);
            player.sendUserInfo(true);
            if (player.getCurrentRegion() != null)
                for (L2WorldRegion neighbor : player.getCurrentRegion().getNeighbors())
                    neighbor.removePlayerFromOtherPlayers(player);
            player.clearHateList(true);
        }
    }

    public static void upParalyzePlayers() {
        for (HardReference<L2Player> ref : players_list) {
            L2Player player = ref.get();
            if (player != null) {
				/*player.getEffectList().stopEffect(L2Skill.SKILL_RAID_CURSE);
				if(player.getPet() != null)
					player.getPet().getEffectList().stopEffect(L2Skill.SKILL_RAID_CURSE);*/
                player.p_block_move(false, null);
                player.sendPacket(new ExSendUIEvent(player, false, false, _eventDuration, 0, "Death Lock"));
                if (player.getPet() != null) {
                    player.getPet().p_block_move(false, null);
                    player.getPet().unblock_hp_mp(true);
                    player.getPet().stopAMuted();
                    player.getPet().stopPMuted();
                    player.getPet().setParalyzedSkill(false);
                }

                if (player.getParty() != null)
                    player.getParty().oustPartyMember(player);
                player.setInvisible(false);
                player.broadcastUserInfo(true);
                if (player.getPet() != null)
                    player.getPet().broadcastPetInfo();
                player.broadcastRelationChanged();
            }
        }
    }

    public static void ressurectPlayers() {
        for (HardReference<L2Player> ref : players_list) {
            L2Player player = ref.get();
            if (player != null && player.isDead()) {
                player.restoreExp();
                player.setCurrentCp(player.getMaxCp());
                player.setCurrentHp(player.getMaxHp(), true);
                player.setCurrentMp(player.getMaxMp());
                player.broadcastPacket(new Revive(player));
            }
        }
    }

    public static void healPlayers() {
        for (HardReference<L2Player> ref : players_list) {
            L2Player player = ref.get();
            if (player != null) {
                player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
                player.setCurrentCp(player.getMaxCp());
            }
        }
    }

    public static void cleanPlayers() {
        for (HardReference<L2Player> ref : players_list) {
            L2Player player = ref.get();
            if (player != null && !checkPlayer(player, false))
                removePlayer(player);
        }
    }

    public static void checkLive() {
        GCSArray<HardReference<L2Player>> new_live_list = new GCSArray<HardReference<L2Player>>();

        for (HardReference<L2Player> ref : live_list) {
            L2Player player = ref.get();
            if (player != null)
                new_live_list.add(ref);
        }

        live_list = new_live_list;

        for (HardReference<L2Player> ref : live_list) {
            L2Player player = ref.get();
            if (player != null) {
                if (player.isInZone(_zone) && !player.isDead() && player.isConnected() && !player.isLogoutStarted()) {
                    player.setTeam(1, false);
                } else
                    loosePlayer(player);
            }
        }

        if (live_list.size() < 1)
            endBattle();
    }

    public static void clearArena() {
        for (L2Object obj : _zone.getObjects())
            if (obj != null) {
                L2Player player = obj.getPlayer();
                if (player != null && !live_list.contains(player.getRef()) && player.getReflection() == reflection)
                    player.teleToLocation(147451, 46728, -3410);
            }
    }


    public static void OnDie(L2Character self, L2Character killer) {
//        if(self.getReflectionId() == reflection.getId()) {
//            // Можно убивать мобов, которые выше 70-ого лвл, разница лвл составляет 10 лвл, не РБ
//            if (_active && self.isMonster() && killer != null && killer.getPlayer() != null) {
//                if (Rnd.chance(ConfigValue.MouseItemChanche)) {
//                    addItem(killer.getPlayer(), 57, 1_000_000);
//                }
//            }
//        }
        if (_status > 1 && self != null && self.isPlayer() && self.getTeam() > 0 && live_list.contains(self.getRef())) {
            L2Player player = (L2Player) self;
            loosePlayer(player);
            checkLive();
            final L2NpcInstance spawn = Functions.spawn(player.getLoc(), points[Rnd.get(points.length)], 120, reflection.getId());
//            if (killer != null && killer.isPlayer() && killer.getPlayer().expertiseIndex - player.expertiseIndex > 2 && !killer.getPlayer().getIP().equals(player.getIP())){
//                //Functions.spawn(player.getLoc(), points[Rnd.get(points.length)], 120, reflection.getId());
//                //addItem((L2Player) killer, ConfigValue.LastHeroBonusID, Math.round(ConfigValue.LastHeroRate ? player.getLevel() * ConfigValue.LastHeroBonusCount : 1 * ConfigValue.LastHeroBonusCount));
//            }
        }
    }


    public static Location OnEscape(L2Player player) {
        if (_status > 1 && player != null && live_list.contains(player.getRef())) {
            removePlayer(player);
            checkLive();
            destroyArmor(player);
            destroyWeapon(player);
            //final ArrayList<L2ItemInstance> l2ItemInstances = items_collector.get(player.getRef());
//            for (L2ItemInstance itemInstance: l2ItemInstances){
//                player.getInventory().destroyItem(itemInstance, 1, true);
//            }
        }
        return null;
    }

    public static void OnPlayerExit(L2Player player) {
        if (player != null && live_list.contains(player.getRef())) {
            // Вышел или вылетел во время регистрации
            if (_status == 0 && _isRegistrationActive && live_list.contains(player.getRef())) {
                removePlayer(player);
                return;
            }

            // Вышел или вылетел во время телепортации
            if (_status == 1 && live_list.contains(player.getRef())) {
                removePlayer(player);

                try {
                    player.setIsInvul(false);
                    player.getEffectList().stopAllEffects();
                    if (player.getPet() != null) {
                        L2Summon summon = player.getPet();
                        summon.getEffectList().stopAllEffects();
                    }
                    String back = player.getVar("backCoords");
                    if (back != null) {
                        player.unsetVar("backCoords");
                        player.unsetVar("reflection");
                        player.teleToLocation(new Location(back), 0);
                    }
                } catch (Exception e) {
                    player.teleToLocation(147800, -55320, -2728, 0);
                    player.unsetVar("backCoords");
                    player.unsetVar("reflection");
                    // e.printStackTrace();
                }
                return;
            }

            // Вышел или вылетел во время эвента
            OnEscape(player);
        }
    }

    private class ZoneListener extends L2ZoneEnterLeaveListener {
        @Override
        public void objectEntered(L2Zone zone, L2Object object) {
            if (object == null)
                return;
            L2Player player = object.getPlayer();
            if (_status > 0 && player != null && !live_list.contains(player.getRef()) && player.getReflection() == reflection)
                ThreadPoolManager.getInstance().schedule(new TeleportTask((L2Character) object, new Location(147451, 46728, -3410)), 3000);
        }

        @Override
        public void objectLeaved(L2Zone zone, L2Object object) {
            if (object == null)
                return;
            L2Player player = object.getPlayer();
            if (_status > 1 && player != null && player.getTeam() > 0 && live_list.contains(player.getRef()) && player.getReflection() == reflection) {
                double angle = Util.convertHeadingToDegree(object.getHeading()); // угол
                // в
                // градусах
                double radian = Math.toRadians(angle - 90); // угол в радианах
                int x = (int) (object.getX() + 50 * Math.sin(radian));
                int y = (int) (object.getY() - 50 * Math.cos(radian));
                int z = object.getZ();
                ThreadPoolManager.getInstance().schedule(new TeleportTask((L2Character) object, new Location(x, y, z)), 3000);
            }
        }
    }

    public class TeleportTask extends l2open.common.RunnableImpl {
        Location loc;
        L2Character target;

        public TeleportTask(L2Character target, Location loc) {
            this.target = target;
            this.loc = loc;
            target.startStunning();
        }

        public void runImpl() {
            target.stopStunning();
            target.teleToLocation(loc);
        }
    }

    private static void loosePlayer(L2Player player) {
        if (player != null) {
            live_list.remove(player.getRef());
            player.setTeam(0, false);
            player._poly_id = 0 << 24;
            player.setIsInEvent((byte) 0);
            player.setEventReg(false);
            player.can_create_party = true;

            for (L2ItemInstance item : items_antidiup.get(player)) {
                if (item != null) {
                    player.getInventory().destroyItem(item, 1, true);
                }

            }
            player.sendPacket(new ExSendUIEvent(player, true, false, 10, 0, "Finish"));
            player.sendMessage(new CustomMessage("scripts.events.LastHero.YouLose", player));
            try {
                destroyArmor(player);
                destroyWeapon(player);
                equipItems(player);
                player.getInventory().unlock();
                player.setIsInvul(false);
                player.getEffectList().stopAllEffects();
                if (player.getPet() != null) {
                    L2Summon summon = player.getPet();
                    summon.getEffectList().stopAllEffects();
                }
                Location loc = new Location(player.getX(), player.getY(), player.getZ());
                Functions.spawn(loc, points[Rnd.get(points.length)], 0, player.getReflectionId());
                String back = player.getVar("backCoords");
                if (back != null) {
                    player.unsetVar("backCoords");
                    player.unsetVar("reflection");
                    player.teleToLocation(new Location(back), 0);
                }
                removePlayer(player);
                if (player.isDead()) {
                    player.restoreExp();
                    player.setCurrentCp(player.getMaxCp());
                    player.setCurrentHp(player.getMaxHp(), true);
                    player.setCurrentMp(player.getMaxMp());
                    player.broadcastPacket(new Revive(player));
                }
            } catch (Exception e) {
                player.teleToLocation(147800, -55320, -2728, 0);
                player.unsetVar("backCoords");
                player.unsetVar("reflection");
                // e.printStackTrace();
            }
        }
    }

    private static void removePlayer(L2Player player) {
        if (player != null) {
            live_list.remove(player.getRef());
            players_list.remove(player.getRef());

            player.setTeam(0, false);
            player._poly_id = 0 << 24;
            player.broadcastUserInfo(true);
            player.setIsInEvent((byte) 0);
            player.getEffectList().stopAllEffects();
            if (player.getPet() != null) {
                L2Summon summon = player.getPet();
                summon.getEffectList().stopAllEffects();
            }
            player.setEventReg(false);
            player.can_create_party = true;
            //returnStyle(player);
            player.getInventory().unlock();
        }
    }

    public void un_reg() {
        L2Player player = (L2Player) getSelf();
        if (player == null)
            return;
        else if (!_isRegistrationActive) {
            player.sendMessage("Вы не можете снять регистрацию с ивента.");
            return;
        }
        removePlayer(player);
        player.sendMessage("Вы сняли регистрацию с Last Hero.");
    }


    public void equipArmor() {
        L2Player player = (L2Player) getSelf();
        L2Item.Grade grade;
        final L2ItemInstance itemInstance = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
        if (itemInstance == null) {
            grade = L2Item.Grade.D;
        } else {
            grade = itemInstance.getCrystalType();
        }
        player.sendMessage(grade.toString());

        Integer[] set = null;
        Integer[] jewel = null;
        Map<String, Integer[]> setType = null;
        Map<String, Integer[]> jewels = jewels_sets;
        ClassId classId = player.getClassId();
        if (classId == ClassId.trooper || classId == ClassId.femaleSoldier || classId == ClassId.femaleSoulhound || classId == ClassId.femaleSoulbreaker || classId == ClassId.maleSoldier || classId == ClassId.maleSoulhound || classId == ClassId.maleSoulbreaker || classId == ClassId.inspector || classId == ClassId.judicator) {
            setType = light_sets;
        } else if (classId == ClassId.berserker || classId == ClassId.doombringer) {
            setType = light_sets;
        } else if (classId == ClassId.warder || classId == ClassId.arbalester || classId == ClassId.trickster) {
            setType = light_sets;
        } else if (classId == ClassId.fighter || classId == ClassId.darkFighter || classId == ClassId.elvenFighter || classId == ClassId.orcFighter || classId == ClassId.warrior || classId == ClassId.knight || classId == ClassId.elvenKnight || classId == ClassId.palusKnight || classId == ClassId.paladin || classId == ClassId.darkAvenger || classId == ClassId.templeKnight || classId == ClassId.shillienKnight || classId == ClassId.phoenixKnight || classId == ClassId.hellKnight || classId == ClassId.evaTemplar || classId == ClassId.shillienTemplar || classId == ClassId.swordSinger || classId == ClassId.swordMuse) {
            setType = heavy_sets;
        } else if (classId == ClassId.mage || classId == ClassId.elvenMage || classId == ClassId.darkMage || classId == ClassId.orcMage || classId == ClassId.wizard || classId == ClassId.cleric || classId == ClassId.elvenWizard || classId == ClassId.darkWizard || classId == ClassId.sorceror || classId == ClassId.archmage || classId == ClassId.necromancer || classId == ClassId.soultaker || classId == ClassId.warlock || classId == ClassId.arcanaLord || classId == ClassId.bishop || classId == ClassId.cardinal || classId == ClassId.prophet || classId == ClassId.hierophant || classId == ClassId.spellsinger || classId == ClassId.mysticMuse || classId == ClassId.elementalMaster || classId == ClassId.elementalSummoner || classId == ClassId.oracle || classId == ClassId.elder || classId == ClassId.evaSaint || classId == ClassId.spellhowler || classId == ClassId.stormScreamer || classId == ClassId.phantomSummoner || classId == ClassId.spectralMaster || classId == ClassId.shillienElder || classId == ClassId.shillienOracle || classId == ClassId.shillienSaint || classId == ClassId.orcShaman || classId == ClassId.overlord || classId == ClassId.dominator || classId == ClassId.warcryer || classId == ClassId.doomcryer) {
            setType = robe_sets;
        } else if (classId == ClassId.rogue || classId == ClassId.treasureHunter || classId == ClassId.adventurer || classId == ClassId.elvenScout || classId == ClassId.plainsWalker || classId == ClassId.windRider || classId == ClassId.assassin || classId == ClassId.abyssWalker || classId == ClassId.ghostHunter) {
            setType = light_sets;
        } else if (classId == ClassId.hawkeye || classId == ClassId.sagittarius || classId == ClassId.silverRanger || classId == ClassId.moonlightSentinel || classId == ClassId.phantomRanger || classId == ClassId.ghostSentinel) {
            setType = light_sets;
        } else if (classId == ClassId.orcRaider || classId == ClassId.destroyer || classId == ClassId.titan) {
            setType = heavy_sets;
        } else if (classId == ClassId.orcMonk || classId == ClassId.tyrant || classId == ClassId.grandKhauatari) {
            setType = light_sets;
        } else if (classId == ClassId.gladiator || classId == ClassId.duelist || classId == ClassId.bladedancer || classId == ClassId.spectralDancer) {
            setType = heavy_sets;
        } else if (classId == ClassId.dwarvenFighter || classId == ClassId.artisan || classId == ClassId.warsmith || classId == ClassId.maestro || classId == ClassId.scavenger || classId == ClassId.bountyHunter || classId == ClassId.fortuneSeeker) {
            setType = heavy_sets;
        } else if (classId == ClassId.warlord || classId == ClassId.dreadnought) {
            setType = heavy_sets;
        }

        if (grade == L2Item.Grade.C) {
            set = setType.get("B");
            jewel = jewels.get("B");
        } else if (grade == L2Item.Grade.B) {
            set = setType.get("A");
            jewel = jewels.get("A");
        } else if (grade == L2Item.Grade.A) {
            set = setType.get("S80");
            jewel = jewels.get("S80");
        } else if (grade == L2Item.Grade.D) {
            set = setType.get("C");
            jewel = jewels.get("C");
        }
        if (set != null && jewel != null) {
            destroyArmor(player);
            for (int i : set) {
                addAndEquipItem(player, i);
            }
            for (int i : jewel) {
                addAndEquipItem(player, i);
            }
        }


        //Блокировка всех итемов в инвенторе
        unlockItems(player);
        lockItems(player);
        playerHeal();
        player.broadcastPacket(new InventoryUpdate());
        //player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.CHARM));

        L2NpcInstance npc = getNpc();
        npc.deleteMe();
    }

    public void equipWeapon() {
        L2Player player = (L2Player) getSelf();
        L2Item.Grade grade;
        final L2ItemInstance itemInstance = player.getActiveWeaponInstance();
        if (itemInstance == null) {
            grade = L2Item.Grade.D;
        } else {
            grade = itemInstance.getCrystalType();
        }
        player.sendMessage(grade.toString());
        destroyWeapon(player);
        Integer[] weapons = null;
        Integer[] shield = null;
        ClassId classId = player.getClassId();
        if (classId == ClassId.trooper || classId == ClassId.femaleSoldier || classId == ClassId.femaleSoulhound || classId == ClassId.femaleSoulbreaker || classId == ClassId.maleSoldier || classId == ClassId.maleSoulhound || classId == ClassId.maleSoulbreaker || classId == ClassId.inspector || classId == ClassId.judicator) {
            weapons = weapons_sets.get(WeaponType.RAPIER);
        } else if (classId == ClassId.berserker || classId == ClassId.doombringer) {
            weapons = weapons_sets.get(WeaponType.ANCIENTSWORD);
        } else if (classId == ClassId.warder || classId == ClassId.arbalester || classId == ClassId.trickster) {
            weapons = weapons_sets.get(WeaponType.CROSSBOW);
        } else if (classId == ClassId.fighter || classId == ClassId.darkFighter || classId == ClassId.elvenFighter || classId == ClassId.orcFighter || classId == ClassId.warrior || classId == ClassId.knight || classId == ClassId.elvenKnight || classId == ClassId.palusKnight || classId == ClassId.paladin || classId == ClassId.darkAvenger || classId == ClassId.templeKnight || classId == ClassId.shillienKnight || classId == ClassId.phoenixKnight || classId == ClassId.hellKnight || classId == ClassId.evaTemplar || classId == ClassId.shillienTemplar || classId == ClassId.swordSinger || classId == ClassId.swordMuse) {
            weapons = weapons_sets.get(WeaponType.SWORD);
            shield = weapons_sets.get(WeaponType.SHIELD);
        } else if (classId == ClassId.mage || classId == ClassId.elvenMage || classId == ClassId.darkMage || classId == ClassId.orcMage || classId == ClassId.wizard || classId == ClassId.cleric || classId == ClassId.elvenWizard || classId == ClassId.darkWizard || classId == ClassId.sorceror || classId == ClassId.archmage || classId == ClassId.necromancer || classId == ClassId.soultaker || classId == ClassId.warlock || classId == ClassId.arcanaLord || classId == ClassId.bishop || classId == ClassId.cardinal || classId == ClassId.prophet || classId == ClassId.hierophant || classId == ClassId.spellsinger || classId == ClassId.mysticMuse || classId == ClassId.elementalMaster || classId == ClassId.elementalSummoner || classId == ClassId.oracle || classId == ClassId.elder || classId == ClassId.evaSaint || classId == ClassId.spellhowler || classId == ClassId.stormScreamer || classId == ClassId.phantomSummoner || classId == ClassId.spectralMaster || classId == ClassId.shillienElder || classId == ClassId.shillienOracle || classId == ClassId.shillienSaint || classId == ClassId.orcShaman || classId == ClassId.overlord || classId == ClassId.dominator || classId == ClassId.warcryer || classId == ClassId.doomcryer) {
            weapons = weapons_sets.get(WeaponType.MAGESWORD);
            shield = weapons_sets.get(WeaponType.SHIELD);
        } else if (classId == ClassId.rogue || classId == ClassId.treasureHunter || classId == ClassId.adventurer || classId == ClassId.elvenScout || classId == ClassId.plainsWalker || classId == ClassId.windRider || classId == ClassId.assassin || classId == ClassId.abyssWalker || classId == ClassId.ghostHunter) {
            weapons = weapons_sets.get(WeaponType.DAGGER);
        } else if (classId == ClassId.hawkeye || classId == ClassId.sagittarius || classId == ClassId.silverRanger || classId == ClassId.moonlightSentinel || classId == ClassId.phantomRanger || classId == ClassId.ghostSentinel) {
            weapons = weapons_sets.get(WeaponType.BOW);
        } else if (classId == ClassId.orcRaider || classId == ClassId.destroyer || classId == ClassId.titan) {
            weapons = weapons_sets.get(WeaponType.BIGSWORD);
        } else if (classId == ClassId.orcMonk || classId == ClassId.tyrant || classId == ClassId.grandKhauatari) {
            weapons = weapons_sets.get(WeaponType.FIST);
        } else if (classId == ClassId.gladiator || classId == ClassId.duelist || classId == ClassId.bladedancer || classId == ClassId.spectralDancer) {
            weapons = weapons_sets.get(WeaponType.DUAL);
        } else if (classId == ClassId.dwarvenFighter || classId == ClassId.artisan || classId == ClassId.warsmith || classId == ClassId.maestro || classId == ClassId.scavenger || classId == ClassId.bountyHunter || classId == ClassId.fortuneSeeker) {
            weapons = weapons_sets.get(WeaponType.BLUNT);
            shield = weapons_sets.get(WeaponType.SHIELD);
        } else if (classId == ClassId.warlord || classId == ClassId.dreadnought) {
            weapons = weapons_sets.get(WeaponType.POLE);
        }
        if (weapons != null) {
            if (grade == L2Item.Grade.C) {
                addAndEquipItem(player, weapons[1]);
                if (shield != null) {
                    addAndEquipItem(player, shield[1]);
                }
            } else if (grade == L2Item.Grade.B) {
                addAndEquipItem(player, weapons[2]);
                if (shield != null) {
                    addAndEquipItem(player, shield[2]);
                }
            } else if (grade == L2Item.Grade.A) {
                addAndEquipItem(player, weapons[3]);
                if (shield != null) {
                    addAndEquipItem(player, shield[3]);
                }
            } else if (grade == L2Item.Grade.D) {
                addAndEquipItem(player, weapons[0]);
                if (shield != null) {
                    addAndEquipItem(player, shield[0]);
                }
            } else if (grade == L2Item.Grade.S80) {
                addAndEquipItem(player, weapons[3]);
                if (shield != null) {
                    addAndEquipItem(player, shield[3]);
                }
            }
        }

        //Блокировка всех итемов в инвенторе
        unlockItems(player);
        lockItems(player);

        playerHeal();
        //player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.VICTORY));

        L2NpcInstance npc = getNpc();
        npc.deleteMe();
    }

    public void buffPlayer(String[] s) {

        L2Player player = (L2Player) getSelf();
        int[] buff = {1204, 2,        //Wind Walk
        };
        if (player != null) {
            if (!player.getEffectList().containEffectFromSkills(new int[]{1044})) {  //проверка на баф Regeneration
                buff = firstBuffs;
            } else if (!player.getEffectList().containEffectFromSkills(new int[]{1354})) {   //проверка на баф Divine Protection
                buff = secondBuffs;
            } else if (!player.getEffectList().containEffectFromSkills(new int[]{264})) {   //проверка на баф Song of Earth
                if ("mag".equalsIgnoreCase(s[0])) {
                    buff = threeBuffsMag;
                } else {
                    buff = threeBuffsFiz;
                }
            } else if (!player.getEffectList().containEffectFromSkills(new int[]{1542})) {   //проверка на баф Counter Critical
                if ("mag".equalsIgnoreCase(s[0])) {
                    buff = fourBuffsMag;
                } else {
                    buff = fourBuffsFiz;
                }
                player.sendMessage("4 buff");
            }
            for (int i = 0; i < buff.length; i += 2) {
                L2Skill skill = SkillTable.getInstance().getInfo(buff[i], buff[i + 1]);

                if (!skill.checkSkillAbnormal(player) && !skill.isBlockedByChar(player, skill))
                    for (EffectTemplate et : skill.getEffectTemplates()) {
                        Env env = new Env(player, player, skill);
                        L2Effect effect = et.getEffect(env);
                        if (effect != null && effect.getCount() == 1 && effect.getTemplate()._instantly && !effect.getSkill().isToggle()) {
                            effect.onStart();
                            effect.onActionTime();
                            effect.onExit();
                        } else if (effect != null) {
                            effect.setPeriod(_eventDuration * 2 * 1000);
                            player.getEffectList().addEffect(effect);
                        }
                    }
            }
            if (player.getPet() != null) {
                L2Summon summon = player.getPet();
                if (!summon.getEffectList().containEffectFromSkills(new int[]{1044})) {  //проверка на баф Regeneration
                    buff = firstBuffs;
                } else if (!player.getEffectList().containEffectFromSkills(new int[]{1354})) {   //проверка на баф Divine Protection
                    buff = secondBuffs;
                } else if (!summon.getEffectList().containEffectFromSkills(new int[]{264})) {   //проверка на баф Song of Earth
                    buff = threeBuffsFiz;
                } else if (!summon.getEffectList().containEffectFromSkills(new int[]{1542})) {   //проверка на баф Counter Critical
                    buff = fourBuffsFiz;
                }
                for (int i = 0; i < buff.length; i += 2) {
                    L2Skill skill = SkillTable.getInstance().getInfo(buff[i], buff[i + 1]);
                    if (!skill.checkSkillAbnormal(summon) && !skill.isBlockedByChar(summon, skill))
                        for (EffectTemplate et : skill.getEffectTemplates()) {
                            Env env = new Env(summon, summon, skill);
                            L2Effect effect = et.getEffect(env);
                            if (effect != null && effect.getCount() == 1 && effect.getTemplate()._instantly && !effect.getSkill().isToggle()) {
                                effect.onStart();
                                effect.onActionTime();
                                effect.onExit();
                            } else if (effect != null) {
                                effect.setPeriod(_eventDuration * 2 * 1000);
                                summon.getEffectList().addEffect(effect);
                            }
                        }
                }
            }

            player.updateEffectIcons();
            player.setCurrentCp(player.getMaxCp());
            player.setCurrentHp(player.getMaxHp(), false);
            player.setCurrentMp(player.getMaxMp());
        }
        L2NpcInstance npc = getNpc();
        npc.deleteMe();
    }

    public static void destroyArmor(L2Player player) {
        player.getInventory().destroyItem(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HEAD), 1, true);
        player.getInventory().destroyItem(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_CHEST), 1, true);
        player.getInventory().destroyItem(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LEGS), 1, true);
        player.getInventory().destroyItem(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_GLOVES), 1, true);
        player.getInventory().destroyItem(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_FEET), 1, true);

        player.getInventory().destroyItem(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LEAR), 1, true);
        player.getInventory().destroyItem(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_REAR), 1, true);
        player.getInventory().destroyItem(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_NECK), 1, true);
        player.getInventory().destroyItem(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LFINGER), 1, true);
        player.getInventory().destroyItem(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RFINGER), 1, true);
    }

    public static void destroyWeapon(L2Player player) {
        final L2Item.Grade crystalType;
        if (player.getActiveWeaponInstance() != null) {
            crystalType = player.getActiveWeaponInstance().getCrystalType();
            int i = 0;
            if (crystalType == L2Item.Grade.C) {
                i = 0;
            } else if (crystalType == L2Item.Grade.B) {
                i = 1;
            } else if (crystalType == L2Item.Grade.A) {
                i = 2;
            } else if (crystalType == L2Item.Grade.S || crystalType == L2Item.Grade.S80 || crystalType == L2Item.Grade.S84) {
                i = 3;
            }
            player.getInventory().destroyItem(player.getInventory().getItemByItemId(soulshot[i]), 1000, false);
            player.getInventory().destroyItem(player.getInventory().getItemByItemId(spiritshot[i]), 1000, false);
            player.getInventory().destroyItem(player.getInventory().getItemByItemId(arrows[i]), 1000, false);
            player.getInventory().destroyItem(player.getInventory().getItemByItemId(bolts[i]), 1000, false);
            player.getInventory().destroyItem(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1, true);
            player.getInventory().destroyItem(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RHAND), 1, true);
            player.getInventory().destroyItem(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LRHAND), 1, true);
        }
    }

    public void equipArmorOnStart() {
        for (HardReference<L2Player> ref : players_list) {
            L2Player player = ref.get();
            if (player != null) {
                //Снятья доспехов
                unEquipItem(player, Inventory.PAPERDOLL_HEAD);
                unEquipItem(player, Inventory.PAPERDOLL_CHEST);
                unEquipItem(player, Inventory.PAPERDOLL_LEGS);
                unEquipItem(player, Inventory.PAPERDOLL_GLOVES);
                unEquipItem(player, Inventory.PAPERDOLL_FEET);
                //Снятья бижу
                unEquipItem(player, Inventory.PAPERDOLL_REAR);
                unEquipItem(player, Inventory.PAPERDOLL_LEAR);
                unEquipItem(player, Inventory.PAPERDOLL_NECK);
                unEquipItem(player, Inventory.PAPERDOLL_RFINGER);
                unEquipItem(player, Inventory.PAPERDOLL_LFINGER);
                //Снятья оружия
                unEquipItem(player, Inventory.PAPERDOLL_LHAND);
                unEquipItem(player, Inventory.PAPERDOLL_RHAND);
                unEquipItem(player, Inventory.PAPERDOLL_LRHAND);
//                if (player.getRace() != Race.kamael) {
//                    player.getInventory().unEquipItemInBodySlot(L2Item.SLOT_SIGIL, null);
//                }

                Integer[] weapons = null;
                Integer[] shield = null;
                Map<String, Integer[]> setType = null;
                ClassId classId = player.getClassId();

                if (classId == ClassId.trooper || classId == ClassId.femaleSoldier || classId == ClassId.femaleSoulhound || classId == ClassId.femaleSoulbreaker || classId == ClassId.maleSoldier || classId == ClassId.maleSoulhound || classId == ClassId.maleSoulbreaker || classId == ClassId.inspector || classId == ClassId.judicator) {
                    weapons = weapons_sets.get(WeaponType.RAPIER);
                    setType = light_sets;
                } else if (classId == ClassId.berserker || classId == ClassId.doombringer) {
                    weapons = weapons_sets.get(WeaponType.ANCIENTSWORD);
                    setType = light_sets;
                } else if (classId == ClassId.warder || classId == ClassId.arbalester || classId == ClassId.trickster) {
                    weapons = weapons_sets.get(WeaponType.CROSSBOW);
                    setType = light_sets;
                } else if (classId == ClassId.fighter || classId == ClassId.darkFighter || classId == ClassId.elvenFighter || classId == ClassId.orcFighter || classId == ClassId.warrior || classId == ClassId.knight || classId == ClassId.elvenKnight || classId == ClassId.palusKnight || classId == ClassId.paladin || classId == ClassId.darkAvenger || classId == ClassId.templeKnight || classId == ClassId.shillienKnight || classId == ClassId.phoenixKnight || classId == ClassId.hellKnight || classId == ClassId.evaTemplar || classId == ClassId.shillienTemplar || classId == ClassId.swordSinger || classId == ClassId.swordMuse) {
                    weapons = weapons_sets.get(WeaponType.SWORD);
                    setType = heavy_sets;
                    shield = weapons_sets.get(WeaponType.SHIELD);
                } else if (classId == ClassId.mage || classId == ClassId.elvenMage || classId == ClassId.darkMage || classId == ClassId.orcMage || classId == ClassId.wizard || classId == ClassId.cleric || classId == ClassId.elvenWizard || classId == ClassId.darkWizard || classId == ClassId.sorceror || classId == ClassId.archmage || classId == ClassId.necromancer || classId == ClassId.soultaker || classId == ClassId.warlock || classId == ClassId.arcanaLord || classId == ClassId.bishop || classId == ClassId.cardinal || classId == ClassId.prophet || classId == ClassId.hierophant || classId == ClassId.spellsinger || classId == ClassId.mysticMuse || classId == ClassId.elementalMaster || classId == ClassId.elementalSummoner || classId == ClassId.oracle || classId == ClassId.elder || classId == ClassId.evaSaint || classId == ClassId.spellhowler || classId == ClassId.stormScreamer || classId == ClassId.phantomSummoner || classId == ClassId.spectralMaster || classId == ClassId.shillienElder || classId == ClassId.shillienOracle || classId == ClassId.shillienSaint || classId == ClassId.orcShaman || classId == ClassId.overlord || classId == ClassId.dominator || classId == ClassId.warcryer || classId == ClassId.doomcryer) {
                    weapons = weapons_sets.get(WeaponType.MAGESWORD);
                    setType = robe_sets;
                    shield = weapons_sets.get(WeaponType.SHIELD);
                } else if (classId == ClassId.rogue || classId == ClassId.treasureHunter || classId == ClassId.adventurer || classId == ClassId.elvenScout || classId == ClassId.plainsWalker || classId == ClassId.windRider || classId == ClassId.assassin || classId == ClassId.abyssWalker || classId == ClassId.ghostHunter) {
                    weapons = weapons_sets.get(WeaponType.DAGGER);
                    setType = light_sets;
                } else if (classId == ClassId.hawkeye || classId == ClassId.sagittarius || classId == ClassId.silverRanger || classId == ClassId.moonlightSentinel || classId == ClassId.phantomRanger || classId == ClassId.ghostSentinel) {
                    weapons = weapons_sets.get(WeaponType.BOW);
                    setType = light_sets;
                } else if (classId == ClassId.orcRaider || classId == ClassId.destroyer || classId == ClassId.titan) {
                    weapons = weapons_sets.get(WeaponType.BIGSWORD);
                    setType = heavy_sets;
                } else if (classId == ClassId.orcMonk || classId == ClassId.tyrant || classId == ClassId.grandKhauatari) {
                    weapons = weapons_sets.get(WeaponType.FIST);
                    setType = light_sets;
                } else if (classId == ClassId.gladiator || classId == ClassId.duelist || classId == ClassId.bladedancer || classId == ClassId.spectralDancer) {
                    weapons = weapons_sets.get(WeaponType.DUAL);
                    setType = heavy_sets;
                } else if (classId == ClassId.dwarvenFighter || classId == ClassId.artisan || classId == ClassId.warsmith || classId == ClassId.maestro || classId == ClassId.scavenger || classId == ClassId.bountyHunter || classId == ClassId.fortuneSeeker) {
                    weapons = weapons_sets.get(WeaponType.BLUNT);
                    setType = heavy_sets;
                    shield = weapons_sets.get(WeaponType.SHIELD);
                } else if (classId == ClassId.warlord || classId == ClassId.dreadnought) {
                    weapons = weapons_sets.get(WeaponType.POLE);
                    setType = heavy_sets;
                }

                //Надеваем оружие
                if (weapons != null) {
                    addAndEquipItem(player, weapons[0]);
                }
                if (shield != null) {
                    addAndEquipItem(player, shield[0]);
                }
                //Надеваем сэт
                if (setType != null) {
                    for (int i : setType.get("C")) {
                        addAndEquipItem(player, i);
                    }
                }
                //Надеваем бижу
                if (setType != null) {
                    for (int i : jewels_sets.get("C")) {
                        addAndEquipItem(player, i);
                    }
                }


                //Блокировка всех итемов в инвенторе
                unlockItems(player);
                lockItems(player);
            }
        }
    }

    public void buffPlayerToStart() {
        for (HardReference<L2Player> ref : players_list) {
            L2Player player = ref.get();
            if (player != null) {
                for (int i = 0; i < firstBuffs.length; i += 2) {
                    L2Skill skill = SkillTable.getInstance().getInfo(firstBuffs[i], firstBuffs[i + 1]);
                    if (!skill.checkSkillAbnormal(player) && !skill.isBlockedByChar(player, skill))
                        for (EffectTemplate et : skill.getEffectTemplates()) {
                            Env env = new Env(player, player, skill);
                            L2Effect effect = et.getEffect(env);
                            if (effect != null && effect.getCount() == 1 && effect.getTemplate()._instantly && !effect.getSkill().isToggle()) {
                                effect.onStart();
                                effect.onActionTime();
                                effect.onExit();
                            } else if (effect != null) {
                                effect.setPeriod(_eventDuration * 2 * 1000);
                                player.getEffectList().addEffect(effect);
                            }
                        }
                }
                if (player.getPet() != null) {
                    L2Summon summon = player.getPet();
                    for (int i = 0; i < firstBuffs.length; i += 2) {
                        L2Skill skill = SkillTable.getInstance().getInfo(firstBuffs[i], firstBuffs[i + 1]);
                        if (!skill.checkSkillAbnormal(summon) && !skill.isBlockedByChar(summon, skill))
                            for (EffectTemplate et : skill.getEffectTemplates()) {
                                Env env = new Env(summon, summon, skill);
                                L2Effect effect = et.getEffect(env);
                                if (effect != null && effect.getCount() == 1 && effect.getTemplate()._instantly && !effect.getSkill().isToggle()) {
                                    effect.onStart();
                                    effect.onActionTime();
                                    effect.onExit();
                                } else if (effect != null) {
                                    effect.setPeriod(_eventDuration * 2 * 1000);
                                    summon.getEffectList().addEffect(effect);
                                }
                            }
                    }
                }
                player.updateEffectIcons();
                player.setCurrentCp(player.getMaxCp());
                player.setCurrentHp(player.getMaxHp(), false);
                player.setCurrentMp(player.getMaxMp());
            }
        }
    }
}