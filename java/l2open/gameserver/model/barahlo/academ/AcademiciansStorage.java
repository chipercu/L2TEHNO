package l2open.gameserver.model.barahlo.academ;

import java.util.ArrayList;
import l2open.util.GArray;

public class AcademiciansStorage
{
	private static final AcademiciansStorage _instance = new AcademiciansStorage();

	public static AcademiciansStorage getInstance()
	{
		return _instance;
	}

	private static GArray<Academicians> academicians = new GArray<Academicians>();

	public GArray<Academicians> get()
	{
		return academicians;
	}

	public boolean find(int obj)
	{
		for(Academicians academic : academicians)
		{
			if(academic.getObjId() == obj)
				return true;
		}
		return false;
	}

	public Academicians get(int obj)
	{
		for(Academicians academic : academicians)
		{
			if(academic.getObjId() == obj)
				return academic;
		}

		return null;
	}

	public boolean clanCheck(int clan)
	{
		for(Academicians academic : academicians)
		{
			if(academic.getClanId() == clan)
				return true;
		}
		return false;
	}
}
