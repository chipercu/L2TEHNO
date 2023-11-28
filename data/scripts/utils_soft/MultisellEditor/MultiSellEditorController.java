package utils_soft.MultisellEditor;

import commands.admin.AdminAdmin;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Player;

/**
 * Created by a.kiperku
 * Date: 28.11.2023
 */

public class MultiSellEditorController implements IAdminCommandHandler, ScriptFile {

    private MultiSellEditorComponent component;


    private static enum Commands {
        admin_multisell_editor,
        admin_multisell_editor_show_multisell,
    }

    @Override
    public boolean useAdminCommand(Enum comm, String[] args, String fullString, L2Player player) {

        Commands command = (Commands) comm;
        if (player.isGM()){
            switch (command){
                case admin_multisell_editor : {
                    component.showMainPage(player, args);
                    break;
                }
                case admin_multisell_editor_show_multisell:{
                    component.showMultiSell(player, args);
                }


            }
            return true;
        }
        return false;
    }


    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public void onLoad() {
        AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
        this.component = new MultiSellEditorComponent(new MultiSellEditorService());
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }


}
