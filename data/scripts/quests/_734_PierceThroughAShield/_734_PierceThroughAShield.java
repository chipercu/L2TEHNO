package quests._734_PierceThroughAShield;

import l2open.gameserver.model.base.ClassId;
import quests.Dominion_KillSpecialUnitQuest;

/**
 * @author VISTALL
 * @date 15:56/12.04.2011
 */
public class _734_PierceThroughAShield extends Dominion_KillSpecialUnitQuest
{
	public _734_PierceThroughAShield()
	{
		super(734);
	}

	@Override
	protected int startNpcString()
	{
		return 73451; // DEFEAT_S1_ENEMY_KNIGHTS;
	}

	@Override
	protected int progressNpcString()
	{
		return 73461; // YOU_HAVE_DEFEATED_S2_OF_S1_KNIGHTS;
	}

	@Override
	protected int doneNpcString()
	{
		return 73462; // YOU_WEAKENED_THE_ENEMYS_DEFENSE
	}

	@Override
	protected int getRandomMin()
	{
		return 10;
	}

	@Override
	protected int getRandomMax()
	{
		return 15;
	}

	@Override
	protected ClassId[] getTargetClassIds()
	{
		return new ClassId[]{
				ClassId.darkAvenger,
				ClassId.hellKnight,
				ClassId.paladin,
				ClassId.phoenixKnight,
				ClassId.templeKnight,
				ClassId.evaTemplar,
				ClassId.shillienKnight,
				ClassId.shillienTemplar
		};
	}
}
