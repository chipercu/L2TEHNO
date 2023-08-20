package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.serverpackets.FlyToLocation.FlyType;
import l2open.util.*;

public class ValidatePosition extends L2GameClientPacket
{
	private final Location _loc = new Location();
	@SuppressWarnings("unused")
	private int _data;
	private double _diff;
	private int _dz;
	private int _h;
	private int _hz;
	private Location _lastClientPosition;
	private Location _lastServerPosition;

	/**
	 * packet type id 0x48
	 * format:		cddddd
	 */
	@Override
	public void readImpl()
	{
		_loc.x = readD();
		_loc.y = readD();
		_loc.z = readD();
		_loc.h = readD();
		_data = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isTeleporting() || activeChar.inObserverMode())
			return;

		if(ConfigValue.DebugValidatePosition && activeChar.getObjectId() == 268485875)
		{
			_log.info("DebugValidatePosition: ["+_loc.x+", "+_loc.y+", "+_loc.z+", "+_loc.h+"]["+_data+"]["+activeChar.getLoc().x+", "+activeChar.getLoc().y+", "+activeChar.getLoc().z+", "+activeChar.getLoc().h+"]");
		}
		
		_lastClientPosition = activeChar.getLastClientPosition();
		_lastServerPosition = activeChar.getLastServerPosition();

		if(_lastClientPosition == null)
			_lastClientPosition = activeChar.getLoc();
		if(_lastServerPosition == null)
			_lastServerPosition = activeChar.getLoc();

		if(activeChar.getX() == 0 && activeChar.getY() == 0 && activeChar.getZ() == 0)
		{
			correctPosition(activeChar);
			return;
		}

		if(activeChar.isInFlyingTransform())
		{
			// В летающей трансформе нельзя находиться на территории Aden
			if(_loc.x > -166168)
			{
				activeChar.setTransformation(0);
				return;
			}

			// В летающей трансформе нельзя летать ниже, чем 0, и выше, чем 6000
			if(_loc.z <= 0 || _loc.z >= 6000)
			{
				activeChar.teleToLocation(activeChar.getLoc().setZ(Math.min(5950, Math.max(50, _loc.z))));
				return;
			}
		}

		activeChar.checkTerritoryFlag();

		_diff = activeChar.getDistance(_loc.x, _loc.y);
		_dz = Math.abs(_loc.z - activeChar.getZ());
		_h = _lastServerPosition.z - activeChar.getZ();
		_hz = _lastClientPosition.z - activeChar.getZ();

		int _dz_c = Math.abs(_loc.z - _lastServerPosition.z);
		int _h_c = _lastServerPosition.z - _loc.z;
		int _hz_c = _lastClientPosition.z - _loc.z;

		if(activeChar.isInVehicle())
		{
			activeChar.setLastClientPosition(_loc.setH(activeChar.getHeading()));
			activeChar.setLastServerPosition(activeChar.getLoc());
			return;
		}

		// Если мы уже падаем, то отключаем все валидейты
		/*if(activeChar.isFalling())
		{
			_diff = 0;
			_dz = 0;
			_h = 0;
		}*/
		/**
		 * При попытке проломится через пол, при валидации координата _loc.z не меняется
		 **/
		if(ConfigValue.DebugMovePackets && activeChar.getObjectId() == 268485875)
		{
			//_log.info("Client:-> ValidatePos: _diff["+getDirectionTo(activeChar, _loc.x, _loc.y)+"]="+((int)_diff)+" _h="+_h+" _dz="+_dz+" _hz="+_hz+" _dz_c="+_dz_c+" _h_c="+_h_c+" _hz_c="+_hz_c+" _loc: "+_loc);
			
		}
		if(_h >= 256/* || _dz >= 512*/ && !activeChar.isFalling() && _hz_c > 5) // Пока падаем, высоту не корректируем
			activeChar.falling(_h > _dz ? _h : _dz);
		else if(_h > 128 && _dz > 512)
			activeChar.broadcastMove(false, 0, 0, 0, 0, true, false);
		else if(!activeChar.isInWater() && _dz >= ConfigValue.GeoTestDeltaZ/*(activeChar.isFlying() ? 1024 : 512)*/ && _diff >= ConfigValue.GeoTestDiffZ)
		{
			int type = activeChar.getIncorrectValidateCount();
			if(ConfigValue.GeoTest != 1000)
				type = ConfigValue.GeoTest;
			Location pos;
			switch(type)
			{
				case 0:
				case 1:
					if(ConfigValue.DebugMovePackets && activeChar.getObjectId() == 268485875)
						_log.info("teleToLocation("+activeChar.getIncorrectValidateCount()+"): LastServerPosition");
					if(activeChar.getLastServerPosition() == null)
						pos = activeChar.getLoc();
					else
						pos = activeChar.getLastServerPosition();
					break;
				case 2:
				case 3:
					if(ConfigValue.DebugMovePackets && activeChar.getObjectId() == 268485875)
						_log.info("teleToLocation("+activeChar.getIncorrectValidateCount()+"): Validation Loc");
					pos = activeChar.getLoc();
					break;
				case 4:
					if(ConfigValue.DebugMovePackets && activeChar.getObjectId() == 268485875)
						_log.info("teleToLocation("+activeChar.getIncorrectValidateCount()+"): GEO: LastServerPosition");
					if(activeChar.getLastServerPosition() == null)
						pos = GeoEngine.findPointToStay(activeChar.getLoc().x, activeChar.getLoc().y, activeChar.getLoc().z+32, 0, 100, activeChar.getReflection().getGeoIndex());
					else
						pos = GeoEngine.findPointToStay(activeChar.getLastServerPosition().x, activeChar.getLastServerPosition().y, activeChar.getLastServerPosition().z+32, 0, 100, activeChar.getReflection().getGeoIndex());
					break;
				case 5:
					if(ConfigValue.DebugMovePackets && activeChar.getObjectId() == 268485875)
						_log.info("teleToLocation("+activeChar.getIncorrectValidateCount()+"): GEO: CharLoc");
					pos = GeoEngine.findPointToStay(activeChar.getLoc().x, activeChar.getLoc().y, activeChar.getLoc().z+32, 0, 100, activeChar.getReflection().getGeoIndex());
					break;
				case 6:
					if(ConfigValue.DebugMovePackets && activeChar.getObjectId() == 268485875)
						_log.info("teleToLocation("+activeChar.getIncorrectValidateCount()+"): CharLoc");					
					if(activeChar.getLastServerPosition() == null)
						pos = GeoEngine.findPointToStay(activeChar.getLoc().x, activeChar.getLoc().y, activeChar.getLoc().z+64, 0, 100, activeChar.getReflection().getGeoIndex());
					else
						pos = GeoEngine.findPointToStay(activeChar.getLastServerPosition().x, activeChar.getLastServerPosition().y, activeChar.getLastServerPosition().z+64, 0, 100, activeChar.getReflection().getGeoIndex());
					break;
				default:
					if(ConfigValue.DebugMovePackets && activeChar.getObjectId() == 268485875)
						_log.info("teleToLocation("+activeChar.getIncorrectValidateCount()+"): CharLoc");
					if(activeChar.getLastClientPosition() == null)
						pos = GeoEngine.findPointToStay(activeChar.getLoc().x, activeChar.getLoc().y, activeChar.getLoc().z+32, 0, Math.min((int)(100+(type*1.5)), 200), activeChar.getReflection().getGeoIndex());
					else
						pos = activeChar.getLastClientPosition();
					break;
			}
			int tempz = GeoEngine.getHeight(pos.x, pos.y, pos.z, activeChar.getReflection().getGeoIndex());
			activeChar.teleToLocation(pos.x, pos.y, tempz+32, activeChar.getReflection().getId(), (type%3) == 0 ? 0 : 1);
			activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
			if(ConfigValue.DebugMovePackets && activeChar.getObjectId() == 268485875)
				activeChar.sendMessage("setIncorrectValidateCount1: " + activeChar.getIncorrectValidateCount());
			return;
		}
		else if(_dz >= (activeChar.isFlying() ? 1024 : 512))
		{
			activeChar.validateLocation(0);
		}
		else if(_loc.z < -30000 || _loc.z > 30000)
		{
			if(activeChar.getIncorrectValidateCount() >= 3)
				activeChar.teleToClosestTown();
			else
			{
				if(activeChar.isGM())
				{
					activeChar.sendMessage("Client Z: " + _loc.z);
					activeChar.sendMessage("Server Z: " + activeChar.getZ());
				}
				correctPosition(activeChar);
				activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
			}
		}
		else if(_diff > 1024)
		{
			//if(activeChar.getIncorrectValidateCount() >= 3)
			//	activeChar.teleToClosestTown();
			//else
			{
				activeChar.teleToLocation(activeChar.getX(), activeChar.getY(), activeChar.getZ()+32, activeChar.getReflection().getId(), 1);
				activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
				if(ConfigValue.DebugMovePackets && activeChar.getObjectId() == 268485875)
					activeChar.sendMessage("setIncorrectValidateCount2: " + activeChar.getIncorrectValidateCount());
			}
		}
		else if(_diff > 256) // 512 - на ПТС
		{
			// Не рассылаем валидацию во время скиллов с полетом
			if (activeChar.isCastingNow())
			{
				L2Skill skill = activeChar.getCastingSkill();
				if(skill != null && skill.getFlyType() != FlyType.NONE)
					return;
			}
			//  && !activeChar.isFlying() && !activeChar.isInBoat()
			//TODO реализовать NetPing и вычислять предельное отклонение исходя из пинга по формуле: 16 + (ping * activeChar.getMoveSpeed()) / 1000
			//_log.info("ValidatePosition: _diff="+_diff+" x="+_loc.x+", y="+_loc.y+"|x="+activeChar.getX()+", y="+activeChar.getY());
			activeChar.validateLocation(0);
			//activeChar.stopMove(false, true);
		/*if(activeChar.isMoving)
			activeChar.broadcastPacket(new CharMoveToLocation(activeChar));
		else
			activeChar.broadcastPacket(new ValidateLocation(activeChar));*/
		}
		else
			activeChar.setIncorrectValidateCount(0);

			//double complete = Math.floor(activeChar._move_data._previous_speed/1000 * (time_now - activeChar._startMoveTime));
			
			//activeChar.getMoveTickInterval(distance, (float)complete, (float)dist, /*character.moveList.size()-1*/0, index, false)

		/*if(activeChar._move_data != null && ConfigValue.DebugMoveIsPlayer)
		{
			// от клиента до конечной
			double diff_client = activeChar._move_data.distance-_loc.distance(activeChar._move_data._x_destination, activeChar._move_data._y_destination);

			// от игрока до конечной
			double diff_char = activeChar._move_data.distance-activeChar.getDistance(activeChar._move_data._x_destination, activeChar._move_data._y_destination);

			// от игрока до клиента
			//_diff;

			double complete = Math.ceil(activeChar._move_data._previous_speed/1000 * (System.currentTimeMillis() - activeChar._move_data._start_time));
			//_log.info("ValidatePosition: diff="+_diff+" diff_client="+diff_client+" diff_char="+diff_char+" done["+activeChar._move_data.done_distance+"]="+complete+" delta="+(activeChar._move_data._previous_speed/1000 * (System.currentTimeMillis() - activeChar._move_data._start_time)));

			//if(_diff > 1)
			//{
			//	if(diff_client > diff_char)
			//		activeChar._startMoveTime = activeChar._followTimestamp = (long)(activeChar._startMoveTime+(_diff/(activeChar.getMoveSpeed()/1000)));
			//	else
			//		activeChar._startMoveTime = activeChar._followTimestamp = (long)(activeChar._startMoveTime-(_diff/(activeChar.getMoveSpeed()/1000)));
			//}
			//activeChar._move_data._previous_speed = Math.max(activeChar.getMoveSpeed()-_diff, 1.0F);
		}*/

		//if(_diff < 64)
		//	activeChar.setXYZInvisible(_loc.x, _loc.y, _loc.z);
		//activeChar.setXYZ(activeChar.getLoc().x, activeChar.getLoc().y, _loc.z, false);
		

		activeChar.checkWaterState();

		if(activeChar.getPet() != null && !activeChar.getPet().isInRange())
			activeChar.getPet().teleportToOwner();

		activeChar.setLastClientPosition(_loc.setH(activeChar.getHeading()));
		activeChar.setLastServerPosition(activeChar.getLoc());

		if(activeChar.isTerritoryFlagEquipped() && TerritorySiege.isInProgress())
			TerritorySiege.setWardLoc(activeChar.getActiveWeaponInstance().getItemId() - 13559, activeChar.getLoc());
		
		//activeChar.validateLocation(2);
	}

	private void correctPosition(L2Player activeChar)
	{
		if(activeChar.isGM())
		{
			activeChar.sendMessage("Server loc: " + activeChar.getLoc());
			activeChar.sendMessage("Correcting position...");
		}
		if(_lastServerPosition.x != 0 && _lastServerPosition.y != 0 && _lastServerPosition.z != 0)
		{
			if(GeoEngine.getNSWE(_lastServerPosition.x, _lastServerPosition.y, _lastServerPosition.z, activeChar.getReflection().getGeoIndex()) == 15)
				activeChar.teleToLocation(_lastServerPosition);
			else
				activeChar.teleToClosestTown();
		}
		else if(_lastClientPosition.x != 0 && _lastClientPosition.y != 0 && _lastClientPosition.z != 0)
		{
			if(GeoEngine.getNSWE(_lastClientPosition.x, _lastClientPosition.y, _lastClientPosition.z, activeChar.getReflection().getGeoIndex()) == 15)
				activeChar.teleToLocation(_lastClientPosition);
			else
				activeChar.teleToClosestTown();
		}
		else
			activeChar.teleToClosestTown();
	}

	public enum TargetDirection
	{
		NONE,
		FRONT,
		SIDE,
		BEHIND
	}

	public static TargetDirection getDirectionTo(L2Character attacker, int x, int y)
	{
		if(attacker == null)
			return TargetDirection.NONE;
		if(isFacing(attacker, x, y, 90*2))
			return TargetDirection.BEHIND;
		if(isFacing(attacker, x, y, 70*2))
			return TargetDirection.FRONT;
		return TargetDirection.SIDE;
	}

	public static boolean isFacing(L2Character attacker, int x, int y, int maxAngle)
	{
		double angleChar, angleTarget, angleDiff, maxAngleDiff;
		maxAngleDiff = maxAngle / 2;
		angleTarget = Util.calculateAngleFrom(attacker.getX(), attacker.getY(), x, y);
		angleChar = Util.convertHeadingToDegree(attacker.getHeading());
		angleDiff = angleChar - angleTarget;
		if(angleDiff <= -360 + maxAngleDiff)
			angleDiff += 360;
		if(angleDiff >= 360 - maxAngleDiff)
			angleDiff -= 360;
		if(Math.abs(angleDiff) <= maxAngleDiff)
			return true;
		return false;
	}

	/*@Override
	public String getType()
	{
		return super.getType()+"["+_loc.x+", "+_loc.y+", "+_loc.z+", "+_loc.h+"]["+_data+"]";
	}*/
}