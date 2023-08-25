package l2open.extensions.scripts;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.database.L2DatabaseFactory;
import l2open.database.mysql;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.itemmall.ItemMall;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExShowTrace;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.MapRegion;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Strings;
import l2open.util.Util;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

/**
 * @Author: Diamond
 * @Date: 7/6/2007
 * @Time: 5:22:23
 */
public class Functions
{
	private static final Logger _log = Logger.getLogger(Functions.class.getName());

	public L2Object self;
	public L2Object target;
	public L2NpcInstance npc=null;
	
	/**
	 * CommunityBoardForge	  		- 1<<123 -- Обычный Точильщик L2CCCP
	 * CommunityBoardForgeAtt 		- 1<<183 -- Точильщик Атт L2CCCP
	 * CommunityBoardClassMaster	- 1<<122 -- КлассМастер L2CCCP
	 * CommunityBoardBuffer			- 1<<111 -- Бафер L2CCCP
	 * ProtectionOfCaptive			- 1<<100 -- Ивент.
	 * 
	 ********************************************************************
	 * if((Functions.script & CommunityBoardBuffer) != CommunityBoardBuffer)
	 **/
	public static long script = 0;

	/**
	 * Вызывает метод с задержкой
	 * @param object - от чьего имени вызывать
	 * @param sClass<?> - вызываемый класс
	 * @param sMethod - вызываемый метод
	 * @param args - массив аргуметов
	 * @param variables - список выставляемых переменных
	 * @param delay - задержка в миллисекундах
	 */
	public static ScheduledFuture<?> executeTask(final L2Object object, final String sClass, final String sMethod, final Object[] args, final HashMap<String, Object> variables, long delay)
	{
		return ThreadPoolManager.getInstance().schedule_scripts(new Runnable(){
			@Override
			public void run()
			{
				if(object != null)
					object.callScripts(sClass, sMethod, args, variables);
			}
		}, delay);
	}

	public static ScheduledFuture<?> executeTask(final String sClass, final String sMethod, final Object[] args, final HashMap<String, Object> variables, long delay)
	{
		return ThreadPoolManager.getInstance().schedule_scripts(new Runnable(){
			@Override
			public void run()
			{
				callScripts(sClass, sMethod, args, variables);
			}
		}, delay);
	}

	public static ScheduledFuture<?> executeTask(final L2Object object, final String sClass, final String sMethod, final Object[] args, long delay)
	{
		return executeTask(object, sClass, sMethod, args, null, delay);
	}

	public static ScheduledFuture<?> executeTask(final String sClass, final String sMethod, final Object[] args, long delay)
	{
		return executeTask(sClass, sMethod, args, null, delay);
	}

	public static Object callScripts(String _class, String method, Object[] args)
	{
		return callScripts(_class, method, args, null);
	}

	public static Object callScripts(String _class, String method, Object[] args, HashMap<String, Object> variables)
	{
		if(l2open.extensions.scripts.Scripts.loading)
			return null;

		ScriptObject o;

		Script scriptClass = Scripts.getInstance().getClasses().get(_class);

		if(scriptClass == null)
			return null;

		try
		{
			o = scriptClass.newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}

		if(variables != null)
			for(Map.Entry<String, Object> obj : variables.entrySet())
				try
				{
					o.setProperty(obj.getKey(), obj.getValue());
				}
				catch(Exception e)
				{}

		return o.invokeMethod(method, args);
	}

	/**
	 * Вызывать только из скриптов
	 */
	public void show(String text, L2Player self)
	{
		show(text, self, getNpc());
	}

	/**
	 * Статический метод, для вызова из любых мест
	 */
	public static void show(String text, L2Player self, L2NpcInstance npc, Object... arg)
	{
		if(text == null || self == null)
			return;

		NpcHtmlMessage msg = new NpcHtmlMessage(self, npc);

		// Не указываем явно язык
		if(text.endsWith(".html-ru") || text.endsWith(".htm-ru"))
			text = text.substring(0, text.length() - 3);

		// приводим нашу html-ку в нужный вид
		if(text.endsWith(".html") || text.endsWith(".htm"))
			msg.setFile(text);
		else
			msg.setHtml(Strings.bbParse(text));

		if(arg != null && arg.length % 2 == 0)
		{
			for(int i = 0; i < arg.length; i = +2)
			{
				msg.replace(String.valueOf(arg[i]), String.valueOf(arg[i + 1]));
			}
		}

		self.sendPacket(msg);
	}

	public static void show(CustomMessage message, L2Player self)
	{
		show(message.toString(), self, null);
	}

	public static void sendMessage(String text, L2Player self)
	{
		self.sendMessage(text);
	}

	public static void sendMessage(CustomMessage message, L2Player self)
	{
		self.sendMessage(message);
	}

	// Белый чат
	public static void npcSayInRange(L2NpcInstance npc, String text, int range)
	{
		if(npc == null)
			return;
		NpcSay cs = new NpcSay(npc, Say2C.NPC_ALL, text);
		for(L2Player player : L2World.getAroundPlayers(npc, range, 200))
			if(player != null && !player.isBlockAll())
				player.sendPacket(cs);
	}

	public static void npcSayInRange(L2NpcInstance npc, int range, int fStringId)
	{
		if(npc == null)
			return;
		NpcSay cs = new NpcSay(npc, Say2C.NPC_ALL, fStringId);
		for(L2Player player : L2World.getAroundPlayers(npc, range, Math.max(range / 2, 200)))
			if(npc.getReflection() == player.getReflection())
				if(player != null && !player.isBlockAll())
					player.sendPacket(cs);
	}

	// Белый чат
	public static void npcSay(L2NpcInstance npc, int fStringId)
	{
		npcSayInRange(npc, 1500, fStringId);
	}

	// Белый чат
	public static void npcSay(L2NpcInstance npc, String text)
	{
		npcSayInRange(npc, text, 1500);
	}

	// Белый чат
	public static void npcSayInRangeCustomMessage(L2NpcInstance npc, int range, String address, Object... replacements)
	{
		if(npc == null)
			return;
		for(L2Player player : L2World.getAroundPlayers(npc, range, 200))
			if(player != null && !player.isBlockAll())
				player.sendPacket(new NpcSay(npc, Say2C.NPC_ALL, new CustomMessage(address, player, replacements).toString()));
	}

	// Белый чат
	public static void npcSayCustomMessage(L2NpcInstance npc, String address, Object... replacements)
	{
		npcSayInRangeCustomMessage(npc, 1500, address, replacements);
	}

	// private message
	public static void npcSayToPlayer(L2NpcInstance npc, L2Player player, String text)
	{
		if(npc == null || player.isBlockAll())
			return;
		player.sendPacket(new NpcSay(npc, 2, text));
	}

	// Shout (желтый) чат
	public static void npcShout(L2NpcInstance npc, String text)
	{
		if(npc == null)
			return;
		NpcSay cs = new NpcSay(npc, Say2C.NPC_SHOUT, text);
		if(ConfigValue.ShoutChatMode == 1)
		{
			for(L2Player player : L2World.getAroundPlayers(npc, 10000, 1500))
				if(player != null && !player.isBlockAll())
					player.sendPacket(cs);
		}
		else
		{
			int mapregion = MapRegion.getInstance().getMapRegion(npc.getX(), npc.getY());
			for(L2Player player : L2ObjectsStorage.getPlayers())
				if(player != null && MapRegion.getInstance().getMapRegion(player.getX(), player.getY()) == mapregion && !player.isBlockAll())
					player.sendPacket(cs);
		}
	}

	public static void npcShout(L2NpcInstance npc, int npcString, String params)
	{
		npcShout(npc, Say2C.NPC_SHOUT, npcString, params);
	}
	// Shout (желтый) чат
	public static void npcShout(L2NpcInstance npc, int type, int npcString, String params)
	{
		if(npc == null)
			return;
		NpcSay cs = new NpcSay(npc, type, npcString, params);

		int rx = Util.regionX(npc);
		int ry = Util.regionY(npc);

		for(L2Player player : L2ObjectsStorage.getPlayers())
		{
			if(player.getReflection() != npc.getReflection())
				continue;

			int tx = Util.regionX(player);
			int ty = Util.regionY(player);

			if(tx >= rx - ConfigValue.ShoutOffset && tx <= rx + ConfigValue.ShoutOffset && ty >= ry - ConfigValue.ShoutOffset && ty <= ry + ConfigValue.ShoutOffset)
				player.sendPacket(cs);
		}
	}

	// Shout (желтый) чат
	public static void npcShoutCustomMessage(L2NpcInstance npc, String address, Object... replacements)
	{
		if(npc == null)
			return;
		if(ConfigValue.ShoutChatMode == 1)
		{
			for(L2Player player : L2World.getAroundPlayers(npc, 10000, 1500))
				if(player != null && !player.isBlockAll())
					player.sendPacket(new NpcSay(npc, Say2C.NPC_SHOUT, new CustomMessage(address, player, replacements).toString()));
		}
		else
		{
			int mapregion = MapRegion.getInstance().getMapRegion(npc.getX(), npc.getY());
			for(L2Player player : L2ObjectsStorage.getPlayers())
				if(player != null && MapRegion.getInstance().getMapRegion(player.getX(), player.getY()) == mapregion && !player.isBlockAll())
					player.sendPacket(new NpcSay(npc, Say2C.NPC_SHOUT, new CustomMessage(address, player, replacements).toString()));
		}
	}

	/**
	 * Добавляет предмет в инвентарь чара
	 * @param playable Владелец инвентаря
	 * @param item_id ID предмета
	 * @param count количество
	 */
	public static void addItem(L2Playable playable, int item_id, long count)
	{
		if(playable == null || count < 1)
			return;

		L2Player player = playable.getPlayer();
		if(item_id == -1000)
		{
			try
			{
				mysql.setEx(L2DatabaseFactory.getInstance(), "INSERT INTO `market_point` (login, points) VALUES ('"+player.getAccountName()+"', '"+count+"') ON DUPLICATE KEY UPDATE points=points+"+count);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			L2ItemInstance item = ItemTemplates.getInstance().createItem(item_id);
			if(item.isStackable())
			{
				item.setCount(count);
				player.getInventory().addItem(item);
				}
			else
			{
				player.getInventory().addItem(item);
				for(int i = 1; i < count; i++)
					player.getInventory().addItem(ItemTemplates.getInstance().createItem(item_id));
			}

			player.sendPacket(SystemMessage.obtainItems(item_id, count, 0));
		}
	}

	public static void addItem(L2Playable playable, int item_id, long count, int enchant)
	{
		if(playable == null || count < 1)
			return;

		L2Player player = playable.getPlayer();
		if(item_id == -1000)
		{
			try
			{
				mysql.setEx(L2DatabaseFactory.getInstance(), "INSERT INTO `market_point` (login, points) VALUES ('"+player.getAccountName()+"', '"+count+"') ON DUPLICATE KEY UPDATE points=points+"+count);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			L2ItemInstance item = ItemTemplates.getInstance().createItem(item_id);

			if(item.isStackable())
			{
				item.setCount(count);
				player.getInventory().addItem(item);
			}
			else
			{
				item.setEnchantLevel(enchant);
				player.getInventory().addItem(item);
				for(int i = 1; i < count; i++)
				{
					item = ItemTemplates.getInstance().createItem(item_id);
					item.setEnchantLevel(enchant);
					player.getInventory().addItem(item);
				}
			}

			player.sendPacket(SystemMessage.obtainItems(item_id, count, enchant));
		}
	}

	/**
	 * Возвращает количество предметов в инвентаре чара.
	 * @param playable Владелец инвентаря
	 * @param item_id ID предмета
	 * @return количество
	 */
	public static long getItemCount(L2Playable playable, int item_id)
	{
		long count = 0;
		L2Player player = playable.getPlayer();
		if(item_id == -1000)
			return player.getPoint(true);
		Inventory inv = player.getInventory();
		if(inv == null)
			return 0;
		L2ItemInstance[] items = inv.getItems();
		for(L2ItemInstance item : items)
			if(item.getItemId() == item_id)
				count += item.getCount();
		return count;
	}

	/**
	 * Удаляет предметы из инвентаря чара.
	 * @param playable Владелец инвентаря
	 * @param item_id ID предмета
	 * @param count количество
	 * @return количество удаленных
	 */
	public static long removeItem(L2Playable playable, int item_id, long count)
	{
		if(playable == null || count < 1)
			return 0;

		L2Player player = playable.getPlayer();
		if(item_id == -1000)
		{
			ItemMall.getInstance().validateMyPoints(player, (int)count, true);
			return count;
		}
		Inventory inv = player.getInventory();
		if(inv == null)
			return 0;
		long removed = count;
		L2ItemInstance[] items = inv.getItems();
		for(L2ItemInstance item : items)
			if(item.getItemId() == item_id && count > 0)
			{
				long item_count = item.getCount();
				long rem = count <= item_count ? count : item_count;
				player.getInventory().destroyItemByItemId(item_id, rem, true);
				count -= rem;
			}
		removed -= count;

		if(removed > 0)
			player.sendPacket(SystemMessage.removeItems(item_id, removed));
		return removed;
	}

	public static void removeItemByObjId(L2Playable playable, int item_obj_id, int count)
	{
		if(playable == null || count < 1)
			return;

		L2Playable player;
		if(playable.isSummon())
			player = playable.getPlayer();
		else
			player = playable;
		Inventory inv = player.getInventory();
		if(inv == null)
			return;
		L2ItemInstance[] items = inv.getItems();
		for(L2ItemInstance item : items)
			if(item.getObjectId() == item_obj_id && count > 0)
			{
				long item_count = item.getCount();
				int item_id = item.getItemId();
				long removed = count <= item_count ? count : item_count;
				player.getInventory().destroyItem(item, removed, true);
				if(removed > 1)
					player.sendPacket(SystemMessage.removeItems(item_id, removed));
			}
	}

	public static boolean ride(L2Player player, int pet)
	{
		if(player.isMounted())
			player.setMount(0, 0, 0);

		if(player.getPet() != null)
		{
			player.sendPacket(Msg.YOU_ALREADY_HAVE_A_PET);
			return false;
		}

		player.setMount(pet, 0, 0);
		return true;
	}

	public static void unRide(L2Player player)
	{
		if(player.isMounted())
			player.setMount(0, 0, 0);
	}

	public static void unSummonPet(L2Player player, boolean onlyPets)
	{
		L2Summon pet = player.getPet();
		if(pet == null)
			return;
		if(pet.isPet() || !onlyPets)
			pet.unSummon();
	}

	public static L2NpcInstance spawn(Location loc, int npcId)
	{
		try
		{
			L2NpcInstance npc = NpcTable.getTemplate(npcId).getNewInstance();
			npc.setSpawnedLoc(loc.correctGeoZ());
			npc.onSpawn();
			npc.spawnMe(npc.getSpawnedLoc());
			return npc;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public L2Object getSelf()
	{
		return self;
	}

	public L2Object getTarget()
	{
		return target;
	}

	public L2Player getSelfPlayer()
	{
		return (L2Player)self;
	}

	public L2NpcInstance getNpc()
	{
		return npc;
	}

	public static String htmlButton(String value, String action, int width)
	{
		return Strings.htmlButton(value, action, width);
	}

	public static String htmlButton(String value, String action, int width, int height)
	{
		return Strings.htmlButton(value, action, width, height);
	}

	public static ExShowTrace Points2Trace(GArray<int[]> points, int step, boolean auto_compleate, boolean maxz)
	{
		ExShowTrace result = new ExShowTrace(60000);

		int[] prev = null;
		int[] first = null;
		for(int[] p : points)
		{
			if(first == null)
				first = p;

			if(prev != null)
				result.addLine(prev[0], prev[1], maxz ? prev[3] : prev[2], p[0], p[1], maxz ? p[3] : p[2], step);

			prev = p;
		}

		if(prev == null || first == null)
			return result;

		if(auto_compleate)
			result.addLine(prev[0], prev[1], maxz ? prev[3] : prev[2], first[0], first[1], maxz ? first[3] : first[2], step);

		return result;
	}

	public static synchronized void SpawnNPCs(int npcId, int[][] locations, GArray<L2Spawn> list)
	{
		L2NpcTemplate template = NpcTable.getTemplate(npcId);
		if(template == null)
		{
			_log.warning("WARNING! Functions.SpawnNPCs template is null for npc: " + npcId);
			Thread.dumpStack();
			return;
		}
		for(int[] location : locations)
			try
			{
				L2Spawn sp = new L2Spawn(template);
				sp.setLoc(new Location(location));
				sp.setAmount(1);
				sp.setRespawnDelay(0);
				sp.init();
				if(list != null)
					list.add(sp);
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
	}

	/*public static L2NpcInstance spawnParam(int id, Location loc, String[] param_name, String[] param_value)
	{
		L2NpcInstance npc = null;
		L2Spawn spawn = null;
		StatsSet npcDat = null;
		StatsSet npcAI = null;
		L2NpcTemplate template = null;
		try
		{
			npcAI = new StatsSet();
			template = NpcTable.getTemplate(id);
			npcDat = template.getSet();

			for(int i = 0;i < param_name.length;i++)
				npcAI.set(param_name[i], param_value[i]);

			npcDat.set("AIparam", npcAI);
			template.setSet(npcDat);

			spawn = new L2Spawn(template);
			spawn.setAmount(1);
			spawn.setRespawnDelay(0, 0);
			spawn.setLoc(loc);
			npc = spawn.doSpawn(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return npc;
	}*/

	public static L2Spawn spawnParam(int id, int[] loc, String param_name, String param_value)
	{
		L2Spawn spawn = null;

		try
		{
			spawn = new L2Spawn(NpcTable.getTemplate(id));
			spawn.setAmount(1);
			spawn.setAIParam(param_name+"="+param_value);
			spawn.setRespawnDelay(0, 0);
			spawn.setLoc(new Location(loc));
			spawn.doSpawn(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return spawn;
	}

	public static synchronized void deSpawnNPCs(GArray<L2Spawn> list)
	{
		for(L2Spawn sp : list)
		{
			if(sp != null)
			{
				sp.stopRespawn();
				sp.getLastSpawn().deleteMe();
			}
		}
		list.clear();
	}

	public static boolean IsActive(String name)
	{
		return ServerVariables.getString(name, "off").equalsIgnoreCase("on");
	}

	public static boolean SetActive(String name, boolean active)
	{
		if(active == IsActive(name))
			return false;
		if(active)
			ServerVariables.set(name, "on");
		else
			ServerVariables.unset(name);
		return true;
	}

	public static boolean SimpleCheckDrop(L2Character mob, L2Character killer)
	{
		return mob != null && mob.isMonster() && !mob.isRaid() && killer != null && killer.getPlayer() != null && killer.getLevel() - mob.getLevel() < 9;
	}

	public static boolean isPvPEventStarted()
	{
		if((Boolean) callScripts("events.TvT.TvT", "isRunned", new Object[] {}))
			return true;
		else if((Boolean) callScripts("events.lastHero.LastHero", "isRunned", new Object[] {}))
			return true;
		else if((Boolean) callScripts("events.CtF.CtF", "isRunned", new Object[] {}))
			return true;
		else if(ConfigValue.EventBoxEnable && (Boolean) callScripts("events.EventBox.EventBox", "isRunned", new Object[] {}))
			return true;
		else if(ConfigValue.Tournament_Enable)
		{
			if((Boolean) callScripts("events.Turnir.Tournament", "isActive", new Object[] {}))
				return true;
		}
		return false;
	}

	public static L2NpcInstance spawn(int x, int y, int z, int npcId)
	{
		return spawn(new Location(x, y, z), npcId, 0);
	}

	public static L2NpcInstance spawn(Location loc, int npcId, int resp)
	{
		return spawn(loc, npcId, resp, 0);
	}

	public static L2NpcInstance spawn(Location loc, int npcId, int resp, int reflection)
	{
		L2NpcInstance _npc = null;
		try
		{
			L2Spawn spawn = new L2Spawn(NpcTable.getTemplate(npcId));
			spawn.setLoc(loc);
			spawn.setRespawnDelay(resp);
			if(resp > 0)
				spawn.startRespawn();
			_npc = spawn.doSpawn(true);
			_npc.setReflection(reflection);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return _npc;
	}

	public static L2NpcInstance spawn(int npcId, int loc, int resp)
	{
		try
		{
			L2Spawn spawn = new L2Spawn(NpcTable.getTemplate(npcId));
			spawn.setLocx(0);
			spawn.setLocy(0);
			spawn.setLocz(0);
			spawn.setLocation(loc);
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

	public static synchronized void SpawnNPCs(int npcId, int locations, String banedTerritory, int resp, int count, GArray<L2Spawn> list)
	{
		L2NpcTemplate template = NpcTable.getTemplate(npcId);
		if(template == null)
		{
			_log.warning("WARNING! Functions.SpawnNPCs template is null for npc: " + npcId);
			Thread.dumpStack();
			return;
		}
		try
		{
			L2Spawn sp = new L2Spawn(template);
			sp.setLocx(0);
			sp.setLocy(0);
			sp.setLocz(0);
			sp.setLocation(locations);
			sp.setRespawnDelay(resp);
			sp.setAmount(count);
			sp.setBanedTerritory(banedTerritory);
			sp.init();
			if(list != null)
				list.add(sp);
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

    public static void openDoor(int doorId, int instanceId)
    {
        for(L2DoorInstance door : ReflectionTable.getInstance().get(instanceId).getDoors())
            if(door.getDoorId() == doorId)
                door.openMe();
    }

    public static void closeDoor(int doorId, int instanceId)
    {
        for(L2DoorInstance door : ReflectionTable.getInstance().get(instanceId).getDoors())
            if(door.getDoorId() == doorId)
                door.closeMe();
    }

	/*public static void SpawnNPCs(int npcId, int locations, int resp, int count, GArray<L2Spawn> list, String ai_type, String param_name, String param_value)
	{
		SpawnNPCs(npcId, locations, resp, count, list, ai_type, "NUN", param_name, param_value);
	}

	public static synchronized void SpawnNPCs(int npcId, int locations, int resp, int count, GArray<L2Spawn> list, String ai_type, String instance, String param_name, String param_value)
	{
		L2NpcTemplate template = NpcTable.getTemplate(npcId);
		StatsSet npcDat = null;
		StatsSet npcAI = null;

		if(template == null)
			return;

		npcAI = new StatsSet();
		npcDat = template.getSet();
		npcAI.set(param_name, param_value);
		npcDat.set("AIparam", npcAI);
		template.setSet(npcDat);

		template.ai_type = ai_type;
		if(!instance.equals("NUN"))
			template.setInstance(instance);

		try
		{
			for(int i = 0;i<count;i++)
			{
				L2Spawn sp = new L2Spawn(template);
				sp.setLocx(0);
				sp.setLocy(0);
				sp.setLocz(0);
				sp.setLocation(locations);
				sp.setRespawnDelay(resp);
				sp.setAmount(1);
				sp.doSpawn(true);
				if(list != null)
					list.add(sp);
			}
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public static synchronized L2Spawn spawnParam(int id, int[] loc, String param_name, String param_value, String instance)
	{
		L2Spawn spawn = null;
		StatsSet npcDat = null;
		StatsSet npcAI = null;
		L2NpcTemplate template = null;
		try
		{
			npcAI = new StatsSet();
			template = NpcTable.getTemplate(id);
			template.setInstance(instance);
			npcDat = template.getSet();

			npcAI.set(param_name, param_value);

			npcDat.set("AIparam", npcAI);
			template.setSet(npcDat);

			spawn = new L2Spawn(template);
			spawn.setAmount(1);
			spawn.setRespawnDelay(0, 0);
			spawn.setLoc(new Location(loc));
			spawn.doSpawn(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return spawn;
	}*/

	public static L2ItemInstance createItem(int itemId)
	{
		L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), itemId);
		item.setLocation(L2ItemInstance.ItemLocation.VOID);
		item.setCount(1L);

		return item;
	}
}