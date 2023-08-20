package npc.model;

import l2open.config.ConfigValue;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;

public final class DebugNpcInstance extends L2NpcInstance
{
	public DebugNpcInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		if(ConfigValue.WaterTest)
		{
			_log.info("DebugNpcInstance: onSpawn");
			l2open.util.Util.test();
		}
	}
}