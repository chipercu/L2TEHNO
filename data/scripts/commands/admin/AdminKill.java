package commands.admin;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;

import java.util.concurrent.ScheduledFuture;

public class AdminKill implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_kill,
		admin_kill_start,
		admin_kill_stop,
		admin_kill_all_mobs
	}

	private static L2NpcInstance killing_now;
	protected ScheduledFuture<?> _runningTask;

	public static void OnDie(L2Character self, L2Character killer)
	{
		if(self == killing_now)
			killing_now = null;
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;

		switch(command)
		{
			case admin_kill:
				if(wordList.length == 1)
					handleKill(activeChar);
				else
					handleKill(activeChar, wordList[1]);
				break;
			case admin_kill_start:
				int time = Integer.parseInt(wordList[1]);
				if(_runningTask != null)
				{
					_runningTask.cancel(true);
					_runningTask = null;
				}
				_runningTask = ThreadPoolManager.getInstance().schedule(new DespawnTask(time, activeChar), time);
				break;
			case admin_kill_stop:
				if(_runningTask != null)
				{
					_runningTask.cancel(true);
					_runningTask = null;
				}
				break;
			case admin_kill_all_mobs:
				int iKillSuccess = 0,
				iKillFail = 0;
				for(L2NpcInstance npc : L2ObjectsStorage.getAllNpcs())
				{
					if(!npc.isMonster() || npc.isDead() || npc.isRaid())
						continue;
					killing_now = npc;
					npc.reduceCurrentHp(npc.getMaxHp() + npc.getMaxCp() + 1, activeChar, null, true, true, false, false, true, npc.getMaxHp() + npc.getMaxCp() + 1, true, false, false, false);
					if(npc.isDead())
						iKillSuccess++;
					else
						iKillFail++;
					if(killing_now != null)
						System.out.println("Fail to Kill: " + killing_now);
				}
				killing_now = null;
				activeChar.sendMessage("Killed " + iKillSuccess + " mobs, failed to kill: " + iKillFail);
				break;
		}

		return true;
	}

	private class DespawnTask extends l2open.common.RunnableImpl 
	{
		int _time = 0;
		L2Player activeChar = null;
		private DespawnTask(int time, L2Player _activeChar)
		{
			_time = time;
			activeChar = _activeChar;
		}

		public void runImpl() 
		{
			int iKillSuccess = 0,
			iKillFail = 0;
			for(L2NpcInstance npc : L2ObjectsStorage.getAllNpcs())
			{
				if(!npc.isMonster() || npc.isDead() || npc.isRaid() || npc.getNpcId() == 29002)
					continue;
				killing_now = npc;
				npc.reduceCurrentHp(npc.getMaxHp() + npc.getMaxCp() + 1, activeChar, null, true, true, false, false, true, npc.getMaxHp() + npc.getMaxCp() + 1, true, false, false, false);
				if(npc.isDead())
					iKillSuccess++;
				else
					iKillFail++;
				if(killing_now != null)
					System.out.println("Fail to Kill: " + killing_now);
			}
			killing_now = null;
			activeChar.sendMessage("Killed " + iKillSuccess + " mobs, failed to kill: " + iKillFail);
			if(_runningTask != null)
			{
				_runningTask.cancel(true);
				_runningTask = null;
			}
			_runningTask = ThreadPoolManager.getInstance().schedule(new DespawnTask(_time, activeChar), _time);
		}
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void handleKill(L2Player activeChar)
	{
		handleKill(activeChar, null);
	}

	private void handleKill(L2Player activeChar, String player)
	{
		L2Object obj = activeChar.getTarget();
		if(player != null)
		{
			L2Player plyr = L2World.getPlayer(player);
			if(plyr != null)
				obj = plyr;
			else
			{
				int radius = Math.max(Integer.parseInt(player), 100);
				for(L2Character character : activeChar.getAroundCharacters(radius, 200))
					character.reduceCurrentHp(character.getMaxHp() + character.getMaxCp() + 1, activeChar, null, true, true, false, false, true, character.getMaxHp() + character.getMaxCp() + 1, true, false, false, false);
				activeChar.sendMessage("Killed within " + radius + " unit radius.");
				return;
			}
		}

		if(obj != null && obj.isCharacter())
		{
			L2Character target = (L2Character) obj;
			target.reduceCurrentHp(target.getMaxHp() + target.getMaxCp() + 1, activeChar, null, true, true, false, false, true, target.getMaxHp() + target.getMaxCp() + 1, true, false, false, false);
		}
		else
			activeChar.sendPacket(Msg.INVALID_TARGET);
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