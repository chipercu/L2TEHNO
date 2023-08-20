package l2open.gameserver.model.entity.ItemBroker;

import l2open.gameserver.model.base.L2Augmentation;
import l2open.gameserver.model.items.L2ItemInstance;

import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.StatsSet;
import l2open.gameserver.xml.ItemTemplates;

public final class AuctionItem
{
	private final int _auctionItemId;
	private final int _auctionLength;
	private final long _auctionInitBid;
	
	private final int _itemId;
	private final long _itemCount;
	private final StatsSet _itemExtra;
	
	public AuctionItem(final int auctionItemId, final int auctionLength, final long auctionInitBid, final int itemId, final long itemCount, final StatsSet itemExtra)
	{
		_auctionItemId = auctionItemId;
		_auctionLength = auctionLength;
		_auctionInitBid = auctionInitBid;
		
		_itemId = itemId;
		_itemCount = itemCount;
		_itemExtra = itemExtra;
	}
	
	public final boolean checkItemExists()
	{
		final L2Item item = ItemTemplates.getInstance().getTemplate(_itemId);
		if(item == null)
			return false;
		return true;
	}
	
	public final int getAuctionItemId()
	{
		return _auctionItemId;
	}
	
	public final int getAuctionLength()
	{
		return _auctionLength;
	}
	
	public final long getAuctionInitBid()
	{
		return _auctionInitBid;
	}
	
	public final int getItemId()
	{
		return _itemId;
	}
	
	public final long getItemCount()
	{
		return _itemCount;
	}
	
	public final L2ItemInstance createNewItemInstance()
	{
		final L2ItemInstance item = ItemTemplates.getInstance().createItem(_itemId);
		item.setCount(_itemCount);

		final int enchantLevel = _itemExtra.getInteger("enchant_level", 0);
		item.setEnchantLevel(enchantLevel);
		
		final int augmentationId = _itemExtra.getInteger("augmentation_id", 0);
		if (augmentationId != 0)
		{
			final int augmentationSkillId = _itemExtra.getInteger("augmentation_skill_id", 0);
			final int augmentationSkillLevel = _itemExtra.getInteger("augmentation_skill_lvl", 0);
			item.setAugmentation(new L2Augmentation(augmentationId, augmentationSkillId, augmentationSkillLevel));
		}
		
		return item;
	}
}
