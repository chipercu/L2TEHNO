package items;

import java.util.*;
import java.sql.SQLException;

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

public class ActiveBuffer extends Functions implements IItemHandler, ScriptFile
{
	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;

		L2Player player = (L2Player) playable;

		if(!ConfigValue.PremiumBufferEnable || player.getBonus().PremiumBuffer)
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
		int time = ConfigValue.PremiumBufferDays[index];

		if(time == 0)
		{
			DifferentMethods.sendMessage(player, new CustomMessage("scripts.items.ActiveBuffer.null", player));
			return;
		}

		ThreadConnection con = null;
		FiltredPreparedStatement offline = null;
		Calendar bonus_expire = Calendar.getInstance();
		bonus_expire.add(Calendar.MINUTE, time);
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("INSERT INTO bonus(obj_id, account, bonus_name, bonus_value, bonus_expire_time) VALUES (?,?,?,?,?)");
			offline.setInt(1, player.getObjectId());
			offline.setString(2, player.getAccountName());
			offline.setString(3, "PremiumBuffer");
			offline.setDouble(4, 1); // Бонус рейты
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

		player.restoreBonus();

		player.broadcastPacket(new MagicSkillUse(player, player, 6176, 1, 1000, 0));

		//DifferentMethods.sendMessage(player, new CustomMessage("scripts.items.AddPremium.add", player).addNumber(time).addString(DifferentMethods.declension(player, time, "Days")));
	}

	private int getIndex(int item_id)
	{
		for(int i=0;i<ConfigValue.PremiumBufferItems.length;i++)
			if(ConfigValue.PremiumBufferItems[i] == item_id)
				return i;
		return -1;
	}

	public final int[] getItemIds()
	{
		return ConfigValue.PremiumBufferItems;
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
