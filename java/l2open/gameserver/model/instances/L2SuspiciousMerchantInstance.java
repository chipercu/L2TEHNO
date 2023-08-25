package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.FortressManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.Calendar;

public class L2SuspiciousMerchantInstance extends L2NpcInstance
{
	public L2SuspiciousMerchantInstance(int objectID, L2NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("showSiegeInfo"))
			showSiegeInfoWindow(player);
		else if(command.startsWith("Chat"))
			try
			{
				int val = Integer.parseInt(command.substring(5));
				showChatWindow(player, val);
			}
			catch(NumberFormatException nfe)
			{
				String filename = command.substring(5).trim();
				if(filename.length() == 0)
					showChatWindow(player, "data/html/npcdefault.htm");
				else
					showChatWindow(player, filename);
			}
		else if(command.startsWith("fortReg"))
		{
			if(player.getClan() == null)
			{
				showChatWindow(player, "data/html/fortress/merchant-noclan.htm");
				return;
			}

			// Нельзя регистрироваться куда-либо, если клан участвует в осаде.
			if(player.getClan() == null || player.getClan().getSiege() != null)
				return;

			if((player.getClanPrivileges() & L2Clan.CP_CS_MANAGE_SIEGE) != L2Clan.CP_CS_MANAGE_SIEGE)
			{
				player.sendPacket(Msg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}

			if(!getFortress().getSiege().isInProgress() && !TerritorySiege.isInProgress())
			{
				for(Fortress fort : FortressManager.getInstance().getFortresses().values())
					if(fort.getSiege().checkIsAttacker(player.getClan()) || (fort.getOwner() == player.getClan() && fort.getSiege().checkIsDefender(player.getClan())) || fort.getSiege().checkIsDefenderWaiting(player.getClan()) || fort.getSiege().checkIsDefenderRefused(player.getClan()))
					{
						player.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestJoinSiege.AlreadyRegistered", player).addString(fort.getName()));
						return;
					}
				if(ConfigValue.FortressSiege3h)
					for(Castle c : CastleManager.getInstance().getCastles().values())
						if(c.getSiege() != null)
						{
							long timeRemaining = c.getSiege().getSiegeDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis(); // Время до начала осады
							if(timeRemaining < 10800000 && (c.getSiege().checkIsAttacker(player.getClan()) ||  c.getSiege().checkIsDefender(player.getClan())))
							{
								player.sendMessage("Вы не можете зарегестрироватся на осаду форта менее чем за 3 часа до начала осад Замка.");
								return;
							}
						}
				
				if(getFortress().getSiege().registerAttacker(player))
					ShowPage(player, "fortress_ordery005.htm");
			}
			else
			{
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile("data/html/fortress/merchant-busy.htm");
				html.replace("%fortname%", getFortress().getName());
				html.replace("%objectId%", String.valueOf(getObjectId()));
				player.sendPacket(html);
			}
		}
		else if(command.startsWith("fortUnReg"))
		{
			// Нельзя регистрироваться куда-либо, если клан участвует в осаде.
			if(player.getClan() == null || player.getClan().getSiege() != null)
				return;

			// TODO: Шлем ШТМЛ, что ты не на реге.
			if(!getFortress().getSiege().checkIsAttacker(player.getClan()))
				return;

			if((player.getClanPrivileges() & L2Clan.CP_CS_MANAGE_SIEGE) != L2Clan.CP_CS_MANAGE_SIEGE)
			{
				player.sendPacket(Msg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}

			getFortress().getSiege().clearSiegeClan(player.getClan(), false);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		String filename;

		L2Clan clan = player.getClan();
		Fortress fortress = getFortress();

		if(val == 0)
			filename = "data/html/fortress/merchant.htm";
		else
			filename = "data/html/fortress/merchant-" + val + ".htm";

		if(fortress != null && fortress.getSiege() != null && fortress.getSiege().isInProgress() || TerritorySiege.isInProgress())
			filename = "data/html/fortress/merchant-busy.htm";

		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcId%", String.valueOf(getNpcId()));
		html.replace("%fortname%", fortress.getName());

		if(getFortress().getOwnerId() > 0)
			html.replace("%clanname%", ClanTable.getInstance().getClan(getFortress().getOwnerId()).getName());
		else
			html.replace("%clanname%", "NPC");

		player.sendPacket(html);
	}

	public void showSiegeInfoWindow(L2Player player)
	{
		if(!getFortress().getSiege().isInProgress() && !TerritorySiege.isInProgress())
			getFortress().getSiege().listRegisterClan(player);
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("data/html/fortress/merchant-busy.htm");
			html.replace("%fortname%", getFortress().getName());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
		}
	}
}