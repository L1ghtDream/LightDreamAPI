package dev.lightdream.api.commands;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.utils.MessageBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Executable extends Command {

    public final IAPI api;

    protected Executable(IAPI api) {
        super("");
        this.api = api;
    }

    public abstract String getPermission();

    public abstract String getUsage();

    public abstract boolean onlyForPlayers();

    public abstract boolean onlyForConsole();

    public abstract List<String> getAliases();

    public abstract int getMinimumArgs();

    public abstract void execute(User user, List<String> args);

    public void execute(CommandSender sender, List<String> args) {
        if (check(sender, args)) {
            sendUsage(sender);
            return;
        }

        if (sender instanceof Player) {
            interExecute(api.getDatabaseManager().getUser(sender), args);
        } else {
            interExecute(api.getConsoleUser(), args);
        }
    }

    @SuppressWarnings("unused")
    private boolean check(CommandSender sender, List<String> args) {
        return args.size() < getMinimumArgs();
    }

    @SuppressWarnings("unused")
    private boolean check(User user, List<String> args) {
        return args.size() < getMinimumArgs();
    }

    private void interExecute(User user, List<String> args) {
        if (check(user, args)) {
            sendUsage(user);
            return;
        }
        execute(user, args);
    }

    public abstract List<String> onTabComplete(User sender, List<String> args);

    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (sender instanceof Player) {
            return onTabComplete(api.getDatabaseManager().getUser(sender), args);
        } else {
            return onTabComplete(api.getConsoleUser(), args);
        }
    }

    @SuppressWarnings("unused")
    public void sendUsage(CommandSender sender) {
        api.getMessageManager().sendMessage(sender, new MessageBuilder(getUsage()));
    }

    public void sendUsage(User user) {
        user.sendMessage(api, getUsage());
    }

}
