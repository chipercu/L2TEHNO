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

public class ExpandCWH extends Functions implements ScriptFile
{
	public void get()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(!ConfigValue.ExpandCWHEnabled)
		{
			show("Сервис отключен.", player);
			return;
		}

		if(player.getClan() == null)
		{
			player.sendMessage("You must be in clan.");
			return;
		}

		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.ExpandCWHItem);
		L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
		if(pay != null && pay.getCount() >= ConfigValue.ExpandCWHPrice)
		{
			player.getInventory().destroyItem(pay, ConfigValue.ExpandCWHPrice, true);
			player.getClan().setWhBonus(player.getClan().getWhBonus() + ConfigValue.ExpandCWHCount);
			player.sendMessage("Warehouse capacity is now " + (ConfigValue.MaximumWarehouseSlotsForClan + player.getClan().getWhBonus()));
		}
		else if(ConfigValue.ExpandCWHItem == 57)
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

		if(player.getClan() == null)
		{
			player.sendMessage("You must be in clan.");
			return;
		}

		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.ExpandCWHItem);

		String html = Files.read("data/scripts/services/ExpandCWH.htm", player);
		html = html.replace("<?arg1?>", String.valueOf((ConfigValue.MaximumWarehouseSlotsForClan + player.getClan().getWhBonus())));
		html = html.replace("<?arg2?>", String.valueOf(ConfigValue.ExpandCWHPrice + " " + item.getName()));
		show(html, player);
	}

	public void onLoad()
	{
		_log.info("Loaded Service: Expand CWH");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}