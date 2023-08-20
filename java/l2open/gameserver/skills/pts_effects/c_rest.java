package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.*;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.skills.EffectType;

/**
 * {c_rest;-63;5}
 * @c_rest
 * @-63 - Количество МП на Тик...
 * @5 - Время тика(666мс 1 тик)
 **/
/**
 * @author : Diagod
 **/
public class c_rest extends c_mp
{
	public c_rest(Env env, EffectTemplate template, Double mp_tick, Integer tick_time)
	{
		super(env, template, mp_tick, tick_time);
	}

	@Override
	public boolean onActionTime()
	{
		L2Player player = (L2Player) _effected;

		if(player.isDead() || !player.isSitting())
		{
			player.setRelax(false);
			exit(true, false);
			return false;
		}

		if(player.isCurrentHpFull())
		{
			player.sendPacket(Msg.HP_WAS_FULLY_RECOVERED_AND_SKILL_WAS_REMOVED);
			player.getEffectList().stopAllSkillEffects(EffectType.c_rest);
			player.setRelax(false);
			return false;
		}
		return super.onActionTime();
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