package l2open.gameserver.model.base;

/**
 * This class defines all races (human, elf, darkelf, orc, dwarf) that a player can chose.<BR><BR>
 */
@SuppressWarnings( { "nls", "unqualified-field-access", "boxing" })
public enum Race
{
	human,
	elf,
	darkelf,
	orc,
	dwarf,
	kamael,

	// расы мобов.
	plant,
	castle_guard,
	divine,
	demonic,
	undead,
	siege_weapon,
	etc,
	fairy,
	dragon,
	elemental,
	mercenary,
	beast,
	humanoid,
	bug,
	construct,
	animal,
	giant,
	none
}
