package items;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;

/**
 * @author Diagod
 */
public class AddVisual implements IItemHandler, ScriptFile
{
	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;
		final L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		if(chest == null || chest.isShadowItem() || chest.isTemporalItem() || chest.getVisualItemId() > 0)
		{
			player.sendMessage("Не подходящий предмет.");
			return;
		}
		player.scriptRequest("Вы желаете сменить внешний вид брони?", "call_bbs", new String[]{"_bbs_visual_ok:-1:0:"+item.getItemId()});
	}

	public final int[] getItemIds()
	{
		return ConfigValue.AddVisualItemList;
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