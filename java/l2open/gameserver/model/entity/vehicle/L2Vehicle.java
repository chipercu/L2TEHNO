package l2open.gameserver.model.entity.vehicle;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.PlaySound;
import l2open.gameserver.serverpackets.Say2;
import l2open.gameserver.templates.L2CharTemplate;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.util.GArray;
import l2open.util.Location;

import java.util.concurrent.ScheduledFuture;

public abstract class L2Vehicle extends L2Character
{
	private int _id;

	public int _cycle = 0;
	public int _speed1;
	public int _speed2;
	public int _runstate = 0;

	protected L2VehicleTrajet _t1;
	protected L2VehicleTrajet _t2;

	protected ScheduledFuture<?> _vehicleCaptainTask;
	private boolean _isArrived;
	private int _playersCountAtBoard;
	
	public GArray<L2Player> _players = new GArray<L2Player>();

	public L2Vehicle(String name, int id)
	{
		super(IdFactory.getInstance().getNextId(), new L2CharTemplate(L2CharTemplate.getEmptyStatsSet()), false);
		_name = name;
		_id = id;
	}

	public int getPlayersCountAtBoard()
	{
		return _playersCountAtBoard;
	}

	public void setIsArrived(boolean val)
	{
		_isArrived = val;
	}

	public boolean isArrived()
	{
		return _isArrived;
	}

	public boolean isDocked()
	{
		return _runstate == 0;
	}

	public void setIsDocked()
	{
		_runstate = 0;
	}

	public int getId()
	{
		return _id;
	}

	@Override
	public float getMoveSpeed()
	{
		return _speed1;
	}

	@Override
	public int getRunSpeed()
	{
		return _speed1;
	}

	public int getRotationSpeed()
	{
		return _speed2;
	}

	@Override
	public void setXYZ(int x, int y, int z)
	{
		super.setXYZ(x, y, z);
		updatePeopleInTheBoat(x, y, z);
		if(isClanAirShip() && !isDocked() && isArrived())
			((L2AirShip) this).tryToLand();
	}

	@Override
	public void setXYZ(int x, int y, int z, boolean MoveTask)
	{
		super.setXYZ(x, y, z, MoveTask);
		updatePeopleInTheBoat(x, y, z);
		if(isClanAirShip() && !isDocked() && isArrived())
			((L2AirShip) this).tryToLand();
	}

	public void VehicleArrived()
	{
		if(_cycle == 1)
			_t1.moveNext();
		else
			_t2.moveNext();
	}

	public void updatePeopleInTheBoat(int x, int y, int z)
	{
		_playersCountAtBoard = 0;
		for(L2Player player : _players)
			if(player != null && player.getVehicle() == this)
			{
				_playersCountAtBoard++;
				player.setXYZ(x, y, z);
			}
	}

	public void begin()
	{
		if(_cycle == 1)
			_t1.moveNext();
		else
			_t2.moveNext();
	}

	protected void exitFromBoat(L2Player player)
	{
		L2VehicleTrajet t = _cycle == 1 ? _t1 : _t2;
		player.sendPacket(Msg.YOU_MAY_NOT_GET_ON_BOARD_WITHOUT_A_PASS);
		player.setVehicle(null);
		broadcastGetOffVehicle(player, t._return);
		player.teleToLocation(t._return);
	}

	public void say(int i)
	{
		if(isClanAirShip())
			return;

		L2VehicleTrajet t = _cycle == 1 ? _t1 : _t2;
		switch(i)
		{
			case 5:
				SayAndSound(t._msgs[0], (getId() == 8 || getId() == 9) ? t._msgs[2] : t._msgs[1], "itemsound.ship_arrival_departure");
				break;
			case 3:
				SayAndSound(t._msgs[0], t._msgs[2], "itemsound.ship_5min"); // 3 минуты (не 5)
				break;
			case 1:
				SayAndSound(t._msgs[0], t._msgs[3], "itemsound.ship_1min");
				break;
			case 0:
				SayAndSound(t._msgs[0], t._msgs[4], null);
				break;
			case -1:
				SayAndSound(t._msgs[0], t._msgs[5], "itemsound.ship_arrival_departure");
				break;
		}
	}

	public void SayAndSound(String npc, String text, String sound)
	{
		if(npc == null || text == null || npc.isEmpty() || text.isEmpty())
			return;

		Say2 s1 = new Say2(0, Say2C.SHOUT, npc, text);
		PlaySound s2 = sound == null ? null : new PlaySound(0, sound, 1, getObjectId(), getLoc());
		for(L2Player player : L2World.getAroundPlayers(this, 10000, 1000))
			if(player != null)
			{
				player.sendPacket(s1);
				if(s2 != null)
					player.sendPacket(s2);
			}
	}

	public void spawn()
	{
		if(isClanAirShip())
		{
			L2AirShip airship = (L2AirShip) this;
			setHeading(airship.getClanAirshipSpawnLoc().h);
			setXYZInvisible(airship.getClanAirshipSpawnLoc());
			airship.startMaintenanceTask();
		}
		spawnMe();
		broadcastVehicleInfo();
		_cycle = 1;
		say(1);
		if(_vehicleCaptainTask != null)
			_vehicleCaptainTask.cancel(true);
		_vehicleCaptainTask = ThreadPoolManager.getInstance().schedule(new L2VehicleCaptain(this, 3), isClanAirShip() ? 0 : getNpcId() == 5 ? 60000 : 60000);
	}

	public void despawn()
	{
		for(L2Player player : _players)
			if(player != null && player.getVehicle() == this)
				exitFromBoat(player);
		if(isVisible())
			decayMe();
		_players.clear();
	}

	public void teleportShip(int x, int y, int z, int heading)
	{
		for(L2Player player : _players)
			if(player != null && player.getVehicle() == this)
			{
				if(player.isGM())
					player.sendMessage("teleport to: (" + x + ", " + y + ", " + z + ")");
				sendStopMove(player);
				broadcastGetOffVehicle(player, new Location(x, y, z));
				player.teleToLocation(x, y, z);
			}

		setHeading(heading);
		teleToLocation(x, y, z);

		ThreadPoolManager.getInstance().schedule(new L2VehicleArrived(this), 5000);
	}

	@Override
	public void broadcastPacket(L2GameServerPacket... packets)
	{
		if(!isVisible())
			return;

		GArray<L2Player> list = new GArray<L2Player>();

		for(L2Player player : L2World.getAroundPlayers(this))
			if(player != null)
				list.add(player);

		for(L2Player player : _players)
			if(player != null && player.getVehicle() == this && !list.contains(player))
				list.add(player);

		for(L2Player target : list)
			target.sendPacket(packets);
	}

	public abstract void broadcastVehicleStart(int state);

	public abstract void broadcastVehicleCheckLocation();

	public abstract void broadcastVehicleInfo();

	public abstract void broadcastStopMove();

	public abstract void broadcastGetOffVehicle(L2Player player, Location loc);

	public abstract void sendVehicleInfo(L2Player player);

	public abstract void sendStopMove(L2Player player);

	public abstract void oustPlayers();

	public void SetTrajet1(int idWaypoint1, int idWTicket1, Location ret_loc, String[] msgs)
	{
		_t1 = new L2VehicleTrajet(this, idWaypoint1, idWTicket1, ret_loc, msgs);
	}

	public L2VehicleTrajet getTrajet1()
	{
		return _t1;
	}

	public void SetTrajet2(int idWaypoint1, int idWTicket1, Location ret_loc, String[] msgs)
	{
		_t2 = new L2VehicleTrajet(this, idWaypoint1, idWTicket1, ret_loc, msgs);
	}

	@Override
	public void updateAbnormalEffect()
	{}

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
	public WeaponType getFistWeaponType()
	{
		return WeaponType.FIST;
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
	public byte getLevel()
	{
		return 0;
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}

	@Override
	public boolean isAttackable(L2Character attacker)
	{
		return false;
	}

	public boolean isClanAirShip()
	{
		return false;
	}

	@Override
	public boolean isVehicle()
	{
		return true;
	}
}