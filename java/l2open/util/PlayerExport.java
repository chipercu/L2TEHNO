package l2open.util;

import l2open.Server;
import l2open.database.mysql;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PlayerExport
{
	@SuppressWarnings("unchecked")
	public static String export(String name)
	{
		StringBuilder e = new StringBuilder();

		// игрок должен быть оффлайн
		L2Player p = L2World.getPlayer(name);
		if(p != null)
			return "";

		ConcurrentHashMap<String, Object> cset = (ConcurrentHashMap<String, Object>) mysql.get("select * from characters where char_name LIKE '" + name + "'");

		if(cset == null)
			return "";

		Long id = (Long) cset.get("obj_Id");

		e.append("<player");
		for(String s : cset.keySet())
		{
			if(s.equals("clanid") || s.equals("deletetime") || s.equals("title"))
				e.append(" " + s + "='0'");
			e.append(" " + s + "='").append(cset.get(s)).append("'");
		}
		e.append(" >\n");

		dumpTable(e, new Object[] { "character_subclasses", "char_obj_id", id }, new String[] {});
		dumpTable(e, new Object[] { "character_skills", "char_obj_id", id }, new String[] {});
		dumpTable(e, new Object[] { "character_variables", "obj_id", id }, new String[] {});
		dumpTable(e, new Object[] { "character_shortcuts", "char_obj_id", id }, new String[] {});
		dumpTable(e, new Object[] { "character_skills_save", "char_obj_id", id }, new String[] {});
		dumpTable(e, new Object[] { "character_recipebook", "char_id", id }, new String[] {});
		dumpTable(e, new Object[] { "character_quests", "char_id", id }, new String[] {});
		dumpTable(e, new Object[] { "character_macroses", "char_obj_id", id }, new String[] {});
		dumpTable(e, new Object[] { "character_hennas", "char_obj_id", id }, new String[] {});
		dumpTable(e, new Object[] { "character_friends", "char_id", id }, new String[] {});
		dumpTable(e, new Object[] { "character_effects_save", "char_obj_id", id }, new String[] {});
		dumpTable(e, new Object[] { "character_blocklist", "obj_Id", id }, new String[] {});
		dumpTable(e, new Object[] { "items_delayed", "owner_id", id }, new String[] {});
		dumpTable(e, new Object[] { "olympiad_nobles", "char_id", id }, new String[] {});
		dumpTable(e, new Object[] { "seven_signs", "char_obj_id", id }, new String[] {});

		HashMap<String, String> ids = new HashMap<String, String>();
		Integer nid = 0;
		GArray<HashMap<String, Object>> set;
		e.append("	<items>\n");
		set = mysql.getAll("SELECT * FROM items WHERE owner_id=" + id);
		for(HashMap<String, Object> map : set)
		{
			e.append("		<row");
			for(String s : map.keySet())
				if(s.equals("object_id"))
				{
					ids.put(map.get(s).toString(), (++nid).toString());
					e.append("object_id").append("='@NEW_ID" + nid + "@'");
				}
				else
					e.append(" " + s + "='").append(map.get(s)).append("'");
			e.append(" />\n");
		}
		e.append("	</items>\n");

		e.append("	<pets>\n");
		for(HashMap<String, Object> map : set)
		{
			cset = (ConcurrentHashMap<String, Object>) mysql.get("SELECT * FROM pets WHERE item_obj_id=" + map.get("object_id"));
			if(cset != null)
			{
				e.append("		<row");
				for(String s : cset.keySet())
					if(s.equals("item_obj_id"))
						e.append(" " + s + "='@NEW_ID" + ids.get(cset.get("item_obj_id")) + "@'");
					else if(s.equals("objId"))
						e.append(" " + s + "='@NEW_ID@'");
					else
						e.append(" " + s + "='").append(cset.get(s)).append("'");
				e.append(" />\n");
			}
		}
		e.append("	</pets>\n");

		e.append("	<augmentations>\n");
		for(HashMap<String, Object> map : set)
		{
			cset = (ConcurrentHashMap<String, Object>) mysql.get("SELECT * FROM augmentations WHERE item_id=" + map.get("object_id"));
			if(cset != null)
			{
				e.append("		<row");
				for(String s : cset.keySet())
					if(!s.equals("item_id"))
						e.append(" " + s + "='").append(cset.get(s)).append("'");
					else
						e.append(" " + s + "='@NEW_ID" + ids.get(cset.get("item_id")) + "@'");
				e.append(" />\n");
			}
		}
		e.append("	</augmentations>\n");

		e.append("</player>");

		String rs = e.toString();
		rs = rs.replaceAll(String.valueOf(id), "@PLAYER_ID@");
		System.out.println(rs);
		Server.halt(0, "PlayerExport");
		return rs;
	}

	private static StringBuilder dumpTable(StringBuilder e, Object[] query, String[] replace)
	{
		e.append("	<" + query[0] + ">\n");
		GArray<HashMap<String, Object>> set = mysql.getAll("SELECT * FROM " + query[0] + " WHERE " + query[1] + "=" + query[2]);
		for(HashMap<String, Object> map : set)
		{
			e.append("		<row");
			for(String s : map.keySet())
				e.append(" " + s + "='").append(map.get(s)).append("'");
			e.append(" />\n");
		}
		e.append("	</" + query[0] + ">\n");
		return e;
	}
}