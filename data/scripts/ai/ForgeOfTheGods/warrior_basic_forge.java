package ai.ForgeOfTheGods;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * @date: 19.08.2012
 * AI for all mobs in the FoG - 100% PTS.
 */

public class warrior_basic_forge extends Fighter
{
	private L2Character myself = null;
	private int TID_MOB_COUNT_REFRESH = 78001;
	private int TIME_MOB_COUNT_REFRESH = 15;
	private int MobCount_bonus_min = 3;
	private int MobCount_bonus_upper_lv01 = 5;
	private int MobCount_bonus_upper_lv02 = 10;
	private int MobCount_bonus_upper_lv03 = 15;
	private int MobCount_bonus_upper_lv04 = 20;
	private int MobCount_bonus_upper_lv05 = 35;
	private int Prob_forge_bonus01 = 20;
	private int Prob_forge_bonus02 = 40;
	private int i_ai1 = 0;

	public warrior_basic_forge(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		i_ai1 = 0;
		super.onEvtSpawn();
		AddTimerEx(TID_MOB_COUNT_REFRESH,( TIME_MOB_COUNT_REFRESH * 1000 ));
	}

	@Override
	public void CLAN_DIED(L2Character victim, L2Character attacker)
	{
		if(victim != myself && DistFromMe(victim) <= 300 && victim.getNpcId() != 18799 && victim.getNpcId() != 18800 && victim.getNpcId() != 18801 && victim.getNpcId() != 18802 && victim.getNpcId() != 18803 && victim.getNpcId() != 22642 && victim.getNpcId() != 22643)
			i_ai1++;
		//_log.info("CLAN_DIED: i_ai1="+i_ai1);
		super.CLAN_DIED(victim,attacker);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TID_MOB_COUNT_REFRESH)
		{
			if(!myself.isDead() && i_ai1 > 0 && myself.getCurrentHp() == myself.getMaxHp())
				i_ai1 = 0;
			AddTimerEx(TID_MOB_COUNT_REFRESH,( TIME_MOB_COUNT_REFRESH * 1000 ));
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		int i0 = Rnd.get(100);
		//_log.info("MY_DYING: Rnd="+i0+" i_ai1="+i_ai1);
		if(i_ai1 > MobCount_bonus_upper_lv05 && i0 <= Prob_forge_bonus02)
			CreateOnePrivateEx(18803,"ForgeOfTheGods.wizard_bonus_forge", "L2Monster", getActor().getX(), getActor().getY(), getActor().getZ(), 0);
		else if(i_ai1 > MobCount_bonus_upper_lv04 && i_ai1 <= MobCount_bonus_upper_lv05)
		{
			if(i0 <= Prob_forge_bonus01)
				CreateOnePrivateEx(18803,"ForgeOfTheGods.wizard_bonus_forge", "L2Monster", getActor().getX(), getActor().getY(), getActor().getZ(), 0);
			else if(i0 <= Prob_forge_bonus02)
				CreateOnePrivateEx(18802,"ForgeOfTheGods.wizard_bonus_forge", "L2Monster", getActor().getX(), getActor().getY(), getActor().getZ(), 0);
		}
		else if(i_ai1 > MobCount_bonus_upper_lv03 && i_ai1 <= MobCount_bonus_upper_lv04)
		{
			if(i0 <= Prob_forge_bonus01)
				CreateOnePrivateEx(18802,"ForgeOfTheGods.wizard_bonus_forge", "L2Monster", getActor().getX(), getActor().getY(), getActor().getZ(), 0);
			else if(i0 <= Prob_forge_bonus02)
				CreateOnePrivateEx(18801,"ForgeOfTheGods.wizard_bonus_forge", "L2Monster", getActor().getX(), getActor().getY(), getActor().getZ(), 0);
		}
		else if(i_ai1 > MobCount_bonus_upper_lv02 && i_ai1 <= MobCount_bonus_upper_lv03)
		{
			if(i0 <= Prob_forge_bonus01)
				CreateOnePrivateEx(18801,"ForgeOfTheGods.wizard_bonus_forge", "L2Monster", getActor().getX(), getActor().getY(), getActor().getZ(), 0);
			else if(i0 <= Prob_forge_bonus02)
				CreateOnePrivateEx(18800,"ForgeOfTheGods.wizard_bonus_forge", "L2Monster", getActor().getX(), getActor().getY(), getActor().getZ(), 0);
		}
		else if(i_ai1 > MobCount_bonus_upper_lv01 && i_ai1 <= MobCount_bonus_upper_lv02)
		{
			if(i0 <= Prob_forge_bonus01)
				CreateOnePrivateEx(18800,"ForgeOfTheGods.wizard_bonus_forge", "L2Monster", getActor().getX(), getActor().getY(), getActor().getZ(), 0);
			else if(i0 <= Prob_forge_bonus02)
				CreateOnePrivateEx(18799,"ForgeOfTheGods.wizard_bonus_forge", "L2Monster", getActor().getX(), getActor().getY(), getActor().getZ(), 0);
		}
		if(i_ai1 >= MobCount_bonus_min && i_ai1 <= MobCount_bonus_upper_lv01 && i0 <= Prob_forge_bonus01)
			CreateOnePrivateEx(18799,"ForgeOfTheGods.wizard_bonus_forge", "L2Monster", getActor().getX(), getActor().getY(), getActor().getZ(), 0);
		i_ai1 = 0;
		super.MY_DYING(killer);
	}
}
