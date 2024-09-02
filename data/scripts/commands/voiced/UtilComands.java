package commands.voiced;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.communitybbs.CommunityBoard;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;

public class UtilComands extends Functions implements IVoicedCommandHandler, ScriptFile {
    private String[] _commandList = new String[]{"itemicon"};

    public void onLoad() {
        VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
    }

    public void onReload() {
    }

    public void onShutdown() {
    }

    public boolean useVoicedCommand(String command, L2Player activeChar, String args) {
        if (command.equals("itemicon")){
            try {
                int id = Integer.parseInt(args);
                L2Item template = ItemTemplates.getInstance().getTemplate(id);
                String icon = template.getIcon();
                activeChar.sendMessage(template.getName() + ": " + icon);
                System.out.println(template.getName() + ": " + icon);
            } catch (Exception ignore){}

        }

        return true;
    }


    public String[] getVoicedCommandList() {
        return _commandList;
    }
}