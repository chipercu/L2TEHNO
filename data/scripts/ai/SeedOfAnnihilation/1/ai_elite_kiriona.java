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

public class ai_elite_kiriona extends AnnihilationFighter
{
	public ai_elite_kiriona(L2NpcInstance self)
	{
		super(self);
	}

	public int Skill01_ID = 419168257;
	public int Skill01_Probability = 10;
	public int Skill01_Target_Type = 0;
	public int Skill02_ID = 419233793;
	public int Skill02_Probability = 10;
	public int Skill02_Target_Type = 1;
	public int Skill03_ID = 419299329;
	public int Skill03_Probability = 10;
	public int Skill03_Target_Type = 0;
	public int FieldCycle_ID = 6;
	public int FieldCycle_point = 10;

	@Override
	public void onEvtSpawn()
	{
		L2NpcInstance myself = getActor();
		int i0;
		int i1;
		for(i0 = 0; i0 < 3;i0++)
		{
			i1 = Rnd.get(3);
			switch(i1)
			{
				case 0:
					myself.CreateOnePrivateEx(1022760,"SeedOfAnnihilation.karnibi",0,1,myself.getX()+Rnd.get(60, 150),myself.getY()+Rnd.get(60, 150),myself.getZ(),0,0,0,0);
					break;
				case 1:
					myself.CreateOnePrivateEx(1022761,"SeedOfAnnihilation.kiriona",0,1,myself.getX()+Rnd.get(60, 150),myself.getY()+Rnd.get(60, 150),myself.getZ(),0,0,0,0);
					break;
				case 2:
					myself.CreateOnePrivateEx(1022762,"SeedOfAnnihilation.caiona",0,1,myself.getX()+Rnd.get(60, 150),myself.getY()+Rnd.get(60, 150),myself.getZ(),0,0,0,0);
					break;
			}
		}
		super.onEvtSpawn();
	}

	/*@Override
	public void USE_SKILL_FINISHED(L2Character target, int skill_name_id, int success)
	{
		if(target.is_pc() == 1)
		{
			if(myself.IsInCategory(112,target.getActiveClassId()) == 1 || myself.IsInCategory(3,target.getActiveClassId()) == 1)
			{
				if(Rnd.get(100) < 5)
				{
					myself.AddUseSkillDesire(target,418709505,0,1,max_desire);
				}
			}
			else if(Rnd.get(100) < 10)
			{
				myself.AddUseSkillDesire(target,418709505,0,1,max_desire);
			}
		}
		super.USE_SKILL_FINISHED(target, skill_name_id, success);
	}*/
}
