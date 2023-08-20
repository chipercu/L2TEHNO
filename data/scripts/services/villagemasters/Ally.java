package services.villagemasters;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2VillageMasterInstance;

public class Ally extends Functions implements ScriptFile
{
	public void onLoad()
	{
		_log.info("Loaded Service: Villagemasters [Alliance Operations]");
	}

	public void CheckCreateAlly()
	{
		if(getNpc() == null || getSelf() == null)
			return;
		L2Player pl = (L2Player) getSelf();
		String htmltext = "ally-01.htm";
		if(pl.isClanLeader())
			htmltext = "ally-02.htm";
		((L2VillageMasterInstance) getNpc()).showChatWindow(pl, "data/html/villagemaster/" + htmltext);
	}

	public void CheckDissolveAlly()
	{
		if(getNpc() == null || getSelf() == null)
			return;
		L2Player pl = (L2Player) getSelf();
		String htmltext = "ally-01.htm";
		if(pl.isAllyLeader())
			htmltext = "ally-03.htm";
		((L2VillageMasterInstance) getNpc()).showChatWindow(pl, "data/html/villagemaster/" + htmltext);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}