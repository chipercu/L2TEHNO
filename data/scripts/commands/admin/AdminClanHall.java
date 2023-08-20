package commands.admin;

import java.util.logging.Logger;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.instancemanager.AuctionManager;
import l2open.gameserver.instancemanager.ClanHallManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.entity.Auction.Auction;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.ClanTable;

public class AdminClanHall implements IAdminCommandHandler, ScriptFile
{
	protected static Logger _log = Logger.getLogger(AdminClanHall.class.getName());

	private static enum Commands
	{
		admin_clanhall,
		admin_clanhallset,
		admin_clanhalldel,
		admin_clanhallopendoors,
		admin_clanhallclosedoors,
		admin_clanhallteleportself
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;

		ClanHall clanhall = null;
		if(wordList.length > 1)
			clanhall = ClanHallManager.getInstance().getClanHall(Integer.parseInt(wordList[1]));

		if(clanhall == null)
		{
			showClanHallSelectPage(activeChar);
			return true;
		}

		switch(command)
		{
			case admin_clanhall:
				showClanHallSelectPage(activeChar);
				break;
			case admin_clanhallset:
				L2Object target = activeChar.getTarget();
				L2Player player = activeChar;
				if(target != null && target.isPlayer())
					player = (L2Player) target;
				if(player.getClan() == null)
					activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
				else
				{
					clanhall.changeOwner(player.getClan());
					Auction auction = AuctionManager.getInstance().getAuction(clanhall.getId());
					if(auction != null)
						auction.cancelAuction();
					_log.fine("ClanHall " + clanhall.getName() + "(id: " + clanhall.getId() + ") owned by clan " + player.getClan().getName());
				}
				break;
			case admin_clanhalldel:
				clanhall.changeOwner(null);
				break;
			case admin_clanhallopendoors:
				clanhall.openCloseDoors(activeChar, true);
				break;
			case admin_clanhallclosedoors:
				clanhall.openCloseDoors(activeChar, false);
				break;
			case admin_clanhallteleportself:
				L2Zone zone = clanhall.getZone();
				if(zone != null)
					activeChar.teleToLocation(zone.getSpawn());
				break;
		}
		showClanHallPage(activeChar, clanhall);
		return true;
	}

	public void showClanHallSelectPage(L2Player activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuffer replyMSG = new StringBuffer("<html><body>");
		replyMSG.append("<table width=268><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center><font color=\"LEVEL\">Clan Halls:</font></center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table><br>");

		replyMSG.append("<table width=268>");
		replyMSG.append("<tr><td width=130>ClanHall Name</td><td width=58>Town</td><td width=80>Owner</td></tr>");

		// TODO: make sort by Location
		for(ClanHall clanhall : ClanHallManager.getInstance().getClanHalls().values())
			if(clanhall != null)
			{
				replyMSG.append("<tr><td>");
				replyMSG.append("<a action=\"bypass -h admin_clanhall " + clanhall.getId() + "\">" + clanhall.getName() + "</a>");
				replyMSG.append("</td><td>" + clanhall.getLocation() + "</td><td>");

				L2Clan owner = clanhall.getOwnerId() == 0 ? null : ClanTable.getInstance().getClan(clanhall.getOwnerId());
				if(owner == null)
					replyMSG.append("none");
				else
					replyMSG.append(owner.getName());

				replyMSG.append("</td></tr>");
			}

		replyMSG.append("</table>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	public void showClanHallPage(L2Player activeChar, ClanHall clanhall)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		StringBuffer replyMSG = new StringBuffer("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>ClanHall Name</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_clanhall\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<center>");
		replyMSG.append("<br><br><br>ClanHall: " + clanhall.getName() + "<br>");
		replyMSG.append("Location: " + clanhall.getLocation() + "<br>");
		replyMSG.append("ClanHall Owner: ");
		L2Clan owner = clanhall.getOwnerId() == 0 ? null : ClanTable.getInstance().getClan(clanhall.getOwnerId());
		if(owner == null)
			replyMSG.append("none");
		else
			replyMSG.append(owner.getName());

		replyMSG.append("<br><br><br>");
		replyMSG.append("<table>");
		replyMSG.append("<tr><td><button value=\"Open Doors\" action=\"bypass -h admin_clanhallopendoors " + clanhall.getId() + "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Close Doors\" action=\"bypass -h admin_clanhallclosedoors " + clanhall.getId() + "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("</table>");
		replyMSG.append("<br>");
		replyMSG.append("<table>");
		replyMSG.append("<tr><td><button value=\"Give ClanHall\" action=\"bypass -h admin_clanhallset " + clanhall.getId() + "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Take ClanHall\" action=\"bypass -h admin_clanhalldel " + clanhall.getId() + "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("</table>");
		replyMSG.append("<br>");
		replyMSG.append("<table><tr>");
		replyMSG.append("<td><button value=\"Teleport self\" action=\"bypass -h admin_clanhallteleportself " + clanhall.getId() + " \" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("</table>");
		replyMSG.append("</center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}