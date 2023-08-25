package l2open.gameserver.model.barahlo;

public class PlayerInfo
{
	public int obj_id;
	public int class_id;
	public String name;
	public int oly_pts_day=0;
	public int oly_pts_week=0;
	
	public int enter_count;
	public int pvp_dead_count;
	public int pvp_kill_count;
	public int pvp_day_dead_count;
	public int pvp_day_kill_count;
	public int zone_time;

	public void incPvpDead()
	{
		pvp_dead_count++;
		pvp_day_dead_count++;
	}

	public void incPvpKill()
	{
		pvp_kill_count++;
		pvp_day_kill_count++;
	}

	public void incEnter()
	{
		enter_count++;
	}

	public void addOlyPts(int value)
	{
		oly_pts_day+=value;
		oly_pts_week+=value;
	}

	public void updateTime()
	{
		zone_time=0;
	}
}