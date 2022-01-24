package dev.lightdream.api.commands.commands.ldapi.subcommands;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.commands.SubCommand;
import dev.lightdream.api.commands.commands.ldapi.LdAPI;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.utils.MessageBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
@dev.lightdream.api.annotations.commands.SubCommand(aliases = {"pl"},
        parent = LdAPI.class,
        command = "plugins")
public class PluginsCommand extends SubCommand {
    public PluginsCommand(@NotNull IAPI api) {
        super(api);
    }

    @Override
    public void execute(User user, List<String> args) {
        System.out.println("YES HERE");
        StringBuilder s = new StringBuilder();
        api.getAPI().plugins.forEach(plugin -> s.append(new MessageBuilder(api.getLang().pluginFormat).addPlaceholders(new HashMap<String, String>() {{
            put("project-name", plugin.getProjectName());
            put("project-id", plugin.getProjectID());
            put("project-version", plugin.getProjectVersion());
        }}).parse()));

        new MessageBuilder(api.getLang().pluginList).addPlaceholders(new HashMap<String, String>() {{
            put("plugins", s.toString());
        }}).send(user);
    }

    @Override
    public List<String> onTabComplete(User user, List<String> args) {
        return null;
    }
}
