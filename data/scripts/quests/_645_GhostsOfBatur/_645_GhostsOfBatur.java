package quests._645_GhostsOfBatur; 

import java.io.File; 

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile; 
import l2open.gameserver.model.instances.L2NpcInstance; 
import l2open.gameserver.model.L2Multisell; 
import l2open.gameserver.model.quest.Quest; 
import l2open.gameserver.model.quest.QuestState; 
import l2open.util.Rnd; 

/** 
*@author Drizzy 
*@version Gracia Epilogue 
*@date 06.06.10 
*/ 

	public class _645_GhostsOfBatur extends Quest implements ScriptFile 
	{ 
		public void onLoad() 
		{} 

		public void onReload() 
		{} 
  
		public void onShutdown() 
		{} 
      
		private static final int Karuda = 32017; 
	    //private static final int CursedGraveGoods = 8089;
        private static final int CursedBurialItems = 14861;
		private static final int DropRate = 5; 
		private static final int[] Mobs = { 22703,22704, 22705, 22706, 22707 }; 
          
		public _645_GhostsOfBatur() 
		{ 
				super(true); 
				addStartNpc(Karuda); 
				addTalkId(Karuda); 
				addKillId(Mobs); 
				addQuestItem(CursedBurialItems);
		} 
  
		@Override 
		public String onEvent(String event, QuestState st, L2NpcInstance npc) 
		{ 
				String htmltext = event; 
				int cond = st.getInt("cond"); 

		if(event.equalsIgnoreCase("32017-03.htm") && cond == 0) 
		{ 
			st.set("cond", "1"); 
			st.setState(STARTED); 
			st.playSound(SOUND_ACCEPT); 
		} 
				return htmltext; 
		} 
  
		@Override 
		public String onTalk(L2NpcInstance npc, QuestState st) 
		{ 
				String htmltext = "noquest"; 
				int npcId = npc.getNpcId(); 
				int cond = st.getInt("cond"); 
                  
				if(npcId == Karuda) 
				{ 
					if(cond == 0) 
								if(st.getPlayer().getLevel() >= 80) 
										htmltext = "32017-01.htm"; 
								else 
										htmltext = "32017-02.htm"; 
  
					if(cond == 1) 
								if(st.getQuestItemsCount(CursedBurialItems) > 0)
										htmltext = "32017-05.htm"; 
								else 
										htmltext = "32017-04.htm"; 
				} 
				return htmltext; 
		} 
  
		@Override 
		public String onKill(L2NpcInstance npc, QuestState st) 
		{ 
				if(st.getState() == STARTED) 
						st.rollAndGive(CursedBurialItems, (int)ConfigValue.RateQuestsDrop, DropRate);
				return null; 
		}
	}