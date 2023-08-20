package l2open.gameserver.ai;

import l2open.gameserver.model.L2Character;

public class SiegeGuardFighter extends SiegeGuard
{
	public SiegeGuardFighter(L2Character actor)
	{
		super(actor);
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
		return 50;
	}

	@Override
	public int getRateDEBUFF()
	{
		return 50;
	}

	@Override
	public int getRateDAM()
	{
		return 75;
	}

	@Override
	public int getRateSTUN()
	{
		return 50;
	}

	@Override
	public int getRateBUFF()
	{
		return 10;
	}

	@Override
	public int getRateHEAL()
	{
		return 50;
	}
}