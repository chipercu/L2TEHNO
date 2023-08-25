package l2open.gameserver.templates;

public class L2CharTemplate
{
	private StatsSet _set;

	// BaseStats
	public byte baseSTR;
	public byte baseCON;
	public byte baseDEX;
	public byte baseINT;
	public byte baseWIT;
	public byte baseMEN;
	public float baseHpMax;
	public float baseCpMax;
	public float baseMpMax;

	/** HP Regen base */
	public float baseHpReg;

	/** MP Regen base */
	public float baseMpReg;

	/** CP Regen base */
	public float baseCpReg;

	public int basePAtk;
	public int baseMAtk;
	public int basePDef;
	public int baseMDef;
	public int basePAtkSpd;
	public float baseMAtkSpd;
	public int baseShldDef;
	public int baseAtkRange;
	public int baseShldRate;
	public int baseCritRate;
	public int baseRunSpd;
	public int baseWalkSpd;

	public float collisionRadius;
	public float collisionHeight;

	public L2CharTemplate(StatsSet set)
	{
		_set = set;

		// Base stats
		baseSTR = set.getByte("baseSTR");
		baseCON = set.getByte("baseCON");
		baseDEX = set.getByte("baseDEX");
		baseINT = set.getByte("baseINT");
		baseWIT = set.getByte("baseWIT");
		baseMEN = set.getByte("baseMEN");
		baseHpMax = set.getFloat("baseHpMax");
		baseCpMax = set.getFloat("baseCpMax");
		baseMpMax = set.getFloat("baseMpMax");
		baseHpReg = set.getFloat("baseHpReg");
		baseCpReg = set.getFloat("baseCpReg");
		baseMpReg = set.getFloat("baseMpReg");
		basePAtk = set.getInteger("basePAtk");
		baseMAtk = set.getInteger("baseMAtk");
		basePDef = set.getInteger("basePDef");
		baseMDef = set.getInteger("baseMDef");
		basePAtkSpd = set.getInteger("basePAtkSpd");
		baseMAtkSpd = set.getFloat("baseMAtkSpd");
		baseShldDef = set.getInteger("baseShldDef");
		baseAtkRange = set.getInteger("baseAtkRange");
		baseShldRate = set.getInteger("baseShldRate");
		baseCritRate = set.getInteger("baseCritRate");
		baseRunSpd = set.getInteger("baseRunSpd");
		baseWalkSpd = set.getInteger("baseWalkSpd");

		// Geometry
		collisionRadius = set.getFloat("collision_radius", 5);
		collisionHeight = set.getFloat("collision_height", 5);
	}

	public void setSets(StatsSet set)
	{
		_set = set;
		baseSTR = set.getByte("baseSTR");
		baseCON = set.getByte("baseCON");
		baseDEX = set.getByte("baseDEX");
		baseINT = set.getByte("baseINT");
		baseWIT = set.getByte("baseWIT");
		baseMEN = set.getByte("baseMEN");
		baseHpMax = set.getFloat("baseHpMax");
		baseCpMax = set.getFloat("baseCpMax");
		baseMpMax = set.getFloat("baseMpMax");
		baseHpReg = set.getFloat("baseHpReg");
		baseCpReg = set.getFloat("baseCpReg");
		baseMpReg = set.getFloat("baseMpReg");
		basePAtk = set.getInteger("basePAtk");
		baseMAtk = set.getInteger("baseMAtk");
		basePDef = set.getInteger("basePDef");
		baseMDef = set.getInteger("baseMDef");
		basePAtkSpd = set.getInteger("basePAtkSpd");
		baseMAtkSpd = set.getInteger("baseMAtkSpd");
		baseShldDef = set.getInteger("baseShldDef");
		baseAtkRange = set.getInteger("baseAtkRange");
		baseShldRate = set.getInteger("baseShldRate");
		baseCritRate = set.getInteger("baseCritRate");
		baseRunSpd = set.getInteger("baseRunSpd");
		baseWalkSpd = set.getInteger("baseWalkSpd");
		collisionRadius = set.getFloat("collision_radius", 5);
		collisionHeight = set.getFloat("collision_height", 5);
	}

	public int getNpcId()
	{
		return 0;
	}

	public StatsSet getSet()
	{
		return _set;
	}

	public void setSet(StatsSet set)
	{
		_set = set;
	}

	public static StatsSet getEmptyStatsSet()
	{
		StatsSet npcDat = new StatsSet();
		npcDat.set("baseSTR", 0);
		npcDat.set("baseCON", 0);
		npcDat.set("baseDEX", 0);
		npcDat.set("baseINT", 0);
		npcDat.set("baseWIT", 0);
		npcDat.set("baseMEN", 0);
		npcDat.set("baseHpMax", 0);
		npcDat.set("baseCpMax", 0);
		npcDat.set("baseMpMax", 0);
		npcDat.set("baseHpReg", 3.e-3f);
		npcDat.set("baseCpReg", 0);
		npcDat.set("baseMpReg", 3.e-3f);
		npcDat.set("basePAtk", 0);
		npcDat.set("baseMAtk", 0);
		npcDat.set("basePDef", 100);
		npcDat.set("baseMDef", 100);
		npcDat.set("basePAtkSpd", 0);
		npcDat.set("baseMAtkSpd", 0);
		npcDat.set("baseShldDef", 0);
		npcDat.set("baseAtkRange", 0);
		npcDat.set("baseShldRate", 0);
		npcDat.set("baseCritRate", 0);
		npcDat.set("baseRunSpd", 0);
		npcDat.set("baseWalkSpd", 0);
		return npcDat;
	}
}