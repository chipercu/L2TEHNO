package ai.SeedOfAnnihilation;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2World;
import l2open.gameserver.tables.SkillTable;

/**
 * author: Drizzy
 * АИ для помощников таркхана и допагена. Сделано по птс.
 */
public class ai_a_seed_boss_helper extends DefaultAI
{
	private L2Character myself = null;
	private int max_desire = 10000000;
	private int CurseOfTacrakhan = 6650;
	private int CurseOfDopagen = 6651;
	private int HPMPSKILL01_ID = 6376;
	private L2Character c0 = null;

	public ai_a_seed_boss_helper(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		for(L2Character cha : L2World.getAroundCharacters(getActor(), 1000, 300))
			if(cha.getNpcId() == 25696 || cha.getNpcId() == 25698)
				c0 = cha;
		if(IsNullCreature(c0) == 0)
		{
			SendScriptEvent(c0,20091017,myself.getObjectId());
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if( script_event_arg1 == 20091021 )
		{
			AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2), SkillTable.getInstance().getInfo(CurseOfTacrakhan,1),1);
		}
		else if( script_event_arg1 == 20091022 )
		{
			AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(CurseOfDopagen,1),1);
		}
		else if( script_event_arg1 == 20091025 )
		{
			if(L2ObjectsStorage.getPlayer(script_event_arg2) != null)
				if(L2ObjectsStorage.getPlayer(script_event_arg2).getEffectList().getEffectBySkillId(HPMPSKILL01_ID) != null)
				{
					int i0 = L2ObjectsStorage.getPlayer(script_event_arg2).getEffectList().getEffectBySkillId(HPMPSKILL01_ID).getSkill().getAbnormalLv();
					switch(i0)
					{
						case 1:
							AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(HPMPSKILL01_ID,2),1);
							break;
						case 2:
							AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(HPMPSKILL01_ID,3),1);
							break;
						case 3:
							AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(HPMPSKILL01_ID,4),1);
							break;
						case 4:
							AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(HPMPSKILL01_ID,5),1);
							break;
						case 5:
							AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(HPMPSKILL01_ID,6),1);
							break;
						case 6:
							AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(HPMPSKILL01_ID,7),1);
							break;
						case 7:
							AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(HPMPSKILL01_ID,8),1);
							break;
						case 8:
							AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(HPMPSKILL01_ID,9),1);
							break;
					}
				}
		}
	}

	@Override
	public void NO_DESIRE()
	{
		if(c0 != null && !c0.isDead())
			if(getActor().getRealDistance3D(c0) > 500)
				getActor().teleToLocation(c0.getX(),c0.getY(),c0.getZ() + 10);
		else if(c0 == null || c0.isDead())
		{
			getActor().deleteMe();
		}
		super.NO_DESIRE();
	}
}
