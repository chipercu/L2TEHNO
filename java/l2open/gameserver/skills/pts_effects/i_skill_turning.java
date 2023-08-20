package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.*;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * @author : Diagod
 **/
public class i_skill_turning extends L2Effect
{
	private int _type;
	public i_skill_turning(Env env, EffectTemplate template, Integer type, Integer chance)
	{
		super(env, template);
		_type = type;
		env.value = chance;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(getEffected().isCastingNow() && !getEffected().isRaid() && !getEffected().isEpicRaid() && getEffected().getCastingSkill().getMagic() == _type && Formulas.calcSkillSuccess(_env, getEffector().getChargedSpiritShot(), false))
			getEffected().abortCast(true);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
/*bool __cdecl CSkillEffect_i_skill_turning::Instant(struct CCreature *a1, struct CObject *a2, const struct CSkillInfo *a3, struct CSkillAction2 *a4, double a5)
{
  __m128i v5; // xmm6@0
  struct CSkillAction2 *v6; // rbp@1
  const struct CSkillInfo *v7; // rdi@1
  struct CCreature *v8; // r12@1
  struct CCreature *v9; // rsi@1
  __int64 v10; // rbx@1
  __int64 v11; // r10@1
  __int64 v12; // r8@1
  bool result; // al@2
  struct CCreature *v14; // rax@3
  int v15; // edi@3
  double v16; // xmm6_8@3
  __int64 v17; // rdx@4
  __int128 v18; // [sp+30h] [bp-48h]@1

  _mm_store_si128((__m128i *)&v18, v5);
  v6 = a4;
  v7 = a3;
  v8 = a2;
  v9 = a1;
  v10 = *(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex);
  v11 = *(_DWORD *)(v10 + 32024);
  v12 = dword_1E8EC90[v11 + 0x100000];
  dword_1E8EC90[v11 + 0x100000] = v12 + 1;
  qword_226F890[v12 + 1000 * v11] = (__int64)&off_C11360;
  if ( sub_758A24((__int64)v7) )
  {
    LODWORD(v14) = (*(int (__fastcall **)(const struct CSkillInfo *))(*(_QWORD *)v7 + 456i64))(v7);
    v15 = (signed int)v14;
    v16 = L2SkillFunc::CalculateProbability((double)*((signed int *)v9 + 5), v8, v14, v6);
    sub_94D9B8();
    if ( v16 <= v16 * Rnd.get(100) )
    {
      --dword_1E8EC90[*(_DWORD *)(v10 + 32024) + 0x100000];
      result = 0;
    }
    else
    {
      v17 = *((_DWORD *)v9 + 4);
      CCreature::CancelCastingSkill(v15);
      --dword_1E8EC90[*(_DWORD *)(v10 + 32024) + 0x100000];
      result = 1;
    }
  }
  else
  {
    --dword_1E8EC90[*(_DWORD *)(v10 + 32024) + 0x100000];
    result = 0;
  }
  return result;
}

double __cdecl L2SkillFunc::CalculateProbability(double a1, struct CCreature *a2, struct CCreature *a3, const struct CSkillInfo *a4)
{
  __m128i v4; // xmm6@0
  __m128i v5; // xmm7@0
  __m128i v6; // xmm8@0
  __m128i v7; // xmm9@0
  bool v8; // di@1
  struct CCreature *v9; // rbx@1
  struct CCreature *v10; // rsi@1
  __int64 v11; // rbp@1
  __int64 v12; // r10@1
  __int64 v13; // rcx@1
  double v14; // xmm8_8@4
  double v15; // xmm6_8@4
  double v16; // xmm7_8@4
  double v17; // xmm8_8@4
  double result; // xmm0_8@4
  __int128 v19; // [sp+30h] [bp-78h]@1
  __int128 v20; // [sp+40h] [bp-68h]@1
  __int128 v21; // [sp+50h] [bp-58h]@1
  __int128 v22; // [sp+60h] [bp-48h]@1

  _mm_store_si128((__m128i *)&v22, v4);
  _mm_store_si128((__m128i *)&v21, v5);
  _mm_store_si128((__m128i *)&v20, v6);
  _mm_store_si128((__m128i *)&v19, v7);
  v8 = (char)a4;
  v9 = a3;
  v10 = a2;
  v11 = *(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex);
  v12 = *(_DWORD *)(v11 + 32024);
  v13 = dword_1E8EC90[v12 + 0x100000];
  dword_1E8EC90[v12 + 0x100000] = v13 + 1;
  qword_226F890[v13 + 1000 * v12] = (__int64)&off_C056C0;
  if ( a2 && a3 && a4 )
  {
    v14 = (double)*((signed int *)a4 + 6);
    v15 = (double)(*(int (__fastcall **)(struct CCreature *))(*(_QWORD *)a3 + 208i64))(a3);
    v16 = (double)*(signed int *)(*((_QWORD *)v9 + 338) + 1916i64);
    v17 = (v14 + a1 - v15 + 30.0 - v16) * CCreature::GetSkillAttributeBonus(v10, v9, v8);
    --dword_1E8EC90[*(_DWORD *)(v11 + 32024) + 0x100000];
    result = v17;
  }
  else
  {
    --dword_1E8EC90[*(_DWORD *)(v11 + 32024) + 0x100000];
    result = 0.0;
  }
  return result;
}

double __cdecl CCreature::GetSkillAttributeBonus(struct CCreature *a1, const struct CSkillInfo *a2, bool a3)
{
  char v3; // r9@0
  double v4; // xmm0_8@0
  __m128i v5; // xmm6@0
  __int64 v6; // rbx@1
  const struct CSkillInfo *v7; // rdi@1
  struct CCreature *v8; // rsi@1
  __int64 v9; // rbp@1
  __int64 v10; // r10@1
  __int64 v11; // r8@1
  double v12; // xmm6_8@1
  __int128 v14; // [sp+30h] [bp-48h]@1

  _mm_store_si128((__m128i *)&v14, v5);
  v6 = a3;
  v7 = a2;
  v8 = a1;
  v9 = *(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex);
  v10 = *(_DWORD *)(v9 + 32024);
  v11 = dword_1E8EC90[v10 + 0x100000];
  dword_1E8EC90[v10 + 0x100000] = v11 + 1;
  qword_226F890[v11 + 1000 * v10] = (__int64)&off_AD2AA0;
  ((void (__usercall *)(__int64@<rdx>, __int64@<rcx>, int@<r8d>, char@<r9b>, __m128i *@<xmm6>))CCreature::GetGeneralTraitBonus)(
    (__int64)a2,
    (__int64)a1,
    *(_DWORD *)(v6 + 432),
    v3,
    (__m128i *)v5.m128i_i64[0]);
  v12 = v4 * CCreature::GetAttributeBonus(v8, v7);
  --dword_1E8EC90[*(_DWORD *)(v9 + 32024) + 0x100000];
  return v12;
}

double __cdecl CCreature::GetAttributeBonus(struct CObject *a1, const struct CSkillInfo *a2)
{
  int *v2; // r8@0
  __m128i v3; // xmm6@0
  __m128i v4; // xmm7@0
  int *v5; // rbx@1
  const struct CSkillInfo *v6; // rsi@1
  struct CObject *v7; // r12@1
  __int64 v8; // rbp@1
  __int64 v9; // r9@1
  __int64 v10; // r8@1
  int v11; // eax@3
  int v12; // edi@3
  int v13; // ebx@3
  int v14; // edx@3
  double v15; // xmm1_8@3
  double *v16; // xmm0_8@4
  double *v17; // xmm6_8@6
  double v18; // xmm6_8@10
  double result; // xmm0_8@14
  double v20; // [sp+30h] [bp-78h]@3
  double v21; // [sp+38h] [bp-70h]@3
  __int64 v22; // [sp+40h] [bp-68h]@1
  __int128 v23; // [sp+50h] [bp-58h]@1
  __int128 v24; // [sp+60h] [bp-48h]@1
  double *v25; // [sp+B8h] [bp+10h]@3
  double v26; // [sp+C8h] [bp+20h]@3

  v22 = -2i64;
  _mm_store_si128((__m128i *)&v24, v3);
  _mm_store_si128((__m128i *)&v23, v4);
  v5 = v2;
  v6 = a2;
  v7 = a1;
  v8 = *(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex);
  v9 = *(_DWORD *)(v8 + 32024);
  v10 = dword_1E8EC90[v9 + 0x100000];
  dword_1E8EC90[v9 + 0x100000] = v10 + 1;
  qword_226F890[v10 + 1000 * v9] = (__int64)&off_AD25F0;
  if ( a2 && (unsigned __int8)(*(int (__fastcall **)(const struct CSkillInfo *))(*(_QWORD *)a2 + 320i64))(a2) )
  {
    LODWORD(v25) = -2;
    v20 = 0.0;
    v21 = 0.0;
    CCreature::GetAttackTypeValue(v7, v5, (int *)&v25);
    v11 = (*(int (__fastcall **)(const struct CSkillInfo *))(*(_QWORD *)v6 + 456i64))(v6);
    CCreature::GetDefendValue(v7, v11, (int *)(unsigned int)v25);
    v12 = (signed int)v25;
    v13 = LODWORD(v26);
    CCreature::GetConstValue((int)v7, SLODWORD(v26), (double *)(unsigned int)v25, &v20);
    CCreature::GetBonusBoundary((int)v7, (double *)(unsigned int)(v14 - v12), &v26);
    v15 = (double)((v13 + 100) * (v13 + 100)) / 144.0 * v20 - (double)((v12 + 100) * (v12 + 100)) / 169.0 * v21;
    if ( *(double *)&v25 <= v15 )
      *(double *)&v16 = v15;
    else
      v16 = v25;
    *(double *)&v17 = v26;
    if ( *(double *)&v16 <= v26 )
    {
      if ( *(double *)&v25 <= v15 )
        *(double *)&v17 = v15;
      else
        v17 = v25;
    }
    v18 = *(double *)&v17 / 100.0 + 1.0;
    if ( (unsigned __int8)(*(int (__fastcall **)(struct CObject *))(*(_QWORD *)v7 + 328i64))(v7)
      && (unsigned __int8)(*(int (__fastcall **)(const struct CSkillInfo *))(*(_QWORD *)v6 + 328i64))(v6)
      && v18 <= 1.0 )
    {
      v18 = 1.0;
    }
    --dword_1E8EC90[*(_DWORD *)(v8 + 32024) + 0x100000];
    result = v18;
  }
  else
  {
    --dword_1E8EC90[*(_DWORD *)(v8 + 32024) + 0x100000];
    result = 1.0;
  }
  return result;
}
*/