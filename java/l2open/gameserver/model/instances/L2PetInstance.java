package l2open.gameserver.model.instances;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.PetInventory;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.SkillAbnormalType;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.PetDataTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.taskmanager.DecayTaskManager;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.util.Rnd;
import l2open.util.SqlBatch;

import java.sql.ResultSet;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class L2PetInstance extends L2Summon
{
	private static final int Deluxe_Food_for_Strider = 5169;

	class FeedTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			try
			{
				L2Player owner = getPlayer();
				if(owner == null)
				{
					stopFeed();
					unSummon();
					return;
				}

				if(getCurrentFed() > getFeedConsume())
					setCurrentFed(getCurrentFed() - getFeedConsume()); // eat
				else
					setCurrentFed(0);
				broadcastStatusUpdate();

				if(isHungry() && tryFeed())
				{}

				if(PetDataTable.isPremiumPet(getNpcId()))
				{
					if(getCurrentFed() <= 0)
					{
						if(getNpcId() == 16050)
						{
							int i2 = Rnd.get(10) + 1;
							if(owner.getPkKills() <= i2)
								owner.setPkKills(0);
							else
								owner.setPkKills(owner.getPkKills() - i2);
							owner.broadcastStatusUpdate();
						}
						owner.sendPacket(new SystemMessage(SystemMessage.THE_HUNTING_HELPER_PET_IS_NOW_LEAVING));
						deleteMe();
					}
					else if(isHungry())
						owner.sendPacket(new SystemMessage(SystemMessage.THERE_IS_NOT_MUCH_TIME_REMAINING_UNTIL_THE_HUNTING_HELPER_PET_LEAVES));
					return;
				}
				else
				{
					if(getCurrentFed() <= 0.10 * getMaxFed() && getCurrentFed() > 0.01 * getMaxFed())
						owner.sendPacket(new SystemMessage(SystemMessage.WHEN_YOUR_PETS_HUNGER_GAUGE_IS_AT_0_YOU_CANNOT_USE_YOUR_PET));
					else if(getCurrentFed() <= 0.01 * getMaxFed()) 
					{
						owner.sendPacket(new SystemMessage(SystemMessage.YOUR_PET_IS_STARVING_AND_WILL_NOT_OBEY_UNTIL_IT_GETS_ITS_FOOD));
						setFollowStatus(true, true);
						setNpcState(100);
					}
				}
			}
			catch(Throwable e)
			{
				_log.log(Level.SEVERE, "", e);
			}
		}
	}

	public void sendPetStatusUpdate()
	{
		L2Player owner = getPlayer();
		if(owner != null)
			owner.sendPacket(new PetStatusUpdate(this));
	}

	public void sendPetInfo()
	{
		L2Player owner = getPlayer();
		if(owner != null)
			owner.sendPacket(new PetInfo(this, 1));
	}

	public void sendItemList()
	{
		L2Player owner = getPlayer();
		if(owner != null && owner.getInventory().getItemByObjectId(getControlItem().getObjectId()) != null)
			owner.sendPacket(new PetItemList(this));
	}

	protected static Logger _log = Logger.getLogger(L2PetInstance.class.getName());

	public static L2PetInstance spawnPet(L2NpcTemplate template, L2Player owner, L2ItemInstance control)
	{
		L2PetInstance result = PlayerData.getInstance().restore_pet(control, template, owner);
		if(result != null)
			result.updateControlItem();
		return result;
	}

	private int _controlItemObjId;
	private int _curFed;
	protected L2PetData _data;
	private Future<?> _feedTask;
	protected PetInventory _inventory;
	private byte _level;
	private boolean _respawned;
	private int lostExp;
	public int _ownerObjectId;

	/**
	 * Создание нового пета
	 */
	public L2PetInstance(int objectId, L2NpcTemplate template, L2Player owner, L2ItemInstance control)
	{
		super(objectId, template, owner);

		_controlItemObjId = control.getObjectId();

		byte itemEnchant = (byte) getControlItem()._enchantLevel;

		// Sin Eater
		if(template.npcId == PetDataTable.SIN_EATER_ID)
		{
			_level = itemEnchant;
			if(_level <= 0)
				_level = owner.getLevel();
		}
		else if(itemEnchant > 0)
			_level = itemEnchant;
		else
			_level = template.level;

		byte minLevel = PetDataTable.getInstance().getInfo(template.npcId, template.level) == null ? 1 : (byte)PetDataTable.getInstance().getInfo(template.npcId, template.level).getMinLevel();
		if(_level < minLevel)
			_level = minLevel;

		_exp = getExpForThisLevel();
		
		if(PetDataTable.isPremiumPet(template.npcId))
		{
			_level = owner.getLevel();
			_exp = getExpForNextLevel();
		}

		_data = PetDataTable.getInstance().getInfo(template.npcId, _level);

		_inventory = new PetInventory(this);
		
		// сохраним, а то при выходе игрока не сможем получить эти данные
		_ownerObjectId = owner.getObjectId();

		//transferPetItems();

		startFeed();
	}

	/**
	 * Загрузка уже существующего пета
	 */
	public L2PetInstance(int objectId, L2NpcTemplate template, L2Player owner, L2ItemInstance control, byte currentLevel, long exp)
	{
		super(objectId, template, owner);

		_controlItemObjId = control.getObjectId();
		_exp = exp;
		_level = (byte) control._enchantLevel;

		if(_level <= 0)
		{
			if(template.npcId == PetDataTable.SIN_EATER_ID)
				_level = owner.getLevel();
			else
				_level = template.level;
			_exp = getExpForThisLevel();
		}

		byte minLevel =  PetDataTable.getInstance().getInfo(template.npcId, template.level) == null ? 1 : (byte)PetDataTable.getInstance().getInfo(template.npcId, template.level).getMinLevel();

		while(_exp >= getExpForNextLevel() && _level < ConfigValue.MaxPetLevel)
			_level++;

		while(_exp < getExpForThisLevel() && _level > minLevel)
			_level--;

		if(_level < minLevel)
		{
			_level = minLevel;
			_exp = getExpForThisLevel();
		}
		
		if(PetDataTable.isPremiumPet(template.npcId))
		{
			_level = owner.getLevel();
			_exp = getExpForNextLevel();
		}

		_data = PetDataTable.getInstance().getInfo(template.npcId, _level);

		_inventory = new PetInventory(this);
		_inventory.restore();

		// сохраним, а то при выходе игрока не сможем получить эти данные
		_ownerObjectId = owner.getObjectId();
		// на олимпе не восстанавливаем и на ивентах
		if(owner.getOlympiadGame() == null && !owner.isInOlympiadMode() && !Olympiad.isRegisteredInComp(owner) && owner.getTeam() == 0)
			PlayerData.getInstance().restoreEffects(this); // восстанавливаем эффекты суммона

		//transferPetItems();

		startFeed();
	}

	private void transferPetItems()
	{
		L2Player owner = getPlayer();
		if(owner == null)
			return;

		boolean transferred = false;

		for(L2ItemInstance item : owner.getInventory().getItemsList())
			if(!item.isEquipped() && (item.getCustomFlags() & L2ItemInstance.FLAG_PET_EQUIPPED) == L2ItemInstance.FLAG_PET_EQUIPPED)
			{
				if(_inventory.getTotalWeight() + item.getItem().getWeight() * item.getCount() > getMaxLoad())
				{
					owner.sendPacket(Msg.EXCEEDED_PET_INVENTORYS_WEIGHT_LIMIT);
					continue;
				}
				if(!item.canBeDropped(owner, false))
					continue;
				item = owner.getInventory().dropItem(item, item.getCount(), false);
				item.setCustomFlags(item.getCustomFlags() | L2ItemInstance.FLAG_PET_EQUIPPED, true);
				_inventory.addItem(item);
				tryEquipItem(item, false);
				transferred = true;
			}

		if(transferred)
		{
			sendItemList();
			broadcastPetInfo();
			owner.sendPacket(new ItemList(owner, false));
		}
	}

	public boolean tryEquipItem(L2ItemInstance item, boolean broadcast)
	{
		if(!item.isEquipable())
			return false;

		int petId = ((L2NpcTemplate) _template).npcId;

		if(item.getItem().isPendant() //
				|| PetDataTable.isWolf(petId) && item.getItem().isForWolf() //
				|| PetDataTable.isHatchling(petId) && item.getItem().isForHatchling() //
				|| PetDataTable.isStrider(petId) && item.getItem().isForStrider() //
				|| PetDataTable.isGWolf(petId) && item.getItem().isForGWolf() //
				|| PetDataTable.isBabyPet(petId) && item.getItem().isForPetBaby() //
				|| PetDataTable.isImprovedBabyPet(petId) && item.getItem().isForPetBaby() //
		)
		{
			if(item.isEquipped())
				_inventory.unEquipItemInSlot(item.getEquipSlot());
			else
				_inventory.equipItem(item, true);

			if(broadcast)
			{
				sendItemList();
				broadcastPetInfo();
			}
			return true;
		}
		return false;
	}

	public boolean tryFeedItem(L2ItemInstance item, boolean manual)
	{
		if(item == null)
			return false;

		boolean deluxFood = PetDataTable.isStrider(getNpcId()) && item.getItemId() == Deluxe_Food_for_Strider;
		if(getFoodId() != item.getItemId() && !deluxFood)
			return false;

		int newFed = Math.min(getMaxFed(), getCurrentFed() + Math.max(getMaxFed() * getAddFed() * (deluxFood ? 2 : 1) / 100, 1));
		if(getCurrentFed() != newFed)
		{
			int count = PetDataTable.isPremiumPet(getNpcId()) ? ConfigValue.VitaminPetRegenItemCount : 1;
			if(item == null || item.getCount() < count)
				return false;

			removeItemFromInventory(item, count, true);
			setCurrentFed(newFed);

			if(!manual)
				getPlayer().sendPacket(new SystemMessage(SystemMessage.PET_TOOK_S1_BECAUSE_HE_WAS_HUNGRY).addItemName(item.getItemId()));
			else if(getCurrentFed() < (((PetDataTable.isPremiumPet(getNpcId()) ? ConfigValue.VitaminPetHungryPercent : 55) / 100f) * getMaxFed()))
				sendPacket(new SystemMessage(SystemMessage.YOUR_PET_ATE_A_LITTLE_BUT_IS_STILL_HUNGRY));
			
			sendPetStatusUpdate();
		}
		return true;
	}

	public boolean tryFeed()
	{
		L2ItemInstance food = getInventory().getItemByItemId(getFoodId());
		if(food == null && PetDataTable.isStrider(getNpcId()))
			food = getInventory().getItemByItemId(Deluxe_Food_for_Strider);
		return tryFeedItem(food, false);
	}

	@Override
	public final boolean isHungry()
	{
		if(PetDataTable.isPremiumPet(getNpcId()))
			return getCurrentFed() < ConfigValue.VitaminPetHungryPercent / 100f * getMaxFed();
		return getCurrentFed() < 0.55 * getMaxFed();
	}

	@Override
	public void addExpAndSp(long addToExp, long addToSp)
	{
		addExpAndSp(addToExp, addToSp, true, true);
	}

	@Override
	public void addExpAndSp(long addToExp, long addToSp, boolean applyBonus, boolean appyToPet)
	{
		L2Player owner = getPlayer();
		if(owner == null)
			return;

		if(PetDataTable.isPremiumPet(getNpcId()))
			return;

		if(getCurrentFed() <= 0.01 * getMaxFed())
			return;

		if(_exp > getMaxExp())
			_exp = getMaxExp();

		if(addToExp + getExp() > getExpForMaxLevel())
		{
			addToExp = getExpForMaxLevel() - getExp();
		}
		
		_exp += addToExp;
		_sp += addToSp;

		if(addToExp > 0 || addToSp > 0)
			owner.sendPacket(new SystemMessage(SystemMessage.THE_PET_ACQUIRED_EXPERIENCE_POINTS_OF_S1).addNumber(addToExp));

		int old_level = _level;

		while(_exp >= getExpForNextLevel() && _level < ConfigValue.MaxPetLevel)
			_level++;

		while(_exp < getExpForThisLevel() && _level > getMinLevel())
			_level--;

		if(old_level != _level)
		{
			updateControlItem();
			updateData();
		}

		boolean needStatusUpdate = true;

		if(old_level < _level)
		{
			owner.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2PetInstance.PetLevelUp", owner).addNumber(_level));
			broadcastPacket2(new SocialAction(getObjectId(), SocialAction.LEVEL_UP));
			setCurrentHpMp(getMaxHp(), getMaxMp());
			needStatusUpdate = false;
		}

		if(needStatusUpdate && (addToExp > 0 || addToSp > 0))
			broadcastStatusUpdate();
	}

	@Override
	public boolean consumeItem(int itemConsumeId, int itemCount)
	{
		L2ItemInstance item = getInventory().getItemByItemId(itemConsumeId);
		return !(item == null || item.getCount() < itemCount) && getInventory().destroyItem(item, itemCount, false) != null;
	}

	private void deathPenalty()
	{
		if(isInZoneBattle())
			return;
		int lvl = getLevel();
		double percentLost = -0.07 * lvl + 6.5;
		// Calculate the Experience loss
		lostExp = (int) Math.round((getExpForNextLevel() - getExpForThisLevel()) * percentLost / 100);
		addExpAndSp(-lostExp, 0);
	}

	@Override
	public void doDie(L2Character killer)
	{
		dieLock.lock();
		try
		{
			if(_killedAlreadyPet)
				return;
			_killedAlreadyPet = true;
		}
		finally
		{
			dieLock.unlock();
		}

		super.doDie(killer);
		// сохраняем эффекты суммона
		PlayerData.getInstance().storeEffects(this);
		//if(isSalvation() && !getPlayer().isInOlympiadMode())
        //    getPlayer().reviveRequest(getPlayer(), 100, false);
        for(L2Effect e : getEffectList().getAllEffects())
            if(e.getEffectType() == EffectType.BlessNoblesse || e.getSkill().getId() == L2Skill.SKILL_FORTUNE_OF_NOBLESSE || e.getSkill().getId() == L2Skill.SKILL_RAID_BLESSING)
                e.exit(true, false);
		L2Player owner = getPlayer();
		if(owner == null)
		{
			onDecay();
			return;
		}
		if(PetDataTable.isPremiumPet(getNpcId()))
			return;
		stopFeed();
		deathPenalty();
		owner.sendPacket(Msg.THE_PET_HAS_BEEN_KILLED_IF_YOU_DO_NOT_RESURRECT_IT_WITHIN_24_HOURS_THE_PETS_BODY_WILL_DISAPPEAR_ALONG_WITH_ALL_THE_PETS_ITEMS);
		DecayTaskManager.getInstance().addDecayTask(this, 86400000);
	}

	@Override
	public void doPickupItem(L2Object object)
	{
		L2Player owner = getPlayer();
		if(owner == null)
			return;

		stopMove();

		if(!object.isItem())
		{
			owner.sendActionFailed();
			return;
		}

		L2ItemInstance target = (L2ItemInstance) object;

		if(target.isCursed())
		{
			owner.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1).addItemName(target.getItemId()));
			return;
		}

		synchronized (target)
		{
			if(!target.isVisible())
			{
				owner.sendActionFailed();
				return;
			}

			if(getInventory().getTotalWeight() + target.getItem().getWeight() * target.getCount() > getMaxLoad())
			{
				owner.sendPacket(Msg.EXCEEDED_PET_INVENTORYS_WEIGHT_LIMIT);
				return;
			}

			if(target.isHerb())
			{
				L2Skill[] skills = target.getItem().getAttachedSkills();
				if(skills != null && skills.length > 0)
					for(L2Skill skill : skills)
						altUseSkill(skill, this);
				target.deleteMe();
				return;
			}

			if(!target.pickupMe(this))
			{
				owner.sendActionFailed();
				return;
			}
		}

		if(owner.getParty() == null || owner.getParty().getLootDistribution() == L2Party.ITEM_LOOTER)
		{
			owner.sendPacket(SystemMessage.obtainItemsBy(target, "Your pet"));
			target.setCustomFlags(target.getCustomFlags() | L2ItemInstance.FLAG_PET_EQUIPPED, true);
			synchronized (_inventory)
			{
				_inventory.addItem(target);
			}
			sendItemList();
			sendPetInfo();
		}
		else
			owner.getParty().distributeItem(owner, target);
		broadcastPickUpMsg(target);

		setFollowStatus(isFollow(), true);
	}

	public void doRevive(double percent)
	{
		restoreExp(percent);
		doRevive();
	}

	@Override
	public void doRevive()
	{
		stopDecay();
		super.doRevive();
		startFeed();
		if(!isHungry())
			setRunning();
		setFollowStatus(true, true);
	}

	@Override
	public int getAccuracy()
	{
		return (int) calcStat(Stats.p_hit, _data.getAccuracy(), null, null);
	}

	@Override
	public L2ItemInstance getActiveWeaponInstance()
	{
		return null;
	}

	@Override
	public WeaponType getFistWeaponType()
	{
		return getTemplate().base_attack_type;
	}

	@Override
	public L2Weapon getActiveWeaponItem()
	{
		return null;
	}

	public L2ItemInstance getControlItem()
	{
		L2Player owner = getPlayer();
		if(owner == null)
			return null;
		int item_obj_id = getControlItemObjId();
		if(item_obj_id == 0)
			return null;
		return owner.getInventory().getItemByObjectId(item_obj_id);
	}

	@Override
	public int getControlItemObjId()
	{
		return _controlItemObjId;
	}

	public void setControlItemObjId(int id)
	{
		_controlItemObjId = id;
	}

	@Override
	public int getCriticalHit(L2Character target, L2Skill skill)
	{
		return (int) calcStat(Stats.CRITICAL_BASE, _data.getCritical(), target, skill);
	}

	@Override
	public int getCurrentFed()
	{
		return _curFed;
	}

	@Override
	public int getEvasionRate(L2Character target)
	{
		return (int) calcStat(Stats.EVASION_RATE, _data.getEvasion(), target, null);
	}

	@Override
	public long getExpForNextLevel()
	{
		try
		{
			return PetDataTable.getInstance().getInfo(getNpcId(), (byte) (_level + 1)).getExp();
		}
		catch(Exception e)
		{
			return Long.MAX_VALUE;
		}
	}

	@Override
	public long getExpForThisLevel()
	{
		return PetDataTable.getInstance().getInfo(getNpcId(), _level > ConfigValue.MaxPetLevel ? ConfigValue.MaxPetLevel+1 : _level).getExp();
	}

	public long getExpForMaxLevel()
	{
		return PetDataTable.getInstance().getInfo(getNpcId(), ConfigValue.MaxPetLevel+1).getExp();
	}


	public int getFoodId()
	{
		return _data.getFoodId();
	}

	public int getAddFed()
	{
		return _data.getAddFed();
	}

	@Override
	public PetInventory getInventory()
	{
		return _inventory;
	}

	@Override
	public final byte getLevel()
	{
		return _level;
	}
	
	public void setLevel(byte level)
	{
		_level = level;
	}

	@Override
	public double getLevelMod()
	{
		return (89. + getLevel()) / 100.0;
	}

	public int getMinLevel()
	{
		return _data.getMinLevel();
	}

	public long getMaxExp()
	{
		return PetDataTable.getInstance().getInfo(getNpcId(), ConfigValue.MaxPetLevel + 1).getExp();
	}

	@Override
	public int getMaxFed()
	{
		return _data.getFeedMax();
	}

	@Override
	public int getMaxLoad()
	{
		return (int) (calcStat(Stats.MAX_LOAD, _data.getMaxLoad(), null, null) * getPlayer().getRateMaxLoad());
	}

	@Override
	public int getMaxHp()
	{
		return (int) calcStat(Stats.p_max_hp, _data.getHP(), null, null);
	}

	@Override
	public int getMaxMp()
	{
		return (int) calcStat(Stats.p_max_mp, _data.getMP(), null, null);
	}

	@Override
	public double getHpReg()
	{
		return _data.getHpRegen();
	}

	@Override
	public double getMpReg()
	{
		return _data.getMpRegen();
	}

	@Override
	public int getPAtk(L2Character target)
	{
		// В базе указаны параметры, уже домноженные на этот модификатор, для удобства. Поэтому вычисляем и убираем его.
		double mod = Formulas.STRbonus[getSTR()] * getLevelMod();
		return (int) calcStat(Stats.p_physical_attack, _data.getPAtk() / mod, target, null);
	}

	@Override
	public int getPDef(L2Character target)
	{
		// В базе указаны параметры, уже домноженные на этот модификатор, для удобства. Поэтому вычисляем и убираем его.
		double mod = getLevelMod();
		return (int) calcStat(Stats.p_physical_defence, _data.getPDef() / mod, target, null);
	}

	@Override
	public int getMAtk(L2Character target, L2Skill skill)
	{
		// В базе указаны параметры, уже домноженные на этот модификатор, для удобства. Поэтому вычисляем и убираем его.
		double ib = Formulas.INTbonus[getINT()];
		double lvlb = getLevelMod();
		double mod = lvlb * lvlb * ib * ib;
		return (int) calcStat(Stats.p_magical_attack, _data.getMAtk() / mod, target, skill);
	}

	@Override
	public int getMDef(L2Character target, L2Skill skill)
	{
		// В базе указаны параметры, уже домноженные на этот модификатор, для удобства. Поэтому вычисляем и убираем его.
		double mod = Formulas.MENbonus[getMEN()] * getLevelMod();
		return (int) calcStat(Stats.p_magical_defence, _data.getMDef() / mod, target, skill);
	}

	@Override
	public double getPAtkSpd()
	{
		double val = calcStat(Stats.p_attack_speed, calcStat(Stats.ATK_BASE, _data.getAtkSpeed(), null, null), null, null);
		return isHungry() ? val / 2 : val;
	}

	@Override
	public double getMAtkSpd()
	{
		double val = calcStat(Stats.p_magic_speed, _data.getCastSpeed(), null, null);
		return isHungry() ? val / 2 : val;
	}

	@Override
	public int getRunSpeed()
	{
		if(isHungry())
			return getSpeed(_data.getSpeed()) / 2;
		return getSpeed(_data.getSpeed());
	}

	@Override
	public int getSoulshotConsumeCount()
	{
		return _data.getSoulShots();
	}

	@Override
	public int getSpiritshotConsumeCount()
	{
		return  _data.getSpiritShots();
	}

	@Override
	public L2ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}

	@Override
	public L2Weapon getSecondaryWeaponItem()
	{
		return null;
	}

	public int getSkillLevel(int skillId)
	{
		if(_skills == null || _skills.get(skillId) == null)
			return -1;
		int lvl = getLevel();
		return lvl > 70 ? 7 + (lvl - 70) / 5 : lvl / 10;
	}

	@Override
	public int getSummonType()
	{
		return 2;
	}

	@Override
	public L2NpcTemplate getTemplate()
	{
		return (L2NpcTemplate) _template;
	}

	@Override
	public boolean isMountable()
	{
		return _data.isMountable();
	}

	public boolean isRespawned()
	{
		return _respawned;
	}

	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, L2Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean isDot, double i2, boolean sendMesseg, boolean bow, boolean crit, boolean tp)
	{
		if(attacker.isPlayable() && isInZoneBattle() != attacker.isInZoneBattle())
		{
			L2Player player = attacker.getPlayer();
			if(player != null)
				player.sendPacket(Msg.INVALID_TARGET());
			return;
		}

		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, isDot, i2, sendMesseg, bow, crit, tp);

		L2Player owner = getPlayer();
		if(owner == null)
			return;

		if(!isDead() && damage > 0)
		{
			SystemMessage sm = new SystemMessage(SystemMessage.THE_PET_RECEIVED_DAMAGE_OF_S2_CAUSED_BY_S1);
			if(attacker.isNpc())
				sm.addNpcName(((L2NpcInstance) attacker).getTemplate().npcId);
			else
				sm.addString(attacker.getName());
			sm.addNumber((long) damage);
			owner.sendPacket(sm);
		}
	}

	public void removeItemFromInventory(L2ItemInstance item, int count, boolean toLog)
	{
		synchronized (_inventory)
		{
			_inventory.destroyItem(item.getObjectId(), count, toLog);
		}
	}

	public void restoreExp(double percent)
	{
		if(lostExp != 0)
		{
			addExpAndSp((long) (lostExp * percent / 100.), 0);
			lostExp = 0;
		}
	}

	@Override
	public void sendChanges()
	{
		L2Player owner = getPlayer();
		if(owner == null || owner.getInventory().getItemByObjectId(getControlItem().getObjectId()) == null)
			return;
		broadcastStatusUpdate();
		owner.sendPacket(new PetItemList(this));
	}

	public void setCurrentFed(int num)
	{
		_curFed = Math.min(getMaxFed(), Math.max(0, num));
		boolean lastHungryState = isHungry();
		_curFed = num > getMaxFed() ? getMaxFed() : num;
		if(_curFed > 0.01 * getMaxFed())
			setNpcState(101);
		sendPacket(new SetupGauge(getObjectId(), 3, (getCurrentFed() * 10000) / getFeedConsume(), (getMaxFed() * 10000) / getFeedConsume()));
		if(lastHungryState != isHungry())
			broadcastUserInfo(true);
	}

	public void setRespawned(boolean respawned)
	{
		_respawned = respawned;
	}

	@Override
	public void setSp(int sp)
	{
		_sp = sp;
	}

	public void startFeed()
	{
		stopFeed();
		if(!isDead() && getPlayer() != null)
			_feedTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FeedTask(), 10000, 10000);
	}

	private void stopFeed()
	{
		if(_feedTask != null)
		{
			_feedTask.cancel(false);
			_feedTask = null;
		}
	}

	@Override
	public void stopDecay()
	{
		DecayTaskManager.getInstance().cancelDecayTask(this);
	}

	public int getFeedConsume()
	{
		if(isInCombat())
			return _data.getFeedBattle();
		else
			return _data.getFeedNormal();
	}

	@Override
	public void onDecay()
	{
		L2Player owner = getPlayer();
		if(owner != null && getNpcId() == 16050)
		{
			int i2 = (Rnd.get(10) + 1);
			if(owner.getPkKills() <= i2)
				owner.setPkKills(0);
			else
				owner.setPkKills(owner.getPkKills() - i2);
			owner.broadcastStatusUpdate();
		}
		deleteMe();
	}

	@Override
	public void deleteMe()
	{
		giveAllToOwner();
		PlayerData.getInstance().destroyControlItem(getPlayer(), getControlItemObjId()); // this should also delete the pet from the db
		stopFeed();
		super.deleteMe();
	}

	@Override
	public void unSummon()
	{
		stopFeed();
		PlayerData.getInstance().storeEffects(this);
		super.deleteMe();
		PlayerData.getInstance().store_pet(this);
	}

	public synchronized void giveAllToOwner()
	{
		L2Player owner = getPlayer();
		if(owner != null)
		{
			synchronized (_inventory)
			{
				for(L2ItemInstance i : _inventory.getItems())
				{
					L2ItemInstance item = _inventory.dropItem(i, i.getCount(), false, true);
					if(owner != null)
					{
						if(owner.getInventoryLimit() * 0.8 > owner.getInventory().getSize())
							owner.getInventory().addItem(item);
						else
							owner.getWarehouse().addItem(item, owner.getName());
					}
					else
						PlayerData.getInstance().giveAllToOwnerBD(this);
						//item.dropMe(this, getLoc().changeZ(25));
				}
			_inventory.getItemsList().clear();
			}
		}
		else
			PlayerData.getInstance().giveAllToOwnerBD(this);
	}

	public void updateControlItem()
	{
		L2ItemInstance controlItem = getControlItem();
		if(controlItem == null)
			return;
		controlItem.setEnchantLevel(_level);
		controlItem.setCustomType2(getName() == null ? 0 : 1);

		L2Player owner = getPlayer();
		if(owner != null)
			owner.sendPacket(new InventoryUpdate().addModifiedItem(controlItem));
	}

	private void updateData()
	{
		_data = PetDataTable.getInstance().getInfo(getTemplate().npcId, _level);
	}

	@Override
	public float getExpPenalty()
	{
		return _data.getExpType();
	}

	@Override
	public void displayHitMessage(L2Character target, int damage, boolean crit, boolean miss)
	{
		L2Player owner = getPlayer();
		if(owner == null)
			return;
		if(crit)
			owner.sendPacket(Msg.PETS_CRITICAL_HIT);
		if(miss)
			owner.sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(this));
		else
			owner.sendPacket(new SystemMessage(SystemMessage.THE_PET_GAVE_DAMAGE_OF_S1).addNumber(damage));
	}

	@Override
	public int getFormId()
	{
		switch(getNpcId())
		{
			case PetDataTable.GREAT_WOLF_ID:
			case PetDataTable.WGREAT_WOLF_ID:
			case PetDataTable.FENRIR_WOLF_ID:
			case PetDataTable.WFENRIR_WOLF_ID:
				if(getLevel() >= 70)
					return 3;
				else if(getLevel() >= 65)
					return 2;
				else if(getLevel() >= 60)
					return 1;
				break;
		}
		return 0;
	}

	// Не отображаем на петах значки клана. 
	@Override 
	public boolean isCrestEnable() 
	{ 
		return false; 
	}
	
	@Override
	public String toString()
	{
		return "Summon: name-" + getName() + "[" + getNpcId() + "], ownerObjId-" + _ownerObjectId;
	}

	@Override
	public boolean isPet()
	{
		return true;
	}
}