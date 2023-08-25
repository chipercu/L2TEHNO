package l2open.util;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Атомарный, неперекрываемый флаг состояния.
 *
 * @author G1ta0
 */
public class AtomicState
{
	protected static Logger _log = Logger.getLogger(AtomicState.class.getName());
	private static final AtomicIntegerFieldUpdater<AtomicState> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(AtomicState.class, "value");

	private volatile String string_value;
	private volatile int value;
	private String value_name;
	private String owner_name=null;
	private int owner_id;
	private boolean isPlayer;

	public AtomicState(int initialValue, int obj_id, String n, boolean player)
	{
		value = initialValue;
		value_name = n;
		owner_id = obj_id;
		isPlayer = player;
	}

	public final boolean get()
	{
		return value != 0;
	}

	private boolean getBool(int val)
	{
		if(val < 0)
		{
			if(ConfigValue.DebugAtomicState)
			{
				try
				{
					throw new IllegalStateException();
				}
				catch(IllegalStateException e)
				{
					_log.log(Level.WARNING,"AtomicState["+System.currentTimeMillis()+"]["+System.nanoTime()+"]_new: value="+value, e);
					_log.log(Level.WARNING,"AtomicState["+System.currentTimeMillis()+"]["+System.nanoTime()+"]_prev: "+string_value);

					//Log.addMy("AtomicState["+System.currentTimeMillis()+"]["+System.nanoTime()+"]_new: value="+val, "debug_atomic_state" , "atomic");
					//Util.test(value_name, null, String.valueOf(val), "debug_atomic_state");
				}
				//string_value = Util.test_return("set["+System.currentTimeMillis()+"]["+System.nanoTime()+"] ");
			}
			if(ConfigValue.TestAtomicState)
				stateUpdater.set(this, 0);
			if(isPlayer)
			{
				if(owner_name == null)
				{
					L2Player player = L2ObjectsStorage.getPlayer(owner_id);
					if(player != null)
						owner_name = player.getName();
				}
				if(owner_name == null)
					Util.test(value_name, null, (value)+":"+owner_id, "debug_player_atomic_state");
				else
					Util.test(value_name, null, (value)+":"+owner_id, "debug_player_atomic_state/"+owner_name);
			}
			else
			{
				Util.test(value_name, null, (value)+":"+owner_id, "debug_npc_atomic_state");
			}
		}
		//else if(val == 0 && ConfigValue.DebugAtomicState)
		//	string_value = Util.test_return("value["+System.currentTimeMillis()+"]["+System.nanoTime()+"] ");
		return val > 0;
	}

	public final boolean setAndGet(boolean newValue)
	{
		if(newValue)
			return getBool(stateUpdater.incrementAndGet(this));
		else
			return getBool(stateUpdater.decrementAndGet(this));
	}

	public final boolean getAndSet(boolean newValue)
	{
		if(newValue)
			return getBool(stateUpdater.getAndIncrement(this));
		else
			return getBool(stateUpdater.getAndDecrement(this));
	}
}
