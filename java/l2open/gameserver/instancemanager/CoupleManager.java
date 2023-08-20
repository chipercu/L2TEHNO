package l2open.gameserver.instancemanager;

import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.Couple;
import l2open.util.GCArray;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

public class CoupleManager
{
	protected static Logger _log = Logger.getLogger(CoupleManager.class.getName());

	private static CoupleManager _instance;

	private GCArray<Couple> _couples;
	private volatile GCArray<Couple> _deletedCouples;

	public static CoupleManager getInstance()
	{
		if(_instance == null)
			new CoupleManager();
		return _instance;
	}

	public CoupleManager()
	{
		_instance = this;
		_log.info("Initializing CoupleManager");
		_instance.load();
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new StoreTask(), 10 * 60 * 1000, 10 * 60 * 1000);
	}

	private void load()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM couples ORDER BY id");
			rs = statement.executeQuery();
			while(rs.next())
			{
				Couple c = new Couple(rs.getInt("id"));
				c.setPlayer1Id(rs.getInt("player1Id"));
				c.setPlayer2Id(rs.getInt("player2Id"));
				c.setMaried(rs.getBoolean("maried"));
				c.setAffiancedDate(rs.getLong("affiancedDate"));
				c.setWeddingDate(rs.getLong("weddingDate"));
				getCouples().add(c);
			}
			_log.info("Loaded: " + getCouples().size() + " couples(s)");
		}
		catch(Exception e)
		{
			_log.warning("Exception: CoupleManager.load(): " + e.getMessage());
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}

	public final Couple getCouple(int coupleId)
	{
		for(Couple c : getCouples())
			if(c != null && c.getId() == coupleId)
				return c;
		return null;
	}

	/**
	 * Вызывается при каждом входе персонажа в мир
	 * @param cha
	 */
	public void engage(L2Player cha)
	{
		int chaId = cha.getObjectId();

		for(Couple cl : getCouples())
			if(cl != null)
				if(cl.getPlayer1Id() == chaId || cl.getPlayer2Id() == chaId)
				{
					if(cl.getMaried())
						cha.setMaried(true);

					cha.setCoupleId(cl.getId());

					if(cl.getPlayer1Id() == chaId)
						cha.setPartnerId(cl.getPlayer2Id());
					else
						cha.setPartnerId(cl.getPlayer1Id());
				}
	}

	/**
	 * Уведомляет партнера персонажа о его входе в мир.
	 * @param cha
	 */
	public void notifyPartner(L2Player cha)
	{
		if(cha.getPartnerId() != 0)
		{
			L2Player partner = L2ObjectsStorage.getPlayer(cha.getPartnerId());
			if(partner != null)
				partner.sendMessage(new CustomMessage("l2open.gameserver.instancemanager.CoupleManager.PartnerEntered", partner));
		}
	}

	public void createCouple(L2Player player1, L2Player player2)
	{
		if(player1 != null && player2 != null)
			if(player1.getPartnerId() == 0 && player2.getPartnerId() == 0)
			{
				if(player1.getAttainment() != null)
					player1.getAttainment().createCouple(player1, player2);
				if(player2.getAttainment() != null)
					player2.getAttainment().createCouple(player2, player1);
				getCouples().add(new Couple(player1, player2));
			}
	}

	public final GCArray<Couple> getCouples()
	{
		if(_couples == null)
			_couples = new GCArray<Couple>();
		return _couples;
	}

	public GCArray<Couple> getDeletedCouples()
	{
		if(_deletedCouples == null)
			_deletedCouples = new GCArray<Couple>();
		return _deletedCouples;
	}

	/**
	 * Вызывется при шатдауне
	 * Сначала очищаем таблицу от ненужных свадеб, потом загоняем в нее все нужные.
	 * Обращение происходит только при загрузке/шатдауне сервера, ну или по запросу
	 */
	public void store()
	{
		ThreadConnection con = null;

		try
		{
			if(_deletedCouples != null && !_deletedCouples.isEmpty())
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				for(Couple c : _deletedCouples)
				{
					FiltredPreparedStatement statement = con.prepareStatement("DELETE FROM couples WHERE id = ?");
					statement.setInt(1, c.getId());
					statement.execute();
					statement.close();
				}
				_deletedCouples.clear();
			}

			if(_couples != null && !_couples.isEmpty())
				for(Couple c : _couples)
					if(c != null && c.isChanged())
					{
						if(con == null)
							con = L2DatabaseFactory.getInstance().getConnection();

						c.store(con);
						c.setChanged(false);
					}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeConnection(con);
		}
	}

	private class StoreTask extends l2open.common.RunnableImpl
	{
		private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

		public void runImpl()
		{
			store();
			_log.fine("Scheduled couple DB storing finished at: " + formatter.format(System.currentTimeMillis()));
		}
	}
}