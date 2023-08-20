package l2open.gameserver.model;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.BookMark;
import l2open.util.GArray;
import l2open.util.Location;

import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookMarkList
{
	private GArray<BookMark> elementData;
	static final Logger _log = Logger.getLogger(BookMarkList.class.getName());
	private L2Player ownerId;
	private int capacity;

	private static enum ForbiddenZones
	{
		Castle(ZoneType.Castle, false),
		Fortress(ZoneType.Fortress, false),
		ClanHall(ZoneType.ClanHall, false),
		ssq_zone(ZoneType.ssq_zone, false),
		OlympiadStadia(ZoneType.OlympiadStadia, false),
		battle_zone(ZoneType.battle_zone, false),
		Siege(ZoneType.Siege, false),
		no_restart(ZoneType.no_restart, true),
		no_summon(ZoneType.no_summon, false);

		public ZoneType type;
		public boolean checkZ;

		private ForbiddenZones(ZoneType type, boolean checkZ)
		{
			this.type = type;
			this.checkZ = checkZ;
		}
	}

	public BookMarkList(L2Player owner, int acapacity)
	{
		ownerId = owner;
		elementData = new GArray<BookMark>(acapacity);
		capacity = acapacity;
	}

	public L2Player getOwner()
	{
		return ownerId;
	}

	public synchronized void setCapacity(int val)
	{
		if(val<=ConfigValue.MyTeleportsMaxSlot)
		{
			capacity = val;
			getOwner().sendPacket(Msg.THE_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_BEEN_INCREASED);
		}
		else
		{
			capacity = ConfigValue.MyTeleportsMaxSlot;
			getOwner().sendPacket(Msg.THE_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_BEEN_INCREASED);
		}
	}

	public int getCapacity()
	{
		return capacity;
	}

	public void clear()
	{
		elementData.clear();
	}

	public BookMark[] toArray()
	{
		return elementData.toArray(new BookMark[elementData.size()]);
	}

	public int incCapacity()
	{
		L2Player owner = getOwner();
		if(owner == null)
			return -1;
		//увеличивать можно только до 9 ячеек по оффу...
		if(capacity+1<=ConfigValue.MyTeleportsMaxSlot)
		{
			capacity++;
			owner.sendPacket(Msg.THE_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_BEEN_INCREASED);
		}
		return getCapacity();
	}

	public synchronized boolean add(BookMark e)
	{
		if(elementData.size() + 1 > getCapacity())
			return false;
		return elementData.add(e);
	}

	public BookMark get(int slot)
	{
		return elementData.get(slot - 1);
	}

	public void remove(int slot)
	{
		elementData.remove(slot - 1);
	}

	public boolean tryTeleport(int slot)
	{
		L2Player owner = getOwner();
		if(!checkFirstConditions(owner) || !checkTeleportConditions(owner))
			return false;

		BookMark bookmark = elementData.get(slot - 1);
		if(!checkTeleportLocation(owner, bookmark.x, bookmark.y, bookmark.z))
			return false;

		//TODO YOU_CANNOT_USE_MY_TELEPORTS_IN_THIS_AREA // Вы находитесь в локации, на которой возврат к флагу недоступен.

		if(Functions.removeItem(owner, 13302, 1) != 1)
			if(Functions.removeItem(owner, 13016, 1) != 1)
				if(Functions.removeItem(owner, 20025, 1) != 1)
				{
					owner.sendPacket(Msg.YOU_CANNOT_TELEPORT_BECAUSE_YOU_DO_NOT_HAVE_A_TELEPORT_ITEM);
					return false;
				}

		owner.teleToLocation(bookmark.x, bookmark.y, bookmark.z);
		return true;
	}

	public boolean add(String aname, String aacronym, int aiconId)
	{
		return add(aname, aacronym, aiconId, true);
	}

	public boolean add(String aname, String aacronym, int aiconId, boolean takeFlag)
	{
		L2Player owner = getOwner();
		return owner != null ? add(owner.getLoc(), aname, aacronym, aiconId, takeFlag) : false;
	}

	public boolean add(Location loc, String aname, String aacronym, int aiconId, boolean takeFlag)
	{
		L2Player owner = getOwner();
		if(!checkFirstConditions(owner) || !checkTeleportLocation(owner, loc))
			return false;

		if(elementData.size() >= getCapacity())
		{
			owner.sendPacket(Msg.YOU_HAVE_NO_SPACE_TO_SAVE_THE_TELEPORT_LOCATION);
			return false;
		}

		if(takeFlag)
			if(Functions.removeItem(owner, 20033, 1) != 1)
			{
				owner.sendPacket(Msg.YOU_CANNOT_BOOKMARK_THIS_LOCATION_BECAUSE_YOU_DO_NOT_HAVE_A_MY_TELEPORT_FLAG);
				return false;
			}

		add(new BookMark(loc, aiconId, aname, aacronym));

		return true;
	}

	public void store()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		int charObjId = ownerId.getObjectId();
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM `character_bookmarks` WHERE char_Id=?");
			statement.setInt(1, charObjId);
			statement.execute();

			DatabaseUtils.closeStatement(statement);
			statement = con.prepareStatement("INSERT INTO `character_bookmarks` VALUES(?,?,?,?,?,?,?,?);");
			int slotId = 0;
			for(BookMark bookmark : elementData)
			{
				statement.setInt(1, charObjId);
				statement.setInt(2, ++slotId);
				statement.setString(3, bookmark.getName());
				statement.setString(4, bookmark.getAcronym());
				statement.setInt(5, bookmark.getIcon());
				statement.setInt(6, bookmark.x);
				statement.setInt(7, bookmark.y);
				statement.setInt(8, bookmark.z);
				statement.execute();
			}
		}
		catch(Exception e)
		{
			_log.warning("store: could not store char[" + charObjId + "] bookmarks: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void restore()
	{
		if(getCapacity() == 0)
		{
			synchronized (this)
			{
				elementData.clear();
			}
			return;
		}

		ThreadConnection con = null;
		FiltredStatement statement = null;
		ResultSet rs = null;
		int charObjId = ownerId.getObjectId();
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			rs = statement.executeQuery("SELECT * FROM `character_bookmarks` WHERE `char_Id`=" + charObjId + " ORDER BY `idx` LIMIT " + getCapacity());
			synchronized (this)
			{
				elementData.clear();
				while(rs.next())
					add(new BookMark(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"), rs.getInt("icon"), rs.getString("name"), rs.getString("acronym")));
			}
		}
		catch(final Exception e)
		{
			_log.log(Level.WARNING, "restore: could not restore char[" + charObjId + "] bookmarks: ", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}

	public static boolean checkFirstConditions(L2Player player)
	{
		if(player == null)
			return false;

		if(player.isCombatFlagEquipped())
		{
			player.sendPacket(Msg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
			return false;
		}
		if(player.isTerritoryFlagEquipped())
		{
			player.sendPacket(Msg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
			return false;
		}
		if(player.getReflection().getId() != 0)
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_IN_AN_INSTANT_ZONE);
			return false;
		}
		if(player.isInDuel())
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL);
			return false;
		}
		if(player.isInCombat() || player.getPvpFlag() != 0)
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE);
			return false;
		}
		if(player.isInOlympiadMode() || player.isInZoneOlympiad())
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH);
			return false;
		}
		if(player.isOnSiegeField() || player.isInZoneBattle() || player.isInZone(ZoneType.Siege))
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGE_SCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE);
			return false;
		}
		if(player.isFlying()) //TODO AirShips
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING);
			return false;
		}
		if(player.isSwimming() || player.isInVehicle())
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER);
			return false;
		}

		return true;
	}

	public static boolean checkTeleportConditions(L2Player player)
	{
		if(player == null)
			return false;

		if(player.isAlikeDead())
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_DEAD);
			return false;
		}
		if(player.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE || player.isInTransaction())
		{
			player.sendPacket(Msg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_THE_PRIVATE_SHOPS);
			return false;
		}
		if(player.isParalyzed() || player.isActionBlock() || player.isStunned() || player.isSleeping())
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_IN_A_FLINT_OR_PARALYZED_STATE);
			return false;
		}

		return true;
	}

	public static boolean checkTeleportLocation(L2Player player, Location loc)
	{
		return checkTeleportLocation(player, loc.x, loc.y, loc.z);
	}

	public static boolean checkTeleportLocation(L2Player player, int x, int y, int z)
	{
		if(player == null)
			return false;

		ZoneManager zoneManager = ZoneManager.getInstance();
		for(ForbiddenZones forbiddenZone : ForbiddenZones.values())
			if(forbiddenZone.checkZ ? zoneManager.checkIfInZone(forbiddenZone.type, x, y, z, player.getReflectionId()) : zoneManager.checkIfInZone(forbiddenZone.type, x, y, player.getReflectionId()))
			{
				player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_TO_REACH_THIS_AREA);
				return false;
			}

		return true;
	}
}