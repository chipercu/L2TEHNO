package l2open.gameserver.model.entity.olympiad;

import javolution.util.FastMap;

import l2open.config.*;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.Reflection;
import l2open.util.GArray;
import l2open.util.GCSArray;
import l2open.util.Rnd;

import java.util.Collection;
import java.util.Map;

public class OlympiadManager extends l2open.common.RunnableImpl
{
	private FastMap<Integer, OlympiadGame> _olympiadInstances = new FastMap<Integer, OlympiadGame>().setShared(true);

	public void wait2(long time)
	{
		try
		{
			wait(time);
		}
		catch(InterruptedException ex)
		{}
	}

	@Override
	public synchronized void runImpl()
	{
		if(Olympiad.isOlympiadEnd() && !Olympiad.isFakeOly())
			return;

		while(Olympiad.inCompPeriod() || Olympiad.isFakeOly())
		{
			// TODO: OLY
			/*if(Olympiad._nobles.isEmpty())
			{
				wait2(60000);
				continue;
			}*/

			while(Olympiad.inCompPeriod() || Olympiad.isFakeOly())
			{
				boolean start_total=false;
				// Подготовка и запуск внеклассовых боев
				if(Olympiad._nonClassBasedRegisters.size() >= ConfigValue.NonClassGameMin)
				{
					start_total=true;
					prepareBattles(CompType.NON_CLASSED, Olympiad._nonClassBasedRegisters);
				}

				// Подготовка и запуск классовых боев
				for(Map.Entry<Integer, GCSArray<Integer>> entry : Olympiad._classBasedRegisters.entrySet())
					if(entry.getValue().size() >= ConfigValue.ClassGameMin)
					{
						start_total=true;
						prepareBattles(CompType.CLASSED, entry.getValue());
					}

				// Подготовка и запуск командных боев случайного типа
				if(Olympiad._teamRandomBasedRegisters.size() >= ConfigValue.RandomTeamGameMin)
				{
					start_total=true;
					prepareBattles(CompType.TEAM_RANDOM, Olympiad._teamRandomBasedRegisters);
				}

				// Подготовка и запуск командных боев
				if(Olympiad._teamBasedRegisters.size() >= ConfigValue.TeamGameMin)
				{
					start_total=true;
					prepareTeamBattles(CompType.TEAM, Olympiad._teamBasedRegisters.values());
				}

				if(ConfigValue.EnableOlyTotalizator && start_total)
					Functions.callScripts("communityboard.manager.OlyTotalizator", "startReg", new Object[] {});
				wait2(60000);
			}

			wait2(60000);
		}

		if(ConfigValue.OlympiadDebug1)
			System.out.println("OlympiadManager: Clear");
		Olympiad._classBasedRegisters.clear();
		Olympiad._nonClassBasedRegisters.clear();
		Olympiad._teamRandomBasedRegisters.clear();
		Olympiad._teamBasedRegisters.clear();
		Olympiad._hwidRegistered.clear();

		// when comp time finish wait for all games terminated before execute the cleanup code
		boolean allGamesTerminated = false;

		// wait for all games terminated
		while(!allGamesTerminated)
		{
			wait2(30000);

			 if(Olympiad.getCountAcitveGames() == 0)
				break;

			allGamesTerminated = true;
			for(OlympiadGame game : Olympiad.getActiveGames())
				if(game.getTask() != null && !game.getTask().isTerminated())
					allGamesTerminated = false;
		}

		Olympiad.getActiveGames().clear();
	}

	private void prepareBattles(CompType type, GCSArray<Integer> list)
	{
		for(int i = 0; i < ConfigValue.OlympiadStadiasCount; i++)
			try
			{
				if(list.size() < type.getMinSize())
					break;
				int Ollyid = Rnd.get(0, Olympiad.STADIUMS.length - 1);
				int _freeGameId = Olympiad.getFreeGameId(); 
				int id = _freeGameId;
				if(_freeGameId == -1)
					break;
				Olympiad._games[_freeGameId] = new OlympiadGame(Olympiad.getFreeGameId(), Ollyid, type, nextOpponents(list, type));
				Olympiad._games[id].sheduleTask(new OlympiadGameTask(Olympiad._games[id], BattleStatus.Begining, 0, 500L));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	}

	private void prepareTeamBattles(CompType type, Collection<GCSArray<Integer>> list)
	{
		for(int i = 0; i < ConfigValue.OlympiadStadiasCount; i++)
			try
			{
				if(list.size() < type.getMinSize())
					return;
				GCSArray<Integer> nextOpponents = nextTeamOpponents(list, type);
				if(nextOpponents == null || nextOpponents.size() != 6)
					continue;
				int Ollyid = Rnd.get(0, Olympiad.STADIUMS.length - 1);
				int _freeGameId = Olympiad.getFreeGameId();
				if(_freeGameId == -1)
					break;
				Olympiad._games[_freeGameId] = new OlympiadGame(Olympiad.getFreeGameId(), Ollyid, type, nextOpponents);
				Olympiad._games[_freeGameId].sheduleTask(new OlympiadGameTask(Olympiad._games[_freeGameId], BattleStatus.Begining, 0, 500L));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	}

	@SuppressWarnings("empty")
	private GCSArray<Integer> nextOpponents(GCSArray<Integer> list, CompType type)
	{
		if(ConfigValue.OldTypeSort == 1)
		{
			GCSArray<Integer> opponents = new GCSArray<Integer>(2);

			// Делаем 2 итерации так как нам нужно 2 игрока...
			for(int i=0;i<2;i++)
			{
				Integer noble = 0;
				int _max = 0;
				int _max2 = 0;

				// Отсеиваем игрока с максимальным количеством очков...
				for(Integer id : list)
				{
					_max = Olympiad.getNoblePoints(id); // присваиваем очки выбраного игрока, для дальнейшего сравнения...
					if(_max2 < _max) // Проверяем, если у текущего игрока очков больше чем у предыдущего то прописываем его ИД и его результат для дальнейшего сравнения...
					{
						_max2 = _max; // Новое, максимальное значение поинтов...
						noble = id; // Новый ID игрока с макс значением поинтов...
					}
				}
				// Отлично, нашли такого игрока, теперь удаляем его из списка ожидающих и добавляем в список опонентов...
				if(list.remove(noble)); // удаляем игрока из нашего списка возможных участников... P.S. if поставил, что бы оно наверняка использовало нужный нам метод ибо бывали эпическии случаи, что оно выбирало не тот метод...
				opponents.add(noble); // добавляем игрока в наш список опанентов...
				Olympiad.removeRegistration(noble); // удаляем игрока из общего списка зарегестрированых...
			}
			return opponents; // Возвращаем заветный список нубов которые будут бится...
		}
		else if(ConfigValue.OldTypeSort == 2)
		{
			GCSArray<Integer> opponents = new GCSArray<Integer>(2);
			int _min = Integer.MAX_VALUE;
			Integer _player1 = 0;
			Integer _player2 = 0;

			for(int i=0;i<list.size();i++)
				for(int i2=0;i2<list.size();i2++)
				{
					int p1 = list.get(i);
					int p2 = list.get(i2);
					int diff = Olympiad.getNoblePoints(p1) - Olympiad.getNoblePoints(p2);
					if(i != i2 && diff >=0)
					{
						if(_min > diff)
						{
							_player1 = p1;
							_player2 = p2;
							_min = diff;
						}
					}
				}
			// Отлично, нашли такого игрока, теперь удаляем его из списка ожидающих и добавляем в список опонентов...
			if(list.remove(_player1)); // удаляем игрока из нашего списка возможных участников... P.S. if поставил, что бы оно наверняка использовало нужный нам метод ибо бывали эпическии случаи, что оно выбирало не тот метод...
			if(list.remove(_player2)); // удаляем игрока из нашего списка возможных участников... P.S. if поставил, что бы оно наверняка использовало нужный нам метод ибо бывали эпическии случаи, что оно выбирало не тот метод...
			opponents.add(_player1); // добавляем игрока в наш список опонентов...
			opponents.add(_player2); // добавляем игрока в наш список опонентов...
			Olympiad.removeRegistration(_player1); // удаляем игрока из общего списка зарегестрированых...
			Olympiad.removeRegistration(_player2); // удаляем игрока из общего списка зарегестрированых...
			return opponents;
		}
		else if(ConfigValue.OldTypeSort == 3)
		{
			GCSArray<Integer> opponents = new GCSArray<Integer>(2);
			Integer noble;
			if(list.size() > 2)
			{
				//for(int i=0;i<ConfigValue.OlympiadFirstGruopCount.length;i++)
				//	parseOpponent1(list, opponents, i);
				/*GCSArray<Integer> list1 = new GCSArray<Integer>(list.size());
				GCSArray<Integer> list2 = new GCSArray<Integer>(list.size());

				for(int i=0;i<list.size();i++)
				{
					Integer pl = list.get(i);
					if(Olympiad.getNoblePoints(pl) >= ConfigValue.OlympiadFirstGruopCount)
						list1.add(pl);
					else
						list2.add(pl);
				}

				if(list1.size() > 1 || list2.size() > 1)
				{
					GCSArray<Integer> list_get = list1.size() > 1 ? list1 : list2;
					for(int i=0;i<2;i++)
					{
						int index = Rnd.get(list_get.size());
						noble = list_get.remove(index);
						if(list.remove(noble));

						opponents.add(noble);
						Olympiad.removeRegistration(noble);
					}
				}
				list1.clear();
				list2.clear();*/
			}
			else
			{
				for(int i=0;i<2;i++)
				{
					noble = list.remove(Rnd.get(list.size()));
					opponents.add(noble);
					Olympiad.removeRegistration(noble);
				}
			}
			return opponents;
		}
		else if(ConfigValue.OldTypeSort == 4)
		{
			GCSArray<Integer> opponents = new GCSArray<Integer>(2);
			Integer noble;

			GCSArray<Integer> list1 = new GCSArray<Integer>(list.size());
			GCSArray<Integer> list2 = new GCSArray<Integer>(list.size());

			for(int i=0;i<list.size();i++)
			{
				Integer pl = list.get(i);
				if(Olympiad.getNoblePoints(pl) >= ConfigValue.OlympiadFirstGruopCount)
					list1.add(pl);
				else
					list2.add(pl);
			}

			if(list1.size() > 1 || list2.size() > 1)
			{
				GCSArray<Integer> list_get = list1.size() > 1 ? list1 : list2;
				for(int i=0;i<2;i++)
				{
					int index = Rnd.get(list_get.size());
					noble = list_get.remove(index);
					if(list.remove(noble));
					opponents.add(noble);
					Olympiad.removeRegistration(noble);
				}
			}
			list1.clear();
			list2.clear();
			return opponents;
		}
		else
		{
			GCSArray<Integer> opponents = new GCSArray<Integer>(2);

			for(int i=0;i<2;i++)
			{
				Integer noble = list.remove(Rnd.get(list.size()));
				opponents.add(noble);
				Olympiad.removeRegistration(noble);
			}
			return opponents;
		}
	}

	private void parseOpponent1(GCSArray<Integer> list, GCSArray<Integer> opponents, int group_id)
	{
		/*Integer noble;

		GCSArray<Integer> list_r = new GCSArray<Integer>(list.size());
		for(int i=0;i<list.size();i++)
		{
			Integer pl = list.get(i);
			int point = Olympiad.getNoblePoints(pl);
			if(point >= ConfigValue.OlympiadFirstGruopCount[group_id][0] && point <= ConfigValue.OlympiadFirstGruopCount[group_id][1])
				list_r.add(pl);
		}
		for(int i=0;i<2;i++)
		{
			int index = Rnd.get(list_r.size());
			noble = list_r.remove(index);
			if(list.remove(noble));
				opponents.add(noble);
			Olympiad.removeRegistration(noble);
		}
		list_r.clear();*/
	}

	private GCSArray<Integer> nextTeamOpponents(Collection<GCSArray<Integer>> list, CompType type)
	{
		if(list.isEmpty())
			return null;
		GCSArray<Integer> opponents = new GCSArray<Integer>(6);
		GArray<GCSArray<Integer>> a = new GArray<GCSArray<Integer>>(list.size());
		a.addAll(list);

		for(int i = 0; i < type.getMinSize(); i++)
		{
			if(a.size() < 1)
				break;
			GCSArray<Integer> team = a.remove(Rnd.get(a.size()));
			if(team.size() == 3)
				for(Integer noble : team)
				{
					opponents.add(noble);
					Olympiad.removeRegistration(noble);
				}
			else
			{
				for(Integer noble : team)
					Olympiad.removeRegistration(noble);
				i--;
			}

			list.remove(team);
		}
		return opponents;
	}
}