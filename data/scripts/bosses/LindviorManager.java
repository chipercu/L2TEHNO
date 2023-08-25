package bosses;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.serverpackets.ExStartScenePlayer;

/**
 * @ author: Drizzy
 * @ date: 29.01.2011
 * @ Manager for Lindvior. This manager run movie on Keucerus Base each some hours.
 */

public class LindviorManager extends Functions implements ScriptFile
{
	private static L2Zone _zone;

	public void init()
	{
        //Run Movie after start server (1 hour)
        ThreadPoolManager.getInstance().schedule(new ShowMovie(), 3600000);
	}

    private class ShowMovie extends l2open.common.RunnableImpl
    {
        public void runImpl()
        {
            _zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.peace_zone, 4602, true);
            if(_zone != null)
                for(L2Player player : _zone.getInsidePlayers())
                    player.showQuestMovie(ExStartScenePlayer.SCENE_LINDVIOR);
            //Run Movie (6 hour)
            ThreadPoolManager.getInstance().schedule(new ShowMovie(), 21600000);
        }
    }

	public void onLoad()
	{
		init();
		_log.info("Lindvior Manager Load.");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}