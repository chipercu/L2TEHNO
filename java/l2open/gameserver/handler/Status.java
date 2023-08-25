package l2open.gameserver.handler;

import l2open.config.*;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.Scripts;
import l2open.gameserver.Shutdown;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.taskmanager.MemoryWatchDog;
import l2open.util.Stats;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.CRC32;

public class Status extends Functions implements IVoicedCommandHandler
{
	private final String[] _commandList = new String[] { "status", "info", "serverdate" };

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	public boolean useVoicedCommand(String command, L2Player activeChar, String target)
	{
		if(ConfigValue.StatusVoiceCommandEnabled)
		{
			if(command.equals("status"))
			{
				StringBuilder sb = new StringBuilder();
				boolean en = !activeChar.isLangRus();
				if(en)
				{
					sb.append("<center><font color=\"LEVEL\">Server status:</font></center>");
					sb.append("<br1>Version: ").append(ConfigValue.version.equalsIgnoreCase("${l2open.revision}") ? "VERSION UNSUPPORTED" : ConfigValue.version);
					sb.append("<br>Total online:  ");
				}
				else
				{
					sb.append("<center><font color=\"LEVEL\">Статус сервера:</font></center>");
					sb.append("<br1>Версия: ").append(ConfigValue.version.equalsIgnoreCase("${l2open.revision}") ? "VERSION UNSUPPORTED" : ConfigValue.version);
					sb.append("<br>Онлайн сервера:  ");
				}
				sb.append(Stats.getOnline(true));
				if(activeChar.getPlayerAccess().CanRestart)
					sb.append("<br1>Memory usage: ").append(MemoryWatchDog.getMemUsedPerc());
				int mtc = Shutdown.getInstance().getSeconds();
				if(mtc > 0)
				{
					if(en)
						sb.append("<br1>Time to restart: ");
					else
						sb.append("<br1>До рестарта: ");
					int numDays = mtc / 86400;
					mtc -= numDays * 86400;
					int numHours = mtc / 3600;
					mtc -= numHours * 3600;
					int numMins = mtc / 60;
					mtc -= numMins * 60;
					int numSeconds = mtc;
					if(numDays > 0)
						sb.append(numDays + "d ");
					if(numHours > 0)
						sb.append(numHours + "h ");
					if(numMins > 0)
						sb.append(numMins + "m ");
					if(numSeconds > 0)
						sb.append(numSeconds + "s");
				}
				else
					sb.append("<br1>Restart task not launched.");

				sb.append("<br><center><button value=\"");
				if(en)
					sb.append("Refresh");
				else
					sb.append("Обновить");
				sb.append("\" action=\"bypass -h user_status\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center>");

				show(sb.toString(), activeChar);
				return true;
			}
			else if(command.equals("info"))
			{
				File f = new File("./lib/l2openserver.jar");
				RandomAccessFile af;
				try
				{
					af = new RandomAccessFile(f, "r");
					byte[] b = new byte[(int) f.length()];
					af.readFully(b);
					CRC32 c = new CRC32();
					c.update(b);
					StringBuilder sb = new StringBuilder();
					sb.append("Version: ").append(ConfigValue.version.equalsIgnoreCase("${l2open.revision}") ? "VERSION UNSUPPORTED" : ConfigValue.version);
					sb.append("<br1>Checksum: ").append(Long.toHexString(c.getValue()).toUpperCase());
					sb.append("<br1>Last modified: ").append(DateFormat.getDateTimeInstance().format(new Date(f.lastModified())));
					sb.append("<br1>OS: ").append(System.getenv("OS"));
					sb.append("<br1>User: ").append(System.getenv("USERNAME"));
					sb.append("<br1>Jar Scripts: ").append(Scripts.JAR);
					show(sb.toString(), activeChar);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if(command.equals("serverdate"))
			{
				activeChar.sendMessage("Server date:" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + Calendar.getInstance().get(Calendar.MONTH) + "." + Calendar.getInstance().get(Calendar.YEAR));
				return true;
			}
		}
		else
			activeChar.sendMessage("This command is switched off by administrator.");
		return false;
	}
}
