package dev.lightdream.api.commands;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.utils.MessageBuilder;
import dev.lightdream.logger.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class SubCommand {

    public final @NotNull List<String> aliases = new ArrayList<>();
    public final @NotNull String description;
    public final @NotNull String permission;
    public final boolean onlyForPlayers;
    public final boolean onlyForConsole;
    public final String usage;
    public final IAPI api;
    public final int minimumArgs;
    public final String parentCommand;

    public SubCommand(@NotNull IAPI api) {
        this.api = api;

        if (!getClass().isAnnotationPresent(dev.lightdream.api.annotations.commands.SubCommand.class)) {
            Logger.error("Class " + getClass().getSimpleName() + " has not been annotated with @SubCommand");
            this.description = "";
            this.permission = "";
            this.onlyForConsole = false;
            this.onlyForPlayers = false;
            this.usage = "";
            this.minimumArgs = 0;
            this.parentCommand = "";
            return;
        }

        dev.lightdream.api.annotations.commands.SubCommand subCommand = getClass().getAnnotation(dev.lightdream.api.annotations.commands.SubCommand.class);

        if (subCommand.aliases().length == 0) {
            this.aliases.add("");
        }
        for (String alias : subCommand.aliases()) {
            this.aliases.add(alias.toLowerCase());
        }
        this.parentCommand = subCommand.parentCommand();

        this.description = subCommand.description();
        if (subCommand.permission().equals("")) {
            this.permission = api.getProjectID() + "." + parentCommand + "." + aliases.get(0);
        } else {
            this.permission = api.getProjectID() + "." + parentCommand + "." + subCommand.permission();
        }
        this.onlyForPlayers = subCommand.onlyForPlayers();
        this.onlyForConsole = subCommand.onlyForConsole();
        this.usage = "/" + parentCommand + " " + aliases.get(0) + " " + subCommand.usage();
        this.minimumArgs = subCommand.minimumArgs();
    }

    public SubCommand(@NotNull IAPI api, String parentCommand) {
        this.api = api;

        if (!getClass().isAnnotationPresent(dev.lightdream.api.annotations.commands.SubCommand.class)) {
            Logger.error("Class " + getClass().getSimpleName() + " has not been annotated with @SubCommand");
            this.description = "";
            this.permission = "";
            this.onlyForConsole = false;
            this.onlyForPlayers = false;
            this.usage = "";
            this.minimumArgs = 0;
            this.parentCommand = "";
            return;
        }

        dev.lightdream.api.annotations.commands.SubCommand subCommand = getClass().getAnnotation(dev.lightdream.api.annotations.commands.SubCommand.class);

        if (subCommand.aliases().length == 0) {
            this.aliases.add("");
        }
        for (String alias : subCommand.aliases()) {
            this.aliases.add(alias.toLowerCase());
        }

        this.description = subCommand.description();
        if (subCommand.permission().equals("")) {
            this.permission = api.getProjectID() + "." + parentCommand + "." + aliases.get(0);
        } else {
            this.permission = api.getProjectID() + "." + parentCommand + "." + subCommand.permission();
        }
        this.onlyForPlayers = subCommand.onlyForPlayers();
        this.onlyForConsole = subCommand.onlyForConsole();
        this.usage = "/" + parentCommand + " " + aliases.get(0) + " " + subCommand.usage();
        this.minimumArgs = subCommand.minimumArgs();
        this.parentCommand = parentCommand;
    }

    @Deprecated
    public SubCommand(@NotNull IAPI api, @NotNull List<String> aliases, @NotNull String description, @NotNull String permission, boolean onlyForPlayers, boolean onlyForConsole, @NotNull String usage, int minimumArgs, String parentCommand) {
        this.api = api;
        this.minimumArgs = minimumArgs;
        this.parentCommand = parentCommand;
        for (String alias : aliases) {
            this.aliases.add(alias.toLowerCase());
        }
        this.description = description;
        if (permission.equals("")) {
            this.permission = api.getProjectID() + "." + parentCommand + "." + aliases.get(0);
        } else {
            this.permission = api.getProjectID() + "." + parentCommand + "." + permission;
        }
        this.onlyForPlayers = onlyForPlayers;
        this.onlyForConsole = onlyForConsole;
        this.usage = "/" + parentCommand + " " + aliases.get(0) + " " + usage;
    }

    @Deprecated
    @SuppressWarnings("unused")
    public SubCommand(@NotNull IAPI api, String alias, boolean onlyForPlayers, boolean onlyForConsole, @NotNull String usage, int minimumArgs, String parentCommand) {
        this.api = api;
        this.minimumArgs = minimumArgs;
        this.parentCommand = parentCommand;
        this.aliases.add(alias.toLowerCase());
        this.description = "";
        this.permission = api.getProjectID() + "." + parentCommand + "." + aliases.get(0);
        this.onlyForPlayers = onlyForPlayers;
        this.onlyForConsole = onlyForConsole;
        this.usage = "/" + parentCommand + " " + aliases.get(0) + " " + usage;
    }

    @Deprecated
    @SuppressWarnings("unused")
    public SubCommand(@NotNull IAPI api, List<String> aliases, boolean onlyForPlayers, boolean onlyForConsole, @NotNull String usage, int minimumArgs, String parentCommand) {
        this.api = api;
        this.minimumArgs = minimumArgs;
        this.parentCommand = parentCommand;
        for (String alias : aliases) {
            this.aliases.add(alias.toLowerCase());
        }
        this.description = "";
        this.permission = api.getProjectID() + "." + parentCommand + "." + aliases.get(0);
        this.onlyForPlayers = onlyForPlayers;
        this.onlyForConsole = onlyForConsole;
        this.usage = "/" + parentCommand + " " + aliases.get(0) + " " + usage;
    }

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
        return args.size() < minimumArgs;
    }

    @SuppressWarnings("unused")
    private boolean check(User user, List<String> args) {
        return args.size() < minimumArgs;
    }

    private void interExecute(User user, List<String> args) {
        if (check(user, args)) {
            sendUsage(user);
            return;
        }
        execute(user, args);
    }

    public abstract void execute(User user, List<String> args);

    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (sender instanceof Player) {
            return onTabComplete(api.getDatabaseManager().getUser(sender), args);
        } else {
            return onTabComplete(api.getConsoleUser(), args);
        }
    }

    public abstract List<String> onTabComplete(User sender, List<String> args);


    @SuppressWarnings("unused")
    public void sendUsage(CommandSender sender) {
        api.getMessageManager().sendMessage(sender, new MessageBuilder(usage));
    }

    public void sendUsage(User user) {
        user.sendMessage(api, usage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubCommand that = (SubCommand) o;
        for (String alias : aliases) {
            if (that.aliases.contains(alias)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(aliases);
    }
}
