package npc.model;

import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * Author: Drizzy
 * Date: 30.05.12
 */
public class UgorosKeeperInstance extends L2NpcInstance
{
	private static boolean alreadyEnter = false;
	private int TID_EXILE_WAIT = 78001;
	private int TIME_EXILE_WAIT = 3;
	public UgorosKeeperInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}
	@Override
	public void onSpawn()
	{
		if(getAI().clearer_mode == 1)
		{
			AddTimerEx(TID_EXILE_WAIT,((TIME_EXILE_WAIT * 60) * 1000));
		}
		super.onSpawn();
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TID_EXILE_WAIT)
		{
			deleteMe();
			L2Zone zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.no_restart, 703171);
			for(L2Player p : zone.getInsidePlayers())
				p.teleToLocation(94224, 83019, -3552);
		}
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		if(getAI().clearer_mode == 1)
			showHtmlFile(player, "batracos005.htm");
		else
			showHtmlFile(player, "batracos001.htm");
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		if(command.equalsIgnoreCase("enter_ugoros"))
		{
			if(getAI().clearer_mode == 1)
				player.teleToLocation(94224, 83019, -3552);
			else if(L2ObjectsStorage.getByNpcId(18863).isDead())
			{
				showHtmlFile(player, "batracos004.htm");
			}
			else if(player.getInventory().getItemByItemId(15496) != null)
			{
				if(getAlreadyEnter())
					showHtmlFile(player, "batracos003.htm");
				else
				{
					player.teleToLocation(95984, 85692, -3692);
					setAlreadyEnter(true);
					player.getInventory().destroyItemByItemId(15496, 1, true);
					L2Character c0 = L2ObjectsStorage.getByNpcId(18863);
					if(c0 != null)
						getAI().SendScriptEvent(c0, 78010084, player.getObjectId());
				}
			}
			else
				showHtmlFile(player, "batracos002.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}

	public void showHtmlFile(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/default/" + file);
		player.sendPacket(html);
	}

	public static void setAlreadyEnter(boolean value)
	{
		alreadyEnter = value;
	}

	public static boolean getAlreadyEnter()
	{
		return alreadyEnter;
	}
}
