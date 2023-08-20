package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.siege.SiegeClan;
import l2open.gameserver.tables.ClanTable;

/**
 * Populates the Siege Attacker List in the SiegeInfo Window<BR>
 * <BR>
 * packet type id 0xca<BR>
 * format: cddddddd + dSSdddSSd<BR>
 * <BR>
 * c = ca<BR>
 * d = CastleID<BR>
 * d = unknow (0x00)<BR>
 * d = unknow (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Attackers Clans?<BR>
 * d = Number of Attackers Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 */
public class SiegeAttackerList extends L2GameServerPacket
{
	private Residence _unit;

	public SiegeAttackerList(Residence unit)
	{
		_unit = unit;
	}

	@Override
	protected final void writeImpl()
	{
		if(_unit == null || _unit.getSiege() == null || _unit.getSiege().getAttackerClans() == null)
			return;

		writeC(0xCA);

		writeD(_unit.getId());

		writeD(0x00); //0
		writeD(0x01); //1
		writeD(0x00); //0

		writeD(_unit.getSiege().getAttackerClans().size());
		writeD(_unit.getSiege().getAttackerClans().size());

		for(SiegeClan siegeclan : _unit.getSiege().getAttackerClans().values())
		{
			L2Clan clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
			if(clan == null)
				continue;

			writeD(clan.getClanId());
			writeS(clan.getName());
			writeS(clan.getLeaderName());
			writeD(clan.getCrestId());
			writeD(0x00); // signed time (seconds) (not storated by L2F)
			writeD(clan.getAllyId());
			if(clan.getAlliance() != null)
			{
				writeS(clan.getAlliance().getAllyName());
				writeS(clan.getAlliance().getAllyLeaderName()); // AllyLeaderName
				writeD(clan.getAlliance().getAllyCrestId());
			}
			else
			{
				writeS("");
				writeS("");
				writeD(0);
			}
		}
	}
}