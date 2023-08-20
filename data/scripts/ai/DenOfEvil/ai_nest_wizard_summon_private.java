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

public class ai_nest_wizard_summon_private extends ai_nest_wizard_basic
{
	private L2NpcInstance myself = null;

	public ai_nest_wizard_summon_private(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public String Privates1 = "";
	public String Privates2 = "";
	
	/**
	 * 22697

	**/
	public int DeBuff = 4046;
	public int DeBuffProb = 2000;
	public int debug_mode = 0;

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		if(myself.getNpcId() == 22697)
		{
			Privates1="ragna_orc_shaman_re:DenOfEvil.ai_nest_wizard:1:0sec";
			Privates2="ragna_orc_warrior_re:DenOfEvil.ai_nest_warrior_buff:1:0sec";
		}
		if(myself.IsMyBossAlive() > 0)
		{
		}
		else
		{
			//maker0 = myself.GetMyMaker();
			//if((maker0.maximum_npc - maker0.i_ai0) >= 1)
			//{
				if(Rnd.get(100) < 50)
				{
					myself.CreatePrivates(Privates1);
				}
				else
				{
					myself.CreatePrivates(Privates2);
				}
			//}
		}
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		super.ATTACKED(attacker, damage, skill);
		if(Rnd.get(10000) < DeBuffProb)
		{
			if(Skill_GetConsumeMP(DeBuff) < myself.getCurrentMp() && Skill_GetConsumeHP(DeBuff) < myself.getCurrentHp() - damage && Skill_InReuseDelay(DeBuff) == 0)
			{
				AddUseSkillDesire(attacker, SkillTable.getInstance().getInfo(DeBuff,9),1);
			}
		}
	}
}
