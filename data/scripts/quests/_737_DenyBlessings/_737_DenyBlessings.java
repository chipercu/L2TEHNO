package quests._737_DenyBlessings;

import l2open.gameserver.model.base.ClassId;
import quests.Dominion_KillSpecialUnitQuest;

/**
 * @author VISTALL
 * @date 16:19/12.04.2011
 */
public class _737_DenyBlessings extends Dominion_KillSpecialUnitQuest
{
	public _737_DenyBlessings()
	{
		super(737);
	}

	@Override
	protected int startNpcString()
	{
		return 73751; // DEFEAT_S1_HEALERS_AND_BUFFERS;
	}

	@Override
	protected int progressNpcString()
	{
		return 73761; // YOU_HAVE_DEFEATED_S2_OF_S1_HEALERS_AND_BUFFERS;
	}

	@Override
	protected int doneNpcString()
	{
		return 73762; // YOU_HAVE_WEAKENED_THE_ENEMYS_SUPPORT;
	}

	@Override
	protected int getRandomMin()
	{
		return 3;
	}

	@Override
	protected int getRandomMax()
	{
		return 8;
	}

	@Override
	protected ClassId[] getTargetClassIds()
	{
		return new ClassId[]{
				ClassId.bishop,
				ClassId.prophet,
				ClassId.elder,
				ClassId.shillienElder,
				ClassId.cardinal,
				ClassId.hierophant,
				ClassId.evaSaint,
				ClassId.shillienSaint,
				ClassId.doomcryer,
				ClassId.warcryer
		};
	}
}
