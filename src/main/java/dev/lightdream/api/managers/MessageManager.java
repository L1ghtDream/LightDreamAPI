package dev.lightdream.api.managers;

import de.themoep.minedown.MineDown;
import dev.lightdream.api.databases.ConsoleUser;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.utils.MessageBuilder;
import dev.lightdream.api.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused"})
public class MessageManager {

    public static boolean useMineDown;
    public static boolean enabled = false;

    public static void sendMessage(CommandSender sender, String message) {
        sendMessage(sender, new MessageBuilder(message));
    }

    public static void sendMessage(CommandSender sender, MessageBuilder builder) {
        if (sender instanceof Player) {
            sendMessage((Player) sender, builder);
        } else {
            builder.parseStringList().forEach(line -> sender.sendMessage(Utils.color(line)));
        }
    }

    public static void sendMessage(Player player, String message) {
        sendMessage(player, new MessageBuilder(message));
    }

    public static void sendMessage(Player player, MessageBuilder builder) {
        init();
        if (MessageManager.useMineDown) {
            builder.parseStringList().forEach(line -> player.spigot().sendMessage(new MineDown(line).toComponent()));
        } else {
            builder.parseStringList().forEach(line -> player.sendMessage(Utils.color(line)));
        }
    }

    public static void sendMessage(User user, String message) {
        sendMessage(user, new MessageBuilder(message));
    }

    public static void sendMessage(User user, MessageBuilder builder) {
        if(user instanceof ConsoleUser){
            ConsoleUser consoleUser = (ConsoleUser) user;
            consoleUser.sendMessage(builder);
        }
        if (!user.isOnline()) {
            return;
        }
        sendMessage(user.getPlayer(), builder);
    }

    public static void sendMessage(OfflinePlayer offlinePlayer, String message) {
        sendMessage(offlinePlayer, new MessageBuilder(message));
    }

    public static void sendMessage(OfflinePlayer offlinePlayer, MessageBuilder builder) {
        if (offlinePlayer.isOnline()) {
            return;
        }
        sendMessage(offlinePlayer.getPlayer(), builder);
    }

    public static void broadcast(String message) {
        init();
        if (useMineDown) {
            Bukkit.broadcastMessage(Arrays.toString(new MineDown(message).toComponent()));
        } else {
            Bukkit.broadcastMessage(Utils.color(message));
        }
    }

    public static void sendAll(MessageBuilder message) {
        Bukkit.getOnlinePlayers().forEach(player -> sendMessage(player, message));
    }

    public static void init() {
        if (enabled) {
            return;
        }
        List<String> mineDownVersions = Arrays.asList("1.16", "1.17", "1.18");
        boolean useMineDown = false;
        for (String version : mineDownVersions) {
            if (Bukkit.getServer().getVersion().contains(version)) {
                useMineDown = true;
                break;
            }
        }
        MessageManager.useMineDown = useMineDown;
    }
}
