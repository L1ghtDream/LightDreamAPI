package dev.lightdream.api.commands;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.commands.commands.base.ReloadCommand;
import dev.lightdream.api.commands.commands.base.VersionCommand;

public abstract class BaseCommand extends Command {

    public BaseCommand(IAPI api) {
        super(api);
        subCommands.add(new ReloadCommand(api));
        subCommands.add(new VersionCommand(api));
    }


}
