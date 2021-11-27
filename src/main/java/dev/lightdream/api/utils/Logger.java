package dev.lightdream.api.utils;

import dev.lightdream.api.IAPI;
import org.fusesource.jansi.Ansi;

public class Logger {

    public static boolean enabled = false;
    private static IAPI api;

    public static void info(Object object) {
        if (object == null) {
            info("null");
            return;
        }
        info(object.toString());
    }

    public static void info(String message) {
        if (api == null) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).boldOff() + "The logger has not been initialized."
                    + Ansi.ansi().fg(Ansi.Color.DEFAULT).boldOff());
            return;
        }
        api.getPlugin().getLogger().info(message);
    }

    public static void error(Object object) {
        if (object == null) {
            error("null");
            return;
        }
        error(object.toString());
    }

    public static void error(String message) {
        if (api == null) {
            info(message);
            return;
        }
        info(Ansi.ansi().fg(Ansi.Color.RED).boldOff() + message +
                Ansi.ansi().fg(Ansi.Color.DEFAULT).boldOff());
    }

    public static void good(Object object) {
        if (object == null) {
            error("null");
            return;
        }
        error(object.toString());
    }

    public static void good(String message) {
        if (api == null) {
            info(message);
            return;
        }
        info(Ansi.ansi().fg(Ansi.Color.GREEN).boldOff() + message +
                Ansi.ansi().fg(Ansi.Color.DEFAULT).boldOff());
    }

    public static void init(IAPI api) {
        Logger.api = api;
        Logger.enabled = true;
    }

}
