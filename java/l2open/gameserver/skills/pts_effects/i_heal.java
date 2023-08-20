package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncPTS;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.serverpackets.SystemMessage;

/**
 * {i_heal;71}
 * @i_heal
 **/
/**
 * @author : Diagod
 **/
public class i_heal extends L2Effect
{
	double _hp;
	public i_heal(Env env, EffectTemplate template, Double hp)
	{
		super(env, template);
		_instantly = true;
		_hp = hp;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isDead() || _effected.isHealBlocked(true, false) || _effected.block_hp.get())
			return;
		double newHp = _hp * _effected.calcStat(Stats.HEAL_EFFECTIVNESS, 100, _effector, getSkill()) / 100;
		newHp = _effector.calcStat(Stats.HEAL_POWER, newHp, _effected, getSkill());
		double addToHp = Math.max(0, newHp);
		if(addToHp > 0)
			addToHp = _effected.setCurrentHp(addToHp + _effected.getCurrentHp(), false);
		_effected.sendPacket(new SystemMessage(SystemMessage.S1_HPS_HAVE_BEEN_RESTORED).addNumber(Math.round(addToHp)));

	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
/**
bool __cdecl CSkillEffect_i_heal::Instant(struct CCreature *a1, struct CObject *a2, const struct CSkillInfo *a3, struct CSkillAction2 *a4, double a5)
{
  __m128i v5; // xmm6@0
  __m128i v6; // xmm7@0
  __m128i v7; // xmm8@0
  __m128i v8; // xmm9@0
  __m128i v9; // xmm10@0
  __m128i v10; // xmm11@0
  __m128i v11; // xmm12@0
  struct CSkillAction2 *v12; // rbp@1
  const struct CSkillInfo *v13; // rsi@1
  struct CObject *v14; // rbx@1
  struct CCreature *v15; // r12@1
  __int64 v16; // rdi@1
  __int64 v17; // r10@1
  __int64 v18; // r8@1
  double v19; // xmm10_8@1
  bool result; // al@2
  __int64 v21; // rax@5
  double v22; // xmm6_8@5
  double v23; // xmm5_8@7
  double v24; // xmm8_8@7
  double v25; // xmm11_8@7
  double v26; // xmm12_8@7
  double v27; // xmm1_8@7
  double v28; // xmm2_8@8
  double v29; // xmm8_8@13
  double v30; // xmm0_8@15
  __m128i *v31; // xmm6_8@15
  double v32; // xmm0_8@15
  __int128 v33; // [sp+30h] [bp-A8h]@1
  __int128 v34; // [sp+40h] [bp-98h]@1
  __int128 v35; // [sp+50h] [bp-88h]@1
  __int128 v36; // [sp+60h] [bp-78h]@1
  __int128 v37; // [sp+70h] [bp-68h]@1
  __int128 v38; // [sp+80h] [bp-58h]@1
  __int128 v39; // [sp+90h] [bp-48h]@1

  _mm_store_si128((__m128i *)&v39, v5);
  _mm_store_si128((__m128i *)&v38, v6);
  _mm_store_si128((__m128i *)&v37, v7);
  _mm_store_si128((__m128i *)&v36, v8);
  _mm_store_si128((__m128i *)&v35, v9);
  _mm_store_si128((__m128i *)&v34, v10);
  _mm_store_si128((__m128i *)&v33, v11);
  v12 = a4;
  v13 = a3;
  v14 = a2;
  v15 = a1;
  v16 = *(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex);
  v17 = *(_DWORD *)(v16 + 32024);
  v18 = dword_1E8EC90[v17 + 0x100000];
  dword_1E8EC90[v17 + 0x100000] = v18 + 1;
  qword_226F890[v18 + 1000 * v17] = (__int64)&off_C0AC00;
  v19 = 0.0;
  if ( sub_758A24((__int64)v13) )
  {
    if ( *((_BYTE *)v13 + 3904) )
    {
      sub_97E9D4(*((_QWORD *)v14 + 340), (__int64)&off_C051D8, 4132);
      v21 = *((_QWORD *)v14 + 338);
      v22 = *(double *)(v21 + 2184);
      if ( *(_BYTE *)(v21 + 1736) )
        v19 = v22 * *(double *)(v21 + 1744);
      sub_97EA74(*((_QWORD *)v14 + 340), 0i64, 0);
      v23 = (double)*((signed int *)v12 + 6);
      v24 = v23 * 0.0116 * v23 + v23 * 0.6371 + 6.4512;
      v25 = *((double *)v14 + 464);
      v26 = *((double *)v13 + 463);
      v27 = v23 * 0.007 * v23 * v23 - v23 * 0.00004 * v23 * v23 * v23 - v23 * 0.1923 * v23 + v23 * 3.6064 - 1.0966 - v22;
      if ( v27 <= 0.0 )
        v28 = 0.0;
      else
        v28 = v27;
      if ( v24 - v28 / 5.0 <= 0.0 )
      {
        v29 = 0.0;
      }
      else
      {
        if ( v27 <= 0.0 )
          v27 = 0.0;
        v29 = v24 - v27 / 5.0;
      }
      v30 = sqrt(v22 + v19);
      *(double *)&v31 = (v30 + *((double *)v15 + 2) + v25 + v29 * *((double *)v14 + 623)) * v26;
      sub_94D9B8();
      v32 = v30 * Rnd.get(100);
      if ( v32 <= 5.0 )
        *(double *)&v31 = *(double *)&v31 * 3.0;
      ((void (__usercall *)(__int64@<rcx>, char@<r8b>, double@<xmm1>, __m128i *@<xmm6>, __m128i *@<xmm7>))sub_533628)(
        (__int64)v13,
        0,
        *(double *)&v31,
        v31,
        0i64);
      L2SkillFunc::SendHpMpChangedSystemMessage(v14, v13, (signed int)floor(v32), 1066);
      --dword_1E8EC90[*(_DWORD *)(v16 + 32024) + 0x100000];
      result = 1;
    }
    else
    {
      --dword_1E8EC90[*(_DWORD *)(v16 + 32024) + 0x100000];
      result = 0;
    }
  }
  else
  {
    --dword_1E8EC90[*(_DWORD *)(v16 + 32024) + 0x100000];
    result = 0;
  }
  return result;
}
**/