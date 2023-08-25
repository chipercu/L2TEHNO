package l2open.gameserver.model;

import l2open.gameserver.model.L2Skill.AddedSkill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;

import java.util.Collection;

public class CursedWeapon
{
	private final String _name;
	private String _transformationName;

	private final int _itemId, _skillMaxLevel;
	private final Integer _skillId;
	private int _dropRate, _disapearChance;
	private int _durationMin, _durationMax, _durationLost;
	private int _transformationId, _transformationTemplateId;
	private int _stageKills, _nbKills = 0, _playerKarma = 0, _playerPkKills = 0;

	private CursedWeaponState _state = CursedWeaponState.NONE;
	private Location _loc = null;
	private long _endTime = 0;
	private L2ItemInstance _item = null;
	private String _plName = "";

	private int _playerObjectId = 0;
	private L2Player _player = null;

	public enum CursedWeaponState
	{
		NONE,
		ACTIVATED,
		DROPPED,
	}

	public CursedWeapon(int itemId, Integer skillId, String name)
	{
		_name = name;
		_itemId = itemId;
		_skillId = skillId;
		_skillMaxLevel = SkillTable.getInstance().getMaxLevel(_skillId);
	}

	public void initWeapon()
	{
		zeroOwner();
		setState(CursedWeaponState.NONE);
		_endTime = 0;
		_item = null;
		_nbKills = 0;
	}

	/** Выпадение оружия из монстра */
	public void create(L2NpcInstance attackable, L2Player killer, boolean force)
	{
		if(force || Rnd.get(100000000) <= _dropRate)
		{
			_item = ItemTemplates.getInstance().createItem(_itemId);
			if(_item != null)
			{
				zeroOwner();
				setState(CursedWeaponState.DROPPED);

				if(_endTime == 0)
					_endTime = System.currentTimeMillis() + getRndDuration() * 60000;

				_item.dropToTheGround(killer, attackable);
				_loc = _item.getLoc();
				_item.setDropTime(0);

				// RedSky and Earthquake
				L2GameServerPacket redSky = new ExRedSky(10);
				L2GameServerPacket eq = new Earthquake(killer.getLoc(), 30, 12);
				for(L2Player aPlayer : L2ObjectsStorage.getPlayers())
					aPlayer.sendPacket(redSky, eq);
			}
		}
	}

	/**
	 * Выпадение оружия из владельца, или исчезновение с определенной вероятностью.
	 * Вызывается при смерти игрока.
	 */
	public boolean dropIt(L2NpcInstance attackable, L2Player killer, L2Player owner)
	{
		if(Rnd.chance(_disapearChance))
			return false;

		L2Player player = getOnlineOwner();
		if(player == null)
		{
			System.out.println("CursedWeapon [" + _name + "] :: dropIt without online owner, using owner from arg: " + owner);
			Thread.dumpStack();

			if(owner == null)
				return false;
			player = owner;
		}

		L2ItemInstance oldItem = player.getInventory().getItemByItemId(_itemId);
		if(oldItem == null)
			return false;

		long oldCount = oldItem.getCount();
		player.validateLocation(0);

		if((oldItem = player.getInventory().dropItem(oldItem, oldCount, true)) == null)
			return false;

		player.setKarma(_playerKarma);
		player.setPkKills(_playerPkKills);
		player.setCursedWeaponEquippedId(0);
		player.setTransformation(0);
		player.setTransformationName(null);

		L2Skill skill = SkillTable.getInstance().getInfo(_skillId, player.getSkillLevel(_skillId));
		if(skill != null)
			for(AddedSkill s : skill.getAddedSkills())
				player.removeSkillById(s.id);

		player.removeSkillById(_skillId);

		player.abortAttack(true, false);

		zeroOwner();
		setState(CursedWeaponState.DROPPED);

		oldItem.dropToTheGround(player, (L2NpcInstance) null);
		_loc = oldItem.getLoc();

		oldItem.setDropTime(0);
		_item = oldItem;

		player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_DROPPED_S1).addItemName(oldItem.getItemId()));
		player.validateItemExpertisePenalties(false, false, true);
		player.broadcastUserInfo(true);
		player.broadcastPacket(new Earthquake(player.getLoc(), 30, 12));

		return true;
	}

	private void giveSkill(L2Player player)
	{
		for(L2Skill s : getSkills(player))
		{
			player.addSkill(s, false);
			player._transformationSkills.put(s.getId(), s);
		}
		player.sendPacket(new SkillList(player));
	}

	private Collection<L2Skill> getSkills(L2Player player)
	{
		int level = 1 + _nbKills / _stageKills;
		if(level > _skillMaxLevel)
			level = _skillMaxLevel;

		L2Skill skill = SkillTable.getInstance().getInfo(_skillId, level);
		GArray<L2Skill> ret = new GArray<L2Skill>();
		ret.add(skill);
		for(AddedSkill s : skill.getAddedSkills())
			ret.add(SkillTable.getInstance().getInfo(s.id, s.level));
		return ret;
	}

	/** вызывается при загрузке оружия */
	public boolean reActivate()
	{
		if(getTimeLeft() <= 0)
		{
			if(getPlayerId() != 0) // to be sure, that cursed weapon will deleted in right way
				setState(CursedWeaponState.ACTIVATED);
			return false;
		}

		if(getPlayerId() == 0)
		{
			if(_loc == null || (_item = ItemTemplates.getInstance().createItem(_itemId)) == null)
				return false;

			_item.dropMe(null, _loc);
			_item.setDropTime(0);

			setState(CursedWeaponState.DROPPED);
		}
		else
			setState(CursedWeaponState.ACTIVATED);
		return true;
	}

	public void activate(L2Player player, L2ItemInstance item)
	{
		if(isDropped() || getPlayerId() != player.getObjectId()) // оружие уже в руках игрока или новый игрок
		{
			_playerKarma = player.getKarma();
			_playerPkKills = player.getPkKills();
		}

		setPlayer(player);
		setState(CursedWeaponState.ACTIVATED);

		if(player.isInParty())
			player.getParty().oustPartyMember(player);
		if(player.isMounted())
			player.setMount(0, 0, 0);

		_item = item;

		player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
		player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_RHAND, null);
		player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_RHAND, _item);

		player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EQUIPPED_YOUR_S1).addItemName(_item.getItemId()));

		
		player.setTransformation(0);
		player.setCursedWeaponEquippedId(_itemId);
		player.teleToLocation(player.getX(), player.getY(), player.getZ()+60);
		player.setTransformation(_transformationId);
		player.setTransformationName(_transformationName);
		player.setTransformationTemplate(_transformationTemplateId);
		player.setKarma(9999999);
		player.setPkKills(_nbKills);

		if(_endTime == 0)
			_endTime = System.currentTimeMillis() + getRndDuration() * 60000;

		giveSkill(player);

		player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
		player.setCurrentCp(player.getMaxCp());

		player.broadcastUserInfo(true);
		player.onTeleported();
	}

	public void increaseKills()
	{
		L2Player player = getOnlineOwner();
		if(player == null)
		{
			System.out.println("CursedWeapon [" + _name + "] :: increaseKills without online owner");
			Thread.dumpStack();
			return;
		}

		_nbKills++;
		player.setPkKills(_nbKills);
		player.broadcastUserInfo(true);
		if(_nbKills % _stageKills == 0 && _nbKills <= _stageKills * (_skillMaxLevel - 1))
			giveSkill(player);
		_endTime -= _durationLost * 60000; // Reduce time-to-live
	}

	public void setDisapearChance(int disapearChance)
	{
		_disapearChance = disapearChance;
	}

	public void setDropRate(int dropRate)
	{
		_dropRate = dropRate;
	}

	public void setDurationMin(int duration)
	{
		_durationMin = duration;
	}

	public void setDurationMax(int duration)
	{
		_durationMax = duration;
	}

	public void setDurationLost(int durationLost)
	{
		_durationLost = durationLost;
	}

	public void setStageKills(int stageKills)
	{
		_stageKills = stageKills;
	}

	public void setTransformationId(int transformationId)
	{
		_transformationId = transformationId;
	}

	public int getTransformationId()
	{
		return _transformationId;
	}

	public void setTransformationTemplateId(int transformationTemplateId)
	{
		_transformationTemplateId = transformationTemplateId;
	}

	public void setTransformationName(String name)
	{
		_transformationName = name;
	}

	public void setNbKills(int nbKills)
	{
		_nbKills = nbKills;
	}

	public void setPlayerId(int playerId)
	{
		_playerObjectId = playerId;
	}

	public void setPlayerKarma(int playerKarma)
	{
		_playerKarma = playerKarma;
	}

	public void setPlayerPkKills(int playerPkKills)
	{
		_playerPkKills = playerPkKills;
	}

	public void setState(CursedWeaponState state)
	{
		_state = state;
	}

	public void setEndTime(long endTime)
	{
		_endTime = endTime;
	}

	public void setPlayer(L2Player player)
	{
		if(player != null)
		{
			_player = player;
			_playerObjectId = player.getObjectId();
			_plName = player.getName();
		}		
	}

	private void zeroOwner()
	{
		_plName = "";
		_player = null;
		_playerObjectId = 0;
		_playerKarma = 0;
		_playerPkKills = 0;
	}

	public void setItem(L2ItemInstance item)
	{
		_item = item;
	}

	public void setLoc(Location loc)
	{
		_loc = loc;
	}

	public CursedWeaponState getState()
	{
		return _state;
	}

	public boolean isActivated()
	{
		return getState() == CursedWeaponState.ACTIVATED;
	}

	public boolean isDropped()
	{
		return getState() == CursedWeaponState.DROPPED;
	}

	public long getEndTime()
	{
		return _endTime;
	}

	public String getName()
	{
		return _name;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public L2ItemInstance getItem()
	{
		return _item;
	}

	public Integer getSkillId()
	{
		return _skillId;
	}

	public int getPlayerId()
	{
		return _playerObjectId;
	}

	public L2Player getPlayer()
	{
		return _player;
	}

	public String getPlayerName()
	{
		return _plName;
	}

	public int getPlayerKarma()
	{
		return _playerKarma;
	}

	public int getPlayerPkKills()
	{
		return _playerPkKills;
	}

	public int getNbKills()
	{
		return _nbKills;
	}

	public int getStageKills()
	{
		return _stageKills;
	}

	/**
	 * Возвращает позицию (x, y, z)
	 * @return Location
	 */
	public Location getLoc()
	{
		return _loc;
	}

	public int getRndDuration()
	{
		if(_durationMin > _durationMax)
			_durationMax = 2 * _durationMin;
		return Rnd.get(_durationMin, _durationMax);
	}

	public boolean isActive()
	{
		return isActivated() || isDropped();
	}

	public int getLevel()
	{
		if(_nbKills > _stageKills * _skillMaxLevel)
			return _skillMaxLevel;
		return _nbKills / _stageKills;
	}

	public long getTimeLeft()
	{
		return _endTime - System.currentTimeMillis();
	}

	public Location getWorldPosition()
	{
		if(isActivated())
		{
			L2Player player = getOnlineOwner();
			if(player != null)
				return player.getLoc();
		}
		else if(isDropped())
			if(_item != null)
				return _item.getLoc();

		return null;
	}

	public L2Player getOnlineOwner()
	{
		L2Player player = getPlayer();
		return player != null && player.isOnline() ? player : null;
	}

	public boolean isOwned()
	{
		return _playerObjectId != 0;
	}
}