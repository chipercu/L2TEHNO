package quests._129_PailakaDevilsLegacy;

import javolution.util.FastMap;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Location;
import l2open.util.Rnd;

public class _129_PailakaDevilsLegacy extends Quest implements ScriptFile
{
	// NPC
	private static int DISURVIVOR = 32498;
	private static int SUPPORTER = 32501;
	private static int DADVENTURER = 32508;
	private static int DADVENTURER2 = 32511;
	private static int CHEST = 32495;
	private static int[] Pailaka2nd = new int[] { 18623, 18624, 18625, 18626, 18627 };

	// BOSS
	private static int KAMS = 18629;
	private static int ALKASO = 18631;
	private static int LEMATAN = 18633;

	// ITEMS
	private static int ScrollOfEscape = 736;
	private static int SWORD = 13042;
	private static int ENCHSWORD = 13043;
	private static int LASTSWORD = 13044;
	private static int KDROP = 13046;
	private static int ADROP = 13047;
	private static int KEY = 13150;
	private static int[] HERBS = new int[] { 8601, 8602, 8604, 8605 };
	private static int[] CHESTDROP = new int[] { 13033, 13048, 13049 }; // TODO нет скилла для бутылки: , 13059 };

	// REWARDS
	private static int PBRACELET = 13295;
	//private static int PERING = 13293;

	private static FastMap<Integer, Integer> _instances = new FastMap<Integer, Integer>();

	public _129_PailakaDevilsLegacy()
	{
		super(false);

		addStartNpc(DISURVIVOR);
		addTalkId(SUPPORTER, DADVENTURER, DADVENTURER2);
		addKillId(KAMS, ALKASO, LEMATAN, CHEST);
		addKillId(Pailaka2nd);
		addQuestItem(SWORD, ENCHSWORD, LASTSWORD, KDROP, ADROP, KEY);
		addQuestItem(CHESTDROP);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		L2Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("Enter"))
		{
			enterInstance(player);
			return null;
		}
		else if(event.equalsIgnoreCase("32498-02.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32498-05.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32501-03.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(SWORD, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int id = st.getState();
		L2Player player = st.getPlayer();
		if(npcId == DISURVIVOR)
		{
			if(cond == 0)
				if(player.getLevel() < 61 || player.getLevel() > 67)
				{
					htmltext = "32498-no.htm";
					st.exitCurrentQuest(true);
				}
				else
					return "32498-01.htm";
			else if(id == COMPLETED)
				htmltext = "32498-no.htm";
			else if(cond == 1 || cond == 2)
				htmltext = "32498-06.htm";
			else
				htmltext = "32498-07.htm";
		}
		else if(npcId == SUPPORTER)
		{
			if(cond == 1 || cond == 2)
				htmltext = "32501-01.htm";
			else
				htmltext = "32501-04.htm";
		}
		else if(npcId == DADVENTURER)
		{
			if(st.getQuestItemsCount(SWORD) > 0 && st.getQuestItemsCount(KDROP) == 0)
				htmltext = "32508-01.htm";
			if(st.getQuestItemsCount(ENCHSWORD) > 0 && st.getQuestItemsCount(ADROP) == 0)
				htmltext = "32508-01.htm";
			if(st.getQuestItemsCount(SWORD) == 0 && st.getQuestItemsCount(KDROP) > 0)
				htmltext = "32508-05.htm";
			if(st.getQuestItemsCount(ENCHSWORD) == 0 && st.getQuestItemsCount(ADROP) > 0)
				htmltext = "32508-05.htm";
			if(st.getQuestItemsCount(SWORD) == 0 && st.getQuestItemsCount(ENCHSWORD) == 0)
				htmltext = "32508-05.htm";
			if(st.getQuestItemsCount(KDROP) == 0 && st.getQuestItemsCount(ADROP) == 0)
				htmltext = "32508-01.htm";
			if(player.getPet() != null)
				htmltext = "32508-04.htm";
			if(st.getQuestItemsCount(SWORD) > 0 && st.getQuestItemsCount(KDROP) > 0)
			{
				st.takeItems(SWORD, 1);
				st.takeItems(KDROP, 1);
				st.giveItems(ENCHSWORD, 1);
				htmltext = "32508-02.htm";
			}
			if(st.getQuestItemsCount(ENCHSWORD) > 0 && st.getQuestItemsCount(ADROP) > 0)
			{
				st.takeItems(ENCHSWORD, 1);
				st.takeItems(ADROP, 1);
				st.giveItems(LASTSWORD, 1);
				htmltext = "32508-03.htm";
			}
			if(st.getQuestItemsCount(LASTSWORD) > 0)
				htmltext = "32508-03.htm";
		}
		else if(npcId == DADVENTURER2)
		{
			if(cond == 4)
			{
				if(player.getPet() != null)
					htmltext = "32511-03.htm";
				else if(player.getPet() == null)
				{
					st.giveItems(ScrollOfEscape, 1);
					st.giveItems(PBRACELET, 1);
					st.addExpAndSp(10800000, 950000);
					st.setCond(5);
					st.setState(COMPLETED);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(false);
					player.setVitality(20000);
					player.getReflection().startCollapseTimer(60000);
					htmltext = "32511-01.htm";
				}
			}
			else if(id == COMPLETED)
				htmltext = "32511-02.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int refId = player.getReflection().getId();
		if(npcId == KAMS && st.getQuestItemsCount(KDROP) == 0)
			st.giveItems(KDROP, 1);
		else if(npcId == ALKASO && st.getQuestItemsCount(ADROP) == 0)
			st.giveItems(ADROP, 1);
		else if(npcId == LEMATAN)
		{
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
			addSpawnToInstance(DADVENTURER2, new Location(84990, -208376, -3342, 55000), 0, refId);
		}
		else if(contains(Pailaka2nd, npcId))
		{
			if(Rnd.get(100) < 80)
				dropItem(npc, HERBS[Rnd.get(HERBS.length)], Rnd.get(1, 2));
		}
		else if(npcId == CHEST)
		{
			if(Rnd.get(100) < 80)
				dropItem(npc, CHESTDROP[Rnd.get(CHESTDROP.length)], Rnd.get(1, 10));
			// TODO вернуть когда будут работать двери
			//else
			//	dropItem(npc, KEY, 1);
		}
		return null;
	}

	private void enterInstance(L2Player player)
	{
		int instancedZoneId = 44;
		InstancedZoneManager ilm = InstancedZoneManager.getInstance();
		FastMap<Integer, InstancedZone> ils = ilm.getById(instancedZoneId);
		if(ils == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}

		InstancedZone il = ils.get(0);
		boolean dispellBuffs = false;//il.isDispellBuffs();

		assert il != null;

		if(player.isInParty())
		{
			player.sendPacket(Msg.A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA);
			return;
		}

		if(player.isCursedWeaponEquipped())
		{
			player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
			return;
		}

		Integer old = _instances.get(player.getObjectId());
		if(old != null)
		{
			Reflection old_r = ReflectionTable.getInstance().get(old);
			if(old_r != null)
			{
				if(dispellBuffs)
				{
					for(L2Effect e : player.getEffectList().getAllEffects())
						if(!e.getSkill().isOffensive() && !e.getSkill().isNewbie() && e.getSkill().isCancelable())
							e.exit(false, false);
					if(player.getPet() != null)
					{
						for(L2Effect e : player.getPet().getEffectList().getAllEffects())
							if(!e.getSkill().isOffensive() && !e.getSkill().isNewbie() && e.getSkill().isCancelable())
								e.exit(false, false);
						player.getPet().updateEffectIcons();
					}
					player.updateEffectIcons();
				}
				player.setReflection(old_r);
				player.teleToLocation(il.getTeleportCoords());
				player.setVar("backCoords", old_r.getReturnLoc().toXYZString());
				return;
			}
		}

		Reflection r = new Reflection(il.getName());
		r.setInstancedZoneId(instancedZoneId);
		for(InstancedZone i : ils.values())
		{
			if(r.getReturnLoc() == null)
				r.setReturnLoc(i.getReturnCoords());
			if(r.getTeleportLoc() == null)
				r.setTeleportLoc(i.getTeleportCoords());
			r.FillSpawns(i.getSpawnsInfo());
		}

		int timelimit = il.getTimelimit();

		if(dispellBuffs)
		{
			for(L2Effect e : player.getEffectList().getAllEffects())
				if(!e.getSkill().isOffensive() && !e.getSkill().isNewbie() && e.getSkill().isCancelable())
					e.exit(false, false);
			if(player.getPet() != null)
			{
				for(L2Effect e : player.getPet().getEffectList().getAllEffects())
					if(!e.getSkill().isOffensive() && !e.getSkill().isNewbie() && e.getSkill().isCancelable())
						e.exit(false, false);
				player.getPet().updateEffectIcons();
			}
			player.updateEffectIcons();
		}
		player.setReflection(r);
		player.teleToLocation(il.getTeleportCoords());
		player.setVar("backCoords", r.getReturnLoc().toXYZString());
		player.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(timelimit));

		r.setNotCollapseWithoutPlayers(true);
		r.startCollapseTimer(timelimit * 60 * 1000L);

		_instances.put(player.getObjectId(), r.getId());
	}

	private void dropItem(L2NpcInstance npc, int itemId, int count)
	{
		L2ItemInstance item = ItemTemplates.getInstance().createItem(itemId);
		item.setCount(count);
		item.dropMe(npc, npc.getLoc());
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}