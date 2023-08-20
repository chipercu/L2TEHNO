package ai.PlainsOfLizardmen;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * АИ для автолута хербов.
 */

public class ai_auragrafter extends DefaultAI
{
	private L2Character myself = null;
	private int TID_LIFETIME = 78001;
	private int TIME_LIFETIME = 3;
	private int aura_hp01 = 6625;
	private int aura_hp02 = 6626;
	private int aura_hp03 = 6627;
	private int aura_mp01 = 6628;
	private int aura_mp02 = 6629;
	private int aura_mp03 = 6630;
	private int aura_melee01 = 6631;
	private int aura_melee02 = 6633;
	private int aura_melee03 = 6635;
	private int aura_melee05 = 6639;
	private int aura_bow01 = 6674;
	private int aura_special01 = 6636;
	private int aura_special02 = 6638;
	private int aura_special03 = 6640;
	private L2Character c_ai0;

	public ai_auragrafter(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		c_ai0 = L2ObjectsStorage.getCharacter(ID);
		if(IsNullCreature(c_ai0) == 0 )
		{
			int i0 = Rnd.get(100);
			if(i0 <= 42)
			{
				int i1 = Rnd.get(100);
				if(i1 <= 7)
				{
					AddUseSkillDesire(c_ai0, SkillTable.getInstance().getInfo(aura_hp03,3), 1);
				}
				else if( i1 <= 45 )
				{
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_hp02,2), 1);
				}
				AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_hp01,1), 1);
			}
			if( i0 <= 11 )
			{
				int i1 = Rnd.get(100);
				if( i1 <= 8 )
				{
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_mp03,3),1);
				}
				else if( i1 <= 60 )
				{
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_mp02,2),1);
				}
				AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_mp01,1),1);
			}
			if( i0 <= 25 )
			{
				int i1 = Rnd.get(100);
				if( i1 <= 20 )
				{
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_melee05,1),1);
				}
				else if( i1 <= 40 )
				{
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_bow01,1),1);
				}
				else if( i1 <= 60 )
				{
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_melee03,1),1);
				}
				else if( i1 <= 80 )
				{
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_melee02,1),1);
				}
				AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_melee01,1),1);
			}
			if( i0 <= 10 )
			{
				AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_bow01,1),1);
			}
			if( i0 <= 1 )
			{
				int i1 = Rnd.get(100);
				if( i1 <= 34 )
				{
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_melee01,1),1);
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_melee02,1),1);
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_melee03,1),1);
				}
				else if( i1 <= 67 )
				{
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_bow01,1),1);
				}
				AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_hp03,3),1);
				AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_mp03,3),1);
			}
			if( i0 <= 11 )
			{
				int i1 = Rnd.get(100);
				if( i1 <= 3 )
				{
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_special03,1),1);
				}
				else if( i1 <= 6 )
				{
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_special02,1),1);
				}
				AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(aura_special01,1),1);
			}
		}
		AddTimerEx(TID_LIFETIME,( TIME_LIFETIME * 1000 ));
	}
	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TID_LIFETIME)
		{
			getActor().deleteMe();
		}
	}
}