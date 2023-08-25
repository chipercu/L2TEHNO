package l2open.gameserver.ai;

import l2open.gameserver.model.L2Character;

public class SiegeGuardRanger extends SiegeGuard
{
	public SiegeGuardRanger(L2Character actor)
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
		return 25;
	}

	@Override
	public int getRateDAM()
	{
		return 75;
	}

	@Override
	public int getRateSTUN()
	{
		return 75;
	}

	@Override
	public int getRateBUFF()
	{
		return 5;
	}

	@Override
	public int getRateHEAL()
	{
		return 50;
	}
}