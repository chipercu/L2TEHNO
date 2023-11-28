package commands.voiced;

import communityboard.CommunityBoardBuffer;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.Buff;
import l2open.gameserver.common.BuffScheme;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.CommunityBoard;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.*;
import l2open.gameserver.model.barahlo.CBBuffSch;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Util;

import java.util.HashMap;
import java.util.Optional;
import java.util.StringTokenizer;

public class Buffer extends Functions implements IVoicedCommandHandler, ScriptFile {
    private String[] _commandList = new String[]{"buff", "petbuff"};

    public void onLoad() {
        VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
    }

    public void onReload() {
    }

    public void onShutdown() {
    }

    public boolean useVoicedCommand(String command, L2Player activeChar, String args) {
        if (command.equals("buff")){
            String _bypass = "bbs_cast_scheme_command " + args + " Player";
            CommunityBoard.getInstance().handleCommands(activeChar.getNetConnection().getConnection().getClient(), _bypass);
        } else if (command.equals("petbuff")) {
            String _bypass = "bbs_cast_scheme_command " + args + " Pet";
            CommunityBoard.getInstance().handleCommands(activeChar.getNetConnection().getConnection().getClient(), _bypass);
        }

        return true;
    }


    public String[] getVoicedCommandList() {
        return _commandList;
    }
}