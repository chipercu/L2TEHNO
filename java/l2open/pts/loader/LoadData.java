package l2open.pts.loader;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Запиздовал Diagod...
 * open-team.ru
 **/
public class LoadData
{
	public static Logger _log = Logger.getLogger(LoadData.class.getName());

	public LoadData()
	{}

	public static List<String> loadFile(String path)
	{
		return loadFile(path, false);
	}

	// Считываем файл и постчроно добавляем его в масив...
	public static List<String> loadFile(String path, boolean trim)
	{
		LineNumberReader lnr = null;
		List<String> _list = new ArrayList<String>();
		try
		{
			lnr = new LineNumberReader(new BufferedReader(new FileReader(new File(path))));
			String line = null;

			while((line = lnr.readLine()) != null)
				if(!line.trim().startsWith("//"))
					_list.add(trim ? line.trim() : line);
			return _list;
		}
		catch(FileNotFoundException e)
		{
			return null;
		}
		catch(Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if(lnr != null)
					lnr.close();
			}
			catch(Exception e1)
			{
				return null;
			}
		}
	}

	public static String writeToFile(String strPage, boolean is)
	{
		FileWriter save = null;

		File file = new File("H:/test/MakerName.java");
		try
		{
			save = new FileWriter(file, is);
			save.write(strPage);
			save.write("\n");
		}
		catch(IOException e)
		{}
		finally
		{
			try
			{
				if(save != null)
					save.close();
			}
			catch (Exception e1)
			{
			}
		}
	    return strPage;
	}

	public static String getTextWith(String text)
	{
		text = text.substring(1, text.length()-1);
		return text;
	}

	public static String[] getTextParse(String text, String split)
	{
		return text.split(split);
	}

	public static List<String> splitList(String s)
	{
		List<String> res = new ArrayList<String>();

		String ef = "";

		int b = 0;
		for(char c : s.toCharArray())
		{
			if(c == '{')
			{
				ef += c;
				b++;
			}
			else if(c == '}')
			{
				b--;
				ef += c;
				if (b == 0)
				{
					res.add(ef.substring(1, ef.length() - 1));
					ef = "";
				}
			}
			else if(c == ';')
			{
				if(b != 0)
					ef += c;
			}
			else
				ef += c;
		}
		return res;
	}

	public static String[] getParseWith(String txt, int index)
	{
		StringTokenizer st = new StringTokenizer(txt, "{");
		int length = st.countTokens();
		String[] text = new String[length-index];
		int i = 0;
		while(st.hasMoreTokens())
		{
			if(i>=index)
			{
				text[i] = st.nextToken().replace("};","").replace("}","").replace("[","").replace("]","");
				i++;
			}
			else
			{
				st.nextToken();
				index--;
			}
		}	
		return text;
	}
}
