package l2open.gameserver.taskmanager;

import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.database.*;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.util.Strings;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class DeleteExpiredVarsManager
{
	public ScheduledFuture<?> _task;
	private static DeleteExpiredVarsManager _instance;

	public DeleteExpiredVarsManager()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new doTask(), 30*1000, 30*60*1000);
	}

	public static DeleteExpiredVarsManager getInstance()
	{
		if(_instance == null)
			_instance = new DeleteExpiredVarsManager();
		return _instance;
	}

	public class doTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			ThreadConnection con = null;
			FiltredPreparedStatement st = null;
			ResultSet rset = null;
			Map<Integer, String> varMap = new HashMap<Integer, String>();
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				st = con.prepareStatement("SELECT obj_id, name FROM character_variables WHERE expire_time > 0 AND expire_time < ?");
				st.setLong(1, System.currentTimeMillis());
				rset = st.executeQuery();
				while(rset.next())
				{
					String name = rset.getString("name");
					String obj_id = Strings.stripSlashes(rset.getString("obj_id"));
					varMap.put(Integer.parseInt(obj_id), name);
				}
			}
			catch(Exception ignored)
			{}
			finally
			{
				DatabaseUtils.closeDatabaseCSR(con, st, rset);
			}
			if(!varMap.isEmpty())
			{
				for(Map.Entry<Integer, String> entry : varMap.entrySet())
				{
					L2Player player = L2ObjectsStorage.getPlayer(entry.getKey());
					if(player != null && player.isOnline())
						player.unsetVar(entry.getValue());
					else
						mysql.set("DELETE FROM `character_variables` WHERE `obj_id`=? AND `type`='user-var' AND `name`=? LIMIT 1", entry.getKey(), entry.getValue());
				}
			}
		}
	}
}