package utils_soft.common;

import l2open.common.Html_Constructor.tags.Button;
import l2open.common.Html_Constructor.tags.Img;
import l2open.common.Html_Constructor.tags.Table;
import l2open.common.Html_Constructor.tags.parameters.Position;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.TutorialShowHtml;
import l2open.util.GArray;

import java.util.ArrayList;
import java.util.List;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;

public class Component {

    protected static int DEFAULT_TWINDOW_WIDTH = 350;

    protected static void showTWindow(L2Player player, String html, String title, String backBypass){
        String s = "<html><body><title>" + title + "</title>" + TWindowCloseButton(backBypass) + html + " </body></html>";
        System.out.println(s.length());
        player.sendPacket(new TutorialShowHtml(s));
    }

    protected static void showTWindow(L2Player player, String html, String backBypass){
        player.sendPacket(new TutorialShowHtml("<html><body>" + TWindowCloseButton(backBypass) + html + " </body></html>"));
    }

    private static String TWindowCloseButton(String backBypass){
        final Table main = new Table(1, 3).setParams(fixwidth(DEFAULT_TWINDOW_WIDTH));
        main.row(0).col(0).setParams(width(150));
        main.row(0).col(1).setParams(width(100)).insert(new Button("Назад", action("bypass -h " + backBypass), 80, 16).build());
        main.row(0).col(2).setParams(width(40)).insert(new Button(action("bypass -h TE00"), 16, 16, "L2UI_CT1.Button_DF_Delete_Down", "L2UI_CT1.Button_DF_Delete").build());
        return main.build();
    }


    public static <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(list.size(), i + batchSize);
            partitions.add(list.subList(i, end));
        }

        return partitions;
    }

    public static <T> List<List<T>> partitionList(GArray<T> list, int batchSize) {
        final ArrayList<T> arrayList = new ArrayList<>(list);
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i += batchSize) {
            int end = Math.min(arrayList.size(), i + batchSize);
            partitions.add(arrayList.subList(i, end));
        }
        return partitions;
    }

    public static String pageTable(int page, int totalPages, String bypass){
        final Table pages = new Table(1, 3);
        pages.row(0).col(0).setParams(width(100), height(20)).insert(new Button("<<", action("bypass -h admin_multisell_editor " + (page == 1? page: page -1)), 80, 20).build());
        pages.row(0).col(1).setParams(width(80)).insert(String.valueOf(page));
        pages.row(0).col(2).setParams(width(100)).insert(new Button(">>", action("bypass -h admin_multisell_editor " + (page > totalPages ? page: page + 1)), 80, 20).build());
        return pages.build();
    }

    public static String formatItemName(String itemName){
        itemName = itemName.replace("Scroll", "S.");
        itemName = itemName.replace("Recipe", "R.");
        itemName = itemName.replace("Greater", "G.");
        itemName = itemName.replace("Escape", "Esc.");
        itemName = itemName.replace("Escape", "Esc.");
        itemName = itemName.replace("<", "(");
        itemName = itemName.replace(">", ")");
        return itemName;
    }


    public static String separator(int width){
        return new Img("l2ui.squaregray", width, 1).build();
    }




}
