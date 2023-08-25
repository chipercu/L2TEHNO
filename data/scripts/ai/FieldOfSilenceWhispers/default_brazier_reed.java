package ai.FieldOfSilenceWhispers;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * АИ для брейзеров и машин в Field of Silence\Whispers
 */

public class default_brazier_reed extends DefaultAI
{
	private L2Character myself = null;
	private int loot = 8605;
	private int loot_roll = 33;
	private int TID_SIGNAL_ROUTINE = 78002;
	private int TIME_SIGNAL_ROUTINE = 60;
	private int i_ai1 = 0;

	public default_brazier_reed(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		AddTimerEx(TID_SIGNAL_ROUTINE,(TIME_SIGNAL_ROUTINE * 1000));
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TID_SIGNAL_ROUTINE)
		{
			BroadcastScriptEvent(78010078,myself.getObjectId(),300);
			AddTimerEx(TID_SIGNAL_ROUTINE,(TIME_SIGNAL_ROUTINE * 1000));
		}
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(IsNullCreature(attacker) == 0)
		{
			if(Rnd.get(3) == 0)
			{
				if(i_ai1 == 0)
				{
					NpcSay ns;
					if(myself.getNpcId() == 18805)
					{
						ns = new NpcSay(getActor(), Say2C.NPC_ALL, 1800851);
					}
					else
					{
						ns = new NpcSay(getActor(), Say2C.NPC_ALL, 1800854);
					}
					i_ai1 = 1;
					getActor().broadcastPacket(ns);
				}
				BroadcastScriptEvent(78010079,attacker.getObjectId(),500);
			}
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		if(IsNullCreature(killer) == 0)
		{
			if((killer.isPlayer() && killer.isMageClass()) || ((killer.isPet() || killer.isSummon()) && (killer.getPet() != null && killer.getPet().getPlayer() != null && killer.getPet().getPlayer().isMageClass())))
			{
				int i0 = Rnd.get(100);
				if(i0 <= loot_roll)
				{
					DropItem1(getActor(),loot,1);
				}
			}
		}
		super.MY_DYING(killer);
	}
}
