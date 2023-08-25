package l2open.gameserver.model.entity.olympiad;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.scripts.*;
import l2open.gameserver.Announcements;
import l2open.gameserver.instancemanager.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.barahlo.PlayerInfo;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.quest.*;
import l2open.gameserver.serverpackets.*;
import l2open.util.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;

public class OlympiadStat
{
	public static HashMap<Integer, PlayerInfo> _stat_info = new HashMap<Integer, PlayerInfo>();

	private static IComparatorD _comparator_day = new IComparatorD();
	private static class IComparatorD implements Comparator<PlayerInfo>
	{
		public int compare(PlayerInfo o1, PlayerInfo o2)
		{
			if(o1 == null || o2 == null)
				return 0;
			// у кого больше убийств, тот и рулит
			if(o2.oly_pts_day > o1.oly_pts_day)
				return 1;
			// у кого меньше смертей, тот и вин
			if(o1.pvp_dead_count > o2.pvp_dead_count)
				return 1;
			if(o2.oly_pts_day < o1.oly_pts_day)
				return -1;
			//return (int)(o2.oly_pts_day - o1.oly_pts_day);
			return o1.name.compareTo(o2.name);
		}
	}

	private static IComparatorW _comparator_week = new IComparatorW();
	private static class IComparatorW implements Comparator<PlayerInfo>
	{
		public int compare(PlayerInfo o1, PlayerInfo o2)
		{
			if(o1 == null || o2 == null)
				return 0;
			// у кого больше убийств, тот и рулит
			if(o2.oly_pts_week > o1.oly_pts_week)
				return 1;
			// у кого меньше смертей, тот и вин
			if(o1.pvp_dead_count > o2.pvp_dead_count)
				return 1;
			if(o2.oly_pts_week < o1.oly_pts_week)
				return -1;
			//return (int)(o2.oly_pts_week - o1.oly_pts_week);
			return o1.name.compareTo(o2.name);
		}
	}

	public static void updateInfo(L2Player player, int type, int pts)
	{
		PlayerInfo pi = null;
		if(_stat_info.containsKey(player.getObjectId()))
			pi = _stat_info.get(player.getObjectId());
		else
		{
			pi = new PlayerInfo();
			pi.obj_id = player.getObjectId();
			pi.class_id = player.getBaseClassId();
			pi.name = player.getName();

			_stat_info.put(player.getObjectId(), pi);
		}
		switch(type)
		{
			case 0:
				pi.incPvpDead();
				pi.addOlyPts(pts);
				break;
			case 1:
				pi.incPvpKill();
				pi.addOlyPts(pts);
				break;
			case 2:
				pi.incEnter();
				break;
			case 3:
				pi.updateTime();
				update_db(player.getObjectId());
				break;
		}
	}

	public static String day_p_name = "-";
	public static int day_p_kill = 0;
	public static int day_p_dead = 0;

	// +
	public static void update_week_stat()
	{
		int state = ServerVariables.getInt("olympiad_week_state", 0);

		// 7 дней прошло, старт на 8й...
		if(state == 7)
		{
			List<PlayerInfo> _list = new ArrayList<PlayerInfo>(_stat_info.values());
			if(_list.size() > 0)
			{
				Collections.sort(_list, _comparator_week);

				PlayerInfo pi1 = _list.get(0);
				PlayerInfo pi2 = _list.get(1);
				PlayerInfo pi3 = _list.get(2);

				for(int i=0;i<ConfigValue.OlympiadWeekReward1st.length;i+=2)
					givePayPrice(pi1.obj_id, (int)ConfigValue.OlympiadWeekReward1st[i], ConfigValue.OlympiadWeekReward1st[i+1]);

				if(pi2 != null)
					for(int i=0;i<ConfigValue.OlympiadWeekReward2st.length;i+=2)
						givePayPrice(pi2.obj_id, (int)ConfigValue.OlympiadWeekReward2st[i], ConfigValue.OlympiadWeekReward2st[i+1]);

				if(pi3 != null)
					for(int i=0;i<ConfigValue.OlympiadWeekReward3st.length;i+=2)
						givePayPrice(pi3.obj_id, (int)ConfigValue.OlympiadWeekReward3st[i], ConfigValue.OlympiadWeekReward3st[i+1]);

				// Лучшими игроками Olympiad на прошлой неделе стали: 1 место - НИК, 2 место - НИК, 3 место - НИК, получают в награду Gold Einhasad.
				Announcements.getInstance().announceByCustomMessage("Olympiad.SetWeekWin", new String[] {pi1.name, pi3 != null ? pi2.name : "-", pi3 != null ? pi3.name : "-"});
			}

			ServerVariables.set("olympiad_week_state", "0");
			reset_week_stat();
		}
	}

	// +
	public static void set_day_reward()
	{
		int state = ServerVariables.getInt("olympiad_week_state", 0);

		ServerVariables.set("olympiad_week_state", String.valueOf(state+1));

		// определяем победителя и выдаем награду...
		List<PlayerInfo> _list = new ArrayList<PlayerInfo>(_stat_info.values());

		if(_list.size() > 0)
		{
			Collections.sort(_list, _comparator_day);

			PlayerInfo pi = _list.get(0);

			ServerVariables.set("olympiad_day_p_name", (day_p_name = pi.name));
			ServerVariables.set("olympiad_day_p_kill", String.valueOf(day_p_kill = pi.pvp_day_kill_count));
			ServerVariables.set("olympiad_day_p_dead", String.valueOf(day_p_dead = pi.pvp_day_dead_count));

			for(int i=0;i<ConfigValue.OlympiadDayReward.length;i+=2)
				givePayPrice(pi.obj_id, (int)ConfigValue.OlympiadDayReward[i], ConfigValue.OlympiadDayReward[i+1]);

			//Самым кровожадным стал НИК, провел 100 пвп. В награду получает тут пиши вручную, так проще мне:D
			Announcements.getInstance().announceToAllC("Olympiad.SetDayWin", day_p_name, String.valueOf(day_p_kill+day_p_dead));

			reset_day_stat();
		}
	}

	// +
	private static void reset_day_stat()
	{
		for(PlayerInfo pi : _stat_info.values())
		{
			pi.pvp_day_dead_count=0;
			pi.pvp_day_kill_count=0;

			pi.oly_pts_day=0;
			pi.oly_pts_week=0;
		}
		mysql.set("UPDATE `olympiad_stat` SET `pvp_day_dead_count`='0', `pvp_day_kill_count`='0', `oly_pts_day`='0', `oly_pts_week`='0'");
	}

	// +
	private static void reset_week_stat()
	{
		_stat_info.clear();
		mysql.set("delete from olympiad_stat;");
	}

	// +
	private static void update_db(int obj_id)
	{
		PlayerInfo pi = _stat_info.get(obj_id);
		mysql.set("REPLACE INTO olympiad_stat VALUES (?,?,?,?,?,?,?,?,?,?,?)",pi.obj_id,pi.class_id,pi.name,pi.enter_count,pi.oly_pts_day,pi.oly_pts_week,pi.pvp_dead_count,pi.pvp_kill_count,pi.pvp_day_dead_count,pi.pvp_day_kill_count,pi.zone_time);
	}

	// +
	private static void load_db()
	{
		if(ConfigValue.OlympiadStatEnable)
		{
			_stat_info.clear();

			day_p_name = ServerVariables.getString("pvp_zone_day_p_name", "-");
			day_p_kill = ServerVariables.getInt("pvp_zone_day_p_kill", 0);
			day_p_dead = ServerVariables.getInt("pvp_zone_day_p_dead", 0);

			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			ResultSet rset = null;

			try
			{
				con = L2DatabaseFactory.getInstanceLogin().getConnection();
				statement = con.prepareStatement("SELECT * FROM olympiad_stat");
				rset = statement.executeQuery();

				while(rset.next())
				{
					PlayerInfo pi = new PlayerInfo();
					pi.obj_id = rset.getInt("obj_id");
					pi.class_id = rset.getInt("class_id");
					pi.name = rset.getString("name");
					pi.enter_count = rset.getInt("enter_count");
					pi.oly_pts_day = rset.getInt("oly_pts_day");
					pi.oly_pts_week = rset.getInt("oly_pts_week");
					pi.pvp_dead_count = rset.getInt("pvp_dead_count");
					pi.pvp_kill_count = rset.getInt("pvp_kill_count");
					pi.pvp_day_dead_count = rset.getInt("pvp_day_dead_count");
					pi.pvp_day_kill_count = rset.getInt("pvp_day_kill_count");
					pi.zone_time = rset.getInt("zone_time");

					_stat_info.put(pi.obj_id, pi);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseUtils.closeDatabaseCSR(con, statement, rset);
			}
		}
	}
	// ---
	private static void givePayPrice(int obj_id, int item_id, long item_count)
	{
        L2Player player = L2ObjectsStorage.getPlayer(obj_id);
        if(player != null) // цель в игре? отлично
            Functions.addItem(player, item_id, item_count);
		else 
		{
            ThreadConnection con = null;
            FiltredPreparedStatement statement = null;
            ResultSet rs = null;
            try
			{
                con = L2DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement("SELECT object_id FROM items WHERE owner_id = ? AND item_id = ? AND loc = 'INVENTORY' LIMIT 1"); // сперва пробуем найти в базе его адену в инвентаре
                statement.setInt(1, obj_id);
                statement.setInt(2, item_id);
                rs = statement.executeQuery();
                if(rs.next())
				{
                    int id = rs.getInt("object_id");
                    DatabaseUtils.closeStatement(statement);
                    statement = con.prepareStatement("UPDATE items SET count=count+? WHERE object_id = ? LIMIT 1"); // если нашли увеличиваем ее количество
                    statement.setLong(1, item_count);
                    statement.setInt(2, id);
                    statement.executeUpdate();
                }
				else
				{
                    DatabaseUtils.closeStatement(statement);
                    statement = con.prepareStatement("INSERT INTO items_delayed (owner_id,item_id,`count`,description) VALUES (?,?,?,'mail')"); // иначе используем items_delayed
                    statement.setLong(1, obj_id);
                    statement.setLong(2, item_id);
                    statement.setLong(3, item_count);
                    statement.executeUpdate();
                }
            }
			catch(SQLException e)
			{
                e.printStackTrace();
            }
			finally
			{
                DatabaseUtils.closeDatabaseCSR(con, statement, rs);
            }
        }
    }

	static
	{
		load_db();
	}
}