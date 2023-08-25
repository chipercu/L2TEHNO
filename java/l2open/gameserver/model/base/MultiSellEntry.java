package l2open.gameserver.model.base;

import l2open.util.GArray;

public class MultiSellEntry
{
	private int _entryId;
	private GArray<MultiSellIngredient> _ingredients = new GArray<MultiSellIngredient>();
	private GArray<MultiSellIngredient> _production = new GArray<MultiSellIngredient>();
	private long _tax;
	public int _chance = 100;
	public int _return_chance = 0;

	public MultiSellEntry()
	{}

	public MultiSellEntry(int id)
	{
		_entryId = id;
	}

	public MultiSellEntry(int id, int product, int prod_count, int enchant)
	{
		_entryId = id;
		addProduct(new MultiSellIngredient(product, prod_count, enchant, false));
	}

	/**
	 * @param entryId The entryId to set.
	 */
	public void setEntryId(int entryId)
	{
		_entryId = entryId;
	}

	/**
	 * @return Returns the entryId.
	 */
	public int getEntryId()
	{
		return _entryId;
	}

	/**
	 * @param ingredients The ingredients to set.
	 */
	public void addIngredient(MultiSellIngredient ingredient)
	{
		if(ingredient.getItemCount() > 0)
			_ingredients.add(ingredient);
	}

	/**
	 * @return Returns the ingredients.
	 */
	public GArray<MultiSellIngredient> getIngredients()
	{
		return _ingredients;
	}

	/**
	 * @param ingredients The ingredients to set.
	 */
	public void addProduct(MultiSellIngredient ingredient)
	{
		_production.add(ingredient);
	}

	/**
	 * @return Returns the ingredients.
	 */
	public GArray<MultiSellIngredient> getProduction()
	{
		return _production;
	}

	public long getTax()
	{
		return _tax;
	}

	public void setTax(long tax)
	{
		_tax = tax;
	}

	public int getChance()
	{
		return _chance;
	}

	public int getReturnChance()
	{
		return _return_chance;
	}

	@Override
	public int hashCode()
	{
		return _entryId;
	}

	@Override
	public MultiSellEntry clone()
	{
		MultiSellEntry ret = new MultiSellEntry(_entryId);
		ret._chance = _chance;
		ret._return_chance = _return_chance;
		for(MultiSellIngredient i : _ingredients)
			ret.addIngredient(i.clone());
		for(MultiSellIngredient i : _production)
			ret.addProduct(i.clone());
		return ret;
	}
}