package l2open.gameserver.model.entity.residence;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.DoorTable;
import l2open.util.GArray;
import l2open.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.logging.Logger;

public abstract class Residence
{
	protected static Logger _log = Logger.getLogger(Residence.class.getName());

	protected int _id = 0;
	protected String _name = "";

	/** Clan objectId */
	protected int _ownerId = 0;
	protected int _ownDate = 0;

	protected L2Zone _zone;

	protected ResidenceType _type = ResidenceType.None;

	private GArray<L2DoorInstance> _doors = new GArray<L2DoorInstance>();
	private GArray<ResidenceFunction> _functions = new GArray<ResidenceFunction>();
	protected GArray<L2Skill> _skills = new GArray<L2Skill>();

	public Residence(int id)
	{
		_id = id;
	}

	public void init()
	{
		loadData();
		loadDoor();
		preLoadFunctions();
		loadFunctions();
		rewardSkills();
	}

	public int getId()
	{
		return _id;
	}

	public String getName()
	{
		return _name;
	}

	public int getOwnerId()
	{
		return _ownerId;
	}

	public L2Clan getOwner()
	{
		return _ownerId > 0 ? ClanTable.getInstance().getClan(_ownerId) : null;
	}

	public L2Zone getZone()
	{
		return _zone;
	}

	protected abstract void loadData();

	public abstract Siege getSiege();

	public abstract int getSiegeDayOfWeek();

	public abstract int getSiegeHourOfDay();

	/** This method sets the residence owner; null here means give it back to NPC */
	public abstract void changeOwner(L2Clan clan);

	/**
	 * Unixtime в секундах
	 */
	public void setOwnDate(int val)
	{
		_ownDate = val;
	}

	/**
	 * Unixtime в секундах
	 */
	public int getOwnDate()
	{
		return _ownDate;
	}

	public abstract void saveOwnDate();

	/**
	 * Возвращает дату последней осады на момент ее старта, unixtime в секундах
	 */
	public int getLastSiegeDate()
	{
		return 0;
	}

	/**
	 * Unixtime в секундах
	 */
	public void setLastSiegeDate(int time)
	{}

	public GArray<L2DoorInstance> getDoors()
	{
		return _doors;
	}

	public L2DoorInstance getDoor(int doorId)
	{
		if(doorId <= 0)
			return null;

		for(int i = 0; i < getDoors().size(); i++)
		{
			L2DoorInstance door = getDoors().get(i);
			if(door.getDoorId() == doorId)
				return door;
		}
		return null;
	}

	/** Return true if object is inside the zone */
	public boolean checkIfInZone(L2Object obj)
	{
		return getZone().checkIfInZone(obj);
	}

	/** Return true if object is inside the zone */
	public boolean checkIfInZone(int x, int y)
	{
		return getZone().checkIfInZone(x, y);
	}

	/** Respawn all doors on residence grounds */
	public void spawnDoor(boolean isDoorWeak)
	{
		for(int i = 0; i < getDoors().size(); i++)
		{
			L2DoorInstance door = getDoors().get(i);
			if(door.getCurrentHp() <= 0) // Сломанные восстанавливаем
			{
				door.decayMe(); // Kill current if not killed already
				door.spawnMe();
			}
			door.setCurrentHp(isDoorWeak ? door.getMaxHp() / 2 : door.getMaxHp(), true);
			door.closeMe();
		}
		loadDoorUpgrade(); // Check for any upgrade the doors may have
	}

	/** Respawn all doors */
	public void spawnDoor()
	{
		spawnDoor(false);
	}

	FastMap<Integer, Integer> doorUpgrades = new FastMap<Integer, Integer>().setShared(true);

	// This method upgrade door
	public void upgradeDoor(int doorId, int hp, boolean db)
	{
		L2DoorInstance door = getDoor(doorId);
		if(door == null)
			return;

		if(door.getDoorId() == doorId)
		{
			door.setUpgradeHp(hp);
			door.setCurrentHp(door.getMaxHp(), false);
			door.broadcastStatusUpdate();

			if(db)
				saveDoorUpgrade(doorId, hp);
			doorUpgrades.put(doorId, hp);
		}
	}

	public Integer getDoorUpgrade(int doorId)
	{
		if(doorUpgrades.get(doorId) != null)
			return doorUpgrades.get(doorId);
		return 0;
	}

	// This method is used to begin removing all residence upgrades
	public void removeUpgrade()
	{
		removeDoorUpgrade();
	}

	/** Move non clan members off siegeUnit area and to nearest town. */
	public void banishForeigner(L2Player activeChar)
	{
		// Get all players
		for(L2Player player : L2World.getAroundPlayers(activeChar))
		{
			// Skip if player is in clan
			if(player.getClanId() == getOwnerId())
				continue;

			if(checkIfInZone(player))
				player.teleToClosestTown();
		}
	}

	public void closeDoor(L2Player activeChar, int doorId)
	{
		openCloseDoor(activeChar, doorId, false);
	}

	public void openDoor(L2Player activeChar, int doorId)
	{
		openCloseDoor(activeChar, doorId, true);
	}

	public void openCloseDoor(L2Player activeChar, int doorId, boolean open)
	{
		if((activeChar.getClanId() != getOwnerId() || getType() != ResidenceType.Castle && getSiege() != null && getSiege().isInProgress()) && !activeChar.isGM())
			return;

		L2DoorInstance door = getDoor(doorId);
		if(door != null)
			if(open)
				door.openMe();
			else
				door.closeMe();
	}

	public void openCloseDoors(L2Player activeChar, boolean open)
	{
		if((activeChar.getClanId() != getOwnerId() || getType() != ResidenceType.Castle && getSiege() != null && getSiege().isInProgress()) && !activeChar.isGM())
			return;

		for(L2DoorInstance door : getDoors())
			if(door != null)
				if(open)
					door.openMe();
				else
					door.closeMe();
	}

	// This method loads door data from database
	private void loadDoor()
	{
		int id = 0;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT id FROM siege_door WHERE unitId = ?");
			statement.setInt(1, getId());
			rset = statement.executeQuery();

			while(rset.next())
			{
				id = rset.getInt("id");
				L2DoorInstance door = DoorTable.getInstance().getDoor(rset.getInt("id"));
				door.setSiegeUnit(this);
				door.decayMe();
				door.spawnMe();
				_doors.add(door);
			}
		}
		catch(Exception e)
		{
			_log.warning("Exception: loadDoor(), id = " + id + ", error: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	// This method loads door upgrade data from database
	private void loadDoorUpgrade()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM siege_doorupgrade WHERE doorId IN (SELECT id FROM siege_door WHERE unitId = ?)");
			statement.setInt(1, getId());
			rset = statement.executeQuery();

			while(rset.next())
				upgradeDoor(rset.getInt("doorId"), rset.getInt("hp"), false);
		}
		catch(Exception e)
		{
			_log.warning("Exception: loadDoorUpgrade(): " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	private void removeDoorUpgrade()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM siege_doorupgrade WHERE doorId IN (SELECT id FROM siege_door WHERE unitId=?)");
			statement.setInt(1, getId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("Exception: removeDoorUpgrade(): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	private void saveDoorUpgrade(int doorId, int hp)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO siege_doorupgrade (doorId, hp) VALUES (?,?)");
			statement.setInt(1, doorId);
			statement.setInt(2, hp);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("Exception: saveDoorUpgrade(int doorId, int hp): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Выдает клану-владельцу скилы резиденции
	 */
	protected void rewardSkills()
	{
		if(getOwner() != null)
		{
			for(L2Skill skill : _skills)
				getOwner().addNewSkill(skill, false);
			if(getType() == ResidenceType.Castle)
				for(L2Skill skill : ((Castle) this).getTerritorySkills())
					getOwner().addNewSkill(skill, false);
		}
	}

	/**
	 * Удаляет у клана-владельца скилы резиденции
	 */
	protected void removeSkills()
	{
		if(getOwner() != null)
		{
			for(L2Skill skill : _skills)
				getOwner().removeSkill(skill);
			if(getType() == ResidenceType.Castle)
				for(L2Skill skill : ((Castle) this).getTerritorySkills())
					getOwner().removeSkill(skill);

			getOwner().boarcastSkillListToOnlineMembers();
		}
	}

	public ResidenceType getType()
	{
		return _type;
	}

	private int getNodeValue(Node node, String key)
	{
		if(node.getAttributes() == null)
			return 0;
		Node keyNode = node.getAttributes().getNamedItem(key);
		return keyNode == null ? 0 : Integer.parseInt(keyNode.getNodeValue());
	}

	private ResidenceFunction checkAndGetFunction(int type)
	{
		ResidenceFunction function = getFunction(type);
		if(function == null)
		{
			function = new ResidenceFunction(getId(), type);
			_functions.add(function);
		}
		return function;
	}

	private void preLoadFunctions()
	{
		try
		{
			File file;
			if (ConfigValue.develop) {
				file = new File("data/xml/residence.xml");
			} else {
				file = new File(ConfigValue.DatapackRoot + "/data/xml/residence.xml");
			}


			DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
			factory1.setValidating(false);
			factory1.setIgnoringComments(true);
			Document doc1 = factory1.newDocumentBuilder().parse(file);

			for(Node n1 = doc1.getFirstChild(); n1 != null; n1 = n1.getNextSibling())
				if("list".equalsIgnoreCase(n1.getNodeName()))
					for(Node d1 = n1.getFirstChild(); d1 != null; d1 = d1.getNextSibling())
						if("residence".equalsIgnoreCase(d1.getNodeName()) && Integer.parseInt(d1.getAttributes().getNamedItem("id").getNodeValue()) == getId())
							for(Node s1 = d1.getFirstChild(); s1 != null; s1 = s1.getNextSibling())
							{
								int level = getNodeValue(s1, "level");
								int lease = (int) (getNodeValue(s1, "lease") * ConfigValue.ResidenceLeaseFuncMultiplier);
								int npcId = getNodeValue(s1, "npcId");
								int listId = getNodeValue(s1, "listId");
								ResidenceFunction function = null;
								if("teleport".equalsIgnoreCase(s1.getNodeName()))
								{
									function = checkAndGetFunction(ResidenceFunction.TELEPORT);
									function.addTeleports(level, parseTeleport(s1));
								}
								else if("support".equalsIgnoreCase(s1.getNodeName()))
								{
									if(level > 9/* && !ConfigValue.AltChAllowHourBuff*/)
										continue;
									function = checkAndGetFunction(ResidenceFunction.SUPPORT);
									function.addBuffs(level);
								}
								else if("item_create".equalsIgnoreCase(s1.getNodeName()))
								{
									function = checkAndGetFunction(ResidenceFunction.ITEM_CREATE);
									function.addBuylist(level, new int[] { npcId, listId });
								}
								else if("curtain".equalsIgnoreCase(s1.getNodeName()))
									function = checkAndGetFunction(ResidenceFunction.CURTAIN);
								else if("platform".equalsIgnoreCase(s1.getNodeName()))
									function = checkAndGetFunction(ResidenceFunction.PLATFORM);
								else if("restore_exp".equalsIgnoreCase(s1.getNodeName()))
									function = checkAndGetFunction(ResidenceFunction.RESTORE_EXP);
								else if("restore_hp".equalsIgnoreCase(s1.getNodeName()))
									function = checkAndGetFunction(ResidenceFunction.RESTORE_HP);
								else if("restore_mp".equalsIgnoreCase(s1.getNodeName()))
									function = checkAndGetFunction(ResidenceFunction.RESTORE_MP);
								if(function != null)
									function.addLease(level, lease);
							}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private TeleportLocation[] parseTeleport(Node s1)
	{
		GArray<TeleportLocation> targets = new GArray<TeleportLocation>();
		for(Node t1 = s1.getFirstChild(); t1 != null; t1 = t1.getNextSibling())
			if("target".equalsIgnoreCase(t1.getNodeName()))
			{
				String target = t1.getAttributes().getNamedItem("loc").getNodeValue();
				String[] names = t1.getAttributes().getNamedItem("name").getNodeValue().split(";");
				long price = Long.parseLong(t1.getAttributes().getNamedItem("price").getNodeValue());
				int item = t1.getAttributes().getNamedItem("item") == null ? 57 : Integer.parseInt(t1.getAttributes().getNamedItem("item").getNodeValue());
				TeleportLocation t = new TeleportLocation(target, item, price, names);
				targets.add(t);
			}
		return targets.toArray(new TeleportLocation[targets.size()]);
	}

	private void loadFunctions()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("SELECT * FROM residence_functions WHERE id = ?");
			statement.setInt(1, getId());
			rs = statement.executeQuery();

			while(rs.next())
			{
				ResidenceFunction function = getFunction(rs.getInt("type"));
				synchronized(function)
				{
					function.setLvl(rs.getInt("lvl"));
					function.setEndTimeInMillis(rs.getInt("endTime") * 1000L);
					function.setInDebt(rs.getBoolean("inDebt"));
					function.setActive(true);
				}
				StartAutoTaskForFunction(function);
			}
		}
		catch(Exception e)
		{
			_log.warning("Exception: SiegeUnit.loadFunctions(): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}

	public boolean isFunctionActive(int type)
	{
		ResidenceFunction function = getFunction(type);
		if(function != null && function.isActive() && function.getLevel() > 0)
			return true;
		return false;
	}

	public ResidenceFunction getFunction(int type)
	{
		for(int i = 0; i < _functions.size(); i++)
			if(_functions.get(i).getType() == type)
				return _functions.get(i);
		return null;
	}

	public boolean updateFunctions(int type, int level)
	{
		L2Clan clan = getOwner();
		if(clan == null)
			return false;

		long count = clan.getAdenaCount();

		ResidenceFunction function = getFunction(type);
		if(function == null)
			return false;

		if(function.isActive() && function.getLevel() == level)
			return true;

		int lease = level == 0 ? 0 : getFunction(type).getLease(level);

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			if(!function.isActive())
			{
				if(count >= lease)
					clan.getWarehouse().destroyItem(57, lease);
				else
					return false;

				long time = Calendar.getInstance().getTimeInMillis() + 86400000;

				statement = con.prepareStatement("REPLACE residence_functions SET id=?, type=?, lvl=?, endTime=?");
				statement.setInt(1, getId());
				statement.setInt(2, type);
				statement.setInt(3, level);
				statement.setInt(4, (int) (time / 1000));
				statement.execute();

				function.setLvl(level);
				function.setEndTimeInMillis(time);
				function.setActive(true);
				StartAutoTaskForFunction(function);
			}
			else
			{
				if(count >= lease - getFunction(type).getLease())
				{
					if(lease > getFunction(type).getLease())
						clan.getWarehouse().destroyItem(57, lease - getFunction(type).getLease());
				}
				else
					return false;

				statement = con.prepareStatement("REPLACE residence_functions SET id=?, type=?, lvl=?");
				statement.setInt(1, getId());
				statement.setInt(2, type);
				statement.setInt(3, level);
				statement.execute();

				function.setLvl(level);
			}
		}
		catch(Exception e)
		{
			_log.warning("Exception: SiegeUnit.updateFunctions(int type, int lvl, int lease, long rate, long time, boolean addNew): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
		return true;
	}

	public void removeFunction(int type)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM residence_functions WHERE id=? AND type=?");
			statement.setInt(1, getId());
			statement.setInt(2, type);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("Exception: removeFunctions(int type): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	private void StartAutoTaskForFunction(ResidenceFunction function)
	{
		if(getOwnerId() != 0)
			try
			{
				L2Clan clan = getOwner();
				if(clan == null)
				{
					_log.warning("SiegeUnit[485]: clan == null for residence id: " + getId());
					return;
				}

				if(function.getEndTimeInMillis() > System.currentTimeMillis())
					ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
				else if(function.isInDebt() && clan.getAdenaCount() >= function.getLease()) // здесь возможно нужно добавит еще одну проверку && function.getLease() != 0
				{
					clan.getWarehouse().destroyItem(57, function.getLease());
					function.updateRentTime(false);
					ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
					Log.add("deducted " + function.getLease() + " adena from " + getName() + " owner's residence for function type " + function.getType(), "residence");
				}
				else if(!function.isInDebt())
				{
					function.setInDebt(true);
					function.updateRentTime(true);
					ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
				}
				else
				{
					function.setLvl(0);
					function.setActive(false);
					removeFunction(function.getType());
					Log.add("deactivate function type " + function.getType() + ", for " + getName() + " residence, because clan don't have enough money", "residence");
				}
			}
			catch(Exception e)
			{
				_log.info("StartAutoTaskForFunction: id: " + getId() + ", type: " + function.getType());
				e.printStackTrace();
			}
	}

	private class AutoTaskForFunctions extends l2open.common.RunnableImpl
	{
		ResidenceFunction _function;

		public AutoTaskForFunctions(ResidenceFunction function)
		{
			_function = function;
		}

		public void runImpl()
		{
			StartAutoTaskForFunction(_function);
		}
	}
}