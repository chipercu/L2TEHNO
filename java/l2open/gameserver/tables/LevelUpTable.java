package l2open.gameserver.tables;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2LvlupData;
import l2open.gameserver.model.base.ClassId;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * This class ...
 *
 * @author NightMarez
 * @version $Revision: 1.3.2.4.2.3 $ $Date: 2005/03/27 15:29:18 $
 */
public class LevelUpTable
{
	private static final String SELECT_ALL = "SELECT classid, defaulthpbase, defaulthpadd, defaulthpmod, defaultcpbase, defaultcpadd, defaultcpmod, defaultmpbase, defaultmpadd, defaultmpmod, class_lvl FROM lvlupgain";
	private static final String CLASS_LVL = "class_lvl";
	private static final String MP_MOD = "defaultmpmod";
	private static final String MP_ADD = "defaultmpadd";
	private static final String MP_BASE = "defaultmpbase";
	private static final String HP_MOD = "defaulthpmod";
	private static final String HP_ADD = "defaulthpadd";
	private static final String HP_BASE = "defaulthpbase";
	private static final String CP_MOD = "defaultcpmod";
	private static final String CP_ADD = "defaultcpadd";
	private static final String CP_BASE = "defaultcpbase";
	private static final String CLASS_ID = "classid";

	private static Logger _log = Logger.getLogger(LevelUpTable.class.getName());

	private static LevelUpTable _instance;

	private HashMap<Integer, L2LvlupData> _lvltable;

	public static LevelUpTable getInstance()
	{
		if(_instance == null)
			_instance = new LevelUpTable();
		return _instance;
	}

	private LevelUpTable()
	{
		_lvltable = new HashMap<Integer, L2LvlupData>();
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_ALL);
			rset = statement.executeQuery();
			L2LvlupData lvlDat;

			while(rset.next())
			{
				lvlDat = new L2LvlupData();
				lvlDat.set_classid(rset.getInt(CLASS_ID));
				lvlDat.set_classLvl(rset.getInt(CLASS_LVL));
				lvlDat.set_classHpBase(rset.getFloat(HP_BASE));
				lvlDat.set_classHpAdd(rset.getFloat(HP_ADD));
				lvlDat.set_classHpModifier(rset.getFloat(HP_MOD));
				lvlDat.set_classCpBase(rset.getFloat(CP_BASE));
				lvlDat.set_classCpAdd(rset.getFloat(CP_ADD));
				lvlDat.set_classCpModifier(rset.getFloat(CP_MOD));
				lvlDat.set_classMpBase(rset.getFloat(MP_BASE));
				lvlDat.set_classMpAdd(rset.getFloat(MP_ADD));
				lvlDat.set_classMpModifier(rset.getFloat(MP_MOD));

				_lvltable.put(lvlDat.get_classid(), lvlDat);
			}

			_log.info("LevelUpData: Loaded " + _lvltable.size() + " Character Level Up Templates.");
		}
		catch(Exception e)
		{
			_log.warning("error while creating Lvl up data table " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	/**
	 * @param template id
	 * @return
	 */
	public L2LvlupData getTemplate(int classId)
	{
		return _lvltable.get(classId);
	}

	public L2LvlupData getTemplate(ClassId classId)
	{
		return _lvltable.get(classId.getId());
	}
}
