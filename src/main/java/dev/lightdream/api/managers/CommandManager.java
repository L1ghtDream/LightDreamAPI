package dev.lightdream.api.managers;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.commands.SubCommand;
import dev.lightdream.api.utils.MessageBuilder;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final IAPI api;
    private List<SubCommand> subCommands;

    public CommandManager(IAPI api, String command, List<SubCommand> subCommands) {
        this.api = api;
        try {
            api.getPlugin().getCommand(command).setExecutor(this);
            api.getPlugin().getCommand(command).setTabCompleter(this);
        } catch (NullPointerException e) {
            api.getLogger().severe("The command '" + command + "' does not exist in plugin.yml");
            return;
        }

        this.subCommands = subCommands;
        this.subCommands.sort(Comparator.comparing(com -> com.aliases.get(0)));
    }

    public void sendUsage(CommandSender sender) {
        StringBuilder helpCommandOutput = new StringBuilder();
        helpCommandOutput.append("\n");

        if (api.getLang().helpCommand.equals("")) {
            for (SubCommand subCommand : subCommands) {
                if (sender.hasPermission(subCommand.permission)) {
                    helpCommandOutput.append(subCommand.usage);
                    helpCommandOutput.append("\n");
                }
            }
        } else {
            helpCommandOutput.append(api.getLang().helpCommand);
        }

        api.getMessageManager().sendMessage(sender, new MessageBuilder(helpCommandOutput.toString()));
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command bukkitCommand, String label, String[] args) {
        if (args.length == 0) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.aliases.get(0).equals("")) {
                    subCommand.execute(sender, Arrays.asList(args));
                    return true;
                }
            }
            sendUsage(sender);
            return true;
        }

        for (SubCommand subCommand : subCommands) {
            if (!(subCommand.aliases.contains(args[0].toLowerCase()))) {
                continue;
            }

            if (subCommand.onlyForPlayers && !(sender instanceof Player)) {
                api.getMessageManager().sendMessage(sender, new MessageBuilder(api.getLang().mustBeAPlayer));
                return true;
            }

            if (subCommand.onlyForConsole && !(sender instanceof ConsoleCommandSender)) {
                api.getMessageManager().sendMessage(sender, new MessageBuilder(api.getLang().mustBeConsole));
                return true;
            }

            if (!hasPermission(sender, subCommand.permission)) {
                api.getMessageManager().sendMessage(sender, new MessageBuilder(api.getLang().noPermission));
                return true;
            }

            subCommand.execute(sender, new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
            return true;
        }

        api.getMessageManager().sendMessage(sender, new MessageBuilder(api.getLang().unknownCommand));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command bukkitCommand, String bukkitAlias, String[] args) {
        if (args.length == 1) {
            ArrayList<String> result = new ArrayList<>();
            for (SubCommand subCommand : subCommands) {
                for (String alias : subCommand.aliases) {
                    if (alias.toLowerCase().startsWith(args[0].toLowerCase()) && hasPermission(sender, subCommand.permission)) {
                        result.add(alias);
                    }
                }
            }
            return result;
        }

        for (SubCommand subCommand : subCommands) {
            if (subCommand.aliases.contains(args[0]) && hasPermission(sender, subCommand.permission)) {
                return subCommand.onTabComplete(sender, new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
            }
        }

        return Collections.emptyList();
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return ((sender.hasPermission(permission) || permission.equalsIgnoreCase("")));
    }
}
