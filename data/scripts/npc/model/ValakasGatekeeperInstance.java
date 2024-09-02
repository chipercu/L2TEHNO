package npc.model;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;
import bosses.ValakasManager;

/**
 * @author pchayka
 */
// 31385|31540
public final class ValakasGatekeeperInstance extends L2NpcInstance {
    private static final int FLOATING_STONE = 7267;
    private static final Location TELEPORT_POSITION1 = new Location(183829, -115406, -3326);

    public ValakasGatekeeperInstance(int objectId, L2NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(L2Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (player.isInOlympiadMode()) {
            player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
            return;
        }
        if (command.equalsIgnoreCase("request_passage")) {
            if (!ValakasManager.isEnableEnterToLair()) {
                player.sendMessage("Valakas is now reborning and there's no way to enter the hall now.");
                return;
            }
            if (player.getInventory().getCountOf(FLOATING_STONE) < 1) {
                player.sendMessage("In order to enter the Hall of Flames you should carry at least one Flotaing Stone");
                return;
            }
            player.teleToLocation(TELEPORT_POSITION1);
            return;
        } else if (command.equalsIgnoreCase("request_valakas")) {
            ValakasManager.enterTheLair(player);
            return;
        } else
            super.onBypassFeedback(player, command);
    }
}