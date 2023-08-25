package l2open.pts.loader;

import java.util.*;

/**
 * Запиздовал Diagod...
 * open-team.ru
 **/
public class NpcData extends LoadData
{
	private static HashMap<Integer, String> _npc_pch = new HashMap<Integer, String>();
	// ------------------------------------------------------------------------------------------------------------------------
	// Загружает имена и ид NPC...
	public static void loadNpcPch()
	{
		long _time1 = System.currentTimeMillis();
		List<String> _pch = loadFile("./data/pts/npc_pch.txt");
		if(_pch == null)
		{
			_log.warning("Not found: './data/pts/npc_pch.txt'");
			return;
		}
		StringTokenizer st;

		for(String line : _pch)
		{
			st = new StringTokenizer(line, "]");
			String name = st.nextToken().substring(1);
			Integer id = Integer.parseInt(st.nextToken().replaceAll("=", "").replaceAll("	", "").replaceAll(" ", ""));
			_npc_pch.put(id, name);
			//_log.info("UPDATE npc SET pts_name='"+name+"' WHERE id='"+(id-1000000)+"';");
		}
		st = null;
		_pch.clear();
		_pch = null;
		long _time2 = System.currentTimeMillis() - _time1;
		_log.info("PTS NpcDataPch: Finish loading data size: "+_npc_pch.size()+" fo "+_time2+" ms...");
	}

	// Возвращает ИД НПС по его имени...
	public static int getNpcId(String name)
	{
		for(Integer id : _npc_pch.keySet())
			if(_npc_pch.get(id).equals(name) && id > 1000000)
				return id-1000000;
		return -1;
	}

	// Возвращает имя НПС по его ИД...
	public static String getNpcName(int id, boolean isNpc)
	{
		//if(id < 1000000 && id > 136)
		if(id < 1000000 && isNpc)
			id = id + 1000000;
		return _npc_pch.get(id);
	}
}
