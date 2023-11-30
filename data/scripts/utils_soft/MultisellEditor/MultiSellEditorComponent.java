package utils_soft.MultisellEditor;

import l2open.common.Html_Constructor.tags.Button;
import l2open.common.Html_Constructor.tags.Row;
import l2open.common.Html_Constructor.tags.Table;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.base.MultiSellEntry;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.templates.Item;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import utils_soft.common.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;
import static l2open.gameserver.model.L2Multisell.*;

public class MultiSellEditorComponent extends Component {

    private final MultiSellEditorService service;
    private L2Multisell l2Multisell = getInstance();

    public MultiSellEditorComponent(MultiSellEditorService service) {
        this.service = service;
    }


    public void showMainPage(L2Player player, String[] args) {
        int page = Integer.parseInt(args[1]);
        final ArrayList<MultiSellListContainer> containers = new ArrayList<>(l2Multisell.getLists().values());
        containers.sort(Comparator.comparingInt(MultiSellListContainer::getListId));
        final List<MultiSellListContainer> containerList = partitionList(containers, 20).get(page - 1);

        final Table main = new Table(2, 1).setParams(width(DEFAULT_TWINDOW_WIDTH));
        final Table pages = new Table(1, 3);
        pages.row(0).col(0).setParams(width(100), height(20)).insert(new Button("<<", action("bypass -h admin_multisell_editor " + (page == 1 ? page : page - 1)), 80, 20).build());
        pages.row(0).col(1).setParams(width(80)).insert(String.valueOf(page));
        pages.row(0).col(2).setParams(width(100)).insert(new Button(">>", action("bypass -h admin_multisell_editor " + (page > containerList.size() ? page : page + 1)), 80, 20).build());

        final Table table = new Table(containerList.size() + 1, 1);
        for (int i = 0; i < containerList.size(); i++) {
            final Table containerTable = new Table(2, 1);
            final Table multiSellTable = new Table(1, 3);
            final MultiSellListContainer container = containerList.get(i);
            multiSellTable.row(0).col(0).insert(new Button(String.valueOf(container.getListId()), action("bypass -h admin_multisell_editor_show_multisell " + container.getListId()), 80, 20).build(), true);
            multiSellTable.row(0).col(1).insert(new Button("redact", action("bypass -h admin_multisell_editor_redact " + container.getListId() + " 1"), 60, 20).build());
            multiSellTable.row(0).col(2).insert(new Button("restore", action(""), 60, 20).build());
            containerTable.row(0).col(0).insert(multiSellTable.build());
            containerTable.row(1).col(0).insert(separator(300));
            table.row(i).col(0).insert(containerTable.build());
        }
        table.row(containerList.size()).col(0).setParams(height(20)).insert("<br> ");

        main.row(0).col(0).insert(pages.build());
        main.row(1).col(0).insert(table.build());

        showTWindow(player, main.build(), "MultiSell Editor");
    }

    public void openMultiSell(L2Player player, String[] args) {
        final int id = Integer.parseInt(args[1]);
        L2Multisell.getInstance().SeparateAndSend(id, player, 0);
    }
    public void openMultiSell(L2Player player, int id){
        L2Multisell.getInstance().SeparateAndSend(id, player, 0);
    }

    public void showMultiSellRedactPage(L2Player player, MultiSellListContainer multisell, int page) {
        final Table configTable = new Table(2, 4);
        configTable.row(0).col(0).insert(new Button("showall", action("bypass -h admin_multisell_editor_set_showall " + multisell.getListId() + " " + page), 60, 20).build());
        configTable.row(0).col(1).insert(new Button("notax", action("bypass -h admin_multisell_editor_set_notax " + multisell.getListId() + " " + page), 60, 20).build());
        configTable.row(0).col(2).insert(new Button("enchant", action("bypass -h admin_multisell_editor_set_keepenchant " + multisell.getListId() + " " + page), 60, 20).build());
        configTable.row(0).col(3).insert(new Button("nokey", action("bypass -h admin_multisell_editor_set_nokey " + multisell.getListId() + " " + page), 60, 20).build());
        configTable.row(1).col(0).insert(String.valueOf(multisell.isShowAll()));
        configTable.row(1).col(1).insert(String.valueOf(multisell.isNoTax()));
        configTable.row(1).col(2).insert(String.valueOf(multisell.isKeepEnchant()));
        configTable.row(1).col(3).insert(String.valueOf(multisell.isNoKey()));

        final int size = multisell.getEntries().size();
        final List<List<MultiSellEntry>> partitionList = partitionList(multisell.getEntries(), 20);
        final List<MultiSellEntry> multiSellEntries = partitionList.get(page - 1);

        final Table itemsTable = new Table(multiSellEntries.size(), 1);
        for (int i = 0; i < multiSellEntries.size(); i++) {
            final MultiSellEntry entry = multiSellEntries.get(i);

            final Table moveTable = new Table(2, 1);
            moveTable.row(0).col(0).insert(new Button("^", action("admin_multisell_editor_change_entry_index " + multisell.getListId() + " " + entry.getEntryId() + " -1 " + page), 16, 16).build());
            moveTable.row(1).col(0).insert(new Button("V", action("admin_multisell_editor_change_entry_index " + multisell.getListId() + " " + entry.getEntryId() + " 1 " + page), 16, 16).build());

            L2Item product = ItemTemplates.getInstance().getTemplate(entry.getProduction().get(0).getItemId());
            final long productCount = entry.getProduction().get(0).getItemCount();
            final Table productInfo = new Table(2, 1);
            productInfo.row(0).col(0).insert(product.getName());
            productInfo.row(1).col(0).insert("count: " + productCount);

            final Table itemTable = new Table(1, 5);
            itemTable.row(0).col(0).setParams(height(32), width(32)).insert(moveTable.build());
            itemTable.row(0).col(1).setParams(height(32), width(32)).insert(product.getIcon());
            itemTable.row(0).col(2).setParams(height(32), width(20)).insert(productInfo.build());
            itemTable.row(0).col(3).setParams(height(32), width(32)).insert(new Button("@", action("admin_multisell_editor_show_entry_page " + multisell.getListId() + " " + entry.getEntryId())).build());
            itemTable.row(0).col(4).setParams(height(32), width(32)).insert(new Button("X", action("admin_multisell_editor_remove_product " + multisell.getListId() + " " + entry.getEntryId() + " " + page)).build());

            itemsTable.row(i).col(0).insert(itemTable.build());
        }


        final Table main = new Table(3, 1);
        main.row(0).col(0).insert(configTable.build());
        main.row(1).col(0).insert(pageTable(page, partitionList.size(), ""));
        main.row(2).col(0).setParams(height(20)).insert("<br> ");

        showTWindow(player, main.build(), "MultiSell Editor");
    }

    public void redactMultiSell(L2Player player, String[] args) {
        final int index = Integer.parseInt(args[1]);
        final int page = Integer.parseInt(args[2]);
        final MultiSellListContainer multisellList = l2Multisell.getList(index);


        // TODO: a.kiperku 30.11.2023

        showMultiSellRedactPage(player, multisellList, page);
    }

    public void setShowAll(L2Player player, String[] args) {
        final int index = Integer.parseInt(args[1]);
        final int page = Integer.parseInt(args[2]);
        final MultiSellListContainer list = l2Multisell.getList(index);
        if (list != null) {
            list.setShowAll(!list.isShowAll());
            showMultiSellRedactPage(player, list, page);
        }
    }

    public void setNoTax(L2Player player, String[] args) {
        final int index = Integer.parseInt(args[1]);
        final int page = Integer.parseInt(args[2]);
        final MultiSellListContainer list = l2Multisell.getList(index);
        if (list != null) {
            list.setNoTax(!list.isNoTax());
            showMultiSellRedactPage(player, list, page);
        }
    }

    public void setKeepEnchant(L2Player player, String[] args) {
        final int index = Integer.parseInt(args[1]);
        final int page = Integer.parseInt(args[2]);
        final MultiSellListContainer list = l2Multisell.getList(index);
        if (list != null) {
            list.setKeepEnchant(!list.isKeepEnchant());
            showMultiSellRedactPage(player, list, page);
        }
    }

    public void setNoKey(L2Player player, String[] args) {
        final int index = Integer.parseInt(args[1]);
        final int page = Integer.parseInt(args[2]);
        final MultiSellListContainer list = l2Multisell.getList(index);
        if (list != null) {
            list.setNoKey(!list.isNoKey());
            showMultiSellRedactPage(player, list, page);
        }
    }

    public void removeProduct(L2Player player, String[] args) {
        final int containerId = Integer.parseInt(args[1]);
        final int entryId = Integer.parseInt(args[2]);
        final int page = Integer.parseInt(args[3]);
        final MultiSellListContainer multisellList = l2Multisell.getList(containerId);
        final GArray<MultiSellEntry> entries = multisellList.getEntries();
        for (MultiSellEntry entry : entries) {
            if (entry.getEntryId() == entryId) {
                multisellList.getEntries().remove(entry);
            }
        }
        showMultiSellRedactPage(player, multisellList, page);
        openMultiSell(player, containerId);
    }

    public void showEntryPage(L2Player player, String[] args) {
        // TODO: a.kiperku 30.11.2023
    }

    public void changeEntryIndex(L2Player player, String[] args) {
        final int containerId = Integer.parseInt(args[1]);
        final int entryId = Integer.parseInt(args[2]);
        final int direction = Integer.parseInt(args[3]);
        final int page = Integer.parseInt(args[4]);
        final MultiSellListContainer multisellList = l2Multisell.getList(containerId);

        // TODO: a.kiperku 30.11.2023

        showMultiSellRedactPage(player, multisellList, page);
        openMultiSell(player, containerId);
    }
}
