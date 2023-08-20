package bosses;

import javolution.util.FastMap;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;
import npc.model.L2SepulcherMonsterInstance;
import npc.model.L2SepulcherNpcInstance;
import npc.model.L2SepulcherRaidInstance;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Logger;

public class FourSepulchersSpawn extends Functions implements ScriptFile
{
    private static Logger _log = Logger.getLogger(FourSepulchersSpawn.class.getName());

    public static FastMap<Integer, Location> _shadowSpawns = new FastMap<Integer, Location>();
    public static FastMap<Integer, Location> _mysteriousBoxSpawns = new FastMap<Integer, Location>();
    public static FastMap<Integer, GArray<Location>> _dukeFinalMobs = new FastMap<Integer, GArray<Location>>();
    public static FastMap<Integer, GArray<Location>> _emperorsGraveNpcs = new FastMap<Integer, GArray<Location>>();
    public static FastMap<Integer, GArray<Location>> _magicalMonsters = new FastMap<Integer, GArray<Location>>();
    public static FastMap<Integer, GArray<Location>> _physicalMonsters = new FastMap<Integer, GArray<Location>>();
    public static FastMap<Integer, Location> _startHallSpawns = new FastMap<Integer, Location>();
    public static FastMap<Integer, Boolean> _hallInUse = new FastMap<Integer, Boolean>();
    public static ArrayList<GateKeeper> _GateKeepers = new ArrayList<GateKeeper>();
    public static FastMap<Integer, Integer> _keyBoxNpc = new FastMap<Integer, Integer>();
    public static FastMap<Integer, Integer> _victim = new FastMap<Integer, Integer>();
    public static FastMap<Integer, Boolean> _archonSpawned = new FastMap<Integer, Boolean>();

    public static FastMap<Integer, GArray<L2SepulcherMonsterInstance>> _dukeMobs = new FastMap<Integer, GArray<L2SepulcherMonsterInstance>>();
    public static FastMap<Integer, GArray<L2SepulcherMonsterInstance>> _viscountMobs = new FastMap<Integer, GArray<L2SepulcherMonsterInstance>>();

    public static GArray<L2SepulcherNpcInstance> _managers;

    public static GArray<L2NpcInstance> _allMobs = new GArray<L2NpcInstance>();

    private static Location[] _startHallSpawn = {
            new Location(181632, -85587, -7218),
            new Location(179963, -88978, -7218),
            new Location(173217, -86132, -7218),
            new Location(175608, -82296, -7218)
    };

    private static Location[][] _shadowSpawnLoc = {
            {
                    // x, y, z, heading, npcId
                    new Location(191231, -85574, -7216, 33380, 25339),
                    new Location(189534, -88969, -7216, 32768, 25349),
                    new Location(173195, -76560, -7215, 49277, 25346),
                    new Location(175591, -72744, -7215, 49317, 25342)
            }, {
                    new Location(191231, -85574, -7216, 33380, 25342),
                    new Location(189534, -88969, -7216, 32768, 25339),
                    new Location(173195, -76560, -7215, 49277, 25349),
                    new Location(175591, -72744, -7215, 49317, 25346)
            }, {
                    new Location(191231, -85574, -7216, 33380, 25346),
                    new Location(189534, -88969, -7216, 32768, 25342),
                    new Location(173195, -76560, -7215, 49277, 25339),
                    new Location(175591, -72744, -7215, 49317, 25349)
            }, {
                    new Location(191231, -85574, -7216, 33380, 25349),
                    new Location(189534, -88969, -7216, 32768, 25346),
                    new Location(173195, -76560, -7215, 49277, 25342),
                    new Location(175591, -72744, -7215, 49317, 25339)
            }
    };

    public static void init()
	{
        initFixedInfo();
        loadMysteriousBox();
        loadPhysicalMonsters();
        loadMagicalMonsters();
        initLocationShadowSpawns();
        loadDukeMonsters();
        loadEmperorsGraveMonsters();
        spawnManagers();
    }

    private static void initFixedInfo()
	{
        _startHallSpawns.put(31921, _startHallSpawn[0]);
        _startHallSpawns.put(31922, _startHallSpawn[1]);
        _startHallSpawns.put(31923, _startHallSpawn[2]);
        _startHallSpawns.put(31924, _startHallSpawn[3]);

        _hallInUse.put(31921, false);
        _hallInUse.put(31922, false);
        _hallInUse.put(31923, false);
        _hallInUse.put(31924, false);

        _GateKeepers.add(new GateKeeper(31925, 182727, -85493, -7200, -32584, 25150012));
        _GateKeepers.add(new GateKeeper(31926, 184547, -85479, -7200, -32584, 25150013));
        _GateKeepers.add(new GateKeeper(31927, 186349, -85473, -7200, -32584, 25150014));
        _GateKeepers.add(new GateKeeper(31928, 188154, -85463, -7200, -32584, 25150015));
        _GateKeepers.add(new GateKeeper(31929, 189947, -85466, -7200, -32584, 25150016));

        _GateKeepers.add(new GateKeeper(31930, 181030, -88868, -7200, -33272, 25150002));
        _GateKeepers.add(new GateKeeper(31931, 182809, -88856, -7200, -33272, 25150003));
        _GateKeepers.add(new GateKeeper(31932, 184626, -88859, -7200, -33272, 25150004));
        _GateKeepers.add(new GateKeeper(31933, 186438, -88858, -7200, -33272, 25150005));
        _GateKeepers.add(new GateKeeper(31934, 188236, -88854, -7200, -33272, 25150006));

        _GateKeepers.add(new GateKeeper(31935, 173102, -85105, -7200, -16248, 25150032));
        _GateKeepers.add(new GateKeeper(31936, 173101, -83280, -7200, -16248, 25150033));
        _GateKeepers.add(new GateKeeper(31937, 173103, -81479, -7200, -16248, 25150034));
        _GateKeepers.add(new GateKeeper(31938, 173086, -79698, -7200, -16248, 25150035));
        _GateKeepers.add(new GateKeeper(31939, 173083, -77896, -7200, -16248, 25150036));

        _GateKeepers.add(new GateKeeper(31940, 175497, -81265, -7200, -16248, 25150022));
        _GateKeepers.add(new GateKeeper(31941, 175495, -79468, -7200, -16248, 25150023));
        _GateKeepers.add(new GateKeeper(31942, 175488, -77652, -7200, -16248, 25150024));
        _GateKeepers.add(new GateKeeper(31943, 175489, -75856, -7200, -16248, 25150025));
        _GateKeepers.add(new GateKeeper(31944, 175478, -74049, -7200, -16248, 25150026));

        _keyBoxNpc.put(18120, 31455);
        _keyBoxNpc.put(18121, 31455);
        _keyBoxNpc.put(18122, 31455);
        _keyBoxNpc.put(18123, 31455);
        _keyBoxNpc.put(18124, 31456);
        _keyBoxNpc.put(18125, 31456);
        _keyBoxNpc.put(18126, 31456);
        _keyBoxNpc.put(18127, 31456);
        _keyBoxNpc.put(18128, 31457);
        _keyBoxNpc.put(18129, 31457);
        _keyBoxNpc.put(18130, 31457);
        _keyBoxNpc.put(18131, 31457);
        _keyBoxNpc.put(18149, 31458);
        _keyBoxNpc.put(18150, 31459);
        _keyBoxNpc.put(18151, 31459);
        _keyBoxNpc.put(18152, 31459);
        _keyBoxNpc.put(18153, 31459);
        _keyBoxNpc.put(18154, 31460);
        _keyBoxNpc.put(18155, 31460);
        _keyBoxNpc.put(18156, 31460);
        _keyBoxNpc.put(18157, 31460);
        _keyBoxNpc.put(18158, 31461);
        _keyBoxNpc.put(18159, 31461);
        _keyBoxNpc.put(18160, 31461);
        _keyBoxNpc.put(18161, 31461);
        _keyBoxNpc.put(18162, 31462);
        _keyBoxNpc.put(18163, 31462);
        _keyBoxNpc.put(18164, 31462);
        _keyBoxNpc.put(18165, 31462);
        _keyBoxNpc.put(18183, 31463);
        _keyBoxNpc.put(18184, 31464);
        _keyBoxNpc.put(18212, 31465);
        _keyBoxNpc.put(18213, 31465);
        _keyBoxNpc.put(18214, 31465);
        _keyBoxNpc.put(18215, 31465);
        _keyBoxNpc.put(18216, 31466);
        _keyBoxNpc.put(18217, 31466);
        _keyBoxNpc.put(18218, 31466);
        _keyBoxNpc.put(18219, 31466);

        _victim.put(18150, 18158);
        _victim.put(18151, 18159);
        _victim.put(18152, 18160);
        _victim.put(18153, 18161);
        _victim.put(18154, 18162);
        _victim.put(18155, 18163);
        _victim.put(18156, 18164);
        _victim.put(18157, 18165);
    }

    private static void initLocationShadowSpawns()
	{
        int locNo = Rnd.get(4);
        int[] gateKeeper = {31929, 31934, 31939, 31944};

        _shadowSpawns.clear();
        for (int i = 0; i <= 3; i++)
            _shadowSpawns.put(gateKeeper[i], _shadowSpawnLoc[locNo][i].clone());
    }

    private static void loadEmperorsGraveMonsters()
	{
        _emperorsGraveNpcs.clear();
        int count = loadSpawn(_emperorsGraveNpcs, 6);
        _log.warning("FourSepulchersManager: loaded " + count + " Emperor's grave NPC spawns.");
    }

    private static void loadDukeMonsters()
	{
        _dukeFinalMobs.clear();
        _archonSpawned.clear();
        int count = loadSpawn(_dukeFinalMobs, 5);
        for (Integer npcId : _dukeFinalMobs.keySet())
            _archonSpawned.put(npcId, false);
        _log.warning("FourSepulchersManager: loaded " + count + " Church of duke monsters spawns.");
    }

    private static void loadMagicalMonsters()
	{
        _magicalMonsters.clear();
        int count = loadSpawn(_magicalMonsters, 2);
        _log.warning("FourSepulchersManager: loaded " + count + " magical monsters spawns.");
    }

    private static void loadPhysicalMonsters()
	{
        _physicalMonsters.clear();
        int count = loadSpawn(_physicalMonsters, 1);
        _log.warning("FourSepulchersManager: loaded " + count + " physical monsters spawns.");
    }

    private static int loadSpawn(FastMap<Integer, GArray<Location>> table, int type)
	{
        int count = 0;
        ThreadConnection con = null;
        FiltredPreparedStatement statement1 = null;
        ResultSet rset1 = null;
        try
		{
            con = L2DatabaseFactory.getInstance().getConnection();
            statement1 = con.prepareStatement("SELECT DISTINCT key_npc_id FROM four_sepulchers_spawnlist WHERE spawntype = ? ORDER BY key_npc_id");
            statement1.setInt(1, type);
            rset1 = statement1.executeQuery();
            while (rset1.next())
			{
                int keyNpcId = rset1.getInt("key_npc_id");

                FiltredPreparedStatement statement2 = null;
                ResultSet rset2 = null;
                try
				{
                    statement2 = con.prepareStatement("SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay, key_npc_id FROM four_sepulchers_spawnlist WHERE key_npc_id = ? AND spawntype = ? ORDER BY id");
                    statement2.setInt(1, keyNpcId);
                    statement2.setInt(2, type);
                    rset2 = statement2.executeQuery();

                    GArray<Location> locations = new GArray<Location>();
                    while (rset2.next())
					{
                        locations.add(new Location(rset2.getInt("locx"), rset2.getInt("locy"), rset2.getInt("locz"), rset2.getInt("heading"), rset2.getInt("npc_templateid")));
                        count++;
                    }

                    table.put(keyNpcId, locations);
                }
				catch (Exception e)
				{
                    e.printStackTrace();
                }
				finally
				{
                    DatabaseUtils.closeDatabaseSR(statement2, rset2);
                }
            }
        }
		catch (Exception e)
		{
            e.printStackTrace();
        }
		finally
		{
            DatabaseUtils.closeDatabaseCSR(con, statement1, rset1);
        }

        return count;
    }

    private static void loadMysteriousBox()
	{
        _mysteriousBoxSpawns.clear();

        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;
        try
		{
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay, key_npc_id FROM four_sepulchers_spawnlist WHERE spawntype = 0 ORDER BY id");
            rset = statement.executeQuery();

            while (rset.next())
                _mysteriousBoxSpawns.put(rset.getInt("key_npc_id"), new Location(rset.getInt("locx"), rset.getInt("locy"), rset.getInt("locz"), rset.getInt("heading"), rset.getInt("npc_templateid")));

            _log.warning("FourSepulchersManager: Loaded " + _mysteriousBoxSpawns.size() + " Mysterious-Box spawns.");
        }
		catch (Exception e)
		{
            e.printStackTrace();
        }
		finally
		{
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
    }

    private static void spawnManagers()
	{
        _managers = new GArray<L2SepulcherNpcInstance>();
        for (int i = 31921; i <= 31924; i++)
            try
			{
                L2NpcTemplate template = NpcTable.getTemplate(i);
                Location loc = null;
                switch (i)
				{
                    case 31921: // conquerors
                        loc = new Location(181061, -85595, -7200, -32584);
                        break;
                    case 31922: // emperors
                        loc = new Location(179292, -88981, -7200, -33272);
                        break;
                    case 31923: // sages
                        loc = new Location(173202, -87004, -7200, -16248);
                        break;
                    case 31924: // judges
                        loc = new Location(175606, -82853, -7200, -16248);
                        break;
                }
                L2SepulcherNpcInstance npc = new L2SepulcherNpcInstance(IdFactory.getInstance().getNextId(), template);
                npc.onSpawn();
                npc.setSpawnedLoc(loc);
                npc.spawnMe(loc);
                _managers.add(npc);
                _log.warning("FourSepulchersManager: Spawned " + template.name);
            }
			catch (Exception e)
			{
                e.printStackTrace();
            }
    }

    static void closeAllDoors()
	{
        for (GateKeeper gk : _GateKeepers)
            try
			{
                gk.door.closeMe();
            }
			catch (Exception e)
			{
                e.printStackTrace();
            }
    }

    public static void deleteAllMobs()
	{
        for (L2NpcInstance mob : _allMobs)
            mob.deleteMe();
        _allMobs.clear();
    }

    public static void spawnShadow(int npcId)
	{
        if (!FourSepulchersManager.isAttackTime())
            return;
        Location loc = _shadowSpawns.get(npcId);
        if (loc == null)
            return;
        L2NpcTemplate template = NpcTable.getTemplate(loc.id);
        try {
            L2Spawn spawn = new L2Spawn(template);
            spawn.setLoc(loc);
            L2SepulcherRaidInstance mob = (L2SepulcherRaidInstance) spawn.spawnOne();
            mob.onSpawn();
            mob.mysteriousBoxId = npcId;
            _allMobs.add(mob);
        }
		catch (ClassNotFoundException e)
		{
            e.printStackTrace();
        }
    }

    public static void locationShadowSpawns()
	{
        int locNo = Rnd.get(4);
        int[] gateKeeper = {31929, 31934, 31939, 31944};
        for (int i = 0; i <= 3; i++)
		{
            Location loc = _shadowSpawns.get(gateKeeper[i]);
            // Обновляем только координаты, не npcId.
            loc.x = _shadowSpawnLoc[locNo][i].x;
            loc.y = _shadowSpawnLoc[locNo][i].y;
            loc.z = _shadowSpawnLoc[locNo][i].z;
            loc.h = _shadowSpawnLoc[locNo][i].h;
        }
    }

    public static void spawnEmperorsGraveNpc(int npcId)
	{
        if (!FourSepulchersManager.isAttackTime())
            return;
        GArray<Location> monsterList = _emperorsGraveNpcs.get(npcId);
        if (monsterList != null)
            for (Location loc : monsterList)
			{
                L2NpcTemplate template = NpcTable.getTemplate(loc.id);
                L2NpcInstance npc = null;
                if (template.isInstanceOf(L2SepulcherMonsterInstance.class))
                    npc = new L2SepulcherMonsterInstance(IdFactory.getInstance().getNextId(), template);
                else
                    npc = new L2SepulcherNpcInstance(IdFactory.getInstance().getNextId(), template);
                npc.onSpawn();
                npc.setSpawnedLoc(loc);
                npc.spawnMe(loc);
                _allMobs.add(npc);
            }
    }

    public static void spawnArchonOfHalisha(int npcId)
	{
        if (!FourSepulchersManager.isAttackTime())
            return;
        if (_archonSpawned.get(npcId))
            return;
        GArray<Location> monsterList = _dukeFinalMobs.get(npcId);
        if (monsterList == null)
            return;
        for (Location loc : monsterList)
		{
            L2NpcTemplate template = NpcTable.getTemplate(loc.id);
            L2SepulcherMonsterInstance mob = new L2SepulcherMonsterInstance(IdFactory.getInstance().getNextId(), template);
            mob.onSpawn();
            mob.setSpawnedLoc(loc);
            mob.spawnMe(loc);
            mob.mysteriousBoxId = npcId;
            _allMobs.add(mob);
        }
        _archonSpawned.put(npcId, true);
    }

    public static void spawnExecutionerOfHalisha(L2NpcInstance npc)
	{
        if (!FourSepulchersManager.isAttackTime())
            return;
        L2NpcTemplate template = NpcTable.getTemplate(_victim.get(npc.getNpcId()));
        L2SepulcherMonsterInstance npc2 = new L2SepulcherMonsterInstance(IdFactory.getInstance().getNextId(), template);
        npc2.onSpawn();
        npc2.setSpawnedLoc(npc.getLoc());
        npc2.spawnMe(npc.getLoc());
        _allMobs.add(npc2);
    }

    public static void spawnKeyBox(L2NpcInstance npc)
	{
        if (!FourSepulchersManager.isAttackTime())
            return;
        L2NpcTemplate template = NpcTable.getTemplate(_keyBoxNpc.get(npc.getNpcId()));
        L2SepulcherNpcInstance npc2 = new L2SepulcherNpcInstance(IdFactory.getInstance().getNextId(), template);
        npc2.onSpawn();
        npc2.setSpawnedLoc(npc.getLoc());
        npc2.spawnMe(npc.getLoc());
        _allMobs.add(npc2);
    }

    public static void spawnMonster(int npcId)
	{
        if (!FourSepulchersManager.isAttackTime())
            return;

        GArray<Location> monsterList;
        GArray<L2SepulcherMonsterInstance> mobs = new GArray<L2SepulcherMonsterInstance>();

        if (Rnd.get(2) == 0)
            monsterList = _physicalMonsters.get(npcId);
        else
            monsterList = _magicalMonsters.get(npcId);

        if (monsterList != null)
		{
            boolean spawnKeyBoxMob = false;
            boolean spawnedKeyBoxMob = false;

            for (Location loc : monsterList)
			{
                if (spawnedKeyBoxMob)
                    spawnKeyBoxMob = false;
                else
                    switch (npcId) {
                        case 31469:
                        case 31474:
                        case 31479:
                        case 31484:
                            if (Rnd.chance(2))
							{
                                spawnKeyBoxMob = true;
                                spawnedKeyBoxMob = true;
                            }
                            break;
                    }

                L2NpcTemplate template = NpcTable.getTemplate(spawnKeyBoxMob ? 18149 : loc.id);
                L2SepulcherMonsterInstance mob = new L2SepulcherMonsterInstance(IdFactory.getInstance().getNextId(), template);
                mob.onSpawn();
                mob.setSpawnedLoc(loc);
                mob.spawnMe(loc);
                mob.mysteriousBoxId = npcId;
                switch (npcId)
				{
                    case 31469:
                    case 31474:
                    case 31479:
                    case 31484:
                    case 31472:
                    case 31477:
                    case 31482:
                    case 31487:
                        mobs.add(mob);
                        break;
                }
                _allMobs.add(mob);
            }

            switch (npcId)
			{
                case 31469:
                case 31474:
                case 31479:
                case 31484:
                    _viscountMobs.put(npcId, mobs);
                    break;

                case 31472:
                case 31477:
                case 31482:
                case 31487:
                    _dukeMobs.put(npcId, mobs);
                    break;
            }
        }
    }

    public static void spawnMysteriousBox(int npcId)
	{
        if(!FourSepulchersManager.isAttackTime() || !_mysteriousBoxSpawns.containsKey(npcId))
            return;
        L2NpcTemplate template = NpcTable.getTemplate(_mysteriousBoxSpawns.get(npcId).id);
        L2SepulcherNpcInstance npc = new L2SepulcherNpcInstance(IdFactory.getInstance().getNextId(), template);
        npc.onSpawn();
        npc.setSpawnedLoc(_mysteriousBoxSpawns.get(npcId));
        npc.spawnMe(npc.getSpawnedLoc());
        _allMobs.add(npc);
    }

    public static synchronized boolean isDukeMobsAnnihilated(int npcId)
	{
        GArray<L2SepulcherMonsterInstance> mobs = _dukeMobs.get(npcId);
        if (mobs == null)
            return true;
        for (L2SepulcherMonsterInstance mob : mobs)
            if (!mob.isDead())
                return false;
        return true;
    }

    public static synchronized boolean isViscountMobsAnnihilated(int npcId)
	{
        GArray<L2SepulcherMonsterInstance> mobs = _viscountMobs.get(npcId);
        if (mobs == null)
            return true;
        for (L2SepulcherMonsterInstance mob : mobs)
            if (!mob.isDead())
                return false;
        return true;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    @SuppressWarnings("serial")
    public static class GateKeeper extends Location
	{
        public final L2DoorInstance door;
        public final L2NpcTemplate template;

        public GateKeeper(int npcId, int _x, int _y, int _z, int _h, int doorId)
		{
            super(_x, _y, _z, _h);
            door = DoorTable.getInstance().getDoor(doorId);
            template = NpcTable.getTemplate(npcId);
            if (template == null)
                System.out.println("FourGoblets::Sepulcher::RoomLock npc_template " + npcId + " undefined");
            if (door == null)
                System.out.println("FourGoblets::Sepulcher::RoomLock door id " + doorId + " undefined");
        }
    }
}