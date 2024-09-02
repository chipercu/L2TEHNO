package events.BattlePass;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.util.Files;
import l2open.util.Rnd;
import l2open.common.Html_Constructor.tags.*;

import java.util.logging.Logger;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;

public class BattlePass extends Functions implements ScriptFile {

    private static final Logger _log = Logger.getLogger(BattlePass.class.getName());
    private static boolean _active = false;

    public static final String BATTLE_PASS_POINTS = "BATTLE_PASS_POINTS";
    public static final String BATTLE_PASS_MOBS_KILLS = "BATTLE_PASS_MOBS_KILLS";

    /**
     * Читает статус ивента из базы.
     * @return
     */
    private static boolean isActive() {
        return IsActive("BattlePass");
    }

    /**
     * Запускает ивент
     */
    public void startEvent() {
        L2Player player = (L2Player) getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }

        if (SetActive("BattlePass", true)) {
            _log.info("Event 'BattlePass' started.");
        } else {
            player.sendMessage("Event 'BattlePass' already started.");
        }
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
        if (SetActive("BattlePass", false)) {
            _log.info("Event 'BattlePass' stopped.");
        } else {
            player.sendMessage("Event 'BattlePass' not started.");
        }
        _active = false;
        show(Files.read("data/html/admin/events/events.htm", player), player);
    }

    /**
     * Обработчик смерти мобов, управляющий ивентовым дропом
     */
    public static void OnDie(L2Character cha, L2Character killer) {
        if (!_active) {
            return;
        }
        if (cha == null || killer == null) {
            return;
        }
        //Проверяем убийцу, если убил монстр или нпц не пускаем дальше.
        if (killer.isMonster() || killer.isNpc()) {
            return;
        }
        //Исключаем другие бредовые смерти.
        if (cha.isPlayer() || cha.isPet() || cha.isSummon() || cha.isDoor()) {
            return;
        }
        //Проверка условии уровня монстров.
        if (cha.isMonster() && !cha.isRaid() && cha.getLevel() < ConfigValue.ESimpleMinLevel || cha.getLevel() > ConfigValue.ESimpleMaxLevel) {
            return;
        }
        //Проверка условии уровня рб.
        if ((cha.isRaid() || cha.isBoss() || cha.isEpicRaid()) && (cha.getLevel() < ConfigValue.ESimpleRbMinLevel || cha.getLevel() > ConfigValue.ESimpleRbMaxLevel)) {
            return;
        }

        int rate = 1;

        if (killer.getPlayer() != null && cha.isMonster() && !cha.isRaid()) {
            int item = ConfigValue.ESimple[Rnd.get(ConfigValue.ESimple.length)];
            int count = Rnd.get(ConfigValue.ESimpleMinCount, ConfigValue.ESimpleMaxCount) * rate;
            ((L2NpcInstance) cha).dropItem(killer.getPlayer(), item, count);
            int current_mobs_kills = killer.getPlayer().getVarInt(BATTLE_PASS_MOBS_KILLS);
            int current_bp_points = killer.getPlayer().getVarInt(BATTLE_PASS_POINTS);
            if (current_mobs_kills >= 50) {
                killer.getPlayer().setVar(BATTLE_PASS_MOBS_KILLS, 0, -1);
                killer.getPlayer().setVar(BATTLE_PASS_POINTS, current_bp_points + 5, -1);
            } else {
                killer.getPlayer().setVar(BATTLE_PASS_MOBS_KILLS, current_mobs_kills + 1, -1);
            }
        }

        if (killer.getPlayer() != null && (cha.isRaid() || cha.isBoss() || cha.isEpicRaid())) {
            int item = ConfigValue.ESimpleRb[Rnd.get(ConfigValue.ESimpleRb.length)];
            int count = Rnd.get(ConfigValue.ESimpleRbMinCount, ConfigValue.ESimpleRbMaxCount) * rate;
            ((L2NpcInstance) cha).dropItem(killer.getPlayer(), item, count);

            int current_bp_points = killer.getPlayer().getVarInt(BATTLE_PASS_POINTS);
            killer.getPlayer().setVar(BATTLE_PASS_POINTS, current_bp_points + 5, -1);
        }
    }


    public void showBoard() {
        if (_active) {
            L2Player player = (L2Player) getSelf();
            BattlePassBoard.showBattlePass(player);
        }
    }
    /**
     * Обработчик входа в игру
     */
    public static void OnPlayerEnter(L2Player player) {
        if (_active) {
            BattlePassBoard.showBattlePass(player);
        }
    }

    @Override
    public void onLoad() {
        if (isActive()) {
            _active = true;
            _log.info("Loaded Event: BattlePass [state: activated]");
        } else {
            _log.info("Loaded Event: BattlePass [state: deactivated]");
        }
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }
}
