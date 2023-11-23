package communityboard.controller;

import communityboard.components.buffer.BufferComponent;
import communityboard.service.buffer.BuffService;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.util.*;

import java.util.*;

public class BufferController extends BaseBBSManager implements ICommunityHandler, ScriptFile {

    private BufferComponent bufferComponent;
    private static final long SYSTEM_LISTS = -1;

    private static enum Commands {
        _bbsbuffer,
        bbs_show_buffs,
        bbs_add_buff,
        bbs_create_scheme,
        bbs_remove_buff,
        bbs_remove_scheme,
        bbs_cast_buff,
        bbs_cast_scheme,
        bbs_show_all_buffs,
        bbs_clear_buffs,
        bbs_clear_scheme,
        bbs_change_enchant_type,
        bbs_show_change_buff_params,
        bbs_show_redact_scheme,
        bbs_change_list_index
    }

    @Override
    public void parsecmd(String bypass, L2Player player) {
        if (player.getEventMaster() != null && player.getEventMaster().blockBbs())
            return;
        if (player.is_block)
            return;
        if (!bufferComponent.check(player))
            return;

        if (ConfigValue.BufferAffterRes) {
            long time = (player.getResTime() + (ConfigValue.BufferAffterResTime * 1000L) - System.currentTimeMillis());
            if (time > 0) {
                int wait = (int) (time / 1000);
                player.sendMessage(new CustomMessage("common.not.yet.wait", player).addNumber(wait <= 0 ? 1 : wait).addString(DifferentMethods.declension(player, wait, "Second")));
                return;
            }
        }

        StringTokenizer st = new StringTokenizer(bypass);
        String cmd = st.nextToken();
        String[] args = new String[10];
        int i = 0;
        while (st.hasMoreTokens()) {
            args[i] = st.nextToken();
            i++;
        }
        if ("_bbsbuffer".equals(cmd)) {
            bufferComponent.showMainPage(player);
        } else if ("bbs_show_buffs".equals(cmd)) {
            bufferComponent.showBuffs(player, args);
        } else if ("bbs_show_redact_scheme".equals(cmd)) {
            bufferComponent.createReadyScheme(player, args);
        } else if ("bbs_add_buff".equals(cmd)) {
            bufferComponent.addBuff(player, args);
        } else if ("bbs_add_buff_ready_set".equals(cmd)) {
            bufferComponent.addBuffToScheme(args, player);
        } else if ("bbs_remove_buff".equals(cmd)) {
            bufferComponent.removeBuff(args, player);
        } else if ("bbs_remove_buff_premium_set".equals(cmd)) {
            bufferComponent.removeBuffFromScheme(args, player);
        } else if ("bbs_remove_buff_ready_set".equals(cmd)) {
            bufferComponent.removeBuffFromScheme(args, player);
        } else if ("bbs_remove_scheme".equals(cmd)) {
            bufferComponent.removeScheme(player, args);
        } else if ("bbs_cast_buff".equals(cmd)) {
            bufferComponent.castBuff(player, args);
        } else if ("bbs_cast_scheme".equals(cmd)) {
            bufferComponent.castScheme(player, args);
        } else if ("bbs_create_scheme".equals(cmd)) {
            bufferComponent.saveScheme(player, args);
        } else if ("bbs_show_all_buffs".equals(cmd)) {
            bufferComponent.showAllBuffs(player, args);
        } else if ("bbs_clear_buffs".equals(cmd)) {
            bufferComponent.clearBuffs(player, args);
        } else if ("bbs_clear_scheme".equals(cmd)) {
            bufferComponent.clearScheme(player, args);
        } else if ("bbs_change_enchant_type".equals(cmd)) {
            bufferComponent.changeEnchantType(args, player);
        } else if ("bbs_show_change_buff_params".equals(cmd)) {
            bufferComponent.showChangeBuffParams(args, player);
        } else if ("bbs_change_list_index".equals(cmd)) {
            bufferComponent.changeListIndex(player, args);
        } else if ("bbs_test_buffer".equals(cmd)) {
            String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "buffer/test.htm", player);
            ShowBoard.separateAndSend(addCustomReplace(html), player);
        }
    }

    public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player) {
    }

    public void onLoad() {
        bufferComponent = new BufferComponent(new BuffService());
        CommunityHandler.getInstance().registerCommunityHandler(this);
    }

    public void onReload() {
    }

    public void onShutdown() {
    }

    @SuppressWarnings("rawtypes")
    public Enum[] getCommunityCommandEnum() {
        return Commands.values();
    }


}