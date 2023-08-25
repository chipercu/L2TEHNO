package items;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.FishDropData;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.FishTable;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.RateService;
import l2open.util.Util;

public class FishItem implements IItemHandler, ScriptFile
{
	private static int[] _itemIds = null;

	public FishItem()
	{
		FishTable ft = FishTable.getInstance();
		_itemIds = new int[ft.GetFishItemCount()];
		for(int i = 0; i < ft.GetFishItemCount(); i++)
			_itemIds[i] = ft.getFishIdfromList(i);
	}

	public synchronized void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;

		GArray<FishDropData> rewards = FishTable.getInstance().getFishReward(item.getItemId());
		int count = 0;
		player.getInventory().destroyItem(item, 1, true);
		for(FishDropData d : rewards)
		{
			long roll = Util.rollDrop(d.getMinCount(), d.getMaxCount(), d.getChance() * ConfigValue.RateFishDropCount * RateService.getRateDropItems(player) * player.getRateItems() * 10000L, false, player);
			if(roll > 0)
			{
				giveItems(player, d.getRewardItemId(), roll);
				count++;
			}
		}
		if(count == 0)
			player.sendMessage(new CustomMessage("scripts.items.FishItem.Nothing", player));
	}

	public void giveItems(L2Player activeChar, short itemId, long count)
	{
		L2ItemInstance item = ItemTemplates.getInstance().createItem(itemId);
		item.setCount(count);
		activeChar.sendPacket(SystemMessage.obtainItems(item));
		activeChar.getInventory().addItem(item);
	}

	public int[] getItemIds()
	{
		return _itemIds;
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