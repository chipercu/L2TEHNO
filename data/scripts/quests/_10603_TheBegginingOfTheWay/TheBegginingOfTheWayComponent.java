package quests._10603_TheBegginingOfTheWay;

import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.RadarControl;
import l2open.util.Files;
import quests.common.CommonComponent;

import static l2open.gameserver.model.quest.Quest.STARTED;

/**
 * Created by a.kiperku
 * Date: 06.12.2023
 */

public class TheBegginingOfTheWayComponent extends CommonComponent implements TheBegginingOfTheWayConstants{


    public static String enterWorld(L2Player player, QuestState st, L2NpcInstance npc){
        return nextNpc(player, st.getCond());
    }

    public static String start(L2Player player, QuestState questState, L2NpcInstance npcInstance) {
        questState.setState(STARTED);
        questState.setCond(1);
        addRadarToNpc(player, getNpcByID(TalkingGrandMaster));
        return nextNpc(player, questState.getCond());
    }



    private void addLevel(L2Player player, int level) {
        long addexp = Experience.LEVEL[3] - player.getExp();
        player.addExpAndSp(addexp, addexp / 5, false, false);
    }

    private void giveWeapon(L2Player player, QuestState st, int weapon) {
        st.giveItems(weapon, 1);
        player.getInventory().equipItem(player.getInventory().getItemByItemId(weapon), true);
    }


    public static L2NpcInstance getNpcByID(int id){
        return L2ObjectsStorage.getByNpcId(id);
    }

    public static String fileRead( String htm, L2Player player, L2NpcInstance npc){
        String text = Files.read(HTML_ROOT + htm, player);
        text = text.replace("<?NPC?>", "<font color=\"0099FF\">" + npc.getName() + "</font>");
        text = text.replace("[button]", "<button action=\"bypass -h Quest _10603_TheBegginingOfTheWay addRadarToNpc " + player + " " + npc +"\" value=\"Понял!\" width=200 height=31 back \"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">");
        return text;
    }

    public static String nextNpc(L2Player player, int cond){
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
        }
        else if (cond == 8) {
            if (player.isMageClass()){
                htmltext = fileRead("talking_nextNPC_08.htm", player, getNpcByID(TalkingMagSchoolGrandMaster));
                addRadarToNpc(player, getNpcByID(TalkingMagSchoolGrandMaster));
            }else {
                htmltext = fileRead("talking_nextNPC_08.htm", player, getNpcByID(TalkingFighterSchoolGrandMaster));
                addRadarToNpc(player, getNpcByID(TalkingFighterSchoolGrandMaster));
            }
        }else if (cond == 9){
            htmltext = fileRead("talking_nextNPC_09.htm", player, getNpcByID(TalkingFizSchoolInstructor));
            addRadarToNpc(player, getNpcByID(TalkingFizSchoolInstructor));
        }else if (cond == 10){
            htmltext = fileRead("talking_nextNPC_10.htm", player, getNpcByID(TalkingSchoolManichean));
            addRadarToNpc(player, getNpcByID(TalkingSchoolManichean));
        }else if (cond == 11){
            htmltext = fileRead("talking_nextNPC_11.htm", player, getNpcByID(TalkingFizSchoolInstructor));
            addRadarToNpc(player, getNpcByID(TalkingFizSchoolInstructor));
        }else if (cond == 12){
            htmltext = fileRead("talking_nextNPC_12.htm", player, getNpcByID(TalkingSchoolManichean));
            addRadarToNpc(player, getNpcByID(TalkingSchoolManichean));
        }else if (cond == 13){
            htmltext = fileRead("talking_nextNPC_13.htm", player, getNpcByID(TalkingFizSchoolInstructor));
            addRadarToNpc(player, getNpcByID(TalkingFizSchoolInstructor));
        }else if (cond == 14){
            htmltext = fileRead("talking_nextNPC_14.htm", player, getNpcByID(TalkingFighterSchoolGrandMaster));
            addRadarToNpc(player, getNpcByID(TalkingFighterSchoolGrandMaster));
        }else if (cond == 15){
            htmltext = fileRead("talking_nextNPC_15.htm", player, getNpcByID(TalkingGrandMaster));
            addRadarToNpc(player, getNpcByID(TalkingGrandMaster));
        }
        return htmltext;
    }



}
