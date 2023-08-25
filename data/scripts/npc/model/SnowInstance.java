package npc.model;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.FStringCache;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Rnd;

/**
 * @author Diagod
 */
public class SnowInstance extends L2NpcInstance
{
	int	search_jaru1 = 10254;
	int	search_jaru2 = 10255;
	int	search_jaru3 = 10256;
	int	search_jaru4 = 10257;
	int	search_jaru5 = 10258;
	int	search_jaru6 = 10259;
	int	search_piece = 10272;
	int	search_scroll = 10274;

	public SnowInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		AddTimerEx(1000,60000);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		if(val != 0)
			return;
		if(getAI().isThemePark == 0)
			showHtmlFile(player,"event_search_manager001a.htm");
		else
			showHtmlFile(player,"event_search_manager001.htm");
	}

	public void showHtmlFile(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/default/" + file);
		player.sendPacket(html);
	}

	public void TIMER_FIRED_EX(int timer_id, int isThemePark)
	{
		L2NpcInstance npc = this;
		if(timer_id == 1000)
		{
			if(Rnd.get(5) < 1)
			{
				if(isThemePark == 0)
				{
					if(Rnd.get(5) < 1)
						Say(null, npc, FStringCache.getString(1600012)); // npcSayInRange
					else if(Rnd.get(4) < 1)
						Say(null, npc, FStringCache.getString(1600013));
					else if(Rnd.get(3) < 1)
						Say(null, npc, FStringCache.getString(1600014));
					else if(Rnd.get(2) < 1)
						Say(null, npc, FStringCache.getString(1600015));
					else
						Say(null, npc, FStringCache.getString(1600016));
				}
				else if(Rnd.get(2) < 1)
					Say(null, npc, FStringCache.getString(1600017));
				else
					Say(null, npc, FStringCache.getString(1600013));
			}
			AddTimerEx(1000,60000);
		}
	}

	public void AddTimerEx(final int timer_id, int time)
	{
		ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				TIMER_FIRED_EX(timer_id, getAI().isThemePark);
			}
		}, time);
	}

	public void MENU_SELECTED(L2Player player, int ask, int reply)
	{
		int isThemePark = getAI().isThemePark;
		L2NpcInstance npc = this;
		if(ask == 1)
		{
			if(reply == 1) // Обменять 50 Частей Приза
			{
				if(OwnItemCount(player,search_piece) >= 50)
				{
					DeleteItem1(player,search_piece,50);
					int _rnd = Rnd.get(10000);
					if(_rnd < 5)
						GiveItem1(player,search_jaru1,1);
					else if(_rnd < 325)
						GiveItem1(player,search_jaru2,1);
					else if(_rnd < 992)
						GiveItem1(player,search_jaru3,1);
					else if(_rnd < 1945)
						GiveItem1(player,search_jaru4,1);
					else if(_rnd < 4678)
						GiveItem1(player,search_jaru5,1);
					else
						GiveItem1(player,search_jaru6,1);

					if(isThemePark == 0)
						showHtmlFile(player,"event_search_manager001a.htm");
					else
						showHtmlFile(player,"event_search_manager001.htm");
				}
				else
					showHtmlFile(player,"event_search_manager_q01_04.htm");
			}
			else if(reply == 2) // Вернуться в город
			{
				isThemePark = player.getVarInt("isThemePark", 19);
				if(isThemePark == 1)
					InstantTeleport(player,-15148,123886,-3112);
				else if(isThemePark == 2)
					InstantTeleport(player,-80881,151307,-3040);
				else if(isThemePark == 3)
					InstantTeleport(player,18342,145271,-3088);
				else if(isThemePark == 4)
					InstantTeleport(player,81536,145638,-3528);
				else if(isThemePark == 5)
					InstantTeleport(player,79943,55619,-1552);
				else if(isThemePark == 6)
					InstantTeleport(player,117415,76498,-2688);
				else if(isThemePark == 7)
					InstantTeleport(player,146945,27152,-2200);
				else if(isThemePark == 8)
					InstantTeleport(player,111829,221375,-3608);
				else if(isThemePark == 9)
					InstantTeleport(player,46911,49441,-3056);
				else if(isThemePark == 10)
					InstantTeleport(player,147867,-58250,-2976);
				else if(isThemePark == 11)
					InstantTeleport(player,43964,-48700,-792);
				else if(isThemePark == 12)
					InstantTeleport(player,87703,-142393,-1336);
				else if(isThemePark == 13)
					InstantTeleport(player,-84752,243122,-3728);
				else if(isThemePark == 14)
					InstantTeleport(player,11179,15848,-4584);
				else if(isThemePark == 15)
					InstantTeleport(player,17441,170434,-3504);
				else if(isThemePark == 16)
					InstantTeleport(player,-44132,-113766,-240);
				else if(isThemePark == 17)
					InstantTeleport(player,114976,-178774,-856);
				else if(isThemePark == 18)
					InstantTeleport(player,-119377,47000,360);
				else
				{
					Say(player, npc, FStringCache.getString(1600019));
					InstantTeleport(player,43964,-48700,-792);
				}
				player.unsetVar("isThemePark");
			}
		}
		else if(ask == 2)
		{
			if(reply == 1) // Назад
			{
				if(isThemePark == 0)
					showHtmlFile(player,"event_search_manager001a.htm");
				else
					showHtmlFile(player,"event_search_manager001.htm");
			}
		}
		else if(ask == 3)
		{
			if(reply == 1) // Переместиться на Остров Грез
			{
				if(Rnd.get(3) < 1)
					InstantTeleport(player,-58752,-56898,-2032);
				else if(Rnd.get(2) < 1)
					InstantTeleport(player,-59722,-57866,-2032);
				else
					InstantTeleport(player,-60695,-56894,-2032);
				player.setVar("isThemePark", ""+isThemePark);
			}
			else if(reply == 2) // Купить Свиток Трансформации в Кубик - 500 аден
			{
				if(ServerVariables.getString("RabbitsToRiches", "off").equalsIgnoreCase("on"))
				{
					if(OwnItemCount(player, 57) >= ConfigValue.RabbitsToRichesScrolPrice)
					{
						showHtmlFile(player,"event_search_manager_q01_03.htm");
						if(GiveEventItem(player,57,ConfigValue.RabbitsToRichesScrolPrice,search_scroll,1,0,ConfigValue.RabbitsToRichesScrolBuyTime,0,0) == -1)
							Say(player, npc, FStringCache.getString(1600018));
					}
					else
						showHtmlFile(player,"event_search_manager_q01_05.htm");
				}
				else
					showHtmlFile(player,"event_search_manager_q01_06.htm");
			}
		}
		else
			super.MENU_SELECTED(player, ask, reply);
	}
}