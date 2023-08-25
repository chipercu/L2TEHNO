package l2open.gameserver.instancemanager;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.olympiad.OlympiadHistory;
import l2open.gameserver.model.entity.olympiad.OlympiadHistoryDB;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.StatsSet;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class OlympiadHistoryManager
{
	private static final OlympiadHistoryManager _instance = new OlympiadHistoryManager();

	private IntObjectMap<List<OlympiadHistory>> _historyNew = new CHashIntObjectMap<List<OlympiadHistory>>();
	private IntObjectMap<List<OlympiadHistory>> _historyOld = new CHashIntObjectMap<List<OlympiadHistory>>();

	public static OlympiadHistoryManager getInstance()
	{
		return _instance;
	}

	OlympiadHistoryManager()
	{
		for(Map.Entry<Boolean, List<OlympiadHistory>> entry : OlympiadHistoryDB.getInstance().select().entrySet())
			for(OlympiadHistory history : entry.getValue())
				addHistory(entry.getKey(), history);
	}

	public void switchData()
	{
		_historyOld.clear();

		_historyOld.putAll(_historyNew);

		_historyNew.clear();

		OlympiadHistoryDB.getInstance().switchData();
	}

	public void saveHistory(OlympiadHistory history)
	{
		addHistory(false, history);

		OlympiadHistoryDB.getInstance().insert(history);
	}

	public void addHistory(boolean old, OlympiadHistory history)
	{
		IntObjectMap<List<OlympiadHistory>> map = old ? _historyOld : _historyNew;

		addHistory0(map, history.getObjectId1(), history);
		addHistory0(map, history.getObjectId2(), history);
	}

	private void addHistory0(IntObjectMap<List<OlympiadHistory>> map, int objectId, OlympiadHistory history)
	{
		List<OlympiadHistory> historySet = map.get(objectId);
		if (historySet == null)
		{
			map.put(objectId, historySet = new CopyOnWriteArrayList<OlympiadHistory>());
		}
		historySet.add(history);
	}

	public void showHistory(L2Player player, int targetClassId, int page)
	{
		Map.Entry<Integer, StatsSet> entry = Hero.getInstance().getHeroStats(targetClassId);
		if (entry == null)
		{
			return;
		}
		List<OlympiadHistory> historyList = _historyOld.get(entry.getKey());
		if (historyList == null)
		{
			historyList = Collections.emptyList();
		}
		NpcHtmlMessage html = new NpcHtmlMessage(player, null);
		html.setFile("data/html/olympiad/monument_hero_info.htm");
		html.replace("%title%", player.getLang().equals("ru") ? "<font color=\"LEVEL\">Всего: </font> %wins% Побед %ties% Ничьих %losses% Проигрышей" : "<font color=\"LEVEL\">Total score: </font> %wins% Wins %ties% Ties %losses% Losses");

		int allStatWinner = 0;
		int allStatLoss = 0;
		int allStatTie = 0;
		for (OlympiadHistory h : historyList)
		{
			if (h.getGameStatus() == 0)
			{
				allStatTie++;
			}
			else
			{
				int team = (entry.getKey() == h.getObjectId1()) ? 1 : 2;
				if (h.getGameStatus() == team)
				{
					allStatWinner++;
				}
				else
				{
					allStatLoss++;
				}
			}
		}
		html.replace("%wins%", String.valueOf(allStatWinner));
		html.replace("%ties%", String.valueOf(allStatTie));
		html.replace("%losses%", String.valueOf(allStatLoss));

		int min = 15 * (page - 1);
		int max = 15 * page;

		int currentWinner = 0;
		int currentLoss = 0;
		int currentTie = 0;
		int breakat = 0;

		StringBuilder b = new StringBuilder(500);

		for (int i = 0; i < historyList.size(); i++)
		{
			breakat = i;
			OlympiadHistory history = historyList.get(i);
			if (history.getGameStatus() == 0)
			{
				currentTie++;
			}
			else
			{
				int team = entry.getKey() == history.getObjectId1() ? 1 : 2;
				if(history.getGameStatus() == team)
					currentWinner++;
				else
				{
					currentLoss++;
				}
			}
			if (i < min)
			{
				continue;
			}
			if (i >= max)
			{
				break;
			}
			b.append("<tr><td>");
			b.append(history.toString(player, entry.getKey(), currentWinner, currentLoss, currentTie));
			b.append("</td></tr");
		}

		if (breakat < (historyList.size() - 1))
		{
			html.replace("%buttprev%", "<button value=\"Prev\" action=\"bypass _match?class=" + targetClassId + "&page=" + (page + 1) + "\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		else
		{
			html.replace("%buttprev%", "");
		}

		if (page > 1)
		{
			html.replace("%buttnext%", "<button value=\"Next\" action=\"bypass _match?class=" + targetClassId + "&page=" + (page - 1) + "\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		else
		{
			html.replace("%buttnext%", "");
		}
		html.replace("%list%", b.toString());

		player.sendPacket(html);
	}
}
