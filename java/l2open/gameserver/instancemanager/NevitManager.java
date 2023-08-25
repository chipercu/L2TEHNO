package l2open.gameserver.instancemanager;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.util.reference.*;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

public class NevitManager
{
	private HardReference<? extends L2Player> owner_ref = HardReferences.emptyRef();

	public ScheduledFuture<?> _task;
	private static NevitManager _instance;
	private List<HardReference<? extends L2Player>> _players;
	private List<HardReference<? extends L2Player>> temp;

	public NevitManager()
	{
		_players = new ArrayList<HardReference<? extends L2Player>>();
		temp = new ArrayList<HardReference<? extends L2Player>>();
	}

	public static NevitManager getInstance()
	{
		if (_instance == null)
			_instance = new NevitManager();
		return _instance;
	}

	public void addPlayer(L2Player player)
	{
		synchronized (_players)
		{
			_players.add(player.getRef());

			if (_task == null)
				_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new nevitTask(), 0, 60000);
		}
	}

	public boolean containPlayer(L2Player player)
	{
		return _players.contains(player.getRef());
	}

	private class nevitTask extends l2open.common.RunnableImpl
	{
		private nevitTask()
		{}

		public void runImpl()
		{
			synchronized (_players)
			{
				if(_players.size() == 0)
				{
					_task.cancel(true);
					_task = null;
				}

				try
				{
					for(HardReference<? extends L2Player> ref : _players)
					{
						try
						{
							L2Player player = ref.get();
							if(player == null || player.isDeleting() || !player.isConnected() || player.isInOfflineMode() || !player.getNevitBlessing().onNevitBonusTimeTick())
								temp.add(ref);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					for(HardReference<? extends L2Player> ref : temp)
						_players.remove(ref);
					temp.clear();
				}
			}
		}
	}
}