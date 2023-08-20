package l2open.loginserver;

import java.util.Map;
import java.util.HashMap;

public class GameInfo
{
	public int id;
	public String name;

	public Map<Integer, Proxy> _proxy = new HashMap<Integer, Proxy>();
	public GameInfo(int i, String s)
	{
		id = i;
		name = s;
	}
}