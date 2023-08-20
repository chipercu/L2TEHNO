package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.residence.ResidenceType;
import l2open.gameserver.model.entity.siege.SiegeClan;
import l2open.gameserver.tables.ClanTable;

/**
 * Populates the Siege Defender List in the SiegeInfo Window<BR>
 * <BR>
 * packet type id 0xcb<BR>
 * format: cddddddd + dSSdddSSd<BR>
 * <BR>
 * c = 0xcb<BR>
 * d = unitId<BR>
 * d = unknow (0x00)<BR>
 * d = активация регистрации (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Defending Clans?<BR>
 * d = Number of Defending Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = Type -> Owner = 0x01 || Waiting = 0x02 || Accepted = 0x03 || Refuse = 0x04<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 */
public class SiegeDefenderList extends L2GameServerPacket
{
	private Residence _unit;

	public SiegeDefenderList(Residence unit)
	{
		_unit = unit;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xCB);
		writeD(_unit.getId());
		writeD(0x00); // 0
		writeD(0x01); // 1 активация регистрации
		writeD(0x00); // 0

		if(_unit.getType() == ResidenceType.Clanhall)
		{
			writeD(0x00);
			writeD(0x00);
			return;
		}

		int size = _unit.getSiege().getDefenderClans().size() + _unit.getSiege().getDefenderWaitingClans().size() + _unit.getSiege().getDefenderRefusedClans().size();
		if(size > 0)
		{
			L2Clan clan;

			writeD(size);
			writeD(size);
			// Listing the Lord and the approved clans
			for(SiegeClan siegeclan : _unit.getSiege().getDefenderClans().values())
			{
				clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
				if(clan == null)
					continue;

				writeD(clan.getClanId());
				writeS(clan.getName());
				writeS(clan.getLeaderName());
				writeD(clan.getCrestId());
				writeD(0x00); // signed time (seconds) (not storated by L2F)
				switch(siegeclan.getType())
				{
					case OWNER:
						writeD(0x01); // owner
						break;
					case DEFENDER_WAITING:
						writeD(0x02); // waiting approved
						break;
					case DEFENDER:
						writeD(0x03); // approved
						break;
					case DEFENDER_REFUSED:
						writeD(0x04); // refuse
						break;
					default:
						writeD(0x00);
						break;
				}
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
			for(SiegeClan siegeclan : _unit.getSiege().getDefenderWaitingClans().values())
			{
				clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
				if(clan == null)
					continue;
				writeD(clan.getClanId());
				writeS(clan.getName());
				writeS(clan.getLeaderName());
				writeD(clan.getCrestId());
				writeD(0x00); // signed time (seconds) (not storated by L2F)
				writeD(0x02); // waiting approval
				writeD(clan.getAllyId());
				if(clan.getAlliance() != null)
				{
					writeS(clan.getAlliance().getAllyName());
					writeS(clan.getAlliance().getAllyLeaderName()); //AllyLeaderName
					writeD(clan.getAlliance().getAllyCrestId());
				}
				else
				{
					writeS("");
					writeS("");
					writeD(0);
				}
			}
			for(SiegeClan siegeclan : _unit.getSiege().getDefenderRefusedClans().values())
			{
				clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
				if(clan == null)
					continue;
				writeD(clan.getClanId());
				writeS(clan.getName());
				writeS(clan.getLeaderName());
				writeD(clan.getCrestId());
				writeD(0x00); // signed time (seconds) (not storated by L2F)
				writeD(0x04); // waiting approval
				writeD(clan.getAllyId());
				if(clan.getAlliance() != null)
				{
					writeS(clan.getAlliance().getAllyName());
					writeS(clan.getAlliance().getAllyLeaderName()); //AllyLeaderName
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
		else
		{
			writeD(0x00);
			writeD(0x00);
		}
	}
}