package l2open.extensions.multilang;

import java.io.*;
import java.util.HashMap;

/**
 * @Author: Death
 * @Date: 25/9/2007
 * @Time: 18:07:24
 *
 * Переводит класс properties из UTF-8 в ASCII
 */
public class ASCIIBuilder
{
	private static final String targetDir = "data/localization/ascii";
	private static final String encoding = "latin1";

	public static void createPropASCII(File f)
	{
		HashMap<String, String> map = new HashMap<String, String>();

		try
		{
			LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));

			String s;

			while((s = lnr.readLine()) != null)
			{
				if(!s.endsWith("\\"))
				{
					String buf = s.replace("\\", "");
					String[] q = new String[2];
					int index = buf.indexOf("=");
					q[0] = buf.substring(0, index);
					q[1] = buf.substring(index + 1);
					map.put(q[0], q[1]);
				}
			}
			lnr.close();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
		}

		for(String s : map.keySet())
			map.put(s, convertString(map.get(s)));

		File dir = new File(targetDir);
		if(dir.exists())
			dir.delete();
		dir.mkdir();

		File target = new File(targetDir + "/" + f.getName());

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
			// Изврат, т.к. properties.store() работает криво.
			FileOutputStream fos = new FileOutputStream(target);
			for(String q : map.keySet())
			{
				fos.write(q.getBytes(encoding));
				fos.write("=".getBytes(encoding));
				fos.write(map.get(q).getBytes(encoding));
				fos.write("\n".getBytes(encoding));
			}
			fos.close();
		}
		catch(IOException e)
		{
			e.printStackTrace(System.err);
		}
	}

	private static String convertString(String s)
	{
		StringBuilder tb = new StringBuilder();

		for(char c : s.toCharArray())
			if(c > 127) // Первые 128 символов в ASCII пишем по человечески
			{
				tb.append("\\u");
				String hex = Integer.toHexString(c);
				int lenght = hex.length();
				for(int i = lenght; i < 4; i++)
					tb.append("0");
				tb.append(hex);
			}
			else
				tb.append(c);

		return tb.toString();
	}
}
