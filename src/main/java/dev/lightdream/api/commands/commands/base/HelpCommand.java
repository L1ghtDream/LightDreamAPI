package dev.lightdream.api.commands.commands.base;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.commands.SubCommand;
import dev.lightdream.api.databases.User;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@dev.lightdream.api.annotations.commands.SubCommand(
        aliases = "help"
)
public class HelpCommand extends SubCommand {
    public HelpCommand(@NotNull IAPI api) {
        super(api);
        //super(api, Collections.singletonList("help"), "", "", false, false, "", 0);
    }

    @Override
    public void execute(User user, List<String> list) {
        api.getBaseCommand().sendUsage(user);
    }

    @Override
    public List<String> onTabComplete(User commandSender, List<String> list) {
        return new ArrayList<>();
    }
}
