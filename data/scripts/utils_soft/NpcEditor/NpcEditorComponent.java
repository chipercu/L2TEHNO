package utils_soft.NpcEditor;

import l2open.common.Html_Constructor.tags.Button;
import l2open.common.Html_Constructor.tags.Edit;
import l2open.common.Html_Constructor.tags.Img;
import l2open.common.Html_Constructor.tags.Table;
import l2open.common.Html_Constructor.tags.parameters.EditType;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import utils_soft.common.Component;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;
import static l2open.common.Html_Constructor.tags.parameters.Position.CENTER;
import static l2open.common.Html_Constructor.tags.parameters.Position.LEFT;
import static utils_soft.NpcEditor.NpcCommands.*;

public class NpcEditorComponent extends Component{

    private static final String backMainBypass = "admin_npc_editor null 0 1";
    private static final String window_titel = "Npc Editor";
    protected static Logger _log = Logger.getLogger(NpcEditorComponent.class.getName());

    public NpcEditorComponent() {
    }

    public static void basePage(L2Player player,int npcId, Table table, Button saveButton) {
        final Table main = new Table(3, 1);
        main.row(0).col(0).insert(headerTable(npcId));
        main.row(1).col(0).insert(table);
        main.row(2).col(0).insert(saveButton);
        showTWindow(player, main, window_titel, backMainBypass);
    }
    public static Table headerTable(int npcId){
        final Table headerTable = new Table(2, 4);
        headerTable.row(0).col(0).insert(new Button("MainStats", actionCom(admin_npc_editor_main_stats, npcId)));
        headerTable.row(0).col(1).insert(new Button("BaseStats", actionCom(admin_npc_editor_base_stats, npcId)));
        headerTable.row(0).col(2).insert(new Button("Skills", actionCom(admin_npc_editor_skills, npcId)));
        headerTable.row(0).col(3).insert(new Button("Drop", actionCom(admin_npc_editor_drop, npcId)));
        headerTable.row(1).col(0).insert(new Button("Visual", actionCom(admin_npc_editor_visual, npcId)));
        headerTable.row(1).col(1).insert(new Button("Elements", actionCom(admin_npc_editor_elements, npcId)));
        headerTable.row(1).col(2).insert(new Button("Location", actionCom(admin_npc_editor_location, npcId)));
        headerTable.row(1).col(4).insert(new Button("Other", actionCom(admin_npc_editor_other, npcId)));
        return headerTable;
    }
    public static Table skillTable(int npcId, List<L2Skill> skills){
        final Table skillsTable = new Table(skills.size(), 5).setParams(cellpadding(0));
        for (int i = 0; i < skills.size(); i++){
            L2Skill skill = skills.get(i);
            skillsTable.row(i).col(0).setParams(width(40)).insert(new Img(skill.getIcon()));
            skillsTable.row(i).col(1).setParams(width(80)).setParams(valign(CENTER), align(LEFT)).insert(skill.getName());
            skillsTable.row(i).col(2).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(skill.getId());
            skillsTable.row(i).col(3).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(skill.getLevel());
            skillsTable.row(i).col(4).setParams(width(40)).insert(new Button("X", actionCom(admin_npc_editor_remove_skill, npcId + " " + skill.getId())));
        }
        return skillsTable;
    }
    public static Table dropTable(int npcId, List<DropItem> dropItems){
        final Table main = new Table(2, 1);
        final Table dropTable = new Table(dropItems.size(), 7).setParams(cellpadding(0));
        for (int i = 0; i < dropItems.size(); i++){
            final DropItem dropItem = dropItems.get(i);
            final L2Item l2Item = ItemTemplates.getInstance().getTemplate(dropItem.getId());
            dropTable.row(i).col(0).setParams(width(40)).insert(new Img(l2Item.getIcon()));
            dropTable.row(i).col(1).setParams(width(80)).setParams(valign(CENTER), align(LEFT)).insert(formatItemName(l2Item.getName()));
            dropTable.row(i).col(2).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(dropItem.getMin());
            dropTable.row(i).col(3).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(dropItem.getMax());
            dropTable.row(i).col(4).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(dropItem.getGroup());
            dropTable.row(i).col(5).setParams(width(40)).setParams(valign(CENTER), align(CENTER)).insert(dropItem.getChance());
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
        return main;
    }

    public static void showMainStats(L2Player player, String[] args){
        int npcId = Integer.parseInt(args[1]);

        final Table table = new Table(7, 2);
        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_save_main_stats, "")));
    }
    public static void showBaseStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);

        final Table table = new Table(7, 2);
        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_save_base_stats, "")));
    }
    public static void showSkills(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);

        final L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        final List<L2Skill> activeSkills = npc.getAllSkills().stream().filter(L2Skill::isActive).collect(Collectors.toList());
        final List<L2Skill> passiveSkills = npc.getAllSkills().stream().filter(L2Skill::isPassive).collect(Collectors.toList());

        final Table table = new Table(3, 1);
        table.row(0).col(0).insert(skillTable(npcId, activeSkills));
        table.row(1).col(0).insert(skillTable(npcId, passiveSkills));

        final Table addSkillTable = new Table(1, 5);
        addSkillTable.row(0).col(0).setParams(width(20), valign(CENTER), align(LEFT)).insert("id");
        addSkillTable.row(0).col(1).insert(new Edit("id", 50, 12, EditType.num, 12));
        addSkillTable.row(0).col(2).setParams(width(35), valign(CENTER), align(LEFT)).insert("Level");
        addSkillTable.row(0).col(3).insert(new Edit("level", 85, 12, EditType.num, 12));
        addSkillTable.row(0).col(4).insert(new Button("Добавить", actionCom(admin_npc_editor_add_skills, npcId + " $id $level"), 80, 20));

        table.row(2).col(0).insert(addSkillTable);

        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_add_skills, "")));
    }
    public static void showDrop(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final List<DropItem> dropItems = NpcRepository.getDropList(npcId);
        final List<DropItem> dropList = dropItems.stream().filter(dropItem -> !dropItem.getIsSpoil()).collect(Collectors.toList());
        final List<DropItem> spoilList = dropItems.stream().filter(DropItem::getIsSpoil).collect(Collectors.toList());

        final Table table = new Table(4, 1);
        table.row(0).col(0).insert("Drop");
        table.row(1).col(0).insert(dropTable(npcId, dropList));
        table.row(2).col(0).insert("Spoil");
        table.row(3).col(0).insert(dropTable(npcId, spoilList));

        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_add_drop, "")));
    }
    public static void showVisual(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final Table table = new Table(7, 2);
        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_save_visual, "")));
    }
    public static void showElements(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final Table table = new Table(7, 2);
        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_save_base_elements, "")));
    }
    public static void showLocation(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final Table table = new Table(7, 2);
        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_save_location, "")));
    }
    public static void showOther(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final Table table = new Table(7, 2);
        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_save_other, "")));
    }

    public static void saveMainStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);

        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);



        NpcRepository.updateMainStats(npc);
        reload();
        showMainStats(player, args);
    }
    public static void saveBaseStats(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcRepository.updateBaseStats(npc);
        reload();
        showBaseStats(player,args);
    }
    public static void addSkills(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int skillId = Integer.parseInt(args[2]);
        int skillLevel = Integer.parseInt(args[3]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
        if (skill != null){
            NpcRepository.addSkill(npc, skill);
            reload();
        }
        showSkills(player, args);
    }
    public static void removeSkill(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int skillId = Integer.parseInt(args[2]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        NpcRepository.removeSkill(npc, skillId);
        reload();
        showSkills(player, args);
    }
    public static void addDrop(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int itemId = Integer.parseInt(args[2]);

//        NpcRepository.addDrop(npcId, itemId);
        reload();
        showDrop(player, args);
    }
    public static void removeDrop(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        int itemId = Integer.parseInt(args[2]);
        boolean isSpoil = Boolean.parseBoolean(args[3]);
        NpcRepository.removeDrop(npcId, itemId, isSpoil);
        reload();
        showDrop(player, args);
    }
    public static void saveVisual(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcRepository.updateVisualStats(npc);
        reload();
        showVisual(player, args);
    }
    public static void saveElements(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcRepository.updateElements(npc);
        reload();
        showElements(player, args);
    }
    public static void saveLocation(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcRepository.updateLocation(npc);
        reload();
        showLocation(player, args);
    }
    public static void saveOther(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        NpcRepository.updateOtherStats(npc);
        reload();
        showOther(player, args);
    }

    public static void reload(){
        NpcTable.getInstance().reloadAllNpc();
    }



}
