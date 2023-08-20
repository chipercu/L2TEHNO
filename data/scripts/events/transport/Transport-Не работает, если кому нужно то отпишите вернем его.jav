package events.transport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import l2open.config.ConfigValue;
import l2open.extensions.listeners.AbstractAINotifyEventListener;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.ai.AbstractAI;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncOwner;
import l2open.gameserver.skills.funcs.FuncSet;
import l2open.gameserver.tables.PetDataTable;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Util;

public class Transport extends Functions implements ScriptFile
{
	private static HashMap<String, Wyvern> wyverns;
	private static ConcurrentHashMap<Integer, Rider> _riders = new ConcurrentHashMap<Integer, Rider>();

	private static NotifyEventListener _notifyEventListener = new NotifyEventListener();

	private static boolean _active = false;

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return ServerVariables.getString("transport", "off").equalsIgnoreCase("on");
	}

	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			loadWyvernPath();
			_log.info("Loaded Event: Transport [state: activated]");
		}
		else
		{
			wyverns = null;
			_log.info("Loaded Event: Transport [state: deactivated]");
		}
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	/**
	* Запускает эвент
	*/
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(!isActive())
		{
			ServerVariables.set("transport", "on");
			loadWyvernPath();
			_log.info("Event 'Transport' started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.transport.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event 'Transport' already started.");

		_active = true;

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	* Останавливает эвент
	*/
	public void stopEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;
		if(isActive())
		{
			ServerVariables.unset("transport");
			wyverns = null;
			_log.info("Event 'Transport' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.transport.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event 'Transport' not started.");

		_active = false;

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	private void loadWyvernPath()
	{
		LineNumberReader lnr = null;
		wyverns = new HashMap<String, Wyvern>();
		try
		{
			File wyvernData = new File(ConfigValue.DatapackRoot, "data/csv/wyvernpath.csv");
			lnr = new LineNumberReader(new BufferedReader(new FileReader(wyvernData)));
			String line = null;
			while((line = lnr.readLine()) != null)
			{
				if(line.trim().length() == 0 || line.startsWith("#"))
					continue;
				Wyvern W = new Wyvern();
				W.parseLine(line);
				wyverns.put(W.name, W);
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
				if(lnr != null)
					lnr.close();
			}
			catch(Exception e1)
			{ /* ignore problems */}
		}
	}

	public class Wyvern implements FuncOwner
	{
		public GArray<Location> path;
		public String name;

		public void parseLine(String line)
		{
			path = new GArray<Location>();
			StringTokenizer st = new StringTokenizer(line, " ");
			name = st.nextToken();
			while(st.hasMoreTokens())
			{
				Location point = null;
				String token = st.nextToken();
				StringTokenizer points = new StringTokenizer(token, ";");
				if(token.startsWith("t"))
				{
					points.nextToken();
					point = new Location(Integer.parseInt(points.nextToken()), Integer.parseInt(points.nextToken()), Integer.parseInt(points.nextToken()), -1);
				}
				else
					point = new Location(Integer.parseInt(points.nextToken()), Integer.parseInt(points.nextToken()), Integer.parseInt(points.nextToken()));
				//point.setZ(point.z + 250);
				point.setZ(Math.max(GeoEngine.getHeight(point.x, point.y, point.z + 1000, 0) + 1000, point.z));
				//point.setZ(Math.max(GeoEngine.getHeight(point.setZ(point.z + 1000), 0) + 1000, point.z));
				if(!path.isEmpty())
				{
					Location previous = path.get(path.size() - 1);
					double len = Util.calculateDistance(point.x, point.y, point.z, previous.x, previous.y, previous.z, true);
					if(len > 2000)
					{
						double steps = Math.ceil(len / 2000.);
						for(int i = 1; i < steps; i++)
						{
							Location loc = new Location((int) (previous.x + i * (point.x - previous.x) / steps), (int) (previous.y + i * (point.y - previous.y) / steps), (int) (previous.z + i * (point.z - previous.z) / steps));
							loc.setZ(Math.max(GeoEngine.getHeight(loc.x, loc.y, loc.z + 1000, 0) + 1000, loc.z));
							path.add(loc);
						}
					}
				}
				path.add(point);
			}
			Location last = path.get(path.size() - 1);
			last.setZ(GeoEngine.getHeight(last, 0) + 250);
		}

		@Override
		public boolean isFuncEnabled()
		{
			return true;
		}

		@Override
		public boolean overrideLimits()
		{
			return true;
		}
	}

	public class Rider
	{
		public Wyvern W;
		public L2Player P;
		public Stack<Location> way;
	}

	public void HireWyvern(String[] param)
	{
		if(param.length < 2)
			throw new IllegalArgumentException();

		if(!_active)
			return;

		loadWyvernPath();

		if(wyverns == null)
			return;

		L2Player player = (L2Player) getSelf();

		int price = Integer.parseInt(param[1]);

		int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if(day != 1 && day != 7 && (hour <= 12 || hour >= 22))
			price /= 2;

		if(player.isMounted() || !L2NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		if(player.getPet() != null || player.getTransformation() != 0)
		{
			player.sendPacket(Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
			return;
		}

		if(player.getAdena() < price)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		if(player.isInOlympiadMode())
		{
			player.sendMessage("Вы участвуете в олимпиаде");
			return;
		}

		if(price > 0)
			player.reduceAdena(price, true);

		player._stablePoint = player.getLoc().setH(price);
		player.setVar("wyvern_moneyback", String.valueOf(price));

		Wyvern W = wyverns.get(param[0]);

		Rider r = new Rider();
		r.P = player;
		r.W = W;
		r.way = new Stack<Location>();
		r.way.addAll(W.path);
		_riders.put(player.getObjectId(), r);

		player.setHeading(r.way.get(1), true);
		player.validateLocation(1);
		player.setMount(PetDataTable.WYVERN_ID, 0, 0);
		player.block();
		player.setIsInvul(true);
		player.addStatFunc(new FuncSet(Stats.p_speed, 0x90, W, 500));

		player.getAI().getListenerEngine().addMethodInvokedListener(_notifyEventListener);
		flyNext(r);
		player.broadcastUserInfo(true);
	}

	public static class NotifyEventListener extends AbstractAINotifyEventListener
	{
		@Override
		public void NotifyEvent(AbstractAI ai, CtrlEvent evt, Object[] args)
		{
			if(evt == CtrlEvent.EVT_ARRIVED || evt == CtrlEvent.EVT_TELEPORTED)
			{
				if(ai == null)
					return;
				L2Character actor = ai.getActor();
				if(actor == null)
					return;
				Rider r = _riders.get(actor.getObjectId());
				if(r == null)
					return;
				flyNext(r);
			}
		}
	}

	private static void flyNext(final Rider r)
	{
		if(!r.way.empty())
		{
			// летим в следующую точку
			Location next = r.way.remove(0);
			if(r.P.getLastClientPosition() != null && Util.getDistance(r.P.getLastClientPosition().x, r.P.getLastClientPosition().y, r.P.getX(), r.P.getY()) > 500)
				r.P.validateLocation(1);
			if(next.h == -1 || !r.P.moveToLocation(next, 0, false))
				r.P.teleToLocation(next);
		}
		else
			// прилетели
			cancel(r, false);
	}

	private static void cancel(Rider r, boolean moneyback)
	{
		if(moneyback)
		{
			r.P.teleToLocation(r.P._stablePoint);
			Functions.addItem(r.P, 57, Integer.parseInt(r.P.getVar("wyvern_moneyback")));
		}
		r.P.setMount(0, 0, 0);
		r.P._stablePoint = null;
		r.P.unsetVar("wyvern_moneyback");
		r.P.removeStatsOwner(r.W);
		r.P.setLastServerPosition(null);
		r.P.setLastClientPosition(null);
		r.P.setIsInvul(false);
		r.P.unblock();
		r.P.getAI().getListenerEngine().removeMethodInvokedListener(_notifyEventListener);
		_riders.remove(r);
		r.P.broadcastUserInfo(true);
	}

	public String DialogAppend_31212(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31212.htm", player);
		}
		return "";
	}

	public String DialogAppend_31213(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31213.htm", player);
		}
		return "";
	}

	public String DialogAppend_31214(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31214.htm", player);
		}
		return "";
	}

	public String DialogAppend_31215(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31215.htm", player);
		}
		return "";
	}

	public String DialogAppend_31216(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31216.htm", player);
		}
		return "";
	}

	public String DialogAppend_31217(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31217.htm", player);
		}
		return "";
	}

	public String DialogAppend_31218(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31218.htm", player);
		}
		return "";
	}

	public String DialogAppend_31219(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31219.htm", player);
		}
		return "";
	}

	public String DialogAppend_31220(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31220.htm", player);
		}
		return "";
	}

	public String DialogAppend_31221(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31221.htm", player);
		}
		return "";
	}

	public String DialogAppend_31222(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31222.htm", player);
		}
		return "";
	}

	public String DialogAppend_31223(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31223.htm", player);
		}
		return "";
	}

	public String DialogAppend_31224(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31224.htm", player);
		}
		return "";
	}

	public String DialogAppend_31767(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31767.htm", player);
		}
		return "";
	}

	public String DialogAppend_31768(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/31768.htm", player);
		}
		return "";
	}

	public String DialogAppend_32048(Integer val)
	{
		if(_active && val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/transport/32048.htm", player);
		}
		return "";
	}
}
