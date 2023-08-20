package l2open.gameserver.model.items;

public final class EnchantScrol
{
	public static enum EnchantScrolType
	{
		Ancient,
		Blessed,
		Destruction,
		ItemMall,
		PcClub
	}

	private int _itemId;
	public EnchantScrol(int id)
	{
		_itemId = id;
	}
}