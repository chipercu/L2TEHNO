package l2open.gameserver.skills.pts_effects;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.util.Rnd;

/**
 * {i_take_adena;-63;5}
 * @i_take_adena
 * @-63 - Количество ХП на Тик...
 * @5 - Время тика(666мс 1 тик)
 **/
/**
 * @author : Diagod
 **/
public class i_take_adena extends L2Effect
{
	private int _min_per_count;
	private int _max_per_count;

	public i_take_adena(Env env, EffectTemplate template, Integer chance, Integer min_per_count, Integer max_per_count)
	{
		super(env, template);

		_instantly = true;
		env.value = chance;
		_min_per_count = min_per_count;
		_max_per_count = max_per_count;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(getEffected().isPlayer() && Formulas.calcSkillSuccess(_env, getEffector().getChargedSpiritShot(), false))
		{
			int per_rnd = Rnd.get(_min_per_count, _max_per_count);
			long adena = (getEffected().getPlayer().getAdena()/100*per_rnd);
			if(adena > 0)
			{
				getEffected().getPlayer().reduceAdena(adena, false);
				getEffected().sendMessage("У вас украли "+adena+" Адены.");

				getEffector().getPlayer().addAdena(adena);
				getEffector().sendMessage("Вы украли "+adena+" Адены.");
			}
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}