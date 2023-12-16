package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

public class DropItem extends Functions implements ScriptFile
{
	public void onLoad()
	{
		_log.info("Loaded Service: DropListTable");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	/*public static void OnDie(L2Character cha, L2Character killer)
	{
		if(cha != null && killer != null)
		{
			L2Player pKiller = killer.getPlayer();
			if(pKiller != null && SimpleCheckDrop(cha, killer) && cha.getLevel() > 75 && Rnd.get(100) < 5)
				((L2NpcInstance)cha).dropItem(pKiller, 15343, 1);
		}
	}*/
}