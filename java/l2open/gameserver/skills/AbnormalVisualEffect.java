package l2open.gameserver.skills;

/**
 * @author : Diagod
 **/
public enum AbnormalVisualEffect
{
	/*0*/ave_none				(0x00000000, 	0),
	/*1*/ave_dot_bleeding		(0x00000001,	0),
	/*2*/ave_dot_poison			(0x00000002, 	0),
	/*3*/UNK_3, // Мигает красный круг вокруг живота персонажа.
	/*4*/UNK_4, // Висит и мигает кусок для на животе персонажа.
	/*5*/ave_turn_flee			(0x80000000, 	0),
	/*6*/UNK_6, // Из персонажа появляется куча бардового дыма и зеленькие кружочки.
	/*7*/ave_stun				(0x00000040, 	0),
	/*8*/ave_sleep				(0x00000080, 	0),
	/*9*/ave_silence			(0x00000100, 	0),
	/*10*/ave_root				(0x00000200, 	0),
	/*11*/ave_paralyze			(0x00000400, 	0),
	/*12*/ave_flesh_stone		(0x00000800, 	0),
	/*13*/UNK_13, //unk
	/*14*/ave_big_head			(0x00002000, 	0),
	/*15*/UNK_15, // Пламя огня начиная с ног персонажа.
	/*16*/ave_change_texture	(0x00008000, 	0),
	/*17*/ave_big_body			(0x00010000, 	0),
	/*18*/ave_floating_root		(0x00020000, 	0),
	/*19*/ave_dance_root		(0x00080000,	0),
	/*20*/UNK_20, // Звездочки как у стана и на ногах красный круг.
	/*21*/ave_stealth			(0x00100000, 	0),
	/*22*/UNK_22, // Вокруг живота синий туман с электричеством.
	/*23*/UNK_23, // Вокруг живота синий туман с электричеством.
	/*24*/ave_magic_square		(0x00800000, 	0),
	/*25*/UNK_25, // Висит и мигает кусок для на животе персонажа.
	/*26*/UNK_26, // Землетрясение.
	/*27*/UNK_27, //unk
	/*28*/ave_ultimate_defence	(0x08000000, 	0),
	/*29*/ave_vp_keep			(0x10000000, 	0), // ???  // TODO: 
	/*30*/ave_real_target		(0x20000000, 	0),
	/*31*/ave_death_mark		(0x40000000,	0),
	/*32*/UNK_32, // Синяя морда черепа над головой.
    // special effects
	/*33*/ave_invincibility		(0x00000001, 	1),
	/*34*/ave_air_battle_slow	(0x00000002, 	1),
	/*35*/ave_air_battle_root	(0x00000004, 	1),
	/*36*/ave_change_wp			(0x00000008, 	1),
	/*37*/ave_change_hair_g		(0x00000010, 	1),
	/*38*/ave_change_hair_p		(0x00000020, 	1),
	/*39*/ave_change_hair_b		(0x00000040, 	1),
	/*40*/UNK_40, // unk
	/*41*/ave_stigma_of_silen	(0x00000100, 	1),
	/*42*/ave_speed_down		(0x00000200, 	1),
	/*43*/ave_frozen_pillar		(0x00000400, 	1),
	/*44*/ave_change_ves_s		(0x00000800,	1),
	/*45*/ave_change_ves_c		(0x00001000,	1),
	/*46*/ave_change_ves_d		(0x00002000,	1),
	/*47*/UNK_47, // Зеленый круговой дождь в тумане над головой персонажа.
	/*48*/SOA_REWPAWN,

    SUNK17,    // ("sunk17", 0x00010000, true), // пусто
    SUNK18,    // ("sunk18", 0x00020000, true), // пусто
    SUNK19,    // ("sunk19", 0x00040000, true), // пусто
    NEVITSYSTEM,    // ("nevitSystem", 0x00080000, true), // пусто
	// -------------------------------------
	/**/ave_unk20			(0x00080000,	1),
	/**/ave_unk22			(0x00200000,	1), // меняет на оружие какое-то
    SUNK23,    // ("sunk23", 0x00400000, true), // пусто
    SUNK24,    // ("sunk24", 0x00800000, true), // пусто
    SUNK25,    // ("sunk25", 0x01000000, true), // пусто
    SUNK26,    // ("sunk26", 0x02000000, true), // пусто
    SUNK27,    // ("sunk27", 0x04000000, true), // пусто
    SUNK28,    // ("sunk28", 0x08000000, true), // пусто
    SUNK29,    // ("sunk29", 0x10000000, true), // пусто
    SUNK30,    // ("sunk30", 0x20000000, true), // пусто
    SUNK31,    // ("sunk31", 0x40000000, true), // пусто
    SUNK32,    // ("sunk32", 0x80000000, true), // пусто
	// -------------------------------------
	/**/br_ave_afro_gold	(0x00000001, 	2), // тут нужно узнать порядок, ибо я хз))) ??? 0x0010  // TODO: 
	/**/br_ave_afro_normal	(0x00000002, 	2), // тут нужно узнать порядок, ибо я хз))) ??? 0x0040  // TODO: 
	/**/br_ave_afro_pink	(0x00000004, 	2), // тут нужно узнать порядок, ибо я хз))) ??? 0x0020  // TODO: 
	/**/br_ave_power_of_eva	(0x00000008, 	2),
	/**/br_ave_soul_avatar	(0x00000010, 	2), // ??? 0x0080  // TODO: 
	/**/br_ave_vesper1		(0x00000020, 	2),
	/**/br_ave_vesper2		(0x00000040, 	2),
	/**/br_ave_vesper3		(0x00000080, 	2),
	
	// NEW Lindvior
    STIGMA_1,    // ("stigma_1", 0x000100, true),
    STAKATOROOT,    // ("stakatoroot", 0x000200, true),
    ICE_SHACKLE,    // ("ice_shackle", 0x000400, true),
    VESPERS,    // ("vespers", 0x000800, false,true),
    VESPERC,    // ("vesperc", 0x001000, false,true),
    VESPERD,    // ("vesperd", 0x002000, false,true),
    HUNTING_BONUS,    // ("hunting_bonus", 0x004000,true),
	/**/ave_arcane_invul	(0x00008000,	1),
    AIR_SHACKLE,    // ("air_shackle", 0x010000, true),
    SUNK50,    // ("sunk50", 0x020000, true),
    KNOCKDOWN,    // ("knockdown", 0x040000, true),
    SUNK52,    // ("sunk52", 0x080000, true),
    SUNK53,    // ("sunk53", 0x100000, true),
    SUNK54,    // ("sunk54", 0x200000, true),
    SUNK55,    // ("sunk55", 0x400000, true),
    CELLED_CUBE,    // ("celled_cube", 0x800000, true),
    SPECIAL_AURA,    // ("special_aura", 0x1000000, true),
    SPECIAL_AURA_1,    // ("special_aura_1", 0x2000000, true),
    SPECIAL_AURA_2,    // ("special_aura_2", 0x4000000, true),
    SPECIAL_AURA_3,    // ("special_aura_3", 0x8000000, true);
    SPECIAL_AURA_4,
    CHANGEBODY,
    E_RADUGA,
    TALISMANPOWER1,
    TALISMANPOWER2,
    TALISMANPOWER3,
    TALISMANPOWER4,
    TALISMANPOWER5,

    KNOCKBACK,
    MP_SHIELD,
    DEPORT,
    BR_SOUL_AVATAR,
    AURA_BUFF,
    CHANGE_7ANNIVERSARY,
    BR_BEAM_SWORD_ONEHAND,
    BR_BEAM_SWORD_DUAL,
    BR_POWER_OF_EVA,
	/**/ave_ghost_stun		(0x00040000, 	0),
    FLOATING_ROOT,
	/**/ave_seizure1		(0x00200000, 	0),
	/**/ave_seizure2		(0x00400000, 	0),
    ULTIMATE_DEFENCE,
	/**/ave_vp_up			(0x10000000, 	0), // ???  // TODO: 
	/**/ave_shake			(0x02000000, 	0),
    SPEED_DOWN,
    TIME_BOMB,
    AIR_BATTLE_SLOW,
    AIR_BATTLE_ROOT,
    AURA_DEBUFF_SELF,
    AURA_DEBUFF,
    HURRICANE_SELF,
    HURRICANE;
	
	private final int _mask;
	private final int _type;

	private AbnormalVisualEffect()
	{
		_mask = 0;
		_type = 0;
	}

	private AbnormalVisualEffect(int mask, int type)
	{
		_mask = mask;
		_type = type;
	}

	public final int getMask()
	{
		return _mask;
	}

	public final boolean isSpecial()
	{
		return _type == 1;
	}

	public final boolean isEvent()
	{
		return _type == 2;
	}
}