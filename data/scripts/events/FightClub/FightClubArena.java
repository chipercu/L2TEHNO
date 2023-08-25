package events.FightClub;

import java.util.Iterator;
import java.util.concurrent.ScheduledFuture;
import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;

public class FightClubArena
{
	private ScheduledFuture<?> _faitTask = null;
	private ScheduledFuture<?> _arenaTeleportTask = null;
	private boolean faitStart = false;
	private boolean _isEnded = false;
	private boolean _alr = false; // затык на выдачу награды...Говорят есть дюп, проверил вроди бы нету но на всякий пожарный сделал затычку, не красиво зато 100% работает...
	private L2Player player1 = null;
	private L2Player player2 = null;
	private int item;
	private long count;
	private Reflection arenaReflection = null;

	public FightClubArena(L2Player p1, L2Player p2, int it, long coun, Reflection ref)
	{
		player1 = p1;
		player2 = p2;
		item = it;
		count = coun;
		arenaReflection = ref;
		teleportPlayer();
	}

	public void onPlayerTeleport(L2Player player)
	{
		if(player == null)
			return;
		if((player.getObjectId() == player1.getObjectId() || player.getObjectId() == player2.getObjectId()) && !_isEnded)
		{
			if(_arenaTeleportTask != null)
			{
				_arenaTeleportTask.cancel(true);
				_arenaTeleportTask = null;
			}
			Loose(player);
			stopEndTask();
		}
	}

	public void OnPlayerExit(L2Player player)
	{
		if(player == null)
			return;
		if((player.getObjectId() == player1.getObjectId() || player.getObjectId() == player2.getObjectId()) && !_isEnded)
		{
			if(_arenaTeleportTask != null)
			{
				_arenaTeleportTask.cancel(true);
				_arenaTeleportTask = null;
			}
			Loose(player);
			stopEndTask();
		}
	}

	public void OnDie(L2Character self)
	{
		if(faitStart && !_isEnded && (self.getObjectId() == player1.getObjectId() || self.getObjectId() == player2.getObjectId()))
		{
			stopEndTask();
			Loose((L2Player)self);
			//System.out.println("OnDie Ok");
		}
	}

	protected void stopEndTask()
	{
		_faitTask.cancel(true);
		_faitTask = ThreadPoolManager.getInstance().schedule(new faitTask(), 3000);
	}

	private void teleportPlayer()
	{
		Object[] arrayOfObject = { player1, player2, arenaReflection };
		_arenaTeleportTask = ThreadPoolManager.getInstance().scheduleAtFixedDelay(new arenaTeleport(), ConfigValue.ArenaTeleportDelay * 1000, 1000);
		_faitTask = ThreadPoolManager.getInstance().schedule(new faitTask(), (ConfigValue.ArenaTeleportDelay + ConfigValue.TimeToPreparation + ConfigValue.TimeToDraw) * 1000L);
		FightClubManager.sayToPlayers("scripts.events.fightclub.TeleportThrough", Integer.valueOf(ConfigValue.ArenaTeleportDelay), false, player1, player2);
		FightClubManager.executeTask("events.FightClub.FightClubManager", "teleportPlayersToColliseum", arrayOfObject, ConfigValue.ArenaTeleportDelay * 1000);
	}

	private void teamReset()
	{
		player1.setTeam(0, false);
		player2.setTeam(0, false);
		player1.setIsInEvent((byte) 0);
		player2.setIsInEvent((byte) 0);
		if(ConfigValue.FightClubOlympiadItems || ConfigValue.FightClubForbiddenItems.length > 0)
		{
			player1.getInventory().unlock();
			player2.getInventory().unlock();
		}
	}

	private void winP(L2Player player)
	{
		if(!_alr)
		{
			_alr = true;
			String str = ItemTemplates.getInstance().createItem(item).getName();
			FightClubManager.sayToPlayer(player, "scripts.events.fightclub.YouWin", false, count * 2, str);
			Functions.addItem(player, item, count * 2);
		}
	}

	private void Loose(L2Player player)
	{
		_isEnded = true;
		if (player.getObjectId() == player1.getObjectId())
			winP(player2);
		else if (player.getObjectId() == player2.getObjectId())
			winP(player1);
		teamReset();
		FightClubManager.sayToPlayer(player, "scripts.events.fightclub.YouLoose", false);
	}

	private void ValidateWiner()
	{
		if(player1.getDamageMy() > player2.getDamageMy())
			Loose(player1);
		else if(player2.getDamageMy() > player1.getDamageMy())
			Loose(player2);
		else
		{
			FightClubManager.sayToPlayers("scripts.events.fightclub.Draw", true, player1, player2);
			if(!_alr)
			{
				_alr = true;
				Functions.addItem(player1, item, count);
				Functions.addItem(player2, item, count);
			}
		}
	}

	protected L2Player getPlayer1()
	{
		return player1;
	}

	protected L2Player getPlayer2()
	{
		return player2;
	}

	protected Reflection getReflection()
	{
		return arenaReflection;
	}

	private void deleteArena(long time)
	{
		FightClubArena[] arrayOfFightClubArena = { this };
		FightClubManager.executeTask("events.FightClub.FightClubManager", "deleteArena", arrayOfFightClubArena, time);
	}

	private class faitTask extends RunnableImpl
	{
		private final Object[] players = { getPlayer1(), getPlayer2() };

		private faitTask()
		{
		}

		public void runImpl() throws Exception
		{
			if(!_isEnded)
			{
				_isEnded = true;
				ValidateWiner();
				stopEndTask();
				return;
			}
			teamReset();
			FightClubManager.sayToPlayers("scripts.events.fightclub.TeleportBack", Integer.valueOf(ConfigValue.TimeToBack), false, getPlayer1(), getPlayer2());
			Functions.executeTask("events.FightClub.FightClubManager", "teleportPlayersBack", players, ConfigValue.TimeToBack * 1000);
			deleteArena((ConfigValue.TimeToBack + 10) * 1000);
		}
	}

	private class arenaTeleport extends RunnableImpl
	{
		int secondToStart = ConfigValue.TimeToPreparation;

		private arenaTeleport()
		{
		}

		public void runImpl() throws Exception
		{
			switch (secondToStart)
			{
				case 60:
					FightClubManager.sayToPlayers("scripts.events.fightclub.TimeToStart", Integer.valueOf(secondToStart), false, getPlayer1(), getPlayer2());
					break;
				case 30:
					FightClubManager.sayToPlayers("scripts.events.fightclub.TimeToStart", Integer.valueOf(secondToStart), false, getPlayer1(), getPlayer2());
					break;
				case 20:
					FightClubManager.sayToPlayers("scripts.events.fightclub.TimeToStart", Integer.valueOf(secondToStart), false, getPlayer1(), getPlayer2());
					break;
				case 10:
					FightClubManager.sayToPlayers("scripts.events.fightclub.TimeToStart", Integer.valueOf(secondToStart), false, getPlayer1(), getPlayer2());
					break;
				case 5:
					FightClubManager.sayToPlayers("scripts.events.fightclub.TimeToStart", Integer.valueOf(secondToStart), false, getPlayer1(), getPlayer2());
					break;
				case 3:
					FightClubManager.sayToPlayers("scripts.events.fightclub.TimeToStart", Integer.valueOf(secondToStart), false, getPlayer1(), getPlayer2());
					break;
				case 2:
					FightClubManager.sayToPlayers("scripts.events.fightclub.TimeToStart", Integer.valueOf(secondToStart), false, getPlayer1(), getPlayer2());
					break;
				case 1:
					FightClubManager.sayToPlayers("scripts.events.fightclub.TimeToStart", Integer.valueOf(secondToStart), false, getPlayer1(), getPlayer2());
					break;
				case 0:
					for(L2DoorInstance door : arenaReflection.getDoors())
						door.openMe();
					if(player1 != null && player2 != null)
					{
						FightClubManager.startBattle(player1, player2);
						// Говорят некст таргет не работает хз...позже нужно отладить, че за затупь...а так то пускай вот здесь еще разок шлется для наверочки)
						player1.broadcastRelationChanged();
						player2.broadcastRelationChanged();
					}
					faitStart = true;
					_arenaTeleportTask.cancel(false);
					_arenaTeleportTask = null;
					break;
			}
			secondToStart -= 1;
		}
	}
}