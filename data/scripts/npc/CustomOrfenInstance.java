package npc.model;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2BossInstance;
import l2open.gameserver.serverpackets.PlaySound;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;
import l2open.util.Rnd;

public class CustomOrfenInstance extends L2BossInstance
{
	public static final Location nest = new Location(43728, 17220, -4342);

	public static final Location[] locs = new Location[] { new Location(55024, 17368, -5412),
			new Location(53504, 21248, -5496), new Location(53248, 24576, -5272) };

	public CustomOrfenInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void setTeleported(boolean flag)
	{
		super.setTeleported(flag);
		Location loc = flag ? nest : locs[Rnd.get(locs.length)];
		setSpawnedLoc(loc);
		clearAggroList(true);
		getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
		teleToLocation(loc);
	}

	@Override
	public void spawnMe()
	{
		super.spawnMe();
		setTeleported(false);
		broadcastPacketToOthers(new PlaySound(1, "BS01_A", 1, 0, getLoc()));
	}

	@Override
	public void doDie(L2Character killer)
	{
		broadcastPacketToOthers(new PlaySound(1, "BS02_D", 1, 0, getLoc()));
		killer.getPlayer().setVarInst(getReflection().getName(), String.valueOf(System.currentTimeMillis()));

		unspawnMinions();

		super.doDie(killer);

		clearReflection();
	}

	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, L2Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean isDot, double i2, boolean sendMesseg, boolean bow, boolean crit, boolean tp)
	{
		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, isDot, i2, sendMesseg, bow, crit, tp);
		if(!isTeleported() && getCurrentHpPercents() <= 50)
			setTeleported(true);
	}

	/**
	 * Удаляет все спауны из рефлекшена и запускает 5ти минутный коллапс-таймер.
	 */
	protected void clearReflection()
	{
		getReflection().clearReflection(5, true);
	}

	@Override
	public void unspawnMinions()
	{
		removeMinions();
	}

	@Override
	public boolean isRaid()
	{
		return false;
	}

	@Override
	public boolean isRefRaid()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}
}