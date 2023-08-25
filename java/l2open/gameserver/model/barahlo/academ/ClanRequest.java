package l2open.gameserver.model.barahlo.academ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.tables.ClanTable;

public class ClanRequest
{
	private static List<L2Clan> clanList = new ArrayList<L2Clan>();
	private static List<ClanRequest> _inviteList = new ArrayList<ClanRequest>();

	private long time;
	private L2Player player;
	private int clanId;
	private String note;

	public ClanRequest(long t, L2Player p, int c, String n)
	{
		time = t;
		player = p;
		clanId = c;
		note = n;
		_inviteList.add(this);
	}

	public long getTime()
	{
		return time;
	}

	public L2Player getPlayer()
	{
		return player;
	}

	public int getClanId()
	{
		return clanId;
	}

	public String getNote()
	{
		return note;
	}

	public static List<ClanRequest> getInviteList(int clanId)
	{
		List<ClanRequest> _invite = new ArrayList<ClanRequest>();

		for(ClanRequest request : _inviteList)
		{
			if(request.getClanId() == clanId)
				_invite.add(request);
		}

		return _invite;
	}

	public static ClanRequest getClanInvitePlayer(int clanId, int obj)
	{
		for(ClanRequest request : _inviteList)
		{
			if(request.getClanId() == clanId && request.getPlayer().getObjectId() == obj)
				return request;
		}

		return null;
	}

	/**
	 * Метод для удаления игрока из списка при отклонении заявки или вступления в клан!
	 */
	public static void removeClanInvitePlayer(int clanId, int obj)
	{
		for(ClanRequest request : _inviteList)
		{
			if(request.getClanId() == clanId && request.getPlayer().getObjectId() == obj)
			{
				_inviteList.remove(request);
				break;
			}
		}
	}

	/**
	 * Метод для удаления игрока из списка при отмене заявки но с проверкой времени!
	 */
	public static boolean removeClanInvitePlayer(int clanId, L2Player player)
	{
		for(ClanRequest request : _inviteList)
		{
			if(request.getClanId() == clanId && request.getPlayer() == player)
			{
				int time = (int) (((request.getTime() + 60000) - System.currentTimeMillis()) / 1000);
				if(time <= 0)
				{
					_inviteList.remove(request);
					return true;
				}
				else
				{
					player.sendMessage("Заявку можно отменить только через " + time + " секунд!");
					return false;
				}
			}
		}
		return false;
	}

	public static void updateList()
	{
		L2Clan[] clans = ClanTable.getInstance().getClans();
		Arrays.sort(clans, new Comparator<L2Clan>(){
			@Override
			public int compare(L2Clan o1, L2Clan o2)
			{
				return o2.getLevel() - o1.getLevel();
			}
		});

		clanList.clear();
		for(L2Clan clan : clans)
		{
			if(clan.getLevel() > 0)
				clanList.add(clan);
		}
	}

	public static List<L2Clan> getClanList()
	{
		return clanList;
	}
}
