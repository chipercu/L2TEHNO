package utils_soft.MultisellEditor;

import l2open.common.Html_Constructor.tags.Button;
import l2open.common.Html_Constructor.tags.Table;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Player;
import l2open.util.ArrayUtils;
import utils_soft.common.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;
import static l2open.gameserver.model.L2Multisell.*;

public class MultiSellEditorComponent extends Component {

    private final MultiSellEditorService service;
    private L2Multisell multisell = getInstance();

    public MultiSellEditorComponent(MultiSellEditorService service) {
        this.service = service;
    }


    public void showMainPage(L2Player player, String[] args){
        int page = Integer.parseInt(args[1]);
        final ArrayList<MultiSellListContainer> containers = new ArrayList<>(multisell.getLists().values());
        containers.sort(Comparator.comparingInt(MultiSellListContainer::getListId));
        final List<MultiSellListContainer> containerList = partitionList(containers, 20).get(page - 1);

        final Table main = new Table(2, 1).setParams(width(DEFAULT_TWINDOW_WIDTH));
        final Table pages = new Table(1, 3);
        pages.row(0).col(0).setParams(width(100), height(20)).insert(new Button("<<", action("bypass -h admin_multisell_editor " + (page == 1? page: page -1)), 80, 20).build());
        pages.row(0).col(1).setParams(width(80)).insert(String.valueOf(page));
        pages.row(0).col(2).setParams(width(100)).insert(new Button(">>", action("bypass -h admin_multisell_editor " + (page > containerList.size() ? page: page + 1)), 80, 20).build());

        final Table table = new Table(containerList.size() + 1, 1);
        for (int i = 0; i < containerList.size(); i++) {
            final Table containerTable = new Table(2, 1);
            final Table multiSellTable = new Table(1, 3);
            final MultiSellListContainer container = containerList.get(i);
            multiSellTable.row(0).col(0).insert(new Button(String.valueOf(container.getListId()), action("bypass -h admin_multisell_editor_show_multisell " + container.getListId()), 80, 20).build(), true);
            multiSellTable.row(0).col(1).insert(new Button("redact", action(""), 60, 20).build());
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










    public void openMultiSell(L2Player player, String[] args){
        final int id = Integer.parseInt(args[1]);
        L2Multisell.getInstance().SeparateAndSend(id, player, 0);
    }











}
