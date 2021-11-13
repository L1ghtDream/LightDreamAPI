package dev.lightdream.api.commands.commands.ldapi;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.commands.SubCommand;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.utils.MessageBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ChoseLangCommand extends SubCommand {
    public ChoseLangCommand(@NotNull IAPI api) {
        super(api, Collections.singletonList("choseLang"), "", "", true, false, "[lang]");
    }

    @Override
    public void execute(User user, List<String> args) {
        if (args.size() != 1) {
            sendUsage(user);
            return;
        }

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
