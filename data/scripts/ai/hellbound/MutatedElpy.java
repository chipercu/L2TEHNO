package ai.hellbound;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.instancemanager.NaiaCoreManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.GArray;

import java.util.concurrent.ScheduledFuture;

public class MutatedElpy extends Fighter
{
	private ScheduledFuture<?> _checkSpawn = null;

	public MutatedElpy(L2NpcInstance actor)
	{
		super(actor);
		actor.p_block_move(true, null);
		actor.setIsInvul(true);
	}

	@Override
	protected void onEvtSpawn()
	{
		if(_checkSpawn != null)
		{
			_checkSpawn.cancel(false);
			_checkSpawn = null;
		}
	  	_checkSpawn = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CheckSpawn(getActor()) , 20000, 300000);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		NaiaCoreManager.launchNaiaCore();
		super.MY_DYING(killer);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		
		if(attacker != null && attacker.getPlayer() != null && attacker.getPlayer().getParty() != null && attacker.getPlayer().getParty().getCommandChannel() != null && attacker.getPlayer().getParty().getCommandChannel().getChannelLeader() == attacker)
		{
			int size = 0;
			for(L2Player player : L2World.getAroundPlayers(actor, 3000, 3000))
				if(bosses.BelethManager.checkPlayer(player))
					size++;
			if(size >= ConfigValue.MutatedElpyCount)
			{
				actor.setIsInvul(false);
				actor.doDie(attacker);
			}
		}
	}

	private class CheckSpawn extends l2open.common.RunnableImpl
	{
		private L2NpcInstance actor;

		private CheckSpawn(L2NpcInstance _actor)
		{
			actor = _actor;
		}

		@Override
		public void runImpl()
		{
			if(ServerVariables.getLong("BelethKillTime", 0) > System.currentTimeMillis())
			{
				if(!actor.isDecayed())
				{
					actor.setDecayed(true);
			 		actor.decayMe();
				}
			}
			else
			{
				if(actor.isDecayed())
				{
					actor.spawnMe();
					actor.setDecayed(false);
				}
			}
		}
	}
}