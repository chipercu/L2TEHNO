package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.cache.FStringCache;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.util.Rnd;

/**
 * @author Diagod
 **/
public class SearchBox extends Fighter
{
	int	search_AW_1 = 8678;
	int	search_AW_2 = 8679;
	int	search_AW_3 = 8680;
	int	search_AW_4 = 8681;
	int	search_AW_5 = 8682;
	int	search_AW_6 = 8683;
	int	search_AW_7 = 8684;
	int	search_AW_8 = 8685;
	int	search_AW_9 = 8686;
	int	search_AW_10 = 8687;
	int	search_AW_11 = 8688;
	int	search_jaru1 = 10254;
	int	search_jaru2 = 10255;
	int	search_jaru3 = 10256;
	int	search_jaru4 = 10257;
	int	search_jaru5 = 10258;
	int	search_jaru6 = 10259;
	int	search_piece = 10272;
	int	search_wolfride = 10273;

	int _ololo = 0;

	public SearchBox(L2Character actor)
	{
		super(actor);
		actor.setParalyzed(true);
		AddTimerEx(1112,4000);
		AddTimerEx(1111,15000);
		_ololo = 1;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		final L2NpcInstance npc = getActor();
		npc.broadcastSkill(new MagicSkillUse(npc, npc, 3156, 1, 500, 0));
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character cast)
	{
		L2NpcInstance npc = getActor();
		L2Player caster = (L2Player)cast;
		if(npc == null || skill == null || npc.isDead())
			return;
		if(skill.getId() == 630)
		{
			if(npc != null)
				npc.doDie(cast);

			_ololo = 0;
			L2NpcInstance.Say(caster, npc, FStringCache.getString(1600006));
			int chance = Rnd.get(100000);
			if(chance < 1)
			{
				int chance2 = Rnd.get(11);
				if(chance2 < 1 )
					L2NpcInstance.GiveItem1(caster,search_AW_1,1L);
				else if(chance2 < 2 )
					L2NpcInstance.GiveItem1(caster,search_AW_2,1L);
				else if(chance2 < 3 )
					L2NpcInstance.GiveItem1(caster,search_AW_3,1L);
				else if(chance2 < 4 )
					L2NpcInstance.GiveItem1(caster,search_AW_4,1L);
				else if(chance2 < 5 )
					L2NpcInstance.GiveItem1(caster,search_AW_5,1L);
				else if(chance2 < 6 )
					L2NpcInstance.GiveItem1(caster,search_AW_6,1L);
				else if(chance2 < 7)
					L2NpcInstance.GiveItem1(caster,search_AW_7,1L);
				else if(chance2 < 8 )
					L2NpcInstance.GiveItem1(caster,search_AW_8,1L);
				else if(chance2 < 9 )
					L2NpcInstance.GiveItem1(caster,search_AW_9,1L);
				else if(chance2 < 10 )
					L2NpcInstance.GiveItem1(caster,search_AW_10,1L);
				else
					L2NpcInstance.GiveItem1(caster,search_AW_11,1L);
			}
			else if(chance < 13500)
				L2NpcInstance.GiveItem1(caster,search_wolfride,1L);
			else if(chance < 31500 )
			{
				int chanche2 = Rnd.get(10000);
				if(chanche2 < 24)
					L2NpcInstance.GiveItem1(caster,search_jaru1,1L);
				else if(chanche2 < 628)
					L2NpcInstance.GiveItem1(caster,search_jaru2,1L);
				else if(chanche2 < 1835)
					L2NpcInstance.GiveItem1(caster,search_jaru3,1L);
				else if(chanche2 < 3560)
					L2NpcInstance.GiveItem1(caster,search_jaru4,1L);
				else if(chanche2 < 5975)
					L2NpcInstance.GiveItem1(caster,search_jaru5,1L);
				else
					L2NpcInstance.GiveItem1(caster,search_jaru6,1L);
			}
			else
				L2NpcInstance.GiveItem1(caster,search_piece,5L + Rnd.get(11));
		}
		super.onEvtSeeSpell(skill, caster);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		L2NpcInstance npc = getActor();
		if(timer_id == 1111)
		{
			if(Rnd.get(2) < 1 && _ololo == 1)
			{
				if(Rnd.get(3) < 1)
					L2NpcInstance.Say(null, npc, FStringCache.getString(1600022));
				else if(Rnd.get(2) < 1)
					L2NpcInstance.Say(null, npc, FStringCache.getString(1600004));
				else
					L2NpcInstance.Say(null, npc, FStringCache.getString(1600005));
			}
			_ololo = 0;
			AddTimerEx(1113,3000);
		}
		else if(timer_id == 1112)
		{
			if(Rnd.get(3) < 1 && _ololo == 1)
			{
				if(Rnd.get(4) < 1)
					L2NpcInstance.Say(null, npc, FStringCache.getString(1600007));
				else if(Rnd.get(3) < 1)
					L2NpcInstance.Say(null, npc, FStringCache.getString(1600008));
				else if(Rnd.get(2) < 1)
					L2NpcInstance.Say(null, npc, FStringCache.getString(1600009));
				else
					L2NpcInstance.Say(null, npc, FStringCache.getString(1600010));
			}
			AddTimerEx(1112,4000);
		}
		else if(timer_id == 1113)
			npc.doDie(null);
	}
}