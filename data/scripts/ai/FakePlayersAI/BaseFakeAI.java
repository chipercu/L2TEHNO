package ai.FakePlayersAI;

import ai.FakePlayersAI.PathManager.PathManager;
import ai.FakePlayersAI.PathManager.PathMap;
import ai.FakePlayersAI.Tasks.RunAwayTask;
import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.L2PlayerAI;
import l2open.gameserver.model.L2Player;

import java.util.concurrent.ScheduledFuture;

public abstract class BaseFakeAI extends L2PlayerAI implements ScriptFile {

    protected ScheduledFuture<?> mainTask = null;
    protected ScheduledFuture<?> runAwayTask;
    protected PathMap pathMap;


    public BaseFakeAI(L2Player actor) {
        super(actor);
        pathMap = PathManager.getInstance().getMapByName("test");
        mainTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new MainTask(), 250 ,250);
        runAwayTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunAwayTask(actor, pathMap), 250, 250);

    }


    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onReload() {
        if (mainTask != null){
            mainTask.cancel(true);
            mainTask = null;
        }
        if (runAwayTask != null){
            runAwayTask.cancel(true);
            runAwayTask = null;
        }
        pathMap = null;


    }

    @Override
    public void onShutdown() {

    }

    public class MainTask extends RunnableImpl{

        @Override
        public void runImpl() throws Exception {
            if (getActor() == null){
                if (mainTask != null){
                    mainTask.cancel(true);
                    mainTask = null;
                }
                if (runAwayTask != null){
                    runAwayTask.cancel(true);
                    runAwayTask = null;
                }
                pathMap = null;
            }
        }
    }

}
