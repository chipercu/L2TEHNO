package l2open.gameserver.model.entity.soi;

import l2open.gameserver.model.L2Player;

import java.util.Map;

public class InitialDelayTask extends l2open.common.RunnableImpl
{
	private L2Player _player = null;
	private SeedOfInfinity _soi = null;

	public InitialDelayTask(L2Player player, SeedOfInfinity soi)
	{
		_player = player;
		_soi = soi;
	}

	@Override
	public void runImpl()
	{
		if(_player != null)
			_soi.initialInstance(_player);
	}
}