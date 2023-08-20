package l2open.gameserver.model;

import l2open.gameserver.model.instances.L2TrapInstance;

import java.util.logging.Logger;

public class L2RoundTerritoryEventTrap extends L2RoundTerritory
{
	private static final Logger _log = Logger.getLogger(L2RoundTerritoryEventTrap.class.getName());

	private final L2Character _effector;
	private final L2Character _owner;

	public L2RoundTerritoryEventTrap(int id, int centerX, int centerY, int radius, int zMin, int zMax, L2Character effector, L2Character owner)
	{
		super(id, centerX, centerY, radius, zMin, zMax);
		_effector = effector;
		_owner = owner;
	}

	@Override
	public void doEnter(L2Object obj)
	{
		super.doEnter(obj);

		if(_effector == null || obj == null || !isInside(obj.getLoc()) || _effector.getObjectId() == obj.getObjectId() || _owner.getObjectId() == obj.getObjectId())
			return;

		if(obj.isCharacter() && obj.getReflection().getId() == _effector.getReflection().getId())
		{
			L2Character effected = (L2Character) obj;
			if(_effector.isTrap())
				((L2TrapInstance) _effector).detonate(effected);
		}
	}
}