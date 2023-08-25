package ai;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

public class NihilInvaderChest extends DefaultAI
{
	private static int[] _firstLevelItems = {4039, 4040, 4041, 4042, 4043, 4044};
	private static int[] _secondLevelItems = {9628, 9629, 9630};

	public NihilInvaderChest(L2NpcInstance actor)
	{
		super(actor);
		actor.p_block_move(true, null);
	}

	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(actor.getNpcId() == 18820)
		{
			if(Rnd.chance(40))
			{
				actor.broadcastSkill(new MagicSkillUse(actor, actor, 2025, 1, 0, 10));
				actor.dropItem(attacker.getPlayer(), _firstLevelItems[Rnd.get(0, _firstLevelItems.length - 1)], Rnd.get(10, 20));
				actor.doDie(null);
			}
			else
			{
				actor.doCast(SkillTable.getInstance().getInfo(5376, 4), attacker, true);
			}
		}
		else if(actor.getNpcId() == 18823)
		{
			if(Rnd.chance(40))
			{
				actor.broadcastSkill(new MagicSkillUse(actor, actor, 2025, 1, 0, 10));
				actor.dropItem(attacker.getPlayer(), _secondLevelItems[Rnd.get(0, _secondLevelItems.length - 1)], Rnd.get(10, 20));
				actor.doDie(null);
			}
			else
			{
				actor.doCast(SkillTable.getInstance().getInfo(5376, 4), attacker, true);
			}
		}
		for(L2NpcInstance npc : actor.getReflection().getNpcs())
			if(npc.getNpcId() == actor.getNpcId())
				npc.deleteMe();

		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void onEvtAggression(L2Character attacker, int aggro)
	{
	}
}