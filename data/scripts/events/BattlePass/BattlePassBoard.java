package events.BattlePass;

import communityboard.CommunityBoardBuffer;
import l2open.common.Html_Constructor.tags.Table;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ShowBoard;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.border;
import static l2open.common.Html_Constructor.tags.parameters.Parameters.height;
import static l2open.common.Html_Constructor.tags.parameters.Parameters.width;

public class BattlePassBoard implements ICommunityHandler, ScriptFile {

    private static enum Commands {
        _bbs_battle_pass_show
    }

    public static void showBattlePass(L2Player player){
        StringBuilder page = new StringBuilder();
        page.append("<html noscrollbar>\n" +
                "<title>BATTLE PASS</title>\n" +
                "<body><br><table width=755><tr><center><td>");

        Table table = new Table(2, 1).setParams(border(1), width(769));

        table.row(0).col(0).setParams(height(250)).insert(questPlace());
        table.row(1).col(0).setParams(height(200)).insert(battlePassPlace());

        page.append(table.build());

        page.append("</td></center></tr></body></html>");
        ShowBoard.separateAndSend(page.toString(), player);
    }

    public static String questPlace(){
        return "QUEST PLACE";
    }
    public static String battlePassPlace(){
        return "BATTLE PASS PLACE";
    }


    @Override
    public void parsecmd(String command, L2Player player) {
        if (player.getEventMaster() != null && player.getEventMaster().blockBbs()) {
            return;
        }
        if (player.is_block) {
            return;
        }

        if (command.equalsIgnoreCase(Commands._bbs_battle_pass_show.name())){
            showBattlePass(player);
        }

    }

    @Override
    public Enum[] getCommunityCommandEnum() {
        return Commands.values();
    }

    @Override
    public void onLoad() {
        CommunityHandler.getInstance().registerCommunityHandler(this);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }
}
