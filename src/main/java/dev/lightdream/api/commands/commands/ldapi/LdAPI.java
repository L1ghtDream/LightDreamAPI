package dev.lightdream.api.commands.commands.ldapi;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.commands.BaseCommand;
import dev.lightdream.api.databases.User;

import java.util.List;

@dev.lightdream.api.annotations.commands.Command(command = "ldapi")
public class LdAPI extends BaseCommand {
    public LdAPI(IAPI api) {
        super(api);
    }

    @Override
    public void execute(User user, List<String> args) {
        sendUsage(user);
    }

    @Override
    public List<String> onTabComplete(User sender, List<String> args) {
        return null;
    }
}
