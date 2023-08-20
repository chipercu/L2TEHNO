package l2open.gameserver.serverpackets;

import javolution.util.FastList;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.LockType;

public class ItemList extends L2GameServerPacket
{
	private L2ItemInstance[] _items;
	private boolean _showWindow;
	private int length;
	private FastList<L2ItemInstance> questItems;

	private LockType _lockType;
	private int[] _lockItems;
	private int agathion = 0;

	public ItemList(L2Player cha, boolean showWindow)
	{
		_lockType = cha.getInventory().getLockType();
		_lockItems = cha.getInventory().getLockItems();

		_items = cha.getInventory().getItems();
		_showWindow = showWindow;
		questItems = FastList.newInstance();
		for(int i = 0; i < _items.length; i++)
		{
			L2ItemInstance temp = _items[i];
			if(temp != null && temp.getItem().isQuest())
			{
				questItems.add(temp); // add to questinv
				_items[i] = null; // remove from list
			}
			else if(temp != null && _lockType != LockType.NONE && cha.getEventMaster() != null && cha.getEventMaster().notShowLockItems(cha) && cha.getInventory().isLockedItem(temp))
				_items[i] = null; // remove from list
			else
				length++; // increase size
		}
		if(_lockType != LockType.NONE && cha.getEventMaster() != null && cha.getEventMaster().notShowLockItems(cha))
			_lockItems = new int[0];
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x11);
		writeH(_showWindow ? 1 : 0);

		writeH(length);
		for(L2ItemInstance temp : _items)
		{
			if(temp == null || temp.getItem() == null)
				continue;

			if(temp.getItem().getAgathionEnergy() > 0)
				agathion++;

			writeD(temp.getObjectId());
			writeD(temp.getItemId());
			writeD(temp.getEquipSlot()); //order
			writeQ(temp.getCount());
			writeH(temp.getItem().getType2ForPackets()); // item type2
			writeH(temp.getCustomType1()); // item type3
			writeH(temp.isEquipped() ? 1 : 0);
			writeD(temp.getBodyPart()); // rev 415  slot    0006-lr.ear  0008-neck  0030-lr.finger  0040-head  0080-??  0100-l.hand  0200-gloves  0400-chest  0800-pants  1000-feet  2000-??  4000-r.hand  8000-r.hand
			writeH(temp.getEnchantLevel()); // enchant level
			writeH(temp.getCustomType2()); // item type3
			writeD(temp.getAugmentationId());
			writeD(temp.isShadowItem() ? temp.getLifeTimeRemaining() : -1);
			writeD(temp.isTemporalItem() ? temp.getLifeTimeRemaining() : 0x00); // limited time item life remaining
			writeItemElements(temp);
			writeH(temp.getEnchantOptions()[0]);
			writeH(temp.getEnchantOptions()[1]);
			writeH(temp.getEnchantOptions()[2]);
		}

		writeH(_lockItems.length);
		if(_lockItems.length > 0)
		{
			writeC(_lockType.ordinal());
			for(int i : _lockItems)
				writeD(i);
		}

		if(questItems.size() > 0)
			getClient().sendPacket(new ExQuestItemList(questItems, getClient().getActiveChar()));

		if(agathion > 0)
			getClient().sendPacket(new ExBR_AgathionEnergyInfoPacket(agathion, _items));
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0x11);
		writeH(_showWindow ? 1 : 0);

		writeH(length);
		for(L2ItemInstance temp : _items)
		{
			if(temp == null || temp.getItem() == null)
				continue;

			if(temp.getItem().getAgathionEnergy() > 0)
				agathion++;

			writeD(temp.getObjectId());
			writeD(temp.getItemId());
			writeD(temp.getEquipSlot()); //order
			writeQ(temp.getCount());
			writeH(temp.getItem().getType2ForPackets()); // item type2
			writeH(temp.getCustomType1()); // item type3
			writeH(temp.isEquipped() ? 1 : 0);
			writeD(temp.getBodyPart()); // rev 415  slot    0006-lr.ear  0008-neck  0030-lr.finger  0040-head  0080-??  0100-l.hand  0200-gloves  0400-chest  0800-pants  1000-feet  2000-??  4000-r.hand  8000-r.hand
			writeH(temp.getEnchantLevel()); // enchant level
			writeH(temp.getCustomType2()); // item type3
			writeD(temp.getAugmentationId());
			writeD(temp.isShadowItem() ? temp.getLifeTimeRemaining() : -1);
			writeD(temp.isTemporalItem() ? temp.getLifeTimeRemaining() : 0x00); // limited time item life remaining
			writeH(0x01); // L2WT GOD
			writeItemElements(temp);
			writeH(temp.getEnchantOptions()[0]);
			writeH(temp.getEnchantOptions()[1]);
			writeH(temp.getEnchantOptions()[2]);
			writeD(temp._visual_item_id); // getVisualId
		}

		writeH(_lockItems.length);
		if(_lockItems.length > 0)
		{
			writeC(_lockType.ordinal());
			for(int i : _lockItems)
				writeD(i);
		}

		if(questItems.size() > 0)
			getClient().sendPacket(new ExQuestItemList(questItems, getClient().getActiveChar()));

		if(agathion > 0)
			getClient().sendPacket(new ExBR_AgathionEnergyInfoPacket(agathion, _items));
		return true;
	}
}