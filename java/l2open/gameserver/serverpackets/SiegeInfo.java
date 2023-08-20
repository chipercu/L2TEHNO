package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Alliance;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.*;
import l2open.gameserver.model.entity.siege.castle.CastleSiege;
import l2open.gameserver.tables.ClanTable;

import java.util.Calendar;
import java.util.logging.Logger;

/**15576  -49176  -1056
 * Shows the Siege Info<BR>
 * <BR>
 * packet type id 0xc9<BR>
 * format: cdddSSdSdd<BR>
 * <BR>
 * c = c9<BR>
 * d = UnitID<BR>
 * d = Show Owner Controls (0x00 default || >=0x02(mask?) owner)<BR>
 * d = Owner ClanID<BR>
 * S = Owner ClanName<BR>
 * S = Owner Clan LeaderName<BR>
 * d = Owner AllyID<BR>
 * S = Owner AllyName<BR>
 * d = current time (seconds)<BR>
 * d = Siege time (seconds) (0 for selectable)<BR>
 * d = (UNKNOW) Siege Time Select Related
 */
public class SiegeInfo extends L2GameServerPacket
{
	private static Logger _log = Logger.getLogger(SiegeInfo.class.getName());
	private long _startTime;
	private int _id = 0;
	private int _owner;
	private String _ownerName;
	private String _leaderName;
	private String _allyNname;
	private int _allyId;
	private int[] _nextTimeMillis;

	public SiegeInfo(Residence unit)
	{
		if(unit == null || unit.getSiege() == null)
			return;

		_ownerName = "NPC";
		_leaderName = "";
		_allyNname = "";
		_allyId = 0;

		_id = unit.getId();
		_owner = unit.getOwnerId();
		if(unit.getOwnerId() > 0)
		{
			L2Clan owner = ClanTable.getInstance().getClan(_owner);
			if(owner != null)
			{
				_ownerName = owner.getName();
				_leaderName = owner.getLeaderName();
				if(owner.getAllyId() != 0)
				{
					L2Alliance alliance = ClanTable.getInstance().getAlliance(owner.getAllyId());
					if(alliance != null)
					{
						_allyId = alliance.getAllyId();
						_allyNname = alliance.getAllyName();
					}
				}
			}
			else
				_log.warning("Null owner for unit: " + unit.getName());
		}

		if(unit.getType() == ResidenceType.Fortress)
		{
			if(System.currentTimeMillis() < unit.getSiege().getSiegeDate().getTimeInMillis())
				_startTime = (int) (unit.getSiege().getSiegeDate().getTimeInMillis() / 1000);
			else if(unit.getLastSiegeDate() * 1000L + 10800000L < System.currentTimeMillis())
				_startTime = (int) (System.currentTimeMillis() / 1000) + 3600;
			else
				_startTime = unit.getLastSiegeDate() + 14400;
		}
		else
		{
			long siegeTimeMillis = unit.getSiege().getSiegeDate().getTimeInMillis();
			if(unit.getType() == ResidenceType.Castle && ((Castle)unit)._setNewData == 0 && ((CastleSiege)unit.getSiege()).getNextSiegeTimes().length > 0)
				_nextTimeMillis = ((CastleSiege)unit.getSiege()).getNextSiegeTimes();
			else
				_startTime = (int)(siegeTimeMillis / 1000);
		}
	}

	@Override
	protected void writeImpl()
	{
		if(_id == 0)
			return;
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		writeC(0xC9);
		writeD(_id);
		writeD(_owner == activeChar.getClanId() && activeChar.isClanLeader() ? 0x01 : 0x00);
		writeD(_owner);

		writeS(_ownerName); // Clan Name
		writeS(_leaderName); // Clan Leader Name
		writeD(_allyId); // Ally ID
		writeS(_allyNname); // Ally Name
		writeD((int) (Calendar.getInstance().getTimeInMillis() / 1000));
		writeD((int) _startTime);
		if(_startTime == 0)
		{
			writeD(_nextTimeMillis.length);
			for(int val : _nextTimeMillis)
				writeD(val);
		}
	}
}