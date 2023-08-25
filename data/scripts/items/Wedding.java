package items;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * @author Diagod
 */
public class Wedding implements IItemHandler, ScriptFile
{
	private static final int[] _itemIds = { 21156 };

	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;

		try
		{
			L2NpcInstance npc = NpcTable.getTemplate(137).getNewInstance();
            npc.setReflection(0);
            npc.setSpawnedLoc(player.getLoc());
			npc.setTitle(player.getName());
            npc.onSpawn();
            npc.spawnMe(npc.getSpawnedLoc());

			ThreadPoolManager.getInstance().schedule(new DeSpawnScheduleTimerTask(npc), 600000);
			player.getInventory().destroyItem(item.getObjectId(), 1, false);
		}
		catch(Exception e)
		{
			player.sendPacket(Msg.YOUR_TARGET_CANNOT_BE_FOUND);
		}
	}

	public class DeSpawnScheduleTimerTask extends l2open.common.RunnableImpl
	{
		L2NpcInstance spawnedTree = null;

		public DeSpawnScheduleTimerTask(L2NpcInstance spawn)
		{
			spawnedTree = spawn;
		}

		public void runImpl()
		{
			try
			{
				spawnedTree.deleteMe();
			}
			catch(Throwable t)
			{}
		}
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