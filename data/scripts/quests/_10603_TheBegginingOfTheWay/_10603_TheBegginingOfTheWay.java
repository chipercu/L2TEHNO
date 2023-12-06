package quests._10603_TheBegginingOfTheWay;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2SkillLearn;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.RadarControl;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Files;
import l2open.util.GArray;


public class _10603_TheBegginingOfTheWay extends Quest implements ScriptFile, TheBegginingOfTheWayConstants {

    public _10603_TheBegginingOfTheWay() {
        super(PARTY_NONE);
        addStartNpc(StartNewbieGuide);
        addTalkId(StartNewbieGuide, TalkingGrandMaster, TalkingWeaponMerchant, TalkingArmorMerchant, TalkingJewelMerchant,
                TalkingGrocerMerchant, TalkingGuard, TalkingFighterSchoolGrandMaster, TalkingFizSchoolInstructor,
                TalkingMagSchoolGrandMaster, TalkingMagSchoolInstructor, GludioGrandMaster);
        addAttackId(TalkingSchoolManichean);
        addKillId(Monster1, Monster2, Monster3);
        addQuestItem(MonsterItem);
    }

    public static L2NpcInstance getNpcByID(int id) {
        return L2ObjectsStorage.getByNpcId(id);
    }

    public static void addRadarToNpc(L2Player player, L2NpcInstance npc) {
        player.sendPacket(new RadarControl(2, 1, npc.getLoc()));// Убираем флажок на карте и стрелку на компасе
        player.sendPacket(new RadarControl(0, 2, npc.getLoc()));// Ставим флажок на карте и стрелку на компасе
        //player.radar.addMarker(npc.getX(), npc.getY(), npc.getZ());
    }

    public static String fileRead(String htm, L2Player player, L2NpcInstance npc) {
        String text = Files.read(HTML_ROOT + htm, player);
        text = text.replace("<?NPC?>", "<font color=\"0099FF\">" + npc.getName() + "</font>");
        text = text.replace("[button]", "<button action=\"bypass -h Quest _10603_TheBegginingOfTheWay addRadarToNpc " + player + " " + npc + "\" value=\"Понял!\" width=200 height=31 back \"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">");
        return text;
    }

    @Override
    public String onKill(L2NpcInstance npc, QuestState qs) {
        int npcId = npc.getNpcId();
        if ((npcId == Monster1 || npcId == Monster2 || npcId == Monster3) && qs.getCond() == 15) {
            if (qs.getQuestItemsCount(MonsterItem) < 10) {
                qs.giveItems(MonsterItem, 1);
                if (qs.getQuestItemsCount(MonsterItem) == 10) {
                    qs.setCond(16);
                    qs.playSound(SOUND_MIDDLE);
                    return nextNpc(qs.getPlayer(), qs.getCond());
                } else {
                    qs.playSound(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }

    @Override
    public String onEvent(String event, QuestState st, L2NpcInstance npc) {
        L2Player player = st.getPlayer();
//        if (event.equalsIgnoreCase("TalkingGrandMaster_Accept")) {
//            st.setCond(2);
//            addRadarToNpc(player, getNpcByID(TalkingWeaponMerchant));
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("give_weapon_sword")) {
//            st.setCond(3);
//            giveWeapon(player, st, SWORD_WEAPON_LOW);
//            if (player.getLevel() < 20) {
//                addLevel(player, 3);
//            }
//            addRadarToNpc(player, getNpcByID(TalkingArmorMerchant));
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("give_weapon_bow")) {
//            st.setCond(3);
//            giveWeapon(player, st, BOW_WEAPON_LOW);
//            st.giveItems(Arrows, 500);
//            if (player.getLevel() < 20) {
//                addLevel(player, 3);
//            }
//            addRadarToNpc(player, getNpcByID(TalkingArmorMerchant));
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("give_weapon_dagger")) {
//            st.setCond(3);
//            giveWeapon(player, st, DAGGER_WEAPON_LOW);
//            if (player.getLevel() < 20) {
//                addLevel(player, 3);
//            }
//            addRadarToNpc(player, getNpcByID(TalkingArmorMerchant));
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("give_weapon_mage")) {
//            st.setCond(3);
//            giveWeapon(player, st, MAG_WEAPON_LOW);
//            if (player.getLevel() < 20) {
//                addLevel(player, 3);
//            }
//            addRadarToNpc(player, getNpcByID(TalkingArmorMerchant));
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("give_weapon_fist")) {
//            st.setCond(3);
//            giveWeapon(player, st, FIST_WEAPON_LOW);
//            if (player.getLevel() < 20) {
//                addLevel(player, 3);
//            }
//            addRadarToNpc(player, getNpcByID(TalkingArmorMerchant));
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("give_weapon_blunt")) {
//            st.setCond(3);
//            giveWeapon(player, st, BLUNT_WEAPON_LOW);
//            if (player.getLevel() < 20) {
//                addLevel(player, 3);
//            }
//            addRadarToNpc(player, getNpcByID(TalkingArmorMerchant));
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("give_armor")) {
//            if (player.isMageClass()) {
//                for (int i : MAG_SET_LOW) {
//                    st.giveItems(i, 1);
//                    player.getInventory().equipItem(player.getInventory().getItemByItemId(i), true);
//                }
//                if (player.getLevel() < 20) {
//                    addLevel(player, 4);
//                }
//            } else {
//                for (int i : LIGHT_SET_LOW) {
//                    st.giveItems(i, 1);
//                    player.getInventory().equipItem(player.getInventory().getItemByItemId(i), true);
//                }
//                if (player.getLevel() < 20) {
//                    addLevel(player, 4);
//                }
//            }
//            st.setCond(4);
//            addRadarToNpc(player, getNpcByID(TalkingJewelMerchant));
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("give_jewel")) {
//            st.setCond(5);
//            for (int i : JEWEL_SET_LOW) {
//                st.giveItems(i, 1);
//                //player.getInventory().equipItem(player.getInventory().getItemByItemId(i), true);
//            }
//            if (player.getLevel() < 20) {
//                addLevel(player, 5);
//            }
//            addRadarToNpc(player, getNpcByID(TalkingGrocerMerchant));
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("give_grocer")) {
//            st.setCond(6);
//            st.giveItems(HealingPotion, 20);
//            st.giveItems(ManaPotion, 10);
//            st.giveItems(SoulShot, 1000);
//            st.giveItems(SpiritShot, 500);
//            if (player.getLevel() < 20) {
//                addLevel(player, 6);
//            }
//            addRadarToNpc(player, getNpcByID(TalkingGrandMaster));
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("to_school")) {
//            st.setCond(7);
//            addRadarToNpc(player, getNpcByID(TalkingGuard));
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("to_school_mag")) {
//            st.setCond(8);
//            addRadarToNpc(player, getNpcByID(TalkingMagSchoolGrandMaster));
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("to_school_fighter")) {
//            st.setCond(8);
//            addRadarToNpc(player, getNpcByID(TalkingFighterSchoolGrandMaster));
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("to_instructor")) {
//            st.setCond(9);
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("to_manichean1")) {
//            st.setCond(10);
//            st.set(ManicheanDamage, 0);
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("to_manichean2")) {
//            st.setCond(12);
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("to_talking_grandmaster")) {
//            st.setCond(15);
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("to_grandmaster")) {
//            st.setCond(14);
//            return nextNpc(player, st.getCond());
//        } else if (event.equalsIgnoreCase("finish_quest")) {
//            st.takeItems(MonsterItem, 10);
//            if (player.getLevel() < 20) {
//                long addexp = Experience.LEVEL[15] - player.getExp();
//                player.addExpAndSp(addexp, addexp / 5, false, false);
//            }
//            st.playSound(SOUND_FINISH);
//            st.setState(COMPLETED);
//            st.exitCurrentQuest(false);
//            return nextNpc(player, st.getCond());
//        }
        if (event == null || event.isEmpty()){
            return "noquest";
        }else {
            final OnEventEnumeration onEventEnumeration = OnEventEnumeration.valueOf(event);
            return onEventEnumeration.onEvent(player, st, npc);
        }
    }

    public static String nextNpc(L2Player player, int cond) {
        String htmltext = null;
        if (cond == 0) {
            htmltext = fileRead("talking_nextNPC_00.htm", player, getNpcByID(StartNewbieGuide));
            addRadarToNpc(player, getNpcByID(TalkingGrandMaster));
        } else if (cond == 1) {
            htmltext = fileRead("talking_nextNPC_01.htm", player, getNpcByID(TalkingGrandMaster));
            addRadarToNpc(player, getNpcByID(TalkingGrandMaster));
        } else if (cond == 2) {
            htmltext = fileRead("talking_nextNPC_02.htm", player, getNpcByID(TalkingWeaponMerchant));
            addRadarToNpc(player, getNpcByID(TalkingWeaponMerchant));
        } else if (cond == 3) {
            htmltext = fileRead("talking_nextNPC_03.htm", player, getNpcByID(TalkingArmorMerchant));
            addRadarToNpc(player, getNpcByID(TalkingArmorMerchant));
        } else if (cond == 4) {
            htmltext = fileRead("talking_nextNPC_04.htm", player, getNpcByID(TalkingJewelMerchant));
            addRadarToNpc(player, getNpcByID(TalkingJewelMerchant));
        } else if (cond == 5) {
            htmltext = fileRead("talking_nextNPC_05.htm", player, getNpcByID(TalkingGrocerMerchant));
            addRadarToNpc(player, getNpcByID(TalkingGrocerMerchant));
        } else if (cond == 6) {
            htmltext = fileRead("talking_nextNPC_06.htm", player, getNpcByID(TalkingGrandMaster));
            addRadarToNpc(player, getNpcByID(TalkingGrandMaster));
        } else if (cond == 7) {
            htmltext = fileRead("talking_nextNPC_07.htm", player, getNpcByID(TalkingGuard));
            addRadarToNpc(player, getNpcByID(TalkingGuard));
        } else if (cond == 8) {
            if (player.isMageClass()) {
                htmltext = fileRead("talking_nextNPC_08.htm", player, getNpcByID(TalkingMagSchoolGrandMaster));
                addRadarToNpc(player, getNpcByID(TalkingMagSchoolGrandMaster));
            } else {
                htmltext = fileRead("talking_nextNPC_08.htm", player, getNpcByID(TalkingFighterSchoolGrandMaster));
                addRadarToNpc(player, getNpcByID(TalkingFighterSchoolGrandMaster));
            }
        } else if (cond == 9) {
            htmltext = fileRead("talking_nextNPC_09.htm", player, getNpcByID(TalkingFizSchoolInstructor));
            addRadarToNpc(player, getNpcByID(TalkingFizSchoolInstructor));
        } else if (cond == 10) {
            htmltext = fileRead("talking_nextNPC_10.htm", player, getNpcByID(TalkingSchoolManichean));
            addRadarToNpc(player, getNpcByID(TalkingSchoolManichean));
        } else if (cond == 11) {
            htmltext = fileRead("talking_nextNPC_11.htm", player, getNpcByID(TalkingFizSchoolInstructor));
            addRadarToNpc(player, getNpcByID(TalkingFizSchoolInstructor));
        } else if (cond == 12) {
            htmltext = fileRead("talking_nextNPC_12.htm", player, getNpcByID(TalkingSchoolManichean));
            addRadarToNpc(player, getNpcByID(TalkingSchoolManichean));
        } else if (cond == 13) {
            htmltext = fileRead("talking_nextNPC_13.htm", player, getNpcByID(TalkingFizSchoolInstructor));
            addRadarToNpc(player, getNpcByID(TalkingFizSchoolInstructor));
        } else if (cond == 14) {
            htmltext = fileRead("talking_nextNPC_14.htm", player, getNpcByID(TalkingFighterSchoolGrandMaster));
            addRadarToNpc(player, getNpcByID(TalkingFighterSchoolGrandMaster));
        } else if (cond == 15) {
            htmltext = fileRead("talking_nextNPC_15.htm", player, getNpcByID(TalkingGrandMaster));
            addRadarToNpc(player, getNpcByID(TalkingGrandMaster));
        }
        return htmltext;
    }

    @Override
    public String onTalk(L2NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        L2Player player = st.getPlayer();
        if (npcId == StartNewbieGuide) {
            if (st.getState() == CREATED) {
                htmltext = "talking_newbie_guide_01.htm";
                npc.makeSupportMagic(player);
            } else {
                htmltext = nextNpc(player, cond);
            }
        } else if (npcId == TalkingGrandMaster) {
            if (cond == 1) {
                htmltext = "talking_grandmaster_01.htm";
            } else if (cond == 6) {
                htmltext = "talking_grandmaster_02.htm";
            } else if (cond == 16 && st.getQuestItemsCount(MonsterItem) == 10) {
                htmltext = "talking_grandmaster_03.htm";
            } else {
                htmltext = nextNpc(player, cond);
            }
        } else if (npcId == TalkingWeaponMerchant) {
            if (cond == 2) {
                if (player.isMageClass()) {
                    htmltext = "talking_weapon_merchant_02.htm";
                } else {
                    htmltext = "talking_weapon_merchant_01.htm";
                }
            } else {
                htmltext = nextNpc(player, cond);
            }
        } else if (npcId == TalkingArmorMerchant) {
            if (cond == 3) {
                htmltext = "talking_armor_merchant_01.htm";
            } else {
                htmltext = nextNpc(player, cond);
            }
        } else if (npcId == TalkingJewelMerchant) {
            if (cond == 4) {
                htmltext = "talking_jewel_merchant_01.htm";
            } else {
                htmltext = nextNpc(player, cond);
            }
        } else if (npcId == TalkingGrocerMerchant) {
            if (cond == 5) {
                htmltext = "talking_grocer_merchant_01.htm";
            } else {
                htmltext = nextNpc(player, cond);
            }
        } else if (npcId == TalkingGuard) {
            if (cond == 7) {
                if (player.isMageClass()) {
                    htmltext = "talking_guard_01.htm";
                } else {
                    htmltext = "talking_guard_02.htm";
                }
            } else if (cond == 8) {
                htmltext = nextNpc(player, cond);
            } else {
                htmltext = nextNpc(player, cond);
            }
        } else if (npcId == TalkingFighterSchoolGrandMaster) {
            if (cond == 8) {
                htmltext = "talking_fighter_school_grand_master_01.htm";
            } else if (cond == 14) {
                htmltext = "talking_fighter_school_grand_master_02.htm";
            } else {
                htmltext = nextNpc(player, cond);
            }
        } else if (npcId == TalkingFizSchoolInstructor) {
            if (cond == 9) {
                htmltext = "talking_fiz_school_instructor_01.htm";
            } else if (cond == 11) {
                giveSkill(player);
                htmltext = "talking_fiz_school_instructor_02.htm";
            } else if (cond == 13) {
                htmltext = "talking_fiz_school_instructor_03.htm";
            } else {
                htmltext = nextNpc(player, cond);
            }
        }

        return htmltext;
    }

    private void giveSkill(L2Player player) {
        int count = 0;
        GArray<L2SkillLearn> skills = player.getAvailableSkills(player.getClassId());
        while (skills.size() > count) {
            count = 0;
            for (L2SkillLearn s : skills) {
                L2Skill sk = SkillTable.getInstance().getInfo(s.id, s.skillLevel);
                if (sk == null || !sk.getCanLearn(player.getClassId())) {
                    count++;
                    continue;
                }
                player.addSkill(sk, true);
            }
            skills = player.getAvailableSkills(player.getClassId());
        }

        player.sendPacket(new SkillList(player));
    }

    @Override
    public void onLoad() {
        ScriptFile._log.info("Loaded Quest: " + QUEST_NAME);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }
}