package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Alliance;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ClanTable;
import l2open.util.GArray;

public class RequestAllyInfo extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player _cha = getClient().getActiveChar();
		if(_cha == null)
			return;

		L2Alliance ally = _cha.getAlliance();
		if(ally == null)
			return;

		int clancount = 0;
		L2Clan leaderclan = _cha.getAlliance().getLeader();
		clancount = ClanTable.getInstance().getAlliance(leaderclan.getAllyId()).getMembers().length;
		int[] online = new int[clancount + 1];
		int[] count = new int[clancount + 1];
		L2Clan[] clans = _cha.getAlliance().getMembers();
		for(int i = 0; i < clancount; i++)
		{
			online[i + 1] = clans[i].getOnlineMembers(0).length;
			count[i + 1] = clans[i].getMembers().length;
			online[0] += online[i + 1];
			count[0] += count[i + 1];
		}

		GArray<L2GameServerPacket> packets = new GArray<L2GameServerPacket>(7 + 5 * clancount);
		packets.add(Msg._ALLIANCE_INFORMATION_);
		packets.add(new SystemMessage(SystemMessage.ALLIANCE_NAME_S1).addString(_cha.getClan().getAlliance().getAllyName()));
		packets.add(new SystemMessage(SystemMessage.CONNECTION_S1_TOTAL_S2).addNumber(online[0]).addNumber(count[0])); //Connection
		packets.add(new SystemMessage(SystemMessage.ALLIANCE_LEADER_S2_OF_S1).addString(leaderclan.getName()).addString(leaderclan.getLeaderName()));
		packets.add(new SystemMessage(SystemMessage.AFFILIATED_CLANS_TOTAL_S1_CLAN_S).addNumber(clancount)); //clan count
		packets.add(Msg._CLAN_INFORMATION_);
		for(int i = 0; i < clancount; i++)
		{
			packets.add(new SystemMessage(SystemMessage.CLAN_NAME_S1).addString(clans[i].getName()));
			packets.add(new SystemMessage(SystemMessage.CLAN_LEADER_S1).addString(clans[i].getLeaderName()));
			packets.add(new SystemMessage(SystemMessage.CLAN_LEVEL_S1).addNumber(clans[i].getLevel()));
			packets.add(new SystemMessage(SystemMessage.CONNECTION_S1_TOTAL_S2).addNumber(online[i + 1]).addNumber(count[i + 1]));
			packets.add(Msg.__DASHES__);
		}
		packets.add(Msg.__EQUALS__);

		_cha.sendPackets(packets);
	}
}