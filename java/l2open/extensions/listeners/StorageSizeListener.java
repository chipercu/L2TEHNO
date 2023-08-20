package l2open.extensions.listeners;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExStorageMaxCount;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;

 /**
 * НЕ ИСПОЛЬЗУЕТСЯ!
 **/
public class StorageSizeListener extends StatsChangeListener
{
	public StorageSizeListener(Stats stat)
	{
		super(stat);
	}

	@Override
	public void statChanged(Double oldValue, double newValue, double baseValue, Env env)
	{
		_calculator._character.sendPacket(new ExStorageMaxCount((L2Player) _calculator._character));
	}
}