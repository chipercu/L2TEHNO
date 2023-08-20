package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Rnd;

/**
 * AI для Timak Orc Troop Leader ID: 20767, кричащего и призывающего братьев по клану при ударе.
 *
 * @author SYS
 */
public class TimakOrcTroopLeader extends Fighter
{
	private static final int[] BROTHERS = { 20768, // Timak Orc Troop Shaman
			20769, // Timak Orc Troop Warrior
			20770 // Timak Orc Troop Archer
	};

	private boolean _firstTimeAttacked = true;

	public TimakOrcTroopLeader(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		if(!actor.isDead() && _firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			Functions.npcSay(actor, "Show yourselves!");
			for(int bro : BROTHERS)
				try
				{
					L2NpcInstance npc = NpcTable.getTemplate(bro).getNewInstance();
					npc.setSpawnedLoc(((L2MonsterInstance) actor).getMinionPosition());
					npc.setReflection(actor.getReflection());
					npc.onSpawn();
					npc.spawnMe(npc.getSpawnedLoc());
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		_firstTimeAttacked = true;
		super.MY_DYING(killer);
	}
}