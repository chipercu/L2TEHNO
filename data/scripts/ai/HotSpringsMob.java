package ai;

import l2open.gameserver.ai.Mystic;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.GArray;
import l2open.util.Rnd;

/**
 * AI for:
 * Hot Springs Atrox (id 21321)
 * Hot Springs Atroxspawn (id 21317)
 * Hot Springs Bandersnatch (id 21322)
 * Hot Springs Bandersnatchling (id 21314)
 * Hot Springs Flava (id 21316)
 * Hot Springs Nepenthes (id 21319)
 *
 * @author Diamond
 */
public class HotSpringsMob extends Mystic
{
	private static final int DeBuffs[] = { 4554, 4552 };

	public HotSpringsMob(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill sk)
	{
		L2NpcInstance actor = getActor();
		if(actor != null && attacker != null && Rnd.chance(5))
		{
			int DeBuff = DeBuffs[Rnd.get(DeBuffs.length)];
			GArray<L2Effect> effect = attacker.getEffectList().getEffectsBySkillId(DeBuff);
			if(effect != null)
			{
				int level = effect.get(0).getSkill().getLevel();
				if(level < 10)
				{
					effect.get(0).exit(true, false);
					L2Skill skill = SkillTable.getInstance().getInfo(DeBuff, level + 1);
					skill.getEffects(actor, attacker, false, false);
				}
			}
			else
			{
				L2Skill skill = SkillTable.getInstance().getInfo(DeBuff, 1);
				if(skill != null)
					skill.getEffects(actor, attacker, false, false);
				else
					System.out.println("Skill " + DeBuff + " is null, fix it.");
			}
		}
		super.ATTACKED(attacker, damage, sk);
	}
}