package l2open.gameserver.model;

public class FishDropData
{
	private final short _fishId;
	private final short _rewarditemId;
	private final int _mindrop;
	private final int _maxdrop;
	private final int _chance;

	public FishDropData(short fishid, short itemid, int mindrop, int maxdrop, int chance)
	{
		_fishId = fishid;
		_rewarditemId = itemid;
		_mindrop = mindrop;
		_maxdrop = maxdrop;
		_chance = chance;
	}

	public short getFishId()
	{
		return _fishId;
	}

	public short getRewardItemId()
	{
		return _rewarditemId;
	}

	/**
	 * Returns the quantity of items dropped
	 * @return int
	 */
	public int getMinCount()
	{
		return _mindrop;
	}

	/**
	 * Returns the chance of having a drop
	 * @return int
	 */
	public int getMaxCount()
	{
		return _maxdrop;
	}

	/**
	 * Returns the chance of having a drop
	 * @return int
	 */
	public int getChance()
	{
		return _chance;
	}
}