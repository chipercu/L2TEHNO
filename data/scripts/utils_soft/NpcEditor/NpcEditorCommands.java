package utils_soft.NpcEditor;

import l2open.gameserver.model.L2Player;
import l2open.extensions.common.CommandsFunction;
import l2open.database.exceptions.ResourceProvideException;

/**
 * Created by a.kiperku
 * Date: 01.12.2023
 */
public enum NpcEditorCommands {
    admin_npc_editor_test_move_pet(NpcEditorComponent::testMovePet),
    admin_npc_editor(NpcEditorComponent::showMainPage),
    admin_npc_editor_main_stats(NpcEditorComponent::showMainStats),
    admin_npc_editor_skills(NpcEditorComponent::showSkills),
    admin_npc_editor_drop(NpcEditorComponent::showDrop),
    admin_npc_editor_visual(NpcEditorComponent::showVisual),
    admin_npc_editor_elements(NpcEditorComponent::showElements),
    admin_npc_editor_location(NpcEditorComponent::showLocation),
    admin_npc_editor_other(NpcEditorComponent::showOther),
    admin_npc_editor_save_main_stats(NpcEditorComponent::saveMainStats),
    admin_npc_editor_add_skills(NpcEditorComponent::addSkills),
    admin_npc_editor_remove_skill(NpcEditorComponent::removeSkill),
    admin_npc_editor_add_drop(NpcEditorComponent::addDrop),
    admin_npc_editor_remove_drop(NpcEditorComponent::removeDrop),
    admin_npc_editor_save_visual(NpcEditorComponent::saveVisual),
    admin_npc_editor_save_base_elements(NpcEditorComponent::saveElements),
    admin_npc_editor_save_location(NpcEditorComponent::saveLocation),
    admin_npc_editor_save_other(NpcEditorComponent::saveOther),
    admin_npc_editor_create(NpcEditorComponent::showNpcCreatePage);

    private final CommandsFunction component;

    NpcEditorCommands(CommandsFunction component) {
        this.component = component;
    }
    public void exec(L2Player player, String[] args) throws ResourceProvideException {
        component.execute(player, args);
    }

}
