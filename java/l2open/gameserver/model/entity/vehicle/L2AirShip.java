package l2open.gameserver.model.entity.vehicle;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2AirShipControllerInstance;
import l2open.gameserver.model.instances.L2StaticObjectInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExAirShipInfo;
import l2open.gameserver.serverpackets.ExAirShipTeleportList;
import l2open.gameserver.serverpackets.ExGetOffAirShip;
import l2open.gameserver.serverpackets.ExStopMoveAirShip;
import l2open.gameserver.tables.AirShipDocksTable;
import l2open.gameserver.tables.AirShipDocksTable.AirShipDock;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.Location;

import java.util.concurrent.ScheduledFuture;

public class L2AirShip extends L2Vehicle
{
	public static final int MAX_FUEL = 600;
	private int _fuel;
	private L2Player _driver;
	private L2Clan _owner;
	private L2StaticObjectInstance _controlKey;
	private Location _clanAirshipSpawnLoc;
	private ScheduledFuture<?> _airshipMaintenanceTask;
	private static final long MAINTENANCE_DELAY = 1 * 60 * 1000L; // 1 min

	public L2AirShip(L2Clan owner, String name, int id)
	{
		super(name, id);
		_owner = owner;
		_speed1 = 300;
		_speed2 = 4000;

		if(isClanAirShip())
		{
			_owner.setAirship(this);
			_fuel = owner.getAirshipFuel();

			StatsSet npcDat = L2NpcTemplate.getEmptyStatsSet();
			npcDat.set("npcId", 0);
			npcDat.set("name", "Helm");
			npcDat.set("type", "L2StaticObject");
			L2NpcTemplate template = new L2NpcTemplate(npcDat);
			L2StaticObjectInstance controlKey = new L2StaticObjectInstance(_owner.getClanId(), template);
			controlKey.setType(3);
			controlKey.setLoc(getLoc());
			_controlKey = controlKey;
		}
	}

	public void startMaintenanceTask()
	{
		if(!isClanAirShip())
			return;
		if(_airshipMaintenanceTask != null)
			_airshipMaintenanceTask.cancel(true);
		_airshipMaintenanceTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new MaintenanceTask(this), MAINTENANCE_DELAY, MAINTENANCE_DELAY);
	}

	@Override
	public void despawn()
	{
		if(_airshipMaintenanceTask != null)
			_airshipMaintenanceTask.cancel(false);

		// FIXME разобраться надо ли оно тут для воздушных кораблей
		for(L2Player player : _players)
			if(player != null && player.getVehicle() == this)
				exitFromBoat(player);

		if(isVisible())
			decayMe();
		_players.clear();
	}

	public int getCurrentDockNpcId()
	{
		if(!isDocked())
			return 0;
		for(L2Character cha : L2World.getAroundCharacters(this, 1000, 500))
			if(cha instanceof L2AirShipControllerInstance)
				return cha.getNpcId();
		return 0;
	}

	public void setClanAirshipSpawnLoc(Location val)
	{
		_clanAirshipSpawnLoc = val;
	}

	public Location getClanAirshipSpawnLoc()
	{
		return _clanAirshipSpawnLoc;
	}

	public L2StaticObjectInstance getControlKey()
	{
		return _controlKey;
	}

	@Override
	public boolean isClanAirShip()
	{
		return _owner != null;
	}

	public void setFuel(int fuel)
	{
		int newFuel = Math.max(0, fuel);
		newFuel = Math.min(newFuel, MAX_FUEL);
		if(_fuel != newFuel)
		{
			_fuel = newFuel;
			broadcastVehicleInfo();
		}
	}

	public int getFuel()
	{
		return isClanAirShip() ? _fuel : 0;
	}

	public void setDriver(L2Player driver)
	{
		_driver = driver;
		broadcastVehicleInfo();
	}

	public L2Player getDriver()
	{
		return _driver;
	}

	public L2Clan getOwner()
	{
		return _owner;
	}

	@Override
	public void deleteMe()
	{
		if(isClanAirShip())
		{
			getOwner().setAirship(null);
			getOwner().setAirshipFuel(getFuel());
			PlayerData.getInstance().updateClanInDB(getOwner());
			getControlKey().deleteMe();
		}
		super.deleteMe();
	}

	@Override
	public void broadcastVehicleStart(int state)
	{}

	@Override
	public void broadcastVehicleCheckLocation()
	{}

	@Override
	public void broadcastVehicleInfo()
	{
		broadcastPacket(new ExAirShipInfo(this));
	}

	@Override
	public void broadcastStopMove()
	{
		broadcastPacket(new ExStopMoveAirShip(this));
	}

	@Override
	public void broadcastGetOffVehicle(L2Player player, Location loc)
	{
		broadcastPacket(new ExGetOffAirShip(player, this, loc));
	}

	@Override
	public void sendVehicleInfo(L2Player player)
	{
		player.sendPacket(new ExAirShipInfo(this));
	}

	@Override
	public void sendStopMove(L2Player player)
	{
		player.sendPacket(new ExStopMoveAirShip(this));
	}

	@Override
	public void oustPlayers()
	{
		oustPlayers(0);
	}

	public void oustPlayers(int movieId)
	{
		for(L2Player player : L2World.getAroundPlayers(this))
			if(player != null && player.getVehicle() == this)
			{
				if(movieId > 0)
					player.showQuestMovie(movieId);
				oustPlayer(player, findPositionToOust(player));
			}
	}

	public void tryToLand()
	{
		int movieId = 0;
		for(L2Character cha : L2World.getAroundCharacters(this, 4000, 2000))
			if(cha instanceof L2AirShipControllerInstance)
			{
				AirShipDock ad = AirShipDocksTable.getInstance().getAirShipDockByNpcId(cha.getNpcId());
				movieId = ad.getDepartureMovieId();
				if(movieId > 0)
				{
					oustPlayers(movieId);
					break;
				}
			}

		if(movieId > 0)
		{
			L2VehicleManager.getInstance().getBoats().remove(getObjectId());
			despawn();
			deleteMe();
		}
	}

	/**
	 * Ищет точку для высадки игрока
	 */
	private static Location findPositionToOust(L2Player player)
	{
		L2AirShip airship = (L2AirShip) player.getVehicle();
		if(airship == null)
			return null;

		Location returnLoc = null;
		if(airship.isClanAirShip())
		{
			for(L2Character cha : L2World.getAroundCharacters(airship, 4000, 2000))
				if(cha instanceof L2AirShipControllerInstance)
				{
					AirShipDock ad = AirShipDocksTable.getInstance().getAirShipDockByNpcId(cha.getNpcId());
					if(ad == null)
						continue;
					returnLoc = ad.getUpsetLoc();
					if(returnLoc != null)
						break;
				}
		}
		else
		{
			L2VehicleTrajet t = airship._cycle == 1 ? airship._t1 : airship._t2;
			returnLoc = t._return;
		}

		if(returnLoc == null)
			returnLoc = player._stablePoint;

		return returnLoc;
	}

	/**
	 * Высаживает игрока с корабля в указанную точку
	 */
	private static void oustPlayer(L2Player player, Location loc)
	{
		L2AirShip airship = (L2AirShip) player.getVehicle();
		if(airship == null || loc == null)
			return;

		if(airship.getDriver() == player)
			airship.setDriver(null);

		player._stablePoint = null; // TODO очень внимательно проверить, во всех ли случаях оно очищается
		player.setVehicle(null);
		player.broadcastPacket(new ExGetOffAirShip(player, airship, loc));
		player.teleToLocation(loc);
	}

	/**
	 * Steer. Allows you to control the Airship.
	 */
	public static void controlSteer(L2Player activeChar)
	{
		L2Vehicle vehicle = activeChar.getVehicle();
		if(vehicle == null || !vehicle.isClanAirShip())
			return;

		L2AirShip airship = (L2AirShip) vehicle;

		// Руль должен быть взят в таргет
		if(!(activeChar.getTarget() instanceof L2StaticObjectInstance) || activeChar.getTarget().getObjectId() != airship.getControlKey().getObjectId())
			return;

		if(airship.getDriver() != null)
		{
			activeChar.sendPacket(Msg.ANOTHER_PLAYER_IS_PROBABLY_CONTROLLING_THE_TARGET);
			return;
		}

		if(activeChar.getTransformation() != 0)
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_CONTROL_THE_TARGET_WHILE_TRANSFORMED);
			return;
		}

		if(activeChar.isParalyzed())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_CONTROL_THE_TARGET_WHILE_YOU_ARE_PETRIFIED);
			return;
		}

		if(activeChar.isDead() || activeChar.isFakeDeath())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_CONTROL_THE_TARGET_WHEN_YOU_ARE_DEAD);
			return;
		}

		if(activeChar.isFishing())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_CONTROL_THE_TARGET_WHILE_FISHING);
			return;
		}

		if(activeChar.isInCombat())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_CONTROL_THE_TARGET_WHILE_IN_A_BATTLE);
			return;
		}

		if(activeChar.getDuel() != null)
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_CONTROL_THE_TARGET_WHILE_IN_A_DUEL);
			return;
		}

		if(activeChar.isSitting())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_CONTROL_THE_TARGET_WHILE_IN_A_SITTING_POSITION);
			return;
		}

		if(activeChar.isCastingNow())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_CONTROL_THE_TARGET_WHILE_USING_A_SKILL);
			return;
		}

		if(activeChar.isCursedWeaponEquipped())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_CONTROL_THE_TARGET_WHILE_A_CURSED_WEAPON_IS_EQUIPPED);
			return;
		}

		if(activeChar.isTerritoryFlagEquipped() || activeChar.isCombatFlagEquipped())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_CONTROL_THE_TARGET_WHILE_HOLDING_A_FLAG);
			return;
		}

		// Другие условия?
		// activeChar.sendPacket(Msg.YOU_CANNOT_CONTROL_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);

		// разоружаем
		L2ItemInstance weapon = activeChar.getActiveWeaponInstance();
		L2ItemInstance weapon2 = activeChar.getSecondaryWeaponInstance();
		if(weapon != null)
			activeChar.getInventory().unEquipItemInBodySlotAndNotify(weapon.getBodyPart(), weapon, true);
		if(weapon2 != null)
			activeChar.getInventory().unEquipItemInBodySlotAndNotify(weapon2.getBodyPart(), weapon2, true);

		// Берем в руки штурвал
		airship.setDriver(activeChar);
		activeChar.broadcastUserInfo(true);
	}

	/**
	 * Cancel Control. Relinquishes control of the Airship.
	 */
	public static void controlCancel(L2Player activeChar)
	{
		L2Vehicle vehicle = activeChar.getVehicle();
		if(vehicle == null || !vehicle.isAirShip())
			return;

		L2AirShip airship = (L2AirShip) vehicle;
		if(airship.getDriver() == null || airship.getDriver() != activeChar)
			return;

		// Отменяем управление
		airship.setDriver(null);
		activeChar.broadcastUserInfo(true);
	}

	/**
	 * Destination Map. Choose from pre-designated locations.
	 */
	public static void controlDestination(L2Player activeChar)
	{
		L2Vehicle vehicle = activeChar.getVehicle();
		if(vehicle == null || !vehicle.isAirShip())
			return;

		L2AirShip airship = (L2AirShip) vehicle;
		if(!airship.isDocked() || airship.getDriver() == null || airship.getDriver() != activeChar)
			return;

		// Открываем миникарту с выбором конечной точки
		activeChar.sendPacket(new ExAirShipTeleportList(airship));
	}

	/**
	 * Exit Airship. Disembarks from the Airship.
	 */
	public static void controlExit(L2Player activeChar)
	{
		L2Vehicle vehicle = activeChar.getVehicle();
		if(vehicle == null || !vehicle.isAirShip())
			return;

		L2AirShip airship = (L2AirShip) vehicle;
		if(airship.isMoving || !airship.isDocked())
		{
			activeChar.sendPacket(Msg.BOARDING_OR_CANCELLATION_OF_BOARDING_ON_AIRSHIPS_IS_NOT_ALLOWED_IN_THE_CURRENT_AREA);
			return;
		}

		Location pos = findPositionToOust(activeChar);
		if(pos != null)
			oustPlayer(activeChar, pos);
		else
			activeChar.sendPacket(Msg.BOARDING_OR_CANCELLATION_OF_BOARDING_ON_AIRSHIPS_IS_NOT_ALLOWED_IN_THE_CURRENT_AREA);
	}

	private class MaintenanceTask extends l2open.common.RunnableImpl
	{
		private L2AirShip _airship;

		public MaintenanceTask(L2AirShip airship)
		{
			_airship = airship;
		}

		public void runImpl()
		{
			// При свободном движении корабля потребляется 10 топлива в минуту
			if(!_airship.isDocked() && _airship.isArrived())
				_airship.setFuel(_airship.getFuel() - 10);

			if(_airship.isDocked() || _airship.isMoving)
				return;
			_airship.updatePeopleInTheBoat(_airship.getX(), _airship.getY(), _airship.getZ());
			if(getPlayersCountAtBoard() > 0)
				return;
			// Освобождаем корабль от использования
			_airship.setFuel(_airship.getFuel() - 20);
			L2VehicleManager.getInstance().getBoats().remove(_airship.getObjectId());
			_airship.despawn();
			_airship.deleteMe();
		}
	}

	@Override
	public void setIsDocked()
	{
		for(L2Character cha : L2World.getAroundCharacters(this, 1000, 500))
			if(cha instanceof L2AirShipControllerInstance)
			{
				_runstate = 0;
				return;
			}
		_runstate = -1;
	}

	@Override
	public boolean isAirShip()
	{
		return true;
	}
}