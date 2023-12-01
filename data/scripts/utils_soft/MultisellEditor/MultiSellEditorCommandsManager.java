package utils_soft.MultisellEditor;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Player;

/**
 * Created by a.kiperku
 * Date: 28.11.2023
 */

public class MultiSellEditorCommandsManager implements IAdminCommandHandler, ScriptFile {


    @Override
    public boolean useAdminCommand(Enum comm, String[] args, String fullString, L2Player player) {
        MultiSellCommands command = (MultiSellCommands) comm;
        if (player.isGM()){
            command.exec(player, args);
            return true;
        }
        return false;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return MultiSellCommands.values();
    }

    @Override
    public void onLoad() {
        AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }
}
