package l2open.gameserver.model;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Scripts;
import l2open.extensions.scripts.Scripts.ScriptClassAndMethod;
import l2open.gameserver.clientpackets.EnterWorld;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.instancemanager.DimensionalRiftManager;
import l2open.gameserver.instancemanager.PartyRoomManager;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2TerritoryFlagInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.tables.PetDataTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2PlayerTemplate;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.reference.HardReference;

import java.util.Collection;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class BotImpl extends L2Player
{
	public BotImpl(int objectId, L2PlayerTemplate template, String accountName, int bot)
	{
		super(objectId, template, accountName, bot);
	}

	@Override
	public BotImpl getBotInstance()
	{
		return this;
	}

	@Override
	public boolean isConnected()
	{
		return true;
	}

	@Override
	public boolean isFantome()
	{
		return true;
	}

	@Override
	public boolean isOnline()
	{
		return true;
	}

	@Override
	public void sendUserInfo(boolean force)
	{}

	@Override
	public void sendStatusUpdate(boolean broadCast, int... fields)
	{}

	@Override
	public void sendReuseMessage(L2Skill skill)
	{}

	@Override
	public void sendDisarmMessage(L2ItemInstance wpn)
	{}

	@Override
	public void sendMessage(CustomMessage message)
	{}

	@Override
	public void sendMessage(String message)
	{}

	@Override
	public void sendPacket(L2GameServerPacket... packets)
	{}

	@Override
	public void sendPackets(Collection<L2GameServerPacket> packets)
	{}

	@Override
	protected void sendPacketsStatsUpdate(Collection<L2GameServerPacket> packets)
	{}

	@Override
	protected void sendPacketStatsUpdate(L2GameServerPacket... packets)
	{}

	@Override
	public void setVar(String name, String value, long expirationTime)
	{
		user_variables.put(name, value);
	}

	@Override
	public void setOnlineStatus(boolean isOnline)
	{
		_isOnline = isOnline;
	}
	// 
	@Override
	public void teleToLocation(int x, int y, int z, int ref, int valid)
	{
		if(isFakeDeath())
			breakFakeDeath();

		if(isTeleporting() || inObserverMode())
			return;

		abortCast(true);

		clearHateList(true);

		if(!isFlying() && !L2World.isWater(x, y, z))
			z = GeoEngine.getHeight(x, y, z, getReflection().getGeoIndex());

		if(DimensionalRiftManager.getInstance().checkIfInRiftZone(getLoc(), true))
		{
			if(isInParty() && getParty().isInDimensionalRift())
			{
				Location newCoords = DimensionalRiftManager.getInstance().getRoom(0, 0).getTeleportCoords();
				x = newCoords.x;
				y = newCoords.y;
				z = newCoords.z;
				getParty().getDimensionalRift().usedTeleport(this);
			}
		}

		setTarget(null);

		if(isLogoutStarted())
			return;

		setIsTeleporting(60000);

		decayMe();

		setXYZInvisible(x, y, z);
		if(ref != getReflection().getId())
			setReflection(ref);

		setLastClientPosition(null);
		setLastServerPosition(null);

		getListeners().onTeleport(x, y, z, ref);

		if(getEventMaster() != null)
			getEventMaster().onTeleportPlayer(this, x, y, z, ref);

		Object[] script_args = new Object[] { this, new Location(x, y, z) };
		for(ScriptClassAndMethod handler : Scripts.onPlayerTeleport)
			callScripts(handler.scriptClass, handler.method, script_args);

		onTeleported();
	}

	@Override
	public void rewardSkills()
	{
		int unLearnable = 0;
		GArray<L2SkillLearn> skills = getAvailableSkills(getClassId());
		while(skills.size() > unLearnable)
		{
			unLearnable = 0;
			for(L2SkillLearn s : skills)
			{
				L2Skill sk = SkillTable.getInstance().getInfo(s.id, s.skillLevel);
				if(sk == null || !sk.getCanLearn(getClassId()) || s.getMinLevel() > ConfigValue.AutoLearnSkillsMaxLevel || s.getItemId() > 0 && !ConfigValue.AutoLearnForgottenSkills)
				{
					unLearnable++;
					continue;
				}
				addSkill(sk, false);
			}
			skills = getAvailableSkills(getClassId());
		}

		refreshOverloaded();
		checkGradeExpertiseUpdate();
		checkWeaponPenalty();
		checkArmorPenalty();
	}

	@Override
	public void setActiveSubClass(int subId, boolean store)
	{
		final L2SubClass sub = getSubClasses().get(subId);
		if(sub == null)
		{
			System.out.print("WARNING! setActiveSubClass<?> :: sub == null :: subId == " + subId);
			Thread.dumpStack();
			return;
		}
		if(subId == getBaseClassId())
			sub.setBase(true);
		else
			sub.setBase(false);
		if(getActiveClass() != null)
			if(QuestManager.getQuest(422) != null)
			{
				String qn = QuestManager.getQuest(422).getName();
				if(qn != null)
				{
					QuestState qs = getQuestState(qn);
					if(qs != null)
						qs.exitCurrentQuest(true);
				}
			}

		if(store)
		{
			final L2SubClass oldsub = getActiveClass();
			oldsub.setCp(getCurrentCp());
			oldsub.setHp(getCurrentHp());
			oldsub.setMp(getCurrentMp());
			oldsub.setActive(false);
			getSubClasses().put(getActiveClassId(), oldsub);
		}

		sub.setActive(true);
		setActiveClass(sub);
		getSubClasses().put(getActiveClassId(), sub);

		setClassId(subId, false);

		if(!ConfigValue.Multi_Enable2)
			removeAllSkills(false);

		getEffectList().stopAllEffects(false);

		if(getPet() != null && (getPet().isSummon() || ConfigValue.ImprovedPetsLimitedUse && (getPet().getNpcId() == PetDataTable.IMPROVED_BABY_KOOKABURRA_ID && !isMageClass() || getPet().getNpcId() == PetDataTable.IMPROVED_BABY_BUFFALO_ID && isMageClass())))
			getPet().unSummon();

		for(L2Cubic cubic : getCubics())
			cubic.deleteMe();

		getCubics().clear();
		setAgathion(0);

		rewardSkills();

		getInventory().refreshListeners(false);
		getInventory().checkAllConditions();

		for(int i = 0; i < 3; i++)
			_henna[i] = null;

		setCurrentHpMp(sub.getHp(), sub.getMp());
		setCurrentCp(sub.getCp());
		broadcastUserInfo(true);
		updateStats();

		broadcastPacket(new SocialAction(getObjectId(), SocialAction.LEVEL_UP));

		getDeathPenalty().restore();

		setIncreasedForce(0);

		ThreadPoolManager.getInstance().schedule(new DisableToogle(), 500); //Затычка для канцела туглов при смене сабов
	}

	@Override
	public void deleteMe()
	{
		getAI().stopAITask();

		if(isLogoutStarted())
			return;

		L2WorldRegion observerRegion = _observNeighbor;
		if(observerRegion != null)
			observerRegion.removeObject(this, false);

		if(recVoteTask != null)
		{
			recVoteTask.cancel(true);
			recVoteTask = null;
		}
		setLogoutStarted(true);

		prepareToLogout();

		// Останавливаем и запоминаем все квестовые таймеры
		Quest.pauseQuestTimes(this);

		setTarget(null);
		stopMove();
		stopRegeneration();
		decayMe();
		L2World.removeObject(this);
		L2ObjectsStorage.remove(this);
		HardReference<? extends L2Object> reference = getRef();
		if(reference != null)
			reference.clear();

		_isDeleting = true;

		getEffectList().stopAllEffects(true);

		setMassUpdating(true);

		//Send friendlists to friends that this player has logged off
		EnterWorld.notifyFriends(this, false);

		if(isInTransaction())
			getTransaction().cancel();

		// Set the online Flag to True or False and update the characters table of the database with online status and lastAccess (called when login and logout)
		try
		{
			setOnlineStatus(false);
		}
		catch(Throwable t)
		{
			_log.log(Level.WARNING, "deletedMe()", t);
		}

		// Stop the HP/MP/CP Regeneration task (scheduled tasks)
		try
		{
			stopAllTimers();
		}
		catch(Throwable t)
		{
			_log.log(Level.WARNING, "deletedMe()", t);
		}

		// Cancel Attak or Cast
		try
		{
			setTarget(null);
		}
		catch(Throwable t)
		{
			_log.log(Level.WARNING, "deletedMe()", t);
		}

		try
		{
			if(isCombatFlagEquipped())
			{
				L2ItemInstance flag = getActiveWeaponInstance();
				if(flag != null)
				{
					int customFlags = flag.getCustomFlags();
					flag.setCustomFlags(0, false);
					flag = getInventory().dropItem(flag, 1, true);
					flag.setCustomFlags(customFlags, false);
					flag.spawnMe2(flag.getLoc().correctGeoZ(), false);
				}
			}

			if(isTerritoryFlagEquipped())
			{
				L2ItemInstance flag = getActiveWeaponInstance();
				if(flag != null && flag.getCustomType1() != 77) // 77 это эвентовый флаг
				{
					L2TerritoryFlagInstance flagNpc = TerritorySiege.getNpcFlagByItemId(flag.getItemId());
					flagNpc.drop(this);
				}
			}
		}
		catch(Throwable t)
		{
			_log.log(Level.WARNING, "deletedMe()", t);
		}

		if(CursedWeaponsManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()) != null)
			CursedWeaponsManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()).setPlayer(null);

		if(getPartyRoom() > 0)
		{
			PartyRoom room = PartyRoomManager.getInstance().getRooms().get(getPartyRoom());
			if(room != null)
				if(room.getLeader() == null || room.getLeader().equals(this))
					PartyRoomManager.getInstance().removeRoom(room.getId());
				else
					room.removeMember(this, false);
		}

		setPartyRoom(0);

		setEffectList(null);

		// Update database with items in its inventory and remove them from the world
		try
		{
			getInventory().deleteMe();
		}
		catch(Throwable t)
		{
			_log.log(Level.WARNING, "deletedMe()", t);
		}

		removeTrap();

		if(_decoy != null)
			_decoy.unSummon();

		stopPvPFlag();

		bookmarks.clear();
		_chat.clear();
		_chat_tell.clear();
		_mail.clear();
		if(_buffSchem != null)
			_buffSchem.clear();
		if(_tpSchem != null)
			_tpSchem.clear();
		_warehouse = null;
		_freight = null;
		_ai = null;
		_summon = null;
		_arrowItem = null;
		_fistsWeaponItem = null;
		_chars = null;
		_enchantScroll = null;
		_agathion = null;
		_lastNpc = null;
		_obsLoc = null;
		_observNeighbor = null;
		_buffSchem = null;
		_tpSchem = null;
		if(_taskforfish != null)
		{
			_taskforfish.cancel(false);
			_taskforfish = null;
		}
		if(_kickTask != null)
		{
			_kickTask.cancel(true);
			_kickTask = null;
		}
		if(_bonusExpiration != null)
		{
			_bonusExpiration.cancel(true);
			_bonusExpiration = null;
		}
		for(Future<?> be : _bonusExpiration2)
			if(be != null)
			{
				be.cancel(true);
				be = null;
			}

		if(_pcCafePointsTask != null)
		{
			_pcCafePointsTask.cancel(false);
			_pcCafePointsTask = null;
		}
		if(_AttainmentTask != null)
		{
			_AttainmentTask.cancel(false);
			_AttainmentTask = null;
		}
		if(_unjailTask != null)
		{
			_unjailTask.cancel(false);
			_unjailTask = null;
		}
		if(_heroTask != null)
		{
			_heroTask.cancel(false);
			_heroTask = null;
		}
		if(_bot_check != null)
		{
			_bot_check.cancel(false);
			_bot_check = null;
		}
		if(_bot_kick != null)
		{
			_bot_kick.cancel(false);
			_bot_kick = null;
		}
		if(_enchantSucer != null)
		{
			_enchantSucer.cancel(false);
			_enchantSucer = null;
		}
		if(_test_task != null)
		{
			_test_task.cancel(false);
			_test_task = null;
		}
	}

	@Override
	public boolean isPhantom()
	{
		return true;
	}

	@Override
	public boolean isBot()
	{
		return true;
	}
}
