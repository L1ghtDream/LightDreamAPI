package dev.lightdream.api.commands.commands.ldapi;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.commands.SubCommand;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.utils.MessageBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@dev.lightdream.api.annotations.commands.SubCommand(
        aliases = "choseLang",
        usage = "[lang]",
        onlyForPlayers = true,
        minimumArgs = 1,
        parentCommand = "ld-api"
)
public class ChoseLangCommand extends SubCommand {
    public ChoseLangCommand(@NotNull IAPI api) {
        super(api);
        //super(api, Collections.singletonList("choseLang"), "", "", true, false, "[lang]", 1);
    }

    @Override
    public void execute(User user, List<String> args) {
        String lang = args.get(0);
        if (!api.getSettings().langs.contains(lang)) {
            user.sendMessage(api, new MessageBuilder(api.getLang().invalidLang));
            return;
        }

        api.getAPI().plugins.forEach(plugin -> plugin.setLang(user, lang));
        user.sendMessage(api, api.getLang().langChanged);
    }

    @Override
    public List<String> onTabComplete(User user, List<String> args) {
        return api.getSettings().langs;
    }
}
