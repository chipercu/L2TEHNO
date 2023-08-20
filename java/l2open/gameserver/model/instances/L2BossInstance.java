package l2open.gameserver.model.instances;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.logging.Logger;

public class L2BossInstance extends L2RaidBossInstance
{
	protected static Logger _log = Logger.getLogger(L2BossInstance.class.getName());
	private boolean _teleportedToNest;

	private static final int BOSS_MAINTENANCE_INTERVAL = 10000;

	/**
	 * Constructor<?> for L2BossInstance. This represent all grandbosses:
	 * <ul>
	 * <li>29001	Queen Ant</li>
	 * <li>29014	Orfen</li>
	 * <li>29019	Antharas</li>
	 * <li>29020	Baium</li>
	 * <li>29022	Zaken</li>
	 * <li>29028	Valakas</li>
	 * <li>29006	Core</li>
	 * </ul>
	 * <br>
	 * <b>For now it's nothing more than a L2Monster but there'll be a scripting<br>
	 * engine for AI soon and we could add special behaviour for those boss</b><br>
	 * <br>
	 * @param objectId ID of the instance
	 * @param template L2NpcTemplate of the instance
	 */
	public L2BossInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected int getMaintenanceInterval()
	{
		return BOSS_MAINTENANCE_INTERVAL;
	}

	@Override
	public final boolean isMovementDisabled()
	{
		// Core should stay anyway
		return getNpcId() == 29006 || super.isMovementDisabled();
	}

	/**
	 * Used by Orfen to set 'teleported' flag, when hp goes to <50%
	 * @param flag
	 */
	public void setTeleported(boolean flag)
	{
		_teleportedToNest = flag;
	}

	public boolean isTeleported()
	{
		return _teleportedToNest;
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}

	@Override
	public boolean isBoss()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}
}
