package l2open.gameserver.model.quest;

import gnu.trove.TIntObjectHashMap;
import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.entity.olympiad.CompType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.ExQuestNpcLogList;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Location;
import org.apache.commons.lang.ArrayUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Quest
{
	public static String SOUND_ITEMGET = "ItemSound.quest_itemget";
	public static String SOUND_ACCEPT = "ItemSound.quest_accept";
	public static String SOUND_MIDDLE = "ItemSound.quest_middle";
	public static String SOUND_FINISH = "ItemSound.quest_finish";
	public static String SOUND_GIVEUP = "ItemSound.quest_giveup";
	public static String SOUND_TUTORIAL = "ItemSound.quest_tutorial";
	public static String SOUND_JACKPOT = "ItemSound.quest_jackpot";
	public static String SOUND_HORROR2 = "SkillSound5.horror_02";
	public static String SOUND_BEFORE_BATTLE = "Itemsound.quest_before_battle";
	public static String SOUND_FANFARE_MIDDLE = "ItemSound.quest_fanfare_middle";
	public static String SOUND_FANFARE2 = "ItemSound.quest_fanfare_2";
	public static String SOUND_BROKEN_KEY = "ItemSound2.broken_key";
	public static String SOUND_ENCHANT_SUCESS = "ItemSound3.sys_enchant_sucess";
	public static String SOUND_ENCHANT_FAILED = "ItemSound3.sys_enchant_failed";
	public static String SOUND_ED_CHIMES05 = "AmdSound.ed_chimes_05";
	public static String SOUND_ARMOR_WOOD_3 = "ItemSound.armor_wood_3";
	public static String SOUND_ITEM_DROP_EQUIP_ARMOR_CLOTH = "ItemSound.item_drop_equip_armor_cloth";

	public static final int ADENA_ID = 57;

	public static final int PARTY_NONE = 0;
	public static final int PARTY_ONE = 1;
	public static final int PARTY_ALL = 2;

	public static enum CheckStatus
	{
		NONE,
		GRACIA_FINAL,
		GRACIA_EPILOGUE
	}

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.NONE;
	public static final CheckStatus CURRENT_STATUS = CheckStatus.GRACIA_EPILOGUE;

	private static Logger _log = Logger.getLogger(Quest.class.getName());

	/** HashMap containing lists of timers from the name of the timer */
	private static Map<String, GArray<QuestTimer>> _allEventTimers = new FastMap<String, GArray<QuestTimer>>().setShared(true);
	private static Map<String, GArray<QuestTimer>> _allPausedEventTimers = new FastMap<String, GArray<QuestTimer>>().setShared(true);
	private TIntObjectHashMap<List<QuestNpcLogInfo>> _npcLogList = new TIntObjectHashMap<List<QuestNpcLogInfo>>();

	private GArray<Integer> _questitems = new GArray<Integer>();

	/**
	 * Этот метод для регистрации квестовых вещей, которые будут удалены
	 * при прекращении квеста, независимо от того, был он закончен или
	 * прерван. <strong>Добавлять сюда награды нельзя</strong>.
	 */
	public void addQuestItem(int... ids)
	{
		for(int id : ids)
			if(id != 0)
			{
				L2Item i = null;
				try
				{
					i = ItemTemplates.getInstance().getTemplate(id);
				}
				catch(Exception e)
				{
					System.out.println("Warning: unknown item " + i + " (" + id + ") in quest drop in " + getName());
				}

				/**if(i == null || i.getType2() != L2Item.TYPE2_QUEST)
					System.out.println("Warning: non-quest item " + i + " (" + id + ") in quest drop in " + getName());

				if(_questitems.contains(id))
					System.out.println("Warning: " + i + " (" + id + ") multiple times in quest drop in " + getName()); Скрываем от клиентов.*/

				_questitems.add(id);
			}
	}

	public GArray<Integer> getItems()
	{
		return _questitems;
	}

	/**
	 * Update informations regarding quest in database.<BR>
	 * <U><I>Actions :</I></U><BR>
	 * <LI>Get ID state of the quest recorded in object qs</LI>
	 * <LI>Save in database the ID state (with or without the star) for the variable called "&lt;state&gt;" of the quest</LI>
	 * @param qs : QuestState
	 */
	public static void updateQuestInDb(QuestState qs)
	{
		updateQuestVarInDb(qs, "<state>", qs.getStateName());
	}

	/**
	 * Insert in the database the quest for the player.
	 * @param qs : QuestState pointing out the state of the quest
	 * @param var : String designating the name of the variable for the quest
	 * @param value : String designating the value of the variable for the quest
	 */
	public static void updateQuestVarInDb(QuestState qs, String var, String value)
	{
		L2Player player = qs.getPlayer();
		if(player == null)
			return;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO character_quests (char_id,name,var,value) VALUES (?,?,?,?)");
			statement.setInt(1, qs.getPlayer().getObjectId());
			statement.setString(2, qs.getQuest().getName());
			statement.setString(3, var);
			statement.setString(4, value);
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "could not insert char quest:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Delete the player's quest from database.
	 * @param qs : QuestState pointing out the player's quest
	 */
	public static void deleteQuestInDb(QuestState qs)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND name=?");
			statement.setInt(1, qs.getPlayer().getObjectId());
			statement.setString(2, qs.getQuest().getName());
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "could not delete char quest:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

		/**
	 * Delete the player's quest from database.
	 * @param qs : QuestState pointing out the player's quest
	 */
	public static void deleteAllQuestInDb(String name)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM character_quests WHERE name=?");
			statement.setString(1, name);
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "could not delete char quest:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Delete a variable of player's quest from the database.
	 * @param qs : object QuestState pointing out the player's quest
	 * @param var : String designating the variable characterizing the quest
	 */
	public static void deleteQuestVarInDb(QuestState qs, String var)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND name=? AND var=?");
			statement.setInt(1, qs.getPlayer().getObjectId());
			statement.setString(2, qs.getQuest().getName());
			statement.setString(3, var);
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "could not delete char quest:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Add quests to the L2Player.<BR><BR>
	 * <U><I>Action : </U></I><BR>
	 * Add state of quests, drops and variables for quests in the HashMap _quest of L2Player
	 * @param player : Player who is entering the world
	 */
	public static void playerEnter(L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		FiltredPreparedStatement invalidQuestData = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			invalidQuestData = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? and name=?");
			statement = con.prepareStatement("SELECT name,value FROM character_quests WHERE char_id=? AND var=?");
			statement.setInt(1, player.getObjectId());
			statement.setString(2, "<state>");
			rset = statement.executeQuery();
			while(rset.next())
			{
				String questId = rset.getString("name");
				String state = rset.getString("value");

				if(state.equalsIgnoreCase("Start")) // невзятый квест
				{
					invalidQuestData.setInt(1, player.getObjectId());
					invalidQuestData.setString(2, questId);
					invalidQuestData.executeUpdate();
					continue;
				}

				// Search quest associated with the ID
				Quest q = QuestManager.getQuest(questId);
				if(q == null)
				{
					if(!ConfigValue.StartWhisoutQuest)
						_log.warning("Unknown quest " + questId + " for player " + player.getName());
					continue;
				}

				// Create a new QuestState for the player that will be added to the player's list of quests
				new QuestState(q, player, getStateId(state));
			}

			invalidQuestData.close();
			DatabaseUtils.closeDatabaseSR(statement, rset);

			// Get list of quests owned by the player from the DB in order to add variables used in the quest.
			statement = con.prepareStatement("SELECT name,var,value FROM character_quests WHERE char_id=?");
			statement.setInt(1, player.getObjectId());
			rset = statement.executeQuery();
			while(rset.next())
			{
				String questId = rset.getString("name");
				String var = rset.getString("var");
				String value = rset.getString("value");
				// Get the QuestState saved in the loop before
				QuestState qs = player.getQuestState(questId);
				if(qs == null)
					continue;
				// затычка на пропущенный первый конд
				if(var.equals("cond") && Integer.parseInt(value) < 0)
					value = String.valueOf(Integer.parseInt(value) | 1);
				// Add parameter to the quest
				qs.set(var, value, false);
			}

			// Восстанавливаем таймеры, есть есть
			resumeQuestTimers(player);
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "could not insert char quest:", e);
		}
		finally
		{
			DatabaseUtils.closeStatement(invalidQuestData);
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	protected String _descr;

	protected final String _name;

	protected final int _party;

	protected final int _questId;

	public final static int MEMOSTATE = 0;
	public final static int CREATED = 1;
	public final static int STARTED = 2;
	public final static int COMPLETED = 3;
	public final static int DELAYED = 4;

	public static String getStateName(int state)
	{
		switch(state)
		{
			case 1:
				return "Start";
			case 2:
				return "Started";
			case 3:
				return "Completed";
			case 4:
				return "Delayed";
		}
		return "Start";
	}

	public static int getStateId(String state)
	{
		if(state.equalsIgnoreCase("Start"))
			return 1;
		else if(state.equalsIgnoreCase("Started"))
			return 2;
		else if(state.equalsIgnoreCase("Completed"))
			return 3;
		else if(state.equalsIgnoreCase("Delayed"))
			return 4;
		return 1;
	}

	/**
	 * Deprecated.
	 */
	public Quest(boolean party)
	{
		this(null, party ? 1 : 0);
	}

	/**
	 * Deprecated.
	 */
	public Quest(String descr, boolean party)
	{
		this(descr, party ? 1 : 0);
	}

	/**
	 * 0 - по ластхиту, 1 - случайно по пати, 2 - всей пати.
	 */
	public Quest(int party)
	{
		this(null, party);
	}

	public Quest(String descr, int party)
	{
		_name = getClass().getSimpleName();
		_questId = Integer.parseInt(_name.split("_")[1]);
		_descr = descr;
		if(ConfigValue.EngQuestNames && _descr == null)
			_descr = getDescr();
		_party = party;

	//	if(LAST_CHECK_STATUS != CURRENT_STATUS)
	//		Log.add("Quest " + _questId + " unchecked, last validation: " + LAST_CHECK_STATUS.toString(), "quest");

		QuestManager.addQuest(this);
	}

	public Quest(String descr, int party, int questId)
	{
		_name = getClass().getSimpleName();
		_questId = questId;
		_descr = descr;
		if(ConfigValue.EngQuestNames && _descr == null)
			_descr = getDescr();
		_party = party;

	//	if(LAST_CHECK_STATUS != CURRENT_STATUS)
	//		Log.add("Quest " + _questId + " unchecked, last validation: " + LAST_CHECK_STATUS.toString(), "quest");

		QuestManager.addQuest(this);
	}

	public Quest(int party, int questId)
	{
		_name = getClass().getSimpleName();
		_questId = questId;
		_descr = null;
		if(ConfigValue.EngQuestNames && _descr == null)
			_descr = getDescr();
		_party = party;

	//	if(LAST_CHECK_STATUS != CURRENT_STATUS)
	//		Log.add("Quest " + _questId + " unchecked, last validation: " + LAST_CHECK_STATUS.toString(), "quest");

		QuestManager.addQuest(this);
	}

	/**
	 * Add this quest to the list of quests that the passed mob will respond to
	 * for Attack Events.<BR>
	 * <BR>
	 *
	 * @param attackIds
	 */
	public void addAttackId(int... attackIds)
	{
		for(int attackId : attackIds)
			addEventId(attackId, QuestEventType.MOBGOTATTACKED);
	}

	/**
	 * Add this quest to the list of quests that the passed mob will respond to
	 * for the specified Event type.<BR>
	 * <BR>
	 *
	 * @param npcId : id of the NPC to register
	 * @param eventType : type of event being registered
	 * @return int : npcId
	 */
	public L2NpcTemplate addEventId(int npcId, QuestEventType eventType)
	{
		try
		{
			L2NpcTemplate t = NpcTable.getTemplate(npcId);
			if(t != null)
				t.addQuestEvent(eventType, this);
			return t;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Add this quest to the list of quests that the passed mob will respond to
	 * for Kill Events.<BR>
	 * <BR>
	 *
	 * @param killIds
	 */
	public void addKillId(int... killIds)
	{
		for(int killid : killIds)
			addEventId(killid, QuestEventType.MOBKILLED);
	}

	public void addKillId(Collection<Integer> killIds)
	{
		for(int killid : killIds)
			addKillId(killid);
	}

	public void addKillNpcWithLog(int cond, String varName, int max, int... killIds)
	{
		if (killIds.length == 0)
		{
			throw new IllegalArgumentException("Npc list cant be empty!");
		}
		addKillId(killIds);
		if (_npcLogList.isEmpty())
			_npcLogList = new TIntObjectHashMap<List<QuestNpcLogInfo>>(5);
		List<QuestNpcLogInfo> vars = _npcLogList.get(cond);
		if (vars == null)
			_npcLogList.put(cond, vars = new ArrayList<QuestNpcLogInfo>(5));
		vars.add(new QuestNpcLogInfo(killIds, varName, max));
	}

	public boolean updateKill(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getPlayer();
		if (player == null)
			return false;
		List<QuestNpcLogInfo> vars = getNpcLogList(st.getCond());
		if (vars == null)
			return false;
		boolean done = true;
		boolean find = false;
		for(QuestNpcLogInfo info : vars)
		{
			int count = st.getInt(info.getVarName());
			if ((!(find)) && (ArrayUtils.contains(info.getNpcIds(), npc.getNpcId())))
			{
				find = true;
				if (count < info.getMaxCount())
				{
					st.set(info.getVarName(), ++count);
					player.sendPacket(new ExQuestNpcLogList(st));
				}
			}

			if (count != info.getMaxCount())
			{
				done = false;
			}
		}
		return done;
	}

	public List<QuestNpcLogInfo> getNpcLogList(int cond)
	{
		return _npcLogList.get(cond);
	}

	/**
	 * Add this quest to the list of quests that the passed npc will respond to
	 * for Skill-Use Events.<BR>
	 * <BR>
	 *
	 * @param npcId : ID of the NPC
	 * @return int : ID of the NPC
	 */
	public L2NpcTemplate addSkillUseId(int npcId)
	{
		return addEventId(npcId, QuestEventType.MOB_TARGETED_BY_SKILL);
	}

	public void addStartNpc(int... npcIds)
	{
		for(int talkId : npcIds)
			addStartNpc(talkId);
	}

	/**
	 * Add the quest to the NPC's startQuest
	 * Вызывает addTalkId
	 *
	 * @param npcId
	 * @return L2NpcTemplate : Start NPC
	 */
	public L2NpcTemplate addStartNpc(int npcId)
	{
		addTalkId(npcId);
		return addEventId(npcId, QuestEventType.QUEST_START);
	}

	/**
	* Add the quest to the NPC's first-talk (default action dialog)
	*
	* @param npcIds
	 * @return L2NpcTemplate : Start NPC
	 */
	public void addFirstTalkId(int... npcIds)
	{
		for(int npcId : npcIds)
			addEventId(npcId, QuestEventType.NPC_FIRST_TALK);
	}

	/**
	 * Add this quest to the list of quests that the passed npc will respond to
	 * for Talk Events.<BR>
	 * <BR>
	 *
	 * @param talkIds : ID of the NPC
	 */
	public void addTalkId(int... talkIds)
	{
		for(int talkId : talkIds)
			addEventId(talkId, QuestEventType.QUEST_TALK);
	}

	public void addTalkId(Collection<Integer> talkIds)
	{
		for(int talkId : talkIds)
			addTalkId(talkId);
	}

	/**
	 * Возвращает название квеста на языке игрока, если возможно
	 */
	public String getDescr(L2Player player)
	{
		if(_descr == null)
			return new CustomMessage("q." + _questId, player).toString();
		return _descr;
	}

	/**
	 * Возвращает английское название квеста
	 */
	public String getDescr()
	{
		if(_descr == null)
			return new CustomMessage("q." + _questId, "en").toString();
		return _descr;
	}

	/**
	 * Return name of the quest
	 * @return String
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * Return ID of the quest
	 * @return int
	 */
	public int getQuestIntId()
	{
		return _questId;
	}

	/**
	 * Return party state of quest
	 * @return String
	 */
	public int getParty()
	{
		return _party;
	}

	/**
	 * Add a new QuestState to the database and return it.
	 * @param player
	 * @param state TODO
	 * @return QuestState : QuestState created
	 */
	public QuestState newQuestState(L2Player player, int state)
	{
		QuestState qs = new QuestState(this, player, state);
		Quest.updateQuestInDb(qs);
		return qs;
	}

	public QuestState newQuestStateAndNotSave(L2Player player, int state)
	{
		return new QuestState(this, player, state);
	}

	public void notifyAttack(L2NpcInstance npc, QuestState qs)
	{
		String res = null;
		try
		{
			res = onAttack(npc, qs);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
			return;
		}
		showResult(npc, qs.getPlayer(), res);
	}

	public void notifyDeath(L2Character killer, L2Playable victim, QuestState qs)
	{
		String res = null;
		try
		{
			res = onDeath(killer, victim, qs);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
			return;
		}
		showResult(null, qs.getPlayer(), res);
	}

	public void notifyEvent(String event, QuestState qs, L2NpcInstance npc)
	{
		String res = null;
		try
		{
			if(npc == null || npc.getTemplate().canTalkThisQuest(this))
				res = onEvent(event, qs, npc);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
			return;
		}
		showResult(npc, qs.getPlayer(), res);
	}

	public void notifyKill(L2NpcInstance npc, QuestState qs)
	{
		String res = null;
		try
		{
			res = onKill(npc, qs);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
			return;
		}
		showResult(npc, qs.getPlayer(), res);
	}

	public void notifyPlayerKill(L2Player player, QuestState qs)
	{
		String res = null;
		try
		{
			res = onPlayerKill(player, qs);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
			return;
		}
		showResult(null, qs.getPlayer(), res);
	}

	/**
	 * Override the default NPC dialogs when a quest defines this for the given NPC
	 */
	public final boolean notifyFirstTalk(L2NpcInstance npc, L2Player player)
	{
		String res = null;
		try
		{
			res = onFirstTalk(npc, player);
		}
		catch(Exception e)
		{
			showError(player, e);
			return true;
		}
		// if the quest returns text to display, display it. Otherwise, use the default npc text.
		return showResult(npc, player, res);
	}

	public boolean notifyTalk(L2NpcInstance npc, QuestState qs)
	{
		String res = null;
		try
		{
			res = onTalk(npc, qs);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			showError(qs.getPlayer(), e);
			return true;
		}
		return showResult(npc, qs.getPlayer(), res);
	}

	public boolean notifySkillUse(L2NpcInstance npc, L2Skill skill, QuestState qs)
	{
		String res = null;
		try
		{
			res = onSkillUse(npc, skill, qs);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
			return true;
		}
		return showResult(npc, qs.getPlayer(), res);
	}

	public void notifyPlayerEnter(QuestState qs)
	{
		try
		{
			onPlayerEnter(qs);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
		}
	}

	public void notifyOlympiadGame(QuestState qs, GArray<L2Player> winTeam, GArray<L2Player> lossTeam, boolean haveWin, CompType type)
	{}

	public void onPlayerEnter(QuestState qs)
	{}

	public void onChange(boolean change)
	{}

	public String onAttack(L2NpcInstance npc, QuestState qs)
	{
		return null;
	}

	public String onDeath(L2Character killer, L2Playable victim, QuestState qs)
	{
		return null;
	}

	public String onEvent(String event, QuestState qs, L2NpcInstance npc)
	{
		return null;
	}

	public String onKill(L2NpcInstance npc, QuestState qs)
	{
		return null;
	}

	public String onPlayerKill(L2Player killed, QuestState st)
	{
		return null;
	}

	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		return null;
	}

	public String onTalk(L2NpcInstance npc, QuestState qs)
	{
		return null;
	}

	public String onSkillUse(L2NpcInstance npc, L2Skill skill, QuestState qs)
	{
		return null;
	}

	public void onAbort(QuestState qs)
	{}

	/**
	 * Show message error to player who has an access level greater than 0
	 * @param player : L2Player
	 * @param t : Throwable
	 */
	private void showError(L2Player player, Throwable t)
	{
		_log.log(Level.WARNING, "", t);
		if(player != null && player.isGM())
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			pw.close();
			String res = "<html><body><title>Script error</title>" + sw.toString() + "</body></html>";
			showResult(null, player, res);
		}
	}

	/**
	 * Show HTML file to client
	 * @param fileName
	 * @return String : message sent to client
	 */
	public String showHtmlFile(L2Player player, String fileName)
	{
		return showHtmlFile(player, fileName, null, (String[]) null); //(String[]) - затык для вызова корректного метода.
	}

	public String showHtmlFile(L2Player player, String fileName, String toReplace, String replaceWith)
	{
		return showHtmlFile(player, fileName, new String[] { toReplace }, new String[] { replaceWith });
	}

	public String showHtmlFile(L2Player player, String fileName, String toReplace[], String replaceWith[])
	{
		String content = null;
		String _path = null;
		// for scripts
		if(fileName.contains("/"))
			content = Files.read(fileName, player);
		else
		{
			_path = getClass().toString();
			_path = _path.substring(6, _path.lastIndexOf(".")) + ".";
			content = Files.read("data/scripts/" + _path.replace(".", "/") + fileName, player);
		}

		if(content == null)
			content = "Can't find file '" + _path + fileName + "'";

		if(player != null && player.getTarget() != null)
			content = content.replaceAll("%objectId%", String.valueOf(player.getTarget().getObjectId()));

		// Make a replacement inside before sending html to client
		if(toReplace != null && replaceWith != null && toReplace.length == replaceWith.length)
			for(int i = 0; i < toReplace.length; i++)
				content = content.replaceAll(toReplace[i], replaceWith[i]);

		// Send message to client if message not empty
		if(content != null && player != null)
		{
			NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
			npcReply.setHtml(content);
			if(getQuestIntId() > 0 && getQuestIntId() < 20000)
				if(getQuestIntId() != 999)
					npcReply.setQuest(getQuestIntId());
			player.sendPacket(npcReply);
		}

		return content;
	}

	/**
	 * Show a message to player.<BR><BR>
	 * <U><I>Concept : </I></U><BR>
	 * 3 cases are managed according to the value of the parameter "res" :<BR>
	 * <LI><U>"res" ends with string ".html" :</U> an HTML is opened in order to be shown in a dialog box</LI>
	 * <LI><U>"res" starts with tag "html" :</U> the message hold in "res" is shown in a dialog box</LI>
	 * <LI><U>"res" is null :</U> do not show any message</LI>
	 * <LI><U>"res" is empty string :</U> show default message</LI>
	 * <LI><U>otherwise :</U> the message hold in "res" is shown in chat box</LI>
	 * @param npc TODO
	 * @param player
	 * @param res : String pointing out the message to show at the player
	 * @return boolean, if false onFirstTalk show default npc message, if true - quest message 
	 */
	private boolean showResult(L2NpcInstance npc, L2Player player, String res)
	{
		if(res == null) // do not show message
			return true;
		if(res.isEmpty()) // show default npc message
			return false;
		if(res.startsWith("no_quest") || res.equalsIgnoreCase("noquest") || res.equalsIgnoreCase("no-quest"))
			showHtmlFile(player, "data/html/no-quest.htm");
		else if(res.equalsIgnoreCase("completed"))
			showHtmlFile(player, "data/html/completed-quest.htm");
		else if(res.endsWith(".htm"))
			showHtmlFile(player, res);
		else
		{
			NpcHtmlMessage npcReply = npc == null ? new NpcHtmlMessage(5) : new NpcHtmlMessage(player, npc);
			npcReply.setHtml(res);
			if(getQuestIntId() > 0 && getQuestIntId() < 20000)
				if(getQuestIntId() != 999)
					npcReply.setQuest(getQuestIntId());
			player.sendPacket(npcReply);
		}
		return true;
	}

	public void removeQuestTimer(QuestTimer timer)
	{
		if(timer == null)
			return;
		GArray<QuestTimer> timers = getQuestTimers(timer.getName());
		if(timers == null)
			return;
		timers.remove(timer);
	}

	// Останавливаем и сохраняем таймеры (при выходе из игры)
	public synchronized static void pauseQuestTimes(L2Player player)
	{
		GArray<QuestTimer> toSleep = new GArray<QuestTimer>();
		for(GArray<QuestTimer> timers : _allEventTimers.values())
			for(QuestTimer timer : timers)
				if(timer != null && timer.getPlayer() == player)
					toSleep.add(timer);

		for(QuestTimer timer : toSleep)
		{
			timer.cancel();
			GArray<QuestTimer> temp = _allPausedEventTimers.get(timer.getName());
			if(temp == null)
			{
				temp = new GArray<QuestTimer>();
				_allPausedEventTimers.put(timer.getName(), temp);
			}
			temp.add(timer);
		}
	}

	// Восстанавливаем таймеры (при входе в игру)
	public synchronized static void resumeQuestTimers(L2Player player)
	{
		GArray<QuestTimer> toWakeUp = new GArray<QuestTimer>();
		for(GArray<QuestTimer> timers : _allPausedEventTimers.values())
			for(QuestTimer timer : timers)
				if(timer != null && timer.getPlayer() == player)
					toWakeUp.add(timer);

		for(QuestTimer timer : toWakeUp)
		{
			GArray<QuestTimer> timers = _allPausedEventTimers.get(timer.getName());
			if(timers != null)
				timers.remove(timer);
			startQuestTimer(timer.getQuest(), timer.getName(), timer.getTime(), timer.getNpc(), player);
		}
	}

	public synchronized static QuestTimer getQuestTimer(Quest quest, String name, L2Player player)
	{
		if(_allEventTimers.get(name) == null)
			return null;
		for(QuestTimer timer : _allEventTimers.get(name))
			if(timer.isMatch(quest, name, player))
				return timer;
		return null;
	}

	public static GArray<QuestTimer> getQuestTimers(String name)
	{
		return _allEventTimers.get(name);
	}

	public void startQuestTimer(String name, long time, L2NpcInstance npc, L2Player player)
	{
		startQuestTimer(this, name, time, npc, player);
	}

	/**
	 * Add a timer to the quest, if it doesn't exist already
	 * @param name: name of the timer (also passed back as "event" in onAdvEvent)
	 * @param time: time in ms for when to fire the timer
	 * @param npc: npc associated with this timer (can be null)
	 * @param player: player associated with this timer (can be null)
	 */
	public synchronized static void startQuestTimer(Quest quest, String name, long time, L2NpcInstance npc, L2Player player)
	{
		// Add quest timer if timer doesn't already exist
		GArray<QuestTimer> timers = getQuestTimers(name);
		if(timers == null)
		{
			timers = new GArray<QuestTimer>();
			timers.add(new QuestTimer(quest, name, time, npc, player));
			_allEventTimers.put(name, timers);
		}
		// a timer with this name exists, but may not be for the same set of npc and player
		else // if there exists a timer with this name, allow the timer only if the [npc, player] set is unique
		// nulls act as wildcards
		if(getQuestTimer(quest, name, player) == null)
			timers.add(new QuestTimer(quest, name, time, npc, player));
	}

	public void cancelQuestTimer(String name, L2Player player)
	{
		cancelQuestTimer(this, name, player);
	}

	public synchronized static void cancelQuestTimer(Quest quest, String name, L2Player player)
	{
		GArray<QuestTimer> toRemove = new GArray<QuestTimer>();
		for(GArray<QuestTimer> timers : _allEventTimers.values())
			for(QuestTimer timer : timers)
				if(timer.isMatch(quest, name, player))
					toRemove.add(timer);
		for(QuestTimer timer : toRemove)
			timer.cancel();
	}

	public synchronized static QuestTimer stopQuestTimers(L2Player player)
	{
		GArray<QuestTimer> toRemove = new GArray<QuestTimer>();
		for(GArray<QuestTimer> timers : _allEventTimers.values())
			for(QuestTimer timer : timers)
			{
				L2Player pl = timer.getPlayer();
				if(pl == null || pl == player)
					toRemove.add(timer);
			}
		for(QuestTimer timer : toRemove)
			timer.cancel();
		return null;
	}

	protected String str(long i)
	{
		return String.valueOf(i);
	}

	// =========================================================
	//  QUEST SPAWNS
	// =========================================================

	public static class DeSpawnScheduleTimerTask extends l2open.common.RunnableImpl
	{
		L2NpcInstance _npc = null;

		public DeSpawnScheduleTimerTask(L2NpcInstance npc)
		{
			_npc = npc;
		}

		@Override
		public void runImpl()
		{
			if(_npc != null)
				if(_npc.getSpawn() != null)
					_npc.getSpawn().despawnAll();
				else
					_npc.deleteMe();
		}
	}

	public L2NpcInstance addSpawn(int npcId, int x, int y, int z, int heading, int randomOffset, int despawnDelay)
	{
		return addSpawn(npcId, new Location(x, y, z, heading), randomOffset, despawnDelay);
	}

	public L2NpcInstance addSpawn(int npcId, Location loc, int randomOffset, int despawnDelay)
	{
		L2NpcInstance result = Functions.spawn(randomOffset > 50 ? loc.rnd(50, randomOffset, false) : loc, npcId);
		if(despawnDelay > 0 && result != null)
			ThreadPoolManager.getInstance().schedule(new DeSpawnScheduleTimerTask(result), despawnDelay);
		return result;
	}

	/**
	 * Добавляет спаун с числовым значением разброса - от 50 до randomOffset.
	 * Если randomOffset указан мене 50, то координаты не меняются.
	 */
	public static L2NpcInstance addSpawnToInstance(int npcId, Location loc, int randomOffset, int refId)
	{
		try
		{
			L2NpcTemplate template = NpcTable.getTemplate(npcId);
			if(template != null)
			{
				L2NpcInstance npc = NpcTable.getTemplate(npcId).getNewInstance();
				npc.setReflection(refId);
				npc.setSpawnedLoc(randomOffset > 50 ? loc.rnd(50, randomOffset, false) : loc);
				npc.onSpawn();
				npc.spawnMe(npc.getSpawnedLoc());
				return npc;
			}
		}
		catch(Exception e1)
		{
			_log.warning("Could not spawn Npc " + npcId);
		}
		return null;
	}

	public static L2NpcInstance addSpawnToInstance(Location loc, int npcId, int refId, int resp)
	{
		try
		{
			L2Spawn spawn = new L2Spawn(NpcTable.getTemplate(npcId));
			spawn.setReflection(refId);
			spawn.setLoc(loc);
			spawn.setRespawnDelay(resp);
			if(resp > 0)
				spawn.startRespawn();
			return spawn.doSpawn(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	protected boolean contains(int[] array, int id)
	{
		for(int i : array)
			if(i == id)
				return true;
		return false;
	}

	public boolean canAbortByPacket()
	{
		return true;
	}

	public int HaveMemo(L2Player player, int id)
	{
		return (player.getQuestState(QuestManager.getQuest(id).getName()) == null || player.getQuestState(QuestManager.getQuest(id).getName()).getCond() < 1) ? 0 : 1;
	}

	// Не красиво до пизды, но пускай пока будет так...
	protected int GetMemoCount(L2Player player)
	{
		int count = 0;
		for(QuestState quest : player.getAllQuestsStates())
			if(quest != null && ((quest.getQuest().getQuestIntId() < 999 || quest.getQuest().getQuestIntId() > 10000) && quest.getQuest().getQuestIntId() != 255) && quest.isStarted() && quest.getCond() > 0)
				count++;
		return count;
	}

	public void ShowQuestPage(L2Player player, String fileName, int id)
	{	
		NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
		npcReply.setHtml(Files.read_pts(fileName, player));
		if(id != 999)
			npcReply.setQuest(id);
		player.sendPacket(npcReply);
	}

	public void ShowPage(L2Player player, String fileName)
	{
		NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
		npcReply.setHtml(Files.read_pts(fileName, player));
		player.sendPacket(npcReply);
	}
}