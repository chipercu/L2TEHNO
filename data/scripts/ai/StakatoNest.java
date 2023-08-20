package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2MinionInstance;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.MinionList;
import l2open.util.Rnd;
import l2open.util.Util;


public class StakatoNest extends Fighter
{
	// Cannibalistic Stakato Leader
	private static final int _stakato_leader = 22625;

	public StakatoNest(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance npc = getActor();
		L2MonsterInstance _mob = (L2MonsterInstance) npc;
		if ((_mob.getNpcId() == _stakato_leader) && (Rnd.get(1000) < 100) && (_mob.getCurrentHp() - damage < (_mob.getMaxHp() * 0.3)))
		{
			MinionList ml = ((L2MonsterInstance) npc).getMinionList();
			if (ml != null)
			{
				for(L2MinionInstance m : ml.getSpawnedMinions())
				{
					double _hp = m.getCurrentHp() - damage;
					
					if (_hp > (m.getMaxHp() * 0.3))
					{
						_mob.abortAttack(true, false);
						_mob.abortCast(true);
						//_mob.setHeading(Util.calculateHeadingFrom(_mob, m));
						_mob.doCast(SkillTable.getInstance().getInfo(4484, 1), _mob, true);
						_mob.setCurrentHp(_mob.getCurrentHp() + _hp, false);
						m.doDie(null);
						m.deleteMe();
					}
				}
			}
		}
		super.ATTACKED(attacker, damage, skill);
	}	
}