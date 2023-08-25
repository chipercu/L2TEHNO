package l2open.gameserver.model.instances;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.ItemAuctionManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.ItemBroker.ItemAuction;
import l2open.gameserver.model.entity.ItemBroker.ItemAuctionInstance;
import l2open.gameserver.serverpackets.ExItemAuctionInfoPacket;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2NpcTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;


public final class L2ItemAuctionBrokerInstance extends L2NpcInstance
{
	private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	private ItemAuctionInstance _instance;

	public L2ItemAuctionBrokerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		String filename;
		if(player.getLang().equals("ru"))
			filename = val == 0 ? "data/html-ru/itemauction/itembroker.htm" : "data/html-ru/itemauction/itembroker-" + val + ".htm";
		else
			filename = val == 0 ? "data/html/itemauction/itembroker.htm" : "data/html/itemauction/itembroker-" + val + ".htm";

		player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		final String[] params = command.split(" ");
		if(params.length == 1)
			return;

		if(params[0].equals("auction"))
		{
			if(_instance == null)
			{
				_instance = ItemAuctionManager.getInstance().getManagerInstance(getTemplate().npcId);
				if(_instance == null)
					//_log.error("L2ItemAuctionBrokerInstance: Missing instance for: " + getTemplate().npcId);
					return;
			}

			if(params[1].equals("cancel"))
			{
				if(params.length == 3)
				{
					int auctionId = 0;

					try
					{
						auctionId = Integer.parseInt(params[2]);
					}
					catch(NumberFormatException e)
					{
						e.printStackTrace();
						return;
					}

					final ItemAuction auction = _instance.getAuction(auctionId);
					if(auction != null)
						auction.cancelBid(player);
					else
						player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU));
				}
				else
				{
					final ItemAuction[] auctions = _instance.getAuctionsByBidder(player.getObjectId());
					for(final ItemAuction auction : auctions)
						auction.cancelBid(player);
				}
			}
			else if(params[1].equals("show"))
			{
				final ItemAuction currentAuction = _instance.getCurrentAuction();
				final ItemAuction nextAuction = _instance.getNextAuction();

				if(currentAuction == null)
				{
					player.sendPacket(Msg.IT_IS_NOT_AN_AUCTION_PERIOD);

					if(nextAuction != null)
						player.sendMessage("The next auction will begin on the " + fmt.format(new Date(nextAuction.getStartingTime())) + ".");
					return;
				}

				if(!player.getAndSetLastItemAuctionRequest())
				{
					player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR));
					return;
				}

				player.sendPacket(new ExItemAuctionInfoPacket(false, currentAuction, nextAuction));
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}