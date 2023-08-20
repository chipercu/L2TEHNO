package l2open.gameserver.skills;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Character;

/**
double __cdecl CCreature::GetAttackTraitBonus(struct CCreature *a1)
{
  __int64 v1; // rdx@0
  double v2; // xmm0_8@0
  __m128i v3; // xmm6@0
  __m128i v4; // xmm8@0
  __int64 v5; // r12@1
  struct CCreature *v6; // r13@1
  __int64 v7; // rbp@1
  __int64 v8; // r9@1
  __int64 v9; // r8@1
  double result; // xmm0_8@2
  __m128i *v11; // xmm6_8@3
  int v12; // ebx@3
  unsigned __int64 v13; // rdi@3
  _DWORD *v14; // rsi@3
  double v15; // xmm6_8@11
  __int128 v16; // [sp+30h] [bp-68h]@1
  __int128 v17; // [sp+40h] [bp-58h]@1

  _mm_store_si128((__m128i *)&v17, v3);
  _mm_store_si128((__m128i *)&v16, v4);
  v5 = v1;
  v6 = a1;
  v7 = *(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex);
  v8 = *(_DWORD *)(v7 + 32024);
  v9 = dword_1E8EC90[v8 + 0x100000];
  dword_1E8EC90[v8 + 0x100000] = v9 + 1;
  qword_226F890[v9 + 1000 * v8] = (__int64)&off_AD2E70;
  CCreature::GetWeaponTraitBonus(v1, *(_DWORD *)(*((_QWORD *)a1 + 338) + 2124i64));
  if ( v2 == 0.0 )
  {
    --dword_1E8EC90[*(_DWORD *)(v7 + 32024) + 0x100000];
    result = 0.0;
  }
  else
  {
    *(double *)&v11 = 1.0;
    v12 = 9;
    v13 = 9i64;
    v14 = &unk_E5DBC4;
    do
    {
      if ( *v14 == 2 && *(_BYTE *)sub_53E13C((__int64)v6 + 5940, v13) && *(_BYTE *)sub_53E13C(v5 + 6320, v13) )
      {
        ((void (__usercall *)(__int64@<rdx>, __int64@<rcx>, int@<r8d>, char@<r9b>, __m128i *@<xmm6>))CCreature::GetGeneralTraitBonus)(
          v5,
          (__int64)v6,
          v12,
          1,
          v11);
        *(double *)&v11 = *(double *)&v11 * v2;
      }
      ++v12;
      ++v13;
      ++v14;
    }
    while ( v12 < 42 );
    if ( *(double *)&v11 == 0.0 )
    {
      --dword_1E8EC90[*(_DWORD *)(v7 + 32024) + 0x100000];
      result = 0.0;
    }
    else
    {
      v15 = *(double *)&v11 * v2;
      if ( v15 >= 0.05 )
      {
        if ( v15 > 2.0 )
          v15 = 2.0;
      }
      else
      {
        v15 = 0.05;
      }
      --dword_1E8EC90[*(_DWORD *)(v7 + 32024) + 0x100000];
      result = v15;
    }
  }
  return result;
}
**/
/**
__int64 __usercall CCreature::GetGeneralTraitBonus@<rax>(__int64 a1@<rdx>, __int64 a2@<rcx>, int a3@<r8d>, char a4@<r9b>, __m128i a5@<xmm6>)
{
  char v5; // r12@1
  __int64 v6; // rsi@1
  __int64 v7; // rbp@1
  __int64 v8; // rdi@1
  __int64 v9; // r10@1
  __int64 v10; // rdx@1
  unsigned __int64 v11; // rbx@2
  __int64 result; // rax@3
  int v13; // eax@4
  double v14; // xmm6_8@11
  double v15; // xmm6_8@11
  __int128 v16; // [sp+30h] [bp-48h]@1

  _mm_store_si128((__m128i *)&v16, a5);
  v5 = a4;
  v6 = a1;
  v7 = a2;
  v8 = *(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex);
  v9 = *(_DWORD *)(v8 + 32024);
  v10 = dword_1E8EC90[v9 + 0x100000];
  dword_1E8EC90[v9 + 0x100000] = v10 + 1;
  qword_226F890[v10 + 1000 * v9] = (__int64)&off_AD29B0;
  if ( (unsigned int)(a3 - 1) > 0x28 )
  {
    result = *(_DWORD *)(v8 + 32024);
    --dword_1E8EC90[result + 0x100000];
  }
  else
  {
    v11 = a3;
    if ( *(_BYTE *)sub_53E13C(v6 + 6362, a3) )
    {
      result = *(_DWORD *)(v8 + 32024);
      --dword_1E8EC90[result + 0x100000];
      return result;
    }
    v13 = *((_DWORD *)&off_A5DBA0 + v11 + 0x100000);
    if ( v13 == 2 )
    {
      if ( !*(_BYTE *)sub_53E13C(v7 + 5940, v11) || !*(_BYTE *)sub_53E13C(v6 + 6320, v11) )
      {
        result = *(_DWORD *)(v8 + 32024);
        --dword_1E8EC90[result + 0x100000];
        return result;
      }
LABEL_11:
      v14 = *(double *)sub_53E1D4(v7 + 5984, v11);
      v15 = v14 - *(double *)sub_53E1D4(v6 + 6408, v11) + 1.0;
      result = *(_DWORD *)(v8 + 32024);
      --dword_1E8EC90[result + 0x100000];
      return result;
    }
    if ( v13 == 3 )
    {
      if ( v5 )
      {
        result = *(_DWORD *)(v8 + 32024);
        --dword_1E8EC90[result + 0x100000];
        return result;
      }
      goto LABEL_11;
    }
    result = *(_DWORD *)(v8 + 32024);
    --dword_1E8EC90[result + 0x100000];
  }
  return result;
}
**/
/**
//----- (000000000084DD7C) ----------------------------------------------------
bool __cdecl CSkillEffect_p_attack_trait::Pump(struct CCreature *a1, const struct CSkillInfo *a2, int a3)
{
  const struct CSkillInfo *v3; // rdi@1
  struct CCreature *v4; // rbx@1
  __int64 v5; // rsi@1
  __int64 v6; // r8@1
  __int64 v7; // rdx@1
  signed int v8; // eax@1
  bool result; // al@3
  double *v10; // rax@4

  v3 = a2;
  v4 = a1;
  v5 = *(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex);
  v6 = *(_DWORD *)(v5 + 32024);
  v7 = dword_1E8EC90[v6 + 0x100000];
  dword_1E8EC90[v6 + 0x100000] = v7 + 1;
  qword_226F890[v7 + 1000 * v6] = (__int64)&off_C258A0;
  v8 = *((_DWORD *)a1 + 4);
  if ( v8 > 0 || v8 < 42 )
  {
    *(_BYTE *)sub_53E13C((__int64)v3 + 5940, *((_DWORD *)a1 + 4)) = 1;
    v10 = (double *)sub_53E1D4((__int64)v3 + 5984, *((_DWORD *)a1 + 4));
    *v10 = (*((double *)v4 + 3) + 100.0) / 100.0 * *v10;
    --dword_1E8EC90[*(_DWORD *)(v5 + 32024) + 0x100000];
    result = 1;
  }
  else
  {
    --dword_1E8EC90[*(_DWORD *)(v5 + 32024) + 0x100000];
    result = 0;
  }
  return result;
}
// C258A0: using guessed type void *off_C258A0;
// 1E8EC90: using guessed type int dword_1E8EC90[];
// 226F890: using guessed type __int64 qword_226F890[];
// 299655C8: using guessed type int TlsIndex;

//----- (000000000084DE74) ----------------------------------------------------
bool __cdecl CSkillEffect_p_defence_trait::Pump(struct CCreature *a1, const struct CSkillInfo *a2, int a3)
{
  const struct CSkillInfo *v3; // rdi@1
  struct CCreature *v4; // rbx@1
  __int64 v5; // rsi@1
  __int64 v6; // r8@1
  __int64 v7; // rdx@1
  signed int v8; // eax@1
  double *v9; // rax@5
  bool result; // al@5

  v3 = a2;
  v4 = a1;
  v5 = *(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex);
  v6 = *(_DWORD *)(v5 + 32024);
  v7 = dword_1E8EC90[v6 + 0x100000];
  dword_1E8EC90[v6 + 0x100000] = v7 + 1;
  qword_226F890[v7 + 1000 * v6] = (__int64)&off_C25990;
  v8 = *((_DWORD *)a1 + 4);
  if ( v8 <= 0 || v8 >= 42 )
  {
    --dword_1E8EC90[*(_DWORD *)(v5 + 32024) + 0x100000];
    result = 0;
  }
  else
  {
    *(_BYTE *)sub_53E13C((__int64)v3 + 6320, *((_DWORD *)a1 + 4)) = 1;
    if ( *((_BYTE *)a1 + 32) )
      *(_BYTE *)sub_53E13C((__int64)v3 + 6362, *((_DWORD *)a1 + 4)) = 1;
    v9 = (double *)sub_53E1D4((__int64)v3 + 6408, *((_DWORD *)a1 + 4));
    *v9 = (*((double *)v4 + 3) + 100.0) / 100.0 * *v9;
    --dword_1E8EC90[*(_DWORD *)(v5 + 32024) + 0x100000];
    result = 1;
  }
  return result;
}
**/
public enum SkillTrait
{
	trait_bleed(TraitGroup.trait_of_skill)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().trait_bleed;
		}

		@Override
		public final double calcPower(L2Character target)
		{
			return target.getTraitStat().trait_bleed_power;
		}

		@Override
		public final boolean fullResist(L2Character target)
		{
			return target.getTraitStat().full_trait_bleed;
		}
	},
	trait_boss(TraitGroup.trait_of_npc)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().trait_boss;
		}

		@Override
		public final double calcPower(L2Character target)
		{
			return target.getTraitStat().trait_boss_power;
		}

		@Override
		public final boolean fullResist(L2Character target)
		{
			return target.getTraitStat().full_trait_boss;
		}
	},
	trait_derangement(TraitGroup.trait_of_skill)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().trait_derangement;
		}

		@Override
		public final double calcPower(L2Character target)
		{
			return target.getTraitStat().trait_derangement_power;
		}

		@Override
		public final boolean fullResist(L2Character target)
		{
			return target.getTraitStat().full_trait_derangement;
		}
	},
	trait_hold(TraitGroup.trait_of_skill)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().trait_hold;
		}

		@Override
		public final double calcPower(L2Character target)
		{
			return target.getTraitStat().trait_hold_power;
		}

		@Override
		public final boolean fullResist(L2Character target)
		{
			return target.getTraitStat().full_trait_hold;
		}
	},
	trait_paralyze(TraitGroup.trait_of_skill)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().trait_paralyze;
		}

		@Override
		public final double calcPower(L2Character target)
		{
			return target.getTraitStat().trait_paralyze_power;
		}

		@Override
		public final boolean fullResist(L2Character target)
		{
			return target.getTraitStat().full_trait_paralyze;
		}
	},
	trait_physical_blockade(TraitGroup.trait_of_skill)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().trait_physical_blockade;
		}

		@Override
		public final double calcPower(L2Character target)
		{
			return target.getTraitStat().trait_physical_blockade_power;
		}

		@Override
		public final boolean fullResist(L2Character target)
		{
			return target.getTraitStat().full_trait_physical_blockade;
		}
	},
	trait_poison(TraitGroup.trait_of_skill)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().trait_poison;
		}

		@Override
		public final double calcPower(L2Character target)
		{
			return target.getTraitStat().trait_poison_power;
		}

		@Override
		public final boolean fullResist(L2Character target)
		{
			return target.getTraitStat().full_trait_poison;
		}
	},
	trait_shock(TraitGroup.trait_of_skill)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().trait_shock;
		}

		@Override
		public final double calcPower(L2Character target)
		{
			return target.getTraitStat().trait_shock_power;
		}

		@Override
		public final boolean fullResist(L2Character target)
		{
			return target.getTraitStat().full_trait_shock;
		}
	},
	trait_sleep(TraitGroup.trait_of_skill)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().trait_sleep;
		}

		@Override
		public final double calcPower(L2Character target)
		{
			return target.getTraitStat().trait_sleep_power;
		}

		@Override
		public final boolean fullResist(L2Character target)
		{
			return target.getTraitStat().full_trait_sleep;
		}
	},
	trait_valakas(TraitGroup.trait_of_npc)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().trait_valakas;
		}

		@Override
		public final double calcPower(L2Character target)
		{
			return target.getTraitStat().trait_valakas_power;
		}

		@Override
		public final boolean fullResist(L2Character target)
		{
			return target.getTraitStat().full_trait_valakas;
		}
	},
	trait_death(TraitGroup.trait_of_npc)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().trait_death;
		}

		@Override
		public final double calcPower(L2Character target)
		{
			return target.getTraitStat().trait_death_power;
		}

		@Override
		public final boolean fullResist(L2Character target)
		{
			return target.getTraitStat().full_trait_death;
		}
	}, // Скиллы есть но бафа/дебафа на повышение/понижение стата нету.
	trait_etc(TraitGroup.trait_of_npc)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().trait_etc;
		}

		@Override
		public final double calcPower(L2Character target)
		{
			return target.getTraitStat().trait_etc_power;
		}

		@Override
		public final boolean fullResist(L2Character target)
		{
			return target.getTraitStat().full_trait_etc;
		}
	},	// Скиллы есть но бафа/дебафа на повышение/понижение стата нету.
	trait_gust(TraitGroup.trait_of_npc)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().trait_gust;
		}

		@Override
		public final double calcPower(L2Character target)
		{
			return target.getTraitStat().trait_gust_power;
		}

		@Override
		public final boolean fullResist(L2Character target)
		{
			return target.getTraitStat().full_trait_gust;
		}
	}, // Скиллы есть но бафа/дебафа на повышение/понижение стата нету.
	trait_sword(TraitGroup.trait_of_weapon)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().full_trait_sword ? 0 : target.getTraitStat().trait_sword == 1 ? 1 : (100+target.getTraitStat().trait_sword)/100;
		}
	},
	trait_blunt(TraitGroup.trait_of_weapon)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().full_trait_blunt ? 0 : target.getTraitStat().trait_blunt == 1 ? 1 : (100+target.getTraitStat().trait_blunt)/100;
		}
	},
	trait_dagger(TraitGroup.trait_of_weapon)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().full_trait_dagger ? 0 : target.getTraitStat().trait_dagger == 1 ? 1 : (100+target.getTraitStat().trait_dagger)/100;
		}
	},
	trait_bow(TraitGroup.trait_of_weapon)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().full_trait_bow ? 0 : target.getTraitStat().trait_bow == 1 ? 1 : (100+target.getTraitStat().trait_bow)/100;
		}
	},
	trait_pole(TraitGroup.trait_of_weapon)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().full_trait_pole ? 0 : target.getTraitStat().trait_pole == 1 ? 1 : (100+target.getTraitStat().trait_pole)/100;
		}
	},
	trait_fist(TraitGroup.trait_of_weapon)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().full_trait_fist ? 0 : target.getTraitStat().trait_fist == 1 ? 1 : (100+target.getTraitStat().trait_fist)/100;
		}
	},
	trait_dual(TraitGroup.trait_of_weapon)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().full_trait_dual ? 0 : target.getTraitStat().trait_dual == 1 ? 1 : (100+target.getTraitStat().trait_dual)/100;
		}
	},
	trait_dualfist(TraitGroup.trait_of_weapon)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().full_trait_dualfist ? 0 : target.getTraitStat().trait_dualfist == 1 ? 1 : (100+target.getTraitStat().trait_dualfist)/100;
		}
	},
	trait_rapier(TraitGroup.trait_of_weapon)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().full_trait_rapier ? 0 : target.getTraitStat().trait_rapier == 1 ? 1 : (100+target.getTraitStat().trait_rapier)/100;
		}
	},
	trait_crossbow(TraitGroup.trait_of_weapon)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().full_trait_crossbow ? 0 : target.getTraitStat().trait_crossbow == 1 ? 1 : (100+target.getTraitStat().trait_crossbow)/100;
		}
	},
	trait_ancientsword(TraitGroup.trait_of_weapon)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().full_trait_ancientsword ? 0 : target.getTraitStat().trait_ancientsword == 1 ? 1 : (100+target.getTraitStat().trait_ancientsword)/100;
		}
	},
	trait_dualdagger(TraitGroup.trait_of_weapon)
	{
		@Override
		public final double calcResist(L2Character target)
		{
			return target.getTraitStat().full_trait_dualdagger ? 0 : target.getTraitStat().trait_dualdagger == 1 ? 1 : (100+target.getTraitStat().trait_dualdagger)/100;
		}
	},
	trait_turn_stone(TraitGroup.trait_of_skill),
	trait_none(TraitGroup.trait_of_none);

	private TraitGroup trait_group;
	private SkillTrait(TraitGroup tg)
	{
		trait_group = tg;
	}

	public double calcResist(L2Character target)
	{
		return 0;
	}

	public double calcPower(L2Character target)
	{
		return 0;
	}

	public boolean fullResist(L2Character target)
	{
		return false;
	}

	public TraitGroup getGroup()
	{
		return trait_group;
	}

	public static enum TraitGroup
	{
		trait_of_none,
		trait_of_npc,
		trait_of_skill,
		trait_of_elemental,
		trait_of_weapon
	}
}