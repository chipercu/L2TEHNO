package ai;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Mystic;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Rnd;

/**
 * AI моба-мага для Isle of Prayer.<br>
 * - Если атакован членом группы, состоящей более чем из 2х чаров, то спаунятся штрафные мобы Witch Warder ID: 18364, 18365, 18366 (случайным образом 2 штуки).
 * @author SYS
 */
public class IsleOfPrayerMystic extends Mystic
{
	private boolean _penaltyMobsNotSpawned = true;
	private static final int PENALTY_MOBS[] = { 18364, 18365, 18366 };

	public IsleOfPrayerMystic(L2Character actor)
	{
		super(actor);
		setGlobalAggro(0);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		if(_penaltyMobsNotSpawned && attacker.isPlayable() && attacker.getPlayer() != null)
		{
			L2Party party = attacker.getPlayer().getParty();
			if(party != null && party.getMemberCount() > 2)
			{
				_penaltyMobsNotSpawned = false;
				for(int i = 0; i < 2; i++)
					try
					{
						L2MonsterInstance npc = new L2MonsterInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(PENALTY_MOBS[Rnd.get(PENALTY_MOBS.length)]));
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
		}

		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		_penaltyMobsNotSpawned = true;
		super.MY_DYING(killer);
	}
}