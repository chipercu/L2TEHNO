package quests._736_WeakenTheMagic;

import l2open.gameserver.model.base.ClassId;
import quests.Dominion_KillSpecialUnitQuest;

/**
 * @author VISTALL
 * @date 16:18/12.04.2011
 */
public class _736_WeakenTheMagic extends Dominion_KillSpecialUnitQuest
{
	public _736_WeakenTheMagic()
	{
		super(736);
	}

	@Override
	protected int startNpcString()
	{
		return 73651; // DEFEAT_S1_WIZARDS_AND_SUMMONERS;
	}

	@Override
	protected int progressNpcString()
	{
		return 73661; // YOU_HAVE_DEFEATED_S2_OF_S1_ENEMIES;
	}

	@Override
	protected int doneNpcString()
	{
		return 73662; // YOU_WEAKENED_THE_ENEMYS_MAGIC;
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
				ClassId.sorceror,
				ClassId.warlock,
				ClassId.spellsinger,
				ClassId.elementalSummoner,
				ClassId.spellhowler,
				ClassId.phantomSummoner,
				ClassId.archmage,
				ClassId.arcanaLord,
				ClassId.mysticMuse,
				ClassId.elementalMaster,
				ClassId.stormScreamer,
				ClassId.spectralMaster,
				ClassId.necromancer,
				ClassId.soultaker
		};
	}
}
