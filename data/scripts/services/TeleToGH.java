package services;

import l2open.config.ConfigValue;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
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

public class TeleToGH extends Functions implements ScriptFile
{
	private static GArray<L2Spawn> _spawns = new GArray<L2Spawn>();

	private L2Zone _zone = ZoneManager.getInstance().getZoneById(ZoneType.offshore, 500014, false);
	private ZoneListener _zoneListener = new ZoneListener();

	public void onLoad()
	{
		if(ConfigValue.GiranHarborZone)
		{
			try
			{
				// init reflection
				ReflectionTable.getInstance().get(-2, true).setCoreLoc(new Location(47416, 186568, -3480));

				// spawn wh keeper
				L2Spawn sp1 = new L2Spawn(NpcTable.getTemplate(30086));
				sp1.setLocx(48059);
				sp1.setLocy(186791);
				sp1.setLocz(-3512);
				sp1.setAmount(1);
				sp1.setHeading(42000);
				sp1.setRespawnDelay(5);
				sp1.init();
				sp1.getAllSpawned().iterator().next().setReflection(-2);
				_spawns.add(sp1);

				// spawn grocery trader
				L2Spawn sp2 = new L2Spawn(NpcTable.getTemplate(32169));
				sp2.setLocx(48146);
				sp2.setLocy(186753);
				sp2.setLocz(-3512);
				sp2.setAmount(1);
				sp2.setHeading(42000);
				sp2.setRespawnDelay(5);
				sp2.init();
				sp2.getAllSpawned().iterator().next().setReflection(-2);
				_spawns.add(sp2);

				// spawn gk
				L2NpcTemplate t = NpcTable.getTemplate(36394);
				t.displayId = 36394;
				t.title = "Gatekeeper";
				t.ai_type = "npc";
				L2Spawn sp3 = new L2Spawn(t);
				sp3.setLocx(47984);
				sp3.setLocy(186832);
				sp3.setLocz(-3445);
				sp3.setAmount(1);
				sp3.setHeading(42000);
				sp3.setRespawnDelay(5);
				sp3.init();
				sp3.getAllSpawned().iterator().next().setReflection(-2);
				_spawns.add(sp3);

				// spawn Orion the Cat
				L2Spawn sp5 = new L2Spawn(NpcTable.getTemplate(31860));
				sp5.setLocx(48129);
				sp5.setLocy(186828);
				sp5.setLocz(-3512);
				sp5.setAmount(1);
				sp5.setHeading(45452);
				sp5.setRespawnDelay(5);
				sp5.init();
				sp5.getAllSpawned().iterator().next().setReflection(-2);
				_spawns.add(sp5);

				// spawn blacksmith (Pushkin)
				L2Spawn sp6 = new L2Spawn(NpcTable.getTemplate(30300));
				sp6.setLocx(48102);
				sp6.setLocy(186772);
				sp6.setLocz(-3512);
				sp6.setAmount(1);
				sp6.setHeading(42000);
				sp6.setRespawnDelay(5);
				sp6.init();
				sp6.getAllSpawned().iterator().next().setReflection(-2);
				_spawns.add(sp6);

				// spawn Item Broker
				L2Spawn sp7 = new L2Spawn(NpcTable.getTemplate(32320));
				sp7.setLocx(47772);
				sp7.setLocy(186905);
				sp7.setLocz(-3480);
				sp7.setAmount(1);
				sp7.setHeading(42000);
				sp7.setRespawnDelay(5);
				sp7.init();
				sp7.getAllSpawned().iterator().next().setReflection(-2);
				_spawns.add(sp7);

				// spawn Item Broker
				L2Spawn sp8 = new L2Spawn(NpcTable.getTemplate(32320));
				sp8.setLocx(46360);
				sp8.setLocy(187672);
				sp8.setLocz(-3480);
				sp8.setAmount(1);
				sp8.setHeading(42000);
				sp8.setRespawnDelay(5);
				sp8.init();
				sp8.getAllSpawned().iterator().next().setReflection(-2);
				_spawns.add(sp8);

				// spawn Item Broker
				L2Spawn sp9 = new L2Spawn(NpcTable.getTemplate(32320));
				sp9.setLocx(49016);
				sp9.setLocy(185960);
				sp9.setLocz(-3480);
				sp9.setAmount(1);
				sp9.setHeading(42000);
				sp9.setRespawnDelay(5);
				sp9.init();
				sp9.getAllSpawned().iterator().next().setReflection(-2);
				_spawns.add(sp9);
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

			ZoneManager.getInstance().getZoneById(ZoneType.offshore, 500014, false).setActive(true);
			ZoneManager.getInstance().getZoneById(ZoneType.peace_zone, 500023, false).setActive(true);
			ZoneManager.getInstance().getZoneById(ZoneType.dummy, 500024, false).setActive(true);

			_log.info("Loaded Service: Teleport to Giran Harbor");
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

	public void toGH()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		if(!L2NpcInstance.canBypassCheck(player, npc))
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		player.setVar("backCoords", player.getLoc().toXYZString());
		player.teleToLocation(_zone.getSpawn().rnd(30, 200, false), -2);
	}

	public void fromGH()
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

	private static final String en = "<br1>[scripts_services.TeleToGH:toGH @811;Giran Harbor|\"I want free admision to the Giran Harbor.\"]<br1>";
	private static final String ru = "<br1>[scripts_services.TeleToGH:toGH @811;Giran Harbor|\"Я хочу бесплатно попасть в Giran Harbor.\"]<br1>";

	public String getHtmlAppends(Integer val)
	{
		if(val != 0 || !ConfigValue.GiranHarborZone)
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

	private static final String en2 = "<br>[scripts_services.ManaRegen:DoManaRegen|Full MP Regeneration. (1 MP for 5 Adena)]<br1>[scripts_services.TeleToGH:fromGH @811;From Giran Harbor|\"Exit the Giran Harbor.\"]<br1>";
	private static final String ru2 = "<br>[scripts_services.ManaRegen:DoManaRegen|Полное восстановление MP. (1 MP за 5 Adena)]<br1>[scripts_services.TeleToGH:fromGH @811;From Giran Harbor|\"Покинуть Giran Harbor.\"]<br1>";

	public String getHtmlAppends2(Integer val)
	{
		if(val != 0 || !ConfigValue.GiranHarborZone)
			return "";
		L2Player player = (L2Player) getSelf();
		if(player == null || player.getReflection().getId() != -2)
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
			if(ConfigValue.GiranHarborZone && player.getReflection().getId() == -2 && player.isVisible())
			{
				L2Playable playable = (L2Playable) object;
				double angle = Util.convertHeadingToDegree(playable.getHeading()); // угол в градусах
				double radian = Math.toRadians(angle - 90); // угол в радианах
				playable.teleToLocation((int) (playable.getX() + 50 * Math.sin(radian)), (int) (playable.getY() - 50 * Math.cos(radian)), playable.getZ());
			}
		}
	}
}