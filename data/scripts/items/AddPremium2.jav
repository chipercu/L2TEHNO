package items;

import java.sql.ResultSet;
import java.util.Calendar;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExBrPremiumState;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Files;
import l2open.util.Log;
import l2open.util.Rnd;

public class AddPremium2 extends Functions implements IItemHandler, ScriptFile
{
	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;

		L2Player player = (L2Player) playable;

		if(!ConfigValue.PremiumUseEnable)
			return;

		if(player.isInOlympiadMode())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return;
		}

		if(player.isOutOfControl() || player.isDead() || player.isStunned() || player.isSleeping() || player.isParalyzed())
			return;

		int index = getIndex(item.getItemId());
		if(index < 0)
			return;
		if(HaveBonus(player))
			return;

		L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
		player.getInventory().destroyItem(pay, 1, true);
		int time = ConfigValue.PremiumUseRandomDays ? Rnd.get(ConfigValue.PremiumRandomDays[0], ConfigValue.PremiumRandomDays[1]) : ConfigValue.PremiumUseDays[index];

		if(time == 0)
		{
			DifferentMethods.sendMessage(player, new CustomMessage("scripts.items.AddPremium.null", player));
			return;
		}

		float value = ConfigValue.PremiumUseValue[index];

		ThreadConnection con = null;
		FiltredPreparedStatement offline = null;
		Calendar bonus_expire = Calendar.getInstance();
		bonus_expire.add(Calendar.DAY_OF_MONTH, time);
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("INSERT INTO bonus(obj_id, account, bonus_name, bonus_value, bonus_expire_time) VALUES (?,?,?,?,?)");
			offline.setInt(1, player.getObjectId());
			offline.setString(2, player.getAccountName());
			offline.setString(3, "RATE_ALL");
			offline.setDouble(4, (double)value); // Бонус рейты
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
		
		player.getNetConnection().setBonus(value);
		player.getNetConnection().setBonusExpire(bonus_expire.getTimeInMillis() / 1000);
		player.restoreBonus();
		player.sendPacket(new ExBrPremiumState(player, 1));
		if(player.getParty() != null)
			player.getParty().recalculatePartyData();

		show(Files.read("data/scripts/services/RateBonusGet.htm", player), player);
		player.broadcastPacket(new MagicSkillUse(player, player, 6176, 1, 1000, 0));

		DifferentMethods.sendMessage(player, new CustomMessage("scripts.items.AddPremium.add", player).addNumber(time).addString(DifferentMethods.declension(player, time, "Days")));
		Log.add(player.getName() + "|" + player.getObjectId() + "|rate bonus|" + value + "|" + time + "|", "services");
	}

	private boolean HaveBonus(L2Player player)
	{
		ResultSet rs = null;
		ThreadConnection con = null;
		FiltredPreparedStatement offline = null;
		Calendar end_ = Calendar.getInstance();
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("SELECT bonus_expire_time FROM bonus WHERE account = ? AND bonus_name = RATE_ALL");
			offline.setString(1, player.getAccountName());
			rs = offline.executeQuery();
			if(rs.next())
			{
				long time = rs.getLong(1) * 1000L;
				if(time > System.currentTimeMillis())
				{
					end_.setTimeInMillis(time);
					//show(new CustomMessage("scripts.services.Bonus.bonus.alreadychar", player).addString(String.valueOf(end_.get(Calendar.DAY_OF_MONTH)) + "." + String.valueOf(end_.get(Calendar.MONTH) + 1) + "." + String.valueOf(end_.get(Calendar.YEAR))), player);
					return true;
				}
			}
			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//show(new CustomMessage("common.Error", player), player);
			return false;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, offline, rs);
		}
	}

	private int getIndex(int item_id)
	{
		for(int i=0;i<ConfigValue.PremiumUseItems.length;i++)
			if(ConfigValue.PremiumUseItems[i] == item_id)
				return i;
		return -1;
	}

	public final int[] getItemIds()
	{
		return ConfigValue.PremiumUseItems;
	}

	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}
