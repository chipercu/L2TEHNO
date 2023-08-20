package commands.admin;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.ReflectionTable;

public class AdminInstance implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_instance,
		admin_instance_id
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanTeleport)
			return false;

		switch(command)
		{
			case admin_instance:
				listOfInstances(activeChar);
				break;
			case admin_instance_id:
				if(wordList.length > 1)
					listOfCharsForInstance(activeChar, wordList[1]);
				break;
		}

		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void listOfInstances(L2Player activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		StringBuffer replyMSG = new StringBuffer("<html><title>Instance Menu</title><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>List of Instances</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table><br><br>");

		for(Reflection reflection : ReflectionTable.getInstance().getAll())
		{
			if(reflection == null || reflection.getId() == 0 || reflection.isCollapseStarted())
				continue;
			int countPlayers = 0;
			if(reflection.getPlayers() != null)
				countPlayers = reflection.getPlayers().size();
			replyMSG.append("<a action=\"bypass -h admin_instance_id ").append(reflection.getId()).append(" \">[").append(reflection.getName()).append("][").append(countPlayers).append("]["+reflection.getId()+"]</a><br>");
		}

		replyMSG.append("<button value=\"Refresh\" action=\"bypass -h admin_instance\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void listOfCharsForInstance(L2Player activeChar, String sid)
	{
		Reflection reflection = ReflectionTable.getInstance().get(Integer.parseInt(sid));

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		StringBuffer replyMSG = new StringBuffer("<html><title>Instance Menu</title><body><br>");
		if(reflection != null)
		{
			replyMSG.append("<table width=260><tr>");
			replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td width=180><center>List of players in ").append(reflection.getName()).append("</center></td>");
			replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_instance\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("</tr></table><br><br>");

			for(L2Player player : reflection.getPlayers())
				replyMSG.append("<a action=\"bypass -h admin_teleportto ").append(player.getName()).append(" \">").append(player.getName()).append("</a><br>");
		}
		else
		{
			replyMSG.append("Instance not active.<br>");
			replyMSG.append("<a action=\"bypass -h admin_instance\">Back to list.</a><br>");
		}

		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
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