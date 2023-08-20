package l2open.gameserver.model.entity.vehicle;

import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.*;
import l2open.util.Location;

public class L2Ship extends L2Vehicle
{
	public L2Ship(String name, int id)
	{
		super(name, id);
	}

	@Override
	public void begin()
	{
		L2VehicleTrajet t = _cycle == 1 ? _t1 : _t2;

		for(L2Player player : _players)
			if(player != null && player.getVehicle() == this)
			{
				L2ItemInstance it = player.getInventory().getItemByItemId(t._ticketId);
				if(it != null && it.getCount() >= 1)
				{
					player.getInventory().destroyItem(it.getObjectId(), 1, false);
					player.sendPacket(new VehicleStart(this, 1));
				}
				else if(it == null && t._ticketId == 0 || player.isGM())
					player.sendPacket(new VehicleStart(this, 1));
				else
					exitFromBoat(player);
			}

		super.begin();
	}

	@Override
	public void broadcastVehicleStart(int state)
	{
		broadcastPacket(new VehicleStart(this, state));
	}

	@Override
	public void broadcastVehicleCheckLocation()
	{
		broadcastPacket(new VehicleCheckLocation(this));
	}

	@Override
	public void broadcastVehicleInfo()
	{
		broadcastPacket(new VehicleInfo(this));
	}

	@Override
	public void broadcastStopMove()
	{
		broadcastPacket(new StopMove(this));
	}

	@Override
	public void broadcastGetOffVehicle(L2Player player, Location loc)
	{
		if(player.isGM())
			player.sendMessage("broadcastGetOffVehicle");
		broadcastPacket(new GetOffVehicle(player, this, loc.x, loc.y, loc.z));
	}

	@Override
	public void sendVehicleInfo(L2Player player)
	{
		if(player.isGM())
			player.sendMessage("sendVehicleInfo");
		player.sendPacket(new VehicleInfo(this));
	}

	@Override
	public void sendStopMove(L2Player player)
	{
		player.sendPacket(new StopMove(this));
	}

	@Override
	public void oustPlayers()
	{}

	@Override
	public boolean isShip()
	{
		return true;
	}
}