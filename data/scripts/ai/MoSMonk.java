package ai;

import l2open.common.*;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;

import static l2open.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

/**
 * AI монахов в Monastery of Silence<br>
 * - агрятся на чаров с оружием в руках
 * - перед тем как броситься в атаку кричат
 * - при смерте спаунят нпс для 457 квеста с шансом, и если квест возможно взять.
 */
public class MoSMonk extends Fighter
{
	public MoSMonk(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void onIntentionAttack(L2Character target)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
			
		if(getIntention() == AI_INTENTION_ACTIVE && Rnd.chance(50))
			Functions.npcSay(actor, "Вы не сможете пронести оружие с собой без особого разрешения!");

		super.onIntentionAttack(target);
	}

	@Override
	public void checkAggression(L2Character target)
	{
		if(target.getActiveWeaponInstance() == null)
			return;
		super.checkAggression(target);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		final L2Player p = killer.getPlayer();
		if(actor != null && p != null && Rnd.chance(1))
		{
			String req = (p.getVar("NextQuest457") == null || p.getVar("NextQuest457").equalsIgnoreCase("null")) ? "0" : p.getVar("NextQuest457");
			if (Long.parseLong(req) > System.currentTimeMillis())
				return;
			try
			{
				Location pos = GeoEngine.findPointToStay(actor.getX(), actor.getY(), actor.getZ(), 100, 120, actor.getReflection().getGeoIndex());
				L2Spawn sp = new L2Spawn(NpcTable.getTemplate(32759));
				sp.setAmount(1);
				sp.setRespawnDelay(0, 0);
				sp.setLoc(pos);
				sp.stopRespawn();
				final L2NpcInstance npc = sp.doSpawn(true);
				ThreadPoolManager.getInstance().schedule(new RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						if(!npc.isDead())
						{
							QuestState st = p.getQuestState("_457_LostAndFound");
							if(st != null)
								st.exitCurrentQuest(true);
							npc.deleteMe();
						}
					}
				}, 20 * 60 * 1000);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(actor != null && p != null)
		{
			String req = (p.getVar("NextQuest464") == null || p.getVar("NextQuest464").equalsIgnoreCase("null")) ? "0" : p.getVar("NextQuest464");
			if (Long.parseLong(req) > System.currentTimeMillis() && p.getInventory().getCountOf(15537) > 0)
				return;
			try
			{
				int r = Rnd.get(1000);
				if(r < 4)
				{
					actor.dropItem(p, 15537, 1);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		super.MY_DYING(killer);
	}
}