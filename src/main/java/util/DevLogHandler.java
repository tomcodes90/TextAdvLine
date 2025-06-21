package util;

import ui.DevLogOverlay;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class DevLogHandler extends Handler {
    @Override
    public void publish(LogRecord record) {
        if (isLoggable(record)) {
            String msg = record.getLevel() + ": " + record.getMessage();
            DevLogOverlay.log(msg);  // Send to the UI
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
