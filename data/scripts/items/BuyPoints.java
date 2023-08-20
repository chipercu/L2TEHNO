package items;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExBR_GamePoint;
import l2open.util.Files;

import java.sql.SQLException;

public class BuyPoints extends Functions implements IItemHandler, ScriptFile
{
	public void buy(String[] arg)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		int add_count = Integer.parseInt(arg[0]);
		
		if(add_count > 0 && player.getInventory().getCountOf(ConfigValue.BuyPointsItemId[0]) >= add_count)
		{
			if(player.getInventory().destroyItemByItemId(ConfigValue.BuyPointsItemId[0], add_count, true) != null)
			{
				add_count*=ConfigValue.BuyPointsForOneItem;

				addPoints(player, add_count, false);
				player.sendMessage("Получено "+add_count+" point.");
				useItem(player, null, false);
			}
		}
		else if(add_count > 0)
			player.sendMessage("Недостаточно предметов, в наличии "+player.getInventory().getCountOf(ConfigValue.BuyPointsItemId[0])+".");
	}

	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(!playable.isPlayer())
			return;

		L2Player activeChar = (L2Player) playable;

		String html = Files.read("data/html/points.htm", activeChar);

		html = html.replace("<?points?>", String.valueOf(activeChar.getPoint(false)));
		html = html.replace("<?points_add?>", String.valueOf(ConfigValue.BuyPointsForOneItem));
		html = html.replace("<?points_all?>", String.valueOf(ConfigValue.BuyPointsForOneItem*activeChar.getInventory().getCountOf(ConfigValue.BuyPointsItemId[0])));

		show(html, activeChar, null);
		activeChar.sendActionFailed();
	}

	public void addPoints(L2Player player, int add, boolean set_game)
	{
        try
		{
            mysql.setEx(set_game ? L2DatabaseFactory.getInstance() : L2DatabaseFactory.getInstanceLogin(), "UPDATE "+(set_game ? "market_point" : "`accounts`")+" SET `points`=points+? WHERE `login`=?", add, player.getAccountName());
        }
		catch (SQLException e)
		{
            e.printStackTrace();
        }
        player.sendPacket(new ExBR_GamePoint(player.getObjectId(), player.getPoint(false)));
    }

	public int[] getItemIds()
	{
		return ConfigValue.BuyPointsItemId;
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