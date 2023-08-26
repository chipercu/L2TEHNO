package l2open.gameserver;

import emudev.KeyChecker;
import javolution.util.FastMap;
import l2open.Server;
import l2open.common.*;
import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.network.MMOConnection;
import l2open.extensions.network.SelectorStats;
import l2open.extensions.network.SelectorThread;
import l2open.extensions.scripts.Events;
import l2open.extensions.scripts.ScriptObject;
import l2open.extensions.scripts.Scripts;
import l2open.gameserver.cache.*;
import l2open.gameserver.communitybbs.Manager.NewsBBSManager;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.handler.UserCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.*;
import l2open.gameserver.itemmall.ItemMall;
import l2open.gameserver.loginservercon.LSConnection;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.DragonValley;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.MonsterRace;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2open.gameserver.model.entity.olympiad.*;
import l2open.gameserver.model.entity.siege.clanhall.BanditStrongholdSiege;
import l2open.gameserver.model.entity.siege.clanhall.RainbowSpringSiege;
import l2open.gameserver.model.entity.siege.clanhall.WildBeastFarmSiege;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.entity.vehicle.L2VehicleManager;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.network.L2GamePacketHandler;
import l2open.gameserver.tables.*;
import l2open.gameserver.taskmanager.DeleteExpiredVarsManager;
import l2open.gameserver.taskmanager.ItemsAutoDestroy;
import l2open.gameserver.taskmanager.MemoryWatchDog;
import l2open.gameserver.taskmanager.TaskManager;
import l2open.gameserver.webserver.WebServer;
import l2open.gameserver.xml.ItemTemplates;
import l2open.gameserver.xml.loader.*;
import l2open.status.Status;
import l2open.util.GArray;
import l2open.util.RRDTools;
import l2open.util.Strings;
import l2open.util.Log;
import l2open.util.Util;

import java.io.File;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@SuppressWarnings({"nls", "unqualified-field-access", "boxing"})
public class GameServer {
    private static final Logger _log = Logger.getLogger(GameServer.class.getName());
    private static final boolean develop = Boolean.parseBoolean(System.getenv("DEVELOP"));

    private static SelectorThread<L2GameClient> _selectorThreads[];
    public static GameServer gameServer;

    public static Status statusServer;

    public static Events events;

    public static FastMap<String, ScriptObject> scriptsObjects = new FastMap<String, ScriptObject>().setShared(true);

    private static int _serverStarted;

    protected static boolean serverLoaded = false;

    protected static final String LOG_FOLDER = "log"; // Name of folder for log file
    protected static final String LOG_NAME = develop ? "java/config/log.properties" :"./config/log.properties"; // Name of log file
    protected static final String LOG_TEXT = "Telnet server is currently disabled.";
    protected static final String Lines = "=================================================";
    private final SelectorStats _selectorStats = new SelectorStats();
    public static Future<?> _garbageCollector = null;

    public static SelectorThread<L2GameClient>[] getSelectorThreads() {
        return _selectorThreads;
    }

    public static int time() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static int uptime() {
        return time() - _serverStarted;
    }

    //private

    @SuppressWarnings("unchecked")
    public GameServer() throws Exception {
        Server.gameServer = this;

        _serverStarted = time();

        if (ConfigValue.CCPGuardEnable)
            ccpGuard.Protection.Init();

        _log.finest("used mem:" + MemoryWatchDog.getMemUsedMb());

        // Отдельным потоком грузим мультиселки, что бы сервак быстрей апался...
        new Thread(new RunnableImpl() {
            @Override
            public void runImpl() {
                Strings.reload();
            }
        }).start();

        IdFactory _idFactory = IdFactory.getInstance();
        if (!_idFactory.isInitialized()) {
            _log.severe("Could not read object IDs from DB. Please Check Your Data.");
            throw new Exception("Could not initialize the ID factory");
        }

        //TODO[DeadPool]: Emu-Dev Interface section begin
        KeyChecker.getInstance();
        //TODO[DeadPool]: Emu-Dev Interface section end

        SelectorThread.getStartAntiFlood();

        if (ConfigValue.DeadLockCheck > 0)
            new DeadlockDetector().start();

        CrestCache.load();
        ImagesChache.getInstance();

        // keep the references of Singletons to prevent garbage collection
        CharNameTable.getInstance();
        //QuestTable.getInstance();

        AuctionManager.getInstance();

        ClanTable.getInstance();

        FakePlayersTable.getInstance();

        SkillTable.getInstance();

        CubicManager.getInstance();

        PetSkillsTable.getInstance();

        ItemTemplates.getInstance();
        XmlOptionDataLoader.getInstance();

        events = new Events();

        TradeController.getInstance();

        RecipeController.getInstance();

        SkillTreeTable.getInstance();
        SkillSpellbookTable.getInstance();
        new l2open.pts.loader.ArmorEnchantBonusData();
        CharTemplateTable.getInstance();
        //XmlPcParameterLoader.getInstance();

        NpcTable.getInstance();
        l2open.pts.loader.NpcData.loadNpcPch();

        if (!NpcTable.isInitialized()) {
            _log.severe("Could not find the extraced files. Please Check Your Data.");
            throw new Exception("Could not initialize the npc table");
        }

        HennaTable _hennaTable = HennaTable.getInstance();
        if (!_hennaTable.isInitialized())
            throw new Exception("Could not initialize the Henna Table");
        HennaTreeTable.getInstance();
        if (!_hennaTable.isInitialized())
            throw new Exception("Could not initialize the Henna Tree Table");

        LevelUpTable.getInstance();

        GeoEngine.load();

        DoorTable.getInstance();

        UnderGroundColliseumManager.getInstance();

        TownManager.getInstance();

        CastleManager.getInstance();
        CastleSiegeManager.load();

        FortressManager.getInstance();
        FortressSiegeManager.load();

        ClanHallManager.getInstance();
        ClanHallSiegeManager.load();

        BanditStrongholdSiege.getInstance();
        WildBeastFarmSiege.getInstance();
        RainbowSpringSiege.getInstance();

        TerritorySiege.load();

        CastleManorManager.getInstance();

        l2open.gameserver.instancemanager.SeedOfAnnihilationZone.init();

        if (ConfigValue.EnablePtsSpawnEngine)
            Scripts.getInstance().callOnLoad();
        else
            SpawnTable.getInstance();

        RaidBossSpawnManager.getInstance();

        DimensionalRiftManager.getInstance();

        InstancedZoneManager.getInstance();

        Announcements.getInstance();

        AugmentName.load();

        FStringCache.load();

        LotteryManager.getInstance();

        MapRegion.getInstance();

        AugmentationData.getInstance();

        PlayerMessageStack.getInstance();

        if (ConfigValue.AutoDestroyDroppedItemAfter > 0 || ConfigValue.AutoDestroyPlayerDroppedItemAfter > 0)
            ItemsAutoDestroy.getInstance();

        MonsterRace.getInstance();

        StaticObjectsTable.getInstance();

        SevenSigns _sevenSignsEngine = SevenSigns.getInstance();
        SevenSignsFestival.getInstance();
        _sevenSignsEngine.updateFestivalScore();

        AutoSpawnHandler _autoSpawnHandler = AutoSpawnHandler.getInstance();
        _log.info("AutoSpawnHandler: Loaded " + _autoSpawnHandler.size() + " handlers in total.");

        AutoChatHandler _autoChatHandler = AutoChatHandler.getInstance();
        _log.info("AutoChatHandler: Loaded " + _autoChatHandler.size() + " handlers in total.");

        _sevenSignsEngine.spawnSevenSignsNPC();

        OlympiadDatabase.loadNobles();
        OlympiadDatabase.loadNoblesRank();
        if (ConfigValue.EnableOlympiad) {
            Olympiad.load();
            Hero.getInstance();
        }

        CursedWeaponsManager.getInstance();

        HellboundManager.getInstance();

        if (ConfigValue.PLRM_Enable)
            PlayerLevelRewardManager.getInstance();

        PlayerRewardManager.getInstance();

        SeedOfInfinityManager.getInstance();

        NaiaTowerManager.getInstance();
        NaiaCoreManager.getInstance();

        if (!ConfigValue.AllowWedding) {
            CoupleManager.getInstance();
            _log.info("CoupleManager initialized");
        }

        ItemHandler _itemHandler = ItemHandler.getInstance();
        _log.info("ItemHandler: Loaded " + _itemHandler.size() + " handlers.");

        AdminCommandHandler _adminCommandHandler = AdminCommandHandler.getInstance();
        _log.info("AdminCommandHandler: Loaded " + _adminCommandHandler.size() + " handlers.");

        UserCommandHandler _userCommandHandler = UserCommandHandler.getInstance();
        _log.info("UserCommandHandler: Loaded " + _userCommandHandler.size() + " handlers.");

        VoicedCommandHandler _voicedCommandHandler = VoicedCommandHandler.getInstance();
        _log.info("VoicedCommandHandler: Loaded " + _voicedCommandHandler.size() + " handlers.");

        TaskManager.getInstance();

        MercTicketManager.getInstance();

        L2VehicleManager.getInstance();
        AirShipDocksTable.getInstance();

        HandysBlockCheckerManager.getInstance();

        Shutdown _shutdownHandler = Shutdown.getInstance();
        Runtime.getRuntime().addShutdownHook(_shutdownHandler);

        // Отдельным потоком грузим мультиселки, что бы сервак быстрей апался...
        new Thread(new l2open.common.RunnableImpl() {
            @Override
            public void runImpl() {
                L2Multisell.getInstance();
                NewsBBSManager.getInstance().selectPortalNews();
                NewsBBSManager.getInstance().selectServerNews();
            }
        }).start();

        try {
            // Colosseum doors
            DoorTable.getInstance().getDoor(24190001).openMe();
            DoorTable.getInstance().getDoor(24190002).openMe();
            DoorTable.getInstance().getDoor(24190003).openMe();
            DoorTable.getInstance().getDoor(24190004).openMe();

            // TOI doors
            DoorTable.getInstance().getDoor(23180001).openMe();
            DoorTable.getInstance().getDoor(23180002).openMe();
            DoorTable.getInstance().getDoor(23180003).openMe();
            DoorTable.getInstance().getDoor(23180004).openMe();
            DoorTable.getInstance().getDoor(23180005).openMe();
            DoorTable.getInstance().getDoor(23180006).openMe();

            // Эти двери, похоже выполняют декоративную функцию,
            // находятся во Frozen Labyrinth над мостом по пути к снежной королеве.
            DoorTable.getInstance().getDoor(23140001).openMe();
            DoorTable.getInstance().getDoor(23140002).openMe();

            DoorTable.getInstance().checkAutoOpen();
        } catch (NullPointerException e) {
            _log.warning("Doors table does not contain the right door info. Update doors.");
            e.printStackTrace();
        }

        _log.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());

        TeleportTable.getInstance();

        PartyRoomManager.getInstance();
        ItemMall.getInstance();

        new File("./log/game").mkdirs();

        int restartTime = 0;
        int restartAt = 0;

        // Время запланированного на определенное время суток рестарта
        if (ConfigValue.AutoRestartAt > -1) {
            Calendar calendarRestartAt = Calendar.getInstance();
            calendarRestartAt.set(Calendar.HOUR_OF_DAY, ConfigValue.AutoRestartAt);
            calendarRestartAt.set(Calendar.MINUTE, 0);

            // Если запланированное время уже прошло, то берем +24 часа
            if (calendarRestartAt.getTimeInMillis() < System.currentTimeMillis())
                calendarRestartAt.add(Calendar.HOUR_OF_DAY, 24);

            restartAt = (int) (calendarRestartAt.getTimeInMillis() - System.currentTimeMillis()) / 1000;
        }

        // Время регулярного рестарта (через определенное время)
        restartTime = ConfigValue.AutoRestart * 60 * 60;

        // Проверяем какой рестарт раньше, регулярный или запланированный
        if (restartTime < restartAt && restartTime > 0 || restartTime > restartAt && restartAt == 0)
            Shutdown.getInstance().setAutoRestart(restartTime);
        else if (restartAt > 0)
            Shutdown.getInstance().setAutoRestart(restartAt);

        MailParcelController.getInstance();

        L2TopManager.getInstance();
        L2VoteManager.getInstance();

        ItemAuctionManager.getInstance();

        BloodAltarManager.getInstance();

        DeleteExpiredVarsManager.getInstance();

        DragonValley.getInstance();
        if (ConfigValue.EnableLindvior)
            l2open.gameserver.model.clan_find.ClanEntryManager.getInstance();

		/*if(ConfigValue.BotSystemEnable)
		{
			AdminCommandHandler.getInstance().registerAdminCommandHandler(new fantoms.admin.AdminFantome());

			fantoms.manager.AiManager.getInstance().load();
			fantoms.manager.StringsManager.getInstance().load();
			fantoms.manager.FantomeManager.getInstance().load();
		}
		if(ConfigValue.BotSystemWriteAI)
		{
			l2open.gameserver.model.actor.listener.CharListenerList.addGlobal(fantoms.listeners.player.EnterListenerImpl.STATIC);
			if(!ConfigValue.BotSystemEnable)
				fantoms.manager.AiManager.getInstance().load();
		}*/

        _log.info("GameServer Started");
        _log.info("Maximum Numbers of Connected Players: " + ConfigValue.MaximumOnlineUsers);

        //Stat.init();

        if (ConfigValue.UseRRD)
            RRDTools.init();

        if (ConfigValue.AcademicEnable)
            l2open.gameserver.model.barahlo.academ2.AcademiciansStorage.getInstance();

        for (int i = 0; i < ConfigValue.LoginPorts.length; i++)
            LSConnection.getInstance(i).start();

        SelectorThread.setAntiFlood(ConfigValue.AntiFloodEnable);
        SelectorThread.setAntiFloodSocketsConf(ConfigValue.MaxUnhandledSocketsPerIP, ConfigValue.UnhandledSocketsMinTTL);

        L2GamePacketHandler gph = new L2GamePacketHandler();
        //SelectorConfig<L2GameClient> sc = new SelectorConfig<L2GameClient>(gph);
        //sc.setMaxSendPerPass(30);
        //sc.setSelectorSleepTime(10);
        SelectorThread.setGlobalReadLock(ConfigValue.GameserverPort.length > 1);
        _selectorThreads = new SelectorThread[ConfigValue.GameserverPort.length];
        InetAddress ad = null;
        try {
            ad = InetAddress.getByName(ConfigValue.GameserverHostname);
        } catch (Exception e) {
        }
        for (int i = 0; i < ConfigValue.GameserverPort.length; i++) {
            _selectorThreads[i] = new SelectorThread<L2GameClient>(_selectorStats, gph, gph, gph, ConfigValue.GameServerProtectEnable ? new SelectorHelperGS() : null);
            _selectorThreads[i].openServerSocket(ad, ConfigValue.GameserverPort[i]);
            _selectorThreads[i].start();
        }

        ServerVariables.unset("MaxOnlineDay");
        Util.max_online = ServerVariables.getInt("MaxOnline", 0);
        Util.donate_2_server = ServerVariables.getLong("donate_2_server", 0);
        if (ConfigValue.WebServerDelay > 0)
            ThreadPoolManager.getInstance().scheduleAtFixedRate(new WebServer(), 5000, ConfigValue.WebServerDelay);

        if (ConfigValue.MmoTopStartWithServer)
            MMOTopManager.start();
        if (ConfigValue.MmoTopStartWithServer2)
            MMOTopManager2.start();
        if (ConfigValue.MmoVoteStartWithServer)
            MMOVoteManager.start();
        if (ConfigValue.OfflineRestoreAfterRestart)
            // Это довольно тяжелая задача поэтому пусть идет отдельным тридом
            new Thread(new l2open.common.RunnableImpl() {
                @Override
                public void runImpl() {
                    if (ConfigValue.OfflineTradeDaysToKick > 0) {
                        int min_offline_restore = (int) (System.currentTimeMillis() / 1000 - (ConfigValue.OfflineTradeDaysToKick * 60 * 60 * 24));
                        mysql.set("DELETE FROM character_variables WHERE `name` = 'offline' AND `value` < " + min_offline_restore);
                    }
                    mysql.set("DELETE FROM character_variables WHERE `name` = 'offline' AND `obj_id` IN (SELECT `obj_id` FROM `characters` WHERE `accessLevel` < 0)");

                    ThreadConnection con = null;
                    FiltredPreparedStatement st = null;
                    ResultSet rs = null;

                    try {
                        GArray<Object> logins = mysql.get_array(L2DatabaseFactory.getInstanceLogin(), "SELECT `login` FROM `accounts` WHERE `access_level` < 0");
                        if (logins.size() > 0) {
                            con = L2DatabaseFactory.getInstance().getConnection();
                            st = con.prepareStatement("DELETE FROM character_variables WHERE `name` = 'offline' AND `obj_id` IN (SELECT `obj_id` FROM `characters` WHERE `account_name`=?)");
                            for (Object login : logins) {
                                st.setString(1, (String) login);
                                st.executeUpdate();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        DatabaseUtils.closeDatabaseCSR(con, st, rs);
                    }

                    GArray<HashMap<String, Object>> list = mysql.getAll("SELECT `obj_id`, `value`, (SELECT `account_name` FROM `characters` WHERE `characters`.`obj_Id` = `character_variables`.`obj_id` LIMIT 1) AS `account_name` FROM `character_variables` WHERE name LIKE 'offline'");
                    for (HashMap<String, Object> e : list) {
                        L2GameClient client = new L2GameClient(new MMOConnection<L2GameClient>(null), true);
                        client.setCharSelection((Integer) e.get("obj_id"));
                        L2Player p = client.loadCharFromDisk(0);
                        if (p == null || p.isDead())
                            continue;
                        client.setLoginName((String) e.get("account_name") == null ? "OfflineTrader_" + p.getName() : (String) e.get("account_name"));
                        client.OnOfflineTrade();
                        //p.restoreBonus();
                        p.spawnMe();
                        //p.updateTerritories();
                        p.setOnlineStatus(true);
                        p.setOfflineMode(true);
                        p.setConnected(false);
                        p.setNameColor(Integer.decode("0x" + ConfigValue.OfflineTradeNameColor));
                        //p.restoreEffects();
                        //p.restoreDisableSkills();
                        //p.broadcastUserInfo(true);
                        if (p.getClan() != null && p.getClan().getClanMember(p.getObjectId()) != null)
                            p.getClan().getClanMember(p.getObjectId()).setPlayerInstance(p, false);
                        if (ConfigValue.OfflineTradeDaysToKick > 0)
                            p.startKickTask(((ConfigValue.OfflineTradeDaysToKick * 60 * 60 * 24) + Integer.parseInt(e.get("value").toString())) * 1000L - System.currentTimeMillis());
                    }
                    _log.info("Restored " + list.size() + " offline traders");
                }
            }).start();

		/*if(ConfigValue.ENABLED)
		try
		{
			_log.info("AntiSpam state: "+ com.mmobite.as.api.AntispamAPI.init("config/client.properties", 273));
			com.mmobite.admin.server.AdminServer.init("config/admin_job.properties", new com.mmobite.api.AdminAction());
		}
		catch(Exception e)
		{}*/

        startGC();
        System.gc();
		/*new Thread()
		{
			public void runImpl()
			{
				Scanner in = new Scanner(System.in);
				while(true)
				{
					// TODO: Налепить на это обработчик команд и в дальнейшем развить его, думаю будет удобно всем клиентам)
					// Можно будет вбивать в консоль нужные параметры и все, а вообще нид почитать, мб можно как-то вызывать еще одну консоль чисто для команд)
					System.out.print(in.next());
				}
			}
		}.start();*/
        if (ConfigValue.LogOnlineDelay > 0)
            ThreadPoolManager.getInstance().scheduleAtFixedRate(new LogOnline(), ConfigValue.LogOnlineDelay * 1000L, ConfigValue.LogOnlineDelay * 1000L);
    }

    private static HashMap<String, String> _online = new HashMap<String, String>();

    static class LogOnline extends l2open.common.RunnableImpl {
        @Override
        public void runImpl() {
            _online.clear();
            for (L2Player player : L2ObjectsStorage.getPlayers())
                if (player != null)
                    _online.put(player.getHWIDs(), player.getHWIDs());
            Log.add(String.valueOf(_online.size()), "clear_online");

            for (L2Clan clan : ClanTable.getInstance().getClans())
                Log.add(String.valueOf(clan.getClearOnline()), "clan/" + clan.getClanId());
        }
    }

    static class ServicePurge extends l2open.common.RunnableImpl {
        @Override
        public void runImpl() {
            int offline = L2ObjectsStorage.getAllOfflineCount();
            int all = L2ObjectsStorage.getAllPlayersCount();
            int fake = FakePlayersTable.getFakePlayersCount();
            int online = L2ObjectsStorage.getOnlineCount();

            if (ConfigValue.GarbageCollectorShowFullStat) {
                _log.info(Lines);
                _log.info("Garbage Collector start.");
            }

            if (ConfigValue.GarbageCollectorShowOnline) {
                _log.info("Players full online = " + all);
                _log.info("Players online = " + online);
                _log.info("Players offline = " + offline);
                _log.info("Players fake = " + fake);
            }

            long _upTime = System.currentTimeMillis();
            if (ConfigValue.GarbageCollectorShowFullStat)
                _log.info("Used to: " + (StatsUtil.getMemUsed() / 1024) + "Kb");
            try {
                System.gc();
            } catch (Exception e) {
            }
            if (ConfigValue.GarbageCollectorShowFullStat) {
                _log.info("Garbage Collector finish(" + (System.currentTimeMillis() - _upTime) + "ms).");
                _log.info("Used after: " + (StatsUtil.getMemUsed() / 1024) + "Kb");

                String memUsage = StatsUtil.getMemUsage().toString();
                for (String line : memUsage.split("\n"))
                    _log.info(line);
            }
            if (ConfigValue.GarbageCollectorShowFullStat)
                _log.info(Lines);
            _garbageCollector = ThreadPoolManager.getInstance().schedule(new ServicePurge(), ConfigValue.GarbageCollectorDelay);
        }
    }

    public static void startGC() {
        if (ConfigValue.GarbageCollectorDelay > 0)
            _garbageCollector = ThreadPoolManager.getInstance().schedule(new ServicePurge(), ConfigValue.GarbageCollectorDelay);
    }

    public static void stopGC() {
        if (_garbageCollector != null)
            _garbageCollector.cancel(true);
    }

    public static boolean isLoaded() {
        return serverLoaded;
    }
}