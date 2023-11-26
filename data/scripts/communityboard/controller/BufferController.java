package communityboard.controller;

import communityboard.components.buffer.BufferComponent;
import communityboard.config.BufferConfig;
import communityboard.service.buffer.BuffService;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.*;

import java.util.*;

public class BufferController extends BaseBBSManager implements ICommunityHandler, ScriptFile {

    private BufferComponent bufferComponent;

    private static enum Commands {
        _bbsbuffer,
        bbs_buffer_global_settings,
        bbs_buffer_save_config,
        bbs_show_buffs,
        bbs_add_buff,
        bbs_create_scheme,
        bbs_create_ready_scheme,
        bbs_show_redact_scheme,
        bbs_add_buff_to_scheme,
        bbs_show_add_buff_to_scheme_page,
        bbs_remove_buff_from_scheme,
        bbs_remove_buff,
        bbs_remove_scheme,
        bbs_cast_buff,
        bbs_cast_scheme,
        bbs_show_all_buffs,
        bbs_clear_buffs,
        bbs_clear_scheme,
        bbs_change_enchant_type,
        bbs_show_change_buff_params,
        bbs_change_list_index
    }

    @Override
    public void parsecmd(String bypass, L2Player player) {
        if (player.getEventMaster() != null && player.getEventMaster().blockBbs())
            return;
        if (player.is_block)
            return;
        if (!check(player))
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
        } else if ("bbs_buffer_global_settings".equals(cmd)) {
            bufferComponent.showBufferConfigPage(player);
        }else if ("bbs_buffer_save_config".equals(cmd)) {
            bufferComponent.setConfig(player, args);
        } else if ("bbs_show_buffs".equals(cmd)) {
            bufferComponent.showBuffs(player, args);
        } else if ("bbs_create_scheme".equals(cmd)) {
            bufferComponent.createPersonalScheme(player, args);
        } else if ("bbs_create_ready_scheme".equals(cmd)) {
            bufferComponent.createSystemScheme(player, args);
        } else if ("bbs_show_redact_scheme".equals(cmd)) {
            bufferComponent.showRedactScheme(player, args);
        } else if ("bbs_add_buff".equals(cmd)) {
            bufferComponent.addBuff(player, args);
        } else if ("bbs_add_buff_to_scheme".equals(cmd)) {
            bufferComponent.addBuffToScheme(args, player);
        } else if ("bbs_show_add_buff_to_scheme_page".equals(cmd)) {
            bufferComponent.showAddBuffToScheme(args, player);
        } else if ("bbs_remove_buff".equals(cmd)) {
            bufferComponent.removeBuff(args, player);
        } else if ("bbs_remove_buff_from_scheme".equals(cmd)) {
            bufferComponent.removeBuffFromScheme(args, player);
        } else if ("bbs_remove_buff_ready_set".equals(cmd)) {
            bufferComponent.removeBuffFromScheme(args, player);
        } else if ("bbs_remove_scheme".equals(cmd)) {
            bufferComponent.removeScheme(player, args);
        } else if ("bbs_cast_buff".equals(cmd)) {
            bufferComponent.castBuff(player, args);
        } else if ("bbs_cast_scheme".equals(cmd)) {
            bufferComponent.castScheme(player, args);
        } else if ("bbs_show_all_buffs".equals(cmd)) {
            bufferComponent.showAllBuffsWindow(player, args);
        } else if ("bbs_clear_buffs".equals(cmd)) {
            bufferComponent.clearBuffs(player, args);
        } else if ("bbs_clear_scheme".equals(cmd)) {
            bufferComponent.clearScheme(player, args);
        } else if ("bbs_change_enchant_type".equals(cmd)) {
            bufferComponent.changeEnchantType(args, player);
        } else if ("bbs_show_change_buff_params".equals(cmd)) {
            bufferComponent.showChangeBuffParams(args, player);
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


    public boolean check(L2Player player) {
        if (player == null)
            return false;
        else if (player.isGM())
            return true;
        else if (player.isInOlympiadMode()) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (player.getReflection().getId() != ReflectionTable.DEFAULT && !ConfigValue.BufferInInstance && !check_event(player)) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (player.isInDuel()) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (player.isInCombat() && !ConfigValue.BufferInCombat) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if ((player.isOnSiegeField() || player.isInZoneBattle()) && !ConfigValue.BufferOnSiege && player.isInEvent() != 5 && !check_event(player)) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (player.isInEvent() > 0 && !check_event(player)) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (player.isFlying()) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (player.isInWater() && !ConfigValue.BufferInWater) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (player.isDead() || player.isMovementDisabled() || player.isAlikeDead() || player.isCastingNow() || player.isAttackingNow() || player.getVar("jailed") != null || player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped()) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (ConfigValue.BufferOnlyPeace && !player.isInZone(L2Zone.ZoneType.peace_zone) && !player.isInZone(L2Zone.ZoneType.epic) && player.getReflection().getId() == ReflectionTable.DEFAULT) {
            player.sendMessage("Функция доступна только в мирной зоне, эпик зоне, а так же в инстансах.");
            return false;
        } else
            return true;
    }

    public boolean check_event(L2Player player) {
        switch (player.isInEvent()) {
            case 1:
                return ConfigValue.FightClubBattleUseBuffer;
            case 2:
                return ConfigValue.LastHeroBattleUseBuffer;
            case 3:
                return ConfigValue.CaptureTheFlagBattleUseBuffer;
            case 4:
                return ConfigValue.TeamvsTeamBattleUseBuffer;
            case 5:
                return ConfigValue.TournamentBattleUseBuffer;
            case 6:
                return ConfigValue.EventBoxUseBuffer;
            case 11:
                return player.getEventMaster().state == 1;
            case 12:
                return ConfigValue.Tournament_UseBuffer;
            case 13:
                return ConfigValue.DeathMatchUseBuffer;
        }
        return false;
    }


}