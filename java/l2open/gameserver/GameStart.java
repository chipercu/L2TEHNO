package l2open.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.sql.ResultSet;

import java.io.*;

import l2open.Server;
import l2open.common.*;
import l2open.config.*;
import l2open.database.*;
import l2open.extensions.scripts.Scripts;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.taskmanager.MemoryWatchDog;
import l2open.status.Status;
import l2open.util.*;

import l2open.gameserver.model.items.L2ItemInstance;
import l2open.extensions.scripts.Functions;

import java.lang.reflect.Method;

@SuppressWarnings({"nls", "unqualified-field-access", "boxing"})
public class GameStart extends GameServer {
    public GameStart() throws Exception {
    }
    private static final Logger _log = Logger.getLogger(GameStart.class.getName());
    public static GameServer gameServer;
    public static Status statusServer;
    public static long _upTime = 0;

    public static void main(String[] args) throws Exception {
        //Compiler.disable();
        Server.SERVER_MODE = Server.MODE_GAMESERVER;
        // Local Constants

        /*** Main ***/
        // Create log folder
        File logFolder = new File(ConfigValue.DatapackRoot, LOG_FOLDER);
        logFolder.mkdir();

        // Create input stream for log file -- or store file data into memory
        InputStream is = Files.newInputStream(new File(LOG_NAME).toPath());
        LogManager.getLogManager().readConfiguration(is);
        is.close();

        ConfigSystem.load();
        if (ConfigValue.FirstTeam)
            ftGuard.ftGuard.Init();

        Util.waitForFreePorts(ConfigValue.GameserverHostname, ConfigValue.GameserverPort);
        L2DatabaseFactory.getInstance();
        Log.InitGSLoggers();
        log_adena();
        //test();

        gameServer = new GameServer();

        if (ConfigValue.EnableTelnet) {
            statusServer = new Status(Server.MODE_GAMESERVER);
            statusServer.start();
        } else
            _log.info(LOG_TEXT);

        Util.gc(5, 1000);
        //_log.info("Free memory " + MemoryWatchDog.getMemFreeMb() + " of " + MemoryWatchDog.getMemMaxMb());
        Log.LogServ(Log.GS_started, (int) MemoryWatchDog.getMemFree(), (int) MemoryWatchDog.getMemMax(), IdFactory.getInstance().size(), 0);
        _upTime = System.currentTimeMillis();
        serverLoaded = true;

        _log.info(Lines);
        String memUsage = StatsUtil.getMemUsage().toString();
        for (String line : memUsage.split("\n"))
            _log.info(line);
        _log.info(Lines);

        _log.info("MinProtocolRevision: " + ConfigValue.MinProtocolRevision);
        if (ConfigValue.ThreadPoolManagerDebugLogFile)
            Log.add("------------------------------------------------------------------", "./../thread_pool/thread_pool_debug_" + ThreadPoolManager.data); // Что бы лучше было видно, когда пошли логи после запуска сервера:)
    }

    private void preload(String classesFileName) {
        try {
            FileReader fReader = new FileReader(classesFileName);
            BufferedReader reader = new BufferedReader(fReader);
            String className = reader.readLine();
            while (className != null) {
                try {
                    Class clazz = Class.forName(className);
                    String n = clazz.getName();
                    Compiler.compileClass(clazz);
                } catch (Exception e) {
                }
                className = reader.readLine();
            }
        } catch (Exception e) {
        }
        Compiler.disable();
    }

    public static HashMap<Integer, HashMap<String, long[]>> _player = new HashMap<Integer, HashMap<String, long[]>>();

    private static void loadVariables() {
        _log.info("Loadet loadVariables...");
        ThreadConnection con = null;
        FiltredPreparedStatement offline = null;
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            offline = con.prepareStatement("SELECT * FROM character_variables");
            rs = offline.executeQuery();
            while (rs.next()) {
                HashMap<String, long[]> _skills = new HashMap<String, long[]>();
                String name = rs.getString("name");
                int obj_id = rs.getInt("obj_id");
                String value = Strings.stripSlashes(rs.getString("value"));
                if (name.startsWith("Scheme_")) {
                    try {
                        long[] sch = new long[0];
                        for (String str : value.split(";"))
                            if (!str.isEmpty()) {
                                String[] arrayOfString2 = str.split(",");
                                long ptsId = Integer.parseInt(arrayOfString2[0]) * 65536 + Integer.parseInt(arrayOfString2[1]);
                                sch = ArrayUtils.add(sch, ptsId);
                            }
                        _skills.put(name.substring(7), sch);
                        _player.put(obj_id, _skills);
                    } catch (NullPointerException npe) {
                        _log.info("Remove (" + name + ") Table in Community Buffer because is null");
                        npe.printStackTrace();
                    } catch (NumberFormatException nfe) {
                        _log.info("Remove (" + name + ") Table in Community Buffer because is null");
                        nfe.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {

        } finally {
            DatabaseUtils.closeDatabaseCSR(con, offline, rs);
        }
        _log.info("Loadet loadVariables ok...");
    }

    private static void log_adena() {
        StringBuilder sb = new StringBuilder();
        int length = ConfigValue.ListObjectIdNoLogItemCount.length;
        for (int i = 0; i < length; i++) {
            sb.append("'");
            sb.append(ConfigValue.ListObjectIdNoLogItemCount[i]);
            sb.append("'");
            if (i < length - 1)
                sb.append(",");
        }

        Log.add("--------------------------------", "item_count");
        for (int item_id : ConfigValue.ListItemIdNoLogItemCount) {
            String count = get_item_count(item_id, sb.toString());
            Log.add("ItemId[" + item_id + "] count: " + count, "item_count");
        }
    }

    public static String get_item_count(int item_id, String exclude) {
        String count = "0";
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("select SUM(count) from items where item_id='" + item_id + "' AND owner_id NOT IN (" + exclude + ")");
            rset = statement.executeQuery();

            if (rset.next())
                count = rset.getString(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
        return count;
    }

    public static List<LogItemInfo> get_item_count(int item_id, long count) {
        List<LogItemInfo> result = new ArrayList<LogItemInfo>();
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("select item_id, count, owner_id from items where item_id='" + item_id + "' AND count>='" + count + "'");
            rset = statement.executeQuery();

            while (rset.next())
                result.add(new LogItemInfo(rset.getInt("owner_id"), rset.getInt("item_id"), rset.getLong("count")));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
        return result;
    }

    private static void test() {
        _log.info("Loadet test...");
        loadVariables();
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();

            for (Integer obj_id : _player.keySet()) {
                String allbuff = "";
                for (String name : _player.get(obj_id).keySet()) {
                    for (long sk_id : _player.get(obj_id).get(name))
                        allbuff = new StringBuilder().append(allbuff).append(sk_id + ";").toString();

                    statement = con.prepareStatement("INSERT INTO community_skillsave (charId,name,skills) VALUES(?,?,?)");
                    statement.setInt(1, obj_id);
                    statement.setString(2, name);
                    statement.setString(3, allbuff);
                    statement.execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        _log.info("Loadet test ok...");
    }

    public static long serverUpTime() {
        return _upTime;
    }

    public static class LogItemInfo {
        public int owner_id;
        public int item_id;
        public long item_count;

        public LogItemInfo(int o, int i, long i_c) {
            owner_id = o;
            item_id = i;
            item_count = i_c;
        }
    }
}