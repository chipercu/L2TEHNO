package UI.testApp;

import UI.ui_models.LogViewer;
import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;

import java.io.PrintStream;
import java.util.concurrent.ScheduledFuture;

public class TestApp1 {

    private final LogViewer logViewer;

    public TestApp1(LogViewer logViewer) {
        this.logViewer = logViewer;
    }

    public void start(){

        ThreadPoolManager.getInstance().scheduleAtFixedDelay(new RunnableImpl() {
            @Override
            public void runImpl() {
                logViewer.addLine("test_app1_log");
            }
        }, 1000, 1000);
    }


}
