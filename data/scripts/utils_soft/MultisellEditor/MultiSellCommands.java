package utils_soft.MultisellEditor;

import l2open.gameserver.model.L2Player;
import l2open.extensions.common.CommandsFunction;
import l2open.database.exceptions.ResourceProvideException;

/**
 * Created by a.kiperku
 * Date: 01.12.2023
 */
public enum MultiSellCommands {

    admin_multisell_editor(MultiSellEditorComponent::showMainPage),
    admin_multisell_editor_show_multisell(MultiSellEditorComponent::openMultiSell),
    admin_multisell_editor_redact(MultiSellEditorComponent::redactMultiSell),
    admin_multisell_editor_set_showall(MultiSellEditorComponent::setShowAll),
    admin_multisell_editor_set_notax(MultiSellEditorComponent::setNoTax),
    admin_multisell_editor_set_keepenchant(MultiSellEditorComponent::setKeepEnchant),
    admin_multisell_editor_set_nokey(MultiSellEditorComponent::setNoKey),
    admin_multisell_editor_remove_product(MultiSellEditorComponent::removeProduct),
    admin_multisell_editor_show_entry_page(MultiSellEditorComponent::showEntryPage),
    admin_multisell_editor_change_entry_index(MultiSellEditorComponent::changeEntryIndex),
    admin_multisell_editor_change_product_enchant(MultiSellEditorComponent::changeProductEnchant),
    admin_multisell_editor_change_product_count(MultiSellEditorComponent::changeProductCount),
    admin_multisell_editor_add_ingredient(MultiSellEditorComponent::addIngredient),
    admin_multisell_editor_change_ingredient_count(MultiSellEditorComponent::changeIngredientCount),
    admin_multisell_editor_remove_ingredient(MultiSellEditorComponent::removeIngredient),
    admin_multisell_editor_add_entry(MultiSellEditorComponent::addEntry),
    admin_multisell_editor_restore(MultiSellEditorComponent::restoreMultisell);

    private final CommandsFunction component;

    MultiSellCommands(CommandsFunction component) {
        this.component = component;
    }
    public void exec(L2Player player, String[] args) throws ResourceProvideException {
        component.execute(player, args);
    }

}
