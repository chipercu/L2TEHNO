package events.Simple;

import java.io.File;
import java.util.logging.Logger;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Rnd;
import l2open.util.Util;

/**
 * <b>Author:</b> L2CCCP<br>
 * <b>Date:</b> 12.02.2013<br>
 * <b>Time:</b> 11:38:00<br>
 * <b>Description:</b> Event Simple, order by client MAYGLI.
 */
public class Simple extends Functions implements ScriptFile {
    private static final Logger _log = Logger.getLogger(Simple.class.getName());
    private static GArray<L2Spawn> _spawns = new GArray<L2Spawn>();

    private static boolean _active = false;
    private static boolean MultiSellLoaded = false;

    private static File multiSellFile = new File(ConfigValue.DatapackRoot, "data/scripts/events/Simple/" + ConfigValue.ESimpleManager + ".xml");

    public void onLoad() {
        if (isActive()) {
            _active = true;
            loadMultiSell();
            spawnEventManagers();
            _log.info("Loaded Event: Simple [state: activated]");
        } else
            _log.info("Loaded Event: Simple [state: deactivated]");
    }

    /**
     * Читает статус ивента из базы.
     * @return
     */
    private static boolean isActive() {
        return IsActive("SimpleEvent");
    }

    /**
     * Запускает ивент
     */
    public void startEvent() {
        L2Player player = (L2Player) getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (SetActive("SimpleEvent", true)) {
            loadMultiSell();
            spawnEventManagers();
            _log.info("Event 'Simple' started.");
            Announcements.getInstance().announceByCustomMessage("events.simple.AnnounceEventStarted", null);
        } else
            player.sendMessage("Event 'Simple' already started.");

        _active = true;

        show(Files.read("data/html/admin/events/events.htm", player), player);
    }

    /**
     * Останавливает ивент
     */
    public void stopEvent() {
        L2Player player = (L2Player) getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (SetActive("SimpleEvent", false)) {
            unSpawnEventManagers();
            _log.info("Event 'Simple' stopped.");
            Announcements.getInstance().announceByCustomMessage("events.simple.AnnounceEventStoped", null);
        } else
            player.sendMessage("Event 'Simple' not started.");

        _active = false;

        show(Files.read("data/html/admin/events/events.htm", player), player);
    }

    /**
     * Спавнит ивент менеджеров
     */
    private void spawnEventManagers() {
        final int EVENT_MANAGERS[][] = {
                {81921, 148921, -3467, 16384},
                {146405, 28360, -2269, 49648},
                {19319, 144919, -3103, 31135},
                {-82805, 149890, -3129, 33202},
                {-12347, 122549, -3104, 32603},
                {110642, 220165, -3655, 61898},
                {116619, 75463, -2721, 20881},
                {85513, 16014, -3668, 23681},
                {81999, 53793, -1496, 61621},
                {148159, -55484, -2734, 44315},
                {44185, -48502, -797, 27479},
                {86899, -143229, -1293, 22021}};

        SpawnNPCs(ConfigValue.ESimpleManager, EVENT_MANAGERS, _spawns);
        _log.info("SpawnManager: spawned npc for event: Simple");
    }

    /**
     * Удаляет спавн ивент менеджеров
     */
    private void unSpawnEventManagers() {
        deSpawnNPCs(_spawns);
        _log.info("SpawnManager: despawned npc for event: Simple");
    }

    /**
     * Загружаем мультиселл.
     */
    private static void loadMultiSell() {
        if (!MultiSellLoaded)
            return;
        L2Multisell.getInstance().parseFile(multiSellFile);
        MultiSellLoaded = true;
    }

    public void onReload() {
        unSpawnEventManagers();
        if (MultiSellLoaded) {
            L2Multisell.getInstance().remove(multiSellFile);
            MultiSellLoaded = false;
        }
    }

    public void onShutdown() {
        unSpawnEventManagers();
    }

    public void show(String var[]) {
        L2Player player = (L2Player) getSelf();
        L2NpcInstance npc = getNpc();

        if (npc.getNpcId() != ConfigValue.ESimpleManager)
            return;

        NpcHtmlMessage html = new NpcHtmlMessage(player, npc);
        html.setFile("data/scripts/events/Simple/" + var[0]);
        String mitems = "", ritems = "";
        for (int i = 0; i < ConfigValue.ESimple.length; i++)
            mitems += ((i != 0 ? (ConfigValue.ESimple.length - 1 == i ? " или " : ", ") : "") + "<font color=\"LEVEL\">" + DifferentMethods.getItemName(ConfigValue.ESimple[i]) + "</font>");

        for (int i = 0; i < ConfigValue.ESimpleRb.length; i++)
            ritems += ((i != 0 ? (ConfigValue.ESimpleRb.length - 1 == i ? " или " : ", ") : "") + "<font color=\"LEVEL\">" + DifferentMethods.getItemName(ConfigValue.ESimpleRb[i]) + "</font>");

        html.replace("%mitemsmin%", String.valueOf(ConfigValue.ESimpleMinCount));
        html.replace("%mitemsmax%", String.valueOf(ConfigValue.ESimpleMaxCount));
        html.replace("%mmin%", String.valueOf(ConfigValue.ESimpleMinLevel));
        html.replace("%mmax%", String.valueOf(ConfigValue.ESimpleMaxLevel));

        html.replace("%mitems%", mitems);

        html.replace("%ritemsmin%", String.valueOf(ConfigValue.ESimpleRbMinCount));
        html.replace("%ritemsmax%", String.valueOf(ConfigValue.ESimpleRbMaxCount));
        html.replace("%rmin%", String.valueOf(ConfigValue.ESimpleRbMinLevel));
        html.replace("%rmax%", String.valueOf(ConfigValue.ESimpleRbMaxLevel));

        html.replace("%ritems%", ritems);
        player.sendPacket(html);

    }

    public void shop() {
        L2Player player = (L2Player) getSelf();
        L2NpcInstance npc = getNpc();

        if (npc.getNpcId() != ConfigValue.ESimpleManager)
            return;

        L2Multisell.getInstance().SeparateAndSend(ConfigValue.ESimpleManager, player, 0);
    }

    /**
     * Обработчик смерти мобов, управляющий ивентовым дропом
     */
    public static void OnDie(L2Character cha, L2Character killer) {
        if (!_active)
            return;

        if (cha == null || killer == null) //Для избежания нпе.
            return;

        if (killer.isMonster() || killer.isNpc()) //Проверяем убийцу, если убил монстр или нпц не пускаем дальше.
            return;

        if (cha.isPlayer() || cha.isPet() || cha.isSummon() || cha.isDoor()) //Исключаем другие бредовые смерти.
            return;

        if (cha.isMonster() && !cha.isRaid() && cha.getLevel() < ConfigValue.ESimpleMinLevel || cha.getLevel() > ConfigValue.ESimpleMaxLevel) //Проверка условии уровня монстров.
            return;

        if ((cha.isRaid() || cha.isBoss() || cha.isEpicRaid()) && (cha.getLevel() < ConfigValue.ESimpleRbMinLevel || cha.getLevel() > ConfigValue.ESimpleRbMaxLevel)) //Проверка условии уровня рб.
            return;

        int rate = 1;

        if (ConfigValue.ESimpleRateHp) //Исключение пока убрал, уже давно небыло ошибок.
        {
            rate = (int) ((L2NpcInstance) cha).getTemplate().rateHp;
            //			try
            //			{
            //				rate = ((int) ((L2NpcInstance) cha).getTemplate().rateHp);
            //			}
            //			catch(ClassCastException e)
            //			{
            //				// Тут исключение часто выбивает, позже нужно изучить, иначе засирает ГС
            //				_log.info("DEBUG SIMPLE: " + e);
            //				_log.info("DEBUG SIMPLE (NPC): " + cha.getName() + " (ID): " + cha.getNpcId());
            //			}
        }

        if (killer.getPlayer() != null && cha.isMonster() && !cha.isRaid() && Rnd.chance(ConfigValue.ESimpleChance * killer.getPlayer().getRateItems())) {
            int item = ConfigValue.ESimple[Rnd.get(ConfigValue.ESimple.length)];
            int count = Rnd.get(ConfigValue.ESimpleMinCount, ConfigValue.ESimpleMaxCount) * rate;
            ((L2NpcInstance) cha).dropItem(killer.getPlayer(), item, count);

            if (ConfigValue.ESimpleMessage)
                sendMessage(killer.getPlayer(), item, count);
        }

        if (killer.getPlayer() != null && (cha.isRaid() || cha.isBoss() || cha.isEpicRaid()) && Rnd.chance(ConfigValue.ESimpleRbChance * killer.getPlayer().getRateItems())) {
            int item = ConfigValue.ESimpleRb[Rnd.get(ConfigValue.ESimpleRb.length)];
            int count = Rnd.get(ConfigValue.ESimpleRbMinCount, ConfigValue.ESimpleRbMaxCount) * rate;
            ((L2NpcInstance) cha).dropItem(killer.getPlayer(), item, count);

            if (ConfigValue.ESimpleMessage)
                sendMessage(killer.getPlayer(), item, count);
        }
    }

    /**
     * Вывод сообщения о дропе предмета.
     */
    public static void sendMessage(L2Player player, int item, int count) {
        player.sendPacket(new ExShowScreenMessage(new CustomMessage("events.simple.dropitem", player).addString(Util.formatAdena(count)).addItemName(item).toString(), 3000, ScreenMessageAlign.TOP_CENTER, true));
        player.sendMessage(new CustomMessage("events.simple.dropitem", player).addString(Util.formatAdena(count)).addItemName(item));
    }

    public static void OnPlayerEnter(L2Player player) {
        if (_active)
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "events.simple.AnnounceEventStarted", null);
    }
}