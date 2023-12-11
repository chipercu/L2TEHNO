package utils_soft.NpcEditor;

import l2open.common.Html_Constructor.tags.*;
import l2open.common.Html_Constructor.tags.parameters.Color;
import l2open.common.Html_Constructor.tags.parameters.EditType;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import utils_soft.common.Component;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;
import static l2open.common.Html_Constructor.tags.parameters.Position.*;
import static utils_soft.MultisellEditor.MultiSellCommands.admin_multisell_editor;
import static utils_soft.NpcEditor.NpcEditorCommands.*;

public class NpcEditorComponent extends Component{

    private static final String backMainBypass = "admin_npc_editor null 0 1";
    private static final String window_titel = "Npc Editor";
    protected static Logger _log = Logger.getLogger(NpcEditorComponent.class.getName());

    public NpcEditorComponent() {
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
        headerTable.row(0).col(0).insert(new Button("HOME", actionCom(admin_npc_editor, "none 0 0"), 150, 36));
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
    public static Table dropTable(int npcId, List<DropItem> dropItems){
        final Table main = new Table(3, 1);
        final Table dropTable = new Table(dropItems.size(), 7).setParams(cellpadding(0));
        for (int i = 0; i < dropItems.size(); i++){
            final DropItem dropItem = dropItems.get(i);
            final float chance = dropItem.getChance() / 10000f;
            dropTable.row(i).col(0).setParams(width(40)).insert(new Img(dropItem.getIcon()));
            dropTable.row(i).col(1).setParams(width(80)).setParams(valign(CENTER), align(LEFT)).insert(formatItemName(dropItem.getName()));
            dropTable.row(i).col(2).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(dropItem.getMin());
            dropTable.row(i).col(3).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(dropItem.getMax());
            dropTable.row(i).col(4).setParams(width(30)).setParams(valign(CENTER), align(CENTER)).insert(dropItem.getGroup());
            dropTable.row(i).col(5).setParams(width(50)).setParams(valign(CENTER), align(CENTER)).insert(new DecimalFormat("#0.00").format(chance) + "%");
            dropTable.row(i).col(6).setParams(width(40)).insert(new Button("X", actionCom(admin_npc_editor_remove_drop, npcId + " " + dropItem.getId() + " " + dropItem.getIsSpoil())));
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
        final Table table = new Table(1, 4);
        table.row(0).col(0).setParams(align(RIGHT), valign(CENTER)).insert(new Font(Color.BROWN, name));
        table.row(0).col(0).setParams(align(LEFT), valign(CENTER)).insert(new Font(Color.BLUE, value));
        table.row(0).col(0).setParams(align(RIGHT), valign(CENTER)).insert(new Edit(name, 50, 12, EditType.num, 2));
        table.row(0).col(0).setParams(align(RIGHT), valign(CENTER)).insert(new Button("OK", actionCom(command, npcId + " " + name +  " $" + name)));
        return table;
    }

    public static void showMainPage(L2Player player, String[] args) {
        String filter = args[1];
        String filterValue = args[2];
        int page = Integer.parseInt(args[3]);

        List<NpcModel> npcList;

        int offset = page * 17;

        if (filter.equals("npcname")){
            npcList = NpcEditorRepository.getNpcListByLikeName(filterValue, offset);
        }else if (filter.equals("npcid")){
            npcList = NpcEditorRepository.getNpcListByLikeId(filterValue, offset);
        }else {
            npcList = NpcEditorRepository.getNpcList(offset);
        }

        final Table mainTable = new Table(2, 1);

        final Table filterTable = new Table(2, 6);
        filterTable.row(0).col(0).setParams(height(10));
        filterTable.row(1).col(0).setParams(width(100)).setParams(align(RIGHT), valign(CENTER)).insert(new Font(Color.GREN, "Поиск:"));
        filterTable.row(1).col(1).setParams(width(150)).setParams(align(LEFT), valign(BOTTOM)).insert("<br>" + new Edit("find", 150, 12, EditType.text, 12).build());
        filterTable.row(1).col(2).setParams(width(100)).setParams(valign(CENTER)).insert(new Button("Npc Id", actionCom(admin_npc_editor,"npcid $find 0"), 100, 32));
        filterTable.row(1).col(3).setParams(width(100)).setParams(valign(CENTER)).insert(new Button("Npc Name", actionCom(admin_npc_editor,"npcname $find 0"), 100, 32));
        filterTable.row(1).col(4).setParams(width(150));
        filterTable.row(1).col(5).setParams(width(100)).insert(new Button("Create New NPC", actionCom(admin_npc_editor_create,"npcid 0 1"), 100, 32));


        final Table npcTable = new Table(2, 1);

        final Table pages = new Table(1, 3);
        pages.row(0).col(0).insert(new Button("<<<", actionCom(admin_npc_editor, filter + " " + filterValue + " " +(page == 0 ? 0 : page - 1)), 60, 20));
        pages.row(0).col(1).setParams(width(100), align(CENTER)).insert(new Font(Color.GOLD, "page:" + page));
        pages.row(0).col(2).insert(new Button(">>>", actionCom(admin_npc_editor, filter + " " + filterValue + " " +( page + 1)), 60, 20));

        final Table table = new Table(npcList.size(), 5).setParams(border(1));
        for (int i = 0; i <npcList.size(); i++) {
            final NpcModel npcModel = npcList.get(i);
            table.row(i).col(0).setParams(width(100)).insert(new Font(Color.BROWN, npcModel.getId()));
            table.row(i).col(1).setParams(width(250)).insert(new Font(Color.GREN, npcModel.getName()));
            table.row(i).col(2).setParams(width(100)).insert(new Font(Color.BROWN, npcModel.getDisplayId()));
            table.row(i).col(3).setParams(width(150)).insert(new Font(Color.BLUE, npcModel.getType()));
            table.row(i).col(4).insert(new Button("redact", actionCom(admin_npc_editor_main_stats, npcModel.getId()), 80, 20));
        }

        npcTable.row(0).col(0).insert(pages);
        npcTable.row(1).col(0).insert(table);
        mainTable.row(0).col(0).setParams(align(CENTER)).insert(filterTable);
        mainTable.row(1).col(0).setParams(align(CENTER)).insert(npcTable);

        CBWindow(player, mainTable, window_titel);
    }

    public static void showMainStats(L2Player player, String[] args){
        int npcId = Integer.parseInt(args[1]);
        final L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        final L2NpcTemplate template = npc.getTemplate();

        final Table table = new Table(14, 1);
        table.row(0).col(0).insert(parametersTable("level", String.valueOf(template.level), admin_npc_editor_save_main_stats, npcId));
        table.row(1).col(0).insert(parametersTable("hp", String.valueOf(template.baseHpMax), admin_npc_editor_save_main_stats, npcId));
        table.row(2).col(0).insert(parametersTable("mp", String.valueOf(template.baseMpMax), admin_npc_editor_save_main_stats, npcId));
        table.row(3).col(0).insert(parametersTable("patk", String.valueOf(template.basePAtk), admin_npc_editor_save_main_stats, npcId));
        table.row(4).col(0).insert(parametersTable("pdef", String.valueOf(template.basePDef), admin_npc_editor_save_main_stats, npcId));
        table.row(5).col(0).insert(parametersTable("matk", String.valueOf(template.baseMAtk), admin_npc_editor_save_main_stats, npcId));
        table.row(6).col(0).insert(parametersTable("mdef", String.valueOf(template.baseMDef), admin_npc_editor_save_main_stats, npcId));
        table.row(7).col(0).insert(parametersTable("atkspd", String.valueOf(template.basePAtkSpd), admin_npc_editor_save_main_stats, npcId));
        table.row(8).col(0).insert(parametersTable("matkspd", String.valueOf(template.baseMAtkSpd), admin_npc_editor_save_main_stats, npcId));
        table.row(9).col(0).insert(parametersTable("walkspd", String.valueOf(template.baseWalkSpd), admin_npc_editor_save_main_stats, npcId));
        table.row(10).col(0).insert(parametersTable("runspd", String.valueOf(template.baseRunSpd), admin_npc_editor_save_main_stats, npcId));
        table.row(11).col(0).insert(parametersTable("exp", String.valueOf(template.revardExp), admin_npc_editor_save_main_stats, npcId));
        table.row(12).col(0).insert(parametersTable("sp", String.valueOf(template.revardSp), admin_npc_editor_save_main_stats, npcId));

        final Table main = new Table(2, 1);
        main.row(0).col(0).insert(" ID: " + new Font(Color.GOLD, npcId).build() + " | " + new Font(Color.GREN, npc.getName()).build());
        main.row(1).col(0).insert(table);

        basePage(player, npcId, main, "");
    }
    public static void showBaseStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);

        final L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        final L2NpcTemplate template = npc.getTemplate();

        final Table table = new Table(14, 1);
        table.row(0).col(0).insert(parametersTable("str", String.valueOf(template.level), admin_npc_editor_save_main_stats, npcId));
        table.row(1).col(0).insert(parametersTable("con", String.valueOf(template.baseHpMax), admin_npc_editor_save_main_stats, npcId));
        table.row(2).col(0).insert(parametersTable("dex", String.valueOf(template.baseMpMax), admin_npc_editor_save_main_stats, npcId));
        table.row(3).col(0).insert(parametersTable("int", String.valueOf(template.basePAtk), admin_npc_editor_save_main_stats, npcId));
        table.row(4).col(0).insert(parametersTable("wit", String.valueOf(template.basePDef), admin_npc_editor_save_main_stats, npcId));
        table.row(5).col(0).insert(parametersTable("men", String.valueOf(template.baseMAtk), admin_npc_editor_save_main_stats, npcId));

        final Table main = new Table(2, 1);
        main.row(0).col(0).insert(" ID: " + new Font(Color.GOLD, npcId).build() + " | " + new Font(Color.GREN, npc.getName()).build());
        main.row(1).col(0).insert(table);


        basePage(player, npcId, table, "");
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
        final List<DropItem> dropItems = NpcEditorRepository.getDropList(npcId);
        final List<DropItem> dropList = dropItems.stream().filter(dropItem -> !dropItem.getIsSpoil()).filter(dropItem -> !dropItem.isHerb()).collect(Collectors.toList());
        final List<DropItem> spoilList = dropItems.stream().filter(DropItem::getIsSpoil).filter(dropItem -> !dropItem.isHerb()).collect(Collectors.toList());
        final List<DropItem> herbs = dropItems.stream().filter(DropItem::isHerb).collect(Collectors.toList());

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

    public static void saveMainStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        String stat = args[2];
        String statValue = args[3];
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcEditorRepository.updateMainStats(npc);
//        reload(npc);
        showMainStats(player, args);
    }
    public static void saveBaseStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcEditorRepository.updateBaseStats(npc);
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
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcEditorRepository.updateElements(npc);
//        reload(npc);
        showElements(player, args);
    }
    public static void saveLocation(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        SpawnModel oldSpawn = NpcEditorRepository.getLocation(npc);
        SpawnModel newSpawn = NpcEditorRepository.getLocation(npc);

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
        NpcTable.getAi_params().remove(npc.getNpcId());
        NpcTable.getNpcsNames().remove(npc.getName().toLowerCase());
        NpcTable.getNpcsByLevel()[npc.getLevel()].remove(npc);
        NpcEditorRepository.restoreNpc(npc.getNpcId());
    }


}
