package l2open.gameserver.model.instances;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.PledgeShowInfoUpdate;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2NpcTemplate;

public final class L2ClanTraderInstance extends L2NpcInstance
{
	public L2ClanTraderInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		NpcHtmlMessage html = new NpcHtmlMessage(player, this);

		if(command.equalsIgnoreCase("crp"))
		{
			if(player.getClan() != null && player.getClan().getLevel() > 4)
				html.setFile("./data/html/default/" + getNpcId() + "-2.htm");
			else
				html.setFile("./data/html/default/" + getNpcId() + "-1.htm");

			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
		}
		else if(command.startsWith("exchange"))
		{
			if(!player.isClanLeader())
			{
				html.setFile("./data/html/default/" + getNpcId() + "-no.htm");
				html.replace("%objectId%", String.valueOf(getObjectId()));
				player.sendPacket(html);
				return;
			}

			int itemId = Integer.parseInt(command.substring(9).trim());

			int reputation = 0;
			long itemCount = 0;

			switch(itemId)
			{
				case 9911: // Blood alliance
					reputation = 500;
					itemCount = 1;
					break;
				case 9910: // 10 Blood oath
					reputation = 200;
					itemCount = 10;
					break;
				case 9912: // 100 Knight's Epaulettes
					reputation = 20;
					itemCount = 100;
					break;
				default:
					reputation = itemId/5;
					itemCount = itemId;
					itemId = 9912;
					break;
			}

			L2ItemInstance item = player.getInventory().getItemByItemId(itemId);
			long playerItemCount = item == null ? 0 : item.getCount();

			if(playerItemCount >= itemCount)
			{
				player.getInventory().destroyItemByItemId(itemId, itemCount, true);
				player.getClan().incReputation(reputation, false, "ClanTrader " + itemId + " from " + player.getName());
				player.getClan().broadcastToOnlineMembers(new PledgeShowInfoUpdate(player.getClan()));
				player.sendPacket(new SystemMessage(SystemMessage.YOUR_CLAN_HAS_ADDED_1S_POINTS_TO_ITS_CLAN_REPUTATION_SCORE).addNumber(reputation));

				html.setFile("./data/html/default/" + getNpcId() + "-ExchangeSuccess.htm");
			}
			else
				html.setFile("./data/html/default/" + getNpcId() + "-ExchangeFailed.htm");

			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
		}
		else
			super.onBypassFeedback(player, command);
	}
}