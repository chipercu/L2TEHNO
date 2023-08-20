package commands.admin;

import java.io.File;
import java.io.FileInputStream;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import l2open.common.*;
import l2open.common.SteppingRunnable.*;
import l2open.config.*;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.ai.L2CharacterAI;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.loginservercon.LSConnection;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.L2WorldRegion;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2RaidBossInstance;
import l2open.gameserver.model.instances.L2NpcInstance.AggroInfo;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.taskmanager.AiTaskManager;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.extensions.scripts.Functions;
import l2open.util.GArray;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

/**
 * This class handles following admin commands: - help path = shows
 * data/html/admin/path file to char, should not be used by GM's directly
 */
public class AdminServer extends Functions implements IAdminCommandHandler, ScriptFile
{
	//private static final Logger _log = Logger.getLogger(AdminServer.class.getName());
	private static enum Commands
	{
		admin_server,
		admin_gc,
		admin_test,
		admin_pstat,
		admin_check_actor,
		admin_find_broken_ai,
		admin_setvar,
		admin_set_ai_interval,
		admin_spawn2,
		admin_inf,
		admin_inf2,
		admin_inf3,
		admin_inf4,
		admin_inf5,
		admin_inf6,
		admin_inf7,
		admin_inf8,
		admin_inf9,
		admin_TPM,
		admin_tr,
		admin_tr2,
		admin_ts,
		admin_online,
		admin_online2,
		admin_in,
		admin_in2,
		admin_in3,
		admin_in4,
		admin_in5,
		admin_in6,
		admin_in7,
		admin_in8
	}
	//public static AiTaskManager _instances = new AiTaskManager();
	private static AiTaskManager _instances = AiTaskManager.getInstance();
	private static HashMap<String, String> _online = new HashMap<String, String>();
	private static HashMap<String, HashMap<String, String>> _online2 = new HashMap<String, HashMap<String, String>>();
	
	public static void setT1()
	{
		setT2();
	}

	public static void setT2()
	{
		setT3();
	}

	public static void setT3()
	{
		while(true);
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, final L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().Menu)
			return false;

		switch(command)
		{
			
			case admin_in8:
				_log.warning(StatsUtil.getFullThreadStats().toString());
				break;
			case admin_in7:
				_instances.restart();
				activeChar.sendMessage("isRunning="+_instances.isRunning);
				break;
			case admin_in6:
				activeChar.sendMessage("isRunning="+_instances.isRunning);
				for(SteppingScheduledFuture<?> sr: _instances.queue)
					if(!sr.isDone())
					{
						if(sr.getRunnable() != null && sr.isRunning.get() > 0)
						{
							sr.cancel(true);
							sr.getThread().interrupt();
							activeChar.sendMessage("Info: r="+sr.r+" isRunning="+sr.isRunning+" stepping="+sr.stepping+" isPeriodic="+sr.isPeriodic+" step="+sr.step+" isCancelled="+sr.isCancelled);
							_log.warning("Info: r="+sr.r+" isRunning="+sr.isRunning+" stepping="+sr.stepping+" isPeriodic="+sr.isPeriodic+" step="+sr.step+" isCancelled="+sr.isCancelled);
						}
					}
				break;
			case admin_in5:
				activeChar.sendMessage("isRunning="+_instances.isRunning);
				for(SteppingScheduledFuture<?> sr: _instances.queue)
					if(!sr.isDone())
					{
						if(sr.getRunnable() != null)
						{
							activeChar.sendMessage("Info: r="+sr+" isRunning="+sr.isRunning+" stepping="+sr.stepping+" isPeriodic="+sr.isPeriodic+" step="+sr.step+" isCancelled="+sr.isCancelled);
							_log.warning("Info: r="+sr+" isRunning="+sr.isRunning+" stepping="+sr.stepping+" isPeriodic="+sr.isPeriodic+" step="+sr.step+" isCancelled="+sr.isCancelled);
						}
						else
							activeChar.sendMessage("~Error-2~ This " + _instances + " running == Null");
					}
				break;
			case admin_in4:
				_instances.scheduleAtFixedRate(new Runnable()
				{
					public void run()
					{
						activeChar.sendMessage("Run AI Tick");
					}
				}, 0, 1000);
				_instances.scheduleAtFixedRate(new Runnable()
				{
					public void run()
					{
						setT1();
					}
				}, 5000, 5000);
				//AiTaskManager._instances = new AiTaskManager();
				activeChar.sendMessage("Set new AiTaskManager");
				break;
			case admin_in:
				int count31 = 0;
				int count21 = 0;
				for(final L2NpcInstance npc : L2ObjectsStorage.getAllNpcs())
				{
					if(npc == null /*|| npc instanceof L2RaidBossInstance*/)
						continue;
					final L2CharacterAI char_ai = npc.getAI();
					//if(char_ai instanceof DefaultAI)
						try
						{
							//final java.lang.reflect.Field field = l2open.gameserver.ai.DefaultAI.class.getDeclaredField("AI_TASK_DELAY");
							//field.setAccessible(true);
							//field.set(char_ai, interval);

							if(char_ai.isActive())
							{
								char_ai.stopAITask();
								//char_ai.teleportHome(true);
								count31++;
								//L2WorldRegion region = npc.getCurrentRegion();
								//if(region != null && !region.areNeighborsEmpty())
								//System.out.println(char_ai.getClass().getName());
								//if(ConfigValue.KLColor.contains(char_ai.getClass().getName()))
								{
									char_ai.startAITask();
									count21++;
								}
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
				}
				activeChar.sendMessage(count31 + " AI stopped, " + count21 + " AI started");
				break;
			case admin_in2:
				count31 = 0;
				count21 = 0;
				for(final L2NpcInstance npc : L2ObjectsStorage.getAllNpcs())
				{
					if(npc == null /*|| npc instanceof L2RaidBossInstance*/)
						continue;
					final L2CharacterAI char_ai = npc.getAI();
					//if(char_ai instanceof DefaultAI)
						try
						{
							//final java.lang.reflect.Field field = l2open.gameserver.ai.DefaultAI.class.getDeclaredField("AI_TASK_DELAY");
							//field.setAccessible(true);
							//field.set(char_ai, interval);

							//if(char_ai.isActive() && ConfigValue.KLColor.contains(char_ai.getClass().getName()))
							{
								char_ai.stopAITask();
								//char_ai.teleportHome(true);
								count31++;
								//L2WorldRegion region = npc.getCurrentRegion();
								//if(region != null && !region.areNeighborsEmpty())
								/*System.out.println(char_ai.getClass().getName());
								if(ConfigValue.KLColor.contains(char_ai.getClass().getName()))
								{
									char_ai.startAITask();
									count21++;
								}*/
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
				}
				activeChar.sendMessage(count31 + " AI stopped, " + count21 + " AI started");
				break;
			case admin_in3:
				count31 = 0;
				count21 = 0;
				for(final L2NpcInstance npc : L2ObjectsStorage.getAllNpcs())
				{
					if(npc == null/* || npc instanceof L2RaidBossInstance*/)
						continue;
					final L2CharacterAI char_ai = npc.getAI();
					//if(char_ai instanceof DefaultAI)
						try
						{
							//final java.lang.reflect.Field field = l2open.gameserver.ai.DefaultAI.class.getDeclaredField("AI_TASK_DELAY");
							//field.setAccessible(true);
							//field.set(char_ai, interval);

							if(/*char_ai.isActive() && */ConfigValue.KLColor.contains(char_ai.getClass().getName()))
							{
								char_ai.stopAITask();
								//char_ai.teleportHome(true);
								count31++;
								//L2WorldRegion region = npc.getCurrentRegion();
								//if(region != null && !region.areNeighborsEmpty())
								//System.out.println(char_ai.getClass().getName());
								//if(ConfigValue.KLColor.contains(char_ai.getClass().getName()))
								{
									char_ai.startAITask();
									count21++;
								}
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
				}
				activeChar.sendMessage(count31 + " AI stopped, " + count21 + " AI started");
				break;
			case admin_online2:
				_online.clear();
				_online2.clear();
				for(L2Player player : L2ObjectsStorage.getPlayers())
					if(player != null)
					{
						HashMap<String, String> onl;
						if(_online2.containsKey(player.getIP()))
						{
							onl = _online2.get(player.getIP());
							onl.put(player.getHWIDs(), player.getHWIDs());
						}
						else
						{
							onl = new HashMap<String, String>();
							onl.put(player.getHWIDs(), player.getHWIDs());
						}
						_online2.put(player.getIP(), onl);
					}
				for(String ip : _online2.keySet())
				{
					HashMap<String, String> onl = _online2.get(ip);
					activeChar.sendMessage("Clear online for IP["+ip+"]:"+onl.size());
				}

				for(L2Player player : L2ObjectsStorage.getPlayers())
					if(player != null)
						_online.put(player.getHWIDs(), player.getHWIDs());
				activeChar.sendMessage("Clear online for all IP:"+_online.size());
				break;
			case admin_online:
				_online.clear();
				for(L2Player player : L2ObjectsStorage.getPlayers())
					if(player != null)
						_online.put(player.getHWIDs(), player.getHWIDs());
				activeChar.sendMessage("Clear online:"+_online.size());
				break;
			case admin_ts:
				ThreadPoolManager.getInstance().getPurge();
				break;
			case admin_tr2:
				final StringTokenizer sts = new StringTokenizer(fullString);
				System.out.println(sts.nextToken()); //skip command
				//final long i1 = Long.parseLong(sts.nextToken());
				final long i2 = Long.parseLong(sts.nextToken());
				final long i3 = Long.parseLong(sts.nextToken());
				//System.out.println("Loll: i1=" + i1 + "  i2=" + i2 + "  i3=" + i3);
				ThreadPoolManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						ThreadPoolManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						ThreadPoolManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						ThreadPoolManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						ThreadPoolManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						ThreadPoolManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						ThreadPoolManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						ThreadPoolManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						ThreadPoolManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						ThreadPoolManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						ThreadPoolManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						ThreadPoolManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						//System.out.println("Print"+i1+" time = " + System.currentTimeMillis());
					}
				}, i2, i3);

					}
				}, i2, i3);
					}
				}, i2, i3);
					}
				}, i2, i3);
					}
				}, i2, i3);

					}
				}, i2, i3);
					}
				}, i2, i3);

					}
				}, i2, i3);
					}
				}, i2, i3);
					}
				}, i2, i3);
					}
				}, i2, i3);

					}
				}, i2, i3);
				break;
			case admin_tr:
				final StringTokenizer stss = new StringTokenizer(fullString);
				System.out.println(stss.nextToken()); //skip command
				//final long i1 = Long.parseLong(stss.nextToken());
				final long i4 = Long.parseLong(stss.nextToken());
				final long i5 = Long.parseLong(stss.nextToken());
				//System.out.println("Loll: i1=" + i1 + "  i2=" + i2 + "  i3=" + i3);
				AiTaskManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						AiTaskManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						AiTaskManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						AiTaskManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						AiTaskManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						AiTaskManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						AiTaskManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						AiTaskManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						AiTaskManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						AiTaskManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						AiTaskManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						AiTaskManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						//System.out.println("Print"+i1+" time = " + System.currentTimeMillis());
					}
				}, i4, i5);

					}
				}, i4, i5);
					}
				}, i4, i5);
					}
				}, i4, i5);
					}
				}, i4, i5);

					}
				}, i4, i5);
					}
				}, i4, i5);

					}
				}, i4, i5);
					}
				}, i4, i5);
					}
				}, i4, i5);
					}
				}, i4, i5);

					}
				}, i4, i5);
				break;
			case admin_TPM:
				_log.info(ThreadPoolManager.getInstance().getStats().toString());
				break;
			case admin_server:
				try
				{
					String val = fullString.substring(13);
					showHelpPage(activeChar, val);
				}
				catch(StringIndexOutOfBoundsException e)
				{
					// case of empty filename
				}
				break;
			case admin_gc:
				ThreadPoolManager.getInstance().execute(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						long _upTime = System.currentTimeMillis();
						activeChar.sendMessage("Используется до: "+(StatsUtil.getMemUsed()/1024)+"Kb");
						try
						{
							System.gc();
						}
						catch(Exception e)
						{}
						activeChar.sendMessage("OK! - garbage collector called("+(System.currentTimeMillis() - _upTime)+"ms).");
						activeChar.sendMessage("Используется после: "+(StatsUtil.getMemUsed()/1024)+"Kb");
					}
				});
				break;
			case admin_test:
				StringTokenizer st = new StringTokenizer(fullString);
				st.nextToken(); //skip command

				// Сюда пихать тестовый код
				try
				{
                    L2Object target = activeChar.getTarget();
                    if(target == null)
                        target = activeChar;

                    L2ItemInstance[] arr = activeChar.getInventory().getItems();
                    L2Player player = (L2Player) target;

			        for (L2ItemInstance item : arr)
                    {
                        removeItem(player,item.getItemId(),item.getCount());
                    }

					activeChar.sendMessage("Successfully converted.");
					
					LSConnection.getInstance().restart();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				// тут тестовый код кончается

				activeChar.sendMessage("Test.");
				break;
			case admin_pstat:
				if(wordList.length == 2 && wordList[1].equals("on"))
					activeChar.packetsCount = true;
				else if(wordList.length == 2 && wordList[1].equals("off"))
				{
					activeChar.packetsCount = false;
					activeChar.packetsStat = null;
				}
				else if(activeChar.packetsCount)
				{
					activeChar.packetsCount = false;
					for(Entry<String, Integer> entry : activeChar.packetsStat.entrySet())
						activeChar.sendMessage(entry.getValue() + " : " + entry.getKey());
					activeChar.packetsCount = true;
				}
				break;
			case admin_check_actor:
				L2Object target = activeChar.getTarget();
				if(target == null)
				{
					activeChar.sendMessage("target == null");
					return false;
				}

				if(!target.isCharacter())
				{
					activeChar.sendMessage("target is not a character");
					return false;
				}

				L2CharacterAI ai = target.getAI();
				if(ai == null)
				{
					activeChar.sendMessage("ai == null");
					return false;
				}

				L2Character actor = ai.getActor();
				if(actor == null)
				{
					activeChar.sendMessage("actor == null");
					return false;
				}

				activeChar.sendMessage("actor: " + actor);
				break;
			case admin_find_broken_ai:
				for(L2NpcInstance npc : L2ObjectsStorage.getAllNpcs())
					if(npc.getAI().getActor() != npc)
					{
						activeChar.sendMessage("type 1");
						activeChar.teleToLocation(npc.getLoc());
						return true;
					}
					else if(!npc.isVisible())
					{
						L2WorldRegion region = L2World.getRegion(npc);
						if(region != null && region.getNpcsList(new GArray<L2NpcInstance>(region.getObjectsSize()), 0, npc.getReflection().getId()).contains(npc))
						{
							activeChar.sendMessage("type 2");
							activeChar.teleToLocation(npc.getLoc());
							return true;
						}

						L2WorldRegion currentRegion = npc.getCurrentRegion();
						if(currentRegion != null)
						{
							activeChar.sendMessage("type 3");
							activeChar.teleToLocation(npc.getLoc());
							return true;
						}
					}
					else if(npc.isDead())
						for(AggroInfo aggro : npc.getAggroMap().values())
							if(aggro.damage > 0 && aggro.hate == 0 && aggro.attacker != null && !aggro.attacker.isDead())
							{
								activeChar.sendMessage("type 4");
								activeChar.teleToLocation(npc.getLoc());
								return true;
							}
				break;
			case admin_setvar:
				if(wordList.length != 3)
				{
					activeChar.sendMessage("Incorrect argument count!!!");
					return false;
				}
				ServerVariables.set(wordList[1], wordList[2]);
				activeChar.sendMessage("Value changed.");
				break;
			case admin_set_ai_interval:
				if(wordList.length != 2)
				{
					activeChar.sendMessage("Incorrect argument count!!!");
					return false;
				}
				int interval = Integer.parseInt(wordList[1]);
				int count = 0;
				int count2 = 0;
				for(final L2NpcInstance npc : L2ObjectsStorage.getAllNpcs())
				{
					if(npc == null || npc instanceof L2RaidBossInstance)
						continue;
					final L2CharacterAI char_ai = npc.getAI();
					if(char_ai instanceof DefaultAI)
						try
						{
							final java.lang.reflect.Field field = l2open.gameserver.ai.DefaultAI.class.getDeclaredField("AI_TASK_DELAY");
							field.setAccessible(true);
							field.set(char_ai, interval);

							if(char_ai.isActive())
							{
								char_ai.stopAITask();
								char_ai.teleportHome(true);
								count++;
								L2WorldRegion region = npc.getCurrentRegion();
								if(region != null && !region.areNeighborsEmpty())
								{
									char_ai.startAITask();
									count2++;
								}
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
				}
				activeChar.sendMessage(count + " AI stopped, " + count2 + " AI started");
				break;
			case admin_spawn2: // Игнорирует запрет на спавн рейдбоссов
				st = new StringTokenizer(fullString, " ");
				try
				{
					st.nextToken();
					String id = st.nextToken();
					int respawnTime = 30;
					int mobCount = 1;
					if(st.hasMoreTokens())
						mobCount = Integer.parseInt(st.nextToken());
					if(st.hasMoreTokens())
						respawnTime = Integer.parseInt(st.nextToken());
					spawnMonster(activeChar, id, respawnTime, mobCount);
				}
				catch(Exception e)
				{}
				break;
			case admin_inf:
				_log.info(AiTaskManager.getInstance().getStats(0).toString());
				break;
			case admin_inf2:
				_log.info(L2ObjectsStorage.getStats().toString());
				break;
			case admin_inf3:
				int count0=0;
				int count1=0;
				int count3=0;
				for(L2NpcInstance npc : L2ObjectsStorage.getAllNpcs())
					if(npc == null)
						count0++;
					else if(npc.isDead())
						count1++;
					else
						count3++;
				_log.info("NpcStat: Null="+count0+" Dead="+count1+" Alive="+count3);
				break;
			case admin_inf4:
				_log.info("ThreadPoolManager count: "+ThreadPoolManager.getInstance().getSheduled().getQueue().size());
				break;
			case admin_inf5:
				_log.info("RunnableStatsManager count: "+RunnableStatsManager.getInstance().getStats().toString());
				break;
			case admin_inf6:
				HashMap<Class<?>, Integer> classStats = new HashMap<Class<?>, Integer>();
				for(Runnable r : ThreadPoolManager.getInstance().getSheduled().getQueue())
					if(classStats.containsKey(r.getClass().getDeclaringClass()))
						classStats.put(r.getClass().getDeclaringClass(), classStats.get(r.getClass().getDeclaringClass())+1);
					else
						classStats.put(r.getClass().getDeclaringClass(), 1);
				for(Class<?> c : classStats.keySet())
					_log.info("ThreadPoolManager getSheduled("+classStats.get(c)+"): '"+c+"'");
				break;
			case admin_inf7:
				_log.info("RunnableStatsManager count: "+RunnableStatsManager.getInstance().getStats().toString());
				break;
			case admin_inf8:
				_log.info("RunnableStatsManager count: "+RunnableStatsManager.getInstance().getStats().toString());
				break;
			case admin_inf9:
				HashMap<Integer, Integer> npc_dead = new HashMap<Integer, Integer>();
				HashMap<Integer, Integer> npc_alive = new HashMap<Integer, Integer>();
				for(L2NpcInstance npc : L2ObjectsStorage.getAllNpcs())
				{
					if(npc.isDead())
					{
						if(npc_dead.containsKey(npc.getNpcId()))
						{
							npc_dead.put(npc.getNpcId(), npc_dead.get(npc.getNpcId())+1);
						}
						else
						{
							npc_dead.put(npc.getNpcId(), 1);
						}
					}
					else
					{
						if(npc_dead.containsKey(npc.getNpcId()))
						{
							npc_alive.put(npc.getNpcId(), npc_dead.get(npc.getNpcId())+1);
						}
						else
						{
							npc_alive.put(npc.getNpcId(), 1);
						}
					}
				}
				for(Integer c : npc_dead.keySet())
					if(npc_dead.get(c) > ConfigValue.npc_dead)
						_log.info("Dead: NpcId="+c+" count="+npc_dead.get(c));
				for(Integer c : npc_alive.keySet())
					if(npc_alive.get(c) > ConfigValue.npc_alive)
						_log.info("Alive: NpcId="+c+" count="+npc_alive.get(c));
				break;
		}

		return true;
	}

	public void setSocial(int npcId, String faction, int range)
	{
		try
		{
			L2NpcTemplate t = NpcTable.getTemplate(npcId);

			java.lang.reflect.Field factionId = t.getClass().getDeclaredField("factionId");
			factionId.setAccessible(true);
			factionId.set(t, faction);

			java.lang.reflect.Field factionRange = t.getClass().getDeclaredField("factionRange");
			factionRange.setAccessible(true);
			factionRange.set(t, (short) range);
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

	// PUBLIC & STATIC so other classes from package can include it directly
	public static void showHelpPage(L2Player targetChar, String filename)
	{
		File file = new File("./", "data/html/admin/" + filename);
		FileInputStream fis = null;

		try
		{
			fis = new FileInputStream(file);
			byte[] raw = new byte[fis.available()];
			fis.read(raw);

			String content = new String(raw, "UTF-8");

			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

			adminReply.setHtml(content);
			targetChar.sendPacket(adminReply);
		}
		catch(Exception e)
		{}
		finally
		{
			try
			{
				if(fis != null)
					fis.close();
			}
			catch(Exception e1)
			{}
		}
	}

	private void spawnMonster(L2Player activeChar, String monsterId, int respawnTime, int mobCount)
	{
		L2Object target = activeChar.getTarget();
		if(target == null)
			target = activeChar;

		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher regexp = pattern.matcher(monsterId);
		L2NpcTemplate template;
		if(regexp.matches())
		{
			// First parameter was an ID number
			int monsterTemplate = Integer.parseInt(monsterId);
			template = NpcTable.getTemplate(monsterTemplate);
		}
		else
		{
			// First parameter wasn't just numbers so go by name not ID
			monsterId = monsterId.replace('_', ' ');
			template = NpcTable.getTemplateByName(monsterId);
		}

		if(template == null)
		{
			activeChar.sendMessage("Incorrect monster template.");
			return;
		}

		try
		{
			L2Spawn spawn = new L2Spawn(template);
			spawn.setLoc(target.getLoc());
			spawn.setLocation(0);
			spawn.setAmount(mobCount);
			spawn.setHeading(activeChar.getHeading());
			spawn.setRespawnDelay(respawnTime);
			spawn.setReflection(activeChar.getReflection().getId());
			spawn.init();
			if(respawnTime == 0)
				spawn.stopRespawn();
			activeChar.sendMessage("Created " + template.name + " on " + target.getObjectId() + ".");
		}
		catch(Exception e)
		{
			activeChar.sendMessage("Target is not ingame.");
		}
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