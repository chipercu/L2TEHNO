package ai.SeedOfAnnihilation;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.SkillTable;

/**
 * @author: Drizzy
 * @АИ сделано по птс скриптам. 100% соответсвие.
 */
public class ai_torumba_helper extends DefaultAI
{
	private L2Character myself = null;
	private static int POISON_SLASH = 6402;
	private static int TORUMBA_CURSE = 6406;
	private L2Character c0 = null;

	public ai_torumba_helper(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		c0 = L2ObjectsStorage.getByNpcId(25697);
		if(IsNullCreature(c0) == 0)
		{
			SendScriptEvent(c0,20091017,myself.getObjectId());
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 20091018)
		{
			if(L2ObjectsStorage.getPlayer(script_event_arg2) != null)
				if(L2ObjectsStorage.getPlayer(script_event_arg2).getEffectList().getEffectBySkillId(POISON_SLASH) != null)
				{
					int i0 = L2ObjectsStorage.getPlayer(script_event_arg2).getEffectList().getEffectBySkillId(POISON_SLASH).getSkill().getAbnormalLv();
					switch(i0)
					{
						case 11:
							AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(POISON_SLASH,2),1);
							break;
						case 12:
							AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(POISON_SLASH,3),1);
							break;
						case 13:
							AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(POISON_SLASH,4),1);
							break;
						case 14:
							AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(POISON_SLASH,5),1);
							break;
						case 15:
							AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(POISON_SLASH,5),1);
							break;
					}
				}
				else
					AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2), SkillTable.getInstance().getInfo(POISON_SLASH,1),1);
		}
		else if(script_event_arg1 == 20091020)
			AddUseSkillDesire(L2ObjectsStorage.getPlayer(script_event_arg2),SkillTable.getInstance().getInfo(TORUMBA_CURSE, 1),1);
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
