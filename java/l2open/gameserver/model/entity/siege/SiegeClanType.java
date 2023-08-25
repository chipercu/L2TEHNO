package l2open.gameserver.model.entity.siege;

public enum SiegeClanType
{
	OWNER(0),
	DEFENDER(0),
	ATTACKER(1),
	DEFENDER_WAITING(2),
	DEFENDER_REFUSED(3);

	private int _id;

	public int getId()
	{
		return _id;
	}

	public SiegeClanType simple()
	{
		return this == OWNER ? DEFENDER : this;
	}

	public static SiegeClanType getById(int id)
	{
		for(SiegeClanType s : values())
			if(s.getId() == id)
				return s;
		return DEFENDER;
	}

	private SiegeClanType(int id)
	{
		_id = id;
	}
}