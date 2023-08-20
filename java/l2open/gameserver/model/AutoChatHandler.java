package l2open.gameserver.model;

import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.util.GArray;
import l2open.util.Rnd;

import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

/**
 * Auto Chat Handler
 *
 * Allows NPCs to automatically send messages to nearby players at a set time
 * interval.
 */
public class AutoChatHandler implements SpawnListener
{
	protected static Logger _log = Logger.getLogger(AutoChatHandler.class.getName());

	private static AutoChatHandler _instance;

	private static final long DEFAULT_CHAT_DELAY = 180000; // 3 mins by default

	Map<Integer, AutoChatInstance> _registeredChats;

	protected AutoChatHandler()
	{
		_registeredChats = new FastMap<Integer, AutoChatInstance>().setShared(true);
		restoreChatData();
		L2Spawn.addSpawnListener(this);
	}

	private void restoreChatData()
	{
		int numLoaded = 0;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		FiltredPreparedStatement statement2 = null;
		ResultSet rset = null, rset2 = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("SELECT * FROM auto_chat ORDER BY groupId ASC");
			statement2 = con.prepareStatement("SELECT * FROM auto_chat_text WHERE groupId=?");

			rset = statement.executeQuery();
			while(rset.next())
			{
				numLoaded++;

				statement2.setInt(1, rset.getInt("groupId"));
				rset2 = statement2.executeQuery();

				GArray<String> list = new GArray<String>();
				while(rset2.next())
					list.add(rset2.getString("chatText"));

				registerGlobalChat(rset.getInt("npcId"), list.toArray(new String[] {}), rset.getLong("chatDelay") * 1000L);
				DatabaseUtils.closeResultSet(rset2);
			}
		}
		catch(Exception e)
		{
			_log.warning("AutoChatHandler: Could not restore chat data: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseSR(statement2, rset2);
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public static AutoChatHandler getInstance()
	{
		if(_instance == null)
			_instance = new AutoChatHandler();

		return _instance;
	}

	public int size()
	{
		return _registeredChats.size();
	}

	/**
	 * Registers a globally active auto chat for ALL instances of the given NPC
	 * ID. <BR>
	 * Returns the associated auto chat instance.
	 *
	 * @param int
	 *						npcId
	 * @param String[]
	 *						chatTexts
	 * @param int
	 *						chatDelay (-1 = default delay)
	 * @return AutoChatInstance chatInst
	 */
	public AutoChatInstance registerGlobalChat(int npcId, String[] chatTexts, long chatDelay)
	{
		return registerChat(npcId, null, chatTexts, chatDelay);
	}

	/**
	 * Registers a NON globally-active auto chat for the given NPC instance, and
	 * adds to the currently assigned chat instance for this NPC ID, otherwise
	 * creates a new instance if a previous one is not found. <BR>
	 * Returns the associated auto chat instance.
	 *
	 * @param L2NpcInstance
	 *						npcInst
	 * @param String[]
	 *						chatTexts
	 * @param int
	 *						chatDelay (-1 = default delay)
	 * @return AutoChatInstance chatInst
	 */
	public AutoChatInstance registerChat(L2NpcInstance npcInst, String[] chatTexts, long chatDelay)
	{
		return registerChat(npcInst.getNpcId(), npcInst, chatTexts, chatDelay);
	}

	private AutoChatInstance registerChat(int npcId, L2NpcInstance npcInst, String[] chatTexts, long chatDelay)
	{
		AutoChatInstance chatInst;

		if(chatDelay < 0)
			chatDelay = DEFAULT_CHAT_DELAY;

		if(_registeredChats.containsKey(npcId))
			chatInst = _registeredChats.get(npcId);
		else
			chatInst = new AutoChatInstance(npcId, chatTexts, chatDelay, (npcInst == null));

		if(npcInst != null)
			chatInst.addChatDefinition(npcInst);

		_registeredChats.put(npcId, chatInst);

		return chatInst;
	}

	/**
	 * Removes and cancels ALL auto chat definition for the given NPC ID, and
	 * removes its chat instance if it exists.
	 *
	 * @param int
	 *						npcId
	 * @return boolean removedSuccessfully
	 */
	public boolean removeChat(int npcId)
	{
		AutoChatInstance chatInst = _registeredChats.get(npcId);

		return removeChat(chatInst);
	}

	/**
	 * Removes and cancels ALL auto chats for the given chat instance.
	 *
	 * @param AutoChatInstance
	 *						chatInst
	 * @return boolean removedSuccessfully
	 */
	public boolean removeChat(AutoChatInstance chatInst)
	{
		if(chatInst == null)
			return false;

		_registeredChats.remove(chatInst.getNPCId());
		chatInst.setActive(false);

		return true;
	}

	/**
	 * Returns the associated auto chat instance either by the given NPC ID or
	 * object ID.
	 *
	 * @param int
	 *						id
	 * @param boolean
	 *						byObjectId
	 * @return AutoChatInstance chatInst
	 */
	public AutoChatInstance getAutoChatInstance(int id, boolean byObjectId)
	{
		if(!byObjectId)
			return _registeredChats.get(id);

		for(AutoChatInstance chatInst : _registeredChats.values())
			if(chatInst.getChatDefinition(id) != null)
				return chatInst;

		return null;
	}

	/**
	 * Sets the active state of all auto chat instances to that specified, and
	 * cancels the scheduled chat task if necessary.
	 *
	 * @param boolean
	 *						isActive
	 */
	public void setAutoChatActive(boolean isActive)
	{
		for(AutoChatInstance chatInst : _registeredChats.values())
			chatInst.setActive(isActive);
	}

	/**
	 * Used in conjunction with a SpawnListener, this method is called every
	 * time an NPC is spawned in the world. <BR>
	 * <BR>
	 * If an auto chat instance is set to be "global", all instances matching
	 * the registered NPC ID will be added to that chat instance.
	 */
	@Override
	public void npcSpawned(L2NpcInstance npc)
	{
		synchronized (_registeredChats)
		{
			if(npc == null)
				return;

			int npcId = npc.getNpcId();

			if(_registeredChats.containsKey(npcId))
			{
				AutoChatInstance chatInst = _registeredChats.get(npcId);

				if(chatInst != null && chatInst.isGlobal())
					chatInst.addChatDefinition(npc);
			}
		}
	}

	@Override
	public void npcDeSpawned(L2NpcInstance npc)
	{}

	/**
	 * Auto Chat Instance <BR>
	 * <BR>
	 * Manages the auto chat instances for a specific registered NPC ID.
	 *
	 * @author Tempy
	 */
	public class AutoChatInstance
	{
		int _npcId;

		private long _defaultDelay = DEFAULT_CHAT_DELAY;

		private String[] _defaultTexts;

		private boolean _defaultRandom = false;

		private boolean _globalChat = false;

		private boolean _isActive;

		private Map<Integer, AutoChatDefinition> _chatDefinitions = new FastMap<Integer, AutoChatDefinition>().setShared(true);

		private ScheduledFuture<?> _chatTask;

		AutoChatInstance(int npcId, String[] chatTexts, long chatDelay, boolean isGlobal)
		{
			_defaultTexts = chatTexts;
			_npcId = npcId;
			_defaultDelay = chatDelay;
			_globalChat = isGlobal;

			setActive(true);
		}

		AutoChatDefinition getChatDefinition(int objectId)
		{
			return _chatDefinitions.get(objectId);
		}

		AutoChatDefinition[] getChatDefinitions()
		{
			return _chatDefinitions.values().toArray(new AutoChatDefinition[_chatDefinitions.values().size()]);
		}

		/**
		 * Defines an auto chat for an instance matching this auto chat
		 * instance's registered NPC ID, and launches the scheduled chat task.
		 * <BR>
		 * Returns the object ID for the NPC instance, with which to refer to
		 * the created chat definition. <BR>
		 * <B>Note</B>: Uses pre-defined default values for texts and chat
		 * delays from the chat instance.
		 *
		 * @param L2NpcInstance
		 *						npcInst
		 * @return int objectId
		 */
		public int addChatDefinition(L2NpcInstance npcInst)
		{
			return addChatDefinition(npcInst, null, 0);
		}

		/**
		 * Defines an auto chat for an instance matching this auto chat
		 * instance's registered NPC ID, and launches the scheduled chat task.
		 * <BR>
		 * Returns the object ID for the NPC instance, with which to refer to
		 * the created chat definition.
		 *
		 * @param L2NpcInstance
		 *						npcInst
		 * @param String[]
		 *						chatTexts
		 * @param int
		 *						chatDelay
		 * @return int objectId
		 */
		public int addChatDefinition(L2NpcInstance npcInst, String[] chatTexts, long chatDelay)
		{
			int objectId = npcInst.getObjectId();
			AutoChatDefinition chatDef = new AutoChatDefinition(this, npcInst, chatTexts, chatDelay);

			_chatDefinitions.put(objectId, chatDef);
			return objectId;
		}

		/**
		 * Removes a chat definition specified by the given object ID.
		 *
		 * @param int
		 *						objectId
		 * @return boolean removedSuccessfully
		 */
		public boolean removeChatDefinition(int objectId)
		{
			if(!_chatDefinitions.containsKey(objectId))
				return false;

			AutoChatDefinition chatDefinition = _chatDefinitions.get(objectId);
			chatDefinition.setActive(false);

			_chatDefinitions.remove(objectId);

			return true;
		}

		/**
		 * Tests if this auto chat instance is active.
		 *
		 * @return boolean isActive
		 */
		public boolean isActive()
		{
			return _isActive;
		}

		/**
		 * Tests if this auto chat instance applies to ALL currently spawned
		 * instances of the registered NPC ID.
		 *
		 * @return boolean isGlobal
		 */
		public boolean isGlobal()
		{
			return _globalChat;
		}

		/**
		 * Tests if random order is the DEFAULT for new chat definitions.
		 *
		 * @return boolean isRandom
		 */
		public boolean isDefaultRandom()
		{
			return _defaultRandom;
		}

		/**
		 * Tests if the auto chat definition given by its object ID is set to be
		 * random.
		 *
		 * @return boolean isRandom
		 */
		public boolean isRandomChat(int objectId)
		{
			if(!_chatDefinitions.containsKey(objectId))
				return false;

			return _chatDefinitions.get(objectId).isRandomChat();
		}

		/**
		 * Returns the ID of the NPC type managed by this auto chat instance.
		 *
		 * @return int npcId
		 */
		public int getNPCId()
		{
			return _npcId;
		}

		/**
		 * Returns the number of auto chat definitions stored for this instance.
		 *
		 * @return int definitionCount
		 */
		public int getDefinitionCount()
		{
			return _chatDefinitions.size();
		}

		/**
		 * Returns a list of all NPC instances handled by this auto chat
		 * instance.
		 *
		 * @return L2NpcInstance[] npcInsts
		 */
		public L2NpcInstance[] getNPCInstanceList()
		{
			GArray<L2NpcInstance> npcInsts = new GArray<L2NpcInstance>();

			for(AutoChatDefinition chatDefinition : _chatDefinitions.values())
				npcInsts.add(chatDefinition._npcInstance);

			return npcInsts.toArray(new L2NpcInstance[npcInsts.size()]);
		}

		/**
		 * A series of methods used to get and set default values for new chat
		 * definitions.
		 */
		public long getDefaultDelay()
		{
			return _defaultDelay;
		}

		public String[] getDefaultTexts()
		{
			return _defaultTexts;
		}

		public void setDefaultChatDelay(long delayValue)
		{
			_defaultDelay = delayValue;
		}

		public void setDefaultChatTexts(String[] textsValue)
		{
			_defaultTexts = textsValue;
		}

		public void setDefaultRandom(boolean randValue)
		{
			_defaultRandom = randValue;
		}

		/**
		 * Sets a specific chat delay for the specified auto chat definition
		 * given by its object ID.
		 *
		 * @param int
		 *						objectId
		 * @param long
		 *						delayValue
		 */
		public void setChatDelay(int objectId, long delayValue)
		{
			AutoChatDefinition chatDef = getChatDefinition(objectId);

			if(chatDef != null)
				chatDef.setChatDelay(delayValue);
		}

		/**
		 * Sets a specific set of chat texts for the specified auto chat
		 * definition given by its object ID.
		 *
		 * @param int
		 *						objectId
		 * @param String[]
		 *						textsValue
		 */
		public void setChatTexts(int objectId, String[] textsValue)
		{
			AutoChatDefinition chatDef = getChatDefinition(objectId);

			if(chatDef != null)
				chatDef.setChatTexts(textsValue);
		}

		/**
		 * Sets specifically to use random chat order for the auto chat
		 * definition given by its object ID.
		 *
		 * @param int
		 *						objectId
		 * @param boolean
		 *						randValue
		 */
		public void setRandomChat(int objectId, boolean randValue)
		{
			AutoChatDefinition chatDef = getChatDefinition(objectId);

			if(chatDef != null)
				chatDef.setRandomChat(randValue);
		}

		/**
		 * Sets the activity of ALL auto chat definitions handled by this chat
		 * instance.
		 *
		 * @param boolean
		 *						isActive
		 */
		public void setActive(boolean activeValue)
		{
			if(_isActive == activeValue)
				return;

			_isActive = activeValue;

			if(!isGlobal())
			{
				for(AutoChatDefinition chatDefinition : _chatDefinitions.values())
					chatDefinition.setActive(activeValue);

				return;
			}

			if(isActive())
			{
				AutoChatRunner acr = new AutoChatRunner(_npcId, -1);
				_chatTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(acr, _defaultDelay, _defaultDelay);
			}
			else
				_chatTask.cancel(false);
		}

		/**
		 * Auto Chat Definition <BR>
		 * <BR>
		 * Stores information about specific chat data for an instance of the
		 * NPC ID specified by the containing auto chat instance. <BR>
		 * Each NPC instance of this type should be stored in a subsequent
		 * AutoChatDefinition class.
		 *
		 * @author Tempy
		 */
		private class AutoChatDefinition
		{
			protected int _chatIndex = 0;

			protected L2NpcInstance _npcInstance;

			protected AutoChatInstance _chatInstance;

			protected ScheduledFuture<?> _chatTask;

			private long _chatDelay = 0;

			private String[] _chatTexts = null;

			private boolean _isActive;

			private boolean _randomChat;

			protected AutoChatDefinition(AutoChatInstance chatInst, L2NpcInstance npcInst, String[] chatTexts, long chatDelay)
			{
				_npcInstance = npcInst;

				_chatInstance = chatInst;
				_randomChat = chatInst.isDefaultRandom();

				_chatDelay = chatDelay;
				_chatTexts = chatTexts;

				// If global chat isn't enabled for the parent instance,
				// then handle the chat task locally.
				if(!chatInst.isGlobal())
					setActive(true);
			}

			String[] getChatTexts()
			{
				if(_chatTexts != null)
					return _chatTexts;
				return _chatInstance.getDefaultTexts();
			}

			private long getChatDelay()
			{
				if(_chatDelay > 0)
					return _chatDelay;
				return _chatInstance.getDefaultDelay();
			}

			private boolean isActive()
			{
				return _isActive;
			}

			boolean isRandomChat()
			{
				return _randomChat;
			}

			void setRandomChat(boolean randValue)
			{
				_randomChat = randValue;
			}

			void setChatDelay(long delayValue)
			{
				_chatDelay = delayValue;
			}

			void setChatTexts(String[] textsValue)
			{
				_chatTexts = textsValue;
			}

			void setActive(boolean activeValue)
			{
				if(isActive() == activeValue)
					return;

				if(activeValue)
				{
					AutoChatRunner acr = new AutoChatRunner(_npcId, _npcInstance.getObjectId());
					_chatTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(acr, getChatDelay(), getChatDelay());
				}
				else
					_chatTask.cancel(false);

				_isActive = activeValue;
			}
		}

		/**
		 * Auto Chat Runner <BR>
		 * <BR>
		 * Represents the auto chat scheduled task for each chat instance.
		 */
		private class AutoChatRunner extends l2open.common.RunnableImpl
		{
			private int _npcId;

			private int _objectId;

			protected AutoChatRunner(int npcId, int objectId)
			{
				_npcId = npcId;
				_objectId = objectId;
			}

			public synchronized void runImpl()
			{
				AutoChatInstance chatInst = _registeredChats.get(_npcId);
				AutoChatDefinition[] chatDefinitions;

				if(chatInst.isGlobal())
					chatDefinitions = chatInst.getChatDefinitions();
				else
				{
					AutoChatDefinition chatDef = chatInst.getChatDefinition(_objectId);

					if(chatDef == null)
					{
						_log.warning("AutoChatHandler: Auto chat definition is NULL for NPC ID " + _npcId + ".");
						return;
					}

					chatDefinitions = new AutoChatDefinition[] { chatDef };
				}

				for(AutoChatDefinition chatDef : chatDefinitions)
					try
					{
						if(chatDef == null)
							continue;

						int maxIndex = chatDef.getChatTexts().length;
						int lastIndex = Rnd.get(maxIndex);

						String text;

						if(!chatDef.isRandomChat())
						{
							lastIndex = chatDef._chatIndex;
							lastIndex++;

							if(lastIndex == maxIndex)
								lastIndex = 0;

							chatDef._chatIndex = lastIndex;
						}

						text = chatDef.getChatTexts()[lastIndex];
						if(text == null || text.equals(""))
							return;

						L2NpcInstance chatNpc = chatDef._npcInstance;

						if(!chatNpc.isVisible())
							return;

						GArray<L2Player> nearbyPlayers = text.startsWith("!") ? L2World.getAroundPlayers(chatNpc) : L2World.getAroundPlayers(chatNpc, 1500, 200);

						if(nearbyPlayers == null || nearbyPlayers.isEmpty())
							return;

						L2Player randomPlayer = nearbyPlayers.get(Rnd.get(nearbyPlayers.size()));

						final int winningCabal = SevenSigns.getInstance().getCabalHighestScore();
						int losingCabal = SevenSigns.CABAL_NULL;

						if(winningCabal == SevenSigns.CABAL_DAWN)
							losingCabal = SevenSigns.CABAL_DUSK;
						else if(winningCabal == SevenSigns.CABAL_DUSK)
							losingCabal = SevenSigns.CABAL_DAWN;

						if(text.indexOf("%player_random%") > -1)
							text = text.replaceAll("%player_random%", randomPlayer.getName());

						if(text.indexOf("%player_cabal_winner%") > -1)
						{
							boolean playerFound = false;

							for(L2Player nearbyPlayer : nearbyPlayers)
							{
								if(nearbyPlayer == null)
									continue;
								if(SevenSigns.getInstance().getPlayerCabal(nearbyPlayer) == winningCabal)
								{
									text = text.replaceAll("%player_cabal_winner%", nearbyPlayer.getName());
									playerFound = true;
									break;
								}
							}

							// If a player on the winning side isn't nearby,
							// just use the randomly selected player.
							if(!playerFound)
								text = "";// text =
							// text.replaceAll("%player_cabal_winner%",
							// randomPlayer.getName());
						}

						if(text.indexOf("%player_cabal_loser%") > -1)
						{
							boolean playerFound = false;

							for(L2Player nearbyPlayer : nearbyPlayers)
							{
								if(nearbyPlayer == null)
									continue;
								if(SevenSigns.getInstance().getPlayerCabal(nearbyPlayer) == losingCabal)
								{
									text = text.replaceAll("%player_cabal_loser%", nearbyPlayer.getName());
									playerFound = true;
									break;
								}
							}

							// If a player on the winning side isn't nearby,
							// just use the randomly selected player.
							if(!playerFound)
								text = "";// text =
							// text.replaceAll("%player_cabal_loser%",
							// randomPlayer.getName());
						}

						if(text.equals(""))
							return;

						NpcSay cs = new NpcSay(chatNpc, text.startsWith("!") ? 23 : 22, text.startsWith("!") ? text.substring(1, text.length() - 1) : text);
						for(L2Player nearbyPlayer : nearbyPlayers)
						{
							if(nearbyPlayer == null)
								continue;
							nearbyPlayer.sendPacket(cs);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						return;
					}
			}
		}
	}
}