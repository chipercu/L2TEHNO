package events.tournament;

import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.util.GArray;

public class Team
{
	private int _id;
	private int _leader, _member1, _member2;
	private int _category;

	private String _name;

	public String getName()
	{
		return _name;
	}

	public void setName(String name)
	{
		_name = name;
	}

	public boolean addMember(int member)
	{
		if(_member1 == 0)
		{
			_member1 = member;
			return true;
		}
		else if(_member2 == 0)
		{
			_member2 = member;
			return true;
		}
		else
			return false;
	}

	public boolean removeMember(int member)
	{
		if(_member1 == member)
		{
			_member1 = 0;
			return true;
		}
		else if(_member2 == member)
		{
			_member2 = 0;
			return true;
		}
		else
			return false;
	}

	public int getLeader()
	{
		return _leader;
	}

	public void setLeader(int leader)
	{
		_leader = leader;
	}

	public int[] getMembers()
	{
		if(_member1 != 0 && _member2 == 0)
			return new int[] { _leader, _member1 };
		if(_member1 == 0 && _member2 != 0)
			return new int[] { _leader, _member2 };
		if(_member1 != 0 && _member2 != 0)
			return new int[] { _leader, _member1, _member2 };

		return new int[] { _leader };
	}

	public GArray<L2Player> getOnlineMembers()
	{
		GArray<L2Player> result = new GArray<L2Player>();
		for(int obj_id : getMembers())
		{
			L2Player player = L2ObjectsStorage.getPlayer(obj_id);
			if(player != null)
				result.add(player);
		}
		return result;
	}

	public int getCount()
	{
		int i = 1;
		if(_member1 != 0)
			i++;
		if(_member2 != 0)
			i++;
		return i;
	}

	public int getOnlineCount()
	{
		return getOnlineMembers().size();
	}

	public int getCategory()
	{
		return _category;
	}

	public void setCategory(int category)
	{
		_category = category;
	}

	public int getId()
	{
		return _id;
	}

	public void setId(int id)
	{
		_id = id;
	}
}
