package l2open.gameserver.ai;

import l2open.gameserver.model.L2Character;

public class Priest extends DefaultAI
{
	public Priest(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		return super.thinkActive() || defaultThinkBuff(10, 5);
	}

	@Override
	protected boolean createNewTask()
	{
		return defaultFightTask();
	}

	@Override
	public int getRatePHYS()
	{
		return 25;
	}

	@Override
	public int getRateDOT()
	{
		return 40;
	}

	@Override
	public int getRateDEBUFF()
	{
		return 40;
	}

	@Override
	public int getRateDAM()
	{
		return 75;
	}

	@Override
	public int getRateSTUN()
	{
		return 20;
	}

	@Override
	public int getRateBUFF()
	{
		return 25;
	}

	@Override
	public int getRateHEAL()
	{
		return 90;
	}
}