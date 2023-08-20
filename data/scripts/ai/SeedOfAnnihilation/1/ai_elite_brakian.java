package ai.SeedOfAnnihilation;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.util.*;

/**
 * Zapizdoval AI Diagod.
 * open-team.ru
 **/
// null

public class ai_elite_brakian extends AnnihilationFighter
{
	public ai_elite_brakian(L2NpcInstance self)
	{
		super(self);
	}

	public int Skill01_ID = 418381826;
	public int Skill01_Probability = 30;
	public int Skill01_Target_Type = 0;
	public int Skill02_ID = 418447361;
	public int Skill02_Probability = 30;
	public int Skill02_Target_Type = 1;
	public int SpecialSkill01_ID = 418185217;
	public int FieldCycle_ID = 4;
	public int FieldCycle_point = 10;

	@Override
	public void onEvtSpawn()
	{
		L2NpcInstance myself = getActor();
		int i0;
		int i1;
		i1 = Rnd.get(3);
		for(i0 = 0; i0 < 3;i0++)
		{
			switch(i1)
			{
				case 0:
					myself.CreateOnePrivateEx(1022746,"SeedOfAnnihilation.bgurent",0,1,myself.getX()+Rnd.get(60, 150),myself.getY()+Rnd.get(60, 150),myself.getZ(),0,0,0,0);
					break;
				case 1:
					myself.CreateOnePrivateEx(1022748,"SeedOfAnnihilation.groykhan",0,1,myself.getX()+Rnd.get(60, 150),myself.getY()+Rnd.get(60, 150),myself.getZ(),0,0,0,0);
					break;
				case 2:
					myself.CreateOnePrivateEx(1022749,"SeedOfAnnihilation.traikhan",0,1,myself.getX()+Rnd.get(60, 150),myself.getY()+Rnd.get(60, 150),myself.getZ(),0,0,0,0);
					break;
			}
		}
		super.onEvtSpawn();
	}

	/*@Override
	public void ATTACKED(L2Character attacker, int damage, int skill_name_id, int skill_id)
	{
		if(myself.DistFromMe(attacker) > 300)
		{
			if(Rnd.get(100) < 5)
			{
				myself.AddUseSkillDesire(attacker,SpecialSkill01_ID,0,1,1000000);
			}
		}
		super.ATTACKED(attacker, damage, skill_name_id, skill_id);
	}*/
}
