package ai;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * AI Bizarre Cocoon 
 * При юзе итема (14832) вызывается рб. Шанс неудачного спауна 8%. Респаун коконов 3 часа.
 * Кокон использует анимацию раскрытия, при вызове рб. (нету анимации скилла при вызове.. хз какой скилл)
 * @author Drizzy
 * @date 25.08.10
 * @Edit: Diagod
 * На деле у каждого РБ свой шанс на спаун, к тому же шанса фейла нету, есть шанс 1% получить итем без убеения РБ)
 */

public class BizarreCocoon extends DefaultAI
{
	private static final int Growth_Accelerator = 2905;
	
	public BizarreCocoon(L2Character actor)
	{
		super(actor);
	}
	
	protected boolean randomAnimation()
	{
		return false;
	}
	
	@Override
	public boolean isGlobalAI()
	{
		return true;
	}	
	
	@Override
	protected boolean randomWalk()
	{
		return false;
	}	

	public boolean isCrestEnable()
	{
		return false;
	}	
	
	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead() || skill == null)
			return;
			
		if(skill.getId() == Growth_Accelerator)
		{
			int i0 = Rnd.get(100);
			int RB = 0;
			if(i0 >= 99)
			{
				if(actor.DistFromMe(caster) <= 1500)
				{
					if(caster.getPlayer().getParty() != null)
					{
						for(L2Player member : caster.getPlayer().getParty().getPartyMembers())
							actor.GiveItem1(member,14834,3);
					}
					else
						actor.GiveItem1(caster.getPlayer(),14834,3);
				}
				actor.broadcastPacketToOthers2(new SocialAction(actor.getObjectId(), 1));
				actor.doDie(caster);
			}
			else
			{
				if(i0 >= 89)
					RB = 25670;
				else if(i0 >= 79)
					RB = 25669;
				else if(i0 >= 59)
					RB =  25667;
				else
					RB = 25668;

				try
				{
					L2Spawn sp = new L2Spawn(NpcTable.getTemplate(RB));
					Location pos = GeoEngine.findPointToStay(actor.getX(), actor.getY(), actor.getZ(), 0, 0, actor.getReflection().getGeoIndex());
					sp.setLoc(pos);
					L2NpcInstance npc = sp.doSpawn(true);
					actor.broadcastPacketToOthers2(new SocialAction(actor.getObjectId(), 1));
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, caster, Rnd.get(1, 100));
					actor.doDie(caster);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}					
			}
		}		
	}
}