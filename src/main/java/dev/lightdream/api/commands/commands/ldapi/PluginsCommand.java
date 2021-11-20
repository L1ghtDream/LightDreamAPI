package dev.lightdream.api.commands.commands.ldapi;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.commands.SubCommand;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.utils.MessageBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

@dev.lightdream.api.annotations.commands.SubCommand(
        aliases = {"plugins", "pl"},
        minimumArgs = 1
)
public class PluginsCommand extends SubCommand {
    public PluginsCommand(@NotNull IAPI api) {
        super(api);
        //super(api, Collections.singletonList("plugins"), "", "", false, false, "", 0);
    }

    @Override
    public void execute(User user, List<String> args) {
        StringBuilder s = new StringBuilder();
        api.getAPI().plugins.forEach(plugin -> s.append(new MessageBuilder(api.getLang().pluginFormat).addPlaceholders(new HashMap<String, String>() {{
            put("project-name", plugin.getProjectName());
            put("project-id", plugin.getProjectID());
            put("project-version", plugin.getProjectVersion());
        }}).parse()));

        user.sendMessage(api, new MessageBuilder(api.getLang().pluginList).addPlaceholders(new HashMap<String, String>() {{
            put("plugins", s.toString());
        }}));
    }

    @Override
    public List<String> onTabComplete(User user, List<String> args) {
        return null;
    }
}
