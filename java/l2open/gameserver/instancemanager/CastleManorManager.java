package l2open.gameserver.instancemanager;

import l2open.config.*;
import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Manor;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.items.Warehouse;
import l2open.gameserver.tables.ClanTable;
import l2open.util.GArray;
import l2open.util.Log;
import l2open.util.Rnd;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.logging.Logger;

public class CastleManorManager
{
	protected static Logger _log = Logger.getLogger(CastleManorManager.class.getName());

	private static CastleManorManager _instance;

	public static final int PERIOD_CURRENT = 0;
	public static final int PERIOD_NEXT = 1;
	protected static final String var_name = "ManorApproved";

	private static final String CASTLE_MANOR_LOAD_PROCURE = "SELECT * FROM castle_manor_procure WHERE castle_id=?";
	private static final String CASTLE_MANOR_LOAD_PRODUCTION = "SELECT * FROM castle_manor_production WHERE castle_id=?";

	private static final int NEXT_PERIOD_APPROVE = ConfigValue.AltManorApproveTime; // 6:00
	private static final int NEXT_PERIOD_APPROVE_MIN = ConfigValue.AltManorApproveMin; //
	private static final int MANOR_REFRESH = ConfigValue.AltManorRefreshTime; // 20:00
	private static final int MANOR_REFRESH_MIN = ConfigValue.AltManorRefreshMin; //
	protected static final long MAINTENANCE_PERIOD = ConfigValue.AltManorMaintenancePeriod / 60000; // 6 mins

	private boolean _underMaintenance;
	private boolean _disabled;

	public static CastleManorManager getInstance()
	{
		if(_instance == null)
		{
			_log.info("Manor System: Initializing...");
			_instance = new CastleManorManager();
		}
		return _instance;
	}

	public class CropProcure
	{
		int _rewardType;
		int _cropId;
		long _buyResidual;
		long _buy;
		long _price;

		public CropProcure(int id)
		{
			_cropId = id;
			_buyResidual = 0;
			_rewardType = 0;
			_buy = 0;
			_price = 0;
		}

		public CropProcure(int id, long amount, int type, long buy, long price)
		{
			_cropId = id;
			_buyResidual = amount;
			_rewardType = type;
			_buy = buy;
			_price = price;
			if(_price < 0)
			{
				_price = 0;
				_log.warning("CropProcure price = " + price);
				Thread.dumpStack();
			}
		}

		public int getReward()
		{
			return _rewardType;
		}

		public int getId()
		{
			return _cropId;
		}

		public long getAmount()
		{
			return _buyResidual;
		}

		public long getStartAmount()
		{
			return _buy;
		}

		public long getPrice()
		{
			return _price;
		}

		public void setAmount(long amount)
		{
			_buyResidual = amount;
		}
	}

	public class SeedProduction
	{
		int _seedId;
		long _residual;
		long _price;
		long _sales;

		public SeedProduction(int id)
		{
			_seedId = id;
			_sales = 0;
			_price = 0;
			_sales = 0;
		}

		public SeedProduction(int id, long amount, long price, long sales)
		{
			_seedId = id;
			_residual = amount;
			_price = price;
			_sales = sales;
		}

		public int getId()
		{
			return _seedId;
		}

		public long getCanProduce()
		{
			return _residual;
		}

		public long getPrice()
		{
			return _price;
		}

		public long getStartProduce()
		{
			return _sales;
		}

		public void setCanProduce(long amount)
		{
			_residual = amount;
		}
	}

	private CastleManorManager()
	{
		load(); // load data from database
		init(); // schedule all manor related events
		_underMaintenance = false;
		_disabled = !ConfigValue.AllowManor;
		for(Castle c : CastleManager.getInstance().getCastles().values())
			c.setNextPeriodApproved(ServerVariables.getBool(var_name));
	}

	private void load()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			// Get Connection
			con = L2DatabaseFactory.getInstance().getConnection();
			for(Castle castle : CastleManager.getInstance().getCastles().values())
			{
				GArray<SeedProduction> production = new GArray<SeedProduction>();
				GArray<SeedProduction> productionNext = new GArray<SeedProduction>();
				GArray<CropProcure> procure = new GArray<CropProcure>();
				GArray<CropProcure> procureNext = new GArray<CropProcure>();

				// restore seed production info
				statement = con.prepareStatement(CASTLE_MANOR_LOAD_PRODUCTION);
				statement.setInt(1, castle.getId());
				rs = statement.executeQuery();
				while(rs.next())
				{
					int seedId = rs.getInt("seed_id");
					long canProduce = rs.getLong("can_produce");
					long startProduce = rs.getLong("start_produce");
					long price = rs.getLong("seed_price");
					int period = rs.getInt("period");
					if(period == PERIOD_CURRENT)
						production.add(new SeedProduction(seedId, canProduce, price, startProduce));
					else
						productionNext.add(new SeedProduction(seedId, canProduce, price, startProduce));
				}

				DatabaseUtils.closeDatabaseSR(statement, rs);

				castle.setSeedProduction(production, PERIOD_CURRENT);
				castle.setSeedProduction(productionNext, PERIOD_NEXT);

				// restore procure info
				statement = con.prepareStatement(CASTLE_MANOR_LOAD_PROCURE);
				statement.setInt(1, castle.getId());
				rs = statement.executeQuery();
				while(rs.next())
				{
					int cropId = rs.getInt("crop_id");
					long canBuy = rs.getLong("can_buy");
					long startBuy = rs.getLong("start_buy");
					int rewardType = rs.getInt("reward_type");
					long price = rs.getLong("price");
					int period = rs.getInt("period");
					if(period == PERIOD_CURRENT)
						procure.add(new CropProcure(cropId, canBuy, rewardType, startBuy, price));
					else
						procureNext.add(new CropProcure(cropId, canBuy, rewardType, startBuy, price));
				}

				castle.setCropProcure(procure, PERIOD_CURRENT);
				castle.setCropProcure(procureNext, PERIOD_NEXT);

				if(!procure.isEmpty() || !procureNext.isEmpty() || !production.isEmpty() || !productionNext.isEmpty())
					_log.info("Manor System: Loaded data for " + castle.getName() + " castle");

				DatabaseUtils.closeDatabaseSR(statement, rs);
			}

			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
		catch(Exception e)
		{
			_log.info("Manor System: Error restoring manor data: " + e.getMessage());
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}

	protected void init()
	{
		if(ServerVariables.getString(var_name, "").isEmpty())
		{
			Calendar manorRefresh = Calendar.getInstance();
			manorRefresh.set(Calendar.HOUR_OF_DAY, MANOR_REFRESH);
			manorRefresh.set(Calendar.MINUTE, MANOR_REFRESH_MIN);
			manorRefresh.set(Calendar.SECOND, 0);
			manorRefresh.set(Calendar.MILLISECOND, 0);

			Calendar periodApprove = Calendar.getInstance();
			periodApprove.set(Calendar.HOUR_OF_DAY, NEXT_PERIOD_APPROVE);
			periodApprove.set(Calendar.MINUTE, NEXT_PERIOD_APPROVE_MIN);
			periodApprove.set(Calendar.SECOND, 0);
			periodApprove.set(Calendar.MILLISECOND, 0);
			boolean isApproved = periodApprove.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() && manorRefresh.getTimeInMillis() > Calendar.getInstance().getTimeInMillis();
			ServerVariables.set(var_name, isApproved);
		}

		Calendar FirstDelay = Calendar.getInstance();
		FirstDelay.set(Calendar.SECOND, 0);
		FirstDelay.set(Calendar.MILLISECOND, 0);
		FirstDelay.add(Calendar.MINUTE, 1);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new ManorTask(), FirstDelay.getTimeInMillis() - Calendar.getInstance().getTimeInMillis(), 60000);
	}

	public void setNextPeriod()
	{
		for(Castle c : CastleManager.getInstance().getCastles().values())
		{
			if(c.getOwnerId() <= 0)
				continue;

			L2Clan clan = ClanTable.getInstance().getClan(c.getOwnerId());
			if(clan == null)
				continue;

			Warehouse cwh = clan.getWarehouse();

			for(CropProcure crop : c.getCropProcure(PERIOD_CURRENT))
			{
				if(crop.getStartAmount() == 0)
					continue;

				// adding bought crops to clan warehouse
				if(crop.getStartAmount() > crop.getAmount())
				{
					_log.info("Manor System [" + c.getName() + "]: Start Amount of Crop " + crop.getStartAmount() + " > Amount of current " + crop.getAmount());
					long count = crop.getStartAmount() - crop.getAmount();

					count = count * 90 / 100;
					if(count < 1 && Rnd.get(99) < 90)
						count = 1;

					if(count >= 1)
					{
						int id = L2Manor.getInstance().getMatureCrop(crop.getId());
						cwh.addItem(id, count, null);
					}
				}

				// reserved and not used money giving back to treasury
				if(crop.getAmount() > 0)
				{
					c.addToTreasuryNoTax(crop.getAmount() * crop.getPrice(), false, false);
					Log.add(c.getName() + "|" + crop.getAmount() * crop.getPrice() + "|ManorManager|" + crop.getAmount() + "*" + crop.getPrice(), "treasury");
				}

				c.setCollectedShops(0);
				c.setCollectedSeed(0);
			}

			c.setSeedProduction(c.getSeedProduction(PERIOD_NEXT), PERIOD_CURRENT);
			c.setCropProcure(c.getCropProcure(PERIOD_NEXT), PERIOD_CURRENT);

			long manor_cost = c.getManorCost(PERIOD_CURRENT);
			if(c.getTreasury() < manor_cost)
			{
				c.setSeedProduction(getNewSeedsList(c.getId()), PERIOD_NEXT);
				c.setCropProcure(getNewCropsList(c.getId()), PERIOD_NEXT);
				Log.add(c.getName() + "|" + manor_cost + "|ManorManager Error@setNextPeriod", "treasury");
			}
			else
			{
				GArray<SeedProduction> production = new GArray<SeedProduction>();
				GArray<CropProcure> procure = new GArray<CropProcure>();
				for(SeedProduction s : c.getSeedProduction(PERIOD_CURRENT))
				{
					s.setCanProduce(s.getStartProduce());
					production.add(s);
				}
				for(CropProcure cr : c.getCropProcure(PERIOD_CURRENT))
				{
					cr.setAmount(cr.getStartAmount());
					procure.add(cr);
				}
				c.setSeedProduction(production, PERIOD_NEXT);
				c.setCropProcure(procure, PERIOD_NEXT);
			}

			c.saveCropData();
			c.saveSeedData();

			// Sending notification to a clan leader
			PlayerMessageStack.getInstance().mailto(clan.getLeaderId(), Msg.THE_MANOR_INFORMATION_HAS_BEEN_UPDATED);

			c.setNextPeriodApproved(false);
		}
	}

	public void approveNextPeriod()
	{
		for(Castle c : CastleManager.getInstance().getCastles().values())
		{
			// Castle has no owner
			if(c.getOwnerId() <= 0)
				continue;

			long manor_cost = c.getManorCost(PERIOD_NEXT);

			if(c.getTreasury() < manor_cost)
			{
				c.setSeedProduction(getNewSeedsList(c.getId()), PERIOD_NEXT);
				c.setCropProcure(getNewCropsList(c.getId()), PERIOD_NEXT);
				manor_cost = c.getManorCost(PERIOD_NEXT);
				if(manor_cost > 0)
					Log.add(c.getName() + "|" + -manor_cost + "|ManorManager Error@approveNextPeriod", "treasury");
				L2Clan clan = ClanTable.getInstance().getClan(c.getOwnerId());
				PlayerMessageStack.getInstance().mailto(clan.getLeaderId(), Msg.THE_AMOUNT_IS_NOT_SUFFICIENT_AND_SO_THE_MANOR_IS_NOT_IN_OPERATION);
			}
			else
			{
				c.addToTreasuryNoTax(-manor_cost, false, false);
				Log.add(c.getName() + "|" + -manor_cost + "|ManorManager", "treasury");
			}
			c.setNextPeriodApproved(true);
		}
	}

	private GArray<SeedProduction> getNewSeedsList(int castleId)
	{
		GArray<SeedProduction> seeds = new GArray<SeedProduction>();
		GArray<Integer> seedsIds = L2Manor.getInstance().getSeedsForCastle(castleId);
		for(int sd : seedsIds)
			seeds.add(new SeedProduction(sd));
		return seeds;
	}

	private GArray<CropProcure> getNewCropsList(int castleId)
	{
		GArray<CropProcure> crops = new GArray<CropProcure>();
		GArray<Integer> cropsIds = L2Manor.getInstance().getCropsForCastle(castleId);
		for(int cr : cropsIds)
			crops.add(new CropProcure(cr));
		return crops;
	}

	public boolean isUnderMaintenance()
	{
		return _underMaintenance;
	}

	public void setUnderMaintenance(boolean mode)
	{
		_underMaintenance = mode;
	}

	public boolean isDisabled()
	{
		return _disabled;
	}

	public void setDisabled(boolean mode)
	{
		_disabled = mode;
	}

	public SeedProduction getNewSeedProduction(int id, long amount, long price, long sales)
	{
		return new SeedProduction(id, amount, price, sales);
	}

	public CropProcure getNewCropProcure(int id, long amount, int type, long price, long buy)
	{
		return new CropProcure(id, amount, type, buy, price);
	}

	public void save()
	{
		for(Castle c : CastleManager.getInstance().getCastles().values())
		{
			c.saveSeedData();
			c.saveCropData();
		}
	}

	private class ManorTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			int H = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			int M = Calendar.getInstance().get(Calendar.MINUTE);

			if(ServerVariables.getBool(var_name)) // 06:00 - 20:00 
			{
				if(H < NEXT_PERIOD_APPROVE || H > MANOR_REFRESH || H == MANOR_REFRESH && M >= MANOR_REFRESH_MIN)
				{
					ServerVariables.set(var_name, false);
					setUnderMaintenance(true);
					_log.info("Manor System: Under maintenance mode started");
				}
			}
			else if(isUnderMaintenance()) // 20:00 - 20:06
			{
				if(H != MANOR_REFRESH || M >= MANOR_REFRESH_MIN + MAINTENANCE_PERIOD)
				{
					setUnderMaintenance(false);
					_log.info("Manor System: Next period started");
					if(isDisabled())
						return;
					setNextPeriod();
					try
					{
						save();
					}
					catch(Exception e)
					{
						_log.info("Manor System: Failed to save manor data: " + e);
					}
				}
			}
			else if(H > NEXT_PERIOD_APPROVE && H < MANOR_REFRESH || H == NEXT_PERIOD_APPROVE && M >= NEXT_PERIOD_APPROVE_MIN)
			{
				ServerVariables.set(var_name, true);
				_log.info("Manor System: Next period approved");
				if(isDisabled())
					return;
				approveNextPeriod();
			}
		}
	}
}
