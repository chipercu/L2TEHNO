package services;

import l2open.config.ConfigValue;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Util;

public class TeleToParnassus extends Functions implements ScriptFile
{
	private static GArray<L2Spawn> _spawns = new GArray<L2Spawn>();

	private L2Zone _zone = ZoneManager.getInstance().getZoneById(ZoneType.offshore, 500032, false);
	private ZoneListener _zoneListener = new ZoneListener();

	public void onLoad()
	{
		if(ConfigValue.ParnassusZone)
		{
			try
			{
				// init reflection
				ReflectionTable.getInstance().get(-1, true).setCoreLoc(new Location(149384, 171896, -952));

				// spawn wh keeper
				L2Spawn sp1 = new L2Spawn(NpcTable.getTemplate(30086));
				sp1.setLocx(149960);
				sp1.setLocy(174136);
				sp1.setLocz(-920);
				sp1.setAmount(1);
				sp1.setHeading(32768);
				sp1.setRespawnDelay(5);
				sp1.init();
				sp1.getAllSpawned().iterator().next().setReflection(-1);
				_spawns.add(sp1);

				// spawn grocery trader (Helvetia)
				L2Spawn sp2 = new L2Spawn(NpcTable.getTemplate(30839));
				sp2.setLocx(149368);
				sp2.setLocy(174264);
				sp2.setLocz(-896);
				sp2.setAmount(1);
				sp2.setHeading(49152);
				sp2.setRespawnDelay(5);
				sp2.init();
				sp2.getAllSpawned().iterator().next().setReflection(-1);
				_spawns.add(sp2);

				// spawn gk
				L2NpcTemplate t = NpcTable.getTemplate(36394);
				t.displayId = 36394;
				t.title = "Gatekeeper";
				t.ai_type = "npc";
				L2Spawn sp3 = new L2Spawn(t);
				sp3.setLocx(149368);
				sp3.setLocy(172568);
				sp3.setLocz(-952);
				sp3.setAmount(1);
				sp3.setHeading(49152);
				sp3.setRespawnDelay(5);
				sp3.init();
				sp3.getAllSpawned().iterator().next().setReflection(-1);
				_spawns.add(sp3);

				// spawn Orion the Cat
				L2Spawn sp5 = new L2Spawn(NpcTable.getTemplate(31860));
				sp5.setLocx(148904);
				sp5.setLocy(173656);
				sp5.setLocz(-952);
				sp5.setAmount(1);
				sp5.setHeading(49152);
				sp5.setRespawnDelay(5);
				sp5.init();
				sp5.getAllSpawned().iterator().next().setReflection(-1);
				_spawns.add(sp5);

				// spawn blacksmith (Pushkin)
				L2Spawn sp6 = new L2Spawn(NpcTable.getTemplate(30300));
				sp6.setLocx(148760);
				sp6.setLocy(174136);
				sp6.setLocz(-920);
				sp6.setAmount(1);
				sp6.setHeading(0);
				sp6.setRespawnDelay(5);
				sp6.init();
				sp6.getAllSpawned().iterator().next().setReflection(-1);
				_spawns.add(sp6);

				// spawn Item Broker
				L2Spawn sp7 = new L2Spawn(NpcTable.getTemplate(32320));
				sp7.setLocx(149368);
				sp7.setLocy(173064);
				sp7.setLocz(-952);
				sp7.setAmount(1);
				sp7.setHeading(16384);
				sp7.setRespawnDelay(5);
				sp7.init();
				sp7.getAllSpawned().iterator().next().setReflection(-1);
				_spawns.add(sp7);
			}
			catch(SecurityException e)
			{
				e.printStackTrace();
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}

			_zone.getListenerEngine().addMethodInvokedListener(_zoneListener);

			ZoneManager.getInstance().getZoneById(ZoneType.peace_zone, 500031, false).setActive(true);
			ZoneManager.getInstance().getZoneById(ZoneType.offshore, 500032, false).setActive(true);
			ZoneManager.getInstance().getZoneById(ZoneType.dummy, 500033, false).setActive(true);

			_log.info("Loaded Service: Teleport to Parnassus");
		}
	}

	public void onReload()
	{
		_zone.getListenerEngine().removeMethodInvokedListener(_zoneListener);
		for(L2Spawn spawn : _spawns)
			spawn.despawnAll();
		_spawns.clear();
	}

	public void onShutdown()
	{}

	public void toParnassus()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		if(!L2NpcInstance.canBypassCheck(player, npc))
			return;

		if(player.getAdena() < ConfigValue.ParnassusPrice)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}

		player.reduceAdena(ConfigValue.ParnassusPrice, true);
		player.setVar("backCoords", player.getLoc().toXYZString());
		player.teleToLocation(_zone.getSpawn().rnd(30, 200, false), -1);
	}

	public void fromParnassus()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		if(!L2NpcInstance.canBypassCheck(player, npc))
			return;

		String var = player.getVar("backCoords");
		if(var == null || var.equals(""))
		{
			teleOut();
			return;
		}
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		player.teleToLocation(new Location(var), 0);
		player.unsetVar("backCoords");
	}

	public void teleOut()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		player.teleToLocation(46776, 185784, -3528, 0);
		show(player.isLangRus() ? "Я не знаю, как Вы попали сюда, но я могу Вас отправить за ограждение." : "I don't know from where you came here, but I can teleport you the another border side.", player, npc);
	}

	public String DialogAppend_30059(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30080(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30177(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30233(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30256(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30320(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30848(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30878(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30899(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_31210(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_31275(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_31320(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_31964(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30006(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30134(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30146(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_32163(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30576(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30540(Integer val)
	{
		return getHtmlAppends(val);
	}

	private static final String en = "<br1>[scripts_services.TeleToParnassus:toParnassus @811;Parnassus|\"Move to Parnassus (offshore zone) - " + ConfigValue.ParnassusPrice + " Adena.\"]<br1>";
	private static final String ru = "<br1>[scripts_services.TeleToParnassus:toParnassus @811;Parnassus|\"Parnassus (торговая зона без налогов) - " + ConfigValue.ParnassusPrice + " Adena.\"]<br1>";

	public String getHtmlAppends(Integer val)
	{
		if(val != 0 || !ConfigValue.ParnassusZone)
			return "";
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return "";
		return player.isLangRus() ? ru : en;
	}

	public String DialogAppend_36394(Integer val)
	{
		return getHtmlAppends2(val);
	}

	private static final String en2 = "<br>[scripts_services.ManaRegen:DoManaRegen|Full MP Regeneration. (1 MP for 5 Adena)]<br1>[scripts_services.TeleToParnassus:fromParnassus @811;From Parnassus|\"Exit the Parnassus.\"]<br1>";
	private static final String ru2 = "<br>[scripts_services.ManaRegen:DoManaRegen|Полное восстановление MP. (1 MP за 5 Adena)]<br1>[scripts_services.TeleToParnassus:fromParnassus @811;From Parnassus|\"Покинуть Parnassus.\"]<br1>";

	public String getHtmlAppends2(Integer val)
	{
		if(val != 0 || !ConfigValue.ParnassusZone)
			return "";
		L2Player player = (L2Player) getSelf();
		if(player == null || player.getReflection().getId() != -1)
			return "";
		return player.isLangRus() ? ru2 : en2;
	}

	public class ZoneListener extends L2ZoneEnterLeaveListener
	{
		@Override
		public void objectEntered(L2Zone zone, L2Object object)
		{
		// обрабатывать вход в зону не надо, только выход
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{
			L2Player player = object.getPlayer();
			if(ConfigValue.ParnassusZone && player.getReflection().getId() == -1 && player.isVisible())
			{
				L2Playable playable = (L2Playable) object;
				double angle = Util.convertHeadingToDegree(playable.getHeading()); // угол в градусах
				double radian = Math.toRadians(angle - 90); // угол в радианах
				playable.teleToLocation((int) (playable.getX() + 50 * Math.sin(radian)), (int) (playable.getY() - 50 * Math.cos(radian)), playable.getZ());
			}
		}
	}
}