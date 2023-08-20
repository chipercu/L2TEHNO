package l2open.gameserver.skills.effects;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.*;
import l2open.util.Rnd;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EffectDispelEffects extends L2Effect
{
	private final String _dispelType;
	private final int _cancelRate;
	private final String[] _stackTypes;
	private int _negateCount;

	/*
	 * cancelRate is skill dependant constant:
	 * Cancel - 25
	 * Touch of Death/Insane Crusher - 25
	 * Mage/Warrior Bane - 80
	 * Mass Mage/Warrior Bane - 40
	 * Infinity Spear - 10
	 */

	public EffectDispelEffects(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
		_dispelType = template.getParam().getString("dispelType", "");
		_cancelRate = template.getParam().getInteger("cancelRate", 0);
		_negateCount = template.getParam().getInteger("negateCount", 5);
		_stackTypes = template.getParam().getString("negateStackTypes", "").split(";");
	}

	@Override
	public void onStart()
	{
		double cancel_res_multiplier = _effected.calcStat(Stats.CANCEL_RECEPTIVE, 0, null, null); // constant resistance is applied for whole cycle of cancellation
		if(ConfigValue.EnableCancelFullResist && cancel_res_multiplier >= 100 && !_dispelType.equals("cleanse"))
			return;
		List<L2Skill> _musicList = new ArrayList<L2Skill>();
		List<L2Skill> _buffList = new ArrayList<L2Skill>();

		//H5 - triggerable skills go first
		if(ConfigValue.EnableCancelRndCount && _dispelType.equals("cancellation"))
			_negateCount = Rnd.get(1, _negateCount);

		// Getting effect lists
		for(L2Effect e : _effected.getEffectList().getAllEffects())
		{
			if(_dispelType.equals("cancellation"))
			{
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
			}
			else if(_dispelType.equals("bane"))
			{
				if(!e.isOffensive() && ArrayUtils.contains(_stackTypes, e.getAbnormalType()) && e.getSkill().isCancelable() && !_buffList.contains(e.getSkill()))
					_buffList.add(e.getSkill());
			}
			else if(_dispelType.equals("cleanse"))
			{
				if(e.isOffensive() && e.getSkill().isCancelable() && getSkill().getId() != 2530 && e.getSkill().getId() != 5660 && e.getSkill().getId() != 4515 && e.getSkill().getId() != 4215 && !_buffList.contains(e.getSkill()))
					_buffList.add(e.getSkill());
			}
		}

		// Reversing lists and adding to a new list
		List<L2Skill> _skillList = new ArrayList<L2Skill>();
		Collections.reverse(_musicList);
		Collections.reverse(_buffList);
		_skillList.addAll(_musicList);
		_skillList.addAll(_buffList);

		if(_skillList.isEmpty())
			return;

		if(ConfigValue.EnableShuffleSkill)
			Collections.shuffle(_skillList);

		double prelimChance, eml, dml;
		long buffTime, negated = 0;

		if(ConfigValue.CancelEbal && _dispelType.equals("cancellation"))
			_negateCount = Rnd.get(ConfigValue.CancelEbalMin, _negateCount);
		for(L2Skill skill : _skillList)
			if(negated < _negateCount)
			{
				eml = skill.getMagicLevel();
				dml = getSkill().getMagicLevel() - (eml == 0 ? _effected.getLevel() : eml); // FIXME: no effect can have have mLevel == 0. Tofix in skilldata
				L2Effect ef = _effected.getEffectList().getEffectBySkillId(skill.getId());
				if(ef == null)
					continue;
				buffTime = ef.getTimeLeft();
				cancel_res_multiplier = 1 - (cancel_res_multiplier * .01);
				prelimChance = (2. * dml + _cancelRate + buffTime / 120000) * cancel_res_multiplier; // retail formula

				if(Rnd.chance(calcSkillChanceLimits(prelimChance, _effector.isPlayable())))
				{
					negated++;
					_effected.getEffectList().stopEffect(skill.getId(), false, false);
					update_effect_list = true;
				}
			}
	}

	private double calcSkillChanceLimits(double prelimChance, boolean isPlayable)
	{
		if(_dispelType.equals("bane"))
		{
			if(prelimChance < ConfigValue.MinBaneChance)
				return ConfigValue.MinBaneChance;
			else if(prelimChance > ConfigValue.MaxBaneChance)
				return ConfigValue.MaxBaneChance;
		}
		else if(_dispelType.equals("cancellation"))
		{
			if(prelimChance < ConfigValue.MinCancellationChance)
				return ConfigValue.MinCancellationChance;
			else if(prelimChance > ConfigValue.MaxCancellationChance)
				return ConfigValue.MaxCancellationChance;
		}
		else if(_dispelType.equals("cleanse"))
			return _cancelRate;
		return prelimChance;
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}