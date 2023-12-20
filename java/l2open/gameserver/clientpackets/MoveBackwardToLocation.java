package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.loginservercon.LSConnection;
import l2open.gameserver.loginservercon.gspackets.ChangeAccessLevel;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.InteractMode;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.CharMoveToLocation;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Log;

import java.util.logging.Logger;

// cdddddd(d)
public class MoveBackwardToLocation extends L2GameClientPacket {
    private static Logger _log = Logger.getLogger(MoveBackwardToLocation.class.getName());
    private int _moveMovement;

    private int _tx;
    private int _ty;
    private int _tz;
    private int _ox;
    private int _oy;
    private int _oz;

    /**
     * packet type id 0x0f
     */
    @Override
    public void readImpl() {
        _tx = readD();
        _ty = readD();
        _tz = readD();
        _ox = readD();
        _oy = readD();
        _oz = readD();

        L2GameClient client = getClient();
        L2Player activeChar = client.getActiveChar();
        if (activeChar == null)
            return;
        //_log.info("_tz="+_tz+" _oz="+_oz+" prev="+(_tz - _oz));

        if (_buf.hasRemaining())
            _moveMovement = readD();
        else {
            _log.warning("Incompatible client found: L2Walker " + client.getLoginName() + "/" + client.getIpAddr());

            if (ConfigValue.L2WalkerPunishment != 0) {
                Log.LogChar(activeChar, Log.L2WalkerFound, client.getLoginName());
                if (ConfigValue.L2WalkerPunishment == 2) {
                    LSConnection.getInstance(client.getLSId()).sendPacket(new ChangeAccessLevel(client.getLoginName(), -66, "Walker Autoban", -1));
                    activeChar.setAccessLevel(-66);
                }
                activeChar.logout(false, false, true, true);
            }
        }
    }

    @Override
    public void runImpl() {
        L2Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        // TODO: мб вылезут баги хз...
        activeChar.setFollowTarget(null);
		/*if(!GeoEngine.canSeeCoord(_ox, _oy, _oz + (int) activeChar.getColHeight() + 32, _tx, _ty, _tz, false, activeChar.getReflection().getGeoIndex()))
		{
			_log.info("canSeeCoord");
			//activeChar.sendPacket(new CharMoveToLocation(activeChar.getObjectId(), _ox, _oy, _oz, _ox, _oy, _oz));
			activeChar.sendActionFailed();
			return;
		}*/
		/*if((_tz - _oz) < -3000)
		{
			//activeChar.sendPacket(new CharMoveToLocation(activeChar.getObjectId(), _ox, _oy, _oz, _ox, _oy, _oz));
			activeChar.sendActionFailed();
			return;
		}*/

        activeChar.setActive();

        if (_moveMovement == 0 && (!ConfigValue.AllowMoveWithKeyboard/* || activeChar.getReflection().getId() > 0*/) || activeChar.p_block_controll.get() || (System.currentTimeMillis() - activeChar.getLastMovePacket() < ConfigValue.MovePacketDelay)) {
            activeChar.sendActionFailed();
            return;
        }

        activeChar.setLastMovePacket();

        if (activeChar.isTeleporting()) {
            activeChar.sendActionFailed();
            return;
        } else if (activeChar.inObserverMode()) {
            if (activeChar.getOlympiadObserveId() == -1)
                activeChar.sendActionFailed();
            else
                activeChar.sendPacket(new CharMoveToLocation(activeChar.getObjectId(), _ox, _oy, _oz, _tx, _ty, _oz + 32/*_tz*/));
            return;
        } else if (activeChar.is_block) {
            activeChar.sendMessage("Вы не можете двигаться и совершать какие-либо действия");
            activeChar.sendActionFailed();
            return;
        } else if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        } else if (activeChar.getTeleMode() > 0) {
            if (activeChar.getTeleMode() == 1)
                activeChar.setTeleMode(0);
            activeChar.sendActionFailed();
            activeChar.teleToLocation(_tx, _ty, _tz);
            //_log.info("{"+_tx+","+_ty+","+_tz+"},");
            return;
        } else if (activeChar.getInteractMode() == InteractMode.TELEPORT_NPC_TO_CURSOR) {
            activeChar.setInteractMode(null);
            final L2NpcInstance interactNpc = activeChar.getInteractNpc();
            if (interactNpc != null){
                interactNpc.teleToLocation(_tx, _ty, _tz);
                activeChar.setInteractNpc(null);
            }
            activeChar.sendActionFailed();
            return;
        } else if (activeChar.getInteractMode() == InteractMode.PET_MOVE_TO) {
            activeChar.setInteractMode(null);
            final L2Summon pet = activeChar.getPet();
            if (pet != null){
                pet.moveToLocation(_tx, _ty, _tz, 10, true);
            }
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInFlyingTransform())
            _tz = Math.min(5950, Math.max(50, _tz)); // В летающей трансформе нельзя летать ниже, чем 0, и выше, чем 6000

        if (activeChar.isInVehicle()) {
            // Чтобы не падать с летающих кораблей.
            if (activeChar.isAirShip()) {
                activeChar.sendActionFailed();
                return;
            }
            if (activeChar.getDistance(_tx, _ty, _tz) > 400) {
                activeChar.sendActionFailed();
                return;
            }
            activeChar.setVehicle(null);
            activeChar.setLastClientPosition(null);
            activeChar.setLastServerPosition(null);
            _tz = GeoEngine.getHeight(_tx, _ty, _tz + 64, 0);
            activeChar.setXYZ(_tx, _ty, _tz, false);
            activeChar.validateLocation(1);
            activeChar.stopMove(false, false);
            activeChar.sendActionFailed();
            return;
        }
        if (activeChar.isStartNoMove()) {
            // Нельзя переместиться после начала беседы. Подождите.
            activeChar.sendPacket(new SystemMessage(3226));
            activeChar.sendActionFailed();
            return;
        }
        if (ConfigValue.DebugMovePackets && activeChar.getObjectId() == 268485875) {
            int water_z = activeChar.getWaterZ();
            _log.info("---------------------------------------------------------------------------------------------------------------");
            _log.info("Client:-> MoveBackward: _originLoc.x=" + _ox + " _originLoc.y=" + _oy + " _originLoc.z=" + _oz + " water_z=" + water_z + " _moveMovement=" + _moveMovement);
            _log.info("Client:-> MoveBackward: _targetLoc.x=" + _tx + " _targetLoc.y=" + _ty + " _targetLoc.z=" + _tz + " water_z=" + water_z + " _moveMovement=" + _moveMovement);
        }
        activeChar.moveToLocation(_tx, _ty, _tz, 0, _moveMovement != 0 && !activeChar.getVarB("no_pf"));
    }
}