package quests._128_PailakaSongofIceandFire;

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

public class _128_PailakaSongofIceandFire extends Quest implements ScriptFile
{
	// NPC
	private static int ADLER = 32497;
	private static int ADLER2 = 32510;
	private static int SINAI = 32500;
	private static int TINSPECTOR = 32507;

	// BOSS
	private static int HILLAS = 18610;
	private static int PAPION = 18609;
	private static int GARGOS = 18607;
	private static int KINSUS = 18608;
	private static int ADIANTUM = 18620;

	// MOBS
	private static int Bloom = 18616;
	private static int CrystalWaterBottle = 32492;
	private static int BurningBrazier = 32493;

	// ITEMS
	private static int PailakaInstantShield = 13032;
	private static int QuickHealingPotion = 13033;
	private static int FireAttributeEnhancer = 13040;
	private static int WaterAttributeEnhancer = 13041;
	private static int SpritesSword = 13034;
	private static int EnhancedSpritesSword = 13035;
	private static int SwordofIceandFire = 13036;
	private static int EssenceofWater = 13038;
	private static int EssenceofFire = 13039;

	private static int TempleBookofSecrets1 = 13130;
	private static int TempleBookofSecrets2 = 13131;
	private static int TempleBookofSecrets3 = 13132;
	private static int TempleBookofSecrets4 = 13133;
	private static int TempleBookofSecrets5 = 13134;
	private static int TempleBookofSecrets6 = 13135;
	private static int TempleBookofSecrets7 = 13136;

	// REWARDS
	private static int PailakaRing = 13294;
	private static int PailakaEarring = 13293;
	private static int ScrollofEscape = 736;

	private static int[] MOBS = new int[] { 18611, 18612, 18613, 18614, 18615 };
	private static int[] HPHERBS = new int[] { 8600, 8601, 8602 };
	private static int[] MPHERBS = new int[] { 8603, 8604, 8605 };

	private static FastMap<Integer, Integer> _instances = new FastMap<Integer, Integer>();

	public _128_PailakaSongofIceandFire()
	{
		super(false);

		addStartNpc(ADLER);
		addTalkId(ADLER2, SINAI);
		addFirstTalkId(TINSPECTOR);
		addKillId(HILLAS, PAPION, ADIANTUM, KINSUS, GARGOS, Bloom, CrystalWaterBottle, BurningBrazier);
		addKillId(MOBS);
		addQuestItem(SpritesSword, EnhancedSpritesSword, SwordofIceandFire, EssenceofWater, EssenceofFire);
		addQuestItem(TempleBookofSecrets1, TempleBookofSecrets2, TempleBookofSecrets3, TempleBookofSecrets4, TempleBookofSecrets5, TempleBookofSecrets6, TempleBookofSecrets7);
		addQuestItem(PailakaInstantShield, QuickHealingPotion, FireAttributeEnhancer, WaterAttributeEnhancer);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		L2Player player = st.getPlayer();
		int refId = player.getReflection().getId();
		String htmltext = event;

		if(event.equalsIgnoreCase("Enter"))
		{
			enterInstance(player);
			return null;
		}
		else if(event.equalsIgnoreCase("32497-04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32500-06.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(SpritesSword, 1);
			st.giveItems(TempleBookofSecrets1, 1);
		}
		else if(event.equalsIgnoreCase("32507-03.htm"))
		{
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
			st.takeItems(TempleBookofSecrets2, -1);
			st.giveItems(TempleBookofSecrets3, 1);
			if(st.getQuestItemsCount(EssenceofWater) == 0)
				htmltext = "32507-01.htm";
			else
			{
				st.takeItems(SpritesSword, -1);
				st.takeItems(EssenceofWater, -1);
				st.giveItems(EnhancedSpritesSword, 1);
			}
			addSpawnToInstance(PAPION, new Location(-53903, 181484, -4555, 30456), 0, refId);
		}
		else if(event.equalsIgnoreCase("32507-07.htm"))
		{
			st.setCond(7);
			st.playSound(SOUND_MIDDLE);
			st.takeItems(TempleBookofSecrets5, -1);
			st.giveItems(TempleBookofSecrets6, 1);
			if(st.getQuestItemsCount(EssenceofFire) == 0)
				htmltext = "32507-04.htm";
			else
			{
				st.takeItems(EnhancedSpritesSword, -1);
				st.takeItems(EssenceofFire, -1);
				st.giveItems(SwordofIceandFire, 1);
			}
			addSpawnToInstance(GARGOS, new Location(-61354, 183624, -4821, 63613), 0, refId);
		}
		else if(event.equalsIgnoreCase("32510-02.htm"))
		{
			st.giveItems(PailakaRing, 1);
			st.giveItems(PailakaEarring, 1);
			st.giveItems(ScrollofEscape, 1);
			st.addExpAndSp(810000, 50000);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
			player.setVitality(20000);
			player.getReflection().startCollapseTimer(60000);
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
		if(npcId == ADLER)
		{
			if(cond == 0)
				if(player.getLevel() < 36 || player.getLevel() > 42)
				{
					htmltext = "32497-no.htm";
					st.exitCurrentQuest(true);
				}
				else
					return "32497-01.htm";
			else if(id == COMPLETED)
				htmltext = "32497-no.htm";
			else
				return "32497-05.htm";
		}
		else if(npcId == SINAI)
		{
			if(cond == 1)
				htmltext = "32500-01.htm";
			else
				htmltext = "32500-06.htm";
		}
		else if(npcId == ADLER2)
			if(cond == 9)
				htmltext = "32510-01.htm";
			else if(id == COMPLETED)
				htmltext = "32510-02.htm";
		return htmltext;
	}

	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		String htmltext = "noquest";
		QuestState st = player.getQuestState(getName());
		if(st == null || st.isCompleted())
			return htmltext;
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(npcId == TINSPECTOR)
			if(cond == 2)
				htmltext = "32507-01.htm";
			else if(cond == 3)
				htmltext = "32507-02.htm";
			else if(cond == 6)
				htmltext = "32507-05.htm";
			else
				htmltext = "32507-04.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int refId = player.getReflection().getId();
		if(contains(MOBS, npcId))
		{
			int herbRnd = Rnd.get(2);
			if(Rnd.get(100) < 50)
				dropItem(npc, HPHERBS[herbRnd], 1);
			if(Rnd.get(100) < 50)
				dropItem(npc, MPHERBS[herbRnd], 1);
		}
		else if(npcId == HILLAS && cond == 2)
		{
			st.takeItems(TempleBookofSecrets1, -1);
			st.giveItems(EssenceofWater, 1);
			st.giveItems(TempleBookofSecrets2, 1);
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}
		else if(npcId == PAPION && cond == 4)
		{
			st.takeItems(TempleBookofSecrets3, -1);
			st.giveItems(TempleBookofSecrets4, 1);
			st.setCond(5);
			st.playSound(SOUND_MIDDLE);
			addSpawnToInstance(KINSUS, new Location(-61404, 181351, -4815, 63953), 0, refId);
		}
		else if(npcId == KINSUS && cond == 5)
		{
			st.takeItems(TempleBookofSecrets4, -1);
			st.giveItems(EssenceofFire, 1);
			st.giveItems(TempleBookofSecrets5, 1);
			st.setCond(6);
			st.playSound(SOUND_MIDDLE);
		}
		else if(npcId == GARGOS && cond == 7)
		{
			st.takeItems(TempleBookofSecrets6, -1);
			st.giveItems(TempleBookofSecrets7, 1);
			st.setCond(8);
			st.playSound(SOUND_MIDDLE);
			addSpawnToInstance(ADIANTUM, new Location(-53297, 185027, -4617, 1512), 0, refId);
		}
		else if(npcId == ADIANTUM && cond == 8)
		{
			st.takeItems(TempleBookofSecrets7, -1);
			st.setCond(9);
			st.playSound(SOUND_MIDDLE);
			addSpawnToInstance(ADLER2, new Location(npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()), 0, refId);
		}
		else if(npcId == Bloom)
		{
			if(Rnd.chance(50))
				dropItem(npc, PailakaInstantShield, Rnd.get(1, 7));
			if(Rnd.chance(30))
				dropItem(npc, QuickHealingPotion, Rnd.get(1, 7));
		}
		else if(npcId == CrystalWaterBottle)
		{
			if(Rnd.chance(50))
				dropItem(npc, PailakaInstantShield, Rnd.get(1, 10));
			if(Rnd.chance(30))
				dropItem(npc, QuickHealingPotion, Rnd.get(1, 10));
			if(Rnd.chance(10))
				dropItem(npc, WaterAttributeEnhancer, Rnd.get(1, 5));
		}
		else if(npcId == BurningBrazier)
		{
			if(Rnd.chance(50))
				dropItem(npc, PailakaInstantShield, Rnd.get(1, 10));
			if(Rnd.chance(30))
				dropItem(npc, QuickHealingPotion, Rnd.get(1, 10));
			if(Rnd.chance(10))
				dropItem(npc, FireAttributeEnhancer, Rnd.get(1, 5));
		}
		return null;
	}

	private void enterInstance(L2Player player)
	{
		int instancedZoneId = 43;
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