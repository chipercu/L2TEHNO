package utils_soft.NpcEditor;

import l2open.common.Html_Constructor.tags.Table;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import utils_soft.common.Component;

import java.util.logging.Logger;

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





    public static void changeBaseParameter(L2NpcInstance npc, String parameter, String value){
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "";
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            query = "UPDATE npc SET " + parameter + " WHERE id = ?";
            statement = con.prepareStatement(query);
            statement.setInt(1, npc.getNpcId());
            statement.execute();
        }
        catch(Exception e1)
        {
            // problem with storing spawn
            _log.warning("npc data couldnt be stored in db, query is :" + query + " : " + e1);
        }
        finally
        {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

}
