package l2open.gameserver.skills.pts_effects;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {c_mp_by_level;-63;5}
 * @c_mp_by_level
 * @-63 - Количество МП на Тик...
 * @5 - Время тика(666мс 1 тик)
 **/
/**
 * @author : Diagod
 * @offlike
 **/
public class c_mp_by_level extends L2Effect
{
	private double _hp_tick;

	public c_mp_by_level(Env env, EffectTemplate template, Double hp_tick, Integer tick_time)
	{
		super(env, template);

		_hp_tick = hp_tick;
		_tick_time = tick_time;
		isDot = true;
	}

	@Override
	public boolean onActionTime()
	{
		if(_effected.isDead() && (!_effected.isBlessedByNoblesse() && !_effected._blessed || getSkill().isToggle()))
		{
			_effected.getEffectList().stopEffect(getSkill().getId());
			return false;
		}

		double damage = _hp_tick*ConfigValue.DotModifer*(_effected.getLevel() * 0.0027 * _effected.getLevel() - _effected.getLevel() * 0.00002 * _effected.getLevel() * _effected.getLevel() + _effected.getLevel() * 0.0203 + 1.2171);
		if(!getSkill().isOffensive())
			if(getSkill().isMagic())
				damage = _effected.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, damage, null, getSkill());
			else
				damage = _effected.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, damage, null, getSkill());

		if(damage*-1 > _effected.getCurrentMp() && getSkill().isToggle())
		{
			_effected.sendPacket(new SystemMessage(SystemMessage.YOUR_SKILL_WAS_REMOVED_DUE_TO_A_LACK_OF_MP));
			_effected.getEffectList().stopEffect(getSkill().getId());
			return false;
		}

		_effected.setCurrentMp(_effected.getCurrentMp() + damage);
		return true;
	}
}
/**
bool __cdecl CSkillEffect_c_mp_by_level::Consume(struct CObject *a1, double a2)
{
  __int64 v2; // rdx@0
  double v3; // xmm2_8@0
  __m128i v4; // xmm6@0
  __int64 v5; // rbx@1
  struct CObject *v6; // rsi@1
  __int64 v7; // rdi@1
  __int64 v8; // r9@1
  __int64 v9; // r8@1
  bool result; // al@2
  double v11; // xmm1_8@3
  char v12; // si@3
  __int64 v13; // rax@5
  __int128 v14; // [sp+30h] [bp-38h]@1

  _mm_store_si128((__m128i *)&v14, v4);
  v5 = v2;
  v6 = a1;
  v7 = *(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex);
  v8 = *(_DWORD *)(v7 + 32024);
  v9 = dword_1E8EC90[v8 + 0x100000];
  dword_1E8EC90[v8 + 0x100000] = v9 + 1;
  qword_226F890[v9 + 1000 * v8] = (__int64)&off_C1DA40;
  if ( sub_758A5C(v2) )
  {
    v11 = (double)*(signed int *)(*(_QWORD *)(v5 + 2704) + 1956i64); // level
    v12 = sub_533818( v5, -0.0 - (v11 * 0.0027 * v11 - v11 * 0.00002 * v11 * v11 + v11 * 0.0203 + 1.2171) * *((double *)v6 + 2)(tick_value) * v3(tick_count));
	27-20+2,03+1,2171=10,2471
	299,4003-738,52074+6,7599+1,2171=-431,14344
	
	19,5075-12,2825+1,7255+1,2171=10,1676
	
	413
    if ( !v12 )
    {
      if ( (unsigned __int8)(*(int (__fastcall **)(__int64))(*(_QWORD *)v5 + 328i64))(v5) )
      {
        LODWORD(v13) = (*(int (__fastcall **)(__int64))(*(_QWORD *)v5 + 480i64))(v5);
        (*(void (__fastcall **)(__int64, signed __int64))(*(_QWORD *)v13 + 2040i64))(v13, 140i64);
      }
    }
    --dword_1E8EC90[*(_DWORD *)(v7 + 32024) + 0x100000];
    result = v12;
  }
  else
  {
    --dword_1E8EC90[*(_DWORD *)(v7 + 32024) + 0x100000];
    result = 0;
  }
  return result;
}

char __fastcall sub_533818(__int64 a1, double a2)
{
  __int64 v2; // rax@1
  __int64 v3; // rbx@1
  char v4; // di@1
  __int64 v5; // rax@4
  double v6; // xmm2_8@4
  double v7; // xmm1_8@4

  v2 = *(_QWORD *)(a1 + 2704);
  v3 = a1;
  v4 = 1;
  if ( *(double *)(v2 + 472) < a2 )
    v4 = 0;
  else
    *(double *)(v2 + 472) = *(double *)(v2 + 472) - a2;
  v5 = *(_QWORD *)(a1 + 2704);
  v6 = *(double *)(v5 + 472);
  v7 = *(double *)(v5 + 2016);
  if ( v6 >= 0.0 )
  {
    if ( v6 > v7 )
      *(double *)(v5 + 472) = v7;
  }
  else
  {
    *(_QWORD *)(v5 + 472) = 0i64;
  }
  if ( (unsigned __int8)(*(int (**)(void))(*(_QWORD *)a1 + 328i64))() )
    (*(void (__fastcall **)(__int64))(*(_QWORD *)v3 + 2896i64))(v3);
  return v4;
}
**/