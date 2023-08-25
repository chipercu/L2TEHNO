package ai.DenOfEvil;

import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date: 27.02.2013
 */

public class ai_nest_warrior_general extends ai_nest_warrior_basic
{
	private L2NpcInstance myself = null;

	public ai_nest_warrior_general(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public String Privates1 = "ragna_orc_healer_re:DenOfEvil.ai_nest_healer:1:0sec";
	public String Privates2 = "ragna_orc_hero_re:DenOfEvil.ai_nest_warrior_hero:1:0sec";
	public String Privates3 = "ragna_orc_seer_re:DenOfEvil.ai_nest_wizard_summon_private:1:0sec";
	public int DeBuff = 4046;
	public int DeBuffProb = 3333;

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		//if((maker0.maximum_npc - maker0.i_ai0) >= 2)
		//{
			myself.CreatePrivates(Privates1);
			if(Rnd.get(100) < 50)
			{
				myself.CreatePrivates(Privates2);
			}
			else
			{
				myself.CreatePrivates(Privates3);
			}
		//}
		myself.i_ai5 = 0;
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		super.ATTACKED(attacker, damage, skill);
		if(Rnd.get(10000) < DeBuffProb)
		{
			if(Skill_GetConsumeMP(DeBuff) < myself.getCurrentMp() && Skill_GetConsumeHP(DeBuff) < myself.getCurrentHp() && Skill_InReuseDelay(DeBuff) == 0)
			{
				AddUseSkillDesire(attacker, SkillTable.getInstance().getInfo(DeBuff,9), 1);
			}
		}
	}
}
