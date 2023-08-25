package items;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ChooseInventoryItem;

public class EnchantScrolls implements IItemHandler, ScriptFile
{
			
	private static final int[] _itemIds = { 729, 730, 731, 732, 947, 948, 949, 950, 951, 952, 953, 954, 955, 956, 957,
			958, 959, 960, 961, 962, 6569, 6570, 6571, 6572, 6573, 6574, 6575, 6576, 6577, 6578, 13540,
			15346, 15347, 15348, 15349, 15350,
			17255, 17256, 17257, 17258, 17259, 17260, 17261, 17262, 17263, 17264, 22314, 22315, 22316, 22317, 22318, 22319, 22341, 
			20517, 20518, 20519, 20520, 20521,
			20522, 21581, 21582, 21707, 22006, 22007, 22008, 22009, 22010, 22011, 22012, 22013, 22014, 22015, 22016, 22017, 22018, 22019, 22020,
			22021, 22221, 22222, 22223, 22224, 22225, 22226, 22227, 22228, 22229, 22230, 22415, 22416, 22428, 22429 };

	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;

		if(player.getEnchantScroll() != null)
			return;

		player._catalystId=0; // скидываем каталист...
		player._enchant_time = System.currentTimeMillis() + ConfigValue.ItemEnchantDelay2;
		player.setEnchantScroll(item);
		player.sendPacket(Msg.SELECT_ITEM_TO_ENCHANT, new ChooseInventoryItem(item.getItemId()));
		return;
	}

	public final int[] getItemIds()
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