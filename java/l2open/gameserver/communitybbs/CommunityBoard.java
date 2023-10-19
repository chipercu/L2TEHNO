package l2open.gameserver.communitybbs;

import l2open.config.*;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.communitybbs.Manager.*;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2ObjectTasks;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.handler.*;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.StatsSet;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.*;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CommunityBoard {
    protected static Logger _log = Logger.getLogger(CommunityBoard.class.getName());
    private static CommunityBoard _instance;
    private static int MONEY_ID = 4357;

    public static CommunityBoard getInstance() {
        if (_instance == null)
            _instance = new CommunityBoard();
        return _instance;
    }

    public void handleCommands(L2GameClient client, String command) {
        //_log.info("CommunityBoard: -> handleCommands: "+command);
        L2Player activeChar = client.getActiveChar();
        if (activeChar == null)
            return;
        try {
            if (!ConfigValue.AllowCommunityBoard || activeChar.isCommunityBlock()) {
                activeChar.sendPacket(Msg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE);
                return;
            } else if (!ConfigValue.AllowCBInAbnormalState) {
                if (!activeChar.isGM() && (activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || (activeChar.isInCombat() && !command.startsWith("_bbsenchantver")) || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isInVehicle() || activeChar.isFlying() || activeChar.isInFlyingTransform())) {
                    FailBBSManager.getInstance().parsecmd(command, activeChar);
                    return;
                }
            }

            if (activeChar.getEventMaster() != null) {
                String html = activeChar.getEventMaster().getBbsIndex(activeChar, command);
                if (html != null) {
                    ShowBoard.separateAndSend(html, activeChar);
                    return;
                }
            }

            if (command.startsWith("_bbsteleport;")) {
                if (!ConfigValue.AllowCBTeleport) {
                    FailBBSManager.getInstance().parsecmd(command, activeChar);
                    return;
                }
                if (!ConfigValue.AllowCBTInAbnormalState) {
                    if (!activeChar.isGM() && (activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isInVehicle() || activeChar.isFlying() || activeChar.isInFlyingTransform())) {
                        FailBBSManager.getInstance().parsecmd(command, activeChar);
                        return;
                    }
                }
                TeleportBBSManager.getInstance().parsecmd(command, activeChar);
                return;
            }

            ICommunityHandler handler = CommunityHandler.getInstance().getCommunityHandler(command.split(";")[0]);
            if (handler == null)
                handler = CommunityHandler.getInstance().getCommunityHandler(command.split(" ")[0]);
            if (handler == null) {
                String[] args = command.split("_");
				
				/*String ewr = "";
				for(String ads : args)
					ewr+=ads+"+";
				_log.info("args: ["+args[0]+"]["+args[1]+"]["+ewr+"]");*/
                handler = CommunityHandler.getInstance().getCommunityHandler(args[0]);
                if (handler == null && args.length > 1)
                    handler = CommunityHandler.getInstance().getCommunityHandler("_" + args[1]);
            }
            if (handler == null)
                handler = CommunityHandler.getInstance().getCommunityHandler(command.split(":")[0]);
            if (handler != null)
                handler.parsecmd(command, activeChar);

            else if (command.startsWith("_bbsclan"))
                ClanBBSManager.getInstance().parsecmd(command, activeChar);
            else if (command.startsWith("_bbsproject") || command.startsWith("_bbsserver"))
                NewsBBSManager.getInstance().parsecmd(command, activeChar);
            else if (command.startsWith("admin_"))
                AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, command);
            else if (command.startsWith("_bbsmemo"))
                TopicBBSManager.getInstance().parsecmd(command, activeChar);
            else if (command.startsWith("_bbstopics"))
                TopicBBSManager.getInstance().parsecmd(command, activeChar);
            else if (command.startsWith("_bbsposts"))
                PostBBSManager.getInstance().parsecmd(command, activeChar);
            else if (command.startsWith("_bbstop") || command.startsWith("_bbshome"))
                TopBBSManager.getInstance().parsecmd(command, activeChar);
            else if (command.startsWith("_bbsloc"))
                RegionBBSManager.getInstance().parsecmd(command, activeChar);
            else if (command.startsWith("_friend") || command.startsWith("_block"))
                FriendsBBSManager.getInstance().parsecmd(command, activeChar);
            else if (command.startsWith("_bbsgetfav"))
                ShowBoard.separateAndSend("<html><body><br><br><center>Закладки пока не реализованы.</center><br><br></body></html>", activeChar);
            else if (command.startsWith("_maillist"))
                MailBBSManager.getInstance().parsecmd(command, activeChar);
            else if (command.startsWith("_bbsechant")) {
                if (!ConfigValue.AllowCBEnchant) {
                    FailBBSManager.getInstance().parsecmd(command, activeChar);
                    return;
                }

                EnchantBBSManager.getInstance().parsecmd(command, activeChar);
            } else if (command.startsWith("_bbsclass")) {
                if (!ConfigValue.AllowCBClassMaster) {
                    FailBBSManager.getInstance().parsecmd(command, activeChar);
                    return;
                }
                if (!ConfigValue.AllowCBCInAbnormalState) {
                    if (!activeChar.isGM() && (activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isInVehicle() || activeChar.isFlying() || activeChar.isInFlyingTransform())) {
                        FailBBSManager.getInstance().parsecmd(command, activeChar);
                        return;
                    }
                }

                ClassBBSManager.getInstance().parsecmd(command, activeChar);
            } else if (command.startsWith("_bbssms;"))
                SmsBBSManager.getInstance().parsecmd(command, activeChar);
            else if (command.startsWith("_bbsstat;"))
                StatBBSManager.getInstance().parsecmd(command, activeChar);
            else if (command.startsWith("_bbsmultisell;")) {
                if (!ConfigValue.AllowCBMInAbnormalState) {
                    if (!activeChar.isGM() && (activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isInVehicle() || activeChar.isFlying() || activeChar.isInFlyingTransform())) {
                        FailBBSManager.getInstance().parsecmd(command, activeChar);
                        return;
                    }
                }
                StringTokenizer st = new StringTokenizer(command, ";");
                st.nextToken();
                TopBBSManager.getInstance().parsecmd("_bbstop;" + st.nextToken(), activeChar);
                try {
                    L2Multisell.getInstance().SeparateAndSend(Integer.parseInt(st.nextToken()), activeChar, 0);
                } catch (Exception e) {
                    // Иногда хз почему проскакивают ошибки)))
                    // Bad RequestBypassToServer: _bbsmultisell;30-2;10006 Player
                    // Фиг его знает, то ли где-то в коде затупь то ли пытаются через бафера читирить)
                }
            } else if (command.startsWith("_bbsscripts")) {
                if (!ConfigValue.AllowCBSInAbnormalState) {
                    if (!activeChar.isGM() && (activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isInVehicle() || activeChar.isFlying() || activeChar.isInFlyingTransform())) {
                        FailBBSManager.getInstance().parsecmd(command, activeChar);
                        return;
                    }
                }

                StringTokenizer st = new StringTokenizer(command, ";");
                st.nextToken();
                String top = st.nextToken();
                String com = st.nextToken();
                String[] word = com.split("\\s+");
                String[] args = com.substring(word[0].length()).trim().split("\\s+");
                String[] path = word[0].split(":");
                String pBypass = st.hasMoreTokens() ? st.nextToken() : null;

                if (pBypass != null) {
                    handler = CommunityHandler.getInstance().getCommunityHandler(pBypass);
                    if (handler != null) {
                        handler.parsecmd(pBypass, activeChar);
                    }
                } else if (!top.trim().isEmpty())
                    TopBBSManager.getInstance().parsecmd("_bbstop;" + top, activeChar);
                if (path.length != 2) {
                    _log.warning("Bad Script bypass!");
                    return;
                }

                HashMap<String, Object> variables = new HashMap<String, Object>();
                variables.put("npc", null);
                activeChar.callScripts(path[0], path[1], word.length == 1 ? new Object[]{} : new Object[]{args}, variables);
            } else if (command.startsWith("_bbssps;")) //TODO: Удалить после порехода всех клиентов на новое кб.
            {
                int price = 1;
                L2Item item = ItemTemplates.getInstance().getTemplate(4357);
                L2ItemInstance pay = activeChar.getInventory().getItemByItemId(item.getItemId());
                if (pay != null && pay.getCount() >= price) {
                    activeChar.getInventory().destroyItem(pay, price, true);
                    activeChar.setSp(activeChar.getSp() + 10000000);
                    activeChar.sendMessage("Вы получили 10kk SP");
                    activeChar.broadcastPacket2(new SocialAction(activeChar.getObjectId(), 16));
                    activeChar.broadcastUserInfo(true);
                } else
                    activeChar.sendMessage("Недостаточно средств.");
            } else if (command.startsWith("_bbsspa;")) //TODO: Удалить после порехода всех клиентов на новое кб.
            {
                int price = 100000000;
                L2Item item = ItemTemplates.getInstance().getTemplate(57);
                L2ItemInstance pay = activeChar.getInventory().getItemByItemId(item.getItemId());
                if (pay != null && pay.getCount() >= price) {
                    activeChar.getInventory().destroyItem(pay, price, true);
                    activeChar.setSp(activeChar.getSp() + 10000000);
                    activeChar.sendMessage("Вы получили 10kk SP");
                    activeChar.broadcastPacket2(new SocialAction(activeChar.getObjectId(), 16));
                    activeChar.broadcastUserInfo(true);
                } else
                    activeChar.sendMessage("Недостаточно средств.");
            } else if (command.startsWith("_bbsnobles;")) //TODO: Удалить после порехода всех клиентов на новое кб.
            {
                if (!activeChar.isNoble()) {
                    if (!checkCondition(activeChar, 30))
                        return;

                    if (activeChar.getSubLevel() < 75) {
                        activeChar.sendMessage("Чтобы стать дворянином, вы должны прокачать сабкласс до 75-го уровня");
                        return;
                    }

                    L2Item item = ItemTemplates.getInstance().getTemplate(MONEY_ID);
                    L2ItemInstance pay = activeChar.getInventory().getItemByItemId(item.getItemId());
                    if (pay != null && pay.getCount() >= 30) {
                        activeChar.getInventory().destroyItem(pay, 30, true);

                        Olympiad.addNoble(activeChar);
                        activeChar.setNoble(true);
                        activeChar.broadcastPacket2(new SocialAction(activeChar.getObjectId(), 3));
                        activeChar.updatePledgeClass();
                        activeChar.updateNobleSkills();
                        activeChar.sendPacket(new SkillList(activeChar));
                        activeChar.broadcastUserInfo(true);
                    } else
                        activeChar.sendMessage("Недостаточно средств.");
                } else
                    activeChar.sendMessage("Вы уже являетесь дворянином. Операция отменена.");
            } else if (command.equals("_bbslvlup")) //TODO: Удалить после порехода всех клиентов на новое кб.
            {
                String name = "None Name";
                name = ItemTemplates.getInstance().getTemplate(ConfigValue.CBLvlUpItem).getName();
                StringBuilder sb = new StringBuilder();
                sb.append("<html><body><br><br><center>");
                sb.append(new StringBuilder("Поднять Lvl за: <font color=\"LEVEL\">" + name + "</font>"));
                sb.append("<img src=\"l2ui.squaregray\" width=\"170\" height=\"1\">");
                for (int i = 0; i < ConfigValue.CBLvlUp.length; i++) {
                    if (activeChar.getLevel() < ConfigValue.CBLvlUp[i]) {
                        sb.append(new StringBuilder("<button value=\"Поднять Lvl до: " + ConfigValue.CBLvlUp[i] + " (Цена:" + ConfigValue.CBLvlUpPrice[i] + " " + name + ") \" action=\"bypass -h _bbslvlup;up;" + ConfigValue.CBLvlUp[i] + ";" + ConfigValue.CBLvlUpPrice[i] + "\" width=300 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
                        sb.append("<br1>");
                    }
                }
                sb.append("</center><br><br></body></html>");
                String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "805.htm", activeChar);
                content = content.replace("%lvlup%", sb.toString());
                ShowBoard.separateAndSend(content, activeChar);
            } else if (command.startsWith("_bbslvlup;up;")) //TODO: Удалить после порехода всех клиентов на новое кб.
            {
                StringTokenizer st = new StringTokenizer(command, ";");
                st.nextToken();
                st.nextToken();
                int level = Integer.parseInt(st.nextToken());
                int count = Integer.parseInt(st.nextToken());

                L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.CBLvlUpItem);
                L2ItemInstance pay = activeChar.getInventory().getItemByItemId(item.getItemId());
                if (pay != null && pay.getCount() >= count) {
                    activeChar.getInventory().destroyItem(pay, count, true);
                    setLevel(activeChar, level);
                } else
                    activeChar.sendPacket(Msg.INCORRECT_ITEM_COUNT);
            } else if (command.equals("_bbshero")) //TODO: Удалить после порехода всех клиентов на новое кб.
            {
                String name = "";
                for (int i = 0; i < ConfigValue.CBHeroItem.length; i++) {
                    name += ItemTemplates.getInstance().getTemplate(ConfigValue.CBHeroItem[i]).getName();
                    if (i + 1 < ConfigValue.CBHeroItem.length)
                        name += ", ";
                }
                StringBuilder sb = new StringBuilder();
                sb.append("<html><body><br><br><center>");
                sb.append("Купить статус Героя за: <font color=\"LEVEL\">" + name + "</font>");
                sb.append("<img src=\"l2ui.squaregray\" width=\"170\" height=\"1\">");
                for (int i = 0; i < ConfigValue.CBHeroItem.length; i++)
                    sb.append(new StringBuilder("<button value=\"Купить статус Героя за: " + ConfigValue.CBHeroItemPrice[i] + " " + ItemTemplates.getInstance().getTemplate(ConfigValue.CBHeroItem[i]).getName() + " \" action=\"bypass -h _bbshero;set;" + i + "\" width=300 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\"><br1>"));
                sb.append("</center><br><br></body></html>");
                String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "806.htm", activeChar);
                content = content.replace("%sethero%", sb.toString());
                ShowBoard.separateAndSend(content, activeChar);
            } else if (command.startsWith("_bbshero;set;")) //TODO: Удалить после порехода всех клиентов на новое кб.
            {
                StringTokenizer st = new StringTokenizer(command, ";");
                st.nextToken();
                st.nextToken();
                int time = Integer.parseInt(st.nextToken());
                int count = ConfigValue.CBHeroItemPrice[time];

                L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.CBHeroItem[time]);
                L2ItemInstance pay = activeChar.getInventory().getItemByItemId(item.getItemId());
                if (pay != null && pay.getCount() >= count) {
                    activeChar.getInventory().destroyItem(pay, count, true);
                    StatsSet hero = new StatsSet();
                    hero.set("class_id", activeChar.getBaseClassId());
                    hero.set("char_id", activeChar.getObjectId());
                    hero.set("char_name", activeChar.getName());

                    //GArray<StatsSet> heroesToBe = new GArray<StatsSet>();
                    //heroesToBe.add(hero);
                    //Hero.getInstance().computeNewHeroes(heroesToBe);

                    activeChar.setHero(true, 2);
                    long expire = System.currentTimeMillis() + (1000 * 60 * 60 * 24 * ConfigValue.CBHeroTime[time]);
                    activeChar.setVar("HeroPremium", String.valueOf(expire), expire);

                    if (ConfigValue.PremiumHeroSetSkill)
                        Hero.addSkills(activeChar);
                    activeChar.sendPacket(new SkillList(activeChar));
                    if (activeChar.isHero())
                        activeChar.broadcastPacket2(new SocialAction(activeChar.getObjectId(), 16));
                    activeChar.broadcastUserInfo(true);
                    activeChar._heroTask = ThreadPoolManager.getInstance().schedule(new L2ObjectTasks.UnsetHero(activeChar, 2), 1000 * 60 * 60 * 24 * ConfigValue.CBHeroTime[time]);
                } else
                    activeChar.sendPacket(Msg.INCORRECT_ITEM_COUNT);
            } else if (command.startsWith("_marketpage;"))
                MarcketBBSManager.getInstance().parsecmd(command, activeChar);
            else
                ShowBoard.separateAndSend("<html><body><br><br><center>Функция: " + command + "(" + command.split(";")[0] + ") пока не реализована</center><br><br></body></html>", activeChar);
        } catch (Exception e) {
            _log.log(Level.WARNING, "Error community: command='" + command + "' Player='" + activeChar.getName() + "':");
            if (command.endsWith(" Player"))
                botPunishment(activeChar, command);
            else
                e.printStackTrace();
        }
    }

    private void setLevel(L2Player activeChar, int level) {
        Long exp_add = Experience.LEVEL[level] - activeChar.getExp();
        activeChar.addExpAndSp(exp_add, 0, false, false);
    }

    public static boolean checkCondition(L2Player activeChar, int CoinCount) {
        synchronized (activeChar) {
            L2ItemInstance Coin = activeChar.getInventory().getItemByItemId(MONEY_ID);

            if (activeChar.isSitting())
                return false;
            if (Coin.getCount() < CoinCount) {
                activeChar.sendMessage("Недостаточно средств.");
                return false;
            }
            return true;
        }
    }

    public void handleWriteCommands(L2GameClient client, String url, String arg1, String arg2, String arg3, String arg4, String arg5) {
        L2Player activeChar = client.getActiveChar();
        if (activeChar == null)
            return;
        if (!ConfigValue.AllowCommunityBoard) {
            activeChar.sendPacket(Msg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE);
            return;
        }

        Log.add(activeChar.getName() + ": url='" + url + "' arg1='" + arg1 + "' arg2='" + arg2 + "' arg3='" + arg3 + "' arg4='" + arg4 + "' arg5='" + arg5 + "'", "handle_write_commands");
        if (url.equals("Topic"))
            TopicBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
        else if (url.equals("Post"))
            PostBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
        else if (url.equals("Region"))
            RegionBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
        else if (url.equals("Notice")) {
            if (arg4.length() > 512) {
                ShowBoard.separateAndSend("<html><body><br><br><center>Вы ввели слишком длинное сообщение, оно будет сохранено не полностью.</center><br><br></body></html>", activeChar);
                return;
            }
            ClanBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
        } else if (url.equals("Mail"))
            MailBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
        else
            ShowBoard.separateAndSend("<html><body><br><br><center>Функция: " + url + " пока не реализована</center><br><br></body></html>", activeChar);
    }

    public void botPunishment(L2Player activeChar, String text) {
        switch (ConfigValue.CommunityFailPunishment) {
            case 0: // Тюрьма
                Log.addBot("Char: " + activeChar.getName() + ", Punishment: Jail after " + ConfigValue.CommunityFailPunishmentTime + " min: '" + text + "'", "other", "community_bot");
                activeChar.setVar("jailedFrom", activeChar.getX() + ";" + activeChar.getY() + ";" + activeChar.getZ() + ";" + activeChar.getReflection().getId());
                activeChar._unjailTask = ThreadPoolManager.getInstance().schedule(new L2ObjectTasks.TeleportTask(activeChar, new Location(activeChar.getX(), activeChar.getY(), activeChar.getZ()), 0), ConfigValue.CommunityFailPunishmentTime * 60000);
                activeChar.setVar("jailed", ConfigValue.CommunityFailPunishmentTime * 60000 + ";" + (System.currentTimeMillis() / 1000));
                activeChar.teleToLocation(-114648, -249384, -2984, -3);
                break;
            case 1: // Тюрьма + Кик
                Log.addBot("Char: " + activeChar.getName() + ", Punishment: Kick + Jail after " + ConfigValue.CommunityFailPunishmentTime + " min: '" + text + "'", "other", "community_bot");
                activeChar.setVar("jailedFrom", activeChar.getX() + ";" + activeChar.getY() + ";" + activeChar.getZ() + ";" + activeChar.getReflection().getId());
                activeChar._unjailTask = ThreadPoolManager.getInstance().schedule(new L2ObjectTasks.TeleportTask(activeChar, new Location(activeChar.getX(), activeChar.getY(), activeChar.getZ()), 0), ConfigValue.CommunityFailPunishmentTime * 60000);
                activeChar.setVar("jailed", ConfigValue.CommunityFailPunishmentTime * 60000 + ";" + (System.currentTimeMillis() / 1000));
                activeChar.teleToLocation(-114648, -249384, -2984, -3);
                activeChar.logout(false, false, true, true);
                break;
            case 2: // Бан
                activeChar.teleToLocation(83432, 148712, -3408);
                Log.addBot("Char: " + activeChar.getName() + ", Punishment: Ban after " + ConfigValue.CommunityFailPunishmentTime + " day: '" + text + "'", "other", "community_bot");
                activeChar.setAccessLevel(-100);
                AutoBan.Banned(activeChar, ConfigValue.CommunityFailPunishmentTime, "Fail Bypass", "ByPassFail");
                activeChar.logout(false, false, true, true);
                break;
            case 3: // Просто Кик
                activeChar.teleToLocation(83432, 148712, -3408);
                Log.addBot("Char: " + activeChar.getName() + ", Punishment: Kick: '" + text + "'", "other", "community_bot");
                activeChar.logout(false, false, true, true);
                break;
            case 4: // Разрешаем ити дальше...
                break;
            default:
                activeChar.teleToLocation(83432, 148712, -3408);
                // Ничего не делаем, просто игнорим...
                Log.addBot("Char: " + activeChar.getName() + ", Punishment: None: '" + text + "'", "other", "community_bot");
                break;
        }
    }
}