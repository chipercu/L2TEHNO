package commands.admin;

import ai.FakePlayersAI.FakeManager;
import ai.FakePlayersAI.PathManager.PathManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.L2PlayerAI;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.serverpackets.NpcHtmlMessage;

import java.lang.reflect.Modifier;

@SuppressWarnings("unused")
public class AdminFakePlayers implements IAdminCommandHandler, ScriptFile {
    private static enum Commands {
        admin_resetai,
        admin_path_map,
        admin_json
    }

    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanUseGMCommand){
            return false;
        }

        switch (command){
            case admin_resetai:{
                activeChar.setAI(new L2PlayerAI(activeChar));
                activeChar.setAI(FakeManager.getAIbyClassId(activeChar));
                break;
            }
            case admin_path_map:{
                PathManager.getInstance().showControlPanel(activeChar);
                break;
            }
            case admin_json:

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                final L2Object target = activeChar.getTarget();
                final String s = gson.toJson(target);
                System.out.println(s);
        }



        return true;
    }

    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    public void onLoad() {
        AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
    }

    public void onReload() {
    }

    public void onShutdown() {
    }
}