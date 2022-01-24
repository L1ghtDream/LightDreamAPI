package dev.lightdream.api;

import dev.lightdream.api.managers.DatabaseManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("unused")
public final class APIPlugin extends LightDreamPlugin {

    @Override
    public void onEnable() {
        init("LightDream-API-Plugin", String.valueOf(UUID.randomUUID()));
    }

    @Override
    public void onDisable() {
        api.disable();
    }

    @Override
    public @NotNull String parsePapi(OfflinePlayer player, String identifier) {
        return "";
    }

    @Override
    public void disable() {

    }

    @Override
    public void registerFileManagerModules() {

    }

    @Override
    public void registerUser(Player player) {
        databaseManager.createUser(player);
    }

    @Override
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    public String getProjectVersion() {
        return api.getProjectVersion();
    }


}
