package l2open.gameserver.templates;

import l2open.gameserver.model.L2DropData;
import l2open.util.GArray;

public final class L2FishTemplate
{
	public static enum FishType
	{
		FAT,
		NIMBLE,
		UGLY,
		CHEST
	}

	public final Short fishId;
	public final String name;
	public final int maxHp;
	public final byte averageLevel;
	public final FishType type;
	public final boolean beginner;
	public final int chance;

	/** The table containing all Item that can be dropped by L2FishInstance using this L2FishTemplate*/
	private final GArray<L2DropData> _drops = new GArray<L2DropData>(5);

	public L2FishTemplate(StatsSet set)
	{
		fishId = set.getShort("fishId");
		name = set.getString("name");
		maxHp = set.getInteger("hp");
		averageLevel = set.getByte("avg_lvl");
		if(name.indexOf("fat") > -1)
			type = FishType.FAT;
		else if(name.indexOf("nimble") > -1)
			type = FishType.NIMBLE;
		else if(name.indexOf("ugly") > -1)
			type = FishType.UGLY;
		else
			type = FishType.CHEST;
		beginner = name.indexOf("For Beginners") > -1;
		chance = set.getInteger("chance");
	}

	public void addDropData(L2DropData drop)
	{
		_drops.add(drop);
	}

	/**
	 * Return the list of all possible drops of this L2NpcTemplate.<BR><BR>
	 */
	public GArray<L2DropData> getDropData()
	{
		return _drops;
	}

	@Override
	public String toString()
	{
		return "Fish template " + name + "[" + fishId + "]";
	}
}
