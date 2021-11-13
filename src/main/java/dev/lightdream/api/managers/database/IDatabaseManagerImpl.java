package dev.lightdream.api.managers.database;

import dev.lightdream.api.databases.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IDatabaseManagerImpl extends IDatabaseManager {

    @NotNull User getUser(@NotNull UUID uuid);

    @SuppressWarnings("unused")
    @Nullable User getUser(@NotNull String name);

    @SuppressWarnings("unused")
    @NotNull User getUser(@NotNull OfflinePlayer player);

    @NotNull User getUser(@NotNull Player player);

    @SuppressWarnings("unused")
    @Nullable User getUser(int id);

    @SuppressWarnings("unused")
    @Nullable User getUser(@NotNull CommandSender sender);

}
