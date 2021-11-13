package dev.lightdream.api.utils;

import dev.lightdream.api.IAPI;

public class Debugger {

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
            System.out.println("The debugger has not been initialized.");
            return;
        }
        if (!api.debug()) {
            return;
        }
        api.getPlugin().getLogger().info(message);
    }

    public static void init(IAPI api) {
        Debugger.api = api;
        Debugger.enabled = true;
    }

}
