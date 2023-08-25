package commands.admin;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.gameserver.model.entity.vehicle.L2Vehicle;
import l2open.gameserver.model.entity.vehicle.L2VehicleManager;
import l2open.gameserver.serverpackets.ExGetOffAirShip;
import l2open.gameserver.serverpackets.ExGetOnAirShip;
import l2open.util.Location;

public class AdminBoat implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_boat_reset,
		admin_boat_reload,
		admin_airship,
		admin_airship_enter,
		admin_airship_invite,
		admin_airship_remove
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().Menu)
			return false;

		L2Vehicle boat = null;

		switch(command)
		{
			case admin_boat_reset:
				if(wordList.length == 2)
					boat = L2VehicleManager.getInstance().getBoat(Integer.parseInt(wordList[1]));
				else
					for(L2Character cha : L2World.getAroundCharacters(activeChar))
						if(cha != null && cha.isVehicle())
						{
							boat = (L2Vehicle) cha;
							break;
						}
				if(boat != null)
				{
					boat.despawn();
					boat.spawn();
				}
				break;
			case admin_boat_reload:
				for(L2Vehicle b : L2VehicleManager.getInstance().getBoats().values())
					if(b != null)
					{
						b.despawn();
						b.deleteMe();
					}
				L2VehicleManager.getInstance().getBoats().clear();
				L2VehicleManager.getInstance().loadShips();
				break;
			case admin_airship:
				boat = new L2AirShip(activeChar.getClan(), "airship", 0);
				boat.setHeading(activeChar.getHeading());
				boat.setXYZ(activeChar.getX(), activeChar.getY(), activeChar.getZ() + 333);
				L2VehicleManager.getInstance().addStaticItem(boat);
				boat.spawn();
				break;
			case admin_airship_enter:
				for(L2Character cha : L2World.getAroundCharacters(activeChar, 1000, 1000))
					if(cha.isAirShip())
					{
						activeChar._stablePoint = activeChar.getLoc().setH(0);
						L2AirShip airship = (L2AirShip) cha;
						activeChar.setInVehiclePosition();
						activeChar.setLoc(airship.getLoc());
						activeChar.setVehicle(airship);
						activeChar.broadcastPacket(new ExGetOnAirShip(activeChar, airship, activeChar.getInVehiclePosition()));
						break;
					}
				break;
			case admin_airship_invite:
				if(activeChar.getVehicle() == null)
					break;
				L2Player target = null;
				L2Object obj = activeChar.getTarget();
				if(obj != null && obj.isPlayer())
				{
					target = (L2Player) obj;
					L2AirShip airship = (L2AirShip) activeChar.getVehicle();
					target.setInVehiclePosition();
					target.setLoc(airship.getLoc());
					target.setVehicle(airship);
					target.broadcastPacket(new ExGetOnAirShip(target, airship, target.getInVehiclePosition()));
				}
				break;
			case admin_airship_remove:
				for(L2Character cha : L2World.getAroundCharacters(activeChar, 1000, 1000))
					if(cha.isAirShip())
					{
						L2AirShip airship = (L2AirShip) cha;
						for(L2Player player : L2ObjectsStorage.getPlayers())
							if(player != null && player.getVehicle() == airship)
							{
								activeChar.setVehicle(null);
								activeChar.broadcastPacket(new ExGetOffAirShip(activeChar, airship, activeChar.getLoc()));
								activeChar.teleToLocation(activeChar.getLoc());
							}
						airship.deleteMe();
						break;
					}
				break;
		}

		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}