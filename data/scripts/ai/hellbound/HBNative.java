package ai.hellbound;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;

import java.util.concurrent.ScheduledFuture;

/**
 * AI Native for Hellbound<br>
 * @author Drizzy
 * @date 26.11.10
 */
 
public class HBNative extends Fighter
{
	private static final int[] Native = { 22322, 22323 };
	private L2NpcInstance _native = null;
	private ScheduledFuture<?> FollowTask;
	
	public HBNative(L2Character actor)
	{
		super(actor);
	}
	
	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		
		int hLevel = HellboundManager.getInstance().getLevel();	
		if(hLevel < 5)
		{
			try
			{
				Location loc = Location.findPointToStay(actor, 150, 350);
				if(loc.equals(0,0,0))
					return;
				L2Spawn sp = new L2Spawn(NpcTable.getTemplate(Native[Rnd.get(Native.length)]));
				sp.setLoc(loc);
				L2NpcInstance npc = sp.doSpawn(true);
				if(FollowTask != null)
					FollowTask.cancel(false);
				FollowTask = null;
				FollowTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Follow(npc, actor), 10, 5000);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, actor, 500);
				_native = npc;
				sp.stopRespawn();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void MY_DYING(L2Character killer)
	{
		int id = getActor().getNpcId();
		int hLevel = HellboundManager.getInstance().getLevel();
		if(id == 22320)
			if (hLevel <= 1)
				HellboundManager.getInstance().addPoints(1);
		if(id == 22321)
			if (hLevel <= 1)
				HellboundManager.getInstance().addPoints(1);
		if(_native != null)
			_native.deleteMe();
		super.MY_DYING(killer);
	}

	private class Follow extends l2open.common.RunnableImpl
	{
		private L2NpcInstance _npc;
		private L2NpcInstance actor;

		private Follow(L2NpcInstance npc, L2NpcInstance pl)
		{
			_npc = npc;
			actor = pl;
		}

		public void runImpl()
		{
			_npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, actor, 250);
		}
	}
}