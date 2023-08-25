package l2open.util;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.Stat;
import l2open.gameserver.taskmanager.MemoryWatchDog;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.rrd4j.core.Util;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.logging.Logger;

import static org.rrd4j.ConsolFun.AVERAGE;
import static org.rrd4j.ConsolFun.MAX;

public class RRDTools
{
	private static volatile boolean _inited = false;
	public static int update_count;

	protected static Logger _log = Logger.getLogger(RRDTools.class.getName());

	public final static void init()
	{
		if(_inited)
			return;
		_inited = true;

		File DbFile;
		RrdDb parentDb;

		try
		{
			DbFile = new File(ConfigValue.RRDPath + "main.rrd");
			if(!DbFile.exists())
			{
				RrdDef def = new RrdDef(ConfigValue.RRDPath + "main.rrd", Util.getTimestamp(), 60); // 60 это step, шаг отсчета в секундах
				def.addDatasource("DS:online:GAUGE:120:0:U"); // 120 это heartbeat, максимальный интервал между отчетами при котором не будет разрыва в графике, рекомендуется 2*step
				def.addArchive(ConsolFun.AVERAGE, 0.5, 1, 28 * 24 * 60); // поминутная точность хранится месяц (28 суток)
				def.addArchive(ConsolFun.AVERAGE, 0.5, 60, 12 * 28 * 24); // почасовая точность хранится год (12 месяцев по 28 суток)
				def.addArchive(ConsolFun.AVERAGE, 0.5, 1440, 10 * 12 * 28); // посуточная информация хранится 10 лет
				def.addArchive(ConsolFun.AVERAGE, 0.5, 10080, 30 * 12 * 4); // понедельная информация хранится 30 лет (из расчета 4 недель в месяце и 48 недель в году)
				def.addArchive(ConsolFun.MAX, 0.5, 1, 40320); // цифра 1 означает что в ячейке хранится 1 отчет, 40320 это число ячеек
				def.addArchive(ConsolFun.MAX, 0.5, 60, 8064);
				def.addArchive(ConsolFun.MAX, 0.5, 1440, 3360);
				def.addArchive(ConsolFun.MAX, 0.5, 10080, 1440);
				parentDb = new RrdDb(def);
				parentDb.close();
			}
		}
		catch(IOException e)
		{
			_log.warning("Unable to init main rrd:");
			e.printStackTrace();
		}

		try
		{
			DbFile = new File(ConfigValue.RRDPath + "memory.rrd");
			if(!DbFile.exists())
			{
				RrdDef def = new RrdDef(ConfigValue.RRDPath + "memory.rrd", Util.getTimestamp(), 60);
				def.addDatasource("DS:memory:GAUGE:120:0:U");
				def.addArchive(ConsolFun.AVERAGE, 0.5, 1, 28 * 24 * 60); // поминутная точность хранится месяц (28 суток)
				def.addArchive(ConsolFun.AVERAGE, 0.5, 60, 12 * 28 * 24); // почасовая точность хранится год (12 месяцев по 28 суток)
				def.addArchive(ConsolFun.AVERAGE, 0.5, 1440, 10 * 12 * 28); // посуточная информация хранится 10 лет
				def.addArchive(ConsolFun.AVERAGE, 0.5, 10080, 30 * 12 * 4); // понедельная информация хранится 30 лет (из расчета 4 недель в месяце и 48 недель в году)
				def.addArchive(ConsolFun.MAX, 0.5, 1, 40320);
				def.addArchive(ConsolFun.MAX, 0.5, 60, 8064);
				def.addArchive(ConsolFun.MAX, 0.5, 1440, 3360);
				def.addArchive(ConsolFun.MAX, 0.5, 10080, 1440);
				parentDb = new RrdDb(def);
				parentDb.close();
			}
		}
		catch(IOException e)
		{
			_log.warning("Unable to init memory rrd:");
			e.printStackTrace();
		}

		if(ConfigValue.UseExtendedRRD)
			try
			{
				DbFile = new File(ConfigValue.RRDPath + "extended.rrd");
				if(!DbFile.exists())
				{
					RrdDef def = new RrdDef(ConfigValue.RRDPath + "extended.rrd", Util.getTimestamp(), 60);
					def.addDatasource("DS:adena:GAUGE:120:0:U");
					def.addDatasource("DS:avglevel:GAUGE:120:0:U");
					def.addArchive(ConsolFun.AVERAGE, 0.5, 1, 28 * 24 * 60); // поминутная точность хранится месяц (28 суток)
					def.addArchive(ConsolFun.AVERAGE, 0.5, 60, 12 * 28 * 24); // почасовая точность хранится год (12 месяцев по 28 суток)
					def.addArchive(ConsolFun.AVERAGE, 0.5, 1440, 10 * 12 * 28); // посуточная информация хранится 10 лет
					def.addArchive(ConsolFun.AVERAGE, 0.5, 10080, 30 * 12 * 4); // понедельная информация хранится 30 лет (из расчета 4 недель в месяце и 48 недель в году)
					def.addArchive(ConsolFun.MAX, 0.5, 1, 40320);
					def.addArchive(ConsolFun.MAX, 0.5, 60, 8064);
					def.addArchive(ConsolFun.MAX, 0.5, 1440, 3360);
					def.addArchive(ConsolFun.MAX, 0.5, 10080, 1440);
					parentDb = new RrdDb(def);
					parentDb.close();
				}
			}
			catch(IOException e)
			{
				_log.warning("Unable to init extended rrd:");
				e.printStackTrace();
			}

		ThreadPoolManager.getInstance().schedule(new updateTask(), getUpdateTime());
	}

	/**
	 * Рисует график
	 *	<br>
	 * @param header - название графика
	 * @param suffix - суффикс названия файла ("1h")
	 * @param time - время в секундах
	 */
	public static void draw(String header, String suffix, long time)
	{
		RrdGraphDef gDef;

		// Онлайн
		gDef = createRrdGraphDef("online" + suffix, "Online " + header, -time);
		gDef.datasource("avg", ConfigValue.RRDPath + "main.rrd", "online", AVERAGE);
		gDef.datasource("max", ConfigValue.RRDPath + "main.rrd", "online", MAX);
		gDef.area("max", getAreaColor(), "online");
		gDef.line("max", getLineColor(), "online", ConfigValue.LineWidth);
		gDef.gprint("max", MAX, "max online = %.0f");
		gDef.gprint("avg", AVERAGE, "average online = %.1f");
		try
		{
			new RrdGraph(gDef);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		// Память
		gDef = createRrdGraphDef("memory" + suffix, "Memory usage " + header, -time);
		gDef.setBase(1024);
		gDef.datasource("avg", ConfigValue.RRDPath + "memory.rrd", "memory", AVERAGE);
		gDef.datasource("max", ConfigValue.RRDPath + "memory.rrd", "memory", MAX);
		gDef.area("max", getAreaColor(), "memory usage");
		//gDef.line("max", getLineColor(), "memory usage", ConfigValue.LineWidth);
		gDef.gprint("max", MAX, "max = %.0f");
		gDef.gprint("avg", AVERAGE, "average = %.1f");
		try
		{
			new RrdGraph(gDef);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		if(ConfigValue.UseExtendedRRD)
			edraw(header, suffix, time);
	}

	public static void edraw(String header, String suffix, long time)
	{
		RrdGraphDef gDef;

		// Адены
		gDef = createRrdGraphDef("adena" + suffix, "Adena " + header, -time);
		gDef.datasource("avg", ConfigValue.RRDPath + "extended.rrd", "adena", AVERAGE);
		gDef.datasource("max", ConfigValue.RRDPath + "extended.rrd", "adena", MAX);
		gDef.area("max", getAreaColor(), null);
		gDef.line("max", getLineColor(), null, ConfigValue.LineWidth);
		gDef.gprint("max", MAX, "max = %,.0f");
		gDef.gprint("avg", AVERAGE, "average = %,.0f");
		try
		{
			new RrdGraph(gDef);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		// Средний уровень
		/*
		gDef = createRrdGraphDef("avglvl" + suffix, "Average level " + header, -time);
		gDef.datasource("avg", ConfigValue.RRDPath + "extended.rrd", "avglevel", AVERAGE);
		gDef.datasource("max", ConfigValue.RRDPath + "extended.rrd", "avglevel", MAX);
		gDef.area("max", getAreaColor(), null);
		gDef.line("max", getLineColor(), null, ConfigValue.LineWidth);
		gDef.gprint("max", MAX, "max = %.2f");
		gDef.gprint("avg", AVERAGE, "average = %.2f");
		try
		{
			new RrdGraph(gDef);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		*/
	}

	public static class updateTask implements Runnable
	{
		private long startTime;
		private Sample sample;
		private RrdDb parentDb;

		@Override
		public void run()
		{
			startTime = System.currentTimeMillis();
			try
			{
				parentDb = new RrdDb(ConfigValue.RRDPath + "main.rrd");
				sample = parentDb.createSample();
				sample.setValue(0, Stats.getOnline(true));
				sample.update();
				parentDb.close();

				parentDb = new RrdDb(ConfigValue.RRDPath + "memory.rrd");
				sample = parentDb.createSample();
				sample.setValue(0, MemoryWatchDog.getMemUsed());
				sample.update();
				parentDb.close();

				if(ConfigValue.UseExtendedRRD)
				{
					parentDb = new RrdDb(ConfigValue.RRDPath + "extended.rrd");
					sample = parentDb.createSample();
					sample.setValue(0, Stat.getAdena());
					//sample.setValue(1, getAvgLevel());
					sample.update();
					parentDb.close();
				}
			}
			catch(Exception e)
			{
				_log.info("RRDTools: Unable to update RrdDb: " + e.getMessage());
			}

			try
			{
				RRDTools.draw("last hour", "1h", 3600);
				RRDTools.draw("last day", "1d", 86400); // день
				RRDTools.draw("last week", "1w", 604800); // 7 дней
				RRDTools.draw("last month", "1m", 2419200); // 28 дней
				RRDTools.draw("last year", "1y", 31536000); // 365 дней
				RRDTools.draw("last 5 years", "5y", 157680000); // 1825 дней (5 лет)
			}
			catch(Exception e)
			{
				_log.info("RRDTools: Unable to draw: " + e.getMessage());
			}

			ThreadPoolManager.getInstance().schedule(this, getUpdateTime() - System.currentTimeMillis() + startTime);
		}
	}

	private static long getUpdateTime()
	{
		return ConfigValue.UpdateDelay * 1000L;
	}

	private static Color parseColor(String color)
	{
		try
		{
			Field f = Color.class.getField(color);
			return (Color) f.get(null);
		}
		catch(Exception e)
		{}
		try
		{
			return new Color(Integer.decode(color));
		}
		catch(Exception e)
		{}
		return new Color(Integer.decode("0x" + color));
	}

	private static Color getAreaColor()
	{
		return parseColor(ConfigValue.GraphAreaColor);
	}

	private static Color getLineColor()
	{
		return parseColor(ConfigValue.GraphLineColor);
	}

	private static RrdGraphDef createRrdGraphDef(String filename, String title, long time)
	{
		RrdGraphDef ret = new RrdGraphDef();
		ret.setFilename(ConfigValue.GraphPath + filename + ".png");
		ret.setStartTime(time - 30);
		ret.setEndTime(-30);
		ret.setTitle(title);
		ret.setWidth(ConfigValue.GraphWidth - 80);
		ret.setHeight(ConfigValue.GraphHeight - 73);
		ret.setLazy(true);
		ret.setNoMinorGrid(true);
		ret.setAntiAliasing(true);
		ret.setShowSignature(false);
		ret.setUnit("");
		ret.setMinValue(0);
		ret.setMaxValue(10);
		ret.setFirstDayOfWeek(Calendar.MONDAY);
		ret.setImageFormat("png");
		ret.setImageInfo("<img src='%s' width='%d' height = '%d'>");
		return ret;
	}
}