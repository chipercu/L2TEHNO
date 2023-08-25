package l2open.gameserver.tables;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.templates.L2Henna;
import l2open.gameserver.templates.StatsSet;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Logger;

@SuppressWarnings( { "nls", "unqualified-field-access", "boxing" })
public class HennaTable
{
	private static final Logger _log = Logger.getLogger(HennaTable.class.getName());

	private static HennaTable _instance;

	private HashMap<Integer, L2Henna> _henna;
	private boolean _initialized = true;

	public static HennaTable getInstance()
	{
		if(_instance == null)
			_instance = new HennaTable();
		return _instance;
	}

	private HennaTable()
	{
		_henna = new HashMap<Integer, L2Henna>();
		RestoreHennaData();

	}

	private void RestoreHennaData()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet hennadata = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT symbol_id, symbol_name, dye_id, dye_amount, price, stat_INT, stat_STR, stat_CON, stat_MEM, stat_DEX, stat_WIT FROM henna");
			hennadata = statement.executeQuery();

			fillHennaTable(hennadata);
		}
		catch(Exception e)
		{
			_log.severe("error while creating henna table " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, hennadata);
		}
	}

	private void fillHennaTable(ResultSet HennaData) throws Exception
	{
		while(HennaData.next())
		{
			StatsSet hennaDat = new StatsSet();
			int id = HennaData.getInt("symbol_id");

			hennaDat.set("symbol_id", id);
			//hennaDat.set("symbol_name", HennaData.getString("symbol_name"));
			hennaDat.set("dye", HennaData.getInt("dye_id"));
			hennaDat.set("price", HennaData.getInt("price"));
			//amount of dye required
			hennaDat.set("amount", HennaData.getInt("dye_amount"));
			hennaDat.set("stat_INT", HennaData.getInt("stat_INT"));
			hennaDat.set("stat_STR", HennaData.getInt("stat_STR"));
			hennaDat.set("stat_CON", HennaData.getInt("stat_CON"));
			hennaDat.set("stat_MEM", HennaData.getInt("stat_MEM"));
			hennaDat.set("stat_DEX", HennaData.getInt("stat_DEX"));
			hennaDat.set("stat_WIT", HennaData.getInt("stat_WIT"));

			L2Henna template = new L2Henna(hennaDat);
			_henna.put(id, template);
		}
		_log.info("HennaTable: Loaded " + _henna.size() + " Templates.");
	}

	public boolean isInitialized()
	{
		return _initialized;
	}

	public L2Henna getTemplate(int id)
	{
		return _henna.get(id);
	}

}
