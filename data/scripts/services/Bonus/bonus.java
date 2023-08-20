package services.Bonus;

import java.sql.ResultSet;
import java.util.Calendar;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExBrPremiumState;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;

import l2open.util.Log;
import l2open.util.Files;

public class bonus extends Functions implements ScriptFile
{
	private void remove_bonus(L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM bonus WHERE (obj_id = ? or account = ?)");
			statement.setInt(1, player.getObjectId());
			statement.setString(2, player.getAccountName());
			statement.executeUpdate();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
		for(int i=0;i<player._bonusExpiration2.length;i++)
			player.stopBonusTask(i);

		player.getNetConnection().setBonus(1);
		player.restoreBonus();
		if(player.getParty() != null)
			player.getParty().recalculatePartyData();
		String msg = new CustomMessage("scripts.services.RateBonus.LuckEnded", player).toString();
		player.sendPacket(new ExShowScreenMessage(msg, 10000, ScreenMessageAlign.TOP_CENTER, true), new ExBrPremiumState(player, 0));
		player.sendMessage(msg);
	}

	private boolean HaveBonus(String type)
	{
		L2Player player = (L2Player) getSelf();
		ResultSet rs = null;
		ThreadConnection con = null;
		FiltredPreparedStatement offline = null;
		Calendar end_;
		end_ = Calendar.getInstance();
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("SELECT bonus_expire_time FROM bonus WHERE (obj_id = ? or account = ?) AND bonus_name = ?");
			offline.setInt(1, player.getObjectId());
			offline.setString(2, player.getAccountName());
			offline.setString(3, type);
			rs = offline.executeQuery();
			if(rs.next())
			{
				long time = rs.getLong(1) * 1000L;
				if(time > System.currentTimeMillis())
				{
					end_.setTimeInMillis(time);
					if(!type.equals("EventSponsor"))
						show(new CustomMessage("scripts.services.Bonus.bonus.alreadychar", player).addString(String.valueOf(end_.get(Calendar.DAY_OF_MONTH)) + "." + String.valueOf(end_.get(Calendar.MONTH) + 1) + "." + String.valueOf(end_.get(Calendar.YEAR))).addString(String.valueOf(end_.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(end_.get(Calendar.MINUTE) + 1) + ":" + String.valueOf(end_.get(Calendar.SECOND))), player);
					else
						show(new CustomMessage("scripts.services.Bonus.bonus.alreadychar2", player).addString(String.valueOf(end_.get(Calendar.DAY_OF_MONTH)) + "." + String.valueOf(end_.get(Calendar.MONTH) + 1) + "." + String.valueOf(end_.get(Calendar.YEAR))).addString(String.valueOf(end_.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(end_.get(Calendar.MINUTE) + 1) + ":" + String.valueOf(end_.get(Calendar.SECOND))), player);
					return true;
				}
			}
			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			show(new CustomMessage("common.Error", player), player);
			return false;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, offline, rs);
		}
	}

	public void remove(String[] args)
	{
		L2Player player = (L2Player) getSelf();
		if(HaveBonus("RATE_ALL"))
		{
			remove_bonus(player);
		}
	}

	public void buy(String[] args)
	{
		L2Player player = (L2Player) getSelf();

		String bonus_class = "";
		int bonus_type = Integer.valueOf(args[0]);
		int day = Integer.valueOf(args[1]);
		int type = Integer.valueOf(args[2]);
		int BONUS_PRICE = Integer.MAX_VALUE;
		int token_count = Integer.MIN_VALUE;
		float BONUS_RATE = 1;

		try
		{
			switch(bonus_type)
			{
				case 0:
					//all
					bonus_class = "RATE_ALL";
					BONUS_PRICE = ConfigValue.BONUS_PRICE_ALL[day];
					BONUS_RATE = ConfigValue.BONUS_RATE_ALL[day];
					break;
				case 1:
					// exp
					bonus_class = "RATE_XP";
					BONUS_PRICE = ConfigValue.BONUS_PRICE_XP[day];
					BONUS_RATE = ConfigValue.BONUS_RATE_XP[day];
					break;
				case 2:
					// sp
					bonus_class = "RATE_SP";
					BONUS_PRICE = ConfigValue.BONUS_PRICE_SP[day];
					BONUS_RATE = ConfigValue.BONUS_RATE_SP[day];
					break;
				case 3:
					//drop
					bonus_class = "RATE_DROP_ITEMS";
					BONUS_PRICE = ConfigValue.BONUS_PRICE_DROP_ITEMS[day];
					BONUS_RATE = ConfigValue.BONUS_RATE_DROP_ITEMS[day];
					break;
				case 4:
					//adena
					bonus_class = "RATE_DROP_ADENA";
					BONUS_PRICE = ConfigValue.BONUS_PRICE_DROP_ADENA[day];
					BONUS_RATE = ConfigValue.BONUS_RATE_DROP_ADENA[day];
					break;
				case 5:
					//spoil
					bonus_class = "RATE_DROP_SPOIL";
					BONUS_PRICE = ConfigValue.BONUS_PRICE_DROP_SPOIL[day];
					BONUS_RATE = ConfigValue.BONUS_RATE_DROP_SPOIL[day];
					break;
				case 6:
					//quest
					bonus_class = "RATE_QUESTS_REWARD";
					BONUS_PRICE = ConfigValue.BONUS_PRICE_QUESTS_REWARD[day];
					BONUS_RATE = ConfigValue.BONUS_RATE_QUESTS_REWARD[day];
					break;
				case 7:
					//quest
					bonus_class = "RATE_QUESTS_DROP";
					BONUS_PRICE = ConfigValue.BONUS_PRICE_QUESTS_DROP[day];
					BONUS_RATE = ConfigValue.BONUS_RATE_QUESTS_DROP[day];
					break;
				case 8:
					//fame
					bonus_class = "RATE_FAME";
					BONUS_PRICE = ConfigValue.BONUS_PRICE_RATE_FAME[day];
					BONUS_RATE = ConfigValue.BONUS_RATE_FAME[day];
					break;
				case 9:
					//Epaulette
					bonus_class = "RATE_EPAULETTE";
					BONUS_PRICE = ConfigValue.BONUS_PRICE_RATE_EPAULETTE[day];
					BONUS_RATE = ConfigValue.BONUS_RATE_EPAULETTE[day];
					break;
				case 10:
					//TradePA
					bonus_class = "CanByTradeItemPA";
					BONUS_PRICE = ConfigValue.CanByTradeItemPA_Price[day];
					BONUS_RATE = 1;
					break;
				case 11:
					//EventSponsor
					bonus_class = "EventSponsor";
					BONUS_PRICE = ConfigValue.TheHungerGames_EventSponsor_Price[day];
					token_count = ConfigValue.TheHungerGames_EventSponsor_AddToken[day];
					BONUS_RATE = 1;
					break;
				case 12:
					//PremiumBuffer
					bonus_class = "PremiumBuffer";
					BONUS_PRICE = ConfigValue.PremiumBuffer_Price[day];
					BONUS_RATE = 1;
					break;
				case 13:
					//INDEX
					bonus_class = "INDEX";
					BONUS_PRICE = ConfigValue.BONUS_PRICE_INDEX[day];
					BONUS_RATE = day;
					break;
				
			}
		}
		catch(Exception e)
		{
			return;
		}
		if(HaveBonus(bonus_class))
			return;
		ThreadConnection con = null;
		FiltredPreparedStatement offline = null;
		Calendar bonus_expire = Calendar.getInstance();
		int price_id = ConfigValue.BONUS_PRICE_ID[Math.min(day, ConfigValue.BONUS_PRICE_ID.length-1)];
		if(getItemCount(player, price_id) < BONUS_PRICE)
		{
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}
		bonus_expire.add(Calendar.DAY_OF_MONTH, ConfigValue.BONUS_DAY[day]);
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("INSERT INTO bonus(obj_id, account, bonus_name, bonus_value, bonus_expire_time) VALUES (?,?,?,?,?)");
			offline.setInt(1, player.getObjectId());
			offline.setString(2, type == 0 ? "-1" : player.getAccountName());
			offline.setString(3, bonus_class);
			offline.setDouble(4, (double)BONUS_RATE); // Бонус рейты
			offline.setLong(5, bonus_expire.getTimeInMillis() / 1000);
			offline.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			show(new CustomMessage("common.Error", player), player);
			return;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, offline);
		}

		if(bonus_type == 11)
		{
			player.setVar("TheHungerGames_SponsorToken", String.valueOf(player.getVarInt("TheHungerGames_SponsorToken", 0)+token_count));
		}
		if(bonus_type != 10 && bonus_type != 11 && bonus_type != 12)
		{
			player.getNetConnection().setBonus(BONUS_RATE);
			player.getNetConnection().setBonusExpire(bonus_expire.getTimeInMillis() / 1000);
		}
		player.restoreBonus();
		if(bonus_type != 10 && bonus_type != 11 && bonus_type != 12)
		{
			player.sendPacket(new ExBrPremiumState(player, 1));
			if(player.getParty() != null)
				player.getParty().recalculatePartyData();
		}

		removeItem(player, price_id, BONUS_PRICE);
		Log.add("Character " + player.getName() + " buy bonus for char " + player.getName(), "bonus_event");

		String html;
		if(bonus_type == 11)
		{
			html = Files.read("data/scripts/services/Bonus/sponsor_ok.htm", player);
			html = html.replace("<?time?>", String.valueOf(ConfigValue.BONUS_DAY[day]));
			html = html.replace("<?token_add?>", String.valueOf(token_count));
			html = html.replace("<?token_all?>", String.valueOf(player.getVarInt("TheHungerGames_SponsorToken", 0)));
		}
		else if(bonus_type == 12)
		{
			html = Files.read("data/scripts/services/Bonus/buffer_ok.htm", player);
			html = html.replace("<?time?>", String.valueOf(ConfigValue.BONUS_DAY[day]));
		}
		else
		{
			html = Files.read("data/scripts/services/Bonus/bonus_ok.htm", player);
			html = html.replace("<?rate?>", String.valueOf(BONUS_RATE));
			html = html.replace("<?time?>", String.valueOf(ConfigValue.BONUS_DAY[day]));
		}
		show(html, player);
	}

	public void onLoad()
	{
		if(ConfigValue.BONUS_ENABLED)
			_log.info("Loaded Service: Bonus [state: activated]");
		else
			_log.info("Loaded Service: Bonus [state: deactivated]");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}