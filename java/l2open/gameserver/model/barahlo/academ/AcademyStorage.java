package l2open.gameserver.model.barahlo.academ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import l2open.gameserver.model.barahlo.academ.dao.AcademyRequestDAO;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.tables.ClanTable;

public class AcademyStorage
{
	private static final AcademyStorage _instance = new AcademyStorage();

	public static AcademyStorage getInstance()
	{
		return _instance;
	}

	private static List<L2Clan> clanList = new ArrayList<L2Clan>();
	private static List<AcademyRequest> academy = new ArrayList<AcademyRequest>();

	public void updateList()
	{
		AcademyRequest[] list = academy.toArray(new AcademyRequest[academy.size()]);

		Arrays.sort(list, new Comparator<AcademyRequest>(){
			@Override
			public int compare(AcademyRequest o1, AcademyRequest o2)
			{
				return (int) (o2.getPrice() - o1.getPrice());
			}
		});

		clanList.clear();
		for(AcademyRequest request : list)
		{
			if(request.getSeats() > 0)
			{
				L2Clan clan = ClanTable.getInstance().getClan(request.getClanId());
				if(clan != null)
					clanList.add(clan);
			}
		}
	}

	public List<L2Clan> getAcademyList()
	{
		return clanList;
	}

	public AcademyRequest getReguest(int clan)
	{
		for(AcademyRequest request : academy)
		{
			if(request.getClanId() == clan)
				return request;
		}

		return null;
	}

	public List<AcademyRequest> get()
	{
		System.out.println("AcademyStorage: academy="+academy.size());
		return academy;
	}
}
