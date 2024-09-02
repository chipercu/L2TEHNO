package l2open.gameserver.model.base;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

import l2open.config.ConfigValue;

import static l2open.gameserver.model.base.ClassLevel.*;
import static l2open.gameserver.model.base.ClassType.*;
import static l2open.gameserver.model.base.Race.*;

public enum PlayerClass
{
	HumanFighter(human, Fighter, First), //0
	Warrior(human, Fighter, Second), //1
	Gladiator(human, Fighter, Third), //2
	Warlord(human, Fighter, Third), //3
	HumanKnight(human, Fighter, Second), //4
	Paladin(human, Fighter, Third), //5
	DarkAvenger(human, Fighter, Third), //6
	Rogue(human, Fighter, Second), //7
	TreasureHunter(human, Fighter, Third), //8
	Hawkeye(human, Fighter, Third), //9
	HumanMystic(human, Mystic, First), //10
	HumanWizard(human, Mystic, Second), //11
	Sorceror(human, Mystic, Third), //12
	Necromancer(human, Mystic, Third), //13
	Warlock(human, Mystic, Third), //14
	Cleric(human, Priest, Second), //15
	Bishop(human, Priest, Third), //16
	Prophet(human, Priest, Third), //17

	ElvenFighter(elf, Fighter, First), //18
	ElvenKnight(elf, Fighter, Second), //19
	TempleKnight(elf, Fighter, Third), //20
	Swordsinger(elf, Fighter, Third), //21
	ElvenScout(elf, Fighter, Second), //22
	Plainswalker(elf, Fighter, Third), //23
	SilverRanger(elf, Fighter, Third), //24
	ElvenMystic(elf, Mystic, First), //25
	ElvenWizard(elf, Mystic, Second), //26
	Spellsinger(elf, Mystic, Third), //27
	ElementalSummoner(elf, Mystic, Third), //28
	ElvenOracle(elf, Priest, Second), //29
	ElvenElder(elf, Priest, Third), //30

	DarkElvenFighter(darkelf, Fighter, First), //31
	PalusKnight(darkelf, Fighter, Second), //32
	ShillienKnight(darkelf, Fighter, Third), //33
	Bladedancer(darkelf, Fighter, Third), //34
	Assassin(darkelf, Fighter, Second), //35
	AbyssWalker(darkelf, Fighter, Third), //36
	PhantomRanger(darkelf, Fighter, Third), //37
	DarkElvenMystic(darkelf, Mystic, First), //38
	DarkElvenWizard(darkelf, Mystic, Second), //39
	Spellhowler(darkelf, Mystic, Third), //40
	PhantomSummoner(darkelf, Mystic, Third), //41
	ShillienOracle(darkelf, Priest, Second), //42
	ShillienElder(darkelf, Priest, Third), //43

	OrcFighter(orc, Fighter, First), //44
	orcRaider(orc, Fighter, Second), //45
	Destroyer(orc, Fighter, Third), //46
	orcMonk(orc, Fighter, Second), //47
	Tyrant(orc, Fighter, Third), //48
	orcMystic(orc, Mystic, First), //49
	orcShaman(orc, Mystic, Second), //50
	Overlord(orc, Mystic, Third), //51
	Warcryer(orc, Mystic, Third), //52

	DwarvenFighter(dwarf, Fighter, First), //53
	DwarvenScavenger(dwarf, Fighter, Second), //54
	BountyHunter(dwarf, Fighter, Third), //55
	DwarvenArtisan(dwarf, Fighter, Second), //56
	Warsmith(dwarf, Fighter, Third), //57

	DummyEntry1(null, null, null), //58
	DummyEntry2(null, null, null), //59
	DummyEntry3(null, null, null), //60
	DummyEntry4(null, null, null), //61
	DummyEntry5(null, null, null), //62
	DummyEntry6(null, null, null), //63
	DummyEntry7(null, null, null), //64
	DummyEntry8(null, null, null), //65
	DummyEntry9(null, null, null), //66
	DummyEntry10(null, null, null), //67
	DummyEntry11(null, null, null), //68
	DummyEntry12(null, null, null), //69
	DummyEntry13(null, null, null), //70
	DummyEntry14(null, null, null), //71
	DummyEntry15(null, null, null), //72
	DummyEntry16(null, null, null), //73
	DummyEntry17(null, null, null), //74
	DummyEntry18(null, null, null), //75
	DummyEntry19(null, null, null), //76
	DummyEntry20(null, null, null), //77
	DummyEntry21(null, null, null), //78
	DummyEntry22(null, null, null), //79
	DummyEntry23(null, null, null), //80
	DummyEntry24(null, null, null), //81
	DummyEntry25(null, null, null), //82
	DummyEntry26(null, null, null), //83
	DummyEntry27(null, null, null), //84
	DummyEntry28(null, null, null), //85
	DummyEntry29(null, null, null), //86
	DummyEntry30(null, null, null), //87

	Duelist(human, Fighter, Fourth, ForceMaster), //88
	Dreadnought(human, Fighter, Fourth, WeaponMaster), //89
	PhoenixKnight(human, Fighter, Fourth, ShieldMaster), //90
	HellKnight(human, Fighter, Fourth, ShieldMaster), //91
	Sagittarius(human, Fighter, Fourth, BowMaster), //92
	Adventurer(human, Fighter, Fourth, DaggerMaster), //93
	Archmage(human, Mystic, Fourth, Wizard), //94
	Soultaker(human, Mystic, Fourth, Wizard), //95
	ArcanaLord(human, Mystic, Fourth, Summoner), //96
	Cardinal(human, Priest, Fourth, Healer), //97
	Hierophant(human, Priest, Fourth, Enchanter), //98

	EvaTemplar(elf, Fighter, Fourth, ShieldMaster), //99
	SwordMuse(elf, Fighter, Fourth, Bard), //100
	WindRider(elf, Fighter, Fourth, DaggerMaster), //101
	MoonlightSentinel(elf, Fighter, Fourth, BowMaster), //102
	MysticMuse(elf, Mystic, Fourth, Wizard), //103
	ElementalMaster(elf, Mystic, Fourth, Summoner), //104
	EvaSaint(elf, Priest, Fourth, Healer), //105

	ShillienTemplar(darkelf, Fighter, Fourth, ShieldMaster), //106
	SpectralDancer(darkelf, Fighter, Fourth, Bard), //107
	GhostHunter(darkelf, Fighter, Fourth, DaggerMaster), //108
	GhostSentinel(darkelf, Fighter, Fourth, BowMaster), //109
	StormScreamer(darkelf, Mystic, Fourth, Wizard), //110
	SpectralMaster(darkelf, Mystic, Fourth, Summoner), //111
	ShillienSaint(darkelf, Priest, Fourth, Healer), //112

	Titan(orc, Fighter, Fourth, WeaponMaster), //113
	GrandKhauatari(orc, Fighter, Fourth, ForceMaster), //114
	Dominator(orc, Mystic, Fourth, Enchanter), //115
	Doomcryer(orc, Mystic, Fourth, Enchanter), //116

	FortuneSeeker(dwarf, Fighter, Fourth, WeaponMaster), //117
	Maestro(dwarf, Fighter, Fourth, WeaponMaster), //118

	DummyEntry31(null, null, null), //119
	DummyEntry32(null, null, null), //120
	DummyEntry33(null, null, null), //121
	DummyEntry34(null, null, null), //122

	/** Kamael */
	MaleSoldier(kamael, Fighter, First), // 123
	FemaleSoldier(kamael, Fighter, First), //124
	Troopier(kamael, Fighter, Second), // 125
	Warder(kamael, Fighter, Second), //126
	Berserker(kamael, Fighter, Third), //127
	MaleSoulbreaker(kamael, Fighter, Third), //128
	FemaleSoulbreaker(kamael, Fighter, Third), //129
	Arbalester(kamael, Fighter, Third), //130

	/** kamael */
	Doombringer(kamael, Fighter, Fourth, WeaponMaster), //131
	MaleSoulHound(kamael, Fighter, Fourth, WeaponMaster), //132
	FemaleSoulHound(kamael, Fighter, Fourth, WeaponMaster), //133
	Trickster(kamael, Fighter, Fourth, BowMaster), //134
	Inspector(kamael, Fighter, Third), //135
	Judicator(kamael, Fighter, Fourth, Enchanter); //136

	private Race _race;
	private ClassLevel _level;
	private ClassType _type;
	public ClassType _typeExtended;

	private static final Set<PlayerClass> mainSubclassSet;
	private static final Set<PlayerClass> kamaelSubclassSet;
	private static final Set<PlayerClass> neverSubclassed = EnumSet.of(Overlord, Warsmith);

	private static final Set<PlayerClass> subclasseSet1 = EnumSet.of(DarkAvenger, Paladin, TempleKnight, ShillienKnight);
	private static final Set<PlayerClass> subclasseSet2 = EnumSet.of(TreasureHunter, AbyssWalker, Plainswalker);
	private static final Set<PlayerClass> subclasseSet3 = EnumSet.of(Hawkeye, SilverRanger, PhantomRanger);
	private static final Set<PlayerClass> subclasseSet4 = EnumSet.of(Warlock, ElementalSummoner, PhantomSummoner);
	private static final Set<PlayerClass> subclasseSet5 = EnumSet.of(Sorceror, Spellsinger, Spellhowler);

	/** kamael SubClasses */
	private static final Set<PlayerClass> subclasseSet6 = EnumSet.of(Inspector);

	private static final EnumMap<PlayerClass, Set<PlayerClass>> subclassSetMap = new EnumMap<PlayerClass, Set<PlayerClass>>(PlayerClass.class);

	static
	{
		kamaelSubclassSet = getSet(kamael, Third);

		Set<PlayerClass> subclasses = getSet(null, Third);

		if(ConfigValue.PlayerSubClassOverlord)
			neverSubclassed.remove(Overlord);
		if(ConfigValue.PlayerSubClassWarsmith)
			neverSubclassed.remove(Warsmith);

		subclasses.removeAll(neverSubclassed);
		subclasses.removeAll(kamaelSubclassSet);

		mainSubclassSet = subclasses;

		subclassSetMap.put(DarkAvenger, subclasseSet1);
		subclassSetMap.put(HellKnight, subclasseSet1);
		subclassSetMap.put(Paladin, subclasseSet1);
		subclassSetMap.put(PhoenixKnight, subclasseSet1);
		subclassSetMap.put(TempleKnight, subclasseSet1);
		subclassSetMap.put(EvaTemplar, subclasseSet1);
		subclassSetMap.put(ShillienKnight, subclasseSet1);
		subclassSetMap.put(ShillienTemplar, subclasseSet1);

		subclassSetMap.put(TreasureHunter, subclasseSet2);
		subclassSetMap.put(Adventurer, subclasseSet2);
		subclassSetMap.put(AbyssWalker, subclasseSet2);
		subclassSetMap.put(GhostHunter, subclasseSet2);
		subclassSetMap.put(Plainswalker, subclasseSet2);
		subclassSetMap.put(WindRider, subclasseSet2);

		subclassSetMap.put(Hawkeye, subclasseSet3);
		subclassSetMap.put(Sagittarius, subclasseSet3);
		subclassSetMap.put(SilverRanger, subclasseSet3);
		subclassSetMap.put(MoonlightSentinel, subclasseSet3);
		subclassSetMap.put(PhantomRanger, subclasseSet3);
		subclassSetMap.put(GhostSentinel, subclasseSet3);

		subclassSetMap.put(Warlock, subclasseSet4);
		subclassSetMap.put(ArcanaLord, subclasseSet4);
		subclassSetMap.put(ElementalSummoner, subclasseSet4);
		subclassSetMap.put(ElementalMaster, subclasseSet4);
		subclassSetMap.put(PhantomSummoner, subclasseSet4);
		subclassSetMap.put(SpectralMaster, subclasseSet4);

		subclassSetMap.put(Sorceror, subclasseSet5);
		subclassSetMap.put(Archmage, subclasseSet5);
		subclassSetMap.put(Spellsinger, subclasseSet5);
		subclassSetMap.put(MysticMuse, subclasseSet5);
		subclassSetMap.put(Spellhowler, subclasseSet5);
		subclassSetMap.put(StormScreamer, subclasseSet5);

		subclassSetMap.put(Doombringer, subclasseSet6);
		subclassSetMap.put(MaleSoulHound, subclasseSet6);
		subclassSetMap.put(FemaleSoulHound, subclasseSet6);
		subclassSetMap.put(Trickster, subclasseSet6);

		subclassSetMap.put(Duelist, EnumSet.of(Gladiator));
		subclassSetMap.put(Dreadnought, EnumSet.of(Warlord));
		subclassSetMap.put(Soultaker, EnumSet.of(Necromancer));
		subclassSetMap.put(Cardinal, EnumSet.of(Bishop));
		subclassSetMap.put(Hierophant, EnumSet.of(Prophet));
		subclassSetMap.put(SwordMuse, EnumSet.of(Swordsinger));
		subclassSetMap.put(EvaSaint, EnumSet.of(ElvenElder));
		subclassSetMap.put(SpectralDancer, EnumSet.of(Bladedancer));
		subclassSetMap.put(Titan, EnumSet.of(Destroyer));
		subclassSetMap.put(GrandKhauatari, EnumSet.of(Tyrant));
		subclassSetMap.put(Dominator, EnumSet.of(Overlord));
		subclassSetMap.put(Doomcryer, EnumSet.of(Warcryer));
		subclassSetMap.put(Warsmith, EnumSet.of(BountyHunter));
		subclassSetMap.put(Maestro, EnumSet.of(BountyHunter));
	}

	PlayerClass(Race race, ClassType type, ClassLevel level)
	{
		this(race, type, level, null);
	}

	PlayerClass(Race race, ClassType type, ClassLevel level, ClassType extended)
	{
		_race = race;
		_level = level;
		_type = type;
		_typeExtended = extended;
	}

	public final Set<PlayerClass> getAvailableSubclasses()
	{
		if(_race == Race.kamael)
			return EnumSet.copyOf(kamaelSubclassSet);

		Set<PlayerClass> subclasses = null;

		if(_level == Third || _level == Fourth)
		{
			subclasses = EnumSet.copyOf(mainSubclassSet);

			subclasses.removeAll(neverSubclassed);
			subclasses.remove(this);

			switch(_race)
			{
				case elf:
					subclasses.removeAll(getSet(darkelf, Third));
					break;
				case darkelf:
					subclasses.removeAll(getSet(elf, Third));
					break;
			}

			Set<PlayerClass> unavailableClasses = subclassSetMap.get(this);

			if(unavailableClasses != null)
				subclasses.removeAll(unavailableClasses);
		}

		return subclasses;
	}

	public static EnumSet<PlayerClass> getSet(Race race, ClassLevel level)
	{
		EnumSet<PlayerClass> allOf = EnumSet.noneOf(PlayerClass.class);

		for(PlayerClass playerClass : EnumSet.allOf(PlayerClass.class))
			if(race == null || playerClass.isOfRace(race))
				if(level == null || playerClass.isOfLevel(level))
					allOf.add(playerClass);

		return allOf;
	}

	public final boolean isOfRace(Race race)
	{
		return _race == race;
	}

	public final boolean isOfType(ClassType type)
	{
		return _type == type || _typeExtended == type;
	}

	public final boolean isOfLevel(ClassLevel level)
	{
		return _level == level;
	}

	/**
	 * Проверяет принципиальную совместимость двух сабов.
	 */
	public static boolean areClassesComportable(PlayerClass c1, PlayerClass c2)
	{
		if(c1.isOfRace(Race.kamael) != c2.isOfRace(Race.kamael))
			return false; // камаэли только с камаэлями
		if(c1.isOfRace(Race.elf) && c2.isOfRace(Race.darkelf) || c1.isOfRace(Race.darkelf) && c2.isOfRace(Race.elf))
			return false; // эльфы несовместимы с темными
		if(c1 == PlayerClass.Overlord || c1 == PlayerClass.Warsmith || c2 == PlayerClass.Overlord || c2 == PlayerClass.Warsmith)
			return false; // эти вообще
		if(subclassSetMap.get(c1) == subclassSetMap.get(c2))
			return false; // однотипные
		return true;
	}
}