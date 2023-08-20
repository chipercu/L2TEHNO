package l2open.gameserver.instancemanager;

import gnu.trove.TIntObjectHashMap;
import l2open.config.*;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.entity.ItemBroker.ItemAuctionInstance;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ItemAuctionManager
{
	private static final Logger _log = Logger.getLogger(ItemAuctionManager.class.getName());
	private final TIntObjectHashMap<ItemAuctionInstance> _managerInstances;
	private final AtomicInteger _auctionIds;

	public static ItemAuctionManager getInstance()
	{
	return SingletonHolder._instance;
	}

	private ItemAuctionManager()
	{
		_managerInstances = new TIntObjectHashMap<ItemAuctionInstance>();
		_auctionIds = new AtomicInteger(1);

		if (!ConfigValue.AltItemAuctionEnabled)
		{
			_log.log(Level.INFO, "ItemAuctionManager: Disabled by config.");
			return;
		}

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT auctionId FROM item_auction ORDER BY auctionId DESC LIMIT 0, 1");
			rs = statement.executeQuery();
			if(rs.next())
				_auctionIds.set(rs.getInt(1) + 1);
		}
		catch (SQLException e)
		{
			_log.log(Level.SEVERE, "ItemAuctionManager: Failed loading auctions.", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}

		File file = new File(ConfigValue.DatapackRoot + "/data/xml/item_auctions.xml");
		if(!file.exists())
		{
			_log.log(Level.WARNING, "ItemAuctionManager: Missing item_auctions.xml!");
			return;
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		try
		{
			Document doc = factory.newDocumentBuilder().parse(file);
			for(Node na = doc.getFirstChild(); na != null; na = na.getNextSibling())
			{
				if(!"list".equalsIgnoreCase(na.getNodeName()))
					continue;
				for(Node nb = na.getFirstChild(); nb != null; nb = nb.getNextSibling())
				{
					if(!"instance".equalsIgnoreCase(nb.getNodeName()))
						continue;
					NamedNodeMap nab = nb.getAttributes();
					int instanceId = Integer.parseInt(nab.getNamedItem("id").getNodeValue());

					if(_managerInstances.containsKey(instanceId))
					{
						throw new Exception("Duplicated instanceId " + instanceId);
					}
					ItemAuctionInstance instance = new ItemAuctionInstance(instanceId, _auctionIds, nb);
					_managerInstances.put(instanceId, instance);
				}

			}

			_log.log(Level.INFO, "ItemAuctionManager: Loaded " + _managerInstances.size() + " instance(s).");
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "ItemAuctionManager: Failed loading auctions from xml.", e);
		}
	}

	public final void shutdown()
	{
		ItemAuctionInstance[] instances = _managerInstances.getValues(new ItemAuctionInstance[_managerInstances.size()]);
		for(ItemAuctionInstance instance : instances)
			instance.shutdown();
	}

	public final ItemAuctionInstance getManagerInstance(int instanceId)
	{
		return _managerInstances.get(instanceId);
	}

	public final int getNextAuctionId()
	{
		return _auctionIds.getAndIncrement();
	}

	public static void deleteAuction(int auctionId)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM item_auction WHERE auctionId=?");
			statement.setInt(1, auctionId);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM item_auction_bid WHERE auctionId=?");
			statement.setInt(1, auctionId);
			statement.execute();
			statement.close();
		}
		catch(SQLException e)
		{
			_log.log(Level.SEVERE, "L2ItemAuctionManagerInstance: Failed deleting auction: " + auctionId, e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	private static class SingletonHolder
	{
		protected static final ItemAuctionManager _instance = new ItemAuctionManager();
	}
}
