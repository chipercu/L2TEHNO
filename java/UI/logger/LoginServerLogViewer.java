package UI.logger;

import UI.controllers.MainController;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LoginServerLogViewer extends Handler {
    @Override
    public void publish(LogRecord record) {
        MainController.getLogViewers().get( MainController.LOG_VIEW_TYPE.LOGIN).addLine( record.getLevel().getName() + ": " + record.getMessage());



    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
