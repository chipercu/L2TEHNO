package ai;

import l2open.gameserver.ai.L2CharacterAI;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.tables.DoorTable;

/**
 * Запиздовал Diagod...
 * open-team.ru
 **/
public class doorkeeper extends L2CharacterAI
{
	private L2NpcInstance myself;
	public doorkeeper(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public String DoorName1 = "";
	public String DoorName2 = "";
	public String fnHi = "gludio_outter_doorman001.htm";
	public String fnNotMyLord = "gludio_outter_doorman002.htm";
	public String fnUnderSiege = "gludio_outter_doorman003.htm";
	public int pos_x01 = 1;
	public int pos_y01 = 1;
	public int pos_z01 = 1;
	public int pos_x02 = 1;
	public int pos_y02 = 1;
	public int pos_z02 = 1;
	public int dominion_id = 81;

	@Override
	public void TALKED(L2Player talker, int _code, int _from_choice)
	{
		if((myself.IsMyLord(talker) == 1) || (myself.HavePledgePower(talker,16) > 0 && myself.Castle_GetPledgeId() == talker.getClanId() && talker.getClanId() != 0) || talker.isGM())
		{
			if(myself.Castle_IsUnderSiege() || TerritorySiege.isInProgress())
			{
				if(myself.IsMyLord(talker) == 1 || myself.Castle_GetPledgeState(talker) == 2 || (myself.Castle_GetPledgeId() == talker.getClanId() && talker.getClanId() != 0) || talker.isGM())
				{
					myself.ShowPage(talker,fnHi);
				}
				else
				{
					myself.ShowPage(talker,fnUnderSiege);
				}
			}
			else
			{
				myself.ShowPage(talker,fnHi);
			}
		}
		else
		{
			myself.ShowPage(talker,fnNotMyLord);
		}
	}

	@Override
	public void MENU_SELECTED(L2Player talker, int ask, int reply)
	{
		StringBuilder fhtml0 = new StringBuilder();
		if(ask == -201)
		{
			if((myself.IsMyLord(talker) == 1) || (myself.HavePledgePower(talker,16) > 0 && myself.Castle_GetPledgeId() == talker.getClanId() && talker.getClanId() != 0) || talker.isGM())
			{
				if(myself.Castle_IsUnderSiege() || TerritorySiege.isInProgress())
				{
					myself.ShowPage(talker,fnUnderSiege);
				}
				else
				{
					switch(reply)
					{
						case 1:
							Castle_GateOpenClose2(DoorName1,0);
							Castle_GateOpenClose2(DoorName2,0);
							break;
						case 2:
							Castle_GateOpenClose2(DoorName1,1);
							Castle_GateOpenClose2(DoorName2,1);
							break;
					}
				}
			}
			else
			{
				myself.ShowPage(talker,fnNotMyLord);
			}
		}
		else if(ask == -202)
		{
			if(myself.IsMyLord(talker) == 1 || myself.Castle_GetPledgeState(talker) == 2 || (myself.HavePledgePower(talker,16) > 0 && myself.Castle_GetPledgeId() == talker.getClanId() && talker.getClanId() != 0) || talker.isGM())
			{
				switch(reply)
				{
					case 1:
						myself.InstantTeleport(talker,pos_x01,pos_y01,pos_z01);
						break;
					case 2:
						myself.InstantTeleport(talker,pos_x02,pos_y02,pos_z02);
						break;
				}
			}
			else
			{
				myself.ShowPage(talker,fnNotMyLord);
			}
		}
	}

	public static void Castle_GateOpenClose2(String name, int open)
	{
		L2DoorInstance door = DoorTable.getInstance().getDoor(name);
		if(door != null)
		{
			if(open == 1)
				door.closeMe();
			else
				door.openMe();
		}
	}
}
