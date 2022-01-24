package dev.lightdream.api.annotations.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String command();

    String[] aliases() default {};

    String permission() default "";

    String usage() default "";

    boolean onlyForPlayers() default false;

    boolean onlyForConsole() default false;

}
