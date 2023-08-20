package items;

import java.sql.SQLException;

import l2open.config.ConfigValue;
import l2open.database.L2DatabaseFactory;
import l2open.database.mysql;
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

public class AddPremium extends Functions implements IItemHandler, ScriptFile
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

		L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
		player.getInventory().destroyItem(pay, 1, true);
		int time = ConfigValue.PremiumUseRandomDays ? Rnd.get(ConfigValue.PremiumRandomDays[0], ConfigValue.PremiumRandomDays[1]) : ConfigValue.PremiumUseDays[index];

		if(time == 0)
		{
			DifferentMethods.sendMessage(player, new CustomMessage("scripts.items.AddPremium.null", player));
			return;
		}

		float value = ConfigValue.PremiumUseValue[index];

		if(player.getNetConnection().getBonus() > 1)
			update(player, time, value);
		else
		{
			try
			{
				mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE `accounts` SET `bonus`=?,`bonus_expire`=UNIX_TIMESTAMP()+" + time + "*24*60*60 WHERE `login`=?", value, player.getAccountName());
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}

			player.getNetConnection().setBonus(value);
			player.getNetConnection().setBonusExpire(System.currentTimeMillis() / 1000 + time * 24 * 60 * 60);
			player.restoreBonus();
			player.sendPacket(new ExBrPremiumState(player, 1));
			if(player.getParty() != null)
				player.getParty().recalculatePartyData();
			show(Files.read("data/scripts/services/RateBonusGet.htm", player), player);
			player.broadcastSkill(new MagicSkillUse(player, player, 6176, 1, 1000, 0), true);

			DifferentMethods.sendMessage(player, new CustomMessage("scripts.items.AddPremium.add", player).addNumber(time).addString(DifferentMethods.declension(player, time, "Days")));

			Log.add(player.getName() + "|" + player.getObjectId() + "|rate bonus|" + value + "|" + time + "|", "services");
		}
	}

	public void update(L2Player player, int days, float value)
	{
		int Total = (int) ((player.getNetConnection().getBonusExpire() - System.currentTimeMillis() / 1000L));
		int Day = Math.round(Total / 60 / 60 / 24);
		long time = (Day + days) * 24 * 60 * 60;
		try
		{
			mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE `accounts` SET `bonus`=?,`bonus_expire`=UNIX_TIMESTAMP()+" + time + " WHERE `login`=?", value, player.getAccountName());
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		player.getNetConnection().setBonus(value);
		player.getNetConnection().setBonusExpire(System.currentTimeMillis() / 1000 + time);
		player.restoreBonus();
		player.sendPacket(new ExBrPremiumState(player, 1));
		if(player.getParty() != null)
			player.getParty().recalculatePartyData();
		show(Files.read("data/scripts/services/RateBonusGet.htm", player), player);
		player.broadcastSkill(new MagicSkillUse(player, player, 6176, 1, 1000, 0), true);
		
		DifferentMethods.sendMessage(player, new CustomMessage("scripts.items.AddPremium.add", player).addNumber(days).addString(DifferentMethods.declension(player, days, "Days")));

		Log.add(player.getName() + "|" + player.getObjectId() + "|rate bonus|" + value + "|" + days + "|", "services");
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
