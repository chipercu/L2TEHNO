package l2open.util;

import javolution.util.FastMap;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Files
{
	private static Logger _log = Logger.getLogger(Strings.class.getName());

	private static final FastMap<String, String> cache = new FastMap<String, String>().setShared(true);
	public static final FastMap<Long, Boolean> cacheCBSkill = new FastMap<Long, Boolean>().setShared(true);
	private static final FastMap<String, String> cache_pts = new FastMap<String, String>().setShared(true);

	public static String read(String name, boolean neww)
	{
		if(neww)
			return read2(name);
		return read(name);
	}

	public static String read(String name)
	{
		return readF(name, true);
	}

	public static String readF(String name, boolean _cache)
	{
		if(name == null)
			return null;

		if(ConfigValue.useFileCache && _cache && cache.containsKey(name))
			return cache.get(name);

		File file = new File("./" + name);

		//		_log.info("Get file "+file.getPath());

		if(!file.exists())
			return null;

		String content = null;

		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new UnicodeReader(new FileInputStream(file), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String s = "";
			while((s = br.readLine()) != null)
				sb.append(s).append("\n");
			content = sb.toString();
			sb = null;
		}
		catch(Exception e)
		{ /* problem are ignored */}
		finally
		{
			try
			{
				if(br != null)
					br.close();
			}
			catch(Exception e1)
			{ /* problems ignored */}
		}

		if(ConfigValue.useFileCache && _cache)
			cache.put(name, content);

		return content;
	}

	public static String read2(String name)
	{
		if(name == null)
			return null;

		if(ConfigValue.useFileCache && cache.containsKey(name))
			return cache.get(name);

		File file = new File("./" + name);

		if(!file.exists())
			return null;

		String content = null;
		FileInputStream is = null;
		try
		{
			is = new FileInputStream(file);
			int b;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while((b=is.read())!=-1)
				baos.write( b );
			content = baos.toString("Cp1251");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(is != null)
					is.close();
			}
			catch(Exception e1)
			{ /* problems ignored */}
		}

		if(ConfigValue.useFileCache)
			cache.put(name, content);

		return content;
	}

	public static void cacheClean()
	{
		cache.clear();
		cacheCBSkill.clear();
		cache_pts.clear();
	}

	public static long lastModified(String name)
	{
		if(name == null)
			return 0;

		return new File(name).lastModified();
	}

	public static String read(String name, L2Player player)
	{
		return read(name, player, false);
	}

	public static String read(String name, L2Player player, boolean neww)
	{
		if(player == null)
			return "";
		if(player.isGM())
			player.sendMessage("HTML: " + name);
		//Util.test();
		return read(name, player.getLang(), neww);
	}

	public static String langFileName(String name, String lang)
	{
		if(lang == null || lang.equalsIgnoreCase("en"))
			lang = "";

		String tmp;

		tmp = name.replaceAll("(.+)(\\.htm)", "$1-" + lang + "$2");
		if(!tmp.equals(name) && lastModified(tmp) > 0)
			return tmp;

		tmp = name.replaceAll("(.+)(/[^/].+\\.htm)", "$1/" + lang + "$2");
		if(!tmp.equals(name) && lastModified(tmp) > 0)
			return tmp;

		tmp = name.replaceAll("(.+?/html)/", "$1-" + lang + "/");
		if(!tmp.equals(name) && lastModified(tmp) > 0)
			return tmp;

		if(lastModified(name) > 0)
			return name;

		return null;
	}

	public static String read(String name, String lang)
	{
		return read(name, lang, false);
	}

	public static String read(String name, String lang, boolean neww)
	{
		String tmp = langFileName(name, lang);

		long last_modif = lastModified(tmp); // время модификации локализованного файла
		if(last_modif > 0) // если он существует
		{
			if(last_modif >= lastModified(name) || !ConfigValue.checkLangFilesModify) // и новее оригинального файла
				return Strings.bbParse(read(tmp, neww)); // то вернуть локализованный

			_log.warning("Last modify of " + name + " more then " + tmp); // если он существует но устарел - выругаться в лог
		}

		return Strings.bbParse(read(name, neww)); // если локализованный файл отсутствует вернуть оригинальный
	}

	/**
	 * Сохраняет строку в файл в кодировке UTF-8.<br>
	 * Если такой файл существует, то перезаписывает его.
	 * @param path путь к файлу
	 * @param string сохраняемая строка
	 */
	public static void writeFile(String path, String string)
	{
		if(string == null || string.length() == 0)
			return;

		File target = new File(path);

		if(!target.exists())
			try
			{
				target.createNewFile();
			}
			catch(IOException e)
			{
				e.printStackTrace(System.err);
			}

		try
		{
			FileOutputStream fos = new FileOutputStream(target);
			fos.write(string.getBytes("UTF-8"));
			fos.close();
		}
		catch(IOException e)
		{
			e.printStackTrace(System.err);
		}
	}

	public static boolean copyFile(String pathSource, String pathDest)
	{
		try
		{
			FileChannel source = new FileInputStream(pathSource).getChannel();
			FileChannel destination = new FileOutputStream(pathDest).getChannel();

			destination.transferFrom(source, 0, source.size());

			source.close();
			destination.close();
		}
		catch(IOException e)
		{
			return false;
		}
		return true;
	}

	public static String htmlItemName(int itemId)
	{
		return "&#" + itemId + ";";
	}

	public static String read_pts(String file, L2Player player)
	{
		if(player != null && player.isGM())
			player.sendMessage("HTML: " + file);
		String path = (player == null || player.isLangRus()) ? ("html-ru/"+file) : ("html-en/"+file);
		String result = cache_pts.get(path);
		if(result == null)
		{
			_log.info("Html("+path+") not wound!");
			return "Html("+path+") not wound!";
		}
		return result;
	}

	public static void loadPtsHtml(String file)
	{
		long _time1 = System.currentTimeMillis();
		FileInputStream fin = null;
		String content = null;
		try
		{
			fin = new FileInputStream(file);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			while((ze = zin.getNextEntry()) != null)
			{
				int i0=0;
				int i1=0;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				for(int c = zin.read(); c != -1; c = zin.read())
				{
					if(i0 == 0)
						i1-=c;
					else if(i0 == 1)
						i1+=c;
					baos.write(c);
					i0++;
				}
				if(i1 == -1)
					content = baos.toString("UnicodeLittle");
				else if(i1 == 1)
					content = baos.toString("UnicodeBig");
				else
					content = baos.toString("Cp1251");
				zin.closeEntry();
				cache_pts.put(ze.getName(), content);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(fin != null)
					fin.close();
			}
			catch(Exception e1)
			{}
			long _time2 = System.currentTimeMillis() - _time1;
			_log.info("PTS HTML: "+file+" loaded "+_time2+"ms...");
		}
	}
}