package l2open.gameserver.model;

import l2open.config.ConfigValue;
import l2open.util.GArray;
import l2open.util.Log;
import l2open.util.Strings;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BypassManager
{
	private static Logger _log = Logger.getLogger(BypassManager.class.getName());

	public static enum BypassType
	{
		ENCODED,
		ENCODED_BBS,
		SIMPLE,
		SIMPLE_BBS,
		SIMPLE_DIRECT
	}

	public static BypassType getBypassType(String bypass)
	{
		switch(bypass.charAt(0))
		{
			case '0':
				return BypassType.ENCODED;
			case '1':
				return BypassType.ENCODED_BBS;
			default:
				if(Strings.matches(bypass, ConfigValue.BypassManagerSimple, Pattern.DOTALL))
					return BypassType.SIMPLE;
				else if(Strings.matches(bypass, ConfigValue.BypassManagerSimpleBbs, Pattern.DOTALL))
					return BypassType.SIMPLE_BBS;
				return BypassType.SIMPLE_DIRECT;
		}
	}

	private static final Pattern p = Pattern.compile("\"(bypass +-h +)(.+?)\"", Pattern.CASE_INSENSITIVE);

	public static String encode(String html, GArray<String> bypassStorage, boolean bbs, boolean special)
	{
		Matcher m = p.matcher(html);
		StringBuffer sb = new StringBuffer();

		while(m.find())
		{
			String bypass = m.group(2);

			String code = bypass;
			String params = "";
			int i = bypass.indexOf(" $");
			boolean use_params = i >= 0;
			if(use_params)
			{
				code = bypass.substring(0, i);
				params = bypass.substring(i).replace("$", "\\$");
			}

			if(bbs)
				m.appendReplacement(sb, "\"bypass -h 1" + Integer.toHexString(bypassStorage.size()) + params + "\"");
			else if(special)
				m.appendReplacement(sb, "\"bypass -h 0" + Integer.toHexString(bypassStorage.size()) + params + " special\"");
			else
				m.appendReplacement(sb, "\"bypass -h 0" + Integer.toHexString(bypassStorage.size()) + params + "\"");

			bypassStorage.add(code);
		}

		m.appendTail(sb);
		return sb.toString();
	}

	public static DecodedBypass decode(String bypass, GArray<String> bypassStorage, boolean bbs, L2Player player)
	{
		synchronized (bypassStorage)
		{
			String[] bypass_parsed = bypass.split(" ");
			int idx = Integer.parseInt(bypass_parsed[0].substring(1), 16);
			String bp;

			try
			{
				bp = bypassStorage.get(idx);
				//System.out.println("DecodedBypass1: -"+bp+"-");
			}
			catch(Exception e)
			{
				bp = null;
			}

			if(bp == null)
			{
				Log.add("Can't decode bypass (bypass not exists): " + (bbs ? "[bbs] " : "") + bypass + " / Player: " + player.getName() + " / Npc: " + (player.getLastNpc() == null ? "null" : player.getLastNpc().getName()), "debug_bypass");
				return null;
			}

			DecodedBypass result = null;
			result = new DecodedBypass(bp, bp, bypass_parsed, bbs);
			for(int i = 1; i < bypass_parsed.length; i++)
				result.bypass += " " + bypass_parsed[i];
			//System.out.println("DecodedBypass2: -"+result.bypass+"-");
			result.trim();
			//System.out.println("DecodedBypass3: -"+result.bypass+"-");

			return result;
		}
	}

	public static class DecodedBypass
	{
		public String[] bypass_parsed = null;
		public String bypass;
		public String bypass2;
		public boolean bbs;

		public DecodedBypass(String _bypass, boolean _bbs)
		{
			bypass = _bypass;
			bbs = _bbs;
		}
	
		public DecodedBypass(String _bypass, String _bypass2, String[] _bypass_parsed, boolean _bbs)
		{
			bypass_parsed = _bypass_parsed;
			bypass = _bypass;
			bypass2 = _bypass2;
			bbs = _bbs;
		}

		public DecodedBypass trim()
		{
			bypass = bypass.trim();
			return this;
		}
	}
}