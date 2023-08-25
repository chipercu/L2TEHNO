package l2open.gameserver.instancemanager;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.CursedWeapon;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Location;
import l2open.util.Rnd;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class CursedWeaponsManager
{
	private static final Logger _log = Logger.getLogger(CursedWeaponsManager.class.getName());

	private static CursedWeaponsManager _instance;

	public static CursedWeaponsManager getInstance()
	{
		if(_instance == null)
			_instance = new CursedWeaponsManager();
		return _instance;
	}

	Map<Integer, CursedWeapon> _cursedWeapons;
	private ScheduledFuture<?> _removeTask;

	private static final int CURSEDWEAPONS_MAINTENANCE_INTERVAL = 5 * 60 * 1000; // 5 min in millisec

	public CursedWeaponsManager()
	{
		_cursedWeapons = new FastMap<Integer, CursedWeapon>().setShared(true);

		if(!ConfigValue.AllowCursedWeapons)
			return;

		load();
		restore();
		checkConditions();

		cancelTask();
		_removeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RemoveTask(), CURSEDWEAPONS_MAINTENANCE_INTERVAL, CURSEDWEAPONS_MAINTENANCE_INTERVAL);

		_log.info("CursedWeaponsManager: Loaded " + _cursedWeapons.size() + " cursed weapon(s).");
	}

	public final void reload()
	{
		_instance = new CursedWeaponsManager();
	}

	private void load()
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);

			File file = new File(ConfigValue.DatapackRoot + "/data/xml/cursed_weapons.xml");
			if(!file.exists())
				return;

			Document doc = factory.newDocumentBuilder().parse(file);

			for(Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
				if("list".equalsIgnoreCase(n.getNodeName()))
					for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
						if("item".equalsIgnoreCase(d.getNodeName()))
						{
							NamedNodeMap attrs = d.getAttributes();
							int id = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
							Integer skillId = Integer.parseInt(attrs.getNamedItem("skillId").getNodeValue());
							String name = "Unknown cursed weapon";
							if(attrs.getNamedItem("name") != null)
								name = attrs.getNamedItem("name").getNodeValue();
							else if(ItemTemplates.getInstance().getTemplate(id) != null)
								name = ItemTemplates.getInstance().getTemplate(id).getName();

							if(id == 0)
								continue;

							CursedWeapon cw = new CursedWeapon(id, skillId, name);
							for(Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
								if("dropRate".equalsIgnoreCase(cd.getNodeName()))
									cw.setDropRate(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
								else if("duration".equalsIgnoreCase(cd.getNodeName()))
								{
									attrs = cd.getAttributes();
									cw.setDurationMin(Integer.parseInt(attrs.getNamedItem("min").getNodeValue()));
									cw.setDurationMax(Integer.parseInt(attrs.getNamedItem("max").getNodeValue()));
								}
								else if("durationLost".equalsIgnoreCase(cd.getNodeName()))
									cw.setDurationLost(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
								else if("disapearChance".equalsIgnoreCase(cd.getNodeName()))
									cw.setDisapearChance(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
								else if("stageKills".equalsIgnoreCase(cd.getNodeName()))
									cw.setStageKills(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
								else if("transformationId".equalsIgnoreCase(cd.getNodeName()))
									cw.setTransformationId(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
								else if("transformationTemplateId".equalsIgnoreCase(cd.getNodeName()))
									cw.setTransformationTemplateId(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
								else if("transformationName".equalsIgnoreCase(cd.getNodeName()))
									cw.setTransformationName(cd.getAttributes().getNamedItem("val").getNodeValue());

							// Store cursed weapon
							_cursedWeapons.put(id, cw);
						}
		}
		catch(Exception e)
		{
			_log.severe("CursedWeaponsManager: Error parsing cursed_weapons file. " + e);
		}
	}

	private void restore()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("SELECT * FROM cursed_weapons");
			rset = statement.executeQuery();

			while(rset.next())
			{
				int itemId = rset.getInt("item_id");
				CursedWeapon cw = _cursedWeapons.get(itemId);
				if(cw != null)
				{
					cw.setPlayerId(rset.getInt("player_id"));
					cw.setPlayerKarma(rset.getInt("player_karma"));
					cw.setPlayerPkKills(rset.getInt("player_pkkills"));
					cw.setNbKills(rset.getInt("nb_kills"));
					cw.setLoc(new Location(rset.getInt("x"), rset.getInt("y"), rset.getInt("z")));
					cw.setEndTime(rset.getLong("end_time") * 1000L);

					if(!cw.reActivate())
						endOfLife(cw);
				}
				else
				{
					removeFromDb(itemId);
					_log.warning("CursedWeaponsManager: Unknown cursed weapon " + itemId + ", deleted");
				}
			}
		}
		catch(Exception e)
		{
			_log.warning("CursedWeaponsManager: Could not restore cursed_weapons data: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	private void checkConditions()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement1 = null, statement2 = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement1 = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=?");
			statement2 = con.prepareStatement("SELECT owner_id FROM items WHERE item_id=?");

			for(CursedWeapon cw : _cursedWeapons.values())
			{
				// Do an item check to be sure that the cursed weapon and/or skill isn't hold by someone
				int itemId = cw.getItemId();
				int skillId = cw.getSkillId();
				boolean foundedInItems = false;

				// Delete all cursed weapons skills (we don`t care about same skill on multiply weapons, when player back, skill will appears again)
				statement1.setInt(1, skillId);
				statement1.executeUpdate();

				statement2.setInt(1, itemId);
				rset = statement2.executeQuery();

				while(rset.next())
				{
					// A player has the cursed weapon in his inventory ...
					int playerId = rset.getInt("owner_id");

					if(!foundedInItems)
					{
						if(playerId != cw.getPlayerId() || cw.getPlayerId() == 0)
						{
							emptyPlayerCursedWeapon(playerId, itemId, cw);
							_log.info("CursedWeaponsManager[254]: Player " + playerId + " owns the cursed weapon " + itemId + " but he shouldn't.");
						}
						else
							foundedInItems = true;
					}
					else
					{
						emptyPlayerCursedWeapon(playerId, itemId, cw);
						_log.info("CursedWeaponsManager[262]: Player " + playerId + " owns the cursed weapon " + itemId + " but he shouldn't.");
					}
				}

				if(!foundedInItems && cw.getPlayerId() != 0)
				{
					removeFromDb(cw.getItemId());

					_log.info("CursedWeaponsManager: Unownered weapon, removing from table...");
				}
			}
		}
		catch(Exception e)
		{
			_log.warning("CursedWeaponsManager: Could not check cursed_weapons data: " + e);
			return;
		}
		finally
		{
			DatabaseUtils.closeStatement(statement1);
			DatabaseUtils.closeDatabaseCSR(con, statement2, rset);
		}
	}

	private void emptyPlayerCursedWeapon(int playerId, int itemId, CursedWeapon cw)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			// Delete the item
			statement = con.prepareStatement("DELETE FROM items WHERE owner_id=? AND item_id=?");
			statement.setInt(1, playerId);
			statement.setInt(2, itemId);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("UPDATE characters SET karma=?, pkkills=? WHERE obj_id=?");
			statement.setInt(1, cw.getPlayerKarma());
			statement.setInt(2, cw.getPlayerPkKills());
			statement.setInt(3, playerId);
			if(statement.executeUpdate() != 1)
				_log.warning("Error while updating karma & pkkills for userId " + cw.getPlayerId());
			// clean up the cursedweapons table.
			removeFromDb(itemId);
		}
		catch(SQLException e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void removeFromDb(int itemId)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("DELETE FROM cursed_weapons WHERE item_id = ?");
			statement.setInt(1, itemId);
			statement.executeUpdate();

			if(getCursedWeapon(itemId) != null)
				getCursedWeapon(itemId).initWeapon();
		}
		catch(SQLException e)
		{
			_log.severe("CursedWeaponsManager: Failed to remove data: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	private void cancelTask()
	{
		if(_removeTask != null)
		{
			_removeTask.cancel(true);
			_removeTask = null;
		}
	}

	private class RemoveTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			for(CursedWeapon cw : _cursedWeapons.values())
				if(cw.isActive() && cw.getTimeLeft() <= 0)
					endOfLife(cw);
		}
	}

	public void endOfLife(CursedWeapon cw)
	{
		if(cw.isActivated())
		{
			L2Player player = cw.getOnlineOwner();
			if(player != null)
			{
				// Remove from player
				_log.info("CursedWeaponsManager: " + cw.getName() + " being removed online from " + player + ".");

				player.abortAttack(true, true);

				player.setKarma(cw.getPlayerKarma());
				player.setPkKills(cw.getPlayerPkKills());
				player.setCursedWeaponEquippedId(0);
				player.setTransformation(0);
				player.setTransformationName(null);
				player.removeSkill(SkillTable.getInstance().getInfo(cw.getSkillId(), player.getSkillLevel(cw.getSkillId())), false, true);

				// Remove
				player.getInventory().unEquipItemInBodySlot(L2Item.SLOT_LR_HAND, null);
				PlayerData.getInstance().store(player, false);

				// Destroy
				if(player.getInventory().destroyItemByItemId(cw.getItemId(), 1, true) == null)
					_log.info("CursedWeaponsManager[395]: Error! Cursed weapon not found!!!");

				player.broadcastUserInfo(true);
			}
			else
			{
				// Remove from Db
				_log.info("CursedWeaponsManager: " + cw.getName() + " being removed offline.");

				ThreadConnection con = null;
				FiltredPreparedStatement statement = null;
				try
				{
					con = L2DatabaseFactory.getInstance().getConnection();

					// Delete the item
					statement = con.prepareStatement("DELETE FROM items WHERE owner_id=? AND item_id=?");
					statement.setInt(1, cw.getPlayerId());
					statement.setInt(2, cw.getItemId());
					statement.executeUpdate();
					DatabaseUtils.closeStatement(statement);

					// Delete the skill
					statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND skill_id=?");
					statement.setInt(1, cw.getPlayerId());
					statement.setInt(2, cw.getSkillId());
					statement.executeUpdate();
					DatabaseUtils.closeStatement(statement);

					// Restore the karma
					statement = con.prepareStatement("UPDATE characters SET karma=?, pkkills=? WHERE obj_Id=?");
					statement.setInt(1, cw.getPlayerKarma());
					statement.setInt(2, cw.getPlayerPkKills());
					statement.setInt(3, cw.getPlayerId());
					statement.executeUpdate();
				}
				catch(SQLException e)
				{
					_log.warning("CursedWeaponsManager: Could not delete : " + e);
				}
				finally
				{
					DatabaseUtils.closeDatabaseCS(con, statement);
				}
			}
		}
		else // either this cursed weapon is in the inventory of someone who has another cursed weapon equipped,
		// OR this cursed weapon is on the ground.
		if(cw.getPlayer() != null && cw.getPlayer().getInventory().getItemByItemId(cw.getItemId()) != null)
		{
			L2Player player = cw.getPlayer();
			if(cw.getPlayer().getInventory().destroyItemByItemId(cw.getItemId(), 1, true) == null)
				_log.info("CursedWeaponsManager[453]: Error! Cursed weapon not found!!!");

			player.sendChanges();
			player.broadcastUserInfo(true);
		}
		// is dropped on the ground
		else if(cw.getItem() != null)
		{
			PlayerData.getInstance().removeFromDb(cw.getItem(), true);
			cw.getItem().deleteMe();
			_log.info("CursedWeaponsManager: " + cw.getName() + " item has been removed from World.");
		}

		cw.initWeapon();
		removeFromDb(cw.getItemId());

		announce(new SystemMessage(SystemMessage.S1_HAS_DISAPPEARED_CW).addString(cw.getName()));
	}

	public void saveData(CursedWeapon cw)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		synchronized (cw)
		{
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();

				// Delete previous datas
				statement = con.prepareStatement("DELETE FROM cursed_weapons WHERE item_id = ?");
				statement.setInt(1, cw.getItemId());
				statement.executeUpdate();
				DatabaseUtils.closeStatement(statement);
				statement = null;

				if(cw.isActive())
				{
					statement = con.prepareStatement("REPLACE INTO cursed_weapons (item_id, player_id, player_karma, player_pkkills, nb_kills, x, y, z, end_time) VALUES (?,?,?,?,?,?,?,?,?)");
					statement.setInt(1, cw.getItemId());
					statement.setInt(2, cw.getPlayerId());
					statement.setInt(3, cw.getPlayerKarma());
					statement.setInt(4, cw.getPlayerPkKills());
					statement.setInt(5, cw.getNbKills());
					statement.setInt(6, cw.getLoc().x);
					statement.setInt(7, cw.getLoc().y);
					statement.setInt(8, cw.getLoc().z);
					statement.setLong(9, cw.getEndTime() / 1000);
					statement.executeUpdate();
				}
			}
			catch(SQLException e)
			{
				_log.severe("CursedWeapon: Failed to save data: " + e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}
		}
	}

	public void saveData()
	{
		for(CursedWeapon cw : _cursedWeapons.values())
			saveData(cw);
	}

	/**
	 * вызывается, когда проклятое оружие оказывается в инвентаре игрока
	 */
	public void checkPlayer(L2Player player, L2ItemInstance item)
	{
		if(player == null || item == null || player.isInOlympiadMode())
			return;

		CursedWeapon cw = _cursedWeapons.get(item.getItemId());
		if(cw == null)
			return;

		if(player.getObjectId() == cw.getPlayerId() || cw.getPlayerId() == 0 || cw.isDropped())
		{
			activate(player, item);
			showUsageTime(player, cw);
		}
		else
		{
			// wtf? how you get it?
			_log.warning("CursedWeaponsManager: " + player + " tried to obtain " + item + " in wrong way");
			player.getInventory().destroyItem(item, item.getCount(), true);
		}
	}

	public void activate(L2Player player, L2ItemInstance item)
	{
		if(player == null || player.isInOlympiadMode())
			return;
		CursedWeapon cw = _cursedWeapons.get(item.getItemId());
		if(cw == null)
			return;

		if(player.isCursedWeaponEquipped()) // cannot own 2 cursed swords
		{
			if(player.getCursedWeaponEquippedId() != item.getItemId())
			{
				CursedWeapon cw2 = _cursedWeapons.get(player.getCursedWeaponEquippedId());
				cw2.setNbKills(cw2.getStageKills() - 1);
				cw2.increaseKills();
			}

			// erase the newly obtained cursed weapon
			endOfLife(cw);
			player.getInventory().destroyItem(item, 1, true);
		}
		else if(cw.getTimeLeft() > 0)
		{
			cw.activate(player, item);
			saveData(cw);
			announce(new SystemMessage(SystemMessage.THE_OWNER_OF_S2_HAS_APPEARED_IN_THE_S1_REGION).addZoneName(player.getLoc()).addString(cw.getName()));
		}
		else
		{
			endOfLife(cw);
			player.getInventory().destroyItem(item, 1, true);
		}
	}

	public void doLogout(L2Player player)
	{
		for(CursedWeapon cw : _cursedWeapons.values())
			if(player.getInventory().getItemByItemId(cw.getItemId()) != null)
				cw.setItem(null);
	}

	/**
	 * drop from L2NpcInstance killed by L2Player
	 */
	public void dropAttackable(L2NpcInstance attackable, L2Player killer)
	{
		if(killer.isInOlympiadMode() || killer.isCursedWeaponEquipped() || _cursedWeapons.isEmpty())
			return;

		synchronized (_cursedWeapons)
		{
			int num = 0;
			short count = 0;
			byte breakFlag = 0;

			while(breakFlag == 0)
			{
				num = _cursedWeapons.keySet().toArray(new Integer[_cursedWeapons.size()])[Rnd.get(_cursedWeapons.size())];
				count++;

				if(_cursedWeapons.get(num) != null && !_cursedWeapons.get(num).isActive())
					breakFlag = 1;
				else if(count >= getCursedWeapons().size())
					breakFlag = 2;
			}

			if(breakFlag == 1)
				_cursedWeapons.get(num).create(attackable, killer, false);
		}
	}

	/**
	 * Выпадение оружия из владельца, или исчезновение с определенной вероятностью.
	 * Вызывается при смерти игрока.
	 */
	public void dropPlayer(L2Player player)
	{
		CursedWeapon cw = _cursedWeapons.get(player.getCursedWeaponEquippedId());
		if(cw == null)
			return;

		if(cw.dropIt(null, null, player))
		{
			saveData(cw);
			announce(new SystemMessage(SystemMessage.S2_WAS_DROPPED_IN_THE_S1_REGION).addZoneName(player.getLoc()).addString(cw.getName()));
		}
		else
			endOfLife(cw);
	}

	public void increaseKills(int itemId)
	{
		CursedWeapon cw = _cursedWeapons.get(itemId);
		if(cw != null)
		{
			cw.increaseKills();
			saveData(cw);
		}
	}

	public int getLevel(int itemId)
	{
		CursedWeapon cw = _cursedWeapons.get(itemId);
		return cw != null ? cw.getLevel() : 0;
	}

	public void announce(SystemMessage sm)
	{
		for(L2Player player : L2ObjectsStorage.getPlayers())
			player.sendPacket(sm);
	}

	public void showUsageTime(L2Player player, short itemId)
	{
		CursedWeapon cw = _cursedWeapons.get(itemId);
		if(cw != null)
			showUsageTime(player, cw);
	}

	public void showUsageTime(L2Player player, CursedWeapon cw)
	{
		SystemMessage sm = new SystemMessage(SystemMessage.S2_MINUTE_OF_USAGE_TIME_ARE_LEFT_FOR_S1);
		sm.addString(cw.getName());
		sm.addNumber(new Long(cw.getTimeLeft() / 60000).intValue());
		player.sendPacket(sm);
	}

	public boolean isCursed(int itemId)
	{
		return _cursedWeapons.containsKey(itemId);
	}

	public Collection<CursedWeapon> getCursedWeapons()
	{
		return _cursedWeapons.values();
	}

	public Set<Integer> getCursedWeaponsIds()
	{
		return _cursedWeapons.keySet();
	}

	public CursedWeapon getCursedWeapon(int itemId)
	{
		return _cursedWeapons.get(itemId);
	}
}