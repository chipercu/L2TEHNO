package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.*;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {c_chameleon_rest;-63;5}
 * @c_chameleon_rest
 * @-63 - Количество МП на Тик...
 * @5 - Время тика(666мс 1 тик)
 **/
/**
 * @author : Diagod
 **/
public class c_chameleon_rest extends c_mp
{
	public c_chameleon_rest(Env env, EffectTemplate template, Double mp_tick, Integer tick_time)
	{
		super(env, template, mp_tick, tick_time);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		L2Player player = (L2Player) _effected;
		player.setRelax(true);
		player.sitDown(true); // TODO: сделать как на ПТСке, через некстАктион)
	}

	@Override
	public void onExit()
	{
		super.onExit();
		L2Player player = (L2Player) _effected;
		player.setRelax(false);
	}
}