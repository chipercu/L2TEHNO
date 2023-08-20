package quests._735_MakeSpearsDull;

import l2open.gameserver.model.base.ClassId;
import quests.Dominion_KillSpecialUnitQuest;

/**
 * @author VISTALL
 * @date 16:14/12.04.2011
 */
public class _735_MakeSpearsDull extends Dominion_KillSpecialUnitQuest
{
	public _735_MakeSpearsDull()
	{
		super(735);
	}

	@Override
	protected int startNpcString()
	{
		return 73551; // DEFEAT_S1_WARRIORS_AND_ROGUES;
	}

	@Override
	protected int progressNpcString()
	{
		return 73561; // YOU_HAVE_DEFEATED_S2_OF_S1_WARRIORS_AND_ROGUES;
	}

	@Override
	protected int doneNpcString()
	{
		return 73562; // YOU_WEAKENED_THE_ENEMYS_ATTACK;
	}

	@Override
	protected int getRandomMin()
	{
		return 15;
	}

	@Override
	protected int getRandomMax()
	{
		return 20;
	}

	@Override
	protected ClassId[] getTargetClassIds()
	{
		return new ClassId[]{
				ClassId.gladiator,
				ClassId.warlord,
				ClassId.treasureHunter,
				ClassId.hawkeye,
				ClassId.plainsWalker,
				ClassId.silverRanger,
				ClassId.abyssWalker,
				ClassId.phantomRanger,
				ClassId.destroyer,
				ClassId.tyrant,
				ClassId.bountyHunter,
				ClassId.duelist,
				ClassId.dreadnought,
				ClassId.sagittarius,
				ClassId.adventurer,
				ClassId.windRider,
				ClassId.moonlightSentinel,
				ClassId.ghostHunter,
				ClassId.ghostSentinel,
				ClassId.titan,
				ClassId.grandKhauatari,
				ClassId.fortuneSeeker,
				ClassId.berserker,
				ClassId.maleSoulbreaker,
				ClassId.femaleSoulbreaker,
				ClassId.arbalester,
				ClassId.doombringer,
				ClassId.maleSoulhound,
				ClassId.femaleSoulhound,
				ClassId.trickster,
				ClassId.swordSinger,
				ClassId.swordMuse,
				ClassId.bladedancer,
				ClassId.spectralDancer,
				ClassId.inspector,
				ClassId.judicator
		};
	}
}
