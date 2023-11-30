package utils_soft.MultisellEditor;

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
        admin_multisell_editor_redact,
        admin_multisell_editor_set_showall,
        admin_multisell_editor_set_notax,
        admin_multisell_editor_set_keepenchant,
        admin_multisell_editor_set_nokey,
        admin_multisell_editor_remove_product,
        admin_multisell_editor_show_entry_page,
        admin_multisell_editor_change_entry_index,
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
                    component.openMultiSell(player, args);
                    break;
                }
                case admin_multisell_editor_redact:{
                    component.redactMultiSell(player, args);
                    break;
                }
                case admin_multisell_editor_set_showall:{
                    component.setShowAll(player, args);
                    break;
                }
                case admin_multisell_editor_set_notax:{
                    component.setNoTax(player, args);
                    break;
                }
                case admin_multisell_editor_set_keepenchant:{
                    component.setKeepEnchant(player, args);
                    break;
                }
                case admin_multisell_editor_set_nokey:{
                    component.setNoKey(player, args);
                    break;
                }
                case admin_multisell_editor_remove_product:{
                    component.removeProduct(player, args);
                    break;
                }
                case admin_multisell_editor_show_entry_page:{
                    component.showEntryPage(player, args);
                    break;
                }
                case admin_multisell_editor_change_entry_index:{
                    component.changeEntryIndex(player, args);
                    break;
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
