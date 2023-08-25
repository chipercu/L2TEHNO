package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.*;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * @author : Diagod
 **/
public class i_mp_by_level extends L2Effect
{
	public i_mp_by_level(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
/**
bool __cdecl CSkillEffect_i_mp_by_level::Instant(struct CCreature *a1, struct CObject *a2, const struct CSkillInfo *a3, struct CSkillAction2 *a4, double a5)
{
  struct CSkillAction2 *v5; // r12@1
  const struct CSkillInfo *v6; // rdi@1
  struct CCreature *v7; // r13@1
  struct CCreature *v8; // rsi@1
  __int64 v9; // rbp@1
  __int64 v10; // r10@1
  __int64 v11; // r8@1
  int v12; // ebx@1
  bool result; // al@2
  __int64 v14; // rax@3
  struct CCreature *v15; // rdi@3
  int v16; // edx@5
  signed int v17; // ecx@5
  double v18; // xmm0_8@6
  int v19; // edx@8
  int v20; // ecx@8

  v5 = a4;
  v6 = a3;
  v7 = a2;
  v8 = a1;
  v9 = *(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex);
  v10 = *(_DWORD *)(v9 + 32024);
  v11 = dword_1E8EC90[v10 + 0x100000];
  dword_1E8EC90[v10 + 0x100000] = v11 + 1;
  qword_226F890[v11 + 1000 * v10] = (__int64)&off_C0C2F0;
  v12 = 0;
  if ( sub_758A24((__int64)v6) )
  {
    LODWORD(v14) = (*(int (__fastcall **)(const struct CSkillInfo *))(*(_QWORD *)v6 + 456i64))(v6);
    v15 = (struct CCreature *)v14;
    if ( *(_BYTE *)(v14 + 3905) )
    {
      v16 = *(_DWORD *)(v14 + 4392);
      v17 = *((_DWORD *)v8 + 4);
      if ( (double)v17 * 1.7 <= (double)(v17 + v16) )
        v18 = (double)*((signed int *)v8 + 4) * 1.7;
      else
        v18 = (double)(v16 + v17);
      v19 = *(_DWORD *)(*(_QWORD *)(v14 + 2704) + 1956i64) - *((_DWORD *)v5 + 6) - 5;
      v20 = v19;
      if ( v19 < 0 )
        v20 = 0;
      if ( 10 * (10 - v20) >= 0 )
      {
        if ( v19 < 0 )
          v19 = 0;
        v12 = 10 * (10 - v19);
      }
	  
	  28 - m_lvl
	  49 - m_pwr
	  40 - p_lvl
	  12 - p_deff
	  14 - r_heal
	  
      sub_533734(v14, (double)v12 * v18 / 100.0);
      L2SkillFunc::SendHpMpChangedSystemMessage(v7, v15, (signed int)floor(v18), 1068);
      --dword_1E8EC90[*(_DWORD *)(v9 + 32024) + 0x100000];
      result = 1;
    }
    else
    {
      --dword_1E8EC90[*(_DWORD *)(v9 + 32024) + 0x100000];
      result = 0;
    }
  }
  else
  {
    --dword_1E8EC90[*(_DWORD *)(v9 + 32024) + 0x100000];
    result = 0;
  }
  return result;
}
**/