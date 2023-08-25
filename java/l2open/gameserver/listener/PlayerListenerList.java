package l2open.gameserver.listener;

import l2open.gameserver.listener.actor.OnAutoSoulShotListener;
import l2open.gameserver.listener.actor.OnRegenTaskListener;
import l2open.gameserver.listener.actor.player.*;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Summon;

/**
 * @author G1ta0
 */
public class PlayerListenerList extends CharListenerList
{
	public PlayerListenerList(L2Player actor)
	{
		super(actor);
	}

	@Override
	public L2Player getActor()
	{
		return (L2Player) actor;
	}

	public void onEnter()
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnPlayerEnterListener.class.isInstance(listener))
					((OnPlayerEnterListener) listener).onPlayerEnter(getActor());

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnPlayerEnterListener.class.isInstance(listener))
					((OnPlayerEnterListener) listener).onPlayerEnter(getActor());
	}

	public void onExit()
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnPlayerExitListener.class.isInstance(listener))
					((OnPlayerExitListener) listener).onPlayerExit(getActor());

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnPlayerExitListener.class.isInstance(listener))
					((OnPlayerExitListener) listener).onPlayerExit(getActor());
	}

	public void onTeleport(int x, int y, int z, int reflection)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnTeleportListener.class.isInstance(listener))
					((OnTeleportListener) listener).onTeleport(getActor(), x, y, z, reflection);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnTeleportListener.class.isInstance(listener))
					((OnTeleportListener) listener).onTeleport(getActor(), x, y, z, reflection);
	}

	public void onPartyInvite()
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnPlayerPartyInviteListener.class.isInstance(listener))
					((OnPlayerPartyInviteListener) listener).onPartyInvite(getActor());

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnPlayerPartyInviteListener.class.isInstance(listener))
					((OnPlayerPartyInviteListener) listener).onPartyInvite(getActor());
	}

	public void onPartyLeave()
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnPlayerPartyLeaveListener.class.isInstance(listener))
					((OnPlayerPartyLeaveListener) listener).onPartyLeave(getActor());

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnPlayerPartyLeaveListener.class.isInstance(listener))
					((OnPlayerPartyLeaveListener) listener).onPartyLeave(getActor());
	}

	public void onSummonServitor(L2Summon servitor)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnPlayerSummonServitorListener.class.isInstance(listener))
					((OnPlayerSummonServitorListener) listener).onSummonServitor(getActor(), servitor);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnPlayerSummonServitorListener.class.isInstance(listener))
					((OnPlayerSummonServitorListener) listener).onSummonServitor(getActor(), servitor);
	}

	public void onAddCp(double addCp)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(listener instanceof OnRegenTaskListener)
					((OnRegenTaskListener) listener).onAddCp(getActor(), addCp);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(listener instanceof OnRegenTaskListener)
					((OnRegenTaskListener) listener).onAddCp(getActor(), addCp);
	}

	public void onSay(int type, String target, String text)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnPlayerSayListener.class.isInstance(listener))
					((OnPlayerSayListener) listener).onSay(getActor(), type, target, text);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnPlayerSayListener.class.isInstance(listener))
					((OnPlayerSayListener) listener).onSay(getActor(), type, target, text);
	}

	public void onSetPrivateStoreType(short type)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(OnSetPrivateStoreType.class.isInstance(listener))
					((OnSetPrivateStoreType) listener).onSetPrivateStoreType(getActor(), type);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(OnSetPrivateStoreType.class.isInstance(listener))
					((OnSetPrivateStoreType) listener).onSetPrivateStoreType(getActor(), type);
	}

	public void onAutoSoulShot(int itemId, boolean enable)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(listener instanceof OnAutoSoulShotListener)
					((OnAutoSoulShotListener) listener).onAutoSoulShot(getActor(), itemId, enable);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(listener instanceof OnAutoSoulShotListener)
					((OnAutoSoulShotListener) listener).onAutoSoulShot(getActor(), itemId, enable);
	}

	public void onSetLevel(int level)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(listener instanceof OnSetLevelListener)
					((OnSetLevelListener) listener).onSetLevel(getActor(), level);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(listener instanceof OnSetLevelListener)
					((OnSetLevelListener) listener).onSetLevel(getActor(), level);
	}

	public void onSetClass(int class_id)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<L2Character> listener : global.getListeners())
				if(listener instanceof OnSetClassListener)
					((OnSetClassListener) listener).onSetClass(getActor(), class_id);

		if(!getListeners().isEmpty())
			for(Listener<L2Character> listener : getListeners())
				if(listener instanceof OnSetClassListener)
					((OnSetClassListener) listener).onSetClass(getActor(), class_id);
	}
}
