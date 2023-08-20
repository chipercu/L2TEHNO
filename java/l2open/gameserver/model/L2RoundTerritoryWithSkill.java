package l2open.gameserver.model;

import l2open.gameserver.model.instances.L2TrapInstance;

import java.util.logging.Logger;

public class L2RoundTerritoryWithSkill extends L2RoundTerritory
{
	private static final Logger _log = Logger.getLogger(L2RoundTerritoryWithSkill.class.getName());

	private final L2Character _effector;
	private final L2Skill _skill;

	public L2RoundTerritoryWithSkill(int id, int centerX, int centerY, int radius, int zMin, int zMax, L2Character effector, L2Skill skill)
	{
		super(id, centerX, centerY, radius, zMin, zMax);
		_effector = effector;
		_skill = skill;
		if(_skill == null)
			_log.severe("L2RoundTerritoryWithSkill with null skill: " + getId());
	}

	@Override
	public void doEnter(L2Object obj)
	{
		super.doEnter(obj);

		if(_effector == null || obj == null || _skill == null || !isInside(obj.getLoc()))
			return;

		if(obj.isCharacter() && obj.getReflection().getId() == _effector.getReflection().getId())
		{
			L2Character effected = (L2Character) obj;
			if(_effector.isTrap())
				((L2TrapInstance) _effector).detonate(effected);
			else if(_skill.checkTarget(_effector, effected, null, false, false) == null)
				_skill.getEffects(_effector, effected, false, false);
		}
	}

	@Override
	public void doLeave(L2Object obj, boolean notify)
	{
		super.doLeave(obj, notify);

		if(_effector == null || obj == null)
			return;

		if(!_effector.isTrap() && obj.isCharacter() && obj.getReflection().getId() == _effector.getReflection().getId() && _skill.getId() != 1422 && _skill.getId() != 5145)
			((L2Character) obj).getEffectList().stopEffect(_skill.getId());
	}
}