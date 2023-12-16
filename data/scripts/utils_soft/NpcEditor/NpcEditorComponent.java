package utils_soft.NpcEditor;

import l2open.common.Html_Constructor.tags.*;
import l2open.common.Html_Constructor.tags.parameters.Color;
import l2open.common.Html_Constructor.tags.parameters.EditType;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;
import utils_soft.NpcEditor.enums.AI_TYPE;
import utils_soft.NpcEditor.enums.INSTANCE_TYPE;
import utils_soft.common.DatabaseResurce.Filter;
import utils_soft.common.DatabaseResurce.Resource;
import utils_soft.common.DatabaseResurce.schemes.DropListTable;
import utils_soft.common.DatabaseResurce.schemes.NpcElementTable;
import utils_soft.common.DatabaseResurce.schemes.NpcModel;
import utils_soft.common.DatabaseResurce.schemes.SpawnListTable;
import utils_soft.common.Component;
import utils_soft.common.DatabaseResurce.ResourceProvider;

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

    private final Resource<NpcElementTable> npcElementResource;


    public NpcEditorComponent() {
        npcElementResource = new ResourceProvider<>(NpcElementTable.class);
        final Filter filter = new Filter().WHERE(NpcElementTable.Id_COLUMN, "100");

        final NpcElementTable npcElementTable = npcElementResource.find(filter);
    }

    public static void basePage(L2Player player,int npcId, Table table, String saveButton) {
        final Table main = new Table(2, 2);
        main.row(0).col(0).insert(headerTable(npcId));
        main.row(0).col(1).insert(table);
        main.row(1).col(1).setParams(align(CENTER)).insert(saveButton);
        CBWindow(player, main, window_titel);
    }
    public static Table headerTable(int npcId){
        final Table headerTable = new Table(9, 1);
        headerTable.row(0).col(0).insert(new Button("HOME", actionCom(admin_npc_editor, "none null 0"), 150, 36));
        headerTable.row(1).col(0).setParams(width(150)).insert(new Button("MainStats", actionCom(admin_npc_editor_main_stats, npcId), 150, 36));
        headerTable.row(2).col(0).insert(new Button("BaseStats", actionCom(admin_npc_editor_base_stats, npcId), 150, 36));
        headerTable.row(3).col(0).insert(new Button("Skills", actionCom(admin_npc_editor_skills, npcId + " active"), 150, 36));
        headerTable.row(4).col(0).insert(new Button("Drop", actionCom(admin_npc_editor_drop, npcId + " DROP"), 150, 36));
        headerTable.row(5).col(0).insert(new Button("Visual", actionCom(admin_npc_editor_visual, npcId), 150, 36));
        headerTable.row(6).col(0).insert(new Button("Elements", actionCom(admin_npc_editor_elements, npcId), 150, 36));
        headerTable.row(7).col(0).insert(new Button("Location", actionCom(admin_npc_editor_location, npcId), 150, 36));
        headerTable.row(8).col(0).insert(new Button("Other", actionCom(admin_npc_editor_other, npcId), 150, 36));
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
    public static Table dropTable(int npcId, List<DropListTable> dropItems){
        final Table main = new Table(3, 1);
        final Table dropTable = new Table(dropItems.size(), 7).setParams(cellpadding(0));
        for (int i = 0; i < dropItems.size(); i++){
            final DropListTable dropItem = dropItems.get(i);
            final float chance = dropItem.getChance() / 10000f;
            dropTable.row(i).col(0).setParams(width(40)).insert(new Img(dropItem.getIcon()));
            dropTable.row(i).col(1).setParams(width(80)).setParams(valign(CENTER), align(LEFT)).insert(formatItemName(dropItem.getName()));
            dropTable.row(i).col(2).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(dropItem.getMin());
            dropTable.row(i).col(3).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(dropItem.getMax());
            dropTable.row(i).col(4).setParams(width(30)).setParams(valign(CENTER), align(CENTER)).insert(dropItem.getCategory());
            dropTable.row(i).col(5).setParams(width(50)).setParams(valign(CENTER), align(CENTER)).insert(new DecimalFormat("#0.00").format(chance) + "%");
            dropTable.row(i).col(6).setParams(width(40)).insert(new Button("X", actionCom(admin_npc_editor_remove_drop, npcId + " " + dropItem.getItemId() + " " + dropItem.isSpoil())));
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

        List<NpcModel> npcList;

        int offset = page * 17;

        switch (filter) {
            case "npcname":
                npcList = NpcEditorRepository.getNpcListByLikeName(filterValue, offset);
                break;
            case "npcid":
                npcList = NpcEditorRepository.getNpcListByLikeId(filterValue, offset);
                break;
            case "npctype":
                npcList = NpcEditorRepository.getNpcListByLikeType(filterValue, offset);
                break;
            default:
                npcList = NpcEditorRepository.getNpcList(offset);
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
            final NpcModel npcModel = npcList.get(i);
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
            table.row(i).col(4).insert(new Button("redact", actionCom(admin_npc_editor_main_stats, npcModel.getId()), 80, 20));
        }

        npcTable.row(0).col(0).setParams(align(CENTER)).insert(pages);
        npcTable.row(1).col(0).insert(table);
        mainTable.row(0).col(0).setParams(align(CENTER)).insert(filterTable);
        mainTable.row(1).col(0).setParams(align(CENTER)).insert(npcTable);

        CBWindow(player, mainTable, window_titel);
    }
    public static void showMainStats(L2Player player, String[] args){
        int npcId = Integer.parseInt(args[1]);
        final NpcModel npc = NpcEditorRepository.getNpc(npcId);

        final Table table = new Table(13, 2);
        table.row(0).col(0).insert(parametersTable("level", String.valueOf(npc.getLevel()), admin_npc_editor_save_main_stats, npcId));
        table.row(1).col(0).insert(separator(200));
        table.row(2).col(0).insert(parametersTable("hp", String.valueOf(npc.getHP()), admin_npc_editor_save_main_stats, npcId));
        table.row(2).col(1).insert(parametersTable("mp", String.valueOf(npc.getMP()), admin_npc_editor_save_main_stats, npcId));
        table.row(3).col(0).insert(separator(200));
        table.row(4).col(0).insert(parametersTable("patk", String.valueOf(npc.getPatk()), admin_npc_editor_save_main_stats, npcId));
        table.row(4).col(1).insert(parametersTable("pdef", String.valueOf(npc.getPdef()), admin_npc_editor_save_main_stats, npcId));
        table.row(5).col(0).insert(separator(200));
        table.row(6).col(0).insert(parametersTable("matk", String.valueOf(npc.getMatk()), admin_npc_editor_save_main_stats, npcId));
        table.row(6).col(1).insert(parametersTable("mdef", String.valueOf(npc.getMdef()), admin_npc_editor_save_main_stats, npcId));
        table.row(7).col(0).insert(separator(200));
        table.row(8).col(0).insert(parametersTable("atkspd", String.valueOf(npc.getAtkspd()), admin_npc_editor_save_main_stats, npcId));
        table.row(8).col(1).insert(parametersTable("matkspd", String.valueOf(npc.getMatkspd()), admin_npc_editor_save_main_stats, npcId));
        table.row(9).col(0).insert(separator(200));
        table.row(10).col(0).insert(parametersTable("walkspd", String.valueOf(npc.getWalkSpd()), admin_npc_editor_save_main_stats, npcId));
        table.row(10).col(1).insert(parametersTable("runspd", String.valueOf(npc.getRunSpd()), admin_npc_editor_save_main_stats, npcId));
        table.row(11).col(0).insert(separator(200));
        table.row(12).col(0).insert(parametersTable("exp", String.valueOf(npc.getExp()), admin_npc_editor_save_main_stats, npcId));
        table.row(12).col(1).insert(parametersTable("sp", String.valueOf(npc.getSP()), admin_npc_editor_save_main_stats, npcId));

        final Table main = new Table(3, 1);
        main.row(0).col(0).setParams(height(20));
        main.row(1).col(0).insert(" ID: " + new Font(Color.GOLD, npcId).build() + " | " + new Font(Color.GREN, npc.getName()).build());
        main.row(2).col(0).insert(table);

        basePage(player, npcId, main, "");
    }
    public static void showBaseStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final NpcModel npc = NpcEditorRepository.getNpc(npcId);
        final Table table = new Table(14, 1);
        table.row(0).col(0).insert(parametersTable("str", String.valueOf(npc.getSTR()), admin_npc_editor_save_main_stats, npcId));
        table.row(1).col(0).insert(parametersTable("con", String.valueOf(npc.getCON()), admin_npc_editor_save_main_stats, npcId));
        table.row(2).col(0).insert(parametersTable("dex", String.valueOf(npc.getDEX()), admin_npc_editor_save_main_stats, npcId));
        table.row(3).col(0).insert(parametersTable("int", String.valueOf(npc.getINT()), admin_npc_editor_save_main_stats, npcId));
        table.row(4).col(0).insert(parametersTable("wit", String.valueOf(npc.getWIT()), admin_npc_editor_save_main_stats, npcId));
        table.row(5).col(0).insert(parametersTable("men", String.valueOf(npc.getMEN()), admin_npc_editor_save_main_stats, npcId));

        final Table main = new Table(3, 1);
        main.row(0).col(0).setParams(height(20));
        main.row(1).col(0).insert(" ID: " + new Font(Color.GOLD, npcId).build() + " | " + new Font(Color.GREN, npc.getName()).build());
        main.row(2).col(0).insert(table);

        basePage(player, npcId, main, "");
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

        basePage(player, npcId, table,"");
    }
    public static void showDrop(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        String DROPLIST = args[2];
        final List<DropListTable> dropItems = NpcEditorRepository.getDropList(npcId);
        final List<DropListTable> dropList = dropItems.stream().filter(dropItem -> !dropItem.isSpoil()).filter(dropItem -> !dropItem.isHerb()).collect(Collectors.toList());
        final List<DropListTable> spoilList = dropItems.stream().filter(DropListTable::isSpoil).filter(dropItem -> !dropItem.isHerb()).collect(Collectors.toList());
        final List<DropListTable> herbs = dropItems.stream().filter(DropListTable::isHerb).collect(Collectors.toList());

        final Table buttons = new Table(1, 3);
        buttons.row(0).col(0).setParams(width(150), align(CENTER), valign(CENTER)).insert(new Button("DROP", actionCom(admin_npc_editor_drop, npcId + " DROP"), 100, 32));
        buttons.row(0).col(1).setParams(width(150), align(CENTER), valign(CENTER)).insert(new Button("SPOIL", actionCom(admin_npc_editor_drop, npcId + " SPOIL"), 100, 32));
        buttons.row(0).col(2).setParams(width(150), align(CENTER), valign(CENTER)).insert(new Button("HERBS", actionCom(admin_npc_editor_drop, npcId + " HERBS"), 100, 32));


        final Table table = new Table(5, 1);
        table.row(0).col(0).insert(buttons);
        table.row(1).col(0).setParams(valign(CENTER), align(CENTER)).insert(new Font(Color.GREN, DROPLIST));
        switch (DROPLIST) {
            case "DROP":
                table.row(2).col(0).insert(dropTable(npcId, dropList));
                break;
            case "SPOIL":
                table.row(2).col(0).insert(dropTable(npcId, spoilList));
                break;
            case "HERBS":
                table.row(2).col(0).insert(dropTable(npcId, herbs));
                break;
        }
        basePage(player, npcId, table, "");
    }
    public static void showVisual(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final Table table = new Table(7, 2);
        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_save_visual, ""), 100, 20).build());
    }
    public static void showElements(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final NpcElementTable npcElementModel = new NpcElementTable();
        npcElementModel.setFireRes(Rnd.get(1, 100));
        final Table table = new Table(7, 2);
        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_save_base_elements, ""), 100, 20).build());
    }
    public static void showLocation(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final Table table = new Table(7, 2);
        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_save_location, ""), 100, 20).build());
    }
    public static void showOther(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final Table table = new Table(7, 2);
        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_save_other, ""), 100, 20).build());
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

        final NpcModel build = new NpcBuilder(name, level, instanceType, ai_type).build();


        Table main = new Table(3, 1);
        main.row(0).col(0).setParams(align(CENTER)).insert("<br>" + new Font(Color.RED, "ВНИМАНИЕ!!! Все поля обязательны").build());
        main.row(1).col(0).setParams(align(CENTER)).insert(table);
        main.row(2).col(0).setParams(align(CENTER)).insert(NpcEditorRepository.maxValue("npc", "level"));


        CBWindow(player, main, window_titel);
    }

    public static void saveMainStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        String stat = args[2];
        String statValue = args[3];
        final NpcModel npcModel = NpcEditorRepository.getNpc(npcId);
        NpcEditorRepository.updateNpcStat(npcModel, stat);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
//        reload(npc);
        showMainStats(player, args);
    }
    public static void saveBaseStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        String stat = args[2];
        String statValue = args[3];
        final NpcModel npcModel = NpcEditorRepository.getNpc(npcId);
        NpcEditorRepository.updateNpcStat(npcModel, stat);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
//        reload(npc);
        showBaseStats(player,args);
    }
    public static void addSkills(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int skillId = Integer.parseInt(args[2]);
        int skillLevel = Integer.parseInt(args[3]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
        if (skill != null){
            NpcTable.getTemplate(npcId).addSkill(skill);
            npc.addSkill(skill);
            NpcEditorRepository.addSkill(npc, skill);
//            reload(npc);
            showSkills(player, new String[]{args[0], args[1], skill.isActive() ? "active" : "passive"});
        }
    }
    public static void removeSkill(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int skillId = Integer.parseInt(args[2]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
        NpcTable.getTemplate(npcId).getSkills().remove(skillId);
        npc.removeSkillById(skillId);
        NpcEditorRepository.removeSkill(npc, skillId);

        showSkills(player, new String[]{args[0], args[1], skill.isActive() ? "active" : "passive"});
    }
    public static void addDrop(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int itemId = Integer.parseInt(args[2]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

//        NpcRepository.addDrop(npcId, itemId);
//        reload(npc);
        showDrop(player, args);
    }
    public static void removeDrop(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int itemId = Integer.parseInt(args[2]);
        boolean isSpoil = Boolean.parseBoolean(args[3]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        NpcEditorRepository.removeDrop(npcId, itemId, isSpoil);
//        reload(npc);
        showDrop(player, args);
    }
    public static void saveVisual(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcEditorRepository.updateVisualStats(npc);
//        reload(npc);
        showVisual(player, args);
    }
    public static void saveElements(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        String stat = args[2];
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcElementTable npcElementModel = new NpcElementTable();

        NpcEditorRepository.updateElements(npcElementModel, stat);
//        reload(npc);
        showElements(player, args);
    }
    public static void saveLocation(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        SpawnListTable oldSpawn = NpcEditorRepository.getSpawn(npc);
        SpawnListTable newSpawn = NpcEditorRepository.getSpawn(npc);

        NpcEditorRepository.updateLocation(oldSpawn, newSpawn);
//        reload(npc);
        showLocation(player, args);
    }
    public static void saveOther(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcEditorRepository.updateOtherStats(npc);
//        reload(npc);
        showOther(player, args);
    }

    public static void reload(L2NpcInstance npc){

    }


}
