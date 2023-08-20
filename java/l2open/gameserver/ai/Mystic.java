package l2open.gameserver.ai;

import l2open.gameserver.model.L2Character;

public class Mystic extends DefaultAI
{
	public Mystic(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		return super.thinkActive() || defaultThinkBuff(10);
	}

	@Override
	protected boolean createNewTask()
	{
		return defaultFightTask();
	}

	@Override
	public int getRatePHYS()
	{
		return _dam_skills.length == 0 ? 30 : 10;
	}

	@Override
	public int getRateDOT()
	{
		return 25;
	}

	@Override
	public int getRateDEBUFF()
	{
		return 20;
	}

	@Override
	public int getRateDAM()
	{
		return 60;
	}

	@Override
	public int getRateSTUN()
	{
		return 15;
	}

	@Override
	public int getRateBUFF()
	{
		return 10;
	}

	@Override
	public int getRateHEAL()
	{
		return 20;
	}
}