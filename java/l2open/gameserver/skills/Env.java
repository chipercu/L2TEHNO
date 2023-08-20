package l2open.gameserver.skills;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;

/**
 *
 * An Env object is just a class to pass parameters to a calculator such as L2Player,
 * L2ItemInstance, Initial value.
 *
 */
public final class Env
{
	public L2Character character;
	public L2Character target;
	public L2Skill skill;
	public double value;
	public int skill_mastery=0;

	public Env()
	{}

	public Env(L2Character cha, L2Character tar, L2Skill sk)
	{
		character = cha;
		target = tar;
		skill = sk;
	}
}
