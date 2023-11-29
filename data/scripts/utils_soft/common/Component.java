package utils_soft.common;

import l2open.common.Html_Constructor.tags.Button;
import l2open.common.Html_Constructor.tags.Img;
import l2open.common.Html_Constructor.tags.Table;
import l2open.common.Html_Constructor.tags.parameters.Position;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.TutorialShowHtml;

import java.util.ArrayList;
import java.util.List;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;

public class Component {

    protected static int DEFAULT_TWINDOW_WIDTH = 350;

    protected void showTWindow(L2Player player, String html, String title){
        player.sendPacket(new TutorialShowHtml("<html><body><title>" + title + "</title>" + TWindowCloseButton() + html + " </body></html>"));
    }

    protected void showTWindow(L2Player player, String html){
        player.sendPacket(new TutorialShowHtml("<html><body>" + TWindowCloseButton() + html + " </body></html>"));
    }

    private String TWindowCloseButton(){
        final Table main = new Table(1, 3).setParams(fixwidth(DEFAULT_TWINDOW_WIDTH));
        main.row(0).col(0).setParams(width(140));
        main.row(0).col(1).setParams(width(140));
        main.row(0).col(2).setParams(width(40)).insert(new Button(action("bypass -h TE00"), 16, 16, "L2UI_CT1.Button_DF_Delete_Down", "L2UI_CT1.Button_DF_Delete").build(), true);
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

    public static String separator(int width){
        return new Img("l2ui.squaregray", width, 1).build();
    }




}
