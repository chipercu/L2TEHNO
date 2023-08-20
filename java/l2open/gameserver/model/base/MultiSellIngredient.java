package l2open.gameserver.model.base;

import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;

public class MultiSellIngredient implements Cloneable
{
	private int _itemId;
	private long _itemCount;
	private int _itemEnchant;
	private int _itemElement;
	private int _itemElementValue;
	public int _temporal = 0;
	public boolean canReturn = false;
	public boolean _setEquip = false;

	public MultiSellIngredient(int itemId, long itemCount, int itemEnchant, int element, int elementValue, int temporal, boolean setEquip, boolean rtrn)
	{
		_itemId = itemId;
		_itemCount = itemCount;
		_itemEnchant = itemEnchant;
		_itemElement = element;
		_itemElementValue = elementValue;
		_temporal = temporal;
		_setEquip = setEquip;
		canReturn = rtrn;
	}

	public MultiSellIngredient(int itemId, long itemCount, int itemEnchant, boolean rtrn)
	{
		this(itemId, itemCount, itemEnchant, L2Item.ATTRIBUTE_NONE, 0, 0, false, rtrn);
	}

	public MultiSellIngredient(int itemId, long itemCount)
	{
		this(itemId, itemCount, 0, L2Item.ATTRIBUTE_NONE, 0, 0, false, false);
	}

	@Override
	public MultiSellIngredient clone()
	{
		return new MultiSellIngredient(_itemId, _itemCount, _itemEnchant, _itemElement, _itemElementValue, _temporal, _setEquip, canReturn);
	}

	/**
	 * @param itemId The itemId to set.
	 */
	public void setItemId(int itemId)
	{
		_itemId = itemId;
	}

	/**
	 * @return Returns the itemId.
	 */
	public int getItemId()
	{
		return _itemId;
	}

	/**
	 * @param itemCount The itemCount to set.
	 */
	public void setItemCount(long itemCount)
	{
		_itemCount = itemCount;
	}

	/**
	 * @return Returns the itemCount.
	 */
	public long getItemCount()
	{
		return _itemCount;
	}

	/**
	 * Returns if item is stackable
	 * @return boolean
	 */
	public boolean isStackable()
	{
		return _itemId <= 0 || ItemTemplates.getInstance().getTemplate(_itemId).isStackable();
	}

	/**
	 * @param itemEnchant The itemEnchant to set.
	 */
	public void setItemEnchant(int itemEnchant)
	{
		_itemEnchant = itemEnchant;
	}

	/**
	 * @return Returns the itemEnchant.
	 */
	public int getItemEnchant()
	{
		return _itemEnchant;
	}

	public void setElement(int element, int value)
	{
		_itemElement = element;
		_itemElementValue = value;
	}

	public byte getElement()
	{
		return (byte) _itemElement;
	}

	public int getElementValue()
	{
		return _itemElementValue;
	}

	public boolean canReturn()
	{
		return canReturn;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (_itemCount ^ (_itemCount >>> 32));
		result = prime * result + _itemElement;
		result = prime * result + _itemElementValue;
		result = prime * result + _itemEnchant;
		result = prime * result + _itemId;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		MultiSellIngredient other = (MultiSellIngredient) obj;
		if(_itemId != other._itemId)
			return false;
		if(_itemCount != other._itemCount)
			return false;
		if(_itemEnchant != other._itemEnchant)
			return false;
		if(_itemElement != other._itemElement)
			return false;
		if(_itemElementValue != other._itemElementValue)
			return false;
		return true;
	}
}