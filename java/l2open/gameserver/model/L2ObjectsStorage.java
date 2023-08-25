package l2open.gameserver.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;

import l2open.config.ConfigValue;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2PetInstance;
import l2open.util.GArray;
import l2open.util.Log;
import l2open.util.StrTable;

import java.util.logging.Logger;

//import objectexplorer.MemoryMeasurer;
//import objectexplorer.ObjectGraphMeasurer;
//import objectexplorer.ObjectGraphMeasurer.Footprint;

/**
 * @author Diagod
 */
public class L2ObjectsStorage
{
	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(L2ObjectsStorage.class.getName());

	private static IntObjectMap<L2Player> _players = new CHashIntObjectMap<L2Player>(ConfigValue.MaximumOnlineUsers);
	private static IntObjectMap<L2Playable> _playables = new CHashIntObjectMap<L2Playable>(ConfigValue.MaximumOnlineUsers);
	private static IntObjectMap<L2NpcInstance> _npcs = new CHashIntObjectMap<L2NpcInstance>(ConfigValue.StorageNpcCap);
	private static IntObjectMap<L2Object> _objects = new CHashIntObjectMap<L2Object>(ConfigValue.StorageOtherCap + ConfigValue.MaximumOnlineUsers);

	@SuppressWarnings("unchecked")
	public static <T extends L2Object> void put(T object)
	{
		if(object.getObjectId() > 0)
		{
			IntObjectMap map = getMapForObject(object);
			map.put(object.getObjectId(), object);
		}
	}

	public static <T extends L2Object> void remove(T object)
	{
		if(object.getObjectId() > 0)
		{
			IntObjectMap map = getMapForObject(object);
			map.remove(object.getObjectId());
		}
	}

	private static <T extends L2Object> IntObjectMap<?> getMapForObject(T object)
	{
		if(object.isPlayer())
			return _players;
		else if(object.isPlayable())
			return _playables;
		else if(object.isNpc())
			return _npcs;
		return _objects;
	}

	public static L2Player getPlayer(String name)
	{
		for (L2Player player : _players.values())
			if (player.getName().equalsIgnoreCase(name))
				return player;
		return null;
	}

	public static L2Player getPlayer(int obj_id)
	{
		return _players.get(obj_id);
	}

	public static Collection<L2Player> getPlayers()
	{
		return _players.values();
	}

	public static Collection<L2NpcInstance> getAllNpcs()
	{
		return _npcs.values();
	}

	public static L2NpcInstance getByNpcId(int npc_id)
	{
		L2NpcInstance result = null;
		for(L2NpcInstance temp : getAllNpcs())
			if(npc_id == temp.getNpcId())
			{
				if(!temp.isDead())
					return temp;
				result = temp;
			}
		return result;
	}

	public static List<L2NpcInstance> getAllByNpcId(int npc_id, boolean justAlive)
	{
		List<L2NpcInstance> result = new ArrayList<L2NpcInstance>(0);
		for(L2NpcInstance temp : getAllNpcs())
			if(temp.getTemplate() != null && npc_id == temp.getTemplate().getNpcId() && (!justAlive || !temp.isDead()))
				result.add(temp);
		return result;
	}

	public static List<L2NpcInstance> getAllByNpcId(int npc_id, boolean justAlive, boolean visible)
	{
		List<L2NpcInstance> result = new ArrayList<L2NpcInstance>();
		for (L2NpcInstance temp : getAllNpcs())
			if (temp.getTemplate() != null && npc_id == temp.getTemplate().getNpcId() && (!justAlive || !temp.isDead()) && (!visible || temp.isVisible()))
				result.add(temp);
		return result;
	}

	public static List<L2NpcInstance> getAllByNpcId(int[] npc_ids, boolean justAlive)
	{
		List<L2NpcInstance> result = new ArrayList<L2NpcInstance>(0);
		for(L2NpcInstance temp : getAllNpcs())
			if(!justAlive || !temp.isDead())
				for(int npc_id : npc_ids)
					if(npc_id == temp.getNpcId())
						result.add(temp);
		return result;
	}

	public static L2NpcInstance getNpc(int obj_id)
	{
		return _npcs.get(obj_id);
	}

	/**
	 * Возвращает онлайн с офф трейдерами, но без накрутки.
	 */
	public static int getAllPlayersCount()
	{
		return _players.size();
	}

	private static long offline_refresh = 0;
	private static int offline_count = 0;
	private static long bot_refresh = 0;
	private static int bot_count = 0;

	public static int getAllOfflineCount()
	{
		if(!ConfigValue.AllowOfflineTrade)
			return 0;

		long now = System.currentTimeMillis();
		if(now > offline_refresh)
		{
			offline_refresh = now + ConfigValue.OnlineRefresh*1000;
			offline_count = 0;
			for(L2Player player : getPlayers())
				if(player.isInOfflineMode())
					offline_count++;
		}

		return offline_count;
	}

	public static int getOnlineCount()
	{
		int online_count = 0;
		for(L2Player player : getPlayers())
			if(!player.isInOfflineMode() && !player.isBot2() && !player.isFantome())
				online_count++;
		return online_count;
	}

	public static int getBotPlayersCount()
	{
		long now = System.currentTimeMillis();
		if(now > bot_refresh)
		{
			bot_refresh = now/* + 10000*/;
			bot_count = 0;
			for(L2Player player : getPlayers())
				if(player.isBot() || player.isFantome())
					bot_count++;
		}

		return bot_count;
	}

	public static int getAllObjectsCount()
	{
		return _players.size() + _playables.size() + _npcs.size() + _objects.size();
	}

	@SuppressWarnings( { "unchecked" })
	public static List<L2Object> getAllObjects()
	{
		List<L2Object> result = new ArrayList<L2Object>(getAllObjectsCount());
		result.addAll(_players.values());
		result.addAll(_playables.values());
		result.addAll(_npcs.values());
		result.addAll(_objects.values());
		return result;
	}

	// потом снесу
	public static L2Character getCharacter(int obj_id)
	{
		L2Character cha = _players.get(obj_id);
		if(cha == null)
			cha = _playables.get(obj_id);
		if(cha == null)
			cha = _npcs.get(obj_id);
		return cha;
	}

	public static void getDump()
	{
		for(L2Player p : _players.values())
			Log.addMy(""+p, "storage", "PLAYER");
		for(L2Playable p : _playables.values())
			Log.addMy(""+p, "storage", "SUMMON");
		for(L2NpcInstance p : _npcs.values())
			Log.addMy(""+p, "storage", "NPC");
		for(L2Object p : _objects.values())
			Log.addMy(""+p, "storage", "OTHER");
	}

	public static StrTable getStats()
	{
		StrTable table = new StrTable("L2 Objects Storage Stats");

		long size = 0;//MemoryMeasurer.measureBytes(_players);
		long mb = size/1048576;
		long kb = (size%1048576)/1024;
		long b = size%1024;

		table.set(0, "Name", "PLAYER");
		table.set(0, "Size", _players.size());
		table.set(0, "RealMemSize", "Mb: "+mb+" kb: "+kb+" byte: "+b);
		//table.set(0, "Inf", ObjectGraphMeasurer.measure(_players));

		//size = MemoryMeasurer.measureBytes(_playables);
		mb = size/1048576;
		kb = (size%1048576)/1024;
		b = size%1024;

		table.set(1, "Name", "SUMMON");
		table.set(1, "Size", _playables.size());
		table.set(1, "RealMemSize", "Mb: "+mb+" kb: "+kb+" byte: "+b);
		//table.set(1, "Inf", ObjectGraphMeasurer.measure(_playables));

		//size = MemoryMeasurer.measureBytes(_npcs);
		mb = size/1048576;
		kb = (size%1048576)/1024;
		b = size%1024;

		table.set(2, "Name", "NPC");
		table.set(2, "Size", _npcs.size());
		table.set(2, "RealMemSize", "Mb: "+mb+" kb: "+kb+" byte: "+b);
		//table.set(2, "Inf", ObjectGraphMeasurer.measure(_npcs));

		//size = MemoryMeasurer.measureBytes(_objects);
		mb = size/1048576;
		kb = (size%1048576)/1024;
		b = size%1024;

		table.set(3, "Name", "OTHER");
		table.set(3, "Size", _objects.size());
		table.set(3, "RealMemSize", "Mb: "+mb+" kb: "+kb+" byte: "+b);
		//table.set(3, "Inf", ObjectGraphMeasurer.measure(_objects));

		return table;
	}
}