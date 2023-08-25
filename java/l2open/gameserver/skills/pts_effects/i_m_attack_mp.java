package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.util.Rnd;

/**
 * {i_m_attack_mp;117;1;1600}
 * @i_m_attack_mp
 * @117 - Повер скила.
 * @1 - теоретически, значение рандомного дамага...Если следующее значение стоит большое и здесь 1 то дамаг оч малый, если здесь 2 то дамаг выше...
 * @1600 - чем выше значение, тем ниже дамаг...хз че оно такое...
 **/
/**
 * @author : Diagod
 **/
public class i_m_attack_mp extends L2Effect
{
	private int _power;
	private int _unk1;
	private int _unk2;

	public i_m_attack_mp(Env env, EffectTemplate template, Integer power, Integer unk1, Integer unk2)
	{
		super(env, template);

		_power = power;
		_unk1 = unk1;
		_unk2 = unk2;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isDead())
			return;
		_effected.reduceCurrentMp(calcManaDam(_effector, _effected, getSkill(), getSkill().isSSPossible() ? _effector.getChargedSpiritShot() : 0, _power), _effector);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}

	public double calcManaDam(L2Character attacker, L2Character target, L2Skill skill, int sps, double power) 
	{
		boolean isPvP = attacker.isPlayable() && target.isPlayable();
        double mAtk = attacker.getMAtk(target, skill);

        if(sps == 2)
            mAtk *= 2;
        else if(sps == 1)
            mAtk *= 1.5;

        double mdef = target.getMDef(null, skill);

        if(mdef < 1)
            mdef = 1;

		// формула выглядит прмиерно так, без учета не понятных параметров...А вообще оно в корне не верно))) Но так дает примерно верный урон)))
		double damage = Math.sqrt(mAtk) * power * target.getMaxMp()/57/mdef;

        damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 3 - attacker.getRandomDamage()) / 100;

		boolean crit = Formulas.calcMCrit(attacker.getMagicCriticalRate(target, skill) * (attacker.isPlayer() ? attacker.getPlayer().getTemplate().m_atk_crit_chance_mod : 1));

        if(crit)
            damage *= attacker.calcStat(Stats.MCRITICAL_DAMAGE, attacker.isPlayable() && target.isPlayable() ? 2.5 : 3., target, skill);

        damage = attacker.calcStat(Stats.MAGIC_DAMAGE, damage, target, skill);

        if(target.isMonster())
		{
			damage = attacker.calcStat(Stats.PVE_MAGICAL_DMG, damage, target, skill);
			if(target.isRaid() || target.isBoss() || target.isEpicRaid() || target.isRefRaid())
				damage = attacker.calcStat(Stats.PVR_MAGICAL_DMG, damage, target, skill);
		}

		int levelDiff = target.getLevel() - skill.getMagicLevel();	
		double magic_rcpt = target.calcStat(Stats.p_resist_dd_magic, 1, attacker, skill) - attacker.calcStat(Stats.MAGIC_POWER, target, skill);
		double failChance = 4. * Math.max(1, levelDiff) * (1 + magic_rcpt / 100);
		//_log.info("failChance: "+failChance);
		
		// при маджик лвл 54, цель 85...Атака в основном срывается, но где-то каждая 5-я идет на 50%
		if(Rnd.chance(failChance))
		{
			SystemMessage msg;
			if (levelDiff > 9 && Rnd.chance(90) || (attacker.calcStat(Stats.MAGIC_POWER, target, skill) <= -900) && Rnd.chance(50))
			{
				damage = 1.0;
				msg = new SystemMessage(SystemMessage.C1_RESISTED_C2S_MAGIC).addName(target).addName(attacker);
				attacker.sendPacket(msg);
				target.sendPacket(msg);
			}
			else
			{
				damage /= 2.0;
				msg = new SystemMessage(SystemMessage.DAMAGE_IS_DECREASED_BECAUSE_C1_RESISTED_AGAINST_C2S_MAGIC).addName(target).addName(attacker);
				attacker.sendPacket(msg);
				target.sendPacket(msg);
			}
		}

		damage = Math.max(1, damage / 2.);

		if(isPvP && damage > 1.0)
		{
			damage *= attacker.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1.0);
			damage /= target.calcStat(Stats.p_pvp_magical_skill_defence_bonus, 1.0);
		}

		if(crit && attacker.isPlayer()) 
			attacker.sendPacket(new SystemMessage(SystemMessage.MAGIC_CRITICAL_HIT).addName(attacker));
		// хз, должно ли это чудо сбивать каст, что-то мне подсказывает, что нет)
		/*if(calcCastBreak(target, crit))
			target.abortCast(false);*/
        return damage;
    }
}
/**
bool __cdecl CSkillEffect_i_m_attack_mp::Instant(struct CCreature *a1, struct CObject *a2, const struct CSkillInfo *a3, struct CSkillAction2 *a4, double a5)
{
  __int64 v5; // xmm0_8@0
  __m128i v6; // xmm6@0
  __m128i *v7; // xmm7_8@0
  __m128i *v8; // xmm8_8@0
  __m128i *v9; // xmm9_8@0
  struct CSkillAction2 *v10; // r12@1
  struct CObject *v11; // rbx@1
  struct CCreature *v12; // rsi@1
  struct CCreature *v13; // rdi@1
  __int64 v14; // rbp@1
  __int64 v15; // r10@1
  __int64 v16; // r8@1
  bool result; // al@2
  __int64 v18; // rax@3
  double v19; // xmm0_8@5
  __m128i *v20; // xmm6_8@5
  __int64 v21; // rax@7
  __int64 v22; // rdi@8
  __int64 v23; // rax@8
  double v24; // xmm1_8@8
  __int64 v25; // rax@12
  char v26; // bl@13
  bool v27; // r12@16
  __int64 v28; // rax@22
  __int64 v29; // rax@22
  char v30; // [sp+58h] [bp-A0h]@5
  double v31; // [sp+60h] [bp-98h]@5
  __int64 v32; // [sp+78h] [bp-80h]@5
  double v33; // [sp+88h] [bp-70h]@5
  double v34; // [sp+90h] [bp-68h]@5
  __int128 v35; // [sp+B0h] [bp-48h]@1
  double v36; // [sp+128h] [bp+30h]@0

  _mm_store_si128((__m128i *)&v35, v6);
  v10 = a4;
  v11 = a3;
  v12 = a2;
  v13 = a1;
  v14 = *(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex);
  v15 = *(_DWORD *)(v14 + 32024);
  v16 = dword_1E8EC90[v15 + 0x100000];
  dword_1E8EC90[v15 + 0x100000] = v16 + 1;
  qword_226F890[v16 + 1000 * v15] = (__int64)&off_C24A30;
  if ( sub_758A24((__int64)v11) )
  {
    LODWORD(v18) = (*(int (__fastcall **)(struct CObject *))(*(_QWORD *)v11 + 456i64))(v11);
    if ( *(_BYTE *)(v18 + 3905) )
    {
      L2SkillFunc::GetMagicAttackerInfo((__int64)&v30, (__int64)v12);
      L2SkillFunc::GetMagicTargetInfo(v12, (__int64)&v32, (__int64)v11, (bool)v10, v5);
      v19 = sqrt(v31);
      *(double *)&v20 = v19 * *((double *)v13 + 2) * 15.0 / v33 * v34;
      if ( *((_DWORD *)v13 + 6) == 1 && *((_DWORD *)v13 + 7) > 0 )
      {
        LODWORD(v21) = (*(int (__fastcall **)(struct CObject *))(*(_QWORD *)v11 + 456i64))(v11);
        *(double *)&v20 = *(double *)&v20
                        * (*(double *)(*(_QWORD *)(v21 + 2704) + 2016i64)
                         / (double)*((signed int *)v13 + 7));
      }
      sub_84A92C(
        v11,
        v12,
        v10,
        (__int64)&v30,
        v20,
        v7,
        v8,
        v9,
        (struct L2SkillFunc::MagicTargetInfo *)&v32,
        *((double *)v13 + 2),
        v36,
        (char)v20,
        1);
      v22 = v32;
      v23 = *(_QWORD *)(v32 + 2704);
      v24 = *(double *)(v23 + 472);
      if ( v24 <= v36 )
        *(_QWORD *)(v23 + 472) = 0i64;
      else
        *(double *)(v23 + 472) = v24 - v36;
      if ( (unsigned __int8)(*(int (__fastcall **)(__int64))(*(_QWORD *)v22 + 328i64))(v22) )
      {
        LODWORD(v25) = (*(int (__fastcall **)(__int64))(*(_QWORD *)v22 + 480i64))(v22);
        (*(void (__fastcall **)(__int64))(*(_QWORD *)v25 + 2896i64))(v25);
      }
      v26 = 0;
      sub_97E9D4(v22 + 5400, (__int64)&off_C051D8, 9246);
      if ( *(_BYTE *)(v22 + 3868) )
      {
        v27 = 1;
      }
      else
      {
        if ( CCreature::_DeleteAbnormalStatus2(126, v22, 0i64, 0i64, v36, 1, 9u) ) // ab_sleep
          v26 = 1;
        v27 = 1;
        if ( CCreature::_DeleteAbnormalStatus2(183, v22, 0i64, 0i64, v36, 1, 9u) ) // ab_force_meditation
          v26 = 1;
      }
      sub_97EA74(v22 + 5400, &off_C051D8, 9258);
      if ( v26 )
      {
        sub_97E9D4(*(_QWORD *)(v22 + 2720), (__int64)&off_C051D8, 9262);
        CCreature::ValidateAllOnSIM();
        sub_97EA74(*(_QWORD *)(v22 + 2720), 0i64, 0);
        if ( (unsigned __int8)(*(int (__fastcall **)(__int64))(*(_QWORD *)v22 + 328i64))(v22) )
        {
          LODWORD(v28) = (*(int (__fastcall **)(__int64))(*(_QWORD *)v22 + 480i64))(v22);
          (*(void (__fastcall **)(__int64))(*(_QWORD *)v28 + 2904i64))(v28);
          LODWORD(v29) = (*(int (__fastcall **)(__int64))(*(_QWORD *)v22 + 480i64))(v22);
          (*(void (__fastcall **)(__int64))(*(_QWORD *)v29 + 1248i64))(v29);
        }
        CCreature::CheckAbnormalVisualEffect();
      }
      --dword_1E8EC90[*(_DWORD *)(v14 + 32024) + 0x100000];
      result = v27;
    }
    else
    {
      --dword_1E8EC90[*(_DWORD *)(v14 + 32024) + 0x100000];
      result = 0;
    }
  }
  else
  {
    --dword_1E8EC90[*(_DWORD *)(v14 + 32024) + 0x100000];
    result = 0;
  }
  return result;
}
**/