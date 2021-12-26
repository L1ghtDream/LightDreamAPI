package dev.lightdream.api.managers.database;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.dto.LambdaExecutor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class HikariDatabaseManagerImpl extends HikariDatabaseManager implements IDatabaseManagerImpl {

    public HikariDatabaseManagerImpl(IAPI api) {
        super(api);
    }

    @Override
    public void setup() {
        setup(User.class);
    }

    @SuppressWarnings("unused")
    public @NotNull User createUser(@NotNull OfflinePlayer player) {
        User user = getUser(player.getUniqueId());
        if (user == null) {
            user = getUser(player.getName());
        }
        if (user != null) {
            user.uuid = player.getUniqueId();
        } else {
            user = new User(api, player.getUniqueId(), player.getName(), api.getSettings().baseLang);
        }
        user.save();
        return user;
    }

    @SuppressWarnings("NullableProblems")
    public @Nullable User getUser(@NotNull UUID uuid) {
        return get(User.class, new HashMap<String, Object>() {{
            put("uuid", uuid);
        }}).stream().findFirst().orElse(null);
    }

    @SuppressWarnings("unused")
    @Override
    public @Nullable User getUser(@NotNull String name) {
        Optional<User> optionalUser = getAll(User.class).stream().filter(user -> user.name.equals(name)).findFirst();

        return optionalUser.orElse(null);
    }

    @SuppressWarnings("unused")
    public @NotNull User getUser(@NotNull OfflinePlayer player) {
        return createUser(player);
    }

    public @NotNull User getUser(@NotNull Player player) {
        return createUser(player);
    }

    @SuppressWarnings("unused")
    @Override
    public @Nullable User getUser(int id) {
        Optional<User> optionalUser = getAll(User.class).stream().filter(user -> user.id == id).findFirst();

        return optionalUser.orElse(null);
    }

    @SuppressWarnings("unused")
    public @Nullable User getUser(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            return getUser((Player) sender);
        }
        return api.getConsoleUser();
    }

    @Override
    public HashMap<Class<?>, String> getDataTypes() {
        return null;
    }

    @Override
    public HashMap<Class<?>, LambdaExecutor> getSerializeMap() {
        return null;
    }

    @Override
    public HashMap<Class<?>, LambdaExecutor> getDeserializeMap() {
        return null;
    }
}