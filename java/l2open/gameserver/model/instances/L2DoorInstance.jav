package l2open.gameserver.model.instances;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Events;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.L2CharacterAI;
import l2open.gameserver.ai.L2StaticObjectAI;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.*;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.residence.ResidenceType;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.SiegeClan;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.MyTargetSelected;
import l2open.gameserver.serverpackets.StaticObject;
import l2open.gameserver.serverpackets.ValidateLocation;
import l2open.gameserver.templates.L2CharTemplate;
import l2open.gameserver.templates.L2Weapon;
import l2open.util.geometry.*;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class L2DoorInstance extends L2Character implements GeoCollision
{
	protected static Logger _log = Logger.getLogger(L2DoorInstance.class.getName());

	protected final int _doorId;
	protected final String _name;
	private boolean _open;
	public boolean geoOpen;
	private boolean _geodata = true;
	private boolean _unlockable;
	private boolean _isHPVisible;
	private boolean _siegeWeaponOlnyAttackable;
	private Residence _siegeUnit;
	private int upgradeHp;

	public int key;
	public byte level = 1;

	protected int _autoActionDelay = -1;
	private ScheduledFuture<?> _autoActionTask;

	@Override
	public L2CharacterAI getAI()
	{
		if(_ai == null)
			_ai = new L2StaticObjectAI(this);
		return _ai;
	}

	public L2DoorInstance(int objectId, L2CharTemplate template, int doorId, String name, boolean unlockable, boolean showHp)
	{
		super(objectId, template);
		_doorId = doorId;
		_name = name;
		_unlockable = unlockable;
		_isHPVisible = showHp;
		geoOpen = true;
		setIsInvul(true);
	}

	public boolean isUnlockable()
	{
		return _unlockable;
	}

	@Override
	public byte getLevel()
	{
		return level;
	}

	/**
	 * @return Returns the doorId.
	 */
	public int getDoorId()
	{
		return _doorId;
	}

	/**
	 * @return Returns true if door is opened.
	 */
	public boolean isOpen()
	{
		return _open;
	}

	/**
	 * @param open The open to set.
	 */
	public synchronized void setOpen(boolean open)
	{
		_open = open;
	}

	/**
	 * Sets the delay in milliseconds for automatic opening/closing
	 * of this door instance.
	 * <BR>
	 * <B>Note:</B> A value of -1 cancels the auto open/close task.
	 *
	 * @param actionDelay время задержки между действием
	 */
	public void setAutoActionDelay(int actionDelay)
	{
		if(_autoActionDelay == actionDelay)
			return;

		if(_autoActionTask != null)
		{
			_autoActionTask.cancel(false);
			_autoActionTask = null;
		}

		if(actionDelay > -1)
			_autoActionTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new AutoOpenClose(), actionDelay, actionDelay);

		_autoActionDelay = actionDelay;
	}

	public int getDamage()
	{
		int dmg = 6 - (int) Math.ceil(getCurrentHpRatio() * 6);
		if(dmg > 6)
			return 6;
		if(dmg < 0)
			return 0;
		return dmg;
	}

	//TODO разобраться
	public boolean isEnemyOf(L2Character cha)
	{
		return true;
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return isAttackable(attacker);
	}

	@Override
	public boolean isAttackable(L2Character attacker)
	{
		if(attacker == null)
			return false;
		if(isSiegeWeaponOnlyAttackable() && (!(attacker instanceof L2Summon) || !((L2Summon) attacker).isSiegeWeapon()))
			return false;
		L2Player player = attacker.getPlayer();
		if(player == null)
			return false;
		L2Clan clan = player.getClan();
		if(clan != null && SiegeManager.getSiege(this, true) == clan.getSiege() && clan.isDefender())
			return false;
		if(player.getTerritorySiege() > -1 && getSiegeUnit() != null && player.getTerritorySiege() == getSiegeUnit().getId())
			return false;
		if(clan != null && getSiegeUnit() != null && clan.getClanId() == getSiegeUnit().getOwnerId() && player.getTerritorySiege() == -1)
			return false;
		return !isInvul();
	}

	@Override
	public void updateAbnormalEffect()
	{}

	/**
	 * Return null.<BR><BR>
	 */
	@Override
	public L2ItemInstance getActiveWeaponInstance()
	{
		return null;
	}

	@Override
	public L2Weapon getActiveWeaponItem()
	{
		return null;
	}

	@Override
	public L2ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}

	@Override
	public L2Weapon getSecondaryWeaponItem()
	{
		return null;
	}

	@Override
	public void onAction(L2Player player, boolean shift, int addDist)
	{
		if(player == null)
			return;

		if(Events.onAction(player, this, shift))
		{
			player.sendActionFailed();
			return;
		}

		if(this != player.getTarget())
		{
			player.setTarget(this);
			player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel()));

			if(isAutoAttackable(player))
				//player.sendPacket(new DoorStatusUpdate(this));
				player.sendPacket(new StaticObject(this));

			// correct location
			player.sendPacket(new ValidateLocation(this));
		}
		else
		{
			player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel()));
			if(isAutoAttackable(player))
				player.getAI().Attack(this, false, shift);
			else if(!isInRange(player, INTERACTION_DISTANCE))
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, 100);
			else
			{
				if(!ConfigValue.AllowChDoorOpenOnClick || getSiegeUnit() == null || getSiegeUnit().getSiege() != null && getSiegeUnit().getSiege().isInProgress() || TerritorySiege.isInProgress() && (getSiegeUnit().getType() == ResidenceType.Castle || getSiegeUnit().getType() == ResidenceType.Fortress) || player.getClan() == null || player.getClanId() != getSiegeUnit().getOwnerId())
				{
					player.sendActionFailed();
					return;
				}
				if(getSiegeUnit().getType() == ResidenceType.Castle && (player.getClanPrivileges() & L2Clan.CP_CS_ENTRY_EXIT) == L2Clan.CP_CS_ENTRY_EXIT)
					switchOpenClose();
				else if(getSiegeUnit().getType() == ResidenceType.Fortress && (player.getClanPrivileges() & L2Clan.CP_CS_ENTRY_EXIT) == L2Clan.CP_CS_ENTRY_EXIT)
					switchOpenClose();
				else if(getSiegeUnit().getType() == ResidenceType.Clanhall && (player.getClanPrivileges() & L2Clan.CP_CH_ENTRY_EXIT) == L2Clan.CP_CH_ENTRY_EXIT)
					switchOpenClose();
				player.sendActionFailed();
			}
		}
	}

	public void switchOpenClose()
	{
		if(!isOpen())
			openMe();
		else
			closeMe();
	}

	@Override
	public void broadcastStatusUpdate()
	{
		StaticObject su = new StaticObject(this);
		for(L2Player player : L2World.getAroundPlayers(this))
			if(player != null)
				player.sendPacket(su);
	}

	public void onOpen()
	{
		scheduleCloseMe(60000);
	}

	public void onClose()
	{
		closeMe();
	}

	/**
	 * Вызывает задание на закрытие двери через заданное время.
	 * @param delay Время в миллисекундах
	 */
	public final void scheduleCloseMe(long delay)
	{
		ThreadPoolManager.getInstance().schedule(new CloseTask(), delay);
	}

	public final void closeMe()
	{
		if(isDead())
			return;

		synchronized (this)
		{
			_open = false;
		}

		if(geoOpen)
			setGeoOpen(false);

		broadcastStatusUpdate();
	}

	public final void openMe()
	{
		if(isDead())
			return;

		synchronized (this)
		{
			_open = true;
		}

		if(!geoOpen)
			setGeoOpen(true);

		broadcastStatusUpdate();
	}

	@Override
	public String toString()
	{
		return "door " + _doorId;
	}

	public String getDoorName()
	{
		return _name;
	}

	public void setSiegeUnit(Residence siegeUnit)
	{
		_siegeUnit = siegeUnit;
	}

	public Residence getSiegeUnit()
	{
		return _siegeUnit;
	}

	@Override
	public void doDie(L2Character killer)
	{
		Siege s = SiegeManager.getSiege(this, true);
		if(s != null)
		{
			for(SiegeClan sc : s.getDefenderClans().values())
			{
				L2Clan clan = sc.getClan();
				if(clan != null)
					for(L2Player player : clan.getOnlineMembers(0))
						if(player != null)
							player.sendPacket(Msg.THE_CASTLE_GATE_HAS_BEEN_BROKEN_DOWN);
			}

			for(SiegeClan sc : s.getAttackerClans().values())
			{
				L2Clan clan = sc.getClan();
				if(clan != null)
					for(L2Player player : clan.getOnlineMembers(0))
						if(player != null)
							player.sendPacket(Msg.THE_CASTLE_GATE_HAS_BEEN_BROKEN_DOWN);
			}
		}

		// TODO territory wars сообщение

		setGeoOpen(true);

		super.doDie(killer);
	}

	@Override
	public void spawnMe()
	{
		super.spawnMe();
		closeMe();
	}

	public boolean isHPVisible()
	{
		return _isHPVisible;
	}

	public void setHPVisible(boolean val)
	{
		_isHPVisible = val;
	}

	@Override
	public int getMaxHp()
	{
		return super.getMaxHp() + upgradeHp;
	}

	public void setUpgradeHp(int hp)
	{
		upgradeHp = hp;
	}

	public int getUpgradeHp()
	{
		return upgradeHp;
	}

	@Override
	public int getPDef(L2Character target)
	{
		switch(SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE))
		{
			case SevenSigns.CABAL_DAWN:
				return (int) (super.getPDef(target) * 1.2);
			case SevenSigns.CABAL_DUSK:
				return (int) (super.getPDef(target) * 0.3);
			default:
				return super.getPDef(target);
		}
	}

	@Override
	public int getMDef(L2Character target, L2Skill skill)
	{
		switch(SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE))
		{
			case SevenSigns.CABAL_DAWN:
				return (int) (super.getMDef(target, skill) * 1.2);
			case SevenSigns.CABAL_DUSK:
				return (int) (super.getMDef(target, skill) * 0.3);
			default:
				return super.getMDef(target, skill);
		}
	}

	/**
	 * Двери на осадах уязвимы во время осады.
	 * Остальные двери не уязвимы вообще.
	 * @return инвульная ли дверь.
	 */
	@Override
	public boolean isInvul()
	{
		if(_siegeUnit != null)
		{
			if(_siegeUnit.getSiege() != null && _siegeUnit.getSiege().isInProgress())
				return false;
			if(TerritorySiege.isInProgress() && (_siegeUnit.getType() == ResidenceType.Castle || _siegeUnit.getType() == ResidenceType.Fortress))
				return false;
		}
		return super.isInvul();
	}

	public int getDoorHeight()
	{
		return shape.getZmax() - shape.getZmin() & 0xfff0;
	}

	/**
	 * Дверь/стена может быть атаоквана только осадным орудием
	 * @return true если дверь/стену можно атаковать только осадным орудием
	 */
	public boolean isSiegeWeaponOnlyAttackable()
	{
		return _siegeWeaponOlnyAttackable;
	}

	/**
	 * Устанавливает двери/стене признак возможности атаковать только осадным оружием
	 * @param val true - дверь/стену можно атаковать только осадным орудием
	 */
	public void setSiegeWeaponOlnyAttackable(boolean val)
	{
		_siegeWeaponOlnyAttackable = val;
	}

	/**
	 * Устанавливает значение закрытости\открытости в геодате<br>
	 * @param val новое значение
	 */
	private void setGeoOpen(boolean open)
	{
		if(geoOpen == open)
			return;

		geoOpen = open;

		if(!getGeodata())
			return;

		if(open)
			GeoEngine.removeGeoCollision(this, getReflection().getGeoIndex());
		else
			GeoEngine.applyGeoCollision(this, getReflection().getGeoIndex());

	}

	private class CloseTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			onClose();
		}
	}

	/**
	 * Manages the auto open and closing of a door.
	 */
	private class AutoOpenClose extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			if(!isOpen())
				openMe();
			else
				closeMe();
		}
	}

	@Override
	public L2DoorInstance clone()
	{
		L2DoorInstance door = new L2DoorInstance(IdFactory.getInstance().getNextId(), _template, _doorId, _name, _unlockable, _isHPVisible);
		door.setXYZInvisible(getLoc());
		door.setCurrentHpMp(door.getMaxHp(), door.getMaxMp(), true);
		door.setOpen(_open);
		door.setSiegeWeaponOlnyAttackable(_siegeWeaponOlnyAttackable);
		door.setGeodata(_geodata);
		door.shape = shape;
		door.level = level;
		door.key = key;
		door.setIsInvul(isInvul());
		door._geoAround = _geoAround;
		return door;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}

	@Override
	public Shape getShape()
	{
		return shape;
	}

	private Polygon shape;

	public void setShape(Polygon value)
	{
		shape = value;
	}

	//private byte[][] _geoAround;
	private HashMap<Long, Byte> _geoAround;

	@Override
	/*public byte[][] getGeoAround()
	{
		return _geoAround;
	}*/

	public HashMap<Long, Byte> getGeoAround()
	{
		return _geoAround;
	}
	public void setGeoAround(HashMap<Long, Byte> value)
	{
		_geoAround = value;
	}

	/*@Override
	public void setGeoAround(byte[][] geo)
	{
		_geoAround = geo;
	}*/

	@Override
	public boolean isConcrete()
	{
		return true;
	}

	public void setGeodata(boolean value)
	{
		_geodata = value;
	}

	public boolean getGeodata()
	{
		return _geodata;
	}
}