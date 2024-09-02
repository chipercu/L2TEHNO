package communityboard;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import l2open.config.*;
import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.instancemanager.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.Files;
import l2open.util.RateService;
import l2open.util.ValueSortMap;
import l2open.util.Util;

import bosses.EpicBossState;

/**
 * @author Powered by L2CCCP
 */
public class CommunityBoardFullStats extends BaseBBSManager implements ICommunityHandler, ScriptFile {
    static final Logger _log = Logger.getLogger(CommunityBoardFullStats.class.getName());

    private static int CommunityBoardFullStats = 1 << 60;

    @SuppressWarnings("rawtypes")
    @Override
    public Enum[] getCommunityCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        _bbsgetfav,
        _bbsstatistic
    }

    private static Map<Integer, StatsSet> _top_pts;
    private static Map<Integer, StatsSet> _top_win_rate;
    public static Map<Integer, EpicBossState> _epic_state = new HashMap<Integer, EpicBossState>();

    public static class Manager {
        public String[] TopPvPName = new String[ConfigValue.StatisticCount];
        public String[] TopPvPClan = new String[ConfigValue.StatisticCount];
        public int[] TopPvPSex = new int[ConfigValue.StatisticCount];
        public int[] TopPvPClass = new int[ConfigValue.StatisticCount];
        public int[] TopPvPOn = new int[ConfigValue.StatisticCount];
        public int[] TopPvPOnline = new int[ConfigValue.StatisticCount];
        public int[] TopPvP = new int[ConfigValue.StatisticCount];
        public int[] TopPvPPvP = new int[ConfigValue.StatisticCount];

        public String[] TopPkName = new String[ConfigValue.StatisticCount];
        public String[] TopPkClan = new String[ConfigValue.StatisticCount];
        public int[] TopPkSex = new int[ConfigValue.StatisticCount];
        public int[] TopPkClass = new int[ConfigValue.StatisticCount];
        public int[] TopPkOn = new int[ConfigValue.StatisticCount];
        public int[] TopPkOnline = new int[ConfigValue.StatisticCount];
        public int[] TopPk = new int[ConfigValue.StatisticCount];
        public int[] TopPkPvP = new int[ConfigValue.StatisticCount];

        public String[] TopOnlineName = new String[ConfigValue.StatisticCount];
        public String[] TopOnlineClan = new String[ConfigValue.StatisticCount];
        public int[] TopOnlineSex = new int[ConfigValue.StatisticCount];
        public int[] TopOnlineClass = new int[ConfigValue.StatisticCount];
        public int[] TopOnlineOn = new int[ConfigValue.StatisticCount];
        public int[] TopOnlineOnline = new int[ConfigValue.StatisticCount];
        public int[] TopOnline = new int[ConfigValue.StatisticCount];
        public int[] TopOnlinePvP = new int[ConfigValue.StatisticCount];

        public String[] TopRichName = new String[ConfigValue.StatisticCount];
        public String[] TopRichClan = new String[ConfigValue.StatisticCount];
        public int[] TopRichSex = new int[ConfigValue.StatisticCount];
        public int[] TopRichClass = new int[ConfigValue.StatisticCount];
        public int[] TopRichOn = new int[ConfigValue.StatisticCount];
        public int[] TopRichOnline = new int[ConfigValue.StatisticCount];
        public long[] TopRich = new long[ConfigValue.StatisticCount];

        public String[] TopClanName = new String[ConfigValue.StatisticCount];
        public String[] TopClanAlly = new String[ConfigValue.StatisticCount];
        public String[] TopClanLeader = new String[ConfigValue.StatisticCount];
        public int[] TopClanLevel = new int[ConfigValue.StatisticCount];
        public int[] TopClanPoint = new int[ConfigValue.StatisticCount];
        public int[] TopClanMember = new int[ConfigValue.StatisticCount];
        public int[] TopClanCastle = new int[ConfigValue.StatisticCount];
        public int[] TopClanFort = new int[ConfigValue.StatisticCount];

        public int Hero = 0;
        public int Noble = 0;
        public int Account = 0;
        public int Players = 0;
        public int Clan = 0;
        public int Ally = 0;

        public int Human = 0;
        public int Elf = 0;
        public int DarkElf = 0;
        public int Orc = 0;
        public int Dwarf = 0;
        public int Kamael = 0;
    }

    public static class RaidBoss {
        public String[] RaidBossName = new String[ConfigValue.StatisticRaid.length];
        public byte[] RaidBossStatus = new byte[ConfigValue.StatisticRaid.length];
        public long[] RaidBossDate = new long[ConfigValue.StatisticRaid.length];
        public long[] RaidBossRes = new long[ConfigValue.StatisticRaid.length];
        public long[] RaidBossTime = new long[ConfigValue.StatisticRaid.length];
        public byte[] RaidBossLevel = new byte[ConfigValue.StatisticRaid.length];
    }

    static Manager ManagerStats = new Manager();
    static RaidBoss RaidBossStats = new RaidBoss();

    public long updateCount = 0;
    public long updateTopPK = 0;
    public long updateTopPVP = 0;
    public long updateTopOnline = 0;
    public long updateTopRich = 0;
    public long updateTopClan = 0;
    public long updateEpicBoss = 0;
    public long updateOlyStat = 0;
    public long updateOlyStat2 = 0;

    public long selectUpdateTime(int id) {
        switch (id) {
            case 0:
                return updateCount;
            case 1:
                return updateTopPK;
            case 2:
                return updateTopPVP;
            case 3:
                return updateTopOnline;
            case 4:
                return updateTopRich;
            case 5:
                return updateTopClan;
            case 6:
                return updateEpicBoss;
            case 7:
                return updateOlyStat;
            case 8:
                return updateOlyStat2;
        }
        return 0;
    }

    public long selectConfigUpdateTime(int id) {
        switch (id) {
            case 0:
                return ConfigValue.StatisticUpdateCount;
            case 1:
                return ConfigValue.StatisticUpdateTopPK;
            case 2:
                return ConfigValue.StatisticUpdateTopPVP;
            case 3:
                return ConfigValue.StatisticUpdateTopOnline;
            case 4:
                return ConfigValue.StatisticUpdateTopRich;
            case 5:
                return ConfigValue.StatisticUpdateTopClan;
            case 6:
                return ConfigValue.StatisticUpdateEpicBoss;
            case 7:
                return ConfigValue.StatisticUpdateOlyStat;
        }
        return 0;
    }

    public boolean isUpdateTime(int id) {
        switch (id) {
            case 0:
                return updateCount + ConfigValue.StatisticUpdateCount * 60 < System.currentTimeMillis() / 1000;
            case 1:
                return updateTopPK + ConfigValue.StatisticUpdateTopPK * 60 < System.currentTimeMillis() / 1000;
            case 2:
                return updateTopPVP + ConfigValue.StatisticUpdateTopPVP * 60 < System.currentTimeMillis() / 1000;
            case 3:
                return updateTopOnline + ConfigValue.StatisticUpdateTopOnline * 60 < System.currentTimeMillis() / 1000;
            case 4:
                return updateTopRich + ConfigValue.StatisticUpdateTopRich * 60 < System.currentTimeMillis() / 1000;
            case 5:
                return updateTopClan + ConfigValue.StatisticUpdateTopClan * 60 < System.currentTimeMillis() / 1000;
            case 6:
                return updateEpicBoss + ConfigValue.StatisticUpdateEpicBoss * 60 < System.currentTimeMillis() / 1000;
            case 7:
                return updateOlyStat + ConfigValue.StatisticUpdateOlyStat * 60 < System.currentTimeMillis() / 1000;
            case 8:
                return updateOlyStat2 + ConfigValue.StatisticUpdateOlyStat2 * 60 < System.currentTimeMillis() / 1000;
        }
        return false;
    }

    @Override
    public void parsecmd(String bypass, L2Player player) {
        if (player.getEventMaster() != null && player.getEventMaster().blockBbs())
            return;
        if (player.is_block || player.isInEvent() > 0)
            return;
        //if((Functions.script & CommunityBoardFullStats) != CommunityBoardFullStats)
        //	return;

        if (!ConfigValue.StatisticAllow) {
            player.sendMessage(new CustomMessage("scripts.services.off", player));
            return;
        } else if (bypass.equals("_bbsstatistic:index") || bypass.equals("_bbsgetfav")) {
            if (isUpdateTime(0)) {
                selectClassesCount();
                selectAllyCount();
                selectClanCount();
                selectHeroCount();
                selectNobleCount();
                selectAccount();
                updatePlayerCount();
                updateCount = System.currentTimeMillis() / 1000;
            }
            if (isUpdateTime(1)) {
                selectTopPK();
                updateTopPK = System.currentTimeMillis() / 1000;
            }
            if (isUpdateTime(2)) {
                selectTopPVP();
                updateTopPVP = System.currentTimeMillis() / 1000;
            }
            if (isUpdateTime(3)) {
                selectTopOnline();
                updateTopOnline = System.currentTimeMillis() / 1000;
            }
            if (isUpdateTime(4)) {
                selectTopRich();
                updateTopRich = System.currentTimeMillis() / 1000;
            }
            if (isUpdateTime(5)) {
                selectTopClan();
                updateTopClan = System.currentTimeMillis() / 1000;
            }
            if (isUpdateTime(6)) {
                selectEpicBoss();
                updateEpicBoss = System.currentTimeMillis() / 1000;
            }
            if (isUpdateTime(7)) {
                selectOlyStat();
                updateOlyStat = System.currentTimeMillis() / 1000;
            }
            if (isUpdateTime(8)) {
                selectOlyStat2();
                updateOlyStat2 = System.currentTimeMillis() / 1000;
            }
            show(player, 1);
        } else if (bypass.equals("_bbsstatistic:pk"))
            show(player, 2);
        else if (bypass.equals("_bbsstatistic:pvp"))
            show(player, 3);
        else if (bypass.equals("_bbsstatistic:online"))
            show(player, 4);
        else if (bypass.equals("_bbsstatistic:rich"))
            show(player, 5);
        else if (bypass.equals("_bbsstatistic:clan"))
            show(player, 6);
        else if (bypass.equals("_bbsstatistic:raid"))
            show(player, 7);
        else if (bypass.equals("_bbsstatistic:olymp"))
            show(player, 8);
        else if (bypass.equals("_bbsstatistic:olymp_rate"))
            show(player, 9);
        else {
            String html = DifferentMethods.getErrorHtml(player, bypass);
            separateAndSend(addCustomReplace(html), player);
            return;
        }
    }

    private void show(L2Player player, int page) {
        int number = 0;
        String html;
        CustomMessage No = new CustomMessage("common.result.no", player);
        CustomMessage Yes = new CustomMessage("common.result.yes", player);

        if (page == 1) {
            html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "statistic/index.htm", player);

            html = html.replace("<?account?>", Integer.toString(ManagerStats.Account));
            html = html.replace("<?players?>", Integer.toString(ManagerStats.Players));
            html = html.replace("<?noblesse?>", Integer.toString(ManagerStats.Noble));
            html = html.replace("<?hero?>", Integer.toString(ManagerStats.Hero));
            html = html.replace("<?clan?>", Integer.toString(ManagerStats.Clan));
            html = html.replace("<?ally?>", Integer.toString(ManagerStats.Ally));

            int race = ManagerStats.Human + ManagerStats.Elf + ManagerStats.DarkElf + ManagerStats.Orc + ManagerStats.Dwarf + ManagerStats.Kamael;

            if (race == 0) {
                html = html.replace("<?Human?>", "...");
                html = html.replace("<?Elf?>", "...");
                html = html.replace("<?Dark_Elf?>", "...");
                html = html.replace("<?Orc?>", "...");
                html = html.replace("<?Dwarf?>", "...");
                html = html.replace("<?Kamael?>", "...");

                html = html.replace("<?Human_percent?>", "0%");
                html = html.replace("<?Elf_percent?>", "0%");
                html = html.replace("<?Dark_Elf_percent?>", "0%");
                html = html.replace("<?Orc_percent?>", "0%");
                html = html.replace("<?Dwarf_percent?>", "0%");
                html = html.replace("<?Kamael_percent?>", "0%");
            } else {
                html = html.replace("<?Human?>", Integer.toString(ManagerStats.Human));
                html = html.replace("<?Elf?>", Integer.toString(ManagerStats.Elf));
                html = html.replace("<?Dark_Elf?>", Integer.toString(ManagerStats.DarkElf));
                html = html.replace("<?Orc?>", Integer.toString(ManagerStats.Orc));
                html = html.replace("<?Dwarf?>", Integer.toString(ManagerStats.Dwarf));
                html = html.replace("<?Kamael?>", Integer.toString(ManagerStats.Kamael));

                html = html.replace("<?Human_percent?>", ManagerStats.Human == 0 ? "0%" : Util.prune((double) ManagerStats.Human * 100 / race, 2) + "%");
                html = html.replace("<?Elf_percent?>", ManagerStats.Elf == 0 ? "0%" : Util.prune((double) ManagerStats.Elf * 100 / race, 2) + "%");
                html = html.replace("<?Dark_Elf_percent?>", ManagerStats.DarkElf == 0 ? "0%" : Util.prune((double) ManagerStats.DarkElf * 100 / race, 2) + "%");
                html = html.replace("<?Orc_percent?>", ManagerStats.Orc == 0 ? "0%" : Util.prune((double) ManagerStats.Orc * 100 / race, 2) + "%");
                html = html.replace("<?Dwarf_percent?>", ManagerStats.Dwarf == 0 ? "0%" : Util.prune((double) ManagerStats.Dwarf * 100 / race, 2) + "%");
                html = html.replace("<?Kamael_percent?>", ManagerStats.Kamael == 0 ? "0%" : Util.prune((double) ManagerStats.Kamael * 100 / race, 2) + "%");
            }

            html = html.replace("<?xp?>", Float.toString(RateService.getRateXp(player)));
            html = html.replace("<?sp?>", Float.toString(RateService.getRateSp(player)));
            html = html.replace("<?adena?>", Double.toString(RateService.getRateDropAdena(player)));
            html = html.replace("<?drop?>", Double.toString(RateService.getRateDropItems(player)));
            html = html.replace("<?spoil?>", Double.toString(RateService.getRateDropSpoil(player)));
            html = html.replace("<?manor?>", Double.toString(ConfigValue.RateManor));
            html = html.replace("<?epaulettes?>", Float.toString(ConfigValue.RateDropEpaulette));
            html = html.replace("<?quests?>", Float.toString(ConfigValue.RateQuestsDrop));
            html = html.replace("<?dropRB?>", Double.toString(ConfigValue.RateRaidBoss));
            html = html.replace("<?fish?>", Float.toString(ConfigValue.RateFishDropCount));
        } else if (page == 2) {
            html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "statistic/pk.htm", player);
            while (number < ConfigValue.StatisticCount) {
                if (ManagerStats.TopPkName[number] != null) {
                    html = html.replace("<?name_" + number + "?>", ManagerStats.TopPkName[number]);
                    html = html.replace("<?clan_" + number + "?>", ManagerStats.TopPkClan[number] == null ? "<font color=\"B59A75\">" + No + "</font>" : ManagerStats.TopPkClan[number]);
                    html = html.replace("<?sex_" + number + "?>", ManagerStats.TopPkSex[number] == 0 ? "Мужчина" : "Женщина");
                    html = html.replace("<?class_" + number + "?>", String.valueOf(DifferentMethods.htmlClassNameNonClient(player, ManagerStats.TopPkClass[number])));
                    html = html.replace("<?on_" + number + "?>", ManagerStats.TopPkOn[number] == 1 ? "<font color=\"66FF33\">" + Yes + "</font>" : "<font color=\"B59A75\">" + No + "</font>");
                    html = html.replace("<?online_" + number + "?>", String.valueOf(onlineTime(ManagerStats.TopPkOnline[number])));
                    html = html.replace("<?pk_count_" + number + "?>", Integer.toString(ManagerStats.TopPk[number]));
                    html = html.replace("<?pvp_count_" + number + "?>", Integer.toString(ManagerStats.TopPkPvP[number]));
                } else {
                    html = html.replace("<?name_" + number + "?>", "...");
                    html = html.replace("<?clan_" + number + "?>", "...");
                    html = html.replace("<?sex_" + number + "?>", "...");
                    html = html.replace("<?class_" + number + "?>", "...");
                    html = html.replace("<?on_" + number + "?>", "...");
                    html = html.replace("<?online_" + number + "?>", "...");
                    html = html.replace("<?pk_count_" + number + "?>", "...");
                    html = html.replace("<?pvp_count_" + number + "?>", "...");
                }

                number++;
            }
        } else if (page == 3) {
            html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "statistic/pvp.htm", player);
            while (number < ConfigValue.StatisticCount) {
                if (ManagerStats.TopPvPName[number] != null) {
                    html = html.replace("<?name_" + number + "?>", ManagerStats.TopPvPName[number]);
                    html = html.replace("<?clan_" + number + "?>", ManagerStats.TopPvPClan[number] == null ? "<font color=\"B59A75\">" + No + "</font>" : ManagerStats.TopPvPClan[number]);
                    html = html.replace("<?sex_" + number + "?>", ManagerStats.TopPvPSex[number] == 0 ? "Мужчина" : "Женщина");
                    html = html.replace("<?class_" + number + "?>", String.valueOf(DifferentMethods.htmlClassNameNonClient(player, ManagerStats.TopPvPClass[number])));
                    html = html.replace("<?on_" + number + "?>", ManagerStats.TopPvPOn[number] == 1 ? "<font color=\"66FF33\">" + Yes + "</font>" : "<font color=\"B59A75\">" + No + "</font>");
                    html = html.replace("<?online_" + number + "?>", String.valueOf(onlineTime(ManagerStats.TopPvPOnline[number])));
                    html = html.replace("<?pk_count_" + number + "?>", Integer.toString(ManagerStats.TopPvP[number]));
                    html = html.replace("<?pvp_count_" + number + "?>", Integer.toString(ManagerStats.TopPvPPvP[number]));
                } else {
                    html = html.replace("<?name_" + number + "?>", "...");
                    html = html.replace("<?clan_" + number + "?>", "...");
                    html = html.replace("<?sex_" + number + "?>", "...");
                    html = html.replace("<?class_" + number + "?>", "...");
                    html = html.replace("<?on_" + number + "?>", "...");
                    html = html.replace("<?online_" + number + "?>", "...");
                    html = html.replace("<?pk_count_" + number + "?>", "...");
                    html = html.replace("<?pvp_count_" + number + "?>", "...");
                }
                number++;
            }
        } else if (page == 4) {
            html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "statistic/online.htm", player);
            while (number < ConfigValue.StatisticCount) {
                if (ManagerStats.TopOnlineName[number] != null) {
                    html = html.replace("<?name_" + number + "?>", ManagerStats.TopOnlineName[number]);
                    html = html.replace("<?clan_" + number + "?>", ManagerStats.TopOnlineClan[number] == null ? "<font color=\"B59A75\">" + No + "</font>" : ManagerStats.TopOnlineClan[number]);
                    html = html.replace("<?sex_" + number + "?>", ManagerStats.TopOnlineSex[number] == 0 ? "Мужчина" : "Женщина");
                    html = html.replace("<?class_" + number + "?>", String.valueOf(DifferentMethods.htmlClassNameNonClient(player, ManagerStats.TopOnlineClass[number])));
                    html = html.replace("<?on_" + number + "?>", ManagerStats.TopOnlineOn[number] == 1 ? "<font color=\"66FF33\">" + Yes + "</font>" : "<font color=\"B59A75\">" + No + "</font>");
                    html = html.replace("<?online_" + number + "?>", String.valueOf(onlineTime(ManagerStats.TopOnlineOnline[number])));
                    html = html.replace("<?pk_count_" + number + "?>", Integer.toString(ManagerStats.TopOnline[number]));
                    html = html.replace("<?pvp_count_" + number + "?>", Integer.toString(ManagerStats.TopOnlinePvP[number]));
                } else {
                    html = html.replace("<?name_" + number + "?>", "...");
                    html = html.replace("<?clan_" + number + "?>", "...");
                    html = html.replace("<?sex_" + number + "?>", "...");
                    html = html.replace("<?class_" + number + "?>", "...");
                    html = html.replace("<?on_" + number + "?>", "...");
                    html = html.replace("<?online_" + number + "?>", "...");
                    html = html.replace("<?pk_count_" + number + "?>", "...");
                    html = html.replace("<?pvp_count_" + number + "?>", "...");
                }
                number++;
            }
        } else if (page == 5) {
            html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "statistic/rich.htm", player);
            while (number < ConfigValue.StatisticCount) {
                if (ManagerStats.TopRichName[number] != null) {
                    html = html.replace("<?name_" + number + "?>", ManagerStats.TopRichName[number]);
                    html = html.replace("<?clan_" + number + "?>", ManagerStats.TopRichClan[number] == null ? "<font color=\"B59A75\">" + No + "</font>" : ManagerStats.TopRichClan[number]);
                    html = html.replace("<?sex_" + number + "?>", ManagerStats.TopRichSex[number] == 0 ? "Мужчина" : "Женщина");
                    html = html.replace("<?class_" + number + "?>", String.valueOf(DifferentMethods.htmlClassNameNonClient(player, ManagerStats.TopRichClass[number])));
                    html = html.replace("<?on_" + number + "?>", ManagerStats.TopRichOn[number] == 1 ? "<font color=\"66FF33\">" + Yes + "</font>" : "<font color=\"B59A75\">" + No + "</font>");
                    html = html.replace("<?online_" + number + "?>", String.valueOf(onlineTime(ManagerStats.TopRichOnline[number])));
                    html = html.replace("<?count_" + number + "?>", Util.formatAdena(ManagerStats.TopRich[number]));
                } else {
                    html = html.replace("<?name_" + number + "?>", "...");
                    html = html.replace("<?clan_" + number + "?>", "...");
                    html = html.replace("<?sex_" + number + "?>", "...");
                    html = html.replace("<?class_" + number + "?>", "...");
                    html = html.replace("<?on_" + number + "?>", "...");
                    html = html.replace("<?online_" + number + "?>", "...");
                    html = html.replace("<?count_" + number + "?>", "...");
                }
                number++;
            }
            html = html.replace("<?item_name?>", DifferentMethods.getItemName(ConfigValue.StatisticTopItem));
        } else if (page == 6) {
            html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "statistic/clan.htm", player);
            while (number < ConfigValue.StatisticCount) {
                if (ManagerStats.TopClanName[number] != null) {
                    html = html.replace("<?name_" + number + "?>", ManagerStats.TopClanName[number]);
                    html = html.replace("<?ally_" + number + "?>", ManagerStats.TopClanAlly[number] == null ? "<font color=\"B59A75\">" + No + "</font>" : ManagerStats.TopClanAlly[number]);
                    html = html.replace("<?leader_" + number + "?>", ManagerStats.TopClanLeader[number]);
                    html = html.replace("<?point_" + number + "?>", Util.formatAdena(ManagerStats.TopClanPoint[number]));
                    html = html.replace("<?level_" + number + "?>", Integer.toString(ManagerStats.TopClanLevel[number]));
                    html = html.replace("<?member_" + number + "?>", Integer.toString(ManagerStats.TopClanMember[number]));
                    html = html.replace("<?castle_" + number + "?>", ManagerStats.TopClanCastle[number] == 0 ? "<font color=\"B59A75\">" + No + "</font>" : DifferentMethods.getCastleName(player, ManagerStats.TopClanCastle[number]).toString());

                    String FortName = DifferentMethods.getFortName(player, ManagerStats.TopClanFort[number]).toString();
                    FortName = "<font color=\"LEVEL\"><a action=\"bypass -h _bbsscripts; ;services.FortInfo:fort " + ManagerStats.TopClanFort[number] + ";_bbsstatistic:clan\">" + (FortName.length() > 15 ? (FortName.substring(0, 15) + "...") : FortName) + "</a></font>";
                    html = html.replace("<?fort_" + number + "?>", ManagerStats.TopClanFort[number] == 0 ? "" + No + "</font>" : FortName);
                } else {
                    html = html.replace("<?name_" + number + "?>", "...");
                    html = html.replace("<?ally_" + number + "?>", "...");
                    html = html.replace("<?leader_" + number + "?>", "...");
                    html = html.replace("<?point_" + number + "?>", "...");
                    html = html.replace("<?level_" + number + "?>", "...");
                    html = html.replace("<?member_" + number + "?>", "...");
                    html = html.replace("<?castle_" + number + "?>", "...");
                    html = html.replace("<?fort_" + number + "?>", "...");
                }
                number++;
            }
        } else if (page == 7) {
            html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "statistic/raid.htm", player);
            while (number < ConfigValue.StatisticRaid.length) {
                if (RaidBossStats.RaidBossName[number] != null) {
                    html = html.replace("<?name_" + number + "?>", RaidBossStats.RaidBossName[number]);
                    html = html.replace("<?status_" + number + "?>", RaidBossStats.RaidBossStatus[number] == 0 ? "<font color=\"FF0000\">Мертв</font>" : "<font color=\"66FF33\">Жив</font>");

                    if (ConfigValue.StatisticShowTime)
                        html = html.replace("<?time_" + number + "?>", RaidBossStats.RaidBossTime[number] == 0 ? "..." : time(RaidBossStats.RaidBossTime[number]));
                    else
                        html = html.replace("<?time_" + number + "?>", "<font color=\"FF0000\">Недоступно</font>");

                    if (ConfigValue.StatisticShowDate)
                        html = html.replace("<?date_" + number + "?>", RaidBossStats.RaidBossDate[number] == 0 ? "..." : date(RaidBossStats.RaidBossDate[number]));
                    else
                        html = html.replace("<?date_" + number + "?>", "<font color=\"FF0000\">Недоступно</font>");

                    if (ConfigValue.StatisticShowRespawn)
                        html = html.replace("<?respawn_" + number + "?>", RaidBossStats.RaidBossRes[number] == 0 ? "..." : onlineTime((int) (RaidBossStats.RaidBossRes[number] - (System.currentTimeMillis() / 1000))));
                    else
                        html = html.replace("<?respawn_" + number + "?>", "<font color=\"FF0000\">Недоступно</font>");

                    html = html.replace("<?level_" + number + "?>", Integer.toString(RaidBossStats.RaidBossLevel[number]));
                } else {
                    html = html.replace("<?name_" + number + "?>", "...");
                    html = html.replace("<?status_" + number + "?>", "...");
                    html = html.replace("<?time_" + number + "?>", "...");
                    html = html.replace("<?date_" + number + "?>", "...");
                    html = html.replace("<?respawn_" + number + "?>", "...");
                    html = html.replace("<?level_" + number + "?>", "...");
                }
                number++;
            }
        }
        /**
         Сделать в Альт Б страничку где будет писать топ10 птс на олимпиаде, обновлятся будет раз в сутки.
         А лучше сделать конфиг - через сколько обновлять топ птс в альт б.
         В альт Б будет топ 10 человек набравшие самое большее колличество птс.
         **/
        else if (page == 8) {
            html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "statistic/olympiad.htm", player);
            StringBuffer msg = new StringBuffer("");

            int index = 0;

            for (Entry<Integer, StatsSet> entry : _top_pts.entrySet()) {
                Integer charId = entry.getKey();
                StatsSet set = entry.getValue();

                L2Player pl = L2ObjectsStorage.getPlayer(charId);

                msg.append("<table width=740>");
                msg.append("	<tr>");
                msg.append("		<td width=740 align=center>");
                msg.append("			<table width=740 height=25 bgcolor=A7A19A>");
                msg.append("				<tr>");
                msg.append("					<td>");
                msg.append("						<table>");
                msg.append("							<tr>");
                msg.append("								<td width=20 valign=top>");
                msg.append("									<img src=\"l2ui_ch3.party_summmon_num" + (index + 1) + "\" width=\"16\" height=\"16\">");
                msg.append("								</td>");
                msg.append("								<td width=120 valign=top>");
                msg.append("									<font color=\"ffd700\">" + set.getString(Olympiad.CHAR_NAME, "") + "</font>");
                msg.append("								</td>");
                msg.append("								<td width=140 align=\"center\" valign=top>");
                msg.append(String.valueOf(DifferentMethods.htmlClassNameNonClient(player, set.getInteger(Olympiad.CLASS_ID))));
                msg.append("								</td>");
                msg.append("								<td width=60 align=\"center\" valign=top>");
                msg.append(String.valueOf(set.getInteger(Olympiad.POINTS)));
                msg.append("								</td>");
                msg.append("								<td width=50 align=\"center\" valign=top>");
                msg.append((pl != null && !pl.isInOfflineMode()) ? "<font color=\"66FF33\">" + Yes + "</font>" : "<font color=\"B59A75\">" + No + "</font>");
                msg.append("								</td>");
                msg.append("							</tr>");
                msg.append("						</table>");
                msg.append("					</td>");
                msg.append("				</tr>");
                msg.append("			</table>");
                msg.append("		</td>");
                msg.append("	</tr>");
                msg.append("</table>");
                if ((++index) >= 10)
                    break;
            }
            for (int i = index; i < 10; i++) {
                msg.append("<table width=740>");
                msg.append("	<tr>");
                msg.append("		<td width=740 align=center>");
                msg.append("			<table width=740 height=25 bgcolor=A7A19A>");
                msg.append("				<tr>");
                msg.append("					<td>");
                msg.append("						<table>");
                msg.append("							<tr>");
                msg.append("								<td width=20 valign=top>");
                msg.append("									<img src=\"l2ui_ch3.party_summmon_num" + (index + 1) + "\" width=\"16\" height=\"16\">");
                msg.append("								</td>");
                msg.append("								<td width=120 valign=top>");
                msg.append("									<font color=\"ffd700\">...</font>");
                msg.append("								</td>");
                msg.append("								<td width=140 align=\"center\" valign=top>");
                msg.append("									...");
                msg.append("								</td>");
                msg.append("								<td width=60 align=\"center\" valign=top>");
                msg.append("									...");
                msg.append("								</td>");
                msg.append("								<td width=50 align=\"center\" valign=top>");
                msg.append("									...");
                msg.append("								</td>");
                msg.append("							</tr>");
                msg.append("						</table>");
                msg.append("					</td>");
                msg.append("				</tr>");
                msg.append("			</table>");
                msg.append("		</td>");
                msg.append("	</tr>");
                msg.append("</table>");
            }

            html = html.replace("<?info?>", msg.toString());
        } else if (page == 9) {
            html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "statistic/olympiad_rate.htm", player);
            StringBuffer msg = new StringBuffer("");

            int index = 0;

            for (Entry<Integer, StatsSet> entry : _top_win_rate.entrySet()) {
                Integer charId = entry.getKey();
                StatsSet set = entry.getValue();
                if (set.getInteger(Olympiad.COMP_DONE) < ConfigValue.OlyStatMinBattle)
                    continue;

                L2Player pl = L2ObjectsStorage.getPlayer(charId);

                msg.append("<table width=740>");
                msg.append("	<tr>");
                msg.append("		<td width=740 align=center>");
                msg.append("			<table width=740 height=25 bgcolor=A7A19A>");
                msg.append("				<tr>");
                msg.append("					<td>");
                msg.append("						<table>");
                msg.append("							<tr>");
                msg.append("								<td width=20 valign=top>");
                msg.append("									<img src=\"l2ui_ch3.party_summmon_num" + (index + 1) + "\" width=\"16\" height=\"16\">");
                msg.append("								</td>");
                msg.append("								<td width=120 valign=top>");
                msg.append("									<font color=\"ffd700\">" + set.getString(Olympiad.CHAR_NAME, "") + "</font>");
                msg.append("								</td>");
                msg.append("								<td width=140 align=\"center\" valign=top>");
                msg.append(String.valueOf(DifferentMethods.htmlClassNameNonClient(player, set.getInteger(Olympiad.CLASS_ID))));
                msg.append("								</td>");
                //msg.append("								<td width=60 align=\"center\" valign=top>");
                //msg.append(String.valueOf((set.getFloat(Olympiad.COMP_WIN)/Math.max(set.getFloat(Olympiad.COMP_DONE), 1))*1000));
                //msg.append("								</td>");
                msg.append("								<td width=50 align=\"center\" valign=top>");
                msg.append((pl != null && !pl.isInOfflineMode()) ? "<font color=\"66FF33\">" + Yes + "</font>" : "<font color=\"B59A75\">" + No + "</font>");
                msg.append("								</td>");
                msg.append("								<td width=50 align=\"center\" valign=top>");
                msg.append(String.valueOf(set.getFloat(Olympiad.COMP_DONE)));
                msg.append("								</td>");
                msg.append("								<td width=50 align=\"center\" valign=top>");
                msg.append(String.valueOf(set.getFloat(Olympiad.COMP_WIN)));
                msg.append("								</td>");
                msg.append("							</tr>");
                msg.append("						</table>");
                msg.append("					</td>");
                msg.append("				</tr>");
                msg.append("			</table>");
                msg.append("		</td>");
                msg.append("	</tr>");
                msg.append("</table>");
                if ((++index) >= 10)
                    break;
            }
            for (int i = index; i < 10; i++) {
                msg.append("<table width=740>");
                msg.append("	<tr>");
                msg.append("		<td width=740 align=center>");
                msg.append("			<table width=740 height=25 bgcolor=A7A19A>");
                msg.append("				<tr>");
                msg.append("					<td>");
                msg.append("						<table>");
                msg.append("							<tr>");
                msg.append("								<td width=20 valign=top>");
                msg.append("									<img src=\"l2ui_ch3.party_summmon_num" + (index + 1) + "\" width=\"16\" height=\"16\">");
                msg.append("								</td>");
                msg.append("								<td width=120 valign=top>");
                msg.append("									<font color=\"ffd700\">...</font>");
                msg.append("								</td>");
                msg.append("								<td width=140 align=\"center\" valign=top>");
                msg.append("									...");
                msg.append("								</td>");
                //msg.append("								<td width=60 align=\"center\" valign=top>");
                //msg.append("									...");
                //msg.append("								</td>");
                msg.append("								<td width=50 align=\"center\" valign=top>");
                msg.append("									...");
                msg.append("								</td>");
                msg.append("								<td width=50 align=\"center\" valign=top>");
                msg.append("									...");
                msg.append("								</td>");
                msg.append("								<td width=50 align=\"center\" valign=top>");
                msg.append("									...");
                msg.append("								</td>");
                msg.append("							</tr>");
                msg.append("						</table>");
                msg.append("					</td>");
                msg.append("				</tr>");
                msg.append("			</table>");
                msg.append("		</td>");
                msg.append("	</tr>");
                msg.append("</table>");
            }

            html = html.replace("<?info?>", msg.toString());
        } else {
            html = DifferentMethods.getErrorHtml(player, "_bbsstatistic:" + page);
            separateAndSend(addCustomReplace(html), player);
            return;
        }

        html = html.replace("<?update?>", String.valueOf(selectConfigUpdateTime(page - 1)));
        html = html.replace("<?last_update?>", String.valueOf(time(selectUpdateTime(page - 1))));
        html = html.replace("<?statistic_menu?>", Files.read(ConfigValue.CommunityBoardHtmlRoot + "statistic/menu.htm", player));
        separateAndSend(addCustomReplace(html), player);
    }

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public static String date(long time) {
        return DATE_FORMAT.format(new Date(time * 1000));
    }

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final DateFormat TIME_FORMAT2 = new SimpleDateFormat("HH:00");

    public static String time(long time) {
        if (ConfigValue.StatisticRaidShowMin)
            return TIME_FORMAT.format(new Date(time * 1000));
        return TIME_FORMAT2.format(new Date(time * 1000));
    }

    private void selectClassesCount() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;

        ManagerStats.Human = 0;
        ManagerStats.Elf = 0;
        ManagerStats.DarkElf = 0;
        ManagerStats.Orc = 0;
        ManagerStats.Dwarf = 0;
        ManagerStats.Kamael = 0;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT class_id FROM character_subclasses WHERE isBase = '1';");
            rset = statement.executeQuery();

            while (rset.next()) {
                if (rset.getInt("class_id") >= 0 && rset.getInt("class_id") <= 17 || rset.getInt("class_id") >= 88 && rset.getInt("class_id") <= 98)
                    ManagerStats.Human++;
                if (rset.getInt("class_id") >= 18 && rset.getInt("class_id") <= 30 || rset.getInt("class_id") >= 99 && rset.getInt("class_id") <= 105)
                    ManagerStats.Elf++;
                if (rset.getInt("class_id") >= 31 && rset.getInt("class_id") <= 43 || rset.getInt("class_id") >= 106 && rset.getInt("class_id") <= 112)
                    ManagerStats.DarkElf++;
                if (rset.getInt("class_id") >= 44 && rset.getInt("class_id") <= 52 || rset.getInt("class_id") >= 113 && rset.getInt("class_id") <= 116)
                    ManagerStats.Orc++;
                if (rset.getInt("class_id") >= 53 && rset.getInt("class_id") <= 57 || rset.getInt("class_id") >= 117 && rset.getInt("class_id") <= 118)
                    ManagerStats.Dwarf++;
                if (rset.getInt("class_id") >= 123 && rset.getInt("class_id") <= 136)
                    ManagerStats.Kamael++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
    }

    private void selectHeroCount() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;

        ManagerStats.Hero = 0;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT char_id FROM heroes;");
            rset = statement.executeQuery();

            while (rset.next())
                ManagerStats.Hero++;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
    }

    private void selectAccount() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;

        ManagerStats.Account = 0;

        try {
            con = L2DatabaseFactory.getInstanceLogin().getConnection();
            statement = con.prepareStatement("SELECT login FROM accounts;");
            rset = statement.executeQuery();

            while (rset.next())
                ManagerStats.Account++;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
    }

    private void selectNobleCount() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;

        ManagerStats.Noble = 0;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT char_id FROM olympiad_nobles;");
            rset = statement.executeQuery();

            while (rset.next())
                ManagerStats.Noble++;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
    }

    private void selectClanCount() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;

        ManagerStats.Clan = 0;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT clan_id FROM clan_data;");
            rset = statement.executeQuery();

            while (rset.next())
                ManagerStats.Clan++;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
    }

    private void selectAllyCount() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;

        ManagerStats.Ally = 0;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT leader_id FROM ally_data WHERE leader_id != '0';");
            rset = statement.executeQuery();

            while (rset.next())
                ManagerStats.Ally++;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
    }

    public void selectTopPVP() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;
        int number = 0;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT char_name, obj_Id, class_id, clanid, sex, online, onlinetime, pkkills, pvpkills FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id) WHERE cs.isBase=1 ORDER BY pvpkills DESC LIMIT " + (ConfigValue.StatisticCount * 2));
            rset = statement.executeQuery();

            while (rset.next()) {
                if (!rset.getString("char_name").isEmpty()) {
                    int obj_Id = rset.getInt("obj_Id");
                    if (ConfigSystem.gmlist.containsKey(obj_Id) && ConfigSystem.gmlist.get(obj_Id).IsGM)
                        continue;
                    ManagerStats.TopPvPName[number] = rset.getString("char_name");
                    int clan_id = rset.getInt("clanid");
                    L2Clan clan = clan_id == 0 ? null : ClanTable.getInstance().getClan(clan_id);
                    ManagerStats.TopPvPClan[number] = clan == null ? null : clan.getName();
                    ManagerStats.TopPvPSex[number] = rset.getInt("sex");
                    ManagerStats.TopPvPClass[number] = rset.getInt("class_id");
                    ManagerStats.TopPvPOn[number] = rset.getInt("online");
                    ManagerStats.TopPvPOnline[number] = rset.getInt("onlinetime");
                    ManagerStats.TopPvP[number] = rset.getInt("pkkills");
                    ManagerStats.TopPvPPvP[number] = rset.getInt("pvpkills");
                } else {
                    ManagerStats.TopPvPName[number] = null;
                    ManagerStats.TopPvPClan[number] = null;
                    ManagerStats.TopPvPSex[number] = 0;
                    ManagerStats.TopPvPClass[number] = 0;
                    ManagerStats.TopPvPOn[number] = 0;
                    ManagerStats.TopPvPOnline[number] = 0;
                    ManagerStats.TopPvP[number] = 0;
                    ManagerStats.TopPvPPvP[number] = 0;
                }
                number++;
                if (number >= ConfigValue.StatisticCount)
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
        return;
    }

    public void selectTopPK() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;
        int number = 0;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT char_name, obj_Id, class_id, clanid, sex, online, onlinetime, pkkills, pvpkills FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id) WHERE cs.isBase=1 ORDER BY pkkills DESC LIMIT " + (ConfigValue.StatisticCount * 2));
            rset = statement.executeQuery();
            while (rset.next()) {
                if (!rset.getString("char_name").isEmpty()) {
                    int obj_Id = rset.getInt("obj_Id");
                    if (ConfigSystem.gmlist.containsKey(obj_Id) && ConfigSystem.gmlist.get(obj_Id).IsGM)
                        continue;
                    ManagerStats.TopPkName[number] = rset.getString("char_name");
                    int clan_id = rset.getInt("clanid");
                    L2Clan clan = clan_id == 0 ? null : ClanTable.getInstance().getClan(clan_id);
                    ManagerStats.TopPkClan[number] = clan == null ? null : clan.getName();
                    ManagerStats.TopPkSex[number] = rset.getInt("sex");
                    ManagerStats.TopPkClass[number] = rset.getInt("class_id");
                    ManagerStats.TopPkOn[number] = rset.getInt("online");
                    ManagerStats.TopPkOnline[number] = rset.getInt("onlinetime");
                    ManagerStats.TopPk[number] = rset.getInt("pkkills");
                    ManagerStats.TopPkPvP[number] = rset.getInt("pvpkills");
                } else {
                    ManagerStats.TopPkName[number] = null;
                    ManagerStats.TopPkClan[number] = null;
                    ManagerStats.TopPkSex[number] = 0;
                    ManagerStats.TopPkClass[number] = 0;
                    ManagerStats.TopPkOn[number] = 0;
                    ManagerStats.TopPkOnline[number] = 0;
                    ManagerStats.TopPk[number] = 0;
                    ManagerStats.TopPkPvP[number] = 0;
                }
                number++;
                if (number >= ConfigValue.StatisticCount)
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
    }

    public void selectTopRich() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;
        int number = 0;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            //statement = con.prepareStatement("SELECT count, char_name, class_id, clanid, sex, online, onlinetime FROM items AS i JOIN characters AS c JOIN character_subclasses AS cs ON c.obj_Id = i.owner_id WHERE i.item_id='" + ConfigValue.StatisticTopItem + "' AND c.obj_Id = cs.char_obj_id AND cs.isBase = '1' ORDER BY i.count DESC LIMIT 0," + ConfigValue.StatisticCount);
            statement = con.prepareStatement("SELECT count, char_name, obj_Id, class_id, clanid, sex, online, onlinetime FROM characters LEFT JOIN character_subclasses ON characters.obj_Id = character_subclasses.char_obj_id AND character_subclasses.isBase='1' LEFT JOIN (SELECT owner_id,SUM(count) AS count FROM items WHERE items.item_id = " + (ConfigValue.StatisticTopItem) + " GROUP BY owner_id ORDER BY count DESC LIMIT " + (ConfigValue.StatisticCount * 2) + ") AS count ON characters.obj_Id=count.owner_id WHERE characters.accesslevel = 0 ORDER BY count DESC, onlinetime DESC LIMIT 0," + ConfigValue.StatisticCount);

            rset = statement.executeQuery();
            while (rset.next()) {
                if (!rset.getString("char_name").isEmpty()) {
                    int obj_Id = rset.getInt("obj_Id");
                    if (ConfigSystem.gmlist.containsKey(obj_Id) && ConfigSystem.gmlist.get(obj_Id).IsGM)
                        continue;
                    ManagerStats.TopRichName[number] = rset.getString("char_name");
                    int clan_id = rset.getInt("clanid");
                    L2Clan clan = clan_id == 0 ? null : ClanTable.getInstance().getClan(clan_id);
                    ManagerStats.TopRichClan[number] = clan == null ? null : clan.getName();
                    ManagerStats.TopRichSex[number] = rset.getInt("sex");
                    ManagerStats.TopRichClass[number] = rset.getInt("class_id");
                    ManagerStats.TopRichOn[number] = rset.getInt("online");
                    ManagerStats.TopRichOnline[number] = rset.getInt("onlinetime");
                    ManagerStats.TopRich[number] = rset.getLong("count");
                } else {
                    ManagerStats.TopRichName[number] = null;
                    ManagerStats.TopRichClan[number] = null;
                    ManagerStats.TopRichSex[number] = 0;
                    ManagerStats.TopRichClass[number] = 0;
                    ManagerStats.TopRichOn[number] = 0;
                    ManagerStats.TopRichOnline[number] = 0;
                    ManagerStats.TopRich[number] = 0;
                }
                number++;
                if (number >= ConfigValue.StatisticCount)
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
    }

    public void selectTopClan() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;
        int number = 0;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT clan_id FROM clan_data ORDER BY clan_level DESC, reputation_score DESC LIMIT 0," + ConfigValue.StatisticCount + ";");
            rset = statement.executeQuery();

            while (rset.next()) {
                int clan_id = rset.getInt("clan_id");
                L2Clan clan = clan_id == 0 ? null : ClanTable.getInstance().getClan(clan_id);

                if (clan != null) {
                    ManagerStats.TopClanName[number] = clan.getName();
                    ManagerStats.TopClanAlly[number] = clan.getAlliance() == null ? null : clan.getAlliance().getAllyName();
                    ManagerStats.TopClanLeader[number] = clan.getLeaderName();
                    ManagerStats.TopClanLevel[number] = clan.getLevel();
                    ManagerStats.TopClanPoint[number] = clan.getReputationScore();
                    ManagerStats.TopClanMember[number] = clan.getMembersCount();
                    ManagerStats.TopClanCastle[number] = clan.getHasCastle();
                    ManagerStats.TopClanFort[number] = clan.getHasFortress();
                } else {
                    ManagerStats.TopClanName[number] = null;
                    ManagerStats.TopClanAlly[number] = null;
                    ManagerStats.TopClanLeader[number] = null;
                    ManagerStats.TopClanLevel[number] = 0;
                    ManagerStats.TopClanPoint[number] = 0;
                    ManagerStats.TopClanMember[number] = 0;
                    ManagerStats.TopClanCastle[number] = 0;
                    ManagerStats.TopClanFort[number] = 0;
                }
                number++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
    }

    public void selectTopOnline() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;
        int number = 0;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT char_name, obj_Id, class_id, clanid, sex, online, onlinetime, pkkills, pvpkills FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id) WHERE cs.isBase=1 ORDER BY onlinetime DESC LIMIT " + (ConfigValue.StatisticCount * 2));
            rset = statement.executeQuery();
            while (rset.next()) {
                if (!rset.getString("char_name").isEmpty()) {
                    int obj_Id = rset.getInt("obj_Id");
                    if (ConfigSystem.gmlist.containsKey(obj_Id) && ConfigSystem.gmlist.get(obj_Id).IsGM)
                        continue;
                    ManagerStats.TopOnlineName[number] = rset.getString("char_name");
                    int clan_id = rset.getInt("clanid");
                    L2Clan clan = clan_id == 0 ? null : ClanTable.getInstance().getClan(clan_id);
                    ManagerStats.TopOnlineClan[number] = clan == null ? null : clan.getName();
                    ManagerStats.TopOnlineSex[number] = rset.getInt("sex");
                    ManagerStats.TopOnlineClass[number] = rset.getInt("class_id");
                    ManagerStats.TopOnlineOn[number] = rset.getInt("online");
                    ManagerStats.TopOnlineOnline[number] = rset.getInt("onlinetime");
                    ManagerStats.TopOnline[number] = rset.getInt("pkkills");
                    ManagerStats.TopOnlinePvP[number] = rset.getInt("pvpkills");
                } else {
                    ManagerStats.TopOnlineName[number] = null;
                    ManagerStats.TopOnlineClan[number] = null;
                    ManagerStats.TopOnlineSex[number] = 0;
                    ManagerStats.TopOnlineClass[number] = 0;
                    ManagerStats.TopOnlineOn[number] = 0;
                    ManagerStats.TopOnlineOnline[number] = 0;
                    ManagerStats.TopOnline[number] = 0;
                    ManagerStats.TopOnlinePvP[number] = 0;
                }
                number++;
                if (number >= ConfigValue.StatisticCount)
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
    }

    public void selectOlyStat() {
        _top_pts = Collections.synchronizedMap(Olympiad._nobles);
        _top_pts = ValueSortMap.sortMapByValue(_top_pts, comparator_pts);
    }

    public void selectOlyStat2() {
        _top_win_rate = Collections.synchronizedMap(Olympiad._nobles);
        _top_win_rate = ValueSortMap.sortMapByValue(_top_win_rate, comparator_rate);
    }

    public void selectEpicBoss() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;
        int number = 0;

        String rb_list = "";
        for (int i = 0; i < ConfigValue.StatisticRaid.length; i++) {
            if (i == ConfigValue.StatisticRaid.length - 1)
                rb_list += "'" + ConfigValue.StatisticRaid[i] + "'";
            else
                rb_list += "'" + ConfigValue.StatisticRaid[i] + "',";
        }


        for (int i = 0; i < ConfigValue.StatisticRaid.length; i++) {
            int npc_id = ConfigValue.StatisticRaid[i];
            if (npc_id != 29118) {
                long time = RaidBossSpawnManager.getInstance().getRespawnTime(npc_id) + System.currentTimeMillis();

                //_log.info("npc_id: "+npc_id+" time="+RaidBossSpawnManager.getInstance().getRespawnTime(npc_id));
                L2NpcTemplate template = NpcTable.getTemplate(npc_id);
                RaidBossStats.RaidBossName[number] = template.name;
                RaidBossStats.RaidBossLevel[number] = template.level;

                if (time > System.currentTimeMillis()) {
                    RaidBossStats.RaidBossStatus[number] = 0;
                    RaidBossStats.RaidBossTime[number] = time / 1000;
                    RaidBossStats.RaidBossDate[number] = time / 1000;
                    RaidBossStats.RaidBossRes[number] = time / 1000;
                } else if (_epic_state.containsKey(npc_id)) {
                    EpicBossState state = _epic_state.get(npc_id);
                    if (state.getRespawnDate() > System.currentTimeMillis()) {
                        RaidBossStats.RaidBossStatus[number] = 0;
                        RaidBossStats.RaidBossTime[number] = state.getRespawnDate() / 1000;
                        RaidBossStats.RaidBossDate[number] = state.getRespawnDate() / 1000;
                        RaidBossStats.RaidBossRes[number] = state.getRespawnDate() / 1000;
                    } else {
                        RaidBossStats.RaidBossStatus[number] = 1;
                        RaidBossStats.RaidBossTime[number] = 0;
                        RaidBossStats.RaidBossDate[number] = 0;
                        RaidBossStats.RaidBossRes[number] = 0;
                    }
                } else {
                    RaidBossStats.RaidBossStatus[number] = 1;
                    RaidBossStats.RaidBossTime[number] = 0;
                    RaidBossStats.RaidBossDate[number] = 0;
                    RaidBossStats.RaidBossRes[number] = 0;
                }
                number++;
            }
        }
        if (ConfigValue.StatisticRaid.length > number && Util.contains(ConfigValue.StatisticRaid, 29118)) {
            L2NpcTemplate template = NpcTable.getTemplate(29118);
            RaidBossStats.RaidBossName[number] = template.name;
            RaidBossStats.RaidBossLevel[number] = template.level;

            long res = ServerVariables.getLong("BelethKillTime", 0);
            if (res > System.currentTimeMillis()) {
                RaidBossStats.RaidBossStatus[number] = 0;
                RaidBossStats.RaidBossTime[number] = res / 1000;
                RaidBossStats.RaidBossDate[number] = res / 1000;
                RaidBossStats.RaidBossRes[number] = res / 1000;
            } else {
                RaidBossStats.RaidBossStatus[number] = 1;
                RaidBossStats.RaidBossTime[number] = 0;
                RaidBossStats.RaidBossDate[number] = 0;
                RaidBossStats.RaidBossRes[number] = 0;
            }
        }
		/*if(ConfigValue.StatisticRaid.length > number)
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT bossId, respawnDate, state FROM `epic_boss_spawn` UNION SELECT id, respawn_delay, 3 FROM `raidboss_status` WHERE id in ("+rb_list+")");
			rset = statement.executeQuery();

			while(rset.next())
			{
				for(int i = 0; i < ConfigValue.StatisticRaid.length; i++)
				{
					if(ConfigValue.StatisticRaid.length <= number)
						break;
					if(ConfigValue.StatisticRaid[i] == rset.getInt("bossId") && ConfigValue.StatisticRaid[i] != 29118)
					{
						L2NpcTemplate template = NpcTable.getTemplate(rset.getInt("bossId"));
						if(template == null)
						{
							_log.info("CommunityBoardFullStats: Select RB info wrong npc id: "+rset.getInt("bossId"));
							continue;
						}
						RaidBossStats.RaidBossName[number] = template.name;
						RaidBossStats.RaidBossLevel[number] = template.level;

						long res = rset.getLong("respawnDate");
						long state = rset.getLong("state");
						if(state == 3 && res > (System.currentTimeMillis() / 1000))
						{
							RaidBossStats.RaidBossStatus[number] = 0;
							RaidBossStats.RaidBossTime[number] = res;
							RaidBossStats.RaidBossDate[number] = res;
							RaidBossStats.RaidBossRes[number] = res;
						}
						else
						{
							RaidBossStats.RaidBossStatus[number] = 1;
							RaidBossStats.RaidBossTime[number] = 0;
							RaidBossStats.RaidBossDate[number] = 0;
							RaidBossStats.RaidBossRes[number] = 0;
						}
						number++;
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}*/
    }

    private void updatePlayerCount() {
        if (ConfigValue.OnlineCheatEnable) {
            if (ConfigValue.OnlineCheatPercentEnable) {
                ManagerStats.Human = ManagerStats.Human + ManagerStats.Human * ConfigValue.OnlineCheatPercent / 100;
                ManagerStats.Elf = ManagerStats.Elf + ManagerStats.Elf * ConfigValue.OnlineCheatPercent / 100;
                ManagerStats.DarkElf = ManagerStats.DarkElf + ManagerStats.DarkElf * ConfigValue.OnlineCheatPercent / 100;
                ManagerStats.Orc = ManagerStats.Orc + ManagerStats.Orc * ConfigValue.OnlineCheatPercent / 100;
                ManagerStats.Dwarf = ManagerStats.Dwarf + ManagerStats.Dwarf * ConfigValue.OnlineCheatPercent / 100;
                ManagerStats.Kamael = ManagerStats.Kamael + ManagerStats.Kamael * ConfigValue.OnlineCheatPercent / 100;
                ManagerStats.Account = ManagerStats.Account + ManagerStats.Account * ConfigValue.OnlineCheatPercent / 100;
            } else {
                ManagerStats.Human += ConfigValue.OnlineCheatCount;
                ManagerStats.Elf += ConfigValue.OnlineCheatCount;
                ManagerStats.DarkElf += ConfigValue.OnlineCheatCount;
                ManagerStats.Orc += ConfigValue.OnlineCheatCount;
                ManagerStats.Dwarf += ConfigValue.OnlineCheatCount;
                ManagerStats.Kamael += ConfigValue.OnlineCheatCount;
                ManagerStats.Account += ConfigValue.OnlineCheatCount;
            }
        }
        ManagerStats.Players = ManagerStats.Human + ManagerStats.Elf + ManagerStats.DarkElf + ManagerStats.Orc + ManagerStats.Dwarf + ManagerStats.Kamael;
        if (ManagerStats.Account < ManagerStats.Players)
            ManagerStats.Account = ManagerStats.Players + ManagerStats.Players * 5 / 100;
    }

    String onlineTime(int time) {
        String result = "...";

        if (time <= 0)
            return result;

        int days = 0, hours = 0, minutes = 0;

        days = time / (24 * 3600);
        hours = (time - days * 24 * 3600) / 3600;
        minutes = (time - days * 24 * 3600 - hours * 3600) / 60;


        if (days >= 1)
            result = days + " д. " + hours + " ч.";
        else if (hours >= 1)
            result = hours + " ч. " + minutes + " м.";
        else if (minutes >= 1)
            result = minutes + " м.";

        return result;
    }

    @Override
    public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar) {
    }

    static Comparator comparator_pts = new Comparator<StatsSet>() {
        @Override
        public int compare(StatsSet o1, StatsSet o2) {
            return (int) (o2.getInteger(Olympiad.POINTS) - o1.getInteger(Olympiad.POINTS));
        }
    };

    static Comparator comparator_rate = new Comparator<StatsSet>() {
        @Override
        public int compare(StatsSet o1, StatsSet o2) {
            return (int) ((o2.getFloat(Olympiad.COMP_WIN) / Math.max(o2.getFloat(Olympiad.COMP_DONE), 1)) * 1000 - (o1.getFloat(Olympiad.COMP_WIN) / Math.max(o1.getFloat(Olympiad.COMP_DONE), 1)) * 1000);
        }
    };

    public static void addEpic(EpicBossState state) {
        _epic_state.put(state.getBossId(), state);
    }

    @Override
    public void onLoad() {
        CommunityHandler.getInstance().registerCommunityHandler(this);

        if (ConfigValue.StatisticAllow) {
            // К - костыли
            reloadAll();
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                public void run() {
                    reloadAll();
                }
            }, 15000);
            _log.info("Full statistics in the commynity board has been updated.");
        }
    }

    private void reloadAll() {
        selectClassesCount();
        selectAllyCount();
        selectClanCount();
        selectHeroCount();
        selectNobleCount();
        selectOlyStat();
        selectOlyStat2();
        selectAccount();
        selectTopPK();
        selectTopPVP();
        selectTopOnline();
        selectTopRich();
        selectTopClan();
        selectEpicBoss();
        updatePlayerCount();
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}