package l2open.gameserver;

import l2open.config.ConfigValue;
import l2open.Server;
import l2open.common.ThreadPoolManager;
import l2open.database.L2DatabaseFactory;
import l2open.debug.HeapDumper;
import l2open.extensions.network.SelectorThread;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Scripts;
import l2open.gameserver.cache.InfoCache;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.CoupleManager;
import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.loginservercon.LSConnection;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2open.gameserver.model.entity.olympiad.OlympiadDatabase;
import l2open.gameserver.model.entity.siege.territory.*;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.tables.*;
import l2open.util.Log;
import l2open.util.Util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

@SuppressWarnings({"nls", "unqualified-field-access", "boxing"})
public class Shutdown extends Thread {
    private static final Logger _log = Logger.getLogger(Shutdown.class.getName());

    private static Shutdown _instance;
    private static Shutdown _counterInstance = null;

    private int secondsShut;
    private int shutdownMode;

    public static final int SIGTERM = 0;
    public static final int GM_SHUTDOWN = 1;
    public static final int GM_RESTART = 2;
    public static final int ABORT = 3;
    private static String[] _modeText = {"shutdown", "shutdown", "restarting", "aborting"};
    private static String[] _modeText2 = {"Выключен", "Выключен", "Перезапущен", "Прервано"};

    public int getSeconds() {
        if (_counterInstance != null)
            return _counterInstance.secondsShut;
        return -1;
    }

    public int getMode() {
        if (_counterInstance != null)
            return _counterInstance.shutdownMode;
        return -1;
    }

    private void announce(final String text, int time, ScreenMessageAlign align, final String add, final String add2) {
        announce(text, time, align, add, add2, "");
    }

    private void announce(final String text, int time, ScreenMessageAlign align, final String add, final String add2, final String add3) {

        Announcements _an = Announcements.getInstance();
        ExShowScreenMessage sm = null;
        switch (ConfigValue.ShutdownMsgType) {
            case 1:
                _an.announceToAllC(text, add, add2);
                break;
            case 2:
                for (L2Player player : L2ObjectsStorage.getPlayers()) {
                    String ts = new CustomMessage(text, player).addString(add).addString(add2).addString(add3).toString();
                    sm = new ExShowScreenMessage(ts, time, align, false);
                    player.sendPacket(sm);
                }
                break;
            case 3:
                _an.announceToAllC(text, add, add2);
                for (L2Player player : L2ObjectsStorage.getPlayers()) {
                    String ts = new CustomMessage(text, player).addString(add).addString(add2).addString(add3).toString();
                    sm = new ExShowScreenMessage(ts, time, align, false);
                    player.sendPacket(sm);
                }
                break;
        }
    }

    /**
     * This function starts a shutdown countdown from Telnet (Copied from Function startShutdown())
     *
     * @param IP      IP Which Issued shutdown command
     * @param seconds seconds untill shutdown
     * @param restart true if the server will restart after shutdown
     */
    public void startTelnetShutdown(String IP, int seconds, boolean restart) {
        _log.warning("IP: " + IP + " issued shutdown command. " + _modeText[shutdownMode] + " in " + seconds + " seconds!");

        int time_m = seconds / 60;
        int time_s = seconds % 60;

        announce("Shutdown1", 10000, ScreenMessageAlign.BOTTOM_RIGHT, _modeText2[restart ? GM_RESTART : GM_SHUTDOWN], String.valueOf(time_m), String.valueOf(time_s));


        if (_counterInstance != null)
            _counterInstance._abort();
        _counterInstance = new Shutdown(seconds, restart);
        _counterInstance.start();
    }

    public void setAutoRestart(int seconds) {
        _log.warning("AutoRestart scheduled through " + Util.formatTime(seconds));
        if (_counterInstance != null)
            _counterInstance._abort();
        _counterInstance = new Shutdown(seconds, true);
        _counterInstance.start();
    }

    /**
     * This function aborts a running countdown
     *
     * @param IP IP Which Issued shutdown command
     */
    public void Telnetabort(String IP) {
        _log.warning("IP: " + IP + " issued shutdown ABORT. " + _modeText[shutdownMode] + " has been stopped!");

        if (_counterInstance != null) {
            _counterInstance._abort();
            announce("Shutdown2", 10000, ScreenMessageAlign.TOP_CENTER, _modeText2[shutdownMode], "");
        }
    }

    /**
     * Default constucter is only used internal to create the shutdown-hook instance
     */
    public Shutdown() {
        secondsShut = -1;
        shutdownMode = SIGTERM;
    }

    /**
     * This creates a countdown instance of Shutdown.
     *
     * @param seconds how many seconds until shutdown
     * @param restart true is the server shall restart after shutdown
     */
    public Shutdown(int seconds, boolean restart) {
        if (seconds < 0)
            seconds = 0;
        secondsShut = seconds;
        if (restart)
            shutdownMode = GM_RESTART;
        else
            shutdownMode = GM_SHUTDOWN;
    }

    /**
     * get the shutdown-hook instance
     * the shutdown-hook instance is created by the first call of this function,
     * but it has to be registrered externaly.
     *
     * @return instance of Shutdown, to be used as shutdown hook
     */
    public static Shutdown getInstance() {
        if (_instance == null)
            _instance = new Shutdown();
        return _instance;
    }

    /**
     * this function is called, when a new thread starts
     * <p>
     * if this thread is the thread of getInstance, then this is the shutdown hook
     * and we save all data and disconnect all clients.
     * <p>
     * after this thread ends, the server will completely exit
     * <p>
     * if this is not the thread of getInstance, then this is a countdown thread.
     * we start the countdown, and when we finished it, and it was not aborted,
     * we tell the shutdown-hook why we call exit, and then call exit
     * <p>
     * when the exit status of the server is 1, startServer.sh / startServer.bat
     * will restart the server.
     * <p>
     * Логгинг в этом методе не работает!!!
     */
    @Override
    public void run() {
        if (this == _instance) {
            for (int i = 0; i < ConfigValue.LoginPorts.length; i++)
                LSConnection.getInstance(i).shutdown();
            System.out.println("Shutting down scripts.");
            // Вызвать выключение у скриптов
            Scripts.getInstance().shutdown();

            Log.add(String.valueOf(Util._hwids.size()), "hwids");
            // ensure all services are stopped
            // stop all scheduled tasks
            saveData();
            try {
                ThreadPoolManager.getInstance().shutdown();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            // last byebye, save all data and quit this server
            // logging doesn't works here :(
            for (int i = 0; i < ConfigValue.LoginPorts.length; i++)
                LSConnection.getInstance(i).shutdown();
            // saveData sends messages to exit players, so shutdown selector after it
            System.out.println("Shutting down selector.");
            for (SelectorThread<L2GameClient> st : GameServer.getSelectorThreads())
                try {
                    st.shutdown();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            // commit data, last chance
            try {
                System.out.println("Shutting down database communication.");
                L2DatabaseFactory.getInstance().shutdown();
            } catch (Throwable t) {
                t.printStackTrace();
            }

            if (ConfigValue.MemorySnapshotOnShutdown)
                try {
                    System.out.println("Prepearing to make memory snapshot - unloading static data...");
                    GeoEngine.unload();
                    IdFactory.unload();
                    L2Multisell.unload();
                    InfoCache.unload();

                    //TODO ClanTable ??
                    NpcTable.unload();
                    PetDataTable.unload();
                    SkillSpellbookTable.unload();
                    SkillTable.unload();
                    SkillTreeTable.unload();
                    SpawnTable.unload();
                    TerritoryTable.unload();

                    Util.gc(10, 1000);
                    System.out.println("Memory snapshot saved: " + HeapDumper.dumpHeap(ConfigValue.SnapshotsDirectory, true));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            // server will quit, when this function ends.
            System.out.println("Shutdown finished.");
            Server.halt(_instance.shutdownMode == GM_RESTART ? 2 : 0, "GS Shutdown");
        } else {
            // gm shutdown: send warnings and then call exit to start shutdown sequence
            countdown();
            // last point where logging is operational :(
            System.out.println("GM shutdown countdown is over. " + _modeText[shutdownMode] + " NOW!");
            switch (shutdownMode) {
                case GM_SHUTDOWN:
                    _instance.setMode(GM_SHUTDOWN);
                    Server.exit(0, "GM_SHUTDOWN");
                    break;
                case GM_RESTART:
                    _instance.setMode(GM_RESTART);
                    Server.exit(2, "GM_RESTART");
                    break;
            }
        }
		saveBackup();
    }

    private static void saveBackup(){
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        String formattedDate = dateFormat.format(currentDate);
        String directoryPath = ConfigValue.DatapackRoot + "/backup_dump/";
        File directory = new File(directoryPath);
        // Проверяем, существует ли уже папка с таким именем
        if (!directory.exists()) {
            // Создаем папку
            boolean result = directory.mkdirs();

            if (result) {
                System.out.println("Папка успешно создана!");
            } else {
                System.out.println("Не удалось создать папку.");
            }
        } else {
            System.out.println("Папка уже существует.");
        }
        String canonicalPath = "";

        try {

            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                canonicalPath = directory.getCanonicalPath() + "\\";
            } else if (os.contains("nix") || os.contains("nux")) {
                canonicalPath = directory.getCanonicalPath() + "/";
            }
            System.out.println(canonicalPath);
        } catch (IOException e) {

        }

        String command = "mysqldump -u "+ ConfigValue.Login +" -p"+ ConfigValue.Password + " l2open -r \"" + canonicalPath + formattedDate + ".sql\"";
        System.out.println(command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Бэкап базы данных сохранен успешно.");
            } else {
                System.out.println("Ошибка при сохранении бэкапа базы данных.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This functions starts a shutdown countdown
     *
     * @param activeChar GM who issued the shutdown command
     * @param seconds    seconds until shutdown
     * @param restart    true if the server will restart after shutdown
     */
    public void startShutdown(L2Player activeChar, int seconds, boolean restart) {
        _log.warning("GM: " + activeChar.getName() + "(" + activeChar.getObjectId() + ") issued shutdown command. " + _modeText[restart ? GM_RESTART : GM_SHUTDOWN] + " in " + seconds + " seconds!");
        //if(shutdownMode > 0)
        // THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS__PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT

        int time_m = seconds / 60;
        int time_s = seconds % 60;

        announce("Shutdown1", 10000, ScreenMessageAlign.BOTTOM_RIGHT, _modeText2[restart ? GM_RESTART : GM_SHUTDOWN], String.valueOf(time_m), String.valueOf(time_s));

        if (_counterInstance != null)
            _counterInstance._abort();

        //the main instance should only run for shutdown hook, so we start a new instance
        _counterInstance = new Shutdown(seconds, restart);
        _counterInstance.start();
    }

    /**
     * This function aborts a running countdown
     *
     * @param activeChar GM who issued the abort command
     */
    public void abort(L2Player activeChar) {
        _log.warning("GM: " + activeChar.getName() + "(" + activeChar.getObjectId() + ") issued shutdown ABORT. " + _modeText[shutdownMode] + " has been stopped!");
        announce("Shutdown2", 10000, ScreenMessageAlign.TOP_CENTER, _modeText2[shutdownMode], "");

        if (_counterInstance != null)
            _counterInstance._abort();
    }

    /**
     * set the shutdown mode
     *
     * @param mode what mode shall be set
     */
    private void setMode(int mode) {
        shutdownMode = mode;
    }

    /**
     * set shutdown mode to ABORT
     */
    private void _abort() {
        shutdownMode = ABORT;
    }

    /**
     * this counts the countdown and reports it to all players
     * countdown is aborted if mode changes to ABORT
     */
    private void countdown() {
        while (secondsShut > 0)
            try {
                //_log.info("Shutdown: secondsShut="+secondsShut);
                switch (secondsShut) {
                    case 1800:
                        announce("Shutdown3", 10000, ScreenMessageAlign.BOTTOM_RIGHT, _modeText2[shutdownMode], "");
                        break;
                    case 600:
                        announce("Shutdown4", 10000, ScreenMessageAlign.BOTTOM_RIGHT, _modeText2[shutdownMode], "");
                        break;
                    case 300:
                        announce("Shutdown5", 10000, ScreenMessageAlign.BOTTOM_RIGHT, _modeText2[shutdownMode], "");
                        break;
                    case 240:
                        announce("Shutdown6", 10000, ScreenMessageAlign.BOTTOM_RIGHT, _modeText2[shutdownMode], "");
                        break;
                    case 180:
                        announce("Shutdown7", 10000, ScreenMessageAlign.BOTTOM_RIGHT, _modeText2[shutdownMode], "");
                        break;
                    case 120:
                        announce("Shutdown8", 10000, ScreenMessageAlign.BOTTOM_RIGHT, _modeText2[shutdownMode], "");
                        break;
                    case 60:
                        System.out.println(l2open.gameserver.model.L2ObjectsStorage.getStats());
                        System.out.println();
                        System.out.println(l2open.gameserver.geodata.PathFindBuffers.getStats());
                        System.out.println();

                        announce("Shutdown9", 10000, ScreenMessageAlign.TOP_CENTER, _modeText2[shutdownMode], "");
                        if (!ConfigValue.StartWhisoutSpawn)
                            try {
                                L2World.deleteVisibleNpcSpawns();
                            } catch (Throwable t) {
                                System.out.println("Error while unspawn Npcs!");
                                t.printStackTrace();
                            }
                        break;
                    case 30:
                        announce("Shutdown10", 10000, ScreenMessageAlign.TOP_CENTER, _modeText2[shutdownMode], "");
                        break;
                    case 15:
                        disconnectAllCharacters();
                        break;
                }

                secondsShut--;

                Thread.sleep(1000);

                if (shutdownMode == ABORT)
                    break;
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * this sends a last byebye, disconnects all players and saves data
     */
    private void saveData() {
        switch (shutdownMode) {
            case SIGTERM:
                System.err.println("SIGTERM received. Shutting down NOW!");
                Log.LogServ(Log.GS_SIGTERM, 0, 0, 0, 0);
                break;
            case GM_SHUTDOWN:
                System.err.println("GM shutdown received. Shutting down NOW!");
                Log.LogServ(Log.GS_shutdown, 0, 0, 0, 0);
                break;
            case GM_RESTART:
                System.err.println("GM restart received. Restarting NOW!");
                Log.LogServ(Log.GS_restart, 0, 0, 0, 0);
                break;
        }

        TerritorySiege.endSiege(); // останавливаем ТВ дабы сохранить все результаты.
        System.out.println("Territory Siege data saved.");

        disconnectAllCharacters();
        // Seven Signs data is now saved along with Festival data.
        if (!SevenSigns.getInstance().isSealValidationPeriod()) {
            SevenSignsFestival.getInstance().saveFestivalData(false);
            System.out.println("Seven Signs Festival data saved.");
        }

        // Save Seven Signs data before closing. :)
        SevenSigns.getInstance().saveSevenSignsData(0, true);
        System.out.println("Seven Signs data saved.");

        if (ConfigValue.EnableOlympiad)
            try {
                OlympiadDatabase.save();
                System.out.println("Olympiad System: Data saved!");
            } catch (Exception e) {
                e.printStackTrace();
            }

        if (ConfigValue.AllowWedding) {
            CoupleManager.getInstance().store();
            System.out.println("Couples: Data saved!");
        }

        if (ConfigValue.AllowCursedWeapons) {
            CursedWeaponsManager.getInstance().saveData();
            System.out.println("CursedWeaponsManager: Data saved!");
        }

        try {
            Hero.getInstance().shutdown();
            System.out.println("Hero: Data saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        NpcTable.storeKillsCount();

        System.out.println("All Data saved. All players disconnected, shutting down.");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            //never happens :p
        }
    }

    /**
     * this disconnects all clients from the server
     */
    private void disconnectAllCharacters() {
        for (L2Player player : L2ObjectsStorage.getPlayers())
            try {
                player.logout(true, false, false, true);
            } catch (Exception e) {
                System.out.println("Error while disconnect char: " + player.getName());
                e.printStackTrace();
            }
        try {
            Thread.sleep(1000);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void dump_stat() {
        StringBuilder sb = new StringBuilder();
        try {
            new File("stats").mkdir();
            FileUtils.writeStringToFile(new File(new StringBuilder().append("stats/RunnableStats-").append(new SimpleDateFormat("MMddHHmmss").format(Long.valueOf(System.currentTimeMillis()))).append(".txt").toString()), l2open.common.RunnableStatsManager.getInstance().getStats().toString());
            sb.append("Runnable stats saved.\n");
        } catch (IOException e) {
            sb.append(new StringBuilder().append("Exception: ").append(e.getMessage()).append("!\n").toString());
        }
    }
}