package l2open.gameserver.skills.triggers;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.conditions.Condition;
import org.apache.commons.lang.ArrayUtils;

public class TriggerInfo extends L2Skill.AddedSkill
{
	private final TriggerType _type;
	private final double _chance;
	public final byte _is_item;
	private Condition[] _conditions = new Condition[0];

	public TriggerInfo(int id, int level, TriggerType type, double chance, byte is_item)
	{
		super(id, level);
		_type = type;
		_chance = chance;
		_is_item = is_item;
	}

	public final void addCondition(Condition c)
	{
		_conditions = ((Condition[]) ArrayUtils.add(_conditions, c));
	}

	public boolean checkCondition(L2Character actor, L2Character target, L2Character aimTarget, L2Skill owner, double damage)
	{
		if (getSkill().checkTarget(actor, aimTarget, aimTarget, (getSkill().getId() == 5682 || getSkill().getId() == 3592), false) != null)
		{
			return false;
		}
		Env env = new Env();
		env.character = actor;
		env.skill = owner;
		env.target = target;
		env.value = damage;

		for (Condition c : _conditions)
			if (!(c.test(env)))
				return false;
		return true;
	}

	public TriggerType getType()
	{
		return _type;
	}

	public double getChance()
	{
		return _chance;
	}
}
