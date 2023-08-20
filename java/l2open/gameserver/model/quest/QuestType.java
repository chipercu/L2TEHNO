package l2open.gameserver.model.quest;

/**
 * @author L2CCCP
 * @site http://l2cccp.com/
 */
public enum QuestType
{
	SOLO(0),
	PARTY_ONE(1),
	PARTY_ALL(2),
	PARTY_ANY(3);

	private final int _id;

	QuestType(final int id)
	{
		_id = id;
	}

	public int getId()
	{
		return _id;
	}
}