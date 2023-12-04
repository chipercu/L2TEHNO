package utils_soft.NpcEditor;

import l2open.gameserver.model.L2Player;
import utils_soft.MultisellEditor.MultiSellEditorComponent;
import utils_soft.common.CommandsFunction;

/**
 * Created by a.kiperku
 * Date: 01.12.2023
 */
public enum NpcCommands {

    admin_npc_editor(NpcEditorComponent::showMainPage);

    private final CommandsFunction component;

    NpcCommands(CommandsFunction component) {
        this.component = component;
    }
    public void exec(L2Player player, String[] args) {
        component.execute(player, args);
    }

}
