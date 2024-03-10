package npc.model;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.PetItemList;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2EtcItem;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;

public class PetAssistanceInstance extends L2NpcInstance {

    private L2Player player;

    private static final String pet_spoil_force = "pet_spoil_force";
    private static final String pet_spoil_single = "pet_spoil_single";
    private static final String pet_pick = "pet_pick";
    private static final String pet_pick_only_adena = "pet_pick_only_adena";
    private static final String pet_pick_herb = "pet_pick_herb";

    public PetAssistanceInstance(int objectId, L2NpcTemplate template) {
        super(objectId, template);
        L2Player player = L2ObjectsStorage.getPlayer(this.getTitle());
        if (player != null) {
            this.player = player;
        }
    }

    @Override
    public void showChatWindow(L2Player player, int val) {
        if (player != this.player){
            return;
        }
        showHtmlFile(player);
    }

    private void changeBollVar(L2Player player, String var){
        boolean b = Boolean.parseBoolean(player.getVar(var));
        if (b){
            player.setVar(var, "false");
        }else {
            player.setVar(var, "true");
        }
    }



    @Override
    public void onBypassFeedback(L2Player player, String command) {

        if (player != this.player){
            return;
        }

        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("setspoilforce")) {
            changeBollVar(player, pet_spoil_force);
            showHtmlFile(player);
        } else if (command.equalsIgnoreCase("setspoilsingle")) {
            changeBollVar(player, pet_spoil_single);
            showHtmlFile(player);
        } else if (command.equalsIgnoreCase("setpick")) {
            changeBollVar(player, pet_pick);
            showHtmlFile(player);
        } else if (command.equalsIgnoreCase("setpickonlyadena")) {
            changeBollVar(player, pet_pick_only_adena);
            showHtmlFile(player);
        } else if (command.equalsIgnoreCase("setpickherb")) {
            changeBollVar(player, pet_pick_herb);
            showHtmlFile(player);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    public void showHtmlFile(L2Player player) {
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        String builder = "<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">" +
                "<tr>" +
                "<td valign=\"top\" align=\"center\">" +
                "<br><font name=\"hs12\">Чем могу помочь хозяюшка?</font><br>" +
                "<table border=0 width=280>" +
                "<tr>" +
                "<td FIXWIDTH=292 align=center valign=top>" +
                "<button value=\"" + (player.getVarB(pet_spoil_force) ? "Оценивать сразу" : "Оценивать после атаки") + "\"" +
                " action=\"bypass -h npc_%objectId%_setspoilforce\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">" +
                "<button value=\"" + (player.getVarB(pet_spoil_single) ? "Оценивать только цель" : "Оценивать группу") + "\"" +
                " action=\"bypass -h npc_%objectId%_setspoilsingle\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">" +
                "<button value=\"" + (player.getVarB(pet_pick) ? "Собирать предметы" : "Не собирать предметы") + "\"" +
                " action=\"bypass -h npc_%objectId%_setpick\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">" +
                "<button value=\"" + (player.getVarB(pet_pick_only_adena) ? "Собирать только адену" : "Собирать не только адену") + "\"" +
                " action=\"bypass -h npc_%objectId%_setpickonlyadena\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">" +
                "<button value=\"" + (player.getVarB(pet_pick_herb) ? "Собирать настойки" : "Не собирать настойки") + "\"" +
                " action=\"bypass -h npc_%objectId%_setpickherb\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">" +
                "</td></tr></table></td></tr></table>";

        html.setHtml(builder);
        player.sendPacket(html);
    }
}
