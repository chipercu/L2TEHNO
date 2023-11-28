package utils_soft.MultisellEditor;

import l2open.common.Html_Constructor.tags.Button;
import l2open.common.Html_Constructor.tags.Table;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Player;
import utils_soft.common.Component;

import java.util.ArrayList;
import java.util.Collection;
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
        final List<MultiSellListContainer> containerList = partitionList(containers, 20).get(page - 1);

        final Table main = new Table(2, 1).setParams(width(DEFAULT_TWINDOW_WIDTH));
        final Table pages = new Table(1, 3);
        pages.row(0).col(0).setParams(width(150), height(20)).insert("prev");
        pages.row(0).col(0).setParams(width(90)).insert(String.valueOf(page));
        pages.row(0).col(0).setParams(width(150)).insert("next");

        final Table table = new Table(containerList.size(), 3).setParams(border(1));
        table.row(0).col(0).setParams(width(100), height(20)).insert("id");
        table.row(0).col(1).setParams(width(80)).insert("");
        table.row(0).col(2).setParams(width(80)).insert("");
        for (int i = 1; i < containerList.size(); i++) {
            final MultiSellListContainer container = containerList.get(i - 1);
            table.row(i).col(0).insert(new Button(String.valueOf(container.getListId()), action("bypass -h admin_multisell_editor_show_multisell " + container.getListId()), 80, 20).build(), true);
            table.row(i).col(1).insert(new Button("redact", action(""), 60, 20).build());
            table.row(i).col(2).insert(new Button("restore", action(""), 60, 20).build());
        }

        main.row(0).col(0).insert(pages.build());
        main.row(1).col(0).insert(table.build());

        showTWindow(player, main.build(), "MultiSell Editor");
    }


    public void showMultiSell(L2Player player, String[] args){
        final int id = Integer.parseInt(args[1]);
        L2Multisell.getInstance().SeparateAndSend(id, player, 0);
    }











}
