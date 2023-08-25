package motion;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.text.Collator;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.sql.ResultSet;
import java.sql.SQLException;

import javolution.util.FastMap;
import l2open.extensions.scripts.ScriptFile;
import l2open.common.ThreadPoolManager;
import l2open.database.*;
import l2open.config.*;
import l2open.gameserver.GameServer;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.*;
import l2open.util.DummyDeadlock.ReentrantDeadlock;
import l2open.util.DummyDeadlock.SynchronizedDeadlock;

public class AdminDebug implements IAdminCommandHandler, ScriptFile
{
	protected final ReentrantLock lock = new ReentrantLock();

	private static enum Commands
	{
		admin_dump_obj,
		admin_dump_mobs_aggro_info,
		admin_dump_commands,
		admin_debug_deadlock_sync,
		admin_debug_deadlock_lock,
		admin_packet_build,
		admin_config,
		admin_start_debug,
		admin_stop_debug,
		admin_restart_debug,
		admin_start_gc,
		admin_stop_gc,
		admin_restart_gc,
		admin_pass_list,
		admin_pass_sa,
		admin_pass_srca,
		admin_pass_sc,
		admin_char_log
	}

	@SuppressWarnings("unchecked")
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.isGM())
			return false;
		String out;

		switch(command)
		{
			case admin_char_log:
				L2Object target = activeChar.getTarget();
				if(target == null || !target.isPlayer())
				{
					activeChar.sendMessage("Не верный таргет.");
					return false;
				}

				L2Player player = (L2Player) target;
				if(player.can_private_log)
				{
					player.can_private_log = false;
					player.unsetVar("can_private_log");
					activeChar.sendMessage("Вы сняли персонажа '"+player.getName()+"' с логирования.");
				}
				else
				{
					player.can_private_log = true;
					player.setVar("can_private_log", "true");
					activeChar.sendMessage("Вы поставили персонажа '"+player.getName()+"' на логирование.");
				}
				break;
			case admin_pass_sc:
				String content = Files.read("data/html/admin/char_pass_list.htm", activeChar);
				content = content.replace("%menu%", getListChar(fullString.substring(14)));

				ShowBoard.separateAndSend(content, activeChar);
				break;
			case admin_pass_sa:
				content = Files.read("data/html/admin/char_pass_list.htm", activeChar);
				String val = wordList[1];
				int page = Integer.parseInt(val);
				content = content.replace("%menu%", getListAcc(fullString.substring(15+val.toCharArray().length), null, page));

				ShowBoard.separateAndSend(content, activeChar);
				break;
			case admin_pass_srca:
				_log.info("admin_pass_srca='"+fullString+"'");
				content = Files.read("data/html/admin/char_pass_list.htm", activeChar);
				val = wordList[1];
				page = Integer.parseInt(val);
				content = content.replace("%menu%", getListAcc(null, fullString.substring(17+val.toCharArray().length), page));

				ShowBoard.separateAndSend(content, activeChar);
				break;
			case admin_pass_list:
				page = -1;
				if(fullString.length() > 16)
				{
					val = fullString.substring(16);
					page = Integer.parseInt(val);
				}
				if(page == -1)
				{
					loadForDBPSWD(1);
					page = 0;
				}

				content = Files.read("data/html/admin/char_pass_list.htm", activeChar);
				content = content.replace("%menu%", getListPasswordCount(page));

				ShowBoard.separateAndSend(content, activeChar);
				break;
			case admin_dump_obj:
				target = activeChar.getTarget();
				if(target == null)
					activeChar.sendMessage("No Target");
				else
				{
					_log.info(target.dump());
					activeChar.sendMessage("Object dumped to stdout");
				}
				break;
			case admin_dump_mobs_aggro_info:
				L2NpcTemplate[] npcs = NpcTable.getAll();
				out = "<?php\r\n";
				for(L2NpcTemplate npc : npcs)
					if(npc != null && npc.isInstanceOf(L2MonsterInstance.class))
						out += "\t$monsters[" + npc.getNpcId() + "]=array('level'=>" + npc.level + ",'aggro'=>" + npc.aggroRange + ");\r\n";
				out += "?>";
				Str2File("monsters.php", out);
				activeChar.sendMessage("Monsters info dumped, checkout for monsters.php in the root of server");
				break;
			case admin_dump_commands:
				out = "Commands list:\r\n";

				HashMap<IAdminCommandHandler, TreeSet<String>> handlers = new HashMap<IAdminCommandHandler, TreeSet<String>>();
				for(String cmd : AdminCommandHandler.getInstance().getAllCommands())
				{
					IAdminCommandHandler key = AdminCommandHandler.getInstance().getAdminCommandHandler(cmd);
					if(!handlers.containsKey(key))
						handlers.put(key, new TreeSet<String>(Collator.getInstance()));
					handlers.get(key).add(cmd.replaceFirst("admin_", ""));
				}

				for(IAdminCommandHandler key : handlers.keySet())
				{
					out += "\r\n\t************** Group: " + key.getClass().getSimpleName().replaceFirst("Admin", "") + " **************\r\n";
					for(String cmd : handlers.get(key))
						out += "//" + cmd + " - \r\n";
				}
				Str2File("admin_commands.txt", out);
				activeChar.sendMessage("Commands list dumped, checkout for admin_commands.txt in the root of server");
				break;
			case admin_debug_deadlock_sync:
				activeChar.sendMessage("Testing Synchronized Deadlock");
				new SynchronizedDeadlock().start();
				break;
			case admin_debug_deadlock_lock:
				activeChar.sendMessage("Testing Reentrant Deadlock");
				new ReentrantDeadlock().start();
				break;
			case admin_start_debug:
				activeChar.sendMessage("Testing ThreadPoolManager start. Interval="+ConfigValue.ThreadPoolManagerDebugInterval);
				ThreadPoolManager.getInstance().startDebug();
				break;
			case admin_stop_debug:
				activeChar.sendMessage("Testing ThreadPoolManager stop.");
				ThreadPoolManager.getInstance().stopDebug();
				break;
			case admin_restart_debug:
				activeChar.sendMessage("Testing ThreadPoolManager restart. Interval="+ConfigValue.ThreadPoolManagerDebugInterval);
				ThreadPoolManager.getInstance().stopDebug();
				ThreadPoolManager.getInstance().startDebug();
				break;
			case admin_start_gc:
				activeChar.sendMessage("GarbageCollector start. Interval="+ConfigValue.GarbageCollectorDelay);
				GameServer.startGC();
				break;
			case admin_stop_gc:
				activeChar.sendMessage("GarbageCollector stop.");
				GameServer.stopGC();
				break;
			case admin_restart_gc:
				activeChar.sendMessage("GarbageCollector restart. Interval="+ConfigValue.GarbageCollectorDelay);
				GameServer.stopGC();
				GameServer.startGC();
				break;
			case admin_packet_build:
				StringTokenizer st = new StringTokenizer(wordList[1]);
				FastMap<Integer, Object> packet = new FastMap<Integer, Object>();
				GArray<FastMap<Integer, Object>> packet2 = new GArray<FastMap<Integer, Object>>();
				for(int i = 0;i <= st.countTokens();i++)
				{
					Object object;
					String type = st.nextToken();
					int types = -1;
					if(type.startsWith("C="))
						types = 0;
					else if(type.startsWith("H="))
						types = 1;
					else if(type.startsWith("D="))
						types = 2;
					else if(type.startsWith("Q="))
						types = 3;
					else if(type.startsWith("F="))
						types = 4;
					else if(type.startsWith("S="))
						types = 5;
					if(types != 5)
						object = Integer.parseInt(type.substring(2));
					else
						object = type.substring(2);
					packet.put(types, object);
					packet2.add(packet);
				}				
				activeChar.sendPacket(new PacketBuilder(packet2));
				activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/packet.htm"));
				break;
			case admin_config:
				GArray<String> configKey = ConfigSystem.getAllKey();
				val = "";
				int pg = 0;
				String name = "";
				String value2 = "";
				try
				{
					val = wordList[1];
					pg = Integer.parseInt(wordList[2]);
					if(wordList.length > 3)
					{
						name = wordList[3];
						value2 = wordList[4];
						try
						{
							System.out.println("Admin: "+activeChar.getName()+" set config("+val+"): "+name+" value: "+ConfigValue.class.getField(name).get(null)+" ==> "+value2);
							// TODO: сделать сохранение изменения конфигов в файл admin.properties
						}
						catch (NoSuchFieldException e)
						{
						}
						catch (IllegalAccessException e)
						{
						}
						ConfigSystem.set(name, value2);
					}
				}
				catch(StringIndexOutOfBoundsException e)
				{
					val = "server.properties";
				}
				catch(ArrayIndexOutOfBoundsException e1)
				{
					val = "server.properties";
				}
				catch(NullPointerException e2)
				{
					val = "server.properties";
				}

				StringBuffer replyMSG = new StringBuffer("<html><body>");
				replyMSG.append("<br>");
				replyMSG.append("<br>");
				replyMSG.append("<br>");
				replyMSG.append("<br>");
				replyMSG.append("<table width=100%><tr>");
				replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
				replyMSG.append("<td width=180><center>Config Menu</center></td>");
				replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
				replyMSG.append("</tr></table>");
				replyMSG.append("<br><br>");

				// TODO: поиск конфига...
				/*replyMSG.append("<center><table><tr><td>");
				replyMSG.append("<edit var=\"character_name\" width=80></td><td><button value=\"Find\" action=\"bypass -h admin_find_character $character_name\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
				replyMSG.append("</td></tr></table></center><br><br>");*/

				// Список файлов конфига...
				replyMSG.append("<center><table width=100%>");
				int _size = ConfigSystem.getPropFileName().keySet().size();
				String[] fName = new String[_size];
				int count = 0;
				for(String fileName : ConfigSystem.getPropFileName().keySet())
				{
					fName[count] = fileName;
					count++;
				}

				String name1 = "NuN";
				String name2 = "NuN";
				String name3 = "NuN";
				String name4 = "NuN";
				String name5 = "NuN";
				Arrays.sort(fName);
				for(int i = 0;i<count;i=i+4)
				{
					try
					{
						name1 = fName[i];
					}
					catch(ArrayIndexOutOfBoundsException e)
					{
						name1 = "NuN";
					}
					try
					{
						name2 = fName[i+1];
					}
					catch(ArrayIndexOutOfBoundsException e)
					{
						name2 = "NuN";
					}
					try
					{
						name3 = fName[i+2];
					}
					catch(ArrayIndexOutOfBoundsException e)
					{
						name3 = "NuN";
					}
					try
					{
						name4 = fName[i+3];
					}
					catch(ArrayIndexOutOfBoundsException e)
					{
						name4 = "NuN";
					}

					replyMSG.append("<tr>");
					if(!name1.startsWith("NuN"))
						replyMSG.append("<td><button value=\"" + name1.substring(0, name1.length()-11)+"\" action=\"bypass -h admin_config " + name1 + " 0\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
					if(!name2.startsWith("NuN"))
						replyMSG.append("<td><button value=\"" + name2.substring(0, name2.length()-11)+"\" action=\"bypass -h admin_config " + name2 + " 0\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
					if(!name3.startsWith("NuN"))
						replyMSG.append("<td><button value=\"" + name3.substring(0, name3.length()-11)+"\" action=\"bypass -h admin_config " + name3 + " 0\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
					if(!name4.startsWith("NuN"))
						replyMSG.append("<td><button value=\"" + name4.substring(0, name4.length()-11)+"\" action=\"bypass -h admin_config " + name4 + " 0\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
					replyMSG.append("</tr>");
				}
				replyMSG.append("</table></center>");
				replyMSG.append("<br>");
				replyMSG.append("<center><table><tr><td><edit var=\"set_value\" width=500></td></tr></table></center>");
				replyMSG.append("<br>");

				int MaxCharactersPerPage = 80; // Макс количество значений на одной странице.
				int MaxPages = ConfigSystem.getPropFileName().get(val).size() / MaxCharactersPerPage;
				if(ConfigSystem.getPropFileName().get(val).size() > MaxCharactersPerPage * MaxPages)
					MaxPages++;
				if(pg > MaxPages)
					pg = MaxPages;
				int CharactersStart = MaxCharactersPerPage * pg;
				int CharactersEnd = ConfigSystem.getPropFileName().get(val).size();
				if(CharactersEnd - CharactersStart > MaxCharactersPerPage)
					CharactersEnd = CharactersStart + MaxCharactersPerPage;

				// Список страниц...
				for(int x = 0;x < MaxPages;x++)
					if(MaxPages > 1)
						replyMSG.append("<center><a action=\"bypass -h admin_config "+val+" "+x+"\">Page " + (x + 1) + "</a></center>");
				replyMSG.append("<br>");

				replyMSG.append("<center><table border=1 width=900>");
				replyMSG.append("<tr><td width=350 align=\"left\" >Config name:</td><td width=550 align=\"center\">Config value fo: "+val+"</td></tr>");
				String[] listC = new String[ConfigSystem.getPropFileName().get(val).size()];
				int a = 0;
				for(String configName : ConfigSystem.getPropFileName().get(val))
				{
					listC[a] = configName;
					a++;
				}
				Arrays.sort(listC);
				for(int i = CharactersStart;i < CharactersEnd;i++)
				{
					String aec = listC[i];
					if
					(
						aec.startsWith("ApasswdTemplate") || 
						aec.startsWith("AllyNameTemplate") || 
						aec.startsWith("ClanTitleTemplate") || 
						aec.startsWith("ClanNameTemplate") || 
						aec.startsWith("CnameTemplate") ||
						aec.startsWith("Accounts_URL") ||
						aec.startsWith("Accounts_Login") ||
						aec.startsWith("Accounts_Password") ||
						aec.startsWith("Driver") ||
						aec.startsWith("URL") ||
						aec.startsWith("Login") ||
						aec.startsWith("Password") ||
						aec.startsWith("MaximumDbConnections") ||
						aec.startsWith("TradeWords") ||
						aec.startsWith("CoreRevision") ||
						aec.startsWith("LicenseRevision") ||
						aec.startsWith("LicenseKey")
					) continue;
					String value = ConfigSystem.get(aec, "");
					replyMSG.append("<tr>");
					replyMSG.append("<td width=350><button width=340 value=\""+aec+"\" action=\"bypass -h admin_config "+val+" 0 "+aec+" $set_value\" width=250 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
					replyMSG.append("<td width=550><font color=\"00ff00\">"+value+"</font></td>");
					replyMSG.append("</tr>");
				}
				replyMSG.append("</table></center>");
				replyMSG.append("<br>");
				replyMSG.append("<br>");
				replyMSG.append("</body></html>");
				ShowBoard.separateAndSend(replyMSG.toString(), activeChar);
				break;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap)
	{
		List<Map.Entry> list = new LinkedList<Map.Entry>(unsortMap.entrySet());
	 
		Collections.sort(list, new Comparator<Map.Entry>()
		{
			public int compare(Map.Entry o1, Map.Entry o2)
			{
				return ((Comparable) o1.getValue()).compareTo(o2.getValue())*-1;
			}
		});

		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator it = list.iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put((String)entry.getKey(), (Integer)entry.getValue());
		}
		return sortedMap;
	}

	private static String getListChar(String login)
	{
		StringBuffer replyMSG = new StringBuffer();
		
		//if(!ConfigValue.LicenseKey.equals("DiagoD") && !ConfigValue.LicenseKey.equals("l2-hunter"))
		//	return replyMSG.toString();

		/*replyMSG.append("<table border=1 width=755>");
		replyMSG.append("<tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td width=180><center>Список долбаёбов</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_pass_list\" width=40 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("</tr>");
		replyMSG.append("</table>");*/

		replyMSG.append("<br><br>\n");
		replyMSG.append("<table border=1>\n");
		replyMSG.append("<tr>\n");
		replyMSG.append("<td width=100><center><font color=\"ffffff\">Имя</font></center><br></td>\n");
		replyMSG.append("<td width=120><center><font color=\"ffffff\">Класс</font></center><br></td>\n");
		replyMSG.append("<td width=70><center><font color=\"ffffff\">Уровень</font></center><br></td>\n");
		replyMSG.append("<td height=20 width=100><center><button action=\"bypass -h admin_pass_srca 0 "+login+"\" value=\"НАЗАД\" width=60 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"/></center></td>\n");

		replyMSG.append("</tr>\n");

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT c.char_name, c.obj_Id, c.online, cs.level, ct.ClassName, b.endban FROM characters c LEFT JOIN character_subclasses cs ON (c.obj_Id=cs.char_obj_id AND cs.isBase=1) LEFT JOIN char_templates ct ON (ct.ClassId = cs.class_id) LEFT JOIN bans b ON (c.obj_Id = b.obj_Id) WHERE c.account_name='"+login+"'");
			rset = statement.executeQuery();
			while(rset.next())
			{
				String char_name = rset.getString("char_name");
				int level = rset.getInt("level");
				int online = rset.getInt("online");
				String ClassName = rset.getString("ClassName");
				long endban = rset.getInt("endban")*1000L;

				replyMSG.append("<tr>\n");

				if(online == 1)
					replyMSG.append("<td height=20 width=100><center><font color=\"00ff00\"><a action=\"bypass -h admin_character_list "+char_name+"\">"+char_name+"</a></font></center></td>\n");
				else if(endban > System.currentTimeMillis())
					replyMSG.append("<td height=20 width=100><center><font color=\"0050ff\">"+char_name+"</font></center></td>\n");
				else
					replyMSG.append("<td height=20 width=100><center><font color=\"ff0000\">"+char_name+"</font></center></td>\n");

				replyMSG.append("<td height=20 width=120><center>"+ClassName+"</center></td>\n");
				replyMSG.append("<td height=20 width=70><center>"+level+"</center></td>\n");
				if(endban < System.currentTimeMillis())
					replyMSG.append("<td height=20 width=120><button action=\"bypass -h admin_ban "+char_name+" -1\" value=\"Бан чара\" width=110 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"/></td>\n");
				else
					replyMSG.append("<td height=20 width=120><button action=\"bypass -h admin_unban "+char_name+"\" value=\"Розбан чара\" width=110 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"/></td>\n");
				replyMSG.append("</tr>\n");
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		replyMSG.append("</table>\n");
		return replyMSG.toString();
	}

	private static boolean isOnlineAcc(String login)
	{
		//if(!ConfigValue.LicenseKey.equals("DiagoD") && !ConfigValue.LicenseKey.equals("l2-hunter"))
		//	return false;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT char_name FROM characters WHERE account_name='"+login+"' AND online='1'");
			rset = statement.executeQuery();
			return rset.next();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return false;
	}

	private static void loadForDBPSWD(int val)
	{
		//if(!ConfigValue.LicenseKey.equals("DiagoD") && !ConfigValue.LicenseKey.equals("l2-hunter"))
		//	return;

		long time1 = System.currentTimeMillis();

		_password_count_list.clear();
		_key_list.clear();
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstanceLogin().getConnection();
			statement = con.prepareStatement("SELECT login, password, COUNT(password) AS count FROM accounts GROUP BY password ORDER BY count DESC");
			rset = statement.executeQuery();
			while(rset.next())
			{
				int count = rset.getInt("count");
				String password = rset.getString("password");
				if(count > val)
					_password_count_list.put(password, count);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		long time2 = System.currentTimeMillis();
		_password_count_list = sortByValue(_password_count_list);
		_key_list.addAll(_password_count_list.keySet());
		long time3 = System.currentTimeMillis();
		_log.info("AdminDebug: time_select="+(time2-time1)+" time_sort="+(time3-time2)+" all="+(time3-time1));
	}

	
	// DELETE FROM LOGIN.accounts WHERE LOGIN.accounts.login NOT IN (SELECT GAMEX50.characters.account_name FROM GAMEX50.characters) AND LOGIN.accounts.login NOT IN (SELECT GAMEX100.characters.account_name FROM GAMEX100.characters);
	private static Map<String, Integer> _password_count_list = new HashMap<String, Integer>();
	private static List<String> _key_list = new ArrayList<String>();

	private static String getListPasswordCount(int page)
	{
		int size = _password_count_list.size();
		int max_per_page = 100;
		int max_page = size / max_per_page;

		if(size > max_per_page * max_page)
			max_page++;

		if(page > max_page)
			page = max_page;

		int page_start = max_per_page * page;
		int page_end = size;
		if(page_end - page_start > max_per_page)
			page_end = page_start + max_per_page;

		StringBuffer replyMSG = new StringBuffer();
		//if(!ConfigValue.LicenseKey.equals("DiagoD") && !ConfigValue.LicenseKey.equals("l2-hunter"))
		//	return replyMSG.toString();

		replyMSG.append("<font color=\"ababab\">\n");
		replyMSG.append("	<table border=0>\n");

		for(int x = 0; x < max_page; x=x+14)
		{
			int pagenr = x + 1;
			replyMSG.append("<tr>\n");
			if(max_page > x+1)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_list "+x+"\">Page " + pagenr + "</a></td>\n");
			if(max_page > x+1)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_list "+(x+1)+"\">Page " + (pagenr + 1) + "</a></td>\n");
			if(max_page > x+2)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_list "+(x+2)+"\">Page " + (pagenr + 2) + "</a></td>\n");
			if(max_page > x+3)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_list "+(x+3)+"\">Page " + (pagenr + 3) + "</a></td>\n");
			if(max_page > x+4)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_list "+(x+4)+"\">Page " + (pagenr + 4) + "</a></td>\n");
			if(max_page > x+5)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_list "+(x+5)+"\">Page " + (pagenr + 5) + "</a></td>\n");
			if(max_page > x+6)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_list "+(x+6)+"\">Page " + (pagenr + 6) + "</a></td>\n");
			if(max_page > x+7)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_list "+(x+7)+"\">Page " + (pagenr + 7) + "</a></td>\n");
			if(max_page > x+8)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_list "+(x+8)+"\">Page " + (pagenr + 8) + "</a></td>\n");
			if(max_page > x+9)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_list "+(x+9)+"\">Page " + (pagenr + 9) + "</a></td>\n");
			if(max_page > x+10)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_list "+(x+10)+"\">Page " + (pagenr + 10) + "</a></td>\n");
			if(max_page > x+11)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_list "+(x+11)+"\">Page " + (pagenr + 11) + "</a></td>\n");
			if(max_page > x+12)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_list "+(x+12)+"\">Page " + (pagenr + 12) + "</a></td>\n");
			if(max_page > x+13)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_list "+(x+13)+"\">Page " + (pagenr + 13) + "</a></td>\n");
			replyMSG.append("</tr>\n");
		}

		replyMSG.append("</table>\n");
		replyMSG.append("</font>\n");
		replyMSG.append("<br><br>\n");
		replyMSG.append("<font color=\"ffffff\"><table border=1 width=450 cellpadding=0 cellspacing=0>\n");
		replyMSG.append("<tr>\n");
		replyMSG.append("<td width=40><font color=\"00ff00\">№</font></td>\n");
		replyMSG.append("<td width=110><font color=\"00ff00\">Пароль</font></td>\n");
		replyMSG.append("<td width=200><font color=\"00ff00\">Хеш пароля</font></td>\n");
		replyMSG.append("<td width=100><font color=\"00ff00\">Логинов</font></td>\n");
		replyMSG.append("</tr>\n");

		for(int i = page_start; i < page_end; i++)
		{
			String password = _key_list.get(i);
			int count = _password_count_list.get(password);
			replyMSG.append("<tr>\n");
			replyMSG.append("<td height=20><font color=\"ff0000\">"+(i+1)+"</font></td>\n");
			replyMSG.append("<td height=20>---</td>\n");
			replyMSG.append("<td height=20><a action=\"bypass -h admin_pass_sa 0 "+password+"\">"+password.substring(0, 10)+"</a></td>\n");
			replyMSG.append("<td height=20>"+count+"</td>\n");
			replyMSG.append("</tr>\n");
		}

		replyMSG.append("</table></font><br><br>\n");
		return replyMSG.toString();
	}

	public static class AccInfo
	{
		String login;
		int access_level;
		boolean is_online;
		public AccInfo(String l, int a, boolean i)
		{
			login = l;
			access_level= a;
			is_online = i;
		}
	}

	private static List<AccInfo> _acc_info_list = new ArrayList<AccInfo>();

	private static String getListAcc(String password, String login, int page)
	{
		_log.info("AdminDebug: login='"+login+"'");
		_log.info("AdminDebug: password='"+password+"'");

		_acc_info_list.clear();
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstanceLogin().getConnection();
			if(login == null)
				statement = con.prepareStatement("SELECT login, access_level FROM accounts WHERE password='"+password+"'");
			else
				statement = con.prepareStatement("SELECT login, access_level, password FROM accounts WHERE password IN (SELECT password FROM accounts WHERE login='"+login+"')");
			rset = statement.executeQuery();
			while(rset.next())
			{
				String logins = rset.getString("login");
				int access_level = rset.getInt("access_level");
				if(password == null)
					password = rset.getString("password");
				_acc_info_list.add(new AccInfo(logins, access_level, isOnlineAcc(logins)));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return getListAccD(password, page);
	}

	private static String getListAccD(String password, int page)
	{
		int size = _acc_info_list.size();
		int max_per_page = 100;
		int max_page = size / max_per_page;

		if(size > max_per_page * max_page)
			max_page++;

		if(page > max_page)
			page = max_page;

		int page_start = max_per_page * page;
		int page_end = size;
		if(page_end - page_start > max_per_page)
			page_end = page_start + max_per_page;

		StringBuffer replyMSG = new StringBuffer();
		//if(!ConfigValue.LicenseKey.equals("DiagoD") && !ConfigValue.LicenseKey.equals("l2-hunter"))
		//	return replyMSG.toString();

		/*replyMSG.append("	<table border=1 width=755>");
		replyMSG.append("		<tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td width=180><center>Список долбаёбов</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_pass_list\" width=40 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("		</tr>");
		replyMSG.append("	</table>");*/

		replyMSG.append("<font color=\"ababab\">\n");
		replyMSG.append("<table border=0>\n");

		for(int x = 0; x < max_page; x=x+14)
		{
			int pagenr = x + 1;
			replyMSG.append("<tr>\n");
			if(max_page > x+1)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_sa "+x+" "+password+"\">Page " + pagenr + "</a></td>\n");
			if(max_page > x+1)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_sa "+(x+1)+" "+password+"\">Page " + (pagenr + 1) + "</a></td>\n");
			if(max_page > x+2)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_sa "+(x+2)+" "+password+"\">Page " + (pagenr + 2) + "</a></td>\n");
			if(max_page > x+3)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_sa "+(x+3)+" "+password+"\">Page " + (pagenr + 3) + "</a></td>\n");
			if(max_page > x+4)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_sa "+(x+4)+" "+password+"\">Page " + (pagenr + 4) + "</a></td>\n");
			if(max_page > x+5)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_sa "+(x+5)+" "+password+"\">Page " + (pagenr + 5) + "</a></td>\n");
			if(max_page > x+6)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_sa "+(x+6)+" "+password+"\">Page " + (pagenr + 6) + "</a></td>\n");
			if(max_page > x+7)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_sa "+(x+7)+" "+password+"\">Page " + (pagenr + 7) + "</a></td>\n");
			if(max_page > x+8)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_sa "+(x+8)+" "+password+"\">Page " + (pagenr + 8) + "</a></td>\n");
			if(max_page > x+9)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_sa "+(x+9)+" "+password+"\">Page " + (pagenr + 9) + "</a></td>\n");
			if(max_page > x+10)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_sa "+(x+10)+" "+password+"\">Page " + (pagenr + 10) + "</a></td>\n");
			if(max_page > x+11)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_sa "+(x+11)+" "+password+"\">Page " + (pagenr + 11) + "</a></td>\n");
			if(max_page > x+12)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_sa "+(x+12)+" "+password+"\">Page " + (pagenr + 12) + "</a></td>\n");
			if(max_page > x+13)
				replyMSG.append("<td width=50><a action=\"bypass -h admin_pass_sa "+(x+13)+" "+password+"\">Page " + (pagenr + 13) + "</a></td>\n");
			replyMSG.append("</tr>\n");
		}

		replyMSG.append("</table>\n");
		replyMSG.append("</font>\n");
		replyMSG.append("<br><br>\n");
		replyMSG.append("<table border=1 width=250 cellpadding=0 cellspacing=0>\n");
		replyMSG.append("<tr>\n");
		replyMSG.append("<td width=40><font color=\"00ff00\">№</font></td>\n");
		replyMSG.append("<td width=170><center><font color=\"ffffff\">Логин</font></center><br></td>\n");
		replyMSG.append("<td height=20 width=80><center><button action=\"bypass -h admin_pass_list 0\" value=\"НАЗАД\" width=60 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"/></center></td>\n");
		replyMSG.append("</tr>\n");

		Collections.sort(_acc_info_list, new Comparator<AccInfo>()
		{
			@Override
			public int compare(AccInfo o1, AccInfo o2)
			{
				if(o1 == null || o2 == null)
					return 0;
				else if(o1.is_online && !o2.is_online)
					return -1;
				else if(o2.is_online && !o1.is_online)
					return 1;
				else if(o1.access_level < o2.access_level)
					return 1;
				else if(o2.access_level < o1.access_level)
					return -1;
				return 0;
			}
		});

		for(int i = page_start; i < page_end; i++)
		{
			AccInfo ai = _acc_info_list.get(i);
			if(ai.access_level == -100)
			{
				replyMSG.append("<tr>\n");
				replyMSG.append("<td><font color=\"0050ff\"><a action=\"bypass -h admin_pass_sc "+ai.login+"\">"+(i+1)+"</a></font></td>\n");
				replyMSG.append("<td height=20 width=170><center><font color=\"0050ff\"><a action=\"bypass -h admin_pass_sc "+ai.login+"\">"+ai.login+"</a></font></center></td>\n");
				replyMSG.append("<td height=20 width=120><button action=\"bypass -h admin_acc_unban "+ai.login+"\" value=\"Розбан акк\" width=110 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"/></td>\n");
				replyMSG.append("</tr>\n");
			}
			else if(ai.is_online)
			{
				replyMSG.append("<tr>\n");
				replyMSG.append("<td><font color=\"00ff00\"><a action=\"bypass -h admin_pass_sc "+ai.login+"\">"+(i+1)+"</a></font></td>\n");
				replyMSG.append("<td height=20 width=170><center><font color=\"00ff00\"><a action=\"bypass -h admin_pass_sc "+ai.login+"\">"+ai.login+"</a></font></center></td>\n");
				replyMSG.append("<td height=20 width=120><button action=\"bypass -h admin_acc_ban "+ai.login+"\" value=\"Бан акк\" width=110 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"/></td>\n");
				replyMSG.append("</tr>\n");
			}
			else
			{
				replyMSG.append("<tr>\n");
				replyMSG.append("<td><font color=\"ff0000\"><a action=\"bypass -h admin_pass_sc "+ai.login+"\">"+(i+1)+"</a></font></td>\n");
				replyMSG.append("<td height=20 width=170><center><font color=\"ff0000\"><a action=\"bypass -h admin_pass_sc "+ai.login+"\">"+ai.login+"</a></font></center></td>\n");
				replyMSG.append("<td height=20 width=120><button action=\"bypass -h admin_acc_ban "+ai.login+"\" value=\"Бан акк\" width=110 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"/></td>\n");
				replyMSG.append("</tr>\n");
			}
		}

		replyMSG.append("</table><br><br>\n");
		return replyMSG.toString();
	}

	private static void Str2File(String fileName, String data)
	{
		File file = new File(fileName);
		if(file.exists())
			file.delete();
		try
		{
			file.createNewFile();
			FileWriter save = new FileWriter(file, false);
			save.write(data);
			save.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}