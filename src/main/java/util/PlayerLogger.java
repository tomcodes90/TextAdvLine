package util;

import com.googlecode.lanterna.gui2.TextBox;

import java.util.logging.*;

public class PlayerLogger {
    private static final Logger logger = Logger.getLogger("player");
    private static TextBox logBox;
    private static Runnable refresher;

    static {
        logger.setUseParentHandlers(false);  // Don't print to terminal
        logger.setLevel(Level.ALL);

        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (logBox != null) {
                    logBox.addLine(record.getMessage());
                    if (refresher != null) refresher.run();
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        });
    }

    public static void init(TextBox box, Runnable refresh) {
        logBox = box;
        refresher = refresh;
    }

    public static Logger get() {
        return logger;
    }

    public static void log(String msg) {
        logger.info(msg);
    }
}
