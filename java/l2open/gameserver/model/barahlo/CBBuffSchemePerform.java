package l2open.gameserver.model.barahlo;

public class CBBuffSchemePerform
{
	public CBBuffSchemePerform(int i, String n, String[] b)
	{
		id = i;
		SchName = n;
		_buffList = b;
	}

	public int id = 0;
	public String SchName = "";
	public String[] _buffList = {};
}