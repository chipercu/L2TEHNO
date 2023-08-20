package npc.model;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Rnd;

public class L2MoSInstance extends L2MonsterInstance
{
	public L2MoSInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void doDie(L2Character killer)
	{
		L2Player p = killer.getPlayer();
		if(p != null)
		{
			String req = (p.getVar("NextQuest464") == null || p.getVar("NextQuest464").equalsIgnoreCase("null")) ? "0" : p.getVar("NextQuest464");
			if (Long.parseLong(req) > System.currentTimeMillis() && p.getInventory().getCountOf(15537) > 0)
				return;
			try
			{
				int r = Rnd.get(1000);
				if(r < 4)
					dropItem(p, 15537, 1);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		super.doDie(killer);
	}
}