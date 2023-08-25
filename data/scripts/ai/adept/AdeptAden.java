package ai.adept;

import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;

public class AdeptAden extends Adept
{
	public AdeptAden(L2NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(146363, 24149, -2008),
			new Location(146345, 25803, -2008),
			new Location(147443, 25811, -2008),
			new Location(146369, 25817, -2008)
		};
	}
}