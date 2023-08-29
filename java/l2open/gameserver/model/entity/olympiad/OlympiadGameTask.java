package l2open.gameserver.model.entity.olympiad;

import l2open.config.*;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.Say2;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Log;

import java.util.concurrent.ScheduledFuture;

public class OlympiadGameTask extends l2open.common.RunnableImpl
{
	private OlympiadGame _game;
	private BattleStatus _status;
	private int _count;
	private long _time;

	private boolean _terminated = false;

	public boolean isTerminated()
	{
		return _terminated;
	}

	public BattleStatus getStatus()
	{
		return _status;
	}

	public int getCount()
	{
		return _count;
	}

	public OlympiadGame getGame()
	{
		return _game;
	}

	public long getTime()
	{
		return _count;
	}

	public ScheduledFuture<?> shedule()
	{
		return ThreadPoolManager.getInstance().schedule(this, _time);
	}

	public OlympiadGameTask(OlympiadGame game, BattleStatus status, int count, long time)
	{
		_game = game;
		_status = status;
		_count = count;
		_time = time;
	}

	@Override
	public void runImpl()
	{
		if(_game == null || _terminated)
			return;

		OlympiadGameTask task = null;

		int gameId = _game.getId();

		try
		{
			//if(!Olympiad.inCompPeriod() && !Olympiad.isFakeOly() && _status != BattleStatus.ValidateWinner && _status != BattleStatus.Ending)
			//	return;

			// Прерываем игру, если один из игроков не онлайн, и игра еще не прервана
			// проверять не обоих игроков, а только того кто вышел...
			if(!_game.checkPlayersOnline() && _status != BattleStatus.ValidateWinner && _status != BattleStatus.Ending)
			{
				Log.add("Player is offline for game " + gameId + ", status: " + _status, "olympiad");
				_game.endGame(5000, true, null);
				return;
			}

			switch(_status)
			{
				case Begining:
				{
					int startTime = ConfigValue.TleportToArena;
					int nextTime = startTime < 5 ? 5 : 15 > startTime ? 15 : 30 > startTime ? 30 : 60 <= startTime ? 60 : 1;
					if(ConfigValue.TleportToArena > 60)
						_game.broadcastPacket(new SystemMessage(SystemMessage.YOU_WILL_ENTER_THE_OLYMPIAD_STADIUM_IN_S1_SECOND_S).addNumber(startTime), true, false);
					startTime -= nextTime;
					task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, nextTime, startTime * 1000);
					break;
				}
				case Begin_Countdown:
				{
					_game.broadcastPacket(new SystemMessage(SystemMessage.YOU_WILL_ENTER_THE_OLYMPIAD_STADIUM_IN_S1_SECOND_S).addNumber(_count), true, false);
					switch (_count)
					{
						case 60:
							task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, _count / 2, _count / 2 * 1000);
							break;
						case 30:
							task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, _count / 2, _count / 2 * 1000);
							break;
						case 15:
							task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 5, 10000);
							break;
						case 1:
							task = new OlympiadGameTask(_game, BattleStatus.PortPlayers, 0, 1000);
							break;
						default:
							task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, _count - 1, 1000);
					}
					break;
				}
				case PortPlayers:
				{
					_game.portPlayersToArena();
					_game.managerShout();
					task = new OlympiadGameTask(_game, BattleStatus.Started, ConfigValue.StartBattle, 1000);
					break;
				}
				case Started55:
				{
					_game.regenPlayers();
					_count -= 5;
					task = new OlympiadGameTask(_game, BattleStatus.Started, _count, 5000);

					//TODO FUZZY  инфо о противнике
					try{
						for (int i = 0; i < _game.getTeam(1).getPlayers().size(); i++) {
							L2Player player1 = _game.getTeam(1).getPlayers().get(i);
							L2Player player2 = _game.getTeam(2).getPlayers().get(i);
							player1.sendPacket(new Say2(0, Say2C.CRITICAL_ANNOUNCEMENT, "", "---------------------------"));
							player1.sendPacket(new Say2(0, Say2C.CRITICAL_ANNOUNCEMENT, "", "Ваш противник:"));
							player1.sendPacket(new Say2(0, Say2C.CRITICAL_ANNOUNCEMENT, "", "Имя: " + player2.getName()));
							player1.sendPacket(new Say2(0, Say2C.CRITICAL_ANNOUNCEMENT, "", "Класс: " + player2.getClassId().name().toUpperCase()));
							player1.sendPacket(new Say2(0, Say2C.CRITICAL_ANNOUNCEMENT, "", "---------------------------"));

							player2.sendPacket(new Say2(0, Say2C.CRITICAL_ANNOUNCEMENT, "", "---------------------------"));
							player2.sendPacket(new Say2(0, Say2C.CRITICAL_ANNOUNCEMENT, "", "Ваш противник:"));
							player2.sendPacket(new Say2(0, Say2C.CRITICAL_ANNOUNCEMENT, "", "Имя: " + player1.getName()));
							player2.sendPacket(new Say2(0, Say2C.CRITICAL_ANNOUNCEMENT, "", "Класс: " + player1.getClassId().name().toUpperCase()));
							player2.sendPacket(new Say2(0, Say2C.CRITICAL_ANNOUNCEMENT, "", "---------------------------"));

						}
					}catch (Exception e){
						System.out.println("Ошибка при показе информации о противнике");
					}
					//TODO FUZZY


					break;
				}
				case Started:
				{
					if(_count == ConfigValue.StartBattle)
					{
						_game.setState(1);
						_game.preparePlayers();
						_game.addBuffers();
						_game.regenPlayers();

						_game.broadcastPacket(new SystemMessage(SystemMessage.THE_GAME_WILL_START_IN_S1_SECOND_S).addNumber(_count), true, true);
						_count -= 5;
						task = new OlympiadGameTask(_game, BattleStatus.Started55, _count, 5000);
						break;
					}
					_game.broadcastPacket(new SystemMessage(SystemMessage.THE_GAME_WILL_START_IN_S1_SECOND_S).addNumber(_count), true, true);
					_count -= 10;

					if(_count > 0)
					{
						task = new OlympiadGameTask(_game, BattleStatus.Started, _count, 10000);
						break;
					}

					if(ConfigValue.EnableOlyTotalizator)
						Functions.callScripts("communityboard.manager.OlyTotalizator", "endReg", new Object[] {});

					_game.openDoors();
					task = new OlympiadGameTask(_game, BattleStatus.CountDown, 5, 5000);
					break;
				}
				case CountDown:
				{
					_game.broadcastPacket(new SystemMessage(SystemMessage.THE_GAME_WILL_START_IN_S1_SECOND_S).addNumber(_count), true, true);
					_count--;
					if(_count <= 0)
					{
						task = new OlympiadGameTask(_game, BattleStatus.StartComp, 30, 1000);
						_game.deleteBuffers();
					}
					else
						task = new OlympiadGameTask(_game, BattleStatus.CountDown, _count, 1000);
					break;
				}
				case StartComp:
				{
					if(_count == 30)
					{
						_game.setState(2);
						_game.broadcastPacket(Msg.STARTS_THE_GAME, true, true);
						_game.broadcastInfo(null, null, false);
						_game.sendRelation();
					}
					else if(_count == 12)
						_game.broadcastPacket(new SystemMessage(SystemMessage.THE_GAME_WILL_END_IN_S1_SECONDS).addNumber(120), true, false);
					else if(_count == 6)
						_game.broadcastPacket(new SystemMessage(SystemMessage.THE_GAME_WILL_END_IN_S1_SECONDS).addNumber(60), true, false);
					else if(_count == 3)
						_game.broadcastPacket(new SystemMessage(SystemMessage.THE_GAME_WILL_END_IN_S1_SECONDS).addNumber(30), true, false);
					else if(_count == 1)
						_game.broadcastPacket(new SystemMessage(SystemMessage.THE_GAME_WILL_END_IN_S1_SECONDS).addNumber(10), true, false);
					else if(_count == 50)
						_game.broadcastPacket(new SystemMessage(SystemMessage.THE_GAME_WILL_END_IN_S1_SECONDS).addNumber(5), true, false);
					_count--;
					if(_count == 49)
						task = new OlympiadGameTask(_game, BattleStatus.ValidateWinner, 0, 5000);
					else if(_count == 0)
						task = new OlympiadGameTask(_game, BattleStatus.StartComp, 50, 5000);
					else
						task = new OlympiadGameTask(_game, BattleStatus.StartComp, _count, 10000);
					break;
				}
				case ValidateWinner:
				{
					try
					{
						_game.validateWinner(_count > 0, null);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					task = new OlympiadGameTask(_game, BattleStatus.Ending, 0, 20000);
					break;
				}
				case Ending:
				{
					_game.portPlayersBack();
					_game.clearSpectators();
					_game.deleteBuffers();
					Olympiad._games[_game.getId()] = null;
					_terminated = true;
					return;
				}
			}

			if(task == null)
			{
				Log.add("task == null for game " + gameId, "olympiad");
				Thread.dumpStack();
				_game.endGame(5000, true, null);
				return;
			}

			_game.sheduleTask(task);
		}
		catch(Exception e)
		{
			Log.add("Error for game " + gameId + " :" + e.getMessage(), "olympiad");
			e.printStackTrace();
			_game.endGame(5000, true, null);
		}
	}
}



