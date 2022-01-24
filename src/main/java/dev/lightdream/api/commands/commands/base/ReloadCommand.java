package dev.lightdream.api.commands.commands.base;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.annotations.commands.SubCommand;
import dev.lightdream.api.databases.User;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SubCommand(parent = ReloadCommand.class,
        command = "reload")
public class ReloadCommand extends dev.lightdream.api.commands.SubCommand {
    public ReloadCommand(@NotNull IAPI api) {
        super(api);
    }

    @Override
    public void execute(User user, List<String> args) {
        api.loadConfigs();
    }

    @Override
    public List<String> onTabComplete(User user, List<String> args) {
        return new ArrayList<>();
    }
}
