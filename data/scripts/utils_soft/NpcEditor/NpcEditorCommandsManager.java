package utils_soft.NpcEditor;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Player;

/**
 * Created by a.kiperku
 * Date: 28.11.2023
 */

public class NpcEditorCommandsManager implements IAdminCommandHandler, ScriptFile {


    @Override
    public boolean useAdminCommand(Enum comm, String[] args, String fullString, L2Player player) {
        NpcEditorCommands command = (NpcEditorCommands) comm;
        if (player.isGM()){
            command.exec(player, args);
            return true;
        }
        return false;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return NpcEditorCommands.values();
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
