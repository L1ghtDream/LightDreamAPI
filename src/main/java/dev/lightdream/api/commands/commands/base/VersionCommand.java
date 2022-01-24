package dev.lightdream.api.commands.commands.base;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.annotations.commands.SubCommand;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.utils.MessageBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SubCommand(parent = VersionCommand.class,
        command = "version")
public class VersionCommand extends dev.lightdream.api.commands.SubCommand {

    public VersionCommand(@NotNull IAPI api) {
        super(api);
    }

    @Override
    public void execute(User user, List<String> args) {
        user.sendMessage(api, new MessageBuilder(api.getLang().version).addPlaceholders(new HashMap<String, String>() {{
            put("project_name", api.getProjectName());
            put("version", api.getProjectVersion());
        }}));
    }

    @Override
    public List<String> onTabComplete(User user, List<String> args) {
        return new ArrayList<>();
    }
}
