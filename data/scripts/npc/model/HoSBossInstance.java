package npc.model;

import l2open.gameserver.model.instances.L2ReflectionBossInstance;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * Данный инстанс используется босами в Hall of Suffering.
 * Босов 2, очистку рефлекшена при смерти не делаем.
 */
public class HoSBossInstance extends L2ReflectionBossInstance
{
	public HoSBossInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	private boolean startBuff = false;

	public void startBuffTask()
	{
		startBuff = true;
	}

	public boolean isStartBuffTask()
	{
		return startBuff;
	}
	@Override
	protected void clearReflection()
	{
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}