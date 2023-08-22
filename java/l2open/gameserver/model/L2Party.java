package l2open.gameserver.model;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.communitybbs.PartyMaker.PartyMaker;
import l2open.gameserver.communitybbs.PartyMaker.PartyMakerGroup;
import l2open.gameserver.instancemanager.PartyRoomManager;
import l2open.gameserver.model.L2ObjectTasks.SoulConsumeTask;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.entity.DimensionalRift;
import l2open.gameserver.model.entity.DragonValley;
import l2open.gameserver.model.entity.EventMaster;
import l2open.gameserver.model.entity.SevenSignsFestival.DarknessFestival;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.*;

import java.util.concurrent.CopyOnWriteArrayList;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

public class L2Party
{
	private final List<L2Player> _members = new CopyOnWriteArrayList<L2Player>();
	private int _partyLvl = 0;
	private int _itemDistribution = 0;
	private int _itemOrder = 0;
	private int _dr;
	private int _reflection;
	private L2CommandChannel _commandChannel;

	public static final int ITEM_LOOTER = 0;
	public static final int ITEM_RANDOM = 1;
	public static final int ITEM_RANDOM_SPOIL = 2;
	public static final int ITEM_ORDER = 3;
	public static final int ITEM_ORDER_SPOIL = 4;

	public float _rateExp;
	public float _rateSp;
	public float _rateDrop;
	public float _rateAdena;
	public float _rateSpoil;
	private final UpdatePositionTask posTask = new UpdatePositionTask(this);
	private ScheduledFuture<?> posTaskThread;
    private int newLootDistr;
    private boolean votingChangeLoot;
    private GCSArray<L2Player> lootResponsers = new GCSArray<L2Player>();
    private static final int[] LOOT_SYSSTRINGS = {487, 488, 798, 799, 800};
    private ScheduledFuture<?> voteTask;

    /**
	 * constructor ensures party has always one member - leader
	 * @param leader создатель парти
	 * @param itemDistribution режим распределения лута
	 */
	public L2Party(L2Player leader, int itemDistribution)
	{
		_itemDistribution = itemDistribution;
		_members.add(leader);
		_partyLvl = leader.getLevel();
		posTaskThread = ThreadPoolManager.getInstance().schedule(posTask, 11000);

		// для надежности
		_rateExp = leader.getBonus().RATE_XP*leader.getAltBonus();
		_rateSp = leader.getBonus().RATE_SP*leader.getAltBonus();
		_rateAdena = leader.getBonus().RATE_DROP_ADENA*leader.getAltBonus();
		_rateDrop = leader.getBonus().RATE_DROP_ITEMS*leader.getAltBonus();
		_rateSpoil = leader.getBonus().RATE_DROP_SPOIL*leader.getAltBonus();
	}

	/**
	 * @return number of party members
	 */
	public int getMemberCount()
	{
		return _members.size();
	}

	public int getMemberCountInRange(L2Player player, int range)
	{
		int ret = 0;

		for(L2Player member : _members)
			if(member == player || member.getReflectionId() == player.getReflectionId() && member.isInRange(player, range))
				ret++;

		return ret;
	}

	/**
	 * @return all party members
	 */
	public List<L2Player> getPartyMembers()
	{
		return _members;
	}

	public L2Player getPartyMember(int index)
	{
		return _members.get(index);
	}

	public List<Integer> getPartyMembersObjIds()
	{
		List<Integer> result = new ArrayList<Integer>(_members.size());
		for(L2Player member : _members)
			result.add(member.getObjectId());
		return result;
	}

	public List<L2Playable> getPartyMembersWithPets()
	{
		List<L2Playable> result = new ArrayList<L2Playable>();
		for(L2Player member : _members)
		{
			result.add(member);
			if(member.getPet() != null)
				result.add(member.getPet());
		}
		return result;
	}

	public L2Player getRandomMember()
	{
		List<L2Player> members = getPartyMembers();
		return members.get(Rnd.get(members.size()));
	}

	/**
	 * @return random member from party
	 */
	private L2Player getRandomMemberInRange(L2Player player, L2ItemInstance item, int range, L2NpcInstance fromNpc)
	{
		List<L2Player> ret = new ArrayList<L2Player>();
		L2Character for_dist = fromNpc != null ? fromNpc : player;

		for(L2Player member : _members)
			if(member != null && member.getReflectionId() == player.getReflectionId() && member.isInRange(for_dist, range) && !member.isDead() && member.getInventory().validateCapacity(item) && member.getInventory().validateWeight(item))
				ret.add(member);

		return ret.isEmpty() ? null : ret.get(Rnd.get(ret.size()));
	}

	/**
	 * @return next item looter
	 */
	private L2Player getNextLooterInRange(L2Player player, L2ItemInstance item, int range, L2NpcInstance fromNpc)
	{
		L2Character for_dist = fromNpc != null ? fromNpc : player;
		synchronized (_members)
		{
			int antiloop = _members.size();
			while(--antiloop > 0)
			{
				int looter = _itemOrder;
				_itemOrder++;
				if(_itemOrder > _members.size() - 1)
					_itemOrder = 0;

				L2Player ret = looter < _members.size() ? _members.get(looter) : player;

				if(ret != null && ret.getReflectionId() == player.getReflectionId() && ret.isInRange(for_dist, range) && !ret.isDead())
					return ret;
			}
			return player;
		}
	}

	/**
	 * true if player is party leader
	 */
	public boolean isLeader(L2Player player)
	{
		L2Player leader = getPartyLeader();
		return leader != null && player.equals(leader);
	}

	/**
	 * Возвращает лидера партии
	 * @return L2Player Лидер партии
	 */
	public L2Player getPartyLeader()
	{
		if(_members.size() == 0)
			return null;
		return _members.get(0);
	}

	/**
	 * Broadcasts packet to every party member
	 * @param msg packet to broadcast
	 */
	public void broadcastToPartyMembers(L2GameServerPacket... msg)
	{
		for(L2Player member : _members)
			if(member != null)
				member.sendPacket(msg);
	}

	/**
	 * Рассылает текстовое сообщение всем членам группы
	 * @param msg сообщение
	 */
	public void broadcastMessageToPartyMembers(String msg)
	{
		broadcastToPartyMembers(new SystemMessage(msg));
	}

	/**
	 * Рассылает пакет всем членам группы исключая указанного персонажа<BR><BR>
	 */
	public void broadcastToPartyMembers(L2Player exclude, L2GameServerPacket msg)
	{
		for(L2Player member : _members)
			if(member != null && exclude.getObjectId() != member.getObjectId())
				member.sendPacket(msg);
	}

	public void broadcastToPartyMembersInRange(L2Player player, L2GameServerPacket msg, int range)
	{
		for(L2Player member : _members)
			if(member != null && member.getReflectionId() == player.getReflectionId() && player.isInRange(member, range))
				member.sendPacket(msg);
	}

	public void broadcastCSToPartyMembers(L2Player player, L2GameServerPacket msg)
	{
		for(L2Player member : _members)
			if(member != null && !member.isInBlockList(player) && !member.isBlockAll())
				member.sendPacket(msg);
	}

	public boolean containsMember(L2Character player)
	{
		return _members.contains(player.getPlayer());
	}

	/**
	 * adds new member to party
	 * @param player L2Player to add
	 */
	public void addPartyMember(L2Player player)
	{
		L2Player leader = getPartyLeader();
		if(leader == null)
			return;

		synchronized (_members)
		{
			if(_members.isEmpty())
				return;
			if(_members.contains(player))
				return;
			if(_members.size() >= ConfigValue.MAX_SIZE)
			{
				leader.sendPacket(Msg.PARTY_IS_FULL);
				player.sendPacket(Msg.PARTY_IS_FULL);
				return;
			}
			_members.add(player);
		}

		L2Summon player_pet, member_pet;
		Collection<L2GameServerPacket> pmember, pmember_proto = new GArray<L2GameServerPacket>(), pplayer = new GArray<L2GameServerPacket>();

		//sends new member party window for all members
		//we do all actions before adding member to a list, this speeds things up a little
		pplayer.add(new PartySmallWindowAll(this, player));
		pplayer.add(new SystemMessage(SystemMessage.YOU_HAVE_JOINED_S1S_PARTY).addString(leader.getName()));

		pmember_proto.add(new SystemMessage(SystemMessage.S1_HAS_JOINED_THE_PARTY).addString(player.getName()));
		pmember_proto.add(new PartySmallWindowAdd(player));
		pmember_proto.add(new PartySpelled(player, true));
		if((player_pet = player.getPet()) != null)
		{
			pmember_proto.add(new ExPartyPetWindowAdd(player_pet));
			pmember_proto.add(new PartySpelled(player_pet, true));
		}

		for(L2Player member : _members)
			if(member != null && member != player)
			{
				pmember = new GArray<L2GameServerPacket>();
				pmember.addAll(pmember_proto);
				pmember.addAll(RelationChanged.update(member, player, member));
				member.sendPackets(pmember);
				pmember = null;

				pplayer.add(new PartySpelled(member, true));
				if((member_pet = member.getPet()) != null)
					pplayer.add(new PartySpelled(member_pet, true));
				pplayer.addAll(RelationChanged.update(player, member, player)); //FIXME
				if(member.is_dv)
					DragonValley.getInstance().recheckBuff(member);
			}

		// Если партия уже в СС, то вновь прибывшем посылаем пакет открытия окна СС
		if(isInCommandChannel())
			pplayer.add(Msg.ExMPCCOpen);

		player.sendPackets(pplayer);
		pplayer = null;
		pmember_proto = null;

		recalculatePartyData();

		if(isInReflection() && getReflection() instanceof DimensionalRift)
			((DimensionalRift) getReflection()).partyMemberInvited();

		if(player.getPartyRoom() > 0)
		{
			PartyRoom room = PartyRoomManager.getInstance().getRooms().get(player.getPartyRoom());
			if(room != null)
				room.updateInfo();
		}
		if(player.is_dv)
			DragonValley.getInstance().recheckBuff(player);

		if(getEventMaster() != null)
			getEventMaster().addPartyMember(this, player);
	}

	/**
	 * Удаляет все связи
	 */
	public void dissolveParty()
	{
		Reflection reflection = getReflection();
		if(reflection != null)
			reflection.dissolveParty(this);
		if(getEventMaster() != null)
			getEventMaster().dissolveParty(this);

		for(L2Player p : _members)
			{
				p.setParty(null);
				if(p.is_dv)
					DragonValley.getInstance().recheckBuff(p);
			}

		synchronized (_members)
		{
			_members.clear();
		}

		setDimensionalRift(null);
		_commandChannel = null;
		posTaskThread.cancel(false);
	}

	/**
	 * removes player from party
	 * @param player L2Player to remove
	 */
	private void removePartyMember(L2Player player)
	{
		synchronized (_members)
		{
			_members.remove(player);
			posTask.remove(player);
		}

		recalculatePartyData();

		Collection<L2GameServerPacket> pplayer = new GArray<L2GameServerPacket>();

		// Отсылаемы вышедшему пакет закрытия СС
		if(isInCommandChannel())
			pplayer.add(Msg.ExMPCCClose);

		pplayer.add(Msg.YOU_HAVE_WITHDRAWN_FROM_THE_PARTY);
		pplayer.add(Msg.PartySmallWindowDeleteAll);
		player.setParty(null);

		L2Summon player_pet;
		Collection<L2GameServerPacket> pmember_proto = new GArray<L2GameServerPacket>();
		if((player_pet = player.getPet()) != null)
			pmember_proto.add(new ExPartyPetWindowDelete(player_pet));
		pmember_proto.add(new PartySmallWindowDelete(player));
		pmember_proto.add(new SystemMessage(SystemMessage.S1_HAS_LEFT_THE_PARTY).addString(player.getName()));

		// TODO: Нужно ли нам это?
		synchronized (_members)
		{
			Collection<L2GameServerPacket> pmember;
			for(L2Player member : _members)
				if(member != null)
				{
					pmember = new GArray<L2GameServerPacket>();
					pmember.addAll(pmember_proto);
					pmember.addAll(RelationChanged.update(member, player, member));
					member.sendPackets(pmember);
					pmember = null;
					pplayer.addAll(RelationChanged.update(player, member, player));
					if(member.is_dv)
						DragonValley.getInstance().recheckBuff(member);
				}
		}

		player.sendPackets(pplayer);
		pplayer = null;

		Reflection reflection = getReflection();
		
		if(reflection != null)
			reflection.oustPartyMember(this, player);

		if(getEventMaster() != null)
			getEventMaster().removePartyMember(this, player);

		if(reflection instanceof DarknessFestival)
			((DarknessFestival) reflection).partyMemberExited();
		else if(isInReflection() && getReflection() instanceof DimensionalRift)
			((DimensionalRift) getReflection()).partyMemberExited(player);
		if(reflection != null && player.getReflection() == reflection && reflection.getReturnLoc() != null)
			player.teleToLocation(reflection.getReturnLoc(), 0);

		if(player.getDuel() != null)
			player.getDuel().onRemoveFromParty(player);

		L2Player leader = getPartyLeader();

		if(_members.size() == 1 || leader == null)
		{
			if(leader != null && leader.getDuel() != null)
				leader.getDuel().onRemoveFromParty(leader);

			// Если в партии остался 1 человек, то удаляем ее из СС
			if(isInCommandChannel())
				_commandChannel.removeParty(this);
			else if(reflection != null)
			{
				//lastMember.teleToLocation(getReflection().getReturnLoc(), 0);
				//getReflection().stopCollapseTimer();
				//getReflection().collapse();
				if(reflection.getInstancedZone() != null)
				{
					if(reflection.getParty() == this) // TODO: убрать затычку
						reflection.startCollapseTimer(60000);
					if(leader != null && leader.getReflection().getId() == reflection.getId())
						leader.broadcastPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(1));
				}
				setReflection(null);
			}

			if(leader != null)
				leader.setParty(null);

			dissolveParty();
		}
		else if(isInCommandChannel() && _commandChannel.getChannelLeader() == player)
			_commandChannel.setChannelLeader(leader);

		if(player.getPartyRoom() > 0)
		{
			PartyRoom room = PartyRoomManager.getInstance().getRooms().get(player.getPartyRoom());
			if(room != null)
				room.updateInfo();
		}
		if(player.is_dv)
			DragonValley.getInstance().recheckBuff(player);
	}

	/**
	 * Change party leader (used for string arguments)
	 * @param name имя нового лидера парти
	 */
	public void changePartyLeader(String name)
	{
		L2Player new_leader = getPlayerByName(name);

		L2Player current_leader = getPartyLeader();

		if(new_leader == null || current_leader == null)
			return;

		if(current_leader.equals(new_leader))
		{
			current_leader.sendPacket(Msg.YOU_CANNOT_TRANSFER_RIGHTS_TO_YOURSELF);
			return;
		}

		synchronized (_members)
		{
			if(!_members.contains(new_leader))
			{
				current_leader.sendPacket(Msg.YOU_CAN_TRANSFER_RIGHTS_ONLY_TO_ANOTHER_PARTY_MEMBER);
				return;
			}
			// Меняем местами нового и текущего лидера
			int idx = _members.indexOf(new_leader);
			_members.set(0, new_leader);
			_members.set(idx, current_leader);
			final Map<Integer, PartyMakerGroup> partyMakerGroupMap = PartyMaker.getInstance().getPartyMakerGroupMap();
			if (partyMakerGroupMap.containsKey(current_leader.getObjectId())){
				final PartyMakerGroup group = PartyMaker.getInstance().getPartyMakerGroupMap().get(current_leader.getObjectId());
				PartyMaker.getInstance().getPartyMakerGroupMap()
						.put(new_leader.getObjectId(), new PartyMakerGroup(group.getMinLevel(), group.getMaxLevel(), new_leader, group.getDescription(), group.getInstance()));
				PartyMaker.getInstance().myGroup(new_leader);
				PartyMaker.getInstance().getPartyMakerGroupMap().remove(current_leader.getObjectId());
			}
		}
		updateLeaderInfo();

		if(isInCommandChannel() && _commandChannel.getChannelLeader() == current_leader)
			_commandChannel.setChannelLeader(new_leader);
	}

	public void updateLeaderInfo()
	{
		L2Player p_member = getPartyLeader();
		if(p_member == null)
			return;
		SystemMessage msg = new SystemMessage(SystemMessage.S1_HAS_BECOME_A_PARTY_LEADER).addString(p_member.getName());
		for(L2Player member : _members)
			// индивидуальные пакеты - удаления и инициализация пати
			if(member != null)
				member.sendPacket(Msg.PartySmallWindowDeleteAll, // Удаляем все окошки
				new PartySmallWindowAll(this, member), // Показываем окошки
				msg); // Сообщаем о смене лидера
		for(L2Player member : _members)
			// броадкасты состояний
			if(member != null)
			{
				broadcastToPartyMembers(member, new PartySpelled(member, true)); // Показываем иконки
				if(member.getPet() != null)
					broadcastToPartyMembers(new ExPartyPetWindowAdd(member.getPet())); // Показываем окошки петов
				// broadcastToPartyMembers(member, new PartyMemberPosition(member)); // Обновляем позицию на карте
			}
		posTask.lastpositions.clear();
	}

	/**
	 * finds a player in the party by name
	 * @param name имя для поиска
	 * @return найденый L2Player или null если не найдено
	 */
	public L2Player getPlayerByName(String name)
	{
		for(L2Player member : _members)
			if(member != null && name.equalsIgnoreCase(member.getName()))
				return member;
		return null;
	}

	/**
	 * Oust player from party
	 * @param player L2Player которого выгоняют
	 */
	public void oustPartyMember(L2Player player)
	{
		oustPartyMember(player, true);
	}

	public void oustPartyMember(L2Player player, boolean isOly)
	{
		synchronized (_members)
		{
			if(player == null || !_members.contains(player))
				return;
		}

		boolean leader = isLeader(player);

		if(isOly)
		{
			// Убираем чара с реги на оли...
			if(Olympiad.isRegisteredTeam(player))
				Olympiad.removeRegistration(player.getObjectId());
			if(player.isInOlympiadMode() || player.getOlympiadGame() != null)
				Olympiad.logoutPlayer(player);
		}
		removePartyMember(player);

		if(leader && _members.size() > 1)
			updateLeaderInfo();
	}

	/**
	 * Oust player from party Overloaded method that takes player's name as
	 * parameter
	 *
	 * @param name имя игрока для изгнания
	 */
	public void oustPartyMember(String name)
	{
		oustPartyMember(getPlayerByName(name));
	}

	/**
	 * distribute item(s) to party members
	 * @param player
	 * @param item
	 */
	public void distributeItem(L2Player player, L2ItemInstance item)
	{
		distributeItem(player, item, null);
	}

	public void distributeItem(L2Player player, L2ItemInstance item, L2NpcInstance fromNpc)
	{
		if(Util.contains(ConfigValue.ListItemToPartyDistribute, item.getItemId()))
		{
			distributeAdena(item, fromNpc, player);
			return;
		}
		L2Player target = player;

		List<L2Player> ret = null;
		switch(_itemDistribution)
		{
			case ITEM_RANDOM:
			case ITEM_RANDOM_SPOIL:
				target = getRandomMemberInRange(player, item, ConfigValue.AltPartyDistributionRange, fromNpc);
				break;
			case ITEM_ORDER:
			case ITEM_ORDER_SPOIL:
				target = getNextLooterInRange(player, item, ConfigValue.AltPartyDistributionRange, fromNpc);
				/*synchronized (_members)
				{
					ret = new CopyOnWriteArrayList<L2Player>(_members);
					while(target == null && !ret.isEmpty())
					{
						int looter = _itemOrder;
						_itemOrder++;
						if(_itemOrder > ret.size() - 1)
							_itemOrder = 0;

						L2Player looterPlayer = looter < ret.size() ? ret.get(looter) : null;

						if(looterPlayer != null)
						{
							if(!looterPlayer.isDead() && looterPlayer.isInRangeZ(player, ConfigValue.AltPartyDistributionRange) && ItemFunctions.canAddItem(looterPlayer, item))
							//if(ret != null && ret.getReflectionId() == player.getReflectionId() && ret.isInRange(for_dist, range) && !ret.isDead())
								target = looterPlayer;
							else
								ret.remove(looterPlayer);
						}
					}
				}*/
				break;
			case ITEM_LOOTER:
			default:
				target = player;
				break;
		}

		if(target == null)
		{
			item.dropToTheGround(player, fromNpc);
			return;
		}

		if(!target.getInventory().validateWeight(item))
		{
			target.sendPacket(Msg.ActionFail, Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			item.dropToTheGround(target, fromNpc);
			return;
		}

		if(!target.getInventory().validateCapacity(item))
		{
			target.sendPacket(Msg.ActionFail, Msg.YOUR_INVENTORY_IS_FULL);
			item.dropToTheGround(player, fromNpc);
			return;
		}

		if(!item.pickupMe(target))
			return;

		target.sendPacket(SystemMessage.obtainItems(item));
		broadcastToPartyMembers(target, SystemMessage.obtainItemsBy(item, target.getName()));

		L2ItemInstance item2 = target.getInventory().addItem(item);
		Log.LogItem(target, fromNpc, Log.GetItemInPaty, item2);

		target.sendChanges();
	}

	/**
	 * distribute adena to party members
	 * @param item инстанс адены для распределения
	 */
	public void distributeAdena(L2ItemInstance item, L2Player player)
	{
		distributeAdena(item, null, player);
	}

	public void distributeAdena(L2ItemInstance item, L2NpcInstance fromNpc, L2Player player)
	{
		if(player == null)
			return;
		L2Character for_dist = fromNpc != null ? fromNpc : player;

		GArray<L2Player> membersInRange = new GArray<L2Player>();

		if(item.getCount() < _members.size())
			membersInRange.add(player);
		else
		{
			for(L2Player member : _members)
				if(member != null)
					if(member.equals(player) || for_dist.isInRange(member, ConfigValue.AltPartyDistributionRange) && !member.isDead())
						membersInRange.add(member);
		}

		if(membersInRange.isEmpty())
			membersInRange.add(player);

		long totalAdena = item.getCount();
		long amount = totalAdena / membersInRange.size();
		long ost = totalAdena % membersInRange.size();

		for(L2Player member : membersInRange)
		{
			L2ItemInstance newAdena = ItemTemplates.getInstance().createItem(item.getItemId());
			newAdena.setCount(member.equals(player) ? amount + ost : amount);
			member.sendPacket(SystemMessage.obtainItems(newAdena));

			L2ItemInstance item2 = member.getInventory().addItem(newAdena);
			if(fromNpc == null)
				Log.LogItem(member, Log.GetItemInPaty, item2);
			else
				Log.LogItem(member, fromNpc, Log.GetItemInPaty, item2);
		}
	}

	public void distributeXpAndSp(double xpReward, double spReward, GArray<L2Player> rewardedMembers, L2Character lastAttacker, L2MonsterInstance monster)
	{
		recalculatePartyData();

		GArray<L2Player> mtr = new GArray<L2Player>();
		int PartyLevel = lastAttacker.getLevel();
		double partyLvlSum = 0;

		// считаем минимальный/максимальный уровень
		for(L2Player member : rewardedMembers)
		{
			if(!lastAttacker.isInRange(member, ConfigValue.AltPartyDistributionRange))
				continue;
			PartyLevel = Math.max(PartyLevel, member.getLevel());
		}

		// составляем список игроков, удовлетворяющих требованиям
		for(L2Player member : rewardedMembers)
		{
			if(!lastAttacker.isInRange(member, ConfigValue.AltPartyDistributionRange))
				continue;
			if(member.getLevel() <= PartyLevel - 15)
				continue;
			partyLvlSum += member.getLevel();
			mtr.add(member);
		}

		if(mtr.size() == 0)
			return;

		// бонус за пати
		double bonus = ConfigValue.AltPartyBonus[mtr.size() - 1];

		// количество эксп и сп для раздачи на всех
		double XP = xpReward * bonus;
		double SP = spReward * bonus;

		for(L2Player member : mtr)
		{
			double lvlPenalty = Experience.penaltyModifier(monster.calculateLevelDiffForDrop(member.getLevel(), false), 9);
			int lvlDiff = PartyLevel - member.getLevel();
			if(lvlDiff >= ConfigValue.PartyPenaltyMinDiff && lvlDiff <= ConfigValue.PartyPenaltyMaxDiff)
				lvlPenalty *= 0.3;
			// отдаем его часть с учетом пенальти
			double memberXp = XP * lvlPenalty * member.getLevel() / partyLvlSum;
			double memberSp = SP * lvlPenalty * member.getLevel() / partyLvlSum;

			// больше чем соло не дадут
			memberXp = Math.min(memberXp, xpReward);
			memberSp = Math.min(memberSp, spReward);

			// Начисление душ камаэлянам
			double neededExp = member.calcStat(Stats.SOULS_CONSUME_EXP, 0, monster, null);
			if(neededExp > 0 && memberXp > neededExp)
			{
				monster.broadcastPacket(new SpawnEmitter(monster, member));
				ThreadPoolManager.getInstance().schedule(new SoulConsumeTask(member), 1000);
			}

			double[] xpsp = member.applyVitality(monster, memberXp, memberSp, memberXp / xpReward);

			if(ConfigValue.RangEnable)
			{
				long point = (long)((xpsp[0]/100)*ConfigValue.RangPercentAddPointMob[member.getRangId()]);
				if(point < 1)
					point = 1;
				member.addRangPoint(point);
				member.sendMessage("Получено "+point+" Очков Воина. Всего "+member.getRangPoint()+" Очков Воина.");
			}
			if(member.getLevel() > monster.getLevel()- ConfigValue.ExpSpPenaltyDiff)
				member.addExpAndSp((long) xpsp[0], (long) xpsp[1], false, true, (long) xpsp[2], (long) xpsp[3], monster);
		}

		recalculatePartyData();
	}

	public void recalculatePartyData()
	{
		_partyLvl = 0;
		float rateExp = 0;
		float rateSp = 0;
		float rateDrop = 0;
		float rateAdena = 0;
		float rateSpoil = 0;
		float minRateExp = Float.MAX_VALUE;
		float minRateSp = Float.MAX_VALUE;
		float minRateDrop = Float.MAX_VALUE;
		float minRateAdena = Float.MAX_VALUE;
		float minRateSpoil = Float.MAX_VALUE;
		byte count = 0;
		for(L2Player member : _members)
			if(member != null)
			{
				int level = member.getLevel();
				_partyLvl = Math.max(_partyLvl, level);
				count++;

				rateExp += member.getBonus().RATE_XP*member.getAltBonus();
				rateSp += member.getBonus().RATE_SP*member.getAltBonus();
				rateDrop += member.getBonus().RATE_DROP_ITEMS*member.getAltBonus();
				rateAdena += member.getBonus().RATE_DROP_ADENA*member.getAltBonus();
				rateSpoil += member.getBonus().RATE_DROP_SPOIL*member.getAltBonus();

				minRateExp = Math.min(minRateExp, member.getBonus().RATE_XP*member.getAltBonus());
				minRateSp = Math.min(minRateSp, member.getBonus().RATE_SP*member.getAltBonus());
				minRateDrop = Math.min(minRateDrop, member.getBonus().RATE_DROP_ITEMS*member.getAltBonus());
				minRateAdena = Math.min(minRateAdena, member.getBonus().RATE_DROP_ADENA*member.getAltBonus());
				minRateSpoil = Math.min(minRateSpoil, member.getBonus().RATE_DROP_SPOIL*member.getAltBonus());
			}
		_rateExp = ConfigValue.RatePartyMin ? minRateExp : rateExp / count;
		_rateSp = ConfigValue.RatePartyMin ? minRateSp : rateSp / count;
		_rateDrop = ConfigValue.RatePartyMin ? minRateDrop : rateDrop / count;
		_rateAdena = ConfigValue.RatePartyMin ? minRateAdena : rateAdena / count;
		_rateSpoil = ConfigValue.RatePartyMin ? minRateSpoil : rateSpoil / count;
	}

	public int getLevel()
	{
		return _partyLvl;
	}

	public int getLootDistribution()
	{
		return _itemDistribution;
	}

	public boolean isDistributeSpoilLoot()
	{
		boolean rv = false;

		if(_itemDistribution == ITEM_RANDOM_SPOIL || _itemDistribution == ITEM_ORDER_SPOIL)
			rv = true;

		return rv;
	}

	public boolean isInDimensionalRift()
	{
		return _dr > 0 && getDimensionalRift() != null;
	}

	public void setDimensionalRift(DimensionalRift dr)
	{
		_dr = dr == null ? 0 : dr.getId();
	}

	public DimensionalRift getDimensionalRift()
	{
		return _dr == 0 ? null : (DimensionalRift) ReflectionTable.getInstance().get(_dr);
	}

	public boolean isInReflection()
	{
		if(_reflection > 0)
			return true;
		if(_commandChannel != null)
			return _commandChannel.isInReflection();
		return false;
	}

	public void setReflection(Reflection reflection)
	{
		_reflection = reflection == null ? 0 : reflection.getId();
	}

	public Reflection getReflection()
	{
		if(_reflection > 0)
			return ReflectionTable.getInstance().get(_reflection);
		if(_commandChannel != null)
			return _commandChannel.getReflection();
		return null;
	}

	public boolean isInCommandChannel()
	{
		return _commandChannel != null;
	}

	public L2CommandChannel getCommandChannel()
	{
		return _commandChannel;
	}

	public void setCommandChannel(L2CommandChannel channel)
	{
		_commandChannel = channel;
	}

	/**
	 * Телепорт всей пати в одну точку (x,y,z)
	 */
	public void Teleport(int x, int y, int z)
	{
		TeleportParty(getPartyMembers(), new Location(x, y, z));
	}

	/**
	 * Телепорт всей пати в одну точку dest
	 */
	public void Teleport(Location dest)
	{
		TeleportParty(getPartyMembers(), dest);
	}

	/**
	 * Телепорт всей пати на территорию, игроки расставляются рандомно по территории
	 */
	public void Teleport(L2Territory territory)
	{
		RandomTeleportParty(getPartyMembers(), territory);
	}

	/**
	 * Телепорт всей пати на территорию, лидер попадает в точку dest, а все остальные относительно лидера
	 */
	public void Teleport(L2Territory territory, Location dest)
	{
		TeleportParty(getPartyMembers(), territory, dest);
	}

	public static void TeleportParty(List<L2Player> members, Location dest)
	{
		for(L2Player _member : members)
		{
			if(_member == null)
				continue;
			_member.teleToLocation(dest);
		}
	}

	public static void TeleportParty(List<L2Player> members, L2Territory territory, Location dest)
	{
		if(!territory.isInside(dest.x, dest.y))
		{
			Log.add("TeleportParty: dest is out of territory", "errors");
			Thread.dumpStack();
			return;
		}
		int base_x = members.get(0).getX();
		int base_y = members.get(0).getY();

		for(L2Player _member : members)
		{
			if(_member == null)
				continue;
			int diff_x = _member.getX() - base_x;
			int diff_y = _member.getY() - base_y;
			Location loc = new Location(dest.x + diff_x, dest.y + diff_y, dest.z);
			while(!territory.isInside(loc.x, loc.y))
			{
				diff_x = loc.x - dest.x;
				diff_y = loc.y - dest.y;
				if(diff_x != 0)
					loc.x -= diff_x / Math.abs(diff_x);
				if(diff_y != 0)
					loc.y -= diff_y / Math.abs(diff_y);
			}
			_member.teleToLocation(loc);
		}
	}

	public static void RandomTeleportParty(List<L2Player> members, L2Territory territory)
	{
		for(L2Player _member : members)
		{
			int[] _loc = territory.getRandomPoint();
			if(_member == null || _loc == null)
				continue;
			_member.teleToLocation(_loc[0], _loc[1], _loc[2]);
		}
	}


    public void requestLootModification(int mode) {
        newLootDistr = mode;
        votingChangeLoot = true;
        //формируем список из тех, от кого будем ждать ответ, и заодно рассылаем всем запрос.
        for (L2Player player : getPartyMembers()) {
            if (player != getPartyLeader()) {
                lootResponsers.add(player);
                player.sendPacket(new ExAskModifyPartyLooting(getPartyLeader().getName(), newLootDistr));
            }
        }
        getPartyLeader().sendPacket(new SystemMessage(3135).addSystemString(LOOT_SYSSTRINGS[newLootDistr]));
        if (voteTask != null)
            voteTask.cancel(true);
        voteTask = ThreadPoolManager.getInstance().schedule(new VoteLootChangeTask(), 10000);

    }

    private void setItemDistribution(boolean forse) {
        if (_itemDistribution == newLootDistr)
            return;
        if (forse) {//кто то дал отрицательный ответ
            if (voteTask != null)
                voteTask.cancel(true);
            voteTask = null;
            newLootDistr = _itemDistribution;
            broadcastToPartyMembers(new ExSetPartyLooting(_itemDistribution, 0));
            broadcastToPartyMembers(new SystemMessage(3137));
            return;
        } else if (lootResponsers.size() > 0) {
            newLootDistr = _itemDistribution;
            broadcastToPartyMembers(new ExSetPartyLooting(_itemDistribution, 0));
            broadcastToPartyMembers(new SystemMessage(3137));
            return;
        }
        lootResponsers.clear();
        _itemDistribution = newLootDistr;
        getPartyLeader().sendPacket(new ExSetPartyLooting(_itemDistribution, 1));
        broadcastToPartyMembers(new SystemMessage(3138).addSystemString(LOOT_SYSSTRINGS[_itemDistribution]));
	
		for(L2Player member : _members)
			if(member != null)
				member.sendPacket(Msg.PartySmallWindowDeleteAll, new PartySmallWindowAll(this, member));
		for(L2Player member : _members)
			if(member != null)
			{
				broadcastToPartyMembers(member, new PartySpelled(member, true)); // Показываем иконки
				if(member.getPet() != null)
					broadcastToPartyMembers(new ExPartyPetWindowAdd(member.getPet())); // Показываем окошки петов
			}
    }

    public void answerLootModification(L2Player member, int answer) {
        if (lootResponsers.contains(member) && answer == 1) {
            lootResponsers.remove(member);
            if (lootResponsers.size() == 0)
                setItemDistribution(false);
        } else
            setItemDistribution(true);
    }

    class VoteLootChangeTask extends l2open.common.RunnableImpl {
        @Override
        public void runImpl() {
            L2Party.this.setItemDistribution(false);
        }
    }

    private class UpdatePositionTask extends l2open.common.RunnableImpl
	{
		private final WeakReference<L2Party> party_ref;
		private final HashMap<Integer, int[]> lastpositions = new HashMap<Integer, int[]>();

		public UpdatePositionTask(L2Party party)
		{
			party_ref = new WeakReference<L2Party>(party);
		}

		public void remove(L2Player player)
		{
			synchronized (lastpositions)
			{
				lastpositions.remove(new Integer(player.getObjectId()));
			}
		}

		public void runImpl()
		{
			L2Party party = party_ref.get();
			if(party == null || party.getMemberCount() < 2)
			{
				synchronized (lastpositions)
				{
					lastpositions.clear();
				}
				party_ref.clear();
				dissolveParty();
				return;
			}
			try
			{
				List<L2Player> full_updated = new ArrayList<L2Player>();
				List<L2Player> members = party.getPartyMembers();
				PartyMemberPosition just_updated = new PartyMemberPosition();
				int[] lastpos;
				for(L2Player member : members)
				{
					if(member == null)
						continue;
					synchronized (lastpositions)
					{
						lastpos = lastpositions.get(new Integer(member.getObjectId()));
						if(lastpos == null)
						{
							just_updated.add(member);
							full_updated.add(member);
							lastpositions.put(member.getObjectId(), new int[] { member.getX(), member.getY(), member.getZ() });
						}
						else if(member.getDistance(lastpos[0], lastpos[1], lastpos[2]) > 256) //TODO подкорректировать
						{
							just_updated.add(member);
							lastpos[0] = member.getX();
							lastpos[1] = member.getY();
							lastpos[2] = member.getZ();
						}
					}
				}

				// посылаем изменения позиций старым членам пати
				if(just_updated.size() > 0)
					for(L2Player member : members)
						if(!full_updated.contains(member))
							member.sendPacket(just_updated);

				// посылаем полный список позиций новым членам пати
				if(full_updated.size() > 0)
				{
					just_updated = new PartyMemberPosition().add(members);
					for(L2Player member : full_updated)
						member.sendPacket(just_updated);
					full_updated.clear();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				posTaskThread = ThreadPoolManager.getInstance().schedule(this, 1000);
			}
		}
	}

	public void addFractionPoint(L2Player for_dist)
	{

	}

	private EventMaster _event_master;
	public EventMaster getEventMaster()
	{
		return _event_master;
	}
	public void setEventMaster(EventMaster em)
	{
		_event_master = em;
	}
}