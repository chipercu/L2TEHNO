package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExPutItemResultForVariationMake;
import l2open.gameserver.templates.L2Item;

import java.util.*;

public class RequestConfirmTargetItem extends L2GameClientPacket
{
	// format: (ch)d
	private int _itemObjId;
	private static List<Integer> tipamosgno = new ArrayList<Integer>();

	static
	{
		tipamosgno.add(10170);
		tipamosgno.add(10314);
		tipamosgno.add(16026);
		tipamosgno.add(21712);
		tipamosgno.add(22175);
		tipamosgno.add(22173);
		tipamosgno.add(22174);
	}

	@Override
	public void readImpl()
	{
		_itemObjId = readD(); // object_id шмотки
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_itemObjId);

		if(item == null)
			return;

		if(activeChar.getLevel() < 46)
		{
			activeChar.sendMessage("You have to be level 46 in order to augment an item");
			return;
		}

		// check if the item is augmentable
		int itemGrade = item.getItem().getItemGrade().cry;
		int itemType = item.getItem().getType2();

		if(item.isAugmented())
		{
			activeChar.sendPacket(Msg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN);
			return;
		}
		//TODO: can do better? : currently: using isdestroyable() as a check for hero / cursed weapons
		else if(itemGrade < L2Item.CRYSTAL_C || itemType != L2Item.TYPE2_WEAPON && itemType != L2Item.TYPE2_ACCESSORY && !ConfigValue.AugmentAll || !item.isDestroyable() || item.isShadowItem() || item.isCommonItem() || (item.getItem().isRaidAccessory()/* && !tipamosgno.contains(item.getItemId())*/) || (!ConfigValue.AltAugmentPvPItem && item.getItem().isPvP()) || item.getItemId() == 13752 || item.getItemId() == 13753 || item.getItemId() == 13754)
		{
			activeChar.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}

		// check if the player can augment
		if(activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION);
			return;
		}
		if(activeChar.isDead())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD);
			return;
		}
		if(activeChar.isParalyzed())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED);
			return;
		}
		if(activeChar.isFishing())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING);
			return;
		}
		if(activeChar.isSitting())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN);
			return;
		}

		activeChar.sendPacket(new ExPutItemResultForVariationMake(_itemObjId), Msg.SELECT_THE_CATALYST_FOR_AUGMENTATION);
	}
}