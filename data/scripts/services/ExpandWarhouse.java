package services;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;

public class ExpandWarhouse extends Functions implements ScriptFile
{
	public void get()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(!ConfigValue.ExpandWarehouseEnabled)
		{
			show("Сервис отключен.", player);
			return;
		}

		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.ExpandWarehouseItem);
		L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
		if(pay != null && pay.getCount() >= ConfigValue.ExpandWarehousePrice)
		{
			player.getInventory().destroyItem(pay, ConfigValue.ExpandWarehousePrice, true);
			player.setExpandWarehouse(player.getExpandWarehouse() + 1);
			player.setVar("ExpandWarehouse", String.valueOf(player.getExpandWarehouse()));
			player.sendMessage("Warehouse capacity is now " + player.getWarehouseLimit());
		}
		else if(ConfigValue.ExpandWarehouseItem == 57)
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);

		show();
	}

	public void show()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(!ConfigValue.ExpandWarehouseEnabled)
		{
			show("Сервис отключен.", player);
			return;
		}

		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.ExpandWarehouseItem);

		String html = Files.read("data/scripts/services/ExpandWarhouse.htm", player);
		html = html.replace("<?arg1?>", String.valueOf(player.getWarehouseLimit()));
		html = html.replace("<?arg2?>", String.valueOf(ConfigValue.ExpandWarehousePrice + " " + item.getName()));
		show(html, player);
	}

	public void onLoad()
	{
		_log.info("Loaded Service: Expand Warehouse");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}