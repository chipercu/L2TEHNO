package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.util.Rnd;

/**
 * {i_dispel_by_slot_probability;ma_up;100}
 * @i_dispel_by_slot_probability
 * @ma_up - abnormal_type с которым будут сняты бафы/дебафы.
 * @100 - шанс снятия эффекта, хз как он расчитывается и влияет ли на него что-то)))
 **/
/**
 * Проверить на ПТСке как работает просчет шанса у этого эффекта...
 * 1. Берем скил 341 - он дает нам 100% резиста от кенцела.
 * 2. Бафаем разные бафы.
 * 3. Берем несколько скилов с этим эффектов, даем шанс в 100%, смотрим на реакцию.
 * 4. Потом даем шанс около 40%, думаю если на него влияет формула канцела, то оно должно урезать в гавно шанс...
 * 5. Еще чето затестить)))
 **/
/**
 * @author : Diagod
 **/
public class i_dispel_by_slot_probability extends L2Effect
{
	private SkillAbnormalType _sat;
	private int _chance;

	public i_dispel_by_slot_probability(Env env, EffectTemplate template, SkillAbnormalType sat, Integer chance)
	{
		super(env, template);
		_sat = sat;
		_chance = chance;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		for(L2Skill skill : getEffected().getEffectList().getAllSkills(false))
		{
			if(skill.getAbnormalType() == _sat)
			{
				double prelimChance, eml, dml, cancel_res_multiplier = getEffected().calcStat(Stats.CANCEL_RECEPTIVE, 0, null, null);
				long buffTime;

				eml = skill.getMagicLevel();
				dml = getSkill().getMagicLevel() - (eml == 0 ? getEffected().getLevel() : eml);

				L2Effect ef = getEffected().getEffectList().getEffectBySkillId(skill.getId());
				if(ef == null)
					continue;
				buffTime = ef.getTimeLeft();
					cancel_res_multiplier = 1 - (cancel_res_multiplier * .01);
				prelimChance = (2. * dml + _chance + buffTime / 120000) * cancel_res_multiplier;

				if(prelimChance < 40)
					prelimChance = 40;
				else if(prelimChance > 90)
					prelimChance = 90;

				if(_chance == 100 || Rnd.chance(prelimChance))
				{
					getEffected().getEffectList().stopEffect(skill.getId(), false, false);
					update_effect_list = true;
				}
			}

			//if(skill.getAbnormalType() == _sat && Rnd.chance(_chance))
			//	getEffected().getEffectList().stopEffect(skill.getId(), false, false);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}