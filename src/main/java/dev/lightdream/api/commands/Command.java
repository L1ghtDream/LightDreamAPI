package dev.lightdream.api.commands;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.annotations.commands.SubCommand;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.managers.MessageManager;
import dev.lightdream.api.utils.MessageBuilder;
import dev.lightdream.logger.Logger;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Command extends Executable {

    public final IAPI api;
    public final List<dev.lightdream.api.commands.SubCommand> subCommands = new ArrayList<>();

    @SneakyThrows
    public Command(IAPI api) {
        super(api);
        this.api = api;

        if (!getClass().isAnnotationPresent(dev.lightdream.api.annotations.commands.Command.class)) {
            Logger.error("Class " + getClass().getSimpleName() + " is not annotated as @Command");
            return;
        }

        setName(getCommand());

        //Register the command
        Field fCommandMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
        fCommandMap.setAccessible(true);

        Object commandMapObject = fCommandMap.get(Bukkit.getPluginManager());
        if (commandMapObject instanceof CommandMap) {
            CommandMap commandMap = (CommandMap) commandMapObject;
            commandMap.register(getCommand(), this);
        } else {
            Logger.error("Command " + getCommand() + " could not be initialized");
            return;
        }
        Logger.good("Command " + getCommand() + " initialized successfully");

        //Get all the subcommands
        new Reflections("dev.lightdream").getTypesAnnotatedWith(SubCommand.class).forEach(aClass -> {
            if (aClass.getAnnotation(SubCommand.class).parent().getSimpleName().equals(getClass().getSimpleName())) {
                try {
                    subCommands.add((dev.lightdream.api.commands.SubCommand) aClass.getDeclaredConstructors()[0].newInstance(api));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public void sendUsage(CommandSender sender) {
        sendUsage(api.getDatabaseManager().getUser(sender));
    }

    public void sendUsage(User user) {
        StringBuilder helpCommandOutput = new StringBuilder();
        helpCommandOutput.append("\n");

        if (api.getLang().helpCommand.equals("")) {
            for (dev.lightdream.api.commands.SubCommand subCommand : subCommands) {
                if (user.hasPermission(subCommand.getPermission())) {
                    helpCommandOutput.append(subCommand.getCommand());
                    helpCommandOutput.append(" ");
                    helpCommandOutput.append(subCommand.getUsage());
                    helpCommandOutput.append("\n");
                }
            }
        } else {
            helpCommandOutput.append(api.getLang().helpCommand);
        }

        user.sendMessage(helpCommandOutput.toString());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            execute(sender, Arrays.asList(args));
            return true;
        }

        for (dev.lightdream.api.commands.SubCommand subCommand : subCommands) {
            if (!(subCommand.getAliases().contains(args[0].toLowerCase()))) {
                continue;
            }

            if (subCommand.onlyForPlayers() && !(sender instanceof Player)) {
                MessageManager.sendMessage(sender, new MessageBuilder(api.getLang().mustBeAPlayer));
                return true;
            }

            if (subCommand.onlyForConsole() && !(sender instanceof ConsoleCommandSender)) {
                MessageManager.sendMessage(sender, new MessageBuilder(api.getLang().mustBeConsole));
                return true;
            }

            if (!hasPermission(sender, subCommand.getPermission())) {
                MessageManager.sendMessage(sender, new MessageBuilder(api.getLang().noPermission));
                return true;
            }

            subCommand.execute(sender, new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
            return true;
        }

        execute(sender, Arrays.asList(args));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String bukkitAlias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            ArrayList<String> result = new ArrayList<>();
            for (dev.lightdream.api.commands.SubCommand subCommand : subCommands) {
                for (String alias : subCommand.getAliases()) {
                    if (alias.toLowerCase().startsWith(args[0].toLowerCase()) && hasPermission(sender, subCommand.getPermission())) {
                        result.add(alias);
                    }
                }
            }
            return result;
        }

        for (dev.lightdream.api.commands.SubCommand subCommand : subCommands) {
            if (subCommand.getAliases().contains(args[0]) && hasPermission(sender, subCommand.getPermission())) {
                return subCommand.onTabComplete(sender, new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
            }
        }

        return Collections.emptyList();
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return ((sender.hasPermission(permission) || permission.equalsIgnoreCase("")));
    }

    private String getCommand() {
        return getClass().getAnnotation(dev.lightdream.api.annotations.commands.Command.class).command();
    }

    @Override
    public String getPermission() {
        return getClass().getAnnotation(dev.lightdream.api.annotations.commands.Command.class).permission();
    }

    @Override
    public String getUsage() {
        return getClass().getAnnotation(dev.lightdream.api.annotations.commands.Command.class).usage();
    }

    @Override
    public boolean onlyForPlayers() {
        return getClass().getAnnotation(dev.lightdream.api.annotations.commands.Command.class).onlyForPlayers();
    }

    @Override
    public boolean onlyForConsole() {
        return getClass().getAnnotation(dev.lightdream.api.annotations.commands.Command.class).onlyForConsole();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList(getClass().getAnnotation(dev.lightdream.api.annotations.commands.Command.class).aliases());
    }

    @Override
    public int getMinimumArgs() {
        return 0;
    }
}
