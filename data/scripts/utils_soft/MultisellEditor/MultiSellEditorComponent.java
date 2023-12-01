package utils_soft.MultisellEditor;

import l2open.common.Html_Constructor.tags.*;
import l2open.common.Html_Constructor.tags.parameters.Color;
import l2open.common.Html_Constructor.tags.parameters.EditType;
import l2open.common.Html_Constructor.tags.parameters.Position;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.MultiSellEntry;
import l2open.gameserver.model.base.MultiSellIngredient;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import utils_soft.common.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;
import static l2open.common.Html_Constructor.tags.parameters.Position.*;
import static l2open.gameserver.model.L2Multisell.MultiSellListContainer;
import static utils_soft.MultisellEditor.MultiSellCommands.*;

public class MultiSellEditorComponent extends Component{


    private static final String backMainBypass = "admin_multisell_editor null 0 1";
    private static final String window_titel = "MultiSell Editor";

    public MultiSellEditorComponent() {
    }

    public static void showMainPage(L2Player player, String[] args) {

        if (args.length < 4){
            return;
        }

        String filter = args[1];
        String filterValue = args[2];
        int page = Integer.parseInt(args[3]);



        List<MultiSellListContainer> containers = new ArrayList<>();
        if ("id".equals(filter)){
            if (!"0".equals(filterValue)){
                containers = L2Multisell.getInstance().getLists().values().stream().filter(m -> String.valueOf(m.getListId()).contains(filterValue)).collect(Collectors.toList());
            }
        } else if ("itemid".equals(filter)) {
            if (!"0".equals(filterValue)){
                try {
                    final int itemId = Integer.parseInt(filterValue);
                    containers = L2Multisell.getInstance().getLists().values().stream()
                            .filter(m -> m.getEntries().stream().anyMatch(e -> e.getProduction().get(0).getItemId() == itemId))
                            .collect(Collectors.toList());
                }catch (Exception e){}
            }
        } else if ("itemname".equals(filter)) {
            if (!"0".equals(filterValue)){
                BiPredicate<Integer, String> biPredicate = (integer, s) -> {
                    if (integer <= 0){
                        return false;
                    }
                    return ItemTemplates.getInstance().getTemplate(integer).getName().trim().contains(s);
                };
                containers = L2Multisell.getInstance().getLists().values().stream()
                        .filter(m -> m.getEntries().stream().anyMatch(e -> biPredicate.test(e.getProduction().get(0).getItemId(),filterValue)))
                        .collect(Collectors.toList());
            }
        }else {
            containers = new ArrayList<>(L2Multisell.getInstance().getLists().values());
        }

        containers.sort(Comparator.comparingInt(MultiSellListContainer::getListId));

        List<MultiSellListContainer> containerList = new ArrayList<>();

        if (!containers.isEmpty()){
            containerList = partitionList(containers, 20).get(page - 1);
        }

//        final List<MultiSellListContainer> containerList = partitionList(containers, 20).get(page - 1);

        final Table main = new Table(3, 1).setParams(width(DEFAULT_TWINDOW_WIDTH));

        final Table filterTable = new Table(1, 4);
        filterTable.row(0).col(0).setParams(width(80)).insert(new Edit("find", 80, 12, EditType.text, 12).build());
        filterTable.row(0).col(1).setParams(width(50)).insert(new Button("Id", actionCom(admin_multisell_editor,"id $find 1"), 50, 20).build());
        filterTable.row(0).col(2).setParams(width(60)).insert(new Button("ItemId", actionCom(admin_multisell_editor,"itemid $find 1"), 60, 20).build());
        filterTable.row(0).col(3).setParams(width(70)).insert(new Button("ItemName", actionCom(admin_multisell_editor,"itemname $find 1"), 70, 20).build());
        main.row(0).col(0).insert(filterTable.build());

        final Table pages = new Table(1, 3);
        pages.row(0).col(0).setParams(width(100), height(20)).insert(new Button("<<", actionCom(admin_multisell_editor, "null 0 " + (page == 1 ? page : page - 1)), 80, 20).build());
        pages.row(0).col(1).setParams(width(80)).insert(String.valueOf(page));
        pages.row(0).col(2).setParams(width(100)).insert(new Button(">>", actionCom(admin_multisell_editor,"null 0 " + (page > containerList.size() ? page : page + 1)), 80, 20).build());

        final Table table = new Table(containerList.size() + 1, 1);
        for (int i = 0; i < containerList.size(); i++) {
            final Table containerTable = new Table(2, 1);
            final Table multiSellTable = new Table(1, 3);
            final MultiSellListContainer container = containerList.get(i);
            multiSellTable.row(0).col(0).insert(new Button(String.valueOf(container.getListId()), actionCom(admin_multisell_editor_show_multisell, "" + container.getListId()), 80, 20).build(), true);
            multiSellTable.row(0).col(1).insert(new Button("redact", actionCom(admin_multisell_editor_redact, container.getListId() + " 1"), 60, 20).build());
            multiSellTable.row(0).col(2).insert(new Button("restore", action(""), 60, 20).build());
            containerTable.row(0).col(0).insert(multiSellTable.build());
            containerTable.row(1).col(0).insert(separator(300));
            table.row(i).col(0).insert(containerTable.build());
        }
        table.row(containerList.size()).col(0).setParams(height(20)).insert("<br> ");

        main.row(1).col(0).insert(pages.build());
        main.row(2).col(0).insert(table.build());

        showTWindow(player, main.build(), window_titel, backMainBypass);
    }

    public static void showMultiSellRedactPage(L2Player player, MultiSellListContainer multisell, int page) {
        final Table main = new Table(6, 1);
        final Table configTable = new Table(2, 4);
        configTable.row(0).col(0).insert(new Button("showall", actionCom(admin_multisell_editor_set_showall, multisell.getListId() + " " + page), 60, 20).build());
        configTable.row(0).col(1).insert(new Button("notax", actionCom(admin_multisell_editor_set_notax, multisell.getListId() + " " + page), 60, 20).build());
        configTable.row(0).col(2).insert(new Button("enchant", actionCom(admin_multisell_editor_set_keepenchant, multisell.getListId() + " " + page), 60, 20).build());
        configTable.row(0).col(3).insert(new Button("nokey", actionCom(admin_multisell_editor_set_nokey, multisell.getListId() + " " + page), 60, 20).build());
        configTable.row(1).col(0).setParams(align(CENTER), valign(LEFT)).insert(new Font(multisell.isShowAll()? Color.GOLD: Color.RED, String.valueOf(multisell.isShowAll())).build());
        configTable.row(1).col(1).setParams(align(CENTER), valign(LEFT)).insert(new Font(multisell.isNoTax()? Color.GOLD: Color.RED, String.valueOf(multisell.isNoTax())).build());
        configTable.row(1).col(2).setParams(align(CENTER), valign(LEFT)).insert(new Font(multisell.isKeepEnchant()? Color.GOLD: Color.RED, String.valueOf(multisell.isKeepEnchant())).build());
        configTable.row(1).col(3).setParams(align(CENTER), valign(LEFT)).insert(new Font(multisell.isNoKey()? Color.GOLD: Color.RED, String.valueOf(multisell.isNoKey())).build());

        main.row(0).col(0).insert(configTable.build());




        final int size = multisell.getEntries().size();
        final List<List<MultiSellEntry>> partitionList = partitionList(multisell.getEntries(), 10);





        if (!partitionList.isEmpty()){
            final List<MultiSellEntry> multiSellEntries = partitionList.get(page - 1);

            final Table pages = new Table(1, 3);
            pages.row(0).col(0).setParams(width(100), height(20)).insert(new Button("<<", actionCom(admin_multisell_editor_redact, multisell.getListId() + " " + (page == 1? page: page -1)), 80, 20).build());
            pages.row(0).col(1).setParams(width(80)).insert(String.valueOf(page));
            pages.row(0).col(2).setParams(width(100)).insert(new Button(">>", actionCom(admin_multisell_editor_redact,multisell.getListId() + " " + (page >= partitionList.size() ? page: page + 1)), 80, 20).build());


            final Table itemsTable = new Table(multiSellEntries.size() * 2, 1).setParams(cellspacing(2), cellpadding(0));
            int index = 0;
            for (int i = 0; i < multiSellEntries.size(); i++) {
                final MultiSellEntry entry = multiSellEntries.get(i);

                final Table moveTable = new Table(2, 1).setParams(cellspacing(2), cellspacing(0));
                moveTable.row(0).col(0).setParams(height(16)).insert(new Button("-", actionCom(admin_multisell_editor_change_entry_index, multisell.getListId() + " " + entry.getEntryId() + " -1 " + page), 16, 16).build());
                moveTable.row(1).col(0).setParams(height(16)).insert(new Button("+", actionCom(admin_multisell_editor_change_entry_index, multisell.getListId() + " " + entry.getEntryId() + " 1 " + page), 16, 16).build());

                L2Item product = ItemTemplates.getInstance().getTemplate(entry.getProduction().get(0).getItemId());
                final long productCount = entry.getProduction().get(0).getItemCount();
                final Table productInfo = new Table(2, 1);
                productInfo.row(0).col(0).setParams(height(16)).insert(product.getName().length() > 15 ? product.getName().substring(0, 15) : product.getName());
                productInfo.row(1).col(0).setParams(height(16)).insert("count: " + productCount);
                final String productName = " " + formatItemName(product.getName());
                final Table itemTable = new Table(1, 5).setParams(cellpadding(0));
//            itemTable.row(0).col(0).setParams(height(32), width(32)).insert(moveTable.build());
                itemTable.row(0).col(0).setParams(height(40), width(1)).insert("");
                itemTable.row(0).col(1).setParams(width(32)).insert(new Img(product.getIcon()).build());
                itemTable.row(0).col(2).setParams(width(170)).setParams(align(LEFT), valign(CENTER)).insert(productName.length() > 25 ? productName.substring(0, 25) : productName);
                itemTable.row(0).col(3).setParams(width(32)).insert(new Button("@", actionCom(admin_multisell_editor_show_entry_page, multisell.getListId() + " " + entry.getEntryId())).build());
                itemTable.row(0).col(4).setParams(width(32)).insert(new Button("X", actionCom(admin_multisell_editor_remove_product, multisell.getListId() + " " + entry.getEntryId() + " " + page)).build());
                itemsTable.row(index).col(0).insert(itemTable.build());
                itemsTable.row(index + 1).col(0).setParams(height(5)).insert(separator(270));
                index = index + 2;
            }

            main.row(1).col(0).insert(pages.build());
            main.row(2).col(0).insert(itemsTable.build());
        }

        final Table addEntryTable = new Table(1, 5);
        addEntryTable.row(0).col(0).setParams(width(20), valign(CENTER), align(LEFT)).insert("id");
        addEntryTable.row(0).col(1).insert(new Edit("itemId", 50, 12, EditType.num, 12).build());
        addEntryTable.row(0).col(2).setParams(width(35), valign(CENTER), align(LEFT)).insert("count");
        addEntryTable.row(0).col(3).insert(new Edit("itemCount", 85, 12, EditType.num, 12).build());
        addEntryTable.row(0).col(4).insert(new Button("Добавить", actionCom(admin_multisell_editor_add_entry, multisell.getListId() + " $itemId $itemCount " + page), 80, 20).build(),true);


        main.row(3).col(0).setParams(height(10));
        main.row(4).col(0).insert(addEntryTable.build(),true);
        main.row(5).col(0).setParams(height(20)).insert("<br> ");

        showTWindow(player, main.build(), window_titel, backMainBypass);
    }

    public static void showEntryPage(L2Player player, String[] args) {
        int multisellId = Integer.parseInt(args[1]);
        int entryId = Integer.parseInt(args[2]);
        final MultiSellListContainer multisellList = L2Multisell.getInstance().getList(multisellId);
        final MultiSellEntry entry = multisellList.getEntries().stream().filter(e -> e.getEntryId() == entryId).findFirst().orElse(null);
        if (entry == null){
            return;
        }
        final MultiSellIngredient multiSellProduct = entry.getProduction().get(0);
        L2Item product = ItemTemplates.getInstance().getTemplate(multiSellProduct.getItemId());

        final Table main = new Table(6, 1);
        final Table productTable = new Table(2, 1).setParams(cellpadding(0));
        final Table productInfoTable = new Table(1, 2).setParams(cellpadding(0));
        productInfoTable.row(0).col(0).setParams(width(40)).insert(new Img(product.getIcon()).build());
        productInfoTable.row(0).col(1).setParams(width(320), align(LEFT), valign(CENTER)).insert("id:" + new Font(Color.RED, product.getItemId()).build() + " | " + new Font(Color.BLUE, formatItemName(product.getName())).build());

        final Table changeProductTable = new Table(1, 2).setParams(cellpadding(0));
        final Table enchantTable = new Table(1, 3).setParams(cellpadding(0));
        enchantTable.row(0).col(0).setParams(width(40)).insert(new Button("<", actionCom(admin_multisell_editor_change_product_enchant, multisellId + " " + entryId + " -1")).build());
        enchantTable.row(0).col(1).setParams(width(40), align(CENTER), valign(CENTER)).insert(new Font(Color.GREN,"+" + multiSellProduct.getItemEnchant()).build());
        enchantTable.row(0).col(2).setParams(width(40)).insert(new Button(">", actionCom(admin_multisell_editor_change_product_enchant, multisellId + " " + entryId + " 1")).build());

        final Table countTable = new Table(1, 4).setParams(cellpadding(0));
        countTable.row(0).col(0).setParams(width(40));
        countTable.row(0).col(1).setParams(width(150), valign(CENTER), align(LEFT)).insert("count: " + new Font(Color.GREN, multiSellProduct.getItemCount()).build() );
        countTable.row(0).col(2).setParams(width(100), valign(TOP), align(CENTER)).insert(new Edit("count", 100, 12, EditType.num, 12).build());
        countTable.row(0).col(3).setParams(width(70)).insert(new Button("ok", actionCom(admin_multisell_editor_change_product_count, multisellId + " " + entryId + " $count")).build());
        changeProductTable.row(0).col(0).insert(product.canBeEnchanted() ? enchantTable.build() : "");
        changeProductTable.row(0).col(1).insert(product.isStackable() ? countTable.build() : "");
        productTable.row(0).col(0).setParams(width(360)).insert(productInfoTable.build());
        productTable.row(1).col(0).setParams(width(360)).insert(changeProductTable.build());

        final GArray<MultiSellIngredient> multiSellIngredients = entry.getIngredients();
        final Table ingredientsTable = new Table(multiSellIngredients.size(), 1);

        int index = 0;
        for (MultiSellIngredient multiSellIngredient: multiSellIngredients){
            final Table table = new Table(1, 5);
            L2Item ingredient = ItemTemplates.getInstance().getTemplate(multiSellIngredient.getItemId());
            table.row(0).col(0).setParams(width(40)).insert(new Img(ingredient.getIcon()).build());
            table.row(0).col(1).setParams(width(80), valign(CENTER), align(LEFT)).insert(new Font(Color.GOLD, multiSellIngredient.getItemCount()).build());
            table.row(0).col(2).setParams(width(50), valign(CENTER), align(CENTER)).insert(multiSellIngredient.isStackable() ? new Edit("count" + index, 50, 20, EditType.num, 12).build() : "");
            table.row(0).col(3).setParams(width(40)).insert(multiSellIngredient.isStackable() ? new Button("ok", actionCom(admin_multisell_editor_change_ingredient_count, multisellId + " " + entryId + " " + index + " $count" + index)).build() : "");
            table.row(0).col(4).setParams(width(40)).insert(new Button("X", actionCom(admin_multisell_editor_remove_ingredient, multisellId + " " + entryId + " " + index)).build());
            ingredientsTable.row(index).col(0).insert(table.build());
            index++;
        }

        final Table addIngredientTable = new Table(1, 5);
        addIngredientTable.row(0).col(0).setParams(width(20), valign(CENTER), align(LEFT)).insert("id");
        addIngredientTable.row(0).col(1).insert(new Edit("itemId", 50, 12, EditType.num, 12).build());
        addIngredientTable.row(0).col(2).setParams(width(35), valign(CENTER), align(LEFT)).insert("count");
        addIngredientTable.row(0).col(3).insert(new Edit("itemCount", 85, 12, EditType.num, 12).build());
        addIngredientTable.row(0).col(4).insert(new Button("Добавить", actionCom(admin_multisell_editor_add_ingredient, multisellId + " " + entryId + " $itemId $itemCount"), 80, 20).build(),true);

        main.row(0).col(0).insert(productTable.build());
        main.row(1).col(0).setParams(height(40),valign(BOTTOM), align(CENTER)).insert("<br>" + new Font(Color.BROWN, "Ingredients").build());
        main.row(2).col(0).insert(ingredientsTable.build());
        main.row(3).col(0).setParams(height(40)).insert("");
        main.row(4).col(0).insert(addIngredientTable.build());
        main.row(5).col(0).setParams(height(20)).insert("<br> ");

        showTWindow(player, main.build(), window_titel, "admin_multisell_editor_redact " + multisellId + " 1");
        openMultiSell(player, multisellList.getListId());
    }

    public static void redactMultiSell(L2Player player, String[] args) {
        final int multisellId = Integer.parseInt(args[1]);
        final int page = Integer.parseInt(args[2]);
        final MultiSellListContainer multisellList = L2Multisell.getInstance().getList(multisellId);

        // TODO: a.kiperku 30.11.2023  Сделать перед редактированием резервную копию текущего мултисела

        showMultiSellRedactPage(player, multisellList, page);
        openMultiSell(player, multisellList.getListId());
    }

    public static void openMultiSell(L2Player player, String[] args) {
        final int multisellId = Integer.parseInt(args[1]);
        L2Multisell.getInstance().SeparateAndSend(multisellId, player, 0);
    }

    public static void openMultiSell(L2Player player, int multisellId){
        L2Multisell.getInstance().SeparateAndSend(multisellId, player, 0);
    }

    public static void setShowAll(L2Player player, String[] args) {
        final int multisellId = Integer.parseInt(args[1]);
        final int page = Integer.parseInt(args[2]);

        final MultiSellListContainer list = L2Multisell.getInstance().getList(multisellId);
        if (list != null) {
            list.setShowAll(!list.isShowAll());
            showMultiSellRedactPage(player, list, page);
        }
        if (list != null){
            openMultiSell(player, list.getListId());
        }
    }

    public static void setNoTax(L2Player player, String[] args) {
        final int multisellId = Integer.parseInt(args[1]);
        final int page = Integer.parseInt(args[2]);

        final MultiSellListContainer list = L2Multisell.getInstance().getList(multisellId);
        if (list != null) {
            list.setNoTax(!list.isNoTax());
            showMultiSellRedactPage(player, list, page);
        }
        if (list != null){
            openMultiSell(player, list.getListId());
        }
    }

    public static void setKeepEnchant(L2Player player, String[] args) {
        final int multisellId = Integer.parseInt(args[1]);
        final int page = Integer.parseInt(args[2]);

        final MultiSellListContainer list = L2Multisell.getInstance().getList(multisellId);
        if (list != null) {
            list.setKeepEnchant(!list.isKeepEnchant());
            showMultiSellRedactPage(player, list, page);
        }
        if (list != null){
            openMultiSell(player, list.getListId());
        }
    }

    public static void setNoKey(L2Player player, String[] args) {
        final int multisellId = Integer.parseInt(args[1]);
        final int page = Integer.parseInt(args[2]);

        final MultiSellListContainer list = L2Multisell.getInstance().getList(multisellId);
        if (list != null) {
            list.setNoKey(!list.isNoKey());
            showMultiSellRedactPage(player, list, page);
        }
        if (list != null){
            openMultiSell(player, list.getListId());
        }
    }

    public static void removeProduct(L2Player player, String[] args) {
        final int containerId = Integer.parseInt(args[1]);
        final int entryId = Integer.parseInt(args[2]);

        final MultiSellListContainer multisellList = L2Multisell.getInstance().getList(containerId);
        final ArrayList<MultiSellEntry> entries = new ArrayList<>(multisellList.getEntries());
        for (MultiSellEntry entry: entries){
            if (entry.getEntryId() == entryId){
                multisellList.getEntries().remove(entry);
            }
        }

        final int page = partitionList(multisellList.getEntries(), 10).size();
        showMultiSellRedactPage(player, multisellList, page);
        openMultiSell(player, containerId);
    }

    public static void changeEntryIndex(L2Player player, String[] args) {
        final int multisellId = Integer.parseInt(args[1]);
        final int entryId = Integer.parseInt(args[2]);
        final int direction = Integer.parseInt(args[3]);
        final int page = Integer.parseInt(args[4]);
        final MultiSellListContainer multisellList = L2Multisell.getInstance().getList(multisellId);

        // TODO: a.kiperku 30.11.2023 Реализовать изменение позиции продукта в мултиселе

        showMultiSellRedactPage(player, multisellList, page);
        openMultiSell(player, multisellId);
    }

    public static void changeProductEnchant(L2Player player, String[] args) {
        int multisellId = Integer.parseInt(args[1]);
        int entryId = Integer.parseInt(args[2]);
        int value = Integer.parseInt(args[3]);

        final MultiSellListContainer multisellList = L2Multisell.getInstance().getList(multisellId);
        final MultiSellEntry entry = multisellList.getEntries().stream().filter(e -> e.getEntryId() == entryId).findFirst().orElse(null);
        if (entry == null){
            return;
        }
        final MultiSellIngredient ingredient = entry.getProduction().get(0);
        if (ingredient.getItemEnchant() == 0 && value == -1){
            return;
        }
        ingredient.setItemEnchant(ingredient.getItemEnchant() + value);
        showEntryPage(player, args);
    }

    public static void changeProductCount(L2Player player, String[] args) {
        int multisellId = Integer.parseInt(args[1]);
        int entryId = Integer.parseInt(args[2]);
        if (args.length<4){return;}
        int value = Integer.parseInt(args[3]);

        final MultiSellListContainer multisellList = L2Multisell.getInstance().getList(multisellId);
        final MultiSellEntry entry = multisellList.getEntries().stream().filter(e -> e.getEntryId() == entryId).findFirst().orElse(null);
        if (entry == null){
            return;
        }
        final MultiSellIngredient ingredient = entry.getProduction().get(0);
        ingredient.setItemCount(value);
        showEntryPage(player, args);
    }

    public static void addIngredient(L2Player player, String[] args) {
        int multisellId = Integer.parseInt(args[1]);
        int entryId = Integer.parseInt(args[2]);
        if (args.length < 4){return;}
        int itemId = Integer.parseInt(args[3]);
        int count = 1;
        if (args.length >= 5){
            count = Integer.parseInt(args[4]);
        }


        final MultiSellListContainer multisellList = L2Multisell.getInstance().getList(multisellId);
        final MultiSellEntry entry = multisellList.getEntries().stream().filter(e -> e.getEntryId() == entryId).findFirst().orElse(null);
        if (entry == null){
            return;
        }
        final L2Item l2Item = ItemTemplates.getInstance().getTemplate(itemId);
        if (l2Item == null){
            return;
        }
        entry.addIngredient(new MultiSellIngredient(itemId, l2Item.isStackable() ? count : 1));
        showEntryPage(player, args);
    }

    public static void changeIngredientCount(L2Player player, String[] args) {
        int multisellId = Integer.parseInt(args[1]);
        int entryId = Integer.parseInt(args[2]);
        int index = Integer.parseInt(args[3]);
        if (args.length<5){return;}
        int count = Integer.parseInt(args[4]);

        final MultiSellListContainer multisellList = L2Multisell.getInstance().getList(multisellId);
        final MultiSellEntry entry = multisellList.getEntries().stream().filter(e -> e.getEntryId() == entryId).findFirst().orElse(null);
        if (entry == null){
            return;
        }
        final MultiSellIngredient ingredient = entry.getIngredients().get(index);
        entry.getIngredients().get(index).setItemCount(ingredient.isStackable() ? count : 1);
        showEntryPage(player, args);
    }

    public static void removeIngredient(L2Player player, String[] args) {
        int multisellId = Integer.parseInt(args[1]);
        int entryId = Integer.parseInt(args[2]);
        int index = Integer.parseInt(args[3]);

        final MultiSellListContainer multisellList = L2Multisell.getInstance().getList(multisellId);
        final MultiSellEntry entry = multisellList.getEntries().stream().filter(e -> e.getEntryId() == entryId).findFirst().orElse(null);
        if (entry == null){
            return;
        }
        entry.getIngredients().remove(index);
        showEntryPage(player, args);
    }

    public static void addEntry(L2Player player, String[] args) {
        int multisellId = Integer.parseInt(args[1]);
        if (args.length < 5){return;}
        int itemId = Integer.parseInt(args[2]);
        int count = Integer.parseInt(args[3]);

        final MultiSellListContainer multisellList = L2Multisell.getInstance().getList(multisellId);
        final MultiSellEntry entry = new MultiSellEntry(multisellList.getEntries().size() + 1);
        entry.addProduct(new MultiSellIngredient(itemId, count));
        entry.addIngredient(new MultiSellIngredient(57, 1));
        multisellList.addEntry(entry);
        final int page = partitionList(multisellList.getEntries(), 10).size();
        showMultiSellRedactPage(player, multisellList, page);
        openMultiSell(player, multisellId);
    }
}
