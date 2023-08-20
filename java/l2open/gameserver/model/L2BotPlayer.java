package l2open.gameserver.model;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2ObjectTasks.HitTask;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.templates.L2PlayerTemplate;
import l2open.util.*;

public class L2BotPlayer extends L2Player
{
	public L2BotPlayer(final int objectId, final L2PlayerTemplate template, final String accountName, int bot)
	{
		super(objectId, template, accountName, bot);
		_log.info("new L2BotPlayer 1");
	}

	public L2BotPlayer(final int objectId, final L2PlayerTemplate template, int bot)
	{
		super(objectId, template, bot);
		_log.info("new L2BotPlayer 2");
	}

	@Override
	protected boolean doAttackHitSimple(Attack attack, L2Character target, double multiplier, boolean unchargeSS, int sAtk, boolean notify)
	{
		int damage1 = (int)(target.getMaxHp()/Rnd.get(15, 100));
		if(getPlayer() != null)
			for (L2Cubic cubic : getPlayer().getCubics()) 
				cubic.startAttack(target);

		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, false, false, attack._soulshot, false, unchargeSS, notify, false, sAtk), sAtk, isPlayable());

		attack.addHit(target, damage1, false, false, false);
		return true;
	}

	@Override
	protected void doAttackHitByBow(Attack attack, L2Character target, int sAtk, boolean notify)
	{
		int range = (int)(getPhysicalAttackRange()+getMinDistance(target));

		int offset = (int)Math.ceil(getMinDistance(target));
		if(getPhysicalAttackRange() <= 300)
			offset += (int)(getPhysicalAttackRange()*.67);
		else
			offset += (int)(getPhysicalAttackRange()-100f);

		if(isInRange(target, offset-40))
		{
			offset = 3000;
			int oldX = getX();
			int oldY = getY();
			double angle = Math.toRadians(Location.calculateAngleFrom(this, target));
			int x = oldX + (int)(offset * Math.cos(angle));
			int y = oldY + (int)(offset * Math.sin(angle));
			moveToLocation(GeoEngine.moveCheck(oldX, oldY, getZ(), x, y, getReflection().getGeoIndex()), 0, false);
			return;
		}
		if(getActiveWeaponItem() == null)
			return;

		int damage1 = (int)(target.getMaxHp()/Rnd.get(15, 100));
		boolean crit1 = false;

		if(getPlayer() != null)
			for(L2Cubic cubic : getPlayer().getCubics())
				cubic.startAttack(target);	

		attack.addHit(target, damage1, false, crit1, false);
		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, crit1, false, attack._soulshot, false, true, notify, true, sAtk), sAtk, isPlayable());
	}

	@Override
	protected void doAttackHitByDual(Attack attack, L2Character target, int sAtk, boolean notify)
	{
		int damage1 = (int)(target.getMaxHp()/Rnd.get(15, 100));
		int damage2 = (int)(target.getMaxHp()/Rnd.get(15, 100));
		boolean crit1 = false;
		boolean crit2 = false;

		if(getPlayer() != null)
			for (L2Cubic cubic : getPlayer().getCubics())
				cubic.startAttack(target);

		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, crit1, false, attack._soulshot, false, true, false, false, sAtk/2), sAtk / 2, isPlayable());
		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage2, crit2, false, attack._soulshot, false, false, notify, false, sAtk), sAtk, isPlayable());

		attack.addHit(target, damage1, false, crit1, false);
		attack.addHit(target, damage2, false, crit2, false);
	}

	@Override
	public boolean isPlayer()
	{
		return true;
	}

	@Override
	public boolean isBot()
	{
		return true;
	}

	@Override
	public boolean isPhantom()
	{
		return true;
	}
}