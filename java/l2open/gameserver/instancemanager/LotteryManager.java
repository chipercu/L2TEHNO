package l2open.gameserver.instancemanager;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Rnd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * User: Keiichi
 * Date: 24.11.2008
 * Time: 23:32:22
 * Порт с SF.
 */
public class LotteryManager
{
	public static final long SECOND = 1000;
	public static final long MINUTE = 60000;

	private static LotteryManager _instance;
	protected static Logger _log = Logger.getLogger(LotteryManager.class.getName());

	private static final String INSERT_LOTTERY = "INSERT INTO games(id, idnr, enddate, prize, newprize) VALUES (?, ?, ?, ?, ?)";
	private static final String UPDATE_PRICE = "UPDATE games SET prize=?, newprize=? WHERE id = 1 AND idnr = ?";
	private static final String UPDATE_LOTTERY = "UPDATE games SET finished=1, prize=?, newprize=?, number1=?, number2=?, prize1=?, prize2=?, prize3=? WHERE id=1 AND idnr=?";
	private static final String SELECT_LAST_LOTTERY = "SELECT idnr, prize, newprize, enddate, finished FROM games WHERE id = 1 ORDER BY idnr DESC LIMIT 1";
	private static final String SELECT_LOTTERY_ITEM = "SELECT enchant_level, custom_type2 FROM items WHERE item_id = 4442 AND custom_type1 = ?";
	private static final String SELECT_LOTTERY_TICKET = "SELECT number1, number2, prize1, prize2, prize3 FROM games WHERE id = 1 AND idnr = ?";

	protected int _number;
	protected long _prize;
	protected boolean _isSellingTickets;
	protected boolean _isStarted;
	protected long _enddate;

	public LotteryManager()
	{
		_number = 1;
		_prize = ConfigValue.LotteryPrize;
		_isSellingTickets = false;
		_isStarted = false;
		_enddate = System.currentTimeMillis();

		if(ConfigValue.AllowLottery)
			new startLottery().run();
	}

	private class startLottery extends l2open.common.RunnableImpl
	{
		protected startLottery()
		{
		// Do nothing
		}

		public void runImpl()
		{
			if(restoreLotteryData())
			{
				announceLottery();
				scheduleEndOfLottery();
				createNewLottery();
			}
		}
	}

	/*public void increasePrize(long count)
	{
		_prize += count;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(UPDATE_PRICE);
			statement.setLong(1, getPrize());
			statement.setLong(2, getPrize());
			statement.setInt(3, getId());
			statement.execute();
			statement.close();
		}
		catch(SQLException e)
		{
			_log.warning("Lottery: Could not increase current lottery prize: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}*/

	private boolean restoreLotteryData()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_LAST_LOTTERY);
			ResultSet rset = statement.executeQuery();

			if(rset.next())
			{
				_number = rset.getInt("idnr");

				if(rset.getInt("finished") == 1)
				{
					_number++;
					_prize = rset.getLong("newprize");
				}
				else
				{
					_prize = rset.getLong("prize");
					_enddate = rset.getLong("enddate");

					if(_enddate <= System.currentTimeMillis() + 2 * MINUTE)
					{
						new finishLottery().run();
						rset.close();
						statement.close();
						return false;
					}

					if(_enddate > System.currentTimeMillis())
					{

						_isStarted = true;
						ThreadPoolManager.getInstance().schedule(new finishLottery(), _enddate - System.currentTimeMillis());

						if(_enddate > System.currentTimeMillis() + 12 * MINUTE)
						{
							_isSellingTickets = true;
							ThreadPoolManager.getInstance().schedule(new stopSellingTickets(), _enddate - System.currentTimeMillis() - 10 * MINUTE);
						}
						rset.close();
						statement.close();
						return false;
					}
				}
			}
			rset.close();
			statement.close();
		}
		catch(SQLException e)
		{
			_log.warning("Lottery: Could not restore lottery data: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}

		return true;
	}

	private void announceLottery()
	{
		if(ConfigValue.AllowLottery)
			_log.info("Lottery: Starting ticket sell for lottery #" + getId() + ".");
		_isSellingTickets = true;
		_isStarted = true;

		Announcements.getInstance().announceToAll("Lottery tickets are now available for Lucky Lottery #" + getId() + ".");
	}

	private void scheduleEndOfLottery()
	{
		//ThreadConnection con = null;
		//FiltredPreparedStatement statement;
		/** Calendar finishtime = Calendar.getInstance();
		 finishtime.setTimeInMillis(_enddate);
		 finishtime.set(Calendar.MINUTE, 0);
		 finishtime.set(Calendar.SECOND, 0);
		 finishtime.add(Calendar.DAY_OF_MONTH, 7);
		 finishtime.set(Calendar.DAY_OF_WEEK, 6);
		 finishtime.set(Calendar.HOUR_OF_DAY, 7);
		 _enddate = finishtime.getTimeInMillis();

		 ThreadPoolManager.getInstance().schedule(new stopSellingTickets(), _enddate - System.currentTimeMillis() - 10 * MINUTE);
		 ThreadPoolManager.getInstance().schedule(new finishLottery(), _enddate - System.currentTimeMillis());
		 **/

		Calendar finishtime = Calendar.getInstance();
		finishtime.setTimeInMillis(_enddate);
		finishtime.set(Calendar.MINUTE, 0);
		finishtime.set(Calendar.SECOND, 0);

		if(finishtime.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
		{
			finishtime.set(Calendar.HOUR_OF_DAY, 19);
			_enddate = finishtime.getTimeInMillis();
			_enddate += 604800000;
		}
		else
		{
			finishtime.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			finishtime.set(Calendar.HOUR_OF_DAY, 19);
			_enddate = finishtime.getTimeInMillis();
		}

		ThreadPoolManager.getInstance().schedule(new stopSellingTickets(), _enddate - System.currentTimeMillis() - 10 * MINUTE);
		ThreadPoolManager.getInstance().schedule(new finishLottery(), _enddate - System.currentTimeMillis());
	}

	private void createNewLottery()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_LOTTERY);
			statement.setInt(1, 1);
			statement.setInt(2, getId());
			statement.setLong(3, getEndDate());
			statement.setLong(4, getPrize());
			statement.setLong(5, getPrize());
			statement.execute();
			statement.close();
		}
		catch(SQLException e)
		{
			_log.warning("Lottery: Could not store new lottery data: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	private class stopSellingTickets extends l2open.common.RunnableImpl
	{
		protected stopSellingTickets()
		{
		// Do nothing
		}

		public void runImpl()
		{
			if(ConfigValue.AllowLottery)
				_log.info("Lottery: Stopping ticket sell for lottery #" + getId() + ".");
			_isSellingTickets = false;

			Announcements.getInstance().announceToAll(Msg.LOTTERY_TICKET_SALES_HAVE_BEEN_TEMPORARILY_SUSPENDED);
		}
	}

	private class finishLottery extends l2open.common.RunnableImpl
	{
		protected finishLottery()
		{
		// Do nothing
		}

		public void runImpl()
		{
			if(ConfigValue.AllowLottery)
				_log.info("Lottery: Ending lottery #" + getId() + ".");

			int[] luckynums = new int[5];
			int luckynum = 0;

			for(int i = 0; i < 5; i++)
			{
				boolean found = true;

				while(found)
				{
					luckynum = Rnd.get(20) + 1;
					found = false;

					for(int j = 0; j < i; j++)
						if(luckynums[j] == luckynum)
							found = true;
				}

				luckynums[i] = luckynum;
			}

			if(ConfigValue.AllowLottery)
				_log.info("Lottery: The lucky numbers are " + luckynums[0] + ", " + luckynums[1] + ", " + luckynums[2] + ", " + luckynums[3] + ", " + luckynums[4] + ".");

			int enchant = 0;
			int type2 = 0;

			for(int i = 0; i < 5; i++)
				if(luckynums[i] < 17)
					enchant += Math.pow(2, luckynums[i] - 1);
				else
					type2 += Math.pow(2, luckynums[i] - 17);

			if(ConfigValue.AllowLottery)
				_log.info("Lottery: Encoded lucky numbers are " + enchant + ", " + type2);

			int count1 = 0;
			int count2 = 0;
			int count3 = 0;
			int count4 = 0;

			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(SELECT_LOTTERY_ITEM);
				statement.setInt(1, getId());
				ResultSet rset = statement.executeQuery();

				while(rset.next())
				{
					int curenchant = rset.getInt("enchant_level") & enchant;
					int curtype2 = rset.getInt("custom_type2") & type2;

					if(curenchant == 0 && curtype2 == 0)
						continue;

					int count = 0;

					for(int i = 1; i <= 16; i++)
					{
						int val = curenchant / 2;

						if(val != (double) curenchant / 2)
							count++;

						int val2 = curtype2 / 2;

						if(val2 != (double) curtype2 / 2)
							count++;

						curenchant = val;
						curtype2 = val2;
					}

					if(count == 5)
						count1++;
					else if(count == 4)
						count2++;
					else if(count == 3)
						count3++;
					else if(count > 0)
						count4++;
				}
				rset.close();
				statement.close();
			}
			catch(SQLException e)
			{
				_log.warning("Lottery: Could restore lottery data: " + e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}

			long prize4 = count4 * ConfigValue.Lottery2and1NumberPrize;
			long prize1 = 0;
			long prize2 = 0;
			long prize3 = 0;
			long newprize;

			if(count1 > 0)
				prize1 = (long) ((getPrize() - prize4) * ConfigValue.Lottery5NumberRate / count1);

			if(count2 > 0)
				prize2 = (long) ((getPrize() - prize4) * ConfigValue.Lottery4NumberRate / count2);

			if(count3 > 0)
				prize3 = (long) ((getPrize() - prize4) * ConfigValue.Lottery3NumberRate / count3);

			//TODO: Уточнить что происходит с джекпотом на оффе. Если с проигрышем всех участников джекпот уменьшается то до каких приделов.
			if(prize1 == 0 && prize2 == 0 && prize3 == 0)
				newprize = getPrize();
			else
				newprize = getPrize() + prize1 + prize2 + prize3;

			if(ConfigValue.AllowLottery)
				_log.info("Lottery: Jackpot for next lottery is " + newprize + ".");

			SystemMessage sm;
			if(count1 > 0)
			{
				// There are winners.
				sm = new SystemMessage(SystemMessage.THE_PRIZE_AMOUNT_FOR_THE_WINNER_OF_LOTTERY__S1__IS_S2_ADENA_WE_HAVE_S3_FIRST_PRIZE_WINNERS);
				sm.addNumber(getId());
				sm.addNumber(getPrize());
				sm.addNumber(count1);
				Announcements.getInstance().announceToAll(sm);
			}
			else
			{
				// There are no winners.
				sm = new SystemMessage(SystemMessage.THE_PRIZE_AMOUNT_FOR_LUCKY_LOTTERY__S1__IS_S2_ADENA_THERE_WAS_NO_FIRST_PRIZE_WINNER_IN_THIS_DRAWING_THEREFORE_THE_JACKPOT_WILL_BE_ADDED_TO_THE_NEXT_DRAWING);
				sm.addNumber(getId());
				sm.addNumber(getPrize());
				Announcements.getInstance().announceToAll(sm);
			}
			con = null;
			try
			{

				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(UPDATE_LOTTERY);
				statement.setLong(1, getPrize());
				statement.setLong(2, newprize);
				statement.setInt(3, enchant);
				statement.setInt(4, type2);
				statement.setLong(5, prize1);
				statement.setLong(6, prize2);
				statement.setLong(7, prize3);
				statement.setInt(8, getId());
				statement.execute();
				statement.close();
			}
			catch(SQLException e)
			{
				_log.warning("Lottery: Could not store finished lottery data: " + e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}

			ThreadPoolManager.getInstance().schedule(new startLottery(), 1 * MINUTE);
			_number++;

			_isStarted = false;
		}
	}

	public int[] decodeNumbers(int enchant, int type2)
	{
		int res[] = new int[5];
		int id = 0;
		int nr = 1;

		while(enchant > 0)
		{
			int val = enchant / 2;
			if(val != (double) enchant / 2)
				res[id++] = nr;
			enchant /= 2;
			nr++;
		}

		nr = 17;

		while(type2 > 0)
		{
			int val = type2 / 2;
			if(val != (double) type2 / 2)
				res[id++] = nr;
			type2 /= 2;
			nr++;
		}

		return res;
	}

	public int[] checkTicket(int id, int enchant, int type2)
	{
		int res[] = { 0, 0 };

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_LOTTERY_TICKET);
			statement.setInt(1, id);
			ResultSet rset = statement.executeQuery();

			if(rset.next())
			{
				int curenchant = rset.getInt("number1") & enchant;
				int curtype2 = rset.getInt("number2") & type2;

				if(curenchant == 0 && curtype2 == 0)
				{
					rset.close();
					statement.close();
					return res;
				}

				int count = 0;

				for(int i = 1; i <= 16; i++)
				{
					int val = curenchant / 2;
					if(val != (double) curenchant / 2)
						count++;
					int val2 = curtype2 / 2;
					if(val2 != (double) curtype2 / 2)
						count++;
					curenchant = val;
					curtype2 = val2;
				}

				switch(count)
				{
					case 0:
						break;
					case 5:
						res[0] = 1;
						res[1] = rset.getInt("prize1");
						break;
					case 4:
						res[0] = 2;
						res[1] = rset.getInt("prize2");
						break;
					case 3:
						res[0] = 3;
						res[1] = rset.getInt("prize3");
						break;
					default:
						res[0] = 4;
						res[1] = 200;
				}
			}

			rset.close();
			statement.close();
		}
		catch(SQLException e)
		{
			_log.warning("Lottery: Could not check lottery ticket #" + id + ": " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}

		return res;
	}

	public int[] checkTicket(L2ItemInstance item)
	{
		return checkTicket(item.getCustomType1(), item.getRealEnchantLevel(), item.getCustomType2());
	}

	public boolean isSellableTickets()
	{
		return _isSellingTickets;
	}

	public boolean isStarted()
	{
		return _isStarted;
	}

	public static LotteryManager getInstance()
	{
		if(_instance == null)
			_instance = new LotteryManager();

		return _instance;
	}

	public int getId()
	{
		return _number;
	}

	public long getPrize()
	{
		return _prize;
	}

	public long getEndDate()
	{
		return _enddate;
	}
}