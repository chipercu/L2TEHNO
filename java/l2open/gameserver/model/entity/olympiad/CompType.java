package l2open.gameserver.model.entity.olympiad;

import l2open.config.ConfigValue;

public enum CompType
{
	CLASSED(2, ConfigValue.AltOlyClassedRewItemCount, 3, true),
	NON_CLASSED(2, ConfigValue.AltOlyNonClassedRewItemCount, 5, true),
	TEAM_RANDOM(6, ConfigValue.AltOlyRandomTeamRewItemCount, 5, false),
	TEAM(2, ConfigValue.AltOlyTeamRewItemCount, 5, false);

	private int _minSize;
	private int _reward;
	private int _looseMult;
	private boolean _hasBuffer;

	private CompType(int minSize, int reward, int looseMult, boolean hasBuffer)
	{
		_minSize = minSize;
		_reward = reward;
		_looseMult = looseMult;
		_hasBuffer = hasBuffer;
	}

	public int getMinSize()
	{
		return _minSize;
	}

	public int getReward()
	{
		return _reward;
	}

	public int getLooseMult()
	{
		return _looseMult;
	}

	public boolean hasBuffer()
	{
		return _hasBuffer;
	}
}