package quests._10295_SevenSignsSolinasTomb;

import org.apache.commons.lang3.ArrayUtils;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.EventTrigger;
import l2open.gameserver.serverpackets.ExStartScenePlayer;
import l2open.util.Location;

/**
 * @author pchayka
 *         <p/>
 *         TODO: спавн минионов
 *         TODO: включение и отключение свечения у Контроллеров Границ
 */
public class _10295_SevenSignsSolinasTomb extends Quest implements ScriptFile
{
	private static final int ErisEvilThoughts = 32792;
	private static final int ElcardiaInzone1 = 32787;
	private static final int TeleportControlDevice = 32820;
	private static final int PowerfulDeviceStaff = 32838;
	private static final int PowerfulDeviceBook = 32839;
	private static final int PowerfulDeviceSword = 32840;
	private static final int PowerfulDeviceShield = 32841;
	private static final int AltarofHallowsStaff = 32857;
	private static final int AltarofHallowsSword = 32858;
	private static final int AltarofHallowsBook = 32859;
	private static final int AltarofHallowsShield = 32860;

	private static final int TeleportControlDevice2 = 32837;
	private static final int TeleportControlDevice3 = 32842;
	private static final int TomboftheSaintess = 32843;

	private static final int ScrollofAbstinence = 17228;
	private static final int ShieldofSacrifice = 17229;
	private static final int SwordofHolySpirit = 17230;
	private static final int StaffofBlessing = 17231;

	private static final int Solina = 32793;

	private static final int[] SolinaGuardians = {18952, 18953, 18954, 18955};
	private static final int[] TombGuardians = {18956, 18957, 18958, 18959};

	private L2NpcInstance[] tombguards1 = new L2NpcInstance[4];
	private L2NpcInstance[] tombguards2 = new L2NpcInstance[4];
	private L2NpcInstance[] tombguards3 = new L2NpcInstance[4];
	private L2NpcInstance[] tombguards4 = new L2NpcInstance[4];

	static
	{
		Location[] minions1 = {new Location(55672, -252120, -6760), new Location(55752, -252120, -6760), new Location(55656, -252216, -6760), new Location(55736, -252216, -6760)};
		Location[] minions2 = {new Location(55672, -252728, -6760), new Location(55752, -252840, -6760), new Location(55768, -252840, -6760), new Location(55752, -252712, -6760)};
		Location[] minions3 = {new Location(56504, -252840, -6760), new Location(56504, -252728, -6760), new Location(56392, -252728, -6760), new Location(56408, -252840, -6760)};
		Location[] minions4 = {new Location(56520, -252232, -6760), new Location(56520, -252104, -6760), new Location(56424, -252104, -6760), new Location(56440, -252216, -6760)};
	}

	public _10295_SevenSignsSolinasTomb()
	{
		super(false);
		addStartNpc(ErisEvilThoughts);
		addTalkId(ElcardiaInzone1, TeleportControlDevice, PowerfulDeviceStaff, PowerfulDeviceBook, PowerfulDeviceSword, PowerfulDeviceShield);
		addTalkId(AltarofHallowsStaff, AltarofHallowsSword, AltarofHallowsBook, AltarofHallowsShield);
		addTalkId(TeleportControlDevice2, TomboftheSaintess, TeleportControlDevice3, Solina);
		addQuestItem(ScrollofAbstinence, ShieldofSacrifice, SwordofHolySpirit, StaffofBlessing);
		addKillId(SolinaGuardians);
		addKillId(TombGuardians);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		L2Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("eris_q10295_5.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("teleport_in"))
		{
			player.teleToLocation(new Location(45512, -249832, -6760));
			teleportElcardia(player);
			return null;
		}
		else if(event.equalsIgnoreCase("teleport_out"))
		{
			player.teleToLocation(new Location(120664, -86968, -3392));
			teleportElcardia(player);
			return null;
		}
		else if(event.equalsIgnoreCase("use_staff"))
		{
			if(st.getQuestItemsCount(StaffofBlessing) > 0)
			{
				st.takeAllItems(StaffofBlessing);
				// TODO: remove glow from NPC
				removeInvincibility(player, 18953);
				return null;
			}
			else
				htmltext = "powerful_q10295_0.htm";
		}
		else if(event.equalsIgnoreCase("use_book"))
		{
			if(st.getQuestItemsCount(ScrollofAbstinence) > 0)
			{
				st.takeAllItems(ScrollofAbstinence);
				// TODO: remove glow from NPC
				removeInvincibility(player, 18954);
				return null;
			}
			else
				htmltext = "powerful_q10295_0.htm";
		}
		else if(event.equalsIgnoreCase("use_sword"))
		{
			if(st.getQuestItemsCount(SwordofHolySpirit) > 0)
			{
				st.takeAllItems(SwordofHolySpirit);
				// TODO: remove glow from NPC
				removeInvincibility(player, 18955);
				return null;
			}
			else
				htmltext = "powerful_q10295_0.htm";
		}
		else if(event.equalsIgnoreCase("use_shield"))
		{
			if(st.getQuestItemsCount(ShieldofSacrifice) > 0)
			{
				st.takeAllItems(ShieldofSacrifice);
				// TODO: remove glow from NPC
				removeInvincibility(player, 18952);
				return null;
			}
			else
				htmltext = "powerful_q10295_0.htm";
		}
		else if(event.equalsIgnoreCase("altarstaff_q10295_2.htm"))
		{
			if(st.getQuestItemsCount(StaffofBlessing) == 0)
				st.giveItems(StaffofBlessing, 1);
			else
				htmltext = "atlar_q10295_0.htm";
		}
		else if(event.equalsIgnoreCase("altarbook_q10295_2.htm"))
		{
			if(st.getQuestItemsCount(ScrollofAbstinence) == 0)
				st.giveItems(ScrollofAbstinence, 1);
			else
				htmltext = "atlar_q10295_0.htm";
		}
		else if(event.equalsIgnoreCase("altarsword_q10295_2.htm"))
		{
			if(st.getQuestItemsCount(SwordofHolySpirit) == 0)
				st.giveItems(SwordofHolySpirit, 1);
			else
				htmltext = "atlar_q10295_0.htm";
		}
		else if(event.equalsIgnoreCase("altarshield_q10295_2.htm"))
		{
			if(st.getQuestItemsCount(ShieldofSacrifice) == 0)
				st.giveItems(ShieldofSacrifice, 1);
			else
				htmltext = "atlar_q10295_0.htm";
		}
		else if(event.equalsIgnoreCase("teleport_solina"))
		{
			player.teleToLocation(new Location(56033, -252944, -6760));
			teleportElcardia(player);
			return null;
		}
		else if(event.equalsIgnoreCase("tombsaintess_q10295_2.htm"))
		{
			
			if(!getDoor(player))
				activateTombGuards(player);
			else
				htmltext = "tombsaintess_q10295_3.htm";
		}
		else if(event.equalsIgnoreCase("teleport_realtomb"))
		{
			player.teleToLocation(new Location(56081, -250391, -6760));
			teleportElcardia(player);
			player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_ELYSS_NARRATION);
			return null;
		}
		else if(event.equalsIgnoreCase("solina_q10295_4.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("solina_q10295_8.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}
		return htmltext;
	}

	private boolean getDoor(L2Player player)
	{
		for(L2DoorInstance door : player.getReflection().getDoors())
			if(door.getDoorId() == 21100101)
				if(door.isOpen())
					return true;
		return false;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		L2Player player = st.getPlayer();
		if(player.isSubClassActive())
			return "no_subclass_allowed.htm";
		if(npcId == ErisEvilThoughts)
		{
			if(cond == 0)
			{
				QuestState qs = player.getQuestState("_10294_SevenSignsMonasteryofSilence");
				if(player.getLevel() >= 81 && qs != null && qs.isCompleted())
					htmltext = "eris_q10295_1.htm";
				else
				{
					htmltext = "eris_q10295_0a.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1)
				htmltext = "eris_q10295_6.htm";
			else if(cond == 2)
				htmltext = "eris_q10295_7.htm";
			else if(cond == 3)
			{
				if(player.getLevel() >= 81)
				{
					htmltext = "eris_q10295_8.htm";
					st.addExpAndSp(125000000, 12500000);
					st.setState(COMPLETED);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(false);
				}
				else
					htmltext = "eris_q10295_0.htm";
			}
		}
		else if(npcId == ElcardiaInzone1)
		{
			htmltext = "elcardia_q10295_1.htm";
		}
		else if(npcId == TeleportControlDevice)
		{
			if(!checkGuardians(player, SolinaGuardians))
				htmltext = "teleport_device_q10295_1.htm";
			else
				htmltext = "teleport_device_q10295_2.htm";

		}
		else if(npcId == PowerfulDeviceStaff)
		{
			htmltext = "powerfulstaff_q10295_1.htm";
		}
		else if(npcId == PowerfulDeviceBook)
		{
			htmltext = "powerfulbook_q10295_1.htm";
		}
		else if(npcId == PowerfulDeviceSword)
		{
			htmltext = "powerfulsword_q10295_1.htm";
		}
		else if(npcId == PowerfulDeviceShield)
		{
			htmltext = "powerfulsheild_q10295_1.htm";
		}
		else if(npcId == AltarofHallowsStaff)
		{
			htmltext = "altarstaff_q10295_1.htm";
		}
		else if(npcId == AltarofHallowsSword)
		{
			htmltext = "altarsword_q10295_1.htm";
		}
		else if(npcId == AltarofHallowsBook)
		{
			htmltext = "altarbook_q10295_1.htm";
		}
		else if(npcId == AltarofHallowsShield)
		{
			htmltext = "altarshield_q10295_1.htm";
		}
		else if(npcId == TeleportControlDevice2)
		{
			htmltext = "teleportdevice2_q10295_1.htm";
		}
		else if(npcId == TomboftheSaintess)
		{
			htmltext = "tombsaintess_q10295_1.htm";
		}
		else if(npcId == TeleportControlDevice3)
		{
			htmltext = "teleportdevice3_q10295_1.htm";
		}
		else if(npcId == Solina)
		{
			if(cond == 1)
				htmltext = "solina_q10295_1.htm";
			else if(cond == 2)
				htmltext = "solina_q10295_4.htm";
			else if(cond == 3)
				htmltext = "solina_q10295_8.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		L2Player player = st.getPlayer();
		if(ArrayUtils.contains(SolinaGuardians, npcId) && checkGuardians(player, SolinaGuardians))
		{
			player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_SOLINA_TOMB_CLOSING);
			player.broadcastPacket(new EventTrigger(21100100, false));
			player.broadcastPacket(new EventTrigger(21100102, true));
		}
		if(ArrayUtils.contains(TombGuardians, npcId))
		{
			if(checkGuardians(player, TombGuardians))
				player.getReflection().openDoor(21100018);
			switch(npcId)
			{
				case 18956:
					for(L2NpcInstance npcs : tombguards3)
						if(npcs != null)
							npcs.deleteMe();
					break;
				case 18957:
					for(L2NpcInstance npcs : tombguards2)
						if(npcs != null)
							npcs.deleteMe();
					break;
				case 18958:
					for(L2NpcInstance npcs : tombguards1)
						if(npcs != null)
							npcs.deleteMe();
					break;
				case 18959:
					for(L2NpcInstance npcs : tombguards4)
						if(npcs != null)
							npcs.deleteMe();
					break;
			}
		}
		return null;
	}

	private void teleportElcardia(L2Player player)
	{
		L2NpcInstance n = player.getReflection().findFirstNPC(ElcardiaInzone1);
		n.teleToLocation(Location.findPointToStay(player, 100));
	}

	private void removeInvincibility(L2Player player, int mobId)
	{
		for(L2NpcInstance n : player.getReflection().getMonsters())
			if(n.getNpcId() == mobId)
			{
				n.i_ai0=0;
				for(L2Effect e : n.getEffectList().getAllEffects())
					if(e.getSkill().getId() == 6371)
						e.exit(true, false);
			}
	}

	private boolean checkGuardians(L2Player player, int[] npcIds)
	{
		for(L2NpcInstance n : player.getReflection().getMonsters())
			if(ArrayUtils.contains(npcIds, n.getNpcId()) && !n.isDead())
				return false;
		return true;
	}

	private void activateTombGuards(L2Player player)
	{
		Reflection r = player.getReflection();
		if(r == null || r.getId() == 0)
			return;

		r.openDoor(21100101);
		r.openDoor(21100102);
		r.openDoor(21100103);
		r.openDoor(21100104);

		tombguards1[0] = addSpawnToInstance(new Location(55672, -252120, -6760), 27403, r.getId(), 5);
		tombguards1[1] = addSpawnToInstance(new Location(55752, -252120, -6760), 27403, r.getId(), 5);
		tombguards1[2] = addSpawnToInstance(new Location(55656, -252216, -6760), 27403, r.getId(), 5);
		tombguards1[3] = addSpawnToInstance(new Location(55736, -252216, -6760), 27403, r.getId(), 5); // tombguards1

		tombguards2[0] = addSpawnToInstance(new Location(55672, -252728, -6760), 27403, r.getId(), 5);
		tombguards2[1] = addSpawnToInstance(new Location(55752, -252840, -6760), 27403, r.getId(), 5);
		tombguards2[2] = addSpawnToInstance(new Location(55768, -252840, -6760), 27403, r.getId(), 5);
		tombguards2[3] = addSpawnToInstance(new Location(55752, -252712, -6760), 27403, r.getId(), 5); // tombguards2

		tombguards3[0] = addSpawnToInstance(new Location(56504, -252840, -6760), 27404, r.getId(), 5);
		tombguards3[1] = addSpawnToInstance(new Location(56504, -252728, -6760), 27404, r.getId(), 5);
		tombguards3[2] = addSpawnToInstance(new Location(56392, -252728, -6760), 27404, r.getId(), 5);
		tombguards3[3] = addSpawnToInstance(new Location(56408, -252840, -6760), 27404, r.getId(), 5); // tombguards3

		tombguards4[0] = addSpawnToInstance(new Location(56520, -252232, -6760), 27404, r.getId(), 5);
		tombguards4[1] = addSpawnToInstance(new Location(56520, -252104, -6760), 27404, r.getId(), 5);
		tombguards4[2] = addSpawnToInstance(new Location(56424, -252104, -6760), 27404, r.getId(), 5);
		tombguards4[3] = addSpawnToInstance(new Location(56440, -252216, -6760), 27404, r.getId(), 5); // tombguards4
	}


	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}