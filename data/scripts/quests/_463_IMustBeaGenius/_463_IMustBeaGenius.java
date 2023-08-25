package quests._463_IMustBeaGenius;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.util.Files;
import l2open.util.Rnd;


public class _463_IMustBeaGenius extends Quest implements ScriptFile
{
	private static final int _gutenhagen = 32069;
	private static final int _corpse_log = 15510;
	private static final int _collection = 15511;
	private static final int[] _mobs = { 22801, 22802, 22804, 22805, 22807, 22808, 22809, 22810, 22811, 22812};
	private static final int[][] _reward = { { 198725, 15892 }, { 278216, 22249 }, { 317961, 25427 }, { 357706, 28606 }, { 397451, 31784 }, { 596176, 47677 }, { 715411, 57212 }, { 794901, 63569 }, { 914137, 73104 }, { 1192352, 95353 } };
	
	public _463_IMustBeaGenius()
	{
		super(false);
		addStartNpc(_gutenhagen);
		for(int _mob : _mobs)
			addKillId(_mob);
	}
	
	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		
		if (npc.getNpcId() == _gutenhagen)
		{
			if (event.equalsIgnoreCase("32069-03.htm"))
			{
				st.playSound(SOUND_ACCEPT);
				st.setState(STARTED);
				st.set("cond", "1");
				int _number = Rnd.get(500, 600);
				st.set("number", String.valueOf(_number));
				for(int _mob : _mobs)
				{
					int _rand = Rnd.get(-2, 4);
					if(_rand == 0)
						_rand = 5;
					st.set(String.valueOf(_mob), String.valueOf(_rand));
				}
				st.set(String.valueOf(_mobs[Rnd.get(0, _mobs.length-1)]), String.valueOf(Rnd.get(1, 100)));
				String htmltext1 = Files.read("data/scripts/quests/_463_IMustBeaGenius/32069-03.htm", st.getPlayer());		
				htmltext = htmltext1.replace("%num%", String.valueOf(_number));
			}
			else if (event.equalsIgnoreCase("32069-05.htm"))
			{
				String htmltext1 = Files.read("data/scripts/quests/_463_IMustBeaGenius/32069-05.htm", st.getPlayer());
				htmltext = htmltext1.replace("%num%", st.get("number"));
			}
			else if (event.equalsIgnoreCase("32069-07.htm"))
			{
				int i = Rnd.get(_reward.length);
			    st.addExpAndSp(_reward[i][0], _reward[i][1]);
				st.unset("number");
				for(int _mob : _mobs)
					st.unset(String.valueOf(_mob));
				st.takeItems(_collection, -1);
				st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(this);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();
		
		if (npcId == _gutenhagen)
		{
			if(id == CREATED)
            {
               if(!st.isNowAvailable())
				    return "32069-08.htm";
                else
                {
                    if (st.getPlayer().getLevel() >= 70)
                        return "32069-01.htm";
                    else
                        return "32069-00.htm";
                }
            }
			if(id == STARTED)
				if (cond == 1)
					return "32069-04.htm";
				else if(cond == 2)
					return "32069-06.htm";
		}
		return "noquest";
	}
	
	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getInt("cond");
		
		if (id == STARTED && cond == 1 && arrayContains(_mobs, npc.getNpcId()))
		{
			int _number = st.getInt(String.valueOf(npc.getNpcId()));
			
			if(_number > 0)
			{
				st.giveItems(_corpse_log, _number);
				st.playSound(SOUND_ITEMGET);
				npc.broadcastPacket(new NpcSay(npc, Say2C.NPC_ALL, "Att... attack... "+st.getPlayer().getName()+"... Ro... rogue... "+_number+".."));
				check(st);
			}
			else if (_number < 0 && ((st.getQuestItemsCount(_corpse_log)+_number) > 0))
			{
				st.takeItems(_corpse_log, Math.abs(_number));
				st.playSound(SOUND_ITEMGET);
				npc.broadcastPacket(new NpcSay(npc, Say2C.NPC_ALL, "Att... attack... "+st.getPlayer().getName()+"... Ro... rogue... "+_number+".."));
				check(st);
			}			
		}
		return null;
	}

	private void check(QuestState st)
	{
		int _day_number = st.getInt("number");
		if (st.getQuestItemsCount(_corpse_log) == _day_number)
		{
			st.takeItems(_corpse_log, -1);
			st.giveItems(_collection, 1);
			st.set("cond", "2");
		}	
	}
	
	private boolean arrayContains(int[] array, int id)
	{
		for(int i : array)
			if(i == id)
				return true;
		return false;
	}
	
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}	
}