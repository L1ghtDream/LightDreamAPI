package dev.lightdream.api.commands.commands.ldapi.subcommands;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.commands.SubCommand;
import dev.lightdream.api.commands.commands.ldapi.LdAPI;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.utils.MessageBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
@dev.lightdream.api.annotations.commands.SubCommand(command = "choseLang",
        usage = "[lang]",
        onlyForPlayers = true,
        minimumArgs = 1,
        parent = LdAPI.class)
public class ChoseLangCommand extends SubCommand {
    public ChoseLangCommand(@NotNull IAPI api) {
        super(api);
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
