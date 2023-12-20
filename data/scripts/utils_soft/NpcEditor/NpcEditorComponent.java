package utils_soft.NpcEditor;

import l2open.common.Html_Constructor.tags.*;
import l2open.common.Html_Constructor.tags.parameters.Color;
import l2open.common.Html_Constructor.tags.parameters.EditType;
import l2open.config.ConfigValue;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.Location;
import utils_soft.NpcEditor.enums.AI_TYPE;
import utils_soft.NpcEditor.enums.INSTANCE_TYPE;
import l2open.extensions.common.Component;
import l2open.database.Filter;
import l2open.database.Resource;
import l2open.database.ResourceProvider;
import l2open.database.exceptions.ResourceProvideException;
import l2open.database.schemes.builders.DroplistBuilder;
import l2open.database.schemes.builders.NpcBuilder;
import l2open.database.schemes.builders.NpcElementBuilder;
import l2open.database.schemes.builders.NpcskillsBuilder;
import l2open.database.schemes.resources.DroplistResource;
import l2open.database.schemes.resources.NpcElementResource;
import l2open.database.schemes.resources.NpcResource;
import l2open.database.schemes.resources.NpcskillsResource;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;
import static l2open.common.Html_Constructor.tags.parameters.Position.*;
import static utils_soft.NpcEditor.NpcEditorCommands.*;

public class NpcEditorComponent extends Component{

    private static final String backMainBypass = "admin_npc_editor null 0 1";
    private static final String window_titel = "Npc Editor";
    protected static Logger _log = Logger.getLogger(NpcEditorComponent.class.getName());

    private static final Resource<NpcResource, NpcBuilder> npcResource = new ResourceProvider<>(NpcResource.class);
    private static final Resource<NpcElementResource, NpcElementBuilder> npcElementResource = new ResourceProvider<>(NpcElementResource.class);
    private static final Resource<DroplistResource, DroplistBuilder> dropListResource = new ResourceProvider<>(DroplistResource.class);
    private static final Resource<NpcskillsResource, NpcskillsBuilder> npcSkillsResource = new ResourceProvider<>(NpcskillsResource.class);

    public NpcEditorComponent() {
    }

    public static void basePage(L2Player player,int npcId, int npcObjectId, Table table, String saveButton) {
        final Table main = new Table(2, 2);
        main.row(0).col(0).insert(headerTable(npcId, npcObjectId));
        main.row(0).col(1).insert(table);
        main.row(1).col(1).setParams(align(CENTER)).insert(saveButton);
        CBWindow(player, main, window_titel);
    }
    public static Table headerTable(int npcId, int npcObjectId){
        String params = npcId + " " + npcObjectId;
        final Table headerTable = new Table(9, 1);
        headerTable.row(0).col(0).insert(new Button("HOME", actionCom(admin_npc_editor, "none null 0"), 150, 36));
        headerTable.row(1).col(0).setParams(width(150)).insert(new Button("MainStats", actionCom(admin_npc_editor_main_stats, params), 150, 36));
        headerTable.row(2).col(0).insert(new Button("BaseStats", actionCom(admin_npc_editor_base_stats, params ), 150, 36));
        headerTable.row(3).col(0).insert(new Button("Skills", actionCom(admin_npc_editor_skills, params + " active"), 150, 36));
        headerTable.row(4).col(0).insert(new Button("Drop", actionCom(admin_npc_editor_drop, params + " DROP"), 150, 36));
        headerTable.row(5).col(0).insert(new Button("Visual", actionCom(admin_npc_editor_visual, params), 150, 36));
        headerTable.row(6).col(0).insert(new Button("Elements", actionCom(admin_npc_editor_elements, params), 150, 36));
        headerTable.row(7).col(0).insert(new Button("Location", actionCom(admin_npc_editor_location, params), 150, 36));
        headerTable.row(8).col(0).insert(new Button("Other", actionCom(admin_npc_editor_other, params), 150, 36));
        return headerTable;
    }
    public static Table skillTable(int npcId, List<L2Skill> skills){
        final Table skillsTable = new Table(skills.size(), 5).setParams(cellpadding(0));
        for (int i = 0; i < skills.size(); i++){
            L2Skill skill = skills.get(i);
            skillsTable.row(i).col(0).setParams(width(40)).insert(new Img(skill.getIcon()));
            skillsTable.row(i).col(1).setParams(width(80)).setParams(valign(CENTER), align(LEFT)).insert(formatSkillName(skill.getName()));
            skillsTable.row(i).col(2).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(skill.getId());
            skillsTable.row(i).col(3).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(skill.getLevel());
            skillsTable.row(i).col(4).setParams(width(40)).insert(new Button("X", actionCom(admin_npc_editor_remove_skill, npcId + " " + skill.getId())));
        }
        return skillsTable;
    }
    public static Table dropTable(int npcId, List<DroplistResource> dropItems){
        final Table main = new Table(3, 1);
        final Table dropTable = new Table(dropItems.size(), 7).setParams(cellpadding(0));
        for (int i = 0; i < dropItems.size(); i++){
            final DroplistResource dropItem = dropItems.get(i);
            final L2Item template = ItemTemplates.getInstance().getTemplate(dropItem.getItemId());

            final float chance = dropItem.getChance() / 10000f;
            dropTable.row(i).col(0).setParams(width(40)).insert(new Img(template.getIcon()));
            dropTable.row(i).col(1).setParams(width(80)).setParams(valign(CENTER), align(LEFT)).insert(formatItemName(template.getName()));
            dropTable.row(i).col(2).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(String.valueOf(dropItem.getMin()));
            dropTable.row(i).col(3).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(String.valueOf(dropItem.getMax()));
            dropTable.row(i).col(4).setParams(width(30)).setParams(valign(CENTER), align(CENTER)).insert(dropItem.getCategory());
            dropTable.row(i).col(5).setParams(width(50)).setParams(valign(CENTER), align(CENTER)).insert(new DecimalFormat("#0.00").format(chance) + "%");
            dropTable.row(i).col(6).setParams(width(40)).insert(new Button("X", actionCom(admin_npc_editor_remove_drop, npcId + " " + dropItem.getItemId() + " " + (dropItem.getSweep() == 1))));
        }
        final Table addDropTable = new Table(1, 5);
        addDropTable.row(0).col(0).setParams(width(20), valign(CENTER), align(LEFT)).insert("id");
        addDropTable.row(0).col(1).insert(new Edit("id", 50, 12, EditType.num, 12));
        addDropTable.row(0).col(2).setParams(width(35), valign(CENTER), align(LEFT)).insert("Level");
        addDropTable.row(0).col(3).insert(new Edit("level", 85, 12, EditType.num, 12));
        addDropTable.row(0).col(4).insert(new Button("Добавить", actionCom(admin_npc_editor_add_drop, npcId + " $id $level"), 80, 20));

        main.row(0).col(0).insert(dropTable);
        main.row(1).col(0).insert(addDropTable);
        main.row(2).col(0).setParams(height(20));
        return main;
    }
    public static Table parametersTable(String name, String value, Enum command, int npcId){
        final Table table = new Table(1, 4).setParams(border(0));
        table.row(0).col(0).setParams(align(RIGHT), valign(CENTER), width(50)).insert(new Font(Color.BROWN, name + ": "));
        table.row(0).col(1).setParams(align(LEFT), valign(CENTER), width(100)).insert(new Font(Color.BLUE, value));
        table.row(0).col(2).setParams(align(RIGHT), valign(CENTER), width(50)).insert("<br>" + new Edit(name, 80, 12, EditType.num, 2).build());
        table.row(0).col(3).setParams(align(RIGHT), valign(CENTER)).insert(new Button("OK", actionCom(command, npcId + " " + name +  " $" + name)));
        return table;
    }

    public static void showMainPage(L2Player player, String[] args) {
        String filter = args[1];
        String filterValue = args[2].equals("0") ? "" : args[2].equals("null") ? "" : args[2];
        int page = args.length < 4 ? 0 : Integer.parseInt(args[3]);

        List<NpcResource> npcList;

        int offset = page * 17;

        switch (filter) {
            case "npcname":
                npcList = npcResource.findList(new Filter().LIKE(NpcResource.NAME, filterValue).ORDER_BY(NpcResource.ID).LIMIT(17).OFFSET(offset));
                break;
            case "npcid":
                npcList = npcResource.findList(new Filter().LIKE(NpcResource.ID, filterValue).ORDER_BY(NpcResource.ID).LIMIT(17).OFFSET(offset));
                break;
            case "npctype":
                npcList = npcResource.findList(new Filter().LIKE(NpcResource.TYPE, filterValue).ORDER_BY(NpcResource.ORDINAL).LIMIT(17).OFFSET(offset));
                break;
            default:
                npcList = npcResource.findList(new Filter().ORDER_BY(NpcResource.ID).LIMIT(17).OFFSET(offset));
                break;
        }

        final Table mainTable = new Table(2, 1);

        final Table filterTable = new Table(2, 7);
        filterTable.row(0).col(0).setParams(height(10));
        filterTable.row(1).col(0).setParams(width(50)).setParams(align(RIGHT), valign(CENTER)).insert(new Font(Color.GREN, "Поиск:"));
        filterTable.row(1).col(1).setParams(width(100)).setParams(align(LEFT), valign(BOTTOM)).insert("<br>" + new Edit("find", 100, 12, EditType.text, 12).build());
        filterTable.row(1).col(2).setParams(width(60)).setParams(valign(CENTER)).insert(new Button("Id", actionCom(admin_npc_editor,"npcid $find 0"), 50, 32));
        filterTable.row(1).col(3).setParams(width(60)).setParams(valign(CENTER)).insert(new Button("Name", actionCom(admin_npc_editor,"npcname $find 0"), 50, 32));
        filterTable.row(1).col(4).setParams(width(60)).setParams(valign(CENTER)).insert(new Button("Type", actionCom(admin_npc_editor,"npctype $find 0"), 50, 32));
        filterTable.row(1).col(5).setParams(width(250), valign(CENTER), align(LEFT)).insert(new Font(Color.GRAY, "filter by: ").build()  + new Font(Color.BROWN, filter).build() + " | " + new Font(Color.BLUE, filterValue).build());
        filterTable.row(1).col(6).setParams(width(100)).insert(new Button("Create New NPC", actionCom(admin_npc_editor_create,"Empty 1 NPC FIGHTER"), 100, 32));


        final Table npcTable = new Table(2, 1);

        final Table pages = new Table(1, 3);
        pages.row(0).col(0).insert(new Button("<<<", actionCom(admin_npc_editor, filter + " " + filterValue + " " +(page == 0 ? 0 : page - 1)), 60, 20));
        pages.row(0).col(1).setParams(width(100), align(CENTER)).insert(new Font(Color.GOLD, "page:" + page));
        pages.row(0).col(2).insert(new Button(">>>", actionCom(admin_npc_editor, filter + " " + filterValue + " " +( page + 1)), 60, 20));

        final Table table = new Table(npcList.size(), 5).setParams(border(1));
        for (int i = 0; i <npcList.size(); i++) {
            final NpcResource npcModel = npcList.get(i);
            Font type;
            switch (npcModel.getType()){
                case "L2Monster" : {type = new Font(Color.BROWN, npcModel.getType());break;}
                case "L2RaidBoss" : {type = new Font(Color.BLUE, npcModel.getType());break;}
                case "L2Minion" : {type = new Font(Color.GREN, npcModel.getType());break;}
                case "L2Npc" : {type = new Font(Color.GRAY, npcModel.getType());break;}
                default: type = new Font(Color.GOLD, npcModel.getType());
            }
            table.row(i).col(0).setParams(width(100)).insert(new Font(Color.BROWN, npcModel.getId()));
            table.row(i).col(1).setParams(width(250)).insert(new Font(Color.GREN, npcModel.getName()));
            table.row(i).col(2).setParams(width(100)).insert(new Font(Color.BROWN, npcModel.getLevel()));
            table.row(i).col(3).setParams(width(150)).insert(type);
            table.row(i).col(4).insert(new Button("redact", actionCom(admin_npc_editor_main_stats, npcModel.getId() + " 0"), 80, 20));
        }

        npcTable.row(0).col(0).setParams(align(CENTER)).insert(pages);
        npcTable.row(1).col(0).insert(table);
        mainTable.row(0).col(0).setParams(align(CENTER)).insert(filterTable);
        mainTable.row(1).col(0).setParams(align(CENTER)).insert(npcTable);

        CBWindow(player, mainTable, window_titel);
    }
    public static void showMainStats(L2Player player, String[] args){
        int npcId = Integer.parseInt(args[1]);
        int npcObjectId = Integer.parseInt(args[2]);
        final NpcResource npc = npcResource.find(new Filter().WHERE(NpcResource.ID, npcId));
        final Table table = new Table(20, 2);
        table.row(0).col(0).insert(parametersTable(NpcResource.LEVEL, String.valueOf(npc.getLevel()), admin_npc_editor_save_main_stats, npcId));
        table.row(1).col(0).insert(separator(200));
        table.row(2).col(0).insert(parametersTable(NpcResource.HP, String.valueOf(npc.getHp()), admin_npc_editor_save_main_stats, npcId));
        table.row(2).col(1).insert(parametersTable(NpcResource.MP, String.valueOf(npc.getMp()), admin_npc_editor_save_main_stats, npcId));
        table.row(3).col(0).insert(separator(200));
        table.row(4).col(0).insert(parametersTable(NpcResource.PATK, String.valueOf(npc.getPatk()), admin_npc_editor_save_main_stats, npcId));
        table.row(4).col(1).insert(parametersTable(NpcResource.PDEF, String.valueOf(npc.getPdef()), admin_npc_editor_save_main_stats, npcId));
        table.row(5).col(0).insert(separator(200));
        table.row(6).col(0).insert(parametersTable(NpcResource.MATK, String.valueOf(npc.getMatk()), admin_npc_editor_save_main_stats, npcId));
        table.row(6).col(1).insert(parametersTable(NpcResource.MDEF, String.valueOf(npc.getMdef()), admin_npc_editor_save_main_stats, npcId));
        table.row(7).col(0).insert(separator(200));
        table.row(8).col(0).insert(parametersTable(NpcResource.ATKSPD, String.valueOf(npc.getAtkspd()), admin_npc_editor_save_main_stats, npcId));
        table.row(8).col(1).insert(parametersTable(NpcResource.MATKSPD, String.valueOf(npc.getMatkspd()), admin_npc_editor_save_main_stats, npcId));
        table.row(9).col(0).insert(separator(200));
        table.row(10).col(0).insert(parametersTable(NpcResource.WALKSPD, String.valueOf(npc.getWalkspd()), admin_npc_editor_save_main_stats, npcId));
        table.row(10).col(1).insert(parametersTable(NpcResource.RUNSPD, String.valueOf(npc.getRunspd()), admin_npc_editor_save_main_stats, npcId));
        table.row(11).col(0).insert(separator(200));
        table.row(12).col(0).insert(parametersTable(NpcResource.EXP, String.valueOf(npc.getExp()), admin_npc_editor_save_main_stats, npcId));
        table.row(12).col(1).insert(parametersTable(NpcResource.SP, String.valueOf(npc.getSp()), admin_npc_editor_save_main_stats, npcId));
        table.row(13).col(0).insert(separator(200));
        table.row(14).col(0).insert(parametersTable(NpcResource.STR, String.valueOf(npc.getStr()), admin_npc_editor_save_main_stats, npcId));
        table.row(14).col(1).insert(parametersTable(NpcResource.CON, String.valueOf(npc.getCon()), admin_npc_editor_save_main_stats, npcId));
        table.row(15).col(0).insert(separator(200));
        table.row(16).col(0).insert(parametersTable(NpcResource.DEX, String.valueOf(npc.getDex()), admin_npc_editor_save_main_stats, npcId));
        table.row(16).col(1).insert(parametersTable(NpcResource._INT, String.valueOf(npc.getInt()), admin_npc_editor_save_main_stats, npcId));
        table.row(17).col(0).insert(separator(200));
        table.row(18).col(0).insert(parametersTable(NpcResource.WIT, String.valueOf(npc.getWit()), admin_npc_editor_save_main_stats, npcId));
        table.row(18).col(1).insert(parametersTable(NpcResource.MEN, String.valueOf(npc.getMen()), admin_npc_editor_save_main_stats, npcId));

        final Table main = new Table(3, 1);
        main.row(0).col(0).setParams(height(20));
        main.row(1).col(0).insert(" ID: " + new Font(Color.GOLD, npcId).build() + " | " + new Font(Color.GREN, npc.getName()).build());
        main.row(2).col(0).insert(table);

        basePage(player, npcId, npcObjectId, main, "");
    }
    public static void showBaseStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int npcObjectId = Integer.parseInt(args[2]);
        final NpcResource npc = npcResource.find(new Filter().WHERE(NpcResource.ID, npcId));
        final Table table = new Table(14, 1);
        table.row(0).col(0).insert(parametersTable("str", String.valueOf(npc.getStr()), admin_npc_editor_save_main_stats, npcId));
        table.row(1).col(0).insert(parametersTable("con", String.valueOf(npc.getCon()), admin_npc_editor_save_main_stats, npcId));
        table.row(2).col(0).insert(parametersTable("dex", String.valueOf(npc.getDex()), admin_npc_editor_save_main_stats, npcId));
        table.row(3).col(0).insert(parametersTable("int", String.valueOf(npc.getInt()), admin_npc_editor_save_main_stats, npcId));
        table.row(4).col(0).insert(parametersTable("wit", String.valueOf(npc.getWit()), admin_npc_editor_save_main_stats, npcId));
        table.row(5).col(0).insert(parametersTable("men", String.valueOf(npc.getMen()), admin_npc_editor_save_main_stats, npcId));

        final Table main = new Table(3, 1);
        main.row(0).col(0).setParams(height(20));
        main.row(1).col(0).insert(" ID: " + new Font(Color.GOLD, npcId).build() + " | " + new Font(Color.GREN, npc.getName()).build());
        main.row(2).col(0).insert(table);

        basePage(player, npcId,npcObjectId, main, "");
    }
    public static void showSkills(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        String skillType = args[2];

        final L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        final List<L2Skill> activeSkills = npc.getAllSkills().stream().filter(L2Skill::isActive).collect(Collectors.toList());
        final List<L2Skill> passiveSkills = npc.getAllSkills().stream().filter(L2Skill::isPassive).collect(Collectors.toList());

        final Table header = new Table(1, 2);
        header.row(0).col(0).setParams(align(CENTER), valign(CENTER), width(150)).insert(new Button("Active Skills", actionCom(admin_npc_editor_skills, npcId + " active"), 100, 32));
        header.row(0).col(1).setParams(align(CENTER), valign(CENTER), width(150)).insert(new Button("Passive Skills", actionCom(admin_npc_editor_skills, npcId + " passive"), 100, 32));

        final Table skillsTable = new Table(2, 1);
        if ("active".equals(skillType)){
            skillsTable.row(0).col(0).setParams(valign(CENTER), align(CENTER)).insert("Active Skills");
            skillsTable.row(1).col(0).insert(skillTable(npcId, activeSkills));
        }else if ("passive".equals(skillType)){
            skillsTable.row(0).col(0).setParams(valign(CENTER), align(CENTER)).insert("Passive Skills");
            skillsTable.row(1).col(0).insert(skillTable(npcId, passiveSkills));
        }

        final Table addSkillTable = new Table(1, 5);
        addSkillTable.row(0).col(0).setParams(width(20), valign(CENTER), align(LEFT)).insert("id");
        addSkillTable.row(0).col(1).insert(new Edit("id", 50, 12, EditType.num, 12));
        addSkillTable.row(0).col(2).setParams(width(35), valign(CENTER), align(LEFT)).insert("Level");
        addSkillTable.row(0).col(3).insert(new Edit("level", 85, 12, EditType.num, 12));
        addSkillTable.row(0).col(4).insert(new Button("Добавить", actionCom(admin_npc_editor_add_skills, npcId + " $id $level"), 80, 20));

        final Table table = new Table(4, 1);
        table.row(0).col(0).insert(header);
        table.row(1).col(0).insert(skillsTable);
        table.row(2).col(0).insert(addSkillTable);
        table.row(3).col(0).setParams(height(20));

        basePage(player, npcId,0, table,"");
    }
    public static void showDrop(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        String DROPLIST = args[2];

        final Table buttons = new Table(1, 3);
        buttons.row(0).col(0).setParams(width(150), align(CENTER), valign(CENTER)).insert(new Button("DROP", actionCom(admin_npc_editor_drop, npcId + " DROP"), 100, 32));
        buttons.row(0).col(1).setParams(width(150), align(CENTER), valign(CENTER)).insert(new Button("SPOIL", actionCom(admin_npc_editor_drop, npcId + " SPOIL"), 100, 32));
        buttons.row(0).col(2).setParams(width(150), align(CENTER), valign(CENTER)).insert(new Button("HERBS", actionCom(admin_npc_editor_drop, npcId + " HERBS"), 100, 32));

        final Table table = new Table(5, 1);
        table.row(0).col(0).insert(buttons);
        table.row(1).col(0).setParams(valign(CENTER), align(CENTER)).insert(new Font(Color.GREN, DROPLIST));
        switch (DROPLIST) {
            case "DROP":
                final List<DroplistResource> drop = dropListResource
                        .findList(new Filter().WHERE(DroplistResource.MOB_ID, npcId).AND(DroplistResource.SWEEP, 0))
                        .stream()
                        .filter(droplistResource -> !ItemTemplates.getInstance().getTemplate(droplistResource.getItemId()).isHerb())
                        .collect(Collectors.toList());

                table.row(2).col(0).insert(dropTable(npcId, drop));
                break;
            case "SPOIL":
                final List<DroplistResource> sweep = dropListResource.findList(new Filter().WHERE(DroplistResource.MOB_ID, npcId).AND(DroplistResource.SWEEP, 1));
                table.row(2).col(0).insert(dropTable(npcId, sweep));
                break;
            case "HERBS":
                final List<DroplistResource> herbs = dropListResource
                        .findList(new Filter().WHERE(DroplistResource.MOB_ID, npcId).AND(DroplistResource.SWEEP, 0))
                        .stream()
                        .filter(droplistResource -> ItemTemplates.getInstance().getTemplate(droplistResource.getItemId()).isHerb())
                        .collect(Collectors.toList());
                table.row(2).col(0).insert(dropTable(npcId, herbs));
                break;
        }
        basePage(player, npcId,0, table, "");
    }
    public static void showVisual(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final Table table = new Table(7, 2);
        basePage(player, npcId,0, table, new Button("Сохранить", actionCom(admin_npc_editor_save_visual, ""), 100, 20).build());
    }
    public static void showElements(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final NpcElementResource npc = npcElementResource.find(new Filter().WHERE(NpcElementResource.ID, npcId));
        final Table table = new Table(7, 2);
        basePage(player, npcId,0, table, new Button("Сохранить", actionCom(admin_npc_editor_save_base_elements, ""), 100, 20).build());
    }
    public static void showLocation(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int npcObjectId = Integer.parseInt(args[2]);
        String params = npcId + " " + npcObjectId;

        final Table table = new Table(7, 2);
        basePage(player, npcId,0, table, new Button("Сохранить", actionCom(admin_npc_editor_save_location, params), 100, 20).build());
    }
    public static void showOther(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final Table table = new Table(7, 2);
        basePage(player, npcId,0, table, new Button("Сохранить", actionCom(admin_npc_editor_save_other, ""), 100, 20).build());
    }
    public static void showNpcCreatePage(L2Player player, String[] args) {
        String name = args[1];
        int level = Integer.parseInt(args[2]);
        INSTANCE_TYPE instanceType = INSTANCE_TYPE.valueOf(args[3]);
        AI_TYPE ai_type = AI_TYPE.valueOf(args[4]);

        final Table table = new Table(1, 9);
        table.row(0).col(0).setParams(width(50), valign(CENTER), align(RIGHT)).insert("Name: ");
        table.row(0).col(1).setParams(width(100)).insert(new Edit("name", 100, 12, EditType.text, 20));
        table.row(0).col(2).setParams(width(50),valign(CENTER), align(RIGHT)).insert("Level: ");
        table.row(0).col(3).insert(new Edit("level", 40, 12, EditType.num, 2));
        table.row(0).col(4).setParams(width(80),valign(CENTER), align(RIGHT)).insert("Inst.Type: ");
        table.row(0).col(5).insert(new Combobox("instance_type", Arrays.stream(INSTANCE_TYPE.values()).map(Enum::name).collect(Collectors.toList())).setParams(width(100)));
        table.row(0).col(6).setParams(width(50),valign(CENTER), align(RIGHT)).insert("AI Type: ");
        table.row(0).col(7).insert(new Combobox("ai_type", Arrays.stream(AI_TYPE.values()).map(Enum::name).collect(Collectors.toList())).setParams(width(100)));
        table.row(0).col(8).insert(new Button("Reload", actionCom(admin_npc_editor_create,"$name $level $instance_type $ai_type"), 100, 32));

        Table main = new Table(3, 1);
        main.row(0).col(0).setParams(align(CENTER)).insert("<br>" + new Font(Color.RED, "ВНИМАНИЕ!!! Все поля обязательны").build());
        main.row(1).col(0).setParams(align(CENTER)).insert(table);

        CBWindow(player, main, window_titel);
    }

    public static void saveMainStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        String stat = args[2];
        String statValue = args[3];
        final NpcResource npcModel = npcResource.find(new Filter().WHERE(NpcResource.ID, npcId));
        npcModel.update(stat, statValue);
//        NpcEditorRepository.updateNpcStat(npcModel, stat);
//        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
//        final L2NpcTemplate template = NpcTable.getTemplate(npcId);

//        reload(npc);
        showMainStats(player, args);
    }
    public static void saveBaseStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        String stat = args[2];
        String statValue = args[3];
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
//        reload(npc);
        showBaseStats(player,args);
    }
    public static void addSkills(L2Player player, String[] args) throws ResourceProvideException {
        int npcId = Integer.parseInt(args[1]);
        int skillId = Integer.parseInt(args[2]);
        int skillLevel = Integer.parseInt(args[3]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
        if (skill != null){

            final NpcskillsResource newSkill = npcSkillsResource.create(new NpcskillsBuilder().withNpcid(npcId).withSkillid(skillId).withLevel(skillLevel));

//            NpcTable.getTemplate(npcId).addSkill(skill);
            npc.addSkill(skill);
//            NpcEditorRepository.addSkill(npc, skill);
//            reload(npc);
            showSkills(player, new String[]{args[0], args[1], skill.isActive() ? "active" : "passive"});
        }
    }
    public static void removeSkill(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int skillId = Integer.parseInt(args[2]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);

        final NpcskillsResource npcskillsResource = npcSkillsResource.find(new Filter().WHERE(NpcskillsResource.NPCID, npcId).AND(NpcskillsResource.SKILLID, skillId));
        npcskillsResource.delete();

//        NpcTable.getTemplate(npcId).getSkills().remove(skillId);
        npc.removeSkillById(skillId);
//        NpcEditorRepository.removeSkill(npc, skillId);

        showSkills(player, new String[]{args[0], args[1], skill.isActive() ? "active" : "passive"});
    }
    public static void addDrop(L2Player player, String[] args) throws ResourceProvideException {
        int npcId = Integer.parseInt(args[1]);
        int itemId = Integer.parseInt(args[2]);
        int sweep = Integer.parseInt(args[3]);
        int category = Integer.parseInt(args[4]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        final DroplistBuilder droplistBuilder = new DroplistBuilder()
                .withMobId(npcId)
                .withItemId(itemId)
                .withCategory(category)
                .withSweep(sweep);

        final DroplistResource newDrop = dropListResource.create(droplistBuilder);

        L2NpcTemplate template = NpcTable.getTemplate(npcId);
        final L2DropData dropData = new L2DropData();

        if (ItemTemplates.getInstance().getTemplate(itemId).isCommonItem()) {
            dropData.setItemId(newDrop.getItemId());
            dropData.setChance(newDrop.getChance() * ConfigValue.RateDropCommonItems);
        } else {
            dropData.setItemId(newDrop.getItemId());
            dropData.setChance(newDrop.getChance() );
        }
        dropData.setMinDrop(newDrop.getMin());
        dropData.setMaxDrop(newDrop.getMax());
        dropData.setSweep(newDrop.getSweep() == 1);
        if (dropData.getItem().isArrow() || dropData.getItemId() == 1419)
            dropData.setGroupId(Byte.MAX_VALUE); // группа для нерейтуемых предметов, сюда же надо всякую фигню
        else
            dropData.setGroupId(newDrop.getCategory());

        if (ConfigValue.EnableModDrop) {
            dropData.setIsRate(true);
            dropData.setIsPremium(true);
        }

        template.addDropData(dropData);


//        NpcRepository.addDrop(npcId, itemId);
//        reload(npc);
        showDrop(player, args);
    }
    public static void removeDrop(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int itemId = Integer.parseInt(args[2]);
        int sweep = Integer.parseInt(args[3]);
        int category = Integer.parseInt(args[4]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        final DroplistResource drop = dropListResource.find(new Filter()
                .WHERE(DroplistResource.MOB_ID, npcId)
                .AND(DroplistResource.ITEM_ID, itemId)
                .AND(DroplistResource.SWEEP, sweep)
                .AND(DroplistResource.CATEGORY, category));

        L2NpcTemplate template = NpcTable.getTemplate(npcId);

        final GArray<L2DropGroup> groups = sweep == 1 ? template.getDropData().getSpoil() : template.getDropData().getNormal();
        final L2DropGroup dropGroup = groups.stream().filter(l2DropGroup -> l2DropGroup.getId() == category).findFirst().orElse(null);

        if (dropGroup != null){
            final GArray<L2DropData> dropItems = dropGroup.getDropItems(true);
            dropItems.stream()
                    .filter(d -> d.getItemId() == itemId)
                    .findFirst()
                    .ifPresent(d -> dropGroup.getDropItems(false).remove(d));
        }
        drop.delete();

//        NpcEditorRepository.removeDrop(npcId, itemId, isSpoil);
//        reload(npc);
        showDrop(player, args);
    }
    public static void saveVisual(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

//        NpcEditorRepository.updateVisualStats(npc);
//        reload(npc);
        showVisual(player, args);
    }
    public static void saveElements(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        String stat = args[2];
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
//        reload(npc);
        showElements(player, args);
    }
    public static void saveLocation(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int npcObjectId = Integer.parseInt(args[2]);
//        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        L2NpcInstance npc = L2ObjectsStorage.getNpc(npcObjectId);
        if (npc != null){
            player.setInteractMode(InteractMode.TELEPORT_NPC_TO_CURSOR);
            player.setInteractNpc(npc);
        }
//        reload(npc);
        showLocation(player, args);
    }
    public static void saveOther(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

//        NpcEditorRepository.updateOtherStats(npc);
//        reload(npc);
        showOther(player, args);
    }

    public static void setInteractMod(L2Player player, String[] args){
        int npcId = Integer.parseInt(args[1]);
        int npcObjectId = Integer.parseInt(args[2]);
//        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        L2NpcInstance npc = L2ObjectsStorage.getNpc(npcObjectId);
        if (npc != null){
            player.setInteractMode(InteractMode.TELEPORT_NPC_TO_CURSOR);
            player.setInteractNpc(npc);
        }
    }

    public static void setNpcLocation(L2Player player, String[] args){
        final Location location = player.getInteractLocation();



    }


    public static void reload(L2NpcInstance npc){

    }


    public static void testMovePet(L2Player player, String[] strings) {
        player.setInteractMode(InteractMode.PET_MOVE_TO);
    }
}
