package l2open.gameserver.model.barahlo.academ2;

public class Academicians
{
	public long time;
	public int obj_id;
	public int clan_id;
	public long price;

	public Academicians(long t, int o, int c, long p, boolean add)
	{
		time = t;
		obj_id = o;
		clan_id = c;
		price = p;
		if(add)
			AcademiciansStorage.getInstance().addAcademic(this);
	}
}
