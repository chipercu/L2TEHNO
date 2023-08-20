package l2open.gameserver.model;

import javolution.util.FastMap;
import l2open.config.*;
import l2open.database.*;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.AuctionManager;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.FortressManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.barahlo.academ.*;
import l2open.gameserver.model.entity.residence.*;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.gameserver.model.items.ClanWarehouse;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemClass;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.util.GArray;
import l2open.util.Log;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class L2Clan
{
	private static final Logger _log = Logger.getLogger(L2Clan.class.getName());

	private String _name;
	private int _clanId;
	private L2ClanMember _leader = null;
	public Map<Integer, L2ClanMember> _members = new ConcurrentHashMap<Integer, L2ClanMember>();

	private int _allyId;
	public byte _level;
	private int _hasCastle = 0;
	private int _hasFortress = 0;
	private int _hiredGuards;
	private int _hasHideout = 0;
	private int _crestId;
	private int _crestLargeId;

	private long _expelledMemberTime;
	private long _leavedAllyTime;
	private long _dissolvedAllyTime;
	private L2AirShip _airship;
	private boolean _airshipLicense;
	private int _airshipFuel;

	private ClanWarehouse _warehouse = new ClanWarehouse(this);
	private int _whBonus = -1;

	private GArray<L2Clan> _atWarWith = new GArray<L2Clan>();
	private GArray<L2Clan> _underAttackFrom = new GArray<L2Clan>();

	public FastMap<Integer, L2Skill> _skills = new FastMap<Integer, L2Skill>().setShared(true);
	public FastMap<Integer, FastMap<Integer, L2Skill>> _squadSkills = new FastMap<Integer, FastMap<Integer, L2Skill>>().setShared(true);
	public FastMap<Integer, RankPrivs> _Privs = new FastMap<Integer, RankPrivs>().setShared(true);
	public FastMap<Integer, SubPledge> _SubPledges = new FastMap<Integer, SubPledge>().setShared(true);

	public int _reputation = 0;

	//	Clan Privileges: system
	public static final int CP_NOTHING = 0;
	public static final int CP_CL_INVITE_CLAN = 2; // Join clan
	public static final int CP_CL_MANAGE_TITLES = 4; // Give a title
	public static final int CP_CL_WAREHOUSE_SEARCH = 8; // View warehouse content
	public static final int CP_CL_MANAGE_RANKS = 16; // manage clan ranks
	public static final int CP_CL_CLAN_WAR = 32;
	public static final int CP_CL_DISMISS = 64;
	public static final int CP_CL_EDIT_CREST = 128; // Edit clan crest
	public static final int CP_CL_APPRENTICE = 256;
	public static final int CP_CL_TROOPS_FAME = 512;
	public static final int CP_CL_SUMMON_AIRSHIP = 1024;

	//	Clan Privileges: clan hall
	public static final int CP_CH_ENTRY_EXIT = 2048; // open a door
	public static final int CP_CH_USE_FUNCTIONS = 4096;
	public static final int CP_CH_AUCTION = 8192;
	public static final int CP_CH_DISMISS = 16384; // Выгнать чужаков из КХ
	public static final int CP_CH_SET_FUNCTIONS = 32768;

	//	Clan Privileges: castle/fotress
	public static final int CP_CS_ENTRY_EXIT = 65536;
	public static final int CP_CS_MANOR_ADMIN = 131072;
	public static final int CP_CS_MANAGE_SIEGE = 262144;
	public static final int CP_CS_USE_FUNCTIONS = 524288;
	public static final int CP_CS_DISMISS = 1048576; // Выгнать чужаков из замка/форта
	public static final int CP_CS_TAXES = 2097152;
	public static final int CP_CS_MERCENARIES = 4194304;
	public static final int CP_CS_SET_FUNCTIONS = 8388606;
	public static final int CP_ALL = 16777214;

	public static final int RANK_FIRST = 1;
	public static final int RANK_LAST = 9;

	// Sub-unit types
	public static final int SUBUNIT_ACADEMY = -1;
	public static final int SUBUNIT_NONE = 0;
	public static final int SUBUNIT_ROYAL1 = 100;
	public static final int SUBUNIT_ROYAL2 = 200;
	public static final int SUBUNIT_KNIGHT1 = 1001;
	public static final int SUBUNIT_KNIGHT2 = 1002;
	public static final int SUBUNIT_KNIGHT3 = 2001;
	public static final int SUBUNIT_KNIGHT4 = 2002;

	private final static ClanReputationComparator REPUTATION_COMPARATOR = new ClanReputationComparator();
	/** Количество мест в таблице рангов кланов */
	private final static int REPUTATION_PLACES = 100;

	public String _notice;
	public static final int NOTICE_MAX_LENGHT = 512;

	public int clan_point = 20;
	public int i_ai0 = 0;
	public boolean _auto_war = false;

	public L2Clan(int clanId)
	{
		_clanId = clanId;
		InitializePrivs();
	}

	public L2Clan(int clanId, String clanName, L2ClanMember leader)
	{
		_clanId = clanId;
		_name = clanName;
		InitializePrivs();
		setLeader(leader, true);
		PlayerData.getInstance().insertNotice(this);
	}

	public int getClanId()
	{
		return _clanId;
	}

	public void setClanId(int clanId)
	{
		_clanId = clanId;
	}

	public int getLeaderId()
	{
		return _leader != null ? _leader.getObjectId() : 0;
	}

	public L2ClanMember getLeader()
	{
		return _leader;
	}

	public void setLeader(L2ClanMember leader, boolean set_lord)
	{
		_leader = leader;
		_members.put(leader.getObjectId(), leader);
		if(set_lord && _hasCastle != 0 && CastleManager.getInstance().getCastleByIndex(_hasCastle).getDominionLord() > 0)
			CastleManager.getInstance().getCastleByIndex(_hasCastle).setDominionLord(leader.getObjectId(), true);
	}

	public String getLeaderName()
	{
		return _leader.getName();
	}

	public String getName()
	{
		return _name;
	}

	public void setName(String name)
	{
		_name = name;
	}

	public void addClanMember(L2ClanMember member)
	{
		_members.put(member.getObjectId(), member);
	}

	public void addClanMember(L2Player player)
	{
		addClanMember(new L2ClanMember(this, player.getName(), player.getTitle(), player.getLevel(), player.getClassId().getId(), player.getObjectId(), player, player.getPledgeType(), player.getPowerGrade(), player.getApprentice(), false, player.getLastAccess(), player.getPvpKills(), player.getPkKills(), 0));
		if(ConfigValue.EnableClanPoint)
		{
			clan_point -= ConfigValue.ClanPointCountEnterClan;
			PlayerData.getInstance().updateClanInDB(this);
			sendMessageToAll("Потеряно "+ConfigValue.ClanPointCountEnterClan+" очков рейтинга ( Игрок вошел в клан ).");
		}
	}

	public L2ClanMember getClanMember(int id)
	{
		return _members.get(id);
	}

	public L2ClanMember getClanMember(String name)
	{
		for(L2ClanMember member : _members.values())
			if(member.getName().equals(name))
				return member;
		return null;
	}

	public int getMembersCount()
	{
		return _members.size();
	}

	public void flush()
	{
		for(L2ClanMember member : getMembers())
			removeClanMember(member.getObjectId());
		for(L2ItemInstance item : _warehouse.listItems(ItemClass.ALL))
			_warehouse.destroyItem(item.getItemId(), item.getCount());
		if(_hasCastle != 0)
			CastleManager.getInstance().getCastleByIndex(_hasCastle).changeOwner(null);
		if(_hasFortress != 0)
			FortressManager.getInstance().getFortressByIndex(_hasFortress).changeOwner(null);
	}

	public void removeClanMember(int id)
	{
		if(id == getLeaderId())
			return;
		L2ClanMember exMember = _members.remove(id);
		if(exMember == null)
			return;
		SubPledge sp = _SubPledges.get(exMember.getPledgeType());
		if(sp != null && sp.getLeaderId() == exMember.getObjectId()) // subpledge leader
			sp.setLeaderId(0); // clan leader has to assign another one, via villagemaster
		if(exMember.hasSponsor())
			getClanMember(exMember.getSponsor()).setApprentice(0);
		if(ConfigValue.EnableClanPoint)
		{
			clan_point -= ConfigValue.ClanPointCountExitClan;
			PlayerData.getInstance().updateClanInDB(this);
			sendMessageToAll("Потеряно "+ConfigValue.ClanPointCountExitClan+" очков рейтинга ( Игрок вышел из клана ).");
		}
		PlayerData.getInstance().removeMemberInDatabase(exMember);
	}

	public L2ClanMember[] getMembers()
	{
		return _members.values().toArray(new L2ClanMember[_members.size()]);
	}

	public L2Player[] getOnlineMembers(int exclude)
	{
		GArray<L2Player> result = new GArray<L2Player>();
		for(L2ClanMember temp : _members.values())
			if(temp.isOnline() && temp.getObjectId() != exclude)
				result.add(temp.getPlayer());
		return result.toArray(new L2Player[result.size()]);
	}

	public int getAllyId()
	{
		return _allyId;
	}

	public byte getLevel()
	{
		return _level;
	}

	/**
	 * Возвращает замок, которым владеет клан
	 * @return ID замка
	 */
	public int getHasCastle()
	{
		return _hasCastle;
	}

	/**
	 * Возвращает крепость, которой владеет клан
	 * @return ID крепости
	 */
	public int getHasFortress()
	{
		return _hasFortress;
	}

	/**
	 * Возвращает кланхолл, которым владеет клан
	 * @return ID кланхолла
	 */
	public int getHasHideout()
	{
		return _hasHideout;
	}

	public void setAllyId(int allyId)
	{
		_allyId = allyId;
	}

	/**
	 * Устанавливает замок, которым владеет клан.<BR>
	 * Одновременно владеть и замком и крепостью нельзя
	 * @param castle ID замка
	 */
	public void setHasCastle(int castle)
	{
		if(_hasFortress == 0)
			_hasCastle = castle;
	}

	/**
	 * Устанавливает крепость, которой владеет клан.<BR>
	 * Одновременно владеть и крепостью и замком нельзя
	 * @param fortress ID крепости
	 */
	public void setHasFortress(int fortress)
	{
		if(_hasCastle == 0)
			_hasFortress = fortress;
	}

	public void setHasHideout(int hasHideout)
	{
		_hasHideout = hasHideout;
	}

	public void setLevel(byte level)
	{
		if(ConfigValue.KLColorlvlClan > 0 && level >= ConfigValue.KLColorlvlClan)
		{
			ServerVariables.set("ClanNickColor_"+_clanId, "1");
			getLeader().getPlayer().sendMessage("Ваш клан достиг "+level+" лвл для вас доступна функция бесплатной покраски ника .colorkl");
			getLeader().getPlayer().sendMessage("Ваш клан достиг "+level+" лвл для вас доступна функция бесплатной покраски ника .colorkl");
		}
		_level = level;
		if(ConfigValue.RecruitmentAllow)
			ClanRequest.updateList(); //Обновляем список кланов так как сортировка происходит по уровню!
		if(ConfigValue.AutoLearnClanSkill)
		{
			L2SkillLearn[] skills = SkillTreeTable.getInstance().getAvailableClanSkills(this);
			for(L2SkillLearn s : skills)
			{
				L2Skill skill = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
				if(skill != null)
					addNewSkill(skill, true);
			}
		}
	}

	public boolean isMember(Integer id)
	{
		return _members.containsKey(id);
	}

	public void broadcastToOnlineMembers(L2GameServerPacket... packets)
	{
		for(L2ClanMember member : _members.values())
			if(member.isOnline())
				member.getPlayer().sendPacket(packets);
	}
	
	public void broadcastCSToOnlineMembers(L2GameServerPacket packet, L2Player player)
	{
		for(L2ClanMember member : _members.values())
			if(member != null && member.isOnline() && !member.getPlayer().isInBlockList(player) && !member.getPlayer().isBlockAll())
				member.getPlayer().sendPacket(packet);
	}

	public void broadcastToOtherOnlineMembers(L2GameServerPacket packet, L2Player player)
	{
		for(L2ClanMember member : _members.values())
			if(member.isOnline() && member.getPlayer() != player)
				member.getPlayer().sendPacket(packet);
	}

	@Override
	public String toString()
	{
		return getName();
	}

	public void setCrestId(int newcrest)
	{
		_crestId = newcrest;
	}

	public int getCrestId()
	{
		return _crestId;
	}

	public boolean hasCrest()
	{
		return _crestId > 0;
	}

	public int getCrestLargeId()
	{
		return _crestLargeId;
	}

	public void setCrestLargeId(int newcrest)
	{
		_crestLargeId = newcrest;
	}

	public boolean hasCrestLarge()
	{
		return _crestLargeId > 0;
	}

	public long getAdenaCount()
	{
		return _warehouse.getAdenaCount();
	}

	public ClanWarehouse getWarehouse()
	{
		return _warehouse;
	}

	public int getHiredGuards()
	{
		return _hiredGuards;
	}

	public void incrementHiredGuards()
	{
		_hiredGuards++;
	}

	public int isAtWar()
	{
		if(_atWarWith != null && !_atWarWith.isEmpty())
			return 1;
		return 0;
	}

	public int isAtWarOrUnderAttack()
	{
		if(_atWarWith != null && !_atWarWith.isEmpty() || _underAttackFrom != null && !_underAttackFrom.isEmpty())
			return 1;
		return 0;
	}

	public boolean isAtWarWith(Integer id)
	{
		L2Clan clan = ClanTable.getInstance().getClan(id);
		if(_atWarWith != null && !_atWarWith.isEmpty())
			if(_atWarWith.contains(clan))
				return true;
		return false;
	}

	public boolean isUnderAttackFrom(Integer id)
	{
		L2Clan clan = ClanTable.getInstance().getClan(id);
		if(_underAttackFrom != null && !_underAttackFrom.isEmpty())
			if(_underAttackFrom.contains(clan))
				return true;
		return false;
	}

	public void setEnemyClan(L2Clan clan)
	{
		_atWarWith.add(clan);
	}

	public void deleteEnemyClan(L2Clan clan)
	{
		_atWarWith.remove(clan);
	}

	// clans that are attacking this clan
	public void setAttackerClan(L2Clan clan)
	{
		_underAttackFrom.add(clan);
	}

	public void deleteAttackerClan(L2Clan clan)
	{
		_underAttackFrom.remove(clan);
	}

	public GArray<L2Clan> getEnemyClans()
	{
		return _atWarWith;
	}

	public int getWarsCount()
	{
		return _atWarWith.size();
	}

	public GArray<L2Clan> getAttackerClans()
	{
		return _underAttackFrom;
	}

	public void broadcastClanStatus(boolean updateList, boolean needUserInfo, boolean relation)
	{
		for(L2ClanMember member : _members.values())
			if(member.isOnline())
			{
				if(updateList)
					member.getPlayer().sendPacket(Msg.PledgeShowMemberListDeleteAll, new PledgeShowMemberListAll(this, member.getPlayer()));
				member.getPlayer().sendPacket(new PledgeShowInfoUpdate(this));
				if(needUserInfo)
					member.getPlayer().broadcastUserInfo(true);
				if(relation)
					member.getPlayer().broadcastRelationChanged();
			}
	}

	public L2Alliance getAlliance()
	{
		return _allyId == 0 ? null : ClanTable.getInstance().getAlliance(_allyId);
	}

	public void setExpelledMemberTime(long time)
	{
		_expelledMemberTime = time;
	}

	public long getExpelledMemberTime()
	{
		return _expelledMemberTime;
	}

	public void setExpelledMember()
	{
		_expelledMemberTime = System.currentTimeMillis();
		PlayerData.getInstance().updateClanInDB(this);
	}

	public void setLeavedAllyTime(long time)
	{
		_leavedAllyTime = time;
	}

	public long getLeavedAllyTime()
	{
		return _leavedAllyTime;
	}

	public void setLeavedAlly()
	{
		_leavedAllyTime = System.currentTimeMillis();
		PlayerData.getInstance().updateClanInDB(this);
	}

	public void setDissolvedAllyTime(long time)
	{
		_dissolvedAllyTime = time;
	}

	public long getDissolvedAllyTime()
	{
		return _dissolvedAllyTime;
	}

	public void setDissolvedAlly()
	{
		_dissolvedAllyTime = System.currentTimeMillis();
		PlayerData.getInstance().updateClanInDB(this);
	}

	public boolean canInvite()
	{
		return System.currentTimeMillis() - _expelledMemberTime >= ConfigValue.EXPELLED_MEMBER_PENALTY * 1000; // 24 * 60 * 60 * 1000L;
	}

	public boolean canJoinAlly()
	{
		return System.currentTimeMillis() - _leavedAllyTime >= ConfigValue.LEAVED_ALLY_PENALTY * 1000; // 24 * 60 * 60 * 1000L;
	}

	public boolean canCreateAlly()
	{
		return System.currentTimeMillis() - _dissolvedAllyTime >= ConfigValue.DISSOLVED_ALLY_PENALTY * 1000; // 24 * 60 * 60 * 1000L;
	}

	public int getRank()
	{
		L2Clan[] clans = ClanTable.getInstance().getClans();
		Arrays.sort(clans, REPUTATION_COMPARATOR);

		int place = 1;
		for(int i = 0; i < clans.length; i++)
		{
			if(i == REPUTATION_PLACES)
				return 0;

			L2Clan clan = clans[i];
			if(clan == this)
				return place + i;
		}

		return 0;
	}

	public int getReputationScore()
	{
		return _reputation;
	}

	public void setReputationScore(int rep)
	{
		if(_reputation >= 0 && rep < 0)
		{
			broadcastToOnlineMembers(Msg.SINCE_THE_CLAN_REPUTATION_SCORE_HAS_DROPPED_TO_0_OR_LOWER_YOUR_CLAN_SKILLS_WILL_BE_DE_ACTIVATED);
			L2Skill[] skills = getAllSkills();
			for(L2ClanMember member : _members.values())
				if(member.isOnline())
				{
					for(L2Skill sk : skills)
						member.getPlayer().removeSkill(sk, false, false);
					member.getPlayer().updateEffectIcons();
				}
		}
		else if(_reputation < 0 && rep >= 0)
		{
			broadcastToOnlineMembers(Msg.THE_CLAN_SKILL_WILL_BE_ACTIVATED_BECAUSE_THE_CLANS_REPUTATION_SCORE_HAS_REACHED_TO_0_OR_HIGHER);
			L2Skill[] skills = getAllSkills();
			for(L2ClanMember member : _members.values())
				if(member.isOnline())
					for(L2Skill sk : skills)
					{
						member.getPlayer().sendPacket(new PledgeSkillListAdd(sk.getId(), sk.getLevel()));
						if(sk.getMinPledgeClass() <= member.getPlayer().getPledgeClass())
							member.getPlayer().addSkill(SkillTable.getInstance().getInfo(sk.getId(), sk.getLevel()), false);
					}
		}

		if(_reputation != rep)
		{
			_reputation = rep;
			broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
		}

		PlayerData.getInstance().updateClanInDB(this);
	}

	public int incReputation(int inc, boolean rate, String source)
	{
		if(_level < 5)
			return 0;

		if(rate && Math.abs(inc) <= ConfigValue.RateClanRepScoreMaxAffected)
			inc = Math.round(inc * ConfigValue.RateClanRepScore);

		setReputationScore(_reputation + inc);
		Log.add(_name + "|" + inc + "|" + _reputation + "|" + source, "clan_reputation");

		return inc;
	}

	/* ============================ clan skills stuff ============================ */
	/** used to retrieve all skills */
	public final L2Skill[] getAllSkills()
	{
		if(_reputation < 0)
			return new L2Skill[0];

		return _skills.values().toArray(new L2Skill[_skills.values().size()]);
	}

	public L2Skill addNewSkill(L2Skill newSkill, boolean store)
	{
		return addNewSkill(newSkill, store, -1);
	}

	/** used to add a new skill to the list, send a packet to all online clan members, update their stats and store it in db*/
	public L2Skill addNewSkill(L2Skill newSkill, boolean store, int plId)
	{
		L2Skill oldSkill = null;
		if(newSkill != null)
		{
			if(plId == -1)
				oldSkill = _skills.put(newSkill.getId(), newSkill);
			else if(plId >= 0)
			{
				FastMap<Integer, L2Skill> oldPSkill = _squadSkills.get(plId);
				if(oldPSkill == null)
					oldPSkill = new FastMap<Integer, L2Skill>().setShared(true);
				oldSkill = oldPSkill.put(newSkill.getId(), newSkill);
				_squadSkills.put(plId, oldPSkill);
			}
			else
			{
				_log.warning("Player " + getLeaderName() + " tried to add a Squad Skill to a squad that doesn't exist, ban him!");
				return null;
			}
			if(store)
				PlayerData.getInstance().addNewSkill(this, newSkill, oldSkill, plId);

			for(L2ClanMember temp : _members.values())
			{
				if(temp.isOnline() && temp.getPlayer() != null)
				{
					if(plId == -1)
					{
						temp.getPlayer().sendPacket(new PledgeSkillListAdd(newSkill.getId(), newSkill.getLevel()));
						if(newSkill.getMinPledgeClass() <= temp.getPlayer().getPledgeClass())
							temp.getPlayer().addSkill(SkillTable.getInstance().getInfo(newSkill.getId(), newSkill.getLevel()), false);
					}
					else
					{
						temp.getPlayer().sendPacket(new ExSubPledgetSkillAdd(newSkill.getId(), newSkill.getLevel(), plId));
						if(temp.getPledgeType() == plId)
							temp.getPlayer().addSkill(SkillTable.getInstance().getInfo(newSkill.getId(), newSkill.getLevel()), false);
					}
				}
			}
		}
		return oldSkill;
	}

	/**
	 * Удаляет скилл у клана, без удаления из базы. Используется для удаления скилов резиденций.
	 * После удаления скила(ов) необходимо разослать boarcastSkillListToOnlineMembers()
	 * @param skill
	 */
	public void removeSkill(L2Skill skill)
	{
		_skills.remove(skill.getId());
		for(L2ClanMember temp : _members.values())
			if(temp.isOnline() && temp.getPlayer() != null)
				temp.getPlayer().removeSkill(skill, false, true);
	}

	public void boarcastSkillListToOnlineMembers()
	{
		for(L2ClanMember temp : _members.values())
			if(temp.isOnline() && temp.getPlayer() != null)
				addAndShowSkillsToPlayer(temp.getPlayer());
	}

	public void addAndShowSkillsToPlayer(L2Player activeChar)
	{
		if(_reputation < 0)
			return;

		activeChar.sendPacket(new PledgeSkillList(this));

		for(L2Skill s : _skills.values())
		{
			if(s == null)
				continue;
			activeChar.sendPacket(new PledgeSkillListAdd(s.getId(), s.getLevel()));
			if(s.getMinPledgeClass() <= activeChar.getPledgeClass())
				activeChar.addSkill(SkillTable.getInstance().getInfo(s.getId(), s.getLevel()), false);
		}
		if(_squadSkills != null && !_squadSkills.isEmpty())
		{
			for(int pledgeId : _squadSkills.keySet())
			{
				FastMap<Integer, L2Skill> skills = _squadSkills.get(pledgeId);
				for(L2Skill s : skills.values())
				{
					activeChar.sendPacket(new ExSubPledgetSkillAdd(s.getId(), s.getLevel(), pledgeId));
					if(pledgeId == activeChar.getPledgeType())
						activeChar.addSkill(SkillTable.getInstance().getInfo(s.getId(), s.getLevel()), false);
				}
			}
		}
		activeChar.sendPacket(new SkillList(activeChar));
	}

	public void showSquadSkillsToPlayer(L2Player player)
	{
		if(_squadSkills != null && !_squadSkills.isEmpty())
		{
			for(int pledgeId : _squadSkills.keySet())
			{
				FastMap<Integer, L2Skill> skills = _squadSkills.get(pledgeId);
				for(L2Skill s : skills.values())
				{
					player.sendPacket(new ExSubPledgetSkillAdd(s.getId(), s.getLevel(), pledgeId));
					if(pledgeId == player.getPledgeType())
						player.addSkill(SkillTable.getInstance().getInfo(s.getId(), s.getLevel()), false);
				}
			}
		}
	}
	/* ============================ clan subpledges stuff ============================ */
	public final boolean isAcademy(int pledgeType)
	{
		return pledgeType == SUBUNIT_ACADEMY;
	}

	public final boolean isRoyalGuard(int pledgeType)
	{
		return pledgeType == SUBUNIT_ROYAL1 || pledgeType == SUBUNIT_ROYAL2;
	}

	public final boolean isOrderOfKnights(int pledgeType)
	{
		return pledgeType == SUBUNIT_KNIGHT1 || pledgeType == SUBUNIT_KNIGHT2 || pledgeType == SUBUNIT_KNIGHT3 || pledgeType == SUBUNIT_KNIGHT4;
	}

	public int getAffiliationRank(int pledgeType)
	{
		if(isAcademy(pledgeType))
			return 9;
		else if(isOrderOfKnights(pledgeType))
			return 8;
		else if(isRoyalGuard(pledgeType))
			return 7;
		else
			return 6;
	}

	public final SubPledge getSubPledge(int pledgeType)
	{
		if(_SubPledges == null)
			return null;

		return _SubPledges.get(pledgeType);
	}

	public int createSubPledge(L2Player player, int pledgeType, int leaderId, String name)
	{
		int temp = pledgeType;
		pledgeType = getAvailablePledgeTypes(pledgeType);

		if(pledgeType == SUBUNIT_NONE)
		{
			if(temp == SUBUNIT_ACADEMY)
				player.sendPacket(Msg.YOUR_CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY);
			else
				player.sendMessage("You can't create any more sub-units of this type");
			return SUBUNIT_NONE;
		}

		switch(pledgeType)
		{
			case SUBUNIT_ACADEMY:
				break;
			case SUBUNIT_ROYAL1:
			case SUBUNIT_ROYAL2:
				if(getReputationScore() < 5000)
				{
					player.sendPacket(Msg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
					return SUBUNIT_NONE;
				}
				incReputation(-5000, false, "SubunitCreate");
				break;
			case SUBUNIT_KNIGHT1:
			case SUBUNIT_KNIGHT2:
			case SUBUNIT_KNIGHT3:
			case SUBUNIT_KNIGHT4:
				if(getReputationScore() < 10000)
				{
					player.sendPacket(Msg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
					return SUBUNIT_NONE;
				}
				incReputation(-10000, false, "SubunitCreate");
				break;
		}

		PlayerData.getInstance().addSubPledge(this, new SubPledge(this, pledgeType, leaderId, name), true);
		return pledgeType;
	}

	public int getAvailablePledgeTypes(int pledgeType)
	{
		if(pledgeType == SUBUNIT_NONE)
			return SUBUNIT_NONE;

		if(_SubPledges.get(pledgeType) != null)
			switch(pledgeType)
			{
				case SUBUNIT_ACADEMY:
					return 0;
				case SUBUNIT_ROYAL1:
					pledgeType = getAvailablePledgeTypes(SUBUNIT_ROYAL2);
					break;
				case SUBUNIT_ROYAL2:
					return 0;
				case SUBUNIT_KNIGHT1:
					pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT2);
					break;
				case SUBUNIT_KNIGHT2:
					pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT3);
					break;
				case SUBUNIT_KNIGHT3:
					pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT4);
					break;
				case SUBUNIT_KNIGHT4:
					return 0;
			}
		return pledgeType;
	}

	/** used to retrieve all subPledges */
	public final SubPledge[] getAllSubPledges()
	{
		return _SubPledges.values().toArray(new SubPledge[_SubPledges.values().size()]);
	}

	public int getSubPledgeLimit(int pledgeType)
	{
		int limit;
		switch(_level)
		{
			case 0:
				limit = ConfigValue.ClanPlayerLimit0;
				break;
			case 1:
				limit = ConfigValue.ClanPlayerLimit1;
				break;
			case 2:
				limit = ConfigValue.ClanPlayerLimit2;
				break;
			case 3:
				limit = ConfigValue.ClanPlayerLimit3;
				break;
			default:
				limit = ConfigValue.ClanPlayerLimit;
				break;
		}
		switch(pledgeType)
		{
			case SUBUNIT_ACADEMY:
				if(getLevel() >= 11)
					limit = ConfigValue.ClanPlayerLimitAc11;
				else
					limit = ConfigValue.ClanPlayerLimitAc;
				break;
			case SUBUNIT_ROYAL1:
			case SUBUNIT_ROYAL2:
				if(getLevel() >= 11)
					limit = ConfigValue.ClanPlayerLimitAcR1R2_11;
				else
					limit = ConfigValue.ClanPlayerLimitAcR1R2;
				break;
			case SUBUNIT_KNIGHT1:
			case SUBUNIT_KNIGHT2:
				if(getLevel() >= 9)
					limit = ConfigValue.ClanPlayerLimitK1K2_9;
				else
					limit = ConfigValue.ClanPlayerLimitK1K2;
				break;
			case SUBUNIT_KNIGHT3:
			case SUBUNIT_KNIGHT4:
				if(getLevel() >= 10)
					limit = ConfigValue.ClanPlayerLimitK3K4_10;
				else
					limit = ConfigValue.ClanPlayerLimitK3K4;
				break;
		}
		return limit;
	}

	public int getSubPledgeMembersCount(int pledgeType)
	{
		int result = 0;
		for(L2ClanMember temp : _members.values())
			if(temp.getPledgeType() == pledgeType)
				result++;
		return result;
	}

	public int getSubPledgeLeaderId(int pledgeType)
	{
		return _SubPledges.get(pledgeType).getLeaderId();
	}

	/* ============================ clan privilege ranks stuff ============================ */
	public void InitializePrivs()
	{
		for(int i = RANK_FIRST; i <= RANK_LAST; i++)
			_Privs.put(i, new RankPrivs(i, 0, CP_NOTHING));
	}

	public void updatePrivsForRank(int rank)
	{
		for(L2ClanMember member : _members.values())
			if(member.isOnline() && member.getPlayer() != null && member.getPlayer().getPowerGrade() == rank)
			{
				if(member.getPlayer().isClanLeader())
					continue;
				member.getPlayer().sendUserInfo(false);
			}
	}

	public RankPrivs getRankPrivs(int rank)
	{
		if(rank < RANK_FIRST || rank > RANK_LAST)
		{
			_log.warning("Requested invalid rank value: " + rank);
			Thread.dumpStack();
			return null;
		}
		if(_Privs.get(rank) == null)
		{
			_log.warning("Request of rank before init: " + rank);
			Thread.dumpStack();
			PlayerData.getInstance().setRankPrivs(this, rank, CP_NOTHING);
		}
		return _Privs.get(rank);
	}

	public int countMembersByRank(int rank)
	{
		int ret = 0;
		for(L2ClanMember m : getMembers())
			if(m.getPowerGrade() == rank)
				ret++;
		return ret;
	}

	/** used to retrieve all privilege ranks */
	public final RankPrivs[] getAllRankPrivs()
	{
		if(_Privs == null)
			return new RankPrivs[0];
		return _Privs.values().toArray(new RankPrivs[_Privs.values().size()]);
	}

	private int _auctionBiddedAt = 0;

	public int getAuctionBiddedAt()
	{
		return _auctionBiddedAt;
	}

	public void setAuctionBiddedAt(int id)
	{
		_auctionBiddedAt = id;
	}

	public void sendMessageToAll(String message)
	{
		for(L2ClanMember member : _members.values())
			if(member.isOnline() && member.getPlayer() != null)
				member.getPlayer().sendMessage(message);
	}

	public void sendMessageToAll(String message, String message_ru)
	{
		L2Player player;
		for(L2ClanMember member : _members.values())
			if(member.isOnline() && (player = member.getPlayer()) != null)
				player.sendMessage(player.isLangRus() && !message_ru.isEmpty() ? message_ru : message);
	}

	private Siege _siege;
	private boolean _isDefender;
	private boolean _isAttacker;

	public void setSiege(Siege siege)
	{
		_siege = siege;
	}

	public Siege getSiege()
	{
		return _siege;
	}

	public void setDefender(boolean b)
	{
		_isDefender = b;
	}

	public void setAttacker(boolean b)
	{
		_isAttacker = b;
	}

	public boolean isDefender()
	{
		return _isDefender;
	}

	public boolean isAttacker()
	{
		return _isAttacker;
	}

	private static class ClanReputationComparator implements Comparator<L2Clan>
	{
		public int compare(L2Clan o1, L2Clan o2)
		{
			if(o1 == null || o2 == null)
				return 0;
			return o2.getReputationScore() - o1.getReputationScore();
		}
	}

	public int getWhBonus()
	{
		return _whBonus;
	}

	public void setWhBonus(int i)
	{
		if(_whBonus != -1)
			mysql.set("UPDATE `clan_data` SET `warehouse`=? WHERE `clan_id`=?", i, getClanId());
		_whBonus = i;
	}

	private int _territorySide = -1;

	public void setTerritorySiege(int side)
	{
		_territorySide = side;
	}

	public int getTerritorySiege()
	{
		return _territorySide;
	}

	public void setAirshipLicense(boolean val)
	{
		_airshipLicense = val;
	}

	public boolean isHaveAirshipLicense()
	{
		return _airshipLicense;
	}

	public L2AirShip getAirship()
	{
		return _airship;
	}

	public void setAirship(L2AirShip airship)
	{
		_airship = airship;
	}

	public int getAirshipFuel()
	{
		return _airshipFuel;
	}

	public void setAirshipFuel(int fuel)
	{
		_airshipFuel = fuel;
	}

	public final FastMap<Integer, FastMap<Integer, L2Skill>> getSquadSkills()
	{
		return _squadSkills;
	}

	public FastMap<Integer, SubPledge> getSubPledges()
	{
		return _SubPledges;
	}

	public boolean checkInviteList(int obj)
	{
		for(ClanRequest request : ClanRequest.getInviteList(getClanId()))
		{
			L2Player invited = request.getPlayer();

			if(invited.getObjectId() == obj)
				return true;
		}

		return false;
	}

	public List<ClanRequest> getInviteList()
	{
		return ClanRequest.getInviteList(getClanId());
	}

	private String _description = "";
	public String getDescription()
	{
		return _description;
	}

	/**
	* Назначить новое описание
	*/
	public void setDescription(String description)
	{
		_description = description;
	}

	public int getAverageLevel()
	{
		int level=0;
		for(L2ClanMember member : _members.values())
			level+=member.getLevel();
		return level/_members.size();
	}

	public long request_war_time=0;

	public <R extends Residence> int getResidenceId(Class<R> r)
	{
		if(r == Castle.class)
			return _hasCastle;
		else if(r == Fortress.class)
			return _hasFortress;
		else if(r == ClanHall.class)
			return _hasHideout;
		else
			return 0;
	}

	public int getAllyCrestId()
	{
		return getAlliance() == null ? 0 : getAlliance().getAllyCrestId();
	}

	public int getClearOnline()
	{
		HashMap<String, String> _online = new HashMap<String, String>();
		GArray<L2Player> result = new GArray<L2Player>();
		for(L2ClanMember temp : _members.values())
			if(temp.isOnline())
				_online.put(temp.getPlayer().getHWIDs(), temp.getPlayer().getHWIDs());
		return _online.size();
	}

	public void sendMail(L2Clan clan)
	{
		String mail_body = ConfigValue.ClanWarMailBody.replace("$clan",clan.getName()).replace("$mdam",String.valueOf((int)(ConfigValue.ClanWarMagicDamageMod*100)-100)).replace("$pdam",String.valueOf((int)(ConfigValue.ClanWarPhysDamageMod*100)-100)).replace("$psdam",String.valueOf((int)(ConfigValue.ClanWarPhysSkillDamageMod*100)-100));
		for(L2ClanMember member : _members.values())
		{
			MailParcelController.Letter mail = new MailParcelController.Letter();
			mail.senderId = 1;
			mail.senderName = "";
			mail.receiverId = member.getObjectId();
			mail.receiverName = member.getName();
			mail.topic = ConfigValue.ClanWarMailTopic;
			mail.body = mail_body;
			mail.price = 0;
			mail.unread = 1;
			mail.system = 0;
			mail.hideSender = 2;
			mail.validtime = ConfigValue.ClanWarMailLife * 3600 + (int) (System.currentTimeMillis() / 1000L);

			MailParcelController.getInstance().sendLetter(mail);

			if(member.isOnline() && member.getPlayer() != null)
				member.getPlayer().sendPacket(new ExNoticePostArrived(1));
		}
	}

	public boolean isAutoWar()
	{
		return _auto_war;
	}

	public void changeAutoWar()
	{
		_auto_war = !_auto_war;
		mysql.set("UPDATE `clan_data` SET `auto_war`=? WHERE `clan_id`=?", _auto_war ? 1 : 0, getClanId());
	}
}