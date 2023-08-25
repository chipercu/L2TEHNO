package l2open.gameserver.model.instances;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * @Author: Death
 * @Date: 17/9/2007
 * @Time: 19:11:50
 *
 * Этот инстанс просто для отрисовки умершей вышки на месте оригинальной на осаде
 * Фэйковый инстанс неуязвим.
 * @see l2open.gameserver.model.instances.L2ControlTowerInstance#spawnMe()
 * @see l2open.gameserver.model.instances.L2ControlTowerInstance#onDecay()
 */
public class L2FakeTowerInstance extends L2NpcInstance
{
	public L2FakeTowerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	/**
	 * Фэйковые вышки нельзя атаковать
	 */
	@Override
	public boolean isAutoAttackable(L2Character player)
	{
		return false;
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}

	/**
	 * Фэйковые вышки неуязвимы
	 * @return true
	 */
	@Override
	public boolean isInvul()
	{
		return true;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}
}