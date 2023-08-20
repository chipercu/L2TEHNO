package npc.model;

import ai.hellbound.NaiaLock;
import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.NaiaTowerManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;

public class NaiaControllerInstance extends L2NpcInstance
{
	public String fnHi = "naiazma_key001.htm";
	public String fnHi2 = "naiazma_key002.htm";
	public int c_entrance_x = -47271;
	public int c_entrance_y = 246098;
	public int c_entrance_z = -9120;

	public NaiaControllerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		i_ai0 = 1;
		c_ai0 = null;
	}

	public void showChatWindow(L2Player talker, int val)
	{
		c_ai0 = talker;
		if(i_ai0 == 0)
			ShowPage(talker,fnHi2);
		else if(i_ai0 == 1)
			ShowPage(talker,fnHi);
	}

	@Override
	public void MENU_SELECTED(L2Player talker, int ask, int reply)
	{
		if(ask == -7801)
		{
			if(reply == 1)
			{
				c_ai0 = talker;
				for(L2NpcInstance lock : L2World.getAroundNpc(this, 300, 50))
					if(lock.getNpcId() == 18491)
					{
						lock.getAI().SendScriptEvent(lock, 78010026,0,0);
						break;
					}
			}
			else if(reply == 2)
			{
				if(!c_ai0.getPlayer().isInParty())
				{
					c_ai0.getPlayer().sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
					return;
				}
				if(talker.isInRange(c_ai0.getPlayer().getParty().getPartyLeader(), 3000))
					NaiaTowerManager.startNaiaTower(c_ai0.getPlayer());
				else
					doCast(SkillTable.getInstance().getInfo(5527, 1), c_ai0, true);
				/*L2Party party0 = c_ai0.getParty();
				if(IsNullParty(party0) == 0)
				{
					if(DistFromMe(GetLeaderOfParty(party0)) <= 3000)
						TeleportParty(party0.getObjectId(),c_entrance_x,c_entrance_y,c_entrance_z,2000,0);
					else
						doCast(SkillTable.getInstance().getInfo(5527, 1), c_ai0, true);
				}
				else
					InstantTeleport(c_ai0,c_entrance_x,c_entrance_y,c_entrance_z);*/
				if(ConfigValue.NaiaLockCanFail)
					i_ai0 = 0;
			}
		}
	}
}