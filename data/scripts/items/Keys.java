package items;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.DoorTable;
import l2open.util.GArray;

public class Keys implements IItemHandler, ScriptFile
{
	private static int[] _itemIds = null;

	public Keys()
	{
		GArray<Integer> keys = new GArray<Integer>();
		for(L2DoorInstance door : DoorTable.getInstance().getDoors())
			if(door != null && door.key > 0)
				keys.add(door.key);
		_itemIds = new int[keys.size()];
		int i = 0;
		for(int id : keys)
		{
			_itemIds[i] = id;
			i++;
		}
	}

	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;

		if(item == null || item.getCount() < 1)
		{
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		L2Object target = player.getTarget();
		if(target == null || !target.isDoor())
		{
			player.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
			return;
		}

		L2DoorInstance door = (L2DoorInstance) target;
		
		if(!player.isInRange(door, 150)) //Если чар стоит от двери дальше чем на 100. То посылаем его лесом!
		{
			player.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
			return;
		}

		if(door.isOpen())
		{
			player.sendPacket(Msg.IT_IS_NOT_LOCKED);
			return;
		}

		if(door.key <= 0 || item.getItemId() != door.key) // ключ не подходит к двери
		{
			player.sendPacket(Msg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
			return;
		}

		if(item.getItemId() == 13808 || item.getItemId() == 13809)
			door.openMe();
		else
		{
			L2ItemInstance ri = player.getInventory().destroyItem(item, 1, true);
			player.sendPacket(SystemMessage.removeItems(ri.getItemId(), 1), new SystemMessage("l2open.gameserver.skills.skillclasses.Unlock.Success", player));
			door.openMe();
			door.onOpen();
		}
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