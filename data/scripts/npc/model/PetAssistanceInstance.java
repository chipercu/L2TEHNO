package npc.model;

import ai.petAssistent.PetAssistentConst;
import l2open.common.Html_Constructor.tags.Img;
import l2open.common.Html_Constructor.tags.Table;
import l2open.common.Html_Constructor.tags.parameters.Parameters;
import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Rnd;
import utils_soft.common.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ai.petAssistent.PetAssistentConst.*;
import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;

public class PetAssistanceInstance extends L2NpcInstance {

    private L2Player player;

    public PetAssistanceInstance(int objectId, L2NpcTemplate template) {
        super(objectId, template);
        L2Player player = L2ObjectsStorage.getPlayer(this.getTitle());
        if (player != null) {
            this.player = player;
        }
    }

    @Override
    public boolean isInvul() {
        return true;
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

    protected void addIgnoreHeb(int id){
        List<Integer> ignoreHerbsList = getIgnoreHerbsList();
        ignoreHerbsList.add(id);
        String collect = ignoreHerbsList.stream().map(String::valueOf).collect(Collectors.joining(";"));
        player.setVar(pet_ignore_herbs, collect);
    }

    protected void removeIgnoreHeb(int id){
        List<Integer> ignoreHerbsList = getIgnoreHerbsList();
        ignoreHerbsList.remove(id);
        String collect = ignoreHerbsList.stream().map(String::valueOf).collect(Collectors.joining(";"));
        player.setVar(pet_ignore_herbs, collect);
    }

    protected List<Integer> getIgnoreHerbsList(){
        String var = player.getVar(pet_ignore_herbs);
        if (var == null || var.isEmpty()){
            return new ArrayList<>();
        }
        return Arrays.stream(var.split(";")).map(Integer::parseInt).collect(Collectors.toList());
    }

    protected void changeDisplayId(int new_id){
        player.setVar(ASSISTENT_DISPLAY_ID, String.valueOf(new_id));
        player.setVar(ASSISTENT_IS_SUMMONED, "false");
        player.unsetVar(ASSISTENT_DELAY);
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                L2ItemInstance itemByItemId = player.getInventory().getItemByItemId(10663);
                ItemHandler.getInstance().getItemHandler(10663).useItem(player, itemByItemId, false);
            }
        }, 250L);

        this.deleteMe();

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
        } else if (command.startsWith("changeDisplayID")) {
            int newId = Integer.parseInt(command.split(" ")[1]);
            if (newId == 0){
                showChangeDisplayIdWindow();
            }else {
                changeDisplayId(newId);
            }

        } else if (command.equalsIgnoreCase("test")) {

            Map<Integer, String> map = ItemTemplates.getInstance().getAllTemplates().stream()
                    .filter(L2Item::isHerb)
                    .collect(Collectors.toMap(L2Item::getItemId, L2Item::getName));

            map.forEach((id, name) -> {
                System.out.println(id + ", // " + name );
            });

        } else if (command.equalsIgnoreCase("showAllHerbList")) {
            showAllHerbList();
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    public void showHtmlFile(L2Player player) {
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        String builder = "<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">" +
                "<tr>" +
                "<td valign=\"top\" align=\"center\">" +
                "<br><font name=\"hs9\">Чем могу помочь хозяюшка?</font><br>" +
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
                "<button value=\"Изменить внешний вид\"" +
                " action=\"bypass -h npc_%objectId%_changeDisplayID 0\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">" +
                "</td></tr></table></td></tr></table>";

        html.setHtml(builder);
        player.sendPacket(html);
    }

    public void showChangeDisplayIdWindow(){
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        String builder = "<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">" +
                "<tr>" +
                "<td valign=\"top\" align=\"center\">" +
                "<br><font name=\"hs9\">Какой облик хотите что бы я принял?</font><br>" +
                "<table border=0 width=280>" +
                "<tr>" +
                "<td FIXWIDTH=292 align=center valign=top>" +
                "<button value=\"Гоблин\" action=\"bypass -h npc_%objectId%_changeDisplayID 20326\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">" +
                "<button value=\"Фея\" action=\"bypass -h npc_%objectId%_changeDisplayID 31845\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">" +
                "<button value=\"Крысалюд\" action=\"bypass -h npc_%objectId%_changeDisplayID 20606\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">" +
                "<button value=\"Лик\" action=\"bypass -h npc_%objectId%_changeDisplayID 20617\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">" +
//                "<button value=\"showAllHerbList\" action=\"bypass -h npc_%objectId%_showAllHerbList\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">" +
//                "<button value=\"test\" action=\"bypass -h npc_%objectId%_test\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">" +
                "</td></tr></table></td></tr></table>";
        html.setHtml(builder);
        player.sendPacket(html);
    }

    public void showAllHerbList(){
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);

        Table table = new Table(6, 5).setParams(height(358), width(292), back("l2ui_ct1.Windows_DF_TooltipBG"), cellpadding(0), cellspacing(0), border(0));
        List<Integer> collect = Arrays.stream(HERBS_LIST).boxed().collect(Collectors.toList());
        List<List<Integer>> lists = Component.partitionList(collect, 5);


        for (int i = 0; i < lists.size(); i++) {
            table.row(i).col(0).insert(new Img(ItemTemplates.getInstance().getTemplate(lists.get(i).get(0)).getIcon(), 32, 32).build());
            table.row(i).col(1).insert(new Img(ItemTemplates.getInstance().getTemplate(lists.get(i).get(1)).getIcon(), 32, 32).build());
            table.row(i).col(2).insert(new Img(ItemTemplates.getInstance().getTemplate(lists.get(i).get(2)).getIcon(), 32, 32).build());
            table.row(i).col(3).insert(new Img(ItemTemplates.getInstance().getTemplate(lists.get(i).get(3)).getIcon(), 32, 32).build());
            table.row(i).col(4).insert(new Img(ItemTemplates.getInstance().getTemplate(lists.get(i).get(4)).getIcon(), 32, 32).build());
        }
        html.setHtml(table.build());
        player.sendPacket(html);
    }



}
