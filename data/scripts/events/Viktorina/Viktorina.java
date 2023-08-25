package events.Viktorina;

import javolution.util.FastList;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.Say2;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Log;
import l2open.util.Rnd;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ScheduledFuture;

import java.util.logging.Logger;

/**
 * Эвент "Викторина"
 * Запускается автоматически во время указаное в эвенте, длится в течении времени указанного в конфиге.
 * После озвучивания вопроса, в течении определенного в конфиге времени можно дать ответ на вопрос.
 * После озвучивается победитель, и выдается награда.
 *
 */
public class Viktorina extends Functions implements ScriptFile, IVoicedCommandHandler
{
	private static final Logger _log = Logger.getLogger(Viktorina.class.getName());

	private String[] _commandList = new String[] { "o", "voff", "von", "vhelp", "vtop", "v", "vo" };
	private ArrayList<String> questions = new ArrayList<String>();
	private static ArrayList<L2Player> playerList = new ArrayList<L2Player>();
	private static List<String> hwid_list = new ArrayList<String>();
	static ScheduledFuture<?> _taskViktorinaStart;
	private static ArrayList<RewardList> _items = new ArrayList<RewardList>();
	static ScheduledFuture<?> _taskStartQuestion;
	static ScheduledFuture<?> _taskStopQuestion;
	long _timeStopViktorina = 0;
	private static boolean _status = false;
	private static boolean _questionStatus = false;
	private static int index;
	private static String question;
	private static String answer;
	private final static String GET_LIST_FASTERS = "SELECT `obj_id`,`value` FROM `character_variables` WHERE `name`='viktorinafirst' ORDER BY `value` DESC LIMIT 0,10";
	private final static String GET_LIST_TOP = "SELECT `obj_id`,`value` FROM `character_variables` WHERE `name`='viktorinaschet' ORDER BY `value` DESC LIMIT 0,10";
	private static Viktorina instance;
	private static boolean DEBUG_VIKROINA = true;

	public static Viktorina getInstance()
	{
		if(instance == null)
			instance = new Viktorina();
		return instance;
	}

	/**
	 * Загружаем базу вопросов.
	 */
	public void loadQuestions()
	{
		File file = new File(ConfigValue.DatapackRoot + "/data/scripts/events/Viktorina/questions.txt");

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while((str = br.readLine()) != null)
			{
				questions.add(str);
			}
			br.close();
			Log.add("Viktorina Event: Questions loaded", "viktorina");
		}
		catch(Exception e)
		{
			Log.add("Viktorina Event: Error parsing questions file. " + e, "viktorina");
			e.printStackTrace();
		}
	}

	/**
	 * Сохраняем вопросы обратно в файл.
	 */
	public void saveQuestions()
	{
		if(!ConfigValue.Victorina_Remove_Question)
			return;
		File file = new File(ConfigValue.DatapackRoot + "/data/scripts/events/Viktorina/questions.txt");

		try
		{
			BufferedWriter br = new BufferedWriter(new FileWriter(file));
			for(String str : questions)
				br.write(str + "\r\n");
			br.close();
			Log.add("Viktorina Event: Questions saved", "viktorina");
		}
		catch(Exception e)
		{
			Log.add("Viktorina Event: Error save questions file. " + e, "viktorina");
			e.printStackTrace();
		}
	}

	/**
	 * Готовим вопрос, вытягиваем рандомно любой вопрос с ответом.
	 */
	public void parseQuestion()
	{
		try
		{
			index = Rnd.get(questions.size());
			String str = questions.get(index);
			StringTokenizer st = new StringTokenizer(str, "|");
			question = st.nextToken();
			answer = st.nextToken();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Анонс вопроса викторины.
	 * @param text
	 */
	public void announseViktorina(String text)
	{
		Say2 cs = new Say2(0, 16, "Викторина", text);
		for(L2Player player : L2ObjectsStorage.getPlayers())
			if(player != null && !player.isInOfflineMode() && player.getVar("viktorina", "on").equals("on"))
				player.sendPacket(cs);
	}

	public void checkPlayers()
	{
		Say2 cs = new Say2(0, 16, "Викторина", "Чтобы отказаться от участия в викторине введите .voff , для справки введите .vhelp");
		for(L2Player player : L2ObjectsStorage.getPlayers())
			if(player != null && !player.isInOfflineMode() && player.getVar("viktorina") == null)
			{
				player.sendPacket(cs);
				player.setVar("viktorina", "on");
			}
	}

	public void viktorinaSay(L2Player player, String text)
	{
		Say2 cs = new Say2(0, 16, "Викторина", text);
		if(player.getVar("viktorina", "on").equals("on"))
			player.sendPacket(cs);
	}

	/**
	 * Подсчет правильно ответивших
	 */
	public void winners()
	{
		try
		{
			if(!isStatus())
			{
				Log.add("Пытался объявить победителя, но викторина оказалась выключена", "viktorina");
				return;
			}
			if(isQuestionStatus())
			{
				Log.add("Пытался объявить победителя, когда действовал вопрос.", "viktorina");
				return;
			}
			if(ServerVariables.getString("viktorinaq") == null)
				ServerVariables.set("viktorinaq", 0);
			if(ServerVariables.getString("viktorinaa") == null)
				ServerVariables.set("viktorinaa", 0);
			if(playerList.size() > 0)
			{
				announseViktorina("Правильных ответов: " + playerList.size() + ", первый ответил: " + playerList.get(0).getName() + ", правильный ответ: " + answer + "");
				ServerVariables.set("viktorinaq", ServerVariables.getInt("viktorinaq") + 1);
				ServerVariables.set("viktorinaa", ServerVariables.getInt("viktorinaa") + 1);
				if(ConfigValue.Victorina_Remove_Question)
					questions.remove(index);
				Log.add("" + playerList.get(0).getName() + "|" + playerList.size() + "|" + question + "|" + answer, "viktorina");
			}
			else
			{
				if(ConfigValue.Victorina_Remove_Question_No_Answer)
					announseViktorina("Правильного ответа не поступило, правильный ответ был:" + answer + "");
				if(!ConfigValue.Victorina_Remove_Question_No_Answer)
					announseViktorina("Правильного ответа не поступило");
				ServerVariables.set("viktorinaq", ServerVariables.getInt("viktorinaq") + 1);
				if(ConfigValue.Victorina_Remove_Question && ConfigValue.Victorina_Remove_Question_No_Answer)
					questions.remove(index);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Считам через сколько стартуем викторину, создаем пул.
	 */
	public void Start()
	{
		try
		{
			if(_taskViktorinaStart != null)
				_taskViktorinaStart.cancel(true);
			Calendar _timeStartViktorina = Calendar.getInstance();
			_timeStartViktorina.set(Calendar.HOUR_OF_DAY, ConfigValue.Victorina_Start_Time);
			_timeStartViktorina.set(Calendar.MINUTE, 0);
			_timeStartViktorina.set(Calendar.SECOND, 0);
			_timeStartViktorina.set(Calendar.MILLISECOND, 0);
			Calendar _timeStopViktorina = Calendar.getInstance();
			_timeStopViktorina.setTimeInMillis(_timeStartViktorina.getTimeInMillis());
			_timeStopViktorina.add(Calendar.HOUR_OF_DAY, ConfigValue.Victorina_Work_Time);
			long currentTime = System.currentTimeMillis();
			// Если время виторины еще не наступило
			if(_timeStartViktorina.getTimeInMillis() >= currentTime)
			{
				_taskViktorinaStart = ThreadPoolManager.getInstance().schedule(new ViktorinaStart(_timeStopViktorina.getTimeInMillis()), _timeStartViktorina.getTimeInMillis() - currentTime);
			}
			// Если как раз идет время викторины - стартуем викторину
			else if(currentTime > _timeStartViktorina.getTimeInMillis() && currentTime < _timeStopViktorina.getTimeInMillis())
			{
				_taskViktorinaStart = ThreadPoolManager.getInstance().schedule(new ViktorinaStart(_timeStopViktorina.getTimeInMillis()), 1000);
			}
			// сегодня олим уже не должен запускаться, значит нада стартовать викторину
			// на след день, прибавляем 24 часа
			else
			{
				_timeStartViktorina.add(Calendar.HOUR_OF_DAY, 24);
				_timeStopViktorina.add(Calendar.HOUR_OF_DAY, 24);
				_taskViktorinaStart = ThreadPoolManager.getInstance().schedule(new ViktorinaStart(_timeStopViktorina.getTimeInMillis()), _timeStartViktorina.getTimeInMillis() - currentTime);
			}

			if(DEBUG_VIKROINA)
				Log.add("Start Viktorina: " + _timeStartViktorina.getTime() + "|Stop Viktorina: " + _timeStopViktorina.getTime(), "viktorina");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
/**
 * Функция продолжения таймера викторины, нужна при ручной остановке викторины.
 * Назначает старт викторины на след день
 */
	public void Continue()
	{
		try
		{
			if(_taskViktorinaStart != null)
				_taskViktorinaStart.cancel(true);
			Calendar _timeStartViktorina = Calendar.getInstance();
			_timeStartViktorina.set(Calendar.HOUR_OF_DAY, ConfigValue.Victorina_Start_Time);
			_timeStartViktorina.set(Calendar.MINUTE, 0);
			_timeStartViktorina.set(Calendar.SECOND, 0);
			_timeStartViktorina.set(Calendar.MILLISECOND, 0);
			Calendar _timeStopViktorina = Calendar.getInstance();
			_timeStopViktorina.setTimeInMillis(_timeStartViktorina.getTimeInMillis());
			_timeStopViktorina.add(Calendar.HOUR_OF_DAY, ConfigValue.Victorina_Work_Time);
			_timeStartViktorina.add(Calendar.HOUR_OF_DAY, 24);
			_timeStopViktorina.add(Calendar.HOUR_OF_DAY, 24);
			long currentTime = System.currentTimeMillis();
			_taskViktorinaStart = ThreadPoolManager.getInstance().schedule(new ViktorinaStart(_timeStopViktorina.getTimeInMillis()), _timeStartViktorina.getTimeInMillis() - currentTime);
			if(DEBUG_VIKROINA)
				Log.add("Continue Viktorina: " + _timeStartViktorina.getTime() + "|Stop Viktorina: " + _timeStopViktorina.getTime(), "viktorina");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * Запуск викторины в ручную!!
	 * запускается на время указанное в настройках.
	 */
	public void ForseStart()
	{
		try
		{
			if(_taskViktorinaStart != null)
				_taskViktorinaStart.cancel(true);
			Calendar _timeStartViktorina = Calendar.getInstance();
			Calendar _timeStopViktorina = Calendar.getInstance();
			_timeStopViktorina.setTimeInMillis(_timeStartViktorina.getTimeInMillis());
			_timeStopViktorina.add(Calendar.HOUR_OF_DAY, ConfigValue.Victorina_Work_Time);
			Log.add("Викторина запущена", "viktorina");
			_taskViktorinaStart = ThreadPoolManager.getInstance().schedule(new ViktorinaStart(_timeStopViktorina.getTimeInMillis()), 1000);
			if(DEBUG_VIKROINA)
				Log.add("Start Viktorina: " + _timeStartViktorina.getTime() + "|Stop Viktorina: " + _timeStopViktorina.getTime(), "viktorina");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Стартуем викторину
	 */
	public class ViktorinaStart extends l2open.common.RunnableImpl
	{

		public ViktorinaStart(long timeStopViktorina)
		{
			_timeStopViktorina = timeStopViktorina;
		}

		public void runImpl()
		{
			try
			{
				if(isStatus())
					return;
				if(_taskStartQuestion != null)
					_taskStartQuestion.cancel(true);
				_taskStartQuestion = ThreadPoolManager.getInstance().schedule(new startQuestion(_timeStopViktorina), 5000);
				Announcements.getInstance().announceToAll("Викторина началась!");
				Announcements.getInstance().announceToAll("Для справки введите .vhelp");
				loadQuestions();
				setStatus(true);

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Задаем вопрос, ждем время, запускаем стоп вопроса.
	 */
	public class startQuestion extends l2open.common.RunnableImpl
	{
		long _timeStopViktorina = 0;

		public startQuestion(long timeStopViktorina)
		{
			_timeStopViktorina = timeStopViktorina;
		}

		public void runImpl()
		{
			try
			{
				long currentTime = Calendar.getInstance().getTimeInMillis();
				if(currentTime > _timeStopViktorina)
				{
					Log.add("Викторина закончена, закругляемся", "viktorina");
					playerList.clear();
					hwid_list.clear();
					setStatus(false);
					setQuestionStatus(false);
					if(ConfigValue.Victorina_Remove_Question)
						saveQuestions();
					announseViktorina("Время работы викторины истекло, Всем участникам приятной игры!");
					Announcements.getInstance().announceToAll("Время викторины закончилось.!");
					return;
				}
				if(!playerList.isEmpty())
				{
					Log.add("Лист правильно ответивших не пустой!", "viktorina");
					playerList.clear();
					hwid_list.clear();
					return;
				}
				if(!isStatus())
				{
					Log.add("Задаётся вопрос, а викторина не запущена.", "viktorina");
					return;
				}
				if(!isQuestionStatus())
				{
					parseQuestion();
					checkPlayers();
					announseViktorina(question);
					if(_taskStopQuestion != null)
						_taskStopQuestion.cancel(true);
					_taskStopQuestion = ThreadPoolManager.getInstance().schedule(new stopQuestion(_timeStopViktorina), ConfigValue.Victorina_Time_Answer * 1000);
					setQuestionStatus(true);
				}
				else
				{
					Log.add("Статус вопроса true", "viktorina");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Стоп вопроса: подсчитываем правильные ответы, и кто дал правильный ответ быстрее всех.
	 * запускаем следующий вопрос.
	 * @author Sevil
	 *
	 */
	public class stopQuestion extends l2open.common.RunnableImpl
	{
		long _timeStopViktorina = 0;

		public stopQuestion(long timeStopViktorina)
		{
			_timeStopViktorina = timeStopViktorina;
		}

		public void runImpl()
		{
			if(!isStatus())
			{
				Log.add("Викторина не запущена, а мы считаем победителей.", "viktorina");
				return;
			}
			setQuestionStatus(false);
			winners();
			rewarding();
			playerList.clear();
			hwid_list.clear();
			if(_taskStartQuestion != null)
				_taskStartQuestion.cancel(true);
			_taskStartQuestion = ThreadPoolManager.getInstance().schedule(new startQuestion(_timeStopViktorina), ConfigValue.Victorina_Time_Pause * 1000);
		}
	}

	/**
	 * Останавливаем эвент.
	 */
	public void stop()
	{
		playerList.clear();
		hwid_list.clear();
		if(_taskStartQuestion != null)
			_taskStartQuestion.cancel(true);
		if(_taskStopQuestion != null)
			_taskStopQuestion.cancel(true);
		setQuestionStatus(false);
		Log.add("Викторина остановлена.", "viktorina");
		if(isStatus())
			Announcements.getInstance().announceToAll("Викторина остановлена!");
		setStatus(false);
		Continue();
	}

	/**
	 * Формируем окно справки. вызывается если игрок не разу не учавствовал в викторине
	 * или командой .vhelp
	 * @param player
	 */
	public void help(L2Player player)
	{
		int schet;
		int first;
		int vq;
		int va;
		String vstatus;
		if(player.getVar("viktorinaschet") == null)
			schet = 0;
		else
			schet = Integer.parseInt(player.getVar("viktorinaschet"));

		if(player.getVar("viktorinafirst") == null)
			first = 0;
		else
			first = Integer.parseInt(player.getVar("viktorinafirst"));

		if(ServerVariables.getString("viktorinaq", "0").equals("0"))
		{
			ServerVariables.set("viktorinaq", 0);
			vq = 0;
		}
		else
			vq = Integer.parseInt(ServerVariables.getString("viktorinaq"));

		if(ServerVariables.getString("viktorinaa", "0").equals("0"))
		{
			ServerVariables.set("viktorinaa", 0);
			va = 0;
		}
		else
			va = Integer.parseInt(ServerVariables.getString("viktorinaa"));
		
		if(player.getVar("viktorina", "on").equals("on"))
			vstatus = "<font color=\"#00FF00\">Вы учавствуете в \"Викторине\"</font><br>";
		else
			vstatus = "<font color=\"#FF0000\">Вы не учавствуете в \"Викторине\"</font><br>";
		
		NpcHtmlMessage HelpReply = new NpcHtmlMessage(0);
		StringBuffer help = new StringBuffer("<html><body>");
		help.append("<center>Помощь по Викторине<br></center>");
		help.append(vstatus);
		help.append("Время начала викторины: " + ConfigValue.Victorina_Start_Time + ":00<br>");
		help.append("Длительность работы викторины " + ConfigValue.Victorina_Work_Time + " ч.<br>");
		help.append("Время в течении которого можно дать ответ: " + ConfigValue.Victorina_Time_Answer + " сек.<br>");
		help.append("Время между вопросами: " + (ConfigValue.Victorina_Time_Answer + ConfigValue.Victorina_Time_Pause) + " сек.<br>");
		help.append("Вопросов уже было заданно: " + vq + ".<br>");
		help.append("Верно ответили на : " + va + ".<br>");
		help.append("Вы верно ответили на : " + schet + ", в " + first + " вы были первым.<br>");
		help.append("<br>");
		help.append("<center>Команды викторины:<br></center>");
		help.append("<font color=\"LEVEL\">.o</font> - команда для ввода ответа на вопрос викторины<br>");
		help.append("<font color=\"LEVEL\">.von</font> - команда для включения викторины<br>");
		help.append("<font color=\"LEVEL\">.voff</font> - команда для выключения викторины<br>");
		help.append("<font color=\"LEVEL\">.vtop</font> - команда для просмотра результатов.<br>");
		help.append("<font color=\"LEVEL\">.vhelp</font> - команда для вызова этой страницы.<br>");
		help.append("<font color=\"LEVEL\">.v</font> - показывает текущий вопрос.<br>");
		help.append("</body></html>");
		HelpReply.setHtml(help.toString());
		player.sendPacket(HelpReply);
	}

	/**
	 * выводит топ
	 * @param player
	 */
	public void top(L2Player player)
	{
		NpcHtmlMessage TopReply = new NpcHtmlMessage(0);
		StringBuffer top = new StringBuffer("<html><body>");
		top.append("<center>Топ Самых Быстрых");
		top.append("<img src=\"L2UI.SquareWhite\" width=270 height=1><img src=\"L2UI.SquareBlank\" width=1 height=3>");
		final List<Scores> fasters = getList(true);
		if(fasters.size() != 0)
		{
			top.append("<table width=300 border=0 bgcolor=\"000000\">");

			int index = 1;

			for(final Scores faster : fasters)
			{
				top.append("<tr>");
				top.append("<td><center>" + index + "<center></td>");
				top.append("<td><center>" + faster.getName() + "<center></td>");
				top.append("<td><center>" + faster.getScore() + "<center></td>");
				top.append("</tr>");
				index++;
			}

			top.append("<tr><td><br></td><td></td></tr>");

			top.append("</table>");
		}
		top.append("<img src=\"L2UI.SquareWhite\" width=270 height=1> <img src=\"L2UI.SquareBlank\" width=1 height=3>");
		top.append("</center>");

		top.append("<center>Общий топ");
		top.append("<img src=\"L2UI.SquareWhite\" width=270 height=1><img src=\"L2UI.SquareBlank\" width=1 height=3>");
		final List<Scores> top10 = getList(false);
		if(top10.size() != 0)
		{
			top.append("<table width=300 border=0 bgcolor=\"000000\">");

			int index = 1;

			for(final Scores top1 : top10)
			{
				top.append("<tr>");
				top.append("<td><center>" + index + "<center></td>");
				top.append("<td><center>" + top1.getName() + "<center></td>");
				top.append("<td><center>" + top1.getScore() + "<center></td>");
				top.append("</tr>");
				index++;
			}

			top.append("<tr><td><br></td><td></td></tr>");

			top.append("</table>");
		}
		top.append("<img src=\"L2UI.SquareWhite\" width=270 height=1> <img src=\"L2UI.SquareBlank\" width=1 height=3>");
		top.append("</center>");

		top.append("</body></html>");

		TopReply.setHtml(top.toString());
		player.sendPacket(TopReply);
	}

	public void setQuestionStatus(boolean b)
	{
		_questionStatus = b;
	}

	public boolean isQuestionStatus()
	{
		return _questionStatus;
	}

	@Override
	public void onLoad()
	{
		if(ConfigValue.Victorina_Enable)
			Start();
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
		Log.add("Викторина загружена", "viktorina");
		_log.info("Loaded Event: Victorina");
	}

	@Override
	public void onReload()
	{
		stop();
	}

	@Override
	public void onShutdown()
	{
		stop();

	}

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	@Override
	public boolean useVoicedCommand(String command, L2Player player, String args)
	{
		if(command.equals("o"))
		{
			if(hwid_list.contains(player.getHWIDs()))
			{
				viktorinaSay(player, "Вы уже отправили ответ на вопрос.");
				return true;
			}
			if(args.equalsIgnoreCase(answer) && isQuestionStatus())
			{
				if(!playerList.contains(player))
				{
					hwid_list.add(player.getHWIDs());
					playerList.add(player);
					viktorinaSay(player, "Ваш ответ принят.");
				}
				Log.add("preprepls " + playerList + "", "viktorina");
			}
			if(!isQuestionStatus())
				viktorinaSay(player, "Возможно вопрос не был задан, или время ответа истекло");
		}
		if(command.equals("von"))
		{
			player.setVar("viktorina", "on");
		}
		if(command.equals("voff"))
		{
			player.setVar("viktorina", "off");
		}
		if(command.equals("vhelp"))
		{
			help(player);
		}
		if(command.equals("vtop"))
		{
			top(player);
		}
		if(command.equals("v"))
		{
			viktorinaSay(player, question);
		}
		if(command.equals("vo") && player.isGM())
		{
			viktorinaSay(player, answer);
		}
		return true;
	}

	/**
	 *выдача награды, начисление очков.
	 */
	private void rewarding()
	{
		if(!isStatus())
		{
			Log.add("Пытался вручить награды, но викторина оказалась выключена", "viktorina");
			return;
		}
		if(isQuestionStatus())
		{
			Log.add("Пытался вручить награды, когда действовал вопрос.", "viktorina");
			return;
		}
		
		parseReward();
		int schet;
		int first;
		for(L2Player player : playerList)
		{
			if(player.getVar("viktorinaschet") == null)
				schet = 0;
			else
				schet = Integer.parseInt(player.getVar("viktorinaschet"));
			if(player.getVar("viktorinafirst") == null)
				first = 0;
			else
				first = Integer.parseInt(player.getVar("viktorinafirst"));
			if(player == playerList.get(0))
			{
				giveItemByChance(player, true);
				player.setVar("viktorinafirst", "" + (first + 1) + "");
			}
			else
				giveItemByChance(player, false);
			player.setVar("viktorinaschet", "" + (schet + 1) + "");
		}
	}

	/**
	 * парсим конфиг наград
	 */
	private void parseReward()
	{
		_items.clear();
		StringTokenizer st = new StringTokenizer(ConfigValue.Victorina_Reward_First, ";");
		StringTokenizer str = new StringTokenizer(ConfigValue.Victorina_Reward_Other, ";");
		while(st.hasMoreTokens())
		{
			String str1 = st.nextToken();
			StringTokenizer str2 = new StringTokenizer(str1, ",");
			final int itemId = Integer.parseInt(str2.nextToken());
			final int count = Integer.parseInt(str2.nextToken());
			final int chance = Integer.parseInt(str2.nextToken());
			final boolean first = true;
			final RewardList item = new RewardList();
			item.setProductId(itemId);
			item.setCount(count);
			item.setChance(chance);
			item.setFirst(first);
			_items.add(item);
		}
		while(str.hasMoreTokens())
		{
			String str1 = str.nextToken();
			StringTokenizer str2 = new StringTokenizer(str1, ",");
			final int itemId = Integer.parseInt(str2.nextToken());
			final int count = Integer.parseInt(str2.nextToken());
			final int chance = Integer.parseInt(str2.nextToken());
			final boolean first = false;
			final RewardList item = new RewardList();
			item.setProductId(itemId);
			item.setCount(count);
			item.setChance(chance);
			item.setFirst(first);
			_items.add(item);
		}
	}

	/**
	 * Выдаем приз на которую укажет шанс + определяем выдавать приз для первого или для остальных
	 * @param player
	 * @param first
	 * @return
	 */
	private boolean giveItemByChance(L2Player player, boolean first)
	{
		int chancesumm = 0;
		int productId = 0;
		int chance = Rnd.get(0, 100);
		int count = 0;
		for(RewardList items : _items)
		{
			chancesumm = chancesumm + items.getChance();
			if(first == items.getFirst() && chancesumm > chance)
			{
				productId = items.getProductId();
				count = items.getCount();
				player.getInventory().addItem(productId, count);
				if(count > 1)
					player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S2_S1S).addItemName(productId).addNumber(count));
				else
					player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1).addItemName(productId));
				if(DEBUG_VIKROINA)
					Log.add("" + player.getName() + "|" + productId + ":" + count + ":" + items.getChance() + ":" + items.getFirst() + "", "viktorina");
				return true;
			}
		}
		return true;
	}

	private class RewardList
	{
		public int _productId;
		public int _count;
		public int _chance;
		public boolean _first;

		private void setProductId(int productId)
		{
			_productId = productId;
		}

		private void setChance(int chance)
		{
			_chance = chance;
		}

		private void setCount(int count)
		{
			_count = count;
		}

		private void setFirst(boolean first)
		{
			_first = first;
		}

		private int getProductId()
		{
			return _productId;
		}

		private int getChance()
		{
			return _chance;
		}

		private int getCount()
		{
			return _count;
		}

		private boolean getFirst()
		{
			return _first;
		}
	}

	private boolean isStatus()
	{
		return _status;
	}

	private void setStatus(boolean status)
	{
		_status = status;
	}

	/**
	 * Возвращаем имя чара по его obj_Id
	 * @param char_id
	 * @return
	 */
	private String getName(int char_id)
	{
		String name = null;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT char_name FROM characters WHERE obj_Id=?");
			statement.setInt(1, char_id);
			rset = statement.executeQuery();
			rset.next();
			name = rset.getString("char_name");
			return name;
		}
		catch(final Exception e)
		{
			Log.add("Игрок не найден с таким obj_Id:" + e.getMessage(), "viktorina");
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return name;
	}
	/**
	 * Возвращаем лист имен.
	 * @param first
	 * @return
	 */
	private List<Scores> getList(final boolean first)
	{
		final List<Scores> names = new FastList<Scores>();

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		String GET_LIST = null;
		if(first)
			GET_LIST = GET_LIST_FASTERS;
		else
			GET_LIST = GET_LIST_TOP;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(GET_LIST);
			rset = statement.executeQuery();

			while(rset.next())
			{
				final String name = (getName(rset.getInt("obj_id")));
				final int score = rset.getInt("value");
				Scores scores = new Scores();
				scores.setName(name);
				scores.setScore(score);
				names.add(scores);
			}
			return names;
		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return names;
	}

	private class Scores
	{
		public String _name;
		public int _score;

		private void setName(String name)
		{
			_name = name;
		}

		private void setScore(int score)
		{
			_score = score;
		}

		private String getName()
		{
			return _name;
		}

		private int getScore()
		{
			return _score;
		}
	}

}