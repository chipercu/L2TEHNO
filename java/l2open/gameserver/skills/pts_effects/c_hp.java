package l2open.gameserver.skills.pts_effects;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {c_hp;-63;5}
 * @c_hp
 * @-63 - Количество ХП на Тик...
 * @5 - Время тика(666мс 1 тик)
 **/
/**
 * @author : Diagod
 **/
public class c_hp extends L2Effect
{
	private double _hp_tick;

	public c_hp(Env env, EffectTemplate template, Double hp_tick, Integer tick_time)
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

		double damage = -ConfigValue.DotModifer*_tick_time*_hp_tick;

		if(damage > _effected.getCurrentHp() && getSkill().isToggle())
		{
			_effected.sendPacket(new SystemMessage(SystemMessage.YOUR_SKILL_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_HP));
			_effected.getEffectList().stopEffect(getSkill().getId());
			return false;
		}

		_effected.reduceCurrentHp(damage, _effector, getSkill(), !_effected.isNpc() && _effected != _effector, _effected != _effector, _effector.isNpc() || getSkill().isToggle() || _effected == _effector, false, true, damage, false, false, false, false);
		return true;
	}
}
/**
bool __cdecl CSkillEffect_c_hp::Consume(struct CObject *a1, double a2)
{
  __int64 v2; // rdx@0
  double v3; // xmm2_8@0
  __m128i v4; // xmm6@0
  __m128i *v5; // xmm7_8@0
  __int64 v6; // rbx@1
  struct CObject *v7; // rdi@1
  __int64 v8; // rsi@1
  __int64 v9; // r9@1
  __int64 v10; // r8@1
  bool result; // al@2
  char v12; // di@3
  __int64 v13; // rax@5
  __int128 v14; // [sp+30h] [bp-38h]@1

  _mm_store_si128((__m128i *)&v14, v4);
  v6 = v2;
  v7 = a1;
  v8 = *(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex);
  v9 = *(_DWORD *)(v8 + 32024);
  v10 = dword_1E8EC90[v9 + 0x100000];
  dword_1E8EC90[v9 + 0x100000] = v10 + 1;
  qword_226F890[v10 + 1000 * v9] = (__int64)&off_C1D7E0;
  if ( sub_758A5C(v2) )
  {
    v12 = sub_52C620(v6, -0.0 - v3 * *((double *)v7 + 2), COERCE__M128I__(v3 * *((double *)v7 + 2)), v5);
    if ( !v12 )
    {
      if ( (unsigned __int8)(*(int (__fastcall **)(__int64))(*(_QWORD *)v6 + 328i64))(v6) )
      {
        LODWORD(v13) = (*(int (__fastcall **)(__int64))(*(_QWORD *)v6 + 480i64))(v6);
        (*(void (__fastcall **)(__int64, signed __int64))(*(_QWORD *)v13 + 2040i64))(v13, 610i64);
      }
    }
    --dword_1E8EC90[*(_DWORD *)(v8 + 32024) + 0x100000];
    result = v12;
  }
  else
  {
    --dword_1E8EC90[*(_DWORD *)(v8 + 32024) + 0x100000];
    result = 0;
  }
  return result;
}
**/