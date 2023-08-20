package l2open.gameserver.templates;

import l2open.gameserver.templates.L2Armor.ArmorType;
import l2open.gameserver.templates.L2Weapon.WeaponType;

public final class L2EtcItem extends L2Item
{
	public enum EtcItemType
	{
		ARROW(1, "Arrow"),
		MATERIAL(2, "Material"),
		PET_COLLAR(3, "PetCollar"),
		POTION(4, "Potion"),
		RECIPE(5, "Recipe"),
		SCROLL(6, "Scroll"),
		QUEST(7, "Quest"),
		MONEY(8, "Money"),
		OTHER(9, "Other"),
		SPELLBOOK(10, "Spellbook"),
		SEED(11, "Seed"),
		BAIT(12, "Bait"),
		SHOT(13, "Shot"),
		BOLT(14, "Bolt");

		final int _id;
		final String _name;

		EtcItemType(int id, String name)
		{
			_id = id;
			_name = name;
		}

		public long mask()
		{
			return 1L << (_id + WeaponType.values().length + ArmorType.values().length);
		}

		@Override
		public String toString()
		{
			return _name;
		}
	}

	/**
	 * Constructor<?> for EtcItem.
	 * @see L2Item constructor
	 * @param type : L2EtcItemType designating the type of object Etc
	 * @param set : StatsSet designating the set of couples (key,value) for description of the Etc
	 */
	public L2EtcItem(EtcItemType type, StatsSet set)
	{
		super(type, set);
	}

	/**
	 * Returns the type of Etc Item
	 * @return L2EtcItemType
	 */
	@Override
	public EtcItemType getItemType()
	{
		return (EtcItemType) super.type;
	}

	/**
	 * Returns the ID of the Etc item after applying the mask.
	 * @return int : ID of the EtcItem
	 */
	@Override
	public long getItemMask()
	{
		return getItemType().mask();
	}

	@Override
	public final boolean isShadowItem()
	{
		return false;
	}
}