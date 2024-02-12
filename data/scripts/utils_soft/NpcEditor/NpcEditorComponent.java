package utils_soft.NpcEditor;

import l2open.common.HtmlBuilder.*;
import l2open.common.HtmlBuilder.parameters.Color;
import l2open.common.HtmlBuilder.parameters.EditType;
import l2open.config.ConfigValue;
import l2open.database.schemes.resources.*;
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
import l2open.database.exceptions.ResourceProvideException;
import l2open.database.schemes.builders.DroplistBuilder;
import l2open.database.schemes.builders.NpcskillsBuilder;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static l2open.common.HtmlBuilder.parameters.Parameters.*;
import static l2open.common.HtmlBuilder.components.Position.*;
import static utils_soft.NpcEditor.NpcEditorCommands.*;
import static utils_soft.NpcEditor.NpcEditorUtils.*;

public class NpcEditorComponent extends Component {

    private static final String backMainBypass = "admin_npc_editor null 0 1";
    private static final String window_titel = "Npc Editor";
    protected static Logger _log = Logger.getLogger(NpcEditorComponent.class.getName());

    public NpcEditorComponent() {
    }

    public static void basePage(L2Player player, int npcId, int npcObjectId, Table table, String saveButton) {
        final Table main = new Table(2, 2);
        main.row(0).col(0).insert(headerTable(npcId, npcObjectId));
        main.row(0).col(1).insert(table);
        main.row(1).col(1).setParams(align(CENTER)).insert(saveButton);
        CBWindow(player, main, window_titel);
    }

    public static Table headerTable(int npcId, int npcObjectId) {
        String params = npcId + " " + npcObjectId;
        final Table headerTable = new Table(8, 1);
        headerTable.row(0).col(0).insert(new Button("HOME", actionCom(admin_npc_editor, "none null 0"), 150, 36));
        headerTable.row(1).col(0).setParams(width(150)).insert(new Button("MainStats", actionCom(admin_npc_editor_main_stats, params), 150, 36));
        headerTable.row(2).col(0).insert(new Button("Skills", actionCom(admin_npc_editor_skills, npcId + " active"), 150, 36));
        headerTable.row(3).col(0).insert(new Button("Drop", actionCom(admin_npc_editor_drop, npcId + " DROP"), 150, 36));
        headerTable.row(4).col(0).insert(new Button("Visual", actionCom(admin_npc_editor_visual, params), 150, 36));
        headerTable.row(5).col(0).insert(new Button("Resists", actionCom(admin_npc_editor_elements, params), 150, 36));
        headerTable.row(6).col(0).insert(new Button("Location", actionCom(admin_npc_editor_location, params), 150, 36));
        headerTable.row(7).col(0).insert(new Button("Other", actionCom(admin_npc_editor_other, params), 150, 36));
        return headerTable;
    }

    public static Table skillTable(int npcId, List<L2Skill> skills) {
        final Table skillsTable = new Table(skills.size(), 5).setParams(cellpadding(0));
        for (int i = 0; i < skills.size(); i++) {
            L2Skill skill = skills.get(i);
            skillsTable.row(i).col(0).setParams(fixwidth(40)).insert(new Img(skill.getIcon()));
            skillsTable.row(i).col(1).setParams(fixwidth(200)).setParams(valign(CENTER), align(LEFT)).insert(formatSkillName(skill.getName()));
            skillsTable.row(i).col(2).setParams(fixwidth(40)).setParams(valign(CENTER), align(CENTER)).insert(skill.getId());
            skillsTable.row(i).col(3).setParams(fixwidth(40)).setParams(valign(CENTER), align(CENTER)).insert(skill.getLevel());
            skillsTable.row(i).col(4).setParams(fixwidth(40)).insert(new Button("X", actionCom(admin_npc_editor_remove_skill, npcId + " " + skill.getId())));
        }
        return skillsTable;
    }

    public static Table dropTable(int npcId, List<DroplistResource> dropItems) {

        final Map<Integer, List<DroplistResource>> droplistByCategory = partitionDroplistByCategory(dropItems);
        final Table main = new Table(droplistByCategory.size() + 2, 1).setParams(cellspacing(5),border(1));
        final Table header = new Table(1, 7);
        header.row(0).col(0).setParams(width(40), height(20));
        header.row(0).col(1).setParams(fixwidth(200)).setParams(align(LEFT)).insert("Item name");
        header.row(0).col(2).setParams(width(40)).insert("min");
        header.row(0).col(3).setParams(width(40)).insert("max");
        header.row(0).col(4).setParams(width(30)).insert("cat");
        header.row(0).col(5).setParams(width(50)).insert("chance");
        header.row(0).col(6).setParams(width(40));

        main.row(0).col(0).insert(header);
        int index = 1;
        for (Map.Entry<Integer, List<DroplistResource>> listEntry : droplistByCategory.entrySet()){
            final List<DroplistResource> list = listEntry.getValue()
                    .stream().sorted(Comparator.comparingInt(DroplistResource::getChance).reversed())
                    .collect(Collectors.toList());

            final Table dropTable = new Table(list.size(), 7).setParams(cellpadding(0));
            for (int i = 0; i < list.size(); i++) {
                final DroplistResource dropItem = list.get(i);
                final L2Item template = ItemTemplates.getInstance().getTemplate(dropItem.getItemId());
                final float chance = dropItem.getChance() / 10000f;
                dropTable.row(i).col(0).setParams(width(40)).insert(new Img(template.getIcon()));
                dropTable.row(i).col(1).setParams(fixwidth(200)).setParams(valign(CENTER), align(LEFT)).insert(formatItemName(template.getName()));
                dropTable.row(i).col(2).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(String.valueOf(dropItem.getMin()));
                dropTable.row(i).col(3).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(String.valueOf(dropItem.getMax()));
                dropTable.row(i).col(4).setParams(width(30)).setParams(valign(CENTER), align(CENTER)).insert(dropItem.getCategory());
                dropTable.row(i).col(5).setParams(width(50)).setParams(valign(CENTER), align(CENTER)).insert(new DecimalFormat("#0.0000").format(chance) + "%");
                dropTable.row(i).col(6).setParams(width(40)).insert(new Button("X", actionCom(admin_npc_editor_remove_drop, npcId + " " + dropItem.getItemId() + " " + (dropItem.getSweep() == 1))));
            }
            main.row(index).col(0).insert(dropTable);
            index++;
        }


//        final Table addDropTable = new Table(1, 5);
//        addDropTable.row(0).col(0).setParams(width(20), valign(CENTER), align(LEFT)).insert("id");
//        addDropTable.row(0).col(1).insert(new Edit("id", 50, 12, EditType.num, 12));
//        addDropTable.row(0).col(2).setParams(width(35), valign(CENTER), align(LEFT)).insert("Level");
//        addDropTable.row(0).col(3).insert(new Edit("level", 85, 12, EditType.num, 12));
//        addDropTable.row(0).col(4).insert(new Button("Добавить", actionCom(admin_npc_editor_add_drop, npcId + " $id $level"), 80, 20));





//        main.row(2).col(0).insert(addDropTable);
        main.row(index).col(0).setParams(height(20));
        return main;
    }

    public static Table parametersTableEdit(String name, String value, NpcEditorCommands command, String npcParams) {
        final Table table = new Table(1, 4).setParams(border(0));
        table.row(0).col(0).setParams(align(RIGHT), valign(CENTER), width(100)).insert(new Font(Color.BROWN, name + ": "));
        table.row(0).col(1).setParams(align(LEFT), valign(CENTER), width(50)).insert(new Font(Color.BLUE, value));
        table.row(0).col(2).setParams(align(RIGHT), valign(CENTER), width(80)).insert("<br>" + new Edit(name, 80, 12, EditType.num, 12).build());
        table.row(0).col(3).setParams(align(RIGHT), valign(CENTER)).insert(new Button("OK", actionCom(command, npcParams + " " + name + " $" + name)));
        return table;
    }

    public static Table parametersTableCombobox(String name, String value, NpcEditorCommands command, String npcParams, List<String> boxValue) {
        final Table table = new Table(1, 4).setParams(border(0));
        table.row(0).col(0).setParams(align(RIGHT), valign(CENTER), width(100)).insert(new Font(Color.BROWN, name + ": "));
        table.row(0).col(1).setParams(align(LEFT), valign(CENTER), width(50)).insert(new Font(Color.BLUE, value));
        table.row(0).col(2).setParams(align(RIGHT), valign(CENTER), width(80)).insert("<br>" + new Combobox(name, boxValue).setParams(width(80)).build());
        table.row(0).col(3).setParams(align(RIGHT), valign(CENTER)).insert(new Button("OK", actionCom(command, npcParams + " " + name + " $" + name)));
        return table;
    }

    public static void showMainPage(L2Player player, String[] args) {
        String filter = args[1];
        String filterValue = args[2].equals("0") ? "" : args[2].equals("null") ? "" : args[2];
        int page = args.length < 4 ? 0 : Integer.parseInt(args[3]);

        List<NpcResource> npcList = NpcEditorResource.getInstance().getNpcFilteredList(filter, filterValue, 17, page * 17);

        final Table mainTable = new Table(2, 1);

        final Table filterTable = new Table(2, 7);
        filterTable.row(0).col(0).setParams(height(10));
        filterTable.row(1).col(0).setParams(width(50)).setParams(align(RIGHT), valign(CENTER)).insert(new Font(Color.GREN, "Поиск:"));
        filterTable.row(1).col(1).setParams(width(100)).setParams(align(LEFT), valign(BOTTOM)).insert("<br>" + new Edit("find", 100, 12, EditType.text, 12).build());
        filterTable.row(1).col(2).setParams(width(60)).setParams(valign(CENTER)).insert(new Button("Id", actionCom(admin_npc_editor, "npcid $find 0"), 50, 32));
        filterTable.row(1).col(3).setParams(width(60)).setParams(valign(CENTER)).insert(new Button("Name", actionCom(admin_npc_editor, "npcname $find 0"), 50, 32));
        filterTable.row(1).col(4).setParams(width(60)).setParams(valign(CENTER)).insert(new Button("Type", actionCom(admin_npc_editor, "npctype $find 0"), 50, 32));
        filterTable.row(1).col(5).setParams(width(250), valign(CENTER), align(LEFT)).insert(new Font(Color.GRAY, "filter by: ").build() + new Font(Color.BROWN, filter).build() + " | " + new Font(Color.BLUE, filterValue).build());
        filterTable.row(1).col(6).setParams(width(100)).insert(new Button("Create New NPC", actionCom(admin_npc_editor_create, "Empty 1 NPC FIGHTER"), 100, 32));


        final Table npcTable = new Table(2, 1);

        final Table pages = new Table(1, 3);
        pages.row(0).col(0).insert(new Button("<<<", actionCom(admin_npc_editor, filter + " " + filterValue + " " + (page == 0 ? 0 : page - 1)), 60, 20));
        pages.row(0).col(1).setParams(width(100), align(CENTER)).insert(new Font(Color.GOLD, "page:" + page));
        pages.row(0).col(2).insert(new Button(">>>", actionCom(admin_npc_editor, filter + " " + filterValue + " " + (page + 1)), 60, 20));

        final Table table = new Table(npcList.size(), 5).setParams(border(1));
        for (int i = 0; i < npcList.size(); i++) {
            final NpcResource npcModel = npcList.get(i);
            Font type;
            switch (npcModel.getType()) {
                case "L2Monster": {
                    type = new Font(Color.BROWN, npcModel.getType());
                    break;
                }
                case "L2RaidBoss": {
                    type = new Font(Color.BLUE, npcModel.getType());
                    break;
                }
                case "L2Minion": {
                    type = new Font(Color.GREN, npcModel.getType());
                    break;
                }
                case "L2Npc": {
                    type = new Font(Color.GRAY, npcModel.getType());
                    break;
                }
                default:
                    type = new Font(Color.GOLD, npcModel.getType());
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

    public static void showMainStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int npcObjectId = Integer.parseInt(args[2]);

        String npcParams = npcId + " " + npcObjectId;
        final NpcResource npc = NpcEditorResource.getInstance().getNpcResource().find(new Filter().WHERE(NpcResource.ID, npcId));
        final Table table = new Table(20, 2);
        table.row(0).col(0).insert(parametersTableEdit(NpcResource.LEVEL, String.valueOf(npc.getLevel()), admin_npc_editor_save_main_stats, npcParams));
        table.row(1).col(0).insert(separator(200));
        table.row(2).col(0).insert(parametersTableEdit(NpcResource.HP, String.valueOf(npc.getHp()), admin_npc_editor_save_main_stats, npcParams));
        table.row(2).col(1).insert(parametersTableEdit(NpcResource.MP, String.valueOf(npc.getMp()), admin_npc_editor_save_main_stats, npcParams));
        table.row(3).col(0).insert(separator(200));
        table.row(4).col(0).insert(parametersTableEdit(NpcResource.PATK, String.valueOf(npc.getPatk()), admin_npc_editor_save_main_stats, npcParams));
        table.row(4).col(1).insert(parametersTableEdit(NpcResource.PDEF, String.valueOf(npc.getPdef()), admin_npc_editor_save_main_stats, npcParams));
        table.row(5).col(0).insert(separator(200));
        table.row(6).col(0).insert(parametersTableEdit(NpcResource.MATK, String.valueOf(npc.getMatk()), admin_npc_editor_save_main_stats, npcParams));
        table.row(6).col(1).insert(parametersTableEdit(NpcResource.MDEF, String.valueOf(npc.getMdef()), admin_npc_editor_save_main_stats, npcParams));
        table.row(7).col(0).insert(separator(200));
        table.row(8).col(0).insert(parametersTableEdit(NpcResource.ATKSPD, String.valueOf(npc.getAtkspd()), admin_npc_editor_save_main_stats, npcParams));
        table.row(8).col(1).insert(parametersTableEdit(NpcResource.MATKSPD, String.valueOf(npc.getMatkspd()), admin_npc_editor_save_main_stats, npcParams));
        table.row(9).col(0).insert(separator(200));
        table.row(10).col(0).insert(parametersTableEdit(NpcResource.WALKSPD, String.valueOf(npc.getWalkspd()), admin_npc_editor_save_main_stats, npcParams));
        table.row(10).col(1).insert(parametersTableEdit(NpcResource.RUNSPD, String.valueOf(npc.getRunspd()), admin_npc_editor_save_main_stats, npcParams));
        table.row(11).col(0).insert(separator(200));
        table.row(12).col(0).insert(parametersTableEdit(NpcResource.EXP, String.valueOf(npc.getExp()), admin_npc_editor_save_main_stats, npcParams));
        table.row(12).col(1).insert(parametersTableEdit(NpcResource.SP, String.valueOf(npc.getSp()), admin_npc_editor_save_main_stats, npcParams));
        table.row(13).col(0).insert(separator(200));
        table.row(14).col(0).insert(parametersTableEdit(NpcResource.STR, String.valueOf(npc.getStr()), admin_npc_editor_save_main_stats, npcParams));
        table.row(14).col(1).insert(parametersTableEdit(NpcResource.CON, String.valueOf(npc.getCon()), admin_npc_editor_save_main_stats, npcParams));
        table.row(15).col(0).insert(separator(200));
        table.row(16).col(0).insert(parametersTableEdit(NpcResource.DEX, String.valueOf(npc.getDex()), admin_npc_editor_save_main_stats, npcParams));
        table.row(16).col(1).insert(parametersTableEdit(NpcResource._INT, String.valueOf(npc.getInt()), admin_npc_editor_save_main_stats, npcParams));
        table.row(17).col(0).insert(separator(200));
        table.row(18).col(0).insert(parametersTableEdit(NpcResource.WIT, String.valueOf(npc.getWit()), admin_npc_editor_save_main_stats, npcParams));
        table.row(18).col(1).insert(parametersTableEdit(NpcResource.MEN, String.valueOf(npc.getMen()), admin_npc_editor_save_main_stats, npcParams));

        final Table main = new Table(3, 1);
        main.row(0).col(0).setParams(height(20));
        main.row(1).col(0).insert(" ID: " + new Font(Color.GOLD, npcId).build() + " | " + new Font(Color.GREN, npc.getName()).build());
        main.row(2).col(0).insert(table);

        basePage(player, npcId, npcObjectId, main, "");
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
        if ("active".equals(skillType)) {
            skillsTable.row(0).col(0).setParams(valign(CENTER), align(CENTER)).insert("Active Skills");
            skillsTable.row(1).col(0).insert(skillTable(npcId, activeSkills));
        } else if ("passive".equals(skillType)) {
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

        basePage(player, npcId, 0, table, "");
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
                final List<DroplistResource> drop = NpcEditorResource.getInstance().getDropListResource()
                        .findList(new Filter().WHERE(DroplistResource.MOB_ID, npcId).AND(DroplistResource.SWEEP, 0).ORDER_BY(DroplistResource.CATEGORY))
                        .stream()
                        .filter(droplistResource -> !ItemTemplates.getInstance().getTemplate(droplistResource.getItemId()).isHerb())
                        .collect(Collectors.toList());

                table.row(2).col(0).insert(dropTable(npcId, drop));
                break;
            case "SPOIL":
                final List<DroplistResource> sweep = NpcEditorResource.getInstance()
                        .getDropListResource()
                        .findList(new Filter().WHERE(DroplistResource.MOB_ID, npcId).AND(DroplistResource.SWEEP, 1).ORDER_BY(DroplistResource.CATEGORY));

                table.row(2).col(0).insert(dropTable(npcId, sweep));
                break;
            case "HERBS":
                final List<DroplistResource> herbs = NpcEditorResource.getInstance()
                        .getDropListResource()
                        .findList(new Filter().WHERE(DroplistResource.MOB_ID, npcId).AND(DroplistResource.SWEEP, 0).ORDER_BY(DroplistResource.CATEGORY))
                        .stream()
                        .filter(droplistResource -> ItemTemplates.getInstance().getTemplate(droplistResource.getItemId()).isHerb())
                        .collect(Collectors.toList());

                table.row(2).col(0).insert(dropTable(npcId, herbs));
                break;
        }
        basePage(player, npcId, 0, table, "");
    }

    public static void showVisual(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final Table table = new Table(7, 2);
        basePage(player, npcId, 0, table,"");
    }

    public static void showElements(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int npcObjectId = Integer.parseInt(args[2]);
        String npsParams = npcId + " " + npcObjectId;
        final NpcResource npc = NpcEditorResource.getInstance().getNpcResource().find(new Filter().WHERE(NpcResource.ID, npcId));
        final NpcElementResource npcElement = NpcEditorResource.getInstance().getNpcElementResource().find(new Filter().WHERE(NpcElementResource.ID, npcId));
        final Table table = new Table(20, 2);
        final ArrayList<String> elements = new ArrayList<String>() {{
            add("NONE");
            add("Fire");
            add("Water");
            add("Earth");
            add("Wind");
            add("Holy");
            add("Dark");
        }};
        String attackElement = elements.get(npcElement.getAtkElement() + 1);
        table.row(2).col(0).insert(parametersTableCombobox(NpcElementResource.ATK_ELEMENT, attackElement, admin_npc_editor_save_base_elements, npsParams, elements));
        table.row(2).col(1).insert(parametersTableEdit(NpcElementResource.ELEM_ATK_POWER, String.valueOf(Math.abs(npcElement.getElemAtkPower())), admin_npc_editor_save_base_elements, npsParams));
        table.row(3).col(0).insert(separator(200));
        table.row(4).col(0).insert(parametersTableEdit(NpcElementResource.FIRE_RES, String.valueOf(Math.abs(npcElement.getFireRes())), admin_npc_editor_save_base_elements, npsParams));
        table.row(4).col(1).insert(parametersTableEdit(NpcElementResource.WATER_RES, String.valueOf(Math.abs(npcElement.getWaterRes())), admin_npc_editor_save_base_elements, npsParams));
        table.row(5).col(0).insert(separator(200));
        table.row(6).col(0).insert(parametersTableEdit(NpcElementResource.EARTH_RES, String.valueOf(Math.abs(npcElement.getEarthRes())), admin_npc_editor_save_base_elements, npsParams));
        table.row(6).col(1).insert(parametersTableEdit(NpcElementResource.WIND_RES, String.valueOf(Math.abs(npcElement.getWindRes())), admin_npc_editor_save_base_elements, npsParams));
        table.row(7).col(0).insert(separator(200));
        table.row(8).col(0).insert(parametersTableEdit(NpcElementResource.HOLY_RES, String.valueOf(Math.abs(npcElement.getHolyRes())), admin_npc_editor_save_base_elements, npsParams));
        table.row(8).col(1).insert(parametersTableEdit(NpcElementResource.DARK_RES, String.valueOf(Math.abs(npcElement.getDarkRes())), admin_npc_editor_save_base_elements, npsParams));

        final Table main = new Table(3, 1);
        main.row(0).col(0).setParams(height(20));
        main.row(1).col(0).insert(" ID: " + new Font(Color.GOLD, npcId).build() + " | " + new Font(Color.GREN, npc.getName()).build());
        main.row(2).col(0).insert(table);
        basePage(player, npcId, 0, table, new Button("Сохранить", actionCom(admin_npc_editor_save_base_elements, ""), 100, 20).build());
    }

    public static void showLocation(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int npcObjectId = Integer.parseInt(args[2]);
        String params = npcId + " " + npcObjectId;

        final Table table = new Table(7, 2);

        final L2NpcInstance npc = L2ObjectsStorage.getNpc(npcObjectId);
        if (npc!= null){
            final SpawnlistResource npcSpawn = NpcEditorResource.getInstance().getNpcSpawn(npc);
            if (npcSpawn != null){
                final Table table1 = new Table(1, 6);
                table1.row(0).col(0).setParams(fixwidth(100)).insert(npcSpawn.getNpcTemplateid());
                table1.row(0).col(1).setParams(fixwidth(100)).insert(npcSpawn.getLocx());
                table1.row(0).col(2).setParams(fixwidth(100)).insert(npcSpawn.getLocy());
                table1.row(0).col(3).setParams(fixwidth(100)).insert(npcSpawn.getLocz());
                table1.row(0).col(4).setParams(fixwidth(100)).insert(npcSpawn.getHeading());
                table1.row(0).col(4).setParams(fixwidth(100)).insert(npcSpawn.getReflection());
                table.row(1).col(0).insert(table1);

                final Table navigateTable = new Table(4, 3);
                navigateTable.row(0).col(0).insert(new Button("◤", actionCom(admin_npc_editor_save_location, params + " WA 10")));
                navigateTable.row(0).col(1).insert(new Button("▲", actionCom(admin_npc_editor_save_location, params + " W 10")));
                navigateTable.row(0).col(2).insert(new Button("◥", actionCom(admin_npc_editor_save_location, params + " WD 10")));
                navigateTable.row(1).col(0).insert(new Button("◀", actionCom(admin_npc_editor_save_location, params + " A 10")));
                navigateTable.row(1).col(2).insert(new Button("▶", actionCom(admin_npc_editor_save_location, params + " D 10")));
                navigateTable.row(2).col(0).insert(new Button("◣", actionCom(admin_npc_editor_save_location, params + " SA 10")));
                navigateTable.row(2).col(1).insert(new Button("▼", actionCom(admin_npc_editor_save_location, params + " S 10")));
                navigateTable.row(2).col(2).insert(new Button("◢", actionCom(admin_npc_editor_save_location, params + " SD 10")));
                navigateTable.row(3).col(0).insert(new Button("<<", actionCom(admin_npc_editor_save_location, params + " LEFT 1000")));
                navigateTable.row(3).col(2).insert(new Button(">>", actionCom(admin_npc_editor_save_location, params + " RIGHT 1000")));

                table.row(1).col(1).insert(navigateTable);
            }
        }

        basePage(player, npcId, 0, table, "");
    }

    public static void showOther(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final Table table = new Table(7, 2);
        basePage(player, npcId, 0, table, "");
    }

    public static void showNpcCreatePage(L2Player player, String[] args) {
        String name = args[1];
        int level = Integer.parseInt(args[2]);
        INSTANCE_TYPE instanceType = INSTANCE_TYPE.valueOf(args[3]);
        AI_TYPE ai_type = AI_TYPE.valueOf(args[4]);

        final Table table = new Table(1, 9);
        table.row(0).col(0).setParams(width(50), valign(CENTER), align(RIGHT)).insert("Name: ");
        table.row(0).col(1).setParams(width(100)).insert(new Edit("name", 100, 12, EditType.text, 20));
        table.row(0).col(2).setParams(width(50), valign(CENTER), align(RIGHT)).insert("Level: ");
        table.row(0).col(3).insert(new Edit("level", 40, 12, EditType.num, 2));
        table.row(0).col(4).setParams(width(80), valign(CENTER), align(RIGHT)).insert("Inst.Type: ");
        table.row(0).col(5).insert(new Combobox("instance_type", Arrays.stream(INSTANCE_TYPE.values()).map(Enum::name).collect(Collectors.toList())).setParams(width(100)));
        table.row(0).col(6).setParams(width(50), valign(CENTER), align(RIGHT)).insert("AI Type: ");
        table.row(0).col(7).insert(new Combobox("ai_type", Arrays.stream(AI_TYPE.values()).map(Enum::name).collect(Collectors.toList())).setParams(width(100)));
        table.row(0).col(8).insert(new Button("reload", actionCom(admin_npc_editor_create, "$name $level $instance_type $ai_type"), 100, 32));

        Table main = new Table(3, 1);
        main.row(0).col(0).setParams(align(CENTER)).insert("<br>" + new Font(Color.RED, "ВНИМАНИЕ!!! Все поля обязательны").build());
        main.row(1).col(0).setParams(align(CENTER)).insert(table);

        CBWindow(player, main, window_titel);
    }

    public static void saveMainStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int npcObjectId = Integer.parseInt(args[2]);
        String stat = args[3];
        String statValue = args[4];
        final NpcResource npcModel = NpcEditorResource.getInstance().getNpcById(npcId);
        npcModel.update(stat, statValue);
        updateNpcStat(npcId, stat, statValue);
        showMainStats(player, args);
    }

    public static void addSkills(L2Player player, String[] args) throws ResourceProvideException {
        int npcId = Integer.parseInt(args[1]);
        int skillId = Integer.parseInt(args[2]);
        int skillLevel = Integer.parseInt(args[3]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
        if (skill != null) {
            final NpcskillsResource newSkill = NpcEditorResource.getInstance().getNpcSkillsResource()
                    .create(new NpcskillsBuilder().withNpcid(npcId).withSkillid(skillId).withLevel(skillLevel));

            npc.addSkill(skill);
            showSkills(player, new String[]{args[0], args[1], skill.isActive() ? "active" : "passive"});
        }
    }

    public static void removeSkill(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int skillId = Integer.parseInt(args[2]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);

        final NpcskillsResource npcskillsResource = NpcEditorResource.getInstance().getNpcSkillsResource()
                .find(new Filter().WHERE(NpcskillsResource.NPCID, npcId).AND(NpcskillsResource.SKILLID, skillId));
        npcskillsResource.delete();

        npc.removeSkillById(skillId);

        showSkills(player, new String[]{args[0], args[1], skill.isActive() ? "active" : "passive"});
    }

    public static void addDrop(L2Player player, String[] args) throws ResourceProvideException {
        int npcId = Integer.parseInt(args[1]);
        int itemId = Integer.parseInt(args[2]);
        int sweep = Integer.parseInt(args[3]);
        int category = Integer.parseInt(args[4]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        final List<L2NpcInstance> allByNpcId = L2ObjectsStorage.getAllByNpcId(npcId, true);

        final DroplistBuilder droplistBuilder = new DroplistBuilder()
                .withMobId(npcId)
                .withItemId(itemId)
                .withCategory(category)
                .withSweep(sweep);

        final DroplistResource newDrop = NpcEditorResource.getInstance().getDropListResource().create(droplistBuilder);

        L2NpcTemplate template = NpcTable.getTemplate(npcId);
        final L2DropData dropData = new L2DropData();

        if (ItemTemplates.getInstance().getTemplate(itemId).isCommonItem()) {
            dropData.setItemId(newDrop.getItemId());
            dropData.setChance(newDrop.getChance() * ConfigValue.RateDropCommonItems);
        } else {
            dropData.setItemId(newDrop.getItemId());
            dropData.setChance(newDrop.getChance());
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
        showDrop(player, args);
    }

    public static void removeDrop(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int itemId = Integer.parseInt(args[2]);
        int sweep = Integer.parseInt(args[3]);
        int category = Integer.parseInt(args[4]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        final DroplistResource drop = NpcEditorResource.getInstance().getDropListResource().find(new Filter()
                .WHERE(DroplistResource.MOB_ID, npcId)
                .AND(DroplistResource.ITEM_ID, itemId)
                .AND(DroplistResource.SWEEP, sweep)
                .AND(DroplistResource.CATEGORY, category));

        L2NpcTemplate template = NpcTable.getTemplate(npcId);

        final GArray<L2DropGroup> groups = sweep == 1 ? template.getDropData().getSpoil() : template.getDropData().getNormal();
        final L2DropGroup dropGroup = groups.stream().filter(l2DropGroup -> l2DropGroup.getId() == category).findFirst().orElse(null);

        if (dropGroup != null) {
            final GArray<L2DropData> dropItems = dropGroup.getDropItems(true);
            dropItems.stream()
                    .filter(d -> d.getItemId() == itemId)
                    .findFirst()
                    .ifPresent(d -> dropGroup.getDropItems(false).remove(d));
        }
        drop.delete();
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
        int npcObjectId = Integer.parseInt(args[2]);
        String stat = args[3];
        String statValue = args[4];
        if (stat.equals("AtkElement")){
            switch (args[4]) {
                case "NONE": statValue = "-1";break;
                case "Fire":statValue = "0";break;
                case "Water":statValue = "1";break;
                case "Earth":statValue = "2";break;
                case "Wind":statValue = "3";break;
                case "Holy":statValue = "4";break;
                case "Dark":statValue = "5";break;
            }
        } else if (!stat.equals("elemAtkPower")) {
            statValue = "-" + statValue;
        }

        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        final NpcElementResource npcElementResource = NpcEditorResource.getInstance().getNpcElementById(npcId);
        npcElementResource.update(stat, statValue);
        updateNpcStat(npcId, stat, statValue);
        showElements(player, args);
    }

    public static void saveLocation(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int npcObjectId = Integer.parseInt(args[2]);
        String direction = args[3];
        int dist = Integer.parseInt(args[4]);
        final L2NpcInstance npc = L2ObjectsStorage.getNpc(npcObjectId);
        if (npc != null){
            final SpawnlistResource npcSpawn = NpcEditorResource.getInstance().getNpcSpawn(npc);
            if (npcSpawn != null){
                final Location loc = npc.getLoc();
                switch (direction){
                    case "WA":{
                        loc.setY(loc.y + 10);
                        loc.setX(loc.x + 10);
                        break;
                    }
                    case "W":{
                        loc.setY(loc.y + 10);
                        break;
                    }
                    case "WD":{
                        loc.setY(loc.y + 10);
                        loc.setX(loc.x - 10);
                        break;
                    }
                    case "A":{
                        loc.setX(loc.x + 10);
                        break;
                    }
                    case "D":{
                        loc.setX(loc.x - 10);
                        break;
                    }
                    case "SA":{
                        loc.setY(loc.y - 10);
                        loc.setX(loc.x + 10);
                        break;
                    }
                    case "S":{
                        loc.setY(loc.y - 10);
                        break;
                    }
                    case "SD":{
                        loc.setY(loc.y - 10);
                        loc.setX(loc.x - 10);
                        break;
                    }
                    case "LEFT":{
                        final int heading = npcSpawn.getHeading() - 1000;
                        if (heading > 0 && heading < 65535){
                            loc.setH(heading);
                        }
                        break;
                    }
                    case "RIGHT":{
                        final int heading = npcSpawn.getHeading() + 1000;
                        if (heading > 0 && heading < 65535){
                            loc.setH(heading);
                        }
                        break;
                    }
                }
                updateSpawnLoc(npc, loc, npcSpawn);
                npc.validateLocation(1);
            }
        }


//        if (npc != null) {
//            player.setInteractMode(InteractMode.TELEPORT_NPC_TO_CURSOR);
//            player.setInteractNpc(npc);
//        }
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

    public static void setInteractMod(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int npcObjectId = Integer.parseInt(args[2]);
//        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        L2NpcInstance npc = L2ObjectsStorage.getNpc(npcObjectId);
        if (npc != null) {
            player.setInteractMode(InteractMode.TELEPORT_NPC_TO_CURSOR);
            player.setInteractNpc(npc);
        }
    }

    public static void setNpcLocation(L2Player player, String[] args) {
        final Location location = player.getInteractLocation();


    }

    public static void testMovePet(L2Player player, String[] strings) {
        player.setInteractMode(InteractMode.PET_MOVE_TO);
    }
}
