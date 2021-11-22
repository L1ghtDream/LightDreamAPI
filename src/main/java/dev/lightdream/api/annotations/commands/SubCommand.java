package dev.lightdream.api.annotations.commands;

import dev.lightdream.api.API;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SubCommand {

    String description() default "";

    String permission() default "";

    boolean onlyForPlayers() default false;

    boolean onlyForConsole() default false;

    String usage() default "";

    int minimumArgs() default 0;

    String[] aliases() default {};

    String parentCommand() default "";

}
