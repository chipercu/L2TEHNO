package l2open.gameserver.tables;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.instances.L2HennaInstance;
import l2open.gameserver.templates.L2Henna;
import l2open.util.GArray;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Logger;

@SuppressWarnings( { "nls", "unqualified-field-access", "boxing" })
public class HennaTreeTable
{
	private static Logger _log = Logger.getLogger(HennaTreeTable.class.getName());
	private static final HennaTreeTable _instance = new HennaTreeTable();
	private HashMap<ClassId, GArray<L2HennaInstance>> _hennaTrees;
	private boolean _initialized = true;

	public static HennaTreeTable getInstance()
	{
		return _instance;
	}

	private HennaTreeTable()
	{
		_hennaTrees = new HashMap<ClassId, GArray<L2HennaInstance>>();
		int classId = 0;
		int count = 0;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		FiltredPreparedStatement statement2 = null;
		ResultSet classlist = null, hennatree = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT id FROM class_list ORDER BY id");
			statement2 = con.prepareStatement("SELECT symbol_id FROM henna_trees where class_id=? ORDER BY symbol_id");
			classlist = statement.executeQuery();
			GArray<L2HennaInstance> list = new GArray<L2HennaInstance>();
			//int parentClassId;
			//L2Henna henna;
			while(classlist.next())
			{
				list = new GArray<L2HennaInstance>();
				classId = classlist.getInt("id");
				statement2.setInt(1, classId);
				hennatree = statement2.executeQuery();
				while(hennatree.next())
				{
					short id = hennatree.getShort("symbol_id");
					//String name = hennatree.getString("name");
					L2Henna template = HennaTable.getInstance().getTemplate(id);
					if(template == null)
						return;
					L2HennaInstance temp = new L2HennaInstance(template);
					temp.setSymbolId(id);
					temp.setItemIdDye(template.getDyeId());
					temp.setAmountDyeRequire(template.getAmountDyeRequire());
					temp.setPrice(template.getPrice());
					temp.setStatINT(template.getStatINT());
					temp.setStatSTR(template.getStatSTR());
					temp.setStatCON(template.getStatCON());
					temp.setStatMEM(template.getStatMEM());
					temp.setStatDEX(template.getStatDEX());
					temp.setStatWIT(template.getStatWIT());

					list.add(temp);
				}
				_hennaTrees.put(ClassId.values()[classId], list);
				count += list.size();
				_log.fine("Henna Tree for Class: " + classId + " has " + list.size() + " Henna Templates.");
				DatabaseUtils.closeResultSet(hennatree);
			}
		}
		catch(Exception e)
		{
			_log.warning("error while creating henna tree for classId " + classId + "	" + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseSR(statement2, hennatree);
			DatabaseUtils.closeDatabaseCSR(con, statement, classlist);
		}

		_log.info("HennaTreeTable: Loaded " + count + " Henna Tree Templates.");

	}

	public L2HennaInstance[] getAvailableHenna(ClassId classId, byte sex)
	{
		if(classId.getId() > 135)
			classId = classId.getParent(sex);

		GArray<L2HennaInstance> henna = _hennaTrees.get(classId);
		if(henna == null || henna.size() == 0)
			return new L2HennaInstance[0];
		return henna.toArray(new L2HennaInstance[henna.size()]);
	}

	public boolean isInitialized()
	{
		return _initialized;
	}
}
