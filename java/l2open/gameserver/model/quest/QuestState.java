package l2open.gameserver.model.quest;

import l2open.config.*;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.olympiad.CompType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.SpawnTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Log;
import l2open.util.Rnd;

import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class QuestState
{
	protected static Logger _log = Logger.getLogger(Quest.class.getName());

	private L2Player ownerStoreId = null;

	/** Quest associated to the QuestState */
	private Quest _quest;

	/** State of the quest */
	private int _state;

	/** List of couples (variable for quest,value of the variable for quest) */
	private ConcurrentHashMap<String, String> _vars;

	/**
	 * Constructor<?> of the QuestState : save the quest in the list of quests of the player.<BR/><BR/>
	 *
	 * <U><I>Actions :</U></I><BR/>
	 * <LI>Save informations in the object QuestState created (Quest, Player, Completion, State)</LI>
	 * <LI>Add the QuestState in the player's list of quests by using setQuestState()</LI>
	 * <LI>Add drops gotten by the quest</LI>
	 * <BR/>
	 * @param quest : quest associated with the QuestState
	 * @param player : L2Player pointing out the player
	 * @param state : state of the quest
	 */
	public QuestState(Quest quest, L2Player player, int state)
	{
		_quest = quest;
		ownerStoreId = player;

		// Save the state of the quest for the player in the player's list of quest onwed
		player.setQuestState(this);

		// set the state of the quest
		_state = state;
		quest.notifyPlayerEnter(this);
	}

	/**
	 * Add XP and SP as quest reward
	 * <br><br>
	 * Метод учитывает рейты!
	 */
	public void addExpAndSp(long exp, long sp)
	{
		addExpAndSp(exp, sp, false);
	}

	/**
	 * Add XP and SP as quest reward
	 * <br><br>
	 * Метод учитывает рейты!
	 * 3-ий параметр true/false показывает является ли квест на профессию 
	 * и рейты учитываются в завимисомти от параметра RateQuestsRewardOccupationChange
	 */
	public void addExpAndSp(long exp, long sp, boolean prof)
	{
		L2Player player = getPlayer();
		if(player == null)
			return;
		if(!prof || prof && ConfigValue.RateQuestsRewardOccupationChange)
		{
			float inRate = ConfigSystem.getQuestRewardRates(_quest);
			if(inRate == 0)
				player.addExpAndSp((long) (exp * getRateQuestsRewardExpSp()), (long) (sp * getRateQuestsRewardExpSp()), false, false);
			else if(inRate > 0)
				player.addExpAndSp((long) (exp * inRate), (long) (sp * inRate), false, false);
		}
		else
			player.addExpAndSp(exp, sp, false, false);
	}

	/**
	 * Add player to get notification of characters death
	 * @param playable : L2Playable of the character to get notification of death
	 */
	public void addNotifyOfDeath(L2Playable playable)
	{
		if(playable != null)
			playable.addNotifyQuestOfDeath(this);
	}

	public void addNotifyOfPlayerKill()
	{
		L2Player player = getPlayer();
		if(player != null)
			player.addNotifyOfPlayerKill(this);
	}

	public void removeNotifyOfPlayerKill()
	{
		L2Player player = getPlayer();
		if(player != null)
			player.removeNotifyOfPlayerKill(this);
	}

	public void endOlympiad(GArray<L2Player> winTeam, GArray<L2Player> lossTeam, boolean haveWin, CompType type)
	{
		getQuest().notifyOlympiadGame(this, winTeam, lossTeam, haveWin, type);
	}

	public void addRadar(int x, int y, int z)
	{
		L2Player player = getPlayer();
		if(player != null)
			player.radar.addMarker(x, y, z);
	}

	public void clearRadar()
	{
		L2Player player = getPlayer();
		if(player != null)
			player.radar.removeAllMarkers();
	}

	/**
	 * Используется для однодневных квестов
	 */
	public void exitCurrentQuest(Quest quest)
	{
		L2Player player = getPlayer();
		exitCurrentQuest(true);
		quest.newQuestState(player, Quest.DELAYED);
		QuestState qs = player.getQuestState(quest.getName());
		qs.setRestartTime();
	}
	
	
	/**
	 * Destroy element used by quest when quest is exited
	 * @param repeatable
	 * @return QuestState
	 */
	public QuestState exitCurrentQuest(boolean repeatable)
	{
		L2Player player = getPlayer();
		if(player == null)
			return this;

		// Clean drops
		if(_quest.getItems() != null)
			// Go through values of class variable "drops" pointing out mobs that drop for quest
			for(Integer itemId : _quest.getItems())
			{
				// Get [item from] / [presence of the item in] the inventory of the player
				L2ItemInstance item = player.getInventory().getItemByItemId(itemId);
				if(item == null || itemId == 57)
					continue;
				long count = item.getCount();
				// If player has the item in inventory, destroy it (if not gold)
				player.getInventory().destroyItemByItemId(itemId, count, true);
				player.getWarehouse().destroyItem(itemId, count);
			}

		// If quest is repeatable, delete quest from list of quest of the player and from database (quest CAN be created again => repeatable)
		if(repeatable)
		{
			player.delQuestState(_quest.getName());
			Quest.deleteQuestInDb(this);
			_vars = null;
		}
		else
		{ // Otherwise, delete variables for quest and update database (quest CANNOT be created again => not repeatable)
			if(_vars != null && !_vars.isEmpty())
				for(String var : _vars.keySet())
					if(var != null)
						unset(var);
			if(ConfigValue.AttainmentIn_QuestOnlyDisposable && player.getAttainment() != null)
				player.getAttainment().questComplet(this);
			setState(Quest.COMPLETED);
			Quest.updateQuestInDb(this); // FIXME: оно вроде не нужно?
		}
		player.sendPacket(new QuestList(player));
		return this;
	}

	public void abortQuest()
	{
		_quest.onAbort(this);
		exitCurrentQuest(true);
	}

	/**
	 * <font color=red>Не использовать для получения кондов!</font><br><br>
	 * 
	 * Return the value of the variable of quest represented by "var"
	 * @param var : name of the variable of quest
	 * @return Object
	 */
	public String get(String var)
	{
		if(_vars == null)
			return null;
		return _vars.get(var);
	}

	public ConcurrentHashMap<String, String> getVars()
	{
		ConcurrentHashMap<String, String> result = new ConcurrentHashMap<String, String>();
		if(_vars != null)
			result.putAll(_vars);
		return result;
	}

	/**
	 * Возвращает переменную в виде целого числа. Для кондов вызывает getCond.
	 * 
	 * @param var : String designating the variable for the quest
	 * @return int
	 */
	public int getInt(String var)
	{
		if(var.equalsIgnoreCase("cond"))
			return getCond();

		return getRawInt(var);
	}

	/**
	 * Возвращает переменную в виде целого числа.
	 * 
	 * @param var : String designating the variable for the quest
	 * @return int
	 */
	public int getRawInt(String var)
	{
		int varint = 0;
		try
		{
			String val = get(var);
			if(val == null)
				return 0;
			varint = Integer.parseInt(val);
		}
		catch(Exception e)
		{
			_log.finer(getPlayer().getName() + ": variable " + var + " isn't an integer: " + varint + e);
			e.printStackTrace();
		}
		return varint;
	}

	/**
	 * Return item number which is equipped in selected slot
	 * @return int
	 */
	public int getItemEquipped(int loc)
	{
		return getPlayer().getInventory().getPaperdollItemId(loc, false, false);
	}

	/**
	 * @return L2Player
	 */
	public L2Player getPlayer()
	{
		return ownerStoreId;
	}

	/**
	 * Return the quest
	 * @return Quest
	 */
	public Quest getQuest()
	{
		return _quest;
	}

	public boolean checkQuestItemsCount(int... itemIds)
	{
		L2Player player = getPlayer();
		if(player == null)
			return false;
		for(int itemId : itemIds)
			if(player.getInventory().getCountOf(itemId) <= 0)
				return false;
		return true;
	}

	public long getSumQuestItemsCount(int... itemIds)
	{
		L2Player player = getPlayer();
		if(player == null)
			return 0;
		long count = 0;
		for(int itemId : itemIds)
			count += player.getInventory().getCountOf(itemId);
		return count;
	}

	/**
	 * Return the quantity of one sort of item hold by the player
	 * @param itemId : ID of the item wanted to be count
	 * @return int
	 */
	public long getQuestItemsCount(int itemId)
	{
		L2Player player = getPlayer();
		return player == null ? 0 : player.getInventory().getCountOf(itemId);
	}

	public long getQuestItemsCount(int... itemsIds)
	{
		long result = 0;
		for(int id : itemsIds)
			result += getQuestItemsCount(id);
		return result;
	}

	/**
	 * Return the QuestTimer object with the specified name
	 * @return QuestTimer<BR> Return null if name does not exist
	 */
	public final QuestTimer getQuestTimer(String name)
	{
		return Quest.getQuestTimer(getQuest(), name, getPlayer());

	}

	public int getState()
	{
		return _state == Quest.DELAYED ? Quest.CREATED : _state;
	}

	public String getStateName()
	{
		return Quest.getStateName(_state);
	}
	
	public Object setStateAndNotSave(int state)
	{
		// set new state if it is not already in that state
		if (_state != state)
		{
			_state = state;
			getPlayer().sendPacket(new QuestList(getPlayer()));
		}
		return state;
	}

	/**
	 * Добавить предмет игроку
	 * By default if item is adena rates 'll be applyed, else no
	 * @param itemId
	 * @param count
	 */
	public L2ItemInstance giveItems(int itemId, long count)
	{
		if(itemId == 57)
			return giveItems(itemId, count, 0, true);
		else
			return giveItems(itemId, count, 0, false);
	}

	/**
	 * Добавить предмет игроку
	 * @param itemId
	 * @param count
	 * @param rate - учет квестовых рейтов
	 */
	public L2ItemInstance giveItems(int itemId, long count, boolean rate)
	{
		return giveItems(itemId, count, 0, rate);
	}

	/**
	 * Добавить предмет игроку
	 * @param itemId
	 * @param count
	 * @param enchantlevel
	 * @param rate - учет квестовых рейтов
	 */
	public L2ItemInstance giveItems(int itemId, long count, int enchantlevel, boolean rate)
	{
		L2Player player = getPlayer();
		if(player == null)
			return null;

		if(count <= 0)
			count = 1;

		float inRate = ConfigSystem.getQuestRewardRates(_quest);

		if(rate && inRate == 0)
		{
			if(itemId == 57)
				count = (long) (count * (getRateQuestsRewardAdena() * ConfigValue.RateDropAdenaMultMod + ConfigValue.RateDropAdenaStaticMod));
			else
				count = (long) (count * getRateQuestsRewardDrop());
		}
		else if(inRate > 0)
		{
			if(itemId == 57)
				count = (long) (count * (inRate * ConfigValue.RateDropAdenaMultMod + ConfigValue.RateDropAdenaStaticMod));
			else
				count = (long) (count * inRate);
		}

		if(count < 1)
			return null;

		if(itemId == 57)
			Log.add("Quest|" + getQuest().getQuestIntId() + "|" + count + "|" + player.getName(), "adena");

		// Get template of item
		L2Item template = ItemTemplates.getInstance().getTemplate(itemId);
		if(template == null)
			return null;

		L2ItemInstance ret = null;
		if(template.isStackable())
		{
			L2ItemInstance item = ItemTemplates.getInstance().createItem(itemId);

			// Set quantity of item
			item.setCount(count);

			// Add items to player's inventory
			ret = player.getInventory().addItem(item);

			if(enchantlevel > 0)
				item.setEnchantLevel(enchantlevel);

			Log.LogItem(player, Log.GetQuestItem, item);
		}
		else
			for(int i = 0; i < count; i++)
			{
				L2ItemInstance item = ItemTemplates.getInstance().createItem(itemId);

				// Add items to player's inventory
				ret = player.getInventory().addItem(item);

				if(enchantlevel > 0)
					item.setEnchantLevel(enchantlevel);

				Log.LogItem(player, Log.GetQuestItem, item);
			}

		player.sendPacket(SystemMessage.obtainItems(template.getItemId(), count, 0));
		player.sendStatusUpdate(false, StatusUpdate.CUR_LOAD);
		return ret;
	}

	public void giveItems(int itemId, long count, byte attributeId, int attributeLevel, int[] deffAttr)
	{
		L2Player player = getPlayer();
		if(player == null)
			return;

		if(count <= 0)
			count = 1;

		// Get template of item
		L2Item template = ItemTemplates.getInstance().getTemplate(itemId);
		if(template == null)
			return;

		for(int i = 0; i < count; i++)
		{
			L2ItemInstance item = ItemTemplates.getInstance().createItem(itemId);

			if(attributeId >= 0 && attributeLevel > 0)
				item.setAttributeElement(attributeId, attributeLevel, deffAttr, true);

			// Add items to player's inventory
			player.getInventory().addItem(item);

			Log.LogItem(player, Log.GetQuestItem, item);
		}

		player.sendPacket(SystemMessage.obtainItems(template.getItemId(), count, 0));
		player.sendStatusUpdate(false, StatusUpdate.CUR_LOAD);
	}

	/**
	 * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов.
	 * <br><br>
	 * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
	 * нужно набить определенное количество предметов не осуществляется.
	 * <br><br>
	 * Ни один из передаваемых параметров не должен быть равен 0
	 *
	 * @param count количество при рейтах 1х
	 * @param calcChance шанс при рейтах 1х, в процентах
	 * @return количество вещей для дропа, может быть 0
	 */
	public int rollDrop(int count, double calcChance, boolean prof)
	{
		if(calcChance <= 0 || count <= 0)
			return 0;
		return rollDrop(count, count, calcChance, prof);
	}

	/**
	 * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов.
	 * <br><br>
	 * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
	 * нужно набить определенное количество предметов не осуществляется.
	 * <br><br>
	 * Ни один из передаваемых параметров не должен быть равен 0
	 *
	 * @param min минимальное количество при рейтах 1х
	 * @param max максимальное количество при рейтах 1х
	 * @param calcChance шанс при рейтах 1х, в процентах
	 * @param prof - учитывать дроп по параметру "рейт дропа для квестов на профу"
	 * @return количество вещей для дропа, может быть 0
	 */
	public int rollDrop(int min, int max, double calcChance, boolean prof)
	{
		if(calcChance <= 0 || min <= 0 || max <= 0)
			return 0;
		int dropmult = 1;
		calcChance *= getRateQuestsDrop(prof);
		if(getQuest().getParty() > Quest.PARTY_NONE)
		{
			L2Player player = getPlayer();
			if(player.getParty() != null)
				calcChance *= ConfigValue.AltPartyBonus[player.getParty().getMemberCountInRange(player, ConfigValue.AltPartyDistributionRange) - 1];
		}
		if(calcChance > 100)
		{
			if((int) Math.ceil(calcChance / 100) <= calcChance / 100)
				calcChance = Math.nextUp(calcChance);
			dropmult = (int) Math.ceil(calcChance / 100);
			calcChance = calcChance / dropmult;
		}
		return Rnd.chance(calcChance) ? Rnd.get(min * dropmult, max * dropmult) : 0;
	}

	public float getRateQuestsDrop(boolean prof)
	{
		L2Player player = getPlayer();
		float Bonus = player == null ? 1 : player.getBonus().RATE_QUESTS_DROP*player.getAltBonus();
		float inDropRate = ConfigSystem.getQuestDropRates(_quest);
		return (inDropRate == 0 ? (prof ? ConfigValue.RateQuestsDropProf : ConfigValue.RateQuestsDrop) : inDropRate) * Bonus;
	}

	public float getRateQuestsRewardAdena()
	{
		L2Player player = getPlayer();
		float Bonus = player == null ? 1 : player.getBonus().RATE_QUESTS_REWARD*player.getAltBonus();
		return ConfigValue.RateQuestsRewardAdena * Bonus;
	}

	public float getRateQuestsRewardDrop()
	{
		L2Player player = getPlayer();
		float Bonus = player == null ? 1 : player.getBonus().RATE_QUESTS_REWARD*player.getAltBonus();
		return ConfigValue.RateQuestsRewardDrop * Bonus;
	}

	public float getRateQuestsRewardExpSp()
	{
		L2Player player = getPlayer();
		float Bonus = player == null ? 1 : player.getBonus().RATE_QUESTS_REWARD*player.getAltBonus();
		return ConfigValue.RateQuestsRewardExpSp * Bonus;
	}

	/**
	 * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
	 * проверяет максимум, а так же проигрывает звук получения вещи.
	 * <br><br>
	 * Ни один из передаваемых параметров не должен быть равен 0
	 *
	 * @param itemId id вещи
	 * @param min минимальное количество при рейтах 1х
	 * @param max максимальное количество при рейтах 1х
	 * @param limit максимум таких вещей
	 * @param calcChance
	 * @return true если после выполнения количество достигло лимита
	 */
	public boolean rollAndGive(int itemId, int min, int max, int limit, double calcChance)
	{
		if(calcChance <= 0 || min <= 0 || max <= 0 || limit <= 0 || itemId <= 0)
			return false;
		return rollAndGive(itemId, min, max, limit, calcChance, false);
	}

	/**
	 * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
	 * проверяет максимум, а так же проигрывает звук получения вещи.
	 * <br><br>
	 * Ни один из передаваемых параметров не должен быть равен 0
	 *
	 * @param itemId id вещи
	 * @param min минимальное количество при рейтах 1х
	 * @param max максимальное количество при рейтах 1х
	 * @param limit максимум таких вещей
	 * @param calcChance
	 * @param prof
	 * @return true если после выполнения количество достигло лимита
	 */
	public boolean rollAndGive(int itemId, int min, int max, int limit, double calcChance, boolean prof)
	{
		if(calcChance <= 0 || min <= 0 || max <= 0 || limit <= 0 || itemId <= 0)
			return false;
		long count = rollDrop(min, max, calcChance, prof);
		if(count > 0)
		{
			long alreadyCount = getQuestItemsCount(itemId);
			if(alreadyCount + count > limit)
				count = limit - alreadyCount;
			if(count > 0)
			{
				giveItems(itemId, count, false);
				if(count + alreadyCount < limit)
					playSound(Quest.SOUND_ITEMGET);
				else
				{
					playSound(Quest.SOUND_MIDDLE);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
	 * а так же проигрывает звук получения вещи.
	 * <br><br>
	 * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
	 * нужно набить определенное количество предметов не осуществляется.
	 * <br><br>
	 * Ни один из передаваемых параметров не должен быть равен 0
	 *
	 * @param itemId id вещи
	 * @param min минимальное количество при рейтах 1х
	 * @param max максимальное количество при рейтах 1х
	 * @param calcChance
	 */
	public void rollAndGive(int itemId, int min, int max, double calcChance)
	{
		if(calcChance <= 0 || min <= 0 || max <= 0 || itemId <= 0)
			return;
		rollAndGive(itemId, min, max, calcChance, false);
	}

	/**
	 * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
	 * а так же проигрывает звук получения вещи.
	 * <br><br>
	 * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
	 * нужно набить определенное количество предметов не осуществляется.
	 * <br><br>
	 * Ни один из передаваемых параметров не должен быть равен 0
	 *
	 * @param itemId id вещи
	 * @param min минимальное количество при рейтах 1х
	 * @param max максимальное количество при рейтах 1х
	 * @param prof - учитывать дроп по параметру "рейт дропа для квестов на профу"
	 * @param calcChance
	 */
	public void rollAndGive(int itemId, int min, int max, double calcChance, boolean prof)
	{
		if(calcChance <= 0 || min <= 0 || max <= 0 || itemId <= 0)
			return;
		int count = rollDrop(min, max, calcChance, prof);
		if(count > 0)
		{
			giveItems(itemId, count, false);
			playSound(Quest.SOUND_ITEMGET);
		}
	}

	/**
	 * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
	 * а так же проигрывает звук получения вещи.
	 * <br><br>
	 * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
	 * нужно набить определенное количество предметов не осуществляется.
	 * <br><br>
	 * Ни один из передаваемых параметров не должен быть равен 0
	 *
	 * @param itemId id вещи
	 * @param count количество при рейтах 1х
	 * @param calcChance
	 */
	public boolean rollAndGive(int itemId, int count, double calcChance)
	{
		if(calcChance <= 0 || count <= 0 || itemId <= 0)
			return false;
		return rollAndGive(itemId, count, calcChance, false);
	}

	/**
	 * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
	 * а так же проигрывает звук получения вещи.
	 * <br><br>
	 * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
	 * нужно набить определенное количество предметов не осуществляется.
	 * <br><br>
	 * Ни один из передаваемых параметров не должен быть равен 0
	 *
	 * @param itemId id вещи
	 * @param count количество при рейтах 1х
	 * @param calcChance
	 * @param prof - учитывать дроп по параметру "рейт дропа для квестов на профу"
	 */
	public boolean rollAndGive(int itemId, int count, double calcChance, boolean prof)
	{
		if(calcChance <= 0 || count <= 0 || itemId <= 0)
			return false;
		int countToDrop = rollDrop(count, calcChance, prof);
		if(countToDrop > 0)
		{
			giveItems(itemId, countToDrop, false);
			playSound(Quest.SOUND_ITEMGET);
			return true;
		}
		return false;
	}

	/**
	 * Return true if quest completed, false otherwise
	 * @return boolean
	 */
	public boolean isCompleted()
	{
		return getState() == Quest.COMPLETED;
	}

	/**
	 * Return true if quest started, false otherwise
	 * @return boolean
	 */
	public boolean isStarted()
	{
		return getState() != Quest.CREATED && getState() != Quest.COMPLETED;
	}

	public void killNpcByObjectId(int _objId)
	{
		L2NpcInstance npc = L2ObjectsStorage.getNpc(_objId);
		if(npc != null)
			npc.doDie(null);
		else
			_log.warning("Attemp to kill object that is not npc in quest " + getQuest().getQuestIntId());
	}

	/**
	 * Аналог set с флагом true, но если получает cond проверяет нотацию (для совместимости).
	 */
	public String set(String var, String val)
	{
		if(var.equalsIgnoreCase("cond"))
			return setCond(Integer.parseInt(val));

		return set(var, val, true);
	}

	/**
	 * Аналог set с флагом true, но если получает cond проверяет нотацию (для совместимости).
	 */
	public String set(String var, int intval)
	{
		if(var.equalsIgnoreCase("cond"))
			return setCond(intval);

		return set(var, String.valueOf(intval), true);
	}

	/**
	 * <font color=red>Использовать осторожно! Служебная функция!</font><br><br>
	 * 
	 * Устанавливает переменную и сохраняет в базу, если установлен флаг. Если получен cond обновляет список квестов игрока (только с флагом).
	 * 
	 * @param var : String pointing out the name of the variable for quest
	 * @param val : String pointing out the value of the variable for quest
	 * @param store : Сохраняет в базу и если var это cond обновляет список квестов игрока.
	 * @return String (equal to parameter "val")
	 */
	public String set(String var, String val, boolean store)
	{
		if(_vars == null)
			_vars = new ConcurrentHashMap<String, String>();
		if(val == null)
			val = "";
		_vars.put(var, val);

		if(store)
			Quest.updateQuestVarInDb(this, var, val);

		return val;
	}

	/**
	 * Return state of the quest after its initialization.<BR><BR>
	 * <U><I>Actions :</I></U>
	 * <LI>Remove drops from previous state</LI>
	 * <LI>Set new state of the quest</LI>
	 * <LI>Add drop for new state</LI>
	 * <LI>Update information in database</LI>
	 * <LI>Send packet QuestList to client</LI>
	 * @param state
	 * @return object
	 */
	public Object setState(int state)
	{
		L2Player player = getPlayer();
		if(player == null)
			return null;

		_state = state;

		if((getQuest().getQuestIntId() < 999 || getQuest().getQuestIntId() > 10000) && isStarted())
			player.sendPacket(new ExShowQuestMark(getQuest().getQuestIntId(), getCond()));

		Quest.updateQuestInDb(this);
		player.sendPacket(new QuestList(player));
		return state;
	}

	public void removeRadar(int x, int y, int z)
	{
		L2Player player = getPlayer();
		if(player != null)
			player.radar.removeMarker(x, y, z);
	}

	/**
	 * Send a packet in order to play sound at client terminal
	 * @param sound
	 */
	public void playSound(String sound)
	{
		L2Player player = getPlayer();
		if(player != null)
		{
			if(!ConfigValue.AttainmentIn_QuestOnlyDisposable && player.getAttainment() != null && sound.equals(Quest.SOUND_FINISH))
				player.getAttainment().questComplet(this);
			player.sendPacket(new PlaySound(sound));
		}
	}

	public void playTutorialVoice(String voice)
	{
		L2Player player = getPlayer();
		if(player != null)
			player.sendPacket(new PlaySound(2, voice, 0, 0, player.getLoc()));
	}

	public void onTutorialClientEvent(int number)
	{
		L2Player player = getPlayer();
		if(player != null)
			player.sendPacket(new TutorialEnableClientEvent(number));
	}

	public void showQuestionMark(int number)
	{
		L2Player player = getPlayer();
		if(player != null)
			player.sendPacket(new TutorialShowQuestionMark(number));
	}

	public void showTutorialHTML(String html)
	{
		L2Player player = getPlayer();
		if(player == null)
			return;
		String text = Files.read("./data/scripts/quests/_255_Tutorial/" + html, player);
		if(text == null || text.equalsIgnoreCase(""))
			text = "<html><body>File data/scripts/quests/_255_Tutorial/" + html + " not found or file is empty.</body></html>";
		player.sendPacket(new TutorialShowHtml(text));
	}

	/**
	 * Start a timer for quest.<BR><BR>
	 * @param name<BR> The name of the timer. Will also be the value for event of onEvent
	 * @param time<BR> The milisecond value the timer will elapse
	 */
	public void startQuestTimer(String name, long time)
	{
		getQuest().startQuestTimer(name, time, null, getPlayer());
	}

	public void cancelQuestTimer(String name)
	{
		getQuest().cancelQuestTimer(name, getPlayer());
	}

	/**
	 * Удаляет указанные предметы из инвентаря игрока, и обновляет инвентарь
	 * @param itemId : id удаляемого предмета
	 * @param count : число удаляемых предметов<br>
	 * Если count передать -1, то будут удалены все указанные предметы.
	 * @return Количество удаленных предметов
	 */
	public long takeItems(int itemId, long count)
	{
		L2Player player = getPlayer();
		if(player == null)
			return 0;

		// Get object item from player's inventory list
		L2ItemInstance item = player.getInventory().getItemByItemId(itemId);
		if(item == null)
			return 0;
		// Tests on count value in order not to have negative value
		if(count < 0 || count > item.getCount())
			count = item.getCount();

		// Destroy the quantity of items wanted
		player.getInventory().destroyItemByItemId(itemId, count, true);
		// Send message of destruction to client
		player.sendPacket(SystemMessage.removeItems(itemId, count));

		return count;
	}

	public long takeAllItems(int itemId)
	{
		return takeItems(itemId, -1);
	}

	public long takeAllItems(int... itemsIds)
	{
		long result = 0;
		for(int id : itemsIds)
			result += takeAllItems(id);
		return result;
	}

	public long takeAllItems(short... itemsIds)
	{
		long result = 0;
		for(int id : itemsIds)
			result += takeAllItems(id);
		return result;
	}

	public long takeAllItems(Collection<Integer> itemsIds)
	{
		long result = 0;
		for(int id : itemsIds)
			result += takeAllItems(id);
		return result;
	}

	/**
	 * Remove the variable of quest from the list of variables for the quest.<BR><BR>
	 * <U><I>Concept : </I></U>
	 * Remove the variable of quest represented by "var" from the class variable FastMap "vars" and from the database.
	 * @param var : String designating the variable for the quest to be deleted
	 * @return String pointing out the previous value associated with the variable "var"
	 */
	public String unset(String var)
	{
		if(_vars == null || var == null)
			return null;
		String old = _vars.remove(var);
		if(old != null)
			Quest.deleteQuestVarInDb(this, var);
		return old;
	}

	private boolean checkPartyMember(L2Player member, int state, int maxrange, L2Object rangefrom)
	{
		if(member == null)
			return false;
		if(rangefrom != null && maxrange > 0 && !member.isInRange(rangefrom, maxrange))
			return false;
		QuestState qs = member.getQuestState(getQuest().getName());
		if(qs == null || qs.getState() != state)
			return false;
		return true;
	}

	public GArray<L2Player> getPartyMembers(int state, int maxrange, L2Object rangefrom)
	{
		GArray<L2Player> result = new GArray<L2Player>();
		L2Party party = getPlayer().getParty();
		if(party == null)
		{
			if(checkPartyMember(getPlayer(), state, maxrange, rangefrom))
				result.add(getPlayer());
			return result;
		}

		for(L2Player _member : party.getPartyMembers())
			if(checkPartyMember(_member, state, maxrange, rangefrom))
				result.add(getPlayer());

		return result;
	}

	public L2Player getRandomPartyMember(int state, int maxrangefromplayer)
	{
		return getRandomPartyMember(state, maxrangefromplayer, getPlayer());
	}

	public L2Player getRandomPartyMember(int state, int maxrange, L2Object rangefrom)
	{
		GArray<L2Player> list = getPartyMembers(state, maxrange, rangefrom);
		if(list.size() == 0)
			return null;
		return list.get(Rnd.get(list.size()));
	}

	/**
	 * Add spawn for player instance
	 * Return object id of newly spawned npc
	 */
	public L2NpcInstance addSpawn(int npcId)
	{
		return addSpawn(npcId, getPlayer().getX(), getPlayer().getY(), getPlayer().getZ(), 0, 0, 0);
	}

	public L2NpcInstance addSpawn(int npcId, int despawnDelay)
	{
		return addSpawn(npcId, getPlayer().getX(), getPlayer().getY(), getPlayer().getZ(), 0, 0, despawnDelay);
	}

	public L2NpcInstance addSpawn(int npcId, int x, int y, int z)
	{
		return addSpawn(npcId, x, y, z, 0, 0, 0);
	}

	/**
	 * Add spawn for player instance
	 * Will despawn after the spawn length expires
	 * Return object id of newly spawned npc
	 */
	public L2NpcInstance addSpawn(int npcId, int x, int y, int z, int despawnDelay)
	{
		return addSpawn(npcId, x, y, z, 0, 0, despawnDelay);
	}

	/**
	 * Add spawn for player instance
	 * Return object id of newly spawned npc
	 */
	public L2NpcInstance addSpawn(int npcId, int x, int y, int z, int heading, int randomOffset, int despawnDelay)
	{
		return getQuest().addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay);
	}

	public L2NpcInstance findTemplate(int npcId)
	{
		for(L2Spawn spawn : SpawnTable.getInstance().getSpawnTable())
			if(spawn != null && spawn.getNpcId() == npcId)
				return spawn.getLastSpawn();
		return null;
	}

	public int calculateLevelDiffForDrop(int mobLevel, int player)
	{
		if(!ConfigValue.UseDeepBlueDropRules)
			return 0;
		return Math.max(player - mobLevel - ConfigValue.DeepBlueDropMaxDiff, 0);
	}

	/**
	 * Возвращает текущий номер конда в стандартной нотации. Если не определен возвращает 0.
	 */
	public int getCond()
	{
		int value = getRawInt("cond");

		if(value < 0) // новая побитовая нотация
			return bitToInt(value);

		return value;
	}

	private int bitToInt(int value)
	{
		// перебираем биты начиная с 30, поскольку 31 это бит знака
		for(int i = 30; i >= 0; i--)
			if((value & (1 << i)) > 0) // если бит под этим номером определен
				return i + 1; // нумерация битов начинается с 0, а кондов с 1
		return 0;
	}

	/** Первый конд пропускать нельзя, Integer.MIN_VALUE флаг новой нотации */
	private static final int mask = 1 | Integer.MIN_VALUE;

	public String setCond(int newCond)
	{
		return setCond(newCond, true);
	}

	/**
	 * Записывает номер конда с проверкой нотации и сохраняет в базу используя set(String, String, true).
	 */
	public String setCond(int newCond, boolean store)
	{
		final String result = set("cond", String.valueOf(newCond), false);
		if(store)
			Quest.updateQuestVarInDb(this, "cond", String.valueOf(newCond));

		L2Player player = getPlayer();
		if(player == null)
			return null;
		player.sendPacket(new QuestList(player));
		if(newCond != 0 && (getQuest().getQuestIntId() < 999 || getQuest().getQuestIntId() > 10000) && isStarted())
			player.sendPacket(new ExShowQuestMark(getQuest().getQuestIntId(), getCond()));
		return result;
	}

	/*
	 * Reset time for Quest
	 * Default: 6:30AM on server time
	 */
	private static final int RESTART_HOUR = 6;
	private static final int RESTART_MINUTES = 30;
	
	/**
	 * Устанавлевает время, когда квест будет доступен персонажу.
	 * Метод используется для квестов, которые проходятся один раз в день.
	 */
	public void setRestartTime()
	{
		Calendar reDo = Calendar.getInstance();
		if(reDo.get(Calendar.HOUR_OF_DAY) >= RESTART_HOUR)
			reDo.add(Calendar.DATE, 1);
		reDo.set(Calendar.HOUR_OF_DAY, RESTART_HOUR);
		reDo.set(Calendar.MINUTE, RESTART_MINUTES);
		set("restartTime", String.valueOf(reDo.getTimeInMillis()));
	}

	/**
	 * Проверяет, наступило ли время для выполнения квеста.
	 * Метод используется для квестов, которые проходятся один раз в день.
	 * @return boolean
	 */
	public boolean isNowAvailable()
	{
		String val = get("restartTime");
		if(val == null)
			return true;

		long restartTime = Long.parseLong(val);
		return restartTime <= System.currentTimeMillis();
	}

	public void SetFlagJournal(L2Player player, int id, int state)
	{
		setCond(state);
	}

	public void ShowQuestMark(L2Player player, int id)
	{
		if(player != null)
		{
			QuestState qs = player.getQuestState(id);
			player.sendPacket(new ExShowQuestMark(id, qs == null ? 0 : qs.getCond()));
		}
	}

	public void SoundEffect(L2Player player, String sound)
	{
		if(player != null)
			player.sendPacket(new PlaySound(sound));
	}

	// Получаем и сравниваем второй стейт с нужным нам при условии что поставили цифру 1 если поставим 0, то это будет первый стейт
	public int GetMemoStateEx(L2Player player, int id, int val)
	{
		return player.getQuestState(id).getInt("MemoState"+val);
	}

	public void SetMemoStateEx(L2Player player, int id, int val, int state)
	{
		player.getQuestState(id).set("MemoState"+val, state);
	}

	public int GetMemoState(L2Player player, int id)
	{
		return player.getQuestState(id).getInt("MemoState0");
	}

	public void SetMemoState(L2Player player, int id, int state)
	{
		player.getQuestState(id).set("MemoState0", state);
	}

	public int HaveMemo(L2Player player, int id)
	{
		return (player.getQuestState(QuestManager.getQuest(id).getName()) == null || player.getQuestState(QuestManager.getQuest(id).getName()).getCond() < 1) ? 0 : 1;
	}
}