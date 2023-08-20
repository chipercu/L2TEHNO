package l2open.gameserver.model.barahlo;

public class CBBuffSch
{
	public CBBuffSch(int i, String n, long[] b)
	{
		id = i;
		SchName = n;
		_buffList = b;
	}

	public int id = 0;
	public String SchName = "";
	public long _buffList[] = {};
}