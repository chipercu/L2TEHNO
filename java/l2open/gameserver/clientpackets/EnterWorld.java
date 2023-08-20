package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.database.mysql;
import l2open.extensions.scripts.Scripts;
import l2open.extensions.scripts.Scripts.ScriptClassAndMethod;
import l2open.gameserver.Announcements;
import l2open.gameserver.GameStart;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.instancemanager.CoupleManager;
import l2open.gameserver.instancemanager.PlayerMessageStack;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.gameserver.model.entity.vehicle.L2Ship;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.serverpackets.ConfirmDlg;
import l2open.gameserver.serverpackets.ExAcquirableSkillListByClass;
import l2open.gameserver.serverpackets.ExAdenaInvenCount;
import l2open.gameserver.serverpackets.ExBR_NewIConCashBtnWnd;
import l2open.gameserver.serverpackets.ExCastleState;
import l2open.gameserver.serverpackets.ExChangeMPCost;
import l2open.gameserver.serverpackets.ExPledgeWaitingListAlarm;
import l2open.gameserver.serverpackets.ExSubjobInfo;
import l2open.gameserver.serverpackets.ExVitalityEffectInfo;
import l2open.gameserver.serverpackets.ExWaitWaitingSubStituteInfo;
import l2open.gameserver.serverpackets.FriendStatus;
import l2open.gameserver.serverpackets.custom.ConfigPacket;
import l2open.gameserver.serverpackets.custom.KeyPacket;
import l2open.gameserver.skills.AbnormalVisualEffect;
import l2open.gameserver.tables.FriendsTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.Log;
import l2open.util.Files;
import l2open.util.Util;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.model.entity.residence.Castle;

/**
 * Format: S
 * Format: bddddbdcccccccccccccccccccc
 */
public class EnterWorld extends L2GameClientPacket {
    private static final Object _lock = new Object();

    private static Logger _log = Logger.getLogger(EnterWorld.class.getName());

    @Override
    public void readImpl() {
        //readS(); - клиент всегда отправляет строку "narcasse"
    }

    @Override
    public void runImpl() {
        L2GameClient client = getClient();
        L2Player activeChar = client.getActiveChar();

        if (activeChar == null) {
            client.closeNow(false);
            return;
        }

        if (System.currentTimeMillis() - activeChar.getLastEnterWorldPacket() < ConfigValue.EnterWorldPacketDelay) {
            client.closeNow(false);
            return;
        }
        activeChar.setLastEnterWorldPacket();

        if (ConfigValue.EnableCustomInterface) {
//            activeChar.sendPacket(new Interface.impl.KeyPacket());
//            activeChar.sendPacket(new Interface.impl.ConfigPacket());
            activeChar.sendPacket(new KeyPacket());
            activeChar.sendPacket(new ConfigPacket());
        }

        if (getClient().isLindvior()) {
            for (Castle c : CastleManager.getInstance().getCastles().values())
                activeChar.sendPacket(new ExCastleState(c));
        }
        sendPacket(new ChangeMoveType(activeChar)); // DELL
        activeChar.setRangPoint();
        activeChar.setIsInvul(false); // Вдруг заклинило:)
        activeChar._visual_enchant_level_test = -1;

        boolean first = activeChar.entering;
        if (first) {
            if (activeChar.getPlayerAccess().GodMode && !ConfigValue.ShowGMLogin)
                activeChar.setInvisible(true);

            activeChar.setNonAggroTime(Long.MAX_VALUE);
            if (activeChar.getClan() != null) {
                if (activeChar.getClan().isAttacker())
                    activeChar.setSiegeState(1);
                else if (activeChar.getClan().isDefender())
                    activeChar.setSiegeState(2);
            }
            activeChar.spawnMe();
            //activeChar.setRunning();
            activeChar.getInventory().restoreCursedWeapon();
        } else if (activeChar.isTeleporting())
            activeChar.onTeleported();
        activeChar.startRegeneration(-2);

        activeChar.getMacroses().sendUpdate();
        sendPacket(new SSQInfo());
        sendPacket(new HennaInfo(activeChar));
        sendPacket(new ItemList(activeChar, false));
        sendPacket(new ShortCutInit(activeChar), new SkillList(activeChar), Msg.WELCOME);
        if (getClient().isLindvior()) {
            sendPacket(new ExAdenaInvenCount(activeChar));
            sendPacket(new SkillCoolTime(activeChar));
            for (Castle c : CastleManager.getInstance().getCastles().values())
                activeChar.sendPacket(new ExCastleState(c));
            activeChar.sendPacket(new ExBR_NewIConCashBtnWnd(), new ExPledgeWaitingListAlarm()); // DELL ExPledgeWaitingListAlarm
        }

        Announcements.getInstance().showAnnouncements(activeChar);

        //add char to online characters
        activeChar.setOnlineStatus(true);

        // Вызов всех хэндлеров, определенных в скриптах
        if (first) {
            Object[] script_args = new Object[]{activeChar};
            for (ScriptClassAndMethod handler : Scripts.onPlayerEnter)
                activeChar.callScripts(handler.scriptClass, handler.method, script_args);
        } else if (ConfigValue.VidakSystem)
            activeChar.callScripts("vidak.VidakService", "OnPlayerEnter", new Object[]{activeChar});

        if (first)
            activeChar.getListeners().onEnter();

        SevenSigns.getInstance().sendCurrentPeriodMsg(activeChar);

        if (first && activeChar.getCreateTime() > 0) {
            Calendar create = Calendar.getInstance();
            create.setTimeInMillis(activeChar.getCreateTime());
            Calendar now = Calendar.getInstance();

            int day = create.get(Calendar.DAY_OF_MONTH);
            if (create.get(Calendar.MONTH) == Calendar.FEBRUARY && day == 29)
                day = 28;

            int myBirthdayReceiveYear = activeChar.getVarInt("MyBirthdayReceiveYear", 0);
            if (create.get(Calendar.MONTH) == now.get(Calendar.MONTH) && create.get(Calendar.DAY_OF_MONTH) == day) {
                if ((myBirthdayReceiveYear == 0 && create.get(Calendar.YEAR) != now.get(Calendar.YEAR)) || myBirthdayReceiveYear > 0 && myBirthdayReceiveYear != now.get(Calendar.YEAR)) {
                    MailParcelController.Letter mail = new MailParcelController.Letter();
                    mail.senderId = 1;
                    mail.senderName = "Алегрия";
                    mail.receiverId = activeChar.getObjectId();
                    mail.receiverName = activeChar.getName();
                    mail.topic = "С днем рождения!";
                    mail.body = "Привет путник!! Вижу ты стал старше на год, потому я все думала поздравить тебя :) Пожалуйста, возьми этот коробок, прикрепленный к письму. Пусть эти подарки принесут тебе радость и счастье в этот особый день. \\nС признательностью, Алегрия.";
                    mail.price = 0;
                    mail.unread = 1;
                    mail.system = 0;
                    mail.hideSender = 2;
                    mail.validtime = 720 * 3600 + (int) (System.currentTimeMillis() / 1000L);
                    L2ItemInstance reward1 = ItemTemplates.getInstance().createItem(22187);
                    reward1.setCount(1);

                    GArray<L2ItemInstance> attachments = new GArray<L2ItemInstance>();
                    attachments.add(reward1);
                    MailParcelController.getInstance().sendLetter(mail, attachments);

                    activeChar.setVar("MyBirthdayReceiveYear", String.valueOf(now.get(Calendar.YEAR)), -1);
                }
            }
        }

        if (activeChar.getClan() != null) {
            notifyClanMembers(activeChar);
            sendPacket(new PledgeShowMemberListAll(activeChar.getClan(), activeChar), new PledgeShowInfoUpdate(activeChar.getClan()), new PledgeSkillList(activeChar.getClan()));
            activeChar.getClan().showSquadSkillsToPlayer(activeChar);
        }

        // engage and notify Partner
        if (first && ConfigValue.AllowWedding) {
            CoupleManager.getInstance().engage(activeChar);
            CoupleManager.getInstance().notifyPartner(activeChar);
        }

        Log.LogChar(activeChar, Log.EnterWorld, "");

        if (first) {
            notifyFriends(activeChar, true);
            if (ConfigValue.TutorialQuestEnable)
                loadTutorial(activeChar);
            PlayerData.getInstance().restoreDisableSkills(activeChar);
        }
        sendPacket(new L2FriendList(activeChar, false), new ExStorageMaxCount(activeChar), new QuestList(activeChar), new ExBasicActionList(activeChar), new EtcStatusUpdate(activeChar));

        // refresh player info
        //activeChar.EtcStatusUpdate();
        if (ConfigValue.AltPcBangPointsEnabled)
            sendPacket(new ExPCCafePointInfo(activeChar.getPcBangPoints(), 0, 1, 2, 12));

        if (ConfigValue.EnableNevitBonus) {
            sendPacket(new ExVoteSystemInfo(activeChar));
            sendPacket(new ExNavitAdventPointInfoPacket(activeChar.getNevitBlessing().getPoints()));
            sendPacket(new ExNavitAdventTimeChange(activeChar.getNevitBlessing().getBonusTime() * 60, false));
        }

        if (!activeChar.getPremiumItemList().isEmpty())
            sendPacket(ConfigValue.GoodsInventoryEnabled ? ExGoodsInventoryChangedNotiPacket.STATIC : ExNotifyPremiumItem.STATIC);

        if (getClient().getBonus() > 1)
            sendPacket(new ExBrPremiumState(activeChar, 1));

        if (activeChar.getVarB("PremiumStart") && ConfigValue.StartPremiumType == 0) {
            activeChar.getNetConnection().setBonus(ConfigValue.StartPremiumRate[0]);
            activeChar.getNetConnection().setBonusExpire(System.currentTimeMillis() / 1000 + ((int) ConfigValue.StartPremiumRate[1] * 24 * 60 * 60));
            activeChar.restoreBonus();
            //activeChar.saveBonus();
            activeChar.sendPacket(new ExBrPremiumState(activeChar, 1));
            activeChar.unsetVar("PremiumStart");
        }

        activeChar.checkHpMessages(activeChar.getMaxHp(), activeChar.getCurrentHp());
        activeChar.checkDayNightMessages();

        //if(activeChar.getPet() != null && (activeChar.getPet().isSummon() || ConfigValue.ImprovedPetsLimitedUse && (activeChar.getPet().getNpcId() == PetDataTable.IMPROVED_BABY_KOOKABURRA_ID && !activeChar.isMageClass() || activeChar.getPet().getNpcId() == PetDataTable.IMPROVED_BABY_BUFFALO_ID && activeChar.isMageClass())))
        //	_log.info("Put UnSummon...");//	activeChar.getPet().unSummon();

        if (!first) {
            if (activeChar.isCastingNow()) {
                L2Character castingTarget = activeChar.getCastingTarget();
                L2Skill castingSkill = activeChar.getCastingSkill();
                long animationEndTime = activeChar.getAnimationEndTime();
                if (castingSkill != null && castingTarget != null && castingTarget.isCharacter() && activeChar.getAnimationEndTime() > 0)
                    sendPacket(new MagicSkillUse(activeChar, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0));
            }

            if (activeChar.isInVehicle() && !activeChar.getVehicle().isClanAirShip())
                if (activeChar.getVehicle().isAirShip())
                    sendPacket(new ExGetOnAirShip(activeChar, (L2AirShip) activeChar.getVehicle(), activeChar.getInVehiclePosition()));
                else
                    sendPacket(new GetOnVehicle(activeChar, (L2Ship) activeChar.getVehicle(), activeChar.getInVehiclePosition()));

            if (activeChar.isMoving || activeChar.isFollow)
                sendPacket(new CharMoveToLocation(activeChar, activeChar.getZ(), false));

            if (activeChar.getMountNpcId() != 0)
                sendPacket(new Ride(activeChar));
        }

        activeChar.entering = false;
        activeChar.sendUserInfo(true);

        if (activeChar.isSitting())
            activeChar.sendPacket(new ChangeWaitType(activeChar, ChangeWaitType.WT_SITTING));
        if (activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE) {
            if (activeChar.getPrivateStoreType() == L2Player.STORE_PRIVATE_BUY)
                sendPacket(new PrivateStoreMsgBuy(activeChar));
            else if (activeChar.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL)
                sendPacket(new PrivateStoreMsgSell(activeChar, false));
            else if (activeChar.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL_PACKAGE)
                sendPacket(new PrivateStoreMsgSell(activeChar, true));
            else if (activeChar.getPrivateStoreType() == L2Player.STORE_PRIVATE_MANUFACTURE)
                sendPacket(new RecipeShopMsg(activeChar));
        }

        if (activeChar.isDead())
            sendPacket(new Die(activeChar));

        activeChar.unsetVar("offline");

        // на всякий случай
        activeChar.sendActionFailed();

        if (first && activeChar.isGM() && ConfigValue.SaveGMEffects && activeChar.getPlayerAccess().CanUseGMCommand) {
            //silence
            if (activeChar.getVarB("gm_silence")) {
                activeChar.setMessageRefusal(true);
                activeChar.sendPacket(Msg.MESSAGE_REFUSAL_MODE);
            }
            //invul
            if (activeChar.getVarB("gm_invul")) {
                activeChar.setIsInvul(true);
                activeChar.startAbnormalEffect(AbnormalVisualEffect.ave_invincibility);
                activeChar.sendMessage(activeChar.getName() + " is now immortal.");
            }
            //gmspeed
            try {
                int var_gmspeed = Integer.parseInt(activeChar.getVar("gm_gmspeed"));
                if (var_gmspeed >= 1 && var_gmspeed <= 4)
                    activeChar.doCast(SkillTable.getInstance().getInfo(7029, var_gmspeed), activeChar, true);
            } catch (Exception E) {
            }
        }

        PlayerMessageStack.getInstance().CheckMessages(activeChar);

        sendPacket(new ClientSetTime(), new ExSetCompassZoneCode(activeChar));
        checkNewMail(activeChar);

        if (activeChar.isReviveRequested())
            sendPacket(new ConfirmDlg(SystemMessage.S1_IS_MAKING_AN_ATTEMPT_AT_RESURRECTION_WITH_$S2_EXPERIENCE_POINTS_DO_YOU_WANT_TO_CONTINUE_WITH_THIS_RESURRECTION, 0, 2).addString("Other player").addString("some"));

        if (!first) {
            if (activeChar.getCurrentRegion() != null)
                for (L2WorldRegion neighbor : activeChar.getCurrentRegion().getNeighbors())
                    neighbor.showObjectsToPlayer(activeChar, false);

            if (activeChar.getPet() != null)
                sendPacket(new PetInfo(activeChar.getPet()));

            if (activeChar.isInParty()) {
                L2Summon member_pet;
                //sends new member party window for all members
                //we do all actions before adding member to a list, this speeds things up a little
                sendPacket(new PartySmallWindowAll(activeChar.getParty(), activeChar));

                for (L2Player member : activeChar.getParty().getPartyMembers())
                    if (member != activeChar) {
                        sendPacket(new PartySpelled(member, true));
                        if ((member_pet = member.getPet()) != null)
                            sendPacket(new PartySpelled(member_pet, true));
                        sendPackets(RelationChanged.update(activeChar, member, activeChar));
                    }

                // Если партия уже в СС, то вновь прибывшем посылаем пакет открытия окна СС
                if (activeChar.getParty().isInCommandChannel())
                    sendPacket(Msg.ExMPCCOpen);
            }

            for (int shotId : activeChar.getAutoSoulShot())
                sendPacket(new ExAutoSoulShot(shotId, true));

            for (L2Effect e : activeChar.getEffectList().getAllFirstEffects())
                if (e.getSkill().isToggle())
                    sendPacket(new MagicSkillLaunched(activeChar.getObjectId(), e.getSkill().getId(), e.getSkill().getLevel(), activeChar, e.getSkill().isOffensive()));

            if (activeChar.getPet() != null && activeChar.isMounted()) {
                //_log.info("Mount Okey...");
                activeChar.broadcastPacket(new Ride(activeChar));
            }
            activeChar.broadcastUserInfo(false);
        } else
            activeChar.sendUserInfo(false); // Отобразит права в клане

        sendPacket(new ExReceiveShowPostFriend(activeChar));

        if (getClient().isLindvior()) {
            activeChar.sendPacket(new ExSubjobInfo(activeChar.getPlayer(), false));
            activeChar.sendPacket(new ExVitalityEffectInfo(activeChar));
            activeChar.sendPacket(new ExTutorialList());
            activeChar.sendPacket(new ExAcquirableSkillListByClass(activeChar));
            activeChar.sendPacket(new ExWaitWaitingSubStituteInfo(true));
            activeChar.sendPacket(new ExChangeMPCost(1, -3));
            activeChar.sendPacket(new ExChangeMPCost(1, -5));
            activeChar.sendPacket(new ExChangeMPCost(0, 20));
            activeChar.sendPacket(new ExChangeMPCost(1, -10));
            activeChar.sendPacket(new ExChangeMPCost(3, -20));
            activeChar.sendPacket(new ExChangeMPCost(22, -20));
            activeChar.sendPacket(new ExWaitWaitingSubStituteInfo(ExWaitWaitingSubStituteInfo.WAITING_CANCEL));
        }


        if (activeChar.getTerritorySiege() > -1)
            activeChar.sendPacket(new ExDominionWarStart(activeChar));
        //if(getClient().getBonus() < 0)
        //	activeChar.callScripts("services.Activation", "activation_page");

        activeChar.setLogout(false);
        activeChar.startPcBangPointsTask();

        if (ConfigValue.CharacterEnter350q) {
            Quest q = QuestManager.getQuest("_350_EnhanceYourWeapon");
            QuestState qs = q.newQuestState(activeChar, Quest.STARTED);
            qs.setCond(1);
        }
        if (first)
            if (TerritorySiege.isInProgress())
                if (activeChar.getTerritorySiege() != -1) {
                    QuestState sakeQuestState = TerritorySiege.getForSakeQuest(activeChar.getTerritorySiege()).newQuestState(activeChar, Quest.CREATED);
                    sakeQuestState.setState(Quest.STARTED);
                    sakeQuestState.setCond(1);

                    if (TerritorySiege.protectObjectAtacked[activeChar.getTerritorySiege() - 1][0]) {
                        Quest q = QuestManager.getQuest("_729_ProtectTheTerritoryCatapult");
                        QuestState questState2 = q.newQuestStateAndNotSave(activeChar, Quest.CREATED);
                        questState2.setCond(1, false);
                        questState2.setStateAndNotSave(Quest.STARTED);
                    }
                    if (TerritorySiege.protectObjectAtacked[activeChar.getTerritorySiege() - 1][1]) {
                        Quest q = QuestManager.getQuest("_733_ProtectTheEconomicAssociationLeader");
                        QuestState questState2 = q.newQuestStateAndNotSave(activeChar, Quest.CREATED);
                        questState2.setCond(1, false);
                        questState2.setStateAndNotSave(Quest.STARTED);
                    }
                    if (TerritorySiege.protectObjectAtacked[activeChar.getTerritorySiege() - 1][2]) {
                        Quest q = QuestManager.getQuest("_731_ProtectTheMilitaryAssociationLeader");
                        QuestState questState2 = q.newQuestStateAndNotSave(activeChar, Quest.CREATED);
                        questState2.setCond(1, false);
                        questState2.setStateAndNotSave(Quest.STARTED);
                    }
                    if (TerritorySiege.protectObjectAtacked[activeChar.getTerritorySiege() - 1][3]) {
                        Quest q = QuestManager.getQuest("_732_ProtectTheReligiousAssociationLeader");
                        QuestState questState2 = q.newQuestStateAndNotSave(activeChar, Quest.CREATED);
                        questState2.setCond(1, false);
                        questState2.setStateAndNotSave(Quest.STARTED);
                    }
                    if (TerritorySiege.protectObjectAtacked[activeChar.getTerritorySiege() - 1][4]) {
                        Quest q = QuestManager.getQuest("_730_ProtectTheSuppliesSafe");
                        QuestState questState2 = q.newQuestStateAndNotSave(activeChar, Quest.CREATED);
                        questState2.setCond(1, false);
                        questState2.setStateAndNotSave(Quest.STARTED);
                    }
                }
        if (!ConfigValue.VidakSystem && ConfigValue.ShowHTMLWelcome && activeChar.getClan() == null) {
            String welcomePath = "./data/html/welcome.htm";
            File mainText = new File(ConfigValue.DatapackRoot, welcomePath); // Return the pathfile of the HTML file
            if (mainText.exists())
                sendPacket(new NpcHtmlMessage(1).setFile(welcomePath));
        }
        Util.setMaxOnline(activeChar);
        if (ConfigValue.MultiHwidSystem && activeChar.is_block) {
            NpcHtmlMessage block_msg = new NpcHtmlMessage(5);
            block_msg.setHtml(Files.read("./data/scripts/services/hwid_confirm.htm", activeChar).replace("<?question?>", activeChar.l2question));
            activeChar.sendPacket(block_msg);
        }
        if (ConfigValue.Enable2Pass) {
            PlayerData.getInstance().select_2pass_and_answer(activeChar);
            activeChar.is_block = true;
            activeChar.i_ai3 = 46534;
            NpcHtmlMessage block_msg = new NpcHtmlMessage(5);
            if (activeChar.password.isEmpty())
                block_msg.setHtml(Files.read("./data/scripts/services/pass_new.htm", activeChar));
            else
                block_msg.setHtml(Files.read("./data/scripts/services/pass_confirm.htm", activeChar).replace("<?question?>", activeChar.l2question));
            activeChar.sendPacket(block_msg);
        }
        activeChar.updateEffectIcons();

        //activeChar.sendPacket(new ExShowUsmVideo(ExShowUsmVideo.GD1_INTRO));
        // где-то не хватает, хз где(((
        //activeChar.sendActionFailed();

        if (activeChar.getAttainment() != null)
            activeChar.getAttainment().enter_world(first);

        //activeChar.sendPacket(new ServerToClientCommunicationPacket("La2Com.ru"));

        if (activeChar.isGM())
            checkItem(activeChar);
    }

    public static void notifyFriends(L2Player cha, boolean login) {
        try {
            for (Integer friend_id : FriendsTable.getInstance().getFriendsList(cha.getObjectId())) {
                L2Player friend = L2ObjectsStorage.getPlayer(friend_id);
                if (friend != null)
                    if (login) {
                        friend.sendPacket(new SystemMessage(SystemMessage.S1_FRIEND_HAS_LOGGED_IN).addString(cha.getName()));
                        if (friend.isLindvior())
                            friend.sendPacket(new FriendStatus(cha, true));
                        else
                            friend.sendPacket(new L2FriendStatus(cha, true));
                    } else {
                        if (friend.isLindvior())
                            friend.sendPacket(new FriendStatus(cha, false));
                        else
                            friend.sendPacket(new L2FriendStatus(cha, false));
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param activeChar
     */
    private void notifyClanMembers(L2Player activeChar) {
        L2Clan clan = activeChar.getClan();
        if (clan == null || clan.getClanMember(activeChar.getObjectId()) == null)
            return;

        clan.getClanMember(activeChar.getObjectId()).setPlayerInstance(activeChar, false);
        //if(activeChar.isClanLeader())
        //{
        //	if(activeChar.getClan().getHasHideout() != 0 && ClanHallManager.getInstance().getClanHall(activeChar.getClan().getHasHideout()).getNotPaid())
        //		activeChar.sendPacket(Msg.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED);
        //}

        int sponsor = activeChar.getSponsor();
        int apprentice = activeChar.getApprentice();
        SystemMessage msg = new SystemMessage(SystemMessage.CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME).addString(activeChar.getName());
        PledgeShowMemberListUpdate memberUpdate = new PledgeShowMemberListUpdate(activeChar);
        for (L2Player clanMember : clan.getOnlineMembers(activeChar.getObjectId()))
            if (clanMember.getObjectId() == sponsor)
                clanMember.sendPacket(memberUpdate, new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_APPRENTICE_HAS_LOGGED_IN).addString(activeChar.getName()));
            else if (clanMember.getObjectId() == apprentice)
                clanMember.sendPacket(memberUpdate, new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_SPONSOR_HAS_LOGGED_IN).addString(activeChar.getName()));
            else
                clanMember.sendPacket(memberUpdate, msg);

        if (!activeChar.isBlocked() && PlayerData.getInstance().isNoticeEnabled(clan) && !PlayerData.getInstance().getNotice(clan).isEmpty()) {
            NpcHtmlMessage notice = new NpcHtmlMessage(5);
            notice.have_encode = false; // фикс есть нормальный, но пускай будет затычка на всякий случай...
            notice.setHtml("<html><body><center><font color=\"LEVEL\">" + activeChar.getClan().getName() + " Clan Notice</font></center><br>" + PlayerData.getInstance().getNotice(activeChar.getClan()) + "</body></html>");
            sendPacket(notice);
        }
    }

    private void loadTutorial(L2Player player) {
        Quest q = QuestManager.getQuest(255);
        if (q != null)
            player.processQuestEvent(q.getName(), "UC", null);
    }

    private void checkNewMail(L2Player activeChar) {
        if (mysql.simple_get_int("messageId", "mail", "unread AND receiver=" + activeChar.getObjectId()) > 0)
            sendPacket(new ExNoticePostArrived(0));
    }

    private void checkItem(L2Player activeChar) {
        for (long[] items : ConfigValue.AnnounceToGmMaxItem) {
            List<GameStart.LogItemInfo> list = GameStart.get_item_count((int) items[0], items[1]);
            for (GameStart.LogItemInfo log_info : list) {
                L2Player pl = L2ObjectsStorage.getPlayer(log_info.owner_id);
                activeChar.sendGMMessage("Внимание, у Игрока" + (pl == null ? "" : " '" + pl.getName() + "'") + "[" + log_info.owner_id + "] Превышен лимит предмета " + DifferentMethods.getItemName(log_info.item_id) + "[" + log_info.item_id + "] в количестве[" + log_info.item_count + "].");
            }
            if (ConfigValue.AnnounceToGmMaxItem.length > 1)
                activeChar.sendGMMessage("-------------------------------");
        }
    }
}