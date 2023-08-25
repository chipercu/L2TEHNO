package l2open.gameserver.model.barahlo;

public class Drop
{
	public int item_id;
	public int min_count;
	public int max_count;
	public int chance;

	public Drop(int item_i, int min_c, int max_c, int c)
	{
		item_id = item_i;
		min_count = min_c;
		max_count = max_c;
		chance = c;
	}
}