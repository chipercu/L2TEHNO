package l2open.gameserver.clientpackets;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

//import ai.FakePlayersAI.PathManager.PathManager;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.communitybbs.CommunityBoard;
import l2open.gameserver.communitybbs.PartyMaker.PartyMaker;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.instancemanager.OlympiadHistoryManager;
import l2open.gameserver.model.BypassManager.DecodedBypass;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.serverpackets.ShowPCCafeCouponShowUI;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.util.Files;
import l2open.util.Log;

public class RequestBypassToServer extends L2GameClientPacket {
    //Format: cS
    private static Logger _log = Logger.getLogger(RequestBypassToServer.class.getName());
    private DecodedBypass bp = null;
    private String bypass = null;

    @Override
    public void readImpl() {
        bypass = readS();
        //_log.info("BypassToServer: -" + bypass + "-");
        if (!bypass.isEmpty())
            bp = getClient().getActiveChar().decodeBypass(bypass, false);

        if (ConfigValue.DebugBypassType == 1) {
            String log_str = "bbs1=" + bp.bbs + " bypass='" + bypass + "' enc_bypass_1='" + bp.bypass + "' enc_bypass_2='" + bp.bypass2 + "'";
            Log.addMy(log_str, "debug_bypass", getClient().getActiveChar().getName());
        } else if (ConfigValue.DebugBypassType == 2) {
            String log_str = getClient().getActiveChar().getName() + ": bbs1=" + bp.bbs + " bypass='" + bypass + "' enc_bypass_1='" + bp.bypass + "' enc_bypass_2='" + bp.bypass2 + "'";
            Log.add(log_str, "debug_bypass");
        }
    }

    @Override
    public void runImpl() {

        L2Player activeChar = getClient().getActiveChar();
        if (activeChar == null || bp == null)
            return;
        if (System.currentTimeMillis() - activeChar.getLastRequestBypassToServerPacket() < ConfigValue.RequestBypassToServerPacketDelay) {
            activeChar.sendActionFailed();
            return;
        }
        activeChar.setLastRequestBypassToServerPacket();
        try {
            //_log.info("Bypass: " + bp.bypass);
            L2NpcInstance npc = activeChar.getLastNpc();
            L2Object target = activeChar.getTarget();
            // Нахрена её туда впихнули???
            if (/*npc == null && */target != null && target.isNpc())
                npc = (L2NpcInstance) target;

            if (activeChar.is_block && !bp.bypass.startsWith("hwid_confirm") && !bp.bypass.startsWith("scripts_vidak") && !bp.bypass.startsWith("script_")) {
                if (bp.bypass.startsWith("_bbsenchantver;fishing;")) {
                    if (activeChar._setImage.equals(bp.bypass.substring(23))) {
                        activeChar._setImage = "";
                        activeChar.is_block = false;
                        activeChar.sendPacket(new ShowBoard(0));
                        activeChar.startLookingForFishTask(null, null, false);
                    } else {
                        activeChar._setImage = "";
                        activeChar.sendPacket(new ShowBoard(0));
                        activeChar.setFish(null);
                        activeChar.setLure(null);
                        activeChar.is_block = false;
                    }
                }
            } else if (bp.bypass.equals("enter_bonus_code"))
                activeChar.sendPacket(new ShowPCCafeCouponShowUI());
            else if (bp.bypass.startsWith("admin_"))
                AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, bp.bypass);
            else if (bp.bypass.startsWith("user_")) {
                String command = bp.bypass.substring(5).trim();
                String word = command.split("\\s+")[0];
                String args = command.substring(word.length()).trim();
                IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(word);

                if (vch != null)
                    vch.useVoicedCommand(word, activeChar, args);
                else
                    _log.warning("Unknow voiced command '" + word + "'");
            } else if (bp.bbs && (activeChar.isDead() && !ConfigValue.CheckInDeath || activeChar.isMovementDisabled() && !ConfigValue.CheckInMoveDisabled || activeChar.isOnSiegeField() && !ConfigValue.CheckInSiege || activeChar.isInCombat() && !ConfigValue.CheckInCombat && !bp.bypass.startsWith("_bbsenchantver") || activeChar.isAttackingNow() && !ConfigValue.CheckInAttack || activeChar.isInOlympiadMode() && !ConfigValue.CheckInOlympiad || activeChar.getVar("jailed") != null || activeChar.isFlying() && !ConfigValue.CheckInFly || activeChar.isInDuel() && !ConfigValue.CheckInDuel || activeChar.getReflectionId() > 0 && !ConfigValue.CheckInInstance || activeChar.isOutOfControl() && !ConfigValue.CheckInOutOfControl || activeChar.isInEvent() != 0 && !ConfigValue.CheckInEvent || !activeChar.isInZonePeace() && activeChar.getNetConnection().getBonus() <= 1 && ConfigValue.CheckOutOfTown)) {
                activeChar.sendPacket(new ExShowScreenMessage(new CustomMessage("communityboard.checkcondition.false.screen", activeChar).toString(), 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                activeChar.sendMessage(new CustomMessage("communityboard.checkcondition.false.chat", activeChar));

                String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "terms.htm", activeChar);
                if (html != null) {
                    String check = " <font color=\"LEVEL\">*</font>";
                    String isTrue = "<font color=\"66FF33\">" + new CustomMessage("common.allowed", activeChar).toString() + "</font>";
                    String isFalse = "<font color=\"FF0000\">" + new CustomMessage("common.prohibited", activeChar).toString() + "</font>";
                    String onlyPremium = "<font color=\"LEVEL\">" + new CustomMessage("common.need.premium", activeChar).toString() + "</font>";

                    html = html.replace("%config_isInZonePeace%", ConfigValue.CheckOutOfTown ? onlyPremium : isTrue);
                    html = html.replace("%config_isDead%", ConfigValue.CheckInDeath ? isTrue : isFalse);
                    html = html.replace("%config_isMovementDisabled%", ConfigValue.CheckInMoveDisabled ? isTrue : isFalse);
                    html = html.replace("%config_isOnSiegeField%", ConfigValue.CheckInSiege ? isTrue : isFalse);
                    html = html.replace("%config_isInCombat%", ConfigValue.CheckInCombat ? isTrue : isFalse);
                    html = html.replace("%config_isAttackingNow%", ConfigValue.CheckInAttack ? isTrue : isFalse);
                    html = html.replace("%config_isInOlympiadMode%", ConfigValue.CheckInOlympiad ? isTrue : isFalse);
                    html = html.replace("%config_isFlying%", ConfigValue.CheckInFly ? isTrue : isFalse);
                    html = html.replace("%config_isInDuel%", ConfigValue.CheckInDuel ? isTrue : isFalse);
                    html = html.replace("%config_isInInstance%", ConfigValue.CheckInInstance ? isTrue : isFalse);
                    html = html.replace("%config_isInJailed%", ConfigValue.CheckInJail ? isTrue : isFalse);
                    html = html.replace("%config_isOutOfControl%", ConfigValue.CheckInOutOfControl ? isTrue : isFalse);
                    html = html.replace("%config_isInEvent%", ConfigValue.CheckInEvent ? isTrue : isFalse);

                    html = html.replace("%check_isInZonePeace%", !activeChar.isInZonePeace() && activeChar.getNetConnection().getBonus() <= 1 && ConfigValue.CheckOutOfTown ? check : "");
                    html = html.replace("%check_isDead%", activeChar.isDead() && !ConfigValue.CheckInDeath ? check : "");
                    html = html.replace("%check_isMovementDisabled%", activeChar.isMovementDisabled() && !ConfigValue.CheckInMoveDisabled ? check : "");
                    html = html.replace("%check_isOnSiegeField%", activeChar.isOnSiegeField() && !ConfigValue.CheckInSiege ? check : "");
                    html = html.replace("%check_isInCombat%", activeChar.isInCombat() && !ConfigValue.CheckInCombat ? check : "");
                    html = html.replace("%check_isAttackingNow%", activeChar.isAttackingNow() && !ConfigValue.CheckInAttack ? check : "");
                    html = html.replace("%check_isInOlympiadMode%", activeChar.isInOlympiadMode() && !ConfigValue.CheckInOlympiad ? check : "");
                    html = html.replace("%check_isFlying%", activeChar.isFlying() && !ConfigValue.CheckInFly ? check : "");
                    html = html.replace("%check_isInDuel%", activeChar.isInDuel() && !ConfigValue.CheckInDuel ? check : "");
                    html = html.replace("%check_isInInstance%", activeChar.getReflectionId() > 0 && !ConfigValue.CheckInInstance ? check : "");
                    html = html.replace("%check_isInJailed%", activeChar.getVar("jailed") != null && !ConfigValue.CheckInJail ? check : "");
                    html = html.replace("%check_isOutOfControl%", activeChar.isOutOfControl() && !ConfigValue.CheckInOutOfControl ? check : "");
                    html = html.replace("%check_isInEvent%", activeChar.isInEvent() != 0 && !ConfigValue.CheckInEvent ? check : "");

                    ShowBoard.separateAndSend(html, activeChar);
                }
                return;
            } else if (bp.bbs)
                CommunityBoard.getInstance().handleCommands(getClient(), bp.bypass);
            else if (bp.bypass.startsWith("bbs:"))
                CommunityBoard.getInstance().handleCommands(getClient(), bp.bypass.substring(4));
            else if (bp.bypass.equals("come_here") && activeChar.isGM())
                comeHere(getClient());
            else if (bp.bypass.startsWith("player_help ")){
                playerHelp(activeChar, bp.bypass.substring(12));
            }
            //TODO [FUZZY]
            else if (bp.bypass.startsWith("party_maker:")) {
                PartyMaker.getInstance().handleCommands(getClient(), bp.bypass.substring(12));
            }else if (bp.bypass.startsWith("path_manager:")){
//                PathManager.getInstance().handleCommands(getClient(), bp.bypass.substring(13));
            }

            //TODO [FUZZY]
            else if (bp.bypass.startsWith("script_")) {
                if (activeChar.getEventMaster() != null && activeChar.getEventMaster().blockNpcBypass())
                    return;
                String command = bp.bypass.substring(7).trim();
                String[] word = command.split("\\s+");
                String args = command.substring(word[0].length()).trim();
                String[] path = word[0].split(":");

                if (path.length != 2) {
                    _log.warning("Bad Script bypass!");
                    return;
                }

                if (word.length == 1)
                    activeChar.callScripts(path[0], path[1], new Object[]{});
                else
                    activeChar.callScripts(path[0], path[1], new Object[]{args});
            } else if (bp.bypass.startsWith("scripts_")) {
                if (!activeChar.isGM() && activeChar.getEventMaster() != null && activeChar.getEventMaster().blockNpcBypass())
                    return;
                String command = bp.bypass.substring(8).trim();
                String[] word = command.split("\\s+");
                String[] args = command.substring(word[0].length()).trim().split("\\s+");
                String[] path = word[0].split(":");
                if (path.length != 2) {
                    _log.warning("Bad Script bypass!");
                    return;
                }

                HashMap<String, Object> variables = new HashMap<String, Object>();

                if (npc != null)
                    variables.put("npc", npc);
                else
                    variables.put("npc", null);
                if (target != null)
                    variables.put("target", target);
                else
                    variables.put("target", null);

                if (word.length == 1)
                    activeChar.callScripts(path[0], path[1], new Object[]{}, variables);
                else
                    activeChar.callScripts(path[0], path[1], new Object[]{args}, variables);
            } else if (bp.bypass.startsWith("npc_")) {
                if (activeChar.getEventMaster() != null && activeChar.getEventMaster().blockNpcBypass())
                    return;
                int endOfId = bp.bypass.indexOf('_', 5);
                String id;
                if (endOfId > 0)
                    id = bp.bypass.substring(4, endOfId);
                else
                    id = bp.bypass.substring(4);
                L2Object object = activeChar.getVisibleObject(Integer.parseInt(id));
                if (object != null && object.isNpc() && endOfId > 0 && activeChar.isInRange(object.getLoc(), ((L2NpcInstance) object).INTERACTION_DISTANCE + ((L2NpcInstance) object).BYPASS_DISTANCE_ADD) && Math.abs(activeChar.getZ() - object.getZ()) < 100) // Сделаем +150 для последующего диалога...На оффе вообще для перехода по ШТМЛ нету проверки на дистанцию, а вот на выполнение функций есть)
                {
                    activeChar.setLastNpc((L2NpcInstance) object);
                    ((L2NpcInstance) object).onBypassFeedback(activeChar, bp.bypass.substring(endOfId + 1));
                }
            } else if (bp.bypass.startsWith("_diary")) {
                String params;
                StringTokenizer st;
                int heroclass;
                int heropage;
                params = this.bp.bypass.substring(bp.bypass.indexOf("?") + 1);
                st = new StringTokenizer(params, "&");
                heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                heropage = Integer.parseInt(st.nextToken().split("=")[1]);
                int heroid = Hero.getInstance().getHeroByClass(heroclass);
                if (heroid > 0)
                    Hero.getInstance().showHeroDiary(activeChar, heroclass, heroid, heropage);
            } else if (this.bp.bypass.startsWith("_match")) {
                String params;
                StringTokenizer st;
                int heroclass;
                int heropage;
                params = this.bp.bypass.substring(this.bp.bypass.indexOf("?") + 1);
                st = new StringTokenizer(params, "&");
                heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                heropage = Integer.parseInt(st.nextToken().split("=")[1]);

                OlympiadHistoryManager.getInstance().showHistory(activeChar, heroclass, heropage);
            } else if (bp.bypass.startsWith("_olympiad")) {
                String val = bp.bypass.substring(10, bp.bypass.length());
                String command = val.split("=")[1].split("&")[0];
                String value = val.split("=")[2];
                Olympiad.useCommand(activeChar, command, value);
            } else if (bp.bypass.startsWith("multisell ")) {
                if (activeChar.getEventMaster() != null && activeChar.getEventMaster().blockNpcBypass())
                    return;
                L2Multisell.getInstance().SeparateAndSend(Integer.parseInt(bp.bypass.substring(10)), activeChar, 0);
            } else if (bp.bypass.startsWith("Quest ")) {
                String p = bp.bypass2.substring(6).trim();
                int idx = p.indexOf(' ');
                if (idx < 0)
                    activeChar.processQuestEvent(p, "", npc);
                else
                    activeChar.processQuestEvent(p.substring(0, idx), p.substring(idx).trim(), npc);
            } else if (bp.bypass.startsWith("manor_menu_select?")) // manor_menu_select?ask=1&state=-1&time=0
            {
                L2Object object = activeChar.getTarget();
                if (object != null && object.isNpc()) {
                    ((L2NpcInstance) object).onBypassFeedback(activeChar, bp.bypass);
                    int startOfId = bp.bypass.indexOf("&");
                    int endOfId = bp.bypass.lastIndexOf("&");
                    int ask = Integer.parseInt(bp.bypass.substring(22, startOfId));
                    int state = Integer.parseInt(bp.bypass.substring(startOfId + 7, endOfId));
                    int time = Integer.parseInt(bp.bypass.substring(endOfId + 6));

                    ((L2NpcInstance) object).MANOR_MENU_SELECTED(activeChar, ask, state, time); // Мб все же когда-то у нас все будет по ПТС)))
                }
            } else if (bp.bypass.startsWith("menu_select?")) // menu_select?ask=1&reply=1
            {
                int endOfId = bp.bypass.indexOf("&");
                int reply = 0;
                int ask = 0;
                if (endOfId == -1)
                    ask = Integer.parseInt(bp.bypass.substring(16));
                else {
                    ask = Integer.parseInt(bp.bypass.substring(16, endOfId));
                    reply = Integer.parseInt(bp.bypass.substring(endOfId + 7));
                }

                try {
                    L2Object object = activeChar.getTarget();
                    if (object != null && object.isNpc() && object.getAI().is_pts)
                        object.getAI().MENU_SELECTED(activeChar, ask, reply);
                    else if (object != null && object.isNpc())
                        ((L2NpcInstance) object).MENU_SELECTED(activeChar, ask, reply);
                } catch (Exception e) {
                }
            } else if (bp.bypass.startsWith("quest_accept?")) // quest_accept?quest_id=708
            {
                try {
                    L2Object object = activeChar.getTarget();
                    if (object != null && object.isNpc())
                        ((L2NpcInstance) object).QUEST_ACCEPTED(Integer.parseInt(bp.bypass.substring(22)), activeChar);
                } catch (Exception e) {
                }
            } else if (bp.bypass.equals("talk_select")) // talk_select - эта поебень на кнопочках Квест.
            {
                try {
                    L2Object object = activeChar.getTarget();
                    if (object != null && object.isNpc())
                        ((L2NpcInstance) object).TALK_SELECTED((L2NpcInstance) object, activeChar, -1, 0);
                } catch (Exception e) {
                }
            } else if (bp.bypass.equals("teleport_request")) // talk_select - эта поебень на кнопочках Квест.
            {
                try {
                    L2Object object = activeChar.getTarget();
                    if (object != null && object.isNpc())
                        ((L2NpcInstance) object).TELEPORT_REQUESTED(activeChar);
                } catch (Exception e) {
                }
            } else if (bp.bypass.startsWith("talk_select?")) // talk_select?code=1 - эта поебень на кнопочках Квест.
            {
                try {
                    L2Object object = activeChar.getTarget();
                    if (object != null && object.isNpc())
                        ((L2NpcInstance) object).TALK_SELECTED((L2NpcInstance) object, activeChar, Integer.parseInt(bp.bypass.substring(17)), 1);
                } catch (Exception e) {
                }
            } else if (bp.bypass.startsWith("hwid_confirm"))
                activeChar.hwid_confirm(bp.bypass.substring(12));
            else if (bp.bypass.startsWith("_buff_store")) {
                String[] txt = bp.bypass.substring(12).split(":");
                int object_id = Integer.parseInt(txt[0]);
                int target_type = Integer.parseInt(txt[1]);
                int skill_id = Integer.parseInt(txt[2]);
                long price = Long.parseLong(txt[3]);

                L2Player object = activeChar.getVisibleObject(object_id).getPlayer();
                L2ItemInstance pay = activeChar.getInventory().getItemByItemId(ConfigValue.BuffStoreItemId);
                if (activeChar.isInRange(object.getLoc(), 300) && Math.abs(activeChar.getZ() - object.getZ()) < 100) // Сделаем +150 для последующего диалога...На оффе вообще для перехода по ШТМЛ нету проверки на дистанцию, а вот на выполнение функций есть)
                {
                    if (pay != null && pay.getCount() >= price) {
                        if (object != null) {
                            activeChar.getInventory().destroyItem(pay, price, true);
                            object.getInventory().addItem(ConfigValue.BuffStoreItemId, price);
                            activeChar.buff_store(object, activeChar, target_type, skill_id);
                        }
                    } else if (ConfigValue.BuffStoreItemId == 57)
                        activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    else
                        activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                }
            } else if (bp.bypass.startsWith("_buff_list")) {
                String[] txt = bp.bypass.substring(11).split(":");
                int object_id = Integer.parseInt(txt[0]);
                int page_id = Integer.parseInt(txt[1]);
                int target_type = Integer.parseInt(txt[2]);

                L2Player object = activeChar.getVisibleObject(object_id).getPlayer();
                if (object != null)
                    activeChar.buff_list(object, page_id, target_type);
            } else
                _log.warning("RequestBypassToServer(201): Wrong bypass=" + bp.bypass + " char: " + activeChar.getName() + " npcId: " + ((activeChar.getTarget() == null || !activeChar.getTarget().isNpc()) ? "" : ((L2NpcInstance) activeChar.getTarget()).getNpcId()));
        } catch (Exception e) {
            e.printStackTrace();
            String st = "Bad RequestBypassToServer: '" + bp.bypass + "'";
            L2Object target = activeChar.getTarget();
            if (activeChar.getTarget() != null)
                if (target.isNpc())
                    st = st + " via NPC #" + ((L2NpcInstance) target).getNpcId();
            _log.log(Level.WARNING, st, e);
        }
    }

    private void comeHere(L2GameClient client) {
        L2Object obj = client.getActiveChar().getTarget();
        if (obj != null && obj.isNpc()) {
            L2NpcInstance temp = (L2NpcInstance) obj;
            L2Player activeChar = client.getActiveChar();
            temp.setTarget(activeChar);
            temp.moveToLocation(activeChar.getLoc(), 0, true);
        }
    }

    private void playerHelp(L2Player activeChar, String path) {
        if (path.indexOf("..") != -1)
            return;

        path = path.substring(0).trim();

        String filename = "data/html/" + path;
        NpcHtmlMessage html = new NpcHtmlMessage(5);
        html.setFile(filename);
        activeChar.sendPacket(html);
    }

    public String getType() {
        return "[C] RequestBypassToServer[" + bypass + "][" + (bp == null ? "null" : bp.bypass) + "]";
    }
}