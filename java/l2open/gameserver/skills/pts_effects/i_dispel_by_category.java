package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.util.Rnd;

import java.util.*;

/**
 * {i_dispel_by_category;slot_buff;25;5}
 * @i_dispel_by_category
 * @slot_buff(slot_debuff) - тип бафы/дебафы для снятия.
 * @25 - шанс снятия эффекта, хз как он расчитывается и влияет ли на него что-то)))
 * @5 - макс количество.
 **/
/**
 * Проверитьна ПТСке как работает просчет шанса у этого эффекта...
 * 1. Берем скил 341 - он дает нам 100% резиста от кенцела.
 * 2. Бафаем разные бафы.
 * 3. Берем несколько скилов с этим эффектов, даем шанс в 100%, смотрим на реакцию.
 * 4. Потом даем шанс около 40%, думаю если на него влияет формула канцела, то оно должно урезать в гавно шанс...
 * 5. Еще чето затестить)))
 **/
/**
 * @author : Diagod
 **/
public class i_dispel_by_category extends L2Effect
{
	private Category _cat;
	private int _chance;
	private int _count;

	public i_dispel_by_category(Env env, EffectTemplate template, Category cat, Integer chance, Integer count)
	{
		super(env, template);
		_cat = cat;
		_chance = chance;
		_count = count;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		List<L2Skill> _musicList = new ArrayList<L2Skill>();
		List<L2Skill> _buffList = new ArrayList<L2Skill>();

		for(L2Effect e : _effected.getEffectList().getAllEffects())
		{
			switch(_cat)
			{
				case slot_buff:
					if(!e.isOffensive() && e.getAbnormalType() != SkillAbnormalType.vote && e.getSkill().isCancelable() && !e.getSkill().isPassive() && e.getEffectType() != EffectType.Vitality)
					{
						if(e.getSkill().isMusic())
						{
							if(!_musicList.contains(e.getSkill()))
								_musicList.add(e.getSkill());
						}
						else if(!_buffList.contains(e.getSkill()))
							_buffList.add(e.getSkill());
					}
					break;
				case slot_debuff:
					if(e.isOffensive() && e.getSkill().isCancelable() && getSkill().getId() != 2530 && e.getSkill().getId() != 5660 && e.getSkill().getId() != 4515 && !_buffList.contains(e.getSkill()))
						_buffList.add(e.getSkill());
					break;
			}
		}
		List<L2Skill> _effectList = new ArrayList<L2Skill>();
		Collections.reverse(_musicList);
		Collections.reverse(_buffList);
		_effectList.addAll(_musicList);
		_effectList.addAll(_buffList);

		if(_effectList.isEmpty())
			return;
		double prelimChance, eml, dml, cancel_res_multiplier = _effected.calcStat(Stats.CANCEL_RECEPTIVE, 0, null, null); // constant resistance is applied for whole cycle of cancellation
		long buffTime, negated = 0;

		for(L2Skill skill : _effectList)
			if(negated < _count)
			{
				eml = skill.getMagicLevel();
				dml = getSkill().getMagicLevel() - (eml == 0 ? _effected.getLevel() : eml); // FIXME: no effect can have have mLevel == 0. Tofix in skilldata
				buffTime = _effected.getEffectList().getEffectBySkillId(skill.getId()).getTimeLeft();
				cancel_res_multiplier = 1 - (cancel_res_multiplier * .01);
				prelimChance = (2. * dml + _chance + buffTime / 120000) * cancel_res_multiplier; // retail formula

				if(_cat == Category.slot_buff)
				{
					if(prelimChance < 25)
						prelimChance = 25;
					else if(prelimChance > 75)
						prelimChance = 75;
				}

				if(Rnd.chance(prelimChance))
				{
					negated++;
					_effected.getEffectList().stopEffect(skill.getId(), true, false);
					update_effect_list = true;
				}
			}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}

	public static enum Category
	{
		slot_buff,
		slot_debuff
	}
}