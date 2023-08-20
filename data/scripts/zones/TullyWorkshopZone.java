package zones;

import gnu.trove.TIntHashSet;
import javolution.util.FastList;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.instancemanager.RaidBossSpawnManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.tables.DoorTable;
import l2open.util.Location;
import l2open.util.NpcUtils;
import l2open.util.Rnd;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * @author: Drizzy
 */
public class TullyWorkshopZone implements ScriptFile
{
	private ZoneListener _zoneListener = new ZoneListener();
	private L2Zone zone;
	private static final int[] zones = {
			797551,
			797552,
			797553,
			797554
	};

	//Спаун при убийстве тулли.
	private static final int[][] POST_MORTEM_SPAWNLIST =
	{
		// Ingenious Contraption
		{
			32371, -12524, 273932, -9014, 49151, 0
		},
		// Ingenious Contraption
		{
			32371, -10831, 273890, -9040, 81895, 0
		},
		// Ingenious Contraption
		{
			32371, -10817, 273986, -9040, -16452, 0
		},
		// Ingenious Contraption
		{
			32371, -13773, 275119, -9040, 49151, 0
		},
		// Ingenious Contraption
		{
			32371, -11547, 271772, -9040, -19124, 0
		},
		// Failed Experimental Timetwister Golem
		{
			22392, -10832, 273808, -9040, 0, 0
		},
		// Failed Experimental Timetwister Golem
		{
			22392, -10816, 274096, -9040, 14964, 0
		},
		// Failed Experimental Timetwister Golem
		{
			22392, -13824, 275072, -9040, -24644, 0
		},
		// Failed Experimental Timetwister Golem
		{
			22392, -11504, 271952, -9040, 9328, 0
		},
		// Failed Experimental Timetwister Golem
		{
			22392, -11680, 275353, -9040, 0, 0
		},
		// Failed Experimental Timetwister Golem
		{
			22392, -12388, 271668, -9040, 0, 0
		},
		// Old Dwarven Ghost
		{
			32370, -11984, 272928, -9040, 23644, 900000
		},
		// Old Dwarven Ghost
		{
			32370, -14643, 274588, -9040, 49152, 0
		},
		// Spooky Tombstone
		{
			32344, -14756, 274788, -9040, -13868, 0
		}
	};

	//Разные перменные.
	private static int countdownTime;
	private static ScheduledFuture<?> _countdown = null;
	public static List<L2NpcInstance> postMortemSpawn = new FastList<L2NpcInstance>();
	public static TIntHashSet brokenContraptions = new TIntHashSet();
	public static TIntHashSet rewardedContraptions = new TIntHashSet();
	public static TIntHashSet talkedContraptions = new TIntHashSet();
	private static L2Spawn pillarSpawn = null;

	@Override
	public void onLoad()
	{
		if(ConfigValue.TullyWorkshopZoneEnable)
		{
			_zoneListener = new ZoneListener();

			_log.info("Loaded Successful");
			for(int s : zones)
			{
				zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.dummy, s);
				zone.getListenerEngine().addMethodInvokedListener(_zoneListener);
			}
			//Запускаем метод на спаун нпсов, если тулли мёртв.
			doOnLoadSpawn();
		}
	}

	//метод спауна тулли (вызывается из АИ).
	public static void SpawnTully(L2NpcInstance npc)
	{
		if ((npc.getNpcId() == 25544) && npc.isInRange(new Location(-12557, 273901, -9000), 1000))
		{
			for (L2NpcInstance spawnedNpc : postMortemSpawn)
			{
				if (spawnedNpc != null)
				{
					spawnedNpc.deleteMe();
				}
			}
			postMortemSpawn.clear();
		}
	}

	//Метод смерти тулли (вызывается из АИ).
	public static void DeadTully(L2NpcInstance npc)
	{
		if((npc.getNpcId() == 25544) && npc.isInRange(new Location(-12557, 273901, -9000), 1000))
		{
			for (int i[] : POST_MORTEM_SPAWNLIST)
			{
				L2NpcInstance spawnedNpc = NpcUtils.spawnSingle(i[0], i[1], i[2], i[3], i[4], i[5]);
				postMortemSpawn.add(spawnedNpc);
			}

			DoorTable.getInstance().getDoor(19260051).openMe();
			DoorTable.getInstance().getDoor(19260052).openMe();

			countdownTime = 600000;
			_countdown = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CountdownTask(), 60000, 10000);
			NpcSay ns = new NpcSay(postMortemSpawn.get(0), Say2C.NPC_SHOUT, 1800117, Integer.toString((countdownTime / 60000)));
			postMortemSpawn.get(0).broadcastPacket(ns);
		}
	}

	//Пул на запуск дангер зоны (наносит 2к дамаг в секунду).
	private static class CountdownTask extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			countdownTime -= 10000;
			L2NpcInstance npc = null;
			if ((postMortemSpawn != null) && (postMortemSpawn.size() > 0))
			{
				npc = postMortemSpawn.get(0);
			}
			if (countdownTime > 60000)
			{
				if ((countdownTime % 60000) == 0)
				{
					if ((npc != null) && (npc.getNpcId() == 32371))
					{
						NpcSay ns = new NpcSay(npc, Say2C.NPC_SHOUT, 1010643, Integer.toString((countdownTime / 60000)));
						npc.broadcastPacket(ns);
					}
				}
			}
			else if (countdownTime <= 0)
			{
				if (_countdown != null)
				{
					_countdown.cancel(false);
					_countdown = null;
				}

				if (postMortemSpawn != null)
				{
					for (L2NpcInstance spawnedNpc : postMortemSpawn)
					{
						if ((spawnedNpc != null) && ((spawnedNpc.getNpcId() == 32371) || (spawnedNpc.getNpcId() == 22392)))
						{
							spawnedNpc.deleteMe();
						}
					}
				}

				brokenContraptions.clear();
				rewardedContraptions.clear();
				talkedContraptions.clear();

				final L2Zone dmgZone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.damage, 200011);
				if (dmgZone != null)
				{
					dmgZone.setActive(true);
				}
				ThreadPoolManager.getInstance().schedule(new disableZone(), 300000);
			}
			else
			{
				if ((npc != null) && (npc.getNpcId() == 32371))
				{
					final NpcSay ns = new NpcSay(npc, Say2C.NPC_SHOUT, 1800079, Integer.toString((countdownTime / 1000)));
					npc.broadcastPacket(ns);
				}
			}
		}
	}

	//Пул на выключение зоны.
	private static class disableZone extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			final L2Zone dmgZone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.damage, 200011);
			if (dmgZone != null)
			{
				dmgZone.setActive(false);
			}
		}
	}

	//Обработка смерти PILLAR
	public static void onDeadPillar(L2NpcInstance npc)
	{
		NpcUtils.spawnSingle(32370, npc.getX() + 30, npc.getY() - 30, npc.getZ(), 0, 900000);
	}

	//Обработка спауна PILLAR
	public static void onSpawnPillar(L2NpcInstance npc)
	{
		npc.setIsInvul(RaidBossSpawnManager.getInstance().getRaidBossStatusId(25603) == RaidBossSpawnManager.Status.ALIVE);
	}

	//Обработка смерти Darion
	public static void DarionDead()
	{
		if (pillarSpawn != null)
		{
			pillarSpawn.getLastSpawn().setIsInvul(false);
		}
	}

	//Обработка спауна Darion
	public static void DarionSpawn()
	{
		if (pillarSpawn != null)
		{
			pillarSpawn.getLastSpawn().setIsInvul(true);
		}
	}

	//Обработка смерти нпс в тулли, при убийстве которых либо добавляется время, либо отнимается (на запуск пула включение зоны).
	public static void DeadTimetwisterGolem(L2NpcInstance npc)
	{
		if ((npc.getNpcId() == 22392) && (_countdown != null))
		{
			if (Rnd.get(1000) >= 700)
			{
				npc.broadcastPacket(new NpcSay(npc, Say2C.NPC_ALL, 1000004));
				if (countdownTime > 180000)
				{
					countdownTime = Math.max(countdownTime - 180000, 60000);
					if ((postMortemSpawn != null) && (postMortemSpawn.size() > 0) && (postMortemSpawn.get(0) != null) && (postMortemSpawn.get(0).getNpcId() == 32371))
					{
						postMortemSpawn.get(0).broadcastPacket(new NpcSay(postMortemSpawn.get(0), Say2C.NPC_SHOUT, 1800118));
					}
				}
			}
			else
			{
				npc.broadcastPacket(new NpcSay(npc, Say2C.NPC_ALL, 1800113));
				if ((countdownTime > 0) && (countdownTime <= 420000))
				{
					countdownTime += 180000;
					if ((postMortemSpawn != null) && (postMortemSpawn.size() > 0) && (postMortemSpawn.get(0) != null) && (postMortemSpawn.get(0).getNpcId() == 32371))
					{
						postMortemSpawn.get(0).broadcastPacket(new NpcSay(postMortemSpawn.get(0), Say2C.NPC_SHOUT, 1800119));
					}
				}
			}
		}
	}

	//Метод обработки спауна при загрузке сервера.
	private void doOnLoadSpawn()
	{
		// Ghost of Tully and Spooky Tombstone should be spawned, if Tully isn't alive
		if (RaidBossSpawnManager.getInstance().getRaidBossStatusId(25544) != RaidBossSpawnManager.Status.ALIVE)
		{
			for (int i = 12; i <= 13; i++)
			{
				int[] data = POST_MORTEM_SPAWNLIST[i];
				L2NpcInstance spawnedNpc = NpcUtils.spawnSingle(data[0], data[1], data[2], data[3], data[4], 0);
				postMortemSpawn.add(spawnedNpc);
			}
		}

		pillarSpawn = NpcUtils.spawnSingle(18506, 21008, 244000, 11087, 0, 0).getSpawn();
		pillarSpawn.setAmount(1);
		pillarSpawn.setRespawnDelay(1200);
		pillarSpawn.startRespawn();
	}

	//Листенер зон тулли, для тп по тулли.
	public class ZoneListener extends L2ZoneEnterLeaveListener
	{
		final Location TullyFloor2LocationPoint = new Location(-14180, 273060, -13600);
		final Location TullyFloor3LocationPoint = new Location(-13361, 272107, -11936);
		final Location TullyFloor4LocationPoint = new Location(-14238, 273002, -10496);
		final Location TullyFloor5LocationPoint = new Location(-10952, 272536, -9062);

		@Override
		public void objectEntered(L2Zone zone, L2Object object)
		{
			L2Player player = object.getPlayer();
			if(player == null)
				return;
			if(zone.isActive())
			{
				if(zone.getName().equalsIgnoreCase("[tully1]"))
					player.teleToLocation(TullyFloor2LocationPoint);
				else if(zone.getName().equalsIgnoreCase("[tully2]"))
					player.teleToLocation(TullyFloor4LocationPoint);
				else if(zone.getName().equalsIgnoreCase("[tully3]"))
					player.teleToLocation(TullyFloor3LocationPoint);
				else if(zone.getName().equalsIgnoreCase("[tully4]"))
					player.teleToLocation(TullyFloor5LocationPoint);

			}
		}
		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{
		}
	}

	@Override
	public void onReload()
	{
		zone.getListenerEngine().removeMethodInvokedListener(_zoneListener);
	}

	@Override
	public void onShutdown()
	{
	}
}
