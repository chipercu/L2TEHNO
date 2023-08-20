package l2open.gameserver.model;

import l2open.gameserver.model.RankPrivs;
import l2open.gameserver.model.SubPledge;
import l2open.gameserver.tables.player.PlayerData;

import java.lang.ref.WeakReference;

public class L2ClanMember
{
	private WeakReference<L2Clan> _clan;
	private String _name;
	private String _title;
	private int _level;
	private int _classId;
	private int _sex;
	private long _lastAccess;
	public int _pledgeType;
	public int _powerGrade;
	public int _apprentice;
	private int _race;
	public int _pvp;
	private int _pk;
	private Boolean _clanLeader;

	private int _objectId;
	public int fraction_id=-1;
	private L2Player _player;

	public L2ClanMember(L2Clan clan, String name, String title, int level, int classId, int objectId, L2Player player, int pledgeType, int powerGrade, int apprentice, Boolean clanLeader, long last_access, int pvp, int pk, int f_id)
	{
		_clan = new WeakReference<L2Clan>(clan);
		_name = name;
		_title = title;
		_level = level;
		_classId = classId;
		_pledgeType = pledgeType;
		_powerGrade = powerGrade;
		_apprentice = apprentice;
		_clanLeader = clanLeader;
		_lastAccess = last_access;
		_objectId = objectId;
		_player = player;
		if(powerGrade != 0)
		{
			RankPrivs r = clan.getRankPrivs(powerGrade);
			r.setParty(clan.countMembersByRank(powerGrade));
		}
		_pvp = pvp;
		_pk = pk;
		fraction_id = f_id;
	}

	public L2ClanMember(L2Player player)
	{
		_objectId = player.getObjectId();
		_player = player;
	}

	public void setPlayerInstance(L2Player player, boolean exit)
	{
		_player = exit ? null : player;
		if(player == null)
			return;

		// this is here to keep the data when the player logs off
		_clan = new WeakReference<L2Clan>(player.getClan());
		_name = player.getName();
		_title = player.getTitle();
		_level = player.getLevel();
		_classId = player.getClassId().getId();
		_pledgeType = player.getPledgeType();
		_powerGrade = player.getPowerGrade();
		_apprentice = player.getApprentice();
		_clanLeader = player.isClanLeader();
		_pvp = player.getPvpKills();
		_pk = player.getPkKills();
		setRace(player.getRace().ordinal());
	}

	public void setRace(int race)
	{
		_race = race;
	}

	public int getRace()
	{
		return _race;
	}

	public L2Player getPlayer()
	{
		return _player;
	}

	public boolean isOnline()
	{
		L2Player player = getPlayer();
		return player != null && !player.isInOfflineMode();
	}

	public L2Clan getClan()
	{
		L2Player player = getPlayer();
		return player == null ? _clan.get() : player.getClan();
	}

	public int getClassId()
	{
		L2Player player = getPlayer();
		return player == null ? _classId : player.getClassId().getId();
	}

	public int getSex()
	{
		L2Player player = getPlayer();
		return player == null ? _sex : player.getSex();
	}

	public int getLevel()
	{
		L2Player player = getPlayer();
		return player == null ? _level : player.getLevel();
	}
	
	public int getPvp()
	{
		L2Player player = getPlayer();
		return player == null ? _pvp : player.getPvpKills();
	}
	
	public int getPk()
	{
		L2Player player = getPlayer();
		return player == null ? _pk : player.getPkKills();
	}

	public String getName()
	{
		L2Player player = getPlayer();
		return player == null ? _name : player.getName();
	}

	public long getLastAccess()
	{
		L2Player player = getPlayer();
		return player == null ? _lastAccess : player.getLastAccess();
	}

	public int getObjectId()
	{
		return _objectId;
	}

	public String getTitle()
	{
		L2Player player = getPlayer();
		return player == null ? _title : player.getTitle();
	}

	public void setTitle(String title)
	{
		L2Player player = getPlayer();
		_title = title;
		if(player != null)
		{
			player.setTitle(title, true);
			player.sendChanges();
		}
		else
			PlayerData.getInstance().setTitleClanMember(this, title);
	}

	public int getPledgeType()
	{
		L2Player player = getPlayer();
		return player == null ? _pledgeType : player.getPledgeType();
	}

	public void setPledgeType(int pledgeType)
	{
		L2Player player = getPlayer();
		_pledgeType = pledgeType;
		if(player != null)
			player.setPledgeType(pledgeType);
		else
			PlayerData.getInstance().updatePledgeType(this);
	}

	public int getPowerGrade()
	{
		L2Player player = getPlayer();
		return player == null ? _powerGrade : player.getPowerGrade();
	}

	public void setPowerGrade(int newPowerGrade)
	{
		L2Player player = getPlayer();
		int oldPowerGrade = getPowerGrade();
		_powerGrade = newPowerGrade;
		if(player != null)
			player.setPowerGrade(newPowerGrade);
		else
			PlayerData.getInstance().updatePowerGrade(this);
		updatePowerGradeParty(oldPowerGrade, newPowerGrade);
	}

	private void updatePowerGradeParty(int oldGrade, int newGrade)
	{
		if(oldGrade != 0)
		{
			RankPrivs r1 = getClan().getRankPrivs(oldGrade);
			r1.setParty(getClan().countMembersByRank(oldGrade));
		}
		if(newGrade != 0)
		{
			RankPrivs r2 = getClan().getRankPrivs(newGrade);
			r2.setParty(getClan().countMembersByRank(newGrade));
		}
	}

	private int getApprentice()
	{
		L2Player player = getPlayer();
		return player == null ? _apprentice : player.getApprentice();
	}

	public void setApprentice(int apprentice)
	{
		L2Player player = getPlayer();
		_apprentice = apprentice;
		if(player != null)
			player.setApprentice(apprentice);
		else
			PlayerData.getInstance().updateApprentice(this);
	}

	public String getApprenticeName()
	{
		if(getApprentice() != 0)
			if(getClan().getClanMember(getApprentice()) != null)
				return getClan().getClanMember(getApprentice()).getName();
		return "";
	}

	public boolean hasApprentice()
	{
		return getApprentice() != 0;
	}

	public int getSponsor()
	{
		if(getPledgeType() != L2Clan.SUBUNIT_ACADEMY)
			return 0;
		int id = getObjectId();
		for(L2ClanMember element : getClan().getMembers())
			if(element.getApprentice() == id)
				return element.getObjectId();
		return 0;
	}

	private String getSponsorName()
	{
		int sponsorId = getSponsor();
		if(sponsorId == 0)
			return "";
		else if(getClan().getClanMember(sponsorId) != null)
			return getClan().getClanMember(sponsorId).getName();
		return "";
	}

	public boolean hasSponsor()
	{
		return getSponsor() != 0;
	}

	public String getRelatedName()
	{
		if(getPledgeType() == L2Clan.SUBUNIT_ACADEMY)
			return getSponsorName();
		return getApprenticeName();
	}

	public boolean isClanLeader()
	{
		L2Player player = getPlayer();
		return player == null ? _clanLeader : player.isClanLeader();
	}

	public int isSubLeader()
	{
		for(SubPledge pledge : getClan().getAllSubPledges())
			if(pledge.getLeaderId() == getObjectId())
				return pledge.getType();
		return 0;
	}
}