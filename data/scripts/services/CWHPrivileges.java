package services;

import l2open.config.ConfigValue;
import l2open.database.mysql;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ClanMember;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.GArray;

public class CWHPrivileges extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "clan" };

	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
		_log.info("Loaded Service: CWH Privileges");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	@Override
	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		if(activeChar.getClan() == null)
			return false;
		if(command.equals("clan"))
		{
			if(ConfigValue.AltAllowClanCommandOnlyForClanLeader && !activeChar.isClanLeader())
				return false;
			if(!((activeChar.getClanPrivileges() & L2Clan.CP_CL_MANAGE_RANKS) == L2Clan.CP_CL_MANAGE_RANKS))
				return false;
			if(args != null)
			{
				String[] param = args.split(" ");
				if(param.length > 0)
					if(param[0].equalsIgnoreCase("allowwh") && param.length > 1)
					{
						L2ClanMember cm = activeChar.getClan().getClanMember(param[1]);
						if(cm != null && cm.getPlayer() != null) // цель онлайн
						{
							if(cm.getPlayer().getVarB("canWhWithdraw"))
							{
								cm.getPlayer().unsetVar("canWhWithdraw");
								activeChar.sendMessage("Privilege removed successfully");
							}
							else
							{
								cm.getPlayer().setVar("canWhWithdraw", "1");
								activeChar.sendMessage("Privilege given successfully");
							}
						}
						else if(cm != null) // цель оффлайн
						{
							int state = mysql.simple_get_int("value", "character_variables", "obj_id=" + cm.getObjectId() + " AND name LIKE 'canWhWithdraw'");
							if(state > 0)
							{
								mysql.set("DELETE FROM `character_variables` WHERE obj_id=" + cm.getObjectId() + " AND name LIKE 'canWhWithdraw' LIMIT 1");
								activeChar.sendMessage("Privilege removed successfully");
							}
							else
							{
								mysql.set("INSERT INTO character_variables  (obj_id, type, name, value, expire_time) VALUES (" + cm.getObjectId() + ",'user-var','canWhWithdraw','1',-1)");
								activeChar.sendMessage("Privilege given successfully");
							}
						}
						else
							activeChar.sendMessage("Player not found.");
					}
					else if(param[0].equalsIgnoreCase("list"))
					{
						StringBuilder sb = new StringBuilder("SELECT `obj_id` FROM `character_variables` WHERE `obj_id` IN (");
						L2ClanMember[] members = activeChar.getClan().getMembers();
						for(int i = 0; i < members.length; i++)
						{
							sb.append(members[i].getObjectId());
							if(i < members.length - 1)
								sb.append(",");
						}
						sb.append(") AND `name`='canWhWithdraw'");
						GArray<Object> list = mysql.get_array(sb.toString());
						sb = new StringBuilder("<html><body>Clan CP (.clan)<br><br><table>");
						for(Object o_id : list)
							for(L2ClanMember m : members)
								if(m.getObjectId() == Integer.parseInt(o_id.toString()))
									sb.append("<tr><td width=10></td><td width=60>").append(m.getName()).append("</td><td width=20><button width=50 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h user_clan allowwh ").append(m.getName()).append("\" value=\"Remove\">").append("<br></td></tr>");
						sb.append("<tr><td width=10></td><td width=20><button width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h user_clan\" value=\"Back\"></td></tr></table></body></html>");
						show(sb.toString(), activeChar);
						return true;
					}
			}
			String dialog = Files.read("data/scripts/services/clan.htm", activeChar);
			if(!ConfigValue.ExpandCWHEnabled)
				dialog = dialog.replaceFirst("%whextprice%", "service disabled");
			else
				dialog = dialog.replaceFirst("%whextprice%", ConfigValue.ExpandCWHPrice + " " + ItemTemplates.getInstance().getTemplate(ConfigValue.ExpandCWHItem).getName());
			show(dialog, activeChar);
			return true;
		}
		return false;
	}
}