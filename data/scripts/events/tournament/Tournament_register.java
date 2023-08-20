package events.tournament;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.util.Strings;
import l2open.util.Util;

public class Tournament_register extends Functions implements ScriptFile
{
	private static boolean active = true;

	public void bypass_listOfTeams()
	{
		String out = "<table><tr><td width=80>Категория</td><td width=80>Название</td><td width=60>Размер</td></tr>";
		if(Tournament_data.getTeams().isEmpty())
			out += "<tr><td>Нет команд</td><td></td><td></td></tr>";
		else
			for(Team team : Tournament_data.getTeams().values())
			{
				out += "<tr><td>" + team.getCategory() + "</td>";
				out += "<td><a action=\"bypass -h scripts_events.tournament.Tournament_register:bypass_teamInfo " + team.getId() + "\">" + team.getName() + "</a></td>";
				out += "<td>" + team.getCount() + "</td></tr>";
			}
		out += "</table>";
		show(out, (L2Player) getSelf());
	}

	public void bypass_listOfTeamsForRegister()
	{
		L2Player player = (L2Player) getSelf();
		int category = Tournament_data.getCategory(player.getLevel());
		String out = "Выберите команду:<br><table><tr><td width=80>Название</td><td width=60>Размер</td></tr>";
		if(Tournament_data.getTeams().isEmpty())
			out += "<tr><td>Нет команд</td><td></td><td></td></tr>";
		else
			for(Team team : Tournament_data.getTeams().values())
				if(team.getCategory() == category && team.getCount() < 3)
				{
					out += "<tr>";
					out += "<td><a action=\"bypass -h scripts_events.tournament.Tournament_register:bypass_teamInfo " + team.getId() + "\">" + team.getName() + "</a></td>";
					out += "<td>" + team.getCount() + "</td></tr>";
				}
		out += "</table>";
		show(out, (L2Player) getSelf());
	}

	public static void bypass_tournamentInfo()
	{
	// TODO Здесь должна отображаться турнирная таблица
	}

	public void bypass_teamInfo(String[] var)
	{
		L2Player player = (L2Player) getSelf();
		if(var.length != 1)
		{
			show("Некорректные данные.", player);
			return;
		}

		Integer team_id;
		try
		{
			team_id = Integer.valueOf(var[0]);
		}
		catch(Exception e)
		{
			show("Некорректные данные.", player);
			return;
		}

		Team team;
		if(team_id > 0)
		{
			team = Tournament_data.getTeamById(team_id);
			if(team == null)
			{
				show("Такой команды не существует.", player);
				return;
			}
		}
		else
		{
			team = getTeamByPlayer(player);
			if(team == null)
			{
				show("Вы не зарегистрированы.", player);
				return;
			}
		}
		String out = "Информация о команде:<br>";
		out += "<br>Название: " + team.getName();
		out += "<br>Лидер: " + Tournament_data.getPlayerName(team.getLeader());
		out += "<br>Категория: " + team.getCategory();
		out += "<br>Состав:<br>";
		out += "<table><tr><td width=80>Имя</td><td width=60>Уровень</td><td width=100>Класс</td><td width=60></td></tr>";
		for(int member : team.getMembers())
		{
			MemberInfo info = Tournament_data.getMemberInfo(member);
			out += "<tr><td>" + info.name + "</td>";
			out += "<td>" + info.level + "</td>";
			out += "<td>" + info.class_name + "</td>";
			if(team.getLeader() == player.getObjectId() && player.getObjectId() != member)
				out += "<td><a action=\"bypass -h scripts_events.tournament.Tournament_register:bypass_deleteMemberByLeader " + member + "\">Выгнать</a></td></tr>";
			else
				out += "<td></td></tr>";
		}
		out += "</table>";
		if(!isRegistered(player))
			out += "<br><a action=\"bypass -h scripts_events.tournament.Tournament_register:bypass_register " + team.getId() + "\">Зарегистрироваться</a>";
		else if(team.getLeader() == player.getObjectId())
			out += "<br><a action=\"bypass -h scripts_events.tournament.Tournament_register:bypass_deleteTeam\">Удалить команду</a>";
		else
			out += "<br><a action=\"bypass -h scripts_events.tournament.Tournament_register:bypass_leave\">Выйти из команды</a>";
		show(out, player);
	}

	public void bypass_createTeam()
	{
		if(!active)
		{
			show("Извините, но периуд регистрации окончен. Дождитесь окончания турнира.", (L2Player) getSelf());
			return;
		}
		show("data/scripts/events/tournament/32130-1.html", (L2Player) getSelf());
	}

	public synchronized void bypass_createTeam(String[] var)
	{
		if(!active)
		{
			show("Извините, но периуд регистрации окончен. Дождитесь окончания турнира.", (L2Player) getSelf());
			return;
		}
		L2Player player = (L2Player) getSelf();
		if(var.length != 1)
		{
			show("Некорректные данные.", player);
			return;
		}

		String team_name = var[0];

		if(!Util.isMatchingRegexp(team_name, ConfigValue.ClanNameTemplate))
		{
			show("Некорректный ввод.", player);
			return;
		}
		if(team_name.equals(""))
		{
			show("Заполните поле 'название команды'.", player);
			return;
		}
		team_name = Strings.addSlashes(team_name);
		if(Tournament_data.getTeamByName(team_name) != null)
		{
			show("Команда с таким именем уже существует.", player);
			return;
		}
		if(isRegistered(player))
		{
			show("Вы уже зарегистрированы.", player);
			return;
		}
		if(Tournament_data.createTeam(team_name, player))
			show("Команда зарегистрирована. Для участия в турнире, вам потребуется еще 2 игрока в команду.", player);
		else
			show("Произошла ошибка. Попробуйте еще раз.", player);
	}

	public synchronized void bypass_register(String[] var)
	{
		if(!active)
		{
			show("Извините, но периуд регистрации окончен. Дождитесь окончания турнира.", (L2Player) getSelf());
			return;
		}
		L2Player player = (L2Player) getSelf();
		if(var.length != 1)
		{
			show("Некорректные данные.", player);
			return;
		}

		Integer team_id;
		try
		{
			team_id = Integer.valueOf(var[0]);
		}
		catch(Exception e)
		{
			show("Некорректные данные.", player);
			return;
		}

		Team team = Tournament_data.getTeamById(team_id);
		if(team == null)
		{
			show("Такой команды не существует.", player);
			return;
		}
		if(team.getCount() > 2)
		{
			show("Команда переполнена.", player);
			return;
		}
		if(isRegistered(player))
		{
			show("Вы уже зарегистрированы.", player);
			return;
		}
		if(!checkCategory(player, team))
		{
			show("Вы не подходите по уровню.", player);
			return;
		}
		if(!checkType(player, team))
		{
			show("Вы не подходите по профессии.", player);
			return;
		}
		if(Tournament_data.register(player, team))
			show("Вы зарегистрировались в команде " + team.getName(), player);
		else
			show("Произошла ошибка. Попробуйте еще раз.", player);
	}

	public synchronized void bypass_deleteTeam()
	{
		if(!active)
		{
			show("Извините, но периуд регистрации окончен. Дождитесь окончания турнира.", (L2Player) getSelf());
			return;
		}
		L2Player player = (L2Player) getSelf();
		Team team = getTeamByPlayer(player);
		if(team == null)
		{
			show("Вы не зарегистрированы.", player);
			return;
		}
		if(team.getLeader() != player.getObjectId())
		{
			show("Только лидер может удалить команду.", player);
			return;
		}
		if(Tournament_data.deleteTeam(team.getId()))
			show("Команда удалена.", player);
		else
			show("Произошла ошибка. Попробуйте еще раз.", player);
	}

	public synchronized void bypass_deleteMemberByLeader(String[] var)
	{
		if(!active)
		{
			show("Извините, но периуд регистрации окончен. Дождитесь окончания турнира.", (L2Player) getSelf());
			return;
		}
		L2Player player = (L2Player) getSelf();
		if(var.length != 1)
		{
			show("Некорректные данные.", player);
			return;
		}

		Integer obj_id;
		try
		{
			obj_id = Integer.valueOf(var[0]);
		}
		catch(Exception e)
		{
			show("Некорректные данные.", player);
			return;
		}

		Team team = getTeamByPlayer(player);
		if(team == null)
		{
			show("Вы не зарегистрированы.", player);
			return;
		}
		if(team.getLeader() != player.getObjectId())
		{
			show("Вы не являетесь лидером.", player);
			return;
		}
		if(Tournament_data.deleteMember(team.getId(), obj_id))
			show("Игрок удален из команды.", player);
		else
			show("Произошла ошибка. Попробуйте еще раз.", player);
	}

	public void bypass_leave()
	{
		if(!active)
		{
			show("Извините, но периуд регистрации окончен. Дождитесь окончания турнира.", (L2Player) getSelf());
			return;
		}
		L2Player player = (L2Player) getSelf();
		Team team = getTeamByPlayer(player);
		if(team == null)
		{
			show("Вы не зарегистрированы.", player);
			return;
		}
		if(Tournament_data.deleteMember(team.getId(), player.getObjectId()))
			show("Вы вышли из команды.", player);
		else
			show("Произошла ошибка. Попробуйте еще раз.", player);
	}

	public static Boolean isRegistered(L2Player player)
	{
		for(Team team : Tournament_data.getTeams().values())
			for(int obj_id : team.getMembers())
				if(player.getObjectId() == obj_id)
					return true;
		return false;
	}

	public static Team getTeamByPlayer(L2Player player)
	{
		for(Team team : Tournament_data.getTeams().values())
			for(int obj_id : team.getMembers())
				if(player.getObjectId() == obj_id)
					return team;
		return null;
	}

	public static boolean checkCategory(L2Player player, Team team)
	{
		Integer category = Tournament_data.getCategory(player.getLevel());
		if(team.getCategory() != category)
			return false;
		return true;
	}

	public static boolean checkType(L2Player player, Team team)
	{
		Integer type = Tournament_data.getPlayerType(player.getObjectId());
		for(int obj_id : team.getMembers())
			if(type == Tournament_data.getPlayerType(obj_id))
				return false;
		return true;
	}

	public static void endRegistration()
	{
		active = false;
	}

	public static void endTournament()
	{
		active = true;
	}
	
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}