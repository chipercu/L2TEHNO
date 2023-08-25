package items;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;

public class AddExpSp extends Functions implements IItemHandler, ScriptFile
{
	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;

		L2Player player = (L2Player) playable;

		if(player.isInOlympiadMode())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return;
		}
		else if(player.isOutOfControl() || player.isDead() || player.isStunned() || player.isSleeping() || player.isParalyzed())
			return;

		int index = getIndex(item.getItemId());
		if(index < 0)
			return;

		L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
		player.getInventory().destroyItem(pay, 1, true);

		long[] val = ConfigValue.AddExpSpUseValue[index];
		player.addExpAndSp(val[0], val[1], false, false);
		
	}

	private int getIndex(int item_id)
	{
		for(int i=0;i<ConfigValue.AddExpSpUseItems.length;i++)
			if(ConfigValue.AddExpSpUseItems[i] == item_id)
				return i;
		return -1;
	}

	public final int[] getItemIds()
	{
		return ConfigValue.AddExpSpUseItems;
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
