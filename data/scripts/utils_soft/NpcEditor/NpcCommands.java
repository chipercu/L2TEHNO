package utils_soft.NpcEditor;

import l2open.gameserver.model.L2Player;
import utils_soft.MultisellEditor.MultiSellEditorComponent;

import utils_soft.common.CommandsFunction;

/**
 * Created by a.kiperku
 * Date: 01.12.2023
 */
public enum NpcCommands {


    admin_npc_editor(NpcEditorComponent::showMainPage),
    admin_npc_editor_main_stats(NpcEditorComponent::showMainStats),
    admin_npc_editor_base_stats(NpcEditorComponent::showBaseStats),
    admin_npc_editor_skills(NpcEditorComponent::showSkills),
    admin_npc_editor_drop(NpcEditorComponent::showDrop),
    admin_npc_editor_visual(NpcEditorComponent::showVisual),
    admin_npc_editor_elements(NpcEditorComponent::showElements),
    admin_npc_editor_location(NpcEditorComponent::showLocation),
    admin_npc_editor_other(NpcEditorComponent::showOther),
    admin_npc_editor_save_main_stats(NpcEditorComponent::saveMainStats),
    admin_npc_editor_save_base_stats(NpcEditorComponent::saveBaseStats),
    admin_npc_editor_save_skills(NpcEditorComponent::saveSkills),
    admin_npc_editor_save_drop(NpcEditorComponent::saveDrop),
    admin_npc_editor_save_visual(NpcEditorComponent::saveVisual),
    admin_npc_editor_save_base_elements(NpcEditorComponent::saveElements),
    admin_npc_editor_save_location(NpcEditorComponent::saveLocation),
    admin_npc_editor_save_other(NpcEditorComponent::saveOther);

    private final CommandsFunction component;

    NpcCommands(CommandsFunction component) {
        this.component = component;
    }
    public void exec(L2Player player, String[] args) {
        component.execute(player, args);
    }

}
