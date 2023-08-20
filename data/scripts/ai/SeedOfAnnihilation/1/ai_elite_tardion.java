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

public class ai_elite_tardion extends AnnihilationFighter
{
	public ai_elite_tardion(L2NpcInstance self)
	{
		super(self);
	}

	public int Skill01_ID = 420478977;
	public int Skill01_Probablity = 10;
	public int Skill01_Target_Type = 0;
	public int Skill02_ID = 420544513;
	public int Skill02_Probablity = 10;
	public int Skill02_Target_Type = 2;
	public int SpecialSkill01_ID = 420610049;
	public int FieldCycle_ID = 5;
	public int FieldCycle_point = 10;

	@Override
	public void onEvtSpawn()
	{
		L2NpcInstance myself = getActor();
		myself.CreateOnePrivateEx(1022754,"SeedOfAnnihilation.turtlian",0,1,myself.getX()+Rnd.get(60, 150),myself.getY()+Rnd.get(60, 150),myself.getZ(),0,0,0,0);
		myself.CreateOnePrivateEx(1022756,"SeedOfAnnihilation.tardion",0,1,myself.getX()+Rnd.get(60, 150),myself.getY()+Rnd.get(60, 150),myself.getZ(),0,0,0,0);
		myself.CreateOnePrivateEx(1022755,"SeedOfAnnihilation.krakian",0,1,myself.getX()+Rnd.get(60, 150),myself.getY()+Rnd.get(60, 150),myself.getZ(),0,0,0,0);
		super.onEvtSpawn();
	}
}
