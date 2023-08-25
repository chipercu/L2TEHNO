package l2open.gameserver.cache;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Впиздовал Diagod.
 **/
public class AugmentName
{
	private static final Logger _log = Logger.getLogger(AugmentName.class.getName());

	private static final ConcurrentHashMap<Integer, String> _stringCatch = new ConcurrentHashMap<Integer, String>();

	public static String getString(int id)
	{
		return _stringCatch.get(id);
	}

	public static void reload()
	{
		_stringCatch.clear();
		load();
	}

	public static void load()
	{
		String id = "";
		LineNumberReader lnr = null;
		try
		{
			lnr = new LineNumberReader(new BufferedReader(new FileReader(new File("data/fstring/optiondata.txt"))));
			String line = null;

			while((line = lnr.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line, "\t");
				id = st.nextToken();
				st.nextToken();
				st.nextToken();
				String l1 = st.nextToken();
				if(l1.length()>2)
					l1 = l1.substring(2, l1.length()-2);
				else if(l1.length() == 2)
					l1 = l1.substring(2);
				String l2 = st.nextToken();
				
				if(l2.length()>2)
					l2 = l2.substring(2, l2.length()-2);
				else if(l2.length() == 2)
					l2 = l2.substring(2);
				
				String l3 = st.nextToken();
				
				if(l3.length()>2)
					l3 = l3.substring(2, l3.length()-2);
				else if(l3.length() == 2)
					l3 = l3.substring(2);
				String result = l1;
				if(l2.length()>0)
					result+="\\n "+l2;
				if(l3.length()>0)
					result+="\\n "+l3;
				_stringCatch.put(Integer.parseInt(id), result);
			}
		}
		catch(Exception e)
		{
			_log.warning("Error!!!: "+id);
			e.printStackTrace();
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
				e1.printStackTrace();
			}
		}
	}
}