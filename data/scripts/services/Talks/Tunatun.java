package services.Talks;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.ClassId;
import l2open.util.Files;

public class Tunatun extends Functions implements ScriptFile
{
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public void Meat()
	{
		L2Player p = (L2Player) getSelf();
		if((p.getClassId().getId() == 2) && p.getLevel() >= 76 && getItemCount(p, 7546) < 1)
			addItem(p, 7546, 1); // Top Quality Meat
		else
			p.sendMessage("Only Gladiators who's level is 76 or higher can obtain this item.");
	}
}