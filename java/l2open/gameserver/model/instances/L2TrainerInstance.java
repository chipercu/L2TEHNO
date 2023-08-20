package l2open.gameserver.model.instances;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.templates.L2NpcTemplate;

public final class L2TrainerInstance extends L2NpcInstance // deprecated?
{
	public L2TrainerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if(val == 0)
			pom = String.valueOf(npcId);
		else
			pom = npcId + "-" + val;

		return "/data/html/trainer/" + pom + ".htm";
	}

	@Override
	public void MENU_SELECTED(L2Player talker, int ask, int reply)
	{
		int i0 = 0;
		L2Player c0 = null;
		if(ask == 708)
		{
			if(reply == 1)
			{
				c0 = Pledge_GetLeader(talker);
				if(IsNullCreature(c0) == 0)
					if(HaveMemo(c0,708) == 1 && GetMemoState(c0,708) == 4)
						ShowQuestPage(talker,"blacksmith_pinter_q0708_04.htm",708);
			}
			if(reply == 2)
			{
				c0 = Pledge_GetLeader(talker);
				if(IsNullCreature(c0) == 0)
				{
					if(HaveMemo(c0,708) == 1 && GetMemoState(c0,708) == 4)
					{
						ShowOnScreenMsgStr(c0,2,0,0,0,1,0,5000,0,MakeFString(70853,"","","","",""));
						ShowPage(talker,"blacksmith_pinter_q0708_05.htm");
						SetMemoState(c0,708,5);
						SetMemoStateEx(c0,708,1,0);
						SetFlagJournal(c0,708,4);
						ShowQuestMark(c0,708);
						SoundEffect(c0,"ItemSound.quest_middle");
					}
				}
				else
					ShowPage(talker,"blacksmith_pinter_q0708_06.htm");
			}
			if(reply == 3)
			{
				c0 = Pledge_GetLeader(talker);
				if(IsNullCreature(c0) == 0)
				{
					if(HaveMemo(c0, 708) == 1 && GetMemoState(c0, 708) % 10 == 5)
					{
						i0 = (GetMemoState(c0,708) / 10);
						if(OwnItemCount(talker,1867) >= 100 && OwnItemCount(talker,1865) >= 100 && OwnItemCount(talker,1869) >= 100 && OwnItemCount(talker,1879) >= 50)
						{
							DeleteItem1(talker,1867,100);
							DeleteItem1(talker,1865,100);
							DeleteItem1(talker,1869,100);
							DeleteItem1(talker,1879,50);
							SetMemoState(c0,708,(9 + (i0 * 10)));
							ShowPage(talker,"blacksmith_pinter_q0708_09.htm");
						}
						else
							ShowPage(talker,"blacksmith_pinter_q0708_10.htm");
					}
				}
				else
					ShowPage(talker,"blacksmith_pinter_q0708_11.htm");
			}
		}
		super.MENU_SELECTED(talker, ask, reply);
	}
}
