package commands.admin;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.scripts.ScriptFile;
import l2open.common.*;
import l2open.common.SteppingRunnable.*;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.taskmanager.AiTaskManager;

import java.lang.StackTraceElement;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class AdminDevelop implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_db_reload,
		admin_t_ai,
		admin_t_all,
		admin_t_ai3
	}

	private static final MemoryMXBean memMXbean = ManagementFactory.getMemoryMXBean();
	private static final ThreadMXBean threadMXbean = ManagementFactory.getThreadMXBean();
	private static AiTaskManager _instances = AiTaskManager.getInstance();

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;

		switch(command)
		{
			case admin_t_ai3:
				StringBuilder list = new StringBuilder();
				for(ThreadInfo info : threadMXbean.dumpAllThreads(true, true))
				{
					list.append("Thread #").append(info.getThreadId()).append(" (").append(info.getThreadName()).append(")").append("\n");
					list.append("=================================================\n");
					list.append("\tgetThreadState: ...... ").append(info.getThreadState()).append("\n");
					list.append("\tgetWaitedTime: ....... ").append(info.getWaitedTime()).append("\n");
					list.append("\tgetBlockedTime: ...... ").append(info.getBlockedTime()).append("\n");
					for(MonitorInfo monitorInfo : info.getLockedMonitors())
					{
						list.append("\tLocked monitor: ....... ").append(monitorInfo).append("\n");
						list.append("\t\t[").append(monitorInfo.getLockedStackDepth()).append(".]: at ").append(monitorInfo.getLockedStackFrame()).append("\n");
					}

					for(LockInfo lockInfo : info.getLockedSynchronizers())
						list.append("\tLocked synchronizer: ...").append(lockInfo).append("\n");
					{
						list.append("\tgetStackTace: ..........\n");
						for(StackTraceElement trace : info.getStackTrace())
							list.append("\t\tat ").append(trace).append("\n");
					}
					list.append("=================================================\n");
				}
				_log.warning(list.toString());
				break;
			case admin_t_all:
				_log.warning(StatsUtil.getFullThreadStats().toString());
				break;
			case admin_t_ai:
				
				for(SteppingScheduledFuture<?> sr: _instances.queue)
					if(!sr.isDone())
					{
						if(sr.getRunnable() != null && sr.isRunning.get() > 0)
						{
							ThreadInfo info = threadMXbean.getThreadInfo(sr.getThread().getId(), Integer.MAX_VALUE);
							String _log2 = "Stack: ";

							StackTraceElement[] stack = info.getStackTrace();
							for(int i = 0;i<stack.length;i++)
							{
								StackTraceElement el = stack[i];
								if(el != null)
								{
									if(el.getFileName() != null)
										_log2 += el.getFileName().replace(".java", "");
									if(el.getMethodName() != null)
										_log2 += ":"+el.getMethodName();
									_log2 += "("+el.getLineNumber()+")<-";
								}
							}
							_log.warning("Info: r="+sr+"["+sr.getThread().getId()+"] "+_log2+" isRunning="+sr.isRunning+" stepping="+sr.stepping+" isPeriodic="+sr.isPeriodic+" step="+sr.step+" isCancelled="+sr.isCancelled);
						}
						else if(sr.getRunnable() == null)
							activeChar.sendMessage("~Error-2~ This " + _instances + " running == Null");
					}
				break;
			case admin_db_reload:
				/*try
				{
					L2DatabaseFactory.getInstance().shutdown();

					L2DatabaseFactory a = L2DatabaseFactory.getInstance();
					L2DatabaseFactory a2 = L2DatabaseFactory.getInstanceLogin();

					java.lang.reflect.Field f = L2DatabaseFactory.class.getDeclaredField("_instance");
					f.setAccessible(true); 
					f.set(a, new L2DatabaseFactory());

					java.lang.reflect.Field f2 = L2DatabaseFactory.class.getDeclaredField("_instanceLogin");
					f2.setAccessible(true); 

					if(ConfigValue.URL.equalsIgnoreCase(ConfigValue.Accounts_URL))
						f2.set(a2, L2DatabaseFactory.getInstance());
					else
					{
						f2.set(a2, new L2DatabaseFactory(ConfigValue.Accounts_URL, ConfigValue.Accounts_Login, ConfigValue.Accounts_Password, ConfigValue.MaximumDbConnections/2, ConfigValue.MaxIdleConnectionTimeout));

					}
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				try
				{
					L2DatabaseFactory.getInstance().shutdown();
					L2DatabaseFactory._instance = new L2DatabaseFactory();
					if(ConfigValue.URL.equalsIgnoreCase(ConfigValue.Accounts_URL))
						L2DatabaseFactory.getInstance()._instanceLogin = L2DatabaseFactory.getInstance();
					else
					{
						L2DatabaseFactory.getInstance()._instanceLogin = new L2DatabaseFactory(ConfigValue.Accounts_URL, ConfigValue.Accounts_Login, ConfigValue.Accounts_Password, ConfigValue.MaximumDbConnections/2, ConfigValue.MaxIdleConnectionTimeout);

					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				break;
		}

		return true;
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