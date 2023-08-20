package items;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;

public class CharChangePotions implements IItemHandler, ScriptFile
{
	private static final int[] _itemIds = { 5235, 5236, 5237, // Face
			5238, 5239, 5240, 5241, // Hair Color
			5242, 5243, 5244, 5245, 5246, 5247, 5248 // Hair Style
	};

	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;

		int itemId = item.getItemId();

		if(player.isActionsDisabled())
		{
			player.sendActionFailed();
			return;
		}

		switch(itemId)
		{
			case 5235:
				player.setFace((byte) 0);
				break;
			case 5236:
				player.setFace((byte) 1);
				break;
			case 5237:
				player.setFace((byte) 2);
				break;
			case 5238:
				player.setHairColor((byte) 0);
				break;
			case 5239:
				player.setHairColor((byte) 1);
				break;
			case 5240:
				player.setHairColor((byte) 2);
				break;
			case 5241:
				player.setHairColor((byte) 3);
				break;
			case 5242:
				player.setHairStyle((byte) 0);
				break;
			case 5243:
				player.setHairStyle((byte) 1);
				break;
			case 5244:
				player.setHairStyle((byte) 2);
				break;
			case 5245:
				player.setHairStyle((byte) 3);
				break;
			case 5246:
				player.setHairStyle((byte) 4);
				break;
			case 5247:
				player.setHairStyle((byte) 5);
				break;
			case 5248:
				player.setHairStyle((byte) 6);
				break;
		}

		player.getInventory().destroyItem(item, 1, true);
		player.broadcastSkill(new MagicSkillUse(player, player, 2003, 1, 1, 0), true);
		player.broadcastUserInfo(true);
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