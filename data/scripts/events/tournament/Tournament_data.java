package events.tournament;

import java.sql.ResultSet;

import javolution.util.FastMap;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.database.mysql;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Player;

public class Tournament_data extends Functions implements ScriptFile
{
	private static FastMap<Integer, Team> teams = new FastMap<Integer, Team>().setShared(true);

	public static Team getTeamById(Integer id)
	{
		return teams.get(id);
	}

	public static Team getTeamByName(String name)
	{
		for(Team team : teams.values())
			if(name.equalsIgnoreCase(team.getName()))
				return team;
		return null;
	}

	public static FastMap<Integer, Team> getTeams()
	{
		return teams;
	}

	public void onLoad()
	{
		loadTeams();
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public static void loadTeams()
	{
		teams = new FastMap<Integer, Team>().setShared(true);
		ThreadConnection con = null;
		FiltredPreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("SELECT * FROM tournament_teams");
			rs = offline.executeQuery();
			while(rs.next())
			{
				Integer team_id = rs.getInt("team_id");
				Team team = teams.get(team_id);
				if(team == null)
				{
					team = new Team();
					teams.put(team_id, team);
				}
				team.setName(rs.getString("team_name"));
				team.setId(team_id);
				team.setCategory(rs.getInt("category"));

				Integer leader = rs.getInt("leader");
				if(leader == 1)
					team.setLeader(rs.getInt("obj_id"));
				else
					team.addMember(rs.getInt("obj_id"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, offline, rs);
		}
	}

	public static Boolean createTeam(String name, L2Player leader)
	{
		Team team = new Team();
		team.setName(name);
		team.setId(IdFactory.getInstance().getNextId());
		team.setLeader(leader.getObjectId());
		team.setCategory(getCategory(leader.getLevel()));
		Integer type = getPlayerType(leader.getObjectId());
		ThreadConnection con = null;
		FiltredPreparedStatement insertion = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			insertion = con.prepareStatement("INSERT INTO tournament_teams (obj_id, type, team_id, team_name, leader, category) VALUES (?,?,?,?,1,?) ");
			insertion.setInt(1, leader.getObjectId());
			insertion.setInt(2, type);
			insertion.setInt(3, team.getId());
			insertion.setString(4, name);
			insertion.setInt(5, team.getCategory());
			insertion.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, insertion);
		}
		teams.put(team.getId(), team);
		return true;
	}

	public static boolean register(L2Player player, Team team)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement insertion = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			insertion = con.prepareStatement("INSERT INTO tournament_teams (obj_id, type, team_id, team_name, leader, category) VALUES (?,?,?,?,0,?) ");
			insertion.setInt(1, player.getObjectId());
			insertion.setInt(2, getPlayerType(player.getObjectId()));
			insertion.setInt(3, team.getId());
			insertion.setString(4, team.getName());
			insertion.setInt(5, team.getCategory());
			insertion.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, insertion);
		}
		team.addMember(player.getObjectId());
		return true;
	}

	public static Boolean deleteTeam(Integer team_id)
	{
		teams.remove(team_id);
		return mysql.set("DELETE FROM tournament_teams WHERE team_id = " + team_id);
	}

	public static Boolean deleteMember(Integer team_id, Integer obj_id)
	{
		teams.get(team_id).removeMember(obj_id);
		return mysql.set("DELETE FROM tournament_teams WHERE obj_id = " + obj_id);
	}

	public static MemberInfo getMemberInfo(Integer obj_id)
	{
		MemberInfo info = new MemberInfo();
		String sql = "SELECT A.char_name, A.online, B.level, B.class_id, C.ClassName FROM characters AS A ";
		sql += "LEFT JOIN character_subclasses AS B ON (A.obj_Id = B.char_obj_id) ";
		sql += "LEFT JOIN char_templates AS C ON (B.class_id = C.ClassId) ";
		sql += "WHERE B.active = 1 AND obj_id = ?";

		ThreadConnection con = null;
		FiltredPreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement(sql);
			offline.setInt(1, obj_id);
			rs = offline.executeQuery();
			if(rs.next())
			{
				if(rs.getInt("online") == 1)
					info.online = true;
				info.name = rs.getString("char_name");
				info.level = rs.getInt("level");
				info.class_id = rs.getInt("class_id");
				info.class_name = rs.getString("ClassName");
				info.category = getCategory(info.level);
				return info;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, offline, rs);
		}
		return null;
	}

	public static Integer getPlayerType(Integer obj_id)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("SELECT B.type FROM character_subclasses AS A LEFT JOIN tournament_class_list AS B ON (A.class_id = B.class_id) WHERE A.active = 1 AND A.char_obj_id = ?");
			offline.setInt(1, obj_id);
			rs = offline.executeQuery();
			if(rs.next())
				return rs.getInt("type");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, offline, rs);
		}
		return null;
	}

	public static String getPlayerName(Integer obj_id)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("SELECT char_name FROM characters WHERE obj_Id = ?");
			offline.setInt(1, obj_id);
			rs = offline.executeQuery();
			if(rs.next())
				return rs.getString("char_name");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, offline, rs);
		}
		return null;
	}

	public static int getCategory(int level)
	{
		if(level >= 20 && level <= 29)
			return 1;
		else if(level >= 30 && level <= 39)
			return 2;
		else if(level >= 40 && level <= 51)
			return 3;
		else if(level >= 52 && level <= 61)
			return 4;
		else if(level >= 62 && level <= 75)
			return 5;
		else if(level >= 76)
			return 6;
		return 0;
	}

	public static boolean createTournamentTable(int category)
	{
		mysql.set("DELETE FROM tournament_table");

		ThreadConnection con1 = null;
		ThreadConnection con2 = null;
		FiltredPreparedStatement statement1 = null, statement2 = null;
		ResultSet rs = null;
		try
		{
			con1 = L2DatabaseFactory.getInstance().getConnection();
			con2 = L2DatabaseFactory.getInstance().getConnection();

			statement1 = con1.prepareStatement("select * from tournament_teams where leader = 1 and category = ? and status = 1");

			statement1.setInt(1, category);
			rs = statement1.executeQuery();

			int i = 0;
			while(rs.next())
				i++;
			rs.beforeFirst();

			if(i == 0)
				return true;
			else if(i == 1)
			{
				rs.next();
				int team_id = rs.getInt("team_id");
				Team team = teams.get(team_id);
				Tournament_battle.announce("Команда " + team.getName() + " выиграла турнир в категории " + category);
				Tournament_battle.giveItemsToWinner(team);
				Tournament_battle.endTournament();
				// турнир окончен
				return true;
			}

			while(rs.next())
			{
				statement2 = con2.prepareStatement("insert into tournament_table (category, team1id, team1name, team2id, team2name) VALUES (?,?,?,?,?)");

				statement2.setInt(1, category);
				statement2.setInt(2, rs.getInt("team_id"));
				statement2.setString(3, rs.getString("team_name"));

				if(rs.next())
				{
					statement2.setInt(4, rs.getInt("team_id"));
					statement2.setString(5, rs.getString("team_name"));
				}
				else
				{
					statement2.setInt(4, 0);
					statement2.setString(5, "");
				}

				statement2.executeUpdate();
				DatabaseUtils.closeStatement(statement2);
			}

			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Tournament_battle.announce("Произошла ошибка, турнир завершен.");
			return true;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con1, statement1, rs);
			DatabaseUtils.closeConnection(con2);
		}
	}

	public static boolean fillNextTeams(int category)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("SELECT * FROM tournament_table WHERE category = ?");
			offline.setInt(1, category);
			rs = offline.executeQuery();
			while(rs.next())
			{
				int team1id = rs.getInt("team1id");
				int team2id = rs.getInt("team2id");

				String team1name = rs.getString("team1name");
				String team2name = rs.getString("team2name");

				removeRecordFromTournamentTable(team1id);

				Tournament_battle.announce("Противники: '" + team1name + "' vs '" + team2name + "'");

				if(team2id == 0)
				{
					teamWin(team1id, team1name, 2); // первая выиграла
					continue;
				}

				Team team1 = teams.get(team1id);
				Team team2 = teams.get(team2id);

				if(team1 == null)
				{
					if(team2 == null)
					{
						disqualifyTeam(team1id, team1name); // первая дисквалифицирована
						disqualifyTeam(team2id, team2name); // вторая дисквалифицирована
						continue;
					}
					disqualifyTeam(team1id, team1name); // первая дисквалифицирована
					teamWin(team2id, team2name, 3); // вторая выиграла
					continue;
				}

				if(team2 == null)
				{
					disqualifyTeam(team2id, team2name); // вторая дисквалифицирована
					teamWin(team1id, team1name, 3); // первая выиграла
					continue;
				}

				if(team1.getOnlineCount() == 0 && team2.getOnlineCount() > 0)
				{
					disqualifyTeam(team1id, team1name); // первая дисквалифицирована
					teamWin(team2id, team2name, 3); // вторая выиграла
					continue;
				}

				if(team1.getOnlineCount() > 0 && team2.getOnlineCount() == 0)
				{
					disqualifyTeam(team2id, team2name); // вторая дисквалифицирована
					teamWin(team1id, team1name, 3); // первая выиграла
					continue;
				}

				if(team1.getOnlineCount() == 0 && team2.getOnlineCount() == 0)
				{
					disqualifyTeam(team1id, team1name); // первая дисквалифицирована
					disqualifyTeam(team2id, team2name); // вторая дисквалифицирована
					continue;
				}

				// обе готовы, начать бой
				Tournament_battle.team1 = team1;
				Tournament_battle.team2 = team2;

				DatabaseUtils.closeDatabaseCSR(con, offline, rs);
				return true;
			}

			// Закончились бои в этом цикле
			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, offline, rs);
		}
		return false;
	}

	public static void removeRecordFromTournamentTable(int team1id)
	{
		mysql.set("DELETE FROM tournament_table WHERE team1id = " + team1id);
	}

	public static void disqualifyTeam(int teamId, String teamName)
	{
		if(teamId > 0)
		{
			mysql.set("UPDATE tournament_teams SET status = 0 WHERE team_id = " + teamId);
			mysql.set("UPDATE tournament_teams SET losts = losts + 1 WHERE team_id = " + teamId);
			Tournament_battle.announce("Команда " + teamName + " дисквалифицирована.");
		}
	}

	public static void teamWin(int teamId, String teamName, int typeWin)
	{
		if(teamId > 0)
		{
			mysql.set("UPDATE tournament_teams SET wins = wins + 1 WHERE team_id = " + teamId);
			switch(typeWin)
			{
				case 1:
					Tournament_battle.announce("Команда " + teamName + " выиграла бой.");
					break;
				case 2:
					Tournament_battle.announce("Команда " + teamName + " выиграла, т.к. для нее нет соперника.");
					break;
				case 3:
					Tournament_battle.announce("Команда " + teamName + " выиграла, т.к. соперник дисквалифицирован.");
					break;
			}
		}
	}

	public static void teamLost(int teamId)
	{
		if(teamId > 0)
		{
			mysql.set("UPDATE tournament_teams SET status = 0 WHERE team_id = " + teamId);
			mysql.set("UPDATE tournament_teams SET losts = losts + 1 WHERE team_id = " + teamId);
		}
	}
}