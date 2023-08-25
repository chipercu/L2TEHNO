package l2open.gameserver.model.entity;

import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2PathfinderInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;

import java.util.*;
import java.util.Timer;
import java.util.TimerTask;

public class KamalokaNightmare extends Reflection
{
	public static final int TIME_LIMIT = 1200000;
	public static final int COLLAPSE_TIME = 1800000;

	public static final int KAMALOKA_ESSENCE = 13002;

	private int _player;
	private Timer _pathfinderTimer;
	private TimerTask _pathfinderTimerTask;
	private ArrayList<Integer> v0id = new ArrayList<Integer>();
	private ArrayList<Integer> doppler = new ArrayList<Integer>();
	private ArrayList<Integer> base = new ArrayList<Integer>();

	public KamalokaNightmare(L2Player player)
	{
		super("Kamaloka Nightmare");
		_player = player.getObjectId();
		startPathfinderTimer(TIME_LIMIT);
	}

	@Override
	public void collapse()
	{
		ReflectionTable.getInstance().removeSoloKamaloka(_player);
		ReflectionTable.getInstance().playerRemoveReflection(_player);
		stopPathfinderTimer();
		super.collapse();
	}

	@Override
	public void removeObject(L2Object o)
	{
		synchronized (_objects_lock)
		{
			_objects.remove(o);
		}
	}

	@Override
	public void addObject(L2Object o)
	{
		synchronized (_objects_lock)
		{
			_objects.add(o);
		}
		if(o.isPlayer())
			ReflectionTable.getInstance().playerAddReflection(Integer.valueOf(o.getObjectId()), this);
	}

	public void registerKilled(L2NpcTemplate template)
	{
		if(getVoid(template.getNpcId()))
		{
			Integer current = v0id.size();
			current++;
			v0id.add(current);
		}
		else if(getDoppler(template.getNpcId()))
		{
			Integer current = doppler.size();
			current++;
			doppler.add(current);
		}
		else if(getBase(template.getNpcId()))
		{
			Integer current = base.size();
			current++;
			base.add(current);
		}
	}

	public ArrayList<Integer> getCounterV0id()
	{
		return v0id;
	}

	public ArrayList<Integer> getCounterDoppler()
	{
		return doppler;
	}

	public ArrayList<Integer> getCounterBase()
	{
		return base;
	}

	private boolean getVoid(int npcid)
	{
		switch(npcid)
		{
			case 22454:
			case 22457:
			case 22460:
			case 22463:
			case 22466:
			case 22469:
			case 22472:
			case 22475:
			case 22478:
			case 22481:
			case 22484:
				return true;
		}
		return false;
	}

	private boolean getDoppler(int npcid)
	{
		switch(npcid)
		{
			case 22453:
			case 22456:
			case 22459:
			case 22462:
			case 22465:
			case 22468:
			case 22471:
			case 22474:
			case 22477:
			case 22480:
			case 22483:
				return true;
		}
		return false;
	}

	private boolean getBase(int npcid)
	{
		switch(npcid)
		{
			case 22452:
			case 22455:
			case 22458:
			case 22461:
			case 22464:
			case 22467:
			case 22470:
			case 22473:
			case 22476:
			case 22479:
			case 22482:
				return true;
		}
		return false;
	}

	public int getLevel(int r)
	{
		switch(r)
		{
			case 46:
				return 25;
			case 47:
				return 30;
			case 48:
				return 35;
			case 49:
				return 40;
			case 50:
				return 45;
			case 51:
				return 50;
			case 52:
				return 55;
			case 53:
				return 60;
			case 54:
				return 65;
			case 55:
				return 70;
			case 56:
				return 75;
		}
		return 0;
	}

	public void startPathfinderTimer(long time)
	{
		if(_pathfinderTimerTask != null)
		{
			_pathfinderTimerTask.cancel();
			_pathfinderTimerTask = null;
		}

		if(_pathfinderTimer != null)
		{
			_pathfinderTimer.cancel();
			_pathfinderTimer = null;
		}

		_pathfinderTimer = new Timer();
		_pathfinderTimerTask = new TimerTask(){
			@Override
			public void run()
			{
				try
				{
					List<L2MonsterInstance> delete_list = new ArrayList<L2MonsterInstance>();
					for(L2Spawn s : KamalokaNightmare.this.getSpawns().toArray(new L2Spawn[0]))
						if(s != null)
							s.despawnAll();

					KamalokaNightmare.this.getSpawns().clear();

					_objects_lock.lock();
					try
					{
						for(L2Object o : _objects)
							if(o != null && o instanceof L2MonsterInstance)
								delete_list.add((L2MonsterInstance) o);
						for(L2MonsterInstance o : delete_list)
							o.deleteMe();
					}
					finally
					{
						_objects_lock.unlock();
					}

					L2Player p = L2ObjectsStorage.getPlayer(_player);
					if(p != null)
					{
						p.getPlayer().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber((COLLAPSE_TIME - TIME_LIMIT) / 60000));

						L2PathfinderInstance npc = new L2PathfinderInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(32485));
						npc.setSpawnedLoc(KamalokaNightmare.this.getTeleportLoc());
						npc.setReflection(KamalokaNightmare.this.getId());
						npc.onSpawn();
						npc.spawnMe(npc.getSpawnedLoc());
					}
					else
						collapse();
					delete_list = null;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};

		_pathfinderTimer.schedule(_pathfinderTimerTask, time);
	}

	public void stopPathfinderTimer()
	{
		if(_pathfinderTimerTask != null)
			_pathfinderTimerTask.cancel();
		_pathfinderTimerTask = null;

		if(_pathfinderTimer != null)
			_pathfinderTimer.cancel();
		_pathfinderTimer = null;
	}

	public int getPlayerId()
	{
		return _player;
	}

	@Override
	public boolean canChampions()
	{
		return false;
	}
}