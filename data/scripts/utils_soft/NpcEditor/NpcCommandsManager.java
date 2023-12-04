package utils_soft.NpcEditor;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Player;
import utils_soft.MultisellEditor.MultiSellCommands;

/**
 * Created by a.kiperku
 * Date: 28.11.2023
 */

public class NpcCommandsManager implements IAdminCommandHandler, ScriptFile {


    @Override
    public boolean useAdminCommand(Enum comm, String[] args, String fullString, L2Player player) {
        NpcCommands command = (NpcCommands) comm;
        if (player.isGM()){
            command.exec(player, args);
            return true;
        }
        return false;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return NpcCommands.values();
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
