package l2open.gameserver.model.instances;

import java.util.StringTokenizer;
import l2open.config.*;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.serverpackets.ExShowDominionRegistry;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;

public class L2MercenaryCaptainInstance extends L2NpcInstance
{
	public L2MercenaryCaptainInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		if(val == 0)
			return "./data/html/MercenaryCaptain/MercenaryCaptain.htm";
		return "./data/html/MercenaryCaptain/MercenaryCaptain-" + val + ".htm";
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		StringTokenizer st = new StringTokenizer(command, " ");
		String str = st.nextToken();
		int territoryId = getNpcId() - 36480;
		if(territoryId > 9 || territoryId < 1)
			return;
		int itemId = 13756 + territoryId;
		if(command.equalsIgnoreCase("Territory"))
			player.sendPacket(new ExShowDominionRegistry(player, territoryId));
		else
		{
			if(command.equalsIgnoreCase("Buy"))
			{
				String multiselId = getNpcId() + "0001";
				L2Multisell.getInstance().SeparateAndSend(Integer.parseInt(multiselId), player, getCastle().getTaxRate());
			}
			else if(command.startsWith("certificate_multisell"))
			{
				StringTokenizer tokenizer = new StringTokenizer(command);
				tokenizer.nextToken();
				int certification = Integer.parseInt(tokenizer.nextToken());
				int multisell = Integer.parseInt(tokenizer.nextToken());

				if(player.getInventory().getCountOf(certification) > 0)
					L2Multisell.getInstance().SeparateAndSend(multisell, player, getCastle().getTaxRate());
				else
					showChatWindow(player, 5);
			}
			else if(command.equalsIgnoreCase("Strider"))
			{
				NpcHtmlMessage nhm = new NpcHtmlMessage(player, this, getHtmlPath(getNpcId(), 3), 5);
				nhm.replace("%striderBadge%", String.valueOf(ConfigValue.MinTerritoryBadgeForStriders));
				nhm.replace("%gstriderBadge%", String.valueOf(ConfigValue.MinTerritoryBadgeForBigStrider));
				player.sendPacket(nhm);
			}
			else if(str.equalsIgnoreCase("TW_Buy"))
			{
				if(st.countTokens() < 1 && !player.isGM())
					return;
				int count = Integer.parseInt(st.nextToken());
				int val = Integer.parseInt(st.nextToken());
				if(player.isGM())
					player.sendMessage("badgeId: " + itemId + ", type: " + val);
				if(player.getInventory().getItemByItemId(itemId) != null)
				{
					if(count <= player.getInventory().getCountOf(itemId))
					{
						int itemIds = 0;
						switch(val)
						{
							case 1:
								itemIds = 4422;
								break;
							case 2:
								itemIds = 4423;
								break;
							case 3:
								itemIds = 4424;
								break;
							case 4:
								itemIds = 14819;
								break;
							default:
								return;
						}
						player.getInventory().destroyItemByItemId(itemId, count, true);
						player.getInventory().addItem(itemIds, 1);
						return;
					}
				}
				showChatWindow(player, 6);
			}
			else
				super.onBypassFeedback(player, command);
		}
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		if(player.getLevel() < 40 || player.getClassId().getLevel() < 3)
			val = 2;
		else if(TerritorySiege.isInProgress())
			val = 10;
		String str = "";
		if(val == 0)
			str = "./data/html/MercenaryCaptain/MercenaryCaptain.htm";
		else
			str = "./data/html/MercenaryCaptain/MercenaryCaptain-" + val + ".htm";
		player.sendPacket(new NpcHtmlMessage(player, this, str, val));
	}
}