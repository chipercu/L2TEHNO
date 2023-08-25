package l2open.gameserver.webserver;

import l2open.config.ConfigValue;
import l2open.util.Files;

import java.io.File;
import java.io.FileWriter;
import java.util.logging.Logger;

public class WebServer extends l2open.common.RunnableImpl
{
	protected static Logger _log = Logger.getLogger(WebServer.class.getName());

	@Override
	public void runImpl()
	{
		try
		{
			File workingDir = new File(ConfigValue.WebServerRoot);
			for(File f : workingDir.listFiles())
			{
				if(!f.getName().endsWith(".fst"))
					continue;

				String content = Files.read(f.getPath());

				if(content == null)
					continue;

				try
				{
					String text = PageParser.parse(content);
					String name = f.getPath();
					name = name.substring(0, name.length() - 4);
					name += text.startsWith("<!DOCTYPE") ? ".html" : ".txt";
					File out = new File(name);
					out.delete();
					out.createNewFile();
					FileWriter fw = new FileWriter(out);
					fw.write(text);
					fw.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}