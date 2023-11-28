package utils_soft.common;

import l2open.common.Html_Constructor.tags.Button;
import l2open.common.Html_Constructor.tags.Table;
import l2open.common.Html_Constructor.tags.parameters.Position;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.TutorialShowHtml;

import java.util.ArrayList;
import java.util.List;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;

public class Component {

    protected static int DEFAULT_TWINDOW_WIDTH = 390;

    protected void showTWindow(L2Player player, String html, String title){
        player.sendPacket(new TutorialShowHtml("<html><body><title>" + title + "</title>" + TWindowCloseButton() + html + " </body></html>"));
    }

    protected void showTWindow(L2Player player, String html){
        player.sendPacket(new TutorialShowHtml("<html><body>" + TWindowCloseButton() + html + " </body></html>"));
    }

    private String TWindowCloseButton(){
        final Table main = new Table(1, 1).setParams(width(DEFAULT_TWINDOW_WIDTH));
        main.row(0).col(0).setParams(valign(Position.RIGHT)).insert(new Button(action("bypass -h TE00"), 16, 16, "L2UI_CT1.Button_DF_Delete_Down", "L2UI_CT1.Button_DF_Delete").build(), true);
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




}
