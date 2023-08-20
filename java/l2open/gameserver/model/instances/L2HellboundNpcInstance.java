package l2open.gameserver.model.instances;

import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;

public class L2HellboundNpcInstance extends L2NpcInstance
{
	private static int Bernarde = 32300;
	private static int Budenka = 32294;
	private static int Buron = 32345;
	private static int Hude = 32298;
	private static int Falk = 32297;
	private static int Jude = 32356;
	private static int Kief = 32354;
	private static int Solomon = 32355;
	private static int NativesNpc = 32357;
	private static int Rignos = 32349;
	private static int HellboundTraitor = 32364;

	public L2HellboundNpcInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public void showChatWindow(L2Player player, int val)
	{
		if (player.getVar("life_points") == null)
		{
			player.setVar("life_points", "0");
		}
		if (player.getVar("checked") == null)
		{
			player.setVar("checked", "0");
		}
		String filename = "";
		String path = "/data/html/hellbound/" + getName().toLowerCase() + "/";
		int npcId = getNpcId();
		int hellboundLevel = HellboundManager.getInstance().getLevel();
		if (npcId == Bernarde)
		{
			if (player.getVar("bernarde") == null)
			{
				player.setVar("bernarde", "false");
			}
			if (hellboundLevel < 2)
				filename = path + "bernarde001.htm";
			else if (hellboundLevel == 2)
			{
				if (!player.isTransformed())
					filename = path + "bernarde001.htm";
				else if (player.getTransformation() != 101)
					filename = path + "bernarde001.htm";
				else
					filename = path + "bernarde002a.htm";
			}
			else if (hellboundLevel == 3)
			{
				if (!player.isTransformed())
					filename = path + "bernarde001a.htm";
				else if (player.getTransformation() != 101)
					filename = path + "bernarde001a.htm";
				else
					filename = path + "bernarde001c.htm";
			}
			if (hellboundLevel == 4)
			{
				if (!player.isTransformed())
					filename = path + "bernarde002.htm";
				else if (player.getTransformation() != 101)
					filename = path + "bernarde002.htm";
				else
					filename = path + "bernarde001d.htm";
			}
			else if (hellboundLevel == 5)
			{
				if (!player.isTransformed())
					filename = path + "bernarde001e.htm";
				else if (player.getTransformation() != 101)
					filename = path + "bernarde001e.htm";
				else
					filename = path + "bernarde001f.htm";
			}
			else if ((hellboundLevel >= 6) && (hellboundLevel < 9))
			{
				if (!player.isTransformed())
					filename = path + "bernarde003e.htm";
				else if (player.getTransformation() != 101)
					filename = path + "bernarde003e.htm";
				else
					filename = path + "bernarde003.htm";
			}
			else if (hellboundLevel == 9)
				filename = path + "bernarde003j.htm";
			else if (hellboundLevel == 10)
				filename = path + "bernarde003g.htm";
			else if (hellboundLevel == 11)
				filename = path + "bernarde003k.htm";
		}
		else if (npcId == Budenka)
		{
			int count1 = 0;
			int count2 = 0;
			Inventory inv = player.getInventory();
			L2ItemInstance[] items = inv.getItems();
			for (L2ItemInstance item : items)
			{
				if (item.getItemId() == 9851)
					count1 = (int)(count1 + item.getLongLimitedCount());
				else if (item.getItemId() == 9852)
					count2 = (int)(count2 + item.getLongLimitedCount());
			}
			filename = path + "caravan_budenka001.htm";

			if (count1 >= 1)
				filename = path + "caravan_budenka002.htm";
			if (count2 >= 1)
				filename = path + "caravan_budenka003.htm";
		}
		else if (npcId == Buron)
		{
			if (hellboundLevel < 2)
				filename = path + "buron001.htm";
			else if (hellboundLevel >= 2 && hellboundLevel < 5)
				filename = path + "buron002.htm";
			else if (hellboundLevel >= 5)
				filename = path + "buron001a.htm";
		}
		else if (npcId == Hude)
		{
			int count9850 = 0;
			int count9851 = 0;
			int count9852 = 0;
			Inventory inv = player.getInventory();
			L2ItemInstance[] items = inv.getItems();
			for (L2ItemInstance item : items)
			{
				if (item.getItemId() == 9850)
					count9850 = (int)(count9850 + item.getLongLimitedCount());
				else if (item.getItemId() == 9851)
					count9851 = (int)(count9851 + item.getLongLimitedCount());
				else if (item.getItemId() == 9852)
				{
					count9852 = (int)(count9852 + item.getLongLimitedCount());
				}
			}
			filename = path + "caravan_hude001.htm";

			if (hellboundLevel >= 4 && count9850 >= 1 && count9851 < 1)
			{
				filename = path + "caravan_hude003.htm";
			}
			if (hellboundLevel > 3 && hellboundLevel < 7 && count9851 >= 1)
			{
				filename = path + "caravan_hude002a.htm";
			}
			if (hellboundLevel >= 7 && count9851 >= 1)
			{
				filename = path + "caravan_hude005.htm";
			}
			if (hellboundLevel >= 7 && count9852 >= 1)
			{
				filename = path + "caravan_hude007.htm";
			}
		}
		else if (npcId == Falk)
		{
			filename = path + "falk001.htm";
		}
		else if (npcId == Jude)
		{
			if (player.getVar("jude") == null)
			{
				player.setVar("jude", "false");
			}
			if (hellboundLevel < 3)
				filename = path + "jude001.htm";
			else if (hellboundLevel == 3)
				filename = path + "jude001c.htm";
			else if (hellboundLevel == 4)
				filename = path + "jude001b.htm";
			else if (hellboundLevel >= 5)
				filename = path + "jude001a.htm";
		}
		else if (npcId == Kief)
		{
			if (hellboundLevel < 2)
				filename = path + "kief001.htm";
			else if (hellboundLevel == 2)
				filename = path + "kief001a.htm";
			else if (hellboundLevel == 3)
				filename = path + "kief001a.htm";
			else if (hellboundLevel == 4)
				filename = path + "kief001e.htm";
			else if (hellboundLevel == 5)
				filename = path + "kief001d.htm";
			else if (hellboundLevel == 6)
				filename = path + "kief001b.htm";
			else if (hellboundLevel == 7)
				filename = path + "kief001c.htm";
			else if (hellboundLevel >= 8)
				filename = path + "kief001f.htm";
		}
		else if (npcId == Solomon)
		{
			if(hellboundLevel == 5)
				filename = path + "solmon001.htm";
			else if(hellboundLevel > 5)
				filename = path + "solmon001a.htm";
		}
		else if (npcId == NativesNpc)
		{
			if (hellboundLevel < 9)
				filename = path + "incastle_native001a.htm";
			else if (hellboundLevel == 9)
				filename = path + "incastle_native001.htm";
			else if (hellboundLevel > 9)
				filename = path + "incastle_native001b.htm";
		}
		else if (npcId == Rignos)
		{
			int count = 0;
			String var = player.getVar("RaceStarted");
			Inventory inv = player.getInventory();
			L2ItemInstance[] items = inv.getItems();
			for (L2ItemInstance item : items)
			{
				if (item.getItemId() == 9850)
				{
					count = (int)(count + item.getLongLimitedCount());
				}
			}
			if (var != null)
			{
				if (player.getEffectList().getEffectsBySkillId(5239) != null)
				{
					if (count < 4)
						filename = path + getNpcId() + "-3.htm";
					else
						filename = path + getNpcId() + "-4.htm";
				}
				else
				{
					player.unsetVar("RaceStarted");
					filename = path + getNpcId() + "-6.htm";
				}
			}
			else
				filename = path + getNpcId() + ".htm";
		}
		else if (npcId == HellboundTraitor && hellboundLevel >= 5 && hellboundLevel <= 6)
		{
			filename = path + "32364.htm";
		}
		player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
	}

	// Не отображаем значки клана на НПЦ ХБ.
	public boolean isCrestEnable()
	{
		return false;
	}
}