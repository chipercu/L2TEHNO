package events.MasterOfEnchanting;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.RateService;
import l2open.util.Rnd;

/**
 * Master Of Enchanting Event
 * Official website description event: http://www.lineage2.com/archive/2009/06/master_of_encha.html
 * Development by L2Phoenix
 * Last modify: 14.06.2010, Magister
 */

public class MasterOfEnchanting extends Functions implements ScriptFile
{
	private static final int MasterYogi = 32599;

	private static final int MasterYogiStaff = 13539;
	private static final int MasterYogiScroll = 13540;

	// Реюз на выдачу 24х скролов, в часах
	private static final int Scroll_24_reuse_time = 6;

	// Reward's
	private static final int Firework = 6406;
	private static final int LargeFirework = 6407;
	private static final int ScrollEnchantWeaponD = 955;
	private static final int ScrollEnchantArmorD = 956;
	private static final int ScrollEnchantWeaponC = 951;
	private static final int ScrollEnchantArmorC = 952;
	private static final int ScrollEnchantArmorB = 948;
	private static final int ScrollEnchantWeaponA = 729;
	private static final int SAccessoryChest = 13992;
	private static final int TopLifeStone76 = 8762;
	private static final int ScrollEnchantWeaponS = 959;
	private static final int SArmorChest = 13991;
	private static final int SWeaponChest = 13990;
	private static final int HighLifeStone76 = 8752;
	private static final int S80ArmorChest = 13989;
	private static final int S80WeaponChest = 13988;

	// Shadow Item - Top Hat
	// Shadow Item - Black Mask
	// Shadow Item - Rider Goggles
	private static final int[] HatShadowReward = { 13074, 13075, 13076 };

	// Event - Top Hat
	// Event - Black Mask
	// Event - Rider Goggles
	private static final int[] HatEventReward = { 13518, 13519, 13522 };
	private static final int[] CrystalReward = { 9570, 9571, 9572 };

	private static GArray<L2Spawn> _spawns = new GArray<L2Spawn>();
	private static boolean _active = false;

	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: MasterOfEnchanting [state: activated]");
		}
		else
			_log.info("Loaded Event: MasterOfEnchanting [state: deactivated]");

		ConfigValue.EnchMasterStaffPrice = (long) (ConfigValue.EnchMasterUseAdenaRates ? ConfigValue.EnchMasterStaffPrice * ConfigValue.RateDropAdena : ConfigValue.EnchMasterStaffPrice);
		ConfigValue.EnchMaster24ScrollPrice = (long) (ConfigValue.EnchMasterUseAdenaRates ? ConfigValue.EnchMaster24ScrollPrice * ConfigValue.RateDropAdena : ConfigValue.EnchMaster24ScrollPrice);
		ConfigValue.EnchMaster1ScrollPrice = (long) (ConfigValue.EnchMasterUseAdenaRates ? ConfigValue.EnchMaster1ScrollPrice * ConfigValue.RateDropAdena : ConfigValue.EnchMaster1ScrollPrice);


	}

	/**
	 * Start Event
	 */
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("MasterOfEnchanting", true))
		{
			spawnEventManagers();
			_log.info("Event: 'MasterOfEnchanting' started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.MasterOfEnchanting.MasterOfEnchanting.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event 'MasterOfEnchanting' already started.");

		_active = true;
		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	 * Stop Event
	 */
	public void stopEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("MasterOfEnchanting", false))
		{
			unSpawnEventManagers();
			_log.info("Event 'MasterOfEnchanting' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.MasterOfEnchanting.MasterOfEnchanting.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event 'MasterOfEnchanting' not started.");

		_active = false;
		show(Files.read("data/html/admin/events.htm", player), player);
	}

	public void onReload()
	{
		unSpawnEventManagers();
	}

	public void onShutdown()
	{
		unSpawnEventManagers();
	}

	/**
	 * Event Drop
	 */
	public static void OnDie(L2Character mob, L2Character killer)
	{
		if(_active && SimpleCheckDrop(mob, killer) && Rnd.get(1000) <= ConfigValue.EnchMasterDropChance * killer.getPlayer().getRateItems() * RateService.getRateDropItems(killer.getPlayer()) * ((L2NpcInstance) mob).getTemplate().rateHp)
			((L2NpcInstance) mob).dropItem(killer.getPlayer(), MasterYogiScroll, 1);
	}

	public static void OnPlayerEnter(L2Player player)
	{
		if(_active)
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.MasterOfEnchanting.MasterOfEnchanting.AnnounceEventStarted", null);
	}

	/**
	 * Base event statuc.
	 */
	private static boolean isActive()
	{
		return IsActive("MasterOfEnchanting");
	}

	/**
	 * Spawn event manager
	 */
	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] = { { 19560, 145448, -3104, 30419 }, // Dion
                { 148024, -57432, -2807, 9138 }, // Goddard
                { 110456, 220168, -3624, 63079 }, // Heine
                { -81640, 150216, -3155, 42910 }, // Gludin
                { 147096, 28552, -2264, 59402 }, // Aden
                { 44296, -48392, -823, 23331 }, // Rune
                { -13448, 122056, -3015, 40099 }, // Gludio
                { 116312, 75464, -2712, 12022 }, // Hellbound
                { 82680, 54152, -1522, 58708 }, // Oren
                { 82088, 148376, -3464, 900 }, // Giran
                { 87736, -141976, -1368, 57344 } // Schuttgart
		};

		SpawnNPCs(MasterYogi, EVENT_MANAGERS, _spawns);
	}

	/**
	 * Delete spawn manager
	 */
	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}

	public void show(String var[])
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		NpcHtmlMessage html = new NpcHtmlMessage(player, npc);
		html.setFile("data/scripts/events/MasterOfEnchanting/" + var[0]);
		html.replace("%StaffPrice%", String.valueOf(ConfigValue.EnchMasterStaffPrice));
		html.replace("%24ScrollPrice%", String.valueOf(ConfigValue.EnchMaster24ScrollPrice));
		html.replace("%1ScrollPrice%", String.valueOf(ConfigValue.EnchMaster1ScrollPrice));
		player.sendPacket(html);
	}

	public void process(String var[])
	{
		L2Player player = (L2Player) getSelf();
		String htmltext = null;
		if(var[0].equalsIgnoreCase("buy_staff"))
		{
			if(getItemCount(player, MasterYogiStaff) == 0 && getItemCount(player, ConfigValue.PriceEnchantMasterId) > ConfigValue.EnchMasterStaffPrice)
			{
				removeItem(player, ConfigValue.PriceEnchantMasterId, ConfigValue.EnchMasterStaffPrice);
				addItem(player, MasterYogiStaff, 1);
				htmltext = "32599-staffbuyed.htm";
			}
			else
				htmltext = "32599-staffcant.htm";
		}
		else if(var[0].equalsIgnoreCase("buy_scroll_24"))
		{
			long curr_time = System.currentTimeMillis();
			String value = player.getVar("MOfE"+player.getAccountName());
			long reuse_time = value == null ? 0 : Long.parseLong(value);
			if(curr_time > reuse_time)
			{
				if(getItemCount(player, ConfigValue.PriceEnchantMasterId) > ConfigValue.EnchMaster24ScrollPrice)
				{
					removeItem(player, ConfigValue.PriceEnchantMasterId, ConfigValue.EnchMaster24ScrollPrice);
					addItem(player, MasterYogiScroll, 24);
					player.setVar("MOfE"+player.getAccountName(), Long.toString(curr_time + Scroll_24_reuse_time * 3600000));
					htmltext = "32599-scroll24.htm";
				}
				else
					htmltext = "32599-s24-no.htm";
			}
			else
			{
				long remaining_time = (reuse_time - curr_time) / 1000;
				int hours = (int) remaining_time / 3600;
				int minutes = ((int) remaining_time % 3600) / 60;
				if(hours > 0)
				{
					player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(hours).addNumber(minutes));
					htmltext = "32599-scroll24.htm";
				}
				else if(minutes >= 0)
				{
					player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(minutes));
					htmltext = "32599-scroll24.htm";
				}
				else
				{
					if(getItemCount(player, ConfigValue.PriceEnchantMasterId) > ConfigValue.EnchMaster24ScrollPrice)
					{
						removeItem(player, ConfigValue.PriceEnchantMasterId, ConfigValue.EnchMaster24ScrollPrice);
						addItem(player, MasterYogiScroll, 24);
						player.setVar("MOfE"+player.getAccountName(), Long.toString(curr_time + Scroll_24_reuse_time * 3600000));
						htmltext = "32599-scroll24.htm";
					}
					else
						htmltext = "32599-s24-no.htm";
				}
			}
		}
		else if(var[0].equalsIgnoreCase("buy_scroll_1"))
		{
			if(getItemCount(player, ConfigValue.PriceEnchantMasterId) > ConfigValue.EnchMaster1ScrollPrice)
			{
				removeItem(player, ConfigValue.PriceEnchantMasterId, ConfigValue.EnchMaster1ScrollPrice);
				addItem(player, MasterYogiScroll, 1);
				htmltext = "32599-scroll-ok.htm";
			}
			else
				htmltext = "32599-s1-no.htm";
		}
		else if(var[0].equalsIgnoreCase("buy_scroll_10_9"))
		{
			if(getItemCount(player, ConfigValue.PriceEnchantMasterId) > ConfigValue.EnchMaster1ScrollPrice*9)
			{
				removeItem(player, ConfigValue.PriceEnchantMasterId, ConfigValue.EnchMaster1ScrollPrice*9);
				addItem(player, MasterYogiScroll, 10);
				htmltext = "32599-scroll-ok.htm";
			}
			else
				htmltext = "32599-s1-no.htm";
		}
		else if(var[0].equalsIgnoreCase("receive_reward"))
		{
			L2ItemInstance staff = player.getActiveWeaponInstance();
			if(staff != null && staff.getItemId() == MasterYogiStaff && staff.getEnchantLevel() > 3)
			{
				switch(staff.getEnchantLevel())
				{
					case 4:
						addItem(player, Firework, 1);
						break;
					case 5:
						addItem(player, Firework, 2);
						addItem(player, LargeFirework, 1);
						break;
					case 6:
						addItem(player, Firework, 3);
						addItem(player, LargeFirework, 2);
						break;
					case 7:
						addItem(player, HatShadowReward[Rnd.get(3)], 1);
						break;
					case 8:
						addItem(player, ScrollEnchantWeaponD, 1);
						break;
					case 9:
						addItem(player, ScrollEnchantWeaponD, 1);
						addItem(player, ScrollEnchantArmorD, 1);
						break;
					case 10:
						addItem(player, ScrollEnchantWeaponC, 1);
						break;
					case 11:
						addItem(player, ScrollEnchantWeaponC, 1);
						addItem(player, ScrollEnchantArmorC, 1);
						break;
					case 12:
						addItem(player, ScrollEnchantArmorB, 1);
						break;
					case 13:
						addItem(player, ScrollEnchantWeaponA, 1);
						break;
					case 14:
						addItem(player, HatEventReward[Rnd.get(3)], 1);
						break;
					case 15:
						addItem(player, SAccessoryChest, 1);
						break;
					case 16:
						addItem(player, TopLifeStone76, 1);
						break;
					case 17:
						addItem(player, ScrollEnchantWeaponS, 1);
						break;
					case 18:
						addItem(player, SArmorChest, 1);
						break;
					case 19:
						addItem(player, SWeaponChest, 1);
						break;
					case 20:
						addItem(player, CrystalReward[Rnd.get(3)], 1); // Red/Blue/Green Soul Crystal - Stage 14
						break;
					case 21:
						addItem(player, TopLifeStone76, 1);
						addItem(player, HighLifeStone76, 1);
						addItem(player, CrystalReward[Rnd.get(3)], 1); // Red/Blue/Green Soul Crystal - Stage 14
						break;
					case 22:
						addItem(player, S80ArmorChest, 1);
						break;
					default:
						if(staff.getEnchantLevel() > 22)
							addItem(player, S80WeaponChest, 1);
						break;
				}
				if(staff.isEquipped())
					player.getInventory().unEquipItemInSlot(staff.getEquipSlot());

				L2ItemInstance destroyedItem = player.getInventory().destroyItem(staff.getObjectId(), 1, true);
				if(destroyedItem == null)
				{
					_log.info("ERROR: Failed to destroy " + staff.getObjectId() + " after reciving reward in event MasterOfEnchanting by " + player.getName());
					player.sendActionFailed();
				}

				htmltext = "32599-rewardok.htm";
			}
			else
				htmltext = "32599-rewardnostaff.htm";
		}
		if(htmltext != null)
			show(Files.read(String.format("data/scripts/events/MasterOfEnchanting/%s", htmltext), player), player);
	}

	public String DialogAppend_32599(Integer val)
	{
		if(val != 0)
			return "";

		L2Player player = (L2Player) getSelf();
		return Files.read("data/scripts/events/MasterOfEnchanting/32599.htm", player);
	}
}