package l2open.extensions;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExBrPremiumState;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Bonus
{
	L2Player _owner = null;
	/** Rate control */
	
	// bypass -h _bbsscripts;82;services.Bonus.bonus:buy 13 1 0
	/**
	BONUS_ENABLED = true
# Итем за который продаем ПА.
# если для всех вариантов ПА итем один, можно прописать BONUS_PRICE_ID = 4037
BONUS_PRICE_ID = 4037;4037;4037

BONUS_PRICE_INDEX=10;100;1000

BONUS_DAY = 1;7;30

BONUS_RATE_XP = 1;1;1
BONUS_RATE_SP = 1;1;1
BONUS_RATE_QUESTS_REWARD = 1;1;1
BONUS_RATE_QUESTS_DROP = 1;1;1
BONUS_RATE_DROP_ADENA = 1;1;1
BONUS_RATE_DROP_ITEMS = 1;1;1
BONUS_RATE_DROP_SPOIL = 1;1;1
# MOD
BONUS_RATE_TOKEN = 1;1;1
BONUS_RATE_FAME = 1;1;1
BONUS_RATE_MAX_LOAD = 1;1;1
BONUS_RATE_EPAULETTE = 1;1;1
BONUS_RATE_CRAFT = 1;1;1
BONUS_RATE_CRAFT_MASTER_WORK = 1;1;1
BONUS_RATE_ENCHANT = 0;0;0
BONUS_RATE_ENCHANT_BLESSED = 0;0;0

BONUS_RATE_ENCHANT_MUL = 2;2;2
BONUS_RATE_ENCHANT_BLESSED_MUL = 2;2;2

	**/
	public int INDEX = -1;

	public float RATE_XP = 1;
	public float RATE_SP = 1;
	public float RATE_QUESTS_REWARD = 1;
	public float RATE_QUESTS_DROP = 1;
	public float RATE_DROP_ADENA = 1;
	public float RATE_DROP_ITEMS = 1;
	public float RATE_DROP_SPOIL = 1;
	public float RATE_EPAULETTE = 1;
	public float RATE_FAME = 1;
	public float RATE_MAX_LOAD = 1;
	public float RATE_TOKEN = 1;

	public float RATE_CRAFT = 1;
	public float RATE_CRAFT_MASTER_WORK = 1;
	public float RATE_ENCHANT = 0;
	public float RATE_ENCHANT_BLESSED = 0;
	public float RATE_ENCHANT_MUL = 1;
	public float RATE_ENCHANT_BLESSED_MUL = 1;
	public float RATE_ALL = 1;
	public boolean CanByTradeItemPA = false;
	public boolean EventSponsor = false;
	public boolean PremiumBuffer = false;

	public long[] bonus_expire_time = new long[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

	public Bonus(L2Player player)
	{
		if(player == null || player.getNetConnection() == null) // игрок отвалился при входе
			return;

		_owner = player;
		if(player.isBot())
			return;
		mysql.set("DELETE FROM bonus WHERE bonus_expire_time<"+System.currentTimeMillis() / 1000);
		if(ConfigValue.BONUS_ENABLED)
			restore();
		else
		{
			restore2();
			restore3();
			restore4();
		}

	//	System.out.println("Bonus[0]: RATE_ALL="+RATE_ALL+" RATE_DROP_ADENA="+RATE_DROP_ADENA);
		float bonus = 1f;
		if(ConfigValue.RateBonusEnabled || ConfigValue.RateBonusApplyRatesThenServiceDisabled)
		{
			if(player.getNetConnection() == null)
				return;

			bonus = player.getNetConnection().getBonus();

			if(player.getNetConnection().getBonusExpire() > System.currentTimeMillis() / 1000)
				player.startBonusTask(player.getNetConnection().getBonusExpire());

			player.sendPacket(new ExBrPremiumState(player, bonus > 1 ? 1 : 0));

			if(bonus > 1)
			{
				RATE_TOKEN = ConfigValue.RATE_TOKEN;
				RATE_XP = bonus;
				RATE_SP = bonus;
				RATE_DROP_ADENA = bonus * ConfigValue.PersonalAdenaRate;
				RATE_DROP_ITEMS = bonus;
				RATE_DROP_SPOIL = bonus;
				//System.out.println("Bonus[1]: RATE_ALL="+RATE_ALL+" RATE_DROP_ADENA="+RATE_DROP_ADENA);
				if(ConfigValue.AffordBonus)
				{
					RATE_QUESTS_REWARD = bonus;
					RATE_QUESTS_DROP = bonus;
					
					RATE_FAME = bonus;
				}
				else if(ConfigValue.L2NameBonus)
				{
					RATE_EPAULETTE = bonus;
					RATE_FAME = bonus;
				}
				else if(ConfigValue.RateMaxLoad)
				{
					RATE_MAX_LOAD = RATE_ALL;
				}
				else if(ConfigValue.L2HunterBonus)
				{
					RATE_EPAULETTE = bonus;
				}
			}
		}
	//	System.out.println("Bonus[2]: RATE_ALL="+RATE_ALL+" RATE_DROP_ADENA="+RATE_DROP_ADENA);
		if(ConfigValue.BONUS_ENABLED && bonus <= 1)
		{
			boolean isBonus = false;
			for(int i=0;i<bonus_expire_time.length;i++)
				if(bonus_expire_time[i] > 0)
				{
					if(i != 10 && i != 11 && i != 12)
					{
						isBonus = true;
						player.getNetConnection().setBonus(2);
					}
					if(bonus_expire_time[i] > System.currentTimeMillis() / 1000)
						player.startBonusTask(bonus_expire_time[i], i);
				}
			if(INDEX > -1)
			{
				RATE_XP = ConfigValue.BONUS_RATE_XP[INDEX];
				RATE_SP = ConfigValue.BONUS_RATE_SP[INDEX];
				RATE_QUESTS_REWARD = ConfigValue.BONUS_RATE_QUESTS_REWARD[INDEX];
				RATE_QUESTS_DROP = ConfigValue.BONUS_RATE_QUESTS_DROP[INDEX];
				RATE_DROP_ADENA = ConfigValue.BONUS_RATE_DROP_ADENA[INDEX];
				RATE_DROP_ITEMS = ConfigValue.BONUS_RATE_DROP_ITEMS[INDEX];
				RATE_DROP_SPOIL = ConfigValue.BONUS_RATE_DROP_SPOIL[INDEX];

				RATE_TOKEN = ConfigValue.BONUS_RATE_TOKEN[INDEX];
				RATE_FAME = ConfigValue.BONUS_RATE_FAME[INDEX];
				RATE_MAX_LOAD = ConfigValue.BONUS_RATE_MAX_LOAD[INDEX];
				RATE_EPAULETTE = ConfigValue.BONUS_RATE_EPAULETTE[INDEX];
				RATE_CRAFT = ConfigValue.BONUS_RATE_CRAFT[INDEX];
				RATE_CRAFT_MASTER_WORK = ConfigValue.BONUS_RATE_CRAFT_MASTER_WORK[INDEX];
				RATE_ENCHANT = ConfigValue.BONUS_RATE_ENCHANT[INDEX];
				RATE_ENCHANT_BLESSED = ConfigValue.BONUS_RATE_ENCHANT_BLESSED[INDEX];

				RATE_ENCHANT_MUL = ConfigValue.BONUS_RATE_ENCHANT_MUL[INDEX];
				RATE_ENCHANT_BLESSED_MUL = ConfigValue.BONUS_RATE_ENCHANT_BLESSED_MUL[INDEX];
				if(INDEX == ConfigValue.CanByTradeItemPA)
					CanByTradeItemPA = true;
			}
			else if(RATE_ALL > 1)
			{
				RATE_TOKEN = ConfigValue.RATE_TOKEN;
				RATE_XP = RATE_ALL;
				RATE_SP = RATE_ALL;
				RATE_DROP_ADENA = RATE_ALL * ConfigValue.PersonalAdenaRate;
				RATE_DROP_ITEMS = RATE_ALL;
				RATE_DROP_SPOIL = RATE_ALL;
				//System.out.println("Bonus[3]: RATE_ALL="+RATE_ALL+" RATE_DROP_ADENA="+RATE_DROP_ADENA);
				if(RATE_ALL == ConfigValue.CanByTradeItemPA)
					CanByTradeItemPA = true;
				if(ConfigValue.AffordBonus)
				{
					RATE_QUESTS_REWARD = RATE_ALL;
					RATE_QUESTS_DROP = RATE_ALL;
					
					RATE_FAME = RATE_ALL;
				}
				else if(ConfigValue.L2NameBonus)
				{
					RATE_EPAULETTE = RATE_ALL;
					RATE_FAME = RATE_ALL;
				}
				else if(ConfigValue.RateMaxLoad)
				{
					RATE_MAX_LOAD = RATE_ALL;
				}
				else if(ConfigValue.L2HunterBonus)
				{
					RATE_EPAULETTE = RATE_ALL;
				}
			}
			player.sendPacket(new ExBrPremiumState(player, isBonus ? 1 : 0));
		}
		//System.out.println("Bonus[4]: RATE_ALL="+RATE_ALL+" RATE_DROP_ADENA="+RATE_DROP_ADENA);
	}

	private void restore2()
	{
		ThreadConnection con = null;
		FiltredStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			rset = statement.executeQuery("SELECT bonus_value,bonus_expire_time from bonus where (obj_id='" + _owner.getObjectId() + "'or account='"+_owner.getAccountName()+"') and (bonus_expire_time='-1' or bonus_expire_time > " + System.currentTimeMillis() / 1000 + ") and bonus_name='CanByTradeItemPA'");
			while(rset.next())
			{
				CanByTradeItemPA = rset.getFloat("bonus_value") == 1;
				bonus_expire_time[10] = rset.getLong("bonus_expire_time");
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		if(bonus_expire_time[10] > System.currentTimeMillis() / 1000)
			_owner.startBonusTask(bonus_expire_time[10], 10);
	}

	private void restore3()
	{
		ThreadConnection con = null;
		FiltredStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			rset = statement.executeQuery("SELECT bonus_value,bonus_expire_time from bonus where (obj_id='" + _owner.getObjectId() + "'or account='"+_owner.getAccountName()+"') and (bonus_expire_time='-1' or bonus_expire_time > " + System.currentTimeMillis() / 1000 + ") and bonus_name='EventSponsor'");
			while(rset.next())
			{
				EventSponsor = rset.getFloat("bonus_value") == 1;
				bonus_expire_time[11] = rset.getLong("bonus_expire_time");
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		if(bonus_expire_time[11] > System.currentTimeMillis() / 1000)
			_owner.startBonusTask(bonus_expire_time[11], 11);
	}

	private void restore4()
	{
		ThreadConnection con = null;
		FiltredStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			rset = statement.executeQuery("SELECT bonus_value,bonus_expire_time from bonus where (obj_id='" + _owner.getObjectId() + "'or account='"+_owner.getAccountName()+"') and (bonus_expire_time='-1' or bonus_expire_time > " + System.currentTimeMillis() / 1000 + ") and bonus_name='PremiumBuffer'");
			while(rset.next())
			{
				PremiumBuffer = rset.getFloat("bonus_value") == 1;
				bonus_expire_time[12] = rset.getLong("bonus_expire_time");
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		if(bonus_expire_time[12] > System.currentTimeMillis() / 1000)
			_owner.startBonusTask(bonus_expire_time[12], 12);
	}

	private void restore()
	{
		ThreadConnection con = null;
		FiltredStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			rset = statement.executeQuery("SELECT bonus_name,bonus_value,bonus_expire_time from bonus where (obj_id='" + _owner.getObjectId() + "'or account='"+_owner.getAccountName()+"') and (bonus_expire_time='-1' or bonus_expire_time > " + System.currentTimeMillis() / 1000 + ")");
			while(rset.next())
			{
				String bonus_name = rset.getString("bonus_name");
				float bonus_value = rset.getFloat("bonus_value");
				Class<?> cls = getClass();
				try
				{
					Field fld = cls.getField(bonus_name);
					try
					{
						if(bonus_name.equals("CanByTradeItemPA") || bonus_name.equals("EventSponsor") || bonus_name.equals("PremiumBuffer"))
							fld.setBoolean(this, bonus_value == 1);
						else if(bonus_name.equals("INDEX"))
							fld.setInt(this, (int)bonus_value);
						else
							fld.setFloat(this, bonus_value);
					}
					catch(IllegalArgumentException e)
					{
						e.printStackTrace();
					}
					catch(IllegalAccessException e)
					{
						e.printStackTrace();
					}
				}
				catch(SecurityException e)
				{
					e.printStackTrace();
				}
				catch(NoSuchFieldException e)
				{
					e.printStackTrace();
				}
				if(bonus_name.equals("RATE_XP"))
					bonus_expire_time[0] = rset.getLong("bonus_expire_time");
				else if(bonus_name.equals("RATE_SP"))
					bonus_expire_time[1] = rset.getLong("bonus_expire_time");
				else if(bonus_name.equals("RATE_QUESTS_REWARD"))
					bonus_expire_time[2] = rset.getLong("bonus_expire_time");
				else if(bonus_name.equals("RATE_QUESTS_DROP"))
					bonus_expire_time[3] = rset.getLong("bonus_expire_time");
				else if(bonus_name.equals("RATE_DROP_ADENA"))
					bonus_expire_time[4] = rset.getLong("bonus_expire_time");
				else if(bonus_name.equals("RATE_DROP_ITEMS"))
					bonus_expire_time[5] = rset.getLong("bonus_expire_time");
				else if(bonus_name.equals("RATE_DROP_SPOIL"))
					bonus_expire_time[6] = rset.getLong("bonus_expire_time");
				else if(bonus_name.equals("RATE_ALL"))
					bonus_expire_time[7] = rset.getLong("bonus_expire_time");
				else if(bonus_name.equals("RATE_FAME"))
					bonus_expire_time[8] = rset.getLong("bonus_expire_time");
				else if(bonus_name.equals("RATE_EPAULETTE"))
					bonus_expire_time[9] = rset.getLong("bonus_expire_time");
				else if(bonus_name.equals("CanByTradeItemPA"))
					bonus_expire_time[10] = rset.getLong("bonus_expire_time");
				else if(bonus_name.equals("EventSponsor"))
					bonus_expire_time[11] = rset.getLong("bonus_expire_time");
				else if(bonus_name.equals("PremiumBuffer"))
					bonus_expire_time[12] = rset.getLong("bonus_expire_time");
				else if(bonus_name.equals("INDEX"))
					bonus_expire_time[13] = rset.getLong("bonus_expire_time");
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}
}