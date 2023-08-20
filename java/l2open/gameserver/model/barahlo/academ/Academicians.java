package l2open.gameserver.model.barahlo.academ;

public class Academicians
{
	private long time;
	private int objId;
	private int clanId;

	public Academicians(long t, int o, int c)
	{
		time = t;
		objId = o;
		clanId = c;
		AcademiciansStorage.getInstance().get().add(this);
	}

	public long getTime()
	{
		return time;
	}

	public int getObjId()
	{
		return objId;
	}

	public int getClanId()
	{
		return clanId;
	}
}
