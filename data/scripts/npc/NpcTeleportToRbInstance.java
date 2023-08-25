package npc.model;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.instancemanager.RaidBossSpawnManager;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.*;

import java.util.concurrent.Future;

public class NpcTeleportToRbInstance extends L2NpcInstance
{
	private Future<?> _timer_task;
	private RaidBossSpawnManager.Status _raidStatus;

	public NpcTeleportToRbInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(final L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		// bypass -h npc_%objectId%_tp_to_rb:npc_id:item_id:item_count:x:y:z
		if(command.startsWith("tp_to_rb"))
		{
			String[] args = command.split(":");

			if(args.length == 7)
			{
				int npc_id = Integer.parseInt(args[1]);
				int item_id = Integer.parseInt(args[2]);
				int item_count = Integer.parseInt(args[3]);

				int x = Integer.parseInt(args[4]);
				int y = Integer.parseInt(args[5]);
				int z = Integer.parseInt(args[6]);

				L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npc_id);
				if(npc != null && !npc.isDead() && npc.isVisible())
				{
					showChatWindow(player, "data/html/tp_to_rb_err1.htm");
					return;
				}
				else if(Functions.getItemCount(player, item_id) < item_count)
				{
					showChatWindow(player, "data/html/tp_to_rb_err2.htm");
					return;
				}

				Functions.removeItem(player, item_id, item_count);
				player.teleToLocation(x, y, z);
			}
			else
				_log.info("err1: "+args.length);
		}
		else if(command.startsWith("tp_to_rb2"))
		{
			String[] args = command.split(":");

			if(args.length == 7)
			{
				int npc_id = Integer.parseInt(args[1]);
				int item_id = Integer.parseInt(args[2]);
				int item_count = Integer.parseInt(args[3]);

				int x = Integer.parseInt(args[4]);
				int y = Integer.parseInt(args[5]);
				int z = Integer.parseInt(args[6]);

				if(Functions.getItemCount(player, item_id) < item_count)
				{
					showChatWindow(player, "data/html/tp_to_rb_err2.htm");
					return;
				}

				Functions.removeItem(player, item_id, item_count);
				player.teleToLocation(x, y, z);
			}
			else
				_log.info("err1: "+args.length);
		}
		// bypass -h npc_%objectId%_spawn_rb:npc_id:time_to_spawn:time_to_despawn:x:y:z
		else if(command.startsWith("spawn_rb"))
		{
			String[] args = command.split(":");

			if(_timer_task == null)
			{
				if(args.length == 7)
				{
					final int npc_id = Integer.parseInt(args[1]);
					final int time_to_spawn = Integer.parseInt(args[2]);
					final int time_to_despawn = Integer.parseInt(args[3]);

					final int x = Integer.parseInt(args[4]);
					final int y = Integer.parseInt(args[5]);
					final int z = Integer.parseInt(args[6]);

					_timer_task = ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
					{
						public void runImpl()
						{
							spawnRb(player, npc_id, time_to_despawn, x, y, z);
						}
					}, time_to_spawn*1000);
				}
				else
					_log.info("err2: "+args.length);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	private void spawnRb(L2Player player, int npc_id, int time_to_despawn, int x, int y, int z)
	{
		_timer_task = null;
		setRaidStatus(RaidBossSpawnManager.Status.DEAD);
		try
		{
			L2Spawn rb_spawn = new L2Spawn(NpcTable.getTemplate(npc_id));
			rb_spawn.setAmount(1);
			rb_spawn.setLoc(new Location(x, y, z));
			rb_spawn.stopRespawn();
			L2NpcInstance npc = rb_spawn.doSpawn(true);
			ThreadPoolManager.getInstance().schedule(new L2ObjectTasks.DeleteTask(npc), time_to_despawn*1000L);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		getSpawn().startRespawn();
		endDecayTask();
	}

	@Override
	public void onDecay()
	{
		super.onDecay();
		RaidBossSpawnManager.getInstance().onBossDespawned(this);
	}

	@Override
	public void onSpawn()
	{
		RaidBossSpawnManager.getInstance().addNewSpawn(getSpawn());
		RaidBossSpawnManager.getInstance().onBossSpawned(this);
		super.onSpawn();
	}

	public void setRaidStatus(RaidBossSpawnManager.Status status)
	{
		_raidStatus = status;
	}

	public RaidBossSpawnManager.Status getRaidStatus()
	{
		return _raidStatus;
	}
}