package utils_soft.NpcEditor;


import l2open.common.Html_Constructor.tags.Button;
import l2open.common.Html_Constructor.tags.Table;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import utils_soft.common.Component;

import java.util.logging.Logger;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.actionCom;
import static utils_soft.NpcEditor.NpcCommands.*;


public class NpcEditorComponent extends Component{

    private static final String backMainBypass = "admin_npc_editor null 0 1";
    private static final String window_titel = "Npc Editor";
    protected static Logger _log = Logger.getLogger(NpcEditorComponent.class.getName());

    public NpcEditorComponent() {
    }


    public static void showMainPage(L2Player player, String[] args) {

        final Table main = new Table(1, 1);

        showTWindow(player, main.build(), window_titel, backMainBypass);
    }

    public static void basePage(L2Player player,int npcId, Table table, Button saveButton) {
        final Table main = new Table(3, 1);
        main.row(0).col(0).insert(headerTable(npcId).build());
        main.row(1).col(0).insert(table.build());
        main.row(2).col(0).insert(saveButton.build());
        showTWindow(player, main.build(), window_titel, backMainBypass);
    }

    public static Table headerTable(int npcId){
        final Table headerTable = new Table(2, 4);
        headerTable.row(0).col(0).insert(new Button("MainStats", actionCom(admin_npc_editor_main_stats, npcId)).build());
        headerTable.row(0).col(1).insert(new Button("BaseStats", actionCom(admin_npc_editor_base_stats, npcId)).build());
        headerTable.row(0).col(2).insert(new Button("Skills", actionCom(admin_npc_editor_skills, npcId)).build());
        headerTable.row(0).col(3).insert(new Button("Drop", actionCom(admin_npc_editor_drop, npcId)).build());
        headerTable.row(1).col(0).insert(new Button("Visual", actionCom(admin_npc_editor_visual, npcId)).build());
        headerTable.row(1).col(1).insert(new Button("Elements", actionCom(admin_npc_editor_elements, npcId)).build());
        headerTable.row(1).col(2).insert(new Button("Location", actionCom(admin_npc_editor_location, npcId)).build());
        headerTable.row(1).col(4).insert(new Button("Other", actionCom(admin_npc_editor_other, npcId)).build());
        return headerTable;
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
        final Table table = new Table(7, 2);
        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_save_skills, "")));
    }

    public static void showDrop(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        final Table table = new Table(7, 2);
        basePage(player, npcId, table, new Button("Сохранить", actionCom(admin_npc_editor_save_drop, "")));
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
    public static void saveSkills(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

//        NpcRepository.addSkill(npc);
        reload();
        showSkills(player, args);
    }
    public static void saveDrop(L2Player player, String[] args) {
        int npcId = Integer.parseInt(args[1]);
        L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

//        NpcRepository.addDrop(npc);
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
