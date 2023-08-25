package services;

import java.io.File;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.database.mysql;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.util.Rnd;
import l2open.util.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class VoteManager extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private static class Vote
	{
		public boolean active;
		public String name;
		public int id;
		public int maxPerHWID;
		public TreeMap<Integer, String> variants = new TreeMap<Integer, String>();
		public HashMap<String, Integer[]> results = new HashMap<String, Integer[]>();
	}

	private static HashMap<Integer, Vote> VoteList = new HashMap<Integer, Vote>();

	@SuppressWarnings("unchecked")
	private boolean vote(String command, L2Player activeChar, String args)
	{
		if(args != null && !args.isEmpty()) // применение голоса
		{
			String[] param = args.split(" ");
			if(param.length >= 2 && Util.isNumber(param[0]) && Util.isNumber(param[1]))
			{
				String playerId = activeChar.getAccountName();
				Vote v = VoteList.get(Integer.parseInt(param[0]));
				if(v == null || !v.active)
					return false;
				int var = Integer.parseInt(param[1]);
				Integer[] alreadyResults = v.results.get(playerId);
				if(alreadyResults == null)
				{
					v.results.put(playerId, new Integer[] { var });
					mysql.set("INSERT IGNORE INTO vote (`id`, `HWID`, `vote`) VALUES (?,?,?)", param[0], playerId, param[1]);
				}
				else if(alreadyResults.length < v.maxPerHWID)
				{
					for(int id : alreadyResults)
						if(id == var)
						{
							show("Error: Вы уже голосовали в данном опросе.", activeChar);
							return false;
						}
					v.results.put(playerId, (Integer[]) Util.addElementToArray(alreadyResults, var, Integer.class));
					mysql.set("INSERT IGNORE INTO vote (`id`, `HWID`, `vote`) VALUES (?,?,?)", param[0], playerId, param[1]);
				}
				else
				{
					show("Error: you have reached votes limit.", activeChar);
					return false;
				}
			}
		}

		int count = 0;
		StringBuilder html = new StringBuilder("!VoteManager:\n<br>");
		String playerId = activeChar.getAccountName();
		for(Entry<Integer, Vote> e : VoteList.entrySet())
			if(e.getValue().active)
			{
				count++;
				html.append(e.getValue().name).append(":<br>");
				Integer[] already = e.getValue().results.get(playerId);
				if(already != null && already.length >= e.getValue().maxPerHWID)
					html.append("Вы уже проголосовали.<br>");
				else
				{
					Entry<Integer, String>[] variants = new Entry[e.getValue().variants.size()];
					int i = 0;
					for(Entry<Integer, String> variant : e.getValue().variants.entrySet())
					{
						variants[i] = variant;
						i++;
					}
					shuffle(variants);

					variants: for(Entry<Integer, String> variant : variants)
					{
						if(already != null)
							for(Integer et : already)
								if(et.equals(variant.getKey()))
									continue variants;
						html.append("[user_vote " + e.getValue().id + " " + variant.getKey() + "|" + variant.getValue() + "]<br1>");
					}
					html.append("<br>");
				}
			}
		if(count == 0)
			html.append("Нет активных опросов.");
		show(html.toString(), activeChar);

		return true;
	}

	private static void shuffle(Entry<Integer, String>[] array)
	{
		int j;
		Entry<Integer, String> tmp;
		// i is the number of items remaining to be shuffled.
		for(int i = array.length; i > 1; i--)
		{
			// Pick a random element to swap with the i-th element.
			j = Rnd.get(i); // 0 <= j <= i-1 (0-based array)
			// Swap array elements.
			tmp = array[j];
			array[j] = array[i - 1];
			array[i - 1] = tmp;
		}
	}

	public static void load()
	{
		VoteList.clear();

		// грузим голосования
		try
		{
			File file;
			if (ConfigValue.develop) {
				file = new File("data/xml/vote.xml");
			} else {
				file = new File(ConfigValue.DatapackRoot + "/data/xml/vote.xml");
			}

			DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
			factory2.setValidating(false);
			factory2.setIgnoringComments(true);
			Document doc2 = factory2.newDocumentBuilder().parse(file);

			for(Node n2 = doc2.getFirstChild(); n2 != null; n2 = n2.getNextSibling())
				if("list".equalsIgnoreCase(n2.getNodeName()))
					for(Node d2 = n2.getFirstChild(); d2 != null; d2 = d2.getNextSibling())
						if("vote".equalsIgnoreCase(d2.getNodeName()))
						{
							Vote v = new Vote();
							v.id = Integer.parseInt(d2.getAttributes().getNamedItem("id").getNodeValue());
							v.maxPerHWID = Integer.parseInt(d2.getAttributes().getNamedItem("maxPerHWID").getNodeValue());
							v.name = d2.getAttributes().getNamedItem("name").getNodeValue();
							v.active = Boolean.parseBoolean(d2.getAttributes().getNamedItem("active").getNodeValue());

							for(Node i = d2.getFirstChild(); i != null; i = i.getNextSibling())
								if("variant".equalsIgnoreCase(i.getNodeName()))
									v.variants.put(Integer.parseInt(i.getAttributes().getNamedItem("id").getNodeValue()), i.getAttributes().getNamedItem("desc").getNodeValue());

							VoteList.put(v.id, v);
						}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		// грузим голоса
		ThreadConnection con = null;
		FiltredPreparedStatement st = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			st = con.prepareStatement("SELECT * FROM vote");
			rs = st.executeQuery();
			while(rs.next())
			{
				Vote v = VoteList.get(rs.getInt("id"));
				if(v != null)
				{
					String HWID = rs.getString("HWID");
					Integer[] rez = v.results.get(HWID);
					v.results.put(HWID, (Integer[]) Util.addElementToArray(rez, rs.getInt("vote"), Integer.class));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, st, rs);
		}
	}

	private String[] _commandList = new String[] { "vote" };

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	@Override
	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		if(command.equalsIgnoreCase("vote"))
			return vote(command, activeChar, args);
		return false;
	}

	@Override
	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
		load();
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}