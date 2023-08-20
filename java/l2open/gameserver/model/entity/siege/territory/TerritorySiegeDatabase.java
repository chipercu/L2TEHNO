package l2open.gameserver.model.entity.siege.territory;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.siege.SiegeClan;
import l2open.gameserver.model.entity.siege.SiegeSpawn;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import l2open.util.Location;

import javolution.util.FastMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TerritorySiegeDatabase {
    private static final Logger _log = Logger.getLogger(TerritorySiegeDatabase.class.getName());

    private static FastMap<Integer, GArray<SiegeSpawn>> _flagSpawnList = new FastMap<Integer, GArray<SiegeSpawn>>().setShared(true);
    private static FastMap<Integer, GArray<L2Spawn>> _catapultSpawn = new FastMap<Integer, GArray<L2Spawn>>().setShared(true);
    private static FastMap<Integer, GArray<L2Spawn>> _guardSpawn = new FastMap<Integer, GArray<L2Spawn>>().setShared(true);
    private static FastMap<Integer, GArray<L2Spawn>> _npcSpawn = new FastMap<Integer, GArray<L2Spawn>>().setShared(true);

    private static void selectSpawn(int type, FastMap<Integer, GArray<L2Spawn>> sp) {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;
        try {
            for (Castle castle : CastleManager.getInstance().getCastles().values()) {
                GArray<L2Spawn> spawn = new GArray<L2Spawn>();

                con = L2DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement("SELECT * FROM territory_spawnlist WHERE castleId = '" + castle.getId() + "' AND spawnType = '" + type + "'");
                rset = statement.executeQuery();
                while (rset.next()) {
                    int npcId = rset.getInt("npcId");
                    Location loc = new Location(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"), rset.getInt("heading"));
                    L2NpcTemplate template = NpcTable.getTemplate(npcId);
                    if (template != null) {
                        L2Spawn _spawn = new L2Spawn(template);
                        _spawn.setAmount(1);
                        _spawn.setLoc(loc);
                        _spawn.setRespawnDelay(0);
                        _spawn.setLocation(0);
                        spawn.add(_spawn);
                    } else
                        _log.warning("Error loading siege guard, missing npc data in npc table for id: " + npcId);
                }
                sp.put(castle.getId(), spawn);
            }
        } catch (Exception localException1) {
            _log.log(Level.WARNING, "TerritorySiegeDatabase loadSiegeGuards: ", localException1);
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
    }

    public static FastMap<Integer, GArray<L2Spawn>> getNpcsSpawnList() {
        return _npcSpawn;
    }

    public static FastMap<Integer, GArray<SiegeSpawn>> getSiegeFlags() {
        return _flagSpawnList;
    }

    public static void loadSiegeMembers() {
        TerritorySiege.getPlayers().clear();
        TerritorySiege.getClans().clear();

        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT obj_Id, side, type FROM siege_territory_members");
            rset = statement.executeQuery();

            while (rset.next())
                if (rset.getInt("type") == 0)
                    TerritorySiege.getPlayers().put(rset.getInt("obj_Id"), rset.getInt("side"));
                else
                    TerritorySiege.getClans().put(new SiegeClan(rset.getInt("obj_Id"), null), rset.getInt("side"));
        } catch (Exception e) {
            _log.log(Level.WARNING, "TerritorySiegeDatabase loadSiegeMembers: ", e);
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
    }

    public static void saveSiegeMembers() {
        clearSiegeMembers();
        for (Entry<Integer, Integer> entry : TerritorySiege.getPlayers().entrySet())
            changeRegistration(entry.getKey(), entry.getValue(), 0, false);
        for (Entry<SiegeClan, Integer> entry : TerritorySiege.getClans().entrySet())
            changeRegistration(entry.getKey().getClanId(), entry.getValue(), 1, false);
    }

    public static void clearSiegeMembers() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM siege_territory_members");
            statement.execute();
        } catch (Exception e) {
            _log.log(Level.WARNING, "TerritorySiegeDatabase clearSiegeMembers: ", e);
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public static void loadSiegeCatapultsSpawnList() {
        selectSpawn(2, _catapultSpawn);
        _log.info("TerritorySiege: load " + _catapultSpawn.size() + " fortress spawns.");
    }

    public static FastMap<Integer, GArray<L2Spawn>> getCatapultsSpawnList() {
        return _catapultSpawn;
    }

    public static void loadNpcsSpawnList() {
        selectSpawn(0, _npcSpawn);
        _log.info("TerritorySiege: load " + _npcSpawn.size() + " town spawns.");
    }

    public static FastMap<Integer, GArray<L2Spawn>> getGuardsSpawnList() {
        return _guardSpawn;
    }

    public static void loadSiegeGuardsSpawnList() {
        selectSpawn(1, _guardSpawn);
        _log.info("TerritorySiege: load " + _guardSpawn.size() + " castle spawns.");
    }

    public static void loadSiegeFlags() {
        try {
            String path = "./config/siege_territory.properties";
            if (Boolean.parseBoolean(System.getenv("DEVELOP"))){
                path = "java/config/siege_territory.properties";
            }
            InputStream is = Files.newInputStream(new File(path).toPath());
            Properties siegeSettings = new Properties();
            siegeSettings.load(is);
            is.close();

            for (Castle castle : CastleManager.getInstance().getCastles().values()) {
                int flagItemId = Integer.parseInt(siegeSettings.getProperty(castle.getName() + "FlagItemId", ""));
                int flagNpcId = Integer.parseInt(siegeSettings.getProperty(castle.getName() + "FlagNpcId", ""));
                String spawnParams = siegeSettings.getProperty(castle.getName() + "FlagPos", "");
                if (spawnParams.length() > 0) {
                    StringTokenizer st = new StringTokenizer(spawnParams.trim(), ",");
                    int xc = Integer.parseInt(st.nextToken());
                    int yc = Integer.parseInt(st.nextToken());
                    int zc = Integer.parseInt(st.nextToken());

                    GArray<SiegeSpawn> flagSpawns = new GArray<SiegeSpawn>();
                    for (int x = xc - 150; x <= xc + 150; x += 150)
                        for (int y = yc - 150; y <= yc + 150; y += 150)
                            flagSpawns.add(new SiegeSpawn(castle.getId(), new Location(x, y, zc), flagNpcId, flagItemId));

                    _flagSpawnList.put(castle.getId(), flagSpawns);
                } else
                    _log.warning("Not found flags for " + castle.getName());
            }
        } catch (Exception e) {
            System.err.println("Error while loading siege data.");
            e.printStackTrace();
        }
    }

    public static void changeRegistration(int obj_Id, int side, int type, boolean delete) {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            if (delete) {
                statement = con.prepareStatement("DELETE FROM `siege_territory_members` WHERE `obj_Id`=? AND `type`=?");
                statement.setInt(1, obj_Id);
                statement.setInt(2, type);
            } else {
                statement = con.prepareStatement("REPLACE INTO siege_territory_members (obj_Id, side, type) VALUES (?, ?, ?)");
                statement.setInt(1, obj_Id);
                statement.setInt(2, side);
                statement.setInt(3, type);
            }
            statement.execute();
        } catch (Exception e) {
            _log.log(Level.WARNING, "TerritorySiegeDatabase saveSiegeMember: ", e);
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }
}