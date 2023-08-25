package ai.CryptsOfDisgrace;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.tables.SkillTable;

/**
 * @author: Drizzy
 * @date: 21.08.2012
 */
public class ai_legend_orc_buff extends DefaultAI
{
	private int Skill01_ID = 6235;

	public ai_legend_orc_buff(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	public void SEE_CREATURE(L2Character creature)
	{
		if(IsNullCreature(creature) == 0 && creature.isPlayer())
		{
			AddUseSkillDesire(getActor(), SkillTable.getInstance().getInfo(Skill01_ID,1),100);
		}
		super.SEE_CREATURE(creature);
	}
}
