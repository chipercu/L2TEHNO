package l2open.gameserver.model;

public class L2ManufactureItem
{
	private int _recipeId;
	private long _cost;

	public L2ManufactureItem(int recipeId, long cost)
	{
		_recipeId = recipeId;
		_cost = cost;
	}

	public int getRecipeId()
	{
		return _recipeId;
	}

	public long getCost()
	{
		return _cost;
	}
}
