package org.gaidumax.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static final boolean TURN_ON = true;
    private static final int TITLE_LENGTH = 12;
    private static final DateFormat FORMAT = new SimpleDateFormat("<HH:mm:ss.SSS>");
    private final String title;

    public Logger(String title) {
        if (title.length() > TITLE_LENGTH) {
            title = title.substring(0, TITLE_LENGTH);
        } else if (title.length() < TITLE_LENGTH) {
            title += " ".repeat(TITLE_LENGTH - title.length());
        }
        this.title = title;
    }

    public void log(String msg) {
        if (!TURN_ON) return;
        System.out.println(FORMAT.format(new Date()) + " [" + title + "] " + msg);
    }
}
