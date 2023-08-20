package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.instancemanager.PartyRoomManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.PartyRoom;
import l2open.gameserver.serverpackets.ExPartyRoomMember;
import l2open.gameserver.serverpackets.PartyMatchList;

public class RequestPartyMatchList extends L2GameClientPacket
{
	private int _lootDist;
	private int _maxMembers;
	private int _minLevel;
	private int _maxLevel;
	private int _roomId;
	private String _roomTitle;

	/**
	 * Format:(ch) dddddS
	 */
	@Override
	public void readImpl()
	{
		_roomId = readD();
		_maxMembers = readD();
		_minLevel = readD();
		_maxLevel = readD();
		_lootDist = readD();
		_roomTitle = readS(64);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		if(System.currentTimeMillis() - activeChar.getLastRequestPartyMatchListPacket() < ConfigValue.RequestPartyMatchListPacketDelay)
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.setLastRequestPartyMatchListPacket();

		PartyRoom room = null;
		if(activeChar.getPartyRoom() == 0)
			room = PartyRoomManager.getInstance().addRoom(_minLevel, _maxLevel, _maxMembers, _lootDist, _roomTitle, activeChar);
		else if(activeChar.getPartyRoom() == _roomId)
			room = PartyRoomManager.getInstance().changeRoom(_roomId, _minLevel, _maxLevel, _maxMembers, _lootDist, _roomTitle);
		else
			return;



		activeChar.sendPacket(new PartyMatchList(room));
		activeChar.sendPacket(new ExPartyRoomMember(room, activeChar));
		activeChar.broadcastUserInfo(true);

		// TODO This packet is used to create a party room.

		/**
		if(_status == 1)
		{
			// window is open fill the list
			// actually the client should get automatic updates for the list
			// for now we only fill it once
			//Collection<L2Player> players = L2World.getPlayers();
			//L2Player[] allPlayers = players.toArray(new L2Player[players.size()]);
			L2Player[] empty = new L2Player[] {};
			ListPartyWaiting matchList = new ListPartyWaiting(empty);

			//sendPacket(new PartyMatchList(allPlayers));
			sendPacket(matchList);
		}
		else if(_status == 3)
		{
			// client does not need any more updates
			if(Config.DEBUG)
				_log.fine("PartyMatch window was closed.");
		}
		*/
	}
}