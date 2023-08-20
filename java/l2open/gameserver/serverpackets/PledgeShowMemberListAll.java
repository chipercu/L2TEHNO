package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.SubPledge;
import l2open.gameserver.model.L2ClanMember;
import l2open.gameserver.model.L2Player;
import l2open.util.GArray;

public class PledgeShowMemberListAll extends L2GameServerPacket
{
	private L2Clan _clan;
	private int clan_id, clan_crest_id, level, rank, rep, ally_id, ally_crest_id;
	private int _pledgeType, HasCastle, HasHideout, HasFortress, AtWar;
	private String clan_name, leader_name, ally_name;
	private GArray<ClanMemberInfo> infos = new GArray<ClanMemberInfo>();

	public PledgeShowMemberListAll(final L2Clan clan, final L2Player activeChar)
	{
		_clan = clan;
		_pledgeType = 0;
		clan_id = _clan.getClanId();
		clan_name = _clan.getName();
		leader_name = _clan.getLeaderName();
		clan_crest_id = _clan.getCrestId();
		level = _clan.getLevel();
		HasCastle = _clan.getHasCastle();
		HasHideout = _clan.getHasHideout();
		HasFortress = _clan.getHasFortress();
		rank = _clan.getRank();
		rep = _clan.getReputationScore();
		ally_id = _clan.getAllyId();
		if(_clan.getAlliance() != null)
		{
			ally_name = _clan.getAlliance().getAllyName();
			ally_crest_id = _clan.getAlliance().getAllyCrestId();
		}
		else
		{
			ally_name = "";
			ally_crest_id = 0;
		}
		AtWar = _clan.isAtWarOrUnderAttack();

		for(SubPledge element : _clan.getAllSubPledges())
			activeChar.sendPacket(new PledgeReceiveSubPledgeCreated(element));

		for(L2ClanMember m : _clan.getMembers())
			if(m.getPledgeType() == _pledgeType)
				infos.add(new ClanMemberInfo(m.getName(), m.getLevel(), m.getClassId(), m.getSex(), m.getObjectId(), m.isOnline() ? 1 : 0, m.hasSponsor() ? 1 : 0));
			else
				activeChar.sendPacket(new PledgeShowMemberListAdd(m));

		activeChar.sendUserInfo(true); // light on clan leader buttons
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x5a);

		writeD(0/*mainOrSubpledge*/); //c5 main clan 0 or any subpledge 1?
		writeD(clan_id);
		writeD(_pledgeType); //c5 - possibly pledge type?
		writeS(clan_name);
		writeS(leader_name);
		writeD(clan_crest_id); // crest id .. is used again
		writeD(level);
		writeD(HasCastle);
		if(getClient().isLindvior())
			writeD(0x00);
		writeD(HasHideout);
		writeD(HasFortress);
		writeD(rank);
		writeD(rep);
		writeD(0);
		writeD(0);
		writeD(ally_id);
		writeS(ally_name);
		writeD(ally_crest_id);
		writeD(AtWar);
		writeD(0); // Territory castle ID, GraciaFinal
		writeD(infos.size());
		for(ClanMemberInfo _info : infos)
		{
			writeS(_info._name);
			writeD(_info.level);
			writeD(_info.class_id);
			writeD(_info.sex); // Пол (не отображается)
			writeD(_info.obj_id); //writeD(1);
			writeD(_info.online); // 1=online 0=offline
			writeD(_info.has_sponsor);
		}
		infos.clear();
	}

	static class ClanMemberInfo
	{
		public String _name;
		public int level, class_id, sex, obj_id, online, has_sponsor;

		public ClanMemberInfo(String __name, int _level, int _class_id, int _sex, int _obj_id, int _online, int _has_sponsor)
		{
			_name = __name;
			level = _level;
			class_id = _class_id;
			sex = _sex;
			obj_id = _obj_id;
			online = _online;
			has_sponsor = _has_sponsor;
		}
	}
}