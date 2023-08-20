package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {c_fake_death;-63;5}
 * @c_fake_death
 * @-63 - Количество МП на Тик...
 * @5 - Время тика(666мс 1 тик)
 **/
/**
 * @author : Diagod
 **/
public class c_fake_death extends c_mp
{
	public c_fake_death(Env env, EffectTemplate template, Double mp_tick, Integer tick_time)
	{
		super(env, template, mp_tick, tick_time);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().startFakeDeath();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		// 5 секунд после FakeDeath на персонажа не агрятся мобы
		getEffected().setNonAggroTime(System.currentTimeMillis() + 5000);
		getEffected().stopFakeDeath();
	}
}