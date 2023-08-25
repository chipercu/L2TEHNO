package commands.user;

import java.text.SimpleDateFormat;
import java.util.*;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IUserCommandHandler;
import l2open.gameserver.handler.UserCommandHandler;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Files;

/**
 * Support for commands:
 * /clanpenalty
 * /instancezone 
 */
public class Penalty extends Functions implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 100, 114 };

	public boolean useUserCommand(int id, L2Player activeChar)
	{
		if(id == 100)
		{
			long leaveClan = 0;
			if(activeChar.getLeaveClanTime() != 0)
				leaveClan = activeChar.getLeaveClanTime() + 1 * 24 * 60 * 60 * 1000L;
			long deleteClan = 0;
			if(activeChar.getDeleteClanTime() != 0)
				deleteClan = activeChar.getDeleteClanTime() + 10 * 24 * 60 * 60 * 1000L;
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			String html = Files.read("data/scripts/commands/user/penalty.htm", activeChar);

			if(activeChar.getClanId() == 0)
			{
				if(leaveClan == 0 && deleteClan == 0)
				{
					html = html.replaceFirst("%reason%", "No penalty is imposed.");
					html = html.replaceFirst("%expiration%", " ");
				}
				else if(leaveClan > 0 && deleteClan == 0)
				{
					html = html.replaceFirst("%reason%", "Penalty for leaving clan.");
					html = html.replaceFirst("%expiration%", format.format(leaveClan));
				}
				else if(deleteClan > 0)
				{
					html = html.replaceFirst("%reason%", "Penalty for dissolving clan.");
					html = html.replaceFirst("%expiration%", format.format(deleteClan));
				}
			}
			else if(activeChar.getClan().canInvite())
			{
				html = html.replaceFirst("%reason%", "No penalty is imposed.");
				html = html.replaceFirst("%expiration%", " ");
			}
			else
			{
				html = html.replaceFirst("%reason%", "Penalty for expelling clan member.");
				html = html.replaceFirst("%expiration%", format.format(activeChar.getClan().getExpelledMemberTime()));
			}
			show(html, activeChar);
		}
		else if(id == 114)
		{
			Reflection actionRef = activeChar.getActiveReflection();
			if(actionRef != null)
				activeChar.sendPacket(new SystemMessage(SystemMessage.INSTANT_ZONE_CURRENTLY_IN_USE__S1).addInstanceName(actionRef.getInstancedZoneId()));

            //if(activeChar.getReflection().getInstancedZoneId() > 0)
             //   activeChar.sendPacket(new SystemMessage(SystemMessage.INSTANT_ZONE_CURRENTLY_IN_USE__S1).addInstanceName(activeChar.getReflection().getInstancedZoneId()));

			int limit;
			boolean noLimit = true;
            boolean showMsg = false;
			InstancedZoneManager ilm = InstancedZoneManager.getInstance();
			List<String> list = new ArrayList<String>();
			for(int i : ilm.getIds())
			{
				limit = ilm.getTimeToNextEnterInstance(i, activeChar);
				if(limit > 0)
				{
					noLimit = false;
                    if(!showMsg)
                    {
                        activeChar.sendPacket(new SystemMessage(SystemMessage.INSTANCE_ZONE_TIME_LIMIT));
                        showMsg = true;
                    }
					String name = ilm.getName(i);
					if(!list.contains(name))
					{
						activeChar.sendPacket(new SystemMessage(SystemMessage.S1_WILL_BE_AVAILABLE_FOR_RE_USE_AFTER_S2_HOURS_S3_MINUTES).addInstanceName(i).addNumber(limit / 60).addNumber(limit % 60).addNumber(limit / 1440));
						list.add(name);
					}
					name = null;
				}
			}
			list.clear();
			list = null;
            if(activeChar.getClan()!= null && (activeChar.getClan().getHasFortress() > 0 || activeChar.getClan().getHasCastle() > 0))
			{
                if(ServerVariables.getLong("_q726"+activeChar.getClanId(), 0) > System.currentTimeMillis())
                {
					if(!showMsg)
					{
						activeChar.sendPacket(new SystemMessage(SystemMessage.INSTANCE_ZONE_TIME_LIMIT));
						showMsg = true;
					}
                    noLimit = false;
                    activeChar.sendPacket(new SystemMessage(SystemMessage.S1_WILL_BE_AVAILABLE_FOR_RE_USE_AFTER_S2_HOURS_S3_MINUTES).addInstanceName(90).addNumber((ServerVariables.getLong("_q726"+activeChar.getClanId()) - System.currentTimeMillis()) / 1000 / 3600).addNumber((ServerVariables.getLong("_q726"+activeChar.getClanId()) - System.currentTimeMillis()) / 1000 / 60 % 60));
                }
                else if(ServerVariables.getLong("_q727"+activeChar.getClanId(), 0) > System.currentTimeMillis())
                {
					if(!showMsg)
					{
						activeChar.sendPacket(new SystemMessage(SystemMessage.INSTANCE_ZONE_TIME_LIMIT));
						showMsg = true;
					}
                    noLimit = false;
                    activeChar.sendPacket(new SystemMessage(SystemMessage.S1_WILL_BE_AVAILABLE_FOR_RE_USE_AFTER_S2_HOURS_S3_MINUTES).addInstanceName(80).addNumber((ServerVariables.getLong("_q727"+activeChar.getClanId()) - System.currentTimeMillis()) / 1000 / 3600).addNumber((ServerVariables.getLong("_q727"+activeChar.getClanId()) - System.currentTimeMillis()) / 1000 / 60 % 60));
                }
			}
			if(noLimit)
				activeChar.sendPacket(Msg.THERE_IS_NO_INSTANCE_ZONE_UNDER_A_TIME_LIMIT);
		}
		return false;
	}

	public final int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}

	public void onLoad()
	{
		UserCommandHandler.getInstance().registerUserCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}