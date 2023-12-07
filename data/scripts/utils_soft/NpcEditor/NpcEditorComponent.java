package utils_soft.NpcEditor;

import l2open.common.Html_Constructor.tags.*;
import l2open.common.Html_Constructor.tags.parameters.Color;
import l2open.common.Html_Constructor.tags.parameters.EditType;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import utils_soft.common.Component;

import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;
import static l2open.common.Html_Constructor.tags.parameters.Position.CENTER;
import static l2open.common.Html_Constructor.tags.parameters.Position.LEFT;
import static utils_soft.NpcEditor.NpcEditorCommands.*;

public class NpcEditorComponent extends Component{

    private static final String backMainBypass = "admin_npc_editor null 0 1";
    private static final String window_titel = "Npc Editor";
    protected static Logger _log = Logger.getLogger(NpcEditorComponent.class.getName());

    public NpcEditorComponent() {
    }

    public static void basePage(L2Player player,int npcId, Table table, String saveButton) {
        final Table main = new Table(3, 1);
        main.row(0).col(0).insert(headerTable(npcId));
        main.row(1).col(0).insert(table);
        main.row(2).col(0).setParams(align(CENTER)).insert(saveButton);
        TWindow(player, main, window_titel);
    }
    public static Table headerTable(int npcId){
        final Table headerTable = new Table(2, 4);
        headerTable.row(0).col(0).setParams(width(70), height(20)).insert(new Button("MainStats", actionCom(admin_npc_editor_main_stats, npcId), 65, 20));
        headerTable.row(0).col(1).setParams(width(70)).insert(new Button("BaseStats", actionCom(admin_npc_editor_base_stats, npcId), 65, 20));
        headerTable.row(0).col(2).setParams(width(70)).insert(new Button("Skills", actionCom(admin_npc_editor_skills, npcId + " active"), 65, 20));
        headerTable.row(0).col(3).setParams(width(70)).insert(new Button("Drop", actionCom(admin_npc_editor_drop, npcId), 65, 20));
        headerTable.row(1).col(0).setParams(width(70), height(20)).insert(new Button("Visual", actionCom(admin_npc_editor_visual, npcId), 65, 20));
        headerTable.row(1).col(1).setParams(width(70)).insert(new Button("Elements", actionCom(admin_npc_editor_elements, npcId), 65, 20));
        headerTable.row(1).col(2).setParams(width(70)).insert(new Button("Location", actionCom(admin_npc_editor_location, npcId), 65, 20));
        headerTable.row(1).col(3).setParams(width(70)).insert(new Button("Other", actionCom(admin_npc_editor_other, npcId), 65, 20));
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

    public static void showMainStats(L2Player player, String[] args){
        int npcId = Integer.parseInt(args[1]);

        final Table table = new Table(7, 2);
        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_save_main_stats, ""), 100, 20).build());
    }
    public static void showBaseStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);

        final Table table = new Table(7, 2);
        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_save_base_stats, ""), 100, 20).build());
    }
    public static void showSkills(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        String skillType = args[2];

        final L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        final List<L2Skill> activeSkills = npc.getAllSkills().stream().filter(L2Skill::isActive).collect(Collectors.toList());
        final List<L2Skill> passiveSkills = npc.getAllSkills().stream().filter(L2Skill::isPassive).collect(Collectors.toList());

        final Table header = new Table(1, 2);
        header.row(0).col(0).setParams(align(CENTER), valign(CENTER), width(150)).insert(new Button("Active Skills", actionCom(admin_npc_editor_skills, npcId + " active"), 65, 20));
        header.row(0).col(1).setParams(align(CENTER), valign(CENTER), width(150)).insert(new Button("Passive Skills", actionCom(admin_npc_editor_skills, npcId + " passive"), 65, 20));

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

        basePage(player, npcId, skillsTable,"");
    }
    public static void showDrop(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final List<DropItem> dropItems = NpcEditorRepository.getDropList(npcId);
        final List<DropItem> dropList = dropItems.stream().filter(dropItem -> !dropItem.getIsSpoil()).collect(Collectors.toList());
        final List<DropItem> spoilList = dropItems.stream().filter(DropItem::getIsSpoil).collect(Collectors.toList());

        final Table table = new Table(4, 1);
        table.row(0).col(0).setParams(valign(CENTER), align(CENTER)).insert(new Font(Color.GREN, "DROP"));
        table.row(1).col(0).insert(dropTable(npcId, dropList));
        table.row(2).col(0).setParams(valign(CENTER), align(CENTER)).insert(new Font(Color.GREN, "SPOIL"));
        table.row(3).col(0).insert(dropTable(npcId, spoilList));

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
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcEditorRepository.updateMainStats(npc);
        reload(npcId);
        showMainStats(player, args);
    }
    public static void saveBaseStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcEditorRepository.updateBaseStats(npc);
        reload(npcId);
        showBaseStats(player,args);
    }
    public static void addSkills(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int skillId = Integer.parseInt(args[2]);
        int skillLevel = Integer.parseInt(args[3]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
        if (skill != null){
            NpcEditorRepository.addSkill(npc, skill);
            reload(npcId);
            showSkills(player, new String[]{args[1], skill.isActive() ? "active" : "passive"});
        }
    }
    public static void removeSkill(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int skillId = Integer.parseInt(args[2]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        NpcEditorRepository.removeSkill(npc, skillId);
        reload(npcId); // TODO: 06.12.2023  сделать релоад только редактируемого НПС
        npc.decayMe();
        npc.spawnMe();
        showSkills(player, args);
    }
    public static void addDrop(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int itemId = Integer.parseInt(args[2]);

//        NpcRepository.addDrop(npcId, itemId);
        reload(npcId);
        showDrop(player, args);
    }
    public static void removeDrop(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int itemId = Integer.parseInt(args[2]);
        boolean isSpoil = Boolean.parseBoolean(args[3]);
        NpcEditorRepository.removeDrop(npcId, itemId, isSpoil);
        reload(npcId);
        showDrop(player, args);
    }
    public static void saveVisual(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcEditorRepository.updateVisualStats(npc);
        reload(npcId);
        showVisual(player, args);
    }
    public static void saveElements(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcEditorRepository.updateElements(npc);
        reload(npcId);
        showElements(player, args);
    }
    public static void saveLocation(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcEditorRepository.updateLocation(npc);
        reload(npcId);
        showLocation(player, args);
    }
    public static void saveOther(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcEditorRepository.updateOtherStats(npc);
        reload(npcId);
        showOther(player, args);
    }

    public static void reload(int npcId){
        NpcEditorRepository.restoreNpc(npcId);
    }



}
