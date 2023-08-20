package items;

import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.MercTicketManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2CastleTeleporterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;

public class MercTicket implements IItemHandler, ScriptFile
{
	// all the items ids that this handler knowns
	private static final int[] _itemIds = MercTicketManager.getInstance().getItemIds();

	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;

		int itemId = item.getItemId();
		Residence castle = CastleManager.getInstance().getCastleByObject(player);

		if(castle == null)
			return;

		int castleId = castle.getId();

		// add check that certain tickets can only be placed in certain castles
		if(MercTicketManager.getInstance().getTicketCastleId(itemId) != castleId)
			switch(castleId)
			{
				case 1:
					player.sendMessage(new CustomMessage("scripts.items.MercTicket.TicketOnlyIn", player).addString("Gludio"));
					return;
				case 2:
					player.sendMessage(new CustomMessage("scripts.items.MercTicket.TicketOnlyIn", player).addString("Dion"));
					return;
				case 3:
					player.sendMessage(new CustomMessage("scripts.items.MercTicket.TicketOnlyIn", player).addString("Giran"));
					return;
				case 4:
					player.sendMessage(new CustomMessage("scripts.items.MercTicket.TicketOnlyIn", player).addString("Oren"));
					return;
				case 5:
					player.sendMessage(new CustomMessage("scripts.items.MercTicket.TicketOnlyIn", player).addString("Aden"));
					return;
				case 6:
					player.sendMessage(new CustomMessage("scripts.items.MercTicket.TicketOnlyIn", player).addString("Innadril"));
					return;
				case 7:
					player.sendMessage(new CustomMessage("scripts.items.MercTicket.TicketOnlyIn", player).addString("Goddard"));
					return;
				case 8:
					player.sendMessage(new CustomMessage("scripts.items.MercTicket.TicketOnlyIn", player).addString("Rune"));
					return;
				case 9:
					player.sendMessage(new CustomMessage("scripts.items.MercTicket.TicketOnlyIn", player).addString("Schuttgart"));
					return;
				default:
					// player is not in a castle
					player.sendMessage(new CustomMessage("scripts.items.MercTicket.TicketOnly", player));
					return;
			}

		if((player.getClanPrivileges() & L2Clan.CP_CS_MERCENARIES) != L2Clan.CP_CS_MERCENARIES)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_POSITION_MERCENARIES);
			player.sendActionFailed();
			return;
		}

		if(castle.getSiege().isInProgress() || TerritorySiege.isInProgress())
		{
			player.sendPacket(Msg.A_MERCENARY_CAN_BE_ASSIGNED_TO_A_POSITION_FROM_THE_BEGINNING_OF_THE_SEAL_VALIDATION_PERIOD_UNTIL_THE_TIME_WHEN_A_SIEGE_STARTS);
			player.sendActionFailed();
			return;
		}

		if(MercTicketManager.getInstance().isAtCasleLimit(item.getItemId()))
		{
			player.sendMessage(new CustomMessage("scripts.items.MercTicket.NoMore", player));
			return;
		}

		if(MercTicketManager.getInstance().isAtTypeLimit(item.getItemId()))
		{
			player.sendPacket(Msg.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE);
			player.sendActionFailed();
			return;
		}
		if(player.isActionBlocked("drop_merchant_guard"))
		{
			player.sendPacket(Msg.YOU_CANNOT_POSITION_MERCENARIES_HERE);
			player.sendActionFailed();
			return;
		}

		// Нельзя размещать наемников в комнате ожидания
		/*for(L2NpcInstance npc : L2World.getAroundNpc(player, 1000, 50))
			if(npc instanceof L2CastleTeleporterInstance && !npc.getName().equalsIgnoreCase("Gatekeeper"))
			{
				player.sendMessage(new CustomMessage("scripts.items.MercTicket.NotThisPlace", player));
				return;
			}*/

		MercTicketManager.getInstance().addTicket(item.getItemId(), player);
		player.getInventory().destroyItem(item, 1, true);
	}

	public final int[] getItemIds()
	{
		return _itemIds;
	}

	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}