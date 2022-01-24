package dev.lightdream.api.managers;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.annotations.commands.Command;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    public final List<dev.lightdream.api.commands.Command> commands = new ArrayList<>();

    public CommandManager() {

    }

    public void register(IAPI api, String projectID) {
        new Reflections("dev.lightdream." + projectID).getTypesAnnotatedWith(Command.class).forEach(aClass -> {
            try {
                for (dev.lightdream.api.commands.Command command : commands) {
                    if (command.getClass().getSimpleName().equals(aClass.getSimpleName())) {
                        return;
                    }
                }
                Object obj = aClass.getDeclaredConstructors()[0].newInstance(api);
                if (obj instanceof dev.lightdream.api.commands.Command) {
                    commands.add((dev.lightdream.api.commands.Command) obj);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

}
